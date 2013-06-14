package com.fawn.urbanIrrigationTool.server;
import java.io.IOException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.logging.Logger;

import com.fawn.urbanIrrigationTool.server.Table.CropCoefficientTable;
import com.fawn.urbanIrrigationTool.server.Table.CurveNumberTable;
import com.fawn.urbanIrrigationTool.server.Table.SoilTypeTable;
import com.fawn.urbanIrrigationTool.server.Calculation.*;

public class DataFeed {
private float[] ets = new float[7];
private float[] rainfalls = new float[7];
private float[] kcs = new float[7];
private float[] fields = null;
public Location zipInfo;
public Calendar startDate = Calendar.getInstance();
public Calendar endDate = Calendar.getInstance();
public String[] printedDates = new String[7];
public static final int RAIN_SENSOR = 2;
public static final int SOIL_MOISTURE = 3;
public static final int ENTER_IRR_DEPTH_BY_NUM = 0;
public static final int ENTER_IRR_DEPTH_BY_IRR_SYS_TYPE = 1;
public static final int METRIC = 0;
public static final int US = 1;
public static final int UNITS = 0;
public static final int ROOT_DEPTH = 1;
public static final int SOIL_TYPE  = 2;
public static final int LOT_SIZE = 3;
public static final int IRR_TECH = 4;
public static final int IRR_DEPTH_METHOD = 5;
public static final int IRR_AMOUNT = 6;
public static final int RSS = 7;
public static final int THRESHOLD = 8;
private String mode = "PROD";
private static final Logger logger = Logger.getLogger(Controller.class.getCanonicalName());

public Hydrology init(Hashtable<String, String> record) throws Exception{
	//rain_sensor_setting,soil_type,zip_code,irr_sys_type,lot_size,street_number,irrigation_amount,Irrigation_date,threshold,root_depth
	//required at all cases
	this.fields =new float[10];
	this.setRequiredFloatFields(record);
	this.setConditionalFloatFields(record);
	logger.info("Retrieving location information of zip code "+record.get("zip_code"));
	zipInfo = ZipCodes.getLocInfoByZip(record.get("zip_code"));
	if(zipInfo == null){ 
		logger.info("Can not find zip code." + record.get("zip_code"));
		throw new Exception(record.get("zip_code") + "can not find.");
	}
    this.setMode(record);    //Mode test or product
    this.setStartEndDate();  //Set Start and End Dates
	this.setETRainfall(record);
	this.setKc(); 
	
	float[] et = ets;
	float[] rain = rainfalls;
	float[] kc = kcs;
	float[] irrigation = this.getIrrigationAmounts(this.fields[IRR_AMOUNT],record.get("street_number"),record.get("irr_dates"));
	float[] settings = new float[25];
	Hydrology h = HydrologyFactory.creator((int)this.fields[IRR_TECH]);
	//TimeBased h = new TimeBased();
	int soilType = (int)this.fields[SOIL_TYPE];
	float lotSize = this.fields[LOT_SIZE];
	settings[Hydrology.TH] = this.fields[THRESHOLD]; 
	settings[Hydrology.RSS] = this.fields[RSS]; 
	settings[Hydrology.FC] = this.getFC(soilType); 
	settings[Hydrology.RD] = this.fields[ROOT_DEPTH]; 
	settings[Hydrology.WP] = this.getWP(soilType); 
	settings[Hydrology.CN] = this.getCN(soilType, lotSize);
	h.init(et,rain,irrigation,kc,settings,lotSize);
	return h;
}
public void setRequiredFloatFields(Hashtable<String,String> record){
	String[] fieldsName = {"units","root_depth","soil_type","lot_size","irr_tech","irrigation_depth_method"};
	
	for(int i=0; i < fieldsName.length; i++){
		String name = fieldsName[i];
		String value = record.get(name);
		if(name.equals("irrigation_depth_method")&&value==null){
			value = "2"; //enter by et controller bug
		}
		this.fields[i] = Float.parseFloat(value);
		this.convertField(i);
	}
}

public void setConditionalFloatFields(Hashtable<String,String> record){
	if(this.fields[IRR_DEPTH_METHOD]==DataFeed.ENTER_IRR_DEPTH_BY_NUM){
		String irrAmount = record.get("irrigation_amount");
		this.fields[IRR_AMOUNT]= Float.parseFloat(irrAmount);
		this.convertField(IRR_AMOUNT);
	}else if(this.fields[IRR_DEPTH_METHOD]==DataFeed.ENTER_IRR_DEPTH_BY_IRR_SYS_TYPE){
		String irrMinutes = record.get("irrigation_minutes");
		String irrSysType = record.get("irrigation_system");
		this.fields[IRR_AMOUNT] = this.calculateIrrAmount(irrMinutes, irrSysType); //cm
	}else{
		this.fields[IRR_AMOUNT] = 1;
	}
	
	if(this.fields[IRR_TECH]== RAIN_SENSOR){
		String rss = record.get("rain_sensor_setting");
		this.fields[RSS] = Float.parseFloat(rss);
		this.convertField(RSS);
		
	}else if(this.fields[IRR_TECH]== SOIL_MOISTURE){
		String threshold = record.get("threshold");
		this.fields[THRESHOLD] = Float.parseFloat(threshold);
		this.convertField(THRESHOLD);
	}
}

// cm
public float calculateIrrAmount(String minutes, String irrSysType){
	int mins = Integer.parseInt(minutes);
	int typeID = Integer.parseInt(irrSysType);
	float[] amountPerHr = {0.25f,1.5f,0.5f,0.5f};
	float irrAmount = mins * amountPerHr[typeID]/60*2.54f;
	return irrAmount;
	
}

public void setKc(){
	String zone = this.getZone();
	CropCoefficientTable Coeff= new CropCoefficientTable();
	for(int i = 0; i < kcs.length;i++){
		int month=0;
		if(this.mode.equals("TEST")){
			Calendar startCal = (Calendar)startDate.clone();
			startCal.add(Calendar.DAY_OF_MONTH, i);
			month = startCal.get(Calendar.MONTH);
		}else{
			Calendar startCal = (Calendar)startDate.clone();
			startCal.add(Calendar.DAY_OF_MONTH, i);
			month =startCal.get(Calendar.MONTH);
		}
		kcs[i] = Coeff.get(month, zone);
	}
}

public void setETRainfall(Hashtable<String,String> record)throws Exception{

	String etsUserInput = record.get("et");
	String rainsUserInput = record.get("rainfall");
	if(this.mode.equals("TEST")){
		//1. TEST MODE: Get from user input, cm only
		String[] etsArr = etsUserInput.split(";");
		String[] rainsArr = rainsUserInput.split(";");
		if(etsArr.length!=7||rainsArr.length!=7){
			throw new Exception("ET and Rainfall must contain 7 numbers.");
		}else{
			for(int i=0; i<7; i++){
				ets[i] = Float.parseFloat(etsArr[i]);
				rainfalls[i] = Float.parseFloat(rainsArr[i]);
			}
		}
		//manually input et and rainfall
		
	}else{
		//2.REAL MODE: collect from FAWN
		this.setETsRainFallsFromDB();
	}
}


public float getFC(int soilTypeIDInt){
	String soilTypeName =  SoilTypeTable.xNames[soilTypeIDInt];
	float fc = SoilTypeTable.get(soilTypeName,"FC");
	return fc;
}

public float getWP(int soilTypeIDInt){
	String soilTypeName =  SoilTypeTable.xNames[soilTypeIDInt];
	float wp = SoilTypeTable.get(soilTypeName,"WP");
	return wp;
}

public int getCN(int soilTypeIDInt, float lotSize){
	String soilTypeName = SoilTypeTable.xNames[soilTypeIDInt];
	float soilGroup = SoilTypeTable.get(soilTypeName,"soil group");
	CurveNumberTable cnt = new CurveNumberTable();
	int cn = cnt.get(lotSize, soilGroup);
	return cn;
}

public float[] getIrrigationAmounts(float irrAmount, String streetNum, String irrigationDates) throws IOException{
	float[] irrAmounts = new float[]{0f,0f,0f,0f,0f,0f,0f};
	
	if(this.zipInfo.isMiami()){
		int streetNumInt = Integer.parseInt(streetNum);
		if(streetNumInt%2==0){
			irrAmounts[Calendar.SUNDAY-1] = irrAmount;
			irrAmounts[Calendar.THURSDAY-1] = irrAmount;
		}else{
			irrAmounts[Calendar.WEDNESDAY-1] = irrAmount;
			irrAmounts[Calendar.SATURDAY-1] = irrAmount;
		}

	}else{
		String[] dates = irrigationDates.split(";");
		for(String date:dates){
			int day = Integer.parseInt(date);
			irrAmounts[day] = irrAmount;
		}
	}
	return irrAmounts;
}
public void setMode(Hashtable<String,String> record){
	/*
	 * 7 days et and rainfall
	 * 1. TEST MODE: Get from user input
	 * or
	 * 2. REAL MODE: collect from FAWN
	 */
	String etsUserInput = record.get("et");
	String rainsUserInput = record.get("rainfall");
	if(etsUserInput==null||rainsUserInput==null){
		this.mode = "PROD";
	}else{
		if(!etsUserInput.equals("-9999")&&!rainsUserInput.equals("-9999")){
			this.mode = "TEST";
		}else{
			this.mode = "PROD";
		}
	}
}
public void setStartEndDate(){
	if(this.mode.equals("TEST")){
		//If it is TEST mode, start date and end date are today.
		this.startDate = Calendar.getInstance(Util.timeZoneUsed);
		this.endDate = this.startDate;
		for(int i = 0; i < this.printedDates.length;i++){
			this.printedDates[i] = Util.formatDate(this.startDate.getTime());
		}
		
	}else{
		this.startDate = Calendar.getInstance(Util.timeZoneUsed);
		int today = startDate.get(Calendar.DAY_OF_WEEK);
		int amount = -7-today+1;
		startDate.add(Calendar.DAY_OF_MONTH, amount);
		this.endDate = Calendar.getInstance(Util.timeZoneUsed);
		amount = -today;
		this.endDate.add(Calendar.DAY_OF_MONTH, amount);
		Calendar calendar = (Calendar)this.startDate.clone();
		for(int i = 0; i < this.printedDates.length;i++){
			this.printedDates[i] = Util.formatDate(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

	}
}



public void setETsRainFallsFromDB() throws Exception  {//include from and to date
	String stnID = zipInfo.getFAWNStn();
	Hashtable<String,String> etRain = Util.requestETRainfall(this.startDate, this.endDate, stnID);
	String[] ets = etRain.get("ets").split(",");
	String[] rains = etRain.get("rains").split(",");
	for(int i = 0; i < ets.length; i++){
		this.ets[i] = this.in2cm(Float.parseFloat(ets[i]));
		this.rainfalls[i] = this.in2cm(Float.parseFloat(rains[i]));
	}
	
}

public void convertField(int index){//convert from inch to cm, m2 to acre
	int[] convertName = {ROOT_DEPTH,LOT_SIZE,IRR_AMOUNT,RSS};
	int[] hit = {0,0,0,0,0,0,0,0,0,0};
	for(int value: convertName){
		hit[value] = 1;
	}
	if(hit[index] == 0){
		//parameter is not in the list, do not need to convert
		return;
	}
	if(index == IRR_AMOUNT && this.fields[IRR_DEPTH_METHOD] == ENTER_IRR_DEPTH_BY_IRR_SYS_TYPE){
		//irrigation amount is get from calculation. cm is used by default.
		return;
	}
	if(this.fields[UNITS]==US){
		if(index != LOT_SIZE)
		this.fields[index] =  this.in2cm(this.fields[index]);
	}else{
		if(index== LOT_SIZE){
			this.fields[index] =   this.fields[index]/4046.86f;
		}else{
			this.fields[index] =  this.fields[index];
		}
	}
}
public float in2cm(float in){
	float cm = in * 2.54f;
	return cm;
}
public void print(){
	for(float et: ets){
		System.out.println(et);
	}
	for(float rainfall: rainfalls){
		System.out.println(rainfall);
	}
}

public String getZone(){
	return this.zipInfo.getZone();
}
public static void main(String[] args) throws IOException {
	DataFeed h = new DataFeed();

	
}
}

