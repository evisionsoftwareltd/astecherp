package com.reportform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
public class RptEmployeeInformationOfficial extends Window 
{
	private SessionBean sessionBean;
	public AbsoluteLayout mainLayout;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;

	private Label lblSection;

	private OptionGroup ogaictive = new OptionGroup();
	private static final List<String> aictiveType = Arrays.asList(new String[]{"Active","Inactive","All"});
	private String stAIctive = "1";

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});


	private CheckBox sectionAll = new CheckBox("All");
	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	int i=0;
	int rownum=0;

	public RptEmployeeInformationOfficial(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE LIST(OFFICIAL) :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
		setEventAction();
		focusMove();
	}

	public void cmbDepartmentAddData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list=session.createSQLQuery(" select distinct ein.vDepartmentId,dept.vDepartmentName from " +
					"tbDepartmentInfo dept inner join tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where vDepartmentName != 'CHO'" +
					"order by dept.vDepartmentName").list();	

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

	public void cmbSectionAddData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list=session.createSQLQuery(" select distinct ein.vSectionId,sein.SectionName from " +
					"tbSectionInfo sein inner join tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where " +
					"sein.vDepartmentId='"+cmbDepartment.getValue()+"' order by sein.SectionName").list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionAddData",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
					cmbSectionAddData();
				}
			}
		});

		sectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sectionAll.booleanValue())
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
	}

	private void formValidation()
	{
		if(cmbDepartment.getValue()!=null)
		{
			if(cmbSection.getValue()!=null || sectionAll.booleanValue())
			{
				if(!stAIctive.equals(""))
				{
					getAllData();
				}
				else
				{
					showNotification("Warning","Select Active/Inactive!!!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning","Select Section!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning","Select Department!!!",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void getAllData()
	{
		String sectionValue = "";
		String typeValue = "";

		if(sectionAll.booleanValue()==true){sectionValue = "%";}
		else{sectionValue = cmbSection.getValue().toString();}
		if(ogaictive.getValue().toString().equals("Active")){typeValue = "1";}
		else if(ogaictive.getValue().toString().equals("Inactive")){typeValue = "0";}
		else{typeValue = "%";}

		reportShow(sectionValue,typeValue);
	}

	private void reportShow(Object sectionValue,Object activeValue)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query1= "select s.SectionName,e.employeeCode,din.designationId,din.designationName,e.vDepartmentID," +
					"s.vDepartmentName,e.vSectionId,e.iStatus,e.vEmployeeName,e.vGender,e.vOtherQualification," +
					"e.dDateOfBirth,e.dJoiningDate,e.dConfirmationDate,e.vEmployeeType,e.vEmployeeNameBan," +
					"e.vProximityId,e.mOthersAllowance,e.accountNo,e.vContact,vMobileBankFlag from tbEmployeeInfo as e  inner " +
					"join tbSectionInfo as s on s.vSectionId=e.vSectionId inner join tbDesignationInfo din on " +
					"e.vDesignationId=din.designationId where e.vDepartmentId = '"+cmbDepartment.getValue()+"' " +
					"and e.vSectionId like '"+sectionValue+"' and iStatus like '"+activeValue+"' order by " +
					"s.SectionName,e.vEmployeeType,e.iStatus,e.employeeCode";

			if(queryValueCheck(query1))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "officialemployeeinformation.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					String strColName[]={"SL#","Employee ID","Proximity ID","Employee Name","Designation","Service type",
							"Official type","Gender","Educational Qualification","Date of Birth","Date of Joining",
							"Date of Confirmation","Gross","Bank A/C No","A/C Status","Contact"};
					String Header="";

					String query="SELECT distinct ein.vDepartmentID,sein.vDepartmentName,ein.vSectionID,sein.SectionName " +
							"from tbEmployeeInfo ein inner join tbSectionInfo sein on ein.vSectionId=sein.vSectionId " +
							"where ein.vDepartmentId = '"+cmbDepartment.getValue()+"' and  ein.vSectionID like '"+sectionValue+"' and " +
							"ein.iStatus like '"+activeValue+"' order by sein.SectionName";

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
						groupItem[countInd]="Department Name : "+element[1].toString()+"          Section Name : "+element[3].toString();
						GroupElement[countInd]=new Object [] {(Object)"Department Name : ",element[1],(Object)"Section Name : ",element[3]};

						detailQuery[countInd]="select e.employeeCode,e.vProximityId,e.vEmployeeName,d.designationName,e.vEmployeeType," +
								"e.vEmployeeNameBan,e.vGender,e.vOtherQualification,e.dDateofBirth,e.dJoiningDate,e.dConfirmationDate," +
								"cast(e.mOthersAllowance as float) mOthersAllowance,e.accountNo,vMobileBankFlag,e.vContact from tbEmployeeInfo as e " +
								"inner join tbSectionInfo as s on s.vSectionId=e.vSectionId inner join tbDesignationInfo as d on " +
								"d.designationId=e.vDesignationId where e.vDepartmentID = '"+element[0].toString()+"' and e.vSectionId " +
								"like '"+element[2].toString()+"' and iStatus like '"+activeValue+"' order by s.SectionName,e.vEmployeeType," +
								"e.iStatus,e.employeeCode";
						countInd++;
					}

					new GenerateExcelReport(sessionBean, loc, url, fname, "Employee Information","EMPLOYEE LIST(OFFICIAL)",
							Header, strColName, 2,groupItem, GroupColName, GroupElement, 1, detailQuery,0,0, "A4","Lanscape",signatureOption);

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
					Window win = new ReportViewer(hm,"report/account/hrmModule/rptEmployeeInformationOfficial.jasper",
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

	private void focusMove()
	{
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("480px");
		setHeight("260px");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(new Label("Department Name : "), "top:25.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:23.0px; left:140.0px;");

		lblSection = new Label("Section Name : ");
		lblSection.setImmediate(true);
		lblSection.setWidth("100px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:55px; left:20px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(cmbSection, "top:53.0px; left:140.0px;");

		sectionAll = new CheckBox("All");
		sectionAll.setImmediate(true);
		sectionAll.setWidth("-1px");
		sectionAll.setHeight("-1px");
		mainLayout.addComponent(sectionAll,"top:55px; left:405px;");

		ogaictive = new OptionGroup("",aictiveType);
		ogaictive.setImmediate(true);
		ogaictive.setValue("Active");
		ogaictive.setWidth("250px");
		ogaictive.setHeight("-1px");
		ogaictive.setStyleName("horizontal");
		mainLayout.addComponent(ogaictive, "top:80px; left:150px;");

		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:150.0px;");

		mainLayout.addComponent(new Label("__________________________________________________________________________________________________"), "top:145.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton, "top:170.0px;left:140.0px;");
		return mainLayout;
	}
}
