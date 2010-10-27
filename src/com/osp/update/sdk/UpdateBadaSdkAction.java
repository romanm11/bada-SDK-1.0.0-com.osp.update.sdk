package com.osp.update.sdk;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.osp.update.httpdownloader.FileInformation;
import com.osp.update.httpdownloader.UpdateInfo;

public class UpdateBadaSdkAction implements IWorkbenchWindowActionDelegate {
    private IWorkbenchWindow window = null;
    public static String REMOTE_URL = "http://static.bada.com/sdk-update";
    public static String SDK_HOME = "BADA_SDK_HOME";
    public static String UPDATE_FILE = "Update.xml";
    public static boolean startUp = true;
    public static boolean UpdateFileFlag = true;
    
    public List<FileInformation> getDownloadList() throws Exception {
        String remoteLocation = REMOTE_URL + "/" + UPDATE_FILE;
        String localLocation = getBadaSdkRoot() + "\\" + UPDATE_FILE;

        //설치된 정보를 읽는다.
        File localFile = new File(localLocation);
        
        UpdateInfo localUpdateInfo = null;
        FileInformation localFileInformation = null;
        
        if (localFile.exists() == true) {
            localUpdateInfo = new UpdateInfo(localFile.toURI().toURL());
            localUpdateInfo.execute();
            
            if (localUpdateInfo.getSdkSize() > 0) {
                List<FileInformation> installedFileInformaiton = localUpdateInfo.getSdk(localUpdateInfo.getSdkSize() - 1);
                localFileInformation = installedFileInformaiton.get(installedFileInformaiton.size() - 1);
            }
            UpdateFileFlag = true;
        }else if(localFile.exists() == false){
            if (UpdateFileFlag == false) {
            	MessageDialog.openWarning(null, "bada SDK Update Error","Cannot find the "+ localLocation + " file. Reinstall the bada SDK.");
            	UpdateFileFlag = false;
                return null;
            }
            UpdateFileFlag = false;
            return null;            
        }
        
        //원격지의 정보를 읽는다.
        UpdateInfo remoteUpdateInfo = new UpdateInfo(new URL(remoteLocation));
        remoteUpdateInfo.execute();
            
        //다운로드 목록을 만든다.
        List<FileInformation> downloadFileInformation = new LinkedList<FileInformation>();
        
        int index = 0;
        int sdkSize = remoteUpdateInfo.getSdkSize();
        boolean found = false;
        
        for (; index < sdkSize; index++) {
            //로컬의 버전과 원격지의 버젼을 비교한다. 찾을 때까지는 아무런 행동을 하지 않는다.
            if (localFileInformation != null && found == false) {
                if (remoteUpdateInfo.getSdkVersion(index).equals(localFileInformation.getProperty("VER")) == false)
                    continue;
                
                found = true;
            }
            
            //로컬에 버젼이 있을 경우, 같은 버젼을 찾지 못하면 이쪽에 들어올 수 없다.
            //Update정보는 버전 순서되로 설치 되므로
            List<FileInformation> fileInformationList = remoteUpdateInfo.getSdk(index);
            
            int subIndex = 0;
            int fileSize = fileInformationList.size();
            
            for (; subIndex < fileSize; subIndex++) {
                FileInformation fileInformation = fileInformationList.get(subIndex);
            
                //현재 설치된 버전과 동일한 버전일 경우
                if (localFileInformation != null &&
                    localFileInformation.getProperty("VER").equals(fileInformation.getProperty("VER")) == true
                ) {
                    //인스톨 본은 설치하지 않는다.
                    if (fileInformation.getProperty("Type").equals("patch") == false)
                        continue;
                    
                    //현재 설치된 것이 patch이고, 이름이 같다면 설치하지 않는다.
                    if (localFileInformation.getProperty("Type").equals("patch") == true && 
                        fileInformation.getProperty("Name").equals(localFileInformation.getProperty("Name")) == true
                    ) 
                        continue;
                    
                    //patch이고, 현재 설치된 것 SDK의 이름이 다른 경우, Update에 등록한다.
                    downloadFileInformation.add(fileInformation);
                    continue;
                }
                    
                //현재 버전 혹은 설치된 것이 없는 경우, 인스톨만을 허락한다.
                if (fileInformation.getProperty("Type").equals("install") == true) {
                    downloadFileInformation.add(fileInformation);
                }                        
            }
        }
        
        return downloadFileInformation;
    }
    
    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(IWorkbenchWindow window) {
        this.window = window;
        
        run(null);
    }

    @Override
    public void run(IAction action) {
        // TODO Auto-generated method stub
        if (window == null)
            return;
        
        //설치할 파일 목록을 가져온다.
        List<FileInformation> fileInformationList = null;
        Shell shell = window.getShell();
        
        try  {
            fileInformationList = getDownloadList();
        }
        catch (Exception e) {
            //MODIFIED 2010.05.06; 전수완; 네트웍크 에러가 발생했을 경우, 오류를 알린다. 
            //  요청방법: Email
            //  요청자: 김진환 책임
            //  수신일자: 2010.05.06
            //  메일제목: [급] 수정요청 - 2-2번내용
        	if (startUp == false) {
        		MessageDialog.openError(
        				shell, 
        				"Error connecting to the bada Update site", 
        				"Unable to connect to the bada Update site due to an unstable network connection."
        		);
        	}
        	startUp = false;
            return;
        }
        
        if (fileInformationList == null || fileInformationList.size() == 0) {
            //MODIFIED 2010.05.06; 전수완; Update할 것이 없는 경우, 정보를 알린다. 
            //  요청방법: Email
            //  요청자: 김진환 책임
            //  수신일자: 2010.05.06
            //  메일제목: [급] 수정요청 - 2-1번내용
            if (startUp == false && UpdateFileFlag == true) {
                MessageDialog.openInformation(shell, "Information", "There is nothing to update.");
                return;
            }
        
            //자동으로 시작할 경우(bada IDE가 시작했을 경우)에는 정보를 보여주지 않는다. 메뉴를 선택했을 때 보여준다.
            startUp =false;
            return;
        }
        
        if (MessageDialog.openConfirm(shell, "bada SDK", "Updates are available for bada SDK. Do you want to install them?") == false)
            return;

        //페이지를 생성한다.
        UpdateBadaSdkWizardPage wizardPage = new UpdateBadaSdkWizardPage(UpdateBadaSdkWizardPage.PAGE_NAME);
        wizardPage.setFileInformationList(fileInformationList);
        
        //위저드를 생성한다.
        WizardDialog dialog = new WizardDialog(shell,  new UpdateBadaSdkWizard(wizardPage));
        dialog.open();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // TODO Auto-generated method stub

    }
	public static String getBadaSdkRoot()
	{
		if( Platform.getInstallLocation() != null)
		{
			String sdkRoot = new Path (Platform.getInstallLocation().getURL().getPath()).removeLastSegments(1).toOSString();
			
			if( sdkRoot.endsWith("\\")) sdkRoot = sdkRoot.substring(0, sdkRoot.length()-1);
			
			return sdkRoot;
			
		}
		
		return "";
	}

}
