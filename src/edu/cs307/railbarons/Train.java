package edu.cs307.railbarons;

import java.util.ArrayList;

public class Train extends RailBaronsDrawable {
	private int trainId  = 0;
	private int trainType = 0;
	private int milesTraveled = 0;
	private int currentCityId = 0;
	private int destinationCityId = 0;
	private int imageId = 0;
	private int pathIndex = 0;
	private long departureTime = 0;
	private int speed = 0;
	private int maxCars = 0;
	private int purchasePrice = 0;
	private int resalePrice = 0;
	private int fuelEfficiency = 0;
	private String name  = "";
	private ArrayList<Car> attachedCars;
	private ArrayList<City> cityPath;
	private long eta;
	private Rail travelRail;
	
	public Train(BoardLayers layer, int trainType, int imageId, int milesTraveled, int currentCityId, int destinationCityId, int pathIndex, long departureTime){
		super(layer, imageId);
		this.trainType = trainType;
		this.imageId = imageId;
		this.milesTraveled = milesTraveled;
		this.currentCityId = currentCityId;
		setDestinationCityId(destinationCityId);
		this.pathIndex = pathIndex;
		this.attachedCars = new ArrayList<Car>();
		// When the train is not traveling, the departureTime is 0.
		setDepartureTime(departureTime);
		this.cityPath = new ArrayList<City>();
	}

	public void calculateETA() {
		if( departureTime<=0 || destinationCityId<=0 || travelRail==null || speed==0 ) {
			eta = 0;
		} else {
			eta = departureTime + (travelRail.getDistance() / (speed)) * 1000L;
		}
	}
	
	@Override
	public void calculatePosition() {
		updatePathSegment();

		City curr = InfoCenter.findCityById(currentCityId);
		City dest = InfoCenter.findCityById(destinationCityId);
		// time elapse calculations
		long newTime = System.currentTimeMillis();
		long deltaTime = newTime - departureTime; // convert milli to seconds	
		
		// distance calculations
		float deltaX = dest.getPosX() - curr.getPosX();
		float deltaY = dest.getPosY() - curr.getPosY();
		double totalTime = eta - departureTime;
		
		// find Positions
		float x = (float)(deltaX * deltaTime / totalTime) + curr.getPosX();
		float y = (float)(deltaY * deltaTime / totalTime) + curr.getPosY();
		this.setPositionX((int)x);
		this.setPositionY((int)y);
	}
	
	public int getPathIndex(){
		return pathIndex;
	}
	
	public int getTrainId(){
		return trainId;
	}
	
	public int getImageId(){
		return imageId;
	}
	
	public int getTrainType(){
		return trainType;
	}
	
	public int getMilesTraveled(){
		return milesTraveled;
	}
	
	public int getCurrentCityId(){
		return currentCityId;
	}
	
	public ArrayList<City> getCityPath(){
		return cityPath;
	}
	
	public void setCityPath(ArrayList<City> city){
		cityPath = city;
	}
	
	public void loadDepartureTime(long departure){
		departureTime = departure;
	}
	
	public ArrayList<Car> getAttachedCars(){
		return attachedCars;
	}
	
	public void addAttachedCar(Car c){
		attachedCars.add(c);
	}
	
	public void removeAttachedCar(Car c){
		attachedCars.remove(c);
	}
	
	public int getDestinationCityId(){
		return destinationCityId;
	}
	
	public long getDepartureTime(){
		return departureTime;
	}
	
	public void setDepartureTime(){
		departureTime = System.currentTimeMillis();
		InfoCenter.update(this, BoardLayers.TRAIN);
		calculateETA();
	}
	
	public void setDepartureTime(long time) {
		departureTime = time;
		InfoCenter.update(this, BoardLayers.TRAIN);
		calculateETA();
	}
	
	public void clearDepartureTime()
	{
		departureTime = 0;
	}
	
	public void setTrainId(int id){
		trainId = id;
	}
	
