package com.example.sparePartsTransaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.CommonButtonNew;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExample;
import com.common.share.ImmediateUploadExampleNew;
import com.common.share.MessageBox;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

public class Raw_OpeningStock extends Window 

{

	SessionBean sessionBean;

	private Label lblOpeningYear;
	private Label lblTransactionNo;
	private Label lblGroupName;
	private Label lblItemName;
	private Label lblProductCode;
	private Label lblSubItemName;
	private Label lblSpecification;
	private Label lblQty;
	private Label lblRate;
	private Label lblAmount;
	private Label lblStore;
	private Label lblRack;
	private Label lblShelf;
	private Label lblUnit;

	private Label lblGroupNameFind;
	private Label lblItemNameFind;
	private Label lblProductCodeFind;

	private ComboBox cmbGroup;
	private ComboBox cmbItem;
	private ComboBox cmbProductCode;
	private TextRead txtSpecification;
	private ComboBox cmbStore;
	private ComboBox cmbRack;
	private TextRead txtUnit;
	private ComboBox cmbShelf;

	private ComboBox cmbGroupFind;
	private ComboBox cmbItemFind;
	private ComboBox cmbProductCodeFind;

	private InlineDateField dOpeningYear;

	private TextRead txtTransactionNo;
	private TextRead txtSubItemName;
	private AmountField aQty;
	private AmountField aRate;
	private TextRead txtAmount;
	private NativeButton btnSave;
	private NativeButton btnFind;

	//private ImmediateUploadExample poAttach;

	private ImmediateUploadExampleNew poAttach;

	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<Label> tblblGroupCode = new ArrayList<Label>();
	private ArrayList<Label> tblblGroup = new ArrayList<Label>();
	private ArrayList<Label> tblblItemCode = new ArrayList<Label>();
	private ArrayList<Label> tblblItem = new ArrayList<Label>();
	private ArrayList<Label> tblblProductCode = new ArrayList<Label>();
	private ArrayList<Label> tblblSubItem = new ArrayList<Label>();
	private ArrayList<Label> tblblSpecificationCode= new ArrayList<Label>();
	private ArrayList<Label> tblblSpecification = new ArrayList<Label>();
	private ArrayList<Label> tblblQty= new ArrayList<Label>();
	private ArrayList<Label> tblblRate= new ArrayList<Label>();
	private ArrayList<Label> tblblAmount = new ArrayList<Label>();
	private ArrayList<Label> tblblStoreId = new ArrayList<Label>();
	private ArrayList<Label> tblblStore = new ArrayList<Label>();
	private ArrayList<Label> tblblRackId = new ArrayList<Label>();
	private ArrayList<Label> tblblRack= new ArrayList<Label>();
	private ArrayList<Label> tblblShelfId = new ArrayList<Label>();
	private ArrayList<Label> tblblShelf= new ArrayList<Label>();
	private ArrayList<Label> tblbltransactionNo= new ArrayList<Label>();
	private ArrayList<Label> tblblUnit= new ArrayList<Label>();
	private ArrayList<CheckBox>tbChkShow= new ArrayList<CheckBox>();

	double totalsum = 0.0;
	private Formatter fmt = new Formatter();
	private TextRead txttotalField = new TextRead();
	boolean isUpdate=false,isFind=false;
	String udFlag;
	//private HashMap supplierAddress=new HashMap();

	String strFlag;
	private Label lbLine;
	private TextField txtReceiptId=new TextField();
	private DecimalFormat df = new DecimalFormat("#0.00");
	private Label lblSpecCode=new Label();

