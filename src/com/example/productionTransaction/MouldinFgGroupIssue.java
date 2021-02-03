package com.example.productionTransaction;

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

import org.apache.naming.java.javaURLContextFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.Sizeable;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class MouldinFgGroupIssue extends Window {

	SessionBean sessionBean;

	private AbsoluteLayout mainLayout;

	private Panel searchPanel;
	private Label lblSearchPanel;
	private FormLayout frmLayout = new FormLayout();

	private PopupDateField dFromDate;
	private PopupDateField dToDate;
	private ComboBox cmbIssueFromFind = new ComboBox("From :");
	private ComboBox cmbIssueToFind = new ComboBox("To :");

	private NativeButton findButton = new NativeButton("Search");

	Table tableFind = new Table();
	ArrayList <Label> lblFindJobNo=new ArrayList<Label>();
	ArrayList <Label> lblFindChallanNo=new ArrayList<Label>();
	ArrayList <Label> lblFindJobDate=new ArrayList<Label>();

	private  Label lbljobNo=new Label("Job No :");
	private TextRead txtJobNo=new TextRead();
	private DecimalFormat decf=new DecimalFormat("#0.00");
	private DecimalFormat dfInteger=new DecimalFormat("#0");

	private Label lbljobDate=new Label("Date :");
	private PopupDateField dJobDate=new PopupDateField();

	private  Label lblchaallanNo=new Label("Challan No :");
	private TextField txtChallanNo =new TextField();

	private Label lblIssueFrom = new Label("From :");
	private ComboBox cmbIssueFrom = new ComboBox();

	private Label lblIssueTo = new Label("To :");
	private ComboBox cmbIssueTo = new ComboBox();
	
	private Label lblFgName = new Label("Finished Goods :");
	private ComboBox cmbFgName1= new ComboBox();
	
	private Label lblMouldStock = new Label("Mould Stock:");
	private TextField txtmouldStock= new TextField();
	//private double stdQty=0.00;
	
	private ComboBox cmbFindProductionType=new ComboBox("Type:");
	private ComboBox cmbFindFrom=new ComboBox("From :");
	private ComboBox cmbFindTo=new ComboBox("To :");
	private ComboBox cmbFindJobOrder=new ComboBox("Job Order :");
	private ComboBox cmbFindFg=new ComboBox("Finished Goods :");
	private ComboBox cmbFindJobNo=new ComboBox("Job No :");
	
	

	private Label lblRawMaterials;

	private  Label lblFinishedGoods=new Label("<font color='blue' size='4px'><b>Finished Goods</b></font>", Label.CONTENT_XHTML);

	private Table tableFg = new Table();
	private Table tableGroup = new Table();


	private ArrayList<Label>lblsl3 = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbsubFg= new ArrayList<ComboBox>();
	private ArrayList<AmountField> txtAmount = new ArrayList<AmountField>(1);
	private ArrayList<AmountField> txtpcs = new ArrayList<AmountField>(1);
	
	

	private Label lbLine=new Label("<font color='#e65100'>===============================================================================================================================================================================================================================================================</font>", Label.CONTENT_XHTML);
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");

	private HashMap hRate = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();

	private TextField txtReceiptId=new TextField();
	Double ttlissue=0.00;
	Double totalissue=0.00;
	private PopupDateField dateField = new PopupDateField();

	// formats
	private DecimalFormat decimalf = new DecimalFormat("#0.00");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat df1 = new DecimalFormat("#0");
	private SimpleDateFormat dFormat = new SimpleDateFormat("dd-MM-yyyy");

	private Label amountWordLabel = new Label("Amount In Words: ");
	private Label totalLabel = new Label("Total  : ");
	private Label lbllank= new Label();
	private TextField amountWordsField = new TextField();

	private TextRead totalField = new TextRead(1);

	private Label label = new Label();
	private Label l1 = new Label();
	private Label l2 = new Label();
	boolean isUpdate=false,isFind=false;
	private Formatter fmt;

	private ReportDate reportTime = new ReportDate();

	OptionGroup Loantype;
	private static final List<String>areatype  = Arrays.asList(new String[] {"Production" });

	Label lblProductionType=new Label("Production Type: ");
	ComboBox cmbProductionType=new ComboBox();

	Label lblJobOrder=new Label("JOb Order: ");
	ComboBox cmbJobOrder=new ComboBox();
   //panel
	Panel panelSearch=new Panel();
	Label lblpanenSearch=new Label();

	double stdQty=0.0;

	public MouldinFgGroupIssue(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption(" Moulding Issue Entry::"+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		componentIni(true);
		btnIni(true);
		tableFggroupInitialise();
		setEventAction();
		focusMove();
		productionTypeLoad();

	}

	private Iterator dbService(String sql){
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			return session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null||session!=null){
				tx.commit();
				session.close();
			}
		}
		return null;
	}

	private void productionTypeLoad()
	{
		Iterator iter=dbService("select productTypeId,productTypeName from tbProductionType");
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}
	}
	
	private void productionTypeLoadFind()
	{
		Iterator iter=dbService("select productTypeId,productTypeName from tbProductionType");
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbFindProductionType.addItem(element[0]);
			cmbFindProductionType.setItemCaption(element[0], element[1].toString());
		}
	}
	
	private AbsoluteLayout buildMainLayout(){

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("1000px");
		mainLayout.setHeight("650px");
		mainLayout.setMargin(false);

		lbljobNo.setWidth("-1px");
		lbljobNo.setHeight("-1px");
		lbljobNo.setImmediate(false);	

		txtJobNo.setWidth("100px");
		txtJobNo.setHeight("24px");
		txtJobNo.setImmediate(true);

		lbljobDate.setWidth("-1px");
		lbljobDate.setHeight("-1px");
		lbljobDate.setImmediate(false);

		dJobDate = new PopupDateField();
		dJobDate.setImmediate(true);
		dJobDate.setWidth("107px");
		dJobDate.setDateFormat("dd-MM-yyyy");
		dJobDate.setValue(new java.util.Date());
		dJobDate.setResolution(PopupDateField.RESOLUTION_DAY);		

		lblchaallanNo.setWidth("-1px");
		lblchaallanNo.setHeight("-1px");
		lblchaallanNo.setImmediate(false);		

		txtChallanNo.setWidth("100px");
		txtChallanNo.setHeight("24px");
		txtChallanNo.setImmediate(true);		

		lblIssueFrom.setWidth("-1px");
		lblIssueFrom.setHeight("-1px");
		lblIssueFrom.setImmediate(false);	

		cmbIssueFrom.setWidth("250px");
		cmbIssueFrom.setNewItemsAllowed(true);
		cmbIssueFrom.setNullSelectionAllowed(true);
		cmbIssueFrom.setImmediate(true);

		lblIssueTo.setWidth("-1px");
		lblIssueTo.setHeight("-1px");
		lblIssueTo.setImmediate(false);		

		cmbIssueTo.setWidth("250px");
		cmbIssueTo.setNewItemsAllowed(true);
		cmbIssueTo.setNullSelectionAllowed(true);
		cmbIssueTo.setImmediate(true);

		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		lblProductionType.setImmediate(false);	

		cmbProductionType.setWidth("250px");
		cmbProductionType.setNewItemsAllowed(true);
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setImmediate(true);

		lblJobOrder.setWidth("-1px");
		lblJobOrder.setHeight("-1px");
		lblJobOrder.setImmediate(false);	

		cmbJobOrder.setWidth("150px");
		cmbJobOrder.setNewItemsAllowed(true);
		cmbJobOrder.setNullSelectionAllowed(true);
		cmbJobOrder.setImmediate(true);
		
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");
		lblFgName.setImmediate(false);	

		cmbFgName1.setWidth("250px");
		cmbFgName1.setNewItemsAllowed(true);
		cmbFgName1.setNullSelectionAllowed(true);
		cmbFgName1.setImmediate(true);
		
		lblMouldStock.setWidth("-1px");
		lblMouldStock.setHeight("-1px");
		lblMouldStock.setImmediate(false);	

		txtmouldStock.setWidth("100px");
		txtmouldStock.setHeight("24px");
		txtmouldStock.setImmediate(true);
		
		tableGroup.setWidth("520");
		tableGroup.setHeight("350px");
		tableGroup.setColumnCollapsingAllowed(true);
		tableGroup.setFooterVisible(true);

		tableGroup.addContainerProperty("SL", Label.class , new Label());
		tableGroup.setColumnWidth("SL",15);

		tableGroup.addContainerProperty("Finished Good", ComboBox.class , new ComboBox());
		tableGroup.setColumnWidth("Finished Good",260);
		
		tableGroup.addContainerProperty("pcs", AmountField.class , new AmountField());
		tableGroup.setColumnWidth("pcs",90);

		tableGroup.addContainerProperty("Qty", AmountField.class , new AmountField());
		tableGroup.setColumnWidth("Qty",90);
		
		
		//tableGroup.setColumnCollapsed("pcs",true);
		
		
		
		lblRawMaterials = new Label("<font color='blue' size='4px'><b>Sub Product</b></font>", Label.CONTENT_XHTML);
		lblRawMaterials.setWidth("-1px");
		lblRawMaterials.setHeight("-1px");
		lblRawMaterials.setImmediate(false);
		
		dFromDate = new PopupDateField("From :");
		dFromDate.setImmediate(true);
		dFromDate.setWidth("107px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		
		
		dToDate = new PopupDateField("To :");
		dToDate.setImmediate(true);
		dToDate.setWidth("107px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		
		
		cmbFindFrom.setWidth("150px");
		cmbFindFrom.setNewItemsAllowed(true);
		cmbFindFrom.setNullSelectionAllowed(true);
		cmbFindFrom.setImmediate(true);
		
		cmbFindTo.setWidth("150px");
		cmbFindTo.setNewItemsAllowed(true);
		cmbFindTo.setNullSelectionAllowed(true);
		cmbFindTo.setImmediate(true);
		
		cmbFindJobOrder.setWidth("150px");
		cmbFindJobOrder.setNewItemsAllowed(true);
		cmbFindJobOrder.setNullSelectionAllowed(true);
		cmbFindJobOrder.setImmediate(true);
		
		cmbFindFg.setWidth("200px");
		cmbFindFg.setNewItemsAllowed(true);
		cmbFindFg.setNullSelectionAllowed(true);
		cmbFindFg.setImmediate(true);
		
		cmbFindJobNo.setWidth("200px");
		cmbFindJobNo.setNewItemsAllowed(true);
		cmbFindJobNo.setNullSelectionAllowed(true);
		cmbFindJobNo.setImmediate(true);
		
		cmbFindProductionType.setWidth("150px");
		cmbFindProductionType.setNewItemsAllowed(true);
		cmbFindProductionType.setNullSelectionAllowed(true);
		cmbFindProductionType.setImmediate(true);


	
		/*lblFinishedGoods.setWidth("-1px");
		lblFinishedGoods.setHeight("-1px");
		lblFinishedGoods.setImmediate(false);*/	

		

/*		// dFromDate
		dFromDate = new PopupDateField("From Date :");
		dFromDate.setImmediate(false);
		dFromDate.setWidth("-1px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		// dToDate
		dToDate = new PopupDateField("To Date :");
		dToDate.setImmediate(false);
		dToDate.setWidth("-1px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);

		cmbIssueFromFind.setWidth("250px");
		cmbIssueFromFind.setNewItemsAllowed(true);
		cmbIssueFromFind.setNullSelectionAllowed(true);
		cmbIssueFromFind.setImmediate(true);	

		cmbIssueToFind.setWidth("250px");
		cmbIssueToFind.setNewItemsAllowed(true);
		cmbIssueToFind.setNullSelectionAllowed(true);
		cmbIssueToFind.setImmediate(true);*/

		// findButton
		findButton.setWidth("80px");
		findButton.setHeight("28px");
		findButton.setIcon(new ThemeResource("../icons/find.png"));
		
		panelSearch=new Panel();
		panelSearch.setWidth("370px");
		panelSearch.setHeight("320px");
		panelSearch.setEnabled(false);
		panelSearch.setStyleName("panelSearch");
		
		
		FormLayout frmLayout=new FormLayout();
		frmLayout.setSpacing(true);
		frmLayout.setMargin(true);
		
		frmLayout.addComponent(dFromDate);
		frmLayout.addComponent(dToDate);
		frmLayout.addComponent(cmbFindProductionType);
		frmLayout.addComponent(cmbFindProductionType);
		frmLayout.addComponent(cmbFindFrom);
		frmLayout.addComponent(cmbFindTo);
		frmLayout.addComponent(cmbFindJobOrder);
		frmLayout.addComponent(cmbFindFg);
		frmLayout.addComponent(cmbFindJobNo);
		
		panelSearch.addComponent(frmLayout);

		/*// tableFind
		tableFind.setSelectable(true);
		tableFind.setWidth("330px");
		tableFind.setHeight("160px");
		tableFind.setColumnCollapsingAllowed(true);

		tableFind.addContainerProperty("JOB NO", Label.class, new Label());
		tableFind.setColumnWidth("JOB NO", 40);
		tableFind.setColumnAlignment("JOB NO", tableFind.ALIGN_CENTER);
		//tableFind.setColumnCollapsed("JOB NO", true);

		tableFind.addContainerProperty("JOb Order", Label.class, new Label());
		tableFind.setColumnWidth("JOb Order", 130);
		tableFind.setColumnAlignment("JOb Order", tableFind.ALIGN_CENTER);

		tableFind.addContainerProperty("JOB DATE", Label.class, new Label());
		tableFind.setColumnWidth("JOB DATE", 100);
		tableFind.setColumnAlignment("JOB DATE", tableFind.ALIGN_CENTER);

		tableFind.setImmediate(true); // react at once when something is selected
		tableFind.setColumnReorderingAllowed(true);
		tableFind.setColumnCollapsingAllowed(true);	*/
/*
		frmLayout.addComponent(dFromDate);
		frmLayout.addComponent(dToDate);
		frmLayout.addComponent(cmbIssueFromFind);
		frmLayout.addComponent(cmbIssueToFind);
		frmLayout.addComponent(findButton);*/
		//frmLayout.addComponent(tableFind);
		
		mainLayout.addComponent(lbljobNo,"top:15.0px;left:20.0px;");
		mainLayout.addComponent(txtJobNo,"top:13.0px;left:95.0px");

		mainLayout.addComponent(lbljobDate,"top:45.0px;left:20.0px;");
		mainLayout.addComponent(dJobDate,"top:43.0px;left:95.0px");

		mainLayout.addComponent(lblchaallanNo,"top:75.0px;left:20.0px;");
		mainLayout.addComponent(txtChallanNo,"top:73.0px;left:95.0px");

		mainLayout.addComponent(lblIssueFrom,"top:45.0px;left:220.0px;");
		mainLayout.addComponent(cmbIssueFrom,"top:43.0px;left:320px;");

		mainLayout.addComponent(lblIssueTo,"top:75.0px;left:220.0px;");
		mainLayout.addComponent(cmbIssueTo,"top:73.0px;left:320px;");

		mainLayout.addComponent(lblProductionType,"top:15.0px;left:220.0px;");
		mainLayout.addComponent(cmbProductionType,"top:13.0px;left:320px;");

		mainLayout.addComponent(lblJobOrder,"top:15.0px;left:580.0px;");
		mainLayout.addComponent(cmbJobOrder,"top:13.0px;left:680px;");
		
		mainLayout.addComponent(lblFgName,"top:45.0px;left:580.0px;");
		mainLayout.addComponent(cmbFgName1,"top:43.0px;left:680px;");
		
		mainLayout.addComponent(lblMouldStock,"top:75.0px;left:580.0px;");
		mainLayout.addComponent(txtmouldStock,"top:73.0px;left:680px;");
		
		mainLayout.addComponent(lblRawMaterials,"top:135.0px;left:60.0px;");
		mainLayout.addComponent(tableGroup,"top:165.0px;left:60.0px;");
		
		mainLayout.addComponent(panelSearch,"top:200.0px;left:600.0px;");
		
		
		//mainLayout.addComponent(lblRawMaterials,"top:85.0px;left:580.0px;");
		//mainLayout.addComponent(tableRM,"top:115.0px;left:0.0px;");

		//mainLayout.addComponent(searchPanel,"top:370.0px;left:50px;");
		//mainLayout.addComponent(lblSearchPanel,"top:375.0px;left:170.0px;");

		mainLayout.addComponent(lbLine,"top:570px;left:0.0px;");
		mainLayout.addComponent(cButton,"top:600px;left:250px;");

		return mainLayout;	
	}

	/*private void tableFindClear()
	{
		dFromDate.setValue(new java.util.Date());
		dToDate.setValue(new java.util.Date());
		cmbIssueFromFind.setValue(null);
		cmbIssueToFind.setValue(null);
		tableFind.removeAllItems();
	}*/

	

	private void focusMove()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();

		allComp.add(dJobDate);
		allComp.add(txtChallanNo);
		allComp.add(cmbProductionType);
		allComp.add(cmbIssueFrom);
		allComp.add(cmbIssueTo);
		allComp.add(cmbJobOrder);
		allComp.add(cmbFgName1);
		for(int i=0;i<lblsl3.size();i++)
		{
			allComp.add(cmbsubFg.get(i));
			allComp.add(txtAmount.get(i));
			
		}

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);

		new FocusMoveByEnter(this,allComp);
	}

	private void sectionDataLoadFind() 
	{
		cmbIssueFromFind.removeAllItems();
		Iterator iter=dbService(" select issueFrom,(case when issueFrom='SEC-0' then 'Moulding Section' else " +
				"(select StepName from tbProductionStep where StepId=issueFrom)  end)Name from tbMouldIssueInfo");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbIssueFromFind.addItem(element[0]);
			cmbIssueFromFind.setItemCaption(element[0], element[1].toString());
		}
	}
	private void issueToDataLoad() 
	{
		cmbIssueTo.removeAllItems();
		String sql="select StepId,StepName from tbProductionStep where productionTypeId like '"+cmbProductionType.getValue()+"' and StepId not like '"+cmbIssueFrom.getValue()+"'";

		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbIssueTo.addItem(element[0]);
			cmbIssueTo.setItemCaption(element[0], element[1].toString());
		}
	}	

