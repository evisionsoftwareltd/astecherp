package com.reportform.DoSalesModule;

import java.text.SimpleDateFormat;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptDateWiseSalesSummary extends Window
{
	private SessionBean sessionBean;

	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout verLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();

	private GridLayout grid = new GridLayout(2,1);

	private PopupDateField dFrom = new PopupDateField("From Date:");
	private PopupDateField dTo = new PopupDateField("To Date:");
	private VerticalLayout space = new VerticalLayout();

	private Label lblSpace = new Label("");

	private ComboBox cmbPartyName = new ComboBox("Party Name :");

	private SimpleDateFormat dfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});

	private OptionGroup RadioBtnType;
	private static final List<String> reportType = Arrays.asList(new String[]{"Summary","Details"});

	public RptDateWiseSalesSummary(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("DATE WISE SALES STATEMENT :: "+ this.sessionBean.getCompany());
		this.setWidth("440px");
		this.setHeight("300px");
		this.setResizable(false);

		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("250px");

		formLayout.addComponent(cmbPartyName);

		formLayout.addComponent(dFrom);
		formLayout.addComponent(dTo);

		RadioBtnType = new OptionGroup("",reportType);
		RadioBtnType.setImmediate(true);
		RadioBtnType.setStyleName("horizontal");
		RadioBtnType.setValue("Summary");
		formLayout.addComponent(RadioBtnType);

		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		formLayout.addComponent(RadioBtnGroup);

		dFrom.setValue(new java.util.Date());
		dFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dFrom.setImmediate(true);
		dFrom.setDateFormat("dd-MM-yyyy");
		dFrom.setWidth("110px");

		dTo.setValue(new java.util.Date());
		dTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dTo.setImmediate(true);
		dTo.setDateFormat("dd-MM-yyyy");
		dTo.setWidth("110px");

		lblSpace.setHeight("15px");
		formLayout.addComponent(lblSpace);

		btnLayout.addComponent(button);

		verLayout.addComponent(space);
		verLayout.setSpacing(true);
		space.setHeight("42px");
		space.setSpacing(true);

		grid.addComponent(formLayout,0,0);
		grid.addComponent(verLayout,1,0);
		mainLayout.addComponent(grid);
		mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);

		this.addComponent(mainLayout);		
		setButtonAction();

		addPartyName();
	}

	public void addPartyName()
	{
		cmbPartyName.removeAllItems();

		cmbPartyName.addItem("%");
		cmbPartyName.setItemCaption("%", "All");
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> lst = session.createSQLQuery(" select partyCode,partyName from tbPartyInfo order by partyName ").list();
			for (Iterator<?> iter = lst.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0]);
				cmbPartyName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
	}

	private void setButtonAction()
	{
		button.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbPartyName.getValue()!=null)
				{
					showReport();
				}
				else
				{
					showNotification("Warning!","Select Party Name",Notification.TYPE_WARNING_MESSAGE);
					cmbPartyName.focus();
				}
			}
		});

		button.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}

	private void showReport()
	{
		ReportOption RadioBtn = new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			ReportDate reportTime = new ReportDate();
			String query = "";
			String jasper = "";
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("company",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("fromDate", dFrom.getValue());
			hm.put("toDate", dTo.getValue());
			hm.put("partyName",cmbPartyName.getItemCaption(cmbPartyName.getValue().toString()));

			if(RadioBtnType.getValue().toString().equals("Summary"))
			{
				query = " select vPartyName,SUM(mPostTotal) Amount,SUM(mVatAmount) totalVat,SUM(mNetTotal) totalAmount" +
						" from tbSalesInvoiceInfo where CONVERT(DATE,dInvoiceDate) between" +
						" '"+dfYMD.format(dFrom.getValue())+"' and '"+dfYMD.format(dTo.getValue())+"'" +
						" and vPartyId like '"+cmbPartyName.getValue().toString()+"' group by vPartyName order by vPartyName ";

				System.out.println("Query" +query );

				jasper = "report/account/DoSales/rptDateWiseSales.jasper";
			}
			else
			{
				query = "select substring(vBillNo,12,Len(vBillNo))vBillNo,dInvoiceDate,vPartyName,mPostTotal,mVatAmount,mNetTotal from tbSalesInvoiceInfo" +
						" where dInvoiceDate between '"+dfYMD.format(dFrom.getValue())+"'and" +
						" '"+dfYMD.format(dTo.getValue())+"' and vPartyId like '"+cmbPartyName.getValue().toString()+"' order by vPartyName,dInvoiceDate";

				System.out.println("Query" +query );

				jasper = "report/account/DoSales/rptDateWiseSalesDetails.jasper";
			}

			hm.put("sql",query);
			Window win = new ReportViewer(hm,jasper,
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);
			win.setCaption("YEARLY SALES STATEMENT :: "+sessionBean.getCompany());

		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


}