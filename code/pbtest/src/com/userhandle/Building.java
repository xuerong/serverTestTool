package com.userhandle;

import com.protocol.PBMessage.PBBuildingInfo;

public class Building extends EntityData{
    public enum BuildingState{
        waitBuild(1),
        Build(2),
        BuildedWaitClick(4),
        Normal(8);
        private int value = 0;
        private BuildingState(int value) {    //    必须是private的，否则编译错误
            this.value = value;
        }
        public static BuildingState valueOf(int value) {    //    手写的从int到enum的转换函数
            switch (value) {
            case 1:return waitBuild;
            case 2:return Build;
            case 4:return BuildedWaitClick;
            case 8:return Normal;
            default:
                return null;
            }
        }

        public int value() {
            return this.value;
        }
    }
    public int numberId;
    public Double stateStartTime ;
    public BuildingState state;

    public Building(long guid, int islandId, int tempId, boolean isTempGuid){
    	this.hasTempGuid = isTempGuid;
        this.TEMP_GUID = guid;
        this.GUID = guid;
        this.tempID = tempId;
        this.islandId = islandId;
        this.state = BuildingState.waitBuild;
    }
    public void changState(BuildingState _state){
    	this.state = _state;
    }
    public PBBuildingInfo.Builder ConvertToPB()
    {
        PBBuildingInfo.Builder pb = PBBuildingInfo.newBuilder();
        pb.setGUID(GUID);
        pb.setTempID(tempID);
        pb.setGridX(27);
        pb.setGridY(26);
        pb.setNumber(1);
        pb.setState(state.value);
        pb.setStateStartTime(stateStartTime.intValue());
        pb.setIslandId(0);
        return pb;
    }
}
