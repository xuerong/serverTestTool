package com.panel;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JPanel;

import com.VFlowLayout;
import com.askcomponent.BeginCycleComponent;
import com.askcomponent.Component;
import com.askcomponent.EndCycleComponent;
import com.askcomponent.PBComponent;
import com.askcomponent.VarProperty;
import com.askcomponent.VarType;

public class AskShowPanel extends JPanel {
	// 用来记录，没有添加beginCyclePanels的
	private Stack<BeginCyclePanel> beginCyclePanels=new Stack<BeginCyclePanel>();
	private List<Component> components=new ArrayList<Component>();
	
	private int componentNum;
	// 装component容器的当前高度
	private int preferredHeight=0;
	
	private int left=10;
	private int top=5;
	private int width=300;
	private int height=40;
	public AskShowPanel(){
		init();
	}
	public void init(){
		componentNum=0;
		//setLayout(null);
		setLayout(new VFlowLayout()); 
	}
	public void removeAllComponent(){
		for (java.awt.Component com : this.getComponents()) {
			if(com instanceof PBPanel){
				PBPanel panel=(PBPanel)com;
				remove(panel);
				components.remove(panel.getComponent());
			}
		}
		componentNum=0;
		preferredHeight=0;
		beginCyclePanels.clear();
	}
	public void removeComponent(PBPanel panel){
		int panelIndex=components.indexOf(panel.getComponent());
		remove(panel);
		components.remove(panel.getComponent());
		// 该panel之后的所有的cycle的begin和end都要减一
		subtract(panelIndex);
		switch (panel.getComponent().getComponentType()) {
		case CycleBegin:
			BeginCyclePanel beginCyclePanel=(BeginCyclePanel)panel;
			if(beginCyclePanel.getEndCyclePanel()!=null){
				remove(beginCyclePanel.getEndCyclePanel());
				panelIndex= components.indexOf(beginCyclePanel.getEndCyclePanel().getComponent());
				components.remove(panelIndex);
				componentNum--;
				subtract(panelIndex);
			}else{
				beginCyclePanels.remove(beginCyclePanel);
			}
			break;
		case CycleEnd:
			// 删除end的时候，begin也删除
			EndCyclePanel endCyclePanel=(EndCyclePanel)panel;
			remove(endCyclePanel.getBeginCyclePanel());
			panelIndex= components.indexOf(endCyclePanel.getBeginCyclePanel().getComponent());
			components.remove(panelIndex);
			componentNum--;
			subtract(panelIndex);
			break;
		case Pb:
	
			break;

		default:
			break;
		}
		componentNum--;
		preferredHeight-=(panel.getSize().height+top);
	}
	private void subtract(int panelIndex){
		for(int i=0;i<components.size();i++){
			switch (components.get(i).getComponentType()) {
			case CycleBegin:
				BeginCycleComponent bcom=(BeginCycleComponent)components.get(i);
				if(bcom.getBegin()>=panelIndex)
					bcom.setBegin(bcom.getBegin()-1);
				if(bcom.getEnd()>=panelIndex)
					bcom.setEnd(bcom.getEnd()-1);
				break;
			case CycleEnd:
				EndCycleComponent ecom=(EndCycleComponent)components.get(i);
				if(ecom.getBegin()>=panelIndex)
					ecom.setBegin(ecom.getBegin()-1);
				System.out.println("panelIndex:"+panelIndex+",begin:"+ecom.getBegin());
				break;
			case Pb:break;
			}
		}
	}
	
	public void addComponent(Component component){
		PBPanel panel=null;
		switch (component.getComponentType()) {
		case CycleBegin:
			BeginCycleComponent bcom=(BeginCycleComponent)component;
			bcom.setBegin(componentNum);
			System.out.println("set:"+componentNum+"");
			panel=new BeginCyclePanel(this);
			panel.setName("begin cycle");
			panel.setComponent(bcom);
			((BeginCyclePanel)panel).setEndCyclePanel(null);
			((BeginCyclePanel)panel).setCycleNum(bcom.getCycleNum());
			beginCyclePanels.push(((BeginCyclePanel)panel));
			break;
		case CycleEnd:
			if(beginCyclePanels.isEmpty())
				return;
			BeginCyclePanel beginPanel=beginCyclePanels.pop();
			((BeginCycleComponent)beginPanel.getComponent()).setEnd(componentNum);
			
			EndCycleComponent ecom=(EndCycleComponent)component;
			ecom.setBegin(((BeginCycleComponent)beginPanel.getComponent()).getBegin());
			System.out.println("get:"+((BeginCycleComponent)beginPanel.getComponent()).getBegin());
			panel=new EndCyclePanel(this);
			panel.setName("end cycle");
			panel.setComponent(ecom);
			beginPanel.setEndCyclePanel((EndCyclePanel)panel);
			((EndCyclePanel)panel).setBeginCyclePanel(beginPanel);
			break;
		case Pb:
			PBComponent pcom=(PBComponent)component;
			panel=new PBPacketPanel(this);
			panel.setName(pcom.getName());
			panel.setComponent(pcom);
			
			break;

		default:
			break;
		}
		panel.setPreferredSize(panel.getSize());
		preferredHeight+=(panel.getSize().height+top);
		components.add(component);
		add(panel);
		componentNum++;
		setPreferredSize(new Dimension(getPreferredSize().width, preferredHeight+top));
		//System.out.println(panel.getWidth());
		updateUI();
	}
	public List<Component> getComponentList() {
		java.awt.Component[] coms=getComponents();
		for (java.awt.Component com : coms) {
			if(com instanceof PBPacketPanel){
				PBPacketPanel panel=(PBPacketPanel)com;
				List<PBVarPanel> varPanels= panel.getVarPanels();
				for (PBVarPanel pbVarPanel : varPanels) {
					pbVarPanel.getVar();// 将刚刚更改的值赋给对应的变量
				}
			}
		}
		return components;
	}
}
