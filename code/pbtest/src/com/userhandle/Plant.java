package com.userhandle;

import com.protocol.PBMessage.PBPlantInfo;
import com.userhandle.Building.BuildingState;

enum PlantType{
    CommonPlant
}
public class Plant extends EntityData
{
    private static long m_CurrentPlantID = 0;
    public enum State{
        UnInit (0),
        Produce (1 << 3),
        Breeding(1 << 4),
        InMarket (1 << 5),
        InHotel (1 << 6),
        InStorage ( 1 << 7);
        
        private int value = 0;
        private State(int value) {    //    必须是private的，否则编译错误
            this.value = value;
        }
        public static State valueOf(int value) {    //    手写的从int到enum的转换函数
            switch (value) {
            case 1:return UnInit;
            case 8:return Produce;
            case 16:return Breeding;
            case 32:return InMarket;
            case 64:return InHotel;
            case 128:return InStorage;
            default:
                return null;
            }
        }
        public int value() {
            return this.value;
        }
    }


    public int level;
    public Double stateStartTime;
    public int feedTimes;
    public int rankLevel;
    public int preProduceNumber;
    public State plantState;

    private float m_rankGain = -1;
    private float m_rankPvPScore = -1;
    private int m_GoldPerMin = -1;

    public Plant(int isLandId, int tempId,boolean isTempGuid)
    {
    	this.GUID = m_CurrentPlantID;
        this.tempID = tempId;
        this.isFlip = false;
        this.dirty = false;
        this.feedTimes = 0;
        this.preProduceNumber = 0;
        this.plantState = State.UnInit;
        this.islandId = isLandId;
        this.hasTempGuid = isTempGuid;
        this.TEMP_GUID = m_CurrentPlantID;

        m_CurrentPlantID++;
    }
    public void SetDirty(){
        super.dirty = true;
    }

    public void ClearDirty(){
    	super.dirty = false;
    }

    public void LoadFromPB(PBPlantInfo plantInfo)
    {
    	this.GUID=plantInfo.getGUID();
    	this.tempID=plantInfo.getTempID();
        this.rankLevel = plantInfo.getRankLevel();
        this.feedTimes = plantInfo.getFeedTimes();
        this.level = plantInfo.getLevel();
        this.gridPos = new Vector2I(plantInfo.getPosX(), plantInfo.getPosY());
        this.stateStartTime = (double)plantInfo.getStateStartTime();
        this.isFlip = plantInfo.getIsFlip()>0;
        this.plantState = Plant.State.valueOf(plantInfo.getPlantState());
        this.preProduceNumber = plantInfo.getPreProduceNumber();
    }

    public PBPlantInfo.Builder ConvertToPB()
    {
        PBPlantInfo.Builder pb = PBPlantInfo.newBuilder();
        pb.setGUID(GUID);
        pb.setTempID(tempID);
        pb.setLevel(level);
        pb.setPosX(52);
        pb.setPosY(63);
        pb.setIsFlip(isFlip?1:0);
        pb.setPlantState(8);
        pb.setStateStartTime(stateStartTime.intValue());
        pb.setPreProduceNumber(preProduceNumber);
        pb.setFeedTimes(feedTimes);
        pb.setRankLevel(rankLevel);
        pb.setIslandId(0);
        return pb;
    }
    public void RandomBreedRankLevel(int randomIndex)
    {
        //int randomIndex = Framework.Random.RandomManager.Range(1, 10001);
        float randomPercent = randomIndex * 1.0f / 10000;
        double rank1 = 0.6;
        double rank2 = 0.25;
        if (randomPercent <= rank1)
            rankLevel = 1;
        else if (randomPercent <= rank1 + rank2)
            rankLevel = 2;
        else
            rankLevel = 3;
    }
}