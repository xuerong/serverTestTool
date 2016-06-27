package com.panel;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.TableView.TableRow;

import com.askcomponent.Ask;
import com.askcomponent.Component;
import com.askcomponent.DataPool;
import com.askcomponent.PBComponent;
import com.askcomponent.PBVar;
import com.file.AskOper;
import com.file.DataPoolOper;
import com.google.protobuf.Message;
import com.io.IoServer;
import com.io.PBPacket;
import com.panel.inputfield.Int64InputField;
import com.panel.robot.Player;
import com.panel.robot.Robot;
import com.protocol.HOpCodeEx;
import com.userhandle.DataPoolDefault;

public class DoTestPanel extends JPanel {
	public DoTestPanel(){
		setSize(formWidth, formHeight);
		setLayout(null);
		init();
		setVisible(true);
	}
	
	private int formWidth=1240;
	private int formHeight=800;
	MyTableModel model=null;
	JTable table=null;
	JTable showDataPoolDataTable=null;
	JTable showAskPbTimeTable=null;
	JTextField urlText=null;
	HashMap<Integer,Robot> robots=new HashMap<Integer,Robot>();
	int robotOverNum=0;
	JTextArea showAskInfoTextArea=null;
	// 导入的datapoolid和对应ask
	HashMap<Long,String> loadDataPools=new HashMap<Long, String>();
	JLabel selectNumShow=null;
	public void init(){
		// url
		JLabel url=new JLabel("URL:");
		url.setBounds(10, 10, 30, 25);
		add(url);
		urlText=new JTextField("http://192.168.2.104:8080/GameServer/s");
		urlText.setBounds(50, 10, formWidth-100, 25);
		add(urlText);
		// table
		String[] columnNameStrings={"index","accoundId","askModel","state","oper"};
		model = new MyTableModel(new Object [0][5],columnNameStrings);
		
		table=new JTable(model);
		table.setRowHeight(25);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					// clear table 时，并没有清除选择行数，导致报错
					System.out.println(table.getSelectedRow()+"--"+table.getRowCount());
					if(table.getSelectedRow()<=table.getRowCount()){
						refreshStateShow();
					}
				}
			}
		});
		
		setTableWidth(table, new int[]{55,100,140,230});
		JScrollPane scrollPane = new JScrollPane(table);  
		table.setFillsViewportHeight(true);
		scrollPane.setBounds(10, 50, formWidth-500, formHeight-200);
		add(scrollPane);
		
		JLabel label=new JLabel("Show Player Info:");
		label.setBounds(formWidth-485, 40, 150, 25);
		add(label);
		scrollPane=createShowPlayerInfoPanel();
		scrollPane.setLocation(formWidth-485, 62);
		add(scrollPane);
		
		label=new JLabel("Show Component Time:");
		label.setBounds(formWidth-240, 40, 150, 25);
		add(label);
		scrollPane=createShowComponentTimePanel();
		scrollPane.setLocation(formWidth-240, 62);
		add(scrollPane);
		
		label=new JLabel("Show Ask Information:");
		label.setBounds(formWidth-485, 465, 150, 25);
		add(label);
		JPanel panel=createShowAskInfoPanel();
		panel.setLocation(formWidth-488, 482);
		add(panel);
		
		JPanel addRobotPanel=createAddRobotPanel();
		addRobotPanel.setLocation(10, formHeight-140);
		add(addRobotPanel);
		
		JButton startRobot=new JButton("StartRobot");
		startRobot.setBounds(formWidth-610,  formHeight-140, 100, 50);
		startRobot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String url=urlText.getText().trim();
				String check = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
				Pattern pattern =Pattern.compile(check);
	            if(!pattern.matcher(url).matches()){
	            	JOptionPane.showMessageDialog(null, "please input right url address ");
	            	return;
	            }
				IoServer.getInstance().setUrlStr(urlText.getText().trim());
				robotOverNum=0;
				for (final Robot robot : robots.values()) {
					new Thread(){
						public void run(){
							robot.getAsk().startUp(robot.getIndex());
							robotOverNum++;
							// 全部结束，保存游戏
							if(robotOverNum==robots.size()){
								List<DataPool> dataPools=new ArrayList<DataPool>();
								for (Robot robot : robots.values()) {
									if(robot.getStatus().isOver() && robot.getStatus().getCurrentStep()==robot.getStatus().getAllStep()){
										dataPools.add(robot.getDataPool());
									}
								}
								if(DataPoolOper.getInstance().saveDataPools(dataPools)){
									showInfo("save datapool success,num="+dataPools.size());
								}else{
									showInfo("save datapool fail,num="+dataPools.size());
								}
							}
						}
					}.start();
				}
			}
		});
		add(startRobot);
		
		JButton removePlayerButton=new JButton("RemoveAll");
		removePlayerButton.setMargin(new Insets(0, 0, 0, 0));
		removePlayerButton.setBounds(formWidth-610,  formHeight-80, 100, 50);
		removePlayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				robots.clear();
				model.getDataVector().clear();
				//model.fireTableDataChanged();//通知模型更新
				
				table.updateUI();//刷新表格
			}
		});
		add(removePlayerButton);
	}
	private JScrollPane createShowPlayerInfoPanel(){
		String[] columnNameStrings={"key","value"};
		MyTableModel model = new MyTableModel(new Object [0][2],columnNameStrings);
		
		showDataPoolDataTable=new JTable(model);
		showDataPoolDataTable.setRowHeight(25);
		
		JScrollPane scrollPane = new JScrollPane(showDataPoolDataTable);  
		showDataPoolDataTable.setFillsViewportHeight(true);
		scrollPane.setSize(240, 400);
		return scrollPane;
	}
	private JScrollPane createShowComponentTimePanel(){
		String[] columnNameStrings={"pb","time(ms)"};
		MyTableModel model = new MyTableModel(new Object [0][2],columnNameStrings);
		
		showAskPbTimeTable=new JTable(model);
		showAskPbTimeTable.setRowHeight(25);
		showAskPbTimeTable.getColumnModel().getColumn(0).setPreferredWidth(125);
		JScrollPane scrollPane = new JScrollPane(showAskPbTimeTable);  
		showAskPbTimeTable.setFillsViewportHeight(true);
		scrollPane.setSize(230, 400);
		
		return scrollPane;
	}
	private JPanel createShowAskInfoPanel(){
		JPanel panel=new JPanel();
		showAskInfoTextArea=new JTextArea("",17,43);
		showAskInfoTextArea.setLineWrap(true);
		JScrollPane scrollPane=new JScrollPane(showAskInfoTextArea);
		scrollPane.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		scrollPane.setLocation(0, 0);
		panel.add(scrollPane);
		panel.setSize(480, 400);
		return panel;
	} 
	private JPanel createAddRobotPanel(){
		JPanel panel=new JPanel();
		panel.setLayout(null);
		panel.setSize(600,125);
		
		JLabel title=new JLabel("create new player");
		title.setBounds(0, 0, 160, 25);
		panel.add(title);
		
		JLabel pNumLabel=new JLabel("Number");
		pNumLabel.setBounds(0, 30, 65, 25);
		panel.add(pNumLabel);
		
		final JTextField pNumText=new JTextField(7);
		pNumText.setBounds(50, 30, 70, 25);
		pNumText.setText(""+30);
		panel.add(pNumText);
		
		JLabel pIdLabel=new JLabel("IdFrom");
		pIdLabel.setBounds(135, 30, 55, 25);
		panel.add(pIdLabel);
		
		final JTextField pIdText=new JTextField(7);
		pIdText.setBounds(180, 30, 70, 25);
		pIdText.setText(""+1000);
		panel.add(pIdText);
		
		JLabel askTypeLabel=new JLabel("AskType");
		askTypeLabel.setBounds(260, 30, 85, 25);
		panel.add(askTypeLabel);
		
		final JComboBox<String> askTypeCom=new JComboBox<String>();
		askTypeCom.setBounds(315, 30, 100, 25);
		for (String askName : AskOper.getInstance().readAsk().keySet()) {
			askTypeCom.addItem(askName);
		}
		panel.add(askTypeCom);
		
		JButton addPlayerButton=new JButton("Add");
		addPlayerButton.setMargin(new Insets(0, 0, 0, 0));
		addPlayerButton.setBounds(430, 30, 70, 25);
		addPlayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str=pNumText.getText().trim();
				if(!isNumber(str)){
					JOptionPane.showMessageDialog(null, "PlayerNum为数字");
					return;
				}
				long playerNum=Long.parseLong(str);
			    if(playerNum<=0 || playerNum>10000000){
			    	JOptionPane.showMessageDialog(null, "0<PlayerNum<=10000000");
					return;
			    }
			    
			    str=pIdText.getText().trim();
				if(!isNumber(str)){
					JOptionPane.showMessageDialog(null, "pIdText为数字");
					return;
				}
				long playerId=Long.parseLong(str);
			    if(playerId<=0 || playerId>10000000){
			    	JOptionPane.showMessageDialog(null, "0<playerId<=10000000");
					return;
			    }
			    String askName=askTypeCom.getSelectedItem().toString();
			    List<Component> components = AskOper.getInstance().readAsk().get(askName);
			    
			    
			    int robotNum=robots.size();
			    for(int i=0;i<playerNum;i++){
			    	List<Component> coms=new ArrayList<Component>();
			    	for (Component component : components) {
						coms.add(component.clone());
					}
			    	Robot robot=new Robot();
			    	robot.setDataPool(new DataPoolDefault(playerId+i));
			    	
			    	Ask ask=new Ask(askName, coms,robot.getDataPool());
			    	robot.setAsk(ask);
			    	
			    	robot.setIndex(robotNum++);
			    	Robot.Status state=new Robot.Status();
			    	state.setAllStep(ask.getAskStepNum());
			    	state.setCurrentStep(0);
			    	for (Component component : coms) {
						if(component instanceof PBComponent){
							PBComponent pbComponent=(PBComponent)component;
							state.setCurrentComponent(pbComponent);
							break;
						}
					}
			    	robot.setStatus(state);
			    	
			    	addRobot(robot);
			    	robots.put(robot.getIndex(),robot);
			    }
			}
		});
		panel.add(addPlayerButton);
		
		// load old player
		title=new JLabel("load old player");
		title.setBounds(0, 65, 160, 25);
		panel.add(title);
		
		JButton selectPlayer=new JButton("loadPlayer");
		selectPlayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HashMap<Long,String> dataPools=getDataPoolAndAsks();
				loadDataPools.clear();
				for (Entry<Long, String> entry : dataPools.entrySet()) {
					loadDataPools.put(entry.getKey(), entry.getValue());
				}
				selectNumShow.setText(loadDataPools.size()+" loaded");
			}
		});
		selectPlayer.setMargin(new Insets(0, 0, 0, 0));
		selectPlayer.setBounds(0, 95, 75, 25);
		panel.add(selectPlayer);
		
		selectNumShow=new JLabel("0 loaded");
		selectNumShow.setBounds(80,95,100,25);
		panel.add(selectNumShow);
		
