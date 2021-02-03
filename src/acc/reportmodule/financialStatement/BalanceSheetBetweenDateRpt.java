package acc.reportmodule.financialStatement;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.PreviewOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
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
public class BalanceSheetBetweenDateRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;	
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();

	private DateField fromDate = new DateField("Date From:");
	private DateField toDate = new DateField("Date To:");

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private String lcw = "130px";	
	private String rpt = "";

	private PreviewOption po = new PreviewOption();

	public BalanceSheetBetweenDateRpt(SessionBean sessionBean,String rpt)
	{
		this.sessionBean = sessionBean;
		this.setWidth("330px");
		this.setResizable(false);
		this.rpt = rpt;

		if(rpt.equals("s"))
		{
			this.setCaption("BALANCE SHEET STATEMENT BETWEEN DATE :: "+this.sessionBean.getCompany());
		}
		/*else if(rpt.equals("comparativeBalanceSheetStatement"))
		{
			this.setCaption("COMPARATIVE BALANCE SHEET STATEMENT :: "+this.sessionBean.getCompany());
		}
		else
		{
			this.setCaption("BALANCE SHEET DETAILS :: "+this.sessionBean.getCompany());
		}*/

		fromDate.setWidth(lcw);
		fromDate.setValue(new java.util.Date());
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

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);

		btnL.setSpacing(true);
		btnL.addComponent(button);
		Component comp[] = {fromDate, button.btnPreview};

		new FocusMoveByEnter(this, comp);

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(po);
		formLayout.addComponent(btnL);
		mainLayout.addComponent(formLayout);

		this.addComponent(mainLayout);

		mainLayout.setMargin(true);
		buttonActionAdd();
		fromDate.focus();
	}

	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(getValidation())
				{
					showBalanceSheet();
				}
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

	private boolean getValidation()
	{
		boolean ret = false;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl1 = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(fromDate.getValue())+"')").list().iterator().next().toString();

		String fsl2 = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();

		if(fsl1.toString().equals(fsl2))
		{
			ret = true;
		}
		else
		{
			showNotification("Warning!","Date should be same fiscal year",Notification.TYPE_WARNING_MESSAGE);
		}

		return ret;
	}

	private void showBalanceSheet()
	{
		String sqlNew = "";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(fromDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;

			if(!fsl.equals("0"))
			{
				session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
				tx.commit();
			}

			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			sqlNew = "SELECT * FROM dbo.balanceSheetBetweenDate('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"','"+sessionBean.getCompanyId()+"')";
			System.out.println("BLSHEET : "+sqlNew);
			hm.put("sql",sqlNew);
			hm.put("fromDate",fromDate.getValue());
			hm.put("toDate",toDate.getValue());
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

			System.out.println(fromDate.getValue());
			System.out.println(sessionBean.getCompanyId());

			sessionBean.setAsOnDate(fromDate.getValue());
			hm.put("url", this.getWindow().getApplication().getURL()+"");

			Object b=this.getWindow().getApplication().getContext().getBaseDirectory();

			sessionBean.setUrl(getWindow().getApplication().getURL());

			sessionBean.setP(b);
			Window win = null;
			if(rpt.equals("s"))
			{
				win = new ReportViewer(hm,"report/account/balancesheet/balancesheetStatementBetweenDate.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setStyleName("cwindow");
				win.setCaption("BALANCE SHEET STATEMENT BETWEEN DATE :: "+sessionBean.getCompany());
				this.getParent().getWindow().addWindow(win);
			}

			/*else if(rpt.equals("comparativeBalanceSheetStatement"))
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

					Date pvToDate = (Date) session.createSQLQuery("Select DateAdd(YYYY,-1,'"+dtfYMD.format(fromDate)+"')").list().iterator().next();
					hm.put("pvToDate", pvToDate);

					Date fromDate = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("fromDate", fromDate);

					Date toDate = (Date) session.createSQLQuery("Select cl_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("toDate", toDate);


					session.createSQLQuery("exec prcComparativeBalanceSheetStatement '"+fsl+"', '"+sl+"', " +
							" '"+dtfYMD.format(toDate)+"', " +
							" '"+dtfYMD.format(pvToDate)+"', '1','1' ").executeUpdate();

					session.createSQLQuery("exec prcComparativeBalanceSheetStatement '"+fsl+"', '"+sl+"', " +
							" '"+dtfYMD.format(fromDate)+"', " +
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
					this.getParent().getWindow().addWindow(win);
				}
				else
				{
					showNotification("Warning","There are no data in this fiscal year");
				}
			}

			else
			{
				win = new ReportViewer(hm,"report/account/balancesheet/balancesheetDetails.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);
				//				win = new ReportPdf(hm,"report/account/balancesheet/balancesheetDetails.jasper",
				//						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
				//						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				win.setCaption("BALANCE SHEET DETAILS :: "+sessionBean.getCompany());
				this.getParent().getWindow().addWindow(win);
			}*/
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}	
}
