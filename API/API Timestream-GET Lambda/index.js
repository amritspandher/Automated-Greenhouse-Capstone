var AWS = require("aws-sdk");

AWS.config.update({ region: "us-east-1" });

queryClient = new AWS.TimestreamQuery();

const table = "myGreenhouseTimestream.DHT_sensor";

exports.handler = async (event) => {
    if(typeof(event.headers) === 'undefined' ){
        return httpResponse(400, 'Invalid Request - Headers unspecified');
    }
    else if(typeof(event.headers.moduleid) === 'undefined' ){
        return httpResponse(400, 'Invalid Request - sensorid header unspecified');
    }
    else if(typeof(event.headers.measurename) === 'undefined' ){
        return httpResponse(400, 'Invalid Request - sensortype header unspecified');
    }

    try{
        let query = 
            `SELECT * from ${table} ` +
            `WHERE time = (SELECT MAX(time) ` +
            `FROM ${table}) ` +
            `AND measure_name = \'${event.headers.measurename}\'` +
            `AND moduleID = \'${event.headers.moduleid}\'`;
        
        console.log("Timestream Query: " + query);

        let tsResponse;
        try {
            tsResponse = await queryClient.query(params = {
                QueryString: query,
            }).promise();
        } catch (err) {
            console.error("Error while querying:", err);
            throw err;
        }

        //parses responses into data object
        var datum = {};
        const columnInfo = tsResponse.ColumnInfo;
        const rowData = tsResponse.Rows[0].Data;
        for(var i in rowData){
            datum[columnInfo[i].Name] = rowData[i].ScalarValue;
        }
        if(datum['measure_value::bigint'])
            datum['measure_value'] = datum['measure_value::bigint'];
        else if(datum['measure_value::double'])
            datum['measure_value'] = datum['measure_value::double'];
        else if(!datum['measure_value'])
            datum['measure_value'] = "unsupported data type";

            
        console.log("Datum: " + JSON.stringify(datum));

        return httpResponse(200, JSON.stringify(datum));
    } catch (error){
        return httpResponse(500, error);
    }
};



// Returns error response with given errorString and statusCode
function httpResponse(statusCode, message){
    const response = {
        statusCode: statusCode,
        body: message
    };
    console.log("Response: " + JSON.stringify(response));
    return response;
}