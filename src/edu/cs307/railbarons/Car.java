package edu.cs307.railbarons;

public class Car extends RailBaronsDrawable {
	private int carId = 0;
	private int carType = 0;
	private int imageId = 0;
	private int trainId = 0;
	
	public Car(BoardLayers layer, int carType, int imageId, int trainId){
		super(layer, imageId);
		this.carType = carType;
		this.imageId = imageId;
		this.trainId = trainId;
	}

	@Override
	public void calculatePosition() {
		// TODO Auto-generated method stub
		
	}
	
	public int getCarId(){
		return carId;
	}
	
	public int getImageId(){
		return imageId;
	}
	
	public int getCarType(){
		return carType;
	}

	public int getTrainId(){
		return trainId;
	}
	
	public void setCarId(int id){
		carId = id;
	}
	public void setTrainId(int id){
		trainId=id;
		InfoCenter.update(this, BoardLayers.CAR);
	}

}
