package com.example.sparePartsReport;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RawRequisitionRegister extends Window{

	SessionBean sessionBean;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();
	int type=0;
	private Label lblFDate;
	private Label lblToDate;
	private Label lblLine;
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	private AbsoluteLayout mainLayout;
	
	public RawRequisitionRegister(SessionBean sessionBean, String string){
		
		this.sessionBean=sessionBean;
		this.setCaption("DATE WISE PURCHASE STATEMENT::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
	}

	private void setEventAction() {
		
		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) 
			{
				
				reportView();	
				
			}
		});
		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		chkpdf.addListener(new ValueChangeListener()
		{
			
			public void valueChange(ValueChangeEvent event)
			{
				
				if(chkpdf.booleanValue()==true)
					chkother.setValue(false);
				else
					chkother.setValue(true);
				
			}
		});
		
		chkother.addListener(new ValueChangeListener()
		{
			
			public void valueChange(ValueChangeEvent event)
			{
				
				if(chkother.booleanValue()==true)
					chkpdf.setValue(false);
				else
					chkpdf.setValue(true);
				
			}
		});
		
	}

	private AbsoluteLayout buildMainLayout() {
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("400px");
		mainLayout.setHeight("150px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("430px");
		setHeight("230px");
		
		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("Form Date :");
		mainLayout.addComponent(lblFDate, "top:20.0px;left:52.0px;");
		
		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("130px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:18.0px;left:120.0px;");
		
		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:50.0px;left:52.0px;");
		
		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("130px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:48.0px;left:120.0px;");
		
		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:70.0px; left:120.0px");
		
		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setValue("_______________________________________________________________________");
		mainLayout.addComponent(lblLine, "top:90.0px;left:25.0px;");
		
		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:120.opx; left:100.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:120.opx; left:180.0px");
		
		return mainLayout;
	}
	
	private void reportView()
	{	
		String query=null;
		Transaction tx=null;
		
		if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;
		
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromdate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));

			/*query= "select rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate,si.supplierId, si.supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.ProductCode,PI.ProductName,  pi.Unit,poi.poDate "
					+"from tbRawPurchaseInfo rpi inner join tbSupplier_Info si on rpi.SupplierId=si.supplierId "
					+"inner join tbRawPurchaseDetails rpd on rpi.ReceiptNo=rpd.ReceiptNo "
					+"inner join tbRawProductInfo pi on rpd.ProductID=pi.ProductCode "
					+"left join tbRawPurchaseOrderInfo poi on rpi.poNo=poi.poNo "
					+"where CONVERT(Date, rpi.Date,105) between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' order by cast(si.supplierId as int)   ";*/

			query="select rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate, "
				+" si.supplierId, si.supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode,"
				+" PI.vRawItemName,  pi.vUnitName,poi.poDate from tbRawPurchaseInfo rpi inner join tbSupplierInfo si "
				+" on rpi.SupplierId=si.supplierId inner join tbRawPurchaseDetails rpd on rpi.ReceiptNo=rpd.ReceiptNo "
				+" inner join tbRawItemInfo pi on rpd.ProductID=pi.vRawItemCode left join tbRawPurchaseOrderInfo poi "
				+" on rpi.poNo=poi.poNo where CONVERT(Date, rpi.Date,105) between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' and pi.vflag='New' "
				+" order by cast(si.supplierId as int)   , rpi.ReceiptNo";
			
			hm.put("sql", query);
			
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){

				Window win = new ReportViewerNew(hm,"report/raw/rptDateWisePurchaseStatement.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}

}
