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
 * Update Site�� �����Ͽ� Update ������ �����Ѵ�.
 * @author ������
 *
 */
public class UpdateInfo {
    private XmlParser xmlParser = null;
    private List<String> sdkIndex = null;
    private  Map<String, List<FileInformation>> fileInformations  = null;
    
    /**
     * ������
     */
    public UpdateInfo(URL url) {
        xmlParser = new XmlParser(url);
        sdkIndex = new LinkedList<String>();
        fileInformations  = new HashMap<String, List<FileInformation>>();
        
        //SDK�� �߻�, NAME�߻�
        //SDK�� NAME�� ����
        
        //XML���� SDK��尡 �߻����� ��, �۾��� �����Ѵ�.
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
                    //1 Name�� �����Ѵ�.
                    createDetailInformation(((Element)node).getElementsByTagName("Name"), fileInformation);
                    
                    //2 URL�� �����Ѵ�.
                    createDetailInformation(((Element)node).getElementsByTagName("URL"), fileInformation);
                    
                    //3 Type�� �����Ѵ�.
                    createDetailInformation(((Element)node).getElementsByTagName("Type"), fileInformation);
                    
                    //4 Size�� �����Ѵ�.
                    createDetailInformation(((Element)node).getElementsByTagName("Size"), fileInformation);
                    
                    //5 MD5�� �����Ѵ�.
                    createDetailInformation(((Element)node).getElementsByTagName("MD5"), fileInformation);
                }
                
                @Override
                public void update(Node node) throws Exception {
                    //1 SDK�� �˻��Ѵ�.
                    NodeList skdNodeList = ((Element)node).getElementsByTagName("SDK");
                    
                    if (skdNodeList == null)
                        return;
                    
                    for (int index = 0; index < skdNodeList.getLength(); index++) {
                        Node selectedNode = (Element)skdNodeList.item(index);
                        
                        if (selectedNode.getNodeType() != Node.ELEMENT_NODE)
                            return;

                        //1.1 Version�� ����Ѵ�.
                        String version = ((Element)selectedNode).getAttribute("version");
                        
                        //���� ������ �����ϴ��� Ȯ���Ѵ�. ������ ���� �۾��� �����Ƿ�, ������ ������ ���� �� ����.
                        if (fileInformations.containsKey(version) == true) {
                            //TODO Exception ó��
                            throw new Exception(version);
                        }
                        
                        sdkIndex.add(version);
                        fileInformations.put(version, new LinkedList<FileInformation>());
                         
                        
                        //2 Install�� �˻��Ѵ�.
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
                        
                        //3. Patch�� �˻��Ѵ�.
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
     * Update ����Ʈ�� ��ϵ� SDK�� ������ �����´�.
     * @return SDK�� ����
     */
    public int getSdkSize() {
        return sdkIndex.size();
    }
    
    /**
     * SDK Version������ �����´�.
     * @param index SDK ����
     * @return ��������
     */
    public String getSdkVersion(int index) {
        return sdkIndex.get(index);
    }

    /**
     * SDK Update�� �����´�.
     * @param index SDK ��ȣ
     * @return SDK Update ����
     */
    public List<FileInformation> getSdk(int index) {
        return fileInformations.get(sdkIndex.get(index));
    }
    
    /**
     * ������ xml�� ���� �Ľ��۾��� �����Ѵ�.
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
