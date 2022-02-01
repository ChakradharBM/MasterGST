package com.mastergst.usermanagement.runtime.domain;

public enum MgstStorage {

	AMAZON("aws");
	
	private String name;
	private MgstStorage(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static MgstStorage get(String name){
		for( MgstStorage val : values()){
			if(val.getName().equals(name)){
				return val;
			}
		}
		return null;
	}
	
}
