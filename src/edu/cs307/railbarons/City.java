package edu.cs307.railbarons;

import java.util.ArrayList;

public class City extends RailBaronsDrawable {
	private int cityId  = 0;
	private int cityLevel = 0;
	private int imageId = 0;
	private String cityName = "";
	private int isAccessible = 0;
	private ArrayList<Rail> attachedRails = null;
	
	public City(BoardLayers layer, int cityLevel, int xPos, int yPos, int imageId, String cityName, int isAccessible){
		super(layer, imageId);
		this.cityLevel = cityLevel;
		setPosition(xPos, yPos);
		this.imageId = imageId;
		this.cityName = cityName;
		this.isAccessible = isAccessible;
		this.attachedRails = new ArrayList<Rail>();
	}

	@Override
	public void calculatePosition() {		
	}
	
	public int getCityId(){
		return cityId;
	}
	
	public int getImageId(){
		return imageId;
	}
	
	public int getCityLevel(){
		return cityLevel;
	}
	
	public String getCityName(){
		return cityName;
	}
	
	public int getIsAccessible(){
		return isAccessible;
	}
	
	public ArrayList<Rail> getRails(){
		return attachedRails;
	}
	
	public void addRailToCity(Rail r){
		attachedRails.add(r);
	}
	
	public boolean isAccessible(){
		if(isAccessible == 1){
			return true;
		}
		return false;
	}
	
	public void setCityId(int id){
		cityId = id;
	}
	
	public void setIsAccessible(int tmp){
		isAccessible = tmp;
		InfoCenter.update(this, BoardLayers.CITY);
	}
	
	/*public void printCity(){
		System.out.println("City: ID=" + cityId);
		System.out.println("cityLevel: " + cityLevel);
		System.out.println("CityName: " + cityName);
		System.out.println("IsAccessible: " + isAccessible);
	}*/
}
