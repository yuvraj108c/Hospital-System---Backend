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

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			try {
				String client_data = new String(receivePacket.getData());

				JSONObject data_json = new JSONObject(client_data);
				String action = data_json.getString("action");

				switch (action) {
					case "get_all_patients":
						byte[] allPatientsData = Patient.getAllPatients().getBytes();
						DatagramPacket allPatientsDataPacket = new DatagramPacket(allPatientsData,
								allPatientsData.length, IPAddress, port);
						serverSocket.send(allPatientsDataPacket);
						break;

					case "add_patient":
						JSONArray patient_data = data_json.getJSONArray("data");
						JSONObject patient_details_json = new JSONObject(patient_data.get(0).toString());

						String p_fname = patient_details_json.getString("fname");
						String p_lname = patient_details_json.getString("lname");
						String p_phone = patient_details_json.getString("phone");
						String p_address = patient_details_json.getString("address");
						String p_dob = patient_details_json.getString("dob");
						String p_gender = patient_details_json.getString("gender");

						boolean success = Patient.createPatient(p_fname, p_lname, p_phone, p_address, p_dob, p_gender);

						String p_msg = "Error in saving patient!";
						if (success) {
							p_msg = "Patient details saved successfully!";
						}
						System.out.println(p_msg);

						byte[] sendData1 = p_msg.getBytes();
						DatagramPacket p_sendPacket = new DatagramPacket(sendData1, sendData1.length, IPAddress, port);
						serverSocket.send(p_sendPacket);
						break;

					case "get_all_doctors":
						byte[] sendData2 = Doctor.getAllDoctors().getBytes();
						DatagramPacket d_sendPacket = new DatagramPacket(sendData2, sendData2.length, IPAddress, port);
						serverSocket.send(d_sendPacket);
						break;

					case "add_checkup":
						JSONArray checkup_data = data_json.getJSONArray("data");
						JSONObject checkup_details_json = new JSONObject(checkup_data.get(0).toString());

						String c_datecreated = checkup_details_json.getString("datecreated");
						String c_patientid = checkup_details_json.getString("patientid");
						String c_doctorid = checkup_details_json.getString("doctorid");
						String c_diagnosis = checkup_details_json.getString("diagnosis");
						String c_reason = checkup_details_json.getString("reason");

						boolean c_success = Checkup.createCheckup(Integer.parseInt(c_patientid),
								Integer.parseInt(c_doctorid), c_reason, c_diagnosis, c_datecreated);

						String c_msg = "Error in saving checkup!";

						if (c_success) {
							c_msg = "Checkup saved successfully!";
						}

						System.out.println(c_msg);

						byte[] sendData3 = c_msg.getBytes();
						DatagramPacket c_sendPacket = new DatagramPacket(sendData3, sendData3.length, IPAddress, port);
						serverSocket.send(c_sendPacket);
						break;

					case "get_checkups_of_doctor":
						JSONArray doctor_data = data_json.getJSONArray("data");
						JSONObject doctor_data_json = new JSONObject(doctor_data.get(0).toString());
						String doctor_id = doctor_data_json.getString("doctorid");

						String checkups = Checkup.getCheckups("Incomplete", Integer.parseInt(doctor_id));

						byte[] sendData4 = checkups.toString().getBytes();
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

						break;
				}

			} catch (Exception e) {
				System.err.println("Got an exception!");
				System.err.println(e.getMessage());
				byte[] sendData_d = e.getMessage().getBytes();
				DatagramPacket sendPacket_default = new DatagramPacket(sendData_d, sendData_d.length, IPAddress, port);
				serverSocket.send(sendPacket_default);
			}

		}
	}

}