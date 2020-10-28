import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

class server {
	public static void main(String args[]) throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");

		DatagramSocket serverSocket = new DatagramSocket(81);
		byte[] receiveData = new byte[1024];
		System.out.println("Server ready and waiting for clients to connect...");

		HashMap<String, String> clients = new HashMap<String, String>();

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
					case "login":
						JSONArray login_data = data_json.getJSONArray("data");
						JSONObject login_data_json = new JSONObject(login_data.get(0).toString());

						String email = login_data_json.getString("email").trim();
						String password = login_data_json.getString("password").trim();

						boolean valid_user = User.login(email, password);

						byte[] loginData = valid_user ? "true".getBytes() : "false".getBytes();
						DatagramPacket loginDataPacket = new DatagramPacket(loginData, loginData.length, IPAddress,
								port);
						serverSocket.send(loginDataPacket);

						break;

					case "identify_client":
						JSONArray device_data = data_json.getJSONArray("data");
						JSONObject device_data_json = new JSONObject(device_data.get(0).toString());

						String device_name = device_data_json.getString("device_name");

						clients.put(device_name, IPAddress.toString());
						System.out.println(device_name + " joined with ip address: " + IPAddress.toString());

						break;

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
						JSONArray doctor_data1 = data_json.getJSONArray("data");
						JSONObject doctor_data1_json = new JSONObject(doctor_data1.get(0).toString());

						String department_id = doctor_data1_json.getString("departmentid");
						byte[] sendData2 = User.getAllDoctors(Integer.parseInt(department_id)).getBytes();
						DatagramPacket d_sendPacket = new DatagramPacket(sendData2, sendData2.length, IPAddress, port);
						serverSocket.send(d_sendPacket);
						break;

					case "add_checkup":
						JSONArray checkup_data = data_json.getJSONArray("data");
						JSONObject checkup_details_json = new JSONObject(checkup_data.get(0).toString());

						String c_date = checkup_details_json.getString("date");
						String c_patientid = checkup_details_json.getString("patientid");
						String c_doctorid = checkup_details_json.getString("doctorid");
						String c_reason = checkup_details_json.getString("reason");

						boolean c_success = Checkup.createCheckup(Integer.parseInt(c_patientid),
								Integer.parseInt(c_doctorid), c_reason, c_date);

						String c_msg = "Error in saving checkup!";

						if (c_success) {
							c_msg = "Checkup saved successfully!";
						}

						System.out.println(c_msg);

						byte[] sendData3 = c_msg.getBytes();
						DatagramPacket c_sendPacket = new DatagramPacket(sendData3, sendData3.length, IPAddress, port);
						serverSocket.send(c_sendPacket);

						// TODO: send all data to general doctor
						byte[] rt_sendpatients = "success".getBytes();
						DatagramPacket rt_sendpatientspacket = new DatagramPacket(rt_sendpatients,
								rt_sendpatients.length,
								InetAddress.getByName(clients.get("General Doctor").replace("/", "")), 82);
						serverSocket.send(rt_sendpatientspacket);

						break;

					case "get_checkups_of_doctor":
						JSONArray doctor_data = data_json.getJSONArray("data");
						JSONObject doctor_data_json = new JSONObject(doctor_data.get(0).toString());
						String doctor_id = doctor_data_json.getString("doctorid");
						String checkup_status = doctor_data_json.getString("status");

						String checkups = Checkup.getCheckups(checkup_status, Integer.parseInt(doctor_id));

						byte[] sendData4 = checkups.toString().getBytes();
						DatagramPacket cd_sendPacket = new DatagramPacket(sendData4, sendData4.length, IPAddress, port);
						serverSocket.send(cd_sendPacket);
						break;

					case "update_checkup":
						JSONArray u_checkup_data = data_json.getJSONArray("data");
						JSONObject u_checkup_data_json = new JSONObject(u_checkup_data.get(0).toString());
						String uc_diagnosis = "";
						String uc_status = "";
						String uc_checkupid = u_checkup_data_json.getString("checkupid");

						try {
							uc_diagnosis = u_checkup_data_json.getString("diagnosis");
						} catch (Exception e) {
						}
						try {
							uc_status = u_checkup_data_json.getString("status");
						} catch (Exception e) {

						}

