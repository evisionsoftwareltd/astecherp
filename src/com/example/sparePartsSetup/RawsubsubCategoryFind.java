package com.example.sparePartsSetup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class RawsubsubCategoryFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private HorizontalLayout hLayout2 = new HorizontalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();
	
	private Label lblCategory = new Label("Category :");
	private Label lblSubCategory = new Label("Sub-Category :");
	
	private ComboBox cmbCategory = new ComboBox();
	private ComboBox cmbSubCategory = new ComboBox(); 

	private String[] co=new String[]{"a","b"};
	public String receiptSubCateId = "";

	private ArrayList<Label> lbsubSubCategoryID = new ArrayList<Label>();
	private ArrayList<Label> lbSubCategoryName = new ArrayList<Label>();
	
	private OptionGroup type;
	private List <String> Categorytype=Arrays.asList(new String[]{"Raw Material","Packing Material","Spare Parts"});

	private SessionBean sessionBean;
	public RawsubsubCategoryFind(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND  SUB SUBCATEGORY INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("470px");
		this.setHeight("350px");
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
			query = "select c.iCategoryCode, c.vCategoryName from tbRawItemsubSubCategory a" +
							" inner join" +
							" tbRawItemSubCategory b" +
							" on b.iSubCategoryID = a.iSubCategoryid" +
							" inner join" +
							" tbRawItemCategory c" +
							" on c.iCategoryCode = b.iCategoryID" +
							" order by c.vCategoryName";
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
			this.getParent().showNotification("Error cmbCategoryDate(): ",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void cmbSubCategoryData()
	{
		cmbSubCategory.removeAllItems();
		
		Transaction tx = null;
		
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "";
			query = "select a.iSubCategoryid, b.vSubCategoryName from tbRawItemsubSubCategory a" +
					" inner join" +
					" tbRawItemSubCategory b" +
					" on b.iSubCategoryID = a.iSubCategoryid" +
					" where b.iCategoryID like '"+cmbCategory.getValue().toString()+"'" +
					" order by b.vSubCategoryName";
			System.out.println();
			
			List list = session.createSQLQuery(query).list();
			
			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				
				cmbSubCategory.addItem(element[0].toString());
				cmbSubCategory.setItemCaption(element[0].toString(), element[1].toString());
			}
			
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error cmbCategoryDate(): ",ex+"",Notification.TYPE_ERROR_MESSAGE);
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
		
		lbsubSubCategoryID.add(ar, new Label(""));
		lbsubSubCategoryID.get(ar).setWidth("100%");
		lbsubSubCategoryID.get(ar).setImmediate(true);
		lbsubSubCategoryID.get(ar).setHeight("23px");		

		table.addItem(new Object[]{lbSubCategoryName.get(ar), lbsubSubCategoryID.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSubCateId = lbsubSubCategoryID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSubCateId);
					windowClose();
				}
			}
		});
		
		cmbCategory.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbCategory.getValue()!=null)
				{
					cmbSubCategoryData();
				}
			}
		});
		
		cmbSubCategory.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbSubCategory.getValue()!=null)
				{
					tableDataAdding();
				}
				else
				{
					tableclear();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbsubSubCategoryID.size(); i++)
		{
			lbsubSubCategoryID.get(i).setValue("");
			lbSubCategoryName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		tableclear();
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query ="select iSubSubCategoryID,vSubSubCategoryName from  tbRawItemSubSubCategory where iSubCategoryid like '"+cmbSubCategory.getValue().toString()+"' order by vSubSubCategoryName";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbsubSubCategoryID.get(i).setValue(element[0]);
					lbSubCategoryName.get(i).setValue(element[1]);

					if((i)==lbsubSubCategoryID.size()-1) {
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
		
		table.addContainerProperty("Sub Sub-Category Name", Label.class, new Label());
		table.setColumnWidth("Sub Sub-Category Name",270);

		table.addContainerProperty("Sub Sub-Category ID", Label.class, new Label());
		table.setColumnWidth("Sub Sub-Category ID",100);		
		table.setColumnAlignment("Sub Sub-Category ID", table.ALIGN_CENTER);
		
		lblCategory.setWidth("90px");
		lblSubCategory.setWidth("90px");
		
		cmbCategory.setImmediate(true);
		cmbSubCategory.setImmediate(true);
		cmbSubCategory.setWidth("170px");
	}

	private void compAdd()
	{
		
		btnLayout.addComponent(lblCategory);
		btnLayout.addComponent(cmbCategory);
		hLayout2.addComponent(lblSubCategory);
		hLayout2.addComponent(cmbSubCategory);
		cmbLayout.setSpacing(true);
		cmbLayout.addComponent(btnLayout);
		cmbLayout.addComponent(hLayout2);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}