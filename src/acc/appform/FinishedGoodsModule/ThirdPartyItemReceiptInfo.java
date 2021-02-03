package acc.appform.FinishedGoodsModule;

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
import com.common.share.ReportViewer;
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

import database.hibernate.TbSubGroup;

public class ThirdPartyItemReceiptInfo extends Window 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SessionBean sessionBean;
	private Label lblSource;
	private Label lblPartyName;
	private Label lblAddress;
	private Label lblReceiptNo;
	private Label lblDate;
	private Label lblChallan;
	private Label lblChallanDate;
	private ComboBox cmbSource;
	private ComboBox cmbPartyName;
	private TextField txtaddress;
	private TextRead txtreceiptNo;
	private TextField txtchallanNo;
	private PopupDateField dateField;
	private PopupDateField dChallanDate;
	//private ImmediateUploadExampleNew poAttach;
	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProductParty = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> cmbProductSource = new ArrayList<ComboBox>();
	private ArrayList<TextRead> unit = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> Qty = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> rate = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> amount = new ArrayList<TextRead>();

	private HashMap productUnit=new HashMap();
	double totalsum = 0.0;
	private Formatter fmt = new Formatter();
	private TextRead txttotalField = new TextRead();

	private TextField txtReceiptId = new TextField();
	private TextRead txtTransectionNo = new TextRead();

	boolean isUpdate=false,isFind=false;
	String udFlag;
	private HashMap supplierAddress=new HashMap();

	String strFlag;
	private Label lbLine;
	//private TextField txtReceiptId=new TextField();
	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat dformate = new DecimalFormat("#0");

	
	//CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "Preview", "", "Exit");

	
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");

	
	private AbsoluteLayout mainLayout;
	/*String filePathTmpReq= "";
	String imageLoc= "0";*/
	HashMap hMap=new HashMap();

	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private HashMap hmproduct=new HashMap();
	private HashMap hmqty= new HashMap();

	private ImmediateUploadExample bpvUpload = new ImmediateUploadExample("");
	Button btnPreview;
	String imageLoc= "0";
	String filePathTmp = "";
	String bpvPdf = null;
	String tempimg="";
	private Label lblCommon;
	
	public ThirdPartyItemReceiptInfo(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("THIRD PARTY ITEM RECEIPT ::"+sessionBean.getCompany());
		setWidth("960px");
		setHeight("645px");
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		btnIni(true);
		componentIni(true);
		txtClear();
		FocusMove();
		cmbPartyNameLoad();
	}
	public void setEventAction()
	{
		cButton.btnPreview.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(isFind&&!txtreceiptNo.getValue().toString().isEmpty()){
					reportShow();
				}
				else
				{
					showNotification("Nothing To Preview",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		
		cmbPartyName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbPartyName.getValue()!=null)
				{
					addressLoad();
					cmbSourceLoad();
				}
				else{
					cmbSource.removeAllItems();
					txtaddress.setValue("");
					tableClear();
				}
			}
		});
		cmbSource.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				if( cmbSource.getValue()!=null)
				{	
					for(int i=0;i<rate.size();i++)
					{
						dataload(i);	
					}
				}

			}
		});
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				autoReciptNo();
				udFlag="New";
				isUpdate=false;
				isFind=false;
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
				else{
					showNotification("Warning","You are not permitted to save date",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
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
		
	}


	private void addressLoad(){
		try{
			String sql="select address from tbPartyInfo where partyCode "
					+ "like '"+cmbPartyName.getValue().toString()+"' and isActive like '1'";
			Iterator<?> iter = dbService(sql); 
			if(iter.hasNext())
			{
				txtaddress.setValue(iter.next());
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private void cmbSourceLoad(){
		try{
			String sql="select vSource,vSourceName from tb3rdPartylabelInformation where vPartyId "
					+ "like '"+cmbPartyName.getValue().toString()+"' and isActive like '1'";
			Iterator<?> iter = dbService(sql); 
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbSource.addItem(element[0].toString());
				cmbSource.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	public void cmbPartyNameLoad()
	{
		cmbPartyName.removeAllItems();
		try
		{
			String sql="select vPartyId,vPartyName from tb3rdPartylabelInformation where isActive like '1'";
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void dataload(int i )
	{
		try{
			cmbProductParty.get(i).removeAllItems();
			cmbProductSource.get(i).removeAllItems();
			String sql="select vLabelCode,vLabelName,vlabelNamesource from tb3rdPartylabelInformation where "
					+ "vSource like '"+cmbSource.getValue().toString()+"' "
					+ "and vPartyId like '"+cmbPartyName.getValue().toString()+"'";
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbProductParty.get(i).addItem(element[0].toString());
				cmbProductParty.get(i).setItemCaption(element[0].toString(),element[1].toString());
				cmbProductSource.get(i).addItem(element[0].toString());
				cmbProductSource.get(i).setItemCaption(element[0].toString(),element[2].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error", Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void FocusMove()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();

		allComp.add(cmbPartyName);
		allComp.add(txtaddress);
		allComp.add(cmbSource);
		allComp.add(txtchallanNo);
		allComp.add(dChallanDate);

		for(int i=0;i<cmbProductParty.size();i++)
		{
			allComp.add(cmbProductParty.get(i));
			allComp.add(cmbProductSource.get(i));
			
			allComp.add(Qty.get(i));	
			allComp.add(rate.get(i));
		}
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);

		new FocusMoveByEnter(this,allComp);
	}
	private void componentIni(boolean b) 
	{
		cmbSource.setEnabled(!b);
		cmbPartyName.setEnabled(!b);
		txtaddress.setEnabled(!b);
		txtreceiptNo.setEnabled(!b);
		dateField.setEnabled(!b);
		txtchallanNo.setEnabled(!b);
		dChallanDate.setEnabled(!b);
		lbLine.setEnabled(!b);

		btnPreview.setEnabled(!b);
		bpvUpload.setEnabled(!b);
		for(int i=0;i<cmbProductParty.size();i++){
			cmbProductParty.get(i).setEnabled(!b);
			cmbProductSource.get(i).setEnabled(!b);
			unit.get(i).setEnabled(!b);
			Qty.get(i).setEnabled(!b);
			rate.get(i).setEnabled(!b);
			amount.get(i).setEnabled(!b);
		}
	}


	public void txtClear()
	{
		try
		{	
			cmbSource.setValue(null);
			cmbPartyName.setValue(null);
			txtaddress.setValue("");
			txtreceiptNo.setValue("");
			txtchallanNo.setValue("");
			
			bpvUpload.fileName = "";
			bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
			filePathTmp = "";
			bpvUpload.actionCheck = false;
			imageLoc = "0";
			btnPreview.setEnabled(false);

			tableclear();
			//table.setColumnFooter("Amount", "Total:"+0.0);
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}

	}
	public void tableClear(){
		for(int i=0;i<unit.size();i++)
		{
			cmbProductParty.get(i).setValue(null);
			cmbProductSource.get(i).setValue(null);
			unit.get(i).setValue("");
			amount.get(i).setValue("");
			rate.get(i).setValue("");
			Qty.get(i).setValue("");
		}
	}


	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);	
		txtClear();
		cmbSource.focus();	
		isUpdate=false;
	}

	private Iterator<?> dbService(String sql){

		System.out.println(sql);
		Session session=null;
		Iterator<?> iter=null;
		try {
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		} 
		catch (Exception e) {
			showNotification(null,""+e,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		// top-level component properties
		// lblPartyName
		lblPartyName = new Label();
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		lblPartyName.setValue("Party Name :");
		mainLayout.addComponent(lblPartyName, "top:20.0px;left:20.0px;");
		// cmbPartyName
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("220px");
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setNewItemsAllowed(false);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbPartyName, "top:18.0px;left:130.0px;");

		// lblAddress
		lblAddress = new Label();
		lblAddress.setImmediate(false);
		lblAddress.setWidth("-1px");
		lblAddress.setHeight("-1px");
		lblAddress.setValue("Address :");
		mainLayout.addComponent(lblAddress, "top:50.0px;left:20.0px;");

		// txtaddress
		txtaddress = new TextField();
		txtaddress.setImmediate(true);
		txtaddress.setWidth("220px");
		txtaddress.setRows(1);
		mainLayout.addComponent(txtaddress, "top:48.0px;left:130.0px;");

		// lblPurchaseType
		lblSource = new Label();
		lblSource.setImmediate(false);
		lblSource.setWidth("-1px");
		lblSource.setHeight("-1px");
		lblSource.setValue("Source :");
		mainLayout.addComponent(lblSource, "top:100.0px;left:20.0px;");

		// cmbPurchaseType
		cmbSource = new ComboBox();
		cmbSource.setImmediate(true);
		cmbSource.setWidth("220px");
		cmbSource.setHeight("-1px");
		cmbSource.setNullSelectionAllowed(true);
		cmbSource.setNewItemsAllowed(false);
		cmbSource.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSource, "top:98.0px;left:130.0px;");

		// lblReceiptNo
		lblReceiptNo = new Label();
		lblReceiptNo.setImmediate(false);
		lblReceiptNo.setWidth("-1px");
		lblReceiptNo.setHeight("-1px");
		lblReceiptNo.setValue("Receipt No :");
		mainLayout.addComponent(lblReceiptNo, "top:20.0px;left:450.0px;");

		// txtreceiptNo
		txtreceiptNo = new TextRead(1);
		txtreceiptNo.setImmediate(true);
		txtreceiptNo.setWidth("100px");
		txtreceiptNo.setHeight("-1px");
		mainLayout.addComponent(txtreceiptNo, "top:18.0px;left:525.0px;");

		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:50.0px;left:450.0px;");

		// dateField
		dateField = new PopupDateField();
		dateField.setImmediate(true);
		dateField.setWidth("107px");
		dateField.setHeight("-1px");
		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dateField, "top:48.0px;left:525.0px;");

		// lblChallan
		lblChallan = new Label();
		lblChallan.setImmediate(false);
		lblChallan.setWidth("-1px");
		lblChallan.setHeight("-1px");
		lblChallan.setValue("Challan No :");
		mainLayout.addComponent(lblChallan, "top:20.0px;left:700.0px;");

		// txtchallanNo
		txtchallanNo = new TextField();
		txtchallanNo.setImmediate(true);
		txtchallanNo.setWidth("100px");
		txtchallanNo.setHeight("-1px");
		mainLayout.addComponent(txtchallanNo, "top:18.0px;left:790.0px;");

		// lblChallanDate
		lblChallanDate = new Label();
		lblChallanDate.setImmediate(false);
		lblChallanDate.setWidth("-1px");
		lblChallanDate.setHeight("-1px");
		lblChallanDate.setValue("Challan Date :");
		mainLayout.addComponent(lblChallanDate, "top:50.0px;left:700.0px;");

		// dChallanDate
		dChallanDate = new PopupDateField();
		dChallanDate.setImmediate(true);
		dChallanDate.setWidth("107px");
		dChallanDate.setHeight("-1px");
		dChallanDate.setDateFormat("dd-MM-yyyy");
		dChallanDate.setValue(new java.util.Date());
		dChallanDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dChallanDate, "top:48.0px;left:790.0px;");

		lblCommon = new Label("Upload:");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:700px;");
		mainLayout.addComponent(bpvUpload, "top:78.0px;left:790px;");
		// btnPreview
		
		btnPreview = new Button("Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:105.0px;left:874.0px;");
		btnPreview.setEnabled(false);
		
		/////////////////
		
		btnPreview.setVisible(false);
		bpvUpload.setVisible(false);
		lblCommon.setVisible(false);
		
		////////////////
		
		// table
		table = new Table();
		table.setWidth("99%");
		table.setHeight("350px");
		table.setFooterVisible(true);

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Product Name(Party)", ComboBox.class , new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Product Name(Party)",250);

		table.addContainerProperty("Product Name(Source)", ComboBox.class , new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Product Name(Source)",250);
		
		table.addContainerProperty("Unit", TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Unit",45);

		table.addContainerProperty("Qty", AmountCommaSeperator.class , new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Qty",72);

		table.addContainerProperty("Rate", AmountCommaSeperator.class , new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Rate",80);

		table.addContainerProperty("Amount", TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Amount",90);
		table.setColumnCollapsingAllowed(true);

		tableInitialise();
		mainLayout.addComponent(table, "top:150.0px;left:5.0px;");

		lbLine = new Label("_____________________________________________________________________________________________________________________________________________________________________________________________");
		mainLayout.addComponent(lbLine, "top:515.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:550.0px;left:213.0px;");

		return mainLayout;
	}

	public void tableInitialise(){
		for(int i=0;i<10;i++){
			tableRowAdd(i);
		}
	}

	public void ProductDataLoad(String id,int ar){
		String sql="select vUnit,mthirdPartyItemRate from tb3rdPartylabelInformation where vLabelCode like '"+id+"' ";
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			unit.get(ar).setValue(element[0]);
			rate.get(ar).setValue(df.format(element[1]));
		}
	}
	public double calculateAmount(int ar){
		double qtya=0,ratea=0,amounta=0;
		qtya=Double.parseDouble(Qty.get(ar).getValue().toString().isEmpty()?"0":Qty.get(ar).getValue().toString());
		ratea=Double.parseDouble(rate.get(ar).getValue().toString().isEmpty()?"0":rate.get(ar).getValue().toString());
		amounta=qtya*ratea;
		amount.get(ar).setValue(df.format(amounta));
		return amounta;
	}

	public void tableRowAdd(final int ar)
	{
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query="";
		System.out.println("Value of tableRowadd: "+ar);
		try{
			tbLblSl.add(ar,new Label());
			tbLblSl.get(ar).setWidth("20px");
			tbLblSl.get(ar).setValue(ar + 1);
			tbLblSl.get(ar).setImmediate(true);

			cmbProductParty.add(ar,new ComboBox());
			cmbProductParty.get(ar).setWidth("100%");
			cmbProductParty.get(ar).setImmediate(true);
			cmbProductParty.get(ar).setNullSelectionAllowed(false);
			cmbProductParty.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			cmbProductParty.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if(cmbProductParty.get(ar).getValue()!=null){
						cmbProductSource.get(ar).setValue(cmbProductParty.get(ar).getValue());
						ProductDataLoad(cmbProductParty.get(ar).getValue().toString(),ar);
					}
					else{
						cmbProductSource.get(ar).setValue(null);
					}
				}
			});

			cmbProductSource.add(ar,new ComboBox());
			cmbProductSource.get(ar).setWidth("100%");
			cmbProductSource.get(ar).setImmediate(true);
			cmbProductSource.get(ar).setNullSelectionAllowed(false);
			cmbProductSource.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			cmbProductSource.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if(cmbProductSource.get(ar).getValue()!=null){
						cmbProductParty.get(ar).setValue(cmbProductSource.get(ar).getValue());
						ProductDataLoad(cmbProductSource.get(ar).getValue().toString(),ar);
					}
					else{
						cmbProductParty.get(ar).setValue(null);
					}
				}
			});
			
			unit.add(ar,new TextRead(""));
			unit.get(ar).setWidth("100%");

			Qty.add( ar , new AmountCommaSeperator());
			Qty.get(ar).setWidth("90%");
			Qty.get(ar).setImmediate(true);

			Qty.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if(cmbProductParty.get(ar).getValue()!=null||cmbProductSource.get(ar).getValue()!=null){
						calculateAmount(ar);
						/*if((Double.parseDouble(Qty.get(ar).getValue().toString().isEmpty()?"0":Qty.get(ar).getValue().toString()))>0){
							calculateAmount(ar);
						}*/
					}
				}
			});

			rate.add(ar,new AmountCommaSeperator());
			rate.get(ar).setWidth("90%");
			rate.get(ar).setImmediate(true);
			
			rate.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if(cmbProductParty.get(ar).getValue()!=null||cmbProductSource.get(ar).getValue()!=null){
						calculateAmount(ar);
						/*if((Double.parseDouble(Qty.get(ar).getValue().toString().isEmpty()?"0":Qty.get(ar).getValue().toString()))>0){
							calculateAmount(ar);
						}*/
					}
				}
			});

			amount.add( ar , new TextRead(1));
			amount.get(ar).setWidth("90%");

			table.addItem(new Object[]{tbLblSl.get(ar),cmbProductParty.get(ar),cmbProductSource.get(ar),unit.get(ar),Qty.get(ar),rate.get(ar),amount.get(ar)},ar);
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private void tableclear()
	{
		for(int i=0;i<unit.size();i++){
			cmbProductParty.get(i).setValue(null);
			cmbProductSource.get(i).setValue(null);
			unit.get(i).setValue("");
			amount.get(i).setValue("");
			rate.get(i).setValue("");
			Qty.get(i).setValue("");
		}
	}
	private void reportShow()
	{	
		int type=1;
		String query=null;
		Transaction tx=null;
		String ReceiptNo="";
		String parentype="";
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			
			hm.put("user", sessionBean.getUserName());
			
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			
		
			if(cmbPartyName.getValue()!=null)
			{
				parentype=cmbPartyName.getValue().toString();	
			}
			query="select Info.vPartyName,Info.vPartyAddress,Info.vChallanNo,Info.dChallanDate, (select vSourceName from tbSourceInfo where info.vSource = iSourceID) vSourceName,Info.vReceiptNo," +
					"Info.dReceiptDate, details.vProductName,details.vUnit,details.mQty,details.mRate,details.mAmount " +
					"from tb3rdPartyReceiptInformation Info inner join tb3rdPartyReceiptDetails details " +
					" on Info.vReceiptNo = details.vReceiptNo " +
					"where Info.vPartyId like '"+parentype+"'  and Info.vReceiptNo like '1'";

			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				Window win = new ReportViewerNew(hm,"report/raw/rptThirdPartyItemReceipt.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else
			{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}

	

	
	private String autoReciptNo(){
		String autoId=null;
		try{
			String sql="Select isnull(max(cast(vReceiptNo as int) ) ,0)+1 id  from tb3rdPartyReceiptInformation";
			Iterator<?> iter=dbService(sql);
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


	private String autocodegenerate(String autocode, int length)
	{

		while(autocode.length()<8)
		{
			autocode="0"+autocode;
		}

		return autocode;


	}

	private void updateButtonEvent(){

		if(cmbPartyName.getValue()!= null)
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
	private boolean doubleEntryCheck(String caption,int row){

		for(int i=0;i<amount.size();i++){

			if(i!=row && caption.equals(cmbProductParty.get(i).getItemCaption(cmbProductParty.get(i).getValue()))){

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
	@SuppressWarnings("unused")
	public String PartyLedger() 
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			String query="select Ledger_Id from tbLedger where Ledger_Id=(select ledgerCode from tbPartyInfo where partyCode like '"+cmbPartyName.getValue().toString()+"')"; 
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

	/*public String productlededger(int i) 
	{
		String autoCode = "";

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query="select Ledger_Id  from tbLedger where Ledger_Id=(select vLedgerCode from tbRawItemInfo where vRawItemCode like '"+cmbProductParty.get(i).getValue().toString()+"')";
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
	}*/


	public boolean tableDataCheck(){
		boolean IsData=false;
		int row=0;
		for(int ar=0;ar<rate.size();ar++){
			if(cmbProductParty.get(ar).getValue()!=null||cmbProductSource.get(ar).getValue()!=null){
				IsData=false;
				if(Double.parseDouble(Qty.get(ar).getValue().toString().isEmpty()?"0":Qty.get(ar).getValue().toString())>0){
					IsData=true;
				}
			}
		}
		if(IsData){
			return true;
		}
		else{
			return false;
		}
	}
	/*	
	private boolean tableDataCheck(){

		for(int a=0;a<cmbProductParty.size();a++){
			if(cmbProductParty.get(a).getValue()!=null  ){
				if(Qty.get(a).getValue()==null){
					return false;
				}
			}
		}
		return true;
	}*/

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

	private void saveButtonEvent() 
	{
		if(cmbPartyName.getValue()!=null)
		{
			if(cmbSource.getValue()!=null){
				if(!txtreceiptNo.getValue().toString().trim().isEmpty())
				{
					if(!txtchallanNo.getValue().toString().trim().isEmpty())
					{
						if(tableDataCheck()){
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
											Session session = SessionFactoryUtil.getInstance().openSession();
											tx = session.beginTransaction();
											if(deleteData(session,tx))
											{
												insertData(session,tx);
											}
											else{
												tx.rollback();
											}
											isUpdate=false;
											mb.close();
										}
									}
								});	

							}
							else
							{
								final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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
											executeProcedure();
											mb.close();
										}
									}

								});	

							}
						}
						else{
							this.getParent().showNotification("Warning :","Please provide all data in table.", Notification.TYPE_WARNING_MESSAGE);
						}	
					}
					else{
						this.getParent().showNotification("Warning :","Please Enter Challan No.", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
					this.getParent().showNotification("Warning :","Please Enter Receipt No..", Notification.TYPE_WARNING_MESSAGE);
			}
			else{
				this.getParent().showNotification("Warning :","Please Select Source..", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
			this.getParent().showNotification("Warning :","Please Select Party Name.", Notification.TYPE_WARNING_MESSAGE);
	}
	public boolean deleteData(Session session,Transaction tx){
		try{
			String sqlUdinfo="insert into tbUd3rdPartyReceiptInformation  "
					+ "select iTransectionId, vReceiptNo, dReceiptDate, vChallanNo, dChallanDate, vPartyId, vPartyName, "
					+ "vPartyAddress, vUserIp, vUserId, vUserName, dEntryTime, vSource,'Update',vImagePath,vPartyLadger "
					+ "from tb3rdPartyReceiptInformation where vReceiptNo like '"+txtreceiptNo.getValue()+"' ";

			String sqlUdDetails="insert into tbUd3rdPartyReceiptDetails "
					+ "select iTransectionId, vReceiptNo, vChallanNo, vProductId, vProductName, vUnit, mQty, mRate, mAmount,'Update',vProductLadger,vlabelNamesource "
					+ " from tb3rdPartyReceiptDetails where vReceiptNo like '"+txtreceiptNo.getValue()+"'";

			String sqlinfo="delete from tb3rdPartyReceiptInformation where vReceiptNo like '"+txtreceiptNo.getValue()+"'";
			String sqlDetails="delete from tb3rdPartyReceiptDetails where vReceiptNo like '"+txtreceiptNo.getValue()+"'";

			session.createSQLQuery(sqlUdinfo).executeUpdate();
			session.createSQLQuery(sqlUdDetails).executeUpdate();
			
			session.createSQLQuery(sqlinfo).executeUpdate();
			session.createSQLQuery(sqlDetails).executeUpdate();

			return true;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
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


	private void insertData(Session session,Transaction tx) 
	{
		String PartyLedger="";
		String ReceiptNo="";
		String transectionId="",partyId="",partyIdName="";
		String sqlInfo="",sqlDetails="",sqlUdInfo="",sqlUdDetails="",  vocherId="";
		
		String productId="";
		String ProductName="",ProductNameSource="";

		//String suplierId=PartyLedger();
		//String imgPatReq = imagePatRequisition(1,txtreceiptNo.getValue().toString())==null?imageLoc:imagePatRequisition(1,txtreceiptNo.getValue().toString());
		
		String attach = imagePath(1,txtreceiptNo.getValue().toString())==null? imageLoc:imagePath(1,txtreceiptNo.getValue().toString());
		
		try
		{
			if(!isUpdate)
			{
				//////////Receipt NO
				String sqlReceipt="Select isnull(max(cast(vReceiptNo as int) ) ,0)+1 id  from tb3rdPartyReceiptInformation";
				System.out.println(sqlReceipt);
				Iterator iterReceipt = session.createSQLQuery(sqlReceipt).list().iterator();
				if(iterReceipt.hasNext())
				{
					ReceiptNo=iterReceipt.next().toString().trim();
				}
			}
			else
			{
				ReceiptNo=txtreceiptNo.getValue().toString();
			}
			//////partyLadger
			String queryLedger="select Ledger_Id from tbLedger where Ledger_Id=(select ledgerCode from tbPartyInfo where partyCode like '"+cmbPartyName.getValue().toString()+"')"; 
			System.out.println(queryLedger);
			Iterator iterLedger = session.createSQLQuery(queryLedger).list().iterator();
			if (iterLedger.hasNext()) 
			{
				PartyLedger = iterLedger.next().toString();
			}
			////////Transection 
			String queryTran="Select isnull(max(cast(iTransectionId as int)),0)+1 id  from tb3rdPartyReceiptInformation"; 
			Iterator iterTran = session.createSQLQuery(queryTran).list().iterator();
			if (iterTran.hasNext()) 
			{
				transectionId = iterTran.next().toString();
				txtTransectionNo.setValue(transectionId);
			}

			if(cmbPartyName.getValue()!=null){
				partyId=cmbPartyName.getValue().toString();
				partyIdName=cmbPartyName.getItemCaption(cmbPartyName.getValue().toString());
			}

			sqlInfo="insert into tb3rdPartyReceiptInformation (iTransectionId, vReceiptNo, dReceiptDate,"
					+ " vChallanNo, dChallanDate, vPartyId, vPartyName, vPartyAddress,  vUserIp, vUserId, vUserName, "
					+ "dEntryTime, vSource,vImagePath,vPartyLadger) "
					+ "values('"+transectionId+"','"+ReceiptNo+"','"+datef.format(dateField.getValue())+"',"
					+ " '"+txtchallanNo.getValue().toString()+"', '"+datef.format(dChallanDate.getValue())+"', "
					+ "'"+partyId+"','"+partyIdName+"', '"+txtaddress.getValue()+"', "
					+ "'"+sessionBean.getUserIp().toString()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserName()+"',"
					+ " CURRENT_TIMESTAMP,'"+cmbSource.getValue().toString()+"','"+attach+"','"+PartyLedger+"')";

			System.out.println(sqlInfo);
			session.createSQLQuery(sqlInfo).executeUpdate();
			if(isUpdate){
				sqlUdInfo="insert into tbUd3rdPartyReceiptInformation (iTransectionId, vReceiptNo, dReceiptDate,"
						+ " vChallanNo, dChallanDate, vPartyId, vPartyName, vPartyAddress,  vUserIp, vUserId, vUserName, "
						+ "dEntryTime, vSource,vUdFlag,vImagePath,vPartyLadger) "
						+ "values('"+transectionId+"','"+ReceiptNo+"','"+datef.format(dateField.getValue())+"',"
						+ " '"+txtchallanNo.getValue().toString()+"', '"+datef.format(dChallanDate.getValue())+"', "
						+ "'"+partyId+"','"+partyIdName+"', '"+txtaddress.getValue()+"', "
						+ "'"+sessionBean.getUserIp().toString()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserName()+"',"
						+ " CURRENT_TIMESTAMP,'"+cmbSource.getValue().toString()+"','New','"+attach+"','"+PartyLedger+"')";
				System.out.println(sqlUdInfo);
				session.createSQLQuery(sqlUdInfo).executeUpdate();
			}

			for (int i = 0; i < cmbProductParty.size(); i++)
			{
				Object temp = cmbProductParty.get(i).getItemCaption(cmbProductParty.get(i).getValue());
				if (cmbProductParty.get(i).getValue()!= null||cmbProductSource.get(i).getValue()!= null && !Qty.get(i).getValue().toString().isEmpty() )
				{
					
					if(cmbProductParty.get(i).getValue()!=null){
						productId =cmbProductParty.get(i).getValue().toString().trim();
						ProductName=cmbProductParty.get(i).getItemCaption(cmbProductParty.get(i).getValue().toString());
					}
					
					if(cmbProductSource.get(i).getValue()!=null){
						productId =cmbProductSource.get(i).getValue().toString().trim();
						ProductNameSource=cmbProductSource.get(i).getItemCaption(cmbProductSource.get(i).getValue().toString());
					}
					
					String ProductLedger="";

					String queryPL="select Ledger_Id  from tbLedger where Ledger_Id=(select vLedgerId from tb3rdPartylabelInformation where vLabelCode like '"+cmbProductParty.get(i).getValue().toString()+"')";
					System.out.println("ledgerpr"+queryPL);
					Iterator iterPL = session.createSQLQuery(queryPL).list().iterator();
					if (iterPL.hasNext()) 
					{
						ProductLedger = iterPL.next().toString();
					}

					sqlDetails="insert into tb3rdPartyReceiptDetails "
							+ "(iTransectionId, vReceiptNo, vChallanNo, vProductId, vProductName, vUnit, mQty, mRate, mAmount,vProductLadger,vlabelNamesource)"
							+ " values('"+transectionId+"','"+txtreceiptNo.getValue().toString()+"',"
							+ " '"+txtchallanNo.getValue().toString()+"','"+productId+"','"+ProductName+"',"
							+ "'"+unit.get(i).getValue()+"', '"+Qty.get(i).getValue()+"', '"+rate.get(i).getValue()+"',"
							+ " '"+amount.get(i).getValue()+"','"+ProductLedger+"','"+ProductNameSource+"') ";

					System.out.println(sqlDetails);
					session.createSQLQuery(sqlDetails).executeUpdate();

					if(isUpdate){
						sqlUdDetails="insert into tbUd3rdPartyReceiptDetails "
								+ "(iTransectionId, vReceiptNo, vChallanNo, vProductId, vProductName, vUnit, mQty, mRate, mAmount,vUdFlag,vProductLadger,vlabelNamesource)"
								+ " values('"+transectionId+"','"+txtreceiptNo.getValue().toString()+"',"
								+ " '"+txtchallanNo.getValue().toString()+"','"+productId+"','"+ProductName+"',"
								+ "'"+unit.get(i).getValue()+"', '"+Qty.get(i).getValue()+"', '"+rate.get(i).getValue()+"',"
								+ " '"+amount.get(i).getValue()+"','New','"+ProductLedger+"','"+ProductNameSource+"') ";
						System.out.println(sqlUdDetails);
						session.createSQLQuery(sqlUdDetails).executeUpdate();
					}
				}
			}
			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			txtClear();
			componentIni(true);
			btnIni(true);
		}
		catch(Exception exp){
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
		Window win=new ThirdPartyItemReceiptFind(sessionBean,txtReceiptId);
		win.addListener(new Window.CloseListener() {
			public void windowClose(CloseEvent e) {
				if(txtReceiptId.getValue().toString().length()>0){
					txtClear();
					findInitialise(txtReceiptId.getValue().toString());
					
					if(imageLoc.equals("0"))
					{btnPreview.setCaption("attach");
					//btnPreview.setEnabled(false);
					}
					else
					{btnPreview.setCaption("Preview");
					//btnPreview.setEnabled(true);
					}
					isFind=true;
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String receiptNo){
		try
		{
			String imgcap="";
			String sql="select a.vReceiptNo, a.dReceiptDate, a.vChallanNo, a.dChallanDate,a.vPartyId,b.vProductId,"
					+ "b.vUnit,b.mQty,b.mRate,b.mAmount,a.vSource,a.vImagePath "
					+ " from tb3rdPartyReceiptInformation a inner join tb3rdPartyReceiptDetails b "
					+ "on a.vReceiptNo=b.vReceiptNo where a.vReceiptNo like '"+receiptNo+"' ";
			Iterator<?> iter=dbService(sql);
			if(iter.hasNext()){
				Object[] element = (Object[]) iter.next();
				int i=0;
				if(i==0){
					txtreceiptNo.setValue(element[0]);
					dateField.setValue(element[1]);
					txtchallanNo.setValue(element[2]);
					dChallanDate.setValue(element[3]);
					cmbPartyName.setValue(element[4]);
					cmbSource.setValue(element[10]);
				}
				dataload(i);
				cmbProductParty.get(i).setValue(element[5]);
				unit.get(i).setValue(element[6]);
				Qty.get(i).setValue(dformate.format(element[7]));
				rate.get(i).setValue(df.format(element[8]));
				amount.get(i).setValue(df.format(element[9]));
				i++;
				
				if(!element[11].toString().equals("0")){
					imageLoc=element[11].toString();
					imgcap=element[11].toString().substring(element[11].toString().lastIndexOf("/")+1,element[11].toString().length());
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
		}
		catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
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
						//Note: Edit your folder name in below line (this line folder name is "joborder") becarefull open this folder in your tomcate / wabapps / report / open your folder 
						
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/ThirdPartyItemReceipt/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/ThirdPartyItemReceipt/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						//Note: Edit your folder name in below line (this line folder name is "joborder")  becarefull open this folder in your tomcate / wabapps / report / open your folder 
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/ThirdPartyItemReceipt/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/ThirdPartyItemReceipt/"+path+".pdf";
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

	//////////////////////////////////
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


}
