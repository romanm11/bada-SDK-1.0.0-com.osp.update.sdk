package com.osp.update.httpdownloader;

import org.w3c.dom.Node;

/**
 * XML 
 * @author ������
 *
 */
public interface XmlListener {
    /**
     * @param node
     */
    void update(Node node) throws Exception;
}
