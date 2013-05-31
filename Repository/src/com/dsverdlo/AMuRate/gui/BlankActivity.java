package com.dsverdlo.AMuRate.gui;

import java.util.Locale;

import com.dsverdlo.AMuRate.objects.AMuRate;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

public class BlankActivity extends Activity {
	private AMuRate amr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		amr = (AMuRate) getApplicationContext();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SubMenu refresh = menu.addSubMenu(Menu.NONE, 199, 0, "Refresh").setIcon(android.R.drawable.ic_menu_rotate);
        
        SubMenu langMenu = menu.addSubMenu(0, 200, 1, "NL-FR-ENG").setIcon(android.R.drawable.ic_menu_preferences);
            langMenu.add(1, 201, 0, "Nederlands");
            langMenu.add(1, 202, 0, "Français");
            langMenu.add(1, 203, 0, "English");
        
        SubMenu quit = menu.addSubMenu(Menu.NONE, 198, 2, "Quit").setIcon(android.R.drawable.ic_lock_power_off);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
        case 198: 
        	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	intent.putExtra("EXIT", true);
        	startActivity(intent);
        	break;
        case 199: 
        	finish();
            Intent refresh = new Intent(amr, getClass());
            refresh.putExtras(getIntent().getExtras());
            startActivity(refresh);
        	break;
        case 201:

            Locale locale = new Locale("nl"); 
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            Toast.makeText(this, "Taal veranderd naar Nederlands !", Toast.LENGTH_LONG).show();
            finish();
            Intent reload_nl = new Intent(amr, MainActivity.class);
            startActivity(reload_nl);
            break;

        case 202:

            Locale locale2 = new Locale("fr"); 
            Locale.setDefault(locale2);
            Configuration config2 = new Configuration();
            config2.locale = locale2;
            getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
            Toast.makeText(this, "Language changé en Français !", Toast.LENGTH_LONG).show();
            finish();
            Intent reload_fr = new Intent(amr, MainActivity.class);
            startActivity(reload_fr);
            break;  


        case 203:

            Locale locale3 = new Locale("en"); 
            Locale.setDefault(locale3);
            Configuration config3 = new Configuration();
            config3.locale = locale3;
            getBaseContext().getResources().updateConfiguration(config3, getBaseContext().getResources().getDisplayMetrics());
            Toast.makeText(this, "Switched to English !", Toast.LENGTH_LONG).show();
            finish();
            Intent reload_en = new Intent(amr, MainActivity.class);
            startActivity(reload_en);
            break;  
        }
        return super.onOptionsItemSelected(item);
    }

	
}
