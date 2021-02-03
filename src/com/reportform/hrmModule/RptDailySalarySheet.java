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
public class RptDailySalarySheet extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private PopupDateField dMonth;
	private Label lblSection;
	private Label lblEmpType;


	private Label lblEmployee;
	private ComboBox cmbEmployee;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private ComboBox cmbEmpType;
	private PopupDateField dSalaryMonth;
	private CheckBox chkallemp;


	ArrayList<Component> allComp = new ArrayList<Component>();
	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private OptionGroup opgActivity = new OptionGroup();
	private static final List<String> aictiveType = Arrays.asList(new String[]{"Active","Inactive","All"});
	public RptDailySalarySheet(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("DAILY SALARY SHEET :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		focusMove();
	}

	public void cmbDepartmentAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmployeeMainAttendance " +
					"where Month(dDate)=Month('"+dFormat.format(dMonth.getValue())+"') and " +
					"year(dDate)=year('"+dFormat.format(dMonth.getValue())+"') and vDepartmentId not like 'DEPT10'  order by vDepartmentName";
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
	
	public void EmployeeTypeAdd(String section)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query ="select distinct 0,vEmployeeType from tbEmployeeInfo tei inner join tbEmployeeMainAttendance tema on tema.vEmployeeID=tei.vEmployeeId "+
			"where Month(tema.dDate)=Month('"+dFormat.format(dMonth.getValue())+"') and " +
			"year(tema.dDate)=year('"+dFormat.format(dMonth.getValue())+"') and " +
			"tema.vDepartmentID='"+cmbDepartment.getValue()+"'";

		System.out.println("emp"+query);	
			
			
			
			/*String query="select distinct vSectionId,vSectionName from tbEmployeeMainAttendance " +
					"where Month(dDate)=Month('"+dFormat.format(dMonth.getValue())+"') and " +
					"year(dDate)=year('"+dFormat.format(dMonth.getValue())+"') and " +
					"vDepartmentID='"+cmbDepartment.getValue()+"' order by vSectionName";*/
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmpType.addItem(element[1]);
			//	cmbEmpType.setItemCaption(element[1],  (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("cmbEmployeeType",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbSectionAddData(String section)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbEmployeeMainAttendance " +
					"where Month(dDate)=Month('"+dFormat.format(dMonth.getValue())+"') and " +
					"year(dDate)=year('"+dFormat.format(dMonth.getValue())+"') and " +
					"vDepartmentID='"+cmbDepartment.getValue()+"' order by vSectionName";
		
			System.out.println("Section"+query);
			
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

	private void EmployeeDataAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "SELECT distinct vEmployeeId,employeeCode from tbEmployeeInfo " +
					"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
					"vSectionId='"+cmbSection.getValue().toString().trim()+"' and vEmployeeType='"+cmbEmpType.getValue().toString().trim()+"' order by employeeCode";
			      lblEmployee.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "SELECT distinct vEmployeeId,vEmployeeName,employeeCode from tbEmployeeInfo " +
						"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
						"vSectionId='"+cmbSection.getValue().toString().trim()+"' and vEmployeeType='"+cmbEmpType.getValue().toString().trim()+"'   order by employeeCode";
				lblEmployee.setValue("Employee Name :");
			}

			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "SELECT distinct vEmployeeId,vProximityID,employeeCode from tbEmployeeInfo " +
						"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
						"vSectionId='"+cmbSection.getValue().toString().trim()+"' and vEmployeeType='"+cmbEmpType.getValue().toString().trim()+"'  order by employeeCode";
				lblEmployee.setValue("Proximity ID :");
			}
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1].toString());
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
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dMonth.getValue()!=null)
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

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpType.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					EmployeeTypeAdd(cmbSection.getValue().toString());
				}
			}


		});
		
		cmbEmpType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					EmployeeDataAdd();
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				EmployeeDataAdd();
			}
		});

		chkallemp.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkallemp.booleanValue())
				{
					cmbEmployee.setValue(null);
					cmbEmployee.setEnabled(false);
					EmployeeDataAdd();	
				}
				else
					cmbEmployee.setEnabled(true);
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						if(cmbEmployee.getValue()!=null || chkallemp.booleanValue())
						{
							reportShow();

						}
						else
						{
							showNotification("Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
						}
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
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});


	}

	/*private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String employee="";
		try
		{ 
			if(cmbEmployee.getValue()!=null)
				employee=cmbEmployee.getValue().toString();
			else
			{
				employee="%";
			}

			query="select vEmployeeName,vEmployeeCode,vDepartmentName,vSectionName,vDesignationName," +
					"mGross,Round((mGross/(itotalDay-iholiday)),0)paidAmount,dOtHour,dOTMin," +
					"Round(((mOTRate*dOtHour)+(mOTRate/60)*dOTMin ),0) OTamount" +
					" from funDailySalarySheet1('"+dFormat.format(dMonth.getValue())+"'," +
					"'"+dFormat.format(dMonth.getValue())+"','"+employee+"','"+cmbDepartment.getValue()+"','"+cmbSection.getValue()+"')";

			System.out.println("OMG"+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
				hm.put("Department",cmbDepartment.getItemCaption(cmbDepartment.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("Date",dFormat.format(dMonth.getValue()));
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptDailySalary.jasper",
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
	}*/

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String employee="";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{ 
			if(cmbEmployee.getValue()!=null)
				employee=cmbEmployee.getValue().toString();
			else
			{
				employee="%";
			}

			query="select vEmployeeName,vEmployeeCode,vDepartmentName,vSectionName,vDesignationName," +
					"mGross,Round((mGross/(itotalDay-iholiday)),0)paidAmount,dOtHour,dOTMin," +
					"Round(((mOTRate*dOtHour)+(mOTRate/60)*dOTMin ),0) OTamount" +
					" from funDailySalarySheet1('"+dFormat.format(dMonth.getValue())+"'," +
					"'"+dFormat.format(dMonth.getValue())+"','"+employee+"','"+cmbDepartment.getValue()+"','"+cmbSection.getValue()+"','"+cmbEmpType.getValue()+"','"+(opgActivity.getValue().toString().equals("Active")?"1":(opgActivity.getValue().toString().equals("All")?"%":"0"))+"')";

			System.out.println("OMG"+query);

			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes" +"/temp/attendanceFolder";
					String fname = "Daily_Salary_Report.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					String strColName[]={"SL#","Employee ID","Employee Name","Designation",
							"Gross","Per Day Salary","OT Hours","OT Amount","Payable Amount"};

					String Header="Department:"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"        Section:"+cmbSection.getItemCaption(cmbSection.getValue())+" ";

					String detailQuery[]=new String[1];
					String [] groupItem=new String[0];
					Object [][] GroupElement=new Object[0][];
					String [] GroupColName=new String[0];
					String [] signatureOption = {""};

					detailQuery[0]="SELECT vEmployeeCode,vEmployeeName,vDesignationName,cast(mGross as float) Gross," +
							"cast(Round((mGross/(itotalDay-iholiday)),0)as float) paidAmount," +
							"cast(dOtHour as varchar(120))+':'+CAST(dOTMin as varchar(120))OThours," +
							"cast(Round(((mOTRate*dOtHour)+(mOTRate/60)*dOTMin ),0)as float) OTamount," +
							"cast ((mGross/(itotalDay-iholiday))+Round((((mOTRate*dOtHour)+(mOTRate/60)*dOTMin )),0)as float)payableamount" +
							" from funDailySalarySheet1('"+dFormat.format(dMonth.getValue())+"','"+dFormat.format(dMonth.getValue())+"','"+employee+"','"+cmbDepartment.getValue()+"','"+cmbSection.getValue()+"','"+cmbEmpType.getValue()+"','"+(opgActivity.getValue().toString().equals("Active")?"1":(opgActivity.getValue().toString().equals("All")?"%":"0"))+"')";

					new GenerateExcelReport(sessionBean, loc, url, fname, "DAILY SALARY REPORT", "DAILY SALARY REPORT",
							Header, strColName, 2,groupItem, GroupColName, GroupElement, 2, detailQuery, 0, 0, "", "",signatureOption);

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

					Window win = new ReportViewer(hm,"report/account/hrmModule/RptDailySalary.jasper",
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
			List <?> lst = session.createSQLQuery(sql).list();
			if (!lst.isEmpty()) 
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
		allComp.add(cmbSection);
		allComp.add(dSalaryMonth);
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
		setWidth("450px");
		setHeight("300px");

		lblMonth = new Label("Date :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:10.0px; left:20.0px;");

		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("110px");
		dMonth.setDateFormat("yyyy-MM-dd");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dMonth, "top:08.0px; left:130.0px;");


		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:35.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:33.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:60.0px; left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:130.0px;");
		
		// lblSection
		lblEmpType = new Label("Employee Type :");
		lblEmpType.setImmediate(false);
		lblEmpType.setWidth("100.0%");
		lblEmpType.setHeight("-1px");
		mainLayout.addComponent(lblEmpType,"top:85.0px; left:20.0px;");

		// cmbSection
		cmbEmpType = new ComboBox();
		cmbEmpType.setImmediate(true);
		cmbEmpType.setWidth("260px");
		cmbEmpType.setHeight("-1px");
		cmbEmpType.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbEmpType, "top:83.0px; left:130.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:110.0px; left:50.0px;");

		//lblEmpType
		lblEmployee=new Label("Employee Name : ");
		mainLayout.addComponent(lblEmployee, "top:135.0px;left:20.0px;");

		//cmbEmpType
		cmbEmployee=new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbEmployee, "top:133.0px; left:130.0px;");

		chkallemp=new CheckBox("All");
		chkallemp.setImmediate(true);
		mainLayout.addComponent(chkallemp, "top:135.0px;left:390.0px;");


		// optionGroup
		opgActivity = new OptionGroup("",aictiveType);
		opgActivity.setImmediate(true);
		opgActivity.setStyleName("horizontal");
		opgActivity.setValue("Active");
		mainLayout.addComponent(opgActivity, "top:160.0px;left:130.0px;");
		
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:185px;left:130.0px;");

		//mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:180.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"bottom:10px; left:175.0px");
		return mainLayout;
	}
}