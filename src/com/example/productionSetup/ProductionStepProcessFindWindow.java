package com.example.productionSetup;
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

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
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

public class ProductionStepProcessFindWindow extends Window
{
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptSubCateId = "";

	private Label lblProductionType;
	private ComboBox cmbProductionType;

	private ArrayList<Label> lbSubGroupID = new ArrayList<Label>();
	private ArrayList<Label> lbSubGroupName = new ArrayList<Label>();
	private ArrayList<Label> lbGroupName = new ArrayList<Label>();

	private SessionBean sessionBean;
	public ProductionStepProcessFindWindow(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND PRODUCTION PROCESS STEP :: "+sessionBean.getCompany());
		this.center();
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setResizable(false);
		this.setStyleName("cwindow");
		
		buildMainLayout();
		setContent(mainLayout);

		tableInitialise();
		setEventAction();
		tableclear();

		cmbProductionTypeData();
	}
	private Iterator dbService(String sql){
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			return session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null||session!=null){
				tx.commit();
				session.close();
			}
		}
		return null;
	}
	public void cmbProductionTypeData()
	{
		cmbProductionType.removeAllItems();
		String sql="select productTypeId,ProductTypeName from tbProductionType";
		Iterator<?>iter=dbService(sql);
		while(iter.hasNext())
		{
			Object[] element = (Object[]) iter.next();
			cmbProductionType.addItem(element[0].toString());
			cmbProductionType.setItemCaption(element[0].toString(), element[1].toString());
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
		lbSubGroupID.add(ar, new Label(""));
		lbSubGroupID.get(ar).setWidth("100%");
		lbSubGroupID.get(ar).setImmediate(true);
		lbSubGroupID.get(ar).setHeight("23px");

		lbSubGroupName.add(ar, new Label(""));
		lbSubGroupName.get(ar).setWidth("100%");
		lbSubGroupName.get(ar).setImmediate(true);
		lbSubGroupName.get(ar).setHeight("23px");

		lbGroupName.add(ar, new Label(""));
		lbGroupName.get(ar).setWidth("100%");
		lbGroupName.get(ar).setImmediate(true);
		lbGroupName.get(ar).setHeight("23px");

		table.addItem(new Object[]{lbSubGroupID.get(ar),lbSubGroupName.get(ar),lbGroupName.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSubCateId = lbSubGroupID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSubCateId);
					windowClose();
				}
			}
		});

		cmbProductionType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProductionType.getValue()!=null)
				{
					tableDataAdding();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbSubGroupID.size(); i++)
		{
			lbSubGroupID.get(i).setValue("");
			lbSubGroupName.get(i).setValue("");
			lbGroupName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		String query="select a.stepId,a.stepName,b.productTypeName from tbProductionstep a"
				+" inner join tbProductionType b on a.productionTypeId=b.productTypeId where b.productTypeId like " +
				"'"+cmbProductionType.getValue()+"' order by a.productionTypeId";
		Iterator<?>iter=dbService(query);
		if(!iter.hasNext()){
			tableclear();
			this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
		}
		else{
			tableclear();
			int i=0;
			while( iter.hasNext())
			{						  
				Object[] element = (Object[]) iter.next();

				lbSubGroupID.get(i).setValue(element[0]);
				lbSubGroupName.get(i).setValue(element[1]);
				lbGroupName.get(i).setValue(element[2]);

				if((i)==lbSubGroupID.size()-1) {
					tableRowAdd(i+1);
				}
				i++;
			}
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private AbsoluteLayout buildMainLayout()
	{
		// mainLayout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		setWidth("460px");
		setHeight("460px");
		
		lblProductionType = new Label("Production Type : ");
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("220px");
		cmbProductionType.setHeight("-1px");

		table.setSelectable(true);
		table.setWidth("200px");
		table.setHeight("310px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Step ID", Label.class, new Label());
		table.setColumnWidth("Step ID",60);
		table.setColumnCollapsed("Step ID", true);

		table.addContainerProperty("Production Step Name", Label.class, new Label());
		table.setColumnWidth("Production Step Name",150);

		table.addContainerProperty("Production Type", Label.class, new Label());
		table.setColumnWidth("Production Type",150);
		table.setColumnCollapsed("Production Type", true);
		
		mainLayout.addComponent(lblProductionType, "top:20px; left:20px;");
		mainLayout.addComponent(cmbProductionType, "top:20px; left:120px;");
		mainLayout.addComponent(table, "top:50px; left:120px;");
		
		return mainLayout;
	}
}