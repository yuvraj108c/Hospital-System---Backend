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
		Connection conn = database.getConnection();

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

							JSONObject doctor = new JSONObject();
							doctor.put("id", d_id);
							doctor.put("fname", d_fname);
							doctor.put("lname", d_lname);
							doctors_details.put(doctor);
						}

						// System.out.println(doctors_details.toString());
						byte[] sendData2 = doctors_details.toString().getBytes();
						DatagramPacket d_sendPacket = new DatagramPacket(sendData2, sendData2.length, IPAddress, port);
						serverSocket.send(d_sendPacket);

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