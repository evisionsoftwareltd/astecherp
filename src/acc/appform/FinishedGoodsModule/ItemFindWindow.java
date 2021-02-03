package acc.appform.FinishedGoodsModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

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

public class ItemFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptItemId = "";

	private ArrayList<Label> lbProductId = new ArrayList<Label>();
	private ArrayList<Label> lbProductName = new ArrayList<Label>();
	private ArrayList<Label> lbGroupName = new ArrayList<Label>();
	private ArrayList<Label> lbSubGroup = new ArrayList<Label>();
	private ArrayList<Label> lbUnit = new ArrayList<Label>();

	private String frmName;
	private SessionBean sessionBean;
	
	public ItemFindWindow(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND ITEM INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("700px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
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
		lbProductId.add(ar, new Label(""));
		lbProductId.get(ar).setWidth("100%");
		lbProductId.get(ar).setImmediate(true);
		lbProductId.get(ar).setHeight("23px");

		lbProductName.add(ar, new Label(""));
		lbProductName.get(ar).setWidth("100%");
		lbProductName.get(ar).setImmediate(true);

		lbGroupName.add(ar, new Label(""));
		lbGroupName.get(ar).setWidth("100%");
		lbGroupName.get(ar).setImmediate(true);
		
		lbSubGroup.add(ar, new Label(""));
		lbSubGroup.get(ar).setWidth("100%");
		lbSubGroup.get(ar).setImmediate(true);
		
		lbUnit.add(ar, new Label(""));
		lbUnit.get(ar).setWidth("100%");
		lbUnit.get(ar).setImmediate(true);

		table.addItem(new Object[]{lbProductId.get(ar),lbProductName.get(ar),lbGroupName.get(ar),lbSubGroup.get(ar),lbUnit.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptItemId = lbProductId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptItemId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbProductId.size(); i++)
		{
			lbProductId.get(i).setValue("");
			lbProductName.get(i).setValue("");
			lbGroupName.get(i).setValue("");
			lbSubGroup.get(i).setValue("");
			lbUnit.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			query =" select productCode,productName,groupName,subGroupName,unitName from tbProductInfo ORDER by productCode ";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbProductId.get(i).setValue(element[0]);
					lbProductName.get(i).setValue(element[1]);
					
					
					if(element[2].toString().equals("")){
						lbGroupName.get(i).setValue("");
					}else{
						lbGroupName.get(i).setValue(element[2]);
					}		
					
					if(element[3].toString().equals("")){
						lbSubGroup.get(i).setValue("");
					}else{
						lbSubGroup.get(i).setValue(element[3]);	
					}
					
						
					if(element[4].toString().equals("")){
						lbUnit.get(i).setValue("");
					}else{
						lbUnit.get(i).setValue(element[4]);	
					}
					
					if((i)==lbProductId.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data Found!", Notification.TYPE_WARNING_MESSAGE); 
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
		table.setHeight("400px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Itm ID", Label.class, new Label());
		table.setColumnWidth("Itm ID",40);

		table.addContainerProperty("Item Name", Label.class, new Label());
		table.setColumnWidth("Item Name",180);

		table.addContainerProperty("Group Name", Label.class, new Label());
		table.setColumnWidth("Section Name",130);
		
		table.addContainerProperty("Sub Group Name", Label.class, new Label());
		table.setColumnWidth("Category Name",100);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",50);
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