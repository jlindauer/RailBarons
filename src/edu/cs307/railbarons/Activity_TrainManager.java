package edu.cs307.railbarons;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Activity_TrainManager extends Activity {
	
	public ScrollView trains, cars, availableCars;
	public int delCar=0;
	public int addCar=0;
	public int trainId=0;
	
    //@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.train_manager);
        
        //sets up the template scroll views containing tablelayouts that will be modified dynamically
        
        this.trains = (ScrollView) findViewById(R.id.trains);
        this.cars = (ScrollView) findViewById(R.id.cars);
        this.cars.setVisibility(View.GONE);
        this.availableCars = (ScrollView) findViewById(R.id.availableCars);
        this.availableCars.setVisibility(View.GONE);

        //calls a function that displays a list of all trains owned by the user
        initTrains();
    }
    
    public void initTrains()
    {
    	ArrayList<Train> trainList = InfoCenter.getTrains();
    	
    	// Get the TableLayout
        TableLayout tl = (TableLayout) findViewById(R.id.trainTable);
        tl.removeAllViews();
    	
        // Go through each item in the ArrayList
        for(Train tempTR: trainList){   	
            // Create a TableRow and give it an ID
            TableRow tr = new TableRow(this);
            
            //set ID and clickListener parameters for the TableRow
            tr.setId(tempTR.getTrainId());
            tr.setClickable(true);
            tr.setOnClickListener(new OnClickListener() {
             	public void onClick(View v) {
             		//handles switching between visible and invisible ScrollViews
             		trains.setVisibility(View.GONE);
             		cars.setVisibility(View.VISIBLE);
             		availableCars.setVisibility(View.GONE);
             		initCars(v.getId());
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
    
    public void initCars(int trainID)
    {
    	ArrayList<Car> carList = InfoCenter.getCar();
    	
        TableLayout tl = (TableLayout) findViewById(R.id.carTable);
        tl.removeAllViews();
        
        trainId=trainID;
    	
        // Go through each item in the ArrayList
        for(Car listEntry: carList){   	
        	if(listEntry.getTrainId()==trainID){
	            // Create a TableRow and give it an ID
	            TableRow tr = new TableRow(this);
	            // tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	                        
	            tr.setId(listEntry.getCarId());
	            tr.setClickable(true);
	            tr.setOnClickListener(new OnClickListener() {
	             	public void onClick(View v) {
	             		
	             		int carId = v.getId();
	             		Car temp = InfoCenter.findCarById(carId);
	             		int carType = temp.getCarType();
	             		
	             		ArrayList<Car> listEntry = InfoCenter.getCar();
	    	        	int count = 0;   
	    	            if(carType<4){
	    	            	ArrayList<Cargo> cargoList = InfoCenter.getCargo();
	    	            	
	    	                for(Cargo var: cargoList){   	
	    	            		if((var.getCarId())==(carId)){
	    	            			count++;
	    	            		}
	    	            	}
	    	            }
	    	            else if(carType>=4){
	    	            	ArrayList<Passenger> passList = InfoCenter.getPassengers();
	    	            	
	    	                for(Passenger var: passList){   	
	    	            		if((var.getPCarId())==(carId)){
	    	            			count++;
	    	            		}
	    	            	}
	    	            }
	    	            
	    	            Train tempVar = InfoCenter.findTrainById(temp.getTrainId());
	    	            
	    	            if(count!=0){
	    	            	popUpWarning();
	    	            }
	    	            else if(count==0&&(tempVar.getDestinationCityId()!=0)){
	    	            	popUpCityWarning();
	    	            }
	    	            else{
	    	            	popUpDelete(v);
	    	            }
	             	}
	               });
	            
	            //the image button for each train's type icon (steam, electric, etc.)
	            ImageView carImg = new ImageView(this);
			    Bitmap bmp=BitmapFactory.decodeResource(getResources(), InfoCenter.getCarSideviewImageId(listEntry.getCarType()));
		    	int width=300;
		    	int height=101;
		        Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
		        carImg.setImageBitmap(resizedbitmap);
	            tr.addView(carImg);
	            
	            //TextView containing the car's type (boxcar, passenger car, etc.)
	            TextView carName = new TextView(this);
	            carName.setPadding(10,0,0,0);
	            int carType = listEntry.getCarType();
	            
	            if(carType==1){
	            	carName.setText("Maglev Cargo");
	            }
	            else if(carType==2){
	            	carName.setText("Steam Cargo");
	            }
	            else if(carType==3){
	            	carName.setText("Diesel Cargo");
	            }
	            else if(carType==4){
	            	carName.setText("Diesel Passenger");
	            }
	            else if(carType==5){
	            	carName.setText("Steam Passenger");
	            }
	            else if(carType==6){
	            	carName.setText("Maglev Passenger");
	            }
	                                                
	            //modifies text formatting and appearance in the TextView
	            carName.setTextAppearance(this, android.R.style.TextAppearance_Large);
	            carName.setTextColor(Color.WHITE);
	            carName.setTypeface(Typeface.SANS_SERIF);
	            carName.setGravity(Gravity.CENTER_VERTICAL);
	            tr.addView(carName);
	            
	            TextView capacity = new TextView(this);
	            capacity.setGravity(Gravity.CENTER_VERTICAL);
	            capacity.setPadding(30,0,0,0);
	            
	            //change conditional statement to differentiate between passenger/cargo cars based on car type
	            //(change text from 5/10 passengers, to x/n cargo)
	            
	        	int count = 0;            
	            if(carType<4){
	            	ArrayList<Cargo> cargoList = InfoCenter.getCargo();
	            	
	                for(Cargo var: cargoList){   	
	            		if((var.getCarId())==(listEntry.getCarId())){
	            			count++;
	            		}
	            	}
	            }
	            else if(carType>=4){
	            	ArrayList<Passenger> passList = InfoCenter.getPassengers();
	            	
	                for(Passenger var: passList){   	
	            		if((var.getPCarId())==(listEntry.getCarId())){
	            			count++;
	            		}
	            	}
	            }
	            
	            String capConcat = Integer.toString(count);
	            capConcat=capConcat.concat("/");
	            
	            if(carType<4){
	            	capConcat=capConcat.concat(Integer.toString(6));
	            	capConcat=capConcat.concat(" Cargo Capacity");
	            }
	            else if(carType>=4){
	            	capConcat=capConcat.concat(Integer.toString(10));
	            	capConcat=capConcat.concat(" Passenger Capacity");
	            }
	            
	            capacity.setText(capConcat);
	            
	            capacity.setTextAppearance(this, android.R.style.TextAppearance_Medium);
	            capacity.setTextColor(Color.WHITE);
	            capacity.setTypeface(Typeface.SANS_SERIF);
	            
	            tr.addView(capacity);
	            
	            //padding is set around the TableRow to make it easier to differentiate between the rows
	            tr.setPadding(0, 10, 0, 10);
	            
	            // Add the TableRow to the TableLayout
	            tl.addView(tr, new TableLayout.LayoutParams(
	                    LayoutParams.FILL_PARENT,
	                    LayoutParams.WRAP_CONTENT));
        	}
        }
    }
    
    public void initAvailCars()
    {
    	// Get the TableLayout
        TableLayout tl = (TableLayout) findViewById(R.id.availableCarTable);
        tl.removeAllViews();
            	
    	ArrayList<Car> carList = InfoCenter.getCar();
    	
    	boolean noneAvailable = false;
    	
    	for(Car findTrain: carList){
    		if(findTrain.getTrainId()==0){
    			noneAvailable=true;
    			break;
    		}
    	}
    	
        if(!noneAvailable){
    		availableCars.setVisibility(View.GONE);
    		cars.setVisibility(View.VISIBLE);
    		trains.setVisibility(View.GONE);
        }
        else{
        	// Go through each item in the ArrayList
        	for(Car listEntry: carList){   	            
	        	if(InfoCenter.findCarById(listEntry.getTrainId())==null){
		        		
		            // Create a TableRow and give it an ID
		            TableRow tr = new TableRow(this);
		            // tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		                        
		            tr.setId(listEntry.getCarId());
		            tr.setClickable(true);
		            tr.setOnClickListener(new OnClickListener() {
		             	public void onClick(View v) {
		             		popUpAdd(v);
		             	}
		               });
		            
		            //the image button for each train's type icon (steam, electric, etc.)
		            ImageView carImg = new ImageView(this);
				    Bitmap bmp=BitmapFactory.decodeResource(getResources(), InfoCenter.getCarSideviewImageId(listEntry.getCarType()));
			    	int width=300;
			    	int height=101;
			        Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
			        carImg.setImageBitmap(resizedbitmap);
		            tr.addView(carImg);
		            
		            //TextView containing the car's type (boxcar, passenger car, etc.)
		            TextView carName = new TextView(this);
		            carName.setPadding(10,0,0,0);
		            int carType = listEntry.getCarType();
		            
		            if(carType==1){
		            	carName.setText("Maglev Cargo");
		            }
		            else if(carType==2){
		            	carName.setText("Steam Cargo");
		            }
		            else if(carType==3){
		            	carName.setText("Diesel Cargo");
		            }
		            else if(carType==4){
		            	carName.setText("Diesel Passenger");
		            }
		            else if(carType==5){
		            	carName.setText("Steam Passenger");
		            }
		            else if(carType==6){
		            	carName.setText("Maglev Passenger");
		            }
		            
		            //modifies text formatting and appearance in the TextView
		            carName.setTextAppearance(this, android.R.style.TextAppearance_Large);
		            carName.setTextColor(Color.WHITE);
		            carName.setTypeface(Typeface.SANS_SERIF);
		            carName.setGravity(Gravity.CENTER_VERTICAL);
		            tr.addView(carName);
		            
		            TextView capacity = new TextView(this);
		            capacity.setGravity(Gravity.CENTER_VERTICAL);
		            capacity.setPadding(30,0,0,0);
		            
		            //change conditional statement to differentiate between passenger/cargo cars based on car type
		            //(change text from 5/10 passengers, to x/n cargo)
		            
		            String capConcat = "Capacity: ";
		            if(carType<4){
		            	capConcat=capConcat.concat(Integer.toString(6));
		            	capConcat=capConcat.concat(" Cargo Capacity");
		            }
		            else if(carType>=4){
		            	capConcat=capConcat.concat(Integer.toString(10));
		            	capConcat=capConcat.concat(" Passenger Capacity");
		            }
		            
		            capacity.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		            capacity.setTextColor(Color.WHITE);
		            capacity.setTypeface(Typeface.SANS_SERIF);
		            
		            tr.addView(capacity);
		            
		            //padding is set around the TableRow to make it easier to differentiate between the rows
		            tr.setPadding(0, 10, 0, 10);
		            
		            // Add the TableRow to the TableLayout
		            tl.addView(tr, new TableLayout.LayoutParams(
		                    LayoutParams.FILL_PARENT,
		                    LayoutParams.WRAP_CONTENT));
	        	}
	        }
        }
    }
    
    public void backButton(View v)
    {
    	
    	//depending on the layout that is currently visible, this set of conditional statements
    	//is responsible for backing through to previous screens
    	
    	if(availableCars.getVisibility() == View.VISIBLE){
    		availableCars.setVisibility(View.GONE);
    		cars.setVisibility(View.VISIBLE);
    	}
    	else if(cars.getVisibility() == View.VISIBLE){
    		initTrains();
    		cars.setVisibility(View.GONE);
    		trains.setVisibility(View.VISIBLE);
    	}
    	else{
    		//closes the current activity and returns to previous activity
    		finish();
    	}
    }
    public void plusButton(View v){
    	//if the plus button is selected and the train is not at maximum capacity for cars, this will bring up
    	//a list of all cars in the user's inventory that can be added to the train
    	
		trains.setVisibility(View.GONE);
		cars.setVisibility(View.GONE);
		availableCars.setVisibility(View.VISIBLE);
    	initAvailCars();
    }
    public void popUpDelete(final View v){
    	
		//this function is responsible for a generating and displaying a pop-up dialog box
    	//responsible for validating whether the user wants to delete a car from the train
    	//it ensures that a user does not accidentally delete a car by selecting it
    	 int start = v.getId();
    	 delCar=start;
		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		 helpBuilder.setTitle("Just Checking...");
		 helpBuilder.setMessage("Are you sure that you want to remove this car?");
		 helpBuilder.setPositiveButton("Yes",
		   new DialogInterface.OnClickListener() {
		 
		    public void onClick(DialogInterface dialog, int which) {
		    	yesClick();
		    	//Car obj = InfoCenter.findCarById(which);
		    	//InfoCenter.delete(obj, BoardLayers.CAR);
		    	//code will be included here to remove the selected car from the train's car list
		    	
		        //TableLayout tl = (TableLayout) findViewById(R.id.carTable);
		        //tl.removeViewAt(v.getId());
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
    public void yesClick(){
    	Car obj = InfoCenter.findCarById(delCar);
    	int t = obj.getTrainId();
    	obj.setTrainId(0);
    	InfoCenter.update(obj, BoardLayers.CAR);
    	initCars(t);
    }
    public void popUpAdd(final View v){
    	
		//this function is responsible for a generating and displaying a pop-up dialog box
    	//responsible for validating whether the user wants to add a car to the train
    	//it ensures that a user does not accidentally add a car by selecting it
    	
		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		 helpBuilder.setTitle("Just Checking...");
		 helpBuilder.setMessage("Are you sure that you want to add this car?");
		 helpBuilder.setPositiveButton("Yes",
		   new DialogInterface.OnClickListener() {
		 
		    public void onClick(DialogInterface dialog, int which) {
		    	
	            ArrayList<Car> carList = InfoCenter.getCar();
	            
	            int count = 0;
	            for(Car c: carList){   	
	            	if((c.getTrainId()==trainId)){
	            		count++;
	            	}
	            }
	            
	            Train obj = InfoCenter.findTrainById(trainId);
	            int save = obj.getTrainType();
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
	            
	            if(count<max){
		    	
	            	Car tempSave = InfoCenter.findCarById(v.getId());
	            	tempSave.setTrainId(trainId);
	            	InfoCenter.update(tempSave, BoardLayers.CAR);
				
					initAvailCars();
					initCars(trainId);
	            }
	            else{
	            	popUpNoAdd();
	            }
				
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
    public void popUpNoAdd(){
    	
		//this function is responsible for a generating and displaying a pop-up dialog box
    	//that displays a message saying that the train is full
		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		 helpBuilder.setTitle("At Capacity!");
		 helpBuilder.setMessage("No more cars can be coupled to this train.");
		 helpBuilder.setPositiveButton("Ok",
		   new DialogInterface.OnClickListener() {
		 
		    public void onClick(DialogInterface dialog, int which) {
		    }
		   });

		 AlertDialog helpDialog = helpBuilder.create();
		 helpDialog.show();
    }
    public void popUpWarning(){
    	
		//this function is responsible for a generating and displaying a pop-up dialog box
    	//that displays a message saying that the train is full
		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		 helpBuilder.setTitle("Not Empty!");
		 helpBuilder.setMessage("You cannot remove a car that is not empty!");
		 helpBuilder.setPositiveButton("Ok",
		   new DialogInterface.OnClickListener() {
		 
		    public void onClick(DialogInterface dialog, int which) {
		    }
		   });

		 AlertDialog helpDialog = helpBuilder.create();
		 helpDialog.show();
    }
    public void popUpCityWarning(){
    	
		//this function is responsible for a generating and displaying a pop-up dialog box
    	//that displays a message saying that the train is full
		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		 helpBuilder.setTitle("In Motion!");
		 helpBuilder.setMessage("You cannot add cars to a train that is in motion!");
		 helpBuilder.setPositiveButton("Ok",
		   new DialogInterface.OnClickListener() {
		 
		    public void onClick(DialogInterface dialog, int which) {
		    }
		   });

		 AlertDialog helpDialog = helpBuilder.create();
		 helpDialog.show();
    }
}