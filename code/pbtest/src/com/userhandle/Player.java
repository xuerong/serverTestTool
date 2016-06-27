package com.userhandle;

import com.askcomponent.UserDataEntity;
import com.protocol.PBMessage.PBPlayerCore;
import com.protocol.PBMessage.PBPlayerInfo;

public class Player implements UserDataEntity{
	
	final int m_a = 134775813;
	final int m_c = 0x7fffffff;

    private long GUID ;
	private String m_Icon;
    private String m_Name = "NONE";
    
    private int m_b = 0;
    private int m_Seed = -1;
    public  int randomCount;
    public int sex;
    public int money;
    public int gem;
    public int magic;
    public int level;
    public int exp;
    public int logoutTime;
    public int loginTime;

    public Double sendPacketTime;
    public Double serverBackTime;
    
    public int newPlayerGuideId;

    public int packetState = 11;

    public void setMagic(int x){
        magic = magic - x;
    }
    public void setExp(int x){
        exp = x;
    }
    public void setMoney(int x){
        money = x;
    }
    public void setLevel(int x){
        level = x;
    }
    public void setGem(int x){
        gem = x;
    }
    public void setPacketSate(int state){
        packetState += state;
    }
    

    public void LoadData(PBPlayerInfo playerInfo){
        GUID = playerInfo.getGuid(); 
        m_Name = playerInfo.getName();
        sex = playerInfo.getSex();
        money = playerInfo.getMoney();
        gem = playerInfo.getGem();
        magic = playerInfo.getMagic();
        level = playerInfo.getLevel();
        exp = playerInfo.getExp();
        logoutTime = playerInfo.getLogoutTime();
        loginTime = playerInfo.getLoginTime();
        
    }

    public PBPlayerCore.Builder ConvertCoreToPB(){
        PBPlayerCore.Builder pb = PBPlayerCore.newBuilder();
        pb.setLevel(level);
        pb.setExp(exp);
        pb.setMoney(money);
        pb.setMagic(magic);
        pb.setGem(gem);
        return pb;
    }
    //设置随机种子
    public void RandomSeed(int seed, int param){
        m_Seed = seed;
        m_b = param;
        randomCount = 0;
    }
    //返回 min到 max-1的随机数 当min=max时候 返回min
    public int Range(int min, int max){
        if (max == min) return min;
        if (max < min)
        {
            int temp = min;
            min = max;
            max = temp;
        }
        _random();

        int seed = Math.abs(m_Seed);  
        return Math.abs(m_Seed) % (max - min) + min;
    }
    private void _random(){
        m_Seed = (int)(((long)m_Seed * (long)m_a + (long)m_b) % m_c);
        randomCount++;
    }
}
