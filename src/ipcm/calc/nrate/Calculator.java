 package ipcm.calc.nrate;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/*
 * The main calculator activity
 */

public class Calculator extends Activity{
	
	// Layout objects
	private ScrollView mainLayout;
	private AutoResizeTextView fertPriceLabel;		// The fertilizer price label
	private EditText fertPriceET;			// The fertilizer price input
	private TextView nPercentLabel;			// The percent nitrogen label
	private EditText nPercentET;			// The percent nitrogen input
	private TextView cornPriceLabel;		// The corn price label
	private EditText cornPriceET;			// The corn price input
	private TextView nPriceLabel;           // The nitrogren price label
	private TextView nPriceValue;			// The nitrogen price calculated value
	private TextView nCornLabel; 			// The N:Corn ratio label
	private TextView nCornValue;			// The N:Corn ratio calculated value
	private TextView outputHeader;			// The header above the output
	private TextView outputText;			// The result
	private TextView outputRange;			// The result range
	private TextView yieldHeader;			// Header over the yield toggle
	private TextView cropHeader;			// Header over the crop toggle
	private ThreeStateToggle yieldSwitch;	// The yield toggle
	private TwoStateToggle cropSwitch;		// The previous crop toggle
	private TextView highYield;				// High yield textview in toggle switch
	private TextView mediumYield;			// Medium yield textview in toggle switch
	private TextView sandsYield;			// Sands yield textview in toggle switch
	private TextView crop1;					// First crop in crop picker
	private TextView crop2;					// Second crop in crop picker
	
	/*
	 * Defines what happens when the Activity is paused, when the app
	 * moves to a new Activity within the TabView. In this case it simply
	 * updates the global variables so that they can be read from the
	 * Email activity whenever they need to be.
	 */
	@Override
	protected void onPause() { 
	   Main.applicationRate = outputText.getText().toString();
	   Main.range = outputRange.getText().toString();
	
	   // Updates soilType based on the yieldSwitch
	   switch(yieldSwitch.getCurrentState())
	   {
	   	case 0:	Main.soilType = "High/Very high yield";
	   			break;
	   			
	  	case 1:	Main.soilType = "Medium yield";
	   			break;
	   			
	   	case 2: Main.soilType = "Sands";
	   			break;
	   }
	   
	   // Updates previous crop based on the cropSwitch
	   switch(cropSwitch.getCurrentState())
	   {
	   	case 0:	if(yieldSwitch.getCurrentState() == 2)
	   				Main.prevCrop = "Irrigated";
	   			else
	   				Main.prevCrop = "Corn, veggies, and forage legumes";
	   			break;
	   	
	   	case 1:	if(yieldSwitch.getCurrentState() == 2)
	   				Main.prevCrop = "Non-Irrigated";
	   			else
	   				Main.prevCrop = "Soybeans, small grains";
	   			break;
	   }
	   
	   Main.fertPrice = fertPriceET.getText().toString();
	   Main.nPercent = nPercentET.getText().toString();
	   Main.cornPrice = cornPriceET.getText().toString();
	   
	   Main.nPrice = nPriceValue.getText().toString();
	   Main.ratio = nCornValue.getText().toString();
	   
	   // Calls the parent onPause() method
	   super.onPause();
	}

	/*
	 * Called when the Activity is first created
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator);		// Draws from calculator.xml
        
        // Assign all of the instance variables
        mainLayout = (ScrollView) findViewById(R.id.main_parent);
        fertPriceLabel = (AutoResizeTextView) findViewById(R.id.fert_price_label);
        fertPriceET = (EditText) findViewById(R.id.fert_price_input);
        nPercentLabel = (TextView) findViewById(R.id.percent_n_label);
        nPercentET = (EditText) findViewById(R.id.percent_n_input);
        cornPriceLabel = (TextView) findViewById(R.id.corn_price_label);
        cornPriceET = (EditText) findViewById(R.id.corn_price_input);
        nPriceLabel = (TextView) findViewById(R.id.percent_n_pound_label);
        nPriceValue = (TextView) findViewById(R.id.percent_n_pound_value);
        nCornLabel = (TextView) findViewById(R.id.n_corn_label);
        nCornValue = (TextView) findViewById(R.id.n_corn_value);
    	outputHeader = (TextView) findViewById(R.id.output_header);
    	outputText = (TextView) findViewById(R.id.output_text);
    	outputRange = (TextView) findViewById(R.id.output_range);
    	yieldHeader = (TextView) findViewById(R.id.soil_yield_header);
    	cropHeader = (TextView) findViewById(R.id.previous_crop_header);
    	yieldSwitch = new ThreeStateToggle();
    	cropSwitch = new TwoStateToggle();
    	highYield = (TextView) findViewById(R.id.high_yield);
    	mediumYield = (TextView) findViewById(R.id.medium_yield);
    	sandsYield = (TextView) findViewById(R.id.sands_yield);
    	crop1 = (TextView) findViewById(R.id.crop_1);
    	crop2 = (TextView) findViewById(R.id.crop_2);
    	
        
        /* 
         * 
         * Layout code
         * 
         */
    	
