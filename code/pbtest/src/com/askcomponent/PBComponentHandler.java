package com.askcomponent;

import com.io.PBPacket;

public interface PBComponentHandler {
	public PBPacket handle(PBComponent component,DataPool dataPool);
}
