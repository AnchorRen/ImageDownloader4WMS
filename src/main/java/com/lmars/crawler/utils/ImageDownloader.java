package com.lmars.crawler.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * HttpClient 图片下载工具类
 * 
 * @author REN
 *
 */
public class ImageDownloader {

	private static final String USER_AGENT = "Mozilla/5.0 Firefox/26.0";

	private static Logger logger = LoggerFactory.getLogger(ImageDownloader.class);

	private static final int TIMEOUT_SECONDS = 60;

	private static final int POOL_SIZE = 200;

	private static CloseableHttpClient httpclient;
	
	/**
	 * 
	 * 根据图片地址下载图片，并以指定名称存储到指定路径. 下载前会进行HttpCient
	 * 的初始化工作。下载完对HttpClient对象进行销毁。
	 * 
	 * @param imageUrl 图片地址
	 * @param imagePath 存储电脑路径
	 * @param imageName 图片名字
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void download(String imageUrl,String imagePath,String imageName) throws ClientProtocolException, IOException{

			initApacheHttpClient();
			fetchContent(imageUrl,imagePath,imageName);
			destroyApacheHttpClient();
	}

	/**
	 * 初始化HttpClient设置
	 */
	private static void initApacheHttpClient() {
		// 设置全局请求配置
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_SECONDS * 1000)
				.setConnectTimeout(TIMEOUT_SECONDS * 1000).build();

		// 自定义配置创建HttpClient对象。对UserAgent等进行配置
		httpclient = HttpClients.custom().setUserAgent(USER_AGENT).setMaxConnTotal(POOL_SIZE)
				.setMaxConnPerRoute(POOL_SIZE).setDefaultRequestConfig(defaultRequestConfig).build();
	}

	/**
	 * 销毁HttpClient对象
	 */
	private static void destroyApacheHttpClient() {
		try {
			httpclient.close();
		} catch (IOException e) {
			logger.error("httpclient close fail", e);
		}
	}

	/**
	 * 根据图片地址下载图片，并以指定名称存储到指定路径
	 * @param imageUrl
	 * @param imagePath
	 * @param imageName
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static void fetchContent(String imageUrl,String imagePath,String imageName) throws ClientProtocolException, IOException {

		HttpGet httpget = new HttpGet(imageUrl);
		httpget.setHeader("Referer", "http://www.google.com");

		System.out.println("executing request " + httpget.getURI());
		CloseableHttpResponse response = httpclient.execute(httpget);

		try {
			HttpEntity entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() >= 400) {
				throw new IOException("Got bad response, error code = " + response.getStatusLine().getStatusCode()
						+ " imageUrl: " + imageUrl);
			}
			if (entity != null) {
				InputStream input = entity.getContent();
				File file = new File(imagePath);
				if(! file.exists()) {  
		            file.mkdirs(); 
		        } 
				OutputStream output = new FileOutputStream(new File(imagePath+imageName));
				IOUtils.copy(input, output);
				output.flush();
			}
		} finally {
			response.close();

		}

	}
}
