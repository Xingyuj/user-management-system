#!/bin/sh
echo running docker container
docker-compose up -d
echo wait for db container to get ready 
sleep 10
echo wait for db container to get ready
sleep 10
echo insert data into db
docker cp ./dump.sql xingyu-postgres:/docker-entrypoint-initdb.d/dump.sql
docker exec -u postgres xingyu-postgres psql postgres postgres -f docker-entrypoint-initdb.d/dump.sql
echo done! ethan UMS is ready to play!
