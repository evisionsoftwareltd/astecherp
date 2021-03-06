package acc.reportmodule.mis;

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
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;

public class DailyTransactionRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout verLayout = new VerticalLayout();
	private GridLayout grid = new GridLayout(2,1);
	private DateField date = new DateField("Date:"); 
	private VerticalLayout space = new VerticalLayout();
	//private NativeButton showRptBtn = new NativeButton("Show Report");
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	public DailyTransactionRpt(SessionBean sessionBean){
		this.sessionBean = sessionBean;
		this.setCaption("DAILY TRANSACTION SUMMARY :: "+this.sessionBean.getCompany());
		this.setWidth("480px");
		this.setResizable(false);

		formLayout.addComponent(date);

		formLayout.addComponent(button);
		formLayout.setSpacing(true);

		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);

		verLayout.addComponent(space);
		verLayout.setSpacing(true);
		space.setHeight("42px");
		space.setSpacing(true);

		grid.addComponent(formLayout,0,0);
		grid.addComponent(verLayout,1,0);
		mainLayout.addComponent(grid);
		mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
		this.addComponent(mainLayout);
		Component comp[] = {date, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		setButtonAction();
		date.focus();
	}

	private void setButtonAction()
	{
		button.btnPreview.addListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event) 
			{
				showReport();
			}
		});

		button.btnExit.addListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void showReport()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
			//hm.put("fromDate", dt);
			//	tx.commit();
		
			HashMap hm = new HashMap();
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("ddate",date.getValue());
			hm.put("companyId",sessionBean.getCompanyId());

			Window win = new ReportPdf(hm,"report/account/mis/dailyTransaction.jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			
			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);
			win.setCaption("DAILY TRANSACTION SUMMARY REPORT :: "+sessionBean.getCompany());

			//System.out.println(clientId.getValue().toString().equals("All")+cid);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}		
	}
}
