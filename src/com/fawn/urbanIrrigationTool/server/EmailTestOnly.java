package com.fawn.urbanIrrigationTool.server;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * This class is for test only. 
 * It is not used in the final version.
 */
public class EmailTestOnly {
	private static String secret = "123456789";
	
public static String createToken(String secret, String timestamp, String app) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    String originalToken = "{" + secret + "}-{" + timestamp + "}-{" + app + "}";
    MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
    digest.update(originalToken.getBytes("UTF8"));
    byte[] hash = digest.digest();
    //String token = new String(Hex.encodeHex(hash));
    StringBuffer hexString = new StringBuffer();
    for (int i=0;i<hash.length;i++) {
        String hex = Integer.toHexString(0xFF & hash[i]);
        if (hex.length() == 1) {
            // could use a for loop, but we're only dealing with a single byte
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString();
}
public static String createToken(String timestamp, String type) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    String originalToken = "{" + secret + "}-{" + timestamp + "}-{" + type + "}";
    MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
    digest.update(originalToken.getBytes("UTF8"));
    byte[] hash = digest.digest();
    StringBuffer hexString = new StringBuffer();
    for (int i=0;i<hash.length;i++) {
        String hex = Integer.toHexString(0xFF & hash[i]);
        if (hex.length() == 1) {
            // could use a for loop, but we're only dealing with a single byte
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString();
}
public static void sendConfirmation(String email) throws IOException, NoSuchAlgorithmException{
	String timestamp = Long.toString(System.nanoTime());
	String app = "UrbanIrrigation";
	String email_token = createToken(timestamp, app);
	String unsubscribe_token = createToken(timestamp,email);
	String urlParameters = "to=" + email+
			"&subject=Urban Irrigation Registration Confirmation" +
			"&template_name=UrbanIrrigationSignUpConfirmation" +
			"&email_token="+ email_token + 
			"&unsubscribe_token=" + unsubscribe_token + 
			"&timestamp=" + timestamp +
			"&app="+ app;
	String request = "http://localhost/mail/send.php";
	URL url = new URL(request);
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setDoOutput(true);
	connection.setDoInput(true);
	connection.setInstanceFollowRedirects(false);
	connection.setRequestMethod("POST");
	connection.setRequestProperty("Content-Type",
			"application/x-www-form-urlencoded");
	connection.setRequestProperty("charset", "utf-8");
	connection.setRequestProperty("Content-Length",
			"" + Integer.toString(urlParameters.getBytes().length));
	connection.setUseCaches(false);
	DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	wr.writeBytes(urlParameters);
	wr.flush();
	wr.close();
	BufferedReader in = new BufferedReader(new InputStreamReader(
			connection.getInputStream()));

	String decodedString;

	while ((decodedString = in.readLine()) != null) {
		System.out.println(decodedString);
	}
	in.close();
	connection.disconnect();
}
public static void sendEmailRequest(String token, String timestamp, String app) throws IOException{
	String urlParameters = 
			"dates=August 20 to August 26 2011" +
			"&percentage_water_not_used=16" +
			"&gallon_water_not_used=400" +
			"&fawn_station_name=Homestead" +
			"&miles_to_fawn_station=8" +
			"&ranking=0" +
			"&to=fanjie@ufl.edu" +
			"&subject=Urban Irrigation Weekly Report" +
			"&template_name=UrbanIrrigation" +
			"&token="+ token + 
			"&timestamp=" + timestamp +
			"&app="+ app;
	String request = "http://fawn.ifas.ufl.edu/mail/send.php";
	URL url = new URL(request);
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setDoOutput(true);
	connection.setDoInput(true);
	connection.setInstanceFollowRedirects(false);
	connection.setRequestMethod("POST");
	connection.setRequestProperty("Content-Type",
			"application/x-www-form-urlencoded");
	connection.setRequestProperty("charset", "utf-8");
	connection.setRequestProperty("Content-Length",
			"" + Integer.toString(urlParameters.getBytes().length));
	connection.setUseCaches(false);
	DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	wr.writeBytes(urlParameters);
	wr.flush();
	wr.close();
	BufferedReader in = new BufferedReader(new InputStreamReader(
			connection.getInputStream()));

	String decodedString;

	while ((decodedString = in.readLine()) != null) {
		System.out.println(decodedString);
	}
	in.close();
	connection.disconnect();
}
public static void main(String[] args) throws ServletException, IOException, NoSuchAlgorithmException {
	EmailTestOnly.sendEmailRequest("fanjie@ufl.edu", "Aa12345678", "Aa12345678");
	/*
	    String secret = "123456789";
	    String timestamp = Long.toString(System.nanoTime());
	    String app = "UrbanIrrigation";
	    String token = Email.createToken(secret, timestamp, app);
		Email.sendEmailRequest(token, timestamp, app);*/
		
	}

}
 