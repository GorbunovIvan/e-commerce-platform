FROM openjdk:21

WORKDIR /app

COPY build/libs/review-service-0.0.1-SNAPSHOT.jar ./app.jar

# Creating a directory for logs inside the container
RUN mkdir -p /app/logs

EXPOSE 8084

ENTRYPOINT ["java", "-DLOG_FILE_PATH=/app/logs/logfile.log", "-jar", "app.jar"]

# Run:
#   'docker build -t ivangorbunovv/review-service-e-commerce-platform-image .'
