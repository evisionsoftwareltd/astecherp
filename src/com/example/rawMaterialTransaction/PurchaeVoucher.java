package com.example.rawMaterialTransaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.naming.java.javaURLContextFactory;
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

public class PurchaeVoucher extends Window 

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
	private Label lblFindMrrNo;
	private Label lblVoucherDate;
	private Label lblvate;
	private Label lblvatcpercent;
	private Label lblvatAmount;
	private Label lblbillNo;
	private Label lblFindvoucherNo;

	private ComboBox cmbItemType;
	private ComboBox cmbSupplier;
	private ComboBox cmbPoNo;
	private ComboBox cmbMrrNo;
	private ComboBox cmbvoucherNo;

	private TextField txtaddress;
	private TextRead txtreceiptNo;
	private TextField txtchallanNo;
	private TextField txtbillNo;

	private PopupDateField dReceiptDate;
	private PopupDateField dChallanDate;
	private PopupDateField dpodate;
	private PopupDateField dvoucherDate;
	private Label lblpurchaseType;

	private ImmediateUploadExampleNew poAttach;

	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> unit = new ArrayList<TextRead>();
	private ArrayList<TextField> rate = new ArrayList<TextField>(1);

	private ArrayList<TextRead> poQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> leftQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> okQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> rejectQty = new ArrayList<TextRead>();
	private ArrayList<TextField> tbTxtRemark = new ArrayList<TextField>();
	private ArrayList<AmountField> amount = new ArrayList<AmountField>();
	private ArrayList<ComboBox> tCmbStoreLocation = new ArrayList<ComboBox>();
	private TextRead txtflag= new TextRead();

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
	private DecimalFormat df = new DecimalFormat("#0.0000");


	private CommonButtonNew cButton=new CommonButtonNew( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Exit","","");
	private AbsoluteLayout mainLayout;
	String filePathTmpReq= "";
	String imageLoc= "0";
	HashMap hMap=new HashMap();

	private ComboBox cmbPurchaseType= new ComboBox();
	private String [] purchaseType={"Local Credit Purchase","L / C","Loan Receive","Loan Issue Return","Cash Purchase" };
	private String[]mrrtype={"With MRR","Without MRR"};

	private Label lblMrrNo=new Label();
	private TextRead  txtvoucherNew= new TextRead();
	private Label lblvoucherNo= new Label();
	private AmountField txtvatpercent= new AmountField();
	private AmountField txtvatAmount= new AmountField();
	private AmountField txttotalAmount= new AmountField();
	private TextRead txtledgerbalance= new TextRead();
	
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	public PurchaeVoucher(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("STORE JOURNAL:: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		btnIni(true);
		componentIni(true);
		FocusMove();
		purchaseTypeDataLoad();
		cmbMrrLoad();
		cmbSupplierAddData("Suppiler");
		authenticationCheck();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			cButton.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			cButton.btnUpdate.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			cButton.btnDelete.setVisible(false);
		}
	}

	private Iterator dbService(String sql)
	{
		Transaction tx=null;
		Session session=null;
		try
		{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			return session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			if(tx!=null||session!=null)
			{
				tx.commit();
				session.close();
			}
		}
		return null;
	}

	private void FocusMove(){
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(cmbItemType);
		allComp.add(cmbSupplier);
		allComp.add(dReceiptDate);
		allComp.add(txtchallanNo);
		allComp.add(dChallanDate);
		allComp.add(cmbPoNo);
		allComp.add(dpodate);

		for(int i=0;i<cmbProduct.size();i++)
		{
			allComp.add(cmbProduct.get(i));
			allComp.add(poQty.get(i));
			allComp.add(leftQty.get(i));
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
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("1170px");
		setHeight("600px");

		lblVoucherDate = new Label();
		lblVoucherDate.setImmediate(false);
		lblVoucherDate.setWidth("-1px");
		lblVoucherDate.setHeight("-1px");
		lblVoucherDate.setValue("Voucher Date :");
		mainLayout.addComponent(lblVoucherDate, "top:20.0px;left:20.0px;");

		dvoucherDate = new PopupDateField();
		dvoucherDate.setImmediate(true);
		dvoucherDate.setWidth("107px");
		dvoucherDate.setHeight("-1px");
		dvoucherDate.setDateFormat("dd-MM-yyyy");
		dvoucherDate.setValue(new java.util.Date());
		dvoucherDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dvoucherDate, "top:18.0px;left:130.0px;");

		lblFindMrrNo = new Label();
		lblFindMrrNo.setImmediate(false);
		lblFindMrrNo.setWidth("-1px");
		lblFindMrrNo.setHeight("-1px");
		lblFindMrrNo.setValue("MRR NO :");
		mainLayout.addComponent(lblFindMrrNo, "top:46.0px;left:20.0px;");

		cmbMrrNo = new ComboBox();
		cmbMrrNo.setImmediate(true);
		cmbMrrNo.setWidth("220px");
		cmbMrrNo.setHeight("-1px");
		cmbMrrNo.setNullSelectionAllowed(true);
		cmbMrrNo.setNewItemsAllowed(false);
		cmbMrrNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbMrrNo, "top:44.0px;left:130.0px;");
		
		lblFindvoucherNo = new Label();
		lblFindvoucherNo.setImmediate(false);
		lblFindvoucherNo.setWidth("-1px");
		lblFindvoucherNo.setHeight("-1px");
		lblFindvoucherNo.setValue("Voucher NO :");
		mainLayout.addComponent(lblFindvoucherNo, "top:72.0px;left:20.0px;");

		cmbvoucherNo = new ComboBox();
		cmbvoucherNo.setImmediate(true);
		cmbvoucherNo.setWidth("220px");
		cmbvoucherNo.setHeight("-1px");
		cmbvoucherNo.setNullSelectionAllowed(true);
		cmbvoucherNo.setNewItemsAllowed(false);
		cmbvoucherNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbvoucherNo, "top:70.0px;left:130.0px;");

		lblpurchaseType = new Label();
		lblpurchaseType.setImmediate(false);
		lblpurchaseType.setWidth("-1px");
		lblpurchaseType.setHeight("-1px");
		lblpurchaseType.setValue("Purchase Type:");
		mainLayout.addComponent(lblpurchaseType, "top:98.0px;left:20.0px;");

		cmbPurchaseType = new ComboBox();
		cmbPurchaseType.setImmediate(true);
		cmbPurchaseType.setWidth("220px");
		cmbPurchaseType.setHeight("-1px");
		cmbPurchaseType.setNullSelectionAllowed(true);
		cmbPurchaseType.setNewItemsAllowed(false);
		cmbPurchaseType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbPurchaseType, "top:96.0px;left:130.0px;");

		for(int i=0;i<purchaseType.length;i++)	
		{
			cmbPurchaseType.addItem(purchaseType[i]);
		}

		lblitemType = new Label();
		lblitemType.setImmediate(false);
		lblitemType.setWidth("-1px");
		lblitemType.setHeight("-1px");
		lblitemType.setValue("Item Type :");
		mainLayout.addComponent(lblitemType, "top:124.0px;left:20.0px;");

		cmbItemType = new ComboBox();
		cmbItemType.setImmediate(true);
		cmbItemType.setWidth("220px");
		cmbItemType.setHeight("-1px");
		cmbItemType.setNullSelectionAllowed(true);
		cmbItemType.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbItemType, "top:122.0px;left:130.0px;");

		lblAddress = new Label();
		lblAddress.setImmediate(false);
		lblAddress.setWidth("-1px");
		lblAddress.setHeight("-1px");
		lblAddress.setValue("Address :");
		mainLayout.addComponent(lblAddress, "top:20.0px;left:365.0px;");

		txtaddress = new TextField();
		txtaddress.setImmediate(true);
		txtaddress.setWidth("120px");
		txtaddress.setHeight("26px");
		txtaddress.setRows(1);
		mainLayout.addComponent(txtaddress, "top:18.0px;left:435.0px;");

		lblReceiptNo = new Label();
		lblReceiptNo.setImmediate(false);
		lblReceiptNo.setWidth("-1px");
		lblReceiptNo.setHeight("-1px");
		lblReceiptNo.setValue("Receipt No :");
		mainLayout.addComponent(lblReceiptNo, "top:50.0px;left:365.0px;");

		txtreceiptNo = new TextRead(1);
		txtreceiptNo.setImmediate(true);
		txtreceiptNo.setWidth("100px");
		txtreceiptNo.setHeight("-1px");
		mainLayout.addComponent(txtreceiptNo, "top:48.0px;left:435.0px;");
		
		lblDate = new Label();
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:76.0px;left:365.0px;");

		dReceiptDate = new PopupDateField();
		dReceiptDate.setImmediate(true);
		dReceiptDate.setWidth("107px");
		dReceiptDate.setHeight("-1px");
		dReceiptDate.setDateFormat("dd-MM-yyyy");
		dReceiptDate.setValue(new java.util.Date());
		dReceiptDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dReceiptDate, "top:74.0px;left:435.0px;");
		
		lblSupplier = new Label();
		lblSupplier.setImmediate(false);
		lblSupplier.setWidth("-1px");
		lblSupplier.setHeight("-1px");
		lblSupplier.setValue("Supplier  :");
		mainLayout.addComponent(lblSupplier, "top:102.0px;left:365.0px;");
		
		

		cmbSupplier = new ComboBox();
		cmbSupplier.setImmediate(true);
		cmbSupplier.setWidth("250px");
		cmbSupplier.setHeight("-1px");
		cmbSupplier.setNullSelectionAllowed(true);
		cmbSupplier.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbSupplier, "top:100.0px;left:435.0px;");
		
		txtledgerbalance = new TextRead();
		txtledgerbalance.setImmediate(true);
		txtledgerbalance.setWidth("120px");
		txtledgerbalance.setHeight("26px");
		mainLayout.addComponent(new Label("Balance"), "top:128.0px;left:365.0px;");
		mainLayout.addComponent(txtledgerbalance, "top:126.0px;left:435.0px;");

		lblChallan = new Label();
		lblChallan.setImmediate(false);
		lblChallan.setWidth("-1px");
		lblChallan.setHeight("-1px");
		lblChallan.setValue("Challan No :");
		mainLayout.addComponent(lblChallan, "top:20.0px;left:700.0px;");

		txtchallanNo = new TextField();
		txtchallanNo.setImmediate(true);
		txtchallanNo.setWidth("100px");
		txtchallanNo.setHeight("-1px");
		mainLayout.addComponent(txtchallanNo, "top:18.0px;left:780.0px;");

		lblChallanDate = new Label();
		lblChallanDate.setImmediate(false);
		lblChallanDate.setWidth("-1px");
		lblChallanDate.setHeight("-1px");
		lblChallanDate.setValue("Challan Date :");
		mainLayout.addComponent(lblChallanDate, "top:46.0px;left:700.0px;");

		dChallanDate = new PopupDateField();
		dChallanDate.setImmediate(true);
		dChallanDate.setWidth("107px");
		dChallanDate.setHeight("-1px");
		dChallanDate.setDateFormat("dd-MM-yyyy");
		dChallanDate.setValue(new java.util.Date());
		dChallanDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dChallanDate, "top:44.0px;left:780.0px;");

		lblPoNo = new Label();
		lblPoNo.setImmediate(false);
		lblPoNo.setWidth("-1px");
		lblPoNo.setHeight("-1px");
		lblPoNo.setValue("P.O No :");
		mainLayout.addComponent(lblPoNo, "top:72.0px;left:700.0px;");

		cmbPoNo = new ComboBox();
		cmbPoNo.setImmediate(true);
		cmbPoNo.setNullSelectionAllowed(true);
		cmbPoNo.setWidth("160px");
		cmbPoNo.setHeight("-1px");
		mainLayout.addComponent(cmbPoNo, "top:70.0px;left:780.0px;");

		lblpodate = new Label();
		lblpodate.setImmediate(false);
		lblpodate.setWidth("-1px");
		lblpodate.setHeight("-1px");
		lblpodate.setValue("PO Date :");
		mainLayout.addComponent(lblpodate, "top:100.0px;left:700.0px;");

		dpodate = new PopupDateField();
		dpodate.setImmediate(true);
		dpodate.setWidth("107px");
		dpodate.setHeight("-1px");
		dpodate.setDateFormat("dd-MM-yyyy");
		dpodate.setValue(new java.util.Date());
		dpodate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dpodate, "top:98.0px;left:780.0px;");

		lblvoucherNo = new Label();
		lblvoucherNo.setImmediate(false);
		lblvoucherNo.setWidth("-1px");
		lblvoucherNo.setHeight("-1px");
		lblvoucherNo.setValue("Voucher No :");
		mainLayout.addComponent(lblvoucherNo, "top:18.0px;left:910.0px;");

		txtvoucherNew = new TextRead();
		txtvoucherNew.setImmediate(true);
		txtvoucherNew.setWidth("120px");
		mainLayout.addComponent(txtvoucherNew, "top:20.0px;left:1000.0px;");
		
		lblbillNo = new Label();
		lblbillNo.setImmediate(false);
		lblbillNo.setWidth("-1px");
		lblbillNo.setHeight("-1px");
		lblbillNo.setValue("BIll No :");
		mainLayout.addComponent(lblbillNo, "top:48.0px;left:910.0px;");

		txtbillNo = new TextField();
		txtbillNo.setImmediate(true);
		txtbillNo.setWidth("120px");
		mainLayout.addComponent(txtbillNo, "top:46.0px;left:1000.0px;");

		poAttach = new ImmediateUploadExampleNew("");
		poAttach.setWidth("-1px");
		poAttach.setHeight("-1px");
		poAttach.setStyleName("uploadReq");
		mainLayout.addComponent(poAttach, "top:60.5px;left:950.0px;");

		table = new Table();
		table.setWidth("98%");
		table.setHeight("280px");
		table.setFooterVisible(true);

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Product Name", ComboBox.class , new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Product Name",410);

		table.addContainerProperty("Unit", TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Unit",45);

		table.addContainerProperty("OK Qty", TextRead.class , new TextRead(1),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("OK Qty",70);


		table.addContainerProperty("Rate", TextField.class , new TextField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Rate",80);

		table.addContainerProperty("Amount", TextField.class , new TextField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Amount",100);

		table.addContainerProperty("Remarks", TextField.class , new TextField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Remarks",150);

		table.addContainerProperty("Store Name", ComboBox.class , new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Store Name",130);

		table.setColumnCollapsingAllowed(true);
		tableInitialise();
		mainLayout.addComponent(table, "top:162.0px;left:12.0px;");

		lbLine = new Label("______________________________________________________________________________________________________________________________________________________________________________________________________________________");
		mainLayout.addComponent(lbLine, "top:500.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:520.0px;left:360.0px;");

		lblvate = new Label();
		lblvate.setImmediate(false);
		lblvate.setWidth("-1px");
		lblvate.setHeight("-1px");
		lblvate = new Label("<font color = 'Red' size = '3px'>VAT :</font>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblvate, "top:460.0px;left:435.0px;");
		//lblvate.setVisible(false);

		txtvatpercent = new AmountField();
		txtvatpercent.setImmediate(true);
		txtvatpercent.setWidth("60px");
		mainLayout.addComponent(txtvatpercent, "top:458.0px;left:480.0px;");
		//txtvatpercent.setVisible(false);

		lblvatcpercent = new Label();
		lblvatcpercent.setImmediate(false);
		lblvatcpercent.setWidth("-1px");
		lblvatcpercent.setHeight("-1px");
		lblvatcpercent = new Label("<font color = 'Red' size = '3px'>%</font>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblvatcpercent, "top:458.0px;left:550.0px;");
		//lblvatcpercent.setVisible(false);

		txtvatAmount = new AmountField();
		txtvatAmount.setImmediate(true);
		txtvatAmount.setWidth("100px");
		mainLayout.addComponent(txtvatAmount, "top:456.0px;left:570.0px;");
		//txtvatAmount.setVisible(false);

		lblvatAmount = new Label();
		lblvatAmount.setImmediate(false);
		lblvatAmount.setWidth("-1px");
		lblvatAmount.setHeight("-1px");
		lblvatAmount = new Label("<font color = 'Red' size = '3px'>GRAND TOTAL : </font>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblvatAmount, "top:488.0px;left:435.0px;");
		//lblvatAmount.setVisible(false);

		txttotalAmount = new AmountField();
		txttotalAmount.setImmediate(true);
		txttotalAmount.setWidth("100px");
		mainLayout.addComponent(txttotalAmount, "top:486.0px;left:570.0px;");
		//txttotalAmount.setVisible(false);
		
		return mainLayout;
	}


	private void tableclear()

	{

		for(int i=0;i<unit.size();i++)
		{
			cmbProduct.get(i).setValue(null);
			unit.get(i).setValue("");
			amount.get(i).setValue("");
			rate.get(i).setValue("");
			poQty.get(i).setValue("");
			okQty.get(i).setValue("");
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
				newButtonEvent();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isSubmitable())
				{
					saveButtonEvent();

				}
				else
				{
					showNotification("Warning!","You are not permitted to save date",Notification.TYPE_WARNING_MESSAGE);
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
				cmbvoucherNo.removeAllItems();
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

		txtvatpercent.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtvatpercent.getValue().toString().isEmpty())
				{
					double vatpercent=0.00;
					double amount=0.00;
					double vat=0.00;
					double totalAmount=0.00;
					amount= Double.parseDouble((table.getColumnFooter("Amount").substring(table.getColumnFooter("Amount").indexOf(":")+1,table.getColumnFooter("Amount").length()).replace(",",""))) ;
					if(amount>0)
					{
						vatpercent=Double.parseDouble(txtvatpercent.getValue().toString());
						vat=(vatpercent*amount)/100;
						txtvatAmount.setValue(df.format(vat));
						totalAmount=vat+amount;
						txttotalAmount.setValue(df.format(totalAmount));

					}
					else
					{
						System.out.println("Nothing");	
					}	
				}	
			}
		});


		txtvatAmount.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtvatAmount.getValue().toString().isEmpty())
				{
					double amount=0.00;
					double vat=0.00;
					double totalAmount=0.00;
					amount= Double.parseDouble((table.getColumnFooter("Amount").substring(table.getColumnFooter("Amount").indexOf(":")+1,table.getColumnFooter("Amount").length()).replace(",",""))) ;
					if(amount>0)
					{
						vat=Double.parseDouble(txtvatAmount.getValue().toString());
						totalAmount=vat+amount;
						txttotalAmount.setValue(totalAmount);

					}
					else
					{
						System.out.println("Nothing");	
					}	
				}	
			}
		});

		cmbItemType.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) 
			{
				if( cmbItemType.getValue()!=null)
				{
					/*for(int i=0;i<rate.size();i++)
					{
						dataload(i);	
					}
					cmbSupplier.focus();*/
				}
			}
		});

		cmbMrrNo.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbMrrNo.getValue()!=null)
				{
					FindDataLoad();
				}
			}
		});
		
		cmbvoucherNo.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event)
			{
				if(cmbvoucherNo.getValue()!=null)
				{
					voucherAction();
				}
			}
		});

		cmbPurchaseType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPurchaseType.getValue()!=null)
				{
					if(cmbPurchaseType.getValue().toString().equalsIgnoreCase("L / C"))
					{
						lblSupplier.setValue("L / C No :");
						cmbSupplier.removeAllItems();
						cmbSupplierAddData("L/c");
					}
					if(cmbPurchaseType.getValue().toString().equalsIgnoreCase("Local Credit Purchase"))
					{
						lblSupplier.setValue("Supplier Name :");	
						cmbSupplier.removeAllItems();
						cmbSupplierAddData("Suppiler");
					}
					if(cmbPurchaseType.getValue().toString().equalsIgnoreCase("Cash Purchase"))
					{
						lblSupplier.setValue("Supplier Name :");	
						cmbSupplier.removeAllItems();
						cmbSupplierAddData("Cash Purchase");
					}

					if(cmbPurchaseType.getValue().toString().equalsIgnoreCase("Local Credit Purchase"))

					{
						lblSupplier.setValue("Supplier Name :");	
						cmbSupplier.removeAllItems();
						cmbSupplierAddData("Suppiler");
					}

					if(cmbPurchaseType.getValue().toString().equalsIgnoreCase("Loan Receive"))

					{
						lblSupplier.setValue("Supplier Name :");	
						cmbSupplier.removeAllItems();
						cmbSupplierAddData("Suppiler");
					}
					
					if(cmbPurchaseType.getValue().toString().equalsIgnoreCase("Loan Issue Return"))

					{
						lblSupplier.setValue("Supplier Name :");	
						cmbSupplier.removeAllItems();
						cmbSupplierAddData("Suppiler");
					}
				}

			}
		});
		
		
		cmbSupplier.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) 
			{
				
			   if (cmbSupplier.getValue()!=null && cmbPurchaseType.getValue().toString().equalsIgnoreCase("L / C") )
			   {
				   String fsl = dbService("Select  [dbo].[VoucherSelect]('"+datef.format(dvoucherDate.getValue())+"')").next().toString();
					String voucher =  "voucher"+fsl;
					
					String sql= "select 0 usd,(select  ISNULL(SUM(DrAmount),0)   from tbLedger_Op_Balance where Ledger_Id='"+cmbSupplier.getValue().toString()+"' and Op_Year='2016')+ ISNULL(SUM(DrAmount),0)- ISNULL(SUM(CrAmount),0) balance   from "+voucher+" where Ledger_Id='"+cmbSupplier.getValue().toString()+"' ";
					
					Iterator<?> iter=dbService(sql);
					if(iter.hasNext()){
						Object element[]=(Object[])iter.next();
						txtledgerbalance.setValue(element[1]);
					}   
			   }
			   else
			   {
				   txtledgerbalance.setValue("0.00");
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
			// image move		
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
			System.out.println("fbgfghthg");
			List lst=null;

			 /*lst = session.createSQLQuery("select vRawItemCode,vRawItemName,subString(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName))as category,vSubSubCategoryName from tbRawItemInfo where vCategoryType like '"+cmbItemType.getValue().toString()+"' order by category,vSubSubCategoryName").list();
			 
			 if(cmbItemType.getValue()==null)
			 {*/
				 lst = session.createSQLQuery("select vRawItemCode,vRawItemName,subString(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName))as category,vSubSubCategoryName from tbRawItemInfo where vCategoryType like '%%' order by category,vSubSubCategoryName").list();
				 	 
			 //}

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
	
	private void voucherAction()
	{
		 try
		 {
			 String sql=  
						"select 0, MrrNo " 
								+"  from tbjournelInfo where VoucherNo like '"+cmbvoucherNo.getValue().toString()+"' " ;

				System.out.println(" New query is"+sql);

				Iterator<?>iter=dbService(sql);
				txtClear("MrrNOAction");
				
				if (iter.hasNext())
				{
					Object[] element = (Object[]) iter.next();
					cmbMrrNo.setValue(element[1].toString());

				}	 
		 }
		 catch(Exception ex)
		 {
			
		 }
		
	}

	//

	public void FindDataLoad( )
	{
		try
		{

			if(!isFind)
			{
				/*String sql=  "Select purchaseType,SupplierId,ReceiptNo,Date, "
						+"ChallanNo,challanDate,poNo,poDate,poAttach,transactionType,billNo  from tbRawPurchaseInfo where MrrNo like '"+cmbMrrNo.getValue().toString()+"' ";
				*/
				String sql="Select purchaseType,SupplierId,ReceiptNo,Date, ChallanNo,ISNULL(challanDate,Date)challanData ,ISNULL(poNo,0)poNo ,ISNULL(poDate,Date)poDate ,ISNULL(poAttach,0)poAttach ,transactionType,ISNULL(billNo,0)billNo   from tbRawPurchaseInfo where MrrNo like '"+cmbMrrNo.getValue().toString()+"' ";

				System.out.println(" New query is"+sql);

				Iterator<?>iter=dbService(sql);
				txtClear("MrrNOAction");

				if (iter.hasNext())
				{
					Object[] element = (Object[]) iter.next();

					cmbPurchaseType.setValue(element[9].toString());
					txtflag.setValue(element[9].toString());

					cmbItemType.setValue(element[0].toString());
					cmbSupplier.setValue(element[1].toString());
					txtreceiptNo.setValue(element[2].toString());
					dReceiptDate.setValue(element[3]);
					txtchallanNo.setValue(element[4]);
					dChallanDate.setValue(element[5]);
					cmbPoNo.addItem(element[6]);
					dpodate.setValue(element[7]);
					txtbillNo.setValue(element[10].toString());

					/*if(!element[8].toString().equals("0"))
					{
						imageLoc = element[8].toString();
						poAttach.status.setValue("Receipt No: "+imageLoc.substring(43));
					}*/

				}	
			}

			if(isFind)
			{
				String sql=  
						"select purchaseType,SupplierId,ReceiptNo,Date, " 
								+"ChallanNo,challanDate,poNo,poDate,poAttach,transactionType,VatPercent,vatAmount,grandTotal,VoucherNo,billNo  from tbjournelInfo where MrrNo like '"+cmbMrrNo.getValue().toString()+"' " ;

				System.out.println(" New query is"+sql);

				Iterator<?>iter=dbService(sql);
				txtClear("MrrNOAction");

				if (iter.hasNext())
				{
					Object[] element = (Object[]) iter.next();

					cmbPurchaseType.setValue(element[9].toString());
					txtflag.setValue(element[9].toString());

					cmbItemType.setValue(element[0].toString());
					cmbSupplier.setValue(element[1].toString());
					txtreceiptNo.setValue(element[2].toString());
					dReceiptDate.setValue(element[3]);
					txtchallanNo.setValue(element[4]);
					dChallanDate.setValue(element[5]);
					cmbPoNo.addItem(element[6]);
					dpodate.setValue(element[7]);
					txtvoucherNew.setValue(element[13].toString());
					txtbillNo.setValue(element[14].toString());

					if(!element[8].toString().equals("0"))
					{
						imageLoc = element[8].toString();
						poAttach.status.setValue("Receipt No: "+imageLoc.substring(43));
					}

					txtvatpercent.setValue(df.format(element[10]));
					txtvatAmount.setValue(df.format(element[11]));
					txttotalAmount.setValue(df.format(element[12]));

				}	
			}



			String query ="select b.ProductID, b.Qty,b.Rate,b.remarks,b.storeId from tbRawPurchaseInfo a inner join "
					+"tbRawPurchaseDetails b on a.ReceiptNo=b.ReceiptNo "
					+"where a.MrrNo='"+cmbMrrNo.getValue()+"' ";

			Iterator<?>iter1=dbService(query);

			int i=0;
			while (iter1.hasNext())
			{
				Object[] element = (Object[]) iter1.next();

				cmbProduct.get(i).setValue(element[0]);
				okQty.get(i).setValue(element[1]);
				rate.get(i).setValue(element[2]);
				tbTxtRemark.get(i).setValue(element[3]);
				tCmbStoreLocation.get(i).setValue(element[4].toString());

				i++;

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
		txtClear("");
		//componentIni(false);
		newComponentInit();
		btnIni(false);
		cmbMrrLoad();
		//autoReciptNo();
		cmbItemType.focus();
		isFind=false;
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
				txtreceiptNo.setValue(autoId);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
		return autoId;
	}*/

	private void newComponentInit()
	{
		dvoucherDate.setEnabled(true);
		cmbMrrNo.setEnabled(true);
		cmbPurchaseType.setEnabled(true);
		cmbSupplier.setEnabled(true);
		txtvatpercent.setEnabled(true);
		txtvatAmount.setEnabled(true);
		txttotalAmount.setEnabled(true);
		txtbillNo.setEnabled(true);

	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear("");
	}

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
		cmbPurchaseType.setEnabled(!b); 
		cmbItemType.setEnabled(!b);
		cmbSupplier.setEnabled(!b);
		txtaddress.setEnabled(!b);
		txtreceiptNo.setEnabled(!b);
		dReceiptDate.setEnabled(!b);
		dpodate.setEnabled(!b);
		txtchallanNo.setEnabled(!b);
		dChallanDate.setEnabled(!b);
		cmbPoNo.setEnabled(!b);
		lbLine.setEnabled(!b);
		txtvoucherNew.setEnabled(!b);
		cmbMrrNo.setEnabled(!b);
		cmbvoucherNo.setEnabled(!b);
		dvoucherDate.setEnabled(!b);
		txtvoucherNew.setEnabled(!b);
		txtvatpercent.setEnabled(!b);
		txtvatAmount.setEnabled(!b);
		txttotalAmount.setEnabled(!b);
		txtbillNo.setEnabled(!b);
	}

	public void txtClear(String MrrNOAction)
	{
		try
		{	
			///dvoucherDate.setValue(new java.util.Date());

			if(!MrrNOAction.equalsIgnoreCase("MrrNOAction"))
			{
				cmbMrrNo.setValue(null);	
			}
             //cmbvoucherNo.removeAllItems();
			cmbPurchaseType.setValue(null);
			cmbItemType.setValue(null);
			cmbSupplier.setValue(null);
			txtaddress.setValue("");
			txtreceiptNo.setValue("");
			dReceiptDate.setValue(new java.util.Date());
			txtchallanNo.setValue("");
			dChallanDate.setValue(new java.util.Date());
			cmbPoNo.setValue(null);
			dpodate.setValue(new java.util.Date());
			txtvoucherNew.setValue("");
			txtflag.setValue("");
			txtbillNo.setValue("");
			txtledgerbalance.setValue("");
			for(int i=0;i<unit.size();i++)
			{
				cmbProduct.get(i).setValue(null);
				unit.get(i).setValue("");
				amount.get(i).setValue("");
				rate.get(i).setValue("");
				poQty.get(i).setValue("");
				okQty.get(i).setValue("");
				rejectQty.get(i).setValue("");
				tbTxtRemark.get(i).setValue("");
			}
			table.setColumnFooter("Amount", "Total:"+0.0);

			txtvatpercent.setValue("");
			txtvatAmount.setValue("");
			txttotalAmount.setValue("");
			
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
		for(int i=0;i<20;i++){
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
			cmbProduct.get(ar).setEnabled(false);
			dataload(ar);
			
			//dataload(ar);

			unit.add(ar,new TextRead(""));
			unit.get(ar).setWidth("100%");


			poQty.add( ar , new TextRead(1));
			poQty.get(ar).setWidth("100%");
			poQty.get(ar).setImmediate(true);

			leftQty.add( ar , new TextRead(1));
			leftQty.get(ar).setWidth("100%");
			leftQty.get(ar).setImmediate(true);


			okQty.add( ar ,new TextRead(1));
			okQty.get(ar).setWidth("90%");
			okQty.get(ar).setImmediate(true);


			rejectQty.add( ar ,new TextRead(1));
			rejectQty.get(ar).setWidth("90%");
			rejectQty.get(ar).setImmediate(true);

			rate.add(ar,new TextField());
			rate.get(ar).setWidth("90%");
			rate.get(ar).setImmediate(true);

			amount.add( ar , new AmountField());
			amount.get(ar).setWidth("90%");

			tbTxtRemark.add( ar , new TextField());
			tbTxtRemark.get(ar).setWidth("100%");

			tCmbStoreLocation.add( ar , new ComboBox());
			tCmbStoreLocation.get(ar).setWidth("100%");
			tCmbStoreLocation.get(ar).setEnabled(false);

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
					//;


					boolean fla = true;
					if(cmbProduct.get(ar).getValue()!=null)
					{

						fla=(doubleEntryCheck(cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue()),ar));	


						if ((Object)cmbProduct.get(ar).getValue()!=null && fla )
						{

							System.out.println("Rabiul hasan Ratan");

							int temp=cmbProduct.size();
							//poQty.get(ar).focus();
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
							getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							cmbProduct.get(ar).setValue(null);


						}
					}

				}
			});



			table.addItem(new Object[]{tbLblSl.get(ar),cmbProduct.get(ar),unit.get(ar),okQty.get(ar),rate.get(ar),amount.get(ar),tbTxtRemark.get(ar),tCmbStoreLocation.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private boolean doubleEntryCheck(String caption,int row){

		for(int i=0;i<amount.size();i++){

			if(i!=row && caption.equals(cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue()))){

				return false;
			}
		}
		return true;
	}


	private void tableColumnAction(final String head,final int r)
	{

		rate.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
		//rate.get(r).setTextChangeTimeout(200);

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
						System.out.println("From PO Qty: "+event.getProperty().toString());
						System.out.println("From PO Rate: "+rate.get(r).getValue().toString());
						if(!event.getProperty().toString().trim().equalsIgnoreCase("&nbsp;") && !rate.get(r).getValue().toString().equalsIgnoreCase("&nbsp;"))
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
					getParent().showNotification("Errora",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
						if(!rate.get(r).getValue().toString().isEmpty())
						{
						String tbquntity;
						double tamount,unitPrice;
						System.out.println("From PO Qtyr: "+event.getProperty().toString());
						System.out.println("From PO Rater: "+okQty.get(r).getValue().toString());
						if(!event.getProperty().toString().trim().equalsIgnoreCase("&nbsp;"))
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
				}

					cmbProduct.get(r+1).focus();
				}
				catch(Exception exp)
				{
					getParent().showNotification("Errora",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		okQty.get(r).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try
				{
					if(!leftQty.get(r).getValue().toString().isEmpty() && !okQty.get(r).getValue().toString().isEmpty()){
						rejectQty.get(r).setValue( (Double.parseDouble(leftQty.get(r).getValue().toString().replaceAll(",", ""))) - (Double.parseDouble(okQty.get(r).getValue().toString().replaceAll(",", "")))  ); 
						System.out.println("Find: "+isFind);
						System.out.println("Find: "+isUpdate);
						if(isFind){
							if(!isUpdate){
								if(Double.parseDouble(rejectQty.get(r).getValue().toString())<0){
									//okQty.get(r).focus();
									showNotification("Qty Exceed Left Qty",Notification.TYPE_WARNING_MESSAGE);
									okQty.get(r).setValue("");
									rejectQty.get(r).setValue("");

								}
							}
						}
					}

					cmbProduct.get(r+1).focus();
				}
				catch(Exception exp)
				{
					getParent().showNotification("Errora",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

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
					getParent().showNotification("Errora",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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

	public void cmbSupplierAddData(String caption)
	{

		Transaction tx;
		try{

			System.out.println("Done");

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			cmbSupplier.removeAllItems();
			supplierAddress.clear();
			List lst=null;
			if(caption.equalsIgnoreCase("Suppiler"))
			{
				lst = session.createSQLQuery("select supplierId,supplierName from tbSupplierInfo where supplierId!='82'   order by supplierName--where supplierId in(select supplierId from tbRawPurchaseOrderInfo)").list();	
			}
			if(caption.equalsIgnoreCase("L/c"))
			{
				lst = session.createSQLQuery("select distinct  Ledger_Id,Ledger_Name from tbLedger where Create_From like '%A4-G400%' ").list();
			}
			if(caption.equalsIgnoreCase("Cash Purchase"))
			{
				lst = session.createSQLQuery("select distinct  Ledger_Id,Ledger_Name from tbLedger where Create_From like '%A4-G402%' or Create_From='A7'").list();
			}

			cmbSupplier.addItem("AL4293");
			cmbSupplier.setItemCaption("AL4293", "Ultra Pack Limited");
			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplier.addItem(element[0].toString());
				cmbSupplier.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}


	public void cmbMrrLoad(){

		try{

			System.out.println("Done");
			cmbMrrNo.removeAllItems();
			List lst=null;

			Iterator<?>iter=dbService("select distinct  0, MrrNo from tbRawPurchaseInfo where  flag=0");	
			while (iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbMrrNo.addItem(element[1].toString());
				cmbMrrNo.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	public void FindMrrLoad()
	{

		try{

			System.out.println("Done");
			cmbMrrNo.removeAllItems();
			List lst=null;

			Iterator<?>iter=dbService("select distinct  0, MrrNo from tbRawPurchaseInfo where  flag=1 ");	
			while (iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbMrrNo.addItem(element[1].toString());
				cmbMrrNo.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	
	public void findVoucherNoLoad()
	{

		try{

			System.out.println("Done");
			cmbvoucherNo.removeAllItems();
			List lst=null;

			Iterator<?>iter=dbService("select distinct  0, VoucherNo from tbRawPurchaseInfo where  flag=1  ");	
			while (iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbvoucherNo.addItem(element[1].toString());
				cmbvoucherNo.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}


	public void purchaseTypeDataLoad(){

		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			cmbSupplier.removeAllItems();
			supplierAddress.clear();
			List lst = session.createSQLQuery("  select  distinct 0,vCategoryType from tbRawItemCategory").list();

			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbItemType.addItem(element[1].toString());
				cmbItemType.setItemCaption(element[1].toString(), element[1].toString());
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
		String query="";

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			if(cmbPurchaseType.getValue().toString().equalsIgnoreCase("Local Credit Purchase")||cmbPurchaseType.getValue().toString().equalsIgnoreCase("Loan Receive")||cmbPurchaseType.getValue().toString().equalsIgnoreCase("Loan Issue Return") )
			{
				query="select Ledger_Id from tbLedger where Ledger_Id=(select ledgerCode from tbSupplierInfo where supplierId like '"+cmbSupplier.getValue().toString()+"')";
				
				if(cmbSupplier.getValue().toString().equalsIgnoreCase("AL4293"))
				{
					query="select Ledger_Id from tbLedger where Ledger_Id= '"+cmbSupplier.getValue().toString()+"'";
					System.out.println("Supplier1"+query);
				}
				
			
			}
			else
			{
				query="select '"+cmbSupplier.getValue()+"'";
				System.out.println("Supplier2"+query);
			}

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
			if(cmbProduct.get(a).getValue()!=null){
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

		for(int a=0;a<cmbProduct.size();a++)
		{
			if(cmbProduct.get(a).getValue()!=null)
			{
				double value = 0;
				double d=Double.parseDouble(okQty.get(a).getValue().toString());
				if(Double.parseDouble(hMap.get(a).toString())!=d){
					double difference=Double.parseDouble(hMap.get(a).toString())-d;
					if(!leftQty.get(a).getValue().toString().isEmpty())
					{
						if(difference>0){
							value=Double.parseDouble(leftQty.get(a).getValue().toString())-difference;
						}
						else if(difference<0){
							value=Double.parseDouble(leftQty.get(a).getValue().toString())+difference;
						}
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
				catch(Exception exp){
					tx.rollback();
					showNotification("From LeftCheck: "+exp,Notification.TYPE_ERROR_MESSAGE);
				}
			}
		}
	}
	private void saveButtonEvent() {
		if(cmbSupplier.getValue()!=null){
			//if(!txtchallanNo.getValue().toString().trim().isEmpty()){
				if(!txttotalField.getValue().toString().trim().isEmpty()){
					/*if(cmbItemType.getValue()!=null)
					{*/
						if(cmbProduct.get(0).getValue()!=null)
						{
							if(tableDataCheck())
							{						
								if(isUpdate)
								{
									final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update the find data?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
									mb.show(new EventListener()
									{
										public void buttonClicked(ButtonType buttonType)
										{
											if(buttonType == ButtonType.YES)
											{
												mb.buttonLayout.getComponent(0).setEnabled(false);
												Transaction tx=null;
												Session session = SessionFactoryUtil.getInstance().getCurrentSession();
												tx = session.beginTransaction();
												if(deleteData(session, tx))
												{
													insertData(session,tx);	
												}
												isUpdate=false;
											}
										}

									});	
								}

								else
								{
									final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
									mb.show(new EventListener()
									{
										public void buttonClicked(ButtonType buttonType)
										{

											if(buttonType == ButtonType.YES)
											{
												mb.buttonLayout.getComponent(0).setEnabled(false);
												Transaction tx=null;
												Session session = SessionFactoryUtil.getInstance().getCurrentSession();
												tx = session.beginTransaction();
												insertData(session,tx);
												isUpdate=false;
												//poAttach.actionCheck = false;
											}
										}
									});	
								}
							}
							else
							{
								this.getParent().showNotification("Warning !","Please Select Store Location.", Notification.TYPE_WARNING_MESSAGE);
							}
						}

						else
						{
							this.getParent().showNotification("Warning !","Please Select Product Name.", Notification.TYPE_WARNING_MESSAGE);	
						}



					/*}

					else{
						this.getParent().showNotification("Warning !","Please Select Item  Type .", Notification.TYPE_WARNING_MESSAGE);
					}*/

				}
				else
					this.getParent().showNotification("Warning :","Please Select Product .", Notification.TYPE_WARNING_MESSAGE);	
			/*}
			else
				this.getParent().showNotification("Warning :","Please Enter Challen No..", Notification.TYPE_WARNING_MESSAGE);*/

		}
		else
			this.getParent().showNotification("Warning :","Please Select Supplier Name.", Notification.TYPE_WARNING_MESSAGE);
	}

	public boolean deleteData(Session session,Transaction tx)
	{
		try{
         
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dvoucherDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			session.createSQLQuery("delete tbjournelInfo  where ReceiptNo='"+txtreceiptNo.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete tbjournalDetails  where ReceiptNo='"+txtreceiptNo.getValue()+"'").executeUpdate();
			session.createSQLQuery(" Delete  "+voucher+" where Voucher_No = '"+txtvoucherNew.getValue().toString()+"'").executeUpdate();

			return true;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	private void tbCmbAddData()
	{
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
	}



	private void insertData(Session session,Transaction tx) 
	{
		String voucharType = "";
		String sqlInfo="",sqlDetails="",sqlUdInfo="",sqlUdDetails="";

		String suplierId=supplierLedger();

		String imgPatReq = imagePatRequisition(1,txtreceiptNo.getValue().toString())==null?imageLoc:imagePatRequisition(1,txtreceiptNo.getValue().toString());

		try
		{
			String id = "";
			voucharType = Option();
			/*if(!isUpdate)
			{
				id=autoReciptNo();
			}
			else
			{
				id=txtreceiptNo.getValue().toString();
			}
			*/
			if(!isUpdate)
			{
				String vocherId="";
				vocherId=vocherIdGenerate();
				txtvoucherNew.setValue(vocherId);	
			}
			
			String itemType="";
			
			if(cmbItemType.getValue()!=null)
			{
				itemType=	cmbItemType.getItemCaption(cmbItemType.getValue());
			}
			else
			{
				itemType=	cmbPurchaseType.getValue().toString();	
			}
			
			
			System.out.println(sqlUdInfo);
			for (int i = 0; i < cmbProduct.size(); i++)
			{
				Object temp = cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue());
				if (cmbProduct.get(i).getValue()!= null )
				{
					String productId =cmbProduct.get(i).getValue().toString().trim();

					String ProductLedeger="";
					ProductLedeger= productlededger(i);

					String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dvoucherDate.getValue())+"')").list().iterator().next().toString();
					String voucher =  "voucher"+fsl;

					String pono=" ";
					if(cmbPoNo.getValue()!=null)
					{
						pono="-PO. No:-"+cmbPoNo.getValue().toString();
					}
					
					
					String Naration=itemType+pono+"-Challan No:-"+txtchallanNo.getValue()+"-Challan Date:-"+new SimpleDateFormat("yyyy-MM-dd").format(dChallanDate.getValue()).toString()+"-Receipt No: "+txtreceiptNo.getValue()+"-Bill No: "+txtbillNo.getValue().toString();
					System.out.println("Receipt Data"+voucher);

					if(i==0)
					{
						double cramount=0.00;
						if(!txtvatAmount.getValue().toString().isEmpty())
						{
							cramount=Double.parseDouble(txttotalField.getValue().toString())+Double.parseDouble(txtvatAmount.getValue().toString());  
						}
						else
						{
							cramount=Double.parseDouble(txttotalField.getValue().toString());
						}

						String SupplierVoucherquery=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
								+" values('"+txtvoucherNew.getValue().toString()+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dvoucherDate.getValue())+"','"+suplierId+"', "  
								+" '"+Naration+"','0' , "
								+" '"+df.format(cramount)+"','jau','U-1','1' ,'"+sessionBean.getUserId()+"', "
								+" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "
								+" '0', '"+sessionBean.getCompanyId()+"','"+imgPatReq+"' ,'"+cmbSupplier.getItemCaption(cmbSupplier.getValue().toString())+"','storejournal') ";

						session.createSQLQuery(SupplierVoucherquery).executeUpdate();
						System.out.println("Supplier"+SupplierVoucherquery);

						if(!txtvatAmount.getValue().toString().isEmpty()||Double.parseDouble(txtvatAmount.getValue().toString().isEmpty()?"0.00":txtvatAmount.getValue().toString())>0 )
						{
							String vatVoucher=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
									+" values('"+txtvoucherNew.getValue().toString()+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dvoucherDate.getValue())+"','AL127', "  
									+" '"+Naration+"','"+txtvatAmount.getValue().toString()+"' , "
									+" '0','jau','U-1','1' ,'"+sessionBean.getUserId()+"', "
									+" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "
									+" '0', '"+sessionBean.getCompanyId()+"' ,'"+imgPatReq+"','"+cmbSupplier.getItemCaption(cmbSupplier.getValue().toString())+"','storejournal' ) ";

							session.createSQLQuery(vatVoucher).executeUpdate();
							System.out.println("purchase"+vatVoucher); 	

						}
						
						
						sqlInfo = "insert into tbjournelInfo values (" +
								" '"+txtreceiptNo.getValue().toString()+"','"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dReceiptDate.getValue()) + "', " +
								" '"+cmbSupplier.getValue()+"','"+txttotalField.getValue()+"','"+txtchallanNo.getValue()+"', " +
								" '"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dChallanDate.getValue())+"'," +
								" '"+(cmbPoNo.getValue()!=null?cmbPoNo.getValue().toString().trim():"0")+"'," +
								" '"+imgPatReq+"','"+txtvoucherNew.getValue().toString()+"','" +voucharType+"','0'," +
								" '0','"+sessionBean.getUserName()+ "'," +
								" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
								" '"+itemType+"','"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dpodate.getValue()) + "', '"+cmbMrrNo.getValue().toString()+"', 'With MRR', '"+cmbPurchaseType.getValue()+"','1','"+txtvatpercent.getValue().toString()+"','"+txtvatAmount.getValue().toString()+"','"+txttotalAmount.getValue().toString()+"','"+txtbillNo.getValue().toString()+"'  )";	
						session.createSQLQuery(sqlInfo).executeUpdate();
						System.out.println("tbjournelInfo"+sqlInfo);
					}
					String purchasevoucherquery=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
							+" values('"+txtvoucherNew.getValue().toString()+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dvoucherDate.getValue())+"','"+ProductLedeger+"', "  
							+" '"+Naration+"','"+amount.get(i).getValue().toString()+"' , "
							+" '0','jau','U-1','1' ,'"+sessionBean.getUserId()+"', "
							+" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "
							+" '0', '"+sessionBean.getCompanyId()+"' ,'"+imgPatReq+"','"+cmbSupplier.getItemCaption(cmbSupplier.getValue().toString())+"','storejournal' ) ";

					session.createSQLQuery(purchasevoucherquery).executeUpdate();
					System.out.println("purchae"+purchasevoucherquery);

					sqlDetails = "insert into tbjournalDetails values(" +
							" '"+txtreceiptNo.getValue().toString()+"','"+productId.trim()+"','"+poQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
							" '"+okQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
							" '"+rejectQty.get(i).getValue().toString().replaceAll(",","")+"'," +
							" '"+rate.get(i).getValue().toString().replaceAll(",", "")+"','"+tbTxtRemark.get(i).getValue().toString().replaceAll("'", " ")+"', " +
							" '"+sessionBean.getUserName()+"'," +
							" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" '"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dReceiptDate.getValue()) + "'," +
							" '"+(tCmbStoreLocation.get(i).getValue()!=null?tCmbStoreLocation.get(i).getValue().toString():"")+"'," +
							" '"+(tCmbStoreLocation.get(i).getValue()!=null?tCmbStoreLocation.get(i).getItemCaption(tCmbStoreLocation.get(i).getValue()):"")+"','"+cmbItemType.getItemCaption(cmbItemType.getValue())+"') ";

					session.createSQLQuery(sqlDetails).executeUpdate();
					System.out.println(sqlDetails);


                        //update tbRawPurchaseDetails set Rate ='',Qty='' where ProductID='' and ReceiptNo=''

					
					session.createSQLQuery( "update tbRawPurchaseDetails set Rate ='"+rate.get(i).getValue().toString().replaceAll(",", "")+"',Qty='"+okQty.get(i).getValue().toString().replaceAll(",", "")+"' where ProductID='"+productId.trim()+"' and ReceiptNo='"+txtreceiptNo.getValue().toString()+"' ").executeUpdate();
					session.createSQLQuery( "update tbRawPurchaseInfo set flag='1' where MrrNo='"+cmbMrrNo.getValue().toString()+"'").executeUpdate();
					session.createSQLQuery( "update tbRawPurchaseInfo set supplierId='"+cmbSupplier.getValue().toString()+"' where  MrrNo='"+cmbMrrNo.getValue().toString()+"' ").executeUpdate();
					session.createSQLQuery( "update tbRawPurchaseInfo set VoucherNo='"+txtvoucherNew.getValue().toString()+"' where MrrNo='"+cmbMrrNo.getValue().toString()+"' ").executeUpdate();
					session.createSQLQuery( "update tbRawPurchaseInfo set transactionType='"+cmbPurchaseType.getValue().toString()+"',ChallanNo='"+txtchallanNo.getValue().toString()+"',billNO='"+txtbillNo.getValue().toString()+"' where MrrNo='"+cmbMrrNo.getValue().toString()+"' ").executeUpdate();
					System.out.println("Voucher Name"+txtvoucherNew.getValue().toString());
				
				}
			}

			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			//txtClear("");
			
			btnIni(true);
			componentIni(true);
			cmbMrrLoad();
			isFind=false;
			isUpdate=false;
		}
		catch(Exception exp)
		{
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
			
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dvoucherDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			
			String query="select 'JV-NO-' + cast(isnull(max(cast(replace(Voucher_No, 'JV-NO-', '')as int))+1, 1)as varchar) from "+voucher+" where substring(vouchertype ,1,1) = 'j'";
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


	private String Option()
	{
		strFlag="debit";
		if(strFlag.equalsIgnoreCase("debit"))
			strFlag= "RDP";
		else
			strFlag= "RCP";

		return strFlag;
	}

	private void findButtonEvent()
	{

		txtClear("");
		isFind=true;
		dvoucherDate.setEnabled(true);
		cmbMrrNo.setEnabled(true);
		cmbvoucherNo.setEnabled(true);
		cmbSupplier.setEnabled(true);
		cmbMrrNo.removeAllItems();
		FindMrrLoad();
		findVoucherNoLoad();

	}


}
