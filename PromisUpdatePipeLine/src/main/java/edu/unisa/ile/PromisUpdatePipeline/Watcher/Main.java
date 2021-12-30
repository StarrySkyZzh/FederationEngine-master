package edu.unisa.ile.PromisUpdatePipeline.Watcher;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.postgresql.PGNotification;

import edu.unisa.ile.PromisUpdatePipeline.Logging.Logging;


public class Main {
	public static void main(String[] args) {

		String url = "jdbc:postgresql://130.220.210.130:5432/lestore";
//		String url = "jdbc:postgresql://127.0.0.1:5432/lestore";
		String user = "unisa";
		String pwd = "unisa";
		String notificationChannel = "mymessage";
		int interval = 1000;
		Logging.init("log4j2.xml");
		
		PGNotificationWatcher watcher = new PGNotificationWatcher(url, user, pwd, notificationChannel, interval,
				new NotificationListener() {
					@Override
					public void doWithNotification(PGNotification[] notifications) {
						String url = "amqp://admin:admin@130.220.210.127:5672";
						String EXCHANGE_NAME = "routing";
						String bindingKey = "high";
						RMQDispatcher md = new RMQDispatcher(url, EXCHANGE_NAME);
						try {
							for (PGNotification notif : notifications) {
								
//								SerialisablePGNotification snotif = new SerialisablePGNotification(notif.getPID(), notif.getName(), notif.getParameter());
//								ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
//								ObjectOutputStream out = new ObjectOutputStream(bos) ;
//								out.writeObject(snotif);
//								out.close();
//								bos.close();
//								byte[] buf = bos.toByteArray();
//								byte[] bytesEncoded = Base64.encodeBase64(buf);
//								String message = new String(bytesEncoded);
								
								String message = notif.getParameter();
								
//								Random r = new Random();
//								message += r.nextInt(1000);

								Logging.info(notif.toString());
								md.send(bindingKey, message);
								Logging.info("sent");
							}
							md.close();	
						} catch (Exception e) {
							Logging.error(e.toString());
						}
					}
				});
		watcher.start();
	}
}
