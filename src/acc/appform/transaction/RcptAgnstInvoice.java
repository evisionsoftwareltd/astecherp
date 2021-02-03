package acc.appform.transaction;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.SessionFactoryUtil;
import com.common.share.SessionBean;
import com.common.share.CommonButton;
import com.common.share.TextRead;
import com.common.share.FocusMoveByEnter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.astechac.AstechacApplication;


public class RcptAgnstInvoice extends Window
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout frmLayout = new FormLayout();
	private HorizontalLayout horLayout1 = new HorizontalLayout();
	private HorizontalLayout horLayout2 = new HorizontalLayout();
	private HorizontalLayout horLayout3 = new HorizontalLayout();
	private HorizontalLayout horLayout4 = new HorizontalLayout();
	private HorizontalLayout bottomLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private Label lblvoucherDate = new Label("Date :");
	private PopupDateField voucherDate = new PopupDateField();
	private Label lblVoucherNo = new Label("Voucher No :");
	private TextRead txtVoucherNo = new TextRead();
	private Label lblReceivedFrom = new Label("Received from :");
	private ComboBox cmbReceivedFrom = new ComboBox();
	private Label lblBankName = new Label("Bank Name :");
	private TextField txtBankName = new TextField();
	private Label lblBranchName = new Label("Branch Name :");
	private TextField txtBranchName = new TextField();
	private Label lblChequeNo = new Label("Cheque No :");
	private TextField txtCheqNo = new TextField();
	private Label lblCheqDate = new Label("Cheq. Date :");
	private PopupDateField CheqDate = new PopupDateField();
	private Label lblDepositACNo = new Label("Deposit A/C No :");
	private ComboBox cmbDepositAcNo = new ComboBox();
	private TextRead txttotalDue = new TextRead(1);
	private TextRead txtTotalPaid = new TextRead(1);
	private TextRead txtTotalaTax = new TextRead(1);
	private TextRead txtTotalrejection = new TextRead(1);
	private TextRead txtTotaldiscount = new TextRead(1);
	private TextRead txtTotalbalance = new TextRead(1);
	//private TextRead txtTotalbalance = new TextRead(1);
	private Label bottomLabel = new Label("Total :");
	private Table table = new Table();
	private ArrayList<CheckBox> select = new ArrayList<CheckBox>();
	private ArrayList<TextRead> invoiceNo = new ArrayList<TextRead>();
	private ArrayList<TextRead> invoiceDate = new ArrayList<TextRead>();
	private ArrayList<TextRead> currentDue = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> paidAmount = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> aTax = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> rejection = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> discount = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> balance = new ArrayList<TextRead>();
	private ArrayList<TextRead> status = new ArrayList<TextRead>();
	public static TextRead findDate = new TextRead();
	public static TextRead findVoucher = new TextRead();
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private String comWidth = "110px";
	public static String title = "";
	public  static String findQuery = "";
	private String voucherPrefix = "";
	private String voucherType = "";
	private String cashLedgerId = "";	
	private boolean isUpdate = false;
	private NumberFormat frmt = new DecimalFormat("#0.00");

	int row = 8;
	String h="28px";
	String w="100px";

	public RcptAgnstInvoice(final SessionBean sessionBean,String title)
	{
		this.title = title;
		this.sessionBean = sessionBean;		

		if(title.equals("bankReceiptAgnstInvoice"))
		{
			this.setCaption("BANK RECEIVED AGAINST INVOICE :: "+sessionBean.getCompany());
			this.setWidth("770px");
			voucherPrefix = "CR-BK-";
			voucherType = "cb%";
		}

		if(title.equals("cashReceiptAgnstInvoice"))
		{
			this.setCaption("CASH RECEIVED AGAINST INVOICE :: "+sessionBean.getCompany());
			this.setWidth("770px");
			voucherPrefix = "CR-CH-";
			voucherType = "cc%";
		}

//		if(title.equals("journalAgnstInvoice"))
//		{
//			this.setCaption("JOURNAL AGAINST INVOICE :: "+sessionBean.getCompany());
//			this.setWidth("800px");
//			voucherPrefix = "JV-NO-";
//			voucherType = "ja%";
//		}

		this.setResizable(false);

		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				try
				{
					componentInit(true);
					btnIni(false);            	
					txtClear();
					//voucherId();
					cmbReceivedFrom.focus();
				}
				catch(Exception ex)
				{
					showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener( new ClickListener() 
		{
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) 
			{
				//System.out.println("KK");
			
				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(voucherDate.getValue())))
					saveEvent();
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

			}
		});

		button.btnEdit.addListener( new ClickListener() 
		{
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) 
			{
				if(txtVoucherNo.getValue() != "")
				{
//					componentInit(true);
//					btnIni(false);
//					isUpdate = true;
					
					if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(voucherDate.getValue())))
					{
						componentInit(true);
						btnIni(false);
						isUpdate = true;
					}     
					else
						showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

				}
				else
				{
					showNotification("Warning :", "Please Select Data to Edit.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnRefresh.addListener( new ClickListener() 
		{
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
			}
		});

		button.btnDelete.addListener( new ClickListener() 
		{
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) 
			{
				if(txtVoucherNo.getValue() != "")
				{
					MessageBox mb = new MessageBox(getParent(), "Question"+sessionBean.getCompany(), MessageBox.Icon.QUESTION, "Do You Want To Delete All Data.", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
					mb.show(new EventListener()
					{
						public void buttonClicked(ButtonType buttonType) 
						{
							if(buttonType == ButtonType.YES)
							{
								//deleteData();
								if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(voucherDate.getValue())))
									deleteData();
								else
									showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

								
							}
						}
					});
				}
				else
				{
					getParent().showNotification("Warning :", "Please Select Data to Delete.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener( new ClickListener() 
		{
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

		button.btnExit.addListener( new ClickListener() 
		{			
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbReceivedFrom.addListener(new ValueChangeListener()
		{
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) 
			{
				try
				{					
					if(findVoucher.getValue().toString() == "" && cmbReceivedFrom.getValue()!= null)
					{

						//		Closed by Sagar				String query = "select i.vInvoiceNo + '/' + i.vConsigneeNo as InvoiceNo, i.InvoiceDate, convert(varchar(20),(i.VatAmount + i.Total)) as TotalAmount,convert(varchar(20),((i.VatAmount + i.Total) - ISNULL(SUM(IVR.PaidAmount), 0) + ISNULL(SUM(IVR.AdvanceTax), 0) + ISNULL(SUM(IVR.Rejection), 0) + ISNULL(SUM(IVR.Discount), 0))) AS balance,0,0,0,0,convert(varchar(20),((i.VatAmount + i.Total) - ISNULL(SUM(IVR.PaidAmount), 0) + ISNULL(SUM(IVR.AdvanceTax), 0) + ISNULL(SUM(IVR.Rejection), 0) + ISNULL(SUM(IVR.Discount), 0))) AS balance,year(i.InvoiceDate)[Year]" +
						//						"from tbInvoiceInfo i left outer join tbInvoiceVoucherReceived ivr on i.vInvoiceNo = ivr.InvoiceNo and i.vConsigneeNo = ivr.ConsigneeNo and year(i.InvoiceDate) = YEAR(ivr.Date) " +
						//						"where i.Status in ('Paid','Unpaid','Partial') and i.ConsigneeLedger = '"+ cmbReceivedFrom.getValue().toString() +"' GROUP BY I.vInvoiceNo, I.vConsigneeNo, InvoiceDate, (i.VatAmount + i.Total),year(i.InvoiceDate) HAVING (i.VatAmount + i.Total) - (ISNULL(SUM(IVR.PaidAmount), 0) + ISNULL(SUM(IVR.AdvanceTax), 0) + ISNULL(SUM(IVR.Rejection), 0) + ISNULL(SUM(IVR.Discount), 0)) <> 0";

						String query = "select i.vInvoiceNo + '/' + i.vConsigneeNo as InvoiceNo, i.InvoiceDate, CONVERT (varchar(20), i.VatAmount + i.Total - ISNULL(SUM(ivr.PaidAmount), 0)) as TotalAmount,convert(varchar(20),((i.VatAmount + i.Total) - ISNULL(SUM(IVR.PaidAmount), 0) + ISNULL(SUM(IVR.AdvanceTax), 0) + ISNULL(SUM(IVR.Rejection), 0) + ISNULL(SUM(IVR.Discount), 0))) AS balance,0,0,0,0,convert(varchar(20),((i.VatAmount + i.Total) - ISNULL(SUM(IVR.PaidAmount), 0) + ISNULL(SUM(IVR.AdvanceTax), 0) + ISNULL(SUM(IVR.Rejection), 0) + ISNULL(SUM(IVR.Discount), 0))) AS balance,year(i.InvoiceDate)[Year]" +
						"from tbInvoiceInfo i left outer join tbInvoiceVoucherReceived ivr on i.vInvoiceNo = ivr.InvoiceNo and i.vConsigneeNo = ivr.ConsigneeNo and year(i.InvoiceDate) = YEAR(ivr.Date) " +
						"where i.Status in ('Paid','Unpaid','Partial') and i.ConsigneeLedger = '"+ cmbReceivedFrom.getValue().toString() +"'  GROUP BY I.vInvoiceNo, I.vConsigneeNo, InvoiceDate, (i.VatAmount + i.Total),year(i.InvoiceDate) HAVING (i.VatAmount + i.Total) - (ISNULL(SUM(IVR.PaidAmount), 0) + ISNULL(SUM(IVR.AdvanceTax), 0) + ISNULL(SUM(IVR.Rejection), 0) + ISNULL(SUM(IVR.Discount), 0)) <> 0";
						//
						ledgerRootPath();						

			
						previewEvent(query);

						totalAmount();
						txtBankName.focus();
					}
				}
				catch(Exception ex)
				{
					getParent().showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		componentInit(false);
		consigneeAdd();
		addAllComponent();
		tableInitialise();
		btnIni(true);
		this.addComponent(mainLayout);
		button.btnNew.focus();
	}

	private void refreshButtonEvent()
	{
		try
		{
			txtClear();
			componentInit(false);
			btnIni(true);
			consigneeAdd();
			if(title.equals("bankReceiptAgnstInvoice"))
			{
				depositAcAdd();
			}
		}
		catch(Exception ex)
		{
			showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void findButtonEvent()
	{
		try
		{

			findVoucher.setValue("");	
			System.out.println(title);
			System.out.println(this.getCaption().substring(0, 4).trim().toString()+""+1);
			if (this.getCaption().substring(0, 4).trim().toString().equals("BANK"))
				title = "bankReceiptAgnstInvoice";
			else
				title = "cashReceiptAgnstInvoice";
			
	//		System.out.println(title);
				
			Window win = new InvoiceLocalFind(sessionBean, title, txtBankName);			
			win.center();			
			this.getParent().addWindow(win);			
			win.setModal(true);
			win.setCloseShortcut(KeyCode.ESCAPE);
			win.addListener(new Window.CloseListener()
			{
				private static final long serialVersionUID = 1L;
				public void windowClose(CloseEvent e) 
				{
					if(findVoucher.getValue().toString().trim().length() > 0)     
//						System.out.println(findVoucher.getValue().toString());
//					System.out.println("M");
						findInitialise();
				}
			});
		}
		catch(Exception ex)
		{
			showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void findInitialise()
	{
		String query = "";
		
		Transaction tx = null;
	
		

		try
		{
			if (this.getCaption().substring(0, 4).trim().toString().equals("BANK"))
				title = "bankReceiptAgnstInvoice";
			else
				title = "cashReceiptAgnstInvoice";
//			System.out.println("iojoij "+findQuery);
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
			tx = session.beginTransaction();
			Iterator iter = session.createSQLQuery(findQuery).list().iterator();

			//	System.out.println("Y");
			if (iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				txtVoucherNo.setValue(element[0].toString().trim());				
				voucherDate.setValue((Date)element[1]);
				cmbReceivedFrom.setValue(element[2].toString());				
				if (title == "bankReceiptAgnstInvoice")
				{
					txtBankName.setValue(element[10].toString().trim());					
					txtBranchName.setValue(element[11].toString().trim());					
					cmbDepositAcNo.setValue(element[6].toString().trim());					
					txtCheqNo.setValue(element[8].toString().trim());					
					CheqDate.setValue((Date)element[9]);					
				}
//				iter.remove();
				//query = "SELECT invoiceNo,InvoiceDate,TotalAmount,sum(Balance) as DueBalance,sum(Pa) as PaidAmount,sum(IT) as IncomeTax,sum(Re) as Rejection,sum(d) as Discount, sum(Balance) - sum(Pa) - sum(IT) - sum(Re) - sum(d) as Balance,Year FROM funInvoiceVoucherReportShow  () where voucherNo in ('"+ findVoucher.getValue().toString().trim() +"','') and LedgerId = '"+ cmbReceivedFrom.getValue().toString() +"'  group by invoiceNo,InvoiceDate,TotalAmount,Year order by invoiceNo";
				//Closed by Sagar	query = "SELECT invoiceNo,InvoiceDate,TotalAmount,sum(Balance) as DueBalance,sum(Pa) as PaidAmount,sum(IT) as IncomeTax,sum(Re) as Rejection,sum(d) as Discount, sum(Balance) - sum(Pa) - sum(IT) - sum(Re) - sum(d) as Balance,Year FROM funInvoiceVoucherReportShow  ('"+sessionBean.getCompanyId()+"') where voucherNo = '"+ findVoucher.getValue().toString().trim() +"' group by invoiceNo,InvoiceDate,TotalAmount,Year order by invoiceNo";
				query = "SELECT invoiceNo,InvoiceDate,TotalAmount, TotalAmount    as DueBalance,sum(Pa) as PaidAmount,sum(IT) as IncomeTax,sum(Re) as Rejection,sum(d) as Discount,balance  as Balance,Year FROM funInvoiceVoucherReportShow('"+sessionBean.getCompanyId()+"') where voucherNo = '"+ findVoucher.getValue().toString().trim() +"' group by invoiceNo,InvoiceDate,TotalAmount,Year,balance order by invoiceNo";
				//	System.out.println(query);
				previewEvent(query);

				totalAmount();
			
			//	txtTotalPaid.setValue(frmt.format(Double.parseDouble(txttotalDue.getValue().toString())-Double.parseDouble(txtTotalbalance.getValue().toString())));
			//	table.setColumnFooter("Paid Amount", frmt.format(Double.parseDouble(txtTotalPaid.getValue().toString())));

				findVoucher.setValue("");
				tx.commit();
			}
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Warning 1:", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void voucherId()
	{
		Transaction tx = null;
		String query ="";		
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(voucherDate.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;
		
		query = "select '"+ voucherPrefix +"' + isnull(cast(max(cast((replace(Voucher_No, '"+ voucherPrefix +"',''))as int))+1 as varchar),1) from "+voucher+" where vouchertype like '"+ voucherType +"' and CompanyId = '"+ sessionBean.getCompanyId() +"'";
		Iterator iter = session.createSQLQuery(query).list().iterator();
		if (iter.hasNext())
		{
			txtVoucherNo.setValue(iter.next().toString());
		}
		iter.remove();
		query = "select ledger_id from tbledger where parent_id='A6' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
		iter = session.createSQLQuery(query).list().iterator();
		if (iter.hasNext())
		{
			cashLedgerId = iter.next().toString();
		}
	}

	public void ledgerRootPath()
	{
		if(cmbReceivedFrom.getValue() != null)
		{
			try
			{
				Transaction tx = null;
				String consigneeLedger = cmbReceivedFrom.getValue().toString().trim();
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String query = "select dbo.funLedgerRootPath ('"+ consigneeLedger +"', '"+ sessionBean.getCompanyId() +"')";				
				Iterator iter = session.createSQLQuery(query).list().iterator();
				if (iter.hasNext())
				{
					showNotification("Root Path:", iter.next().toString(), Notification.TYPE_TRAY_NOTIFICATION);
				}
			}
			catch(Exception ex)
			{
				showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
			}
		}
	}

	private void previewEvent(String query)
	{
		try
		{
			if (cmbReceivedFrom.getValue()!= null)
			{
//				if (dateCompare())
//				{
					tableClear();

					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					Transaction tx = session.beginTransaction();
					List list = session.createSQLQuery(query).list();	
					
//					System.out.println('K');
//					System.out.println(query);
					

					int i = 0;
					for(Iterator iter = list.iterator(); iter.hasNext();)
					{
						Object[] element = (Object[]) iter.next();
						if (i == invoiceNo.size())
							tableRowAdd(i);			

						invoiceNo.get(i).setValue(element[0].toString());
//					System.out.println('L');
//						System.out.println(element[1]);
						invoiceDate.get(i).setValue((Object)element[1]);						
						currentDue.get(i).setValue(frmt.format(Double.parseDouble(element[3].toString())));
//
//						if(!title.equals("journalAgnstInvoice"))
//						{
							paidAmount.get(i).setValue(frmt.format(Double.parseDouble((element[4].toString()))));						
							paidAmount.get(i).setEnabled(Double.parseDouble(element[4].toString())>0? true:false);	
							select.get(i).setValue(Double.parseDouble(element[4].toString())>0? true:false);
							paidAmount.get(i).setTextChangeEventMode(TextChangeEventMode.LAZY);
							paidAmount.get(i).setTextChangeTimeout(200);

							tableColumnAction(i);
//						}
//						else
//						{
//							aTax.get(i).setValue(frmt.format(element[5]));
//							rejection.get(i).setValue(frmt.format(element[6]));
//							discount.get(i).setValue(frmt.format(element[7]));
//							select.get(i).setValue(Double.parseDouble(element[5].toString()) > 0|| Double.parseDouble(element[6].toString()) > 0|| Double.parseDouble(element[7].toString())>0? true:false);
//							System.out.println("XY");
//							tableColumnAction(i);
//						}

						balance.get(i).setValue(frmt.format(Double.parseDouble(element[8].toString())));					


						if (Double.parseDouble(element[3].toString()) == Double.parseDouble(element[8].toString()))
							status.get(i).setValue("Unpaid");
						else
							status.get(i).setValue("Partial");

						i++;

					//	tableColumnAction(i);
					}
				}
//				else
//					showNotification("Error :","Please Check Your Date Range.",Notification.TYPE_ERROR_MESSAGE);
//			}
		}
		catch(Exception ex)
		{
			showNotification("Error :", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void saveEvent()
	{
		if (this.getCaption().substring(0, 4).trim().toString().equals("BANK"))
			title = "bankReceiptAgnstInvoice";
		else
			title = "cashReceiptAgnstInvoice";
		
		boolean flag = false;
		if (title.equals("bankReceiptAgnstInvoice"))
			flag = nullCheckBank();
		else
			flag = nullCheckOther();
		if(flag)
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do You Want To Save All Data.", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{						
						if (isUpdate==true)
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							if(sessionBean.isUpdateable())
								insertData();
							else
								showNotification("Waring 9","You Are Not Permitted For Update Data.", Notification.TYPE_WARNING_MESSAGE);
						}
						else
						{
							if(sessionBean.isSubmitable())
							{
								mb.buttonLayout.getComponent(0).setEnabled(false);
								voucherId();
								if (title.equals("bankReceiptAgnstInvoice"))
								{
									boolean check = DoubleEntryCheck();
									if(check==true)
									{
										insertData();
									}
								}
								else
									//voucherId();
									insertData();
							}
							else
								showNotification("Waring 10","You Are Not Permitted For Insert Data.", Notification.TYPE_WARNING_MESSAGE);
						}
					}
				}
			});
		}
	}

	private boolean nullCheckBank()
	{
//		if (!txtVoucherNo.getValue().toString().trim().isEmpty())
//		{
			if (!voucherDate.getValue().toString().trim().isEmpty())
			{
				if (cmbReceivedFrom.getValue() != null)
				{
					if (!txtBankName.getValue().toString().trim().isEmpty())
					{
						if (!txtBranchName.getValue().toString().trim().isEmpty())
						{
							if (!txtCheqNo.getValue().toString().trim().isEmpty())
							{
								if (!CheqDate.getValue().toString().trim().isEmpty())
								{
									if (cmbDepositAcNo.getValue() != null)
									{
										return true;
									}
									else
									{
										showNotification("Warning :", "Please Enter Deposit Acount Number.", Notification.TYPE_WARNING_MESSAGE);
										cmbDepositAcNo.focus();
										return false;
									}
								}
								else
								{
									showNotification("Warning :", "Please Enter Cheque Date.", Notification.TYPE_WARNING_MESSAGE);
									CheqDate.focus();
									return false;
								}
							}
							else
							{
								showNotification("Warning :", "Please Enter Cheque No.", Notification.TYPE_WARNING_MESSAGE);
								txtCheqNo.focus();
								return false;
							}
						}
						else
						{
							showNotification("Warning :", "Please Enter Bank Branch.", Notification.TYPE_WARNING_MESSAGE);
							txtBranchName.focus();
							return false;
						}
					}
					else
					{
						showNotification("Warning :", "Please Enter Bank Name.", Notification.TYPE_WARNING_MESSAGE);
						txtBankName.focus();
						return false;
					}
				}
				else
				{
					showNotification("Warning :", "Please Select Consignee.", Notification.TYPE_WARNING_MESSAGE);
					cmbReceivedFrom.focus();
					return false;
				}
			}
			else
			{
				showNotification("Warning :", "Please Enter Voucher Date.", Notification.TYPE_WARNING_MESSAGE);
				voucherDate.focus();
				return false;
			}
//		}
//		else
//		{
//			showNotification("Warning :", "Please Enter Voucher No.", Notification.TYPE_WARNING_MESSAGE);
//			return false;
//		}
	}

	private boolean nullCheckOther()
	{
//		if (!txtVoucherNo.getValue().toString().trim().isEmpty())
//		{
			if (!voucherDate.getValue().toString().trim().isEmpty())
			{
				if (cmbReceivedFrom.getValue() != null)
				{
					return true;
				}
				else
				{
					showNotification("Warning :", "Please Select Consignee.", Notification.TYPE_WARNING_MESSAGE);
					cmbReceivedFrom.focus();
					return false;
				}
			}
			else
			{
				showNotification("Warning :", "Please Enter Voucher Date.", Notification.TYPE_WARNING_MESSAGE);
				voucherDate.focus();
				return false;
			}
//		}
//		else
//		{
//			showNotification("Warning :", "Please Enter Voucher No.", Notification.TYPE_WARNING_MESSAGE);
//			return false;
//		}
	}

	private void insertData() 
	{
		totalAmount();
		String query = "";
		String voucherType = "";
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(voucherDate.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;
		String cheque =  "chequeDetails"+fsl;
		
		try
		{
			boolean flag = true;
			if(isUpdate)
				flag = updateData(session, tx);	

			session.createSQLQuery("Delete from "+voucher+" where voucher_No  = '"+ txtVoucherNo.getValue().toString()+"' and Companyid = '" +sessionBean.getCompanyId() +"'").executeUpdate();
			//	System.out.println(flag);
			if(flag)
			{
				for(int i = 0; i < select.size(); i++)
				{
					if (select.get(i).getValue().toString() == "true")
					{
						String invcons = invoiceNo.get(i).getValue().toString();
						String invNo = invcons.substring(0, invcons.indexOf("/"));
						String consNo = invcons.substring(invcons.indexOf("/")+1, invcons.length());

//						if(title.equals("journalAgnstInvoice"))
//						{
//							query ="insert into tbInvoiceVoucherReceived(VoucherNo,InvoiceNo,ConsigneeNo,Date,PaidAmount,AdvanceTax,Rejection,Discount,Balance, companyId)" +
//							"values('"+ txtVoucherNo.getValue().toString()+"', '"+ invNo +"', '"+ consNo +"', convert(date,'"+ invoiceDate.get(i).getValue().toString().trim()+"'), 0, '"+ aTax.get(i).getValue().toString() +"', '"+ rejection.get(i).getValue().toString() +"', '"+ discount.get(i).getValue().toString() +"', '"+ balance.get(i).getValue() +"', '"+ sessionBean.getCompanyId() +"')";
//						}
//						else
//						{

							query ="insert into tbInvoiceVoucherReceived(VoucherNo,InvoiceNo,ConsigneeNo,Date,PaidAmount,AdvanceTax,Rejection,Discount,Balance, companyId)" +
							"values('"+ txtVoucherNo.getValue().toString()+"', '"+ invNo +"', '"+ consNo +"', convert(date,'"+ invoiceDate.get(i).getValue().toString().trim()+"'), '"+ paidAmount.get(i).getValue().toString() +"', 0, 0, 0, '"+ balance.get(i).getValue() +"', '"+ sessionBean.getCompanyId() +"')";
							//System.out.println(query);
//						}
						session.createSQLQuery(query).executeUpdate();
						query = "";

						query = "update tbInvoiceInfo set Status = '"+ status.get(i).getValue().toString() +"' where vInvoiceNo = '"+ invNo +"' and vConsigneeNo = '"+ consNo +"' and InvoiceDate = convert(date,'"+invoiceDate.get(i).getValue().toString().trim()+"') AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
						session.createSQLQuery(query).executeUpdate();
						query = "";
					}
				}

				if(title.equals("bankReceiptAgnstInvoice"))
				{

					session.createSQLQuery("Delete from "+cheque+" where Cheque_No = '"+ txtCheqNo.getValue().toString() +"' and voucher_no = '" + txtVoucherNo.getValue().toString() + "' and companyId = '" + sessionBean.getCompanyId().toString() + "'").executeUpdate();

					query = "insert into "+cheque+" (Cheque_No,Cheque_Date,Voucher_No,Bank_Id,BankName,BranchName, companyId) " +
					"values('"+ txtCheqNo.getValue().toString() +"', '"+ dtfYMD.format(CheqDate.getValue())+"', '"+ txtVoucherNo.getValue().toString() +"', '"+ cmbDepositAcNo.getValue().toString() +"', '"+ txtBankName.getValue().toString() +"', '"+ txtBranchName.getValue().toString() +"', '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(query).executeUpdate();

					voucherType = "cbi";

					query = "insert into "+voucher+" (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag, companyId)" +
					"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(CheqDate.getValue()) +"', '"+ cmbReceivedFrom.getValue().toString() +"', 'Invoice', '0', '"+ txtTotalPaid.getValue().toString() +"', '"+ voucherType +"',0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(query).executeUpdate();

					query = "insert into "+voucher+" (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag, companyId)" +
					"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(CheqDate.getValue()) +"', '"+ cmbDepositAcNo.getValue().toString() +"', 'Invoice', '"+ txtTotalPaid.getValue().toString() +"', '0', '"+ voucherType +"',0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(query).executeUpdate();
				}

				if(title.equals("cashReceiptAgnstInvoice"))
				{
					voucherType = "cci";

					query = "insert into "+voucher+" (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag, companyId)" +
					"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(voucherDate.getValue()) +"', '"+ cmbReceivedFrom.getValue().toString() +"', 'Invoice', '0', '"+ txtTotalPaid.getValue().toString() +"', '"+ voucherType +"',0, '"+ sessionBean.getCompanyId() +"')";
					System.out.println(query);
					session.createSQLQuery(query).executeUpdate();


					query = "insert into "+voucher+" (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag, companyId)" +
					"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(voucherDate.getValue()) +"', '"+ cashLedgerId +"', 'Invoice', '"+ txtTotalPaid.getValue().toString() +"', '0', '"+ voucherType +"',0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(query).executeUpdate();
				}

//				if(title.equals("journalAgnstInvoice"))
//				{
//					voucherType = "jai";
//
//					double total = Double.parseDouble(txtTotalaTax.getValue().toString()) + Double.parseDouble(txtTotalrejection.getValue().toString()) + Double.parseDouble(txtTotaldiscount.getValue().toString());
//
//					query = "insert into vwVoucher (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag, companyId)" +
//					"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(CheqDate.getValue()) +"', '"+ cmbReceivedFrom.getValue().toString() +"', 'Invoice', '0', '"+ total +"', '"+ voucherType +"',0, '"+ sessionBean.getCompanyId() +"')";
//					session.createSQLQuery(query).executeUpdate();
//
//					query = "insert into vwVoucher (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag, companyId)" +
//					"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(CheqDate.getValue()) +"', 'AL3', 'Invoice', '"+ Double.parseDouble(txtTotalaTax.getValue().toString()) +"', '0', '"+ voucherType +"',0, '"+ sessionBean.getCompanyId() +"')";
//					session.createSQLQuery(query).executeUpdate();
//
//					query = "insert into vwVoucher (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag, companyId)" +
//					"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(CheqDate.getValue()) +"', 'EL1', 'Invoice', '"+ Double.parseDouble(txtTotalrejection.getValue().toString()) +"', '0', '"+ voucherType +"',0, '"+ sessionBean.getCompanyId() +"')";
//					session.createSQLQuery(query).executeUpdate();
//
//					query = "insert into vwVoucher (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag, companyId)" +
//					"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(CheqDate.getValue()) +"', 'EL2', 'Invoice', '"+ Double.parseDouble(txtTotaldiscount.getValue().toString()) +"', '0', '"+ voucherType +"',0, '"+ sessionBean.getCompanyId() +"')";
//					session.createSQLQuery(query).executeUpdate();
//				}

				tx.commit();
				showNotification("Information : ","Saved Successfully.", Notification.TYPE_HUMANIZED_MESSAGE);
			//	txtClear();
				componentInit(false);
				btnIni(true);
				button.btnEdit.setEnabled(false);
			}
			else
			{
				showNotification("Error : ","Cannot Save Successfully. Please Try Later.", Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
			//refreshButtonEvent();
		}
		catch(Exception ex)
		{
			showNotification("Error : ",ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private boolean updateData(Session session, Transaction tx)
	{

		try
		{
			//Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		//	Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(voucherDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequeDetails"+fsl;
			
			String query = "";
			query = "select sum(CrAmount) from "+voucher+" where Voucher_No = '"+ txtVoucherNo.getValue() +"' and DrAmount=0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			query = "insert tbDeleteUpdateVoucher(Voucher_No, Date, Ledger_Id, Narration, vouchertype, TransactionWith,UserName,DUDtime,TType, DrAmount, CrAmount, companyId) select voucher_no, date, Ledger_ID,  Narration, voucherType, TransactionWith, '"+ sessionBean.getUserName() +"', getdate(), 'Updated', '"+ txtTotalPaid.getValue() +"', '0','"+sessionBean.getUserIp()+"', companyId from "+voucher+" where Voucher_No='"+ txtVoucherNo.getValue().toString() + "' and DrAmount=0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			if(!title.equals("cashReceiptAgnstInvoice"))
			{
				query = "delete "+cheque+" where Voucher_No = '"+ txtVoucherNo.getValue().toString() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(query).executeUpdate();
				
				query = "insert into "+cheque+" (Cheque_No,Cheque_Date,Voucher_No,Bank_Id,BankName,BranchName, companyId) " +
				"values('"+ txtCheqNo.getValue().toString() +"', '"+ dtfYMD.format(CheqDate.getValue())+"', '"+ txtVoucherNo.getValue().toString() +"', '"+ cmbDepositAcNo.getValue().toString() +"', '"+ txtBankName.getValue().toString() +"', '"+ txtBranchName.getValue().toString() +"', '"+ sessionBean.getCompanyId() +"')";
				session.createSQLQuery(query).executeUpdate();
//				query = "update vwChequeDetails set cheque_no = '"+ txtCheqNo.getValue().toString() +"',Cheque_Date = '"+ dtfYMD.format(CheqDate.getValue()) +"',Bank_Id = '"+ cmbDepositAcNo.getValue().toString() +"',BankName = '"+ txtBankName.getValue().toString() +"',BranchName = '"+ txtBranchName.getValue().toString() +"', companyId = '"+ sessionBean.getCompanyId() +"' where voucher_no='"+ txtVoucherNo.getValue().toString() +"'";
//				session.createSQLQuery(query).executeUpdate();
			}

			query = "Update "+voucher+" set Date='"+ dtfYMD.format(voucherDate.getValue()) +"',Ledger_Id='"+ cmbReceivedFrom.getValue().toString() +"', Narration='Bank Received Against Invoice',CrAmount=0,DrAmount="+ txtTotalPaid.getValue().toString() +", companyId = '"+ sessionBean.getCompanyId() +"' where voucher_no='"+ txtVoucherNo.getValue().toString() +"' and Dramount>0  AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			query = "Update "+voucher+" set Date='"+ dtfYMD.format(voucherDate.getValue()) +"',Ledger_Id='"+ cmbReceivedFrom.getValue().toString() +"', Narration='Bank Received Against Invoice',CrAmount="+ txtTotalPaid.getValue().toString() +",DrAmount=0, companyId = '"+ sessionBean.getCompanyId() +"' where voucher_no='"+ txtVoucherNo.getValue().toString() +"' and Dramount>0  AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			query = "Delete tbInvoiceVoucherReceived where VoucherNo = '"+ txtVoucherNo.getValue().toString() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();
			


			isUpdate = false;
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}

	}

	private void deleteData()
	{

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(voucherDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequeDetails"+fsl;
			
			String query = "";
			query = "select sum(CrAmount) from "+voucher+" where Voucher_No = '"+ txtVoucherNo.getValue() +"' and DrAmount=0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			query = "insert tbDeleteUpdateVoucher(Voucher_No, Date, Ledger_Id, Narration, vouchertype, TransactionWith,UserName,DUDtime,TType, DrAmount, CrAmount, companyId) select voucher_no, date, Ledger_ID,  Narration, voucherType, TransactionWith, '"+ sessionBean.getUserName() +"', getdate(), 'Delete', '"+ txtTotalPaid.getValue() +"', '"+ iter.next() +"','"+sessionBean.getUserIp()+"', companyId from "+voucher+" where Voucher_No='"+ txtVoucherNo.getValue().toString() + "' and DrAmount=0  AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			query = "delete "+voucher+"  where Voucher_No='"+ txtVoucherNo.getValue().toString() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			query = "delete "+cheque+" where Voucher_No = '"+ txtVoucherNo.getValue().toString() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			query = "delete tbInvoiceVoucherReceived where VoucherNo = '"+ txtVoucherNo.getValue().toString() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			isUpdate = false;
			tx.commit();
			showNotification("Information :","Successfully Deleted.", Notification.TYPE_HUMANIZED_MESSAGE);
			txtClear();
			componentInit(false);
			btnIni(true);
		}
		catch(Exception ex)
		{
			showNotification("Error:",ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void addAllComponent()
	{
		lblVoucherNo.setWidth("95px");
		lblvoucherDate.setWidth("350px");
		lblvoucherDate.setStyleName("fright");
		lblReceivedFrom.setWidth("95px");
		lblBankName.setWidth("130px");
		lblBankName.setStyleName("fright");
		txtBankName.setWidth("210px");
		lblBranchName.setWidth("95px");
		txtBranchName.setWidth("200px");
		lblChequeNo.setWidth("75px");
		lblChequeNo.setStyleName("fright");
		lblCheqDate.setWidth("75px");
		lblCheqDate.setStyleName("fright");
		lblDepositACNo.setWidth("95px");
		txtVoucherNo.setWidth("135px");
		bottomLabel.setStyleName("fright");

		voucherDate.setWidth(comWidth);
		voucherDate.setValue(new java.util.Date());
		voucherDate.setResolution(PopupDateField.RESOLUTION_DAY);
		voucherDate.setDateFormat("dd-MM-yy");
		voucherDate.setInvalidAllowed(false);
		voucherDate.setImmediate(true);

		CheqDate.setWidth(comWidth);
		CheqDate.setValue(new java.util.Date());
		CheqDate.setResolution(PopupDateField.RESOLUTION_DAY);
		CheqDate.setDateFormat("dd-MM-yy");
		CheqDate.setInvalidAllowed(false);
		CheqDate.setImmediate(true);

		cmbReceivedFrom.setImmediate(true);
		cmbReceivedFrom.setWidth("250px");

		cmbDepositAcNo.setNullSelectionAllowed(true);
		cmbDepositAcNo.setImmediate(true);
		cmbDepositAcNo.setWidth("250px");		
		//consigneeAdd();

		horLayout1.addComponent(lblVoucherNo);
		horLayout1.addComponent(txtVoucherNo);

		horLayout1.addComponent(lblvoucherDate);
		horLayout1.addComponent(voucherDate);

		horLayout2.addComponent(lblReceivedFrom);
		horLayout2.addComponent(cmbReceivedFrom);
		frmLayout.addComponent(horLayout1);
		frmLayout.addComponent(horLayout2);
		if(title.equals("bankReceiptAgnstInvoice"))
		{
			horLayout2.addComponent(lblBankName);
			horLayout2.addComponent(txtBankName);
			horLayout3.addComponent(lblBranchName);
			horLayout3.addComponent(txtBranchName);
			horLayout3.addComponent(lblChequeNo);
			horLayout3.addComponent(txtCheqNo);
			horLayout3.addComponent(lblCheqDate);
			horLayout3.addComponent(CheqDate);
			horLayout4.addComponent(lblDepositACNo);
			horLayout4.addComponent(cmbDepositAcNo);			
			frmLayout.addComponent(horLayout3);
			frmLayout.addComponent(horLayout4);	
			depositAcAdd();
		}
		mainLayout.addComponent(frmLayout);
		mainLayout.addComponent(table);
		mainLayout.addComponent(bottomLayout);
		btnLayout.addComponent(button);		
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.MIDDLE_CENTER);

		table.setWidth("95%");
		table.setHeight("263px");
		table.addContainerProperty("", CheckBox.class, null);
		//		table.setColumnWidth("", 18);
		table.addContainerProperty("Invoice No", TextRead.class, null);
		table.addContainerProperty("Invoice Date", TextRead.class, null);
		table.addContainerProperty("Current Due", TextRead.class, null);

		bottomLayout.addComponent(bottomLabel);
		
//		if(title.equals("journalAgnstInvoice"))
//		{
		table.setColumnWidth("Invoice No", 90);
		table.setColumnWidth("Invoice Date", 70);
		table.setColumnWidth("Current Due", 100);
		table.setColumnWidth("Paid Amount", 100);
		table.setColumnWidth("Balance", 100);
//			txttotalDue.setWidth("105px");
//			txtTotalaTax.setWidth("55px");
//			txtTotalrejection.setWidth("85px");
//			txtTotaldiscount.setWidth("80px");
//			table.addContainerProperty("A.Tax", AmountCommaSeperator.class, null);
//			//			table.setColumnWidth("A.Tax", 60);		
//			table.addContainerProperty("Rejection", AmountCommaSeperator.class, null);
//			//			table.setColumnWidth("Rejection", 70);
//			table.addContainerProperty("Discount", AmountCommaSeperator.class, null);
//			//			table.setColumnWidth("Discount", 60);
//			bottomLayout.addComponent(txtTotalaTax);
//			bottomLayout.addComponent(txtTotalrejection);
//			bottomLayout.addComponent(txtTotaldiscount);
//			bottomLabel.setWidth("235px");
//		}
//		else
//		{
			//			table.setColumnWidth("Invoice No", 150);
			//			table.setColumnWidth("Invoice Date", 100);
			//			table.setColumnWidth("Current Due", 95);
			
			txttotalDue.setWidth("100px");
			txtTotalPaid.setWidth("100px");
			//txtTotalPaid.setHeight("22px");
			//txtTotalPaid.A
			table.addContainerProperty("Paid Amount", AmountCommaSeperator.class, null);
					table.setColumnWidth("Paid Amount", 95);
			
			bottomLayout.addComponent(txttotalDue);
			bottomLayout.addComponent(txtTotalPaid);
			bottomLayout.setComponentAlignment(txtTotalPaid, Alignment.BOTTOM_CENTER);
			bottomLabel.setWidth("240px");			
//		}
		txtTotalbalance.setWidth("100px");
		bottomLayout.addComponent(txtTotalbalance);
		bottomLayout.setSpacing(true);
		table.addContainerProperty("Balance", TextRead.class, null);
		//		table.setColumnWidth("Balance", 90);
		table.addContainerProperty("Status", TextRead.class, null);
		table.setFooterVisible(true);
		table.setColumnFooter("Invoice No", "Total :");
		//		table.setColumnWidth("Status", 70);

//		if(title.equals("journalAgnstInvoice"))
//			table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});
//		else
			table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_CENTER});

		table.setImmediate(true);

		mainLayout.setSpacing(true);
		Component ob[] = {voucherDate, cmbReceivedFrom, txtBankName, txtBranchName, txtCheqNo,CheqDate, cmbDepositAcNo, button.btnEdit, button.btnSave, button.btnRefresh};
		new FocusMoveByEnter(this,ob);

	}

	private void depositAcAdd()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		cmbDepositAcNo.removeAllItems();
		List<?> list = session.createSQLQuery("Select  ledger_Id, ledger_Name from  tbledger where substring(Create_From, 1, 2) in ('A7','L7') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') order by Ledger_Name").list();
		for(Iterator<?> iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[])iter.next();
			cmbDepositAcNo.addItem(element[0].toString());
			cmbDepositAcNo.setItemCaption(element[0].toString(), element[1].toString());
		}
	}

	private void consigneeAdd()
	{
		try
		{
			Transaction trans = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			cmbReceivedFrom.removeAllItems();
			trans = session.beginTransaction();
			System.out.println("select Ledger_Id, Ledger_Name from tbledger where create_from like 'A5%' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') order by Ledger_Name");
			List<?> list = session.createSQLQuery("select Ledger_Id, Ledger_Name from tbledger where create_from like 'A5%' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') order by Ledger_Name").list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[])iter.next();			
				cmbReceivedFrom.addItem(element[0].toString());
				cmbReceivedFrom.setItemCaption(element[0].toString(), element[1].toString());
			}
			cmbReceivedFrom.setNullSelectionAllowed(false);
		}
		catch(Exception ex)
		{
			showNotification(ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tableInitialise()
	{		
		for(int i=0;i<8;i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd(final int ans)
	{
		select.add(ans, new CheckBox());
		select.get(ans).setWidth("100%");
		invoiceNo.add(ans, new TextRead(""));
		invoiceNo.get(ans).setWidth("100%");
		invoiceDate.add(ans, new TextRead(""));
		invoiceDate.get(ans).setWidth("100%");
		currentDue.add(ans, new TextRead(1));
		currentDue.get(ans).setWidth("100%");

//		if(title.equals("journalAgnstInvoice"))
//		{
//			aTax.add(ans, new AmountCommaSeperator());
//			aTax.get(ans).setEnabled(false);
//			aTax.get(ans).setWidth("100%");
//			rejection.add(ans, new AmountCommaSeperator());
//			rejection.get(ans).setEnabled(false);
//			rejection.get(ans).setWidth("100%");		
//			discount.add(ans, new AmountCommaSeperator());
//			discount.get(ans).setEnabled(false);
//			discount.get(ans).setWidth("100%");
//		}
//		else
//		{
			paidAmount.add(ans, new AmountCommaSeperator());
			paidAmount.get(ans).setEnabled(false);
			paidAmount.get(ans).setWidth("100%");
//		}

		balance.add(ans, new TextRead(1));
		balance.get(ans).setWidth("100%");
		status.add(ans, new TextRead(""));
		status.get(ans).setWidth("100%");
		select.get(ans).setImmediate(true);

		select.get(ans).addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//System.out.println("X");
				tableColumnAction(ans);
				totalAmount();
			}
		});

//		if(title.equals("journalAgnstInvoice"))
//			table.addItem(new Object[]{select.get(ans),invoiceNo.get(ans), invoiceDate.get(ans), currentDue.get(ans), aTax.get(ans), rejection.get(ans), discount.get(ans), balance.get(ans), status.get(ans)},ans);
//		else
			table.addItem(new Object[]{select.get(ans),invoiceNo.get(ans), invoiceDate.get(ans), currentDue.get(ans),paidAmount.get(ans), balance.get(ans), status.get(ans)},ans);
	}

	private void tableColumnAction(final int r)
	{
//		if(title.equals("journalAgnstInvoice"))
//		{	
//			aTax.get(r).setEnabled(select.get(r).getValue().toString() == "true"?true: false);
//			rejection.get(r).setEnabled(select.get(r).getValue().toString() == "true"?true: false);
//			discount.get(r).setEnabled(select.get(r).getValue().toString() == "true"?true: false);
//			aTax.get(r).setImmediate(true);
//			aTax.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
//			aTax.get(r).setTextChangeTimeout(200);
//			rejection.get(r).setImmediate(true);
//			rejection.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
//			rejection.get(r).setTextChangeTimeout(200);
//			discount.get(r).setImmediate(true);
//			discount.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
//			discount.get(r).setTextChangeTimeout(200);
//			//System.out.println("XX");
//			
//
//
//			aTax.get(r).addListener(new TextChangeListener() 
//			{
//				@Override
//				public void textChange(TextChangeEvent event) 
//				{
//					
//					try
//					{
//							//System.out.println("XX");
//						double valtax = event.getText().trim().replace(",","").isEmpty() ? 0 : Double.parseDouble(event.getText().trim().replace(",", ""));
//						//double valtax = event.getText().trim().isEmpty() ? 0 : Double.parseDouble(event.getText().trim());
//						//System.out.println("XX1");
//						double valrejection = rejection.get(r).getValue().toString().isEmpty()? 0 : Double.parseDouble(rejection.get(r).getValue().toString());
//						//System.out.println("XX2");
//						double valdiscount = discount.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(discount.get(r).getValue().toString());
//						//System.out.println("XX3");
//						double currentdue = currentDue.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(currentDue.get(r).getValue().toString());
//						balance.get(r).setValue(frmt.format(currentdue - (valtax+valrejection+valdiscount)));
//						if(currentdue > (currentdue - (valtax+valrejection+valdiscount)))
//							status.get(r).setValue("Partial");
//						else
//							status.get(r).setValue("Unpaid");
//						totalAmountEvent(aTax.get(r),event, r);
//					//	totalAmount();						
//					
//			
//						aTax.get(r).focus();
//					}
//					catch(Exception ex)
//					{
//						showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
//					}
//				}
//			});
//
//			rejection.get(r).addListener(new TextChangeListener() 
//			{
//				@Override
//				public void textChange(TextChangeEvent event) 
//				{
//					// TODO Auto-generated method stub
//					try
//					{
//						double valtax = aTax.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(aTax.get(r).getValue().toString());
//						double valrejection = event.getText().trim().isEmpty() ? 0 : Double.parseDouble(event.getText().trim());
//						double valdiscount = discount.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(discount.get(r).getValue().toString());
//						double currentdue = currentDue.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(currentDue.get(r).getValue().toString());
//						balance.get(r).setValue(frmt.format(currentdue - (valtax+valrejection+valdiscount)));
//						if(currentdue > (currentdue - (valtax+valrejection+valdiscount)))
//							status.get(r).setValue("Partial");
//						else
//							status.get(r).setValue("Unpaid");
//					//	totalAmountEvent(rejection.get(r),event, r);
//					//	totalAmount();
//					//	aTax.get(r).focus();
//					}
//					catch(Exception ex)
//					{
//						showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
//					}
//				}
//			});
//
//			discount.get(r).addListener(new TextChangeListener() 
//			{
//				@Override
//				public void textChange(TextChangeEvent event) 
//				{
//					// TODO Auto-generated method stub
//					try
//					{
//						double valtax = aTax.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(aTax.get(r).getValue().toString());
//						double valrejection = rejection.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(rejection.get(r).getValue().toString());
//						double valdiscount = event.getText().trim().isEmpty() ? 0 : Double.parseDouble(event.getText().trim());
//						double currentdue = currentDue.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(currentDue.get(r).getValue().toString());
//						balance.get(r).setValue(frmt.format(currentdue - (valtax+valrejection+valdiscount)));
//						if(currentdue > (currentdue - (valtax+valrejection+valdiscount)))
//							status.get(r).setValue("Partial");
//						else
//							status.get(r).setValue("Unpaid");
//					//	totalAmountEvent(discount.get(r),event, r);
//					//	totalAmount();
//						
//			
//					}
//					catch(Exception ex)
//					{
//						showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
//					}
//				}
//			
//			});
//			//totalAmount();
//		}
//		else
//		{

			paidAmount.get(r).setImmediate(true);
			//System.out.println("KL");
			paidAmount.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
			paidAmount.get(r).setTextChangeTimeout(200);
			paidAmount.get(r).setEnabled(select.get(r).getValue().toString() == "true"?true: false);


			//paidAmount.get(r).setValue("100");

			//			paidAmount.get(r).addListener(new ValueChangeListener() {
			//				
			//				@Override
			//				public void valueChange(ValueChangeEvent event) {
			//					// TODO Auto-generated method stub
			//						System.out.println(event.getProperty());
			//				}
			//			});


			paidAmount.get(r).addListener(new TextChangeListener() 
			{
				@Override
				public void textChange(TextChangeEvent event) 
				{
					// TODO Auto-generated method stub
					//		System.out.println("KL");
					try
					{

						double paidamount = event.getText().trim().replace(",","").isEmpty() ? 0 : Double.parseDouble(event.getText().trim().replace(",", ""));

						double currentdue = (Double) (currentDue.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(currentDue.get(r).getValue().toString()));
						balance.get(r).setValue(frmt.format(currentdue - paidamount));
						//		System.out.println(currentDue);
						//		System.out.println(paidamount);
						if(currentdue > (currentdue - paidamount))
							status.get(r).setValue("Partial");
						else
							status.get(r).setValue("Unpaid");
					//	totalAmountEvent(paidAmount.get(r),event, r);
					//	totalAmount();
						//		System.out.println(balance.get(r).getValue().toString());

						paidAmount.get(r).focus();
			//			totalAmountEvent(paidAmount.get(r),event, r);
			//				totalAmount();
					}
					catch(Exception ex)
					{
						showNotification("Error ",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
					}
				}
			});
//		}
	}

	private void totalAmountEvent(AmountCommaSeperator field,TextChangeEvent event, int r)
	{
		double total = 0.00;
		if(title.equals("journalAgnstInvoice"))
		{
			//	double total = 0.00;
			double temp = 0.0;
//				if(aTax.get(r) == field)
//					{
			//System.out.println(aTax.get(r));
			for(int i = 0; i < select.size(); i++)
			{
				/*	if(select.get(i).getValue().toString() == "true")
					{
					//	total = aTax.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(aTax.get(i).getValue().toString());
						//System.out.println("A");
						//temp = Double.parseDouble(aTax.get(i).getValue().replace(",", "").toString());
					//	temp = i == r ?	event.getText().trim().isEmpty()? 0:Double.parseDouble(event.getText().trim()): aTax.get(i).getValue().toString().trim().isEmpty()?0:Double.parseDouble(aTax.get(i).getValue().toString());
						temp = i == r ?	event.getText().replace(",","").trim().isEmpty()? 0:Double.parseDouble(event.getText().replace(",","").trim().toString()): aTax.get(i).getValue().replace(",", "").toString().trim().isEmpty()?0:Double.parseDouble(aTax.get(i).getValue().replace(",","").toString());
						//temp = i == r ?	event.getText().replace(",","").trim().isEmpty()? 0:Double.parseDouble(event.getText().replace(",","").trim().toString()): aTax.get(i).getValue().replace(",", "").toString().trim().isEmpty()?0:Double.parseDouble(aTax.get(i).getValue().replace(",","").toString());
					System.out.println(total+" "+temp);
						total = total + temp;						
//					}
					}*/
				//temp=0.0;
				total = (Double.parseDouble(event.getText())+Double.parseDouble(aTax.get(i).getValue().toString()));
				if(select.get(i).getValue().equals(true))
				{
//					System.out.println(i+r+" hi "+total+" "+(Double.parseDouble(event.getText())+Double.parseDouble(aTax.get(i).getValue().toString())));
//				if (i!=r){	
					temp = Double.parseDouble(aTax.get(i).getValue().toString());
					total = total+temp;
//					System.out.println(total+" 1");
//				}
//					System.out.println(total+" 2");
				}
				System.out.println(total+" 2");
				//total = (Double.parseDouble(event.getText())+Double.parseDouble(aTax.get(i).getValue().toString()));
				txtTotalaTax.setValue(frmt.format(total));				
				table.setColumnFooter("A.Tax", frmt.format(total)+"");
				
//				table.setColumnFooter("Paid Amount", frmt.format(total)+"");
//				txtTotalPaid.setValue(frmt.format(total));	
			}
//					}
			total = 0.00;
			temp = 0.0;
			//	if(rejection.get(r) == field)
			//	{
			for(int i = 0; i < select.size(); i++)
			{
				if(select.get(i).getValue().toString() == "true")
				{

					temp = i == r ?	event.getText().replace(",","").trim().isEmpty()? 0:Double.parseDouble(event.getText().replace(",","").trim().toString()): rejection.get(i).getValue().replace(",", "").toString().trim().isEmpty()?0:Double.parseDouble(rejection.get(i).getValue().replace(",","").toString());
					//						temp = i == r ?	event.getText().trim().isEmpty()? 0:Double.parseDouble(event.getText().trim().toString()): rejection.get(i).getValue().toString().trim().isEmpty()?0:Double.parseDouble(rejection.get(i).getValue().toString());
					total = total + temp;
				}
			}
			txtTotalrejection.setValue(frmt.format(total));
			table.setColumnFooter("Rejection", total+"");
			//	}
			total = 0.00;
			temp = 0.0;
			//			if(discount.get(r) == field)
			//			{
			for(int i = 0; i < select.size(); i++)
			{	
				if(select.get(i).getValue().toString() == "true")
				{
					temp = i == r ?	event.getText().replace(",","").trim().isEmpty()? 0:Double.parseDouble(event.getText().replace(",","").trim().toString()): discount.get(i).getValue().replace(",", "").toString().trim().isEmpty()?0:Double.parseDouble(discount.get(i).getValue().replace(",","").toString());
					//	temp = i == r ?	event.getText().trim().isEmpty()? 0:Double.parseDouble(event.getText().trim().toString()): discount.get(i).getValue().toString().trim().isEmpty()?0:Double.parseDouble(discount.get(i).getValue().toString());
					//	total = total + temp;				
				}
			}
			txtTotaldiscount.setValue(frmt.format(total));
			table.setColumnFooter("Discount", total+"");
			//	}
			//			total = 0.00;
			//			temp = 0.0;
			//total = Double.parseDouble(txtTotalaTax.getValue().toString())+ Double.parseDouble(txtTotalrejection.getValue().toString())+ Double.parseDouble(txtTotaldiscount.getValue().toString()) + Double.parseDouble(txttotalDue.getValue().toString());
			//txtTotalbalance.setValue(total);
		}
		else
		{
			//			txtTotalPaid.setImmediate(true);
			double temp = 0.0;

			for(int i = 0; i < select.size(); i++)
			{
				if(select.get(i).getValue().toString() == "true")
				{	
					//					temp =  Double.parseDouble(event.getText().replace(",","").trim().toString());
					//					System.out.println(temp+"2");
					temp = i == r ?	event.getText().replace(",","").trim().isEmpty()? 0:Double.parseDouble(event.getText().replace(",","").trim().toString()): paidAmount.get(i).getValue().replace(",", "").toString().trim().isEmpty()?0:Double.parseDouble(paidAmount.get(i).getValue().replace(",","").toString());
					total = total + temp;
			//		System.out.println(total+"1");
					table.setColumnFooter("Paid Amount", frmt.format(total)+"");
					txtTotalPaid.setValue(frmt.format(total));	
				}
				//			}
				//			

			}

			total = 0.0;
		}
	}

	private void totalAmount()
	{		
		try
		{

			double total = 0.00;
			for(int i = 0; i < select.size(); i++)
				total = currentDue.get(i).getValue().toString() == "" ? total + 0: total + Double.parseDouble(currentDue.get(i).getValue().toString());

			txttotalDue.setValue(frmt.format(total));
			table.setColumnFooter("Current Due", frmt.format(total)+"");
			total = 0.00;		

			for(int i = 0; i < select.size(); i++)		
				total = paidAmount.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(paidAmount.get(i).getValue().toString());

			txtTotalPaid.setValue(frmt.format(total));			
			table.setColumnFooter("PAID AMOUNT", frmt.format(total)+"");
			
			total=0.00;
			//
			for(int i = 0; i < select.size(); i++)		
				total = balance.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(balance.get(i).getValue().toString());

			txtTotalbalance.setValue(frmt.format(total));			
			table.setColumnFooter("Balance", frmt.format(total)+"");
//
//			total = 0.00;
//			for(int i = 0; i < select.size(); i++)		
//				total = aTax.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(aTax.get(i).getValue().toString());
//
//			txtTotalaTax.setValue(frmt.format(total));			
//			table.setColumnFooter("A.Tax", frmt.format(total)+"");
			//			total = 0.00;	
			//			for(int i = 0; i < select.size(); i++)		
			//				total = aTax.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(aTax.get(i).getValue().toString());
			//
			//			txtTotalaTax.setValue(frmt.format(total));			
			//			table.setColumnFooter("A.Tax", frmt.format(total)+"");
			//			
			//			txtTotalaTax.setValue(frmt.format(total));				
			//			table.setColumnFooter("A.Tax", total+"");

			//			total = 0.00;
			//			for(int i = 0; i < select.size(); i++)		
			//				total = aTax.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(aTax.get(i).getValue().toString());
			//	//		txtTotalaTax.setValue(frmt.format(total));				
			//			table.setColumnFooter("A.Tax", total+"");
			//			txtTotalPaid.setValue(select.size());	
			//		for(int i = 0; i == select.size(); i++)
			//			total = Double.parseDouble(paidAmount.get(i).getValue().replace(",","").toString());  		
			//			total = paidAmount.get(i).getValue().replace(",","").toString() == "0.00"? total + 0 : total + Double.parseDouble(paidAmount.get(i).getValue().replace(",","").toString());
			//			{
			//				if(select.get(i).getValue().toString() == "true")
			//				{	
			//				total = paidAmount.get(i).getValue().replace(",","").toString() == "0"? total + 0 : total + Double.parseDouble(paidAmount.get(i).getValue().replace(",","").toString());
			//				}
			//			}
			//			//total = paidAmount.get(i).getValue().replace(",","").toString() == "" ? total + 0 : total + Double.parseDouble(paidAmount.get(i).getValue().replace(",","").toString());
			//			
			//
			//		System.out.println(total);
			//			txtTotalPaid.setValue(frmt.format(total));			
			//		table.setColumnFooter("Paid Amount", frmt.format(total)+"");
			//			
			//			//txtTotalPaid.setValue(frmt.format(total));
			//		total = 0.00;
			//			
		}
		catch(Exception ex)
		{
			showNotification("Error 1",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void txtClear()
	{
		txtVoucherNo.setValue("");
		cmbReceivedFrom.setValue(null);	
		txtBankName.setValue("");
		txtBranchName.setValue("");
		txtCheqNo.setValue("");
		cmbDepositAcNo.setValue(null);
		tableClear();
	}

	private void tableClear()
	{
		for(int i = 0; i < select.size(); i++)
		{
			select.get(i).setValue(false);
			invoiceNo.get(i).setValue("");
			invoiceDate.get(i).setValue("");
			currentDue.get(i).setValue("");
//			if(title.equals("journalAgnstInvoice"))
//			{
//				aTax.get(i).setValue("");
//				rejection.get(i).setValue("");
//				discount.get(i).setValue("");
//			}
//			else
				paidAmount.get(i).setValue("");
			balance.get(i).setValue("");
			status.get(i).setValue("");
		}

		txttotalDue.setValue("");
		txtTotalPaid.setValue("");
		txtTotalaTax.setValue("");
		txtTotalbalance.setValue("");
		txtTotaldiscount.setValue("");
		txtTotalrejection.setValue("");

		txtTotalbalance.setValue("0.00");			
		table.setColumnFooter("Balance", "0.00");

		table.setColumnFooter("Paid Amount", "0.00");
		txtTotalPaid.setValue("0.00");	

		txttotalDue.setValue("0.00");
		table.setColumnFooter("Current Due", "0.00");

	}

	private void componentInit(boolean t)
	{
		horLayout1.setEnabled(t);
		horLayout2.setEnabled(t);
		horLayout3.setEnabled(t);
		horLayout4.setEnabled(t);		
		table.setEnabled(t);
		bottomLayout.setEnabled(t);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		//button.btnRefresh.setEnabled(!t);		
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private boolean dateCompare()
	{		
		if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(voucherDate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
				&&
				(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(voucherDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			return true;
		else
			return false;
	}

	private boolean DoubleEntryCheck()
	{


		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(voucherDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequeDetails"+fsl;
			
			String q = "Select [Cheque_No] from "+cheque+" where [Cheque_No] = '"+ txtCheqNo.getValue().toString().trim()+"' and companyId = '" + sessionBean.getCompanyId()+ "'";
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

}
