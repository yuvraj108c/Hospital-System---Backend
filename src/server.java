import java.sql.Connection;

class server {
	public static void main(String args[]) throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = database.getConnection();
	}
}