package com.osp.update.httpdownloader;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlParser {
    private URL url = null;
    private Map<XmlListener, XmlListener> xmlListeners = null;
    
    /**
     * ������
     */
    public XmlParser(URL url) {
        xmlListeners = new HashMap<XmlListener, XmlListener>();
        this.url = url;
    }
    
    /**
     * XML Listener�� ��ġ�Ѵ�.
     * @param xmlListener XML Listener
     */
    public void attachXmlListener(XmlListener xmlListener) {
        if (xmlListeners.containsKey(xmlListener) == true)
            return;
        
        xmlListeners.put(xmlListener, xmlListener);
    }
    
    /**
     * ��ġ�� XML Listener�� �����Ѵ�.
     * @param xmlListener XML Listener
     */
    public void deatchXmlListener(XmlListener xmlListener) {
        if (xmlListeners.containsKey(xmlListener) == false)
            return;
        
        xmlListeners.remove(xmlListener);
    }
    
    /**
     * XML ������ �Ľ��Ѵ�. 
     * @throws Exception
     */
    public void parse() throws Exception {
        //URL�� �����Ͽ� Document ������ �����Ѵ�.
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(url.openStream());
        document.getDocumentElement().normalize();
        
        NodeList nodeList = document.getChildNodes();
        
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
        
            //��ϵ� XmlListener�� ���� ��带 �����Ѵ�. 
            Iterator<XmlListener> iteratorXmlListener = xmlListeners.values().iterator();
            
            while (iteratorXmlListener.hasNext() == true) {
                XmlListener xmlListener = iteratorXmlListener.next();
                xmlListener.update(node);
            }
        }
    }
}
