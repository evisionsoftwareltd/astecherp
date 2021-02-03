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

public class DivisionFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	
	private TextField txtReceiptDivisionId =new TextField();
	private String 	receiptDivisionId="";
	private Table table=new Table();

	private ArrayList<Label> lblDivisionId = new ArrayList<Label>();
	private ArrayList<Label> lblDivisionName = new ArrayList<Label>();
	private ArrayList<Label>lblDivIncharge = new ArrayList<Label>();
	private ArrayList<Label>lblDesignation = new ArrayList<Label>();

	private SessionBean sessionBean;

	public DivisionFindWindow(SessionBean sessionBean,TextField ReligionId,String frmName)
	{
		this.txtReceiptDivisionId = ReligionId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND DIVISION INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("530px");
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
					receiptDivisionId = lblDivisionId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();

					txtReceiptDivisionId.setValue(receiptDivisionId);
					System.out.println("DivisionId"+receiptDivisionId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblDivisionId.size(); i++)
		{
			lblDivisionId.get(i).setValue("");
			lblDivisionName.get(i).setValue("");
			lblDivIncharge.get(i).setValue("");
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
		lblDivisionId.add(ar, new Label(""));
		lblDivisionId.get(ar).setWidth("100%");
		lblDivisionId.get(ar).setImmediate(true);
		lblDivisionId.get(ar).setHeight("12px");

		lblDivisionName.add(ar, new Label(""));
		lblDivisionName.get(ar).setWidth("100%");
		lblDivisionName.get(ar).setImmediate(true);
		
		lblDivIncharge.add(ar, new Label(""));
		lblDivIncharge.get(ar).setWidth("100%");
		lblDivIncharge.get(ar).setImmediate(true);
		
		lblDesignation.add(ar, new Label(""));
		lblDesignation.get(ar).setWidth("100%");
		lblDesignation.get(ar).setImmediate(true);


		table.addItem(new Object[]{lblDivisionId.get(ar),lblDivisionName.get(ar),lblDivIncharge.get(ar),lblDesignation.get(ar)},ar);
	}

	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			query =" select di.vDivisionId,di.vDivisionName,ei.vEmployeeName,di.vDesignation from tbDivisionInfo di inner join tbEmployeeInfo ei on di.vEmployeeId=ei.vEmployeeId order by CONVERT(int, di.vDivisionId) ";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblDivisionId.get(i).setValue(element[0]);
					lblDivisionName.get(i).setValue(element[1]);
					lblDivIncharge.get(i).setValue(element[2]);
					lblDesignation.get(i).setValue(element[3]);


					if((i)==lblDivisionId.size()-1)
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

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#",30);

		table.addContainerProperty("Division Name", Label.class, new Label());
		
		table.addContainerProperty("Division In-charge", Label.class, new Label());
		
		table.addContainerProperty("Degination", Label.class, new Label());
	}

	private void compAdd()
	{
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}             
}

