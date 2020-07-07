/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.operation;

import com.fabulous.FabulousManagementSystem.data.SQLHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author O-O
 */
public class Operation {

    private final List<Process> processes = new ArrayList<>();

    public synchronized List<Output> Run(HashMap<String, String> params, HashMap<String, Object> context) throws SQLException {
        try (Connection conn = SQLHelper.getConnection()) {
            List<Output> outputs = new ArrayList<>();
            conn.setAutoCommit(false);
            for (Process proc : getProcesses()) {
                Output output = proc.Run(conn, params, context);
                outputs.add(output);
            }
            conn.commit();
            SQLHelper.closeConnection(conn);
            return outputs;
        }
    }

    /**
     * @return the processes
     */
    public List<Process> getProcesses() {
        return processes;
    }
}
