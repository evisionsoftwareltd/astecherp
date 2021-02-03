package acc.reportmodule.daybook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.naming.java.javaURLContextFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportPdf;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class DayBook extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private FormLayout formButton = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();

	private HorizontalLayout h1Layout = new HorizontalLayout();
	private HorizontalLayout h2Layout = new HorizontalLayout();
	private HorizontalLayout hMainLayout = new HorizontalLayout();

	private DateField fromDate = new DateField("From Date:");
	private DateField toDate = new DateField("To Date:");
	private ComboBox bankList = new ComboBox("Bank List:");
	private String lcw = "130px";
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtfDMY = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	private String rpt = "";
	private ComboBox costCentre = new ComboBox("Cost Centre:");
	private CheckBox chkAll = new CheckBox("All");
	private Label lblHeight = new Label();
	ArrayList<Component> comp = new ArrayList<Component>();

	public DayBook(SessionBean sessionBean,final String r)
	{
		this.rpt = r;
		this.sessionBean = sessionBean;
		this.setWidth("450px");
		this.setResizable(false);

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);

		formLayout.addComponent(costCentre);
		costCentre.setWidth("235px");
		costCentre.setImmediate(true);

		//// System.out.println(r);

		lblHeight.setHeight("63px");
		formButton.addComponent(lblHeight);
		formButton.addComponent(chkAll);
		chkAll.setImmediate(true);
		//chkAll.setValue(true);
		costCentre.setEnabled(false);
		//chkAll.setEnabled(false);s

		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkAll.getValue().equals(true))
				{
					costCentre.setEnabled(false);
					costCentre.setValue(null);
				}
				else
				{
					costCentre.setEnabled(true);
				}
				chkAllEffect(r);
			}
		});

		h2Layout.addComponent(formButton);

		if(r.equalsIgnoreCase("c"))
		{
			this.setCaption("CASH BOOK :: "+this.sessionBean.getCompany());
			comp.add(fromDate);
			comp.add(toDate);
			comp.add(costCentre);

			formLayout.addComponent(bankList);
			bankList.setImmediate(true);
			bankList.setWidth("240px");
			bankList.setCaption("Cash A/C list :");
			comp.add(bankList);
			cashAcInitialise();

			comp.add(button.btnPreview);

		}
		else if(r.equalsIgnoreCase("dcr"))
		{
			this.setCaption("DAILY CHEQUE REGISTER :: "+this.sessionBean.getCompany());
			lblHeight.setHeight("42px");
			toDate.setCaption("Date: ");
			formLayout.addComponent(bankList);
			//formLayout.setSpacing(true);
			//formLayout.setMargin(false);
			bankList.setImmediate(true);
			bankList.setWidth("240px");
			//bankList.addItem("-1");
			//bankList.setItemCaption("-1", "All");
			
			//bankList.setValue("-1");
			costCentre.setVisible(false);
			fromDate.setVisible(false);
			comp.add(toDate);
			comp.add(bankList);
			comp.add(button.btnPreview);
			bankList.setCaption("Bank Account Name :");
			toDate.setValue(new java.util.Date());
			bankInitialise();
		}
		else if(r.equalsIgnoreCase("plc"))
		{
			this.setCaption("COST CENTER WISE PROFIT & LOSS :: "+this.sessionBean.getCompany());
			comp.add(fromDate);
			comp.add(toDate);
			comp.add(costCentre);
			comp.add(button.btnPreview);
		}
		else if(r.equalsIgnoreCase("plb"))
		{
			this.setCaption("BUNKER WISE PROFIT & LOSS :: "+this.sessionBean.getCompany());
			fromDate.setVisible(false);
			toDate.setCaption("As on date:");
			costCentre.setCaption("Bunker Name:");
		}
		else if(r.equalsIgnoreCase("b"))
		{
			this.setCaption("BANK BOOK :: "+this.sessionBean.getCompany());
			//this.setWidth("400px");
			formLayout.addComponent(bankList);
			bankList.setImmediate(true);
			bankList.setWidth("240px");
			bankInitialise();
			comp.add(fromDate);
			comp.add(toDate);
			comp.add(costCentre);
			comp.add(bankList);
			comp.add(button.btnPreview);
		}
		else if(r.equalsIgnoreCase("j"))
		{
			this.setCaption("JOURNAL BOOK :: "+this.sessionBean.getCompany());
			comp.add(fromDate);
			comp.add(toDate);
			comp.add(costCentre);
			comp.add(button.btnPreview);
		}
		else if(r.equalsIgnoreCase("s"))
		{
			this.setCaption("SALES BOOK :: "+this.sessionBean.getCompany());
			comp.add(fromDate);
			comp.add(toDate);
			comp.add(costCentre);
			comp.add(button.btnPreview);
		}
		else if(r.equalsIgnoreCase("cr"))
		{
			this.setCaption("OUTWARD CHEQUE REGISTER :: "+this.sessionBean.getCompany());
			formLayout.addComponent(bankList);
			bankList.setImmediate(true);
			bankList.setWidth("240px");
			bankInitialise();
			costCentre.setVisible(false);
			comp.add(fromDate);
			comp.add(toDate);
			comp.add(bankList);
			comp.add(button.btnPreview);
			chkAll.setVisible(false);
			bankList.setCaption("Bank Account Name :");
		}
		else if(r.equalsIgnoreCase("crIn"))
		{
			this.setCaption("INWARD CHEQUE REGISTER :: "+this.sessionBean.getCompany());
			formLayout.addComponent(bankList);
			bankList.setImmediate(true);
			bankList.setWidth("240px");
			bankInitialise();
			costCentre.setVisible(false);
			comp.add(fromDate);
			comp.add(toDate);
			comp.add(bankList);
			comp.add(button.btnPreview);
			chkAll.setVisible(false);
			bankList.setCaption("Bank Account Name :");
		}
		else if(r.equalsIgnoreCase("crp"))
		{
			this.setCaption("RECEIVED CHEQUE REGISTER :: "+this.sessionBean.getCompany());
			formLayout.addComponent(bankList);
			bankList.setImmediate(true);
			bankList.setWidth("240px");
			//		bankList.addItem("-1");
			//		bankList.setItemCaption("-1", "All");
			//		// System.out.println("A");
			//ledgerInitialise();
			bankInitialise();
			bankList.setCaption("Bank Account Name :");
			//		bankList.setValue("-1");
			costCentre.setVisible(false);
			//fromDate.setVisible(false);
			comp.add(fromDate);
			comp.add(toDate);
			comp.add(bankList);
			comp.add(button.btnPreview);
			chkAll.setVisible(false);
		}

		fromDate.setWidth(lcw);
		fromDate.setValue(sessionBean.getFiscalOpenDate());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);

		toDate.setWidth(lcw);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);

		btnL.setSpacing(true);
		btnL.addComponent(button);
		//Component comp[] = {fromDate, toDate, costCentre, button.btnPreview, button.btnExit};
		new FocusMoveByEnter(this,comp);
		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(btnL);

		h1Layout.addComponent(formLayout);
		hMainLayout.addComponent(h1Layout);
		hMainLayout.addComponent(h2Layout);
		mainLayout.addComponent(hMainLayout);
		//		mainLayout.addComponent(formLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		buttonActionAdd();
		if(!r.equalsIgnoreCase("cr"))
			costCenterInitialise();		
	}
	private void chkAllEffect(String r){
		//if(r.equalsIgnoreCase("dcr")){
		if(chkAll.booleanValue()){
			bankList.setValue(null);
			bankList.setEnabled(false);
		}
		else{
			bankList.setEnabled(true);
		}
	}
	private void bankInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			Iterator<?> iter = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A8') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list().iterator();
			for(;iter.hasNext();)
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
		
		/*if(this.rpt.equalsIgnoreCase("dcr")){
			bankList.removeAllItems();
			try{
				System.out.println("toDate: "+toDate.getValue());
				
				String sql=" select distinct Bank_Id,(select Ledger_Name from tbLedger where Ledger_Id=Bank_Id)BankName from ChequeDetails1 "+
						" where Cheque_Date='"+dtfYMD.format(toDate.getValue())+"' order by BankName";
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				session.beginTransaction();
				Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
				for(;iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					bankList.addItem(element[0]);
					bankList.setItemCaption(element[0], element[1].toString());
				}
			}
			catch(Exception exp){
				showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}*/
	}

	private void cashAcInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			//if (r.equalsIgnoreCase("crp"))
			Iterator<?> iter = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A7') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list().iterator();
			for(;iter.hasNext();)
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

	@SuppressWarnings("deprecation")
	private void preBtnAction()
	{
		//// System.out.println("G");
		//		if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
		//				&&
		//				(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
		//		{
		if(rpt.equalsIgnoreCase("dcr"))
		{
			//// System.out.println("G"+"1");
			//		if (chkDate())
			showReport();
		}
		else if(rpt.equalsIgnoreCase("crp"))
		{
			//// System.out.println("G"+"1");
			if (chkDate())
				showReport();
		}
		else if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())))
		{
			//// System.out.println("G"+"2");
			if (chkDate())
				showReport();
		}
		else
		{
			this.getParent().showNotification("","From date can not be greater than to date.",Notification.TYPE_WARNING_MESSAGE);
		}
		//		}
		//		else
		//		{
		//			this.getParent().showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
		//		}	
	}

	private void showReport()
	{
		try
		{
			//			// System.out.println("A");
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			tx.commit();
			//// System.out.println("A");
			HashMap<Object, Object> hm = new HashMap<Object, Object>();

			hm.put("fromTo", dtfDMY.format(fromDate.getValue())+" To "+dtfDMY.format(toDate.getValue()));
			hm.put("fromDate", dtfYMD.format(fromDate.getValue()));
			hm.put("toDate", dtfYMD.format(toDate.getValue()));			
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("logo",sessionBean.getCompanyLogo());

			sessionBean.setFromDate(fromDate.getValue());
			sessionBean.setAsOnDate(toDate.getValue());
			hm.put("url", this.getWindow().getApplication().getURL()+"");

			Object b=this.getWindow().getApplication().getContext().getBaseDirectory();

			sessionBean.setUrl(getWindow().getApplication().getURL());

			sessionBean.setP(b);


			if(costCentre.getValue()==null)
			{
				//hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costCentre","All");
				hm.put("costId","%");
			}
			else
			{
				hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costId",costCentre.getValue()+"");
			}

			/*			if(costCentre.isVisible())
			{
				if(costCentre.getValue().toString().equals("-1"))
				{
					hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
					hm.put("costId","%");
				}
				else
				{
					hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
					hm.put("costId",costCentre.getValue()+"");
				}
			}*/
			//BankBook.jasper
			if(rpt.equalsIgnoreCase("c"))
			{
				if(bankList.getValue() != null)
				{

					hm.put("ledgerName", bankList.getItemCaption(bankList.getValue()).toString());
					hm.put("ledgerId", bankList.getValue().toString());
					//				Window win = new ReportPdf(hm,"report/account/book/CashBook.jasper",
					//						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					//						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

					Window	win = new ReportViewer(hm,"report/account/book/CashBook.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

					this.getParent().getWindow().addWindow(win);
					win.setCaption("CASH BOOK :: "+sessionBean.getCompany());

				}
				else
					showNotification("Warning ","Please Select Cash A/C Name.", Notification.TYPE_WARNING_MESSAGE);
			}

			else if(rpt.equalsIgnoreCase("s"))
			{
				//fromDate.getValue()
				String sql = "";
				sql = "SELECT     lo.AutoId,  l.Ledger_Id, l.Ledger_Name, lo.Voucher_No, lo.Date, "+
						"lo.DrAmount, lo.CrAmount,lo.Narration FROM         dbo.vwVoucher AS lo INNER JOIN "+
						"dbo.tbLedger AS l ON lo.Ledger_Id = l.Ledger_Id and lo.companyId = l.companyId WHERE     (SUBSTRING(lo.Voucher_No, 1, 2) IN ('IV')) AND "+
						"(lo.vouchertype = 'ils') and (lo.Date between '"+dtfYMD.format(fromDate.getValue())+"' and '"+dtfYMD.format(toDate.getValue())+"') and lo.costId = '"+costCentre.getValue()+"' and lo.companyId = '"+sessionBean.getCompanyId()+"' "+
						"order by lo.date,Convert(Numeric,(SUBSTRING(lo.Voucher_No, 7, 12)))";

				hm.put("sql",sql);

				// // System.out.println(sql);
				Window win = new ReportPdf(hm,"report/account/book/SalesBook.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				this.getParent().getWindow().addWindow(win);
				win.setCaption("SALES BOOK :: "+sessionBean.getCompany());
			}
			else if(rpt.equalsIgnoreCase("plc"))
			{

				hm.put("fromDate", fromDate.getValue());
				hm.put("toDate", toDate.getValue());

				//				Window win = new ReportPdf(hm,"report/account/profitloss/profitLossCost.jasper",
				//						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
				//						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

				Window	win = new ReportViewer(hm,"report/account/profitloss/profitLossCost.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
				this.getParent().getWindow().addWindow(win);
				win.setCaption("COST CENTER WISE PROFIT & LOSS :: "+sessionBean.getCompany());

			}
			else if(rpt.equalsIgnoreCase("plb"))
			{
				hm.put("fromDate", sessionBean.getFiscalOpenDate());
				hm.put("toDate", toDate.getValue());
				Window win = new ReportPdf(hm,"report/account/profitloss/profitLossBunker.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				this.getParent().getWindow().addWindow(win);
				win.setCaption("COST CENTER BUNKER WISE PROFIT & LOSS :: "+sessionBean.getCompany());
			}
			else if(rpt.equalsIgnoreCase("b"))
			{
				if(bankList.getValue() != null)
				{
					hm.put("bankName", bankList.getItemCaption(bankList.getValue()).toString());
					hm.put("bankId", bankList.getValue().toString());


					Window	win = new ReportViewer(hm,"report/account/book/BankBook.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

					//					Window win = new ReportPdf(hm,"report/account/book/BankBook.jasper",
					//							getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					//							getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
					this.getParent().getWindow().addWindow(win);
					win.setCaption("BANK BOOK :: "+sessionBean.getCompany());
				}
				else
					showNotification("Warning ","Please Select Bank Name.", Notification.TYPE_WARNING_MESSAGE);
			}
			else if(rpt.equalsIgnoreCase("j"))
			{
				//				Window win = new ReportPdf(hm,"report/account/book/JournalBook.jasper",
				//						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
				//						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

				Window	win = new ReportViewer(hm,"report/account/book/JournalBook.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				this.getParent().getWindow().addWindow(win);
				win.setCaption("JOURNAL BOOK :: "+sessionBean.getCompany());
			}
			else if(rpt.equalsIgnoreCase("cr"))
			{
				if(bankList.getValue() != null||chkAll.booleanValue())
				{
					hm.put("fromDate", dtfYMD.format(fromDate.getValue()));
					hm.put("toDate", dtfYMD.format(toDate.getValue()));
					hm.put("bankName",bankList.getValue()!=null?bankList.getItemCaption(bankList.getValue()).toString():"All");
					hm.put("bankId",bankList.getValue()!=null?bankList.getValue().toString():"-1");
					hm.put("logo", sessionBean.getCompanyLogo());

					// System.out.println("From Date : "+fromDate.getValue());
					// System.out.println("To Date : "+toDate.getValue());
					// System.out.println("Bank Name : "+bankList.getItemCaption(bankList.getValue()).toString());
					// System.out.println("Bank Id : "+bankList.getValue().toString());

					/*					Window win = new ReportPdf(hm,"report/account/mis/chequeRegisterDateRange.jasper",
							getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);*/
					Window win = new ReportViewer(hm,"report/account/mis/chequeRegisterDateRangeBankPayment.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
					this.getParent().getWindow().addWindow(win);
					win.setCaption("OUTWARD CHEQUE REGISTER :: "+sessionBean.getCompany());
				}
				else
				{
					showNotification("Warning ","Please Select Bank Name.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else if(rpt.equalsIgnoreCase("crIn"))
			{
				if(bankList.getValue() != null)
				{
					hm.put("fromDate", fromDate.getValue());
					hm.put("toDate", toDate.getValue());
					hm.put("bankName", bankList.getItemCaption(bankList.getValue()).toString());
					hm.put("bankId", bankList.getValue().toString());
					hm.put("logo", sessionBean.getCompanyLogo());

					// System.out.println("From Date : "+fromDate.getValue());
					// System.out.println("To Date : "+toDate.getValue());
					// System.out.println("Bank Name : "+bankList.getItemCaption(bankList.getValue()).toString());
					// System.out.println("Bank Id : "+bankList.getValue().toString());

					/*					Window win = new ReportPdf(hm,"report/account/mis/chequeRegisterDateRange.jasper",
							getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);*/

					Window win = new ReportViewer(hm,"report/account/mis/chequeRegisterDateRangeBankReceived.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
					this.getParent().getWindow().addWindow(win);
					win.setCaption("INWARD CHEQUE REGISTER :: "+sessionBean.getCompany());
				}
				else
				{
					showNotification("Warning ","Please Select Bank Name.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else if(rpt.equalsIgnoreCase("crp"))
			{
				if(bankList.getValue() != null)
				{
					// System.out.println(fromDate.getValue());
					hm.put("fromdate", fromDate.getValue());
					hm.put("todate", toDate.getValue());
					hm.put("partyName", bankList.getItemCaption(bankList.getValue()).toString());
					hm.put("ledgerid", bankList.getValue().toString());
					hm.put("companyID", sessionBean.getCompanyId());

					Window win = new ReportPdf(hm,"report/account/mis/ReceivableChequeRegister.jasper",
							getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

					this.getParent().getWindow().addWindow(win);
					win.setCaption("RECEIVED CHEQUE REGISTER :: "+sessionBean.getCompany());
				}
				else
				{
					showNotification("Warning ","Please Select Party Name.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else if(rpt.equalsIgnoreCase("dcr"))
			{
				if(bankList.getValue() != null||chkAll.booleanValue())
				{
					hm.put("ddate",  dtfYMD.format(toDate.getValue()));
					System.out.println("Date: "+ dtfYMD.format(toDate.getValue()));
					hm.put("bankName",bankList.getValue()!=null? bankList.getItemCaption(bankList.getValue()).toString():"All");
					hm.put("bankId", bankList.getValue()!=null?bankList.getValue().toString():"-1");

					if(chkAll.booleanValue())
					{
						Window win = new ReportPdf(hm,"report/account/mis/chequeRegisterDailyAll.jasper",
								getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
								getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

						this.getParent().getWindow().addWindow(win);
						win.setCaption("DAILY CHEQUE REGISTER :: "+sessionBean.getCompany());
					}
					else
					{
						Window win = new ReportPdf(hm,"report/account/mis/dailyChequeRegister.jasper",
								getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
								getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

						this.getParent().getWindow().addWindow(win);
						win.setCaption("DAILY CHEQUE REGISTER :: "+sessionBean.getCompany());
					}
				}
				else
				{
					showNotification("Warning ","Please Select Bank Name.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				this.getParent().showNotification("Error :","Internal error. Please contact your software vendor.",Notification.TYPE_ERROR_MESSAGE);
			}
		}		
		catch(Exception exp)
		{			
			this.getParent().showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void costCenterInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			Iterator<?> iter = session.createSQLQuery("SELECT id,costCentreName FROM tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' ORDER BY costCentreName").list().iterator();
			for(;iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				costCentre.addItem(element[0]);
				costCentre.setItemCaption(element[0], element[1].toString());
			}
			costCentre.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	@SuppressWarnings("deprecation")
	private boolean chkDate()
	{
		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			if (f.equals("1"))
			{
				return true;
			}
			else
			{
				this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else
		{
			this.getParent().showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}
}
