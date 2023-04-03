# Automated Greenhouse API

Use the Automated Greenhouse API to access the database.

### URL:

```
https://eknqepv86f.execute-api.us-east-1.amazonaws.com
```

## POST Request

Uses HTTP Request Headers to build queries to the databases.

## POST /database/rds

Posts to the MySQL database.

### Request Headers:

```
table*: 'Users'
username: {username}
password: {password}
userid: {userID}
greenhouseid: {greenhouseID}
moduleid: {moduleID}
name: {item name}
type: {module type}
time: {time in format HR:MN (24hr)}
day: {day of week (example: Wednesday)}
repeatstatus: 'daily' || 'weekly' || 'monthly' || 'annually'
action: 'on'|| 'off'
```

Starred headers are required

### POST Users

Creates new User entry, password defaults to null if none is provided.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Users, username:GoHawks, password:GoHawks12
```

MySQL Statement:

```
INSERT INTO GreenhouseDB.Users(username,password)
VALUES('GoHawks','GoHawks12')
```

### POST Greenhouses

Creates new Greenhouse entry under the given userID.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Greenhouses, userid:1, name:Greenhouse1
```

MySQL Statement:

```
INSERT INTO GreenhouseDB.Greenhouses(name, userID)
VALUES('Greenhouse1','1')
```

### POST Modules

Creates new Module entry under the given greenhouseID.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Modules, moduleid:1, name:soilMoisture, type:sensor, greenhouseid:1
```

MySQL Statement:

```
INSERT INTO GreenhouseDB.Modules(moduleID, name, type, greenhouseID)
VALUES(1,'soilMoisture','sensor','1')
```

### POST Schedules

Creates new Schedule entry under the given greenhouseID with moduleID, time, day, repeatstatus, and action.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Schedules, greenhouseid:1, moduleid:1001, time:'4:30', day:'Wednesday', repeatstatus:'weekly', action:'on'
```

MySQL Statement:

```
INSERT INTO GreenhouseDB.Schedules(greenhouseID, moduleID, time, day, repeatStatus, action)
VALUES(1, 1001, '4:30', 'Wednesday', 'weekly', 'on')
```

# Unsecured Database Access

Users can currently submit any MySQL query using the following. A future patch will remove this.

## POST /database/rds

### Request Headers

```
query: {any MySQL query}
```

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: query:SELECT * FROM Users
```
