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

public class SpareRackInfoFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtfind_rackId;
	private Table table=new Table();


	private ComboBox cmbStore = new ComboBox();
	
	private String[] co=new String[]{"a","b"};
	public String rack_Id = "";

	private ArrayList<Label> lbRackID = new ArrayList<Label>();
	private ArrayList<Label> lbRackName = new ArrayList<Label>();

	private SessionBean sessionBean;
	public SpareRackInfoFind(SessionBean sessionBean,TextField txtfind_rackId)
	{
		this.txtfind_rackId = txtfind_rackId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SPARE RACK INFO :: "+sessionBean.getCompany());
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
		cmbStoreData();
//		tableDataAdding();
	}
	
	private void cmbStoreData()
	{
		cmbStore.removeAllItems();
		
		Transaction tx = null;
		
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "";
			query = " Select vDepoId,vDepoName from tbRackInformation  " +
							" order by  vDepoName";
			System.out.println();
			
			List list = session.createSQLQuery(query).list();
			
			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				
				cmbStore.addItem(element[0].toString());
				cmbStore.setItemCaption(element[0].toString(), element[1].toString());
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
		
		lbRackID.add(ar, new Label(""));
		lbRackID.get(ar).setWidth("100%");
		lbRackID.get(ar).setImmediate(true);
		lbRackID.get(ar).setHeight("23px");	
		
		lbRackName.add(ar, new Label(""));
		lbRackName.get(ar).setWidth("100%");
		lbRackName.get(ar).setImmediate(true);
		lbRackName.get(ar).setHeight("23px");
	
		table.addItem(new Object[]{lbRackID.get(ar),lbRackName.get(ar)},ar);
	}

	public void setEventAction()
	{
		cmbStore.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbStore.getValue()!=null)
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
					rack_Id = lbRackID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtfind_rackId.setValue(rack_Id);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbRackID.size(); i++)
		{
			lbRackID.get(i).setValue("");
			lbRackName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query =" Select vRackId,vRackName from tbRackInformation where vDepoId like  '"+cmbStore.getValue().toString()+"'  order by iAutoId desc";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbRackID.get(i).setValue(element[0]);
					lbRackName.get(i).setValue(element[1]);

					if((i)==lbRackID.size()-1) {
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
		

		table.addContainerProperty("Rack ID", Label.class, new Label());
		table.setColumnWidth("Rack ID",100);		
		table.setColumnAlignment("Rack ID", table.ALIGN_CENTER);
		
		table.addContainerProperty("Rack Name", Label.class, new Label());
		table.setColumnWidth("Rack Name",270);
		
		cmbStore.setImmediate(true);
		cmbStore.setInputPrompt("Select Store Name");
		cmbStore.setWidth("200px");
		cmbStore.setHeight("-1px");
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		btnLayout.addComponent(cmbStore);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}