package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptNetWorkingHour extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private ComboBox cmbsection;
	private ComboBox cmbDepartment;

	private Label lblEmployee;
	private ComboBox cmbEmployee;


	private Label lblMonth;
	private PopupDateField dMonth;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[  ]{"PDF","Other"});
	private static final String CHO="'DEPT10'";
	public RptNetWorkingHour(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("NET WORKING HOUR :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		addDepartmentName();
		setEventAction();
		focusMove();
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
					addDepartmentName();
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbsection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					addSectionName();
				}
			}
		});

		cmbsection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbsection.getValue()!=null)
				{
					addemployeeName();
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbsection.getValue()!=null ) 
				{
					prcExecute();
					reportShow();
				}
				else
				{
					showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
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
				addemployeeName();
			}
		});
	}

	public void addDepartmentName()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmployeeMainAttendance " +
					"where Month(dDate)=Month('"+dFormat.format(dMonth.getValue())+"') and " +
					"year(dDate)=year('"+dFormat.format(dMonth.getValue())+"') and vDepartmentId!="+CHO+" order by vDepartmentName";

			Iterator <?> itr=session.createSQLQuery(query).list().iterator();
			while(itr.hasNext())
			{
				Object [] element=(Object[])itr.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}

		catch(Exception exp)
		{
			showNotification("addDepartmentName : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addSectionName()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbEmployeeMainAttendance " +
					"where Month(dDate)=Month('"+dFormat.format(dMonth.getValue())+"') and " +
					"year(dDate)=year('"+dFormat.format(dMonth.getValue())+"') and " +
					"vDepartmentID='"+cmbDepartment.getValue()+"'  and vDepartmentId!="+CHO+" order by vSectionName";

			Iterator <?> itr=session.createSQLQuery(query).list().iterator();
			while(itr.hasNext())
			{
				Object [] element=(Object[])itr.next();
				cmbsection.addItem(element[0]);
				cmbsection.setItemCaption(element[0], element[1].toString());
			}
		}

		catch(Exception exp)
		{
			showNotification("addSectionName : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addemployeeName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "SELECT distinct vEmployeeId,employeeCode from tbEmployeeInfo " +
					"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
					"vSectionId='"+cmbsection.getValue().toString().trim()+"' and " +
					"iStatus = 1  and vDepartmentId!="+CHO+" order by employeeCode";
			lblEmployee.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "SELECT distinct vEmployeeId,vEmployeeName,employeeCode from tbEmployeeInfo " +
						"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
						"vSectionId='"+cmbsection.getValue().toString().trim()+"' and " +
						"iStatus = 1  and vDepartmentId!="+CHO+" order by employeeCode";
				lblEmployee.setValue("Employee Name :");
			}

			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "SELECT distinct vEmployeeId,vProximityID,employeeCode from tbEmployeeInfo " +
						"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
						"vSectionId='"+cmbsection.getValue().toString().trim()+"' and " +
						"iStatus = 1  and vDepartmentId!="+CHO+" order by employeeCode";
				lblEmployee.setValue("Proximity ID :");
			}
			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addemployeeName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void prcExecute()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			String query="exec prcEmployeeNetWorking '"+dFormat.format(dMonth.getValue())+"','"+cmbEmployee.getValue().toString()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'";
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
		}

		catch(Exception exp)
		{
			showNotification("prcExecute : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}


	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{	
			String query = "select vEmployeeCode,vProximityID,vEmployeeName,vEmployeeType,dJoiningDate,vDesignationName,vDepartmentName," +
					"vSectionName,vContactNo,dAttDate,dTime1,dTime2,dDuration1,dTime3,dTime4,dDuration2,dTime5,dTime6,dDuration3," +
					"dTime7,dTime8,dDuration4,dTime9,dTime10,dDuration5,dTime11,dTime12,dDuration6,dTime13,dTime14,dDuration7," +
					"dTime15,dTime16,dDuration8,dTime17,dTime18,dDuration9,dTotalDuration,vRemarks from tbEmployeeNetWorkingHour order by dAttDate";
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("month",new SimpleDateFormat("MMMMMM").format(dMonth.getValue()));
				hm.put("year",new SimpleDateFormat("yy").format(dMonth.getValue()));
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptNetworkingHour.jasper",
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
		finally{session.close();}
		return false;
	}

	private void focusMove()
	{
		allComp.add(cmbEmployee);
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
		setWidth("460px");
		setHeight("300px");

		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:30.0px; left:30.0px;");

		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("140px");
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:28.0px; left:130.0px;");

		cmbDepartment=new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260.0px");
		cmbDepartment.setHeight("24.0px");
		mainLayout.addComponent(new Label("Department :"), "top:60.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:58.0px;left:130.0px;");

		cmbsection=new ComboBox();
		cmbsection.setImmediate(true);
		cmbsection.setWidth("260.0px");
		cmbsection.setHeight("24.0px");
		mainLayout.addComponent(new Label("Section :"), "top:90.0px; left:30.0px;");
		mainLayout.addComponent(cmbsection, "top:88.0px;left:130.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:120.0px; left:50.0px;");

		// lblCategory
		lblEmployee = new Label();
		lblEmployee.setImmediate(false);
		lblEmployee.setWidth("100.0%");
		lblEmployee.setHeight("-1px");
		lblEmployee.setValue("Employee ID :");
		mainLayout.addComponent(lblEmployee,"top:150.0px; left:30.0px;");

		// cmbEmployee
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(false);
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:148.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:180.0px;left:130.0px;");

		mainLayout.addComponent(new Label("______________________________________________________________________________"), "top:200.0px; left:20.0px; right:20.0px;");
		mainLayout.addComponent(cButton,"top:230.opx; left:150.0px");
		return mainLayout;
	}
}
