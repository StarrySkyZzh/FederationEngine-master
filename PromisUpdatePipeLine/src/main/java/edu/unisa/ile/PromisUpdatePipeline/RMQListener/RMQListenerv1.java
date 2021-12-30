package edu.unisa.ile.PromisUpdatePipeline.RMQListener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import edu.unisa.ile.PromisUpdatePipeline.Logging.Logging;

public class RMQListenerv1 {

	
	String EXCHANGE_NAME;
	Connection connection;
	Channel channel;
	public Channel getChannel() {
		return channel;
	}
	
	public static void main(String[] args) throws Exception{
		String url = "amqp://admin:admin@130.220.210.127:5672";
		String EXCHANGE_NAME = "routing";
		String bindingKeys[] = { "mid", "high" };
		Logging.init("log4j2.xml");
		RMQListenerv1 receiver = new RMQListenerv1(url, EXCHANGE_NAME);
		final Channel chan = receiver.getChannel();
		Consumer consumer = new DefaultConsumer(chan) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) {
				try {
					
//					//				decoding and deserializing object SerialisablePGNotification
//					byte[] decodedBytes = Base64.decodeBase64(body);
//					ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes) ;
//					ObjectInputStream in = new ObjectInputStream(bis);
//					in.reset();
//					SerialisablePGNotification snotif = (SerialisablePGNotification) in.readObject();
//					bis.close();
//					in.close();
//					Logging.info(" [x] Received '" + envelope.getRoutingKey() + "':'" + snotif.getPID() + " " + snotif.getName() + " " + snotif.getParameter()+ "'");
					
					String message = new String(body, "UTF-8");
					Logging.info(envelope.getRoutingKey() + "," + message + "");
					message = message.replace("'", "").replace("\"", "").replace("(","").replace(")","");
					chan.basicAck(envelope.getDeliveryTag(), true);
				} catch (Exception e) {
					Logging.error(e.toString());
				}
			}
		};
		receiver.listen(bindingKeys, consumer, "test");
	}

	public RMQListenerv1(String Url, String EXCHANGE_NAME) {
		this.EXCHANGE_NAME = EXCHANGE_NAME;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			// factory.setUri("amqp://admin:admin@130.220.210.127:5672");
			factory.setUri(Url);
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		} catch (Exception e) {
			Logging.error(e.toString());
		}
	}

	public void listen(String[] bindingKeys, Consumer consumer, String queueName) throws Exception {

		channel.queueDeclare(queueName, true, false, false, null);
		if (bindingKeys != null) {
			for (String bindingKey : bindingKeys) {
				channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
			}
		}
		boolean autoAck = false;
		channel.basicConsume(queueName, autoAck, consumer);
	}
	
	public void close() throws Exception{
		channel.close();
		connection.close();
	}
}