package com.reportform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
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
public class RptEmployeeList extends Window 
{
	private SessionBean sessionBean;
	public AbsoluteLayout mainLayout;

	private ComboBox cmbCategory;	
	private ComboBox cmbDepartment;	
	private ComboBox cmbSection;
	private ComboBox cmbDesignation;
	private ComboBox cmbReligion;
	private ComboBox cmbGender;

	private Label lblCategory;
	private Label lblSection;
	private Label lblDesignation;
	private Label lblReligion;
	private Label lblGender;

	private OptionGroup ogaictive = new OptionGroup();
	private static final List<String> aictiveType = Arrays.asList(new String[]{"Active","Inactive","All"});
	private String stAIctive = "1";

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	private CheckBox categoryAll = new CheckBox("All");
	private CheckBox chkDepartmentAll = new CheckBox("All");
	private CheckBox sectionAll = new CheckBox("All");
	private CheckBox designationAll = new CheckBox("All");
	private CheckBox ReligionAll = new CheckBox("All");
	private CheckBox chkGenderAll=new CheckBox("All");
	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private static final String[] category = new String[] {"Permanent", "Temporary", "Provisionary", "Casual"};
	private static final String[] religion = new String[] {"Islam","Hindu","Buddism","Cristian","Other"};

	LinkedHashMap<String, Object> rowMap=new LinkedHashMap<String, Object>();
	int i=0;
	int rownum=0;
	private static final String CHO="'DEPT10'";
	public RptEmployeeList(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE LIST :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
		cmbSectionAddData("%");
		cmbDesignationaddData();
		cmbGenderDataLoad();
		setEventAction();
		focusMove();
	}

