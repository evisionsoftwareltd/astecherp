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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
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
public class RptDateBetweenSectionWiseSpecialAbsent extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private ComboBox cmbSection;

	private Label lblDepartment;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartmentAll;

	private Label lblFromDate;

	private Label lblEmpID;
	private ComboBox cmbEmpID;

	private PopupDateField dFrom;
	private PopupDateField dTo;

	private CheckBox chkAllSec;
	private CheckBox chkallemp;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dRptFormat = new SimpleDateFormat("dd-MM-yy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private static final String CHO="'DEPT10'";
	public RptDateBetweenSectionWiseSpecialAbsent(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("SECTION WISE DATE BETWEEN SPECIAL ABSENT :: "+sessionBean.getCompany());
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
			String query="select distinct vDepartmentID,vDepartmentName from tbEmployeeMainAttendance " +
					"where dDate between '"+dFormat.format(dFrom.getValue())+"'  and '"+dFormat.format(dTo.getValue())+"' " +
					" and vDepartmentID!="+CHO+" order by vDepartmentName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbSectionAddData(String DepartmentID)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionID,vDepartmentName,vSectionName from tbEmployeeMainAttendance " +
					"where dDate between '"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
					"and vDepartmentID like '"+DepartmentID+"'  and vDepartmentID!="+CHO+" order by vDepartmentName,vSectionName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void EmployeeDataAdd(String DepartmentID,String SectionID)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vEmployeeId,employeeCode from tbEmployeeMainAttendance " +
					"where dDate between '"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
					"and vDepartmentID like '"+DepartmentID+"' and vSectionID like '"+SectionID+"'  and vDepartmentID!="+CHO+"  " +
					"order by vDepartmentName,vSectionName,employeeCode";
			lblEmpID.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select vEmployeeId,vEmployeeName from tbEmployeeMainAttendance " +
						"where dDate between '"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
						"and vDepartmentID like '"+DepartmentID+"' and vSectionID like '"+SectionID+"'  and vDepartmentID!="+CHO+"  " +
						"order by vDepartmentName,vSectionName,employeeCode";
				lblEmpID.setValue("Employee Name :");
			}

			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "select vEmployeeId,vProximityID from tbEmployeeMainAttendance " +
						"where dDate between '"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
						"and vDepartmentID like '"+DepartmentID+"' and vSectionID like '"+SectionID+"'  and vDepartmentID!="+CHO+"  " +
						"order by vDepartmentName,vSectionName,employeeCode";
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
				{
					cmbDepartmentAddData();
				}
			}
		});

		dTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dTo.getValue()!=null)
				{
					cmbDepartmentAddData();
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionAddData(cmbDepartment.getValue().toString());
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(chkDepartmentAll.booleanValue())
				{
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
					cmbSectionAddData("%");
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpID.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					String DepartmentID="%";
					if(cmbDepartment.getValue()!=null)
						DepartmentID=cmbDepartment.getValue().toString();
					EmployeeDataAdd(DepartmentID,cmbSection.getValue().toString());
				}
			}
		});

		chkAllSec.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpID.removeAllItems();
				chkallemp.setValue(false);

				if(chkAllSec.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);

					String DepartmentID="%";
					if(cmbDepartment.getValue()!=null)
						DepartmentID=cmbDepartment.getValue().toString();
					EmployeeDataAdd(DepartmentID,"%");
				}
				else
					cmbSection.setEnabled(true);
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
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkAllSec.booleanValue())
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
					showNotification("Select Month",Notification.TYPE_WARNING_MESSAGE);
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

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						cmbEmpID.removeAllItems();
						EmployeeDataAdd(cmbDepartment.getValue().toString(),cmbSection.getValue().toString());
					}
				}
			}
		});
	}

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String Department="%";
		String section="%";
		String employee="";

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{

			if(chkallemp.booleanValue())
				employee="%";
			else
				employee=cmbEmpID.getValue().toString();

			if(cmbSection.getValue()!=null)
				section=cmbSection.getValue().toString();

			if(cmbDepartment.getValue()!=null)
				Department=cmbDepartment.getValue().toString();

			query="select * from funDailyEmployeeAttendance('"+dFormat.format(dFrom.getValue())+"','"+dFormat.format(dTo.getValue())+"','"+employee+"','"+Department+"','"+section+"') " +
					" where ((ISNULL(dInTimeOne,'')='' or ISNULL(dOutTimeOne,'')='') or datepart(HH,dTotalWorkingHour)<7)  and vDepartmentID!="+CHO+"  and not(ISNULL(dInTimeOne,'')='' and ISNULL(dOutTimeOne,'')='') " +
					" order by vDepartmentName," +
					" vSectionName,vEmployeeCode";

			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "excel.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					Object GroupColName[]={(Object)"SL#",(Object)"Date",(Object)"Shift Name",(Object)"In Date",(Object)"In TIme",
							(Object)"Out Date",(Object)"Out Time",(Object)"Total Hrs",(Object)"OT Hrs"};

					String Header="Date : "+dRptFormat.format(dFrom.getValue())+" to "+dRptFormat.format(dTo.getValue());

					String query1="SELECT distinct EMA.employeeCode,EMA.vProximityID,EMA.vEmployeeName,ein.dJoiningDate,EMA.vDesignationName,EMA.vDepartmentID,EMA.vDepartmentName,EMA.vSectionID,EMA.vSectionName,EMA.vEmployeeID " +
							"from tbEmployeeMainAttendance EMA inner join tbEmployeeInfo ein on ein.vEmployeeID=EMA.vEmployeeId where dDate between '"+dFormat.format(dFrom.getValue())+"' and '"+dFormat.format(dTo.getValue())+"' " +
							"and EMA.vDepartmentID like '"+Department+"'  and ein.vDepartmentID!="+CHO+"  and EMA.vSectionID like '"+section+"' and EMA.vEmployeeID like '"+employee+"' and ((ISNULL(dInTimeOne,'')='' or ISNULL(dOutTimeOne,'')='') " +
							"or datediff(ss,dInTimeOne,dOutTimeOne)<25200) order by EMA.vDepartmentName,EMA.vSectionName";


					List <?> lst1=session.createSQLQuery(query1).list();
					//System.out.println("lst1 : " + lst1);

					String detailQuery[]=new String[lst1.size()];
					String [] strColName={"","","","","","","","",""};
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] signatureOption = new String [0];

					int countInd=0;
					for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
					{
						Object [] element = (Object[]) itr1.next();
						groupItem[countInd]="Employee ID : "+element[0].toString()+"    Proximity ID : "+element[1].toString()+"    " +
								"Employee Name : "+element[2].toString()+"    Joining Date : "+dRptFormat.format(element[3]).toString()+"    " +
								"Designation Name : "+element[4].toString()+"    Section Name : "+element[6].toString()+"    Department Name : "+element[8].toString();
						GroupElement[countInd]=new Object [] {(Object)"1"};

						detailQuery[countInd]="select CONVERT(date,Date) dDate,vShiftName,CONVERT(date,dInTimeOne) inDate," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,dInTimeTwo)),1,8)+', ','')+ISNULL(subString(convert(varchar(50),CONVERT(time,dInTimeOne)),1,8),'') inTime," +
								"ISNULL(CONVERT(varchar(20),CONVERT(date,dOutTimeTwo),105)+', ','')+ISNULL(CONVERT(varchar(20),CONVERT(date,dOutTimeOne),105),'') outDate," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,dOutTimeTwo)),1,8)++', ','')+ISNULL(subString(convert(varchar(50),CONVERT(time,dOutTimeOne)),1,8),'') outTime," +
								"subString(convert(varchar(50),dTotalWorkingHour),1,8) totalHrs,subString(Convert(varchar(50),dOtHour),1,8) OTHrs " +
								"from funDailyEmployeeAttendance('"+dFormat.format(dFrom.getValue())+"','"+dFormat.format(dTo.getValue())+"','"+element[9].toString()+"','"+element[5].toString()+"','"+element[7].toString()+"') " +
								"where ((ISNULL(dInTimeOne,'')='' or ISNULL(dOutTimeOne,'')='') or datepart(HH,dTotalWorkingHour)<7) and not(ISNULL(dInTimeOne,'')='' and ISNULL(dOutTimeOne,'')='')" +
								"   and vDepartmentID!="+CHO+"  order by vDepartmentName,vSectionName,vEmployeeCode";
						countInd++;
					}
					new GenerateExcelReport(sessionBean, loc, url, fname, "Date_Btn_Special_Absent", "DATE BETWEEN SPECIAL ABSENT REGISTER",
							Header, strColName,	5, groupItem, GroupColName, GroupElement, 2, detailQuery, 0, 0,"A4","Portrait",signatureOption);

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
					hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
					hm.put("fromDate",dRptFormat.format(dFrom.getValue()));
					hm.put("toDate",dRptFormat.format(dTo.getValue()));
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptDatebetweenSpecialAbsent.jasper",
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
		finally{session.close();}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
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

		// lblSection
		lblDepartment = new Label("Department Name :");
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment,"top:70.0px; left:30.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDepartment, "top:68.0px; left:140.0px;");

		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		//mainLayout.addComponent(chkDepartmentAll, "top:70.0px;left:415.0px;");

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

		chkAllSec=new CheckBox("All");
		chkAllSec.setImmediate(true);
		//mainLayout.addComponent(chkAllSec, "top:100.0px;left:415.0px;");

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