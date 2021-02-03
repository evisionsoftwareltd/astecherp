package com.reportform.hrmModule;

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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class rptActiveInactiveEmployeeList extends Window 
{
	private SessionBean sessionBean;
	public AbsoluteLayout mainLayout;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private ComboBox cmbDesignation;

	private Label lblDepartment;
	private Label lblSection;
	private Label lblDesignation;


	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private CheckBox DepartmentAll = new CheckBox("All");
	private CheckBox sectionAll = new CheckBox("All");
	private CheckBox designationAll = new CheckBox("All");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private static final String CHO="'DEPT10'";
	public rptActiveInactiveEmployeeList(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("ACTIVE/INACTIVE EMPLOYEE LIST :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmbDepartmentDataAdd();
		cmbDesignationaddData();
		setEventAction();
		focusMove();
	}


	public void cmbDepartmentDataAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " select distinct vDepartmentId,vDepartmentName from tbInactiveEmployee where ISNULL(vDepartmentID,'')!='' and vDepartmentID!="+CHO+" order " +
					"by vDepartmentName";

			List <?> list=session.createSQLQuery(sql).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
			String sql = " select distinct vSectionId,vDepartmentName,vSectionName from tbInactiveEmployee where vDepartmentId like '"+DepartmentID+"' and vDepartmentID!="+CHO+" order by vSectionName"; 

			List <?> list=session.createSQLQuery(sql).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString() + "(" + element[2].toString() + ")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionAddData",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}

		finally
		{
			session.close();
		}
	}

	public void cmbDesignationaddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String desigQuery="select distinct ein.vDesignationId,din.designationName,din.designationSerial from tbInactiveEmployee " +
					"ein inner join tbDesignationInfo din on ein.vDesignationId=din.designationId where " +
					"ISNULL(ein.vProximityId,'')!=''  order by din.designationName";
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
		finally
		{
			session.close();
		}
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

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!= null)
					cmbSectionAddData(cmbDepartment.getValue().toString());
			}
		});

		DepartmentAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				cmbSection.removeAllItems();
				if(DepartmentAll.booleanValue())
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
	}

	private void formValidation()
	{
		if(cmbDepartment.getValue()!=null || DepartmentAll.booleanValue())
		{
			if(cmbSection.getValue()!=null || sectionAll.booleanValue())
			{
				if(cmbDesignation.getValue()!=null || designationAll.booleanValue())
				{
					reportShow();
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

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		try
		{
			query="select dInactiveDate,dActiveDate,convert(varchar,convert(date,dInactiveDate)) inactiveDate,convert(varchar,convert(date,dActiveDate)) activeDate," +
					"vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName,vDepartmentId,vDepartmentName,vSectionID,vSectionName,vDesignationID, " +
					"vDesignationName,vActive_Inactive,vPermittedBy,vReason,vUserName,vUserIP,dEntryTime from tbInactiveEmployee where " +
					"vDepartmentId like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' and vDepartmentID!="+CHO+" and vSectionID like " +
					"'"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and vDesignationID like " +
					"'"+(cmbDesignation.getValue()!=null?cmbDesignation.getValue().toString():"%")+"' order by vDepartmentName,vSectionName,vEmployeeName";
			System.out.println("Report Query:"+query);
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone",sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptActiveInactiveEmployeeList.jasper",
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

	private void focusMove()
	{
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbDesignation);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("460px");
		setHeight("250px");

		lblDepartment = new Label("Department : ");
		lblDepartment.setImmediate(true);
		lblDepartment.setWidth("100px");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment, "top:20px; left:40px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(cmbDepartment, "top:18.0px; left:120.0px;");

		DepartmentAll = new CheckBox("All");
		DepartmentAll.setImmediate(true);
		DepartmentAll.setWidth("-1px");
		DepartmentAll.setHeight("-1px");
		mainLayout.addComponent(DepartmentAll,"top:20px; left:385px;");

		// lblSection
		lblSection = new Label("Section : ");
		lblSection.setImmediate(true);
		lblSection.setWidth("100px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:50px; left:40px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(cmbSection, "top:48.0px; left:120.0px;");

		// sectionAll 
		sectionAll = new CheckBox("All");
		sectionAll.setImmediate(true);
		sectionAll.setWidth("-1px");
		sectionAll.setHeight("-1px");
		mainLayout.addComponent(sectionAll,"top:50px; left:385px;");

		// lblDesignation
		lblDesignation = new Label("Designation : ");
		lblDesignation.setImmediate(true);
		lblDesignation.setWidth("100px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation, "top:80px; left:40px;");

		// cmbDesignation
		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("260px");
		cmbDesignation.setHeight("-1px");
		mainLayout.addComponent(cmbDesignation, "top:78.0px; left:120.0px;");

		// designationAll 
		designationAll = new CheckBox("All");
		designationAll.setImmediate(true);
		designationAll.setWidth("-1px");
		designationAll.setHeight("-1px");
		mainLayout.addComponent(designationAll,"top:80px; left:385px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:120.0px;left:150.0px;");

		mainLayout.addComponent(new Label("__________________________________________________________________________________________________"), "top:145.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton, "top:170.0px;left:140.0px;");
		return mainLayout;
	}
}
