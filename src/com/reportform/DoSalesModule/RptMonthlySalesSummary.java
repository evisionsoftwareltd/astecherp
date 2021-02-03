package com.reportform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.vaadin.ui.Alignment;
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

public class RptMonthlySalesSummary extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private SessionBean sessionBean;

	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout verLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private GridLayout grid = new GridLayout(2,1);

	private InlineDateField Month = new InlineDateField("Month :");

	private Label lbl = new Label();
	private SimpleDateFormat chkDf = new SimpleDateFormat("MM");
	private SimpleDateFormat chkDfy = new SimpleDateFormat("YYYY");

	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});

	public RptMonthlySalesSummary(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("MONTHLY SALES STATEMENT :: "+ this.sessionBean.getCompany());
		this.setWidth("400px");
		this.setHeight("220px");
		this.setResizable(false);

		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");

		formLayout.addComponent(Month);
		formLayout.addComponent(RadioBtnGroup);
		btnLayout.addComponent(button);

		lbl.setHeight("30px");
		formLayout.addComponent(lbl);
		formLayout.setSpacing(true);

		Month.setValue(new java.util.Date());
		Month.setDateFormat("dd-MM-yy");
		Month.setResolution(InlineDateField.RESOLUTION_MONTH);
		Month.setImmediate(true);

		grid.addComponent(formLayout,0,0);
		grid.addComponent(verLayout,1,0);
		mainLayout.addComponent(grid);
		mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);

		this.addComponent(mainLayout);		
		setButtonAction();		
	}

	private void setButtonAction()
	{
		button.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				showReport();
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

			String sql = " select vPartyName,CONVERT(date,CONVERT(varchar(10),YEAR(dInvoiceDate))+'-'+CONVERT(varchar(10),"+
					" MONTH(dInvoiceDate))+'-01') salesMonth,SUM(mPostTotal) Amount,SUM(mVatAmount) totalVat,SUM(mNetTotal) totalAmount from tbSalesInvoiceInfo"+
					" where MONTH(dInvoiceDate)='"+chkDf.format(Month.getValue())+"' and YEAR(dInvoiceDate)='"+chkDfy.format(Month.getValue())+"' " +
					" group by vPartyName,CONVERT(date,CONVERT(varchar(10),YEAR(dInvoiceDate))+'-'+CONVERT(varchar(10),"+
					" MONTH(dInvoiceDate))+'-01') order by vPartyName";
			System.out.println(chkDf.format(Month.getValue()));
			if(queryValueCheck(sql))
			{
				hm.put("sql",sql);

				Window win = new ReportViewer(hm,"report/account/DoSales/rptMonthlySales.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				win.setCaption("MONTHLY SALES STATEMENT :: "+sessionBean.getCompany());
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
