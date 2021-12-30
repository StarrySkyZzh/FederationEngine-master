package routing;

import java.io.IOException;
import java.util.Random;

import com.rabbitmq.client.*;

public class MsgDispatch {

	private static final String EXCHANGE_NAME = "routing";

    public static void main(String[] argv)
                  throws Exception {
    	String priorities[]={"low", "mid", "high"};
    	
    	ConnectionFactory factory = new ConnectionFactory();
	    factory.setUri("amqp://admin:admin@130.220.210.127:5672");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        Random r = new Random();
        String priority="";
        String message="";
        for (int i =0; i<10000; i++){
        	
        	priority = priorities[r.nextInt(3)];
            message = "test " +i;

            channel.basicPublish(EXCHANGE_NAME, priority, null, message.getBytes());
            System.out.println(" [x] Sent '" + priority + "':'" + message + "'");
        }

//        channel.basicPublish(EXCHANGE_NAME, priority, null, message.getBytes());
//        System.out.println(" [x] Sent '" + priority + "':'" + message + "'");

        channel.close();
        connection.close();
    }
}
