package com.lmars.crawler.picture;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.lmars.crawler.pojo.InitialExtends;
import com.lmars.crawler.pojo.TaskQuene;
import com.lmars.crawler.pojo.UrlParams;
import com.lmars.crawler.utils.DBCPUtil;
import com.lmars.crawler.utils.ImageDownloader;
import com.lmars.crawler.utils.JsonUtils;
import com.lmars.crawler.utils.URLUtil;

public class MapServiceDownloader {

	private static final int START_ID = 2005568; // 数据库中 mapService 起始ID
	private static final int LAYER_NUMS = 150; // 每次读取的数量
	private static final String TBNAME = "tb_mapservice_layers"; //数据库名称
	private static final String BASEPATH = "E:\\images"; //图片存储的根目录
	public volatile TaskQuene quene = new TaskQuene(); //任务队列
	List<UrlParams> urlParamsList = new LinkedList<UrlParams>();
	
	
	public MapServiceDownloader() throws Exception {
		urlParamsList = getUrlParamsList();
		quene.setQueue(urlParamsList);
		System.out.println("constrctor!");
	}

	public static void main(String[] args) throws Exception {
		
		/*MapServiceDownloader downloader = new MapServiceDownloader();
		downloader.quene.
		List<UrlParams> queue = downloader.quene.getQueue();
		
		for(int i =0;i<5;i++){
			new Thread(){
				public void run(){  
	                while(true){  
	                	UrlParams o;  
	  
	                    //提取队列元素的时候，需要锁住队列  
	                    synchronized(list){  
	                        //当队列长度为0的时候，线程逐个结束  
	                        if(queue.size() == 0){  
	                            break;  
	                        }  
	                        o = queue.get(0) ;  
	                    }  
	                    //这里可以写一些对元素o的操作  
	                    //……  
	                }  
	            }  
	        }.start();  
			}
		}*/
		List<UrlParams> urlParamsList = getUrlParamsList();
		for (UrlParams urlParams : urlParamsList) {
			
			try {
				download256(urlParams);
				download512(urlParams);
				UrlParams level2 = downloadFourChildren(urlParams);
				if(level2 != null){
					download512(level2);
					UrlParams level3 = downloadFourChildren(level2);
					if(level3 != null){
						download512(level3);
						UrlParams level4 = downloadFourChildren(level3);
						if(level4 != null){
							download512(level4);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	/**
	 * 抓取四级缩略图
	 * 
	 * @param urlParams
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void download4Level(UrlParams urlParams) throws ClientProtocolException, IOException{
		
		download256(urlParams);
		download512(urlParams);
		
		UrlParams level2 = downloadFourChildren(urlParams);
		if(level2 != null){
			download512(level2);
			UrlParams level3 = downloadFourChildren(level2);
			if(level3 != null){
				download512(level3);
				UrlParams level4 = downloadFourChildren(level3);
				if(level4 != null){
					download512(level4);
				}
			}
		}
	}
	
	/**
	 * 通过UrlParams参数，下载下一级四张图片。并返回最大图片对应的参数。
	 * 
	 * @return 四张图片中，最大的一张的UrlParams 参数
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static UrlParams downloadFourChildren(UrlParams params) throws ClientProtocolException, IOException{

		List<UrlParams> fourParams = getChildUrlParams(params);
		Map<String,UrlParams> map = new HashMap<String,UrlParams>();
		
		for (UrlParams urlParams : fourParams) {
			
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
	
	/**
	 * 根据UrlParam参数  下载 256*256 缩略图
	 * @param params
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void download256(UrlParams params) throws ClientProtocolException, IOException{
		
		String path = BASEPATH + "\\" + params.getId()+"\\";
		System.out.println(path);
		System.out.println(params.getFileName());
		String url = getUrl(params.getLayerId(), params.getMapServerUrl(), params.getInitialExtends(), 256, 256);
		ImageDownloader.download(url, path, params.getFileName()+".png");
		System.out.println("-------------------------------");
	}
	
	/**
	 * 根据UrlParam参数  下载 512*512 缩略图
	 * @param params
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void download512(UrlParams params) throws ClientProtocolException, IOException{
		
		String path = BASEPATH + "\\" + params.getId()+"\\";
		System.out.println(path);
		System.out.println(params.getFileName()+"M");
		String url = getUrl(params.getLayerId(), params.getMapServerUrl(), params.getInitialExtends(), 512, 512);
		ImageDownloader.download(url, path, getMaxName(params.getFileName())+".png");
		System.out.println("---------------------------");
	}
	
	private static String getMaxName(String imageName){
		
		return imageName+"M";
	}
	
	/**
	 * 获取当前url参数下一级的四张图片的Url参数集合
	 * @param params 当前UrlParams
	 * @return 四张子UrlParams
	 */
	private static List<UrlParams> getChildUrlParams(UrlParams params){
		
		List<UrlParams> list = new ArrayList<UrlParams>();
		
		InitialExtends extend = params.getInitialExtends();
		String fileName = params.getFileName();
		int id = params.getId();
		int layerId = params.getLayerId();
		String mapServerUrl = params.getMapServerUrl();
		
		double xmin = extend.getXmin();
		double xmax = extend.getXmax();
		double ymin = extend.getYmin();
		double ymax = extend.getYmax();
		
		String fileName1 = fileName+"_01";
		String fileName2 = fileName+"_02";
		String fileName3 = fileName+"_03";
		String fileName4 = fileName+"_04";
		
		double xmid = (xmin + xmax)/2;
		double ymid = (ymin + ymax)/2;
		
		InitialExtends extend1 = new InitialExtends();
		InitialExtends extend2 = new InitialExtends();
		InitialExtends extend3 = new InitialExtends();
		InitialExtends extend4 = new InitialExtends();
		
		extend1.setXmin(xmin);
		extend1.setYmin(ymid);
		extend1.setXmax(xmid);
		extend1.setYmax(ymax);
		
		extend2.setXmin(xmid);
		extend2.setYmin(ymid);
		extend2.setXmax(xmax);
		extend2.setYmax(ymax);
		
		extend3.setXmin(xmin);
		extend3.setYmin(ymin);
		extend3.setXmax(xmid);
		extend3.setYmax(ymid);
		
		extend4.setXmin(xmid);
		extend4.setYmin(ymin);
		extend4.setXmax(xmax);
		extend4.setYmax(ymid);
		
		UrlParams child1 = new UrlParams();
		child1.setFileName(fileName1);
		child1.setId(id);
		child1.setInitialExtends(extend1);
		child1.setLayerId(layerId);
		child1.setMapServerUrl(mapServerUrl);
		list.add(child1);
		
		UrlParams child2 = new UrlParams();
		child2.setFileName(fileName2);
		child2.setId(id);
		child2.setInitialExtends(extend2);
		child2.setLayerId(layerId);
		child2.setMapServerUrl(mapServerUrl);
		list.add(child2);
		
		UrlParams child3 = new UrlParams();
		child3.setFileName(fileName3);
		child3.setId(id);
		child3.setInitialExtends(extend3);
		child3.setLayerId(layerId);
		child3.setMapServerUrl(mapServerUrl);
		list.add(child3);
		
		UrlParams child4 = new UrlParams();
		child4.setFileName(fileName4);
		child4.setId(id);
		child4.setInitialExtends(extend4);
		child4.setLayerId(layerId);
		child4.setMapServerUrl(mapServerUrl);
		list.add(child4);
		
		return list;
	}
	
	/**
	 * 获取Url初始参数集合，此集合中保存着拼接各个图层的Url参数的初始值。范围为InitialExtends
	 * @return
	 * @throws Exception
	 */
	public static List<UrlParams> getUrlParamsList() throws Exception{
		ResultSet resultSet = URLUtil.getImageUrlSet(START_ID, LAYER_NUMS, TBNAME);
		List<UrlParams> urlParamsList = new ArrayList<UrlParams>();
		while(resultSet.next()){
			int layerId = resultSet.getInt("layerId"); //URL中用于设置显示的图层
			int id = resultSet.getInt("id"); //用于设置图片保存到的文件夹名
			int mapServiceId = resultSet.getInt("mapserviceId"); //此Id用于从MapService表中查询initialExtends
			String layerUrl = resultSet.getString("layerUrl"); 
			int index = layerUrl.lastIndexOf("/");
			layerUrl = layerUrl.substring(0, index); //mapServer的Url地址
			InitialExtends initialExtends = getExtends(mapServiceId); //初始化范围
			
			UrlParams param = new UrlParams();
			param.setId(id);
			param.setInitialExtends(initialExtends);
			param.setLayerId(layerId);
			param.setMapServerUrl(layerUrl);
			param.setFileName(id+"_01");
			urlParamsList.add(param);
		}
		
		return urlParamsList;
	}
	


	/**
	 * 根据参数手动拼接图片URL地址
	 * ref: http://cloud.insideidaho.org/arcgis/sdk/rest/index.html#/Export_Map/02ss00000062000000/
	 * @param layerId
	 * @param mapServerUrl
	 * @param initialExtends
	 * @param width
	 * @param height
	 * @return
	 */
	private static String getUrl(int layerId,String mapServerUrl,InitialExtends initialExtends,int width,int height){
		
		double xmin = initialExtends.getXmin();
		double ymin = initialExtends.getYmin();
		double xmax = initialExtends.getXmax();
		double ymax = initialExtends.getYmax();
		
		return  mapServerUrl+"/export?bbox="+xmin+","+ymin+","+xmax+","+ymax+"&bboxSR=&layers=show:"+layerId+
				"&layerDefs=&size="+width+","+height+"&imageSR=&format=png&transparent=true&dpi=&time=&layerTimeOptions=&dynamicLayers=&gdbVersion=&mapScale=&f=image";
	}
	
	/**
	 * 根据图层Id获取其InitialExtends
	 * 
	 * @param id
	 *            数据库中MapService表中的主键ID
	 * @return
	 */
	private static InitialExtends getExtends(int id) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = DBCPUtil.getConnection();
			statement = connection.prepareStatement("SELECT * FROM tb_mapservice WHERE id = ?");
			statement.setInt(1, id);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {

				String extendsStr = resultSet.getString("initialExtend");
				InitialExtends initialExtends = JsonUtils.jsonToPojo(extendsStr, InitialExtends.class);
				return initialExtends;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(resultSet != null){
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
