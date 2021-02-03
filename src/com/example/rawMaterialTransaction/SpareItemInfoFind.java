package com.example.rawMaterialTransaction;

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
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class SpareItemInfoFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private HorizontalLayout hLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;

	private Table table=new Table();

	public String receiptItemId = "";

	private Label lblCategoryName=new Label("Category");
	private ComboBox cmbCategoryName=new ComboBox() ;

	private Label lblSubCategoryName=new Label("Sub-Category");
	private ComboBox cmbSubCategoryName=new ComboBox() ;

	private ArrayList<Label> lbProductId = new ArrayList<Label>();
	private ArrayList<Label> lbProductName = new ArrayList<Label>();
	private ArrayList<Label> lbCategory = new ArrayList<Label>();
	private ArrayList<Label> lbSubCategory= new ArrayList<Label>();
	private ArrayList<Label> lbUnit = new ArrayList<Label>();

	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private Label lblCountProduct=new Label();

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;

	public SpareItemInfoFind(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SPARE ITEM INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("700px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");

		compInit();
		compAdd();
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

		lbUnit.add(ar, new Label(""));
		lbUnit.get(ar).setWidth("100%");
		lbUnit.get(ar).setImmediate(true);

		table.addItem(new Object[]{lbProductId.get(ar),lbProductName.get(ar),lbCategory.get(ar),lbSubCategory.get(ar),lbUnit.get(ar)},ar);
	}

	public void setEventAction()
	{
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

				cmbAddSubcategoryData();
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				if(cmbCategoryName.getValue()==null)
				{
					showNotification("Please Select Category Name ", Notification.TYPE_WARNING_MESSAGE);
					cmbCategoryName.focus();
				}
				else 
				{
					tableclear();
					tableDataAdding();
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

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			if(isFind)
			{
				category = cmbCategoryName.getValue().toString();
				subCategory = (cmbSubCategoryName.getValue()==null?"%":cmbSubCategoryName.getValue().toString());

				query =" select vSpareItemCode,vSpareItemName,vGroupName,vSubGroupName,vUnitName from tbSpareItemInfo where vGroupId='"+category+"' and vSubGroupId like '"+subCategory+"' order by iAutoId ";
			}
			else
			{
				query ="select vSpareItemCode,vSpareItemName,vGroupName,vSubGroupName,vUnitName from tbSpareItemInfo order by iAutoId";
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


					if(element[4].toString().equals("")){
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
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT Group_Id,vCategoryName from tbRawItemCategory order by iCategoryCode ").list();

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
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery(" SELECT SubGroup_Id,vSubCategoryName FROM tbRawItemSubCategory where Group_Id='"+cmbCategoryName.getValue().toString()+"' order by iSubCategoryID ").list();

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

		cmbCategoryName.setNullSelectionAllowed(true);
		cmbCategoryName.setImmediate(true);
		cmbCategoryName.setWidth("200px");

		cmbSubCategoryName.setNullSelectionAllowed(true);
		cmbSubCategoryName.setImmediate(true);
		cmbSubCategoryName.setWidth("200px");

		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("500px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Product Id", Label.class, new Label());
		table.setColumnWidth("Product Id",80);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name",180);

		table.addContainerProperty("Category Name", Label.class, new Label());
		table.setColumnWidth("Category Name",100);

		table.addContainerProperty("Sub Category Name", Label.class, new Label());
		table.setColumnWidth("Category Category Name",100);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",40);

		table.setFooterVisible(true);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		hLayout.setSpacing(true);
		btnLayout.setSpacing(true);
		hLayout.addComponent(lblCategoryName);
		hLayout.addComponent(cmbCategoryName);
		hLayout.addComponent(lblSubCategoryName);
		hLayout.addComponent(cmbSubCategoryName);
		hLayout.addComponent(cButton);
		mainLayout.addComponent(lblCountProduct);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}