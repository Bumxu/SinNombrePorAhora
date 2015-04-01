package com.bumxu.commercials;

public class Commercial {
	private String id, address;
	private float latitude, longitude, price, area;
	private boolean approximate;

	public Commercial(String id, float latitude, float longitude, boolean approximate) {
		this.id        = id;
		this.latitude  = latitude;
		this.longitude = longitude;
		this.address   = "";
		this.area      = 0;
		this.price     = 0;
		
		this.approximate = approximate;
	}
	
	public Commercial(String id, float latitude, float longitude, boolean approximate, String address, float area, float price) {
		this.id        = id;
		this.latitude  = latitude;
		this.longitude = longitude;
		this.address   = Character.toUpperCase(address.charAt(0)) + address.substring(1);
		this.area      = area;
		this.price     = price;
		
		this.approximate = approximate;
	}

	@Override
	public String toString() {
		return (approximate ? "~ " : "") + address + " (" + id + ") - " + price + "€ - " + area + "m² -> (" + latitude + ", " + longitude + ")";
	}
	
	public boolean isApproximate() {
		return approximate;
	}

	public void setApproximate(boolean approximate) {
		this.approximate = approximate;
	}


	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = Character.toUpperCase(address.charAt(0)) + address.substring(1);
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getArea() {
		return area;
	}

	public void setArea(float area) {
		this.area = area;
	}
	
}