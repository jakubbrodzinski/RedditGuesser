#!/bin/sh

echo "********************************************************"
echo "Wait for mongodb to be available (sleep for 10 seconds hotfix)"
echo "********************************************************"

echo "********************************************************"
echo "Starting myapp"
echo "********************************************************"

java -cp ml_webapp:ml_webapp/lib/* -Dspring.data.mongodb.host=$MONGODB_HOST -Dspring.data.mongodb.port=$MONGODB_PORT bach.project.configuration.Application
