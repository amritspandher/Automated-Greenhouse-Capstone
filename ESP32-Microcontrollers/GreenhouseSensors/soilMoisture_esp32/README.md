# Soil Moisture Module

Measures soil moisture as a percentage and uploads data to AWS IoT Core via MQTT.
Uses an Arduino Nano to read and calibrate the soil moisture sensor. This communicates with the ESP32 via UART (Serial) which connects to WiFi and IoT Core and uploads the data.

## Calibration

The user will first place the soil moisture sensor in the air to calibrate the air reading (0%) then place it in a glass of water to get the water reading (100%).

1. Fill a glass of water.
2. Dry the soil moisture sensor and hold it in the air.
3. Press and hold the reset button until the light begins to flash. It is now beginning the calibration process.
4. Hold the sensor in the air until the light blinks 5x slow and 5x fast.
5. Once the light stops blinking, place the sensor in the glass of water.
6. Hold the sensor in the glass of water until the light blinks 5x slow and 5x fast.
7. Calibration process is complete!