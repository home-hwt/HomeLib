package com.guo.androidlib.db;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.guo.androidlib.db.annotation.HomeAnoCellName;
import com.guo.androidlib.db.annotation.HomeDbColumnIgnore;
import com.guo.androidlib.db.entity.HomeCellEntity;
import com.guo.androidlib.db.entity.HomeTableEntity;
import com.guo.androidlib.util.GHLog;

public class HomeSqliteDatabase {

	private HomeDatabaseConfig databaseConfig;

	public HomeSqliteDatabase(Context context, String dbName) {
		this(context, dbName, 1);
	}

	public HomeSqliteDatabase(Context context, String dbName, int dbVersion) {
		this(context, dbName, dbVersion, null);
	}

	public HomeSqliteDatabase(Context context, String dbName, int dbVersion,
			HomeSqliteListener sqliteListener) {
		this(context, null, dbName, dbVersion, sqliteListener);
	}

	public HomeSqliteDatabase(Context context, String dbDirPath, String dbName,
			int dbVersion, HomeSqliteListener sqliteListener) {
		SQLiteDatabase sqliteDB;
		if (dbDirPath == null && context != null) {
			sqliteDB = context.openOrCreateDatabase(dbName,
					Context.MODE_PRIVATE, null);
		} else {
			sqliteDB = SQLiteDatabase.openOrCreateDatabase(dbDirPath
					+ File.separator + dbName, null);
		}
		if (sqliteListener != null) {
			versionHandler(sqliteListener);
		}
		sqliteDB.setVersion(dbVersion);
		databaseConfig = new HomeDatabaseConfig(dbName, dbVersion, sqliteDB,
				sqliteListener);
	}

	/**
	 * 版本更新通知
	 * 
	 * @param sqliteListener
	 */
	private void versionHandler(HomeSqliteListener sqliteListener) {
		int version = databaseConfig.getDb().getVersion();
		int newVersion = databaseConfig.getDbVersion();
		if (version > 0 && version < newVersion) {
			sqliteListener.sqliteUpgrade(databaseConfig.getDb(), version,
					newVersion);
		}
	}

	private boolean isExistTable(String table) {
		return HomeSqliteUtil.getInstance().isExistTable(
				databaseConfig.getDb(), table);
	}

	private void execSql(String sql) {
		GHLog.gHLog("sql:" + sql);
		databaseConfig.getDb().execSQL(sql);
	}

	private List<HomeCellEntity> tableEntity2ListCellEntity(
			HomeTableEntity tableEntity) {
		Field[] fields = tableEntity.getClass().getFields();

		// 主键字段
		// String primaryKeys = tableEntity.getPrimaryKeys();
		// String[] split = primaryKeys.split(",");

		List<HomeCellEntity> columnList = new ArrayList<HomeCellEntity>();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.getAnnotation(HomeDbColumnIgnore.class) != null) {// 胡略的字段
				continue;
			}
			String columnName = field.getName();// 列名
			HomeAnoCellName cellAnnotation = field
					.getAnnotation(HomeAnoCellName.class);
			if (cellAnnotation != null) {
				columnName = cellAnnotation.cellName();
			}
			HomeColumnDbType dbColumnType = HomeSqliteUtil.getInstance()
					.getColumnType(field.getType());

			try {
				Method declaredMethod = tableEntity.getClass().getMethod(
						"get"
								+ columnName.substring(0, 1).toUpperCase(
										Locale.getDefault())
								+ columnName.substring(1));
				Object columnValue = declaredMethod.invoke(tableEntity);

				HomeCellEntity homeCellEntity = new HomeCellEntity();
				homeCellEntity.setCellName(columnName);
				homeCellEntity.setCellType(dbColumnType);
				homeCellEntity.setCellValue(columnValue);
				columnList.add(homeCellEntity);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return columnList;
	}

