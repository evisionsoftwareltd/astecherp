package acc.appform.transaction;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class InvoiceLocalFind extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "Find", "", "", "", "");
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout firstHorizonTal = new HorizontalLayout();
	private HorizontalLayout thirdHorizonTal = new HorizontalLayout();
	private Label lblfromDate = new Label("From Date :");
	private PopupDateField fromDate = new PopupDateField();
	private Label lbltoDate = new Label("To Date :");
	private PopupDateField toDate = new PopupDateField();
	private Table table = new Table();
	private ArrayList<Label> voucherNo = new  ArrayList<Label>();
	private ArrayList<Label> date = new ArrayList<Label>();
	private ArrayList<Label> ledgerName = new ArrayList<Label>();
	private ArrayList<Label> Narration = new ArrayList<Label>();
	private ArrayList<Label> Amount = new ArrayList<Label>();
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtfDMY = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	private NumberFormat frmt = new DecimalFormat("#0.00");
	private String comWidth = "180px";
	private String title = "";
	SessionBean sessionBean;
	TextField txtVoucherNoFind;
	public InvoiceLocalFind(SessionBean sessionBean, String title,final TextField txtVoucherNoFind)
	{
		this.txtVoucherNoFind=txtVoucherNoFind;
		this.title = title;
		this.sessionBean = sessionBean;
		this.setCaption("INVOICE FIND :: " + sessionBean.getCompany());
		this.setWidth("650px");
		this.setResizable(false);
		this.setStyleName("cwindow");
		table.setWidth("100%");
		table.setHeight("170px");
		table.addContainerProperty("Invoice No", Label.class , null);
		table.setColumnWidth("Invoice No",100);
		table.addContainerProperty("Date", Label.class , null);
		table.setColumnWidth("Date",90);
		table.addContainerProperty("Ledger Name", Label.class , null);
		table.setColumnWidth("Ledger Name",200);
		table.addContainerProperty("Narration", Label.class , null);
		table.setColumnWidth("Narration",90);
		table.addContainerProperty("Amount", Label.class , null);
		table.setColumnWidth("Amount",70);
		table.setColumnAlignments(new String[] {Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_RIGHT});
		tableInitialize();
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					txtVoucherNoFind.setValue(voucherNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					//System.out.println(voucherNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					close();
				}
			}
		});
		
		addAllComponent();
		this.addComponent(mainLayout);
		Component ob[] = {fromDate, toDate, button.btnFind};
		new FocusMoveByEnter(this,ob);
		fromDate.focus();
	}
	private void tableInitialize(){
		for(int a=0;a<10;a++){
			tableRowAdd(a);
		}
	}
	/*private void tabRowSelect(ItemClickEvent event)
	{
		try
		{
			if (event.isDoubleClick())
			{
				Transaction Tx=null;
//				System.out.println(event.getItemId());
//				System.out.println(event.getItem());
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				StringTokenizer st=new StringTokenizer(event.getItem().toString());
				String vN = st.nextToken();
			//	System.out.println(vN);
			//	RcptAgnstInvoice.findVoucher.setValue(voucherNo.get(Integer.valueOf(event.getItemId()+"")).getValue());
				RcptAgnstInvoice.findVoucher.setValue(vN);
			//	RcptAgnstInvoice.findDate.setValue("11-02-2013");
			//	System.out.println(RcptAgnstInvoice.findVoucher);
				//RcptAgnstInvoice.findVoucher.setValue(voucherNo.get(Integer.valueOf(event.getItemId().toString())).getValue());			
				//RcptAgnstInvoice.findDate.setValue((Object)date.get( Integer.valueOf(event.getItemId().toString())).getValue());
				Tx = session.beginTransaction();
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				String cheque =  "chequeDetails"+fsl;
		///		System.out.println("KL");
				System.out.println(title);
				System.out.println(vN);
				if (title == "bankReceiptAgnstInvoice")
				
				RcptAgnstInvoice.findQuery = "select v.Voucher_No, v.Date, L1.Ledger_Id, L1.Ledger_Name, Narration, CrAmount Amount, L2.Ledger_Id 'Ledger Id', L2.Ledger_Name 'Ledger Name', c.Cheque_No, c.Cheque_Date, c.BankName, c.BranchName from "+voucher+" v inner join tbLedger L1 on v.Ledger_Id = L1.Ledger_Id inner join "+cheque+" c on v.Voucher_No = c.Voucher_No inner join tbLedger L2 on c.Bank_Id = L2.Ledger_Id where v.Voucher_No = '"+ vN +"' and v.Cramount>0 and v.vouchertype='cbi' and Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and v.CompanyId = '" + sessionBean.getCompanyId() + "' and C.CompanyId = '" + sessionBean.getCompanyId() + "' order by date,convert(numeric,substring(v.voucher_no,7,50))";
				//				System.out.println(q);

				if (title == "cashReceiptAgnstInvoice")
					RcptAgnstInvoice.findQuery = "select 'Voucher No'=v.Voucher_No, 'V. Date'=Date,v.Ledger_Id,l.ledger_Name,Narration,CrAmount Amount,'','','','','','' from "+voucher+" v, tbLedger l Where v.Voucher_No = '"+ vN +"' and cramount>0 and vouchertype='cci' And l.ledger_id = v.ledger_id and Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and v.CompanyId = '" + sessionBean.getCompanyId() + "' order by date,convert(numeric,substring(V.Voucher_No,7,len(V.Voucher_No)-6))";
				//	System.out.println("select 'Voucher No'=v.Voucher_No, 'V. Date'=Date,v.Ledger_Id,l.ledger_Name,Narration,CrAmount Amount,'','','','','','' from vwVoucher v, tbLedger l Where  cramount>0 and vouchertype='cci' And l.ledger_id = v.ledger_id and Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' order by date,convert(numeric,substring(V.Voucher_No,7,len(V.Voucher_No)-6))");
				if (title == "journalAgnstInvoice")
				{
					journalAgnstInvoice.findVoucher.setValue(vN);
					journalAgnstInvoice.findQuery = "select 'Voucher No'=v.Voucher_No, 'V. Date'=Date,v.Ledger_Id,l.ledger_Name,Narration,CrAmount Amount,costId,'','','','','','' from "+voucher+" v, tbLedger l Where v.Voucher_No = '"+ vN +"' and cramount>0 and vouchertype='jai' And l.ledger_id = v.ledger_id and Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' order by date,convert(numeric,substring(V.Voucher_No,7,len(V.Voucher_No)-6))";
				
				}

			//System.out.println(q);
				this.close();
			}
		}
		catch(Exception ex)
		{
			showNotification(ex.toString());
		}
	}*/

	public void addAllComponent()
	{
		firstHorizonTal.addComponent(lblfromDate);
		fromDate.setWidth(comWidth);
		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);
		firstHorizonTal.addComponent(fromDate);

		firstHorizonTal.addComponent(lbltoDate);
		toDate.setWidth(comWidth);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);
		firstHorizonTal.addComponent(toDate);
		firstHorizonTal.setSpacing(true);
		mainLayout.addComponent(firstHorizonTal);
		mainLayout.setSpacing(true);
		firstHorizonTal.addComponent(button.btnFind);
		mainLayout.addComponent(thirdHorizonTal);
		mainLayout.setComponentAlignment(thirdHorizonTal, Alignment.BOTTOM_RIGHT);

		button.btnFind.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				/*for(int i=0;i<=voucherNo.size();i++){
					//if(!voucherNo.get(i).getValue().toString().isEmpty()){
						
						voucherNo.get(i).setValue("");
						System.out.println("dd");
					//}
					
				}*/
				//table.removeAllItems();
				tableClear();
				valueAdd();
			}
		});

		mainLayout.addComponent(table);
	}
	private void tableClear(){
		for(int a=0;a<voucherNo.size();a++){
			voucherNo.get(a).setValue("");
			date.get(a).setValue("");
			ledgerName.get(a).setValue("");
			Narration.get(a).setValue("");
			Amount.get(a).setValue("");
		}
	}
	private void valueAdd()
	{
		try
		{
			if (dateCompare())
			{
				Transaction Tx=null;
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
//				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				Tx = session.beginTransaction();
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				String cheque =  "chequeDetails"+fsl;
				System.out.println(title);

				String query ="";
				if (title == "bankReceiptAgnstInvoice")
					query = "select distinct v.Voucher_No, v.Date, L1.Ledger_Id, L1.Ledger_Name, Narration, CrAmount Amount from "+voucher+" v inner join tbLedger L1 on v.Ledger_Id = L1.Ledger_Id inner join "+cheque+" c on v.Voucher_No = c.Voucher_No inner join tbLedger L2 on c.Bank_Id = L2.Ledger_Id where v.Cramount>0 and v.vouchertype='cbi' and Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and  v.companyId = '" +sessionBean.getCompanyId().toString() + "'";
			//	System.out.println(query);
				//query = "select v.Voucher_No, v.Date, L1.Ledger_Id, L1.Ledger_Name, Narration, CrAmount Amount, L2.Ledger_Id 'Ledger Id', L2.Ledger_Name 'Ledger Name', c.Cheque_No, c.Cheque_Date, c.BankName, c.BranchName from vwVoucher v inner join tbLedger L1 on v.Ledger_Id = L1.Ledger_Id inner join vwChequeDetails c on v.Voucher_No = c.Voucher_No inner join tbLedger L2 on c.Bank_Id = L2.Ledger_Id where v.Cramount>0 and v.vouchertype='cbi' and Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' order by date,convert(numeric,substring(v.voucher_no,7,50))";

				if (title == "cashReceiptAgnstInvoice")
					//query = "select 'Voucher No'=v.Voucher_No, 'V. Date'=convert(varchar(10),Date,105),v.Ledger_Id,l.ledger_Name,Narration,CrAmount Amount,'','','','','','' from vwVoucher v, tbLedger l Where  cramount>0 and vouchertype='cci' And l.ledger_id = v.ledger_id and Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' order by date,convert(numeric,substring(V.Voucher_No,7,len(V.Voucher_No)-6))";
					query = "select v.Voucher_No, v.Date,v.Ledger_Id,l.ledger_Name,Narration,CrAmount Amount,'','','','','','' from "+voucher+" v, tbLedger l Where  cramount>0 and vouchertype='cci' And l.ledger_id = v.ledger_id and Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and  v.companyId = '" +sessionBean.getCompanyId().toString() + "' order by date,convert(numeric,substring(V.Voucher_No,7,len(V.Voucher_No)-6))";
			//	System.out.println(query);
				if (title == "journalAgnstInvoice")
					query = "select 'Voucher No'=v.Voucher_No, v.Date,v.Ledger_Id,l.ledger_Name,Narration,CrAmount Amount,'','','','','','' from "+voucher+" v, tbLedger l Where  cramount>0 and vouchertype='jai' And l.ledger_id = v.ledger_id and Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and  v.companyId = '" +sessionBean.getCompanyId().toString() + "' order by date,convert(numeric,substring(V.Voucher_No,7,len(V.Voucher_No)-6))"; 
				
				System.out.println(query+"A");
		   //  String query = "select vInvoiceNo, vConsigneeNo, InvoiceDate from tbInvoiceInfo where InvoiceDate between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"'";
			//	Tx = session.beginTransaction();
				List list = session.createSQLQuery(query).list();
				int i = 0;	
				System.out.println("10-7-13");
				for (Iterator iter = list.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();
					/*table.addItem(new Object[] {element[0].toString().trim(), dtfDMY.format(element[1]).toString(), 
							element[3].toString(), element[4].toString(), frmt.format(element[5])}, i);*/
					voucherNo.get(i).setValue(element[0]);
					date.get(i).setValue(element[1]);
					ledgerName.get(i).setValue(element[3]);
					Narration.get(i).setValue(element[4]);
					Amount.get(i).setValue(element[5]);
					if(i==voucherNo.size()-1){
						tableRowAdd(i+1);
					}
//					table.setValue();
//					tableRowAdd(i, element[0].toString().trim(), dtfDMY.format(element[1]).toString(), element[3].toString(), element[4].toString(), frmt.format(element[5]));
					
					i++;
				}
			}
//			else
//			{
//				this.getParent().showNotification("Warning :", "Please Enter From Date And To Date In Correct Format.", Notification.TYPE_WARNING_MESSAGE);
//			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Warning : ", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}


	private void tableRowAdd(int ar)
	{
		voucherNo.add(ar,new Label());
		voucherNo.get(ar).setWidth("100%");
		//voucherNo.get(ar).setValue(invoiceNo);	
		
		date.add(ar,new Label(""));
		date.get(ar).setWidth("100%");
		//date.get(ar).setValue(dt);
		
		ledgerName.add(ar,new Label(""));
		ledgerName.get(ar).setWidth("100%");
		//ledgerName.get(ar).setValue(LedgerName);
		
		Narration.add(ar,new Label(""));
		Narration.get(ar).setWidth("100%");
		//Narration.get(ar).setValue(narration);
		
		Amount.add(ar,new Label(""));
		Amount.get(ar).setWidth("100%");
		//Amount.get(ar).setValue(amount);	
		
		table.addItem(new Object[]{voucherNo.get(ar),date.get(ar), ledgerName.get(ar), Narration.get(ar), Amount.get(ar)},ar);
	}

	private boolean dateCompare()
	{
		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
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
}