/*	private void tableClear()
	{
		for(int a=0;a<lblSl.size();a++)
		{

			cmbItemName.get(a).setValue(null);
			cmbFgName.get(a).setValue(null);
			txtUnit.get(a).setValue("");
			txtSectionStock.get(a).setValue("");
			txtFloorStock.get(a).setValue("");
			txtFloorStockPcs.get(a).setValue("");
			txtMouldStock.get(a).setValue("");
			txtMouldStockPcs.get(a).setValue("");
			txtSectinIssueQty.get(a).setValue("");
			txtSectinIssuePcs.get(a).setValue("");
			txtWastageIssue.get(a).setValue("");
			txttotal.get(a).setValue("");
			txtremarks.get(a).setValue("");
			txtWastageStock.get(a).setValue("");
		}
	}*/

/*	private void rawtableClear()
	{
		for(int a=0;a<lblSl.size();a++)
		{

			txtUnit.get(a).setValue("");
			txtSectionStock.get(a).setValue("");
			txtFloorStock.get(a).setValue("");
			txtSectinIssueQty.get(a).setValue("");
			txtWastageIssue.get(a).setValue("");
			txttotal.get(a).setValue("");
			txtremarks.get(a).setValue("");
			txtWastageStock.get(a).setValue("");
		}

	}*/


	private void insertData()
	{

		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String itemType = "";
		String jobNo = "";
		String name="";
		String query1="";
		Double totalIssue=0.00;
		Double ttlIssue=0.00;

		try
		{

			String sql =" insert into tbMouldIssueInfo" +
					" (jobNo, jobDate, challanNo, issueFrom, issueTo,jobOrderNo, userIp, userId, entryTime,productionType,tempFgId)" +
					" values" +
					" ('"+txtJobNo.getValue().toString()+"'," +
					" '"+dateFormat.format(dJobDate.getValue())+" "+getTime()+"'," +
					" '"+txtChallanNo.getValue()+"'," +
					" '"+cmbIssueFrom.getValue()+"'," +
					" '"+cmbIssueTo.getValue()+"'," +
					" '"+cmbJobOrder.getValue()+"'," +
					" '"+sessionBean.getUserIp()+"'," +
					" '"+sessionBean.getUserId()+"'," +
					" CURRENT_TIMESTAMP,'"+cmbProductionType.getValue()+"','"+cmbFgName1.getValue().toString()+"') " ;

			System.out.println(sql);
			
			
	  for (int i = 0; i < cmbsubFg.size(); i++)
			{
				System.out.println("for");

				if (cmbsubFg.get(i).getValue()!=null && ! txtpcs.get(i).getValue().toString().isEmpty())
				{
					System.out.println("if");

					String query = 	" insert into tbMouldIssueDetails" +
							" (jobNo,jobOrderNo, fgCode,RmCode,unit,sectionStock,floorStock,MouldStock, sectionissueQty,sectionIssuePcs, wastageissueQty, userIp, userid,remarks, entryTime)" +
							" values" +
							" ('"+txtJobNo.getValue().toString()+"'," +
							" '"+cmbJobOrder.getValue().toString()+"'," +
							" '"+cmbsubFg.get(i).getValue().toString()+"'," +
							" ''," +
							" ''," +
							" '0.00'," +
							" '0.00'," +
							" '0.00'," +
							" '"+txtAmount.get(i).getValue().toString()+"'," +
							" '"+txtpcs.get(i).getValue().toString()+"'," +
							" '0.00'," +
							" '"+sessionBean.getUserIp()+"'," +
							" '"+sessionBean.getUserId()+"'," +
							" ''," +
							" CURRENT_TIMESTAMP)";

					System.out.println(query);
					session.createSQLQuery(query).executeUpdate();

					if(i==0)
					{
						session.createSQLQuery(sql).executeUpdate();
					}
					
				}
			}

			tx.commit();
			this.getParent().showNotification("All information is saved successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			System.out.println(exp);
		}
	}

	private Date getTime()
	{
		Date time = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = null;

		try
		{
			String sql = "";

			sql = "select convert(time, CURRENT_TIMESTAMP)";
			System.out.println("time sql"+sql);

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if(iter.hasNext())
			{
				time = (Date) iter.next();
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("getTime error: "+ex, Notification.TYPE_ERROR_MESSAGE);
		}

		return time;
	}

	private void saveButtonEvent() 
	{
		if(!txtChallanNo.getValue().toString().isEmpty())
		{	
			if (cmbIssueFrom.getValue()!=null) 
			{
				if(cmbIssueTo.getValue()!=null)
				{
					if(cmbJobOrder.getValue()!=null)
					{
						if(cmbFgName1.getValue()!=null)
						{
							if(cmbsubFg.get(0).getValue()!=null)
							{
								if(!txtAmount.get(0).getValue().toString().isEmpty())
								{
									if (isUpdate) 
									{
										MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
										mb.show(new EventListener() 
										{
											public void buttonClicked(ButtonType buttonType) 
											{
												if (buttonType == ButtonType.YES) 
												{
													Transaction tx = null;
													Session session = SessionFactoryUtil.getInstance().getCurrentSession();

													tx = session.beginTransaction();

													if (deleteData(session, tx))
													{
														insertData();
													}
													else 
													{
														tx.rollback();
													}
													componentIni(true);
													btnIni(true);
													txtClear();
													
												}
											}
										});
									} 
									else
									{
										MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
										mb.show(new EventListener() 
										{
											public void buttonClicked(ButtonType buttonType) 
											{
												if(buttonType == ButtonType.YES)
												{
													insertData();
													componentIni(true);
													btnIni(true);
													txtClear();	
													;
												}
											}
										});
									}	
								}
								else
								{
									this.getParent().showNotification("Warning :","Please Provide Qty.",Notification.TYPE_WARNING_MESSAGE);
									txtAmount.get(0).focus();			
								}	
								}
								else
								{
									this.getParent().showNotification("Warning :","Please Select Sub Finished Goods.",Notification.TYPE_WARNING_MESSAGE);
									cmbsubFg.get(0).focus();	
								}		
						}
						else
						{
							this.getParent().showNotification("Warning :","Please Select Finished Goods.",Notification.TYPE_WARNING_MESSAGE);
							cmbJobOrder.focus()	;
						}
						
					
					}
					
					else
					{
						this.getParent().showNotification("Warning :","Please Select Job Order No.",Notification.TYPE_WARNING_MESSAGE);
						cmbJobOrder.focus(); 	
					}
					
						 						
				}
				else
				{
					this.getParent().showNotification("Warning :","Please Select Issue To.",Notification.TYPE_WARNING_MESSAGE);
					cmbIssueTo.focus();
				}						
			} 
			else
			{
				this.getParent().showNotification("Warning :","Please Select Issue From.",Notification.TYPE_WARNING_MESSAGE);
				cmbIssueFrom.focus();
			}	
		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Challan No",Notification.TYPE_WARNING_MESSAGE);
			txtChallanNo.focus();
		}
	}

	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete tbMouldIssueInfo where jobNo='"+txtJobNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbMouldIssueInfo where jobNo='"+txtJobNo.getValue()+ "' ");

			session.createSQLQuery("delete tbMouldIssueDetails where jobNo='"+txtJobNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbMouldIssueDetails where jobNo='"+txtJobNo.getValue()+ "' ");

			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void cmbFromDataLoad() 
	{
		String sql="select * from( "+
				"select 1 as a,StepId,StepName from tbProductionStep where productionTypeId like '"+cmbProductionType.getValue()+"') aa order by a";
		Iterator iter=dbService(sql);
		cmbIssueFrom.removeAllItems();
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbIssueFrom.addItem(element[1]);
			cmbIssueFrom.setItemCaption(element[1], element[2].toString());
		}
	}
	
	private void findFromLoad() 
	{
		String sql="select * from( "+
				"select 1 as a,StepId,StepName from tbProductionStep where productionTypeId like '"+cmbFindProductionType.getValue()+"') aa order by a";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbFindFrom.addItem(element[1]);
			cmbFindFrom.setItemCaption(element[1], element[2].toString());
		}
	}
	

	private void jobOrderNoLoad() 
	{
		cmbJobOrder.removeAllItems();
		Iterator iter=dbService("select 0,orderNo from tbJobOrderInfo");
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbJobOrder.addItem(element[1]);
		}
	}
	
	private void FindjobOrderNoLoad() 
	{
		System.out.println("OK DONE");
		//cmbFindJobOrder.removeAllItems();
		String sql = "select distinct 0,  joborderNo  from tbMouldIssueInfo where issueFrom like '"+cmbFindFrom.getValue().toString()+"' and issueTo like '"+cmbFindTo.getValue().toString()+"' "
				     +"and CONVERT(date,jobDate,105) between '"+dateFormat.format(dFromDate.getValue())+"' and '"+dateFormat.format(dToDate.getValue()) +"' ";
		System.out.println("query is"+sql);
		Iterator iter=dbService(sql);
		while(iter.hasNext())
		{
			Object element[]=(Object[]) iter.next();
			cmbFindJobOrder.addItem(element[1]);
		}
	}

