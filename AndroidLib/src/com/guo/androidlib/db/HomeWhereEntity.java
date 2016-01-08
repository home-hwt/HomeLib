package com.guo.androidlib.db;

import java.util.ArrayList;
import java.util.List;

public class HomeWhereEntity {
	private final List<String> whereItems;

    private HomeWhereEntity() {
        this.whereItems = new ArrayList<String>();
    }

    /**
     * create new instance
     * @return
     */
    public static HomeWhereEntity newInstance() {
        return new HomeWhereEntity();
    } 
    
    
    /**
     * @param columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value
     * @return
     */
    public HomeWhereEntity add(String columnName, String op, Object value) {
    	insertOp2List(null,columnName, op, value);
        return this;
    }
    
    /**
     * add AND condition
     *
     * @param columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value
     * @return
     */
    public HomeWhereEntity and(String columnName, String op, Object value) {
    	insertOp2List("and" ,columnName, op, value);
        return this;
    }

    /**
     * add OR condition
     *
     * @param columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value
     * @return
     */
    public HomeWhereEntity or(String columnName, String op, Object value) {
    	insertOp2List("or" ,columnName, op, value);
        return this;
    }
    
    private void insertOp2List(String conj,String columnName, String op, Object value){
    	StringBuilder sb = new StringBuilder();

    	if(whereItems.size() > 0 && conj != null){
    		sb.append(" " + conj + " ");
    	}
    	sb.append(columnName);
    	sb.append(" ");
    	sb.append(op);
    	sb.append(" ");
    	
    	HomeColumnDbType columnValueType = HomeSqliteUtil.getInstance().getColumnType(value.getClass());
    	if(HomeColumnDbType.TEXT.equals(columnValueType)){
    		sb.append("'" + value + "'");
    	}else{
    		sb.append(value);
    	}
    	
    	whereItems.add(sb.toString());
    }
    
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < whereItems.size(); i++) {
    		sb.append(whereItems.get(i)).append(" ");
		}
    	return sb.toString();
    }
}
