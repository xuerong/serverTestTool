package com.panel.inputfield;

import javax.swing.JTextField;

public class Int32InputField extends JTextField implements InputFieldInterface {
	
	public static final int WIDTH=80;
	public static final int HEIGHT=25;
	
	public Int32InputField(){
		this(null);
	}
	
	public Int32InputField(Integer value){
		super();
		if(value!=null){
			setText(value.toString());
		}
		setSize(WIDTH, HEIGHT);
	}

	@Override
	public Object getValue() {
		return Integer.parseInt(getText().trim().toString());
	}
}
