package com.example.QueueConsumerSpring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.codehaus.jackson.map.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.core.MediaType;

import javax.jms.*;

public class TestConsumerThread extends Thread{

    private String threadId;
    private long millis;

    public TestConsumerThread(String threadId, long millis) {
        this.threadId = threadId;
        this.millis = millis;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    @Override
    public void run() {
        String nombreCola = "queue.so1.demo";
        String nombreServicio = "EjemploCola_" + threadId;
       // String serverLocation = "failover:(tcp://172.17.0.2:61616)?timeout=3000";
         String serverLocation = "failover:(tcp://localhost:61616)?timeout=3000";

        try {

            //MessageConsumer consumer = QueueUtil.getMessageConsumer(serverLocation, nombreCola, nombreServicio);
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(serverLocation);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(nombreCola);

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            while (true) {
                try {

                    Message message = consumer.receive(1000);
                    // extraccion de datos en cola
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();
                        System.out.println("[" + threadId + "]Recibiendo: " + text);


                        ObjectMapper mapper = new ObjectMapper();
                        Object objeto = mapper.readValue(text, Object.class);

                        Object response = new Object();


                    } else {
                        System.out.println("[" + threadId + "]Received: " + message);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesarMensaje(Message message) {

        if (message instanceof ActiveMQTextMessage) {
            ActiveMQTextMessage msg = (ActiveMQTextMessage) message;

            try {
                System.out.println("[" + threadId + "] mensaje:" + msg.getText());

            } catch (Exception e) {
                System.out.println("[" + threadId + "]" + "Mensaje no puede ser leido ..");
            }

        } else {
            System.out.println("[" + threadId + "]" + "Se desconoce el formato de mensaje..." + message);
        }

    }

}
