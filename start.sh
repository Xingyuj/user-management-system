#!/bin/sh -x
docker-compose up -d
docker cp ./dump.sql xingyu-postgres:/docker-entrypoint-initdb.d/dump.sql
docker exec -u postgres xingyu-postgres psql postgres postgres -f docker-entrypoint-initdb.d/dump.sql