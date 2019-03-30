# Reddit Guesser
Machine learning approach based solution for searching quality / up-and-coming comments on Reddit platform using Boosted Decision Trees.

System consist of:
    - REST API written in Python responsible for computing predictions with trained statistical learning models.
    - WEB APP written in Java with Spring Boot that is mainly GUI for REST API that lets generate new predictions and browse the previous ones.
    - MongoDB for storing user accounts and users' predictions history. (Added do docker-compose file)

To build and run ```Docker``` is required.
1. Build docker image of ```ml_rest``` module with command:

    ```docker build -t ml_rest:latest .```
    
2. Build docker image of ```ml_webapp``` module with maven command:

    ```mvn clean install dockerfile:build```
    
3. Use ```docker-compose``` to start running two modules built earlier and NoSQL MongoDB container with ```mongo-express```.
