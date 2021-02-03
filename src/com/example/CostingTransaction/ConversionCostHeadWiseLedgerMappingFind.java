package com.example.CostingTransaction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ConversionCostHeadWiseLedgerMappingFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	public String receiptSupplierId = "";

	private ArrayList<Label> lblTransactionId = new ArrayList<Label>();
	private ArrayList<Label> lblheadId = new ArrayList<Label>();
	private ArrayList<Label> lblHeadName = new ArrayList<Label>();

	private SessionBean sessionBean;
	public ConversionCostHeadWiseLedgerMappingFind(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("CONVERSION COST HEAD WISE LEDGER MAPPING FIND :: "+this.sessionBean.getCompany());
		this.center();
		this.setWidth("470px");
		this.setHeight("270px");
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
		lblTransactionId.add(ar, new Label(""));
		lblTransactionId.get(ar).setWidth("100%");
		lblTransactionId.get(ar).setImmediate(true);
		lblTransactionId.get(ar).setHeight("23px");
		
		lblheadId.add(ar, new Label(""));
		lblheadId.get(ar).setWidth("100%");
		lblheadId.get(ar).setImmediate(true);
		lblheadId.get(ar).setHeight("23px");
		lblHeadName.add(ar, new Label(""));
		lblHeadName.get(ar).setWidth("100%");
		lblHeadName.get(ar).setImmediate(true);
		lblHeadName.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblTransactionId.get(ar),lblheadId.get(ar),lblHeadName.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lblTransactionId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
			        txtReceiptSupplierId.setValue(receiptSupplierId);
					windowClose();				
					
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblheadId.size(); i++)
		{
			lblheadId.get(i).setValue("");
			lblHeadName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String query ="Select distinct transactionNo,headId,headName FROM tbConversionCostWiseLedgerMappingInfo order by headName";
			System.out.println("Increment : "+query);
			List<?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblTransactionId.get(i).setValue(element[0]);
					lblheadId.get(i).setValue(element[1]);
					lblHeadName.get(i).setValue(element[2]);

					if((i)==lblheadId.size()-1)
					{
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
			this.getParent().showNotification( ex+"", Notification.TYPE_ERROR_MESSAGE);
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

		table.addContainerProperty("Transaction Id", Label.class, new Label());
		table.setColumnWidth("Transaction Id",80);
		table.setColumnCollapsed("Transaction Id", true);
		
		table.addContainerProperty("Head Id", Label.class, new Label());
		table.setColumnWidth("Head Id",80);

		table.addContainerProperty("Head Name", Label.class, new Label());
		table.setColumnWidth("Head Name",275);
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