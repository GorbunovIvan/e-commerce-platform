package org.example;

import net.devh.boot.grpc.server.serverfactory.GrpcServerLifecycle;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ReviewServiceApplicationTests {

	@MockBean
	private GrpcServerLifecycle grpcServerLifecycle; // To ignore bean creation

	@Test
	void contextLoads() {
	}

}
