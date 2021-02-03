package com.example.CostingTransaction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.*;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ProductionStepSelectionFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	ComboBox cmbParty,cmbFG;

	public String receiptSupplierId = "";

	private ArrayList<Label> lblTransactionId = new ArrayList<Label>();
	private ArrayList<Label> lblTransactionDate = new ArrayList<Label>();
	private ArrayList<Label> lblHeadName = new ArrayList<Label>();

	private SessionBean sessionBean;
	public ProductionStepSelectionFind(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("PRODUCTION STEP SELECTION FIND :: "+this.sessionBean.getCompany());
		this.center();
		this.setWidth("470px");
		this.setHeight("370px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		tableclear();
		tableDataAdding("%","%");
		cmbPartyDataLoad();
		//cmbFgDataLoad();
	}

	private void cmbFgDataLoad() {
		cmbFG.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query = "select FgId,fgName from tbProductionStepSelectionInfo where partyId='"+cmbParty.getValue()+"' order by fgName";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbFG.addItem(element[0]);
				cmbFG.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbPartyDataLoad() {
		cmbParty.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query = "select partyId,partyName from tbProductionStepSelectionInfo order by partyName";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbParty.addItem(element[0]);
				cmbParty.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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

		lblTransactionDate.add(ar, new Label(""));
		lblTransactionDate.get(ar).setWidth("100%");
		lblTransactionDate.get(ar).setImmediate(true);
		lblTransactionDate.get(ar).setHeight("23px");
		lblHeadName.add(ar, new Label(""));
		lblHeadName.get(ar).setWidth("100%");
		lblHeadName.get(ar).setImmediate(true);
		lblHeadName.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblTransactionId.get(ar),lblTransactionDate.get(ar),lblHeadName.get(ar)},ar);
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
		cmbParty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				tableclear();
				if(cmbParty.getValue()!=null){
					cmbFgDataLoad();
					tableDataAdding(cmbParty.getValue().toString(), "%");
				}
				else{
					cmbFG.removeAllItems();
					tableDataAdding("%", "%");
				}
			}
		});
		cmbFG.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				tableclear();
				if(cmbFG.getValue()!=null){
					tableDataAdding("%",cmbFG.getValue().toString());
				}
				else{
					tableDataAdding("%", "%");
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblTransactionDate.size(); i++)
		{
			lblTransactionDate.get(i).setValue("");
			lblTransactionId.get(i).setValue("");
		}
	}

	private void tableDataAdding(String PartyId,String FgId)
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String query ="Select distinct transactionNo,convert(date,date,103) FROM tbProductionStepSelectionInfo " +
					"where  partyId like '"+PartyId+"' and FgId like '"+FgId+"' order by transactionNo";
			System.out.println("Increment : "+query);
			List<?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblTransactionId.get(i).setValue(element[0]);
					lblTransactionDate.get(i).setValue(element[1]);
					//lblHeadName.get(i).setValue(element[2]);

					if((i)==lblTransactionDate.size()-1)
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
		//table.setColumnCollapsed("Transaction Id", true);

		table.addContainerProperty("Transaction Date", Label.class, new Label());
		table.setColumnWidth("Transaction Date",150);

		table.addContainerProperty("Head Name", Label.class, new Label());
		table.setColumnWidth("Head Name",275);
		table.setColumnCollapsed("Head Name", true);

		cmbParty = new ComboBox("Party Name: ");
		cmbParty.setImmediate(true);
		cmbParty.setWidth("250px");
		cmbParty.setHeight("24px");	
		cmbParty.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		cmbFG = new ComboBox("Master Product Name: ");
		cmbFG.setImmediate(true);
		cmbFG.setWidth("250px");
		cmbFG.setHeight("24px");	
		cmbFG.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		cmbLayout.addComponent(cmbParty);
		cmbLayout.addComponent(cmbFG);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}