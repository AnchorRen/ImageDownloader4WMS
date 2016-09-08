package com.lmars.crawler.pojo;

public class UrlParams {

	private int layerId; //图层id,生成URL参数用
	private int id;  //用于生成文件夹加名和文件名
	private String mapServerUrl; 
	private String fileName;
	private InitialExtends initialExtends;
	
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getLayerId() {
		return layerId;
	}
	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMapServerUrl() {
		return mapServerUrl;
	}
	public void setMapServerUrl(String mapServerUrl) {
		this.mapServerUrl = mapServerUrl;
	}
	public InitialExtends getInitialExtends() {
		return initialExtends;
	}
	public void setInitialExtends(InitialExtends initialExtends) {
		this.initialExtends = initialExtends;
	}
	@Override
	public String toString() {
		return "UrlParams [layerId=" + layerId + ", id=" + id + ", mapServerUrl=" + mapServerUrl + ", fileName="
				+ fileName + ", initialExtends=" + initialExtends + "]";
	}
	
	
}
