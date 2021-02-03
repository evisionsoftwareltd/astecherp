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
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.Button;
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
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;

public class LeasedAssetScheduleRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();

	private DateField asOnDate = new DateField("As on Date:");
	private ComboBox bankList = new ComboBox("Bank List:");

	private String lcw = "130px";
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	private String rpt = "";

	public LeasedAssetScheduleRpt(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setWidth("330px");
		this.setResizable(false);

		formLayout.addComponent(asOnDate);

		this.setCaption("ASSET SCHEDULE :: "+this.sessionBean.getCompany());

		asOnDate.setWidth(lcw);
		asOnDate.setValue(new java.util.Date());
		asOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		asOnDate.setDateFormat("dd-MM-yy");
		asOnDate.setInvalidAllowed(false);
		asOnDate.setImmediate(true);


		btnL.setSpacing(true);
		btnL.addComponent(button);
		//		btnL.addComponent(exitBtn);

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(btnL);
		mainLayout.addComponent(formLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		Component comp[] = {asOnDate, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		buttonActionAdd();
		asOnDate.focus();
	}

	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				preBtnAction(event);
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

	private void preBtnAction(ClickEvent event)
	{
//		if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(asOnDate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
//				&&
//				(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(asOnDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
//		{
			showReport();
//		}
//		else
//		{
//			this.getParent().showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
//		}	
	}
	private void showReport()
	{
		try
		{ 
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(asOnDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			//session = SessionFactoryUtil.getInstance().getCurrentSession();
			//tx = session.beginTransaction();
			Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
			//hm.put("fromDate", dt);
				tx.commit();
				
			HashMap hm = new HashMap();
			hm.put("fromDate",dt);
		//	System.out.println(sessionBean.getFiscalOpenDate());
			//System.out.println(dft.format(sessionBean.getFiscalOpenDate()));
			hm.put("toDate", asOnDate.getValue());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId",sessionBean.getCompanyId());
			hm.put("path","report/account/asset/");
			Window win = new ReportPdf(hm,"report/account/asset/leasedAssetSchdule.jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			this.getParent().getWindow().addWindow(win);
			win.setCaption("ASSET SCHEDULE :: "+sessionBean.getCompany());

		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