    	/*
    	 * Layout for the first row of inputs
    	 */
        // Gets the width and height of the display we are working with
        Display display = getWindowManager().getDefaultDisplay();
        final int width = display.getWidth();
        int height = display.getHeight();        
        
        // The layout that the three top inputs are nested in
        LinearLayout inputLayout = (LinearLayout) findViewById(R.id.inputLayout);
        
        // Sets the padding of the layout. My padding in the mock-up I drew was 30px for a 480px screen, hence the /16.
        int padding = width/16;
        inputLayout.setPadding(padding, padding, padding, width/32);
        
        // Get the EditText objects
        final EditText fertPriceET = (EditText) findViewById(R.id.fert_price_input);
        final EditText nPercentET = (EditText) findViewById(R.id.percent_n_input);
        final EditText cornPriceET = (EditText) findViewById(R.id.corn_price_input);
                
        // Get their default padding and width. All these values will be based on my mock-up.
        int inputWidth = width/4;
        int inputPadding = padding;
        
        // Set their dimensions and paddings       
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(inputWidth, 3*inputWidth/5);
        //params.setMargins(0, 0, inputPadding, 0);
        fertPriceET.setLayoutParams(params);
        nPercentET.setLayoutParams(params);
        cornPriceET.setLayoutParams(params);
        
        LinearLayout firstInput = (LinearLayout) findViewById(R.id.fert_price_layout);
        LinearLayout secondInput = (LinearLayout) findViewById(R.id.percent_n_layout);
        LinearLayout thirdInput = (LinearLayout) findViewById(R.id.corn_price_layout);
        
        firstInput.setPadding(0, 0, inputPadding, 0);
        secondInput.setPadding(0, 0, inputPadding, 0);
        //thirdInput.setPadding(0, 0, inputPadding, 0);
        
        // Get the input labels       
        
        // Set the labels' dimensions
        
        // Set the labels' paddings
        fertPriceLabel.setPadding(0, 0, 0, 0);
        nPercentLabel.setPadding(0, 0, 0, 0);
        cornPriceLabel.setPadding(0, 0, 0, 0);       
        
        	/*
        	 * Layout for the second row of inputs
        	 */
        
        // Get the TextView objects
        final TextView nPriceLabel = (TextView) findViewById(R.id.percent_n_pound_label);
        final TextView nPriceValue = (TextView) findViewById(R.id.percent_n_pound_value);
        final TextView nCornLabel = (TextView) findViewById(R.id.n_corn_label);
        final TextView nCornValue = (TextView) findViewById(R.id.n_corn_value);
        
        // Set min widths
        //nPriceValue.setMinWidth(width/8);
        //nCornValue.setMinWidth(width/8);
        
        // Set appropriate paddings
        //nPriceLabel.setPadding(inputPadding/5, inputPadding/5, inputPadding/5, inputPadding/5);
        nPriceValue.setPadding(inputPadding/3, 0, 0, 0);
        //nCornLabel.setPadding(inputPadding/5, 0, inputPadding/5, 0);
        nCornValue.setPadding(inputPadding/3, 0, 0, 0);
        
        params = (LinearLayout.LayoutParams) nPriceValue.getLayoutParams();
        params.rightMargin = width/16;
        
        	/*
        	 * Layout for the soil yield header
        	 */
        
        // Get the header
        TextView soilYieldHeader = (TextView) findViewById(R.id.soil_yield_header);
        
        // Set appropriate paddings
        soilYieldHeader.setPadding(0, width/32, 0, width/48);
        
        	/*
        	 * Layout for the soil yield picker
        	 */
        
        // Set paddings
        highYield.setPadding(0, width/48, 0, width/48);
        mediumYield.setPadding(0, width/48, 0, width/48);
        sandsYield.setPadding(0, width/48, 0, width/48);
        
        // Set widths
        highYield.setMinWidth(width * 7/24);
        mediumYield.setMinWidth(width * 7/24);
        sandsYield.setMinWidth(width * 7/24);
        
