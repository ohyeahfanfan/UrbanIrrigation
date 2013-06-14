package com.fawn.urbanIrrigationTool.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.fawn.urbanIrrigationTool.server.Calculation.Hydrology;
import java.util.Calendar;

public class Util {
	//private static String secret = "123456789";
	private static String mailServerURL = "http://fawn.ifas.ufl.edu/mail/send.php";
	public static String dataServerURL = "http://fawn.ifas.ufl.edu/data/reports/?res";
	public static TimeZone timeZoneUsed = TimeZone
			.getTimeZone("America/New_York");
	public static String END_OF_LINE = "END__OF__LINE";
	public static int EMAIL_INTRO = 0;
	public static int EMAIL_WEEKLY_REPORT = 1;
	private static final Logger logger = Logger.getLogger(Controller.class
			.getCanonicalName());

	public static String writeJSON(Hashtable<String, String> ht) {
		if (ht == null) {
			return "{\"data\":{}}";
		}
		StringBuilder sb = new StringBuilder();
		Enumeration<String> keys = ht.keys();
		sb.append("{\"data\": ");
		sb.append("{");
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			sb.append("\"" + key + "\" : \"" + ht.get(key) + "\",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));

		sb.append("}");
		sb.append("}");
		return sb.toString();
	}

	public static String createToken(String timestamp, String type)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, ServletException {
		Database db = new Database("Secret");
		String secret = db.fetch("secret", "secret");
		String originalToken = "{" + secret + "}-{" + timestamp + "}-{" + type
				+ "}";
		MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
		digest.update(originalToken.getBytes("UTF8"));
		byte[] hash = digest.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xFF & hash[i]);
			if (hex.length() == 1) {
				// could use a for loop, but we're only dealing with a single
				// byte
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	
	public static String formatDate(Date date) {

		DateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
		formatter.setTimeZone(Util.timeZoneUsed);
		return formatter.format(date);
	}

	public static String getCurDateTime() {
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("MMM dd yyyy HH:mm:ss SSS");
		formatter.setTimeZone(Util.timeZoneUsed);
		return formatter.format(date.getTime());
	}

	public static String postRequest2ExternalServer(String serverURL,
			String postParas) throws IOException {
		URL url = new URL(serverURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(postParas.getBytes().length));
		connection.setUseCaches(false);
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(postParas);
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String decodedString;
		String response = "";
		while ((decodedString = in.readLine()) != null) {
			response += decodedString + Util.END_OF_LINE;
		}
		in.close();
		connection.disconnect();
		if (response.length() == 0) {
			// fail
			logger.log(Level.WARNING, "Fail to get response from request "
					+ serverURL);
			return null;
		}
		return response;
	}

	public static String requestWeeklyReport(Hydrology h, DataFeed df,
			String email) throws Exception {
		String urlParameters = buildWeeklyReportParameters(h, df, email);
		return postRequest2ExternalServer(mailServerURL, urlParameters);

	}

	public static String requestSignUpComfirmation(String email, int irrSysTech)
			throws NoSuchAlgorithmException, IOException, ServletException {
		String urlParameters = buildRegisterConfirmParameters(email, irrSysTech);
		return postRequest2ExternalServer(mailServerURL, urlParameters);
	}

	/*
	 * "FAWN Station","Period","2m Rain tot (in)","2m Rain max over 15min(in)",
	 * "N (# obs)","ET (in)" "Jay","26 Jun 2011","0.04","0.04","96","0.19"
	 * "Jay","27 Jun 2011","0","0","96","0.18"
	 * "Jay","28 Jun 2011","0.46","0.22","96","0.2"
	 * "Jay","29 Jun 2011","0","0","96","0.2"
	 * "Jay","30 Jun 2011","0","0","96","0.22"
	 * "Jay","1 Jul 2011","0","0","96","0.23"
	 * "Jay","2 Jul 2011","0.5","0.21","96","0.17"
	 * "Jay","3 Jul 2011","0","0","96","0.2"
	 * "Jay","4 Jul 2011","0","0","96","0.19"
	 * "Jay","5 Jul 2011","0.35","0.18","96","0.2"
	 * "Jay","6 Jul 2011","0","0","96","0.21"
	 * "Jay","7 Jul 2011","0.04","0.02","96","0.19" Sun to Sat (1-7) if
	 * monday/../Friday then calculate from one week before the passing sunday
	 * to passing saturday. if Sunday then from last sunday to last saturday.
	 * Get from FAWN Report Generator
	 */
	public static Hashtable<String, String> requestETRainfall(
			Calendar startDate, Calendar endDate, String stnID)
			throws Exception {
		String urlParameter = Util.buildRainETRequestURL(startDate, endDate,
				stnID);
		String response = Util.postRequest2ExternalServer(Util.dataServerURL,
				urlParameter);
		if (response == null) {
			response = Util.postRequest2ExternalServer(Util.dataServerURL,
					urlParameter);
			if (response == null)
				throw new Exception("ET & Rainfall Request Failed Twice");
		}
		if (response.contains("N/A")) {
			logger.log(Level.SEVERE, "[RequestETRain NA]:" + response);
		}
		String[] lines = response.split(Util.END_OF_LINE);
		Hashtable<String, String> etRain = new Hashtable<String, String>();
		String ets = "";
		String rains = "";
		for (String inputLine : lines) {
			String[] inputs = inputLine.split(",");
			if (inputs.length > 0 && inputs[0].equals("\"FAWN Station\"")) {
				continue;
			}
			for (int i = 0; i < 6; i++) {
				inputs[i] = inputs[i].replace("\"", "");
			}
			String et = (inputs[5].equals("N/A") ? "0" : inputs[5]);
			String rain = (inputs[2].equals("N/A") ? "0" : inputs[2]);
			ets += et + ",";
			rains += rain + ",";
		}
		ets = ets.substring(0, ets.length() - 1);
		rains = rains.substring(0, rains.length() - 1);
		etRain.put("ets", ets);
		etRain.put("rains", rains);
		return etRain;
	}

	// weekly Report
	public static String buildWeeklyReportParameters(Hydrology h, DataFeed df,
			String email) throws Exception {

		String timestamp = Long.toString(System.nanoTime());
		String app = "UrbanIrrigation";
		String email_token = createToken(timestamp, app);
		String unsubscribe_token = createToken(timestamp, email);
		float waterNotUsed = h.finalResult[Hydrology.WATER_NOT_USED];
		float waterStressDay = h.finalResult[Hydrology.WATER_STRESS];
		boolean tooWet = (waterNotUsed > 0 ? true : false);
		boolean tooDry = (waterStressDay > 0 ? true : false);
		RefLink ref = new RefLink();
		String links = ref.getWeeklyReportEmailLinks(tooWet, tooDry,
				h.getIrrTechID());
		String urlParameters = "dates=" + formatDate(df.startDate.getTime())
				+ " to " + formatDate(df.endDate.getTime())
				+ "&percentage_water_not_used=" + waterNotUsed
				+ "&gallon_water_not_used="
				+ h.finalResult[Hydrology.WATER_NOT_USED_GAL]
				+ "&water_stress_day=" + waterStressDay + "&fawn_station_name="
				+ df.zipInfo.getFAWNStnName() + "&miles_to_fawn_station="
				+ df.zipInfo.distance + "&ranking="
				+ h.finalResult[Hydrology.RANKING] + "&to=" + email
				+ "&subject=Urban Irrigation Weekly Report" + "&template_name="
				+ app + "&email_token=" + email_token + "&unsubscribe_token="
				+ unsubscribe_token + "&timestamp=" + timestamp + "&app=" + app
				+ "&links=" + links;
		return urlParameters;
	}

	public static String buildRegisterConfirmParameters(String email,
			int irrSysTech) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, ServletException {
		RefLink ref = new RefLink();
		String link = ref.getIntroEmailLinks(irrSysTech);
		String timestamp = Long.toString(System.nanoTime());
		String app = "UrbanIrrigation";
		String email_token = createToken(timestamp, app);
		String unsubscribe_token = createToken(timestamp, email);
		String urlParameters = "to=" + email
				+ "&subject=Urban Irrigation Registration Confirmation"
				+ "&template_name=UrbanIrrigationSignUpConfirmation"
				+ "&email_token=" + email_token + "&unsubscribe_token="
				+ unsubscribe_token + "&timestamp=" + timestamp + "&app=" + app
				+ "&links=" + link;

		return urlParameters;
	}

	public static String buildRainETRequestURL(Calendar fromDate,
			Calendar toDate, String stnID) {
		int fromMonth = fromDate.get(Calendar.MONTH) + 1;
		int fromDay = fromDate.get(Calendar.DAY_OF_MONTH);
		int fromYear = fromDate.get(Calendar.YEAR);
		int toMonth = toDate.get(Calendar.MONTH) + 1;
		int toDay = toDate.get(Calendar.DAY_OF_MONTH);
		int toYear = toDate.get(Calendar.YEAR);

		String urlParameters = "locs__" + stnID.trim() + "=on" + "&fromDate_m="
				+ fromMonth + "&fromDate_d=" + fromDay + "&fromDate_y="
				+ fromYear + "&toDate_m=" + toMonth + "&toDate_d=" + toDay
				+ "&toDate_y=" + toYear + "&reportType=daily&presetRange=dates"
				+ "&vars__Rainfall=on&vars__ET=on&" + "format=.CSV+%28Excel%29";
		return urlParameters;
	}

	public static void main(String[] args) throws ServletException,
			IOException, NoSuchAlgorithmException {
		// Util.requestSignUpComfirmation("fanjie@ufl.edu");
		/*
		 * Calendar start = Calendar.getInstance();
		 * start.add(Calendar.DAY_OF_MONTH, -1); Calendar end =
		 * (Calendar)start.clone(); end.add(Calendar.DAY_OF_MONTH, -6);
		 * Hashtable<String, String> etRain; try { etRain =
		 * Util.requestETRainfall(end, start, "110");
		 * System.out.println(etRain.get("ets"));
		 * System.out.println(etRain.get("rains")); } catch (Exception e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
		Util.requestSignUpComfirmation("fanjie@ufl.edu", Hydrology.TIME_BASED);

		// String secret = "123456789";
		// String timestamp = Long.toString(System.nanoTime());
		// String app = "UrbanIrrigation";
		// String token = Email.createToken(secret, timestamp, app);
		// Email.sendEmailRequest(token, timestamp, app);

	}

}
