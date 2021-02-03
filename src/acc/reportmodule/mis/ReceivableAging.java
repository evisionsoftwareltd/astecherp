package acc.reportmodule.mis;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.asm.Label;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class ReceivableAging extends Window 
{
	SessionBean sessionBean;
	private PopupDateField fromDate=new PopupDateField("From Date :");
	private PopupDateField toDate=new PopupDateField("To Date :");
	private NativeButton btnView=new NativeButton("View");
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");	
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	private ComboBox cmbConsignee=new ComboBox("Consignee Name :");
	
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout frmLayout=new FormLayout();
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");
	String title;
	public ReceivableAging(SessionBean sessionBean)
	{
		String title="receivableAging";
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.title = title;		
		
		if(title.equals("CMWDate"))
		{
			this.setCaption("Invoice summary Between Date :: "+sessionBean.getCompany());
			this.setWidth("350px");
		}
		
		if(title.equals("CWDate"))
		{
			this.setCaption("Consignee Wise Invoice Summary :: "+sessionBean.getCompany());
			this.setWidth("400px");
		}
		
		if(title.equals("salesbd"))
		{
			this.setCaption("Sales Satetement Between Date :: "+sessionBean.getCompany());
			this.setWidth("400px");
		}
		
		if(title.equals("receivableAging"))
		{
			this.setCaption("Receivable Aging :: "+sessionBean.getCompany());
			this.setWidth("400px");
		}
		
		fromDate.setWidth("200px");
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		
		toDate.setWidth("200px");
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		
		btnView.setWidth("80");
		btnView.setHeight("30px");
		frmLayout.addComponent(fromDate);
		if (!title.equals("reciveableAging"))
		{
			frmLayout.addComponent(toDate);
		}
		else
		{
			fromDate.setCaption("As On Date :");
		}
		cmbConsignee.setWidth("220px");
		cmbConsignee.setNullSelectionAllowed(false);
		if(title.equals("CMWDate"))//|| title.equals("SalesStatement") || title.equals("reciveableAging"))
		{}
		else
		{
			frmLayout.addComponent(cmbConsignee);
			consigneeFill();
		}
		frmLayout.addComponent(btnView);
		mainLayout.addComponent(frmLayout);
		//frmLayout.addComponent(fouLayout);
		mainLayout.setComponentAlignment(frmLayout, Alignment.MIDDLE_CENTER);
		btnView.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{				
				if (dateCompare())
				{
//					if (cmbConsignee.getValue() != null)
//					{
						printPreviewEvent();
//					}
//					else
//						getParent().showNotification("Warning :","Please Select Consignee Name.",Notification.TYPE_WARNING_MESSAGE);
				}
				else
					getParent().showNotification("Warning :","Please Check Your Date Range.",Notification.TYPE_WARNING_MESSAGE);
							
			}
		});		
		addComponent(mainLayout);
	}
	
	private void consigneeFill()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String query = "select Ledger_Id, vConsignee from tbConsignee";
		List list = session.createSQLQuery(query).list();
		cmbConsignee.addItem("0");
		cmbConsignee.setItemCaption("0", "All");
		cmbConsignee.setValue("0");
		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbConsignee.addItem(element[0].toString());
			cmbConsignee.setItemCaption(element[0].toString(), element[1].toString());
		}
		
	}
	
	private void printPreviewEvent()
	{
		try
		{
			HashMap hm = new HashMap();
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String sql ="";
			String path = "";
			

				
			System.out.println(title);
			if (title == "CMWDate")
			{
				sql = "select * from dbo.funMonthWiseSummary ('"+ dateformat.format(fromDate.getValue())+"', '"+dateformat.format(toDate.getValue())+"', '"+ sessionBean.getCompanyId() +"')";
				
				hm.put("sql",sql);
				hm.put("date"," From : " + dtf.format(fromDate.getValue()) + "        To : " + dtf.format(toDate.getValue()));
				hm.put("companyname",sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				path = "rptMonthWiseSummary.jasper";
			}
			
			if (title == "CWDate")
			{
				String ledgerid = "";
				if (cmbConsignee.getValue().toString() == "0")
					ledgerid = "%";
				else
					ledgerid = cmbConsignee.getValue().toString();
				
				sql = "select month(i.InvoiceDate) sl, DATENAME(MM,i.InvoiceDate) [month],c.vConsignee, sum(i.Total)Net, SUM(i.VatAmount)Vat, SUM( i.Total + i.VatAmount) Total from tbInvoiceInfo i inner join tbConsignee c on i.ConsigneeLedger = c.Ledger_Id where i.companyId = '"+ sessionBean.getCompanyId() +"' AND i.InvoiceDate between '"+ dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"' and ConsigneeLedger like '"+ ledgerid +"' group by month(i.InvoiceDate), c.vConsignee, DATENAME(MM,i.InvoiceDate) order by month(i.InvoiceDate)";
				
				hm.put("sql",sql);
				hm.put("date"," From : " + dtf.format(fromDate.getValue()) + "        To : " + dtf.format(toDate.getValue()));
				hm.put("companyname",sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				path = "rptConsigneeWiseSummary.jasper";
			}
			
			if(title.equals("salesbd"))
			{
				String ledgerid = "";
				if (cmbConsignee.getValue().toString() == "0")
					ledgerid = "%";
				else
					ledgerid = cmbConsignee.getValue().toString();
				
				sql = "select c.vConsignee, sum(Total)Net,sum(VatAmount)VAT, sum(Total+ VatAmount)Total from tbInvoiceInfo i inner join tbConsignee c on i.ConsigneeLedger = c.Ledger_Id where i.companyId = '"+ sessionBean.getCompanyId() +"' AND InvoiceDate between '"+ dateformat.format(fromDate.getValue()) +"' and '"+ dateformat.format(toDate.getValue()) +"' and ConsigneeLedger like '"+ ledgerid +"' group by c.vConsignee";
				
				hm.put("sql",sql);
				hm.put("date"," From : " + dtf.format(fromDate.getValue()) + "        To : " + dtf.format(toDate.getValue()));
				hm.put("companyname",sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());				
				path = "rptSalesStatement.jasper";
			}
			
			if(title.equals("receivableAging"))
			{
				String ledgerid = "";
				
				String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
				//String voucher =  "voucher"+fsl;
				session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
					tx.commit();
					
				if (cmbConsignee.getValue().toString() == "0")
					ledgerid = "%";
				else
					ledgerid = cmbConsignee.getValue().toString();
				
				sql = "select * from dbo.funReceivableAging ('"+ dateformat.format(fromDate.getValue()) +"', '"+ ledgerid +"', '"+ sessionBean.getCompanyId() +"')";
				
				hm.put("sql",sql);
				hm.put("date"," As on Date :    " + dtf.format(fromDate.getValue()));
				hm.put("companyname",sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());				
				path = "rptReceivableAging.jasper";
			}
			
			Window win = new ReportViewer(hm,"report/account/mis/"+path, getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,getWindow().getApplication().getURL()+"VAADIN/applet/",false);
			this.getParent().getWindow().addWindow(win);			
			win.setCaption("Invoice"+ sessionBean.getCompany());			
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private boolean dateCompare()
	{
		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			System.out.println(f);
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
