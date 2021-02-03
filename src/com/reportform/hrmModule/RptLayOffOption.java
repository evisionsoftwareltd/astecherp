package com.reportform.hrmModule;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
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
public class RptLayOffOption extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private Label lblSection;

	private PopupDateField dMonth;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private SimpleDateFormat fMonth = new SimpleDateFormat("MM");
	private SimpleDateFormat fMonth2 = new SimpleDateFormat("MMMMM-yy");
	private SimpleDateFormat fYear = new SimpleDateFormat("yyyy");

	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	public RptLayOffOption(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("LAY OFF REPORT :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentDataLoad();
		setEventAction();
	}

	private void cmbDepartmentDataLoad()
	{

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql=" select distinct vDepartmentID,vDepartmentName  " +
					"from tbLayOff where vApproveFlag='1' and MONTH(dFromDate)='"+fMonth.format(dMonth.getValue())+"' and YEAR(dFromDate)='"+fYear.format(dMonth.getValue())+"' "
					;
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	public void cmbSectionAddData()
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vSectionId,vSectionName from tbLayOff where vApproveFlag='1' " +
					" and vDepartmentID='"+cmbDepartment.getValue().toString()+"'";
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
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
					cmbDepartmentDataLoad();
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
				if(cmbSection.getValue()!=null)
				{
					reportShow();
				}
				else
				{
					showNotification("Warniung!","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
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
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			/*String query1=" select distinct lo.vDepartmentName,lo.vSectionName,tlo.vEmployeeCode," +
					"lo.vEmployeeName,ISNULL(COUNT(distinct tlo.dLayOffDate),0) totalDays,lo.mPerDayRate" +
					" from tbLayOff lo inner join tbTempLayOff tlo on lo.vEmployeeID = tlo.vEmployeeID " +
					"where MONTH(tlo.dLayOffDate) = '"+fMonth.format(dMonth.getValue())+"' and YEAR(tlo.dLayOffDate) = '"+fYear.format(dMonth.getValue())+"'" +
					" and lo.vApproveFlag = 1 and vSectionID='"+cmbSection.getValue().toString()+"'" +
					" Group by lo.vDepartmentName,lo.vSectionName,tlo.vEmployeeCode,lo.vEmployeeName,lo.mPerDayRate order by tlo.vEmployeeCode";*/

			String query1="select distinct lo.vEmployeeCode,lo.vEmployeeName,vDepartmentName,vSectionName,ISNULL((select COUNT(dLayOffDate) " +
					"from tbTempLayOff where MONTH(dLayOffDate) = '"+fMonth.format(dMonth.getValue())+"' and YEAR(dLayOffDate) = '"+fYear.format(dMonth.getValue())+"' " +
					"and vEmployeeID = lo.vEmployeeID),0) totalDays,lo.mPerDayRate," +
					"(ISNULL((select COUNT(dLayOffDate) from tbTempLayOff where MONTH(dLayOffDate) = '"+fMonth.format(dMonth.getValue())+"' " +
					"and YEAR(dLayOffDate) = '"+fYear.format(dMonth.getValue())+"' and vEmployeeID = lo.vEmployeeID),0)*lo.mPerDayRate) " +
					"totalAMT,'' vSignature,'' vRemarks from tbLayOff lo where MONTH(dFromDate) = '"+fMonth.format(dMonth.getValue())+"' " +
					"and YEAR(dFromDate) = '"+fYear.format(dMonth.getValue())+"' and lo.vSectionID='"+cmbSection.getValue().toString()+"' order by lo.vEmployeeCode";
			
			System.out.println("Waqja"+1);
			if(queryValueCheck(query1))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "lay_off.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;
					
					String header[]=new String[2];
					header [0] = "Month : "+fMonth2.format(dMonth.getValue());
					header [1] = "Department Name : "+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"     Section Name : "+cmbSection.getItemCaption(cmbSection.getValue())+"";
					String reportName = "LAYOFF REPORT";
					String detailQuery[]=new String[1];
					String GroupQuery[]=new String[1];
					String [] signatureOption = new String [0];
					
					detailQuery[0]=" select distinct lo.vEmployeeCode,lo.vEmployeeName,ISNULL((select COUNT(dLayoffDate) " +
					"from tbTempLayOff where MONTH(dLayoffDate) = '"+fMonth.format(dMonth.getValue())+"' and YEAR(dLayoffDate) = '"+fYear.format(dMonth.getValue())+"' " +
					"and vEmployeeID = lo.vEmployeeID),0) totalDays,cast(lo.mPerDayRate as int) perday," +
					"cast((ISNULL((select COUNT(dLayOffDate) from tbTempLayOff where MONTH(dLayoffDate) = '"+fMonth.format(dMonth.getValue())+"' " +
					"and YEAR(dLayoffDate) = '"+fYear.format(dMonth.getValue())+"' and vEmployeeID = lo.vEmployeeID),0)*lo.mPerDayRate)as float) " +
					"totalAMT,'' vSignature,'' vRemarks from tbLayOff lo where MONTH(dFromDate) = '"+fMonth.format(dMonth.getValue())+"' " +
					"and YEAR(dFromDate) = '"+fYear.format(dMonth.getValue())+"' and lo.vSectionID='"+cmbSection.getValue().toString()+"' order by lo.vEmployeeCode";

					File inFile;
					inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/MonthWiseLayOffSheet.xls");

					int rowWidth=3;
					new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "LayOffSheet", 
							reportName, 2, GroupQuery, 2, detailQuery, rowWidth,7,signatureOption);

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
					hm.put("month",fMonth2.format(dMonth.getValue()));
					hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql",query1);

					ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
					Window win = new ReportViewer(hm,"report/account/hrmModule/rptLayOffSalarySheet.jasper",
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

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("460px");
		setHeight("220px");

		// lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:10.0px; left:20.0px;");

		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("100px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:08.0px; left:130.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:40.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Section Name : ");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:70.0px; left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:68.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:100.0px;left:130.0px;");

		mainLayout.addComponent(new Label("______________________________________________________________________"), "top:120.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:140.opx; left:140.0px");
		return mainLayout;
	}
}
