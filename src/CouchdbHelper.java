import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class CouchdbHelper {
	
	static String host="http://reyalpsirc.iriscouch.com/";
	static String database="template";

	public static String Fetch(String database_url){
		 return CouchdbHelper.excuteGet(host+database+"/"+database_url);
	}
	
	private static String excutePost(String targetURL, String urlParameters) {
		 HttpURLConnection connection = null;  
		 try {
			 //Create connection
			 URL url = new URL(targetURL);
			 connection = (HttpURLConnection)url.openConnection();
			 connection.setRequestMethod("POST");
			 connection.setRequestProperty("Content-Type", "application/json");
			 connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			 connection.setRequestProperty("Accept", "*/*");  
			 connection.setUseCaches(false);
			 connection.setDoOutput(true);

			 //Send request
			 DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			 wr.writeBytes(urlParameters);
			 wr.close();

			 //Get Response  
			 InputStream is = connection.getInputStream();
			 BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			 StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
			 String line;
			 while((line = rd.readLine()) != null) {
				 response.append(line);
				 response.append('\r');
			 }
			 rd.close();
			 return response.toString();
		 } catch (Exception e) {
			  e.printStackTrace();
			  return null;
		 } finally {
			  if(connection != null) {
				  connection.disconnect(); 
			  }
		 }
	}
	
	private static String excuteGet(String targetURL) {
		 HttpURLConnection connection = null;  
		 try {
			 //Create connection
			 System.out.println(targetURL);
			 URL url = new URL(targetURL);
			 connection = (HttpURLConnection)url.openConnection();
			 connection.setRequestMethod("POST");
			 connection.setRequestProperty("Content-Type", "application/json");
			 connection.setRequestProperty("Accept", "*/*");  
			 connection.setUseCaches(false);
			 connection.setDoOutput(true);

			 //Get Response  
			 InputStream is = connection.getInputStream();
			 BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			 StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
			 String line;
			 while((line = rd.readLine()) != null) {
				 response.append(line);
				 response.append('\r');
			 }
			 rd.close();
			 return response.toString();
		 } catch (Exception e) {
			  e.printStackTrace();
			  return null;
		 } finally {
			  if(connection != null) {
				  connection.disconnect(); 
			  }
		 }
	}
}
