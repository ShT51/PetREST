This Pet project based on "RESTful Web Services, Java, Spring Boot, Spring MVC and JPA" course on Udemy.
(https://www.udemy.com/course/restful-web-service-with-spring-boot-jpa-and-mysql/).

This service was build with Spring MVC and Spring Boot. I have implemented:
- Authentication and Authorization features using Spring Security Framework.
- Password Reset and Email Verification features using Amazon SES service
- Different Roles for Users (ROLE_USER and ROLE_ADMIN)

Service uses MySQL Data base and H2 in-memory database for tests. 
For testing my service I used JUnit5 and Mockito. To test service endpoints I used Rest Assured and Postman.

This RESTfull Web Service can handle several endpoints in JSON and XML formats.

Open end-points:
- Create User (POST) http://localhost:8080/mobile-app-ws/users
- Password Reset (POST) http://localhost:8080/mobile-app-ws/users/password-reset-request

This end-point is available only after Email Verification.
- Login User (POST) http://localhost:8080/mobile-app-ws/users/login

This end-points are available only to authorized Users.
And they are available only to User itself or User with ROLE_ADMIN.
- Get User (POST) http://localhost:8080/mobile-app-ws/users/{id}
- UPDATE User (PUT) http://localhost:8080/mobile-app-ws/users/{id}
- DELETE User (DELETE) http://localhost:8080/mobile-app-ws/users/{id}
- Get list of User Addresses (POST) http://localhost:8080/mobile-app-ws/users/{id}/addresses
- Get User Address (POST) http://localhost:8080/mobile-app-ws/users/{id}/addresses/{address id}

This end-points are available only to User with ROLE_ADMIN
- Get all Users (POST) http://localhost:8080/mobile-app-ws/users

See the examples of request in requestEx.txt file

List TODO:
- add new endpoints
- update view layer
- deploy my RESTful WebService on Amazon EC2 Server
- update requestEx.txt file
- add to public repo Interactive Documentation made with Swagger
