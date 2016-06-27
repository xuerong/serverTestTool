package com.panel.inputfield;

import javax.swing.JTextField;


public class Int64InputField extends JTextField implements InputFieldInterface {
	
	public static final int WIDTH=80;
	public static final int HEIGHT=25;
	
	public Int64InputField(){
		this(0l);
	}
	public Int64InputField(Long value){
		super();
		if(value!=null){
			setText(value.toString());
		}
		setSize(WIDTH, HEIGHT);
	}
	@Override
	public Long getValue() {
		return Long.parseLong(getText().trim().toString());
	}
}
