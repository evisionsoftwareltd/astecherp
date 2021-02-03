package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.*;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class AssembleFindWindow extends Window {

	SessionBean sessionBean;
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	Label lblTransactionNo,lblPartyName,lblSemiFgName;
	ComboBox cmbTransactionNo,cmbPartyName,cmbSemiFgName;
	PopupDateField dFromDate,dToDate;

	private ArrayList<Label> tbSl=new ArrayList<Label>();
	private ArrayList<Label> tbMasterProductId=new ArrayList<Label>();
	private ArrayList<Label> tbMasterProductName=new ArrayList<Label>();
	private ArrayList<Label> tbMasterProductQty=new ArrayList<Label>();
	private ArrayList<Label> tbAssembleId=new ArrayList<Label>();
	private ArrayList<PopupDateField> tbAssembleDate=new ArrayList<PopupDateField>();
	Table table=new Table();
	NativeButton btnFind=new NativeButton("FIND");
	String receiptItemId;
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0");

	public AssembleFindWindow(SessionBean sessionBean,TextField txtItemId){
		this.sessionBean=sessionBean;
		this.txtItemID=txtItemId;
		this.setResizable(false);
		this.setModal(true);
		center();
		setStyleName("cwindow");
		this.setCaption("MASTER PRODUCT ASSEMBLE FIND :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		//tableDataLoad("%","%");
		cmbTransactionNoLoad();
		setEventAction();
	}

	private void cmbTransactionNoLoad() {
		cmbTransactionNo.removeAllItems();
		String sql="select 0,transactionId from tbMasterProductAssembleInfo where " +
				"assembleDate between '"+dateFormat.format(dFromDate.getValue())+"' and '"+dateFormat.format(dToDate.getValue())+"'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbTransactionNo.addItem(element[1]);
			cmbTransactionNo.setItemCaption(element[1], element[1].toString());
		}
	}
	private void setEventAction() {
		btnFind.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) 
			{
				if(cmbTransactionNo.getValue()!=null)
				{
					btnFindEvent();	
				}
				else
				{
					showNotification("Please, select Transaction No",Notification.TYPE_WARNING_MESSAGE);
				}
				
			}
		});
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptItemId = tbAssembleId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println(receiptItemId);
					txtItemID.setValue(receiptItemId);
					close();
				}
			}
		});
		dFromDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbTransactionNoLoad();
			}
		});
		dToDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbTransactionNoLoad();
			}
		});
	}

	private void btnFindEvent() {
		
		tableDataLoad();
	}
	private void tableDataLoad(){
		for(int a=0;a<tbMasterProductId.size();a++){
			tbMasterProductId.get(a).setValue("");
			tbMasterProductName.get(a).setValue("");
			tbMasterProductQty.get(a).setValue("");
			tbAssembleDate.get(a).setValue(null);
		}
		String sql="select distinct masterProductId,masterProductName,assembleQty,assembleDate," +
				"transactionId from tbIngradiantAssembleDetails where transactionId='"+cmbTransactionNo.getValue()+"'";
		System.out.println(sql);
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==tbSl.size()-1){
				tableRowAdd(a);
			}
			tbMasterProductId.get(a).setValue(element[0]);
			tbMasterProductName.get(a).setValue(element[1]);
			tbMasterProductQty.get(a).setValue(df.format(element[2]));
			tbAssembleDate.get(a).setValue(element[3]);
			tbAssembleId.get(a).setValue(element[4]);
			a++;
		}
	}
	private Iterator dbService(String sql){
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			return session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null||session!=null){
				session.close();
			}
		}
		return null;
	}
	private AbsoluteLayout buildMainLayout(){
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("550px");
		setHeight("500px");

		dFromDate = new PopupDateField();
		dFromDate.setImmediate(false);
		dFromDate.setWidth("110px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setValue(new Date());

		dToDate = new PopupDateField();
		dToDate.setImmediate(false);
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setValue(new Date());

		// lblProudctionType
		lblTransactionNo = new Label("Tranasaction No: ");
		lblTransactionNo.setImmediate(true);
		lblTransactionNo.setWidth("100.0%");
		lblTransactionNo.setHeight("18px");

		// cmbTransactionNo
		cmbTransactionNo = new ComboBox();
		cmbTransactionNo.setImmediate(true);
		cmbTransactionNo.setWidth("110px");
		cmbTransactionNo.setHeight("24px");
		cmbTransactionNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbTransactionNo.setNullSelectionAllowed(true);



		btnFind.setImmediate(true);
		btnFind.setWidth("100px");
		btnFind.setHeight("28px");

		table.setWidth("540px");
		table.setHeight("295px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);
		table.setSelectable(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",15);

		table.addContainerProperty("Master Product ID", Label.class , new Label());
		table.setColumnWidth("Master Product ID",100);
		table.setColumnCollapsed("Master Product ID", true);

		table.addContainerProperty("Master Product NAME", Label.class , new Label());
		table.setColumnWidth("Master Product NAME",260);

		table.addContainerProperty("QTY", Label.class , new Label());
		table.setColumnWidth("QTY",70);

		table.addContainerProperty("ASSEMBLE Date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("ASSEMBLE Date",120);

		table.addContainerProperty("ASSEMBLE ID", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("ASSEMBLE ID",120);
		table.setColumnCollapsed("ASSEMBLE ID", true);
		tableInitialize();

		mainLayout.addComponent(lblTransactionNo,"top:90.0px;left:50.0px;");
		mainLayout.addComponent(cmbTransactionNo, "top:87.0px;left:150.0px;");

		mainLayout.addComponent(new Label("To Date: "),"top:60.0px;left:50.0px;");
		mainLayout.addComponent(dToDate, "top:57.0px;left:150.0px;");

		mainLayout.addComponent(new Label("From Date: "),"top:30.0px;left:50.0px;");
		mainLayout.addComponent(dFromDate, "top:27.0px;left:150.0px;");

		mainLayout.addComponent(btnFind, "top:125.0px;left:150.0px;");

		mainLayout.addComponent(table, "top:155.0px;left:5.0px;");

		return mainLayout;
	}
	private void tableInitialize() {
		for(int a=0;a<10;a++){
			tableRowAdd(a);
		}
	}
	private void tableRowAdd(int ar){
		tbSl.add(ar,new Label());
		tbSl.get(ar).setWidth("100%");
		tbSl.get(ar).setHeight("25px");
		tbSl.get(ar).setValue(ar + 1);

		tbMasterProductId.add(ar,new Label());
		tbMasterProductId.get(ar).setWidth("100%");
		tbMasterProductId.get(ar).setHeight("25px");

		tbMasterProductName.add(ar,new Label());
		tbMasterProductName.get(ar).setWidth("100%");
		tbMasterProductName.get(ar).setHeight("25px");

		///tbUnit.add(ar,new Label());
		//tbUnit.get(ar).setWidth("100%");
		//tbUnit.get(ar).setHeight("25px");

		tbMasterProductQty.add(ar,new Label());
		tbMasterProductQty.get(ar).setWidth("100%");
		tbMasterProductQty.get(ar).setHeight("25px");

		tbAssembleDate.add(ar,new PopupDateField());
		tbAssembleDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbAssembleDate.get(ar).setImmediate(true);
		tbAssembleDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbAssembleDate.get(ar).setWidth("100%");
		tbAssembleDate.get(ar).setHeight("25px");

		tbAssembleId.add(ar,new Label());
		tbAssembleId.get(ar).setWidth("100%");
		tbAssembleId.get(ar).setHeight("25px");

		table.addItem(new Object[]{tbSl.get(ar),tbMasterProductId.get(ar),tbMasterProductName.get(ar),tbMasterProductQty.get(ar),tbAssembleDate.get(ar),tbAssembleId.get(ar)}, ar);
	}
}
