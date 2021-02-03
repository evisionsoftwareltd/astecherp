package com.example.productionTransaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
public class JobOrderFindWindow extends Window{

	SessionBean sessionBean;
	TextField txtd=new TextField();
	VerticalLayout mainLayout=new VerticalLayout();
	
	private PopupDateField dFromDate;
	private PopupDateField dToDate;
	
	private ComboBox cmbPartyName = new ComboBox("Customer :");

//	private ComboBox cmbFinishedGoods = new ComboBox("Finished Goods :");
	
	private NativeButton findButton = new NativeButton("Find");
	
	ArrayList <Label> lblpoNo=new ArrayList<Label>();
	ArrayList <Label> lblPoDate=new ArrayList<Label>();
	ArrayList <Label> lblDeliveryDate=new ArrayList<Label>();
	ArrayList <Label> lblorderNo=new ArrayList<Label>();
	ArrayList <Label> lblorderDate=new ArrayList<Label>();
	ArrayList <Label> lblTransactionNo=new ArrayList<Label>();
	Table table=new Table();
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd");
	
	public JobOrderFindWindow(SessionBean sessionBean,TextField txtReceiptId){
		
		this.sessionBean=sessionBean;
		this.txtd=txtReceiptId;
		this.setCaption("Find Window");
		this.center();
		this.setWidth("770px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		addCmp();
		issueFromData();
		setEventAction();
	}
	
	private void tableclear()
	{
		for(int i=0; i<lblpoNo.size(); i++)
		{
			/*lblpoNo.get(i).setValue("");
			lblPodate.get(i).setValue("");
			lblIssueFrom.get(i).setValue("");
			lblIssueTo.get(i).setValue("");*/
		}
	}
	
	
	private void setEventAction(){
		cmbPartyName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbPartyName.getValue()!=null)
				{
					System.out.println("I am Ok");

					table.removeAllItems();
				}
			}
		});
		/*cmbIssueTo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbIssueTo.getValue()!=null){
//					tableDataLoad();
				}
			}
		});*/
		/*cmbFinishedGoods.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				tableDataLoad();
			}
		});*/
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick())
				{
					String receiptId="";
					receiptId=lblTransactionNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println("value is"+receiptId);
					txtd.setValue(receiptId);
					windowClose();

				}
			}
		});
		
		findButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbPartyName.getValue()!=null)
				{
				
					tableDataLoad();
				}
				
				
				else
				{
					getParent().showNotification("Please Select Customer",Notification.TYPE_WARNING_MESSAGE);
					cmbPartyName.focus();
				}
			}
		});
		
		dFromDate.addListener(new ValueChangeListener() 
		{
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				issueFromData();
				
			}
		});
		
		dToDate.addListener(new ValueChangeListener() 
		{
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				issueFromData();
				
			}
		});
	}
	
	private void windowClose()
	{

		this.close();
	}
	private void tableDataLoad(){
		table.removeAllItems();
		Transaction tx=null;
		String query=null;
		try{
			//query="select * from tbLabelIssueInfo where issueFrom like '"+cmbPartyName.getValue()+"'  and convert(date,jobDate,105) between '"+dateFormatSql.format(dFromDate.getValue())+"' and '"+dateFormatSql.format(dToDate.getValue())+"' ";
			
			String sql= "select poNo,CONVERT(date,poDate,105) podate,CONVERT(date,DeliveryDate,105)deliverydate,orderNo,CONVERT(date,orderDate,105) orderDate,transactionNo "
				 +"from tbJobOrderInfo where CONVERT(date,orderDate,105) between '"+dateFormatSql.format(dFromDate.getValue())+"' and '"+dateFormatSql.format(dToDate.getValue())+"'  and partyId like '"+cmbPartyName.getValue().toString()+"' ";
			System.out.println("tableDataLoad: "+query);
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(sql).list();
			int a=list.size();
			Iterator iter=list.iterator();
			int i = 0;
			
			
			if(!list.isEmpty())
			{
				while(a>0){
					if(iter.hasNext()){
						Object element[]=(Object[]) iter.next();
						String poNo=element[0].toString();
						String poDate=dateFormat.format(element[1]);
						String deliveryDate=dateFormat.format(element[2]);
						String orderNo=element[3].toString();
						String orderDate=dateFormat.format(element[4]);
						String trNo=element[5].toString();
						tableRowAdd(i,poNo,poDate,deliveryDate,orderNo,orderDate,trNo);
					}
					a--;
					i++;
				}	
			}
			else
			{
				showNotification("Ther is No Data Given Criterea",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("TableDataLoad: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
/*	private void FinishedGoodsDataLoad() {
		cmbFinishedGoods.removeAllItems();
		Transaction tx=null;
		String query=null;

		try
		{
			query=" select vProductId,vProductName from tbStandardFinishedInfo";
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbFinishedGoods.addItem(element[0].toString());
				cmbFinishedGoods.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}*/
	private void issueFromData(){
		cmbPartyName.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{
			query= "select  distinct partyId,(select distinct top 1 partyName from tbPartyInfo where vGroupId like partyId ) as partyname  from  tbJobOrderInfo where  orderDate between  '"+dateFormatSql.format(dFromDate.getValue())+"' and '"+dateFormatSql.format(dToDate.getValue())+"' ";

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}
	
	
	
	
	/*private void tableInitialize(){
		for(int a=0;a<10;a++){
			//tableRowAdd(a);
		}
	}*/
	private void tableRowAdd(int ar,String poNo,String PoDate,String deliveryDate,String orderNo,String OrderDate,String TrNo){
		
		lblpoNo.add(ar,new Label());
		lblpoNo.get(ar).setWidth("100%");
		lblpoNo.get(ar).setImmediate(true);
		lblpoNo.get(ar).setValue(poNo);
		
		lblPoDate.add(ar,new Label());
		lblPoDate.get(ar).setWidth("100%");
		lblPoDate.get(ar).setImmediate(true);
		lblPoDate.get(ar).setValue(PoDate);
		
		lblDeliveryDate.add(ar,new Label());
		lblDeliveryDate.get(ar).setWidth("100%");
		lblDeliveryDate.get(ar).setImmediate(true);
		lblDeliveryDate.get(ar).setValue(deliveryDate);
		

		
		lblorderNo.add(ar,new Label());
		lblorderNo.get(ar).setWidth("100%");
		lblorderNo.get(ar).setImmediate(true);
		lblorderNo.get(ar).setValue(orderNo);
		
		lblorderDate.add(ar,new Label());
		lblorderDate.get(ar).setWidth("100%");
		lblorderDate.get(ar).setImmediate(true);
		lblorderDate.get(ar).setValue(OrderDate);
		
		lblTransactionNo.add(ar,new Label());
		lblTransactionNo.get(ar).setWidth("100%");
		lblTransactionNo.get(ar).setImmediate(true);
		lblTransactionNo.get(ar).setValue(TrNo);
		
//		lblFinishedGoods.get(ar).setValue(cmbFinishedGoods.getItemCaption(cmbFinishedGoods.getValue()));
		
		table.addItem(new Object[]{lblpoNo.get(ar),lblPoDate.get(ar),lblDeliveryDate.get(ar),lblorderNo.get(ar),lblorderDate.get(ar),lblTransactionNo.get(ar)},ar);
	}
	private void addCmp() {
		mainLayout.setSpacing(true);
		FormLayout frmLayout=new FormLayout();
		frmLayout.setSpacing(true);
		
		
		
		
		// dFromDate
		dFromDate = new PopupDateField("From Date :");
		dFromDate.setImmediate(false);
		dFromDate.setWidth("-1px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		
		// dToDate
		dToDate = new PopupDateField("To Date :");
		dToDate.setImmediate(false);
		dToDate.setWidth("-1px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		
		
		
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("200px");
		cmbPartyName.setNullSelectionAllowed(true);
		
		

		
//		frmLayout.addComponent(cmbFinishedGoods);
		
//		cmbFinishedGoods.setImmediate(true);
//		cmbFinishedGoods.setWidth("200px");
//		cmbFinishedGoods.setNullSelectionAllowed(true);
		
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		
		table.addContainerProperty("Po No", Label.class, new Label());
		table.setColumnWidth("Po No",170);
		
		table.addContainerProperty("Po Date", Label.class, new Label());
		table.setColumnWidth("Po Date", 100);
		
		table.addContainerProperty("Delivery Date", Label.class, new Label());
		table.setColumnWidth("Delivery Date",100);
		
		table.addContainerProperty("Job Order No", Label.class, new Label());
		table.setColumnWidth("Job Order No", 170);
		
		table.addContainerProperty("Job Oredr Date", Label.class, new Label());
		table.setColumnWidth("Job Oredr Date",100);
		
		table.addContainerProperty("TrNo", Label.class, new Label());
		table.setColumnWidth("TrNo",100);
		table.setColumnCollapsed("TrNo", true);
		
		
		//tableInitialize();
		
		findButton.setWidth("80px");
		findButton.setHeight("28px");
		findButton.setIcon(new ThemeResource("../icons/print.png"));
		
		frmLayout.addComponent(dFromDate);
		frmLayout.addComponent(dToDate);
		frmLayout.addComponent(cmbPartyName);

		frmLayout.addComponent(findButton);
		mainLayout.addComponent(frmLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}
