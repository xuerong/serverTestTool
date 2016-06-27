package com;

import com.askcomponent.PBComponentHandler;

public class Single {
	private static Single single=new Single();
	public static Single getInstance(){
		return single;
	}
	private Single(){}
	
	
}
