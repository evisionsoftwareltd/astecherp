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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptEmployeeListSectionWise extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblDepartment;
	private ComboBox cmbDepartment;
	private CheckBox DepartmentAll;
	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox sectionAll;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private OptionGroup RadioBtnStatus;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	private static final String CHO="'DEPT10'";
	public RptEmployeeListSectionWise(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE LIST(SECTION WISE) :: "+sessionBean.getCompany());
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
				if(cmbDepartment.getValue()!=null)
				{
					addSectionName();
				}
			}
		});

		DepartmentAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(DepartmentAll.booleanValue())
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

		sectionAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
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

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				String section = "";
				if(cmbSection.getValue()!=null || sectionAll.booleanValue()==true)
				{
					if(sectionAll.booleanValue()==true)
					{section = "%";}
					else
					{section = cmbSection.getValue().toString();}

					reportShow(section);
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
	}
	
	
	public void addDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="SELECT vDepartmentID,vDepartmentName from tbDepartmentInfo " +
					"where vDepartmentID!="+CHO+" order by vDepartmentName";
			
			System.out.println("OMG"+query);
			
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
			List <?> list = session.createSQLQuery(" SELECT vSectionID,vDepartmentName,SectionName from tbSectionInfo " +
					"where vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and vDepartmentID!="+CHO+" and SectionName!='CHO' order by SectionName ").list();

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

	private void reportShow(Object sectionName)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
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
		try
		{
			String query = " SELECT b.SectionName,dept.vDepartmentName,a.employeeCode,a.vProximityId,a.vEmployeeName,c.designationName," +
					" a.dJoiningDate,a.accountNo,a.mMonthlySalary,a.mHouseRent,a.mMedicalAllowance,a.mConAllowance," +
					" a.mProvidentFund,a.mKFund,a.mSpecial,a.mOthersAllowance as totalSalary,vMobileBankFlag " +
					" from tbEmployeeInfo as a inner join tbDepartmentInfo dept on a.vDepartmentId=dept.vDepartmentId"+
					" inner join tbSectionInfo as b on a.vSectionId=b.vSectionId inner join" +
					" tbDesignationInfo as c on a.vDesignationId=c.designationId where a.vDepartmentID like " +
					" '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and a.vDepartmentID!="+CHO+" and b.vSectionId " +
					" like '"+sectionName+"' and  a.iStatus like '"+status+"' order by dept.vDepartmentName,b.SectionName,a.employeeCode ";
			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "Section_Wise_Employee_List.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					String strColName[]={"SL#","Employee ID","Proximity ID","Employee Name","Designation",
							"Joining Date","Bank A/C NO.","A/C Status","Basic","HR","Medical","Conv.","Att. Bonus","Gross"};
					String Header="";

					String groupQuery="SELECT distinct ein.vDepartmentID,sein.vDepartmentName,ein.vSectionID,sein.SectionName " +
							"from tbEmployeeInfo ein inner join tbSectionInfo sein on ein.vSectionId=sein.vSectionId where " +
							"ein.vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' " +
							"and ein.vDepartmentID!="+CHO+" and ein.vSectionID like '"+sectionName+"' order by sein.vDepartmentName,sein.SectionName";

					List <?> lst1=session.createSQLQuery(groupQuery).list();

					String detailQuery[]=new String[lst1.size()];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					String [] signatureOption = new String [0];
					int countInd=0;

					for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
					{
						Object [] element = (Object[]) itr1.next();
						groupItem[countInd]="Department Name : "+element[1].toString()+"     Section Name : "+element[3].toString();
						GroupElement[countInd]=new Object [] {(Object)"Department Name : ",element[1],(Object)"Section Name : ",element[3]};

						detailQuery[countInd]="SELECT employeeCode,vProximityId,vEmployeeName,c.designationName," +
								" a.dJoiningDate,a.accountNo,vMobileBankFlag,cast(a.mMonthlySalary as float) mMonthlySalary," +
								" cast(a.mHouseRent as float) mHouseRent,cast(a.mMedicalAllowance as float) mMedicalAllowance," +
								" cast(a.mConAllowance as float) mConAllowance, cast(a.mSpecial as float) mSpecial," +
								" cast(mOthersAllowance as float) as totalSalary  from tbEmployeeInfo as a inner join" +
								" tbDesignationInfo as c on a.vDesignationId=c.designationId where a.vDepartmentID like" +
								" '"+element[0].toString()+"' and a.vDepartmentID!="+CHO+" and a.vSectionId like '"+element[2].toString()+"' and  a.iStatus like '"+status+"'   order by" +
								" a.employeeCode";
						countInd++;
					}

					new GenerateExcelReport(sessionBean, loc, url, fname, "Employee Information", "EMPLOYEE LIST (SECTION WISE)",
							Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, detailQuery, 0, 0, "A4", "Landscape",signatureOption);

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
					hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/RptSectionWiseEmployee.jasper",
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
			showNotification("reportShow"+exp,Notification.TYPE_ERROR_MESSAGE);
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

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("430px");
		setHeight("220px");

		// lblCategory
		lblDepartment = new Label();
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		lblDepartment.setValue("Department Name :");
		mainLayout.addComponent(lblDepartment,"top:30.0px; left:30.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("200px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:28.0px; left:150.0px;");

		//sectionAll
		DepartmentAll = new CheckBox("All");
		DepartmentAll.setHeight("-1px");
		DepartmentAll.setWidth("-1px");
		DepartmentAll.setImmediate(true);
		mainLayout.addComponent(DepartmentAll, "top:30.0px; left:356.0px;");

		// lblCategory
		lblSection = new Label();
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Section Name :");
		mainLayout.addComponent(lblSection,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:150.0px;");

		//sectionAll
		sectionAll = new CheckBox("All");
		sectionAll.setHeight("-1px");
		sectionAll.setWidth("-1px");
		sectionAll.setImmediate(true);
		mainLayout.addComponent(sectionAll, "top:60.0px; left:356.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:150.0px;");
		
		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:90.0px;left:150.0px;");
		mainLayout.addComponent(cButton,"top:140.opx; left:140.0px");
		return mainLayout;
	}
}
