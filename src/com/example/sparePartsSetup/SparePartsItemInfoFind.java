package com.example.sparePartsSetup;

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
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class SparePartsItemInfoFind extends Window
{
	private AbsoluteLayout mainLayout;

	private TextField txtReceiptSupplierId;

	private Table table=new Table();

	public String receiptItemId = "";
	private Label lblCategoryName=new Label("Category :");
	private ComboBox cmbCategoryName=new ComboBox() ;
	private Label lblRackName=new Label("Rack Name :");
	private ComboBox cmbRackName=new ComboBox() ;
	private Label lblSubRackName=new Label("Sub Rack Name :");
	private ComboBox cmbSubRackName=new ComboBox() ;
	private Label lblSubCategoryName=new Label("Sub-Category :");
	private ComboBox cmbSubCategoryName=new ComboBox() ;
	private Label lblsubsubCategoryname=new Label("sub Sub-Category :");
	private ComboBox cmbsubsubCategory= new ComboBox();
	private ArrayList<Label> lbProductId = new ArrayList<Label>();
	private ArrayList<Label> lbProductName = new ArrayList<Label>();
	private ArrayList<Label> lbCategory = new ArrayList<Label>();
	private ArrayList<Label> lbSubCategory= new ArrayList<Label>();
	private ArrayList<Label> lbUnit = new ArrayList<Label>();
	private ArrayList<Label> lbsubSubCategory= new ArrayList<Label>(); 
	private CheckBox chkAllCategory,chkAllSubCategory,chkAllRack,chkAllSubRack;
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private ArrayList<PopupView> btnPopupView = new ArrayList<PopupView>();
	
	private ArrayList<Panel> PImage = new ArrayList<Panel>();
	
	private Label lblCountProduct=new Label();

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;

	public SparePartsItemInfoFind(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SPARE ITEM INFORMATION :: "+sessionBean.getCompany());
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
		//categoryTypeDataLoad();
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
		
		PImage.add(ar, new Panel());

		//PImage.get(ar).setWidth("100%");
		PImage.get(ar).setImmediate(true);
		PImage.get(ar).setWidth("310px");
		PImage.get(ar).setHeight("420px");

		btnPopupView.add(ar,new PopupView("View",PImage.get(ar)));
		btnPopupView.get(ar).setImmediate(true);
		btnPopupView.get(ar).setWidth("100%");

		table.addItem(new Object[]{lbProductId.get(ar),lbProductName.get(ar),lbCategory.get(ar),lbSubCategory.get(ar), lbUnit.get(ar),btnPopupView.get(ar)},ar);
	}

	
	public void ImageforPopup(String img,int i)
	{
		if(!img.equals("0")){
			File  fileStu_I = new File(img);
			Embedded eStu_I = new Embedded("",new FileResource(fileStu_I, getApplication()));
			eStu_I.requestRepaint();
			eStu_I.setWidth("260px");
			eStu_I.setHeight("350px");
			PImage.get(i).removeAllComponents();
			PImage.get(i).addComponent(eStu_I);
		}	
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
				cmbAddCategoryData();
			}
			}
		});
		chkAllSubCategory.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(chkAllSubCategory.booleanValue()==true){
				
						cmbSubCategoryName.setValue(null);
						cmbSubCategoryName.setEnabled(false);
						cmbAddRackData();
				}
				else{
					cmbSubCategoryName.setEnabled(true);
					cmbAddSubcategoryData();
				}
			}
		});
		chkAllRack.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(chkAllRack.booleanValue()==true){

						cmbRackName.setValue(null);			
						cmbRackName.setEnabled(false);
						cmbAddSubRackData();
				}
				else{
					cmbRackName.setEnabled(true);
					cmbAddRackData();
			}
			}
		});
		chkAllSubRack.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(chkAllSubRack.booleanValue()==true){

						cmbSubRackName.setValue(null);			
						cmbSubRackName.setEnabled(false);
				}
				else{
					cmbSubRackName.setEnabled(true);
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
					cmbAddRackData();
				}
			}
		});

		cmbRackName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbRackName.getValue()!=null||chkAllRack.booleanValue()==true)
				{
					cmbAddSubRackData();
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
			PImage.get(i).removeAllComponents();
			//lbsubSubCategory.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = null;
		String category="",subCategory="",rack="",subRack="";
	
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
				if(chkAllRack.booleanValue()||cmbRackName.getValue()==null)
				{
					rack ="%"; 
				}
				else
				{
					rack = cmbRackName.getValue().toString();
				}
				if(chkAllSubRack.booleanValue()||cmbSubRackName.getValue()==null)
				{
					subRack ="%"; 
				}
				else
				{
					subRack = cmbSubRackName.getValue().toString();
				}
		//category = cmbCategoryName.getValue().toString();
				//subCategory = (cmbSubCategoryName.getValue()==null?"%":cmbSubCategoryName.getValue().toString());

				//subsubcategoryid=(cmbsubsubCategory.getValue()==null?"%":cmbsubsubCategory.getValue().toString());
				//query =" select vRawItemCode,vRawItemName,vGroupName,vSubGroupName,vUnitName from tbRawItemInfo where vGroupId='"+category+"' and vSubGroupId like '"+subCategory+"' order by iAutoId ";
				query= " select vRawItemCode,replace(vRawItemName,'~','''') vRawItemName,vGroupName,"
						+ "vSubGroupName,vUnitName,vSubSubCategoryname,vRackId,vRackName,vSubRackId,"
						+ "vSubRackName,imageLoc from tbRawItemInfo where  vCategoryType like  '%Spare Parts%' and "
						+ "vGroupId like '"+category+"' and  vSubGroupId like '"+subCategory+"' and vRackId like '"+rack+"' and vSubRackId like '"+subRack+"' and vFlag='New' order by iAutoId desc "; 

				System.out.println("query is"+query);
			}
			/*else
			{
				query ="select vRawItemCode,vRawItemName,vGroupName,vSubGroupName,vUnitName from tbRawItemInfo order by iAutoId";
			}*/
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

				//	lbsubSubCategory.get(i).setValue(element[5].toString());
					
					//ImageforPopup(element[10].toString(),i);

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
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vGroupId,vGroupName  from  tbRawItemInfo where vCategoryType like '%Spare Parts%' and vFlag='New' ").list();

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
	
	public void cmbAddRackData()
	{
		cmbRackName.removeAllItems();
		String category="",subcategory="";
		if(chkAllCategory.booleanValue())
		{
			category ="%"; 
		}
		else
		{
			category = cmbCategoryName.getValue().toString();
		}
		if(chkAllSubCategory.booleanValue())
		{
			subcategory ="%"; 
		}
		else
		{
			subcategory = cmbSubCategoryName.getValue().toString();
		}

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
		String query="select distinct vRackId, vRackName  from  tbRawItemInfo where vGroupId like '"+category+"' and vSubGroupId like  '"+subcategory+"' and vCategoryType like '%Spare Parts%' and vFlag='New' ";
			List list=session.createSQLQuery(query).list();
			System.out.println(query);
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbRackName.addItem(element[0].toString());
				cmbRackName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}	
	}
	
	public void cmbAddSubRackData()
	{
		cmbSubRackName.removeAllItems();
		String rackId="";
		if(chkAllRack.booleanValue())
		{
			rackId ="%"; 
		}
		else
		{
			rackId = cmbRackName.getValue().toString();
		}
		
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select distinct vSubRackId, vSubRackName  from  tbRawItemInfo where vRackId like '"+rackId+"' and vFlag='New'").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSubRackName.addItem(element[0].toString());
				cmbSubRackName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
			
	}


	public void cmbAddSubcategoryData()
	{
		cmbSubCategoryName.removeAllItems();
		String category="";
		if(chkAllCategory.booleanValue()){
			category="%";
		}
		else{
			category=cmbCategoryName.getValue().toString();
		}
	
			Transaction tx=null;
			try
			{
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery(" SELECT vSubGroupId,vSubGroupName FROM tbRawItemInfo  where  vCategoryType  like  '%Spare Parts%' and  vGroupId like '"+category+"' and vFlag='New'  order by vSubGroupName").list();

				for(Iterator iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbSubCategoryName.addItem(element[0].toString());
					cmbSubCategoryName.setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch(Exception exp)
			{
				showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	


/*	public void cmbAddSubSubcategoryData()
	{
		cmbsubsubCategory.removeAllItems();
		if(cmbCategoryName.getValue()!=null)
		{
			Transaction tx=null;
			try
			{
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery("select  iSubSubCategoryID,vSubSubCategoryName  from tbRawItemsubSubCategory where SubGroupid like '"+cmbSubCategoryName.getValue().toString()+"' order by iSubSubCategoryID").list();

				for(Iterator iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbsubsubCategory.addItem(element[0].toString());
					cmbsubsubCategory.setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}*/

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
		cmbCategoryName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbCategoryName.setWidth("250px");
		
		chkAllCategory=new CheckBox("All");
		chkAllCategory.setImmediate(true);
		chkAllCategory.setValue(false);

		cmbSubCategoryName.setNullSelectionAllowed(true);
		cmbSubCategoryName.setImmediate(true);
		cmbSubCategoryName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSubCategoryName.setWidth("250px");
		
		chkAllSubCategory=new CheckBox("All");
		chkAllSubCategory.setImmediate(true);
		chkAllSubCategory.setValue(false);
		
		cmbRackName.setNullSelectionAllowed(false);
		cmbRackName.setImmediate(true);
		cmbRackName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbRackName.setWidth("200px");
		
		chkAllRack=new CheckBox("All");
		chkAllRack.setImmediate(true);
		chkAllRack.setValue(false);
		
		cmbSubRackName.setNullSelectionAllowed(true);
		cmbSubRackName.setImmediate(true);
		cmbSubRackName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSubRackName.setWidth("200px");
		
		chkAllSubRack=new CheckBox("All");
		chkAllSubRack.setImmediate(true);
		chkAllSubRack.setValue(false);

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

		table.addContainerProperty("Product View", PopupView.class, new PopupView("",null));
		table.setColumnWidth("Product View",80);
		
		table.setFooterVisible(true);
	}

	private AbsoluteLayout compAdd()
	{
		//mainLayout.addComponent(lblcategoryType, "top:20px; left:20px;");
		//mainLayout.addComponent(cmbCategoryType, "top:18px; left:120px;");
		mainLayout.addComponent(lblCategoryName, "top:20px; left:20px;");
		mainLayout.addComponent(cmbCategoryName, "top:18px; left:120px;");
		mainLayout.addComponent(chkAllCategory, "top:20.0px;left:370.0px;");
		mainLayout.addComponent(lblSubCategoryName, "top:50px; left:20px;");
		mainLayout.addComponent(cmbSubCategoryName, "top:48px; left:120px;");
		mainLayout.addComponent(chkAllSubCategory, "top:50.0px;left:370.0px;");
		mainLayout.addComponent(lblRackName, "top:20px; left:420px;");
		mainLayout.addComponent(cmbRackName, "top:18px; left:520px;");
		mainLayout.addComponent(chkAllRack, "top:20.0px;left:770.0px;");
		mainLayout.addComponent(lblSubRackName, "top:50px; left:420px;");
		mainLayout.addComponent(cmbSubRackName, "top:48px; left:520px;");
		mainLayout.addComponent(chkAllSubRack, "top:50.0px;left:770.0px;");
		//mainLayout.addComponent(lblsubsubCategoryname, "top:50px; left:350px;");
		//mainLayout.addComponent(cmbsubsubCategory, "top:48px; left:460px;");

		mainLayout.addComponent(cButton, "top:80px; left:120px;");
		//		mainLayout.addComponent(lblCountProduct, "top:; left:;");

		mainLayout.addComponent(table, "top:150px; left:0px;");

		return mainLayout;
	}
}