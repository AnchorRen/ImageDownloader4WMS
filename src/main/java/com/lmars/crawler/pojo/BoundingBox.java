package com.lmars.crawler.pojo;
/**
 * WMS 图层范围参数POJO类
 * @author REN
 * @date 2016年6月11日 上午11:16:23
 */
public class BoundingBox {

	private String crs;   //坐标系统
	private double south; //南纬
	private double north; //北纬
	private double east;  //东经
	private double west;  //西经

	public String getCrs() {
		return crs;
	}

	public void setCrs(String crs) {
		this.crs = crs;
	}

	public double getSouth() {
		return south;
	}

	public void setSouth(double south) {
		this.south = south;
	}

	public double getNorth() {
		return north;
	}

	public void setNorth(double north) {
		this.north = north;
	}

	public double getEast() {
		return east;
	}

	public void setEast(double east) {
		this.east = east;
	}

	public double getWest() {
		return west;
	}

	public void setWest(double west) {
		this.west = west;
	}

	@Override
	public String toString() {
		return "BoundBox [crs=" + crs + ", south=" + south + ", north=" + north + ", east=" + east + ", west=" + west
				+ "]";
	}

}
