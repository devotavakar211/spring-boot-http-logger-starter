# Spring Boot HTTP Logger Starter 🚀
A simple, auto-configurable Spring Boot starter for logging incoming HTTP requests and outgoing responses. Drop it into your project to get instant, detailed HTTP traffic visibility in your logs, perfect for debugging and development.

JitPackLicense

# ✨ Features
Zero Configuration: Just add the dependency and it works out of the box.
Detailed Request Logging: Logs the HTTP method, URI, query parameters, headers, and request body.
Detailed Response Logging: Logs the HTTP status code, headers, and response body.
Lightweight: Implemented as a simple Servlet Filter.
Customizable: Easily enable/disable or configure logging behavior via your application.properties or application.yml file.
🏁 Getting Started
To use this starter in your own Spring Boot project, follow these steps.

# Prerequisites
Java 11+
Spring Boot 2.x+
Maven
1. Add the JitPack Repository
   Since this project is published via JitPack, you need to add the JitPack repository to your pom.xml file.

XML

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
2. Add the Dependency
Next, add the starter as a dependency to your pom.xml.

XML

<dependencies>
    <!-- ... other dependencies ... -->

    <dependency>
        <groupId>com.github.devotavakar211</groupId>
        <artifactId>spring-boot-http-logger-starter</artifactId>
        <version>1.0.0</version> <!-- Or check the badge above for the latest version -->
    </dependency>
</dependencies>
That's it! The next time you build your project, Maven will download the dependency. Run your Spring Boot application and make a request to one of your endpoints to see the logs.

⚙️ Configuration
While the starter works with zero configuration, you can customize its behavior by adding properties to your application.properties or application.yml file.

Property	Default	Description
http.logging.enabled	true	Set to false to completely disable the logging filter.
http.logging.include-headers	true	Set to false to exclude HTTP headers from the logs.
http.logging.include-payload	true	Set to false to exclude the request/response body.
http.logging.max-payload-size	10240	The maximum payload size (in bytes) to log. Prevents logging huge files.
Example: application.properties
properties

# Disable logging of the request/response body for security and performance
http.logging.enabled=true
http.logging.include-payload=false
Example: application.yml
YAML

http:
logging:
enabled: true
include-payload: false
📝 Example Log Output
When you run your application, you will see logs similar to the following in your console for each HTTP request.

Incoming Request:

text

[HTTP LOG] > REQUEST: [POST /api/users]
[HTTP LOG] > Headers: {content-type=[application/json], user-agent=[PostmanRuntime/7.29.2], ...}
[HTTP LOG] > Body: {"username":"testuser", "email":"test@example.com"}
Outgoing Response:

text

[HTTP LOG] < RESPONSE: [201 CREATED] in 45ms
[HTTP LOG] < Headers: {Content-Type=[application/json], Location=[/api/users/123], ...}
[HTTP LOG] < Body: {"id":123, "status":"CREATED"}
🛠️ How to Build From Source
If you want to contribute or build the project locally, you can do so with Maven.

Bash

# Clone the repository
git clone https://github.com/devotavakar211/spring-boot-http-logger-starter.git
cd spring-boot-http-logger-starter

# Build and install locally
mvn clean install
🤝 Contributing
Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are greatly appreciated.

Fork the Project
Create your Feature Branch (git checkout -b feature/AmazingFeature)
Commit your Changes (git commit -m 'Add some AmazingFeature')
Push to the Branch (git push origin feature/AmazingFeature)
Open a Pull Request
📜 License
This project is distributed under the Apache License 2.0. See LICENSE for more information.