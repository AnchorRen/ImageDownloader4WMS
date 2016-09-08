package com.lmars.crawler.pojo;

public class Downloader {

	private String url;
	private String fileName;
	private String path;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return "Downloader [url=" + url + ", fileName=" + fileName + ", path=" + path + "]";
	}
	
	
}
