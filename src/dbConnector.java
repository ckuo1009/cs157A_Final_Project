import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

public class dbConnector {
    private static final Logger logger = Logger.getLogger(dbConnector.class.getName());

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String password = sc.nextLine();

        try {
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
        }
        catch (Exception E) {
            System.err.println("Unable to load driver.");
            logger.log(Level.SEVERE, "Unable to load driver.", E);
        }
        try {
            Connection C = DriverManager.getConnection("jdbc:mysql://localhost:3306/exoticasia", "root", password);
            Statement s = C.createStatement();
            String sql="select * from user";
            s.executeQuery(sql);
            ResultSet res = s.getResultSet();
            if (res!=null) {
                while(res.next()) {
                    System.out.println("\n"+res.getString(1) + "\t"+res.getString(2));
                }
            }
            C.close();
        }
        catch (SQLException E) {
            System.out.println("SQLException:" + E.getMessage());
            System.out.println("SQLState:" + E.getSQLState());
            System.out.println("VendorError:" + E.getErrorCode());
        }
    }
}