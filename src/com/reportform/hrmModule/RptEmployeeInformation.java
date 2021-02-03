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
public class RptEmployeeInformation extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private ComboBox cmbDynamic;
	private OptionGroup radioButtonGroup;

	private Label lblComboLabel;
	private CommonButton cButton= new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private static final List<String> groupButton=Arrays.asList(new String[]{"Card","Proximity ID","Name"});

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private ReportDate reportTime = new ReportDate();

	public RptEmployeeInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EMPLOYEE INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setBtnAction();
		setContent(mainLayout);
		initialCombo();
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
				if(event.getProperty().toString()=="Card")
				{
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					lblComboLabel.setValue("Card No :");
					selectCardNo();
					System.out.println("Card id :");
				}

				else if(event.getProperty().toString()=="Proximity ID")
				{
					System.out.println("Proximity id :");
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					lblComboLabel.setValue("Proximity id :");
					selectProximityId();
				}

				else if(event.getProperty().toString()=="Name")
				{
					System.out.println("Name :");
					cmbDynamic.removeAllItems();
					cmbDynamic.setValue(null);
					lblComboLabel.setValue("Emp. Name :");
					selectName();
				}
			}
		});
	}

	private void selectCardNo()
	{
		cmbDynamic.removeAllItems();
		String query = " select 0,vEmployeeId from tbEmployeeInfo order by iFingerID asc ";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();		
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDynamic.addItem(element[1].toString());
				cmbDynamic.setItemCaption(element[1], element[1].toString());
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
		cmbDynamic.removeAllItems();
		String query = "select vEmployeeId,vProximityId from tbEmployeeInfo order by iFingerID asc";
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
		cmbDynamic.removeAllItems();
		String query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo order by iFingerID asc";
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
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("400px");
		setHeight("200px");

		// optionGroup
		radioButtonGroup = new OptionGroup("",groupButton);
		radioButtonGroup.setImmediate(true);
		radioButtonGroup.setStyleName("horizontal");
		radioButtonGroup.setValue("Card");
		mainLayout.addComponent(radioButtonGroup, "top:25.0px; left:90.0px;");

		//ComboLabel
		lblComboLabel=new Label("Card No :");
		lblComboLabel.setImmediate(true);
		lblComboLabel.setWidth("100px");
		mainLayout.addComponent(lblComboLabel, "top:60.0px; left:30.0px;");

		// comboBox_1
		cmbDynamic = new ComboBox();
		cmbDynamic.setImmediate(true);
		cmbDynamic.setWidth("195px");
		cmbDynamic.setHeight("-1px");
		mainLayout.addComponent(cmbDynamic, "top:57.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:90.0px;left:130.0px;");

		cButton.btnPreview.setImmediate(true);
		mainLayout.addComponent(cButton, "top:120.0px; left:110.0px;");
		return mainLayout;
	}

	private void initialCombo()
	{
		cmbDynamic.removeAllItems();
		String query = "select 0,vEmployeeId from tbEmployeeInfo order by iFingerID asc";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();		
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDynamic.addItem(element[1].toString());
				cmbDynamic.setItemCaption(element[1], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("selectCardNo", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportView()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());

		try
		{
			String query = " SELECT * from vw_rptEmployeeifo as a" +
					" left join tbEducation as b on a.vEmployeeId = b.vEmployeeId" +
					" Where a.vEmployeeId ='"+cmbDynamic.getValue().toString()+" '";

			if(queryCheckValue(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
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
			List <?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("selectCardNo", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}
}
