import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Patient {
    static Connection conn = Database.getConnection();

    public static String getAllPatients() throws SQLException, JSONException {
        JSONArray patients_details = new JSONArray();

        String query = "select * from patient";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet patients = preparedStmt.executeQuery();

        while (patients.next()) {
            String id = patients.getString("id");
            String fname = patients.getString("fname");
            String lname = patients.getString("lname");
            String phone = patients.getString("phone");
            String address = patients.getString("address");
            String dob = patients.getString("dob");
            String gender = patients.getString("gender");

            JSONObject patient = new JSONObject();
            patient.put("id", id);
            patient.put("fname", fname);
            patient.put("lname", lname);
            patient.put("phone", phone);
            patient.put("address", address);
            patient.put("dob", dob);
            patient.put("gender", gender);
            patients_details.put(patient);
        }

        return patients_details.toString();
    }
}
