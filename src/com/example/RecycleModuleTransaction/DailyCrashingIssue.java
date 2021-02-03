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

public class DailyCrashingIssue extends Window {

	SessionBean sessionBean;
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");
	private AbsoluteLayout mainLayout1;
	
	private Label lblissueNo = new Label("Issue No :");
	private TextRead txtissueNo = new TextRead();
	
	Label lblDate=new Label();
	private PopupDateField dIssueDate = new PopupDateField();
	

	private Label lblIssueFrom = new Label("Issue From :");
	private ComboBox cmbIssueFrom = new ComboBox();
	
	private Label lblissueTo = new Label("Issue To :");
	private ComboBox cmbissueTo = new ComboBox();


	private Label lblchallanNo = new Label("Challan No :");
	private TextField txtChallanNo= new TextField();
	
	private Label lblchallanDate= new Label();
	private PopupDateField dChallanDate = new PopupDateField();
	

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
	private NativeButton btnLoadData=new NativeButton("Load Data");
	private NativeButton btnRequisitionEntry=new NativeButton("Requisition Entry");
	private NativeButton btnRequisitionReport=new NativeButton("Requisition Report");
	
	private Table table = new Table();
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> color = new ArrayList<TextRead>();
	private ArrayList<TextRead> unit = new ArrayList<TextRead>();
	private ArrayList<TextRead> grade = new ArrayList<TextRead>();
	private ArrayList<TextRead> balanceQtyKg = new ArrayList<TextRead>();
	private ArrayList<AmountField> issueQtyKg = new ArrayList<AmountField>(1);
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

