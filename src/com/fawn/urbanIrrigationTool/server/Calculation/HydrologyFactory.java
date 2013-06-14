package com.fawn.urbanIrrigationTool.server.Calculation;

public class HydrologyFactory {
public static Hydrology creator(int type){
	Hydrology h = null;
	if(type == Hydrology.TIME_BASED){
		h =  new TimeBased();
	}else if(type == Hydrology.RAIN_SENSOR_BASED){
		h = new RainSensor();
	}else if(type == Hydrology.SOIL_WATER_SENSOR_BASED){
		h = new SoilWaterSensor();
	}else if(type == Hydrology.ETBASED){
		h = new ETController();
	}
	return h;
}
}
