package ipcm.calc.nrate;

import android.widget.EditText;
import android.widget.TextView;

/*
 * A class that assists with performing the calculations for the final result
 */

public class CalcHelper {

	// Layout objects that need to be read from or written to
	private TextView nCornRatio;			// The N:Corn ratio calculated value
	private ThreeStateToggle yieldPicker;	// The yield switch
	private TwoStateToggle cropPicker;		// The previous crop switch
	private TextView result;				// The result
	private TextView resultRange;			// The result range
	
	// A table with all of the possible results that can be easily used to look up the result
	private int[][] resultTable = new int[4][6];{
	resultTable[0][0] = 190;
	resultTable[0][1] = 140;
	resultTable[0][2] = 145;
	resultTable[0][3] = 130;
	resultTable[0][4] = 215;
	resultTable[0][5] = 140;
	resultTable[1][0] = 165;
	resultTable[1][1] = 120;
	resultTable[1][2] = 125;
	resultTable[1][3] = 100;
	resultTable[1][4] = 200;
	resultTable[1][5] = 130;
	resultTable[2][0] = 150;
	resultTable[2][1] = 105;
	resultTable[2][2] = 115;
	resultTable[2][3] = 85;
	resultTable[2][4] = 185;
	resultTable[2][5] = 120;
	resultTable[3][0] = 135;
	resultTable[3][1] = 90;
	resultTable[3][2] = 105;
	resultTable[3][3] = 70;
	resultTable[3][4] = 175;
	resultTable[3][5] = 110;}
	
	// A table with all of the ranges that can be used to look up the correct range
	private String[][] rangeTable = new String[4][6];{
	rangeTable[0][0] = "170 - 210";
	rangeTable[0][1] = "125 - 160";
	rangeTable[0][2] = "130 - 160";
	rangeTable[0][3] = "110 - 150";
	rangeTable[0][4] = "200 - 230";
	rangeTable[0][5] = "130 - 150";
	rangeTable[1][0] = "155 - 180";
	rangeTable[1][1] = "105 - 130";
	rangeTable[1][2] = "115 - 140";
	rangeTable[1][3] = "85 - 120";
	rangeTable[1][4] = "185 - 210";
	rangeTable[1][5] = "120 - 140";
	rangeTable[2][0] = "140 - 160";
	rangeTable[2][1] = "95 - 115";
	rangeTable[2][2] = "105 - 125";
	rangeTable[2][3] = "70 - 95";
	rangeTable[2][4] = "175 - 195";
	rangeTable[2][5] = "110 - 130";
	rangeTable[3][0] = "125 - 150";
	rangeTable[3][1] = "80 - 105";
	rangeTable[3][2] = "95 - 110";
	rangeTable[3][3] = "60 - 80";
	rangeTable[3][4] = "165 - 185";
	rangeTable[3][5] = "100 - 120";
	}
	
	/*
	 * A constructor that takes all of the necessary layout objects to be read from or written to
	 */
	public CalcHelper(TextView ratio, ThreeStateToggle yP, TwoStateToggle cP, TextView res, TextView range)
	{
		nCornRatio = ratio;
		yieldPicker = yP;
		cropPicker = cP;
		result = res;
		resultRange = range;
	}
	
	/*
	 * Calculates the range by deciding which indexes to look up
	 * and looking them up in the tables
	 */
	public void calculate()
	{
		int r = 0;
		int c = 0;
		
		if(!nCornRatio.getText().toString().equals("-") && yieldPicker.getCurrentState() != -1 && cropPicker.getCurrentState() != -1)
		{
			double ratio = Double.parseDouble(nCornRatio.getText().toString());
			
			if(ratio >= 0.18)
			{
				r = 3;
			}
			else
			{
				if(ratio >= 0.13)
				{
					r = 2;
				}
				else
				{
					if(ratio >= 0.08)
					{
						r = 1;
					}
					else
					{
						r = 0;
					}
				}
			}
			
			int yield = yieldPicker.getCurrentState();
			
			if(yield == 0)
			{
				c = cropPicker.getCurrentState();
			}
			if(yield == 1)
			{
				c = 2 + cropPicker.getCurrentState();
			}
			if(yield == 2)
			{
				c = 4 + cropPicker.getCurrentState();
			}
			
			result.setText(((Integer)resultTable[r][c]).toString());
			resultRange.setText(rangeTable[r][c]);
		}
		else
		{
			result.setText("000");
			resultRange.setText("000 - 000");
		}
	}
	
}
