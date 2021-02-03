package com.example.sparePartsSetup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
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
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class SpareItemSubCategoryFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private Label lblCategory = new Label("Category :");
	private ComboBox cmbCategory = new ComboBox();
	
	private String[] co=new String[]{"a","b"};
	public String receiptSubCateId = "";

	private ArrayList<Label> lbSubCategoryID = new ArrayList<Label>();
	private ArrayList<Label> lbSubCategoryName = new ArrayList<Label>();

	private SessionBean sessionBean;
	public SpareItemSubCategoryFind(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SPARE SUB CATEGORY INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("470px");
		this.setHeight("300px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		tableclear();
		cmbCategoryData();
//		tableDataAdding();
	}
	
	private void cmbCategoryData()
	{
		cmbCategory.removeAllItems();
		
		Transaction tx = null;
		
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "";
			query = "select distinct a.iCategoryID, b.vCategoryName from tbRawItemSubCategory a" +
							" inner join" +
							" tbRawItemCategory b" +
							" on a.iCategoryID = b.iCategoryCode where b.vCategoryType  like '%Spare Parts%'  and b.vFlag='New'" +
							" order by  b.vCategoryName";
			System.out.println();
			
			List list = session.createSQLQuery(query).list();
			
			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				
				cmbCategory.addItem(element[0].toString());
				cmbCategory.setItemCaption(element[0].toString(), element[1].toString());
			}
			
		}
		catch(Exception ex)
		{
			showNotification("Error !: ",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
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
		lbSubCategoryName.add(ar, new Label(""));
		lbSubCategoryName.get(ar).setWidth("100%");
		lbSubCategoryName.get(ar).setImmediate(true);
		lbSubCategoryName.get(ar).setHeight("23px");
		
		lbSubCategoryID.add(ar, new Label(""));
		lbSubCategoryID.get(ar).setWidth("100%");
		lbSubCategoryID.get(ar).setImmediate(true);
		lbSubCategoryID.get(ar).setHeight("23px");		

		table.addItem(new Object[]{lbSubCategoryName.get(ar),lbSubCategoryID.get(ar)},ar);
	}

	public void setEventAction()
	{
		cmbCategory.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbCategory.getValue()!=null)
				{
					tableDataAdding();
				}
			}
		});
		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSubCateId = lbSubCategoryID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSubCateId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbSubCategoryID.size(); i++)
		{
			lbSubCategoryID.get(i).setValue("");
			lbSubCategoryName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query ="Select iSubCategoryID,vSubCategoryName from tbRawItemSubCategory where iCategoryID like '"+cmbCategory.getValue().toString()+"'  and vFlag='New' order by iAutoId desc";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbSubCategoryID.get(i).setValue(element[0]);
					lbSubCategoryName.get(i).setValue(element[1]);

					if((i)==lbSubCategoryID.size()-1) {
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
	}

	private void windowClose()
	{
		this.close();
	}

	private void compInit()
	{
		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("175px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		
		
		table.addContainerProperty("Sub-Category Name", Label.class, new Label());
		table.setColumnWidth("Sub-Category Name",270);

		table.addContainerProperty("Sub-Category ID", Label.class, new Label());
		table.setColumnWidth("Sub-Category ID",100);		
		table.setColumnAlignment("Sub-Category ID", table.ALIGN_CENTER);
		
		lblCategory.setWidth("60px");
		
		cmbCategory.setImmediate(true);
		cmbCategory.setWidth("180px");
		cmbCategory.setHeight("-1px");
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		btnLayout.addComponent(lblCategory);
		btnLayout.addComponent(cmbCategory);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}