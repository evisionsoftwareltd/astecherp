package com.common.share;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import database.hibernate.TbLogin;

@SuppressWarnings("serial")
public class ChangePass extends Window 
{
	CommonButton button = new CommonButton("", "", "Edit", "", "", "", "", "", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private TextField name = new TextField("User Name:");
	private PasswordField presentPass = new PasswordField("Present Password:");
	private PasswordField password = new PasswordField("New Password:");
	private PasswordField confirmPassword = new PasswordField("Confirm New Password:");

	private String cw = "230px";

	public ChangePass(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("CHANGE PASSWORD :: "+sessionBean.getCompany());
		this.setWidth("450px");
		this.setResizable(false);

		formLayout.addComponent(name);
		name.setWidth(cw);

		presentPass.setWidth(cw);
		formLayout.addComponent(presentPass);

		password.setWidth(cw);
		formLayout.addComponent(password);
		confirmPassword.setWidth(cw);
		formLayout.addComponent(confirmPassword);

		btnLayout.addComponent(button);		

		btnLayout.setSpacing(true);
		formLayout.addComponent(btnLayout);

		formLayout.setMargin(true);
		mainLayout.addComponent(formLayout);

		this.addComponent(mainLayout);
		buttonActionAdd();
		userNameInitialise();
	}
	
	private void userNameInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			List<?> com = session.createQuery("SELECT c FROM TbLogin as c WHERE c.userId = "+sessionBean.getUserId()).list();
			Iterator<?> iter = com.iterator();
			TbLogin element = (TbLogin) iter.next();
			name.setValue(element.getName());
			name.setDebugId(element.getPass());
			name.setReadOnly(true);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void buttonActionAdd()
	{
		button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				updateBtnAction();
			}
		});
		
		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				exitBtnAction();
			}
		});
	}
	
	private void updateBtnAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to change password?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					updateData();
					presentPass.setValue("");
					password.setValue("");
					confirmPassword.setValue("");
				}
			}
		});
	}
	
	private void updateData()
	{
		if(!presentPass.getValue().toString().equals(name.getDebugId()))
		{
			showNotification( "","Please insert valid present password.",Notification.TYPE_WARNING_MESSAGE);
		}
		else if(!password.getValue().toString().equals(confirmPassword.getValue().toString()))
		{
			showNotification( "","Mismatch new password & confirm new password",Notification.TYPE_WARNING_MESSAGE);
		}
		else
		{
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String sql = "UPDATE tbLogin SET pass = '"+password.getValue()+"' WHERE userId = '"+sessionBean.getUserId()+"'";
				session.createSQLQuery(sql).executeUpdate();
				tx.commit();
				showNotification("Password update successfully.");
			}
			catch(Exception exp)
			{
				tx.rollback();
				showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}
	
	private void exitBtnAction()
	{
		this.close();
	}
}
