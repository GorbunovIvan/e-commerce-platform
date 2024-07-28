<h2><b>Frontend-Service</b> is a microservice of "E-COMMERCE PLATFORM" application.</h2>

<p>The web part of the application that allows users to interact with the platform.<br/>
	 - Technologies - OpenFeign, GraphQL, RabbitMQ, gRPC, Kafka, Thymeleaf, OAuth2.0 (Keycloak).
</p>
<p>Authentication is extracted to Keycloak</p>
<hr/>
<p>Communications:</p>
<ul>
    <li>with <b>User-service</b>: via REST (Feign-client) secured with OAuth2.0</li>
    <li>with <b>Product-service</b>: via REST (Feign-client) secured with OAuth2.0</li>
    <li>with <b>Order-service</b>: reads via GraphQL, writes via publishing messages to RabbitMQ</li>
    <li>with <b>Review-service</b>: via gRPC</li>
    <li>with <b>Notification-service</b>: publishes notifications to Kafka</li>
</ul>