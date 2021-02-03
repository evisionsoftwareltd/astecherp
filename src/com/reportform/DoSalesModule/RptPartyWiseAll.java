package com.reportform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;


import com.common.share.PreviewOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class RptPartyWiseAll extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	// Left Panel
	private Panel leftPanel;
	private OptionGroup RadioButtonGroup1;
	private static final List<String> groupButton1=Arrays.asList(new String[]{"DATE BETWEEN","MONTHLY","YEARLY",});

	// previewPanel
	private Panel previewPanel;

	private Label lblPeriod;
	private Label lblPreviewOption;

	// Date Between
	private Panel datePanel;

	private FormLayout fLayoutDate = new FormLayout();

	private PopupDateField fromDate;
	private PopupDateField toDate;
	

	// Monthly
	private Panel monthPanel;

	private FormLayout fLayoutMonth = new FormLayout();

	private OptionGroup RadioButtonGroup2;
	private static final List<String> groupButton2=Arrays.asList(new String[]{"Single","Comparative"});

	private InlineDateField dSelectedMonth;
	private InlineDateField dComparedMonth;

	private Label monthSeperator;

	// Yearly
	private Panel yearPanel;

	private FormLayout fLayoutYear = new FormLayout();

	private OptionGroup RadioButtonGroup3;
	private static final List<String> groupButton3=Arrays.asList(new String[]{"Single","Comparative"});

	private InlineDateField dSelectedYear;
	private InlineDateField dComparedYear;

	private Label yearSeperator;

	// Ledger Panel
	private Panel ledgerPanel;
	private Label lblLedger;
	private OptionGroup RadioButtonGroup4;
	private static final List<String> groupButton4=Arrays.asList(new String[]{"Ledger With Value","Ledger Without Value"});

	// Fund Panel
	private Panel fundPanel;
	private Label lblFund;
	private OptionGroup RadioButtonGroup5;
	private static final List<String> groupButton5=Arrays.asList(new String[]{"Without Fund","With Fund"});

	// date Formats
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dformat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat dFFiscal = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM-01");
	private SimpleDateFormat y = new SimpleDateFormat("yyyy-01-01");

	private PreviewOption po = new PreviewOption();

	private HorizontalLayout hButtonLayout = new HorizontalLayout();
	private NativeButton btnPreview = new NativeButton("Preview");
	private NativeButton btnPrint = new NativeButton("Report");
	private NativeButton btnClose = new NativeButton("Close");
	
	String prevGtotal;
	String prevItotal;
	String prevDtotal;
	String prevTotal;
	String prsntGtotal;
	String prsntItotal;
	String prsntDtotal;
	String prsntTotal;

	public RptPartyWiseAll(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		//this.setCaption("RECEIPT/PAYMENT REPORT ::"+sessionBean.CompanyName());
		this.setWidth("850px");
		this.setHeight("330px");
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);

		setEventAction();

		datePanel.setVisible(true);
		monthPanel.setVisible(false);
		yearPanel.setVisible(false);
	}

	public void setEventAction()
	{
		RadioButtonGroup1.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				clearData();

				RadioButtonGroup2.setValue("Single");
				RadioButtonGroup3.setValue("Single");
				RadioButtonGroup4.setValue("Ledger With Value");
				RadioButtonGroup5.setValue("Without Fund");
				po.txtType.setValue("PDF");

				if(event.getProperty().toString()=="DATE BETWEEN")
				{
					datePanel.setVisible(true);
					monthPanel.setVisible(false);
					yearPanel.setVisible(false);
				}
				else if(event.getProperty().toString()=="MONTHLY")
				{
					datePanel.setVisible(false);
					monthPanel.setVisible(true);
					yearPanel.setVisible(false);

					RadioButtonGroup2.addListener(new ValueChangeListener() 
					{
						public void valueChange(ValueChangeEvent event) 
						{
							clearData();
							RadioButtonGroup4.setValue("Ledger With Value");
							RadioButtonGroup5.setValue("Without Fund");
							po.txtType.setValue("PDF");

							if(event.getProperty().toString()=="Single")
							{
								dSelectedMonth.setEnabled(true);
								dComparedMonth.setEnabled(false);
							}							
							else
							{
								dSelectedMonth.setEnabled(true);
								dComparedMonth.setEnabled(true);
							}
						}
					});
				}
				else if(event.getProperty().toString()=="YEARLY")
				{
					datePanel.setVisible(false);
					monthPanel.setVisible(false);
					yearPanel.setVisible(true);

					RadioButtonGroup3.addListener(new ValueChangeListener() 
					{
						public void valueChange(ValueChangeEvent event) 
						{
							clearData();
							RadioButtonGroup4.setValue("Ledger With Value");
							RadioButtonGroup5.setValue("Without Fund");
							po.txtType.setValue("PDF");

							if(event.getProperty().toString()=="Single")
							{
								dSelectedYear.setEnabled(true);
								dComparedYear.setEnabled(false);
							}							
							else
							{
								dSelectedYear.setEnabled(true);
								dComparedYear.setEnabled(true);
							}
						}
					});
				}
				else
				{
					datePanel.setVisible(false);
					monthPanel.setVisible(false);
					yearPanel.setVisible(false);
				}
			}
		});

		btnPrint.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(RadioButtonGroup1.getValue().toString()=="DATE BETWEEN")
				{
					if (chkDate())
					{
						reportShow();
					}
				}
				else if(RadioButtonGroup1.getValue().toString()=="MONTHLY")
				{
					if(RadioButtonGroup2.getValue().toString()=="Single")
					{
						reportShow();
					}
					else if(Date.parse(dFFiscal.format(dSelectedMonth.getValue())+"") > Date.parse(dFFiscal.format(dComparedMonth.getValue())+""))
					{
						reportShow();
					}
					else
					{
						showNotification("Warning","Compared Month Must be Smaller than Selected Month.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					if(RadioButtonGroup3.getValue().toString()=="Single")
					{
						reportShow();
					}
					else if(Date.parse(dFFiscal.format(dSelectedYear.getValue())+"") > Date.parse(dFFiscal.format(dComparedYear.getValue())+""))
					{
						reportShow();
					}
					else
					{
						showNotification("Warning","Compared Year Must be Smaller than Selected Year.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		btnClose.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

	}

	private boolean chkDate()
	{

		if(Date.parse(dFFiscal.format(fromDate.getValue())+"") <= Date.parse(dFFiscal.format(toDate.getValue())+""))
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+df.format(fromDate.getValue())+"','"+df.format(toDate.getValue())+"')").list().iterator().next().toString();
			System.out.println(f);
			if (f.equals("1"))	
				//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue()))>= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
				//					&&
				//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue()))<= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
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

	private void reportShow()
	{
		String query=null;
		String qry = "";
		String activeFlag = null;
		String report = "";
		Object OpMonth ="";
		Object ClMonth ="";
		String OpYear ="";
		String ClYear ="";


		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();



			HashMap hm = new HashMap();
			//hm.put("comName", sessionBean.getCompanyName());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());

			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());

			if(RadioButtonGroup1.getValue().toString()=="DATE BETWEEN")
			{
				hm.put("fromDate", fromDate.getValue());
				hm.put("toDate", toDate.getValue());

				String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+df.format(toDate.getValue())+"')").list().iterator().next().toString();
				//String voucher =  "voucher"+fsl;

				//if(!fsl.equals("0"))
				//{
				session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();

				//}
				tx.commit();
				
				qry = "SELECT dbo.udf_MoneyFormat(SUM(generalFund)) as a,dbo.udf_MoneyFormat(SUM(internshipFund))as b,dbo.udf_MoneyFormat(SUM(developmentFund))as c,dbo.udf_MoneyFormat(SUM(totalAmount))as d FROM dbo.funPaymentReceipt('"+df.format(fromDate.getValue())+"','"+df.format(toDate.getValue())+"','1') where Particular in ('OPENING BALANCE','RECEIPTS') ";
				totalValue(qry);

				if(RadioButtonGroup4.getValue().toString().equalsIgnoreCase("Ledger With Value"))
				{
					query = "SELECT * FROM dbo.funPaymentReceipt('"+df.format(fromDate.getValue())+"','"+df.format(toDate.getValue())+"','1') where totalAmount!='' ";
				}
				else
				{
					query = "SELECT * FROM dbo.funPaymentReceipt('"+df.format(fromDate.getValue())+"','"+df.format(toDate.getValue())+"','1')";
				}

				if(RadioButtonGroup5.getValue().toString().equalsIgnoreCase("Without Fund"))
				{
					report = "report/account/profitloss/receiptPaymentStatement.jasper";
				}
				else
				{
					report = "report/account/profitloss/receiptPaymentStatementFundWise.jasper";
				}

			}
			if(RadioButtonGroup1.getValue().toString()=="MONTHLY")
			{
				hm.put("flag", "MONTHLY");

				if(RadioButtonGroup2.getValue().toString()=="Single")
				{

					hm.put("fromDate", dSelectedMonth.getValue());
					
					qry = "SELECT dbo.udf_MoneyFormat(SUM(presentGeneralFund))as a,dbo.udf_MoneyFormat(SUM(presentInternshipFund))as b,dbo.udf_MoneyFormat(SUM(presentDevelopmentFund))as c,dbo.udf_MoneyFormat(SUM(presentTotal))as d FROM dbo.funPaymentReceiptComparative('"+ym.format(dSelectedMonth.getValue())+"','','MONTHLY','1') where Particular in ('OPENING BALANCE','RECEIPTS')";
					totalValue(qry);

					if(RadioButtonGroup4.getValue().toString().equalsIgnoreCase("Ledger With Value"))
					{
						query = "SELECT * FROM dbo.funPaymentReceiptComparative('"+ym.format(dSelectedMonth.getValue())+"','','MONTHLY','1') where presentTotal!='' ";
					}
					else
					{
						query = "SELECT * FROM dbo.funPaymentReceiptComparative('"+ym.format(dSelectedMonth.getValue())+"','','MONTHLY','1')";
					}

					if(RadioButtonGroup5.getValue().toString().equalsIgnoreCase("Without Fund"))
					{
						report = "report/account/profitloss/singleReceiptPaymentStatement.jasper";
					}
					else
					{
						report = "report/account/profitloss/singleReceiptPaymentStatementFundWise.jasper";
					}
				}
				else
				{
					hm.put("fromDate", dSelectedMonth.getValue());
					hm.put("toDate", dComparedMonth.getValue());
					
					qry = "SELECT dbo.udf_MoneyFormat(SUM(presentGeneralFund))as a,dbo.udf_MoneyFormat(SUM(presentInternshipFund))as b,dbo.udf_MoneyFormat(SUM(presentDevelopmentFund))as c,dbo.udf_MoneyFormat(SUM(presentTotal))as d," +
							"dbo.udf_MoneyFormat(SUM(previousGeneralFund))as e,dbo.udf_MoneyFormat(SUM(previousInternshipFund))as f,dbo.udf_MoneyFormat(SUM(previousDevelopmentFund))as g,dbo.udf_MoneyFormat(SUM(previousTotal))as h " +
							"FROM dbo.funPaymentReceiptComparative('"+ym.format(dSelectedMonth.getValue())+"','"+ym.format(dComparedMonth.getValue())+"','MONTHLY','1') where Particular in ('OPENING BALANCE','RECEIPTS') ";
					
					totalValue(qry);

					hm.put("GabTotal2", prevGtotal);
					hm.put("IabTotal2", prevItotal);
					hm.put("DabTotal2", prevDtotal);
					hm.put("abTotal2", prevTotal);

					if(RadioButtonGroup4.getValue().toString().equalsIgnoreCase("Ledger With Value"))
					{
						query = "SELECT * FROM dbo.funPaymentReceiptComparative('"+ym.format(dSelectedMonth.getValue())+"','"+ym.format(dComparedMonth.getValue())+"','MONTHLY','1') where previousTotal!='' or presentTotal!='' ";
					}
					else
					{
						query = "SELECT * FROM dbo.funPaymentReceiptComparative('"+ym.format(dSelectedMonth.getValue())+"','"+ym.format(dComparedMonth.getValue())+"','MONTHLY','1') ";	
					}

					if(RadioButtonGroup5.getValue().toString().equalsIgnoreCase("Without Fund"))
					{
						report = "report/account/profitloss/comparativeReceiptPaymentStatement.jasper";
					}
					else
					{
						report = "report/account/profitloss/comparativeReceiptPaymentStatementFundWise.jasper";
					}
				}
			}
			if(RadioButtonGroup1.getValue().toString()=="YEARLY")
			{
				hm.put("flag", "YEARLY");

				if(RadioButtonGroup3.getValue().toString()=="Single")
				{
					hm.put("fromDate", dSelectedYear.getValue());

					qry = "SELECT dbo.udf_MoneyFormat(SUM(presentGeneralFund))as a,dbo.udf_MoneyFormat(SUM(presentInternshipFund))as b,dbo.udf_MoneyFormat(SUM(presentDevelopmentFund))as c,dbo.udf_MoneyFormat(SUM(presentTotal))as d FROM dbo.funPaymentReceiptComparative('"+y.format(dSelectedYear.getValue())+"','','YEARLY','1') where Particular in ('OPENING BALANCE','RECEIPTS') ";
					totalValue(qry);

					if(RadioButtonGroup4.getValue().toString().equalsIgnoreCase("Ledger With Value"))
					{
						query = "SELECT * FROM dbo.funPaymentReceiptComparative('"+y.format(dSelectedYear.getValue())+"','','YEARLY','1') where presentTotal!='' ";
					}
					else
					{
						query = "SELECT * FROM dbo.funPaymentReceiptComparative('"+y.format(dSelectedYear.getValue())+"','','YEARLY','1') ";
					}

					if(RadioButtonGroup5.getValue().toString().equalsIgnoreCase("Without Fund"))
					{
						report = "report/account/profitloss/singleReceiptPaymentStatement.jasper";
					}
					else
					{
						report = "report/account/profitloss/singleReceiptPaymentStatementFundWise.jasper";
					}
				}
				else
				{
					hm.put("fromDate", dSelectedYear.getValue());
					hm.put("toDate", dComparedYear.getValue());


					qry = "SELECT dbo.udf_MoneyFormat(SUM(presentGeneralFund))as a,dbo.udf_MoneyFormat(SUM(presentInternshipFund))as b,dbo.udf_MoneyFormat(SUM(presentDevelopmentFund))as c,dbo.udf_MoneyFormat(SUM(presentTotal))as d," +
							"dbo.udf_MoneyFormat(SUM(previousGeneralFund))as e,dbo.udf_MoneyFormat(SUM(previousInternshipFund))as f,dbo.udf_MoneyFormat(SUM(previousDevelopmentFund))as g,dbo.udf_MoneyFormat(SUM(previousTotal))as h " +
							"FROM dbo.funPaymentReceiptComparative('"+y.format(dSelectedYear.getValue())+"','"+y.format(dComparedYear.getValue())+"','YEARLY','1') where Particular in ('OPENING BALANCE','RECEIPTS') ";
					
					totalValue(qry);

					hm.put("GabTotal2", prevGtotal);
					hm.put("IabTotal2", prevItotal);
					hm.put("DabTotal2", prevDtotal);
					hm.put("abTotal2", prevTotal);

					if(RadioButtonGroup4.getValue().toString().equalsIgnoreCase("Ledger With Value"))
					{
						query = "SELECT * FROM dbo.funPaymentReceiptComparative('"+y.format(dSelectedYear.getValue())+"','"+y.format(dComparedYear.getValue())+"','YEARLY','1') where previousTotal!='' or presentTotal!='' ";
					}
					else
					{
						query = "SELECT * FROM dbo.funPaymentReceiptComparative('"+y.format(dSelectedYear.getValue())+"','"+y.format(dComparedYear.getValue())+"','YEARLY','1') ";
					}

					if(RadioButtonGroup5.getValue().toString().equalsIgnoreCase("Without Fund"))
					{
						report = "report/account/profitloss/comparativeReceiptPaymentStatement.jasper";
					}
					else
					{
						report = "report/account/profitloss/comparativeReceiptPaymentStatementFundWise.jasper";
					}
				}
			}

			System.out.println("Query: "+query);

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				hm.put("GabTotal", prsntGtotal);
				hm.put("IabTotal", prsntItotal);
				hm.put("DabTotal", prsntDtotal);
				hm.put("abTotal", prsntTotal);

				Window win = new ReportViewer(hm,report,
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);
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
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void totalValue(String sql)
	{
		prevGtotal="0";
		prevItotal="0";
		prevDtotal="0";
		prevTotal="0";
		prsntGtotal="0";
		prsntItotal="0";
		prsntDtotal="0";
		prsntTotal="0";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
						
				if(RadioButtonGroup2.getValue().toString()=="Comparative" || RadioButtonGroup3.getValue().toString()=="Comparative" )	
				{
					prsntGtotal = element[0].toString();
					prsntItotal = element[1].toString();
					prsntDtotal = element[2].toString();
					prsntTotal = element[3].toString();
					
					prevGtotal = element[4].toString();
					prevItotal = element[5].toString();
					prevDtotal = element[6].toString();
					prevTotal = element[7].toString();
				}
				else
				{
					prsntGtotal = element[0].toString();
					prsntItotal = element[1].toString();
					prsntDtotal = element[2].toString();
					prsntTotal = element[3].toString();
				}
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
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

	/*private void componentIni(boolean t)
		{
			dSelectedYear.setEnabled(t);
			dComparedYear.setEnabled(t);
			dSelectedMonth.setEnabled(t);
			dComparedMonth.setEnabled(t);
			FromDate.setEnabled(t);
			ToDate.setEnabled(t);
		}*/

	private void clearData()
	{
		fromDate.setValue(new java.util.Date());
		toDate.setValue(new java.util.Date());

		dSelectedMonth.setValue(new java.util.Date());
		dComparedMonth.setValue(new java.util.Date());

		dSelectedYear.setValue(new java.util.Date());
		dComparedYear.setValue(new java.util.Date());
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("830px");
		mainLayout.setHeight("270px");
		mainLayout.setMargin(false);

		/*------------------------------leftPanel---------------------------------------------*/
		// leftPanel
		leftPanel = new Panel();
		leftPanel.setWidth("150px");
		leftPanel.setHeight("140px");
		leftPanel.setImmediate(true);
		leftPanel.setStyleName("radius");
		mainLayout.addComponent(leftPanel, "top:20px;left:20px;");

		// lblPeriod
		lblPeriod = new Label();
		lblPeriod.setImmediate(true);
		lblPeriod.setContentMode(Label.CONTENT_XHTML);
		lblPeriod.setValue("<font color='#9c27b0' size='2px'><b>Period</b></font>");
		mainLayout.addComponent(lblPeriod, "top:30px;left:35px;");

		// RadioButtonGroup1
		RadioButtonGroup1 = new OptionGroup("",groupButton1);
		RadioButtonGroup1.setValue("DATE BETWEEN");
		RadioButtonGroup1.setImmediate(true);
		RadioButtonGroup1.setStyleName("vertical");
		mainLayout.addComponent(RadioButtonGroup1,"top:55.0px;left:30.0px");

		/*-------------------------previewPanel-----------------------------------------*/
		// previewPanel
		previewPanel = new Panel();
		previewPanel.setWidth("150px");
		previewPanel.setHeight("100px");
		previewPanel.setImmediate(true);
		previewPanel.setStyleName("radius");

		po.setImmediate(true);
		previewPanel.addComponent(po);
		mainLayout.addComponent(previewPanel, "top:165px;left:20px;");

		// lblPreviewOption
		lblPreviewOption = new Label();
		lblPreviewOption.setImmediate(true);
		lblPreviewOption.setContentMode(Label.CONTENT_XHTML);
		lblPreviewOption.setValue("<font color='#9c27b0' size='2px'><b>Report Preview</b></font>");
		mainLayout.addComponent(lblPreviewOption, "top:170px;left:42px;");




		/*---------------------------------Date Between--------------------------------*/

		// datePanel
		datePanel = new Panel();
		datePanel.setImmediate(true);
		datePanel.setWidth("440px");
		datePanel.setHeight("245px");
		datePanel.setStyleName("radius");

		// FromDate
		fromDate = new PopupDateField("From Date : ");
		fromDate.setImmediate(true);
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setValue(new java.util.Date());

		// ToDate
		toDate = new PopupDateField("To Date : ");
		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setValue(new java.util.Date());

		// adding components to datePanel
		fLayoutDate.addComponent(fromDate);
		fLayoutDate.addComponent(toDate);

		datePanel.addComponent(fLayoutDate);

		mainLayout.addComponent(datePanel, "top:20px;left:180px;");

		/*---------------------------------Monthly--------------------------------*/

		// monthPanel
		monthPanel = new Panel();
		monthPanel.setImmediate(true);
		monthPanel.setWidth("440px");
		monthPanel.setHeight("245px");
		monthPanel.setStyleName("radius");

		// RadioButtonGroup1
		RadioButtonGroup2 = new OptionGroup("",groupButton2);
		RadioButtonGroup2.setValue("Single");
		RadioButtonGroup2.setImmediate(true);
		RadioButtonGroup2.setStyleName("horizontalBold");

		// monthSeperator
		monthSeperator = new Label();
		monthSeperator.setHeight("15px");
		monthSeperator.setImmediate(true);

		// dSelectedMonth
		dSelectedMonth = new InlineDateField("Select Month : ");
		dSelectedMonth.setInvalidAllowed(false);
		dSelectedMonth.setResolution(InlineDateField.RESOLUTION_MONTH);
		dSelectedMonth.setImmediate(true);

		// dComparedMonth
		dComparedMonth = new InlineDateField("Compared With : ");
		dComparedMonth.setInvalidAllowed(false);
		dComparedMonth.setResolution(InlineDateField.RESOLUTION_MONTH);
		dComparedMonth.setImmediate(true);
		dComparedMonth.setEnabled(false);

		// adding components to datePanel
		fLayoutMonth.addComponent(RadioButtonGroup2);
		fLayoutMonth.addComponent(monthSeperator);
		fLayoutMonth.addComponent(dSelectedMonth);
		fLayoutMonth.addComponent(dComparedMonth);

		monthPanel.addComponent(fLayoutMonth);

		mainLayout.addComponent(monthPanel, "top:20px;left:180px;");

		/*---------------------------------Yearly--------------------------------*/

		// monthPanel
		yearPanel = new Panel();
		yearPanel.setImmediate(true);
		yearPanel.setWidth("440px");
		yearPanel.setHeight("245px");
		yearPanel.setStyleName("radius");

		// RadioButtonGroup3
		RadioButtonGroup3 = new OptionGroup("",groupButton3);
		RadioButtonGroup3.setValue("Single");
		RadioButtonGroup3.setImmediate(true);
		RadioButtonGroup3.setStyleName("horizontalBold");

		// yearSeperator
		yearSeperator = new Label();
		yearSeperator.setHeight("15px");
		yearSeperator.setImmediate(true);

		// dSelectedYear
		dSelectedYear = new InlineDateField("Select Year : ");
		dSelectedYear.setWidth("120px");
		dSelectedYear.setDateFormat("yyyy");
		dSelectedYear.setImmediate(true);
		dSelectedYear.setResolution(PopupDateField.RESOLUTION_YEAR);

		// dComparedYear
		dComparedYear = new InlineDateField("Compared With : ");
		dComparedYear.setWidth("120px");
		dComparedYear.setDateFormat("yyyy");
		dComparedYear.setImmediate(true);	
		dComparedYear.setEnabled(false);
		dComparedYear.setResolution(PopupDateField.RESOLUTION_YEAR);

		// adding components to datePanel
		fLayoutYear.addComponent(RadioButtonGroup3);
		fLayoutYear.addComponent(yearSeperator);
		fLayoutYear.addComponent(dSelectedYear);
		fLayoutYear.addComponent(dComparedYear);

		yearPanel.addComponent(fLayoutYear);

		mainLayout.addComponent(yearPanel, "top:20px;left:180px;");

		/*--------------------------------------------------------Fund Panel-------------------------------------------*/
		// fundPanel
		fundPanel = new Panel();
		fundPanel.setWidth("180px");
		fundPanel.setHeight("140px");
		fundPanel.setImmediate(true);
		fundPanel.setStyleName("radius");
		mainLayout.addComponent(fundPanel, "top:20px;left:635px;");

		// lblFund
		lblFund = new Label();
		lblFund.setImmediate(true);
		lblFund.setContentMode(Label.CONTENT_XHTML);
		lblFund.setValue("<font color='#9c27b0' size='2px'><b>Fund</b></font>");
		mainLayout.addComponent(lblFund, "top:30px;left:649px;");

		// RadioButtonGroup5
		RadioButtonGroup5 = new OptionGroup("",groupButton5);
		RadioButtonGroup5.setValue("Without Fund");
		RadioButtonGroup5.setImmediate(true);
		RadioButtonGroup5.setStyleName("vertical");
		mainLayout.addComponent(RadioButtonGroup5, "top:55px;left:645px;");
		/*----------------------------------------------------------------------------------------------------------------*/

		/*--------------------------------------------------------Ledger Panel-------------------------------------------*/
		// ledgerPanel
		ledgerPanel = new Panel();
		ledgerPanel.setWidth("180px");
		ledgerPanel.setHeight("100px");
		ledgerPanel.setImmediate(true);
		ledgerPanel.setStyleName("radius");
		mainLayout.addComponent(ledgerPanel, "top:165px;left:635px;");

		// lblLedger
		lblLedger = new Label();
		lblLedger.setImmediate(true);
		lblLedger.setContentMode(Label.CONTENT_XHTML);
		lblLedger.setValue("<font color='#9c27b0' size='2px'><b>Ledger</b></font>");
		mainLayout.addComponent(lblLedger, "top:173px;left:649px;");

		// RadioButtonGroup4
		RadioButtonGroup4 = new OptionGroup("",groupButton4);
		RadioButtonGroup4.setValue("Ledger With Value");
		RadioButtonGroup4.setImmediate(true);
		RadioButtonGroup4.setStyleName("vertical");
		mainLayout.addComponent(RadioButtonGroup4, "top:195px;left:645px;");

		/*--------------------------------------------------------hButtonLayout-------------------------------------------*/
		btnPreview.setWidth("100px");
		btnPreview.setHeight("28px");
		btnPreview.setIcon(new ThemeResource("../icons/search.png"));

		btnPrint.setWidth("80px");
		btnPrint.setHeight("28px");
		btnPrint.setIcon(new ThemeResource("../icons/print.png"));

		btnClose.setWidth("80px");
		btnClose.setHeight("28px");
		btnClose.setIcon(new ThemeResource("../icons/cancel.png"));

		//hButtonLayout
		hButtonLayout.addComponent(btnPrint);
		hButtonLayout.addComponent(btnClose);

		mainLayout.addComponent(hButtonLayout,"top:230px;left:300px;");
		/*----------------------------------------------------------------------------------------------------------------*/

		return mainLayout;
	}
}
