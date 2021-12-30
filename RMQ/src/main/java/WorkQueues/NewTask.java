package WorkQueues;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class NewTask {
	private final static String QUEUE_NAME = "work";

	public static void main(String[] argv) throws Exception {
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setUri("amqp://admin:admin@130.220.210.127:5672");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    
	    boolean durable = true;
	    channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
	    
	    for (int i=0;i<10;i++){
	    	String message = "test "+i;
		    channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		    System.out.println(" [x] Sent '" + message + "'");
	    }
	    
	    channel.close();
	    connection.close();
	}
}