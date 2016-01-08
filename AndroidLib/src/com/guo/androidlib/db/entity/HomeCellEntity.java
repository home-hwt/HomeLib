package com.guo.androidlib.db.entity;

import com.guo.androidlib.db.HomeColumnDbType;

public class HomeCellEntity {
	private String cellName;
	private HomeColumnDbType cellType;
	private Object cellValue;
	private boolean isPrimaryKeys;
	
	public String getCellName() {
		return cellName;
	}
	public void setCellName(String cellName) {
		this.cellName = cellName;
	}
	public HomeColumnDbType getCellType() {
		return cellType;
	}
	public void setCellType(HomeColumnDbType cellType) {
		this.cellType = cellType;
	}
	public Object getCellValue() {
		return cellValue;
	}
	public void setCellValue(Object cellValue) {
		this.cellValue = cellValue;
	}
	public boolean isPrimaryKeys() {
		return isPrimaryKeys;
	}
	public void setPrimaryKeys(boolean isPrimaryKeys) {
		this.isPrimaryKeys = isPrimaryKeys;
	}
}
