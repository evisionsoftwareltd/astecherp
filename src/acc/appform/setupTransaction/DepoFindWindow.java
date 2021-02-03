package acc.appform.setupTransaction;

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
import com.vaadin.ui.Window.Notification;

public class DepoFindWindow extends Window 
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptAreaId =new TextField();
	private String 	receiptAreaId="";
	private Table table=new Table();

	private ArrayList<Label> lblDepoId = new ArrayList<Label>();
	private ArrayList<Label> lblDepoName = new ArrayList<Label>();
	private ArrayList<Label> lblDepoAddress = new ArrayList<Label>();
	private ArrayList<Label>lblDepoIncharge = new ArrayList<Label>();
	private ArrayList<Label>lblDesignation = new ArrayList<Label>();

	private SessionBean sessionBean;

	public DepoFindWindow(SessionBean sessionBean,TextField ReligionId,String frmName)
	{
		this.txtReceiptAreaId = ReligionId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND STORE INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("550px");
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
	
	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptAreaId = lblDepoId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();

					txtReceiptAreaId.setValue(receiptAreaId);
					System.out.println("DivisionId"+receiptAreaId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblDepoId.size(); i++)
		{
			lblDepoId.get(i).setValue("");
			lblDepoName.get(i).setValue("");
			lblDepoAddress.get(i).setValue("");
			lblDepoIncharge.get(i).setValue("");
			lblDesignation.get(i).setValue("");
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
		lblDepoId.add(ar, new Label(""));
		lblDepoId.get(ar).setWidth("100%");
		lblDepoId.get(ar).setImmediate(true);
		lblDepoId.get(ar).setHeight("12px");
		
		lblDepoName.add(ar, new Label(""));
		lblDepoName.get(ar).setWidth("100%");
		lblDepoName.get(ar).setImmediate(true);

		lblDepoAddress.add(ar, new Label(""));
		lblDepoAddress.get(ar).setWidth("100%");
		lblDepoAddress.get(ar).setImmediate(true);
		
		lblDepoIncharge.add(ar, new Label(""));
		lblDepoIncharge.get(ar).setWidth("100%");
		lblDepoIncharge.get(ar).setImmediate(true);
		
		lblDesignation.add(ar, new Label(""));
		lblDesignation.get(ar).setWidth("100%");
		lblDesignation.get(ar).setImmediate(true);


		table.addItem(new Object[]{lblDepoId.get(ar),lblDepoName.get(ar),lblDepoAddress.get(ar),lblDepoIncharge.get(ar),lblDesignation.get(ar)},ar);
	}

	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			query ="select vDepoId,vDepoName,vDepoAddress,vDepoIncharge,vDesignation from tbDepoInformation order by CONVERT(int, iAutoId) ";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblDepoId.get(i).setValue(element[0]);
					lblDepoName.get(i).setValue(element[1]);
					lblDepoAddress.get(i).setValue(element[2]);
					lblDepoIncharge.get(i).setValue(element[3].toString());
					lblDesignation.get(i).setValue(element[4]);


					if((i)==lblDepoId.size()-1)
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
		catch (Exception ex)
		{
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

		table.addContainerProperty("STORE ID", Label.class, new Label());
		table.setColumnWidth("STORE ID",30);

		table.addContainerProperty("STORE Name", Label.class, new Label());
		table.addContainerProperty("STORE Address", Label.class, new Label());
		table.addContainerProperty("STORE In-charge", Label.class, new Label());
		table.addContainerProperty("DESIGNATION", Label.class, new Label());
	}

	private void compAdd()
	{
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	} 

}
