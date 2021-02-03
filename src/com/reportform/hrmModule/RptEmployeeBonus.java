package com.reportform.hrmModule;

import java.io.File;
import java.io.IOException;
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
import com.common.share.SalaryExcelReport;
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
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptEmployeeBonus extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private InlineDateField dYear;
	private Label lblOccasion;
	private ComboBox cmbOccasion;
	private Label lblDepartment;
	private ComboBox cmbDepartment;
	private CheckBox departmentAll;
	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox sectionAll;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dYearFormat=new SimpleDateFormat("yyyy");

	public RptEmployeeBonus(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("Festival Bonus :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		addDepartmentName();
		addOccasionName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		dYear.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				addDepartmentName();
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				addSectionName();
			}
		});
		
		departmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(departmentAll.booleanValue())
				{
					cmbDepartment.setEnabled(false);
					cmbDepartment.setValue(null);
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});
		
		
		sectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(sectionAll.booleanValue())
				{
					cmbSection.setEnabled(false);
					cmbSection.setValue(null);
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
				if(cmbDepartment.getValue()!=null || departmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null || sectionAll.booleanValue())
					{
						if(cmbOccasion.getValue()!=null)
						{
							reportShow();
						}
						else
						{
							showNotification("Warning","Select Occasion",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
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

	public void addOccasionName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(" SELECT distinct vOccasion,vOccasion from tbEmployeeBonus where " +
					"YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"')").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbOccasion.addItem(element[0]);
				cmbOccasion.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addOccasionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(" SELECT distinct vDepartmentID,vDepartmentName from " +
					"tbEmployeeBonus where YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') " +
					"and vDepartmentName!='CHO' order by vDepartmentName").list();
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
			List <?> list = session.createSQLQuery(" SELECT distinct vSectionID,vDepartmentName,vSectionName from tbEmployeeBonus " +
					"where YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') and vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' " +
					"and vSectionName!='CHO' order by vDepartmentName,vSectionName").list();
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

	private void reportShow()
	{
		String Dept="";
		String Section="";
				
		
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		
		if(cmbDepartment.getValue()!=null)
		{
			Dept=cmbDepartment.getValue().toString();
		}
		else
		{
			Dept="%";
		}
		
		
		if(cmbSection.getValue()!=null)
		{
			Section=cmbSection.getValue().toString();
		}
		else
		{
			Section="%";
		}
		
		
		
		String query = " select dGenerateDate,dBonusDate,vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName,vDesignationID," +
				"vDesignationName,vDepartmentID,vDepartmentName,vSectionID,vSectionName,mGrossSalary,dJoiningDate," +
				"(lengthOfService/365) losYear,(lengthOfService%365)/30 losMonth,(lengthOfService%365)%30 losDay," +
				"lengthOfService,mBonusAmt,vOccasion,vUserName,vUserIP,dEntryTime from tbEmployeeBonus where " +
				"YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') and vDepartmentID like '"+Dept+"' " +
				"and vSectionID like '"+Section+"' and vOccasion='"+cmbOccasion.getValue().toString()+"' order " +
				"by vDepartmentName,vSectionName,vEmployeeCode";

		if(queryValueCheck(query))
		{
			try
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";

					String fname = "Employee_Bonus.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					File inFile;
					String header[]=new String[3];
					header[0]="For : "+cmbOccasion.getValue().toString();
					header[1]="Year : "+dYearFormat.format(dYear.getValue());
					header[2]="Department Name : "+cmbDepartment.getItemCaption(cmbDepartment.getValue())+" Section Name : "+cmbSection.getItemCaption(cmbSection.getValue());
					String reportName = "BONUS REPORT";
					String detailQuery[]=new String[1];
					String GroupQuery[]=new String[1];
					String [] signatureOption = new String [0];
					int rowWidth=0;
					inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/EmployeeBonusReport.xls");
					detailQuery[0]=" select vEmployeeCode,vProximityID,vEmployeeName,vDesignationName,CAST(mGrossSalary as FLOAT) mGrossSalary," +
							"dJoiningDate,(lengthOfService/365) losYear,(lengthOfService%365)/30 losMonth," +
							"(lengthOfService%365)%30 losDay,CAST(mBonusAmt as FLOAT) mBonusAmt,'' Signature,'' Remarks from tbEmployeeBonus " +
							"where YEAR(dBonusDate)=YEAR('"+dFormat.format(dYear.getValue())+"') and vDepartmentID like '"+Dept+"' " +
							"and vSectionID like '"+Section+"' and vOccasion='"+cmbOccasion.getValue().toString()+"' order by vDepartmentName,vSectionName," +
							"vEmployeeCode";
					rowWidth=13;
					new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "EMPLOYEE_BONUS", 
							reportName, 2, GroupQuery, 2, detailQuery, rowWidth,9,signatureOption);

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
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptEmployeeBonus.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

					win.setCaption("Project Report");
					this.getParent().getWindow().addWindow(win);
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			catch(Exception exp)
			{
				showNotification("reportView "+exp,Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
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
		allComp.add(cmbOccasion);
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
		setHeight("260px");

		dYear = new InlineDateField();
		dYear.setResolution(InlineDateField.RESOLUTION_YEAR);
		dYear.setDateFormat("yyyy");
		dYear.setValue(new java.util.Date());
		dYear.setImmediate(true);
		mainLayout.addComponent(new Label("Year : "), "top:30.0px; left:30.0px;");
		mainLayout.addComponent(dYear, "top:28.0px; left:150.0px;");

		// lblCategory
		lblDepartment = new Label();
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		lblDepartment.setValue("Department Name :");
		mainLayout.addComponent(lblDepartment,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("250px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:58.0px; left:150.0px;");
		
		departmentAll = new CheckBox("All");
		departmentAll.setImmediate(true);
		departmentAll.setWidth("-1px");
		departmentAll.setHeight("-1px");
		//mainLayout.addComponent(departmentAll, "top:60.0px;left:400.0px;");
		
		// lblCategory
		lblSection = new Label();
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Section Name :");
		mainLayout.addComponent(lblSection,"top:90.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("250px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:88.0px; left:150.0px;");
		
		sectionAll  = new CheckBox("All");
		sectionAll.setImmediate(true);
		sectionAll.setWidth("-1px");
		sectionAll.setHeight("-1px");
		mainLayout.addComponent(sectionAll, "top:90.0px;left:400.0px;");
		

		// lblCategory
		lblOccasion = new Label();
		lblOccasion.setImmediate(false);
		lblOccasion.setWidth("100.0%");
		lblOccasion.setHeight("-1px");
		lblOccasion.setValue("Occasion Name :");
		mainLayout.addComponent(lblOccasion,"top:120.0px; left:30.0px;");

		cmbOccasion = new ComboBox();
		cmbOccasion.setImmediate(false);
		cmbOccasion.setWidth("250px");
		cmbOccasion.setHeight("-1px");
		cmbOccasion.setNullSelectionAllowed(true);
		cmbOccasion.setImmediate(true);
		mainLayout.addComponent(cmbOccasion, "top:118.0px; left:150.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:150.0px;left:150.0px;");
		mainLayout.addComponent(cButton,"top:180.opx; left:165.0px");
		return mainLayout;
	}
}
