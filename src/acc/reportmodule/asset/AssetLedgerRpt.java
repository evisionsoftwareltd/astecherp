package acc.reportmodule.asset;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportPdf;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;

public class AssetLedgerRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout h1Layout = new HorizontalLayout();
	private HorizontalLayout h2Layout = new HorizontalLayout();
	private HorizontalLayout hMainLayout = new HorizontalLayout();
	private FormLayout formLayout = new FormLayout();
	private FormLayout formButton = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	private DateField fromDate = new DateField("From Date:");
	private DateField toDate = new DateField("To Date:");
	private ComboBox costCentre = new ComboBox("Cost Centre:");
	private ComboBox ledgerList = new ComboBox("Ledger List:");
	private CheckBox withoutChk = new CheckBox("Without Narration:");

	private String lcw = "130px";
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateRpt = new SimpleDateFormat("dd-MM-yy");
	private String rpt = "";
	
	private CheckBox chkAll = new CheckBox("All");
	private Label lblHeight = new Label();

	public AssetLedgerRpt(SessionBean sessionBean)
	{
		
		this.sessionBean = sessionBean;
		this.setWidth("320px");
		this.setResizable(false);

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);

		
			this.setCaption("ASSET LEDGER :: "+this.sessionBean.getCompany());

		this.setWidth("480px");
		formLayout.addComponent(costCentre);
		costCentre.setWidth("240px");
		costCentre.setImmediate(true);
		formLayout.addComponent(ledgerList);
		ledgerList.setImmediate(true);
		ledgerList.setWidth("240px");
		ledgerList.setImmediate(true);
		ledgerInitialise();

		formLayout.addComponent(withoutChk);
		withoutChk.setImmediate(true);

		fromDate.setWidth(lcw);
		fromDate.setValue(sessionBean.getFiscalOpenDate());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);

		toDate.setWidth(lcw);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);

		btnL.setSpacing(true);
		btnL.addComponent(button);
		
		chkAll.setImmediate(true);
		chkAll.setValue(true);
		costCentre.setEnabled(false);
		
		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkAll.getValue().equals(true))
				{
					costCentre.setEnabled(false);
					costCentre.setValue(null);
				}
				else
				{
					costCentre.setEnabled(true);
				}
			}
		});
		
		lblHeight.setHeight("55px");
		formButton.addComponent(lblHeight);
		formButton.addComponent(chkAll);

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(btnL);
		h1Layout.addComponent(formLayout);
		h2Layout.addComponent(formButton);
		
		hMainLayout.addComponent(h1Layout);
		hMainLayout.addComponent(h2Layout);
		mainLayout.addComponent(hMainLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		Component comp[] = {fromDate, toDate, costCentre, ledgerList, button.btnPreview};
		buttonActionAdd();
		costCenterInitialise();
		new FocusMoveByEnter(this, comp);
		fromDate.focus();
	}

	private void ledgerInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String sql = "";
			sql = "SELECT ledger_Id,ledger_Name FROM TbLedger WHERE SUBSTRING(create_From,1,ABS(CHARINDEX('G', Create_From) - 2)) = 'A1' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name";
			Iterator iter = session.createSQLQuery(sql).list().iterator();

			for(int i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				ledgerList.addItem(element[0]);
				ledgerList.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				preBtnAction();
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void preBtnAction()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dateFormatter.format(fromDate.getValue())+"','"+dateFormatter.format(toDate.getValue())+"')").list().iterator().next().toString();
		System.out.println(f);
		if (f.equals("1"))	
		{
			//		if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
			//				&&
			//				(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			//		{
			if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())))
			{
				if(ledgerList.getValue()!=null){
					String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormatter.format(toDate.getValue())+"')").list().iterator().next().toString();
					String voucher =  "voucher"+fsl;

					session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
					//session.close();
					tx.commit();

					showReport();
				}
				else
					showNotification("","Please provide the ledger name.",Notification.TYPE_WARNING_MESSAGE);
			}
			else
			{
				showNotification("","From date can not be greater than to date.",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		else
		{
			tx.commit();
			this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);

		}
			
			
			
			//		}
//		else
//		{
//			showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
//		}	
	}

	private void showReport()
	{
		try
		{
			HashMap hm = new HashMap();

			hm.put("fromTo", dateRpt.format(fromDate.getValue())+" To "+dateRpt.format(toDate.getValue()));
			hm.put("fromDate", dateFormatter.format(fromDate.getValue()));
			hm.put("toDate", dateFormatter.format(toDate.getValue()));

			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("comName", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax", sessionBean.getCompanyContact());
			
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
			
			sessionBean.setFromDate(fromDate.getValue());
			sessionBean.setAsOnDate(toDate.getValue());

			hm.put("url", this.getWindow().getApplication().getURL()+"");
			
			Object b=this.getWindow().getApplication().getContext().getBaseDirectory();
			
			sessionBean.setUrl(getWindow().getApplication().getURL());
			
			sessionBean.setP(b);
			
			
			if(costCentre.getValue()==null)
			{
				//hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costCentre","All");
				hm.put("costId","%");
			}
			else
			{
				hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costId",costCentre.getValue()+"");
			}
			
		//	System.out.println(costCentre.getValue());

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String msg = "";

			Iterator iter = session.createQuery("SELECT substring(id.r,1,1),id.h+isnull('\\'+id.g,'')+isnull('\\'+id.s,'')+'\\'+id.l FROM VwLedgerList WHERE id.ledgerId = '"+ledgerList.getValue()+"'").list().iterator();
			Object[] element = (Object[]) iter.next();
			if(element[0].toString().equalsIgnoreCase("A"))
				msg = "Assets\\"+element[1];
			else if(element[0].toString().equalsIgnoreCase("I"))
				msg = "Income\\"+element[1];
			else if(element[0].toString().equalsIgnoreCase("E"))
				msg = "Expenses\\"+element[1];
			else 
				msg = "Liabilities\\"+element[1];
			hm.put("ledgerPath", msg);
			hm.put("userName",sessionBean.getUserName());
			hm.put("userIp",sessionBean.getUserIp());

			//General Ledger
/*			if(rpt.equalsIgnoreCase("g")||rpt.equalsIgnoreCase("d")||rpt.equalsIgnoreCase("c"))
			{*/
				hm.put("ledgerName", ledgerList.getItemCaption(ledgerList.getValue()).toString());
				hm.put("ledgerId", ledgerList.getValue().toString());
				
				hm.put("withNarration", !new Boolean(withoutChk.getValue().toString()));
				
				String costId = "";
				if(chkAll.getValue().equals(true))
				{
					costId = "%";
				}
				else
				{
					costId = costCentre.getValue().toString();
				}
				
				///////////
				String sql="SELECT * FROM dbo.rptCostLedger( " +
						" '"+new SimpleDateFormat("yyyy-MM-dd").format(fromDate.getValue())+"', " +
						" '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"', " +
						" '"+ledgerList.getValue().toString()+"', " +
						" '"+costId+"','"+sessionBean.getCompanyId()+"')  " +
						" order by date,convert(Numeric,autoid)";
				hm.put("sql",sql);
				System.out.println(sql);
				

			//	Window win = new ReportPdf(hm,"report/account/book/GeneralLedger.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				
		//		Window win = new ReportViewer(hm,"report/account/book/GeneralLedger(backnavigation).jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,getWindow().getApplication().getURL()+"VAADIN/applet",true);
				
				Window	win = new ReportViewer(hm,"report/account/book/GeneralLedger(backnavigation).jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
				
				
	//	Window	win=new ReportViewer(hm,"report/account/book/GeneralLedger(backnavigation).jasper",
//						sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp";
//						sessionBean.getUrl()+"VAADIN/rpttmp",false;
//						sessionBean.getUrl()+"VAADIN/applet",true;
//				
				this.getParent().getWindow().addWindow(win);
				win.setStyleName("cwindow");
				win.setCaption("GENERAL LEDGER :: "+sessionBean.getCompany());
/*			}
			else
			{
				showNotification("Error ","Internal error. Please contact your software vendor.",Notification.TYPE_ERROR_MESSAGE);
			}*/
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void costCenterInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery("SELECT id,costCentreName FROM tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' ORDER BY costCentreName").list().iterator();
			
			for(int i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				costCentre.addItem(element[0]);
				costCentre.setItemCaption(element[0], element[1].toString());
			}
			costCentre.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
