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
public class RptDestinationWiseDeliveryChallan extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblFromDate;
	private PopupDateField dFromDate;
	private PopupDateField dToDate;

	private Label lblPartyName;
	private ComboBox cmbPartyName;

	private Label lblDestination;
	private ComboBox cmbDestination;
	private CheckBox chkAll = new CheckBox();

	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});

	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public RptDestinationWiseDeliveryChallan(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("	DESTINATION WISE DELIVERY CHALLAN :: "+sessionBean.getCompany());
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
				if(cmbDestination.getValue()!=null)
				{
					reportPreview();
				}
				else
				{
					showNotification("Warning!","Select Destination Name.",Notification.TYPE_WARNING_MESSAGE);
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

		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbPartyName.removeAllItems();
				if(dFromDate.getValue()!=null)
					addPartyName();
			}
		});

		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbPartyName.removeAllItems();
				if(dToDate.getValue()!=null)
					addPartyName();
			}
		});

		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDestination.removeAllItems();
				if(cmbPartyName.getValue()!=null)
				{
					addDestination();
				}
			}
		});

		chkAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkAll.booleanValue()==true)
				{
					cmbDestination.setEnabled(false);
					cmbDestination.setValue(null);
				}
				else
				{
					cmbDestination.setEnabled(true);
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
			String sql = " select vPartyId,vPartyName  from tbDeliveryChallanInfo  "
					+ "where dChallanDate  between CONVERT(datetime,'"+new SimpleDateFormat("yyyy-MM-dd").format(dFromDate.getValue())+"'+ ' 00:00:00',121) and" +
					" CONVERT(datetime, '"+new SimpleDateFormat("yyyy-MM-dd").format(dToDate.getValue())+"'+ ' 23:59:59',121)  " +
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

	private void addDestination()
	{
		cmbDestination.removeAllItems();
		cmbDestination.setValue(null);
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String sql = " select 0,vDestination from tbDeliveryChallanInfo  where " +
					" dChallanDate between " +
					" CONVERT(datetime,'"+new SimpleDateFormat("yyyy-MM-dd").format(dFromDate.getValue())+"'+ ' 00:00:00',121) and" +
					" CONVERT(datetime, '"+new SimpleDateFormat("yyyy-MM-dd").format(dToDate.getValue())+"'+ ' 23:59:59',121) " +
					" and vPartyID = '"+cmbPartyName.getValue()+"' order by dChallanDate desc ";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDestination.addItem(element[1]);
				cmbDestination.setItemCaption(element[1], element[1].toString());
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
			
			
			String queryBill = "SElect di.vChallanNo, di.dChallanDate , di.vPartyName,di.vDestination,dd.vProductId,dd.vProductName,dd.mChallanQty,"
					+ "di.vDriverName,di.vDriverMobile,di.vTruckNo from tbDeliveryChallanInfo di inner join tbDeliveryChallanDetails dd  "
					+ "on di.vChallanNo=dd.vChallanNo where di.vPartyId='"+cmbPartyName.getValue()+"' and " 
					+"di.vDestination like  '"+cmbDestination.getValue().toString()+"' and " 
					+"Convert(date,dChallanDate,105) between  '"+dateFormat.format(dFromDate.getValue())+"' and '"+dateFormat.format(dToDate.getValue())+"' " 
					+"order by di.dChallanDate,SUBSTRING(di.vChallanNo,9,CHARINDEX('-', di.vChallanNo))";
			

			/*queryBill = "SELECT funSIR.vPartyName,funSIR.vPartyAddress,funSIR.vPartyMobile,funSIR.vBillNo," +
					" funSIR.dBillDate,funSIR.vProductId,funSIR.vProductName,funSIR.mChallanQty," +
					" funSIR.vProductUnit,funSIR.mProductRate,funSIR.mAmount,funSIR.vInWrod," +
					" funSIR.mCommissionAmnt,funSIR.mVatAmount,funSIR.mBillAmount,funSIR.mTruckFare," +
					" funSIR.mNetAmount,funSIR.dDatetime,funSIR.vRemarks,funSIR.vChallaNo,funSIR.vUserName," +
					" dci.vDelChallanNo,dci.vVatChallanNo,dci.dChallanDate,dci.vTruckNo,dci.vDriverName,dci.vDestination," +
					" dcd.vProductUnit,dcd.mChallanQty,dcd.vDoNo,Convert (date,dcd.dDoDate) dDoDate," +
					" dci.vDepoName from funSalesInvoiceReport('"+cmbPartyName.getValue()+"'," +
					"'"+(cmbDestination.getValue()!=null?cmbDestination.getValue().toString():"%")+"'," +
					" '"+dateFormat.format(dFromDate.getValue())+"','"+dateFormat.format(dToDate.getValue())+"') funSIR " +
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
			hm.put("toDate", new SimpleDateFormat("dd-MM-yy").format(dToDate.getValue()));
			hm.put("fromDate", new SimpleDateFormat("dd-MM-yy").format(dFromDate.getValue()));
			if(queryValueCheck(queryBill))
			{
				hm.put("sql", queryBill);

				Window win = new ReportViewer(hm,"report/account/DoSales/RptDestinationWiseDeliveryChallan.jasper",
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
		mainLayout.setWidth("410px");
		mainLayout.setHeight("180px");
		mainLayout.setMargin(false);

		setWidth("470px");
		setHeight("230px");

		lblFromDate = new Label("From Date : ");
		lblFromDate.setImmediate(true);
		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate, "top:35.0px; left:30.0px;");

		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:35.0px; left:120.0px;");

		mainLayout.addComponent(new Label(" To "), "top:35.0px; left:235.0px;");

		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:33.0px; left:260.0px;");

		lblPartyName = new Label("Party Name : ");
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		mainLayout.addComponent(lblPartyName, "top:70.0px; left:30.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("280px");
		mainLayout.addComponent(cmbPartyName, "top:68.0px; left:120.0px;");

		lblDestination = new Label("Destination : ");
		lblDestination.setImmediate(true);
		lblDestination.setWidth("-1px");
		lblDestination.setHeight("-1px");
		mainLayout.addComponent(lblDestination, "top:95.0px; left:30.0px;");

		cmbDestination = new ComboBox();
		cmbDestination.setImmediate(true);
		cmbDestination.setWidth("280px");
		mainLayout.addComponent(cmbDestination, "top:93.0px; left:120.0px;");

		chkAll = new CheckBox("All");
		chkAll.setImmediate(true);
		chkAll.setHeight("-1px");
		chkAll.setWidth("-1px");
		//mainLayout.addComponent(chkAll, "top:85.0px; left:305.0px;");

		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		//mainLayout.addComponent(RadioBtnGroup, "top:110.0px; left:140.0px;");

		mainLayout.addComponent(button,"top:145.opx; left:125.0px");

		return mainLayout;
	}
}