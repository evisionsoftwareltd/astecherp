package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

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

@SuppressWarnings("serial")
public class ShiftFindWin extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtShiftId;
	private Table table=new Table();

	public String receiptShiftId = "";

	private ArrayList<Label> lbShiftID = new ArrayList<Label>();
	private ArrayList<Label> lbShiftName = new ArrayList<Label>();

	@SuppressWarnings("unused")
	private String frmName;
	@SuppressWarnings("unused")
	private com.common.share.SessionBean sessionBean;
	public ShiftFindWin(SessionBean sessionBean,TextField txtShiftId,String frmName)
	{
		this.txtShiftId = txtShiftId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SHIFT INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("350px");
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
		lbShiftID.add(ar, new Label(""));
		lbShiftID.get(ar).setWidth("100%");
		lbShiftID.get(ar).setImmediate(true);
		lbShiftID.get(ar).setHeight("23px");

		lbShiftName.add(ar, new Label(""));
		lbShiftName.get(ar).setWidth("100%");
		lbShiftName.get(ar).setImmediate(true);
		lbShiftName.get(ar).setHeight("23px");

		table.addItem(new Object[]{lbShiftID.get(ar),lbShiftName.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptShiftId = lbShiftID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtShiftId.setValue(receiptShiftId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lbShiftID.size(); i++)
		{
			lbShiftID.get(i).setValue("");
			lbShiftName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query ="select ISNULL(vShiftId,'') vShiftId,ISNULL(vShiftName,'') vShiftName from " +
					"tbShiftInformation order by ISNULL(vShiftId,'')";
			List <?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					lbShiftID.get(i).setValue(element[0].toString());
					lbShiftName.get(i).setValue(element[1].toString());
				
					if((i)==lbShiftID.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("tableDataAdding", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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

		table.addContainerProperty("Shift ID", Label.class, new Label());
		table.setColumnWidth("Shift ID",60);

		table.addContainerProperty("Shift Name", Label.class, new Label());
		table.setColumnWidth("Shift Name",200);
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