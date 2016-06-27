package com.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import com.askcomponent.DataPool;
import com.askcomponent.PBComponent;
import com.askcomponent.PBComponentHandler;
import com.askcomponent.PBVar;
import com.askcomponent.VarProperty;
import com.askcomponent.VarType;

public class IoServer {
	
	private static IoServer ioServer=new IoServer();
	public static IoServer getInstance(){
		return ioServer;
	}
	// 一个访问认为的超时次数
	private static final int OVERTIME=50000;
	// 一个连接认为的超时时间，如果连接超时，尝试重新连接，一直到连接总时间大于OVERTIME
	private static final int TIMEOUT=4000;
	// 尝试连接的次数
	private static final int TRYCONNECTIONTIMES=10;
	
	private IoServer(){
	}
	private String urlStr=null;
	private IoSession session=null;
	private PBComponentHandler handler=null;
	private static final String KEY_GAME_SESSION="HE-VALUEID";
	/* 
	 * 返回一个PBPacket值,对应result值
	 * 如果成功，返回1
	 * 异常返回0,包括连接超时
	 * 服务器超时，返回-1
	 * 错误，返回-2
	 * 返回opcode
	*/
	public PBPacket sendPB(final PBComponent pbComponent,DataPool dataPool,final String session){
		
		//showPb(pbComponent);
		final PBPacket packet = handler.handle(pbComponent,dataPool);
		packet.setSession(session);
		final PBPacket rePacket = new PBPacket(pbComponent.getBackPbComponent().getOpcode(), new byte[0]);
		rePacket.setResult(-1);
		final Thread thisThread=Thread.currentThread();
		Thread thread=new Thread(){
			HttpURLConnection connection=null;
			int tryConnectTimes=0;
			@Override
			public void run(){
				while(true){
					try{
						URL url=new URL(urlStr);
						
						connection=(HttpURLConnection)url.openConnection();
						connection.setConnectTimeout(TIMEOUT);
						connection.setDoOutput(true);
						
						connection .setRequestProperty("Accept-Encoding", "identity");
						connection.setRequestProperty("opcode", ""+packet.getOpcode());
						connection.setRequestProperty(KEY_GAME_SESSION, session);
						connection.setRequestMethod("POST");
						BufferedOutputStream out=new BufferedOutputStream(connection.getOutputStream());
						
						byte[] data=packet.getData();
						out.write(data,0,data.length);
						out.flush();
						out.close();
						
						InputStream is = connection.getInputStream();
						
						int bufSize=connection.getContentLength();
						
						if(bufSize < 0){
							System.out.println("server return bufsize:"+bufSize);
							rePacket.setResult(-2);
							synchronized(thisThread){
								thisThread.notify();
							}
							return ;
						}
						byte[] buffer = new byte[bufSize];
						int size = is.read(buffer);
						int readedSize = size;
						if (size != bufSize) {
							while (size > -1) {
								size = is.read(buffer, readedSize, bufSize - readedSize);
								readedSize += size;
							}
						}
						String opcodeStr = connection.getHeaderField("opcode");
						String sessionStr= connection.getHeaderField(KEY_GAME_SESSION);
						if (opcodeStr == null || sessionStr==null) {
							rePacket.setResult(-2);
							synchronized(thisThread){
								thisThread.notify();
							}
							return ;
						}
						int opcode = Integer.parseInt(opcodeStr);
						//System.out.println(pbComponent.getOpcode()+"--"+pbComponent.getBackPbComponent().getOpcode()+"--"+opcode);
						if(pbComponent.getBackPbComponent().getOpcode()==opcode){
							rePacket.setResult(1);
						}else{
							rePacket.setResult(opcode);
						}
						rePacket.setSession(sessionStr);
						rePacket.setOpcode(opcode);
						rePacket.setData(buffer);
						synchronized(thisThread){
							thisThread.notify();
						}
						return;
					}catch(IOException e){
						if(e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof SocketException){
							if(tryConnectTimes<TRYCONNECTIONTIMES){
								tryConnectTimes++;
								System.out.println("CONNECTnum:"+tryConnectTimes+"----------------------------------------------------------");
								continue;
							}
						}
						e.printStackTrace();
						rePacket.setResult(0);
						synchronized(thisThread){
							thisThread.notify();
						}
						return;
					}finally{
						connection.disconnect();
					}
				}
			}
		};
		thread.start();
		try {
			synchronized(thisThread){
				thisThread.wait(OVERTIME);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return rePacket;
	}
	private void showPb(PBComponent pbComponent){
		List<PBVar> vars= pbComponent.getVars();
		for (PBVar pbVar : vars) {
			System.out.print("	"+pbVar.getName());
			VarType type=pbVar.getType();
			VarProperty property=pbVar.getProperty();
			if(property==VarProperty.Optional || property==VarProperty.Required){
				switch (type) {
				case Int32:
					System.out.println("	"+pbVar.getValue());
					break;
				case Int64:
					System.out.println("	"+pbVar.getValue());
					break;
				case String:
					System.out.println("	"+pbVar.getValue());
					break;
				case Pb:
					showPb((PBComponent)pbVar.getValue());
					break;
				default:
					break;
				}
			}else{
				List pbList=(List)pbVar.getValue();
				for (Object object : pbList) {
					if(object instanceof Integer){
						System.out.println("	"+object);
					}else if(object instanceof Long){
						System.out.println("	"+object);
					}else if(object instanceof String){
						System.out.println("	"+object);
					}else if(object instanceof PBComponent){
						showPb((PBComponent)object);
					}
					
				}
			}
		}
	}
	
	
	public void setHandler(PBComponentHandler handler) {
		this.handler = handler;
	}
	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}
	
}
