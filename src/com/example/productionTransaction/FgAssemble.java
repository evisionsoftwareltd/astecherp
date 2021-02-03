package com.example.productionTransaction;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.FinishedGoodsModule.SemiFgFindWindow;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class FgAssemble extends Window {

	SessionBean sessionBean;
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	private NativeButton btnSubmit = new NativeButton("Submit");

	private Label lblMasterProductName,lblBatchNo,lblStockQty,lblFgName;
	private Label lblFgQty;
	private Label lblTrId;
	private Label lblDate;
	private ComboBox cmbMasterProduct,cmbBatchNo,cmbFgName;
	private TextRead txtTrId,txtStockQty;
	private AmountField txtFgQty;
	private PopupDateField dateField;

	private Table table = new Table(); 

	private ArrayList<Label> tbsl = new ArrayList<Label>();
	private ArrayList<Label> tbLblIngradName = new ArrayList<Label>();
	private ArrayList<TextRead> tbQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbStockQty = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> tbRejectQty = new ArrayList<AmountField>();
	private ArrayList<TextRead> tbtxtratio = new ArrayList<TextRead>(1);
	private ArrayList<Label> tbtxtIngId = new ArrayList<Label>();

	private Table tableAssemble = new Table(); 
	private ArrayList<Label> tbslAssemble = new ArrayList<Label>();
	private ArrayList<CheckBox> tbChkEdit = new ArrayList<CheckBox>();
	private ArrayList<Label> tbLbMasterProductName = new ArrayList<Label>();
	private ArrayList<Label> tbLbMasterProductId = new ArrayList<Label>();
	private ArrayList<Label> tbLblFgName = new ArrayList<Label>();
	private ArrayList<Label> tbLblFgId = new ArrayList<Label>();
	private ArrayList<Label> tblblBatchNo = new ArrayList<Label>();
	private ArrayList<TextRead> tbAssembleQty = new ArrayList<TextRead>(1);
	
	private ArrayList<Label> tbIngOneId = new ArrayList<Label>();
	private ArrayList<Label> tbIngOneQty = new ArrayList<Label>();
	private ArrayList<Label> tbIngTwoId = new ArrayList<Label>();
	private ArrayList<Label> tbIngTwoQty = new ArrayList<Label>();
	private ArrayList<Label> tbIngThreeId = new ArrayList<Label>();
	private ArrayList<Label> tbIngThreeQty = new ArrayList<Label>();
	private ArrayList<Label> tbIngFourId = new ArrayList<Label>();
	private ArrayList<Label> tbIngFourQty = new ArrayList<Label>();
	private ArrayList<Label> tbIngFiveId = new ArrayList<Label>();
	private ArrayList<Label> tbIngFiveQty = new ArrayList<Label>();

	private Label label = new Label();
	private Label l1 = new Label();
	private Label l2 = new Label();
	DecimalFormat df=new DecimalFormat("#0");
	boolean isUpdate=false;
	boolean isFind=false;
	private Formatter fmt;

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat decimalf = new DecimalFormat("#0.00");
	private SimpleDateFormat dateformatNew = new SimpleDateFormat("yyyy-MM-dd");

	ArrayList<Component> allComp = new ArrayList<Component>();

	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");

	public FgAssemble(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("DAILY ASSEMBLE ENTRY::"+sessionBean.getCompany());
		setContent(buildMainLayout());
		tableInitialise();
		tableInitialiseAssemble();
		btnIni(true);
		componentIni(true);
		setEventAction();
		//cmbBatchNoData();
		cmbMasterProductLoad();
		focusMoveByEnter();
		//transactionNoLoad();
	}
	private void transactionNoLoad()
	{
		String sql="select cast(isnull(MAX(transactionId),0)as int)+1 from tbMasterProductAssembleInfo";
		Iterator iter =dbService(sql);
		if(iter.hasNext())
		{
			//Object element=(Object[])iter.next();
			txtTrId.setValue(iter.next());
		}
	}
	/*private void cmbBatchNoData(){
		cmbBatchNo.removeAllItems();
		String sql="select distinct 0,batchNo from tbIssueToAssembleInfo where isAdjust=0";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbBatchNo.addItem(element[1]);
			cmbBatchNo.setItemCaption(element[1], element[1].toString());
		}
	}*/
	private void cmbMasterProductLoad()
	{
		cmbMasterProduct.removeAllItems();
		String sql="select distinct fgId,fgName from tbFinishedProductDetailsNew where consumptionStage='assemble' ";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbMasterProduct.addItem(element[0]);
			cmbMasterProduct.setItemCaption(element[0], element[1].toString());
		}
	}
	
	
	
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("620px");
		mainLayout.setHeight("610px");


		lblBatchNo = new Label();
		lblBatchNo.setImmediate(false);
		
		
		lblBatchNo.setWidth("-1px");
		lblBatchNo.setHeight("-1px");
		lblBatchNo.setValue( "Batch No : ");
		mainLayout.addComponent(lblBatchNo, "top:25.0px;left:21.0px;");
		lblBatchNo.setVisible(false);

		cmbBatchNo= new ComboBox();
		cmbBatchNo.setImmediate(true);
		cmbBatchNo.setWidth("200px");
		cmbBatchNo.setHeight("24px");
		cmbBatchNo.setNullSelectionAllowed(true); 
		cmbBatchNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbBatchNo, "top:23.0px;left:155.0px;");
		cmbBatchNo.setVisible(false);

		lblMasterProductName = new Label();
		lblMasterProductName.setImmediate(false);
		lblMasterProductName.setWidth("-1px");
		lblMasterProductName.setHeight("-1px");
		lblMasterProductName.setValue( "Master Product Name : ");
		mainLayout.addComponent(lblMasterProductName, "top:22.0px;left:21.0px;");

		cmbMasterProduct= new ComboBox();
		cmbMasterProduct.setImmediate(true);
		cmbMasterProduct.setWidth("345px");
		cmbMasterProduct.setHeight("24px");
		cmbMasterProduct.setNullSelectionAllowed(true);
		cmbMasterProduct.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbMasterProduct, "top:20.0px;left:155.0px;");
		
		lblFgName = new Label();
		lblFgName.setImmediate(false);
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");
		lblFgName.setValue( "Secondary FG Name : ");
		mainLayout.addComponent(lblFgName, "top:50.0px;left:21.0px;");

		cmbFgName= new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setWidth("345px");
		cmbFgName.setHeight("24px");
		cmbFgName.setNullSelectionAllowed(true);
		cmbFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbFgName, "top:48.0px;left:155.0px;");

		lblFgQty = new Label();
		lblFgQty.setImmediate(false);
		lblFgQty.setWidth("-1px");
		lblFgQty.setHeight("-1px");
		lblFgQty.setValue( "Assemble Qty :");
		mainLayout.addComponent(lblFgQty, "top:75.0px;left:21.0px;");

		txtFgQty= new AmountField();
		txtFgQty.setImmediate(true);
		txtFgQty.setWidth("120px");
		txtFgQty.setHeight("24px");
		mainLayout.addComponent(txtFgQty, "top:73.0px;left:155.0px;");

		lblTrId = new Label();
		lblTrId.setImmediate(false);
		lblTrId.setWidth("-1px");
		lblTrId.setHeight("-1px");
		lblTrId.setValue( "Transection ID :");
		mainLayout.addComponent(lblTrId, "top:100.0px;left:285.0px;");

		txtTrId= new TextRead(1);
		txtTrId.setImmediate(false);
		txtTrId.setWidth("100px");
		txtTrId.setHeight("24px");
		mainLayout.addComponent(txtTrId, "top:98.0px;left:400.0px;");
		
		lblStockQty = new Label();
		lblStockQty.setImmediate(false);
		lblStockQty.setWidth("-1px");
		lblStockQty.setHeight("-1px");
		lblStockQty.setValue( "Assembled Stock :");
		mainLayout.addComponent(lblStockQty, "top:75.0px;left:285.0px;");

		txtStockQty= new TextRead(1);
		txtStockQty.setImmediate(false);
		txtStockQty.setWidth("100px");
		txtStockQty.setHeight("24px");
		mainLayout.addComponent(txtStockQty, "top:73.0px;left:400.0px;");

		lblDate = new Label();
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue( "Date :");
		mainLayout.addComponent(lblDate, "top:100.0px;left:21.0px;");

		dateField= new PopupDateField();
		dateField.setWidth("110px");
		dateField.setHeight("24px");
		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dateField, "top:98.0px;left:155.0px;");

		btnSubmit.setWidth("95px");
		btnSubmit.setHeight("50px");
		//btnSubmit.setIcon(new ThemeResource("../icons/icon_get_world.gif"));
		mainLayout.addComponent(btnSubmit, "top:70.0px;left:510.0px;");


		table.setWidth("575px");
		table.setHeight("160px");

		table.setFooterVisible(true);
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",15);

		table.addContainerProperty("Ingradiant Name", Label.class , new Label());
		table.setColumnWidth("Ingradiant Name",255);

		table.addContainerProperty("Stock Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Stock Qty",55);

		table.addContainerProperty("Ratio", TextRead.class , new TextRead(1));
		table.setColumnWidth("Ratio",30);

		table.addContainerProperty("Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Qty",55);
		
		table.addContainerProperty("Reject Qty", AmountField.class , new AmountField());
		table.setColumnWidth("Reject Qty",60);

		table.addContainerProperty("Ingradiant Id", Label.class , new Label());
		table.setColumnWidth("Ingradiant Id",75);
		table.setColumnCollapsed("Ingradiant Id", true);

		mainLayout.addComponent(table,"top: 140.0px; left: 21.0px;");

		tableAssemble.setWidth("575px");
		tableAssemble.setHeight("240px");

		tableAssemble.setFooterVisible(true);
		tableAssemble.setFooterVisible(true);
		tableAssemble.setColumnCollapsingAllowed(true);

		tableAssemble.addContainerProperty("SL", Label.class , new Label());
		tableAssemble.setColumnWidth("SL",15);

		tableAssemble.addContainerProperty("Edit", CheckBox.class , new CheckBox());
		tableAssemble.setColumnWidth("Edit",30);

		tableAssemble.addContainerProperty("Master Product Name", Label.class , new Label());
		tableAssemble.setColumnWidth("Master Product Name",350);

		tableAssemble.addContainerProperty("Assemble Qty", TextRead.class , new TextRead(1));
		tableAssemble.setColumnWidth("Assemble Qty",85);

		tableAssemble.addContainerProperty("Master Product Id", TextRead.class , new TextRead());
		tableAssemble.setColumnWidth("Master Product Id",75);
		tableAssemble.setColumnCollapsed("Master Product Id", true);
		
		tableAssemble.addContainerProperty("Secondary FG Id", TextRead.class , new TextRead());
		tableAssemble.setColumnWidth("Secondary FG Id",75);
		tableAssemble.setColumnCollapsed("Secondary FG Id", true);
		
		tableAssemble.addContainerProperty("Secondary FG Name", TextRead.class , new TextRead());
		tableAssemble.setColumnWidth("Secondary FG Name",75);
		tableAssemble.setColumnCollapsed("Secondary FG Name", true);

		tableAssemble.addContainerProperty("BatchNo", TextRead.class , new TextRead());
		tableAssemble.setColumnWidth("BatchNo",75);
		tableAssemble.setColumnCollapsed("BatchNo", true);
		
		tableAssemble.addContainerProperty("ingOneId", Label.class , new Label());
		tableAssemble.setColumnWidth("ingOneId",55);
		tableAssemble.addContainerProperty("ingOneQty", Label.class , new Label());
		tableAssemble.setColumnWidth("ingOneQty",55);
		tableAssemble.addContainerProperty("ingTwoId", Label.class , new Label());
		tableAssemble.setColumnWidth("ingTwoId",55);
		tableAssemble.addContainerProperty("ingTwoQty", Label.class , new Label());
		tableAssemble.setColumnWidth("ingTwoQty",55);
		tableAssemble.addContainerProperty("ingThreeId", Label.class , new Label());
		tableAssemble.setColumnWidth("ingThreeId",55);
		tableAssemble.addContainerProperty("ingThreeQty", Label.class , new Label());
		tableAssemble.setColumnWidth("ingThreeQty",55);
		tableAssemble.addContainerProperty("ingFourId", Label.class , new Label());
		tableAssemble.setColumnWidth("ingFourId",55);
		tableAssemble.addContainerProperty("ingFourQty", Label.class , new Label());
		tableAssemble.setColumnWidth("ingFourQty",55);
		tableAssemble.addContainerProperty("ingFiveId", Label.class , new Label());
		tableAssemble.setColumnWidth("ingFiveId",55);
		tableAssemble.addContainerProperty("ingFiveQty", Label.class , new Label());
		tableAssemble.setColumnWidth("ingFiveQty",55);
		
		tableAssemble.setColumnCollapsed("ingOneId", true);
		tableAssemble.setColumnCollapsed("ingTwoId", true);
		tableAssemble.setColumnCollapsed("ingThreeId", true);
		tableAssemble.setColumnCollapsed("ingFourId", true);
		tableAssemble.setColumnCollapsed("ingFiveId", true);
		tableAssemble.setColumnCollapsed("ingOneQty", true);
		tableAssemble.setColumnCollapsed("ingTwoQty", true);
		tableAssemble.setColumnCollapsed("ingThreeQty", true);
		tableAssemble.setColumnCollapsed("ingFourQty", true);
		tableAssemble.setColumnCollapsed("ingFiveQty", true);

		mainLayout.addComponent(tableAssemble,"top: 315.0px; left: 21.0px;");

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:550.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:575.0px;left:40.0px;");

		return mainLayout;

	}
	private void focusMoveByEnter()
	{
		allComp.add(cmbBatchNo);
		allComp.add(cmbMasterProduct);
		allComp.add(txtFgQty);
		//allComp.add(btnSubmit);
		//allComp.add(txtTrId);
		
		for(int i=0;i<tbRejectQty.size();i++)
		{
			allComp.add(tbRejectQty.get(i));
		}
		allComp.add(btnSubmit);
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnFind);
		allComp.add(cButton.btnExit);
		new FocusMoveByEnter(this,allComp);
	}
	private void tableinitialise()
	{
		for(int i=0; i<10; i++)
		{
			tableRowAdd(i);
		}
	}
	public Iterator dbService(String sql){
		Iterator iter=null;
		Transaction tx = null;
		Session session = null;
		try
		{
			session = SessionFactoryUtil.getInstance().openSession();
			tx = session.beginTransaction();
			iter =session.createSQLQuery(sql).list().iterator();
			return iter;
		}
		catch(Exception exp){

		}
		finally{
			if(tx!=null){
				session.close();
			}
		}
		return iter;
	}
	private void tableClear(){
		for(int a=0;a<tbLblIngradName.size();a++){
			tbtxtIngId.get(a).setValue("");
			tbLblIngradName.get(a).setValue("");
			tbStockQty.get(a).setValue("");
			tbtxtratio.get(a).setValue("");
			tbQty.get(a).setValue("");
			tbRejectQty.get(a).setValue("");
		}
	}
	/*private void tableDataLoad(){
		tableClear();
		String sql="select ingradiantId,ingradinatName,stockQty,ratioQty from funcAssembleLoad('"+cmbMasterProduct.getValue()+"')";
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbtxtIngId.get(a).setValue(element[0]);
			tbLblIngradName.get(a).setValue(element[1]);
			tbStockQty.get(a).setValue(decimalf.format(element[2]));
			tbtxtratio.get(a).setValue(decimalf.format(element[3]));
			a++;
		}
	}*/
	private boolean checkStock()
	{
		if(!isFind)
		{
			double fgQty=Double.parseDouble(txtFgQty.getValue().toString().isEmpty()?"0.0":txtFgQty.getValue().toString());
			for(int a=0;a<tbLblIngradName.size();a++){
				if(!tbLblIngradName.get(a).getValue().toString().isEmpty())
				{
					double tbQty=Double.parseDouble(tbStockQty.get(a).getValue().toString().isEmpty()?"0":tbStockQty.get(a).getValue().toString());
					if(fgQty>tbQty)
					{
						showNotification("Master Product Qty Exceed "+tbLblIngradName.get(a).getValue()+" Stock Qty",Notification.TYPE_WARNING_MESSAGE);
						return false;
					}
				}
			}
		}
		return true;
	}
	private void tableDataLoadIngradiant(String id){
		String sql="select semiFgId,semiFgName,isnull((select assembleStock from funAssembleStockNew(semiFgId,CURRENT_TIMESTAMP)),0)stockqty, "+
				" qty from tbFinishedProductDetailsNew where semiFgId like '%Semi%'and fgId = '"+id+"' and semiFgSubId "+
				" not in(select semiFgSubId from tbFinishedProductDetailsNew where semiFgSubId like '%SemiFgSub%' and fgId='"+id+"' )and consumptionStage='Assemble' "+
				" union "+
				" select semiFgSubId,semiFgSubName,isnull((select assembleStock from funAssembleStockNew(semiFgSubId,CURRENT_TIMESTAMP)),0) "+
				" stockqty,qty from tbFinishedProductDetailsNew where semiFgSubId like '%SemiFgSub%' and fgId='"+id+"' and consumptionStage='Assemble'"
				+"union "
				+"select semiFgId,semiFgName,isnull((select assembleStock from funAssembleStockNew(semiFgId,CURRENT_TIMESTAMP) ),0)stockqty,qty from tbFinishedProductDetailsNew a where fgId = '"+id+"' and semiFgId like '%RI%' ";
		
		System.out.println("sql is:"+sql);
		
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbtxtIngId.get(a).setValue(element[0]);
			tbLblIngradName.get(a).setValue(element[1]);
			tbStockQty.get(a).setValue(df.format(element[2]));
			tbtxtratio.get(a).setValue(element[3]);
			a++;
			if(a==tbtxtIngId.size()){
				tableRowAdd(a);
			}
		}
	}
	private void ingradiantStockLoad(){
		double Qty=Double.parseDouble(txtFgQty.getValue().toString().isEmpty()?"0":txtFgQty.getValue().toString());
		for(int a=0;a<tbLblIngradName.size();a++){
			if(!tbLblIngradName.get(a).getValue().toString().isEmpty()){
				tbQty.get(a).setValue(decimalf.format(Qty));
			}
		}
	}
	private boolean nullCheck(){
		for(int a=0;a<tbAssembleQty.size();a++){
			if(!tbLbMasterProductId.get(a).getValue().toString().isEmpty()){
				if(tbAssembleQty.get(a).getValue().toString().isEmpty()){
					return false;
				}
			}
		}
		return true;
	}
	private void fgNameSelectData(String masterProductId){
		
		String sql="select semiFgSubId,semiFgSubName from tbFinishedProductDetailsNew where fgId='"+masterProductId+"'  "+
				" and  semiFgSubId is not null and semiFgSubId not like 'null' and semiFgSubId not like '' ";
		Iterator<?> iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbFgName.addItem(element[0]);
			cmbFgName.setItemCaption(element[0], element[1].toString());
			cmbFgName.setValue(element[0]);
		}
	}
	public void setEventAction()
	{
		cmbBatchNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbBatchNo.getValue()!=null){
					cmbMasterProductLoad();
				}
				else{
					cmbMasterProduct.removeAllItems();
				}
			}
		});
		cmbMasterProduct.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				tableClear();
				//txtFgQty.setValue("");
				if(cmbMasterProduct.getValue()!=null){
					fgNameSelectData(cmbMasterProduct.getValue().toString());
					tableDataLoadIngradiant(cmbMasterProduct.getValue().toString());
					txtFgQty.focus();
				}
				else{
					cmbFgName.setValue(null);
				}
			}
		});
		txtFgQty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{

				/*Date date1= new Date() ;
				Date date2= (Date) dateField.getValue();
				long seconds = (date1.getTime()-date2.getTime())/1000;
				System.out.println("second is:"+seconds);*/
				
				Date date1= new Date();
				//Date date2= (Date) dProductionDate.getValue();
				String strDate=dateformatNew.format(dateField.getValue());
				DateFormat formatter;
				Date date2 = null;
				formatter = new SimpleDateFormat("yyyy-MM-dd");
				try {
					date2 = formatter.parse(strDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				
				
				long seconds = (date1.getTime()-date2.getTime())/1000;
				System.out.println("second is:"+seconds);
				 
				
				if(checkStock())
				{
					ingradiantStockLoad();
				}
				
				if(!isUpdate)
				{
					if (!sessionBean.getUserId().toString().equalsIgnoreCase("22") && seconds>259199 )
					{
						   txtFgQty.setValue("0.00");
						   for(int a=0;a<tbLblIngradName.size();a++)
						   {
								if(!tbLblIngradName.get(a).getValue().toString().isEmpty())
								{
									tbQty.get(a).setValue("");
								}
							}
						   showNotification("Not Permited TO Entry Time Limit Exceed")	;
						   
							
					}
					else
					{
					  
						
					}
					
				}
				
				
			}
		});
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			
			{
				newButtonEvent();
			}

		});
		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtTrId.getValue().toString().isEmpty()){
					if(!tbAssembleQty.get(0).getValue().toString().isEmpty()){
						if(nullCheck()){
							saveButtonEvent();
						}
						else{
							showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else{
						showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Please Provide Transaction Id",Notification.TYPE_WARNING_MESSAGE);
				}

			}

		});
		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				updateButtonEvent();
			}
		});
		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();

			}
		});
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				isUpdate=true;
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		btnSubmit.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(cmbMasterProduct.getValue()!=null){
				if(checkZero()){
					int a=findBlank();
					tbLbMasterProductId.get(a).setValue(cmbMasterProduct.getValue());
					tbLbMasterProductName.get(a).setValue(cmbMasterProduct.getItemCaption(cmbMasterProduct.getValue()));
					
					if(cmbFgName.getValue()!=null)
					{
						tbLblFgId.get(a).setValue(cmbFgName.getValue());
						tbLblFgName.get(a).setValue(cmbFgName.getItemCaption(cmbFgName.getValue()));	
					}
					else
					{
						tbLblFgId.get(a).setValue("");
						tbLblFgName.get(a).setValue("");	
					}
					
					
					tbAssembleQty.get(a).setValue(txtFgQty.getValue());
					
					tbIngOneId.get(a).setValue(tbtxtIngId.get(0));
					tbIngOneQty.get(a).setValue(tbRejectQty.get(0));
					
					tbIngTwoId.get(a).setValue(tbtxtIngId.get(1));
					tbIngTwoQty.get(a).setValue(tbRejectQty.get(1));
					
					tbIngThreeId.get(a).setValue(tbtxtIngId.get(2));
					tbIngThreeQty.get(a).setValue(tbRejectQty.get(2));
					
					tbIngFourId.get(a).setValue(tbtxtIngId.get(3));
					tbIngFourQty.get(a).setValue(tbRejectQty.get(3));
					
					//tbIngFiveId.get(a).setValue(tbtxtIngId.get(4));
					//tbIngFiveQty.get(a).setValue(tbRejectQty.get(4));
					
					cmbMasterProduct.setValue(null);
					cmbFgName.setValue(null);
					txtFgQty.setValue("");
					txtStockQty.setValue("");
					if(a==tbLbMasterProductId.size()-1){
						tableRowAddAssemble(a+1);
					}
				}
				else{
					showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
				}
				}
				else{
					showNotification("Please Select Master Product.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
	}
	private int findBlank(){
		for(int a=0;a<tblblBatchNo.size();a++){
			if(tbLbMasterProductId.get(a).getValue().toString().isEmpty()){
				return a;
			}
		}
		return 0;
		
	}
	private boolean checkZero(){
		double assembleQty,ingradiantQty;
		assembleQty=Double.parseDouble(txtFgQty.getValue().toString().isEmpty()?"0.0":txtFgQty.getValue().toString());
		for(int x=0;x<tbLblIngradName.size();x++){
			if(!tbtxtIngId.get(x).getValue().toString().isEmpty()){
				ingradiantQty=Double.parseDouble(tbQty.get(x).getValue().toString().isEmpty()?"0.0":tbQty.get(x).getValue().toString());
				if(tbRejectQty.get(x).getValue().toString().isEmpty()){
					showNotification("Please Provide Reject Qty",Notification.TYPE_WARNING_MESSAGE);
					return false;
				}
				if(assembleQty!=ingradiantQty||ingradiantQty<=0){
					return false;
				}
			}
		}
		return true;
	}
	private void findButtonEvent(){
		Window win = new AssembleFindWindow(sessionBean,txtItemID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtItemID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtItemID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}
	private void findInitialise(String id){
		String sql="select distinct masterProductId,masterProductName,assembleQty,assembleDate," +
				"transactionId,secondaryStageFgId,secondaryStageFgName from tbIngradiantAssembleDetails where transactionId='"+id+"'";
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbLbMasterProductId.get(a).setValue(element[0]);
			tbLbMasterProductName.get(a).setValue(element[1]);
			tbLblFgId.get(a).setValue(element[5]);
			tbLblFgName.get(a).setValue(element[6]);
			tbAssembleQty.get(a).setValue(df.format(element[2]));
			if(a==0){
				txtTrId.setValue(element[4]);
				dateField.setValue(element[3]);
			}
			if(a==tbLbMasterProductId.size()-1){
				tableRowAddAssemble(a+1);
			}
			a++;
		}
		for(int x=0;x<tbAssembleQty.size();x++){
			if(!tbLbMasterProductId.get(x).getValue().toString().isEmpty()){
				String sqlReject="select semiFgId,rejectQty from tbIngradiantAssembleDetails where " +
						"transactionId='"+id+"' and masterProductId='"+tbLbMasterProductId.get(x).getValue()+"'";
				Iterator<?>iterReject=dbService(sqlReject);
				int i=0;
				while(iterReject.hasNext()){
					Object elementReject[]=(Object[])iterReject.next();
					if(i==0){
						tbIngOneId.get(x).setValue(elementReject[0]);
						tbIngOneQty.get(x).setValue(elementReject[1]);
						i=1;
					}
					else if(i==1){
						tbIngTwoId.get(x).setValue(elementReject[0]);
						tbIngTwoQty.get(x).setValue(elementReject[1]);
						i=2;
					}
					else if(i==2){
						tbIngThreeId.get(x).setValue(elementReject[0]);
						tbIngThreeQty.get(x).setValue(elementReject[1]);
						i=3;
					}
					else if(i==3){
						tbIngFourId.get(x).setValue(elementReject[0]);
						tbIngFourQty.get(x).setValue(elementReject[1]);
						i=4;
					}
					else if(i==4){
						tbIngFiveId.get(x).setValue(elementReject[0]);
						tbIngFiveQty.get(x).setValue(elementReject[1]);
						break;
					}
				}
				
			}
		}
	}
	private void cmbMasterProductData()
	{
		cmbMasterProduct.removeAllItems();
		String sql="select vProductId,vProductName from tbFinishedProductInfo where " +
				"vCategoryId like '"+cmbBatchNo.getValue()+"'  and vUnitName like '%set%' and isActive=1";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbMasterProduct.addItem(element[0]);
			cmbMasterProduct.setItemCaption(element[0], element[1].toString());
		}
	}
	private void insertData(){
		Session session=null;
		Transaction tx=null;
		try{
			
			System.out.println("Done");
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			String sqlInfo="insert into tbMasterProductAssemble(transactionNo,assembleDate,userId,userIp,userName,entryTime)values "+
					"('"+txtTrId.getValue()+"','"+dateFormat.format(dateField.getValue())+"','"+sessionBean.getUserId()+"'," +
							"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
			session.createSQLQuery(sqlInfo).executeUpdate();
			
			System.out.println("master asembel info:"+sqlInfo);
			int k=1;
			for(int a=0;a<tbLbMasterProductId.size();a++){
				
				
				if(!tbLbMasterProductId.get(a).getValue().toString().isEmpty()){
					
					System.out.println("transaction Id:"+txtTrId.getValue());
					System.out.println("transaction Date:"+dateFormat.format(dateField.getValue()));
					System.out.println("master product id :"+tbLbMasterProductId.get(a).getValue()); 
					System.out.println("master product Name :"+tbLbMasterProductName.get(a).getValue()); 
					System.out.println("fg id :"+tbLblFgId.get(a).getValue()); 
					System.out.println("fg Name :"+tbLblFgName.get(a).getValue()); 
					System.out.println("assemble qty :"+tbAssembleQty.get(a).getValue());
					System.out.println("user Name:"+sessionBean.getUserName());
					System.out.println("user Ip:"+sessionBean.getUserIp()); 
					
					
					String sql="insert into tbMasterProductAssembleInfo(transactionId,assembleDate,masterProductId,masterProductName,secondaryStageFgId,secondaryStageFgName, "+
							" assembleQty,userName,userIP,entryTime)values('"+txtTrId.getValue()+"','"+dateFormat.format(dateField.getValue())+"'," +
							"'"+tbLbMasterProductId.get(a).getValue()+"','"+tbLbMasterProductName.get(a).getValue()+"'," +
							"'"+tbLblFgId.get(a).getValue()+"','"+tbLblFgName.get(a).getValue()+"'," +
							"'"+tbAssembleQty.get(a).getValue()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
					System.out.println(" detaails:"+sql); 
					session.createSQLQuery(sql).executeUpdate();
					String sqlProcedure="exec prcAssembleEntryNew '"+txtTrId.getValue()+"'," +
					"'"+tbLbMasterProductId.get(a).getValue()+"','"+tbLbMasterProductName.get(a).getValue()+"'," +
					"'"+tbLblFgId.get(a).getValue()+"','"+tbLblFgName.get(a).getValue()+"'," +
					"'"+tbAssembleQty.get(a).getValue()+"','"+dateFormat.format(dateField.getValue())+"'," +
					"'"+tbIngOneId.get(a).getValue()+"','"+tbIngOneQty.get(a).getValue()+"'" +
					",'"+tbIngTwoId.get(a).getValue()+"','"+tbIngTwoQty.get(a).getValue()+"'" +
					",'"+tbIngThreeId.get(a).getValue()+"','"+tbIngThreeQty.get(a).getValue()+"'" +
					",'"+tbIngFourId.get(a).getValue()+"','"+tbIngFourQty.get(a).getValue()+"'" +
					",'"+tbIngFiveId.get(a).getValue()+"','"+tbIngFiveQty.get(a).getValue()+"','"+tbAssembleQty.get(a).getValue()+"'";
					
					session.createSQLQuery(sqlProcedure).executeUpdate();
					System.out.println(" detaails:"+k); 
					k++;
					
				}
			}
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("Insert Data: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			}
		}
	}
	private boolean deleteData(){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			session.createSQLQuery("delete from tbMasterProductAssemble where transactionNo='"+txtTrId.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete from tbIngradiantAssembleDetails where transactionId='"+txtTrId.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete from tbMasterProductAssembleInfo where transactionId='"+txtTrId.getValue()+"'").executeUpdate();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("Delete Data: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				//showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
				return true;
			}
		}
		return false;
	}
	private void saveButtonEvent() 
	{

		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(deleteData()){
							insertData();
							isUpdate = false;
							btnIni(true);
							componentIni(true);
							txtClear();
							cButton.btnNew.focus();
						}
					}
				}
			});																	
		}
		else
		{									
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						/*isUpdate = false;
						btnIni(true);
						componentIni(true);
						txtClear();*/
					}
				}
			});
		}
	}
	private void refreshButtonEvent() {

		componentIni(true);
		btnIni(true);
		txtClear();
		isUpdate=false;
		isFind=false;
	}
	private void updateButtonEvent(){

		if(!tbAssembleQty.get(0).getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
	}


	private void newButtonEvent(){

		componentIni(false);
		btnIni(false);
		txtClear();
		transactionNoLoad();
		isUpdate=false;
		isFind=false;
		cmbMasterProduct.focus();
	}
	public void txtClear(){

		cmbBatchNo.setValue(null);
		cmbMasterProduct.setValue(null);
		cmbFgName.setValue(null);
		txtFgQty.setValue("");
		txtTrId.setValue("");
		txtStockQty.setValue("");
		for(int i=0;i<tbQty.size();i++)
		{
			tbLblIngradName.get(i).setValue("");
			tbStockQty.get(i).setValue("");
			tbtxtratio.get(i).setValue("");
			tbQty.get(i).setValue("");
			tbtxtIngId.get(i).setValue("");
			tbRejectQty.get(i).setValue("");
		}
		for(int i=0;i<tbAssembleQty.size();i++)
		{
			tbChkEdit.get(i).setValue(false);
			tbLbMasterProductId.get(i).setValue("");
			tbLbMasterProductName.get(i).setValue("");
			tbLblFgId.get(i).setValue("");
			tbLblFgName.get(i).setValue("");
			tbAssembleQty.get(i).setValue("");
			
			tbIngOneId.get(i).setValue("");
			tbIngOneQty.get(i).setValue("");
			tbIngTwoId.get(i).setValue("");
			tbIngTwoQty.get(i).setValue("");
			tbIngThreeId.get(i).setValue("");
			tbIngThreeQty.get(i).setValue("");
			tbIngFourId.get(i).setValue("");
			tbIngFourQty.get(i).setValue("");
			tbIngFiveId.get(i).setValue("");
			tbIngFiveQty.get(i).setValue("");
		}

	}

	private void componentIni(boolean b) {

		table.setEnabled(!b);
		tableAssemble.setEnabled(!b);
		btnSubmit.setEnabled(!b);
		cmbBatchNo.setEnabled(!b);
		cmbFgName.setEnabled(!b);
		cmbMasterProduct.setEnabled(!b);
		dateField.setEnabled(!b);
		txtTrId.setEnabled(!b);
		txtStockQty.setEnabled(!b);
		txtFgQty.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnFind.setEnabled(t);

	}
	public void tableInitialiseAssemble()
	{
		for(int i=0;i<7;i++)
		{
			tableRowAddAssemble(i);
		}
	}
	private void tableClearAssemble(int a){

			tbChkEdit.get(a).setValue(false);
			tbLbMasterProductId.get(a).setValue("");
			tbLbMasterProductName.get(a).setValue("");
			tblblBatchNo.get(a).setValue("");
			tbAssembleQty.get(a).setValue("");
			tbIngOneId.get(a).setValue("");
			tbIngOneQty.get(a).setValue("");
			tbIngTwoId.get(a).setValue("");
			tbIngTwoQty.get(a).setValue("");
			tbIngThreeId.get(a).setValue("");
			tbIngThreeQty.get(a).setValue("");
			tbIngFourId.get(a).setValue("");
			tbIngFourQty.get(a).setValue("");
			tbIngFiveId.get(a).setValue("");
			tbIngFiveQty.get(a).setValue("");
	}
	private void rejectionLoadData(){
		String sql="select semiFgId,rejectQty from tbIngradiantAssembleDetails where transactionId='"+txtTrId.getValue()+"' ";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			for(int a=0;a<tbRejectQty.size();a++){
				if(element[0].toString().equalsIgnoreCase(tbtxtIngId.get(a).getValue().toString())){
					tbRejectQty.get(a).setValue(df.format(element[1]));
				}
			}
		}
	}
	public void tableRowAddAssemble(final int ar)
	{
		try
		{
			tbslAssemble.add(ar,new Label());
			tbslAssemble.get(ar).setWidth("100%");
			tbslAssemble.get(ar).setImmediate(true);                                                                                                        
			tbslAssemble.get(ar).setValue(ar+1);

			tbChkEdit.add(ar, new CheckBox());
			tbChkEdit.get(ar).setImmediate(true);
			tbChkEdit.get(ar).addListener(new ValueChangeListener() {
				
				public void valueChange(ValueChangeEvent event) {
					if(tbChkEdit.get(ar).booleanValue()&&!tbLbMasterProductId.get(ar).getValue().toString().isEmpty()){
						cmbMasterProduct.setValue(tbLbMasterProductId.get(ar).getValue());
						cmbFgName.setValue(tbLblFgId.get(ar).getValue());
						
						txtFgQty.setValue(tbAssembleQty.get(ar).getValue());
						tableClear();
						tableDataLoadIngradiant(tbLbMasterProductId.get(ar).getValue().toString());
						ingradiantStockLoad();
						tableClearAssemble(ar);
						if(isFind){
							rejectionLoadData();
						}
					}
					else if(tbLbMasterProductId.get(ar).getValue().toString().isEmpty()){
						showNotification("There is no data",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			});

			tbLbMasterProductName.add(ar,new Label ());
			tbLbMasterProductName.get(ar).setWidth("100%"); 
			tbLbMasterProductName.get(ar).setImmediate(true);

			tbAssembleQty.add(ar,new TextRead(1));
			tbAssembleQty.get(ar).setWidth("100%");
			tbAssembleQty.get(ar).setImmediate(true);

			tbLbMasterProductId.add(ar,new TextRead(1));
			tbLbMasterProductId.get(ar).setWidth("100%");
			tbLbMasterProductId.get(ar).setImmediate(true);
			
			tbLblFgId.add(ar,new TextRead(1));
			tbLblFgId.get(ar).setWidth("100%");
			tbLblFgId.get(ar).setImmediate(true);
			
			tbLblFgName.add(ar,new Label ());
			tbLblFgName.get(ar).setWidth("100%"); 
			tbLblFgName.get(ar).setImmediate(true);

			tblblBatchNo.add(ar,new TextRead(1));
			tblblBatchNo.get(ar).setWidth("100%");
			tblblBatchNo.get(ar).setImmediate(true);

			tbIngOneId.add(ar,new Label());
			tbIngOneId.get(ar).setWidth("100%");
			
			tbIngOneQty.add(ar,new Label());
			tbIngOneQty.get(ar).setWidth("100%");
			
			tbIngTwoId.add(ar,new Label());
			tbIngTwoId.get(ar).setWidth("100%");
			
			tbIngTwoQty.add(ar,new Label());
			tbIngTwoQty.get(ar).setWidth("100%");
			
			tbIngThreeId.add(ar,new Label());
			tbIngThreeId.get(ar).setWidth("100%");
			
			tbIngThreeQty.add(ar,new Label());
			tbIngThreeQty.get(ar).setWidth("100%");
			
			tbIngFourId.add(ar,new Label());
			tbIngFourId.get(ar).setWidth("100%");
			
			tbIngFourQty.add(ar,new Label());
			tbIngFourQty.get(ar).setWidth("100%");
			
			tbIngFiveId.add(ar,new Label());
			tbIngFiveId.get(ar).setWidth("100%");
			
			tbIngFiveQty.add(ar,new Label());
			tbIngFiveQty.get(ar).setWidth("100%");
			
			tableAssemble.addItem(new Object[]{ tbslAssemble.get(ar),tbChkEdit.get(ar),tbLbMasterProductName.get(ar),
					tbAssembleQty.get(ar),tbLbMasterProductId.get(ar),tbLblFgId.get(ar),tbLblFgName.get(ar),tblblBatchNo.get(ar),tbIngOneId.get(ar),tbIngOneQty.get(ar),
					tbIngTwoId.get(ar),tbIngTwoQty.get(ar),tbIngThreeId.get(ar),tbIngThreeQty.get(ar),tbIngFourId.get(ar),
					tbIngFourQty.get(ar),tbIngFiveId.get(ar),tbIngFiveQty.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	public void tableInitialise()
	{
		for(int i=0;i<4;i++)
		{
			tableRowAdd(i);
		}
	}
	public void tableRowAdd(final int ar)
	{
		try
		{
			tbsl.add(ar,new Label());
			tbsl.get(ar).setWidth("100%");
			tbsl.get(ar).setImmediate(true);                                                                                                        
			tbsl.get(ar).setValue(ar+1);

			tbLblIngradName.add(ar,new Label ());
			tbLblIngradName.get(ar).setWidth("100%"); 
			tbLblIngradName.get(ar).setImmediate(true);

			tbStockQty.add(ar,new TextRead(1));
			tbStockQty.get(ar).setWidth("100%");
			tbStockQty.get(ar).setImmediate(true);

			tbQty.add(ar,new TextRead(1));
			tbQty.get(ar).setWidth("100%");
			tbQty.get(ar).setImmediate(true);
			
			tbRejectQty.add(ar,new AmountField());
			tbRejectQty.get(ar).setWidth("100%");
			tbRejectQty.get(ar).setImmediate(true);

			tbtxtratio.add(ar,new TextRead(1));
			tbtxtratio.get(ar).setWidth("100%");
			tbtxtratio.get(ar).setImmediate(true);

			tbtxtIngId.add(ar,new Label());
			tbtxtIngId.get(ar).setWidth("100%");
			tbtxtIngId.get(ar).setImmediate(true);

			table.addItem(new Object[]{ tbsl.get(ar),tbLblIngradName.get(ar),tbStockQty.get(ar),tbtxtratio.get(ar),tbQty.get(ar),
					tbRejectQty.get(ar),tbtxtIngId.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

}
