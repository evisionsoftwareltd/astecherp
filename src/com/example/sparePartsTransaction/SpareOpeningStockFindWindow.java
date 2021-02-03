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
import com.vaadin.ui.Window.Notification;

public class SpareOpeningStockFindWindow extends Window
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

	private SessionBean sessionBean;
	public SpareOpeningStockFindWindow(SessionBean sessionBean,TextField txtReceiptProductId,TextField txtReceiptOpeningYear)
	{
		this.txtReceiptProductId = txtReceiptProductId;
		this.txtReceiptOpeningYear = txtReceiptOpeningYear;
		this.sessionBean=sessionBean;
		this.setCaption("FIND OPENING STOCK INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("650px");
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
			String sql="select 0,DATEPART(yyyy,openingYear)as Date from tbSpareProductOpening";
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

		table.addItem(new Object[]{lbSL.get(ar),lbProductId.get(ar),lbProductName.get(ar),lbCategory.get(ar),lbSubCategory.get(ar),lbUnit.get(ar)},ar);
	}

	public void setEventAction()
	{
		cmbOpeningYear.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				
				tableclear();
				if(cmbOpeningYear.getValue()!=null){
					
					Transaction tx=null;
					try{
						Session session=SessionFactoryUtil.getInstance().getCurrentSession();
						tx=session.beginTransaction();
						/*String sql="select po.ProductId,pi.ProductName,rc.CategoryName,sc.SubCategoryName,pi.Unit from tbRawProductOpening po"
									+" inner join tbRawProductInfo pi on po.ProductId=pi.ProductCode"
									+" inner join tbRawCategory rc on pi.CategoryCode=rc.CategoryCode"
									+" inner join tbRawSubCategory sc on pi.subCategoryCode=sc.SubCategoryID"
									+" where DATEPART(YYYY,openingYear)like'%"+cmbOpeningYear.getValue()+"%'";*/
						
						
						
						
						
						/*String sql= "select ProductCode,b.ProductName,c.Group_Name, ISNULL(d.Sub_Group_Name,'') as subgroup  from tbRawProductOpening a  inner join tbRawProductInfo b " 
							        +"on a.ProductId=b.ProductCode "
								    +"inner join tbMain_Group c "
								   + " on c.Group_Id=b.groupId "
								    +"left join  "
								    +"tbSub_Group d "
								    +"on d.Sub_Group_Id=b.subgroupId "
								   + "where DATEPART(YYYY,openingYear)like '"+cmbOpeningYear.getValue()+"' "; */
						
						/*String sql="select b.vSpareItemCode,b.vSpareItemCode,c.Group_Name, ISNULL(d.Sub_Group_Name,'') as subgroup,b.vUnitName  "
								+" from tbSpareProductOpening a  inner join tbSpareItemInfo b on a.ProductId=b.vRawItemCode "
								+" inner join tbMain_Group c  on c.Group_Id=b.vGroupId "
								+" left join  tbSub_Group d on d.Sub_Group_Id=b.vSubGroupId where DATEPART(YYYY,openingYear) like '"+cmbOpeningYear.getValue().toString()+"' ";*/
						
						String sql="select b.productId, b.productName, c.vCategoryName, d.vSubCategoryName, b.unit from tbSpareProductOpening a inner join tbSpareProductDetails b " 
								+"on a.productId=b.productId inner join tbRawItemCategory c on b.categoryId=c.Group_Id left outer join "
								+"tbRawItemSubCategory d on d.SubGroup_Id=b.subCategoryId";
						
						System.out.println(sql);
						List list=session.createSQLQuery(sql).list();
						if(!list.isEmpty()){
							int ar=0;
							for(Iterator iter=list.iterator();iter.hasNext();){
								Object element[]=(Object[]) iter.next();
								
								lbProductId.get(ar).setValue(element[0].toString());
								lbProductName.get(ar).setValue(element[1].toString());
								lbCategory.get(ar).setValue(element[2].toString());
								lbSubCategory.get(ar).setValue(element[3]==null?"":element[3].toString());
								lbUnit.get(ar).setValue(element[4].toString());
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

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",55);
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