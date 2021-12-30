package edu.unisa.ile.PromisUpdatePipeline.Watcher;

import java.sql.*;

import edu.unisa.ile.PromisUpdatePipeline.Logging.Logging;

public class PGNotificationWatcher {

	String url;
	String user;
	String pwd;
	String notificationChannel;
	int interval;
	NotificationListener listener;

	public PGNotificationWatcher(String url, String user, String pwd, String notificationChannel, int interval, NotificationListener listener) {
		this.url = url;
		this.user = user;
		this.pwd = pwd;
		this.notificationChannel = notificationChannel;
		this.interval = interval;
		this.listener = listener;
	}

	public void start() {
		Connection conn = null;
		while (true) {
			try {
				Class.forName("org.postgresql.Driver");
				conn = DriverManager.getConnection(url, user, pwd);
				Logging.info("New connection created");
//				listener = new NotificationListener();
				listener.listen(conn, interval, notificationChannel);

			} catch (SQLException se) {
				if (se.getSQLState().equals("08003") || se.getSQLState().equals("08006")) {
					Logging.info("connection lost detected");
				}
				try {
					conn.close();
					System.out.println("old connection closed");
					Logging.info("old connection closed");
					Thread.sleep(1000);
				} catch (Exception e) {
					Logging.error(e.toString());
				}
				Logging.error(se.toString());
			} catch (Exception ce) {
				Logging.error(ce.toString());
			}
		}
	}

}
