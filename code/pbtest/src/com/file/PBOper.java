package com.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import com.Single;
import com.askcomponent.PBComponent;
import com.askcomponent.PBComponentHandler;
import com.askcomponent.PBVar;
import com.askcomponent.VarProperty;
import com.askcomponent.VarType;
import com.io.IoServer;

/**
 * PB文件操作
 * 包括：读取PB
 * **/
public class PBOper {
	private static PBOper pbOper=new PBOper();
	public static PBOper getInstance(){
		return pbOper;
	}
	private PBOper(){
		init();
		createHandlerClass();
	}
	
	
	private static final String FILENAME="PBMessage.proto";
	
	private String pbString;
	
	private List<PBComponent> messageList=new ArrayList<PBComponent>();
	private List<PBComponent> pbMessageList=new ArrayList<PBComponent>();
	private List<PBComponent> csMessageList=new ArrayList<PBComponent>();
	private List<PBComponent> scMessageList=new ArrayList<PBComponent>();
	
	public List<PBComponent> getMessageList(){
		return messageList;
	}
	public List<PBComponent> getPbMessageList(){
		return pbMessageList;
	}
	public List<PBComponent> getCsMessageList(){
		return csMessageList;
	}
	public List<PBComponent> getScMessageList(){
		return scMessageList;
	}
	private void init(){
		pbString=getPBFileStr(FILENAME);
		
		messageList=getMessageList(pbString);
		int opcode=20002;
		for (PBComponent component : messageList) {
			// 暂时先这样
			component.setOpcode(opcode++);
			
			if(component.getName().toUpperCase().startsWith("PB")){
				pbMessageList.add(component);
				continue;
			}
			if(component.getName().toUpperCase().startsWith("CS")){
				csMessageList.add(component);
				continue;
			}
			if(component.getName().toUpperCase().startsWith("SC")){
				scMessageList.add(component);
				continue;
			}
		}
		// 挂载sc到cs上
		for (PBComponent csCom : csMessageList) {
			String csName=csCom.getName().substring(2);
			for (PBComponent scCom : scMessageList) {
				String scName=scCom.getName().substring(2);
				if(scName.toLowerCase().endsWith("ret")){
					scName=scName.substring(0,scName.length()-3);
				}
				if(csName.equals(scName)){
					csCom.setBackPbComponent(scCom);
					break;
				}
			}
		}
		
		for (PBComponent component : messageList) {
			for (PBVar var : component.getVars()) {
				if(var.getType()==VarType.Pb && (var.getProperty()==VarProperty.Optional || var.getProperty()==VarProperty.Required)){
					for (PBComponent pbComponent : messageList) {
						if(pbComponent.getName().equals(var.getModel().toString())){
							var.setValue(pbComponent.clone());
							var.setModel(pbComponent.clone());
						}
					}
				}else if(var.getType()==VarType.Pb && var.getProperty()==VarProperty.Repeated){
					for (PBComponent pbComponent : messageList) {
						if(pbComponent.getName().equals(var.getModel().toString())){
							var.setValue(new ArrayList<PBComponent>());
							var.setModel(pbComponent.clone());
						}
					}
				}
			}
		}
		// 初始化handler
	}
	/**
	 * 根据pb生成由comonent向pbpacket转化的类
	 * **/
	private void createHandlerClass(){
		ClassPool pool = ClassPool.getDefault();
		pool.importPackage(PBComponent.class.getName());
		
		CtClass oldClass=null;
		try {
			oldClass = pool.get(PBComponentHandler.class.getName());
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		}
		
		CtClass ct = pool.makeClass(oldClass.getName()+"$Proxy"); //这里需要生成一个新类，并且继承自原来的类
		ct.addInterface(oldClass);
		
		//实现接口的两个方法csMessageList
		HQStringBuilder m1Str = new HQStringBuilder(1);
		m1Str.append("public com.google.protobuf.GeneratedMessage.Builder pbComToBuilder(com.askcomponent.PBComponent component) {");
		for (PBComponent component : messageList) {
			m1Str.append("if(component.getName().equals(\""+component.getName()+"\")){");
			m1Str.append("com.protocol.PBMessage."+component.getName()+".Builder  builder = com.protocol.PBMessage."+component.getName()+".newBuilder();")
			.append("java.util.List bb=component.getVars();")
			.append("for(int i=0;i<bb.size();i++){")
			.append("com.askcomponent.PBVar var=(com.askcomponent.PBVar)bb.get(i);");
			
			for (PBVar var : component.getVars()) {
				m1Str.append("if(var.getName().equals(\""+var.getName()+"\")){");
				//m1Str.append("System.out.println(\"set"+doVarName(var.getName())+"\");");
				if(var.getProperty()==VarProperty.Optional || var.getProperty()==VarProperty.Required){
					switch (var.getType()) {
					case String:
						m1Str.append("builder.set"+doVarName(var.getName())+"((String)var.getValue());");
						break;
					case Int64:
						m1Str.append("builder.set"+doVarName(var.getName())+"(((Long)var.getValue()).longValue());");
						break;
					case Int32: 
						m1Str.append("builder.set"+doVarName(var.getName())+"(((Integer)var.getValue()).intValue());");
						break;
					case Pb:
						m1Str.append("builder.set"+doVarName(var.getName())+"((com.protocol.PBMessage."+
								firstCharToUpper(((PBComponent)var.getModel()).getName())+
									".Builder)(pbComToBuilder((com.askcomponent.PBComponent)var.getValue())));");
						
						break;
					default:
						break;
					}
				}else{
					m1Str.append("java.util.List aa=(java.util.List)var.getValue();");
					m1Str.append("for(int i=0;i<aa.size();i++){")
					.append("Object ob=aa.get(i);");
					switch (var.getType()) {
					case String:
						m1Str.append("builder.add"+doVarName(var.getName())+"((String)ob);");
						break;
					case Int64:
						m1Str.append("builder.add"+doVarName(var.getName())+"(((Long)ob).longValue());");
						break;
					case Int32: 
						m1Str.append("builder.add"+doVarName(var.getName())+"(((Integer)ob).intValue());");
						break;
					case Pb:
						m1Str.append("builder.add"+doVarName(var.getName())+"((com.protocol.PBMessage."+
								firstCharToUpper(((PBComponent)var.getModel()).getName())+".Builder)(pbComToBuilder((com.askcomponent.PBComponent)ob)));");
						break;
					default:
						break;
					}
					m1Str.append("}");
				}
				m1Str.append("}");
			}
			m1Str.append("}");
			m1Str.append("return builder;");
			m1Str.append("}");
		}
		m1Str.append("return null;");
		m1Str.append("}");
		//System.out.println(m1Str);
		HQStringBuilder handleMe = new HQStringBuilder(1);
		handleMe.append("public com.io.PBPacket handle(com.askcomponent.PBComponent component,com.askcomponent.DataPool dataPool) {")
		.append("com.google.protobuf.GeneratedMessage.Builder builder=pbComToBuilder(component);")
		// 对发送的数据进行一遍用户程序处理
		.append("if(dataPool!=null){")
	    .append("dataPool.handleSendData(component.getOpcode(), builder);")
	    .append("}")
		.append("com.io.PBPacket pt = new com.io.PBPacket(component.getOpcode(), builder);")
		.append("return pt;");
		handleMe.append("}");
		
		try {
			CtMethod method = CtMethod.make(m1Str.toString(), ct);
			ct.addMethod(method);
			method = CtMethod.make(handleMe.toString(), ct);
			ct.addMethod(method);
			IoServer.getInstance().setHandler((PBComponentHandler)ct.toClass().newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}
	// 首字母大写
	private String firstCharToUpper(String str){
		StringBuilder sb = new StringBuilder(str);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
	// 处理varname，这些name在pb生成的时候做了处理，入_
	private String doVarName(String str){
		String[] nameStrs=str.split("_");
		String result="";
		for (String string : nameStrs) {
			if(string.trim().length()>0)
				result+=firstCharToUpper(string);
		}
		return result;
	}
	private String getPBFileStr(String fileName){
		StringBuilder result=new StringBuilder();
		try {
			String encoding="UTF-8";
            File file=new File(fileName);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	result.append(lineTxt+"\n");
                }
                read.close();
	    }else{
	        System.out.println("找不到指定的文件");
	    }
	    } catch (Exception e) {
	        System.out.println("读取文件内容出错");
	        e.printStackTrace();
	    }
		return result.toString();
	}
	
	private List<PBComponent> getMessageList(String pbFileStr){
		List<PBComponent> result=new ArrayList<PBComponent>();
		String[] strs=pbFileStr.split("\n");
		int num=strs.length;
		for (int i=0;i<num;i++) {
			String string=strs[i];
			if(string.startsWith("message")){
				String message=string;
				while(!strs[++i].trim().equals("{"));
				message+=("\n"+strs[i]);
				while(!strs[++i].trim().equals("}"))
					message+=("\n"+strs[i]);
				message+=("\n"+strs[i]);
				result.add(getPBComponent(message));
			}
		}
		return result;
	}
	
	private PBComponent getPBComponent(String str){
		PBComponent component=new PBComponent();
		String[] strs=str.split("\n");
		component.setName(strs[0].split(" ")[1]);
		for(int i=2;i<strs.length-1;i++){
			PBVar var=getPBVar(strs[i]);
			if(var!=null)
				component.getVars().add(var);
		}
		return component;
	}
	
	private PBVar getPBVar(String str){
		List<String> strs=splitStr(str);
		String string="";
		if(strs==null || strs.size()<3)
			return null;
		PBVar var=new PBVar();
		if(strs.get(1).equals("int32")){
			var.setType(VarType.Int32);
		}else if(strs.get(1).equals("int64")){
			var.setType(VarType.Int64);
		}else if(strs.get(1).equals("string")){
			var.setType(VarType.String);
		}else if(strs.get(1).startsWith("PB") || strs.get(1).startsWith("CS") || strs.get(1).startsWith("SC")){
			var.setType(VarType.Pb);
			var.setModel(strs.get(1));
		}else 
			return null;
		if(strs.get(0).equals("repeated")){
			var.setProperty(VarProperty.Repeated);
			if(var.getType()!=VarType.Pb){
				var.setValue(new ArrayList());
				var.setModel(VarType.getDefaultValue(var.getType()));
			}
		}else if(strs.get(0).equals("optional")){
			var.setProperty(VarProperty.Optional);
			if(var.getType()!=VarType.Pb){
				var.setValue(VarType.getDefaultValue(var.getType()));
				var.setModel(VarType.getDefaultValue(var.getType()));
			}
		}else if(strs.get(0).equals("required")){
			var.setProperty(VarProperty.Required);
			if(var.getType()!=VarType.Pb){
				var.setValue(VarType.getDefaultValue(var.getType()));
				var.setModel(VarType.getDefaultValue(var.getType()));
			}
		}else 
			return null;
		string=strs.get(2);
		if(!string.contains("=")){
			var.setName(string);
		}else{
			var.setName(string.split("=")[0]);
		}
		String idStr="";
here:	for(int i=2;i<strs.size();i++){
			string=strs.get(i);
			if(!string.contains("="))
				continue;
			int begin=string.indexOf("=");
			if(string.endsWith(";")){
				var.setId(Integer.parseInt(string.substring(begin+1, string.length()-1)));
				break here;
			}
			idStr=string.substring(begin+1, string.length());
			for(int j=i+1;j<strs.size();j++){
				string=strs.get(j);
				if(!string.contains(";")){
					idStr+=string;
					continue;
				}
				if(string.length()!=1)
					idStr+=(string.substring(0, string.indexOf(";")));
				var.setId(Integer.parseInt(idStr));
				break here;
			}
		}
		return var;
	}
	
	private List<String> splitStr(String str){
		if(str==null || str.trim().length()<=0)
			return null;
		String[] strs=str.split("\t");
		List<String> result=new ArrayList<String>();
		for (String string : strs) {
			String[] strings=string.split(" ");
			for (String string2 : strings) {
				string2=string2.trim();
				if(string2.length()>0)
					result.add(string2);
			}
		}
		return result;
	}
}
class HQStringBuilder{
	private StringBuilder sb=new StringBuilder();
	private String currentTab="";
	private final String nextLine="\r\n";
	private final int tabLength=1;
	public HQStringBuilder(int tabNum){
		for(int i=0;i<tabNum;i++){
			currentTab+="\t";
		}
	}
	public HQStringBuilder append(String str){
		if(str.startsWith("}")){
			currentTab=currentTab.substring(0, currentTab.length()-tabLength);
		}
		sb.append(currentTab+str+nextLine);
		if(str.endsWith("{"))
			currentTab+="\t";
		return this;
	}
	public HQStringBuilder nextLine(){
		sb.append(nextLine);
		return this;
	}
	public HQStringBuilder tab(){
		sb.append("\t");
		return this;
	}
	@Override
	public String toString(){
		return sb.toString();
	}
}