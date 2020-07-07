/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author O-O
 */
public class ConfigurationManager {

    private static String dbDriver = "";
    private static String dbUrl = "";
    private static String dbUser = "";
    private static String dbPassword = "";
    private static String encryptionkey = "";
    private static String webinfPath = "";
    private static String operationPath = "";
    private static String quotationTemplateFile = "";
    private static String logoUrl = "";

    static {
        FileInputStream fis = null;
        try {
            String path = ConfigurationManager.class.getResource("/").getPath();
            webinfPath = path.replace("/build/classes", "").replace("%20", " ").replace("classes/", "").replaceFirst("/", "");
            //webinfPath = "/usr/share/tomcat/tomcat8/webapps/ROOT/WEB-INF/";
            operationPath = webinfPath + "operation/";
            // 从配置文件dbinfo.properties中读取配置信息
            Properties pp = new Properties();
            //ConfigurationManager.class.getClassLoader().getResourceAsStream("/config.properties");
            String propertyPath = (getWebinfPath() + "config.properties");
            fis = new FileInputStream(propertyPath);
            pp.load(fis);

            //class.getClass().getClassLoader().getResourceAsStream("Web-INF/web.xml");
            dbDriver = pp.getProperty("dbdriver");
            dbUrl = pp.getProperty("dburl");
            dbUser = pp.getProperty("dbusername");
            dbPassword = pp.getProperty("dbpassword");
            encryptionkey = pp.getProperty("encryptionkey");
            quotationTemplateFile = pp.getProperty("quotationtemplatefile");
            logoUrl = pp.getProperty("logourl");

        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return the dbDriver
     */
    public static String getDbDriver() {
        return dbDriver;
    }

    /**
     * @return the dbUrl
     */
    public static String getDbUrl() {
        return dbUrl;
    }

    /**
     * @return the dbUser
     */
    public static String getDbUser() {
        return dbUser;
    }

    /**
     * @return the dbPassword
     */
    public static String getDbPassword() {
        return dbPassword;
    }

    /**
     * @return the encryptionkey
     */
    public static String getEncryptionkey() {
        return encryptionkey;
    }

    /**
     * @return the webinfPath
     */
    public static String getWebinfPath() {
        return webinfPath;
    }

    /**
     * @return the dboperationPath
     */
    public static String getOperationPath() {
        return operationPath;
    }

    /**
     * @return the quotationTemplateFile
     */
    public static String getQuotationTemplateFile() {
        return quotationTemplateFile;
    }

    /**
     * @param aQuotationTemplateFile the quotationTemplateFile to set
     */
    public static void setQuotationTemplateFile(String aQuotationTemplateFile) {
        quotationTemplateFile = aQuotationTemplateFile;
    }

    /**
     * @return the logoUrl
     */
    public static String getLogoUrl() {
        return logoUrl;
    }

    /**
     * @param aLogoUrl the logoUrl to set
     */
    public static void setLogoUrl(String aLogoUrl) {
        logoUrl = aLogoUrl;
    }
}
