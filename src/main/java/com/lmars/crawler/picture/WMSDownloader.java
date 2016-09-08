package com.lmars.crawler.picture;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.lmars.crawler.pojo.BoundingBox;
import com.lmars.crawler.pojo.UrlParams;
import com.lmars.crawler.pojo.WMSParams;
import com.lmars.crawler.utils.ImageDownloader;
import com.lmars.crawler.utils.JsonUtils;
import com.lmars.crawler.utils.URLUtil;

/**
 * WMS 图层缩略图下载器
 * 
 * @author REN
 * @date 2016年6月11日 上午11:20:36
 */
public class WMSDownloader {

	private static final int START_ID = 4292; // 数据库中 mapService 起始ID
	private static final int LAYER_NUMS = 100; // 每次读取的数量
	private static final String TBNAME = "tb_wms_layers"; //数据库名称
	private static final String BASEPATH = "F:\\images2"; //图片存储的根目录
	
	
	public static void main(String[] args) throws SQLException {
		List<WMSParams> initialWMSParamsList = getInitialWMSParamsList();
		for (WMSParams wmsParams : initialWMSParamsList) {
			List<WMSParams> firstLevelBoxs = getFirstLevelBoxs(wmsParams); //获取第一级集合
			
			try {
				WMSParams level1 = downloadFirstLevel(firstLevelBoxs);
				download512(level1);
				if(level1 != null){
					WMSParams level2 = downloadFourChildren(level1);
					if(level2 != null){
						download512(level2);
						WMSParams level3 = downloadFourChildren(level2);
						if(level3 != null){
							download512(level3);
							WMSParams level4 = downloadFourChildren(level3);
							if(level4 != null){
								download512(level4);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public static WMSParams downloadFirstLevel(List<WMSParams> list) throws ClientProtocolException, IOException{
		
		if(list != null && list.size() >0){
			
			if(list.size() == 1){
				download256(list.get(0));
				return list.get(0);
			}
			
			Map<String,WMSParams> map = new HashMap<String,WMSParams>();
			for (WMSParams wmsParams : list) {
				
				download256(wmsParams);
				String path = BASEPATH + "\\" + wmsParams.getId()+"\\";
				String imageName = wmsParams.getFileName()+".png";
				map.put(path+imageName, wmsParams);
			}
			
			String maxImage = "";
			Long maxSize = 0L;
			
			for(String file:map.keySet()){
				Long size = getFileSize(file);
				if(size > maxSize){
					maxSize = size;
					maxImage = file;
				}
			}
			
			if(maxImage != ""){
				return map.get(maxImage);
			}
		}
		
		return null;
		
	}
	/**
	 * 通过WMSParams参数，下载下一级四张图片。并返回最大图片对应的参数。
	 * 
	 * @return 四张图片中，最大的一张的UrlParams 参数
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static WMSParams downloadFourChildren(WMSParams params) throws ClientProtocolException, IOException{

		List<WMSParams> fourParams = getFourChildURLs(params);
		Map<String,WMSParams> map = new HashMap<String,WMSParams>();
		
		for (WMSParams urlParams : fourParams) {
			
			download256(urlParams);
			String path = BASEPATH + "\\" + urlParams.getId()+"\\";
			String imageName = urlParams.getFileName()+".png";
			map.put(path+imageName, urlParams);
		}
		
		String maxImage = "";
		Long maxSize = 0L;
		
		for(String file:map.keySet()){
			Long size = getFileSize(file);
			if(size > maxSize){
				maxSize = size;
				maxImage = file;
			}
		}
		
		if(maxImage != ""){
			return map.get(maxImage);
		}
		return null;
		
	}
	
	/**
	 * 根据UrlParam参数  下载 256*256 缩略图
	 * @param params
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void download256(WMSParams params) throws ClientProtocolException, IOException{
		
		String path = BASEPATH + "\\" + params.getId()+"\\";
		System.out.println(path);
		System.out.println(params.getFileName());
		String url = getUrlByWMSParams(params, 256, 256);
		ImageDownloader.download(url, path, params.getFileName()+".png");
		System.out.println("-------------------------------");
	}
	
	/**
	 * 根据UrlParam参数  下载 512*512 缩略图
	 * @param params
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void download512(WMSParams params) throws ClientProtocolException, IOException{
		
		String path = BASEPATH + "\\" + params.getId()+"\\";
		System.out.println(path);
		System.out.println(params.getFileName()+"M");
		String url = getUrlByWMSParams(params, 512, 512);
		ImageDownloader.download(url, path, getMaxName(params.getFileName())+".png");
		System.out.println("---------------------------");
	}
	
	/**
	 * 计算指定文件的长度
	 * 
	 * @param path 文件路径
	 * @return 文件长度，如果文件不存在，则返回-1。
	 */
	public static Long getFileSize(String path){
		File f= new File(path);
		if (f.exists() && f.isFile()){
			return f.length();
		}else{
			System.out.println("file doesn't exist or is not a file");
			return -1L;
		}
	}
	
	private static String getMaxName(String imageName){
		
		return imageName+"M";
	}
	
	/**
	 * 根据父WMSParams参数获取四个子参数
	 * @param params
	 * @return
	 */
	public static List<WMSParams> getFourChildURLs(WMSParams params){
		
		List<WMSParams> list = new ArrayList<WMSParams>();
		BoundingBox box = params.getBoundingBox();
		String url = params.getUrl();
		String fileName = params.getFileName();
		int id = params.getId();
		
		if(box != null){
			
			double south = box.getSouth();
			double north = box.getNorth();
			double west = box.getWest();
			double east = box.getEast();
			
			double midWE = (west + east)/2;
			double midSN = (south + north)/2;
			
			BoundingBox box1 = new BoundingBox();
			BoundingBox box2 = new BoundingBox();
			BoundingBox box3 = new BoundingBox();
			BoundingBox box4 = new BoundingBox();
			
			box1.setWest(west);
			box1.setNorth(north);
			box1.setSouth(midSN);
			box1.setEast(midWE);
			
			box2.setNorth(north);
			box2.setWest(midWE);
			box2.setSouth(midSN);
			box2.setEast(east);
			
			box3.setNorth(midSN);
			box3.setWest(west);
			box3.setSouth(south);
			box3.setEast(midWE);
			
			box4.setNorth(midSN);
			box4.setWest(midWE);
			box4.setSouth(south);
			box4.setEast(east);
			
			WMSParams param1 = new WMSParams();
			param1.setBoundingBox(box1);
			param1.setUrl(url);
			param1.setId(id);
			param1.setFileName(fileName+"_01");
			param1.setUrl(getUrlByWMSParams(param1, 256, 256));
			list.add(param1);
			
			WMSParams param2 = new WMSParams();
			param2.setBoundingBox(box2);
			param2.setUrl(url);
			param2.setId(id);
			param2.setFileName(fileName+"_02");
			param2.setUrl(getUrlByWMSParams(param2, 256, 256));
			list.add(param2);
			
			WMSParams param3 = new WMSParams();
			param3.setBoundingBox(box3);
			param3.setUrl(url);
			param3.setId(id);
			param3.setFileName(fileName+"_03");
			param3.setUrl(getUrlByWMSParams(param3, 256, 256));
			list.add(param3);
			

			WMSParams param4 = new WMSParams();
			param4.setBoundingBox(box4);
			param4.setUrl(url);
			param4.setId(id);
			param4.setFileName(fileName+"_04");
			param4.setUrl(getUrlByWMSParams(param4, 256, 256));
			list.add(param4);
		}
		return list;
	}
	
	/**
	 * 通过WMSParams 参数和width、height构建图片url
	 * @param params WMSParams参数
	 * @param width  图片宽度
	 * @param height 图片高度
	 * @return  拼接后的图片URL
	 */
	public static String getUrlByWMSParams(WMSParams params,int width,int height){
		
		String url = params.getUrl();
		BoundingBox boundingBox = params.getBoundingBox();
		String boxString = boundingBox.getSouth()+","+boundingBox.getWest()+","+boundingBox.getNorth()+","+boundingBox.getEast();
		
		String [] strs=null;
		if(url.contains("BBOX")){
			strs = url.split("BBOX=");
		}else if(url.contains("bbox")){
			strs = url.split("bbox=");
		}
		
		String[] splits = strs[1].split("&");
		
		url = strs[0]+"bbox="+boxString+strs[1].substring(splits[0].length());
		
		url = getImageUrl(url,height,width);
		
		return url;
		
	}
	
	/**
	 * 替换URL中的height、width为指定的高和宽
	 * @param url url
	 * @param height 要替换的高度
	 * @param width 要替换的宽度
	 * @return 替换后的URl
	 */
	private static String getImageUrl(String url,int height,int width){
		if(url.contains("width")){
			String[] split = url.split("width");
			String[] split2 = split[1].split("&");
			
			url = split[0] + "width="+width+ split[1].substring(split2[0].length());
		}else if(url.contains("WIDTH")){
			String[] split = url.split("WIDTH");
			String[] split2 = split[1].split("&");
			
			url = split[0] + "WIDTH="+width+ split[1].substring(split2[0].length());
		}
		
		if(url.contains("height")){
			String[] split = url.split("height");
			String[] split2 = split[1].split("&");
			
			url = split[0] + "height="+height+ split[1].substring(split2[0].length());
		}else if(url.contains("HEIGHT")){
			String[] split = url.split("HEIGHT");
			String[] split2 = split[1].split("&");
			
			url = split[0] + "HEIGHT="+height+ split[1].substring(split2[0].length());
		}
		
		return url;
	}
	
	
	/**
	 * 根据初始WMSParams 获得第一级缩略图的参数集合(进行裁剪)
	 * @param param 
	 * @return
	 */
	public static List<WMSParams> getFirstLevelBoxs(WMSParams param) {

		BoundingBox boundingBox = param.getBoundingBox();
		int id = param.getId();
		List<WMSParams> list = new ArrayList<WMSParams>();

		double south = boundingBox.getSouth();
		double west = boundingBox.getWest();
		double north = boundingBox.getNorth();
		double east = boundingBox.getEast();

		double sn = north - south;
		double we = east - west;

		if (sn >= we) {

			int multiple = (int) Math.floor(sn / we); // 倍数下取整

			double dValue = sn - we * multiple; // 经纬度差值
			// 截去差值
			south = south + dValue / 2;
			north = north - dValue / 2;

			double line = south + we;

			for (int i = 1; i <= multiple; i++) {

				BoundingBox box = new BoundingBox();
				box.setSouth(south);
				box.setNorth(line);
				box.setEast(east);
				box.setWest(west);

				String fileName = id+"_0"+i;
				
				WMSParams params = new WMSParams();
				params.setBoundingBox(box);
				params.setUrl(param.getUrl());
				params.setFileName(fileName);
				params.setId(id);
				params.setUrl(getUrlByWMSParams(params, 256, 256));
				
				list.add(params);
				south = south + we;
				line = line + we;
			}

			return list;

		} else {
			
			int multiple = (int) Math.floor(we / sn); // 倍数下取整

			double dValue = we - sn * multiple; // 经纬度差值
			// 截去差值
			west = west + dValue / 2;
			east = east - dValue / 2;

			double line = west + sn;

			for (int i = 1; i <= multiple; i++) {

				BoundingBox box = new BoundingBox();
				box.setSouth(south);
				box.setNorth(north);
				box.setEast(line);
				box.setWest(west);

				String fileName = id+"_0"+i;
				
				WMSParams params = new WMSParams();
				params.setBoundingBox(box);
				params.setUrl(param.getUrl());
				params.setFileName(fileName);
				params.setId(id);
				params.setUrl(getUrlByWMSParams(params, 256, 256));
				
				list.add(params);
				
				west = west + sn;
				line = line + sn;
			}

			return list;
		}

	}
	
	/**
	 * 获得初始WMsParams集合，boundingBox未经过裁剪的。
	 * @return
	 * @throws SQLException
	 */
	public static List<WMSParams> getInitialWMSParamsList() throws SQLException{
		ResultSet resultSet = URLUtil.getImageUrlSet(START_ID, LAYER_NUMS, TBNAME);
		List<WMSParams> list = new ArrayList<WMSParams>();
		while(resultSet.next()){
			
			String url = resultSet.getString("url");
			int id = resultSet.getInt("id");
			BoundingBox box = getInitialBoundingBox(url);
			
			WMSParams param = new WMSParams();
			param.setBoundingBox(box);
			param.setId(id);
			param.setUrl(url);
			list.add(param);
			
		}
		
		return list;
	}

	/**
	 * 从Url中截取出初始默认的WMSParams.
	 * 
	 * @param url
	 *            数据库中图层图片Url
	 * @return 初始WMSParams
	 */
	public static BoundingBox getInitialBoundingBox(String url) {

		String[] strs = null;
		if (url != null && !url.equals("") && url != "") {
			if (url.contains("BBOX")) {
				strs = url.split("BBOX=");
			} else if (url.contains("bbox")) {
				strs = url.split("bbox=");
			}
			if (strs.length > 0) {

				String boundingBoxStr = strs[1].split("&")[0];
				String[] box = boundingBoxStr.split(",");
				double south = Double.parseDouble(box[0]);
				double west = Double.parseDouble(box[1]);
				double north = Double.parseDouble(box[2]);
				double east = Double.parseDouble(box[3]);

				BoundingBox boundingBox = new BoundingBox();
				boundingBox.setEast(east);
				boundingBox.setNorth(north);
				boundingBox.setSouth(south);
				boundingBox.setWest(west);
				
				return boundingBox;
			}
		}
		return null;
	}
	
	
}
