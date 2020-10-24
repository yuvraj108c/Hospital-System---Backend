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

    public static String getValidCheckups(String status, int doctorid) throws SQLException {
        String query = "select c.*,p.id as pid , p.fname as pfname,p.lname as plname, p.phone as pphone, p.gender as pgender,d.id as did, d.fname as dfname, d.lname as dfname, d.gender as dgender, d.domain as ddomain,d.phone as dphone  from checkup c, patient p, doctor d where c.patientid = p.id and c.doctorid = d.id and datecreated >= CURDATE() and status LIKE ? and c.doctorid = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, status);
        preparedStmt.setInt(2, doctorid);

        ResultSet checkups = preparedStmt.executeQuery();
        JSONArray checkup_details = new JSONArray();

        while (checkups.next()) {
            String id = checkups.getString("id");
            String pid = checkups.getString("pid");
            String pfname = checkups.getString("pfname");
            String plname = checkups.getString("plname");
            String pphone = checkups.getString("pphone");
            String pgender = checkups.getString("pgender");
            String did = checkups.getString("did");
            String dfname = checkups.getString("dfname");
            String dlname = checkups.getString("dlname");
            String dgender = checkups.getString("dgender");
            String ddomain = checkups.getString("ddomain");
            String dphone = checkups.getString("dphone");

            JSONObject checkup = new JSONObject();
            checkup.put("checkupid", id);
            checkup.put("patient_id", pid);
            checkup.put("patient_fname", pfname);
            checkup.put("patient_lname", plname);
            checkup.put("patient_phone", pphone);
            checkup.put("patient_gender", pgender);
            checkup.put("doctor_id", did);
            checkup.put("doctor_fname", dfname);
            checkup.put("doctor_lname", dlname);
            checkup.put("doctor_phone", dphone);
            checkup.put("doctor_gender", dgender);
            checkup.put("doctor_domain", ddomain);

            checkup_details.put(checkup);
        }
        return checkup_details.toString();
    }
}