//		askTypeLabel=new JLabel("AskType");
//		askTypeLabel.setBounds(180, 95, 85, 25);
//		panel.add(askTypeLabel);
//		
//		final JComboBox<String> askTypeCom2=new JComboBox<String>();
//		askTypeCom2.setBounds(235, 95, 100, 25);
//		for (String askName : AskOper.getInstance().readAsk().keySet()) {
//			askTypeCom2.addItem(askName);
//		}
//		panel.add(askTypeCom2);
		
		addPlayerButton=new JButton("Add");
		addPlayerButton.setMargin(new Insets(0, 0, 0, 0));
		addPlayerButton.setBounds(350, 95, 70, 25);
		addPlayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    //List<Component> components = AskOper.getInstance().readAsk().get(askName);
			    int robotNum=robots.size();
			    HashMap<Long, DataPool> dataPools=DataPoolOper.getInstance().getDataPools();
			    HashMap<String, List<Component>> asks=AskOper.getInstance().readAsk();
			    for (Entry<Long, String> entry : loadDataPools.entrySet()) {
			    	Robot robot=new Robot();
			    	robot.setDataPool(dataPools.get(entry.getKey()));
			    	
			    	List<Component> coms=new ArrayList<Component>();
			    	
			    	for (Component component : asks.get(entry.getValue())) {
						coms.add(component.clone());
					}
			    	Ask ask=new Ask(entry.getValue(), coms,robot.getDataPool());
			    	robot.setAsk(ask);
			    	robot.setIndex(robotNum++);
			    	Robot.Status state=new Robot.Status();
			    	state.setAllStep(ask.getAskStepNum());
			    	state.setCurrentStep(0);
			    	for (Component component : coms) {
						if(component instanceof PBComponent){
							state.setCurrentComponent((PBComponent)component);
							break;
						}
					}
			    	robot.setStatus(state);
			    	addRobot(robot);
			    	robots.put(robot.getIndex(),robot);
				}
			}
		});
		panel.add(addPlayerButton);
		
		return panel;
	}
	private boolean isNumber(String str){
		if(str.length()<=0){
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");  
	    if(!pattern.matcher(str).matches()){
			return false;
	    }
	    return true;
	}
	private void setTableWidth(JTable table,int[] widths){
		int num=widths.length;
		for (int i=0;i<num;i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
			table.getColumnModel().getColumn(i).setMaxWidth(widths[i]);
			table.getColumnModel().getColumn(i).setMinWidth(widths[i]);
		}
		// 最后一行自适应
		table.getColumnModel().getColumn(num).setPreferredWidth(10);
	}
	private void refreshStateShow(){
		 HashMap<String, Object> dataPoolData = robots.get(table.getSelectedRow()).getDataPool().getDataPoolData();
         
         MyTableModel model=(MyTableModel)showDataPoolDataTable.getModel();
         model.getDataVector().clear();
			//model.fireTableDataChanged();//通知模型更新
			
         for (Entry<String, Object> entry : dataPoolData.entrySet()) {
         	model.addRow(new Object[]{entry.getKey(),entry.getValue()});
			}
         
         Ask ask = robots.get(table.getSelectedRow()).getAsk();
         List<Component> comList=ask.getComList();
         long[] useTime= ask.getUseTimes();
         
         model=(MyTableModel)showAskPbTimeTable.getModel();
         model.getDataVector().clear();
			//model.fireTableDataChanged();//通知模型更新
         model.addRow(new Object[]{"all time",0});
         long allTime=0;
         for (int i=0,len=useTime.length;i<len;i++) {
         	String key="";
         	Component com = comList.get(i);
         	if(com instanceof PBComponent){
         		key=((PBComponent)com).getName();
         	}else{
         		key= com.getClass().toString();
         	}
         	model.addRow(new Object[]{key,useTime[i]});
         	if(useTime[i]!=-1){
         		allTime+=useTime[i];
         	}
		 }
         model.setValueAt(allTime, 0, 1);
	}
	private void addRobot(Robot robot){
		Vector<String> strs=new Vector<String>();
		strs.add(""+robot.getIndex());
		strs.add(""+robot.getDataPool().getAccountId());
		strs.add(robot.getAsk().getAskName());
		strs.add(""+robot.getStatus().toString());
		strs.add(""+robot.getIndex());
		model.addRow(strs);
	}
	public void showAskResult(int robotIndex,PBComponent component,PBPacket packet){
		Robot robot=robots.get(robotIndex);
		if(packet.getResult()==1){
			Robot.Status state=robot.getStatus();
			state.setCurrentStep(state.getCurrentStep()+1);
			state.setCurrentComponent(component);
			if(state.getCurrentStep()==state.getAllStep()){
				state.setOver(true);
			}
			refreshRobotState(robot);
		}else{
			String info="index:"+robot.getIndex()+",accountId:"+robot.getDataPool().getAccountId()+",send:"+component.getName()+",result:"+packet.getResult();
			showInfo(info);
			robot.getStatus().setOver(true);
			refreshRobotState(robot);
		}
		if(robot.getIndex()==table.getSelectedRow()){
			refreshStateShow();
		}
	}
	private void refreshRobotState(Robot robot){
		Robot.Status state=robot.getStatus();
		if(state.isOver()){
			String contentString=state.getCurrentComponent().getName()+"("+state.getCurrentStep()+"/"+state.getAllStep()+")";
			if(state.getCurrentStep()!=state.getAllStep()){
				contentString="<html><font color='red'>"+contentString+"\tfail</font></html>";
			}else{
				contentString="<html><font color='green'>"+contentString+"\tsuccess</font></html>";
			}
			model.setValueAt(contentString, (int)robot.getIndex(), 3);
		}else{
			model.setValueAt(state.getCurrentComponent().getName()+"("+state.getCurrentStep()+"/"+state.getAllStep()+")", (int)robot.getIndex(), 3);
		}
	}
	private void showInfo(String str){
		showAskInfoTextArea.append(str);
		showAskInfoTextArea.append("\n");
	}
	// 返回所选择的datapool和对应的ask
	private HashMap<Long, String> getDataPoolAndAsks(){
		final HashMap<Long,String> result=new HashMap<Long, String>();
		
		final HashMap<Long,DataPool> dataPools=DataPoolOper.getInstance().getDataPools();
		final HashMap<String, List<Component>> asks=AskOper.getInstance().readAsk();
		
		final JDialog dialog=new JDialog();
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.setSize(600, 600);
		dialog.setLayout(null);
		
		String[] columnNameStrings={"accountId","askType","selected"};
		final DataPoolShowTableModel model = new DataPoolShowTableModel(new Object [0][3],columnNameStrings);
		
		final JTable table=new JTable(model);
		table.setRowHeight(25);
		
		//table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		setTableWidth(table, new int[]{120,160});
		JScrollPane scrollPane = new JScrollPane(table);  
		table.setFillsViewportHeight(true);
		scrollPane.setBounds(10, 10, 340, 520);
		dialog.add(scrollPane);
		
		for (DataPool dataPool : dataPools.values()) {
			Vector vector=new Vector();
			vector.add(dataPool.getAccountId());
			JComboBox<String> c=new JComboBox<String>();
			String initAskName="";
			for (String askName : asks.keySet()) {
				c.addItem(askName);
				initAskName=askName;
			}
			vector.add(initAskName);
			table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(c));  
			table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer());  
			vector.add(false);
			model.addRow(vector);
		}
		
		// accountId range
		JLabel label=new JLabel("accountId range");
		label.setBounds(360, 10, 100, 25);
		dialog.add(label);
		
		label=new JLabel("From");
		label.setBounds(360, 38, 40, 25);
		dialog.add(label);
		
		final Int64InputField textFrom=new Int64InputField(0l);
		textFrom.setBounds(390, 38, 90, 22);
		dialog.add(textFrom);
		
		label=new JLabel("To");
		label.setBounds(482, 38,18, 25);
		dialog.add(label);
		
		final Int64InputField textTo=new Int64InputField(10000000l);
		textTo.setBounds(500, 38, 90, 22);
		dialog.add(textTo);
		// selected
		label=new JLabel("selected");
		label.setBounds(360, 70, 100, 25);
		dialog.add(label);
		
		JCheckBox checkBox=new JCheckBox("Select All");
		checkBox.setBounds(360, 92, 80, 25);
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox box=(JCheckBox)e.getSource();
				for(int i=0;i<table.getRowCount();i++){
					long accountId=(long) table.getValueAt(i, 0);
					if(accountId<=textTo.getValue() && accountId>=textFrom.getValue()){
						table.setValueAt(box.isSelected(), i, 2);
					}
				}
			}
		});
		dialog.add(checkBox);
		
		JButton button=new JButton("Reverse");
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBounds(460, 92, 80, 22);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<table.getRowCount();i++){
					long accountId=(long) table.getValueAt(i, 0);
					//accountId
					if(accountId<=textTo.getValue() && accountId>=textFrom.getValue()){
						table.setValueAt(!((boolean)table.getValueAt(i, 2)), i, 2);
					}
				}
			}
		});
		dialog.add(button);
		
		// askType
		label=new JLabel("AskType");
		label.setBounds(360, 120, 100, 25);
		dialog.add(label);
		
		JComboBox<String> askTypeBox=new JComboBox<String>();
		for (String askName : asks.keySet()) {
			askTypeBox.addItem(askName);
		}
		askTypeBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JComboBox<String> box=(JComboBox<String>)e.getSource();
				for(int i=0;i<table.getRowCount();i++){
					long accountId=(long) table.getValueAt(i, 0);
					//accountId
					if(accountId<=textTo.getValue() && accountId>=textFrom.getValue()){
						table.setValueAt(box.getSelectedItem(), i, 1);
					}
				}
			}
		});
		askTypeBox.setBounds(360, 145, 120, 25);
		dialog.add(askTypeBox);
		
		button=new JButton("Random");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<table.getRowCount();i++){
					long accountId=(long) table.getValueAt(i, 0);
					int size=asks.keySet().size();
					if(accountId<=textTo.getValue() && accountId>=textFrom.getValue()){
						table.setValueAt(asks.keySet().toArray()[(int) (Math.random()*size)], i, 1);
					}
				}
			}
		});
		button.setBounds(490, 145, 100, 25);
		dialog.add(button);
		
		// consfirm and cancel
		button=new JButton("confirm");
		button.setBounds(500, 530, 80, 28);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index=0;
				for (DataPool dataPool : dataPools.values()) {
					if(model.getValueAt(index, 2)==Boolean.TRUE){
						result.put(dataPool.getAccountId(), model.getValueAt(index, 1).toString());
					}
					index++;
				}
				dialog.dispose();
			}
		});
		dialog.add(button);
		
		button=new JButton("cancel");
		button.setBounds(400, 530, 80, 28);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		dialog.add(button);
		
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		
		return result;
	}
	public static final class MyTableModel extends DefaultTableModel{
		public MyTableModel(Object[][] data, Object[] columnNames){
			super(data, columnNames);//这里一定要覆盖父类的构造方法，否则不能实例myTableModel
		}
		public boolean isCellEditable(int row, int column){
			return false;//父类的方法里面是 return true的，所以就可以编辑了~~~
		}
	}
	public static final class DataPoolShowTableModel extends DefaultTableModel{
		public DataPoolShowTableModel(Object[][] data, Object[] columnNames){
			super(data, columnNames);//这里一定要覆盖父类的构造方法，否则不能实例myTableModel
		}
		public boolean isCellEditable(int row, int column){
			if(column==0)
				return false;//父类的方法里面是 return true的，所以就可以编辑了~~~
			return true;
		}
		public Class<?> getColumnClass(int columnIndex) {
			return   getValueAt(0,   columnIndex).getClass();   
		}
	}
}
