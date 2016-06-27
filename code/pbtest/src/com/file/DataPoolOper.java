package com.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.askcomponent.Component;
import com.askcomponent.DataPool;

public class DataPoolOper {

	private static final String FILENAME="save.datapool";
	
	private static DataPoolOper dataPoolOper=new DataPoolOper();
	public static DataPoolOper getInstance(){
		return dataPoolOper;
	}
	
	private HashMap<Long,DataPool> dataPoolMap=null;
	
	private DataPoolOper(){
		readDataPool();
	}
	public boolean saveDataPools(List<DataPool> dataPools){
		File file =new File(FILENAME);
        FileOutputStream out;
        try {
        	for (DataPool dataPool : dataPools) {
        		dataPoolMap.put(dataPool.getAccountId(), dataPool);
			}
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(dataPoolMap);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
		return true;
	}
	public boolean saveDataPool(Long account,DataPool dataPool){
		File file =new File(FILENAME);
        FileOutputStream out;
        try {
        	dataPoolMap.put(account, dataPool);
        	//dataPoolMap=new HashMap<Long, DataPool>();
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(dataPoolMap);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
		return true;
	}
	public HashMap<Long,DataPool> getDataPools(){
		return dataPoolMap;
	}
	@SuppressWarnings("unchecked")
	private void readDataPool(){
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
            temp=new HashMap<Long, DataPool>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            temp=new HashMap<Long, DataPool>();
        }
        dataPoolMap=(HashMap<Long, DataPool>)temp;
	}
}
