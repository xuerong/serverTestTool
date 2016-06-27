package com.panel;

import javax.swing.JLabel;

public class BeginCyclePanel extends PBPanel{
	
	private EndCyclePanel endCyclePanel;
	private JLabel cycleNumLabel;
	
	public BeginCyclePanel(final AskShowPanel mainPanel){
		super(mainPanel);
		endCyclePanel=null;
		cycleNumLabel=new JLabel();
		cycleNumLabel.setBounds(150, 5, 50, 25);
		add(cycleNumLabel);
	}

	public EndCyclePanel getEndCyclePanel() {
		return endCyclePanel;
	}

	public void setEndCyclePanel(EndCyclePanel endCyclePanel) {
		this.endCyclePanel = endCyclePanel;
	}
	public void setCycleNum(int num){
		cycleNumLabel.setText(""+num);
	}
}
