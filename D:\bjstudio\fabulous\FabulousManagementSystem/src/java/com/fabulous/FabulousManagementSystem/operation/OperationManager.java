/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.operation;

import com.fabulous.FabulousManagementSystem.common.StringCommon;
import com.fabulous.FabulousManagementSystem.configuration.ConfigurationManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author O-O
 */
public class OperationManager {

    public static Operation GetOperation(String name) throws Exception {

        Operation operation = Cache.get(name);
        if (operation != null) {
            return operation;
        } else {
            try {
                String rootPath = ConfigurationManager.getOperationPath();
                String filePath = name.replaceAll("\\.", "/") + ".xml";
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(rootPath + filePath);
                operation = BuildOperation(doc);
                Cache.add(name, operation);
                return operation;
            } catch (IOException | ParserConfigurationException | DOMException | SAXException | XPathExpressionException ex) {
                Logger.getLogger(OperationManager.class.getName()).log(Level.SEVERE, null, ex);
                throw new Exception("Cannot build operation:" + ex.getLocalizedMessage());
            }
        }
    }

    private static NodeList GetNodeList(Object item, String path) throws XPathExpressionException {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        XPathExpression pathExpression = xPath.compile(path);
        NodeList result = (NodeList) pathExpression.evaluate(item, XPathConstants.NODESET);
        return result;
    }

    private static Operation BuildOperation(Document doc) throws XPathExpressionException {
        Operation operation = new Operation();
        NodeList procNodes = GetNodeList(doc, "//operation/process");
        for (int i = 0; i < procNodes.getLength(); i++) {
            Node procNode = procNodes.item(i);
            Process process = BuildProcess(procNode);
            operation.getProcesses().add(process);
        }
        return operation;
    }

    private static Process BuildProcess(Node processNode) throws XPathExpressionException {
        Process process = new Process();
        Element element = (Element) processNode;
        String type = element.getAttribute("type");
        if (StringCommon.IsNotNullandEmpty(type)) {
            process.setType(type);
        }
        NodeList paramsNodes = GetNodeList(processNode, "parameters/parameter");
        for (int i = 0; i < paramsNodes.getLength(); i++) {
            Node paramNode = paramsNodes.item(i);
            Parameter param = BuildParameter(paramNode);
            process.getParameters().add(param);
        }
        NodeList sqlNodeList = GetNodeList(processNode, "content");
        if (sqlNodeList.getLength() > 0) {
            process.setContent(sqlNodeList.item(0).getTextContent());
        }
        return process;
    }

    private static Parameter BuildParameter(Node parameterNode) {
        Parameter param = new Parameter();
        Element element = (Element) parameterNode;
        param.setName(element.getAttribute("name"));
        param.setFrom(element.getAttribute("from"));
        param.setType(element.getAttribute("type"));
        return param;
    }
}
