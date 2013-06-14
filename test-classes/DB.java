package com.fawn.urbanIrrigationTool.server;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.mortbay.util.ajax.JSON;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class DB {
	private Cache cache;
	private String dbName;
	
	public String fetch(String key) throws ServletException {
		String info = "";
		cache = createCache();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Key clientKey = KeyFactory.createKey(this.dbName, key);
		info = (String) cache.get(KeyFactory.keyToString(clientKey));
		if (info == null) {
			try{
			Entity result = datastore.get(clientKey);
			info = (String) result.getProperty("info");
			}catch(Exception e){
				info = null;
			}

		}
		return info;
	}
	public Hashtable<String,String> fetchHash(String key)throws 
	ServletException{
		String result = this.fetch(key);
		if(result==null){
			return null;
		}
		Hashtable<String,String> recordArr = this.processStr2Arr(result);
		return recordArr;
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
	
	public void init(String kind){
		this.dbName = kind;
	}
	
	public static void main(String[] args) throws ServletException {
		DB db = new DB();
		String dbname = "UrbanIrrigationTest";
		db.init(dbname);
		Hashtable<String,String> settings = db.fetchHash("fanjie@ufl.edu");
	}

}
