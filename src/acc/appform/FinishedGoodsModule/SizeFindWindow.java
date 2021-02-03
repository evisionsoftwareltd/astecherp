package acc.appform.FinishedGoodsModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
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
import com.vaadin.ui.Window.Notification;

public class SizeFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSizeId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptSizeId = "";

	private ArrayList<Label> lblSizeId = new ArrayList<Label>();
	private ArrayList<Label> lblSizeName = new ArrayList<Label>();

	private String frmName;
	SessionBean sessionBean;

	public SizeFindWindow(SessionBean sessionBean,TextField txtReceiptSizeId,String frmName)
	{
		this.txtReceiptSizeId = txtReceiptSizeId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SIZE INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("400px");
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
		lblSizeId.add(ar, new Label(""));
		lblSizeId.get(ar).setWidth("100%");
		lblSizeId.get(ar).setImmediate(true);
		lblSizeId.get(ar).setHeight("23px");

		lblSizeName.add(ar, new Label(""));
		lblSizeName.get(ar).setWidth("100%");
		lblSizeName.get(ar).setImmediate(true);
		lblSizeName.get(ar).setHeight("23px");


		table.addItem(new Object[]{lblSizeId.get(ar),lblSizeName.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSizeId = lblSizeId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSizeId.setValue(receiptSizeId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblSizeId.size(); i++)
		{
			lblSizeId.get(i).setValue("");
			lblSizeName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try{
			Session session = com.common.share.SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query ="Select vSizeId,vSizeName From tbSizeInfo order by iAutoId";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblSizeId.get(i).setValue(element[0]);
					lblSizeName.get(i).setValue(element[1]);

					if((i)==lblSizeId.size()-1) {
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
		table.setHeight("200px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Size ID", Label.class, new Label());
		table.setColumnWidth("Size ID",100);

		table.addContainerProperty("Size", Label.class, new Label());
		table.setColumnWidth("Size",200);

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