	public void setDestinationCityId(int id){
		this.destinationCityId = id;
		InfoCenter.update(this, BoardLayers.TRAIN);
		if( id==0 ) { travelRail=null; return; }
		City c = InfoCenter.findCityById(currentCityId);
		City d = InfoCenter.findCityById(destinationCityId);
		for(Rail r:c.getRails()) {
			if(r.getCity1().equals(d) || r.getCity2().equals(d)) {
				travelRail = r;
				break;
			}
		}
	}
	
	public void setCurrentCityId(int id){
		this.currentCityId = id;
		InfoCenter.update(this, BoardLayers.TRAIN);
	}
	
	public void setTypeInfo(int speed, int maxCars, int purchasePrice, int resalePrice, int fuelEfficiency, String name){
		this.speed = speed;
		this.maxCars = maxCars;
		this.purchasePrice = purchasePrice;
		this.resalePrice = resalePrice;
		this.fuelEfficiency = fuelEfficiency;
		this.name = name;
		InfoCenter.update(this, BoardLayers.TRAIN);
	}
	
	public int getSpeed(){
		return speed;
	}
	
	public int getMaxCars(){
		return maxCars;
	}
	
	public int getPurchasePrice(){
		return purchasePrice;
	}
	
	public int getResalePrice(){
		return resalePrice;
	}
	
	public int getFuelEfficiency(){
		return fuelEfficiency;
	}
	
	public String getName(){
		return name;
	}
	
	public void incrementPathIndex(){
		pathIndex++;
	}
	
	public void setPathIndex(int pathIndex)
	{
		this.pathIndex = pathIndex;
		InfoCenter.update(this, BoardLayers.TRAIN);
	}
	
	/*public void printTrain(){
		System.out.println("Train: ID=" + trainId);
		System.out.println("trainType: " + trainType);
		System.out.println("Miles Traveled: " + milesTraveled);
		System.out.println("CurrentCityId= " + currentCityId);
		System.out.println("DestinationCityId= " + destinationCityId);
		System.out.println("PathIndex= " + pathIndex);
		System.out.println("Departure Time=" + departureTime);
	}*/

	//Current city needs to be updated before calling this
	public void checkCargoDropOff() {
		System.out.println("Current City: " + InfoCenter.findCityById(currentCityId).getCityName());
		ArrayList<Cargo> cargo = InfoCenter.getCargo();
		ArrayList<Cargo> cargoToBeRemoved = new ArrayList<Cargo>();
		for (Car car:attachedCars) {
			for (Cargo temp:cargo) {
				if ((temp.getCarId() == car.getCarId()) && (temp.getDestinationCityId() == currentCityId)) {
					cargoToBeRemoved.add(temp);
				}
			}
		}
		
		for(Cargo removedCargo : cargoToBeRemoved)
		{
			InfoCenter.delete(removedCargo, BoardLayers.CARGO);
			System.out.println("Trying to remove cargo that is going to: " + InfoCenter.findCityById(removedCargo.getDestinationCityId()).getCityName());
		}
	}

	//Current city needs to be updated before calling this
	public void checkPassengerDropOff() {
		ArrayList<Passenger> passengers = InfoCenter.getPassengers();
		System.out.println("Current City: " + InfoCenter.findCityById(currentCityId).getCityName());
		ArrayList<Passenger> passengerToBeRemoved = new ArrayList<Passenger>();
		for (Car car:attachedCars) {
			for (Passenger temp:passengers) {
				if ((temp.getPCarId() == car.getCarId()) && (temp.getDestinationCityId() == currentCityId)) {
					passengerToBeRemoved.add(temp);
				}
			}
		}
		
		for(Passenger removedPassenger : passengerToBeRemoved)
		{
			InfoCenter.delete(removedPassenger, BoardLayers.PASSENGER);
			System.out.println("Trying to remove passenger that is going to: " + InfoCenter.findCityById(removedPassenger.getDestinationCityId()).getCityName());
		}
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		calculateETA();
		InfoCenter.update(this, BoardLayers.TRAIN);
	}
	
	public void updatePathSegment() {
		long now = System.currentTimeMillis();
		if(eta!=0L) {
			if(now >= eta) {
				InfoCenter.addPathIndex(this);
				updatePathSegment();
			}
		}
	}
}
