package com.lmars.crawler.others;

import java.io.File;

public class Url {

	public static void main(String[] args) {


			File f= new File("F:\\images\\2000221\\2000221_01.png");
			if (f.exists() && f.isFile()){

				System.out.println(f.length());
			}else{
				System.out.println("file doesn't exist or is not a file");
			}
		
	}
	
	
}
