package com.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.askcomponent.PBComponent;
import com.askcomponent.PBVar;
import com.askcomponent.VarProperty;
import com.askcomponent.VarType;
import com.panel.inputfield.InputFieldInterface;
import com.panel.inputfield.Int32InputField;
import com.panel.inputfield.Int64InputField;
import com.panel.inputfield.PBInputField;
import com.panel.inputfield.StringInputField;

public class PBVarPanel extends JPanel implements ActionListener{
	
	public static final int BUTTONWIDTH=100;
	public static final int BUTTONHEIGHT=25;
	
	public static final int INTWIDTH=200;
	public static final int INTHEIGHT=25;
	
	public static final int STRINGWIDTH=200;
	public static final int STRINGHEIGHT=25;
	
	private static final int LABELHEIGHT=25;
	
	public static final int LEFT=10;
	
	public static final int CHARWIDTH=8;
	
	private PBVar var;
	
	InputFieldInterface inputField=null;
	
	private JDialog showRequiredFrame=null;
	
	public PBVarPanel(PBVar var){
		this.var=var;
		this.setLayout(null);
		init();
	}
	
	private void init(){
		if(var.getProperty()==VarProperty.Optional || var.getProperty()==VarProperty.Required){
			JLabel label=null;
			switch(var.getType()){
			case Int32:
				label=new JLabel("<html>"+var.getName()+" <font color=blue>["+VarType.getStringByType(VarType.Int32)+"]</font></html>");
				label.setBounds(0, 0, (var.getName().length()+5)*CHARWIDTH, LABELHEIGHT);
				this.add(label);
				Int32InputField input32Field=new Int32InputField((Integer)var.getValue());
				input32Field.setLocation(label.getSize().width, 0);
				inputField=input32Field;
				this.add(input32Field);
				this.setSize(label.getSize().width+input32Field.getSize().width, INTHEIGHT);
				break;
			case Int64:
				//this.setSize(INTWIDTH, INTHEIGHT);
				label=new JLabel("<html>"+var.getName()+" <font color=blue>["+VarType.getStringByType(VarType.Int64)+"]</font></html>");
				label.setBounds(0, 0, (var.getName().length()+5)*CHARWIDTH, LABELHEIGHT);
				this.add(label);
				Int64InputField input64Field=new Int64InputField((Long)var.getValue());
				input64Field.setLocation(label.getSize().width, 0);
				inputField=input64Field;
				this.add(input64Field);
				this.setSize(label.getSize().width+input64Field.getSize().width, INTHEIGHT);
				break;
			case String:
				//this.setSize(STRINGWIDTH, STRINGHEIGHT);
				label=new JLabel("<html>"+var.getName()+" <font color=blue>["+VarType.getStringByType(VarType.String)+"]</font></html>");
				label.setBounds(0, 0, (var.getName().length()+5)*CHARWIDTH, LABELHEIGHT);
				this.add(label);
				StringInputField inputStringField=new StringInputField((String)var.getValue());
				inputStringField.setLocation(label.getSize().width, 0);
				inputField=inputStringField;
				this.add(inputStringField);
				this.setSize(label.getSize().width+inputStringField.getSize().width, INTHEIGHT);
				break;
			case Pb:
				PBInputField pbInputField=new PBInputField("<html>"+var.getName()+" <font color=blue>["+
							VarType.getStringByType(VarType.Pb)+"]</font></html>",(PBComponent)var.getValue());
				pbInputField.setLocation(0, 5);
				pbInputField.setMargin(new java.awt.Insets(0,0,0,0)); 
				//pbInputField.setLocation(0, 0);
				pbInputField.setBounds(0, 0,(var.getName().length()+5)*CHARWIDTH,BUTTONHEIGHT);
				this.setSize(pbInputField.getSize());
				inputField=pbInputField;
				this.add(pbInputField);
				break;
			}
		}else if(var.getProperty()==VarProperty.Repeated){
			JButton button=new JButton("<html>"+var.getName()+" <font color=red>["+VarType.getStringByType(var.getType())+"]</font></html>");
			button.setMargin(new java.awt.Insets(0,0,0,0)); 
			button.setBounds(0, 0,(var.getName().length()+5)*CHARWIDTH,BUTTONHEIGHT);
			this.setSize(button.getSize());
			button.addActionListener(this);
			
			add(button);
		}
	}
	
