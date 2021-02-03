package acc.reportmodule.mis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportPdf;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;

public class InterestCalculation extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	
	private HorizontalLayout btnL = new HorizontalLayout();
	private PopupDateField fromDate = new PopupDateField("From Date:");

	private PopupDateField toDate = new PopupDateField("To Date :");
	private ComboBox bankList = new ComboBox("Bank List :");
	private ComboBox accountNumber = new ComboBox("Account Number :");
	private AmountCommaSeperator intRate = new AmountCommaSeperator("Rate of Interest (%) :");
	private AmountCommaSeperator creditLimit = new AmountCommaSeperator("Credit Limit Amt. :");
	private AmountCommaSeperator overLimit = new AmountCommaSeperator("Int. for over limit (%) :");

	
	private String lcw = "100px";
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtfDMY = new SimpleDateFormat("dd-MM-yyyy");
	private String rpt = "";
	
	private ComboBox costCentre = new ComboBox("Cost Centre:");
	ArrayList<Component> comp = new ArrayList<Component>();

	public InterestCalculation(SessionBean sessionBean)
	{
		String r = "";
		rpt = r;
		this.sessionBean = sessionBean;
		this.setWidth("480px");
		this.setResizable(false);


		
		fromDate.setWidth(lcw);
		toDate.setWidth(lcw);
		accountNumber.setWidth("250px");
		intRate.setWidth("140");
		creditLimit.setWidth("140");
		overLimit.setWidth("140");
		
		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yy");
		
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yy");
		
		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);
		formLayout.addComponent(accountNumber);
		formLayout.addComponent(intRate);
		
		formLayout.addComponent(creditLimit);
		formLayout.addComponent(overLimit);

		
		btnL.addComponent(button);
		accountInitialise();
		Component ob[] = {fromDate,toDate,accountNumber,intRate,creditLimit,overLimit,button.btnPreview,button.btnExit};
		new FocusMoveByEnter(this,ob);  
		
		mainLayout.addComponent(formLayout);

		mainLayout.addComponent(btnL);
		mainLayout.setComponentAlignment(btnL, Alignment.BOTTOM_CENTER);
		
		addComponent(mainLayout);

		mainLayout.setMargin(true);
		buttonActionAdd();
		
		
	}

	private void bankInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A7','L7') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list().iterator();

			for(int i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				bankList.addItem(element[0]);
				bankList.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				preBtnAction();
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void preBtnAction()
	{
		if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
				&&
				(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
		{
			if(rpt.equalsIgnoreCase("dcr"))
			{
				showReport();
			}
			else if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())))
			{
				showReport();
			}
			else
			{
				this.getParent().showNotification("","From date can not be greater than to date.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
		}	
	}

	private void showReport()
	{
		try
		{
			HashMap hm = new HashMap();

			hm.put("fromTo", dtfDMY.format(fromDate.getValue())+" To "+dtfDMY.format(toDate.getValue()));
			hm.put("fromDate", dtfYMD.format(fromDate.getValue()));
			hm.put("toDate", dtfYMD.format(toDate.getValue()));
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("ledgerId", accountNumber.getValue());
			hm.put("accountName",accountNumber.getItemCaption(accountNumber.getValue()));
			hm.put("rateOfInt",Double.parseDouble(intRate.getValue().toString().trim().replaceAll(",", "")));
			hm.put("rateofIntOL",Double.parseDouble(overLimit.getValue().toString().trim().replaceAll(",", "")));
			hm.put("creditLimit",Double.parseDouble(creditLimit.getValue().toString().trim().replaceAll(",", "")));
			
			//sessionBean Session;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

		session.createSQLQuery("exec prcLedger '"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"','"+accountNumber.getValue()+"','"+sessionBean.getCompanyId()+"'").executeUpdate();

		
			
			
			
			System.out.println("");
				Window win = new ReportPdf(hm,"report/account/mis/InterestCalculation.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				this.getParent().getWindow().addWindow(win);
				win.setCaption("Interest Calculation :: "+sessionBean.getCompany());
		}
		catch(Exception exp)
		{			
			this.getParent().showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void accountInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery("SELECT     TOP (100) PERCENT Ledger_Id, Ledger_Name FROM         dbo.tbLedger WHERE     (REPLACE(SUBSTRING(Create_From, 1, 3), '-', '') IN ('L5', 'L7', 'L8', 'L9', 'L10', 'L11', 'A7')) AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')ORDER BY Ledger_Name").list().iterator();
			
			for(int i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				accountNumber.addItem(element[0]);
				accountNumber.setItemCaption(element[0], element[1].toString());
			}
			accountNumber.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
