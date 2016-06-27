package com.userhandle;

import com.askcomponent.UserDataEntity;

public class EntityData implements UserDataEntity
{
    public long GUID ;
    public int tempID ;
    public Vector2I gridPos;
    public boolean isFlip;
    public int islandId;
    public boolean dirty;

    public long TEMP_GUID ;
    public boolean hasTempGuid=false;
    boolean m_hasReplecedGuid = false;
    public boolean replaceGuid(long guid, long tempGuid){
        if (TEMP_GUID != tempGuid || (m_hasReplecedGuid && guid != GUID)){
            return false;
        }
        GUID = guid;
        m_hasReplecedGuid = true;
        return true;
    }
    public void changGuid(long guid){
        GUID = guid;
    }
    public void Update() {

    }
}
