package com.guo.androidlib.test;

import com.guo.androidlib.db.entity.HomeTableEntity;

public class DbTablePerson extends HomeTableEntity {
	
	public String name;
	public int age;
	public int score = 20;
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
//	public String des;
//	
//	public String getDes() {
//		return des;
//	}
//	public void setDes(String des) {
//		this.des = des;
//	}
//	
//	public String bangji;
//	
//	public String getBangji() {
//		return bangji;
//	}
//	public void setBangji(String bangji) {
//		this.bangji = bangji;
//	}

//	public int parents ;
//	
//	public int getParents() {
//		return parents;
//	}
//	public void setParents(int parents) {
//		this.parents = parents;
//	}

	
	//public String add;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
