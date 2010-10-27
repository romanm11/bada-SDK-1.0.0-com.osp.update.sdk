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
 * �������� �ִ� ������ �ٿ�ε��Ѵ�.
 * @author ������
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
        
        //���� ������ ������ ���Ѵ�. (���������� �ٿ� ���� �ͺ��� �ٽ� �޴´�.)
        File  localFile = new File(getLocalFilename());
        
        if (localFile.exists() == true) {
            localFileSize = localFile.length();
        }
        
        //�ٿ�ε� ���� ������ ������ ���Ѵ�.
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(getRemoteFilename()); 
        HttpResponse response = httpclient.execute(httpget);

        if (response.getStatusLine().getStatusCode() != HTTP_OK) {
            throw new HttpResponseException(
                response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()
            );
        }

        //������ ������ ������ ������ ���Ѵ�.
        HttpEntity entity = response.getEntity();
        remoteFileSize = entity.getContentLength();

        if (downloaderListener != null)
            downloaderListener.begin(getRemoteFilename(), remoteFileSize, getLocalFilename(), localFileSize);
        
        //�������� ���ϰ� ���� ������ ����� �����ϸ� �۾��� �������� �ʴ´�.
        if (localFileSize == remoteFileSize) {
            if (downloaderListener != null)
                downloaderListener.end(getRemoteFilename(), remoteFileSize, getLocalFilename(), localFileSize);
            
            return true;
        }

        if (entity != null) {
                //���� ������ �д´�.
            BufferedInputStream input = null;
            RandomAccessFile output = null;
            
            try {
                input = new BufferedInputStream(entity.getContent());
                input.skip(localFileSize);
                
                //���������� �ٿ�ε�� ��ġ�� �̵��Ѵ�.
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
     * �ٿ�ε��� ������ ��θ� �����Ѵ�.
     * @return the remoteFilename ������ ���
     */
    public String getRemoteFilename() {
        return remoteFilename;
    }

    /**
     * ������ ��θ� �����´�.
     * @param remoteFilename ������ ���(HTTP URL)
     */
    public void setRemoteFilename(String remoteFilename) {
        this.remoteFilename = remoteFilename;
    }

    /**
     * ������ ������ ������ �̸��� �����Ѵ�.
     * @return the localFilename ���� �����̸�
     */
    public String getLocalFilename() {
        return localFilename;
    }

    /**
     * ������ ������ ������ �̸��� �����´�.
     * @param localFilename ���� �����̸�
     */
    public void setLocalFilename(String localFilename) {
        this.localFilename = localFilename;
    }

    /**
     * �ٿ�ε� �۾��� �˸��� DownloaderListener�� �����Ѵ�.
     * @return the httpDownloaderListener DownloaderListener
     */
    public DownloaderListener getDownloaderListener() {
        return downloaderListener;
    }

    /**
     * �ٿ�ε� �۾��� �˸��� DownloaderListener�� �����ش�.
     * @param httpDownloaderListener DownloaderListener
     */
    public void setDownloaderListener(DownloaderListener httpDownloaderListener) {
        this.downloaderListener = httpDownloaderListener;
    }
    
    
}
