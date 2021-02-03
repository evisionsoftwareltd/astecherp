package acc.appform.FinishedGoodsModule;

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

public class SourceInformationFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private Label lblPartyId = new Label("Party :");
	private ComboBox cmbPartyId = new ComboBox();
	
	private String[] co=new String[]{"a","b"};
	public String receiptSubCateId = "";

	private ArrayList<Label> tblblSourceId = new ArrayList<Label>();
	private ArrayList<Label> tblblSourceName = new ArrayList<Label>();

	private SessionBean sessionBean;
	public SourceInformationFind(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("Source Information Find :: "+sessionBean.getCompany());
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
		cmbProductDataLoad();
		//tableDataAdding();
	}
	public void cmbProductDataLoad()
	{
		cmbPartyId.removeAllItems();
		try
		{
			String sql="select vPartyID,vPartyName from tbSourceInfo";
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyId.addItem(element[0].toString());
				cmbPartyId.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private Iterator<?> dbService(String sql){

		System.out.println(sql);
		Session session=null;
		Iterator<?> iter=null;
		try {
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		} 
		catch (Exception e) {
			showNotification(null,""+e,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
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
		tblblSourceName.add(ar, new Label(""));
		tblblSourceName.get(ar).setWidth("100%");
		tblblSourceName.get(ar).setImmediate(true);
		tblblSourceName.get(ar).setHeight("23px");
		
		tblblSourceId.add(ar, new Label(""));
		tblblSourceId.get(ar).setWidth("100%");
		tblblSourceId.get(ar).setImmediate(true);
		tblblSourceId.get(ar).setHeight("23px");		

		table.addItem(new Object[]{tblblSourceName.get(ar),tblblSourceId.get(ar)},ar);
	}

	public void setEventAction()
	{
		cmbPartyId.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbPartyId.getValue()!=null)
				{
					tableDataAdding();
				}
				else
				{
					tableclear();
				}
			}
		});
		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSubCateId = tblblSourceId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSubCateId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<tblblSourceId.size(); i++)
		{
			tblblSourceId.get(i).setValue("");
			tblblSourceName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query ="Select iSourceID,vSourceName from tbSourceInfo where vPartyID like '"+cmbPartyId.getValue().toString()+"'  order by vSourceName";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			tableclear();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tblblSourceId.get(i).setValue(element[0]);
					tblblSourceName.get(i).setValue(element[1]);
					if((i)==tblblSourceId.size()-1) {
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
		
		table.addContainerProperty("Source Name", Label.class, new Label());
		table.setColumnWidth("Source Name",270);

		table.addContainerProperty("Source Id", Label.class, new Label());
		table.setColumnWidth("Source Id",100);		
		table.setColumnAlignment("Source Id", table.ALIGN_CENTER);
		
		lblPartyId.setWidth("60px");
		cmbPartyId.setImmediate(true);
		cmbPartyId.setWidth("180px");
		cmbPartyId.setHeight("-1px");
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		btnLayout.addComponent(lblPartyId);
		btnLayout.addComponent(cmbPartyId);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}