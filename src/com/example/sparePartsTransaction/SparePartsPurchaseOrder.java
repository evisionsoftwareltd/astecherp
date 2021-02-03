package com.example.sparePartsTransaction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.xml.JRPenFactory.Right;

import org.apache.naming.java.javaURLContextFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.setupTransaction.SupplierInformation;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExampleNew;
import com.common.share.ImmediateUploadNote;
import com.common.share.MessageBox;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.rawMaterialSetup.RawItemCategory;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;

public class SparePartsPurchaseOrder extends Window 
{
	private SessionBean sessionBean;
	private Label lblDate;
	private Label lblPoNo;
	private Label lblSupplier;
	private Label lblLastSupplyDate;
	private Label lblUserName;
	private Label lblPlace;
	private Label lblQuotaRef;
	private Label lblQuotaDate;
	private Label lblStoreName;
	private Label lblMasterReq;
	private Label lblTerms;
	private Label lblItemType;

	private PopupDateField dateField;
	private TextField txtpo;
	private ComboBox cmbSupplier;
	private ComboBox cmbStoreName;
	private ComboBox cmbSectionReqNo;
	private PopupDateField lastsupplyDate;
	private TextRead txtuser;
	private TextField txtplace;
	private TextField txtQuotaRef;
	private PopupDateField dQuotaDate;
	private TextArea txtMasterReqNo;
	private TextField txtterscondition1;
	private TextField txtterscondition2;
	private TextField txtterscondition3;
	private TextField txtterscondition4;
	private TextField txtterscondition5;

	private TextRead txtcolorBox1=new TextRead();
	private TextRead txtcolorBox2=new TextRead();
	private TextRead txtcolorBox3=new TextRead();
	private TextRead txtcolorBox4=new TextRead();
	private TextRead txtcolorBox5=new TextRead();

	private CheckBox chkWithOutReq;
	private ComboBox cmbItemType;

	public NativeButton btnSection;

	private Table table = new Table();

	private ArrayList<Label> txtsl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<Label> qty = new ArrayList<Label>();
	private ArrayList<TextRead> unit=new ArrayList<TextRead>();
	private ArrayList<AmountField> OrderQty = new ArrayList<AmountField>();
	private ArrayList<AmountField>  rate= new ArrayList<AmountField>();
	private ArrayList<Label> amount = new ArrayList<Label>();

	private ArrayList<TextField> remarks = new ArrayList<TextField>();


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


	private static final String[] cities = new String[] { "Active", "Inactive" };
	TextArea txtInActioveRemarks;
	Label lblInactiveDate=new Label("Inactive Date :");
	Label lblInactioveRemarks=new Label("Remarks :");
	ComboBox cmbStatus;
	PopupDateField dInActive;

	SimpleDateFormat dateFYMD = new SimpleDateFormat("yyyy-MM-dd");

	private AbsoluteLayout mainLayout;

	private ImmediateUploadNote bpvUpload = new ImmediateUploadNote("");
	Button btnPreview;
	String imageLoc = "0" ;
	String filePathTmp = "";
	String bpvPdf = null;
	String tempimg="";
	private Label lblCommon;
	public SparePartsPurchaseOrder(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("PURCHASE ORDER :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		supplierNameData();
		storeNumData();
		cmbItemTypeData();
		setEventAction();
		btnIni(true);
		componentIni(true);
		focusEnter();

	}

	private void storeNumData() {

		cmbStoreName.removeAllItems();
		Transaction tx = null;
		try {
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("select * from tbDepoInformation where vDepoId in(select sectionid from tbRawRequisitionInfo)").list();
			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbStoreName.addItem(element[1].toString());
				cmbStoreName.setItemCaption(element[1].toString(), element[2].toString());

			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
	}
	private void cmbItemTypeData() {

		cmbItemType.removeAllItems();
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List list = session.createSQLQuery("select 0,vCategoryType from tbRawItemCategory  where vCategoryType  like '%Spare Parts%'").list();

			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				//cmbDepartment.addItem(element[0]+"#");
				//cmbDepartment.setItemCaption(element[0]+"#", (String) element[1]);
				cmbItemType.addItem(element[1]);
				//cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("1165px");
		setHeight("626px");

		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("220px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:18.0px;left:20.0px;");

		// dateField
		dateField = new PopupDateField();
		dateField.setImmediate(true);
		dateField.setWidth(dateFieldWidth);
		dateField.setHeight("-1px");
		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dateField, "top:18.0px;left:120.0px;");

		// lblPoNo
		lblPoNo = new Label();
		lblPoNo.setImmediate(true);
		lblPoNo.setWidth("220px");
		lblPoNo.setHeight("-1px");
		lblPoNo.setValue("PO No :");
		mainLayout.addComponent(lblPoNo, "top:43.0px;left:20.0px;");

		// txtPo
		txtpo = new TextField();
		txtpo.setImmediate(true);
		txtpo.setWidth(textFieldWidth);
		txtpo.setHeight("23px");
		mainLayout.addComponent(txtpo, "top:43.0px;left:121.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='5px'> * </Font></b>" ,Label.CONTENT_XHTML), "top:43.0px;left:270.0px;");

		// lblSupplier
		lblSupplier = new Label();
		lblSupplier.setImmediate(true);
		lblSupplier.setWidth("220px");
		lblSupplier.setHeight("-1px");
		lblSupplier.setValue("Supplier Name :");
		mainLayout.addComponent(lblSupplier, "top:68.0px;left:20.0px;");

		// cmbSupplier
		cmbSupplier = new ComboBox();
		cmbSupplier.setImmediate(true);
		cmbSupplier.setNewItemsAllowed(false);
		cmbSupplier.setNullSelectionAllowed(true);
		cmbSupplier.setWidth(comboWidth);
		cmbSupplier.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSupplier, "top:68.0px;left:120.0px;");

		//btnSection
		btnSection = new NativeButton();
		btnSection.setIcon(new ThemeResource("../icons/add.png"));
		btnSection.setImmediate(true);
		btnSection.setWidth("28px");
		btnSection.setHeight("24px");
		mainLayout.addComponent(btnSection, "top:68.0px;left:340.0px;");

		mainLayout.addComponent(new Label("<b><Font Color='#CD0606' size='5px'><sub>* </sub></Font></b>&nbsp&nbsp -- Mandatory Field." ,Label.CONTENT_XHTML), "top:95.0px;left:120.0px;");

		// lblUser
		lblUserName = new Label();
		lblUserName.setImmediate(true);
		lblUserName.setWidth(comboWidth);
		lblUserName.setValue("User :");
		mainLayout.addComponent(lblUserName, "top:18.0px;left:370.0px;");

