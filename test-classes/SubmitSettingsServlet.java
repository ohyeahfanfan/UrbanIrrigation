// Copyright 2009 Google Inc.
package com.fawn.urbanIrrigationTool.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gargoylesoftware.htmlunit.Cache;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Test Use. Mock up controller.php in FAWN
 */
public class SubmitSettingsServlet extends HttpServlet {
	// private static final Logger logger =
	// Logger.getLogger(FawnImgServlet.class.getName());

	/**
	 * Expect requests of the form {@code level/x_y.ext}, where {@code level},
	 * {@code x}, and {@code y} are integers and {@code ext} is a file
	 * extension.
	 */
	// private static final Pattern PATH_INFO_PATTERN =
	// Pattern.compile("^/([A-Za-z]+)(\\d+)\\..*$");
	private static final Pattern PATH_INFO_PATTERN = Pattern
			.compile("^/([A-Za-z]+)$");

	@Override
	public void init() throws ServletException {

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		//this.printOutPost(request, response);

		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        PrintWriter out = response.getWriter();
        
		if (user==null){
        	response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
        	return;
        }else{
        	String logout = "<a href = \""+ userService.createLogoutURL(request.getRequestURI()) + "\" >Log out</a><br />";
            logout += "<a href = \"/urbanirrigationapp/calculate/"+ user.getEmail() + "/\" >View 7 days Calculation</a><br />";
            logout += "<a href = \"/urbanirrigationapp/control/\" >Revise Settings</a><br />";
        	out.println(logout);
            
        }
//		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/form_test.jsp");
//		view.forward(request, response);
		if(request.getParameter("submit_form")!=null){
			String dataStr = this.compress2Str(request, response);
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
	       
			Key clientKey = KeyFactory.createKey("UrbanIrrigationTest", user.getEmail());
			Entity greeting = new Entity(clientKey);
			//greeting.setProperty("userID", user.getEmail());
			greeting.setProperty("info", dataStr);
			datastore.put(greeting);
			out.println("Data has been submitted successfully.");
		}else{
			
			response.sendRedirect("/form_test.jsp");
		}
		
	}
	public String compress2Str(HttpServletRequest request,
	HttpServletResponse response) throws IOException, ServletException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Enumeration paramNames = request.getParameterNames();
		String data = "";
		String paramNamesStr = "";
		String paramValsStr = "";
		    while(paramNames.hasMoreElements()) {
		      String paramName = (String)paramNames.nextElement();
		      String[] paramValues = request.getParameterValues(paramName);
		      if(paramName.equals("submit_form")){
		    	  continue;
		      }
		      paramNamesStr += paramName + ",";
		      if (paramValues.length == 1) {
		          String paramValue = paramValues[0];
		          if (paramValue.length() == 0){
		              paramValsStr += "-9999"+",";
		          }else{
		        	  paramValsStr += paramValue+",";
		           }
		        } else {
		        		
			          for(int i=0; i<paramValues.length; i++) {
			        	  paramValsStr += paramValues[i]+";";
			          }
			          paramValsStr += ",";
		        }
		    }
		    data = paramNamesStr+":"+paramValsStr;
		    return data;
	}
	public void printOutPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Enumeration paramNames = request.getParameterNames();
		    while(paramNames.hasMoreElements()) {
		      String paramName = (String)paramNames.nextElement();
		      String[] paramValues = request.getParameterValues(paramName);
		      if (paramValues.length == 1) {
		          String paramValue = paramValues[0];
		          if (paramValue.length() == 0)
		            out.println(paramName +"=>"+"NA"+"<br />");
		          else
		            out.println(paramName +"=>"+paramValue+"<br />");
		        } else {
		        	out.print(paramName +"=>");
			          for(int i=0; i<paramValues.length; i++) {
			            out.print(paramValues[i]+",");
			          }
			         out.println("<br />");
		        }
		     
		        
		    }
		
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		this.doPost(request, response);
	
	}
}
