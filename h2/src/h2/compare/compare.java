package compare;

import java.sql.Connection;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

class project {
	public static String a="roughike-BottomBar";
	public static String b = "fastlane-fastlane";
	public static String c = "bumptech-glide";
	public static String d = "zxing-zxing";
	public static String e = "NativeScript-NativeScript";
	public static String f = "affollestad-material-dialogs";
	public static String g = "google-ExoPlayer";
	public static String h = "realm-realm-java";
}



public class compare {
	static ArrayList sbrs=new ArrayList<Integer>();
	// store brs
	static HashMap<Integer, String> map = new HashMap<Integer, String>();
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static String DB_URL = "jdbc:h2:C:\\Users\\skdisk\\Documents\\URP\\github\\db\\roughike-BottomBar";
	static String[] ps = new String[8];
	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";
	public static void main(String[] args) throws IOException {
		      
		ps[0]=project.a;
		ps[1]=project.b;
		ps[2]=project.c;
		ps[3]=project.d;
		ps[4]=project.e;
		ps[5]=project.f;
		ps[6]=project.g;
		ps[7]=project.h;
		for(int i =0; i<8;i++) {
			DB_URL = "jdbc:h2:C:\\Users\\skdisk\\Documents\\URP\\github\\db\\"+ps[i];
			System.out.println(DB_URL);
			Connection conn = null;
			Statement stmt = null;
			
			File csv = new File("C:\\Users\\skdisk\\Documents\\jaava\\h2\\src\\compare\\"+ps[i]+".csv");
            BufferedReader br = new BufferedReader(new FileReader(csv));
            String line = "";
            int row =0;
            int sid=0;
            
            String ys = "";
            String pre="";
            while ((line = br.readLine()) != null) {
				try {
					String array[] = line.split(",");
					ys = array[3];
					sid = Integer.parseInt(array[0]);
					if(ys.contains("o")) {
						sbrs.add(sid);
					}
				} catch (Exception e) {

				}
				
            }
            br.close();
			

			try {
				// STEP 1: Register JDBC driver
				Class.forName(JDBC_DRIVER);
				// STEP 2: Open a connection

				System.out.println("Connecting to database...");

				conn = DriverManager.getConnection(DB_URL, USER, PASS);

				// STEP 3: Execute a query

				System.out.println("Connected database successfully...");
				stmt = conn.createStatement();
				String sql = "SELECT BUG_ID, OPEN_DATE, SMR_COR, DESC_COR FROM BUG_INFO";
				ResultSet rs = stmt.executeQuery(sql);
				
				//출력할 파일
				PrintWriter pw = new PrintWriter(new File(ps[i]+".csv"));
		        StringBuilder sb = new StringBuilder();
		        sb.append("id");
		        sb.append(',');
		        sb.append("date");
		        sb.append(',');
		        sb.append("report");
		        sb.append(',');
		        sb.append("security");
		        sb.append('\n');
				pw.write(sb.toString());

				while (rs.next()) {
					
					// Retrieve by column name
					int id = rs.getInt("BUG_ID");
					String title = rs.getString("SMR_COR");
					String date=rs.getString("OPEN_DATE");
					String body = rs.getString("DESC_COR");
				
					StringBuilder bs = new StringBuilder();
					String text=body.replaceAll("\n", " ");
					text=text.replaceAll(",", " ");
					title=title.replaceAll(",", " ");
					title=title.replaceAll("\n", " ");
					date=date.replaceAll("\n", "");
					date=date.substring(2, 10);					
			        bs.append(id);
			        bs.append(',');
			        bs.append(date);
			        bs.append(',');
			        bs.append("Issue "+id+" : "+title+" "+text);
			        bs.append(',');
					if (sbrs.contains(id)) {
						bs.append(1);
					} else {
						bs.append(0);
					}
			        bs.append('\n');

					pw.write(bs.toString());

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
			
		}
		
	

	}
}