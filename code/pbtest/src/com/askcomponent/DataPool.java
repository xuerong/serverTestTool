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
	// �����յ��İ�������datapool
	public abstract void handleRetData(PBPacket packet);
	// ����֮ǰ�Է��͵����ݽ��й��ˣ���Ҫ����ֵ��д�����������
	public abstract void handleSendData(int opcode,com.google.protobuf.GeneratedMessage.Builder<?> builder);
	// ���û������л�ȡ�����ݣ�������ʾ���ڻ��������е�ʱ���û�����ȥ��ʾ
	public abstract HashMap<String, Object> getDataPoolData();
}
