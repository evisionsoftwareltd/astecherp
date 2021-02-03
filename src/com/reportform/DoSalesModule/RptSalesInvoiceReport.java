package com.reportform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
public class RptSalesInvoiceReport extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblInvoiceDate;
	private PopupDateField dFromInvoiceDate;
	private PopupDateField dToInvoiceDate;

	private Label lblPartyName;
	private ComboBox cmbPartyName;

	private Label lblSalesInvoice;
	private ComboBox cmbSalesInvoice;
	private CheckBox chkAll = new CheckBox();

	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});

	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public RptSalesInvoiceReport(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("SALES INVOICE REPORT :: "+sessionBean.getCompany());
		this.setWidth("350px");
		this.setHeight("250px");
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		actionEvent();
		addPartyName();
	}

	public void actionEvent()
	{
		button.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbSalesInvoice.getValue()!=null || chkAll.booleanValue()==true)
				{
					reportPreview();
				}
				else
				{
					showNotification("Warning!","Select Invoice No",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		dFromInvoiceDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbPartyName.removeAllItems();
				if(dFromInvoiceDate.getValue()!=null)
					addPartyName();
			}
		});

		dToInvoiceDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbPartyName.removeAllItems();
				if(dToInvoiceDate.getValue()!=null)
					addPartyName();
			}
		});

		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSalesInvoice.removeAllItems();
				if(cmbPartyName.getValue()!=null)
				{
					addSalesInvoice();
				}
			}
		});

		chkAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkAll.booleanValue()==true)
				{
					cmbSalesInvoice.setEnabled(false);
					cmbSalesInvoice.setValue(null);
				}
				else
				{
					cmbSalesInvoice.setEnabled(true);
				}
			}
		});
	}

	private void addPartyName()
	{
		cmbPartyName.removeAllItems();
		cmbPartyName.setValue(null);
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String sql = " select distinct vPartyId,vPartyName from tbSalesInvoiceInfo where " +
					" dInvoiceDate between CONVERT(datetime,'"+new SimpleDateFormat("yyyy-MM-dd").format(dFromInvoiceDate.getValue())+"'+ ' 00:00:00',121) and" +
					" CONVERT(datetime, '"+new SimpleDateFormat("yyyy-MM-dd").format(dToInvoiceDate.getValue())+"'+ ' 23:59:59',121) " +
					" order by vPartyName desc ";
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

	private void addSalesInvoice()
	{
		cmbSalesInvoice.removeAllItems();
		cmbSalesInvoice.setValue(null);
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String sql = " select 0 zero,vBillNo from tbSalesInvoiceInfo where " +
					" dInvoiceDate between " +
					" CONVERT(datetime,'"+new SimpleDateFormat("yyyy-MM-dd").format(dFromInvoiceDate.getValue())+"'+ ' 00:00:00',121) and" +
					" CONVERT(datetime, '"+new SimpleDateFormat("yyyy-MM-dd").format(dToInvoiceDate.getValue())+"'+ ' 23:59:59',121) " +
					" and vPartyID = '"+cmbPartyName.getValue()+"' order by dInvoiceDate desc ";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSalesInvoice.addItem(element[1]);
				cmbSalesInvoice.setItemCaption(element[1], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void reportPreview()
	{
		String subReportQueryBill = null;
		ReportOption RadioBtn = new ReportOption(RadioBtnGroup.getValue().toString());

		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();

			String queryBill = "SELECT vPartyName,vPartyAddress,vPartyMobile,vBillNo,dBillDate,vProductId,vProductName," +
					" mChallanQty,vProductUnit,mProductRate,mAmount,vInWrod,mCommissionAmnt,mVatAmount,mBillAmount," +
					" mTruckFare,mNetAmount,dDatetime,vRemarks,vChallaNo,vUserName,vDelChallanNo,dChallanDate," +
					" vVatChallanNo,vTruckNo,vDriverName,vDestination,vDoNo,dDoDate,vDepoName from " +
					" funSalesInvoiceReport('"+cmbPartyName.getValue()+"'," +
					" '"+(cmbSalesInvoice.getValue()!=null?cmbSalesInvoice.getValue().toString():"%")+"'," +
					" '"+dateFormat.format(dFromInvoiceDate.getValue())+"'," +
					" '"+dateFormat.format(dToInvoiceDate.getValue())+"')";

			/*queryBill = "SELECT funSIR.vPartyName,funSIR.vPartyAddress,funSIR.vPartyMobile,funSIR.vBillNo," +
					" funSIR.dBillDate,funSIR.vProductId,funSIR.vProductName,funSIR.mChallanQty," +
					" funSIR.vProductUnit,funSIR.mProductRate,funSIR.mAmount,funSIR.vInWrod," +
					" funSIR.mCommissionAmnt,funSIR.mVatAmount,funSIR.mBillAmount,funSIR.mTruckFare," +
					" funSIR.mNetAmount,funSIR.dDatetime,funSIR.vRemarks,funSIR.vChallaNo,funSIR.vUserName," +
					" dci.vDelChallanNo,dci.vVatChallanNo,dci.dChallanDate,dci.vTruckNo,dci.vDriverName,dci.vDestination," +
					" dcd.vProductUnit,dcd.mChallanQty,dcd.vDoNo,Convert (date,dcd.dDoDate) dDoDate," +
					" dci.vDepoName from funSalesInvoiceReport('"+cmbPartyName.getValue()+"'," +
					"'"+(cmbSalesInvoice.getValue()!=null?cmbSalesInvoice.getValue().toString():"%")+"'," +
					" '"+dateFormat.format(dFromInvoiceDate.getValue())+"','"+dateFormat.format(dToInvoiceDate.getValue())+"') funSIR " +
					" inner join tbDeliveryChallanDetails dcd on funSIR.vChallaNo=dcd.vChallanNo inner join" +
					" tbDeliveryChallanInfo dci on funSIR.vChallaNo=dci.vChallanNo";*/
			//hm.put("path", "./report/account/DoSales/");
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("subsql", subReportQueryBill);

			if(queryValueCheck(queryBill))
			{
				hm.put("sql", queryBill);

				Window win = new ReportViewer(hm,"report/account/DoSales/rptInvoiceBill.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning!","There are no Data",Notification.TYPE_WARNING_MESSAGE);
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
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		return false;
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("390px");
		mainLayout.setHeight("180px");
		mainLayout.setMargin(false);

		setWidth("420px");
		setHeight("225px");

		lblInvoiceDate = new Label("Invoice Date : ");
		lblInvoiceDate.setImmediate(true);
		lblInvoiceDate.setWidth("-1px");
		lblInvoiceDate.setHeight("-1px");
		mainLayout.addComponent(lblInvoiceDate, "top:35.0px; left:30.0px;");

		dFromInvoiceDate = new PopupDateField();
		dFromInvoiceDate.setImmediate(true);
		dFromInvoiceDate.setWidth("110px");
		dFromInvoiceDate.setDateFormat("dd-MM-yyyy");
		dFromInvoiceDate.setValue(new java.util.Date());
		dFromInvoiceDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromInvoiceDate, "top:35.0px; left:120.0px;");

		mainLayout.addComponent(new Label(" To "), "top:35.0px; left:235.0px;");

		dToInvoiceDate = new PopupDateField();
		dToInvoiceDate.setImmediate(true);
		dToInvoiceDate.setWidth("110px");
		dToInvoiceDate.setDateFormat("dd-MM-yyyy");
		dToInvoiceDate.setValue(new java.util.Date());
		dToInvoiceDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToInvoiceDate, "top:33.0px; left:260.0px;");

		lblPartyName = new Label("Party Name : ");
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		mainLayout.addComponent(lblPartyName, "top:60.0px; left:30.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("250px");
		mainLayout.addComponent(cmbPartyName, "top:58.0px; left:120.0px;");

		lblSalesInvoice = new Label("Invoice No : ");
		lblSalesInvoice.setImmediate(true);
		lblSalesInvoice.setWidth("-1px");
		lblSalesInvoice.setHeight("-1px");
		mainLayout.addComponent(lblSalesInvoice, "top:85.0px; left:30.0px;");

		cmbSalesInvoice = new ComboBox();
		cmbSalesInvoice.setImmediate(true);
		cmbSalesInvoice.setWidth("180px");
		mainLayout.addComponent(cmbSalesInvoice, "top:83.0px; left:120.0px;");

		chkAll = new CheckBox("All");
		chkAll.setImmediate(true);
		chkAll.setHeight("-1px");
		chkAll.setWidth("-1px");
		mainLayout.addComponent(chkAll, "top:85.0px; left:305.0px;");

		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px; left:140.0px;");

		mainLayout.addComponent(button,"top:145.opx; left:125.0px");

		return mainLayout;
	}
}