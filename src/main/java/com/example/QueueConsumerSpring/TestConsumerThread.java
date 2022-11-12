package com.example.QueueConsumerSpring;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.codehaus.jackson.map.ObjectMapper;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

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
        String serverLocation = "failover:(tcp://165.168.1.16:61616)?timeout=3000";
        //String serverLocation = "failover:(tcp://localhost:61616)?timeout=3000";

        try {
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
                    Message message = consumer.receive(5000);
                    // extraccion de datos en cola
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();
                        System.out.println("[" + threadId + "]Recibiendo: " + text);
                        ObjectMapper mapper = new ObjectMapper();
                        Map objeto = mapper.readValue(text, Map.class);
                        envia(text);
                    } else {
                        System.out.println("[" + threadId + "]Recibiendo: " + message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void envia(String msj ) throws JMSException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://165.168.1.18:80/service/recibe");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(msj.length()));
            conn.setUseCaches(false);
            try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                dos.writeBytes(msj);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream())))
            {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println("[" + threadId + "]Enviando a Servicio externo: "+line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(conn!=null){
                conn.disconnect();
            }
        }
    }
}
