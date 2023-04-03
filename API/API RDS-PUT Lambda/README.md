# Automated Greenhouse API

Use the Automated Greenhouse API to access the database.

### URL:

```
https://eknqepv86f.execute-api.us-east-1.amazonaws.com
```

## PUT Request

Uses HTTP Request Headers to build queries to the databases.

## PUT /database/rds

Posts to the MySQL database.

### Request Headers:

```
table*: 'Users' || 'Greenhouses'
username: {username}
password: {password}
greenhouseid: {greenhouseID}
name: {item name}
```

Starred headers are required

### PUT Users

Updates User entry with new password.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Users, username:GoHawks, password:GoHawks12
```

MySQL Statement:

```
UPDATE GreenhouseDB.Users
SET password = 'GoHawks12'
WHERE username = 'GoHawks
```

### PUT Greenhouses

Updates Greenhouse entry with new name.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: table:Greenhouses, greenhouseid:1, name:NewGreenhouseName
```

MySQL Statement:

```
UPDATE GreenhouseDB.Greenhouses
SET name = 'NewGreenhouseName'
WHERE greenhouseID = 1
```
