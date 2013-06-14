package com.fawn.urbanIrrigationTool.server.Table;

public class Rank {
	
	/*
		Ref: Output assessment
		Table 2: Evaluating results of the model and providing a color bar value.
		Rank	Water not used (%)	N loss (%)		Water stress days	Corresponding value	Color bar
		1	    15% or less			15% or less		0					1 to 1.5	 
		2		16 to 30%			16 to 30%		1 to 2				1.6 to 2.5	 
		3		31 to 50%			31 to 50%		3					2.6 to 3.5	 
		4		51 to 75%			51 to 75%		4					3.6 to 4.5	 
		5		76% and up			76% and up		5 or above			4.6 to 5.0	 
	N loss is not implemented	
	*/
	public static int getWaterNotUseRank(float percentage) throws Exception {
		if (percentage >= 0 && percentage <= 15) {
			return 1;
		} else if (percentage <= 30 && percentage >= 16) {
			return 2;
		} else if (percentage <= 50 && percentage >= 31) {
			return 3;
		} else if (percentage <= 75 && percentage >= 51) {
			return 4;
		} else if(percentage >= 76 && percentage <= 100){
			return 5;
		}else{
			throw new Exception("Water not used percentage should be in [0,100]");
		}
		
	}
	
	public static int getWaterStressDaysRank(int days) throws Exception{
		if(days == 0){
			return 1;
		}else if(days == 1 || days==2){
			return 2;
		}else if(days==3){
			return 3;
		}else if(days==4){
			return 4;
		}else if(days >= 5){
			return 5;
		}else{
			throw new Exception("Water stress day should be larger than 0");
		}
	}
	
	public static int getRank(float waterNotUsePercentage, float waterStressDays) throws Exception{
		int rank1 = getWaterNotUseRank(waterNotUsePercentage);
		int rank2 = getWaterStressDaysRank((int)waterStressDays);
		int correspondingValue = (rank1+rank2)/2;
		if(correspondingValue >= 1 && correspondingValue <= 1.5){
			return 1;
		}else if(correspondingValue >= 1.6 && correspondingValue <= 2.5){
			return 2;
		}else if(correspondingValue >= 2.6 && correspondingValue <= 3.5){
			return 3;
		}else if(correspondingValue >= 3.6 && correspondingValue >= 4.5){
			return 4;
		}else{
			return 5;
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println(Rank.getRank(30,1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
