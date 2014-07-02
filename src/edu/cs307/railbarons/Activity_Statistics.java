package edu.cs307.railbarons;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

public class Activity_Statistics extends Activity {
	
	private ImageButton x_button;
	MediaPlayer mediaPlayer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.statistics);
        setupMediaPlayer();
        this.x_button = (ImageButton) findViewById(R.id.x_button);
        initStatTable();
    }
    
    public void initStatTable(){
        TableLayout tl = (TableLayout) findViewById(R.id.statTable);
        tl.removeAllViews();
        
        TableRow tr = new TableRow(this);
        
        int numCars = InfoCenter.getCar().size();
        
        TextView cars = new TextView(this);
        cars.setText("You own "+Integer.toString(numCars)+" cars!");
        cars.setTextAppearance(this, android.R.style.TextAppearance_Large);
        cars.setTextColor(Color.WHITE);
        cars.setTypeface(Typeface.SANS_SERIF);
        cars.setGravity(Gravity.CENTER_VERTICAL);
        cars.setPadding(10,0,0,0);
        tr.addView(cars);
        
        tl.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        
        TableRow trb = new TableRow(this);
        int numTrains = InfoCenter.getTrains().size();
        
        TextView trains = new TextView(this);
        trains.setText("You own "+Integer.toString(numTrains)+" trains!");
        trains.setTextAppearance(this, android.R.style.TextAppearance_Large);
        trains.setTextColor(Color.WHITE);
        trains.setTypeface(Typeface.SANS_SERIF);
        trains.setGravity(Gravity.CENTER_VERTICAL);
        trains.setPadding(10,10,0,0);
        trb.addView(trains);
        
        tl.addView(trb, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        
        TableRow tr2 = new TableRow(this);
        
        int currency = InfoCenter.getMoney();
        
        TextView money = new TextView(this);
        money.setText("Bank Balance: $"+Integer.toString(currency));
        money.setTextAppearance(this, android.R.style.TextAppearance_Large);
        money.setTextColor(Color.GREEN);
        money.setTypeface(Typeface.SANS_SERIF);
        money.setGravity(Gravity.CENTER_VERTICAL);
        money.setPadding(10,10,0,0);
        tr2.addView(money);
        
        tl.addView(tr2, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        
        TableRow tr3 = new TableRow(this);
        
        int accessibleCities = InfoCenter.getAccessibleCities().size();
        
        TextView cities = new TextView(this);
        cities.setText("You have access to "+Integer.toString(accessibleCities)+"/37 cities.");
        cities.setTextAppearance(this, android.R.style.TextAppearance_Large);
        cities.setTextColor(Color.WHITE);
        cities.setTypeface(Typeface.SANS_SERIF);
        cities.setGravity(Gravity.CENTER_VERTICAL);
        cities.setPadding(10,10,0,0);
        tr3.addView(cities);
        
        tl.addView(tr3, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
                
        TableRow tr4 = new TableRow(this);  
        
        User temp = InfoCenter.getUser();
        int numCargo = temp.getCargo();
        
        TextView cargo = new TextView(this);
        cargo.setText("You have transported "+Integer.toString(numCargo)+" units of cargo.");
        cargo.setTextAppearance(this, android.R.style.TextAppearance_Large);
        cargo.setTextColor(Color.WHITE);
        cargo.setTypeface(Typeface.SANS_SERIF);
        cargo.setGravity(Gravity.CENTER_VERTICAL);
        cargo.setPadding(10,10,0,0);
        tr4.addView(cargo);
        
        tl.addView(tr4, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        
        TableRow tr5 = new TableRow(this);    
        
        temp = InfoCenter.getUser();
        int numPassengers = temp.getPassengers();
        
        TextView passengers = new TextView(this);
        passengers.setText("You have transported "+Integer.toString(numPassengers)+" people.");
        passengers.setTextAppearance(this, android.R.style.TextAppearance_Large);
        passengers.setTextColor(Color.WHITE);
        passengers.setTypeface(Typeface.SANS_SERIF);
        passengers.setGravity(Gravity.CENTER_VERTICAL);
        passengers.setPadding(10,10,0,0);
        tr5.addView(passengers);
        
        tl.addView(tr5, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
    }
    
    public void backButton(View v)
    {
    	finish(); 
    }
    
    public void setupMediaPlayer()
    {
    	mediaPlayer = MediaPlayer.create(Activity_Statistics.this, R.raw.statistics_music);
    	mediaPlayer.setLooping(true);
    	if(InfoCenter.getAudioStatus())
    	{
        	mediaPlayer.start();
    	}
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer != null && InfoCenter.getAudioStatus())
        	mediaPlayer.pause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(mediaPlayer != null && InfoCenter.getAudioStatus())
    	{
    		mediaPlayer.start();
    	}
    }
}
