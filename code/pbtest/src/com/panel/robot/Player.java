package com.panel.robot;

import java.util.HashMap;

public class Player {
	
	private HashMap<String, String> listData=new HashMap<String, String>();
	
	public Player(long accountId){
		this.accountId=accountId;
		this.Name=accountId+"robot";
		this.level=1;
		this.exp=0;
		this.gems=0;
		this.money=0;
		this.magic=0;
		this.plantCount=0;
	}
	
	private long accountId;
	private String Name;
	private int level;
	private int exp;
	private int gems;
	private int money;
	private int magic;
	private int plantCount;
	
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getGems() {
		return gems;
	}
	public void setGems(int gems) {
		this.gems = gems;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public int getMagic() {
		return magic;
	}
	public void setMagic(int magic) {
		this.magic = magic;
	}
	public int getPlantCount() {
		return plantCount;
	}
	public void setPlantCount(int plantCount) {
		this.plantCount = plantCount;
	}
}
