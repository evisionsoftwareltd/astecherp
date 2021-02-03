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
public class ConversionCostEntryFind extends Window
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
	private ArrayList<Label> lblStepName = new ArrayList<Label>();

	private SessionBean sessionBean;
	public ConversionCostEntryFind(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("CONVERSION COST ENTRY FIND :: "+this.sessionBean.getCompany());
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
		tableDataAdding("%");
		//cmbPartyDataLoad();
		cmbFgDataLoad();
	}

	private void cmbFgDataLoad() {
		cmbFG.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query="select transactionNo,semiFgName,stepName from ( "+
					" select transactionNo,semiFgId,semiFgName,stepName,1 type from tbConversionCostInfo where semiFgId not like ''"+
					" union"+
					" select transactionNo,FgId,FgName,stepName,2 type from tbConversionCostInfo where FgId not like ''"+
					" union "+
					" select transactionNo,masterProductId,masterProductName,stepName,3 type from tbConversionCostInfo "+
					" where FgId like '' and masterProductId not like '' "+
					" )a";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbFG.addItem(element[0]);
				cmbFG.setItemCaption(element[0], element[1].toString()+" # Step: "+element[2]);
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	/*private void cmbPartyDataLoad() {
		cmbParty.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query = "select partyId,partyName from tbConversionCostInfo order by partyName";
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
	}*/

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
		lblStepName.add(ar, new Label(""));
		lblStepName.get(ar).setWidth("100%");
		lblStepName.get(ar).setImmediate(true);
		lblStepName.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblTransactionId.get(ar),lblTransactionDate.get(ar),lblStepName.get(ar)},ar);
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
		cmbFG.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				tableclear();
				if(cmbFG.getValue()!=null){
					tableDataAdding(cmbFG.getValue().toString());
				}
				else{
					tableDataAdding("%");
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
			lblStepName.get(i).setValue("");
		}
	}

	private void tableDataAdding(String transactionNo)
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String query ="Select distinct transactionNo,convert(date,date,103),stepName FROM tbConversionCostInfo " +
					"where  transactionNo like '"+transactionNo+"'  order by transactionNo";
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
					lblStepName.get(i).setValue(element[2]);

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
		table.setColumnWidth("Transaction Date",120);

		table.addContainerProperty("Production Step", Label.class, new Label());
		table.setColumnWidth("Production Step",150);
		//table.setColumnCollapsed("Production Step", true);

		cmbParty = new ComboBox("Party Name: ");
		cmbParty.setImmediate(true);
		cmbParty.setWidth("250px");
		cmbParty.setHeight("24px");	
		cmbParty.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		cmbFG = new ComboBox("Product Name: ");
		cmbFG.setImmediate(true);
		cmbFG.setWidth("300px");
		cmbFG.setHeight("24px");	
		cmbFG.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		//cmbLayout.addComponent(cmbParty);
		cmbLayout.addComponent(cmbFG);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}