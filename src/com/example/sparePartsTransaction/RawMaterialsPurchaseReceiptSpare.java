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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
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

public class RawMaterialsPurchaseReceiptSpare extends Window 

{
	SessionBean sessionBean;
	private Label lblitemType;
	private Label lblSupplier;
	private Label lblAddress;
	private Label lblReceiptNo;
	private Label lblDate;
	private Label lblChallan;
	private Label lblChallanDate;
	private Label lblPoNo;
	private Label lblpodate;

	private Label lblbillNo;

	private ComboBox cmbItemType;
	private ComboBox cmbSupplier;
	private ComboBox cmbPoNo;

	private TextField txtaddress;
	private TextRead txtreceiptNo;
	private TextField txtchallanNo;
	private TextField txtbillNo;

	private PopupDateField dateField;
	private PopupDateField dChallanDate;
	private PopupDateField dpodate;

	private ImmediateUploadExampleNew poAttach;
	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> unit = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> rate = new ArrayList<AmountCommaSeperator>();

	private ArrayList<TextRead> poQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> leftQty = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> okQty = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountField>  qtyBag= new ArrayList<AmountField>();
	private ArrayList<AmountCommaSeperator> rejectQty = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextField> tbTxtRemark = new ArrayList<TextField>();
	private ArrayList<TextRead> amount = new ArrayList<TextRead>();
	private ArrayList<ComboBox> tCmbStoreLocation = new ArrayList<ComboBox>();

	private HashMap productUnit=new HashMap();

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
	private AbsoluteLayout mainLayout;
	String filePathTmpReq= "";
	String imageLoc= "0";
	HashMap hMap=new HashMap();

	private Label lblpurchaseType= new Label();
	private Label lblmrrType= new Label();

	private OptionGroup opmrrType=new OptionGroup();
	private OptionGroup oppurchaseType= new OptionGroup();

	private String [] purchaseType={"Local Credit Purchase","Cash Purchase"};
	private String[]mrrtype={"With MRR"};

	private Label lblMrrNo=new Label();
	private TextRead txtmrrNo= new TextRead();
	private TextRead  txtvoucherNo= new TextRead();
	private Label lblvoucherNo= new Label();

	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private HashMap hmproduct=new HashMap();
	private HashMap hmqty= new HashMap();

	public RawMaterialsPurchaseReceiptSpare(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("PURCHASE / RECEIPT ENTRY (SPARE PARTS):: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		btnIni(true);
		componentIni(true);
		txtClear();
		FocusMove();
		purchaseTypeDataLoad();
		cmbSupplierAddData("Local Credit Purchase");
		cmbSupplier.focus();
	}

	private void FocusMove()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();
		//allComp.add(cmbItemType);
		allComp.add(cmbSupplier);
		allComp.add(txtaddress);
		allComp.add(dateField);
		allComp.add(txtchallanNo);
		allComp.add(dChallanDate);
		allComp.add(cmbPoNo);
		allComp.add(dpodate);
		allComp.add(txtbillNo);

		for(int i=0;i<cmbProduct.size();i++)
		{
			allComp.add(cmbProduct.get(i));
			//allComp.add(qtyBag.get(i));
			allComp.add(okQty.get(i));	
			allComp.add(rate.get(i));
			allComp.add(tbTxtRemark.get(i));
			allComp.add(tCmbStoreLocation.get(i));
		}
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnUpdate);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("1265px");
		setHeight("645px");

		lblpurchaseType = new Label();
		lblpurchaseType.setImmediate(false);
		lblpurchaseType.setWidth("-1px");
		lblpurchaseType.setHeight("-1px");
		lblpurchaseType.setValue("Purchase Type:");
		mainLayout.addComponent(lblpurchaseType, "top:22.0px;left:20.0px;");

		oppurchaseType= new OptionGroup("");
		oppurchaseType.setImmediate(true);
		oppurchaseType.setWidth("-1px");
		oppurchaseType.setHeight("-1px");
		oppurchaseType.setStyleName("horizontal");
		mainLayout.addComponent(oppurchaseType, "top:25.0px;left:130.0px;");

		for(int i=0;i<purchaseType.length;i++)	
		{
			oppurchaseType.addItem(purchaseType[i]);
		}

		oppurchaseType.select("Local Credit Purchase");

		// lblPurchaseType
		lblitemType = new Label();
		lblitemType.setImmediate(false);
		lblitemType.setWidth("-1px");
		lblitemType.setHeight("-1px");
		lblitemType.setValue("Item Type :");
		mainLayout.addComponent(lblitemType, "top:47.0px;left:20.0px;");

