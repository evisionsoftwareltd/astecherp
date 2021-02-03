package acc.appform.setupTransaction;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
public class PartyFindWindow extends Window
{		
	private SessionBean sessionBean;
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();

	private TextField txtReceiptSupplierId;
	private TextField txtSearch;

	private Table table=new Table();

	public String receiptSupplierId = "";

	private ArrayList<Label> lbSupplierName = new ArrayList<Label>();
	private ArrayList<Label> lbSupplierId = new ArrayList<Label>();
	private ArrayList<Label> lbAddress = new ArrayList<Label>();

	public PartyFindWindow(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND DEALER/PARTY INFO :: "+this.sessionBean.getCompany());
		this.center();
		this.setWidth("590px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		finButtonEvent();
	}

	public void tableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbSupplierId.add(ar, new Label(""));
		lbSupplierId.get(ar).setWidth("100%");
		lbSupplierId.get(ar).setImmediate(true);
		lbSupplierId.get(ar).setHeight("18px");

		lbSupplierName.add(ar, new Label(""));
		lbSupplierName.get(ar).setWidth("100%");
		lbSupplierName.get(ar).setImmediate(true);

		lbAddress.add(ar, new Label(""));
		lbAddress.get(ar).setWidth("100%");
		lbAddress.get(ar).setImmediate(true);

		table.addItem(new Object[]{lbSupplierId.get(ar),lbSupplierName.get(ar),lbAddress.get(ar)},ar);
	}

	public void setEventAction()
	{			
		txtSearch.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				finButtonEvent();
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lbSupplierId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSupplierId);
					close();
				}
			}
		});				
	}

	private void tableclear()
	{
		for(int i=0; i<lbSupplierId.size(); i++)
		{
			lbSupplierName.get(i).setValue("");
			lbSupplierId.get(i).setValue("");
			lbAddress.get(i).setValue("");
		}
	}

	private void finButtonEvent()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String findString = "%"+(txtSearch.getValue().toString())+"%";
			String sql = " SELECT partyCode,partyName,address from tbPartyInfo  where partyCode like"
					+ " '"+findString+"' or partyName like '"+findString+"' or address like '"+findString+"' order by partyName ";
			List<?> list = session.createSQLQuery(sql).list();

			if(!list.isEmpty())
			{
				tableclear();
				int i = 0;
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbSupplierId.get(i).setValue(element[0]);
					lbSupplierName.get(i).setValue(element[1]);
					lbAddress.get(i).setValue(element[2]);

					if(i == lbSupplierId.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				showNotification("Warning!","No data found.", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void compInit()
	{
		mainLayout.setSpacing(true);

		txtSearch = new TextField();
		txtSearch.setWidth("200px");
		txtSearch.setHeight("-1px");
		txtSearch.setImmediate(true);
		txtSearch.setInputPrompt("Search");
		txtSearch.setDescription("Find Party Information using party code or party name or address.");

		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",65);

		table.addContainerProperty("Party Name", Label.class, new Label());
		table.setColumnWidth("Party Name",200);

		table.addContainerProperty("Address", Label.class, new Label());
		table.setColumnWidth("Address",245);
		table.setColumnCollapsed("ID", true);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		mainLayout.addComponent(cmbLayout);		
		mainLayout.addComponent(txtSearch);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}