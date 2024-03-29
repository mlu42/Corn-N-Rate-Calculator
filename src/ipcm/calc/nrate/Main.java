package ipcm.calc.nrate;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

/*
 * The main class for the app. This activity holds a tab widget
 * for navigation within the app as well as a frame layout to
 * hold the individual activities within the app
 */

public class Main extends TabActivity {
	
	// Global variables that can be accessed from anywhere
	public static String applicationRate = "";	// The application rate
	public static String range = "";			// The application rate range
	public static String soilType = "";			// The soil type the user has selected
	public static String prevCrop = "";			// The previous crop the user has selected
	public static String fertPrice = "";		// The fertilizer price per ton
	public static String nPercent = "";			// The nitrogen concentration
	public static String cornPrice = "";		// The corn price per bushel
	public static String nPrice = "";			// The nitrogen price calculated
	public static String ratio = "";			// The N:Corn ratio calculated
	
    /*
     * Called when the activity is first created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 			// Sets the layout to draw from main.xml in the res/layout directory
        
        Resources res = this.getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost(); // Gets the TabHost from the TabActivity
        tabHost.setup();				// The activity TabHost
        TabHost.TabSpec spec;  			// Resusable TabSpec for each tab
        Intent intent;  				// Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, Calculator.class);							// Sets the intent to launch the Activity defined by Calculator.java

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("calculator"); 										// Sets the "tag" used to keep track of this tab
        spec.setIndicator("Calculator", res.getDrawable(R.drawable.calculator_icon)); 	// Sets the title and icon
        spec.setContent(intent); 														// Sets the action to take when selected
        tabHost.addTab(spec); 															// Adds the tab to the 

        // Do the same for the other tabs
        intent = new Intent().setClass(this, Guidelines.class);
        spec = tabHost.newTabSpec("guidelines");
        spec.setIndicator("Guidelines", res.getDrawable(R.drawable.guidelines_icon));
        spec.setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, Credits.class);
        spec = tabHost.newTabSpec("credits");
        spec.setIndicator("N Credits", res.getDrawable(R.drawable.credits_icon));
        spec.setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, Email.class);
        
        spec = tabHost.newTabSpec("email");
        spec.setIndicator("Email", res.getDrawable(R.drawable.email_icon));
        spec.setContent(intent);
        tabHost.addTab(spec);
        
        // Commented out because it was getting cramped and I wanted to try adding this to the context menu
        /*intent = new Intent().setClass(this, Info.class);
        spec = tabHost.newTabSpec("info");
        spec.setIndicator("Info", res.getDrawable(R.drawable.info_icon));
        spec.setContent(intent);
        tabHost.addTab(spec);*/

        tabHost.setCurrentTab(0); // Sets the tab view to be focused on the calculator activity initially

    }
    
    /*
     * Creates the context menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.context_menu, menu);
        return true;
    }
    
    /*
     * Specifies the behavior of the options menu item(s)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:	Intent intent = new Intent(this, Info.class);
            				startActivity(intent);
            				return true;
            default:		return false;				
            
            				
        }
    }
}