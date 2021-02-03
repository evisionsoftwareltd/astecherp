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
public class RptEditMonthlySalary_CHO extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblDepartment;
	private Label lblSection;
	private Label lblSalaryMonth;

	private PopupDateField dSalaryMonth;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private Label lblEmployee;
	private ComboBox cmbEmployee;

	private CheckBox chkEmployeeAll;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");
	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptEditMonthlySalary_CHO(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EDIT MONTHLY SALARY :: "+sessionBean.getCompany());
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
			String sql="select distinct vDepartmentID,vDepartmentName from tbUdSalary where " +
					"vMonthName=datename(mm,'"+dFormat.format(dSalaryMonth.getValue())+"') and " +
					"year=year('"+dFormat.format(dSalaryMonth.getValue())+"') and UDFlag!='DELETE' " +
					"and vDepartmentName = 'CHO 'order by vDepartmentName";

			List <?> list=session.createSQLQuery(sql).list();
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
		finally
		{
			session.close();
		}
	}

	public void cmbSectionAddData(String DepartmentID)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct SectionID,Section from tbUdSalary where vDepartmentId = '"+DepartmentID+"' " +
					"and vMonthName=datename(mm,'"+dFormat.format(dSalaryMonth.getValue())+"') and " +
					"year=year('"+dFormat.format(dSalaryMonth.getValue())+"') and UDFlag!='DELETE' and Section = 'CHO' order by Section";
			List <?> list=session.createSQLQuery(sql).list();
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
		finally
		{
			session.close();
		}
	}

	public void cmbEmployeeAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vAutoEmployeeID,empID from tbUdSalary where vMonthName=datename(mm,'"+dFormat.format(dSalaryMonth.getValue())+"') "
					+ " and year=year('"+dFormat.format(dSalaryMonth.getValue())+"') and vDepartmentId = '"+cmbDepartment.getValue()+"' and "
					+ " SectionID = '"+cmbSection.getValue()+"' and UDFlag!='DELETE' order by empID";
			lblEmployee.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select distinct vAutoEmployeeID,empName from tbUdSalary where vMonthName=datename(mm,'"+dFormat.format(dSalaryMonth.getValue())+"') "
					+ " and year=year('"+dFormat.format(dSalaryMonth.getValue())+"') and vDepartmentId = '"+cmbDepartment.getValue()+"' and "
					+ " SectionID = '"+cmbSection.getValue()+"' and UDFlag!='DELETE' order by empName";
				lblEmployee.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "select distinct vAutoEmployeeID,empCode from tbUdSalary where vMonthName=datename(mm,'"+dFormat.format(dSalaryMonth.getValue())+"') "
						+ " and year=year('"+dFormat.format(dSalaryMonth.getValue())+"') and vDepartmentId = '"+cmbDepartment.getValue()+"' and "
						+ " SectionID = '"+cmbSection.getValue()+"' and UDFlag!='DELETE' order by empCode";
				lblEmployee.setValue("Proximity ID :");
			}
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element = (Object[]) itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], (String) element[1]);
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
		dSalaryMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dSalaryMonth.getValue()!=null)
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
				if(cmbDepartment.getValue()!= null)
					cmbSectionAddData(cmbDepartment.getValue().toString());
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeAddData();
				}
			}
		});

		chkEmployeeAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkEmployeeAll.booleanValue()==true)
				{
					cmbEmployee.setValue(null);
					cmbEmployee.setEnabled(false);
				}
				else
				{
					cmbEmployee.setEnabled(true);
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dSalaryMonth.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						if(cmbSection.getValue()!=null)
						{
							if(cmbEmployee.getValue()!=null || chkEmployeeAll.booleanValue())
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
				cmbEmployee.removeAllItems();
				cmbEmployeeAddData();
			}
		});
	}

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			/*String query="select year,vMonthName,empId,empCode,empName,empType,designation,vDepartmentId,vDepartmentName,SectionID,Section,joinDate,"
					+ " totalWorkingDay,present,absentDay,leaveDay,holiday,Gross,basicSalary,houseRent,Conveyance,Medical,perDay,AttBonus,"
					+ " FridayAllowance,salaryCutAbsent,advanceSalary,incomeTax,Insurance,ProvidentFund,totalDeduction,Adjust,Less,round(Gross+AttBonus,0) "
					+ " as subTotal,round(absentDay*perDay,0) as absAmt,UDFlag,userId,userIP,entryTime from tbUdSalary where "
					+ " vMonthName=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') and year=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') "
					+ " and vDepartmentId='"+cmbDepartment.getValue().toString()+"' and SectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' "
					+ " and UDFlag!='DELETE' order by vDepartmentName,Section,empId";*/
			
			String query = "select 'Current' vStatus,ISNULL(userId,'') vUserName,ISNULL(userIP,'') vUserIP,ISNULL(entryTime,'') dEntryTime,vMonthName,year,"
					+ " autoEmployeeID,empId,empCode,empName,designation,joinDate,totalDaysofMonth,totalWorkingDay,holiday,present,absentDay,leaveDay,Gross,"
					+ " basicSalary,houseRent,Conveyance,Medical,AttBonus,FridayAllowance,ROUND((Gross+AttBonus),0) subTotal,ROUND((absentDay*perDay),0) absAmt,"
					+ " advanceSalary,incomeTax,Insurance,Adjust,Less from tbSalary where SectionID like '"+cmbSection.getValue()+"' and (autoEmployeeID like "
					+ " '"+(cmbEmployee.getValue()!=null?cmbEmployee.getValue():"%")+"' and autoEmployeeID in (select vAutoEmployeeID from tbUdSalary where "
					+ " SectionID like '"+cmbSection.getValue()+"' and MONTH(dDate) = MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') and "
					+ " YEAR(dDate)=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') and UDFlag!='DELETE')) and MONTH(dDate) = MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') "
					+ " and YEAR(dDate) = YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') union  all select 'OLD' vStatus,ISNULL(userId,'') vUserName,"
					+ " ISNULL(userIP,'') vUserIP,ISNULL(entryTime,'') dEntryTime,vMonthName, year, vAutoEmployeeID,empId,empCode,empName,designation,joinDate,"
					+ " totalDaysofMonth,totalWorkingDay,holiday,present,absentDay,leaveDay,Gross,basicSalary,houseRent,Conveyance,Medical,AttBonus,FridayAllowance,"
					+ " (Gross+AttBonus) subTotal,(absentDay*perDay) absAmt,advanceSalary,incomeTax,Insurance,Adjust,Less from tbUDSalary where SectionID like "
					+ " '"+cmbSection.getValue()+"' and vAutoEmployeeID like '"+(cmbEmployee.getValue()!=null?cmbEmployee.getValue():"%")+"' and "
					+ " MONTH(dDate) = MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') and YEAR(dDate) = YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') "
					+ " and UDFlag!='DELETE' order by empName,autoEmployeeID,vStatus,dEntryTime desc";
			
			System.out.println("Edit :" +query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("month",dMonthFormat.format(dSalaryMonth.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("DepartmentName", cmbDepartment.getItemCaption(cmbDepartment.getValue()));
				hm.put("SectionName", cmbSection.getItemCaption(cmbSection.getValue()));
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEditMonthlySalary.jasper",
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

		finally
		{
			session.close();
		}
		return false;
	}

	private void focusMove()
	{
		allComp.add(cmbDepartment);
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
		setHeight("290px");

		// lblSalaryMonth
		lblSalaryMonth = new Label("Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("100.0%");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth,"top:20.0px; left:30.0px;");

		// dSalaryMonth
		dSalaryMonth = new PopupDateField();
		dSalaryMonth.setImmediate(true);
		dSalaryMonth.setWidth("140px");
		dSalaryMonth.setHeight("-1px");
		dSalaryMonth.setDateFormat("MMMMM-yyyy");
		dSalaryMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dSalaryMonth.setValue(new java.util.Date());
		mainLayout.addComponent(dSalaryMonth, "top:18.0px; left:130.0px;");

		lblDepartment = new Label("Department :");
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment,"top:50.0px; left:30.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDepartment, "top:48.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:80.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:78.0px; left:130.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:110.0px; left:50.0px;");

		lblEmployee = new Label("Employee ID");
		mainLayout.addComponent(lblEmployee, "top:140.0px; left:30.0px;");
		
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:138.0px; left:130.0px;");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeAll, "top:140.0px; left:390.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:170.0px;left:130.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:190.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:210.opx; left:140.0px");
		return mainLayout;
	}
}