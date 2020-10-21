import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {

    public static Connection getConnection() {
        Connection conn = null;
        try {

            conn = DriverManager.getConnection(environment.connection_string, environment.db_user,
                    environment.db_password);
            System.out.println("Connected to database...");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return conn;
    }

}