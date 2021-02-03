package acc.appform.transaction;

import java.util.Date;
import java.text.SimpleDateFormat;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import java.util.Formatter;

@SuppressWarnings("serial")
public class VoucherFind extends Window
{
	private CommonButton button = new CommonButton("", "", "", "", "", "Find", "", "", "", "");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private GridLayout topGrid = new GridLayout(1,1);
	private DateField fromDate = new DateField();
	private DateField toDate = new DateField();
	private Table table = new Table();

	private int tr = 3000;
	private Label vNo[] = new Label[tr];
	private Label vDate[] = new Label[tr];
	private Label ledger[] = new Label[tr];
	private Label narration[] = new Label[tr];
	private Label amt[] = new Label[tr];
	private Label duDate[] = new Label[tr];
	private String sqlE;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");	
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
	private TextField voucherNo;
	private TextField voucherDate;
	private TextField strflag,txtVoucherNo;
	private Formatter fmt = new Formatter();

	public VoucherFind(SessionBean sessionBean, String sqlF, String sqlE, TextField voucherNo,
			TextField voucherDate, TextField strflag)
	{
		this.voucherNo = voucherNo;
		this.voucherDate = voucherDate;
		this.strflag = strflag; 
		this.sessionBean = sessionBean;
		this.setCaption("VOUCHER SEARCH :: "+this.sessionBean.getCompany());
		this.setWidth("690px");
		this.setResizable(false);
		this.sqlE = sqlE;
		table.setWidth("650px");
		table.setHeight("255px");
		table.addContainerProperty("Voucher No", Label.class, new Label());
		table.setColumnWidth("Voucher No", 65);
		table.addContainerProperty("V. Date", Label.class, new Label());
		table.setColumnWidth("V. Date", 50);
		table.addContainerProperty("Ledger", Label.class, new Label());
		table.setColumnWidth("Ledger", 150);
		table.addContainerProperty("Narration", Label.class, new Label());
		table.setColumnWidth("Narration", 220);
		table.addContainerProperty("Amount", Label.class, new Label());
		table.setColumnWidth("Amount", 75);

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				tabRowSelect(event);
			}
		});

		horLayout.addComponent(new Label("From Date :"));
		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);
		horLayout.addComponent(fromDate);

		horLayout.addComponent(new Label("To Date :"));
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);
		horLayout.addComponent(toDate);

		txtVoucherNo = new TextField();
		txtVoucherNo.setWidth("100px");
		txtVoucherNo.setHeight("-1px");
		txtVoucherNo.setImmediate(true);
		horLayout.addComponent(new Label("Voucher No :"));
		horLayout.addComponent(txtVoucherNo);

		horLayout.addComponent(button.btnFind);

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(chkDate())
				{
					findBtnAction();
				}
			}
		});

		txtVoucherNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkDate())
				{
					findBtnAction();
				}
			}
		});

		horLayout.setSpacing(true);
		topGrid.addComponent(horLayout);

		HorizontalLayout tabLayout = new HorizontalLayout();
		tabLayout.addComponent(table);
		mainLayout.addComponent(topGrid);
		mainLayout.setComponentAlignment(topGrid, Alignment.TOP_CENTER);

		mainLayout.addComponent(tabLayout);
		this.addComponent(mainLayout);
		ComponentContainer cont[] = {horLayout};
		new FocusMoveByEnter(this,cont);		
		setStyleName("cwindow");
		fromDate.focus();
	}

	@SuppressWarnings("deprecation")
	private boolean chkDate()
	{
		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			if(f.equals("1"))
			{
				return true;
			}
			else
			{
				showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else
		{
			showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}

	private void tabRowSelect(ItemClickEvent event)
	{
		if(event.isDoubleClick())
		{
			voucherNo.setValue(vNo[Integer.valueOf(event.getItemId()+"")]);
			try
			{
				voucherDate.setValue((duDate[Integer.valueOf(event.getItemId()+"")]));
			}
			catch (Exception ex)
			{
				System.out.println(ex);
			}
			this.close();
		}
	}

	@SuppressWarnings("deprecation")
	private void findBtnAction()
	{
		String sqlG = "";
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
		String voucher = "Voucher"+fsl;
		String v = strflag.getValue().toString();
		String voucherNo = "%"+(txtVoucherNo.getValue().toString().isEmpty()?"":txtVoucherNo.getValue().toString())+"%";
		if(strflag.getValue().equals("cashpay"))
		{
			sqlG = "SELECT tbLedger.Ledger_Id, tbLedger.Ledger_Name, vwVoucher.Date, vwVoucher.Voucher_No,"
					+ " vwVoucher.Narration,vwVoucher.CrAmount,vwVoucher.DrAmount,vwVoucher.vouchertype FROM"
					+ " tbLedger INNER JOIN "+voucher+" as vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id"
					+ " WHERE vouchertype = 'dca' AND vwVoucher.CrAmount !=0 AND vwVoucher.companyId ="
					+ " '"+ sessionBean.getCompanyId() +"' AND ";
		}
		else if(v=="bankpay")
		{
			sqlG  = "SELECT tbLedger.Ledger_Id, tbLedger.Ledger_Name, vwVoucher.Date, vwVoucher.Voucher_No,"
					+ " vwVoucher.Narration,vwVoucher.CrAmount,vwVoucher.DrAmount,vwVoucher.vouchertype FROM"
					+ " tbLedger INNER JOIN "+voucher+" as vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id"
					+ " WHERE vouchertype = 'dba' AND vwVoucher.CrAmount !=0 AND vwVoucher.companyId ="
					+ " '"+ sessionBean.getCompanyId() +"' and tbledger.companyId = '"+ sessionBean.getCompanyId() +"' AND ";
		}
		else if(v=="cashReceive")
		{
			sqlG = "SELECT tbLedger.Ledger_Id, tbLedger.Ledger_Name, vwVoucher.Date, vwVoucher.Voucher_No,"
					+ "vwVoucher.Narration,vwVoucher.DrAmount,vwVoucher.CrAmount,vwVoucher.vouchertype FROM"
					+ " tbLedger INNER JOIN "+voucher+" as vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id"
					+ " WHERE vouchertype = 'cca' AND vwVoucher.DrAmount !=0 AND vwVoucher.companyId = "
					+ "'"+ sessionBean.getCompanyId() +"' and tbledger.companyId = '"+ sessionBean.getCompanyId() +"' AND ";
		}
		else if(v=="bankReceive")
		{
			sqlG = "SELECT tbLedger.Ledger_Id, tbLedger.Ledger_Name, vwVoucher.Date, vwVoucher.Voucher_No,"
					+ "vwVoucher.Narration,vwVoucher.DrAmount,vwVoucher.CrAmount,vwVoucher.vouchertype FROM"
					+ " tbLedger INNER JOIN "+voucher+" as vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id"
					+ " WHERE vouchertype = 'cba' AND vwVoucher.DrAmount !=0 AND vwVoucher.companyId ="
					+ " '"+ sessionBean.getCompanyId() +"' and tbledger.companyId = '"+ sessionBean.getCompanyId() +"' AND ";
		}

		String sql = sqlG+" Date between '"+dateFormatter.format(fromDate.getValue())+"' AND"
				+ " '"+dateFormatter.format(toDate.getValue())+"' AND vwVoucher.Voucher_No like '"+voucherNo+"' "+sqlE;
		table.removeAllItems();
		try
		{
			List<?> led = session.createSQLQuery(sql).list();
			int i = 0;
			for(Iterator<?> iter = led.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				vNo[i] = new Label();
				vDate[i] = new Label();
				ledger[i] = new Label();
				narration[i] = new Label();
				amt[i] = new Label();
				amt[i].setStyleName("fright");
				duDate[i] = new Label();

				vNo[i].setValue(element[3].toString());
				vDate[i].setValue(sdf.format(Date.parse(element[2].toString().replace("-", "/").substring(0,10).trim())));
				duDate[i].setValue(dateFormatter.format(Date.parse(element[2].toString().replace("-", "/").substring(0,10).trim())));
				ledger[i].setValue(element[1].toString());
				narration[i].setValue(element[4].toString());
				fmt = new Formatter();
				fmt.format("%.2f",Double.valueOf(element[5].toString()));
				amt[i].setValue(fmt);
				table.addItem(new Object[]{vNo[i],vDate[i],ledger[i],narration[i],amt[i]},i); 
				i++;
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}