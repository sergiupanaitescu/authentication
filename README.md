# Authentication demo app

This is an authentication demo app based on Spring Boot.
It uses an jwt token as an authentication mechanism with a 10 minutes validity.
The app can be run via IDE (Eclipse run as jaava application) or via cmd line mvnw spring-boot:run
and the endpoints can be accesed at localhost:8080.
To run with tests at start use mvn test spring-boot:run

### Registered users

The app contains 2 default users and others can be added in data.sql file.

### Database

The app uses the H2 in memory database so all the data is lost at app restart.

### Rest API

The following rest endpoints are provided:

1. api/v1/authentication/login - POST - Request Parameters: username, password
   
   Response example:
   
   {
    "username": "testUser",
    "name": "Ion",
    "age": 33,
    "address": "acasa",
    "token": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJqd3RDcmF6eVRva2VuIiwic3ViIjoidGVzdFVzZXIiLCJhdXRob3JpdGllcyI6WyJVU0VSIiwiQURNSU4iXSwiaWF0IjoxNjMxMjAzMzcxLCJleHAiOjE2MzEyMDM5NzF9.RWNjTCsuUYe7Tn9VgCMyQ7_asBAN2s94ij2A62cGKp4rboQKl4ZjOTkYoJVSXGJXBiBjE9wO-kVM9wu8ek8gTQ",
    "roles": [
        "USER",
        "ADMIN"
    ]
}
	
2. api/v1/authentication/logout - POST - Headers: Authorization : Bearer xxxxxxx(the token provided at login)

   Invalidates the provided token that cannot be used at further calls.
   
3. /api/v1/user/details - POST - Headers: Authorization : Bearer xxxxxxx(the token provided at login), Request Parameters: username

   Can be accessed by a user that has the role ADMIN with a valid corresponding token.

   Provides details for the specific user provided as param:
   
   Response example:
   
   {
    "username": "testUser",
    "name": "Ion",
    "age": 33,
    "address": "acasa",
    "roles": [
        "USER",
        "ADMIN"
    ]
}
   
  
