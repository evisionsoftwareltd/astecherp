package com.example.sparePartsTransaction;

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

public class OpeningStockFindWindow extends Window
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
	private ArrayList<Label> lbsubsubcategory = new ArrayList<Label>();
	
	private ComboBox cmbSubCategoryName=new ComboBox("Sub-Category :") ;
	private ComboBox cmbsubsubCategory= new ComboBox("sub Sub-Category :");
	private ComboBox cmbCategoryType=new ComboBox("Category Type :");
	private ComboBox cmbCategoryName=new ComboBox("Category :") ;
	
	private NativeButton findButton = new NativeButton("Find");

	private SessionBean sessionBean;
	public OpeningStockFindWindow(SessionBean sessionBean,TextField txtReceiptProductId,TextField txtReceiptOpeningYear)
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
		//yrarDataAdding();
		yearDataAdding();
		tableclear();
		//tableDataAdding();
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
			List list=session.createSQLQuery("select distinct 0,vCategoryType from tbRawItemCategory where vCategoryType not like 'Spare Parts' ").list();

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

	/*public void yrarDataAdding()
	{
		cmbOpeningYear.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("Select Distinct datepart(year,openingYear) as openingYear,datepart(year,openingYear) as openingYear from vwOpeningStockNew").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbOpeningYear.addItem(element[0]);
				cmbOpeningYear.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}*/
	private void yearDataAdding(){
		cmbOpeningYear.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select 0,DATEPART(yyyy,openingYear)as Date from tbRawProductOpening";
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
		
		lbsubsubcategory.add(ar, new Label(""));
		lbsubsubcategory.get(ar).setWidth("100%");
		lbsubsubcategory.get(ar).setImmediate(true);
		lbsubsubcategory.get(ar).setHeight("23px");
		

		lbUnit.add(ar, new Label(""));
		lbUnit.get(ar).setWidth("100%");
		lbUnit.get(ar).setImmediate(true);
		lbUnit.get(ar).setHeight("23px");

		table.addItem(new Object[]{lbSL.get(ar),lbProductId.get(ar),lbProductName.get(ar),lbCategory.get(ar),lbSubCategory.get(ar),lbsubsubcategory.get(ar),lbUnit.get(ar)},ar);
	}

	public void setEventAction()
	{
		cmbOpeningYear.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				
				/*tableclear();
				if(cmbOpeningYear.getValue()!=null){
					
					Transaction tx=null;
					try{
						Session session=SessionFactoryUtil.getInstance().getCurrentSession();
						tx=session.beginTransaction();
						String sql="select po.ProductId,pi.ProductName,rc.CategoryName,sc.SubCategoryName,pi.Unit from tbRawProductOpening po"
									+" inner join tbRawProductInfo pi on po.ProductId=pi.ProductCode"
									+" inner join tbRawCategory rc on pi.CategoryCode=rc.CategoryCode"
									+" inner join tbRawSubCategory sc on pi.subCategoryCode=sc.SubCategoryID"
									+" where DATEPART(YYYY,openingYear)like'%"+cmbOpeningYear.getValue()+"%'";
						
						
						
						
						
						String sql= "select ProductCode,b.ProductName,c.Group_Name, ISNULL(d.Sub_Group_Name,'') as subgroup  from tbRawProductOpening a  inner join tbRawProductInfo b " 
							        +"on a.ProductId=b.ProductCode "
								    +"inner join tbMain_Group c "
								   + " on c.Group_Id=b.groupId "
								    +"left join  "
								    +"tbSub_Group d "
								    +"on d.Sub_Group_Id=b.subgroupId "
								   + "where DATEPART(YYYY,openingYear)like '"+cmbOpeningYear.getValue()+"' "; 
						
						String sql="select b.vRawItemCode,b.vRawItemCode,c.Group_Name, ISNULL(d.Sub_Group_Name,'') as subgroup,b.vUnitName  "
								+" from tbRawProductOpening a  inner join tbRawItemInfo b on a.ProductId=b.vRawItemCode "
								+" inner join tbMain_Group c  on c.Group_Id=b.vGroupId "
								+" left join  tbSub_Group d on d.Sub_Group_Id=b.vSubGroupId  where DATEPART(YYYY,openingYear)like '"+cmbOpeningYear.getValue()+"' ";
						
						String sql=" select b.vRawItemCode,b.vRawItemName,c.Group_Name, ISNULL(d.Sub_Group_Name,'') as subgroup,b.vUnitName,e.vSubSubCategoryName   "
								 + " from tbRawProductOpening a  inner join tbRawItemInfo b on a.ProductId=b.vRawItemCode  "
								 + " inner join tbMain_Group c  on c.Group_Id=b.vGroupId  "
								 + " left join  tbSub_Group d on d.Sub_Group_Id=b.vSubGroupId "
								 + " left join "
								 + " tbRawItemsubSubCategory e "
								 + " on e.iSubSubCategoryID=b.vsubsubCategoryId "
								 + " where DATEPART(YYYY,openingYear)like '"+cmbOpeningYear.getValue()+"'  ";
						
						
						
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
								lbsubsubcategory.get(ar).setValue(element[5].toString());
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
				}*/
			}
		});
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
				cmbAddSubSubcategoryData();
			}
		});
		
		cmbCategoryType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbCategoryType.getValue()!=null)
				cmbAddCategoryData();
				
			}
		});
		
		cmbsubsubCategory.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				
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
						/*String sql="select po.ProductId,pi.ProductName,rc.CategoryName,sc.SubCategoryName,pi.Unit from tbRawProductOpening po"
									+" inner join tbRawProductInfo pi on po.ProductId=pi.ProductCode"
									+" inner join tbRawCategory rc on pi.CategoryCode=rc.CategoryCode"
									+" inner join tbRawSubCategory sc on pi.subCategoryCode=sc.SubCategoryID"
									+" where DATEPART(YYYY,openingYear)like'%"+cmbOpeningYear.getValue()+"%'";*/
						
						
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
						
						if(cmbsubsubCategory.getValue()!=null)
						{
							subSubCategory = cmbsubsubCategory.getValue().toString();
						}
						else
						{
							subSubCategory = "%";
						}
						
						/*String sql= "select ProductCode,b.ProductName,c.Group_Name, ISNULL(d.Sub_Group_Name,'') as subgroup  from tbRawProductOpening a  inner join tbRawProductInfo b " 
							        +"on a.ProductId=b.ProductCode "
								    +"inner join tbMain_Group c "
								   + " on c.Group_Id=b.groupId "
								    +"left join  "
								    +"tbSub_Group d "
								    +"on d.Sub_Group_Id=b.subgroupId "
								   + "where DATEPART(YYYY,openingYear)like '"+cmbOpeningYear.getValue()+"' "; */
						
