/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.operation;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author O-O
 */
public class Output {

    private int total;

    private List<HashMap<String, Object>> rows;

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return the rows
     */
    public List<HashMap<String, Object>> getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(List<HashMap<String, Object>> rows) {
        this.rows = rows;
    }

}