/*	private void fgLoad() {
		String sql="select distinct fgId,(select vProductName from tbFinishedProductInfo where vProductId like fgId) "+
				" as fgName from tbJobOrderDetails where orderNo ='"+cmbJobOrder.getValue()+"'";
		for(int a=0;a<lblSl.size();a++){
			cmbFgName.get(a).removeAllItems();
			Iterator iter=dbService(sql);
			while(iter.hasNext()){
				Object element[]=(Object[]) iter.next();
				cmbFgName.get(a).addItem(element[0]);
				cmbFgName.get(a).setItemCaption(element[0], element[1].toString());
			}
		}


	}*/
	public void setEventAction(){

		cmbIssueFrom.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbIssueFrom.getValue()!=null)
				{
					issueToDataLoad();
					
				}
			}
		});
		cmbIssueTo.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {

				if(cmbIssueTo.getValue()!=null){
					jobOrderNoLoad();
				}
			}
		});
		
		cmbFindTo.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {

				if(cmbFindTo.getValue()!=null)
				{
					 FindjobOrderNoLoad() ;
				}
			}
		});
		
		cmbFindFg.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {

				if(cmbFindFg.getValue()!=null)
				{
					FindJobNoLoad() ;
				}
			}
		});
		
		cmbJobOrder.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbJobOrder.getValue()!=null)
				{
					FinishedGoodsLoad();
					
					
				}
				else
				{
					cmbFgName1.removeAllItems();
				}
			}
		});
		
		
		cmbFindJobOrder.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindJobOrder.getValue()!=null)
				{
					//FinishedGoodsLoad();
					FindFinishedGoodsLoad();
						
				}
				else
				{
					cmbFindFg.removeAllItems();
				}
			}
		});
		
		cmbFindJobNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindJobNo.getValue()!=null)
				{
					txtClear();
					findinitialise();
						
				}
				else
				{
					cmbFindFg.removeAllItems();
				}
			}
		});
		
		
		
		cmbFgName1.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbFgName1.getValue()!=null){
					StockDataLoad();
					//tableClear();
					for(int i=0;i<lblsl3.size();i++)
					{
						tableDataload(i);	
					}
				}
			}
		});

		cmbIssueFromFind.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbIssueFromFind.getValue()!=null)
				{

					issueToDataLoadFind();
					showNotification(cmbIssueFromFind.getValue().toString(),Notification.TYPE_TRAY_NOTIFICATION);
				}
			}
		});
		cmbProductionType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductionType.getValue()!=null)
				{
					cmbFromDataLoad();
				}
			}
		});
		cButton.btnSave.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				saveButtonEvent();
			}
		});

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				txtJobNo.setValue(autoIssueNo());
				isFind=false;
				dJobDate.focus();

			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				editButtonEvent();
				//isFind=false;
			}
		});

		cButton.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//deleteButtonEvent();
				isFind=false;
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				isFind=false;
				isUpdate=false;
			}
		});

	
		cButton.btnFind.addListener(new ClickListener() 
		{

			public void buttonClick(ClickEvent event)
			{
				isFind=true;
				findButtonEvent();
				productionTypeLoadFind();
				 
			}

		});
		
		cmbFindProductionType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbFindProductionType.getValue()!=null)
				{
					findFromLoad();
				}
				
			}
		});
		
	cmbFindFrom.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbFindFrom.getValue()!=null)
				{
					issueToDataLoadFind();
				}
				
			}
		});


		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		findButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				tableFindDataLoad();
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtChallanNo.getValue().toString().isEmpty())

				{
					if(cmbIssueFrom.getValue()!=null)
					{
						if(cmbIssueTo.getValue()!=null)
						{
							reportShow();
						}
					}
				}

				else
				{
					showNotification("Warning!","Find a Challan No to genarate preview",Notification.TYPE_WARNING_MESSAGE);	
				}


			}
		});
	}
	
	private void tableFindDataLoad(){
		/*for(int a=0;a<lblFindJobNo.size();a++){
			lblFindJobNo.get(a).setValue("");
			lblFindChallanNo.get(a).setValue("");
			lblFindJobDate.get(a).setValue("");
		}*/
		tableFind.removeAllItems();
		Iterator iter=dbService(" select jobNo,joborderNo,jobDate from tbMouldIssueInfo where " +
				"issueFrom='"+cmbIssueFromFind.getValue()+"' and issueTo='"+cmbIssueToFind.getValue()+"' and "+
				"CONVERT(date,jobDate,105) between '"+new SimpleDateFormat("yyyy-MM-dd").format(dFromDate.getValue())+"'" +
				" and '"+new SimpleDateFormat("yyyy-MM-dd").format(dToDate.getValue())+"'");
		int ar=0;
		if(!iter.hasNext()){
			showNotification("Sorry!!","There is No Data",Notification.TYPE_WARNING_MESSAGE);
		}
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tableRowAdd(ar,element[0].toString(),element[2].toString(),element[1].toString());
			ar++;
		}
	}

	private void issueToDataLoadFind() 
	{
		/*cmbFindTo.removeAllItems();
		Iterator iter=dbService(" select distinct issueTo,(select StepName from tbProductionStep where StepId=issueTo)as" +
				" Name from tbMouldIssueInfo where issueFrom='"+cmbFindFrom.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbFindTo.addItem(element[0]);
			cmbFindTo.setItemCaption(element[0], element[1].toString());
		}*/
		
		cmbFindTo.removeAllItems();
		String sql="select StepId,StepName from tbProductionStep where productionTypeId like '"+cmbFindProductionType.getValue()+"' and StepId not like '"+cmbFindFrom.getValue()+"'";

		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbFindTo.addItem(element[0]);
			cmbFindTo.setItemCaption(element[0], element[1].toString());
		}
	}
	
	private void FinishedGoodsLoad() 
	{
		cmbFgName1.removeAllItems();
		String sql= "select b.fgId,(select vProductName from tbFinishedProductInfo where vProductId like b.fgId)fgName from  tbJobOrderInfo a "
				    +"inner join tbJobOrderDetails b on a.orderNo=b.orderNo where a.orderNo like '"+cmbJobOrder.getValue()+"' ";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbFgName1.addItem(element[0]);
			cmbFgName1.setItemCaption(element[0], element[1].toString());
		}
	}
	
	
	private void FindFinishedGoodsLoad() 
	{
		cmbFindFg.removeAllItems();
		String sql=  "select distinct tempFgId,(select vProductName from tbFinishedProductInfo where vProductId like tempFgId ) productName from tbMouldIssueInfo where  issueFrom like '"+cmbFindFrom.getValue().toString()+"' and issueTo like '"+cmbFindTo.getValue().toString()+"'  "
				     +"and joborderNo like '"+cmbFindJobOrder.getValue().toString()+"' ";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbFindFg.addItem(element[0]);
			cmbFindFg.setItemCaption(element[0], element[1].toString());
		}
	}
	
	private void FindJobNoLoad() 
	{
		cmbFindJobNo.removeAllItems();
		String sql=  "select distinct 0,jobNo from tbMouldIssueInfo where  issueFrom like '"+cmbFindFrom.getValue().toString()+"' and issueTo like '"+cmbFindTo.getValue().toString()+"' "
				    + "and joborderNo like '"+cmbFindJobOrder.getValue().toString()+"'  and tempFgId like '"+cmbFindFg.getValue().toString()+"' and CONVERT(date,jobDate,105) "
		             +"between '"+dateFormat.format(dFromDate.getValue())+"' and '"+dateFormat.format(dToDate.getValue())+"' ";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbFindJobNo.addItem(element[1]);
			
		}
	}
	
	
	private void tableDataload(int index) 
	{
		cmbsubFg.get(index).removeAllItems();
		String sql= "select b.subProductId,(select vProductName from tbFinishedProductInfo where vProductId like b.subProductId) from tbFgGroupInfo a "
				    +"inner join tbFgGroupDetails b on a.jobNo=b.jobNo "
				    +" where a.fgId like '"+cmbFgName1.getValue().toString()+"' and a.declarationDate like (select MAX(declarationDate) from tbFgGroupInfo where fgId like '"+cmbFgName1.getValue().toString()+"') ";

		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbsubFg.get(index).addItem(element[0]);
			cmbsubFg.get(index).setItemCaption(element[0], element[1].toString());
		}
	}
	
	
	private void StockDataLoad() 
	{
		txtmouldStock.setValue("");
		String sql1="select * from dbo.funcMouldproductionStockNew" +
				"('"+datef.format(dJobDate.getValue())+"','1','"+cmbProductionType.getValue().toString()+"','"+cmbIssueTo.getValue()+"'," +
				"'"+cmbIssueFrom.getValue()+"','"+cmbFgName1.getValue()+"') ";
		Iterator iter1=dbService(sql1);
		while(iter1.hasNext())
		{
			Object element1[]=(Object[])iter1.next();
			;
		
			txtmouldStock.setValue(decf.format(element1[4]));
			stdQty=Double.parseDouble(element1[5].toString());
			
		}
	}
	
	


	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
	}

	private void findinitialise()
	{
		
		
		
		String sql= "select distinct a.jobNo,a.jobDate,a.challanNo,a.productionType,a.issueFrom,a.issueTo,a.joborderNo,a.tempFgId,b.fgCode,b.sectionIssuePcs,b.SectionIssueQty from tbMouldIssueInfo a inner join "
				    +"tbMouldIssueDetails b on a.jobNo=b.jobNo "
				   + "where a.jobNo like '"+cmbFindJobNo.getValue().toString()+"' ";
		
		Iterator iter=dbService(sql);
		int ar=0;
		if(!iter.hasNext()){
			showNotification("Sorry!!","There is No Data",Notification.TYPE_WARNING_MESSAGE);
		}
		while(iter.hasNext())
		{
			Object element[]=(Object[])iter.next();
			if(ar==0)
			{
			
				txtJobNo.setValue(element[0]);
				dJobDate.setValue(element[1]);
				txtChallanNo.setValue(element[2]);
				cmbProductionType.setValue(element[3]);
				cmbIssueFrom.setValue(element[4]);
				cmbIssueTo.setValue(element[5]);
				cmbJobOrder.setValue(element[6]);
				cmbFgName1.setValue(element[7]);	
			}
			cmbsubFg.get(ar).setValue(element[8]);
			txtpcs.get(ar).setValue(element[9]);
			txtAmount.get(ar).setValue(element[10]);
			
			ar++;
		}
	}

	private void tableRowAdd(int ar,String issNo,String issDate,String challanNo){

		lblFindJobNo.add(ar,new Label());
		lblFindJobNo.get(ar).setWidth("100%");
		lblFindJobNo.get(ar).setImmediate(true);
		lblFindJobNo.get(ar).setValue(issNo);

		lblFindChallanNo.add(ar,new Label());
		lblFindChallanNo.get(ar).setWidth("100%");
		lblFindChallanNo.get(ar).setImmediate(true);
		lblFindChallanNo.get(ar).setValue(challanNo);

		lblFindJobDate.add(ar,new Label());
		lblFindJobDate.get(ar).setWidth("100%");
		lblFindJobDate.get(ar).setImmediate(true);
		lblFindJobDate.get(ar).setValue(issDate);

		tableFind.addItem(new Object[]{lblFindJobNo.get(ar),lblFindChallanNo.get(ar),lblFindJobDate.get(ar)},ar);
	}