	private CommonButtonNew cButton=new CommonButtonNew( "New",  "",  "",  "",  "Refresh",  "", "", "Exit","","Preview");
	private static final String[] types = new String[] { "Local Purchase", "Against PO" };
	private AbsoluteLayout mainLayout;
	String filePathTmpReq= "";
	String imageLoc= "0";
	HashMap<String,String> hMapSubItemName=new HashMap<String,String>();
	HashMap<String,String> hMapUnit=new HashMap<String,String>();
	HashMap<String,String> hMapSpecCode=new HashMap<String,String>();
	HashMap<String,String> hMapSpecName=new HashMap<String,String>();

	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	public Raw_OpeningStock(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("OPENING STOCK :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		setEventAction();
		btnIni(true);
		componentIni(true);
		txtClear();
		FocusMove();
	}
	private Iterator<?> dbService(String sql){
		Transaction tx=null;
		Session session=null;
		Iterator<?> iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			iter=session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){

				session.close();
			}
		}
		return iter;
	}
	private void amountCalc(){
		double qty=Double.parseDouble( aQty.getValue().toString().isEmpty()?"0.0":aQty.getValue().toString());
		double rate=Double.parseDouble( aRate.getValue().toString().isEmpty()?"0.0":aRate.getValue().toString());
		double amount=qty*rate;
		txtAmount.setValue(df.format(amount));
	}
	private void cmbGroupDataLoad(){

		Iterator<?>iter=dbService("select Group_Id,vGroupName from tbGroupInformation");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbGroup.addItem(element[0]);
			cmbGroup.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbStoreDataLoad(){

		Iterator<?>iter=dbService("select vDepoId,vDepoName from tbDepoInformation");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbStore.addItem(element[0]);
			cmbStore.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbItemDataLoad(){
		cmbItem.removeAllItems();
		Iterator<?>iter=dbService("select SubGroup_Id,vItemName from tbItemInfosub where Group_Id='"+cmbGroup.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbItem.addItem(element[0]);
			cmbItem.setItemCaption(element[0], element[1].toString());
		}
	}
	private void FocusMove(){
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(txtTransactionNo);
		allComp.add(cmbGroup);
		allComp.add(cmbItem);
		allComp.add(cmbProductCode);
		allComp.add(txtSubItemName);
		allComp.add(txtUnit);
		allComp.add(txtSpecification);
		allComp.add(aQty);
		allComp.add(aRate);
		//allComp.add(txtAmount);
		allComp.add(cmbStore);
		allComp.add(cmbRack);
		allComp.add(cmbShelf);

		/*allComp.add(cButton.btnSave);
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnUpdate);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);*/
		allComp.add(btnSave);
		allComp.add(cmbGroupFind);
		allComp.add(cmbItemFind);
		allComp.add(cmbProductCodeFind);
		allComp.add(btnFind);
		

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1200px");
		mainLayout.setHeight("560px");

		lblOpeningYear = new Label();
		lblOpeningYear.setImmediate(false);
		lblOpeningYear.setWidth("-1px");
		lblOpeningYear.setHeight("-1px");
		lblOpeningYear.setValue("Opening Year :");
		mainLayout.addComponent(lblOpeningYear, "top:20.0px;left:20.0px;");

		dOpeningYear = new InlineDateField();
		dOpeningYear.setImmediate(true);
		dOpeningYear.setDateFormat("yyyy");
		dOpeningYear.setWidth("-1px");
		dOpeningYear.setHeight("-1px");
		dOpeningYear.setInvalidAllowed(false);
		dOpeningYear.setResolution(6);
		mainLayout.addComponent(dOpeningYear, "top:18.0px;left:110.0px;");

		lblTransactionNo= new Label();
		lblTransactionNo.setImmediate(false);
		lblTransactionNo.setWidth("-1px");
		lblTransactionNo.setHeight("-1px");
		lblTransactionNo.setValue("Transaction No:");
		mainLayout.addComponent(lblTransactionNo, "top:45.0px;left:20.0px;");

		txtTransactionNo = new TextRead(1);
		txtTransactionNo.setImmediate(true);
		txtTransactionNo.setWidth("105px");
		txtTransactionNo.setHeight("-1px");
		mainLayout.addComponent(txtTransactionNo, "top:43.0px;left:110.0px;");

		lblGroupName = new Label();
		lblGroupName.setImmediate(false);
		lblGroupName.setWidth("-1px");
		lblGroupName.setHeight("-1px");
		lblGroupName.setValue("Group Name :");
		mainLayout.addComponent(lblGroupName, "top:70.0px;left:20.0px;");

		cmbGroup = new ComboBox();
		cmbGroup .setImmediate(true);
		cmbGroup .setWidth("240px");
		cmbGroup .setHeight("-1px");
		cmbGroup .setNullSelectionAllowed(true);
		cmbGroup.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbGroup, "top:68.0px;left:110.0px;");

		lblItemName= new Label();
		lblItemName.setImmediate(false);
		lblItemName.setWidth("-1px");
		lblItemName.setHeight("-1px");
		lblItemName.setValue("Item Name :");
		mainLayout.addComponent(lblItemName, "top:95.0px;left:20.0px;");

		cmbItem = new ComboBox();
		cmbItem .setImmediate(true);
		cmbItem.setWidth("250px");
		cmbItem.setHeight("-1px");
		cmbItem .setNullSelectionAllowed(true);
		cmbItem .setNewItemsAllowed(false);
		mainLayout.addComponent(cmbItem, "top:93.0px;left:110.0px;");

		lblProductCode= new Label();
		lblProductCode.setImmediate(false);
		lblProductCode.setWidth("-1px");
		lblProductCode.setHeight("-1px");
		lblProductCode.setValue("Product Code :");
		mainLayout.addComponent(lblProductCode, "top:120.0px;left:20.0px;");

		cmbProductCode = new ComboBox();
		cmbProductCode.setImmediate(true);
		cmbProductCode.setWidth("150px");
		cmbProductCode.setHeight("-1px");
		mainLayout.addComponent(	cmbProductCode, "top:120.0px;left:110.0px;");

		/*lblSubItemName = new Label();
		lblSubItemName .setImmediate(false);
		lblSubItemName .setWidth("-1px");
		lblSubItemName .setHeight("-1px");
		lblSubItemName .setValue("Sub Item Name :");
		mainLayout.addComponent(lblSubItemName , "top:45.0px;left:830.0px;");*/

		txtSubItemName = new TextRead(1);
		txtSubItemName .setImmediate(true);
		txtSubItemName .setWidth("140px");
		txtSubItemName .setHeight("-1px");
		//txtSubItemName .setRows(1);
		mainLayout.addComponent(	txtSubItemName, "top:122.0px;left:270.0px;");

		/*lblUnit= new Label();
		lblUnit .setImmediate(false);
		lblUnit .setWidth("-1px");
		lblUnit .setHeight("-1px");
		lblUnit .setValue("Unit:");
		mainLayout.addComponent(lblUnit , "top:45.0px;left:1075.0px;");*/

		txtUnit = new TextRead(1);
		txtUnit  .setImmediate(true);
		txtUnit  .setWidth("50px");
		txtUnit  .setHeight("-1px");
		//txtSubItemName .setRows(1);
		mainLayout.addComponent(	txtUnit , "top:122.0px;left:420.0px;");
		/*	
		lblSpecification= new Label();
		lblSpecification .setImmediate(false);
		lblSpecification .setWidth("-1px");
		lblSpecification .setHeight("-1px");
		lblSpecification .setValue("Specification:");
		mainLayout.addComponent(lblSpecification , "top:69.0px;left:20.0px;");*/

		txtSpecification = new TextRead(1);
		txtSpecification  .setImmediate(true);
		txtSpecification  .setWidth("140px");
		txtSpecification  .setHeight("-1px");
		//txtSubItemName .setRows(1);
		mainLayout.addComponent(	txtSpecification , "top:122.0px;left:480.0px;");

		lblQty= new Label();
		lblQty.setImmediate(false);
		lblQty.setWidth("-1px");
		lblQty.setHeight("-1px");
		lblQty.setValue("Qty :");
		mainLayout.addComponent(lblQty, "top:20.0px;left:380.0px;");

		aQty= new AmountField();
		aQty.setImmediate(true);
		aQty.setWidth("100px");
		aQty.setHeight("-1px");
		mainLayout.addComponent(	aQty, "top:18.0px;left:440.0px;");

		lblRate = new Label();
		lblRate.setImmediate(false);
		lblRate .setWidth("-1px");
		lblRate .setHeight("-1px");
		lblRate.setValue("Rate :");
		mainLayout.addComponent(lblRate , "top:45.0px;left:380.0px;");

		aRate= new AmountField();
		aRate.setImmediate(true);
		aRate.setWidth("100px");
		aRate.setHeight("-1px");
		mainLayout.addComponent(	aRate, "top:43.0px;left:440.0px;");

		lblAmount = new Label();
		lblAmount.setImmediate(false);
		lblAmount .setWidth("-1px");
		lblAmount .setHeight("-1px");
		lblAmount.setValue("Amount :");
		mainLayout.addComponent(lblAmount , "top:70.0px;left:380.0px;");

		txtAmount= new TextRead(1);
		txtAmount.setImmediate(true);
		txtAmount.setWidth("100px");
		txtAmount.setHeight("-1px");
		mainLayout.addComponent(	txtAmount, "top:68.0px;left:440.0px;");

		lblStore= new Label();
		lblStore.setImmediate(false);
		lblStore.setWidth("-1px");
		lblStore.setHeight("-1px");
		lblStore.setValue("Store :");
		mainLayout.addComponent(lblStore, "top:20.0px;left:575.0px;");

		cmbStore = new ComboBox();
		cmbStore.setImmediate(true);
		cmbStore.setWidth("190px");
		cmbStore.setHeight("-1px");
		cmbStore.setNullSelectionAllowed(true);
		cmbStore.setNewItemsAllowed(false);
		mainLayout.addComponent(		cmbStore, "top:18.0px;left:620.0px;");

		lblRack= new Label();
		lblRack.setImmediate(false);
		lblRack.setWidth("-1px");
		lblRack.setHeight("-1px");
		lblRack.setValue("Rack :");
		mainLayout.addComponent(lblRack, "top:45.0px;left:575.0px;");

		cmbRack = new ComboBox();
		cmbRack.setImmediate(true);
		cmbRack.setWidth("190px");
		cmbRack.setHeight("-1px");
		cmbRack.setNullSelectionAllowed(true);
		cmbRack.setNewItemsAllowed(false);
		mainLayout.addComponent(		cmbRack, "top:43.0px;left:620.0px;");

		lblShelf= new Label();
		lblShelf.setImmediate(false);
		lblShelf.setWidth("-1px");
		lblShelf.setHeight("-1px");
		lblShelf.setValue("Shelf :");
		mainLayout.addComponent(lblShelf, "top:70.0px;left:575.0px;");

		cmbShelf = new ComboBox();
		cmbShelf .setImmediate(true);
		cmbShelf .setWidth("190px");
		cmbShelf .setHeight("-1px");
		cmbShelf .setNullSelectionAllowed(true);
		cmbShelf .setNewItemsAllowed(false);
		mainLayout.addComponent(	cmbShelf , "top:68.0px;left:620.0px;");

		btnSave= new NativeButton("Save");
		btnSave.setImmediate(false);
		btnSave.setIcon(new ThemeResource("../icons/update1.png"));
		btnSave.setImmediate(true);
		btnSave.setWidth("115px");
		btnSave.setHeight("24px");
		mainLayout.addComponent(btnSave, "top:100.0px;left:640.0px;");

		Label lblLine1 = new Label("<b><font color='#ffffff'>=================FIND DATA=================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine1, "top:20.0px;left:840.0px;");

		lblGroupNameFind = new Label();
		lblGroupNameFind.setImmediate(false);
		lblGroupNameFind.setWidth("-1px");
		lblGroupNameFind.setHeight("-1px");
		lblGroupNameFind.setValue("Group Name :");
		mainLayout.addComponent(lblGroupNameFind, "top:45.0px;left:840.0px;");

		cmbGroupFind = new ComboBox();
		cmbGroupFind .setImmediate(true);
		cmbGroupFind .setWidth("240px");
		cmbGroupFind .setHeight("-1px");
		cmbGroupFind .setNullSelectionAllowed(true);
		cmbGroupFind.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbGroupFind, "top:43.0px;left:920.0px;");

		lblItemNameFind= new Label();
		lblItemNameFind.setImmediate(false);
		lblItemNameFind.setWidth("-1px");
		lblItemNameFind.setHeight("-1px");
		lblItemNameFind.setValue("Item Name :");
		mainLayout.addComponent(lblItemNameFind, "top:70.0px;left:840.0px;");

		cmbItemFind = new ComboBox();
		cmbItemFind .setImmediate(true);
		cmbItemFind.setWidth("250px");
		cmbItemFind.setHeight("-1px");
		cmbItemFind .setNullSelectionAllowed(true);
		cmbItemFind .setNewItemsAllowed(false);
		mainLayout.addComponent(cmbItemFind, "top:68.0px;left:920.0px;");

		lblProductCodeFind= new Label();
		lblProductCodeFind.setImmediate(false);
		lblProductCodeFind.setWidth("-1px");
		lblProductCodeFind.setHeight("-1px");
		lblProductCodeFind.setValue("Product Code :");
		mainLayout.addComponent(lblProductCodeFind, "top:95.0px;left:840.0px;");

		cmbProductCodeFind = new ComboBox();
		cmbProductCodeFind.setImmediate(true);
		cmbProductCodeFind.setWidth("150px");
		cmbProductCodeFind.setHeight("-1px");
		mainLayout.addComponent(	cmbProductCodeFind, "top:93.0px;left:920.0px;");

		btnFind= new NativeButton("FIND");
		btnFind.setImmediate(false);
		btnFind.setIcon(new ThemeResource("../icons/update1.png"));
		btnFind.setImmediate(true);
		btnFind.setWidth("90px");
		btnFind.setHeight("24px");
		mainLayout.addComponent(btnFind, "top:95.0px;left:1080.0px;");

		Label lblLine2 = new Label("<b><font color='#ffffff'>=================FIND DATA=================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine2, "top:120.0px;left:840.0px;");

		table = new Table();
		table.setWidth("99%");
		table.setHeight("350px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);
		//table.setColumnHeaderMode()

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 15);

		table.addContainerProperty("Group Code",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Group Code",30);
		table.setColumnCollapsed("Group Code", true);

		table.addContainerProperty("Group Name",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Group Name",140);

		table.addContainerProperty("Item Code",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Item Code",20);
		table.setColumnCollapsed("Item Code",true);

		table.addContainerProperty("Item Name",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Item Name",140);

		/*table.addContainerProperty("Item Code",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Item Code",20);
		table.setColumnCollapsed("Item Code",true);*/

		table.addContainerProperty("Code",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Code",100);

		table.addContainerProperty("Sub Item Name",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Sub Item Name",80);

		table.addContainerProperty("Spec Code",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Spec Code",20);
		table.setColumnCollapsed("Spec Code",true);

		table.addContainerProperty("Specification", Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Specification",70);

		table.addContainerProperty("Qty",  TextRead.class , new  TextRead(1),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Qty",60);

		table.addContainerProperty("Rate",  TextRead.class , new  TextRead(1),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Rate",50);

		table.addContainerProperty("Amount",  TextRead.class , new  TextRead(1),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Amount",70);

		table.addContainerProperty("Store Id",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Store Id",20);
		table.setColumnCollapsed("Store Id",true);

		table.addContainerProperty("Store",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Store",80);

		table.addContainerProperty("Rack Id",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Rack Id",40);
		table.setColumnCollapsed("Rack Id",true);

		table.addContainerProperty("Rack",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Rack",80);

		table.addContainerProperty("Shelf Id",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Shelf Id",40);
		table.setColumnCollapsed("Shelf Id",true);

		table.addContainerProperty("Shelf",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Shelf",80);

		table.addContainerProperty("Edit", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Edit", 25);

		table.addContainerProperty("TransactionNo",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("TransactionNo",80);
		table.setColumnCollapsed("TransactionNo",true);

		table.addContainerProperty("Unit",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Unit",80);
		table.setColumnCollapsed("Unit",true);

		table.setColumnCollapsingAllowed(true);
		tableInitialise();
		mainLayout.addComponent(table, "top:155.0px;left:5.0px;");

		//	lbLine = new Label("_______________________________________________________________________________________________________________________________________________________________________________________________________________________");
		lbLine=new Label("<b><font color='#e65100'>=======================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lbLine, "top:500.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:520.0px;left:445.0px;");

		return mainLayout;
	}

	/*private void tableclear()

	{

		for(int i=0;i<txtUnit.size();i++){
			//cmbProduct.get(i).setValue("x#"+i);
		//	cmbProduct.get(i).setValue(null);
			//unit.get(i).setValue("");
			//lblSection.get(i).setValue("");
			aAmount.get(i).setValue();
			aRate.get(i).setValue("");
			aQty.get(i).setValue("");

		}


	}*/

	public void setEventAction()
	{

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				udFlag="New";
				newButtonEvent();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isSubmitable())
				{
					//saveButtonEvent();
				}
				else{
					showNotification("Warning","You are not permitted to save date",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnUpdate.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				udFlag="Update";
				if(sessionBean.isUpdateable())
				{
					//updateButtonEvent();
				}
				else
				{
					showNotification("Warning,","You are not permitted to edit data.",Notification.TYPE_WARNING_MESSAGE);
				}	
			}
		});

		cButton.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				udFlag="Delete";
				if(sessionBean.isDeleteable())
				{
					//deleteButtonEvent();
				}
				else
				{
					showNotification("Warning,","You are not permitted to delete data.",Notification.TYPE_WARNING_MESSAGE);
				}

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
				//findButtonEvent();
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		cmbGroup.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbGroup.getValue()!=null){
					cmbItemDataLoad();
				}
				else{
					cmbItem.removeAllItems();
				}
			}
		});
		cmbItem.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbItem.getValue()!=null){
					cmbProductCodeLoad();
				}
				else{
					cmbProductCode.removeAllItems();
				}
			}
		});
		cmbProductCode.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				aQty.setValue("");
				aRate.setValue("");
				txtAmount.setValue("");
				if(cmbProductCode.getValue()!=null){
					if(!isFind){
						txtSubItemName.setValue(hMapSubItemName.get(cmbProductCode.getValue()));
						txtUnit.setValue(hMapUnit.get(cmbProductCode.getValue()));
						txtSpecification.setValue(hMapSpecName.get(cmbProductCode.getValue()));
						lblSpecCode.setValue(hMapSpecCode.get(cmbProductCode.getValue()));
					}
				}
				else{
					txtSubItemName.setValue("");
					txtUnit.setValue("");
					txtSpecification.setValue("");

				}
			}
		});
		aQty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				amountCalc();
			}
		});
		aRate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				amountCalc();
			}
		});
		cmbStore.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbStore.getValue()!=null){
					cmbRackDataLoad();
				}
				else{
					cmbRack.removeAllItems();
				}
			}
		});
		cmbRack.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbRack.getValue()!=null){
					cmbShelfDataLoad();
				}
				else{
					cmbShelf.removeAllItems();
				}
			}
		});
		btnSave.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				saveButtonEvent();
			}
		});
		btnFind.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});
		cmbGroupFind.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbGroupFind.getValue()!=null){
					cmbItemDataLoadFind();
				}
				else{
					cmbItemFind.removeAllItems();
				}
			}
		});
		cmbItemFind.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbItemFind.getValue()!=null){
					cmbProductDataLoadFind();
				}
				else{
					cmbProductCodeFind.removeAllItems();
				}
			}
		});
	}
	private void cmbProductDataLoadFind(){
		cmbProductCodeFind.removeAllItems();
		Iterator<?>iter=dbService("select distinct vProductCode from tbItemOpening where vGroupId like '"+cmbGroupFind.getValue()+"' and vItemId like '"+cmbItemFind.getValue()+"'");
		while(iter.hasNext()){
			cmbProductCodeFind.addItem(iter.next());
		}
	}
	private void cmbItemDataLoadFind(){
		cmbItemFind.removeAllItems();
		Iterator<?>iter=dbService("select distinct vItemId,vItemName from tbItemOpening where vGroupId like '"+cmbGroupFind.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbItemFind.addItem(element[0]);
			cmbItemFind.setItemCaption(element[0], element[1].toString());
		}
	}
	private boolean findCheckValidation(){
		if(cmbGroupFind.getValue()!=null){
			if(cmbItemFind.getValue()!=null){
				if(cmbProductCodeFind.getValue()!=null){
					return true;
				}
				else{
					showNotification("Please Provide Product Code Find",Notification.TYPE_WARNING_MESSAGE);
					cmbProductCodeFind.focus();
				}
			}
			else{
				showNotification("Please Provide Item Name Find",Notification.TYPE_WARNING_MESSAGE);
				cmbItemFind.focus();
			}
		}
		else{
			showNotification("Please Provide Group Name Find",Notification.TYPE_WARNING_MESSAGE);
			cmbGroupFind.focus();
		}
		return false;
	}
	private void findButtonEvent(){
		if(findCheckValidation()){
			txtClear();
			tableClear();
			tableDataLoadFromFind();
			findClear();
		}
	}
	private void tableDataLoadFromFind(){
		String sql="select vGroupId,vGroupName,vItemId,vItemName,vProductCode,vSubItemName,vUnit,vSpecCode,vSpecName, "+
				" mQty,mRate,mAmount,vStoreId,vStoreName,vReckId,vReckName,vShelfId,vShelfName,iTransactionNo, "+
				" vOpeningYear from tbItemOpening where vProductCode like '"+cmbProductCodeFind.getValue()+"'";
		int ar=findBlankRow();
		Iterator<?>iter=dbService(sql);
		//int ar=0;
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(ar==tblblAmount.size()-1){
				tableRowAdd(ar+1);
			}
			tblblGroupCode.get(ar).setValue(element[0]);
			tblblGroup.get(ar).setValue(element[1]);
			tblblItemCode.get(ar).setValue(element[2]);
			tblblItem.get(ar).setValue(element[3]);
			tblblProductCode.get(ar).setValue(element[4]);
			tblblSubItem.get(ar).setValue(element[5]);
			tblblUnit.get(ar).setValue(element[6]);
			tblblSpecificationCode.get(ar).setValue(element[7]);
			tblblSpecification.get(ar).setValue(element[8]);
			tblblQty.get(ar).setValue(df.format(element[9]));
			tblblRate.get(ar).setValue(df.format(element[10]));
			tblblAmount.get(ar).setValue(df.format(element[11]));
			if(element[12]!=null&&element[13]!=null){
				tblblStoreId.get(ar).setValue(element[12]);
				tblblStore.get(ar).setValue(element[13]);
			}
			if(element[14]!=null&&element[15]!=null){
				tblblRackId.get(ar).setValue(element[14]);
				tblblRack.get(ar).setValue(element[15]);
			}
			if(element[16]!=null&&element[17]!=null){
				tblblShelfId.get(ar).setValue(element[16]);
				tblblShelf.get(ar).setValue(element[17]);
			}
			tblbltransactionNo.get(ar).setValue(element[18]);
		}
	}
	private void saveButtonEvent(){
		int x=0;
		if(checkValidation()){
			final int a=findBlankRow();

			if(isUpdate)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update product information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new MessageBox.EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							if(deleteData()){
								insertData();
								addDataToTable(a);
								txtClear();
								isFind=false;
								isUpdate=false;
								autoTransactionNo();
								cmbGroupDataLoadFind();
								cmbGroup.focus();
							}
						}
					}
				});		
			}
			else
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new MessageBox.EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							insertData();
							addDataToTable(a);
							txtClear();
							autoTransactionNo();
							cmbGroupDataLoadFind();
							cmbGroup.focus();
						}
					}
				});		
			}
		}
	}
	private boolean deleteData(){
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			String sql="delete from tbItemOpening where iTransactionNo='"+txtTransactionNo.getValue()+"'";
			session.createSQLQuery(sql).executeUpdate();
			if(session!=null||tx!=null){
				tx.commit();
				session.close();
			}
			return true;
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

		return false;
	}
	private void insertData(){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			String sql="insert into tbItemOpening(iTransactionNo,vProductCode,dDate,vGroupId,vGroupName,vItemId,vItemName,vSubItemName, "+
					"vSpecCode,vSpecName,vUnit,mQty,mRate,mAmount,vOpeningYear,iRunningFlag,vUserIp,vUserName,dEntryTime,vStoreId,vStoreName," +
					"vReckId,vReckName,vShelfId,vShelfName)values" +
					"('"+txtTransactionNo.getValue()+"','"+cmbProductCode.getValue()+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dOpeningYear.getValue())+"'," +
					"'"+cmbGroup.getValue()+"','"+cmbGroup.getItemCaption(cmbGroup.getValue())+"','"+cmbItem.getValue()+"'," +
					"'"+cmbItem.getItemCaption(cmbItem.getValue())+"','"+txtSubItemName.getValue()+"','"+lblSpecCode.getValue()+"'," +
					"'"+txtSpecification.getValue()+"','"+txtUnit.getValue()+"','"+aQty.getValue()+"','"+aRate.getValue()+"'," +
					"'"+txtAmount.getValue()+"','"+new SimpleDateFormat("yyyy").format(dOpeningYear.getValue())+"',1," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'"+cmbStore.getValue()+"'," +
					"'"+cmbStore.getItemCaption(cmbStore.getValue())+"','"+cmbRack.getValue()+"','"+cmbRack.getItemCaption(cmbRack.getValue())+"'," +
					"'"+cmbShelf.getValue()+"','"+cmbShelf.getItemCaption(cmbShelf.getValue())+"')";
			System.out.println(sql);
			session.createSQLQuery(sql).executeUpdate();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				session.close();
				showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			}
		}
	}
	private void addDataToTable(int a){
		tblblGroupCode.get(a).setValue(cmbGroup.getValue());
		tblblGroup.get(a).setValue(cmbGroup.getItemCaption(cmbGroup.getValue()));

		tblblItemCode.get(a).setValue(cmbItem.getValue());
		tblblItem.get(a).setValue(cmbItem.getItemCaption(cmbItem.getValue()));

		tblblProductCode.get(a).setValue(cmbProductCode.getValue());
		tblblSubItem.get(a).setValue(txtSubItemName.getValue());

		tblblSpecificationCode.get(a).setValue(lblSpecCode.getValue());
		tblblSpecification.get(a).setValue(txtSpecification.getValue());

		tblblQty.get(a).setValue(aQty.getValue());
		tblblRate.get(a).setValue(aRate.getValue());
		tblblAmount.get(a).setValue(txtAmount.getValue());

		tblblStoreId.get(a).setValue(cmbStore.getValue());
		tblblStore.get(a).setValue(cmbStore.getItemCaption(cmbStore.getValue()));

		tblblRackId.get(a).setValue(cmbRack.getValue());
		tblblRack.get(a).setValue(cmbRack.getItemCaption(cmbRack.getValue()));

		tblblShelfId.get(a).setValue(cmbShelf.getValue());
		tblblShelf.get(a).setValue(cmbShelf.getItemCaption(cmbShelf.getValue()));

		tblbltransactionNo.get(a).setValue(txtTransactionNo.getValue());
		tblblUnit.get(a).setValue(txtUnit.getValue());
		tbChkShow.get(a).setValue(false);
		if(a==tblblAmount.size()-1){
			tableRowAdd(a+1);
		}
	}
	private int findBlankRow(){
		for(int a=0;a<tbChkShow.size();a++){
			if(tblblProductCode.get(a).getValue().toString().isEmpty()){
				return a;
			}
		}
		return 0;
	}
	private boolean checkValidation(){
		if(cmbGroup.getValue()!=null){
			if(cmbItem.getValue()!=null){
				if(cmbProductCode.getValue()!=null){
					if(!aQty.getValue().toString().isEmpty()){
						if(!aRate.getValue().toString().isEmpty()){
							if(!txtAmount.getValue().toString().isEmpty()){
								if(cmbStore.getValue()!=null){
									return true;
								}
								else{
									showNotification("Please Select Store Name",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else{
								showNotification("Please Select Amount",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else{
							showNotification("Please Select Rate",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else{
						showNotification("Please Select Qty",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Please Select Product Code",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Select Item Name",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Select Group Name",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private void cmbRackDataLoad(){
		cmbRack.removeAllItems();
		Iterator<?>iter=dbService("select vSubStoreId,vSubStoreName from tbRackInfo where vStoreId='"+cmbStore.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbRack.addItem(element[0]);
			cmbRack.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbShelfDataLoad(){
		cmbShelf.removeAllItems();
		Iterator<?>iter=dbService("select vSubSubStoreId,vSubSubStoreName from tbShelfInfo where vSubStoreId='"+cmbRack.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbShelf.addItem(element[0]);
			cmbShelf.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbProductCodeLoad(){
		cmbProductCode.removeAllItems();
		hMapSubItemName.clear();
		hMapUnit.clear();
		hMapSubItemName.clear();
		hMapSpecCode.clear();
		String sql="select a.Specificaionid,case when a.flag=0 then '' else a.subitemName end,a.specificationName,a.unit,(b.vGroupCode+'.'+c.itemcode+'.'+ a.specificationcode)productCode "+
				" from tbspecification a inner join tbGroupInformation b on a.groupId=b.Group_Id "+
				" inner join tbItemInfosub c  on a.subgroupId=c.SubGroup_Id "+
				" where a.groupId like '"+cmbGroup.getValue()+"' and a.subgroupId like '"+cmbItem.getValue()+"' " +
				"and a.Specificaionid not in(select distinct vSpecCode from tbItemOpening) ";
		Iterator<?>iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductCode.addItem(element[4]);
			hMapSubItemName.put(element[4].toString(), element[1].toString());
			hMapUnit.put(element[4].toString(), element[3].toString());
			hMapSpecName.put(element[4].toString(), element[2].toString());
			hMapSpecCode.put(element[4].toString(), element[0].toString());
		}
	}
	private void newButtonEvent() 
	{
		txtClear();
		tableClear();
		findClear();
		componentIni(false);
		btnIni(false);
		autoTransactionNo();
		cmbGroup.focus();
		cmbGroupDataLoad();
		cmbStoreDataLoad();
		cmbGroupDataLoadFind();
	}
	private void cmbGroupDataLoadFind(){
		Iterator<?>iter=dbService("select distinct vGroupId,vGroupName from tbItemOpening");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbGroupFind.addItem(element[0]);
			cmbGroupFind.setItemCaption(element[0], element[1].toString());
		}
	}
	private void autoTransactionNo(){
		Iterator<?>iter=dbService("select isnull(MAX(iTransactionNo),0)+1 from tbItemOpening");
		if(iter.hasNext()){
			txtTransactionNo.setValue(iter.next());
		}
	}
	/*	private String autoReciptNo(){
		String autoId=null;
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select cast(isnull(max(cast(replace(ReceiptNo, '', '')as int))+1, 1)as varchar) from tbRawPurchaseInfo").list().iterator();

			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
		return autoId;
	}*/

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
		tableClear();
		findClear();
	}


	private void componentIni(boolean b) {

		txtTransactionNo.setEnabled(!b);
		cmbGroup.setEnabled(!b);
		cmbItem.setEnabled(!b);
		cmbProductCode.setEnabled(!b);
		txtSubItemName.setEnabled(!b);
		txtUnit.setEnabled(!b);
		txtSpecification.setEnabled(!b);
		aQty.setEnabled(!b);
		aRate.setEnabled(!b);
		txtAmount.setEnabled(!b);
		cmbStore.setEnabled(!b);
		cmbRack.setEnabled(!b);
		cmbShelf.setEnabled(!b);
		lbLine.setEnabled(!b);
		btnSave.setEnabled(!b);
		table.setEnabled(!b);

		cmbGroupFind.setEnabled(!b);
		cmbItemFind.setEnabled(!b);
		cmbProductCodeFind.setEnabled(!b);
		btnFind.setEnabled(!b);
	}

	public void txtClear()
	{
		try
		{

			cmbGroup.setValue(null);
			cmbItem.setValue(null);
			cmbProductCode.setValue(null);
			txtSubItemName.setValue("");
			//cmbUnit.setValue(null);
			txtSpecification.setValue("");
			cmbStore.setValue(null);
			cmbRack.setValue(null);
			cmbShelf.setValue(null);
			aQty.setValue("");
			aRate.setValue("");
			txtAmount.setValue("");
			table.setColumnFooter("Amount", "Total:"+0.0);
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}

	}

	public void tableInitialise(){
		for(int i=0;i<13;i++){
			tableRowAdd(i);
		}
	}



	public void tableRowAdd(final int ar)
	{
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			tbLblSl.add(ar,new Label());
			tbLblSl.get(ar).setWidth("20px");
			tbLblSl.get(ar).setValue(ar + 1);

			tbChkShow.add(ar, new CheckBox());
			tbChkShow.get(ar).setImmediate(true);

			tbChkShow.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(!tblbltransactionNo.get(ar).getValue().toString().isEmpty()){
						if(tbChkShow.get(ar).booleanValue()){
							isFind=true;
							isUpdate=true;
							cmbGroup.setValue(tblblGroupCode.get(ar).getValue());
							cmbItem.setValue(tblblItemCode.get(ar).getValue());
							cmbProductCode.addItem(tblblProductCode.get(ar).getValue());
							cmbProductCode.setValue(tblblProductCode.get(ar).getValue());
							txtSpecification.setValue(tblblSpecification.get(ar).getValue());
							lblSpecCode.setValue(tblblSpecificationCode.get(ar).getValue());
							txtUnit.setValue(tblblUnit.get(ar).getValue());
							txtSubItemName.setValue(tblblSubItem.get(ar).getValue());
							aQty.setValue(tblblQty.get(ar).getValue());
							aRate.setValue(tblblRate.get(ar).getValue());
							cmbStore.setValue(tblblStoreId.get(ar).getValue());
							cmbRack.setValue(tblblRackId.get(ar).getValue());
							cmbShelf.setValue(tblblShelfId.get(ar).getValue());
							txtTransactionNo.setValue(tblbltransactionNo.get(ar).getValue());
							tableRowClear(ar);

						}
					}
					else{
						showNotification("Nothing To Show",Notification.TYPE_WARNING_MESSAGE);
						tbChkShow.get(ar).setValue(false);
					}
				}
			});

			tblblGroupCode.add(ar, new Label());
			tblblGroupCode.get(ar).setImmediate(true);
			tblblGroupCode.get(ar).setWidth("100%");
			tblblGroupCode.get(ar).setHeight("-1px");

			tblblGroup.add(ar, new Label());
			tblblGroup.get(ar).setImmediate(true);
			tblblGroup.get(ar).setWidth("80%");
			tblblGroup.get(ar).setHeight("-1px");

			tblblItemCode.add(ar, new Label());
			tblblItemCode.get(ar).setImmediate(true);
			tblblItemCode.get(ar).setWidth("100%");
			tblblItemCode.get(ar).setHeight("-1px");

			tblblItem.add(ar, new Label());
			tblblItem.get(ar).setImmediate(true);
			tblblItem.get(ar).setWidth("80%");
			tblblItem.get(ar).setHeight("-1px");

			tblblProductCode.add(ar, new Label());
			tblblProductCode.get(ar).setImmediate(true);
			tblblProductCode.get(ar).setWidth("80%");
			tblblProductCode.get(ar).setHeight("-1px");

			/*tblblProduct.add(ar, new Label());
			tblblProduct.get(ar).setImmediate(true);
			tblblProduct.get(ar).setWidth("80%");
			tblblProduct.get(ar).setHeight("-1px");*/			

			tblblSubItem.add(ar, new Label());
			tblblSubItem.get(ar).setImmediate(true);
			tblblSubItem.get(ar).setWidth("100%");
			tblblSubItem.get(ar).setHeight("-1px");

			tblblSpecificationCode.add(ar, new Label());
			tblblSpecificationCode.get(ar).setImmediate(true);
			tblblSpecificationCode.get(ar).setWidth("100%");
			tblblSpecificationCode.get(ar).setHeight("-1px");

			tblblSpecification.add(ar, new Label());
			tblblSpecification.get(ar).setImmediate(true);
			tblblSpecification.get(ar).setWidth("100%");
			tblblSpecification.get(ar).setHeight("-1px");

			tblblQty.add(ar, new TextRead(1));
			tblblQty.get(ar).setImmediate(true);
			tblblQty.get(ar).setWidth("100%");
			tblblQty.get(ar).setHeight("-1px");

			tblblRate.add(ar, new TextRead(1));
			tblblRate.get(ar).setImmediate(true);
			tblblRate.get(ar).setWidth("80%");
			tblblRate.get(ar).setHeight("-1px");

			tblblAmount.add(ar, new TextRead(1));
			tblblAmount.get(ar).setImmediate(true);
			tblblAmount.get(ar).setWidth("100%");
			tblblAmount.get(ar).setHeight("-1px");

			tblblStoreId.add(ar, new Label());
			tblblStoreId.get(ar).setImmediate(true);
			tblblStoreId.get(ar).setWidth("100%");
			tblblStoreId.get(ar).setHeight("-1px");

			tblblStore.add(ar, new Label());
			tblblStore.get(ar).setImmediate(true);
			tblblStore.get(ar).setWidth("100%");
			tblblStore.get(ar).setHeight("-1px");

			tblblRackId.add(ar, new Label());
			tblblRackId.get(ar).setImmediate(true);
			tblblRackId.get(ar).setWidth("100%");
			tblblRackId.get(ar).setHeight("-1px");

			tblblRack.add(ar, new Label());
			tblblRack.get(ar).setImmediate(true);
			tblblRack.get(ar).setWidth("100%");
			tblblRack.get(ar).setHeight("-1px");

			tblblShelfId.add(ar, new Label());
			tblblShelfId.get(ar).setImmediate(true);
			tblblShelfId.get(ar).setWidth("100%");
			tblblShelfId.get(ar).setHeight("-1px");


			tblblShelf.add(ar, new Label());
			tblblShelf.get(ar).setImmediate(true);
			tblblShelf.get(ar).setWidth("100%");
			tblblShelf.get(ar).setHeight("-1px");

			tblbltransactionNo.add(ar, new Label());
			tblbltransactionNo.get(ar).setImmediate(true);
			tblbltransactionNo.get(ar).setWidth("100%");
			tblbltransactionNo.get(ar).setHeight("-1px");

			tblblUnit.add(ar, new Label());
			tblblUnit.get(ar).setImmediate(true);
			tblblUnit.get(ar).setWidth("100%");
			tblblUnit.get(ar).setHeight("-1px");


			table.addItem(new Object[]{tbLblSl.get(ar),	tblblGroupCode.get(ar),tblblGroup.get(ar),tblblItemCode.get(ar),
					tblblItem.get(ar),tblblProductCode.get(ar),tblblSubItem.get(ar),
					tblblSpecificationCode.get(ar),tblblSpecification.get(ar),tblblQty.get(ar),tblblRate.get(ar),
					tblblAmount.get(ar),tblblStoreId.get(ar),tblblStore.get(ar),tblblRackId.get(ar),tblblRack.get(ar),
					tblblShelfId.get(ar),tblblShelf.get(ar),tbChkShow.get(ar),tblbltransactionNo.get(ar),tblblUnit.get(ar)},ar);
			//tblblGroupCode.get(ar),tblblItemCode.get(ar),
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private void tableClear(){
		for(int a=0;a<tblblAmount.size();a++){
			tblblGroupCode.get(a).setValue("");
			tblblGroup.get(a).setValue("");
			tblblItemCode.get(a).setValue("");
			tblblItem.get(a).setValue("");
			tblblProductCode.get(a).setValue("");
			tblblSubItem.get(a).setValue("");
			tblblSpecificationCode.get(a).setValue("");
			tblblSpecification.get(a).setValue("");
			tblblQty.get(a).setValue("");
			tblblRate.get(a).setValue("");
			tblblAmount.get(a).setValue("");
			tblblStoreId.get(a).setValue("");
			tblblStore.get(a).setValue("");
			tblblRackId.get(a).setValue("");
			tblblRack.get(a).setValue("");
			tblblShelfId.get(a).setValue("");
			tblblShelf.get(a).setValue("");
			tblbltransactionNo.get(a).setValue("");
			tblblUnit.get(a).setValue("");
			tbChkShow.get(a).setValue(false);
		}
	}
	private void tableRowClear(int ar){
		tblblGroupCode.get(ar).setValue("");
		tblblGroup.get(ar).setValue("");
		tblblItemCode.get(ar).setValue("");
		tblblItem.get(ar).setValue("");
		tblblProductCode.get(ar).setValue("");
		tblblSubItem.get(ar).setValue("");
		tblblSpecificationCode.get(ar).setValue("");
		tblblSpecification.get(ar).setValue("");
		tblblQty.get(ar).setValue("");
		tblblRate.get(ar).setValue("");
		tblblAmount.get(ar).setValue("");
		tblblStoreId.get(ar).setValue("");
		tblblStore.get(ar).setValue("");
		tblblRackId.get(ar).setValue("");
		tblblRack.get(ar).setValue("");
		tblblShelfId.get(ar).setValue("");
		tblblShelf.get(ar).setValue("");
		tblbltransactionNo.get(ar).setValue("");
		tblblUnit.get(ar).setValue("");
	}
	private void findClear(){
		cmbProductCodeFind.setValue(null);
		cmbItemFind.setValue(null);
		cmbGroupFind.setValue(null);
	}
	/*private boolean doubleEntryCheck(String caption,int row){

		for(int i=0;i<aAmount.size();i++){

			if(i!=row && caption.equals(cmbProductCode.get(i).getItemCaption(cmbProductCode.get(i).getValue()))){

				return false;
			}
		}
		return true;
	}*/

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnUpdate.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnPrev.setEnabled(t);
	}


}
