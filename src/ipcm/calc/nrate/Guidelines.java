package ipcm.calc.nrate;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

public class Guidelines extends Activity{

	WebView webview;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.guidelines);
        
        webview = (WebView) findViewById(R.id.guidelines_webview);	// Finds the WebView
        
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(webview.getWindowToken(), 0);        
        
        webview.loadUrl("file:///android_asset/guidelines.html");// Loads the WebView's content from the assets
        
    }
    
    @Override
    public void onResume()
    {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(webview.getWindowToken(), 0);
    	
    	super.onResume();
    }
}
