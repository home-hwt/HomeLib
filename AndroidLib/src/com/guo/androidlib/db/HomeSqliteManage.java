package com.guo.androidlib.db;

import java.io.File;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class HomeSqliteManage {
	private static HomeSqliteManage homeSqliteManage;
	private HashMap<String, HomeSqliteDatabase> sqliteDatabaseMap = new HashMap<String, HomeSqliteDatabase>();
	
	private HomeSqliteManage() {
	}

	public static HomeSqliteManage getInstance() {
		if (homeSqliteManage == null) {
			homeSqliteManage = new HomeSqliteManage();
		}
		return homeSqliteManage;
	}
	
	/**
	 * 创建或打开数据库
	 * @param dbName 数据库名称
	 */
	public HomeSqliteDatabase createOrOpenDatabase(Context context,String dbName){
		return createOrOpenDatabase(context,dbName, 1);
	}
	
	/**
	 * 创建或打开数据库
	 * @param dbName 数据库名称
	 * @param dbVersion 数据库版本
	 */
	public HomeSqliteDatabase createOrOpenDatabase(Context context, String dbName,int dbVersion){
		return createOrOpenDatabase(context,dbName, dbVersion, null);
	}
	
	/**
	 * 
	 * @param context
	 * @param dbDirPath 数据库创建目录（SD 卡使用）
	 * @param dbName 数据库名称
	 * @return
	 */
	public HomeSqliteDatabase createOrOpenDatabase(String dbDirPath,String dbName){
		return createOrOpenDatabase(null,dbDirPath ,dbName, 1, null);
	}
	
	/**
	 * 创建或打开数据库
	 * @param dbName 数据库名称
	 * @param dbVersion 数据库版本
	 * @param sqliteListener 版本更新回调
	 */
	public HomeSqliteDatabase createOrOpenDatabase(Context context, String dbName,int dbVersion,HomeSqliteListener sqliteListener){
		return createOrOpenDatabase(context,null, dbName, dbVersion, sqliteListener);
	}
	
	/**
	 * 创建或打开数据库
	 * @param dirPath 数据库创建目录（SD 卡使用）
	 * @param dbName 数据库名称
	 * @param dbVersion 数据库版本
	 * @param sqliteListener 版本更新回调
	 */
	public HomeSqliteDatabase createOrOpenDatabase(Context context, String dirPath,String dbName,int dbVersion,HomeSqliteListener sqliteListener){
		HomeSqliteDatabase sqliteDatabase = sqliteDatabaseMap.get(dbName);
		if(sqliteDatabase == null){
			sqliteDatabase = new HomeSqliteDatabase(context, dirPath, dbName, dbVersion, sqliteListener);
		}
		return sqliteDatabase;
	}
	
	/**
	 * 
	 * @param context
	 * @return 是否删除成功
	 */
	public boolean deleteDb(Context context,String dbName){
		return context.deleteDatabase(dbName);
	}
	
	/**
	 * android.os.Build.VERSION.SDK_INT >= 16
	 * @param file
	 * @return
	 */
	@SuppressLint("NewApi")
	public boolean deleteDb(File file){
		if(android.os.Build.VERSION.SDK_INT >= 16){
			return SQLiteDatabase.deleteDatabase(file);
		}
		return false;
	}
	
	/**
	 * 关闭
	 * @param dbName
	 */
	public void closeDatabase(String dbName){
		HomeSqliteDatabase sqliteDatabase = sqliteDatabaseMap.get(dbName);
		if(sqliteDatabase != null){
			sqliteDatabase.close();
		}
	}
}