		//txtuser
		txtuser = new TextRead();
		txtuser.setImmediate(true);
		txtuser.setWidth(textFieldWidth);
		txtuser.setHeight("24px");
		mainLayout.addComponent(txtuser, "top:18.0px;left:485.0px;");

		// lblLastSupplyDate
		lblLastSupplyDate = new Label();
		lblLastSupplyDate.setImmediate(true);
		lblLastSupplyDate.setWidth("-1px");
		lblLastSupplyDate.setHeight("-1px");
		lblLastSupplyDate.setValue("Last Suppply Date :");
		mainLayout.addComponent(lblLastSupplyDate, "top:43.0px;left:370.0px;");

		// lastsupplyDate
		lastsupplyDate = new PopupDateField();
		lastsupplyDate.setImmediate(true);
		lastsupplyDate.setWidth(dateFieldWidth);
		lastsupplyDate.setDateFormat("dd-MM-yyyy");
		lastsupplyDate.setValue(new java.util.Date());
		lastsupplyDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(lastsupplyDate, "top:43.0px;left:485.0px;");

		// lblPlace
		lblPlace = new Label();
		lblPlace.setImmediate(true);
		lblPlace.setWidth("-1px");
		lblPlace.setHeight("-1px");
		lblPlace.setValue("Place :");
		mainLayout.addComponent(lblPlace, "top:68.0px;left:370.0px;");

		// txtplace
		txtplace = new TextField();
		txtplace.setImmediate(true);
		txtplace.setWidth(textFieldWidth);
		mainLayout.addComponent(txtplace, "top:68.0px;left:485.0px;");

		// lblQuotaRef
		lblQuotaRef = new Label();
		lblQuotaRef.setImmediate(true);
		lblQuotaRef.setWidth("-1px");
		lblQuotaRef.setHeight("-1px");
		lblQuotaRef.setValue("Quota. Ref :");
		mainLayout.addComponent(lblQuotaRef, "top:18.0px;left:635.0px;");

		// txtptxtQuotaReflace
		txtQuotaRef = new TextField();
		txtQuotaRef.setImmediate(true);
		txtQuotaRef.setWidth(dateFieldWidth);
		mainLayout.addComponent(txtQuotaRef, "top:18.0px;left:722.0px;");

		// lblQuotaDate
		lblQuotaDate = new Label();
		lblQuotaDate.setImmediate(true);
		lblQuotaDate.setWidth("-1px");
		lblQuotaDate.setHeight("-1px");
		lblQuotaDate.setValue("Quota. Date :");
		mainLayout.addComponent(lblQuotaDate, "top:43.0px;left:635.0px;");

