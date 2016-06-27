package com.askcomponent;

public enum VarType {
	Int32,//int
	Int64,//long
	String,//string
	Pb//PBComponent
	;
	
	public static String getStringByType(VarType type){
		switch (type) {
		case Int32:return "I32";
		case Int64:return "I64";
		case String:return "STR";
		case Pb:return "PB";
		}
		return "";
	}
	public static Object getDefaultValue(VarType type){
		switch (type) {
		case Int32:return new Integer(0);
		case Int64:return new Long(0);
		case String:return "";
		case Pb:return "PBName";
		}
		return "";
	}
	public static boolean isTypeEquals(Object object,VarType type){
		switch (type) {
		case Int32:return object instanceof Integer;
		case Int64:return object instanceof Long;
		case String:return object instanceof String;
		case Pb:return object instanceof PBComponent;
		}
		return false;
	}
}
