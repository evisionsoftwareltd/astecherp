package com.example.astechac;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.LogIn;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class AstechacApplication extends Application implements HttpServletRequestListener
{
	String tempParameter;
	SessionBean sessionBean;
	File a;
	URL b;
	int flag = 1,i=0;
	public String contextname;
	URL context;

	public void init()
	{
		sessionBean = new SessionBean();
		sessionBean.setUserId("1");
		sessionBean.isAdmin(true);
		sessionBean.setCompanyId("1");
		sessionBean.setSubmitable(true);
		sessionBean.setUpdateable(true); 
		sessionBean.setDeleteable(true);
		sessionBean.setCompany("ASTECH GROUP");

		dataInitialise();

		setMainWindow(new MainWindow());

		System.out.println("url print "+context+"\t"+contextname);
		URIHandler uriHandler = new URIHandler()
		{
			public DownloadStream handleURI(URL context,String relativeUri)
			{
				return null;
			}
		};

		context = this.getURL();
		System.out.println("Context is:"+context);
		contextname = context.toString().substring(context.toString().indexOf("/",7)+1,context.toString().length()-1);
		System.out.println("context name is:"+contextname);
		System.out.println("url print "+context+"\t"+contextname);

		sessionBean.setContextName(contextname);
		getMainWindow().addURIHandler(uriHandler);
		setTheme("astechactheme");
	}

	private void dataInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			Iterator<?> iter = session.createSQLQuery("SELECT * FROM tbCompanyInfo where companyId ="
					+ " '"+ sessionBean.getCompanyId() +"'").list().iterator();
			if(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();

				sessionBean.setCompanyLogo(element[12].toString());
			}
			tx.commit();
		}
		catch(Exception exp)
		{
			//showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public Window getWindow(String name)
	{
		// If the window is identified by name, we are good to go
		Window w = super.getWindow(name);

		// If not, we must create a new window for this new browser window/tab
		if (w == null)
		{
			w = new MainWindow();
			System.out.println(name);
			w.setName(name);
			addWindow(w);
		}
		return w;
	}

	private class MainWindow extends Window  
	{
		MainWindow() 
		{
			this.setCaption("ASTECH ERP SOLUTION");		
			this.setStyleName("backcolor");

			poolStart();

			System.out.println("Context : "+sessionBean.getContextName());

			System.out.println(tempParameter);

			if(tempParameter.equalsIgnoreCase("/UIDL")  && i==0)
			{
				if(flag==1)
				{			
					LogIn rm = new LogIn(sessionBean);
					//getWindow().addWindow(rm);
					
					addWindow(rm);
					flag = 0;
					
				}
			}
			if(!tempParameter.equalsIgnoreCase("/UIDL") && tempParameter.trim().length()>3)
			{
				try
				{
					HashMap<Object, Object> hm = new HashMap<Object, Object>();
					hm.put("clDate",sessionBean.getasOnDate());
					hm.put("comName",sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phoneFax",sessionBean.getCompanyContact());

					hm.put("userName", sessionBean.getUserName());
					hm.put("userIp", sessionBean.getUserIp());

					hm.put("url", sessionBean.getUrl()+"");
					System.out.println(tempParameter.substring(0, tempParameter.indexOf("="))+" full Parameter:"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length()));
					System.out.println(tempParameter);

					if(tempParameter.substring(0, tempParameter.indexOf("=")).equalsIgnoreCase("/voucher"))
					{
						String voucher=tempParameter.substring(tempParameter.indexOf("=")+1,14);

						System.out.println("Voucher="+voucher);

						if(voucher.matches("JV-NO"))
						{
							System.out.println(voucher);
							String	sql = "SELECT * FROM vwJournalVoucher WHERE  Voucher_No in('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"') And companyId = '"+ sessionBean.getCompanyId() +"' and company_Id = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";
							System.out.println(sql);

							hm.put("sql",sql);
							Window win = new ReportViewer(hm,"report/account/voucher/JournalVoucherWithoutLink.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);
							win.setStyleName("cwindow");
							win.setCaption("JOURNAL VOUCHER :: "+sessionBean.getCompany());
							win.setClosable(false);
						}

						if(voucher.matches("EMPIN"))
						{
							hm.put("company", sessionBean.getCompany());
							hm.put("address", sessionBean.getCompanyAddress());
							hm.put("phone",sessionBean.getCompanyContact());
							hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
							hm.put("SysDate",new Date());
							hm.put("logo", sessionBean.getCompanyLogo());

							String sql = " SELECT * from vw_rptEmployeeifo as a" +
									" left join tbEducation as b on a.vEmployeeId = b.vEmployeeId" +
									" Where a.vEmployeeId ='"+(tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())).toString().replaceAll("EMPIN-", "")+"'";
							System.out.println(sql);

							hm.put("sql", sql);

							Window win = new ReportViewer(hm,"report/account/hrmModule/RptEmployeeInfo.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);
							win.setStyleName("cwindow");
							win.setCaption("EMPLOYEE INFORMATION :: "+sessionBean.getCompany());
							win.setClosable(false);
						}

						if(voucher.matches("AP-CR"))
						{
							String sql = "Select * from vwAssetPurchaseVoucher WHERE (VoucherNo IN ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"')) AND (companyId ='"+ sessionBean.getCompanyId() +"') AND (company_Id ='"+ sessionBean.getCompanyId() +"')";
							System.out.println(sql);

							hm.put("sql",sql);
							Window win = new ReportViewer(hm,"report/account/voucher/FixedAssetVoucherWithoutLink.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);
							win.setStyleName("cwindow");
							win.setCaption("FIXED ASSET VOUCHER :: "+sessionBean.getCompany());
							win.setClosable(false);
						}

						if(voucher.matches("ASDEP"))
						{
							String	sql = "Select * from vwDepreciationVoucher WHERE (Voucher_No IN ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"')) AND (companyId ='"+ sessionBean.getCompanyId() +"') AND (company_Id ='"+ sessionBean.getCompanyId() +"')";
							System.out.println(sql);

							hm.put("sql",sql);
							Window win;
							win=new ReportViewer(hm,"report/account/voucher/DepriciationVoucher.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);
							win.setStyleName("cwindow");
							win.setCaption("FIXED ASSET VOUCHER :: "+sessionBean.getCompany());
							win.setClosable(false);
						}

						if(voucher.matches("DR-CH"))
						{
							hm.put("url", sessionBean.getUrl()+"");

							String	sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by DrAmount desc";

							System.out.println("Mezbah: "+sql);
							hm.put("sql",sql);

							Window win = new ReportViewer(hm,"report/account/voucher/CashPaymentVoucherWithoutLink.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);
							win.setStyleName("cwindow");
							win.setCaption("CASH PAYMENT VOUCHER :: "+sessionBean.getCompany());
							win.setClosable(false);

						}

						if(voucher.matches("CR-CH"))
						{
							String sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by CrAmount desc";
							System.out.println(sql);
							hm.put("sql",sql);

							Window win = new ReportViewer(hm,"report/account/voucher/CashReceipttVoucherLink.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);

							getWindow().addWindow(win);
							win.setStyleName("cwindow");
							win.setCaption("CASH RECEIPT VOUCHER :: "+sessionBean.getCompany());
							win.setClosable(false);
						}

						if(voucher.matches("DR-BK"))
						{
							String sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount DESC";
							hm.put("sql",sql);

							Window win = new ReportViewer(hm,"report/account/voucher/BankPaymentVoucherWithoutLink.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);
							getWindow().addWindow(win);
							win.setStyleName("cwindow");
							win.setCaption("BANK PAYMENT VOUCHER :: "+sessionBean.getCompany());
							win.setClosable(false);
						}

						if(voucher.matches("CR-BK"))
						{
							String sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"') and companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CrAmount Desc";
							System.out.println(sql);
							hm.put("sql",sql);

							Window win = new ReportViewer(hm,"report/account/voucher/BankReceiveVoucherLink.jasper",
									sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
									sessionBean.getUrl()+"VAADIN/rpttmp",false,
									sessionBean.getUrl()+"VAADIN/applet",true);

							getWindow().addWindow(win);
							win.setStyleName("cwindow");
							win.setClosable(false);
							win.setCaption("BANK RECEIVE VOUCHER :: "+sessionBean.getCompany());
						}
					}

					//Window win = null;

					if(tempParameter.substring(0, tempParameter.indexOf("=")).equalsIgnoreCase("/head"))
					{
						String sql="SELECT * FROM dbo.funBalanceSheetDetails('"+new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getasOnDate()) +"', '"+sessionBean.getCompanyId()+"' )" +
								"where headId='"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"' " +
								"order by SLNo, LedgerName";

						hm.put("sql",sql);

						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						session.beginTransaction();

						System.out.println(	sessionBean.getP());

						Window win=new ReportViewer(hm,"report/account/balancesheet/balancesheetDetails(backnavigation).jasper",
								sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
								sessionBean.getUrl()+"VAADIN/rpttmp",false,
								sessionBean.getUrl()+"VAADIN/applet",true);

						win.setCaption("Project Report");
						getWindow().addWindow(win);
						win.setStyleName("cwindow");
						win.setClosable(false);
					}

					if(tempParameter.substring(0, tempParameter.indexOf("=")).equalsIgnoreCase("/headId"))
					{
						String sql = "select * from dbo.profitLossDetail('"+new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFromDate())+"','"+new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getasOnDate())+"','"+sessionBean.getCompanyId()+"')  where substring(create_from,1,2)='"+tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length())+"' order by mainhead desc,PrimaryGroup ASC,MainGroup ASC,SubGroup ASC";
						hm.put("sql",sql);

						System.out.println(sql);
						hm.put("fromDate", sessionBean.getFromDate());
						hm.put("toDate", sessionBean.getasOnDate());

						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						session.beginTransaction();

						System.out.println(	sessionBean.getP());

						Window win=new ReportViewer(hm,"report/account/profitloss/profitLossDetail.jasper",
								sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
								sessionBean.getUrl()+"VAADIN/rpttmp",false,
								sessionBean.getUrl()+"VAADIN/applet",true);

						win.setCaption("PROFIT & LOSS STATEMENT(DETAILS) :: "+sessionBean.getCompany());
						getWindow().addWindow(win);
						win.setStyleName("cwindow");
						win.setClosable(false);
					}

					if(tempParameter.substring(0, tempParameter.indexOf("=")).equalsIgnoreCase("/ledger"))
					{
						String tempLedger = tempParameter.substring(tempParameter.indexOf("=")+1,tempParameter.length());

						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						Transaction tx = session.beginTransaction();

						String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+new SimpleDateFormat("yyyy-MM-dd").format( sessionBean.getasOnDate())+"')").list().iterator().next().toString();

						session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
						Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();

						String ledgerName = " select * from tbLedger where Ledger_Id='"+tempLedger+"'";
						Iterator<?> iterLedger = session.createSQLQuery(ledgerName).list().iterator();

						if(iterLedger.hasNext())
						{
							Object[] element = (Object[]) iterLedger.next();
							hm.put("ledgerName", element[1]);
						}	
						tx.commit();

						hm.put("fromTo",new SimpleDateFormat("dd-MM-yyyy").format( dt)+" To "+new SimpleDateFormat("dd-MM-yyyy").format(sessionBean.getasOnDate()));


						String sql = "SELECT * FROM dbo.rptCostLedger('"+dt+"','"+new SimpleDateFormat("yyyy-MM-dd").format( sessionBean.getasOnDate())+"','"+tempLedger+"','U-3','"+sessionBean.getCompanyId()+"') order by date,convert(Numeric,autoid)";
						hm.put("sql",sql);

						System.out.println(sql);

						Window win = new ReportViewer(hm,"report/account/book/GeneralLedger(backnavigation).jasper",
								sessionBean.getP()+"".replace("\\","/")+"/VAADIN/rpttmp",
								sessionBean.getUrl()+"VAADIN/rpttmp",false,
								sessionBean.getUrl()+"VAADIN/applet",true);

						win.setCaption("Project Report");
						getWindow().addWindow(win);
						win.setStyleName("cwindow");
						win.setClosable(false);
					}
				}
				catch(Exception e)
				{
					System.out.println(e);
				}
				i=1;
			}
		}
	}

	public void poolStart()
	{
		try
		{
			class PoolStart extends Thread
			{
				public void run()
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					Transaction tx = session.beginTransaction();
					Iterator<?> iter = session.createSQLQuery("SELECT Op_Date, Cl_Date,SlNo FROM tbFiscal_Year where Running_Flag = 1").list().iterator();

					if(iter.hasNext())
					{
						Object[] element = (Object[]) iter.next();
						System.out.println(element[0].toString());
						sessionBean.setFiscalOpenDate(element[0]);
						sessionBean.setFiscalCloseDate(element[1]);
						sessionBean.setFiscalRunningSerial(element[2].toString());
					}
					tx.commit();
				}
			};
			new PoolStart().start(); 
		}
		catch(Exception ex)
		{
		}
	}

	public void onRequestStart(HttpServletRequest request,HttpServletResponse response)
	{
		tempParameter=request.getPathInfo().toString();
		//System.out.println("Start "+tempParameter);
	}

	
	
	public void onRequestEnd(HttpServletRequest request,HttpServletResponse response)
	{
		// TODO Auto-generated method stub
		//System.out.println("End");
	}
	
	

	public String tbVoucherName(String date)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+date+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;

		return voucher;
	}

	public boolean isClosedFiscal(String date)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();

		String flag =	session.createSQLQuery("Select isClosed from tbFiscal_Year where '"+date+"' between op_date and cl_date").list().iterator().next().toString();
		System.out.println(flag);
		if (flag.toString().equals("true"))
			return true;
		else
			return false;
	}
}