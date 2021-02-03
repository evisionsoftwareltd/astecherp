package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptMonthlyInactiveEmployeeList extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	
	private ComboBox cmbSection,cmbDepartment,cmbSeparationType;
	CheckBox chkDepartmentAll=new CheckBox("All");
	CheckBox chkSectionAll=new CheckBox("All");
	CheckBox chkSeparationTypell=new CheckBox("All");
	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;
	
	private Label lblMonth;
	private PopupDateField dMonth;

	private OptionGroup opgTimeSelect;
	private List<?> timeSelect = Arrays.asList(new String[]{"Monthly","Between Date"});

	//private Label lblLoanType;
	//private ComboBox cmbLoanType;
	//private static final String[] loanType = new String[] {"Advanced","Salary Loan","PF Loan"};
	//private CheckBox chkLoanTypeAll;

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dDbMonth = new SimpleDateFormat("MMMMMMMMM-yyyy");
	
	public RptMonthlyInactiveEmployeeList(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY INACTIVE EMPLOYEE LIST :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentDataLoad();
		setEventAction();
		//cmbDepartment.setEnabled(false);
	}
	
	private void cmbSeparationDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sql="",section="%";
		if(!chkSectionAll.booleanValue())
		{
			section=cmbSection.getValue().toString();
		}
		
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct 0,vStatus from tbEmployeeInfo where iStatus=0 "
					+ "and MONTH(dStatusDate) =MONTH('"+dDbFormat.format(dMonth.getValue())+"') "
					+ "and YEAR(dStatusDate) =YEAR('"+dDbFormat.format(dMonth.getValue())+"') "
					+ "and vDepartmentId='"+cmbDepartment.getValue()+"' and vDepartmentId!='DEPT10' and vSectionId like '"+section+"' "
					+ "order by vStatus ";
			}
			else
			{
				sql = "select distinct 0,vStatus from tbEmployeeInfo where iStatus=0 "
					+ "and dStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' "
					+ "and '"+dDbFormat.format(dToDate.getValue())+"' "
					+ "and vDepartmentId='"+cmbDepartment.getValue()+"' and vDepartmentId!='DEPT10' and vSectionId like '"+section+"' "
					+ "order by vStatus ";
			}
			System.out.println("cmbSeparationDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbSeparationType.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbSeparationType.addItem(element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSectionDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sql="";
		
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct vSectionId,"
						+ "(select SectionName from tbSectionInfo where vSectionID=a.vSectionId)SectionName  "
						+ "from tbEmployeeInfo a "
						+ "where iStatus=0 and vDepartmentId!='DEPT10' and vDepartmentId='"+cmbDepartment.getValue()+"' "
						+ "and MONTH(dStatusDate) =MONTH('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and YEAR(dStatusDate) =YEAR('"+dDbFormat.format(dMonth.getValue())+"') order by SectionName ";
			}
			else
			{
				sql = "select distinct vSectionId,"
						+ "(select SectionName from tbSectionInfo where vSectionID=a.vSectionId)SectionName  "
						+ "from tbEmployeeInfo a "
						+ "where iStatus=0 and vDepartmentId!='DEPT10' and vDepartmentId='"+cmbDepartment.getValue()+"' "
						+ "and dStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' "
						+ "order by SectionName";
			}
			System.out.println("cmbSectionDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbSection.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbSection.addItem(element[0].toString());
				cmbSection.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbDepartmentDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sql="";
		try
		{
			if(opgTimeSelect.getValue().toString()=="Monthly")
			{
				sql = "select distinct vDepartmentId,"
						+ "(select vDepartmentName from tbDepartmentInfo where vDepartmentId=a.vDepartmentId)vDepartmentName "
						+ "from tbEmployeeInfo a "
						+ "where iStatus=0 and vDepartmentId!='DEPT10' "
						+ "and MONTH(dStatusDate) =MONTH('"+dDbFormat.format(dMonth.getValue())+"') " +
						"and YEAR(dStatusDate) =YEAR('"+dDbFormat.format(dMonth.getValue())+"') order by vDepartmentName ";
			}
			else
			{
				sql = "select distinct vDepartmentId,"
						+ "(select vDepartmentName from tbDepartmentInfo where vDepartmentId=a.vDepartmentId)vDepartmentName "
						+ "from tbEmployeeInfo a "
						+ "where iStatus=0 and vDepartmentId!='DEPT10' "
						+ "and dStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' and '"+dDbFormat.format(dToDate.getValue())+"' "
						+ "order by vDepartmentName";
			}
			System.out.println("cmbDepartmentDataLoad: "+sql);
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbDepartment.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbDepartment.addItem(element[0].toString());
				cmbDepartment.setItemCaption(element[0].toString(), element[1].toString());
				//cmbDepartment.setValue(element[0].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void setEventAction()
	{
		opgTimeSelect.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgTimeSelectAction();
			}
		});
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						cmbDepartmentDataLoad();
					}
				}
				else
				{
					if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
					{
						cmbDepartmentDataLoad();
					}
				}
			}
		});
		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						cmbDepartmentDataLoad();
					}
				}
				else
				{
					if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
					{
						cmbDepartmentDataLoad();
					}
				}
			}
		});
		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgTimeSelect.getValue().toString()=="Monthly")
				{
					if(dMonth.getValue()!=null)
					{
						cmbDepartmentDataLoad();
					}
				}
				else
				{
					if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
					{
						cmbDepartmentDataLoad();
					}
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbDepartment.getValue()!=null)
				{
					cmbSection.removeAllItems();
					cmbSectionDataLoad();
				}
			}
		});
		cmbSection.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						cmbSeparationType.removeAllItems();
						cmbSeparationDataLoad();
					}
				}
			}
		});
		chkSectionAll.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkSectionAll.booleanValue()){
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					cmbSeparationDataLoad();
				}
				else{
					cmbSection.setEnabled(true);
				}
			}
		});
		chkSeparationTypell.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkSeparationTypell.booleanValue()){
					cmbSeparationType.setValue(null);
					cmbSeparationType.setEnabled(false);
				}
				else{
					cmbSeparationType.setEnabled(true);
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSeparationType.getValue()!=null || chkSeparationTypell.booleanValue())
				{
					if(cmbDepartment.getValue()!=null)
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(opgTimeSelect.getValue().toString().equals("Monthly"))
							{
								if(dMonth.getValue()!=null)
								{
									reportpreview();
								}
							}
							else
							{
								if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
								{
									reportpreview();
								}
								else
								{
									showNotification("Warning","Select Date Range",Notification.TYPE_WARNING_MESSAGE);
								}
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
				else
				{
					showNotification("Warning","Select Separation Type",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}
	public void opgActionWiseVisible(boolean b)
	{
		lblMonth.setVisible(b);
	    dMonth.setVisible(b);
	    lblFromDate.setVisible(!b);
	    dFromDate.setVisible(!b);
	    lblToDate.setVisible(!b);
	    dToDate.setVisible(!b);
	}
	private void opgTimeSelectAction()
	{
		if(opgTimeSelect.getValue().toString()=="Monthly")
		{
			opgActionWiseVisible(true);
			dFromDate.setValue(new Date());
			dToDate.setValue(new Date());
			dMonth.setValue(new Date());
			
			cmbDepartment.removeAllItems();
			chkSectionAll.setValue(false);
			cmbSection.setEnabled(true);
			cmbSection.removeAllItems();
			chkSeparationTypell.setValue(false);
			cmbSeparationType.setEnabled(true);
			cmbSeparationType.removeAllItems();
			if(dMonth.getValue()!=null)
			{
				cmbDepartmentDataLoad();
			}
		}
		else
		{
			opgActionWiseVisible(false);
			dFromDate.setValue(new Date());
			dToDate.setValue(new Date());
			dMonth.setValue(new Date());
			
			cmbDepartment.removeAllItems();
			chkSectionAll.setValue(false);
			cmbSection.setEnabled(true);
			cmbSection.removeAllItems();
			chkSeparationTypell.setValue(false);
			cmbSeparationType.setEnabled(true);
			cmbSeparationType.removeAllItems();
			if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
			{
				cmbDepartmentDataLoad();
			}
		}
	}
	
	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String loanType= "",report="",query="",vEmployeeStatus="%";
		Session session=SessionFactoryUtil.getInstance().openSession();
		if(!chkSeparationTypell.booleanValue())
		{
			vEmployeeStatus=cmbSeparationType.getValue().toString();
		}	
		
		try
		{
			//"Monthly","Between Date
			if(opgTimeSelect.getValue().toString().equals("Monthly"))
			{
				report="report/account/hrmModule/RptMonthlyEmployeeList.jasper";
				
				query="select employeeCode,a.vEmployeeName,"
						+ "(select designationName from tbDesignationInfo where designationId=a.vDesignationId )designationName,"
						+ "(select vDepartmentName from tbDepartmentInfo where vDepartmentId=a.vDepartmentId )vDepartmentName,"
						+ "(select SectionName from tbSectionInfo where vSectionID=a.vSectionId )SectionName,"
						+ "vEmployeeType,dStatusDate,iStatus,vStatus "
						+ "from tbEmployeeInfo a "
						+ "where iStatus=0 and YEAR(dStatusDate) = YEAR('"+dDbFormat.format(dMonth.getValue())+"') "
						+ "and month(dStatusDate) = month('"+dDbFormat.format(dMonth.getValue())+"') "
						+ "and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"")+"'  "
						+ "and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' "
						+ "and vStatus like '"+vEmployeeStatus+"' "
						+ "order by vDepartmentName,SectionName,dJoiningDate";
			}
			else if(opgTimeSelect.getValue().toString().equals("Between Date"))
			{
				report="report/account/hrmModule/RptDateBetweenEmployeeList.jasper";
				query="select employeeCode,a.vEmployeeName,"
						+ "(select designationName from tbDesignationInfo where designationId=a.vDesignationId )designationName,"
						+ "(select vDepartmentName from tbDepartmentInfo where vDepartmentId=a.vDepartmentId )vDepartmentName,"
						+ "(select SectionName from tbSectionInfo where vSectionID=a.vSectionId )SectionName,"
						+ "vEmployeeType,dStatusDate,iStatus,vStatus "
						+ "from tbEmployeeInfo a "
						+ "where iStatus=0 and dStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' "
						+ "and '"+dDbFormat.format(dToDate.getValue())+"' "
						+ "and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"")+"'  "
						+ "and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' "
						+ "and vStatus like '"+vEmployeeStatus+"' "
						+ "order by vDepartmentName,SectionName,dJoiningDate";
			}
			
			System.out.println("Report Query: "+query);

			if(queryValueCheck(query))
			{
				//==========Didarul Alam Update By: 27-02-2020 Start==========//
				if(RadioBtnGroup.getValue()=="Excel")
				{
					if(opgTimeSelect.getValue().toString().equals("Monthly"))
					{
						//hm.put("dMonth", dDbMonth.format(dMonth.getValue()).toString());
						
						String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
						String fname = "InactiveEmployeeList.xls";
						String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;
						String strColName[]={"SL#","Employee ID","Employee Name","Designation","Joining Date","Separation Date","Length Of Service","Separation Type"};
						String Header=dDbMonth.format(dMonth.getValue()).toString();
						String exelSql="";
						
						exelSql = "select distinct vDepartmentId,(select vDepartmentName from tbDepartmentInfo where vDepartmentId=a.vDepartmentId)vDepartmentName,"
								+ "vSectionId,(select SectionName from tbSectionInfo where vSectionID=a.vSectionId)SectionName,vEmployeeType "
								+ "from tbEmployeeInfo a "
								+ "where iStatus=0 and YEAR(dStatusDate) = YEAR('"+dDbFormat.format(dMonth.getValue())+"') "
								+ "and month(dStatusDate) = month('"+dDbFormat.format(dMonth.getValue())+"') "
								+ "and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"")+"'  "
								+ "and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' "
								+ "and vStatus like '"+vEmployeeStatus+"' order by vDepartmentName,SectionName,vEmployeeType";
						
						System.out.println("exelSql: "+exelSql);
						
						List <?> lst1=session.createSQLQuery(exelSql).list();
								
						String detailQuery[]=new String[lst1.size()];
						String [] signatureOption = {"HEAD OF HR","HEAD OF ACCOUNTS","GROUP C.E.O / CHAIRMAN"};
						//String [] signatureOption = new String [0];
						String [] groupItem=new String[lst1.size()];
						Object [][] GroupElement=new Object[lst1.size()][];
						String [] GroupColName=new String[0];
						int countInd=0;
						
						for(Iterator<?> iter=lst1.iterator(); iter.hasNext();)
						{
							 Object [] element = (Object[])iter.next();
								groupItem[countInd]="Department : "+element[1].toString()+"              Section : "+element[3].toString()+"              Employee Type : "+element[4].toString();
								GroupElement[countInd]=new Object [] {(Object)"",(Object)"Department : ",element[1],(Object)"Section : ",element[3],(Object)"Employee Type : ",element[4]};
								
								detailQuery[countInd]="select employeeCode,a.vEmployeeName,"
										+ "(select designationName from tbDesignationInfo where designationId=a.vDesignationId )designationName,"
										+ "dJoiningDate,dStatusDate,dbo.funLengthOfService(dJoiningDate,dStatusDate),vEmployeeType "
										+ "from tbEmployeeInfo a "
										+ "where iStatus=0 and YEAR(dStatusDate) = YEAR('"+dDbFormat.format(dMonth.getValue())+"') "
										+ "and month(dStatusDate) = month('"+dDbFormat.format(dMonth.getValue())+"') "
										+ "and vDepartmentId like '"+element[0].toString()+"'  "
										+ "and vSectionId like '"+element[2].toString()+"' "
										+ "and vEmployeeType like '"+element[4].toString()+"' "
										+ "and vStatus like '"+vEmployeeStatus+"' "
										+ "order by dJoiningDate";
								
							System.out.println("Details query :"+detailQuery[countInd]);
							countInd++;
							
						}
						
						new GenerateExcelReport(sessionBean, loc, url, fname, "INACTIVE EMPLOYEE LIST", "INACTIVE EMPLOYEE LIST",
								Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, detailQuery, 14, 25, "A4",
								"Landscape",signatureOption);
						
						Window window = new Window();
						getApplication().addWindow(window);
						getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
					}
					else if(opgTimeSelect.getValue().toString().equals("Between Date"))
					{
						String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
						String fname = "InactiveEmployeeList.xls";
						String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;
						String strColName[]={"SL#","Employee ID","Employee Name","Designation","Joining Date","Separation Date","Separation Type"};
						String Header="From: "+dFromDate.getValue()+" To: "+dToDate.getValue();
						String exelSql="";
						
						exelSql = "select distinct vDepartmentId,(select vDepartmentName from tbDepartmentInfo where vDepartmentId=a.vDepartmentId)vDepartmentName,"
								+ "vSectionId,(select SectionName from tbSectionInfo where vSectionID=a.vSectionId)SectionName,vEmployeeType "
								+ "from tbEmployeeInfo a "
								+ "where iStatus=0 and dStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' "
								+ "and '"+dDbFormat.format(dToDate.getValue())+"' "
								+ "and vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"")+"'  "
								+ "and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' "
								+ "and vStatus like '"+vEmployeeStatus+"' order by vDepartmentName,SectionName,vEmployeeType";
						
						System.out.println("exelSql: "+exelSql);
						
						List <?> lst1=session.createSQLQuery(exelSql).list();
								
						String detailQuery[]=new String[lst1.size()];
						String [] signatureOption = {"HEAD OF HR","HEAD OF ACCOUNTS","GROUP C.E.O / CHAIRMAN"};
						//String [] signatureOption = new String [0];
						String [] groupItem=new String[lst1.size()];
						Object [][] GroupElement=new Object[lst1.size()][];
						String [] GroupColName=new String[0];
						int countInd=0;
						
						for(Iterator<?> iter=lst1.iterator(); iter.hasNext();)
						{
							 Object [] element = (Object[])iter.next();
								groupItem[countInd]="Department : "+element[1].toString()+"              Section : "+element[3].toString()+"              Employee Type : "+element[4].toString();
								GroupElement[countInd]=new Object [] {(Object)"",(Object)"Department : ",element[1],(Object)"Section : ",element[3],(Object)"Employee Type : ",element[4]};
								
								detailQuery[countInd]="select employeeCode,a.vEmployeeName,"
										+ "(select designationName from tbDesignationInfo where designationId=a.vDesignationId )designationName,"
										+ "dJoiningDate,dStatusDate,dbo.funLengthOfService(dJoiningDate,dStatusDate),vEmployeeType "
										+ "from tbEmployeeInfo a "
										+ "where iStatus=0 and dStatusDate between '"+dDbFormat.format(dFromDate.getValue())+"' "
										+ "and '"+dDbFormat.format(dToDate.getValue())+"' "
										+ "and vDepartmentId like '"+element[0].toString()+"'  "
										+ "and vSectionId like '"+element[2].toString()+"' "
										+ "and vEmployeeType like '"+element[4].toString()+"' "
										+ "and vStatus like '"+vEmployeeStatus+"' "
										+ "order by dJoiningDate";
								
							System.out.println("Details query :"+detailQuery[countInd]);
							countInd++;
							
						}
						
						new GenerateExcelReport(sessionBean, loc, url, fname, "INACTIVE EMPLOYEE LIST", "INACTIVE EMPLOYEE LIST",
								Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, detailQuery, 14, 25, "A4",
								"Landscape",signatureOption);
						
						Window window = new Window();
						getApplication().addWindow(window);
						getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
					}
				}
				else
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", cmbDepartment.getItemCaption(cmbDepartment.getValue()));
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("dMonth", dDbMonth.format(dMonth.getValue()).toString());
					hm.put("dDate", dFromDate.getValue());
					hm.put("fromDate", dFromDate.getValue());
					hm.put("toDate", dToDate.getValue());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("Department", cmbDepartment.getItemCaption(cmbDepartment.getValue()));
					hm.put("sql", query);
	
					Window win = new ReportViewer(hm,report,
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
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
	
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		opgTimeSelect=new OptionGroup("",timeSelect);
		opgTimeSelect.select("Monthly");
		opgTimeSelect.setImmediate(true);
		opgTimeSelect.setStyleName("horizontal");
		mainLayout.addComponent(opgTimeSelect, "top:10.0px; left:150px;");

		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:40.0px; left:30.0px;");
		lblFromDate.setVisible(false);
		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:38.0px; left:135.0px;");
		dFromDate.setVisible(false);
		//lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:40.0px; left:30.0px;");
		
		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMMMMMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:38.0px; left:135.0px;");
		
		// lblToDate
		lblToDate = new Label("To");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate,"top:40.0px; left:250.0px;");
		lblToDate.setVisible(false);

		// dToDate
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:38.0px; left:268.0px;");
		dToDate.setVisible(false);
		
		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("250.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department : "), "top:70.0px;left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68.0px;left:135.0px");
		
		cmbSection=new ComboBox();
		cmbSection.setWidth("250.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainLayout.addComponent(new Label("Section : "), "top:100.0px;left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:98.0px;left:135.0px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:100.0px;left:385.0px");

		cmbSeparationType=new ComboBox();
		cmbSeparationType.setWidth("250.0px");
		cmbSeparationType.setHeight("-1px");
		cmbSeparationType.setImmediate(true);
		cmbSeparationType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Type : "), "top:130.0px;left:30.0px;");
		mainLayout.addComponent(cmbSeparationType, "top:128.0px;left:135.0px");
		chkSeparationTypell.setImmediate(true);
		mainLayout.addComponent(chkSeparationTypell, "top:130.0px;left:385.0px");
		
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:160px;left:150px;");

		mainLayout.addComponent(new Label("_______________________________________________________________________________"), "top:175.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:200.opx; left:135.0px");

		return mainLayout;
	}
}