        // Creates the yieldSwitch from the TextViews
        yieldSwitch = new ThreeStateToggle(highYield, mediumYield, sandsYield, yieldHeader, cropHeader);
        
        	/* 
        	 * Layout for the previous crop header
        	 */
        
        // Get the header
        TextView prevCropheader = (TextView) findViewById(R.id.previous_crop_header);;
        
        // Set appropriate paddings
        prevCropheader.setPadding(0, width/24, 0, width/48);
        
	        /*
	    	 * Layout for the previous crop picker
	    	 */
	    
	    // Get the TextViews
	    
	    // Set paddings
	    crop1.setPadding(width/24, width/48, width/24, width/48);
	    crop2.setPadding(width/24, width/48, width/24, width/48);
	    
	    // Set widths
	    
	    crop1.setText("Corn, veggies,\nForage legumes");
	    crop2.setText("Soybeans,\nsmall grains");
	    
	    cropSwitch = new TwoStateToggle(crop1, crop2, cropHeader);
	    
	    // Get the layout for padding setting purposes
	    
	    LinearLayout prevCropLayout = (LinearLayout)findViewById(R.id.crop_picker_layout);
	    
	    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    params.bottomMargin = width/16;

	    prevCropLayout.setLayoutParams(params);
	    
	    	/*
	    	 * Layout for the output box
	    	 */
	    
	    // Get the layout box
	    LinearLayout outputBox = (LinearLayout)findViewById(R.id.output_layout);
	    
	    // Set its width
	    params = new LinearLayout.LayoutParams(width * 7/8, ViewGroup.LayoutParams.WRAP_CONTENT);
	    params.leftMargin = width/16;
	    params.rightMargin = width/16;
	    params.bottomMargin = width/16;
	    
	    outputBox.setLayoutParams(params);
	    
	    // Get the header
	    outputHeader.setPadding(0, width/48, 0, 0);
	    
	    // Get the output
	    outputText.setPadding(0, 0, 0, 0);
	    
	    // Get the output
	    outputRange.setPadding(0, 0, 0, width/32);
	    
	    // The CalcHelper object to do the final calculations
        final CalcHelper calc = new CalcHelper(nCornValue, yieldSwitch, cropSwitch, outputText, outputRange);
        
	    /*
	     * End layout
	     */
	    
	    //////////////////////////////////////////////////////////////////////
	    //////////////////////////////////////////////////////////////////////
	    
	    /*
	     * Behavior code
	     */
	    
        // Sets the default keyboard for all three inputs to be a simple keypad
	    fertPriceET.setRawInputType(Configuration.KEYBOARD_12KEY);
	    nPercentET.setRawInputType(Configuration.KEYBOARD_12KEY);
	    cornPriceET.setRawInputType(Configuration.KEYBOARD_12KEY);
	    
		/*
		 * The action that the fertilizer price EditText takes when
		 * the input has changed. Optimized for currency input
		 * in American dollars
		 */    
	    fertPriceET.addTextChangedListener( new CurrencyWholeFormatWatcher(fertPriceET, fertPriceET, nPercentET, cornPriceET, nPriceValue, nCornValue, yieldSwitch, cropSwitch, outputText, outputRange));
	   
	    // Makes the cursor move to the end of the EditText when it is clicked on (focused on)
	    fertPriceET.setOnFocusChangeListener(new OnFocusChangeListener(){

		   public void onFocusChange(View v, boolean f)
		   {
			   if(f)
				   fertPriceET.setSelection(fertPriceET.getText().length());
		   }
		   
	    });
	   
	    /*
	     * The action that the corn price EditText takes when
	     * the input has changed. Optimized for currency input
	     * in American dollars
	     */ 
	    cornPriceET.addTextChangedListener(new DollarFormatWatcher(cornPriceET, fertPriceET, nPercentET, cornPriceET, nPriceValue, nCornValue, yieldSwitch, cropSwitch, outputText, outputRange));
	    
	    // Makes the cursor move to the end of the EditText when it is clicked on (focused on)
	    cornPriceET.setOnFocusChangeListener(new OnFocusChangeListener(){

			   public void onFocusChange(View v, boolean f)
			   {
				   if(f)
					   cornPriceET.setSelection(cornPriceET.getText().length());
			   }
			   
		   });
	    
	    /*
	     * The action that the nitrogen percent EditText takes when
	     * the input has changed. Optimized for currency input
	     * in American dollars 
	     */ 
	    nPercentET.addTextChangedListener(new PercentFormatWatcher(nPercentET, fertPriceET, nPercentET, cornPriceET, nPriceValue, nCornValue, yieldSwitch, cropSwitch, outputText, outputRange));
	    
