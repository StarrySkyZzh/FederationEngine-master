package edu.unisa.ile.PromisUpdatePipeline.Watcher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import edu.unisa.ile.PromisUpdatePipeline.Logging.Logging;

public class RMQDispatcher {

	String EXCHANGE_NAME;
	Connection connection;
	Channel channel;

	public RMQDispatcher(String Url, String EXCHANGE_NAME) {
		this.EXCHANGE_NAME = EXCHANGE_NAME;
		try{
			ConnectionFactory factory = new ConnectionFactory();
//			factory.setUri("amqp://admin:admin@130.220.210.127:5672");
			factory.setUri(Url);
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		} catch(Exception e){
			Logging.error(e.toString());
		}
		
	}

	public void send(String bindingKey, String message) throws Exception {
		channel.basicPublish(EXCHANGE_NAME, bindingKey, null, message.getBytes());
		Logging.info(" [x] Sent '" + bindingKey + "':'" + message + "'");
	}
	
	public void close() throws Exception{
		channel.close();
		connection.close();
	}

}
