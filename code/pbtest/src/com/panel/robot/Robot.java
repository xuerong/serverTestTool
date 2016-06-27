package com.panel.robot;

import com.askcomponent.Ask;
import com.askcomponent.DataPool;
import com.askcomponent.PBComponent;

public class Robot {
	private int index;
	//private long accountId;
	private Ask ask;
	private Status status;
	private DataPool dataPool;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Status getStatus() {
		return status;
	}

	public DataPool getDataPool() {
		return dataPool;
	}

	public void setDataPool(DataPool dataPool) {
		this.dataPool = dataPool;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	public Ask getAsk() {
		return ask;
	}

	public void setAsk(Ask ask) {
		this.ask = ask;
	}

	public static final class Status{
		private int allStep;// 算上循环的step
		private int currentStep;// 算上循环的step
		private PBComponent currentComponent;
		private boolean isOver; // 没有走完也可能over了，报错了
		
		public int getAllStep() {
			return allStep;
		}
		public void setAllStep(int allStep) {
			this.allStep = allStep;
		}
		public int getCurrentStep() {
			return currentStep;
		}
		public void setCurrentStep(int currentStep) {
			this.currentStep = currentStep;
		}
		public PBComponent getCurrentComponent() {
			return currentComponent;
		}
		public void setCurrentComponent(PBComponent currentComponent) {
			this.currentComponent = currentComponent;
		}
		public String toString(){
			return currentComponent.getName()+"("+currentStep+"/"+allStep+")";
		}
		public boolean isOver() {
			return isOver;
		}
		public void setOver(boolean isOver) {
			this.isOver = isOver;
		}
	}
}