	public void createTable(HomeTableEntity tableEntity) {
		String tableName = HomeSqliteUtil.getInstance().getTableNameByObject(
				tableEntity);
		List<HomeCellEntity> columnList = tableEntity2ListCellEntity(tableEntity);

		int size = columnList.size();
		StringBuilder insertCellsNames = new StringBuilder();// table item names
		StringBuilder insertCellsValues = new StringBuilder();// table item
																// values
		StringBuilder createTableSql = new StringBuilder();

		insertCellsNames.append("(");
		insertCellsValues.append("(");
		createTableSql.append("(");
		for (int i = 0; i < size; i++) {
			HomeCellEntity homeCellEntity = columnList.get(i);
			insertCellsNames.append(homeCellEntity.getCellName());
			insertCellsValues.append("'" + homeCellEntity.getCellValue() + "'");
			createTableSql.append(homeCellEntity.getCellName());
			createTableSql.append(" ");
			createTableSql.append(homeCellEntity.getCellType());
			if (i < size - 1) {// 除了最后一项
				insertCellsNames.append(",");
				insertCellsValues.append(",");
				createTableSql.append(",");
			}
		}
		insertCellsNames.append(")");
		insertCellsValues.append(")");

		// 没有设置主键时默认添加id为自增长主键
		if (TextUtils.isEmpty(tableEntity.getPrimaryKeys())) {
			//createTableSql.append(", id INTEGER PRIMARY KEY AUTOINCREMENT");
			//tableEntity.setPrimaryKeys("id");
		} else {
			createTableSql.append(",Primary Key("
					+ tableEntity.getPrimaryKeys() + ")");
		}
		createTableSql.append(")");

		if (!isExistTable(tableName)) {
			// Create Table MyINFO(id Number(3) Not Null,Name Varchar2(15) Not
			// Null,code varchar2(6),Constraint pk_mytable Primary Key(id,Name))
			execSql(" create table if not exists " + tableName + createTableSql
					+ ";");
		}
	}

	/**
	 * 插入一行
	 * 
	 * @param tableEntity
	 */
	public void save(HomeTableEntity tableEntity) {
		String tableName = HomeSqliteUtil.getInstance().getTableNameByObject(
				tableEntity);
		List<HomeCellEntity> columnList = tableEntity2ListCellEntity(tableEntity);

		int size = columnList.size();
		StringBuilder insertCellsNames = new StringBuilder();// table item names
		StringBuilder insertCellsValues = new StringBuilder();// table item
																// values
		StringBuilder createTableSql = new StringBuilder();

		insertCellsNames.append("(");
		insertCellsValues.append("(");
		createTableSql.append("(");
		for (int i = 0; i < size; i++) {
			HomeCellEntity homeCellEntity = columnList.get(i);
			insertCellsNames.append(homeCellEntity.getCellName());
			insertCellsValues.append("'" + homeCellEntity.getCellValue() + "'");
			createTableSql.append(homeCellEntity.getCellName());
			createTableSql.append(" ");
			createTableSql.append(homeCellEntity.getCellType());
			if (i < size - 1) {// 除了最后一项
				insertCellsNames.append(",");
				insertCellsValues.append(",");
				createTableSql.append(",");
			}
		}
		// 没有设置主键时默认添加id为自增长主键
		if (TextUtils.isEmpty(tableEntity.getPrimaryKeys())) {
			//createTableSql.append(", id INTEGER PRIMARY KEY AUTOINCREMENT");
			//tableEntity.setPrimaryKeys("id");
		} else {
			createTableSql.append(",Primary Key("
					+ tableEntity.getPrimaryKeys() + ")");
		}
		createTableSql.append(")");

		insertCellsNames.append(")");
		insertCellsValues.append(")");

		if (!isExistTable(tableName)) {
			// Create Table MyINFO(id Number(3) Not Null,Name Varchar2(15) Not
			// Null,code varchar2(6),Constraint pk_mytable Primary Key(id,Name))
			execSql(" create table if not exists " + tableName + createTableSql
					+ ";");
		}

		// //insert into table1(field1,field2) values(value1,value2)
		String sql = "insert into " + tableName + insertCellsNames + " values "
				+ insertCellsValues + ";";
		execSql(sql);
	}

