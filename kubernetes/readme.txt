### Preparing DOCKER images ###

cd config-server
docker build -t ivangorbunovv/config-server-e-commerce-platform-image .
docker tag ivangorbunovv/config-server-e-commerce-platform-image ivangorbunovv/e-commerce-platform:config-server
docker push ivangorbunovv/e-commerce-platform:config-server
cd ..

cd eureka-server
docker build -t ivangorbunovv/e-commerce-platform:eureka-server .
docker tag ivangorbunovv/eureka-server-e-commerce-platform-image ivangorbunovv/e-commerce-platform:eureka-server
docker push ivangorbunovv/e-commerce-platform:eureka-server
cd ..

cd api-gateway
docker build -t ivangorbunovv/api-gateway-e-commerce-platform-image .
docker tag ivangorbunovv/api-gateway-e-commerce-platform-image ivangorbunovv/e-commerce-platform:api-gateway
docker push ivangorbunovv/e-commerce-platform:api-gateway
cd ..

cd user-service
docker build -t ivangorbunovv/user-service-e-commerce-platform-image .
docker tag ivangorbunovv/user-service-e-commerce-platform-image ivangorbunovv/e-commerce-platform:user-service
docker push ivangorbunovv/e-commerce-platform:user-service
cd ..

cd product-service
docker build -t ivangorbunovv/product-service-e-commerce-platform-image .
docker tag ivangorbunovv/product-service-e-commerce-platform-image ivangorbunovv/e-commerce-platform:product-service
docker push ivangorbunovv/e-commerce-platform:product-service
cd ..

cd order-service
docker build -t ivangorbunovv/order-service-e-commerce-platform-image .
docker tag ivangorbunovv/order-service-e-commerce-platform-image ivangorbunovv/e-commerce-platform:order-service
docker push ivangorbunovv/e-commerce-platform:order-service
cd ..

cd review-service
docker build -t ivangorbunovv/review-service-e-commerce-platform-image .
docker tag ivangorbunovv/review-service-e-commerce-platform-image ivangorbunovv/e-commerce-platform:review-service
docker push ivangorbunovv/e-commerce-platform:review-service
cd ..

cd notification-service
docker build -t ivangorbunovv/notification-service-e-commerce-platform-image .
docker tag ivangorbunovv/notification-service-e-commerce-platform-image ivangorbunovv/e-commerce-platform:notification-service
docker push ivangorbunovv/e-commerce-platform:notification-service
cd ..

cd frontend-service
docker build -t ivangorbunovv/frontend-service-e-commerce-platform-image .
docker tag ivangorbunovv/frontend-service-e-commerce-platform-image ivangorbunovv/e-commerce-platform:frontend-service
docker push ivangorbunovv/e-commerce-platform:frontend-service
cd ..


### Kubernetes operations ###

kubectl apply -f kubernetes\deploy-and-service-for-mysql.yaml
kubectl apply -f kubernetes\deploy-and-service-for-postgres.yaml
kubectl apply -f kubernetes\deploy-and-service-for-mongo.yaml
kubectl apply -f kubernetes\deploy-and-service-for-kafka.yaml
kubectl apply -f kubernetes\deploy-and-service-for-rabbitmq.yaml
kubectl apply -f kubernetes\deploy-and-service-for-keycloak.yaml
kubectl apply -f kubernetes\deploy-and-service-for-config-server.yaml
kubectl apply -f kubernetes\deploy-and-service-for-eureka-server.yaml
kubectl apply -f kubernetes\deploy-and-service-for-api-gateway.yaml
kubectl apply -f kubernetes\deploy-and-service-for-user-service.yaml
kubectl apply -f kubernetes\deploy-and-service-for-product-service.yaml
kubectl apply -f kubernetes\deploy-and-service-for-order-service.yaml
kubectl apply -f kubernetes\deploy-and-service-for-review-service.yaml
kubectl apply -f kubernetes\deploy-and-service-for-notification-service.yaml
kubectl apply -f kubernetes\deploy-and-service-for-frontend-service.yaml
