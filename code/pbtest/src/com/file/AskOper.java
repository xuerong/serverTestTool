package com.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import com.askcomponent.Component;

public class AskOper {
	
	private static final String FILENAME="save.ask";
	
	private static AskOper askOper=new AskOper();
	public static AskOper getInstance(){
		return askOper;
	}
	
	private HashMap<String,List<Component>> askMap=null;
	
	private AskOper(){
		readAsk();
	}
	
	public boolean saveAsk(String name,List<Component> comList){
		File file =new File(FILENAME);
        FileOutputStream out;
        try {
        	askMap.put(name, comList);
        	//askMap=new HashMap<String, List<Component>>();
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(askMap);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
		return true;
	}
	@SuppressWarnings("unchecked")
	public HashMap<String,List<Component>> readAsk(){
		Object temp=null;
        File file =new File(FILENAME);
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(in);
            temp=objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        askMap=(HashMap<String, List<Component>>)temp;
        return askMap;
	}
}
