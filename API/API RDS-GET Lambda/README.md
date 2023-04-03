# Automated Greenhouse API

Use the Automated Greenhouse API to access the database.

### URL:

```
https://eknqepv86f.execute-api.us-east-1.amazonaws.com
```

## GET Request

Uses HTTP Request Headers to build queries to the databases.

## GET /database/rds

Accesses the MySQL database.

### Request Headers:

```
table*: 'Users' || 'Greenhouses' || 'Modules'
username: username
userid: userID
greenhouseid: greenhouseID
moduleid: moduleID
scheduleid: scheduleID
```

Starred headers are required

### GET Users

Returns all user info for the user with the provided username.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Users, username:GoHawks
```

MySQL Statement:

```
SELECT * FROM GreenhouseDB.Users
WHERE username = 'GoHawks'
```

### GET Greenhouses

Returns all Greenhouses in the provided user's account. Also supports requests with username header instead of userid.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Greenhouses, userid:1
```

MySQL Statement:

```
SELECT * FROM Greenhouses
WHERE userID = 1
```

### GET Modules

Returns all Modules in the provided greenhouse or the module matching the provided moduleID.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Greenhouses, username:GoHawks, greenhouseid:1
```

MySQL Statement:

```
SELECT * FROM Modules
WHERE Modules.greenhouseID = 1
```

Returns Module info matching the provided moduleID.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Greenhouses, moduleid:1
```

MySQL Statement:

```
SELECT * FROM Modules
WHERE Modules.moduleID = 1
```

### GET Schedules

Returns all Schedule events in the provided greenhouse or for the provided module.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Schedules, greenhouseid:1
```

MySQL Statement:

```
SELECT * FROM Schedules
WHERE Schedules.greenhouseID = 1
```

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Schedules, moduleid:1
```

MySQL Statement:

```
SELECT * FROM Schedules
WHERE Schedules.moduleID = 1
```

# Unsecured Database Access

Users can currently submit any MySQL query using the following. A future patch will remove this.

## GET /database/rds

### Request Headers

```
query: {any MySQL query}
```

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: query:SELECT * FROM Users
```
