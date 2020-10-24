import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Checkup {
    static Connection conn = Database.getConnection();

    public static boolean createCheckup(Integer patientid, Integer doctorid, String reason, String diagnosis,
            String datecreated) throws SQLException {
        String query = "Insert into checkup(patientid,doctorid,reason,diagnosis,status,datecreated) values(?,?,?,?,?,?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, 1);
        preparedStmt.setInt(2, 1);
        preparedStmt.setString(3, reason);
        preparedStmt.setString(4, "");
        preparedStmt.setString(5, "Incomplete");
        preparedStmt.setString(6, datecreated);

        return preparedStmt.executeUpdate() == 1;
    }
}
