<h2><b>Order-Service</b> is a microservice of "E-COMMERCE PLATFORM" application.</h2>

<p>Responsible for order processing, fulfillment, and order status tracking.<br/> 
- For read-operations: allows you to retrieve information from it using GraphQL.<br/> 
- For write-operations: allows you to send write operations to it via RabbitMQ, implementing an event-driven architecture.<br/>
<br/>
Stores orders in MongoDB.
<br/>
<br/>
	 - Technologies - GraphQL (for client-services to read), RabbitMQ (for client-services to write), MongoDB.
</p>