/*						String sql="select b.vRawItemCode,b.vRawItemCode,c.Group_Name, ISNULL(d.Sub_Group_Name,'') as subgroup,b.vUnitName  "
								+" from tbRawProductOpening a  inner join tbRawItemInfo b on a.ProductId=b.vRawItemCode "
								+" inner join tbMain_Group c  on c.Group_Id=b.vGroupId "
								+" left join  tbSub_Group d on d.Sub_Group_Id=b.vSubGroupId  where DATEPART(YYYY,openingYear)like '"+cmbOpeningYear.getValue()+"' ";*/
						
						/*String sql=" select b.vRawItemCode,b.vRawItemName,c.Group_Name, ISNULL(d.Sub_Group_Name,'') as subgroup,b.vUnitName,isnull(e.vSubSubCategoryName,'') as  vSubSubCategoryName  "
								 + " from tbRawProductOpening a  inner join tbRawItemInfo b on a.ProductId=b.vRawItemCode  "
								 + " inner join tbMain_Group c  on c.Group_Id=b.vGroupId  "
								 + " left join  tbSub_Group d on d.Sub_Group_Id=b.vSubGroupId "
								 + " left join "
								 + " tbRawItemsubSubCategory e "
								 + " on e.iSubSubCategoryID=b.vsubsubCategoryId "
								 + " where DATEPART(YYYY,openingYear)like '"+cmbOpeningYear.getValue()+"' and b.vGroupId    like '"+category+"' and b.vSubGroupId like '"+subCategory+"' and b.vsubsubCategoryId  like '"+subSubCategory+"' and b.vCategoryType like '"+cmbCategoryType.getValue().toString()+"'  ";*/
						
						String sql="select b.vRawItemCode,b.vRawItemName,c.vCategoryName, ISNULL(d.vSubCategoryName,'') as subgroup, "+
								"b.vUnitName,isnull(e.vSubSubCategoryName,'') as  vSubSubCategoryName   from tbRawProductOpening a   "+
								"inner join tbRawItemInfo b on a.ProductId=b.vRawItemCode    "+
								"inner join tbRawItemCategory c  on c.Group_Id=b.vGroupId    "+
								"left join  tbRawItemSubCategory d on d.iSubCategoryID=b.vSubGroupId   "+
								"left join  tbRawItemsubSubCategory e  on e.iSubSubCategoryID=b.vsubsubCategoryId   "+
								"where DATEPART(YYYY,openingYear)like '"+cmbOpeningYear.getValue()+"' and b.vGroupId    like '"+category+"' and b.vSubGroupId like '"+subCategory+"'  "+
								"and b.vsubsubCategoryId  like '"+subSubCategory+"' and b.vCategoryType like '"+cmbCategoryType.getValue().toString()+"' ";
						
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
								lbsubsubcategory.get(ar).setValue(element[5].toString());
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
			List list=session.createSQLQuery("   select iCategoryCode, vCategoryName  from  tbRawItemCategory where vCategoryType like '"+cmbCategoryType.getValue().toString()+"' ").list();

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
				List list=session.createSQLQuery(" SELECT iSubCategoryID,vSubCategoryName FROM tbRawItemSubCategory where Group_Id='"+cmbCategoryName.getValue().toString()+"' order by iSubCategoryID ").list();

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
			lbsubsubcategory.get(i).setValue("");
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
		
		cmbsubsubCategory.setWidth("150px");
		cmbsubsubCategory.setImmediate(true);
		cmbsubsubCategory.setNullSelectionAllowed(false);
		
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

		table.addContainerProperty("Category Name", Label.class, new Label());
		table.setColumnWidth("Category Name",150);

		table.addContainerProperty("Sub Category Name", Label.class, new Label());
		table.setColumnWidth("Sub Category Name",150);
		
		table.addContainerProperty("Sub Sub-Category Name", Label.class, new Label());
		table.setColumnWidth("Sub Sub-Category Name",150);
		

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",55);
	}

	private void compAdd()
	{
		cmbLayout.addComponent(cmbOpeningYear);
		cmbLayout.addComponent(cmbCategoryType);
		cmbLayout.addComponent(cmbCategoryName);
		cmbLayout.addComponent(cmbSubCategoryName);
		cmbLayout.addComponent(cmbsubsubCategory);
		cmbLayout.addComponent(findButton);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(table);
		mainLayout.addComponent(btnLayout);
		addComponent(mainLayout);
	}
}