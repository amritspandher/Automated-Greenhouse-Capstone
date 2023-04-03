/*
 * Automated Greenhouse Sensor Sublayer
 */

// libraries
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include "DHT.h"
#include "DHTcerts.h"
#include <stdio.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

//OLED display screen size and parameters
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
#define OLED_RESET -1

// define display
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

// function used to define which bus for current OLED display
void TCA9548A(uint8_t bus)
{
  Wire.beginTransmission(0x70);
  Wire.write(1 << bus);
  Wire.endTransmission();
}

// Type of the DHT Sensor
#define DHT_TYPE DHT22  

//DHT sensors
#define DHT_PIN_1 27 
DHT dht_1(DHT_PIN_1, DHT_TYPE);

#define DHT_PIN_2 25 
DHT dht_2(DHT_PIN_2, DHT_TYPE);

#define DHT_PIN_3 26 
DHT dht_3(DHT_PIN_3, DHT_TYPE);

#define DHT_PIN_4 33 
DHT dht_4(DHT_PIN_4, DHT_TYPE);

//current temp and humidity variables
double currentT;
double currentH;

// ultrasonic sensor 
#define trig 5
#define echo 18

// define variables for calculating water level
long duration; // variable for the duration of sound wave travel
int distance; // variable for the distance measurement
int currentW; // current water level

#define soilSensor_1 34
#define soilSensor_2 32
#define soilSensor_3 35

//set tested constant values for when sensor is in air vs in water
const int airVal_1 = 2600;
const int waterVal_1 = 1200;

const int airVal_2 = 2520;
const int waterVal_2 = 1065;

const int airVal_3 = 2525;
const int waterVal_3 = 1055;

//changing variables for soil moisture
int soilMoistureVal_1, soilMoistureVal_2, soilMoistureVal_3 = 0;
int soilMoisturePercent_1, soilMoisturePercent_2, soilMoisturePercent_3 = 0;
int currentPercent;

// WiFi credentials
#define WIFI_SSID "BC" // SSID of your WIFI
#define WIFI_PASSWORD "" //your wifi password

// AWS credentials
#define THINGNAME "Sensors"// thing unique ID
#define AWS_IOT_ENDPOINT "a2dn2g6vdt23ze-ats.iot.us-east-1.amazonaws.com" // your host for uploading data to AWS,
#define PUBLISH_TOPIC "bc/greenhouse/sensors/pub"  

// WiFi client
WiFiClientSecure net = WiFiClientSecure();
PubSubClient client(net);

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
  client.subscribe(PUBLISH_TOPIC);

  // successfullly connected and subscribed to topic
  Serial.println("AWS IoT Connected!");
}


void setup(){
  Serial.begin(9600); 
  
  // OLED displays
  Wire.begin();
  TCA9548A(2);
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;);
  }
  display.clearDisplay();

  TCA9548A(3);
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;);
  }
  display.clearDisplay();


  TCA9548A(4);
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;);
  }
  display.clearDisplay();

  TCA9548A(5);
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;);
  }
  display.clearDisplay();

  TCA9548A(6);
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;);
  }
  display.clearDisplay();

  //turn on the DHT sensors
  dht_1.begin();
  dht_2.begin();
  dht_3.begin();
  dht_4.begin();

  // turn on ultrasonic sensor
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);

  // connect to the WiFi and AWS
  connectWiFi();
  connectAWS();
}

