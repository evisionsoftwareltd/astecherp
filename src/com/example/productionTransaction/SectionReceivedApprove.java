package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.sun.java.swing.plaf.windows.resources.windows;
import com.vaadin.data.Property.*;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.*;
import com.vaadin.ui.Window.Notification;

public class SectionReceivedApprove extends Window {

	private static final long serialVersionUID = -3672729251787238695L;
	SessionBean sessionBean;
	AbsoluteLayout mainLayout;
	Label lblFromDate,lblToDate,lblDate,lblProductionType,lblReqNo;
	PopupDateField dateFrom,dateTo,dateCur;
	ComboBox cmbProductionType,cmbReqNo;
//CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private NativeButton btnApprovedReport = new NativeButton("Approved Report");
	private NativeButton btnRequisition = new NativeButton("Requisition Report");
	private NativeButton btnFloorStockAsOn = new NativeButton("Floor Stock As On Date");
	private NativeButton btnFloorStockDateBetween = new NativeButton("Floor Stock Date Between");
	private NativeButton btnLoadData = new NativeButton("Load Data");
//	private NativeButton btnFind = new NativeButton("Load Data");

	private NativeButton btnApproved = new NativeButton("Approved");
	private NativeButton btnRefresh = new NativeButton("Refresh");
	private NativeButton btnExit = new NativeButton("Exit");

	ArrayList<Label>tbSl = new ArrayList<Label>();
	ArrayList<CheckBox>tbChk=new ArrayList<CheckBox>();
	ArrayList<Label>tbRawId=new ArrayList<Label>();
	ArrayList<Label>tbRawName=new ArrayList<Label>();
	ArrayList<TextRead>tbUnit=new ArrayList<TextRead>(1);
	ArrayList<TextRead>tbRequiredBag=new ArrayList<TextRead>(1);
	ArrayList<TextRead>tbRequiredKg=new ArrayList<TextRead>(1);
	ArrayList<TextRead>tbReceivedBag=new ArrayList<TextRead>(1);
	ArrayList<TextRead>tbReceivedKg=new ArrayList<TextRead>(1);
	Table table=new Table();

	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0.00");
	int count=0;
	
