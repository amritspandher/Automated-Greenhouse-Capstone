# Automated Greenhouse API

Use the Automated Greenhouse API to delete entries in the database.

### URL:

```
https://eknqepv86f.execute-api.us-east-1.amazonaws.com
```

## DELETE Request

Uses HTTP Request Headers to build queries to the databases.

## DELETE /database/rds

Deletes entries from the MySQL database.

### Request Headers:

```
table*: 'Users' || 'Schedules'
username: {username}
scheduleid: {scheduleID}
```

Starred headers are required

### DELETE Users

Deletes user entry if one exists matching the provided username.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Users, username:GoHawks
```

MySQL Statement:

```
DELETE FROM GreenhouseDB.Users
WHERE GreenhouseDB.Users.username = 'GoHawks'
```

### DELETE Schedules

Deletes user entry if one exists matching the provided username.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Schedules, scheduleid:42
```

MySQL Statement:

```
DELETE FROM GreenhouseDB.Schedules
WHERE GreenhouseDB.Schedules.scheduleID = 42
```

# Unsecured Database Access

Users can currently submit any MySQL query using the following. A future patch will remove this.

## DELETE /database/rds

### Request Headers

```
query: {any MySQL query}
```

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: query:SELECT * FROM Users
```
