/**
 * 
 */
package com.osp.update.httpdownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * HTTP Downloader
 * 원격지에 있는 파일을 다운로드한다.
 * @author 전수완
 *
 */
public class HttpDownloader implements Downloader {
    public static int DEFAULT_BUFFER_SIZE = 4096;
    public static int HTTP_OK = 200;
    
    private String remoteFilename = null;
    private String localFilename = null;
    private DownloaderListener downloaderListener = null;
    
    /* (non-Javadoc)
     * @see com.spike.httpdownloader.Downloader#execute()
     */
    @Override
    public boolean  execute() throws Exception {
        long localFileSize = 0;
        long remoteFileSize = 0;
        boolean run = true;
        
        //현재 파일의 정보를 구한다. (마지막으로 다운 받은 것부터 다시 받는다.)
        File  localFile = new File(getLocalFilename());
        
        if (localFile.exists() == true) {
            localFileSize = localFile.length();
        }
        
        //다운로드 받을 파일의 정보를 구한다.
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(getRemoteFilename()); 
        HttpResponse response = httpclient.execute(httpget);

        if (response.getStatusLine().getStatusCode() != HTTP_OK) {
            throw new HttpResponseException(
                response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()
            );
        }

        //원격지 파일의 사이즈 정보를 구한다.
        HttpEntity entity = response.getEntity();
        remoteFileSize = entity.getContentLength();

        if (downloaderListener != null)
            downloaderListener.begin(getRemoteFilename(), remoteFileSize, getLocalFilename(), localFileSize);
        
        //원격지의 파일과 현재 파일의 사이즈가 동일하면 작업을 수행하지 않는다.
        if (localFileSize == remoteFileSize) {
            if (downloaderListener != null)
                downloaderListener.end(getRemoteFilename(), remoteFileSize, getLocalFilename(), localFileSize);
            
            return true;
        }

        if (entity != null) {
                //원격 파일을 읽는다.
            BufferedInputStream input = null;
            RandomAccessFile output = null;
            
            try {
                input = new BufferedInputStream(entity.getContent());
                input.skip(localFileSize);
                
                //마지막으로 다운로드된 위치로 이동한다.
                output =  new RandomAccessFile( localFile, "rw");
                output.seek(localFileSize); 
                
                    
                byte[] block = new byte[DEFAULT_BUFFER_SIZE];
                int bytes = 0;
                
                while (run == true && ((bytes = input.read(block, 0, DEFAULT_BUFFER_SIZE)) > -1)){
                    output.write(block, 0, bytes);
                    localFileSize += bytes;
                    
                    if (downloaderListener != null)
                        run = downloaderListener.progress(getRemoteFilename(), remoteFileSize, getLocalFilename(), localFileSize);
                }
                
                output.close();
                input.close();
            }
            catch (Exception e) {
                if (output != null)
                    output.close();
                
                if (input != null)
                    input.close();

                if (downloaderListener != null && run == false)
                        downloaderListener.cancel(getRemoteFilename(), remoteFileSize, getLocalFilename(), localFileSize);
                
                throw e;
            }
        }

        httpclient.getConnectionManager().shutdown();
        
        if (downloaderListener != null) {
            if (run == true)
                downloaderListener.end(getRemoteFilename(), remoteFileSize, getLocalFilename(), localFileSize);
            else
                downloaderListener.cancel(getRemoteFilename(), remoteFileSize, getLocalFilename(), localFileSize);
        }
        
        return run;
    }

    /**
     * 다운로드할 원격지 경로를 설정한다.
     * @return the remoteFilename 원격지 경로
     */
    public String getRemoteFilename() {
        return remoteFilename;
    }

    /**
     * 원격지 경로를 가져온다.
     * @param remoteFilename 원격지 경로(HTTP URL)
     */
    public void setRemoteFilename(String remoteFilename) {
        this.remoteFilename = remoteFilename;
    }

    /**
     * 원격지 파일을 저장할 이름을 설정한다.
     * @return the localFilename 저장 파일이름
     */
    public String getLocalFilename() {
        return localFilename;
    }

    /**
     * 원격지 파일을 저장할 이름을 가져온다.
     * @param localFilename 저장 파일이름
     */
    public void setLocalFilename(String localFilename) {
        this.localFilename = localFilename;
    }

    /**
     * 다운로드 작업을 알리는 DownloaderListener를 설정한다.
     * @return the httpDownloaderListener DownloaderListener
     */
    public DownloaderListener getDownloaderListener() {
        return downloaderListener;
    }

    /**
     * 다운로드 작업을 알리는 DownloaderListener를 돌려준다.
     * @param httpDownloaderListener DownloaderListener
     */
    public void setDownloaderListener(DownloaderListener httpDownloaderListener) {
        this.downloaderListener = httpDownloaderListener;
    }
    
    
}