/*
	private void RawItemDataLoad(int ar)
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query = "";
			query=  "	select distinct a.ProductID,a.vRawItemName  from "
					+"	( "
					+" select b.ProductID,c.vRawItemName from tbRawIssueInfo a "
					+"inner join "
					+"tbRawIssueDetails b "
					+"on "
					+"a.IssueNo=b.IssueNo "
					+"inner join "
					+"tbRawItemInfo c "
					+"on "
					+"c.vRawItemCode=b.ProductID "
					+"where IssuedTo like (select AutoID from tbSectionInfo where SectionName like '"+cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue())+"') "
					+"union " 
					+"select b.ProductID,c.vRawItemName from tbRawIssueInfo a "
					+"inner join "
					+"tbRawIssueDetails b "
					+"on "
					+"a.IssueNo=b.IssueNo "
					+"inner join "
					+"tbRawItemInfo c "
					+"on "
					+"c.vRawItemCode=b.ProductID "
					+"where IssuedTo like (select AutoID from tbSectionInfo where SectionName like '"+cmbIssueTo.getItemCaption(cmbIssueTo.getValue())+"') "
					+") as a ";

			List list=session.createSQLQuery(query).list();

			cmbItemName.get(ar).removeAllItems();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbItemName.get(ar).addItem(element[0].toString());
				cmbItemName.get(ar).setItemCaption(element[0].toString(), (String) element[1]);
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}*/


	private void reportShow()
	{
		String query=null;
		String Subquery=null;
		Transaction tx=null;
		int type=0;

		//		if(chkpdf.booleanValue()==true)
		type=1;
		//		else
		//			type=0;

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//			hm.put("fromdate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("From",cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()) );
			hm.put("To",cmbIssueTo.getItemCaption(cmbIssueTo.getValue()) );
			hm.put("SysDate",reportTime.getTime);

			query="select b.Fgid,c.vProductName,c.vUnitName,c.vCategoryId,c.vCategoryName,a.challanNo,a.jobNo,a.jobDate from  " +
					"tbMouldingIssueInfo as a inner join tbMouldingFgIssueDetails b on a.jobNo=b.jobNo inner join" +
					" tbFinishedProductInfo c on c.vProductId=b.Fgid where a.issueFrom " +
					"like  '"+cmbIssueFrom.getValue()+"' and a.IssueTo like '"+cmbIssueTo.getValue()+"'  and a.challanNo like '"+txtChallanNo.getValue()+"' ";

			Subquery="select b.productId,c.vRawItemName,c.vUnitName,a.challanNo,a.jobNo,a.jobDate,b.sectionissue," +
					"b.wastageissue from  tbMouldingIssueInfo as a inner join tbMouldingRawIssueDetails b on a.jobNo=b.jobNo " +
					"inner join tbRawItemInfo c on c.vRawItemCode=b.productId where a.issueFrom " +
					"like '"+cmbIssueFrom.getValue()+"' and a.issueTo like '"+cmbIssueTo.getValue()+"'  and a.challanNo like '"+txtChallanNo.getValue()+"' ";


			System.out.println(query);
			System.out.println(Subquery);
			hm.put("sql", query);
			hm.put("subsql", Subquery);
			hm.put("path", "./report/production/");


			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptMoudingIssue.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}

	/*private void ReportView()
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
			//hm.put("FromDate", new SimpleDateFormat("dd-MM-yyyy").format(fromDate.getValue()));
			//hm.put("ToDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("userName", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			//query="select * from VwRawReceivedReturn where ProductID like '"+cmbId + "%' and Qty>0 and Date between '"+new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())+"' and '"+new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())+"' order by ProductName,date";
			//query= "select tb.Unit, rpi.ChallanNo, sd.SupplierName, tb.ProductName ,rpi.SupplierId, rpd.ProductID, rpd.Date, rpd.Amount as Rate, rpd.Qty, (rpd.Qty * rpd.Amount) as Amount from   tbRawPurchaseReturnDetails rpd  inner join tbRawPurchaseReturn rpr on rpd.ReceiptNo=rpr.ReceiptNo inner join tbRawPurchaseInfo rpi on rpi.ReceiptNo=rpd.ReceiptNo inner join tbRawProductInfo tb on tb.ProductCode=rpd.ProductID inner join tbSupplierDetails sd on sd.SupplierID=rpi.SupplierId where CONVERT(date,rpd.Date,105) between '"+new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())+"'  and  '"+new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())+"' and rpd.ProductID like '%'";

			query = "select rc.SectionName , rc.Address,rp.Unit, CONVERT(date,rd.Date,105 )as 'Date', rd.IssueNo, rd.IssuedTo ,rd.IssueType, rs.ProductID ,rp.ProductName,  rs.Qty,rs.remarks,rd.challanNo from tbRawIssueInfo rd  inner join  tbRawIssueDetails rs on rd.IssueNo=rs.IssueNo  inner join tbRawProductInfo rp on rp.ProductCode=rs.ProductID inner join tbSectionInfo rc on rc.AutoID=rd.IssuedTo where rd.IssueNo='"+issueNo.getValue().toString().trim()+"' ";
			System.out.println(query);
			hm.put("sql", query);

			Window win = new ReportViewer(hm,"report/rptChallan.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
			win.setCaption("PROJECT REPORT :: "+sessionBean.getCompanyName());
			win.setResizable(false);
			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);

		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);

		}
				}
		else{

			this.getParent().showNotification("Warning","Please Select vaild Date.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	 */

	private String autoIssueNo() 
	{
		String autoCode = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select  ISNULL(MAX(jobNo),0)+1  from tbMouldIssueInfo";

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

	
	private void findButtonEvent()
	{
		componentIni(true);
		panelSearch.setEnabled(true);
		isFind=true;
	}

	private void refreshButtonEvent()
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}
	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
	}

	public void txtClear()
	{
		txtJobNo.setValue("");
		dJobDate.setValue(new java.util.Date());
		txtChallanNo.setValue("");
		cmbProductionType.setValue(null);
		cmbJobOrder.setValue(null);
		cmbIssueFrom.setValue(null);
		cmbIssueTo.setValue(null);
		cmbJobOrder.setValue(null);
		cmbFgName1.setValue(null);
		txtmouldStock.setValue("");
		
		for(int i=0;i<cmbsubFg.size();i++)
		{
			cmbsubFg.get(i).setValue(null);
			txtAmount.get(i).setValue("");
			txtpcs.get(i).setValue("");
		}	
	}

	private void componentIni(boolean b) 
	{
		txtJobNo.setEnabled(!b);
		dJobDate.setEnabled(!b);
		txtChallanNo.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		cmbJobOrder.setEnabled(!b);
		cmbIssueFrom.setEnabled(!b);
		cmbIssueTo.setEnabled(!b);
		cmbFgName1.setEnabled(!b);
		txtmouldStock.setEnabled(!b);
		lblFinishedGoods.setEnabled(!b);
		lblRawMaterials.setEnabled(!b);
		tableGroup.setEnabled(!b);
		
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnPreview.setEnabled(t);

	}
	
	public void tableFggroupInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAddGroup(i);
		}
	}
	
	public boolean selectFrom()
	{
		try
		{
			Transaction tx=null;
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select AutoID from tbSectionInfo where AutoID like '"+cmbIssueFrom.getValue().toString()+"'";
			List list=session.createSQLQuery(sql).list();
			if(list.isEmpty()){
				return false;
			}
		}
		catch(Exception exp)
		{
			showNotification("Issueqty Value change"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		return true;
	}
	
	public void tableRowAddGroup(final int ar)
	{
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query=null;

		try
		{

			lblsl3.add(ar,new Label());
			lblsl3.get(ar).setWidth("20px");
			lblsl3.get(ar).setValue(ar + 1);

			cmbsubFg.add(ar,new ComboBox());
			cmbsubFg.get(ar).setWidth("100%");
			cmbsubFg.get(ar).setImmediate(true);
			cmbsubFg.get(ar).setNullSelectionAllowed(true);	
			
			cmbsubFg.get(ar).addListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbsubFg.get(ar).getValue()!=null)
					{
						if (doubleEntry(ar))
						{
							
						}
						else
						{
							showNotification("Double Entry",Notification.TYPE_WARNING_MESSAGE);
							cmbsubFg.get(ar).setValue(null);
						}	
					}
					
					
				}
			});
			
			
			txtAmount.add(ar,new AmountField());
			txtAmount.get(ar).setWidth("100%");
			
			/*txtAmount.get(ar).addListener(new ValueChangeListener()
			{
				
				public void valueChange(ValueChangeEvent event)
				{
					if(!txtAmount.get(ar).getValue().toString().isEmpty())
					{
						double stock=0.00;
						double substock=0.00;
						Double issuePcs=0.0;
						stock= Double.parseDouble(txtmouldStock.getValue().toString());
						System.out.println("Done");
						
						for(int i=0;i<lblsl3.size();i++)
						{
							if(!txtAmount.get(i).getValue().toString().isEmpty())
							{
								substock=substock+ Double.parseDouble(txtAmount.get(i).getValue().toString());
								
							}	
						}
						
						if(substock<=stock)
						{
							issuePcs=substock/stdQty;
							System.out.println("Issue Pcs: "+issuePcs);
							txtpcs.get(ar).setValue(dfInteger.format(issuePcs));
							
						}
						else
						{
							showNotification("Must Not Excced Mould Stock",Notification.TYPE_WARNING_MESSAGE);
							txtAmount.get(ar).setValue("");
						}
					
					}
					
				}
			});*/
			
			txtpcs.add(ar,new AmountField());
			txtpcs.get(ar).setWidth("100%");
			
			txtpcs.get(ar).addListener(new ValueChangeListener()
			{
				
				public void valueChange(ValueChangeEvent event)
				{
					if(!txtpcs.get(ar).getValue().toString().isEmpty())
					{
						double stock=0.00;
						double substock=0.00;
						Double issueQty=0.0;
						stock= Double.parseDouble(txtmouldStock.getValue().toString());
						System.out.println("Done");
						
						for(int i=0;i<lblsl3.size();i++)
						{
							if(!txtpcs.get(i).getValue().toString().isEmpty())
							{
								substock=substock+ Double.parseDouble(txtpcs.get(i).getValue().toString());
								
							}	
						}
						
						if(substock<=stock)
						{
							issueQty=Double.parseDouble(txtpcs.get(ar).getValue().toString())*stdQty;
							System.out.println("Std:"+stdQty);
							System.out.println("pcs:"+Double.parseDouble(txtpcs.get(ar).getValue().toString()));
							//System.out.println("Issue Pcs: "+issueQty);
							txtAmount.get(ar).setValue(dfInteger.format(issueQty));
							
						}
						else
						{
							showNotification("Must Not Excced Mould Stock",Notification.TYPE_WARNING_MESSAGE);
							txtpcs.get(ar).setValue("");
						}
					
					}
					
				}
			});


			tableGroup.addItem(new Object[]{lblsl3.get(ar), cmbsubFg.get(ar),txtpcs.get(ar),txtAmount.get(ar)},ar);

		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	
	public boolean doubleEntry(int ar)
	{
		String value=cmbsubFg.get(ar).getValue().toString();
		String value1=cmbsubFg.get(ar).getValue().toString();

		for(int x=0;x<cmbsubFg.size();x++)
		{
			if(cmbsubFg.get(x).getValue()!=null)
			{
				if(x!=ar&&value.equalsIgnoreCase(cmbsubFg.get(x).getValue().toString()))
				{
					return false;
				}
			}
		}
		return true;
	}
}
