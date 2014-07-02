package edu.cs307.railbarons;

public class User {
	private int userId = 0;
	private int money = 0;
	private int totalFuelPrice = 0;
	private int hoursPlayed = 0;
	private int audioOn = 0;
	private int cargoTrans = 0;
	private int passengerTrans = 0;
	
	public User(int money, int totalFuelPrice, int hoursPlayed, int audioOn, int cargoTrans, int passengerTrans){
		this.money = money;
		this.totalFuelPrice = totalFuelPrice;
		this.hoursPlayed = hoursPlayed;
		this.audioOn = audioOn;
		this.cargoTrans = cargoTrans;
		this.passengerTrans = passengerTrans;
	}
	
	public int getMoney(){
		return money;
	}
	
	public void addMoney(int total){
		money += total;
	}
	
	public void removeMoney(int total){
		money -= total;
	}
	
	public void addCargo(){
		cargoTrans++;
		InfoCenter.updateUser();
	}
	
	public void addPassenger(){
		passengerTrans++;
		InfoCenter.updateUser();
	}
	
	public int getCargo(){
		return cargoTrans;
	}
	
	public int getPassengers(){
		return passengerTrans;
	}
	
	public int getTotalFuelPrice(){
		return totalFuelPrice;
	}
	
	public int getHoursPlayed(){
		return hoursPlayed;
	}
	
	public int getAudioOn(){
		return audioOn;
	}
	
	public int getUserId(){
		return userId;
	}
	
	public void setAudioStatus(int status){
		audioOn = status;
		InfoCenter.updateUser();
	}
	
	public void setUserId(int id){
		userId = id;
	}
	
	/*public void printUser(){
		System.out.println("User: ID=" + userId);
		System.out.println("Money: " + money);
		System.out.println("totalFuelPrice: " + totalFuelPrice);
		System.out.println("hoursPlayed: " + hoursPlayed);
		System.out.println("AudioOn= " + audioOn);
		System.out.println("CargoTrans= " + cargoTrans);
		System.out.println("PassengerTrans= " + passengerTrans);
	}*/

}