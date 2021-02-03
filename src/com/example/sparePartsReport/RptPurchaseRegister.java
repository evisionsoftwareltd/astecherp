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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RptPurchaseRegister extends Window{

	SessionBean sessionBean;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();
	int type=0;
	private Label lblFDate;
	private Label lblToDate;
	private Label lblLine;
	private Label lblParentType=new Label();
	private ComboBox cmbParentType=new ComboBox();
	private Label lblSupplier=new Label();
	private ComboBox cmbSupplier=new ComboBox();
	private Label lblItem=new Label("Item Name: ");
	private ComboBox cmbItem=new ComboBox();
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;
	private CheckBox chkAllSupplier;
	private CheckBox chkAllItem;
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	private AbsoluteLayout mainLayout;
	
	
		
 public RptPurchaseRegister(SessionBean sessionBean, String string) 
 {
	
			this.sessionBean=sessionBean;
			this.setCaption("PURCHASE REGISTER::"+sessionBean.getCompany());
			this.setResizable(false);
			buildMainLayout();
			this.addComponent(mainLayout);
			setEventAction();
			parentType();
	}
	
	
	private void setEventAction() {
		
		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) 
			{
				if(cmbParentType.getValue()!=null)
				{
					if(cmbSupplier.getValue()!=null || chkAllSupplier.booleanValue())
					{
						if(cmbItem.getValue()!=null || chkAllItem.booleanValue())
						{
							reportView();	
						}
						else
						{
							showNotification("Please Select Item Name",Notification.TYPE_WARNING_MESSAGE);		
						}
						
							
					}
					else
					{
						showNotification("Please Select Supplier Name",Notification.TYPE_WARNING_MESSAGE);	
					}
						
				}
				else
				{
					showNotification("Please Select Parent Type",Notification.TYPE_WARNING_MESSAGE);
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
		
		cmbParentType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
			   
				if(cmbParentType.getValue()!=null)
				{
					 supplierDataLoad();
				}
				
			}
		});
		
		
		cmbSupplier.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
			   
				if(cmbParentType.getValue()!=null && cmbSupplier.getValue()!=null)
				{
					String supplier =cmbSupplier.getValue().toString();
					ItemDataLoad(supplier); 
				}
				
			}
		});
		
		
		chkAllSupplier.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				 if(chkAllSupplier.booleanValue())
				 {
					 cmbSupplier.setValue(null);
					 cmbSupplier.setEnabled(false);
					 String supplier="%";
					 ItemDataLoad(supplier); 
				 }
				 
				 else
				 {
					 cmbSupplier.setEnabled(true);
					 cmbItem.removeAllItems();
				 }
			}
		});
		
	chkAllItem.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				 if(chkAllItem.booleanValue())
				 {
					 cmbItem.setValue(null);
					 cmbItem.setEnabled(false);
					 //ItemDataLoad(); 
				 }
			}
		});
		
	}
	
	protected void ItemDataLoad(String supplier)
	{
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct  c.vRawItemCode,c.vRawItemName  from tbRawPurchaseInfo a "
                       +"inner join tbRawPurchaseDetails b on a.ReceiptNo=b.ReceiptNo inner join "
                       +"tbRawItemInfo c on c.vRawItemCode=b.ProductID where c.vCategoryType like '"+cmbParentType.getValue().toString()+"' and a.SupplierId like '"+supplier+"' " ;

			
			
			List list=session.createSQLQuery(sql).list();
			
			cmbItem.removeAllItems();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbItem.addItem(element[0]);
				cmbItem.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	
	}


	private void parentType()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		String query ="select distinct 0,  c.vCategoryType  from tbRawPurchaseInfo a  "
				      +"inner join "
				      +"tbRawPurchaseDetails b on a.ReceiptNo=b.ReceiptNo "  
				      +"inner join "
				      +"tbRawItemInfo c on c.vRawItemCode=b.ProductID ";

		//String query = "  select  distinct 0,vCategoryType from tbRawItemCategory";

		System.out.println(query);

		cmbParentType.removeAllItems();
		List<?> list = session.createSQLQuery(query).list();		
		for(Iterator<?> iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbParentType.addItem(element[1]);
			cmbParentType.setItemCaption(element[1], element[1].toString());
		}
	}
	
	
	private void supplierDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		String query = "select distinct  a.SupplierId,(select supplierName from tbSupplierInfo where supplierId like a.SupplierId ) supplierName  from tbRawPurchaseInfo a "
				       +"inner join tbRawPurchaseDetails b  on a.ReceiptNo=b.ReceiptNo inner join tbRawItemInfo c on c.vRawItemCode=b.ProductID "
				       +"where c.vCategoryType like '"+cmbParentType.getValue().toString()+"' ";

		
		System.out.println(query);

		cmbSupplier.removeAllItems();
		List<?> list = session.createSQLQuery(query).list();		
		for(Iterator<?> iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbSupplier.addItem(element[0]);
			cmbSupplier.setItemCaption(element[0], element[1].toString());
		}
	}
	

	
	
	private AbsoluteLayout buildMainLayout() {
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		
		//mainLayout.setWidth("550px");
		mainLayout.setHeight("240px");
		mainLayout.setMargin(false);

		//top-level component properties
		setWidth("580px");
		//setHeight("355px");
		
		lblParentType = new Label();
		lblParentType.setImmediate(false);
		lblParentType.setWidth("-1px");
		lblParentType.setHeight("-1px");
		lblParentType.setValue("Parent Type:");
		mainLayout.addComponent(lblParentType, "top:20.0px;left:52.0px;");

		// cmbSection
		cmbParentType = new ComboBox();
		cmbParentType.setImmediate(true);
		cmbParentType.setWidth("260px");
		cmbParentType.setHeight("24px");
		cmbParentType.setNullSelectionAllowed(false);
		cmbParentType.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbParentType, "top:18.0px;left:145.0px;");
		
		lblSupplier = new Label();
		lblSupplier.setImmediate(false);
		lblSupplier.setWidth("-1px");
		lblSupplier.setHeight("-1px");
		lblSupplier.setValue("Supplier Name :");
		mainLayout.addComponent(lblSupplier, "top:46.0px;left:52.0px;");

		// cmbSection
		cmbSupplier = new ComboBox();
		cmbSupplier.setImmediate(true);
		cmbSupplier.setWidth("320px");
		cmbSupplier.setHeight("24px");
		cmbSupplier.setNullSelectionAllowed(false);
		cmbSupplier.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbSupplier, "top:44.0px;left:145.0px;");
		
		chkAllSupplier = new CheckBox("");
		chkAllSupplier.setCaption("All");
		chkAllSupplier.setWidth("-1px");
		chkAllSupplier.setHeight("24px");
		chkAllSupplier.setImmediate(true);
		mainLayout.addComponent(chkAllSupplier, "top:44.0px;left:470.0px;");
		
		lblItem = new Label();
		lblItem.setImmediate(false);
		lblItem.setWidth("-1px");
		lblItem.setHeight("-1px");
		lblItem.setValue("Item Name :");
		mainLayout.addComponent(lblItem, "top:72.0px;left:52.0px;");

		// cmbSection
		cmbItem = new ComboBox();
		cmbItem.setImmediate(true);
		cmbItem.setWidth("320px");
		cmbItem.setHeight("24px");
		cmbItem.setNullSelectionAllowed(false);
		cmbItem.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbItem, "top:70.0px;left:145.0px;");
		
		chkAllItem = new CheckBox("");
		chkAllItem.setCaption("All");
		chkAllItem.setWidth("-1px");
		chkAllItem.setHeight("24px");
		chkAllItem.setImmediate(true);
		mainLayout.addComponent(chkAllItem, "top:70.0px;left:470.0px;");
		
		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("Form Date :");
		mainLayout.addComponent(lblFDate, "top:98.0px;left:52.0px;");
		
		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("130px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:96.0px;left:145.0px;");
		
		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:124.0px;left:52.0px;");
		
		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("130px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:122.0px;left:145.0px;");
		
		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:150.0px; left:200.0px");
		
		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setValue("_______________________________________________________________________");
		mainLayout.addComponent(lblLine, "top:166.0px;left:25.0px;");
		
		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:186.opx; left:180.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:186.opx; left:260.0px");
		
		return mainLayout;
	}
	
	
	
	private void reportView()
	{	
		String query=null;
		Transaction tx=null;
		
		
		String parent="";
		String supplier="";
		String item="";
		
		if(cmbParentType.getValue()!=null)
		{
			parent=cmbParentType.getValue().toString();
		}
		if(cmbSupplier.getValue()!=null)
		{
			supplier=cmbSupplier.getValue().toString();
		}
		
		if(chkAllSupplier.booleanValue())
		{
			supplier="%";	
		}
		
		if(cmbItem.getValue()!=null)
		{
		  item=cmbItem.getValue().toString();	
		}
		
		if(chkAllItem.booleanValue())
		{
			item="%";	
		}
		
		
		
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

			query="select rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate,  si.supplierId,"
					+ "si.supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode, PI.vRawItemName,  pi.vUnitName, "
					+ "poi.poDate,pi.vCategoryType from tbRawPurchaseInfo rpi inner join tbSupplierInfo si  on rpi.SupplierId=si.supplierId  "
					+ "inner join tbRawPurchaseDetails rpd on rpi.ReceiptNo=rpd.ReceiptNo  inner join tbRawItemInfo pi on "
					+ "rpd.ProductID=pi.vRawItemCode left join tbRawPurchaseOrderInfo poi  on rpi.poNo=poi.poNo"
					+ " where CONVERT(Date, rpi.Date,105) between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  and pi.vCategoryType like '"+parent+"' and si.supplierId "
					+ "like '"+supplier+"' and pi.vRawItemCode like '"+item+"'  "
					+ "order by cast(si.supplierId as int) ,pi.vRawItemCode,CONVERT(Date, rpi.Date,105)  ,rpi.ReceiptNo ";
			
			hm.put("sql", query);
			
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{

				Window win = new ReportViewerNew(hm,"report/raw/rptPurchaseRegister.jasper",
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
