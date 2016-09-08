package com.lmars.crawler.picture;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.lmars.crawler.utils.ImageDownloader;

public class EsriRESTMapDownloader {

			//private static String imageUrl = "http://cloud.insideidaho.org/arcgis/rest/services/structure/structures/MapServer/export?bbox=-1.3805515062645724E7%2C5524233.517241331%2C-1.159607871095782E7%2C6312628.771867333&bboxSR=&layers=&layerDefs=&size=256,256&imageSR=&format=png&transparent=true&dpi=&time=&layerTimeOptions=&dynamicLayers=&gdbVersion=&mapScale=&f=image";
			//private static String imageUrl = "http://cloud.insideidaho.org/arcgis/rest/services/IGS/Mines_CAT_20160318b/MapServer?f=jsapi";
			//private static String imageUrl = "http://cloud.insideidaho.org/arcgis/rest/services/transportation/roads/MapServer/export?dpi=96&transparent=true&format=png8&bbox=1.307837714151418E7%2C6036343.6557243075%2C-1.2901236203625845E7%2C6112584.90212922&bboxSR=102100&imageSR=102100&size=2048,2048&f=image";
			//private static String imageUrl = "http://cloud.insideidaho.org/arcgis/rest/services/UofI/lapwai_Land/MapServer/export?bbox=-1.3093964968032712E7%2C5800126.404140872%2C-1.2905845841794452E7%2C5881353.649928699&bboxSR=&layers=&layerDefs=&size=256,256&imageSR=&format=png&transparent=true&dpi=&time=&layerTimeOptions=&dynamicLayers=&gdbVersion=&mapScale=&f=image";
			private static String imageUrl = "http://cloud.insideidaho.org/arcgis/rest/services/UofI/wrp_MicaCreek/MapServer/export?bbox=-116.38330577843215%2C47.11480216437605%2C-115.84507721373245%2C47.35051709927307&bboxSR=&layers=&layerDefs=&size=512,512&imageSR=&format=png&transparent=false&dpi=&time=&layerTimeOptions=&dynamicLayers=&gdbVersion=&mapScale=&f=image";
			
			private static String imagePath = "E:\\Images\\EsriREST\\6\\";
			private static int level = 4;
			
			public static void main(String[] args) throws IOException, Exception {
				downloadImages(imageUrl, imagePath, "1", level);
			}
			
			private static void downloadImages(String imageUrl,String imagePath,String imageName,int level) throws Exception, IOException {
				
				if(level<=0)
					return;
				
				ImageDownloader downloader = new ImageDownloader();
				downloader.download(imageUrl, imagePath,imageName+".png");
				downloader.download(getMaxImageUrl512(imageUrl), imagePath,imageName+"Max.png");
				System.out.println("------------------LEVEL "+level+" ------------------");
				String [] strs=null;
				if(imageUrl.contains("BBOX")){
					strs = imageUrl.split("BBOX=");
				}else if(imageUrl.contains("bbox")){
					strs = imageUrl.split("bbox=");
				}
				String boundingBox = strs[1].split("&")[0];
				String[] box = boundingBox.split("%2C");
				double Xmin = Double.parseDouble(box[0]);
				double Ymin = Double.parseDouble(box[1]);
				double Xmax = Double.parseDouble(box[2]);
				double Ymax = Double.parseDouble(box[3]);
				
				double Xmid =(double) ((Xmin + Xmax)/2.0);
				double Ymid = (double) ((Ymin + Ymax)/2.0);
				
				String pic1 = Xmin +"%2C"+Ymid+"%2C"+Xmid+"%2C"+Ymax;
				String pic2 = Xmid +"%2C"+Ymid+"%2C"+Xmax+"%2C"+Ymax;
				String pic3 = Xmin +"%2C"+Ymin+"%2C"+Xmid+"%2C"+Ymid;
				String pic4 = Xmid +"%2C"+Ymin+"%2C"+Xmax+"%2C"+Ymid;
				String subString = imageUrl.substring(imageUrl.indexOf("bboxSR")-1);
				String pic1Url =strs[0]+"bbox="+pic1+subString;
				String pic2Url =strs[0]+"bbox="+pic2+subString;
				String pic3Url =strs[0]+"bbox="+pic3+subString;
				String pic4Url =strs[0]+"bbox="+pic4+subString;
				
				level = --level;
				downloadImages(pic1Url, imagePath+"1\\","1",level);
				downloadImages(pic2Url, imagePath+"2\\","2",level);
				downloadImages(pic3Url, imagePath+"3\\","3",level);
				downloadImages(pic4Url, imagePath+"4\\","4",level);
				
			}
			
			private static String getMaxImageUrl2048(String url){
				if(url.contains("size=2048,2048")){
					String[] strs = url.split("size=2048,2048");
					return strs[0]+"size=4096,4096"+strs[1];
				} else {
					String[] strs = url.split("SIZE=2048,2048");
					return strs[0]+"SIZE=4096,4096"+strs[1];
				}
			}
			
			private static String getMaxImageUrl1024(String url){
				if(url.contains("size=1024,1024")){
					String[] strs = url.split("size=1024,1024");
					return strs[0]+"size=2048,2048"+strs[1];
				} else {
					String[] strs = url.split("SIZE=1024,1024");
					return strs[0]+"SIZE=2048,2048"+strs[1];
				}
			}
			
			private static String getMaxImageUrl256(String url){
				if(url.contains("size=256,256")){
					String[] strs = url.split("size=256,256");
					return strs[0]+"size=512,512"+strs[1];
				} else {
					String[] strs = url.split("SIZE=256,256");
					return strs[0]+"SIZE=512,512"+strs[1];
				}
			}
			private static String getMaxImageUrl512(String url){
				if(url.contains("size=512,512")){
					String[] strs = url.split("size=512,512");
					return strs[0]+"size=1024,1024"+strs[1];
				} else {
					String[] strs = url.split("SIZE=512,512");
					return strs[0]+"SIZE=1024,1024"+strs[1];
				}
			}
}
