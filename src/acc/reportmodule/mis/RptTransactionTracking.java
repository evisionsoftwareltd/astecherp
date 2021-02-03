package acc.reportmodule.mis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Window.Notification;

public class RptTransactionTracking extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblVoucher;
	private ComboBox cmbVoucherType;

	private Label lblUser;
	private ComboBox cmbUserName;

	private Label lblDate;
	private PopupDateField dDate;

	private Label lblToDate;
	private PopupDateField tDate;

	private Label lblInvoice;
	private ComboBox cmbInvoiceNo ;
	private CheckBox chkInvoiceNo;

	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtfDMY = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	ArrayList<Component> allComp = new ArrayList<Component>();

	public RptTransactionTracking(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		buildMainLayout();
		setContent(mainLayout);

		//cmbVoucherType.setWidth(lcw);
		cmbVoucherType.setImmediate(true);
		cmbVoucherType.addItem("dca");
		cmbVoucherType.setItemCaption("dca", "Cash Payment Voucher");
		cmbVoucherType.addItem("cca','cci");
		cmbVoucherType.setItemCaption("cca','cci", "Cash Receipt Voucher");
		cmbVoucherType.addItem("dba");
		cmbVoucherType.setItemCaption("dba", "Bank Payment Voucher");
		cmbVoucherType.addItem("cba','cbi");
		cmbVoucherType.setItemCaption("cba','cbi", "Bank Receipt Voucher");
		cmbVoucherType.addItem("jau','jcv");
		cmbVoucherType.setItemCaption("jau','jcv", "Journal Voucher");
		cmbVoucherType.addItem("pao");
		cmbVoucherType.setItemCaption("pao", "FixedAsset Voucher");
		cmbVoucherType.addItem("daj");
		cmbVoucherType.setItemCaption("daj", "Depriciation Voucher");

		cmbVoucherType.setValue(null);
		cmbVoucherType.setNullSelectionAllowed(true);

		init();

		addUserName();

		focusMove();

		actionEvent();
	}

	public void actionEvent()
	{
		button.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		chkInvoiceNo.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkInvoiceNo.booleanValue()==true)
				{
					cmbInvoiceNo.setValue(null);
					cmbInvoiceNo.setEnabled(false);
				}
				else
				{
					cmbInvoiceNo.setEnabled(true);
				}
			}
		});

		cmbVoucherType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbVoucherType.getValue()!=null)
				{	
					selectVoucherNo();
				}
			}
		});

		cmbUserName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbVoucherType.getValue()!=null)
				{	
					selectVoucherNo();
				}
			}
		});
	}

	private void selectVoucherNo()
	{
		String sql = "";
		String userName = "";

		if(cmbUserName.getValue()!=null)
		{
			userName = cmbUserName.getValue().toString();
		}
		else
		{
			userName = "%";
		}

		try
		{
			cmbInvoiceNo.removeAllItems();

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(tDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequeDetails"+fsl;

			if(cmbVoucherType.getValue().toString().equalsIgnoreCase("cba"))
				sql = "SELECT distinct(voucher_No),cast(substring(voucher_No,7,50) as int) FROM VwChequeRegister WHERE vouchertype = 'cba' "+
						" AND date >= '"+dtfYMD.format(dDate.getValue())+"' AND date <= '"+dtfYMD.format(tDate.getValue())+
						" ' AND mrNo = 0 AND clearanceDate is null and companyId in ('0', '"+ sessionBean.getCompanyId() +"')" +
						" and userId like '"+userName+"' order by cast(substring(voucher_No,7,50) as int)";
			else
				sql = "SELECT distinct(voucher_No),CAST(substring(voucher_No,7,50) AS int) FROM "+voucher+" "+
						" WHERE date >= '"+dtfYMD.format(dDate.getValue())+"' AND date <= '"+dtfYMD.format(tDate.getValue())+
						" 'AND vouchertype in ('"+cmbVoucherType.getValue()+"') and companyId in" +
						" ('0', '"+ sessionBean.getCompanyId() +"') and userId like '"+userName+"' " +
						" ORDER BY CAST(substring(voucher_No,7,50) AS int)";

			//System.out.println(sql);
			Iterator iter = session.createSQLQuery(sql).list().iterator();
			int i = 0;
			for(i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();

				cmbInvoiceNo.addItem(element[0]);
				cmbInvoiceNo.setItemCaption(element[0], element[0].toString());
			}
			if(i==0)
				showNotification("In given criteria there is no voucher.", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			getParent().showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void formValidation()
	{
		if(cmbVoucherType.getValue()!=null)
		{
			if(cmbUserName.getValue()!=null)
			{
				if(cmbInvoiceNo.getValue()!=null || chkInvoiceNo.booleanValue() == true)
				{
					if(chkDate())
					{
						reportPreview();
					}
				}
				else
				{
					showNotification("Warning!","Select InvoiceNo",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning!","Select UserName",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning!","Select Voucher Type",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public void addUserName()
	{
		cmbUserName.removeAllItems();
		Transaction tx = null;
		String sql = "";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select userId,name from tbLogin where name != 'Admin' ";
			List list = session.createSQLQuery(sql).list();
			System.out.println(sql);

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUserName.addItem(element[0].toString());
				cmbUserName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void reportPreview()
	{
		String All = "";
		String getAll = "";
		String userName = "";
		String VoucherNo = "";

		if(chkInvoiceNo.booleanValue()==true)
		{
			All = "%";
			getAll = "All";
		}
		else
		{
			All =cmbInvoiceNo.getValue().toString();
			getAll = cmbInvoiceNo.getValue().toString();
		}

		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		try
		{
			HashMap hm = new HashMap();
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(tDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			tx.commit();
			hm.put("dDate",dDate.getValue());
			hm.put("tDate",tDate.getValue());
			hm.put("VoucherType",cmbVoucherType.getItemCaption(cmbVoucherType.getValue()));
			hm.put("company", sessionBean.getCompany());
			hm.put("printDateTime", getDatetime());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone",sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			//hm.put("receiptAll", getAll);
			hm.put("UserName", cmbUserName.getItemCaption(cmbUserName.getValue().toString()));

			query = " select vv.Voucher_No,vv.Date,vv.entryTime,lg.Ledger_Name,vv.Narration,(vv.DrAmount+vv.CrAmount) Amount, " +
					" case when vv.DrAmount>0 then 'Dr' when vv.CrAmount>0 then 'Cr' end Type,vv.TransactionWith,ct.costCentreName," +
					" vv.userIp from vwVoucher as vv inner join tbLedger as lg on vv.Ledger_Id =lg.Ledger_Id inner join " +
					" tbCostCentre as ct on vv.costId=ct.id WHERE date >= '"+dtfYMD.format(dDate.getValue())+"' AND date <= '"+dtfYMD.format(tDate.getValue())+"' " +
					" AND vouchertype in ('"+cmbVoucherType.getValue().toString()+"') and vv.userId like '"+cmbUserName.getValue().toString()+"' and Voucher_No like '"+All+"' order by date ";

			System.out.println("Query="+query);

			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/mis/RptTransactionTracking.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning!","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		catch(Exception exp)
		{
			this.getParent().showNotification("Error",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private Date getDatetime()
	{
		Date dateTime = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			String query="select CURRENT_TIMESTAMP";
			Iterator iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				dateTime= (Date) iter.next();
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error3",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
		return dateTime;
	}

	private boolean chkDate()
	{
		boolean get;
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(dDate.getValue())+"','"+dtfYMD.format(tDate.getValue())+"')").list().iterator().next().toString();
		if (f.equals("1"))
		{
			get = true;
		}
		else if (f.equals("0"))
		{
			this.getParent().showNotification("","Working fiscal year not found.",Notification.TYPE_WARNING_MESSAGE);
			get = false;
		}
		else
		{
			this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
			get = false;
		}
		return get;
	}

	public void init()
	{
		this.setCaption("TRANSACTION TRACKING :: "+sessionBean.getCompany());
		this.setWidth("400px");
		this.setHeight("280px");
		this.setResizable(false);
	}

	private void focusMove()
	{
		allComp.add(cmbUserName);
		allComp.add(cmbInvoiceNo);
		allComp.add(button.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("380px");
		mainLayout.setHeight("230px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("480px");
		setHeight("250px");

		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("From :");
		mainLayout.addComponent(lblDate, "top:30.0px;left:72.0px;");

		// dDate
		dDate = new PopupDateField();
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setInvalidAllowed(false);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setWidth("110px");
		mainLayout.addComponent(dDate, "top:28.0px;left:115.0px;");

		// lblToDate
		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To :");
		mainLayout.addComponent(lblToDate, "top:30.0px;left:230.0px;");

		// tDate
		tDate = new PopupDateField();
		tDate.setResolution(PopupDateField.RESOLUTION_DAY);
		tDate.setInvalidAllowed(false);
		tDate.setDateFormat("dd-MM-yyyy");
		tDate.setValue(new java.util.Date());
		tDate.setWidth("110px");
		mainLayout.addComponent(tDate, "top:28.0px;left:255.0px;");

		// lblVoucherType
		lblVoucher = new Label("Voucher Type : ");
		lblVoucher.setImmediate(true);
		lblVoucher.setWidth("-1px");
		lblVoucher.setHeight("-1px");
		mainLayout.addComponent(lblVoucher, "top:65.0px; left:30.0px;");

		//cmbVoucherType
		cmbVoucherType = new ComboBox();
		cmbVoucherType.setImmediate(true);
		cmbVoucherType.setWidth("220px");
		cmbVoucherType.setHeight("24px");
		cmbVoucherType.setNullSelectionAllowed(true);
		cmbVoucherType.setNewItemsAllowed(true);
		mainLayout.addComponent( cmbVoucherType, "top:63.0px;left:115.0px;");

		// lblUser
		lblUser = new Label("User Name : ");
		lblUser.setImmediate(true);
		lblUser.setWidth("-1px");
		lblUser.setHeight("-1px");
		mainLayout.addComponent(lblUser, "top:100.0px; left:36.0px;");

		//cmbUserName
		cmbUserName = new ComboBox();
		cmbUserName.setImmediate(true);
		cmbUserName.setWidth("220px");
		cmbUserName.setHeight("24px");
		cmbUserName.setNullSelectionAllowed(true);
		cmbUserName.setNewItemsAllowed(true);
		mainLayout.addComponent( cmbUserName, "top:98.0px;left:115.0px;");

		//lblInvoice
		lblInvoice = new Label("Invoice No :");
		lblInvoice.setImmediate(false);
		lblInvoice.setWidth("-1px");
		lblInvoice.setHeight("-1px");
		mainLayout.addComponent(lblInvoice, "top:135.0px;left:42.0px;");

		//cmbInvoiceNo
		cmbInvoiceNo = new ComboBox();
		cmbInvoiceNo.setImmediate(true);
		cmbInvoiceNo.setWidth("220px");
		cmbInvoiceNo.setHeight("24px");
		cmbInvoiceNo.setNullSelectionAllowed(true);
		cmbInvoiceNo.setNewItemsAllowed(true);
		mainLayout.addComponent( cmbInvoiceNo, "top:133.0px;left:115.0px;");

		// chkInvoiceNo
		chkInvoiceNo = new CheckBox("All");
		chkInvoiceNo.setImmediate(true);
		chkInvoiceNo.setWidth("-1px");
		chkInvoiceNo.setHeight("24px");
		//chkInvoiceNo.setVisible(false);
		mainLayout.addComponent(chkInvoiceNo, "top:135.0px;left:335.0px;");

		mainLayout.addComponent(button,"top:200.opx; left:110.0px");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:170.0px;left:150.0px;");

		return mainLayout;
	}
}
