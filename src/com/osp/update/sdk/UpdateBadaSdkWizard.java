package com.osp.update.sdk;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.osp.update.httpdownloader.FileInformation;

public class UpdateBadaSdkWizard extends Wizard implements INewWizard {
    public static String TITLE = "Update bada SDK";
    public static String JOB = "Download from the bada update site";
    
	private UpdateBadaSdkWizardPage updateBadaSdkWizardPage = null;

	public UpdateBadaSdkWizard(UpdateBadaSdkWizardPage updateBadaSdkWizardPage) {
		super();
		
		this.updateBadaSdkWizardPage = updateBadaSdkWizardPage;
        addPage(updateBadaSdkWizardPage);
        
		setWindowTitle(TITLE);
		
		//setDialogSettings(CUIPlugin.getDefault().getDialogSettings());
		setNeedsProgressMonitor(true);
		setForcePreviousAndNextButtons(true);
	}

	 @Override
	 public void init(IWorkbench workbench, IStructuredSelection selection) {
	     //선택된 대상이 없으므로, 처리하지 않는다.
	 }
	   
	@Override
	public boolean performFinish() {
		FileInformation fileInformation = updateBadaSdkWizardPage.getSelectedFileInformation();
		
		if (fileInformation == null) {
		    return false;
		}
		
		Job job = new DownloadJob(JOB, fileInformation);
		job.setName(job.getName() + " " + job.hashCode());
		job.setUser(true);
		job.schedule();
			return true;
	}
}
