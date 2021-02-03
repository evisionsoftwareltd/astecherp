package com.example.RecycleModuleTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import java.text.DecimalFormat;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RejectedItemOpeningFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private NativeSelect cmbOpeningYear = new NativeSelect("Opening Year"); 
	private Table table=new Table();

	public String receiptTransactionId = "";
	private TextField txtReceiptProductId;

	public String receiptOpeningYear = "";
	private TextField txtReceiptOpeningYear;
	private TextField txtReceiptId;

	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lbltransactionDate = new ArrayList<Label>();
	private ArrayList<Label> lbProductId = new ArrayList<Label>();
	private ArrayList<Label> lbProductName = new ArrayList<Label>();
	private ArrayList<Label> lbUnit = new ArrayList<Label>();
	private ArrayList<Label> lblcolor = new ArrayList<Label>();
	private ArrayList<Label> lblTransactionId = new ArrayList<Label>();

	private SessionBean sessionBean;
	public RejectedItemOpeningFind(SessionBean sessionBean ,TextField txtReceiptId)
	{
		
		this.txtReceiptId=txtReceiptId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND OPENING STOCK INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("850px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		yearDataAdding();
		tableclear();
	}
	
	

	private void yearDataAdding(){
		cmbOpeningYear.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select 0,DATEPART(yyyy,openingYear)as Date from tbRejectedOpening";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[])iter.next();
				cmbOpeningYear.addItem(element[1]);
				
			}
		}
		catch(Exception exp){
			this.getParent().showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void tableInitialise()
	{
		for(int i=0;i<7;i++){
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbSL.add(ar, new Label(""));
		lbSL.get(ar).setWidth("100%");
		lbSL.get(ar).setImmediate(true);
		lbSL.get(ar).setHeight("23px");
		lbSL.get(ar).setValue(ar+1);

		lbProductId.add(ar, new Label(""));
		lbProductId.get(ar).setWidth("100%");
		lbProductId.get(ar).setImmediate(true);
		lbProductId.get(ar).setHeight("23px");

		lbProductName.add(ar, new Label(""));
		lbProductName.get(ar).setWidth("100%");
		lbProductName.get(ar).setImmediate(true);
		lbProductName.get(ar).setHeight("23px");

		
		
		
		lbUnit.add(ar, new Label(""));
		lbUnit.get(ar).setWidth("100%");
		lbUnit.get(ar).setImmediate(true);
		lbUnit.get(ar).setHeight("23px");
		
		
		lblcolor.add(ar, new Label(""));
		lblcolor.get(ar).setWidth("100%");
		lblcolor.get(ar).setImmediate(true);
		lblcolor.get(ar).setHeight("23px");
		
		lblTransactionId.add(ar, new Label(""));
		lblTransactionId.get(ar).setWidth("100%");
		lblTransactionId.get(ar).setImmediate(true);
		lblTransactionId.get(ar).setHeight("23px");
		
		lbltransactionDate.add(ar, new Label(""));
		lbltransactionDate.get(ar).setWidth("100%");
		lbltransactionDate.get(ar).setImmediate(true);
		lbltransactionDate.get(ar).setHeight("23px");
		
		
		table.addItem(new Object[]{lbSL.get(ar), lbltransactionDate.get(ar)  ,lbProductId.get(ar),lbProductName.get(ar),lbUnit.get(ar),
				lblcolor.get(ar),lblTransactionId.get(ar)},ar);
	}

	public void setEventAction()
	{
	
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					if(cmbOpeningYear.getValue()!=null)
					{
						receiptTransactionId = lblTransactionId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						txtReceiptId.setValue(receiptTransactionId);

						windowClose();
					}
					else{
						getParent().showNotification("Select Opening Year",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});
		
		
		cmbOpeningYear.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event)
			{
			  if(cmbOpeningYear.getValue()!=null)
			  {
				 try
				 {
					 Transaction tx=null;
					 Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					 tx=session.beginTransaction();
					 
					 String sql="select iTransactionId, CONVERT(varchar,dTransactionDate,110) transactionDate ,vProductId,vProductName,vUnitName,vColor from tbRejectedOpening "
							    +"where YEAR(openingYear)='"+cmbOpeningYear.getValue()+"'";
					 
					 
					 System.out.println(sql);
						List list=session.createSQLQuery(sql).list();
						if(!list.isEmpty()){
							int ar=0;
							for(Iterator iter=list.iterator();iter.hasNext();)
							{
								Object element[]=(Object[]) iter.next();
								
							
								
								lblTransactionId.get(ar).setValue(element[0].toString());
								lbltransactionDate.get(ar).setValue(element[1]);
								
								
								lbProductId.get(ar).setValue(element[2].toString());
								lbProductName.get(ar).setValue(element[3].toString());
								lbUnit.get(ar).setValue(element[4].toString());
								lblcolor.get(ar).setValue(element[5].toString());
							
								if(ar==lbProductId.size()-1){
									tableRowAdd(ar+1);
								}
								ar++;
							}
						}
						else
						{
							getParent().showNotification("No Data Found!!",Notification.TYPE_WARNING_MESSAGE);
						}
					 
					 
					 
				 }
				 
				 catch(Exception ex)
				 {
					 
				 }
			  }
				
			}
		});
		
		
	}

	
	private void tableclear()
	{
		for(int i=0; i<lbProductId.size(); i++)
		{
			lbltransactionDate.get(i).setValue("");
			lbProductId.get(i).setValue("");
			lbProductName.get(i).setValue("");
			lblcolor.get(i).setValue("");
			lbUnit.get(i).setValue("");
			lblTransactionId.get(i).setValue("");
		}
	}

	private void windowClose(){
		this.close();
	}

	private void compInit()
	{
		cmbOpeningYear.setWidth("100px");
		cmbOpeningYear.setImmediate(true);
		cmbOpeningYear.setNullSelectionAllowed(true);
		
		
		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("SL #", Label.class, new Label());
		table.setColumnWidth("SL #",20);
		
		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date",110);
		

		table.addContainerProperty("Product ID", Label.class, new Label());
		table.setColumnWidth("Product ID",65);
		table.setColumnCollapsed("Product ID", true);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name",450);
		
		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",55);
		
		table.addContainerProperty("Color", Label.class, new Label());
		table.setColumnWidth("Color",55);

		table.addContainerProperty("Trnsaction Number", Label.class, new Label());
		table.setColumnWidth("Trnsaction Number",55);
		table.setColumnCollapsed("Trnsaction Number", true);
	
	}

	private void compAdd()
	{
		cmbLayout.addComponent(cmbOpeningYear);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(table);
		mainLayout.addComponent(btnLayout);
		addComponent(mainLayout);
	}
}
