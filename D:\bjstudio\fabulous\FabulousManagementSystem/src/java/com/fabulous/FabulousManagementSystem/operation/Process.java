/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.operation;

import com.fabulous.FabulousManagementSystem.data.SQLHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author O-O
 */
public class Process {

    private final List<Parameter> parameters = new ArrayList<>();
    private String content = "";
    private String type = "query";

    public synchronized Output Run(Connection conn, HashMap<String, String> params, HashMap<String, Object> context) throws SQLException {
        Output output = new Output();
        PreparedStatement stmt = PrepareStatement(conn, params, context);
        switch (type) {
            case "execute":
                int count = stmt.executeUpdate();
                output.setTotal(count);
                break;
            default:
                ResultSet rs = stmt.executeQuery();
                List<HashMap<String, Object>> result = ConvertResultSet(rs);
                output.setTotal(result.size());
                output.setRows(result);
                break;
        }
        return output;
    }

    /**
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    private List<HashMap<String, Object>> ConvertResultSet(ResultSet rs) throws SQLException {
        List<HashMap<String, Object>> list = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        while (rs.next()) {
            HashMap<String, Object> map = new HashMap<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String colName = meta.getColumnLabel(i);
                Object value = rs.getObject(i);
                if (value != null) {
                    map.put(colName, value);
                } else {
                    map.put(colName, "");
                }
            }
            list.add(map);
        }
        return list;
    }

    private PreparedStatement PrepareStatement(Connection conn, HashMap<String, String> params, HashMap<String, Object> context) throws SQLException {
        int size = parameters.size();
        Object[] objectParams = new Object[size];
        for (int i = 0; i < size; i++) {
            Parameter parameter = parameters.get(i);
            Object p;
            switch (parameter.getFrom()) {
                case "context":
                    if ("".equals(context.get(parameter.getName()))) {
                        p = null;
                    } else {
                        p = context.get(parameter.getName());
                    }
                    break;
                default:
                    if ("".equals(params.get(parameter.getName()))) {
                        p = null;
                    } else {
                        p = params.get(parameter.getName());
                    }
                    break;
            }
            if (p != null) {
                switch (parameter.getType()) {
                    case "int":
                        objectParams[i] = Integer.parseInt(p.toString());
                        break;
                    default:
                        objectParams[i] = p;
                        break;
                }
            } else {
                objectParams[i] = p;
            }
        }
        return SQLHelper.getPreparedStatement(conn, content, objectParams);
    }
}
