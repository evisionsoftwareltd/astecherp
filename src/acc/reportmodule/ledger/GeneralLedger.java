package acc.reportmodule.ledger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.GenerateExcelReport11;
import com.common.share.PreviewOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class GeneralLedger extends Window
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
	private ComboBox ledgerList = new ComboBox("Ledger List:");
	private CheckBox withoutChk = new CheckBox("Without Narration:");

	private String lcw = "130px";
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateRpt = new SimpleDateFormat("dd-MM-yy");
	private String rpt = "";

	private Label lblHeight = new Label();

	private PreviewOption po = new PreviewOption();

	public GeneralLedger(SessionBean sessionBean,String r)
	{
		rpt = r;
		this.sessionBean = sessionBean;
		this.setWidth("320px");
		this.setResizable(false);

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);

		if(r.equalsIgnoreCase("g"))
			this.setCaption("GENERAL LEDGER :: "+this.sessionBean.getCompany());
		else if(r.equalsIgnoreCase("d"))
			this.setCaption("DEBTORS LEDGER :: "+this.sessionBean.getCompany());
		else if(r.equalsIgnoreCase("c"))
			this.setCaption("CREDITORS LEDGER :: "+this.sessionBean.getCompany());

		this.setWidth("480px");

		formLayout.addComponent(ledgerList);
		ledgerList.setImmediate(true);
		ledgerList.setWidth("300px");
		ledgerList.setImmediate(true);
		ledgerList.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
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

		/*chkAll.setImmediate(true);
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
		});*/

		lblHeight.setHeight("55px");
		formButton.addComponent(lblHeight);
		//formButton.addComponent(chkAll);

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(po);
		formLayout.addComponent(btnL);
		h1Layout.addComponent(formLayout);
		h2Layout.addComponent(formButton);

		hMainLayout.addComponent(h1Layout);
		hMainLayout.addComponent(h2Layout);
		mainLayout.addComponent(hMainLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		Component comp[] = {fromDate, toDate, /*costCentre,*/ ledgerList, button.btnPreview};
		buttonActionAdd();
		//costCenterInitialise();
		new FocusMoveByEnter(this, comp);
		fromDate.focus();
	}

	private void ledgerInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String sql = "";
			if(rpt.equals("g"))
				sql = "SELECT ledger_Id,L as ledger_Name FROM vwLedgerList where H <> 'Fixed Asset' and CompanyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name";
			else if(rpt.equals("d"))
				sql = "SELECT ledger_Id,ledger_Name FROM TbLedger WHERE create_From like 'A6%' AND CompanyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name";
			else if(rpt.equals("c"))
				sql = "SELECT ledger_Id,ledger_Name FROM TbLedger WHERE create_From like 'L7%' AND CompanyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();

			for(;iter.hasNext();)
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

	@SuppressWarnings("deprecation")
	private void preBtnAction()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dateFormatter.format(fromDate.getValue())+"','"+dateFormatter.format(toDate.getValue())+"')").list().iterator().next().toString();
		System.out.println(f);
		if (f.equals("1"))	
		{
			if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())))
			{
				if(ledgerList.getValue()!=null)
				{
					String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormatter.format(toDate.getValue())+"')").list().iterator().next().toString();
					session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
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
	}

	private void showReport()
	{
		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fromTo", dateRpt.format(fromDate.getValue())+" To "+dateRpt.format(toDate.getValue()));
			hm.put("fromDate", dateFormatter.format(fromDate.getValue()));
			hm.put("toDate", dateFormatter.format(toDate.getValue()));

			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("comName", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax", sessionBean.getCompanyContact());

			if(!po.actionCheck)
			{
				hm.put("image", "");
			}
			else
			{
				hm.put("image", "logo");
				hm.put("logo", sessionBean.getCompanyLogo());
			}

			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("urlLink", this.getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", ""));

			sessionBean.setFromDate(fromDate.getValue());
			sessionBean.setAsOnDate(toDate.getValue());

			hm.put("url", this.getWindow().getApplication().getURL()+"");

			Object b = this.getWindow().getApplication().getContext().getBaseDirectory();

			sessionBean.setUrl(getWindow().getApplication().getURL());
			sessionBean.setP(b);

			//if(costCentre.getValue()==null)
			{
				//hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costCentre","All");
				hm.put("costId","%");
			}
			/*else
			{
				hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costId",costCentre.getValue()+"");
			}*/

			//	System.out.println(costCentre.getValue());

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String msg = "";

			Iterator<?> iter = session.createQuery("SELECT substring(id.r,1,1),id.h+isnull('\\'+id.g,'')+isnull('\\'+id.s,'')+'\\'+id.l FROM VwLedgerList WHERE id.ledgerId = '"+ledgerList.getValue()+"'").list().iterator();
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
			if(rpt.equalsIgnoreCase("g")||rpt.equalsIgnoreCase("d")||rpt.equalsIgnoreCase("c"))
			{
				hm.put("ledgerName", ledgerList.getItemCaption(ledgerList.getValue()).toString());
				hm.put("ledgerId", ledgerList.getValue().toString());

				hm.put("withNarration", !new Boolean(withoutChk.getValue().toString()));

				String costId = "";
				//if(chkAll.getValue().equals(true))
				{
					costId = "%";
				}
				/*else
				{
					costId = costCentre.getValue().toString();
				}*/

				String sql="SELECT * FROM dbo.rptCostLedger( " +
						" '"+new SimpleDateFormat("yyyy-MM-dd").format(fromDate.getValue())+"', " +
						" '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"', " +
						" '"+ledgerList.getValue().toString()+"', " +
						" '"+costId+"','"+sessionBean.getCompanyId()+"')  " +
						" order by date,convert(Numeric,autoid)";
				System.out.println(sql);
				if(po.txtType.getValue()=="Excel"){
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "GeleralLedger.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;
					String strColName[]={"SL","Date","Voucher","Particular","DrAmount","CrAmount"};
					String Header="From: "+dateRpt.format(fromDate.getValue())+" To "+dateRpt.format(toDate.getValue());
					String exelSql="";
					
					exelSql = "select top(1) Section,empType,SectionID from tbSalary ";
					
					System.out.println("exelSql: "+exelSql);
					
					List <?> lst1=session.createSQLQuery(exelSql).list();
							
					String detailQuery[]=new String[lst1.size()];
					String [] signatureOption = {""};
					//String [] signatureOption = new String [0];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					int countInd=0;
					
					for(Iterator<?> iter1=lst1.iterator(); iter1.hasNext();)
					{
						 Object [] element1 = (Object[])iter1.next();
							groupItem[countInd]="Ledger Name: "+ledgerList.getItemCaption(ledgerList.getValue()).toString();
							GroupElement[countInd]=new Object [] {(Object)"",(Object)""};
						
							detailQuery[countInd]="SELECT date,voucher_no,(ledger_name+'- Narration: '+Narration)vParticular,CAST(ISNULL(CrAmount,0) as FLOAT)DrAmount," +
									"CAST(ISNULL(DrAmount,0) as FLOAT)CrAmount,CAST(ISNULL(mBalance,0) as FLOAT)mBalance " +
									"FROM dbo.rptCostLedgerxlx('"+new SimpleDateFormat("yyyy-MM-dd").format(fromDate.getValue())+"', " +
									" '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"', " +
									" '"+ledgerList.getValue().toString()+"', '"+costId+"','"+sessionBean.getCompanyId()+"')" ;
									
							
						System.out.println("Details query :"+detailQuery[countInd]);
						countInd++;
						
					}
					
					new GenerateExcelReport11(sessionBean, loc, url, fname, "Statement of Ledger", "Statement of Ledger",
							Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, detailQuery, 0, 0, "A4",
							"Landscape",signatureOption);
					
					Window window = new Window();
					getApplication().addWindow(window);
					getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
				
				}
				else
				{
					hm.put("sql",sql);
					Window	win = new ReportViewer(hm,"report/account/book/GeneralLedger(backnavigation).jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);
					this.getParent().getWindow().addWindow(win);
					win.setStyleName("cwindow");
					win.setCaption("GENERAL LEDGER :: "+sessionBean.getCompany());
				}
			}
			else
			{
				showNotification("Error ","Internal error. Please contact your software vendor.",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	/*private void costCenterInitialise()
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
	}*/
}
