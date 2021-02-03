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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptAgeAsOnDate extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblCategory;
	private Label lblEmployeeName;
	private Label lblAsOnDate;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private ComboBox cmbCategory;
	private ComboBox cmbEmployeeName;
	private PopupDateField asOnDate;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	private CheckBox chkDepartmentAll;
	private CheckBox chkSectionAll;
	private CheckBox chkCategoryAll;
	private CheckBox chkEmployeeAll;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private static final String[] category = new String[] { "Permanent", "Temporary", "Provisionary", "Casual"};
	private ReportDate reportTime = new ReportDate();
	private static final String CHO="'DEPT10'";
	public RptAgeAsOnDate(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("AGE(AS ON DATE) :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		addDepartmentName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				addSectionName();
			}
		});

		chkDepartmentAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				cmbSection.removeAllItems();
				if(chkDepartmentAll.booleanValue()==true)
				{
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
					addSectionName();
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
				cmbEmployeeName.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeNameData(cmbSection.getValue().toString());
				}
			}
		});

		chkSectionAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				cmbEmployeeName.removeAllItems();
				if(chkSectionAll.booleanValue()==true)
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					cmbEmployeeNameData("%");
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		chkCategoryAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkCategoryAll.booleanValue()==true)
				{
					cmbCategory.setValue(null);
					cmbCategory.setEnabled(false);
				}
				else
				{
					cmbCategory.setEnabled(true);
				}
			}
		});

		chkEmployeeAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkEmployeeAll.booleanValue()==true)
				{
					cmbEmployeeName.setValue(null);
					cmbEmployeeName.setEnabled(false);
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
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.getValue()!=null)
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue()==true )
					{
						if(cmbCategory.getValue()!=null || chkCategoryAll.booleanValue()==true)
						{
							if(cmbEmployeeName.getValue()!=null || chkEmployeeAll.booleanValue()==true)
							{
								if(asOnDate.getValue()!=null)
								{
									getAllData();
								}
								else
								{
									showNotification("Warning","Select Date",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Category Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Department Name",Notification.TYPE_WARNING_MESSAGE);
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
		RadioBtnStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(cmbCategory.getValue()!=null || chkCategoryAll.booleanValue())
				{
					cmbEmployeeNameData((cmbSection.getValue()!=null?cmbSection.getValue().toString():"%"));
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				cmbEmployeeNameData((cmbSection.getValue()!=null?cmbSection.getValue().toString():"%"));
			}
		});
	}

	public void addDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(" SELECT vDepartmentID,vDepartmentName from tbDepartmentInfo order by vDepartmentID ").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addDepartmentName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addSectionName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(" SELECT vSectionId,vDepartmentName,SectionName from tbSectionInfo " +
					"where vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and vDepartmentId!="+CHO+" " +
					"order by vSectionId ").list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("addSectionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbEmployeeNameData(String Section)
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
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vEmployeeId,employeeCode from tbEmployeeInfo where vDepartmentID like " +
					"'"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and vSectionId like " +
					"'"+Section+"' and iStatus like '"+status+"'  and vDepartmentId!="+CHO+"  order by employeeCode";
			lblEmployeeName.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo where vDepartmentID like " +
						"'"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and vSectionId like " +
						"'"+Section+"' and iStatus like '"+status+"'  and vDepartmentId!="+CHO+"  order by employeeCode";
				lblEmployeeName.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "select vEmployeeId,vProximityID from tbEmployeeInfo where vDepartmentID like " +
						"'"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and vSectionId like " +
						"'"+Section+"' and iStatus like '"+status+"'  and vDepartmentId!="+CHO+"  order by employeeCode";
				lblEmployeeName.setValue("Proximity ID :");
			}
			
			List <?> list=session.createSQLQuery(query).list();	
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeName.addItem(element[0]);
				cmbEmployeeName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeNameData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void getAllData()
	{
		String sectionValue = "";
		String categoryValue = "";
		String nameValue = "";

		if(cmbSection.getValue()!=null)
		{
			sectionValue = cmbSection.getValue().toString();
		}
		else
		{
			sectionValue = "%";
		}

		if(cmbCategory.getValue()!=null)
		{
			categoryValue = cmbCategory.getValue().toString();
		}
		else
		{
			categoryValue = "%";
		}

		if(cmbEmployeeName.getValue()!=null)
		{
			nameValue = cmbEmployeeName.getValue().toString();
		}
		else
		{
			nameValue = "%";
		}

		reportShow(sectionValue,categoryValue,nameValue,new SimpleDateFormat("yyyy-MM-dd").format(asOnDate.getValue()));
	}

	private void reportShow(Object Section,Object Category,Object Name,Object date)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;

		try
		{
			query = " SELECT  dept.vDepartmentName,b.SectionName,a.vEmployeeType,b.SectionName,a.vEmployeeType," +
					" a.employeeCode,a.vProximityID,a.vEmployeeName,c.designationName,a.dDateOfBirth," +
					" ((DATEDIFF(DAY,dDateOfBirth,'"+date+"'))/365) as year,(((DATEDIFF(DAY,dDateOfBirth,'"+date+"'))%365)/30) " +
					" as month,((((DATEDIFF(DAY,dDateOfBirth,'"+date+"'))%365)%30)) as day from tbEmployeeInfo as a inner join " +
					" tbDepartmentInfo as dept on a.vDepartmentID=dept.vDepartmentID inner join tbSectionInfo " +
					" as b on a.vSectionId=b.vSectionId inner join tbDesignationInfo as c on a.vDesignationId=c.designationId where" +
					" a.vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' " +
					" and b.vSectionId like '"+Section+"' and a.vEmployeeType like '"+Category+"' and a.vEmployeeId like '"+Name+"'" +
					" and a.dDateOfBirth<='"+date+"'  and a.vDepartmentId!="+CHO+" order by dept.vDepartmentName,b.SectionName,a.vEmployeeType,a.employeeCode";

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("asOnDate", asOnDate.getValue());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptAgeAsOnDate.jasper",
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
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbCategory);
		allComp.add(cmbEmployeeName);
		allComp.add(asOnDate);
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
		setWidth("430px");
		setHeight("320px");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("200px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:30.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:28.0px; left:150.0px;");

		// chkSectionAll
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:30.0px; left:355.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:150.0px;");

		// chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:60.0px; left:355.0px;");

		// lblCategory
		lblCategory = new Label("Category :");
		lblCategory.setImmediate(false);
		lblCategory.setWidth("100.0%");
		lblCategory.setHeight("-1px");
		mainLayout.addComponent(lblCategory,"top:90.0px; left:30.0px;");

		// cmbCategory
		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(false);
		cmbCategory.setWidth("200px");
		cmbCategory.setHeight("-1px");
		cmbCategory.setNullSelectionAllowed(true);
		cmbCategory.setImmediate(true);
		mainLayout.addComponent(cmbCategory, "top:88.0px; left:150.0px;");
		for(int i=0; i<category.length; i++)
		{cmbCategory.addItem(category[i]);}

		// chkCategoryAll
		chkCategoryAll = new CheckBox("All");
		chkCategoryAll.setHeight("-1px");
		chkCategoryAll.setWidth("-1px");
		chkCategoryAll.setImmediate(true);
		mainLayout.addComponent(chkCategoryAll, "top:90.0px; left:355.0px;");
		
		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:120.0px;left:50.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:140.0px; left:50.0px;");

		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name :");
		lblEmployeeName.setImmediate(false);
		lblEmployeeName.setWidth("100.0%");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName,"top:170.0px; left:30.0px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(false);
		cmbEmployeeName.setWidth("200px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setNullSelectionAllowed(true);
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbEmployeeName.setImmediate(true);
		mainLayout.addComponent(cmbEmployeeName, "top:168.0px; left:150.0px;");

		// chkEmployeeAll
		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeAll, "top:170.0px; left:355.0px;");

		// lblAsOnDate
		lblAsOnDate = new Label("As On Date :");
		lblAsOnDate.setImmediate(true);
		lblAsOnDate.setWidth("100%");
		lblAsOnDate.setHeight("-1px");
		mainLayout.addComponent(lblAsOnDate, "top:200.0px; left:30.0px;");

		// asOnDate
		asOnDate = new PopupDateField();
		asOnDate.setWidth("110px");
		asOnDate.setDateFormat("dd-MM-yyyy");
		asOnDate.setValue(new java.util.Date());
		asOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(asOnDate, "top:198.0px; left:150.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:220.0px;left:150.0px;");

		mainLayout.addComponent(cButton,"top:255.opx; left:130.0px");
		return mainLayout;
	}
}
