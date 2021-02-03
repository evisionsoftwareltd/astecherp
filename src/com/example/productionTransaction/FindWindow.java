package com.example.productionTransaction;

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
import com.common.share.TextRead;
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

public class FindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextRead txtReceiptSupplierId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptSupplierId = "";

	private ArrayList<Label> lblGroupId = new ArrayList<Label>();
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<Label> lblItemName = new ArrayList<Label>();
//private ArrayList<Label> lblgroupCode = new ArrayList<Label>();

	private SessionBean sessionBean;
	
	public FindWindow(SessionBean sessionBean, TextRead txtGroupId,String string) {
		this.txtReceiptSupplierId = txtGroupId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND ITEM INFO  :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("520px");
		this.setHeight("270");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		tableclear();
		tableDataAdding();
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
		lblSl.add(ar, new Label(""));
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setImmediate(true);
		lblSl.get(ar).setHeight("-1px");
		lblSl.get(ar).setValue(ar+1);
		
		lblGroupId.add(ar, new Label(""));
		lblGroupId.get(ar).setWidth("100%");
		lblGroupId.get(ar).setImmediate(true);
		lblGroupId.get(ar).setHeight("-1px");

		lblItemName.add(ar, new Label(""));
		lblItemName.get(ar).setWidth("100%");
		lblItemName.get(ar).setImmediate(true);
		lblItemName.get(ar).setHeight("-1px");
		
		table.addItem(new Object[]{lblSl.get(ar),lblGroupId.get(ar),lblItemName.get(ar),},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lblSl.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSupplierId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblGroupId.size(); i++)
		{
			lblGroupId.get(i).setValue("");
			lblItemName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query ="select transactionid,groupid,groupName from tbItemGroupInfo order by transactionid ";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			tableclear();
			if(!list.isEmpty())
			{
				System.out.print("Rabiul Hasan");
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					lblSl.get(i).setValue(element[0]);
					lblGroupId.get(i).setValue(element[1]);
					lblItemName.get(i).setValue(element[2]);

					if((i)==lblGroupId.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else 
			{		
			 showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) 
		{
			//his.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
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

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",30);
		table.setColumnCollapsed("SL", true);
		
		table.addContainerProperty("GroupID", Label.class, new Label());
		table.setColumnWidth("Group ID",100);
		
		table.addContainerProperty("Group Name", Label.class, new Label());
		table.setColumnWidth("Group Name",290);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}