package edu.cs307.railbarons;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Intent;

public class Activity_Settings extends Activity implements CompoundButton.OnCheckedChangeListener {
	
	private ImageButton x_button;
	private Switch audioSwitch;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.settings);
        this.x_button = (ImageButton) findViewById(R.id.x_button);
        initAudioSwitch();
    }
    
    public void initAudioSwitch()
    {
    	audioSwitch = (Switch) findViewById(R.id.audioSwitch);
        if(audioSwitch != null)
        {
        	audioSwitch.setOnCheckedChangeListener(this);
        }
        boolean audioSwitchSet = InfoCenter.getAudioStatus();
        audioSwitch.setChecked(audioSwitchSet);
    }
    
    public void backButton(View v)
    {
    	finish(); 
    }
    
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
    	//Toast.makeText(this, "You have switched the switch", Toast.LENGTH_SHORT).show();
    	InfoCenter.setAudioStatus(audioSwitch.isChecked());
    }
}
