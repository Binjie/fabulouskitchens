/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.web;

import com.fabulous.FabulousManagementSystem.operation.Operation;
import com.fabulous.FabulousManagementSystem.operation.OperationManager;
import com.fabulous.FabulousManagementSystem.operation.Output;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author O-O
 */
public class OperationService extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (request.getSession().getAttribute("context") == null) {
            throw new ServletException("Please Login First.");
        }
        try (PrintWriter out = response.getWriter()) {
            ObjectMapper mapper = new ObjectMapper();
            Object obj = null;
            try {
                obj = processData(request, response);
            } catch (Exception ex) {
                Logger.getLogger(OperationService.class.getName()).log(Level.SEVERE, null, ex);
                throw new ServletException("Operation Exception:" + ex.getLocalizedMessage());
            }
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.getSerializationConfig().setDateFormat(format);
            mapper.writeValue(out, obj);
            out.flush();
            out.close();
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private List<Output> processData(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {
        String name = request.getParameter("operationname");
        Operation operation = OperationManager.GetOperation(name);
        HttpSession session = request.getSession();
        HashMap<String, Object> context = (HashMap<String, Object>) session.getAttribute("context");
        HashMap<String, String> params = processParams(request, response);
        return operation.Run(params, context);
    }

    private HashMap<String, String> processParams(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        HashMap<String, String> params = new HashMap<>();
        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = enu.nextElement();
            params.put(paraName, request.getParameter(paraName).trim());
        }
        return params;
    }
}
