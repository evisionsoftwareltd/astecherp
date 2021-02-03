package acc.appform.transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.accountsSetup.CostInformation;
import acc.appform.accountsSetup.LedgerCreate;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExample;
import com.common.share.MessageBox;
import com.common.share.NumberInWords;
import com.common.share.ReportPdf;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.SpellIt;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.astechac.AstechacApplication;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class BankVoucherPay extends Window
{
	private SessionBean sessionBean;

	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "CQ Print", "Exit");

	private ComboBox description = new ComboBox();

	private Table table = new Table();

	private ArrayList<ComboBox> acHead = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> costCentreMulti = new ArrayList<ComboBox>();
	private ArrayList<TextRead> budget = new ArrayList<TextRead>();
	private ArrayList<TextRead> balance = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> debit = new ArrayList<AmountCommaSeperator>();

	private HorizontalLayout btnLayout = new HorizontalLayout();

	private NumberFormat frmt = new DecimalFormat("#0.00");
	private CommaSeparator cms = new CommaSeparator();

	public Double DebitTotal = 0.00;

	public TextField vfDate = new TextField(); 
	public TextField vflag = new TextField();

	private boolean isUpdate = false;
	private boolean acUnder = false;
	private boolean isFind = false;

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

	private AbsoluteLayout mainLayout;

	private TextField voucherNo;
	private TextRead bankBal;
	private TextRead bankBudget;
	private TextField paidTo;

	private DateField chqDate;
	private DateField date;

	private ComboBox bankHead;
	private ComboBox costCentre;
	private ComboBox cheqNo;

	private Label lblDescription;
	private Label lblBalance;
	private Label lblBudget;
	private Label lblVoucherNo;
	private Label lblChequeDate;
	private Label lblCheque;
	private Label lblPaymentBank;
	private Label lblCostCentre;
	private Label lblPaidTo;
	private Label lblDate;

	private NativeButton btnbankHead;
	private NativeButton btnCostCentre;

	private ImmediateUploadExample bpvUpload = new ImmediateUploadExample("");
	private ImmediateUploadExample bpvChequeUpload = new ImmediateUploadExample("");

	String bpvChequePdf = null;
	String filePathChequeTmp = "";

	Button btnChequePreview;

	String imageChequeLoc = "0" ;

	String bpvPdf = null;
	String filePathTmp = "";

	Button btnPreview;

	String imageLoc = "0" ;

	public BankVoucherPay(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("BANK PAYMENT VOUCHER :: "+this.sessionBean.getCompany());
		this.setResizable(false);
		button.btnNew.focus();

		buildMainLayout();
		setContent(mainLayout);

		buttonActionAdd();

		buttonShortCut();

		bankHeadIni();

		btnIni(true);
		txtEnable(false);

		txtDisable();

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
		List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();

		costCentreIni(costCentre, costCenter);	
		addCmbParticularData();

		Component allComp[] = {date,paidTo,costCentre,bankHead,cheqNo,chqDate,acHead.get(0),button.btnNew,button.btnEdit,button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};
		new FocusMoveByEnter(this,allComp);

		setButtonShortCut();

		button.btnNew.focus();
	}

	public void addCmbParticularData()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			description.removeAllItems();
			String sql = "select NarrationId,Narration from tbNarrationList where NarrationId like '%BP%'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			for(;iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				description.addItem(element[1]);
				description.setItemCaption(element[1],element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(button.btnNew, KeyCode.N, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnEdit, KeyCode.U, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnSave, KeyCode.S, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnRefresh, KeyCode.C, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnDelete, KeyCode.D, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnFind, KeyCode.F, ModifierKey.ALT,ModifierKey.SHIFT));
	}

	private void bankHeadIni()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> bh = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A8','L8') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
			for(Iterator<?> iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				bankHead.addItem(element[0].toString());
				bankHead.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void headSelect(String head,int t)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;
		try
		{
			if(head.equalsIgnoreCase("x"))
			{
				if(t > -1)
				{
					budget.get(t).setValue(0);
					balance.get(t).setValue(0);
				}
				else
				{
					bankBudget.setValue(0);
					bankBal.setValue(0);
				}
			}
			else
			{
				//Session session = SessionFactoryUtil.getInstance().getCurrentSession();


				String msg = "";
				Iterator<?> iter = null;
				if(acUnder)
				{
					iter = session.createSQLQuery("SELECT substring(r,1,1) a,h+isnull('\\'+g,'')+isnull('\\'+s,'')+'\\'+l b FROM VwLedgerList WHERE ledger_Id = '"+head+"' AND CompanyId in ('0', '"+ sessionBean.getCompanyId() +"')").list().iterator();
					Object[] element = (Object[]) iter.next();
					if(element[0].toString().equalsIgnoreCase("A"))
						msg = "Assets\\"+element[1].toString();
					else if(element[0].toString().equalsIgnoreCase("I"))
						msg = "Income\\"+element[1].toString();
					else if(element[0].toString().equalsIgnoreCase("E"))
						msg = "Expenses\\"+element[1].toString();
					else
						msg = "Liabilities\\"+element[1].toString();
					showNotification("",msg,Notification.TYPE_TRAY_NOTIFICATION);
				}

				iter = session.createSQLQuery("SELECT budgetAmount FROM TbBudget WHERE ledger_Id = '"+head+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' AND op_Year = (SELECT year(op_Date) FROM TbFiscal_Year WHERE slNo = "+fsl+")").list().iterator();

				double budAmt = 0;				

				if(iter.hasNext())
					budAmt = Double.valueOf(iter.next().toString());

				double bal = Double.valueOf(session.createSQLQuery("SELECT ISNULL(SUM(drAmount)- SUM(crAmount),0) FROM "+voucher+" WHERE Ledger_Id = '"+head+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator().next().toString());

				double opBal = Double.valueOf(session.createSQLQuery("SELECT ISNULL(SUM(drAmount)- SUM(crAmount),0) FROM TbLedger_Op_Balance WHERE ledger_Id = '"+head+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' AND op_Year = (SELECT year(op_Date) FROM TbFiscal_Year WHERE slNo = "+fsl+")").list().iterator().next().toString());

				if(t>-1)
				{
					budget.get(t).setValue(cms.setComma(Double.valueOf(frmt.format(budAmt))));
					balance.get(t).setValue(cms.setComma(Double.valueOf(frmt.format((bal+opBal)))));
				}
				else
				{
					bankBudget.setValue(cms.setComma(Double.valueOf(frmt.format(budAmt))));
					bankBal.setValue(cms.setComma(Double.valueOf(frmt.format((bal+opBal)))));
				}
			}	
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addChequeNo()
	{
		cheqNo.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = " Select * from tbChequeBook where ledgerId = '"+bankHead.getValue()+"' and (status = 'NO' or status = 'DELETE') ";			
			List<?> bh = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cheqNo.addItem(element[5].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void buttonActionAdd()
	{
		bankHead.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				headSelect(bankHead.getValue()== null?"x":bankHead.getValue().toString(),-1);

				if(!isUpdate && !isFind)
				{
					addChequeNo();
				}
			}
		});

		button.btnNew.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				acUnder = true;
				newBtnAction();
				paidTo.focus();
			}
		});

		button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				acUnder = true;

				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
				{
					updateBtnAction();
					acUnder = true;
				}
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

			}
		});

		button.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(nullCheck())
					//saveBtnAction();
					if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
						saveBtnAction();
					else
						showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

			}
		});

		button.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				btnIni(true);
				txtEnable(false);
				txtClear();
				bankHeadIni();
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
				List costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();
				costCentreIni(costCentre, costCenter);
				table.removeAllItems();
				tableInitialise();
				addCmbParticularData();
				button.btnNew.focus();
			}
		});

		button.btnDelete.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
					deleteBtnAction();
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

			}
		});

		button.btnFind.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				acUnder = false;
				findBtnAction();
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		button.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewBtnAction();
			}
		});

		button.btnChequePrint.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				chequePrintBtnAction();
			}
		});

		//		button.btnPrint.addListener( new ClickListener()
		//		{
		//			public void buttonClick(ClickEvent event) 
		//			{
		//			//	if(nullCheck())
		//					printBtnAction();
		//			}
		//		});

		btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				// Hyperlink to a given URL
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString();

					if(link.endsWith("uptd/"))
					{
						link = link.replaceAll("uptd", "report")+filePathTmp;
					}
					else if(link.endsWith("MSML/"))
					{
						link = link.replaceAll("MSML", "report")+filePathTmp;
					}
					else if(link.endsWith("astecerp/"))
					{
						link = link.replaceAll("astecerp", "report")+filePathTmp;
					}
					else if(link.endsWith("UNIGLOBAL/"))
					{
						link = link.replaceAll("UNIGLOBAL", "report")+filePathTmp;
					}

					System.out.println(link);
					System.out.println("aa :"+event.getSource());

					getWindow().open(new ExternalResource(link),"_blank", // window name
							500, // width
							200, // weight
							Window.BORDER_NONE // decorations
							);
				}
				if(isUpdate)
				{
					if(!bpvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								link = link.replaceAll("uptd/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("MSML/"))
							{
								link = link.replaceAll("MSML/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("astecerp/"))
							{
								link = link.replaceAll("astecerp/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("UNIGLOBAL/"))
							{
								link = link.replaceAll("UNIGLOBAL/", imageLoc.substring(27, imageLoc.length()));
							}

							System.out.println(link);
							System.out.println("bb : "+event.getSource());

							getWindow().open(new ExternalResource(link),"_blank", // window name
									500, // width
									200, // weight
									Window.BORDER_NONE // decorations
									);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+filePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+filePathTmp;
						}
						else if(link.endsWith("astecerp/"))
						{
							link = link.replaceAll("astecerp", "report")+filePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+filePathTmp;
						}

						System.out.println(link);
						System.out.println("aa :"+event.getSource());

						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
			}
		});

		btnChequePreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				// Hyperlink to a given URL
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString();

					if(link.endsWith("uptd/"))
					{
						link = link.replaceAll("uptd", "report")+filePathChequeTmp;
					}
					else if(link.endsWith("MSML/"))
					{
						link = link.replaceAll("MSML", "report")+filePathChequeTmp;
					}
					else if(link.endsWith("astecerp/"))
					{
						link = link.replaceAll("astecerp", "report")+filePathChequeTmp;
					}
					else if(link.endsWith("UNIGLOBAL/"))
					{
						link = link.replaceAll("UNIGLOBAL", "report")+filePathChequeTmp;
					}

					System.out.println(link);
					System.out.println("aa :"+event.getSource());

					getWindow().open(new ExternalResource(link),"_blank", // window name
							500, // width
							200, // weight
							Window.BORDER_NONE // decorations
							);
				}
				if(isUpdate)
				{
					if(!bpvChequeUpload.actionCheck)
					{
						if(!imageChequeLoc.equalsIgnoreCase("0"))
						{

							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								link = link.replaceAll("uptd/", imageChequeLoc.substring(22, imageChequeLoc.length()));
							}
							else if(link.endsWith("MSML/"))
							{
								link = link.replaceAll("MSML/", imageChequeLoc.substring(22, imageChequeLoc.length()));
							}
							else if(link.endsWith("astecerp/"))
							{
								link = link.replaceAll("astecerp/", imageChequeLoc.substring(22, imageChequeLoc.length()));
							}
							else if(link.endsWith("UNIGLOBAL/"))
							{
								link = link.replaceAll("UNIGLOBAL/", imageChequeLoc.substring(27, imageChequeLoc.length()));
							}

							System.out.println(link);
							System.out.println("bb : "+event.getSource());

							getWindow().open(new ExternalResource(link),"_blank", // window name
									500, // width
									200, // weight
									Window.BORDER_NONE // decorations
									);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvChequeUpload.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+filePathChequeTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+filePathChequeTmp;
						}
						else if(link.endsWith("astecerp/"))
						{
							link = link.replaceAll("astecerp", "report")+filePathChequeTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+filePathChequeTmp;
						}

						System.out.println(link);
						System.out.println("aa :"+event.getSource());

						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
			}
		});

		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"");
				System.out.println("Done");
			}
		});

		bpvChequeUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePathCheque(0,"");
				System.out.println("Done");
			}
		});

		btnbankHead.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				bankHeadLink();
			}
		});

		btnCostCentre.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				costCentreLink();	
			}
		});
	};


	private void chequePrintBtnAction()
	{
		if(!voucherNo.getValue().toString().isEmpty() && cheqNo.getValue()!=null)
		{
			try
			{
				HashMap hm = new HashMap();

				double sum = 0.0;

				for(int i=0;i<debit.size();i++)
				{
					if(!debit.get(i).getValue().toString().isEmpty())
					{
						sum += Double.parseDouble(debit.get(i).getValue().toString().replaceAll(",", ""));
					}
				}

				String sDate="";
				String tempDate=new SimpleDateFormat("ddMMyyyy").format(chqDate.getValue()).toString();
				System.out.println(tempDate);
				for(int i=0;i<tempDate.length();i++)
				{			
					sDate = sDate + tempDate.charAt(i)+" ";
				}

				hm.put("date", df.format(chqDate.getValue()).toString());
				hm.put("amount", new CommaSeparator().setComma(sum).toString());
				hm.put("amount_word", new NumberInWords().setWords(String.valueOf(sum)).toString());
				hm.put("spaceDate", sDate);
				hm.put("ledgerName", acHead.get(0).getItemCaption(acHead.get(0).getValue()));
				hm.put("userName", sessionBean.getUserName());
				hm.put("userIp", sessionBean.getUserIp());

				Window win = new ReportViewer(hm,"report/account/voucher/rptChequePrint.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				win.setStyleName("cwindow");
				getParent().getWindow().addWindow(win);
				win.setCaption("CHEQUE PRINT :: "+sessionBean.getCompany());
			}
			catch(Exception ex)
			{
				this.getParent().showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning","There are no data for Print",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void previewBtnAction()
	{
		if(!voucherNo.getValue().toString().isEmpty())
		{
			showReport();
		}
		else
		{
			showNotification("Warning","There are no data for Preview",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void showReport()
	{
		try
		{
			HashMap hm = new HashMap();
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());

			hm.put("logo", sessionBean.getCompanyLogo());

			String link = getApplication().getURL().toString();

			if(link.endsWith("uptd/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("uptd/", ""));
			}
			else if(link.endsWith("MSML/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("MSML/", ""));
			}
			else if(link.endsWith("astecherp/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("astecherp/", ""));
			}
			else if(link.endsWith("UNIGLOBAL/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("UNIGLOBAL/", ""));
			}

			String sql = "SELECT Voucher_No,Date,TransactionWith,Narration,DrAmount,CrAmount,dbo.number(CrAmount)+' Only' AS amtWord,"+
					"Cheque_No,Cheque_Date,Ledger_Name,replace(attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill FROM dbo.vwChequeRegister WHERE Voucher_No in('"+voucherNo.getValue().toString()+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount DESC";

			hm.put("sql",sql);
			Window win = new ReportViewer(hm,"report/account/voucher/BankPaymentVoucher.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			win.setStyleName("cwindow");
			getParent().getWindow().addWindow(win);
			win.setCaption("BANK PAYMENT VOUCHER :: "+sessionBean.getCompany());
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public void bankHeadLink()
	{
		Window win = new LedgerCreate(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				bankHeadIni();
			}
		});
		this.getParent().addWindow(win);
	}

	public void costCentreLink()
	{
		Window win = new CostInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
				List costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();

				costCentreIni(costCentre, costCenter);
			}
		});
		this.getParent().addWindow(win);
	}

	private String imagePathCheque(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			// image move
			if(bpvChequeUpload.fileName.trim().length()>0)
				try {
					if(bpvChequeUpload.fileName.toString().endsWith(".jpg")){
						String path = sessionBean.getUserId()+"BPVC";
						fileMove(basePath+bpvChequeUpload.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						bpvChequePdf = SessionBean.imagePathTmp+path+".jpg";
						filePathChequeTmp = path+".jpg";
					}else{
						String path = sessionBean.getUserId()+"BPVC";
						fileMove(basePath+bpvChequeUpload.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						bpvChequePdf = SessionBean.imagePathTmp+path+".pdf";
						filePathChequeTmp = path+".pdf";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return bpvChequePdf;
		}

		if(flag==1)
		{
			// image move
			if(bpvChequeUpload.fileName.trim().length()>0)
				try {
					if(bpvChequeUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvChequeUpload.fileName.trim(),SessionBean.imagePath+projectName+"/chequeBillPayment/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/chequeBillPayment/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvChequeUpload.fileName.trim(),SessionBean.imagePath+projectName+"/chequeBillPayment/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/chequeBillPayment/"+path+".pdf";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return stuImage;
		}

		return null;
	}

	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
				try {
					if(bpvUpload.fileName.toString().endsWith(".jpg")){
						String path = sessionBean.getUserId()+"BPV";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						bpvPdf = SessionBean.imagePathTmp+path+".jpg";
						filePathTmp = path+".jpg";
					}else{
						String path = sessionBean.getUserId()+"BPV";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						bpvPdf = SessionBean.imagePathTmp+path+".pdf";
						filePathTmp = path+".pdf";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return bpvPdf;
		}

		if(flag==1)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
				try {
					if(bpvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/bankBillPayment/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/bankBillPayment/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/bankBillPayment/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/bankBillPayment/"+path+".pdf";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return stuImage;
		}

		return null;
	}

	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp){}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}

	private boolean nullCheck()
	{
		boolean a = false;
		if(!paidTo.getValue().equals(""))
		{
			if(cheqNo.getValue()!=null)
			{
				if(bankHead.getValue()!=null)
				{
					if(costCentre.getValue()!=null)
					{
						for(int i=0;i<acHead.size();i++)
						{
							if(acHead.get(i).getValue()!= null)
							{
								if(Double.valueOf("0"+debit.get(i).getValue()) == 0)
								{
									showNotification("","Please insert amount for "+acHead.get(i).getItemCaption(acHead.get(i).getValue()),Notification.TYPE_WARNING_MESSAGE);
									debit.get(i).focus();
									return false;
								}
								else
								{
									a = true;
								}
							}
						}
						if(!a)
						{					
							showNotification("Warning ","Please select an account head.",Notification.TYPE_WARNING_MESSAGE);
							acHead.get(0).focus();
						}
					}
					else
					{				
						showNotification("Warning ","Please select a cost centre head.",Notification.TYPE_WARNING_MESSAGE);
						costCentre.focus();
					}
				}
				else
				{
					showNotification("Warning ","Please select a bank head.",Notification.TYPE_WARNING_MESSAGE);
					bankHead.focus();
				}
			}
			else
			{
				showNotification("Warning ","Please Select cheque no.",Notification.TYPE_WARNING_MESSAGE);
				cheqNo.focus();
			}
		}
		else
		{
			showNotification("Warning ","Please select a paid .",Notification.TYPE_WARNING_MESSAGE);
			paidTo.focus();
		}
		return a;
	}

	private void buttonShortCut()
	{
		button.btnNew.setClickShortcut(KeyCode.N, ModifierKey.ALT);
		button.btnEdit.setClickShortcut(KeyCode.U, ModifierKey.ALT);
		button.btnSave.setClickShortcut(KeyCode.S, ModifierKey.ALT);
		button.btnRefresh.setClickShortcut(KeyCode.C, ModifierKey.ALT);
		button.btnDelete.setClickShortcut(KeyCode.D, ModifierKey.ALT);
		button.btnFind.setClickShortcut(KeyCode.F, ModifierKey.ALT);
	}

	private void newBtnAction()
	{
		isUpdate = false;
		isFind = false;
		btnIni(false);
		txtEnable(true);
		txtClear();
	}

	private void updateBtnAction()
	{
		if(sessionBean.isUpdateable())
		{
			if(voucherNo.getValue().toString().trim().length()>0)
			{
				btnIni(false);
				txtEnable(true);
				isUpdate = true;
				isFind = false;
			}
			else
			{
				showNotification("Edit Failed","There are no data for Edit.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void saveBtnAction()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						//						if (!paidTo.equals(null))
						//						{
						//							if (!cheqNo.equals(null))
						//							{
						boolean check = DoubleEntryCheck();
						if(check==true)
						{
							insertData();
						}
						else
						{

						}
						//							}
						//							else
						//							{
						//								showNotification("Warning : Please enter cheque number!",Notification.TYPE_ERROR_MESSAGE);
						//
						//							}
						//						}
					}
				}
			});
		}
	}

	private void updateData()
	{
		if(sessionBean.isUpdateable())
		{
			Transaction tx = null;

			//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue()))>= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
			//					&&
			//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue()))<= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			//			{
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				//String sql;
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				String cheque =  "chequeDetails"+fsl;

				String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
						"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'Update' TType,costId, companyId from "+
						""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				sql = "INSERT INTO tbDeleteUpdateCheque SELECT Cheque_No,Cheque_Date,PartyAccountDetails,Voucher_No,"+
						"Bank_Id,ClearanceDate,BankName,BranchName,MrNo,PayNo,pbankId,pbranchId,'Admin' AS UserName,"+
						"CURRENT_TIMESTAMP AS DUDtime,'Update' AS TType, companyId FROM vwChequeDetails WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				//voucher delete
				sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();


				double tb = 0;
				NumberFormat f = new DecimalFormat("#0.00");

				String imagePath = imagePath(1,voucherNo.getValue().toString())==null?imageLoc:imagePath(1,voucherNo.getValue().toString());
				String imagePathCheque = imagePathCheque(1,voucherNo.getValue().toString())==null?imageChequeLoc:imagePath(1,voucherNo.getValue().toString());

				//debit insert
				for(int i=0;i<acHead.size();i++)
				{
					if(acHead.get(i).getValue()!= null && costCentreMulti.get(i).getValue() != null)
					{
						tb = tb + Double.valueOf("0"+debit.get(i).getValue().toString());

						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill, attachChequeBill) "+
								" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','"+
								new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken()+"','"+
								description.getValue()+"','0"+ debit.get(i).getValue()+"','0','dba','"+paidTo.getValue()+"','"+ costCentreMulti.get(i).getValue()+"','"+ sessionBean.getUserId() +"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"', '"+imagePathCheque+"')";
						session.createSQLQuery(sql).executeUpdate();
						System.out.println(sql);
					}
				}

				//credit insert
				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill, attachChequeBill) "+
						" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','"+bankHead.getValue()+"','"+description.getValue()+"','0','"+
						f.format(tb)+"','dba','"+paidTo.getValue()+"','"+costCentre.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"', '"+imagePathCheque+"')";
				session.createSQLQuery(sql).executeUpdate();

				//chequeDetails delete
				sql = "DELETE FROM "+cheque+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				//chequeDetails insert 
				sql = "INSERT INTO "+cheque+"(Cheque_No,Cheque_Date,Voucher_No,Bank_Id, companyId) VALUES('"+cheqNo.getValue()+"','"+
						dtfYMD.format(chqDate.getValue())+"','"+voucherNo.getValue()+"','"+bankHead.getValue()+"', '"+ sessionBean.getCompanyId() +"')";
				session.createSQLQuery(sql).executeUpdate();

				sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
						"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'New' TType,costId, companyId from "+
						""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				sql = "INSERT INTO tbDeleteUpdateCheque SELECT Cheque_No,Cheque_Date,PartyAccountDetails,Voucher_No,"+
						"Bank_Id,ClearanceDate,BankName,BranchName,MrNo,PayNo,pbankId,pbranchId,'Admin' AS UserName,"+
						"CURRENT_TIMESTAMP AS DUDtime,'New' AS TType, companyId FROM "+cheque+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				showNotification("Desired voucher no update successfully.");
				txtEnable(false);
				btnIni(true);
				button.btnNew.focus();
			}
			catch(Exception exp)
			{
				showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		//			else
		//			{
		//				showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
		//			}
		//		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void insertData()
	{		
		if(sessionBean.isSubmitable())
		{
			Transaction tx = null;

			//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue()))>= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
			//					&&
			//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue()))<= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			//			{
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				String cheque =  "chequedetails"+fsl;

				int sl = 1;

				Iterator iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1)  FROM "+voucher+" WHERE vouchertype = 'dba' and CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
				if(iter.hasNext())
					sl = Integer.valueOf(iter.next().toString());

				voucherNo.setValue("DR-BK-"+sl);
				double tb = 0;
				NumberFormat f = new DecimalFormat("#0.00");
				String sql = "";


				String imagePath = imagePath(1,"DR-BK-"+sl)==null?imageLoc:imagePath(1,"DR-BK-"+sl);
				String imagePathCheque = imagePathCheque(1,"DR-BK-"+sl)==null?imageChequeLoc:imagePath(1,"DR-BK-"+sl);

				//debit insert
				for(int i=0;i<acHead.size();i++)
				{
					if(acHead.get(i).getValue()!= null && costCentreMulti.get(i).getValue() != null)
					{
						tb = tb+Double.valueOf("0"+debit.get(i).getValue().toString());

						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill, attachChequeBill) "+
								" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','"+
								new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken()+"','"+
								description.getValue()+"','0"+debit.get(i).getValue()+"','0','dba','"+paidTo.getValue()+"','"+costCentreMulti.get(i).getValue()+
								"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId()+"', '"+imagePath+"', '"+imagePathCheque+"')";
						session.createSQLQuery(sql).executeUpdate();
					}
				}

				//credit insert
				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill, attachChequeBill) "+
						" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','"+bankHead.getValue()+"','"+description.getValue()+"','0','"+
						f.format(tb)+"','dba','"+paidTo.getValue()+"','"+costCentre.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"', '"+imagePathCheque+"')";
				session.createSQLQuery(sql).executeUpdate();

				//chequeDetails insert 
				sql = "INSERT INTO "+cheque+"(Cheque_No,Cheque_Date,Voucher_No,Bank_Id, companyId) VALUES('"+cheqNo.getValue().toString().trim()+"','"+
						dtfYMD.format(chqDate.getValue())+"','"+voucherNo.getValue().toString().trim()+"','"+bankHead.getValue().toString().trim()+"', '"+ sessionBean.getCompanyId().toString().trim() +"')";
				session.createSQLQuery(sql).executeUpdate();

				String sqlC = " update tbChequeBook set status = 'YES' where " +
						" ledgerId = '"+bankHead.getValue()+"' and folioNo = '"+cheqNo.getValue()+"' ";

				session.createSQLQuery(sqlC).executeUpdate();

				Object cNo = cheqNo.getValue();
				cheqNo.removeAllItems();
				cheqNo.addItem(cNo);
				cheqNo.setValue(cNo);

				tx.commit();
				showNotification("All information saved successfully.");
				txtEnable(false);
				btnIni(true);
				button.btnNew.focus();
			}
			catch(Exception exp)
			{
				showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}

		}
		//			else
		//			{
		//				showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
		//			}
		//
		//		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void deleteBtnAction()
	{
		if(sessionBean.isDeleteable())
		{
			if(voucherNo.getValue().toString().trim().length()>0)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete voucher no. "+voucherNo.getValue()+"?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							deleteData();
						}
					}
				});
			}
			else
			{
				showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for delete.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void deleteData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequedetails"+fsl;

			String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
					"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'Delete' TType,costId, companyId from "+
					""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();

			sql = "INSERT INTO tbDeleteUpdateCheque SELECT Cheque_No,Cheque_Date,PartyAccountDetails,Voucher_No,"+
					"Bank_Id,ClearanceDate,BankName,BranchName,MrNo,PayNo,pbankId,pbranchId,'Admin' AS UserName,"+
					"CURRENT_TIMESTAMP AS DUDtime,'Delete' AS TType, companyId FROM "+cheque+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();

			sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();

			sql = "DELETE FROM "+cheque+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();

			String sqlC = " update tbChequeBook set status = 'DELETE' where " +
					" ledgerId = '"+bankHead.getValue()+"' and folioNo = '"+cheqNo.getValue()+"' ";
			session.createSQLQuery(sqlC).executeUpdate();

			tx.commit();
			showNotification("Desired Information deleted successfully.");
			txtClear();
			isUpdate = false;
			isFind = false;

		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void txtClear()
	{
		try
		{
			voucherNo.setValue("");
			paidTo.setValue("");
			cheqNo.setValue(null);
			bankHead.setNullSelectionAllowed(true);
			bankHead.setValue(null);
			bankBudget.setValue("");
			bankBal.setValue("");
			costCentre.setNullSelectionAllowed(true);
			costCentre.setValue("");
			description.setValue("");
			//description.setCaption("");
			//date.setValue(new java.util.Date());
			chqDate.setValue(new java.util.Date());
			bankHead.setNullSelectionAllowed(false);

			bpvUpload.fileName = "";
			bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
			filePathTmp = "";
			bpvUpload.actionCheck = false;
			imageLoc = "0";

			bpvChequeUpload.fileName = "";
			bpvChequeUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
			filePathChequeTmp = "";
			bpvChequeUpload.actionCheck = false;
			imageChequeLoc = "0";


			//table.removeAllItems();
			for(int i=0;i<acHead.size();i++)
			{
				acHead.get(i).setValue(null);
				costCentreMulti.get(i).setValue(null);
				budget.get(i).setValue(0);
				balance.get(i).setValue(0);
				debit.get(i).setValue("");			}
		}
		catch(Exception ex)
		{
			//	showNotification("Warning 1:",ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		//button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
		button.btnPreview.setEnabled(t);
		button.btnChequePrint.setEnabled(t);
	}

	private void txtEnable(boolean t)
	{
		paidTo.setEnabled(t);
		bankHead.setEnabled(t);
		date.setEnabled(t);
		cheqNo.setEnabled(t);
		chqDate.setEnabled(t);
		description.setEnabled(t);
		costCentre.setEnabled(t);
		table.setEnabled(t);
		bankBudget.setEnabled(t);
		bankBal.setEnabled(t);
		bpvUpload.setEnabled(t);
		btnPreview.setEnabled(t);
		bpvChequeUpload.setEnabled(t);
		btnChequePreview.setEnabled(t);
		btnCostCentre.setEnabled(t);
		btnbankHead.setEnabled(t);
		//		for(int i=0;i<acHead.size();i++)
		//		{
		//			acHead.get(i).setEnabled(t);
		//			costCentreMulti.get(i).setEnabled(t);
		//			debit.get(i).setEnabled(t);
		//		}
	}

	private void txtDisable()
	{
		//		bankBudget.setEnabled(false);
		//		bankBal.setEnabled(false);
		voucherNo.setEnabled(false);
	}

	private void findBtnAction()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;

		String sqlF = "SELECT tbLedger.Ledger_Id, tbLedger.Ledger_Name, vwVoucher.Date, vwVoucher.Voucher_No, vwVoucher.Narration,vwVoucher.CrAmount,vwVoucher.DrAmount,vwVoucher.vouchertype FROM tbLedger INNER JOIN "+voucher+" as vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id WHERE vouchertype = 'dba' AND vwVoucher.CrAmount !=0 AND vwVoucher.companyId = '"+ sessionBean.getCompanyId() +"' AND ";
		String sqlE = " order by Date,CAST(SUBSTRING(Voucher_No,7,50) AS INT)";
		vflag.setValue("bankpay");
		Window win = new VoucherFind(sessionBean,sqlF,sqlE,voucherNo,vfDate,vflag);
		win.center();
		this.getParent().addWindow(win);
		win.setModal(true);
		win.setCloseShortcut(KeyCode.ESCAPE);
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e) 
			{
				if(voucherNo.getValue().toString().length()>0)
					findInitialise();
			}
		});
		win.bringToFront();
	}

	private void findInitialise()
	{
		isFind = true;

		Transaction tx = null;
		if (!vfDate.getValue().equals(""))
		{
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();	

				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+vfDate.getValue()+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				String cheque =  "ChequeDetails"+fsl;

				Iterator chk = session.createSQLQuery("SELECT cheque_No,cheque_Date FROM "+cheque+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();


				if(chk.hasNext())
				{
					Object[] element = (Object[]) chk.next();
					cheqNo.addItem(element[0]);
					cheqNo.setValue(element[0]);
					chqDate.setValue(new Date(element[1].toString().replace("-", "/").substring(0,10).trim()));
				}

				List list = session.createSQLQuery("SELECT date,transactionWith,ledger_Id,narration,crAmount,costId,attachBill,attachChequeBill FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND crAmount != 0 AND companyId = '"+ sessionBean.getCompanyId() +"'").list();
				System.out.println("SELECT date,transactionWith,ledger_Id,narration,crAmount,costId FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND crAmount != 0 AND companyId = '"+ sessionBean.getCompanyId() +"'");
				for (Iterator iter = list.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();
					date.setValue(new Date(element[0].toString().replace("-", "/").substring(0,10).trim()));
					paidTo.setValue(element[1]);
					bankHead.setValue(element[2]);
					description.addItem(element[3]);
					description.setItemCaption(element[3],element[3].toString());
					description.setValue(element[3]);
					if(element[5]!=null)
						costCentre.setValue(element[5]);

					imageLoc = element[6].toString();
					imageChequeLoc = element[7].toString();
				}

				list = session.createSQLQuery("SELECT ledger_Id, costId, drAmount FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND drAmount != 0 AND companyId = '"+ sessionBean.getCompanyId() +"'").list();
				List ledger = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2))!='A1' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
				List costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' order by costCentreName").list();

				for(int i=0;i<acHead.size();i++)
				{
					acHead.get(i).setValue(null);
					budget.get(i).setValue(0);
					balance.get(i).setValue(0);
					debit.get(i).setValue("");
				}

				int i = 0;

				for (Iterator iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					acHead.get(i).setValue(element[0].toString()+"#"+i);
					costCentreMulti.get(i).setValue(element[1].toString());
					debit.get(i).setValue(frmt.format(element[2]));
					//		i++;
					/*if(i==acHead.size())
					tableRowAdd(i, ledger, costCenter);*/	
				}
				this.bringToFront();
				button.btnEdit.focus();
			}
			catch(Exception exp)
			{
				//showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}

	private void tableInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		List ledger = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2))!='A1' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
		List costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();

		for(int i=0;i<1;i++)
		{
			tableRowAdd(i, ledger, costCenter);	
		}
	}

	private void tableRowAdd(final int ar, final List ledger, final List costCenter)
	{
		try
		{
			acHead.add(ar, new ComboBox());
			acHead.get(ar).setNullSelectionAllowed(false);
			acHead.get(ar).setWidth("100%");
			acHead.get(ar).setImmediate(true);
			acHead.get(ar).removeAllItems();

			costCentreMulti.add(ar, new ComboBox());
			costCentreMulti.get(ar).setWidth("100%");
			costCentreMulti.get(ar).setImmediate(true);
			costCentreMulti.get(ar).setNullSelectionAllowed(false);

			for (Iterator iter = ledger.iterator(); iter.hasNext();) 
			{				
				Object[] element = (Object[]) iter.next();
				acHead.get(ar).addItem(element[0].toString()+"#"+ar);
				acHead.get(ar).setItemCaption(element[0].toString()+"#"+ar, element[1].toString());
			}

			costCentreIni(costCentreMulti.get(ar), costCenter);

			acHead.get(ar).addListener(new ValueChangeListener()
			{
				@Override
				public void valueChange(ValueChangeEvent event)
				{
					if(acHead.get(ar).getValue()!=null)
					{

						for(int i=0;i<ar;i++)
						{
							if(i!=ar)
							{	
								String a_head = acHead.get(ar).getItemCaption(acHead.get(ar).getValue()).toString();
								String b_head = acHead.get(i).getItemCaption(acHead.get(i).getValue()).toString();

								if(a_head.equalsIgnoreCase(b_head))
								{
									showNotification("Warning","Duplicate Ledger Name",Notification.TYPE_WARNING_MESSAGE);
									acHead.get(ar).setValue(null);
									break;
								}
							}
						}

						StringTokenizer st = new StringTokenizer(acHead.get(ar).getValue() == null?"x#"+ar:event.getProperty().toString(),"#");
						String str = st.nextToken();
						int r = Integer.valueOf(st.nextToken());

						headSelect(str,r);
						int temp=debit.size();
						String flag=acHead.get(r).getValue()== null?"":acHead.get(r).getValue().toString();	
						debitActionInit(r);

						/*	if((r+1)==temp && (!flag.equals("")))
						{						
							tableRowAdd(temp, ledger, costCenter);
						}*/
						costCentreMulti.get(ar).focus();

					}

				}
			});

			costCentreMulti.get(ar).addListener(new ValueChangeListener() 
			{				
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					debit.get(ar).focus();
				}
			});


			budget.add(ar, new TextRead());
			budget.get(ar).setWidth("100%");
			budget.get(ar).setStyleName("fright");

			balance.add(ar, new TextRead());
			balance.get(ar).setWidth("100%");
			balance.get(ar).setStyleName("fright");

			debit.add(ar, new AmountCommaSeperator());
			debit.get(ar).setStyleName("fright");
			debit.get(ar).setWidth("100%");
			table.setColumnAlignment("Debit", Table.ALIGN_RIGHT);
			table.addItem(new Object[]{acHead.get(ar), costCentreMulti.get(ar),budget.get(ar),balance.get(ar),debit.get(ar)},ar); 
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void debitActionInit(final int ar)
	{
		try
		{
			debit.get(ar).setImmediate(true);
			debit.get(ar).addListener(new ValueChangeListener() 
			{
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					double totalAmt=0.0;
					for(int i=0;i<debit.size();i++)
					{
						if(!debit.get(i).getValue().toString().trim().isEmpty())
						{
							totalAmt += i == ar ? event.getProperty().toString().trim().isEmpty()? 0:Double.parseDouble(event.getProperty().toString().trim().replace(",", "")): debit.get(i).getValue().toString().trim().isEmpty()?0:Double.parseDouble(debit.get(i).getValue().toString().replace(",", ""));
							//totalAmt += i == ar ? event.getProperty().toString().trim().isEmpty()? 0:Double.parseDouble(event.getProperty().toString().trim()): debit.get(i).getValue().toString().trim().isEmpty()?0:Double.parseDouble(debit.get(i).getValue().toString().replace(",", ""));
							//System.out.println(debit.get(ar).getValue().toString().trim());
							//totalAmt += Double.parseDouble(debit.get(ar).getValue().toString().trim().isEmpty()?"0":debit.get(ar).getValue().toString());
						}
					}

					table.setColumnFooter("Debit", cms.setComma(Double.parseDouble(frmt.format(totalAmt))));
					//table.setColumnFooter("Debit", frmt.format(f.format(totalAmt)));
					//debit.get(ar).setValue(Double.parseDouble(cms.setComma(Double.parseDouble(event.getProperty().toString().trim() == "" ? "0": event.getProperty().toString().trim()))));
					acHead.get(ar + 1).focus();

					DebitTotal = totalAmt;

				}
			});
		}
		catch(Exception ex)
		{
			showNotification("Warning ", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void costCentreIni(ComboBox cmb, List costCenter)
	{
		Transaction tx = null;
		try
		{
			for (Iterator iter = costCenter.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmb.addItem(element[0].toString());
				cmb.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void printBtnAction()
	{
		if(voucherNo.getValue().toString().trim().length()>0)
		{ 
			try
			{
				String amountWord = new SpellIt().number(DebitTotal);

				HashMap<Object, Object> hm = new HashMap<Object, Object>();
				hm.put("date", chqDate.getValue());
				hm.put("spaceDate",spaceDate());
				hm.put("paidto",paidTo.getValue());
				hm.put("amount",DebitTotal.doubleValue());
				hm.put("amount_word",amountWord);

				Window win = new ReportPdf(hm,"report/account/voucher/rptcheque.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

				this.getParent().getWindow().addWindow(win);
				win.setCaption("GENERAL LEDGER :: "+sessionBean.getCompany());
			}
			catch(Exception exp)
			{
				//showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}	
		}
		else
		{
			showNotification("","No Cheque found for Print!",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public String spaceDate()
	{	
		String sDate="";
		String tempDate=new SimpleDateFormat("ddMMyyyy").format(chqDate.getValue()).toString();
		//		System.out.println(tempDate);
		for(int i=0;i<tempDate.length();i++){			
			sDate=sDate+tempDate.charAt(i)+" ";
		}		
		return sDate;
	}

	private boolean DoubleEntryCheck()
	{	
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "ChequeDetails"+fsl;

			String q = "Select [Cheque_No] from "+cheque+" where [Cheque_No] = '"+cheqNo.getValue().toString().trim()+"' and BanK_Id = '"+ bankHead.getValue().toString().trim()+"'";
			Iterator iter = session.createSQLQuery(q).list().iterator();
			//System.out.println("ok");
			String str = null;
			if(iter.hasNext())
			{
				str = iter.next().toString();

			}
			tx.commit();
			if(str!=null){
				this.getParent().showNotification("Cheque number Already exits",Notification.TYPE_WARNING_MESSAGE);

				return false;
			}


		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		return true;
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("950px");
		setHeight("370px");

		// lblPaidTo
		lblPaidTo = new Label();
		lblPaidTo.setImmediate(true);
		lblPaidTo.setWidth("-1px");
		lblPaidTo.setHeight("-1px");
		lblPaidTo.setValue("Paid to :");
		mainLayout.addComponent(lblPaidTo, "top:22.0px;left:20.0px;");

		// txtPaidTo
		paidTo = new TextField();
		paidTo.setImmediate(true);
		paidTo.setWidth("180px");
		paidTo.setHeight("-1px");
		mainLayout.addComponent(paidTo, "top:22.0px;left:130.0px;");

		// lblCostCentre
		lblCostCentre = new Label();
		lblCostCentre.setImmediate(true);
		lblCostCentre.setWidth("-1px");
		lblCostCentre.setHeight("-1px");
		lblCostCentre.setValue("Cost Centre :");
		mainLayout.addComponent(lblCostCentre, "top:47.0px;left:20.0px;");

		// costCentre
		costCentre = new ComboBox();
		costCentre.setImmediate(true);
		costCentre.setHeight("-1px");
		costCentre.setWidth("210px");
		costCentre.setNullSelectionAllowed(false);
		mainLayout.addComponent(costCentre, "top:47.0px;left:130.0px;");

		// btnCostCentre
		btnCostCentre = new NativeButton();
		btnCostCentre.setCaption("");
		btnCostCentre.setImmediate(true);
		btnCostCentre.setWidth("28px");
		btnCostCentre.setHeight("24px");
		btnCostCentre.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnCostCentre,"top:47.0px;left:340.0px;");

		// lblPaymentBank
		lblPaymentBank = new Label();
		lblPaymentBank.setImmediate(true);
		lblPaymentBank.setWidth("-1px");
		lblPaymentBank.setHeight("-1px");
		lblPaymentBank.setValue("Payment Bank AC :");
		mainLayout.addComponent(lblPaymentBank, "top:72.0px;left:20.0px;");

		// bankHead
		bankHead = new ComboBox();
		bankHead.setImmediate(true);
		bankHead.setWidth("-1px");
		bankHead.setHeight("-1px");
		bankHead.setWidth("270px");
		bankHead.setImmediate(true);
		bankHead.setNullSelectionAllowed(false);
		mainLayout.addComponent(bankHead, "top:72.0px;left:130.0px;");

		// btnbankHead
		btnbankHead = new NativeButton();
		btnbankHead.setCaption("");
		btnbankHead.setImmediate(true);
		btnbankHead.setWidth("28px");
		btnbankHead.setHeight("24px");
		btnbankHead.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnbankHead,"top:72.0px;left:405.0px;");

		// lblBudget
		lblBudget = new Label();
		lblBudget.setImmediate(true);
		lblBudget.setWidth("-1px");
		lblBudget.setHeight("-1px");
		lblBudget.setValue("Budget :");
		mainLayout.addComponent(lblBudget, "top:95.0px;left:20.0px;");

		// bankBudget
		bankBudget = new TextRead("",1);
		bankBudget.setImmediate(true);
		bankBudget.setWidth("120px");
		bankBudget.setHeight("-1px");
		mainLayout.addComponent(bankBudget, "top:97.0px;left:131.0px;");

		// lblBalance
		lblBalance = new Label();
		lblBalance.setImmediate(true);
		lblBalance.setWidth("-1px");
		lblBalance.setHeight("-1px");
		lblBalance.setValue("Balance :");
		mainLayout.addComponent(lblBalance, "top:95.0px;left:255.0px;");

		// bankBal
		bankBal = new TextRead("",1);
		bankBal.setImmediate(true);
		bankBal.setWidth("120px");
		bankBal.setHeight("-1px");
		mainLayout.addComponent(bankBal, "top:97.0px;left:310.0px;");

		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:22.0px;left:450.0px;");

		// date
		date = new DateField();
		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		mainLayout.addComponent(date,"top:22.0px;left:530.0px;");

		// lblVoucherNo
		lblVoucherNo = new Label();
		lblVoucherNo.setImmediate(true);
		lblVoucherNo.setWidth("-1px");
		lblVoucherNo.setHeight("-1px");
		lblVoucherNo.setValue("Voucher No :");
		mainLayout.addComponent(lblVoucherNo, "top:47.0px;left:450.0px;");

		// voucherNo
		voucherNo = new TextField();
		voucherNo.setImmediate(true);
		voucherNo.setWidth("120px");
		voucherNo.setHeight("-1px");
		voucherNo.setSecret(false);
		mainLayout.addComponent(voucherNo, "top:47.0px;left:530.0px;");

		// lblCheque
		lblCheque = new Label();
		lblCheque.setImmediate(true);
		lblCheque.setWidth("-1px");
		lblCheque.setHeight("-1px");
		lblCheque.setValue("Cheque No :");
		mainLayout.addComponent(lblCheque, "top:72.0px;left:450.0px;");

		// cheqNo
		cheqNo = new ComboBox();
		cheqNo.setImmediate(true);
		cheqNo.setWidth("120px");
		cheqNo.setHeight("-1px");
		cheqNo.setNewItemsAllowed(false);
		mainLayout.addComponent(cheqNo, "top:72.0px;left:530.0px;");

		// lblChequeDate
		lblChequeDate = new Label();
		lblChequeDate.setImmediate(true);
		lblChequeDate.setWidth("-1px");
		lblChequeDate.setHeight("-1px");
		lblChequeDate.setValue("Cheque Date :");
		mainLayout.addComponent(lblChequeDate, "top:97.0px;left:450.0px;");

		// chqDate
		chqDate = new DateField();
		chqDate.setValue(new java.util.Date());
		chqDate.setResolution(PopupDateField.RESOLUTION_DAY);
		chqDate.setDateFormat("dd-MM-yy");
		chqDate.setInvalidAllowed(false);
		chqDate.setImmediate(true);
		mainLayout.addComponent(chqDate, "top:97.0px;left:530.0px;");

		// bpvUpload
		mainLayout.addComponent(bpvUpload, "top:22.0px;left:660.0px;");

		// btnPreview
		btnPreview = new Button("Bill Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:47.0px;left:780.0px;");

		// bpvChequeUpload
		bpvChequeUpload.upload.setButtonCaption("Attach Cheque");
		bpvChequeUpload.upload.setWidth("100px");
		mainLayout.addComponent(bpvChequeUpload, "top:72.0px;left:660.0px;");

		// btnPreview
		btnChequePreview = new Button("Cheque Preview");
		btnChequePreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnChequePreview.addStyleName("icon-after-caption");
		btnChequePreview.setImmediate(true);
		btnChequePreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnChequePreview, "top:97.0px;left:780.0px;");

		table.setFooterVisible(true);
		table.setWidth("100%");
		table.setHeight("80px");
		table.setFooterVisible(false);
		table.addContainerProperty("LEDGER NAME", ComboBox.class, new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("LEDGER NAME",310);
		table.setColumnFooter("LEDGER NAME", "Total :");
		table.addContainerProperty("Cost Centre", ComboBox.class, new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Cost Centre", 210);
		table.addContainerProperty("Budget", TextRead.class, new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Budget",90);
		table.addContainerProperty("Balance", TextRead.class, new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Balance", 90);
		table.addContainerProperty("Debit AMOUNT", AmountCommaSeperator.class, new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Debit",90);
		table.setFooterVisible(true);

		tableInitialise();

		mainLayout.addComponent(table, "top:130.0px;left:20.0px;");

		// lblDescription
		lblDescription = new Label();
		lblDescription.setImmediate(true);
		lblDescription.setWidth("-1px");
		lblDescription.setHeight("-1px");
		lblDescription.setValue("Description :");
		mainLayout.addComponent(lblDescription, "top:220.0px;left:20.0px;");

		// cmbDescription
		description.setWidth("650px");
		description.setNullSelectionAllowed(false);
		description.setNewItemsAllowed(true);

		mainLayout.addComponent(description, "top:220.0px;left:100.0px;");

		//common button
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout, "top:260.0px;left:90.0px;");

		return mainLayout;
	}
}