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
					else if(typeof(event.headers.password) === 'undefined')
						query = `INSERT INTO GreenhouseDB.Users (username) VALUES (\'${event.headers.username}\')`;
					else
						query = `INSERT INTO GreenhouseDB.Users (username, password) VALUES (\'${event.headers.username}\', \'${event.headers.password}\')`;
					break;

				case 'Greenhouses':
					if(typeof(event.headers.name) === 'undefined' || typeof(event.headers.userid) === 'undefined')
						returnError(400, 'Invalid Request - No name and/or userid header provided');
					else
						query = `INSERT INTO GreenhouseDB.Greenhouses (name, userID) VALUES (\'${event.headers.name}\', ${event.headers.userid})`;
					break;

				case 'Modules':
					if(typeof(event.headers.greenhouseid) !== 'undefined' || typeof(event.headers.moduleid) !== 'undefined' || typeof(event.headers.name) !== 'undefined' || typeof(event.headers.type) !== 'undefined'){
						query = 
							`INSERT INTO GreenhouseDB.Modules (moduleID, name, type, greenhouseID) 
							VALUES (\'${event.headers.moduleid}\', \'${event.headers.name}\', \'${event.headers.type}\', ${event.headers.greenhouseid})`;
					}
					else
						returnError(400, 'Invalid Request - headers required (for now): greenhouseid, name, type, moduleid');
					break;
				case 'Schedules':
					if(typeof(event.headers.greenhouseid) !== 'undefined' || typeof(event.headers.moduleid) !== 'undefined' || typeof(event.headers.time) !== 'undefined' || typeof(event.headers.day) !== 'undefined' || typeof(event.headers.repeatstatus) !== 'undefined' || typeof(event.headers.action) !== 'undefined')
						query =
							`INSERT INTO GreenhouseDB.Schedules (greenhouseid, moduleid, time, day, repeatStatus, action) 
							VALUES (${event.headers.greenhouseid}, ${event.headers.moduleid}, \'${event.headers.time}\', \'${event.headers.day}\', \'${event.headers.repeatstatus}\', \'${event.headers.action}\')`;
					else
						returnError(400, 'Invalid Request - header required (for now): greenhouseid, moduleid, time, day, repeatstatus, action');
					break;
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