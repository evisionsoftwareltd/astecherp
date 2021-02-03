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

public class SectionFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private TextField txtReceiptDepartmentId;
	private Table table=new Table();

	public String receiptSupplierId = "";
	public String receiptDepartmentId = "";

	private ArrayList<Label> lblDepartmentID = new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentName = new ArrayList<Label>();
	private ArrayList<Label> lbSectionID = new ArrayList<Label>();
	private ArrayList<Label> lbSectionName = new ArrayList<Label>();
	private ArrayList<Label> lbAddress = new ArrayList<Label>();

	private String frmName;
	private SessionBean sessionBean;
	
	public SectionFindWindow(SessionBean sessionBean,TextField txtReceiptDepartmentId,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.txtReceiptDepartmentId = txtReceiptDepartmentId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SECTION INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("550px");
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
		lblDepartmentID.add(ar, new Label(""));
		lblDepartmentID.get(ar).setWidth("100%");
		lblDepartmentID.get(ar).setImmediate(true);
		lblDepartmentID.get(ar).setHeight("23px");
		
		lblDepartmentName.add(ar, new Label(""));
		lblDepartmentName.get(ar).setWidth("100%");
		lblDepartmentName.get(ar).setImmediate(true);
		lblDepartmentName.get(ar).setHeight("23px");
		
		lbSectionID.add(ar, new Label(""));
		lbSectionID.get(ar).setWidth("100%");
		lbSectionID.get(ar).setImmediate(true);
		lbSectionID.get(ar).setHeight("23px");

		lbSectionName.add(ar, new Label(""));
		lbSectionName.get(ar).setWidth("100%");
		lbSectionName.get(ar).setImmediate(true);
		lbSectionName.get(ar).setHeight("23px");

		lbAddress.add(ar, new Label(""));
		lbAddress.get(ar).setWidth("100%");
		lbAddress.get(ar).setImmediate(true);
		lbAddress.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblDepartmentID.get(ar),lblDepartmentName.get(ar),lbSectionID.get(ar),lbSectionName.get(ar),lbAddress.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lbSectionID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					receiptDepartmentId = lblDepartmentID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptSupplierId.setValue(receiptSupplierId);
					txtReceiptDepartmentId.setValue(receiptDepartmentId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbSectionID.size(); i++)
		{
			lblDepartmentID.get(i).setValue("");
			lblDepartmentName.get(i).setValue("");
			lbSectionID.get(i).setValue("");
			lbSectionName.get(i).setValue("");
			lbAddress.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query ="Select ISNULL(vDepartmentID,'') vDepartmentID,ISNULL(vDepartmentName,'') vDepartmentName,vSectionID,SectionName,Address from tbSectionInfo order by vDepartmentName,AutoID desc";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblDepartmentID.get(i).setValue(element[0]);
					lblDepartmentName.get(i).setValue(element[1]);
					lbSectionID.get(i).setValue(element[2]);
					lbSectionName.get(i).setValue(element[3]);
					lbAddress.get(i).setValue(element[4]);

					if((i)==lbSectionID.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else 
			{
				tableclear();
				showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) 
		{
			showNotification("tableDataAdding", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
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
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Dept. ID", Label.class, new Label());
		table.setColumnWidth("Dept. ID",20);
		
		table.addContainerProperty("Dept. Name", Label.class, new Label());
		table.setColumnWidth("Dept. Name",150);
		
		table.addContainerProperty("S ID", Label.class, new Label());
		table.setColumnWidth("S ID",20);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name",150);

		table.addContainerProperty("Address", Label.class, new Label());
		table.setColumnWidth("Address",150);

		table.setColumnCollapsed("Dept. ID", true);
		table.setColumnCollapsed("S ID", true);
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