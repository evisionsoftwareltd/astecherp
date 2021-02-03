package acc.appform.transaction;

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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class FindWindowMoneyReceipt extends Window {
	
	private VerticalLayout mainLayout=new VerticalLayout();
	private HorizontalLayout hLayout=new HorizontalLayout();
	
	private PopupDateField fromDate = new PopupDateField();
	private PopupDateField toDate = new PopupDateField();
	private Label lblFrom = new Label("Form Date:");
	private Label lblTo = new Label("To Date:");
	
	private NativeButton btnFind = new NativeButton("Find");
	
	private Table table = new Table();
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy-MM-dd");
	
	private ArrayList<Label> lblMrNo = new ArrayList<Label>();
	
	private SessionBean sessionBean ;
	private String string ;
	private TextRead mrNo;
	
	private String px = "150px";

	public FindWindowMoneyReceipt(SessionBean sessionBean, TextRead mrNo, String string) 
	{	
		this.sessionBean = sessionBean;
		this.string = string;
		this.mrNo = mrNo;
		this.setCaption("Voucher Search ( "+string+" ) :: "+sessionBean.getCompany());
		
		this.center();
		this.setWidth("740px");
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
					String id = lblMrNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					mrNo.setValue(id);

					System.out.println("find "+id);

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
		Transaction tx;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List lst = null;
			
			String sql = null ;
			
			if(string.equalsIgnoreCase("cash"))
			{
				sql = " Select DISTINCT convert(int,a.MR_no) as mrNo,a.voucher_no,a.dDate, "+
					  " ( Select distinct costCentreName from tbCostCentre where id = b.costId ) as costCenter, "+
					  " ( Select distinct Ledger_Name from tbLedger where Ledger_Id  = b.Ledger_Id ) as accountHead, "+
					  " a.ReceivedBy "+
					  " from MRandVoucherNo as a inner join vwVoucher as b on a.voucher_no = b.Voucher_No  "+ 
					  " where b.vouchertype = 'cca' and b.Ledger_Id != 'AL183' and  "+
					  " a.dDate >= '"+dF.format(fromDate.getValue())+" 00:00:00"+"' and a.dDate <= '"+dF.format(toDate.getValue())+" 23:59:59"+"' order by convert(int,a.MR_no)  ";
			}
			else if(string.equalsIgnoreCase("cheque"))
			{
				sql = " Select DISTINCT convert(int,a.MrNo) as mrNo,a.Voucher_No,a.Cheque_Date,a.Cheque_No, "+
					  " ( Select distinct Ledger_Name from tbLedger where Ledger_Id = b.Ledger_Id ) as accountHead, "+
					  " ( Select distinct Ledger_Name from tbLedger where  Ledger_Id = a.Bank_Id and Create_From like 'A9%' ) as depositBank "+
					  " from vwChequeDetails as a inner join vwVoucher as b on a.Voucher_No = b.Voucher_No "+
  					  " where b.CrAmount != 0.00 and b.vouchertype = 'cba' and "+
  					  " b.Date >= '"+dF.format(fromDate.getValue())+" 00:00:00"+"' and b.Date <= '"+dF.format(toDate.getValue())+" 23:59:59"+"' order by convert(int,a.MrNo)  ";
			}
			
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

					if(string.equalsIgnoreCase("cash"))
					{	
						table.addItem(new Object[] { element[0], element[1], dateFormat.format(element[2]), element[3], element[4], element[5] }, new Integer(i));
						
						lblMrNo.get(i).setValue(element[1]);
					}
					else if(string.equalsIgnoreCase("cheque"))
					{
						table.addItem(new Object[] { element[0], element[1], dateFormat.format(element[2]), element[3], element[4], element[5] }, new Integer(i));
						
						lblMrNo.get(i).setValue(element[1]);
					}


					if(lblMrNo.size()-1==i)
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

	private void tableInitialise()
	{
		for(int i=0;i<20000;i++)
		{
			tableRowAdd(i);
		}
	}
	
	public void tableRowAdd(final int ar)
	{	
		lblMrNo.add(ar,new Label());
		lblMrNo.get(ar).setWidth("100%");
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

		if(string.equalsIgnoreCase("cash"))
		{
			table.addContainerProperty("MR No", Label.class, new Label());
			table.setColumnWidth("MR No",50);
			table.addContainerProperty("Voucher No", String.class, null);
			table.setColumnWidth("Voucher No",100);
			table.addContainerProperty("Date", Label.class, new Label());
			table.setColumnWidth("Date",100);
			table.addContainerProperty("Cost Center", Label.class, new Label());
			table.setColumnWidth("Cost Center",100);
			table.addContainerProperty("Account Head", Label.class, new Label());
			table.setColumnWidth("Account Head",170);
			table.addContainerProperty("Received By", Label.class, new Label());
			table.setColumnWidth("Received By",100);
		}
		else if(string.equalsIgnoreCase("cheque"))
		{
			table.addContainerProperty("MR No", Label.class, new Label());
			table.setColumnWidth("MR No",50);
			table.addContainerProperty("Voucher No", String.class, null);
			table.setColumnWidth("Voucher No",100);
			table.addContainerProperty("Cheque Date", Label.class, new Label());
			table.setColumnWidth("Cheque Date",100);
			table.addContainerProperty("Cheque No", Label.class, new Label());
			table.setColumnWidth("Cheque No",120);
			table.addContainerProperty("Account Head", Label.class, new Label());
			table.setColumnWidth("Account Head",130);
			table.addContainerProperty("Deposit Bank", Label.class, new Label());
			table.setColumnWidth("Deposit Bank",120);
		}
	}
}
