package acc.appform.transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

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

public class MoneyReceiptFind extends Window
{
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private TextField txtInvoiceId;
	private Table table=new Table();

	public String receiptSupplierId = "";

	private Label lblFromDate;
	private PopupDateField dFromDate;

	private Label lblToDate;
	private PopupDateField dToDate;

	CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");

	private ComboBox cmbMrNo;

	private ArrayList<Label> lblMrNo = new ArrayList<Label>();
	private ArrayList<Label> lblMrDate = new ArrayList<Label>();
	private ArrayList<Label> lblPartyLedger = new ArrayList<Label>();
	private ArrayList<Label> lblVoucherNo = new ArrayList<Label>();

	private ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormatView = new SimpleDateFormat("dd-MM-yyyy");

	private String receiptType;

	public MoneyReceiptFind(SessionBean sessionBean,TextField txtInvoiceId, String receiptType)
	{
		this.txtInvoiceId = txtInvoiceId;
		this.sessionBean=sessionBean;
		this.receiptType=receiptType;

		this.setCaption("FIND MONEY RECEIPT OF "+receiptType.toUpperCase()+" :: "+sessionBean.getCompany());
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
		lblMrNo.add(ar, new Label(""));
		lblMrNo.get(ar).setWidth("100%");
		lblMrNo.get(ar).setImmediate(true);
		lblMrNo.get(ar).setHeight("15px");

		lblMrDate.add(ar, new Label(""));
		lblMrDate.get(ar).setWidth("100%");
		lblMrDate.get(ar).setImmediate(true);

		lblPartyLedger.add(ar, new Label(""));
		lblPartyLedger.get(ar).setWidth("100%");
		lblPartyLedger.get(ar).setImmediate(true);
		
		lblVoucherNo.add(ar, new Label(""));
		lblVoucherNo.get(ar).setWidth("100%");
		lblVoucherNo.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblMrNo.get(ar),lblMrDate.get(ar),lblPartyLedger.get(ar),lblVoucherNo.get(ar)},ar);
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
					receiptSupplierId = lblVoucherNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtInvoiceId.setValue(receiptSupplierId);
					windowClose();
				}
			}
		});

		cmbMrNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbMrNo.getValue()!=null)
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

		cmbMrNo.removeAllItems();

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String Findquery =" SELECT vMrNo,dDate,vLedgerName,vVoucherNo FROM tbMoneyReceipt where dDate between" +
					" '"+from+"' and '"+to+"' and vReceiptType = '"+receiptType+"' order by iAutoId ";

			List list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				clearTable();
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblMrNo.get(i).setValue(element[0].toString());
					lblMrDate.get(i).setValue(dFormatView.format(element[1]));
					lblPartyLedger.get(i).setValue(element[2]);
					lblVoucherNo.get(i).setValue(element[3]);

					cmbMrNo.addItem(element[0]);

					if((i)==lblMrNo.size()-1)
					{
						tableRowAdd(i+1);
					}

					i++;
				}
			}
			else
			{
				this.getParent().showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void finButtonEventCombo()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String Findquery =" SELECT vMrNo,dDate,vLedgerName,vVoucherNo FROM tbMoneyReceipt where"+
					" vMrNo = '"+cmbMrNo.getValue().toString()+"' ";

			List list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				Iterator iter = list.iterator();
				if( iter.hasNext())
				{						  
					Object[] element = (Object[]) iter.next();

					lblMrNo.get(0).setValue(element[0].toString());
					lblMrDate.get(0).setValue(dFormatView.format(element[1]));
					lblPartyLedger.get(0).setValue(element[2]);
					lblVoucherNo.get(0).setValue(element[3]);
				}
			}
			else
			{
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

	private void clearTable()
	{
		for (int i=0; i<lblMrNo.size(); i++)
		{
			lblMrNo.get(i).setValue("");
			lblMrDate.get(i).setValue("");
			lblPartyLedger.get(i).setValue("");
			lblVoucherNo.get(i).setValue("");
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

		cmbMrNo = new ComboBox();
		cmbMrNo.setImmediate(true);
		cmbMrNo.setHeight("-1px");
		cmbMrNo.setWidth("160px");
		mainLayout.addComponent(cmbMrNo, "top:18.0px; left:435.0px");

		table.setSelectable(true);
		table.setWidth("587px");
		table.setHeight("295px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("MR No", Label.class, new Label());
		table.setColumnWidth("MR No",120);

		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date",70);

		table.addContainerProperty("Account Head", Label.class, new Label());
		table.setColumnWidth("Account Head",310);
		
		table.addContainerProperty("Voucher No", Label.class, new Label());
		table.setColumnWidth("Voucher No",100);
		table.setColumnCollapsed("Voucher No", true);

		mainLayout.addComponent(table,"top:50.00px; left:15.00px;");

		return mainLayout;
	}
}