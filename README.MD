<h3>Application "E-COMMERCE PLATFORM" (pet-project).</h3>

A platform where users can browse, purchase, and review products.

Microservices (of multi-module Gradle Spring-Boot project):

<ul>
	<ol>1. User-service: Responsible for user authentication, registration, and profile management.<br/>
	 - Technologies - REST, MySQL, OAuth2.0 (Keycloak), Swagger.
	</ol>
	<br/>
	<ol>
		2. Product-service: Responsible for product management.<br/>
	 - Technologies - REST, PostgreSQL, OAuth2.0 (Keycloak), Swagger.
	</ol>
	<br/>
	<ol>3. Order-sevice: Responsible for order processing, fulfillment, and order status tracking.<br/> 
		For read-operations: allows you to retrieve information from it using GraphQL.<br/>
		For write-operations: allows you to send write operations to it via RabbitMQ, implementing an event-driven architecture.<br/>
		- Technologies - GraphQL (for client-services to read), RabbitMQ (for client-services to write), MongoDB.
	</ol>
	<br/>
	<ol>4. Review Service: Responsible for managing product reviews and ratings submitted by users.<br/> 
		Stores reviews in MongoDB.<br/>
	 - Technologies - gRPC, MongoDB.
	</ol>
	<br/>
	<ol>5. Notification Service: Responsible for sending to email (let's say to admin email) any notifications received via Kafka from other services.<br/> 
		Doesn't save processed notifications elsewhere.<br/>
	 - Technologies - Kafka (to receive notifications from other services).
	</ol>
	<br/>
	<ol>6. Frontend Service: The web part of the application that allows users to interact with the platform.<br/>
	 - Technologies - OpenFeign, GraphQL, RabbitMQ, gRPC, Kafka, Thymeleaf, OAuth2.0 (Keycloak).
	</ol>
	<br/>
	<ol>Spring Cloud Infrastructure Microservices:<br/>
	- eureka-server (spring-cloud-starter-netflix-eureka-server)<br/>
	- api-gateway (spring-cloud-starter-gateway)<br/>
	- config-server (spring-cloud-config-server)
	</ol>
</ul>

<p>
Each service is configured to write a log to a file passed via the VM option "LOG_FILE_PATH", like this:
-DLOG_FILE_PATH=../<path-to-log-directory>/logfile.log
</p>

