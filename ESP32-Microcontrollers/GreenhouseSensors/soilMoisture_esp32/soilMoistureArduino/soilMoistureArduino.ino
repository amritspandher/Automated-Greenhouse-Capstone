#include <EEPROM.h>

#define MIN_MOISTURE 12
#define MAX_MOISTURE 25
#define SOILPERCENT (100.0 * (airVal - analogRead(sensorAPin)) / (airVal - waterVal))

const int sensorAPin = 7;
const int ledPin = 10;
const int buttonPin = 5;

int airVal;     //EEPROM addr: 0
int waterVal;   //EEPROM addr: sizeOf(airVal)

int soilPercent = 0;

void setup() {
  pinMode(ledPin, OUTPUT);
  pinMode(buttonPin, INPUT);
  Serial.begin(9600);
  
  EEPROM.get(0, airVal);
  EEPROM.get(sizeof(airVal), waterVal);

//  Serial.print("Air Value: ");
//  Serial.println(airVal);
//  Serial.print("Water Value: ");
//  Serial.println(waterVal);
  
  if(airVal<0 || airVal>1000 || waterVal<0 || waterVal>1000){
//    Serial.println("Error reading EEPROM");
//    Serial.print("airVal/waterVal: ");
//    Serial.print(airVal);
//    Serial.print("/");
//    Serial.println(waterVal);
//    Serial.println("\n\n");
    calibrateSensor();
  }
  
}

void loop() {
  soilPercent = SOILPERCENT;
  Serial.print(soilPercent);
  
  if(digitalRead(buttonPin) == HIGH){
    delay(1000);
    if(digitalRead(buttonPin) == HIGH)
//      digitalWrite(pumpPin, LOW);
//      digitalWrite(pumpPin, LOW);
      calibrateSensor();
  }

  delay(5000);
}

void calibrateSensor(){
  nBlinks(5);
  avg(&airVal);

  Serial.print("Air Value: ");
  Serial.println(airVal);
  
  delay(5000);
  nBlinks(5);
  avg(&waterVal);

  
  Serial.print("Water Value: ");
  Serial.println(waterVal);

  EEPROM.put(0, airVal);
  EEPROM.put(sizeof(airVal), waterVal);
  
  delay(2000);
}

void avg(int *typeVal){
  *typeVal = 0;
  for(int i=0; i<20; i++){
    digitalWrite(ledPin, HIGH);
    *typeVal += analogRead(sensorAPin);
    delay(125);
    digitalWrite(ledPin, LOW);
    delay(125);
  }
  *typeVal /= 20;
}

void nBlinks(int num){
  for(int z=0; z<num; z++){
    digitalWrite(ledPin, HIGH);
    delay(500);
    digitalWrite(ledPin, LOW);
    delay(500);
  }
}
