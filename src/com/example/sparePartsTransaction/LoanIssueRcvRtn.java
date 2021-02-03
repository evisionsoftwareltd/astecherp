package com.example.sparePartsTransaction;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javassist.expr.Instanceof;

import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.lowagie.text.xml.xmp.DublinCoreSchema;
import com.vaadin.data.Buffered;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
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

public class LoanIssueRcvRtn extends Window {

	SessionBean sessionBean;

	private AbsoluteLayout mainLayout;

	private Label lblissueType;
	private Label lblissueto;
	private Label lblDate;
	private Label lblIssueNo;
	private Label lblChallanNo;
	private Label lblvoucherNo;
	
	private Label lblReferenceNo;

	OptionGroup Loantype;
	private ComboBox cmbissueTo;
	private TextRead issueNo;
	private TextField challanNo;
	private TextField txtreferenceNo;
	private PopupDateField dateField;

	private Table table = new Table(); 
	private TextField txtReceiptId=new TextField();
	private TextRead txtvoucherNo= new TextRead();
	//private TextRead totalField = new TextRead(1);

	private Label lbLine=new Label("____________________________________________________________________________________________________________________________________________________________________");

	private HashMap hRate = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();

	double totalsum = 0.0;

	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat decimalf = new DecimalFormat("#0.00");
	private SimpleDateFormat dateformatNew = new SimpleDateFormat("yyyy-MM-dd");

	private Label amountWordLabel = new Label("Amount In Words: ");
	private Label totalLabel ;
	private TextField amountWordsField = new TextField();

	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> stockQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> rate = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> qty = new ArrayList<AmountField>();
	private ArrayList<AmountField>  qtyBag= new ArrayList<AmountField>();
	private ArrayList<TextRead> amount = new ArrayList<TextRead>();
	private ArrayList<ComboBox> cmbStoreLocation=new ArrayList<ComboBox>();
	private ArrayList<TextField> remarks = new ArrayList<TextField>();
	private ArrayList<Label> lblModelNo = new ArrayList<Label>(1);
	private ArrayList<TextRead> txtunit = new ArrayList<TextRead>(1);
	private ArrayList<ComboBox>cmbProductType=new ArrayList<ComboBox>();
	private ArrayList<Label>lblsl=new ArrayList<Label>();

	private Label label = new Label();
	private Label l1 = new Label();
	private Label l2 = new Label();
	DecimalFormat df=new DecimalFormat("#0.00");
	boolean isUpdate=false;
	boolean isFind=false;
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Component> allComp = new ArrayList<Component>();
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	private static final List<String>areatype  = Arrays.asList(new String[] {"Loan Issue","Loan Receive Return" });

	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");

	public LoanIssueRcvRtn(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("Loan Issue/Receive Return::"+sessionBean.getCompany());
		setContent(buildMainLayout());
		tableInitialise();
		btnIni(true);
		componentIni(true);
		setEventAction();
		cmbsupplierData();
		focusMoveByEnter();
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1220px");
		mainLayout.setHeight("475px");

		lblissueType = new Label();
		lblissueType.setImmediate(false);
		lblissueType.setWidth("-1px");
		lblissueType.setHeight("-1px");
		lblissueType.setValue( "Issue Type :");
		mainLayout.addComponent(lblissueType, "top:24.0px;left:21.0px;");

		Loantype= new OptionGroup("",areatype);
		Loantype.setImmediate(true);
		Loantype.setWidth("-1px");
		Loantype.setHeight("-1px");
		Loantype.setStyleName("horizontal");
		Loantype.select("Loan Issue");
		mainLayout.addComponent(Loantype, "top:22.0px;left:85.0px;");

		lblissueto = new Label();
		lblissueto.setImmediate(false);
		lblissueto.setWidth("-1px");
		lblissueto.setHeight("-1px");
		lblissueto.setValue( "Issue To :");
		mainLayout.addComponent(lblissueto, "top:49.0px;left:21.0px;");
		lblissueto.setContentMode(label.CONTENT_XHTML);

		cmbissueTo = new ComboBox();
		cmbissueTo.setImmediate(true);
		cmbissueTo.setWidth("260px");
		cmbissueTo.setHeight("24px");
		cmbissueTo.setNullSelectionAllowed(false);
		cmbissueTo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbissueTo, "top:47.0px;left:85.0px;");

