package com.panel.inputfield;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;

import com.askcomponent.PBComponent;
import com.askcomponent.PBVar;
import com.askcomponent.VarType;
import com.panel.PBVarPanel;

public class PBInputField extends JButton implements InputFieldInterface ,ActionListener{
	
	public static final int WIDTH=1000;
	public static final int HEIGHT=25;
	
	private static final int LEFT=10;
	private static final int TOP=10;
	
	private PBComponent component=null;
	
	public PBInputField(){
		this("",null);
	}
	public PBInputField(String text,PBComponent component){
		super(text);
		setSize(WIDTH, HEIGHT);
		this.component = component;
		addActionListener(this);
	}
	@Override
	public Object getValue() {
		return component;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(component!=null){
			int height=0;
			final JDialog showPBFrame=new JDialog();
			
			showPBFrame.setLayout(null);
			showPBFrame.setModalityType(ModalityType.TOOLKIT_MODAL);
			
			
			int currentLeft=LEFT;
			List<PBVar> vars=component.getVars();
			for (PBVar pbVar : vars) {
				PBVarPanel panel=new PBVarPanel(pbVar);
				if(currentLeft+panel.getSize().width+LEFT>WIDTH){
					currentLeft=LEFT;
					height+=(HEIGHT+TOP);
				}
				panel.setLocation(currentLeft, height+TOP);
				currentLeft+=(panel.getSize().width+LEFT);
				showPBFrame.add(panel);
			}
			
			// save 把showPBFrame中的东西，保存到PBComponent的Vars
			JButton button =new JButton("ok");
			height+=(HEIGHT+TOP);
			button.setBounds(LEFT, height+TOP, 80, HEIGHT);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<PBVar> vars=component.getVars();
					vars.clear();
					for (Component com: ((Container)(showPBFrame.getRootPane().getLayeredPane().getComponent(0))).getComponents()) {
						if(com instanceof PBVarPanel){
							PBVarPanel panel=(PBVarPanel)com;
							vars.add(panel.getVar());
						}
					}
					showPBFrame.setVisible(false);
				}
			});
			
			showPBFrame.add(button);
			
			showPBFrame.setSize(WIDTH, height+HEIGHT+TOP+60);
			showPBFrame.setLocationRelativeTo(null);
			showPBFrame.setVisible(true);
		}
	}
}
