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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptEmployeeWiseCoinAnalysis extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private Label lblDepartment;
	private Label lblSection;
	private Label lblEmployee;

	private PopupDateField dMonth;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private ComboBox cmbEmployee;
	private CheckBox chkEmployeeAll;
	private CheckBox chkSectionAll;
	private CheckBox chkDepartmentAll;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat monthformat = new SimpleDateFormat("MMMMMM");
	private SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptEmployeeWiseCoinAnalysis(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE WISE COIN ANALYSIS :: "+sessionBean.getCompany());
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
			String sql = "select distinct vDepartmentID,vDepartmentName from tbSalary where " +
					"Month(dDate)=MONTH('"+dbFormat.format(dMonth.getValue())+"') and " +
					"YEAR(dDate)='"+yearformat.format(dMonth.getValue())+"' and vDepartmentName != 'CHO' order " +
					"by vDepartmentName";
			List <?> list=session.createSQLQuery(sql).list();
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
		finally
		{
			session.close();
		}
	}

	public void cmbSectionAddData(String DepartmentID)
	{
		cmbSection.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{	
			String sql = "select distinct SectionID,Section,vDepartmentName from tbSalary where " +
					"vDepartmentId like '"+DepartmentID+"' and Month(dDate)=MONTH('"+dbFormat.format(dMonth.getValue())+"') and " +
					"YEAR(dDate)='"+yearformat.format(dMonth.getValue())+"' order by vDepartmentName,Section";
			List <?> list=session.createSQLQuery(sql).list();
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


	public void cmbEmpoyeeAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{		
			String query = "select autoEmployeeId,empID from tbSalary where " +
					"vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and " +
					"SectionID like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and " +
					"Month(dDate)=MONTH('"+dbFormat.format(dMonth.getValue())+"') and " +
					"YEAR(dDate)='"+yearformat.format(dMonth.getValue())+"'" +
					"order by vDepartmentName,Section,empID";
			lblEmployee.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select autoEmployeeId,empName,empID from tbSalary where " +
						"vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and " +
						"SectionID like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and " +
						"Month(dDate)=MONTH('"+dbFormat.format(dMonth.getValue())+"') and " +
						"YEAR(dDate)='"+yearformat.format(dMonth.getValue())+"'" +
						"order by vDepartmentName,Section,empID";
				lblEmployee.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "select autoEmployeeId,empCode,empID from tbSalary where " +
						"vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and " +
						"SectionID like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and " +
						"Month(dDate)=MONTH('"+dbFormat.format(dMonth.getValue())+"') and " +
						"YEAR(dDate)='"+yearformat.format(dMonth.getValue())+"'" +
						"order by vDepartmentName,Section,empID";
				lblEmployee.setValue("Proximity ID :");
			}
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmpoyeeAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
				if(cmbDepartment.getValue()!= null)
					cmbSectionAddData(cmbDepartment.getValue().toString());
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(chkDepartmentAll.booleanValue())
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
		
		cmbSection.addListener(new ValueChangeListener()
		{		
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmpoyeeAddData();
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbEmployee.removeAllItems();
				if(chkSectionAll.booleanValue()==true)
				{	
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					cmbEmpoyeeAddData();
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});


		chkEmployeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkEmployeeAll.booleanValue()==true)
				{	
					cmbEmployee.setValue(null);
					cmbEmployee.setEnabled(false);
				}
				else
				{
					cmbEmployee.setEnabled(true);
				}
			}
		});
		
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!= null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbEmployee.getValue()!=null || chkEmployeeAll.booleanValue())
						{
							getAllData();
						}
						else
						{
							showNotification("Warniung","Select Employee Name or Click the Check Box!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning", "Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}

				}
				else
				{
					showNotification("Warning", "Select Department Name!!!", Notification.TYPE_WARNING_MESSAGE);
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
				cmbEmpoyeeAddData();
			}
		});
	}

	private void getAllData()
	{
		String sectionValue = "%";
		String employeeValue = "%";
		String DepartmentValue = "%";

		if(cmbDepartment.getValue()!=null)
		{
			DepartmentValue=cmbDepartment.getValue().toString();
		}
		
		if(cmbSection.getValue() !=null)
		{
			sectionValue = cmbSection.getValue().toString();
		}
		
		if(cmbEmployee.getValue()!=null)
		{
			employeeValue = cmbEmployee.getValue().toString();
		}

		reportShow(DepartmentValue,sectionValue,employeeValue);
	}

	private void reportShow(String Department,String Section,String Employee)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query="select * from dbo.funEmpCoinAnalysis('"+Department+"','"+Section+"','"+Employee+"','"+monthformat.format(dMonth.getValue())+"','"+yearformat.format(dMonth.getValue())+"') where vDepartmentName != 'CHO'  order by vDepartmentName,SectionName,vEmployeeCode";

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("month", new SimpleDateFormat("MMMMM").format(dMonth.getValue())+","+new SimpleDateFormat("yyyy").format(dMonth.getValue()));
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/EmployeeWiseCoinAnalysis.jasper",
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
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);}
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
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("460px");
		setHeight("290px");

		// lblMonth
		lblMonth = new Label("Month :");
		lblMonth.setImmediate(true);
		lblMonth.setWidth("100.0%");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth,"top:20.0px; left:20.0px;");

		// dMonth
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("100px");
		dMonth.setHeight("-1px");
		dMonth.setDateFormat("MMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:18.0px; left:130.0px;");

		lblDepartment = new Label("Department Name :");
		lblDepartment.setImmediate(true);
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment,"top:50.0px; left:20.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:48.0px; left:130.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:50.0px; left:396.0px;");
		
		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(true);
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:80.0px; left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:78.0px; left:130.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:80.0px; left:396.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:110.0px; left:50.0px;");

		lblEmployee = new Label("Employee ID :");
		lblEmployee.setImmediate(true);
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee,"top:140.0px; left:20.0px;");

		// cmbSection
		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:138.0px; left:130.0px;");

		// chkEmployeeAll
		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeAll, "top:140.0px; left:396.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:170.0px;left:130.0px;");

		mainLayout.addComponent(new Label("________________________________________________________________________________"), "top:190.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:220.opx; left:140.0px");
		return mainLayout;
	}
}
