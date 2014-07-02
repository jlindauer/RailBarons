package edu.cs307.railbarons;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
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
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Activity_CarMarket extends Activity {
	
	private ImageButton x_button;
	private ScrollView newCars, trains;
	//private InfoCenter InfoCenter;
	MediaPlayer mediaPlayer;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.car_market);
        //InfoCenter = new InfoCenter();
        setupMediaPlayer();
        this.x_button = (ImageButton) findViewById(R.id.x_button);
        this.newCars = (ScrollView) findViewById(R.id.newCars);
        this.trains = (ScrollView) findViewById(R.id.trains);
        initCars();
    }
    
    public void initCars()
    {
    	newCars.setVisibility(View.VISIBLE);
    	trains.setVisibility(View.GONE);
    	// Get the TableLayout
        TableLayout tl = (TableLayout) findViewById(R.id.carTable);
        tl.removeAllViews();
        
        /*
         * The way this will work eventually
         * InfoCenter returns list of all of the current car types (this will have to have been initialized upon first launch somehow)
         * Item created in table for each of these
         * When item selected, a new entry of said type will be made in table car
         * This car ID is then used to associate it to a given train
         */
        
        //Load all carTypes
  		String selectQuery = "SELECT * FROM carTypes";
  		Cursor cursor = InfoCenter.getDbConnection().getDb().rawQuery(selectQuery, null);
  		//InfoCenter.getDbConnection().close();
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
  	  		         		popUpAdd(v.getId());
  	  		         	}
  	  		           });
  	  		        
  	  		        ImageView view1 = new ImageView(this);
  	  		        Bitmap bmp=BitmapFactory.decodeResource(getResources(), InfoCenter.getCarSideviewImageId(Integer.parseInt(cursor.getString(0))));

  	  		        int width=400;
  	  		        int height=135;
  	  		        Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
  	  		        view1.setImageBitmap(resizedbitmap);
  	  		        //view1.setScaleType(ScaleType.FIT_CENTER);
  	  		        type1.addView(view1);
  	  		        
  	  		        TextView carName1 = new TextView(this);
  	  		        carName1.setPadding(10,0,0,0);
  	  		        carName1.setText(cursor.getString(3));
  	  		        carName1.setTextAppearance(this, android.R.style.TextAppearance_Large);
  	  		        carName1.setTextColor(Color.WHITE);
  	  		        carName1.setTypeface(Typeface.SANS_SERIF);
  	  		        carName1.setGravity(Gravity.CENTER_VERTICAL);
  	  		        type1.addView(carName1);
  	  		        
  	  		        TextView price1 = new TextView(this);
  	  		        price1.setGravity(Gravity.CENTER_VERTICAL);
  	  		        price1.setPadding(30,0,0,0);
  	  		        price1.setText("$" + cursor.getString(4));
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
    
    public void initTrains(final int carType,final int carId)
    {
    	newCars.setVisibility(View.GONE);
    	trains.setVisibility(View.VISIBLE);

    	ArrayList<Train> trainList = InfoCenter.getTrains();
    	
    	// Get the TableLayout
        TableLayout tl = (TableLayout) findViewById(R.id.trainTable);
        tl.removeAllViews();
    	
        // Go through each item in the ArrayList
        for(Train tempTR: trainList){
        	
            ArrayList<Car> carList = InfoCenter.getCar();
            
            int count = 0;
            for(Car c: carList){
            	if((c.getTrainId())==tempTR.getTrainId()){
            		count++;
            	}
            }
            
            String numCars = Integer.toString(count)+"/";
            int save = tempTR.getTrainType();
            int max=0;
                        
            if(save==1){
            	max=3;
            }
            else if(save==2){
            	max=8;
            }
            else if(save==3){
            	max=10;            	
            }
            
        	if((tempTR.getDestinationCityId()==0)&&(count<max)){
	            // Create a TableRow and give it an ID
	            TableRow tr = new TableRow(this);
	            
	            //set ID and clickListener parameters for the TableRow
	            tr.setId(tempTR.getTrainId());
	            tr.setClickable(true);
	            tr.setOnClickListener(new OnClickListener() {
	             	public void onClick(View v) {
	             		//handles switching between visible and invisible ScrollViews
	             		secondPurchaseCheck(v.getId(),carType,carId);
	             	}
	               });
	            
	            //the image button for each train's type icon (steam, electric, etc.)
	            ImageView trainImg = new ImageView(this);
			    Bitmap bmp=BitmapFactory.decodeResource(getResources(), InfoCenter.getTrainImageId(tempTR.getTrainType()));
		    	int width=300;
		    	int height=101;
		        Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
		        trainImg.setImageBitmap(resizedbitmap);
	            tr.addView(trainImg);
	
	            //TextView containing the train's type (steam, electric, etc.)
	            TextView trainName = new TextView(this);
	            trainName.setPadding(10,0,0,0);
	            int num = tempTR.getTrainType();
	            String train = null;
	            
	            if(num==1){
	            	train="Steam Engine";
	            }
	            else if(num==2){
	            	train="Diesel Engine";
	            }
	            else if(num==3){
	            	train="Maglev Engine";
	            }
	            
	            if(trainName!=null){
	            	trainName.setText(train);
	            }
	
	            //modifies text formatting and appearance in the TextView
	            trainName.setTextAppearance(this, android.R.style.TextAppearance_Large);
	            trainName.setTextColor(Color.WHITE);
	            trainName.setTypeface(Typeface.SANS_SERIF);
	            trainName.setGravity(Gravity.CENTER_VERTICAL);
	            tr.addView(trainName);
	            
	            //TableLayout that contains TextViews for train destination and car capacity
	            //These TextViews are each a row in the TableLayout, making it easier to format
	            //the TextView implementation and formatting are virtually the same as for train type
	            
	            TableLayout sub = new TableLayout(this);
	            sub.setGravity(Gravity.CENTER_VERTICAL);
	            
	            TextView dest = new TextView(this);
	            dest.setGravity(Gravity.CENTER_VERTICAL);
	            dest.setPadding(30,0,0,0);
	            
	            String printVal = "Destination: ";
	            int cityID = tempTR.getDestinationCityId();
	           
	            if(cityID==0){
	            	printVal="Destination: ARRIVED";
	            	int city = tempTR.getCurrentCityId();
	            	City findName = InfoCenter.findCityById(city);
	            	String cityName = findName.getCityName();
	            	cityName=cityName.toUpperCase();
	            	printVal = printVal.concat(" IN "+cityName);
	            }
	            else{
	            	String city = (InfoCenter.findCityById(cityID)).getCityName();
	            	printVal=printVal.concat(city);
	            }
	            
	        	dest.setText(printVal);
	            dest.setTextAppearance(this, android.R.style.TextAppearance_Medium);
	            dest.setTextColor(Color.WHITE);
	            dest.setTypeface(Typeface.SANS_SERIF);
	            
	            TextView cars = new TextView(this);
	            cars.setGravity(Gravity.CENTER_VERTICAL);
	            cars.setPadding(30,0,0,0);
	
	            carList = InfoCenter.getCar();
	            
	            count = 0;
	            for(Car c: carList){   	
	            	if((c.getTrainId())==tempTR.getTrainId()){
	            		count++;
	            	}
	            }
	            
	            numCars = Integer.toString(count)+"/";
	            save = tempTR.getTrainType();
	            max=0;
	                        
	            if(save==1){
	            	max=3;
	            }
	            else if(save==2){
	            	max=8;
	            }
	            else if(save==3){
	            	max=10;            	
	            }
	            
	            numCars=numCars.concat(Integer.toString(max)+" Cars Coupled");
	            
	            cars.setText(numCars);
	            cars.setTextAppearance(this, android.R.style.TextAppearance_Medium);
	            cars.setTextColor(Color.WHITE);
	            cars.setTypeface(Typeface.SANS_SERIF);
	            
	            sub.addView(dest);
	            sub.addView(cars);
	            
	            tr.addView(sub);
	            
	            //padding is set around the TableRow to make it easier to differentiate between the rows
	            tr.setPadding(0, 10, 0, 10);
	            
	            // Add the TableRow to the TableLayout
	            tl.addView(tr, new TableLayout.LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT));
        	}
        }
    	
    }
    
    public void popUpAdd(final int carType){
    	
		//this function is responsible for a generating and displaying a pop-up dialog box
    	//responsible for validating whether the user wants to add a car to the train
    	//it ensures that a user does not accidentally add a car by selecting it
    	
    	int money = InfoCenter.getMoney();
    	int cost = 0;
    	if(carType==1){
    		cost=7000;
    	}
    	else if(carType==2){
    		cost=4000;
    	}
    	else if(carType==3){
    		cost=6000;
    	}
    	else if(carType==4){
    		cost=6000;
    	}
    	else if(carType==5){
    		cost=4500;
    	}
    	else if(carType==6){
    		cost=10000;
    	}
    	
    	if(cost<=money){
			 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
			 helpBuilder.setTitle("Just Checking...");
			 helpBuilder.setMessage("Are you sure that you want to buy this car?");
			 helpBuilder.setPositiveButton("Yes",
			   new DialogInterface.OnClickListener() {		 
			    public void onClick(DialogInterface dialog, int which) {
			    	Car temp = InfoCenter.createCar(BoardLayers.CAR, carType, 0);
		         	initTrains(carType,temp.getCarId());
		         	int cost=0;
		         	
	         		switch(carType)
	         		{
	         		case 1:
	         			cost = 6000;
	         			break;
	         		case 2:
	         			cost = 4000;
	         			break;
	         		case 3:
	         			cost = 7000;
	         			break;
	         		case 4:
	         			cost = 6000;
	         			break;
	         		case 5:
	         			cost = 4500;
	         			break;
	         		case 6:
	         			cost = 10000;
	         			break;
	         		}
		         	
		         	InfoCenter.removeMoney(cost);
		           	//code will be included here to add the selected car to the train's car list
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
    	else{
    		//this function is responsible for a generating and displaying a pop-up dialog box
        	//that displays a message saying that the train is full
    		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
    		 helpBuilder.setTitle("We regret to inform you...");
    		 helpBuilder.setMessage("You don't seem to have enough funds available");
    		 helpBuilder.setPositiveButton("Ok",
    		   new DialogInterface.OnClickListener() {
    		 
    		    public void onClick(DialogInterface dialog, int which) {
    		    }
    		   });

    		 AlertDialog helpDialog = helpBuilder.create();
    		 helpDialog.show();
    	}
    }
    
    public void secondPurchaseCheck(final int trainId, final int carType, final int carId){
    	
		//this function is responsible for a generating and displaying a pop-up dialog box
    	//responsible for validating whether the user wants to add a car to the train
    	//it ensures that a user does not accidentally add a car by selecting it
    	
		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		 helpBuilder.setTitle("Just Checking...");
		 helpBuilder.setMessage("Are you sure you want to add the car to this train?");
		 helpBuilder.setPositiveButton("Yes",
		   new DialogInterface.OnClickListener() {		 
		    public void onClick(DialogInterface dialog, int which) {
		    	ArrayList<Car> carList = InfoCenter.getCar();
		    	Car hold=null;
		    	
	        	for(Car listEntry: carList){
	        		if(listEntry.getCarId()==carId){
	        			listEntry.setTrainId(trainId);
	        			hold=listEntry;
	        		}
	        	}
		    	
         		InfoCenter.update(hold, BoardLayers.CAR);
         		
         		newCars.setVisibility(View.VISIBLE);
         		trains.setVisibility(View.GONE);
	           	//code will be included here to add the selected car to the train's car list
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
    
    public void backButton(View v)
    {
    	if(newCars.getVisibility() == View.VISIBLE)
    	{
    		finish();
    	}
    	else
    	{
    		initCars();
    	}
    }
    
    public void setupMediaPlayer()
    {
    	mediaPlayer = MediaPlayer.create(Activity_CarMarket.this, R.raw.submenu_background);
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
