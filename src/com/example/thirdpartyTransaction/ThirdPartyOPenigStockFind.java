package com.example.thirdpartyTransaction;

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

public class ThirdPartyOPenigStockFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private NativeSelect cmbOpeningYear = new NativeSelect("Opening Year"); 
	private Table table=new Table();

	public String receiptProductId = "";
	private TextField txtReceiptProductId;

	public String receiptOpeningYear = "";
	private TextField txtReceiptOpeningYear;

	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lbProductId = new ArrayList<Label>();
	private ArrayList<Label> lbProductName = new ArrayList<Label>();
	private ArrayList<Label> lbCategory = new ArrayList<Label>();
	private ArrayList<Label> lbSubCategory = new ArrayList<Label>();
	private ArrayList<Label> lbUnit = new ArrayList<Label>();
	private ArrayList<Label> lblPartyName = new ArrayList<Label>();
	private ComboBox cmbSubCategoryName=new ComboBox("Sub-Category :") ;
	private ComboBox cmbCategoryType=new ComboBox("Category Type :");
	private ComboBox cmbCategoryName=new ComboBox("Category :") ;
	
	private NativeButton findButton = new NativeButton("Find");

	private SessionBean sessionBean;
	public ThirdPartyOPenigStockFind(SessionBean sessionBean,TextField txtReceiptProductId,TextField txtReceiptOpeningYear)
	{
		this.txtReceiptProductId = txtReceiptProductId;
		this.txtReceiptOpeningYear = txtReceiptOpeningYear;
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
		categoryTypeDataLoad();
	}
	
	public void categoryTypeDataLoad()
	{
		cmbCategoryType.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select 0, b.vCategoryType from tbThirdPartyOpening a inner join  tbThirdPartyItemInfo b on a.productid=b.vCode").list();

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

	private void yearDataAdding(){
		cmbOpeningYear.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select 0,DATEPART(yyyy,openingYear)as Date from tbThirdPartyOpening";
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

		lbCategory.add(ar, new Label(""));
		lbCategory.get(ar).setWidth("100%");
		lbCategory.get(ar).setImmediate(true);
		lbCategory.get(ar).setHeight("23px");

		lbSubCategory.add(ar, new Label(""));
		lbSubCategory.get(ar).setWidth("100%");
		lbSubCategory.get(ar).setImmediate(true);
		lbSubCategory.get(ar).setHeight("23px");
		
		
		lbUnit.add(ar, new Label(""));
		lbUnit.get(ar).setWidth("100%");
		lbUnit.get(ar).setImmediate(true);
		lbUnit.get(ar).setHeight("23px");
		
		
		lblPartyName.add(ar, new Label(""));
		lblPartyName.get(ar).setWidth("100%");
		lblPartyName.get(ar).setImmediate(true);
		lblPartyName.get(ar).setHeight("23px");
		

		table.addItem(new Object[]{lbSL.get(ar),lbProductId.get(ar),lbProductName.get(ar),lbUnit.get(ar),lblPartyName.get(ar),lbCategory.get(ar),lbSubCategory.get(ar)},ar);
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
						receiptProductId = lbProductId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						txtReceiptProductId.setValue(receiptProductId);

						receiptOpeningYear = cmbOpeningYear.getItemCaption(cmbOpeningYear.getValue());
						txtReceiptOpeningYear.setValue(receiptOpeningYear);

						windowClose();
					}
					else{
						getParent().showNotification("Select Opening Year",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});
		
		cmbCategoryName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbCategoryName.getValue()!=null)
				cmbAddSubcategoryData();
			}
		});
		
		cmbSubCategoryName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbSubCategoryName.getValue()!=null)
				{
					
				}
				
			}
		});
		
		cmbCategoryType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbCategoryType.getValue()!=null)
				cmbAddCategoryData();
				
			}
		});
		
		
		findButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				tableclear();
				if(cmbCategoryType.getValue()!=null){
					
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
						
						String sql=
								"select vCode,vItemName , a.vGroupName, "
								+"a.vSubGroupName,a.vUnitName,a.vPartyName from tbThirdPartyItemInfo a inner join tbThirdPartyOpening b "
								+"on a.vCode=b.productId where YEAR(openingYear)='"+cmbOpeningYear.getValue()+"'  and a.vGroupId='"+category+"' and a.vSubGroupId='"+subCategory+"' ";
						
						System.out.println(sql);
						List list=session.createSQLQuery(sql).list();
						if(!list.isEmpty()){
							int ar=0;
							for(Iterator iter=list.iterator();iter.hasNext();){
								Object element[]=(Object[]) iter.next();
								
								lbProductId.get(ar).setValue(element[0].toString());
								lbProductName.get(ar).setValue(element[1].toString());
								lbCategory.get(ar).setValue(element[2].toString());
								lbSubCategory.get(ar).setValue(element[3].toString());
								lbUnit.get(ar).setValue(element[4].toString());
								lblPartyName.get(ar).setValue(element[5].toString());
								//tableRowAdd(ar+1);
								if(ar==lbProductId.size()-1){
									tableRowAdd(ar+1);
								}
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
				else
				{
					getParent().showNotification("Please Select Category Type",Notification.TYPE_WARNING_MESSAGE);
					cmbCategoryType.focus();
				}

			}
		});
	}

	public void cmbAddCategoryData()
	{
		cmbCategoryName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			
		
		   String sql="select vGroupId,vGroupName from tbThirdPartyItemInfo a inner join tbThirdPartyOpening b "
				      +" on a.vCode=b.productId where vCategoryType='"+cmbCategoryType.getValue().toString()+"' ";

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
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				String sql="select vSubGroupId,vSubGroupName from tbThirdPartyItemInfo a inner join tbThirdPartyOpening b "
						   +"on a.vCode=b.productId where vGroupId='"+cmbCategoryName.getValue()+"' ";
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
	

	
	
	/*private void tableDataAdding()
	{
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String selectQuery = "Select Distinct ProductCode,ProductName,CategoryCode,categoryName,SubCategoryID,subCategoryName,Unit from vwOpeningStockNew";

			System.out.println("selectQuery : "+selectQuery);
			List list = session.createSQLQuery(selectQuery).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbProductId.get(i).setValue(element[0]);
					lbProductName.get(i).setValue(element[1]);

					if(!element[2].toString().equals("")){
						lbCategory.get(i).setValue(element[3]);
					}
					else{
						lbCategory.get(i).setValue("");
					}

					if(!element[4].toString().equals("")){
						lbSubCategory.get(i).setValue(element[5]);
					}
					else{
						lbSubCategory.get(i).setValue("");
					}

					lbUnit.get(i).setValue(element[6]);

					if((i)==lbProductId.size()-1) {
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else {
				tableclear();
				this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) {
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}*/

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

	private void windowClose(){
		this.close();
	}

	private void compInit()
	{
		cmbOpeningYear.setWidth("100px");
		cmbOpeningYear.setImmediate(true);
		cmbOpeningYear.setNullSelectionAllowed(true);
		
		cmbCategoryType.setWidth("200px");
		cmbCategoryType.setImmediate(true);
		cmbCategoryType.setNullSelectionAllowed(false);
		
		cmbCategoryName.setWidth("200px");
		cmbCategoryName.setImmediate(true);
		cmbCategoryName.setNullSelectionAllowed(false);
		
		cmbSubCategoryName.setWidth("250px");
		cmbSubCategoryName.setImmediate(true);
		cmbSubCategoryName.setNullSelectionAllowed(false);
		
		
		findButton.setWidth("80px");
		findButton.setHeight("28px");
		findButton.setIcon(new ThemeResource("../icons/print.png"));
		

		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("SL #", Label.class, new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Product ID", Label.class, new Label());
		table.setColumnWidth("Product ID",65);
		table.setColumnCollapsed("Product ID", true);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name",150);
		
		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",55);
		
		table.addContainerProperty("Party Name", Label.class, new Label());
		table.setColumnWidth("Party Name",150);

		table.addContainerProperty("Category Name", Label.class, new Label());
		table.setColumnWidth("Category Name",150);

		table.addContainerProperty("Sub Category Name", Label.class, new Label());
		table.setColumnWidth("Sub Category Name",150);
		
		
		
	}

	private void compAdd()
	{
		cmbLayout.addComponent(cmbOpeningYear);
		cmbLayout.addComponent(cmbCategoryType);
		cmbLayout.addComponent(cmbCategoryName);
		cmbLayout.addComponent(cmbSubCategoryName);
		cmbLayout.addComponent(findButton);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(table);
		mainLayout.addComponent(btnLayout);
		addComponent(mainLayout);
	}
}