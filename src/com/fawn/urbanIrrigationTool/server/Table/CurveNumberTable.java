package com.fawn.urbanIrrigationTool.server.Table;

public class CurveNumberTable {
	/* Ref: 
	 * FAWN Irrigation Tool Model Manual
	 * Table 3 CN values used by the model
	 * this.curveNumber
	 * x: Lot Size   type: float range: [0,2]
	 * y: Soil Group type: float range: [0,3] 0,1,2,3 stand for A, B, C, D
	 * Lot Size Unit: acres
	 * 
	 */
//	CNI
//	private int[][] curveNumber = new int[][] {
//			{ 77, 85, 90, 92 },// 1/8 or less
//			{ 61, 75, 83, 87 },// 1/4
//			{ 57, 72, 81, 86 },// 0.33
//			{ 54, 70, 80, 85 },// 1/2
//			{ 51, 68, 79, 84 },// 1 
//			{ 46, 65, 77, 82 }//  2 
//			};
	//CNII
	private int[][] curveNumber = new int[][] {
			{ 59, 70, 79, 82 },// 1/8 or less
			{ 41, 56, 67, 73 },// 1/4
			{ 37, 53, 64, 72 },// 0.33
			{ 34, 50, 63, 70 },// 1/2
			{ 32, 48, 62, 69 },// 1 
			{ 27, 45, 59, 66 }//  2 
			};

	public int get(float x, float yFloat) {
		int returnVal = 9999;
		//soil group y can not be float. but soil group is float in soil type table. then convert to int here.
		int y = (int) yFloat;
		if (x >= 0 && x <= 2 && y >= 0 && y <= 3) {
			// y: soil group 0=A, 1=B, 2=C, 3=D
			if (x <= 0.25) {
				returnVal = this.curveNumber[0][y];
			} else if (x <= 0.25 && x > 0.125) {
				returnVal = this.curveNumber[1][y];
			} else if (x <= 0.33 && x > 0.25) {
				returnVal = this.curveNumber[2][y];
			} else if (x <= 0.5 && x > 0.33) {
				returnVal = this.curveNumber[3][y];
			} else if (x <= 1 && x > 0.5) {
				returnVal = this.curveNumber[4][y];
			} else if (x <= 2 && x > 1) {
				returnVal = this.curveNumber[5][y];
			}
		} else {
			System.out.println("x should be in [0,2] and y should be in[0,3]");
			
			//System.exit(1);
		}
		return returnVal;
	}

	public static void main(String[] args) {
		CurveNumberTable stt = new CurveNumberTable();
		int fc = stt.get(0.125f, 2);
		System.out.print(fc);
	}
}
