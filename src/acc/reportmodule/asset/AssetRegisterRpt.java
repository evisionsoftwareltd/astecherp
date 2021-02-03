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

public class AssetRegisterRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;	
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	private DateField fromDate = new DateField("From Date:");
	private DateField toDate = new DateField("To Date:");
	private String lcw = "130px";
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtfDMY = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");

	public AssetRegisterRpt(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setWidth("330px");
		this.setResizable(false);

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);

		this.setCaption("ASSET REGISTER :: "+this.sessionBean.getCompany());

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

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(btnL);
		mainLayout.addComponent(formLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		Component comp[] = {fromDate, toDate, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		buttonActionAdd();
		fromDate.focus();
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
//		if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
//				&&
//				(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
//		{
//			if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())))
//			{
		if (chkDate())
				showReport();
//			}
//			else
//			{
//				showNotification("Warning ","From date can not be greater than to date.",Notification.TYPE_WARNING_MESSAGE);
//			}
//		}
//		else
//		{
//			showNotification("Warning ","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
//		}	
	}
	private void showReport()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
				tx.commit();
				
			HashMap hm = new HashMap();

			hm.put("fromTo", dtfDMY.format(fromDate.getValue())+" to "+dtfDMY.format(toDate.getValue()));
			hm.put("start", dtfYMD.format(fromDate.getValue()));
			hm.put("end", dtfYMD.format(toDate.getValue()));
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId", sessionBean.getCompanyId());
		//	System.out.println(fromDate.getValue());
			hm.put("fromDate", dtfYMD.format(fromDate.getValue()));
			hm.put("toDate", dtfYMD.format(toDate.getValue()));	
			//System.out.println(dtfYMD.format(toDate.getValue()));
			hm.put("path","report/account/asset/");
			
			Window win = new ReportPdf(hm,"report/account/asset/AssetRegister.jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			this.getParent().getWindow().addWindow(win);
			win.setCaption("ASSET REGISTER :: "+sessionBean.getCompany());

		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private boolean chkDate()
	{
		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
		//System.out.println(f);
			if (f.equals("1"))	
				//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue()))>= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
				//					&&
				//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue()))<= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			{
				return true;
			}
			else
			{
				this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else
		{
			this.getParent().showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}
}
