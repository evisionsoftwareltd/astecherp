package acc.appform.transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.hibernate.Session;

import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
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
public class InvoiceVoucherFind extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "Find", "", "", "", "");
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout firstHorizonTal = new HorizontalLayout();
	private HorizontalLayout thirdHorizonTal = new HorizontalLayout();

	private Label lblfromDate = new Label("From :");
	private PopupDateField fromDate = new PopupDateField();
	private Label lbltoDate = new Label("To :");
	private PopupDateField toDate = new PopupDateField();

	private TextField txtFind = new TextField();
	private TextField txtFindVoucherNo = new TextField();

	private ComboBox cmbPartyName;

	private Label lblFindVoucher = new Label("Voucher No :");
	private Table table = new Table();

	private ArrayList<Label> tbReferenceNo = new  ArrayList<Label>();
	private ArrayList<Label> tbVoucherNo = new  ArrayList<Label>();
	private ArrayList<Label> tbVoucherDate = new ArrayList<Label>();
	private ArrayList<Label> tbLedgerName = new ArrayList<Label>();
	private ArrayList<Label> tbNarration = new ArrayList<Label>();
	private ArrayList<Label> tbAmount = new ArrayList<Label>();

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtfDMY = new SimpleDateFormat("dd-MM-yyyy");

	private String comWidth = "110px";
	SessionBean sessionBean;

	public InvoiceVoucherFind(SessionBean sessionBean, TextField txtInvoiceNo, String title,String type)
	{
		this.sessionBean = sessionBean;
		this.setCaption(title+" :: " + sessionBean.getCompany());
		this.txtFind = txtInvoiceNo;

		this.setWidth("870px");
		this.setResizable(false);
		this.setStyleName("cwindow");

		table.setWidth("100%");
		table.setHeight("300px");

		table.addContainerProperty("Reference No", Label.class , null);
		table.setColumnWidth("Reference No",100);

		table.addContainerProperty("Voucher No", Label.class , null);
		table.setColumnWidth("Voucher No",80);

		table.addContainerProperty("Voucher Date", Label.class , null);
		table.setColumnWidth("Voucher Date",80);

		table.addContainerProperty("Ledger Name", Label.class , null);
		table.setColumnWidth("Ledger Name",200);

		table.addContainerProperty("Narration", Label.class , null);
		table.setColumnWidth("Narration",80);

		table.addContainerProperty("Amount", Label.class , null);
		table.setColumnWidth("Amount",80);

		table.setColumnCollapsingAllowed(true);
		table.setColumnCollapsed("Reference No", true);
		table.setSelectable(true);

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					txtFind.setValue(tbReferenceNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					close();
				}
			}
		});

		addAllComponent();

		cmbPartyData();

		this.addComponent(mainLayout);
		Component ob[] = {fromDate, toDate, button.btnFind};
		new FocusMoveByEnter(this,ob);
		fromDate.focus();

		tableinitialise();
	}

	private void tableinitialise()
	{
		for(int i=0; i<10; i++)
		{
			tableRowAdd(i);
		}
	}
	
	private void dataclear()
	{
	   for(int i=0;i<tbReferenceNo.size();i++)
	   {
		   tbReferenceNo.get(i).setValue("");
		   tbVoucherNo.get(i).setValue("");
		   tbVoucherDate.get(i).setValue("");
		   tbLedgerName.get(i).setValue("");
		   tbNarration.get(i).setValue("");
		   tbAmount.get(i).setValue("");
		    
	   }
		
	}

	private void tableRowAdd(final int ar)
	{
		tbReferenceNo.add(ar,new Label());
		tbReferenceNo.get(ar).setWidth("100%");
		tbReferenceNo.get(ar).setValue(ar+1);

		tbVoucherNo.add(ar,new Label());
		tbVoucherNo.get(ar).setWidth("100%");
		tbVoucherNo.get(ar).setImmediate(true);

		tbVoucherDate.add(ar,new Label());
		tbVoucherDate.get(ar).setWidth("100%");
		tbVoucherDate.get(ar).setImmediate(true);

		tbLedgerName.add(ar,new Label());
		tbLedgerName.get(ar).setWidth("100%");
		tbLedgerName.get(ar).setImmediate(true);

		tbNarration.add(ar,new Label());
		tbNarration.get(ar).setWidth("100%");
		tbNarration.get(ar).setImmediate(true);

		tbAmount.add(ar,new Label());
		tbAmount.get(ar).setWidth("100%");
		tbAmount.get(ar).setImmediate(true);

		table.addItem(new Object[]{tbReferenceNo.get(ar),tbVoucherNo.get(ar),tbVoucherDate.get(ar),
				tbLedgerName.get(ar),tbNarration.get(ar),tbAmount.get(ar)},ar);
	}

	private void cmbPartyData()
	{
		cmbPartyName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> listParty = session.createSQLQuery("select distinct vPartyId,vPartyName from tbReceivedAgainstInvoiceInfo"+
					" order by vPartyName").list();
			for(Iterator<?> iter = listParty.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addAllComponent()
	{
		firstHorizonTal.addComponent(new Label("Party :"));
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setWidth("220px");
		firstHorizonTal.addComponent(cmbPartyName);
		firstHorizonTal.setSpacing(true);

		firstHorizonTal.addComponent(lblfromDate);
		fromDate.setWidth(comWidth);
		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);
		firstHorizonTal.addComponent(fromDate);
		firstHorizonTal.setSpacing(true);

		firstHorizonTal.addComponent(lbltoDate);
		toDate.setWidth(comWidth);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);
		firstHorizonTal.addComponent(toDate);
		firstHorizonTal.setSpacing(true);
		
		firstHorizonTal.addComponent(lblFindVoucher);
		txtFindVoucherNo.setWidth("120px");
		txtFindVoucherNo.setHeight("24px");
		txtFindVoucherNo.setImmediate(true);
		firstHorizonTal.addComponent(txtFindVoucherNo);

		mainLayout.addComponent(firstHorizonTal);
		mainLayout.setSpacing(true);
		
		
		firstHorizonTal.setSpacing(true);

		button.btnFind.setHeight("25px");
		button.btnFind.setWidth("32.5px");
		firstHorizonTal.addComponent(button.btnFind);

		mainLayout.addComponent(thirdHorizonTal);
		mainLayout.setComponentAlignment(thirdHorizonTal, Alignment.BOTTOM_RIGHT);

		button.btnFind.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				/*if(cmbPartyName.getValue()!=null)
				{*/
					valueAdd();
				/*}
				else
				{
					showNotification("Warning!","Select Party Name",Notification.TYPE_WARNING_MESSAGE);
					cmbPartyName.focus();
				}*/
			}
		});

		mainLayout.addComponent(table);
	}

	private void valueAdd()
	{
		String query = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String partyId="";
			String VoucherNO="";
			if(cmbPartyName.getValue()!=null)
			{
				partyId=cmbPartyName.getValue().toString();
			}
			else
			{
			   partyId="%";	
			 	
			}
			
			if(!txtFindVoucherNo.getValue().toString().isEmpty())
			{
				VoucherNO="%"+txtFindVoucherNo.getValue().toString()+"%";
			}
			else
			{
				VoucherNO="%";	
			 	
			}
			
			
			
			/*query = "select vReferenceNo,vVoucherNo,dVoucherDate,vDepoLedgerName,'Invoice' vNarration,mTotalAmountAfterTax" +
					" from tbReceivedAgainstInvoiceInfo where vPartyId like  '"+cmbPartyName.getValue().toString()+"' and" +
					" dVoucherDate between '"+dtfYMD.format(fromDate.getValue())+"' and '"+dtfYMD.format(toDate.getValue())+"' ";
			*/
			query = "select vReferenceNo,vVoucherNo,dVoucherDate,vDepoLedgerName,'Invoice' vNarration,mTotalAmountAfterTax" +
					" from tbReceivedAgainstInvoiceInfo where vPartyId like  '"+partyId+"' and vVoucherNo like '"+VoucherNO+"' and " +
					" dVoucherDate between '"+dtfYMD.format(fromDate.getValue())+"' and '"+dtfYMD.format(toDate.getValue())+"' ";
			dataclear();
			
			List<?> list = session.createSQLQuery(query).list();
			int i = 0;
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				tbReferenceNo.get(i).setValue(element[0].toString());
				tbVoucherNo.get(i).setValue(element[1].toString());
				tbVoucherDate.get(i).setValue(dtfDMY.format(element[2]));
				tbLedgerName.get(i).setValue(element[3].toString());

				tbNarration.get(i).setValue(element[4].toString());
				tbAmount.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[5].toString())));
				if(tbReferenceNo.size()-1==i)
				{
					tableRowAdd(i+1);
				}
				i++;
			}
			if(i == 0)
			{
				showNotification("Warning!","No Data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			showNotification("Warning!", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}
}