	    // Makes the cursor move to the correct position when the EditText is clicked on (focused on)
	    nPercentET.setOnFocusChangeListener(new OnFocusChangeListener(){

			   public void onFocusChange(View v, boolean f)
			   {
				   if(f)
					   if(nPercentET.getText().length() > 0)
						   nPercentET.setSelection(nPercentET.getText().length());
			   }
			   
		   });
	    
	    /*
	     * The actions that will be performed when the high yield toggle is clicked
	     */
	    highYield.setOnClickListener(new OnClickListener(){
	    	
	    	public void onClick(View v)
	    	{
	    		if(yieldSwitch.getCurrentState() != 0)
	    		{
	    			yieldSwitch.setState(0);
	    		}
	    		
	    		if(!cropSwitch.getLeftView().getText().equals(R.string.high_medium_left))
	    		{
	    			cropSwitch.getLeftView().setText(R.string.high_medium_left);
	    			cropSwitch.getRightView().setText(R.string.high_medium_right);
	    		}
	    		
	    		calc.calculate();
	    	}
	    	
	    });
		
	    /*
	     * The actions that will be performed when the medium yield toggle is clicked
	     */
	    mediumYield.setOnClickListener(new OnClickListener(){
	    	
	    	public void onClick(View v)
	    	{
	    		if(yieldSwitch.getCurrentState() != 1)
	    		{
	    			yieldSwitch.setState(1);
	    		}
	    		
	    		if(!cropSwitch.getLeftView().getText().equals(R.string.high_medium_left))
	    		{
	    			cropSwitch.getLeftView().setText(R.string.high_medium_left);
	    			cropSwitch.getRightView().setText(R.string.high_medium_right);
	    		}
	    		
	    		calc.calculate();
	    	}
	    	
	    });
	    
	    /*
	     * The actions that will be performed when the sands yield toggle is clicked
	     */
	    sandsYield.setOnClickListener(new OnClickListener(){
	    	
	    	public void onClick(View v)
	    	{
	    		if(yieldSwitch.getCurrentState() != 2)
	    		{
	    			yieldSwitch.setState(2);
	    		}
	    		
	    		if(!cropSwitch.getLeftView().getText().equals(R.string.sands_left))
	    		{
	    			cropSwitch.getLeftView().setText(R.string.sands_left);
	    			cropSwitch.getRightView().setText(R.string.sands_right);
	    		}
	    		
	    		calc.calculate();
	    	}
	    	
	    });
	    
	    /*
	     * The actions that will be performed when the first crop toggle is clicked
	     */
	    crop1.setOnClickListener(new OnClickListener(){
	    	
	    	public void onClick(View v)
	    	{
	    		if(cropSwitch.getCurrentState() != 0)
	    		{
	    			cropSwitch.setState(0);
	    		}
	    		
	    		calc.calculate();
	    	}
	    	
	    });
	    
	    /*
	     * The actions that will be performed when the second crop toggle is clicked
	     */
	    crop2.setOnClickListener(new OnClickListener(){
	    	
	    	public void onClick(View v)
	    	{
	    		if(cropSwitch.getCurrentState() != 1)
	    		{
	    			cropSwitch.setState(1);
	    		}
	    		
	    		calc.calculate();
	    	}
	    	
	    });
        
	    /*
	     * End layout
	     */
	    
	    ViewTreeObserver vto = fertPriceLabel.getViewTreeObserver(); 
	    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
	    	
	        public void onGlobalLayout() { 
	            fertPriceLabel.getViewTreeObserver().removeGlobalOnLayoutListener(this); 
	            float textSize  = fertPriceLabel.getTextSize();
	            nPercentLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
	            cornPriceLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
	            
	            fertPriceET.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*5/3);
	            nPercentET.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*5/3);
	            cornPriceET.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*5/3);

	            nPriceLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            nPriceValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            nCornLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            nCornValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            

	            yieldHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            cropHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);

	            highYield.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            mediumYield.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            sandsYield.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            crop1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            crop2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*6/5);
	            

	            outputHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
	            outputText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize*5);
	            outputRange.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);


	        } 
	    });
	    
    }
    
    protected void onStart(){
    	
    	super.onStart();
    	
    	 /*
    	 * Label sizes
    	 */
    	
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            
            if (event.getAction() == MotionEvent.ACTION_UP 
            			&& (x < w.getLeft() || x >= w.getRight() 
            			|| y < w.getTop() || y > w.getBottom()) ) { 
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
     return ret;
    }
    
}
