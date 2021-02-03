package com.example.rawMaterialSetup;

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

public class RawItemCategoryFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();
	private Label lblCategoryName=new Label("Category :");
	private ComboBox cmbCategoryName=new ComboBox() ;
	private String[] co=new String[]{"a","b"};
	public String receiptSupplierId = "";

	private ArrayList<Label> lbCategoryID = new ArrayList<Label>();
	private ArrayList<Label> lbCategoryName = new ArrayList<Label>();

	private SessionBean sessionBean;
	public RawItemCategoryFind(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND CATEGORY INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("520px");
		this.setHeight("320");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
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
		lbCategoryID.add(ar, new Label(""));
		lbCategoryID.get(ar).setWidth("100%");
		lbCategoryID.get(ar).setImmediate(true);
		lbCategoryID.get(ar).setHeight("23px");

		lbCategoryName.add(ar, new Label(""));
		lbCategoryName.get(ar).setWidth("100%");
		lbCategoryName.get(ar).setImmediate(true);
		lbCategoryName.get(ar).setHeight("23px");

		table.addItem(new Object[]{lbCategoryID.get(ar),lbCategoryName.get(ar)},ar);
	}

	public void setEventAction()
	{
		cmbCategoryName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbCategoryName.getValue()!=null)
				{
					tableclear();
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
					receiptSupplierId = lbCategoryID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSupplierId);
					windowClose();
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
			List list=session.createSQLQuery("Select iCategoryCode, vCategoryName  from  tbRawItemCategory order by vCategoryName").list();

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
	private void tableclear()
	{
		for(int i=0; i<lbCategoryID.size(); i++)
		{
			lbCategoryID.get(i).setValue("");
			lbCategoryName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query ="Select iCategoryCode,vCategoryName FROM tbRawItemCategory Where  iCategoryCode like '"+cmbCategoryName.getValue()+"' order by iCategoryCode";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbCategoryID.get(i).setValue(element[0]);
					lbCategoryName.get(i).setValue(element[1]);

					if((i)==lbCategoryID.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else {
				tableclear();
				this.getParent().showNotification("Data not found !!", Notification.TYPE_WARNING_MESSAGE); 
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

		cmbCategoryName.setNullSelectionAllowed(false);
		cmbCategoryName.setImmediate(true);
		cmbCategoryName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbCategoryName.setWidth("360px");
		cmbCategoryName.setInputPrompt("Select Category");		
		
		
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("185px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Cate ID", Label.class, new Label());
		table.setColumnWidth("Cate ID",50);

		table.addContainerProperty("Category Name", Label.class, new Label());
		table.setColumnWidth("Category Name",325);
	}

	private void compAdd()
	{
		//cmbLayout.setSpacing(true);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(cmbCategoryName);
		mainLayout.addComponent(table);
		mainLayout.addComponent(btnLayout);
		addComponent(mainLayout);
	}
}