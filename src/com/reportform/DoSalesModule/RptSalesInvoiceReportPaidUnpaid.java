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
public class RptSalesInvoiceReportPaidUnpaid extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblInvoiceDate;
	private PopupDateField dFromInvoiceDate;
	private PopupDateField dToInvoiceDate;

	private Label lblPartyName;
	private ComboBox cmbPartyName;
	private CheckBox chkPartyAll;

	private Label lblSalesInvoice;
	private ComboBox cmbSalesInvoice;
	private CheckBox chkInvoiceAll;

	private ComboBox cmbBillStatus;

	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});

	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	public RptSalesInvoiceReportPaidUnpaid(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("SALES INVOICE REPORT :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		cmbBillStatus.addItem("%");
		cmbBillStatus.setItemCaption("%", "All");
		cmbBillStatus.addItem("Unpaid");
		cmbBillStatus.setItemCaption("Unpaid", "Unpaid");
		cmbBillStatus.addItem("Partial");
		cmbBillStatus.setItemCaption("Partial", "Partial");
		cmbBillStatus.addItem("Paid");
		cmbBillStatus.setItemCaption("Paid", "Paid");

		actionEvent();
		addPartyName();
	}

	public void actionEvent()
	{
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

		dFromInvoiceDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addInvoiceNo();
			}
		});

		dToInvoiceDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addInvoiceNo();
			}
		});

		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addInvoiceNo();
			}
		});

		cmbBillStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addInvoiceNo();
			}
		});

		chkInvoiceAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkInvoiceAll.booleanValue())
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

		chkPartyAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				addInvoiceNo();
				if(chkPartyAll.booleanValue())
				{
					cmbPartyName.setEnabled(false);
					cmbPartyName.setValue(null);
				}
				else
				{
					cmbPartyName.setEnabled(true);
				}
			}
		});
	}

	private void checkParameter()
	{
		if(cmbPartyName.getValue()!=null || chkPartyAll.booleanValue())
		{
			if(cmbBillStatus.getValue()!=null)
			{
				if(cmbSalesInvoice.getValue()!=null || chkInvoiceAll.booleanValue())
				{
					reportPreview();
				}
				else
				{
					showNotification("Warning!","Select bill no.",Notification.TYPE_WARNING_MESSAGE);
					cmbSalesInvoice.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select bill status.",Notification.TYPE_WARNING_MESSAGE);
				cmbBillStatus.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select bill status.",Notification.TYPE_WARNING_MESSAGE);
			cmbPartyName.focus();
		}
	}

	private void addPartyName()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = "Select partyCode,partyName from tbPartyInfo ORDER by partyName ";
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

	private void addInvoiceNo()
	{
		String Party = (chkPartyAll.booleanValue()?"%":(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():""));
		String BillType =(cmbBillStatus.getValue()!=null?cmbBillStatus.getValue().toString():"");
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = "select vBillNo, 0 Zero from [dbo].[funPartyWiseInvoiceStatus]('"+Party+"'," +
					" '"+df.format(dFromInvoiceDate.getValue())+"', '"+df.format(dToInvoiceDate.getValue())+"'," +
					" '%', '"+BillType+"')";

			cmbSalesInvoice.removeAllItems();
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSalesInvoice.addItem(element[0]);
				cmbSalesInvoice.setItemCaption(element[0], element[0].toString());
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
		String BillType =(cmbBillStatus.getValue()!=null?cmbBillStatus.getValue().toString():"");
		String Invoice = (chkInvoiceAll.booleanValue()?"%":(cmbSalesInvoice.getValue()!=null?cmbSalesInvoice.getValue().toString():""));

		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();

			String query = " select vPartyId,vPartyName,vPartyAddress,vBillNo,vBillDate,mNetAmount,mVatAmount,mInvoiceAmount,mReceivedAmount," +
					"mRemainAmount,vStatus from [dbo].[funPartyWiseInvoiceStatus]('"+Party+"', '"+df.format(dFromInvoiceDate.getValue())+"'," +
					" '"+df.format(dToInvoiceDate.getValue())+"', '"+Invoice+"', '"+BillType+"')";
			/*String query = "select substring(vBillNo,12,Len(vBillNo))vBillNo,dInvoiceDate,vPartyName,mPostTotal,mVatAmount," +
					"mNetTotal from tbSalesInvoiceInfo where dInvoiceDate between '"+df.format(dFromInvoiceDate.getValue())+"' " +
					"and '"+df.format(dToInvoiceDate.getValue())+"' and vPartyId like '"+Party+"' " +
					"and vBillNo like '"+cmbSalesInvoice.getValue().toString()+"' order by vPartyName,dInvoiceDate";*/

			hm.put("path", "./report/account/DoSales/");
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("fromDate",dFromInvoiceDate.getValue());
			hm.put("toDate",dToInvoiceDate.getValue());

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/DoSales/rptSalesInvoiceReport.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);
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
		mainLayout.setHeight("280px");
		mainLayout.setMargin(false);

		lblPartyName = new Label("Party Name : ");
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		mainLayout.addComponent(lblPartyName, "top:20.0px; left:30.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("300px");
		cmbPartyName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbPartyName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbPartyName, "top:18.0px; left:120.0px;");

		chkPartyAll = new CheckBox("All");
		chkPartyAll.setImmediate(true);
		chkPartyAll.setHeight("-1px");
		chkPartyAll.setWidth("-1px");
		mainLayout.addComponent(chkPartyAll, "top:20.0px; left:425.0px;");

		lblInvoiceDate = new Label("From : ");
		lblInvoiceDate.setImmediate(true);
		lblInvoiceDate.setWidth("-1px");
		lblInvoiceDate.setHeight("-1px");
		mainLayout.addComponent(lblInvoiceDate, "top:50.0px; left:30.0px;");

		dFromInvoiceDate = new PopupDateField();
		dFromInvoiceDate.setImmediate(true);
		dFromInvoiceDate.setWidth("110px");
		dFromInvoiceDate.setDateFormat("dd-MM-yyyy");
		dFromInvoiceDate.setValue(new Date());
		dFromInvoiceDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromInvoiceDate, "top:48.0px; left:120.0px;");

		mainLayout.addComponent(new Label(" To : "), "top:80.0px; left:30.0px;");

		dToInvoiceDate = new PopupDateField();
		dToInvoiceDate.setImmediate(true);
		dToInvoiceDate.setWidth("110px");
		dToInvoiceDate.setDateFormat("dd-MM-yyyy");
		dToInvoiceDate.setValue(new Date());
		dToInvoiceDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToInvoiceDate, "top:78.0px; left:120.0px;");

		cmbBillStatus = new ComboBox();
		cmbBillStatus.setImmediate(true);
		cmbBillStatus.setWidth("130px");
		cmbBillStatus.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbBillStatus.setValue("All Invoice");
		mainLayout.addComponent(new Label("Bill Status :"), "top:110.0px; left:30.0px");
		mainLayout.addComponent(cmbBillStatus, "top:108.0px; left:120.0px;");

		lblSalesInvoice = new Label("Invoice No : ");
		lblSalesInvoice.setImmediate(true);
		lblSalesInvoice.setWidth("-1px");
		lblSalesInvoice.setHeight("-1px");
		mainLayout.addComponent(lblSalesInvoice, "top:140.0px; left:30.0px;");

		cmbSalesInvoice = new ComboBox();
		cmbSalesInvoice.setImmediate(true);
		cmbSalesInvoice.setWidth("220px");
		cmbSalesInvoice.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSalesInvoice.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSalesInvoice, "top:138.0px; left:120.0px;");

		chkInvoiceAll = new CheckBox("All");
		chkInvoiceAll.setImmediate(true);
		chkInvoiceAll.setHeight("-1px");
		chkInvoiceAll.setWidth("-1px");
		mainLayout.addComponent(chkInvoiceAll, "top:140.0px; left:345.0px;");

		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:170.0px; left:125.0px;");

		mainLayout.addComponent(button,"top:200.opx; left:150.0px");

		return mainLayout;
	}
}