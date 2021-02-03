package com.example.rawMaterialReport;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
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

public class DateWisePurchaseRpt extends Window{

	SessionBean sessionBean;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();
	int type=0;
	private Label lblFDate;
	private Label lblToDate;
	private Label lblLine;
	private Label lblCategoryType=new Label();
	private ComboBox cmbcategorytype=new ComboBox();
	private ComboBox cmbsupplier=new ComboBox();
	private Label lblsupplier=new Label();
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	private AbsoluteLayout mainLayout;

	public DateWisePurchaseRpt(SessionBean sessionBean, String string){

		this.sessionBean=sessionBean;
		this.setCaption("DATE WISE PURCHASE STATEMENT::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
		categoryType();
		cmbcategorytype.focus();
	}


	private void setEventAction() {

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) 
			{
				if(cmbcategorytype.getValue()!=null)
				{
					if(cmbsupplier.getValue()!=null)
					{
						reportView();	
					}
					else
					{
						showNotification("Please Select Supplier Name",Notification.TYPE_WARNING_MESSAGE);	
					}

				}
				else
				{
					showNotification("Please Select Category Type",Notification.TYPE_WARNING_MESSAGE);
				}

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

		cmbcategorytype.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{

				if(cmbcategorytype.getValue()!=null)
				{
					supplierDataLoad();
				}

			}
		});
	}

	private void categoryType()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		String query = " Select  distinct 0, vCategoryType from tbRawItemInfo where vRawItemCode in (select vRawItemCode from tbRawPurchaseDetails)";


		System.out.println(query);

		cmbcategorytype.removeAllItems();
		List<?> list = session.createSQLQuery(query).list();		
		for(Iterator<?> iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbcategorytype.addItem(element[1]);
			cmbcategorytype.setItemCaption(element[1], element[1].toString());
		}
	}


	private void supplierDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();


		String query = "select distinct  a.SupplierId, isnull((select supplierName from tbSupplierInfo where supplierId like a.SupplierId ),'')  supplierName  from tbRawPurchaseInfo a inner join tbRawPurchaseDetails b  on a.ReceiptNo=b.ReceiptNo inner join tbRawItemInfo c on c.vRawItemCode=b.ProductID where c.vCategoryType like '"+cmbcategorytype.getValue().toString()+"' " 
				+"and  SupplierId not like '%AL%' " 
				+"union "
				+"select distinct  a.SupplierId,isnull((select Ledger_Name from tbLedger where Ledger_Id=a.SupplierId ) ,'') supplierName  from tbRawPurchaseInfo a inner join tbRawPurchaseDetails b  on a.ReceiptNo=b.ReceiptNo inner join tbRawItemInfo c on c.vRawItemCode=b.ProductID where c.vCategoryType like '"+cmbcategorytype.getValue().toString()+"' "
				+"and  SupplierId  like '%AL%' " ;



		System.out.println(query);

		cmbsupplier.removeAllItems();
		List<?> list = session.createSQLQuery(query).list();		
		for(Iterator<?> iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbsupplier.addItem(element[0]);
			cmbsupplier.setItemCaption(element[0], element[1].toString());
		}
	}




	private AbsoluteLayout buildMainLayout() {
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);

		mainLayout.setWidth("550px");
		mainLayout.setHeight("220px");
		mainLayout.setMargin(false);

		//top-level component properties
		setWidth("580px");
		setHeight("300px");

		lblCategoryType = new Label();
		lblCategoryType.setImmediate(false);
		lblCategoryType.setWidth("-1px");
		lblCategoryType.setHeight("-1px");
		lblCategoryType.setValue("Parent Type:");
		mainLayout.addComponent(lblCategoryType, "top:20.0px;left:52.0px;");

		// cmbSection
		cmbcategorytype = new ComboBox();
		cmbcategorytype.setImmediate(true);
		cmbcategorytype.setWidth("260px");
		cmbcategorytype.setHeight("24px");
		cmbcategorytype.setNullSelectionAllowed(false);
		cmbcategorytype.setNewItemsAllowed(false);
		cmbcategorytype.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbcategorytype, "top:18.0px;left:145.0px;");

		lblsupplier = new Label();
		lblsupplier.setImmediate(false);
		lblsupplier.setWidth("-1px");
		lblsupplier.setHeight("-1px");
		lblsupplier.setValue("Supplier Name :");
		mainLayout.addComponent(lblsupplier, "top:46.0px;left:52.0px;");

		// cmbSection
		cmbsupplier = new ComboBox();
		cmbsupplier.setImmediate(true);
		cmbsupplier.setWidth("320px");
		cmbsupplier.setHeight("24px");
		cmbsupplier.setNullSelectionAllowed(false);
		cmbsupplier.setNewItemsAllowed(false);
		cmbsupplier.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbsupplier, "top:44.0px;left:145.0px;");


		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("Form Date :");
		mainLayout.addComponent(lblFDate, "top:72.0px;left:52.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("130px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:70.0px;left:145.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:98.0px;left:52.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("130px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:96.0px;left:145.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:136.0px; left:130.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setValue("_______________________________________________________________________");
		mainLayout.addComponent(lblLine, "top:146.0px;left:25.0px;");

		previewButton.setWidth("95px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:167.opx; left:160.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:167.opx; left:250.0px");

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


			query= "select * from " 
					+"( "
					+"select rpi.MrrNo,rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate, "
					+"CAST(si.supplierId as varchar(120))supplierId , si.supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode, "
					+"PI.vRawItemName,  pi.vUnitName,poi.poDate,pi.vCategoryType from tbRawPurchaseInfo rpi "
					+"inner join tbSupplierInfo si  on rpi.SupplierId=si.supplierId inner join tbRawPurchaseDetails rpd "
					+"on rpi.ReceiptNo=rpd.ReceiptNo  inner join tbRawItemInfo pi on rpd.ProductID=pi.vRawItemCode "
					+"left join tbRawPurchaseOrderInfo poi  on rpi.poNo=poi.poNo where CONVERT(Date, rpi.Date,105) between "
					+" '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' and pi.vCategoryType like '"+cmbcategorytype.getValue().toString()+"' " 
					+"and si.supplierId like '"+cmbsupplier.getValue().toString()+"' and rpi.supplierId not like '%AL%' "

							+"union all "

							+"select rpi.MrrNo, rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate, "
							+"si.Ledger_Id, si.Ledger_Name,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode, "
							+"PI.vRawItemName,  pi.vUnitName,poi.poDate,pi.vCategoryType from tbRawPurchaseInfo rpi "
							+"inner join tbLedger si  on rpi.SupplierId=si.Ledger_Id inner join tbRawPurchaseDetails rpd "
							+"on rpi.ReceiptNo=rpd.ReceiptNo  inner join tbRawItemInfo pi on rpd.ProductID=pi.vRawItemCode "
							+"left join tbRawPurchaseOrderInfo poi  on rpi.poNo=poi.poNo where CONVERT(Date, rpi.Date,105) between "
							+"'"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' and pi.vCategoryType like '"+cmbcategorytype.getValue().toString()+"' " 
							+"and si.Ledger_Id like '"+cmbsupplier.getValue().toString()+"' and rpi.supplierId  like '%AL%' "

							+") as temp order by temp.supplierId   , temp.ReceiptNo ";

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
