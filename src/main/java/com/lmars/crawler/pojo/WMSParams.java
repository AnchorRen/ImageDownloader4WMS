package com.lmars.crawler.pojo;

public class WMSParams {

	private String url;
	private BoundingBox boundingBox;
	private int id; //用于生成文件夹名和图片命名用；
	private String fileName;

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	@Override
	public String toString() {
		return "WMSParams [url=" + url + ", boundingBox=" + boundingBox + "]";
	}

}
