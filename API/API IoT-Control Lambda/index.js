var AWS = require("aws-sdk");

const topic = 'led/sub';

exports.handler = async (event) => {
	AWS.config.update({ region: "us-east-1" });
	var iotdata = new AWS.IotData({ endpoint: 'a2dn2g6vdt23ze-ats.iot.us-east-1.amazonaws.com' });

	if(typeof(event.headers)==='undefined')
		return returnMessage(400, 'No headers provided');
	else if(typeof(event.headers.moduleid)==='undefined')
		return returnMessage(400, 'No moduleid header provided');
	else if(typeof(event.headers.action)==='undefined')
		return returnMessage(400, 'no action header provided');
	else{
		const payload = {
			moduleID: event.headers.moduleid,
			powerState: event.headers.action
		};

		const params = {
			topic: topic,
			payload: JSON.stringify(payload),
			qos: 0
		};

		console.log(`Params: ${JSON.stringify(params)}`);

		var response = {};

		//publish to IoT Core MQTT topic and return success/error message
		await iotdata.publish(params, function(err, data) {
			if (err) {
				console.log("ERROR => " + JSON.stringify(err));
				response = returnMessage(500, JSON.stringify(err));
			}
			else {
				console.log("Success");
				response = returnMessage(200, 'Success');
			}
		}).promise();

		return response;
	}
	
	
	
	// Returns response with given message and statusCode
	function returnMessage(statusCode, message){
		console.log(`Status Code: ${statusCode}\nMessage: ${message}`);

		const response = {
			statusCode: statusCode,
			body: message
		};

		console.log(`Response: ${JSON.stringify(response)}`);
		return response;
	}
}