	public DailyCrashingIssue(SessionBean sessionBean) {

		this.sessionBean=sessionBean;
		this.setCaption("CRASHING ISSUE ENTRY::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout1);
		componentIni(true);
		btnIni(true);
		setEventAction();
		issueTo();
		issueFrom();
		focusMove();
		tbCmbProductLoad();
	
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
		allComp.add(dIssueDate);
		allComp.add(cmbIssueFrom);
		allComp.add(cmbissueTo);
		allComp.add(txtChallanNo);
		allComp.add(dChallanDate);
		
			for(int i=0;i<cmbProduct.size();i++)
			{
				allComp.add(cmbProduct.get(i));
				allComp.add(issueQtyKg.get(i));
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

		for(int a=0;a<cmbProduct.size();a++)
		{
			String sql= "select vItemCode,vItemName from tbRecycleItemInfo ";
			List list=dbService(sql);

			cmbProduct.get(a).removeAllItems();
			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				Object[] element=(Object[]) iter.next();

				cmbProduct.get(a).addItem(element[0].toString());
				String name=element[1].toString();
				cmbProduct.get(a).setItemCaption(element[0].toString(), name);
			}
		}
	}
	private void tbCmbProductLoadFind(int a)
	{

		String sql= "select vItemCode,vItemName from tbRecycleItemInfo ";
		List list=dbService(sql);

		cmbProduct.get(a).removeAllItems();
		for(Iterator iter=list.iterator(); iter.hasNext();)
		{
			Object[] element=(Object[]) iter.next();

			cmbProduct.get(a).addItem(element[0].toString());
			String name=element[0].toString()+"#"+element[1].toString();
			cmbProduct.get(a).setItemCaption(element[0].toString(), name);
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
		
		lblissueNo.setWidth("-1px");
		lblissueNo.setHeight("-1px");
		lblissueNo.setImmediate(false);

		txtissueNo.setWidth("100px");
		txtissueNo.setHeight("24px");
		txtissueNo.setImmediate(true);
		
		
		lblIssueFrom.setWidth("-1px");
		lblIssueFrom.setHeight("-1px");
		lblIssueFrom.setImmediate(false);


		cmbIssueFrom.setWidth("250px");
		cmbIssueFrom.setNewItemsAllowed(true);
		cmbIssueFrom.setNullSelectionAllowed(true);
		

		lblissueTo.setWidth("-1px");
		lblissueTo.setHeight("-1px");
		lblissueTo.setImmediate(false);


		cmbissueTo.setWidth("250px");
		cmbissueTo.setNewItemsAllowed(true);
		cmbissueTo.setNullSelectionAllowed(true);


	

		lblDate=new Label();
		lblDate.setValue("Date :");
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");

		dIssueDate.setWidth("108px");
		dIssueDate.setHeight("24px");
		dIssueDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dIssueDate.setDateFormat("dd-MM-yyyy");
		dIssueDate.setValue(new java.util.Date());
		dIssueDate.setImmediate(true);

		
		

		lblchallanNo=new Label();
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
		dChallanDate.setImmediate(true);
		
		


		table.setWidth("98%");
		table.setHeight("280px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",15);
		table.addContainerProperty("RECYCLED ITEM", ComboBox.class , new ComboBox());
		table.setColumnWidth("RECYCLED ITEM",450);
		table.addContainerProperty("Unit", TextRead.class , new TextRead());
		table.setColumnWidth("Unit",40);
		table.addContainerProperty("color", TextRead.class , new TextRead());
		table.setColumnWidth("color",40);
		
		table.addContainerProperty("grade", TextRead.class , new TextRead());
		table.setColumnWidth("grade",80);
		
		table.addContainerProperty("BalnceQty(kg)", TextRead.class , new TextRead());
		table.setColumnWidth("BalnceQty(kg)",70);
		
		table.addContainerProperty("IssueQty(kg)", AmountField.class , new AmountField());
		table.setColumnWidth("IssueQty(kg)",70);
		
		
		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",250);

		tableInitialise();

		

		mainLayout1.addComponent(lblissueNo,"top:20.0px;left:10.0px;");
		mainLayout1.addComponent(txtissueNo,"top:20.0px;left:122.0px");

		mainLayout1.addComponent(lblDate,"top:50.0px;left:10.0px;");
		mainLayout1.addComponent(dIssueDate,"top:50.0px;left:122.0px");
		
		mainLayout1.addComponent(lblIssueFrom,"top:20.0px;left:502.0px;");
		mainLayout1.addComponent(cmbIssueFrom,"top:20.0px;left:590.0px;");
		
		mainLayout1.addComponent(lblissueTo,"top:50.0px;left:502.0px;");
		mainLayout1.addComponent(cmbissueTo,"top:50.0px;left:590.0px;");



		mainLayout1.addComponent(lblchallanNo,"top:20.0px;left:250.0px;");
		mainLayout1.addComponent(txtChallanNo,"top:20.0px;left:372.0px");
		

		mainLayout1.addComponent(lblchallanDate,"top:50.0px;left:250.0px;");
		mainLayout1.addComponent(dChallanDate,"top:50.0px;left:372.0px");


		
		
		mainLayout1.addComponent(table,"top:100.0px;left:10.0px;");

		mainLayout1.addComponent(lbLine,"top:410px;left:20.0px;");
		mainLayout1.addComponent(cButton,"top:440px;left:250px;");

		return mainLayout1;	
	}

	private void issueTo(){

		cmbissueTo.removeAllItems();
		Transaction tx=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql="select AutoID,SectionName depName from tbSectionInfo";
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
	
	
	private void issueFrom(){

		cmbIssueFrom.removeAllItems();
		Transaction tx=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql="select AutoID,SectionName depName from tbSectionInfo";
			List<?>  list=session.createSQLQuery(sql).list();
           
			cmbIssueFrom.removeAllItems();
			for(Iterator<?>  iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbIssueFrom.addItem(element[0].toString());
				cmbIssueFrom.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}

		catch(Exception exp){
			System.out.println(exp);
		}
	}




	private boolean tableDataCheck(){

		for(int a=0;a<cmbProduct.size();a++){
			if(cmbProduct.get(a).getValue()!=null && (!issueQtyKg.get(a).getValue().toString().isEmpty())){
					
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
				if(cmbissueTo.getValue()!=null)
				{
					if(!txtChallanNo.getValue().toString().isEmpty())
					{
						if(cmbProduct.get(0).getValue()!=null)
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
				
					reportShow();
				
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

			String query = "select ISNULL(MAX(issueNo),0)+1  from tbRecycledItemIssueInfo";

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

		Window win=new FindWindow(sessionBean,txtReceiptId,"crashingIssue");
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
			String query= 
					"select a.issueNo,a.dIssueDate,a.issueTo,a.challanNo,a.challandate, " 
					+"b.productId,b.issueQtyKg,vremarks,issueFrom from  tbRecycledItemIssueInfo a inner join tbRecycledIssueDetails b " 
					+"on a.issueNo=b.issueNo where a.issueNo='"+txtReceiptId.getValue().toString()+"' ";
			
			List led = session.createSQLQuery(query).list();
			Iterator<?>itr=led.iterator();
			int i=0;
			while(itr.hasNext()){

				Object[] element = (Object[]) itr.next();

				if(i==0)
				{
					txtissueNo.setValue(element[0]);
					dIssueDate.setValue(element[1]);
					cmbissueTo.setValue(element[2]);
					txtChallanNo.setValue(element[3]);
					dChallanDate.setValue(element[4]);
					cmbIssueFrom.setValue(element[8].toString());
				}
				
                    cmbProduct.get(i).setValue(element[5]);
                    balanceQtyKg.get(i).setValue(Double.parseDouble(balanceQtyKg.get(i).getValue().toString())+Double.parseDouble(element[6].toString()));
                    issueQtyKg.get(i).setValue(Double.parseDouble(element[6].toString()));
                    remarks.get(i).setValue(element[7].toString());
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


		if(txtissueNo.getValue()!= null)
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
			
			session.createSQLQuery("delete  from tbRecycledItemIssueInfo where issueNo='"+txtissueNo.getValue().toString()+"'").executeUpdate();
			session.createSQLQuery("delete  from tbRecycledIssueDetails where issueNo='"+txtissueNo.getValue().toString()+"'").executeUpdate();

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

		if(!txtissueNo.getValue().toString().trim().isEmpty()){

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
			String sqlInfo="",sqlDetailsnew="",sqlUdInfo="",sqlUdDetails="",  vocherId="",udFlag="",sqlDetails="";
			if(!isUpdate)
			{
				
				udFlag="New";
			}

			else
			{
				
				udFlag="Update";
			}
			
			/*if( Double.parseDouble(totalField.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":totalField.getValue().toString().replaceAll(",", ""))<=0  )
			{
				vocherId="";	
			}

			
			voucharType = Option();
			*/
			
			
			String sql="insert into tbRecycledItemIssueInfo (issueNo,issueTo,dIssueDate,challanNo,challandate,userIp,userId,dEntryTime,isReceived,issueFrom) "
					   +"values('"+txtissueNo.getValue()+"','"+cmbissueTo.getValue()+"','"+dateformat.format(dIssueDate.getValue())+"',"
					   	+ " '"+txtChallanNo.getValue()+"','"+dateformat.format(dChallanDate.getValue())+"',"
					   	+ " '"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',getdate(),1,'"+cmbIssueFrom.getValue()+"' )";
			
			session.createSQLQuery(sql).executeUpdate();
			
		

			for (int i = 0; i < cmbProduct.size(); i++)
			{
				if(cmbProduct.get(i).getValue()!=null && (!issueQtyKg.get(i).getValue().toString().isEmpty()))
				{
					
					sqlDetails="insert into tbRecycledIssueDetails (issueNo,productId,productName,unit,issueQtyKg, "
							   +"userIp,userId,dEntryTime,color,grade,vremarks) values('"+txtissueNo.getValue().toString()+"',"
							   +" '"+cmbProduct.get(i).getValue()+"','"+cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue())+"',"
							   + " '"+unit.get(i).getValue().toString()+"','"+issueQtyKg.get(i).getValue()+"',"
							   + " '"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',"
							   + " getdate(),'"+color.get(i).getValue()+"','"+grade.get(i).getValue()+"','"+remarks.get(i).getValue().toString()+"') ";
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
		cmbissueTo.focus();
		txtissueNo.setValue(autoIssueNo());

	}

	private void tableRmClear()
	{
		for(int i=0;i<cmbProduct.size();i++)
		{
			
			cmbProduct.get(i).setValue(null);
			unit.get(i).setValue("");
			color.get(i).setStyleName("stockclor");
			grade.get(i).setValue("");
			balanceQtyKg.get(i).setValue("");
			issueQtyKg.get(i).setValue("");
			remarks.get(i).setValue("");
		
			
		}
	}
	
	public void txtClear(){

		txtissueNo.setValue("");
		dIssueDate.setValue(new Date());
		cmbIssueFrom.setValue(null);
		cmbissueTo.setValue(null);
		dChallanDate.setValue(new Date());
		txtChallanNo.setValue("");
		tableRmClear();
		
	}

	private void componentIni(boolean b) {

		cmbIssueFrom.setEnabled(!b);
		cmbissueTo.setEnabled(!b);
		txtissueNo.setEnabled(!b);
		dIssueDate.setEnabled(!b);
		lblchallanNo.setEnabled(!b);
		dChallanDate.setEnabled(!b);
		
		for(int i=0;i<cmbProduct.size();i++){
			cmbProduct.get(i).setEnabled(!b);
			color.get(i).setEnabled(!b);
			unit.get(i).setEnabled(!b);
			balanceQtyKg.get(i).setEnabled(!b);
			issueQtyKg.get(i).setEnabled(!b);
			remarks.get(i).setEnabled(!b);	
		}
		
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

		for(int i=0;i<color.size();i++)
		{

			if(i!=row && caption.equals(cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue())))
			{
				return false;
			}

		}
		return true;
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
							if(fla)
							{
								int temp=cmbProduct.size();

								if(ar==temp-1)
								{
									tableRowAdd(temp);
									tbCmbProductLoadFind(temp);
								}
								
								String sql="select unit,color,grade,balanceQtyKg from funRecycledProductStockSectionWise(getdate(),'"+cmbProduct.get(ar).getValue()+"','"+cmbIssueFrom.getValue().toString()+"')";
								List lst= session.createSQLQuery(sql).list();
								Iterator<?>itr=lst.iterator();
								if(itr.hasNext())
								{
									Object[] element=(Object[]) itr.next();
									unit.get(ar).setValue(element[0].toString());
									color.get(ar).setValue(element[1].toString());
									grade.get(ar).setValue(element[2].toString());
									balanceQtyKg.get(ar).setValue( decimalf.format(Double.parseDouble(element[3].toString())));
									//balanceQtyPcs.get(ar).setValue( decimalf.format(Double.parseDouble(element[3].toString())));
										
								}
		
							}
							
							else
							{	
								Object checkNull=(Object)cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue());
								System.out.print(checkNull);
								if(!checkNull.equals(""))
								{
									cmbProduct.get(ar).setValue(null);//("x#"+ar,"");
									getParent().showNotification("Warning :","Same Product Name Is not applicable.",Notification.TYPE_WARNING_MESSAGE);
								}
							}
														
						}						
					}
				}
			
			});

			color.add(ar,new TextRead(1));
			color.get(ar).setWidth("100%");
			
			unit.add(ar,new TextRead(1));
			unit.get(ar).setWidth("100%");
			
			
			grade.add(ar,new TextRead(1));
			grade.get(ar).setWidth("100%");
			
			
			balanceQtyKg.add(ar,new TextRead(1));
			balanceQtyKg.get(ar).setWidth("100%");
			
			
			

			issueQtyKg.add(ar,new AmountField());
			issueQtyKg.get(ar).setWidth("100%");
			
			issueQtyKg.get(ar).addListener(new ValueChangeListener() 
			{
				
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					if(!issueQtyKg.get(ar).getValue().toString().isEmpty())
					{
					    double balanceKg=0;
					    double issueQty=0;
					    
					    balanceKg=Double.parseDouble(balanceQtyKg.get(ar).getValue().toString().isEmpty()?"0.00":balanceQtyKg.get(ar).getValue().toString()) ;
					    issueQty=Double.parseDouble(issueQtyKg.get(ar).getValue().toString().isEmpty()?"0.00":issueQtyKg.get(ar).getValue().toString()) ;
					    
					    if(issueQty>balanceKg)
					    {
					       showNotification("Issue Qty Must No Exceed Balance Qty",Notification.TYPE_WARNING_MESSAGE);	
					       issueQtyKg.get(ar).setValue("");
					    }
					    
					}
					
				}
			});
			
			
		

			remarks.add( ar , new TextField());
			remarks.get(ar).setWidth("100%");
			remarks.get(ar).setImmediate(true);

			table.addItem(new Object[]{lblSl.get(ar),cmbProduct.get(ar),unit.get(ar),color.get(ar),grade.get(ar) ,balanceQtyKg.get(ar),issueQtyKg.get(ar),remarks.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

}