	public PBVar getVar() {
		if(var.getProperty()!=VarProperty.Repeated)
			var.setValue(inputField.getValue());
		return var;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		showRequiredFrame=new JDialog();
		
		showRequiredFrame.setLayout(null);
		showRequiredFrame.setModalityType(ModalityType.TOOLKIT_MODAL);
		
		FloatPanel floatPanel=new FloatPanel(var);
		floatPanel.initCurrentVar();
		floatPanel.setVisible(false);
		showRequiredFrame.add(floatPanel);
		showRequiredFrame.setSize(floatPanel.getSize().width, floatPanel.getSize().height+50);
		showRequiredFrame.setLocationRelativeTo(null);
		
		floatPanel.setVisible(true);
		showRequiredFrame.setVisible(true);
	}
	/**
	 * 用于编辑required变量的面板
	 * **/
	class FloatPanel extends JPanel implements ActionListener{
		public static final int WIDTH=100;
		public static final int HEIGHT=30;
		
		private static final int WIDTHCONTAINER=160;
		
		public static final int TOP=10;
		public static final int LEFT=10;
		
		private PBVar var;
		private JButton buttonAdd=null;
		private JButton buttonOk=null;
		private JPanel varPanelContainer=null;
		int currentNum=0;
		
		private InputFieldInterface mainInputField=null;
		
		public FloatPanel(PBVar var){
			setLayout(null);
			this.var=var;
			this.setSize(WIDTH*2+WIDTHCONTAINER+LEFT*4, (HEIGHT+TOP)*12+TOP*3);
			varPanelContainer=new JPanel();
			varPanelContainer.setLayout(null);
			varPanelContainer.setSize(WIDTHCONTAINER, TOP*2);
			varPanelContainer.setPreferredSize(new Dimension(WIDTHCONTAINER, TOP*2));
			JScrollPane scrollPane=new JScrollPane(varPanelContainer);
			scrollPane.setBounds(10, 10,WIDTHCONTAINER+LEFT,(HEIGHT+TOP)*12+TOP);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			add(scrollPane);
			
			Container inputField=createInputField(var.getType(),var.getModel());
			inputField.setLocation(LEFT*2+WIDTHCONTAINER, TOP);
			this.add(inputField);
			mainInputField=(InputFieldInterface)inputField;
			
			buttonAdd=new JButton("add");
			buttonAdd.setBounds(LEFT*2+WIDTHCONTAINER, TOP*2+HEIGHT, WIDTH, HEIGHT);
			buttonAdd.addActionListener(this);
			add(buttonAdd);
			
			buttonOk=new JButton("ok");
			buttonOk.setBounds(LEFT*3+WIDTHCONTAINER+WIDTH, TOP*2+HEIGHT, WIDTH, HEIGHT);
			buttonOk.addActionListener(this);
			add(buttonOk);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button=(JButton)e.getSource();
			if(button.getText().equals("ok")){
				if(var.getValue()==null)
					var.setValue(new ArrayList<Object>());;
				List<Object> objects=(List<Object>)var.getValue();
				objects.clear();
				for (Component component : varPanelContainer.getComponents()) {
					if(component instanceof InputFieldInterface){
						objects.add(((InputFieldInterface)component).getValue());
					}
				}
				showRequiredFrame.dispose();
				return;
			}
			Container inputField=createInputField(var.getType(),mainInputField.getValue());
			addInputField(inputField);
		}
		private void addInputField(final Container inputField){
			inputField.setLocation(LEFT, currentNum*(TOP+inputField.getSize().height)+TOP);
			varPanelContainer.add(inputField);
			// 删除按钮
			final JButton removeButton=new JButton("x");
			removeButton.setBounds(WIDTHCONTAINER-36, currentNum*(TOP+inputField.getSize().height)+TOP+4, 18, 18);
			removeButton.setMargin(new java.awt.Insets(0,0,0,0)); 
			removeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					varPanelContainer.remove(inputField);
					varPanelContainer.remove(removeButton);
					varPanelContainer.updateUI();
				}
			});
			varPanelContainer.add(removeButton);
			currentNum++;
			varPanelContainer.setSize(WIDTHCONTAINER, currentNum*(TOP+HEIGHT)+TOP);
			varPanelContainer.setPreferredSize(new Dimension(WIDTHCONTAINER, currentNum*(TOP+HEIGHT)+TOP));
		}
		private Container createInputField(VarType type,Object value){
			if(value==null){
				switch(type){
				case Int32: return new Int32InputField();
				case Int64:return new Int64InputField();
				case String:return new StringInputField();
				case Pb:return new PBInputField();
				}
			}else{
				switch(type){
				case Int32: return new Int32InputField(new Integer((Integer)value));
				case Int64:return new Int64InputField(new Long((Long)value));
				case String:return new StringInputField(new String(value.toString()));
				case Pb:return new PBInputField("pb",((PBComponent)value).clone());// 这个地方用的是clone，所以，model没有被保存
				}
			}
			return null;
		}
		
		public void initCurrentVar(){
			// 初始化当前的变量
			if(var.getValue()!=null){
				List<Object> objects=(List<Object>)var.getValue();
				for (Object object : objects) {
					Container id=createInputField(var.getType(),object);
					addInputField(id);
				}
			}
		}

	}

}
