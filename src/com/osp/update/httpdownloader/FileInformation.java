package com.osp.update.httpdownloader;

import java.util.HashMap;
import java.util.Map;

/**
 * FileInformation
 * Update Site���� �����ϴ� Update File�� ������ �����Ѵ�.
 * @author ������
 *
 */
public class FileInformation {
    private Map<String, String> properties = null;
    
    /**
     * ������
     */
    public FileInformation() {
        properties = new HashMap<String, String>();
    }

    /**
     * �Ӽ��� ����Ѵ�.
     * 
     * @param key �Ӽ� Ű
     * @param value �Ӽ� ��
     */
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    /**
     * �Ӽ��� ���� ���� �����ش�.
     *  
     * @param key �Ӽ� Ű
     * @return �Ӽ� ��
     */
    public String getProperty(String key) {
        return properties.get(key);
    }
    
    /**
     * �Ӽ��� ������ �����ش�.
     * @return �Ӽ��� ������ �����ش�.
     */
    public int getSize() {
        return properties.size();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FileInformation [properties=" + properties + "]";
    }
    
    
}
