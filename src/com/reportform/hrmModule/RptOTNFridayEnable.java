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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptOTNFridayEnable extends Window 
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
	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptOTNFridayEnable(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("OT & FRIDAY ENABLE :: "+sessionBean.getCompany());
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
				if(DepartmentAll.booleanValue()==true)
				{
					addSectionName();
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
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

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!=null || DepartmentAll.booleanValue())
				{
					String section = "";
					if(cmbSection.getValue()!=null || sectionAll.booleanValue())
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
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					"tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where dept.vDepartmentId!='DEPT10' and (ein.otStatus=1 or " +
					"ein.FridayStatus=1) and ein.iStatus=1 and ISNULL(vProximityID,'')!=''  order by dept.vDepartmentName";
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
			String query="select distinct ein.vSectionId,sein.vDepartmentName,sein.SectionName from tbSectionInfo sein inner join " +
					"tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where (ein.otStatus=1 or ein.FridayStatus=1) " +
					"and ein.iStatus=1 and ISNULL(vProximityID,'')!='' and  (sein.vSectionId!='SEC-41' or sein.vSectionId!='SEC-42') order by sein.vDepartmentName,sein.SectionName";
			List <?> list = session.createSQLQuery(query).list();
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
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query = "select ei.employeeCode,ei.vProximityId,ei.vEmployeeId,ei.vEmployeeName,ei.vDepartmentID,sinf.vDepartmentName," +
					"ei.vSectionId,sinf.SectionName,ei.vDesignationId,din.designationName,ei.OtStatus,ei.FridayStatus from tbEmployeeInfo " +
					"ei inner join tbDesignationInfo din on ei.vDesignationId=din.designationId inner join tbSectionInfo sinf on " +
					"ei.vSectionId=sinf.vSectionId where ei.vDepartmentID not like 'DEPT10' and ei.vSectionId like '"+sectionName+"' and (ei.otStatus=1 or ei.FridayStatus=1) and " +
					"ei.iStatus=1 and ISNULL(vProximityID,'')!='' and (ei.vSectionId not like 'SEC-41' or ei.vSectionId not like 'SEC-42') and ei.iStatus like '"+status+"'  order by sinf.vDepartmentName,sinf.SectionName,ei.employeeCode,din.designationSerial,ei.dJoiningDate";
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptOTNFridayEnable.jasper",
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
		setWidth("490px");
		setHeight("260px");

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
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:28.0px; left:150.0px;");

		//sectionAll
		DepartmentAll = new CheckBox("All");
		DepartmentAll.setHeight("-1px");
		DepartmentAll.setWidth("-1px");
		DepartmentAll.setImmediate(true);
		mainLayout.addComponent(DepartmentAll, "top:30.0px; left:416.0px;");

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
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:150.0px;");

		//sectionAll
		sectionAll = new CheckBox("All");
		sectionAll.setHeight("-1px");
		sectionAll.setWidth("-1px");
		sectionAll.setImmediate(true);
		mainLayout.addComponent(sectionAll, "top:60.0px; left:416.0px;");
		
		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:90.0px;left:165.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:165.0px;");

		mainLayout.addComponent(new Label("______________________________________________________________________________________"), "top:125.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:145.opx; left:155.0px");
		return mainLayout;
	}
}
