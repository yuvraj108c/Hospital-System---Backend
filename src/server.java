import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;

class server {
	public static void main(String args[]) throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = Database.getConnection();

		DatagramSocket serverSocket = new DatagramSocket(81);
		byte[] receiveData = new byte[1024];
		System.out.println("Server ready and waiting for clients to connect...");

		while (true) {

			try {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String client_data = new String(receivePacket.getData());
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();

				JSONObject data_json = new JSONObject(client_data);
				String action = data_json.getString("action");

				switch (action) {
					case "add_patient":
						JSONArray patient_data = data_json.getJSONArray("data");
						JSONObject patient_details_json = new JSONObject(patient_data.get(0).toString());

						String p_fname = patient_details_json.getString("fname");
						String p_lname = patient_details_json.getString("lname");
						String p_phoneNumber = patient_details_json.getString("phoneNumber");
						String p_address = patient_details_json.getString("address");
						String p_DOB = patient_details_json.getString("DOB");
						String p_gender = patient_details_json.getString("gender");

						String query1 = "Insert into patient (fname,lname,phoneNumber,address,DOB,gender) values(?,?,?,?,?,?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query1);
						preparedStmt.setString(1, p_fname);
						preparedStmt.setString(2, p_lname);
						preparedStmt.setString(3, p_phoneNumber);
						preparedStmt.setString(4, p_address);
						preparedStmt.setString(5, p_DOB);
						preparedStmt.setString(6, p_gender);
						preparedStmt.executeUpdate();

						System.out.println("Patient details saved!");

						byte[] sendData1 = "Patient details saved successfully!".getBytes();
						DatagramPacket p_sendPacket = new DatagramPacket(sendData1, sendData1.length, IPAddress, port);
						serverSocket.send(p_sendPacket);
						break;

					case "get_doctors":
						String query2 = "Select * from doctor";
						PreparedStatement p2 = conn.prepareStatement(query2);
						ResultSet doctors = p2.executeQuery();
						JSONArray doctors_details = new JSONArray();

						while (doctors.next()) {
							String d_id = doctors.getString("id");
							String d_fname = doctors.getString("fname");
							String d_lname = doctors.getString("lname");
							String d_domain = doctors.getString("domain");

							JSONObject doctor = new JSONObject();
							doctor.put("id", d_id);
							doctor.put("fname", d_fname);
							doctor.put("lname", d_lname);
							doctor.put("domain", d_domain);
							doctors_details.put(doctor);
						}

						// System.out.println(doctors_details.toString());
						byte[] sendData2 = doctors_details.toString().getBytes();
						DatagramPacket d_sendPacket = new DatagramPacket(sendData2, sendData2.length, IPAddress, port);
						serverSocket.send(d_sendPacket);
						break;

					case "add_checkup":
						JSONArray checkup_data = data_json.getJSONArray("data");
						JSONObject checkup_details_json = new JSONObject(checkup_data.get(0).toString());

						String c_date = checkup_details_json.getString("date");
						String c_patientId = checkup_details_json.getString("patientid");
						String c_doctorId = checkup_details_json.getString("doctorid");
						String c_diagnosis = checkup_details_json.getString("diagnosis");
						String c_reason = checkup_details_json.getString("reason");

						String query3 = "Insert into checkup (date,patientid,doctorid,diagnosis,reason,status) values(?,?,?,?,?,?)";
						PreparedStatement preparedStmt3 = conn.prepareStatement(query3);
						preparedStmt3.setString(1, c_date);
						preparedStmt3.setString(2, c_patientId);
						preparedStmt3.setString(3, c_doctorId);
						preparedStmt3.setString(4, c_diagnosis);
						preparedStmt3.setString(5, c_reason);
						preparedStmt3.setString(6, "Incomplete");
						preparedStmt3.executeUpdate();

						System.out.println("Checkup details saved!");

						byte[] sendData3 = "Checkup saved successfully!".getBytes();
						DatagramPacket c_sendPacket = new DatagramPacket(sendData3, sendData3.length, IPAddress, port);
						serverSocket.send(c_sendPacket);
						break;

					case "get_checkups_of_doctor":
						JSONArray doctor_data = data_json.getJSONArray("data");
						JSONObject doctor_data_json = new JSONObject(doctor_data.get(0).toString());
						String doctor_id = doctor_data_json.getString("doctorid");

						String query4 = "Select c.*,p.* from checkup c, patient p where c.doctorid = ? and p.id = c.patientid and c.status LIKE 'incomplete'";
						PreparedStatement preparedStmt4 = conn.prepareStatement(query4);
						preparedStmt4.setString(1, doctor_id);

						ResultSet doctor_checkups = preparedStmt4.executeQuery();
						JSONArray doctor_checkups_details = new JSONArray();

						while (doctor_checkups.next()) {
							String cd_patient_id = doctor_checkups.getString("patientid");
							String cd_doctor_id = doctor_checkups.getString("doctorid");
							String cd_status = doctor_checkups.getString("status");
							String cd_reason = doctor_checkups.getString("reason");
							String cd_diagnosis = doctor_checkups.getString("fname");
							String cd_date = doctor_checkups.getString("date");
							String cd_patient_name = doctor_checkups.getString("fname") + " "
									+ doctor_checkups.getString("lname");

							JSONObject checkup = new JSONObject();
							checkup.put("patient_id", cd_patient_id);
							checkup.put("patient_name", cd_patient_name);
							checkup.put("doctor_id", cd_doctor_id);
							checkup.put("status", cd_status);
							checkup.put("reason", cd_reason);
							checkup.put("diagnosis", cd_diagnosis);
							checkup.put("date", cd_date);
							doctor_checkups_details.put(checkup);
						}

						// System.out.println(doctor_checkups_details.toString());
						byte[] sendData4 = doctor_checkups_details.toString().getBytes();
						DatagramPacket cd_sendPacket = new DatagramPacket(sendData4, sendData4.length, IPAddress, port);
						serverSocket.send(cd_sendPacket);
						break;

					case "update_checkup":
						JSONArray u_checkup_data = data_json.getJSONArray("data");
						JSONObject u_checkup_data_json = new JSONObject(u_checkup_data.get(0).toString());
						String query5 = "";
						String uc_diagnosis = "";
						String uc_status = "";
						String uc_patientid = u_checkup_data_json.getString("patientid");
						String uc_date = u_checkup_data_json.getString("date");

						try {
							uc_diagnosis = u_checkup_data_json.getString("diagnosis");
						} catch (Exception e) {
						}
						try {
							uc_status = u_checkup_data_json.getString("status");
						} catch (Exception e) {

						}
						PreparedStatement p5 = null;

						if (uc_diagnosis.length() > 0 && uc_status.length() > 0) {
							query5 = "Update checkup set status = ? , diagnosis = ? where date = ? and patientid = ?";
							p5 = conn.prepareStatement(query5);
							p5.setString(1, uc_status);
							p5.setString(2, uc_diagnosis);
							p5.setString(3, uc_date);
							p5.setString(4, uc_patientid);
						} else if (uc_status.length() > 0) {
							query5 = "Update checkup set status = ? where date = ? and patientid = ?";
							p5 = conn.prepareStatement(query5);
							p5.setString(1, uc_status);
							p5.setString(2, uc_date);
							p5.setString(3, uc_patientid);
						} else if (uc_diagnosis.length() > 0) {
							query5 = "Update checkup set diagnosis = ? where date = ? and patientid = ?";
							p5 = conn.prepareStatement(query5);
							p5.setString(1, uc_diagnosis);
							p5.setString(2, uc_date);
							p5.setString(3, uc_patientid);
						}

						p5.executeUpdate();

						System.out.println("Checkup details updated!");

						byte[] sendData5 = "Checkup saved successfully!".getBytes();
						DatagramPacket uc_sendPacket = new DatagramPacket(sendData5, sendData5.length, IPAddress, port);
						serverSocket.send(uc_sendPacket);
						break;

					default:
						byte[] sendData_d = "Invalid action!".getBytes();
						DatagramPacket sendPacket_default = new DatagramPacket(sendData_d, sendData_d.length, IPAddress,
								port);
						serverSocket.send(sendPacket_default);
						System.out.println(IPAddress);
						break;
				}

			} catch (Exception e) {
				System.err.println("Got an exception!");
				System.err.println(e.getMessage());
			}

		}
	}

}