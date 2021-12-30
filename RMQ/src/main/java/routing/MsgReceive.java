package routing;

import com.rabbitmq.client.*;

public class MsgReceive {
	private static final String EXCHANGE_NAME = "routing";

	public static void main(String[] argv) throws Exception {
		String priorities[]={"mid", "high"};
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setUri("amqp://admin:admin@130.220.210.127:5672");
	    Connection connection = factory.newConnection();
	    final Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		String queueName = "test";
		channel.queueDeclare("test", true, false, false, null);


		for (String priority : priorities) {
			channel.queueBind(queueName, EXCHANGE_NAME, priority);
		}

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) {
				try{
					String message = new String(body, "UTF-8");
					System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
					channel.basicAck(envelope.getDeliveryTag(), true);
				} catch(Exception e){
					System.out.println(e);
				}
			}
		};
		boolean autoAck = false;
		channel.basicConsume(queueName, autoAck, consumer);
	}
}
