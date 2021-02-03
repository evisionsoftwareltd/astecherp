package acc.appform.asset;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
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
public class SearchAssetSales extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "Find", "", "", "", "");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private GridLayout topGrid = new GridLayout(1,1);
	private DateField fromDate = new DateField();
	private DateField toDate = new DateField();
	private Table table = new Table();

	private int tr = 3000;
	private Label vNo[] = new Label[tr];
	private Label vNoDep[] = new Label[tr];
	private Label vDate[] = new Label[tr];
	private Label ledger[] = new Label[tr];
	private Label narration[] = new Label[tr];
	private Label amt[] = new Label[tr];
	private Label duDate[] = new Label[tr];
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
	private TextField voucherNo;
	private TextField voucherNoDep;
	private TextField voucherDate;
	private Formatter fmt = new Formatter();

	public SearchAssetSales(SessionBean sessionBean,TextField voucherNo,TextField voucherNoDep,TextField voucherDate)
	{
		this.voucherNo = voucherNo;
		this.voucherNoDep=voucherNoDep;
		this.voucherDate = voucherDate;
		this.sessionBean = sessionBean;
		this.setCaption("ASSET SALES SEARCH :: "+this.sessionBean.getCompany());
		this.setWidth("700px");
		this.setResizable(false);
		table.setWidth("650px");
		table.setHeight("255px");

		table.addContainerProperty("Voucher No[Sales]", Label.class, new Label());
		table.setColumnWidth("Voucher No[Sales]", 65);
		table.addContainerProperty("Voucher No[Dep]", Label.class, new Label());
		table.setColumnWidth("Voucher No[Dep]", 65);
		table.addContainerProperty("Sales Date", Label.class, new Label());
		table.setColumnWidth("Sales Date", 50);
		table.addContainerProperty("Asset Name", Label.class, new Label());
		table.setColumnWidth("Asset Name", 150);
		table.addContainerProperty("Narration", Label.class, new Label());
		table.setColumnWidth("Narration", 220);
		table.addContainerProperty("Sales Amount", Label.class, new Label());
		table.setColumnWidth("Sales Amount", 75);

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
		horLayout.addComponent(button.btnFind);

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(chkDate())
					findBtnAction(event);
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
		setStyleName("cwindow");

		fromDate.focus();
		Component ob[] = {fromDate,toDate,button.btnFind};
		new FocusMoveByEnter(this,ob);
	}

	@SuppressWarnings("deprecation")
	private boolean chkDate()
	{
		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dateFormatter.format(fromDate.getValue())+"','"+dateFormatter.format(toDate.getValue())+"')").list().iterator().next().toString();
			System.out.println(f);
			if (f.equals("1"))
			{
				return true;
			}
			else
			{
				this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else
		{
			this.getParent().showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}

	private void tabRowSelect(ItemClickEvent event)
	{
		if (event.isDoubleClick())
		{
			//this.getParent().showNotification("Selected: " + vNo[Integer.valueOf(event.getItemId()+"")]);
			voucherNo.setValue(vNo[Integer.valueOf(event.getItemId()+"")]);
			voucherNoDep.setValue(vNoDep[Integer.valueOf(event.getItemId()+"")]);
			voucherDate.setValue((duDate[Integer.valueOf(event.getItemId()+"")]));
			this.close();
		}
	}

	private void findBtnAction(ClickEvent e)
	{
		String sql = "SELECT Voucher_No,Date,a.AssetId,l.Ledger_Name,Narration,SalesAmount,voucherNoDep FROM tbAssetSales AS a"
				+ " INNER JOIN tbLedger as l on a.AssetId = l.Ledger_Id WHERE Date >="
				+ " '"+dateFormatter.format(fromDate.getValue())+"' AND Date <="
				+ " '"+dateFormatter.format(toDate.getValue())+"' AND a.CompanyId = '"+ sessionBean.getCompanyId() +"'";
		table.removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> led = session.createSQLQuery(sql).list();
			int i = 0;
			for(Iterator<?> iter = led.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				vNo[i] = new Label();
				vNoDep[i]=new Label();
				vDate[i] = new Label();
				ledger[i] = new Label();
				narration[i] = new Label();
				amt[i] = new Label();
				amt[i].setStyleName("fright");
				duDate[i] = new Label();

				vNo[i].setValue(element[0].toString());
				vNoDep[i].setValue(element[6].toString());
				vDate[i].setValue(sdf.format(element[1]));
				duDate[i].setValue(dateFormatter.format(element[1]));
				ledger[i].setValue(element[3].toString());
				narration[i].setValue(element[4].toString());
				fmt = new Formatter();
				fmt.format("%.2f",Double.valueOf(element[5].toString()));
				amt[i].setValue(fmt);

				table.addItem(new Object[]{vNo[i],vNoDep[i],vDate[i],ledger[i],narration[i],amt[i]},i); 
				i++;
			}

		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
