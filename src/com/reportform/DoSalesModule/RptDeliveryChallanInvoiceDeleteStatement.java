package com.reportform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptDeliveryChallanInvoiceDeleteStatement extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblDate;
	private PopupDateField dFromDate;
	private PopupDateField dToDate;

	private Label lblPartyName;
	private ComboBox cmbPartyName;
	private CheckBox chkPartyAll;

	private Label lblUserName;
	private ComboBox cmbUserName;
	private CheckBox chkUserNameAll;

	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});

	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	public RptDeliveryChallanInvoiceDeleteStatement(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption(" INVOICE DELETE STATEMENT :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		actionEvent();
		
		IniStep();
	}

	private void IniStep() {
		dToDate.setEnabled(false);
		cmbUserName.setEnabled(false);
		chkUserNameAll.setEnabled(false);
		cmbPartyName.setEnabled(false);
		chkPartyAll.setEnabled(false);
	}

	public void actionEvent()
	{
		
		dFromDate.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				dToDate.setEnabled(true);
				addUserName();
				cmbUserName.setEnabled(true);
				chkUserNameAll.setEnabled(true);
			}
		});
		button.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				checkParameter();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbUserName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
			
					cmbPartyName.setEnabled(true);
					chkPartyAll.setEnabled(true);
					addPartyName();
				
			}
		});

		chkUserNameAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkUserNameAll.booleanValue())
				{
					cmbPartyName.setEnabled(true);
					chkPartyAll.setEnabled(true);
					cmbUserName.setEnabled(false);
					cmbUserName.setValue(null);
					addPartyName();
				}
				else
				{
					cmbPartyName.setEnabled(false);
					chkPartyAll.setEnabled(false);
					cmbUserName.setEnabled(true);
				}
			}
		});

		chkPartyAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				addUserName();
				if(chkPartyAll.booleanValue())
				{
					cmbPartyName.setEnabled(false);
					cmbPartyName.setValue(null);
				}
				else
				{
					cmbPartyName.setEnabled(true);
					addPartyName();
				}
			}
		});
	}

	private void checkParameter()
	{
		if(cmbPartyName.getValue()!=null || chkPartyAll.booleanValue())
		{
				if(cmbUserName.getValue()!=null || chkUserNameAll.booleanValue())
				{
					reportPreview();
				}
				else
				{
					showNotification("Warning!","Please Select User Name.",Notification.TYPE_WARNING_MESSAGE);
					cmbUserName.focus();
				}
		}
		else
		{
			showNotification("Warning!","Please Select Party Name.",Notification.TYPE_WARNING_MESSAGE);
			cmbPartyName.focus();
		}
	}

	private void addPartyName()
	{
		
		
		String UserName = "";
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		if(chkUserNameAll.booleanValue()==true){
			UserName="%";
		}
		else
		{
			UserName=cmbUserName.getValue().toString();
		}
		
		try
		{
			String sql = "select Distinct vPartyId,vPartyName from tbUdDeliveryChallanInfoDetails where vUserName like '"+UserName+"'  ";
			
			System.out.println(" Party Name sql :"+sql);
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0]);
				cmbPartyName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addPartyName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addUserName()
	{
		cmbUserName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = "select Distinct vUserName,vUserName from tbUdDeliveryChallanInfoDetails ";
			
			System.out.println(" User Name sql :"+sql);
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUserName.addItem(element[0]);
				cmbUserName.setItemCaption(element[0], element[0].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void reportPreview()
	{
		ReportOption RadioBtn = new ReportOption(RadioBtnGroup.getValue().toString());
		String Party = (chkPartyAll.booleanValue()?"%":(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():""));
		String UserName = (chkUserNameAll.booleanValue()?"%":(cmbUserName.getValue()!=null?cmbUserName.getValue().toString():""));

		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();

			String query = "select entryTime,dInvoiceDate,vBillNo,vChallanNo,vPartyName, "
					+ " vProductName,vProductUnit,mChallanQty,userName,userIp "
					+ "from tbUdSalesInvoiceInfoDetails "
					+ " where convert(date, entryTime,105) between '"+df.format(dFromDate.getValue())+"' "
							+ "and '"+df.format(dToDate.getValue())+"' and"
					+ " userName like '"+UserName+"' and vPartyId like '"+Party+"' order by entryTime,iAutoId";
			
			/*dd-MM-yyyy  hh:mm:ss aaa*/
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("username", sessionBean.getUserName() +"  "+  sessionBean.getUserIp());
			hm.put("fromDate", "From  "+df.format(dFromDate.getValue())+"  to  "+df.format(dToDate.getValue()));
			hm.put("toDate", dToDate.getValue());

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/finishedGoods/RptDeliveryChallanInvoiceDeleteStatement.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try 
		{
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
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
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("500px");
		mainLayout.setHeight("260px");
		mainLayout.setMargin(false);

		
		lblDate = new Label("From Date : ");
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:30.0px;" );

		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:18.0px; left:120.0px;" );

		mainLayout.addComponent(new Label(" To Date : "), "top:50.0px; left:30.0px;" );

		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:48.0px; left:120.0px;" );

		lblUserName = new Label("User Name : ");
		lblUserName.setImmediate(true);
		lblUserName.setWidth("-1px");
		lblUserName.setHeight("-1px");
		mainLayout.addComponent(lblUserName, "top:80.0px; left:30.0px;");

		cmbUserName = new ComboBox();
		cmbUserName.setImmediate(true);
		cmbUserName.setWidth("250px");
		cmbUserName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbUserName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbUserName, "top:78.0px; left:120.0px;");

		chkUserNameAll = new CheckBox("All");
		chkUserNameAll.setImmediate(true);
		chkUserNameAll.setHeight("-1px");
		chkUserNameAll.setWidth("-1px");
		mainLayout.addComponent(chkUserNameAll, "top:79.0px; left:380.0px;");
		
		lblPartyName = new Label("Party Name : ");
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		mainLayout.addComponent(lblPartyName, "top:108.0px; left:30.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("250px");
		cmbPartyName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbPartyName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbPartyName, "top:106.0px; left:120.0px;");

		chkPartyAll = new CheckBox("All");
		chkPartyAll.setImmediate(true);
		chkPartyAll.setHeight("-1px");
		chkPartyAll.setWidth("-1px");
		mainLayout.addComponent(chkPartyAll, "top:107.0px; left:380.0px;");


		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		//mainLayout.addComponent(RadioBtnGroup, "top:170.0px; left:125.0px;");

		mainLayout.addComponent(button,"top:170.opx; left:150.0px");

		return mainLayout;
	}
}