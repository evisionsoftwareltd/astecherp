package com.common.share;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.menuform.menu.RootMenu;

import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class LogIn extends Window 
{
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();

	private HorizontalLayout hLayout = new HorizontalLayout();
	public FormLayout frmImage = new FormLayout();

	private HorizontalLayout btnLayout = new HorizontalLayout();
	private ComboBox cmbCompany = new ComboBox("Company Name:");
	private TextField name = new TextField("User Name:");
	private PasswordField password = new PasswordField("Password:");	
	private NativeButton loginBtn = new NativeButton("LogIn");

	public Button btnImage = new Button("");

	private Label lblHeight = new Label();

	private String cw = "180px";
	private String warId;
	HashMap<Object, Object> ohm = new HashMap<Object, Object>();

	Embedded eStu_I;

	public LogIn(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LOG IN :: "+sessionBean.getCompany());
		this.setWidth("610px");
		this.setHeight("250px");
		this.setResizable(false);
		this.center();
		this.setClosable(false);
		this.setDraggable(false);

		loadWarId();
		sessionBean.setWar(warId);

		formLayout.addComponent(cmbCompany);
		cmbCompany.setWidth("250px");
		cmbCompany.setNullSelectionAllowed(false);

		name.setInputPrompt("User Name");
		formLayout.addComponent(name);		
		name.setWidth(cw);

		password.setWidth(cw);
		password.setInputPrompt("password");
		formLayout.addComponent(password);
		lblHeight.setHeight("10px");
		formLayout.addComponent(lblHeight);

		btnLayout.addComponent(loginBtn);
		loginBtn.setStyleName("loginBtn");
		loginBtn.setWidth("80px");
		loginBtn.setHeight("28px");

		btnLayout.setSpacing(true);
		formLayout.addComponent(btnLayout);

		formLayout.setMargin(true);

		eStu_I = new Embedded("",new ThemeResource("../icons/astechLogo.png"));
		eStu_I.requestRepaint();
		eStu_I.setWidth("126px");
		eStu_I.setHeight("100px");
		frmImage.removeAllComponents();
		frmImage.addComponent(eStu_I);
		
		hLayout.addComponent(frmImage);
		hLayout.addComponent(formLayout);
		mainLayout.addComponent(hLayout);
		this.addComponent(mainLayout);

		companyLoad();
		cmbCompany.setEnabled(false);

		buttonActionAdd();

		Component ob[] = {name,password,loginBtn};
		new FocusMoveByEnter(this,ob);
		setStyleName("cwindow");
		name.focus();
	}

	private void buttonActionAdd()
	{
		loginBtn.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				loginBtnAction();
			}
		});		
	}
	
	

	private void loginBtnAction()
	{
		/* File file = new File("D:\\Tomcat 7.0\\temp");     
        String[] myFiles;   
            if(file.isDirectory()){
                myFiles = file.list();
                for (int i=0; i<myFiles.length; i++) 
                {
                    File myFile = new File(file, myFiles[i]);
                    myFile.delete();
                }
             }
		*/
		
		if(isExpired())
			try
		{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				session.beginTransaction();
				Iterator<?> iter = session.createSQLQuery("SELECT * FROM TbLogin WHERE name = '"+name.getValue()+"' AND pass = '"+password.getValue()+"'").list().iterator();

				if(iter.hasNext() && cmbCompany.getValue() != null)
				{
					Object[] element = (Object[]) iter.next();

					if(!element[1].toString().equals(name.getValue().toString()) || !element[2].toString().equals(password.getValue().toString()))
					{
						this.getParent().showNotification("Login Fail","Please insert valid user name & password",Notification.TYPE_WARNING_MESSAGE);
					}
					else if(Integer.parseInt(element[9].toString()) == 1)
					{
						sessionBean.setUserId(element[0]+"");
						sessionBean.setUserName(element[1].toString());
						if(Integer.parseInt(element[5].toString()) == 1)
							sessionBean.isAdmin(true);
						else
							sessionBean.isAdmin(false);
						if(Integer.parseInt(element[6].toString()) == 1)
							sessionBean.setSubmitable(true);
						else
							sessionBean.setSubmitable(false);
						if(Integer.parseInt(element[7].toString()) == 1)
							sessionBean.setUpdateable(true);
						else
							sessionBean.setUpdateable(false);
						if(Integer.parseInt(element[8].toString()) == 1)
							sessionBean.setDeleteable(true);
						else
							sessionBean.setDeleteable(false);

						if(Integer.parseInt(element[10].toString()) == 1)
							sessionBean.isSuperAdmin(true);
						else
							sessionBean.isSuperAdmin(false);

						iter.remove();

						String sql = "SELECT companyName, address, phoneNo, fax, email, imageLoc FROM TbCompanyInfo where companyId = '"+cmbCompany.getValue().toString()+"'";
						iter = session.createSQLQuery(sql).list().iterator();
						if(iter.hasNext())
						{
							Object[] elmnt = (Object[]) iter.next();
							sessionBean.setCompanyId(cmbCompany.getValue().toString());
							sessionBean.setCompany(elmnt[0].toString());
							sessionBean.setCompanyAddress(elmnt[1].toString());
							sessionBean.setCompanyContact("Phone: " + elmnt[2].toString()+", Fax: "+elmnt[3].toString()+", Email: "+elmnt[4].toString());
							sessionBean.setCompanyLogo(elmnt[5].toString());
						}
						userIpSet();

						RootMenu rm = new RootMenu(sessionBean);
						File  fileLogo = new File(sessionBean.getCompanyLogo());
						rm.btnImage.setIcon(new FileResource(fileLogo,getApplication()));
						this.getParent().addComponent(rm);
					     //addComponent(rm);
						this.close();
					}
					else
					{
						showNotification("Account Locked","This account is locked by administrator.Please contact with adminstrator.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Login Fail","Please insert valid Company, User Name & Password",Notification.TYPE_WARNING_MESSAGE);
				}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean isExpired()
	{
		System.out.println("DateValid: "+new Date().getTime());
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String s = session.createSQLQuery("SELECT COUNT(*) FROM tbCompanyInfo WHERE cast(validTime as bigint)> "+(new Date().getTime()+" and companyId = '"+cmbCompany.getValue().toString()+"' " )).list().listIterator().next().toString();

			if(s.trim().equals("1"))
			{
				return true;
			}
			else
			{
				session.createSQLQuery("UPDATE tbCompanyInfo SET validTime = 'a' where companyId = '"+warId+"' ").executeUpdate();
				tx.commit();
				this.getParent().showNotification("Error","Error converting data type varchar to bigint.",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error2","Error converting data type varchar to bigint.",Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}

	@SuppressWarnings("static-access")
	private void loadWarId()
	{
		sessionBean.imageLogo = "D:/Tomcat 7.0/webapps/report/astecherp/";
		sessionBean.supplierLogo = "D:/Tomcat 7.0/webapps/report/astecherp/supplier/";
		sessionBean.employeeImage = "D:/Tomcat 7.0/webapps/report/astecherp/employee/";
		sessionBean.ProductImage = "D:/Tomcat 7.0/webapps/report/astecherp/Product/";
		sessionBean.employeeBirth = "D:/Tomcat 7.0/webapps/report/astecherp/employee/birth/";
		sessionBean.employeeNid = "D:/Tomcat 7.0/webapps/report/astecherp/employee/nid/";
		sessionBean.employeeApplication = "D:/Tomcat 7.0/webapps/astecherp/uptd/employee/application/";
		sessionBean.employeeJoin = "D:/Tomcat 7.0/webapps/report/astecherp/employee/join/";
		sessionBean.vehicleMaintenBillImage = "D:/Tomcat 7.0/webapps/report/astecherp/vehicleBill/";
		sessionBean.Requisition = "D:/Tomcat 7.0/webapps/report/astecherp/Requisition/";
		sessionBean.Purchase = "D:/Tomcat 7.0/webapps/report/astecherp/Purchase/";
	}

	private void companyLoad()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		List<?> list = session.createSQLQuery("SELECT companyId, companyName FROM TbCompanyInfo where"
				+ " companyId = '1'").list();
		for(Iterator<?> iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbCompany.addItem(element[0].toString());
			cmbCompany.setItemCaption(element[0].toString(), element[1].toString());	
			cmbCompany.setValue(element[0].toString());
		}
	}

	private void userIpSet()
	{
		WebApplicationContext context = ((WebApplicationContext) getApplication().getContext());
		WebBrowser webBrowser = context.getBrowser();
		sessionBean.setUserIp(webBrowser.getAddress());
	}
}