package com.osp.update.httpdownloader;

/**
 * HttpDownloader�� ������¸� �˷��ش�
 * @author ������
 *
 */
public interface DownloaderListener {
    /**
     * �ٿ�ε� �۾��� �����Ѵ�.
     * 
     * @param targetFilename ������ �����̸�
     * @param targetFilesize ������ ���ϻ�����
     * @param destinationFilename ������ �����̸�
     * @param destinationFilesize ������ ���ϻ�����
     */
    void begin(String targetFilename, long targetFilesize, String destinationFilename, long destinationFilesize);
    
    /**
     * �ٿ�ε� �۾��� �����Ѵ�.
     * 
     * @param targetFilename ������ �����̸�
     * @param targetFilesize ������ ���ϻ�����
     * @param destinationFilename ������ �����̸�
     * @param destinationFilesize ������ ���ϻ�����
     * @return true�� ��� �۾��� ��� �����ϰ�, false�� ��� �۾��� �����Ѵ�.
     */
     boolean progress(String targetFilename, long targetFilesize, String destinationFilename, long destinationFilesize);
    
    /**
     * �ٿ�ε� �۾��� �Ϸ��Ѵ�.
     * 
     * @param targetFilename ������ �����̸�
     * @param targetFilesize ������ ���ϻ�����
     * @param destinationFilename ������ �����̸�
     * @param destinationFilesize ������ ���ϻ�����
     */
    void end(String targetFilename, long targetFilesize, String destinationFilename, long destinationFilesize);

    /**
     * �ٿ�ε� �۾��� ����Ѵ�.
     * 
     * @param targetFilename ������ �����̸�
     * @param targetFilesize ������ ���ϻ�����
     * @param destinationFilename ������ �����̸�
     * @param destinationFilesize ������ ���ϻ�����
     */
    void cancel(String remoteFilename, long remoteFileSize, String localFilename, long localFileSize);
}