	/**
	 * 插入多行
	 * 
	 * @param list
	 */
	public void save(List<HomeTableEntity> list) {
		databaseConfig.getDb().beginTransaction();
		for (int i = 0; i < list.size(); i++) {
			save(list.get(i));
		}
		databaseConfig.getDb().endTransaction();
	}

	/**
	 * 
	 * @param tableEntity
	 *            要修改的实体表，直接传递一个对象就可以
	 * @param where
	 *            修改的条件
	 * @return
	 */
	public List<HomeTableEntity> query(HomeTableEntity tableEntity,
			HomeWhereEntity whereEntity) {
		// SELECT * FROM emp where sal=(SELECT MAX(sal) from emp));
		String tableName = HomeSqliteUtil.getInstance().getTableNameByObject(
				tableEntity);
		StringBuilder rawQuerySql = new StringBuilder();
		rawQuerySql.append("select * from ").append(tableName);
		if (whereEntity != null) {
			rawQuerySql.append(" where ").append(whereEntity.toString());
		}
		Cursor rawQuery = databaseConfig.getDb().rawQuery(
				rawQuerySql.toString(), null);
		List<HomeTableEntity> queryList = new ArrayList<HomeTableEntity>();
		try {
			while (rawQuery.moveToNext()) {
				Constructor<? extends HomeTableEntity> constructor = tableEntity
						.getClass().getConstructor();
				HomeTableEntity newInstance = constructor.newInstance();
				// HomeTableEntity newInstance =
				// tableEntity.getClass().newInstance();
				String[] columnNames = rawQuery.getColumnNames();
				for (int j = 0; j < columnNames.length; j++) {
					String columnName = columnNames[j];
					int columnIndex = rawQuery.getColumnIndex(columnName);
					Object columnValue = null;
					String methodName = columnName.substring(0, 1).toUpperCase(
							Locale.getDefault())
							+ columnName.substring(1);

					Field field = newInstance.getClass().getField(columnName);

					Method setMethod = newInstance.getClass().getMethod(
							"set" + methodName, field.getType());
					Class<?>[] parameterTypes = setMethod.getParameterTypes();
					if (parameterTypes.length > 0) {
						HomeColumnDbType columnType = HomeSqliteUtil
								.getInstance().getColumnType(parameterTypes[0]);
						if (HomeColumnDbType.TEXT.equals(columnType)) {
							columnValue = rawQuery.getString(columnIndex);
						} else if (HomeColumnDbType.INTEGER.equals(columnType)) {
							columnValue = rawQuery.getInt(columnIndex);
						} else if (HomeColumnDbType.REAL.equals(columnType)) {
							columnValue = rawQuery.getDouble(columnIndex);
						} else if (HomeColumnDbType.BLOB.equals(columnType)) {
							columnValue = rawQuery.getBlob(columnIndex);
						}
						setMethod.invoke(newInstance, columnValue);
					}
				}
				queryList.add(newInstance);
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rawQuery != null) {
				rawQuery.close();
			}
		}
		return queryList;
	}

	/**
	 * 更新数据
	 */
	public void update(String table, String updateSet, String where) {
		// update t_test t set t.password = '*', t.remark = '*' where t.bs = 1;
		if (isExistTable(table)) {
			execSql("update " + table + " set " + updateSet + " where " + where);
		} else {
			GHLog.gHLog(GHLog.LOG_ERROR, "table is not exists");
		}
	}

