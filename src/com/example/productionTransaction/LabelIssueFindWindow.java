package com.example.productionTransaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
public class LabelIssueFindWindow extends Window{

	SessionBean sessionBean;
	TextField txtd=new TextField();
	VerticalLayout mainLayout=new VerticalLayout();
	
	private PopupDateField dFromDate;
	private PopupDateField dToDate;
	
	private ComboBox cmbIssueFrom = new ComboBox("From :");
	private ComboBox cmbIssueTo = new ComboBox("To :");
//	private ComboBox cmbFinishedGoods = new ComboBox("Finished Goods :");
	
	private NativeButton findButton = new NativeButton("Find");
	
	ArrayList <Label> lblIssueNo=new ArrayList<Label>();
	ArrayList <Label> lblIssueDate=new ArrayList<Label>();
	ArrayList <Label> lblIssueFrom=new ArrayList<Label>();
	ArrayList <Label> lblIssueTo=new ArrayList<Label>();
	ArrayList <Label> lblFinishedGoods=new ArrayList<Label>();
	Table table=new Table();
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd");
	
	public LabelIssueFindWindow(SessionBean sessionBean,TextField txtReceiptId){
		
		this.sessionBean=sessionBean;
		this.txtd=txtReceiptId;
		this.setCaption("Find Window");
		this.center();
		this.setWidth("770px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		addCmp();
		issueFromData();
		setEventAction();
	}
	
	private void tableclear()
	{
		for(int i=0; i<lblIssueNo.size(); i++)
		{
			lblIssueNo.get(i).setValue("");
			lblIssueDate.get(i).setValue("");
			lblIssueFrom.get(i).setValue("");
			lblIssueTo.get(i).setValue("");
		}
	}
	
	private void issueToDataLoad() 
	{
		cmbIssueTo.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Tube Section"))
			{

				query="select Distinct  StepId,StepName  from tbProductionStep a  inner join  tbProductionType b on  a.productionTypeId=b.productTypeId " 
						+"where   b.productTypeName like 'Label Production' and a.StepName like 'Printing' ";

			}
			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Printing"))
			{
				query="select distinct StepId,StepName  from  tbProductionStep where StepName not like 'Printing' and StepId like '%Level%' ";

			}


			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				System.out.print("Is This OK");

				Object[] element=(Object[]) iter.next();

				cmbIssueTo.addItem(element[0]);
				cmbIssueTo.setItemCaption(element[0], (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private void setEventAction(){
		cmbIssueFrom.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbIssueFrom.getValue()!=null)
				{
					System.out.println("I am Ok");
					issueToDataLoad();
					table.removeAllItems();
				}
			}
		});
		/*cmbIssueTo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbIssueTo.getValue()!=null){
//					tableDataLoad();
				}
			}
		});*/
		/*cmbFinishedGoods.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				tableDataLoad();
			}
		});*/
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick())
				{
					String receiptId="";
					receiptId=lblIssueNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println("value is"+receiptId);
					txtd.setValue(receiptId);
					windowClose();

				}
			}
		});
		
		findButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbIssueFrom.getValue()!=null)
				{
				if(cmbIssueTo.getValue()!=null)
				{
					tableDataLoad();
				}
				else
				{
					getParent().showNotification("Please Select To",Notification.TYPE_WARNING_MESSAGE);
					cmbIssueTo.focus();
				}
				}
				else
				{
					getParent().showNotification("Please Select From",Notification.TYPE_WARNING_MESSAGE);
					cmbIssueFrom.focus();
				}
			}
		});
	}
	private void windowClose()
	{

		this.close();
	}
	private void tableDataLoad(){
		table.removeAllItems();
		Transaction tx=null;
		String query=null;
		try{
			query="select * from tbLabelIssueInfo where issueFrom like '"+cmbIssueFrom.getValue()+"' and issueTo like '"+cmbIssueTo.getValue()+"' and convert(date,jobDate,105) between '"+dateFormatSql.format(dFromDate.getValue())+"' and '"+dateFormatSql.format(dToDate.getValue())+"'";
			System.out.println("tableDataLoad: "+query);
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(query).list();
			int a=list.size();
			Iterator iter=list.iterator();
			int i = 0;
			
			
			if(!list.isEmpty())
			{
				while(a>0){
					if(iter.hasNext()){
						Object element[]=(Object[]) iter.next();
						String issNo=element[1].toString();
						String issDate=dateFormat.format(element[2]);
						tableRowAdd(i,issNo,issDate);
					}
					a--;
					i++;
				}	
			}
			
			else
			{
				showNotification("Ther is No Data Given Criterea",Notification.TYPE_WARNING_MESSAGE);
			}
			
		}
		catch(Exception exp){
			this.getParent().showNotification("TableDataLoad: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
/*	private void FinishedGoodsDataLoad() {
		cmbFinishedGoods.removeAllItems();
		Transaction tx=null;
		String query=null;

		try
		{
			query=" select vProductId,vProductName from tbStandardFinishedInfo";
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbFinishedGoods.addItem(element[0].toString());
				cmbFinishedGoods.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}*/
	private void issueFromData(){
		cmbIssueFrom.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			query= "  select * from  "
					+"( "
					+"	select '1' as type, CAST(AutoID as varchar(120)) as id ,SectionName as section  from tbSectionInfo where SectionName like '%Tube%' "
					+"	union "
					+"	select distinct '2' as type,  StepId as id ,StepName section  from tbProductionStep a  "
					+"	inner join  "
					+"	tbProductionType b  "
					+"	on a.productionTypeId=b.productTypeId "
					+"	where b.productTypeName like '%Label Production%' "
					+"	) as a  order by a.type ";

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbIssueFrom.addItem(element[1].toString());
				cmbIssueFrom.setItemCaption(element[1].toString(), (String) element[2]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}
	/*private void tableInitialize(){
		for(int a=0;a<10;a++){
			//tableRowAdd(a);
		}
	}*/
	private void tableRowAdd(int ar,String issNo,String issDate){
		
		lblIssueNo.add(ar,new Label());
		lblIssueNo.get(ar).setWidth("100%");
		lblIssueNo.get(ar).setImmediate(true);
		lblIssueNo.get(ar).setValue(issNo);
		
		lblIssueDate.add(ar,new Label());
		lblIssueDate.get(ar).setWidth("100%");
		lblIssueDate.get(ar).setImmediate(true);
		lblIssueDate.get(ar).setValue(issDate);
		
		lblIssueFrom.add(ar,new Label());
		lblIssueFrom.get(ar).setWidth("100%");
		lblIssueFrom.get(ar).setImmediate(true);
		lblIssueFrom.get(ar).setValue(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()));
		
		lblIssueTo.add(ar,new Label());
		lblIssueTo.get(ar).setWidth("100%");
		lblIssueTo.get(ar).setImmediate(true);
		lblIssueTo.get(ar).setValue(cmbIssueTo.getItemCaption(cmbIssueTo.getValue()));
		
		lblFinishedGoods.add(ar,new Label());
		lblFinishedGoods.get(ar).setWidth("100%");
		lblFinishedGoods.get(ar).setImmediate(true);
//		lblFinishedGoods.get(ar).setValue(cmbFinishedGoods.getItemCaption(cmbFinishedGoods.getValue()));
		
		table.addItem(new Object[]{lblIssueNo.get(ar),lblIssueDate.get(ar),lblIssueFrom.get(ar),lblIssueTo.get(ar),lblFinishedGoods.get(ar)},ar);
	}
	private void addCmp() {
		mainLayout.setSpacing(true);
		FormLayout frmLayout=new FormLayout();
		frmLayout.setSpacing(true);
		
		
		
		
		// dFromDate
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
		
		
		
		cmbIssueFrom.setImmediate(true);
		cmbIssueFrom.setWidth("200px");
		cmbIssueFrom.setNullSelectionAllowed(true);
		
		
		
		cmbIssueTo.setImmediate(true);
		cmbIssueTo.setWidth("200px");
		cmbIssueTo.setNullSelectionAllowed(true);
		
//		frmLayout.addComponent(cmbFinishedGoods);
		
//		cmbFinishedGoods.setImmediate(true);
//		cmbFinishedGoods.setWidth("200px");
//		cmbFinishedGoods.setNullSelectionAllowed(true);
		
		table.addContainerProperty("ISSUE NO", Label.class, new Label());
		table.setColumnWidth("ISSUE NO", 50);
		
		table.addContainerProperty("ISSUE Date", Label.class, new Label());
		table.setColumnWidth("ISSUE Date", 110);
		
		table.addContainerProperty("ISSUE From", Label.class, new Label());
		table.setColumnWidth("ISSUE From", 150);
		
		table.addContainerProperty("ISSUE To", Label.class, new Label());
		table.setColumnWidth("ISSUE To", 150);
		
		table.addContainerProperty("Finished Goods", Label.class, new Label());
		table.setColumnWidth("Finished Goods", 200);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		//tableInitialize();
		
		findButton.setWidth("80px");
		findButton.setHeight("28px");
		findButton.setIcon(new ThemeResource("../icons/print.png"));
		
		frmLayout.addComponent(dFromDate);
		frmLayout.addComponent(dToDate);
		frmLayout.addComponent(cmbIssueFrom);
		frmLayout.addComponent(cmbIssueTo);
		frmLayout.addComponent(findButton);
		mainLayout.addComponent(frmLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}
