package com.example.thirdpartyTransaction;

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

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.example.productionReport.RptFloorStockAsOnDate;
import com.example.productionReport.RptFloorStockDateBetween;
import com.example.productionTransaction.ItemActivation;
import com.example.productionTransaction.ProductionRequistioFind;
import com.example.productionTransaction.ProductionRequistion;
import com.example.rawMaterialTransaction.FindWindow;
//import com.example.rawMaterialSetup.RawItemCategory;
//import com.example.share.AmountField;
//import com.example.share.TextRead;
//import com.example.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
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
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class ThirdPartyRMIssue extends Window {

	SessionBean sessionBean;
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");
	private AbsoluteLayout mainLayout1;

	private Label lblissueTo = new Label("Issue To :");
	private ComboBox cmbissueTo = new ComboBox();

	private Label lblissueNo = new Label("Issue No :");
	private Label lblchallanNo = new Label("Challan No :");
	private TextRead issueNo = new TextRead();
	private TextField challanNo = new TextField();

	Label lblReqDate,lblToDate;
	PopupDateField dReqDate,dToDate;

	private Label lblGroupType;
	private ComboBox cmbGroupType;

	private Label lblProductionType;
	private ComboBox cmbProductionType;

	private Label lblProductionReqNo;
	private ComboBox cmbProductionReqNo;

	private Label lblReqRef;
	private TextRead txtReqRef;

	private Label lblIssueRef;
	private TextRead txtIssueRef;

	private Label lblCategory;
	private ComboBox cmbCategory;	

	private Label lblSubCategory;
	private ComboBox cmbSubCategory;

	private Label lblSubSubCategory;
	private ComboBox cmbSubSubCategory;

	private CheckBox chkAllCategoryType;
	private CheckBox chkAllCategory;
	private CheckBox chkAllSubCategory;
	private CheckBox chkAllSubSubCategory;

	private String categoryType = "";
	private String category = "";
	private String subCategory = "";
	private String subSubCategory = "";
	private Label lblItemType;
	private ComboBox cmbItemType;
	private CheckBox chkAll;

	private Label lblChallanNo;
	private TextField txtChallanNo;
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	private Label lbLine=new Label("<b><font color='#e65100'>===========================================================================================================================================================</font></b>", Label.CONTENT_XHTML);

	private HashMap hRate = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();

	private TextField txtReceiptId=new TextField();

	double totalsum = 0.0;

	private PopupDateField dateField = new PopupDateField();
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat decimalf = new DecimalFormat("#0.00");


	private Label amountWordLabel = new Label("Amount In Words: ");
	private Label totalLabel = new Label("Total                     : ");
	private Label lbllank= new Label();
	private TextField amountWordsField = new TextField();

	private TextRead totalField = new TextRead(1);
	private Label lblReqNo;
	private ComboBox cmbReqNo;
	private NativeButton btnLoadData=new NativeButton("Load Data");
	private NativeButton btnRequisitionEntry=new NativeButton("Requisition Entry");
	private NativeButton btnRequisitionReport=new NativeButton("Requisition Report");
	
	private Table table = new Table();
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> stockQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> requiredQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> ReceivedQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> RemainQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> unit = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> minLevel = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> qty = new ArrayList<AmountField>();
	private ArrayList<AmountField> qtybag = new ArrayList<AmountField>();
	private ArrayList<TextRead> blncqtybag = new ArrayList<TextRead>();
	private ArrayList<TextRead> blncqtykg = new ArrayList<TextRead>();
	private ArrayList<TextField> remarks = new ArrayList<TextField>();
	private ArrayList<ComboBox> cmbStroreLocation = new ArrayList<ComboBox>();
	private TextRead txtvoucherno= new TextRead();


	private Label label = new Label();
	private Label l1 = new Label();
	private Label l2 = new Label();
	boolean isUpdate=false,isFind=false;
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	OptionGroup Loantype;
	private static final List<String>areatype  = Arrays.asList(new String[] {"Production"});

	public ThirdPartyRMIssue(SessionBean sessionBean) {

		this.sessionBean=sessionBean;
		this.setCaption("THIRD PARTY RM ISSUE ENTRY::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout1);
		componentIni(true);
		btnIni(true);
		setEventAction();
		issueTo();
		productionTypeData();
		focusMove();
		reqNoLoadData("%");
		tbCmbProductLoad();
		itemTypeDataLoad();
	}
	
	private void itemTypeDataLoad(){
		List list=dbService("select distinct  0, vCategoryType from tbThirdPartyItemInfo");

		cmbItemType.removeAllItems();
		for(Iterator iter=list.iterator(); iter.hasNext();)
		{
			Object[] element=(Object[]) iter.next();

			cmbItemType.addItem(element[1].toString());
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
	private void reqNoLoadData(String type)
	{
		cmbReqNo.removeAllItems();
		String sql="";
		List list=null;
		if(!isFind)
		{
			 sql="select a.ReqNo,convert(varchar(10),ReqDate,105)date,cast(SUBSTRING(a.ReqNo,CHARINDEX('-',a.ReqNo)+1,len(a.ReqNo)-CHARINDEX('-',a.ReqNo)+1) as int)sl from tbProductionReqInfo a inner join " 
					   +"tbProductionRmDetails b on a.ReqNo=b.reqNo "
					   +"inner join tbThirdPartyItemInfo c on c.vCode=b.rawItemCode "
					   +"where a.ReqNo not in  (select IssueRef from tbThirPartyIssueInfo) and productionTypeId like '"+type+"' ";
			
			 list=dbService(sql);		
		}
		
		else
		{
			 sql="select a.ReqNo,convert(varchar(10),ReqDate,105)date,cast(SUBSTRING(a.ReqNo,CHARINDEX('-',a.ReqNo)+1,len(a.ReqNo)-CHARINDEX('-',a.ReqNo)+1) as int)sl from tbProductionReqInfo a inner join " 
					   +"tbProductionRmDetails b on a.ReqNo=b.reqNo "
					   +"inner join tbThirdPartyItemInfo c on c.vCode=b.rawItemCode "
					   +"where  productionTypeId like '"+type+"' ";
			
			 list=dbService(sql);
		}
		for(Iterator iter=list.iterator();iter.hasNext();) 
		{
			Object element[]=(Object[]) iter.next();
			cmbReqNo.addItem(element[0].toString());
		}
	}
	private void reqDateLoad(String reqNo)
	{

		String sql="select  0,CONVERT(Date,Reqdate,105)ReqDate  from "
			 		+ " tbProductionReqInfo where ReqNo like '"+reqNo+"' ";
		
		List list= dbService(sql);
		Iterator iter=list.iterator();
			if(iter.hasNext())
			{
			Object element[]=(Object[]) iter.next();
			dReqDate.setValue(element[1]);
			
		}
	}

	private void focusMove(){
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(cmbissueTo);
		allComp.add(txtChallanNo);
		allComp.add(cmbItemType);
		allComp.add(cmbProductionType);
		allComp.add(cmbReqNo);
		
			for(int i=0;i<cmbProduct.size();i++)
			{
				allComp.add(cmbProduct.get(i));
				allComp.add(qtybag.get(i));
				allComp.add(qty.get(i));
				allComp.add(cmbStroreLocation.get(i));
			}

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);


		new FocusMoveByEnter(this,allComp);
		}
	
	

	private void productionTypeData() {
		cmbProductionType.removeAllItems();
		Transaction tx=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery("  select productTypeId,productTypeName from tbProductionType").list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbProductionType.addItem(element[0].toString());
				cmbProductionType.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	
	
	private void tbCmbProductLoad(){

		for(int a=0;a<cmbProduct.size();a++)
		{
			String sql= "select vCode ,vItemName, SUBSTRING(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName) )subGroup " 
                        +"from  tbThirdPartyItemInfo  order by vGroupId,vSubGroupId ";
			List list=dbService(sql);

			cmbProduct.get(a).removeAllItems();
			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				Object[] element=(Object[]) iter.next();

				cmbProduct.get(a).addItem(element[0].toString());
				String name=element[1].toString()+"#"+element[2].toString();
				cmbProduct.get(a).setItemCaption(element[0].toString(), name);
			}
		}
	}
	private void tbCmbProductLoadFind(int a){

		String sql="select vCode ,vItemName, SUBSTRING(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName) )subGroup " 
                +"from  tbThirdPartyItemInfo  order by vGroupId,vSubGroupId ";

		List list=dbService(sql);

		cmbProduct.get(a).removeAllItems();
		for(Iterator iter=list.iterator(); iter.hasNext();)
		{
			Object[] element=(Object[]) iter.next();

			cmbProduct.get(a).addItem(element[0].toString());
			String name=element[1].toString()+"#"+element[2].toString();
			cmbProduct.get(a).setItemCaption(element[0].toString(), name);
		}
	}
	

	private AbsoluteLayout buildMainLayout(){

		mainLayout1 = new AbsoluteLayout();
		mainLayout1.setImmediate(false);
		mainLayout1.setWidth("1230px");
		mainLayout1.setHeight("530px"); 
		mainLayout1.setMargin(false);

		// top-level component properties
		setWidth("1250px");
		setHeight("610px");

		lblissueTo.setWidth("-1px");
		lblissueTo.setHeight("-1px");
		lblissueTo.setImmediate(false);


		cmbissueTo.setWidth("250px");
		cmbissueTo.setNewItemsAllowed(true);
		cmbissueTo.setNullSelectionAllowed(true);


		lblissueNo.setWidth("-1px");
		lblissueNo.setHeight("-1px");
		lblissueNo.setImmediate(false);

		issueNo.setWidth("100px");
		issueNo.setHeight("24px");
		issueNo.setImmediate(true);

		Label lblDate=new Label();
		lblDate.setValue("Date :");
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");

		dateField.setWidth("108px");
		dateField.setHeight("24px");
		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setImmediate(true);

		lblProductionType=new Label();
		lblProductionType.setValue("Production Type :");
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");

		cmbProductionType=new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setNewItemsAllowed(true);
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblIssueRef=new Label();
		lblIssueRef.setValue("Issuer Ref :");
		lblIssueRef.setWidth("-1px");
		lblIssueRef.setHeight("-1px");
		
		txtIssueRef=new TextRead();
		txtIssueRef.setImmediate(true);
		txtIssueRef.setWidth("200px");
		txtIssueRef.setHeight("25px");

		lblReqNo=new Label();
		lblReqNo.setValue("Req No :");
		lblReqNo.setWidth("-1px");
		lblReqNo.setHeight("-1px");

		cmbReqNo=new ComboBox();
		cmbReqNo.setWidth("220px");
		cmbReqNo.setNewItemsAllowed(true);
		cmbReqNo.setNullSelectionAllowed(true);
		cmbReqNo.setImmediate(true);
		cmbReqNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		btnLoadData.setWidth("100px");
		btnLoadData.setHeight("28px");
		btnLoadData.setImmediate(true);
		btnLoadData.setIcon(new ThemeResource("../icons/icon_get_world.gif"));

		btnRequisitionEntry.setWidth("150px");
		btnRequisitionEntry.setHeight("28px");
		btnRequisitionEntry.setImmediate(true);
		btnRequisitionEntry.setIcon(new ThemeResource("../icons/generate.png"));

		btnRequisitionReport.setWidth("150px");
		btnRequisitionReport.setHeight("28px");
		btnRequisitionReport.setImmediate(true);
		btnRequisitionReport.setIcon(new ThemeResource("../icons/generate.png"));
		
	/*	btnFloorAsOnDate.setWidth("165px");
		btnFloorAsOnDate.setHeight("28px");
		btnFloorAsOnDate.setImmediate(true);
		btnFloorAsOnDate.setIcon(new ThemeResource("../icons/generate.png"));

		btnFloorDateBetween.setWidth("175px");
		btnFloorDateBetween.setHeight("28px");
		btnFloorDateBetween.setImmediate(true);
		btnFloorDateBetween.setIcon(new ThemeResource("../icons/generate.png"));*/
		
		
		lblChallanNo=new Label("Challan No :");
		lblChallanNo.setWidth("-1px");
		lblChallanNo.setHeight("-1px");
		lblChallanNo.setImmediate(false);

		txtChallanNo=new TextField();
		txtChallanNo.setWidth("100px");
		txtChallanNo.setHeight("24px");
		txtChallanNo.setImmediate(true);
		
		lblItemType=new Label("Item Type :");
		lblItemType.setWidth("-1px");
		lblItemType.setHeight("-1px");
		lblItemType.setImmediate(false);

		cmbItemType=new ComboBox();
		cmbItemType.setImmediate(true);
		cmbItemType.setWidth("200px");
		cmbItemType.setNewItemsAllowed(true);
		cmbItemType.setNullSelectionAllowed(true);
		cmbItemType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		
		lblReqDate = new Label("Req Date :");
		lblReqDate.setImmediate(false);
		lblReqDate.setWidth("-1px");
		lblReqDate.setHeight("-1px");

		dReqDate = new PopupDateField();
		dReqDate.setImmediate(true);
		dReqDate.setWidth("-1px");
		dReqDate.setHeight("-1px");
		dReqDate.setDateFormat("dd-MM-yyyy");
		dReqDate.setValue(new java.util.Date());
		dReqDate.setResolution(PopupDateField.RESOLUTION_DAY);

		table.setWidth("98%");
		table.setHeight("280px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",15);
		table.addContainerProperty("Product", ComboBox.class , new ComboBox());
		table.setColumnWidth("Product",250);
		table.addContainerProperty("Unit", TextRead.class , new TextRead());
		table.setColumnWidth("Unit",40);
		table.addContainerProperty("Min Level", TextRead.class , new TextRead());
		table.setColumnWidth("Min Level",40);
		table.addContainerProperty("Stock Qty", TextRead.class , new TextRead());
		table.setColumnWidth("Stock Qty",60);
		table.addContainerProperty("Required Qty", TextRead.class , new TextRead());
		table.setColumnWidth("Required Qty",70);
		table.addContainerProperty("Received Qty", TextRead.class , new TextRead());
		table.setColumnWidth("Received Qty",70);
		table.addContainerProperty("Remain Qty", TextRead.class , new TextRead());
		table.setColumnWidth("Remain Qty",70);
		table.addContainerProperty("Qty(Bag)", TextField.class , new TextField());
		table.setColumnWidth("Qty(Bag)",80);
		table.addContainerProperty("Issue Qty", TextField.class , new TextField());
		table.setColumnWidth("Issue Qty",60);
		table.addContainerProperty("Balance Qty(Bag)", TextRead.class , new TextRead());
		table.setColumnWidth("Balance Qty(Bag)",80);

		table.addContainerProperty("Balance Qty", TextRead.class , new TextRead());
		table.setColumnWidth("Balance Qty",80);

		table.addContainerProperty("Store Location", ComboBox.class , new ComboBox());
		table.setColumnWidth("Store Location",150);

		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",150);

		tableInitialise();

		mainLayout1.addComponent(lblissueTo,"top:10.0px;left:10.0px;");
		mainLayout1.addComponent(cmbissueTo,"top:8.0px;left:122.0px;");

		mainLayout1.addComponent(lblissueNo,"top:40.0px;left:10.0px;");
		mainLayout1.addComponent(issueNo,"top:38.0px;left:122.0px");

		mainLayout1.addComponent(lblDate,"top:70.0px;left:10.0px;");
		mainLayout1.addComponent(dateField,"top:68.0px;left:122.0px");

		mainLayout1.addComponent(lblProductionType,"top:10.0px;left:400.0px;");
		mainLayout1.addComponent(cmbProductionType,"top:8.0px;left:500.0px;");

		mainLayout1.addComponent(lblChallanNo,"top:100.0px;left:10.0px;");
		mainLayout1.addComponent(txtChallanNo,"top:98.0px;left:122.0px");
		
		
		mainLayout1.addComponent(lblItemType,"top:130.0px;left:10.0px;");
		mainLayout1.addComponent(cmbItemType,"top:128.0px;left:122.0px");

		mainLayout1.addComponent(btnLoadData,"top:128.0px;left:500.0px;");
		mainLayout1.addComponent(btnRequisitionEntry,"top:128.0px;left:603.0px");
		mainLayout1.addComponent(btnRequisitionReport,"top:128.0px;left:758.0px;");
		/*mainLayout1.addComponent(btnFloorAsOnDate,"top:100.0px;left:673.0px;");
		mainLayout1.addComponent(btnFloorDateBetween,"top:100.0px;left:845.0px;");*/
		


		mainLayout1.addComponent(lblIssueRef,"top:40.0px;left:400.0px;");
		mainLayout1.addComponent(txtIssueRef,"top:38.0px;left:500.0px;");	

		mainLayout1.addComponent(lblReqNo,"top:70.0px;left:400.0px;");
		mainLayout1.addComponent(cmbReqNo,"top:68.0px;left:500.0px;");	

		mainLayout1.addComponent(lblReqDate,"top:10.0px;left:720.0px;");
		mainLayout1.addComponent(dReqDate,"top:8.0px;left:790.0px;");	

	
		mainLayout1.addComponent(table,"top:170.0px;left:10.0px;");

		mainLayout1.addComponent(lbLine,"top:455px;left:20.0px;");
		mainLayout1.addComponent(cButton,"top:485px;left:250px;");

		return mainLayout1;	
	}
	private void cmbGroupData() {
		cmbGroupType.removeAllItems();
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List list = session.createSQLQuery("select 0,vCategoryType from tbThirdPartyItemInfo").list();

			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbGroupType.addItem(element[1]);
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}

	private void issueTo(){

		cmbissueTo.removeAllItems();
		Transaction tx=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql="select AutoID,SectionName+'-'+vDepartmentName depName from tbSectionInfo";
			List<?>  list=session.createSQLQuery(sql).list();

			for(Iterator<?>  iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbissueTo.addItem(element[0].toString());
				cmbissueTo.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}

		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void cmbIssueData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List<?>  list = session.createSQLQuery("select * from tbThirPartyIssueInfo").list();

			for(Iterator<?>  iter=list.iterator();iter.hasNext();)
			{

				Object[] element = (Object[]) iter.next();
				cmbissueTo.addItem(element[1]);

			}
		}catch(Exception exp){
			getParent().showNotification(
					"Error1",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			//tx.rollback();
		}

	}

	private void cmbProductAdd()
	{


		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			

			String sql= "select a.vRawItemCode ,a.vRawItemname, SUBSTRING(b.vSubGroupName,CHARINDEX('-',b.vSubGroupName)+1,LEN(b.vSubGroupName) ) as category  from  tbStandardFinishedDetails a " 
					    +"inner join " 
					    +"tbThirdPartyItemInfo b" 
					    +"on  a.vRawItemCode=b.vCode" ;

			System.out.println("sql is"+sql);

			for(int i=0;i<cmbProduct.size();i++)
			{
				cmbProduct.get(i).removeAllItems();	
			}
			List list = session.createSQLQuery(sql).list();

			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				for(int a=0;a<cmbProduct.size()-1;a++){
					cmbProduct.get(a).addItem(element[0]);
					String name=element[1].toString()+"( "+element[2].toString()+"-"+element[2].toString()+" )";
					cmbProduct.get(a).setItemCaption(element[0],name);

				}
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}
	private boolean tableDataCheck(){

		for(int a=0;a<cmbProduct.size();a++){
			if(cmbProduct.get(a).getValue()!=null){
					if(cmbStroreLocation.get(a).getValue()==null){
						return false;
					}
			}
		}
		return true;
	}
	private boolean cehkProductionTypeForReq(){
		if(cmbReqNo.getValue()!=null){
			if(cmbProductionType.getValue()!=null){
				return true;
			}
			else{
				showNotification("Please Select Production Type ",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else{
			showNotification("Please Provide all Data",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
		//return false;
	}
	@SuppressWarnings("serial")
	public void setEventAction(){


		cmbProductionType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductionType.getValue()!=null){

					reqNoLoadData(cmbProductionType.getValue().toString());
				}
				
			}
		});
		
		dateField.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				
			   tableRmClear();	
			}
		});
		
		cmbItemType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbItemType.getValue()!=null)
				{
                    for(int i=0;i<remarks.size();i++)
                    {
                    	tableRmClear();
                    	productDataload(i);	
                    }
                	if(!(cmbItemType.getValue().toString().equalsIgnoreCase("Spare Parts"))){
						
						table.setColumnCollapsed("Qty(Bag)", false);
						table.setColumnCollapsed("Balance Qty(Bag)",false);
				}
				else{
					table.setColumnCollapsed("Qty(Bag)", true);
					table.setColumnCollapsed("Balance Qty(Bag)",true);
				}
	
				}
			}
		});
		
		
		
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{

				newButtonEvent();
				autoIssueNo();
				isFind=false;
				isUpdate=false;
			}


		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbissueTo.getValue()!=null)
				{
					if(!txtChallanNo.getValue().toString().isEmpty())
					{
						if(cmbProduct.get(0).getValue()!=null)
						{
							if(cehkProductionTypeForReq()){
								if(tableDataCheck()){
									isFind=false;
									saveButtonEvent();	
								}
								else{
									showNotification("Warning !!","Please select store location.", Notification.TYPE_WARNING_MESSAGE);
								}
							}
						}
						else{
							showNotification("Warning !!","Please select product name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning !!","Please select challan no",Notification.TYPE_WARNING_MESSAGE);	
					}
				}
				else{
					showNotification("Warning !!","Please select issue to",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				updateButtonEvent();
				isFind=false;
			}
		});

		cButton.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				deleteButtonEvent();
				isFind=false;
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=false;
				refreshButtonEvent();
				isFind=false;
				isUpdate=false;
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
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
					reportShow();
				}

				else
				{
					showNotification("Warning!","Find a Issue No to genarate Challan",Notification.TYPE_WARNING_MESSAGE);	
				}


			}
		});
		
		btnRequisitionReport.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbReqNo.getValue()!=null)

				{
					reportView();
				}

				else
				{
					showNotification("Warning!","Please Select a Requisition Number",Notification.TYPE_WARNING_MESSAGE);	
				}


			}
		});

		
		btnLoadData.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(cmbReqNo.getValue()!=null){
					tableLoadAReq();
				}
				else{
					showNotification("Please Provide Req No.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		btnRequisitionEntry.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(cmbReqNo.getValue()!=null){
					requisitionEntryLoad();
				}
				else{
					showNotification("Please Provide Req No.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		
		/*btnFloorAsOnDate.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) 
			{
				System.out.println("Group Form");
				floorAsOnDate();				
			}
		});
		btnFloorDateBetween.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) 
			{
				System.out.println("Group Form");
				floorDateBetwn();				
			}
		});
		*/
		
		
		cmbReqNo.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				tableRmClear();
				if(cmbReqNo.getValue()!=null)
				{
					reqDateLoad(cmbReqNo.getValue().toString());
					for(int i=0;i<cmbProduct.size();i++)
					{
						dataload(i)	;
					}
					;
					
				}
				else
				{
				
				}
				
			}
		});
	}
	protected void floorDateBetwn()
	{
		Window win = new RptFloorStockDateBetween(sessionBean,"");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				//cmbGroupValueAdd();
				System.out.println("Date Between");
			}
		});

		this.getParent().addWindow(win);

	}
	
	protected void ItemGroup()
	{
		Window win = new ItemActivation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				//cmbGroupValueAdd();
				System.out.println("Date Between");
			}
		});

		this.getParent().addWindow(win);

		
		
	}
	protected void floorAsOnDate() 
	{
		Window win = new RptFloorStockAsOnDate(sessionBean,"");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				//cmbGroupValueAdd();
				System.out.println("As On Date");
			}
		});

		this.getParent().addWindow(win);

		
	}
	private void tableLoadAReq(){
		List list=dbService("select rawId,ReqQty,RemainQty from funcReqRemainCalc('"+cmbReqNo.getValue()+"') where rawId like '%TH%' ");
		int a=0;
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			cmbProduct.get(a).setValue(element[0]);
			requiredQty.get(a).setValue(decimalf.format(element[1]));
			RemainQty.get(a).setValue(decimalf.format(element[2]));
			if(a==lblSl.size()-1){
				tableRowAdd(a+1);
			}
			a++;
		}
	}
	private void requisitionEntryLoad(){
		Window win = new ProductionRequistion(sessionBean,cmbReqNo.getValue().toString(),1);

		this.getParent().addWindow(win);
	}
	private void issueRefAction(){
		tableRmClear();
		List list=dbService("select *,cast(subString(rawItemId,CHARINDEX('-',rawItemId)+1,len(rawItemId)-CHARINDEX('-',rawItemId))as int) as sl" +
				" from funcThirdPartyRMInventoryLoadData ('"+txtIssueRef.getValue().toString().trim()+"') order by sl");
		int a=0;
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			cmbProduct.get(a).removeAllItems();
			cmbProduct.get(a).addItem(element[0]);
			cmbProduct.get(a).setItemCaption(element[0], element[1].toString());
			cmbProduct.get(a).setValue(element[0]);
			unit.get(a).setValue(element[2]);
			stockQty.get(a).setValue(decimalf.format(element[3]));
			//tbjobQty.get(a).setValue(df.format(element[8]));
			requiredQty.get(a).setValue(decimalf.format(element[4]));
			RemainQty.get(a).setValue(decimalf.format(element[5]));
			if(a==lblSl.size()-1){
				tableRowAdd(a+1);
			}
			a++;
		}
	}
	
	private void productDataload(int ar)
	{ 		
		String sql="";
        if(!isFind)
        {
        	sql= "select vCode,vItemName from tbThirdPartyItemInfo where vCategoryType='"+cmbItemType.getValue().toString()+"' ";
       	  
        }
        else
        {
        	sql= "select vCode,vItemName from tbThirdPartyItemInfo "; 
        }
			
		List list=dbService(sql);

		cmbProduct.get(ar).removeAllItems();
		for(Iterator iter=list.iterator(); iter.hasNext();)
		{
			Object[] element=(Object[]) iter.next();
			cmbProduct.get(ar).addItem(element[0].toString());
			cmbProduct.get(ar).setItemCaption(element[0].toString(), element[1].toString());
		}	
}
	
	public void dataload(int a )
	{
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql= "select vCode ,vItemName, SUBSTRING(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName) )subGroup " 
					    +"from  tbThirdPartyItemInfo  order by vGroupId,vSubGroupId ";
			
			List list=dbService(sql);
			

			cmbProduct.get(a).removeAllItems();
			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				Object[] element=(Object[]) iter.next();

				cmbProduct.get(a).addItem(element[0].toString());
				String name=element[1].toString()+"#"+element[2].toString();
				cmbProduct.get(a).setItemCaption(element[0].toString(), name);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("dataload(int i ): "+exp, Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void reportView()
	{	
		String query=null;
		Transaction tx=null;

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("user", sessionBean.getUserName());
			hm.put("type","" );
			hm.put("SUBREPORT_DIR", "./report/production/");

			
			query = "select * from funcProductionReqRmDetails('"+cmbReqNo.getValue().toString()+"') order by jobOrderNo,mouldId,fgId,fgName ";
			
			String subsql = "select  rawItemCode,rawItemName,unit, ISNULL(SUM(ReqQty),0) ReqQty   from tbProductionRmDetails "
					       +"where ReqNo='"+cmbReqNo.getValue().toString()+"' group by rawItemCode,rawItemName,unit ";
			System.out.println("subsql is"+subsql);
			
			hm.put("sql", query);
			hm.put("subsql", subsql);
			
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptProductionRequisition.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
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
	
	private void reportShow()
	{		
		String  storeReq;
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String date = df.format(new Date())+" "+"23:59:59";

		String query=null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("user", sessionBean.getUserName());
			hm.put("type","" );
			
			query = "select * from funReqWiseIssue('"+cmbReqNo.getValue()+"')";
			
			hm.put("sql", query);
		List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/RptRequisitionWiseIssue.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	
	private void cmbsupplierData() 
	{      
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();  
		String quer="  select SupplierID,SupplierName  from tbSupplierDetails";
		List lst = session.createSQLQuery(quer).list();

		for(  Iterator iter=lst.iterator();iter.hasNext(); )
		{
			Object [] element = (Object []) iter.next();

			cmbissueTo.addItem(element[0]);
			cmbissueTo.setItemCaption(element[0], element[1].toString());  
		}
	}



	private String autoIssueNo() 
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select isnull(max( CAST(IssueNo as int) ),0)+1 from  tbThirPartyIssueInfo";

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
	
	private String autoUdTransactionNo() 
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "select ISNULL(MAX(transactionNo),0)+1  from tbUDThirdPartyIssueInfo";

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

		Window win=new FindWindow(sessionBean,txtReceiptId,"ThirdPartyIssue");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtReceiptId.getValue().toString().length()>0)
					System.out.println("Not Done");
				findInitialise();
			}
		});


		this.getParent().addWindow(win);
		isFind=true;

	}

	private void findInitialise(){

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();


			txtClear();
			String query= "select IssueNo,IssuedTo,Date,vProductionType," +
					"challanNo,issueRef,isnull(vItemType,'') vItemType ,convert(varchar(10),Date,105)date from tbThirPartyIssueInfo where IssueNo " +
					"like '"+txtReceiptId.getValue().toString()+"' ";
			List led = session.createSQLQuery(query).list();
			
			
			System.out.println("sangita :"+query);
			if(led.iterator().hasNext()){

				Object[] element = (Object[]) led.iterator().next();


				issueNo.setValue(element[0]);
				cmbissueTo.setValue(element[1].toString());
				dateField.setValue(element[2]);
				cmbProductionType.setValue(element[3]);
				cmbItemType.setValue(element[6].toString());
				txtChallanNo.setValue(element[4]);
				cmbReqNo.setValue(element[5].toString());
				
			}
			
			String query1="select ProductID,Qty,remarks,storeID,a.groupId from tbThirdPartyIssueDetails a " +
					"inner join tbUDThirdPartyIssueInfo b on a.IssueNo=b.issueNo where a.IssueNo=" +
					"'"+txtReceiptId.getValue().toString().trim()+"'";
			System.out.println("query is :"+query1);


			List list=session.createSQLQuery("select ProductID,Qty,remarks,storeID,a.groupId from tbThirdPartyIssueDetails a " +
					"inner join tbThirPartyIssueInfo b on a.IssueNo=b.issueNo where a.IssueNo=" +
					"'"+txtReceiptId.getValue().toString().trim()+"'").list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();

				if(i==lblSl.size()-1){
					tableRowAdd(i+1);
					tbCmbProductLoadFind(i+1);
				}
				fmt = new Formatter();
				
			    cmbProduct.get(i).setValue(element[0].toString());
				
				stockQty.get(i).setValue(Double.parseDouble(stockQty.get(i).getValue().toString())+Double.parseDouble(element[1].toString()));
				qty.get(i).setValue(decimalf.format(element[1]));

				remarks.get(i).setValue(element[2]);
				cmbStroreLocation.get(i).setValue(element[3]);

				System.out.println("I = "+i+"Size: "+lblSl.size());


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

		componentIni(true);
		btnIni(true);
		txtClear();
	}



	private void deleteButtonEvent(){


		if(issueNo.getValue()!= null)
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
	}

	public boolean deleteData(Session session,Transaction tx){


		try{
			session.createSQLQuery("delete tbThirPartyIssueInfo  where IssueNo='"+issueNo.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete tbThirdPartyIssueDetails  where IssueNo='"+issueNo.getValue()+"'").executeUpdate();

			session.createSQLQuery("delete from tbMouldSectionReceiptInfo where reqNo='"+cmbReqNo.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete from tbMouldSectionReceiptDetails where reqNo='"+cmbReqNo.getValue()+"'").executeUpdate();
			return true;

		}
		catch(Exception exp){

			this.getParent().showNotification("Error4",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void updateButtonEvent(){

		if(cmbissueTo.getValue()!= null)
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


	private void saveButtonEvent() {

		if(!issueNo.getValue().toString().trim().isEmpty()){

			if(isUpdate)
			{
				this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
						new YesNoDialog.Callback() {
					public void onDialogResult(boolean yes) {
						if(yes){
							Transaction tx=null;
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							tx = session.beginTransaction();
							if(deleteData(session,tx) && nullCheck())
								insertData(session,tx);
							else{
								tx.rollback();
							}
							isUpdate=false;
							txtClear();

						}
					}
				} ));
			}else{
				this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
						new YesNoDialog.Callback() {



					public void onDialogResult(boolean yes) 
					{
						if(nullCheck())
						{
							if(yes)
							{
								Transaction tx=null;
								Session session = SessionFactoryUtil.getInstance().getCurrentSession();
								tx = session.beginTransaction();

								//if(nullCheck())
								insertData(session,tx);
								
								//txtClear();
							}	
						}
					}					
				}));
			}
		}
		else
			this.getParent().showNotification("Warning :","Please Select Product .", Notification.TYPE_WARNING_MESSAGE);	
	}



	

	private boolean nullCheck()
	{

		for(int i=0;i<cmbProduct.size();i++)
		{

			if(cmbProduct.get(i).getValue()!=null)
			{
				return true;	
			}
			else
			{
				this.getParent().showNotification("Warning :","Please Select Your Desire Product Name.", Notification.TYPE_WARNING_MESSAGE);
				return false;
			}

		}

		return false;

	}

	private String Option(){
		String strFlag="debit";
		if(strFlag.equalsIgnoreCase("debit"))
			strFlag= "RDP";
		else
			strFlag= "RCP";

		return strFlag;
	}


	private void insertData(Session session,Transaction tx){
		try
		{

			String voucharType = "",reqNo="";
			String sqlInfo="",sqlDetailsnew="",sqlUdInfo="",sqlUdDetails="",  vocherId="",udFlag="",sqludDetails="";
			if(!isUpdate)
			{
				
				udFlag="New";
			}

			else
			{
				
				udFlag="Update";
			}
			
			if( Double.parseDouble(totalField.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":totalField.getValue().toString().replaceAll(",", ""))<=0  )
			{
				vocherId="";	
			}

			String RawmaterialLedger ="";

			voucharType = Option();
			if(cmbReqNo.getValue()!=null){
				reqNo=cmbReqNo.getValue().toString();
			}
			String itemtype="";
			if(cmbItemType.getValue()!=null)
			{
				itemtype=cmbItemType.getValue().toString(); 	
			}
			String reqDate="";
			if(dReqDate.getValue()!=null && cmbReqNo.getValue()!=null)
			{
				reqDate=dateformat.format(dReqDate.getValue()); 	
			}
			else
			{
				reqDate=" ";
			}
			
			String sql="insert into tbThirPartyIssueInfo(IssueNo,IssuedTo,Date,challanNo,UserId,userIp,EntryTime, "
			          +"IssueRef,isActive,vItemType,dReqDate,vProductionType) values( '"+issueNo.getValue().toString()+"',"
			          + "  '"+cmbissueTo.getValue().toString()+"','"+dateformat.format(dateField.getValue())+"',"
			          + " '"+txtChallanNo.getValue().toString()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',"
			          + " getdate(),'"+reqNo+"','1','"+itemtype+"','"+reqDate+"','"+cmbProductionType.getValue().toString()+"' ) ";
			
			int udtransaction=Integer.parseInt(autoUdTransactionNo()) ;
			
			String sqludInfo="insert into tbUDThirdPartyIssueInfo (IssueNo,IssuedTo,Date,challanNo,UserId,userIp,EntryTime, "
					         +"IssueRef,isActive,vItemType,dReqDate,vProductionType,vFlag,transactionNo) "
					         +"values('"+issueNo.getValue().toString()+"','"+cmbissueTo.getValue().toString()+"',"
					         + " '"+dateformat.format(dateField.getValue())+"','"+txtChallanNo.getValue().toString()+"',"
					         + " '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',getdate(),"
					         + " '"+cmbReqNo.getValue().toString()+"','1','"+cmbItemType.getValue().toString()+"',"
					         + " '"+reqDate+"','"+cmbProductionType.getValue().toString()+"','"+udFlag+"','"+udtransaction+"' ) ";

			String sqlSectionInfo="insert into tbMouldSectionReceiptInfo (IssueNo,ReqNo,IssueDate,ApproveFlag,userId,userName,EntryTime)values "+
					" ('"+issueNo.getValue().toString()+"','"+cmbReqNo.getValue()+"','"+dateformat.format(dateField.getValue())+"'," +
					"0,'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
			

			for (int i = 0; i < cmbProduct.size(); i++)
			{
				Object temp = cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue());

				if (temp != null && !qty.get(i).getValue().toString().isEmpty())
				{
					String productId="";
					String group="";
					
					productId=cmbProduct.get(i).getValue().toString().trim();	
					
					System.out.println("Code: "+productId+" Caption: "+cmbProduct.get(i).getItemCaption(productId));

					String sqlDetails="";
					
					if(cmbReqNo.getValue()!=null)
					{
						System.out.print("group Is"+group);
						
						sqlDetails="insert into tbThirdPartyIssueDetails(IssueNo,ProductID,Qty,remarks,storeId,margeFrom,isActive,returnFlag, "
								  + "productType,groupid) values('"+issueNo.getValue().toString()+"','"+productId+"',"
								  + " '"+qty.get(i).getValue().toString()+"','"+remarks.get(i).getValue().toString()+"',"
								  + " '"+cmbStroreLocation.get(i).getValue()+"','','1','1','"+cmbProductionType.getValue().toString()+"',"
								  + " '"+group+"' ) ";
						

						sqludDetails="insert into tbUDThirdPartyIssueDetails (IssueNo,ProductID,Qty,remarks,storeId,margeFrom,returnFlag,productType,groupid,transactionNo) "
								     +"values('"+issueNo.getValue().toString()+"','"+productId.trim()+"', '"+qty.get(i).getValue()+"','"+remarks.get(i).getValue().toString()+"', "
								     + " '"+cmbStroreLocation.get(i).getValue().toString()+"','','1','"+cmbItemType.getValue()+"','"+group+"','"+udtransaction+"' ) ";
						
					}
					
					double requiredKg=Double.parseDouble(requiredQty.get(i).getValue().toString().isEmpty()?"0.0":requiredQty.get(i).getValue().toString());
					String requiredBag=bagcalculation(requiredKg);
					
					
					String sqlSectionDetails="insert into tbMouldSectionReceiptDetails(issueNo,ReqNo,rawItemCode,rawItemName,subItemCode,subItemName,unit,requiredQtyBag, "+
							" requiredQtyKg,ReceiptQtyBag,ReceiptQtyKg,ApproveFlag)values('"+issueNo.getValue().toString()+"','"+cmbReqNo.getValue()+"'," +
							"'"+productId.trim()+"','"+cmbProduct.get(i).getItemCaption(productId)+"','','','"+unit.get(i).getValue()+"'," +
							"'"+requiredBag+"','"+requiredQty.get(i).getValue()+"','"+qtybag.get(i).getValue().toString().trim()+"'," +
							"'"+qty.get(i).getValue().toString().trim()+"',0)";

					session.createSQLQuery(sqlDetails).executeUpdate();
					session.createSQLQuery(sqludDetails).executeUpdate();
					if(cmbReqNo.getValue()!=null){
						session.createSQLQuery(sqlSectionDetails).executeUpdate();
						if(i==0)
						{
							session.createSQLQuery(sqlSectionInfo).executeUpdate();//tbMouldSectionReceiptInfo
						}
					}
				}
				
				if(i==0)
				{
					session.createSQLQuery(sql).executeUpdate();
					session.createSQLQuery(sqludInfo).executeUpdate();
				}	
			}
			
			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			//txtClear();
			componentIni(true);
			btnIni(true);

		}catch(Exception exp){
			tx.rollback();
			this.getParent().showNotification("Error5",exp+"",Notification.TYPE_ERROR_MESSAGE);

		}
	}

	private String bagcalculation(double requiredKg)
	{
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		
		System.out.println("required qty is:"+requiredKg);
	 List lst=	session.createSQLQuery("select 0, cast("+requiredKg+"/25 as varchar(120)) ").list();
	 Iterator<?>iter=lst.iterator();
	 
	 if(iter.hasNext())
	 {
		 Object[]element=(Object[]) iter.next();
		return   (String) element[1];
	 }
		
	 return "0";
	}
	
	private void newButtonEvent() 
	{

		componentIni(false);
		btnIni(false);
		txtClear();
		cmbissueTo.focus();
		issueNo.setValue(autoIssueNo());

	}

	private void tableRmClear()
	{
		for(int i=0;i<stockQty.size();i++)
		{
			cmbProduct.get(i).setValue(null);
			stockQty.get(i).setValue("");
			stockQty.get(i).setStyleName("stockclor");
			requiredQty.get(i).setValue("");
			RemainQty.get(i).setValue("");
			ReceivedQty.get(i).setValue("");
			qty.get(i).setValue("");
			remarks.get(i).setValue("");
			cmbStroreLocation.get(i).setValue(null);
			unit.get(i).setValue("");
			minLevel.get(i).setValue("");
			qtybag.get(i).setValue("");
			blncqtybag.get(i).setValue("");
			blncqtykg.get(i).setValue("");
			
		}
	}
	
	public void txtClear(){

		issueNo.setValue("");
		cmbissueTo.setValue(null);
		challanNo.setValue("");
		cmbProductionType.setValue(null);
		cmbItemType.setValue(null);
		txtChallanNo.setValue("");
		dReqDate.setValue(new java.util.Date());
		table.setColumnCollapsed("Sub Product", true);
		tableRmClear();
		totalField.setValue("");
		cmbReqNo.setValue(null);
	}

	private void componentIni(boolean b) {

		cmbissueTo.setEnabled(!b);
		issueNo.setEnabled(!b);
		dateField.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		cmbItemType.setEnabled(!b);
		txtChallanNo.setEnabled(!b);
		lbLine.setEnabled(!b);
		
		for(int i=0;i<cmbProduct.size();i++){
			cmbProduct.get(i).setEnabled(!b);
			stockQty.get(i).setEnabled(!b);
			requiredQty.get(i).setEnabled(!b);
			RemainQty.get(i).setEnabled(!b);
			qty.get(i).setEnabled(!b);
			remarks.get(i).setEnabled(!b);
			cmbStroreLocation.get(i).setEnabled(!b);
			unit.get(i).setEnabled(!b);
			minLevel.get(i).setEnabled(!b);
			qtybag.get(i).setEnabled(!b);
			blncqtybag.get(i).setEnabled(!b);
			blncqtykg.get(i).setEnabled(!b);
		}
		txtIssueRef.setEnabled(!b);
		dReqDate.setEnabled(!b);
		

		cmbReqNo.setEnabled(!b);
		btnLoadData.setEnabled(!b);
		btnRequisitionEntry.setEnabled(!b);
		btnRequisitionReport.setEnabled(!b);
		
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
	public void tableInitialise()
	{

		for(int i=0;i<40;i++)
		{
			tableRowAdd(i);
		}
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
	
	
	public void proComboChange(String headId,int r,String type)
	{

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String productId =headId;
			System.out.println("Mezbah");

			List list = session.createSQLQuery("select unit,minLevel,closingQty from dbo.[funThirdPartyRMStock]('"+dateformat.format(dateField.getValue())+" 23:59:59','"+productId+"')").list();

			int cmbflag=0;
			if(list.iterator().hasNext()){

				Object[] element = (Object[]) list.iterator().next();
				cmbflag=1;

				fmt = new Formatter();
				stockQty.get(r).setValue(fmt.format("%.2f",element[2]));
				
				qty.get(r).setValue("");
				unit.get(r).setValue(element[0].toString());
				minLevel.get(r).setValue(element[1].toString());
				fmt = new Formatter();
			}

			System.out.println("a"+stockQty.get(r).getValue());

			if(headId!=null && stockQty.get(r).getValue().equals("")){

				getParent().showNotification("Warnning!!","Stock is not available!",Notification.TYPE_ERROR_MESSAGE);

				Object checkNull=headId;

				if(!checkNull.equals("") && type.equalsIgnoreCase("Product")){

					cmbProduct.get(r).setValue(null);
				}
				
			}


		}catch(Exception exp){
			this.getParent().showNotification(
					"Error6",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			
		}

	}

	private void tableColumnAction(final String head,final int r)
	{

		qty.get(r).setImmediate(true);
		qty.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
		qty.get(r).setTextChangeTimeout(100);
		qty.get(r).addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) 
			{
			  if(!qty.get(r).getValue().toString().isEmpty())
				  {
				  if(Double.parseDouble( qty.get(r).getValue().toString())>0)
					{
					try{
						System.out.println("\n"+"column Action");
						System.out.println(head);
						if(head.equalsIgnoreCase("x"))
						{
							
						}
						else
						{
						
							double tbquntity;
							double tamount,unitPrice;

							tbquntity = event.getProperty().toString().trim().isEmpty()? 0: Double.parseDouble(event.getProperty().toString().trim());
							String stockQ = stockQty.get(r).getValue().toString().isEmpty()?"0":stockQty.get(r).getValue().toString();
							String Remain=RemainQty.get(r).getValue().toString().isEmpty()?"0.0":RemainQty.get(r).getValue().toString();
							
							if(isUpdate)
							{
								if(Double.parseDouble(stockQ)>=tbquntity)
								{
									double balanceqty=0.00;
									double tBalanceQty=0.00;
									fmt = new Formatter();
									tBalanceQty=Double.parseDouble(stockQ)-tbquntity;
									qtybag.get(r).setValue(decimalf.format(tbquntity/25));
									blncqtykg.get(r).setValue(decimalf.format(tBalanceQty));
									blncqtybag.get(r).setValue(decimalf.format(tBalanceQty/25));	
								}
								else
								{
									getParent().showNotification("Warnning!!","Issue Qty  exceed Stock Qty. Of "+cmbProduct.get(r).getItemCaption(cmbProduct.get(r).getValue())+" "+stockQty.get(r).getValue().toString(),
											Notification.TYPE_WARNING_MESSAGE);
									qty.get(r).setValue("");	
									qtybag.get(r).setValue("");
								}
								
							}
							else if(!isFind&&cmbReqNo.getValue()!=null)
							{

								if(Double.parseDouble(stockQ) >= tbquntity)
								{
									
										double balanceqty=0.00;
										double tBalanceQty=0.00;
										tBalanceQty=Double.parseDouble(stockQ)-tbquntity;
										qtybag.get(r).setValue(decimalf.format(tbquntity/25));
										blncqtykg.get(r).setValue(decimalf.format(tBalanceQty));
										blncqtybag.get(r).setValue(decimalf.format(tBalanceQty/25));


									
								}
								else{
									getParent().showNotification("Warnning","Issue Qty  exceed Stock Qty. Of "+cmbProduct.get(r).getItemCaption(cmbProduct.get(r).getValue())+" "+stockQty.get(r).getValue().toString(),
											Notification.TYPE_WARNING_MESSAGE);
									qty.get(r).setValue("");
								}
							}
							else if(cmbReqNo.getValue()==null)
							{
								if(Double.parseDouble(stockQ) >= tbquntity)
								{
									double balanceqty=0.00;
									double tBalanceQty=0.00;
									fmt = new Formatter();
									qtybag.get(r).setValue(decimalf.format(tbquntity/25));
									tBalanceQty=Double.parseDouble(stockQ)-tbquntity;
									blncqtykg.get(r).setValue(decimalf.format(tBalanceQty));
									blncqtybag.get(r).setValue(decimalf.format(tBalanceQty/25));
								}
								else{

									getParent().showNotification("Warnning","Issue Qty  exceed Stock Qty. Of "+cmbProduct.get(r).getItemCaption(cmbProduct.get(r).getValue())+" "+stockQty.get(r).getValue().toString(),
											Notification.TYPE_WARNING_MESSAGE);
									qty.get(r).setValue("");
									qtybag.get(r).setValue("");

								}
							}

							else
							{
								double balanceqty=0.00;
								double tBalanceQty=0.00;
								fmt = new Formatter();
								qtybag.get(r).setValue(decimalf.format(tbquntity/25));
								tBalanceQty=Double.parseDouble(stockQ)-tbquntity;
								blncqtykg.get(r).setValue(decimalf.format(tBalanceQty));
								blncqtybag.get(r).setValue(decimalf.format(tBalanceQty/25));


							}
						
						}
						cmbProduct.get(r+1).focus();
					}
					catch(Exception exp)
					{
						getParent().showNotification("Error7",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
						//qty.get(r).focus();
						System.out.println("ASD");
					
					}
				}

				else
				{
					showNotification("Warning!!","issue qty can't be zero.",Notification.TYPE_WARNING_MESSAGE);
					qty.get(r).setValue("");
							
			}
			}
		}
		});
}
	private boolean groupcheck(int i)
	{
		try
		{
		  String sql="select * from tbitemGroupInfo where groupid ='"+cmbProduct.get(i).getValue().toString()+"' ";
		  List lst=dbService(sql);
		  if(!lst.isEmpty())
		  {
			  return true;  
		  }
		}
		catch(Exception ex)
		{
		  System.out.print("Exception is :"+ex);	
		}
		
		return false;
	}
	


	public void tableRowAdd(final int ar)
	{

		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query="";
		System.out.println("Value of ar: "+ar);
		try
		{
			lblSl.add(ar, new Label());
			lblSl.get(ar).setValue(ar+1);
			lblSl.get(ar).setImmediate(true);

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

						if (cmbProduct.get(ar).getValue() != null )
						{
							
							if(groupcheck(ar) && cmbReqNo.getValue()!=null)
							{
								
								table.setColumnCollapsed("Sub Product", false);
								
									
							}
						
							int temp=cmbProduct.size();

							if(ar==temp-1)
							{

								tableRowAdd(temp);
								tbCmbProductLoad();
							}

							
							else if(fla)
							{
								int a;
								String head=cmbProduct.get(ar).getValue().toString();
								System.out.println("ProductId: "+head);
								proComboChange(head,ar,"product");
								if(!(cmbItemType.getValue().toString().equalsIgnoreCase("Spare Parts"))){
									
									qtybag.get(ar).focus();
							}
							else{
								qty.get(ar).focus();
							}

								

								tableColumnAction(head,ar);
								if((ar+1)==qty.size() )
								{
									tableRowAdd(qty.size());
									
									if(cmbReqNo.getValue()!=null)
									{
										dataload(ar+1);	
									 	
									}
									else
									{
										 productDataload(ar+1);	
									}								
								}	
							}
							
							else
							{	
								Object checkNull=(Object)cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue());
								System.out.print(checkNull);
								if(!checkNull.equals("")){
									cmbProduct.get(ar).setValue(null);//("x#"+ar,"");
									getParent().showNotification("Warning :","Same Product Name Is not applicable.",Notification.TYPE_WARNING_MESSAGE);
								}
							}							
						}						
					}
				}
			
			});

			
			
			stockQty.add(ar,new TextRead(1));
			stockQty.get(ar).setWidth("100%");

			unit.add(ar,new TextRead(1));
			unit.get(ar).setWidth("100%");
			
			minLevel.add(ar,new TextRead(1));
			minLevel.get(ar).setWidth("100%");

			requiredQty.add(ar,new TextRead(1));
			requiredQty.get(ar).setWidth("100%");
			
			ReceivedQty.add(ar,new TextRead(1));
			ReceivedQty.get(ar).setWidth("100%");

			RemainQty.add(ar,new TextRead(1));
			RemainQty.get(ar).setWidth("100%");
			
			RemainQty.get(ar).addListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event)
				{
					if(!RemainQty.get(ar).getValue().toString().isEmpty())
					{
					   double requiredqty=Double.parseDouble(requiredQty.get(ar).getValue().toString().isEmpty()?"0.00":requiredQty.get(ar).getValue().toString());
					   double remainQty=Double.parseDouble(RemainQty.get(ar).getValue().toString().isEmpty()?"0.00":RemainQty.get(ar).getValue().toString());
					   double receiveQty=requiredqty-remainQty;
					   ReceivedQty.get(ar).setValue(receiveQty);
					}
					
				}
			});
			
			
			qtybag.add( ar , new AmountField());
			qtybag.get(ar).setWidth("100%");
			qtybag.get(ar).setImmediate(true);

			qtybag.get(ar).addListener(new ValueChangeListener() 
			{

				public void valueChange(ValueChangeEvent event) 
				{
					if(Double.parseDouble(qtybag.get(ar).getValue().toString().isEmpty()?"0":qtybag.get(ar).getValue().toString())>0 )
					{
					if(cmbProduct.get(ar).getValue()!=null&&!qtybag.get(ar).getValue().toString().isEmpty() &&  qty.get(ar).getValue().toString().isEmpty()){

						double kgQty=Double.parseDouble(qtybag.get(ar).getValue().toString().isEmpty()?"0.0":
							qtybag.get(ar).getValue().toString())*25;
						System.out.println("Quantity Is "+qty);

						qty.get(ar).setValue(decimalf.format(kgQty));
						System.out.println("Qty in Kg"+kgQty);
						

					}
					}
					
					else
					{
						showNotification("Warning!!","Ok qty can't be zero.",Notification.TYPE_WARNING_MESSAGE);
						qtybag.get(ar).setValue("");
						qty.get(ar).setValue("");
						qtybag.get(ar).focus();
					}				
				}
			});



			qty.add( ar , new AmountField());
			qty.get(ar).setWidth("100%");
			qty.get(ar).setImmediate(true);

			blncqtybag.add( ar , new TextRead(1));
			blncqtybag.get(ar).setWidth("100%");

			blncqtykg.add( ar , new TextRead(1));
			blncqtykg.get(ar).setWidth("100%");


			
			cmbStroreLocation.add(ar,new ComboBox());
			cmbStroreLocation.get(ar).setWidth("100%");
			cmbStroreLocation.get(ar).setImmediate(true);
			cmbStroreLocation.get(ar).setNullSelectionAllowed(true);

			List list = session.createSQLQuery("select vDepoId,vDepoName from tbDepoInformation").list();

			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbStroreLocation.get(ar).addItem(element[0]);
				cmbStroreLocation.get(ar).setItemCaption(element[0], element[1].toString());
			}

			remarks.add( ar , new TextField());
			remarks.get(ar).setWidth("100%");
			remarks.get(ar).setImmediate(true);

			table.addItem(new Object[]{lblSl.get(ar),cmbProduct.get(ar),unit.get(ar),minLevel.get(ar), stockQty.get(ar),requiredQty.get(ar),ReceivedQty.get(ar),RemainQty.get(ar),qtybag.get(ar),qty.get(ar),blncqtybag.get(ar),blncqtykg.get(ar),cmbStroreLocation.get(ar),remarks.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

}

