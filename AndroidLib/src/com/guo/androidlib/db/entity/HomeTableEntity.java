package com.guo.androidlib.db.entity;

import com.guo.androidlib.db.annotation.HomeDbColumnIgnore;

/**
 * Ö÷¼üÉèÖÃ
 * @author home 
 *
 */
public abstract class HomeTableEntity {
	@HomeDbColumnIgnore
	public String id;
	
	@HomeDbColumnIgnore
	public String primaryKeys;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public final String getPrimaryKeys(){
		return primaryKeys;
	}
	
	public final void setPrimaryKeys(String primaryKeys){
		this.primaryKeys = primaryKeys;
	}
}
