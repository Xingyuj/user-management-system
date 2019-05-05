## Ethan UMS Service Api

### How to start
Download repo
```bash
    git clone https://github.com/Xingyuj/user-management-system.git
```

Go into repo root path simply run (chmod file if no permission)
```bash
    ./start.sh
```
This script will call docker-compose and run DML to insert test data.
After all docker containers start, you should be able to visit http://127.0.0.1:8080/swagger-ui.html to check the system API doc.

### How to test
Run unit tests
```bash
    mvn test
```
Manually call API by using
- [Postman](https://www.getpostman.com/downloads/)
- [Swagger](http://127.0.0.1:8080/swagger-ui.html)

When doing manul test, one should firstly get a `JWT Authorization Token` from Authentication service by calling `POST` `/authentications` under Authentication Controller. This endpoint needs username and password as parameters. For the convenience of testing, an `admin` user has been initialised into database. Please use this admin user to start testing and create other accounts.
```json
{
  "username":"admin",
  "password":"admin"
}
```

Futhermore, according to requirements, all resource request needs to add `Authorization` as `header`, value is the token string you just get from `POST` `/authentications`. The rest specific parameters that each endpoint needs can be found in [API Doc](http://127.0.0.1:8080/swagger-ui.html)

### How to build

This project is using maven as dependencies management. To install dependencies you should
```bash
    mvn install
```
to get a runnable jar run
```bash
    mvn package
```
docker maven build plugin is included in thie project, to create a new docker image run
```bash
    mvn package docker:build
```
### Requirements
- [git](https://git-scm.com/downloads)
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com/community-edition)
- [Docker-Compose](https://docs.docker.com/compose/install/)

### Challenges

* Shiro would prevent swagger resource to be loaded to display api doc, checked on stack overflow, it mentioned to add 'anon' for all swagger resources. But after I added them into the shiro filter its still not working. After researching I add those into application.yml to config and it finnally working. This took me neally an hour to figured out.

* I was tring to config docker-compose to automatically dump DML data into database after containers running. After researching finnally find a solution:
```bash
    docker exec -u postgres xingyu-postgres psql postgres postgres -f docker-entrypoint-initdb.d/dump.
```

* The whole project costs around two days to be finished. Consider time limited, major functions are to be implemented first. There are still several bugs to be fixed, some refactorings to be done. Therefore, exceptions may occur when unexpected parameters pass in. As a result, you may get `500 internal server error` as a response.
