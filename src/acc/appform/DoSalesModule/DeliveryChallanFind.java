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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class DeliveryChallanFind extends Window
{
	private AbsoluteLayout mainLayout = new AbsoluteLayout();
	private TextField txtChallanNo;
	private Table table = new Table();

	public String receiptSupplierId = "";

	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;

	private ComboBox cmbChallanNo;

	CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");

	private ArrayList<Label> lblChallanNo = new ArrayList<Label>();
	private ArrayList<Label> lblGatePassNo = new ArrayList<Label>();
	private ArrayList<Label> lblPartyName = new ArrayList<Label>();

	private ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	public DeliveryChallanFind(SessionBean sessionBean,TextField txtChallanNo, String frmName)
	{
		this.txtChallanNo = txtChallanNo;
		this.setCaption("FIND DELIVERY CHALLAN :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("650px");
		this.setHeight("350px");

		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");

		buildMainLayout();
		setContent(mainLayout);

		tableInitialise();
		setEventAction();
		tableclear();

		focusEnter();
	}

	private void focusEnter()
	{
		allComp.add(dFromDate);
		allComp.add(dToDate);

		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	public void tableInitialise()
	{
		for(int i=0; i<8; i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lblChallanNo.add(ar, new Label(""));
		lblChallanNo.get(ar).setWidth("100%");
		lblChallanNo.get(ar).setImmediate(true);
		lblChallanNo.get(ar).setHeight("20px");

		lblGatePassNo.add(ar, new Label(""));
		lblGatePassNo.get(ar).setWidth("100%");
		lblGatePassNo.get(ar).setImmediate(true);

		lblPartyName.add(ar, new Label(""));
		lblPartyName.get(ar).setWidth("100%");
		lblPartyName.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblChallanNo.get(ar),lblGatePassNo.get(ar),lblPartyName.get(ar)},ar);
	}

	public void setEventAction()
	{
		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				finButtonEvent();
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lblChallanNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtChallanNo.setValue(receiptSupplierId);
					windowClose();
				}
			}
		});

		cmbChallanNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbChallanNo.getValue()!=null)
				{
					setTableData();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblChallanNo.size(); i++)
		{
			lblChallanNo.get(i).setValue("");
			lblGatePassNo.get(i).setValue("");
			lblPartyName.get(i).setValue("");
		}
	}

	private void finButtonEvent()
	{
		cmbChallanNo.removeAllItems();

		String from = dFormat.format(dFromDate.getValue());
		String to = dFormat.format(dToDate.getValue());
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String Findquery ="select vChallanNo,vGatePassNo,vPartyName from" +
					" tbDeliveryChallanInfo where CONVERT(date,dChallanDate) between " +
					" '"+from+"' and '"+to+"' order by iAutoId";
			List<?> list = session.createSQLQuery(Findquery).list();
			if(!list.isEmpty())
			{
				tableclear();
				int i = 0;
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					lblChallanNo.get(i).setValue(element[0].toString());
					lblGatePassNo.get(i).setValue(element[1].toString());
					lblPartyName.get(i).setValue(element[2].toString());

					if((i)==lblPartyName.size()-1)
					{
						tableRowAdd(i+1);
					}

					cmbChallanNo.addItem(element[0]);

					i++;
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setTableData()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String Findquery ="select vChallanNo,vGatePassNo,vPartyName from" +
					" tbDeliveryChallanInfo where vChallanNo = '"+cmbChallanNo.getValue().toString()+"'";
			List<?> list = session.createSQLQuery(Findquery).list();
			if(!list.isEmpty())
			{
				tableclear();
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblChallanNo.get(0).setValue(element[0].toString());
					lblGatePassNo.get(0).setValue(element[1].toString());
					lblPartyName.get(0).setValue(element[2].toString());
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
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

	private AbsoluteLayout buildMainLayout()
	{
		lblFromDate = new Label("From Date:");
		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate, "top:23.0px; left:15.0px;");

		dFromDate = new PopupDateField();
		dFromDate.setWidth("110px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:20.0px; left:80.0px;");

		lblToDate = new Label("To Date:");
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:23.0px; left:200.0px;");

		dToDate = new PopupDateField();
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:20.0px; left:250.0px;");

		mainLayout.addComponent(cButton,"top:18.0px; left:375.0px");

		cmbChallanNo = new ComboBox("Challan No :");
		cmbChallanNo.setHeight("-1px");
		cmbChallanNo.setWidth("150");
		cmbChallanNo.setImmediate(true);
		mainLayout.addComponent(cmbChallanNo, "top:20.0px; left:475.0px");

		table.setSelectable(true);
		table.setWidth("615px");
		table.setHeight("230px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Challan No", Label.class, new Label());
		table.setColumnWidth("Challan No",130);

		table.addContainerProperty("Gate Pass No", Label.class, new Label());
		table.setColumnWidth("Gate Pass No",130);

		table.addContainerProperty("Party Name", Label.class, new Label());
		table.setColumnWidth("Party Name",295);

		mainLayout.addComponent(table,"top:66.00px; left:15.00px;");

		return mainLayout;
	}
}