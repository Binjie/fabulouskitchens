/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.operation;

import java.util.HashMap;

/**
 *
 * @author O-O
 */
public class Cache {

    private static final HashMap<String, Operation> cacheList = new HashMap<>();

    public synchronized static void add(String key, Operation value) {
        cacheList.put(key, value);
    }

    public synchronized static Operation get(String key) {
        if (cacheList.containsKey(key)) {
            return cacheList.get(key);
        }
        return null;
    }

    public synchronized static void remove(String key) {
        if (cacheList.containsKey(key)) {
            cacheList.remove(key);
        }
    }
}
