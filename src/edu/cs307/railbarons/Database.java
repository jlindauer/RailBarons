package edu.cs307.railbarons;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class Database {
	private static SQLiteDatabase database = null;
	
	public Database(){
		initDatabase();
	}
	
	public static void initDatabase()
	{
		String tb = "CREATE  TABLE IF NOT EXISTS ";
		try{
			database = SQLiteDatabase.openOrCreateDatabase("/data/data/edu.cs307.railbarons/database.db", null);	
		}
		catch (SQLiteException e){
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		database.execSQL(tb + "trainTypes ( trainTypeID INTEGER PRIMARY KEY , speed INT NULL ,  maxCars INT NULL ,  purchasePrice INT NULL ,  resalePrice INT NULL ,  fuelEfficiency INT NULL, trainName VARCHAR(100) )");
		database.execSQL(tb + "city (cityID INTEGER PRIMARY KEY , cityLevel INT NULL , xPos INT NULL , yPos INT NULL , cityName VARCHAR(100) NULL , isAccessible TINYINT(1) NULL )");
		database.execSQL(tb + "trains (trainID INTEGER PRIMARY KEY, trainType INT NOT NULL, milesTraveled INT NULL, currentCityID INT NULL, destinationCityID INT NULL, pathIndex INT NULL, departureTime INTEGER NULL, FOREIGN KEY(trainType) REFERENCES trainTypes(trainTypeID))");
		database.execSQL(tb + "carTypes ( carTypeID INTEGER PRIMARY KEY , maxCapacity INT NULL , cargo TINYINT(1) NULL , carName VARCHAR(100) NULL , purchasePrice INT NULL , resalePrice INT NULL )");
		database.execSQL(tb + "cars (carID INTEGER PRIMARY KEY , carType INT NULL , trainID INT NULL )");
		database.execSQL(tb + "userStats (id INTEGER PRIMARY KEY , money INT NULL , totalFuelPrice INT NULL , hoursPlayed INT NULL , audioOn TINYINT(1) NOT NULL , cargoTrans INT NULL , passengerTrans INT NULL )");
		database.execSQL(tb + "cargoTypes (cargoTypeID INTEGER PRIMARY KEY , imageNo INT NULL , name VARCHAR(45) NULL , invoiceCost INT NULL )");
		database.execSQL(tb + "cargo (cargoID INTEGER PRIMARY KEY , cargoType INT NULL , carPosition INT NULL , carID INT NULL , destinationCityID INT NULL , price INT NULL )");
		database.execSQL(tb + "passengers (passengerID INTEGER PRIMARY KEY , seatNo INT NULL , pcarID INT NULL, imageID INT NULL, name VARCHAR(100) , destinationCityID INT NULL , price INT NULL )");
		database.execSQL(tb + "rails (railID INTEGER PRIMARY KEY , cityID1 INT NOT NULL , cityID2 INT NOT NULL , distance INT NOT NULL , isVisible INT NOT NULL )");
		database.execSQL(tb + "transit (transitID INTEGER PRIMARY KEY , trainID INT NOT NULL , cityID1 INT NOT NULL , cityID2 INT NOT NULL , pathIndex INT NOT NULL )");
		database.close();
		
		if(firstLaunch()){
			loadCarTypes();
			loadTrainTypes();
			loadCargoTypes();
			loadUserData();
			initializeStartingObjects();
		}
	}
	
	public static void loadTrainTypes()
	{
		try{
			database = SQLiteDatabase.openOrCreateDatabase("/data/data/edu.cs307.railbarons/database.db", null);	
		}
		catch (SQLiteException e){
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
				
		String selectQuery = "SELECT * FROM trainTypes";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.getCount() == 0)
		{
			ContentValues cv = new ContentValues();
			cv.put("trainTypeID", 1);
			cv.put("speed", 2);
			cv.put("maxCars", 3);
			cv.put("purchasePrice", 5000);
			cv.put("resalePrice", 1300);
			cv.put("fuelEfficiency", 1);
			cv.put("trainName", "Steam Engine");
			database.insert("trainTypes", null, cv);
			
			cv = new ContentValues();
			cv.put("trainTypeID", 2);
			cv.put("speed", 4);
			cv.put("maxCars", 8);
			cv.put("purchasePrice", 12000);
			cv.put("resalePrice", 8000);
			cv.put("fuelEfficiency", 2);
			cv.put("trainName", "Diesel Engine");
			database.insert("trainTypes", null, cv);
			
			cv = new ContentValues();
			cv.put("trainTypeID", 3);
			cv.put("speed", 6);
			cv.put("maxCars", 10);
			cv.put("purchasePrice", 20000);
			cv.put("resalePrice", 16000);
			cv.put("fuelEfficiency", 3);
			cv.put("trainName", "Maglev Train");
			database.insert("trainTypes", null, cv);
		}
		database.close();
	}
	
	public static void loadCarTypes()
	{
		try{
			database = SQLiteDatabase.openOrCreateDatabase("/data/data/edu.cs307.railbarons/database.db", null);	
		}
		catch (SQLiteException e){
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
		String selectQuery = "SELECT * FROM carTypes";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.getCount() == 0)
		{
			ContentValues cv = new ContentValues();
			cv.put("carTypeID", 1);
			cv.put("maxCapacity", 6);
			cv.put("cargo", 1);
			cv.put("carName", "Maglev Cargo");
			cv.put("purchasePrice", 7000);
			cv.put("resalePrice", 3000);
			database.insert("carTypes", null, cv);
			
			cv = new ContentValues();
			cv.put("carTypeID", 2);
			cv.put("maxCapacity", 6);
			cv.put("cargo", 1);
			cv.put("carName", "Steam Cargo");
			cv.put("purchasePrice", 4000);
			cv.put("resalePrice", 2500);
			database.insert("carTypes", null, cv);
			
			cv = new ContentValues();
			cv.put("carTypeID", 3);
			cv.put("maxCapacity", 6);
			cv.put("cargo", 1);
			cv.put("carName", "Diesel Cargo");
			cv.put("purchasePrice", 6000);
			cv.put("resalePrice", 3500);
			database.insert("carTypes", null, cv);
			
			cv = new ContentValues();
			cv.put("carTypeID", 4);
			cv.put("maxCapacity", 10);
			cv.put("cargo", 0);
			cv.put("carName", "Diesel Passenger");
			cv.put("purchasePrice", 6000);
			cv.put("resalePrice", 3000);
			database.insert("carTypes", null, cv);
			
			cv = new ContentValues();
			cv.put("carTypeID", 5);
			cv.put("maxCapacity", 10);
			cv.put("cargo", 0);
			cv.put("carName", "Steam Passenger");
			cv.put("purchasePrice", 4500);
			cv.put("resalePrice", 2000);
			database.insert("carTypes", null, cv);
			
			cv = new ContentValues();
			cv.put("carTypeID", 6);
			cv.put("maxCapacity", 10);
			cv.put("cargo", 0);
			cv.put("carName", "Maglev Passenger");
			cv.put("purchasePrice", 10000);
			cv.put("resalePrice", 6700);
			database.insert("carTypes", null, cv);
		}
		database.close();
	}
	
	public static void loadCargoTypes()
	{
		try{
			database = SQLiteDatabase.openOrCreateDatabase("/data/data/edu.cs307.railbarons/database.db", null);	
		}
		catch (SQLiteException e){
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
				
		String selectQuery = "SELECT * FROM cargoTypes";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.getCount() == 0)
		{
			ContentValues cv = new ContentValues();
			cv.put("cargoTypeID", 1);
			cv.put("name", "hay");
			cv.put("invoiceCost", 100);
			database.insert("cargoTypes", null, cv);
			
			cv = new ContentValues();
			cv.put("cargoTypeID", 2);
			cv.put("name", "wood");
			cv.put("invoiceCost", 200);
			database.insert("cargoTypes", null, cv);
			
			cv = new ContentValues();
			cv.put("cargoTypeID", 3);
			cv.put("name", "steel");
			cv.put("invoiceCost", 400);
			database.insert("cargoTypes", null, cv);
		}
		database.close();
	}
	
	public static SQLiteDatabase getDb()
	{
		if(!database.isOpen())
		{
			try{
				database = SQLiteDatabase.openOrCreateDatabase("/data/data/edu.cs307.railbarons/database.db", null);	
			}
			catch (SQLiteException e){
				//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
		return database;
	}
	
	public static void close()
	{
		if(database.isOpen())
		{
			try{
				database.close();
			}
			catch (SQLiteException e){
				//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public static void clearDatabase()
	{
		try{
			database = SQLiteDatabase.openOrCreateDatabase("/data/data/edu.cs307.railbarons/database.db", null);	
		}
		catch (SQLiteException e){
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
		//drop all tables
		database.execSQL("DROP TABLE IF EXISTS trainTypes");
		database.execSQL("DROP TABLE IF EXISTS city");
		database.execSQL("DROP TABLE IF EXISTS trains");
		database.execSQL("DROP TABLE IF EXISTS carTypes");
		database.execSQL("DROP TABLE IF EXISTS cars");
		database.execSQL("DROP TABLE IF EXISTS userStats");
		database.execSQL("DROP TABLE IF EXISTS cargoTypes");
		database.execSQL("DROP TABLE IF EXISTS cargo");
		database.execSQL("DROP TABLE IF EXISTS passengerTypes");
		database.execSQL("DROP TABLE IF EXISTS passengers");
		database.execSQL("DROP TABLE IF EXISTS rails");
		database.execSQL("DROP TABLE IF EXISTS transit");
		
		database.close();
		InfoCenter.clearArrayLists();
		initDatabase();
	}
	
	public static boolean firstLaunch()
	{
		try{
			database = SQLiteDatabase.openOrCreateDatabase("/data/data/edu.cs307.railbarons/database.db", null);	
		}
		catch (SQLiteException e){
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		String selectQuery = "SELECT * FROM userStats";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.getCount() == 0)
		{
			database.close();
			return true;
		}
		database.close();
		return false;
	}

	public static void loadUserData()
	{
		try{
			database = SQLiteDatabase.openOrCreateDatabase("/data/data/edu.cs307.railbarons/database.db", null);	
		}
		catch (SQLiteException e){
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		ContentValues cv = new ContentValues();
		cv.put("id", 1);
		cv.put("money", 15000);
		cv.put("totalFuelPrice", 6);
		cv.put("hoursPlayed", 0);
		cv.put("audioOn", 1);
		cv.put("cargoTrans", 0);
		cv.put("passengerTrans", 0);
		database.insert("userStats", null, cv);
		
		database.close();
	}
	
	public static void initializeStartingObjects(){
		//Create all of the initial cities
		InfoCenter.createCity(BoardLayers.CITY, 5, 2770, 770, "New York", 1);
		InfoCenter.createCity(BoardLayers.CITY, 5, 425, 1223, "Los Angeles", 0);
		InfoCenter.createCity(BoardLayers.CITY, 5, 2055, 821, "Chicago", 0);
		InfoCenter.createCity(BoardLayers.CITY, 5, 1695, 1667, "Houston", 0);
		InfoCenter.createCity(BoardLayers.CITY, 5, 2695, 850, "Philadelphia", 1); /* id = 5 */
		InfoCenter.createCity(BoardLayers.CITY, 5, 750, 1315, "Phoenix", 0);
		InfoCenter.createCity(BoardLayers.CITY, 5, 1500, 1670, "San Antonio", 0);
		InfoCenter.createCity(BoardLayers.CITY, 5, 495, 1320, "San Diego", 0);
		InfoCenter.createCity(BoardLayers.CITY, 5, 1590, 1475, "Dallas", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 2460, 1721, "Tampa", 0); /* id = 10 */
		InfoCenter.createCity(BoardLayers.CITY, 4, 447, 287, "Seattle", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 420, 452, "Portland", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 2148, 1000, "Indianapolis", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 1218, 980, "Denver", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 1900, 1105, "St. Louis", 0); /* id = 15 */
		InfoCenter.createCity(BoardLayers.CITY, 4, 2855, 626, "Boston", 1);
		InfoCenter.createCity(BoardLayers.CITY, 4, 2322, 1355, "Atlanta", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 1795, 615, "Minneapolis", 0);
		InfoCenter.createCity(BoardLayers.CITY, 4, 2310, 1000, "Columbus", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 2260, 740, "Detroit", 0); /* id = 20 */
		InfoCenter.createCity(BoardLayers.CITY, 2, 770, 445, "Helena", 0);
		InfoCenter.createCity(BoardLayers.CITY, 2, 750, 775, "Salt Lake City", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 595, 1058, "Las Vegas", 0);
		InfoCenter.createCity(BoardLayers.CITY, 4, 298, 986, "San Francisco", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 625, 575, "Boise", 0); /* id = 25 */
		InfoCenter.createCity(BoardLayers.CITY, 3, 2640, 1040, "Richmond", 0);
		InfoCenter.createCity(BoardLayers.CITY, 4, 2665, 945, "Washington, D.C.", 0);
		InfoCenter.createCity(BoardLayers.CITY, 4, 2508, 1193, "Charlotte", 0);
		InfoCenter.createCity(BoardLayers.CITY, 2, 2565, 1330, "Charleston", 0);
		InfoCenter.createCity(BoardLayers.CITY, 2, 2590, 670, "Syracuse", 1); /* id = 30 */
		InfoCenter.createCity(BoardLayers.CITY, 2, 2892, 488, "Augusta", 1);
		InfoCenter.createCity(BoardLayers.CITY, 2, 2232, 1100, "Louisville", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 2000, 1635, "New Orleans", 0);
		InfoCenter.createCity(BoardLayers.CITY, 3, 1800, 1350, "Little Rock", 0);
		InfoCenter.createCity(BoardLayers.CITY, 1, 1839, 509, "Duluth", 0); /* id = 35 */
		InfoCenter.createCity(BoardLayers.CITY, 1, 2113, 950, "West Lafayette", 0);
		InfoCenter.createCity(BoardLayers.CITY, 1, 1968, 970, "Peoria", 0);
		
		//Create starting rails
		InfoCenter.createRail(BoardLayers.RAIL, 1, 5, 117, 1);
		InfoCenter.createRail(BoardLayers.RAIL, 1, 16, 165, 1);
		InfoCenter.createRail(BoardLayers.RAIL, 1, 30, 216, 1);
		InfoCenter.createRail(BoardLayers.RAIL, 16, 30, 276, 1);
		InfoCenter.createRail(BoardLayers.RAIL, 16, 31, 145, 1);
		InfoCenter.createRail(BoardLayers.RAIL, 5, 30, 208, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 5, 27, 100, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 5, 19, 397, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 27, 26, 98, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 27, 19, 355, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 26, 28, 202, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 26, 29, 298, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 28, 17, 212, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 28, 29, 150, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 28, 32, 325, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 17, 10, 435, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 17, 33, 465, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 17, 34, 498, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 17, 32, 299, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 17, 29, 243, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 32, 34, 420, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 32, 19, 110, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 32, 15, 311, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 32, 13, 104, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 19, 13, 163, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 19, 20, 206, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 13, 20, 263, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 13, 36, 43, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 13, 15, 256, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 36, 37, 151, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 36, 3, 124, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 37, 3, 168, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 37, 15, 134, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 3, 18, 332, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 18, 35, 115, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 34, 9, 386, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 34, 33, 476, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 34, 15, 124, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 9, 7, 213, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 9, 4, 222, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 4, 33, 301, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 4, 7, 196, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 11, 12, 167, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 11, 25, 339, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 11, 21, 360, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 12, 25, 239, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 12, 24, 552, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 24, 23, 321, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 24, 2, 268, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 24, 22, 513, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 25, 21, 195, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 25, 22, 236, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 22, 21, 331, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 22, 14, 511, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 22, 23, 323, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 23, 2, 249, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 23, 8, 287, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 23, 6, 300, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 8, 2, 119, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 8, 6, 273, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 6, 14, 576, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 6, 9, 855, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 14, 9, 619, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 14, 15, 721, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 14, 18, 683, 0);
		InfoCenter.createRail(BoardLayers.RAIL, 21, 18, 698, 0);
		
		//Create starting train
		InfoCenter.createTrain(BoardLayers.TRAIN, 3, 16, 0, 0, 0);
		InfoCenter.createTrain(BoardLayers.TRAIN, 2, 16, 0, 0, 0);
		InfoCenter.createTrain(BoardLayers.TRAIN, 3, 16, 0, 0, 0);
		//TODO remove these because they are temporary
		InfoCenter.createCar(BoardLayers.CAR, 2, 1);
		InfoCenter.createCar(BoardLayers.CAR, 1, 1);
		InfoCenter.createCar(BoardLayers.CAR, 5, 1);
		InfoCenter.createCar(BoardLayers.CAR, 2, 2);
	}
}
