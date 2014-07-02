package edu.cs307.railbarons;

public class Cargo extends RailBaronsDrawable {
	private int cargoId = 0;
	private int cargoType = 0;
	private int imageId = 0;
	private int cargoPosition = 0;
	private int carId = 0;
	private int destinationCityId = 0;
	private int price = 0;
	
	public Cargo(BoardLayers layer, int cargoType, int cargoPosition, int carId, int destinationCityId, int price){
		super(layer, R.drawable.cargo);
		this.cargoPosition = cargoPosition;
		this.cargoType = cargoType;
		this.carId = carId;
		this.destinationCityId = destinationCityId;
		this.price = price;
	}

	@Override
	public void calculatePosition() {
		// TODO Auto-generated method stub
		
	}
	
	public int getDestinationCityId(){
		return destinationCityId;
	}
	
	public int getCargoId(){
		return cargoId;
	}
	
	public int getImageId(){
		return imageId;
	}
	
	public int getCargoType(){
		return cargoType;
	}

	public int getCargoPosition(){
		return cargoPosition;
	}
	
	public int getCarId(){
		return carId;
	}
	
	public void setCargoId(int id){
		cargoId = id;
	}
	
	public void setCarId(int id)
	{
		carId = id;
		InfoCenter.update(this, BoardLayers.CARGO);
	}
	
	public int getPrice()
	{
		return price;
	}
	
	/*public void printCargo(){
		System.out.println("Cargo: ID=" + cargoId);
		System.out.println("cargoType: " + cargoType);
		System.out.println("Cargo Position: " + cargoPosition);
		System.out.println("destinationCityId: " + destinationCityId);
		System.out.println("Price: " + price);
	}*/
}