		// txtptxtQuotaReflace
		dQuotaDate = new PopupDateField();
		dQuotaDate.setImmediate(true);
		dQuotaDate.setWidth(dateFieldWidth);
		dQuotaDate.setDateFormat("dd-MM-yyyy");
		dQuotaDate.setValue(new java.util.Date());
		dQuotaDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dQuotaDate, "top:43.0px;left:722.0px;");

		// lblMasterReq
		lblItemType = new Label();
		lblItemType.setImmediate(true);
		lblItemType.setWidth("-1px");
		lblItemType.setHeight("-1px");
		lblItemType.setValue("Item Type :");
		mainLayout.addComponent(lblItemType, "top:18.0px;left:840.0px;");

		// txtMasterReqNo
		cmbItemType = new ComboBox();
		cmbItemType.setImmediate(true);
		cmbItemType.setNewItemsAllowed(false);
		cmbItemType.setNullSelectionAllowed(true);
		cmbItemType.setWidth(comboWidth);
		cmbItemType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbItemType, "top:18.0px;left:932.0px;");
		// lblMasterReq
		lblStoreName = new Label();
		lblStoreName.setImmediate(true);
		lblStoreName.setWidth("-1px");
		lblStoreName.setHeight("-1px");
		lblStoreName.setValue("Store Name :");
		mainLayout.addComponent(lblStoreName, "top:45.0px;left:840.0px;");

		// txtMasterReqNo
		cmbStoreName = new ComboBox();
		cmbStoreName.setImmediate(true);
		cmbStoreName.setNewItemsAllowed(false);
		cmbStoreName.setNullSelectionAllowed(true);
		cmbStoreName.setWidth(comboWidth);
		cmbStoreName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbStoreName, "top:45.0px;left:932.0px;");

		// lblTerms
		lblMasterReq = new Label();
		lblMasterReq.setImmediate(true);
		lblMasterReq.setWidth("-1px");
		lblMasterReq.setHeight("-1px");
		lblMasterReq.setValue("Section Req No :");
		mainLayout.addComponent(lblMasterReq, "top:72.0px;left:840.0px;");

		// txtMasterReqNo
		cmbSectionReqNo = new ComboBox();
		cmbSectionReqNo.setImmediate(true);
		cmbSectionReqNo.setNewItemsAllowed(false);
		cmbSectionReqNo.setNullSelectionAllowed(true);
		cmbSectionReqNo.setWidth(comboWidth);
		cmbSectionReqNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSectionReqNo, "top:72.0px;left:932.0px;");

		chkWithOutReq=new CheckBox("Without Requisition");
		chkWithOutReq.setImmediate(true);
		mainLayout.addComponent(chkWithOutReq, "top:99.0px;left:932.0px;");

		// table 
		table.setWidth("98%");
		table.setHeight("230px");

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",30);

		table.addContainerProperty("Product Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("Product Name",330);

		table.addContainerProperty("Stock Qty",Label.class , new Label() ,null, null, Table.ALIGN_RIGHT);
		table.setColumnWidth("Stock Qty",90);
		table.setColumnAlignment("Stock Qty", Table.ALIGN_RIGHT);


		table.addContainerProperty("Unit", TextRead.class, new TextRead(), null, null, Table.ALIGN_CENTER);
		table.setColumnWidth("Unit", 65);

		table.addContainerProperty("Order Qty", AmountField.class , new AmountField(),null, null, Table.ALIGN_RIGHT);
		table.setColumnWidth("Order Qty",90);

		table.addContainerProperty("Rate", AmountField.class , new AmountField(),null, null, Table.ALIGN_RIGHT);
		table.setColumnWidth("Rate",85);
		table.setColumnAlignment("Rate", Table.ALIGN_RIGHT);

		table.addContainerProperty("Amount", Label.class , new Label(),null, null, Table.ALIGN_RIGHT);
		table.setColumnWidth("Amount",120);

		table.addContainerProperty("remarks1", TextField.class , new TextField());
		table.setColumnWidth("remarks1",200);

		table.setColumnCollapsingAllowed(false);

		tableInitialise();
		mainLayout.addComponent(table, "top:130.0px;left:23.0px;");

		lblTerms = new Label("<font color=red size=+1>Terms & Condition :</font>",Label.CONTENT_XHTML);
		lblTerms.setImmediate(true);
		lblTerms.setWidth("-1px");
		lblTerms.setHeight("-1px");
		mainLayout.addComponent(lblTerms, "top:365.0px;left:20.0px;");

		txtcolorBox1 = new TextRead();
		txtcolorBox1.setImmediate(true);
		txtcolorBox1.setWidth("12px");
		txtcolorBox1.setHeight("12px");
		txtcolorBox1.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox1, "top:395.0px;left:165.0px;");

		txtterscondition1 = new TextField();
		txtterscondition1.setImmediate(true);
		txtterscondition1.setWidth("650px");
		txtterscondition1.setHeight("24px");
		mainLayout.addComponent(txtterscondition1, "top:390.0px;left:195.0px;");

		txtcolorBox2 = new TextRead();
		txtcolorBox2.setImmediate(true);
		txtcolorBox2.setWidth("12px");
		txtcolorBox2.setHeight("12px");
		txtcolorBox2.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox2, "top:421.0px;left:165.0px;");

		txtterscondition2 = new TextField();
		txtterscondition2.setImmediate(true);
		txtterscondition2.setWidth("650px");
		txtterscondition2.setHeight("24px");
		mainLayout.addComponent(txtterscondition2, "top:416.0px;left:195.0px;");

		txtcolorBox3 = new TextRead();
		txtcolorBox3.setImmediate(true);
		txtcolorBox3.setWidth("12px");
		txtcolorBox3.setHeight("12px");
		txtcolorBox3.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox3, "top:447.0px;left:165.0px;");

		txtterscondition3 = new TextField();
		txtterscondition3.setImmediate(true);
		txtterscondition3.setWidth("650px");
		txtterscondition3.setHeight("24px");
		mainLayout.addComponent(txtterscondition3, "top:442.0px;left:195.0px;");

		txtcolorBox4 = new TextRead();
		txtcolorBox4.setImmediate(true);
		txtcolorBox4.setWidth("12px");
		txtcolorBox4.setHeight("12px");
		txtcolorBox4.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox4, "top:473.0px;left:165.0px;");

		txtterscondition4 = new TextField();
		txtterscondition4.setImmediate(true);
		txtterscondition4.setWidth("650px");
		txtterscondition4.setHeight("24px");
		mainLayout.addComponent(txtterscondition4, "top:468.0px;left:195.0px;");

		txtcolorBox5 = new TextRead();
		txtcolorBox5.setImmediate(true);
		txtcolorBox5.setWidth("12px");
		txtcolorBox5.setHeight("12px");
		txtcolorBox5.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox5, "top:499.0px;left:165.0px;");

		txtterscondition5 = new TextField();
		txtterscondition5.setImmediate(true);
		txtterscondition5.setWidth("650px");
		txtterscondition5.setHeight("24px");
		mainLayout.addComponent(txtterscondition5, "top:494.0px;left:195.0px;");

		cmbStatus = new ComboBox();
		cmbStatus.setImmediate(true);
		cmbStatus.setWidth("110px");
		cmbStatus.setHeight("-1px");
		cmbStatus.setFilteringMode(cmbStatus.FILTERINGMODE_CONTAINS);
		//mainLayout.addComponent(new Label("Status"),"top:394.0px; left:860.0px;");
		mainLayout.addComponent(cmbStatus, "top:391.0px; left:937.0px;");
		cmbStatus.setNullSelectionAllowed(false);
		for (int i = 0; i < cities.length; i++) {
			cmbStatus.addItem(cities[i]);
		}
		cmbStatus.setValue(cities[0]);
		cmbStatus.setVisible(false);

		dInActive = new PopupDateField("");
		dInActive.setImmediate(true);
		dInActive.setWidth("110px");
		dInActive.setHeight("-1px");
		dInActive.setDateFormat("dd-MM-yyyy");
		dInActive.setValue(new java.util.Date());
		dInActive.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(lblInactiveDate,"top:425.0px; left:860.0px;");
		mainLayout.addComponent(dInActive, "top:423.0px; left:937.0px;");

		lblInactiveDate.setVisible(false);
		dInActive.setVisible(false);

		txtInActioveRemarks = new TextArea();
		txtInActioveRemarks.setImmediate(false);
		txtInActioveRemarks.setWidth("200px");
		txtInActioveRemarks.setHeight("48px");
		txtInActioveRemarks.setImmediate(true);
		mainLayout.addComponent(lblInactioveRemarks,"top:455.0px; left:860.0px;");
		mainLayout.addComponent(txtInActioveRemarks, "top:452.0px; left:937.5px;");

		lblInactioveRemarks.setVisible(false);
		txtInActioveRemarks.setVisible(false);

		////////////////////
		// bpvUpload
		lblCommon = new Label("Upload:");
		mainLayout.addComponent(lblCommon, "top:81.0px; left:635px;");
		mainLayout.addComponent(bpvUpload, "top:70.0px;left:720.0px;");
		bpvUpload.setWidth("100px");
		// btnPreview
		btnPreview = new Button("Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
	
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:108.0px;left:727.0px;");
		btnPreview.setEnabled(false);
		/*	NativeButton btnIcon=new NativeButton();
			btnIcon.setIcon(new ThemeResource("../icons/AttachBlk-44.png"));
			btnIcon.setStyleName(Button.STYLE_LINK);
			mainLayout.addComponent(btnIcon, "top:478.0px;left:175.0px;");*/
		lbLine = new Label("_____________________________________________________________________________________________________________________________________________________________");
		mainLayout.addComponent(lbLine, "top:520.0px;left:23.0px;");

		mainLayout.addComponent(cButton, "top:550.0px;left:290.0px;");

		return mainLayout;
	}

	public void tableInitialise()
	{
		for(int i=0;i<6;i++)
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
			txtsl.add(ar,new Label());
			txtsl.get(ar).setWidth("95%");
			txtsl.get(ar).setValue(ar+1);

			cmbProduct.add(ar, new ComboBox());
			cmbProduct.get(ar).setWidth("95%");
			cmbProduct.get(ar).setImmediate(true);
			cmbProduct.get(ar).setNullSelectionAllowed(true);
			cmbProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			/*List lst = session.createSQLQuery("Select ProductName, ProductCode, Unit from  tbRawProductInfo order by ProductName").list();

			cmbProduct.get(ar).addItem("x#" + ar);
			cmbProduct.get(ar).setItemCaption("x#" + ar,"");


			for (Iterator iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbProduct.get(ar).addItem(element[1].toString() + "#" + ar);
				cmbProduct.get(ar).setItemCaption(element[1].toString() + "#" + ar,element[0].toString());

				hUnit.put(element[1], element[2]);
				hItemCode.put(element[0], element[1]);
			})*/

			cmbProduct.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbProduct.get(ar).getValue()!=null)
					{
						boolean fla = (doubleEntryCheck(cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue()), ar));

						if ((Object) cmbProduct.get(ar).getValue() != null && fla) 
						{
							Transaction tx=null;
							try{
								Session session = SessionFactoryUtil.getInstance().getCurrentSession();
								tx = session.beginTransaction();
								String productId=cmbProduct.get(ar).getValue().toString();

								List list = session.createSQLQuery("select * from dbo.[funRawMaterialsStock]('"+ dateFormat.format(dateField.getValue())+ " 23:59:59','" + productId + "')").list();

								int cmbflag = 0;

								if (list.iterator().hasNext()) 
								{
									Object[] element = (Object[]) list.iterator().next();

									cmbflag = 1;

									qty.get(ar).setValue(decimalform.format(element[21]));
									rate.get(ar).setValue(decimalform.format(element[22]));
								}

								List lst = session.createSQLQuery(" select 0, vUnitName from tbRawItemInfo where vRawItemCode like '"+productId+"' ").list();
								System.out.print(lst.iterator().next());
								Iterator iter = lst.iterator();
								if(iter.hasNext())
								{
									Object[] element = (Object[]) iter.next();
									System.out.println(element[1].toString());
									unit.get(ar).setValue(element[1].toString());
									OrderQty.get(ar).focus();
								}
								
								if(ar==cmbProduct.size()-1)
								{
									tableRowAdd(ar+1);
									
									if(cmbSectionReqNo.getValue()!=null)
									{
										cmbProductSectionWise(ar+1);	
									}
									else
									{
									   cmbProductAdd(ar+1)	;
										
									}
									
								}
							}
							catch(Exception exp){
								getParent().showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
							}
						}

						else 
						{
							getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);

							cmbProduct.get(ar).setValue(null);
							qty.get(ar).setValue("");
							unit.get(ar).setValue("");
							OrderQty.get(ar).setValue("");
							rate.get(ar).setValue("");
							amount.get(ar).setValue("");
							remarks.get(ar).setValue("");
						}
					}
				}
			});

			qty.add(ar,new Label());
			qty.get(ar).setWidth("95%");		
			qty.get(ar).setImmediate(true);
			qty.get(ar).setStyleName("stockQty");

			unit.add(ar, new TextRead(""));
			unit.get(ar).setWidth("95%");

			OrderQty.add(ar,new AmountField());
			OrderQty.get(ar).setWidth("95%");
			OrderQty.get(ar).setImmediate(true);
			//OrderQty.get(ar).setStyleName("OrserQty");
			OrderQty.get(ar).addListener(new TextChangeListener() 
			{	
				public void textChange(TextChangeEvent event)
				{
					totalAmount(event, ar, "Qty");
				}
			});

			rate.add(ar,new AmountField());
			rate.get(ar).setWidth("95%");
			rate.get(ar).setImmediate(true);
			//rate.get(ar).setStyleName("OrserQty");
			rate.get(ar).addListener(new TextChangeListener() 
			{	
				public void textChange(TextChangeEvent event)
				{
					totalAmount(event, ar, "Rate");
				}
			});

			amount.add(ar,new Label());
			amount.get(ar).setWidth("95%");
			amount.get(ar).setImmediate(true);
			amount.get(ar).setStyleName("stockQty");

			remarks.add(ar,new TextField(""));
			remarks.get(ar).setWidth("95%");

			table.addItem(new Object[]{txtsl.get(ar),cmbProduct.get(ar),qty.get(ar),unit.get(ar),OrderQty.get(ar),rate.get(ar),amount.get(ar),remarks.get(ar)},ar);

		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	public void proComboChange(String head,int r)
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String productId = cmbProduct.get(r).getValue().toString().trim();
			productId = productId.substring(0, productId.indexOf('#'));
			if(!productId.equalsIgnoreCase("x")){
				List list = session.createSQLQuery("select * from dbo.[funRawMaterialsStock]('"+ dateFormat.format(dateField.getValue())+ " 23:59:59','" + productId + "')").list();

				int cmbflag = 0;

				if (list.iterator().hasNext()) 
				{
					Object[] element = (Object[]) list.iterator().next();

					cmbflag = 1;

					qty.get(r).setValue(element[21]);
					rate.get(r).setValue(decimalform.format(element[22]));
				}

				List lst = session.createSQLQuery("select 0, Unit from tbRawProductInfo where ProductCode like '"+productId+"' ").list();
				System.out.print(lst.iterator().next());
				Iterator iter = lst.iterator();
				if(iter.hasNext())
				{
					Object[] element = (Object[]) iter.next();
					System.out.println(element[1].toString());
					unit.get(r).setValue(element[1].toString());

				}

				System.out.println("a" + qty.get(r).getValue());

				if (!cmbProduct.get(r).getItemCaption(cmbProduct.get(r).getValue()).equals("") && qty.get(r).getValue().equals("")) 
				{
					getParent().showNotification("Warnning","Stock is not Available!",Notification.TYPE_ERROR_MESSAGE);

					Object checkNull = (Object) cmbProduct.get(r).getItemCaption(cmbProduct.get(r).getValue());

					if (!checkNull.equals("")) 
					{
						cmbProduct.get(r).setValue("x#" + r);
					}
				}
			}
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error6", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void supplierNameData()
	{
		cmbSupplier.removeAllItems();
		Transaction tx = null;
		try {
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("select * from tbSupplierInfo").list();
			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplier.addItem(element[1].toString());
				cmbSupplier.setItemCaption(element[1].toString(), element[2].toString());

			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
	}


	private void focusEnter()
	{
		allComp.add(txtpo);
		allComp.add(cmbSupplier);
		allComp.add(lastsupplyDate);
		allComp.add(txtplace);
		allComp.add(txtQuotaRef);
		allComp.add(dQuotaDate);
		//allComp.add(txtMasterReqNo);
		allComp.add(cmbItemType);
		allComp.add(cmbStoreName);
		allComp.add(cmbSectionReqNo);
		//allComp.add(txtterscondition);

		for(int i=0;i<cmbProduct.size();i++)
		{
			allComp.add(cmbProduct.get(i));
			allComp.add(OrderQty.get(i));
			allComp.add(rate.get(i));
			allComp.add(remarks.get(i));
		}

		allComp.add(txtterscondition1);
		allComp.add(txtterscondition2);
		allComp.add(txtterscondition3);
		allComp.add(txtterscondition4);
		allComp.add(txtterscondition5);

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);
		allComp.add(cButton.btnFind);

		new FocusMoveByEnter(this,allComp);
	}
	private void storeWiseReqNoLoad(){

		cmbSectionReqNo.removeAllItems();
		Transaction tx = null;
		try {
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("select 0,sectionReqNo from tbRawRequisitionInfo where sectionId like '"+cmbStoreName.getValue()+"'").list();
			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbSectionReqNo.addItem(element[1].toString());
				//cmbSectionReqNo.setItemCaption(element[1].toString(), element[2].toString());
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
	}
	private void cmbProductAdd(){


		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql="select vRawItemCode,vRawItemName,subString(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName))as category,"
					+" vSubSubCategoryName from tbRawItemInfo where vCategoryType like '"+cmbItemType.getValue()+"' order by category,vSubSubCategoryName";

			List list = session.createSQLQuery(sql).list();

			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				String name = "";
				Object[] element = (Object[]) iter.next();

				//cmbDepartment.addItem(element[0]+"#");
				//cmbDepartment.setItemCaption(element[0]+"#", (String) element[1]);
				for(int a=0;a<cmbProduct.size();a++){
					cmbProduct.get(a).addItem(element[0]);

					if(element[2].toString().isEmpty())
					{
						name=element[1].toString()	;
					}
					else
					{
						name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
					}
					//					String name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
					cmbProduct.get(a).setItemCaption(element[0], name);
				}
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}
	
	
	private void cmbProductAdd(int index){


		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql="select vRawItemCode,vRawItemName,subString(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName))as category,"
					+" vSubSubCategoryName from tbRawItemInfo where vCategoryType like '"+cmbItemType.getValue()+"' order by category,vSubSubCategoryName";

			List list = session.createSQLQuery(sql).list();
			
			cmbProduct.get(index).removeAllItems();
			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				String name = "";
				Object[] element = (Object[]) iter.next();

					cmbProduct.get(index).addItem(element[0]);

					if(element[2].toString().isEmpty())
					{
						name=element[1].toString()	;
					}
					else
					{
						name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
					}

					cmbProduct.get(index).setItemCaption(element[0], name);
				
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}
	
	
	private void cmbProductSectionWise()
	{

		Transaction tx = null;
		try 
		{

			System.out.print("Yes dONE");

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql="select a.productId,a.productName,subString(b.vSubGroupName,CHARINDEX('-',b.vSubGroupName)+1,LEN(b.vSubGroupName))as category,"
					+" b.vSubSubCategoryName from tbRawRequisitionDetails a inner join tbRawItemInfo b "
					+" on a.productId=b.vRawItemCode where a.sectionReqNo like '"+cmbSectionReqNo.getValue()+"' and  b.vCategoryType like '"+cmbItemType.getValue()+"' order by category,b.vSubSubCategoryName";

			List list = session.createSQLQuery(sql).list();

			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				String name = "";
				Object[] element = (Object[]) iter.next();

				//cmbDepartment.addItem(element[0]+"#");
				//cmbDepartment.setItemCaption(element[0]+"#", (String) element[1]);
				for(int a=0;a<cmbProduct.size()-1;a++){
					cmbProduct.get(a).addItem(element[0]);
					if(element[2].toString().isEmpty())
					{
						name=element[1].toString()	;
					}
					else
					{
						name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
					}
					//					String name=element[1].toString();
					cmbProduct.get(a).setItemCaption(element[0], name);
				}
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}
	
	
	
	private void cmbProductSectionWise(int index)
	{

		Transaction tx = null;
		try 
		{

			System.out.print("Yes dONE");

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql="select a.productId,a.productName,subString(b.vSubGroupName,CHARINDEX('-',b.vSubGroupName)+1,LEN(b.vSubGroupName))as category,"
					+" b.vSubSubCategoryName from tbRawRequisitionDetails a inner join tbRawItemInfo b "
					+" on a.productId=b.vRawItemCode where a.sectionReqNo like '"+cmbSectionReqNo.getValue()+"' and  b.vCategoryType like '"+cmbItemType.getValue()+"' order by category,b.vSubSubCategoryName";

			List list = session.createSQLQuery(sql).list();
			cmbProduct.get(index).removeAllItems();
			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				String name = "";
				Object[] element = (Object[]) iter.next();

					cmbProduct.get(index).addItem(element[0]);
					if(element[2].toString().isEmpty())
					{
						name=element[1].toString()	;
					}
					else
					{
						name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
					}
					cmbProduct.get(index).setItemCaption(element[0], name);
				
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}
	
	
	private void reportShow()
	{
		String query=null;
		String activeFlag = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			//hm.put("phone", "Phone: "+sessionBean.getCompanyPhone()+"   Fax:  "+sessionBean.getCompanyFax()+",   E-mail:  "+sessionBean.getCompanyEmail());
			hm.put("user", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			query="Select * from vwPurchaseOrder  where convert(Date,poDate,105) ='"+new SimpleDateFormat("yyyy-MM-dd").format(dateField.getValue()) +"'  and supplierId='"+cmbSupplier.getValue()+"'";
			System.out.println(query);
			hm.put("sql", query);
			Window win = new ReportViewerNew(hm,"report/raw/rptRawPurchaseOrder.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
			win.setCaption("Report : Purchase Order");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp){

			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);

		}
	}
	private void supplierFromLink(){
		Window win = new SupplierInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				supplierNameData();
				System.out.println("Category Form");
			}
		});
		this.getParent().addWindow(win);
	}

	public void cmbStatusAction(){
		dInActive.setValue(new Date());
		txtInActioveRemarks.setValue("");
		dInActive.setVisible(true);
		txtInActioveRemarks.setVisible(true);
		lblInactiveDate.setVisible(true);
		lblInactioveRemarks.setVisible(true);
	}
	public void statusWiseClear(){
		dInActive.setValue(new Date());
		txtInActioveRemarks.setValue("");
		dInActive.setVisible(false);
		txtInActioveRemarks.setVisible(false);
		lblInactiveDate.setVisible(false);
		lblInactioveRemarks.setVisible(false);

	}
	public boolean dateValidation(){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Date dateInactive=(Date) dInActive.getValue();//
		Date datePoDate=(Date) dateField.getValue();
		cal1.setTime(datePoDate);
		cal2.setTime(dateInactive);
		if(cal1.after(cal2)){
			System.out.println("dateInactive is after CurrentDate");
			return false;
		}
		if(cal1.before(cal2)){
			System.out.println("dateInactive is before CurrentDate");
			return true;
		}
		if(cal1.equals(cal2)){
			System.out.println("dateInactive is equal CurrentDate");
			return true;
		}
		return false;
	}

	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		System.out.println("basePath is:"+basePath+bpvUpload.fileName.trim());
		String stuImage = null;
		if(flag==0)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
				try {
					if(bpvUpload.fileName.toString().endsWith(".jpg")){
						String path = sessionBean.getUserId();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+path+".jpg");
						tempimg=basePath+bpvUpload.fileName.trim();
						bpvPdf = SessionBean.imagePath+path+".jpg";
						filePathTmp = path+".jpg";
					}
					else{
						String path = sessionBean.getUserId();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+path+".pdf");
						bpvPdf = SessionBean.imagePath+path+".pdf";
						filePathTmp = path+".pdf";
					}
				} 
			catch (IOException e){
				e.printStackTrace();
			}
			return bpvPdf;
		}

		if(flag==1)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
			{
				try
				{	
					if(bpvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/RawPurchaseOrder/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/RawPurchaseOrder/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/RawPurchaseOrder/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/RawPurchaseOrder/"+path+".pdf";
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}
		return null;
	}

	//////////////////////////////////Tin upload end
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

	public void setEventAction()
	{
		
		btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
					getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
				}
				if(isUpdate)
				{
					if(!bpvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", imageLoc.substring(22, imageLoc.length()));
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});
		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"");
				btnPreview.setCaption("Preview");
				btnPreview.setEnabled(true);
				System.out.println("Done");
			}
		});

		/*btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString();
					
					System.out.println("link is:"+link);
					if(link.endsWith(""+sessionBean.getContextName()+"/"))
					{
						link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+filePathTmp;
						link=imagePath(0,"");
						link=tempimg;
						link=link+"VAADIN/themes"+tempimg.substring(tempimg.lastIndexOf("/"));
						System.out.println(link);
					}
					getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
				}
				if(isUpdate)
				{
					if(!bpvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith(""+sessionBean.getContextName()+"/"))
							{
								link = link.replaceAll(""+sessionBean.getContextName()+"/", imageLoc.substring(22, imageLoc.length()));
							}
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+filePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});
		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"");
				btnPreview.setCaption("Preview");
				btnPreview.setEnabled(true);
				System.out.println("Done");
			}
		});*/


		cmbStatus.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbStatus.getValue()!=null){
					if(cmbStatus.getValue().toString().equals("Inactive")){
						cmbStatusAction();
					}
					else{
						statusWiseClear();
					}
				}
				else{
					statusWiseClear();
				}
			}
		});

		dInActive.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(!dateValidation()){
					dInActive.setValue(new Date());
					showNotification(null,"Inactive date should greater than P.O date",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		btnSection.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				supplierFromLink();
			}
		});
		cButton.btnPreview.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(isFind || cmbSupplier.getValue()!=null){
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
				
				newButtonEvent();
				txtpo.focus();
				//autoPONo();
				txtuser.setValue(sessionBean.getUserName());
			}
		});

		cmbStoreName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbStoreName.getValue()!=null){
					storeWiseReqNoLoad();
				}
			}
		});
		cmbSectionReqNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				System.out.println("I am Ok");
				if(cmbSectionReqNo.getValue()!=null){
					for(int a=0;a<cmbProduct.size();a++){
						
						cmbProduct.get(a).removeAllItems();


					}
					cmbProductSectionWise();
				}
			}
		});

		txtpo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				if(!isFind)
				{
					if(!txtpo.getValue().toString().isEmpty()){
						Transaction tx=null;
						String sql="";

						try
						{
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							tx = session.beginTransaction();
							sql="select *  from  tbRawPurchaseOrderInfo where poNo like '"+txtpo.getValue().toString()+"'";
							List lst=session.createSQLQuery(sql).list();
							if(!lst.isEmpty())
							{
								showNotification("This Po Number is  Already Exists",Notification.TYPE_WARNING_MESSAGE);
								txtpo.setValue("");
							}

						}

						catch(Exception ex)
						{
							showNotification("Here Is Error"+ex);
						}
						;
					}	
				}

			}
		});




		chkWithOutReq.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(chkWithOutReq.booleanValue()){
					cmbStoreName.setEnabled(false);
					cmbSectionReqNo.setEnabled(false);
					cmbStoreName.setValue(null);
					cmbSectionReqNo.setValue(null);
					for(int a=0;a<cmbProduct.size();a++){
						cmbProduct.get(a).removeAllItems();
					}
					cmbProductAdd();
				}
				else{
					cmbStoreName.setEnabled(true);
					cmbSectionReqNo.setEnabled(true);
				}
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
							if(nullCheck()){
								if(cmbStatus.getValue().toString().trim().equals("Inactive")){
									if(!txtInActioveRemarks.getValue().toString().isEmpty()){
										saveButtonEvent();
									}
									else{
										showNotification("Warning!","Provide Inactive remarks.", Notification.TYPE_WARNING_MESSAGE);
										txtInActioveRemarks.focus();
									}
								}
								else{
									saveButtonEvent();
								}

							}else{
								showNotification("Provide All Fields.",Notification.TYPE_WARNING_MESSAGE);
							}

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
				findButtonEvent();
				isFind=true;
			}
		});

		cButton.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
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

		btnSection.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				System.out.println("Designation");
				//sectionLink();
			}
		});
	}

	public void txtClear()
	{
		txtpo.setValue("");
		cmbSupplier.setValue(null);
		txtuser.setValue("");
		txtplace.setValue("");
		//txtMasterReqNo.setValue("");
		txtterscondition1.setValue("");
		txtQuotaRef.setValue("");
		cmbSectionReqNo.setValue(null);
		cmbStoreName.setValue(null);
		cmbItemType.setValue(null);
		chkWithOutReq.setValue(false);
		dateField.setValue(new java.util.Date());
		lastsupplyDate.setValue(new java.util.Date());
		dQuotaDate.setValue(new java.util.Date());
		for(int i=0;i<cmbProduct.size();i++)
		{

			cmbProduct.get(i).setValue(null);
			qty.get(i).setValue("");
			unit.get(i).setValue("");
			OrderQty.get(i).setValue("");
			rate.get(i).setValue("");
			amount.get(i).setValue("");
			remarks.get(i).setValue("");
		}

		txtterscondition1.setValue("");
		txtterscondition2.setValue("");
		txtterscondition3.setValue("");
		txtterscondition4.setValue("");
		txtterscondition5.setValue("");
		cmbStatus.setValue("Active");

		bpvUpload.fileName = "";
		bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		bpvUpload.actionCheck = false;
		imageLoc = "0";
		btnPreview.setEnabled(false);
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
		lbLine.setEnabled(!b);
		table.setEnabled(!b);
		dateField.setEnabled(!b);
		txtpo.setEnabled(!b);

		cmbSupplier.setEnabled(!b);
		btnSection.setEnabled(!b);
		txtuser.setEnabled(!b);
		lastsupplyDate.setEnabled(!b);
		txtplace.setEnabled(!b);
		txtQuotaRef.setEnabled(!b);
		dQuotaDate.setEnabled(!b);
		//txtMasterReqNo.setEnabled(!b);
		txtterscondition1.setEnabled(!b);
		txtterscondition2.setEnabled(!b);
		txtterscondition3.setEnabled(!b);
		txtterscondition4.setEnabled(!b);
		txtterscondition5.setEnabled(!b);
		cmbSectionReqNo.setEnabled(!b);
		cmbStoreName.setEnabled(!b);
		cmbItemType.setEnabled(!b);
		chkWithOutReq.setEnabled(!b);

		cmbStatus.setEnabled(!b);
		txtInActioveRemarks.setEnabled(!b);
		dInActive.setEnabled(!b);
		btnPreview.setEnabled(!b);
		bpvUpload.setEnabled(!b);
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

	private void findButtonEvent() 
	{
		Window win = new FindWindowSP(sessionBean, txtPurchaseOrderId,"purchaseOrderForm");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtPurchaseOrderId.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtPurchaseOrderId.getValue().toString());
					isFind=true;
					if(imageLoc.equals("0"))
					{btnPreview.setCaption("attach");
					//btnPreview.setEnabled(false);
					}
					else
					{btnPreview.setCaption("Preview");
					//btnPreview.setEnabled(true);
					}
				}
			}
		});

		this.getParent().addWindow(win);
	}



	private boolean nullCheck() 
	{
		if (cmbSupplier.getValue() != null)
		{
			for (int i = 0; i < cmbProduct.size(); i++) {
				Object temp = cmbProduct.get(i).getItemCaption(
						cmbProduct.get(i).getValue());
				System.out.println(cmbProduct.get(i).getValue());
				if (temp != null) {
					if (!amount.get(i).getValue().toString().trim().isEmpty()) {

						return true;
					} 
					else
					{
						this.getParent().showNotification("Warning :","Please Enter Valid  Qty .",Notification.TYPE_WARNING_MESSAGE);
					}

				}
			}
		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Issue To .",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}

	private void saveButtonEvent()
	{
		if(isUpdate)
		{				
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update ?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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

						if(deleteData(session,tx)){
							insertData(session,tx);
						}
						mb.close();
					}
				}
			});	
		}
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save ?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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
						mb.close();
					}
				}
			});	

		}

	}

	private String autoPONo()
	{
		String autoId="0";
		Transaction tx=null;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select isnull(max(poNo),0)+1 from tbRawPurchaseOrderInfo").list().iterator();

			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
				txtpo.setValue(autoId);

			}	
		}
		catch(Exception exp){
			System.out.println(exp);
		}

		return autoId;
	}

	private void updateButtonEvent(){

		if(!txtpo.getValue().equals(""))
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);

			//Uses After FindButton Clicked
			cmbSectionReqNo.setEnabled(false);
			cmbStoreName.setEnabled(false);
		}
		else
			this.getParent().showNotification(
					"Nothing to Update.",
					Notification.TYPE_WARNING_MESSAGE);
	}

	private boolean doubleEntryCheck(String caption,int row)
	{
		for(int i=0;i<qty.size();i++)
		{
			if(i!=row && caption.equals(cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue())))
			{
				return false;
			}

		}
		return true;
	}

	public boolean deleteData(Session session,Transaction tx){
		try{
			session.createSQLQuery("delete from tbRawPurchaseOrderInfo where poNo='"+txtpo.getValue().toString()+"' ").executeUpdate();
			session.createSQLQuery("delete from tbRawPurchaseOrderDetails where poNo='"+txtpo.getValue().toString()+"' ").executeUpdate();
			return true;

		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void insertData(Session session,Transaction tx)
	{
		try
		{

			String Inactiveremarks="";
			if(cmbStatus.getValue().toString().equals("Inactive")){
				Inactiveremarks=txtInActioveRemarks.getValue().toString();
			}
			else{
				//Date FastDate=Date.parse("1900-01-01");
				//dInActive.setValue(dateFDMY.format(Date.parse(1900-01-01)));
			}

			String autoID=txtpo.getValue().toString();
			System.out.println("Save Start: ");
			/*String sqlInfo = "insert into tbRawPurchaseOrderInfo " +
					"values('"+autoID+"','"+cmbSupplier.getValue()+"','" + dateFormat.format(dateField.getValue()) + "','" +txtuser.getValue() + "'," +
					"'" + dateFormat.format(lastsupplyDate.getValue()) + "','"+txtplace.getValue()+"','"+txtterscondition.getValue()+"'," +
					"'"+cmbSectionReqNo.getValue().toString().trim()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					"'"+txtQuotaRef.getValue().toString().trim()+"','"+dateFormat.format(dQuotaDate.getValue())+"')";*/
			String poType="";
			String store = cmbStoreName.getValue()!=null?cmbStoreName.getValue().toString():"";
			if(chkWithOutReq.booleanValue()){
				poType=chkWithOutReq.getCaption();
			}
			String attach = imagePath(1,autoID)==null? imageLoc:imagePath(1,autoID);
			String sqlInfo="insert into tbRawPurchaseOrderInfo values('"+autoID+"','"+cmbSupplier.getValue()+"','" + dateFormat.format(dateField.getValue()) + "" +
					"','" + dateFormat.format(lastsupplyDate.getValue()) + "','"+txtplace.getValue()+"','"+txtterscondition1.getValue()+"'," +
					"'"+cmbSectionReqNo.getValue()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate()," +
					"'"+txtQuotaRef.getValue().toString().trim()+"','"+dateFormat.format( dQuotaDate.getValue())+"','"+store+"','"+poType+"',"
					+ "'"+cmbItemType.getValue()+"','"+txtterscondition2.getValue()+"',"
					+ "'"+txtterscondition3.getValue()+"','"+txtterscondition4.getValue()+"',"
					+ "'"+txtterscondition5.getValue()+"','"+cmbStatus.getValue().toString()+"','"+dateFYMD.format(dInActive.getValue() )+"','"+Inactiveremarks+"','"+attach+"')";

			session.createSQLQuery(sqlInfo).executeUpdate();
			System.out.println(sqlInfo);

			String sqlDetails = "";
			for (int i=0; i<cmbProduct.size(); i++)
			{
				Object temp = cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue());
				if (temp != null)
				{
					String productId =cmbProduct.get(i).getValue().toString().trim();
					//productId = productId.substring(0, productId.indexOf('#'));

					sqlDetails = "insert into tbRawPurchaseOrderDetails  values(" +
							"'"+autoID+"','"+productId.trim()+"','"+OrderQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
							"'"+rate.get(i).getValue().toString().replaceAll(",", "")+"'," +
							"'"+amount.get(i).getValue().toString().replaceAll(",", "")+"'," +
							"'"+remarks.get(i).getValue().toString().trim()+"','"+sessionBean.getUserName()+"'," +
							"'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+OrderQty.get(i).getValue()+"','"+OrderQty.get(i).getValue().toString().replaceAll(",", "")+"','1')";

					session.createSQLQuery(sqlDetails).executeUpdate();
					System.out.println(sqlDetails);
				}
			}

			//txtClear();
			componentIni(true);
			btnIni(true);
			this.getParent().showNotification("All information saved successfully.");
			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void findInitialise(String txtPurchaseOrderId) 
	{
		String imgcap="";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List led = session.createSQLQuery("select * from tbRawPurchaseOrderInfo where poNo='"+txtPurchaseOrderId+ "'").list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();
				cmbSupplier.setValue(element[2].toString());
				txtuser.setValue(element[4]);
				txtpo.setValue(element[1]);
				dateField.setValue(element[3]);

				if(!element[5].equals(null)){
					txtplace.setValue(element[5]);
				}else{
					txtplace.setValue("");
				}

				/*if(!element[7].equals(null)){
					txtMasterReqNo.setValue(element[8]);
				}else{
					txtMasterReqNo.setValue("");
				}*/
				cmbItemType.setValue(element[15]);
				cmbStoreName.setValue(element[13]);
				cmbSectionReqNo.setValue(element[7]);

				if(!element[6].equals(null)){
					txtterscondition1.setValue(element[6]);
				}else{
					txtterscondition1.setValue("");
				}

				txtterscondition2.setValue(element[16]);
				txtterscondition3.setValue(element[17]);
				txtterscondition4.setValue(element[18]);
				txtterscondition5.setValue(element[19]);


				txtuser.setValue(element[8]);

				if(!element[12].equals(null)){
					txtQuotaRef.setValue(element[11].toString());
				}else{
					txtQuotaRef.setValue("");
				}
				dQuotaDate.setValue(element[12]);
				lastsupplyDate.setValue(element[4]);
				System.out.println("Select 1");

				if(element[14].toString().equals("Without Requisition")){
					chkWithOutReq.setValue(true);
					cmbSectionReqNo.setEnabled(false);
					cmbStoreName.setEnabled(false);
					cmbProductAdd();
				}
				cmbStatus.setValue(element[20].toString());
				if(element[20].toString().equals("Inactive"))
				{
					dInActive.setValue(element[21]);
					txtInActioveRemarks.setValue(element[22].toString());
				}

				///////////////////////// Attach
				if(!element[23].toString().equals("0")){
					imageLoc=element[23].toString();
					imgcap=element[23].toString().substring(element[23].toString().lastIndexOf("/")+1,element[23].toString().length());
					bpvUpload.status.setValue(new Label("<font size=1px>("+imgcap+")</font>",Label.CONTENT_XHTML));
				}
				else{
					bpvUpload.fileName = "";
					bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
					filePathTmp = "";
					bpvUpload.actionCheck = false;
					imageLoc = "0";
				}
			}

			List list = session.createSQLQuery("Select * from tbRawPurchaseOrderDetails  where poNo='"+txtPurchaseOrderId+ "'").list();

			int i = 0;

			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbProduct.get(i).setValue(element[2]);
				qty.get(i).setValue(df.format(element[3]));
				OrderQty.get(i).setValue(element[10]);
				rate.get(i).setValue(df.format(element[4]));
				amount.get(i).setValue(df.format(element[5]));
				remarks.get(i).setValue(element[6]);

				System.out.println("Select 2");

				i++;
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	/*public void sectionLink()
	{
		Window win = new SupplierInfo(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				supplierNameData();
				System.out.println("Supplier Form");

			}
		});

		this.getParent().addWindow(win);
	}

	private void deleteButtonEvent(){
		if(cmbSupplier.getValue()!=null && !txtpo.getValue().toString().isEmpty())
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

						if(deleteData(session,tx)){
							showNotification("Successfully Deleted",Notification.TYPE_HUMANIZED_MESSAGE);
							txtClear();
							tx.commit();
						}
					}
				}
			});	
		}else{
			showNotification("Nothing To Delete",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	 */
	private void  totalAmount(Event event, int ar, String flag)
	{
		try
		{
			Double orderQty = 0.0;
			Double rateValue = 0.0;

			if(flag.equals("Qty"))
			{
				orderQty =  Double.parseDouble(((TextChangeEvent) event).getText().toString().isEmpty()?"0.00":((TextChangeEvent) event).getText().toString());
				rateValue =  Double.parseDouble(rate.get(ar).getValue().toString().isEmpty()?"0.00":rate.get(ar).getValue().toString());
			}

			if(flag.equals("Rate"))
			{
				rateValue =  Double.parseDouble(((TextChangeEvent) event).getText().toString().isEmpty()?"0.00":((TextChangeEvent) event).getText().toString());
				orderQty = Double.parseDouble(OrderQty.get(ar).getValue().toString().isEmpty()?"0.00":OrderQty.get(ar).getValue().toString());
			}

			Double totalamount = orderQty*rateValue;
			amount.get(ar).setValue(decimalform.format(totalamount));	
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error !",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

}
