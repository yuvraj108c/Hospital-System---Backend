import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Checkup {
    static Connection conn = Database.getConnection();

    public static boolean createCheckup(String patientid, String doctorid, String reason, String diagnosis,
            String datecreated) throws SQLException {
        String query = "Insert into patient (patientid,doctorid,reason,diagnosis,status,datecreated) values(?,?,?,?,?,?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, patientid);
        preparedStmt.setString(2, doctorid);
        preparedStmt.setString(3, reason);
        preparedStmt.setString(4, "");
        preparedStmt.setString(5, "Incomplete");
        preparedStmt.setString(6, datecreated);

        return preparedStmt.executeUpdate() == 1;
    }
}
