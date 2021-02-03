package com.example.RecycleModuleTransaction;

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
import com.example.RecycleModuleTransaction.DailyCrashingFind;
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

public class DailyCrashing extends Window {

	SessionBean sessionBean;
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");
	private AbsoluteLayout mainLayout1;
	
	private Label lblTransctionNo = new Label("Transaction No :");
	private TextRead txtTransactionNo = new TextRead();
	private TextRead txtFindTransactionNo = new TextRead();
	
	Label lblTransactionDate=new Label();
	private PopupDateField dTransctionDate = new PopupDateField();
	

	/*private Label lblissueTo = new Label("Issue To :");
	private ComboBox cmbissueTo = new ComboBox();*/


	/*private Label lblchallanNo = new Label("Challan No :");
	private TextField txtChallanNo= new TextField();*/
	
	/*private Label lblchallanDate= new Label();
	private PopupDateField dChallanDate = new PopupDateField();*/
	

	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	private Label lbLine=new Label("<b><font color='#e65100'>===========================================================================================================================================================</font></b>", Label.CONTENT_XHTML);

	private HashMap hRate = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();

	private TextField txtReceiptId=new TextField();

	double totalsum = 0.0;

	
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat decimalf = new DecimalFormat("#0.00");


	private Label amountWordLabel = new Label("Amount In Words: ");
	private Label totalLabel = new Label("Total                     : ");
	private Label lbllank= new Label();
	private TextField amountWordsField = new TextField();

	private TextRead totalField = new TextRead(1);
	private Label lblReqNo;
	private ComboBox cmbReqNo;
	
	
	private Label lblSectionName;
	private ComboBox cmbsectionName;
	
	
	private Table table = new Table();
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbRejectedProduct = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> cmbRecycledProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> color = new ArrayList<TextRead>();
	private ArrayList<TextRead> unit = new ArrayList<TextRead>();
	private ArrayList<TextRead> grade = new ArrayList<TextRead>();
	private ArrayList<AmountField> processQtyKg = new ArrayList<AmountField>();
	private ArrayList<AmountField> processQtyPcs = new ArrayList<AmountField>();
	private ArrayList<AmountField> producedQtyKg = new ArrayList<AmountField>(1);
	private ArrayList<AmountField> producedQtyPcs = new ArrayList<AmountField>(1);
	private ArrayList<AmountField> wastageQtyKg = new ArrayList<AmountField>(1);
	private ArrayList<AmountField> wastageQtyPcs = new ArrayList<AmountField>(1);
	
	private ArrayList<TextField> remarks = new ArrayList<TextField>();

	private TextRead txtvoucherno= new TextRead();


	private Label label = new Label();
	private Label l1 = new Label();
	private Label l2 = new Label();
	boolean isUpdate=false,isFind=false;
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	OptionGroup Loantype;
	private static final List<String>areatype  = Arrays.asList(new String[] {"Production"});

