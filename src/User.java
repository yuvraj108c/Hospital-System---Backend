import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
    static Connection conn = Database.getConnection();

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String login(String email, String password) throws SQLException, JSONException {

        String query = "select users.*,department.name as dname from users,department where email = ? and password = ? and department.id = users.departmentid";

        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, email);
        preparedStmt.setString(2, MD5(password));

        ResultSet rs = preparedStmt.executeQuery();

        if (rs.next()) {
            return rs.getString("dname");
        }

        return "";
    }

    public static String getAllDoctors(int dept_id) throws SQLException, JSONException {
        JSONArray doctors_details = new JSONArray();

        String query = "select users.*, department.id as deptid, department.name as deptname from users,department where users.departmentid = department.id and department.id = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, dept_id);
        ResultSet doctors = preparedStmt.executeQuery();

        while (doctors.next()) {
            String id = doctors.getString("id");
            String fname = doctors.getString("fname");
            String lname = doctors.getString("lname");
            String phone = doctors.getString("phone");
            String address = doctors.getString("address");
            String dob = doctors.getString("dob");
            String gender = doctors.getString("gender");
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
            doctor.put("deptid", deptid);
            doctor.put("deptname", deptname);
            doctors_details.put(doctor);
        }

        return doctors_details.toString();
    }

}