		// cmbPurchaseType
		cmbItemType = new ComboBox();
		cmbItemType.setImmediate(true);
		cmbItemType.setWidth("220px");
		cmbItemType.setHeight("-1px");
		cmbItemType.setNullSelectionAllowed(true);
		cmbItemType.setNewItemsAllowed(false);
		cmbItemType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbItemType, "top:47.0px;left:130.0px;");

		// lblSupplier
		lblSupplier = new Label();
		lblSupplier.setImmediate(false);
		lblSupplier.setWidth("-1px");
		lblSupplier.setHeight("-1px");
		lblSupplier.setValue("Supplier Name :");
		mainLayout.addComponent(lblSupplier, "top:72.0px;left:20.0px;");

		// cmbSupplier
		cmbSupplier = new ComboBox();
		cmbSupplier.setImmediate(true);
		cmbSupplier.setWidth("220px");
		cmbSupplier.setHeight("-1px");
		cmbSupplier.setNullSelectionAllowed(true);
		cmbSupplier.setNewItemsAllowed(false);
		cmbSupplier.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSupplier, "top:72.0px;left:130.0px;");

		// lblAddress
		lblAddress = new Label();
		lblAddress.setImmediate(false);
		lblAddress.setWidth("-1px");
		lblAddress.setHeight("-1px");
		lblAddress.setValue("Address :");
		mainLayout.addComponent(lblAddress, "top:97.0px;left:20.0px;");

		// txtaddress
		txtaddress = new TextField();
		txtaddress.setImmediate(true);
		txtaddress.setWidth("220px");
		txtaddress.setRows(1);
		mainLayout.addComponent(txtaddress, "top:97.0px;left:130.0px;");

		// lblReceiptNo
		lblReceiptNo = new Label();
		lblReceiptNo.setImmediate(false);
		lblReceiptNo.setWidth("-1px");
		lblReceiptNo.setHeight("-1px");
		lblReceiptNo.setValue("Receipt No :");
		mainLayout.addComponent(lblReceiptNo, "top:22.0px;left:450.0px;");

		// txtreceiptNo
		txtreceiptNo = new TextRead(1);
		txtreceiptNo.setImmediate(true);
		txtreceiptNo.setWidth("100px");
		txtreceiptNo.setHeight("-1px");
		mainLayout.addComponent(txtreceiptNo, "top:22.0px;left:525.0px;");

		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:47.0px;left:450.0px;");

		// dateField
		dateField = new PopupDateField();
		dateField.setImmediate(true);
		dateField.setWidth("107px");
		dateField.setHeight("-1px");
		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dateField, "top:47.0px;left:525.0px;");

		// lblReceiptNo
		lblmrrType = new Label();
		lblmrrType.setImmediate(false);
		lblmrrType.setWidth("-1px");
		lblmrrType.setHeight("-1px");
		lblmrrType.setValue("MRR Type :");
		mainLayout.addComponent(lblmrrType, "top:72.0px;left:450.0px;");

		opmrrType= new OptionGroup("");
		opmrrType.setImmediate(true);
		opmrrType.setWidth("-1px");
		opmrrType.setHeight("-1px");
		opmrrType.setStyleName("horizontal");

		mainLayout.addComponent(opmrrType, "top:72.0px;left:525.0px;");

		for(int i=0;i<mrrtype.length;i++)	
		{
			opmrrType.addItem(mrrtype[i]);
		}
		opmrrType.select("With MRR");

		lblMrrNo = new Label();
		lblMrrNo.setImmediate(false);
		lblMrrNo.setWidth("-1px");
		lblMrrNo.setHeight("-1px");
		lblMrrNo.setValue("MRR No :");
		mainLayout.addComponent(lblMrrNo, "top:97.0px;left:450.0px;");

		txtmrrNo = new TextRead(1);
		txtmrrNo.setImmediate(true);
		txtmrrNo.setWidth("100px");
		mainLayout.addComponent(txtmrrNo, "top:97.0px;left:525.0px;");

		// lblChallan
		lblChallan = new Label();
		lblChallan.setImmediate(false);
		lblChallan.setWidth("-1px");
		lblChallan.setHeight("-1px");
		lblChallan.setValue("Challan No :");
		mainLayout.addComponent(lblChallan, "top:22.0px;left:700.0px;");

		txtchallanNo = new TextField();
		txtchallanNo.setImmediate(true);
		txtchallanNo.setWidth("100px");
		txtchallanNo.setHeight("-1px");
		mainLayout.addComponent(txtchallanNo, "top:22.0px;left:790.0px;");

		lblChallanDate = new Label();
		lblChallanDate.setImmediate(false);
		lblChallanDate.setWidth("-1px");
		lblChallanDate.setHeight("-1px");
		lblChallanDate.setValue("Challan Date :");
		mainLayout.addComponent(lblChallanDate, "top:47.0px;left:700.0px;");

		dChallanDate = new PopupDateField();
		dChallanDate.setImmediate(true);
		dChallanDate.setWidth("107px");
		dChallanDate.setHeight("-1px");
		dChallanDate.setDateFormat("dd-MM-yyyy");
		dChallanDate.setValue(new java.util.Date());
		dChallanDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dChallanDate, "top:47.0px;left:790.0px;");

		lblPoNo = new Label();
		lblPoNo.setImmediate(false);
		lblPoNo.setWidth("-1px");
		lblPoNo.setHeight("-1px");
		lblPoNo.setValue("P.O No :");
		mainLayout.addComponent(lblPoNo, "top:72.0px;left:700.0px;");

		cmbPoNo = new ComboBox();
		cmbPoNo.setImmediate(true);
		cmbPoNo.setNullSelectionAllowed(true);
		cmbPoNo.setWidth("180px");
		cmbPoNo.setHeight("-1px");
		cmbPoNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbPoNo, "top:72.0px;left:790.0px;");

		lblpodate = new Label();
		lblpodate.setImmediate(false);
		lblpodate.setWidth("-1px");
		lblpodate.setHeight("-1px");
		lblpodate.setValue("PO Date :");
		mainLayout.addComponent(lblpodate, "top:97.0px;left:700.0px;");

		dpodate = new PopupDateField();
		dpodate.setImmediate(true);
		dpodate.setWidth("107px");
		dpodate.setHeight("-1px");
		dpodate.setDateFormat("dd-MM-yyyy");
		dpodate.setValue(new java.util.Date());
		dpodate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dpodate, "top:97.0px;left:790.0px;");

		lblbillNo = new Label();
		lblbillNo.setImmediate(false);
		lblbillNo.setWidth("-1px");
		lblbillNo.setHeight("-1px");
		lblbillNo.setValue("Bill No :");
		mainLayout.addComponent(lblbillNo, "top:22.0px;left:920.0px;");

		txtbillNo = new TextField();
		txtbillNo.setImmediate(true);
		txtbillNo.setWidth("120px");
		mainLayout.addComponent(txtbillNo, "top:20.0px;left:980.0px;");

		lblvoucherNo = new Label();
		lblvoucherNo.setImmediate(false);
		lblvoucherNo.setWidth("-1px");
		lblvoucherNo.setHeight("-1px");
		lblvoucherNo.setValue("Voucher No :");
		//mainLayout.addComponent(lblvoucherNo, "top:22.0px;left:810.0px;");

		txtvoucherNo = new TextRead();
		txtvoucherNo.setImmediate(true);
		txtvoucherNo.setWidth("120px");
		//mainLayout.addComponent(txtvoucherNo, "top:22.0px;left:885.0px;");


		poAttach = new ImmediateUploadExampleNew("");
		poAttach.setWidth("-1px");
		poAttach.setHeight("-1px");
		poAttach.setStyleName("uploadReq");
		mainLayout.addComponent(poAttach, "top:47.5px;left:970.0px;");

		table = new Table();
		table.setWidth("99%");
		table.setHeight("350px");
		table.setFooterVisible(true);

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Product Name", ComboBox.class , new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Product Name",320);

		table.addContainerProperty("Unit", TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Unit",45);

		/*table.addContainerProperty("Section", Label.class , new Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Section",120);*/

		table.addContainerProperty("PO Qty",TextRead.class , new TextRead(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("PO Qty",75);

		table.addContainerProperty("Left Qty", TextRead.class , new TextRead(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Left Qty",75);

	/*	table.addContainerProperty("Qty(Bag )", AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Qty(Bag)",72);*/

		table.addContainerProperty("OK Qty", AmountCommaSeperator.class , new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("OK Qty",72);

		table.addContainerProperty("Short Qty", AmountCommaSeperator.class , new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Short Qty",72);

		table.addContainerProperty("Rate", AmountCommaSeperator.class , new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Rate",80);

		table.addContainerProperty("Amount", TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Amount",90);

		table.addContainerProperty("Remarks", TextField.class , new TextField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Remarks",100);

		table.addContainerProperty("Store Loc.", ComboBox.class , new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Store Loc.",110);

		table.setColumnCollapsingAllowed(true);
		tableInitialise();
		mainLayout.addComponent(table, "top:160.0px;left:5.0px;");

		lbLine = new Label("_____________________________________________________________________________________________________________________________________________________________________________________________");
		mainLayout.addComponent(lbLine, "top:515.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:550.0px;left:305.0px;");

		return mainLayout;
	}


	private void tableclear()

	{

		for(int i=0;i<unit.size();i++){
			//cmbProduct.get(i).setValue("x#"+i);
			cmbProduct.get(i).setValue(null);
			unit.get(i).setValue("");
			//lblSection.get(i).setValue("");
			amount.get(i).setValue("");
			rate.get(i).setValue("");
			poQty.get(i).setValue("");
			okQty.get(i).setValue("");
			//qtyBag.get(i).setValue("");
			leftQty.get(i).setValue("");
			rejectQty.get(i).setValue("");
			tbTxtRemark.get(i).setValue("");
			tCmbStoreLocation.get(i).setValue(null);
		}


	}
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

			query="select * from vwRawPurchaseReceipt where SupplierId = '"+cmbSupplier.getValue().toString()+"'" +
					" and ChallanNo = '"+txtchallanNo.getValue().toString()+"' ";

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
	private void poDateSet()
	{

		Transaction tx;
		try{

			System.out.println("Done");

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List lst = session.createSQLQuery("select 0,poDate from tbRawPurchaseOrderInfo where poNo='"+cmbPoNo.getValue()+"'").list();

			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				dpodate.setValue(element[1]);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	@SuppressWarnings("serial")
	public void setEventAction()
	{

		cButton.btnPrev.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(isFind&&!txtreceiptNo.getValue().toString().isEmpty()){
					reportShow();
				}
				else{
					showNotification("Nothing To Preview",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				udFlag="New";
				isUpdate=false;
				isFind=false;
				newButtonEvent();
				autoReciptNo();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isSubmitable())
				{
					if(!isUpdate && opmrrType.getValue().toString().equalsIgnoreCase("With MRR"))	
					{
						autoMrrNo();
					}
					saveButtonEvent();
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
					updateButtonEvent();
				}
				else
				{
					showNotification("Warning,","You are not permitted to edit data.",Notification.TYPE_WARNING_MESSAGE);
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
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});


		cmbSupplier.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbSupplier.getValue()!=null)
				{
					supplierValueChange();
					cmbPONoData();
				}
			}
		});


		cmbItemType.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {
				if( cmbItemType.getValue()!=null)
				{
					for(int i=0;i<rate.size();i++)
					{
						dataload(i);	
					}
					cmbSupplier.focus();
				}

			}
		});


		cmbPoNo.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {
				if(cmbPoNo.getValue()!=null)
				{
					poDateSet();

					for(int i=0;i<rate.size();i++)
					{
						productDataLoad(i);	
					}

				}
				else if(cmbItemType.getValue()!=null)
				{
					for(int i=0;i<rate.size();i++)
					{
						dataload(i);	
					}
				}

			}
		});

		oppurchaseType.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event)
			{
				if(oppurchaseType.getValue().toString().equalsIgnoreCase("L / C"))
				{
					lblSupplier.setValue("L / C No :");
					cmbSupplier.removeAllItems();
					cmbSupplierAddData("L/c");

				}
				if(oppurchaseType.getValue().toString().equalsIgnoreCase("Local Credit Purchase"))
				{
					lblSupplier.setValue("Supplier Name :");	
					cmbSupplier.removeAllItems();
					cmbSupplierAddData("Local Credit Purchase");
				}

				if(oppurchaseType.getValue().toString().equalsIgnoreCase("Cash Purchase"))
				{
					lblSupplier.setValue("Supplier Name :");	
					cmbSupplier.removeAllItems();
					cmbSupplierAddData("Cash Purchase");
				}
			}
		});


		poAttach.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePatRequisition(0,"temp");
				System.out.println("Done");
			}
		});

		poAttach.nbDOBPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString();
					System.out.println(link+"  "+link.endsWith("uptd/"));

					if(link.endsWith("uptd/"))
					{
						link = link.replaceAll("uptd", sessionBean.report)+filePathTmpReq;
					}

					System.out.println(link);
					System.out.println("aa :"+event.getSource());

					getWindow().open(new ExternalResource(link),"_blank", // window name
							500, // width
							200, // weight
							Window.BORDER_NONE // decorations
							);
				}

				if(isUpdate)
				{
					if(!imageLoc.equalsIgnoreCase("0"))
					{
						if(!poAttach.actionCheck)
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								System.out.println("Into "+link);
								link = link.replaceAll("uptd/", imageLoc.substring(22, imageLoc.length()));
								System.out.println("LINK : " +link);
							}
							System.out.println(link);
							System.out.println("bb : "+event.getSource());

							getWindow().open(new ExternalResource(link),"_blank", // window name
									500, // width
									200, // weight
									Window.BORDER_NONE // decorations
									);
						}
					}
					else{
						if(!poAttach.actionCheck){
							getParent().showNotification("There is no Requisition Form",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}

					if(poAttach.actionCheck)
					{
						String link = getApplication().getURL().toString();
						System.out.println(link+"  "+link.endsWith("uptd/"));

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", sessionBean.report)+filePathTmpReq;
						}

						System.out.println(link);
						System.out.println("aa :"+event.getSource());

						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
			}
		});
	}

	private String imagePatRequisition(int flag, String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String  ReqImage = null;

		if(flag==0)
		{
			// image move		
			if(poAttach.fileName.trim().length()>0)
			{
				System.out.println("hello : "+poAttach.fileName.trim());
				if(((String) poAttach.fileExtension).equalsIgnoreCase(".jpg") || poAttach.fileExtension.equalsIgnoreCase(".pdf"))
				{
					try {
						String path = str;
						fileMove(basePath+poAttach.fileName.trim(), SessionBean.imagePathTmp+path+poAttach.fileExtension);
						ReqImage = SessionBean.imagePathTmp+path+poAttach.fileExtension;
						filePathTmpReq = path+poAttach.fileExtension;
					}catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return ReqImage;
		}

		if(flag==1)
		{
			if(poAttach.fileName.trim().length()>0)
			{
				System.out.println("reqAttached.fileName : "+poAttach.fileName.trim());
				if(poAttach.fileExtension.equalsIgnoreCase(".jpg") || poAttach.fileExtension.equalsIgnoreCase(".pdf"))
				{
					try {
						String path =str;
						fileMove(basePath+poAttach.fileName.trim(),SessionBean.Purchase+path+poAttach.fileExtension);
						ReqImage = SessionBean.Purchase+path+poAttach.fileExtension;
						filePathTmpReq = path+poAttach.fileExtension;
					}catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return ReqImage;
		}
		return null;
	}

	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp){}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}



	public void dataload(int i )
	{
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List lst = session.createSQLQuery("select vRawItemCode,vRawItemName,subString(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName))as category,vSubSubCategoryName from tbRawItemInfo where vCategoryType like '"+cmbItemType.getValue().toString()+"' order by category,vSubSubCategoryName").list();

			cmbProduct.get(i).removeAllItems();
			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProduct.get(i).addItem(element[0]);
				String name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
				cmbProduct.get(i).setItemCaption(element[0].toString(), name);

			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error", Notification.TYPE_ERROR_MESSAGE);
		}
	}


	public void productDataLoad(int i )
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
	}


	private void newButtonEvent() 
	{
		txtClear();
		componentIni(false);
		btnIni(false);
		cmbItemType.focus();	
		isUpdate=false;
	}

	private String autoReciptNo(){
		String autoId=null;
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select isnull(max(cast(ReceiptNo as int) ) ,0)+1  from tbRawPurchaseInfo").list().iterator();

			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
				txtreceiptNo.setValue(autoId);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
		return autoId;
	}

	private void refreshButtonEvent() 
	{
		isFind=false;
		isUpdate=false;
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void  autoMrrNo(){
		String autoId=null;
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("select  ISNULL(MAX(CAST(MrrNo as int)),0)+1    from tbRawPurchaseInfo").list().iterator();

			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
				txtmrrNo.setValue(autocodegenerate(autoId,8));

				/*if(autoId.length()==1)
				{
					txtmrrNo.setValue("0000000"+autoId);
				}
				else if (autoId.length()==2)
				{
					txtmrrNo.setValue("000000"+autoId);
				}
				else if (autoId.length()==3)
				{
					txtmrrNo.setValue("00000"+autoId);	
				}
				else if (autoId.length()==4)
				{
					txtmrrNo.setValue("0000"+autoId);	
				}
				else if (autoId.length()==5)
				{
					txtmrrNo.setValue("000"+autoId);	
				}
				else if (autoId.length()==6)
				{
					txtmrrNo.setValue("000"+autoId);	
				}
				else if (autoId.length()==7)
				{
					txtmrrNo.setValue("0"+autoId);

				}*/

				System.out.println("Mrr No Is"+txtmrrNo.getValue().toString()	);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	
	
	private String autocodegenerate(String autocode, int length)
	{
		
		while(autocode.length()<8)
		{
			autocode="0"+autocode;
		}
		
		return autocode;
	
	}
	
	
	

	/*	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}*/

	private void updateButtonEvent(){

		if(cmbSupplier.getValue()!= null)
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
		}
		else
			this.getParent().showNotification(
					"Update Failed",
					"There are no data for update.",
					Notification.TYPE_WARNING_MESSAGE);
	}

	private void componentIni(boolean b) 
	{
		cmbItemType.setEnabled(!b);
		cmbSupplier.setEnabled(!b);
		txtaddress.setEnabled(!b);
		txtreceiptNo.setEnabled(!b);
		dateField.setEnabled(!b);
		dpodate.setEnabled(!b);
		txtchallanNo.setEnabled(!b);
		dChallanDate.setEnabled(!b);
		cmbPoNo.setEnabled(!b);
		poAttach.setEnabled(!b);
		lbLine.setEnabled(!b);
		table.setEnabled(!b);
		txtmrrNo.setEnabled(!b);
		txtvoucherNo.setEnabled(!b);
		txtbillNo.setEnabled(!b);
	}

	public void txtClear()
	{
		try
		{	
			//cmbItemType.setValue(null);
			cmbSupplier.setValue(null);
			txtaddress.setValue("");
			txtreceiptNo.setValue("");
			txtchallanNo.setValue("");
			cmbPoNo.setValue(null);
			//txtvoucherno.setValue("");
			txtvoucherNo.setValue("");
			txtmrrNo.setValue("");
			oppurchaseType.select("Local Credit Purchase");
			opmrrType.select("With MRR");
			txtbillNo.setValue("");
			for(int i=0;i<unit.size();i++)
			{
				cmbProduct.get(i).setValue(null);
				unit.get(i).setValue("");
				amount.get(i).setValue("");
				rate.get(i).setValue("");
				poQty.get(i).setValue("");
				okQty.get(i).setValue("");
				//qtyBag.get(i).setValue("");
				rejectQty.get(i).setValue("");
				tbTxtRemark.get(i).setValue("");
				tCmbStoreLocation.get(i).setValue(null);
			}
			table.setColumnFooter("Amount", "Total:"+0.0);
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}

	}


	private void supplierValueChange(){
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List lst = session.createSQLQuery("select 0,address from tbSupplierInfo where supplierId like '"+cmbSupplier.getValue().toString()+"' ").list();
			Iterator iter = lst.iterator(); 
			if(iter.hasNext())
			{
				txtaddress.setValue("");
				Object[] element = (Object[]) iter.next();
				txtaddress.setValue(element[1].toString());
				txtchallanNo.focus();
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}


	public void tableInitialise(){
		for(int i=0;i<10;i++){
			tableRowAdd(i);
		}
	}

	public void cmbPONoData()
	{
		cmbPoNo.removeAllItems();
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			cmbPoNo.removeAllItems();
			String sql = "";
			sql = "Select 0, poNo from dbo.tbRawPurchaseOrderInfo  where  supplierId = '"+cmbSupplier.getValue().toString()+"'";
			System.out.println(sql);
			List lst = session.createSQLQuery(sql).list();

			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPoNo.addItem(element[1]);
				//cmbPoNo.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error", Notification.TYPE_ERROR_MESSAGE);
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

			cmbProduct.add(ar,new ComboBox());
			cmbProduct.get(ar).setWidth("100%");
			cmbProduct.get(ar).setImmediate(true);
			cmbProduct.get(ar).setNullSelectionAllowed(false);
			cmbProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			unit.add(ar,new TextRead(""));
			unit.get(ar).setWidth("100%");


			poQty.add( ar , new TextRead());
			poQty.get(ar).setWidth("100%");
			poQty.get(ar).setImmediate(true);

			leftQty.add( ar , new TextRead());
			leftQty.get(ar).setWidth("100%");
			leftQty.get(ar).setImmediate(true);


			okQty.add( ar , new AmountCommaSeperator());
			okQty.get(ar).setWidth("90%");
			okQty.get(ar).setImmediate(true);

			/*qtyBag.add( ar , new AmountField());
			qtyBag.get(ar).setWidth("100%");
			qtyBag.get(ar).setImmediate(true);*/


			rejectQty.add( ar , new AmountCommaSeperator());
			rejectQty.get(ar).setWidth("90%");
			rejectQty.get(ar).setImmediate(true);

			rate.add(ar,new AmountCommaSeperator());
			rate.get(ar).setWidth("90%");
			rate.get(ar).setImmediate(true);

			amount.add( ar , new TextRead(1));
			amount.get(ar).setWidth("90%");

			tbTxtRemark.add( ar , new TextField());
			tbTxtRemark.get(ar).setWidth("100%");

			tCmbStoreLocation.add( ar , new ComboBox());
			tCmbStoreLocation.get(ar).setWidth("100%");

			List list = session.createSQLQuery(" select  vDepoId,vDepoName from tbDepoInformation").list();

			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				tCmbStoreLocation.get(ar).addItem(element[0].toString());
				tCmbStoreLocation.get(ar).setItemCaption(element[0].toString(), element[1].toString());
			}

			cmbProduct.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
				{
					boolean fla = true;
					if(cmbProduct.get(ar).getValue()!=null)
					{
						fla=(doubleEntryCheck(cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue()),ar));	
						if ((Object)cmbProduct.get(ar).getValue()!=null && fla )
						{
							String productid=cmbProduct.get(ar).getValue().toString();
							if (!isFind)
							{
								int temp=cmbProduct.size();
								String flag=cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue());
								String head = cmbProduct.get(ar).getValue().toString();

								Session session1=SessionFactoryUtil.getInstance().getCurrentSession();
								Transaction tx1=session1.beginTransaction();
								List lst = session1.createSQLQuery(" select 0, vUnitName from tbRawItemInfo where vRawItemCode like  '"+cmbProduct.get(ar).getValue()+"' ").list();
								System.out.print(head);
								Iterator iter = lst.iterator();
								if(iter.hasNext())
								{
									Object[] element = (Object[]) iter.next();
									unit.get(ar).setValue(element[1].toString());
									//qtyBag.get(ar).focus();

								}

								if(cmbPoNo.getValue()!=null)
								{
									List lst1 = session1.createSQLQuery("  select 0, qty,leftQty from tbRawPurchaseOrderDetails where poNo like '"+cmbPoNo.getValue().toString()+"' and productId like '"+cmbProduct.get(ar).getValue().toString()+"' ").list();
									System.out.print(head);
									Iterator iter1 = lst1.iterator();
									if(iter1.hasNext())
									{
										Object[] element = (Object[]) iter1.next();
										poQty.get(ar).setValue(df.format(Double.parseDouble(element[1].toString()) ));
										leftQty.get(ar).setValue(df.format(Double.parseDouble(element[2].toString()) ));
										//lblSection.get(ar).setValue(element[2].toString());
									}	
								}

								tableColumnAction(head,ar);
								if(ar==temp-1)
								{
									tableRowAdd(temp);
									if(cmbPoNo.getValue()!=null)
									{
										productDataLoad(temp)	;
									}
									else
									{
										dataload(temp);
									}

								}	
							}

							else
							{
								String productId="";
								if(hmproduct.get(ar)!=null)
								{
									productId=(String) hmproduct.get(ar);	
								}
								else
								{
									productId="";	
								}

								if(!querycheck(productId))
								{
									showNotification("Previous Item Can Not be Changed",Notification.TYPE_WARNING_MESSAGE);
									cmbProduct.get(ar).setValue(hmproduct.get(ar));
								}

								else
								{
									int temp=cmbProduct.size();
									String flag=cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue());
									String head = cmbProduct.get(ar).getValue().toString();

									Session session1=SessionFactoryUtil.getInstance().getCurrentSession();
									Transaction tx1=session1.beginTransaction();
									List lst = session1.createSQLQuery(" select 0, vUnitName from tbRawItemInfo where vRawItemCode like  '"+cmbProduct.get(ar).getValue()+"' ").list();
									System.out.print(head);
									Iterator iter = lst.iterator();
									if(iter.hasNext())
									{
										Object[] element = (Object[]) iter.next();
										unit.get(ar).setValue(element[1].toString());
									}

									if(cmbPoNo.getValue()!=null)
									{
										List lst1 = session1.createSQLQuery("  select 0, qty,leftQty from tbRawPurchaseOrderDetails where poNo like '"+cmbPoNo.getValue().toString()+"' and productId like '"+cmbProduct.get(ar).getValue().toString()+"' ").list();
										System.out.print(head);
										Iterator iter1 = lst1.iterator();
										if(iter1.hasNext())
										{
											Object[] element = (Object[]) iter1.next();
											poQty.get(ar).setValue(df.format(Double.parseDouble(element[1].toString()) ));
											leftQty.get(ar).setValue(df.format(Double.parseDouble(element[2].toString()) ));
											//lblSection.get(ar).setValue(element[2].toString());
										}	
									}

									tableColumnAction(head,ar);

									if(ar==temp-1)
									{
										tableRowAdd(temp);
										if(cmbPoNo.getValue()!=null)
										{
											productDataLoad(temp)	;
										}
										else
										{
											dataload(temp);
										}
									}
								}
							}
						}

						else
						{	
							getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							cmbProduct.get(ar).setValue(null);
						}
					}

				}
			});
			table.addItem(new Object[]{tbLblSl.get(ar),cmbProduct.get(ar),unit.get(ar),poQty.get(ar),leftQty.get(ar),okQty.get(ar),rejectQty.get(ar),rate.get(ar),amount.get(ar),tbTxtRemark.get(ar),tCmbStoreLocation.get(ar)},ar);
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void bagKgCalc(int ar)
	{

		double kg = Double.parseDouble(okQty.get(ar).getValue().toString().isEmpty()? "0.0":okQty.get(ar).getValue().toString());
		double bag = kg/25;
		qtyBag.get(ar).setValue(df.format(bag));	
		System.out.println("Error");

	}

	private boolean doubleEntryCheck(String caption,int row){

		for(int i=0;i<amount.size();i++){

			if(i!=row && caption.equals(cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue()))){

				return false;
			}
		}
		return true;
	}

	private boolean querycheck(String productid)
	{
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String query="select * from tbRawIssueInfo a inner join tbRawIssueDetails b "
				+"on a.IssueNo=b.IssueNo where a.Date >'"+dateformat.format(dateField.getValue())+"' and  b.ProductID like '"+productid+"' ";

		List lst= session.createSQLQuery(query).list();

		if(!lst.isEmpty())
		{
			return false;	
		}



		return true;		
	}


	private void tableColumnAction(final String head,final int r)
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
					cmbProduct.get(r+1).focus();
				}
				catch(Exception exp)
				{
					getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		rate.get(r).addListener(new ValueChangeListener() {
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

						if(!event.getProperty().toString().trim().isEmpty())
						{
							tbquntity=event.getProperty().toString().trim().replaceAll(",", "");
							String tempPrice=okQty.get(r).getValue().toString().isEmpty()?"0.0":okQty.get(r).getValue().toString().replaceAll(",", "");
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

					cmbProduct.get(r+1).focus();
				}
				catch(Exception exp)
				{
					getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		okQty.get(r).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				try
				{
						if(!okQty.get(r).getValue().toString().isEmpty())
						{
						if(Double.parseDouble(okQty.get(r).getValue().toString().isEmpty()?"0":okQty.get(r).getValue().toString())>0 )
								//if(Double.parseDouble(okQty.get(r).getValue().toString())>0 )
							{
						
							if(cmbProduct.get(r).getValue()!=null)
							{
								String productid="";
								double tbquntity;
								if(hmproduct.get(r)!=null)
								{
									productid=(String) hmproduct.get(r);
								}

								if (isFind)
								{
									if(querycheck(productid) )
									{
										if(Double.parseDouble(okQty.get(r).getValue().toString().isEmpty()?"0":okQty.get(r).getValue().toString())>0)
										{
											//bagKgCalc(r);
											//tbquntity = event.getProperty().toString().trim().isEmpty()? 0: Double.parseDouble(event.getProperty().toString().trim());
											if(!leftQty.get(r).getValue().toString().isEmpty() && !okQty.get(r).getValue().toString().isEmpty()){
												rejectQty.get(r).setValue( (Double.parseDouble(leftQty.get(r).getValue().toString().replaceAll(",", ""))) - (Double.parseDouble(okQty.get(r).getValue().toString().replaceAll(",", "")))  ); 
												//qtyBag.get(r).setValue(decimalf.format(tbquntity/25));
											}
											if(!isUpdate)
											{
												if(Double.parseDouble(rejectQty.get(r).getValue().toString().isEmpty()?"0":rejectQty.get(r).getValue().toString())<0)
												{
													okQty.get(r).focus();
													showNotification("Qty Exceed Left Qty",Notification.TYPE_WARNING_MESSAGE);
													okQty.get(r).setValue("");
													//qtyBag.get(r).setValue("");
													rejectQty.get(r).setValue("");

												}
											}	
										}									  
									}

									else
									{
										showNotification("Previuos transaction can't be changed,Purchase qty already issued",Notification.TYPE_WARNING_MESSAGE);
										okQty.get(r).setValue(hmqty.get(r));
									}	
								}

								else if (!isFind)
								{

									System.out.println("Sa");
									//bagKgCalc(r);
									if(!leftQty.get(r).getValue().toString().isEmpty() && !okQty.get(r).getValue().toString().isEmpty()){
										rejectQty.get(r).setValue( (Double.parseDouble(leftQty.get(r).getValue().toString().replaceAll(",", ""))) - (Double.parseDouble(okQty.get(r).getValue().toString().replaceAll(",", "")))  ); 
										//qtyBag.get(r).setValue(decimalf.format(tbquntity/25));
										System.out.println("Find: "+isFind);
										System.out.println("Find: "+isUpdate);

										System.out.println("Rabiul Hasan");
									}
									if(!isUpdate)
									{
										if(Double.parseDouble(rejectQty.get(r).getValue().toString().isEmpty()?"0":rejectQty.get(r).getValue().toString())<0)
										{
											okQty.get(r).focus();
											showNotification("Qty exceed left qty",Notification.TYPE_WARNING_MESSAGE);
											okQty.get(r).setValue("");
											//qtyBag.get(r).setValue("");
											rejectQty.get(r).setValue("");
										}
									}	
								}
							}	
						}
						else
						{
							showNotification("Warning!!","ok qty can't be zero.",Notification.TYPE_WARNING_MESSAGE);
							okQty.get(r).setValue("");
							
						}				
					}
							
				}
				catch(Exception exp)
				{
					getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

	/*	qtyBag.get(r).addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProduct.get(r).getValue()!=null && !qtyBag.get(r).getValue().toString().isEmpty())
				{
					if(Double.parseDouble(qtyBag.get(r).getValue().toString().isEmpty()?"0":qtyBag.get(r).getValue().toString())>0 )
					{
					double bag = Double.parseDouble(qtyBag.get(r).getValue().toString()) ;
					double  kg = bag*25;
					okQty.get(r).setValue(df.format(kg).toString());
					}
					
					else
					{
						showNotification("Warning!!","bag qty can't be zero.",Notification.TYPE_WARNING_MESSAGE);
						qtyBag.get(r).setValue("");
						
					}
				}
			}
		});
*/
		poQty.get(r).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try
				{
					if(!poQty.get(r).getValue().toString().isEmpty() && !okQty.get(r).getValue().toString().isEmpty()){
						rejectQty.get(r).setValue( (Double.parseDouble(poQty.get(r).getValue().toString().replaceAll(",", ""))) - (Double.parseDouble(okQty.get(r).getValue().toString().replaceAll(",", "")))  ); 
					}
					cmbProduct.get(r+1).focus();
				}
				catch(Exception exp)
				{
					getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}	
			}
		});


		amount.get(r).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(!amount.get(r).getValue().toString().isEmpty()){
					double total = 0.0;
					for(int i = 0;i<amount.size();i++){
						if(!amount.get(i).getValue().toString().isEmpty()){
							total+=Double.parseDouble(amount.get(i).getValue().toString());
							table.setColumnFooter("Amount", "Total:"+new com.common.share.CommaSeparator().setComma(total));
						}

					}
				}
			}
		});


	}

	public void cmbSupplierAddData(String caption){

		Transaction tx;
		try{

			System.out.println("Done");

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			cmbSupplier.removeAllItems();
			supplierAddress.clear();
			List lst=null;
			if(caption.equalsIgnoreCase("Local Credit Purchase"))
			{
				lst = session.createSQLQuery("select supplierId,supplierName from tbSupplierInfo order by supplierName--where supplierId in(select supplierId from tbRawPurchaseOrderInfo)").list();	
			}
			if(caption.equalsIgnoreCase("L/c"))
			{
				lst = session.createSQLQuery("select distinct  Ledger_Id,Ledger_Name from tbLedger where Create_From like '%A4-G400%' ").list();
			}

			if(caption.equalsIgnoreCase("Cash Purchase"))
			{
				lst = session.createSQLQuery("select distinct  Ledger_Id,Ledger_Name from tbLedger where Create_From like '%A4-G402%' or Create_From='A7'").list();
			}


			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplier.addItem(element[0].toString());
				cmbSupplier.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}


	@SuppressWarnings("unused")
	public void purchaseTypeDataLoad(){

		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			cmbSupplier.removeAllItems();
			supplierAddress.clear();
			List lst = session.createSQLQuery("  select  distinct 0,vCategoryType from tbRawItemCategory  where vCategoryType like 'Spare Parts' ").list();

			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbItemType.addItem(element[1].toString());
				cmbItemType.setItemCaption(element[1].toString(), element[1].toString());
				cmbItemType.setValue(element[1].toString());
				//supplierAddress.put(element[1], element[2]);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	public String supplierLedger() 
	{
		String autoCode = "";

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query="select Ledger_Id from tbLedger where Ledger_Id=(select ledgerCode from tbSupplierInfo where supplierId like '"+cmbSupplier.getValue().toString()+"')"; 

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}

	public String productlededger(int i) 
	{
		String autoCode = "";

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query="select Ledger_Id  from tbLedger where Ledger_Id=(select vLedgerCode from tbRawItemInfo where vRawItemCode like '"+cmbProduct.get(i).getValue().toString()+"')";
			System.out.println("ledgerpr"+query);
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}

	private boolean tableDataCheck(){

		for(int a=0;a<cmbProduct.size();a++){
			if(cmbProduct.get(a).getValue()!=null  ){
				if(tCmbStoreLocation.get(a).getValue()==null){
					return false;
				}
			}
		}
		return true;
	}

	private void executeProcedure() {
		String autoCode = "";

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query="exec PrcPOleftQty ''";
			session.createSQLQuery(query).executeUpdate();
			tx.commit();

		} 
		catch (Exception ex) 
		{
			tx.rollback();
			showNotification("From Procedure"+ex,Notification.TYPE_ERROR_MESSAGE);
		}

	}
	private void leftCheck()
	{
		if(cmbPoNo.getValue()!=null)
		{
			for(int a=0;a<cmbProduct.size();a++){
				if(cmbProduct.get(a).getValue()!=null){
					double value = 0;
					double d=Double.parseDouble(okQty.get(a).getValue().toString());

					if(hMap.get(a)!=null)
					{
						if(Double.parseDouble(hMap.get(a).toString())!=d)
						{
							double difference=Double.parseDouble(hMap.get(a).toString())-d;
							if(!leftQty.get(a).getValue().toString().isEmpty())
							{
								if(difference>0)
								{
									value=Double.parseDouble(leftQty.get(a).getValue().toString())-difference;
								}
								else if(difference<0)
								{
									value=Double.parseDouble(leftQty.get(a).getValue().toString())+difference;
								}
							}
						}	
					}

					else if(hMap.get(a)==null)
					{
						if(!leftQty.get(a).getValue().toString().isEmpty())
						{

							value=Double.parseDouble(leftQty.get(a).getValue().toString())-d;
						}

					}


					Transaction tx=null;
					try{
						Session session=SessionFactoryUtil.getInstance().getCurrentSession();
						tx=session.beginTransaction();
						String sql="update tbRawPurchaseOrderDetails set leftQty='"+value+"' where poNo=" +
								"'"+cmbPoNo.getValue()+"' and productId='"+cmbProduct.get(a).getValue()+"'";
						session.createSQLQuery(sql).executeUpdate();
						tx.commit();
					}
					catch(Exception exp)
					{
						tx.rollback();
						showNotification("From LeftCheck: "+exp,Notification.TYPE_ERROR_MESSAGE);
					}
				}
			}	
		}		
	}
	private void saveButtonEvent() 
	{
		if(cmbSupplier.getValue()!=null)
		{
			if(!txtchallanNo.getValue().toString().trim().isEmpty())
			{
				if(!txttotalField.getValue().toString().trim().isEmpty())
				{
					if(cmbItemType.getValue()!=null)
					{
						if(!txtbillNo.getValue().toString().isEmpty())
						{
							if(cmbProduct.get(0).getValue()!=null)
							{
								if(tableDataCheck()){

									if(isUpdate)
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
														leftCheck();
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
													executeProcedure();
												}
											}

										});	

									}
								}
								else{
									this.getParent().showNotification("Warning :","Please Select Store Location.", Notification.TYPE_WARNING_MESSAGE);
								}

							}

							else
							{
								this.getParent().showNotification("Warning :","Please Select Product Name.", Notification.TYPE_WARNING_MESSAGE);	
							}	
						}
						else
						{
							this.getParent().showNotification("Warning :","Please Provide Bill No.", Notification.TYPE_WARNING_MESSAGE);	
						}

					}

					else{
						this.getParent().showNotification("Warning :","Please Select Item  Type .", Notification.TYPE_WARNING_MESSAGE);
					}

				}
				else
					this.getParent().showNotification("Warning :","Please Select Rate.", Notification.TYPE_WARNING_MESSAGE);	
			}
			else
				this.getParent().showNotification("Warning :","Please Enter Challen No..", Notification.TYPE_WARNING_MESSAGE);

		}
		else
			this.getParent().showNotification("Warning :","Please Select Supplier Name.", Notification.TYPE_WARNING_MESSAGE);
	}

	public boolean deleteData(Session session,Transaction tx){
		try{

			session.createSQLQuery("delete tbRawPurchaseDetails  where ReceiptNo='"+txtreceiptNo.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete tbRawPurchaseInfo  where ReceiptNo='"+txtreceiptNo.getValue()+"'").executeUpdate();
			return true;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void tbCmbAddData(){
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List lst = session.createSQLQuery("Select ProductName, ProductCode, Unit,Ledger_Id from  tbRawProductInfo order by ProductName").list();
			for(int ar=0;ar<cmbProduct.size();ar++){
				for (Iterator iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbProduct.get(ar).addItem(element[1].toString());
					cmbProduct.get(ar).setItemCaption(element[1].toString(), element[0].toString());
					productUnit.put(element[1], element[2]);
				}
			}

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

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


	private void insertData(Session session,Transaction tx) 
	{
		if(isUpdate){
			session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
		}
		String voucharType = "";
		String sqlInfo="",sqlDetails="",sqlUdInfo="",sqlUdDetails="",  vocherId="";

		System.out.println(vocherId);
		String suplierId=supplierLedger();
		String imgPatReq = imagePatRequisition(1,txtreceiptNo.getValue().toString())==null?imageLoc:imagePatRequisition(1,txtreceiptNo.getValue().toString());

		try
		{
			String id = "";
			voucharType = Option();
			if(!isUpdate)
			{
				id=autoReciptNo();
			}
			else
			{
				id=txtreceiptNo.getValue().toString();
			}


			sqlInfo = "insert into tbRawPurchaseInfo values (" +
					" '"+txtreceiptNo.getValue().toString()+"','"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dateField.getValue()) + "', " +
					" '"+cmbSupplier.getValue()+"','"+txttotalField.getValue()+"','"+txtchallanNo.getValue()+"', " +
					" '"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dChallanDate.getValue())+"'," +
					" '"+(cmbPoNo.getValue()!=null?cmbPoNo.getValue().toString().trim():"0")+"'," +
					" '"+imgPatReq+"','"+vocherId+"','" +voucharType+"','0'," +
					" '0','"+sessionBean.getUserName()+ "'," +
					" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '"+cmbItemType.getItemCaption(cmbItemType.getValue())+"','"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dpodate.getValue()) + "', " +
					"'"+txtmrrNo.getValue().toString()+"', '"+opmrrType.getValue()+"', '"+oppurchaseType.getValue()+"','0'," +
					"'"+txtbillNo.getValue().toString()+"',''  )";	
			session.createSQLQuery(sqlInfo).executeUpdate();
			System.out.println(sqlInfo);

			sqlUdInfo = "insert into tbUdRawPurchaseInfo values(" +
					" '" +txtreceiptNo.getValue().toString()+ "'," +
					" '" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dateField.getValue()) + "','" + cmbSupplier.getValue() + "'," +
					" '" +txttotalField.getValue()+"','" +txtchallanNo.getValue()+ "','" +vocherId+ "','" +voucharType+"'," +
					" '0','0'," +
					" '"+sessionBean.getUserName()+"','"+udFlag+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '"+cmbItemType.getItemCaption(cmbItemType.getValue())+"','"+txtbillNo.getValue().toString()+"')";
			session.createSQLQuery(sqlUdInfo).executeUpdate();
			System.out.println(sqlUdInfo);
			for (int i = 0; i < cmbProduct.size(); i++)
			{
				Object temp = cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue());
				if (cmbProduct.get(i).getValue()!= null )
				{
					String productId =cmbProduct.get(i).getValue().toString().trim();

					String ProductLedeger="";
					ProductLedeger= productlededger(i);
					sqlDetails = "insert into tbRawPurchaseDetails values(" +
							" '"+txtreceiptNo.getValue().toString()+"','"+productId.trim()+"','"+poQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
							" '"+okQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
							" '"+rejectQty.get(i).getValue().toString().replaceAll(",","")+"'," +
							" '"+rate.get(i).getValue().toString().replaceAll(",", "")+"','"+tbTxtRemark.get(i).getValue().toString()+"', " +
							" '"+sessionBean.getUserName()+"'," +
							" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" '"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dateField.getValue()) + "'," +
							" '"+(tCmbStoreLocation.get(i).getValue()!=null?tCmbStoreLocation.get(i).getValue().toString():"")+"'," +
							" '"+(tCmbStoreLocation.get(i).getValue()!=null?tCmbStoreLocation.get(i).getItemCaption(tCmbStoreLocation.get(i).getValue()):"")+"','"+cmbItemType.getItemCaption(cmbItemType.getValue())+"') ";

					session.createSQLQuery(sqlDetails).executeUpdate();
					System.out.println(sqlDetails);
					
					sqlUdDetails = "insert into tbUdRawPurchaseDetails values(" +
							" '"+txtreceiptNo.getValue().toString()+"','"+productId.trim()+"','"+poQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
							" '"+okQty.get(i).getValue().toString().replaceAll(",", "")+"', '"+rejectQty.get(i).getValue().toString().replaceAll(",","")+"'," +
							" '"+rate.get(i).getValue().toString().replaceAll(",", "")+"','"+sessionBean.getUserName()+"'," +
							" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" '"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dateField.getValue()) + "'," +
							" '"+(tCmbStoreLocation.get(i).getValue()!=null?tCmbStoreLocation.get(i).getValue().toString():"")+"'," +
							" '"+(tCmbStoreLocation.get(i).getValue()!=null?tCmbStoreLocation.get(i).getItemCaption(tCmbStoreLocation.get(i).getValue()):"")+"') ";


					session.createSQLQuery(sqlUdDetails).executeUpdate();
					System.out.println(sqlUdDetails);


					String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dateField.getValue())+"')").list().iterator().next().toString();
					String voucher =  "voucher"+fsl;

					String pono=" ";
					if(cmbPoNo.getValue()!=null)
					{
						pono="-PO. No:-"+cmbPoNo.getValue().toString();
					}

					String Naration=cmbItemType.getItemCaption(cmbItemType.getValue().toString())+pono+"-Challan No:-"+txtchallanNo.getValue()+"-Challan Date:-"+new SimpleDateFormat("yyyy-MM-dd").format(dChallanDate.getValue()).toString()+"-Receipt No: "+txtreceiptNo.getValue();

					System.out.println("Receipt Data"+voucher);

				}
			}

			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			txtClear();
			componentIni(true);
			btnIni(true);
		}catch(Exception exp){
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public String vocherIdGenerate()
	{
		String vo_id = null;
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query="select 'JV-NO-' + cast(isnull(max(cast(replace(Voucher_No, 'JV-NO-', '')as int))+1, 1)as varchar) from vwVoucher where substring(vouchertype ,1,1) = 'j'";
			Iterator iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				vo_id=iter.next().toString().trim();
			}
		}
		catch(Exception ex){

			this.getParent().showNotification(
					"Error",
					ex+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
		return vo_id;
	}


	private String Option(){
		strFlag="debit";
		if(strFlag.equalsIgnoreCase("debit"))
			strFlag= "RDP";
		else
			strFlag= "RCP";

		return strFlag;
	}

	private void findButtonEvent()
	{
		Window win=new FindWindowSP(sessionBean,txtReceiptId,"spareissue");
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
	}

	private void findInitialise(){
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List led = session.createSQLQuery("select * from tbRawPurchaseInfo where ReceiptNo='"+txtReceiptId.getValue().toString().trim()+"' ").list();
			System.out.println("select * from tbRawPurchaseInfo where ReceiptNo='"+txtReceiptId.getValue().toString().trim()+"'");

			txtClear();
			if(led.iterator().hasNext()){
				Object[] element = (Object[]) led.iterator().next();


				oppurchaseType.select(element[20]);
				txtreceiptNo.setValue(element[1]);
				dateField.setValue(element[2]);
				cmbSupplier.setValue(element[3]);
				//cmbPONoData();
				txttotalField.setValue(element[4]);
				txtchallanNo.setValue(element[5]);
				dChallanDate.setValue(element[6]);
				cmbPoNo.setValue(element[7]);
				cmbPoNo.addItem(element[7]);
				txtvoucherNo.setValue(element[9]);

				cmbItemType.setValue(element[16].toString());

				opmrrType.select(element[19]);
				txtmrrNo.setValue(element[18]);
				txtbillNo.setValue(element[22]);

				if(!element[8].toString().equals("0"))
				{
					imageLoc = element[8].toString();
					poAttach.status.setValue("Receipt No: "+imageLoc.substring(43));
				}
			}
			System.out.println(txtReceiptId.getValue().toString().trim());
			List list=session.createSQLQuery("select * from tbRawPurchaseDetails where" +
					" ReceiptNo='"+txtReceiptId.getValue().toString().trim()+"' ").list();
			int i=0;



			for(Iterator iter=list.iterator();iter.hasNext();){
				Object[] element=(Object[]) iter.next();
				System.out.println(element[2]);
				cmbProduct.get(i).addItem(element[2]);
				hmproduct.put(i,element[2] );
				cmbProduct.get(i).setValue(element[2]);
				poQty.get(i).setValue(element[3]);
				okQty.get(i).setValue(element[4]);
				hmqty.put(i,element[4]);
				hMap.put(i, element[4]);
				rejectQty.get(i).setValue(element[5]);
				rate.get(i).setValue(df.format(element[6]));
				tbTxtRemark.get(i).setValue(element[7]);
				if(!element[12].toString().equals("0"))
				{
					tCmbStoreLocation.get(i).setValue(element[12].toString());
				}

				i++;
			}
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}


}
