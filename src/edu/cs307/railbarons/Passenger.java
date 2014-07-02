package edu.cs307.railbarons;

public class Passenger extends RailBaronsDrawable {
	private int passengerId = 0;
	private int imageId = 0;
	private int seatNo = 0;
	private int pCarId = 0;
	private String name;
	private int destinationCityId = 0;
	private int price = 0;
	
	public Passenger(BoardLayers layer, int imageId, int seatNo, int pCarId, String name, int destinationCityId, int price){
		super(layer, imageId);
		this.imageId = imageId;
		this.seatNo = seatNo;
		this.pCarId = pCarId;
		this.name = name;
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
	
	public int getPassengerId(){
		return passengerId;
	}
	
	public int getImageId(){
		return imageId;
	}
	
	public int getSeatNo(){
		return seatNo;
	}
	
	public int getPCarId(){
		return pCarId;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setPassengerId(int id){
		passengerId = id;
	}
	
	public void setCarId(int id)
	{
		pCarId = id;
		InfoCenter.update(this, BoardLayers.PASSENGER);
	}
	
	public int getPrice()
	{
		return price;
	}
	
	/*public void printPassenger(){
		System.out.println("Passenger: ID=" + passengerId);
		System.out.println("Name: " + name);
		System.out.println("SeatNo: " + seatNo);
		System.out.println("PCarId= " + pCarId);
		System.out.println("destinationCityId: " + destinationCityId);
	}*/
	
}
