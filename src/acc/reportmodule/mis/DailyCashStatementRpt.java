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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;

public class DailyCashStatementRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private FormLayout formLayout = new FormLayout();
	private FormLayout formButton = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout verLayout = new VerticalLayout();
	private HorizontalLayout h1Layout = new HorizontalLayout();
	private HorizontalLayout h2Layout = new HorizontalLayout();
	private HorizontalLayout hMainLayout = new HorizontalLayout();
	private GridLayout grid = new GridLayout(2,1);
	private ComboBox costCentre = new ComboBox("Cost Centre:");
	private ComboBox cashList = new ComboBox("Cash A/C List:");
	private DateField date = new DateField("Date:"); 
	private VerticalLayout space = new VerticalLayout();
	private CheckBox chkAll = new CheckBox("All");
	private Label lblHeight = new Label();
	//private NativeButton showRptBtn = new NativeButton("Show Report");
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	public DailyCashStatementRpt(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("DAILY CASH STATEMENT :: "+this.sessionBean.getCompany());
		this.setWidth("480px");
		this.setResizable(false);
		
		formLayout.addComponent(date);
		formLayout.addComponent(costCentre);
		costCentre.setWidth("240px");
		cashList.setWidth("240px");
	    formLayout.addComponent(cashList);
	    cashAcInitialise();
		
		formLayout.addComponent(button);
		formLayout.setSpacing(true);
		
		chkAll.setImmediate(true);
		chkAll.setValue(true);
		costCentre.setEnabled(false);
		
		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkAll.getValue().equals(true))
				{
					costCentre.setEnabled(false);
					costCentre.setValue(null);
				}
				else
				{
					costCentre.setEnabled(true);
				}
			}
		});
		
		lblHeight.setHeight("26px");
		formButton.addComponent(lblHeight);
		formButton.addComponent(chkAll);

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		
		
		date.setValue(new java.util.Date());
	    date.setResolution(PopupDateField.RESOLUTION_DAY);
	    date.setDateFormat("dd-MM-yy");
	    date.setInvalidAllowed(false);
	    date.setImmediate(true);
	    
/*	    verLayout.addComponent(space);
	    verLayout.setSpacing(true);
	    space.setHeight("42px");
	    space.setSpacing(true);*/
/*	    
	    grid.addComponent(formLayout,0,0);
	    grid.addComponent(verLayout,1,0);
	    mainLayout.addComponent(grid);
	    mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);*/
	    

	    
		h1Layout.addComponent(formLayout);
		h2Layout.addComponent(formButton);
		
		hMainLayout.addComponent(h1Layout);
		hMainLayout.addComponent(h2Layout);
		mainLayout.addComponent(hMainLayout);
	    mainLayout.setComponentAlignment(hMainLayout, Alignment.MIDDLE_CENTER);

	    this.addComponent(mainLayout);
	    Component comp[] = {date, button.btnPreview};
	    new FocusMoveByEnter(this, comp);
	    setButtonAction();
	    costCenterInitialise();
	    date.focus();
	}
	
	private void cashAcInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			//if (r.equalsIgnoreCase("crp"))
			Iterator iter = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A7') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list().iterator();

			for(int i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				cashList.addItem(element[0]);
				cashList.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
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
			if(cashList.getValue() != null)
			{
				

				
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dateFormatter.format(date.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
			
			HashMap hm = new HashMap();
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId",sessionBean.getCompanyId());
			hm.put("ledgerName", cashList.getItemCaption(cashList.getValue()).toString());
			hm.put("ledgerId", cashList.getValue().toString());
			
			if(costCentre.getValue()==null)
			{
				//hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costCentre","All");
				hm.put("costId","%");
			}
			else
			{
				hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costId",costCentre.getValue()+"");
			}
			
			hm.put("onDate", dateFormatter.format(date.getValue()));
			hm.put("date",date.getValue());
			hm.put("path","report/account/mis/");
			
			Window win = new ReportPdf(hm,"report/account/mis/dailyCashStatement.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			this.getParent().getWindow().addWindow(win);
			win.setCaption("DAILY CASH STATEMENT:: "+sessionBean.getCompany());
			}
			else
				showNotification("Warning ","Please Select Cash A/C Name.", Notification.TYPE_WARNING_MESSAGE);
			
			//System.out.println(clientId.getValue().toString().equals("All")+cid);
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void costCenterInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			Iterator iter = session.createSQLQuery("SELECT id,costCentreName FROM tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' ORDER BY costCentreName").list().iterator();
									
			for(int i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				costCentre.addItem(element[0]);
				costCentre.setItemCaption(element[0], element[1].toString());
			}
			costCentre.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
