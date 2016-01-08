package com.guo.androidlib.db;

import java.util.Locale;

import com.guo.androidlib.util.GHLog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HomeSqliteUtil {
	private static HomeSqliteUtil homeSqliteUtil;

	private HomeSqliteUtil() {
	}

	public static HomeSqliteUtil getInstance() {
		if (homeSqliteUtil == null) {
			homeSqliteUtil = new HomeSqliteUtil();
		}
		return homeSqliteUtil;
	}

	public String getTableNameByObject(Object obj) {
		return getTableNameByObject(obj.getClass());
	}

	public String getTableNameByObject(Class<?> cls) {
		return "t_" + cls.getSimpleName().toLowerCase(Locale.getDefault());
	}

	/**
	 * 
	 * @param db
	 * @param dbName
	 *            数据库名
	 * @param tableName
	 *            表名
	 * @return 表是否存在
	 */
	public boolean isExistTable(SQLiteDatabase db,
			String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		Cursor cursor = null;
		try {
//			String sql = "select count(*) as c from " + dbName
//					+ " where type ='table' and name ='" + tableName.trim()
//					+ "'";
			String sql = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='"+tableName.trim()+"'";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			GHLog.gHLog(GHLog.LOG_ERROR, "" + e.getMessage());
		}
		return result;
	}

	public HomeColumnDbType getColumnType(Class<?> columnJavaType) {
		String javaTypeName = columnJavaType.getName();
		if ("String".equalsIgnoreCase(javaTypeName)
				|| "Character".equals(javaTypeName)) {
			return HomeColumnDbType.TEXT;
		} else if ("int".equalsIgnoreCase(javaTypeName)
				|| "Long".equalsIgnoreCase(javaTypeName)
				|| "Short".equalsIgnoreCase(javaTypeName)
				|| "Integer".equalsIgnoreCase(javaTypeName)
				|| "Byte".equalsIgnoreCase(javaTypeName)) {
			return HomeColumnDbType.INTEGER;
		} else if ("Double".equalsIgnoreCase(javaTypeName)
				|| "Float".equals(javaTypeName)) {
			return HomeColumnDbType.REAL;
		}
		return HomeColumnDbType.TEXT;
	}
}
