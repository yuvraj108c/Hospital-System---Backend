import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpecialTreatment {
    static Connection conn = Database.getConnection();

    public static String getPatientsForSpecialTreatment(int dept_id) throws SQLException, JSONException {
        JSONArray st_details = new JSONArray();

        String query = "select c.*,p.*,st.* from specialtreatment st, checkup c, patient p where st.checkupid = c.id and c.patientid = p.id and st.departmentid = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, dept_id);
        ResultSet st = preparedStmt.executeQuery();

        while (st.next()) {
            String id = st.getString("id");
            String fname = st.getString("fname");
            String lname = st.getString("lname");
            String phone = st.getString("phone");
            String address = st.getString("address");
            String dob = st.getString("dob");
            String gender = st.getString("gender");

            JSONObject patient = new JSONObject();
            patient.put("id", id);
            patient.put("fname", fname);
            patient.put("lname", lname);
            patient.put("phone", phone);
            patient.put("address", address);
            patient.put("dob", dob);
            patient.put("gender", gender);
            st_details.put(patient);
        }

        return st_details.toString();
    }

}
