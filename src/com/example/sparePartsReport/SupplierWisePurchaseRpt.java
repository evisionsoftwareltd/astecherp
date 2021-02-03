package com.example.sparePartsReport;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class SupplierWisePurchaseRpt extends Window{

	SessionBean sessionBean;
	private Label lblSupplier=new Label("Supplier Name: ");
	private ComboBox cmbSupplier=new ComboBox();
	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();

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
	private HashMap hmnew = new HashMap();

	private AbsoluteLayout mainLayout;

	public SupplierWisePurchaseRpt(SessionBean sessionBean, String string){
		this.sessionBean=sessionBean;
		this.setCaption("SUPPLIER WISE PURCHASE STATEMENT::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		cmbSupplierDataLoad();
		this.addComponent(mainLayout);
		setEventAction();
		cmbSupplier.focus();
		
	}

	private void cmbSupplierDataLoad() {
		cmbSupplier.removeAllItems();
		Transaction tx = null;
		try {
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String sql= "select distinct temp.type, temp.SupplierId,temp.supplierName from " 
					    +"( "
					    +"select '1' as type, a.supplierId,b.supplierName from tbRawPurchaseInfo a inner join tbSupplierInfo b on a.supplierId=b.supplierId "
					    +"where a.SupplierId not like '%AL%' and a.vflag='New'"
					    +"union all "
					    +"select '2' as type, a.supplierId,b.Ledger_Name from tbRawPurchaseInfo a inner join tbLedger b on a.supplierId=b.Ledger_Id "
					    +"where a.SupplierId  like '%AL%' and a.vflag='New'"
					    +") as temp order by temp.type ";

			List list = session.createSQLQuery(sql).list();
			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				hmnew.put(element[1].toString(),element[0].toString());
				cmbSupplier.addItem(element[1].toString());
				cmbSupplier.setItemCaption(element[1].toString(), element[2].toString());

			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
	}
	private void setEventAction() {

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(cmbSupplier.getValue()!=null)
				{
					reportView();
				}
				else
					getParent().showNotification("Please Select Supplier Name", Notification.TYPE_WARNING_MESSAGE);
			}
		});
		
		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAll.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbSupplier.setEnabled(false);
					cmbSupplier.setValue(null);

				}
				else{
					cmbSupplier.setEnabled(true);
					cmbSupplier.focus();
				}
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

	         
			int a= Integer.parseInt((String) hmnew.get(cmbSupplier.getValue().toString()));
			
		
			 if(a==1)
			 {
				 query="select rpi.MrrNo,rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate,"
							+" si.supplierId, si.supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode,PI.vRawItemName," 
							+" pi.vUnitName,poi.poDate,si.address from tbRawPurchaseInfo rpi inner join tbSupplierInfo si on rpi.SupplierId=si.supplierId" 
							+" inner join tbRawPurchaseDetails rpd on rpi.ReceiptNo=rpd.ReceiptNo inner join tbRawItemInfo pi "
							+" on rpd.ProductID=pi.vRawItemCode left join tbRawPurchaseOrderInfo poi on rpi.poNo=poi.poNo "
							+" where CONVERT(Date, rpi.Date,105) between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"'  and rpi.vflag='New' and  si.supplierId like '"+cmbSupplier.getValue().toString()+"' and rpi.supplierId not like '%AL%'  order by rpi.Date";
					 
			 }
			 else
			 {
			    query=  "select rpi.MrrNo,rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate, "
			    		+"si.Ledger_Id supplierId, si.Ledger_Name supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode,PI.vRawItemName, " 
			    		+"pi.vUnitName,poi.poDate,''address from tbRawPurchaseInfo rpi inner join tbLedger si on rpi.SupplierId=si.Ledger_Id "
			    		+"inner join tbRawPurchaseDetails rpd on rpi.ReceiptNo=rpd.ReceiptNo inner join tbRawItemInfo pi " 
			    		+"on rpd.ProductID=pi.vRawItemCode left join tbRawPurchaseOrderInfo poi on rpi.poNo=poi.poNo " 
			    		+"where CONVERT(Date, rpi.Date,105) between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"'   and rpi.vflag='New' and  si.Ledger_Id like '"+cmbSupplier.getValue().toString()+"' and " 
			    		+"rpi.SupplierId like '%AL%'  order by rpi.Date "  ;	 
			 }
			 
			
			System.out.println("query is"+query);
			

			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/raw/rptSupplierWisePurchaseStatement.jasper",
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
	
	private AbsoluteLayout buildMainLayout() {

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("460px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		lblSupplier = new Label();
		lblSupplier.setImmediate(false);
		lblSupplier.setWidth("-1px");
		lblSupplier.setHeight("-1px");
		lblSupplier.setValue("Supplier Name:");
		mainLayout.addComponent(lblSupplier, "top:16.0px;left:40.0px;");

		cmbSupplier = new ComboBox();
		cmbSupplier.setImmediate(false);
		cmbSupplier.setWidth("260px");
		cmbSupplier.setHeight("24px");
		cmbSupplier.setNullSelectionAllowed(true);
		cmbSupplier.setNewItemsAllowed(false);
		cmbSupplier.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbSupplier, "top:15.0px;left:130.0px;");

		chkAll.setImmediate(true);
		chkAll.setWidth("30px");
		chkAll.setHeight("15px");
		mainLayout.addComponent( chkAll, "top:18.0px;left:395.0px;");

		lblAll.setWidth("-1px");
		lblAll.setHeight("-1px");
		lblAll.setValue("All");
		mainLayout.addComponent( lblAll, "top:18.0px;left:415.0px;");

		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:44.0px;left:62.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("150px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:44.0px;left:130.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:70.0px;left:62.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("150px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:70.0px;left:130.0px;");
		
		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:95.0px; left:130.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setValue("____________________________________________________________________________________________________");
		mainLayout.addComponent(lblLine, "top:120.0px;left:25.0px;");

		previewButton.setWidth("95px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:155.opx; left:130.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:155.opx; left:220.0px");
		
		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
