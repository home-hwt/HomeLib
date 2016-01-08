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
	 * ����������ݿ�
	 * @param dbName ���ݿ�����
	 */
	public HomeSqliteDatabase createOrOpenDatabase(Context context,String dbName){
		return createOrOpenDatabase(context,dbName, 1);
	}
	
	/**
	 * ����������ݿ�
	 * @param dbName ���ݿ�����
	 * @param dbVersion ���ݿ�汾
	 */
	public HomeSqliteDatabase createOrOpenDatabase(Context context, String dbName,int dbVersion){
		return createOrOpenDatabase(context,dbName, dbVersion, null);
	}
	
	/**
	 * 
	 * @param context
	 * @param dbDirPath ���ݿⴴ��Ŀ¼��SD ��ʹ�ã�
	 * @param dbName ���ݿ�����
	 * @return
	 */
	public HomeSqliteDatabase createOrOpenDatabase(String dbDirPath,String dbName){
		return createOrOpenDatabase(null,dbDirPath ,dbName, 1, null);
	}
	
	/**
	 * ����������ݿ�
	 * @param dbName ���ݿ�����
	 * @param dbVersion ���ݿ�汾
	 * @param sqliteListener �汾���»ص�
	 */
	public HomeSqliteDatabase createOrOpenDatabase(Context context, String dbName,int dbVersion,HomeSqliteListener sqliteListener){
		return createOrOpenDatabase(context,null, dbName, dbVersion, sqliteListener);
	}
	
	/**
	 * ����������ݿ�
	 * @param dirPath ���ݿⴴ��Ŀ¼��SD ��ʹ�ã�
	 * @param dbName ���ݿ�����
	 * @param dbVersion ���ݿ�汾
	 * @param sqliteListener �汾���»ص�
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
	 * @return �Ƿ�ɾ���ɹ�
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
	 * �ر�
	 * @param dbName
	 */
	public void closeDatabase(String dbName){
		HomeSqliteDatabase sqliteDatabase = sqliteDatabaseMap.get(dbName);
		if(sqliteDatabase != null){
			sqliteDatabase.close();
		}
	}
}
