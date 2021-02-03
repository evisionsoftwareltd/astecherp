package acc.appform.accountsSetup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class FindChequeBookEntry extends Window 
{
	private SessionBean sessionBean;
	private TextRead txtId;
	
	private VerticalLayout mainLayout=new VerticalLayout();
	private HorizontalLayout hLayout=new HorizontalLayout();
	
	private Label lblFrom = new Label("Form Date:");
	private Label lblTo = new Label("To Date:");
	
	private PopupDateField fromDate = new PopupDateField();
	private PopupDateField toDate = new PopupDateField();
	
	private String px = "150px";
	
	private Table table = new Table();
	
	private ArrayList<Label> lblId = new ArrayList<Label>();
	
	private NativeButton btnFind = new NativeButton("Find");
	
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	public FindChequeBookEntry(SessionBean sessionBean, TextRead id) 
	{
		this.sessionBean = sessionBean;
		this.txtId = id;
		this.setCaption("Find Window (Cheque Book Entry) :: "+sessionBean.getCompany());
		
		this.center();
		this.setWidth("620px");
		this.setHeight("");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		
		compInit();
		compAdd();
		tableInitialise();
		
		setEventAction();
	}
	
	private void setEventAction()
	{
		btnFind.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
				
			}
		});
		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					String id = lblId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtId.setValue(id);

					System.out.println("find "+id);

					windowClose();
				}
			}
		});
	}
	
	private void findButtonEvent()
	{
		Transaction tx;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List lst = null;

			String sql = null ;

			sql = " Select * from funChequeBookEntry('"+dF.format(fromDate.getValue())+"','"+dF.format(toDate.getValue())+"') ";

			System.out.println(sql);

			lst = session.createSQLQuery(sql).list();
			System.out.println(sql);

			int i = 0 ;

			if(!lst.isEmpty())
			{
				table.removeAllItems();

				for (Iterator iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();


					table.addItem(new Object[] { dateFormat.format(element[1]), element[3] , element[4], element[7], element[0] }, new Integer(i));

					lblId.get(i).setValue(element[0]);


					if(lblId.size()-1==i)
					{
						tableRowAdd(i+1);
					}

					i++;
				}
			}
			else
			{
				table.removeAllItems();
				showNotification("Warning","There is no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	private void windowClose()
	{
		this.close();
	}
	
	private void tableInitialise()
	{
		for(int i=0;i<20000;i++)
		{
			tableRowAdd(i);
		}
	}
	
	public void tableRowAdd(final int ar)
	{	
		lblId.add(ar,new Label());
		lblId.get(ar).setWidth("100%");
	}
	
	private void compAdd()
	{
		hLayout.setSpacing(true);
		hLayout.addComponent(lblFrom);
		hLayout.addComponent(fromDate);
		hLayout.addComponent(lblTo);
		hLayout.addComponent(toDate);
		hLayout.addComponent(btnFind);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
	
	private void compInit()
	{
		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);
		fromDate.setWidth(px);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);
		toDate.setWidth(px);

		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date",70);
		table.addContainerProperty("Bank AC Name", Label.class, new Label());
		table.setColumnWidth("Bank AC Name",200);
		table.addContainerProperty("CQ Book No", Label.class, new Label());
		table.setColumnWidth("CQ Book No",70);
		table.addContainerProperty("Folio(From-To)", Label.class, new Label());
		table.setColumnWidth("Folio(From-To)",180);
		table.addContainerProperty("id", Label.class, new Label());
		table.setColumnWidth("id",50);
		
		table.setColumnCollapsed("id", true);
	}
}
