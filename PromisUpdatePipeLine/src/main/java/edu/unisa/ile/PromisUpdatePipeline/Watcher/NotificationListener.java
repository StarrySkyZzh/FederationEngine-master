package edu.unisa.ile.PromisUpdatePipeline.Watcher;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import edu.unisa.ile.PromisUpdatePipeline.Logging.Logging;

public class NotificationListener {

	Connection conn;

	public void listen(Connection conn, int interval, String channel) throws Exception{
		Statement stmt = conn.createStatement();
		stmt.execute("LISTEN " + channel);
		stmt.close();
		PGConnection pgconn = (PGConnection) conn;
		
			while (true) {
				// issue a dummy query to contact the backend
				// and receive any pending notifications.
				Statement dummyStmt = conn.createStatement();
				ResultSet rs = dummyStmt.executeQuery("SELECT 1");
				rs.close();
				dummyStmt.close();

				Logging.info("heart beat");
				PGNotification notifications[] = pgconn.getNotifications();
				if (notifications != null) {
					doWithNotification(notifications);
				}
				// wait a while before checking again for new
				// notifications
				Thread.sleep(interval);
			}

	}

	public void doWithNotification(PGNotification[] notifications) {
		for (PGNotification notification : notifications) {
			Logging.info("Got notification: " + notification.getParameter());
		}
	}
}
