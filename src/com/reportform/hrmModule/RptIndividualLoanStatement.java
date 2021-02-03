package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptIndividualLoanStatement extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblEmployeeName;
	private ComboBox cmbEmployeeName;

	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;

	private Label lblLoanType;
	private ComboBox cmbLoanType;
	private static final String[] loanType = new String[] {"General Loan","Salary Loan","Others"};

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");

	public RptIndividualLoanStatement(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("INDIVIDUAL LOAN STATEMENT :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbAddEmployeeName();
		setEventAction();
	}

	private void setEventAction()
	{
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbEmployeeName.getValue()!=null)
				{
					if(dFromDate.getValue()!=null && dToDate.getValue()!=null)
					{
						if(cmbLoanType.getValue()!=null)
						{
							reportpreview();
						}
						else
						{
							showNotification("Warning","Select Loan Type",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Date Range",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
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

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				cmbAddEmployeeName();
			}
		});
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("420px");
		setHeight("250px");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:20.0px; left:50.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("100.0%");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName,"top:50.0px; left:30.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("220px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:48.0px; left:135.0px;");

		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:80.0px; left:30.0px;");

		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:78.0px; left:135.0px;");

		// lblToDate
		lblToDate = new Label("To");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate,"top:80.0px; left:250.0px;");

		// dToDate
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:78.0px; left:268.0px;");

		// lblLoanType
		lblLoanType = new Label("Loan Type :");
		lblLoanType.setImmediate(false);
		lblLoanType.setWidth("100.0%");
		lblLoanType.setHeight("-1px");
		mainLayout.addComponent(lblLoanType,"top:110.0px; left:30.0px;");

		// cmbLoanType
		cmbLoanType = new ComboBox();
		cmbLoanType.setImmediate(true);
		cmbLoanType.setWidth("150px");
		cmbLoanType.setHeight("-1px");
		cmbLoanType.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbLoanType, "top:108.0px; left:135.0px;");
		for(int i=0; i<loanType.length;i++)
		{cmbLoanType.addItem(loanType[i]);}

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:140.0px;left:135.0px;");
		mainLayout.addComponent(cButton,"top:170.opx; left:130.0px");
		return mainLayout;
	}

	private void cmbAddEmployeeName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String querySection = " Select La.vAutoEmployeeId, ein.employeeCode from tbLoanApplication La inner join " +
					"tbEmployeeInfo ein on ein.vEmployeeId= La.vAutoEmployeeID where La.vDepartmentId!='DEPT10' order by ein.employeeCode ";
			lblEmployeeName.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				querySection = "  Select La.vAutoEmployeeId, ein.vEmployeeName from tbLoanApplication La inner join " +
						"tbEmployeeInfo ein on ein.vEmployeeId= La.vAutoEmployeeID where La.vDepartmentId!='DEPT10' order by ein.employeeCode ";
				lblEmployeeName.setValue("Employee Name :");
			}

			else if(opgEmployee.getValue()=="Proximity ID")
			{
				querySection = " Select La.vAutoEmployeeId, ein.vProximityID from tbLoanApplication La inner join " +
						"tbEmployeeInfo ein on ein.vEmployeeId= La.vAutoEmployeeID where La.vDepartmentId!='DEPT10' order by ein.employeeCode ";
				lblEmployeeName.setValue("Proximity ID :");
			}

			List <?> list = session.createSQLQuery(querySection).list();	
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbAddEmployeeName",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}	

	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String loanType= "";

		String fromDate = dDbFormat.format(dFromDate.getValue())+ " 00:00:00";
		String toDate = dDbFormat.format(dToDate.getValue())+ " 23:59:59";

		loanType= cmbLoanType.getValue().toString();

		try
		{
			String query = " select * from  funIndividualLoanStatementNew('"+cmbEmployeeName.getValue().toString()+"','"+fromDate+"','"+toDate+"','"+loanType+"') ";

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("fromDate", dFromDate.getValue());
				hm.put("toDate", dToDate.getValue());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptIndividualLoanStatement.jasper",
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
			showNotification("reportpreview",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
}
