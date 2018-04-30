package trytry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.h2.jdbc.JdbcSQLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ShowCaseParser {
	static String gitUrl = "https://github.com/";
	static String urlSuffix = "?utf8=&q=is%3Aissue+is%3Aclosed+label%3A";

	
	static String apiUrl = "https://api.github.com/repos/";
	static String apiSuffix = "?state=closed&labels=bug";
	
	static String inputFile = "input.csv";
	
	static ArrayList<String> fullNameList = new ArrayList<String>(); 
	static ArrayList<String> targetOssArray = new ArrayList<String>();
	static HashMap<String, String> labelNameMap = new HashMap<String, String>(); 
	
	static boolean createDB = true;
	public static void main(String[] a) throws Exception
	{		
		getOssList();
		setLabelNameMap();
		System.out.println(targetOssArray.size());
		
		int num = 0 ;
		
		DecimalFormat format = new DecimalFormat("#.###");  
		
		for(int i = 0 ; i<targetOssArray.size(); i++){
			String targetName = targetOssArray.get(i).replace("/issues", "");

			System.out.println(targetOssArray.get(i).replace("/issues", "").replaceAll("/", "-"));
			System.out.println("[CURRENT STATE] "+format.format((i+1.0)/targetOssArray.size()*100)+"%  "+targetName);
						
			Document doc;		
						
			try{
				String url = gitUrl+targetOssArray.get(i)+urlSuffix+labelNameMap.get(targetName);
				System.out.println(url);
				doc = Jsoup.connect(url).maxBodySize(0).timeout(100000).get();
			}catch(ConnectException e){
				System.err.println("CONNECTION TIMED OUT - 109");
				continue;
			}catch(SocketTimeoutException e1){
				System.err.println("CONNECTION TIMED OUT Socket - 109");
				continue;
			}

			int lastID = Integer.parseInt(String.valueOf(doc.select("div.float-left.col-9.p-2.lh-condensed").get(0).getElementsByAttribute("href")).split("\"")[1].split("issues/")[1]);
			
			try{
				String url = gitUrl+targetOssArray.get(i)+urlSuffix+labelNameMap.get(targetName)+"+sort%3Acreated-asc";
				System.out.println(url);
				doc = Jsoup.connect(url).maxBodySize(0).timeout(10000).get();
			}catch(ConnectException e){
				System.err.println("CONNECTION TIMED OUT - 109");
				continue;
			}catch(SocketTimeoutException e1){
				System.err.println("CONNECTION TIMED OUT Socket - 109");
				continue;
			}
			
			int startID = Integer.parseInt(String.valueOf(doc.select("div.float-left.col-9.p-2.lh-condensed").get(0).getElementsByAttribute("href")).split("\"")[1].split("issues/")[1]);
						
			System.out.println(startID + " " +lastID);
			
			HashMap<Integer, ArrayList<Integer>> issuePullMap = new HashMap<Integer, ArrayList<Integer>>(); 
			HashMap<Integer, ArrayList<String>> issueCommitMap = new HashMap<Integer, ArrayList<String>>();
			
			for(int j = startID; j<=lastID; j++){
				String rfileName = "C:\\Users\\skdisk\\"+targetOssArray.get(i).replace("/issues", "").replaceAll("/", "_")+"_rfile.txt" ;
				String ifileName = "C:\\Users\\skdisk\\"+targetOssArray.get(i).replace("/issues", "").replaceAll("/", "_")+"_issues.txt" ;
				String ibodyfileName = "C:\\Users\\skdisk\\"+targetOssArray.get(i).replace("/issues", "").replaceAll("/", "_")+"_issuesbody.txt" ;
				String commentsfile="C:\\Users\\skdisk\\"+targetOssArray.get(i).replace("/issues", "").replaceAll("/", "_")+"_comments.txt" ;
				
				System.out.println(j);
				Document doc2 = null;
				Document docpull = null;
				if(j==296) {
					j++; //no 296 page, skip
				}
				String key = targetOssArray.get(i).replace("/issues", "");
				String url = gitUrl+targetOssArray.get(i)+"/"+j+"\t";
				
				try{
					doc2 = Jsoup.connect(url).maxBodySize(0).timeout(10000).ignoreContentType(true).get();				
				}catch(ConnectException e){
					System.err.println("ERROR TIME CONNECT");
					continue;
				}catch( SocketTimeoutException e2){
					System.err.println("errot socket timeout");
					continue;
				}
				String title = String.valueOf(doc2.getElementsByTag("title")).toLowerCase();

				//issues 
				String temptitle = String.valueOf(doc2.select("span.js-issue-title"));
				String issuetitle= temptitle.split("</")[0].split(">")[1];
				String tempopendate=String.valueOf(doc2.select("div.TableObject-item.TableObject-item--primary").select("relative-time"));
				String opendate=null;
				String closedate=null;
				String body=null;
				try {
					opendate=tempopendate.split("T")[0].split("\"")[1];
				}
				catch(ArrayIndexOutOfBoundsException exception) {
					//case: ~~ wants to merge 1 commit into ~~ form ~~
				    continue;
				}
				
				String tempclosedate=String.valueOf(doc2.select("div.discussion-item.discussion-item-closed").select("relative-time"));
				String noclosedate=String.valueOf(doc2.select("div.timeline-comment-header.clearfix").select("relative-time"));
				Elements elementdate=doc2.select("div.timeline-comment-header.clearfix").select("relative-time");
				int sizeof=elementdate.size();
				try {
					closedate=tempclosedate.split("T")[0].split("\"")[1];
				}catch(ArrayIndexOutOfBoundsException exception){
					noclosedate=String.valueOf(elementdate.get(sizeof-1));
					closedate=noclosedate.split("T")[0].split("\"")[1]; //not pull, no close date
				}
				//pull
				if(title.contains("pull request #"+j)) {
					closedate=opendate;
					noclosedate=String.valueOf(elementdate.get(0));
					opendate=noclosedate.split("T")[0].split("\"")[1];
				}
				BufferedWriter fw = new BufferedWriter(new FileWriter(ifileName, true));
				fw.write("(("+j+")) "+issuetitle+" ((open)) "+opendate+"  ((close)) "+closedate);
				fw.newLine();
				fw.flush();
				fw.close();
				
				//body
				Elements tempbody= doc2.select("td.d-block.comment-body.markdown-body.js-comment-body");
				body=String.valueOf(tempbody.get(0)).split("<td class=\"d-block comment-body markdown-body  js-comment-body\">")[1];
				BufferedWriter bodyw = new BufferedWriter(new FileWriter(ibodyfileName, true));
				bodyw.write("(("+j+"))  "+body);
				bodyw.newLine();
				bodyw.flush();
				bodyw.close();
				
				//comments
				String cdate=null;
				String cbody=null;
				String userid=null;
				Elements celements=doc2.select("div.timeline-comment-wrapper");
				
				for(int c=1;c<celements.size();c++) {
					BufferedWriter commentw = new BufferedWriter(new FileWriter(commentsfile, true));
					userid=String.valueOf(celements.get(c).select("a.author.text-inherit")).split(">")[1].split("<")[0];
					cdate=String.valueOf(celements.get(c).select("relative-time")).split("\"")[1].split("T")[0];
					cbody=String.valueOf(celements.get(c).select("td.d-block.comment-body.markdown-body.js-comment-body")).split("<td class=\"d-block comment-body markdown-body  js-comment-body\">")[1];
					System.out.println(cbody);
					commentw.write("(("+j+")) "+userid+"  ((date)) "+cdate+ "  ((body)) "+cbody);
					commentw.newLine();
					commentw.flush();
					commentw.close();
				}
								
				
				
				
				//pull request's revised files
				String pullurl=gitUrl+targetName+"/pull/"+j+"/files\t";
				if(title.contains("pull request #"+j)){
					System.out.println(pullurl);
					try{
						docpull = Jsoup.connect(pullurl).maxBodySize(0).timeout(10000).ignoreContentType(true).get();				
					}catch(ConnectException e){
						System.err.println("ERROR TIME CONNECT");
						continue;
					}catch( SocketTimeoutException e2){
						System.err.println("errot socket timeout");
						continue;
					}
					String rfilen = String.valueOf(docpull.select("a.link-gray-dark"));
					Elements element=docpull.select("a.link-gray-dark");
					
					for(int z = 0; z<element.size();z++) {
						String filen=element.get(z).attr("title");
						BufferedWriter fw2 = new BufferedWriter(new FileWriter(rfileName, true));
						fw2.write(j+" "+filen);
						fw2.newLine();
						fw2.flush();
						fw2.close();
					}
					continue;
				}
				
				Elements elements = doc2.select("h4.discussion-item-ref-title");
				for(int k = 0 ; k < elements.size(); k++){
					Element element = elements.get(k);
					String linkURL = element.select("a").get(0).attr("href");
					int pullID = -1;
					if(linkURL.contains(key+"/pull/")){
						pullID = Integer.parseInt(linkURL.replace(key+"/pull/", "").replace("/", ""));
						if(issuePullMap.containsKey(j)){
							ArrayList<Integer> data = issuePullMap.get(j);
							data.add(pullID);
							issuePullMap.replace(j, data);
						}else{
							ArrayList<Integer> data = new ArrayList<Integer>();
							data.add(pullID);
							issuePullMap.put(j, data);							
						}	
						/*
						String fileName = "C:\\Users\\skdisk\\pullparse.txt" ;
						try {

							BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
							fw.write(j+" "+k+" [PULL] "+linkURL+" "+pullID );
							fw.newLine();
							fw.flush();
							fw.close();
						}catch(Exception e){
							e.printStackTrace();
						}
						*/
						//System.out.println(j+" "+k+" [PULL] "+linkURL+" "+pullID);//이거를 파일에	
					}
				}
				
				elements = doc2.select("a.commit-id");
				for(int k = 0 ; k < elements.size(); k++){
					Element element = elements.get(k);
					String linkURL = element.attr("href");
					String commitID = linkURL.replace(key+"/commit/","").replace("/", "");
					if(issueCommitMap.containsKey(j)){
						ArrayList<String> data = issueCommitMap.get(j);
						data.add(commitID);
						issueCommitMap.replace(j, data);
					}else{
						ArrayList<String> data = new ArrayList<String>();
						data.add(commitID);
						issueCommitMap.put(j, data);							
					}	
					/*
					String fileName = "C:\\Users\\skdisk\\commitparse.txt" ;
					try {

						BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
						fw.write(j+" "+k+" [COMMIT] "+linkURL);
						fw.newLine();
						fw.flush();
						fw.close();
					}catch(Exception e){
						e.printStackTrace();
					}
*/
					//System.out.println(j+" "+k+" [COMMIT] "+linkURL); //이거를 파일에
				}
			}
		}
		System.out.println("end");
	}
	
	private static void setLabelNameMap() {		
		labelNameMap.put("bitcoin/bitcoin", "Bug");
		//labelNameMap.put("getsentry/sentry", "\"Type%3A+Bug\"");
		
	}

	private static void getOssList() throws IOException {
		BufferedReader br = new BufferedReader (new FileReader (inputFile));
		String str;
		boolean flag = false;
		while((str=br.readLine())!=null){			
			if(!flag){
				flag = true;
				continue;
			}
			targetOssArray.add(str.split(",")[0]+"/"+str.split(",")[1]+"/issues");
		}
	}

	static public HashMap<String, Connection> connMap = new HashMap<String, Connection>();;;
	
	public static boolean insertText(int bugID, String simpleText, String justText, String htmlText, Connection conn) throws Exception
	{
		try
		{	Statement q2 = conn.createStatement();
			if(!q2.executeQuery("SELECT * FROM ISSUE_REPORT WHERE BUG_ID = "+bugID).next()){				
				Statement q = conn.createStatement();			
				q.execute("INSERT INTO ISSUE_REPORT VALUES ("+bugID+",'"+simpleText+"','"+justText+"','"+htmlText+"');");
				q.close();
			}
			q2.close();
			return true;
						
		}
		catch(Exception e1)
		{
			System.err.print("insertText ISSUE_REPORT");
			
			e1.printStackTrace();
			return false;
		}
	}	
	public static int selectIssueReport(int bugID,  Connection conn) throws Exception{
		Statement q2 = conn.createStatement();
		int result = -1;
		try{
			ResultSet rs = q2.executeQuery("SELECT * FROM ISSUE_REPORT ORDER BY BUG_ID DESC"); 
			if(rs.next()){
				result =rs.getInt("BUG_ID");
			}
			q2.close();
		}catch(JdbcSQLException e){
			e.printStackTrace();
			System.err.println("ERROR JDBCSQLEXCEPTION");
		}
		return result;
	}
	
	public static void insertCuezilla(int bugID, int item, int actionKey, int obsKey, int expKey, int s2rKey, int buildKey, int uiKey, 
			int code, int sTrace, int patch, int screenShot,  Connection conn) throws Exception
	{
		try
		{
			
				Statement q = conn.createStatement();
				q.execute("INSERT INTO CUEZILLA VALUES ("+bugID+","+ item+","+ actionKey+","+ obsKey+","+ expKey+","+ s2rKey+","+ buildKey+","+ uiKey+","+ code+","+ sTrace+","+ patch+","+ screenShot+");");
				q.close();
			
						
		}
		catch(Exception e1)
		{
			System.err.print("CUEZILLA insertCuezilla");
		}
	}	
	
	static HashSet<String> keyMap = new HashSet<String>(); 
	private static void createTables(Connection conn, String key) throws Exception {	

		Statement q = conn.createStatement();
		try
		{
			q.execute("Create Table ISSUE_REPORT("
					+ "BUG_ID int PRIMARY KEY,"
					+ "SIMPLE_TEXT VARCHAR(99999),"
					+ "JUST_TEXT VARCHAR(99999),"
					+ "HTML_TEXT VARCHAR(99999));");
			
			System.out.println("--- ISSUE_REPORT TABLE CREATED..." + key);
		}catch(Exception e)
		{
			System.err.println("---ISSUE_REPORT TABLE CREATION ERROR..."+ key);
		}
		q = conn.createStatement();
		try
		{
//			q.execute("Create Table CUEZILLA("
//					+ "BUG_ID int PRIMARY KEY,"
//					+ "ITEM int,"
//					+ "ACTION_KEY int,"
//					+ "OBS_KEY int,"
//					+ "EXP_KEY int,"
//					+ "S2R_KEY int,"
//					+ "BUILD_KEY int,"
//					+ "UI_KEY int,"
//					+ "CODE int,"
//					+ "STRACE int,"
//					+ "PATCH int,"
//					+ "SCREENSHOT int);");
//			
//			System.out.println("--- CUEZILLA TABLE CREATED..."+ key);
		}catch(Exception e)
		{
			System.err.println("---CUEZILLA TABLE CREATION ERROR..."+key);
		}	
		
	}
	private static void dropTable(String key) throws Exception
	{
		try{
			Statement q = connMap.get(key).createStatement();
			q.execute("DROP TABLE ISSUE_REPORT;");
			System.out.println("---DROP ISSUE_REPORT TABLE...");
			q.execute("DROP TABLE CUEZILLA;");
			System.out.println("---DROP CUEZILLA TABLE...");
		}catch(Exception e){
			System.err.println("DROP TABLE ERROR "+key);
		}
		
	}
	

	private static String removeBracket(String content) {
//		System.out.println(content);
		String result = "";
		if(!content.contains(">") && !content.contains("<"))
			return content;
		
		String[] data = content.split("<");
		for(int i = 0 ; i<data.length; i++){			
			if(!data[i].contains(">"))
				result = result + data[i]+" ";
			else{
				String[] data2 = data[i].split(">",2);
				result = result + data2[1];			
			}
		}
			
//		System.out.println(result);
		
		return result;
	}

	public static List<String> readValidJsonStrings(String allText) {   
	    List<String> jsonList = new ArrayList<String>();
	    int[] endsAt = new int[1];
	    endsAt[0] = 0;
	    int num = 0;
	    while(num >= 0) {
	    	try{
		        int startsAt = allText.indexOf("{", endsAt[0]);
//		        System.out.println(startsAt+" "+endsAt[0]);
		        num = startsAt-endsAt[0];
		        if(startsAt < endsAt[0]){
//		        	System.err.println(startsAt+" "+endsAt[0]);
		        	break;
		        }
		        if (startsAt == -1) {
		            break;
		        }
		        
		        String aJson = parseJson(allText, startsAt, endsAt);
		        if(aJson.equals(null))
		        	break;
		        jsonList.add(aJson);
	    	}catch(Exception e ){
	    		return jsonList;
	    	}
	    }
		return jsonList;
	}

	private static String parseJson(String str, int startsAt, int[] endsAt) {

	    Stack<Integer> opStack = new Stack<Integer>();
	    int i = startsAt + 1;
	    while (i < str.length()) {

	        if (str.charAt(i) == '}') {
	            if (opStack.isEmpty()) {
	                endsAt[0] = i + 1;
	                return str.substring(startsAt, i + 1);
	            } else {
	                opStack.pop();
	            }
	        }else if (str.charAt(i) == '{') {
	            opStack.push(i);
	        }

	        i++;
	    }
	    return null;
	}
	

}


