package com.fawn.urbanIrrigationTool.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fawn.urbanIrrigationTool.server.Calculation.ETController;
import com.fawn.urbanIrrigationTool.server.Calculation.TimeBased;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;
import com.fawn.urbanIrrigationTool.server.Calculation.*;
public class CalculationServlet extends HttpServlet {

	private static final Pattern PATH_INFO_PATTERN = Pattern
			.compile("^/([A-Za-z0-9]+)$");
	private static final Pattern PATH_INFO_PATTERN1 = Pattern
			.compile("^/(.+)/$");
	private static final Pattern PATH_INFO_PATTERN2 = Pattern
			.compile("^/([A-Za-z0-9]+)=([A-Za-z0-9]+)$");

	private static final Logger logger = Logger.getLogger(DataServlet.class
			.getName());

	private Cache cache;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException{

		String cacheKey = "key";
		cache = createCache();
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		String path = request.getPathInfo();
		if (path.equals("/")) {
			out.println("path can not be null. right format: /calculate/useremailaddress/");
			return;
		}
		Matcher match = PATH_INFO_PATTERN1.matcher(path);
		String key = "";
		if (match.matches()) {
			key = match.group(1);
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			Key clientKey = KeyFactory.createKey("UrbanIrrigationTest", key);
			String info = "";
			info = (String) cache.get(KeyFactory.keyToString(clientKey));
			if (info == null) {
				logger.info("Retrieving " + cacheKey + " from cache miss");
				try {
					Entity result = datastore.get(clientKey);
					info = (String) result.getProperty("info");

				} catch (EntityNotFoundException e) {
					out.println("No record for " + key + ". Try another one!");
					return;
				}
			}
			//out.println(info);
			Hashtable<String,String> recordArr = this.processStr2Arr(info);
			DataFeed df = new DataFeed();
			try {
				Hydrology h=  df.init(recordArr);
				String output = h.getCalculationResult();
				response.setContentType("text/csv");
				String fileName = h.getMethod();
		        String disposition = "attachment; fileName="+fileName+".csv";
				response.setHeader("Content-Disposition", disposition);
				out = response.getWriter();
				out.print(output);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.print("");
			}
		} else {
			out.println("invalid format.  /calculate/useremailaddress/");
		}

	}
	public Hashtable<String,String> processStr2Arr(String record){
		Hashtable<String,String> recordArr =  new Hashtable<String,String>();
		String[] firstArr = record.split(":");
		if(firstArr.length==2){
			String paramNamesStr = firstArr[0];
			String paramValsStr = firstArr[1];
			String[] paramNames = paramNamesStr.split(",");
			String[] paramVals = paramValsStr.split(",");
			for(int i = 0; i< paramNames.length; i++){
				recordArr.put(paramNames[i], paramVals[i]);
			}
		}
		return recordArr;
	}
	private Cache createCache() throws ServletException {
		Map<String, Object> props = Collections.emptyMap();
		try {
			return CacheManager.getInstance().getCacheFactory()
					.createCache(props);
		} catch (CacheException ex) {
			throw new ServletException("Could not initialize cache:", ex);
		}
	}
	
	
}