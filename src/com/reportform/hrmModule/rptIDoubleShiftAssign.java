package com.reportform.hrmModule;

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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class rptIDoubleShiftAssign extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private Label lblDepartment;
	private Label lblSection;

	private ComboBox cmbDate;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartment;

	private ComboBox cmbSection;
	private CheckBox chkSection;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dformat = new SimpleDateFormat("dd-MM-yyyy");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public rptIDoubleShiftAssign(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("I DOUBLE SHIFT ASSIGN :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		cmbDateDataAdd();
		setContent(mainLayout);
		setEventAction();
	}

	public void setEventAction()
	{

		cmbDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(cmbDate.getValue()!=null)
				{
					CmbDepartmentDataAdd(cmbDate.getValue().toString());
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

		chkDepartment.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkDepartment.booleanValue()==true)
				{
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
					cmbSectionAddData("%");
				}
				else
				{
					cmbDepartment.setEnabled(true);
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
				formValidation();
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

	public void cmbDateDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query="select distinct dDate newDate,dDate from tbIdoubleShift order by dDate desc";
			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDate.addItem(element[0]);
				cmbDate.setItemCaption(element[0], dformat.format(element[1]));
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDateDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

		finally
		{
			session.close();
		}
	}

	public void CmbDepartmentDataAdd(String strDate)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbIdoubleShift where " +
					"dDate= '"+strDate+"' order by vDepartmentName";
			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("CmbDepartmentDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
			String query="select distinct vSectionId,vSectionName,vDepartmentName from tbIDoubleShift where " +
					"dDate='"+dateformat.format(cmbDate.getValue())+"' and vdepartmentId like '"+DepartmentID+"' " +
					"order by vSectionName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0],  element[2].toString() + "(" + element[1].toString() + ")");
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

	private void formValidation()
	{

		if(cmbDepartment.getValue()!=null || chkDepartment.booleanValue()==true)

		{
			if(cmbSection.getValue()!=null || chkSection.booleanValue()==true)
			{										
				getAllData();												
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

	private void getAllData()
	{
		String departmentValue = "";
		String sectionValue = "";
		

		if(chkDepartment.booleanValue()==true){
			departmentValue = "%";}
		else{
			departmentValue = cmbDepartment.getValue().toString();}

		if(chkSection.booleanValue()==true){
			sectionValue = "%";}
		else{
			sectionValue = cmbSection.getValue().toString();}

		reportShow(departmentValue,sectionValue);
	}


	private void reportShow(String departmentValue,String sectionValue)
	{
		String status="%";
		if(RadioBtnStatus.getValue().equals("Active"))
		{
			status="1";
		}
		else if(RadioBtnStatus.getValue().equals("Left"))
		{
			status="0";
		}
		else
		{
			status="%";
		}
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query="select IDS.dDate,ein.employeeCode,IDS.vProximityId,IDS.vEmployeeName,IDS.vDepartmentId,IDS.vDepartmentName,IDS.vSectionId,IDS.vSectionName,IDS.vDesignationId," +
					"IDS.vDesignationName from tbIdoubleShift IDS inner join tbEmployeeInfo ein on IDS.vEmployeeID=ein.vEmployeeId where " +
					"IDS.dDate='"+dateformat.format(cmbDate.getValue())+"' and IDS.vDepartmentId like '"+departmentValue+"' and IDS.vDepartmentId not like 'DEPT10' and IDS.vSectionId like '"+sectionValue+"' and ein.iStatus like '"+status+"'  order by IDS.vSectionName,ein.employeeCode";

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptIDoubleshiftEnable.jasper",
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

		finally
		{
			session.close();
		}
		return false;
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("460px");
		setHeight("280px");

		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:30.0px; left:30.0px;");

		cmbDate = new ComboBox();
		cmbDate.setImmediate(true);
		cmbDate.setWidth("110px");
		cmbDate.setHeight("-1px");
		mainLayout.addComponent(cmbDate, "top:28.0px; left:130.0px;");

		lblDepartment = new Label("Department :");
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment,"top:60.0px; left:30.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:58.0px; left:130.0px;");

		chkDepartment=new CheckBox("All");
		chkDepartment.setImmediate(true);
		mainLayout.addComponent(chkDepartment, "top:60.0px; left:400.0px;");

		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:90.0px; left:30.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:88.0px; left:130.0px;");

		chkSection=new CheckBox("All");
		chkSection.setImmediate(true);
		mainLayout.addComponent(chkSection, "top:90.0px; left:400.0px;");

		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:120.0px;left:165.0px;");

		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:150.0px;left:130.0px;");

		mainLayout.addComponent(new Label("__________________________________________________________________________________"), "top:180.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:200.opx; left:140.0px");
		return mainLayout;
	}
}
