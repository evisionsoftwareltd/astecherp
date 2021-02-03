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
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
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

public class RecycledItemOpeningFindWindow extends Window
{
	private AbsoluteLayout mainLayout;
	
	private NativeSelect cmbOpeningYear = new NativeSelect(); 
	private Table table=new Table();
	
	private CheckBox chkAllCat = new CheckBox("All");
	private CheckBox chkAllSubCat = new CheckBox("All");

	public String receiptSupplier = "";
	private TextField txtSupplier=new TextField();
	

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");

	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lbCategory = new ArrayList<Label>();
	private ArrayList<Label> lbSubCategory = new ArrayList<Label>();
	private ArrayList<Label> lbItemName = new ArrayList<Label>();
	private ArrayList<Label> lbUnit = new ArrayList<Label>();
	private ArrayList<Label> lbGrade = new ArrayList<Label>();
	private ArrayList<Label> lbColor = new ArrayList<Label>();
	private ArrayList<Label> lblTransactionId=new ArrayList<Label>();
	
	private Label lblOpeningYear= new Label();
	private Label lblCategory = new Label();
	private Label lblSubCategory = new Label();
	
	private ComboBox cmbSubCategoryName=new ComboBox() ;
	private ComboBox cmbCategoryName=new ComboBox() ;
	
	private NativeButton findButton = new NativeButton("Find");

