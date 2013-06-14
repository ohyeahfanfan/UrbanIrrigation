package com.fawn.urbanIrrigationTool.server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.Random;
import java.util.Calendar;

import com.fawn.urbanIrrigationTool.server.Calculation.Hydrology;

public class RefLink {
	public static final String[][] links = {
			{Link.TIME_BASED_SCHEDULER,"Watering Your Florida Lawn","http://edis.ifas.ufl.edu/lh025"}
			,{Link.TIME_BASED_SCHEDULER,"Fertilization and Irrigation Needs for Florida Lawns and Landscapes","http://edis.ifas.ufl.edu/ep110"}
			,{Link.RAIN_SENSOR,"Residential Irrigation System Rainfall Shutoff Devices","http://edis.ifas.ufl.edu/ae221"}
			,{Link.RAIN_SENSOR,"Watering Your Florida Lawn","http://edis.ifas.ufl.edu/lh025"}
			,{Link.RAIN_SENSOR,"Fertilization and Irrigation Needs for Florida Lawns and Landscapes","http://edis.ifas.ufl.edu/ep110"}
			,{Link.SOIL_MOISTURE_SENSOR,"Smart Irrigation Controllers: How do Soil Moisture Sensor (SMS) Irrigation Controller Work?","http://edis.ifas.ufl.edu/ae437"}
			,{Link.SOIL_MOISTURE_SENSOR,"Watering Your Florida Lawn","http://edis.ifas.ufl.edu/lh025"}
			,{Link.SOIL_MOISTURE_SENSOR,"Fertilization and Irrigation Needs for Florida Lawns and Landscapes","http://edis.ifas.ufl.edu/ep110"}
			,{Link.ET_CONTROLLER,"Smart Irrigation Controllers: Operation of Evapotranspiration-Based Controllers","http://edis.ifas.ufl.edu/ae446"}
			,{Link.ET_CONTROLLER,"Smart Irrigation Controllers: Programming for Evapotranspiration-Based Irrigation Controllers","http://edis.ifas.ufl.edu/ae445"}
			,{Link.ET_CONTROLLER,"Watering Your Florida Lawn","http://edis.ifas.ufl.edu/lh025"}
			,{Link.ET_CONTROLLER,"Fertilization and Irrigation Needs for Florida Lawns and Landscapes","http://edis.ifas.ufl.edu/ep110"}
			,{Link.TOO_WET,"Frequency of Residential Irrigation Maintenance Problems","http://edis.ifas.ufl.edu/ae472"}
			,{Link.TOO_DRY,"Let Your Lawn Tell You When To Water","http://edis.ifas.ufl.edu/ep054"}};
	public static final String[] generalLinks = {
		"Do you know about the Florida Yards and Neighborhoods program? Learn more at: <a href='http://fyn.ifas.ufl.edu'>http://fyn.ifas.ufl.edu</a>"
		,"There is a right plant for each place. Do you know which plants are right for your yard? Check with your local County Extension Service or on-line at <a href=' http://ifas.ufl.edu'> http://ifas.ufl.edu</a>"
	
	};
	private Hashtable<String,ArrayList<Link>> linksTable = new Hashtable<String,ArrayList<Link>>(); //key: link type value: links array
	private static final Logger logger = Logger.getLogger(Database.class.getCanonicalName());
	public RefLink(){
		loadLinks();
	}
	/*
	 * Load Ref.links to linksTable. it will be called by the constructor.
	 *
	 */
	private void loadLinks(){
		for(String[] linkArr: RefLink.links){
			Link link = new Link(linkArr[0],linkArr[1],linkArr[2]);
			ArrayList<Link> links = linksTable.get(link.getType());
			if(links==null){
				links = new ArrayList<Link>();
				linksTable.put(link.getType(), links);
			}
			links.add(link);
		}
	}
	
	//Random select s link according to link type
	public Link getLink(String type){
		ArrayList<Link> links = linksTable.get(type);
		if(links==null||links.size()==0){
			logger.info("Can not find any link for " + type);
			return null;
		}
		int length = links.size();
		int index = this.getRandomIndex(length);//length exclude
		return links.get(index);
	}
	
	public String getGeneralLink(){
		int index = this.getRandomIndex(RefLink.generalLinks.length);//length exclude
		return RefLink.generalLinks[index];
	}
	public int getRandomIndex(int max){
		Calendar cal = Calendar.getInstance();
		long seed = cal.getTimeInMillis();
		Random rand = new Random(seed);
	    int index = rand.nextInt(max);
	    return index;
	}
	
	public String getIntroEmailLinks(int irrSysTech){
		String links = null;
		String linkType = this.irrTech2LinkType(irrSysTech);
		Link techLink  = this.getLink(linkType);
		links = (techLink!=null ? techLink.toString() : "");
		return links;
	}
	
	public String irrTech2LinkType(int irrSysTech){
		String type = null;
		if(irrSysTech == Hydrology.TIME_BASED){
			type = Link.TIME_BASED_SCHEDULER;
			
		}else if(irrSysTech == Hydrology.RAIN_SENSOR_BASED){
			type = Link.RAIN_SENSOR;
			
		}else if(irrSysTech == Hydrology.SOIL_WATER_SENSOR_BASED){
			type = Link.SOIL_MOISTURE_SENSOR;
			
		}else if(irrSysTech == Hydrology.ETBASED){
			type = Link.ET_CONTROLLER;
			
		}
		return type;
	}
	public String getWeeklyReportEmailLinks(boolean tooWet, boolean tooDry, int irrTech){
		String links = "";
		if(tooWet){
			Link tooWetLink = this.getLink(Link.TOO_WET);
			links = tooWetLink.toString()+"<br />";
		}
		if(tooDry){
			Link tooWetLink = this.getLink(Link.TOO_DRY);
			links = tooWetLink.toString()+"<br />";
		}
		String linkType = this.irrTech2LinkType(irrTech);
		Link techLink  = this.getLink(linkType);
		links += (techLink != null ? techLink.toString() : "");
		links += "<br /><br />";
		links += "<p>"+this.getGeneralLink()+"</p>";
		return links;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RefLink test = new RefLink();
		String link = test.getIntroEmailLinks( Hydrology.TIME_BASED);
		System.out.println(link);

	}

}

class Link{
	private String displayName;
	private String url;
	private String type;
	public static final String TIME_BASED_SCHEDULER = "TIME_BASED_SCHEDULER";
	public static final String RAIN_SENSOR = "RAIN_SENSOR";
	public static final String SOIL_MOISTURE_SENSOR = "SOIL_MOISTURE_SENSOR";
	public static final String ET_CONTROLLER = "ET_CONTROLLER";
	public static final String GENERAL_LAWN = "GENERAL_LAWN";
	public static final String TOO_WET = "TOO_WET";
	public static final String TOO_DRY = "TOO_DRY";
	
	public Link( String type, String name, String url ){
		this.displayName = name;
		this.url = url;
		this.type = type;
	}
	
	public String toString(){
		return "<a href='"+this.url+"'>"+ this.displayName +"</a>";
				
	}
	public String getDisplayName(){
		return this.displayName;
	}
	
	public String getURL(){
		return this.url;
	}
	
	public String getType(){
		return this.type;
	}
	
}