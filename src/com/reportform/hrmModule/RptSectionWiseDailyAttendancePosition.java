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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
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
public class RptSectionWiseDailyAttendancePosition extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblDepartment;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartmentAll;
	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox chkSectionAll;
	private Label lblDate;
	private PopupDateField dDate;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	SimpleDateFormat dfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private static final String CHO="'DEPT10'";
	public RptSectionWiseDailyAttendancePosition(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("DAILY ATTENDANCE POSITION :: "+sessionBean.getCompany());
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
					addSectionName(cmbDepartment.getValue().toString());
				}
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
					addSectionName("%");
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});

		chkSectionAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkSectionAll.booleanValue()==true)
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
				if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						String section = "%";
						String Department="%";

						if(cmbDepartment.getValue()!=null)
						{Department = (cmbDepartment.getValue().toString());}

						if(cmbSection.getValue()!=null)
						{section = (cmbSection.getValue().toString());}

						reportShow(Department,section);
					}
					else
					{
						showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
						cmbSection.focus();
					}
				}
				else
				{
					showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
					cmbSection.focus();
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
					"tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where ein.vDepartmentId!="+CHO+" order by dept.vDepartmentName";
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
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addSectionName(String DepartmentID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ein.vSectionId,sein.vDepartmentName,sein.SectionName from tbSectionInfo sein inner join " +
					"tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where sein.vDepartmentID like '"+DepartmentID+"' " +
					" and ein.vDepartmentId!="+CHO+" order by sein.vDepartmentName,sein.SectionName";
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

	private void reportShow(Object departmentId,Object sectionId)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query="select * from funDailyEmployeeAttendance"+
					"('"+dfYMD.format(dDate.getValue())+"','"+dfYMD.format(dDate.getValue())+"','%','"+departmentId+"','"+sectionId+"') where vDepartmentId!="+CHO+" ";
			
			if(queryValueCheck(query))
			{
				query="select distinct dept.vDepartmentName,sein.SectionName,COUNT(ein.vEmployeeId) totalEmp," +
						"(select COUNT(fcea.vEmployeeId) from funDailyEmployeeAttendance('"+dfYMD.format(dDate.getValue())+"'," +
						"'"+dfYMD.format(dDate.getValue())+"','%','"+departmentId+"','"+sectionId+"') fcea where " +
						"fcea.vSectionId=ein.vSectionId) totalPre,ISNULL((select COUNT(el.vAutoEmployeeId) from tbEmployeeLeave el where " +
						"el.vDepartmentID=ein.vDepartmentID and el.vSectionID=ein.vSectionId and " +
						"el.dSenctionFrom<='"+dfYMD.format(dDate.getValue())+"' and dSenctionTo>='"+dfYMD.format(dDate.getValue())+"' " +
						"group by el.vDepartmentID,el.vSectionID),0) totalLeave from tbEmployeeInfo ein inner join tbDepartmentInfo dept " +
						"on ein.vDepartmentId=dept.vDepartmentId inner join tbSectionInfo sein on " +
						"ein.vSectionId=sein.vSectionId where ein.iStatus=1 and ISNULL(ein.vProximityID,'')!='' and ein.vDepartmentID like '"+departmentId+"' " +
						"and ein.vsectionId like '"+sectionId+"'  ein.vDepartmentId!="+CHO+" group by ein.vDepartmentId,dept.vDepartmentName,ein.vSectionId," +
						"sein.SectionName";
				
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("date", new SimpleDateFormat().format(dDate.getValue()));
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("date", new SimpleDateFormat("dd-MM-yyyy").format(dDate.getValue()));
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptDailyAttendencePosition.jasper",
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
		setWidth("475px");
		setHeight("240px");

		// lblAsOnDate
		lblDate = new Label("Date :");
		lblDate.setImmediate(true);
		lblDate.setWidth("100%");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:30.0px;");

		// asOnDate
		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:18.0px; left:150.0px;");

		// lblCategory
		lblDepartment = new Label();
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		lblDepartment.setValue("Department Name :");
		mainLayout.addComponent(lblDepartment,"top:50.0px; left:30.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:48.0px; left:150.0px;");

		//chkSectionAll
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:50.0px; left:415.0px;");

		// lblCategory
		lblSection = new Label();
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Section Name :");
		mainLayout.addComponent(lblSection,"top:80.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:78.0px; left:150.0px;");

		//chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:80.0px; left:415.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:150.0px;");

		mainLayout.addComponent(new Label("___________________________________________________________________________________"), "top:120.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:150.opx; left:160.0px");
		return mainLayout;
	}
}
