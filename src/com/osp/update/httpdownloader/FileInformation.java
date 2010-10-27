package com.osp.update.httpdownloader;

import java.util.HashMap;
import java.util.Map;

/**
 * FileInformation
 * Update Site에서 관리하는 Update File의 정보를 관리한다.
 * @author 전수완
 *
 */
public class FileInformation {
    private Map<String, String> properties = null;
    
    /**
     * 생성자
     */
    public FileInformation() {
        properties = new HashMap<String, String>();
    }

    /**
     * 속성을 등록한다.
     * 
     * @param key 속성 키
     * @param value 속성 값
     */
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    /**
     * 속성이 가진 값을 돌려준다.
     *  
     * @param key 속성 키
     * @return 속성 값
     */
    public String getProperty(String key) {
        return properties.get(key);
    }
    
    /**
     * 속성의 개수를 돌려준다.
     * @return 속성의 개수를 돌려준다.
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