	private SessionBean sessionBean;
	public RecycledItemOpeningFindWindow(SessionBean sessionBean,TextField txtSupplier)
	{

		
		this.txtSupplier = txtSupplier;
		this.sessionBean=sessionBean;
		this.setCaption("FIND OPENING RECYCLED ITEM :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("850px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		setContent(buildMainLayout());
		tableInitialise();
		setEventAction();
		yearDataAdding();
		tableclear();
		cmbAddCategoryData();
	}
	private void yearDataAdding(){
		cmbOpeningYear.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select 0,DATEPART(yyyy,openingYear)as Date from tbRecycleItemOpening";
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

		lbCategory.add(ar, new Label(""));
		lbCategory.get(ar).setWidth("100%");
		lbCategory.get(ar).setImmediate(true);
		lbCategory.get(ar).setHeight("23px");

		lblTransactionId.add(ar, new Label(""));
		lblTransactionId.get(ar).setWidth("100%");
		lblTransactionId.get(ar).setImmediate(true);
		lblTransactionId.get(ar).setHeight("23px");

		lbSubCategory.add(ar, new Label(""));
		lbSubCategory.get(ar).setWidth("100%");
		lbSubCategory.get(ar).setImmediate(true);
		lbSubCategory.get(ar).setHeight("23px");
		
		lbItemName.add(ar, new Label(""));
		lbItemName.get(ar).setWidth("100%");
		lbItemName.get(ar).setImmediate(true);
		lbItemName.get(ar).setHeight("23px");

		
		lbUnit.add(ar, new Label(""));
		lbUnit.get(ar).setWidth("100%");
		lbUnit.get(ar).setImmediate(true);
		lbUnit.get(ar).setHeight("23px");

		lbGrade.add(ar, new Label(""));
		lbGrade.get(ar).setWidth("100%");
		lbGrade.get(ar).setImmediate(true);
		lbGrade.get(ar).setHeight("23px");

		lbColor.add(ar, new Label(""));
		lbColor.get(ar).setWidth("100%");
		lbColor.get(ar).setImmediate(true);
		lbColor.get(ar).setHeight("23px");
		
		table.addItem(new Object[]{lbSL.get(ar),lblTransactionId.get(ar),lbCategory.get(ar),lbSubCategory.get(ar),lbItemName.get(ar),lbUnit.get(ar),lbGrade.get(ar),lbColor.get(ar)},ar);
	}

	public void setEventAction()
	{
		cmbOpeningYear.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				
			
			}
		});
		chkAllCat.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(chkAllCat.booleanValue())
				{
					cmbCategoryName.setValue(null);
					cmbCategoryName.setEnabled(false);
				}
				else
				{
					cmbCategoryName.setEnabled(true);
				}
			}
		});

		chkAllSubCat.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(chkAllSubCat.booleanValue())
				{
					cmbSubCategoryName.setValue(null);
					cmbSubCategoryName.setEnabled(false);
				}
				else
				{
					cmbSubCategoryName.setEnabled(true);
				}
			}
		});
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplier=lblTransactionId.get(Integer.parseInt(event.getItemId().toString())).getValue().toString();
					txtSupplier.setValue(receiptSupplier);
					close();
				}
			}
		});
		
		cmbCategoryName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbCategoryName.getValue()!=null)
				cmbAddSubcategoryData();
			}
		});
		
		
		findButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				
				tableDataLoad();
			}

		});
	}
	private void tableDataLoad() {
		tableclear();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			
			String category = "";
			String subCategory = "";
			String subSubCategory = "";

			if(cmbCategoryName.getValue()!=null)
			{
				category = cmbCategoryName.getValue().toString();
			}
			else
			{
				category = "%";
			}
			
			if(cmbSubCategoryName.getValue()!=null)
			{
				subCategory = cmbSubCategoryName.getValue().toString();
			}
			else
			{
				subCategory = "%";
			}
			
			String sql=" select vCategoryName,vSubCategoryName,vItemName,vUnit,vGrade,vColor,vTransactionId from tbRecycleItemOpening "
					+ "where year(openingYear) like  '"+cmbOpeningYear.getValue()+"' "
					+ "and iCategoryid like '"+category+"' "
					+ "and iSubCategoryid like '"+subCategory+"'";
			
			System.out.println(sql);
			List list=session.createSQLQuery(sql).list();
			if(!list.isEmpty()){
				int ar=0;
				for(Iterator iter=list.iterator();iter.hasNext();){
					Object element[]=(Object[]) iter.next();
					lbCategory.get(ar).setValue(element[0].toString());
					lbSubCategory.get(ar).setValue(element[1].toString());
					lbItemName.get(ar).setValue(element[2].toString());
					lbUnit.get(ar).setValue(element[3].toString());
					lbGrade.get(ar).setValue(element[4].toString());
					lbColor.get(ar).setValue(element[5].toString());
					lblTransactionId.get(ar).setValue(element[6]);
					//tableRowAdd(ar+1);
					/*if(ar==lbProductId.size()-1){
						tableRowAdd(ar+1);
					}*/
					ar++;
				}
			}
			else{
				getParent().showNotification("No Data Found!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){
			getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public void cmbAddCategoryData()
	{
		cmbCategoryName.removeAllItems();
		Transaction tx=null;
		try
		{
			
			String sql="select iCategoryId, vCategoryName  from  tbRecycleItemOpening";
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbCategoryName.addItem(element[0].toString());
				cmbCategoryName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public void cmbAddSubcategoryData()
	{
		cmbSubCategoryName.removeAllItems();
		if(cmbCategoryName.getValue()!=null)
		{
			Transaction tx=null;
			try
			{
				String sql="SELECT iSubCategoryID,vSubCategoryName FROM tbRecycleItemOpening where iCategoryId='"+cmbCategoryName.getValue().toString()+"' order by iSubCategoryID ";
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery(sql).list();

				for(Iterator iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbSubCategoryName.addItem(element[0].toString());
					cmbSubCategoryName.setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}
	


	private void tableclear()
	{
		for(int i=0; i<lblTransactionId.size(); i++)
		{
			lbCategory.get(i).setValue("");
			lbSubCategory.get(i).setValue("");
			lbUnit.get(i).setValue("");
			lbColor.get(i).setValue("");
			lbGrade.get(i).setValue("");
			lbItemName.get(i).setValue("");
			lblTransactionId.get(i).setValue("");
			
		}
	}

	private void windowClose(){
		this.close();
	}


	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("850px");
		mainLayout.setHeight("484");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("900px");
		setHeight("500px");
		
		
		lblOpeningYear.setImmediate(false);
		lblOpeningYear.setWidth("-1px");
		lblOpeningYear.setHeight("-1px");
		lblOpeningYear.setValue("Opening Year:");
		mainLayout.addComponent(lblOpeningYear, "top:30.0px; left:20.0px;");

		
		cmbOpeningYear.setImmediate(true);
		cmbOpeningYear.setNullSelectionAllowed(false);
		cmbOpeningYear.setNewItemsAllowed(false);
		cmbOpeningYear.setWidth("100px");
		cmbOpeningYear.setHeight("-1px");
		mainLayout.addComponent(cmbOpeningYear,"top:30.0px; left:150.0px;");

		lblCategory.setImmediate(false);
		lblCategory.setWidth("-1px");
		lblCategory.setHeight("-1px");
		lblCategory.setValue("Category:");
		mainLayout.addComponent(lblCategory,"top:55.0px; left:20.0px;");
		
		cmbCategoryName.setImmediate(true);
		cmbCategoryName.setNullSelectionAllowed(false);
		cmbCategoryName.setNewItemsAllowed(false);
		cmbCategoryName.setWidth("200px");
		cmbCategoryName.setHeight("-1px");
		cmbCategoryName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbCategoryName,"top:55.0px; left:150.0px;");
		
		chkAllCat.setWidth("-1px");
		chkAllCat.setHeight("-1px");
		chkAllCat.setImmediate(true);
	    mainLayout.addComponent(chkAllCat,"top:55.0px; left:350.0px;");
		
	    lblSubCategory.setImmediate(false);
		lblSubCategory.setWidth("-1px");
		lblSubCategory.setHeight("-1px");
		lblSubCategory.setValue("Sub Category:");
		mainLayout.addComponent(lblSubCategory,"top:80.0px; left:20.0px;");
		
		cmbSubCategoryName.setImmediate(true);
		cmbSubCategoryName.setNullSelectionAllowed(false);
		cmbSubCategoryName.setNewItemsAllowed(false);
		cmbSubCategoryName.setWidth("250px");
		cmbSubCategoryName.setHeight("-1px");
		cmbSubCategoryName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSubCategoryName,"top:80.0px; left:150.0px;");
		
		chkAllSubCat.setWidth("-1px");
		chkAllSubCat.setHeight("-1px");
		chkAllSubCat.setImmediate(true);
	    mainLayout.addComponent(chkAllSubCat,"top:80.0px; left:400.0px;");
		
		findButton.setWidth("80px");
		findButton.setHeight("28px");
		findButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(findButton,"top:105.0px; left:20.0px;");
		
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("SL #", Label.class, new Label());
		table.setColumnWidth("SL #",20);
		
		table.addContainerProperty("Transaction Id", Label.class, new Label());
		table.setColumnWidth("Transaction Id",50);

		table.addContainerProperty("Category Name", Label.class, new Label());
		table.setColumnWidth("Category Name",165);

		table.addContainerProperty("Sub Category Name", Label.class, new Label());
		table.setColumnWidth("Sub Category Name",165);
		
		table.addContainerProperty("Item Name", Label.class, new Label());
		table.setColumnWidth("Item Name",165);
		

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",55);
		
		table.addContainerProperty("Grade", Label.class, new Label());
		table.setColumnWidth("Grade",65);
		//table.setColumnCollapsed("Product ID", true);

		table.addContainerProperty("Color", Label.class, new Label());
		table.setColumnWidth("Color",65);
		
		table.setColumnCollapsingAllowed(true);
		table.setColumnCollapsed("Transaction Id",true);

		mainLayout.addComponent(table,"top:135.0px; left:20.0px;");
		
		return mainLayout;
	}

}