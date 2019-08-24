package user;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class MyMap extends Observable{
    private Map<String, Boolean> myMap;

    public MyMap()
    {
        super();
        myMap = new HashMap<String, Boolean>();
    }

    public MyMap(String key, boolean value)
    {
        super();
        myMap = new HashMap<String, Boolean>();
        myMap.put(key, value);
    }

    public void put(String key, boolean value)
    {
        myMap.put(key, value);
        setChanged();
        notifyObservers();
    }

    public void replace(String key, boolean value)
    {
        myMap.remove(key);
        myMap.put(key, value);
        setChanged();
        notifyObservers();
    }

    public Map<String, Boolean> getIsOnlineMap()
    {
        return myMap;
    }

    public void setIsOnlineMap(Map<String, Boolean> isOnlineMap)
    {
        this.myMap = isOnlineMap;
    }

    public boolean getValue(String key)
    {
        return myMap.get(key);
    }

    public void remove(String key)
    {
        myMap.remove(key);
    }

    public boolean containsValue(String key)
    {
        return myMap.containsValue(key);
    }
}
