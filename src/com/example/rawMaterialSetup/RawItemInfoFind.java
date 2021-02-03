package com.example.rawMaterialSetup;

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

public class RawItemInfoFind extends Window
{
	private AbsoluteLayout mainLayout;

	private TextField txtReceiptSupplierId;

	private Table table=new Table();

	public String receiptItemId = "";

	private Label lblCategoryName=new Label("Category :");
	private ComboBox cmbCategoryName=new ComboBox() ;

	private Label lblSubCategoryName=new Label("Sub-Category :");
	private ComboBox cmbSubCategoryName=new ComboBox() ;
	private Label lblsubsubCategoryname=new Label("sub Sub-Category :");
	private ComboBox cmbsubsubCategory= new ComboBox();
	private Label lblcategoryType=new Label("Category Type :");
	private ComboBox cmbCategoryType=new ComboBox();

	private ArrayList<Label> lbProductId = new ArrayList<Label>();
	private ArrayList<Label> lbProductName = new ArrayList<Label>();
	private ArrayList<Label> lbCategory = new ArrayList<Label>();
	private ArrayList<Label> lbSubCategory= new ArrayList<Label>();
	private ArrayList<Label> lbUnit = new ArrayList<Label>();
	
	private ArrayList<PopupView> btnPopupView = new ArrayList<PopupView>();
	private ArrayList<Panel> PImage = new ArrayList<Panel>();
	
	
	///////////////////////
	private CheckBox chkAllCategory;
	private CheckBox chkAllSubCategory;
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private Label lblCountProduct=new Label();

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;
	public  RawItemInfoFind(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND RAW ITEM INFORMATION :: "+sessionBean.getCompany());
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
		categoryTypeDataLoad();
		//cmbAddCategoryData();
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

		cmbCategoryType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbCategoryType.getValue()!=null)
				{
					cmbAddCategoryData();
				}

			}
		});
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				if( cmbCategoryType.getValue().toString()!=null )
				{
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
				else 
				{
					showNotification("Please select Category Type. ", Notification.TYPE_WARNING_MESSAGE);
					cmbCategoryType.focus();

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
			//btnPopupView.get(i).setVisible(false);
			//lbsubSubCategory.get(i).setValue("");
		}
	}

	public void ImageforPopup(String img,int i)
	{
		if(!img.equals("0")&&!img.equals("null")){
			File  fileStu_I = new File(img);
			Embedded eStu_I = new Embedded("",new FileResource(fileStu_I, getApplication()));
			eStu_I.requestRepaint();
			eStu_I.setWidth("260px");
			eStu_I.setHeight("350px");
			PImage.get(i).removeAllComponents();
			PImage.get(i).addComponent(eStu_I);
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
				//category = cmbCategoryName.getValue().toString();
				//subCategory = (cmbSubCategoryName.getValue()==null?"%":cmbSubCategoryName.getValue().toString());

				//subsubcategoryid=(cmbsubsubCategory.getValue()==null?"%":cmbsubsubCategory.getValue().toString());


				//query =" select vRawItemCode,vRawItemName,vGroupName,vSubGroupName,vUnitName from tbRawItemInfo where vGroupId='"+category+"' and vSubGroupId like '"+subCategory+"' order by iAutoId ";
				query= "Select vRawItemCode,replace(vRawItemName,'~','''') vRawItemName,vGroupName,vSubGroupName,vUnitName,vSubSubCategoryname,imageLoc from tbRawItemInfo where  vCategoryType = '"+cmbCategoryType.getValue().toString()+"' and vGroupId like '"+category+"' and  vSubGroupId like '"+subCategory+"' order by iAutoId "; 

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
					
					if(!element[6].toString().equals("0")&&!element[6].toString().equals("null")){
						ImageforPopup(element[6].toString(),i);
					}
					else{
						btnPopupView.get(i).setVisible(false);
					}
					//lbsubSubCategory.get(i).setValue(element[5].toString());
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
		/*if(chkAllCategory.booleanValue()){
			type="%";
		}*/
		if(cmbCategoryType.getValue()!=null){
			type=cmbCategoryType.getValue().toString();
		}

		if(cmbCategoryType.getValue()!=null)
		{
			Transaction tx=null;
			try
			{
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery("select iCategoryCode, vCategoryName  from  tbRawItemCategory where vCategoryType like '"+type+"' ").list();

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
	}


	public void categoryTypeDataLoad()
	{
		cmbCategoryType.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("Select distinct 0,vCategoryType from tbRawItemCategory").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbCategoryType.addItem(element[1].toString());
				cmbCategoryType.setItemCaption(element[1].toString(), element[1].toString());
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
				List list=session.createSQLQuery(" SELECT iSubCategoryID,vSubCategoryName FROM tbRawItemSubCategory where  vCategoryType = '"+cmbCategoryType.getValue().toString()+"' and  Group_Id like '"+type+"' order by iSubCategoryID ").list();

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


	public void cmbAddSubSubcategoryData()
	{
		cmbsubsubCategory.removeAllItems();
		if(cmbCategoryName.getValue()!=null)
		{
			Transaction tx=null;
			try
			{
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery("select iSubSubCategoryID,vSubSubCategoryName  from tbRawItemsubSubCategory where SubGroupid like '"+cmbSubCategoryName.getValue().toString()+"' order by iSubSubCategoryID").list();

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
	}

	private void compInit()
	{		
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		cmbCategoryType.setNullSelectionAllowed(false);
		cmbCategoryType.setImmediate(true);
		cmbCategoryType.setWidth("200px");

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

		table.addContainerProperty("Product View", PopupView.class, new PopupView("",null));
		table.setColumnWidth("Product View",80);

		table.setFooterVisible(true);
	}

	private AbsoluteLayout compAdd()
	{
		mainLayout.addComponent(lblcategoryType, "top:20px; left:20px;");
		mainLayout.addComponent(cmbCategoryType, "top:18px; left:120px;");
		mainLayout.addComponent(lblCategoryName, "top:50px; left:20px;");
		mainLayout.addComponent(cmbCategoryName, "top:48px; left:120px;");
		mainLayout.addComponent(chkAllCategory, "top:50.0px;left:370.0px;");
		mainLayout.addComponent(lblSubCategoryName, "top:20px; left:350px;");
		mainLayout.addComponent(cmbSubCategoryName, "top:18px; left:460px;");
		mainLayout.addComponent(chkAllSubCategory, "top:20.0px;left:720.0px;");
		//mainLayout.addComponent(lblsubsubCategoryname, "top:50px; left:350px;");
		//mainLayout.addComponent(cmbsubsubCategory, "top:48px; left:460px;");

		mainLayout.addComponent(cButton, "top:80px; left:120px;");
		//		mainLayout.addComponent(lblCountProduct, "top:; left:;");

		mainLayout.addComponent(table, "top:150px; left:0px;");

		return mainLayout;
	}
}