package com.reportform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RptYearlySalesSummary extends Window
{
	private SessionBean sessionBean;

	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout verLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();

	private GridLayout grid = new GridLayout(2,1);

	private InlineDateField StartMonth = new InlineDateField("Starting Month :");
	private InlineDateField EndMonth = new InlineDateField("Ending Month :");
	private VerticalLayout space = new VerticalLayout();

	private Label lblSpace = new Label("");

	private ComboBox cmbPartyName = new ComboBox("Party Name :");

	private SimpleDateFormat chkDfy = new SimpleDateFormat("yyyy");
	private SimpleDateFormat chkDfm = new SimpleDateFormat("MM");

	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});

	public RptYearlySalesSummary(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;

		this.setCaption("YEARLY SALES STATEMENT :: "+ this.sessionBean.getCompany());
		this.setWidth("440px");
		this.setHeight("280px");
		this.setResizable(false);

		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("250px");

		formLayout.addComponent(cmbPartyName);

		formLayout.addComponent(StartMonth);
		formLayout.addComponent(EndMonth);

		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		formLayout.addComponent(RadioBtnGroup);

		StartMonth.setValue(new java.util.Date());
		StartMonth.setResolution(InlineDateField.RESOLUTION_MONTH);
		StartMonth.setImmediate(true);

		EndMonth.setValue(new java.util.Date());
		EndMonth.setResolution(InlineDateField.RESOLUTION_MONTH);
		EndMonth.setImmediate(true);

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

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List lst = session.createSQLQuery("SElect distinct vPartyId,vPartyName from tbSalesInvoiceInfo order by vPartyName").list();
			for (Iterator iter = lst.iterator();iter.hasNext();) 
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
					if(Double.parseDouble(chkDfm.format(StartMonth.getValue()).toString()) < 
							Double.parseDouble(chkDfm.format(EndMonth.getValue()).toString()))
					{
						showReport();
					}
					else
					{
						showNotification("Warning!","Ending month should less than starting month",Notification.TYPE_WARNING_MESSAGE);
					}
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
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			HashMap hm = new HashMap();
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			hm.put("company",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());

			hm.put("partyName",cmbPartyName.getItemCaption(cmbPartyName.getValue()));

			String sql = " select CONVERT(date,CONVERT(varchar(10),YEAR(dInvoiceDate))+'-'+CONVERT(varchar(10), MONTH(dInvoiceDate))+'-01')"+
					" salesMonth,SUM(mPostTotal) Amount,SUM(mVatAmount) totalVat,SUM(mNetTotal) totalAmount from tbSalesInvoiceInfo where" +
					" MONTH(dInvoiceDate) between '"+chkDfm.format(StartMonth.getValue())+"' and '"+chkDfm.format(EndMonth.getValue())+"' " +
					" and YEAR(dInvoiceDate) between '"+chkDfy.format(StartMonth.getValue())+"' and '"+chkDfy.format(StartMonth.getValue())+"'" +
					" and vPartyId like '"+cmbPartyName.getValue().toString()+"' "+
					" group by CONVERT(date,CONVERT(varchar(10),YEAR(dInvoiceDate))+'-'+CONVERT(varchar(10), MONTH(dInvoiceDate))+'-01') " +
					" order by CONVERT(date,CONVERT(varchar(10),YEAR(dInvoiceDate))+'-'+CONVERT(varchar(10), MONTH(dInvoiceDate))+'-01') ";

			if(queryValueCheck(sql))
			{
				hm.put("sql",sql);

				Window win = new ReportViewer(hm,"report/account/DoSales/rptYearlySales.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				win.setCaption("YEARLY SALES STATEMENT :: "+sessionBean.getCompany());
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
}
