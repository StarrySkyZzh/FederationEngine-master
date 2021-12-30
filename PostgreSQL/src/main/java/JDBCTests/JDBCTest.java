package JDBCTests;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCTest {
   public static void main(String args[]) {
      Connection c = null;
      try {
         Class.forName("org.postgresql.Driver");
         c = DriverManager
            .getConnection("jdbc:postgresql://130.220.210.130:5432/promis3",
            "unisa", "unisa");
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName()+": "+e.getMessage());
         System.exit(0);
      }
      System.out.println("Opened database successfully");
   }
}