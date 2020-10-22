import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class server {
	public static void main(String args[]) throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = database.getConnection();

		DatagramSocket serverSocket = new DatagramSocket(81);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		System.out.println("Server ready and waiting for clients to connect...");

		while (true) {

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

					String fname = patient_details_json.getString("fname");
					String lname = patient_details_json.getString("lname");
					String phoneNumber = patient_details_json.getString("phoneNumber");
					String address = patient_details_json.getString("address");
					String DOB = patient_details_json.getString("DOB");
					String gender = patient_details_json.getString("gender");

					try {
						String query = "Insert into patient (fname,lname,phoneNumber,address,DOB,gender) values(?,?,?,?,?,?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString(1, fname);
						preparedStmt.setString(2, lname);
						preparedStmt.setString(3, phoneNumber);
						preparedStmt.setString(4, address);
						preparedStmt.setString(5, DOB);
						preparedStmt.setString(6, gender);
						preparedStmt.executeUpdate();

						System.out.println("Patient details saved!");

						sendData = "Patient details saved successfully!".getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
						serverSocket.send(sendPacket);
					} catch (Exception e) {
						System.err.println("Got an exception!");
						System.err.println(e.getMessage());
					}

					break;

				default:
					break;
			}

		}
	}

}