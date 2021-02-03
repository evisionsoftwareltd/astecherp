package com.reportform.DoSalesModule;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.Window.Notification;

public class RptSRwiseDo extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblDivision;
	private ComboBox cmbDivision;

	private Label lblArea;
	private ComboBox cmbArea;

	private Label lblSR;
	private TextRead txtSR;

	private CheckBox chkAreaAll;
	private ReportDate reportTime = new ReportDate();
	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");
	
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat rptdateFormat = new SimpleDateFormat("dd-MM-yyyy");

	private ArrayList<Component> allComp = new ArrayList<Component>();

	public RptSRwiseDo(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("AS ON DATE WISE DO :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbAddDivisionData();
		btnAction();

		focusEnter();
	}

	private void focusEnter()
	{
		allComp.add(txtSR);

		allComp.add(button.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	private void btnAction()
	{
		button.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewReport();
			}
		});

		button.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbDivision.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbArea.setValue(null);
				if(cmbDivision.getValue()!=null)
				{
					cmbArea.removeAllItems();
					cmbAreaAddData();
				}
			}
		});

		chkAreaAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkAreaAll.booleanValue()==true)
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
		
		cmbArea.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)		
			{
				if(cmbArea.getValue()!=null)
				{
					try
					{
						Transaction tx= null;
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						tx = session.beginTransaction();
						List list = session.createSQLQuery("select vEmployeeName from tbAreaInfo where vDivisionId='"+cmbDivision.getValue()+"' and vAreaId='"+cmbArea.getValue()+"' ").list();
						Iterator iter = list.iterator();
						if( iter.hasNext()) 
						{
							Object[] element = (Object[]) iter.next();
							txtSR.setValue(element[0].toString());
						}
					}
					catch(Exception ex)
					{

					}
				}
			}
		});

	}

	private void previewReport()
	{
		String queryAll = null;
		String Area = "";
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			if(chkAreaAll.booleanValue()==true)
			{
				Area="%";
			}

			else
			{
				Area=cmbArea.getValue().toString();
			}

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String date=dateFormat.format(txtSR.getValue());
			System.out.println("Date: "+date);

			HashMap hm = new HashMap();

			queryAll = "select * from funAsOnDateDO('"+(dateFormat.format(txtSR.getValue()))+"','"+cmbDivision.getValue()+"','"+Area+"') where mBalQty>0 order by iSerial " ;
			
			System.out.println("Report Query: "+queryAll);

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("Date",reportTime.getTime);
			hm.put("AsOnDate",txtSR.getValue());
			hm.put("Author", sessionBean.getUserName());
		
			if(queryValueCheck(queryAll))
			{
				hm.put("sql", queryAll);

				Window win = new ReportViewer(hm,"report/account/DoSales/rptAsOnDateWiseDOt.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

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
				List list=session.createSQLQuery(" SELECT vAreaId,vAreaName FROM tbAreaInfo WHERE vDivisionId = '"+cmbDivision.getValue().toString()+"' ").list();

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

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("430px");
		setHeight("200px");

		lblDivision = new Label("Division / Zone :");
		lblDivision.setImmediate(true);
		lblDivision.setWidth("-1px");
		lblDivision.setHeight("-1px");
		mainLayout.addComponent(lblDivision, "top:20.0px; left:30.0px;");

		cmbDivision = new ComboBox();
		cmbDivision.setImmediate(true);
		cmbDivision.setWidth("200px");
		cmbDivision.setHeight("-1px");
		cmbDivision.setNullSelectionAllowed(false);
		cmbDivision.setNewItemsAllowed(false);
		cmbDivision.setImmediate(true);
		mainLayout.addComponent(cmbDivision, "top:18.0px; left:170px;");

		lblArea = new Label("Area / Region :");
		lblArea.setImmediate(true);
		lblArea.setWidth("-1px");
		lblArea.setHeight("-1px");
		mainLayout.addComponent(lblArea, "top:45.0px; left:30.0px;");

		cmbArea = new ComboBox();
		cmbArea.setImmediate(true);
		cmbArea.setWidth("200px");
		cmbArea.setHeight("-1px");
		cmbArea.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbArea, "top:43.0px; left:170px;");

		//CategoryAll
		chkAreaAll = new CheckBox("All");
		chkAreaAll.setHeight("-1px");
		chkAreaAll.setWidth("-1px");
		chkAreaAll.setImmediate(true);
		mainLayout.addComponent(chkAreaAll, "top:45.0px; left:366.0px;");

		// lblSR
		lblSR = new Label("MO/SR: ");
		lblSR.setImmediate(true);
		lblSR.setWidth("-1px");
		lblSR.setHeight("-1px");
		mainLayout.addComponent(lblSR, "top:70.0px; left:30.0px;");

		// txtSR
		txtSR = new TextRead();
		txtSR.setImmediate(true);
		txtSR.setWidth("220px");
		txtSR.setHeight("20px");
		txtSR.setImmediate(true);
		mainLayout.addComponent(txtSR,"top:68.0px; left:170px;");
		
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:90.0px;left:170.0px;");

		// CButton
		mainLayout.addComponent(button, "top:115.0px;left:130.0px;");

		return mainLayout;
	}
}
