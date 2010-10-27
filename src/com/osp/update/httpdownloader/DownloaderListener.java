package com.osp.update.httpdownloader;

/**
 * HttpDownloader의 진행상태를 알려준다
 * @author 전수완
 *
 */
public interface DownloaderListener {
    /**
     * 다운로드 작업을 시작한다.
     * 
     * @param targetFilename 원격지 파일이름
     * @param targetFilesize 원격지 파일사이즈
     * @param destinationFilename 저장할 파일이름
     * @param destinationFilesize 저정할 파일사이즈
     */
    void begin(String targetFilename, long targetFilesize, String destinationFilename, long destinationFilesize);
    
    /**
     * 다운로드 작업을 진행한다.
     * 
     * @param targetFilename 원격지 파일이름
     * @param targetFilesize 원격지 파일사이즈
     * @param destinationFilename 저장할 파일이름
     * @param destinationFilesize 저정할 파일사이즈
     * @return true일 경우 작업을 계속 진행하고, false일 경우 작업을 종료한다.
     */
     boolean progress(String targetFilename, long targetFilesize, String destinationFilename, long destinationFilesize);
    
    /**
     * 다운로드 작업을 완료한다.
     * 
     * @param targetFilename 원격지 파일이름
     * @param targetFilesize 원격지 파일사이즈
     * @param destinationFilename 저장할 파일이름
     * @param destinationFilesize 저정할 파일사이즈
     */
    void end(String targetFilename, long targetFilesize, String destinationFilename, long destinationFilesize);

    /**
     * 다운로드 작업을 취소한다.
     * 
     * @param targetFilename 원격지 파일이름
     * @param targetFilesize 원격지 파일사이즈
     * @param destinationFilename 저장할 파일이름
     * @param destinationFilesize 저정할 파일사이즈
     */
    void cancel(String remoteFilename, long remoteFileSize, String localFilename, long localFileSize);
}
