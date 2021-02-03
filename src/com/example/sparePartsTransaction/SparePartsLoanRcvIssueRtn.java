package com.example.sparePartsTransaction;



import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
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
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class SparePartsLoanRcvIssueRtn extends Window 
{

	SessionBean sessionBean;

	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");

	private FormLayout leftFrmLayout = new FormLayout();
	private FormLayout rightFrmLayout = new FormLayout();
	private HorizontalLayout hriLayout = new HorizontalLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private VerticalLayout vLayout = new VerticalLayout();
	private HorizontalLayout spaceLayout = new HorizontalLayout();
	private HorizontalLayout lowerHorizontalLayout = new HorizontalLayout();
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private Label lbLine=new Label("____________________________________________________________________________________________________________________________________");
	private Table table = new Table();
	
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> stockQty = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> rate = new ArrayList<AmountField>(1);
	private ArrayList<AmountField> qty = new ArrayList<AmountField>();
	private ArrayList<AmountField>  qtyBag= new ArrayList<AmountField>();
	private ArrayList<TextRead> amount = new ArrayList<TextRead>();
	private ArrayList<ComboBox> cmbStoreLocation=new ArrayList<ComboBox>();
	private ArrayList<TextField> remarks = new ArrayList<TextField>();
	private ArrayList<Label> lblModelNo = new ArrayList<Label>(1);
	private ArrayList<TextRead> unit = new ArrayList<TextRead>(1);
	private ArrayList<ComboBox>cmbProductType=new ArrayList<ComboBox>();
	private ArrayList<Label>lblsl=new ArrayList<Label>();
	
	
	
	private Label lblSupplier=new Label();
	private ComboBox cmbSupplier = new ComboBox();
	
	private Label lblChallanNo=new Label();
	private TextField txtchallanNo = new TextField();
	
	private Label lblAddress=new Label();
	private TextField txtaddress = new TextField();
	
	private Label lblreceiptNo=new Label();
	private TextRead txtreceiptNo  = new TextRead();
	
	private Label lblReference=new Label();
	private TextField txtreference  = new TextField();
	
	private Label lblMrrNo=new Label();
	private TextRead txtmrrNo = new TextRead();
	
	private Label lblStoreLocation=new Label();
	private ComboBox cmbstoreLocation  = new ComboBox();
	
	private Label lblReceiptDate=new Label();
	private PopupDateField dReceiptDate = new PopupDateField();
	
	private Label lblLoanType=new Label();
	OptionGroup Loantype;
	
	 TextField txtRcvId=new TextField();
	
	private AbsoluteLayout mainLayoutNew=new AbsoluteLayout();
	private static final List<String>areatype  = Arrays.asList(new String[] {"Loan Receive" ,"Loan Issue Return" });

	private static final Object[] String = null;


	private HashMap supplierAddress=new HashMap();
	private HashMap sectionAddress=new HashMap();
	private HashMap productUnit=new HashMap();
	private Formatter fmt = new Formatter();


	private Label label = new Label();
	private Label l1 = new Label();
	double totalsum = 0.0;
	boolean isUpdate=false;
	String strFlag;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	ArrayList<Component> allComp = new ArrayList<Component>();
	private DecimalFormat df=new DecimalFormat("#0.00");

	public String receiptNo;

	public SparePartsLoanRcvIssueRtn(SessionBean sessionBean) 
	{

		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("LOAN RECEIVE/RETURN :: "+sessionBean.getCompany());
		this.setContent(buildMainLayout() );
		tableInitialise();
		btnIni(true);
		componentIni(true);
		setEventAction();
		cmbSupplierAddData();
		focusEnter();
	
	}
	
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayoutNew = new AbsoluteLayout();
		mainLayoutNew.setImmediate(true);
		mainLayoutNew.setMargin(false);
		mainLayoutNew.setWidth("1220px");
		mainLayoutNew.setHeight("500px");

		//Receipt No
		lblreceiptNo = new Label();
		lblreceiptNo.setImmediate(false);
		lblreceiptNo.setWidth("-1px");
		lblreceiptNo.setHeight("-1px");
		lblreceiptNo.setValue( "Receipt No:");
		mainLayoutNew.addComponent(lblreceiptNo, "top:24.0px;left:21.0px;");
		
		txtreceiptNo = new TextRead();
		txtreceiptNo.setImmediate(false);
		txtreceiptNo.setWidth("80px");
		txtreceiptNo.setHeight("24px");
		txtreceiptNo.setImmediate(true);
		mainLayoutNew.addComponent(txtreceiptNo, "top:22.0px;left:120.0px;");
		
		//Receipt Date
		
		lblReceiptDate = new Label();
		lblReceiptDate.setImmediate(false);
		lblReceiptDate.setWidth("-1px");
		lblReceiptDate.setHeight("-1px");
		lblReceiptDate.setValue( "Receipt Date:");
		mainLayoutNew.addComponent(lblReceiptDate, "top:50.0px;left:21.0px;");
		
		dReceiptDate= new PopupDateField();
		dReceiptDate.setWidth("110px");
		dReceiptDate.setHeight("24px");
		dReceiptDate.setDateFormat("dd-MM-yyyy");
		dReceiptDate.setValue(new java.util.Date());
		dReceiptDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayoutNew.addComponent(dReceiptDate, "top:48.0px;left:120.0px;");
		
		
		//Receipt Type
		lblLoanType = new Label();
		lblLoanType.setImmediate(false);
		lblLoanType.setWidth("-1px");
		lblLoanType.setHeight("-1px");
		lblLoanType.setValue( "Receipt Type:");
		mainLayoutNew.addComponent(lblLoanType, "top:76.0px;left:21.0px;");
		
		Loantype= new OptionGroup("",areatype);
		Loantype.setImmediate(true);
		Loantype.setWidth("-1px");
		Loantype.setHeight("-1px");
		Loantype.setStyleName("horizontal");
		Loantype.select("Loan Receive");
		mainLayoutNew.addComponent(Loantype, "top:74.0px;left:120.0px;");

		//Supplier Name
		lblSupplier = new Label();
		lblSupplier.setImmediate(false);
		lblSupplier.setWidth("-1px");
		lblSupplier.setHeight("-1px");
		lblSupplier.setValue( "Received From:");
		mainLayoutNew.addComponent(lblSupplier, "top:24.0px;left:370.0px;");
		
		cmbSupplier = new ComboBox();
		cmbSupplier.setImmediate(true);
		cmbSupplier.setWidth("260px");
		cmbSupplier.setHeight("24px");
		cmbSupplier.setNullSelectionAllowed(false);
		cmbSupplier.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayoutNew.addComponent(cmbSupplier, "top:22.0px;left:470.0px;");
		
		
		
		//Challan No
		
		lblChallanNo = new Label();
		lblChallanNo.setImmediate(false);
		lblChallanNo.setWidth("-1px");
		lblChallanNo.setHeight("-1px");
		lblChallanNo.setValue( "Challan No:");
		mainLayoutNew.addComponent(lblChallanNo, "top:50.0px;left:370.0px;");
	
		
		txtchallanNo = new TextField();
		txtchallanNo.setImmediate(true);
		txtchallanNo.setWidth("140px");
		txtchallanNo.setHeight("24px");
		mainLayoutNew.addComponent(txtchallanNo, "top:48.0px;left:470.0px;");
		
		lblMrrNo = new Label();
		lblMrrNo.setImmediate(false);
		lblMrrNo.setWidth("-1px");
		lblMrrNo.setHeight("-1px");
		lblMrrNo.setValue( "MrrNo:");
		mainLayoutNew.addComponent(lblMrrNo, "top:76.0px;left:370.0px;");
	
		
		txtmrrNo = new TextRead();
		txtmrrNo.setImmediate(true);
		txtmrrNo.setWidth("140px");
		txtmrrNo.setHeight("24px");
		mainLayoutNew.addComponent(txtmrrNo, "top:74.0px;left:470.0px;");
		
		//Receipt No
		lblReference = new Label();
		lblReference.setImmediate(false);
		lblReference.setWidth("-1px");
		lblReference.setHeight("-1px");
		lblReference.setValue( "Reference No:");
		mainLayoutNew.addComponent(lblReference, "top:24.0px;left:800.0px;");
		
		txtreference = new TextField();
		txtreference.setImmediate(false);
		txtreference.setWidth("80px");
		txtreference.setHeight("24px");
		txtreference.setImmediate(true);
		mainLayoutNew.addComponent(txtreference, "top:22.0px;left:890.0px;");
		
		
		
		table.setWidth("97%");
		table.setHeight("270px");

        table.setFooterVisible(true);
        table.setFooterVisible(true);
        table.setColumnFooter("Amount", "Total : "+0);
        table.setColumnCollapsingAllowed(true);
        
        table.addContainerProperty("SL",Label.class , new Label());
		table.setColumnWidth("SL",20);
		
        table.addContainerProperty("Product Type", ComboBox.class , new ComboBox());
		table.setColumnWidth("Product Type",120);
		table.addContainerProperty("Product", ComboBox.class , new ComboBox());
		table.setColumnWidth("Product",230);
		table.addContainerProperty("Unit", TextRead.class , new TextRead());
		table.setColumnWidth("Unit",60);
		table.addContainerProperty("Model No", Label.class , new Label());
		table.setColumnWidth("Model No",60);
		table.addContainerProperty("Rate", TextField.class , new TextField());
		table.setColumnWidth("Rate",70);
		table.addContainerProperty("Qty(Bag )", AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Qty(Bag)",72);
		table.addContainerProperty("Qty", TextField.class , new TextField());
		table.setColumnWidth("Qty",80);
		table.addContainerProperty("Amount", TextRead.class , new TextRead());
		table.setColumnWidth("Amount",115);
		table.addContainerProperty("Store Location", ComboBox.class, new ComboBox());
		table.setColumnWidth("Store Location", 130);
		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",130);
		table.setColumnCollapsingAllowed(true);
		mainLayoutNew.addComponent(table,"top: 120.0px; left: 20.0px;");
	
		
		mainLayoutNew.addComponent(cButton, "top:420.0px;left:270.0px;");
		
		return  mainLayoutNew;
		
	}
	
	private void focusEnter()
	{
		allComp.add(dReceiptDate);
		allComp.add(cmbSupplier);
		allComp.add(txtchallanNo);
		allComp.add(txtreference);
		
		for(int i=0;i<cmbProductType.size();i++)
		{
			allComp.add(cmbProductType.get(i));
			allComp.add(cmbProduct.get(i));
			allComp.add(rate.get(i));
			allComp.add(qtyBag.get(i));
			allComp.add(qty.get(i));
			allComp.add(cmbStoreLocation.get(i));
			allComp.add(remarks.get(i));	
		}

		new FocusMoveByEnter(this,allComp);
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
				}
				*/
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

	
	private String Option()
	{

		strFlag="debit";
		if(strFlag.equalsIgnoreCase("debit"))
			strFlag= "RDP";
		else
			strFlag= "RCP";

		return strFlag;
	}

	private String autoReciptNo()
	{

		String autoId=null;


		Transaction tx;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select cast(isnull(max(cast(replace(substring(ReceiptNo,1,len(ReceiptNo)), '', '')as int))+1, 1) as varchar) from tbRawPurchaseInfo").list().iterator();

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

	public void cmbSupplierAddData(){

		Transaction tx;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();


			cmbSupplier.removeAllItems();
			supplierAddress.clear();
			List lst = session.createSQLQuery("select supplierId,supplierName  from tbSupplierInfo order by supplierName").list();

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



	public void cmbSectionData(){

		Transaction tx;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			cmbSupplier.removeAllItems();
			List lst = session.createSQLQuery("Select  * from tbSectionInfo").list();

			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplier.addItem(element[1].toString());
				cmbSupplier.setItemCaption(element[1].toString(), element[1].toString());
				//supplierAddress.put(element[1], element[2]);
			}



		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	
	public void cmbSectionDataForSupplier(){

		Transaction tx;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			cmbSupplier.removeAllItems();
			List lst = session.createSQLQuery("Select  * from tbSectionInfo").list();

			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplier.addItem(element[0].toString());
				cmbSupplier.setItemCaption(element[0].toString(), element[1].toString());
				sectionAddress.put(element[0].toString(), element[2]);
				System.out.println("Section Address: "+sectionAddress.get(element[0].toString()));
			}



		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	public void tableInitialise(){

		for(int i=0;i<7;i++){
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		Transaction tx;
		try{

			final Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			
			lblsl.add(ar,new Label());
			lblsl.get(ar).setWidth("100%");
			lblsl.get(ar).setImmediate(true);
			lblsl.get(ar).setValue(ar+1);
			

			cmbProductType.add(ar,new ComboBox());
			cmbProductType.get(ar).setWidth("100%");
			cmbProductType.get(ar).setImmediate(true);
			cmbProductType.get(ar).setNullSelectionAllowed(false);
			cmbProductType.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			
			List lst1 = session.createSQLQuery("select distinct 0,vCategoryType from tbRawItemInfo where vCategoryType  like '%Spare Parts%'").list();
			for (Iterator iter = lst1.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProductType.get(ar).addItem(element[1].toString());
			}
			
			cmbProductType.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
				  if(cmbProductType.get(ar).getValue()!=null)
				  {
					 /* if(!cmbProductType.get(ar).getValue().toString().equalsIgnoreCase("Spare Parts"))
						{
							table.setColumnCollapsed("Qty(Bag )", false);					
							qtyBag.get(ar).setValue(0);
						}
						else
						{
							table.setColumnCollapsed("Qty(Bag )", true);					
						}*/
					  
					  
					  cmbProduct.get(ar).removeAllItems();
					  
					  List lst2 = session.createSQLQuery(" select vRawItemCode,vRawItemName from tbRawItemInfo where vCategoryType like '"+cmbProductType.get(ar).getValue().toString()+"' ").list();
						for (Iterator iter = lst2.iterator(); iter.hasNext();)
						{
							Object[] element = (Object[]) iter.next();
							cmbProduct.get(ar).addItem(element[0].toString());
							cmbProduct.get(ar).setItemCaption(element[0].toString(), element[1].toString());
						} 
				  }
				  
				  else
				  {
					  cmbProduct.get(ar).removeAllItems();  
				  }
				  	
				}
			});

			
			cmbProduct.add(ar,new ComboBox());
			cmbProduct.get(ar).setWidth("100%");
			cmbProduct.get(ar).setImmediate(true);
			cmbProduct.get(ar).setNullSelectionAllowed(false);
			cmbProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			
			cmbProduct.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbProduct.get(ar).getValue()!=null)
					{
						boolean fla=(doubleEntryCheck(cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue()),ar));
						if (fla )
						{
							List lst=session.createSQLQuery("select distinct vUnitName,ISNULL(modelNo,'')as modelNo   from tbRawItemInfo where vRawItemCode like '"+cmbProduct.get(ar).getValue().toString()+"'").list();
							Iterator iter=lst.iterator();
							tableColumnAction(cmbProduct.get(ar).getValue().toString(),ar);
							if(iter.hasNext())
							{
								Object[]element=(Object[]) iter.next();
								unit.get(ar).setValue(element[0].toString());
								lblModelNo.get(ar).setValue(element[1].toString());
								qty.get(ar).focus();
							/*	if (cmbProductType.toString().equalsIgnoreCase("Spare Parts"))
								{
									qtyBag.get(ar).focus();
								}
								else{
									qty.get(ar).focus();
								}	*/
							}
							rate.get(ar).focus();
							if((ar+1)==rate.size() && !cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue()).isEmpty())
							{
								tableRowAdd(rate.size());
							}
						}
						else
						{	
						cmbProduct.get(ar).setValue(null);
						getParent().showNotification("Warning!!","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
						
						}	
					}
				}
			});
			

			unit.add(ar,new TextRead(""));
			unit.get(ar).setWidth("100%");
			
			lblModelNo.add(ar,new Label());
			lblModelNo.get(ar).setWidth("100%");
			lblModelNo.get(ar).setImmediate(true);


			rate.add(ar,new AmountField());
			rate.get(ar).setWidth("100%");
			rate.get(ar).setImmediate(true);
			
			rate.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					double rate1=0.00;
					double qty1=0.00;
					double amount=0.00;
					double total=0.00;
					
					rate1=Double.parseDouble(rate.get(ar).getValue().toString().isEmpty()?"0.00":rate.get(ar).getValue().toString()) ;
					qty1=Double.parseDouble(qty.get(ar).getValue().toString().isEmpty()?"0.00":qty.get(ar).getValue().toString()) ;
					amount=rate1*qty1;
					SparePartsLoanRcvIssueRtn.this.amount.get(ar).setValue(amount);
					
					for(int i=0;i<rate.size();i++)
					{
						
						if(!SparePartsLoanRcvIssueRtn.this.amount.get(i).getValue().toString().isEmpty())
						{
							total=total+Double.parseDouble(SparePartsLoanRcvIssueRtn.this.amount.get(i).getValue().toString()); 	
							
						}
					}
					
					table.setColumnFooter("Amount","Total :"+total);
				}
			});

			qty.add( ar , new AmountField());
			qty.get(ar).setWidth("100%");
			qty.get(ar).setImmediate(true);
			
			qty.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					double rate1=0.00;
					double qty1=0.00;
					double amount=0.00;
					double total=0.00;
					
					rate1=Double.parseDouble(rate.get(ar).getValue().toString().isEmpty()?"0.00":rate.get(ar).getValue().toString()) ;
					qty1=Double.parseDouble(qty.get(ar).getValue().toString().isEmpty()?"0.00":qty.get(ar).getValue().toString()) ;
					amount=rate1*qty1;
					SparePartsLoanRcvIssueRtn.this.amount.get(ar).setValue(amount);
					
					for(int i=0;i<rate.size();i++)
					{
						
						if(!SparePartsLoanRcvIssueRtn.this.amount.get(i).getValue().toString().isEmpty())
						{
							total=total+Double.parseDouble(SparePartsLoanRcvIssueRtn.this.amount.get(i).getValue().toString()); 	
						}
					}
					
					table.setColumnFooter("Amount","Total :"+total);
					
					
				}
			});
			
			qtyBag.add( ar , new AmountField());
			qtyBag.get(ar).setWidth("100%");
			qtyBag.get(ar).setImmediate(true);

			amount.add( ar , new TextRead(1));
			amount.get(ar).setWidth("100%");
			amount.get(ar).setImmediate(true);
			
			cmbStoreLocation.add(ar,new ComboBox());
			cmbStoreLocation.get(ar).setWidth("100%");
			cmbStoreLocation.get(ar).setImmediate(true);
			cmbStoreLocation.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			
			List list1=session.createSQLQuery("select  vDepoId,vDepoName from tbDepoInformation").list();
			
			for(Iterator iter=list1.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbStoreLocation.get(ar).addItem(element[0].toString().trim());
				cmbStoreLocation.get(ar).setItemCaption(element[0].toString().trim(), element[1].toString());
			}

			remarks.add( ar , new TextField());
			remarks.get(ar).setWidth("100%");
			remarks.get(ar).setImmediate(true);
			remarks.get(ar).setImmediate(true);

			table.addItem(new Object[]{lblsl.get(ar), cmbProductType.get(ar),cmbProduct.get(ar),unit.get(ar),lblModelNo.get(ar),rate.get(ar),qtyBag.get(ar),qty.get(ar),amount.get(ar),cmbStoreLocation.get(ar),remarks.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private void tableColumnAction(final String head,final int r)
	{
		qty.get(r).setImmediate(true);
		qty.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
		qty.get(r).setTextChangeTimeout(100);
		qty.get(r).addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProduct.get(0).getValue()!=null)
				{
					try
					{
						if(Double.parseDouble(qty.get(0).getValue().toString())>0 )
						{
						double tbquntity;
						double tamount,unitPrice;
						bagKgCalc(r);
						/*tbquntity = event.getProperty().toString().trim().isEmpty()? 0: Double.parseDouble(event.getProperty().toString().trim());

						String stockQ = stockQty.get(r).getValue().toString().isEmpty()?"0":stockQty.get(r).getValue().toString();

						if(Double.parseDouble(stockQ) >= tbquntity  )
						{
							bagKgCalc(r);
							String tempPrice=rate.get(r).getValue().toString().isEmpty()?"0":rate.get(r).getValue().toString();
							unitPrice=Double.parseDouble(tempPrice);
							System.out.println("Error Here.....3333");
							tamount=unitPrice*tbquntity;
							fmt = new Formatter();
							amount.get(r).setValue(decimalf.format(tamount));

							totalsum=0.0;
							for(int flag=0;flag<amount.size();flag++)
							{							
								if( !amount.get(flag).getValue().toString().isEmpty())
								{
									String flagbit = amount.get(flag).getValue().toString();
									totalsum=totalsum+Double.parseDouble(flagbit);
								}
							}


							table.setColumnFooter("Amount","Total      :"+totalsum);

						}
						else
						{
							showNotification("Warning!!","Qty must not exceed Stock Qty",Notification.TYPE_WARNING_MESSAGE);
							qty.get(r).setValue("");
						}



						cmbProduct.get(r+1).focus();  	*/
					}
						else
						{
							showNotification("Warning!!","ok qty can't be zero.",Notification.TYPE_WARNING_MESSAGE);
							qty.get(r).setValue("");
						}			
				}
					catch(Exception ex)
					{
						//getParent().showNotification("Error7"+ex);
					}
				}
			}
		});
		
		
		
		
		
		qtyBag.get(r).addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProduct.get(r).getValue()!=null && !qtyBag.get(r).getValue().toString().isEmpty())
				{
					if(Double.parseDouble(qtyBag.get(r).getValue().toString())>0 )
					{
					double bag = Double.parseDouble(qtyBag.get(r).getValue().toString()) ;
					double  kg = bag*25;
					qty.get(r).setValue(df.format(kg).toString());		
					}
					else
					{
						showNotification("Warning!!","bag qty can't be zero.",Notification.TYPE_WARNING_MESSAGE);
						qtyBag.get(r).setValue("");
						qtyBag.get(r).focus();
					}			
				}
			}
		});
	}
	
	/*private void tableColumnAction(final String head,final int r)
	{

		rate.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);

		rate.get(r).setTextChangeTimeout(200);

		//rate.get(r).focus();
		qty.get(r).addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				System.out.print("ok");
				rate.get(r).focus();
			}
		});

		rate.get(r).addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
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
							tbquntity=event.getProperty().toString().trim();

							String tempPrice=qty.get(r).getValue().toString().trim();
							//System.out.println(tempPrice);
							unitPrice=Double.parseDouble(tempPrice);
							System.out.println("column Action");
							tamount=unitPrice*(Double.parseDouble(tbquntity));
							fmt = new Formatter();
							amount.get(r).setValue(fmt.format("%.2f",tamount));

						}
						
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
												
					}

					cmbProduct.get(r+1).focus();
				}
				catch(Exception exp)
				{
					getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

	}*/
	private void bagKgCalc(int ar)
	{

		double kg = Double.parseDouble(qty.get(ar).getValue().toString().isEmpty()? "0.0":qty.get(ar).getValue().toString());
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

	public void setEventAction()
	{

		
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
				if(sessionBean.isSubmitable())
				{
					if(!isUpdate)	
					{
						autoMrrNo();
					}

					saveButtonEvent();	
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

		cButton.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				deleteButtonEvent();
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
				if(cmbSupplier.getValue()!=null){
					if(Loantype.getValue().toString().equalsIgnoreCase("Loan Receive")){
						supplierValueChange();
					}
					if(Loantype.getValue().toString().equalsIgnoreCase("Loan Issue Return")){
						sectionAddressAdd();
					}
					
				}
			}
		});

	}

	private void supplierValueChange(){	
		txtaddress.setValue("");
		txtaddress.setValue(supplierAddress.get(cmbSupplier.getValue().toString().trim()));
	}
	
	private void sectionAddressAdd(){	
		txtaddress.setValue("");
		if(sectionAddress.get(cmbSupplier.getValue().toString().trim())==null){
			txtaddress.setValue("");
		}else{
			txtaddress.setValue(sectionAddress.get(cmbSupplier.getValue().toString().trim()));
		}
		
	}

	private void findButtonEvent(){
		Window win=new FindLoanRcvIssue(sessionBean,txtRcvId,"rawLoanReceive");
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(txtRcvId.getValue().toString().length()>0)
					findInitialise(txtRcvId.getValue().toString());
			}
		});


		this. getParent(). addWindow(win);


	}
	private void findInitialise( String RcvId)
	{

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List led = session.createSQLQuery("select distinct ReceiptNo,Date,SupplierId,ChallanNo,transactionType, isnull(MrrNo,'')MrrNo, isnull(vloanRefNo,'')vloanRefNo  from tbRawPurchaseInfo where ReceiptNo like '"+RcvId+"' ").list();

			if(led.iterator().hasNext()){

				Object[] element = (Object[]) led.iterator().next();
				txtreceiptNo.setValue(element[0].toString());
				dReceiptDate.setValue(element[1]);
				cmbSupplier.setValue(element[2]);
				txtchallanNo.setValue(element[3]);
				Loantype.select(element[4].toString());
				txtmrrNo.setValue(element[5].toString());
				txtreference.setValue(element[6].toString());
			
			}
			
			List list=session.createSQLQuery("select ReceiptNo,ProductID,Qty,Rate,remarks,storeId,itemType from tbRawPurchaseDetails where ReceiptNo like '"+RcvId+"' ").list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();
				cmbProductType.get(i).setValue(element[6].toString());
				cmbProduct.get(i).setValue(element[1].toString());
				rate.get(i).setValue( df.format(element[3]));
				qty.get(i).setValue(df.format(element[2]));
				cmbStoreLocation.get(i).setValue(element[5].toString());
				remarks.get(i).setValue(element[4]);
				
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

	private void refreshButtonEvent() 
	{

		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void deleteButtonEvent(){


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
							getParent().showNotification("All information deleted Successfully");
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
	}

	private void updateButtonEvent(){

		if(cmbSupplier.getValue()!= null)
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
			this.getParent().showNotification(
					"Update Failed",
					"There are no data for update.",
					Notification.TYPE_WARNING_MESSAGE);
	}


	private void saveButtonEvent()
	{

		
			if(cmbSupplier.getValue()!=null){
				if(!txtchallanNo.getValue().toString().trim().isEmpty())
				{
					if(productcheck())
					{
						if(ratecheck())
						{
							if(qtycheck())
							{
								if(isUpdate)
								{
									this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
											new YesNoDialog.Callback() {
										public void onDialogResult(boolean yes) {
											if(yes){
												Transaction tx=null;
												Session session = SessionFactoryUtil.getInstance().getCurrentSession();
												tx = session.beginTransaction();
												if(deleteData(session,tx))
													insertData(session,tx);
												else
												{
													tx.rollback();
												}
												isUpdate=false;
											}
										}
									}));
								}else{
									this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
											new YesNoDialog.Callback() {
										public void onDialogResult(boolean yes) {
											if(yes){
												Transaction tx=null;
												Session session = SessionFactoryUtil.getInstance().getCurrentSession();
												tx = session.beginTransaction();
												insertData(session,tx);
											}
										}
									}));
								}		
							}
							else
							{
								this.getParent().showNotification("Warning !!","Please provide qty", Notification.TYPE_WARNING_MESSAGE);		
							}
							
						}
						else
						{
							this.getParent().showNotification("Warning !!","Please provide rate", Notification.TYPE_WARNING_MESSAGE);	
						}
				
					}
					else
					{
						this.getParent().showNotification("Warning !!","Please select product", Notification.TYPE_WARNING_MESSAGE);	
					}
								
						
					
				}
				else
					this.getParent().showNotification("Warning !!","Please enter challan no..", Notification.TYPE_WARNING_MESSAGE);

			}
			else
				this.getParent().showNotification("Warning !!","Please select received from.", Notification.TYPE_WARNING_MESSAGE);

		
		
	/*	else{
			if(cmbSupplier.getValue()!=null){
				if(!txtchallanNo.getValue().toString().trim().isEmpty()){
					if(cmbstoreLocation.getValue()!=null){
							if(!txttotalField.getValue().toString().trim().isEmpty()){

								if(isUpdate)
								{
									this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
											new YesNoDialog.Callback() {
										public void onDialogResult(boolean yes) {
											if(yes){
												Transaction tx=null;
												Session session = SessionFactoryUtil.getInstance().getCurrentSession();
												tx = session.beginTransaction();
												if(deleteData(session,tx))
													insertData(session,tx);
												else{
													tx.rollback();
												}
												isUpdate=false;
											}
										}
									}));
								}else{
									this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
											new YesNoDialog.Callback() {
										public void onDialogResult(boolean yes) {
											if(yes){
												Transaction tx=null;
												Session session = SessionFactoryUtil.getInstance().getCurrentSession();
												tx = session.beginTransaction();
												insertData(session,tx);
											}
										}
									}));
								}
							}
							else
								this.getParent().showNotification("Warning :","Please Select Product .", Notification.TYPE_WARNING_MESSAGE);	
					}
					else
						this.getParent().showNotification("Warning :","Please Select Location.", Notification.TYPE_WARNING_MESSAGE);
				}
				else
					this.getParent().showNotification("Warning :","Please Enter Challen No..", Notification.TYPE_WARNING_MESSAGE);

			}
			else
				this.getParent().showNotification("Warning :","Please Select Supplier Name.", Notification.TYPE_WARNING_MESSAGE);

		}*/


	}

	public boolean deleteData(Session session,Transaction tx)
	{


		try{
			
			session.createSQLQuery("delete tbRawPurchaseInfo  where ReceiptNo='"+txtreceiptNo.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete tbRawPurchaseDetails  where ReceiptNo='"+txtreceiptNo.getValue()+"'").executeUpdate();
			
			return true;

		}
		catch(Exception exp){

			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	
	
	private boolean productcheck()
	{
		for(int i=0;i<qty.size();i++)
		{
		  if(cmbProduct.get(i).getValue()!=null)
		  {
			  return true;  
		  }
		}
		
		return false;
	}
	
	
	
	private boolean ratecheck()
	{
		for(int i=0;i<qty.size();i++)
		{
		  if(!rate.get(i).getValue().toString().isEmpty())
		  {
			  return true;  
		  }
		}
		
		return false;
	}
	
	private boolean qtycheck()
	{
		for(int i=0;i<qty.size();i++)
		{
		  if(!qty.get(i).getValue().toString().isEmpty())
		  {
			  return true;  
		  }
		}
		
		return false;
	}
	
	
	

	/*public String vocherIdGenerate(){

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


	}*/
	public String vocherIdGenerate()
	{
		String vo_id = null;
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dReceiptDate.getValue())+"')").list().iterator().next().toString();
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
	private void insertData(Session session,Transaction tx) 
	{
		String voucharType = "";
		String vocherId=vocherIdGenerate();
		System.out.println(vocherId);
		try
		{
			voucharType = Option();
			StringTokenizer st=new StringTokenizer(table.getColumnFooter("Amount"),":");
			String []a= new String[2];
			int count=st.countTokens();
			int i=0;
			while(st.hasMoreTokens())
			{
				System.out.println("Rabiul Hansa Bahar");
				a[i]=st.nextToken();
				i++;	
			}
			
			double total= Double.parseDouble(a[1].toString()) ;
			
			System.out.println("Amount Is :"+total);
			
			
			
			String sql ="insert into tbRawPurchaseInfo(ReceiptNo,Date,SupplierId,TotalAmount,ChallanNo,VoucherNo,VoucherType,StoreLocation,username,userIp,entryDate,purchaseType,transactionType,MrrType,MrrNo,flag ,vloanRefNo)" +
					    " values('"+txtreceiptNo.getValue().toString()+"','"+dateformat.format(dReceiptDate.getValue())+"', " +
					    " '"+cmbSupplier.getValue()+"','"+total+"','"+txtchallanNo.getValue().toString()+"',''," +
					    " '"+voucharType+"','"+cmbStoreLocation.get(i).getValue()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate(),'"+Loantype.getValue().toString()+"','"+Loantype.getValue().toString()+"','','"+txtmrrNo.getValue().toString()+"','0','"+txtreference.getValue().toString()+"') ";
			session.createSQLQuery(sql).executeUpdate();
			
			   
				for (int i1 = 0; i1 < cmbProduct.size(); i1++)
				{
					
					
					if(cmbProduct.get(i1).getValue()!=null && ! rate.get(i1).getValue().toString().isEmpty() && ! qty.get(i1).getValue().toString().isEmpty())
					{
						String sql1= " insert into tbRawPurchaseDetails(ReceiptNo,ProductID,Qty,Rate,storeId,remarks,itemType)" +
								    " values('"+txtreceiptNo.getValue().toString()+"','"+cmbProduct.get(i1).getValue()+"','"+qty.get(i1).getValue().toString()+"', " +
								    " '"+rate.get(i1).getValue().toString()+"','"+cmbStoreLocation.get(i1).getValue()+"','"+remarks.get(i1).getValue().toString()+"','"+cmbProductType.get(i1).getValue()+"') ";
						
						session.createSQLQuery(sql1).executeUpdate();
					}
					
				}
	
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			txtClear();
			componentIni(true);
			btnIni(true);
			isUpdate=false;

		}catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);

		}
	}

	private void newButtonEvent() 
	{
		table.setColumnCollapsed("Qty(Bag )",false);
		isUpdate=false;
		txtClear();
		componentIni(false);
		btnIni(false);
		autoReciptNo();
		cmbSupplier.focus();	
	}

	public void txtClear()
	{

		try
		{
			
			txtreceiptNo.setValue("");
			dReceiptDate.setValue(new java.util.Date());
			Loantype.select("Loan Receive");
			cmbSupplier.setValue(null);
			txtchallanNo.setValue("");
			txtmrrNo.setValue("");
			txtreceiptNo.setValue("");
			txtreference.setValue("");
		
			for(int i=0;i<unit.size();i++)
			{
				cmbProductType.get(i).setValue(null);
				cmbProduct.get(i).setValue(null);
				unit.get(i).setValue("");
				lblModelNo.get(i).setValue("");
				rate.get(i).setValue("");
				qty.get(i).setValue("");
				qtyBag.get(i).setValue("");
				amount.get(i).setValue("");
				cmbStoreLocation.get(i).setValue(null);
				remarks.get(i).setValue("");
			}

			
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void componentIni(boolean b)
	{
      txtreceiptNo.setEnabled(!b);
      dReceiptDate.setEnabled(!b);
      Loantype.setEnabled(!b);
      cmbSupplier.setEnabled(!b);
      txtchallanNo.setEnabled(!b);
      txtmrrNo.setEnabled(!b);
      txtreference.setEnabled(!b);
      
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
	}

}
