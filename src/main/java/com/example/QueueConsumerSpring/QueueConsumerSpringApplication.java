package com.example.QueueConsumerSpring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.jms.*;

@SpringBootApplication
public class QueueConsumerSpringApplication {
	static TestConsumerThread algo = new TestConsumerThread("1", 100);
	static TestConsumerThread algo2 = new TestConsumerThread("2", 100);

	public static void main(String[] args) {
		SpringApplication.run(QueueConsumerSpringApplication.class, args);
		algo.start();
		algo2.start();
	}

}
