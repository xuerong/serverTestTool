package com.askcomponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.MainForm;
import com.io.IoServer;
import com.io.PBPacket;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.userhandle.DataPoolDefault;

/**
 * ����һ���������
 * 
 * **/
public class Ask {
	private static final int SENDPACKETINTERVAL=1000;
	
	private String askName;
	// ÿһ��ask��һ��session
	private String session="";
	List<Component> comList=new ArrayList<Component>();
	long[] useTimes=null;
	// ask���԰���ң���һ��datapool
	private DataPool dataPool=null;
	
	public Ask(String askName,List<Component> comList,DataPool dataPool){
		this.askName=askName;
		this.comList=comList;
		this.dataPool=dataPool;
		useTimes=new long[comList.size()];
		Arrays.fill(useTimes, -1l);
	}
	
	public String getAskName() {
		return askName;
	}
	public void setAskName(String askName) {
		this.askName = askName;
	}
	// ��ȡask��step num�����������ѭ�����
	public int getAskStepNum(){
		return comList.size();
	}

	public long[] getUseTimes() {
		return useTimes;
	}

	public List<Component> getComList() {
		return comList;
	}

	public boolean startUp(int robotIndex){
		int size=comList.size();
		for(int i=0;i<size;i++){
			Component component=comList.get(i);
			switch (component.getComponentType()) {
			case CycleBegin:
				BeginCycleComponent beginCom=(BeginCycleComponent)component;
				if(beginCom.getCurrentNum()>=beginCom.getCycleNum()){
					// ѭ������������ѭ������һ�����
					beginCom.setCurrentNum(0);
					if(beginCom.getEnd()+1>=size)
						return false;
					i=beginCom.getEnd();
				}else{
					beginCom.setCurrentNum(beginCom.getCurrentNum()+1);
				}
				break;
			case CycleEnd:
				EndCycleComponent endCom=(EndCycleComponent)component;
				i=endCom.getBegin()-1;
				break;
			case Pb:
				PBComponent pbCom=(PBComponent)component;
				long t1=System.nanoTime();
				PBPacket result=IoServer.getInstance().sendPB(pbCom,dataPool,session);
				this.session=result.getSession();
				long accountId=dataPool==null?-1:dataPool.getAccountId();
				//String info="accountId:"+accountId+",send:"+pbCom.getName()+",result:"+result.getResult()+",use time:"+(System.nanoTime()-t1)/1000000;
				//System.out.println(info);
				useTimes[i]=(System.nanoTime()-t1)/1000000;
				if(robotIndex!=-1){
					MainForm.getMainForm().getDoTestPanel().showAskResult(robotIndex,pbCom,result);
				}
				if(result.getResult()!=1){
					return false;
				}
				// �����û��ĳ���Խ�����д���
				dataPool.handleRetData(result);
				break;
			}
		}
		return true;
	}
}
