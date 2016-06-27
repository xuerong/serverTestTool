package com.panel.inputfield;

import javax.swing.JTextField;


public class StringInputField extends JTextField implements InputFieldInterface {
	
	public static final int WIDTH=80;
	public static final int HEIGHT=25;
	
	public StringInputField(){
		this("");
	}
	public StringInputField(String text){
		super(text);
		setSize(WIDTH, HEIGHT);
	}
	@Override
	public Object getValue() {
		return getText();
	}
}
