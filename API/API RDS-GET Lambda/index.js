var mysql = require('mysql');
var config = require('./config.json');

var pool  = mysql.createPool({
    host     : config.dbhost,
    user     : config.dbuser,
    password : config.dbpw,
    database : config.dbname
});

exports.handler = (event, context, callback) => {
	context.callbackWaitsForEmptyEventLoop = false;
	pool.getConnection(function(err, connection) {
		
		console.log(`Event RouteKey: ${event.routeKey}`);
		
		//error handling:
		
		if(err){
			returnError(400, JSON.stringify(err));
		}
		
		//if no headers provided
		else if(typeof(event.headers) === 'undefined' ){
			returnError(400, 'Invalid Request - Headers unspecified');
		}

		//if table + username headers are provided, queries database as necessary
		else if (typeof(event.headers.table) !== 'undefined'){
			let query;
			switch (event.headers.table){
				case 'Users':
					if(typeof(event.headers.username) === 'undefined'){
						returnError(400, 'Invalid Request - username header unspecified');
						break;
					}	
					query = 'SELECT * FROM GreenhouseDB.Users WHERE username = \'' + event.headers.username + '\'';
					break;
				case 'Greenhouses':
					if(typeof(event.headers.userid) === 'undefined')
						if(typeof(event.headers.username) !== 'undefined'){
							query = 
								'SELECT Greenhouses.greenhouseID, Greenhouses.name, Greenhouses.location, Greenhouses.userID ' +
								'FROM Greenhouses ' +
								'INNER JOIN Users ON Users.userID = Greenhouses.userID ' +
								'WHERE Users.username = \'' + event.headers.username + '\'';
						}
						else
							returnError(400, 'Invalid Request - No userid or username header provided');
					else
						query = `SELECT * FROM Greenhouses WHERE userID = ${event.headers.userid}`;
					break;
				case 'Modules':
					if(typeof(event.headers.greenhouseid) !== 'undefined'){
						query = 
							'SELECT * FROM Modules ' +
							'WHERE Modules.greenhouseID = ' + event.headers.greenhouseid;
					}
					else if(typeof(event.headers.moduleid) !== 'undefined'){
						query =
							'SELECT * FROM Modules WHERE Modules.moduleID = ' + event.headers.moduleid;
					}
					else
						returnError(400, 'No greenhouseid or moduleid headers provided');
					break;
				case 'Schedules':
					if(typeof(event.headers.greenhouseid) !== 'undefined')
						query = 
							`SELECT * FROM Schedules WHERE Schedules.greenhouseID = ${event.headers.greenhouseid}`;
					else if(typeof(event.headers.scheduleid) !== 'undefined')
						query = 
							`SELECT * FROM Schedules WHERE Schedules.scheduleID = ${event.headers.scheduleid}`;
					else
						returnError(400, 'No greenhouseid or scheduleid header provided');
					break;
				default:
					returnError(400, 'Invalid Request - table does not exist or is not yet supported');
			}
			
			console.log('QUERY :  ' + query);
			queryDatabase(query);
		}

		//if 'query' header provided (NOT SECURE!)
		else if (typeof(event.headers.query) !== 'undefined' ){
			queryDatabase(event.headers.query);
		}

		else{
			returnError(400, 'Invalid Request');
		}



		// Queries the database
		function queryDatabase(query){
			// Use the connection to query database
			connection.query(query, function (error, results, fields) {
				connection.release();
				
				//returns database query error message
				if (error) {
					returnError(500, JSON.stringify(error));
				}
				
				//returns response
				else {
					const response = {
						statusCode: 200,
						body: JSON.stringify(results)
					};
					console.log("Response: " + JSON.stringify(response));
					callback(null, response);
				}
			});
		}

		// Returns error response with given errorString and statusCode
		function returnError(statusCode, errorString){
			console.log(`ERROR: ${errorString}`);
			const response = {
				statusCode: statusCode,
				body: errorString
			};
			callback(null, response);
		}
	});
};