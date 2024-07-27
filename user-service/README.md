<h2><b>User-Service</b> microservice of "E-COMMERCE PLATFORM" application.</h2>

<p>Responsible for user authentication, registration, and profile management.<br/>
	 - Technologies - REST, MySQL, OAuth2.0 (Keycloak), Swagger.
</p>
<p>Since this service is secure, to use it as a client you need to first obtain an <b>access-token</b>. 
Something like this:</p>
<pre>
POST http://localhost:8080/realms/e-commerce-platform/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id = user-service &
client_secret = SlDu0pPghNiAJ2zo4UzHsp7zy1DVUvbp &
grant_type = client_credentials
</pre>

<hr/>

<p>It will return an <b>access-token</b> (if the client_secret is correct and the Keycloak server is running).</p>
<p>You should then take that <b>access-token</b> and add it as a bearer of authorization to your requests to this service.<br/>For example:</p>
<pre>
GET http://localhost:8081/api/v1/users
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJkdnZ0NGNZTVdrWERf..
</pre>