package acc.reportmodule.financialStatement;

import java.text.SimpleDateFormat;
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
public class AdjustedTrialRptBetDate extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;	
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();

	private DateField dFromDate = new DateField("Date From :");
	private DateField dToDate = new DateField("Date To :");

	private String lcw = "130px";

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private String rpt = "";

	private PreviewOption po = new PreviewOption();
	private AmountCommaSeperator txtUsdRate = new AmountCommaSeperator("USD Rate :");
	private CheckBox chkAmount = new CheckBox("With Value");

	public AdjustedTrialRptBetDate(SessionBean sessionBean,String rpt)
	{
		this.sessionBean = sessionBean;
		this.setWidth("330px");
		this.setResizable(false);
		this.rpt = rpt;

		if(rpt.equals("a"))
		{
			this.setCaption("ADJUSTED TRIAL BALANCE BETWEEN DATE :: "+this.sessionBean.getCompany());
		}
		else if(rpt.equals("USD"))
		{
			this.setCaption("ADJUSTED TRIAL BALANCE BETWEEN DATE :: "+this.sessionBean.getCompany());
		}
		else
		{
			this.setCaption("GROUP WISE TRIAL BALANCE :: "+this.sessionBean.getCompany());
		}

		dFromDate.setWidth(lcw);
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setInvalidAllowed(false);
		dFromDate.setImmediate(true);

		dToDate.setWidth(lcw);
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setInvalidAllowed(false);
		dToDate.setImmediate(true);

		btnL.setSpacing(true);
		btnL.addComponent(button);

		formLayout.addComponent(dFromDate);
		formLayout.addComponent(dToDate);
		if(rpt.equals("a")||rpt.equals("USD")){
			formLayout.addComponent(chkAmount);
		}

		if(rpt.equals("USD"))
		{
			txtUsdRate.setImmediate(true);
			txtUsdRate.setWidth("60px");
			txtUsdRate.setHeight("-1px");
			formLayout.addComponent(txtUsdRate);
		}

		Component comp[] = {dFromDate, button.btnPreview};
		new FocusMoveByEnter(this, comp);

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(po);
		formLayout.addComponent(btnL);
		
		mainLayout.addComponent(formLayout);
		
		this.addComponent(mainLayout);

		mainLayout.setMargin(true);

		buttonActionAdd();
		dFromDate.focus();
	}

	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(getValidation())
				{
					if(rpt.equals("USD"))
					{
						checkUsd();
					}
					else
					{
						preBtnAction();
					}
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

	private boolean getValidation()
	{
		boolean ret = false;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl1 = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(dFromDate.getValue())+"')").list().iterator().next().toString();

		String fsl2 = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(dToDate.getValue())+"')").list().iterator().next().toString();

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

	private void preBtnAction()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(dFromDate.getValue())+"')").list().iterator().next().toString();

		session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
		tx.commit();

		if(rpt.equals("a") || rpt.equals("USD"))
		{
			showAdjustedTrial();
		}
		else
		{
			showGroupTRial();	
		}
	}

	private void showAdjustedTrial()
	{
		double usdRate = (Double.parseDouble((txtUsdRate.getValue().toString().isEmpty()?
				"1":txtUsdRate.getValue().toString().replaceAll(",", ""))));

		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("fromDate",dFromDate.getValue());
			hm.put("toDate",dToDate.getValue());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId",sessionBean.getCompanyId());

			hm.put("userIp", sessionBean.getUserIp().toString());
			hm.put("userName", sessionBean.getUserName().toString());

			String sql = "select Ledger_Id,Ledger_Name,closingBal/"+usdRate+" closingBal,Tranbal/"+usdRate+"" +
					" Tranbal,Drbal/"+usdRate+" Drbal,Crbal/"+usdRate+" Crbal,trDrBal/"+usdRate+" trDrBal,trCrBal/"+usdRate+" trCrBal,lgroup,sl,HeadName," +
					" HeadId,GroupName from [funAddjustedTrialBetweenDate]('"+dtfYMD.format(dFromDate.getValue())+"'," +
					" '"+dtfYMD.format(dToDate.getValue())+"','"+sessionBean.getCompanyId()+"')"+
					" where (closingBal!=0 or Tranbal!=0 or Drbal!=0)"+
					" order by sl,HeadId,GroupName,Ledger_Name";
			
			if(!chkAmount.booleanValue()){
				 sql = "select Ledger_Id,Ledger_Name,closingBal/"+usdRate+" closingBal,Tranbal/"+usdRate+"" +
						" Tranbal,Drbal/"+usdRate+" Drbal,Crbal/"+usdRate+" Crbal,trDrBal/"+usdRate+" trDrBal,trCrBal/"+usdRate+" trCrBal,lgroup,sl,HeadName," +
						" HeadId,GroupName from [funAddjustedTrialBetweenDate]('"+dtfYMD.format(dFromDate.getValue())+"'," +
						" '"+dtfYMD.format(dToDate.getValue())+"','"+sessionBean.getCompanyId()+"')"+
					//	" where (closingBal!=0 or Tranbal!=0 or Drbal!=0)"+
						" order by sl,HeadId,GroupName,Ledger_Name";
			}
			
			System.out.println(sql);

			if(!po.actionCheck)
			{
				hm.put("image", "");
			}
			else
			{
				hm.put("image", "logo");
				hm.put("logo", sessionBean.getCompanyLogo());
			}

			hm.put("usdRate",Double.parseDouble((txtUsdRate.getValue().toString().isEmpty()?
					"1":txtUsdRate.getValue().toString().replaceAll(",", ""))));

			hm.put("sql", sql);

			System.out.println(sessionBean.getCompanyId());

			Window	win = new ReportViewer(hm,"report/account/trialbal/trialAdjustedBetweenDateNew.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);

			if(rpt.equals("USD"))
				win.setCaption("ADJUSTED TRIAL BALANCE BETWEEN DATE USD :: "+sessionBean.getCompany());
			else
				win.setCaption("ADJUSTED TRIAL BALANCE BETWEEN DATE :: "+sessionBean.getCompany());
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void showGroupTRial()
	{
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("onDate",dFromDate.getValue());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId",sessionBean.getCompanyId());

			hm.put("userIp", sessionBean.getUserIp().toString());
			hm.put("userName", sessionBean.getUserName().toString());

			if(!po.actionCheck)
			{
				hm.put("image", "");
			}
			else
			{
				hm.put("image", "logo");
				hm.put("logo", sessionBean.getCompanyLogo());
			}

			Window	win = new ReportViewer(hm,"report/account/trialbal/trialAdjustedGroup.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);
			win.setCaption("GROUP WISE TRIAL BALANCE :: "+sessionBean.getCompany());
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
