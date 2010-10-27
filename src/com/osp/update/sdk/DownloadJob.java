/**
 * 
 */
package com.osp.update.sdk;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.osp.update.httpdownloader.DownloaderListener;
import com.osp.update.httpdownloader.FileInformation;
import com.osp.update.httpdownloader.HttpDownloader;

/**
 * @author Suwan Jeon
 *
 */
public class DownloadJob extends Job {
    public static String TASK = "Download from bada SDK Update Site";
    public static String TMP_PATH = "bada.tmp";
    private FileInformation fileInformation = null;
    
    private static class DownloaderListenerImpl implements DownloaderListener {
        private IProgressMonitor monitor = null;
        private IStatus status = Status.OK_STATUS;
        private int previousPercentage = 0;
        
        /**
         * 다운로드 진행상태를 나타낸다.
         * @param monitor 진행모니터
         */
        public DownloaderListenerImpl(IProgressMonitor monitor) {
            this.monitor = monitor;
        }
        
        /**
         * 종료 상태를 돌려준다.
         * @return the status 종료상태
         */
        public IStatus getStatus() {
            return status;
        }

        /* (non-Javadoc)
         * @see com.osp.update.httpdownloader.DownloaderListener#begin(java.lang.String, long, java.lang.String, long)
         */
        @Override
        public void begin(String targetFilename, long targetFilesize, String destinationFilename, long destinationFilesize) {
            monitor.beginTask(TASK, 100);
        }

        /* (non-Javadoc)
         * @see com.osp.update.httpdownloader.DownloaderListener#end(java.lang.String, long, java.lang.String, long)
         */
        @Override
        public void end(String targetFilename, long targetFilesize, String destinationFilename, long destinationFilesize) {
            monitor.done();
            status = Status.OK_STATUS;
        }
        
        /* (non-Javadoc)
         * @see com.osp.update.httpdownloader.DownloaderListener#cancel(java.lang.String, long, java.lang.String, long)
         */
        @Override
        public void cancel(String remoteFilename, long remoteFileSize, String localFilename, long localFileSize) {
            monitor.done();
            status = Status.CANCEL_STATUS;
        }
        
        /* (non-Javadoc)
         * @see com.osp.update.httpdownloader.DownloaderListener#progress(java.lang.String, long, java.lang.String, long)
         */
        @Override
        public boolean progress(String targetFilename, long targetFilesize, String destinationFilename, long destinationFilesize) {
                if (monitor.isCanceled() == true)
                    return false;
                
                int percentage = (int)((double)destinationFilesize/(double)targetFilesize*100);
                monitor.subTask("Update completed: " + percentage + "%");
                monitor.worked(percentage - previousPercentage);
                
                previousPercentage = percentage;
                
                try {
                    Thread.sleep(1);
                }
                catch (Exception e) {
                    return false;
                }
                
                return true;
            }            
    }
    
    /**
     * 생성자
     * @param string 다운로드 타이틀
     * @param fileInformation 다운로드 정보
     */
    public DownloadJob(String string, FileInformation fileInformation) {
        super(string);
        this.setName(string);
        this.fileInformation = fileInformation;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        HttpDownloader httpDownloader = new HttpDownloader();
        
        //다운로드 파일명을 생성한다.
        String remoteFilename = fileInformation.getProperty("URL");
        String remoteFilenameToken[] = remoteFilename.split("\\/");
        String localFilename = remoteFilenameToken[remoteFilenameToken.length - 1];

        DownloaderListenerImpl downloadListenerImpl = new DownloaderListenerImpl(monitor);
        
        httpDownloader.setRemoteFilename(remoteFilename);
        httpDownloader.setLocalFilename(localFilename);
        httpDownloader.setDownloaderListener(downloadListenerImpl);

        try {
            httpDownloader.execute();
        } catch (Exception e1) {
            return Status.CANCEL_STATUS;
        }
        
        if (downloadListenerImpl.getStatus() == Status.OK_STATUS) {
            try {
                Process process = Runtime.getRuntime().exec(new String[] {"cmd.exe",  "/c", localFilename});
                process.waitFor();
                
                File file = new File(localFilename);
                
                if (file.exists() == true)
                    file.delete();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return downloadListenerImpl.getStatus();
    }
}
