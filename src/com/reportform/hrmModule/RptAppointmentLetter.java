package com.reportform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptAppointmentLetter extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblEmployeeName;
	private Label lblAsOnDate;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;

	private ComboBox cmbEmployeeName;
	private PopupDateField asOnDate;
	
	ArrayList<Component> allComp = new ArrayList<Component>();

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});


	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	private OptionGroup RadioBtnGroup2;
	private static final List<String> type2=Arrays.asList(new String[]{"Application Form","Appointment Letter"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	public RptAppointmentLetter(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("APPLICATION & APPOINTMENT LETTER :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		addDepartmentName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				addSectionName();
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					EmployeeDataAdd(cmbSection.getValue().toString());
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!=null )
				{
					if(cmbSection.getValue()!=null )
					{
							if(cmbEmployeeName.getValue()!=null)
							{
								if(asOnDate.getValue()!=null)
								{
									getAllData();
								}
								else
								{
									showNotification("Warning","Select Date",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
							}
					}
					else
					{
						showNotification("Warning","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Department Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		RadioBtnStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					EmployeeDataAdd(cmbSection.getValue().toString());
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				cmbEmployeeName.removeAllItems();
				EmployeeDataAdd(cmbSection.getValue().toString());
			}
		});
	}

	public void addDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(" SELECT vDepartmentID,vDepartmentName from tbDepartmentInfo where vDepartmentName!= 'CHO' order by vDepartmentID ").list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addDepartmentName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addSectionName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(" SELECT vSectionId,vDepartmentName,SectionName from " +
					"tbSectionInfo where vDepartmentId='"+cmbDepartment.getValue().toString()+"' " +
					"order by vSectionId ").list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("addSectionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void EmployeeDataAdd(Object Section)
	{
		String status="%";
		if(RadioBtnStatus.getValue().equals("Active"))
		{
			status="1";
		}
		else if(RadioBtnStatus.getValue().equals("Left"))
		{
			status="0";
		}
		else
		{
			status="%";
		}
		cmbEmployeeName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vEmployeeId,employeeCode from tbEmployeeInfo where vSectionId like '"+Section+"' and iStatus like '"+status+"'";
			lblEmployeeName.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				query="select distinct vEmployeeId,vEmployeeName from tbEmployeeInfo where vSectionId like '"+Section+"' and iStatus like '"+status+"'";
				lblEmployeeName.setValue("Employee Name :");
			}

			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query="select vEmployeeId,vProximityID from tbEmployeeInfo  where vSectionId like '"+Section+"' and iStatus like '"+status+"'";
				lblEmployeeName.setValue("Proximity ID :");

			}

			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbEmployeeName.addItem(element[0]);
					cmbEmployeeName.setItemCaption(element[0], element[1].toString());
				}
			}
			else
				showNotification("Warning","No Employee Found!!!",Notification.TYPE_WARNING_MESSAGE);

		}
		catch (Exception exp)
		{
			showNotification("EmployeeDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void getAllData()
	{
		String sectionValue = "";

		if(cmbSection.getValue()!=null)
		{
			sectionValue = cmbSection.getValue().toString();
		}
		reportShow(sectionValue);
	}

	private void reportShow(Object Section)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;

		try
		{
			String ReportValue="";
			if(RadioBtnGroup2.getValue().equals("Appointment Letter"))
			{
				query = "select distinct ein.vEmployeeID,ein.dJoiningDate,din.designationName,ein.mOthersAllowance,sein.SectionName," +
						"ein.vEmployeeName,ein.employeeCode,ein.vProximityId," +
						"(case when ein.OtStatus = 1 then 'OT' else '' end) OTStatus from tbEmployeeInfo as ein" +
						" inner join tbDesignationInfo as din on ein.vDesignationId=din.designationId " +
						"inner join tbSectionInfo as  sein on sein.vSectionId=ein.vSectionId where ein.vEmployeeID = '"+cmbEmployeeName.getValue()+"' order" +
						" by ein.employeeCode";

				ReportValue="report/account/hrmModule/rptAppointmentLetter.jasper";
			}
			else
			{
				query = "select distinct ein.vFatherName,ein.dJoiningDate,ein.vMotherName,vPermanentAddress,vMailingAddress,ein.dDateOfBirth,ein.vNationality," +
						"vReligion,vBloodGroup,vContact,din.designationName,ein.mMonthlySalary,sein.SectionName,ein.vEmployeeName,ein.employeeCode," +
						"ein.vProximityId,(case when ein.OtStatus = 1 then 'OT' else '' end) OTStatus from tbEmployeeInfo as ein inner join tbDesignationInfo as din on ein.vDesignationId=din.designationId " +
						"inner join tbSectionInfo as sein on sein.vSectionId=ein.vSectionId where ein.vEmployeeID = '"+cmbEmployeeName.getValue()+"'  order" +
						" by ein.employeeCode";

				ReportValue="report/account/hrmModule/rptApplicationForm.jasper";
			}
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("dDate", asOnDate.getValue());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,ReportValue,
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		catch(Exception exp)
		{
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			Iterator <?> iter = session.createSQLQuery(sql).list().iterator();
			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}

	private void focusMove()
	{
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeName);
		allComp.add(asOnDate);
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("430px");
		setHeight("360px");

		lblAsOnDate = new Label("Date :");
		lblAsOnDate.setImmediate(true);
		lblAsOnDate.setWidth("100%");
		lblAsOnDate.setHeight("-1px");
		mainLayout.addComponent(lblAsOnDate, "top:30.0px; left:30.0px;");

		// asOnDate
		asOnDate = new PopupDateField();
		asOnDate.setWidth("110px");
		asOnDate.setDateFormat("dd-MM-yyyy");
		asOnDate.setValue(new java.util.Date());
		asOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(asOnDate, "top:28.0px; left:150.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("200px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:60.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:58.0px; left:150.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:90.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:88.0px; left:150.0px;");
		
		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:120.0px;left:50.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:150.0px; left:50.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee ID :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("100.0%");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName,"top:180.0px; left:30.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(false);
		cmbEmployeeName.setWidth("200px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbEmployeeName.setImmediate(true);
		mainLayout.addComponent(cmbEmployeeName, "top:178.0px; left:150.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:210.0px;left:150.0px;");

		RadioBtnGroup2 = new OptionGroup("",type2);
		RadioBtnGroup2.setImmediate(true);
		RadioBtnGroup2.setStyleName("horizontal");
		RadioBtnGroup2.setValue("Application Form");
		mainLayout.addComponent(RadioBtnGroup2, "top:240.0px;left:150.0px;");
		mainLayout.addComponent(cButton,"top:270.opx; left:130.0px");
		return mainLayout;
	}
}