	public DailyCrashing(SessionBean sessionBean) {

		this.sessionBean=sessionBean;
		this.setCaption("DAILY CRASHING ENTRY::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout1);
		componentIni(true);
		btnIni(true);
		setEventAction();
		issueTo();
		focusMove();
		tbCmbProductLoad();
		cmbsectionLoad();
	
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
		catch(Exception exp)
		{

		}
		finally{
			if(tx!=null){
				session.close();
			}
		}
		return list;
	}


	private void focusMove(){
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(dTransctionDate);
		
		
		
			for(int i=0;i<cmbRejectedProduct.size();i++)
			{
				allComp.add(cmbRejectedProduct.get(i));
				allComp.add(cmbRecycledProduct.get(i));
				
				allComp.add(processQtyKg.get(i));
				allComp.add(processQtyPcs.get(i));
				
				allComp.add(producedQtyKg.get(i));
				allComp.add(producedQtyPcs.get(i));
				
				allComp.add(wastageQtyKg.get(i));
				allComp.add(wastageQtyPcs.get(i));
				
				allComp.add(remarks.get(i));
					
			}

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);


		new FocusMoveByEnter(this,allComp);
		}
	
	
	private void tbCmbProductLoad(){

		for(int a=0;a<cmbRejectedProduct.size();a++)
		{
			String sql= 
					"select semiFgCode,semiFgName from tbSemiFgInfo " 
					+"union "
					+"select semiFgSubId,semiFgSubName from tbSemiFgSubInformation ";
			List list=dbService(sql);

			cmbRejectedProduct.get(a).removeAllItems();
			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				Object[] element=(Object[]) iter.next();

				cmbRejectedProduct.get(a).addItem(element[0].toString());
				String name=element[1].toString();
				cmbRejectedProduct.get(a).setItemCaption(element[0].toString(), name);
			}
		}
	}
	
	
	private void cmbsectionLoad(){

		for(int a=0;a<cmbRejectedProduct.size();a++)
		{
			String sql= "select AutoID,SectionName from tbSectionInfo";
			List list=dbService(sql);

			cmbsectionName.removeAllItems();
			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				Object[] element=(Object[]) iter.next();
                cmbsectionName.addItem(element[0]);
                cmbsectionName.setItemCaption(element[0], element[1].toString());
			}
		}
	}
	
	
	private void tbCmbProductLoadFind(int a)
	{

		String sql= 
				"select semiFgCode,semiFgName from tbSemiFgInfo " 
				+"union "
				+"select semiFgSubId,semiFgSubName from tbSemiFgSubInformation ";
		List list=dbService(sql);

		cmbRejectedProduct.get(a).removeAllItems();
		for(Iterator iter=list.iterator(); iter.hasNext();)
		{
			Object[] element=(Object[]) iter.next();

			cmbRejectedProduct.get(a).addItem(element[0].toString());
			String name=element[0].toString()+"#"+element[1].toString();
			cmbRejectedProduct.get(a).setItemCaption(element[0].toString(), name);
		}
	}
	

	private AbsoluteLayout buildMainLayout(){

		mainLayout1 = new AbsoluteLayout();
		mainLayout1.setImmediate(false);
		mainLayout1.setWidth("1230px");
		mainLayout1.setHeight("480px"); 
		mainLayout1.setMargin(false);

		// top-level component properties
		setWidth("1250px");
		setHeight("560px");
		
		lblTransctionNo.setWidth("-1px");
		lblTransctionNo.setHeight("-1px");
		lblTransctionNo.setImmediate(false);

		txtTransactionNo.setWidth("100px");
		txtTransactionNo.setHeight("24px");
		txtTransactionNo.setImmediate(true);
		

	


	

		lblTransactionDate=new Label();
		lblTransactionDate.setValue("Date :");
		lblTransactionDate.setWidth("-1px");
		lblTransactionDate.setHeight("-1px");

		dTransctionDate.setWidth("108px");
		dTransctionDate.setHeight("24px");
		dTransctionDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dTransctionDate.setDateFormat("dd-MM-yyyy");
		dTransctionDate.setValue(new java.util.Date());
		dTransctionDate.setImmediate(true);
		
		lblSectionName= new Label("Section");
		lblSectionName.setWidth("-1px");
		lblSectionName.setHeight("-1px");
		lblSectionName.setImmediate(false);

        cmbsectionName= new ComboBox();
		cmbsectionName.setWidth("250px");
		cmbsectionName.setNewItemsAllowed(true);
		cmbsectionName.setNullSelectionAllowed(true);

		
		

		/*lblchallanNo=new Label();
		lblchallanNo.setValue("Challan No :");
		lblchallanNo.setWidth("-1px");
		lblchallanNo.setHeight("-1px");
		
		txtChallanNo.setWidth("100px");
		txtChallanNo.setHeight("24px");
		txtChallanNo.setImmediate(true);
		
		
		lblchallanDate=new Label();
		lblchallanDate.setValue("Challan Date:");
		lblchallanDate.setWidth("-1px");
		lblchallanDate.setHeight("-1px");
		
		dChallanDate.setWidth("108px");
		dChallanDate.setHeight("24px");
		dChallanDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dChallanDate.setDateFormat("dd-MM-yyyy");
		dChallanDate.setValue(new java.util.Date());
		dChallanDate.setImmediate(true);*/
		
		

	

		table.setWidth("1175px");
		table.setHeight("280px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",20);
		
		
		table.addContainerProperty("Rejected Product", ComboBox.class , new ComboBox());
		table.setColumnWidth("Rejected Product",250);
		
		table.addContainerProperty("Recycled Product", ComboBox.class , new ComboBox());
		table.setColumnWidth("Recycled Product",250);
		
		table.addContainerProperty("Unit", TextRead.class , new TextRead());
		table.setColumnWidth("Unit",40);
		
		table.addContainerProperty("color", TextRead.class , new TextRead());
		table.setColumnWidth("color",40);
		
		table.addContainerProperty("Grade", TextRead.class , new TextRead());
		table.setColumnWidth("Grade",40);
		
		table.addContainerProperty("ProcessQty(kg)", AmountField.class , new AmountField());
		table.setColumnWidth("ProcessQty(kg)",90);
		
		table.addContainerProperty("ProcessQty(Pcs)", AmountField.class , new AmountField());
		table.setColumnWidth("ProcessQty(Pcs)",90);
		
		table.addContainerProperty("ProduceQty(kg)", AmountField.class , new AmountField());
		table.setColumnWidth("ProduceQty(kg)",90);
		
		table.addContainerProperty("ProduceQty(Pcs)", AmountField.class , new AmountField());
		table.setColumnWidth("ProduceQty(Pcs)",70);
		table.setColumnCollapsed("ProduceQty(Pcs)", true);
		
		table.addContainerProperty("WastageQty(kg)", AmountField.class , new AmountField());
		table.setColumnWidth("WastageQty(kg)",90);
		
		table.addContainerProperty("WastageQty(Pcs)", AmountField.class , new AmountField());
		table.setColumnWidth("WastageQty(Pcs)",90);
		
		
		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",150);

		tableInitialise();

		
		mainLayout1.addComponent(lblTransctionNo,"top:20.0px;left:10.0px;");
		mainLayout1.addComponent(txtTransactionNo,"top:20.0px;left:122.0px");

		mainLayout1.addComponent(lblTransactionDate,"top:50.0px;left:10.0px;");
		mainLayout1.addComponent(dTransctionDate,"top:50.0px;left:122.0px");
		
		mainLayout1.addComponent(lblSectionName,"top:20.0px;left:280.0px;");
		mainLayout1.addComponent(cmbsectionName,"top:20.0px;left:350.0px");
		
/*		mainLayout1.addComponent(lblissueTo,"top:20.0px;left:502.0px;");
		mainLayout1.addComponent(cmbissueTo,"top:20.0px;left:590.0px;");



		mainLayout1.addComponent(lblchallanNo,"top:20.0px;left:250.0px;");
		mainLayout1.addComponent(txtChallanNo,"top:20.0px;left:372.0px");
		

		mainLayout1.addComponent(lblchallanDate,"top:50.0px;left:250.0px;");
		mainLayout1.addComponent(dChallanDate,"top:50.0px;left:372.0px");*/


		
		
		mainLayout1.addComponent(table,"top:100.0px;left:10.0px;");

		mainLayout1.addComponent(lbLine,"top:410px;left:20.0px;");
		mainLayout1.addComponent(cButton,"top:440px;left:250px;");

		return mainLayout1;	
	}

	private void issueTo(){

		//cmbissueTo.removeAllItems();
		Transaction tx=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql="select AutoID,SectionName+'-'+vDepartmentName depName from tbSectionInfo";
			List<?>  list=session.createSQLQuery(sql).list();

			for(Iterator<?>  iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				/*cmbissueTo.addItem(element[0].toString());
				cmbissueTo.setItemCaption(element[0].toString(), (String) element[1]);*/
			}
		}

		catch(Exception exp){
			System.out.println(exp);
		}
	}



	private boolean tableDataCheck(){

		for(int a=0;a<cmbRejectedProduct.size();a++){
			if(cmbRejectedProduct.get(a).getValue()!=null  && cmbRecycledProduct.get(a).getValue()
			!=null && !processQtyKg.get(a).getValue().toString().isEmpty() && !processQtyPcs.get(a).getValue().toString().isEmpty()
			&& !producedQtyKg.get(a).getValue().toString().isEmpty() ){
					
						return true;	
			}
		}
		return false;
	}
	
	@SuppressWarnings("serial")
	public void setEventAction(){


		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{

				newButtonEvent();
				isFind=false;
				isUpdate=false;
			}


		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbRejectedProduct.get(0).getValue()!=null)
						{
							
								if(tableDataCheck())
								{
									isFind=false;
									saveButtonEvent();	
								}
								else
								{
									showNotification("Warning !!","Please select All Data", Notification.TYPE_WARNING_MESSAGE);
								}
							
						}
						else{
							showNotification("Warning !!","Please select product name",Notification.TYPE_WARNING_MESSAGE);
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
				
					reportShow();
				
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
	
	
	
	
/*	private void productDataload(int ar)
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
}*/
	
/*	public void dataload(int a )
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
	}*/


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

	
	/*private void cmbsupplierData() 
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
	}*/



	private String autoReceiptNo() 
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "select ISNULL(MAX(iTransactionNo),0)+1  from tbDailyCrashingInfo";

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
	
/*	private String autoUdTransactionNo() 
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
	}*/
	private void findButtonEvent()
	{

		Window win=new DailyCrashingFind(sessionBean,txtFindTransactionNo);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtFindTransactionNo.getValue().toString().length()>0)
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
			String query= 
					"select a.iTransactionNo,CONVERT(date,a.dTransactionDate,105)trnsactionDate, "
					+"b.RejectedItemId,b.RejectedItemName, b.RecycledItemId,b.ReceycledItemName,b.mProcessQtyKg, "
					+"b.mProcessQtyPcs,"
					+"b.mProducedQtyKg,b.mProducedQtyPcs,b.mWastageQtyKg, "
					+"b.mWastageQtyPcs,vRemarks,sectionId from tbDailyCrashingInfo a inner join tbDailyCrashingDetails b "
					+"on a.iTransactionNo=b.iTransactionNo where a.iTransactionNo='"+txtFindTransactionNo.getValue().toString()+"' ";
			
			List led = session.createSQLQuery(query).list();
			Iterator<?>itr=led.iterator();
			int i=0;
			while(itr.hasNext()){

				Object[] element = (Object[]) itr.next();

				if(i==0)
				{
					txtTransactionNo.setValue(element[0]);
					dTransctionDate.setValue(element[1]);
					cmbsectionName.setValue(element[13]);
					
				}
				
				cmbRejectedProduct.get(i).setValue(element[2].toString());
				cmbRecycledProduct.get(i).setValue(element[4]);
				
				processQtyKg.get(i).setValue(decimalf.format(Double.parseDouble(element[6].toString())) );
				processQtyPcs.get(i).setValue(decimalf.format(Double.parseDouble(element[7].toString())) );
				
				producedQtyKg.get(i).setValue(decimalf.format(Double.parseDouble(element[8].toString())) );
				producedQtyPcs.get(i).setValue(decimalf.format(Double.parseDouble(element[9].toString())) );
				
				wastageQtyKg.get(i).setValue(decimalf.format(Double.parseDouble(element[10].toString())) );
				wastageQtyPcs.get(i).setValue(decimalf.format(Double.parseDouble(element[11].toString())) );
				
				remarks.get(i).setValue(element[12]);
				      
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


		if(txtTransactionNo.getValue()!= null)
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


		try
		{
			session.createSQLQuery("delete  from tbDailyCrashingInfo where iTransactionNo='"+txtTransactionNo.getValue().toString()+"'").executeUpdate();
			session.createSQLQuery("delete  from tbDailyCrashingDetails where iTransactionNo='"+txtTransactionNo.getValue().toString()+"'").executeUpdate();

			return true;

		}
		catch(Exception exp){

			this.getParent().showNotification("Error4",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void updateButtonEvent()
	{

		if(!txtTransactionNo.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
			this.getParent().showNotification
			(
					"Update Failed",
					"There are no data for update.",
					Notification.TYPE_WARNING_MESSAGE);
	}


	private void saveButtonEvent() {

		if(!txtTransactionNo.getValue().toString().trim().isEmpty()){

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
								{
								   insertData(session,tx);
								}
								
							else
							{
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

								if(nullCheck())
								insertData(session,tx);
								
								txtClear();
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

		for(int i=0;i<cmbRejectedProduct.size();i++)
		{

			if(cmbRejectedProduct.get(i).getValue()!=null)
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
			String sqlInfo="",sqlDetailsnew="",sqlUdInfo="",sqlUdDetails="",  vocherId="",udFlag="",sqlDetails="";
			if(!isUpdate)
			{
				
				udFlag="New";
			}

			else
			{
				
				udFlag="Update";
			}
			
			String transactionNo;
			if(!isUpdate)
			{
			  txtTransactionNo.setValue(autoReceiptNo());  	
			}
			
			
			/*if( Double.parseDouble(totalField.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":totalField.getValue().toString().replaceAll(",", ""))<=0  )
			{
				vocherId="";	
			}*/

			
			//voucharType = Option();
		
			
			
			String sql= "insert into tbDailyCrashingInfo(iTransactionNo,dTransactionDate,userIp,userId,dEntryTime,sectionId,sectionName) "
					    +"values('"+txtTransactionNo.getValue().toString()+"','"+dateformat.format(dTransctionDate.getValue())+"',"
					    		+ " '"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',getdate(),'"+cmbsectionName.getValue()+"','"+cmbsectionName.getItemCaption(cmbsectionName.getValue())+"' ) ";
			
			session.createSQLQuery(sql).executeUpdate();
			
		

			for (int i = 0; i < cmbRecycledProduct.size(); i++)
			{
				if(cmbRecycledProduct.get(i).getValue()!=null && cmbRejectedProduct.get(i).getValue()!=null && !processQtyKg.get(i).getValue().toString().isEmpty()
				  && !processQtyPcs.get(i).getValue().toString().isEmpty() && !producedQtyKg.get(i).getValue().toString().isEmpty()
				  )
				{
					
					double proQtyKg=Double.parseDouble(processQtyKg.get(i).getValue().toString().isEmpty()?"0.00":processQtyKg.get(i).getValue().toString()) ;
					double proQtyPcs=Double.parseDouble(processQtyPcs.get(i).getValue().toString().isEmpty()?"0.00":processQtyPcs.get(i).getValue().toString()) ;
					
					double prdQtyKg=Double.parseDouble(producedQtyKg.get(i).getValue().toString().isEmpty()?"0.00":producedQtyKg.get(i).getValue().toString()) ;
					double prdQtyPcs=Double.parseDouble(producedQtyPcs.get(i).getValue().toString().isEmpty()?"0.00":producedQtyPcs.get(i).getValue().toString()) ;
					
					double wstQtyKg=Double.parseDouble(wastageQtyKg.get(i).getValue().toString().isEmpty()?"0.00":wastageQtyKg.get(i).getValue().toString()) ;
					double wstQtyPcs=Double.parseDouble(wastageQtyPcs.get(i).getValue().toString().isEmpty()?"0.00":wastageQtyPcs.get(i).getValue().toString()) ;
					
					
					
					sqlDetails="insert into tbDailyCrashingDetails(iTransactionNo,RejectedItemId,RejectedItemName, "
							   +"RecycledItemId,ReceycledItemName,vUnit,vColor,vGrade,mProcessQtyKg,mProcessQtyPcs, "
							   +"mProducedQtyKg,mProducedQtyPcs,vuserIp,vUserId,dEntryTime,mWastageQtyKg,mWastageQtyPcs) values('"+txtTransactionNo.getValue()+"',"
							   + " '"+cmbRejectedProduct.get(i).getValue()+"','"+cmbRejectedProduct.get(i).getItemCaption(cmbRejectedProduct.get(i).getValue())+"',"
							   + " '"+cmbRecycledProduct.get(i).getValue()+"','"+cmbRecycledProduct.get(i).getItemCaption(cmbRecycledProduct.get(i).getValue())+"',"
							   + " '"+unit.get(i).getValue()+"','"+color.get(i).getValue()+"','"+grade.get(i).getValue()+"','"+proQtyKg+"',"
							   + "  '"+proQtyPcs+"','"+prdQtyKg+"','"+prdQtyPcs+"','"+sessionBean.getUserIp()+"',"
							   + " '"+sessionBean.getUserId()+"',getdate(),'"+wstQtyKg+"','"+wstQtyPcs+"' ) ";
					session.createSQLQuery(sqlDetails).executeUpdate(); 
				}
			}
			
			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			txtClear();
			componentIni(true);
			btnIni(true);

		}catch(Exception exp){
			tx.rollback();
			this.getParent().showNotification("Error5",exp+"",Notification.TYPE_ERROR_MESSAGE);

		}
	}


	
	private void newButtonEvent() 
	{

		componentIni(false);
		btnIni(false);
		txtClear();
		//cmbissueTo.focus();
		txtTransactionNo.setValue(autoReceiptNo());

	}

	private void tableRmClear()
	{
		for(int i=0;i<cmbRejectedProduct.size();i++)
		{
			
			cmbRejectedProduct.get(i).setValue(null);
			cmbRecycledProduct.get(i).setValue(null);
			unit.get(i).setValue("");
			color.get(i).setValue("");
			grade.get(i).setValue("");
			processQtyKg.get(i).setValue("");
			processQtyPcs.get(i).setValue("");
			producedQtyKg.get(i).setValue("");
			producedQtyPcs.get(i).setValue("");
			wastageQtyKg.get(i).setValue("");
			wastageQtyPcs.get(i).setValue("");
			remarks.get(i).setValue("");
		
			
		}
	}
	
	public void txtClear(){

		txtTransactionNo.setValue("");
		dTransctionDate.setValue(new Date());
		cmbsectionName.setValue(null);
		tableRmClear();
		
	}

	private void componentIni(boolean b) {

		
		txtTransactionNo.setEnabled(!b);
		dTransctionDate.setEnabled(!b);
		cmbsectionName.setEnabled(!b);
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

		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private boolean doubleEntryCheck(String caption,int row)
	{

		for(int i=0;i<color.size();i++)
		{

			if(i!=row && caption.equals(cmbRejectedProduct.get(i).getItemCaption(cmbRejectedProduct.get(i).getValue())))
			{
				return false;
			}

		}
		return true;
	}
	
	
	private void tbcmbRejectedProductAdd(int ar)
	{
		
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		
		String sqlRejectedItem="select semiFgCode,semiFgName from tbSemiFgInfo "
				   +"union "
				   +"select semiFgSubId,semiFgSubName from tbSemiFgSubInformation ";
		
		Iterator<?>itr=session.createSQLQuery(sqlRejectedItem).list().iterator();
		
		cmbRejectedProduct.get(ar).removeAllItems();
		while(itr.hasNext())
		{
		     Object[]elemtn=(Object[]) itr.next();	
		     cmbRejectedProduct.get(ar).addItem(elemtn[0]);
		     cmbRejectedProduct.get(ar).setItemCaption(elemtn[0].toString(), elemtn[1].toString());
		}
		
	}
	
	
	private void tbcmbRecycledItemAdd(int ar)
	{
		
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		
		String sqlRejectedItem="select vItemCode,vItemName from tbRecycleItemInfo";
		
		Iterator<?>itr=session.createSQLQuery(sqlRejectedItem).list().iterator();
		
		cmbRecycledProduct.get(ar).removeAllItems();
		while(itr.hasNext())
		{
		     Object[]elemtn=(Object[]) itr.next();	
		     cmbRecycledProduct.get(ar).addItem(elemtn[0]);
		     cmbRecycledProduct.get(ar).setItemCaption(elemtn[0].toString(), elemtn[1].toString());
		}
		
	}
	


	public void tableRowAdd(final int ar)
	{

		final Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query="";
		System.out.println("Value of ar: "+ar);
		try
		{
			lblSl.add(ar, new Label());
			lblSl.get(ar).setValue(ar+1);
			lblSl.get(ar).setImmediate(true);

			cmbRejectedProduct.add(ar,new ComboBox());
			cmbRejectedProduct.get(ar).setWidth("100%");
			cmbRejectedProduct.get(ar).setImmediate(true);
			cmbRejectedProduct.get(ar).setNullSelectionAllowed(false);
			cmbRejectedProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			
			String sqlRejectedItem="select semiFgCode,semiFgName from tbSemiFgInfo "
					   +"union "
					   +"select semiFgSubId,semiFgSubName from tbSemiFgSubInformation ";
			
			Iterator<?>itr=session.createSQLQuery(sqlRejectedItem).list().iterator();
			
			while(itr.hasNext())
			{
			     Object[]elemtn=(Object[]) itr.next();	
			     cmbRejectedProduct.get(ar).addItem(elemtn[0]);
			     cmbRejectedProduct.get(ar).setItemCaption(elemtn[0].toString(), elemtn[1].toString());
			}
			
			
			cmbRecycledProduct.add(ar,new ComboBox());
			cmbRecycledProduct.get(ar).setWidth("100%");
			cmbRecycledProduct.get(ar).setImmediate(true);
			cmbRecycledProduct.get(ar).setNullSelectionAllowed(false);
			cmbRecycledProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			
			String sqlRecycledItem="select vItemCode,vItemName from tbRecycleItemInfo ";
			
			
			Iterator<?>itrr=session.createSQLQuery(sqlRecycledItem).list().iterator();
			
			while(itrr.hasNext())
			{
			     Object[]elemtn=(Object[]) itrr.next();	
			     cmbRecycledProduct.get(ar).addItem(elemtn[0]);
			     cmbRecycledProduct.get(ar).setItemCaption(elemtn[0].toString(), elemtn[1].toString());
			}
			
			cmbRecycledProduct.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
				{


					if(cmbRecycledProduct.get(ar).getValue()!=null)

					{
						boolean fla=(doubleEntryCheck(cmbRecycledProduct.get(ar).getItemCaption(cmbRecycledProduct.get(ar).getValue()),ar));

						if (cmbRecycledProduct.get(ar).getValue() != null )
						{
							if(fla)
							{
								int temp=cmbRecycledProduct.size();

								if(ar==temp-1)
								{
									tableRowAdd(temp);
									tbcmbRecycledItemAdd(temp);
									tbcmbRejectedProductAdd(temp);
								}
								
								String sql="select vModelNo,vUnitName,vColor from tbRecycleItemInfo where vItemCode='"+cmbRecycledProduct.get(ar).getValue()+"' ";
								List lst= session.createSQLQuery(sql).list();
								Iterator<?>itr=lst.iterator();
								if(itr.hasNext())
								{
									Object[] element=(Object[]) itr.next();
									grade.get(ar).setValue(element[0].toString());
									unit.get(ar).setValue(element[1].toString());
									color.get(ar).setValue(element[2].toString());
									
								}
		
							}
							
							else
							{	
								Object checkNull=(Object)cmbRecycledProduct.get(ar).getItemCaption(cmbRecycledProduct.get(ar).getValue());
								System.out.print(checkNull);
								if(!checkNull.equals(""))
								{
									cmbRecycledProduct.get(ar).setValue(null);//("x#"+ar,"");
									getParent().showNotification("Warning :","Same Product Name Is not applicable.",Notification.TYPE_WARNING_MESSAGE);
								}
							}
														
						}						
					}
				}
			
			});
			
			unit.add(ar,new TextRead(1));
			unit.get(ar).setWidth("100%");

			color.add(ar,new TextRead(1));
			color.get(ar).setWidth("100%");
			
			
			
			grade.add(ar,new TextRead(1));
			grade.get(ar).setWidth("100%");
			
			processQtyKg.add(ar,new AmountField());
			processQtyKg.get(ar).setWidth("100%");
			
			processQtyPcs.add(ar,new AmountField());
			processQtyPcs.get(ar).setWidth("100%");
			
			producedQtyKg.add(ar,new AmountField());
			producedQtyKg.get(ar).setWidth("100%");
			
			producedQtyPcs.add(ar,new AmountField());
			producedQtyPcs.get(ar).setWidth("100%");
			
			wastageQtyKg.add(ar,new AmountField());
			wastageQtyKg.get(ar).setWidth("100%");
			
			wastageQtyPcs.add(ar,new AmountField());
			wastageQtyPcs.get(ar).setWidth("100%");
			
			

			remarks.add( ar , new TextField());
			remarks.get(ar).setWidth("100%");
			remarks.get(ar).setImmediate(true);

			table.addItem(new Object[]{lblSl.get(ar),cmbRejectedProduct.get(ar),cmbRecycledProduct.get(ar) ,unit.get(ar),color.get(ar),
					 grade.get(ar) , processQtyKg.get(ar) ,processQtyPcs.get(ar)	,producedQtyKg.get(ar),producedQtyPcs.get(ar),
					wastageQtyKg.get(ar),wastageQtyPcs.get(ar),remarks.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

}


