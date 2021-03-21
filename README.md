# Simple Message Queue
This project demonstrates how asyncronous processing can be acomplished with a queue in Java.

![Queue Diagram](/diagram.jpeg)

## Requirements
- Java 11

## Setup
```
chmod +x setup.sh
./setup.sh
```
## Running the project
### Producer
```
java Producer [TIME_INTERVAL_IN_MS]
```
TIME_INTERVAL_IN_MS defaults to 2500

### Queue
```
java Queue
```
The queue routing configuration is located in the `config` file.

### Consumer
```
java Consumer [CONSUMER_ID]
```
CONSUMER_ID is a number from 0 to totalConsumers - 1 where totalConsumers is the number located in the `config` file.