	/**
	 * @param tableEntity
	 *            修改成此实体类中的值
	 * @param whereEntity
	 *            修改条件
	 */
	public void update(HomeTableEntity tableEntity, HomeWhereEntity whereEntity) {
		String tableName = HomeSqliteUtil.getInstance().getTableNameByObject(
				tableEntity);

		if (!isExistTable(tableName)) {
			GHLog.gHLog(GHLog.LOG_ERROR, "table is not exists");
			return;
		}

		List<HomeCellEntity> tableEntityList = tableEntity2ListCellEntity(tableEntity);

		StringBuilder setString = new StringBuilder();
		int size = tableEntityList.size();
		for (int i = 0; i < size; i++) {
			HomeCellEntity homeCellEntity = tableEntityList.get(i);
			setString.append(homeCellEntity.getCellName() + "=");
			Object cellValue = homeCellEntity.getCellValue();
			if (HomeColumnDbType.TEXT.equals(HomeSqliteUtil.getInstance()
					.getColumnType(cellValue.getClass()))) {// 字符串加引号
				setString.append("'" + cellValue + "'");
			} else {
				setString.append(cellValue);
			}

			if (i < size - 1) {// 不是最后一项
				setString.append(",");
			}
		}
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("update ").append(tableName).append(" set ")
				.append(setString);
		updateSql.append(" where ").append(whereEntity.toString());
		execSql(updateSql.toString());
	}

	/**
	 * 
	 * @param table
	 *            表名
	 * @param whereEntity
	 *            条件
	 */
	public void delete(Class<? extends HomeTableEntity> tableClass,
			HomeWhereEntity whereEntity) {
		// DELETE FROM Person WHERE LastName = 'Wilson'
		execSql("delete from "
				+ HomeSqliteUtil.getInstance().getTableNameByObject(tableClass)
				+ " where " + whereEntity.toString());
	}

	public void dropTable(String tableName) {
		if (HomeSqliteUtil.getInstance().isExistTable(databaseConfig.getDb(),
				tableName)) {
			execSql("drop table " + tableName);
		}
	}

	public void dropTable(HomeTableEntity tableEntity) {
		dropTable(tableEntity.getClass());
	}

	/**
	 * 删除表
	 * 
	 * @param tableClass
	 *            表实体类class
	 */
	public void dropTable(Class<? extends HomeTableEntity> tableClass) {
		// execSql("drop table "
		// + HomeSqliteUtil.getInstance().getTableNameByObject(tableClass));
		dropTable(HomeSqliteUtil.getInstance().getTableNameByObject(tableClass));
	}

