package com.osp.update.httpdownloader;

/**
 * Downloader
 * 다운로드 인터페이스
 * @author 전수완
 *
 */
public interface Downloader {
    /**
     * 다운로드 작업을 수행한다.
     * @return true 다운로드 완료, false 다운로드 미완료
     * @throws Exception 다운로드를 진행할 수 없는 상태
     */
    public boolean  execute() throws Exception;
}
