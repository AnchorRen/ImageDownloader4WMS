package com.lmars.crawler.others;

import java.util.List;

import org.junit.Test;

import com.lmars.crawler.picture.WMSDownloader;
import com.lmars.crawler.pojo.BoundingBox;
import com.lmars.crawler.pojo.WMSParams;

public class Others {

	@Test
	public void divide(){
		
		System.out.println((int)Math.floor(3.56778/1.2345));
	}
	
	@Test
	public void getBoxs(){
		
	/*	WMSParams param = WMSDownloader.getInitialBoundingBox("http://glims.colorado.edu/cgi-bin/glims_ogc?request=GetMap&service=wms&WMTVER=1.1.1&SRS=EPSG:4326&WIDTH=400&HEIGHT=200&LAYERS=BLUE_MARBLE&BBOX=-180.0,-90.0,180.0,90.0&TRANSPARENT=TRUE&STYLES=&FORMAT=image/png");
		
		System.out.println(param);
		System.out.println("-------------");
		
		List<WMSParams> boxs = WMSDownloader.getFirstLevelBoxs(param);
		for (WMSParams boundingBox : boxs) {
			System.out.println(WMSDownloader.getUrlByWMSParams(boundingBox, 256, 256));
			
			List<WMSParams> fourChildURLs = WMSDownloader.getFourChildURLs(boundingBox);
			for (WMSParams wmsParams : fourChildURLs) {
				
				System.out.println(WMSDownloader.getUrlByWMSParams(wmsParams, 256, 256));
			}
			System.out.println("-------------------------");
			
		}*/
	}
}
