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

public class AreaFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptAreaId =new TextField();
	private String 	receiptAreaId="";
	private Table table=new Table();

	private ArrayList<Label> lblAreaId = new ArrayList<Label>();
	private ArrayList<Label> lblDivisionName = new ArrayList<Label>();
	private ArrayList<Label> lblAreaName = new ArrayList<Label>();
	private ArrayList<Label>lblMr = new ArrayList<Label>();
	private ArrayList<Label>lblDesignation = new ArrayList<Label>();

	private SessionBean sessionBean;

	public AreaFindWindow(SessionBean sessionBean,TextField ReligionId,String frmName)
	{
		this.txtReceiptAreaId = ReligionId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND ZONE INFORMATION :: "+sessionBean.getCompany());
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
					receiptAreaId = lblAreaId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();

					txtReceiptAreaId.setValue(receiptAreaId);
					System.out.println("DivisionId"+receiptAreaId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblAreaId.size(); i++)
		{
			lblAreaId.get(i).setValue("");
			lblDivisionName.get(i).setValue("");
			lblAreaName.get(i).setValue("");
			lblMr.get(i).setValue("");
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
		lblAreaId.add(ar, new Label(""));
		lblAreaId.get(ar).setWidth("100%");
		lblAreaId.get(ar).setImmediate(true);
		lblAreaId.get(ar).setHeight("12px");
		
		lblDivisionName.add(ar, new Label(""));
		lblDivisionName.get(ar).setWidth("100%");
		lblDivisionName.get(ar).setImmediate(true);

		lblAreaName.add(ar, new Label(""));
		lblAreaName.get(ar).setWidth("100%");
		lblAreaName.get(ar).setImmediate(true);
		
		lblMr.add(ar, new Label(""));
		lblMr.get(ar).setWidth("100%");
		lblMr.get(ar).setImmediate(true);
		
		lblDesignation.add(ar, new Label(""));
		lblDesignation.get(ar).setWidth("100%");
		lblDesignation.get(ar).setImmediate(true);


		table.addItem(new Object[]{lblAreaId.get(ar),lblDivisionName.get(ar),lblAreaName.get(ar),lblMr.get(ar),lblDesignation.get(ar)},ar);
	}

	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			query ="select ai.vAreaId,ai.vDivisionName,ai.vAreaName,ai.vEmployeeName,ai.vDesignation from " +
					"tbDivisionInfo di inner join tbAreaInfo ai on di.vDivisionId = ai.vDivisionId order " +
					"by CONVERT(int, ai.vAreaId) ";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblAreaId.get(i).setValue(element[0]);
					lblDivisionName.get(i).setValue(element[1]);
					lblAreaName.get(i).setValue(element[2]);
					lblMr.get(i).setValue(element[3]);
					lblDesignation.get(i).setValue(element[4]);


					if((i)==lblAreaId.size()-1)
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

		table.addContainerProperty("Division Name", Label.class, new Label());
	
		table.addContainerProperty("Zone Name", Label.class, new Label());
	
		table.addContainerProperty("TSO", Label.class, new Label());
	
		table.addContainerProperty("Degination", Label.class, new Label());
	}

	private void compAdd()
	{
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	} 

}
