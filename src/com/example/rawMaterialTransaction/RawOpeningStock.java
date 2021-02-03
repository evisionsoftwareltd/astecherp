package com.example.rawMaterialTransaction;

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

public class RawOpeningStock extends Window 

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
	
	private ComboBox cmbGroup;
	private ComboBox cmbItem;
	private ComboBox cmbProductCode;
	private TextRead txtSpecification;
	private ComboBox cmbStore;
	private ComboBox cmbRack;
	private TextRead txtUnit;
	private ComboBox cmbShelf;
	
	private InlineDateField dOpeningYear;
	
	private TextField txtTransactionNo;
	private TextField txtSubItemName;
	private AmountField aQty;
	private AmountField aRate;
	private TextField txtAmount;
	private NativeButton btnSubmit;
	private NativeButton btnEdit;

	//private ImmediateUploadExample poAttach;

	private ImmediateUploadExampleNew poAttach;

	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<Label> tblblGroupCode = new ArrayList<Label>();
	private ArrayList<Label> tblblGroup = new ArrayList<Label>();
	private ArrayList<Label> tblblSubGroupCode = new ArrayList<Label>();
	private ArrayList<Label> tblblItem = new ArrayList<Label>();
	private ArrayList<Label> tblblProductCode = new ArrayList<Label>();
	private ArrayList<Label> tblblProduct = new ArrayList<Label>();
	private ArrayList<Label> tblblSubItem= new ArrayList<Label>();
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
	private ArrayList<CheckBox>tbChkShow= new ArrayList<CheckBox>();
	
	double totalsum = 0.0;
	private Formatter fmt = new Formatter();
	private TextRead txttotalField = new TextRead();
	boolean isUpdate=false,isFind=false;
	String udFlag;
	private HashMap supplierAddress=new HashMap();

	String strFlag;
	private Label lbLine;
	private TextField txtReceiptId=new TextField();
	private DecimalFormat df = new DecimalFormat("#0.00");

	private CommonButtonNew cButton=new CommonButtonNew( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Exit","","Preview");
	private static final String[] types = new String[] { "Local Purchase", "Against PO" };
	private AbsoluteLayout mainLayout;
	String filePathTmpReq= "";
	String imageLoc= "0";
	HashMap hMap=new HashMap();

	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	public RawOpeningStock(SessionBean sessionBean) 
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
//		cmbPONoData();
	
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
	

	private void FocusMove(){
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(cmbGroup);
		allComp.add(cmbItem);
		allComp.add(cmbProductCode);
		allComp.add(txtSubItemName);
		allComp.add(txtUnit);
		allComp.add(txtSpecification);
		allComp.add(aQty);
		allComp.add(aRate);
		allComp.add(txtAmount);
		allComp.add(cmbStore);
		allComp.add(cmbRack);
		allComp.add(cmbShelf);
	
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnUpdate);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1200px");
		mainLayout.setHeight("580px");

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
		mainLayout.addComponent(lblTransactionNo, "top:20.0px;left:315.0px;");

		txtTransactionNo = new TextField();
		txtTransactionNo.setImmediate(true);
		txtTransactionNo.setWidth("100px");
		txtTransactionNo.setHeight("-1px");
		mainLayout.addComponent(txtTransactionNo, "top:18.0px;left:400.0px;");
		
		lblGroupName = new Label();
		lblGroupName.setImmediate(false);
		lblGroupName.setWidth("-1px");
		lblGroupName.setHeight("-1px");
		lblGroupName.setValue("Group Name :");
		mainLayout.addComponent(lblGroupName, "top:45.0px;left:20.0px;");

		cmbGroup = new ComboBox();
		cmbGroup .setImmediate(true);
		cmbGroup .setWidth("200px");
		cmbGroup .setHeight("-1px");
		cmbGroup .setNullSelectionAllowed(true);
		cmbGroup.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbGroup, "top:43.0px;left:110.0px;");
	
		lblItemName= new Label();
		lblItemName.setImmediate(false);
		lblItemName.setWidth("-1px");
		lblItemName.setHeight("-1px");
		lblItemName.setValue("Item Name :");
		mainLayout.addComponent(lblItemName, "top:45.0px;left:315.0px;");

		cmbItem = new ComboBox();
		cmbItem .setImmediate(true);
		cmbItem.setWidth("220px");
		cmbItem.setHeight("-1px");
		cmbItem .setNullSelectionAllowed(true);
		cmbItem .setNewItemsAllowed(false);
		mainLayout.addComponent(cmbItem, "top:43.0px;left:400.0px;");

		
		lblProductCode= new Label();
		lblProductCode.setImmediate(false);
		lblProductCode.setWidth("-1px");
		lblProductCode.setHeight("-1px");
		lblProductCode.setValue("Product Code :");
		mainLayout.addComponent(lblProductCode, "top:45.0px;left:640.0px;");

		cmbProductCode = new ComboBox();
		cmbProductCode.setImmediate(true);
		cmbProductCode.setWidth("90px");
		cmbProductCode.setHeight("-1px");
		cmbProductCode.setNullSelectionAllowed(true);
		cmbProductCode.setNewItemsAllowed(false);
		mainLayout.addComponent(		cmbProductCode, "top:43.0px;left:730.0px;");

		lblSubItemName = new Label();
		lblSubItemName .setImmediate(false);
		lblSubItemName .setWidth("-1px");
		lblSubItemName .setHeight("-1px");
		lblSubItemName .setValue("Sub Item Name :");
		mainLayout.addComponent(lblSubItemName , "top:45.0px;left:830.0px;");

		txtSubItemName = new TextField();
		txtSubItemName .setImmediate(true);
		txtSubItemName .setWidth("140px");
		txtSubItemName .setHeight("-1px");
		//txtSubItemName .setRows(1);
		mainLayout.addComponent(	txtSubItemName, "top:43.0px;left:930.0px;");
		
		lblUnit= new Label();
		lblUnit .setImmediate(false);
		lblUnit .setWidth("-1px");
		lblUnit .setHeight("-1px");
		lblUnit .setValue("Unit:");
		mainLayout.addComponent(lblUnit , "top:45.0px;left:1075.0px;");

		txtUnit = new TextRead();
		txtUnit  .setImmediate(true);
		txtUnit  .setWidth("80px");
		txtUnit  .setHeight("-1px");
		//txtSubItemName .setRows(1);
		mainLayout.addComponent(	txtUnit , "top:43.0px;left:1100.0px;");
		
		lblSpecification= new Label();
		lblSpecification .setImmediate(false);
		lblSpecification .setWidth("-1px");
		lblSpecification .setHeight("-1px");
		lblSpecification .setValue("Specification:");
		mainLayout.addComponent(lblSpecification , "top:69.0px;left:20.0px;");

		txtSpecification = new TextRead();
		txtSpecification  .setImmediate(true);
		txtSpecification  .setWidth("100px");
		txtSpecification  .setHeight("-1px");
		//txtSubItemName .setRows(1);
		mainLayout.addComponent(	txtSpecification , "top:68.0px;left:110.0px;");

		lblQty= new Label();
		lblQty.setImmediate(false);
		lblQty.setWidth("-1px");
		lblQty.setHeight("-1px");
		lblQty.setValue("Qty :");
		mainLayout.addComponent(lblQty, "top:69.0px;left:210.0px;");

		aQty= new AmountField();
		aQty.setImmediate(true);
		aQty.setWidth("80px");
		aQty.setHeight("-1px");
		mainLayout.addComponent(	aQty, "top:68.0px;left:240.0px;");

		lblRate = new Label();
		lblRate.setImmediate(false);
		lblRate .setWidth("-1px");
		lblRate .setHeight("-1px");
		lblRate.setValue("Rate :");
		mainLayout.addComponent(lblRate , "top:69.0px;left:330.0px;");
		
		aRate= new AmountField();
		aRate.setImmediate(true);
		aRate.setWidth("60px");
		aRate.setHeight("-1px");
		mainLayout.addComponent(	aRate, "top:68.0px;left:365.0px;");

		lblAmount = new Label();
		lblAmount.setImmediate(false);
		lblAmount .setWidth("-1px");
		lblAmount .setHeight("-1px");
		lblAmount.setValue("Amount :");
		mainLayout.addComponent(lblAmount , "top:69.0px;left:430.0px;");
		
		txtAmount= new AmountField();
		txtAmount.setImmediate(true);
		txtAmount.setWidth("80px");
		txtAmount.setHeight("-1px");
		mainLayout.addComponent(	txtAmount, "top:68.0px;left:490.0px;");
		
		lblStore= new Label();
		lblStore.setImmediate(false);
		lblStore.setWidth("-1px");
		lblStore.setHeight("-1px");
		lblStore.setValue("Store :");
		mainLayout.addComponent(lblStore, "top:69.0px;left:580.0px;");

		cmbStore = new ComboBox();
		cmbStore.setImmediate(true);
		cmbStore.setWidth("160px");
		cmbStore.setHeight("-1px");
		cmbStore.setNullSelectionAllowed(true);
		cmbStore.setNewItemsAllowed(false);
		mainLayout.addComponent(		cmbStore, "top:68.0px;left:620.0px;");

		lblRack= new Label();
		lblRack.setImmediate(false);
		lblRack.setWidth("-1px");
		lblRack.setHeight("-1px");
		lblRack.setValue("Rack :");
		mainLayout.addComponent(lblRack, "top:69.0px;left:780.0px;");

		cmbRack = new ComboBox();
		cmbRack.setImmediate(true);
		cmbRack.setWidth("150px");
		cmbRack.setHeight("-1px");
		cmbRack.setNullSelectionAllowed(true);
		cmbRack.setNewItemsAllowed(false);
		mainLayout.addComponent(		cmbRack, "top:68.0px;left:820.0px;");
		
		lblShelf= new Label();
		lblShelf.setImmediate(false);
		lblShelf.setWidth("-1px");
		lblShelf.setHeight("-1px");
		lblShelf.setValue("Shelf :");
		mainLayout.addComponent(lblShelf, "top:69.0px;left:970.0px;");

		cmbShelf = new ComboBox();
		cmbShelf .setImmediate(true);
		cmbShelf .setWidth("150px");
		cmbShelf .setHeight("-1px");
		cmbShelf .setNullSelectionAllowed(true);
		cmbShelf .setNewItemsAllowed(false);
		mainLayout.addComponent(	cmbShelf , "top:68.0px;left:1010.0px;");
		
		btnSubmit= new NativeButton("Submit");
		btnSubmit.setImmediate(false);
	//	btnSubmit.setIcon(new ThemeResource("../icons/add.png"));
		btnSubmit.setImmediate(true);
		btnSubmit.setWidth("75px");
		btnSubmit.setHeight("24px");
		mainLayout.addComponent(btnSubmit, "top:120.0px;left:580.0px;");

		table = new Table();
		table.setWidth("99%");
		table.setHeight("350px");
		table.setFooterVisible(true);

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);
		
		table.addContainerProperty("Group Code",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Group Code",50);
		table.setColumnCollapsingAllowed(false);
		
		table.addContainerProperty("Group Name",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Group Name",250);

		table.addContainerProperty("Sub Group Code",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Item Code",50);
		table.setColumnCollapsingAllowed(false);
		
		table.addContainerProperty("Item Name",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Item Name",250);
		
		table.addContainerProperty("Item Code",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Product Name",220);
		table.setColumnCollapsingAllowed(false);
		
		table.addContainerProperty("Product Code",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Product Code",220);
		
		table.addContainerProperty("Sub ItemName",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Sub Item Name",160);
		
		table.addContainerProperty("Specification", Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Specification Name",100);
		 
		table.addContainerProperty("Qty",  Label.class , new  Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Qty",75);
		
		table.addContainerProperty("Rate",  Label.class , new  Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Rate",75);
		
		table.addContainerProperty("Amount",  Label.class , new  Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Amount",75);
		
		table.addContainerProperty("Store Id",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Store Id",50);
		table.setColumnCollapsingAllowed(false);
		
		table.addContainerProperty("Store",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Store",120);
		
		table.addContainerProperty("Rack Id",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Rack Id",50);
		table.setColumnCollapsingAllowed(false);
		
		table.addContainerProperty("Rack",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Rack",120);
		
		table.addContainerProperty("Shelf Id",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Shelf Id",50);
		table.setColumnCollapsingAllowed(false);
		
		table.addContainerProperty("Shelf",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Shelf",50);
		
		table.addContainerProperty("Edit", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Edit", 40);

		table.setColumnCollapsingAllowed(true);
		tableInitialise();
		mainLayout.addComponent(table, "top:155.0px;left:5.0px;");

		lbLine = new Label("_______________________________________________________________________________________________________________________________________________________________________________________________________________________");
		mainLayout.addComponent(lbLine, "top:520.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:540.0px;left:325.0px;");

		return mainLayout;
	}


/*	private void tableclear()

	{

		for(int i=0;i<unit.size();i++){
			//cmbProduct.get(i).setValue("x#"+i);
		//	cmbProduct.get(i).setValue(null);
			unit.get(i).setValue("");
			//lblSection.get(i).setValue("");
			amount.get(i).setValue("");
			rate.get(i).setValue("");
			poQty.get(i).setValue("");
			okQty.get(i).setValue("");
			leftQty.get(i).setValue("");
			rejectQty.get(i).setValue("");
			tbTxtRemark.get(i).setValue("");
			tCmbStoreLocation.get(i).setValue(null);
		}


	}*/
	private void reportShow()
	{
		String query=null;
		String activeFlag = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("URL",getApplication().getURL().toString().replace("uptd/", ""));
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());

		//	query="select * from vwRawPurchaseReceipt where SupplierId = '"+cmbSupplier.getValue().toString()+"'" +
					//" and ChallanNo = '"+txtchallanNo.getValue().toString()+"' ";

			System.out.println(query);
			hm.put("sql", query);
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/raw/rptGRN.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",0);
				win.setCaption("Report : Goods Receive Note (GRN)");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{

			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public void setEventAction()
	{

		/*cButton.btnPrev.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(isFind&&!txtreceiptNo.getValue().toString().isEmpty()){
					reportShow();
				}
				else{
					showNotification("Nothing To Preview",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});*/
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

	}

	/*public void productDataLoad(int i )
	{
		String poNo;
		if(cmbPoNo.getValue()==null){
			poNo="%";
		}
		else{
			poNo=cmbPoNo.getValue().toString();
		}
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			System.out.println("fbgfghthg");

			String sql=" select a.vRawItemCode,a.vRawItemName,subString(a.vSubGroupName,CHARINDEX('-',a.vSubGroupName)+1,LEN(a.vSubGroupName))as category,a.vSubSubCategoryName  from tbRawItemInfo a  "
					+" inner join "
					+" tbRawPurchaseOrderDetails b "
					+ " on  a.vRawItemCode=b.ProductID "
					+ " where b.pono like '"+poNo+"' ";

			System.out.println("sql"+ sql);
			List lst = session.createSQLQuery(sql).list();

			cmbProduct.get(i).removeAllItems();
			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProduct.get(i).addItem(element[0].toString());
				String name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
				cmbProduct.get(i).setItemCaption(element[0].toString(), name);

				System.out.println("Product Name: "+element[1].toString());

			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error", Notification.TYPE_ERROR_MESSAGE);
		}
	}*/


	private void newButtonEvent() 
	{
		txtClear();
		componentIni(false);
		btnIni(false);
		autoReciptNo();
		//cmbItemType.focus();	
		cmbGroupDataLoad();
	}
	private void cmbGroupDataLoad(){
		
		Iterator<?>iter=dbService("select Group_Id,vGroupName from tbGroupInformation");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbGroup.addItem(element[0]);
			cmbGroup.setItemCaption(element[0], element[1].toString());
			System.out.println(element[1]);
		}
	}

	private String autoReciptNo(){
		String autoId=null;
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select cast(isnull(max(cast(replace(ReceiptNo, '', '')as int))+1, 1)as varchar) from tbRawPurchaseInfo").list().iterator();

			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
				//txtreceiptNo.setValue(autoId);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
		return autoId;
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	
	private void componentIni(boolean b) {

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
		table.setEnabled(!b);
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
	      // aQty.setValue("");
		   // aRate.setValue("");
			//aAmount.setValue("");
			cmbStore.setValue("");
			cmbRack.setValue("");
			cmbShelf.setValue("");
			table.setColumnFooter("Amount", "Total:"+0.0);
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}

	}

	/*private void deleteButtonEvent(){
		if(cmbSupplier.getValue()!= null)
		{
			this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
					new YesNoDialog.Callback() {
				public void onDialogResult(boolean yes) {
					if(yes){
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						Transaction tx= session.beginTransaction();
						if(deleteData(session,tx)){
							tx.commit();
							txtClear();
							getParent().showNotification("All information delete Successfully");
						}
						else{
							tx.rollback();
							getParent().showNotification(
									"Delete Failed",
									"There are no data for delete.",
									Notification.TYPE_WARNING_MESSAGE);
						}
					}
				}
			}));
		}
		else
			this.getParent().showNotification(
					"Delete Failed",
					"There are no data for delete.",
					Notification.TYPE_WARNING_MESSAGE);
	}*/



	public void tableInitialise(){
		for(int i=0;i<10;i++){
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
			
			tblblGroupCode.add(ar, new Label());
			tblblGroupCode.get(ar).setImmediate(true);
			tblblGroupCode.get(ar).setWidth("100%");
			tblblGroupCode.get(ar).setHeight("-1px");
			
			tblblGroup.add(ar, new Label());
			tblblGroup.get(ar).setImmediate(true);
			tblblGroup.get(ar).setWidth("100%");
			tblblGroup.get(ar).setHeight("-1px");
			
			tblblSubGroupCode.add(ar, new Label());
			tblblSubGroupCode.get(ar).setImmediate(true);
			tblblSubGroupCode.get(ar).setWidth("100%");
			tblblSubGroupCode.get(ar).setHeight("-1px");
			
			tblblItem.add(ar, new Label());
			tblblItem.get(ar).setImmediate(true);
			tblblItem.get(ar).setWidth("100%");
			tblblItem.get(ar).setHeight("-1px");
			
			tblblProductCode.add(ar, new Label());
			tblblProductCode.get(ar).setImmediate(true);
			tblblProductCode.get(ar).setWidth("100%");
			tblblProductCode.get(ar).setHeight("-1px");
			
			tblblProduct.add(ar, new Label());
			tblblProduct.get(ar).setImmediate(true);
			tblblProduct.get(ar).setWidth("100%");
			tblblProduct.get(ar).setHeight("-1px");			

			tblblSubItem.add(ar, new Label());
			tblblSubItem.get(ar).setImmediate(true);
			tblblSubItem.get(ar).setWidth("100%");
			tblblSubItem.get(ar).setHeight("-1px");
			
			tblblSpecification.add(ar, new Label());
			tblblSpecification.get(ar).setImmediate(true);
			tblblSpecification.get(ar).setWidth("100%");
			tblblSpecification.get(ar).setHeight("-1px");
			
			tblblSpecification.add(ar, new Label());
			tblblSpecification.get(ar).setImmediate(true);
			tblblSpecification.get(ar).setWidth("100%");
			tblblSpecification.get(ar).setHeight("-1px");
			
			tblblQty.add(ar, new Label());
			tblblQty.get(ar).setImmediate(true);
			tblblQty.get(ar).setWidth("100%");
			tblblQty.get(ar).setHeight("-1px");
			
			tblblRate.add(ar, new Label());
			tblblRate.get(ar).setImmediate(true);
			tblblRate.get(ar).setWidth("100%");
			tblblRate.get(ar).setHeight("-1px");
			
			tblblAmount.add(ar, new Label());
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


			table.addItem(new Object[]{tbLblSl.get(ar),	tblblGroupCode.get(ar),tblblGroup.get(ar),tblblSubGroupCode.get(ar),tblblItem.get(ar),tblblProductCode.get(ar),tblblProduct.get(ar),tblblSubItem.get(ar),tblblSpecification.get(ar),tblblQty.get(ar),tblblRate.get(ar),
					tblblAmount.get(ar),tblblStoreId.get(ar),tblblStore.get(ar),tblblRackId.get(ar),tblblRack.get(ar),tblblShelfId.get(ar),tblblShelf.get(ar),tbChkShow.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	/*private boolean doubleEntryCheck(String caption,int row){

		for(int i=0;i<aAmount.size();i++){

			if(i!=row && caption.equals(cmbProductCode.get(i).getItemCaption(cmbProductCode.get(i).getValue()))){

				return false;
			}
		}
		return true;
	}*/


	/*private void tableColumnAction(final String head,final int r)
	{

		rate.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
		rate.get(r).setTextChangeTimeout(200);

		poQty.get(r).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try
				{
					if(head.equalsIgnoreCase("x"))
					{
						amount.get(r).setValue("");
					}
					else
					{
						String tbquntity;
						double tamount,unitPrice;

						if(!event.getProperty().toString().trim().isEmpty() && !rate.get(r).getValue().toString().trim().isEmpty())
						{
							tbquntity=event.getProperty().toString().trim().replaceAll(",", "");
							String tempPrice=rate.get(r).getValue().toString().replaceAll(",", "");
							unitPrice=Double.parseDouble(tempPrice);
							System.out.println("column Action");
							tamount=unitPrice*(Double.parseDouble(tbquntity));
							fmt = new Formatter();
							amount.get(r).setValue(fmt.format("%.2f",tamount));
						}
						txttotalField.setImmediate(true);
						totalsum=0.0;
						for(int flag=0;flag<amount.size();flag++)
						{							
							if(amount.get(flag).getValue().toString().trim().length()>0)
							{
								String flagbit = amount.get(flag).getValue().toString();
								totalsum=totalsum+Double.parseDouble(flagbit);//flagbit;
							}
						}
						fmt = new Formatter();
						txttotalField.setValue(fmt.format("%.2f",totalsum));						
					}
					//cmbProductCode.get(r+1).focus();
				}
				catch(Exception exp)
				{
					getParent().showNotification("Errora",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});*/

		
	
							/*if(isUpdate)
							{
								MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update ?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
								mb.show(new EventListener()
								{
									public void buttonClicked(ButtonType buttonType)
									{
										if(buttonType == ButtonType.YES)
										{
											Transaction tx=null;
											Session session = SessionFactoryUtil.getInstance().getCurrentSession();
											tx = session.beginTransaction();
											if(deleteData(session,tx))
											{
												insertData(session,tx);
												poAttach.actionCheck = false;	
											}

											else{
												tx.rollback();
											}
											isUpdate=false;
										}
									}
								});	

							}else{
								MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
								mb.show(new EventListener()
								{
									public void buttonClicked(ButtonType buttonType)
									{
										if(buttonType == ButtonType.YES)
										{
											Transaction tx=null;
											Session session = SessionFactoryUtil.getInstance().getCurrentSession();
											tx = session.beginTransaction();

											insertData(session,tx);
											isUpdate=false;
											poAttach.actionCheck = false;
										}
									}
								});	

							}*/

			

/*	private void tbCmbAddData(){
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List lst = session.createSQLQuery("Select ProductName, ProductCode, Unit,Ledger_Id from  tbRawProductInfo order by ProductName").list();
			for(int ar=0;ar<cmbProduct.size();ar++){
				for (Iterator iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbProductCode.get(ar).addItem(element[1].toString());
					cmbProduct.get(ar).setItemCaption(element[1].toString(), element[0].toString());
					productUnit.put(element[1], element[2]);
				}
			}

		}
		catch(Exception exp){
			System.out.println(exp);
		}
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



	
	

	/*private void findButtonEvent(){
		Window win=new FindWindow(sessionBean,txtReceiptId,"raw");
		win.addListener(new Window.CloseListener() {
			public void windowClose(CloseEvent e) {
				if(txtReceiptId.getValue().toString().length()>0){
					txtClear();
					findInitialise();
					isFind=true;
				}
			}
		});
		this.getParent().addWindow(win);
	}*/

	


}
