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

@SuppressWarnings("serial")
public class JVFind extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "Find", "", "", "", "");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private GridLayout topGrid = new GridLayout(1,1);
	private DateField fromDate = new DateField();
	private DateField toDate = new DateField();
	private Table table = new Table();
	//private Button findBtn = new Button("Find");

	private int tr = 3000;
	private Label vNo[] = new Label[tr];
	private Label vDate[] = new Label[tr];
	private Label narration[] = new Label[tr];
	private Label duDate[] = new Label[tr];
	private String sqlE;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
	private TextField voucherNo;
	public TextField voucherDate;
	public TextField voucherType,txtVoucherNo;

	public JVFind(SessionBean sessionBean,String sqlF,String sqlE,TextField voucherNo,TextField voucherDate,TextField voucherType)
	{
		this.voucherNo = voucherNo;
		this.sessionBean = sessionBean;
		this.voucherDate = voucherDate;
		this.voucherType = voucherType;
		this.setCaption("VOUCHER SEARCH :: "+this.sessionBean.getCompany());
		this.setWidth("700px");
		this.setResizable(false);
		this.sqlE = sqlE;
		table.setWidth("650px");
		table.setHeight("255px");
		table.addContainerProperty("Voucher No", Label.class, new Label());
		table.setColumnWidth("Voucher No", 75);
		table.addContainerProperty("V. Date", Label.class, new Label());
		table.setColumnWidth("V. Date", 60);
		table.addContainerProperty("Narration", Label.class, new Label());
		table.setColumnWidth("Narration", 300);
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
		ComponentContainer ob[] = {horLayout};
		new FocusMoveByEnter(this,ob);        
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
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dateFormatter.format(fromDate.getValue())+"','"+dateFormatter.format(toDate.getValue())+"')").list().iterator().next().toString();

			if (f.equals("1"))
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
			voucherDate.setValue((duDate[Integer.valueOf(event.getItemId()+"")]));
			this.close();
		}
	}

	@SuppressWarnings("deprecation")
	private void findBtnAction()
	{
		String sqlF = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormatter.format(toDate.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;
		String voucherNo = "%"+(txtVoucherNo.getValue().toString().isEmpty()?"":txtVoucherNo.getValue().toString())+"%";
		if (voucherType.getValue().toString().equals("jau"))
		{
			/*sqlF = "SELECT DISTINCT(Voucher_No),Date,Narration FROM "+voucher+" WHERE vouchertype = 'jau' AND companyId = '"+ sessionBean.getCompanyId() +"' AND  Voucher_No not in (select VoucherNo from tbRawPurchaseInfo where CONVERT(date,Date,105)  AND"
                  +"between '"+dateFormatter.format(fromDate.getValue())+"' and '"+dateFormatter.format(toDate.getValue())+"' and VoucherNo!='' "
                  + " union "
                  +"select voucherNo from tbLcChargeInfo where CONVERT(date,Date,105) "
                  +"between '"+dateFormatter.format(fromDate.getValue())+"' and '"+dateFormatter.format(toDate.getValue())+"' and VoucherNo!='' "
                  +"union "
                  +"select voucherNo from tbRawIssueInfo where CONVERT(date,Date,105) "
                 +" between '"+dateFormatter.format(fromDate.getValue())+"' and '"+dateFormatter.format(toDate.getValue())+"' and VoucherNo!='') AND  ";
			*/
			
			sqlF = "SELECT DISTINCT(Voucher_No),Date,Narration FROM "+voucher+" WHERE vouchertype = 'jau' AND companyId = '"+ sessionBean.getCompanyId() +"' AND  Voucher_No not in( select Voucher_No from funjvfromInventory('"+dateFormatter.format(fromDate.getValue())+"','"+dateFormatter.format(toDate.getValue())+"')) AND ";
		
		}
		else if (voucherType.getValue().toString().equals("jcv"))
		{
			
			sqlF = "SELECT DISTINCT(Voucher_No),Date,Narration FROM "+voucher+" WHERE vouchertype = 'jcv' AND companyId = '"+ sessionBean.getCompanyId() +"' AND  Voucher_No not in( select Voucher_No from funjvfromInventory('"+dateFormatter.format(fromDate.getValue())+"','"+dateFormatter.format(toDate.getValue())+"')) AND ";
	                 /* +"between '"+dateFormatter.format(fromDate.getValue())+"' and '"+dateFormatter.format(toDate.getValue())+"' and VoucherNo!='' "
	                  + " union "
	                  +"select voucherNo from tbLcChargeInfo where CONVERT(date,Date,105) "
	                  +"between '"+dateFormatter.format(fromDate.getValue())+"' and '"+dateFormatter.format(toDate.getValue())+"' and VoucherNo!='' "
	                  +"union "
	                  +"select voucherNo from tbRawIssueInfo where CONVERT(date,Date,105) "
	                 +" between '"+dateFormatter.format(fromDate.getValue())+"' and '"+dateFormatter.format(toDate.getValue())+"' and VoucherNo!='') AND  ";
			
			*/
			
		}

		String sql = sqlF+" Date between '"+dateFormatter.format(fromDate.getValue())+"' AND"
				+ " '"+dateFormatter.format(toDate.getValue())+"' AND Voucher_No like '"+voucherNo+"' "+sqlE;
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
				narration[i] = new Label();
				duDate[i] = new Label();

				vNo[i].setValue(element[0].toString());
				vDate[i].setValue(sdf.format(Date.parse(element[1].toString().replace("-", "/").substring(0,10).trim())));
				duDate[i].setValue(dateFormatter.format(Date.parse(element[1].toString().replace("-", "/").substring(0,10).trim())));
				narration[i].setValue(element[2].toString());
				table.addItem(new Object[]{vNo[i],vDate[i],narration[i]},i); 
				i++;
			}
		}
		catch(Exception exp)
		{
			//	showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}