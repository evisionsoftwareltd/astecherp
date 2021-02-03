package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptPerformanceEvaluation extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private ComboBox cmbDynamic;
	private PopupDateField dFromDate,dToDate;
	private OptionGroup radioButtonGroup;

	private Label lblComboLabel;
	private CommonButton cButton= new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private static final List<String> groupButton=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private static final List<String> lstGroup=Arrays.asList(new String[]{"Management","Non-Management"});
	private OptionGroup type;
	private ReportDate reportTime = new ReportDate();
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");

	public RptPerformanceEvaluation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("PERFORMANCE EVALUATION :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setBtnAction();
		setContent(mainLayout);
		initialCombo();
	}

	private void setBtnAction()
	{
		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbDynamic.getValue()!=null)
				{
					if(dFromDate.getValue()!=null)
					{
						if(dToDate.getValue()!=null)
						{
							reportView();
						}
						else
						{
							showNotification("Warning","Please Select To Date!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Please Select From Date!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					getParent().showNotification("Please Select "+lblComboLabel.getValue().toString()+"", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		radioButtonGroup.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(event.getProperty().toString()=="Employee ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					lblComboLabel.setValue("Employee ID :");
					selectCardNo();
				}
				else if(event.getProperty().toString()=="Proximity ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					lblComboLabel.setValue("Proximity ID :");
					selectProximityId();
				}
				else if(event.getProperty().toString()=="Employee Name")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					lblComboLabel.setValue("Employee Name :");
					selectName();
				}
			}
		});
	}

	private void selectCardNo()
	{
		cmbDynamic.removeAllItems();
		String query = " select vEmployeeId,employeeCode from tbEmployeeInfo where ISNULL(vProximityID,'')!='' " +
				"order by CAST(SUBSTRING(vEmployeeId,5,LEN(vEmployeeId)) as Int) asc ";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();		
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDynamic.addItem(element[0].toString());
				cmbDynamic.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("selectCardNo", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void selectProximityId()
	{
		cmbDynamic.removeAllItems();
		String query = "select vEmployeeId,vProximityId from tbEmployeeInfo where ISNULL(vProximityID,'')!='' order by CAST(SUBSTRING(vEmployeeId,5,LEN(vEmployeeId)) as Int) asc";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();		
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDynamic.addItem(element[0].toString());
				cmbDynamic.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("selectCardNo", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void selectName()
	{
		cmbDynamic.removeAllItems();
		String query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo where ISNULL(vProximityID,'')!='' " +
				"order by CAST(SUBSTRING(vEmployeeId,5,LEN(vEmployeeId)) as Int) asc";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();		
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDynamic.addItem(element[0].toString());
				cmbDynamic.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("selectCardNo", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("400px");
		setHeight("280px");

		type=new OptionGroup("",lstGroup);
		type.setImmediate(true);
		type.setStyleName("horizontal");
		type.select("Management");
		mainLayout.addComponent(new Label("Type : "), "top:10.0px; left:30.0px;");
		mainLayout.addComponent(type, "top:10.0px; left:130.0px;");

		// optionGroup
		radioButtonGroup = new OptionGroup("",groupButton);
		radioButtonGroup.setImmediate(true);
		radioButtonGroup.setStyleName("horizontal");
		radioButtonGroup.setValue("Employee ID");
		mainLayout.addComponent(radioButtonGroup, "top:35.0px; left:40.0px;");

		//ComboLabel
		lblComboLabel=new Label("Employee ID :");
		lblComboLabel.setImmediate(true);
		lblComboLabel.setWidth("100px");
		mainLayout.addComponent(lblComboLabel, "top:60.0px; left:30.0px;");

		// comboBox_1
		cmbDynamic = new ComboBox();
		cmbDynamic.setImmediate(true);
		cmbDynamic.setWidth("225px");
		cmbDynamic.setHeight("-1px");
		cmbDynamic.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDynamic, "top:58.0px; left:130.0px;");

		dFromDate=new PopupDateField();
		dFromDate.setWidth("110px");
		dFromDate.setHeight("22px");
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setImmediate(true);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new Date());
		mainLayout.addComponent(new Label("From Date : "), "top:90.0px; left:30.0px;");
		mainLayout.addComponent(dFromDate, "top:88.0px; left:130.0px;");

		dToDate=new PopupDateField();
		dToDate.setWidth("110px");
		dToDate.setHeight("22px");
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setImmediate(true);
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new Date());
		mainLayout.addComponent(new Label("To Date : "), "top:120.0px; left:30.0px;");
		mainLayout.addComponent(dToDate, "top:118.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:150.0px;left:160.0px;");

		cButton.btnPreview.setImmediate(true);
		mainLayout.addComponent(new Label("______________________________________________________________________________"), "top:170.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton, "top:190.0px; left:120.0px;");

		return mainLayout;
	}

	private void initialCombo()
	{
		cmbDynamic.removeAllItems();
		String query = "select vEmployeeId,employeeCode from tbEmployeeInfo where ISNULL(vProximityID,'')!='' order by employeeCode";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();		
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDynamic.addItem(element[0].toString());
				cmbDynamic.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("selectCardNo", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportView()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query = "select ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vDesignationId," +
					"ein.vSectionId,din.designationName,si.SectionName,ISNULL((select vNameOfExam from tbEducation " +
					"ed where ed.vEmployeeId=ein.vEmployeeId and ed.iSerialNo=(select MAX(iSerialNo) from tbEducation " +
					"ed where ed.vEmployeeId=ein.vEmployeeId)),'') vHighestDegree,ein.dInterviewDate,ein.dJoiningDate " +
					"from tbEmployeeInfo ein inner join tbDesignationInfo din on ein.vDesignationId=din.designationId " +
					"inner join tbSectionInfo si on si.vSectionId=ein.vSectionId Where ein.vEmployeeId ='"+cmbDynamic.getValue().toString()+"' order by ein.employeeCode";
			System.out.println(query);

			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("fromDate", dateFormat.format(dFromDate.getValue()));
			hm.put("toDate", dateFormat.format(dToDate.getValue()));
			hm.put("sql", query);

			String rptName="";
			if(type.getValue().toString().equals("Management"))
			{
				rptName="RptPerformanceEvaluationManagement.jasper";
			}

			else
			{
				rptName="RptPerformanceEvaluationNonManagement.jasper";
			}

			Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("reportView",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
