package ipcm.calc.nrate;

import java.text.DecimalFormat;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/*
 * Defines the behavior that the first and third EditTexts should
 * take when input is changed in order to create currency format.
 * This class limits the currency to whole numbers
 */

class CurrencyWholeFormatWatcher implements TextWatcher {
	
	// Layout objects
	private EditText et;					// The EditText being manipulated	
	private EditText fertPriceET;			// The fertilizer price EditText
	private EditText nPercentET;			// The N percent EditText
	private EditText cornPriceET;			// The corn price EditText
	private TextView nPrice;				// The nitrogen price calculated value
	private TextView ratio;					// The N:Corn ratio calculated value
	private ThreeStateToggle yieldPicker;	// The yield toggle switch
	private TwoStateToggle cropPicker;		// The previous crop toggle switch
	private TextView result;				// The result calculated
	private TextView resultRange;			// The result range calculated
	
	// Other instance variables
	private String prevState = "";										// The previous state of the EditText (et)
	private String singleDigit = "\\d";									// A java regex that is defined as a single digit (0-9)
	private String generalCorrectInput = "\\d{1,4}";	// The general form of a correct input ($####)
	
	
	/*
	 * A constructor that takes all of the necessary layout objects
	 */
	public CurrencyWholeFormatWatcher(EditText e, EditText fertPrice, EditText nPercent, EditText cornPrice, TextView a, TextView b, ThreeStateToggle yP, TwoStateToggle cP, TextView r, TextView rR)
	{
		et = e;
		fertPriceET = fertPrice;
		nPercentET = nPercent;
		cornPriceET = cornPrice;
		nPrice = a;
		ratio = b;
		yieldPicker = yP;
		cropPicker = cP;
		result = r;
		resultRange = rR;
	}
	
	/*
	 * The action to take before the text is changed. I did nothing
	 */
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
		cursorPos();
	}
	
	/*
	 * The action to take after the text is changed
	 */
	public void afterTextChanged(Editable e)
	{		
		// This if prevents infinite loops and stack overflows
		if(!et.getText().toString().equals(prevState))
			et.setText(prevState);
		
		// Calculate the nitrogen price
		Double nP = calcNPrice();
		String nPriceString = nP.toString();
		
		// Add trailing zeroes to ensure two decimal places
		if(nPriceString.contains("."))
		{
			if(nPriceString.substring(nPriceString.indexOf(".")).length() == 1)
				nPriceString += "00";
			else
				if(nPriceString.substring(nPriceString.indexOf(".")).length() == 2)
					nPriceString += "0";
		}
		else
		{
			nPriceString += ".00";
		}
		
		// Sets a null value if the result could not be calculated
		if(nP == -1)
			nPrice.setText("-");
		else
			nPrice.setText("$" + nPriceString);
		
		// Calculate the N:Corn ratio
		Double r = calcRatio();
		String ratioString = r.toString();
		
		// Add trailing zeroes to ensure two decimal places
		if(ratioString.contains("."))
		{
			if(ratioString.substring(ratioString.indexOf(".")).length() == 1)
				ratioString += "00";
			else
				if(ratioString.substring(ratioString.indexOf(".")).length() == 2)
					ratioString += "0";
		}
		else
		{
			ratioString += ".00";
		}
		
		// Sets a null value if the result could not be calculated
		if(nP == -1)
			nPrice.setText("-");
		else
			nPrice.setText("$" + nPriceString);
		
		// Sets a null value if the result could not be calculated
		if(r == -1)
			ratio.setText("-");
		else
			ratio.setText(ratioString);
		
		// Creates a CalcHelper and calculates the final results
		CalcHelper calc = new CalcHelper(ratio, yieldPicker, cropPicker, result, resultRange);
		calc.calculate();
		
		cursorPos();	// Adjusts the cursor position
	}
	
	/*
	 * Sets the behavior to take when the text is being changed. This is where
	 * the main work for formatting the input happens.
	 */
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		// Converts the new input to a String because it's easier to work with
		
		String text = s.toString();
		
		if(!text.equals(""))
		{			
			if(text.matches(generalCorrectInput))
			{
				int val = Integer.parseInt(text);
				
				if(val <= 2000)
					prevState = ((Integer) val).toString();
			}
			else
			{
				
			}
		}
		else
		{
			prevState = "";
		}		
	}
	
	// Resets the cursor position to the end of the text
	private void cursorPos()
	{
		et.setSelection(et.getText().length());
	}
	
	/*
	 * Calculates the price of nitrogen
	 */
	private double calcNPrice()
	{
		double fertPrice = 0;
		double nPercent = 0;
		
		// If there is input
		try
		{
			if(fertPriceET.getText().toString().length() > 0)
				fertPrice = Double.parseDouble(fertPriceET.getText().toString());
		}
		catch(NumberFormatException e)
		{
			fertPrice = 0;
		}
		
		// If there is input
		try
		{
			if(nPercentET.getText().toString().length() > 0)
				nPercent = Double.parseDouble(nPercentET.getText().toString());
		}
		catch(NumberFormatException e)
		{
			nPercent = 0;
		}
		
		// If both of the values are not 0, do the calculation
		if(fertPrice != 0 && nPercent != 0)
		{
			double temp = fertPrice*(100/nPercent)/2000;
			DecimalFormat twoPlaces = new DecimalFormat("#.##");
			return Double.valueOf(twoPlaces.format(temp));
		}
		else
			return -1;	// Return the default value
	}
	
	/*
	 * Calculated the N:Corn ratio
	 */
	private double calcRatio()
	{
		double nP = 0;
		double cP = 0;
		
		// If there is input
		if(!nPrice.getText().toString().equals("-"))
			nP = Double.parseDouble(nPrice.getText().toString().substring(1));
		
		// If there is input
		if(!cornPriceET.getText().toString().equals(""))
			cP = Double.parseDouble(cornPriceET.getText().toString().substring(0));
		
		// If neither of them are 0, do the calculation
		if(nP != 0 && cP != 0)
		{
			double temp = nP/cP;
			DecimalFormat twoPlaces = new DecimalFormat("#.##");
			String ret = twoPlaces.format(temp);
			return Double.valueOf(ret);
		}
		else
		{
			return -1;	// Return the default value
		}			
	}
	
}