package com.osp.update.httpdownloader;

/**
 * Downloader
 * �ٿ�ε� �������̽�
 * @author ������
 *
 */
public interface Downloader {
    /**
     * �ٿ�ε� �۾��� �����Ѵ�.
     * @return true �ٿ�ε� �Ϸ�, false �ٿ�ε� �̿Ϸ�
     * @throws Exception �ٿ�ε带 ������ �� ���� ����
     */
    public boolean  execute() throws Exception;
}
