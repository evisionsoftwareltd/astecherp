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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptMonthlyAttendanceSummaryRegister extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private ComboBox cmbDepartment;
	private ComboBox cmbsection;
	private CheckBox chkSectionAll;

	private Label lblEmployee;
	private ComboBox cmbEmployee;
	private CheckBox chkempall;

	private Label lblMonth;
	private PopupDateField dMonth;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private static final String CHO="'DEPT10'";
	public RptMonthlyAttendanceSummaryRegister(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("MONTHLY EMPLOYEE ATTENDANCE SUMMARY REGISTER :: "+sessionBean.getCompany());
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
					addemployeeName(cmbsection.getValue().toString());
				}
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(chkSectionAll.booleanValue())
				{
					cmbsection.setValue(null);
					cmbsection.setEnabled(false);
					addemployeeName("%");
				}
				else
				{
					cmbsection.setEnabled(true);
				}
			}
		});

		chkempall.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkempall.booleanValue())
				{
					cmbEmployee.setValue(null);
					cmbEmployee.setEnabled(false);
				}
				else
					cmbEmployee.setEnabled(true);
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbsection.getValue()!=null || chkSectionAll.booleanValue()) 
					{
						String section = "%";
						String employeeName="%";
						if(cmbsection.getValue()!=null)
						{
							section=cmbsection.getValue().toString();
						}
						if(cmbEmployee.getValue()!=null)
						{
							employeeName = cmbEmployee.getValue().toString(); 
						}

						reportShow(section,employeeName);
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

	public void addDepartmentName()
	{
		cmbDepartment.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					"tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where ein.vDepartmentId!="+CHO+" order by dept.vDepartmentName";

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
			showNotification("addDepartmentName",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addSectionName()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ein.vSectionId,sein.vDepartmentName,sein.SectionName from tbSectionInfo " +
					"sein inner join tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where " +
					"ein.vDepartmentID= '"+cmbDepartment.getValue()+"' and ein.vDepartmentId!="+CHO+" order by sein.vDepartmentName,sein.SectionName";

			Iterator <?> itr=session.createSQLQuery(query).list().iterator();
			while(itr.hasNext())
			{
				Object [] element=(Object[])itr.next();
				cmbsection.addItem(element[0]);
				cmbsection.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("addSectionName",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addemployeeName(String Section)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery("SELECT vEmployeeId,employeeCode from tbEmployeeInfo where " +
					"vDepartmentID='"+cmbDepartment.getValue()+"' and vDepartmentId!="+CHO+" and vSectionId like '"+Section+"' order by employeeCode").list();
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

	private void reportShow(String sectionName,String employeeName)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query="select dMonthYear,autoEmpID,empId,empCode,empName,DepartmentID,Department,sectionId,Section,designationID,designation," +
					"designationSerial,iDayCount,Day01,Day02,Day03,Day04,Day05,Day06,Day07,Day08,Day09,Day10,Day11,Day12,Day13,Day14,Day15," +
					"Day16,Day17,Day18,Day19,Day20,Day21,Day22,Day23,Day24,Day25,Day26,Day27,Day28,Day29,Day30,Day31,iTotalPresentDays," +
					"iTotalLeaveDays,iTotalHolidays,iTotalAbsentDays from funCalcMonthlyAttendanceSummaryRegister" +
					"('"+dFormat.format(dMonth.getValue())+"','"+employeeName+"','"+cmbDepartment.getValue()+"','"+sectionName+"') where DepartmentID!="+CHO+" "+
					"order by Section,empID";

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

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptMonthlyAttendanceSummaryRegister.jasper",
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
			showNotification("reportShow"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
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
		setHeight("250px");

		lblMonth = new Label("Month :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:10.0px; left:30.0px;");

		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("140px");
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:08.0px; left:130.0px;");
		
		cmbDepartment=new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260.0px");
		cmbDepartment.setHeight("24.0px");
		mainLayout.addComponent(new Label("Department :"), "top:40.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38.0px;left:130.0px;");

		cmbsection=new ComboBox();
		cmbsection.setImmediate(true);
		cmbsection.setWidth("260.0px");
		cmbsection.setHeight("24.0px");
		mainLayout.addComponent(new Label("Section :"), "top:70.0px; left:30.0px;");
		mainLayout.addComponent(cmbsection, "top:68.0px;left:130.0px;");

		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:70.0px; left:395.0px;");

		// lblCategory
		lblEmployee = new Label();
		lblEmployee.setImmediate(false);
		lblEmployee.setWidth("100.0%");
		lblEmployee.setHeight("-1px");
		lblEmployee.setValue("Employee ID :");
		mainLayout.addComponent(lblEmployee,"top:100.0px; left:30.0px;");

		// cmbEmployee
		cmbEmployee = new ComboBox();
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setImmediate(true);
		mainLayout.addComponent(cmbEmployee, "top:98.0px; left:130.0px;");

		//sectionAll
		chkempall = new CheckBox("All");
		chkempall.setHeight("-1px");
		chkempall.setWidth("-1px");
		chkempall.setImmediate(true);
		mainLayout.addComponent(chkempall, "top:100.0px; left:396.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:130.0px;");

		mainLayout.addComponent(new Label("______________________________________________________________________________"), "top:150.0px; left:20.0px; right:20.0px;");
		mainLayout.addComponent(cButton,"top:170.opx; left:150.0px");
		return mainLayout;
	}
}
