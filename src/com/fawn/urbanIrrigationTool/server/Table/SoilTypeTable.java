package com.fawn.urbanIrrigationTool.server.Table;

import java.util.Arrays;
import java.util.List;

public class SoilTypeTable {
	/* Ref: 
	 * FAWN Irrigation Tool Model Manual
	 * Table 1
	 * this.SoilType
	 * x: Soil Type  type: String   range: see this.xNames
	 * y: Para Name  type: String   range: see this.yNames
	 */
	public static float[][] soilTypes =
			new float[][] { { 0.08f, 0.02f, 0}//Sand
							,{0.16f, 0.06f, 0}//Sandy Loam
							,{0.26f, 0.08f, 1}//Loam
							,{0.31f, 0.01f, 1}//Silt Loam
							,{0.34f, 0.14f, 2}//Clay Loam
							,{0.37f, 0.16f, 3}};//Clay
	public static String[] yNames = new String[] { "FC", "WP","soil group"};//Column Name
	public static String[] xNames = new String[] { "sand", "sandy loam", "loam",
			"silt loam", "clay loam", "clay" };
	
	public static boolean areParaNamesValid(String x, String y){
		List<String> yNamesList = Arrays.asList(yNames);
		List<String> xNamesList = Arrays.asList(xNames);
		boolean a = yNamesList.contains("FC");
		if (!yNamesList.contains(y)) {
			System.out.println("Invalid y para Name");
			return false;
		}
		if (!xNamesList.contains(x)) {
			System.out.println("Invalid x para Name");
			return false;
		}
		return true;
	}
	public static float get(String xName, String yName) {
		float returnVal = 9999f;
		List<String> yNamesList = Arrays.asList(yNames);
		List<String> xNamesList = Arrays.asList(xNames);
		if(areParaNamesValid(xName,yName)){
			int x = xNamesList.indexOf(xName);
			int y = yNamesList.indexOf(yName);
			if (x < soilTypes.length && y < soilTypes[x].length) {
				returnVal = soilTypes[x][y];
			} else {
				System.out.println("Out of Bound");
				System.exit(1);
			}
		}else{
			System.exit(0);
		}
		return returnVal;
	}

	public static void main(String[] args) {
		//SoilTypeTable stt = new SoilTypeTable();
		float fc = SoilTypeTable.get("sandy loam","soil group");
		System.out.print(fc);
	}
}