void soilMoisture() {
  soilMoistureVal_1 = analogRead(soilSensor_1);  //read from sensor
  Serial.print("Soil 1: ");
  Serial.println(soilMoistureVal_1);
  soilMoisturePercent_1 = map(soilMoistureVal_1, airVal_1, waterVal_1, 0, 100);  //map to a percenetage using constant values

  soilMoistureVal_2 = analogRead(soilSensor_2);  //read from sensor
  Serial.print("Soil 2: ");
  Serial.println(soilMoistureVal_2);
  soilMoisturePercent_2 = map(soilMoistureVal_2, airVal_2, waterVal_2, 0, 100);  //map to a percenetage using constant values

  soilMoistureVal_3 = analogRead(soilSensor_3);  //read from sensor
  Serial.print("Soil 3: ");
  Serial.println(soilMoistureVal_3);
  soilMoisturePercent_3 = map(soilMoistureVal_3, airVal_3, waterVal_3, 0, 100);  //map to a percenetage using constant values

  int AvgSoilMoisture = (soilMoisturePercent_1 + soilMoisturePercent_2 + soilMoisturePercent_3) / 3;

  TCA9548A(4);
  display.clearDisplay();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(0,0);
  display.println("Soil");
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(0,20);
  display.println("Moisture:");
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(20,45);
  display.print(AvgSoilMoisture);
  display.println("%");
  display.display();

  if (AvgSoilMoisture == currentPercent) {
    delay(5000);
    return;
  }
  else {
    currentPercent = AvgSoilMoisture;

    StaticJsonDocument<200> doc;

  doc["moduleID"] = 1009;
  doc["name"] = "SoilMoisture";
  doc["type"] = "soilMoisturePercentage";
  doc["greenhouseID"] = 123;
  doc["soilMoisture"] = AvgSoilMoisture;

   
  char jsonBuffer[512];
  serializeJson(doc, jsonBuffer);
  client.publish(PUBLISH_TOPIC, jsonBuffer);
  }
}

void dhtSensors() {
  delay(1000);

  // TEMP/HUMIDITY
  // read temperature and humidity

  //DHT_1
  double t_1 = dht_1.readTemperature(); // return temperature in °C
  float h_1 = dht_1.readHumidity();// return humidity in %
  float f_1 = dht_1.readTemperature(true);

  if(isnan(h_1) || isnan(t_1) || isnan(f_1)) {
    Serial.println(F("***** Failed to read from DHT_1 sensor *****"));
    return;
  }

  // convert to Farenheit
  t_1 = (t_1 * 1.8) + 32;

  // print readings from DHT 1
  Serial.print("DHT 1 -- temp: ");
  Serial.println(t_1); 
  Serial.print("      -- humidity: ");
  Serial.println(h_1);
  Serial.println();

  // DHT_2
  double t_2 = dht_2.readTemperature(); // return temperature in °C
  float h_2 = dht_2.readHumidity();// return humidity in %
  float f_2 = dht_2.readTemperature(true);

  if(isnan(h_2) || isnan(t_2) || isnan(f_2)) {
    Serial.println(F("***** Failed to read from DHT_2 sensor *****"));
    delay(1000);
    return;
  }

  // convert to Farenheit
  t_2 = (t_2 * 1.8) + 32;

  // print readings from DHT 2
  Serial.print("DHT 2 -- temp: ");
  Serial.println(t_2); 
  Serial.print("      -- humidity: ");
  Serial.println(h_2);
  Serial.println();

  //DHT_3
  double t_3 = dht_3.readTemperature(); // return temperature in °C
  float h_3 = dht_3.readHumidity();// return humidity in %
  float f_3 = dht_3.readTemperature(true);

  if(isnan(h_3) || isnan(t_3) || isnan(f_3)) {
    Serial.println(F("***** Failed to read from DHT_3 sensor *****"));
    return;
  }

  // convert to Farenheit
  t_3 = (t_3 * 1.8) + 32;

  // print readings from DHT 3
  Serial.print("DHT 3 -- temp: ");
  Serial.println(t_3); 
  Serial.print("      -- humidity: ");
  Serial.println(h_3);
  Serial.println();

  //DHT_4
  double t_4 = dht_4.readTemperature(); // return temperature in °C
  float h_4 = dht_4.readHumidity();// return humidity in %
  float f_4 = dht_4.readTemperature(true);

  if(isnan(h_4) || isnan(t_4) || isnan(f_4)) {
    Serial.println(F("***** Failed to read from DHT_4 sensor *****"));
    delay(1000);
    return;
  }

  // convert to Farenheit
  t_4 = (t_4 * 1.8) + 32;

  // print readings from DHT 4
  Serial.print("DHT 4 -- temp: ");
  Serial.println(t_4); 
  Serial.print("      -- humidity: ");
  Serial.println(h_4);
  Serial.println();

  //average
  double avgT = (t_1 + t_2 + t_3 + t_4) / 4;
  float avgH = (h_1 + h_2 + h_3 + h_4) / 4;
  
  Serial.print("Average Temp: ");
  Serial.println(avgT);

   TCA9548A(2);
  display.clearDisplay();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(0, 10);
  display.println("Temp:");
  display.setTextSize(3);
  display.setTextColor(WHITE);
  display.setCursor(0, 40);
  display.print(avgT);
  display.println(" F");
  display.display();

  TCA9548A(3);
  display.clearDisplay();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(0,10);
  display.println("Humidity:");
  display.setTextSize(3);
  display.setTextColor(WHITE);
  display.setCursor(0,40);
  display.print(avgH);
  display.println("%");
  display.display();

  if (int(avgT) == currentT && int(avgH) == currentH){
    delay(5000);
    waterLevel();
  }
  else {
    currentT = int(avgT);
    currentH = int(avgH);
 
     //construct message
    StaticJsonDocument<200> doc;
    doc["moduleID"] = 1002;
    doc["name"] = "DHT11",
    doc["type"] = "tempHumid",
    doc["greenhouseID"] = 123;
    doc["temperature"] = avgT;
    doc["humidity"] = avgH;
   
    char jsonBuffer[512];
    serializeJson(doc, jsonBuffer);
    if (!client.publish(PUBLISH_TOPIC, jsonBuffer)) {
      Serial.println("***Failed to upload to AWS***");
      connectAWS();
      client.loop();
    }
    else {
      Serial.println("uploaded to AWS");
    }
  }
}

