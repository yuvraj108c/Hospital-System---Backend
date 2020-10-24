import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor {
    static Connection conn = Database.getConnection();

    public static String getAllDoctors() throws SQLException, JSONException {
        JSONArray doctors_details = new JSONArray();

        String query = "select doctor.*, department.id as deptid, department.name as deptname from doctor,department where doctor.departmentid = deparment.id";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet doctors = preparedStmt.executeQuery();

        while (doctors.next()) {
            String id = doctors.getString("id");
            String fname = doctors.getString("fname");
            String lname = doctors.getString("lname");
            String phone = doctors.getString("phone");
            String address = doctors.getString("address");
            String dob = doctors.getString("dob");
            String gender = doctors.getString("gender");
            String domain = doctors.getString("domain");
            String license = doctors.getString("license");
            String deptid = doctors.getString("deptid");
            String deptname = doctors.getString("deptname");

            JSONObject doctor = new JSONObject();
            doctor.put("id", id);
            doctor.put("fname", fname);
            doctor.put("lname", lname);
            doctor.put("phone", phone);
            doctor.put("address", address);
            doctor.put("dob", dob);
            doctor.put("gender", gender);
            doctor.put("domain", domain);
            doctor.put("license", license);
            doctor.put("deptid", deptid);
            doctor.put("deptname", deptname);
            doctors_details.put(doctor);
        }

        return doctors_details.toString();
    }
}
