package edu.cs307.railbarons;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

public class InfoCenter {
	private static Database dbConnection;
	private static ArrayList<Train> _trains;
	private static ArrayList<Rail> _rails;
	private static ArrayList<Cargo> _cargo;
	private static ArrayList<City> _cities;
	private static ArrayList<Car> _cars;
	private static ArrayList<Passenger> _passengers;
	private static User _user;
	
	public InfoCenter(){
		_trains = new ArrayList<Train>();
		_rails = new ArrayList<Rail>();
		_cargo = new ArrayList<Cargo>();
		_cities = new ArrayList<City>();
		_cars = new ArrayList<Car>();
		_passengers = new ArrayList<Passenger>();
		dbConnection = new Database();
		_user = createUser();
		loadAll();
		removeStationPassengers();
		removeStationCargo();
	}
	
	public static User createUser(){
		if(_user == null){
			String selectQuery = "SELECT * FROM userStats";
			Cursor cursor = dbConnection.getDb().rawQuery(selectQuery, null);
			//Loop through the cursor and add all rows to the userStats table
			if(cursor.moveToFirst())
			{
				User newUser = new User(Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)),Integer.parseInt(cursor.getString(6)));
				newUser.setUserId(Integer.parseInt(cursor.getString(0)));
				_user = newUser;
			}
			dbConnection.close();
		}
		return _user;
	}
	
	public static Train createTrain(BoardLayers layer, int trainType, int currentCityId, int destinationCityId, int pathIndex, long time){
		Train newTrain = new Train(layer, trainType, getTrainImageId(trainType), 0, currentCityId, destinationCityId, pathIndex, time);
		ContentValues cv = new ContentValues();
		cv.put("trainType", trainType);
		cv.put("milesTraveled", 0);
		cv.put("currentCityID", currentCityId);
		cv.put("destinationCityId", destinationCityId);
		cv.put("pathIndex", pathIndex);
		cv.put("departureTime", 0);
		long newTrainId = dbConnection.getDb().insert("trains", null, cv);
		newTrain.setTrainId((int)newTrainId);
		dbConnection.close();
		
		String selectQuery = "SELECT speed FROM trainTypes WHERE trainTypeID=" + trainType;
		Cursor cursor = dbConnection.getDb().rawQuery(selectQuery, null);
		//Loop through the cursor and add all rows to the train ArrayList
		if(cursor.moveToFirst())
		{
			do{
				newTrain.setSpeed(cursor.getInt(0));
				break;
			}while(cursor.moveToNext());
		}
		dbConnection.close();
		
		_trains.add(newTrain);
		return newTrain;
	}
	
	public static City createCity(BoardLayers layer, int cityLevel, int xPos, int yPos, String cityName, int isAccessible){	
		City newCity = new City(layer, cityLevel, xPos, yPos, getCityImageId(cityLevel), cityName, isAccessible);
		ContentValues cv = new ContentValues();
		cv.put("cityLevel", cityLevel);
		cv.put("xPos", xPos);
		cv.put("yPos", yPos);
		cv.put("cityName", cityName);
		cv.put("isAccessible", isAccessible);
		long newCityId = dbConnection.getDb().insert("city", null, cv);
		newCity.setCityId((int)newCityId);
		dbConnection.close();
		_cities.add(newCity);
		return newCity;
	}
	
	public static Rail createRail(BoardLayers layer, int cityId1, int cityId2, int distance, int isVisible){
		Rail newRail = new Rail(layer, R.drawable.left, cityId1, cityId2, distance, isVisible);
		ContentValues cv = new ContentValues();
		cv.put("cityId1", cityId1);
		cv.put("cityId2", cityId2);
		cv.put("distance", distance);
		cv.put("isVisible", isVisible);
		long newRailId = dbConnection.getDb().insert("rails", null, cv);
		newRail.setRailId((int)newRailId);
		dbConnection.close();
		_rails.add(newRail);
		City city1 = findCityById(cityId1);
		city1.addRailToCity(newRail);
		City city2 = findCityById(cityId1);
		city2.addRailToCity(newRail);
		return newRail;
	}
	
	public static Cargo createCargo(BoardLayers layer, int cargoType, int cargoPosition, int carId, int destinationCityId, int price){
		Cargo newCargo = new Cargo(layer, cargoType, cargoPosition, carId, destinationCityId, price);
		ContentValues cv = new ContentValues();
		cv.put("cargoType", cargoType);
		cv.put("carPosition", cargoPosition);
		cv.put("carId", carId);
		cv.put("destinationCityID", destinationCityId);
		cv.put("price", price);
		long newCargoId = dbConnection.getDb().insert("cargo", null, cv);
		newCargo.setCargoId((int)newCargoId);
		dbConnection.close();
		_cargo.add(newCargo);
		return newCargo;
	}
	
	public static Passenger createPassenger( BoardLayers layer, int imageId, int seatNo, int pCarId, String name, int destinationCityId, int price){
		Passenger newPassenger = new Passenger(layer, imageId, seatNo, pCarId, name, destinationCityId, price);
		ContentValues cv = new ContentValues();
		cv.put("seatNo", seatNo);
		cv.put("pCarId", pCarId);
		cv.put("imageId", imageId);
		cv.put("name", name);
		cv.put("destinationCityID", destinationCityId);
		cv.put("price", price);
		long newPassengerId = dbConnection.getDb().insert("passengers", null, cv);
		newPassenger.setPassengerId((int)newPassengerId);
		dbConnection.close();
		_passengers.add(newPassenger);
		return newPassenger;
	}
	
	public static Car createCar(BoardLayers layer, int carType, int trainId){
		Car newCar = new Car(layer, carType, getCarImageId(carType), trainId);
		ContentValues cv = new ContentValues();
		cv.put("carType", carType);
		cv.put("trainId", trainId);
		long newCarId = dbConnection.getDb().insert("cars", null, cv);
		newCar.setCarId((int)newCarId);
		dbConnection.close();
		if(newCar.getTrainId() > 0){
			Train train = findTrainById(newCar.getTrainId());
			if(train != null){
				train.addAttachedCar(newCar);
			}
		}
		_cars.add(newCar);
		return newCar;
	}
	
	//Adds a path to a train
	public static void addPath(ArrayList<City> cityList, int trainId){
		for(int i = 0; i < cityList.size() - 1; i++){
			ContentValues cv = new ContentValues();
			cv.put("trainID", trainId);
			cv.put("cityID1", cityList.get(i).getCityId());
			cv.put("cityID2", cityList.get(i+1).getCityId());
			cv.put("pathIndex", i+1);
			dbConnection.getDb().insert("transit", null, cv);
			dbConnection.close();
		}
		Train train = findTrainById(trainId);
		train.setCityPath(cityList);
		train.setPathIndex(1);
		train.setDestinationCityId(cityList.get(1).getCityId());
		train.setDepartureTime();
		InfoCenter.update(train, BoardLayers.TRAIN);
	}
	
	//Once city has reached a given city in its path, the currentCity and destinationCity are updated
	public static void addPathIndex(Train train){
		train.incrementPathIndex();
		int size = train.getCityPath().size();
		if(train.getPathIndex() < size){
			train.setCurrentCityId(train.getCityPath().get(train.getPathIndex()-1).getCityId());
			train.setDestinationCityId(train.getCityPath().get(train.getPathIndex()).getCityId());
			train.setDepartureTime();
		}
		else{
			train.setCurrentCityId(train.getCityPath().get(train.getPathIndex()-1).getCityId());
			train.setDestinationCityId(0);
			removePath(train.getTrainId());
			train.clearDepartureTime();
		}
		
		train.checkPassengerDropOff();
		train.checkCargoDropOff();
		InfoCenter.update(train, BoardLayers.TRAIN);
	}
	
	public static void removePath(int trainId){
		dbConnection.getDb().delete("transit", "trainID=" + trainId, null);
		dbConnection.close();
	}
	
	public static ArrayList<Train> getTrains(){
		return _trains;
	}
	
	public static ArrayList<Rail> getRails(){
		return _rails;
	}
	
	public static ArrayList<Cargo> getCargo(){
		return _cargo;
	}
	
	public static ArrayList<City> getCities(){
		return _cities;
	}
	
	public static ArrayList<Car> getCar(){
		return _cars;
	}
	
	public static ArrayList<Passenger> getPassengers(){
		return _passengers;
	}
	
	public static User getUser(){
		return _user;
	}
	
	public static int getMoney()
	{
		return _user.getMoney();
	}
	
	public static Train findTrainById(int id){
		for(Train t: _trains){
			if(t.getTrainId() == id){
				return t;
			}
		}
		return null;
	}
	
	public static City findCityById(int id){
		for(City t: _cities){
			if(t.getCityId() == id){
				return t;
			}
		}
		return null;
	}
	
	public static Rail findRailById(int id){
		for(Rail t: _rails){
			if(t.getRailId() == id){
				return t;
			}
		}
		return null;
	}
	
	public static Cargo findCargoById(int id){
		for(Cargo t: _cargo){
			if(t.getCargoId() == id){
				return t;
			}
		}
		return null;
	}
	
	public static Passenger findPassengerById(int id){
		for(Passenger t: _passengers){
			if(t.getPassengerId() == id){
				return t;
			}
		}
		return null;
	}
	
	public static Car findCarById(int id){
		for(Car t: _cars){
			if(t.getCarId() == id){
				return t;
			}
		}
		return null;
	}
	
	public static void updateUser(){
		ContentValues cv = new ContentValues();
		cv.put("id", _user.getUserId());
		cv.put("money", _user.getMoney());
		cv.put("totalFuelPrice", _user.getTotalFuelPrice());
		cv.put("hoursPlayed", _user.getHoursPlayed());
		cv.put("audioOn", _user.getAudioOn());
		cv.put("cargoTrans", _user.getCargo());
		cv.put("passengerTrans", _user.getPassengers());
		dbConnection.getDb().update("userStats", cv, null, null);
		dbConnection.close();
	}
	
	public static void removeMoney(int money)
	{
		_user.removeMoney(money);
		updateUser();
	}
	
	public static void addMoney(int money)
	{
		_user.addMoney(money);
		updateUser();
	}
	
	//This will update the given object in the database
	public static void update(Object ob, BoardLayers layer){
		if(layer == BoardLayers.TRAIN){
			Train updatedTrain = (Train) ob;
			ContentValues cv = new ContentValues();
			cv.put("trainType", updatedTrain.getTrainType());
			cv.put("milesTraveled", updatedTrain.getMilesTraveled());
			cv.put("currentCityID", updatedTrain.getCurrentCityId());
			cv.put("destinationCityId", updatedTrain.getDestinationCityId());
			cv.put("pathIndex", updatedTrain.getPathIndex());
			cv.put("departureTime", updatedTrain.getDepartureTime());
			dbConnection.getDb().update("trains", cv, "trainID=" + updatedTrain.getTrainId(), null);
			dbConnection.close();
		}
		else if(layer == BoardLayers.CITY){
			City updatedCity = (City) ob;
			ContentValues cv = new ContentValues();
			cv.put("cityLevel", updatedCity.getCityLevel());
			cv.put("cityName", updatedCity.getCityName());
			cv.put("isAccessible", updatedCity.getIsAccessible());
			dbConnection.getDb().update("city", cv, "cityID=" + updatedCity.getCityId(), null);
			dbConnection.close();
			
		}
		else if(layer == BoardLayers.RAIL){
			Rail updatedRail = (Rail) ob;
			if(updatedRail.getIsVisible()==0){
				int money = InfoCenter.getMoney();
				int railCost = 5*(updatedRail.getDistance());
				if(railCost<=money){
					InfoCenter.removeMoney(railCost);
					updatedRail.setVisible(1);
					findCityById(updatedRail.getCityId1()).setIsAccessible(1);
					findCityById(updatedRail.getCityId2()).setIsAccessible(1);	
					updatedRail.setDrawStyle(Rail.DrawStyle.RAILACTIVE);
					ContentValues cv = new ContentValues();
					cv.put("cityId1", updatedRail.getCityId1());
					cv.put("cityId2", updatedRail.getCityId2());
					cv.put("distance", updatedRail.getDistance());
					cv.put("isVisible", updatedRail.getIsVisible());
					dbConnection.getDb().update("rails", cv, "railID=" + updatedRail.getRailId(), null);
					dbConnection.close();
				}
			}
		}
		else if(layer == BoardLayers.CARGO){
			Cargo updatedCargo = (Cargo) ob;
			ContentValues cv = new ContentValues();
			cv.put("cargoType", updatedCargo.getCargoType());
			cv.put("carPosition", updatedCargo.getCargoPosition());
			cv.put("carId", updatedCargo.getCarId());
			cv.put("destinationCityID", updatedCargo.getDestinationCityId());
			dbConnection.getDb().update("cargo", cv, "cargoID=" + updatedCargo.getCargoId(), null);
			dbConnection.close();
			
		}
		else if(layer == BoardLayers.PASSENGER){
			Passenger updatedPassenger = (Passenger) ob;
			ContentValues cv = new ContentValues();
			cv.put("seatNo", updatedPassenger.getSeatNo());
			cv.put("pCarId", updatedPassenger.getPCarId());
			cv.put("imageId", updatedPassenger.getImageId());
			cv.put("name", updatedPassenger.getName());
			cv.put("destinationCityID", updatedPassenger.getDestinationCityId());
			dbConnection.getDb().update("passengers", cv, "passengerID=" + updatedPassenger.getPassengerId(), null);
			dbConnection.close();
			
		}
		else if(layer == BoardLayers.CAR){
			Car updatedCar = (Car) ob;
			ContentValues cv = new ContentValues();
			cv.put("carType", updatedCar.getCarType());
			cv.put("trainId", updatedCar.getTrainId());
			if(updatedCar.getTrainId() > 0){
				Train train = findTrainById(updatedCar.getTrainId());
				if(train != null){
					train.addAttachedCar(updatedCar);
				}
			}
			dbConnection.getDb().update("cars", cv, "carID=" + updatedCar.getCarId(), null);
			dbConnection.close();
		}
		
	}
	
	//This function will delete an object from the database and the cached objects
	public static void delete(Object ob, BoardLayers layer){
		if(layer == BoardLayers.TRAIN){
			Train deletedTrain = (Train) ob;
			dbConnection.getDb().delete("trains", "trainID=" + deletedTrain.getTrainId(), null);
			dbConnection.close();
			_trains.remove(deletedTrain);
		}
		else if(layer == BoardLayers.CITY){
			City deletedCity = (City) ob;
			dbConnection.getDb().delete("city", "cityID=" + deletedCity.getCityId(), null);
			dbConnection.close();
			_cities.remove(deletedCity);
		}
		else if(layer == BoardLayers.RAIL){
			Rail deletedRail = (Rail) ob;
			dbConnection.getDb().delete("rails", "railID=" + deletedRail.getRailId(), null);
			dbConnection.close();
			_rails.remove(deletedRail);
		}
		else if(layer == BoardLayers.CARGO){
			Cargo deletedCargo = (Cargo) ob;
			dbConnection.getDb().delete("cargo", "cargoID=" + deletedCargo.getCargoId(), null);
			dbConnection.close();
			_cargo.remove(deletedCargo);
		}
		else if(layer == BoardLayers.PASSENGER){
			Passenger deletedPassenger = (Passenger) ob;
			dbConnection.getDb().delete("passengers", "passengerID=" + deletedPassenger.getPassengerId(), null);
			dbConnection.close();
			_passengers.remove(deletedPassenger);
		}
		else if(layer == BoardLayers.CAR){
			Car deletedCar = (Car) ob;
			dbConnection.getDb().delete("car", "carID=" + deletedCar.getCarId(), null);
			dbConnection.close();
			_cars.remove(deletedCar);
		}
		
	}
	
	public static void loadAll(){
		//Load all cities
		String selectQuery = "SELECT * FROM city";
		Cursor cursor = dbConnection.getDb().rawQuery(selectQuery, null);
		//Loop through the cursor and add all rows to the train ArrayList
		if(cursor.moveToFirst())
		{
			do{
				int cityLevel = Integer.parseInt(cursor.getString(1));
				int imageId = getCityImageId(cityLevel);
				City city = new City(BoardLayers.CITY, Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), imageId, cursor.getString(4), Integer.parseInt(cursor.getString(5)));
				city.setCityId(Integer.parseInt(cursor.getString(0)));
				if(InfoCenter.findCityById(city.getCityId()) == null){
					_cities.add(city);
				}
				city.setCityId(Integer.parseInt(cursor.getString(0)));
				if(InfoCenter.findCityById(city.getCityId()) == null){
					_cities.add(city);
				}
			}while(cursor.moveToNext());
		}
		dbConnection.close();
				
		//Load all rails
		selectQuery = "SELECT * FROM rails";
		cursor = dbConnection.getDb().rawQuery(selectQuery, null);
		//Loop through the cursor and add all rows to the train ArrayList
		if(cursor.moveToFirst())
		{
			do{
				int imageId = R.drawable.left;
				Rail rail = new Rail(BoardLayers.RAIL, imageId, Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));
				rail.setRailId(Integer.parseInt(cursor.getString(0)));
				if(InfoCenter.findRailById(rail.getRailId()) == null){
					_rails.add(rail);					
				}
			}while(cursor.moveToNext());
			
		}
		dbConnection.close();
		
		//Load all trains
		selectQuery = "SELECT * FROM trains INNER JOIN trainTypes ON trains.trainType=trainTypes.trainTypeID";
		cursor = dbConnection.getDb().rawQuery(selectQuery, null);
		//Loop through the cursor and add all rows to the train ArrayList
		if(cursor.getCount()>0)
  		{
			if(cursor.moveToFirst())
			{
				do{
					int trainId = Integer.parseInt(cursor.getString(0));
					int trainType = Integer.parseInt(cursor.getString(1));
					int imageId = getTrainImageId(trainType);
					Train train = new Train(BoardLayers.TRAIN, Integer.parseInt(cursor.getString(1)), imageId, Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), cursor.getLong(cursor.getColumnIndex("departureTime")));
					train.setTrainId(trainId);
					train.loadDepartureTime(Long.parseLong(cursor.getString(6)));
					train.setSpeed(cursor.getInt(cursor.getColumnIndex("trainTypes.speed")));
					if(InfoCenter.findTrainById(train.getTrainId()) == null){
						_trains.add(train);					
					}
					train.setTypeInfo(cursor.getInt(cursor.getColumnIndex("trainTypes.speed")), cursor.getInt(cursor.getColumnIndex("trainTypes.maxCars")),	cursor.getInt(cursor.getColumnIndex("trainTypes.purchasePrice")), cursor.getInt(cursor.getColumnIndex("trainTypes.resalePrice")), cursor.getInt(cursor.getColumnIndex("trainTypes.fuelEfficiency")), cursor.getString(cursor.getColumnIndex("trainTypes.trainName")));
				}while(cursor.moveToNext());
			}
  		}
		
		dbConnection.close();
		
		//Load all cargo
		selectQuery = "SELECT * FROM cargo";
		cursor = dbConnection.getDb().rawQuery(selectQuery, null);
		//Loop through the cursor and add all rows to the train ArrayList
		if(cursor.moveToFirst())
		{
			do{
				do{
					Cargo cargo = new Cargo(BoardLayers.CARGO, Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));
	 				cargo.setCargoId(Integer.parseInt(cursor.getString(0)));
	 				if(InfoCenter.findCargoById(cargo.getCargoId()) == null){
	 					_cargo.add(cargo);					
	 				}
	 			}while(cursor.moveToNext());
			}while(cursor.moveToNext());
			
		}
		dbConnection.close();
		
		//Load all cars
		selectQuery = "SELECT * FROM cars";
		cursor = dbConnection.getDb().rawQuery(selectQuery, null);
		//Loop through the cursor and add all rows to the train ArrayList
		if(cursor.moveToFirst())
		{
			do{
				int carType = Integer.parseInt(cursor.getString(1));
				int imageId = getCarImageId(carType);
				Car car = new Car(BoardLayers.CAR, Integer.parseInt(cursor.getString(1)), imageId, Integer.parseInt(cursor.getString(2)));
				car.setCarId(Integer.parseInt(cursor.getString(0)));
				if(InfoCenter.findCarById(car.getCarId()) == null){
					_cars.add(car);
				}
				if(car.getTrainId() > 0){
					Train train = findTrainById(car.getTrainId());
					if(train != null){
						train.addAttachedCar(car);
					}
				}
			}while(cursor.moveToNext());
		}
		dbConnection.close();
		
		//Load all passengers
		selectQuery = "SELECT * FROM passengers";
		cursor = dbConnection.getDb().rawQuery(selectQuery, null);
		//Loop through the cursor and add all rows to the train ArrayList
		if(cursor.moveToFirst())
		{
			do{
				int passengerID = Integer.parseInt(cursor.getString(0));
				int seatNo = Integer.parseInt(cursor.getString(1));
				int pcarId = Integer.parseInt(cursor.getString(2));
				int imageId = Integer.parseInt(cursor.getString(3));
				String name = cursor.getString(4);
				int destinationCityId = Integer.parseInt(cursor.getString(5));
				int price = Integer.parseInt(cursor.getString(6));
				Passenger passenger = new Passenger(BoardLayers.PASSENGER, imageId, seatNo, pcarId, name, destinationCityId, price);
				passenger.setPassengerId(passengerID);
				if(InfoCenter.findPassengerById(passenger.getPassengerId()) == null){
					_passengers.add(passenger);
				}
			}while(cursor.moveToNext());
		}
		dbConnection.close();
		
		for(Train train : _trains)
		{
			loadTransit(train);
		}
	}
	
	public static void loadTransit(Train train)
	{
		//Load all passengers
		String selectQuery = "SELECT * FROM transit WHERE trainID = " + train.getTrainId();
		Cursor cursor = dbConnection.getDb().rawQuery(selectQuery, null);
		ArrayList<City> cityPath = new ArrayList<City>();
		//Loop through the cursor and add all rows to the train ArrayList
		if(cursor.moveToFirst())
		{
			do{
				int pathIndex = Integer.parseInt(cursor.getString(4));
				if(pathIndex == 1)
				{
					cityPath.add(findCityById(Integer.parseInt(cursor.getString(2))));
					cityPath.add(findCityById(Integer.parseInt(cursor.getString(3))));
				}
				else
				{
					cityPath.add(findCityById(Integer.parseInt(cursor.getString(3))));
				}
			}while(cursor.moveToNext());
		}
		train.setCityPath(cityPath);
		if(cityPath.size() != 0)
		{
			train.setCurrentCityId(cityPath.get(0).getCityId());
			train.setDestinationCityId(cityPath.get(1).getCityId());
		}
		
		train.updatePathSegment();
		dbConnection.close();
	}
	
	public static int getTrainImageId(int trainType)
	{
		switch(trainType)
		{
			case 1: return R.drawable.steam_engine1;
			case 2: return R.drawable.diesel_engine;
			case 3: return R.drawable.train_maglev;
			default: return R.drawable.steam_engine1;
		}
	}
	
	public static int getCargoImageId(int cargoType)
	{
		switch(cargoType)
		{
			case 1: return R.drawable.passenger_car1;
			default: return R.drawable.passenger_car2;
		}
	}
	
	public static int getCityImageId(int cityLevel)
	{
		switch(cityLevel)
		{
			case 1: return R.drawable.city1;
			case 2: return R.drawable.city2;
			case 3: return R.drawable.city3;
			case 4: return R.drawable.city4;
			case 5: return R.drawable.city5;
			default: return R.drawable.city1;
		}
	}
	
	public static int getPassengerImageId(int passengerType)
	{
		switch(passengerType)
		{
			case 1: return R.drawable.passenger_car1;
			default: return R.drawable.passenger_car2;
		}
	}
	
	public static int getCarImageId(int carType)
	{
		switch(carType)
		{
			case 1: return R.drawable.cargo_car1;
			case 2: return R.drawable.cargo_car2;
			case 3: return R.drawable.cargo_car3;
			case 4: return R.drawable.passenger_car1;
			case 5: return R.drawable.passenger_car2;
			case 6: return R.drawable.passenger_car3; 
			default: return R.drawable.passenger_car1;
		}
	}
	
	public static int getCarSideviewImageId(int carType)
	{
		switch(carType)
		{
			case 1: return R.drawable.cargo_car1_sideview;
			case 2: return R.drawable.cargo_car2_sideview;
			case 3: return R.drawable.cargo_car3_sideview;
			case 4: return R.drawable.passenger_car1_sideview;
			case 5: return R.drawable.passenger_car2_sideview;
			case 6: return R.drawable.passenger_car3_sideview;
			default: return R.drawable.passenger_car1_sideview;
		}
	}
	
	public static ArrayList<Car> carsOnTrain(int trainId)
	{
		ArrayList<Car> cars = new ArrayList<Car>();
		for(Car car : _cars)
		{
			if(car.getTrainId() == trainId)
			{
				cars.add(car);
			}
		}
		return cars;
	}
	
	public static ArrayList<Cargo> cargoInCar(int carId)
	{
		ArrayList<Cargo> cargo = new ArrayList<Cargo>();
		for(Cargo tmp : _cargo)
		{
			if(tmp.getCarId() == carId)
			{
				cargo.add(tmp);
			}
		}
		
		return cargo;
	}
	
	public static ArrayList<Passenger> passengersInCar(int carId)
	{
		ArrayList<Passenger> passengers = new ArrayList<Passenger>();
		for(Passenger tmp: _passengers)
		{
			if(tmp.getPCarId() == carId)
			{
				passengers.add(tmp);
			}
		}
		
		return passengers;
	}
	
	public static Database getDbConnection()
	{
		return dbConnection;
	}
	
	public static boolean getAudioStatus()
	{
		if(_user.getAudioOn() == 1){
			return true;
		}
		return false;
	}
	
	public static void setAudioStatus(boolean audio)
	{
		if(audio){
			_user.setAudioStatus(1);
		}
		else{
			_user.setAudioStatus(0);
		}
		updateUser();
	}
	
	public static ArrayList<Train> numberOfTrainsInCity(int cityId){
        City city = findCityById(cityId);
        ArrayList<Train> trainsInCity = new ArrayList<Train>();
        if(city == null){ return trainsInCity; }
        for(Train tmp: _trains){
        	if(tmp.getCurrentCityId() == city.getCityId() && tmp.getDestinationCityId() == 0)
        	{
        		trainsInCity.add(tmp);
        	}
        }
        return trainsInCity;
	}

	public static ArrayList<City> getAccessibleCities(){
        ArrayList<City> accessibleCities = new ArrayList<City>();
        for(City city: _cities){
                if(city.isAccessible()){
                        accessibleCities.add(city);
                }
        }
        return accessibleCities;
	}

	//This method will remove passengers from the station, when leaving the station view
	public static void removeStationPassengers(){
	    Passenger p = null;
	    while(true)
	    {
	            for(Passenger tmp: _passengers){
	                    if(tmp.getPCarId() == 0){
	                            p=tmp;
	                            break;
	                    }
	            }
	            if(p != null)
	            {
	                    delete(p, BoardLayers.PASSENGER);
	                    p = null;
	            }
	            else
	                    break;
	    }
	}

	//This method will remove cargo from the station, when leaving the station view
	public static void removeStationCargo(){
		Cargo c = null;
        while(true)
        {
	        for(Cargo tmp: _cargo){
	                if(tmp.getCarId() == 0){
	                        c=tmp;
	                        break;
	                }
	        }
	        if(c != null)
	        {
	                delete(c, BoardLayers.CARGO);
	                c = null;
	        }
	        else
	        	break;
        }       
    }
	
	/*public static void printTrains(){
		for(Train t: _trains){
			t.printTrain();
		}
	}
	
	public static void printCities(){
		for(City t: _cities){
			t.printCity();
		}
	}
	
	public static void printRails(){
		for(Rail t: _rails){
			t.printRail();
		}
	}
	
	public static void printCargo(){
		for(Cargo t: _cargo){
			t.printCargo();
		}
	}
	
	public static void printPassengers(){
		for(Passenger t: _passengers){
			t.printPassenger();
		}
	}
	
	public static void printUser(){
		_user.printUser();
	}*/
	

	public static int getNumCarItems(int carId) {
		int numItems = 0;
		for(Cargo cargo : _cargo)
		{
			if(cargo.getCarId() == carId)
			{
				numItems++;
			}
		}
		for(Passenger passenger : _passengers)
		{
			if(passenger.getPCarId() == carId)
			{
				numItems++;
			}
		}
		
		return numItems;
	}
	public static void clearArrayLists(){
		_trains = new ArrayList<Train>();
		_rails = new ArrayList<Rail>();
		_cargo = new ArrayList<Cargo>();
		_cities = new ArrayList<City>();
		_cars = new ArrayList<Car>();
		_passengers = new ArrayList<Passenger>();
		dbConnection = new Database();
		_user = null;
		_user = createUser();
		loadAll();
		removeStationPassengers();
		removeStationCargo();
	}
}
