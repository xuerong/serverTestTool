package com.userhandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.askcomponent.DataPool;
import com.io.PBPacket;
import com.protocol.HOpCodeEx;
import com.protocol.PBMessage.CSAskBreedSeed;
import com.protocol.PBMessage.CSAskCompleteNewPlayerGuide;
import com.protocol.PBMessage.CSBuyBuilding;
import com.protocol.PBMessage.CSCollectionBuilding;
import com.protocol.PBMessage.CSFeedMagicForPlant;
import com.protocol.PBMessage.CSLogin;
import com.protocol.PBMessage.CSLogout;
import com.protocol.PBMessage.CSPlantBreedSeed;
import com.protocol.PBMessage.CSShopBuyPlant;
import com.protocol.PBMessage.CSSpeedUpBreed;
import com.protocol.PBMessage.CSTuiTuSettlePlant;
import com.protocol.PBMessage.PBBreedRoomInfo;
import com.protocol.PBMessage.PBBuildingInfo;
import com.protocol.PBMessage.PBPlantInfo;
import com.protocol.PBMessage.PBPlayerInfo;
import com.protocol.PBMessage.SCAskUserData;
import com.protocol.PBMessage.SCBuyBuilding;
import com.protocol.PBMessage.SCLoginRet;
import com.protocol.PBMessage.SCPlantBreedSeedRet;
import com.protocol.PBMessage.SCShopBuyPlantRet;
import com.userhandle.Building.BuildingState;
import com.google.protobuf.GeneratedMessage.Builder;
import com.google.protobuf.InvalidProtocolBufferException;

public class DataPoolDefault extends DataPool{
	
	//propertity;
    public Player player ; //玩家;
    public HashMap<Long, Plant> plantList ;
    public int IslandId=0;
    // 临时plant
    public Plant plant=null;
    public Building building=null;
    PBPlantInfo.Builder plantInfoBuilder=null;
    
    Plant motherPlant=null;
    Plant fatherPlant=null;
    Plant childPlant=null;
    
    int settlePlantIndex=1;
	
	public DataPoolDefault(long account){
		super(account);
        player = new Player();
        plantList = new HashMap<Long,Plant>();
    }
    public void Destroy(){
    	player = new Player();
    	plantList.clear();
    	IslandId = 0;
    }
    

    public List<Plant> Plant_List(int state){
        List<Plant> tList = new ArrayList<Plant>();
        for (Plant plant : plantList.values()){
            if ((plant.plantState.value() & state) != 0){
                tList.add(plant);
            }
        }
        return tList;
    }
    public EntityData FindEntityByGuid(long guid){
        if (plantList.containsKey(guid))
            return plantList.get(guid);
        if (guid < 0){
            for (Plant plant :plantList.values()){
                if (plant.hasTempGuid && plant.TEMP_GUID == guid)
                    return plant;
            }
        }
        return null;
    }
    public boolean ReplaceEntityGuid(long guid, long tempGuid){
        EntityData data = FindEntityByGuid(tempGuid);
        if(null == data) {
            return false;
        }
        if (data.replaceGuid(guid, tempGuid)){
            if (plantList.containsKey(tempGuid)){
                plantList.remove(tempGuid);
                plantList.put(guid, (Plant)data);
            }
        }
        return true;
    }
    public int GetDownPlantNumber(int islandId){
        if (islandId >= 0)
        {
            int num = 0;
            for (Plant plant : Plant_List(Plant.State.Produce.value() | Plant.State.Breeding.value() | Plant.State.InHotel.value())){
                if (plant.islandId == islandId){
                    num++;
                }
            }
            return num;
        }
        else{
            return Plant_List(Plant.State.Produce.value() | Plant.State.Breeding.value() | Plant.State.InHotel.value()).size();
        }
    }


