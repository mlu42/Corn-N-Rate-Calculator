package ipcm.calc.nrate;

import java.text.DecimalFormat;



import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/*
 * Defines the way in which the second input field should behave
 * in order to format input for integer percentages less than 100
 */

class PercentFormatWatcher implements TextWatcher {
	
	// Layout objects that need to be read from or written to
	private EditText et;					// The EditText that is being input into
	private EditText fertPriceET;			// The fertilizer price field
	private EditText nPercentET;			// The N percent field
	private EditText cornPriceET;			// The corn price field
	private TextView nPrice;				// The nitrogen price output
	private TextView ratio;					// The N:Corn output
	private ThreeStateToggle yieldPicker;	// The toggle switch of yields
	private TwoStateToggle cropPicker;		// The toggle switch of previous crops
	private TextView result;				// The result field
	private TextView resultRange;			// The result range output
	
	// Other instance variables
	private String prevState = "";						// The previous state of the EditText
	private String generalCorrectInput = "\\d{1,3}";	// A java regex pattern for a percent. This one if 1-3 digits followed by a percent sign (e.g. 4%, 67%, 100%)
	
	
	/*
	 * A constructor that takes all of the necessary layout objects as arguments.
	 */
	public PercentFormatWatcher(EditText e, EditText fertPrice, EditText nPercent, EditText cornPrice, TextView a, TextView b, ThreeStateToggle yP, TwoStateToggle cP, TextView r, TextView rR)
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
	 * An overridden method that defines the behavior before text is changed
	 */
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
		cursorPos();
	}
	
	/*
	 * An overridden method that defines the behavior of the EditText after the text has
	 * been changed. Essentially, it resets the cursor position to the correct position and
	 * re-calculates all the calculated values.
	 */
	public void afterTextChanged(Editable e)
	{		
		cursorPos();
		
		// Set the EditText to the new text if it doesn't already equal it.
		if(!et.getText().toString().equals(prevState))
			et.setText(prevState);
		
		// Calculate the N price and convert it to a String
		Double nP = calcNPrice();
		String nPriceString = nP.toString();
		
		// Adds 0's at the end to guarantee that the result will have two decimal places
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
		
		// -1 is the default result if a result can't be calculated yet. If nP is not -1, we can show the result.
		// NOTE: This can be optimized to make it so the code doesn't run if nP is -1. Do that at some point.
		if(nP == -1)
			nPrice.setText("-");
		else
			nPrice.setText("$" + nPriceString);
		
		// Calculates the N:Corn ratio and converts it to a String
		Double r = calcRatio();
		String ratioString = r.toString();
		
		// Add 0's at end to guarantee two decimal places
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
		
		// -1 is the default results if a result can't be calculated yet. If nP is not -1, we can show the result
		// NOTE: This can be optimized to make it so the code doesn't run if nP is -1. Do that at some point.
		if(nP == -1)
			nPrice.setText("-");
		else
			nPrice.setText("$" + nPriceString);
		
		// -1 is the default results if a result can't be calculated yet. If nP is not -1, we can show the result
				// NOTE: This can be optimized to make it so the code doesn't run if nP is -1. Do that at some point.
		if(r == -1)
			ratio.setText("-");
		else
			ratio.setText(ratioString);
		
		// Creates a CalcHelper and calculates the result and range
		CalcHelper calc = new CalcHelper(ratio, yieldPicker, cropPicker, result, resultRange);
		calc.calculate();
		
		// A quick, dirty fix for a wierd bug where the N price would sometimes show "$NaN.00" when it should be $0.00
		if(nPrice.getText().toString().equals("$NaN.00"))
			nPrice.setText("$0.00");
		
	}
	
	/*
	 * Sets the behavior that the EditText takes when text has been changed
	 */
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{		
		// Converts the new input to a string because it's easier to work with
		String text = s.toString();
		
		if(!text.equals(""))
		{			
			if(text.matches(generalCorrectInput))
			{
				int val = Integer.parseInt(text);
				
				if(val <= 100)
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
	
	/*
	 * A method that resets the cursor position to the 
	 */
	private void cursorPos()
	{
		// If there is text
			et.setSelection(et.getText().length());
	}
	
	/*
	 * Calculates the price of nitrogen per pound based on the input values
	 */
	private double calcNPrice()
	{
		double fertPrice = 0;
		double nPercent = 0;
		
		// Gets the values
		String fPrice = fertPriceET.getText().toString();
		String percentN = nPercentET.getText().toString();
		
		// If either of them are null, the calculation will not be performed
		if(!fPrice.equals("") && !percentN.equals(""))
		{
			/*
			 * This try block is a dirty fix for a bug that I was having when it was
			 * deleted back to zero. It works, but would probably be worth going back
			 * to look at again in the future.
			 */
			try
			{
				fertPrice = Double.parseDouble(fPrice.substring(0));
				nPercent = Double.parseDouble(percentN.substring(0));
				
				double temp = fertPrice*(100/nPercent)/2000;
				DecimalFormat twoPlaces = new DecimalFormat("#.##");
				
				return Double.valueOf(twoPlaces.format(temp)); // Bug was here without the try block
			}
			catch(Exception e)
			{
				return -1; // Default value when it can't be calculated
			}
			
		}
		else
			return -1; // -1 is the default value when something can't be calculated
	}
	
	private double calcRatio()
	{
		double nP = 0;
		double cP = 0;
		
		// If the nPrice is has been calculated
		if(!nPrice.getText().toString().equals("-"))
		{
			/*
			 * This try block also fixes a bug with a quick, dirty fix
			 */
			try
			{
				nP = Double.parseDouble(nPrice.getText().toString().substring(1));				
			}
			catch(Exception e)
			{
				return -1; // Default return value
			}
		}
		
		// If a corn price has been entered
		if(!cornPriceET.getText().toString().equals(""))
			cP = Double.parseDouble(cornPriceET.getText().toString().substring(0));
		
		// If both nPrice and cornPrice are available
		if(nP != 0 && cP != 0)
		{
			double temp = nP/cP;
			DecimalFormat twoPlaces = new DecimalFormat("#.##");
			return Double.valueOf(twoPlaces.format(temp));
		}
		else
		{
			return -1; // Default return value
		}
			
	}
	
}