		lblDate = new Label();
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue( "Date :");
		mainLayout.addComponent(lblDate, "top:24.0px;left:400.0px;");

		dateField= new PopupDateField();
		dateField.setWidth("110px");
		dateField.setHeight("24px");
		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dateField, "top:22.0px;left:470.0px;");


		lblIssueNo = new Label();
		lblIssueNo.setImmediate(false);
		lblIssueNo.setWidth("-1px");
		lblIssueNo.setHeight("-1px");
		lblIssueNo.setValue( "Issue No :");
		mainLayout.addComponent(lblIssueNo, "top:49.0px;left:400.0px;");

		issueNo = new TextRead();
		issueNo.setImmediate(false);
		issueNo.setWidth("80px");
		issueNo.setHeight("24px");
		issueNo.setImmediate(true);
		mainLayout.addComponent(issueNo, "top:48.0px;left:470.0px;");
		
		lblReferenceNo = new Label();
		lblReferenceNo.setImmediate(false);
		lblReferenceNo.setWidth("-1px");
		lblReferenceNo.setHeight("-1px");
		lblReferenceNo.setValue( "Reference  No :");
		mainLayout.addComponent(lblReferenceNo, "top:24.0px;left:610.0px;");

		txtreferenceNo = new TextField();
		txtreferenceNo.setImmediate(false);
		txtreferenceNo.setWidth("120px");
		txtreferenceNo.setHeight("24px");
		txtreferenceNo.setImmediate(true);
		mainLayout.addComponent(txtreferenceNo, "top:22.0px;left:710px;");

		lblChallanNo = new Label();
		lblChallanNo.setImmediate(false);
		lblChallanNo.setWidth("-1px");
		lblChallanNo.setHeight("-1px");
		lblChallanNo.setValue( "Challan No :");
		mainLayout.addComponent(lblChallanNo, "top:49.0px;left:610.0px;");

		challanNo = new TextField();
		challanNo.setImmediate(false);
		challanNo.setWidth("120px");
		challanNo.setHeight("24px");
		challanNo.setImmediate(true);
		mainLayout.addComponent(challanNo, "top:48.0px;left:710px;");
		
		lblvoucherNo = new Label();
		lblvoucherNo.setImmediate(false);
		lblvoucherNo.setWidth("-1px");
		lblvoucherNo.setHeight("-1px");
		lblvoucherNo.setValue( "Voucher No :");
		mainLayout.addComponent(lblvoucherNo, "top:49.0px;left:850.0px;");

		txtvoucherNo = new  TextRead();
		txtvoucherNo.setImmediate(false);
		txtvoucherNo.setWidth("120px");
		txtvoucherNo.setHeight("24px");
		txtvoucherNo.setImmediate(true);
		mainLayout.addComponent(txtvoucherNo, "top:48.0px;left:930px;");

		table.setWidth("97%");
		table.setHeight("275px");

		table.setFooterVisible(true);
		table.setFooterVisible(true);
		table.setColumnFooter("Amount", "Total : "+0);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",40);
		table.addContainerProperty("Product Type", ComboBox.class , new ComboBox());
		table.setColumnWidth("Product Type",110);
		table.addContainerProperty("Product", ComboBox.class , new ComboBox());
		table.setColumnWidth("Product",230);
		table.addContainerProperty("Unit", TextRead.class , new TextRead());
		table.setColumnWidth("Unit",60);
		table.addContainerProperty("Model No", Label.class , new Label());
		table.setColumnWidth("Model No",60);
		table.addContainerProperty("Stock Qty", TextRead.class , new TextRead());
		table.setColumnWidth("Stock Qty",75);
		table.addContainerProperty("Rate", TextRead.class , new TextRead());
		table.setColumnWidth("Rate",70);
		table.addContainerProperty("Qty(Bag )", AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Qty(Bag)",72);
		table.addContainerProperty("Qty", TextField.class , new TextField());
		table.setColumnWidth("Qty",80);
		/*table.addContainerProperty("Qty(Bag )", AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Qty(Bag)",72);*/
		table.addContainerProperty("Amount", TextRead.class , new TextRead());
		table.setColumnWidth("Amount",113);
		table.addContainerProperty("Store Location", ComboBox.class, new ComboBox());
		table.setColumnWidth("Store Location", 110);
		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",70);
		table.setColumnCollapsingAllowed(true);
		mainLayout.addComponent(table,"top: 120.0px; left: 20.0px;");


		mainLayout.addComponent(cButton, "top:421.0px;left:220.0px;");

		return mainLayout;

	}


	private void focusMoveByEnter()
	{
		allComp.add(cmbissueTo);
		allComp.add(dateField);
		allComp.add(txtreferenceNo);
		allComp.add(challanNo);
		
		for(int i=0;i<cmbProductType.size();i++)
		{
			allComp.add(cmbProductType.get(i));
			allComp.add(cmbProduct.get(i));
			allComp.add(qty.get(i));
			allComp.add(qtyBag.get(i));
			allComp.add(cmbStoreLocation.get(i));
			allComp.add(remarks.get(i));	
		}

		new FocusMoveByEnter(this,allComp);
	}

	private void tableinitialise()
	{
		for(int i=0; i<10; i++)
		{
			tableRowAdd(i);
		}
	}

	public List dbService(String sql){
		List list = null;
		Transaction tx = null;
		Session session = null;
		try
		{
			session = SessionFactoryUtil.getInstance().openSession();
			tx = session.beginTransaction();
			list=session.createSQLQuery(sql).list();
			return list;
		}
		catch(Exception exp){

		}
		finally{
			if(tx!=null){
				session.close();
			}
		}
		return list;
	}
	public void setEventAction(){

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 

			{
				try 
				{
					isUpdate=false;
					newButtonEvent();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
				autoIssueNo();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbissueTo.getValue()!=null)
				{
					if(!challanNo.getValue().toString().isEmpty())
					{
						if(productCheck())
						{
							if(qtycheck())
							{
								if(amountcheck())
								{
									saveButtonEvent();		
								}
								else
								{
									showNotification("Warning!!","Please enter valid qty",Notification.TYPE_WARNING_MESSAGE);	
								}
							}
							else
							{
								showNotification("Warning!!","Please provide desired qty",Notification.TYPE_WARNING_MESSAGE);		
							}

						}
						else
						{
							showNotification("Warning!!","Please select product name",Notification.TYPE_WARNING_MESSAGE);	
						}

					}
					else
					{
						showNotification("Warning!!","Please select challan no",Notification.TYPE_WARNING_MESSAGE);	
					}

				}
				else
				{
					showNotification("Warning!!","Select section name",Notification.TYPE_WARNING_MESSAGE);
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
		
		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cButton.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//deleteButtonEvent();
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
				findButtonEvent();
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!issueNo.getValue().toString().isEmpty())

				{
					ReportView();
				}

				else
				{
					showNotification("Warning!","Find a Issue No to genarate Challan",Notification.TYPE_WARNING_MESSAGE);	
				}
			}
		});

	}

	private void cmbsupplierData() 
	{  
		String quer="select distinct  supplierId,supplierName from tbSupplierInfo";

		List lst = dbService(quer);

		System.out.println("try again");
		for(  Iterator iter=lst.iterator();iter.hasNext(); )
		{
			Object [] element = (Object []) iter.next();
			cmbissueTo.addItem(element[0].toString());
			cmbissueTo.setItemCaption(element[0].toString(), element[1].toString());  
		}

	}
	private void ReportView()
	{

		String IssueNo;
		IssueNo=issueNo.getValue().toString().trim();
		System.out.print(IssueNo);

		String query=null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("comName", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("reportName","RAW MATERIAL RETURN REGISTER");
			hm.put("userName", sessionBean.getUserName()+" "+sessionBean.getUserIp());	
			query = "select rc.SectionName , rc.Address,rp.Unit, CONVERT(date,rd.Date,105 )as 'Date', rd.IssueNo, rd.IssuedTo ,rd.IssueType, rs.ProductID ,rp.ProductName,  rs.Qty,rs.remarks,rd.challanNo from tbRawIssueInfo rd  inner join  tbRawIssueDetails rs on rd.IssueNo=rs.IssueNo  inner join tbRawProductInfo rp on rp.ProductCode=rs.ProductID inner join tbSectionInfo rc on rc.AutoID=rd.IssuedTo where rd.IssueNo='"+issueNo.getValue().toString().trim()+"' ";
			System.out.println(query);
			hm.put("sql", query);

			Window win = new ReportViewer(hm,"report/rptChallan.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

			win.setCaption("PROJECT REPORT :: "+sessionBean.getCompany());
			win.setResizable(false);
			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);

		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);

		}
		/*		}
		else{

			this.getParent().showNotification("Warning","Please Select vaild Date.",Notification.TYPE_WARNING_MESSAGE);
		}*/
	}







	private String autoIssueNo() 
	{
		String autoCode = "";
		String query = "select  ISNULL(MAX(cast(IssueNo as int)),0)+1   from tbRawIssueInfo ";

		Iterator iter = dbService(query).iterator();

		if (iter.hasNext()) 
		{
			autoCode = iter.next().toString();
		}
		return autoCode;
	}



	private void findButtonEvent()
	{

		Window win=new FindLoanIssueRcv(sessionBean,txtReceiptId,"spareIssueLoan");

		this.getParent().addWindow(win);

		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) {
				if(txtReceiptId.getValue().toString().length()>0)
					findInitialise();
			}
		});


	}

	private void findInitialise(){

		Transaction tx = null;
		Session session=null;
		try
		{
			session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();


			txtClear();

			List led = dbService("select IssueNo,issuedTo,Date,IssueType,challanNo,VoucherNo ,isnull(vloanRefNo,'')vloanRefNo  from tbRawIssueInfo where IssueNo='"+txtReceiptId.getValue().toString().trim()+"'");

			if(led.iterator().hasNext())
			{

				Object[] element = (Object[]) led.iterator().next();



				System.out.println("value is"+element[1]);

				issueNo.setValue(element[0]);
				cmbissueTo.setValue(element[1]);
				dateField.setValue(element[2]);
				Loantype.setValue(element[3]);
				if(element[4]!=null)
				{
					challanNo.setValue(element[4]);
				}

				else
				{

					challanNo.setValue("");
				}
				txtvoucherNo.setValue(element[5].toString());
				txtreferenceNo.setValue(element[6].toString());

			}


			List list=dbService("select  productType ,productId,Qty,Rate,storeId,remarks from tbRawIssueDetails where IssueNo like '"+txtReceiptId.getValue().toString().trim()+"'");

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{


				Object[] element=(Object[]) iter.next();
				System.out.println("Done");
				fmt = new Formatter();
				cmbProductType.get(i).setValue(element[0].toString().trim());
				cmbProduct.get(i).setValue(element[1].toString());
				qty.get(i).setValue(decimalf.format(element[2]));
				rate.get(i).setValue(decimalf.format(element[3]));
				cmbStoreLocation.get(i).setValue(element[4]);
				remarks.get(i).setValue(element[5].toString());

				i++;
			}
		}catch(Exception exp)
		{
			this.getParent().showNotification(
					"Error2",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}

	}


	private void refreshButtonEvent() {
		txtClear();
		componentIni(true);
		btnIni(true);
	
	}


	private boolean deleteGodwonData(String itemId,Session session,Transaction tx){

		try{
			session.createSQLQuery("delete tbRawGodownStockReport  where ItemCode='"+itemId+"'").executeUpdate();

			return true;

		}
		catch(Exception exp){

			this.getParent().showNotification("Error3",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}

	}
	
	public boolean deleteData()
	{
         Transaction tx=null;
         Session session=SessionFactoryUtil.getInstance().getCurrentSession();

		try
		{
			tx=session.beginTransaction();
			System.out.println("Not Done");
			
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dateField.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			
			session.createSQLQuery(" delete from tbRawIssueInfo where IssueNo like '"+issueNo.getValue()+"' ").executeUpdate();
			session.createSQLQuery("delete from tbRawIssueDetails where IssueNo like '"+issueNo.getValue()+"' ").executeUpdate();
			session.createSQLQuery("delete  from "+voucher+"  where Voucher_No like '"+txtvoucherNo.getValue().toString()+"' ").executeUpdate();
			tx.commit();
			return true;

		}
		catch(Exception exp)
		{

			this.getParent().showNotification("Error4",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			return false;
		}


	}

	private void updateButtonEvent(){

		if(cmbissueTo.getValue()!= null)
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
		}
		else
			this.getParent().showNotification
			(
					"Update Failed",
					"There are no data for update.",
					Notification.TYPE_WARNING_MESSAGE);
	}
	
	public String vocherIdGenerate()
	{

		String vo_id = null;
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dateField.getValue())+"')").list().iterator().next().toString();
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


	private void saveButtonEvent() 
	{
		if(!isUpdate)
		{
			String vocherId="";
			vocherId=vocherIdGenerate();
			txtvoucherNo.setValue(vocherId);
		}
		
	/*	String vocherId="";
		vocherId=vocherIdGenerate();
		txtvoucherNo.setValue(vocherId);*/
			

		if(!issueNo.getValue().toString().trim().isEmpty())
		{

			if(isUpdate)
			{
				this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
						new YesNoDialog.Callback() {
					public void onDialogResult(boolean yes) {
						if(yes)
						{
							
							if(deleteData())
								insertData();
							
							isUpdate=false;
						}
					}
				}));
			}
			else
			{
				this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
						new YesNoDialog.Callback() {
					public void onDialogResult(boolean yes) {
						if(yes)
						{
							insertData();	

						}
					}
				}));
			}
		}
		else
			this.getParent().showNotification("Warning !!","Please Select Product .", Notification.TYPE_WARNING_MESSAGE);	
	}


	private boolean productCheck()
	{
		for(int i=0;i<rate.size();i++)
		{
			if(cmbProduct.get(i).getValue()!=null)
			{
				return  true;	
			}
		}
		return false;

	}

	private boolean qtycheck()
	{
		for(int i=0;i<rate.size();i++)
		{
			if(!qty.get(i).getValue().toString().isEmpty())
			{
				return  true;	
			}
		}

		return false;

	}

	private boolean amountcheck()
	{
		for(int i=0;i<rate.size();i++)
		{
			if(!amount.get(i).getValue().toString().isEmpty())
			{
				return  true;	
			}
		}


		return false;

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

			
				query="select Ledger_Id from tbLedger where Ledger_Id=(select ledgerCode from tbSupplierInfo where supplierId like '"+cmbissueTo.getValue().toString()+"')"; 

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



	private void  insertData()
	{
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		try
		{
			String sql= null;
			String  IssueType=Loantype.getValue().toString();
			String IssueNo=issueNo.getValue().toString();
			String ChallanNo=challanNo.getValue().toString();
			String IssueTo=cmbissueTo.getValue().toString();

			StringTokenizer st=new StringTokenizer(table.getColumnFooter("Amount"),":");
			String s1=st.nextToken();
			String s2=st.nextToken();
			Double totalAmount=Double.parseDouble(s2);

			String date=dateformatNew.format(dateField.getValue());
			
			String voucherNo="";
			String SupplierLedger="";
			SupplierLedger=supplierLedger();
			
			if(totalsum<=0)
			{
			  txtvoucherNo.setValue("");	
			}


			sql="insert  into tbRawIssueInfo(IssueNo,IssuedTo,Date,TotalAmount,ProductionType,productionStep,finishedGoods,challanNo," +
					"UserId,userIp,EntryTime,IssueType,VoucherNo,VoucherType,IssueRef,isActive,vloanRefNo) " 
					+"values('"+IssueNo+"','"+IssueTo+"','"+date+"' ,'"+totalAmount+"','','','','"+ChallanNo+"','"+sessionBean.getUserId()+"',"
					+" '"+sessionBean.getUserIp()+"',getdate(),'"+IssueType+"' ,'"+txtvoucherNo.getValue().toString()+"','','',0,'"+txtreferenceNo.getValue().toString()+"')";

			System.out.println(sql);
			session.createSQLQuery(sql).executeUpdate();
			
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dateField.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			

			System.out.println("Receipt Data"+voucher);

			String naration="Supplier :"+cmbissueTo.getItemCaption(cmbissueTo.getValue()).toString()+" "+"Ref No :"+issueNo.getValue().toString()+" "+"Issue Date :"+new SimpleDateFormat("yyyy-MM-dd").format(dateField.getValue()).toString();

			
			

			for(int i=0;i<qty.size();i++)
			{

				if(cmbProduct.get(i).getValue()!=null && !qty.get(i).getValue().toString().isEmpty() && !amount.get(i).getValue().toString().isEmpty())
				{
					String  productId=cmbProduct.get(i).getValue().toString();
					String  productType=cmbProductType.get(i).getValue().toString();
					String StoreLocation="";
					if(cmbStoreLocation.get(i).getValue()!=null)
					{
						StoreLocation=cmbStoreLocation.get(i).getValue().toString();  
					}
					String rmks=remarks.get(i).getValue().toString();



					String query=  "insert into tbRawIssueDetails(IssueNo,ProductID,Qty,Rate,storeId,margeFrom,isActive,productType,remarks,returnFlag)"
							+" values('"+IssueNo+"','"+productId+"','"+qty.get(i).getValue().toString()+"','"+rate.get(i).getValue().toString()+"'," +
							" '"+StoreLocation+"','',0,'"+productType+"','"+rmks+"','1')";

					System.out.println(query);
					session.createSQLQuery(query).executeUpdate();
					
					if(totalsum>0)
					{
						if(i==0)
						{
							String SupplierVoucherquery=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
									+" values('"+txtvoucherNo.getValue().toString()+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dateField.getValue())+"','"+SupplierLedger+"', "  
									+" '"+naration+"','"+totalsum+"' , "
									+" '0','jau','U-1','1' ,'"+sessionBean.getUserId()+"', "
									+" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "
									+" '2', '"+sessionBean.getCompanyId()+"','' ,'"+cmbissueTo.getItemCaption(cmbissueTo.getValue())+"','loanIssueRcvRtn') ";

							session.createSQLQuery(SupplierVoucherquery).executeUpdate();
							
						}	
					}
					
					
					
					
					String proid =cmbProduct.get(i).getValue().toString().trim();

					String ProductLedeger="";
					ProductLedeger= productlededger(i);
					
					if(totalsum>0)
					{

						String purchasevoucherquery=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
								+" values('"+txtvoucherNo+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dateField.getValue())+"','"+ProductLedeger+"', "  
								+" '"+naration+"','0' , "
								+" '"+amount.get(i).getValue()+"','jau','U-1','1' ,'"+sessionBean.getUserId()+"', "
								+" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "
								+" '2', '"+sessionBean.getCompanyId()+"' ,'','"+cmbissueTo.getItemCaption(cmbissueTo.getValue())+"','loanIssueRcvRtn') ";

						session.createSQLQuery(purchasevoucherquery).executeUpdate();
						System.out.println("purchae"+purchasevoucherquery);	
					}


				}
				
			}
			showNotification("All information saved successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			tx.commit();
			txtClear();
			componentIni(true);
			btnIni(true);
			isUpdate=false;
			isFind=false;

		}

		catch(Exception exp)
		{
			tx.rollback();	
		}
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

	private void newButtonEvent() throws IOException {

		table.setColumnCollapsed("Qty(Bag )",false);
		componentIni(false);
		btnIni(false);
		txtClear();
		issueNo.setValue(autoIssueNo());
		cmbissueTo.focus();
		/*String Name="Rabiul Hasan";
		int a=Name.indexOf("Ra");
		System.out.println("Position IS"+a);
		String str=Name.substring(1, 3);
		String strNew=Name.replaceAll("Ra","Su");
		System.out.println("Reverse Is"+strNew);

		InputStreamReader in=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(in);
		String [] number= new String[5];
		Integer [] num=new  Integer[5];


		for(int i=0;i<number.length;i++)
		{
			System.out.println("Please Enter Your Desire Number At "+i+" " );
			number [i]=br.readLine();
			num[i]= Integer.valueOf( number [i]);
		}

		int max=0;

		for(int i=0;i<number.length;i++)
		{

			if(num[i]>max)
			{
			   max=num[i];	
			}

		}


	  System.out.println("Maximum Number Is"+max);*/
	}

	public void txtClear(){

		issueNo.setValue("");
		cmbissueTo.setValue(null);
		txtreferenceNo.setValue("");
		challanNo.setValue("");

		for(int i=0;i<cmbProductType.size();i++)
		{
			cmbProductType.get(i).setValue(null);
			cmbProduct.get(i).setValue(null);
			txtunit.get(i).setValue("");
			lblModelNo.get(i).setValue("");
			rate.get(i).setValue("");
			qty.get(i).setValue("");
			qtyBag.get(i).setValue("");
			amount.get(i).setValue("");
			cmbStoreLocation.get(i).setValue(null);
			remarks.get(i).setValue("");
			stockQty.get(i).setValue("");
			
		}

		table.setColumnFooter("Amount", "Total :"+0);

	}

	private void componentIni(boolean b) {

		lbLine.setEnabled(!b);
		table.setEnabled(!b);
		issueNo.setEnabled(!b);
		dateField.setEnabled(!b);
		challanNo.setEnabled(!b);
		Loantype.setEnabled(!b);
		cmbissueTo.setEnabled(!b);
		txtreferenceNo.setEnabled(!b);
		txtvoucherNo.setEnabled(!b);
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

	public void tableInitialise()
	{

		for(int i=0;i<7;i++)
		{
			tableRowAdd(i);
		}
	}

	private boolean doubleEntryCheck(String caption,int row)
	{

		for(int i=0;i<amount.size();i++)
		{

			if(i!=row && caption.equals(cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue())))
			{
				return false;
			}

		}
		return true;
	}

	public void proComboChange(String headId,int r)
	{

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String productId =cmbProduct.get(r).getValue().toString().trim();

			List list = session.createSQLQuery("select  productId,productName,unit,closingRate,closingQty,isnull(modelNo,'')modelNo   from funRawMaterialsStock('"+ dateformat.format(dateField.getValue())+" "+"23:59:59" +"','"+productId+"') ").list();
			rowclear(r);
			int cmbflag=0;
			if(list.iterator().hasNext()){

				Object[] element = (Object[]) list.iterator().next();
				cmbflag=1;
				fmt = new Formatter();
				txtunit.get(r).setValue(element[2].toString());
				lblModelNo.get(r).setValue(element[5].toString());
				stockQty.get(r).setValue(fmt.format("%.2f",element[4]) );
				rate.get(r).setValue( df.format(Double.parseDouble(element[3].toString())));
				if (cmbProductType.toString().equalsIgnoreCase("Spare Parts"))
				{
					qtyBag.get(r).focus();
				}
				else{
					qty.get(r).focus();
				}
			}

		}

		catch(Exception exp)
		{

			showNotification("Error is Here"+exp);	
		}


	}

	private void rowclear(int i)
	{
		txtunit.get(i).setValue("");
		lblModelNo.get(i).setValue("");
		stockQty.get(i).setValue("");
		rate.get(i).setValue("");
		qty.get(i).setValue("");
		qtyBag.get(i).setValue("");
		amount.get(i).setValue("");
		cmbStoreLocation.get(i).setValue(null);
		remarks.get(i).setValue("");
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
						tbquntity = event.getProperty().toString().trim().isEmpty()? 0: Double.parseDouble(event.getProperty().toString().trim());

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

						cmbProduct.get(r+1).focus();  	
					}
						else
						{
							showNotification("Warning!!","Ok qty can't be zero.",Notification.TYPE_WARNING_MESSAGE);
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
					}		
				}
			}
		});
	}
	private void bagKgCalc(int ar)
	{

		double kg = Double.parseDouble(qty.get(ar).getValue().toString().isEmpty()? "0.0":qty.get(ar).getValue().toString());
		double bag = kg/25;
		qtyBag.get(ar).setValue(df.format(bag));	
		System.out.println("Error");

	}
	public void tableRowAdd(final int ar)
	{

		//final Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		//Transaction tx=session.beginTransaction();

		try
		{

			lblsl.add(ar,new Label());
			lblsl.get(ar).setWidth("100%");
			lblsl.get(ar).setImmediate(true);
			lblsl.get(ar).setValue(ar+1);

			cmbProductType.add(ar,new ComboBox());
			cmbProductType.get(ar).setWidth("100%");
			cmbProductType.get(ar).setImmediate(true);
			cmbProductType.get(ar).setNullSelectionAllowed(false);
			cmbProductType.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			List lst1 = dbService("select distinct 0,vCategoryType from tbRawItemInfo where vCategoryType not like '%Spare Parts%'");
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
						if(!cmbProductType.get(ar).getValue().toString().equalsIgnoreCase("Spare Parts"))
						{
							table.setColumnCollapsed("Qty(Bag )", false);					
							qtyBag.get(ar).setValue(0);
						}
						else
						{
							table.setColumnCollapsed("Qty(Bag )", true);					
						}

						cmbProduct.get(ar).removeAllItems();
						List lst2 = dbService(" select vRawItemCode,vRawItemName from tbRawItemInfo where vCategoryType like '"+cmbProductType.get(ar).getValue().toString()+"' ");
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
							proComboChange(cmbProduct.get(ar).getValue().toString(),ar);
							qtyBag.get(ar).focus();
							tableColumnAction(cmbProduct.get(ar).getValue().toString(),ar);
							if((ar+1)==rate.size() && !cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue()).isEmpty())
							{
								tableRowAdd(rate.size());
							}
						}
						else
						{	
							cmbProduct.get(ar).setValue(null);
							getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
						}	
					}
				}
			});


			stockQty.add(ar,new TextRead(1));
			stockQty.get(ar).setWidth("100%");
			
			rate.add(ar,new TextRead(1));
			rate.get(ar).setWidth("100%");
			rate.get(ar).setImmediate(true);

			txtunit.add(ar,new TextRead(1));
			txtunit.get(ar).setWidth("100%");
			txtunit.get(ar).setImmediate(true);

			lblModelNo.add(ar,new Label());
			lblModelNo.get(ar).setWidth("100%");
			lblModelNo.get(ar).setImmediate(true);

			qty.add( ar , new AmountField());
			qty.get(ar).setWidth("100%");
			qty.get(ar).setImmediate(true);
			
			qtyBag.add( ar , new AmountField());
			qtyBag.get(ar).setWidth("100%");
			qtyBag.get(ar).setImmediate(true);
			
		/*	
			cmbProductType.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!cmbProductType.get(ar).getValue().toString().equalsIgnoreCase("Spare Parts"))
					{
						table.setColumnCollapsed("Qty(Bag )", false);					
						qtyBag.get(ar).setValue(0);
					}
					else
					{
						table.setColumnCollapsed("Qty(Bag )", true);					
					}
				}
			});

*/
			amount.add( ar , new TextRead(1));
			amount.get(ar).setWidth("100%");

			cmbStoreLocation.add(ar,new ComboBox());
			cmbStoreLocation.get(ar).setWidth("100%");
			cmbStoreLocation.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			List list1=dbService("select  vDepoId,vDepoName from tbDepoInformation");

			for(Iterator iter=list1.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbStoreLocation.get(ar).addItem(element[0].toString().trim());
				cmbStoreLocation.get(ar).setItemCaption(element[0].toString().trim(), element[1].toString());
			}

			remarks.add( ar , new TextField());
			remarks.get(ar).setWidth("100%");
			remarks.get(ar).setImmediate(true);

			table.addItem(new Object[]{ lblsl.get(ar),cmbProductType.get(ar),cmbProduct.get(ar),txtunit.get(ar),lblModelNo.get(ar),stockQty.get(ar),rate.get(ar),qtyBag.get(ar),qty.get(ar),amount.get(ar),cmbStoreLocation.get(ar),remarks.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

}

