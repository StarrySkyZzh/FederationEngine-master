package JDBCTests;

import java.sql.*;

import org.postgresql.PGNotification;

public class PGNotificationWatcher {

	public static void main(String[] args) {

		String url = "jdbc:postgresql://130.220.210.130:5432/lestore";
		String user = "unisa";
		String pwd = "unisa";
		String notificationChannel = "mymessage";
		int interval = 1000;

		PGNotificationWatcher watcher = new PGNotificationWatcher(url, user, pwd, notificationChannel, interval, new NotificationListener() {
			@Override
			public void doWithNotification(PGNotification notification){
				System.out.println("xxxx");
			}
		});
		watcher.start();
	}

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
				System.out.println("New connection created");
//				listener = new NotificationListener();
				listener.listen(conn, interval, notificationChannel);

			} catch (SQLException se) {
				if (se.getSQLState().equals("08003") || se.getSQLState().equals("08006")) {
					System.out.println("connection lost detected");
				}
				try {
					conn.close();
					System.out.println("old connection closed");
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				se.printStackTrace();
			} catch (Exception ce) {
				ce.printStackTrace();
			}
		}
	}

}
