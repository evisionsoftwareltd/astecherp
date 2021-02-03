package com.example.sparePartsTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
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

public class RawmaterialIssueReturn extends Window {

	SessionBean sessionBean;
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");
	private AbsoluteLayout mainLayout1;
	
	private Label lbltransactionno = new Label("Transaction No :");
	private TextRead txttransactionNo = new TextRead();
	private TextRead txtvoucherno = new TextRead();

	private Label lblreturnForm = new Label("Return From :");
	private ComboBox cmbrteturnFrom = new ComboBox();
	
	private Label lblreturnDate = new Label("Return Date :");
	private PopupDateField dreturnDate = new PopupDateField();
	
	private Label lblIssueNo = new Label("Issue No:");
	private ComboBox cmbIssueNo = new ComboBox();
	
	private Label lblchallanNo = new Label("Challan No :");
	private TextField txtchallanNo = new TextField();
	
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private DecimalFormat decimalf = new DecimalFormat("#0.00");

	private Label lbLine=new Label("____________________________________________________________________________________________________________________________________________________________________");

	private HashMap hRate = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();
	private TextField txtReceiptId=      new TextField();

	double totalsum = 0.0;


	private Table table = new Table();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> unit = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> txtissueqty = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> txtissueRturnQty = new ArrayList<AmountField>(1);
	private ArrayList<TextRead> txtrate = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> txtrAmount = new ArrayList<TextRead>(1);
	private ArrayList<ComboBox> cmbStroreLocation = new ArrayList<ComboBox>();
	private ArrayList<TextField> txtremarks = new ArrayList<TextField>();
	


	private Label label = new Label();
	private Label l1 = new Label();
	private Label l2 = new Label();
	boolean isUpdate=false,isFind=false;
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	OptionGroup Loantype;
	private static final List<String>areatype  = Arrays.asList(new String[] {"Production" });

	public RawmaterialIssueReturn(SessionBean sessionBean) {

		this.sessionBean=sessionBean;
		this.setCaption("ISSUE RETURN ENTRY::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout1);
		componentIni(true);
		btnIni(true);
		setEventAction();
		issueTo();
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(txtchallanNo);
		allComp.add(dreturnDate);
		allComp.add(cmbrteturnFrom);
		allComp.add(cmbIssueNo);
		for(int i=0;i<cmbProduct.size();i++)
		{
			allComp.add(cmbProduct.get(i));
			allComp.add(txtissueRturnQty.get(i));
			allComp.add(cmbStroreLocation.get(i));
			allComp.add(txtremarks.get(i));
		}
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);

		new FocusMoveByEnter(this,allComp);

	}


	private AbsoluteLayout buildMainLayout(){

		mainLayout1 = new AbsoluteLayout();
		mainLayout1.setImmediate(false);
		mainLayout1.setWidth("1180px");
		mainLayout1.setHeight("560px");
		mainLayout1.setMargin(false);

		// top-level component properties
		setWidth("1220px");
		setHeight("650px");
		
		lbltransactionno.setWidth("-1px");
		lbltransactionno.setHeight("-1px");
		lbltransactionno.setImmediate(false);
		mainLayout1.addComponent(lbltransactionno,"top:20.0px;left:15.0px;");

		txttransactionNo.setWidth("100px");
		txttransactionNo.setHeight("24px");
		txttransactionNo.setImmediate(true);
		mainLayout1.addComponent(txttransactionNo,"top:18.0px;left:130.0px");
		
		lblchallanNo.setWidth("-1px");
		lblchallanNo.setHeight("-1px");
		lblchallanNo.setImmediate(false);
		mainLayout1.addComponent(lblchallanNo,"top:46.0px;left:15.0px;");

		txtchallanNo.setWidth("100px");
		txtchallanNo.setHeight("24px");
		txtchallanNo.setImmediate(true);
		mainLayout1.addComponent(txtchallanNo,"top:44.0px;left:130.0px");
		
		
		lblreturnDate.setWidth("-1px");
		lblreturnDate.setHeight("-1px");
		lblreturnDate.setImmediate(false);
		mainLayout1.addComponent(lblreturnDate,"top:72.0px;left:15.0px;");


		dreturnDate.setWidth("108px");
		dreturnDate.setHeight("24px");
		dreturnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dreturnDate.setDateFormat("dd-MM-yyyy");
		dreturnDate.setValue(new java.util.Date());
		dreturnDate.setImmediate(true);
		mainLayout1.addComponent(dreturnDate,"top:70.0px;left:130.0px");
		
		
		lblreturnForm.setWidth("-1px");
		lblreturnForm.setHeight("-1px");
		lblreturnForm.setImmediate(false);
		mainLayout1.addComponent(lblreturnForm,"top:20.0px;left:260.0px;");

		cmbrteturnFrom.setWidth("250px");
		cmbrteturnFrom.setNewItemsAllowed(true);
		cmbrteturnFrom.setNullSelectionAllowed(true);
		cmbrteturnFrom.setImmediate(true);
		cmbrteturnFrom.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout1.addComponent(cmbrteturnFrom,"top:18.0px;left:350px;");

		
		lblIssueNo.setWidth("-1px");
		lblIssueNo.setHeight("-1px");
		lblIssueNo.setImmediate(false);
		mainLayout1.addComponent(lblIssueNo,"top:45.0px;left:260.0px;");

		cmbIssueNo.setWidth("150px");
		cmbIssueNo.setNewItemsAllowed(true);
		cmbIssueNo.setNullSelectionAllowed(true);
		cmbIssueNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout1.addComponent(cmbIssueNo,"top:44.0px;left:350px;");
		
		
		
		table.setWidth("100%");
		table.setHeight("360px");
		table.setFooterVisible(true);

		table.addContainerProperty("Product", ComboBox.class , new ComboBox());
		table.setColumnWidth("Product",350);
		table.addContainerProperty("Unit", TextRead.class , new TextRead());
		table.setColumnWidth("Unit",80);
		table.addContainerProperty("Issue Qty", TextRead.class , new TextRead());
		table.setColumnWidth("Issue Qty",80);
		table.addContainerProperty("Return Qty", AmountField.class , new AmountField());
		table.setColumnWidth("Return Qty",80);
		table.addContainerProperty("Rate", TextRead.class , new TextRead());
		table.setColumnWidth("Rate",80);
		table.addContainerProperty("Amount", TextRead.class , new TextRead());
		table.setColumnWidth("Amount",80);
		table.addContainerProperty("Store Location", ComboBox.class , new ComboBox());
		table.setColumnWidth("Store Location",180);
		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",130);
		table.setColumnFooter("Rate", "Total");
				
		
		
		table.setColumnCollapsingAllowed(true);
		tableInitialise();
		mainLayout1.addComponent(table,"top:120.0px;left:0.0px;");

		mainLayout1.addComponent(lbLine,"top:490px;left:20.0px;");
		mainLayout1.addComponent(cButton,"top:520px;left:250px;");

		return mainLayout1;	
	}
/*	private void cmbGroupData() {
		cmbGroupType.removeAllItems();
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List list = session.createSQLQuery("select 0,vCategoryType from tbRawItemCategory").list();

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
	}*/
	
