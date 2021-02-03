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
public class SalesInvoiceFind extends Window
{
	private AbsoluteLayout mainLayout = new AbsoluteLayout();

	private TextField txtInvoiceId;
	public String receiptSupplierId = "";

	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;

	CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");

	private ComboBox cmbBillNo;

	private Table table = new Table();
	private ArrayList<Label> lblBillNo = new ArrayList<Label>();
	private ArrayList<Label> lblInvoiceDate = new ArrayList<Label>();
	private ArrayList<Label> lblDeliveryChallanNo = new ArrayList<Label>();
	private ArrayList<Label> lblPartyName = new ArrayList<Label>();

	private ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormatView = new SimpleDateFormat("dd-MM-yy");

	public SalesInvoiceFind(SessionBean sessionBean,TextField txtInvoiceId, String frmName)
	{
		this.txtInvoiceId = txtInvoiceId;
		this.setCaption("FIND SALES INVOICE :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("620px");
		this.setHeight("400px");

		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");

		buildMainLayout();		
		setContent(mainLayout);

		tableInitialise();
		setEventAction();

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
		lblBillNo.add(ar, new Label(""));
		lblBillNo.get(ar).setWidth("100%");
		lblBillNo.get(ar).setImmediate(true);
		lblBillNo.get(ar).setHeight("15px");

		lblInvoiceDate.add(ar, new Label(""));
		lblInvoiceDate.get(ar).setWidth("100%");
		lblInvoiceDate.get(ar).setImmediate(true);

		lblDeliveryChallanNo.add(ar, new Label(""));
		lblDeliveryChallanNo.get(ar).setWidth("100%");
		lblDeliveryChallanNo.get(ar).setImmediate(true);

		lblPartyName.add(ar, new Label(""));
		lblPartyName.get(ar).setWidth("100%");
		lblPartyName.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblBillNo.get(ar),lblInvoiceDate.get(ar),lblDeliveryChallanNo.get(ar),lblPartyName.get(ar)},ar);
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
					receiptSupplierId = lblBillNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtInvoiceId.setValue(receiptSupplierId);
					windowClose();
				}
			}
		});

		cmbBillNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbBillNo.getValue()!=null)
				{
					clearTable();
					finButtonEventCombo();
				}
			}
		});
	}

	private void finButtonEvent()
	{
		String from = dFormat.format(dFromDate.getValue())+" 00:00:00";
		String to = dFormat.format(dToDate.getValue())+" 23:59:59";

		cmbBillNo.removeAllItems();

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String Findquery =" select vBillNo,dInvoiceDate,vDeliveryChallanNo,vPartyName from tbSalesInvoiceInfo"+
				" where dInvoiceDate between '"+from+"' and '"+to+"' order by iAutoId ";
			List<?> list = session.createSQLQuery(Findquery).list();
			if(!list.isEmpty())
			{
				clearTable();
				int i = 0;
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					lblBillNo.get(i).setValue(element[0].toString());
					lblInvoiceDate.get(i).setValue(dFormatView.format(element[1]));
					lblDeliveryChallanNo.get(i).setValue(element[2].toString().replaceAll("'", ""));
					lblPartyName.get(i).setValue(element[3]);
					cmbBillNo.addItem(element[0]);
					cmbBillNo.setItemCaption(element[0], element[0].toString());

					if((i)==lblPartyName.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void finButtonEventCombo()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String Findquery =" select vBillNo,dInvoiceDate,vDeliveryChallanNo,vPartyName from tbSalesInvoiceInfo"+
					" where vBillNo = '"+cmbBillNo.getValue().toString()+"' ";
			List<?> list = session.createSQLQuery(Findquery).list();
			if(!list.isEmpty())
			{
				Iterator<?> iter = list.iterator();
				if(iter.hasNext())
				{
					Object[] element = (Object[]) iter.next();
					lblBillNo.get(0).setValue(element[0].toString());
					lblInvoiceDate.get(0).setValue(dFormatView.format(element[1]));
					lblDeliveryChallanNo.get(0).setValue(element[2]);
					lblPartyName.get(0).setValue(element[3]);
				}
			}
			else
			{
				showNotification("Warning!","No data found.", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private void clearTable()
	{
		for (int i=0; i<lblBillNo.size(); i++)
		{
			lblBillNo.get(i).setValue("");
			lblInvoiceDate.get(i).setValue("");
			lblDeliveryChallanNo.get(i).setValue("");
			lblPartyName.get(i).setValue("");			
		}
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

		lblToDate = new Label("To");
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:23.0px; left:195.0px;");

		dToDate = new PopupDateField();
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:20.0px; left:220.0px;");

		mainLayout.addComponent(cButton,"top:18.0px; left:340.0px");

		cmbBillNo = new ComboBox();
		cmbBillNo.setImmediate(true);
		cmbBillNo.setHeight("-1px");
		cmbBillNo.setWidth("160px");
		mainLayout.addComponent(cmbBillNo, "top:18.0px; left:435.0px");

		table.setSelectable(true);
		table.setWidth("587px");
		table.setHeight("295px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Bill No", Label.class, new Label());
		table.setColumnWidth("Bill No",110);

		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date",50);

		table.addContainerProperty("Delivery Challan No", Label.class, new Label());
		table.setColumnWidth("Delivery Challan No",200);

		table.addContainerProperty("Party Name", Label.class, new Label());
		table.setColumnWidth("Party Name",170);

		mainLayout.addComponent(table,"top:50.00px; left:15.00px;");

		return mainLayout;
	}
}