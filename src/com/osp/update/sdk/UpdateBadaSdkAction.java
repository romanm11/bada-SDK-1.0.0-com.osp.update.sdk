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
        String localLocation = getBadaSdkRoot() + java.io.File.separatorChar + UPDATE_FILE;

        //��ġ�� ������ �д´�.
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
        
        //������� ������ �д´�.
        UpdateInfo remoteUpdateInfo = new UpdateInfo(new URL(remoteLocation));
        remoteUpdateInfo.execute();
            
        //�ٿ�ε� ����� �����.
        List<FileInformation> downloadFileInformation = new LinkedList<FileInformation>();
        
        int index = 0;
        int sdkSize = remoteUpdateInfo.getSdkSize();
        boolean found = false;
        
        for (; index < sdkSize; index++) {
            //������ ����� ������� ������ ���Ѵ�. ã�� �������� �ƹ��� �ൿ�� ���� �ʴ´�.
            if (localFileInformation != null && found == false) {
                if (remoteUpdateInfo.getSdkVersion(index).equals(localFileInformation.getProperty("VER")) == false)
                    continue;
                
                found = true;
            }
            
            //���ÿ� ������ ���� ���, ���� ������ ã�� ���ϸ� ���ʿ� ���� �� ���.
            //Update������ ���� ��Ƿ� ��ġ �ǹǷ�
            List<FileInformation> fileInformationList = remoteUpdateInfo.getSdk(index);
            
            int subIndex = 0;
            int fileSize = fileInformationList.size();
            
            for (; subIndex < fileSize; subIndex++) {
                FileInformation fileInformation = fileInformationList.get(subIndex);
            
                //���� ��ġ�� ����� ������ ������ ���
                if (localFileInformation != null &&
                    localFileInformation.getProperty("VER").equals(fileInformation.getProperty("VER")) == true
                ) {
                    //�ν��� ���� ��ġ���� �ʴ´�.
                    if (fileInformation.getProperty("Type").equals("patch") == false)
                        continue;
                    
                    //���� ��ġ�� ���� patch�̰�, �̸��� ���ٸ� ��ġ���� �ʴ´�.
                    if (localFileInformation.getProperty("Type").equals("patch") == true && 
                        fileInformation.getProperty("Name").equals(localFileInformation.getProperty("Name")) == true
                    ) 
                        continue;
                    
                    //patch�̰�, ���� ��ġ�� �� SDK�� �̸��� �ٸ� ���, Update�� ����Ѵ�.
                    downloadFileInformation.add(fileInformation);
                    continue;
                }
                    
                //���� ���� Ȥ�� ��ġ�� ���� ��� ���, �ν��縸�� ����Ѵ�.
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
        
        //��ġ�� ���� ����� �����´�.
        List<FileInformation> fileInformationList = null;
        Shell shell = window.getShell();
        
        try  {
            fileInformationList = getDownloadList();
        }
        catch (Exception e) {
            //MODIFIED 2010.05.06; �����; ��Ʈ��ũ ������ �߻����� ���, ���� �˸���. 
            //  ��û���: Email
            //  ��û��: ����ȯ å��
            //  ��������: 2010.05.06
            //  ��������: [��] ������û - 2-2���
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
            //MODIFIED 2010.05.06; �����; Update�� ���� ��� ���, ������ �˸���. 
            //  ��û���: Email
            //  ��û��: ����ȯ å��
            //  ��������: 2010.05.06
            //  ��������: [��] ������û - 2-1���
            if (startUp == false && UpdateFileFlag == true) {
                MessageDialog.openInformation(shell, "Information", "There is nothing to update.");
                return;
            }
        
            //�ڵ����� ������ ���(bada IDE�� �������� ���)���� ������ �������� �ʴ´�. �޴��� �������� �� �����ش�.
            startUp =false;
            return;
        }
        
        if (MessageDialog.openConfirm(shell, "bada SDK", "Updates are available for bada SDK. Do you want to install them?") == false)
            return;

        //�������� ���Ѵ�.
        UpdateBadaSdkWizardPage wizardPage = new UpdateBadaSdkWizardPage(UpdateBadaSdkWizardPage.PAGE_NAME);
        wizardPage.setFileInformationList(fileInformationList);
        
        //����带 ���Ѵ�.
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
			
			if( sdkRoot.endsWith(java.io.File.separator)) sdkRoot = sdkRoot.substring(0, sdkRoot.length()-1);
			
			return sdkRoot;
			
		}
		
		return "";
	}

}
