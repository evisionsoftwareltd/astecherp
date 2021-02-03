package acc.appform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class DemandFindWindow extends Window
{
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout hLayout = new HorizontalLayout();
	private PopupDateField fromDate = new PopupDateField();
	private PopupDateField toDate = new PopupDateField();
	private Label lblFrom = new Label("Form:");
	private Label lblTo = new Label("To:");
	private TextField txtReceiptId;

	CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");

	private Table table = new Table();

	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private ComboBox cmbPartyName;

	public String DeptId = "a";
	public String receiptDept = "a";

	private ArrayList<Label>lblIdAndDate = new ArrayList<Label>();	
	private ArrayList<Component> allComp = new ArrayList<Component>();

	public DemandFindWindow(SessionBean sessionBean,TextField txtReceiptId,String frmName)
	{
		this.txtReceiptId = txtReceiptId;
		this.setCaption("PO FIND WINDOW :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("700px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");

		compInit();
		compAdd();
		tableInitialise();

		partyNameData();

		setEventAction();

		focusEnter();
	}

	public void partyNameData()
	{
		cmbPartyName.removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> lst = session.createSQLQuery(" select partyCode,partyName from tbPartyInfo where isActive='1' order by partyName ").list();
			for(Iterator<?> iter = lst.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
	}

	public void tableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private void focusEnter()
	{
		allComp.add(fromDate);
		allComp.add(toDate);

		allComp.add(cButton.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	public void tableRowAdd(final int ar)
	{
		lblIdAndDate.add(ar,new Label());
		lblIdAndDate.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblIdAndDate.get(ar)},ar);
	}

	public void setEventAction()
	{
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbPartyName.getValue()!=null)
				{
					tableClear();
					findButtonEvent();
				}
				else
				{
					showNotification("Warning!","Select party name",Notification.TYPE_WARNING_MESSAGE);
					cmbPartyName.focus();
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					DeptId=lblIdAndDate.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
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

	private void findButtonEvent()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String sql = " select convert(date, doDate,105) as doDate, doNo, partyname from tbDemandOrderInfo where Convert(date,doDate)" +
					" between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' and" +
					" partyId = '"+cmbPartyName.getValue().toString()+"' ";
			List<?> lst = session.createSQLQuery(sql).list();
			int i=1;
			if(!lst.isEmpty())
			{
				for(Iterator<?> iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					if(i==lblIdAndDate.size())
					{
						tableRowAdd(i);
					}
					table.addItem(new Object[] {i,element[1].toString(),element[0],element[2]}, new Integer(i));
					lblIdAndDate.get(i).setValue(element[1].toString()+"@"+element[0].toString());
					i++;
				}
			}
			else
			{
				getParent().showNotification("Warning: ","There are no Data.");					
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void compInit()
	{
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("270px");
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setNullSelectionAllowed(false);

		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setWidth("110px");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);

		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setWidth("110px");
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);

		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",20);

		table.addContainerProperty("PO No", Label.class, new Label());
		table.setColumnWidth("PO No",240);

		table.addContainerProperty("PO Date", Label.class, new Label());
		table.setColumnWidth("PO Date",65);

		table.addContainerProperty("Party Name", Label.class, new Label());
		table.setColumnWidth("Party Name",265);

		table.setColumnAlignments(new String[] { Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_CENTER, Table.ALIGN_LEFT});	
	}

	private void compAdd()
	{
		hLayout.setSpacing(true);
		hLayout.addComponent(cmbPartyName);
		hLayout.addComponent(lblFrom);
		hLayout.addComponent(fromDate);
		hLayout.addComponent(lblTo);
		hLayout.addComponent(toDate);
		hLayout.addComponent(cButton.btnFind);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}

	private void tableClear()
	{
		table.removeAllItems();
	}
}