						boolean uc_success = Checkup.updateCheckup(Integer.parseInt(uc_checkupid), uc_diagnosis,
								uc_status);

						String uc_msg = "Error in updating checkup!";

						if (uc_success) {
							uc_msg = "Checkup updated successfully";
						}

						System.out.println(uc_msg);

						byte[] sendData5 = uc_msg.getBytes();
						DatagramPacket uc_sendPacket = new DatagramPacket(sendData5, sendData5.length, IPAddress, port);
						serverSocket.send(uc_sendPacket);
						break;

					case "get_patients_for_specialtreatment":
						JSONArray st_data = data_json.getJSONArray("data");
						JSONObject st_data_json = new JSONObject(st_data.get(0).toString());
						String dept_id = st_data_json.getString("dept_id");
						String st_status = st_data_json.getString("status");

						byte[] st_senddata = SpecialTreatment
								.getPatientsForSpecialTreatment(Integer.parseInt(dept_id), st_status).getBytes();
						DatagramPacket st_sendpacket = new DatagramPacket(st_senddata, st_senddata.length, IPAddress,
								port);
						serverSocket.send(st_sendpacket);
						break;

					case "add_special_treatment":
						JSONArray spt_data = data_json.getJSONArray("data");
						JSONObject spt_data_json = new JSONObject(spt_data.get(0).toString());

						String spt_checkupid = spt_data_json.getString("checkupid");
						// String spt_specialistid = spt_data_json.getString("specialistid");
						String spt_date = spt_data_json.getString("date");
						String spt_departmentid = spt_data_json.getString("departmentid");

						boolean spt_success = SpecialTreatment.createSpecialTreatment(Integer.parseInt(spt_checkupid),
								spt_date, Integer.parseInt(spt_departmentid));

						String spt_msg = "Error in saving Special Treatment!";
						if (spt_success) {
							spt_msg = "Special Treatment saved successfully!";
						}
						System.out.println(spt_msg);

						byte[] spt_sendData = spt_msg.getBytes();
						DatagramPacket spt_sendPacket = new DatagramPacket(spt_sendData, spt_sendData.length, IPAddress,
								port);
						serverSocket.send(spt_sendPacket);

						// TODO: send data to ent
						byte[] as_sendData = "success".getBytes();
						DatagramPacket as_sendDatapacket = new DatagramPacket(as_sendData, as_sendData.length,
								InetAddress.getByName(clients.get("ENT").replace("/", "")), 83);
						serverSocket.send(as_sendDatapacket);
						break;

					case "get_checkup_of_specialtreatment":
						JSONArray spt2 = data_json.getJSONArray("data");
						JSONObject spt2_json = new JSONObject(spt2.get(0).toString());
						String spt2_id = spt2_json.getString("specialtreatment_id");

						byte[] spt2_senddata = SpecialTreatment.getCheckupForSpecialTreatment(Integer.parseInt(spt2_id))
								.getBytes();
						DatagramPacket spt2_sendpacket = new DatagramPacket(spt2_senddata, spt2_senddata.length,
								IPAddress, port);
						serverSocket.send(spt2_sendpacket);

						break;

					case "update_specialtreatment":
						JSONArray uspt = data_json.getJSONArray("data");
						JSONObject uspt_json = new JSONObject(uspt.get(0).toString());
						String uspt_id = uspt_json.getString("specialtreatment_id");
						String uspt_specialistid = uspt_json.getString("specialist_id");
						String uspt_giventreatment = uspt_json.getString("given_treatment");
						String uspt_status = uspt_json.getString("status");

						boolean uspt_success = SpecialTreatment.updateSpecialTreatment(Integer.parseInt(uspt_id),
								uspt_giventreatment, Integer.parseInt(uspt_specialistid), uspt_status);

						String uspt_msg = "Error in updating special treatment!";

						if (uspt_success) {
							uspt_msg = "Special Treatment updated successfully";
						}

						System.out.println(uspt_msg);

						byte[] upst_sendData = uspt_msg.getBytes();
						DatagramPacket upst_sendPacket = new DatagramPacket(upst_sendData, upst_sendData.length,
								IPAddress, port);
						serverSocket.send(upst_sendPacket);
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