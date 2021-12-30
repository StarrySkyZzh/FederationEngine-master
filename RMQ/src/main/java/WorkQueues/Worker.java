package WorkQueues;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Worker {

	private final static String QUEUE_NAME = "work";

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setUri("amqp://admin:admin@130.220.210.127:5672");
	    Connection connection = factory.newConnection();
	    final Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		channel.basicQos(1);
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
				try {
				      doWork(message);
				    } catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
				      System.out.println(" [x] Done with tag: " + envelope.getDeliveryTag());
				      channel.basicAck(envelope.getDeliveryTag(), false);
				    }
			}
		};
		boolean noAck = false;
		channel.basicConsume(QUEUE_NAME, noAck, consumer);
	}

	private static void doWork(String task) throws InterruptedException {
	    for (char ch: task.toCharArray()) {
	        if (ch == '.') Thread.sleep(1000);
	    }
	}
}
