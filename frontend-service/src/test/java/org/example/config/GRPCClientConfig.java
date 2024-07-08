package org.example.config;

import io.grpc.ManagedChannelBuilder;
import org.example.grpc.GrpcReviewServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GRPCClientConfig {

//    @Value("${review-service.grpc.host}")
//    private String grpcHost;
//
//    @Value("${review-service.grpc.port}")
//    private int port;
//
//    @Bean
//    public GrpcReviewServiceGrpc.GrpcReviewServiceBlockingStub blockingStub() {
//
//        var channel = ManagedChannelBuilder
//                .forAddress(grpcHost, port)
//                .usePlaintext()
//                .build();
//
//        return GrpcReviewServiceGrpc.newBlockingStub(channel);
//    }
}
