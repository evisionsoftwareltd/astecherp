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
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import database.hibernate.TbCompanyInfo;

public class BankPositionRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout verLayout = new VerticalLayout();
	private GridLayout grid = new GridLayout(2,1);
	private DateField FromDate = new DateField();	
	private VerticalLayout space = new VerticalLayout();
	private Label lbl = new Label();
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	
	public BankPositionRpt(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("BANK POSITIONS :: "+this.sessionBean.getCompany());
		this.setWidth("400px");
		this.setHeight("220px");
		this.setResizable(false);
		formLayout.addComponent(FromDate);

		FromDate.setCaption("As on Date :");
		lbl.setHeight("30px");
		formLayout.addComponent(lbl);
		formLayout.addComponent(button);
		formLayout.setSpacing(true);
		
		FromDate.setValue(new java.util.Date());
		FromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		FromDate.setDateFormat("dd-MM-yy");
		FromDate.setInvalidAllowed(false);
		FromDate.setImmediate(true);
	    
	    verLayout.addComponent(space);
	    verLayout.setSpacing(true);
	    space.setHeight("42px");
	    space.setSpacing(true);
	    
	    grid.addComponent(formLayout,0,0);
	    grid.addComponent(verLayout,1,0);
	    mainLayout.addComponent(grid);
	    mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
	    this.addComponent(mainLayout);
	    Component comp[] = {FromDate, button.btnPreview};
		new FocusMoveByEnter(this, comp);
	    setButtonAction();
	    FromDate.focus();
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
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(FromDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
			//hm.put("fromDate", dt);
				tx.commit();
				
			HashMap hm = new HashMap();
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("fromDate",dt);
			hm.put("toDate",FromDate.getValue());
			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("url", this.getWindow().getApplication().getURL()+"");
			
			sessionBean.setAsOnDate(FromDate.getValue());
			Object b=this.getWindow().getApplication().getContext().getBaseDirectory();
			
			sessionBean.setUrl(getWindow().getApplication().getURL());
			
			sessionBean.setP(b);

			//Window win = new ReportPdf(hm,"report/account/mis/bankPosition.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			
			Window	win = new ReportViewer(hm,"report/account/mis/bankPosition.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
			this.getParent().getWindow().addWindow(win);
			win.setCaption("BANK POSITIONS REPORT :: "+sessionBean.getCompany());
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
