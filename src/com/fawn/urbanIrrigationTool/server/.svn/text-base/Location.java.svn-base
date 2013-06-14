package com.fawn.urbanIrrigationTool.server;

import java.util.Comparator;

class Location implements Comparator<Location>{
	private String city;
	private String zip;
	private float lng;
	private float lat;
	private String zone;
	private String fawnStnID;
	private String fawnStnName;
	public float distance = -1f;

	public void initFawnLoc(String stnID, String stnName, float lat, float lng, float distance) {
		this.lat = lat;
		this.lng = lng;
		this.fawnStnID = stnID;
		this.fawnStnName = stnName;
		this.distance = distance;
		this.setZone();
	}

	public void initFLZip(String inputStr) {
		String[] arr = inputStr.split(",");
		this.zip = arr[0].trim();
		this.lat = Float.parseFloat(arr[1].trim());
		this.lng = Float.parseFloat(arr[2].trim());
		this.city = arr[3].trim();
		this.setZone();
	}
	public boolean isMiami(){
		int zip = Integer.parseInt(this.zip);
		if(zip >= 33012 && zip <= 33016){//Hialeah
			return true;
		}else if(zip >= 33030 && zip <= 33035){//Homestead
			return true;
		}else if(zip >= 33054 && zip <=  33056){//Opa Locka
			return true;
		}else if(zip == 33010||zip == 33018
				||zip == 33109||zip == 33122
				||zip == 33149||zip == 33150
				||zip == 33160|| zip ==33161||zip==33162
				||zip == 33193|| zip ==33194||zip==33196||zip==34141 ){// Miami Beach Miami
			return true;
		}else if(zip >= 33125 && zip <= 33147){
			return true;
		}else if(zip >= 33155 && zip <= 33158){
			return true;
		}else if(zip >= 33165 && zip <= 33190){
			return true;
		}else{
			return false;
		}
	}
	public int compareTo(Location other){
		if(this.distance == other.distance){
			return 0;
		}else if(this.distance > other.distance){
			return 1;
		}else{
			return -1;
		}
	}
	public String getCity() {
		return city;
	}

	public String getZip() {
		return zip;
	}

	public float getLng() {
		return lng;
	}

	public float getLat() {
		return lat;
	}

	public String getZone() {
		return zone;
	}

	public String getFAWNStn() {
		return this.fawnStnID;
	}
	public String getFAWNStnName() {
		return this.fawnStnName;
	}
	public void setZone() {
		float north2Central = 29.1f;
		float central2South = 27.41f;
		float lat = this.getLat();
		String zone = null;
		if(lat > north2Central){
			zone = "north";
		}
		if(lat > central2South && lat < north2Central){
			zone = "central";
		}
		if(lat < north2Central){
			zone ="south";
		}
		this.zone = zone;
		
	}
	public void setFAWNStnName(String name){
		this.fawnStnName = name;
	}
	public void setFAWNStn(String stnid) {
		this.fawnStnID = stnid;
	}

	public String print() {
		String obj = String.format("zip:%s,city:%s,lat:%f,lng:%f",
				this.getZip(), this.getCity(), this.getLat(), this.getLng());
		if (zone != null) {
			obj += String.format(",zone:%s",
					this.zone);
		}else if(fawnStnID != null){
			obj += String.format(",nearby FAWN Station ID:%s;",
					this.fawnStnID);
		}

		return obj;
	}
	
	public String printFawn(){
		String obj = String.format("fawn id:%s,lat:%f,lng:%f",
				this.getFAWNStn(),  this.getLat(), this.getLng());
		if (distance != -1f) {
			obj += String.format(",distance:%f;",
					this.distance);
		}
		return obj;
	}
	public int compare(Location me, Location other) {
		
		if(me.distance == other.distance){
			return 0;
		}else if(me.distance > other.distance){
			return 1;
		}else{
			return -1;
		}
	
	}
}
