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
 * 这是一个访问组件
 * 
 * **/
public class Ask {
	private static final int SENDPACKETINTERVAL=1000;
	
	private String askName;
	// 每一个ask有一个session
	private String session="";
	List<Component> comList=new ArrayList<Component>();
	long[] useTimes=null;
	// ask可以绑定玩家，即一个datapool
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
	// 获取ask的step num，根据里面的循环组件
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
					// 循环结束，跳到循环的下一个语句
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
				// 调用用户的程序对结果进行处理
				dataPool.handleRetData(result);
				break;
			}
		}
		return true;
	}
}
