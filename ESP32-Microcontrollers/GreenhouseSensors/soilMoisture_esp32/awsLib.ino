#include "awsLib.h"
#include <WiFi.h>

//method to connect microcontroller to WiFi and AWS IoT Core
void connectWiFi(char ssid[], char pass[])
{

  //establish a conenction to local WiFi
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, pass);

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
