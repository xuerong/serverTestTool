package com.askcomponent;

public class EndCycleComponent implements Component{
	private int begin;
	
	@Override
	public EndCycleComponent clone(){
		EndCycleComponent o = null;  
        try {  
            o = (EndCycleComponent) super.clone();  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }
        return o;  
	}
	
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	@Override
	public ComponentType getComponentType() {
		// TODO Auto-generated method stub
		return ComponentType.CycleEnd;
	}
}
