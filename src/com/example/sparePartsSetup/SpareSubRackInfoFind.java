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

public class SpareSubRackInfoFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtfind_subrackId;
	private Table table=new Table();


	private ComboBox cmbRack = new ComboBox();
	
	private String[] co=new String[]{"a","b"};
	public String subrack_Id = "";


	private ArrayList<Label> lbStoreName = new ArrayList<Label>();
	private ArrayList<Label> lbSubRackID = new ArrayList<Label>();
	private ArrayList<Label> lbSubRackName = new ArrayList<Label>();

	private SessionBean sessionBean;
	public SpareSubRackInfoFind(SessionBean sessionBean,TextField txtfind_subrackId)
	{
		this.txtfind_subrackId = txtfind_subrackId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SPARE SUB RACK INFO :: "+sessionBean.getCompany());
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
		cmbRackData();
//		tableDataAdding();
	}
	
	private void cmbRackData()
	{
		cmbRack.removeAllItems();
		
		Transaction tx = null;
		
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "";
			query = " Select vRackId,vRackName from tbSubRackInformation  " +
							" order by  vRackName";
			System.out.println();
			
			List list = session.createSQLQuery(query).list();
			
			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				
				cmbRack.addItem(element[0].toString());
				cmbRack.setItemCaption(element[0].toString(), element[1].toString());
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
		lbStoreName.add(ar, new Label(""));
		lbStoreName.get(ar).setWidth("100%");
		lbStoreName.get(ar).setImmediate(true);
		lbStoreName.get(ar).setHeight("23px");	
		
		lbSubRackID.add(ar, new Label(""));
		lbSubRackID.get(ar).setWidth("100%");
		lbSubRackID.get(ar).setImmediate(true);
		lbSubRackID.get(ar).setHeight("23px");	
		
		lbSubRackName.add(ar, new Label(""));
		lbSubRackName.get(ar).setWidth("100%");
		lbSubRackName.get(ar).setImmediate(true);
		lbSubRackName.get(ar).setHeight("23px");
	
		table.addItem(new Object[]{lbStoreName.get(ar),lbSubRackID.get(ar),lbSubRackName.get(ar)},ar);
	}

	public void setEventAction()
	{
		cmbRack.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbRack.getValue()!=null)
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
					subrack_Id = lbSubRackID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtfind_subrackId.setValue(subrack_Id);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbSubRackID.size(); i++)
		{
			lbSubRackID.get(i).setValue("");
			lbSubRackName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query =" Select vDepoName,vSubRackId,vSubRackName from tbSubRackInformation where vRackId like  '"+cmbRack.getValue().toString()+"'  order by iAutoId desc";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					lbStoreName.get(i).setValue(element[0]);
					lbSubRackID.get(i).setValue(element[1]);
					lbSubRackName.get(i).setValue(element[2]);

					if((i)==lbSubRackID.size()-1) {
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
		
		table.addContainerProperty("Store Name", Label.class, new Label());
		table.setColumnWidth("Store Name",130);		

		table.addContainerProperty("Sub Rack ID", Label.class, new Label());
		table.setColumnWidth("Sub Rack ID",30);		
		table.setColumnAlignment("Sub Rack ID", table.ALIGN_CENTER);
		table.setColumnCollapsed("Sub Rack ID", true);
		
		table.addContainerProperty("Sub Rack Name", Label.class, new Label());
		table.setColumnWidth("Sub Rack Name",250);
		
		cmbRack.setImmediate(true);
		cmbRack.setInputPrompt("Select Rack Name");
		cmbRack.setWidth("250px");
		cmbRack.setHeight("-1px");
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		btnLayout.addComponent(cmbRack);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}