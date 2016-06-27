package com.askcomponent;

import java.util.ArrayList;

public class BeginCycleComponent implements Component{
	private int begin;
	private int end;
	private int cycleNum;
	private int currentNum;
	
	
	public BeginCycleComponent(){
		currentNum=0;
		cycleNum=3;
	}
	
	
	@Override
	public BeginCycleComponent clone(){
		BeginCycleComponent o = null;  
        try {  
            o = (BeginCycleComponent) super.clone();  
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

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getCycleNum() {
		return cycleNum;
	}

	public void setCycleNum(int cycleNum) {
		this.cycleNum = cycleNum;
	}

	public int getCurrentNum() {
		return currentNum;
	}

	public void setCurrentNum(int currentNum) {
		this.currentNum = currentNum;
	}

	@Override
	public ComponentType getComponentType() {
		// TODO Auto-generated method stub
		return ComponentType.CycleBegin;
	}
	
	
}
