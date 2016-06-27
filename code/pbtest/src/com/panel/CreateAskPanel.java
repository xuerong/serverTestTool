package com.panel;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.VFlowLayout;
import com.panel.AskShowPanel;
import com.askcomponent.Ask;
import com.askcomponent.BeginCycleComponent;
import com.askcomponent.Component;
import com.askcomponent.EndCycleComponent;
import com.askcomponent.PBComponent;
import com.file.AskOper;
import com.file.PBOper;
import com.io.IoServer;

public class CreateAskPanel extends JPanel {
	public CreateAskPanel(){
		init();
	}
	
	private List<PBComponent> pbComList=new ArrayList<PBComponent>();
	private int formWidth=1240;
	private int formHeight=800;
	
	AskShowPanel mainPanel;
	JScrollBar bar=null;
	String currentName="undefine";
	
	JTextField urlField=null;
	
	public void init(){
		
		JLabel label=new JLabel("URL:");
		label.setBounds(10, 10, 40, 25);
		add(label);
		urlField=new JTextField("http://192.168.2.104:8080/GameServer/s");
		urlField.setBounds(50, 10, formWidth-290, 25);
		add(urlField);
		
		//setBackground(Color.BLUE);
		mainPanel=new AskShowPanel();
		//mainPanel.setSize(formWidth-200, formHeight);
		mainPanel.setPreferredSize(new Dimension(formWidth-230, 10));
		JScrollPane scrollPane=new JScrollPane(mainPanel);
		bar=scrollPane.getVerticalScrollBar();
		scrollPane.setBounds(10, 40, formWidth-220, formHeight-100);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);
		
		pbComList=PBOper.getInstance().getCsMessageList();
		
		setSize(formWidth, formHeight);
		setLayout(null);
		
		final JPanel panel=createComponentPanel();
		scrollPane=new JScrollPane(panel);
		scrollPane.setBounds(formWidth-200, 40,panel.getSize().width+10,formHeight-160);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);
		//panel.setLocation(0, -800);
		JLabel searchLabel=new JLabel("search");
		searchLabel.setBounds(formWidth-210, 10,50,25);
		add(searchLabel);
		final JTextField seachField=new JTextField();
		seachField.setBounds(formWidth-165, 10,150,25);
		seachField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				doSeachPb(seachField,panel);
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				doSeachPb(seachField,panel);
			}
			@Override
			public void changedUpdate(DocumentEvent e) {}
		});
		add(seachField);
		
		// 循环
		final JTextField cycleNumText=new JTextField();
		cycleNumText.setText(""+3);
		cycleNumText.setBounds(formWidth-70, formHeight-110,60,40);
		add(cycleNumText);
		
		JButton button=new JButton();
		button.setText("添加循环头");
		button.setBounds(formWidth-200, formHeight-110,120,40);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BeginCycleComponent beginCycleComponent=new BeginCycleComponent();
				beginCycleComponent.setCycleNum(Integer.parseInt(cycleNumText.getText().trim()));
				mainPanel.addComponent(beginCycleComponent);
			    bar.setValue(bar.getMaximum());
			}
		});
		add(button);
		button=new JButton();
		button.setText("添加循环尾");
		button.setBounds(formWidth-200, formHeight-60,120,40);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				mainPanel.addComponent(new EndCycleComponent());
			    bar.setValue(bar.getMaximum());
			}
		});
		add(button);
		
		// 载入
		button=new JButton("载入");
		button.setBounds(formWidth-800, formHeight-50,160,40);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel panel=createAskListPanel();
				JScrollPane scrollPane=new JScrollPane(panel);
				scrollPane.setBounds(600, 0,panel.getSize().width+10,400);
				scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				JDialog dialog = new JDialog();
				dialog.add(scrollPane);
				dialog.setSize(panel.getSize().width+10,400);
				dialog.setLocationRelativeTo(null);
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
		add(button);
		
		// 保存
		button=new JButton("保存");
		button.setBounds(formWidth-600, formHeight-50,160,40);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name=JOptionPane.showInputDialog("name", currentName);
				System.out.println(name);
				if(name==null)
					return;
				if(name==""){
					name="lastsave";
				}
				List<Component> comList=mainPanel.getComponentList();
				if(AskOper.getInstance().saveAsk(name,comList)){
					currentName=name;
					JOptionPane.showMessageDialog(null, "save success");
				}else{
					JOptionPane.showMessageDialog(null, "save fail");
				}
			}
		});
		add(button);
		
		// 发送
		button=new JButton("发送");
		button.setBounds(formWidth-400, formHeight-50,160,40);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String url=urlField.getText().trim();
				String check = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
				Pattern pattern =Pattern.compile(check);
	            if(!pattern.matcher(url).matches()){
	            	JOptionPane.showMessageDialog(null, "please input right url address ");
	            	return;
	            }
				IoServer.getInstance().setUrlStr(url);
				Ask ask=new Ask(null,mainPanel.getComponentList(),null);
				ask.startUp(-1);
			}
		});
		add(button);
		
		setVisible(true);
	}
	
	private JPanel createComponentPanel(){
		JPanel panel=new JPanel();
		panel.setLayout(null);
		int left=10;
		int top=5;
		int width=160;
		int height=40;
		int i=0;
		for (final PBComponent component : pbComList) {
			JButton button=new JButton(component.getName());
			button.setBounds(left, i*(height+top)+top, width, height);
			
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mainPanel.addComponent(component.clone());
				    bar.setValue(bar.getMaximum());
				}
			});
			panel.add(button);
			i++;
		}
		panel.setSize(left*2+width, pbComList.size()*(top+height)+top);
		panel.setPreferredSize(new Dimension(left*2+width, pbComList.size()*(top+height)+top));
		return panel;
	}
	
	private JPanel createAskListPanel(){
		JPanel panel=new JPanel();
		panel.setLayout(new VFlowLayout());
		HashMap<String, List<Component>> comListMap=AskOper.getInstance().readAsk();
		Iterator iter = comListMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			final String name = (String)entry.getKey();
			final List<Component> comList = (List<Component>)entry.getValue();
			JButton button=new JButton(name);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mainPanel.removeAllComponent();
					currentName=name;
					for (Component component : comList) {
						mainPanel.addComponent(component);
						if(component instanceof PBComponent){
							PBComponent pbComponent=(PBComponent)component;
						}
						
					}
					bar.setValue(bar.getMaximum());
				}
			});
			panel.add(button);
		}
		return panel;
	}
	private void doSeachPb(JTextField seachField , JPanel panel){
		String seachStr=seachField.getText().trim();
		if(seachStr.length()==0)
			return;
		int i=0;
		for (PBComponent component : pbComList) {
			if(component.getName().toLowerCase().startsWith(seachStr)){
				panel.setLocation(0, -1*(45*i));
				break;
			}
			i++;
		}
	}
}
