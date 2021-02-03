package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptUDEmployeeAttendanceDoubleShift extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblFromDate;
	private Label lblEmpID;
	private ComboBox cmbEmpID;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private PopupDateField dFrom;
	private PopupDateField dTo;
	private CheckBox chkallemp;
	private CheckBox chkallsection;
	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptUDEmployeeAttendanceDoubleShift(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EDIT EMPLOYEE ATTENDANCE (DOUBLE SHIFT) :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
		setEventAction();
		focusMove();
	}

	public void cmbDepartmentAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbUDEmployeeAttendanceDoubleShift " +
					"where dDate between '"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
					"order by vDepartmentName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("cmbDepartmentAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbSectionAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbUDEmployeeAttendanceDoubleShift " +
					"where dDate between '"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
					"and vDepartmentID = '"+cmbDepartment.getValue()+"' order by vSectionName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void EmployeeDataAdd(String Section)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vEmployeeId,vEmployeeCode from tbUDEmployeeAttendanceDoubleShift where dDate between " +
					"'"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
					"and vDepartmentID = '"+cmbDepartment.getValue()+"' and vSectionID like '"+Section+"' " +
					"order by vEmployeeCode";
			lblEmpID.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select vEmployeeId,vEmployeeName from tbUDEmployeeAttendanceDoubleShift where dDate between " +
						"'"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
						"and vDepartmentID = '"+cmbDepartment.getValue()+"' and vSectionID like '"+Section+"' " +
						"order by vEmployeeCode";
				lblEmpID.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "select vEmployeeId,vProximityID from tbUDEmployeeAttendanceDoubleShift where dDate between " +
						"'"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
						"and vDepartmentID = '"+cmbDepartment.getValue()+"' and vSectionID like '"+Section+"' " +
						"order by vEmployeeCode";
				lblEmpID.setValue("Proximity ID :");
			}

			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbEmpID.addItem(element[0]);
					cmbEmpID.setItemCaption(element[0], element[1].toString());
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

	public void setEventAction()
	{
		dFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dFrom.getValue()!=null)
					cmbDepartmentAddData();
			}
		});

		dTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dTo.getValue()!=null)
					cmbDepartmentAddData();
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
					cmbSectionAddData();
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpID.removeAllItems();
				if(cmbSection.getValue()!=null)
					EmployeeDataAdd(cmbSection.getValue().toString());
			}
		});

		chkallsection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpID.removeAllItems();
				if(chkallsection.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					EmployeeDataAdd("%");
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		chkallemp.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkallemp.booleanValue())
				{
					cmbEmpID.setValue(null);
					cmbEmpID.setEnabled(false);
				}
				else
					cmbEmpID.setEnabled(true);
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dFrom.getValue()!=null && dTo.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						if(cmbSection.getValue()!=null || chkallsection.booleanValue())
						{
							if(cmbEmpID.getValue()!=null || chkallemp.booleanValue())
								reportShow();
						}
						else
						{
							showNotification("Select Section Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Select Department Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Date",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpID.removeAllItems();
				EmployeeDataAdd(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%");
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String section="%";
		String employee="%";
		try
		{
			if(cmbSection.getValue()!=null)
				section=cmbSection.getValue().toString();

			if(cmbEmpID.getValue()!=null)
				employee=cmbEmpID.getValue().toString();

			String query="select vEmployeeCode,vProximityId,vEmployeeName,vDesignation,vDepartmentID,vDepartmentName,vSectionId," +
					"vSectionName,dAttDate,dAttInTime,dAttOutTime,dAttInTimeMax,dAttOutTimeMax,permittedBy,vReason,udFlag," +
					"vUserId,vUserIP,dEntryTime from tbUDEmployeeAttendanceDoubleShift where vDepartmentID='"+cmbDepartment.getValue()+"' " +
					"and vSectionId like '"+section+"' and vEmployeeId like '"+employee+"' and dAttDate between " +
					"'"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' order by vSectionId," +
					"vEmployeeCode,dAttDate";

			System.out.println("Query"+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
				hm.put("fromDate",dFrom.getValue());
				hm.put("toDate",dTo.getValue());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEditAttendanceDoubleShift.jasper",
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
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
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
		allComp.add(dFrom);
		allComp.add(dTo);
		allComp.add(cmbSection);
		allComp.add(cmbEmpID);
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
		setWidth("475px");
		setHeight("300px");

		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:10.0px; left:30.0px;");

		// dFrom
		dFrom = new PopupDateField();
		dFrom.setImmediate(true);
		dFrom.setWidth("110px");
		dFrom.setHeight("-1px");
		dFrom.setDateFormat("dd-MM-yyyy");
		dFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dFrom.setValue(new java.util.Date());
		mainLayout.addComponent(dFrom, "top:08.0px; left:140.0px;");

		dTo=new PopupDateField();
		dTo.setImmediate(true);
		dTo.setWidth("110px");
		dTo.setHeight("-1px");
		dTo.setDateFormat("dd-MM-yyyy");
		dTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dTo.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("To Date :"), "top:40.0px; left:30.0px;");
		mainLayout.addComponent(dTo, "top:38.0px; left:140.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:70.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68.0px; left:140.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:100.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:98.0px; left:140.0px;");

		chkallsection=new CheckBox("All");
		chkallsection.setImmediate(true);
		mainLayout.addComponent(chkallsection, "top:100.0px;left:415.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:130.0px; left:50.0px;");

		//lblEmpID
		lblEmpID=new Label("Employee ID : ");
		mainLayout.addComponent(lblEmpID, "top:160.0px;left:30.0px;");

		//cmbEmpID
		cmbEmpID=new ComboBox();
		cmbEmpID.setImmediate(true);
		cmbEmpID.setWidth("260px");
		cmbEmpID.setHeight("-1px");
		cmbEmpID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmpID, "top:158.0px;left:140.0px;");

		chkallemp=new CheckBox("All");
		chkallemp.setImmediate(true);
		mainLayout.addComponent(chkallemp, "top:160.0px;left:415.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:190.0px;left:140.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________"), "top:205.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:230.opx; left:160.0px");
		return mainLayout;
	}
}