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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class ReportEmployeeInformation extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private ComboBox cmbDynamic;
	private OptionGroup radioButtonGroup;
    
	private Label lblComboLabel;
	private CommonButton cButton= new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	private static final List<String> groupButton=Arrays.asList(new String[]{"Employee ID","Proximity ID","Name"});
	private ReportDate reportTime = new ReportDate();
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public ReportEmployeeInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setBtnAction();
		setContent(mainLayout);
		selectCardNo();
	}

	private void setBtnAction()
	{
		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbDynamic.getValue()!=null)
				{
					reportView();
				}
				else
				{
					getParent().showNotification("Please Select "+lblComboLabel.getValue().toString()+"", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener(new ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		radioButtonGroup.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(event.getProperty().toString()=="Employee ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					lblComboLabel.setValue("Employee ID :");
					selectCardNo();
					System.out.println("Employee ID:");
				}
				else if(event.getProperty().toString()=="Proximity ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					lblComboLabel.setValue("Proximity ID :");
					selectProximityId();
				}
				else if(event.getProperty().toString()=="Name")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					lblComboLabel.setValue("Employee Name :");
					selectName();
				}
			}
		});
		RadioBtnStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(event.getProperty().toString()=="Active" && radioButtonGroup.getValue().toString()=="Employee ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					selectCardNo();
				}
				if(event.getProperty().toString()=="Active" && radioButtonGroup.getValue().toString()=="Proximity ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					selectProximityId();
				}
				if(event.getProperty().toString()=="Active" && radioButtonGroup.getValue().toString()=="Name")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					selectName();
				}
				if(event.getProperty().toString()=="Left" && radioButtonGroup.getValue().toString()=="Employee ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					selectCardNo();
				}
				if(event.getProperty().toString()=="Left" && radioButtonGroup.getValue().toString()=="Proximity ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					selectProximityId();
				}
				if(event.getProperty().toString()=="Left" && radioButtonGroup.getValue().toString()=="Name")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					selectName();
				}
				if(event.getProperty().toString()=="All" && radioButtonGroup.getValue().toString()=="Employee ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					selectCardNo();
				}
				if(event.getProperty().toString()=="All" && radioButtonGroup.getValue().toString()=="Proximity ID")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					selectProximityId();
				}
				if(event.getProperty().toString()=="All" && radioButtonGroup.getValue().toString()=="Name")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					selectName();
				}
				
				
			}
		});
	}

	private void selectCardNo()
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
		cmbDynamic.removeAllItems();
		String query = " select vEmployeeId,employeeCode from tbEmployeeInfo where iStatus like '"+status+"' order by iFingerID asc ";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();		
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDynamic.addItem(element[0].toString());
				cmbDynamic.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("selectCardNo", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void selectProximityId()
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
		cmbDynamic.removeAllItems();
		String query = "select vEmployeeId,vProximityId from tbEmployeeInfo where iStatus like '"+status+"'  order by iFingerID asc";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();		
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDynamic.addItem(element[0].toString());
				cmbDynamic.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("selectCardNo", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void selectName()
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

		cmbDynamic.removeAllItems();
		String query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo where iStatus like '"+status+"' order by iFingerID asc";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();		
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDynamic.addItem(element[0].toString());
				cmbDynamic.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("selectCardNo", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("400px");
		setHeight("230px");
		

		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:10.0px;left:40.0px;");

		// optionGroup
		radioButtonGroup = new OptionGroup("",groupButton);
		radioButtonGroup.setImmediate(true);
		radioButtonGroup.setStyleName("horizontal");
		radioButtonGroup.setValue("Employee ID");
		mainLayout.addComponent(radioButtonGroup, "top:45.0px; left:40.0px;");

		//ComboLabel
		lblComboLabel=new Label("Employee ID :");
		lblComboLabel.setImmediate(true);
		lblComboLabel.setWidth("100px");
		mainLayout.addComponent(lblComboLabel, "top:80.0px; left:30.0px;");

		// comboBox_1
		cmbDynamic = new ComboBox();
		cmbDynamic.setImmediate(true);
		cmbDynamic.setWidth("195px");
		cmbDynamic.setHeight("-1px");
		mainLayout.addComponent(cmbDynamic, "top:78.0px; left:140.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:105.0px;left:170.0px;");

		cButton.btnPreview.setImmediate(true);
		mainLayout.addComponent(cButton, "top:130.0px; left:120.0px;");

		return mainLayout;
	}

	private void reportView()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			String query = " SELECT * from vw_rptEmployeeifo as a" +
					" left join tbEducation as b on a.vEmployeeId = b.vEmployeeId" +
					" Where a.vEmployeeId ='"+cmbDynamic.getValue().toString()+"'";

			if(queryCheckValue(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptEmployeeInfo.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
				showNotification("Warning", "No Data Found", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("reportView",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean queryCheckValue(String query)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("Warning", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}
}
