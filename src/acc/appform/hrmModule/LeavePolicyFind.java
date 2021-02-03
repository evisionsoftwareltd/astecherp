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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class LeavePolicyFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private TextField txtReceiptId;
	private Table table=new Table();

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public String DeptId="a";
	public String receiptDept="a";

	private ArrayList<Label>lblDate = new ArrayList<Label>();	
	private ArrayList<Label>lblSl = new ArrayList<Label>();


	@SuppressWarnings("unused")
	private String frmName;

	public LeavePolicyFind(SessionBean sessionBean,TextField txtReceiptId,String frmName)
	{
		this.txtReceiptId=txtReceiptId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND WINDOW :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("570px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		FindData();
		setEventAction();
	}

	public void tableInitialise()
	{
		for(int i=0;i<50;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lblDate.add(ar,new Label());
		lblDate.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblDate.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					DeptId=lblDate.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptId.setValue(DeptId);
					System.out.println("Find dept:"+DeptId);
					windowClose();
				}
			}
		});
	}

	private void windowClose()
	{
		this.close();
	}

	private void FindData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " SELECT distinct YEAR(currentYear),iClOpening,iCarryCl,iSlOpening,iCarrySl,iAlOpening,iCarryAl,iMlOpening,iCarryMl from tbLeaveBalanceNew ";
			List <?> lst= session.createSQLQuery(sql).list();
			int i=1;

			if(!lst.isEmpty())
				for (Iterator <?> iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					if(i==lblSl.size())
					{
						tableRowAdd(i);
					}
					table.addItem(new Object[] {i,element[0].toString(),element[1].toString(),element[3].toString(), element[5].toString(),element[7].toString()}, new Integer(i));
					lblDate.get(i).setValue(element[0].toString());
					i++;
				}
			else
			{
				table.addItem("");
				getParent().showNotification("Warning: ","There are no Data.");					
			}
		}
		catch(Exception exp)
		{
			showNotification("FindData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
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

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",30);

		table.addContainerProperty("Opening Year", Label.class, new Label());
		table.setColumnWidth("Opening Year",80);

		table.addContainerProperty("Casual Leave", Label.class, new Label());
		table.setColumnWidth("Casual Leave",80);

		table.addContainerProperty("Sick Leave", Label.class, new Label());
		table.setColumnWidth("Sick Leave",80);

		table.addContainerProperty("Annual Leave", Label.class, new Label());
		table.setColumnWidth("Annual Leave",80);


		table.addContainerProperty("Maternity Leave", Label.class, new Label());
		table.setColumnWidth("Maternity Leave",80);

		table.setColumnAlignments(new String[] { Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER });	
	}

	private void compAdd()
	{
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}