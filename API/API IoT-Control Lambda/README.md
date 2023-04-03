# Automated Greenhouse API

Use the Automated Greenhouse API to control IoT devices connected to AWS IoT Core.

### URL:

```
https://eknqepv86f.execute-api.us-east-1.amazonaws.com
```

## POST Request

Uses HTTP Request Headers to publish MQTT messages to a topic.

## POST /iot/control

Published MQTT messages to a topic to control IoT devices.

### Request Headers:

```
moduleid*: {moduleID}
action*: 'on' || 'off
```

Starred headers are required

### POST

Controls IoT device with the given moduleID.

Sample Request:

```
Endpoint: https://eknqepv86f.execute-api.us-east-1.amazonaws.com
Headers: moduleid:1069, action:'on'
```

Sends message to 'led/sub' MQTT topic. If the moduleID matches the device, its powerState is set to the provided action.
