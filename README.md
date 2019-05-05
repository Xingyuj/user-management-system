## Ethan UMS Service Api


#### How to start
Download repo

    git clone https://github.com/Xingyuj/user-management-system.git

Go into repo root path simply run (chmod file if no permission)

    ./start.sh

This script will call docker-compose and run DML to insert test data.
After all docker containers start, you should be able to visit http://127.0.0.1:8080/swagger-ui.html to check the system API doc.

#### How to build

This project is using maven as dependencies management. To install dependencies you should

    mvn install

to get a runnable jar run

    mvn package

docker maven build plugin is included in thie project, to create a new docker image run

    mvn package docker:build

#### Challenges

* Shiro would prevent swagger resource to be loaded to display api doc, checked on stack overflow, it mentioned to add 'anon' for all swagger resources. But after I added them into the shiro filter its still not working. After researching I add those into application.yml to config and it finnally working. This took me neally an hour to figured out.

* I was tring to config docker-compose to automatically dump DML data into database after containers running. After researching finnally find a solution:

      docker exec -u postgres xingyu-postgres psql postgres postgres -f docker-entrypoint-initdb.d/dump.sql
