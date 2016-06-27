package com.askcomponent;

import java.io.Serializable;
import java.util.HashMap;

import com.io.PBPacket;

public abstract class DataPool implements Serializable{
	protected long accountId;
	public DataPool(long accountId){
		this.accountId=accountId;
	}
	
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	// 处理收到的包，存入datapool
	public abstract void handleRetData(PBPacket packet);
	// 发送之前对发送的数据进行过滤，需要程序赋值的写在这个函数中
	public abstract void handleSendData(int opcode,com.google.protobuf.GeneratedMessage.Builder<?> builder);
	// 从用户数据中获取该数据，用于显示，在机器人运行的时候用户数据去显示
	public abstract HashMap<String, Object> getDataPoolData();
}
