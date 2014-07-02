package edu.cs307.railbarons;

import java.util.ArrayList;
import java.util.Random;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Activity_Station extends Activity {
    private MediaPlayer mediaPlayer;
    private ImageButton left, right;
    private ImageView trainView;
    private RelativeLayout mainView, addPassenger, addCargo, carLayout, pickTrain;
    private int cityId;
    private ArrayList<Train> trainsInCity;
    private ArrayList<Car> cars;
    private int carIndex;
    private int chosenTrain = 0;
    private ImageView carItem1, carItem2, carItem3, carItem4, carItem5, carItem6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.station);
        this.left = (ImageButton) findViewById(R.id.left);
        this.right = (ImageButton) findViewById(R.id.right);
        this.trainView = (ImageView) findViewById(R.id.trainView);
        this.mainView = (RelativeLayout) findViewById(R.id.mainView);
        this.addPassenger = (RelativeLayout) findViewById(R.id.addPassengers);
        this.addCargo = (RelativeLayout) findViewById(R.id.addCargo);
        this.carLayout = (RelativeLayout) findViewById(R.id.carLayout);
        this.pickTrain = (RelativeLayout) findViewById(R.id.pickTrain);
        this.carItem1 = (ImageView) findViewById(R.id.carItem1);
        this.carItem2 = (ImageView) findViewById(R.id.carItem2);
        this.carItem3 = (ImageView) findViewById(R.id.carItem3);
        this.carItem4 = (ImageView) findViewById(R.id.carItem4);
        this.carItem5 = (ImageView) findViewById(R.id.carItem5);
        this.carItem6 = (ImageView) findViewById(R.id.carItem6);
        carItem1.setVisibility(View.INVISIBLE);
    	carItem2.setVisibility(View.INVISIBLE);
    	carItem3.setVisibility(View.INVISIBLE);
    	carItem4.setVisibility(View.INVISIBLE);
    	carItem5.setVisibility(View.INVISIBLE);
    	carItem6.setVisibility(View.INVISIBLE);
    	left.setVisibility(View.INVISIBLE);
    	right.setVisibility(View.INVISIBLE);
        
        //Get the passed in cityId
        Intent myIntent = getIntent();
        cityId = myIntent.getIntExtra("cityId", 0);
        
        //Make mainView only one visible
        addPassenger.setVisibility(View.GONE);
        addCargo.setVisibility(View.GONE);
        pickTrain.setVisibility(View.GONE);
        
        //Check if city is accessible yet
        City city = InfoCenter.findCityById(cityId);
        if(!city.isAccessible())
        {
        	AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
     		helpBuilder.setTitle("City Inaccessible");
     		helpBuilder.setMessage("You have not yet added this city to your rail network");
     		helpBuilder.setNegativeButton("Ok",
     		new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int which) {
    		    	//This will close out of the station view
    		    	finish();
    		    }
     		});

     		AlertDialog helpDialog = helpBuilder.create();
     		helpDialog.show();
        }
        
        trainsInCity = InfoCenter.numberOfTrainsInCity(cityId);
        if(trainsInCity.size() >= 2){
                pickTrain();
        }
        else if(trainsInCity.size() == 1)
        {
                chosenTrain = (trainsInCity.get(0)).getTrainId();
                getCars(chosenTrain);
                createPassengers();
                createCargo();
        }
        setupMediaPlayer();
    }
    
    public void pickTrain(){
    	addPassenger.setVisibility(View.GONE);
        addCargo.setVisibility(View.GONE);
        mainView.setVisibility(View.GONE);
        pickTrain.setVisibility(View.VISIBLE);
        
        TableLayout tl = (TableLayout) findViewById(R.id.trainTable);
        tl.removeAllViews();
        
        for(Train train : trainsInCity)
        {
        	TableRow type1 = new TableRow(this);
	        type1.setId(train.getTrainId());
	        type1.setClickable(true);
	        type1.setOnClickListener(new OnClickListener() {
	         	public void onClick(View v) {
	         		chosenTrain = v.getId();
	                getCars(chosenTrain);
	                createPassengers();
	                createCargo();
	         		closeSelector();
	         	}
            });
	        
	        ImageView view1 = new ImageView(this);
	        Bitmap bmp=BitmapFactory.decodeResource(getResources(), InfoCenter.getTrainImageId(train.getTrainType()));

	        int width=400;
	        int height=135;
	        Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
	        view1.setImageBitmap(resizedbitmap);
	        type1.addView(view1);
	        
	        TextView trainName1 = new TextView(this);
	        trainName1.setPadding(10,0,0,0);
	        switch(train.getTrainType())
	        {
	        case 1:
	        	trainName1.setText("Steam Engine");
	        	break;
	        case 2:
	        	trainName1.setText("Diesel Engine");
	        	break;
	        case 3:
	        	trainName1.setText("Maglev Train");
	        	break;
	        }
	        trainName1.setTextAppearance(this, android.R.style.TextAppearance_Large);
	        trainName1.setTextColor(Color.WHITE);
	        trainName1.setTypeface(Typeface.SANS_SERIF);
	        trainName1.setGravity(Gravity.CENTER_VERTICAL);
	        type1.addView(trainName1);
	        
	        tl.addView(type1);
        }
    }
    
    public void createCargo()
    {
        City thisCity = InfoCenter.findCityById(cityId);
        ArrayList<City> accessibleCities = InfoCenter.getAccessibleCities();
        accessibleCities.remove(thisCity);
        int numToGenerate = 3*thisCity.getCityLevel();
        Random randomGenerator = new Random();
        for(int i = 0; i < numToGenerate; i++)
        {       
                int cargoType = randomGenerator.nextInt(3) + 1;
                int destinationCityId = (accessibleCities.get((randomGenerator.nextInt(accessibleCities.size())))).getCityId();
                
                City destCity = InfoCenter.findCityById(destinationCityId);
                double crowsDistance = Math.sqrt(Math.pow((destCity.getPosX() - thisCity.getPosX()), 2) + Math.pow((destCity.getPosY() - thisCity.getPosY()), 2));
                int price = 0;
                switch(cargoType){
                case 1:
                	price = (int) ((Math.round(crowsDistance))/2);
                    break;
                case 2:
                	price = (int) ((Math.round(crowsDistance)));
                    break;
                case 3:
                	price = (int) ((Math.round(crowsDistance))*2);
                    break;  
                }
                
                //bases the price on the type of the train, the number of cars, and the speed
                Train train = InfoCenter.findTrainById(chosenTrain);
                price*=train.getTrainType();
                double multFactor = 1;
                switch(cars.size())
                {
                case 1:
                	multFactor = 0.98;
                	break;
                case 2:
                	multFactor = 0.96;
                	break;
                case 3:
                	multFactor = 0.94;
                	break;
                case 4:
                	multFactor = 0.92;
                	break;
                case 5:
                	multFactor = 0.90;
                	break;
                case 6:
                	multFactor = 0.88;
                	break;
                case 7:
                	multFactor = 0.86;
                	break;
                case 8:
                	multFactor = 0.84;
                	break;
                case 9:
                	multFactor = 0.82;
                	break;
                case 10:
                	multFactor = 0.80;
                	break;
            	default:
            		multFactor = 1;
                }
                
                price = (int) (Math.round(price*multFactor));
                
                InfoCenter.createCargo(BoardLayers.CARGO, cargoType, 0, 0, destinationCityId, price);
        }
    }
    
    public void createPassengers()
    {
        String [] lastNames = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Carter", "Walker", "Lee", "Lewis"};
        String [] fFirstName = {"Sophia", "Isabella", "Emma", "Olivia", "Ava", "Emily", "Abigail", "Madison", "Mia", "Chloe", "Elizabeth", "Ella", "Addison", "Natalie", "Lily", "Grace", "Samantha", "Avery", "Sofia", "Aubrey", "Brooklyn", "Lillian", "Victoria", "Evelyn", "Hannah"};
        String [] mFirstName = {"Jacob", "Mason", "William", "Jayden", "Noah", "Michael", "Ethan", "Alexander", "Aiden", "Daniel", "Anthony", "Matthew", "Elijah", "Joshua", "Liam", "Andrew", "James", "David", "Benjamin", "Logan", "Christopher", "Joseph", "Jackson", "Gabriel", "Ryan"};
        
        City thisCity = InfoCenter.findCityById(cityId);
        ArrayList<City> accessibleCities = InfoCenter.getAccessibleCities();
        accessibleCities.remove(thisCity);
        int numToGenerate = 3*thisCity.getCityLevel();
        Random randomGenerator = new Random();
        for(int i = 0; i < numToGenerate; i++)
        {       
                int lastNameNum = randomGenerator.nextInt(lastNames.length);
                String chosenLastName = lastNames[lastNameNum];
                String chosenFirstName;
                int imageId = 0;
                int gender = randomGenerator.nextInt(2);
                if(gender == 1)
                {
                        int firstNameNum = randomGenerator.nextInt(fFirstName.length);
                        chosenFirstName = fFirstName[firstNameNum];
                        
                        //Female Images: 4
                        int girlImage = randomGenerator.nextInt(4);
                        switch(girlImage) {
                        case 0: imageId = R.drawable.blue_dress_girl_front;
                                break;
                        case 1: imageId = R.drawable.girl_front;
                                break;
                        case 2: imageId = R.drawable.pink_dress_girl_front;
                                break;
                        case 3: imageId = R.drawable.poor_girl_front;
                                break;
                        }
                }
                else
                {
                        int firstNameNum = randomGenerator.nextInt(mFirstName.length);
                        chosenFirstName = mFirstName[firstNameNum];
                        
                        //Male Images: 6
                        int boyImage = randomGenerator.nextInt(6);
                        switch(boyImage) {
                        case 0: imageId = R.drawable.boy_front;
                                break;
                        case 1: imageId = R.drawable.cowboy_front;
                                break;
                        case 2: imageId = R.drawable.doctor_front;
                                break;
                        case 3: imageId = R.drawable.rich_dude_front;
                                break;
                        case 4: imageId = R.drawable.shop_owner_front;
                                break;
                        case 5: imageId = R.drawable.top_hat_front;
                                break;
                        }
                }
                
                chosenFirstName = chosenFirstName.concat(" ");
                chosenFirstName = chosenFirstName.concat(chosenLastName);
                
                int destinationCityId = (accessibleCities.get((randomGenerator.nextInt(accessibleCities.size())))).getCityId();

                City destCity = InfoCenter.findCityById(destinationCityId);
                double crowsDistance = Math.sqrt(Math.pow((destCity.getPosX() - thisCity.getPosX()), 2) + Math.pow((destCity.getPosY() - thisCity.getPosY()), 2));
                int price = (int) (Math.round(crowsDistance));
                //bases the price on the type of the train, the number of cars, and the speed
                Train train = InfoCenter.findTrainById(chosenTrain);
                price*=train.getTrainType();
                double multFactor = 1;
                switch(cars.size())
                {
                case 1:
                	multFactor = 0.98;
                	break;
                case 2:
                	multFactor = 0.96;
                	break;
                case 3:
                	multFactor = 0.94;
                	break;
                case 4:
                	multFactor = 0.92;
                	break;
                case 5:
                	multFactor = 0.90;
                	break;
                case 6:
                	multFactor = 0.88;
                	break;
                case 7:
                	multFactor = 0.86;
                	break;
                case 8:
                	multFactor = 0.84;
                	break;
                case 9:
                	multFactor = 0.82;
                	break;
                case 10:
                	multFactor = 0.80;
                	break;
            	default:
            		multFactor = 1;
                }
                
                price = (int) (Math.round(price*multFactor));
                
                
                InfoCenter.createPassenger(BoardLayers.PASSENGER, imageId, 0, 0, chosenFirstName, destinationCityId, price);
        }
    }
    
    public void getCars(int trainId)
    {
        cars = InfoCenter.carsOnTrain(trainId);
        carIndex = 0;
        drawCar();
    }
    
    public void drawCar()
    {
    	carItem1.setVisibility(View.INVISIBLE);
    	carItem2.setVisibility(View.INVISIBLE);
    	carItem3.setVisibility(View.INVISIBLE);
    	carItem4.setVisibility(View.INVISIBLE);
    	carItem5.setVisibility(View.INVISIBLE);
    	carItem6.setVisibility(View.INVISIBLE);
    	
    	if(cars.size() != 0)
    	{
    		Car car = cars.get(carIndex);
            int carType = car.getCarType();
            trainView.setImageResource(InfoCenter.getCarImageId(carType));
            if(carType == 1 || carType == 2 || carType == 3)
            {
                    ArrayList<Cargo> cargo = InfoCenter.cargoInCar(car.getCarId());
                    for(int i = 0; i < cargo.size(); i ++)
                    {
                    	switch(i)
                    	{
                    	case 0:
                    		carItem1.setImageResource(R.drawable.cargo);
                    		carItem1.setVisibility(View.VISIBLE);
                    		break;
                    	case 1:
                    		carItem2.setImageResource(R.drawable.cargo);
                    		carItem2.setVisibility(View.VISIBLE);
                    		break;
                    	case 2:
                    		carItem3.setImageResource(R.drawable.cargo);
                    		carItem3.setVisibility(View.VISIBLE);
                    		break;
                    	case 3:
                    		carItem4.setImageResource(R.drawable.cargo);
                    		carItem4.setVisibility(View.VISIBLE);
                    		break;
                    	case 4:
                    		carItem5.setImageResource(R.drawable.cargo);
                    		carItem5.setVisibility(View.VISIBLE);
                    		break;
                    	case 5:
                    		carItem6.setImageResource(R.drawable.cargo);
                    		carItem6.setVisibility(View.VISIBLE);
                    		break;
                    	}
                    }
                    
            }
            else
            {
                    ArrayList<Passenger> passengers = InfoCenter.passengersInCar(car.getCarId());
                    for(int i = 0; i < passengers.size(); i ++)
                    {
                    	switch(i)
                    	{
                    	case 0:
                    		carItem1.setImageResource((passengers.get(i).getImageId()));
                    		carItem1.setVisibility(View.VISIBLE);
                    		break;
                    	case 1:
                    		carItem2.setImageResource((passengers.get(i).getImageId()));
                    		carItem2.setVisibility(View.VISIBLE);
                    		break;
                    	case 2:
                    		carItem3.setImageResource((passengers.get(i).getImageId()));
                    		carItem3.setVisibility(View.VISIBLE);
                    		break;
                    	case 3:
                    		carItem4.setImageResource((passengers.get(i).getImageId()));
                    		carItem4.setVisibility(View.VISIBLE);
                    		break;
                    	case 4:
                    		carItem5.setImageResource((passengers.get(i).getImageId()));
                    		carItem5.setVisibility(View.VISIBLE);
                    		break;
                    	case 5:
                    		carItem6.setImageResource((passengers.get(i).getImageId()));
                    		carItem6.setVisibility(View.VISIBLE);
                    		break;
                    	}
                    }
            }
            
            if(carIndex == 0)
            {
            	left.setVisibility(View.INVISIBLE);
            	if(cars.size() == 1)
            		right.setVisibility(View.INVISIBLE);
            	else
            		right.setVisibility(View.VISIBLE);
            }
            else if(carIndex == (cars.size() - 1))
            {
            	right.setVisibility(View.INVISIBLE);
            	if(cars.size() == 1)
            		left.setVisibility(View.INVISIBLE);
            	else
            		left.setVisibility(View.VISIBLE);
            }
            else
            {
            	left.setVisibility(View.VISIBLE);
            	right.setVisibility(View.VISIBLE);
            }
    	}
    	else
    	{
    		left.setVisibility(View.INVISIBLE);
    		right.setVisibility(View.INVISIBLE);
    	}
    	
    	
    }
    
    public void backButton(View v)
    {
        InfoCenter.removeStationPassengers();
        InfoCenter.removeStationCargo();
        finish();
    }
    
    public void pickCargo(View v)
    {
        addPassenger.setVisibility(View.GONE);
        addCargo.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.GONE);
        pickTrain.setVisibility(View.GONE);
        
        TableLayout tl = (TableLayout) findViewById(R.id.cargoTable);
        tl.removeAllViews();
        
        ArrayList<Cargo> allCargo = InfoCenter.getCargo();
        for(Cargo myCargo : allCargo)
        {
                if(myCargo.getCarId() == 0)
                {
                        TableRow type1 = new TableRow(this);
                        type1.setId(myCargo.getCargoId());
                        type1.setClickable(true);
                        type1.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                	Car car;
                            		if(cars.size() != 0)
                            		{
                            			car = cars.get(carIndex);
                            			int carType = car.getCarType();
                            			if(carType == 1 || carType == 2 || carType == 3)
                            			{
                            				if(InfoCenter.getNumCarItems((cars.get(carIndex)).getCarId()) != 6)
                                    		{
        	                                	int cargoId = v.getId();
        	                                    Cargo cargo = InfoCenter.findCargoById(cargoId);
        	                                    InfoCenter.addMoney(cargo.getPrice());
        	                                    cargo.setCarId((cars.get(carIndex)).getCarId());
        	                                    InfoCenter.update(cargo, BoardLayers.CARGO);
        	                                    User temp = InfoCenter.getUser();
            	                                temp.addCargo();
        	                                    pickCargo(v);
                                    		}
                            				else
                            				{
                            					carFullPopup();
                            				}
                            			}
                            			else
                            			{
                            				wrongCarPopup();
                            			}
                            		}
                            		else
                            		{
                            			noCarsPopup();
                            		}
                                }
                           });
                        
                        ImageView view1 = new ImageView(this);
                        Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.cargo);
                        int width=120;
                        int height=80;
                        Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
                        view1.setImageBitmap(resizedbitmap);
                        //view1.setScaleType(ScaleType.FIT_CENTER);
                        type1.addView(view1);
                                
                        TextView cargoName = new TextView(this);
                        cargoName.setPadding(10,0,0,0);
                        int cargoType = myCargo.getCargoType();
                        switch(cargoType){
                        case 1: cargoName.setText("Hay");
                                break;
                        case 2: cargoName.setText("Wood");
                                break;
                        case 3: cargoName.setText("Steel");
                                break;
                        }
                        cargoName.setTextAppearance(this, android.R.style.TextAppearance_Large);
                        cargoName.setTextColor(Color.WHITE);
                                cargoName.setTypeface(Typeface.SANS_SERIF);
                                cargoName.setGravity(Gravity.CENTER_VERTICAL);
                        type1.addView(cargoName);
                        
                        TextView destinationCity = new TextView(this);
                        int destCityId = myCargo.getDestinationCityId();
                        City destCity = InfoCenter.findCityById(destCityId);
                        String cityName = destCity.getCityName();
                        destinationCity.setPadding(10,0,0,0);
                        destinationCity.setText("Destination: " + cityName);
                        destinationCity.setTextAppearance(this, android.R.style.TextAppearance_Large);
                        destinationCity.setTextColor(Color.WHITE);
                        destinationCity.setTypeface(Typeface.SANS_SERIF);
                        destinationCity.setGravity(Gravity.CENTER_VERTICAL);
                        type1.addView(destinationCity);
                        
                        TextView price = new TextView(this);
                        price.setText("$" + myCargo.getPrice());
                        price.setPadding(10,0,0,0);
                        price.setTextAppearance(this, android.R.style.TextAppearance_Large);
                        price.setTextColor(Color.WHITE);
                        price.setTypeface(Typeface.SANS_SERIF);
                        price.setGravity(Gravity.CENTER_VERTICAL);
                        type1.addView(price);
                        
                        tl.addView(type1);
                }
        }
    }
    
    public void pickPassengers(View v)
    {
        addPassenger.setVisibility(View.VISIBLE);
        addCargo.setVisibility(View.GONE);
        mainView.setVisibility(View.GONE);
        pickTrain.setVisibility(View.GONE);
        
        TableLayout tl = (TableLayout) findViewById(R.id.passengerTable);
        tl.removeAllViews();
        
        ArrayList<Passenger> allPassengers = InfoCenter.getPassengers();
        for(Passenger myPassenger : allPassengers)
        {
                if(myPassenger.getPCarId() == 0)
                {
                        TableRow type1 = new TableRow(this);
                        type1.setId(myPassenger.getPassengerId());
                        type1.setClickable(true);
                        type1.setOnClickListener(new OnClickListener() {
	                        public void onClick(View v) {
	                    		Car car;
                        		if(cars.size() != 0)
                        		{
                        			car = cars.get(carIndex);
                        			int carType = car.getCarType();
                        			if(carType == 4 || carType == 5 || carType == 6)
                        			{
                        				if(InfoCenter.getNumCarItems((cars.get(carIndex)).getCarId()) != 6)
                                		{
                        					int pId = v.getId();
        	                                Passenger passenger = InfoCenter.findPassengerById(pId);
        	                                InfoCenter.addMoney(passenger.getPrice());
        	                                passenger.setCarId((cars.get(carIndex)).getCarId());
        	                                InfoCenter.update(passenger, BoardLayers.PASSENGER);
        	                                User temp = InfoCenter.getUser();
        	                                temp.addPassenger();
        	                                pickPassengers(v);
                                		}
                        				else
                        				{
                        					carFullPopup();
                        				}
                        			}
                        			else
                        			{
                        				wrongCarPopup();
                        			}
                        		}
                        		else
                        		{
                        			noCarsPopup();
                        		}
	                        }
                        });
                        
                        ImageView view1 = new ImageView(this);
                        Bitmap bmp=BitmapFactory.decodeResource(getResources(), myPassenger.getImageId());
                        int width=68;
                        int height=100;
                        Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
                        view1.setImageBitmap(resizedbitmap);
                        //view1.setScaleType(ScaleType.FIT_CENTER);
                        type1.addView(view1);
                                
                        TextView passengerName = new TextView(this);
                        passengerName.setPadding(10,0,0,0);
                        passengerName.setText(myPassenger.getName());
                        passengerName.setTextAppearance(this, android.R.style.TextAppearance_Large);
                        passengerName.setTextColor(Color.WHITE);
                        passengerName.setTypeface(Typeface.SANS_SERIF);
                        passengerName.setGravity(Gravity.CENTER_VERTICAL);
                        type1.addView(passengerName);
                        
                        TextView destinationCity = new TextView(this);
                        int destCityId = myPassenger.getDestinationCityId();
                        City destCity = InfoCenter.findCityById(destCityId);
                        String cityName = destCity.getCityName();
                        destinationCity.setPadding(10,0,0,0);
                        destinationCity.setText("Destination: " + cityName);
                        destinationCity.setTextAppearance(this, android.R.style.TextAppearance_Large);
                        destinationCity.setTextColor(Color.WHITE);
                        destinationCity.setTypeface(Typeface.SANS_SERIF);
                        destinationCity.setGravity(Gravity.CENTER_VERTICAL);
                        type1.addView(destinationCity);
                        
                        TextView price = new TextView(this);
                        price.setText("$" + myPassenger.getPrice());
                        price.setPadding(10,0,0,0);
                        price.setTextAppearance(this, android.R.style.TextAppearance_Large);
                        price.setTextColor(Color.WHITE);
                        price.setTypeface(Typeface.SANS_SERIF);
                        price.setGravity(Gravity.CENTER_VERTICAL);
                        type1.addView(price);
                                
                        tl.addView(type1);
                }
        }
    }
    
    public void getLeftCar(View v)
    {
        if(carIndex != 0)
        {
        	carIndex--;
        	drawCar();
        }
    }
    
    public void getRightCar(View v)
    {
        if(carIndex != (cars.size() - 1))
        {
        	carIndex++;
        	drawCar();
        }
    }
    
    public void closeSelector(View v)
    {
        addPassenger.setVisibility(View.GONE);
        addCargo.setVisibility(View.GONE);
        mainView.setVisibility(View.VISIBLE);
        pickTrain.setVisibility(View.GONE);
        if(cars != null)
        {
        	drawCar();
        }
    }
    
    public void closeSelector()
    {
        addPassenger.setVisibility(View.GONE);
        addCargo.setVisibility(View.GONE);
        mainView.setVisibility(View.VISIBLE);
        pickTrain.setVisibility(View.GONE);
        if(cars != null)
        {
        	drawCar();
        }
    }
    
    public void setupMediaPlayer()
    {
        mediaPlayer = MediaPlayer.create(Activity_Station.this, R.raw.station_background);
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
    
    @Override
    public void onBackPressed() {
        InfoCenter.removeStationPassengers();
        InfoCenter.removeStationCargo();
        finish();
    }
    
    public void noCarsPopup()
    {
    	AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
 		helpBuilder.setTitle("No Car");
 		helpBuilder.setMessage("There is not a car to add this item to");
 		helpBuilder.setNegativeButton("Ok",
 		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	//Do nothing
		    }
 		});

 		AlertDialog helpDialog = helpBuilder.create();
 		helpDialog.show();
    }
    
    public void carFullPopup()
    {
    	AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
 		helpBuilder.setTitle("Car Full");
 		helpBuilder.setMessage("You have added the maximum number of items to this car");
 		helpBuilder.setNegativeButton("Ok",
 		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	//Do nothing
		    }
 		});

 		AlertDialog helpDialog = helpBuilder.create();
 		helpDialog.show();
    }
    
    public void wrongCarPopup()
    {
    	AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
 		helpBuilder.setTitle("Wrong car type");
 		helpBuilder.setMessage("You cannot add an item of this type to the current car");
 		helpBuilder.setNegativeButton("Ok",
 		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	//Do nothing
		    }
 		});

 		AlertDialog helpDialog = helpBuilder.create();
 		helpDialog.show();
    }
    
    public void selectDestination(View v)
    {
    	if(chosenTrain != 0)
    	{
    		ArrayList<Cargo> cargo = InfoCenter.getCargo();
    		for (Car car:cars) {
    			for (Cargo temp:cargo) {
    				if ((temp.getCarId() == car.getCarId())) {
    					System.out.println("Cargo going to city: " + InfoCenter.findCityById(temp.getDestinationCityId()).getCityName());
    				}
    			}
    		}
    		
    		Renderer.setEventStateSelectDestination(chosenTrain);
        	finish();
    	}
    }
}