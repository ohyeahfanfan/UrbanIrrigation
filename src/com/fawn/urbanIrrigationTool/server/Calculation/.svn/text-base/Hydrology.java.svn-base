package com.fawn.urbanIrrigationTool.server.Calculation;

import com.fawn.urbanIrrigationTool.server.Table.Rank;

public abstract class Hydrology {
	public static final int RSS = 0; //user input
	public static final int TH = 1; //user input
	public static final int ETCpre = 2; //default = 0
	public static final int R = 3; //fawn input
	public static final int I = 4; //User Input or Based on user input to calculate
	public static final int ET = 5;//fawn input
	public static final int Kc = 6;//Calculated by Zip Code
	public static final int FC = 7;//By user input soil type
	public static final int RD = 8; // By user input
	public static final int WP = 9; //By user input soil type
	public static final int CN = 10; //By user input lot size
	public static final int SWCcur = 11; //input & output
	public static final int WB = 12;
	public static final int ETa = 13;
	public static final int SWCm = 14;
	public static final int SWCf = 15;
	public static final int Smx = 16;
	public static final int S = 17;
	public static final int Q = 18;
	public static final int F = 19;
	public static final int PERC = 20;
	public static final int SWCnext = 21;
	public static final int ETCcur = 22;
	public static final int TIME_BASED = 1;
	public static final int RAIN_SENSOR_BASED = 2;
	public static final int SOIL_WATER_SENSOR_BASED = 3;
	public static final int ETBASED = 4;
	public static final int PERC_SUM =0;
	public static final int Q_SUM = 1;
	public static final int I_SUM=2;
	public static final int R_SUM = 3;
	public static final int WATER_NOT_USED =4;
	public static final int WATER_STRESS = 5;
	public static final int WATER_NOT_USED_GAL=6;
	public static final int RANKING = 7;
	
	public float[][] result;
	public float[] et;
	public float[] rain;
	public float[] irrigation;
	public float[] Kcs;
	public float[] settings;
	public float[] finalResult;
	protected String method;
	protected float lotSize;
	
	
	public String getMethod(){
		return this.method;
	}
	public int getIrrTechID() throws Exception{
		String irrTechName = this.getMethod();
		if(irrTechName=="TimeBased"){
			return Hydrology.TIME_BASED;
		}else if(irrTechName=="RainSensor"){
			return Hydrology.RAIN_SENSOR_BASED;
		}else if(irrTechName=="SoilWaterSensor"){
			return Hydrology.SOIL_WATER_SENSOR_BASED;
		}else if(irrTechName=="ETController"){
			return Hydrology.ETBASED;
		}else{
			throw new Exception("Not valid irr tech");
			
		}
	}
	public void init(float[] et, float[] rain, float[] irrigation, float[] kc, float[] settings, float lotSize){
		this.et = et;
		this.rain = rain;
		this.Kcs = kc;
		this.irrigation = irrigation;
		this.settings = settings;
		this.lotSize = lotSize;
		this.method = "result";
	}
	
	public String getCalculationResult(String[] dates){
		if(this.result==null){
			this.calculate();
			this.calOutputAssessment();
		}
		String str = "";
		String[] names = new String[]{
			"Date","RSS","TH","ETpre","R", "I", "ET", "Kc", "FC", "RD"
		    ,"WP","CN","SWCi","WB","ETa","SWCm"
		    ,"SWCf","Smx","S","Q","F","PERC","SWCi+1","ETCcur"
		};
		for(String name: names){
			str += name+",";
		}
		str +="\r\n";
		int num = 0;
		for(float[] line :this.result){
			str += dates[num]+",";
			for(float item: line){
				str += item+",";
			}
			str +="\r\n";
			num++;
		}
		str +="Lot_Size,PERC_SUM,Q_SUM,I_SUM,R_SUM,Water Not Used%, Water Stress,WATER_NOT_USED_GAL,Ranking\r\n";
		str += this.lotSize+",";
		for(float number: finalResult){
			str += number+",";
		}
		str += "\r\n";
		return str;
		
	}
	public float[] calOutputAssessment(){
		float[] output = new float[8];
		float percSum = 0f;
		float qSum = 0f;
		float iSum = 0f;
		float rSum = 0f;
		int wsDays = 0;
		float awMad = this.getAW() * 0.5f;
		for(float[] row:this.result){
			percSum += row[Hydrology.PERC];
			qSum += row[Hydrology.Q];
			iSum += row[Hydrology.I];
			rSum += row[Hydrology.R];
			
		}
		for(float[] row:this.result){
			float SWCi = row[Hydrology.SWCnext];
			if(SWCi < awMad){
				wsDays++;
			}
			
		}
		output[Hydrology.PERC_SUM] = percSum;
		output[Hydrology.Q_SUM] = qSum;
		output[Hydrology.I_SUM] = iSum;
		output[Hydrology.R_SUM] = rSum;
		output[Hydrology.WATER_NOT_USED] = (percSum + qSum - rSum )/iSum*100;
		output[Hydrology.WATER_NOT_USED] = (output[Hydrology.WATER_NOT_USED]<=0 ? 0 : output[Hydrology.WATER_NOT_USED]);
		output[Hydrology.WATER_STRESS] = wsDays;
		output[Hydrology.WATER_NOT_USED_GAL] = (percSum + qSum -rSum) * 0.03281f * this.lotSize*43560f*7.48f;
		output[Hydrology.WATER_NOT_USED_GAL] = (output[Hydrology.WATER_NOT_USED_GAL]<=0 ? 0 : output[Hydrology.WATER_NOT_USED_GAL]);
		try {
			output[Hydrology.RANKING] = Rank.getRank(output[Hydrology.WATER_NOT_USED], output[Hydrology.WATER_STRESS]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); //log here
		}
		this.finalResult = output;
		return output;
	}
	
	public float getAW(){
		float aw = 0;
		float[] row = this.result[0];
		aw = row[FC]*row[RD]-row[WP]*row[RD];
		return aw;
	}
	public abstract float[][] calculate();
}
