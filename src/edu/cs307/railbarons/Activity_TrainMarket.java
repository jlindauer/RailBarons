package edu.cs307.railbarons;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Activity_TrainMarket extends Activity {
	
	private ImageButton x_button;
	private ScrollView newTrains;
	private RelativeLayout trainView, cityView;
	private int trainChosen;
	//private InfoCenter InfoCenter;
	MediaPlayer mediaPlayer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.train_market);
        this.x_button = (ImageButton) findViewById(R.id.x_button);
        this.trainView = (RelativeLayout) findViewById(R.id.trainView);
        this.cityView = (RelativeLayout) findViewById(R.id.cityView);
        cityView.setVisibility(View.GONE);
        setupMediaPlayer();
        this.newTrains = (ScrollView) findViewById(R.id.newTrains);
        initTrains();
    }
    
    public void backButton(View v)
    {
    	finish(); 
    }
    
    public void initTrains()
    {
    	// Get the TableLayout
        TableLayout tl = (TableLayout) findViewById(R.id.trainTable);
        tl.removeAllViews();
        
        //Load all carTypes
  		String selectQuery = "SELECT * FROM trainTypes";
  		Cursor cursor = InfoCenter.getDbConnection().getDb().rawQuery(selectQuery, null);
  		if(cursor.getCount()>0)
  		{
  	  		//Loop through the cursor and add all rows to the train ArrayList
  			if(cursor.moveToFirst())
  	  		{
  	  			do
  	  			{
  	  				TableRow type1 = new TableRow(this);
  	  		        type1.setId(Integer.parseInt(cursor.getString(0)));
  	  		        type1.setClickable(true);
  	  		        type1.setOnClickListener(new OnClickListener() {
  	  		         	public void onClick(View v) {
  	  		         		if(adequateMoney(v.getId()))
  	  		         			showPopup(v);
  	  		         		else
  	  		         		{
	  	  		         		showWarning(v);
  	  		         		}
  	  		         	}
  	  		           });
  	  		        
  	  		        ImageView view1 = new ImageView(this);
  	  		        Bitmap bmp=BitmapFactory.decodeResource(getResources(), InfoCenter.getTrainImageId(Integer.parseInt(cursor.getString(0))));

  	  		        int width=400;
  	  		        int height=135;
  	  		        Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
  	  		        view1.setImageBitmap(resizedbitmap);
  	  		        //view1.setScaleType(ScaleType.FIT_CENTER);
  	  		        type1.addView(view1);
  	  		        
  	  		        TextView trainName1 = new TextView(this);
  	  		        trainName1.setPadding(10,0,0,0);
  	  		        trainName1.setText(cursor.getString(6));
  	  		        trainName1.setTextAppearance(this, android.R.style.TextAppearance_Large);
  	  		        trainName1.setTextColor(Color.WHITE);
  	  		        trainName1.setTypeface(Typeface.SANS_SERIF);
  	  		        trainName1.setGravity(Gravity.CENTER_VERTICAL);
  	  		        type1.addView(trainName1);
  	  		        
  	  		        TextView price1 = new TextView(this);
  	  		        price1.setGravity(Gravity.CENTER_VERTICAL);
  	  		        price1.setPadding(30,0,0,0);
  	  		        price1.setText("$" + cursor.getString(3));
  	  		        price1.setTextAppearance(this, android.R.style.TextAppearance_Medium);
  	  		        price1.setTextColor(Color.WHITE);
  	  		        price1.setTypeface(Typeface.SANS_SERIF);
  	  		        type1.addView(price1);
  	  		        
  	  		        tl.addView(type1);

  	  			}
  	  			while (cursor.moveToNext());
  	  		}
  		}
  		InfoCenter.getDbConnection().close();
    }
    
    public void showPopup(View v)
    {
    	final int trainType = v.getId();
		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		 helpBuilder.setTitle("Just Checking...");
		 helpBuilder.setMessage("Are you sure that you want to buy this train?");
		 helpBuilder.setPositiveButton("Yes",
		   new DialogInterface.OnClickListener() {
		 
		    public void onClick(DialogInterface dialog, int which) {
		    	trainView.setVisibility(View.GONE);
		    	cityView.setVisibility(View.VISIBLE);
		    	trainChosen = trainType;
		    	initCityList();
		    }
		   });
		 helpBuilder.setNegativeButton("No",
			new DialogInterface.OnClickListener() {
 		    public void onClick(DialogInterface dialog, int which) {
 		    	//no functionality should be placed here
 		    	//the system should ignore a "No" request, since no car will be removed from the train     		    
 		    }
 		   });

		 AlertDialog helpDialog = helpBuilder.create();
		 helpDialog.show();
    }
    
    public void showWarning(View v)
    {
    	AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
 		helpBuilder.setTitle("We regret to inform you...");
 		helpBuilder.setMessage("You don't seem to have enough funds available");
 		helpBuilder.setNegativeButton("Ok",
 		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	//no functionality should be placed here
		    	//the system should ignore a "No" request, since no car will be removed from the train     		    
		    }
 		});

 		AlertDialog helpDialog = helpBuilder.create();
 		helpDialog.show();
    }
    
    public void initCityList()
    {
    	// Get the TableLayout
        TableLayout tl = (TableLayout) findViewById(R.id.cityTable);
        tl.removeAllViews();
        
        //Load all carTypes
        ArrayList<City> activeCities = InfoCenter.getAccessibleCities();
        for(final City city : activeCities)
        {
        	TableRow type1 = new TableRow(this);
        	type1.setId(city.getCityId());
	        type1.setClickable(true);
	        type1.setOnClickListener(new OnClickListener() {
	         	public void onClick(View v) {
	         		//Create train and finish activity
	         		InfoCenter.createTrain(BoardLayers.TRAIN, trainChosen, city.getCityId(), 0, 0, 0L);
	         		int money = 0;
	         		switch(trainChosen)
	         		{
	         		case 1:
	         			money = 5000;
	         			break;
	         		case 2:
	         			money = 12000;
	         			break;
	         		case 3:
	         			money = 20000;
	         			break;
	         		}
	         		InfoCenter.removeMoney(money);
	         		backButton(v);
	         	}
            });
	        
	        TextView cityName = new TextView(this);
	        cityName.setPadding(10,0,0,0);
	        cityName.setText(city.getCityName());
	        cityName.setTextAppearance(this, android.R.style.TextAppearance_Large);
	        cityName.setTextColor(Color.WHITE);
	        cityName.setTypeface(Typeface.SANS_SERIF);
	        cityName.setGravity(Gravity.CENTER_VERTICAL);
	        type1.addView(cityName);
	        
	        tl.addView(type1);
        }
    }
    
    public void setupMediaPlayer()
    {
    	mediaPlayer = MediaPlayer.create(Activity_TrainMarket.this, R.raw.submenu_background);
    	mediaPlayer.setLooping(true);
    	if(InfoCenter.getAudioStatus())
    	{
        	mediaPlayer.start();
    	}
    }
    
    public boolean adequateMoney(int trainType)
    {
    	User user = InfoCenter.getUser();
    	int money = user.getMoney();
    	int price = 0;
    	switch(trainType)
    	{
    	case 1:
    		price = 5000;
    		break;
    	case 2:
    		price = 12000;
    		break;
    	case 3: 
    		price = 20000;
    		break;
    	}
    	
    	if(price > money)
    		return false;
    	else
    		return true;
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
