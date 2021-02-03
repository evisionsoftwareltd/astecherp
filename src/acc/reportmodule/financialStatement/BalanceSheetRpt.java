package acc.reportmodule.financialStatement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.PreviewOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.CheckBox;
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
public class BalanceSheetRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;	
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	private DateField onDate = new DateField("As on Date:");

	private AmountCommaSeperator dollarRate = new AmountCommaSeperator("Rate($):");
	private CheckBox chkAmount = new CheckBox("With Value");

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private String lcw = "110px";	
	private String rpt = "";

	private PreviewOption po = new PreviewOption();

	public BalanceSheetRpt(SessionBean sessionBean,String rpt)
	{
		this.sessionBean = sessionBean;
		this.setWidth("350px");
		this.setResizable(false);
		this.rpt = rpt;

		formLayout.addComponent(onDate);

		if(rpt.equals("s"))
		{
			this.setCaption("BALANCE SHEET STATEMENT :: "+this.sessionBean.getCompany());

			formLayout.addComponent(chkAmount);
			chkAmount.setImmediate(true);
		}
		else if(rpt.equals("sd"))
		{
			this.setCaption("BALANCE SHEET STATEMENT WITH DOLLAR :: "+this.sessionBean.getCompany());

			formLayout.addComponent(dollarRate);
			dollarRate.setImmediate(true);
			dollarRate.setWidth("60px");

			formLayout.addComponent(chkAmount);
			chkAmount.setImmediate(true);
		}
		else if(rpt.equals("comparativeBalanceSheetStatement"))
		{
			this.setCaption("COMPARATIVE BALANCE SHEET STATEMENT :: "+this.sessionBean.getCompany());
		}
		else
		{
			this.setCaption("BALANCE SHEET NOTES :: "+this.sessionBean.getCompany());
		}

		onDate.setWidth(lcw);
		onDate.setValue(new java.util.Date());
		onDate.setResolution(PopupDateField.RESOLUTION_DAY);
		onDate.setDateFormat("dd-MM-yyyy");
		onDate.setInvalidAllowed(false);
		onDate.setImmediate(true);

		btnL.setSpacing(true);
		btnL.addComponent(button);
		Component comp[] = {onDate, dollarRate, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(po);
		formLayout.addComponent(btnL);
		mainLayout.addComponent(formLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		buttonActionAdd();
		onDate.focus();
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
		showBalanceSheet();
	}

	private void showBalanceSheet()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(onDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;

			if(!fsl.equals("0"))
			{
				session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
				tx.commit();
			}

			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("clDate",onDate.getValue());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId",sessionBean.getCompanyId());
			hm.put("userName",sessionBean.getUserName());
			hm.put("userIp",sessionBean.getUserIp());

			if(!po.actionCheck)
			{
				hm.put("image", "");
			}
			else
			{
				hm.put("image", "logo");
				hm.put("logo", sessionBean.getCompanyLogo());
			}

			sessionBean.setAsOnDate(onDate.getValue());
			hm.put("url", this.getWindow().getApplication().getURL()+"");

			Object b=this.getWindow().getApplication().getContext().getBaseDirectory();

			sessionBean.setUrl(getWindow().getApplication().getURL());

			sessionBean.setP(b);
			Window win = null;

			if(rpt.equals("s"))
			{
				double dollar = 0; 
				String sql = "";

				if(!dollarRate.getValue().toString().isEmpty())
				{
					dollar = Double.parseDouble(dollarRate.getValue().toString().replaceAll(",", ""));
				}

				if(chkAmount.booleanValue()==true)
				{
					sql = "SELECT * FROM dbo.balanceSheet('"+dtfYMD.format(onDate.getValue())+"', '"+sessionBean.getCompanyId()+"','1')  where InnerAmount!=0";
				}
				else
				{
					sql = "SELECT * FROM dbo.balanceSheet('"+dtfYMD.format(onDate.getValue())+"', '"+sessionBean.getCompanyId()+"','1')";
				}
				System.out.println("sql="+sql);

				hm.put("sql", sql);
				hm.put("dollarRate", dollar);
				System.out.println(dollar+"Dollar");

				win = new ReportViewer(hm,"report/account/balancesheet/balancesheetStatement.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setCaption("BALANCE SHEET STATEMENT :: "+sessionBean.getCompany());
				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
			}

			else if(rpt.equals("sd"))
			{
				if(Double.parseDouble("0"+dollarRate.getValue().toString().replaceAll(",", ""))>0)
				{
					double dollar = 0; 
					String sql = "";

					if(!dollarRate.getValue().toString().isEmpty())
					{
						dollar = Double.parseDouble(dollarRate.getValue().toString().replaceAll(",", ""));
					}

					if(chkAmount.booleanValue()==true)
					{
						sql = "SELECT * FROM dbo.balanceSheet('"+dtfYMD.format(onDate.getValue())+"', '"+sessionBean.getCompanyId()+"','"+dollar+"')  where InnerAmount!=0";
					}
					else
					{
						sql = "SELECT * FROM dbo.balanceSheet('"+dtfYMD.format(onDate.getValue())+"', '"+sessionBean.getCompanyId()+"','"+dollar+"')";
					}
					System.out.println("sql="+sql);

					hm.put("sql", sql);
					hm.put("dollarRate", dollar);
					System.out.println(dollar+"Dollar");

					win = new ReportViewer(hm,"report/account/balancesheet/balancesheetStatementDollar.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

					win.setCaption("BALANCE SHEET STATEMENT WITH DOLLAR :: "+sessionBean.getCompany());
					win.setStyleName("cwindow");
					this.getParent().getWindow().addWindow(win);
				}
				else
				{
					showNotification("Warning!","Please provide dollar($) rate",Notification.TYPE_WARNING_MESSAGE);
					dollarRate.focus();
				}
			}

			else if(rpt.equals("comparativeBalanceSheetStatement"))
			{
				
				session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				if(!fsl.equals("0"))
				{
					Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("fromDate", dt);
					sessionBean.setFromDate(dt);
				}

				int sl = Integer.parseInt(fsl);
				if(sl>1)
				{
					sl = sl - 1;

					Date pvFromDate = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+sl+"").list().iterator().next();
					hm.put("pvFromDate", pvFromDate);
					

					Date pvToDate = (Date) session.createSQLQuery("Select DateAdd(YYYY,-1,'"+dtfYMD.format(onDate.getValue())+"')").list().iterator().next();
					hm.put("pvToDate", pvToDate);
					System.out.println("Atik Hasan");
					
					

					Date fromDate = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("fromDate", fromDate);

					Date toDate = (Date) session.createSQLQuery("Select cl_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("toDate", toDate);
					
					

					session.createSQLQuery("exec prcComparativeBalanceSheetStatement '"+fsl+"', '"+sl+"', " +
							" '"+dtfYMD.format(onDate.getValue())+"', " +
							" '"+dtfYMD.format(pvToDate)+"', '1','1' ").executeUpdate();

					tx.commit();

					String sql = " Select a.* , b.HeadId as gHeadId, b.SlNo as s, b.flg as gFlg, b.Notes as gNotes, "+
							" isNull(b.InnerAmount,0) as gInnerAmount, "+
							" isNull(b.OuterAmount,0) as gOuterAmount "+
							" from tbTempComparativeBalance1 as a "+
							" inner join tbTempComparativeBalance2 as b "+
							" on a.SlNo = b.SlNo ";

					hm.put("sql", sql);

					System.out.println(sql);

					win = new ReportViewer(hm,"report/account/balancesheet/comparativeBalancesheetStatement.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

					win.setCaption("COMPARATIVE BALANCE SHEET STATEMENT :: "+sessionBean.getCompany());
					win.setStyleName("cwindow");
					this.getParent().getWindow().addWindow(win);
				}
				else if(sl != 0)
				{	
					Date fromDate = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("fromDate", fromDate);

					Date toDate = (Date) session.createSQLQuery("Select cl_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("toDate", toDate);


					session.createSQLQuery("exec prcComparativeBalanceSheetStatement '"+fsl+"', '"+sl+"', " +
							" '"+dtfYMD.format(toDate)+"', " +
							" '', '"+sessionBean.getCompanyId()+"','2' ").executeUpdate();
					tx.commit();

					String sql = " Select a.* , b.HeadId as gHeadId , b.SlNo as s, b.flg as gFlg, b.Notes as gNotes, "+
							" isNull(b.InnerAmount,0) as gInnerAmount, "+
							" isNull(b.OuterAmount,0) as gOuterAmount "+
							" from tbTempComparativeBalance1 as a "+
							" left join tbTempComparativeBalance2 as b "+
							" on a.SlNo = b.SlNo ";

					hm.put("sql", sql);

					System.out.println(sql);

					win = new ReportViewer(hm,"report/account/balancesheet/comparativeBalancesheetStatement.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

					win.setCaption("COMPARATIVE BALANCE SHEET STATEMENT :: "+sessionBean.getCompany());
					win.setStyleName("cwindow");
					this.getParent().getWindow().addWindow(win);
				}
				else
				{
					showNotification("Warning","There are no data in this fiscal year");
				}
			}

			else if(rpt.equals("d"))
			{
				String sql = " SELECT * FROM dbo.funBalanceSheetDetails('"+dtfYMD.format(onDate.getValue())+"', '"+sessionBean.getCompanyId()+"') order by cast(Notes as numeric),HeadId,SubGroupName,LedgerId";
				System.out.println("Report SQL: "+sql);

				hm.put("sql",sql);

				win = new ReportViewer(hm,"report/account/balancesheet/balancesheetDetails.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setCaption("BALANCE SHEET DETAILS :: "+sessionBean.getCompany());
				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
			}
			
			else 
			{
				
				session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
			  	
				int sl = Integer.parseInt(fsl);
				
				session.createSQLQuery("truncate table  tbtembl1").executeUpdate();
				session.createSQLQuery("truncate table  tbtembl2").executeUpdate();
				
				Date pvToDate = (Date) session.createSQLQuery("Select DateAdd(YYYY,-1,'"+dtfYMD.format(onDate.getValue())+"')").list().iterator().next();
				hm.put("pvDate", pvToDate);
				System.out.println("Atik Hasan");
				
				String pvsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(pvToDate)+"')").list().iterator().next().toString();
				
				String sql =" insert into  tbtembl1 select *  FROM dbo.funBalanceSheetDetails('"+dtfYMD.format(onDate.getValue())+"', '1') order by HeadId,SubGroupName,LedgerId";
				session.createSQLQuery(sql).executeUpdate();
				
				session.createSQLQuery("exec prcAlterVoucher "+pvsl+" ").executeUpdate();
				
				String sql1 =" insert into  tbtembl2 select *  FROM dbo.funBalanceSheetDetails('"+dtfYMD.format(pvToDate)+"', '1') order by HeadId,SubGroupName,LedgerId";
				session.createSQLQuery(sql1).executeUpdate();
				tx.commit();
				
				
				
				
				
				String query="select * from funblnotecompare('%') order by Notes, HeadId,HeadName,SubGroupName,LedgerId ";
				System.out.println("Query is:"+query);
				
				hm.put("sql",query);
				
				
				win = new ReportViewer(hm,"report/account/balancesheet/balancesheetDetailscompare.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setCaption("BALANCE SHEET NOTES COMPARE :: "+sessionBean.getCompany());
				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				
				
				
			}
		}
		catch(Exception exp)
		{
			showNotification("Error  here is ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}	
}
