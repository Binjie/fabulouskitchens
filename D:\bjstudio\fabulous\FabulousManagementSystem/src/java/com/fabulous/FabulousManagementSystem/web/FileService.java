/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.web;

import com.fabulous.FabulousManagementSystem.common.StringCommon;
import com.fabulous.FabulousManagementSystem.configuration.ConfigurationManager;
import com.fabulous.FabulousManagementSystem.operation.Operation;
import com.fabulous.FabulousManagementSystem.operation.OperationManager;
import com.fabulous.FabulousManagementSystem.operation.Output;
import com.lowagie.text.DocumentException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.Compiler.command;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.codehaus.jackson.map.ObjectMapper;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 *
 * @author O-O
 */
@WebServlet(name = "FileService", urlPatterns = {"/FileService"})
public class FileService extends HttpServlet {

    public static String TemplateHtml = "";

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
        String operationname = request.getParameter("operationname");
        Object rtn = null;
        switch (operationname) {
            case "generatepdf":
                String id = request.getParameter("id");
                String pdffile;
                try {
                    Operation operation = OperationManager.GetOperation("quot.savehistory");
                    HttpSession session = request.getSession();
                    HashMap<String, Object> context = (HashMap<String, Object>) session.getAttribute("context");
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", id);
                    operation.Run(params, context);
                    pdffile = GeneratePDF(id, request);
                    rtn = pdffile;
                } catch (Exception ex) {
                    ServletException sex = new ServletException();
                    sex.setStackTrace(ex.getStackTrace());
                    throw sex;
                }
                break;
            case "getrelatedfiles":
                try {
                    String filenumber = request.getParameter("file_number");
                    String subfile = request.getParameter("sub_file");
                    //设置parent文件夹权限
                    String pfpath = GetFolderPath(filenumber, "");
                    File pf = new File(pfpath);
                    if (!pf.exists()) {
                        pf.mkdirs();
                    }
                    pf.setReadable(true, false);//设置可读权限
                    pf.setWritable(true, false);//设置可写权限
                    pf.setExecutable(true, false);
                    SetFilePermission(pfpath);
//                    response.getWriter().write(pfpath);
//                    response.getWriter().write(Boolean.toString(pf.canRead()));
//                    response.getWriter().write(Boolean.toString(pf.canWrite()));
//                    response.getWriter().write(Boolean.toString(pf.canExecute()));
                    String folderpath = GetFolderPath(filenumber, subfile);
                    File folder = new File(folderpath);
                    if (!folder.exists()) {
                        folder.mkdirs();
                        //folder.setExecutable(true,false);//设置可执行权限
                    }
                    folder.setReadable(true, false);//设置可读权限
                    folder.setWritable(true, false);//设置可写权限
                    folder.setExecutable(true, false);
                    SetFilePermission(folderpath);
                    File[] files = folder.listFiles(); // 得到f文件夹下面的所有文件。  
                    List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                    for (File file : files) {
                        String filename = file.getName();
                        file.setReadable(true, false);//设置可读权限
                        file.setWritable(true, false);//设置可写权限
                        file.setExecutable(true, false);
                        String urlpath = GetWebPath(filenumber, subfile);
                        String url = urlpath + "/" + filename;
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("filename", filename);
                        map.put("url", url);
                        list.add(map);
                    }
                    rtn = list;
                } catch (Exception ex) {
                    ServletException sex = new ServletException();
                    sex.setStackTrace(ex.getStackTrace());
                    throw sex;
                }
                break;
            default:
                break;
        }
        try (PrintWriter out = response.getWriter()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, rtn);
            out.flush();
            out.close();
        }
    }

    private void SetFilePermission(String filepath) throws IOException {
        try {
            Process exec = Runtime.getRuntime().exec(new String[]{"chmod -R 777 ", filepath});
            exec.waitFor();
        } catch (IOException e) {
            //throw e;
        } catch (InterruptedException ex) {
            Logger.getLogger(FileService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String GetFolderPath(String filenumber, String subfile) {
        String path = getServletContext().getRealPath("/");
        String folderpath = path + "/Pages/Data/" + GetClassifiedPath(filenumber) + "/" + filenumber;
        if (!"".equals(subfile)) {
            folderpath = folderpath + "/" + subfile;
        }
        return folderpath;
    }

    private String GetWebPath(String filenumber, String subfile) {
        String url = "/Pages/Data/" + GetClassifiedPath(filenumber) + "/" + filenumber;
        if (!"".equals(subfile)) {
            url = url + "/" + subfile;
        }
        return url;
    }

//    private String GetWebUrl(HttpServletRequest request) {
//        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
//    }
    private String GetClassifiedPath(String filenumber) {
        String rtn = "other";
        int idx = filenumber.indexOf("(");
        if (idx <= 0) {
            idx = filenumber.length();
        }
        int i = -1;
        String num = filenumber.substring(2, idx).trim();
        try {
            i = Integer.parseInt(num);
        } catch (Exception e) {
        }
        if (i > 0) {
            int st = i - i % 100;
            int ed = st + 99;
            rtn = filenumber.substring(0, 2) + String.valueOf(st) + "-" + String.valueOf(ed);
        }
        return rtn;
    }

    private String GeneratePDF(String id, HttpServletRequest request) throws Exception {
        Operation operation = OperationManager.GetOperation("quot.get");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        List<Output> outputs = operation.Run(params, null);
        Output output = outputs.get(0);
        String rtn = "";
        if (output.getTotal() > 0) {
            HashMap map = output.getRows().get(0);
            rtn = GenerateHtml(map, request);
        }
        return rtn;
    }

    private String GenerateHtml(HashMap<String, Object> data, HttpServletRequest request) throws FileNotFoundException, IOException, DocumentException {
        StringBuilder sbhtml = new StringBuilder();
        if (TemplateHtml.equals("")) {
            String tempFile = ConfigurationManager.getWebinfPath() + ConfigurationManager.getQuotationTemplateFile();
            BufferedReader br = new BufferedReader(new FileReader(tempFile));
            String line;
            StringBuilder sbtemp = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sbtemp.append(line);
            }
            br.close();
            TemplateHtml = sbtemp.toString();
        }

        char[] chars = TemplateHtml.toCharArray();
        int i = 0;
        boolean isV = false;
        StringBuilder sbVariable = new StringBuilder();
        while (i < chars.length) {
            if (chars[i] == '{' && chars[i + 1] == '{') {
                i = i + 2;
                isV = true;
            }
            if (chars[i] == '}' && chars[i + 1] == '}') {
                i = i + 2;
                isV = false;
                String var = sbVariable.toString();

                if (sbVariable.length() > 0 && sbVariable.charAt(0) == '=') {
                    sbhtml.append(ProcessTemplateVariables(data, request, var));
                } else {
                    sbhtml.append(GetMapValue(data, var));
                }
                sbVariable = new StringBuilder();
            }
            if (isV) {
                sbVariable.append(chars[i]);
            } else {
                sbhtml.append(chars[i]);
            }
            i++;
        }

        String filenumber = GetMapValue(data, "file_number");
        String subfile = GetMapValue(data, "sub_file");
        String folderpath = GetFolderPath(filenumber, subfile);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");//设置日期格式
        String datestr = df.format(new Date());
        String filename = datestr + "_" + filenumber + "(" + subfile + ")" + "_contract" + ".pdf";
        String wholepath = folderpath + "/" + filename;
        wholepath = wholepath.replaceAll("&amp;", "&");

        OutputStream outputStream = new FileOutputStream(wholepath);
        ITextRenderer renderer = new ITextRenderer();
        FontResolver resolver = renderer.getFontResolver();
        String fontfolder = getServletContext().getRealPath("/");

        renderer.getFontResolver()
                .addFont(fontfolder + "/Fonts/arial.ttf", true);
        renderer.getFontResolver()
                .addFont(fontfolder + "/Fonts/arialbd.ttf", true);

        renderer.setDocumentFromString(sbhtml.toString());
//        PDFEncryption pdfEncryption = new PDFEncryption(null, null, PdfWriter.ALLOW_PRINTING);
//        renderer.setPDFEncryption(pdfEncryption);
        renderer.layout();

        renderer.createPDF(outputStream);

        renderer.finishPDF();

        outputStream.flush();

        outputStream.close();

        String webpath = GetWebPath(filenumber, subfile);
        webpath = webpath.replaceAll("&amp;", "&");
        filename = filename.replaceAll("&amp;", "&");
        return webpath + "/" + filename;
    }

    private String GetMapValue(HashMap<String, Object> data, String name) {
        String rtn = "";
        if (data != null && name != null) {
            if (data.containsKey(name)) {
                Object o = data.get(name);
                if (o instanceof Date) {
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String ds = df.format(o);
                    rtn = ds.replaceAll(" 00:00:00", "");
                } else {
                    rtn = o.toString();
                }
                rtn = rtn.replaceAll("&", "&amp;");
                //rtn = rtn.replaceAll(" ", "&nbsp;");
                //rtn = rtn.replaceAll("<", "&lt;");
                //rtn = rtn.replaceAll(">", "&gt;");
                //rtn = rtn.replaceAll("\"", "&quot;");
            }
        }
        return rtn;
    }

    private String ProcessTemplateVariables(HashMap<String, Object> data, HttpServletRequest request, String variable) {
        StringBuilder sbt = new StringBuilder();
        if ("=logourl".equals(variable)) {
            //sbt.append(GetWebUrl(request)).append("img/logo_new.jpg");
            sbt.append(ConfigurationManager.getLogoUrl());
        }
        if ("=file_number".equals(variable)) {
            String fn = GetMapValue(data, "file_number");
            String sf = GetMapValue(data, "sub_file");
            sbt.append(fn);
            if (StringCommon.IsNotNullandEmpty(sf)) {
                sbt.append("(");
                sbt.append(sf);
                sbt.append(")");
            }
        }
        if ("=ic_extra_note".equals(variable)) {
            String en = GetMapValue(data, "ic_extra_note");
            if (StringCommon.IsNotNullandEmpty(en)) {
                sbt.append("<tr>\n");
                sbt.append("<td colspan='2'>Extra Note:</td>\n");
                sbt.append("<td colspan='4'>").append(en).append("</td>\n");
                sbt.append("</tr>\n");
            }
        }
        if ("=door_extra_note".equals(variable)) {
            String en = GetMapValue(data, "door_extra_note");
            if (StringCommon.IsNotNullandEmpty(en)) {
                sbt.append("<tr>\n");
                sbt.append("<td>Extra Note:</td>\n");
                sbt.append("<td colspan='3'>").append(en).append("</td>\n");
                sbt.append("</tr>\n");
            }
        }
        if ("=panel_extra_note".equals(variable)) {
            String en = GetMapValue(data, "panel_extra_note");
            if (StringCommon.IsNotNullandEmpty(en)) {
                sbt.append("<tr>\n");
                sbt.append("<td>Extra Note:</td>\n");
                sbt.append("<td colspan='3'>").append(en).append("</td>\n");
                sbt.append("</tr>\n");
            }
        }
        if ("=df_extra_note".equals(variable)) {
            String en = GetMapValue(data, "df_extra_note");
            if (StringCommon.IsNotNullandEmpty(en)) {
                sbt.append("<tr>\n");
                sbt.append("<td>Extra Note:</td>\n");
                sbt.append("<td colspan='3'>").append(en).append("</td>\n");
                sbt.append("</tr>\n");
            }
        }
        if ("=additional_notes".equals(variable)) {
            String an = GetMapValue(data, "additional_notes").replaceAll("\n", "<br/>");
            sbt.append(an);
        }
        if ("=appliances".equals(variable)) {
            for (int i = 1; i <= 20; i++) {
                String at = GetMapValue(data, "appliance_type" + String.valueOf(i));
                String am = GetMapValue(data, "appliance_model" + String.valueOf(i));
                String ad = GetMapValue(data, "appliance_dimension" + String.valueOf(i));
                if (!"".equals(at) || !"".equals(am) || !"".equals(ad)) {
                    sbt.append("<tr>\n");
                    sbt.append("<td>").append(at).append("</td>\n");
                    sbt.append("<td>").append(am).append("</td>\n");
                    sbt.append("<td>").append(ad).append("</td>\n");
                    sbt.append("</tr>\n");
                }
            }
        }
        if ("=accessories".equals(variable)) {
            for (int i = 1; i <= 30; i++) {
                String at = GetMapValue(data, "accessories_type" + String.valueOf(i));
                String am = GetMapValue(data, "accessories_model" + String.valueOf(i));
                String ao = GetMapValue(data, "accessories_options" + String.valueOf(i));
                //if (!"labor".equals(ao.toLowerCase())) {
                if (!ao.toLowerCase().contains("labor")) {
                    if (!"".equals(at) || !"".equals(am) || !"".equals(ao)) {
                        sbt.append("<tr>\n");
                        String lastat = "";
                        if (i > 1) {
                            lastat = GetMapValue(data, "accessories_type" + String.valueOf(i - 1));
                        }
                        if (lastat.equals(at)) {
                            sbt.append("<td>").append("</td>\n");
                        } else {
                            sbt.append("<td>").append(at).append("</td>\n");
                        }
                        sbt.append("<td>").append(am).append("</td>\n");
                        sbt.append("<td>");
                        if (ao.equals("")) {
                            if (at.toLowerCase().equals("handles") || at.toLowerCase().equals("runner") || at.toLowerCase().equals("cutlery tray")) {
                                sbt.append("Quantity:").append(GetMapValue(data, "accessories_qty" + String.valueOf(i)));
                            }
                        } else {
                            sbt.append(ao);
                        }
                        sbt.append("</td>\n");
                        sbt.append("</tr>\n");
                    }
                }
            }
        }
        if ("=benchtops".equals(variable)) {
            for (int i = 1; i <= 20; i++) {
                String btr = GetMapValue(data, "benchtop_range" + String.valueOf(i));
                String bc = GetMapValue(data, "benchtop_color" + String.valueOf(i));
                String btp = GetMapValue(data, "benchtop_totalprice" + String.valueOf(i));
                if (!"".equals(btr) || !"".equals(bc)) {
                    sbt.append("<tr>\n");
                    sbt.append("<td>").append(btr).append("</td>\n");
                    sbt.append("<td>").append(bc).append("</td>\n");
                    sbt.append("<td>").append(btp).append("</td>\n");
                    sbt.append("</tr>\n");
                }
            }
        }
        if ("=long_travel_fee".equals(variable)) {
            String str = GetMapValue(data, "long_travel_fee");
            if (StringCommon.IsNotNullandEmpty(str)) {
                sbt.append("Long Travel Fee: ");
                //sbt.append(str);
                sbt.append("Yes");
            }
        }
        return sbt.toString();
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

}
