package com.userhandle;

public class RandomManager{
    final static int m_a = 134775813;
    final static int m_c = 0x7fffffff;

    private static int m_b = 0;
    private static int m_Seed = -1;
    public static int randomCount;

    //返回 min到 max-1的随机数 当min=max时候 返回min
    static public int Range(int min, int max){
        //Game.Log.Message(LogModuleType.Random, "Before Random:------------------------\nm_Seed=" + m_Seed + "  m_b=" + m_b + "  Count=" + RandomCount + "  PlayerId=" + Game.DataPool.Player.GUID);
        //Game.Log.Assert(m_Seed != -1);
        if (max == min) return min;
        if (max < min)
        {
            int temp = min;
            min = max;
            max = temp;
        }
        _random();

        int seed = Math.abs(m_Seed);            //Game.Log.Message(LogModuleType.Random, "m_Seed=" + m_Seed + "  m_b=" + m_b + "  Count=" + RandomCount + "  PlayerId=" + Game.DataPool.Player.GUID + "\nEnd Random:------------------------");
        return Math.abs(m_Seed) % (max - min) + min;
    }

    //设置随机种子
    static public void RandomSeed(int seed, int param){
        m_Seed = seed;
        m_b = param;
        randomCount = 0;
        
    }

    static private void _random(){
        m_Seed = (int)(((long)m_Seed * (long)m_a + (long)m_b) % m_c);
        randomCount++;
    }
}