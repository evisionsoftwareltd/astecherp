package acc.reportmodule.mis;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.ReportOption;
import com.common.share.ReportPdf;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class CashFlowYearly extends Window
{
	CommonButton button= new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;

	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();

	private InlineDateField findingMonth = new InlineDateField("Finding Month upto :");
	private InlineDateField compareMonth = new InlineDateField("Compared Month upto :");

	private SimpleDateFormat dtf = new SimpleDateFormat("MMMM, yyyy");
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat checkDateFormat = new SimpleDateFormat("MM");
	
	private OptionGroup RadioBtnGroup = new OptionGroup("",option);
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});

	public CashFlowYearly(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setWidth("500px");
		this.setResizable(false);
		this.setCaption("CASH FLOW STATEMENT :: "+this.sessionBean.getCompany());

		findingMonth.setValue(new Date());
		findingMonth.setDateFormat("dd-MM-yy");
		findingMonth.setResolution(InlineDateField.RESOLUTION_MONTH);
		findingMonth.setImmediate(true);
		formLayout.addComponent(findingMonth);

		compareMonth.setValue(new Date());
		compareMonth.setDateFormat("dd-MM-yy");
		compareMonth.setResolution(InlineDateField.RESOLUTION_MONTH);
		compareMonth.setImmediate(true);
		formLayout.addComponent(compareMonth);

		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setValue("PDF");
		RadioBtnGroup.setStyleName("horizontal");
		formLayout.addComponent(RadioBtnGroup);

		btnL.setSpacing(true);
		btnL.addComponent(button);

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(btnL);
		mainLayout.addComponent(formLayout);
		this.addComponent(mainLayout);

		mainLayout.setMargin(true);
		buttonActionAdd();
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
		if(chkDate())		
		{
			showReport();
		}
	}

	private void showReport()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(compareMonth.getValue())+"')").list().iterator().next().toString();

			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();

			session.createSQLQuery("exec prcCashFlowStatementmonthly '"+ new SimpleDateFormat("MM").format(findingMonth.getValue()) +"','"+ new SimpleDateFormat("yyyy").format(findingMonth.getValue()) +"','"+ new SimpleDateFormat("MM").format(compareMonth.getValue()) +"','"+ new SimpleDateFormat("yyyy").format(findingMonth.getValue()) +"','"+sessionBean.getCompanyId()+"'").executeUpdate();

			tx.commit();
			HashMap hm = new HashMap();

			hm.put("comName",sessionBean.getCompany());
			hm.put("address",sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("MonthYear", dtf.format(findingMonth.getValue()).toString());
			hm.put("MonthYear1", dtf.format(compareMonth.getValue()).toString());

			System.out.println(findingMonth.getValue().toString());

			//if(subjectType.getValue().toString().equals(""))
			{
				Window win = new ReportViewer(hm,"report/account/mis/rptCashflowStatement.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);
			
				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				win.setCaption("CASH FLOW STATEMENT :: "+sessionBean.getCompany());
			}

		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean chkDate()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String f = session.createSQLQuery("Select [dbo].[dateSelect]('"+dtfYMD.format(findingMonth.getValue())+"','"+dtfYMD.format(compareMonth.getValue())+"')").list().iterator().next().toString();

		if(f.equals("1"))
		{
			if(Double.parseDouble(checkDateFormat.format(compareMonth.getValue()).toString()) < 
					Double.parseDouble(checkDateFormat.format(findingMonth.getValue()).toString()))
			{
				return true;
			}
			else
			{
				this.getParent().showNotification("Warning!","Finding month should greater than compare month",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else
		{
			this.getParent().showNotification("Warning!","From/To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}
}
