/*
 * Automated Greenhouse Actuator Sublayer
 */

#include "ActuatorCerts.h"
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <WiFi.h>

// define WiFi parameters
// these are unique to each user WiFi connection
#define WIFI_SSID "BC" // SSID of your WIFI
#define WIFI_PASSWORD "" //your wifi password

//define the name of device associated with AWS IoT Core and endpoint
#define THINGNAME "Actuators"
#define AWS_IOT_ENDPOINT "a2dn2g6vdt23ze-ats.iot.us-east-1.amazonaws.com"

//define the names of the MQTT topics
#define PUBLISH_TOPIC   "bc/greenhouse/actuators/pub"  //this one will push messages to AWS
#define SUBSCRIBE_TOPIC "bc/greenhouse/actuators/sub"  //this one will receive messages from AWS


//set clients
WiFiClientSecure net = WiFiClientSecure();
PubSubClient client(net);

//set relay pins
#define fans 33
#define lights 21
#define irrigation 19

//method to connect microcontroller to WiFi and AWS IoT Core
void connectWiFi()
{

  //establish a conenction to local WiFi
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  //message to try and connect
  Serial.println("Connecting to Wi-Fi");

  //print a ". . . . " while waiting for connection
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }

  //if failure to connect to WiFi
  if(WiFi.status() != WL_CONNECTED) {
    Serial.println("***Failed to Connect to WiFi***");
    esp_deep_sleep_start();
  } else {
      Serial.println("\n  WiFi Connected!");
      Serial.println(WiFi.localIP());
  }
}

void connectAWS() {
  // upon successful WiFi connection,
  // Configure WiFiClientSecure to use the AWS IoT device credentials attached in fil "certs.h"
  net.setCACert(AWS_CERT_CA);
  net.setCertificate(AWS_CERT_CRT);
  net.setPrivateKey(AWS_CERT_PRIVATE);

  // Connect to the MQTT broker on the AWS endpoint we defined earlier
  client.setServer(AWS_IOT_ENDPOINT, 8883);

  // Create a message handler
  client.setCallback(messageHandler);

  Serial.println("Connecting to AWS IoT");

  // print ". . . ." while trying to establish connection to IoT
  while (!client.connect(THINGNAME)) {
    Serial.print(".");
    delay(100);
  }

  //if disconnected from AWS IoT
  if(!client.connected()){
    Serial.println("AWS IoT Timeout!");
    return;
  }

  // Subscribe to a topic
  client.subscribe(SUBSCRIBE_TOPIC);

  // successfullly connected and subscribed to topic
  Serial.println("AWS IoT Connected!");
}


//method for pushing messages to AWS IoT,
// 
void publishMessage(int moduleID, String deviceName, int greenhouseID)
{
  Serial.println("We are in publish message now");
  StaticJsonDocument<200> doc;

  doc["moduleID"] = moduleID;
  doc["name"] = deviceName;
  doc["greenhouseID"] = greenhouseID;

  Serial.println("first 3 vriables have been set");

  int caseNo = 0;
  if (moduleID == 1003) {caseNo = 1;} //fans
  else if (moduleID == 1004) {caseNo = 2;} //lights
  else if (moduleID == 1005) {caseNo = 3;} //irrigation
  else {caseNo = 4;}

  switch(caseNo) {
    
  case 1:
    if (digitalRead(fans)) {
      // its on
      doc["powerState"] = "ON";
    }
    else if (!digitalRead(fans)) {
      //its off
      doc["powerState"] = "OFF";
    }
    break;
  case 2:
    if (digitalRead(lights)) {
      // its on
      doc["powerState"] = "ON";
    }
    else if (!digitalRead(lights)) {
      //its off
      doc["powerState"] = "OFF";
    }
    break;
  case 3:
    if (digitalRead(irrigation)) {
      // its on
      doc["powerState"] = "ON";
    }
    else if (!digitalRead(irrigation)) {
      //its off
      doc["powerState"] = "OFF";
    }
    break;
  case 4:
    // error, moduleID not recognized 
    Serial.println("ERROR");
  }

  Serial.println("We have checked the powerState");
  
  char jsonBuffer[512]; 
  serializeJson(doc, jsonBuffer); // print to client

  client.publish(PUBLISH_TOPIC, jsonBuffer);

  Serial.println("Message sent");
}


// method to handle incoming messages from AWS IoT

/*
 * JSON format
 * 
 * {
 *  "moduleID": (int),
 *  "name": (String, name of device),
 *  "type": (String, type of device),
 *  "greenhouseID": (int)
 *  "powerState": (String, "on" or "off")
 * }
 * 
 */


void messageHandler(char* topic, byte* payload, unsigned int length) {
  Serial.print("incoming: ");
  Serial.println(topic);

  StaticJsonDocument<200> doc;
  deserializeJson(doc, payload, length);
  int moduleID = doc["moduleID"];
  String deviceName = doc["name"];
  int greenhouseID = doc["greenhouseID"];
  String powerState = doc["powerState"];
  powerState.toUpperCase();

  int deviceNo = 0;
  if (moduleID == 1003) {deviceNo = 1;} //fans
  else if (moduleID == 1004) {deviceNo = 2;}  //lights
  else if (moduleID == 1005) {deviceNo = 3;}  //irrigation
  else {deviceNo = 4;}

  switch(deviceNo) {
    case 1: //fans
      if (powerState.equals("ON")) {
        digitalWrite(fans, HIGH);
        Serial.println("Fans are on");
      }
      else if (powerState.equals("OFF")) {
        digitalWrite(fans, LOW);
        Serial.println("Fans are off");
      }
      else {
        Serial.println("ERROR: powerState must be String variable, \"on\" or \"off\"");
      }
      publishMessage(moduleID, deviceName, greenhouseID);
      break;
      
    case 2: //lights
      if (powerState.equals("ON")) {
        digitalWrite(lights, HIGH);
        Serial.println("Lights are on");
      } else if (powerState.equals("OFF")) {
        digitalWrite(lights, LOW);
        Serial.println("Lights are off");
      }
      else {
        Serial.println("ERROR: powerState must be String variable, \"on\" or \"off\"");
      }
      publishMessage(moduleID, deviceName, greenhouseID);
      break;

    case 3:  //irrigation
      if (powerState.equals("ON")) {
        digitalWrite(irrigation, HIGH);
        Serial.println("Water pump is on!");
      } else if (powerState.equals("OFF")) {
        digitalWrite(irrigation, LOW);
        Serial.println("Water pump is off!");
      } else {
        Serial.println("ERROR: powerState must be String variable, \"on\" or \"off\"");
      }
      publishMessage(moduleID, deviceName, greenhouseID);
      break;
    
    case 4:
      Serial.println("ERROR: moduleID not recognized");
      break;
  }
  //publishMessage();
  
}

//setup program here
void setup() {
  Serial.begin(9600);
  pinMode (fans, OUTPUT);
  pinMode (lights, OUTPUT);
  pinMode (irrigation, OUTPUT);
  connectWiFi();
  connectAWS();
  //publishMessage();
}

//start program here
void loop() {
  //publishMessage();
  client.loop();
  delay(1000);
}
