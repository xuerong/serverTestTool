package com.panel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.askcomponent.Component;
import com.askcomponent.PBComponent;
import com.askcomponent.PBVar;

public class PBPacketPanel extends PBPanel{
	
	private int height=TOP;

	private ArrayList<PBVarPanel> varPanels=new ArrayList<PBVarPanel>();
	
	public PBPacketPanel(final AskShowPanel mainPanel){
		super(mainPanel);
		setLayout(null);
	}
	
	@Override
	public void setComponent(Component component) {
		PBComponent com=(PBComponent)component;
		int currentLeft=LABBELWIDTH+LEFT;
		List<PBVar> vars=com.getVars();
		for (PBVar pbVar : vars) {
			PBVarPanel panel=new PBVarPanel(pbVar);
			if(currentLeft+panel.getSize().width+LEFT+REMOVEBUTTONWIDTH>WIDTH){
				currentLeft=LABBELWIDTH+LEFT;
				height+=(HEIGHT+TOP);
			}
			panel.setLocation(currentLeft, height);
			currentLeft+=(panel.getSize().width+LEFT);
			varPanels.add(panel);
			add(panel);
		}
		this.setSize(WIDTH,height+HEIGHT+TOP);
		this.component = component;
	}

	public ArrayList<PBVarPanel> getVarPanels() {
		return varPanels;
	}
	
}