	private void productDataAdd (int ar) 
	{ 

		cmbProduct.get(ar).removeAllItems();
		
		Transaction tx=null;
		String query="";

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			
			if(!isFind)
			{
				query=    " select a.ProductID,b.vRawItemName  from tbRawIssueDetails a "                                                                                                                    
						+"inner join "
						+"tbRawItemInfo b "
						+"on a.ProductID=b.vRawItemCode "
						+"where a.IssueNo like    '"+cmbIssueNo.getValue().toString()+"'  and returnFlag  like '1'  ";	
			}
			
			if(isFind)
			{
				query=    " select a.ProductID,b.vRawItemName  from tbRawIssueDetails a "                                                                                                                    
						+"inner join "
						+"tbRawItemInfo b "
						+"on a.ProductID=b.vRawItemCode "
						+"where a.IssueNo like    '"+cmbIssueNo.getValue().toString()+"'  ";	
			}
			

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbProduct.get(ar).addItem(element[0].toString());
				cmbProduct.get(ar).setItemCaption(element[0].toString(), (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void issueTo(){

		cmbrteturnFrom.removeAllItems();
		Transaction tx=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery("select AutoID,SectionName from tbSectionInfo where AutoID in(select IssuedTo from tbRawIssueInfo)").list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbrteturnFrom.addItem(element[0].toString());
				cmbrteturnFrom.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}
	
	
	private void issueDataLoad(){

		cmbIssueNo.removeAllItems();
		cmbIssueNo.setValue(null);
		Transaction tx=null;
		String query=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			
			if(!isFind)
			{
				query=  "select distinct 0, a.IssueNo from tbRawIssueInfo a "
						+"inner join "
						+"tbRawIssueDetails b "
						+"on "
						+"a.IssueNo=b.IssueNo "
						+"where b.returnFlag like '1' and a.IssuedTo like '"+cmbrteturnFrom.getValue().toString()+"' ";	
			}
			
			if(isFind)
			{
				query=  "select distinct 0, a.IssueNo from tbRawIssueInfo a "
						+"inner join "
						+"tbRawIssueDetails b "
						+"on "
						+"a.IssueNo=b.IssueNo "
						+"where  a.IssuedTo like '"+cmbrteturnFrom.getValue().toString()+"' ";		
			}
		

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();

				cmbIssueNo.addItem(element[1].toString());
				
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void cmbIssueData(){
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List list = session.createSQLQuery("select * from tbRawIssueInfo").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{

				Object[] element = (Object[]) iter.next();
				cmbrteturnFrom.addItem(element[1]);

			}
		}catch(Exception exp){
			getParent().showNotification(
					"Error1",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			//tx.rollback();
		}

	}

	private void cmbProductAdd(){


		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
/*			String sql="select vRawItemCode,vRawItemName,subString(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName))as category,"
					+" vSubSubCategoryName from tbRawItemInfo where vCategoryType like '%' and vRawItemCode in(select productId from tbRawProductOpening union select ProductID from tbRawPurchaseDetails) order by category,vSubSubCategoryName";
			*/
			
			String sql= "select a.vRawItemCode ,a.vRawItemname, SUBSTRING(b.vSubGroupName,CHARINDEX('-',b.vSubGroupName)+1,LEN(b.vSubGroupName) ) as category  from  tbStandardFinishedDetails a "
					    +"inner join "
					    +"tbRawItemInfo b "
					    +" on  a.vRawItemCode=b.vRawItemCode ";
					  
			
	
			System.out.println("sql is"+sql);
			
			//cmbProduct.removeAll(cmbProduct);
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
	private boolean returnqtycheck()
	{
		for(int i=0;i<cmbProduct.size();i++)
		{
		   if(!txtissueRturnQty.get(i).getValue().toString().isEmpty())
		   {
			  return true;   
		   }
		}
		
		return false;
		
	}
	
	private boolean productdataCheck(){

		for(int i=0;i<cmbProduct.size();i++)
		{
		   if(cmbProduct.get(i).getValue()!=null)
		   {
			  return true;   
		   }
		}
		
		return false;
	}
	
	
	
	
	public void setEventAction(){


		/*cmbProductionType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductionType.getValue()!=null){

					productionStepData();
				}
			}
		});*/
	/*	cmbProductionStep.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductionStep.getValue()!=null){
					finishedGoodsData();
				}
			}
		});*/
		
		
		cmbrteturnFrom.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbrteturnFrom.getValue()!=null)
				{
					issueDataLoad();
				}
			}
		});
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{

				newButtonEvent();
				autotxttransactionNo();
				isFind=false;

			}

		});
		
		cmbIssueNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbIssueNo.getValue()!=null)
				{
					for(int i=0;i<cmbProduct.size();i++)
					{
						productDataAdd(i);
					}
				}
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtchallanNo.getValue().toString().isEmpty())
				{
					if(cmbrteturnFrom.getValue()!=null)
					{
						if(cmbIssueNo.getValue()!=null)
								{
									if(productdataCheck())
									{
										if(returnqtycheck()){
											
											saveButtonEvent();
											isFind=false;
											isUpdate=false;
										}
										else{
											showNotification("Warning :","Please Provide Issue Return Qty", Notification.TYPE_WARNING_MESSAGE);
										}
									}
									else{
										showNotification("Warning :","Select Product Name",Notification.TYPE_WARNING_MESSAGE);
									}
								}
								else
								{
									showNotification("Warning :","Select Issue No",Notification.TYPE_WARNING_MESSAGE);	
								}

					}
					else
					{
						showNotification("Warning :","Please Select Section Name",Notification.TYPE_WARNING_MESSAGE);	
					}

				}
				else{
					showNotification("Please Provide Challan No",Notification.TYPE_WARNING_MESSAGE);
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
				isFind=false;
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				isFind=false;

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
		
		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txttransactionNo.getValue().toString().isEmpty())

				{
					reportShow();
				}

				else
				{
					showNotification("Warning!","Find a Issue No to genarate Challan",Notification.TYPE_WARNING_MESSAGE);	
				}


			}
		});


	}
	
	
	private void reportShow()
	{
		String query=null;
		String query1=null;
		String activeFlag = null;

		String sectionId= cmbrteturnFrom.getItemCaption(cmbrteturnFrom.getValue());
		//String demandNo=cmbdemandNo.getItemCaption(cmbdemandNo.getValue());

		if(sectionId.equals("All")){
			sectionId="%";
		}else{
			sectionId = cmbrteturnFrom.getValue().toString();
		}
		
	

		try{

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			//String FromDate = new SimpleDateFormat("yyyy-MM-dd").format(fromDate.getValue());
			//String ToDate =  new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue());

			//System.out.println("From Date : "+FromDate+", To Date : "+ToDate);

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			//hm.put("phone", "Phone: "+sessionBean.getCompanyPhone()+"   Fax:  "+sessionBean.getCompanyFax()+",   E-mail:  "+sessionBean.getCompanyEmail());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIP",sessionBean.getUserIp());
			//hm.put("fromDate", new SimpleDateFormat("dd-MM-yyyy").format(dateField.getValue()));
			//hm.put("toDate",new SimpleDateFormat("dd-MM-yyyy").format(dateField.getValue()));

			//query="select * from [funcRawIssueRegister]('"+FromDate+"','"+ToDate+"','"+sectionId+"')";
			/*query="select a.sectionReqNo, a.sectionId, a.date as reqdate, b.productId, b.productName, b.reqQty, a.sectionName, c.Date as issuedate, d.Qty, e.Unit "
					+"from tbRawRequisitioninfo a inner join tbRawRequisitionDetails b on a.reqNo=b.reqNo inner join tbRawIssueInfo "
					+"c on c.vDemandNo=a.sectionReqNo left join tbRawIssueDetails d on d.ProductID=b.productId inner join tbRawProductInfo e on "
					+"e.ProductCode=b.productId where a.sectionId like '"+cmbSectionName.getValue().toString()+"' and c.vDemandNo like '"+cmbdemandNo.getValue().toString()+"'";*/

			/*query="select isi.Date as issuedate, isi.txttransactionNo, ri.sectionName, ri.sectionReqNo, ri.date as demanddate, "
				 +"pin.ProductCode  as ProductID, pin.ProductName, pin.Unit, rd.reqQty, isd.Qty, isd.Rate, isd.Rate*isd.Qty "
				 +"as amount from tbRawIssueInfo isi inner join tbRawIssueDetails isd on isi.txttransactionNo=isd.txttransactionNo inner join "
				 +"tbRawProductInfo pin on pin.ProductCode=isd.ProductID inner join tbRawRequisitionInfo ri on "
				 +"ri.sectionReqNo=isi.vDemandNo inner join tbRawRequisitionDetails rd on isd.ProductID=rd.productId "
				 +"and rd.sectionReqNo=ri.sectionReqNo and ri.sectionId=isi.IssuedTo where isi.Date >= '"+new SimpleDateFormat("yyyy-MM-dd").format(fromDate.getValue())+"' and "
				 +"isi.Date <='"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"' and ri.sectionId like '"+sectionId+"' and ri.sectionReqNo like '"+demandNo+"' Order by isi.txttransactionNo, "
				 +"isi.Date, ri.sectionReqNo, ri.date ";*/
/*			
			query="select CONVERT(date,isi.Date,105)as issuedate, isi.txttransactionNo, isi.IssuedTo, si.SectionName, isi.vDemandNo,CONVERT(date,isi.Date,105)as "
				 +"demanddate, pin.ProductCode  as ProductID, pin.ProductName, pin.Unit, isd.demandQty, isd.Qty, isd.Rate, isd.Rate*isd.Qty "
				 +"as amount from tbRawIssueInfo isi inner join tbRawIssueDetails isd on isi.txttransactionNo=isd.txttransactionNo inner join tbRawProductInfo "
				 +"pin on pin.ProductCode=isd.ProductID inner join tbSectionInfo si on si.AutoID=isi.IssuedTo where isi.Date >= '"+new SimpleDateFormat("yyyy-MM-dd").format(fromDate.getValue())+"' and "
				 +"isi.Date <='"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"' and isi.IssuedTo like '"+sectionId+"' and isi.vDemandNo like '"+demandNo+"'Order by cast(SUBSTRING(isi.txttransactionNo,4,LEN(isi.txttransactionNo)) as int), CONVERT(date,isi.Date,105), isi.vDemandNo";*/
			
		/*	query = "select a.txttransactionNo,a.IssuedTo,c.SectionName,a.Date,a.challanNo,b.ProductID,d.vRawItemName,a.ProductionType,e.productTypeName ,a.productionStep,f.StepName ,a.finishedGoods,b.Qty,b.Rate,g.vProductName from tbRawIssueInfo a" +
					" inner join" +
					" tbRawIssueDetails b" +
					" on a.txttransactionNo=b.txttransactionNo" +
					" inner join" +
					" tbSectionInfo c" +
					" on c.AutoID=a.IssuedTo" +
					" inner join" +
					" tbRawItemInfo d" +
					" on d.vRawItemCode=b.ProductID" +
					" left join" +
					" tbProductionType e" +
					" on e.productTypeId=a.ProductionType" +
					" left join" +
					" tbProductionStep f" +
					" on f.StepId=a.productionStep" +
					" left join" +
					" tbFinishedProductInfo g" +
					" on g.vProductId=a.finishedGoods" +
					" where  CONVERT(Date,a.Date,105)  between  '"+new SimpleDateFormat("yyyy-MM-dd").format(dateField.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(dateField.getValue())+"' " +
					" and a.challanNo like '"+txtChallanNo.getValue().toString()+"' and a.IssuedTo like '"+sectionId+"' " +
					" order by a.Date, cast(a.txttransactionNo as int)";*/
			
			System.out.println("Desire Query Is"+query);
			hm.put("sql", query);

			//query1="select [dbo].[number](sum(a.Qty*a.Rate)) from tbrawIssueDetails a inner join tbrawIssueInfo b on a.txttransactionNo=b.txttransactionNo where b.IssuedTo like '"+sectionId+"' and b.vDemandNo like '"+demandNo+"'";
			
			//List lst1=session.createSQLQuery(query1).list();
			
			//hm.put("InWords", lst1.iterator().next().toString());
			
			List lst=session.createSQLQuery(query).list();
			
			if(!lst.isEmpty())
			{
				Window win = new ReportViewer(hm,"report/raw/rptDepartmentWiseIssue.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
				win.setCaption("DEPARTMENT WISE ISSUE :: "+sessionBean.getCompany());

				this.getParent().getWindow().addWindow(win);
			}
			
			else
			{
				
				getParent().showNotification("Date Not Found", Notification.TYPE_WARNING_MESSAGE);
				
			}
			
			/*Window win = new ReportViewer(hm,"report/raw/rptDeptWiseIssue.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",0);
			win.setCaption("Report : Department Wise Issue");
			this.getParent().getWindow().addWindow(win);*/
		}
		catch(Exception exp){

			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);

		}
	}
	
	
	
	

	/*private void finishedGoodsData(){
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();  
		String quer="  select vProductId,vProductName from tbFinishedProductInfo";
		List lst = session.createSQLQuery(quer).list();

		for(  Iterator iter=lst.iterator();iter.hasNext(); )
		{
			Object [] element = (Object []) iter.next();

			cmbFinishGoods.addItem(element[0]);
			cmbFinishGoods.setItemCaption(element[0], element[1].toString());  
		}
	}*/
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

			cmbrteturnFrom.addItem(element[0]);
			cmbrteturnFrom.setItemCaption(element[0], element[1].toString());  
		}


	}




	/*private void ReportView()
	{

		String txttransactionNo;
		txttransactionNo=txttransactionNo.getValue().toString().trim();
		System.out.print(txttransactionNo);

		String query=null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("comName", sessionBean.getCompanyName());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("reportName","RAW MATERIAL RETURN REGISTER");
			//hm.put("FromDate", new SimpleDateFormat("dd-MM-yyyy").format(fromDate.getValue()));
			//hm.put("ToDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("userName", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			//query="select * from VwRawReceivedReturn where ProductID like '"+cmbId + "%' and Qty>0 and Date between '"+new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())+"' and '"+new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())+"' order by ProductName,date";
			//query= "select tb.Unit, rpi.ChallanNo, sd.SupplierName, tb.ProductName ,rpi.SupplierId, rpd.ProductID, rpd.Date, rpd.Amount as Rate, rpd.Qty, (rpd.Qty * rpd.Amount) as Amount from   tbRawPurchaseReturnDetails rpd  inner join tbRawPurchaseReturn rpr on rpd.ReceiptNo=rpr.ReceiptNo inner join tbRawPurchaseInfo rpi on rpi.ReceiptNo=rpd.ReceiptNo inner join tbRawProductInfo tb on tb.ProductCode=rpd.ProductID inner join tbSupplierDetails sd on sd.SupplierID=rpi.SupplierId where CONVERT(date,rpd.Date,105) between '"+new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())+"'  and  '"+new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())+"' and rpd.ProductID like '%'";

			query = "select rc.SectionName , rc.Address,rp.Unit, CONVERT(date,rd.Date,105 )as 'Date', rd.txttransactionNo, rd.IssuedTo ,rd.IssueType, rs.ProductID ,rp.ProductName,  rs.Qty,rs.remarks,rd.challanNo from tbRawIssueInfo rd  inner join  tbRawIssueDetails rs on rd.txttransactionNo=rs.txttransactionNo  inner join tbRawProductInfo rp on rp.ProductCode=rs.ProductID inner join tbSectionInfo rc on rc.AutoID=rd.IssuedTo where rd.txttransactionNo='"+txttransactionNo.getValue().toString().trim()+"' ";
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



	private String autotxttransactionNo() 
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "select  ISNULL(MAX(CAST( transactionNo as int) ),0)+1  from tbIssueReturnInfo";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				System.out.println("I Am Rabiul Hasan");
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}



	private void findButtonEvent(){

		Window win=new IssueReturnFind(sessionBean,txtReceiptId,"rawIssue");
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

	}

	private void findInitialise(){

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();


			txtClear();
			String sql= " select transactionNo,transactionDate,challanNo,issueNo,VoucherNo,total,returnForm from tbIssueReturnInfo  "
					    +" where transactionNo like '"+txtReceiptId.getValue().toString()+"' ";

			List led=session.createSQLQuery(sql).list();
			
			if(led.iterator().hasNext()){

				Object[] element = (Object[]) led.iterator().next();


				txttransactionNo.setValue(element[0]);
				dreturnDate.setValue(element[1]);
				txtchallanNo.setValue(element[2]);
				cmbrteturnFrom.setValue(element[6].toString());
				cmbIssueNo.setValue(element[3].toString());
				txtvoucherno.setValue(element[4].toString());
				
			}


			List list=session.createSQLQuery("select  productId,qty,storelocation   from tbIssueReturnDetails where transactionNo like '"+txttransactionNo.getValue().toString()+"' ").list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();

				fmt = new Formatter();
				cmbProduct.get(i).setValue(element[0].toString());
				txtissueRturnQty.get(i).setValue(decimalf.format(element[1]));
				cmbStroreLocation.get(i).setValue(element[2].toString());

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
	
	private void deleteButtonEvent()
	{
		if(txttransactionNo.getValue()!= null)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						Transaction tx= session.beginTransaction();
						if(deleteData(session,tx)){
							tx.commit();
							//txtClear();
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
			});
		}
		else
			this.getParent().showNotification(
					"Delete Failed",
					"There are no data for delete.",
					Notification.TYPE_WARNING_MESSAGE);
	}

	public boolean deleteData(Session session,Transaction tx){


		try{
			session.createSQLQuery("delete tbIssueReturnInfo  where transactionNo='"+txttransactionNo.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete tbIssueReturnDetails  where transactionNo='"+txttransactionNo.getValue()+"'").executeUpdate();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dreturnDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			session.createSQLQuery("delete "+voucher+"  where Voucher_No='"+txtvoucherno.getValue()+"'").executeUpdate();
			return true;

		}
		catch(Exception exp){

			this.getParent().showNotification("Error4",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}


	}

	private void updateButtonEvent(){

		if(cmbrteturnFrom.getValue()!= null)
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
		if(isUpdate)
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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
						if(deleteData(session,tx) )
							insertData(session,tx);
						else{
							tx.rollback();
						}
						isUpdate=false;
						txtClear();
						mb.close();
					}
				}
			});
		}
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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
						cButton.btnNew.focus();
						mb.close();
					}
				}
			});
		}
	}



	/*	private boolean nullCheck(){

		if(cmbrteturnFrom.getValue()!=null){

			for (int i = 0; i < cmbProduct.size(); i++)
			{
				Object temp = cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue());
				System.out.println(cmbProduct.get(i).getValue());
				if (temp != null && !cmbProduct.get(i).getValue().equals(("x#"+i)))
				{
					if(!amount.get(i).getValue().toString().trim().isEmpty())
					{

						return true;
					}
					else{

						this.getParent().showNotification("Warning :","Please Enter Valid Issue Qty .", Notification.TYPE_WARNING_MESSAGE);
					}

				}
			}
		}
		else{
			this.getParent().showNotification("Warning :","Please Select Issue To .", Notification.TYPE_WARNING_MESSAGE);

		}


		return false;
	}*/





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

			String voucharType = "";
			String sqlInfo="",sqlDetailsnew="",sqlUdInfo="",sqlUdDetails="",  vocherId="";
			
			if(!isUpdate)
			{
				vocherId=vocherIdGenerate();	
			}

			else
			{
				vocherId=txtvoucherno.getValue().toString();	
			}

			String RawmaterialLedger ="";

			voucharType = Option();
			Double total=  Double.parseDouble(table.getColumnFooter("Amount")) ;
			String issueno="";
			String sectionm="";
			String challanNo="";
			issueno=cmbIssueNo.getValue().toString();
			sectionm=cmbrteturnFrom.getValue().toString();
			challanNo=txtchallanNo.getValue().toString();
		
			
			
			String sql= "  insert into tbIssueReturnInfo(transactionNo,transactionDate,challanNo,issueNo,returnForm,userIp,entryTime,VoucherNo,VoucherType,total,userid)   "
					  + " values ('"+txttransactionNo.getValue().toString()+"','"+datef.format(dreturnDate.getValue()) +"', "
					  +"  '"+challanNo+"','"+issueno+"','"+sectionm+"','"+sessionBean.getUserIp()+"',getdate(),'"+vocherId+"','"+voucharType+"','"+total+"','"+sessionBean.getUserName()+"')   ";


			System.out.println(sql);
			System.out.println("Done");
			session.createSQLQuery(sql).executeUpdate();

			for (int i = 0; i < cmbProduct.size(); i++)
			{
			
				
				if (cmbProduct.get(i).getValue()!=null && ! txtissueRturnQty.get(i).getValue().toString().isEmpty())
				{
					String productId =cmbProduct.get(i).getValue().toString().trim();
					System.out.println("Code: "+productId+" Caption: "+cmbProduct.get(i).getItemCaption(productId));
					Double Amount =  Double.parseDouble(txtrate.get(i).getValue().toString())*Double.parseDouble(txtissueRturnQty.get(i).getValue().toString()); 
					String StroreId="";
					if(cmbStroreLocation.get(i).getValue()!=null)
							
					{
						StroreId= (String) cmbStroreLocation.get(i).getValue(); 	
					}
					String sqlDetails= " insert  into tbIssueReturnDetails (transactionNo,productId,qty,rate,storelocation,remarks,userIp,userid,entryTime,amount) "
					                  +"values('"+txttransactionNo.getValue().toString()+"','"+cmbProduct.get(i).getValue().toString()+"','"+txtissueRturnQty.get(i).getValue().toString()+"','"+txtrate.get(i).getValue().toString()+"','"+cmbStroreLocation.get(i).getValue().toString()+"','"+txtremarks.get(i).getValue().toString()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',getdate(),'"+Amount+"'  ) ";
					
					session.createSQLQuery(sqlDetails).executeUpdate();


					String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dreturnDate.getValue())+"')").list().iterator().next().toString();
					String voucher =  "voucher"+fsl;

					System.out.println("Receipt Data"+voucher);

					String naration="Section :"+cmbrteturnFrom.getItemCaption(cmbrteturnFrom.getValue()).toString()+" "+"Ref No :"+txttransactionNo.getValue().toString()+" "+"Issue Date :"+new SimpleDateFormat("yyyy-MM-dd").format(dreturnDate.getValue()).toString();

					if(i==0)
					{
						String SupplierVoucherquery=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,TransactionWith) "
								+" values('"+vocherId+"','"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dreturnDate.getValue())+"','EL5', "  
								+" '"+naration+"','' , "
								+" '"+total+"','jau','U-1','1' ,'"+sessionBean.getUserId()+"', "
								+" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "
								+" '2', '"+sessionBean.getCompanyId()+"','' ,'"+cmbrteturnFrom.getItemCaption(cmbrteturnFrom.getValue())+"') ";

						session.createSQLQuery(SupplierVoucherquery).executeUpdate();
						System.out.println("Supplier"+SupplierVoucherquery);

					}


					String proid =cmbProduct.get(i).getValue().toString().trim();

					String ProductLedeger="";
					ProductLedeger= productlededger(i);


					String purchasevoucherquery=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,TransactionWith) "
							+" values('"+vocherId+"','"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dreturnDate.getValue())+"','"+ProductLedeger+"', "  
							+" '"+naration+"','"+txtrAmount.get(i).getValue().toString()+"' , "
							+" '0','jau','U-1','1' ,'"+sessionBean.getUserId()+"', "
							+" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "
							+" '2', '"+sessionBean.getCompanyId()+"' ,'','"+cmbrteturnFrom.getItemCaption(cmbrteturnFrom.getValue())+"' ) ";

					session.createSQLQuery(purchasevoucherquery).executeUpdate();
					System.out.println("purchae"+purchasevoucherquery);
					
					String query= " select 0, ISNULL(SUM(qty),0) as total  from  tbIssueReturnDetails a  " 
							    + " inner join " 
							     +" tbIssueReturnInfo b on a.transactionNo=b.transactionNo  " 
							    + " where  a.productId like '"+cmbProduct.get(i).getValue().toString()+"' and  b.issueNo like '"+cmbIssueNo.getValue().toString()+"'   " ;

                        Double rtnqty=0.00;
                        List lst=session.createSQLQuery(query).list();
                        if(!lst.isEmpty())
                        {
                        	Iterator iter=lst.iterator();
                        	if(iter.hasNext())
                        	{
                        	  Object[] element=(Object[]) iter.next();
                        	  rtnqty= Double.parseDouble(element[1].toString());
                        	}
                        	
                        	
                        }
                        
                        if(rtnqty== Double.parseDouble(txtissueqty.get(i).getValue().toString()) )
                        {
                           String sql1= "   update  tbRawIssueDetails set returnFlag='0' where ProductID like '"+cmbProduct.get(i).getValue().toString()+"' and IssueNo like '"+cmbIssueNo.getValue().toString()+"' ";	
                           session.createSQLQuery(sql1).executeUpdate();
                        }
					
				}
			}
			
			
			
			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			txtClear();
			componentIni(true);
			btnIni(true);
			isFind=false;

		}catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error5",exp+"",Notification.TYPE_ERROR_MESSAGE);

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


	private void newButtonEvent() 
	{

		componentIni(false);
		btnIni(false);
		txtClear();
		txtchallanNo.focus();
		txttransactionNo.setValue(autotxttransactionNo());

	}

	/*public String vocherIdGenerate()
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
	}*/
	public String vocherIdGenerate()
	{
		String vo_id = null;
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+datef.format(dreturnDate.getValue())+"')").list().iterator().next().toString();
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

	public void txtClear(){

		txttransactionNo.setValue("");
		txtchallanNo.setValue("");
		dreturnDate.setValue(new java.util.Date());
		cmbrteturnFrom.setValue(null);
		cmbIssueNo.setValue(null);
		
		for(int i=0;i<cmbProduct.size();i++)
		{

			cmbProduct.get(i).setValue(null);
			unit.get(i).setValue("");
			txtissueqty.get(i).setValue("");
			txtissueRturnQty.get(i).setValue("");
			txtrate.get(i).setValue("");
			txtrAmount.get(i).setValue("");
			cmbStroreLocation.get(i).setValue(null);
			txtremarks.get(i).setValue("");
			
		}

		
	}

	private void componentIni(boolean b) {

		cmbrteturnFrom.setEnabled(!b);
		txttransactionNo.setEnabled(!b);
		cmbIssueNo.setEnabled(!b);
		dreturnDate.setEnabled(!b);
		txtchallanNo.setEnabled(!b);
		lbLine.setEnabled(!b);
		table.setEnabled(!b);

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

		for(int i=0;i<11;i++)
		{
			tableRowAdd(i);
		}
	}

	private boolean doubleEntryCheck(String caption,int row)
	{

		for(int i=0;i<cmbProduct.size();i++)
		{

			if(i!=row && caption.equals(cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue())))
			{
				return false;
			}

		}
		return true;
	}

	/*public void proComboChange(String headId,int r)
	{

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String productId =cmbProduct.get(r).getValue().toString().trim();
			//productId = productId.substring(0, productId.indexOf('#'));

			System.out.println("Mezbah");

			//			List list = session.createSQLQuery("select ItemCode,ItemName,ClosingQty,ClosingRate from tbRawGodownStockReport  where ItemCode='"+headId+"'").list();
			List list = session.createSQLQuery("select * from dbo.[funRawMaterialsStock]('"+dateformat.format(dateField.getValue())+" 23:59:59','"+productId+"')").list();

			int cmbflag=0;
			if(list.iterator().hasNext()){

				Object[] element = (Object[]) list.iterator().next();
				cmbflag=1;
				fmt = new Formatter();
				rate.get(r).setValue(fmt.format("%.2f",element[22]));
				fmt = new Formatter();
				stockQty.get(r).setValue(fmt.format("%.2f",element[21]));
				qty.get(r).setValue("");
				amount.get(r).setValue("");
				unit.get(r).setValue(element[3].toString());

			}

			System.out.println("a"+stockQty.get(r).getValue());

			if(!cmbProduct.get(r).getItemCaption(cmbProduct.get(r).getValue()).equals("") && stockQty.get(r).getValue().equals("")){

				getParent().showNotification("Warnning","Stock is not Available!",Notification.TYPE_ERROR_MESSAGE);

				Object checkNull=(Object)cmbProduct.get(r).getItemCaption(cmbProduct.get(r).getValue());

				if(!checkNull.equals("")){

					cmbProduct.get(r).setValue(null);
				}
			}


		}catch(Exception exp){
			this.getParent().showNotification(
					"Error6",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			//tx.rollback();
		}

	}*/

