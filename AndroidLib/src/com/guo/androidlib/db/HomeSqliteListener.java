package com.guo.androidlib.db;

import android.database.sqlite.SQLiteDatabase;

public interface HomeSqliteListener {
	public void sqliteUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
