package com.askcomponent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PBVar implements Cloneable,Serializable{
	private VarProperty property;
	private VarType type;
	private String name;
	private int id;
	private Object value;
	// pb模板,即,如果是pb list类型,用这个记录pb的模板，否则是null
	private Object model;
	
	@Override
	public PBVar clone(){
		PBVar o = null;  
        try {  
            o = (PBVar) super.clone();  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }
        if(value instanceof Integer)
        	o.value=new Integer((Integer)value);
        else if(value instanceof Long)
        	o.value=new Long((Long)value);
        else if(value instanceof String)
        	o.value=new String((String)value);
        else if(value instanceof PBComponent)
        	o.value=((PBComponent) value).clone();
        else if(value instanceof List<?>){
        	List valueList=(List) value;
        	if(valueList.size()==0)
        		o.value=new ArrayList();
        	else{
        		Object firstObject=valueList.get(0);
        		if(firstObject instanceof Integer){
                	o.value=new ArrayList<Integer>();
                	for (Object object : valueList) {
                		((List<Integer>)o.value).add(new Integer((Integer)object));
					}
        		}else if(firstObject instanceof Long){
        			o.value=new ArrayList<Long>();
                	for (Object object : valueList) {
                		((List<Long>)o.value).add(new Long((Long)object));
					}
        		}else if(firstObject instanceof String){
        			o.value=new ArrayList<String>();
                	for (Object object : valueList) {
                		((List<String>)o.value).add(new String((String)object));
					}
        		}else{
        			o.value=new ArrayList<PBComponent>();
                	for (Object object : valueList) {
                		((List<PBComponent>)o.value).add(((PBComponent)object).clone());
					}
        		}
        	}
        }
        return o;
	}
	
	public VarProperty getProperty() {
		return property;
	}
	public void setProperty(VarProperty property) {
		this.property = property;
	}
	public VarType getType() {
		return type;
	}
	public void setType(VarType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}
}
