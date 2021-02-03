package com.example.ReceycleModuleSetup;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class RecycleItemFind extends Window
{
	private AbsoluteLayout mainLayout;

	private TextField txtReceiptSupplierId;

	private Table table=new Table();

	public String receiptItemId = "";

	private Label lblCategoryName=new Label("Category :");
	private ComboBox cmbCategoryName=new ComboBox() ;

	private Label lblSubCategoryName=new Label("Sub-Category :");
	private ComboBox cmbSubCategoryName=new ComboBox() ;
	

	private ArrayList<Label> lbProductId = new ArrayList<Label>();
	private ArrayList<Label> lbProductName = new ArrayList<Label>();
	private ArrayList<Label> lbCategory = new ArrayList<Label>();
	private ArrayList<Label> lbSubCategory= new ArrayList<Label>();
	private ArrayList<Label> lbUnit = new ArrayList<Label>();
	

	///////////////////////
	private CheckBox chkAllCategory;
	private CheckBox chkAllSubCategory;
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private Label lblCountProduct=new Label();

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;
	public  RecycleItemFind(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("RECYCLED  ITEM FIND :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("910px");
		this.setHeight("750px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		setContent(mainLayout);
		tableInitialise();
		setEventAction();
		tableclear();
		tableDataAdding();
		cmbAddCategoryData();
	}

	public void tableInitialise()
	{
		for(int i=0;i<7;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbProductId.add(ar, new Label(""));
		lbProductId.get(ar).setWidth("100%");
		lbProductId.get(ar).setImmediate(true);
		lbProductId.get(ar).setHeight("15px");

		lbProductName.add(ar, new Label(""));
		lbProductName.get(ar).setWidth("100%");
		lbProductName.get(ar).setImmediate(true);

		lbCategory.add(ar, new Label(""));
		lbCategory.get(ar).setWidth("100%");
		lbCategory.get(ar).setImmediate(true);

		lbSubCategory.add(ar, new Label(""));
		lbSubCategory.get(ar).setWidth("100%");
		lbSubCategory.get(ar).setImmediate(true);
		/*
		lbsubSubCategory.add(ar, new Label(""));
		lbsubSubCategory.get(ar).setWidth("100%");
		lbsubSubCategory.get(ar).setImmediate(true);*/

		lbUnit.add(ar, new Label(""));
		lbUnit.get(ar).setWidth("100%");
		lbUnit.get(ar).setImmediate(true);

	


		table.addItem(new Object[]{lbProductId.get(ar),lbProductName.get(ar),lbCategory.get(ar),lbSubCategory.get(ar), lbUnit.get(ar)},ar);
	}

	public void setEventAction()
	{

		chkAllCategory.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(chkAllCategory.booleanValue()==true){

					cmbCategoryName.setValue(null);			
					cmbCategoryName.setEnabled(false);
					cmbAddSubcategoryData();
				}
				else{
					cmbCategoryName.setEnabled(true);
				}
			}
		});
		chkAllSubCategory.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(chkAllSubCategory.booleanValue()==true){

					cmbSubCategoryName.setValue(null);
					cmbSubCategoryName.setEnabled(false);
				}
				else{
					cmbSubCategoryName.setEnabled(true);
					if(cmbSubCategoryName.getValue()!=null ||chkAllSubCategory.booleanValue()==true)
					{
						cmbAddSubcategoryData();
					}
				}
			}
		});
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptItemId = lbProductId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptItemId);
					windowClose();
				}
			}
		});
		cmbCategoryName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbCategoryName.getValue()!=null ||chkAllCategory.booleanValue()==true)
				{
					cmbAddSubcategoryData();
				}

			}
		});

		cmbSubCategoryName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbSubCategoryName.getValue()!=null ||chkAllSubCategory.booleanValue()==true)
				{
					//cmbAddSubSubcategoryData();
				}
			}
		});

		
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				
					if( cmbCategoryName.getValue()!=null ||chkAllCategory.booleanValue()==true ) 
					{
						tableclear();
						tableDataAdding();
					}
					else 
					{
						showNotification("Please select Category Name.", Notification.TYPE_WARNING_MESSAGE);
						cmbCategoryName.focus();

					}
				
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbProductId.size(); i++)
		{
			lbProductId.get(i).setValue("");
			lbProductName.get(i).setValue("");
			lbCategory.get(i).setValue("");
			lbSubCategory.get(i).setValue("");
			lbUnit.get(i).setValue("");
			
		}
	}

	
	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = null;
		String category="";
		String subCategory="";
		String subsubcategoryid="";

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			if(isFind)
			{
				if(chkAllCategory.booleanValue())
				{
				 category ="%"; 
				}

				else
				{
				 category = cmbCategoryName.getValue().toString();
				}
				if(chkAllSubCategory.booleanValue()||cmbSubCategoryName.getValue()==null)
				{
					subCategory ="%"; 
				}
				else
				{
					subCategory = cmbSubCategoryName.getValue().toString();
				}
				
				query= "select vItemCode,vItemName,vCategoryName,vSubCategoryName,vUnitName from tbRecycleItemInfo " 
					   +"where iCategoryId like '"+category+"' and iSubCategoryId like  '"+subCategory+"'  ";

				System.out.println("query is"+query);
			}
			
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbProductId.get(i).setValue(element[0]);
					lbProductName.get(i).setValue(element[1]);


					if(element[2].toString().equals("")){
						lbCategory.get(i).setValue("");
					}else{
						lbCategory.get(i).setValue(element[2]);
					}		

					if(element[3].toString().equals("")){
						lbSubCategory.get(i).setValue("");
					}else{
						lbSubCategory.get(i).setValue(element[3]);	
					}

					if(element[4].toString().equals(""))
					{
						lbUnit.get(i).setValue("");
					}else{
						lbUnit.get(i).setValue(element[4]);	
					}
					
					
					if((i)==lbProductId.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
				table.setColumnFooter("Product Name","Total Product = "+i);
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data Found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) {
			//this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void windowClose()
	{
		this.close();
	}


	public void cmbAddCategoryData()
	{

		cmbCategoryName.removeAllItems();
		String type="";
		
		
			Transaction tx=null;
			try
			{
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery("select iCategoryId,vCategoryName from tbRecycleItemInfo").list();

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
		String type="";
		if(chkAllCategory.booleanValue()){
			type="%";
		}
		else{
			type=cmbCategoryName.getValue().toString();
		}

		if(cmbCategoryName.getValue()!=null || chkAllCategory.booleanValue() ==true)
		{
			Transaction tx=null;
			try
			{
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery("select iSubCategoryId,vSubCategoryName from tbRecycleItemInfo where iCategoryId like  '"+type+"' ").list();

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


	private void compInit()
	{		
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		

		cmbCategoryName.setNullSelectionAllowed(false);
		cmbCategoryName.setImmediate(true);
		cmbCategoryName.setWidth("250px");

		chkAllCategory=new CheckBox("All");
		chkAllCategory.setImmediate(true);
		chkAllCategory.setValue(false);

		cmbSubCategoryName.setNullSelectionAllowed(true);
		cmbSubCategoryName.setImmediate(true);
		cmbSubCategoryName.setWidth("250px");

		chkAllSubCategory=new CheckBox("All");
		chkAllSubCategory.setImmediate(true);
		chkAllSubCategory.setValue(false);

		/*	cmbsubsubCategory.setNullSelectionAllowed(true);
		cmbsubsubCategory.setImmediate(true);
		cmbsubsubCategory.setWidth("200px");*/

		table.setSelectable(true);
		table.setWidth("98%");
		table.setHeight("100%");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Product Id", Label.class, new Label());
		table.setColumnWidth("Product Id",60);
		table.setColumnCollapsed("Product Id", true);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name",250);

		table.addContainerProperty("Category Name", Label.class, new Label());
		table.setColumnWidth("Category Name",200);

		table.addContainerProperty("Sub Category Name", Label.class, new Label());
		table.setColumnWidth("Sub Category Name",200);

		/*		table.addContainerProperty("Sub Sub-Category Name", Label.class, new Label());
		table.setColumnWidth("Sub Sub-Category Name",150);*/

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",60);
		Label lbl=new Label();

	
		table.setFooterVisible(true);
	}

	private AbsoluteLayout compAdd()
	{

		mainLayout.addComponent(lblCategoryName, "top:50px; left:20px;");
		mainLayout.addComponent(cmbCategoryName, "top:48px; left:120px;");
		mainLayout.addComponent(chkAllCategory, "top:50.0px;left:370.0px;");
		mainLayout.addComponent(lblSubCategoryName, "top:50px; left:420px;");
		mainLayout.addComponent(cmbSubCategoryName, "top:48px; left:530px;");
		mainLayout.addComponent(chkAllSubCategory, "top:50.0px;left:790.0px;");
		//mainLayout.addComponent(lblsubsubCategoryname, "top:50px; left:350px;");
		//mainLayout.addComponent(cmbsubsubCategory, "top:48px; left:460px;");

		mainLayout.addComponent(cButton, "top:80px; left:120px;");
		//		mainLayout.addComponent(lblCountProduct, "top:; left:;");

		mainLayout.addComponent(table, "top:150px; left:0px;");

		return mainLayout;
	}
}