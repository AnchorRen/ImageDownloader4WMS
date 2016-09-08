package com.lmars.crawler.picture;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.lmars.crawler.utils.ImageDownloader;

public class WMSImagesDownloader {

	//private static String imageUrl = "http://mrdata.usgs.gov/services/ngs?request=GetMap&service=wms&VERSION=1.3.0&CRS=EPSG:4326&WIDTH=400&HEIGHT=200&LAYERS=National_Geochemical_Survey&BBOX=24.0,-165.0,73.0,-66.0&TRANSPARENT=TRUE&STYLES=&FORMAT=image/png";
		//private static String imageUrl = "http://sdf.ndbc.noaa.gov/wms/?request=getmap&service=wms&wmtver=1.1.1&srs=epsg:4326&width=400&height=200&layers=hfradar&bbox=-180.0,-90.0,180.0,90.0&transparent=true&styles=&format=png&version=1.1.1&".toLowerCase();
		//private static String imageUrl = "http://maps.eatlas.org.au/maps/ows?request=GetMap&service=wms&VERSION=1.3.0&CRS=EPSG:4326&WIDTH=400&HEIGHT=200&LAYERS=TS_TSRA_SLUP-2010:Badu-Contours_Major&BBOX=-10.181441935538913,142.08459256480822,-10.059595082094303,142.19173697873777&TRANSPARENT=TRUE&STYLES=&FORMAT=image/png";
		//private static String imageUrl = "http://mrdata.usgs.gov/services/mi?request=GetMap&service=wms&VERSION=1.3.0&CRS=EPSG:4326&WIDTH=400&HEIGHT=200&LAYERS=age-low&BBOX=41.6,-90.5,48.3,-82.1&TRANSPARENT=TRUE&STYLES=&FORMAT=image/png";
		private static String imageUrl = "http://webmap.ornl.gov/ogcbroker/wms?request=GetMap&service=wms&WMTVER=1.1.1&SRS=EPSG:4326&WIDTH=256&HEIGHT=256&LAYERS=571_10&BBOX=-124.5,25.0,-67.0,49.0&TRANSPARENT=TRUE&STYLES=&FORMAT=image/png";
		
		private static String imagePath = "E:\\Images\\4\\";
		private static int level = 4; 
		private static int levelIndex = 1;
		
		public static void main(String[] args) throws ClientProtocolException, IOException {
			downloadImages(imageUrl, imagePath, "1", level);
		}
		
		private static void downloadImages(String imageUrl,String imagePath,String imageName,int level) throws ClientProtocolException, IOException {
			
			if(level<=0)
				return;
			
			ImageDownloader downloader = new ImageDownloader();
			downloader.download(imageUrl, imagePath,imageName+".png");
			//downloader.download(getMaxImageUrl(imageUrl), imagePath,imageName+"Max.png");
			System.out.println("------------------LEVEL "+level+" ------------------");
			String [] strs=null;
			if(imageUrl.contains("BBOX")){
				strs = imageUrl.split("BBOX=");
			}else if(imageUrl.contains("bbox")){
				strs = imageUrl.split("bbox=");
			}
			String boundingBox = strs[1].split("&")[0];
			String[] box = boundingBox.split(",");
			double south = Double.parseDouble(box[0]);
			double west = Double.parseDouble(box[1]);
			double north = Double.parseDouble(box[2]);
			double east = Double.parseDouble(box[3]);
			
			double midLongtitude =(double) ((east + west)/2.0);
			double midLatitude = (double) ((north + south)/2.0);
			
			String pic1 = midLatitude+","+west +","+ north +","+ midLongtitude;
			String pic2 = midLatitude +"," + midLongtitude+"," + north +"," + east;
			String pic3 = south + "," + west +"," +midLatitude +"," + midLongtitude;
			String pic4 = south + "," +midLongtitude +"," +midLatitude+"," +east;
			String subString = imageUrl.substring(imageUrl.toLowerCase().indexOf("transparent")-1);
			String pic1Url =strs[0]+"bbox="+pic1+subString;
			String pic2Url =strs[0]+"bbox="+pic2+subString;
			String pic3Url =strs[0]+"bbox="+pic3+subString;
			String pic4Url =strs[0]+"bbox="+pic4+subString;
			
			level = --level;
			downloadImages(pic1Url, imagePath,imageName+"1",level);
			downloadImages(pic2Url, imagePath,imageName+"2",level);
			downloadImages(pic3Url, imagePath,imageName+"3",level);
			downloadImages(pic4Url, imagePath,imageName+"4",level);
			//levelIndex++;
		}
		
		private static String getMaxImageUrl(String url){
			if(url.contains("width=256&height=256")){
				String[] strs = url.split("width=256&height=256");
				return strs[0]+"width=512&height=512"+strs[1];
			} else {
				String[] strs = url.split("WIDTH=256&HEIGHT=256");
				return strs[0]+"WIDTH=512&HEIGHT=512"+strs[1];
			}
		}
}
