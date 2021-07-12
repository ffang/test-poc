package io.fabric8.quickstarts.camel;
import org.apache.qpid.jms.JmsConnectionFactory;
import javax.jms.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.concurrent.CountDownLatch;
public class AmqpsslExample {

    public void amqpTest() throws Exception{

        JmsConnectionFactory activeMQConnectionFactory = new JmsConnectionFactory("k4YM6rob","x5AZ35Yk","amqps://ex-aao-ss-0.ex-aao-hdls-svc.new-message-project.svc.cluster.local:5672?" +
                "transport.trustStoreLocation=/home/kkakarla/development/amq7/amq7-on-openshift/client.ts&transport.keyStoreLocation=/home/kkakarla/development/amq7/amq7-on-openshift/broker.ks" +
                "&transport.trustStorePassword=artemis7&transport.keyStorePassword=artemis7&transport.verifyHost=false");
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        MessageProducer messageProducer = session.createProducer(session.createQueue("test_q"));
        Message message = session.createTextMessage("this is amqp two way ssl testing");
        messageProducer.send(message);
        connection.close();
    }
    
    public void amqpTestConsumer() throws Exception{

        JmsConnectionFactory activeMQConnectionFactory = new JmsConnectionFactory("k4YM6rob","x5AZ35Yk","amqps://ex-aao-ss-0.ex-aao-hdls-svc.new-message-project.svc.cluster.local:5672?" +
                "transport.trustStoreLocation=/home/kkakarla/development/amq7/amq7-on-openshift/client.ts&transport.keyStoreLocation=//home/kkakarla/development/amq7/amq7-on-openshift/broker.ks" +
                "&transport.trustStorePassword=artemis7&transport.keyStorePassword=artemis7&transport.verifyHost=false");
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        // Step 5. create a moving receiver, this means the message will be removed from the queue
        MessageConsumer consumer = session.createConsumer(session.createQueue("test_q"));

        // Step 7. receive the simple message
        TextMessage m = (TextMessage) consumer.receive(5000);
        System.out.println("message = " + m.getText());
        connection.close();
    }
}