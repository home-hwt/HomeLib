package com.guo.androidlib.db;

import android.database.sqlite.SQLiteDatabase;

public class HomeDatabaseConfig {
	private String dbName = "homeDb";
	private SQLiteDatabase db;
	private HomeSqliteListener sqliteListener;
	private int dbVersion = 1;

	/*
	public HomeDatabaseConfig(SQLiteDatabase db) {
		this(null,db);
	}

	public HomeDatabaseConfig(String dbName, SQLiteDatabase db) {
		this(null,1,db);
	}

	public HomeDatabaseConfig(String dbName,int dbVersion, SQLiteDatabase db) {
		this(null,1,db,null);
	}*/
	
	protected HomeDatabaseConfig(String dbName,int dbVersion, SQLiteDatabase db,HomeSqliteListener sqliteListener) {
		this.dbName = dbName;
		this.dbVersion = dbVersion;
		this.db = db;
		this.sqliteListener = sqliteListener;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public HomeSqliteListener getSqliteListener() {
		return sqliteListener;
	}

	public void setSqliteListener(HomeSqliteListener sqliteListener) {
		this.sqliteListener = sqliteListener;
	}

	public int getDbVersion() {
		return dbVersion;
	}

	public void setDbVersion(int dbVersion) {
		this.dbVersion = dbVersion;
	}
}
