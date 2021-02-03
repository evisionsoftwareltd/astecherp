package com.example.sparePartsTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class FindWindowSP extends Window{

	private VerticalLayout mainLayout=new VerticalLayout();
	private HorizontalLayout hLayout=new HorizontalLayout();
	private PopupDateField fromDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private Label lblFrom=new Label("Form Date:");
	private Label lblTo=new Label("To Date:");
	private TextField txtd= new TextField();
	private NativeButton btnFind=new NativeButton("Find");
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	private SessionBean sessionBean;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat year = new SimpleDateFormat("dd-MM-yy");

	private String px="150px";
	private String vocherType;
	private String receiptId="";

	private ArrayList<Label> lblAutoID = new ArrayList<Label>();

	private ArrayList<Label> lbIssueNo = new ArrayList<Label>();
	private ArrayList<Label> lbIssueTo = new ArrayList<Label>();

	//private TextField txtReceiptId= new TextField();

	private TextField Asaud=new TextField();


	private DecimalFormat df = new DecimalFormat("#0.00");


	private String frmName;

	public FindWindowSP(SessionBean sessionBean,TextField txtReceiptId,String frmName)
	{


		this.sessionBean=sessionBean;
		this.txtd=txtReceiptId;
		this.setCaption("Find Window");
		this.center();
		this.setWidth("700px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");

		if(frmName.equals("raw"))
			vocherType="RDP";

		if(frmName.equals("pack"))
			vocherType="PDP";

		compInit();
		compAdd();
		tableInitialise();	
		setEventAction();

	}
	public void tableInitialise()
	{

		for(int i=0;i<50;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lblAutoID.add(ar,new Label());
		lblAutoID.get(ar).setWidth("100%");
		table.addItem(new Object[]{lblAutoID.get(ar)},ar);
	}
	public void setEventAction(){

		btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

	
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick())
				{
					receiptId=lblAutoID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println("value is"+receiptId);
					txtd.setValue(receiptId);
					System.out.print("our Desire value is"+Asaud.getValue().toString());
					windowClose();
				}
			}
		});
	}

	private void windowClose()
	{

		this.close();
	}

	private void findButtonEvent()
	{	
		Transaction tx;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List lst = null;

			if(frmName.equals("pack"))
				lst= session.createSQLQuery("select 'P. Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), ReceiptNo, ChallanNo, TotalAmount from tbPackingPurchaseInfo where vouchertype='"+vocherType+"' and Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"'  order by date").list();
			if(frmName.equals("raw"))
			{
				String sql = "select * from " 
						+"("
						+"select 'P. Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), ReceiptNo, "
						+"ChallanNo, TotalAmount,MrrNo,a.supplierId,(select supplierName from tbSupplierInfo "
						+"where supplierId=a.supplierId and a.supplierId not like '%AL%')supplier from tbRawPurchaseInfo a where vouchertype like  "
						+"'RDP' and convert(date,Date,105)  between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' "
						+"and  purchaseType  like '%Spare Parts%' and a.SupplierId not like '%AL%' and a.vflag='New'"    

								  +"union all "
								  +"select 'P. Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), ReceiptNo, "
								  +"ChallanNo, TotalAmount,MrrNo,a.supplierId,(select Ledger_Name from tbLedger "
								  +"where Ledger_Id=a.supplierId) from tbRawPurchaseInfo a where vouchertype like " 
								  +"'RDP' and convert(date,Date,105) between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' "
								  +"and  purchaseType  like '%Spare Parts%' and a.SupplierId  like '%AL%'  and a.vflag='New' " 
								  +") as temp order by CAST(temp.MrrNo as int) ,'P. Date' ";


				lst= session.createSQLQuery(sql).list();	
			}


			if(frmName.equals("finishedProductionInfo"))
				lst= session.createSQLQuery("select distinct 'Date '=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Receipt No' = ReceiptNo from tbProductionInfo where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by date").list();

			if(frmName.equals("rawLoanReceive"))
				lst= session.createSQLQuery("select 'P. Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), ReceiptNo, ChallanNo, TotalAmount from tbRawPurchaseInfo where  convert(date,Date,105) between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"'  and  purchaseType like '%loan%'     order by date").list();


			// ISSUE

			if(frmName.equals("packingIssue"))
				lst= session.createSQLQuery("select 'I. Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Issue No' = IssueNo, 'Issued To'=IssuedTo from tbPackingIssueInfo where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by date").list();
			if(frmName.equals("rawIssue"))
				lst= session.createSQLQuery("select 'I. Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Issue No' = IssueNo, 'Issued To'=IssuedTo," +
						"'Section Name' = (select SectionName from tbSectionInfo where AutoID = IssuedTo),'ReqNo' = IssueRef " +
						" from tbRawIssueInfo where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"'     order by date").list();

			if(frmName.equals("rawIssueLoan"))
			{
				
				lst= session.createSQLQuery(      "select 'I. Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)),  IssueNo,b.SupplierName   from tbRawIssueInfo a "
						+ "inner join  tbSupplierDetails b  on a.SupplierID= b.SupplierID "
						+" where  convert(date,Date,105) between '"+dateformat.format(fromDate.getValue())+"'  and '"+ dateformat.format(toDate.getValue()) +"'  and issueType like '%Loan%' order by date ").list();
			}



			if(frmName.equals("spareissue"))
			{

				String query= "select IssueNo,CONVERT(date,IssueDate,105) as Date,issueTo ,b.SectionName  from tbSpareIssueInfo a   "
						+  " inner join   tbSectionInfo b on a.issueTo=b.AutoID   where convert(date,a.IssueDate,105) between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"'  ";

				System.out.println(query);

				lst= session.createSQLQuery(query).list();
			}

			if(frmName.equals("spareIssueLoan"))
			{
				String query= "select IssueNo,IssuedTo,CONVERT(date,Date,105)as Date,(select supplierName from tbSupplierInfo where supplierId like IssuedTo) from tbRawIssueInfo "
						+"where CONVERT(date,Date,105) between '"+dateformat.format(fromDate.getValue())+"' and  '"+dateformat.format(toDate.getValue())+"' and issuetype like '%loan%' order by Date";
				System.out.println(query);
				lst= session.createSQLQuery(query).list();
			}


			if(frmName.equals("deliveryEntry"))
				lst= session.createSQLQuery("select 'I. Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Issue No' = IssueNo, 'Issued To'=IssuedTo from tbDeliveryInfo where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by date").list();

			//  GOODS RETURN
			if(frmName.equals("packingGoodsReturn"))
				lst= session.createSQLQuery("select distinct 'Date '=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Invoice No' = InvoiceNo from tbPackingIssueReturnDetails  where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by date").list();
			if(frmName.equals("rawGoodsReturn"))
				lst= session.createSQLQuery("select distinct 'Date '=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Invoice No' = InvoiceNo from tbRawIssueReturnDetails  where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by date").list();
			if(frmName.equals("finiGoodsReturn"))
				lst= session.createSQLQuery("select distinct 'Date '=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Invoice No' = InvoiceNo from tbDeliveryReturnDetails  where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by date").list();

			//   WASTAGE ENTRY
			if(frmName.equals("packWastageEntry"))
				lst= session.createSQLQuery("select distinct 'Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Invoice No' = InvoiceNo from tbPackingWastageDetails  where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by date").list();
			if(frmName.equals("rawWastageEntry"))
				lst= session.createSQLQuery("select distinct 'Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Invoice No' = InvoiceNo from tbRawWastageDetails  where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by date").list();
			if(frmName.equals("finiWastageEntry"))
				lst= session.createSQLQuery("select distinct 'Date'=convert(varchar(2),day(Date)) + '-' + convert(varchar(2),month(Date)) + '-' + convert(varchar(4),year(Date)), 'Invoice No' = InvoiceNo from tbFiniWastageDetails  where Date between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by date").list();

			if(frmName.equals("reqFormforheadoffice")){

				String sql="select a.date,a.reqNo,b.vDepoName from tbRawRequisitionInfo a "
						+" inner join tbDepoInformation b on a.sectionId=b.vDepoId where a.date  between '"+dateformat.format(fromDate.getValue())+" 00:00:00"+"' and '"+dateformat.format(toDate.getValue())+" 23:59:59.999"+"' order by a.date";
				lst=session.createSQLQuery(sql).list();
			}
			if(frmName.equals("purchaseOrderForm")){

				String sql="select a.poNo,a.poDate,b.supplierName from tbRawPurchaseOrderInfo a "
						+" inner join tbSupplierInfo b on a.SupplierId=b.supplierId where a.poDate  between '"+dateformat.format(fromDate.getValue())+" 00:00:00"+"' and '"+dateformat.format(toDate.getValue())+" 23:59:59.999"+"' order by a.poNo";
				lst=session.createSQLQuery(sql).list();
			}
			if(frmName.equals("MixtureIssue")){

				String sql="select  convert(varchar(10),issueDate,103),issueNo,productionTypeName from tbMixtureIssueEntryInfo where convert(date,issueDate,105) between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' order by issueDate";
				lst=session.createSQLQuery(sql).list();
			}
			if(frmName.equals("MixtureIssueReturn")){

				String sql="select  convert(date,returnDate,105),returnNo,productionTypeName from tbMixtureIssueReturnEntryInfo where returnDate between '"+dateformat.format(fromDate.getValue())+" 00:00:00"+"' and '"+dateformat.format(toDate.getValue())+" 23:59:59.999"+"' order by returnDate";
				lst=session.createSQLQuery(sql).list();
			}

			int i=1;
			if(!lst.isEmpty())
				for (Iterator iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					if(frmName.equals("reqFormforheadoffice")){

						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0] ,element[1],element[2]}, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());
					}
					if(frmName.equals("purchaseOrderForm")){

						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {new SimpleDateFormat("dd-MM-yyyy").format(element[1]) ,element[0],element[2]}, new Integer(i));
						lblAutoID.get(i).setValue(element[0].toString().trim());
					}
					if(frmName.equals("pack") || frmName.equals("raw")){

						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],element[6],lblAutoID.get(i),element[4],element[2], df.format(element[3]) }, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());

					}

					if(frmName.equals("spareIssueLoan"))
					{
						System.out.println("issue");
						if(i==lblAutoID.size())
						{
							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[2] ,element[0],element[3]}, new Integer(i));
						lblAutoID.get(i).setValue(element[0].toString().trim());
					}

					if(frmName.equals("finishedProductionInfo")){

						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i)}, new Integer(i));

						lblAutoID.get(i).setValue(element[1].toString().trim());
					}

					if(frmName.equals("rawIssueLoan"))
					{

						System.out.println("Rabiul Hasan");



						System.out.println("issue");
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0] ,element[1],element[2]}, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());
					}


					if(frmName.equals("finishedDeliveryEntry")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}

						table.addItem(new Object[] {element[0],lblAutoID.get(i),element[2]}, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());

					}



					//  ISSUE
					if(frmName.equals("packingIssue"))
					{
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i),element[2]}, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());
					}



					if(frmName.equals("spareissue"))
					{
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[1].toString(),element[0].toString(),element[3].toString()}, new Integer(i));
						lblAutoID.get(i).setValue(element[0].toString().trim());
					}




					if(frmName.equals("rawIssue")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i),element[3],element[4]}, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());
					}
					if(frmName.equals("deliveryEntry")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i),element[2]}, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());
					}

					//  GOODS RETURN

					if(frmName.equals("packingGoodsReturn")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i)}, new Integer(i));

						lblAutoID.get(i).setValue(element[1].toString().trim());
					}
					if(frmName.equals("rawGoodsReturn")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i)}, new Integer(i));

						lblAutoID.get(i).setValue(element[1].toString().trim());
					}
					if(frmName.equals("finiGoodsReturn")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i)}, new Integer(i));

						lblAutoID.get(i).setValue(element[1].toString().trim());
					}


					//   WASTAGE ENTRY
					if(frmName.equals("packWastageEntry")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i)}, new Integer(i));

						lblAutoID.get(i).setValue(element[1].toString().trim());
					}

					if(frmName.equals("rawLoanReceive"))
					{

						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i),element[2],element[3]}, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());

					}


					if(frmName.equals("rawWastageEntry")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i)}, new Integer(i));

						lblAutoID.get(i).setValue(element[1].toString().trim());
					}
					if(frmName.equals("finiWastageEntry")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0],lblAutoID.get(i)}, new Integer(i));

						lblAutoID.get(i).setValue(element[1].toString().trim());
					}
					if(frmName.equals("MixtureIssue")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0] ,element[1],element[2]}, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());
						System.out.println("Ok");
					}
					if(frmName.equals("MixtureIssueReturn")){
						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[0] ,element[1],element[2]}, new Integer(i));
						lblAutoID.get(i).setValue(element[1].toString().trim());
						System.out.println("Ok");
					}


					i++;
				}
			else
				getParent().showNotification("Warning: ","There are no Data.");



		}
		catch(Exception exp){
			System.out.println(exp);
		}
		

	}
	private void compInit(){


		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);
		fromDate.setWidth(px);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);
		toDate.setWidth(px);


		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("320px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		if(frmName.equals("pack") || frmName.equals("raw"))
		{

			table.addContainerProperty("P.Date", String.class, null);
			table.setColumnWidth("P.Date", 90);
			table.addContainerProperty("Supplier Name", String.class, null);
			table.setColumnWidth("Supplier Name", 250);
			table.addContainerProperty("Receipt No", Label.class, new Label());
			table.setColumnWidth("Receipt No",100);
			table.setColumnCollapsed("Receipt No", true);
			table.addContainerProperty("MRR No", Label.class, new Label());
			table.setColumnWidth("MRR No",100);
			table.addContainerProperty("Challan No", String.class, null);
			table.setColumnWidth("Challan No", 70);
			table.addContainerProperty("Amount", String.class, null);
			table.setColumnWidth("Amount", 70);
		}
		if(frmName.equals("reqFormforheadoffice")){
			table.addContainerProperty("Req.Date", String.class, null);
			table.setColumnWidth("Req Date",100);
			table.addContainerProperty("Req No", Label.class, new Label());
			table.setColumnWidth("Req No",60);
			table.addContainerProperty("Store Name", String.class, null);
			//table.addContainerProperty("Amount", String.class, null);
		}
		if(frmName.equals("purchaseOrderForm")){

			table.addContainerProperty("Po. Date", Label.class, new Label());
			table.setColumnWidth("Po. Date",100);
			table.addContainerProperty("Po. No", String.class, null);
			table.setColumnWidth("Po. No",60);
			table.addContainerProperty("Supplier Name", String.class, null);
		}
		if(frmName.equals("finishedProductionInfo"))
		{

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Receipt No", Label.class, new Label());
			table.setColumnWidth("Receipt No",200);
		}



		//   ISSUE 
		if(frmName.equals("packingIssue"))
		{

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Issue No", Label.class, new Label());
			table.setColumnWidth("Issue No",200);
			table.addContainerProperty("Issue To", Label.class, new Label());
			table.setColumnWidth("Issue To",200);
		}


		if(frmName.equals("spareissue"))
		{

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Issue No", Label.class, new Label());
			table.setColumnWidth("Issue No",200);
			table.addContainerProperty("Issue To", Label.class, new Label());
			table.setColumnWidth("Issue To",200);
		}

		if(frmName.equals("spareIssueLoan"))
		{
			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Issue No", Label.class, new Label());
			table.setColumnWidth("Issue No",70);
			table.addContainerProperty("Issue To", Label.class, new Label());
			table.setColumnWidth("Issue To",330);
		}




		if(frmName.equals("rawIssue"))
		{

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Issue No", Label.class, new Label());
			table.setColumnWidth("Issue No",70);
			table.addContainerProperty("Issue To", Label.class, new Label());
			table.setColumnWidth("Issue To",200);
			table.addContainerProperty("Req No", Label.class, new Label());
			table.setColumnWidth("Req No",80);
		}

		if(frmName.equals("rawIssueLoan"))
		{

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Issue No", Label.class, new Label());
			table.setColumnWidth("Issue No",70);
			table.addContainerProperty("Issue To", Label.class, new Label());
			table.setColumnWidth("Issue To",330);
		}


		if(frmName.equals("deliveryEntry"))
		{

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Issue No", Label.class, new Label());
			table.setColumnWidth("Issue No",200);
			table.addContainerProperty("Issue To", Label.class, new Label());
			table.setColumnWidth("Issue To",200);
		}


		if( frmName.equals("rawLoanReceive"))
		{

			table.addContainerProperty("P.Date", String.class, null);
			table.addContainerProperty("Receipt No", Label.class, new Label());
			table.setColumnWidth("Receipt No",100);
			table.addContainerProperty("Challan No", String.class, null);
			table.addContainerProperty("Amount", String.class, null);

		}


		//   GOODS RETURN
		if(frmName.equals("packingGoodsReturn")){

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Invoice No", Label.class, new Label());
			table.setColumnWidth("Invoice No",200);	
		}
		if(frmName.equals("rawGoodsReturn")){

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Invoice No", Label.class, new Label());
			table.setColumnWidth("Invoice No",200);	
		}
		if(frmName.equals("finiGoodsReturn")){

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Invoice No", Label.class, new Label());
			table.setColumnWidth("Invoice No",200);	
		}



		//   WASTAGE ENTRY
		if(frmName.equals("packWastageEntry")){

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Invoice No", Label.class, new Label());
			table.setColumnWidth("Invoice No",200);

		}
		if(frmName.equals("rawWastageEntry")){

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Invoice No", Label.class, new Label());
			table.setColumnWidth("Invoice No",200);

		}
		if(frmName.equals("finiWastageEntry")){

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Invoice No", Label.class, new Label());
			table.setColumnWidth("Invoice No",200);

		}
		if(frmName.equals("MixtureIssue")){

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Issue No", Label.class, new Label());
			table.addContainerProperty("Production Type", Label.class, new Label());
			table.setColumnWidth("Production Type",200);

		}
		if(frmName.equals("MixtureIssueReturn")){

			table.addContainerProperty("Date", String.class, null);
			table.addContainerProperty("Return No", Label.class, new Label());
			table.addContainerProperty("Production Type", Label.class, new Label());
			table.setColumnWidth("Production Type",200);

		}

	}
	private void compAdd(){

		hLayout.setSpacing(true);
		hLayout.addComponent(lblFrom);
		hLayout.addComponent(fromDate);
		hLayout.addComponent(lblTo);
		hLayout.addComponent(toDate);
		hLayout.addComponent(btnFind);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}


}
