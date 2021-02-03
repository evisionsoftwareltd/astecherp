package acc.reportmodule.financialStatement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.PreviewOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
public class ProfitLossRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;

	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();

	private DateField fromDate = new DateField("From Date:");
	private DateField toDate = new DateField("To Date:");

	private CheckBox chkAmount = new CheckBox("With Value");

	private String lcw = "130px";	
	private String rpt = "";
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private PreviewOption po = new PreviewOption();

	private AmountCommaSeperator txtUsdRate = new AmountCommaSeperator("USD Rate :");

	public ProfitLossRpt(SessionBean sessionBean,String r)
	{
		rpt = r;
		this.sessionBean = sessionBean;
		this.setWidth("350px");
		this.setResizable(false);

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);

		if(r.equalsIgnoreCase("a"))
			this.setCaption("AS ON DATE STATEMENT (SUMMARY) :: "+this.sessionBean.getCompany());

		else if(r.equalsIgnoreCase("USD($)"))
			this.setCaption("AS ON DATE STATEMENT (SUMMARY) USD($) :: "+this.sessionBean.getCompany());

		else if(r.equalsIgnoreCase("d"))
			this.setCaption("PROFIT & LOSS STATEMENT (NOTES) :: "+this.sessionBean.getCompany());

		else if(r.equalsIgnoreCase("USD (NOTES)"))
			this.setCaption("PROFIT & LOSS STATEMENT (NOTES) USD($) :: "+this.sessionBean.getCompany());

		/*else if(r.equalsIgnoreCase("comparativePlStatement"))
			this.setCaption("COMPARATIVE PROFIT & LOSS STATEMENT :: "+this.sessionBean.getCompany());*/

		else if(r.equalsIgnoreCase("s"))
			this.setCaption("BETWEEN DATE STATEMENT (SUMMARY) :: "+this.sessionBean.getCompany());

		else if(r.equalsIgnoreCase("SUMMARY USD"))
			this.setCaption("BETWEEN DATE STATEMENT (SUMMARY) USD :: "+this.sessionBean.getCompany());

		txtUsdRate.setImmediate(true);
		txtUsdRate.setWidth("60px");
		txtUsdRate.setHeight("-1px");

		fromDate.setWidth(lcw);
		fromDate.setValue(sessionBean.getFiscalOpenDate());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);

		toDate.setWidth(lcw);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);

		formLayout.addComponent(chkAmount);
		chkAmount.setImmediate(true);

		if(r.equalsIgnoreCase("s"))// || r.equalsIgnoreCase("d"))
		{
			/*formLayout.addComponent(chkAmount);
			chkAmount.setImmediate(true);*/
		}
		else if(r.equalsIgnoreCase("a") || rpt.equals("USD($)"))
		{
			toDate.setCaption("As On Date :");
			fromDate.setVisible(false);

			if(rpt.equals("USD($)"))
			{
				formLayout.addComponent(txtUsdRate);
			}

			/*formLayout.addComponent(chkAmount);
			chkAmount.setImmediate(true);*/
		}
		else if(r.equalsIgnoreCase("comparativePlStatement"))
		{
			toDate.setCaption("As On Date :");
			fromDate.setVisible(false);
		}

		if(rpt.equals("USD (NOTES)") || rpt.equals("SUMMARY USD"))
		{
			formLayout.addComponent(txtUsdRate);

			if(!rpt.equals("USD (NOTES)"))
			{
				/*formLayout.addComponent(chkAmount);
				chkAmount.setImmediate(true);*/
			}
		}

		btnL.setSpacing(true);
		btnL.addComponent(button);
		Component comp[] = {fromDate, toDate, button.btnPreview};
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
				if(rpt.equals("USD($)") || rpt.equals("USD($) GROUP") || rpt.equals("USD (NOTES)") || rpt.equals("SUMMARY USD"))
				{
					checkUsd();
				}
				else
				{
					preBtnAction();
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

		txtUsdRate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtUsdRate.getValue().toString().equals(""))
				{
					button.btnPreview.focus();
				}
			}
		});
	}

	public void checkUsd()
	{
		if(Double.parseDouble("0"+txtUsdRate.getValue().toString().replaceAll(",", ""))>0)
		{
			preBtnAction();
		}
		else
		{
			showNotification("Warning!","Provide USD Rate",Notification.TYPE_WARNING_MESSAGE);
			txtUsdRate.focus();
		}
	}

	private void preBtnAction()
	{
		if (!rpt.equals("a") && !rpt.equals("comparativePlStatement"))
		{
			if (chkDate())
				showReport();
		}
		else 
		{
			showReport();
		}	
	}

	private void showReport()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String closeAmount = "0";
		Object dateClosing = null;

		try
		{
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();

			if(!fsl.equals("0"))
			{
				session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			}

			String sqlDate = "SELECT DATEADD(DD,-1,'"+dtfYMD.format(fromDate.getValue())+"') ";

			Iterator<?> iter1 = session.createSQLQuery(sqlDate).list().iterator();
			if(iter1.hasNext())
				dateClosing = (iter1.next());

			String sqlBalance = "SELECT ISNULL(totalAmount,0) FROM dbo.profitLossStatement('"+dtfYMD.format(sessionBean.getFiscalOpenDate())+"'," +
					" '"+dateClosing+"', '"+sessionBean.getCompanyId()+"', '"+closeAmount+"') where SLflg = 42 or SLflg = 43 ";

			Iterator<?> iter = session.createSQLQuery(sqlBalance).list().iterator();
			if(iter.hasNext())
				closeAmount = (iter.next().toString());

			tx.commit();

			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("fromDate", fromDate.getValue());
			hm.put("toDate", toDate.getValue());
			hm.put("clDate", toDate.getValue());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address",sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId", sessionBean.getCompanyId());

			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());

			if(!po.actionCheck)
			{
				hm.put("image", "");
			}
			else
			{
				hm.put("image", "logo");
				hm.put("logo", sessionBean.getCompanyLogo());
			}

			sessionBean.setFromDate(fromDate.getValue());
			sessionBean.setAsOnDate(toDate.getValue());
			hm.put("url", this.getWindow().getApplication().getURL()+"");

			hm.put("usdRate",Double.parseDouble((txtUsdRate.getValue().toString().isEmpty()?
					"1":txtUsdRate.getValue().toString().replaceAll(",", ""))));

			Object b = this.getWindow().getApplication().getContext().getBaseDirectory();

			sessionBean.setUrl(getWindow().getApplication().getURL());

			sessionBean.setP(b);

			if(rpt.equalsIgnoreCase("s") || rpt.equalsIgnoreCase("USD($)") || rpt.equalsIgnoreCase("SUMMARY USD"))
			{
				String sql = "";
				if(chkAmount.booleanValue()==true)
				{
					sql = "SELECT * FROM dbo.profitLossStatement('"+dtfYMD.format(fromDate.getValue())+"'," +
							"'"+dtfYMD.format(toDate.getValue())+"', '"+sessionBean.getCompanyId()+"','"+closeAmount+"') where Amount!=0 ";
				}
				else
				{
					sql = "SELECT * FROM dbo.profitLossStatement('"+dtfYMD.format(fromDate.getValue())+"'," +
							"'"+dtfYMD.format(toDate.getValue())+"', '"+sessionBean.getCompanyId()+"','"+closeAmount+"')";
				}

				hm.put("sql",sql);
				System.out.println(sql);

				Window	win = new ReportViewer(hm,"report/account/profitloss/profitLossStatement.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				win.setCaption("PROFIT & LOSS STATEMENT(MANUFACTURING) :: "+sessionBean.getCompany());
			}

			if(rpt.equalsIgnoreCase("a"))
			{
				String sql = "";
				if(chkAmount.booleanValue()==true)
				{
					sql = "SELECT * FROM dbo.profitLossStatement('"+dtfYMD.format(fromDate.getValue())+"'," +
							"'"+dtfYMD.format(toDate.getValue())+"', '"+sessionBean.getCompanyId()+"','"+closeAmount+"') where Amount!=0 ";
				}
				else
				{
					sql = "SELECT * FROM dbo.profitLossStatement('"+dtfYMD.format(fromDate.getValue())+"'," +
							"'"+dtfYMD.format(toDate.getValue())+"', '"+sessionBean.getCompanyId()+"','"+closeAmount+"')";
				}

				//String sql = "SELECT * FROM dbo.profitLossStatement('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"', '"+sessionBean.getCompanyId()+"')";
				hm.put("sql",sql);
				System.out.println(sql);

				session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
				hm.put("fromDate", dt);
				sessionBean.setFromDate(dt);

				Window	win = new ReportViewer(hm,"report/account/profitloss/profitLossStatement.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				win.setCaption("PROFIT & LOSS STATEMENT(MANUFACTURING) :: "+sessionBean.getCompany());
			}

			else if(rpt.equalsIgnoreCase("d") || rpt.equals("USD (NOTES)"))
			{
				double usdRate = (Double.parseDouble((txtUsdRate.getValue().toString().isEmpty()?
						"1":txtUsdRate.getValue().toString().replaceAll(",", ""))));

				hm.put("usdRate",Double.parseDouble((txtUsdRate.getValue().toString().isEmpty()?
						"1":txtUsdRate.getValue().toString().replaceAll(",", ""))));

				String sql = ""; 

				if(chkAmount.booleanValue()==true)
				{
					sql="select MainHead,PrimaryGroup,MainGroup,SubGroup,Ledger_Name,parent_id," +
							" Ledger_Id,Amount/"+usdRate+" Amount,Create_From,Head_id,Notes from" +
							" dbo.profitLossDetail('"+dtfYMD.format(fromDate.getValue())+"'," +
							" '"+dtfYMD.format(toDate.getValue())+"','"+sessionBean.getCompanyId()+"') where Amount!=0" +
							" order by isnull(NOTES,0),mainhead desc,PrimaryGroup ASC,MainGroup ASC,SubGroup ASC,Ledger_Name";
				}
				else
				{
					sql="select MainHead,PrimaryGroup,MainGroup,SubGroup,Ledger_Name,parent_id," +
							" Ledger_Id,Amount/"+usdRate+" Amount,Create_From,Head_id,Notes from" +
							" dbo.profitLossDetail('"+dtfYMD.format(fromDate.getValue())+"'," +
							" '"+dtfYMD.format(toDate.getValue())+"','"+sessionBean.getCompanyId()+"')" +
							" order by isnull(NOTES,0),mainhead desc,PrimaryGroup ASC,MainGroup ASC,SubGroup ASC,Ledger_Name";
				}

				hm.put("sql",sql);
				System.out.println(sql);

				Window	win = new ReportViewer(hm,"report/account/profitloss/profitLossDetail.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				win.setCaption("PROFIT & LOSS STATEMENT(NOTES) :: "+sessionBean.getCompany());
			}

			if(rpt.equalsIgnoreCase("comparativePlStatement"))
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

					/*					Date pvToDate = (Date) session.createSQLQuery("Select cl_date  from tbFiscal_Year where slNo = "+sl+"").list().iterator().next();
					hm.put("pvToDate", pvToDate);*/

					Date pvToDate = (Date) session.createSQLQuery("Select DateAdd(YYYY,-1,'"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next();
					hm.put("pvToDate", pvToDate);

					Date fromDate = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("fromDate", fromDate);

					Date toDate = (Date) session.createSQLQuery("Select cl_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("toDate", toDate);

					hm.put("toDate", toDate);
					
					System.out.println("Done");
					
					String query="exec prcComparativePl '"+fsl+"', '"+sl+"', " +
							" '"+dtfYMD.format(fromDate)+"', '"+dtfYMD.format(toDate)+"', '"+dtfYMD.format(pvFromDate)+"', " +
							" '"+dtfYMD.format(pvToDate)+"', '"+sessionBean.getCompanyId()+"','1' ";
					
					System.out.println("Query is:"+query);

					session.createSQLQuery("exec prcComparativePl '"+fsl+"', '"+sl+"', " +
							" '"+dtfYMD.format(fromDate)+"', '"+dtfYMD.format(toDate)+"', '"+dtfYMD.format(pvFromDate)+"', " +
							" '"+dtfYMD.format(pvToDate)+"', '"+sessionBean.getCompanyId()+"','1' ").executeUpdate();
					tx.commit();

					String sql = " Select a.*, "+
							"	b.Accountname as gAccountName , "+
							"	b.Amount as gAmount , "+
							"	b.TotalAmount as gTotalAmount , "+
							"	b.flgCaption as gFlgCaption "+
							"	from tbTempComparativePl1 as a "+
							"	inner join tbTempComparativePl2 as b "+
							"	on a.SLflg = b.SLFlg ";

					System.out.println(sql);

					hm.put("sql", sql);

					Window	win = new ReportViewer(hm,"report/account/profitloss/comparativeProfitLossStatement.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

					win.setStyleName("cwindow");
					this.getParent().getWindow().addWindow(win);
					win.setCaption("COMPARATIVE PROFIT & LOSS STATEMENT :: "+sessionBean.getCompany());
				}
				else if(sl != 0)
				{	
					Date fromDate = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("fromDate", fromDate);

					Date toDate = (Date) session.createSQLQuery("Select cl_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
					hm.put("toDate", toDate);


					session.createSQLQuery("exec prcComparativePl '"+fsl+"', '', " +
							" '"+dtfYMD.format(fromDate)+"', '"+dtfYMD.format(toDate)+"', '', " +
							" '', '"+sessionBean.getCompanyId()+"','2' ").executeUpdate();
					tx.commit();

					String sql = " Select a.*, "+
							"	b.Accountname as gAccountName , "+
							"	b.Amount as gAmount , "+
							"	b.TotalAmount as gTotalAmount , "+
							"	b.flgCaption as gFlgCaption "+
							"	from tbTempComparativePl1 as a "+
							"	left join tbTempComparativePl2 as b "+
							"	on a.SLflg = b.SLFlg ";

					System.out.println(sql);

					hm.put("sql", sql);

					Window	win = new ReportViewer(hm,"report/account/profitloss/comparativeProfitLossStatement.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

					win.setStyleName("cwindow");
					this.getParent().getWindow().addWindow(win);
					win.setCaption("COMPARATIVE PROFIT & LOSS STATEMENT :: "+sessionBean.getCompany());
				}
				else
				{
					showNotification("Warning","There are no data in this fiscal year");
				}
			}
			else
			{
				//showNotification("Error ","Internal error. Please contact your software vendor.",Notification.TYPE_ERROR_MESSAGE);
			}			
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean chkDate()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
		//System.out.println(f);
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
}