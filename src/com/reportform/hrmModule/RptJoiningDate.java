package com.reportform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.GenerateExcelReport;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptJoiningDate extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblCategory;
	private Label lblEmployeeName;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private ComboBox cmbCategory;
	private ComboBox cmbEmployeeName;

	private CheckBox departmentAll;
	private CheckBox sectionAll;
	private CheckBox categoryAll;
	private CheckBox employeeAll;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	ArrayList<Component> allComp = new ArrayList<Component>();

	private static final String[] category = new String[] { "Permanent", "Temporary", "Provisionary", "Casual"};

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private static final String CHO="'DEPT10'";
	public RptJoiningDate(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE JOINING DATE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
		setEventAction();
	}

	public void cmbDepartmentAddData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			List <?> list=session.createSQLQuery(" SELECT ein.vDepartmentID,dept.vDepartmentName from tbDepartmentInfo dept " +
					"inner join tbEmployeeInfo ein on ein.vDepartmentID=dept.vDepartmentID where ein.vDepartmentID!="+CHO+" order by vDepartmentID ").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
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
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			List <?> list=session.createSQLQuery(" SELECT vSectionID,vDepartmentName,SectionName from tbSectionInfo where vDepartmentID like " +
					"'"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and vDepartmentID!="+CHO+" order by vSectionID ").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
					"'"+Section+"' and iStatus like '"+status+"'  and vDepartmentID!="+CHO+" order by employeeCode";
			lblEmployeeName.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo where vDepartmentID like " +
						"'"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and vSectionId like " +
						"'"+Section+"' and iStatus like '"+status+"'  and vDepartmentID!="+CHO+" order by employeeCode";
				lblEmployeeName.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "select vEmployeeId,vProximityID from tbEmployeeInfo where vDepartmentID like " +
						"'"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and vSectionId like " +
						"'"+Section+"' and iStatus like '"+status+"'  and vDepartmentID!="+CHO+" order by employeeCode";
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

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!=null || departmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null || sectionAll.booleanValue())
					{
						if(cmbCategory.getValue()!=null || categoryAll.booleanValue())
						{
							if(cmbEmployeeName.getValue()!=null || employeeAll.booleanValue())
							{
								getAllData();
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

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				cmbSectionAddData();
			}
		});

		departmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbSection.removeAllItems();
				if(departmentAll.booleanValue())
				{
					cmbSectionAddData();
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
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
					cmbEmployeeNameData((cmbSection.getValue()!=null?cmbSection.getValue().toString():"%"));
				}
			}
		});
		RadioBtnStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(cmbSection.getValue()!=null || sectionAll.booleanValue())
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
		
		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		sectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(sectionAll.booleanValue()==true)
				{
					String Section= "%";
					cmbEmployeeNameData(Section);
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		categoryAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(categoryAll.booleanValue()==true)
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

		employeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(employeeAll.booleanValue()==true)
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
	}

	private void getAllData()
	{
		String sectionValue = "";
		String categoryValue = "";
		String employeeValue = "";


		if(sectionAll.booleanValue()==true){
			sectionValue = "%";}
		else{
			sectionValue =cmbSection.getValue().toString();}

		if(categoryAll.booleanValue()==true){
			categoryValue = "%";}
		else{
			categoryValue = cmbCategory.getItemCaption(cmbCategory.getValue());}

		if(employeeAll.booleanValue()==true){
			employeeValue = "%";}
		else{
			employeeValue = cmbEmployeeName.getValue().toString();}

		reportShow(categoryValue,sectionValue,employeeValue);
	}

	private void reportShow(Object categoryValue,Object sectionValue,Object employeeValue){

		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " SELECT dept.vDepartmentName,b.SectionName," +
					" vEmployeeType as employee,employeeCode,vProximityID,vEmployeeName,c.designationName,dJoiningDate " +
					" from tbEmployeeInfo as a inner join tbDepartmentInfo as dept on a.vDepartmentID=dept.vDepartmentID" +
					" inner join tbSectionInfo as b on a.vSectionId=b.vSectionId" +
					" inner join tbDesignationInfo as c on a.vDesignationId=c.designationId where" +
					" a.vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' " +
					" and a.vSectionId like '"+sectionValue+"' and" +
					" vEmployeeType like '"+categoryValue+"'" +
					" and vEmployeeId like '"+employeeValue+"'  and a.vDepartmentID!="+CHO+"  order by dept.vDepartmentName,b.SectionName,vEmployeeType,dJoiningDate ";

			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "JoiningDate.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					String strColName[]={"SL#","Employee ID","Proximity ID","Employee Name","Designation Name","Joining Date"};
					String Header="";

					String GroupQuery="SELECT distinct ein.vDepartmentID,sein.vDepartmentName,ein.vSectionID," +
							"sein.SectionName,ein.vEmployeeType from tbEmployeeInfo ein inner join tbSectionInfo " +
							"sein on ein.vSectionId=sein.vSectionId where ein.vSectionID like '"+sectionValue+"' and " +
							"ein.vEmployeeType like '"+categoryValue+"'  and ein.vDepartmentID!="+CHO+" order by sein.vDepartmentName,sein.SectionName," +
							"ein.vEmployeeType";
					List <?> lst1=session.createSQLQuery(GroupQuery).list();

					String detailQuery[]=new String[lst1.size()];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					String [] signatureOption = new String [0];
					int countInd=0;

					for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
					{
						Object [] element = (Object[]) itr1.next();
						groupItem[countInd]="Department Name : "+element[1].toString()+"     Section Name : "+element[3].toString()+"     Category Name : "+element[4].toString();
						GroupElement[countInd]=new Object [] {(Object)"Department Name : ",element[1],(Object)"Section Name : ",element[3],(Object)"Category Name : ",element[4]};

						detailQuery[countInd]=" SELECT employeeCode,vProximityId,vEmployeeName,c.designationName," +
								" a.dJoiningDate from tbEmployeeInfo as a inner join tbSectionInfo as b on " +
								"a.vSectionId=b.vSectionID inner join tbDesignationInfo as c on " +
								"a.vDesignationId=c.designationId where vEmployeeType like '"+element[4].toString()+"' " +
								"and b.vDepartmentID = '"+element[0].toString()+"' and b.vSectionId = '"+element[2].toString()+"' " +
								"and vEmployeeID like '"+employeeValue+"' and b.vDepartmentID!="+CHO+" order by b.SectionName,vEmployeeType,iStatus,employeeCode," +
								"a.dJoiningDate";
						countInd++;
					}

					new GenerateExcelReport(sessionBean, loc, url, fname, "Joining_report","JOINING DATE OF EMPLOYEE",
							Header, strColName, 2,groupItem, GroupColName, GroupElement, 1, detailQuery,0,0,"A4",
							"LandScape",signatureOption);

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
					hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/RptJoiningDate.jasper",
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

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("430px");
		setHeight("330px");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("200px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(new Label("Department Name : "), "top:20.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:18.0px; left:150.0px;");

		// sectionAll
		departmentAll = new CheckBox("All");
		departmentAll.setImmediate(true);
		departmentAll.setWidth("-1px");
		departmentAll.setHeight("-1px");
		mainLayout.addComponent(departmentAll,"top:20px; left:355px;");

		// lblSection
		lblSection = new Label("Section Name : ");
		lblSection.setImmediate(true);
		lblSection.setWidth("100px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:50px; left:30px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(cmbSection, "top:48.0px; left:150.0px;");

		// sectionAll
		sectionAll = new CheckBox("All");
		sectionAll.setImmediate(true);
		sectionAll.setWidth("-1px");
		sectionAll.setHeight("-1px");
		mainLayout.addComponent(sectionAll,"top:50px; left:355px;");

		// lblCategory
		lblCategory = new Label("Category : ");
		lblCategory.setImmediate(true);
		lblCategory.setWidth("100px");
		lblCategory.setHeight("-1px");
		mainLayout.addComponent(lblCategory, "top:80px; left:30px;");

		// cmbCategory
		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(true);
		cmbCategory.setWidth("200px");
		cmbCategory.setHeight("-1px");
		mainLayout.addComponent(cmbCategory, "top:78.0px; left:150.0px;");
		for(int i=0; i<category.length; i++)
		{cmbCategory.addItem(category[i]);}

		// categoryAll
		categoryAll = new CheckBox("All");
		categoryAll.setImmediate(true);
		categoryAll.setWidth("-1px");
		categoryAll.setHeight("-1px");
		mainLayout.addComponent(categoryAll,"top:80px; left:355px;");
		
		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:110.0px;left:50.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:130.0px; left:50.0px;");
		
		// lblEmployeeName
		lblEmployeeName = new Label("Employee Name : ");
		lblEmployeeName.setImmediate(true);
		lblEmployeeName.setWidth("100px");
		lblEmployeeName.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeName, "top:160px; left:30px;");

		// cmbEmployeeName
		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("200px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:158.0px; left:150.0px;");

		// employeeAll
		employeeAll = new CheckBox("All");
		employeeAll.setImmediate(true);
		employeeAll.setWidth("-1px");
		employeeAll.setHeight("-1px");
		mainLayout.addComponent(employeeAll,"top:160px; left:355px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:190.0px;left:150.0px;");
		mainLayout.addComponent(cButton, "top:215.0px;left:120.0px;");
		return mainLayout;
	}
}
