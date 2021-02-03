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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptAuditMonthlySalary extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblSalaryMonth;

	private Label lblEmpType;
	private ComboBox cmbEmpType;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private PopupDateField dSalaryMonth;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	public RptAuditMonthlySalary(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY_SALARY :: "+sessionBean.getCompany());
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
			String query="select distinct vDepartmentId,vDepartmentName from tbAuditSalary where " +
					"Month(dDate) = Month('"+dFormat.format(dSalaryMonth.getValue())+"') order by vDepartmentName";
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
			String query="select distinct vSectionID,vSectionName from tbAuditSalary where " +
					"vDepartmentID='"+cmbDepartment.getValue()+"' and " +
					"Month(dDate) = Month('"+dFormat.format(dSalaryMonth.getValue())+"') order by vSectionName";
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

	public void setEventAction()
	{

		dSalaryMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
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
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionAddData();
				}
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
						if(dSalaryMonth.getValue()!=null)
						{
							if(cmbEmpType.getValue()!=null)
								reportShow();
						}
						else
						{
							showNotification("Select Month",Notification.TYPE_WARNING_MESSAGE);
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

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String rptName="";

		if(cmbEmpType.getValue().toString().equalsIgnoreCase("Casual") || cmbEmpType.getValue().toString().equalsIgnoreCase("Temporary"))
		{
			query=" select distinct iYear,vMonthName,vEmployeeID,vEmployeeCode,vEmployeeName,vEmployeeType,"
					+ "vDesignationID,vDesignationName,vDepartmentID,vDepartmentName,vSectionID,vSectionName,dJoinDate,iTotalWorkingDay,"
					+ "iPresentDays,iAbsentDays,iLeaveDays,iHolidays,mGross,mPerDaySalary,mAttendanceBonus,"
					+ "mAdvanceSalary,mAdjust,mIncomeTax,mInsurance,iTotalOTHour,iTotalOTMin,iExtraOT,"
					+ " mOTRate,(mGross-ROUND(iAbsentDays*mPerDaySalary,0)) as salaryAmt,"
					+ "round(itotalOTHour*mOTRate+(mOTRate/60*iTotalOTMin),0) as otAmt from "
					+ "tbAuditSalary where vMonthName=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') " +
					"and iyear=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') and vDepartmentID ='"+cmbDepartment.getValue().toString()+"' and vSectionID='"+cmbSection.getValue().toString()+"' " +
					"and vEmployeeType='"+cmbEmpType.getValue().toString()+"' order by vSectionID,vEmployeeCode";
			rptName="rptAuditMonthlySalaryCasual.jasper";
		}

		else
		{
			query="select distinct iYear,vMonthName,vEmployeeID,vEmployeeCode,vProximityId,vEmployeeName,vEmployeeType,vDesignationID,"
					+ "vDesignationName,vDepartmentID,vDepartmentName,vSectionID, vSectionName,dJoinDate,iTotalWorkingDay,iPresentDays,"
					+ "iAbsentDays,iLeaveDays,iHolidays,cast(mGross as float) mGross,cast(mBasic as float) mBasic,cast(mHouseRent as float) mHouseRent,cast(mConveyance as float) mConveyance,cast(mMedicalAllowance as float) mMedicalAllowance,"
					+ "mPerDaySalary,mAttendanceBonus,mAdvanceSalary,mIncomeTax,mInsurance,mAdjust,"
					+ "round(mGross+mAttendanceBonus,0) assubTotal,round(iAbsentDays*mPerDaySalary,0) as "
					+ "absAmt from tbAuditSalary " +
					"where vMonthName=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') and iyear=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"')" +
					" and vDepartmentID='"+cmbDepartment.getValue().toString()+"' and vSectionId='"+cmbSection.getValue().toString()+"' and vEmployeeType='"+cmbEmpType.getValue().toString()+"' order by vSectionID,vEmployeeCode";
			rptName="rptAuditMonthlySalary.jasper";
		}

		if(queryValueCheck(query))
		{
			try
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";

					String fname = "Audit_Monthly_Salary_casual.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					File inFile; 
					String header[]=new String[0];
					String reportName = "Employee Salary("+cmbEmpType.getValue().toString()+")       Department Name:"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"      Section Name:"+cmbSection.getItemCaption(cmbSection.getValue())+"    Salary For:"+dMonthFormat.format(dSalaryMonth.getValue());
					String detailQuery[]=new String[1];
					String GroupQuery[]=new String[1];
					String signatureOption [] = {"Prepared By HR Officer","Checked By HR Executive","Manager (HR & Admin)","Manager (Accounts & Finance)","Approved By"};
					int rowWidth=0;
					if(cmbEmpType.getValue().toString().equalsIgnoreCase("Casual") || cmbEmpType.getValue().toString().equalsIgnoreCase("Temporary"))
					{	
						inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/RptAuditMonthlySalaryCasual.xls");
						detailQuery[0]="select distinct vEmployeeCode,vEmployeeName,"
								+ "vDesignationName,cast(mGross as float) mGross,dJoinDate,iPresentDays,iAbsentDays,cast(mPerDaySalary as float) mPerDaySalary,"
								+ "cast(mGross-ROUND(iAbsentDays*mPerDaySalary,0) as float) as salaryAmt,iExtraOT,convert(varchar,itotalOTHour)+':'+convert(varchar,iTotalOTMin) totalOT,"
								+ "cast(mOTRate as varchar) mOTRate,cast(round(itotalOTHour*mOTRate+(mOTRate/60*iTotalOTMin),0) as float) as otAmt,"
								+ "cast(mAttendanceBonus as float) mAttendanceBonus,cast(mAdvanceSalary as float) mAdvanceSalary,0 as TotalDeduction,''TotalAmount,mAdjust,cast((mGross-ROUND(iAbsentDays*mPerDaySalary,0)+round(itotalOTHour*mOTRate+(mOTRate/60*iTotalOTMin),0)+mAttendanceBonus) as float) PayableAmount,''Signature,''Remarks from "
								+ "tbAuditSalary where vMonthName=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') " +
								"and iyear=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"')  and vDepartmentID='"+cmbDepartment.getValue().toString()+"' and vSectionID='"+cmbSection.getValue().toString()+"' " +
								"and vEmployeeType='"+cmbEmpType.getValue().toString()+"' order by vEmployeeCode";
						rowWidth=22;
						new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "Monthly_Salary_Casual", 
								reportName, 2, GroupQuery, 2, detailQuery, rowWidth, 6,signatureOption);
					}

					else
					{
						inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/RptAuditMonthlySalaryPermanent.xls");
						detailQuery[0]="select distinct vEmployeeCode,vEmployeeName,"
								+ "vDesignationName,cast(mGross as float) mGross,dJoinDate,iPresentDays,"
								+ "iAbsentDays,iLeaveDays,cast(mBasic as float) mBasic,cast(mHouseRent as float) mHouseRent,cast(mConveyance as float) mConveyance,cast(mMedicalAllowance as float) mMedicalAllowance,"
								+ "cast(mAttendanceBonus as float) mAttendanceBonus,0 as FridayAllowance,cast(round(mGross+mAttendanceBonus,0) as float) as subTotal,"
								+ "cast(round(iAbsentDays,0) as float) as absAmt,cast(mAdvanceSalary as float) mAdvanceSalary,cast(mIncomeTax as float) mIncomeTax,"
								+ "cast(mInsurance as float) mInsurance,cast(mInsurance+mIncomeTax as float) as TotalDeduction,cast(mAdjust as float) mAdjust,"
								+ "cast((mAttendanceBonus+mGross-mInsurance-mIncomeTax) as float) PayableAmount,"
								+ "'' Signature,''Remarks from tbAuditSalary " +
								"where vMonthName=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') and iyear=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"')" +
								" and vSectionId='"+cmbSection.getValue().toString()+"' and vEmployeeType='"+cmbEmpType.getValue().toString()+"' order by vEmployeeCode";
						rowWidth=25;
						new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "Monthly_Salary_Permanent", 
								reportName, 2, GroupQuery, 2, detailQuery, rowWidth, 7,signatureOption);
					}

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
					hm.put("month",dMonthFormat.format(dSalaryMonth.getValue()));
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());

					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
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
		setHeight("270px");

		// lblSalaryMonth
		lblSalaryMonth = new Label("Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("100.0%");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth,"top:10.0px; left:20.0px;");

		// dSalaryMonth
		dSalaryMonth = new PopupDateField();
		dSalaryMonth.setImmediate(true);
		dSalaryMonth.setWidth("140px");
		dSalaryMonth.setHeight("-1px");
		dSalaryMonth.setDateFormat("MMMMM-yyyy");
		dSalaryMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dSalaryMonth.setValue(new java.util.Date());
		mainLayout.addComponent(dSalaryMonth, "top:08.0px; left:130.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:40.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:70.0px; left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:68.0px; left:130.0px;");

		//lblEmpType
		lblEmpType=new Label("Employee Type : ");
		mainLayout.addComponent(lblEmpType, "top:100.0px;left:20.0px;");

		//cmbEmpType
		cmbEmpType=new ComboBox();
		cmbEmpType.setImmediate(true);
		cmbEmpType.addItem("Permanent");
		cmbEmpType.addItem("Temporary");
		cmbEmpType.addItem("Provisionary");
		cmbEmpType.addItem("Casual");
		cmbEmpType.setWidth("200px");
		cmbEmpType.setHeight("-1px");
		mainLayout.addComponent(cmbEmpType, "top:98.0px;left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:130.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:150.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:170.opx; left:140.0px");
		return mainLayout;
	}
}