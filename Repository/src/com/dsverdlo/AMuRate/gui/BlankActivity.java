package com.dsverdlo.AMuRate.gui;

import java.util.Locale;

import com.dsverdlo.AMuRate.R;
import com.dsverdlo.AMuRate.objects.AMuRate;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;


/**
 * This BlankActivity is an extension of the Activity class. Every activity
 * in the AMuRate GUI will extend the BlankActivity. The settings menu is
 * defined in this activity and should be available in every activity.
 * 
 * @author David Sverdlov
 *
 */
public class BlankActivity extends Activity {
	private AMuRate amr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		amr = (AMuRate) getApplicationContext();
	}
	
	/**
	 * Create the options menu.
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {

		// First item: refresh: reloads the currect screen
        SubMenu refresh = menu.addSubMenu(Menu.NONE, 199, 0, "Refresh").setIcon(android.R.drawable.ic_menu_rotate);
        
        // Second item: change language. To add languages, extend the menu here
        SubMenu langMenu = menu.addSubMenu(0, 200, 1, R.string.language).setIcon(android.R.drawable.ic_menu_preferences);
            langMenu.add(1, 201, 0, "Nederlands");
            langMenu.add(1, 202, 0, "Français");
            langMenu.add(1, 203, 0, "English");
        
        // Third item: quit application
        SubMenu quit = menu.addSubMenu(Menu.NONE, 198, 2, "Quit").setIcon(android.R.drawable.ic_lock_power_off);
       
        return super.onCreateOptionsMenu(menu);
    }

	/**
	 * This method defines the actions to be taken when a menu item is pressed.
	 * To add a language (or other menu action), add it in this switch
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
        
        case 198: actionQuit();	break;
        	
        case 199: actionRefresh(); break;
        	
        case 201: 
        	actionSetLanguage("nl", "Taal veranderd naar Nederlands !");
            // Then reload the current activity to update the strings
            actionRefresh();
            break;

        case 202:
        	actionSetLanguage("fr", "Language changé en Français !");
            actionRefresh();
            break;  

        case 203:
        	actionSetLanguage("en", "Switched to English !");
            actionRefresh();
            break;  
        }
        return super.onOptionsItemSelected(item);
    }

	private void actionQuit() {
    	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	intent.putExtra("EXIT", true);
    	startActivity(intent);
	}
	
	private void actionRefresh() {
    	finish();
        Intent refresh = new Intent(amr, getClass());
        if(getIntent().getExtras() != null) refresh.putExtras(getIntent().getExtras());
        startActivity(refresh);
	}
	
	private void actionSetLanguage(String lang, String toast) {
        Locale locale = new Locale(lang); 
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
	}
}
