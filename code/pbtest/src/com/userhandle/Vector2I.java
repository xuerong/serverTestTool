package com.userhandle;

import com.askcomponent.UserDataEntity;

public class Vector2I implements UserDataEntity
{
	public int x;
    public int y;

    public Vector2I(int xi, int yi)
    {
        x = xi;
        y = yi;
    }

    public Vector2I(float xf, float yf)
    {
        x = (int)xf;
        y = (int)yf;
    }

    public String toString()
    {
        return "({"+x+"}, {"+y+"})";//String.Format("({0}, {1})", x, y);
    }

    public static Vector2I sub(Vector2I u, Vector2I v){
        return new Vector2I(u.x - v.x, u.y - v.y);
    }

    public static Vector2I add(Vector2I u, Vector2I v){
        return new Vector2I(u.x + v.x, u.y + v.y);
    }

    public static boolean isNotEqual(Vector2I u, Vector2I v){
        return (u != v);
    }

    public static boolean isEqual(Vector2I u, Vector2I v){
        return ((u.x == v.x) && (u.y == v.y));
    }

    public static Vector2I mul(Vector2I u, int a){
        return new Vector2I(a * u.x, a * u.y);
    }

    public static Vector2I div(Vector2I u, int a){
        return new Vector2I(u.x / a, u.y / a);
    }
}