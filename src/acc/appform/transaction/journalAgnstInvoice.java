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


public class journalAgnstInvoice extends Window
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
	private Label lblReceivedFrom = new Label("Party Name :");
	private ComboBox cmbReceivedFrom = new ComboBox();
	private TextRead txttotalDue = new TextRead(1);
	private TextRead txtTotalaTax = new TextRead(1);
	private TextRead txtTotalrejection = new TextRead(1);
	private TextRead txtTotaldiscount = new TextRead(1);
	private TextRead txtTotalbalance = new TextRead(1);
	private Label lblcostCentre = new Label("Cost Centre:");
	private ComboBox costCentre = new ComboBox();
	private Label bottomLabel = new Label("	  Total :");
	private Label bottomL = new Label("");
	private Label bottomL1 = new Label("");
	private Label bottomL2 = new Label("");
	private Label bottomL3 = new Label("");
	private Table table = new Table();
	private ArrayList<CheckBox> select = new ArrayList<CheckBox>();
	private ArrayList<TextRead> invoiceNo = new ArrayList<TextRead>();
	private ArrayList<TextRead> invoiceDate = new ArrayList<TextRead>();
	private ArrayList<TextRead> currentDue = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> aTax = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> rejection = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> discount = new ArrayList<AmountCommaSeperator>();
	ArrayList<Component> tbComp = new ArrayList<Component>();
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
	private boolean isUpdate = false,isFind=false;
	private NumberFormat frmt = new DecimalFormat("#0.00");

	int row = 8;
	String h="28px";
	String w="100px";
	TextField txtVoucherNoFind=new TextField();
	

	public journalAgnstInvoice(final SessionBean sessionBean)
	{
		this.title = title;
		this.sessionBean = sessionBean;		

		this.setCaption("JOURNAL AGAINST INVOICE :: "+sessionBean.getCompany());
		this.setWidth("940px");
		voucherPrefix = "JV-NO-";
		voucherType = "ja%";


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
					isFind=false;
					cmbReceivedFrom.focus();
					for(int a=0;a<invoiceNo.size();a++){
						select.get(a).setEnabled(true);
					}
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
						
						for(int a=0;a<invoiceNo.size();a++){
							//if(!(Boolean) select.get(a).getValue()){
								select.get(a).setEnabled(true);
								invoiceNo.get(a).setEnabled(false);
								invoiceDate.get(a).setEnabled(false);
								currentDue.get(a).setEnabled(false);
								aTax.get(a).setEnabled(false);
								rejection.get(a).setEnabled(false);
								discount.get(a).setEnabled(false);
								balance.get(a).setEnabled(false);
								status.get(a).setEnabled(false);
							//}
						}
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
								//								deleteData();
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
				for(int a=0;a<invoiceNo.size();a++){
					select.get(a).setEnabled(false);
				}
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

						/*String query = "select i.vInvoiceNo + '/' + i.vConsigneeNo as InvoiceNo, i.InvoiceDate, CONVERT (varchar(20), i.VatAmount + i.Total - ISNULL(SUM(ivr.PaidAmount), 0)) as TotalAmount,convert(varchar(20),((i.VatAmount + i.Total) - ISNULL(SUM(IVR.PaidAmount), 0) + ISNULL(SUM(IVR.AdvanceTax), 0) + ISNULL(SUM(IVR.Rejection), 0) + ISNULL(SUM(IVR.Discount), 0))) AS balance,0,0,0,0,convert(varchar(20),((i.VatAmount + i.Total) - ISNULL(SUM(IVR.PaidAmount), 0) + ISNULL(SUM(IVR.AdvanceTax), 0) + ISNULL(SUM(IVR.Rejection), 0) + ISNULL(SUM(IVR.Discount), 0))) AS balance,year(i.InvoiceDate)[Year]" +
						"from tbInvoiceInfo i left outer join tbInvoiceVoucherReceived ivr on i.vInvoiceNo = ivr.InvoiceNo and i.vConsigneeNo = ivr.ConsigneeNo and year(i.InvoiceDate) = YEAR(ivr.Date) " +
						"where i.Status in ('Paid','Unpaid','Partial') and i.ConsigneeLedger = '"+ cmbReceivedFrom.getValue().toString() +"' GROUP BY I.vInvoiceNo, I.vConsigneeNo, InvoiceDate, (i.VatAmount + i.Total),year(i.InvoiceDate) HAVING (i.VatAmount + i.Total) - (ISNULL(SUM(IVR.PaidAmount), 0) + ISNULL(SUM(IVR.AdvanceTax), 0) + ISNULL(SUM(IVR.Rejection), 0) + ISNULL(SUM(IVR.Discount), 0)) <> 0";*/

						String query="select vBillNo,dInvoiceDate,(mNetTotal-ReceivedAmount-ATax-Rejection-Discount)balance from( "+
								" select a.vBillNo,convert(date,a.dInvoiceDate,105)dInvoiceDate,a.mNetTotal, "+
								" (select isnull(SUM(mTotalAmount),0) from tbReceivedAgainstInvoiceDetails where vInvoiceNo=a.vBillNo)ReceivedAmount, "+
								" (select isnull(SUM(AdvanceTax),0) from tbJournalAgainstInvoice where vBillNo=a.vBillNo)ATax, "+
								" (select isnull(SUM(Rejection),0) from tbJournalAgainstInvoice where vBillNo=a.vBillNo)Rejection, "+
								" (select isnull(SUM(Discount),0) from tbJournalAgainstInvoice where vBillNo=a.vBillNo)Discount  "+
								" from tbSalesInvoiceInfo a /*left join tbReceivedAgainstInvoiceDetails c on a.vBillNo=c.vInvoiceNo "+
								" left join tbJournalAgainstInvoice d on a.vBillNo=d.vBillNo*/ where a.vPartyLedgerId='"+ cmbReceivedFrom.getValue().toString() +"' "+
								" ) a where (mNetTotal-ReceivedAmount-ATax-Rejection-Discount)>0";

						ledgerRootPath();						
						previewEvent(query);
						//totalAmount();

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
		focusMove();
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
			//System.out.println(title);
			title = "journalAgnstInvoice";
			Window win = new InvoiceLocalFind(sessionBean, title,txtVoucherNoFind);			
			win.center();			
			this.getParent().addWindow(win);			
			win.setModal(true);
			win.setCloseShortcut(KeyCode.ESCAPE);
			win.addListener(new Window.CloseListener()
			{
				private static final long serialVersionUID = 1L;
				public void windowClose(CloseEvent e) 
				{
					if(txtVoucherNoFind.getValue().toString().trim().length() > 0)            		
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
		isFind=true;
		String query = "";
		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
			tx = session.beginTransaction();
			findQuery = "select VoucherNo,vPartyLedger,vBillNo,date,costId,AdvanceTax,Rejection,Discount,paidAmount,balance " +
					"from tbJournalAgainstInvoice where VoucherNo='"+txtVoucherNoFind.getValue()+"'";
			Iterator iter = session.createSQLQuery(findQuery).list().iterator();
			while (iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				/*txtVoucherNo.setValue(element[0].toString().trim());				
				voucherDate.setValue((Date)element[1]);
				cmbReceivedFrom.setValue(element[2].toString());	
				costCentre.setValue(element[6].toString());

				iter.remove();
				//query = "SELECT invoiceNo,InvoiceDate,TotalAmount,sum(Balance) as DueBalance,sum(Pa) as PaidAmount,sum(IT) as IncomeTax,sum(Re) as Rejection,sum(d) as Discount, sum(Balance) - sum(Pa) - sum(IT) - sum(Re) - sum(d) as Balance,Year FROM funInvoiceVoucherReportShow  () where voucherNo in ('"+ findVoucher.getValue().toString().trim() +"','') and LedgerId = '"+ cmbReceivedFrom.getValue().toString() +"'  group by invoiceNo,InvoiceDate,TotalAmount,Year order by invoiceNo";
				//Closed by Sagar	query = "SELECT invoiceNo,InvoiceDate,TotalAmount,sum(Balance) as DueBalance,sum(Pa) as PaidAmount,sum(IT) as IncomeTax,sum(Re) as Rejection,sum(d) as Discount, sum(Balance) - sum(Pa) - sum(IT) - sum(Re) - sum(d) as Balance,Year FROM funInvoiceVoucherReportShow  ('"+sessionBean.getCompanyId()+"') where voucherNo = '"+ findVoucher.getValue().toString().trim() +"' group by invoiceNo,InvoiceDate,TotalAmount,Year order by invoiceNo";
				query = "select VoucherNo,vBillNo,AdvanceTax,Rejection,Discount from tbJournalAgainstInvoice where VoucherNo='"+txtVoucherNo.getValue()+"'";
				System.out.println(query);
				//previewEvent(query);

				//totalAmount();
				//totalAmountfind();*/
				txtVoucherNo.setValue(element[0]);
				cmbReceivedFrom.setValue(element[1]);
				costCentre.setValue(element[4]);
				int a=checkBillNo(element[2].toString());
				if(a!=-1){
					
					aTax.get(a).setValue(frmt.format(element[5]));
					rejection.get(a).setValue(frmt.format(element[6]));
					discount.get(a).setValue(frmt.format(element[7]));
					//select.get(a).setValue(true);
					//calcBalance(a);
					currentDue.get(a).setValue(frmt.format(element[8]));
					balance.get(a).setValue(frmt.format(element[9]));
					
					/*invoiceNo.get(a).setEnabled(true);
					invoiceDate.get(a).setEnabled(true);
					currentDue.get(a).setEnabled(true);
					aTax.get(a).setEnabled(true);
					rejection.get(a).setEnabled(true);
					discount.get(a).setEnabled(true);
					balance.get(a).setEnabled(true);
					status.get(a).setEnabled(true);*/
				}
				calcTotal();
			}
			findVoucher.setValue("");
			tx.commit();
		}
		catch(Exception ex)
		{
			showNotification("Warning 1:", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private int checkBillNo(String billNo){
		for(int a=0;a<invoiceNo.size();a++){
			if(billNo.equalsIgnoreCase(invoiceNo.get(a).getValue().toString())){
				return a;
			}
		}
		return -1;
	}
	/*private void totalAmountfind()
	{
		try
		{
			double total = 0.00;
			for(int i = 0; i < select.size(); i++)
			{
				if (select.get(i).getValue().toString() == "true")
					total = aTax.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(aTax.get(i).getValue().toString());
			}	
			txtTotalaTax.setValue(frmt.format(total));			
			table.setColumnFooter("A.Tax", frmt.format(total)+"");
			total = 0.00;
			//
			//System.out.println("K");
			for(int i = 0; i < select.size(); i++)
			{
				if (select.get(i).getValue().toString() == "true")
					total = rejection.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(rejection.get(i).getValue().toString());
			}	
			txtTotalrejection.setValue(frmt.format(total));			
			table.setColumnFooter("Rejection", frmt.format(total)+"");
			total = 0.00;

			for(int i = 0; i < select.size(); i++)
			{
				if (select.get(i).getValue().toString() == "true")
					total = discount.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(discount.get(i).getValue().toString());
			}
			txtTotaldiscount.setValue(frmt.format(total));			
			table.setColumnFooter("Discount", frmt.format(total)+"");
			total = 0.00;		

			//			for(int i = 0;i<select.size();i++)
			//			{
			//				select.get(i).setValue(false);	
			//			}
			//select.
		}
		catch(Exception ex)
		{
			showNotification("Warning 1:", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}*/



	private void voucherId()
	{
		Transaction tx = null;
		String query ="";		
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		int sl = 0;

		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(voucherDate.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;
		///
		Iterator iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE  CompanyId = '"+ sessionBean.getCompanyId() +"' and (vouchertype = 'jau' or vouchertype = 'jcv' or vouchertype = 'jai')").list().iterator();
		//System.out.println("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE vouchertype = 'jau' or vouchertype = 'jcv' or vouchertype = 'jai' and CompanyId = '"+ sessionBean.getCompanyId() +"'");
		try
		{
			if(iter.hasNext())
				sl = Integer.valueOf(iter.next().toString());
			System.out.println(sl);
		}
		catch(Exception exp)
		{

		}
		txtVoucherNo.setValue("JV-NO-"+sl);
		///
		//		query = "select '"+ voucherPrefix +"' + isnull(cast(max(cast((replace(Voucher_No, '"+ voucherPrefix +"',''))as int))+1 as varchar),1) from "+voucher+" where vouchertype = 'jau' or vouchertype = 'jcv' or vouchertype = 'jai' and CompanyId = '"+ sessionBean.getCompanyId() +"'";
		//		System.out.println(query);
		//		Iterator iter = session.createSQLQuery(query).list().iterator();
		//		if (iter.hasNext())
		//		{
		//			txtVoucherNo.setValue(iter.next().toString());
		//		}
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

				int i = 0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					//		System.out.println(invoiceNo.size());
					if (i == invoiceNo.size())
						tableRowAdd(i);			

					invoiceNo.get(i).setValue(element[0].toString());
					invoiceDate.get(i).setValue((Object)element[1]);						
					currentDue.get(i).setValue(frmt.format(Double.parseDouble(element[2].toString())));
					/*aTax.get(i).setValue(frmt.format(element[5]));
						rejection.get(i).setValue(frmt.format(element[6]));
						discount.get(i).setValue(frmt.format(element[7]));
						select.get(i).setValue(Double.parseDouble(element[5].toString()) > 0|| Double.parseDouble(element[6].toString()) > 0|| Double.parseDouble(element[7].toString())>0? true:false);
//						System.out.println("XY");
//						tableColumnAction(i);
						balance.get(i).setValue(frmt.format(Double.parseDouble(element[8].toString())));					

						if (Double.parseDouble(element[3].toString()) == Double.parseDouble(element[8].toString()))
							status.get(i).setValue("Unpaid");
						else
							status.get(i).setValue("Partial");*/

					i++;

					//						tableColumnAction(i);
				}
				//				}
				//				else
				//					showNotification("Error :","Please Check Your Date Range.",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			showNotification("Error :", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void saveEvent()
	{

		boolean flag = false;
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
							{

								insertData();
							}
							else
								showNotification("Waring 9","You Are Not Permitted For Update Data.", Notification.TYPE_WARNING_MESSAGE);
						}
						else
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							if(sessionBean.isSubmitable())
							{
								voucherId();
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



	private boolean nullCheckOther()
	{
		//		if (!txtVoucherNo.getValue().toString().trim().isEmpty())
		//		{
		if (!voucherDate.getValue().toString().trim().isEmpty())
		{
			if (cmbReceivedFrom.getValue() != null)
			{

				if (costCentre.getValue() != null)
				{	
					return true;
				}
				else
				{
					showNotification("Warning :", "Please Select Cost Center.", Notification.TYPE_WARNING_MESSAGE);
					costCentre.focus();
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

	private void insertData() 
	{
		//totalAmountfind();
		String query = "";
		String voucherType = "";
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(voucherDate.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;

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
						query ="insert into tbJournalAgainstInvoice(VoucherNo,vBillNo,vPartyLedger,Date,PaidAmount,AdvanceTax,Rejection,Discount,Balance, companyId,costId)" +
								"values('"+ txtVoucherNo.getValue().toString()+"', '"+invoiceNo.get(i).getValue()+"', " +
								"'"+ cmbReceivedFrom.getValue() +"', convert(date,'"+ invoiceDate.get(i).getValue().toString().trim()+"'), '"+currentDue.get(i).getValue()+"'," +
								" '"+ aTax.get(i).getValue().toString() +"', '"+ rejection.get(i).getValue().toString() +"', " +
								"'"+ discount.get(i).getValue().toString() +"', '"+ balance.get(i).getValue() +"', " +
								"'"+ sessionBean.getCompanyId() +"','"+costCentre.getValue()+"')";
						session.createSQLQuery(query).executeUpdate();
						query = "";

						//query = "update tbInvoiceInfo set Status = '"+ status.get(i).getValue().toString() +"' where vInvoiceNo = '"+ invNo +"' and vConsigneeNo = '"+ consNo +"' and InvoiceDate = convert(date,'"+invoiceDate.get(i).getValue().toString().trim()+"') AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
						//session.createSQLQuery(query).executeUpdate();
						//query = "";

					}
				}


				voucherType = "jai";

				double total = Double.parseDouble(txtTotalaTax.getValue().toString()) + Double.parseDouble(txtTotalrejection.getValue().toString()) + Double.parseDouble(txtTotaldiscount.getValue().toString());

				query = "insert into "+voucher+" (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag,costID, companyId,sourceForm)" +
						"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(voucherDate.getValue()) +"', '"+ cmbReceivedFrom.getValue().toString() +"', 'Journal against invoice', '0', '"+ total +"', '"+ voucherType +"',2,'"+ costCentre.getValue().toString() +"', '"+ sessionBean.getCompanyId() +"','journalAgainstInvoice')";
				session.createSQLQuery(query).executeUpdate();

				query = "insert into "+voucher+" (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag,costID, companyId,sourceForm)" +
						"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(voucherDate.getValue()) +"', 'AL429', 'Journal against invoice', '"+ Double.parseDouble(txtTotalaTax.getValue().toString()) +"', '0', '"+ voucherType +"',2,'"+ costCentre.getValue().toString() +"', '"+ sessionBean.getCompanyId() +"','journalAgainstInvoice')";
				session.createSQLQuery(query).executeUpdate();

				query = "insert into "+voucher+" (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag,costID, companyId,sourceForm)" +
						"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(voucherDate.getValue()) +"', 'EL303', 'Journal against invoice', '"+ Double.parseDouble(txtTotalrejection.getValue().toString()) +"', '0', '"+ voucherType +"',2,'"+ costCentre.getValue().toString() +"', '"+ sessionBean.getCompanyId() +"','journalAgainstInvoice')";
				session.createSQLQuery(query).executeUpdate();

				query = "insert into "+voucher+" (Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,auditapproveflag,costID, companyId,sourceForm)" +
						"values('"+ txtVoucherNo.getValue().toString() +"', '"+ dtfYMD.format(voucherDate.getValue()) +"', 'EL304', 'Journal against invoice', '"+ Double.parseDouble(txtTotaldiscount.getValue().toString()) +"', '0', '"+ voucherType +"',2,'"+ costCentre.getValue().toString() +"','"+ sessionBean.getCompanyId() +"','journalAgainstInvoice')";
				session.createSQLQuery(query).executeUpdate();


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
			//	Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			//	Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(voucherDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			String query = "";
			query = "select sum(CrAmount) from "+voucher+" where Voucher_No = '"+ txtVoucherNo.getValue() +"' and DrAmount=0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			query = "insert tbDeleteUpdateVoucher(Voucher_No, Date, Ledger_Id, Narration, vouchertype, TransactionWith,UserName,DUDtime,TType, DrAmount, CrAmount, userIp, companyId) select voucher_no, date, Ledger_ID,  Narration, voucherType, TransactionWith, '"+ sessionBean.getUserName() +"', getdate(), 'Updated', '"+ txtTotalaTax.getValue() +"', '0', '"+ sessionBean.getUserIp() +"', companyId from "+voucher+" where Voucher_No='"+ txtVoucherNo.getValue().toString() + "' and DrAmount=0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			query = "insert tbDeleteUpdateVoucher(Voucher_No, Date, Ledger_Id, Narration, vouchertype, TransactionWith,UserName,DUDtime,TType, DrAmount, CrAmount, userIp, companyId) select voucher_no, date, Ledger_ID,  Narration, voucherType, TransactionWith, '"+ sessionBean.getUserName() +"', getdate(), 'Updated', '"+ txtTotalrejection.getValue() +"', '0', '"+ sessionBean.getUserIp() +"', companyId from "+voucher+" where Voucher_No='"+ txtVoucherNo.getValue().toString() + "' and DrAmount=0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			query = "insert tbDeleteUpdateVoucher(Voucher_No, Date, Ledger_Id, Narration, vouchertype, TransactionWith,UserName,DUDtime,TType, DrAmount, CrAmount, userIp, companyId) select voucher_no, date, Ledger_ID,  Narration, voucherType, TransactionWith, '"+ sessionBean.getUserName() +"', getdate(), 'Updated', '"+ txtTotaldiscount.getValue() +"', '0', '"+ sessionBean.getUserIp() +"', companyId from "+voucher+" where Voucher_No='"+ txtVoucherNo.getValue().toString() + "' and DrAmount=0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();


			//			query = "Update vwVoucher set Date='"+ dtfYMD.format(voucherDate.getValue()) +"',Ledger_Id='"+ cmbReceivedFrom.getValue().toString() +"', Narration='Bank Received Against Invoice',CrAmount=0,DrAmount="+ txtTotalPaid.getValue().toString() +", companyId = '"+ sessionBean.getCompanyId() +"' where voucher_no='"+ txtVoucherNo.getValue().toString() +"' and Dramount>0  AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			//			session.createSQLQuery(query).executeUpdate();
			//
			//			query = "Update vwVoucher set Date='"+ dtfYMD.format(voucherDate.getValue()) +"',Ledger_Id='"+ cmbReceivedFrom.getValue().toString() +"', Narration='Bank Received Against Invoice',CrAmount="+ txtTotalPaid.getValue().toString() +",DrAmount=0, companyId = '"+ sessionBean.getCompanyId() +"' where voucher_no='"+ txtVoucherNo.getValue().toString() +"' and Dramount>0  AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			//			session.createSQLQuery(query).executeUpdate();

			query = "Delete tbJournalAgainstInvoice where VoucherNo = '"+ txtVoucherNo.getValue().toString() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
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

			String query = "";
			query = "select sum(CrAmount) from "+voucher+" where Voucher_No = '"+ txtVoucherNo.getValue() +"' and DrAmount=0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			//			query = "insert tbDeleteUpdateVoucher(Voucher_No, Date, Ledger_Id, Narration, vouchertype, TransactionWith,UserName,DUDtime,TType, DrAmount, CrAmount, companyId) select voucher_no, date, Ledger_ID,  Narration, voucherType, TransactionWith, '"+ sessionBean.getUserName() +"', getdate(), 'Delete', '"+ txtTotalPaid.getValue() +"', '"+ iter.next() +"', companyId from vwVoucher where Voucher_No='"+ txtVoucherNo.getValue().toString() + "' and DrAmount=0  AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			//			session.createSQLQuery(query).executeUpdate();

			query = "delete "+voucher+"  where Voucher_No='"+ txtVoucherNo.getValue().toString() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(query).executeUpdate();

			//			query = "delete vwChequeDetails where Voucher_No = '"+ txtVoucherNo.getValue().toString() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			//			session.createSQLQuery(query).executeUpdate();

			query = "delete tbJournalAgainstInvoice where VoucherNo = '"+ txtVoucherNo.getValue().toString() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
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
		//		lblBankName.setWidth("130px");
		//		lblBankName.setStyleName("fright");
		//		txtBankName.setWidth("210px");
		//		lblBranchName.setWidth("95px");
		//		txtBranchName.setWidth("200px");
		//		lblChequeNo.setWidth("75px");
		//		lblChequeNo.setStyleName("fright");
		//		lblCheqDate.setWidth("75px");
		//		lblCheqDate.setStyleName("fright");
		//		lblDepositACNo.setWidth("95px");
		txtVoucherNo.setWidth("135px");
		bottomLabel.setStyleName("fright");

		voucherDate.setWidth(comWidth);
		voucherDate.setValue(new java.util.Date());
		voucherDate.setResolution(PopupDateField.RESOLUTION_DAY);
		voucherDate.setDateFormat("dd-MM-yy");
		voucherDate.setInvalidAllowed(false);
		voucherDate.setImmediate(true);

		//		CheqDate.setWidth(comWidth);
		//		CheqDate.setValue(new java.util.Date());
		//		CheqDate.setResolution(PopupDateField.RESOLUTION_DAY);
		//		CheqDate.setDateFormat("dd-MM-yy");
		//		CheqDate.setInvalidAllowed(false);
		//		CheqDate.setImmediate(true);

		cmbReceivedFrom.setImmediate(true);
		cmbReceivedFrom.setWidth("250px");
		costCentre.setImmediate(true);
		costCentre.setWidth("243px");


		//		cmbDepositAcNo.setNullSelectionAllowed(true);
		//		cmbDepositAcNo.setImmediate(true);
		//		cmbDepositAcNo.setWidth("250px");		
		//consigneeAdd();

		horLayout1.addComponent(lblVoucherNo);
		horLayout1.addComponent(txtVoucherNo);

		horLayout1.addComponent(lblvoucherDate);
		horLayout1.addComponent(voucherDate);

		horLayout2.addComponent(lblReceivedFrom);
		horLayout2.addComponent(cmbReceivedFrom);
		horLayout2.addComponent(lblcostCentre);
		horLayout2.addComponent(costCentre);
		horLayout2.setSpacing(true);
		frmLayout.addComponent(horLayout1);
		frmLayout.addComponent(horLayout2);

		mainLayout.addComponent(frmLayout);
		mainLayout.addComponent(table);
		mainLayout.addComponent(bottomLayout);
		btnLayout.addComponent(button);		
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.MIDDLE_CENTER);

		table.setWidth("100%");
		table.setHeight("340px");
		table.addContainerProperty("", CheckBox.class, null);
		//		table.setColumnWidth("", 18);
		table.addContainerProperty("Invoice No", TextRead.class, null);
		table.setColumnWidth("Invoice No", 140);
		table.addContainerProperty("Invoice Date", TextRead.class, null);
		table.setColumnWidth("Invoice Date", 70);
		table.addContainerProperty("Current Due", TextRead.class, null);
		table.setColumnWidth("Current Due", 100);


		bottomLabel.setWidth("245px");
		bottomLayout.addComponent(bottomLabel);
		//		bottomLayout.addComponent(txttotalDue);
		//		bottomLayout.addComponent(bottomL);

		txttotalDue.setWidth("105px");
		txtTotalaTax.setWidth("105px");
		txtTotalrejection.setWidth("105px");
		txtTotaldiscount.setWidth("105px");
		txtTotalbalance.setWidth("105px");

		table.addContainerProperty("A.Tax", AmountCommaSeperator.class, null);
		table.setColumnWidth("A.Tax", 100);		
		table.addContainerProperty("Rejection", AmountCommaSeperator.class, null);
		table.setColumnWidth("Rejection", 100);
		table.addContainerProperty("Discount", AmountCommaSeperator.class, null);
		table.setColumnWidth("Discount", 100);

		bottomLayout.addComponent(txttotalDue);
		//bottomLayout.addComponent(bottomL1);
		bottomLayout.addComponent(txtTotalaTax);
		//	bottomLayout.addComponent(bottomL1);
		bottomLayout.addComponent(txtTotalrejection);
		//	bottomLayout.addComponent(bottomL2);
		bottomLayout.addComponent(txtTotaldiscount);
		bottomLayout.addComponent(txtTotalbalance);


		//txtTotalbalance.setWidth("90px");
		//	bottomLayout.addComponent(bottomL3);

		bottomLayout.setSpacing(true);
		table.addContainerProperty("Balance", TextRead.class, null);
		//		table.setColumnWidth("Balance", 90);
		table.addContainerProperty("Status", TextRead.class, null);
		table.setFooterVisible(true);
		//table.setColumnFooter("Invoice Date", "Total :");
		//		table.setColumnWidth("Status", 70);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_CENTER});

		table.setImmediate(true);
		costCentreIni();
		mainLayout.setSpacing(true);
		//		Component ob[] = {voucherDate, cmbReceivedFrom,costCentre, button.btnUpdate, button.btnSave, button.btnRefresh};
		//		new FocusMoveByEnter(this,ob);


	}

	private void focusMove()
	{
		tbComp.add(voucherDate);
		tbComp.add(cmbReceivedFrom);
		tbComp.add(costCentre);
		//		tbComp.add(voucherDate);
		//		tbComp.add(voucherDate);


		for(int i=0;i<select.size();i++)
		{
			tbComp.add(aTax.get(i));
			tbComp.add(rejection.get(i));
			tbComp.add(discount.get(i));
		}
		new FocusMoveByEnter(this,tbComp);
	}

	private void consigneeAdd()
	{
		try
		{
			Transaction trans = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			cmbReceivedFrom.removeAllItems();
			trans = session.beginTransaction();
			System.out.println("select Ledger_Id, Ledger_Name from tbledger where create_from like 'A6%' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') order by Ledger_Name");
			List<?> list = session.createSQLQuery("select Ledger_Id, Ledger_Name from tbledger where create_from like 'A6%' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') order by Ledger_Name").list();
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
		for(int i=0;i<10;i++)
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
		aTax.add(ans, new AmountCommaSeperator());
		aTax.get(ans).setEnabled(false);
		aTax.get(ans).setWidth("100%");
		rejection.add(ans, new AmountCommaSeperator());
		rejection.get(ans).setEnabled(false);
		rejection.get(ans).setWidth("100%");		
		discount.add(ans, new AmountCommaSeperator());
		discount.get(ans).setEnabled(false);
		discount.get(ans).setWidth("100%");
		//		}
		//		else
		//		{
		//			paidAmount.add(ans, new AmountCommaSeperator());
		//			paidAmount.get(ans).setEnabled(false);
		//			paidAmount.get(ans).setWidth("100%");
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
				//totalAmountfind();
				calcTotal();
				tableColumnAction(ans);
			}
		});


		table.addItem(new Object[]{select.get(ans),invoiceNo.get(ans), invoiceDate.get(ans), currentDue.get(ans), aTax.get(ans), rejection.get(ans), discount.get(ans), balance.get(ans), status.get(ans)},ans);

	}
	private void calcBalance(int r){
		double valtax = aTax.get(r).getValue().toString().isEmpty()? 0 : Double.parseDouble(aTax.get(r).getValue().toString());
		double valrejection = rejection.get(r).getValue().toString().isEmpty()? 0 : Double.parseDouble(rejection.get(r).getValue().toString());
		double valdiscount = discount.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(discount.get(r).getValue().toString());
		double currentdue = currentDue.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(currentDue.get(r).getValue().toString());
		balance.get(r).setValue(frmt.format(currentdue - (valtax+valrejection+valdiscount)));
	}
	private void tableColumnAction(final int r)
	{

		aTax.get(r).setEnabled(select.get(r).getValue().toString() == "true"?true: false);
		rejection.get(r).setEnabled(select.get(r).getValue().toString() == "true"?true: false);
		discount.get(r).setEnabled(select.get(r).getValue().toString() == "true"?true: false);
		aTax.get(r).setImmediate(true);
		aTax.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
		aTax.get(r).setTextChangeTimeout(200);
		rejection.get(r).setImmediate(true);
		rejection.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
		rejection.get(r).setTextChangeTimeout(200);
		discount.get(r).setImmediate(true);
		discount.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
		discount.get(r).setTextChangeTimeout(200);
		//System.out.println("XX");



		aTax.get(r).addListener(new TextChangeListener() 
		{
			@Override
			public void textChange(TextChangeEvent event) 
			{

				try
				{
					double valtax = event.getText().trim().replace(",","").isEmpty() ? 0 : Double.parseDouble(event.getText().trim().replace(",", ""));
					double valrejection = rejection.get(r).getValue().toString().isEmpty()? 0 : Double.parseDouble(rejection.get(r).getValue().toString());
					double valdiscount = discount.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(discount.get(r).getValue().toString());
					double currentdue = currentDue.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(currentDue.get(r).getValue().toString());
					balance.get(r).setValue(frmt.format(currentdue - (valtax+valrejection+valdiscount)));

					if(currentdue > (valtax+valrejection+valdiscount))
						status.get(r).setValue("Partial");
					else if ((valtax+valrejection+valdiscount)== currentdue)
						status.get(r).setValue("Paid");
					else
						status.get(r).setValue("Unpaid");

					aTax.get(r).focus();
					//						totalAmountEvent(aTax.get(r),event, r);
					//						totalAmount();						



				}
				catch(Exception ex)
				{
					showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		rejection.get(r).addListener(new TextChangeListener() 
		{
			@Override
			public void textChange(TextChangeEvent event) 
			{
				// TODO Auto-generated method stub
				try
				{
					double valtax = aTax.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(aTax.get(r).getValue().toString());
					double valrejection = event.getText().trim().isEmpty() ? 0 : Double.parseDouble(event.getText().trim().replace(",",""));
					double valdiscount = discount.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(discount.get(r).getValue().toString());
					double currentdue = currentDue.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(currentDue.get(r).getValue().toString());
					balance.get(r).setValue(frmt.format(currentdue - (valtax+valrejection+valdiscount)));

					if(currentdue > (valtax+valrejection+valdiscount))
						status.get(r).setValue("Partial");
					else if ((valtax+valrejection+valdiscount)== currentdue)
						status.get(r).setValue("Paid");
					else
						status.get(r).setValue("Unpaid");

					rejection.get(r).focus();
					//						totalAmountEvent(rejection.get(r),event, r);
					//						totalAmount();						



				}
				catch(Exception ex)
				{
					showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		discount.get(r).addListener(new TextChangeListener() 
		{
			@Override
			public void textChange(TextChangeEvent event) 
			{
				// TODO Auto-generated method stub
				try
				{
					double valtax = aTax.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(aTax.get(r).getValue().toString());
					double valrejection = rejection.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(rejection.get(r).getValue().toString());
					double valdiscount = event.getText().trim().isEmpty() ? 0 : Double.parseDouble(event.getText().trim().replace(",", ""));
					double currentdue = currentDue.get(r).getValue().toString().isEmpty() ? 0 : Double.parseDouble(currentDue.get(r).getValue().toString());
					balance.get(r).setValue(frmt.format(currentdue - (valtax+valrejection+valdiscount)));

					if(currentdue > (valtax+valrejection+valdiscount))
						status.get(r).setValue("Partial");
					else if ((valtax+valrejection+valdiscount)== currentdue)
						status.get(r).setValue("Paid");
					else
						status.get(r).setValue("Unpaid");

					discount.get(r).focus();
					//						totalAmountEvent(discount.get(r),event, r);
					//						totalAmount();						

				}
				catch(Exception ex)
				{
					showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}

		});
		aTax.get(r).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				calcTotal();
				calcBalance(r);
				//showNotification("Hello");
			}
		});
		rejection.get(r).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				calcTotal();
				calcBalance(r);
				//showNotification("Hello");
			}
		});
		discount.get(r).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				calcTotal();
				calcBalance(r);
				//showNotification("Hello");
			}
		});
		//totalAmount();
	}
	private void calcTotal(){
		double tax=0.0,reject=0.0,disc=0.0,bal=0.0,total=0.0;
		for(int a=0;a<aTax.size();a++){
			tax=tax+Double.parseDouble(aTax.get(a).getValue().toString().isEmpty()?"0":aTax.get(a).getValue().toString());
			reject=reject+Double.parseDouble(rejection.get(a).getValue().toString().isEmpty()?"0":rejection.get(a).getValue().toString());
			disc=disc+Double.parseDouble(discount.get(a).getValue().toString().isEmpty()?"0":discount.get(a).getValue().toString());
			bal=bal+Double.parseDouble(balance.get(a).getValue().toString().isEmpty()?"0":balance.get(a).getValue().toString());
			total=total+Double.parseDouble(currentDue.get(a).getValue().toString().isEmpty()?"0":currentDue.get(a).getValue().toString());
		}
		txtTotalaTax.setValue(frmt.format(tax));
		txtTotalrejection.setValue(frmt.format(reject));
		txtTotaldiscount.setValue(frmt.format(disc));
		txtTotalbalance.setValue(frmt.format(bal));
		txttotalDue.setValue(frmt.format(total));
	}
	/*private void totalAmountEvent(AmountCommaSeperator field,TextChangeEvent event, int r)
	{
		double total = 0.00;
		double temp = 0.0;

		if(aTax.get(r) == field)
		{
			for(int i = 0; i < select.size(); i++)
			{
				if(select.get(i).getValue().toString() == "true")
				{

					temp = i == r ?	event.getText().replace(",","").trim().isEmpty()? 0:Double.parseDouble(event.getText().replace(",","").trim().toString()): aTax.get(i).getValue().replace(",", "").toString().trim().isEmpty()?0:Double.parseDouble(aTax.get(i).getValue().replace(",","").toString());
					total = total + temp;						
				}					
			}

			txtTotalaTax.setValue(frmt.format(total));				
			table.setColumnFooter("A.Tax", frmt.format(total)+"");
		}
		total = 0.00;
		temp = 0.0;

		if(rejection.get(r) == field)
		{
			for(int i = 0; i < select.size(); i++)
			{
				if(select.get(i).getValue().toString() == "true")
				{
					temp = i == r ?	event.getText().replace(",","").trim().isEmpty()? 0:Double.parseDouble(event.getText().replace(",","").trim().toString()): rejection.get(i).getValue().replace(",", "").toString().trim().isEmpty()?0:Double.parseDouble(rejection.get(i).getValue().replace(",","").toString());

					total = total + temp;
				}
			}
			txtTotalrejection.setValue(frmt.format(total));
			table.setColumnFooter("Rejection", frmt.format(total)+"");
		}
		total = 0.00;
		temp = 0.0;

		if(discount.get(r) == field)
		{
			for(int i = 0; i < select.size(); i++)
			{	
				if(select.get(i).getValue().toString() == "true")
				{
					temp = i == r ?	event.getText().replace(",","").trim().isEmpty()? 0:Double.parseDouble(event.getText().replace(",","").trim().toString()): discount.get(i).getValue().replace(",", "").toString().trim().isEmpty()?0:Double.parseDouble(discount.get(i).getValue().replace(",","").toString());
					total = total + temp;	
				}
			}

			txtTotaldiscount.setValue(frmt.format(total));
			table.setColumnFooter("Discount", frmt.format(total)+"");			
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
			//
			//			for(int i = 0; i < select.size(); i++)		
			//			total = aTax.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(aTax.get(i).getValue().toString());
			//			
			//			txtTotalaTax.setValue(frmt.format(total));			
			//			table.setColumnFooter("A.Tax", frmt.format(total)+"");
			//			total = 0.00;
			//			//
			//			
			//			for(int i = 0; i < select.size(); i++)		
			//				total = rejection.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(rejection.get(i).getValue().toString());
			//				
			//				txtTotalrejection.setValue(frmt.format(total));			
			//				table.setColumnFooter("Rejection", frmt.format(total)+"");
			//				total = 0.00;
			//				
			//				for(int i = 0; i < select.size(); i++)		
			//					total = discount.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(rejection.get(i).getValue().toString());
			//					
			//					txtTotaldiscount.setValue(frmt.format(total));			
			//					table.setColumnFooter("Discount", frmt.format(total)+"");
			//					total = 0.00;
			//			//
			for(int i = 0; i < select.size(); i++)		
				total = balance.get(i).getValue() == "" ? total + 0 : total + Double.parseDouble(balance.get(i).getValue().toString());

			txtTotalbalance.setValue(frmt.format(total));			
			table.setColumnFooter("Balance", frmt.format(total)+"");

		}
		catch(Exception ex)
		{
			showNotification("Error 1",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}*/

	private void txtClear()
	{
		txtVoucherNo.setValue("");
		cmbReceivedFrom.setValue(null);
		costCentre.setValue(null);	

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

			aTax.get(i).setValue("");
			rejection.get(i).setValue("");
			discount.get(i).setValue("");
			balance.get(i).setValue("");
			status.get(i).setValue("");

		}

		txttotalDue.setValue("0.00");

		txtTotalaTax.setValue("0.00");
		txtTotalbalance.setValue("0.00");
		txtTotaldiscount.setValue("0.00");
		txtTotalrejection.setValue("0.00");

		txtTotalbalance.setValue("0.00");		
		/*table.setColumnFooter("A.Tax", "0.00");
		table.setColumnFooter("Rejection", "0.00");
		table.setColumnFooter("Discount", "0.00");
		table.setColumnFooter("Balance", "0.00");
		table.setColumnFooter("Paid Amount", "0.00");*/

		txttotalDue.setValue("0.00");
		//table.setColumnFooter("Current Due", "0.00");

	}

	private void componentInit(boolean t)
	{
		horLayout1.setEnabled(t);
		horLayout2.setEnabled(t);
		horLayout3.setEnabled(t);
		horLayout4.setEnabled(t);		
		//table.setEnabled(t);
		for(int a=0;a<invoiceNo.size();a++){
			invoiceNo.get(a).setEnabled(t);
			invoiceDate.get(a).setEnabled(t);
			currentDue.get(a).setEnabled(t);
			aTax.get(a).setEnabled(t);
			rejection.get(a).setEnabled(t);
			discount.get(a).setEnabled(t);
			balance.get(a).setEnabled(t);
			status.get(a).setEnabled(t);
		}
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
	private void costCentreIni()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			costCentre.removeAllItems();
			List group = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				costCentre.addItem(element[0].toString());
				costCentre.setItemCaption(element[0].toString(), element[1].toString());
			}
			costCentre.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
}

