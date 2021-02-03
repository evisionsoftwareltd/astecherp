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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptMonthWiseOt extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private Label lblSection;
	private Label lblEmpType;

	private PopupDateField dMonth;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private ComboBox cmbEmpType;
	
	private CheckBox chkAll;

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat yearMonthFormat = new SimpleDateFormat("MMMMM-yyyy");
	
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	public RptMonthWiseOt(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTH WISE OT :: "+sessionBean.getCompany());
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
			List <?> list=session.createSQLQuery("select distinct vDepartmentID,vDepartmentName from tbSalary where " +
					"vMonthName=DateName(MM,'"+dateformat.format(dMonth.getValue())+"') and " +
					"year='"+yearFormat.format(dMonth.getValue())+"' and iTotalOTHour>0 and  vDepartmentID !='DEPT10' order by " +
					"vDepartmentName").list();
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
			List <?> list=session.createSQLQuery("select distinct SectionID,Section from tbSalary where " +
					"vDepartmentID='"+cmbDepartment.getValue()+"' and vMonthName=DateName(MM,'"+dateformat.format(dMonth.getValue())+"') and " +
					"year='"+yearFormat.format(dMonth.getValue())+"' and iTotalOTHour>0 order by Section").list();
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
	
	public void cmbEmpTypeAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list=session.createSQLQuery("select distinct 0,empType from tbSalary where " +
					"vDepartmentID='"+cmbDepartment.getValue()+"' and SectionID='"+cmbSection.getValue()+"' and vMonthName=DateName(MM,'"+dateformat.format(dMonth.getValue())+"') and " +
					"year='"+yearFormat.format(dMonth.getValue())+"' and iTotalOTHour>0").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmpType.addItem((String) element[1]);
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
					cmbSectionAddData();
				}
			}
		});
		
		cmbSection.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				cmbEmpType.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmpTypeAddData();
				}
				
			}
		});
		
		chkAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAll.booleanValue())
				{
					cmbEmpType.setValue(null);
					cmbEmpType.setEnabled(false);
				}
				else
				{
					cmbEmpType.setEnabled(true);
				}
			}
		});
		

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null)
				{
					getAllData();
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

	private void getAllData()
	{
		String sectionValue = "";
		String empType="%";
		if(cmbEmpType.getValue()!=null)
		{
			empType= cmbEmpType.getValue().toString().trim();
		}
		sectionValue = cmbSection.getValue().toString();
		reportShow(sectionValue);
	}

	private void reportShow(Object Section)
	{
		String empType="%";
		if(cmbEmpType.getValue()!=null)
		{
			empType= cmbEmpType.getValue().toString().trim();
		}
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query="select ts.year,ts.vMonthName,ts.empId,ts.empName,ts.designation,ts.Gross,ts.FridayAllowance," +
					"ts.itotalFridayOTHour,ts.itotalFridayOTMin,ts.iTotalNormalOTHour,ts.iTotalNormalOTMin,ts.otRate,ts.vDepartmentName," +
					"ts.Section,ts.itotalOTHour,ts.itotalOTMin,ts.iExtraOT,ts.iLessOT from tbSalary ts inner join tbEmployeeInfo " +
					"ein on ein.vEmployeeId=ts.autoEmployeeID where ein.OtStatus=1 and " +
					"ts.year= year('"+dateformat.format(dMonth.getValue())+"') and " +
					"ts.vMonthName= DateName(mm,'"+dateformat.format(dMonth.getValue())+"') and ts.vDepartmentID=" +
					"'"+cmbDepartment.getValue()+"' and ts.SectionID='"+cmbSection.getValue()+"' and ts.empType like '"+empType+"' order by ts.empId";

			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";

					String fname = "Month_Wise_OT.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					File inFile; 
					String header[]=new String[3];
					String reportName = "SECTION WISE MONTHLY OT";
					String detailQuery[]=new String[1];
					String GroupQuery[]=new String[1];
					String signatureOption [] = {"Prepared By HR Officer","Checked By HR Executive","Manager (HR & Admin)","Manager (Accounts & Finance)","Approved By"};
					int rowWidth=0;
					header[0]="Department Name : "+cmbDepartment.getItemCaption(cmbDepartment.getValue());
					header[1]="Section Name : "+cmbSection.getItemCaption(cmbSection.getValue());
					header[2]="Month : "+yearMonthFormat.format(dMonth.getValue());

					inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/MonthWiseOT.xls");

					detailQuery[0]=	" select ts.empId,ts.empName,ts.designation,cast(ts.Gross as float) Gross," +
							" ts.FridayAllowance,cast(ts.itotalFridayOTHour as varchar)+':'+cast(ts.iTotalFridayOTMin as " +
							" varchar) totalFridayOT,round(cast((itotalFridayOTHour*otRate)+(otRate/60)*itotalFridayOTMin as" +
							" float),0)  fridayOTAmount,cast(ts.iTotalNormalOTHour as varchar)+':'+cast(ts.iTotalNormalOTMin " +
							" as varchar) totalNormalOT,cast(ts.otRate as varchar) otRate," +
							"cast((select count(*) from tbIdoubleShift where MONTH(dDate)=MONTH('"+dateformat.format(dMonth.getValue())+"') and YEAR(dDate)=YEAR('"+dateformat.format(dMonth.getValue())+"') and vEmployeeID=ts.autoEmployeeID) as varchar) iShift," +
							"round(cast((iTotalNormalOTHour*otRate)" +
							" +otRate/60*iTotalNormalOTMin as float),0) otAmt,ts.iExtraOT,ts.iLessOT,cast(ts.itotalOTHour as varchar)+':'+" +
							" CAST(ts.iTotalOTMin as varchar),round(cast((itotalOTHour*otRate)+otRate/60*itotalOTMin as float)," +
							" 0) totalOT,0 Extra,round(cast((itotalOTHour*otRate)+otRate/60*itotalOTMin as float),0) payableAmt," +
							" '' Signature,'' Remarks from tbSalary ts inner join tbEmployeeInfo ein on" +
							" ein.vEmployeeId=ts.autoEmployeeID where ein.OtStatus=1 and " +
							" ts.year= year('"+dateformat.format(dMonth.getValue())+"') and " +
							" ts.vMonthName= DateName(mm,'"+dateformat.format(dMonth.getValue())+"') and " +
							" ts.vDepartmentID='"+cmbDepartment.getValue()+"' and " +
							" ts.SectionID='"+Section+"' and ts.empType like '"+empType+"' order by ts.empId";

					rowWidth=10;
					new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "Section_Wise_Monthly_OT", 
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
					hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthlyOTPermanent.jasper",
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
		catch(Exception exp){
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);}
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
		setHeight("260px");

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
		
		// lblSection
		lblEmpType = new Label("Employee Type : ");
		lblEmpType.setImmediate(false);
		lblEmpType.setWidth("100.0%");
		lblEmpType.setHeight("-1px");
		mainLayout.addComponent(lblEmpType,"top:100.0px; left:20.0px;");

		// cmbSection
		cmbEmpType = new ComboBox();
		cmbEmpType .setWidth("100px");
		cmbEmpType .setHeight("-1px");
		cmbEmpType .setNullSelectionAllowed(true);
		cmbEmpType .setImmediate(true);
		mainLayout.addComponent(cmbEmpType , "top:98.0px; left:130.0px;");
		
		chkAll=new CheckBox("All");
		chkAll.setImmediate(true);
		mainLayout.addComponent(chkAll, "top:96.0px; left:235.0px;");
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:120.0px;left:130.0px;");

		mainLayout.addComponent(new Label("______________________________________________________________________"), "top:130.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:150.opx; left:140.0px");
		return mainLayout;
	}
}
