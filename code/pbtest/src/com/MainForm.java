package com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javassist.bytecode.analysis.Util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.panel.AskShowPanel;
import com.panel.CreateAskPanel;
import com.panel.DoTestPanel;
import com.askcomponent.Ask;
import com.askcomponent.BeginCycleComponent;
import com.askcomponent.Component;
import com.askcomponent.EndCycleComponent;
import com.askcomponent.PBComponent;
import com.file.AskOper;
import com.file.PBOper;

public class MainForm extends JFrame{
	public static void main(String[] args){
		mainForm=new MainForm();
	}
	private static MainForm mainForm=null;
	public static MainForm getMainForm(){
		return mainForm;
	}
	private List<PBComponent> pbComList=new ArrayList<PBComponent>();
	private int formWidth=1240;
	private int formHeight=920;
	private static final int FUNCTIONBUTTONHEIGHT=80;
	
	AskShowPanel mainPanel;
	JScrollBar bar=null;
	String currentName="undefine";
	
	CreateAskPanel createAskPanel =null;
	DoTestPanel doTestPanel=null;
	
	private MainForm(){
		setSize(formWidth, formHeight);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);
		
		init();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	public void init(){
		
		createAskPanel = new CreateAskPanel();
		createAskPanel.setLocation(0, 0);
		createAskPanel.setVisible(false);
		add(createAskPanel);
		
		doTestPanel = new DoTestPanel();
		doTestPanel.setLocation(0, 0);
		add(doTestPanel);
		
		JPanel mainFunctionJPanel=createMainFunctionPanel();
		mainFunctionJPanel.setLocation(0, formHeight-120);
		add(mainFunctionJPanel);
		
	}
	private JPanel createMainFunctionPanel(){
		JPanel panel=new JPanel();
		panel.setLayout(null);
		panel.setSize(1240, 120);
		panel.setBackground(Color.red);
		
		JButton createAskButton=new JButton("haha1");
		createAskButton.setBounds(20, 10, FUNCTIONBUTTONHEIGHT, FUNCTIONBUTTONHEIGHT);
		createAskButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doTestPanel.setVisible(false);
				createAskPanel.setVisible(true);
			}
		});
		panel.add(createAskButton);
		
		JButton doTest=new JButton("doTest");
		doTest.setBounds(20*3+FUNCTIONBUTTONHEIGHT, 10, FUNCTIONBUTTONHEIGHT, FUNCTIONBUTTONHEIGHT);
		doTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doTestPanel.setVisible(true);
				createAskPanel.setVisible(false);
			}
		});
		panel.add(doTest);
		
		return panel;
	}
	public DoTestPanel getDoTestPanel() {
		return doTestPanel;
	}
	
}
