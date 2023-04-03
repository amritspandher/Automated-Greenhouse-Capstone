# Automated Greenhouse API

Use the Automated Greenhouse API to access the database.

### URL:

```
https://eknqepv86f.execute-api.us-east-1.amazonaws.com
```

## GET Request

Uses HTTP Request Headers to build queries to the databases.

## GET /database/timestream

Accesses the AWS Timestream database.

### Request Headers:

```
moduleid*: {moduleID}
measurename*: {name of measurement}
```

Starred headers are required

### GET

Returns most recent entry with given moduleID and Measure Name

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: moduleid:1002, measurename:temperature
```

Timestream SQL Statement:

```
SELECT * FROM myGreenhouseTimestream.DHT_sensor
WHERE time = (SELECT MAX(time) FROM myGreenhouseTimestream.DHT_sensor)
AND measure_name = 'temperature'
AND moduleID = '1002'
```
