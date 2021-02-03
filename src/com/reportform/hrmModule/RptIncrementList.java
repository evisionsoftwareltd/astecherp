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

@SuppressWarnings("serial")
public class RptIncrementList extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	
	private ComboBox cmbEmployeetype;
	private Label lblEmployeetype;

	private Label lblDepartment;
	private Label lblSection;
	private Label lblSalaryMonth;
	private Label lblEmployeeName;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private PopupDateField dSalaryMonth;
	private ComboBox cmbEmployeeName;

	private CheckBox chkSectionAll;
	private CheckBox chkEmployeeName;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat fMonth = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat fYear = new SimpleDateFormat("yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	
	private static final String[] category = new String[] {"Permanent", "Temporary", "Provisionary", "Casual"};
	
	
	public RptIncrementList(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("SALARY INCREMENT :: "+sessionBean.getCompany());
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
			String query=" select distinct vDepartmentID,vDepartmentName from tbSalaryIncrement " +
					"where Month(dDate)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') and " +
					"YEAR(dDate)='"+fYear.format(dSalaryMonth.getValue())+"' order by vDepartmentName";
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
			String query="select distinct vSectionID,vSectionName from tbSalaryIncrement where " +
					"vDepartmentId = '"+DepartmentID+"' and " +
					"Month(dDate)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') and " +
					"YEAR(dDate)='"+fYear.format(dSalaryMonth.getValue())+"' order by vSectionName";
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
		finally
		{
			session.close();
		}
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
				if(cmbDepartment.getValue()!= null)
					cmbSectionAddData(cmbDepartment.getValue().toString());
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(cmbSection.getValue()!=null)
				{employeeSetData(cmbSection.getValue().toString());}
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				cmbEmployeeName.removeAllItems();
				if(chkSectionAll.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					employeeSetData("%");
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		}); 

		chkEmployeeName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkEmployeeName.booleanValue())
				{
					cmbEmployeeName.setEnabled(false);
					cmbEmployeeName.setValue(null);
				}
				else
				{
					cmbEmployeeName.setEnabled(true);
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbEmployeeName.getValue()!=null || chkEmployeeName.booleanValue())
						{
							reportShow();
						}
						else
						{
							showNotification("Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
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

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				employeeSetData((cmbSection.getValue()!=null?cmbSection.getValue().toString():"%"));
			}
		});
	}

	public void employeeSetData(String sectionId)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select vEmployeeId,employeeCode from tbSalaryIncrement where vDepartmentID = '"+cmbDepartment.getValue()+"' and vSectionId='"+sectionId+"' order by employeeCode ";
			lblEmployeeName.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				query = " select vEmployeeId,vEmployeeName,employeeCode from tbSalaryIncrement where vDepartmentID = '"+cmbDepartment.getValue()+"' and vSectionId='"+sectionId+"' order by employeeCode ";
				lblEmployeeName.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = " select vEmployeeId,vProximityID,employeeCode from tbSalaryIncrement where vDepartmentID = '"+cmbDepartment.getValue()+"' and vSectionId='"+sectionId+"' order by employeeCode";
				lblEmployeeName.setValue("Proximity ID :");
			}
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("employeeSetData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String EmployeeId = "%";
		String SectionId = "%";
		
		if(cmbEmployeeName.getValue()!=null)
		{
			EmployeeId = cmbEmployeeName.getValue().toString();
		}

		if(cmbSection.getValue()!=null)
		{
			SectionId = cmbSection.getValue().toString();
		}
		
		
		try
		{
			String query="select * from tbSalaryIncrement where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and vSectionId like'"+SectionId+"' " +
					"and employeecode like '"+EmployeeId+"' and Month(dDate)=MONTH('"+dFormat.format(dSalaryMonth.getValue())+"') and " +
					"YEAR(dDate)='"+fYear.format(dSalaryMonth.getValue())+"' and vEmployeeType='"+cmbEmployeetype.getValue()+"' order by dDate  ";
			
			System.out.println("OKK"+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
				hm.put("month",fMonth.format(dSalaryMonth.getValue()));
				hm.put("year",fYear.format(dSalaryMonth.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);
				
				

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptSalaincrement.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
				/*Window win = new ReportViewer(hm,"report/account/hrmModule/rptpayslip.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);*/

				
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);}
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
		setWidth("470px");
		setHeight("320px");

		
		
		// lblSalaryMonth
		lblSalaryMonth = new Label("Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("100.0%");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth,"top:20.0px; left:30.0px;");

		// dSalaryMonth
		dSalaryMonth = new PopupDateField();
		dSalaryMonth.setImmediate(true);
		dSalaryMonth.setWidth("140px");
		dSalaryMonth.setHeight("-1px");
		dSalaryMonth.setDateFormat("MMMMM-yyyy");
		dSalaryMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dSalaryMonth.setValue(new java.util.Date());
		mainLayout.addComponent(dSalaryMonth, "top:18.0px; left:130.0px;");

		lblDepartment = new Label("Department :");
		lblDepartment.setImmediate(false);
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment,"top:50.0px; left:30.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDepartment, "top:48.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:80.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:78.0px; left:130.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:80.0px; left:395.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:110.0px; left:50.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee ID :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("100.0%");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName,"top:140.0px; left:30.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("260px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:138.0px; left:130.0px;");

		chkEmployeeName = new CheckBox("All");
		chkEmployeeName.setImmediate(true);
		chkEmployeeName.setHeight("-1px");
		chkEmployeeName.setWidth("-1px");
		mainLayout.addComponent(chkEmployeeName, "top:140.0px; left:395.0px;");
		
		
		lblEmployeetype = new Label("Category : ");
		lblEmployeetype.setImmediate(true);
		lblEmployeetype.setWidth("100px");
		lblEmployeetype.setHeight("-1px");
		mainLayout.addComponent(lblEmployeetype, "top:170px; left:30px;");

		// cmbCategory
		cmbEmployeetype = new ComboBox();
		cmbEmployeetype.setImmediate(true);
		cmbEmployeetype.setWidth("260px");
		cmbEmployeetype.setHeight("-1px");
		mainLayout.addComponent(cmbEmployeetype, "top:168.0px; left:130.0px;");
		for(int i=0; i<category.length; i++)
		{cmbEmployeetype.addItem(category[i]);}


		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:200.0px;left:130.0px;");

		mainLayout.addComponent(new Label("_______________________________________________________________________________"), "top:220.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:240.opx; left:140.0px");
		return mainLayout;
	}
}