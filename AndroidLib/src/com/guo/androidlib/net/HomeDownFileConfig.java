package com.guo.androidlib.net;

import java.io.File;

import android.os.Environment;
/**
 * �����ļ�������Ϣ
 * @author Home
 *
 */
public class HomeDownFileConfig {
	private static HomeDownFileConfig homeDownFileConfig;
	
	private String dbName = "home_file.db";//���ݿ���
	
	private String defaultFilePath ;//Ĭ���ļ�����Ŀ¼

	private String tableName = "home_down_file";//�����ļ�������Ϣ
	
	
	private HomeDownFileConfig(){
		String sdState = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(sdState)){
			File downDir = new File(Environment.getExternalStorageDirectory(), "HomeDownFile");
			if(!downDir.exists()){
				downDir.mkdirs();
			}
			defaultFilePath = downDir.getAbsolutePath();
		}
	}
	
	public static HomeDownFileConfig getInstance(){
		if(homeDownFileConfig == null){
			homeDownFileConfig = new HomeDownFileConfig();
		}
		return homeDownFileConfig;
	}
	
	

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDefaultFilePath() {
		return defaultFilePath;
	}

	public void setDefaultFilePath(String defaultFilePath) {
		this.defaultFilePath = defaultFilePath;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
