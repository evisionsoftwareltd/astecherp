package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
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
public class rptMonthlyFridayAllowance_CHO extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private Label lblSection;

	private PopupDateField dMonth;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private CheckBox chkSection;

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat yearMonthFormat = new SimpleDateFormat("MMMMM-yyyy");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	public rptMonthlyFridayAllowance_CHO(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY FRIDAY ALLOWANCE(CHO) :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
		setEventAction();
	}

	public void cmbDepartmentAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					"tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where dept.vDepartmentName = 'CHO' order by dept.vDepartmentName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
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
			String query="select distinct ein.vSectionId,sein.SectionName from tbSectionInfo sein inner join " +
					"tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where " +
					"ein.vDepartmentId='"+cmbDepartment.getValue()+"' order by sein.SectionName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionAddData();
				}
			}
		});

		chkSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkSection.booleanValue())
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

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null || chkSection.booleanValue())
					{
						reportShow();
					}
					else
					{
						showNotification("Warniung!","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warniung!","Select Department Name",Notification.TYPE_WARNING_MESSAGE);
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
	}

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String strSection="%";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(cmbSection.getValue()!=null)
				strSection=cmbSection.getValue().toString();

			String query=" select ts.vMonthName,ts.year,ts.autoEmployeeID,ts.empId,ts.empCode,ts.empName,ts.vDesignationID," +
					" ts.designation,ts.vDepartmentID,ts.vDepartmentName,ts.SectionID,ts.Section,ts.Gross,ts.FridayAllowance,ts.Friday,ts.iTotalNormalOTHour," +
					" ts.itotalFridayOTHour,iTotalFridayOTMin,ein.FridayLunchFee," +
					" (ein.FridayLunchFee*ts.FridayAllowance) as totalAmt,ts.fridayAmount from " +
					" tbSalary ts inner join tbEmployeeInfo ein on ts.autoEmployeeID=ein.vEmployeeId where " +
					" ein.FridayStatus=1 and ts.vMonthName=DATENAME(MM,'"+dateformat.format(dMonth.getValue())+"') and " +
					" year=YEAR('"+dateformat.format(dMonth.getValue())+"') and ts.SectionID like '"+strSection+"' and " +
					" ts.vDepartmentID like '"+cmbDepartment.getValue()+"' and ts.FridayAllowance>0 order by Section,ts.empId,ts.iDesignationSerial";

			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes" +"/temp/attendanceFolder";
					String fname = "Monthly_Friday_Allow.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					String strColName[]={"SL#","Employee ID","Proximity ID","Employee Name","Designation Name",
							"Gross","Friday Duty","Friday OT Hrs","Friday Allowance","Friday Duty (< 5 Hours)","Extra","Payable Amount","Signature","Remarks"};

					String Header="Month: "+yearMonthFormat.format(dMonth.getValue());

					String query1= "SELECT distinct SectionID,Section,ts.vDepartmentID,vDepartmentName from tbSalary ts "+
							"inner join tbEmployeeInfo ein on ts.autoEmployeeID = ein.vEmployeeId where " +
							"SectionID like '"+strSection+"' and ts.vDepartmentId = '"+cmbDepartment.getValue()+"' and " +
							"year=YEAR('"+dateformat.format(dMonth.getValue())+"') and " +
							"ts.vMonthName=DATENAME(MM,'"+dateformat.format(dMonth.getValue())+"')and ein.FridayStatus = 1 " +
							"order by Section";

					List <?> lst1=session.createSQLQuery(query1).list();

					String detailQuery[]=new String[lst1.size()];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					String [] signatureOption = {"Prepared By HR Officer","Checked By HR Executive","Manager (HR & Admin)","Manager (Accounts & Finance)","Approved By"};
					int countInd=0;

					for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
					{
						Object [] element = (Object[]) itr1.next();
						groupItem[countInd]="Department Name : "+element[3].toString() +  "               Section Name : "+element[1].toString();
						GroupElement[countInd]=new Object [] {(Object)"",(Object)"",(Object)"Department Name : ",element[3],(Object)"Section Name : ",element[1]};

						detailQuery[countInd]=" select ts.empId,ts.empCode,ts.empName,ts.designation,cast(ts.Gross as float) Gross,ts.FridayAllowance," +
								" cast(ts.itotalFridayOTHour as varchar)+':'+cast(iTotalFridayOTMin as varchar) TotalFridayOT ,cast(ein.FridayLunchFee as float) FridayLunchFee,cast(ts.FridayAllowance-ts.Friday as int)  " +
								" friDuty,cast(ts.fridayAmount-(ein.FridayLunchFee*(ts.FridayAllowance-ts.Friday)) as float) extra,cast(ts.fridayAmount as float) payable,'' Sig,'' Rem from tbSalary ts inner" +
								" join tbEmployeeInfo ein on ts.autoEmployeeID=ein.vEmployeeId  where " +
								" ein.FridayStatus=1 and ts.vMonthName=DATENAME(MM,'"+dateformat.format(dMonth.getValue())+"') and " +
								" year=YEAR('"+dateformat.format(dMonth.getValue())+"') and ts.SectionID = '"+element[0]+"' and " +
								" ts.vDepartmentID like '"+cmbDepartment.getValue()+"' and ts.FridayAllowance>0 order by Section,ts.empId,ts.iDesignationSerial";
						countInd++;
					}

					new GenerateExcelReport(sessionBean, loc, url, fname, "MONTHLY FRIDAY ALLOWANCE", "MONTHLY FRIDAY ALLOWANCE",
							Header, strColName, 2,groupItem, GroupColName, GroupElement, 2, detailQuery, 0, 0, query1, query1,signatureOption);

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
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthlyFridayAllowance.jasper",
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
		catch(Exception exp){showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);}
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

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("460px");
		setHeight("250px");

		// lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:30.0px; left:20.0px;");

		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("100px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:28.0px; left:130.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(new Label("Department Name : "),"top:60.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:58.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:90.0px; left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:88.0px; left:130.0px;");

		chkSection=new CheckBox("All");
		chkSection.setImmediate(true);
		mainLayout.addComponent(chkSection, "top:90.0px; left:400.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:120.0px;left:130.0px;");

		mainLayout.addComponent(new Label("__________________________________________________________________________________"), "top:140.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:170.opx; left:140.0px");
		return mainLayout;
	}
}
