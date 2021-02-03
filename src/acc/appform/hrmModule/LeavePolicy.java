package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import java.text.SimpleDateFormat;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LeavePolicy extends Window 
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;
	String computerName = "";
	String userName = "";
	String year = "";
	String deptID = "";

	private boolean isUpdate=false;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
	private Label lblYear;
	private Label lblCasualLeave;
	private Label lblSickLeave;
	private Label lblAnnualLeave;
	private Label lblMaternityLeave;
	private Label lblCarryForWordCassual;
	private Label lblCarryForWordSick;
	private Label lblCarryForWordAnnual;
	private Label lblCarryForWordMaternity;
	private InlineDateField dYear;
	private AmountField txtCasual ;
	private AmountField txtSick ;
	private AmountField txtAnnual;
	private AmountField txtMaternal;
	private AmountField txtCarryCl;
	private AmountField txtCarrySl;
	private AmountField txtCarryAl;
	private AmountField txtCarryMl;
	private TextField txreceiptId= new TextField();
	ArrayList<Component> allComp = new ArrayList<Component>();

	String textFieldWidth = "70px";

	public LeavePolicy(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		computerName = sessionBean.getUserName();
		userName = sessionBean.getUserName();

		this.setCaption("LEAVE POLICY :: "+sessionBean.getCompany());
		this.setWidth("540px");
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		txtCasual.focus();
		variousButtonAction();
		focusEnter();
		openingLeaveLoad();
		authenticationCheck();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			button.btnSave.setVisible(false);
		}
		if(!sessionBean.isUpdateable()){
			button.btnEdit.setVisible(false);
		}
		if(!sessionBean.isDeleteable()){
			button.btnDelete.setVisible(false);
		}
	}

	private void openingLeaveLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select isnull(iClOpening,0) ab,isnull(iCarryCl,0) bc ,isnull(iSlOpening,0) ca ,isnull(iCarrySl,0) sd,isnull(iAlOpening,0) xy," +
					"isnull(iCarryAl,0) ds,isnull(iMlOpening,0) gf,isnull(iCarryMl,0) hg from tbLeaveBalanceNew " +
					"where year(currentYear)='"+dateformat.format(dYear.getValue())+"'";
			List <?> list = session.createSQLQuery(query).list();
			Iterator <?> iter = list.iterator();
			if(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				txtCasual.setValue(element[0].toString());
				txtCarryCl.setValue(element[1].toString());
				txtSick.setValue(element[2].toString());
				txtCarrySl.setValue(element[3].toString());
				txtAnnual.setValue(element[4].toString());
				txtCarryAl.setValue(element[5].toString());
				txtMaternal.setValue(element[6].toString());
				txtCarryMl.setValue(element[7].toString());
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);;
	}

	private void componentIni(boolean b) 
	{
		dYear.setEnabled(!b);
		txtCasual.setEnabled(!b);
		txtSick.setEnabled(!b);
		txtAnnual.setEnabled(!b);
		txtMaternal.setEnabled(!b);
		txtCarryCl.setEnabled(!b);
		txtCarrySl.setEnabled(!b);
		txtCarryAl.setEnabled(!b);
		txtCarryMl.setEnabled(!b);
	}

	private void newButtonEvent() 
	{
		txtCasual.focus();
		componentIni(false);
		btnIni(false);
		txtClear();
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();	
	}

	private void focusEnter()
	{
		allComp.add(txtCasual);
		allComp.add(txtSick);
		allComp.add(txtAnnual);
		allComp.add(txtMaternal);
		allComp.add(txtCarryCl);
		allComp.add(txtCarrySl);
		allComp.add(txtCarryAl);
		allComp.add(txtCarryMl);
		allComp.add(button.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void variousButtonAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				newButtonEvent();
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});


		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				updateAction();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				isUpdate = false;
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(txtCasual.getValue().toString().isEmpty())
				{
					showNotification("please Enter Casual Leave", Notification.TYPE_WARNING_MESSAGE);
					txtCasual.focus();
				}
				else if(txtSick.getValue().toString().isEmpty())
				{
					showNotification("please Enter Sick Leave", Notification.TYPE_WARNING_MESSAGE);
					txtSick.focus();
				}
				else if(txtAnnual.getValue().toString().isEmpty())
				{

					showNotification("please Enter Annual Leave", Notification.TYPE_WARNING_MESSAGE);
					txtAnnual.focus();

				}
				else if(txtMaternal.getValue().toString().isEmpty())
				{
					showNotification("please Enter Maternity Leave", Notification.TYPE_WARNING_MESSAGE);
					txtMaternal.focus();
				}
				else
				{
					saveButtonAction();
				}
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void txtClear()
	{
		dYear.setValue(new java.util.Date());
		txtCasual.setValue("");
		txtSick.setValue("");
		txtAnnual.setValue("");
		txtMaternal.setValue("");
		txtCarryCl.setValue("");
		txtCarrySl.setValue("");
		txtCarryAl.setValue("");
		txtCarryMl.setValue("");
	}

	private void updateAction() 
	{
		System.out.println("Update");
		if (!txtCasual.getValue().toString().isEmpty()) 
		{
			btnIni(false);
			componentIni(false);
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void findButtonEvent()
	{
		Window win=new LeavePolicyFind(sessionBean,  txreceiptId,"DeliveryOrder");
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(txreceiptId.getValue().toString().length()>0)
				{
					findInitialise(txreceiptId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private boolean chkLeaveBalance(String query)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("chkLeaveBalance", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void saveButtonAction()
	{
		if(!isUpdate)
		{
			String query = "select iClOpening,iCarryCl,iSlOpening,iCarrySl,iAlOpening," +
					"iCarryAl,iMlOpening,iCarryMl from tbLeaveBalanceNew " +
					"where year(currentYear)='"+dateformat.format(dYear.getValue())+"'";
			if(!chkLeaveBalance(query))
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save  information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					@SuppressWarnings("static-access")
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == buttonType.YES)
						{
							System.out.println("Hi3");
							dataSave();
						}
					}
				});
			}
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Update  information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				@SuppressWarnings("static-access")
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == buttonType.YES)
					{
						updateButtonEvent();
					}
				}
			});
		}
	}

	private void findInitialise( String txtid) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String selectQuery= "select currentYear,iClOpening,iCarryCl,iSlOpening,iCarrySl,iAlOpening,iCarryAl," +
					"iMlOpening,iCarryMl from tbLeaveBalanceNew where year(currentYear)='"+txtid+"' ";
			List <?> list = session.createSQLQuery(selectQuery).list();

			if(!list.isEmpty())
			{
				if (list.iterator().hasNext()) 
				{
					Object[] element = (Object[]) list.iterator().next();
					dYear.setValue(element[0]);
					txtCasual.setValue(element[1]);
					txtCarryCl.setValue(element[2]);
					txtSick.setValue(element[3]);
					txtCarrySl.setValue(element[4]);
					txtAnnual.setValue(element[5]);
					txtCarryAl.setValue(element[6]);
					txtMaternal.setValue(element[7]);
					txtCarryMl.setValue(element[8]);
				}
			}
			else{
				this.getParent().showNotification("There is no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void dataSave()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			year = dateformat.format(dYear.getValue());
			String checkPreviousEntry = " SELECT * from tbLeaveBalanceNew where YEAR(currentYear) < '"+year+"'";
			List <?> checkList = session.createSQLQuery(checkPreviousEntry).list();
			if(!checkList.isEmpty())
			{
				session.createSQLQuery("update tbLeaveBalanceNew set iFlag = '0' where YEAR(currentYear) < '"+year+"'").executeUpdate();
			}

			String query = " SELECT vEmployeeId,employeeCode,vProximityId,vSectionId,vDepartmentID from tbEmployeeInfo " +
					" where iStatus=1 and ISNULL(vProximityID,'')!='' and vEmployeeType!='Casual' order by vEmployeeId ";
			List <?> list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{	
					String empId = "";
					String employeeId = "";
					String ProxId = "";
					String sectionId = "";
					String departmentID = "";
					Object[] element = (Object[]) iter.next();
					empId = element[0].toString();
					employeeId = element[1].toString();
					ProxId = element[2].toString();
					sectionId = element[3].toString();
					departmentID = element[4].toString();

					String sql = " INSERT into tbLeaveBalanceNew values(" +
							" '"+year+"','"+empId+"','"+employeeId+"','"+ProxId+"','"+sectionId+"'," +
							" '0','0','0','0', " +
							" '"+txtCasual.getValue().toString()+"', '"+txtCarryCl.getValue().toString()+"'," +
							" '"+txtSick.getValue().toString()+"', '"+txtCarrySl.getValue().toString()+"'," +
							" '"+txtAnnual.getValue().toString()+"', '"+txtCarryAl.getValue().toString()+"'," +
							" '"+txtMaternal.getValue().toString()+"', '"+txtCarryMl.getValue().toString()+"'," +
							" '0','0','0','0','0','0','0','0','1','0', " +
							" '"+sessionBean.getUserName()+"',current_timestamp,'"+sessionBean.getUserIp()+"'," +
							" '"+departmentID+"')";

					session.createSQLQuery(sql).executeUpdate();
				}
			}
			else
			{
				getParent().showNotification("There are No Employee in This Company",Notification.TYPE_WARNING_MESSAGE);
			}
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			componentIni(true);
			btnIni(true);
			isUpdate=false;
			txtClear();
		}
		catch(Exception ex)
		{
			tx.rollback();
			this.getParent().showNotification("dataSave", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void updateButtonEvent()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			year = dateformat.format(dYear.getValue());
			String sql = "update tbLeaveBalanceNew set" +
					" iClOpening = '"+txtCasual.getValue().toString()+"'," +
					" iCarryCl = '"+txtCarryCl.getValue().toString()+"'," +
					" iSlOpening = '"+txtSick.getValue().toString()+"'," +
					" iCarrySl = '"+txtCarrySl.getValue().toString()+"'," +
					" iAlOpening = '"+txtAnnual.getValue().toString()+"'," +
					" iCarryAl = '"+txtCarryAl.getValue().toString()+"'," +
					" iMlOpening = '"+txtMaternal.getValue().toString()+"', " +
					" iCarryMl = '"+txtCarryMl.getValue().toString()+"', " +
					" userName = '"+sessionBean.getUserName()+"', " +
					" entryTime = current_timestamp, " +
					" userIp = '"+sessionBean.getUserIp()+"' " +
					" where year(currentYear)='"+year+"' ";
			session.createSQLQuery(sql).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All Information Saved Successfully");	
			txtClear();
			isUpdate=false;
			btnIni(true);
			componentIni(true);
		}
		catch(Exception ex)
		{
			tx.rollback();
			this.getParent().showNotification("updateButtonEvent", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("560px");
		setHeight("280px");

		lblYear = new Label();
		lblYear.setImmediate(true);
		lblYear.setWidth("-1px");
		lblYear.setHeight("-1px");
		lblYear.setValue("Opening Year :");
		mainLayout.addComponent(lblYear, "top:20.0px;left:45.0px;");

		dYear = new InlineDateField();
		dYear.setValue(new java.util.Date());
		dYear.setWidth("110px");
		dYear.setHeight("24px");
		dYear.setResolution(InlineDateField.RESOLUTION_YEAR);
		dYear.setDateFormat("yyyy");
		dYear.setInvalidAllowed(false);
		dYear.setImmediate(true);
		mainLayout.addComponent(dYear, "top:18.0px;left:150.0px;");

		lblCasualLeave = new Label();
		lblCasualLeave.setImmediate(true);
		lblCasualLeave.setWidth("-1px");
		lblCasualLeave.setHeight("-1px");
		lblCasualLeave.setValue("Casual Leave :");
		mainLayout.addComponent(lblCasualLeave, "top:68.0px;left:45.0px;");

		txtCasual = new AmountField();
		txtCasual.setImmediate(true);
		txtCasual.setWidth("60px");
		txtCasual.setHeight("24px");
		mainLayout.addComponent(txtCasual, "top:66.0px;left:150.0px;");

		lblSickLeave = new Label();
		lblSickLeave.setImmediate(true);
		lblSickLeave.setWidth("-1px");
		lblSickLeave.setHeight("-1px");
		lblSickLeave.setValue("Sick Leave :");
		mainLayout.addComponent(lblSickLeave, "top:94.0px;left:45.0px;");

		txtSick = new AmountField();
		txtSick.setImmediate(true);
		txtSick.setWidth("60px");
		txtSick.setHeight("24px");
		mainLayout.addComponent(txtSick, "top:92.0px;left:150.0px;");

		lblAnnualLeave = new Label();
		lblAnnualLeave.setImmediate(true);
		lblAnnualLeave.setWidth("-1px");
		lblAnnualLeave.setHeight("-1px");
		lblAnnualLeave.setValue("Earned Leave :");
		mainLayout.addComponent(lblAnnualLeave, "top:119.0px;left:45.0px;");

		txtAnnual = new AmountField();
		txtAnnual.setImmediate(true);
		txtAnnual.setWidth("60px");
		txtAnnual.setHeight("24px");
		mainLayout.addComponent(txtAnnual, "top:118.0px;left:150.0px;");

		lblMaternityLeave = new Label();
		lblMaternityLeave.setImmediate(true);
		lblMaternityLeave.setWidth("-1px");
		lblMaternityLeave.setHeight("-1px");
		lblMaternityLeave.setValue("Maternity Leave :");
		mainLayout.addComponent(lblMaternityLeave, "top:146.0px;left:45.0px;");

		txtMaternal = new AmountField();
		txtMaternal.setImmediate(true);
		txtMaternal.setWidth("60px");
		txtMaternal.setHeight("24px");
		mainLayout.addComponent(txtMaternal, "top:144.0px;left:150.0px;");

		lblCarryForWordCassual = new Label();
		lblCarryForWordCassual.setImmediate(true);
		lblCarryForWordCassual.setWidth("-1px");
		lblCarryForWordCassual.setHeight("-1px");
		lblCarryForWordCassual.setValue("Balance Carry Forward Upto :");
		mainLayout.addComponent(lblCarryForWordCassual, "top:68.0px;left:250.0px;");

		txtCarryCl = new AmountField();
		txtCarryCl.setImmediate(true);
		txtCarryCl.setWidth("60px");
		txtCarryCl.setHeight("24px");
		mainLayout.addComponent(txtCarryCl, "top:66.0px;left:420.0px;");

		lblCarryForWordSick = new Label();
		lblCarryForWordSick.setImmediate(true);
		lblCarryForWordSick.setWidth("-1px");
		lblCarryForWordSick.setHeight("-1px");
		lblCarryForWordSick.setValue("Balance Carry Forward Upto :");
		mainLayout.addComponent(lblCarryForWordSick, "top:94.0px;left:250.0px;");

		txtCarrySl = new AmountField();
		txtCarrySl.setImmediate(true);
		txtCarrySl.setWidth("60px");
		txtCarrySl.setHeight("24px");
		mainLayout.addComponent(txtCarrySl, "top:92.0px;left:420.0px;");

		lblCarryForWordAnnual = new Label();
		lblCarryForWordAnnual.setImmediate(true);
		lblCarryForWordAnnual.setWidth("-1px");
		lblCarryForWordAnnual.setHeight("-1px");
		lblCarryForWordAnnual.setValue("Balance Carry Forward Upto :");
		mainLayout.addComponent(lblCarryForWordAnnual, "top:119.0px;left:250.0px;");

		txtCarryAl = new AmountField();
		txtCarryAl.setImmediate(true);
		txtCarryAl.setWidth("60px");
		txtCarryAl.setHeight("24px");
		mainLayout.addComponent(txtCarryAl, "top:118.0px;left:420.0px;");

		lblCarryForWordMaternity = new Label();
		lblCarryForWordMaternity.setImmediate(true);
		lblCarryForWordMaternity.setWidth("-1px");
		lblCarryForWordMaternity.setHeight("-1px");
		lblCarryForWordMaternity.setValue("Balance Carry Forward Upto :");
		mainLayout.addComponent(lblCarryForWordMaternity, "top:146.0px;left:250.0px;");

		txtCarryMl = new AmountField();
		txtCarryMl.setImmediate(true);
		txtCarryMl.setWidth("60px");
		txtCarryMl.setHeight("24px");
		mainLayout.addComponent(txtCarryMl, "top:144.0px;left:420.0px;");
		mainLayout.addComponent(button, "top:190.0px;left:20.0px;");
		return mainLayout;
	}
}
