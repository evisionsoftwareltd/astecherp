package acc.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionFactoryUtil;
import com.common.share.SessionBean;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class TourApplicationForm extends Window
{
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private OptionGroup opgTypeOfSearch;
	private List<String> lst=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	private Label lblApplicationDate;
	private PopupDateField dApplicationDate;

	private Label lblDepartment;
	private ComboBox cmbDepartment;

	private Label lblSection;
	private ComboBox cmbSection;

	private Label lblEmployeeName;
	private ComboBox cmbEmployeeName;

	private Label lblEmployeeId;
	private TextRead txtEmployeeId;

	private Label lblId;
	private TextRead txtId;

	private Label lblDesignation;
	private TextRead txtDesignation;

	private Label lblTotalDays;
	private TextRead txtTotalDays;

	private Label lblPurposeOfTour;
	private TextField txtPurposeOfTour;

	private Label lblTourAddress;
	private TextField txtTourAddress;

	private Label lblMobileNo;
	private TextField txtMobileNo;

	private Label lblTourFrom;
	private PopupDateField dTourFrom;

	private Label lblTourTo;
	private PopupDateField dTourTo;

	private Label lblJoiningDate;
	private PopupDateField dJoiningDate;

	private ArrayList<Component> allComp = new ArrayList<Component>(); 
	private TextField txtTransactionID=new TextField();
	
	private CheckBox chkApproved;
	private CheckBox chkCancel;

	String sectionId = "";
	String designationId = "";
	String leaveType = "0";
	String totalDays = "";
	String CBalance = "";
	String SBalance = "";
	String ABalance = "";
	String MBalance = "";
	String CEnjoy = "";
	String SEnjoy = "";
	String AEnjoy = "";
	String MEnjoy = "";
	String findEmployeeId = "";
	String typeOfSearch="";
	String employee = "";
	String Notify = "";

	private boolean Find=false;
	private boolean Update=false;

	private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dbMonthFormat = new SimpleDateFormat("MM");
	private SimpleDateFormat dbYearFormat = new SimpleDateFormat("yyyy");

	public TourApplicationForm(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("TOUR APPLICATION FORM::"+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		setEventAction();
		cButton.btnNew.focus();
		focusEnter();
		cmbDepartmentValueAdd();
	}
	private void setEventAction() 
	{
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionValueAdd();
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				employee = "";
				cmbEmployeeName.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					addemployeeName();
				}
			}
		});

		opgTypeOfSearch.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				employeeTextClear();
				if(cmbEmployeeName.getValue()!=null)
					employee = cmbEmployeeName.getValue().toString();
				cmbEmployeeName.removeAllItems();
				addemployeeName();
				setEmployeeInfo();
			}
		});

		cmbEmployeeName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				employeeTextClear();
				if(cmbEmployeeName.getValue()!=null)
				{
					employee = cmbEmployeeName.getValue().toString();
					setEmployeeInfo();
				}
			}
		});

		dTourFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dTourFrom.getValue()!=null && dTourTo.getValue()!=null)
				{
					tourCalculation();
				}
			}
		});

		dTourTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dTourFrom.getValue()!=null && dTourTo.getValue()!=null)
				{
					tourCalculation();
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				Find = false;
				Update = false;
				chkApproved.setValue(true);
				txtClear();
				componentIni(false);
				btnIni(false);
				dApplicationDate.focus();
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(checkForm())
				{
					Update = true;
					componentIni(false);
					btnIni(false);
				}
				else
					showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				Find = false;
				Update = false;
				txtClear();
				componentIni(true);
				btnIni(true);
			}
		});

		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				Find = true;
				findbuttonEvent();
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
		
		chkApproved.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkApproved.booleanValue())
				{
					chkCancel.setValue(false);
				}
				else
				{
					chkCancel.setValue(true);
				}
			}
		});
		
		chkCancel.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkCancel.booleanValue())
				{
					chkApproved.setValue(false);
				}
				else
				{
					chkApproved.setValue(true);
				}
			}
		});
	}

	private void cmbDepartmentValueAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ei.vDepartmentId,dept.vDepartmentName from tbEmployeeInfo ei " +
					"inner join tbDepartmentInfo dept on ei.vDepartmentId=dept.vDepartmentId WHERE dept.vDepartmentName!='CHO' order by dept.vDepartmentName";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectionValueAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ei.vSectionId,si.SectionName from tbEmployeeInfo ei " +
					"inner join tbSectionInfo si on ei.vSectionId=si.vSectionId where " +
					"ei.vDepartmentID='"+cmbDepartment.getValue()+"' order by si.SectionName";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addemployeeName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "SELECT distinct vEmployeeId,employeeCode from tbEmployeeInfo " +
					"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
					"vSectionId='"+cmbSection.getValue().toString().trim()+"' and " +
					"iStatus = 1 order by employeeCode";

			lblEmployeeName.setValue("Employee ID :");
			cmbEmployeeName.setWidth("140.0px");
			lblEmployeeId.setValue("Employee Name :");
			txtEmployeeId.setWidth("250.0px");
			lblId.setValue("Proximity ID :");
			txtId.setWidth("140.0px");

			if(opgTypeOfSearch.getValue()=="Employee Name")
			{
				query = "SELECT distinct vEmployeeId,vEmployeeName,employeeCode from tbEmployeeInfo " +
						"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
						"vSectionId='"+cmbSection.getValue().toString().trim()+"' and " +
						"iStatus = 1 order by employeeCode";
				lblEmployeeName.setValue("Employee Name :");
				cmbEmployeeName.setWidth("250.0px");
				lblEmployeeId.setValue("Employee ID :");
				txtEmployeeId.setWidth("140.0px");
				lblId.setValue("Proximity ID :");
				txtId.setWidth("140.0px");
			}

			else if(opgTypeOfSearch.getValue()=="Proximity ID")
			{
				query = "SELECT distinct vEmployeeId,vProximityID,employeeCode from tbEmployeeInfo " +
						"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
						"vSectionId='"+cmbSection.getValue().toString().trim()+"' and " +
						"iStatus = 1 order by employeeCode";
				lblEmployeeName.setValue("Proximity ID :");
				cmbEmployeeName.setWidth("140.0px");
				lblEmployeeId.setValue("Employee ID :");
				txtEmployeeId.setWidth("140.0px");
				lblId.setValue("Employee Name :");
				txtId.setWidth("250.0px");
			}

			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addemployeeName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEmployeeInfo()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "Select vEmployeeId,employeeCode,vEmployeeName,vProximityID,din.designationName from tbEmployeeInfo ein inner join tbDesignationInfo din on ein.vDesignationID = din.designationID " +
					"where vEmployeeId='"+employee+"' and iStatus = 1 order by employeeCode";
			if(opgTypeOfSearch.getValue()=="Proximity ID")
				query = "Select vEmployeeId,vProximityID,employeeCode,vEmployeeName,din.designationName from tbEmployeeInfo ein inner join tbDesignationInfo din on ein.vDesignationID = din.designationID " +
						"where vEmployeeId='"+employee+"' and iStatus = 1 order by employeeCode";
			else if(opgTypeOfSearch.getValue()=="Employee Name")
				query = "Select vEmployeeId,vEmployeeName,employeeCode,vProximityID,din.designationName from tbEmployeeInfo ein inner join tbDesignationInfo din on ein.vDesignationID = din.designationID " +
						"where vEmployeeId='"+employee+"' and iStatus = 1 order by employeeCode";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[])itr.next();
					cmbEmployeeName.setValue(element[0]);
					txtEmployeeId.setValue(element[2]);
					txtId.setValue(element[3]);
					txtDesignation.setValue(element[4]);
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("Warning",exp.toString(),Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void tourCalculation()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst = session.createSQLQuery("select dTourDate from tbTempTourApplication where vEmployeeID='"+cmbEmployeeName.getValue()+"'" +
					" and dTourDate between '"+dbDateFormat.format(dTourFrom.getValue())+"' and " +
					"'"+dbDateFormat.format(dTourTo.getValue())+"'").list();
			if(!lst.isEmpty() && !Find)
			{
				dTourTo.setValue(null);
				dTourFrom.setValue(new Date());
				txtTotalDays.setValue("");
				showNotification("Warning", "Data Already Exists!!!", Notification.TYPE_WARNING_MESSAGE);
			}
			else
			{
				String query = "select DATEDIFF(dd,'"+dbDateFormat.format(dTourFrom.getValue())+"','"+dbDateFormat.format(dTourTo.getValue())+"')";
				int leaveDuration = Integer.parseInt(session.createSQLQuery(query).list().iterator().next().toString());
				if(leaveDuration>=0)
				{
					txtTotalDays.setValue(Integer.toString(leaveDuration+1));
				}
				else
				{
					dTourTo.setValue(null);
					dTourFrom.setValue(new Date());
					txtTotalDays.setValue("");
					showNotification("Warning", "Provide Tour Days!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("tourCalculation", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(checkForm())
		{
			saveButtonEvent();
		}
		else
			showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
	}

	private boolean checkForm()
	{
		if(cmbEmployeeName.getValue()!=null)
		{
			if(!txtTotalDays.getValue().toString().trim().isEmpty())
			{
				if(!txtPurposeOfTour.getValue().toString().trim().isEmpty())
				{
					if(!txtTourAddress.getValue().toString().trim().isEmpty())
					{
						if(!txtMobileNo.getValue().toString().trim().isEmpty())
						{
							return true;
						}
						else
						{
							txtMobileNo.focus();
							Notify = "Provide Mobile Address!!!";
						}
					}
					else
					{
						txtTourAddress.focus();
						Notify = "Provide Visiting Address!!!";
					}
				}
				else
				{
					txtPurposeOfTour.focus();
					Notify = "Provide Purpose of Tour!!!";
				}
			}
			else
			{
				Notify = "Provide Tour Days!!!";
			}
		}
		else
		{
			cmbEmployeeName.focus();
			Notify = "Please Select "+lblEmployeeName.getValue()+"!!!";
		}
		return false;
	}

	private boolean chkSalary(String query)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				return true;
			}
		}
		catch (Exception exp)
		{
			showNotification("chkSalary", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void saveButtonEvent()
	{
		String query = "select * from tbSalary where autoEmployeeID='"+cmbEmployeeName.getValue()+"' and " +
				"((MONTH(dDate) = '"+dbMonthFormat.format(dTourFrom.getValue())+"' and " +
				"YEAR(dDate) = '"+dbYearFormat.format(dTourFrom.getValue())+"') or " +
				"(MONTH(dDate) = '"+dbMonthFormat.format(dTourTo.getValue())+"' and " +
				"YEAR(dDate) = '"+dbYearFormat.format(dTourTo.getValue())+"'))";
		if(!chkSalary(query))
		{
			if(Update)
			{
				MessageBox mb = new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,"Do You Want to Save All Information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						updateData();
					}
				});
			}
			else
			{
				MessageBox mb = new MessageBox(getParent(),"Are You Sure?",MessageBox.Icon.QUESTION,"Do You Want to Save All Information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						insertData();
					}
				});
			}
		}
		else
		{
			showNotification("Warning", "Salary Already Generated for this Month!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		Find = false;
		Update = false;
	}

	private String transactionIDGenerate()
	{
		String transactionID = "TRA-";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select ISNULL(MAX(CAST(SUBSTRING(vTransactionID,5,LEN(vTransactionID)) as int)),0)+1 from tbTourApplication";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				transactionID += lst.iterator().next().toString();
			}
		}
		catch (Exception exp)
		{
			showNotification("transactionIDGenerate", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return transactionID;
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String transactionID = transactionIDGenerate();
			String employeeCode = cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue());
			String proximity = txtId.getValue().toString();
			String employeeName = txtEmployeeId.getValue().toString();

			if(opgTypeOfSearch.getValue()=="Proximity ID")
			{
				proximity = cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue());
				employeeCode = txtEmployeeId.getValue().toString();
				employeeName = txtId.getValue().toString();
			}
			else if(opgTypeOfSearch.getValue()=="Employee Name")
			{
				employeeName = cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue());
				proximity = txtId.getValue().toString();
				employeeCode = txtEmployeeId.getValue().toString();
			}

			String approved = "1";
			if(chkCancel.booleanValue())
				approved = "0";
			
			String query = "insert into tbTourApplication (vTransactionID,dApplicationDate,vEmployeeID,vEmployeeCode," +
					"vProximityID,vEmployeeName,vDesignationID,vDesignationName,vDepartmentID," +
					"vDepartmentName,vSectionId,vSectionName,dJoiningDate,vMobileNo,dTourFrom,dTourTo,iTotalDays," +
					"vApprovedFlag,vPurposeOfLeave,vVisitingAddress,vUserName,vUserIP,dEntryTime) values " +
					"('"+transactionID+"'," +
					"'"+dbDateFormat.format(dApplicationDate.getValue())+"'," +
					"'"+cmbEmployeeName.getValue()+"'," +
					"'"+employeeCode+"'," +
					"'"+proximity+"'," +
					"'"+employeeName+"'," +
					"(select vDesignationID from tbEmployeeInfo where vEmployeeID = '"+cmbEmployeeName.getValue()+"')," +
					"'"+txtDesignation.getValue()+"'," +
					"'"+cmbDepartment.getValue()+"'," +
					"'"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"'," +
					"'"+cmbSection.getValue()+"'," +
					"'"+cmbSection.getItemCaption(cmbSection.getValue())+"'," +
					"'"+dbDateFormat.format(dJoiningDate.getValue())+"'," +
					"'"+txtMobileNo.getValue().toString()+"'," +
					"'"+dbDateFormat.format(dTourFrom.getValue())+"'," +
					"'"+dbDateFormat.format(dTourTo.getValue())+"'," +
					"'"+txtTotalDays.getValue().toString().trim()+"'," +
					"'"+approved+"'," +
					"'"+txtPurposeOfTour.getValue().toString().trim()+"'," +
					"'"+txtTourAddress.getValue().toString().trim()+"'," +
					"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',GETDATE())";
			session.createSQLQuery(query).executeUpdate();
			if(chkApproved.booleanValue())
				executeProcess(transactionID,session);
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("insertData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally
		{
			session.close();
			txtClear();
			componentIni(true);
			btnIni(true);
		}
	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String transID = txtTransactionID.getValue().toString();

			String approved = "1";
			if(chkCancel.booleanValue())
				approved = "0";
			
			String query = "insert into tbUDTourApplication (vTransactionID,dApplicationDate,vEmployeeID,vEmployeeCode," +
					"vProximityID,vEmployeeName,vDesignationID,vDesignationName,vDepartmentID,vDepartmentName,vSectionId," +
					"vSectionName,dJoiningDate,vMobileNo,dTourFrom,dTourTo,iTotalDays,vApprovedFlag,vPurposeOfLeave,vVisitingAddress," +
					"UDFlag,vUserName,vUserIP,dEntryTime) select vTransactionID,dApplicationDate,vEmployeeID,vEmployeeCode," +
					"vProximityID,vEmployeeName,vDesignationID,vDesignationName,vDepartmentID,vDepartmentName,vSectionId," +
					"vSectionName,dJoiningDate,vMobileNo,dTourFrom,dTourTo,iTotalDays,vApprovedFlag,vPurposeOfLeave,vVisitingAddress," +
					"'UPDATE',vUserName,vUserIP,dEntryTime from tbTourApplication where vTransactionID = '"+transID+"'";
			session.createSQLQuery(query).executeUpdate();
			query = "delete from tbTempTourApplication where vTransactionID = '"+transID+"'";
			session.createSQLQuery(query).executeUpdate();
			query = "update tbTourApplication set dTourFrom = '"+dbDateFormat.format(dTourFrom.getValue())+"'," +
					"dTourTo = '"+dbDateFormat.format(dTourTo.getValue())+"',iTotalDays='"+txtTotalDays.getValue().toString().trim()+"'," +
					"vPurposeOfLeave = '"+txtPurposeOfTour.getValue().toString().trim()+"',vVisitingAddress='"+txtTourAddress.getValue().toString().trim()+"'," +
					"vApprovedFlag='"+approved+"',vUserName = '"+sessionBean.getUserName()+"',vUserIP = '"+sessionBean.getUserIp()+"'," +
					"dEntryTime=GETDATE() where vTransactionID = '"+transID+"'";
			System.out.println(query);
			session.createSQLQuery(query).executeUpdate();
			if(chkApproved.booleanValue())
				executeProcess(transID,session);
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("updateData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally
		{
			session.close();
			txtClear();
			componentIni(true);
			btnIni(true);
		}
	}

	private void executeProcess(String transID,Session session)
	{
		String query = "exec prcTourApplication '"+cmbEmployeeName.getValue()+"'," +
				"'"+dbDateFormat.format(dApplicationDate.getValue())+"'," +
				"'"+transID+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'";
		session.createSQLQuery(query).executeUpdate();
	}

	private void findbuttonEvent()
	{
		Window win = new TourApplicationFormFind(sessionBean, txtTransactionID);
		win.addListener(new CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(!txtTransactionID.getValue().toString().trim().isEmpty())
				{
					txtClear();
					findInitialize(txtTransactionID.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialize(String TransID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select dApplicationDate,vDepartmentID,vSectionId,vEmployeeID,dTourFrom," +
					"dTourTo,vPurposeOfLeave,vVisitingAddress,vMobileNo,vApprovedFlag from tbTourApplication where " +
					"vTransactionID = '"+TransID+"'";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object [])itr.next();
					dApplicationDate.setValue(element[0]);
					cmbDepartment.setValue(element[1]);
					cmbSection.setValue(element[2]);
					cmbEmployeeName.setValue(element[3]);
					dTourFrom.setValue(element[4]);
					dTourTo.setValue(element[5]);
					txtPurposeOfTour.setValue(element[6]);
					txtTourAddress.setValue(element[7]);
					txtMobileNo.setValue(element[8]);
					if(element[9].toString().equals("1"))
						chkApproved.setValue(true);
					else
						chkCancel.setValue(true);
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("findInitialize", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	public AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("780px");
		mainLayout.setHeight("310px");
		mainLayout.setMargin(false);

		// lblApplicationDate
		lblApplicationDate = new Label("Application Date : ");
		lblApplicationDate.setImmediate(false);
		lblApplicationDate.setWidth("-1px");
		lblApplicationDate.setHeight("-1px");
		mainLayout.addComponent(lblApplicationDate, "top:20.0px; left:30.0px;");

		// dApplicationDate
		dApplicationDate = new PopupDateField();
		dApplicationDate.setImmediate(false);
		dApplicationDate.setWidth("110px");
		dApplicationDate.setHeight("-1px");
		dApplicationDate.setValue(new Date());
		dApplicationDate.setDateFormat("dd-MM-yyyy");
		dApplicationDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dApplicationDate, "top:18.0px; left:140.0px;");

		// lblSection
		lblDepartment = new Label("Department Name :");
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("-1px");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment, "top:45.0px; left:30.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("200px");
		cmbDepartment.setHeight("22px");
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartment, "top:43.0px; left:140.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:70.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("22px");
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:68.0px; left:140.0px;");

		opgTypeOfSearch=new OptionGroup("",lst);
		opgTypeOfSearch.setImmediate(true);
		opgTypeOfSearch.setStyleName("horizontal");
		opgTypeOfSearch.select("Employee ID");
		mainLayout.addComponent(opgTypeOfSearch, "top:95.0px;left:25.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee ID :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName, "top:120.0px; left:30.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("140.0px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:118.0px; left:140.0px;");

		lblEmployeeId=new Label("Employee Name :");
		lblEmployeeId.setImmediate(false);
		lblEmployeeId.setWidth("-1px");
		lblEmployeeId.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeId, "top:145.0px;left:30.0px;");

		txtEmployeeId=new TextRead();
		txtEmployeeId.setImmediate(false);
		txtEmployeeId.setWidth("250px");
		txtEmployeeId.setHeight("22px");
		mainLayout.addComponent(txtEmployeeId, "top:143.0px;left:140.0px;");

		// lblId
		lblId = new Label("Proximity ID :");
		lblId.setImmediate(false);
		lblId.setWidth("-1px");
		lblId.setHeight("-1px");
		mainLayout.addComponent(lblId, "top:170.0px; left:30.0px;");

		// txtId
		txtId = new TextRead();
		txtId.setImmediate(true);
		txtId.setWidth("140px");
		txtId.setHeight("22px");
		mainLayout.addComponent(txtId, "top:168.0px; left:140.0px;");

		// lblDesignation
		lblDesignation = new Label("Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("-1px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:195.0px; left:30.0px;");

		// txtDesignation
		txtDesignation = new TextRead();
		txtDesignation.setImmediate(true);
		txtDesignation.setWidth("200px");
		txtDesignation.setHeight("22px");
		mainLayout.addComponent(txtDesignation, "top:193.0px; left:141.0px;");

		// lblJoiningDate
		lblJoiningDate = new Label("Joining Date : ");
		lblJoiningDate.setImmediate(false);
		lblJoiningDate.setWidth("-1px");
		lblJoiningDate.setHeight("-1px");
		mainLayout.addComponent(lblJoiningDate, "top:220.0px; left:30.0px;");

		// dJoiningDate
		dJoiningDate = new PopupDateField();
		dJoiningDate.setImmediate(true);
		dJoiningDate.setWidth("110px");
		dJoiningDate.setHeight("-1px");
		dJoiningDate.setValue(new Date());
		dJoiningDate.setDateFormat("dd-MM-yyyy");
		dJoiningDate.setEnabled(false);
		dJoiningDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dJoiningDate, "top:218.0px; left:140.0px;");

		// lblTourFrom
		lblTourFrom = new Label("From :");
		lblTourFrom.setImmediate(false);
		lblTourFrom.setWidth("-1px");
		lblTourFrom.setHeight("-1px");
		mainLayout.addComponent(lblTourFrom, "top:20.0px; left:430.0px;");

		// dTourFrom
		dTourFrom = new PopupDateField();
		dTourFrom.setImmediate(true);
		dTourFrom.setWidth("110px");
		dTourFrom.setHeight("-1px");
		dTourFrom.setValue(new Date());
		dTourFrom.setDateFormat("dd-MM-yyyy");
		dTourFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dTourFrom, "top:18.0px; left:530.0px;");

		// lblTourTo
		lblTourTo = new Label("To :");
		lblTourTo.setImmediate(false);
		lblTourTo.setWidth("-1px");
		lblTourTo.setHeight("-1px");
		mainLayout.addComponent(lblTourTo, "top:45.0px; left:430.0px;");

		// dTourTo
		dTourTo = new PopupDateField();
		dTourTo.setImmediate(true);
		dTourTo.setWidth("110px");
		dTourTo.setHeight("-1px");
		dTourTo.setDateFormat("dd-MM-yyyy");
		dTourTo.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dTourTo, "top:43.0px; left:530.0px;");

		lblTotalDays = new Label("Total Days:");
		lblTotalDays.setImmediate(false);
		lblTotalDays.setWidth("-1px");
		lblTotalDays.setHeight("-1px");
		mainLayout.addComponent(lblTotalDays, "top:70.0px; left:430.0px;");

		txtTotalDays = new TextRead();
		txtTotalDays.setImmediate(true);
		txtTotalDays.setWidth("200px");
		txtTotalDays.setHeight("22px");
		mainLayout.addComponent(txtTotalDays, "top:68.0px; left:530.0px;");


		// lblPurposeOfTour
		lblPurposeOfTour = new Label("Purpose :");
		lblPurposeOfTour.setImmediate(true);
		lblPurposeOfTour.setWidth("-1px");
		lblPurposeOfTour.setHeight("-1px");
		mainLayout.addComponent(lblPurposeOfTour, "top:95.0px; left:430.0px;");

		// txtPurposeOfTour
		txtPurposeOfTour = new TextField();
		txtPurposeOfTour.setImmediate(true);
		txtPurposeOfTour.setWidth("220px");
		txtPurposeOfTour.setHeight("60px");
		mainLayout.addComponent(txtPurposeOfTour, "top:93.0px; left:530.0px;");

		// lblTourAddress
		lblTourAddress = new Label("Visiting address :");
		lblTourAddress.setImmediate(false);
		lblTourAddress.setWidth("-1px");
		lblTourAddress.setHeight("-1px");
		mainLayout.addComponent(lblTourAddress, "top:155.0px; left:430.0px;");

		// txtTourAddress
		txtTourAddress = new TextField();
		txtTourAddress.setImmediate(true);
		txtTourAddress.setWidth("220px");
		txtTourAddress.setHeight("60px");
		mainLayout.addComponent(txtTourAddress, "top:153.0px; left:530.0px;");

		// lblMobileNo
		lblMobileNo = new Label("Mobile No :");
		lblMobileNo.setImmediate(false);
		lblMobileNo.setWidth("-1px");
		lblMobileNo.setHeight("-1px");
		mainLayout.addComponent(lblMobileNo, "top:215.0px; left:430.0px;");

		// txtMobileNo
		txtMobileNo = new TextField();
		txtMobileNo.setImmediate(true);
		txtMobileNo.setWidth("150px");
		txtMobileNo.setHeight("-1px");
		mainLayout.addComponent(txtMobileNo, "top:213.0px; left:530.0px;");
		
		chkApproved = new CheckBox("Approved");
		chkApproved.setImmediate(true);
		chkApproved.setValue(true);
		mainLayout.addComponent(chkApproved, "top:240.0px; left:430.0px;");
		
		chkCancel = new CheckBox("Cancel");
		chkCancel.setImmediate(true);
		mainLayout.addComponent(chkCancel, "top:240.0px; left:530.0px;");

		mainLayout.addComponent(cButton, "top:270.0px; left:135.0px;");
		return mainLayout;
	}

	private void btnIni(boolean t) 
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private void componentIni(boolean t)
	{
		dApplicationDate.setEnabled(!t);
		cmbDepartment.setEnabled(!t);
		cmbSection.setEnabled(!t);
		opgTypeOfSearch.setEnabled(!t);
		cmbEmployeeName.setEnabled(!t);
		txtEmployeeId.setEnabled(!t);
		txtId.setEnabled(!t);
		txtDesignation.setEnabled(!t);
		dTourFrom.setEnabled(!t);
		dTourTo.setEnabled(!t);
		txtTotalDays.setEnabled(!t);
		txtPurposeOfTour.setEnabled(!t);
		txtTourAddress.setEnabled(!t);
		txtMobileNo.setEnabled(!t);
		chkApproved.setEnabled(!t);
		chkCancel.setEnabled(!t);
	}

	private void employeeTextClear()
	{
		txtEmployeeId.setValue("");
		txtId.setValue("");
		txtDesignation.setValue("");
	}

	private void txtClear()
	{
		dApplicationDate.setValue(new Date());
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbEmployeeName.setValue(null);
		employeeTextClear();
		dJoiningDate.setValue(new Date());
		dTourTo.setValue(null);
		dTourFrom.setValue(new Date());
		txtPurposeOfTour.setValue("");
		txtTourAddress.setValue("");
		txtMobileNo.setValue("");
		txtTotalDays.setValue("");
	}

	private void focusEnter()
	{
		allComp.add(dApplicationDate);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeName);
		allComp.add(dTourFrom);
		allComp.add(dTourTo);
		allComp.add(txtPurposeOfTour);
		allComp.add(txtTourAddress);
		allComp.add(txtMobileNo);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}
}
