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
import com.common.share.GenerateExcelReport;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptAuditSectionWiseDailyAttendance extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblDepartment;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartmentAll;

	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox chkSectionAll;

	private Label lblDate;
	private PopupDateField dDate;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	SimpleDateFormat dfYMD = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dRptFormat = new SimpleDateFormat("dd-MM-yyyy");

	public RptAuditSectionWiseDailyAttendance(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("SECTION_WISE_DAILY_ATTENDANCE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		addDepartmentName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						String DepartmentID = "%";
						String SectionID = "%";
						if(cmbSection.getValue()!=null)
						{
							SectionID = cmbSection.getValue().toString();
						}

						if(cmbDepartment.getValue()!=null)
						{
							DepartmentID=cmbDepartment.getValue().toString();
						}

						reportShow(DepartmentID,SectionID);
					}
					else
					{
						showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
						cmbSection.focus();
					}
				}
				else
				{
					showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
					cmbSection.focus();
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

		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dDate.getValue()!=null)
					addDepartmentName();
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					addSectionName(cmbDepartment.getValue().toString());
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkDepartmentAll.booleanValue())
				{
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
					addSectionName("%");
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});

		chkSectionAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkSectionAll.booleanValue()==true)
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});
	}

	public void addDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmployeeMainAttendance " +
					"where dDate='"+dfYMD.format(dDate.getValue())+"' order by vDepartmentName";
			List <?> list = session.createSQLQuery(query).list();

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
		}finally
		{
			session.close();
		}
	}

	public void addSectionName(String  DepartmentID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vDepartmentName,vSectionName from tbEmployeeMainAttendance " +
					"where vDepartmentID like '"+DepartmentID+"' and dDate='"+dfYMD.format(dDate.getValue())+"' " +
					"order by vDepartmentName,vSectionName";
			List <?> list = session.createSQLQuery(query).list();

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
		finally
		{
			session.close();
		}
	}

	private void reportShow(String DepartmentID,String SectionId)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			query="select * from funAuditDailyEmployeeAttendance('"+dfYMD.format(dDate.getValue())+"','"+dfYMD.format(dDate.getValue())+"','%','"+DepartmentID+"','"+SectionId+"')" +
					" where ISNULL(dInTimeOne,'')!='' and ISNULL(dOutTimeOne,'')!='' and dTotalWorkingHour>='07:00:00' order by vDepartmentName,vSectionName,vEmployeeCode";

			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "EmployeeDailyAttendance_"+dRptFormat.format(dDate.getValue()).toString()+".xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					String strColName[]={"SL#","Employee ID","Proximity ID","Employee Name","Designation Name",
							"Shift Name","In Date","In Time","Out Date","Out Time","Total Hrs","OT Hrs"};

					String Header="Date : "+dRptFormat.format(dDate.getValue());

					String query1="SELECT distinct vDepartmentID,vDepartmentName,vSectionID,vSectionName" +
							" from tbEmployeeMainAttendance where vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' "
							+ "and vSectionID like '"+SectionId+"' and dDate='"+dfYMD.format(dDate.getValue())+"' order by vDepartmentName,vSectionName";

					List <?> lst1=session.createSQLQuery(query1).list();
					//System.out.println("lst1 : " + lst1);

					String detailQuery[]=new String[lst1.size()];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					String [] signatureOption = new String [0];
					int countInd=0;

					for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
					{
						Object [] element = (Object[]) itr1.next();
						groupItem[countInd]="Department Name : "+element[1].toString()+"Section Name : "+element[3].toString();
						GroupElement[countInd]=new Object [] {(Object)"",(Object)"Department Name : ",element[1],(Object)"Section Name : ",element[3]};

						detailQuery[countInd]="select vEmployeeCode,vProximityID,vEmployeeName,vDesignationName,vShiftName,"
								+ "ISNULL(CONVERT(varchar(20),CONVERT(date,dInTimeOne),105),'') inDate,"
								+ "ISNULL(subString(convert(varchar(50),CONVERT(time,dInTimeOne)),1,8),'') inTime,ISNULL(CONVERT(varchar(20),"
								+ "CONVERT(date,dOutTimeOne),105),'') outDate,ISNULL(subString(convert(varchar(50),"
								+ "CONVERT(time,dOutTimeOne)),1,8),'') outTime,subString(convert(varchar(50),dTotalWorkingHour),1,8) totalHrs,"
								+ "subString(Convert(varchar(50),dOtHour),1,8) OTHrs from"
								+ " funAuditDailyEmployeeAttendance('"+dfYMD.format(dDate.getValue())+"'," +
								"'"+dfYMD.format(dDate.getValue())+"','%','"+element[0].toString()+"','"+element[2].toString()+"') "
								+ "where ISNULL(dInTimeOne,'')!='' and ISNULL(dOutTimeOne,'')!='' and dTotalWorkingHour>='07:00:00'"
								+ "order by vDepartmentName,vSectionName,vEmployeeCode";
						countInd++;
					}

					new GenerateExcelReport(sessionBean, loc, url, fname, "Daily Employee Attendance", "SECTION WISE DAILY ATTENDANCE",
							Header, strColName, 2,groupItem, GroupColName, GroupElement, 2, detailQuery, 0, 0, query1,
							query1, signatureOption);

					Window window = new Window();
					getApplication().addWindow(window);
					getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);

				}
				else
				{

					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("date", dDate.getValue());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptAuditDailyAttendence.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

					win.setCaption("Project Report");
					this.getParent().getWindow().addWindow(win);
				}
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
		finally
		{
			session.close();
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
		finally
		{
			session.close();
		}
		return false;
	}

	private void focusMove()
	{
		allComp.add(cmbSection);
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
		setWidth("480px");
		setHeight("250px");

		// lblAsOnDate
		lblDate = new Label("Date :");
		lblDate.setImmediate(true);
		lblDate.setWidth("100%");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:30.0px;");

		// asOnDate
		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:18.0px; left:140.0px;");

		// lblCategory
		lblDepartment = new Label();
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		lblDepartment.setValue("Department Name :");
		mainLayout.addComponent(lblDepartment,"top:50.0px; left:30.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:48.0px; left:140.0px;");

		//chkSectionAll
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:50.0px; left:405.0px;");

		// lblCategory
		lblSection = new Label();
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Section Name :");
		mainLayout.addComponent(lblSection,"top:80.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:78.0px; left:140.0px;");

		//chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:80.0px; left:405.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:170.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:130.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:160.opx; left:160.0px");
		return mainLayout;
	}
}