/*	private void tableColumnAction(final String head,final int r)
	{

		qty.get(r).setImmediate(true);
		qty.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);
		qty.get(r).setTextChangeTimeout(100);
		qty.get(r).addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub

				try{
					System.out.println("\n"+"column Action");
					System.out.println(head);
					if(head.equalsIgnoreCase("x"))
					{
						amount.get(r).setValue("");


					}
					else
					{
						//System.out.println("Error Here.....111");
						double tbquntity;
						double tamount,unitPrice;

						tbquntity = event.getProperty().toString().trim().isEmpty()? 0: Double.parseDouble(event.getProperty().toString().trim());

						String stockQ = stockQty.get(r).getValue().toString().isEmpty()?"0":stockQty.get(r).getValue().toString();
                        
						
						
						if(!isFind)
						{
						   
							if(Double.parseDouble(stockQ) >= tbquntity)
							{
								String tempPrice=rate.get(r).getValue().toString().isEmpty()?"0":rate.get(r).getValue().toString();
								unitPrice=Double.parseDouble(tempPrice);
								//System.out.println("Error Here.....3333");
								tamount=unitPrice*tbquntity;
								fmt = new Formatter();
								amount.get(r).setValue(decimalf.format(tamount));
							}
							else{
								
									getParent().showNotification("Warnning","Stock Qty can not be exceed Qty. Stock Qty "+stockQty.get(r).getValue().toString(),
											Notification.TYPE_WARNING_MESSAGE);
									qty.get(r).setValue("");
								
							}
						
						}
						
						
						else
						{
						    
							String tempPrice=rate.get(r).getValue().toString().isEmpty()?"0":rate.get(r).getValue().toString();
							unitPrice=Double.parseDouble(tempPrice);
							//System.out.println("Error Here.....3333");
							tamount=unitPrice*tbquntity;
							fmt = new Formatter();
							amount.get(r).setValue(decimalf.format(tamount));
							
						}
						
						totalField.setImmediate(true);
						totalsum=0.0;
						for(int flag=0;flag<amount.size();flag++)
						{							
							if(amount.get(flag).getValue().toString().trim().length()>0 && !amount.get(flag).getValue().toString().isEmpty())
							{
								String flagbit = amount.get(flag).getValue().toString();
								totalsum=totalsum+Double.parseDouble(flagbit);//flagbit;
							}
						}
						fmt = new Formatter();
						totalField.setValue(fmt.format("%.2f",totalsum));						
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
		});


	}*/

	public void tableRowAdd(final int ar)
	{

		final Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query="";

		try
		{
			
			cmbProduct.add(ar,new ComboBox());
			cmbProduct.get(ar).setWidth("100%");
			cmbProduct.get(ar).setImmediate(true);
			cmbProduct.get(ar).setNullSelectionAllowed(false);
			
			unit.add(ar,new TextRead(1));
			unit.get(ar).setWidth("100%");
			
			txtissueqty.add(ar,new TextRead(1));
			txtissueqty.get(ar).setWidth("100%");
			
			txtissueRturnQty.add( ar , new AmountField());
			txtissueRturnQty.get(ar).setWidth("100%");
			txtissueRturnQty.get(ar).setImmediate(true);
			
			txtissueRturnQty.get(ar).addListener(new ValueChangeListener() 
			{
				
				public void valueChange(ValueChangeEvent event) 
				{
					 Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					Transaction tx=session.beginTransaction();
					
					if(!txtissueRturnQty.get(ar).getValue().toString().isEmpty())
					{
						System.out.println("Shoaib Akhter");
						String query="";
						Double returnqty=0.00;
						Double presentreturnqty=0.00;
						Double totalreturnqty=0.00;
						Double IssueQty=0.00;
						Double Amount=0.00;
						Double rate=0.00;
						
						if(!isFind)
						{
							query=  " select 0, ISNULL(SUM(qty),0) as amount  from tbIssueReturnDetails a "
									+"inner join "
									+"tbIssueReturnInfo b "
									+"on "
									+"b.transactionNo=a.transactionNo "
									+"where b.issueNo like '"+cmbIssueNo.getValue().toString()+"' and a.productId like '"+cmbProduct.get(ar).getValue().toString()+"'  ";
									
						}
						
						if(isFind)
						{
							System.out.println("You Are Ok");
							
							query=  " select 0, ISNULL(SUM(qty),0) as amount  from tbIssueReturnDetails a "
									+"inner join "
									+"tbIssueReturnInfo b "
									+"on "
									+"b.transactionNo=a.transactionNo "
									+"where b.issueNo like '"+cmbIssueNo.getValue().toString()+"' and a.productId like '"+cmbProduct.get(ar).getValue().toString()+"' and a.transactionNo not like '"+txttransactionNo.getValue().toString()+"' ";
								
						}
						
					
						List lst=session.createSQLQuery(query).list();
						Iterator iter=lst.iterator();
						if(iter.hasNext())
						{
							System.out.println("Nothing");
							Object[]elemtnt=(Object[]) iter.next();
							returnqty=Double.parseDouble(elemtnt[1].toString()) ;  
						}
						System.out.println("Return is"+returnqty);
						presentreturnqty= Double.parseDouble(txtissueRturnQty.get(ar).getValue().toString());
						IssueQty= Double.parseDouble(txtissueqty.get(ar).getValue().toString());
						totalreturnqty=presentreturnqty+returnqty;
						System.out.println("Total is"+totalreturnqty);
						
						
						if(totalreturnqty<=IssueQty )
						{
							if( !txtrate.get(ar).getValue().toString().isEmpty())
							{
								rate= Double.parseDouble(txtrate.get(ar).getValue().toString()) ;
								Amount=rate*presentreturnqty;
								txtrAmount.get(ar).setValue(Amount);
							}
						
							
						}
						
						else
						{
							showNotification("Return Qty Must Not Exceed Issuq Qty",Notification.TYPE_WARNING_MESSAGE);
							txtissueRturnQty.get(ar).setValue("");
							txtrAmount.get(ar).setValue("");
						}	
					}
					
					
				}
			});
			
			txtrate.add(ar,new TextRead(1));
			txtrate.get(ar).setWidth("100%");
			
			txtrAmount.add(ar,new TextRead(1));
			txtrAmount.get(ar).setWidth("100%");
			
			txtrAmount.get(ar).addListener(new ValueChangeListener() {
				
				public void valueChange(ValueChangeEvent event) 
				{
					Double total=0.00;
					
					for(int i=0;i<cmbProduct.size();i++)
					{
					   if(!txtrAmount.get(i).getValue().toString().isEmpty())
					   {
						   total=total+ Double.parseDouble(txtrAmount.get(i).getValue().toString());  
					   }
					}
					
					table.setColumnFooter("Amount", total.toString());
					
					
				}
			});
			
			
			cmbProduct.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
				{
					Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					Transaction tx=session.beginTransaction();

					if(cmbProduct.get(ar).getValue()!=null)

					{
						boolean fla=(doubleEntryCheck(cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue()),ar));

						if (fla )
						{
							String query="";
							query=  " select  ProductID,b.vRawItemName,Qty,Rate,b.vUnitName from  tbRawIssueDetails a "
									+"inner join tbRawItemInfo b on a.ProductID=b.vRawItemCode "
									+"where a.IssueNo like '"+cmbIssueNo.getValue().toString()+"' and a.ProductID like '"+cmbProduct.get(ar).getValue().toString()+"' " ;

							List lst=session.createSQLQuery(query).list();
							Iterator iter=lst.iterator();
							if(iter.hasNext())
							{
								Object[]element=(Object[]) iter.next();
								txtissueqty.get(ar).setValue(element[2].toString());
								unit.get(ar).setValue(element[4]);
								txtrate.get(ar).setValue(decimalf.format(element[3]) );
								cmbStroreLocation.get(ar).setValue("1");
							}

							
						}
						else
						{	
							Object checkNull=(Object)cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue());
							System.out.print(checkNull);
							if(!checkNull.equals("")){
								cmbProduct.get(ar).setValue(null);//("x#"+ar,"");
								getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							}

						}


					}


				}
			});



			cmbStroreLocation.add(ar,new ComboBox());
			cmbStroreLocation.get(ar).setWidth("100%");
			cmbStroreLocation.get(ar).setImmediate(true);
			cmbStroreLocation.get(ar).setNullSelectionAllowed(true);
			
			    Session session1=SessionFactoryUtil.getInstance().getCurrentSession();
				Transaction txt=session1.beginTransaction();

			List list = session1.createSQLQuery("select vDepoId,vDepoName from tbDepoInformation").list();

			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbStroreLocation.get(ar).addItem(element[0]);
				cmbStroreLocation.get(ar).setItemCaption(element[0], element[1].toString());

			}


		    txtremarks.add( ar , new TextField());
		    txtremarks.get(ar).setWidth("100%");
		    txtremarks.get(ar).setImmediate(true);

			table.addItem(new Object[]{cmbProduct.get(ar),unit.get(ar),txtissueqty.get(ar),txtissueRturnQty.get(ar),txtrate.get(ar),txtrAmount.get(ar),cmbStroreLocation.get(ar),txtremarks.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

}
