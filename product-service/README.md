<h2><b>Product-Service</b> microservice of "E-COMMERCE PLATFORM" application.</h2>

<p>Responsible for product management.<br/>
	 - Technologies - REST, PostgreSQL, OAuth2.0 (Keycloak), Swagger.
</p>
<p>Since this service is secure, to use it as a client you need to first obtain an <b>access-token</b>. 
Something like this:</p>
<pre>
POST http://localhost:8080/realms/e-commerce-platform/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id = product-service &
client_secret = sWdHyl6nx6kKw2WkM6jIf66z2nmgEpos &
grant_type = client_credentials
</pre>

<hr/>

<p>It will return an <b>access-token</b> (if the client_secret is correct and the Keycloak server is running).</p>
<p>You should then take that <b>access-token</b> and add it as a bearer of authorization to your requests to this service.<br/>For example:</p>
<pre>
GET http://localhost:8082/api/v1/products
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJkdnZ0NGNZTVdrWERf..
</pre>