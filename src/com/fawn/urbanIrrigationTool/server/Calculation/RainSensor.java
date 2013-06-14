package com.fawn.urbanIrrigationTool.server.Calculation;

import java.util.Calendar;

import com.fawn.urbanIrrigationTool.server.Util;

public class RainSensor extends Hydrology{
	public void init(float[] et, float[] rain, float[] irrigation, float[] kc, float[] settings, float lotSize){
		super.init(et, rain, irrigation, kc, settings, lotSize);
		this.method = "RainSensor";
	}
	public void calculateOneRound(float[] input, float[] output, int roundNum){
	    if(input[R] > input[RSS]){
	    	output[WB] = input[R];
	    }else{
	    	output[WB] = input[R] + input[I];
	    }
	    output[ETa] = input[ET] * input[Kc];
	    if(roundNum==0){
			output[SWCcur] = 0.75f * input[FC] * input[RD];
			output[SWCf] = output[SWCcur];
			output[SWCm] = output[SWCf];
		}else{
			output[SWCcur] = input[SWCnext];
			float WPMULRD = input[WP] * input[RD];
			if(input[SWCnext]<WPMULRD){
				output[SWCf] = WPMULRD;
			}else{
				output[SWCf] = input[SWCnext];
			}
			float left = output[SWCf] - output[ETa];
			if(left > WPMULRD){
				output[SWCm] = left;
			}else{
				output[SWCm] = WPMULRD;
			}
		}
		output[Smx] = 2540/input[CN] - 25.4f;
		output[S] = output[Smx]*(1-output[SWCm]/(input[FC]*input[RD]));
		if(0.2*output[S] > 0 && 0.2*output[S] < output[WB]){
			output[Q] = output[WB]- 0.2f*output[S];
			output[Q] = (float)Math.pow(output[Q], 2)/(output[WB]+0.8f*output[S]);
		}else{
			output[Q] = 0;
		}
		if(output[WB]-output[Q]>0){
			output[F] = output[WB]-output[Q];
			float left = output[F] + output[SWCm];
			float right = input[FC] * input[RD];
			if(left < right){
				output[PERC] = 0;
			}else{
				output[PERC] = left-right;
			}
		}else{
			output[F] = 0;
			output[PERC] = 0;
		}
		if(output[PERC]>0){
			output[SWCnext] = input[FC] * input[RD];
		}else{
			output[SWCnext] = output[SWCf]+ output[F]-output[ETa];
		}
	}
	public float[][] calculate() {
		float[] output = new float[25];
		float[] input = new float[25];
		float[][] outputs = new float[7][25];
		for(int i = 0; i < 7; i++){
			input[R] = rain[i];
			input[I] = irrigation[i];
			input[ET] = et[i];
			input[Kc] = Kcs[i];
			input[FC] = settings[FC];
			input[RD] = settings[RD];
			input[WP] = settings[WP];
			input[CN] = settings[CN];
			input[RSS] = settings[RSS];
			for(int j = WB; j < output.length; j++){
				input[j] = output[j];
			}
			this.calculateOneRound(input, output, i);
			for(int j=0; j < output.length; j++){
				if(j<SWCcur){
					outputs[i][j] = input[j];
				}else{
					outputs[i][j] = output[j];
				}
			}
		}
		result = outputs;
		return outputs;
	}
	public static void main(String[] args) {
		RainSensor h = new RainSensor();
		float[] et = new float[]{0.30f,0.38f,0.46f,0.48f,0.51f,0.53f,0.48f};
		float[] rain = new float[]{0.28f,0.58f,0,0,0,0,0};
		float[] irrigation = new float[]{1.27f,0,0,0,1.27f,0,0};
		float[] Kcs = new float[]{0.86f,0.86f,0.86f,0.86f,0.86f,0.86f,0.86f};
		float[] settings = new float[25];
		settings[FC]=0.16f; settings[RD] = 30f; settings[WP] = 0.06f; settings[CN]=77f;
		h.init(et,rain,irrigation,Kcs, settings,0.12f);
		//h.init(et,rain,irrigation,settings,0.12f);
		//float[][] outputs  = h.calculate();
		Calendar startDate = Calendar.getInstance();
		String[] printedDates = new String[7];
		for(int i = 0; i < printedDates.length;i++){
			printedDates[i] = Util.formatDate(startDate.getTime());
		}
		System.out.println(h.getCalculationResult(printedDates));
		
	}
}
