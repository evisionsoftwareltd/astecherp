package com.example.rawMaterialTransaction;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.naming.java.javaURLContextFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.setupTransaction.SupplierInformation;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class RawMaterialPurchase extends Window 
{
	private SessionBean sessionBean;
	private Label lblSupplier;
	private Label lblAddress;
	private Label lblChallan;
	private Label lblChallanDate;
	private Label lblBillNo;
	private Label lblBillDate;
	private Label lblInvoiceNo;
	private Label lblInvoiceDate;
	private Label lblDate;
	private Label lblTransactionNo;
	private Label  lblReceiptNo;
	private Label lblPONo;
	private Label  lblPODate;
	private Label lblTransaportReceiptNo;
	private Label  lblReceiptDate;
	private Label lblQualityCertificate;
	private Label lblUnit;
	private Label lblSubItemName;
	private Label lblSpecification;

	private ComboBox cmbSupplier;
	private TextField txtAddress;
	private TextField txtChallanNo;
	private PopupDateField dChallanDate;
	private TextField txtBillNo;
	private PopupDateField dBillDate;
	private TextField txtInvoiceNo;
	private PopupDateField dInvoiceDate;
	private PopupDateField dDate;
	private TextField txtTransactionNo;
	private TextField txtReceiptNo;
	private TextField txtPONo;
	private PopupDateField dPODate;
	private TextField txtTransportReceipt;
	private PopupDateField dReceiptDate;
	private TextField txtQualityCerificate;
	private TextRead txtUnit;
	private TextRead txtSubItemName;
	private TextRead txtSpecification;

	private PopupDateField dateField;
	private TextField txtpo;

	public NativeButton btnSection;

	private Table table = new Table();
	private ArrayList<Label>tblblsl = new ArrayList<Label>();
	private ArrayList<CheckBox>tbChkShow= new ArrayList<CheckBox>();
	private ArrayList<ComboBox> tbcmbGroup = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbcmbItem = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbcmbProductCode = new ArrayList<ComboBox>();
	private ArrayList<TextRead>tbtxtSubIttemName = new ArrayList<TextRead>();
	private ArrayList<TextRead>tbtxtUnit= new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtSpecification =new ArrayList<TextRead>();
	private ArrayList<AmountField> tbaQty = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbaRate= new ArrayList<AmountField>();
	private ArrayList<TextRead> tbtxtAmount = new ArrayList<TextRead>();
	private ArrayList<ComboBox> tbcmbStoreName = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbcmbRackName= new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbcmbShelfName = new ArrayList<ComboBox>();
	private ArrayList<TextField> tbtxtRemarks = new ArrayList<TextField>();

	ArrayList<Component> allComp = new ArrayList<Component>();

	String dateFieldWidth = "100px";
	String textFieldWidth = "140px";
	String comboWidth = "220px";
	boolean isUpdate=false,isFind=false;
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private DecimalFormat decimalform = new DecimalFormat("#0.00");

	private Label lbLine;
	private CommonButton cButton=new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");

	private HashMap hRate = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();
	DecimalFormat df=new DecimalFormat("#.##");
	private TextField txtPurchaseOrderId = new TextField();

	private AbsoluteLayout mainLayout;
	public RawMaterialPurchase(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("MATERIAL RECEIPT ENTRY :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		setEventAction();
		btnIni(true);
		componentIni(true);
		focusEnter();
		cmbSupplierAddData("Suppiler");

	}

	public void cmbSupplierAddData(String caption){

		Transaction tx;
		try{

			System.out.println("Done");

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			cmbSupplier.removeAllItems();
		//	supplierAddress.clear();
			List lst=null;
			if(caption.equalsIgnoreCase("Suppiler"))
			{
				lst = session.createSQLQuery("select supplierId,supplierName from tbSupplierInfo order by supplierName--where supplierId in(select supplierId from tbRawPurchaseOrderInfo)").list();	
			}
			else
			{
				lst = session.createSQLQuery("select distinct  Ledger_Id,Ledger_Name from tbLedger where Create_From like '%A4-G400%' ").list();
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
	private AbsoluteLayout buildMainLayout() {
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1240px");
		mainLayout.setHeight("540px");

		lblSupplier = new Label();
		lblSupplier.setImmediate(true);
		lblSupplier.setWidth("-1px");
		lblSupplier.setHeight("-1px");
		lblSupplier.setValue("Supplier Name :");
		mainLayout.addComponent(lblSupplier, "top:18.0px;left:20.0px;");

		cmbSupplier = new ComboBox();
		cmbSupplier.setImmediate(true);
		cmbSupplier.setNewItemsAllowed(false);
		cmbSupplier.setNullSelectionAllowed(true);
		cmbSupplier.setWidth("200px");
		cmbSupplier.setHeight("-1px");
		mainLayout.addComponent(cmbSupplier, "top:16.0px;left:120.0px;");

		lblAddress = new Label();
		lblAddress.setImmediate(true);
		lblAddress.setWidth("-1px");
		lblAddress.setHeight("-1px");
		lblAddress.setValue("Address :");
		mainLayout.addComponent(lblAddress, "top:43.0px;left:20.0px;");

		txtAddress = new TextField();
		txtAddress.setImmediate(true);
		txtAddress.setWidth("200px");
		txtAddress.setHeight("40px");
		mainLayout.addComponent(txtAddress, "top:41.0px;left:120.0px;");

		lblChallan = new Label();
		lblChallan.setImmediate(true);
		lblChallan.setWidth("-1px");
		lblChallan.setHeight("-1px");
		lblChallan.setValue("Challan No :");
		mainLayout.addComponent(lblChallan, "top:84.0px;left:20.0px;");

		txtChallanNo= new TextField();
		txtChallanNo.setImmediate(true);
		txtChallanNo.setWidth("106px");
		txtChallanNo.setHeight("-1px");
		mainLayout.addComponent(txtChallanNo, "top:82.0px;left:120.0px;");

		lblChallanDate = new Label();
		lblChallanDate.setImmediate(true);
		lblChallanDate.setWidth("-1px");
		lblChallanDate.setHeight("-1px");
		lblChallanDate.setValue("Challan Date :");
		mainLayout.addComponent(lblChallanDate, "top:110.0px;left:20.0px;");

		dChallanDate = new PopupDateField();
		dChallanDate.setImmediate(true);
		dChallanDate.setWidth("-1px");
		dChallanDate.setHeight("-1px");
		dChallanDate.setDateFormat("dd-MM-yyyy");
		dChallanDate.setValue(new java.util.Date());
		dChallanDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dChallanDate, "top:108.0px;left:120.0px;");

		lblBillNo = new Label();
		lblBillNo.setImmediate(true);
		lblBillNo.setWidth("-1px");
		lblBillNo.setHeight("-1px");
		lblBillNo.setValue("Bill No :");
		mainLayout.addComponent(lblBillNo, "top:18.0px;left:330.0px;");

		txtBillNo= new TextField();
		txtBillNo.setImmediate(true);
		txtBillNo.setWidth("106px");
		txtBillNo.setHeight("-1px");
		mainLayout.addComponent(txtBillNo, "top:16.0px;left:420.0px;");

		lblBillDate = new Label();
		lblBillDate.setImmediate(true);
		lblBillDate.setWidth("-1px");
		lblBillDate.setHeight("-1px");
		lblBillDate.setValue("Bill Date :");
		mainLayout.addComponent(lblBillDate, "top:44.0px;left:330.0px;");

		dBillDate = new PopupDateField();
		dBillDate.setImmediate(true);
		dBillDate.setWidth("-1px");
		dBillDate.setHeight("-1px");
		dBillDate.setDateFormat("dd-MM-yyyy");
		dBillDate.setValue(new java.util.Date());
		dBillDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dBillDate, "top:42.0px;left:420.0px;");

		lblTransactionNo = new Label();
		lblTransactionNo.setImmediate(true);
		lblTransactionNo.setWidth("-1px");
		lblTransactionNo.setHeight("-1px");
		lblTransactionNo.setValue("Transaction No:");
		mainLayout.addComponent(lblTransactionNo, "top:68.0px;left:330.0px;");

		txtTransactionNo= new TextField();
		txtTransactionNo.setImmediate(true);
		txtTransactionNo.setWidth("106px");
		txtTransactionNo.setHeight("-1px");
		mainLayout.addComponent(txtTransactionNo, "top:66.0px;left:420.0px;");

		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:93.0px;left:330.0px;");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("-1px");
		dDate.setHeight("-1px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:91.0px;left:420.0px;");

		lblInvoiceNo = new Label();
		lblInvoiceNo.setImmediate(true);
		lblInvoiceNo.setWidth("-1px");
		lblInvoiceNo.setHeight("-1px");
		lblInvoiceNo.setValue("Invoice No :");
		mainLayout.addComponent(lblInvoiceNo, "top:18.0px;left:540.0px;");

		txtInvoiceNo= new TextField();
		txtInvoiceNo.setImmediate(true);
		txtInvoiceNo.setWidth("106px");
		txtInvoiceNo.setHeight("-1px");
		mainLayout.addComponent(txtInvoiceNo, "top:16.0px;left:620.0px;");

		lblInvoiceDate = new Label();
		lblInvoiceDate.setImmediate(true);
		lblInvoiceDate.setWidth("-1px");
		lblInvoiceDate.setHeight("-1px");
		lblInvoiceDate.setValue("Invoice Date :");
		mainLayout.addComponent(lblInvoiceDate, "top:44.0px;left:540.0px;");

		dInvoiceDate = new PopupDateField();
		dInvoiceDate.setImmediate(true);
		dInvoiceDate.setWidth("-1px");
		dInvoiceDate.setHeight("-1px");
		dInvoiceDate.setDateFormat("dd-MM-yyyy");
		dInvoiceDate.setValue(new java.util.Date());
		dInvoiceDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dInvoiceDate, "top:42.0px;left:620.0px;");

		lblReceiptNo = new Label();
		lblReceiptNo.setImmediate(true);
		lblReceiptNo.setWidth("-1px");
		lblReceiptNo.setHeight("-1px");
		lblReceiptNo.setValue("Receipt No:");
		mainLayout.addComponent(lblReceiptNo, "top:68.0px;left:540.0px;");

		txtReceiptNo= new TextField();
		txtReceiptNo.setImmediate(true);
		txtReceiptNo.setWidth("106px");
		txtReceiptNo.setHeight("-1px");
		mainLayout.addComponent(txtReceiptNo, "top:66.0px;left:620.0px;");

		lblReceiptDate = new Label();
		lblReceiptDate.setImmediate(true);
		lblReceiptDate.setWidth("-1px");
		lblReceiptDate.setHeight("-1px");
		lblReceiptDate.setValue("Receipt Date :");
		mainLayout.addComponent(lblReceiptDate, "top:93.0px;left:540.0px;");

		dReceiptDate = new PopupDateField();
		dReceiptDate.setImmediate(true);
		dReceiptDate.setWidth("-1px");
		dReceiptDate.setHeight("-1px");
		dReceiptDate.setDateFormat("dd-MM-yyyy");
		dReceiptDate.setValue(new java.util.Date());
		dReceiptDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dReceiptDate, "top:91.0px;left:620.0px;");

		lblPONo = new Label();
		lblPONo.setImmediate(true);
		lblPONo.setWidth("-1px");
		lblPONo.setHeight("-1px");
		lblPONo.setValue("PO No:");
		mainLayout.addComponent(lblPONo, "top:18.0px;left:740.0px;");

		txtPONo= new TextField();
		txtPONo.setImmediate(true);
		txtPONo.setWidth("106px");
		txtPONo.setHeight("-1px");
		mainLayout.addComponent(txtPONo, "top:16.0px;left:880.0px;");

		lblPODate = new Label();
		lblPODate.setImmediate(true);
		lblPODate.setWidth("-1px");
		lblPODate.setHeight("-1px");
		lblPODate.setValue("PO Date :");
		mainLayout.addComponent(lblPODate, "top:44.0px;left:740.0px;");

		dPODate = new PopupDateField();
		dPODate.setImmediate(true);
		dPODate.setWidth("-1px");
		dPODate.setHeight("-1px");
		dPODate.setDateFormat("dd-MM-yyyy");
		dPODate.setValue(new java.util.Date());
		dPODate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dPODate, "top:42.0px;left:880.0px;");

		lblTransaportReceiptNo= new Label();
		lblTransaportReceiptNo.setImmediate(true);
		lblTransaportReceiptNo.setWidth("-1px");
		lblTransaportReceiptNo.setHeight("-1px");
		lblTransaportReceiptNo.setValue("Transaport Receipt No :");
		mainLayout.addComponent(lblTransaportReceiptNo, "top:68.0px;left:740.0px;");

		txtTransportReceipt= new TextField();
		txtTransportReceipt.setImmediate(true);
		txtTransportReceipt.setWidth("106px");
		txtTransportReceipt.setHeight("-1px");
		mainLayout.addComponent(txtTransportReceipt, "top:66.0px;left:880.0px;");


		lblQualityCertificate= new Label();
		lblQualityCertificate.setImmediate(true);
		lblQualityCertificate.setWidth("-1px");
		lblQualityCertificate.setHeight("-1px");
		lblQualityCertificate.setValue("Quality Certificate No:");
		mainLayout.addComponent(lblQualityCertificate, "top:93.0px;left:740.0px;");

		txtQualityCerificate= new TextField();
		txtQualityCerificate.setImmediate(true);
		txtQualityCerificate.setWidth("106px");
		txtQualityCerificate.setHeight("-1px");
		mainLayout.addComponent(txtQualityCerificate, "top:91.0px;left:880.0px;");

		lblUnit= new Label();
		lblUnit.setImmediate(true);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		lblUnit.setValue("Unit :");
		mainLayout.addComponent(lblUnit, "top:18.0px;left:1000.0px;");

		txtUnit= new TextRead(1);
		txtUnit.setImmediate(true);
		txtUnit.setWidth("106px");
		txtUnit.setHeight("-1px");
		mainLayout.addComponent(txtUnit, "top:16.0px;left:1100.0px;");

		lblSubItemName= new Label();
		lblSubItemName.setImmediate(true);
		lblSubItemName.setWidth("-1px");
		lblSubItemName.setHeight("-1px");
		lblSubItemName.setValue("Sub Item Name:");
		mainLayout.addComponent(lblSubItemName, "top:44.0px;left:1000.0px;");

		txtSubItemName= new TextRead(1);
		txtSubItemName.setImmediate(true);
		txtSubItemName.setWidth("106px");
		txtSubItemName.setHeight("-1px");
		mainLayout.addComponent(txtSubItemName, "top:42.0px;left:1100.0px;");

		lblSpecification= new Label();
		lblSpecification.setImmediate(true);
		lblSpecification.setWidth("-1px");
		lblSpecification.setHeight("-1px");
		lblSpecification.setValue("Specification:");
		mainLayout.addComponent(lblSpecification, "top:68.0px;left:1000.0px;");

		txtSpecification= new TextRead(1);
		txtSpecification.setImmediate(true);
		txtSpecification.setWidth("106px");
		txtSpecification.setHeight("-1px");
		mainLayout.addComponent(txtSpecification, "top:66.0px;left:1100.0px;");


		table.setWidth("99.5%");
		table.setHeight("330px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Check", CheckBox.class, new CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Check", 20);

		table.addContainerProperty("Group Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("Group Name",180);

		table.addContainerProperty("Item Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("Item Name",200);

		table.addContainerProperty("Product Code", ComboBox.class , new ComboBox());
		table.setColumnWidth("Product Code",120);

		table.addContainerProperty("Unit", TextRead.class, new TextRead(), null, null, Table.ALIGN_CENTER);
		table.setColumnWidth("Unit",54);
		table.setColumnCollapsed("Unit", false);

		table.addContainerProperty("Sub Item Name", TextRead.class, new TextRead(), null, null, Table.ALIGN_CENTER);
		table.setColumnWidth("Sub Item Name", 120);
		table.setColumnCollapsed("Sub Item Name", false);

		table.addContainerProperty("Specification", TextRead.class, new TextRead(), null, null, Table.ALIGN_CENTER);
		table.setColumnWidth("Specification", 100);
		table.setColumnCollapsed("Specification", false);

		table.addContainerProperty("Qty", AmountField.class ,new  AmountField(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Qty",60);

		table.addContainerProperty("Rate", AmountField.class , new  AmountField(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Rate",60);

		table.addContainerProperty("Amount", Label.class , new  Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Amount",60);

		table.addContainerProperty("Store Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("Store Name",80);

		table.addContainerProperty("Rack Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("Rack  Name",80);

		table.addContainerProperty("Shelf Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("Shelf Name",80);

		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",80);

		tableInitialise();
		mainLayout.addComponent(table, "top:140.0px;left:05.0px;");

		//lbLine = new Label("_________________________________________________________________________________________________________________________________________________________________________________________________________________");
		lbLine=new Label("<b><font color='#e65100'>==================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lbLine, "top:470.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:500.0px;left:290.0px;");

		return mainLayout;
	}

	public void tableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();

		try
		{				
			tblblsl.add(ar,new Label());
			tblblsl.get(ar).setWidth("95%");
			tblblsl.get(ar).setValue(ar+1);

			tbChkShow.add(ar, new CheckBox());
			tbChkShow.get(ar).setImmediate(true);

			tbcmbGroup.add(ar, new ComboBox());
			tbcmbGroup.get(ar).setWidth("95%");
			tbcmbGroup.get(ar).setImmediate(true);
			tbcmbGroup.get(ar).setNullSelectionAllowed(true);

			tbcmbItem.add(ar, new ComboBox());
			tbcmbItem.get(ar).setWidth("95%");
			tbcmbItem.get(ar).setImmediate(true);
			tbcmbItem.get(ar).setNullSelectionAllowed(true);

			tbcmbProductCode.add(ar, new ComboBox());
			tbcmbProductCode.get(ar).setWidth("95%");
			tbcmbProductCode.get(ar).setImmediate(true);
			tbcmbProductCode.get(ar).setNullSelectionAllowed(true);

			tbtxtSubIttemName.add(ar, new TextRead(1));
			tbtxtSubIttemName.get(ar).setImmediate(true);
			tbtxtSubIttemName.get(ar).setWidth("95%");
			tbtxtSubIttemName.get(ar).setHeight("-1px");

			tbtxtUnit.add(ar, new TextRead(1));
			tbtxtUnit.get(ar).setImmediate(true);
			tbtxtUnit.get(ar).setWidth("95%");
			tbtxtUnit.get(ar).setHeight("-1px");

			tbtxtSpecification.add(ar, new TextRead(1));
			tbtxtSpecification.get(ar).setImmediate(true);
			tbtxtSpecification.get(ar).setWidth("95%");
			tbtxtSpecification.get(ar).setHeight("-1px");

			tbaQty.add(ar,new AmountField()) ;
			tbaQty.get(ar).setImmediate(true);
			tbaQty.get(ar).setWidth("95%");
			tbaQty.get(ar).setHeight("-1px");

			tbaRate.add(ar,new AmountField()) ;
			tbaRate.get(ar).setImmediate(true);
			tbaRate.get(ar).setWidth("95%");
			tbaRate.get(ar).setHeight("-1px");

			tbtxtAmount.add(ar, new TextRead(1));
			tbtxtAmount.get(ar).setImmediate(true);
			tbtxtAmount.get(ar).setWidth("100%");
			tbtxtAmount.get(ar).setHeight("-1px");

			tbcmbStoreName.add(ar, new ComboBox());
			tbcmbStoreName.get(ar).setWidth("95%");
			tbcmbStoreName.get(ar).setImmediate(true);
			tbcmbStoreName.get(ar).setNullSelectionAllowed(true);

			tbcmbRackName.add(ar, new ComboBox());
			tbcmbRackName.get(ar).setWidth("95%");
			tbcmbRackName.get(ar).setImmediate(true);
			tbcmbRackName.get(ar).setNullSelectionAllowed(true);

			tbcmbShelfName.add(ar, new ComboBox());
			tbcmbShelfName.get(ar).setWidth("95%");
			tbcmbShelfName.get(ar).setImmediate(true);
			tbcmbShelfName.get(ar).setNullSelectionAllowed(true);

			tbtxtRemarks.add(ar, new TextField(""));
			tbtxtRemarks.get(ar).setImmediate(true);
			tbtxtRemarks.get(ar).setWidth("95%");

			table.addItem(new Object[]{tblblsl.get(ar),tbChkShow.get(ar),tbcmbGroup.get(ar),tbcmbItem.get(ar),
					tbcmbProductCode.get(ar),tbtxtSubIttemName.get(ar),tbtxtUnit.get(ar),tbtxtSpecification.get(ar),
					tbaQty.get(ar),tbaRate.get(ar),tbtxtAmount.get(ar),tbcmbStoreName.get(ar),tbcmbRackName.get(ar),tbcmbShelfName.get(ar),tbtxtRemarks.get(ar)},ar);
		}

		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	private void focusEnter()
	{
		allComp.add(cmbSupplier);
		allComp.add(txtAddress);
		allComp.add(txtChallanNo);
		allComp.add(dChallanDate);
		allComp.add(txtBillNo);
		allComp.add(dBillDate);
		//allComp.add(txtMasterReqNo);
		allComp.add(txtTransactionNo);
		allComp.add(dDate);
		allComp.add(txtInvoiceNo);
		allComp.add(dInvoiceDate);
		allComp.add(txtReceiptNo);
		allComp.add(dReceiptDate);
		allComp.add(txtPONo);
		allComp.add(dPODate);
		allComp.add(txtTransportReceipt);
		allComp.add(txtQualityCerificate);
		allComp.add(txtUnit);
		allComp.add(txtSubItemName);
		allComp.add(txtSpecification);
		allComp.add(table);

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);
		allComp.add(cButton.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	public void setEventAction()
	{

		cButton.btnPreview.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(isFind&&cmbSupplier.getValue()!=null){
					//reportShow();
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
				newButtonEvent();
				//txtpo.focus();
				//autoPONo();
				//txtuser.setValue(sessionBean.getUserName());
			}
		});


		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isSubmitable())
				{
					if(!txtpo.getValue().toString().isEmpty()){
						if(cmbSupplier.getValue()!=null){
							//if(!txtMasterReqNo.getValue().toString().isEmpty()){
							/*	if(nullCheck()){
							saveButtonEvent();
						}else{
							showNotification("Provide All Fields.",Notification.TYPE_WARNING_MESSAGE);
						}*/

							//saveButtonEvent();
							/*}else{
							showNotification("Provide Requisition No.",Notification.TYPE_WARNING_MESSAGE);
						}*/
						}else{
							showNotification("Select Supplier Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}else{
						showNotification("Select PO No",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","You are not permitted to save date",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				txtClear();
				componentIni(true);
				btnIni(true);
				isFind=true;
			}
		});

		cButton.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				//	findButtonEvent();
				isFind=true;
			}
		});

		cButton.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
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

		cButton.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cButton.btnDelete.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
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


	}

	public void txtClear()
	{

		cmbSupplier.setValue(null);
		txtAddress.setValue("");
		txtChallanNo.setValue("");
		dChallanDate.setValue(new java.util.Date());
		txtBillNo.setValue("");
		dBillDate.setValue(new java.util.Date());
		txtTransactionNo.setValue("");
		dDate.setValue(new java.util.Date());
		txtInvoiceNo.setValue("");
		dInvoiceDate.setValue(new java.util.Date());
		txtReceiptNo.setValue("");
		dReceiptDate.setValue(new java.util.Date());
		txtPONo.setValue("");
		dPODate.setValue(new java.util.Date());
		txtTransportReceipt.setValue("");
		txtQualityCerificate.setValue("");
		txtUnit.setValue("");
		txtSubItemName.setValue("");
		txtSpecification.setValue("");


	}

	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		isFind=false;
	}

	private void componentIni(boolean b)
	{

		cmbSupplier.setEnabled(!b);
		txtAddress.setEnabled(!b);
		txtChallanNo.setEnabled(!b);
		dChallanDate.setEnabled(!b);
		txtBillNo.setEnabled(!b);
		dBillDate.setEnabled(!b);
		txtTransactionNo.setEnabled(!b);
		dDate.setEnabled(!b);
		txtInvoiceNo.setEnabled(!b);
		dInvoiceDate.setEnabled(!b);
		txtReceiptNo.setEnabled(!b);
		dReceiptDate.setEnabled(!b);
		txtPONo.setEnabled(!b);
		dPODate.setEnabled(!b);
		txtTransportReceipt.setEnabled(!b);
		txtQualityCerificate.setEnabled(!b);
		txtUnit.setEnabled(!b);
		txtSubItemName.setEnabled(!b);
		txtSpecification.setEnabled(!b);


		lbLine.setEnabled(!b);
		table.setEnabled(!b);

	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnPreview.setEnabled(t);
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


}
