package com.common.share;

import acc.appform.hrmModule.EmployeeInformation;

import com.common.share.MessageBox.ButtonType;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

public class UserCreate extends Window 
{
	private AbsoluteLayout mainLayout;

	String rId;

	private VerticalLayout btnLayout = new VerticalLayout();

	private NativeSelect userType;
	private NativeSelect isActive;

	private Label lbCurrentStatus;
	private Label lbUserType;
	private Label lbConfirmPassword;
	private Label lbPassword;
	private Label lbUserName;
	private Label lblCompanyName;
	private Label lblEmployeeName;
	private Label lblAccessModule;

	private TextField txtCompanyName;
	private ComboBox cmbEmployeeName;

	private CheckBox deleteable;
	private CheckBox updateable;
	private CheckBox insertable;

	private PasswordField confirmPassword;
	private PasswordField password;

	private TextField name;
	private TextField txtDays;

	private NativeButton btnEmployee;

	public Table table = new Table();

	public ArrayList<CheckBox> tbCheck = new ArrayList<CheckBox>();
	public ArrayList<Label> tbModuleName = new ArrayList<Label>();

	CommonButton cButton = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "", "Exit");

	private SessionBean sessionBean;
	private boolean isUpdate = false;
	private boolean isFind = false;
	private TextField txtUserID = new TextField();
	String updateId = "";

	int action = 0;

	ArrayList<Component> allComp = new ArrayList<Component>();

	public UserCreate(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("USER CREATE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmpInit();
		buttonLayoutAdd();

		tbinitialize();

		btnIni(true);
		txtEnable(false);
		buttonActionAdd();

		cmbEmployeeData();

		authencationCheck();
	}

	private void cmbEmployeeData()
	{	
		cmbEmployeeName.removeAllItems();

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List list = session.createSQLQuery(" Select vEmployeeId,vEmployeeName from tbEmployeeInfo ").list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}

	private void authencationCheck()
	{
		if(sessionBean.isSubmitable())
		{
			cButton.btnSave.setVisible(true);
		}
		else
		{
			cButton.btnSave.setVisible(false);
		}
		if(sessionBean.isUpdateable())
		{
			cButton.btnEdit.setVisible(true);
		}
		else
		{
			cButton.btnEdit.setVisible(false);
		}
		if(sessionBean.isDeleteable())
		{
			cButton.btnDelete.setVisible(true);
		}
		else
		{
			cButton.btnDelete.setVisible(false);
		}
	}

	public void tbinitialize()
	{
		for(int i=0;i<13;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{	
		tbCheck.add(ar,new CheckBox());
		tbCheck.get(ar).setWidth("100%");
		tbCheck.get(ar).setValue(false);
		tbCheck.get(ar).setImmediate(true);
		tbCheck.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				action=1;
			}
		});

		tbModuleName.add(ar,new Label());
		tbModuleName.get(ar).setWidth("100%");
		tbModuleName.get(ar).setHeight("10px");
		tbModuleName.get(ar).setImmediate(true);

		if(ar==0)
		{
			tbModuleName.get(ar).setValue("SETUP MODULE");
		}
		if(ar==1)
		{
			tbModuleName.get(ar).setValue("INVENTORY MODULE");
		}
	
		if(ar==2)
		{
			tbModuleName.get(ar).setValue("PRODUCTION MODULE");
		}
		if(ar==3)
		{
			tbModuleName.get(ar).setValue("FINISH GOODS MODULE");
		}
		if(ar==4)
		{
			tbModuleName.get(ar).setValue("DO & SALES MODULE");
		}
		if(ar==5)
		{
			tbModuleName.get(ar).setValue("ACCOUNTS MODULE");
		}
		if(ar==6)
		{
			tbModuleName.get(ar).setValue("FIXED ASSET MODULE");
		}
		if(ar==9)// For last module moduleid is 9
		{
			tbModuleName.get(ar).setValue("L/C MODULE");
		}
		if(ar==7)
		{
			tbModuleName.get(ar).setValue("HRM MODULE");
		}
		if(ar==8)
		{
			tbModuleName.get(ar).setValue("TRANSPORT MODULE");
		}
		if(ar==10)
		{
			tbModuleName.get(ar).setValue("COSTING MODULE");
		}
		if(ar==11)
		{
			tbModuleName.get(ar).setValue("SPARE PARTS MODULE");
		}
		
		if(ar==12)
		{
			//tbModuleName.get(ar).setValue("THIRD PARTY R/M MODULE");
		}
		
		if(ar==12)
		{
			tbModuleName.get(ar).setValue("CRASHING MODULE");
		}

		table.addItem(new Object[]{tbCheck.get(ar),tbModuleName.get(ar)},ar);	
	}

	private void focusEnter()
	{
		allComp.add(cmbEmployeeName);
		allComp.add(name);
		allComp.add(password);
		allComp.add(confirmPassword);
		allComp.add(userType);
		allComp.add(insertable);
		allComp.add(updateable);
		allComp.add(deleteable);
		allComp.add(isActive);

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);
		allComp.add(cButton.btnFind);
		allComp.add(cButton.btnExit);

		new FocusMoveByEnter(this,allComp);
	}

	private void buttonActionAdd()
	{
		cButton.btnNew.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				newBtnAction();
				focusEnter();
				isFind = false;
			}
		});

		cButton.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!name.getValue().toString().trim().isEmpty())
				{
					if(sessionBean.isUpdateable())
					{
						updateBtnAction();
						txtEnable(true);
						password.setEnabled(false);
						confirmPassword.setEnabled(false);
						name.focus();
						focusEnter();
						isFind = false;
					}
					else
					{
						getParent().showNotification("Warning,","You have no Proper Authentication to Edit.",Notification.TYPE_ERROR_MESSAGE);
					}
				}
				else
				{
					getParent().showNotification("Warning,","There is nothing to Edit.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isUpdateable())
				{
					saveButtonAction();
					isFind = false;
				}
				else
				{
					getParent().showNotification("Warning,","You have no Proper Authentication to Save.",Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		cButton.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				cancelBtnAction();
				isFind = false;
			}
		});

		cButton.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cButton.btnFind.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				findBtnAction();
			}
		});

		cButton.btnDelete.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isUpdateable())
				{
					deleteBtnAction();
				}
				else
				{
					getParent().showNotification("Warning,","You have no Proper Authentication to Delete.",Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		userType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(userType.getValue().toString().equals("1") || userType.getValue().toString().equals("2"))
				{
					insertable.setEnabled(false);
					updateable.setEnabled(false);
					deleteable.setEnabled(false);
					insertable.setValue(true);
					deleteable.setValue(true);
					updateable.setValue(true);
				}
				else
				{
					insertable.setEnabled(true);
					updateable.setEnabled(true);
					deleteable.setEnabled(true);
					insertable.setValue(false);
					deleteable.setValue(false);
					updateable.setValue(false);
				}
			}
		});

		name.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(!isFind)
				{
					if(!name.getValue().toString().isEmpty())
					{

					}
				}
			}
		});

		btnEmployee.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				empData();
			}
		});
	}

	public void empData()
	{
		Window win = new EmployeeInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				cmbEmployeeData();
			}
		});
		this.getParent().addWindow(win);
	}

	public boolean duplicateNameCheck() 
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		try 
		{
			String query = " Select name from tbLogin where name = '"+name.getValue()+"' ";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				return false;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return true;
	}

	private void findBtnAction() 
	{
		Window win = new UserCreateFindWindow(sessionBean, txtUserID,"SectionId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtUserID.getValue().toString().length() > 0)
				{
					clearAll();
					findInitialise(txtUserID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}	

	private void findInitialise(String txtUserId) 
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			updateId = txtUserId;

			String sql = " Select * from tbLogin Where userId = '"+txtUserId+"'";

			List led = session.createSQLQuery(sql).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				password.setValue(element[2]);
				confirmPassword.setValue(element[2]);

				cmbEmployeeName.setValue(element[11]);

				name.setValue(element[1]);

				if(element[5].toString().equals("1"))
				{	
					userType.addItem("1");
					userType.setItemCaption("1", "Admin");
					userType.setValue("1");
				}
				else
				{
					userType.addItem("0");
					userType.setItemCaption("0", "General");
					userType.setValue("0");
				}

				if(element[6].toString().equalsIgnoreCase("1"))
				{
					insertable.setValue(true);
				}
				else
				{
					insertable.setValue(false);
				}
				if(element[7].toString().equalsIgnoreCase("1"))
				{
					updateable.setValue(true);
				}
				else
				{
					updateable.setValue(false);
				}
				if(element[8].toString().equalsIgnoreCase("1"))
				{
					deleteable.setValue(true);
				}
				else
				{
					deleteable.setValue(false);
				}

				isActive.setValue(element[9]);
			}

			String sql1 = " Select userId,moduleId from tbLoginDetails  "+ 
					" where userId = '"+txtUserId+"'";

			System.out.println(sql1);

			List lst = session.createSQLQuery(sql1).list();

			for (Iterator iter = lst.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				int i = Integer.parseInt(element[1].toString());
				tbCheck.get(i).setValue(true);
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void deleteBtnAction()
	{
		if(sessionBean.isDeleteable())
		{
			if(!name.getValue().toString().trim().isEmpty())
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new MessageBox.EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							deleteData();
						}
					}
				});
			}
			else
			{
				showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for delete.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void deleteData()
	{
		Transaction tx = null;
		if(!isUpdate)
		{
			rId = autoId();
		}
		else
		{
			rId = updateId;
		}

		int isins = 0;
		int isup = 0;
		int isdel = 0;

		if(insertable.booleanValue())
		{
			isins = 1;
		}
		if(updateable.booleanValue())
		{
			isup = 1;
		}
		if(deleteable.booleanValue())
		{
			isdel = 1;
		}

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		String employeeName = cmbEmployeeName.getValue()==null?"":cmbEmployeeName.getValue().toString();
		try
		{
			tx = session.beginTransaction();

			String sql = " Delete from tbLogin where userId = '"+updateId+"' ";
			session.createSQLQuery(sql).executeUpdate();
			System.out.println(sql);

			String sqlDetails = " Delete from tbLoginDetails where userId = '"+updateId+"' ";
			session.createSQLQuery(sqlDetails).executeUpdate();
			System.out.println(sqlDetails);

			String udSql = "INSERT INTO tbUdLogin(userId,name,pass,createTime,createBy,isAdmin,isInsertable,isUpdateable,isDeleteable,isActive,isSuperAdmin, " +
					" employeeId,uId,uIp,entrytime,flag ) VALUES('"+rId+"','"+
					name.getValue()+"','"+password.getValue()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserId()+"','0','"+
					isins+"','"+isup+"','"+isdel+"','"+isActive.getValue()+"','0', " +
					" '"+employeeName+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'Deleted' " +
					" )";
			session.createSQLQuery(udSql).executeUpdate();

			for(int i=0;i<tbCheck.size();i++)
			{
				if(tbCheck.get(i).booleanValue()==true && !tbModuleName.get(i).getValue().toString().equals(""))
				{
					String udSql1 = " Insert into tbUdLoginDetails (userId,moduleId,moduleName,uId,uIp,entrytime,flag) values ( " +
							" '"+rId+"', '"+i+"', '"+tbModuleName.get(i).getValue()+"', " +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'Deleted' " +
							" ) ";
					session.createSQLQuery(udSql1).executeUpdate();
				}
			}

			tx.commit();
			clearAll();
			showNotification("Desired Information deleted successfully.");

		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void cancelBtnAction()
	{
		initialise();
		clearAll();
	}

	private void saveButtonAction()
	{
		if(!name.getValue().toString().trim().isEmpty())
		{
			if(!password.getValue().toString().trim().isEmpty())
			{
				if(!confirmPassword.getValue().toString().trim().isEmpty())
				{
					if(action>=1)
					{
						if(password.getValue().toString().equals(confirmPassword.getValue().toString()))
						{
							saveBtnAction();
						}
						else
						{
							this.getParent().showNotification("Warning!","Password & Confirm password mismatch",Notification.TYPE_WARNING_MESSAGE);
							password.focus();
						}
					}
					else
					{
						getWindow().showNotification("Warning!","Select at least one module",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					this.getParent().showNotification("Warning!","Enter Confirm Password.", Notification.TYPE_WARNING_MESSAGE);
					confirmPassword.focus();
				}
			}
			else
			{
				this.getParent().showNotification("Warning!","Enter Password.", Notification.TYPE_WARNING_MESSAGE);
				password.focus();
			}
		}
		else
		{
			this.getParent().showNotification("Warning,","Enter User Name.", Notification.TYPE_WARNING_MESSAGE);
			name.focus();
		}
	}

	public String autoId() 
	{
		String autoCode = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " Select cast(isnull(max(cast(replace(userId, '', '')as int))+1, 1)as varchar) from tbLogin ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}

	private void saveBtnAction()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update user information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						cButton.btnSave.setEnabled(false);
						updateData();
						initialise();
						cButton.btnNew.focus();
					}
				}
			});
		}
		else
		{
			if(duplicateNameCheck())
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new MessageBox.EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{			
							cButton.btnSave.setEnabled(false);
							insertData();
							initialise();
							cButton.btnNew.focus();
							clearAll();
						}
					}
				});
			}
			else
			{
				name.setValue("");
				getWindow().showNotification("Warning!","User Name Already exits",Notification.TYPE_WARNING_MESSAGE);
				name.focus();
			}
		}
	}

	private void insertData()
	{
		if(sessionBean.isSubmitable())
		{
			Transaction tx = null;

			System.out.println(updateId);

			String sql = "";
			String udSql = "";
			String flag = "";

			try
			{
				if(!isUpdate)
				{
					rId = autoId();
					flag = "New";
				}
				else
				{
					rId = updateId;
					flag = "Update";
				}

				int isins = 0;
				int isup = 0;
				int isdel = 0;

				if(insertable.booleanValue())
				{
					isins = 1;
				}
				if(updateable.booleanValue())
				{
					isup = 1;
				}
				if(deleteable.booleanValue())
				{
					isdel = 1;
				}

				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				String employeeName = cmbEmployeeName.getValue()==null?"":cmbEmployeeName.getValue().toString();

				if (userType.getValue().toString()=="2")
				{
					sql = " INSERT INTO tbLogin(userId,name,pass,createTime,createBy,isAdmin,isInsertable,isUpdateable,isDeleteable,isActive,isSuperAdmin, " +
							" employeeId) VALUES('"+rId+"','"+
							name.getValue()+"','"+password.getValue()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserId()+"','0', '"+
							isins+"','"+isup+"','"+isdel+"','"+isActive.getValue()+"','1', " +							
							" '"+employeeName+"' )";

					udSql = " INSERT INTO tbUdLogin(userId,name,pass,createTime,createBy,isAdmin,isInsertable,isUpdateable,isDeleteable,isActive,isSuperAdmin, " +
							" employeeId,uId,uIp,entrytime,flag ) VALUES('"+rId+"','"+
							name.getValue()+"','"+password.getValue()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserId()+"','0', '"+
							isins+"','"+isup+"','"+isdel+"','"+isActive.getValue()+"','1', " +
							" '"+employeeName+"', " +
							" '"+txtDays.getValue().toString()+"', " +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+flag+"' " +
							" )";
				}
				else if(userType.getValue().toString()=="1") 
				{
					sql = "INSERT INTO tbLogin(userId,name,pass,createTime,createBy,isAdmin,isInsertable,isUpdateable,isDeleteable,isActive,isSuperAdmin, " +
							" employeeId) VALUES('"+rId+"','"+
							name.getValue()+"','"+password.getValue()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserId()+"','1','"+
							isins+"','"+isup+"','"+isdel+"','"+isActive.getValue()+"','0', " +
							" '"+employeeName+"' )";

					udSql = "INSERT INTO tbUdLogin(userId,name,pass,createTime,createBy,isAdmin,isInsertable,isUpdateable,isDeleteable,isActive,isSuperAdmin, " +
							" employeeId,uId,uIp,entrytime,flag ) VALUES('"+rId+"','"+
							name.getValue()+"','"+password.getValue()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserId()+"','1','"+
							isins+"','"+isup+"','"+isdel+"','"+isActive.getValue()+"','0', " +
							" '"+employeeName+"', " +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+flag+"' " +
							" )";
				}
				else
				{
					sql = "INSERT INTO tbLogin(userId,name,pass,createTime,createBy,isAdmin,isInsertable,isUpdateable,isDeleteable,isActive,isSuperAdmin, " +
							" employeeId ) VALUES('"+rId+"','"+
							name.getValue()+"','"+password.getValue()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserId()+"','0','"+
							isins+"','"+isup+"','"+isdel+"','"+isActive.getValue()+"','0', " +
							" '"+employeeName+"' )";

					udSql = "INSERT INTO tbUdLogin(userId,name,pass,createTime,createBy,isAdmin,isInsertable,isUpdateable,isDeleteable,isActive,isSuperAdmin, " +
							" employeeId,uId,uIp,entrytime,flag ) VALUES('"+rId+"','"+
							name.getValue()+"','"+password.getValue()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserId()+"','0','"+
							isins+"','"+isup+"','"+isdel+"','"+isActive.getValue()+"','0', " +
							" '"+employeeName+"', " +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+flag+"' " +
							" )";
				}

				for(int i=0;i<tbCheck.size();i++)
				{
					if(tbCheck.get(i).booleanValue()==true && !tbModuleName.get(i).getValue().toString().equals(""))
					{	
						String sql1 = " Insert into tbLoginDetails (userId,moduleId,moduleName) values ( " +
								" '"+rId+"', '"+i+"', '"+tbModuleName.get(i).getValue()+"' ) ";

						String udSql1 = " Insert into tbUdLoginDetails (userId,moduleId,moduleName,uId,uIp,entrytime,flag) values ( " +
								" '"+rId+"', '"+i+"', '"+tbModuleName.get(i).getValue()+"', " +
								" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+flag+"' " +
								" ) ";

						session.createSQLQuery(sql1).executeUpdate();
						session.createSQLQuery(udSql1).executeUpdate();
					}
				}

				session.createSQLQuery(sql).executeUpdate();
				session.createSQLQuery(udSql).executeUpdate();
				tx.commit();

				clearAll();

				this.getParent().showNotification("User Createted successfully.");
			}
			catch(Exception exp)
			{
				tx.rollback();
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void initialise()
	{
		isUpdate = false;
		action = 0;
		txtEnable(false);
		btnIni(true);
	}

	private void updateData()
	{
		Transaction tx = null;
		String sql = "";
		String flag = "";

		try
		{
			int isins = 0;
			int isup = 0;
			int isdel = 0;

			if(insertable.booleanValue())
			{
				isins = 1;
			}
			if(updateable.booleanValue())
			{
				isup = 1;
			}
			if(deleteable.booleanValue())
			{
				isdel = 1;
			}

			if(!isUpdate)
			{
				rId = autoId();
				flag = "New";
			}
			else
			{
				rId = updateId;
				flag = "Update";
			}

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql = " Delete tbLogin where userId = '"+updateId+"' ";
			String sqlDelete = " Delete tbLoginDetails where userId = '"+updateId+"' ";

			session.createSQLQuery(sql).executeUpdate();
			session.createSQLQuery(sqlDelete).executeUpdate();
			tx.commit();

			insertData();

			this.getParent().showNotification("User update successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

		clearAll();
	}

	private void updateBtnAction()
	{
		if(sessionBean.isUpdateable())
		{
			if(!name.getValue().toString().isEmpty())
			{
				isUpdate = true;
				btnIni(false);

				if(Integer.valueOf(userType.getValue().toString())==0)
				{
					insertable.setEnabled(true);
					updateable.setEnabled(true);
					deleteable.setEnabled(true);
				}
			}
			else
			{
				this.getParent().showNotification("Edit Failed","There are no data for Edit.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void newBtnAction()
	{
		btnIni(false);
		clearAll();
		txtEnable(true); 
	}

	private void clearAll()
	{
		cmbEmployeeName.setValue(null);

		name.setValue("");
		password.setValue("");
		confirmPassword.setValue("");
		userType.setValue("0");
		insertable.setValue(false);
		deleteable.setValue(false);
		updateable.setValue(false);

		insertable.setEnabled(false);
		deleteable.setEnabled(false);
		updateable.setEnabled(false);
		isActive.setValue("1");

		for(int i=0;i<tbCheck.size();i++)
		{
			tbCheck.get(i).setValue(false);
		}
	}

	private void txtEnable(boolean t)
	{
		cmbEmployeeName.setEnabled(t);

		table.setEnabled(t);

		name.setEnabled(t);
		password.setEnabled(t);
		confirmPassword.setEnabled(t);
		userType.setEnabled(t);
		insertable.setEnabled(t);
		updateable.setEnabled(t);
		deleteable.setEnabled(t);
		isActive.setEnabled(t);
		btnEmployee.setEnabled(t);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnFind.setEnabled(t);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("730px");
		setHeight("400px");

		// lblCompanyName
		lblCompanyName = new Label();
		lblCompanyName.setImmediate(false);
		lblCompanyName.setWidth("-1px");
		lblCompanyName.setHeight("-1px");
		lblCompanyName.setValue("Company Name : ");
		mainLayout.addComponent(lblCompanyName,"top:20.0px;left:20.0px;");

		// txtCompanyName
		txtCompanyName = new TextField();
		txtCompanyName.setImmediate(true);
		txtCompanyName.setWidth("290px");
		txtCompanyName.setHeight("-1px");
		txtCompanyName.setEnabled(false);
		txtCompanyName.setValue(sessionBean.getCompany().toString());
		mainLayout.addComponent(txtCompanyName, "top:18.0px;left:130.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label();
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		lblEmployeeName.setValue("Employee Name : ");
		mainLayout.addComponent(lblEmployeeName,"top:45.0px;left:20.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setNewItemsAllowed(false);
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setWidth("230px");
		cmbEmployeeName.setHeight("-1px");
		mainLayout.addComponent(cmbEmployeeName, "top:43.0px;left:130.0px;");

		// btnEmployee
		btnEmployee = new NativeButton();
		btnEmployee.setCaption("");
		btnEmployee.setImmediate(true);
		btnEmployee.setWidth("28px");
		btnEmployee.setHeight("24px");
		btnEmployee.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnEmployee,"top:43.0px;left:365.0px;");

		// lbUserName
		lbUserName = new Label();
		lbUserName.setImmediate(false);
		lbUserName.setWidth("-1px");
		lbUserName.setHeight("-1px");
		lbUserName.setValue("User Name :");
		mainLayout.addComponent(lbUserName, "top:70.0px;left:20.0px;");

		// name
		name = new TextField();
		name.setImmediate(false);
		name.setHeight("-1px");
		name.setSecret(false);
		mainLayout.addComponent(name, "top:68.0px;left:130.0px;");

		// lbPassword
		lbPassword = new Label();
		lbPassword.setImmediate(false);
		lbPassword.setWidth("-1px");
		lbPassword.setHeight("-1px");
		lbPassword.setValue("Password :");
		mainLayout.addComponent(lbPassword, "top:95.0px;left:20.0px;");

		// password
		password = new PasswordField();
		password.setImmediate(false);
		password.setWidth("-1px");
		password.setHeight("-1px");
		mainLayout.addComponent(password, "top:93.0px;left:130.0px;");

		// lbConfirmPassword
		lbConfirmPassword = new Label();
		lbConfirmPassword.setImmediate(false);
		lbConfirmPassword.setWidth("-1px");
		lbConfirmPassword.setHeight("-1px");
		lbConfirmPassword.setValue("Confirm Password :");
		mainLayout.addComponent(lbConfirmPassword, "top:120.0px;left:20.0px;");

		// confirmPassword
		confirmPassword = new PasswordField();
		confirmPassword.setImmediate(false);
		confirmPassword.setWidth("-1px");
		confirmPassword.setHeight("-1px");
		mainLayout.addComponent(confirmPassword, "top:118.0px;left:130.0px;");

		// lbUserType
		lbUserType = new Label();
		lbUserType.setImmediate(false);
		lbUserType.setWidth("-1px");
		lbUserType.setHeight("-1px");
		lbUserType.setValue("User Type :");
		mainLayout.addComponent(lbUserType, "top:145.0px;left:20.0px;");

		// userType
		userType = new NativeSelect();
		userType.setImmediate(true);
		userType.setWidth("117px");
		userType.setHeight("-1px");
		mainLayout.addComponent(userType, "top:143.0px;left:130.0px;");

		// insertable
		insertable = new CheckBox();
		insertable.setCaption("Save/Data Entry");
		insertable.setImmediate(false);
		insertable.setWidth("-1px");
		insertable.setHeight("-1px");
		mainLayout.addComponent(insertable, "top:168.0px;left:130.0px;");

		// updateable
		updateable = new CheckBox();
		updateable.setCaption("Edit");
		updateable.setImmediate(false);
		updateable.setWidth("-1px");
		updateable.setHeight("-1px");
		mainLayout.addComponent(updateable, "top:193.0px;left:130.0px;");

		// deleteable
		deleteable = new CheckBox();
		deleteable.setCaption("Delete");
		deleteable.setImmediate(false);
		deleteable.setWidth("-1px");
		deleteable.setHeight("-1px");
		mainLayout.addComponent(deleteable, "top:218.0px;left:130.0px;");

		// lbCurrentStatus
		lbCurrentStatus = new Label();
		lbCurrentStatus.setImmediate(false);
		lbCurrentStatus.setWidth("-1px");
		lbCurrentStatus.setHeight("-1px");
		lbCurrentStatus.setValue("Current Status :");
		mainLayout.addComponent(lbCurrentStatus, "top:243.0px;left:20.0px;");

		// isActive
		isActive = new NativeSelect();
		isActive.setImmediate(true);
		isActive.setWidth("117px");
		isActive.setHeight("-1px");
		mainLayout.addComponent(isActive, "top:243.0px;left:130.0px;");

		// lblAccessModule
		lblAccessModule = new Label("<h3>Access Module</h3>", Label.CONTENT_XHTML);
		lblAccessModule.setImmediate(false);
		lblAccessModule.setWidth("-1");
		lblAccessModule.setHeight("-1");
		mainLayout.addComponent(lblAccessModule,"top:0.0px; left:520.0px;");

		//  table
		table.setWidth("245px");
		table.setHeight("250px");
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(false);
		table.setPageLength(0);
		table.addContainerProperty("Select", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Select", 40);
		table.addContainerProperty("Module Name", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Module Name", 160);

		mainLayout.addComponent(table,"top:35.0px; left:460.0px;");

		return mainLayout;
	}

	private void cmpInit()
	{
		insertable.setIcon(new ThemeResource("../icons/action_save.gif"));
		updateable.setIcon(new ThemeResource("../icons/document-edit.png"));
		deleteable.setIcon(new ThemeResource("../icons/trash.png"));

		userType.addItem("0");
		userType.setItemCaption("0", "General");
		userType.addItem("1");
		userType.setItemCaption("1", "Admin");
		userType.addItem("2");
		userType.setItemCaption("2", "Super Admin");
		userType.setImmediate(true);
		userType.setNullSelectionAllowed(false);
		userType.setValue("0");
		userType.setWidth("110px");

		isActive.addItem("1");
		isActive.setItemCaption("1", "Yes");
		isActive.addItem("0");
		isActive.setItemCaption("0", "No");
		isActive.setNullSelectionAllowed(false);
		isActive.setValue("1");
		isActive.setWidth("70px");
	}

	private void buttonLayoutAdd()
	{
		btnLayout.addComponent(cButton);
		mainLayout.addComponent(btnLayout,"top:310px;left:70px;");
	}
}
