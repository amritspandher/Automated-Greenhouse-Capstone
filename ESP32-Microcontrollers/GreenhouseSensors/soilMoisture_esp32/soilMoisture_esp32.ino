#include "awsLib.h"
#include "certs.h"
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <WiFiClientSecure.h>


// define WiFi parameters
// these are unique to each user WiFi connection
char ssid[] = "SSID"; // SSID of your WIFI
char wifiPassword[] = "PASSWORD"; //your wifi password

//define the name of device associated with AWS IoT Core and endpoint
#define THINGNAME "soilMoisture"
#define AWS_IOT_ENDPOINT "a2dn2g6vdt23ze-ats.iot.us-east-1.amazonaws.com"

//define the names of the MQTT topics
#define AWS_IOT_PUBLISH_TOPIC   "bc/greenhouse/sensors/pub"  //this one will push messages to AWS
#define AWS_IOT_SUBSCRIBE_TOPIC "bc/greenhouse/sensors/sub"  //this one will receive messages from AWS


//Serial communication with Arduino Nano
#define RXp2 16
#define TXp2 17

#define THRESHOLD 2 //minimum change to report

#define soilMoisturePin 36 //vp pin

#define MODULE_ID 2001
#define GREENHOUSE_ID 123
#define TYPE "soilMoisture"

int soilMoisture = 0; // variable for percent of maxDepth
int temp; // stores data to compare

//set clients
WiFiClientSecure net = WiFiClientSecure();
PubSubClient client(net);

void connectAWS() {
  // upon successful WiFi connection,
  // Configure WiFiClientSecure to use the AWS IoT device credentials attached in fil "certs.h"
  net.setCACert(AWS_CERT_CA);
  net.setCertificate(AWS_CERT_CRT);
  net.setPrivateKey(AWS_CERT_PRIVATE);

  // Connect to the MQTT broker on the AWS endpoint we defined earlier
  client.setServer(AWS_IOT_ENDPOINT, 8883);

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

  // successfullly connected
  Serial.println("AWS IoT Connected!");
}

//method for pushing messages to AWS IoT
void publishMessage(int percent)
{
  StaticJsonDocument<200> doc;

  doc["moduleID"] = MODULE_ID;
  doc["name"] = THINGNAME,
  doc["type"] = TYPE,
  doc["greenhouseID"] = GREENHOUSE_ID;
  doc["soilMoisture"] = percent;
    
  char jsonBuffer[512]; 
  serializeJson(doc, jsonBuffer); // print to client

  Serial.print("Publishing to ");
  Serial.println(AWS_IOT_PUBLISH_TOPIC);
  if (!client.publish(AWS_IOT_PUBLISH_TOPIC, jsonBuffer)) {
    Serial.println("Rebooting...");
    ESP.restart();
  }
  else {
    Serial.println("uploaded to AWS");
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial2.begin(9600, SERIAL_8N1, RXp2, TXp2);
  Serial.println("Setup complete");
  connectWiFi(ssid, wifiPassword);
  connectAWS();
}

void loop() {
  if(Serial2.available() > 0){
    temp = Serial2.parseInt();
    if(temp>100 || temp<0){
      Serial.print("Calibration: ");
      Serial.println(temp);
    }
    else{
      Serial.print(temp);
      Serial.println("%\n");

      if(abs(temp-soilMoisture) >= THRESHOLD){
        soilMoisture = temp;
        Serial.print("Publishing - Soil Moisture: ");
        Serial.print(soilMoisture);
        Serial.println("%\n");
  
        publishMessage(soilMoisture);
      }
    }
  }
}
