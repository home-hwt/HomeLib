package com.guo.androidlib.db;

public class HomeReferenceUtil {
	private static HomeReferenceUtil referenceUtil;
	
	private HomeReferenceUtil(){}
	
	public static HomeReferenceUtil getInstance(){
		if(referenceUtil == null){
			referenceUtil = new HomeReferenceUtil();
		}
		return referenceUtil;
	}
	
}
