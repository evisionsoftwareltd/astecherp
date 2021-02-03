package com.example.productionTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class ProductionRequistioFind extends Window {

	SessionBean sessionBean;
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	private PopupDateField dToDate,dFromDate;
	Label lblToDate,lblPartyName,lblSemiFgName;
	ComboBox cmbReqNo,cmbPartyName,cmbSemiFgName;

	private ArrayList<Label> tbSl=new ArrayList<Label>();
	private ArrayList<PopupDateField> tbReqDate=new ArrayList<PopupDateField>();
	private ArrayList<Label> tbSectionName=new ArrayList<Label>();
	private ArrayList<Label> tbReqNo=new ArrayList<Label>();
	SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd");

	Table table=new Table();
	NativeButton btnFind=new NativeButton("FIND");
	String receiptItemId;

	public ProductionRequistioFind(SessionBean sessionBean,TextField txtItemId){
		this.sessionBean=sessionBean;
		this.txtItemID=txtItemId;
		this.setResizable(false);
		this.setModal(true);
		center();
		setStyleName("cwindow");
		this.setCaption("MATERRIAL REQUISITION FIND :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		reqNoLoad();
	}
	private void reqNoLoad(){
		tableClear();
		Iterator iter=dbService("select ReqNo from tbProductionReqInfo order by ReqDate");
		int a=0;
		while(iter.hasNext())
		{
			Object element=(Object) iter.next();
			tbReqNo.get(a).setValue(element);
			a++;
			if(a==tbReqNo.size()-1)
			{
				tableRowAdd(a+1);
			}
		}
	}
	private void setEventAction() {

		btnFind.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				reqNoLoadFind();
			}
		});
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptItemId = tbReqNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println(receiptItemId);
					txtItemID.setValue(receiptItemId);
					close();
				}
			}
		});
	}
	private void tableClear(){
		for(int a=0;a<tbReqNo.size();a++){
			tbReqDate.get(a).setValue(new java.util.Date());
			tbSectionName.get(a).setValue("");
			tbReqNo.get(a).setValue("");
		}
	}
	private void reqNoLoadFind() {
		tableClear();
		String sql="select ReqDate,ReqNo,productionTypeName from tbProductionReqInfo where convert(date,ReqDate,105) " +
				"between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' order by ReqDate";
		System.out.println(sql);
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			tbReqDate.get(a).setValue(element[0]);
			tbSectionName.get(a).setValue(element[2]);
			tbReqNo.get(a).setValue(element[1]);
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
		setWidth("450px");
		setHeight("350px");

		// lblProudctionType
		lblToDate = new Label("Requisition Date: ");
		lblToDate.setImmediate(true);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("18px");

		// cmbProductionType
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);



		btnFind.setImmediate(true);
		btnFind.setWidth("100px");
		btnFind.setHeight("28px");

		table.setWidth("400px");
		table.setHeight("185px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);
		table.setSelectable(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",15);
		table.addContainerProperty("Req Date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("Req Date",105);
		table.addContainerProperty("Section Name", Label.class , new Label());
		table.setColumnWidth("Section Name",130);
		table.addContainerProperty("Req No", Label.class , new Label());
		table.setColumnWidth("Req No",70);


		tableInitialize();

		mainLayout.addComponent(new Label("From Date: "),"top:10.0px;left:70.0px;");
		mainLayout.addComponent(dFromDate, "top:7.0px;left:170.0px;");
		
		mainLayout.addComponent(new Label("To Date"),"top:35.0px;left:70.0px;");
		mainLayout.addComponent(dToDate, "top:33.0px;left:170.0px;");

		//mainLayout.addComponent(lblPartyName,"top:60.0px;left:70.0px;");
		//mainLayout.addComponent(cmbPartyName, "top:57.0px;left:170.0px;");

		//mainLayout.addComponent(lblSemiFgName,"top:90.0px;left:70.0px;");
		//mainLayout.addComponent(cmbSemiFgName, "top:87.0px;left:170.0px;");

		mainLayout.addComponent(btnFind, "top:70.0px;left:170.0px;");

		mainLayout.addComponent(table, "top:110.0px;left:10.0px;");

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
		
		tbReqDate.add(ar,new PopupDateField());
		tbReqDate.get(ar).setWidth("100%");
		tbReqDate.get(ar).setHeight("25px");
		tbReqDate.get(ar).setImmediate(true);
		tbReqDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbReqDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbReqDate.get(ar).setValue(new java.util.Date());

		tbSectionName.add(ar,new Label());
		tbSectionName.get(ar).setWidth("100%");
		tbSectionName.get(ar).setHeight("25px");
		
		tbReqNo.add(ar,new Label());
		tbReqNo.get(ar).setWidth("100%");
		tbReqNo.get(ar).setHeight("25px");


		table.addItem(new Object[]{tbSl.get(ar),tbReqDate.get(ar),tbSectionName.get(ar),tbReqNo.get(ar)}, ar);
	}
}
