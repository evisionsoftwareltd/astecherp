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
import com.vaadin.ui.*;
public class TubeFindWindow extends Window{

	SessionBean sessionBean;
	TextField txtd;
	String frmName;
	VerticalLayout mainLayout=new VerticalLayout();
	private ComboBox cmbIssueFrom = new ComboBox("From :");
	private ComboBox cmbIssueTo = new ComboBox("To :");
	private ComboBox cmbFinishedGoods = new ComboBox("Finished Goods :");
	
	ArrayList <Label> lblIssueNo=new ArrayList<Label>();
	ArrayList <Label> lblIssueDate=new ArrayList<Label>();
	ArrayList <Label> lblIssueFrom=new ArrayList<Label>();
	ArrayList <Label> lblIssueTo=new ArrayList<Label>();
	ArrayList <Label> lblFinishedGoods=new ArrayList<Label>();
	ArrayList <Label> lbljoborderNo=new ArrayList<Label>();
	Table table=new Table();
	
	public TubeFindWindow(SessionBean sessionBean,TextField txtReceiptId,String frmName){
		
		this.sessionBean=sessionBean;
		this.txtd=txtReceiptId;
		this.setCaption("Find Window");
		this.center();
		this.setWidth("830px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");
		addCmp();
		issueFromData();
		setEventAction();
	}
	private void issueToDataLoad() {
		cmbIssueTo.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Tube Section"))
			{

				query="select Distinct  StepId,StepName  from tbProductionStep a  inner join  tbProductionType b on  a.productionTypeId=b.productTypeId " 
						+"where   b.productTypeName like 'Tube Production' and a.StepName like 'Printing' ";

			}
			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Printing"))
			{
				query="select distinct StepId,StepName  from  tbProductionStep where StepName like 'Tubing' ";

			}
			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Tubing"))
			{
				query="select Distinct  StepId,StepName  from tbProductionStep where  StepName like 'Shouldering' ";

			}
			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Shouldering"))
			{
				query="select Distinct  StepId,StepName  from tbProductionStep where  StepName like 'sealing' ";

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
					table.removeAllItems();
					System.out.println("I am Ok");
					issueToDataLoad();


				}
			}
		});
		cmbIssueTo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbIssueTo.getValue()!=null && cmbIssueFrom.getValue()!=null)
				{
					table.removeAllItems();
					FinishedGoodsDataLoad();
				}
			}
		});
		cmbFinishedGoods.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) 
			{
				table.removeAllItems();
				tableDataLoad();
			}
		});
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick())
				{
					String receiptId;
					receiptId=lblIssueNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println("value is"+receiptId);
					txtd.setValue(receiptId);
					//System.out.print("our Desire value is"+Asaud.getValue().toString());
					windowClose();

				}
			}
		});
	}
	private void windowClose()
	{

		this.close();
	}
	private void tableDataLoad(){
		Transaction tx=null;
		String query=null;
		try{
			//query="select IssueNo,IssueDate from tbTubeIssueInfo where IssueFrom like '"+cmbIssueFrom.getValue()+"' and IssueTo like '"+cmbIssueTo.getValue()+"' and FinishedGood like '"+cmbFinishedGoods.getValue()+"'";
			query=  " select IssueNo,CONVERT(date,IssueDate,105) as Date, isnull(jobOrderNo,'') as orderNo,IssueFrom,IssueTo,FinishedGood  from tbTubeIssueInfo "
					+" where IssueFrom like '"+cmbIssueFrom.getValue().toString()+"' and IssueTo like '"+cmbIssueTo.getValue().toString()+"' and FinishedGood like '"+cmbFinishedGoods.getValue().toString()+"' ";
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int a=list.size();
				Iterator iter=list.iterator();
				int i = 0;
				while(a>0){
					if(iter.hasNext()){
						Object element[]=(Object[]) iter.next();
						String issNo=element[0].toString();
						String issDate=element[1].toString();
						String joborderNo=element[2].toString();
						tableRowAdd(i,issNo,issDate,joborderNo);
					}
					a--;
					i++;
				}	
			}
			else
			{
				this.getParent().showNotification("There Is No Data",Notification.TYPE_WARNING_MESSAGE);	
			}
			
		}
		catch(Exception exp){
			this.getParent().showNotification("TableDataLoad: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void FinishedGoodsDataLoad() {
		cmbFinishedGoods.removeAllItems();
		Transaction tx=null;
		String query=null;

		try
		{
			//query=" select a.fGCode,b.vProductName from tbFinishedGoodsStandardInfo a inner join tbFinishedProductInfo b on a.fGCode=b.vProductId";
			query= "select FinishedGood,(select vProductName  From tbFinishedProductInfo where vProductId like FinishedGood ) as FinishGoods from tbTubeIssueInfo  "
					+" where IssueFrom like '"+cmbIssueFrom.getValue().toString()+"' and IssueTo like '"+cmbIssueTo.getValue().toString()+"' ";
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
	}
	private void issueFromData(){
		cmbIssueFrom.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			query= "  select * from  "
					+"( "
					+"	select '1' as type, CAST(AutoID as varchar(120)) as id ,SectionName as section  from tbSectionInfo where SectionName like '%Tube Section%' "
					+"	union "
					+"	select distinct '2' as type,  StepId as id ,StepName section  from tbProductionStep a  "
					+"	inner join  "
					+"	tbProductionType b  "
					+"	on a.productionTypeId=b.productTypeId "
					+"	where b.productTypeName like '%Tube%' "
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
	private void tableRowAdd(int ar,String issNo,String issDate,String OrderNo){
		
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
		lblFinishedGoods.get(ar).setValue(cmbFinishedGoods.getItemCaption(cmbFinishedGoods.getValue()));
		
		lbljoborderNo.add(ar,new Label());
		lbljoborderNo.get(ar).setWidth("100%");
		lbljoborderNo.get(ar).setImmediate(true);
		lbljoborderNo.get(ar).setValue(OrderNo);
		
		table.addItem(new Object[]{lblIssueNo.get(ar),lblIssueDate.get(ar),lbljoborderNo.get(ar) ,lblIssueFrom.get(ar),lblIssueTo.get(ar),lblFinishedGoods.get(ar)},ar);
	}
	private void addCmp() {
		mainLayout.setSpacing(true);
		FormLayout frmLayout=new FormLayout();
		frmLayout.setSpacing(true);
		frmLayout.addComponent(cmbIssueFrom);
		
		cmbIssueFrom.setImmediate(true);
		cmbIssueFrom.setWidth("200px");
		cmbIssueFrom.setNullSelectionAllowed(true);
		
		frmLayout.addComponent(cmbIssueTo);
		
		cmbIssueTo.setImmediate(true);
		cmbIssueTo.setWidth("200px");
		cmbIssueTo.setNullSelectionAllowed(true);
		
		frmLayout.addComponent(cmbFinishedGoods);
		
		cmbFinishedGoods.setImmediate(true);
		cmbFinishedGoods.setWidth("200px");
		cmbFinishedGoods.setNullSelectionAllowed(true);
		
		table.addContainerProperty("ISSUE NO", Label.class, new Label());
		table.setColumnWidth("ISSUE NO", 50);
		
		table.addContainerProperty("ISSUE Date", Label.class, new Label());
		table.setColumnWidth("ISSUE Date", 110);
		
		table.addContainerProperty("Job Order No", Label.class, new Label());
		table.setColumnWidth("Job Order No", 110);
		
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
		
		mainLayout.addComponent(frmLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}
