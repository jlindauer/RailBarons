package edu.cs307.railbarons;

public class Rail extends RailBaronsDrawable {
	public enum DrawStyle {
		RAILACTIVE, RAILINACTIVE, RAILAVAILABLE, RAILSELECTED;
	}
	
	private int railId  = 0;
	private int imageId = 0;
	private int cityId1  = 0;
	private int cityId2  = 0;
	private int distance = 0;
	private int isVisible = 0;
	private City city1 = null;
	private City city2 = null;
	private DrawStyle drawStyle = DrawStyle.RAILINACTIVE;
	
	public Rail(BoardLayers layer, int imageId, int cityId1, int cityId2, int distance, int isVisible){
		super(layer, imageId);
		this.imageId = imageId;
		this.cityId1 = cityId1;
		this.cityId2 = cityId2;
		this.distance = distance;
		this.city1 = InfoCenter.findCityById(cityId1);
		this.city2 = InfoCenter.findCityById(cityId2);
		this.city1.getRails().add(this);
		this.city2.getRails().add(this);
		if(!city1.isAccessible() && isVisible == 1){
			city1.setIsAccessible(1);
			InfoCenter.update(city1, BoardLayers.CITY);
		}
		if(!city2.isAccessible() && isVisible == 1){
			city2.setIsAccessible(1);
			InfoCenter.update(city2, BoardLayers.CITY);
		}
		this.isVisible = isVisible;
		if(isVisible==1) {
			drawStyle = DrawStyle.RAILACTIVE;
		} else {
			drawStyle = DrawStyle.RAILINACTIVE;
		}
	}

	@Override
	public void calculatePosition() {
		// Empty Function
		// Pass
	}
	
	public void setVisible(int visible) {
		this.isVisible=visible;
		InfoCenter.update(this, BoardLayers.RAIL);
	}
	
	public int getRailId(){
		return railId;
	}
	
	public int getImageId(){
		return imageId;
	}
	
	public int getCityId1(){
		return cityId1;
	}
	
	public int getCityId2(){
		return cityId2;
	}
	
	public City getCity1(){
		return city1;
	}
	
	public City getCity2(){
		return city2;
	}
	
	public int getDistance(){
		return (int)Math.sqrt(Math.pow(city2.getPosition().x-city1.getPosition().x,2) + 
							  Math.pow(city2.getPosition().y-city1.getPosition().y, 2));
	}
	
	public int getIsVisible(){
		return isVisible;
	}
	
	public boolean isVisible(){
		if(isVisible == 1){
			return true;
		}
		return false;
	}
	
	public void setRailId(int id){
		railId = id;
	}
	
	/*public void printRail(){
		System.out.println("Rail: ID=" + railId);
		System.out.println("cityId1: " + cityId1);
		System.out.println("cityId2: " + cityId2);
		System.out.println("distance: " + distance);
	}*/
	
	public DrawStyle getDrawStyle() {
		return drawStyle;
	}
	
	public void setDrawStyle(DrawStyle d) {
		//   We may want to set some conditional checking to
		//   that we are not setting the incorrect draw style
		//   so that we don't draw the rail if the rail isn't seen.
		drawStyle = d;
	}
}
