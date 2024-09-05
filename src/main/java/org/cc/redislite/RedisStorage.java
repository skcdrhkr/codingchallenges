package org.cc.redislite;

import java.util.HashMap;
import java.util.Map;

public class RedisStorage {
    private final Map<String, String> storage = new HashMap<>();

    public String getValue(String key) {
        return storage.get(key);
    }

    public void setEntry(String key, String value) {
        storage.put(key, value);
    }
}