	public SectionReceivedApprove(SessionBean sesionBean){
		this.sessionBean=sesionBean;
		this.setResizable(false);
		this.setCaption("SECTION RECEIVED APPROVE :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();
		setEventAction();
		cmbReqNoLoad(dateFormat.format(dateFrom.getValue()),dateFormat.format(dateTo.getValue()));
	}
	private void cmbReqNoLoad(String fromDate,String toDate){
		cmbReqNo.removeAllItems();
		Iterator iter=dbService("select ReqNo from tbMouldSectionReceiptInfo where IssueDate between " +
				"'"+fromDate+"' and '"+toDate+"' and ApproveFlag=0");
		while(iter.hasNext()){
			cmbReqNo.addItem(iter.next());
		}
	}
	private void tableDataLoad(){
		String sql="select rawItemCode,rawItemName,unit,requiredQtyBag,requiredQtyKg,ReceiptQtyBag,ReceiptQtyKg "+
				" from tbMouldSectionReceiptDetails where ReqNo='"+cmbReqNo.getValue()+"'";
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbRawId.get(a).setValue(element[0]);
			tbRawName.get(a).setValue(element[1]);
			tbUnit.get(a).setValue(element[2]);
			tbRequiredBag.get(a).setValue(df.format(element[3]));
			tbRequiredKg.get(a).setValue(df.format(element[4]));
			tbReceivedBag.get(a).setValue(df.format(element[5]));
			tbReceivedKg.get(a).setValue(df.format(element[6]));
			a++;
			if(a==tbRawId.size()-1){
				tableRowAdd(a+1);
			}
		}
	}
	private void tableClear(){
		for(int a=0;a<tbChk.size();a++){
			tbChk.get(a).setValue(false);
			tbRawId.get(a).setValue("");
			tbRawName.get(a).setValue("");
			tbUnit.get(a).setValue("");
			tbReceivedBag.get(a).setValue("");
			tbReceivedKg.get(a).setValue("");
			tbRequiredBag.get(a).setValue("");
			tbRequiredKg.get(a).setValue("");
		}
	}
	private void setEventAction() {
		table.addListener(new HeaderClickListener() {

			public void headerClick(HeaderClickEvent event) {

				if(count==0){
					for(int a=0;a<tbChk.size();a++){
						if(!tbRawName.get(a).getValue().toString().isEmpty()){
							tbChk.get(a).setValue(true);
						}
					}
					count=1;
				}
				else if(count==1){
					for(int a=0;a<tbChk.size();a++){
						if(!tbRawName.get(a).getValue().toString().isEmpty()){
							tbChk.get(a).setValue(false);
						}
					}
					count=0;
				}
			}
		});
		cmbReqNo.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				tableClear();
				if(cmbReqNo.getValue()!=null){
					tableDataLoad();
				}
				
			}
		});
		btnLoadData.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				cmbReqNoLoad(dateFormat.format(dateFrom.getValue()),dateFormat.format(dateTo.getValue()));
			}
		});
		btnApproved.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
			
				if(checkValidation()){
					approvedButtonEvent();
				}
			}
		});
		btnRefresh.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				txtClear();
				tableClear();
			}
		});
		
		
		btnExit.addListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) 
			{
				close();
				
			}
		});
	}
	private boolean checkValidation(){
		
		if(cmbReqNo.getValue()!=null){
			if(!tbRawId.get(0).getValue().toString().isEmpty()){
				if(tbChk.get(0).booleanValue()){
					return true;
				}
				else{
					showNotification("Please Check at least on Product",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Requisition",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private void insertData(){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			for(int a=0;a<tbRawId.size();a++){
				if(tbChk.get(a).booleanValue()&&!tbRawId.get(a).getValue().toString().isEmpty()){
					String sql="update tbMouldSectionReceiptDetails set ApproveFlag=1,ApprovedBy='"+sessionBean.getUserName()+"' where ReqNo like '"+cmbReqNo.getValue()+"'" +
							" and rawItemCode like '"+tbRawId.get(a).getValue()+"'";
					session.createSQLQuery(sql).executeUpdate();
					
				}
			}
			String sqlInfo="exec PrcApproveInfoUpdate '"+cmbReqNo.getValue()+"'";
			session.createSQLQuery(sqlInfo).executeUpdate();
		}
		catch(Exception exp){
			tx.rollback();
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				session.close();
				showNotification("Approved Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			}
		}
	}
	private void txtClear(){
		dateCur.setValue(new java.util.Date());
		dateFrom.setValue(new java.util.Date());
		dateTo.setValue(new java.util.Date());
		cmbReqNo.removeAllItems();
		cmbReqNo.setValue(null);
		cmbReqNoLoad(dateFormat.format(dateFrom.getValue()),dateFormat.format(dateTo.getValue()));
	}
	private void approvedButtonEvent(){
		final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Approve?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					mb.buttonLayout.getComponent(0).setEnabled(false);
					insertData();
					//cmbReqNo.setValue(null);
					txtClear();
					tableClear();
					
				}
			}
		});
	}
	private Iterator<?> dbService(String sql){
		Session session=SessionFactoryUtil.getInstance().openSession();
		Iterator<?> iter=null;
		try{
			iter= session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}
	private void tableInitialize() {
		for(int a=0;a<10;a++){
			tableRowAdd(a);
		}
	}
	private void tableRowAdd(int ar){

		tbSl.add(ar, new Label());
		tbSl.get(ar).setValue(ar+1);
		tbSl.get(ar).setWidth("100%");
		tbSl.get(ar).setHeight("-1px");

		tbChk.add(ar, new CheckBox());
		tbChk.get(ar).setImmediate(true);

		tbRawName.add(ar, new Label());
		tbRawName.get(ar).setWidth("100%");
		tbRawName.get(ar).setHeight("-1px");

		tbRawId.add(ar, new Label());
		tbRawId.get(ar).setValue(ar+1);
		tbRawId.get(ar).setWidth("100%");
		tbRawId.get(ar).setHeight("-1px");

		tbUnit.add(ar, new TextRead(1));
		tbUnit.get(ar).setImmediate(true);
		tbUnit.get(ar).setWidth("100%");
		tbUnit.get(ar).setHeight("-1px");

		tbReceivedBag.add(ar, new TextRead(1));
		tbReceivedBag.get(ar).setImmediate(true);
		tbReceivedBag.get(ar).setWidth("100%");
		tbReceivedBag.get(ar).setHeight("-1px");

		tbReceivedKg.add(ar, new TextRead(1));
		tbReceivedKg.get(ar).setImmediate(true);
		tbReceivedKg.get(ar).setWidth("100%");
		tbReceivedKg.get(ar).setHeight("-1px");

		tbRequiredBag.add(ar, new TextRead(1));
		tbRequiredBag.get(ar).setImmediate(true);
		tbRequiredBag.get(ar).setWidth("100%");
		tbRequiredBag.get(ar).setHeight("-1px");

		tbRequiredKg.add(ar, new TextRead(1));
		tbRequiredKg.get(ar).setImmediate(true);
		tbRequiredKg.get(ar).setWidth("100%");
		tbRequiredKg.get(ar).setHeight("-1px");

		table.addItem(new Object[]{tbSl.get(ar),tbChk.get(ar),tbRawName.get(ar),tbRawId.get(ar),tbUnit.get(ar),
				tbRequiredBag.get(ar),tbRequiredKg.get(ar),tbReceivedBag.get(ar),tbReceivedKg.get(ar)},ar);
	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("880px");
		setHeight("650px");

		// lblDate
		lblFromDate = new Label("From Date: ");
		lblFromDate.setImmediate(true);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("18px");

		//Declare Date
		dateFrom = new PopupDateField();
		dateFrom.setImmediate(true);
		dateFrom.setWidth("110px");
		dateFrom.setDateFormat("dd-MM-yyyy");
		dateFrom.setValue(new java.util.Date());
		dateFrom.setResolution(PopupDateField.RESOLUTION_DAY);

		// lblDate
		lblToDate = new Label("To Date: ");
		lblToDate.setImmediate(true);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("18px");

		//Declare Date
		dateTo = new PopupDateField();
		dateTo.setImmediate(true);
		dateTo.setWidth("110px");
		dateTo.setDateFormat("dd-MM-yyyy");
		dateTo.setValue(new java.util.Date());
		dateTo.setResolution(PopupDateField.RESOLUTION_DAY);

		// lblDate
		lblDate = new Label("Date: ");
		lblDate.setImmediate(true);
		lblDate.setWidth("100.0%");
		lblDate.setHeight("18px");

		//Declare Date
		dateCur = new PopupDateField();
		dateCur.setImmediate(true);
		dateCur.setWidth("110px");
		dateCur.setDateFormat("dd-MM-yyyy");
		dateCur.setValue(new java.util.Date());
		dateCur.setResolution(PopupDateField.RESOLUTION_DAY);

		/*lblProductionType = new Label("Production Type : ");
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("100.0%");
		lblProductionType.setHeight("18px");

		cmbProductionType =new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);*/

		lblReqNo = new Label("Req No: ");
		lblReqNo.setImmediate(false);
		lblReqNo.setWidth("100.0%");
		lblReqNo.setHeight("18px");

		cmbReqNo =new ComboBox();
		cmbReqNo.setImmediate(true);
		cmbReqNo.setWidth("200px");
		cmbReqNo.setHeight("24px");
		cmbReqNo.setNullSelectionAllowed(true);
		cmbReqNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		btnLoadData.setWidth("100px");
		btnLoadData.setHeight("28px");
		btnLoadData.setIcon(new ThemeResource("../icons/icon_get_world.gif"));

		btnApprovedReport.setWidth("180px");
		btnApprovedReport.setHeight("28px");
		btnApprovedReport.setIcon(new ThemeResource("../icons/generate.png"));

		btnRequisition.setWidth("180px");
		btnRequisition.setHeight("28px");
		btnRequisition.setIcon(new ThemeResource("../icons/generate.png"));

		btnFloorStockAsOn.setWidth("180px");
		btnFloorStockAsOn.setHeight("28px");
		btnFloorStockAsOn.setIcon(new ThemeResource("../icons/generate.png"));

		btnFloorStockDateBetween.setWidth("180px");
		btnFloorStockDateBetween.setHeight("28px");
		btnFloorStockDateBetween.setIcon(new ThemeResource("../icons/generate.png"));

		//table.setSelectable(true);
		table.setWidth("840px");
		table.setHeight("320px");
		table.setImmediate(true); // react at once when something is selected
		table.setColumnCollapsingAllowed(true);	
		table.setFooterVisible(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",15);
		table.setColumnAlignment("SL", table.ALIGN_CENTER);

		table.addContainerProperty("CHK",  CheckBox.class, new CheckBox());
		table.setColumnWidth("CHK",20);

		table.addContainerProperty("Item Name", Label.class, new Label());
		table.setColumnWidth("Item Name", 300);
		table.setColumnAlignment("Item Name", table.ALIGN_CENTER);

		table.addContainerProperty("Item Id", Label.class, new Label());
		table.setColumnWidth("Item Id", 50);
		table.setColumnAlignment("Item Id", table.ALIGN_CENTER);
		table.setColumnCollapsed("Item Id", true);

		table.addContainerProperty("Unit", TextRead.class, new TextRead(1));
		table.setColumnWidth("Unit", 50);
		table.setColumnAlignment("Unit", table.ALIGN_CENTER);

		table.addContainerProperty("Required(KG)", TextRead.class, new TextRead(1));
		table.setColumnWidth("Required(KG)", 80);
		table.setColumnAlignment("Required(KG)", table.ALIGN_CENTER);

		table.addContainerProperty("Required(Bag)", TextRead.class, new TextRead(1));
		table.setColumnWidth("Required(Bag)", 80);
		table.setColumnAlignment("Required(Bag)", table.ALIGN_CENTER);

		table.addContainerProperty("Received(KG)", TextRead.class, new TextRead(1));
		table.setColumnWidth("Received(KG)", 80);
		table.setColumnAlignment("Received(KG)", table.ALIGN_CENTER);

		table.addContainerProperty("Received(Bag)", TextRead.class, new TextRead(1));
		table.setColumnWidth("Received(Bag)", 80);
		table.setColumnAlignment("Received(Bag)", table.ALIGN_CENTER);

		btnApproved.setWidth("100px");
		btnApproved.setHeight("28px");
		btnApproved.setIcon(new ThemeResource("../icons/icon_get_world.gif"));

		btnRefresh.setWidth("100px");
		btnRefresh.setHeight("28px");
		btnRefresh.setIcon(new ThemeResource("../icons/generate.png"));

		btnExit.setWidth("100px");
		btnExit.setHeight("28px");
		btnExit.setIcon(new ThemeResource("../icons/generate.png"));

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);

		mainLayout.addComponent(lblFromDate,"top:60px;left:20px;");
		mainLayout.addComponent(dateFrom,"top:58px;left:120px;");

		mainLayout.addComponent(lblToDate,"top:90px;left:20px;");
		mainLayout.addComponent(dateTo,"top:88px;left:120px;");

		mainLayout.addComponent(lblDate,"top:30px;left:20px;");
		mainLayout.addComponent(dateCur,"top:28px;left:120px;");

		mainLayout.addComponent(lblReqNo,"top:155px;left:20px;");
		mainLayout.addComponent(cmbReqNo,"top:153px;left:120px;");

		//mainLayout.addComponent(lblReqNo,"top:150px;left:20px;");
		//mainLayout.addComponent(cmbReqNo,"top:148px;left:120px;");

		mainLayout.addComponent(btnApprovedReport,"top:30px;left:340px;");
		mainLayout.addComponent(btnRequisition,"top:60px;left:340px;");
		mainLayout.addComponent(btnFloorStockAsOn,"top:90px;left:340px;");
		mainLayout.addComponent(btnFloorStockDateBetween,"top:120px;left:340px;");
		mainLayout.addComponent(btnLoadData,"top:118px;left:120px;");

		mainLayout.addComponent(table,"top:190px;left:20px;");
		mainLayout.addComponent(lblLine,"top:530px;left:0px;");
		mainLayout.addComponent(btnApproved,"top:560px;left:200px;");
		mainLayout.addComponent(btnRefresh,"top:560px;left:310px;");
		mainLayout.addComponent(btnExit,"top:560px;left:420px;");


		return mainLayout;
	}
}
