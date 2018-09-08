package h2;

import java.sql.Connection;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

class e {
	public static String[] exceptionword= new String[150];
	public static String[] hazard = new String[101];

}


public class h2connect {
	// store brs
	static HashMap<Integer, String> map = new HashMap<Integer, String>();
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:C:\\Users\\skdisk\\Documents\\URP\\github\\db\\roughike-BottomBar";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";
	public static void main(String[] args) {
		// Read the exception handling list file
		File f = new File("C:\\Users\\skdisk\\Documents\\jaava\\h2\\src\\h2\\exception.txt");
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br1 = new BufferedReader(fr);
			int i=1;
			int index=0;
			while (true) {
				String str = br1.readLine(); // buffer로부터 한 줄씩 읽어서 저장
				if (str == null) { // 읽을 내용이 없으면 반복문 종료
					break;
				} else {
					if (i % 2 == 0) {
						e.exceptionword[index] = str;
						index+=1;
					}
					i++;
				}
			}

			br1.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
		Connection conn = null;
		Statement stmt = null;

		try {
			// STEP 1: Register JDBC driver
			Class.forName(JDBC_DRIVER);
			// STEP 2: Open a connection

			System.out.println("Connecting to database...");

			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// STEP 3: Execute a query

			System.out.println("Connected database successfully...");
			stmt = conn.createStatement();
			String sql = "SELECT BUG_ID, DESC_COR FROM BUG_INFO";
			ResultSet rs = stmt.executeQuery(sql);
			
			//출력할 파일
			PrintWriter pw = new PrintWriter(new File("roughike-BottomBar.csv"));
	        StringBuilder sb = new StringBuilder();
	        sb.append("id");
	        sb.append(',');
	        sb.append("body");
	        sb.append(',');
	        sb.append("exception");
	        sb.append(',');
	        sb.append("security_exception");
	        sb.append('\n');
			pw.write(sb.toString());

			while (rs.next()) {
				
				// Retrieve by column name
				int id = rs.getInt("BUG_ID");
				String body = rs.getString("DESC_COR");
				map.put(id, body);
				StringBuilder bs = new StringBuilder();
				String text=body.replaceAll("\n", " ");
				text=text.replaceAll(",", " ");
				
		        bs.append(id);
		        bs.append(',');
		        bs.append(text);
		        bs.append(',');
		       
				f = new File("C:\\Users\\skdisk\\Documents\\jaava\\h2\\src\\h2\\exception.txt");
				try {
					FileReader fr = new FileReader(f);
					BufferedReader br1 = new BufferedReader(fr);
					int i=1;
					int index=0;
					while (true) {
						String str = br1.readLine(); // buffer로부터 한 줄씩 읽어서 저장
						if (str == null) { // 읽을 내용이 없으면 반복문 종료
							break;
						} else {
							if (i % 2 == 0) {
								// exception 포함하는지 확인
								if(body.contains(str)) {
									System.out.println(id+": " +str);
									bs.append(1);
									bs.append(',');
								}
								
							}
							i++;
						}
					}
					if (body.contains("java.lang.IllegalArgumentException")||body.contains("java.lang.SecurityException")||body.contains("javax.net.ssl.SSLException")||body.contains("java.security.AccessControlException")||body.contains("org.bukkit.plugin.IllegalPluginAccessException")||body.contains("java.security.cert.CertificateException")||body.contains("javax.net.ssl.SSLPeerUnverifiedException")||body.contains("sun.security.provider.certpath.SunCertPathBuilderException")||body.contains("javax.net.ssl.SSLHandshakeException")) {
						bs.append(1);
					}
			        bs.append('\n');
			        pw.write(bs.toString());
					br1.close();
					fr.close();
			        
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			// STEP 4: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
			pw.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Exit!");
		System.out.println(map.size());
	}
}