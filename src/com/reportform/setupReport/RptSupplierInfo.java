package com.reportform.setupReport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.Window.Notification;

public class RptSupplierInfo extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblPartyName;

	private ComboBox cmbPartyName;

	private OptionGroup RadioBtnGroup;

	private Label lblDivision;
	private ComboBox cmbDivision;
	private CheckBox chkDivision;

	private Label lblArea;
	private ComboBox cmbArea;
	private CheckBox chkArea;

	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");
	private ReportDate reportTime = new ReportDate();

	public RptSupplierInfo(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("SUPPLIER INFORMATION :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		btnAction();
		cmbAddDivisionData();
	}

	private void btnAction()
	{
		button.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if (cmbDivision.getValue()!=null || chkDivision.booleanValue()==true)
				{
					if (cmbArea.getValue()!=null || chkArea.booleanValue()==true)
					{
						previewBtnAction();
					}
					else 
					{
						showNotification("Please Select Zone", Notification.TYPE_WARNING_MESSAGE);	
						cmbArea.focus();
					}
				}
				else 
				{
					showNotification("Please Select Division", Notification.TYPE_WARNING_MESSAGE);
					cmbDivision.focus();
				}
			}


		});

		cmbDivision.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbArea.removeAllItems();
				cmbArea.setValue(null);
				if(cmbDivision.getValue()!=null)
				{
					cmbAreaAddData();
				}
			}
		});

		chkArea.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkArea.booleanValue()==true)
				{
					cmbArea.setValue(null);
					cmbArea.setEnabled(false);
				}
				else
				{
					cmbArea.setEnabled(true);
				}
			}
		});

		chkDivision.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkDivision.booleanValue()==true)
				{
					chkArea.setEnabled(false);
					chkArea.setValue(true);
					cmbDivision.setValue(null);
					cmbDivision.setEnabled(false);
					cmbArea.setValue(null);
					cmbArea.setEnabled(false);
				}
				else
				{
					cmbArea.setEnabled(true);
					cmbDivision.setEnabled(true);
					chkArea.setEnabled(true);
					chkArea.setValue(false);
				}
			}
		});


		button.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	public void cmbAddDivisionData()
	{
		cmbDivision.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vDivisionId,vDivisionName from tbDivisionInfo order by iAutoId").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbDivision.addItem(element[0].toString());
				cmbDivision.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbAreaAddData()
	{
		Transaction tx=null;

		if(cmbDivision.getValue()!=null)
		{
			try
			{

				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery(" SELECT vAreaId,vAreaName FROM tbAreaInfo WHERE vDivisionId ='"+cmbDivision.getValue().toString()+"' ").list();

				for(Iterator iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbArea.addItem(element[0].toString());
					cmbArea.setItemCaption(element[0].toString(), element[1].toString());

				}
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error:",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}

		}
	}

	private void previewBtnAction()
	{
		String queryAll = null;
		String Division ="";
		String Area ="";
		try
		{
			if(chkDivision.booleanValue()==true)
			{
				Division="%";
				Area="%";
			}
			else
			{
				Division=cmbDivision.getValue().toString();
				if(chkArea.booleanValue()==true)
				{
					Area="%";
				}
				else
				{
					Area=cmbArea.getValue().toString();
				}
			}
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();

			queryAll = "select * from tbSupplierInfo where GroupId like '"+Division+"' and SubGroupId like '"+Area+"' and isActive='1' order by supplierName";

			System.out.println("Report Query: "+queryAll);

			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("company", sessionBean.getCompany());
			hm.put("UserIp", sessionBean.getUserIp());
			hm.put("UserName", sessionBean.getUserName());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("SysDate",reportTime.getTime);

			hm.put("sql", queryAll);

			Window win = new ReportViewer(hm,"report/account/SetupTransaction/rptSupplierInfo.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("420px");
		setHeight("200px");

		lblDivision = new Label("Division :");
		lblDivision.setImmediate(true);
		lblDivision.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblDivision, "top:20px;left:30px;");

		cmbDivision=new ComboBox();
		cmbDivision.setImmediate(true);
		cmbDivision.setWidth("200px");
		cmbDivision.setHeight("-1px");
		cmbDivision.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDivision, "top:18px;left:150px;");

		//CategoryAll
		chkDivision = new CheckBox("All");
		chkDivision.setHeight("-1px");
		chkDivision.setWidth("-1px");
		chkDivision.setImmediate(true);
		mainLayout.addComponent(chkDivision, "top:20px; left:350.0px;");

		lblArea = new Label("Zone :");
		lblArea.setImmediate(true);
		lblArea.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblArea, "top:45px;left:30px;");

		cmbArea=new ComboBox();
		cmbArea.setImmediate(true);
		cmbArea.setWidth("200px");
		cmbArea.setHeight("-1px");
		cmbArea.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbArea, "top:43px;left:150px;");

		chkArea = new CheckBox("All");
		chkArea.setHeight("-1px");
		chkArea.setWidth("-1px");
		chkArea.setImmediate(true);
		mainLayout.addComponent(chkArea, "top:45px; left:350.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:70.0px;left:145.0px;");		

		//CButton
		mainLayout.addComponent(button, "top:100.0px;left:130.0px;");

		return mainLayout;
	}
}
