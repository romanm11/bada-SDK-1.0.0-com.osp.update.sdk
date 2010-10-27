package com.osp.update.sdk;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.osp.update.httpdownloader.FileInformation;

public class UpdateBadaSdkWizardPage extends WizardPage {
    public static String PAGE_NAME = "MainPage";
    public static String TITLE = "Update Information";
    public static String DESCRIPTION = "Please choose a bada SDK.";
    public static String ICON = "icons/app_wizard.bmp";
    
	private boolean finish = false;
    private java.util.List<FileInformation> sitefileList = null;
    private List list = null;
    
	protected UpdateBadaSdkWizardPage(String pageName) {
		super(pageName);
		
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, ICON));
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		createListGroup(composite);

		setControl(parent);

		updatePageComplete();
		setMessage(null);
		setErrorMessage(null);
	}

	private void createListGroup(Composite parent) {
		list = new List (parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		for(int i =0; i < sitefileList.size(); i++){
		    FileInformation fileInformation = sitefileList.get(i);
		    
			list.add(
			    fileInformation.getProperty("Name") + 
			    " (ver. "+ 
			    fileInformation.getProperty("VER") +
			    ")" +
			    " - " + 
			    fileInformation.getProperty("Type")
			);
		}

		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true, 1, 1));
		
	    list.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	        	if (list.getSelectionIndex() != -1){
	        		finish = true;
	        		updatePageComplete();
	        	}
	        }
	      });
	}
	
	void setFileInformationList(java.util.List<FileInformation> fileInformationList){
		sitefileList = fileInformationList;
	}
	
	FileInformation getSelectedFileInformation(){
		return sitefileList.get(list.getSelectionIndex());
	}
	
	private void updatePageComplete() {
		setPageComplete(false);

		if (finish == false)
			return;
		setPageComplete(true);

	}
}
