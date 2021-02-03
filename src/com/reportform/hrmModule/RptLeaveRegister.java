package com.reportform.hrmModule;

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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptLeaveRegister extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblYear;
	private Label lblSectionName;

	private ComboBox cmbYear;
	private ComboBox cmbDepartmentName;
	private ComboBox cmbSectionName;

	private CheckBox chkDepartmentAll;
	private CheckBox chkSectionAll;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptLeaveRegister(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("LEAVE REGISTER :: "+sessionBean.getCompany());
		this.setWidth("500px");
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmbAddYear();

		setEventAction();
	}

	private void cmbAddYear()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String querySection = " SELECT distinct DATEPART(year, currentYear) as yearId,DATEPART(year, currentYear) as " +
					"balanceYear from tbLeaveBalanceNew ";
			List <?> list = session.createSQLQuery(querySection).list();	
			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbYear.addItem(element[0]);
				cmbYear.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbAddYear",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbDepartmentDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String str = " SELECT distinct lbn.vDepartmentID,dept.vDepartmentName from tbLeaveBalanceNew lbn inner join" +
					" tbDepartmentInfo dept on lbn.vDepartmentID=dept.vDepartmentId where " +
					"YEAR(currentYear)='"+cmbYear.getValue().toString()+"' and dept.vDepartmentName!='CHO' order by lbn.vDepartmentID ";
			List <?> list = session.createSQLQuery(str).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartmentName.addItem(element[0]);
				cmbDepartmentName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectionDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String str = " SELECT distinct lbn.vSectionID,sein.vDepartmentName,sein.SectionName from tbLeaveBalanceNew lbn " +
					"inner join tbSectionInfo sein on lbn.vSectionID=sein.vSectionID where " +
					"YEAR(currentYear)='"+cmbYear.getValue().toString()+"' and " +
					"lbn.vDepartmentID='"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' " +
					"order by sein.SectionName ";
			List <?> list = session.createSQLQuery(str).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEventAction()
	{
		cmbYear.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartmentName.removeAllItems();
				if(cmbYear.getValue()!=null)
				{
					cmbDepartmentDataAdd();
				}
			}
		});

		cmbDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(cmbDepartmentName.getValue()!=null)
				{
					cmbSectionDataAdd();
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(chkDepartmentAll.booleanValue()==true)
				{
					cmbDepartmentName.setEnabled(false);
					cmbDepartmentName.setValue(null);
					cmbSectionDataAdd();
				}
				else
				{
					cmbDepartmentName.setEnabled(true);
				}
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkSectionAll.booleanValue()==true)
				{
					cmbSectionName.setEnabled(false);
					cmbSectionName.setValue(null);
				}
				else
				{
					cmbSectionName.setEnabled(true);
				}
			}
		});

		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbYear.getValue()!=null)
				{
					if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
						{
							reportpreview();
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
				else
				{
					showNotification("Warning","Select Register Year",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("430px");
		setHeight("230px");

		// lblYear
		lblYear = new Label("Year :");
		lblYear.setImmediate(false);
		lblYear.setWidth("100.0%");
		lblYear.setHeight("-1px");
		mainLayout.addComponent(lblYear,"top:30.0px; left:20.0px;");

		// cmbYear
		cmbYear = new ComboBox();
		cmbYear.setImmediate(false);
		cmbYear.setWidth("100px");
		cmbYear.setHeight("-1px");
		cmbYear.setNullSelectionAllowed(true);
		cmbYear.setImmediate(true);
		mainLayout.addComponent(cmbYear, "top:28.0px; left:130.0px;");

		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setWidth("220px");
		cmbDepartmentName.setHeight("-1px");
		cmbDepartmentName.setNullSelectionAllowed(true);
		cmbDepartmentName.setImmediate(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:60.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartmentName, "top:58.0px; left:130.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:60.0px; left:355.0px;");

		// lblSectionName
		lblSectionName = new Label("Section :");
		lblSectionName.setImmediate(false);
		lblSectionName.setWidth("100.0%");
		lblSectionName.setHeight("-1px");
		mainLayout.addComponent(lblSectionName,"top:90.0px; left:20.0px;");

		// cmbSectionName
		cmbSectionName = new ComboBox();
		cmbSectionName.setWidth("220px");
		cmbSectionName.setHeight("-1px");
		cmbSectionName.setNullSelectionAllowed(true);
		cmbSectionName.setImmediate(true);
		mainLayout.addComponent(cmbSectionName, "top:88.0px; left:130.0px;");

		// chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:90.0px; left:355.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:120.0px;left:130.0px;");
		mainLayout.addComponent(cButton,"top:150.opx; left:125.0px");
		return mainLayout;
	}

	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String section = "%";
		String Department = "%";
		if(cmbSectionName.getValue()!=null)
		{
			section = cmbSectionName.getValue().toString();
		}
		if(cmbDepartmentName.getValue()!=null)
		{
			Department = cmbDepartmentName.getValue().toString();
		}
		
		try
		{
			String query=" select * from funLeaveRegister('"+cmbYear.getValue().toString()+"'," +
					"'"+Department+"','"+section+"') order by DepartmentName,sectionName,EmployeeID";

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("year", cmbYear.getValue().toString());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);
				
				Window win = new ReportViewer(hm,"report/account/hrmModule/RptLeaveRegister.jasper",
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
			showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
}
