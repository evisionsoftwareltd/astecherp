package acc.reportmodule.voucher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class UnusedChequeList extends Window 
{
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout;

	private Label lblBankAccount;
	private Label lblChequeNo;

	private ComboBox cmbBankAccount;
	private ComboBox cmbChequeNo;

	private CheckBox chBankAccount;
	private CheckBox chChequeNo;

	private NativeButton btnPreview = new NativeButton("Preview");
	private NativeButton btnExit = new NativeButton("Exit");

	private HorizontalLayout btnLayout = new HorizontalLayout();

	public UnusedChequeList(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("UNUSED CHEQUE LIST :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		btnAction();

		bankHeadIni();
	}

	private void btnAction()
	{
		btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewBtnAction();
			}
		});

		btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbBankAccount.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbBankAccount.getValue()!=null)
				{
					chBankAccount.setValue(false);

					addChequeNo();
				}
				else
				{
					cmbChequeNo.removeAllItems();
				}
			}
		});

		cmbChequeNo.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbChequeNo.getValue()!=null)
				{
					chChequeNo.setValue(false);
				}
			}
		});

		chBankAccount.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(event.getProperty().toString().equalsIgnoreCase("true"))
				{
					cmbBankAccount.setValue(null);
					cmbBankAccount.setEnabled(false);
				}
				else
				{
					cmbBankAccount.setEnabled(true);
				}
			}
		});

		chChequeNo.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(event.getProperty().toString().equalsIgnoreCase("true"))
				{
					cmbChequeNo.setValue(null);
					cmbChequeNo.setEnabled(false);
				}
				else
				{
					cmbChequeNo.setEnabled(true);
				}
			}
		});
	}

	private void previewBtnAction()
	{
		if(cmbBankAccount.getValue()!=null || !chBankAccount.getValue().toString().equalsIgnoreCase("false"))
		{
			if(cmbChequeNo.getValue()!=null || !chChequeNo.getValue().toString().equalsIgnoreCase("false"))
			{
				showReport();
			}
			else
			{
				showNotification("Warning","There are no data for Preview",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning","There are no data for Preview",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void showReport()
	{
		try
		{
			HashMap hm = new HashMap();
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());

			hm.put("logo", sessionBean.getCompanyLogo());

			String link = getApplication().getURL().toString();

			if(link.endsWith("RSRM/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("RSRM/", ""));
			}
			else if(link.endsWith("MSML/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("MSML/", ""));
			}
			else if(link.endsWith("RJSL/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("RJSL/", ""));
			}
			else if(link.endsWith("UNIGLOBAL/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("UNIGLOBAL/", ""));
			}

			String bankId = "";
			if(cmbBankAccount.getValue()!=null)
			{
				bankId = cmbBankAccount.getValue().toString().replaceAll("#", "");
			}
			else if(cmbBankAccount.getValue()==null && chBankAccount.getValue().toString().equalsIgnoreCase("true"))
			{
				bankId = "%";
			}

			String chequeNo = "";
			if(cmbChequeNo.getValue()!=null)
			{
				chequeNo = cmbChequeNo.getValue().toString().replaceAll("#", "");
			}
			else if(cmbChequeNo.getValue()==null && chChequeNo.getValue().toString().equalsIgnoreCase("true"))
			{
				chequeNo = "%";
			}

			String sql = "Select * from vwChequeBookEntry where ledgerId like '"+bankId+"' " +
					" and folioNo like '"+chequeNo+"' and (status = 'NO' or  status = 'DELETE' ) " +
					" order by Ledger_Name,CONVERT(money,bookNo),CONVERT(money,folioNo) ";

			System.out.println(sql);
			hm.put("sql",sql);
			Window win = new ReportViewer(hm,"report/account/voucher/UnusedChequeList .jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

			win.setStyleName("cwindow");
			getParent().getWindow().addWindow(win);
			win.setCaption("UNUSED CHEQUE LIST :: "+sessionBean.getCompany());
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void addChequeNo()
	{
		cmbChequeNo.removeAllItems();

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = " Select * from tbChequeBook where ledgerId = '"+cmbBankAccount.getValue().toString().replaceAll("#", "")+"' and status = 'NO' ";

			System.out.println(sql);

			List bh = session.createSQLQuery(sql).list();

			for (Iterator iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbChequeNo.addItem(element[5].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void bankHeadIni()
	{
		cmbBankAccount.removeAllItems();
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List bh = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A8') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();

			for (Iterator iter = bh.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbBankAccount.addItem(element[0]+"#");
				cmbBankAccount.setItemCaption(element[0]+"#", element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("620px");
		setHeight("180px");

		// lblBankAccount
		lblBankAccount = new Label();
		lblBankAccount.setImmediate(true);
		lblBankAccount.setWidth("-1px");
		lblBankAccount.setHeight("-1px");
		lblBankAccount.setValue("Bank AC Name :");
		mainLayout.addComponent(lblBankAccount, "top:22.0px;left:100.0px;");

		// cmbBankAccount
		cmbBankAccount = new ComboBox();
		cmbBankAccount.setImmediate(true);
		cmbBankAccount.setWidth("-1px");
		cmbBankAccount.setHeight("-1px");
		cmbBankAccount.setWidth("270px");
		cmbBankAccount.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbBankAccount, "top:22.0px;left:210.0px;");

		// chBankAccount
		chBankAccount = new CheckBox("ALL");
		chBankAccount.setImmediate(true);
		chBankAccount.setWidth("-1px");
		chBankAccount.setHeight("-1px");
		mainLayout.addComponent(chBankAccount, "top:22.0px;left:485.0px;");

		// lblBankAccount
		lblChequeNo = new Label();
		lblChequeNo.setImmediate(true);
		lblChequeNo.setWidth("-1px");
		lblChequeNo.setHeight("-1px");
		lblChequeNo.setValue("Cheque No :");
		mainLayout.addComponent(lblChequeNo, "top:47.0px;left:100.0px;");

		// cmbChequeNo
		cmbChequeNo = new ComboBox();
		cmbChequeNo.setImmediate(true);
		cmbChequeNo.setWidth("-1px");
		cmbChequeNo.setHeight("-1px");
		cmbChequeNo.setWidth("220px");
		cmbChequeNo.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbChequeNo, "top:47.0px;left:210.0px;");

		// chChequeNo
		chChequeNo = new CheckBox("ALL");
		chChequeNo.setImmediate(true);
		chChequeNo.setWidth("-1px");
		chChequeNo.setHeight("-1px");
		mainLayout.addComponent(chChequeNo, "top:47.0px;left:485.0px;");

		// btnPreview
		buttonsize(btnPreview);
		btnPreview.setIcon(new ThemeResource("../icons/preview.png"));

		// btnExit
		buttonsize(btnExit);
		btnExit.setIcon(new ThemeResource("../icons/Exit.png"));

		// button
		btnLayout.addComponent(btnPreview);
		btnLayout.addComponent(btnExit);
		mainLayout.addComponent(btnLayout, "top:90.0px;left:210.0px;");

		return mainLayout;
	}

	private void buttonsize(Button btn)
	{
		btn.setWidth("80px");
		btn.setHeight("28px");
	}
}
