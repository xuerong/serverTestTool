package com.panel;

public class EndCyclePanel extends PBPanel{
	
	private BeginCyclePanel beginCyclePanel;
	
	public EndCyclePanel(final AskShowPanel mainPanel){
		super(mainPanel);
	}

	public BeginCyclePanel getBeginCyclePanel() {
		return beginCyclePanel;
	}

	public void setBeginCyclePanel(BeginCyclePanel beginCyclePanel) {
		this.beginCyclePanel = beginCyclePanel;
	}
	
}
