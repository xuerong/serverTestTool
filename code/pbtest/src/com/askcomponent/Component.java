package com.askcomponent;

import java.io.Serializable;

public interface Component extends Serializable{
	public ComponentType getComponentType();
	public Component clone();
}