	/**
	 * 更新表实体类
	 * 
	 * @param tableEntity
	 */
	public void alterTable(HomeTableEntity tableEntity) {
		List<HomeCellEntity> newColummList = tableEntity2ListCellEntity(tableEntity);
		// String sql ="select name from syscolumns where id=object_id('表名')";
		String tableName = HomeSqliteUtil.getInstance().getTableNameByObject(
				tableEntity);
		String sql = "select * from " + tableName + " limit 1";
		Cursor rawQuery = databaseConfig.getDb().rawQuery(sql, null);
		ArrayList<String> removeColumnList = new ArrayList<String>();// 要删除的字段
		if (rawQuery != null) {
			String[] columnNames = rawQuery.getColumnNames();
			// 获取添加的字段列表
			for (int j = 0; j < columnNames.length; j++) {
				String columnName = columnNames[j];
				if ("id".equalsIgnoreCase(columnName)) {// id字段忽略
					continue;
				}
				removeColumnList.add(columnName);
				for (int i = 0; i < newColummList.size(); i++) {
					HomeCellEntity homeCellEntity = newColummList.get(i);
					if (homeCellEntity.getCellName().equalsIgnoreCase(
							columnName)) {// 字段存在
						// 移除
						newColummList.remove(homeCellEntity);
						removeColumnList.remove(columnName);
						break;
					}
				}
			}
		}

		// 添加字段
		for (int i = 0; i < newColummList.size(); i++) {
			// alter table [表名] add 字段名 int default 0 增加数字字段，长整型，缺省值为0
			HomeCellEntity homeCellEntity = newColummList.get(i);
			StringBuilder alertSqlSb = new StringBuilder();
			alertSqlSb.append("alter table ").append(tableName).append(" add ");
			alertSqlSb.append(homeCellEntity.getCellName());
			alertSqlSb.append(" ");
			alertSqlSb.append(homeCellEntity.getCellType());
			alertSqlSb.append(" default ");
			alertSqlSb.append(homeCellEntity.getCellValue());
			execSql(alertSqlSb.toString());
		}
		rawQuery.close();
		// 删除字段 alter table [表名] drop 字段名
		if (removeColumnList.size() > 0) {
			String newSql = "select * from " + tableName + " limit 2";
			Cursor newRawQuery = databaseConfig.getDb().rawQuery(newSql, null);// 获取添加新字段后的表字段
			String[] newColumnNames = newRawQuery.getColumnNames();
			// 新表包含字段
			ArrayList<String> newTableColumnNames = new ArrayList<String>();
			for (int i = 0; i < newColumnNames.length; i++) {
				String columnName = newColumnNames[i];
				newTableColumnNames.add(columnName);
				for (int j = 0; j < removeColumnList.size(); j++) {
					String removeCol = removeColumnList.get(j);
					if (TextUtils.equals(removeCol, columnName)) {
						newTableColumnNames.remove(columnName);
					}
				}
			}

			// 创建temp表并将旧表数据复制到temp表上
			// createTable(tableEntity);
			// create table temp as select * from record where 1=2;
			String tempTableName = "temp";
			dropTable(tempTableName);
			StringBuilder tempTableCreateSb = new StringBuilder();
			tempTableCreateSb.append("create table if not exists "
					+ tempTableName + " as select ");

			StringBuilder copyDataSqlSb = new StringBuilder();
			copyDataSqlSb.append("insert into ");
			copyDataSqlSb.append(tempTableName);
			copyDataSqlSb.append(" select ");
			for (int i = 0; i < newTableColumnNames.size(); i++) {
				String colName = newTableColumnNames.get(i);
				tempTableCreateSb.append(colName);
				tempTableCreateSb.append(",");
				copyDataSqlSb.append(colName).append(",");
			}
			tempTableCreateSb.deleteCharAt(tempTableCreateSb.length() - 1);
			tempTableCreateSb.append(" from ").append(tableName)
					.append(" where 1=2");
			execSql(tempTableCreateSb.toString());

			copyDataSqlSb.deleteCharAt(copyDataSqlSb.length() - 1);
			copyDataSqlSb.append(" from ").append(tableName);
			// insert into temp_target_table(id,name,age) select id,name,age
			// from target_table
			execSql(copyDataSqlSb.toString());
			
			
			dropTable(tableEntity);// 删除旧表
			// alter table temp rename to record; 将temp表重命名为原表
			execSql("alter table " + tempTableName + " rename to " + tableName);
			//主键设置 alter table 表名add primary key (列名)
			if(!TextUtils.isEmpty(tableEntity.getPrimaryKeys())){
				execSql("alter table " + tableName + " add primary key (" + tableEntity.getPrimaryKeys() +")");
			}
		}
	}

	/**
	 * 删除数据库
	 * @param context
	 * @return 是否删除成功
	 */
	public boolean deleteDb(Context context) {
		return context.deleteDatabase(databaseConfig.getDbName());
	}

	/**
	 * android.os.Build.VERSION.SDK_INT >= 16
	 * 
	 * @param file
	 * @return
	 */
	@SuppressLint("NewApi")
	public boolean deleteDb(File file) {
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			return SQLiteDatabase.deleteDatabase(file);
		}
		return false;
	}

	/**
	 * 开启事务
	 */
	public void beginTransaction() {
		databaseConfig.getDb().beginTransaction();
	}

	/**
	 * 事务结束
	 */
	public void endTransaction() {
		databaseConfig.getDb().endTransaction();
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		SQLiteDatabase db = databaseConfig.getDb();
		if (db != null && !db.isOpen()) {
			db.close();
		}
	}

	// public HomeDatabaseConfig getDatabaseConfig() {
	// return databaseConfig;
	// }
}
