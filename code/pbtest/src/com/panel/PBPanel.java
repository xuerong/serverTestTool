package com.panel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.askcomponent.Component;

public abstract class PBPanel extends JPanel{
	protected static final int LABBELWIDTH=100;
	protected static final int REMOVEBUTTONWIDTH=20;
	protected static final int WIDTH=1000;
	protected static final int HEIGHT=25;
	
	protected static final int LEFT=10;
	protected static final int TOP=5;
	
	protected AskShowPanel mainPanel;
	protected Component component;
	protected String name;
	
	JLabel label=new JLabel();
	JButton remove=new JButton("x");
	
	public PBPanel(final AskShowPanel mainPanel){
		this.mainPanel=mainPanel;
		setLayout(null);
		label.setText("no define");
		label.setLocation(5, 5);
		label.setSize(LABBELWIDTH, 20);
		add(label);
		remove.setBounds(WIDTH-REMOVEBUTTONWIDTH-10, 10, REMOVEBUTTONWIDTH, REMOVEBUTTONWIDTH);
		remove.setMargin(new java.awt.Insets(0,0,0,0)); 
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.removeComponent((PBPanel)((JButton)e.getSource()).getParent());
				mainPanel.updateUI();
			}
		});
		add(remove);
		this.setSize(WIDTH,HEIGHT+TOP+TOP);
		setBackground(Color.RED);
	}
	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		label.setText(name);
		this.name = name;
	}
}
