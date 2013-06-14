package com.fawn.urbanIrrigationTool.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.CacheException;

import com.fawn.urbanIrrigationTool.server.Calculation.Hydrology;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.ibm.icu.util.Calendar;

/*
 * This servlet responds to the all the requests of urban irrigation tool. 
 * 
 */
@SuppressWarnings("serial")
public class Controller extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(Controller.class.getCanonicalName());
	 
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		service(request, response);

	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String path = request.getPathInfo();
		if (path.equals("/init")) {//initial index page: redirect to login page or return user's settings 
			response.setContentType("application/json; charset=utf-8");
			response.setHeader("Cache-Control", "no-cache");
			logger.info("Retrieving information about the user from Google UserService.");
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			String url = "";
			if (user == null) {
				//User is not loged in,so redirect to google login page
				logger.info("Redirect User to Google Login Page.");
				url = userService.createLoginURL("/index.html");//after login, redirect to index.html page
				Hashtable<String, String> settings = new Hashtable<String, String>();
				settings.put("redirect", "yes");
				settings.put("url", url);
				String json = Util.writeJSON(settings);
				out.println(json);
			} else {
				//User is logged in, pull out user's information from database. push the user's setting to index.html
				String email = user.getEmail();
				Database db = new Database("Users");
				Hashtable<String, String> settings = db.fetchHash(email, "info");
				if(settings==null){
					settings = new Hashtable<String, String>();
				}
				settings.put("redirect", "no");
				String json = Util.writeJSON(settings);
				out.println(json);
			}
			
		} else if (path.equals("/save")) {
			logger.log(Level.INFO, "Retrieving information about the user from Google UserService.");
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			if(user==null){
				response.sendRedirect("/index.html");
			}
			String email = user.getEmail();
			String dataStr = this.compress2Str(request, response);
			String irrSysTech = request.getParameter("irr_tech");
			Database db = new Database("Users");
			String exists = db.fetch(email, "info");
			logger.log(Level.INFO, "Saving " + email +" new setting to DB.");
			if(exists==null){//first time register, send email confirmation
				try {
					db.replace(email, "info", dataStr);
					logger.log(Level.INFO, "Sending email to " + email);
					logger.log(Level.INFO, Util.requestSignUpComfirmation(email,Integer.parseInt(irrSysTech)));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.log(Level.WARNING, e.getMessage());
				}
			}else{
				db.replace(email, "info", dataStr);
			}
			
			Hashtable<String, String> settings = new Hashtable<String, String>();
			String logoutURL = userService.createLogoutURL("/index.html");
            String userEmail= user.getEmail();
            settings.put("logOutURL", logoutURL);
            settings.put("userEmail", userEmail);
			String json = Util.writeJSON(settings);
			out.println(json);
			
		}else if(path.contains("/calculate")){
			Pattern calPattern = Pattern.compile("^/calculate/(.+)/$");
			Matcher match = calPattern.matcher(path);
			if (!match.matches()) {
				out.println("invalid format.  /calculate/useremailaddress/");
				return;
			}else{
				String email = match.group(1);
				logger.log(Level.INFO, "Retrieving " + email +"'s setting from DB.");
				Database db = new Database("Users");
				Hashtable<String,String> userSettings = db.fetchHash(email, "info");
				if(userSettings==null){
					logger.log(Level.INFO,  email +"doesn't exist");
					out.println("user "+email+" doesn't exist.");
					return;
				}else{
					DataFeed df = new DataFeed();
					try {
						Hydrology h=  df.init(userSettings);
						logger.log(Level.INFO, "Calculating " + email +"'s water use.");
						String output = h.getCalculationResult(df.printedDates);
						db = new Database("Record");
						db.replace(Util.getCurDateTime(), "record", output);
						logger.log(Level.INFO, "Saving " + email +"'s water use result in a csv file.");
						response.setContentType("text/csv");
						String fileName = h.getMethod();
				        String disposition = "attachment; fileName="+fileName+".csv";
						response.setHeader("Content-Disposition", disposition);
						out = response.getWriter();
						out.print(output);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.log(Level.WARNING, e.getMessage());
					}
				}
			}
		}else if(path.contains("/zipcode")){
			Pattern zipPattern = Pattern.compile("^/zipcode/(.+)/$");
			Matcher match = zipPattern.matcher(path);
			response.setContentType("text/plain");
			if (!match.matches()) {
				logger.log(Level.INFO, "Invalid zip format:"+path);
				out.println("invalid format. right format: /zipcode/32602/");
				return;
			}else{
				String zip = match.group(1);
				Location zipInfo = ZipCodes.getLocInfoByZip(zip);
				if(zipInfo==null){
					//Maybe miss valid Florida zip code, so record each unsuccessfully mapped zip code.
					logger.log(Level.WARNING, "Zip " + zip + " is not in the DB.");
					out.print("notin");
				}else{
					if(zipInfo.isMiami()){
						out.print("yes");
					}else{
						out.print("no");
					}
				}
			}
		}else if(path.contains("/send")){
			String email =  request.getParameter("email");
			if(email==null){
				out.print("email is missing");
				return;
			}
			email = email.trim();
			Database db = new Database("Users");
			Hashtable<String,String> userSettings = db.fetchHash(email, "info");
			if(userSettings==null){
				logger.log(Level.INFO, "User "+email+" doesn't exist.");
				out.println("user "+email+" doesn't exist.");
				return;
			}else{
				String active = userSettings.get("active");
				if(active!=null&&active.equals("N")){
					logger.log(Level.INFO, "send:"+email+"is unsubscribed");
					out.println(email+" is unsubscribed");
					return;
				}
				DataFeed df = new DataFeed();
				try {
					Hydrology h=  df.init(userSettings);
					out.println("Calculating " + email +"'s water use.");
					h.getCalculationResult(df.printedDates);
					out.println("Sending email to  " + email);
					out.println(Util.requestWeeklyReport(h, df, email));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.log(Level.WARNING, e.getMessage());
				}
			}
			
		}else if(path.contains("/allsend")){
			Database db = new Database("Users");
			Hashtable<String, Hashtable<String,String>> ht = db.fetchAll();
			if(ht!=null){
				Enumeration<String> enumeration = ht.keys(); 
		        while (enumeration.hasMoreElements ()) { 
		            String email = (String) enumeration.nextElement (); 
		            Hashtable<String,String> userSettings =  ht.get (email); 
		            String active = userSettings.get("active");
					if(active != null && active.equals("N")){
						logger.log(Level.INFO,email+"is unsubscribed");
						continue;
					}
					DataFeed df = new DataFeed();
					try {
						Hydrology h=  df.init(userSettings);
						logger.log(Level.INFO, "Calculating " + email +"'s water use.");
						String output = h.getCalculationResult(df.printedDates);
						db = new Database("Record");
						db.replace(Util.getCurDateTime(), "record", output);
						logger.log(Level.INFO, "Sending email to  " + email);
						String sent = Util.requestWeeklyReport(h, df, email);
						logger.log(Level.INFO,sent);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.log(Level.WARNING, e.getMessage());
					}
		          } 
			}
			
		}else if(path.contains("/unsubscribe")){
			String email =  request.getParameter("email").trim();
			String userToken = request.getParameter("token").trim();
			String timestamp = request.getParameter("timestamp").trim();
			try {
				String token = Util.createToken(timestamp, email);
				if(userToken.equals(token)){
					Database db = new Database("Users");
					String info = db.fetch(email, "info");
					if(info!=null){
						StringBuffer strBuf = new StringBuffer(info);
						strBuf.append(",active=N");
						db.replace(email, "info", strBuf.toString());
						out.println("You have unsubscribed successfully.");
					}else{
						logger.log(Level.INFO, email + ":Sorry. We can not find your record. Please contact webmaster@fawn.ifas.ufl.edu. ");
						out.println("Sorry. We can not find your record. Please contact webmaster@fawn.ifas.ufl.edu. ");
					}
				}else{
					logger.log(Level.INFO,"User token doesn't match: "+email + "," + token + "," + timestamp);
					out.println("Sorry. Please contact webmaster@fawn.ifas.ufl.edu to complete request.");
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.log(Level.WARNING, e.getMessage());
				
			}
			
		}else if(path.contains("/wakeup")){
			
			out.println("I am alive");
			
		}else if(path.contains("/refresh")){
			String email =  request.getParameter("email").trim();
			try {
				out.println("Clearing Cache");
				Database db  = new Database("Users");
				db.cleanUpCacheDB(email);
				
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				out.println(e.getMessage());
			}
		}else if(path.contains("/changesecret")){
			//http://fawnapps.appspot.com/urbanirrigationapp/changesecret?old=xxxxx&new=xxxxx
			String oldSecret = request.getParameter("old");
			String newSecret = request.getParameter("new");
			Database db = new Database("Secret");
			if(oldSecret==null||newSecret==null){
				out.println("missing parameter");
			}else{
				oldSecret = oldSecret.trim();
				newSecret = newSecret.trim();
			}
			try{
				String dbOldSecret = db.fetch("secret", "secret");
				//db.replace("secret", "secret", newSecret.trim());
				if(dbOldSecret.equals(oldSecret)){
					db.replace("secret", "secret", newSecret.trim());
					out.println("true "+db.fetch("secret", "secret"));
				}else{
					out.print("invalid secret!");
				}
				
			}catch(Exception e){
				out.println("update failed");
			}
			
		}
		return;

	
	}
	public String compress2Str(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException{
				Enumeration paramNames = request.getParameterNames();
				String data = "";
				String toString = "";
				    while(paramNames.hasMoreElements()) {
				      String paramName = (String)paramNames.nextElement();
				      String[] paramValues = request.getParameterValues(paramName);
				      toString += paramName+ "=";
				      if (paramValues.length == 1) {
				          String paramValue = paramValues[0];
				          if (paramValue.length() == 0){
				             toString += "-9999"+",";
				          }else{
				        	 toString += paramValue+",";
				           }
				        } else {
				        		
					          for(int i=0; i<paramValues.length; i++) {
					        	 toString += paramValues[i]+";";
					          }
					          toString += ",";
					          
				        }
				    }
				    
				    return toString;
			}

}