void waterLevel() 
{

  // Clears the trigPin condition
  digitalWrite(trig, LOW);
  delayMicroseconds(2);
  // Sets the trigPin HIGH (ACTIVE) for 10 microseconds
  digitalWrite(trig, HIGH);
  delayMicroseconds(10);
  digitalWrite(trig, LOW);
  // Reads the echoPin, returns the sound wave travel time in microseconds
  duration = pulseIn(echo, HIGH);
  // Calculating the distance
  distance = duration * 0.034 / 2; // Speed of sound wave divided by 2 (go and back)
  // Displays the distance on the Serial Monitor
  Serial.print("Distance: ");
  Serial.print(distance);
  Serial.println(" cm");

  int level = map(distance, 0, 32, 100, 0);
  
  // prints
  Serial.print("Water Level: ");
  Serial.print(level);
  Serial.println("%");

  TCA9548A(5);
  display.clearDisplay();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(0,10);
  display.println("H20 Level:");
  display.setTextSize(3);
  display.setTextColor(WHITE);
  display.setCursor(10,40);
  display.print(level);
  display.println("%");
  display.display();

if (level == currentW){
    delay(5000);
    soilMoisture();
  }
  else {
    currentW = level;
 
     //construct message
    StaticJsonDocument<200> doc;
    doc["moduleID"] = 1006;
    doc["name"] = "Ultrasonic Sensor",
    doc["type"] = "Water Level",
    doc["greenhouseID"] = 123;
    doc["waterLevel"] = level;
   
    char jsonBuffer[512];
    serializeJson(doc, jsonBuffer);
    if (!client.publish(PUBLISH_TOPIC, jsonBuffer)) {
      Serial.println("***Failed to upload to AWS***");
      connectAWS();
      client.loop();
    }
    else {
      Serial.println("uploaded to AWS");
    }
  }
  
}

void loop() {
  soilMoisture();
  delay(5000);
  dhtSensors();
  delay(5000);
  waterLevel();
  delay(5000);
}
