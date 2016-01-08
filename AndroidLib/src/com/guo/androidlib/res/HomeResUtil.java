package com.guo.androidlib.res;

import android.content.Context;

public class HomeResUtil {
	private static HomeResUtil homeResUtil;
	
	private HomeResUtil(){}
	
	public static HomeResUtil getInstance(){
		if(homeResUtil == null){
			homeResUtil = new HomeResUtil();
		}
		return homeResUtil;
	}
	
	
	public int getLayoutId(Context context,String layoutName){
		return context.getResources().getIdentifier(layoutName, "layout", context.getPackageName());
	}
	
	
	public int getStringId(Context context,String resName){
		return getResId(context, resName, ResType.STRING);
	}
	
	public int getResId(Context context ,String resName,ResType resType){
		return context.getResources().getIdentifier(resName, resType.toString(), context.getPackageName());
	}
	
	public int getResId(Context context ,String resName,ResType resType,String packageName){
		return context.getResources().getIdentifier(resName, resType.toString(), packageName);
	}
	
	private enum ResType{
		LAYOUT("layout"),DRAWABLE("drawable"),STRING("string"),ID("id"),COLOR("color"),DIMEN("dimen"),STYLE("style");
		
		private String value;
		private ResType(String value){
			this.value = value;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return value;
		}
	}
}