    protected void LoadPlayer(PBPlayerInfo playerInfo){
        player.LoadData(playerInfo);
    }
    public void LoadPlant(List<PBPlantInfo> plantInfoList){
        for (PBPlantInfo plantInfo : plantInfoList){
            Plant plant = new Plant(plantInfo.getIslandId(), plantInfo.getTempID(),false);
            plant.LoadFromPB(plantInfo);
            plantList.put(plant.GUID, plant);
        }
    }
	/**
	 * 在这里对发送的数据进行过滤
	 * 1 对数据进行校验
	 * 2 由data pool的值进行赋值
	 * **/
	@Override
	public void handleSendData(int opcode,Builder<?> builder){
		switch (opcode) {
		case HOpCodeEx.CSLogin:
			CSLogin.Builder loginBuilder=(CSLogin.Builder)builder;
			loginBuilder.setAccountId(getAccountId());
			loginBuilder.setMid(getAccountId()+"");
			break;
		case HOpCodeEx.CSShopBuyPlant:
			CSShopBuyPlant.Builder shoBuilder=(CSShopBuyPlant.Builder)builder;
			PBPlantInfo.Builder pBuilder=shoBuilder.getPlantInfo().toBuilder();
			pBuilder.setStateStartTime((int)System.currentTimeMillis());
			shoBuilder.setPlantInfo(pBuilder);
			plant=new Plant(0, 10000001, true);
			plant.LoadFromPB(shoBuilder.getPlantInfo());
			break;
		case HOpCodeEx.CSFeedMagicForPlant:
			CSFeedMagicForPlant.Builder feedBuilder=(CSFeedMagicForPlant.Builder)builder;
			plant.level++;
			feedBuilder.setPlantInfo(plant.ConvertToPB());
			break;
		case HOpCodeEx.CSBuyBuilding:
			CSBuyBuilding.Builder buildBuilder=(CSBuyBuilding.Builder)builder;
			PBBuildingInfo.Builder bBuilder=buildBuilder.getBuildingInfo().toBuilder();
			bBuilder.setStateStartTime((int)System.currentTimeMillis());
			buildBuilder.setBuildingInfo(bBuilder);
			building=new Building(0, 0,40000005, true);
			building.numberId=buildBuilder.getBuildingInfo().getNumber();
			building.stateStartTime=(double)buildBuilder.getBuildingInfo().getStateStartTime();
			building.state=BuildingState.valueOf(buildBuilder.getBuildingInfo().getState()) ;
			break;
		case HOpCodeEx.CSCollectionBuilding:
			CSCollectionBuilding.Builder collectionBuilder=(CSCollectionBuilding.Builder)builder;
			PBBuildingInfo.Builder builder2= collectionBuilder.getBuildingInfo().toBuilder();
			builder2.setGUID(building.GUID);
			collectionBuilder.setBuildingInfo(builder2);
			break;
		case HOpCodeEx.CSAskBreedSeed:
			CSAskBreedSeed.Builder askBreedBuilder=(CSAskBreedSeed.Builder)builder;
			PBPlantInfo.Builder builder3= askBreedBuilder.getMotherPlant().toBuilder();
			for (Long key : plantList.keySet()) {
				if(key!=plant.GUID){
					motherPlant=plantList.get(key);
				}
			}
			builder3.setGUID(motherPlant.GUID);
			builder3.setStateStartTime((int)System.currentTimeMillis());
			askBreedBuilder.setMotherPlant(builder3);
			
			PBPlantInfo.Builder builder4= askBreedBuilder.getFatherPlant().toBuilder();
			builder4.setGUID(plant.GUID);
			builder4.setStateStartTime((int)System.currentTimeMillis());
			fatherPlant=plant;
			askBreedBuilder.setFatherPlant(builder4);
			break;
		case HOpCodeEx.CSSpeedUpBreed:
			CSSpeedUpBreed.Builder speedUpBuilder=(CSSpeedUpBreed.Builder)builder;
			PBBreedRoomInfo.Builder builder5= speedUpBuilder.getBreedRoomInfo().toBuilder();
			builder5.setMaPlant(motherPlant.GUID);
			builder5.setFaPlant(fatherPlant.GUID);
			speedUpBuilder.setBreedRoomInfo(builder5);
			break;
		case HOpCodeEx.CSPlantBreedSeed:
			CSPlantBreedSeed.Builder plantBreedBuilder=(CSPlantBreedSeed.Builder)builder;
			PBBreedRoomInfo.Builder builder6= plantBreedBuilder.getBreedRoomInfo().toBuilder();
			builder6.setMaPlant(motherPlant.GUID);
			builder6.setFaPlant(fatherPlant.GUID);
			plantBreedBuilder.setBreedRoomInfo(builder6);
			plantBreedBuilder.setMotherPlant(motherPlant.ConvertToPB());
			plantBreedBuilder.setFatherPlant(fatherPlant.ConvertToPB());
			plant=new Plant(0, -174, true);
			plant.LoadFromPB(plantBreedBuilder.getChildPlant());
			for(int i=0;i<5;i++){
				plant.RandomBreedRankLevel(player.Range(1, 10001));
			}
			plantBreedBuilder.setChildPlant(plant.ConvertToPB());
			childPlant=plant;
			break;
		case HOpCodeEx.CSTuiTuSettlePlant:
			CSTuiTuSettlePlant.Builder tuiTuSettleBuilder=(CSTuiTuSettlePlant.Builder)builder;
			if(settlePlantIndex==1){
				tuiTuSettleBuilder.setPlantGUID(fatherPlant.GUID);
				settlePlantIndex++;
			}else {
				tuiTuSettleBuilder.setPlantGUID(motherPlant.GUID);
				settlePlantIndex--;
			}
			tuiTuSettleBuilder.setCellID(settlePlantIndex);
			tuiTuSettleBuilder.setGasStationTabID(1);
			break;
		default:
			break;
		}
	}
	// 处理收到的包，存入datapool
	@Override
	public void handleRetData(PBPacket packet) {
		try {
			switch (packet.getOpcode()) {
			case HOpCodeEx.SCLoginRet:
				SCLoginRet loginRet=SCLoginRet.parseFrom(packet.getData());
				break;
			case HOpCodeEx.SCAskUserData:
				SCAskUserData askUserDataRet=SCAskUserData.parseFrom(packet.getData());
				player.LoadData(askUserDataRet.getPlayerInfo());
				player.RandomSeed(askUserDataRet.getMSeed(), askUserDataRet.getMB());
				LoadPlant(askUserDataRet.getPlantInfoList());
				break;
			case HOpCodeEx.SCShopBuyPlantRet:
				SCShopBuyPlantRet shopBuyPlantRet=SCShopBuyPlantRet.parseFrom(packet.getData());
				plant.changGuid(shopBuyPlantRet.getPlantGuid());
				plantList.put(plant.GUID, plant);
				break;
			case HOpCodeEx.SCBuyBuilding:
				SCBuyBuilding buyBuildingRet=SCBuyBuilding.parseFrom(packet.getData());
				building.changGuid(buyBuildingRet.getGUID());
				break;
			case HOpCodeEx.SCPlantBreedSeedRet:
				SCPlantBreedSeedRet plantBreedSeedRet=SCPlantBreedSeedRet.parseFrom(packet.getData());
				childPlant.GUID=plantBreedSeedRet.getChildGuid();
				plantList.put(childPlant.GUID, childPlant);
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	@Override
	public HashMap<String, Object> getDataPoolData() {
		HashMap<String, Object> dataPoolData=new HashMap<String, Object>();
		dataPoolData.put("accountId", getAccountId()+"");
		dataPoolData.put("accountId", getAccountId()+"");
		return dataPoolData;
	}
}
