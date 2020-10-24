import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient {
    static Connection conn = Database.getConnection();

    public static String getAllPatients() throws SQLException {
        String query = "select * from patient";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet patients = preparedStmt.executeQuery();

        while (patients.next()) {
            String d_id = patients.getString("id");
            String d_fname = patients.getString("fname");
            String d_lname = patients.getString("lname");
            String d_domain = patients.getString("domain");

            // JSONObject doctor = new JSONObject();
            // doctor.put("id", d_id);
            // doctor.put("fname", d_fname);
            // doctor.put("lname", d_lname);
            // doctor.put("domain", d_domain);
            // doctors_details.put(doctor);
        }

        return "";
    }
}
