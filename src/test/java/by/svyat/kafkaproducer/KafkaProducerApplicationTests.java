package by.svyat.kafkaproducer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;

@SpringBootTest
class KafkaProducerApplicationTests {

	@MockBean
	private KafkaAdmin kafkaAdmin;

	@Test
	void contextLoads() {
	}

}
