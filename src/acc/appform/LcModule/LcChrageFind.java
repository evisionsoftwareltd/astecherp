package acc.appform.LcModule;

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
import com.common.share.TextRead;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class LcChrageFind extends Window
{
	private SessionBean sessionBean;
	
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private TextField txtInvoiceId;
	private TextRead txtreferenceNo;
	private Table table=new Table();

	public String receiptSupplierId = "";
	String ReferenceNo="";

	private Label lblFromDate;
	private PopupDateField dFromDate;
	
	private Label lblToDate;
	private PopupDateField dToDate;
	
	CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");
	
	private ArrayList<Label> lblReferenceNo = new ArrayList<Label>();
	private ArrayList<Label> lblLcNo = new ArrayList<Label>();
	private ArrayList<Label> lblVoucherNo = new ArrayList<Label>();
	//private ArrayList<Label> lblShipName = new ArrayList<Label>();
	
	private ArrayList<Component> allComp = new ArrayList<Component>();
	
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	public LcChrageFind(SessionBean sessionBean,TextField txtInvoiceId, String frmName,TextRead txtreferenceNo)
	{
		this.txtInvoiceId = txtInvoiceId;
		this.txtreferenceNo = txtreferenceNo;
		this.sessionBean=sessionBean;
		this.setCaption("FIND LC CHARGE :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("520px");
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
		lblReferenceNo.add(ar, new Label(""));
		lblReferenceNo.get(ar).setWidth("100%");
		lblReferenceNo.get(ar).setImmediate(true);
		lblReferenceNo.get(ar).setHeight("16px");
		
		lblLcNo.add(ar, new Label(""));
		lblLcNo.get(ar).setWidth("100%");
		lblLcNo.get(ar).setImmediate(true);
		
		lblVoucherNo.add(ar, new Label(""));
		lblVoucherNo.get(ar).setWidth("100%");
		lblVoucherNo.get(ar).setImmediate(true);

		/*lblShipName.add(ar, new Label(""));
		lblShipName.get(ar).setWidth("100%");
		lblShipName.get(ar).setImmediate(true);*/
		
		table.addItem(new Object[]{lblReferenceNo.get(ar),lblLcNo.get(ar),lblVoucherNo.get(ar)},ar);
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
					
					ReferenceNo = lblReferenceNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtreferenceNo.setValue(ReferenceNo);
					
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblLcNo.size(); i++)
		{
			lblReferenceNo.get(i).setValue("");
			lblLcNo.get(i).setValue("");
			lblVoucherNo.get(i).setValue("");
			//lblShipName.get(i).setValue("");
		}
	}

	private void finButtonEvent()
	{
		String from = dFormat.format(dFromDate.getValue())+" 00:00:00";
		String to = dFormat.format(dToDate.getValue())+" 23:59:59";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
		/*	String Findquery =  " select distinct a.shipLcNo,a.voucherNo,a.shipName,a.bankName from tbLcChargeInfo as a" +
								" inner join tbLcChargeDetails as b on a.voucherNo=b.voucherNo where a.voucherDate" +
								" between '"+from+"' and '"+to+"' ";*/
			
			String findQuery="  select referenceNo,lcNo,voucherNo from tbLcChargeInfo where date between '"+from+"' and '"+to+"' ";
			
			List list = session.createSQLQuery(findQuery).list();
			
			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblReferenceNo.get(i).setValue(element[0]);
					lblLcNo.get(i).setValue(element[1]);
					lblVoucherNo.get(i).setValue(element[2]);
					//lblShipName.get(i).setValue(element[3]);
					
					if((i)==lblVoucherNo.size()-1)
					{
						tableRowAdd(i+1);
					}
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

	private void windowClose()
	{
		this.close();
	}

	private AbsoluteLayout buildMainLayout()
	{
		lblFromDate = new Label("From Date:");
		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate, "top:23.0px; left:35.0px;");
		
		dFromDate = new PopupDateField();
		dFromDate.setWidth("110px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:20.0px; left:100.0px;");
		
		lblToDate = new Label("To Date:");
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:23.0px; left:220.0px;");
		
		dToDate = new PopupDateField();
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:20.0px; left:270.0px;");
		
		mainLayout.addComponent(cButton,"top:18.0px; left:400.0px");
		
		table.setSelectable(true);
		table.setWidth("485px");
		table.setHeight("240px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Reference No", Label.class, new Label());
		table.setColumnWidth("Reference No",100);
		
		table.addContainerProperty("L/C No", Label.class, new Label());
		table.setColumnWidth("L/C No",150);

		table.addContainerProperty("Voucher No", Label.class, new Label());
		table.setColumnWidth("Voucher No",150);
		
		//table.addContainerProperty("Bank Name", Label.class, new Label());
		//table.setColumnWidth("Bank Name",150);
		
		mainLayout.addComponent(table,"top:60.00px; left:15.00px;");
		
		return mainLayout;
	}
}