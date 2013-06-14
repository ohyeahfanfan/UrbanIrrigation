package com.fawn.urbanIrrigationTool.server.Table;

import java.util.Arrays;
import java.util.List;

public class CropCoefficientTable {
	/* Table 2
	 * this.cropCoefficient
	 * x: Month           type: int      range: [0,11]
	 * y: Part in Florida type: String   range: (north, central, south)
	 */
	private float[][] cropCoefficient 
							= new float[][]{{0.35f,0.45f,0.71f}//Jan
											,{0.35f,0.45f,0.79f}//Feb
											,{0.55f,0.65f,0.78f}//Mar
											,{0.80f,0.80f,0.86f}//Apr
											,{0.90f,0.90f,0.99f}//May
											,{0.75f,0.75f,0.86f}//Jun
											,{0.70f,0.70f,0.86f}//Jul
											,{0.70f,0.70f,0.90f}//Aug
											,{0.75f,0.75f,0.87f}//Sep
											,{0.70f,0.70f,0.86f}//Oct
											,{0.60f,0.60f,0.84f}//Nov
											,{0.45f,0.45f,0.71f}//Dec
											};
	private String[] yNames = new String[]{"north","central","south"};
	
	public boolean areParaNamesValid(int x, String y){
		List<String> yNamesList = Arrays.asList(this.yNames);
		
		boolean a = yNamesList.contains("FC");
		if (!yNamesList.contains(y)) {
			System.out.println("Invalid y para Name");
			return false;
		}
		if (x >= 12 || x < 0) {
			System.out.println("Invalid x para Name");
			return false;
		}
		return true;
	}
	public float get(int xName, String yName) {
		float returnVal = 9999f;
		List<String> yNamesList = Arrays.asList(this.yNames);
		
		if(areParaNamesValid(xName,yName)){
			int y = yNamesList.indexOf(yName);
			int x = xName;
			if (x < 12 && y < this.cropCoefficient[x].length) {
				returnVal = this.cropCoefficient[x][y];
			} else {
				System.out.println("Crop Coefficient is out of Bound");
				System.exit(1);
			}
		}else{
			System.exit(0);
		}
		return returnVal;
	}

	public static void main(String[] args) {
		CropCoefficientTable stt = new CropCoefficientTable();
		float fc = stt.get(4,"south");
		System.out.print(fc);
	}
}


