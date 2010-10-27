package com.osp.update.httpdownloader;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Update Site에 접속하여 Update 정보를 추출한다.
 * @author 전수완
 *
 */
public class UpdateInfo {
    private XmlParser xmlParser = null;
    private List<String> sdkIndex = null;
    private  Map<String, List<FileInformation>> fileInformations  = null;
    
    /**
     * 생성자
     */
    public UpdateInfo(URL url) {
        xmlParser = new XmlParser(url);
        sdkIndex = new LinkedList<String>();
        fileInformations  = new HashMap<String, List<FileInformation>>();
        
        //SDK가 발생, NAME발생
        //SDK와 NAME을 공유
        
        //XML에서 SDK노드가 발생했을 때, 작업을 수행한다.
        xmlParser.attachXmlListener (
            new XmlListener() {
                private void createDetailInformation(NodeList nodeList, FileInformation fileInformation) {
                    if (nodeList == null)
                        return;
                    
                    Node node = (Element)nodeList.item(0);
                    
                    if (node == null)
                        return;
                    
                    if (node.getNodeType() != Node.ELEMENT_NODE)
                        return;
                    
                    fileInformation.setProperty(
                        node.getNodeName(), 
                        (((Element)node).getChildNodes().item(0)).getNodeValue()
                    );
                }
                
                private void createInstallInformation(Node node, FileInformation fileInformation) {
                    //1 Name을 설정한다.
                    createDetailInformation(((Element)node).getElementsByTagName("Name"), fileInformation);
                    
                    //2 URL을 설정한다.
                    createDetailInformation(((Element)node).getElementsByTagName("URL"), fileInformation);
                    
                    //3 Type을 설정한다.
                    createDetailInformation(((Element)node).getElementsByTagName("Type"), fileInformation);
                    
                    //4 Size를 설정한다.
                    createDetailInformation(((Element)node).getElementsByTagName("Size"), fileInformation);
                    
                    //5 MD5를 설정한다.
                    createDetailInformation(((Element)node).getElementsByTagName("MD5"), fileInformation);
                }
                
                @Override
                public void update(Node node) throws Exception {
                    //1 SDK를 검색한다.
                    NodeList skdNodeList = ((Element)node).getElementsByTagName("SDK");
                    
                    if (skdNodeList == null)
                        return;
                    
                    for (int index = 0; index < skdNodeList.getLength(); index++) {
                        Node selectedNode = (Element)skdNodeList.item(index);
                        
                        if (selectedNode.getNodeType() != Node.ELEMENT_NODE)
                            return;

                        //1.1 Version을 등록한다.
                        String version = ((Element)selectedNode).getAttribute("version");
                        
                        //현재 버젼이 존재하는지 확인한다. 버젼에 따라 작업이 나뉘므로, 동일한 버젼은 들어올 수 없다.
                        if (fileInformations.containsKey(version) == true) {
                            //TODO Exception 처리
                            throw new Exception(version);
                        }
                        
                        sdkIndex.add(version);
                        fileInformations.put(version, new LinkedList<FileInformation>());
                         
                        
                        //2 Install을 검색한다.
                        NodeList nodeList = ((Element)selectedNode).getElementsByTagName("Install");
                        int elementIndex = 0;
                        
                        for (; elementIndex < nodeList.getLength(); elementIndex++) {
                            Node currentNode = nodeList.item(elementIndex);
                            FileInformation fileInformation = new FileInformation();
                            
                            createInstallInformation(currentNode, fileInformation);
                            
                            if (fileInformation.getSize() > 0)
                                fileInformations.get(version).add(fileInformation);
                            
                            fileInformation.setProperty("VER", version);
                        }
                        
                        //3. Patch을 검색한다.
                        nodeList = ((Element)selectedNode).getElementsByTagName("Patch");
                        
                        for (elementIndex = 0; elementIndex < nodeList.getLength(); elementIndex++) {
                            Node currentNode = nodeList.item(elementIndex);
                            
                            FileInformation fileInformation = new FileInformation();
                            createInstallInformation(currentNode, fileInformation);
                            
                            if (fileInformation.getSize() > 0)
                                fileInformations.get(version).add(fileInformation);
                            
                            fileInformation.setProperty("VER", version);
                        }
                    }
                }
            }
        );
    }
    
    /**
     * Update 사이트에 등록된 SDK의 개수를 가져온다.
     * @return SDK의 개수
     */
    public int getSdkSize() {
        return sdkIndex.size();
    }
    
    /**
     * SDK Version정보를 가져온다.
     * @param index SDK 버전
     * @return 버전정보
     */
    public String getSdkVersion(int index) {
        return sdkIndex.get(index);
    }

    /**
     * SDK Update를 가져온다.
     * @param index SDK 번호
     * @return SDK Update 정보
     */
    public List<FileInformation> getSdk(int index) {
        return fileInformations.get(sdkIndex.get(index));
    }
    
    /**
     * 지정된 xml에 대한 파싱작업을 수행한다.
     * @throws Exception
     */
    public void execute() throws Exception {
        sdkIndex.clear();
        fileInformations.values().clear();
        fileInformations.clear();
        System.gc();
        
        xmlParser.parse();
    }
}