	public void cmbDepartmentAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list=session.createSQLQuery(" select distinct ein.vDepartmentId,dept.vDepartmentName from " +
					" tbDepartmentInfo dept inner join tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId "+
					" where ein.vDepartmentId!="+CHO+"" +
					" order by dept.vDepartmentName").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentAddData",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbSectionAddData(String DepartmentID)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list=session.createSQLQuery("select distinct ein.vSectionId,sein.vDepartmentName," +
					" sein.SectionName from tbSectionInfo sein inner join tbEmployeeInfo ein on " +
					" sein.vSectionId=ein.vSectionId where ein.vDepartmentID like '"+DepartmentID+"' "+
					" and ein.vDepartmentId!="+CHO+" "+
					" order by sein.vDepartmentName,sein.SectionName").list();	
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionAddData",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbDesignationaddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String desigQuery="select distinct ein.vDesignationId,din.designationName from tbEmployeeInfo " +
					" ein inner join tbDesignationInfo din on ein.vDesignationId=din.designationId where " +
					" ISNULL(ein.vProximityId,'')!='' and ein.vDepartmentId!="+CHO+"  ";
			List <?> list=session.createSQLQuery(desigQuery).list();	
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDesignation.addItem(element[0]);
				cmbDesignation.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDesignationaddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbGenderDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String desigQuery="select distinct vGender,vGender from tbEmployeeInfo";
			List <?> list=session.createSQLQuery(desigQuery).list();	
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbGender.addItem(element[0]);
				cmbGender.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDesignationaddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void setEventAction()
	{
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

		ogaictive.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(event.getProperty().toString()=="Active")
				{
					stAIctive = "1";
				}
				else if(event.getProperty().toString()=="Inactive")
				{
					stAIctive = "0";
				}
				else
				{
					stAIctive = "%";
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
					cmbSectionAddData(cmbDepartment.getValue().toString());
				}
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbSection.removeAllItems();
				if(chkDepartmentAll.booleanValue()==true)
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

		sectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sectionAll.booleanValue()==true)
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

		designationAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(designationAll.booleanValue()==true)
				{
					cmbDesignation.setValue(null);
					cmbDesignation.setEnabled(false);
				}
				else
				{
					cmbDesignation.setEnabled(true);
				}
			}
		});

		ReligionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(ReligionAll.booleanValue()==true)
				{
					cmbReligion.setValue(null);
					cmbReligion.setEnabled(false);
				}
				else
				{
					cmbReligion.setEnabled(true);
				}
			}
		});
		chkGenderAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkGenderAll.booleanValue()==true)
				{
					cmbGender.setValue(null);
					cmbGender.setEnabled(false);
				}
				else
				{
					cmbGender.setEnabled(true);
				}
			}
		});
	}

	private void formValidation()
	{
		if(cmbCategory.getValue()!=null || categoryAll.booleanValue())
		{
			if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
			{
				if(cmbSection.getValue()!=null || sectionAll.booleanValue())
				{
					if(cmbDesignation.getValue()!=null || designationAll.booleanValue())
					{
						if(cmbReligion.getValue()!=null || ReligionAll.booleanValue())
						{
							if(!stAIctive.equals(""))
							{
								getAllData();
							}
							else
							{
								showNotification("Warning","Select Activity",Notification.TYPE_WARNING_MESSAGE);	
							}
						}
						else
						{
							showNotification("Warning","Select Religion",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Designation",Notification.TYPE_WARNING_MESSAGE);
					}
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
		else
		{
			showNotification("Warning","Select Category",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void getAllData()
	{
		String categoryValue = "%";
		String DepartmentValue = "%";
		String sectionValue = "%";
		String designationValue = "%";
		String religionValue = "%";
		String typeValue = "%";
		String gender="%";

		if(cmbCategory.getValue()!=null)
			categoryValue = cmbCategory.getItemCaption(cmbCategory.getValue().toString());

		if(cmbDepartment.getValue()!=null)
			DepartmentValue = cmbDepartment.getValue().toString();

		if(cmbSection.getValue()!=null)
			sectionValue = cmbSection.getValue().toString();

		if(cmbDesignation.getValue()!=null)
			designationValue = cmbDesignation.getValue().toString();

		if(cmbReligion.getValue()!=null)
			religionValue = cmbReligion.getItemCaption(cmbReligion.getValue().toString());
		if(cmbGender.getValue()!=null)
			gender=cmbGender.getItemCaption(cmbGender.getValue().toString());

		if(ogaictive.getValue().toString().equals("Active")){
			typeValue = "1";}

		else if(ogaictive.getValue().toString().equals("Inactive")){
			typeValue = "0";}

		else{
			typeValue = "%";}

		reportShow(categoryValue,DepartmentValue,sectionValue,designationValue,religionValue,gender,typeValue);
	}

	private void reportShow(Object categoryValue,Object departmentValue,Object SectionValue,Object designationValue,Object religionvalue,Object gender,Object activeValue)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query1=" SELECT vEmployeeType,d.vDepartmentName,b.SectionName,employeeCode,iFingerID,vProximityId,vEmployeeId,vEmployeeName,c.designationName, " +
					" vReligion,a.vGender,iStatus,a.dJoiningDate from tbEmployeeInfo as a inner join tbDepartmentInfo d on a.vDepartmentID=d.vDepartmentID " +
					" inner join tbSectionInfo as b on a.vSectionId=b.vSectionId inner join tbDesignationInfo as c on a.vDesignationId=c.designationId " +
					" where a.vEmployeeType like '"+categoryValue+"' and a.vDepartmentID like '"+departmentValue+"' and " +
					" a.vSectionId like '"+SectionValue+"' and a.vDesignationId like '"+designationValue+"' and " +
					" a.vReligion like '"+religionvalue+"' and a.vGender like '"+gender+"' and a.iStatus like '"+activeValue+"' and a.vDepartmentId!="+CHO+"  order by d.vDepartmentID,b.SectionName,vEmployeeType,iStatus, " +
					" employeeCode ";
			
			System.out.println("query1"+query1);

			if(queryValueCheck(query1))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "excel.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					String strColName[]={"SL#","Employee ID","Proximity ID","Employee Name","Designation Name","Religion","Gender","Joining Date"};
					String Header="";

					String query="SELECT distinct ein.vDepartmentID,dept.vDepartmentName,ein.vSectionID,sein.SectionName,ein.vEmployeeType,ein.iStatus,ein.vGender " +
							"from tbEmployeeInfo ein inner join tbDepartmentInfo dept on dept.vDepartmentID=ein.vDepartmentID inner join tbSectionInfo sein on " +
							"ein.vSectionId=sein.vSectionId where ein.vDepartmentID like '"+departmentValue+"' and ein.vSectionID like '"+SectionValue+"' and " +
							"ein.vEmployeeType like '"+categoryValue+"' and ein.iStatus like '"+activeValue+"' and ein.vGender like '"+gender+"' and ein.vDepartmentId!="+CHO+"  order by dept.vDepartmentName,sein.SectionName," +
							"ein.vEmployeeType,ein.iStatus";

					List <?> lst1=session.createSQLQuery(query).list();

					String detailQuery[]=new String[lst1.size()];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					String [] signatureOption = new String [0];
					int countInd=0;

					for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
					{
						Object [] element = (Object[]) itr1.next();
						groupItem[countInd]="Department Name : "+element[1].toString()+"     Section Name : "+element[3].toString()+"     Category Name : "+element[4].toString()+"     Status : "+(element[5].toString().equals("1")?"Active":"Inactive");
						GroupElement[countInd]=new Object [] {(Object)"Department Name : ",element[1],(Object)"Section Name : ",element[3],(Object)"Category Name : ",element[4],(Object)"Status : ",(element[5].toString().equals("1")?(Object)"Active":(Object)"Inactive")};

						detailQuery[countInd]=" SELECT employeeCode,vProximityId,vEmployeeName,c.designationName," +
								" vReligion,a.vGender,a.dJoiningDate from tbEmployeeInfo as a inner join tbDepartmentInfo dept" +
								" on a.vDepartmentID=dept.vDepartmentID inner join tbSectionInfo as b on a.vSectionId=b.vSectionId inner" +
								" join tbDesignationInfo as c on a.vDesignationId=c.designationId where vEmployeeType like '"+element[4].toString()+"' and" +
								" a.vDepartmentID like '"+element[0].toString()+"' and a.vSectionId like '"+element[2].toString()+"' and a.vDesignationId like '"+designationValue+"' and" +
								" vReligion like '"+religionvalue+"' and a.vGender like '"+gender+"' and iStatus like '"+element[5].toString()+"' and a.vDepartmentId!="+CHO+"  order by dept.vDepartmentName,b.SectionName,vEmployeeType,iStatus," +
								" employeeCode";
						countInd++;
					}

					new GenerateExcelReport(sessionBean, loc, url, fname, "Employee Information", "EMPLOYEE LIST",
							Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, detailQuery, 0, 0, "A4",
							"Landscape",signatureOption);

					Window window = new Window();
					getApplication().addWindow(window);
					getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
				}

				else
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone",sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query1);

					ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
					Window win = new ReportViewer(hm,"report/account/hrmModule/rptEmployeeList.jasper",
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
		allComp.add(cmbCategory);
		allComp.add(cmbSection);
		allComp.add(cmbDesignation);
		allComp.add(cmbReligion);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("460px");
		setHeight("350px");

		//lblCategory
		lblCategory = new Label("Category : ");
		lblCategory.setImmediate(true);
		lblCategory.setWidth("100px");
		lblCategory.setHeight("-1px");
		mainLayout.addComponent(lblCategory, "top:10px; left:20px;");

		// cmbCategory
		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(true);
		cmbCategory.setWidth("260px");
		cmbCategory.setHeight("-1px");
		mainLayout.addComponent(cmbCategory, "top:08.0px; left:130.0px;");
		for(int i=0; i<category.length; i++)
		{cmbCategory.addItem(category[i]);}

		// categoryAll 
		categoryAll = new CheckBox("All");
		categoryAll.setImmediate(true);
		categoryAll.setWidth("-1px");
		categoryAll.setHeight("-1px");
		mainLayout.addComponent(categoryAll,"top:10px; left:395px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(new Label("Department Name : "), "top:40px; left:20px;");
		mainLayout.addComponent(cmbDepartment, "top:38.0px; left:130.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll,"top:40px; left:395px;");

		// lblSection
		lblSection = new Label("Section : ");
		lblSection.setImmediate(true);
		lblSection.setWidth("100px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:70px; left:20px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(cmbSection, "top:68.0px; left:130.0px;");

		// sectionAll 
		sectionAll = new CheckBox("All");
		sectionAll.setImmediate(true);
		sectionAll.setWidth("-1px");
		sectionAll.setHeight("-1px");
		mainLayout.addComponent(sectionAll,"top:70px; left:395px;");

		// lblDesignation
		lblDesignation = new Label("Designation : ");
		lblDesignation.setImmediate(true);
		lblDesignation.setWidth("100px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:100px; left:20px;");

		// cmbDesignation
		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("260px");
		cmbDesignation.setHeight("-1px");
		mainLayout.addComponent(cmbDesignation, "top:98.0px; left:130.0px;");

		// designationAll 
		designationAll = new CheckBox("All");
		designationAll.setImmediate(true);
		designationAll.setWidth("-1px");
		designationAll.setHeight("-1px");
		mainLayout.addComponent(designationAll,"top:100px; left:395px;");

		// lblReligion
		lblReligion = new Label("Religion : ");
		lblReligion.setImmediate(true);
		lblReligion.setWidth("100px");
		lblReligion.setHeight("-1px");
		mainLayout.addComponent(lblReligion, "top:130px; left:20px;");

		// cmbReligion
		cmbReligion = new ComboBox();
		cmbReligion.setImmediate(true);
		cmbReligion.setWidth("260px");
		cmbReligion.setHeight("-1px");
		mainLayout.addComponent(cmbReligion, "top:128.0px; left:130.0px;");
		for(int i=0; i<religion.length; i++)
		{cmbReligion.addItem(religion[i]);}

		// ReligionAll 
		ReligionAll = new CheckBox("All");
		ReligionAll.setImmediate(true);
		ReligionAll.setWidth("-1px");
		ReligionAll.setHeight("-1px");
		mainLayout.addComponent(ReligionAll,"top:130px; left:395px;");

		
		lblGender=new Label("Gender : ");
		lblGender.setHeight("-1px");
		lblGender.setWidth("-1px");
		mainLayout.addComponent(lblGender,"top:160px;left:20px");
		
		cmbGender = new ComboBox();
		cmbGender.setImmediate(true);
		cmbGender.setWidth("260px");
		cmbGender.setHeight("-1px");
		mainLayout.addComponent(cmbGender, "top:158.0px; left:130.0px;");

		// designationAll 
		chkGenderAll = new CheckBox("All");
		chkGenderAll.setImmediate(true);
		chkGenderAll.setWidth("-1px");
		chkGenderAll.setHeight("-1px");
		mainLayout.addComponent(chkGenderAll,"top:160px; left:395px;");
		// ogaictive
		ogaictive = new OptionGroup("",aictiveType);
		ogaictive.setImmediate(true);
		ogaictive.setValue("Active");
		ogaictive.setWidth("250px");
		ogaictive.setHeight("-1px");
		ogaictive.setStyleName("horizontal");
		mainLayout.addComponent(ogaictive, "top:190px; left:130px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:220.0px;left:150.0px;");

		mainLayout.addComponent(new Label("__________________________________________________________________________________________________"), "top:230.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton, "top:260.0px;left:140.0px;");
		return mainLayout;
	}
}
