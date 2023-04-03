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
					if(typeof(event.headers.username) === 'undefined')
						returnError(400, 'Invalid Request - No username provided in request headers');
					else
						query = `DELETE FROM GreenhouseDB.Users WHERE (GreenhouseDB.Users.username = \'${event.headers.username}\')`;
					break;
				case 'Schedules':
					if(typeof(event.headers.scheduleid) === 'undefined')
						returnError(400, 'Invalid Request - No username provided in request headers');
					else
						query = `DELETE FROM GreenhouseDB.Schedules WHERE (GreenhouseDB.Schedules.scheduleID = \'${event.headers.scheduleid}\')`;
				// case 'Greenhouses':
				// 	query = 
				// 		'SELECT Greenhouses.greenhouseID, Greenhouses.name, Greenhouses.location, Greenhouses.userID ' +
				// 		'FROM Greenhouses ' +
				// 		'INNER JOIN Users ON Users.userID = Greenhouses.userID ' +
				// 		'WHERE Users.username = \'' + event.headers.username + '\'';
				// 	break;
				// case 'Modules':
				// 	if(typeof(event.headers.greenhouseID) !== 'undefined'){
				// 		query = 
				// 		'SELECT * FROM Modules ' +
				// 		'WHERE Modules.greenhouseID = ' + event.headers.greenhouseID;
				// 	}
				// 	else
				// 		returnError(400, 'No greenhouseID provided');
				// 	break;
				default:
					returnError(400, 'Invalid Request - table does not exist or is not yet supported');
			}
			
			console.log('QUERY :  ' + query);
			queryDatabase(query);
		}

		//if 'query' header provided (NOT SECURE!)
		else if (typeof(event.headers.query) !== 'undefined' ){
			console.log(event.headers.query);
			queryDatabase(event.headers.query);
		}

		else{
			returnError(400, `Invalid Request - ${JSON.stringify(event.headers)}`);
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