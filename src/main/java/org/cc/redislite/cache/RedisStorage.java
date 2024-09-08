package org.cc.redislite.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedisStorage {
    private final Map<String, RedisField> storage = new HashMap<>();

    public Object getValue(String key) {
        long curTime = System.currentTimeMillis();
        if (!storage.containsKey(key)) return null;
        long expiryTime = storage.get(key).expiryTime();
        if (curTime >= expiryTime) {
            storage.remove(key);
            return null;
        }
        return storage.get(key).value();
    }

    public long getSetTime(String key) {
        return storage.get(key).setTime();
    }

    public long getExpiryTime(String key) {
        return storage.get(key).expiryTime();
    }

    public void setEntry(String key, Object value) {
        setEntry(key, value, System.currentTimeMillis(), Long.MAX_VALUE);
    }

    public void setEntry(String key, Object value, long curTime, long expiryTime) {
        storage.put(key, new RedisField(value, curTime, expiryTime));
    }

    public Set<String> getKeySet() {
        return storage.keySet();
    }

    public void removeCacheKey(String key) {
        storage.remove(key);
    }
}
