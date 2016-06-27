package com.askcomponent;

import java.util.ArrayList;
import java.util.List;
/**
 * 用于发送的组件，发送的一个基本单元
 * **/
public class PBComponent implements Component,Cloneable{
	
	private String name;
	private int opcode;
	
	private List<PBVar> vars=new ArrayList<PBVar>();
	
	// 需要保存期返回值对应pb的引用
	private PBComponent backPbComponent;
	
	public PBComponent(){
		super();
	}
	
	@Override
	public ComponentType getComponentType() {
		return ComponentType.Pb;
	}
	@Override
	public PBComponent clone(){
		PBComponent o = null;  
        try {  
            o = (PBComponent) super.clone();  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        } 
        o.vars=new ArrayList<PBVar>();
        for (PBVar var : vars) {
			o.vars.add(var.clone());
		}
        o.backPbComponent=this.backPbComponent;
        return o;  
	}

	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PBVar> getVars() {
		return vars;
	}

	public void setVars(List<PBVar> vars) {
		this.vars = vars;
	}

	public int getOpcode() {
		return opcode;
	}

	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}

	public PBComponent getBackPbComponent() {
		return backPbComponent;
	}

	public void setBackPbComponent(PBComponent backPbComponent) {
		this.backPbComponent = backPbComponent;
	}
	
	
}
