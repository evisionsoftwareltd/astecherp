package acc.appform.hrmModule;

import com.common.share.*;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class AddBirthDayCertificate extends Window 
{
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout hrLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();

	public FileUploadBirthDate imageFileNew = new FileUploadBirthDate("Image");

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public AddBirthDayCertificate(SessionBean sessionBean)
	{		
		this.sessionBean = sessionBean;

		this.setWidth("500px");
		this.setHeight("690px");
		this.setResizable(false);

		cmpInitialize();
		cmpAddition();
	}

	private void cmpInitialize()
	{
		
	}

	private void cmpAddition()
	{
		btnLayout.setSpacing(true);

		hrLayout.addComponent(imageFileNew);

		mainLayout.addComponent(hrLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		
		addComponent(mainLayout);
	}

}
