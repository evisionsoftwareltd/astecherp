package com.example.sparePartsReport;
import java.text.SimpleDateFormat;
import java.util.*;

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
@SuppressWarnings("serial")
public class ItemWisePurcaseRegisterRpt extends Window{

	SessionBean sessionBean;
	private Label lblItem=new Label("Item Name: ");
	private ComboBox cmbItem=new ComboBox();
	private Label lblAll=new Label();
	private Label lblParentType= new Label();
	private ComboBox cmbparentype=new ComboBox();
	private CheckBox chkallparent= new CheckBox();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private CheckBox chkAll=new CheckBox();
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate;
	private Label lblToDate;
	private Label lblLine;
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	@SuppressWarnings("unused")
	private Label lblline;

	@SuppressWarnings("unused")
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;

	public ItemWisePurcaseRegisterRpt(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("ITEM WISE PURCHASE REGISTER::"+sessionBean.getCompany());
		this.setResizable(false);
		this.addComponent(buildMainLayout());
		setEventAction();
		itemtypeDataload();
		cmbparentype.focus();
	}
	@SuppressWarnings({ "rawtypes", "unused" })
	private void cmbItemDataLoad(String parenttype) 
	{

		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			cmbItem.removeAllItems();

			String sql=		"select distinct b.ProductID,c.vRawItemName from tbRawPurchaseInfo a "
					+ "inner join "
					+ "tbRawPurchaseDetails b on a.ReceiptNo=b.ReceiptNo "
					+ "inner join "
					+ "tbRawItemInfo c on c.vRawItemCode=b.ProductID "
					+"where c.vCategoryType like '"+parenttype+"' ";

			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbItem.addItem(element[0]);
				cmbItem.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void itemtypeDataload() {

		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();


			String sql= "select distinct  0,vCategoryType from tbRawPurchaseInfo a "
					+"inner join "
					+"tbRawPurchaseDetails b on a.ReceiptNo=b.ReceiptNo "
					+"inner join "
					+"tbRawItemInfo c on c.vRawItemCode=b.ProductID ";

			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbparentype.addItem(element[1]);
				cmbparentype.setItemCaption(element[1], element[1].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setEventAction() {

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) 
			{
				if(cmbparentype.getValue()!=null || chkallparent.booleanValue())
				{
					if(cmbItem.getValue()!=null || chkAll.booleanValue())
					{
						reportView();
					}
					else
					{
						getParent().showNotification("Select Item Name", Notification.TYPE_WARNING_MESSAGE);	
					}	
				}
				else
				{
					getParent().showNotification("Select Parent Type", Notification.TYPE_WARNING_MESSAGE);	
				}

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
					cmbItem.setEnabled(false);
					cmbItem.setValue(null);

				}
				else{
					cmbItem.setEnabled(true);
					cmbItem.focus();
				}
			}
		});

		chkallparent.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkallparent.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbparentype.setEnabled(false);
					cmbparentype.setValue(null);
					cmbItemDataLoad("%");

				}
				else{
					cmbparentype.setEnabled(true);
					cmbItem.removeAllItems();
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

		cmbparentype.addListener(new ValueChangeListener() 
		{

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbparentype.getValue()!=null)
				{
					String parenttype=cmbparentype.getValue().toString();
					cmbItemDataLoad(parenttype);
				}
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private void reportView()
	{	
		String query=null;
		Transaction tx=null;
		String productId="";
		String parentype="";

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
			hm.put("fromdate",new SimpleDateFormat("dd-MM-yy").format(formDate.getValue()) );
			hm.put("todate", new SimpleDateFormat("dd-MM-yy").format(toDate.getValue()));


			if(chkAll.booleanValue())
			{
				productId="%";	
			}
			else
			{
				productId=cmbItem.getValue().toString(); 	
			}

			if(chkallparent.booleanValue())
			{
				parentype="%";	
			}
			else
			{
				parentype=cmbparentype.getValue().toString();	
			}
			
			/*query="select rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate,"
					+"  si.supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode,PI.vRawItemName , "
					+" pi.vUnitName,poi.poDate,PI.vCategoryType   from tbRawPurchaseInfo rpi inner join tbSupplierInfo si "
					+" on rpi.SupplierId=si.supplierId inner join tbRawPurchaseDetails rpd on "
					+" rpi.ReceiptNo=rpd.ReceiptNo inner join tbRawItemInfo pi on rpd.ProductID=pi.vRawItemCode "
					+" left join tbRawPurchaseOrderInfo poi on rpi.poNo=poi.poNo where CONVERT(Date, rpi.Date,105) "
					+" between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' and pi.vRawItemCode like '"+productId+"' and  PI.vCategoryType like '"+parentype+"'   order by PI.vCategoryType , pi.vRawItemCode,rpi.Date, rpi.ReceiptNo ";

			*/


			query= 	"select rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate,  si.supplierName,rpd.Qty,rpd.Rate, "
					+"(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode,PI.vRawItemName ,  pi.vUnitName,poi.poDate,PI.vCategoryType,rpi.MrrNo  "
					+"from tbRawPurchaseInfo rpi inner join tbSupplierInfo si "
					+"on rpi.SupplierId=si.supplierId inner join tbRawPurchaseDetails rpd on  "
					+"rpi.ReceiptNo=rpd.ReceiptNo inner join tbRawItemInfo pi on rpd.ProductID=pi.vRawItemCode  "
					+"left join tbRawPurchaseOrderInfo poi on rpi.poNo=poi.poNo where CONVERT(Date, rpi.Date,105) "
			        +"between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' and pi.vRawItemCode like '"+productId+"' and  PI.vCategoryType  "
			        +"like '"+parentype+"' and transactionType  not in ('L / C','Cash Purchase') and rpi.SupplierId not like 'AL%'"
			        
			        +"union all "
			        
			      +"select rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate,  si.Ledger_Name supplierName,rpd.Qty,rpd.Rate,  "
			      +"(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode,PI.vRawItemName ,  pi.vUnitName,poi.poDate,PI.vCategoryType,rpi.MrrNo  "
			      +"from tbRawPurchaseInfo rpi inner join tbLedger si  "
			      +"on rpi.SupplierId=si.Ledger_Id inner join tbRawPurchaseDetails rpd on "
			     +"rpi.ReceiptNo=rpd.ReceiptNo inner join tbRawItemInfo pi on rpd.ProductID=pi.vRawItemCode  "
			     +"left join tbRawPurchaseOrderInfo poi on rpi.poNo=poi.poNo where CONVERT(Date, rpi.Date,105)  "
			     +"between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' and pi.vRawItemCode like '"+productId+"' and  PI.vCategoryType  "
			     +"like '"+parentype+"' and transactionType  in ('L / C','Cash Purchase') and rpi.SupplierId  like 'AL%'   order by PI.vCategoryType , pi.vRawItemCode,rpi.Date, rpi.ReceiptNo " ;




			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				Window win = new ReportViewerNew(hm,"report/raw/rptItemWisePurchaseRegister.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else
			{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}
	private AbsoluteLayout buildMainLayout() {

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("470px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		lblParentType = new Label();
		lblParentType.setImmediate(false);
		lblParentType.setWidth("-1px");
		lblParentType.setHeight("-1px");
		lblParentType.setValue("Parent Type:");
		mainLayout.addComponent(lblParentType, "top:17.0px;left:62.0px;");

		cmbparentype = new ComboBox();
		cmbparentype.setImmediate(false);
		cmbparentype.setWidth("260px");
		cmbparentype.setHeight("24px");
		cmbparentype.setNullSelectionAllowed(true);
		cmbparentype.setNewItemsAllowed(false);
		cmbparentype.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbparentype, "top:15.0px;left:130.0px;");

		chkallparent.setCaption("All");
		chkallparent.setWidth("-1px");
		chkallparent.setHeight("24px");
		chkallparent.setImmediate(true);
		mainLayout.addComponent( chkallparent, "top:15.0px;left:395.0px;");


		lblItem = new Label();
		lblItem.setImmediate(false);
		lblItem.setWidth("-1px");
		lblItem.setHeight("-1px");
		lblItem.setValue("Item Name:");
		mainLayout.addComponent(lblItem, "top:42.0px;left:62.0px;");

		cmbItem = new ComboBox();
		cmbItem.setImmediate(false);
		cmbItem.setWidth("260px");
		cmbItem.setHeight("24px");
		cmbItem.setNullSelectionAllowed(true);
		cmbItem.setNewItemsAllowed(false);
		cmbItem.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbItem, "top:40.0px;left:130.0px;");

		chkAll.setCaption("All");
		chkAll.setWidth("-1px");
		chkAll.setHeight("24px");
		chkAll.setImmediate(true);
		mainLayout.addComponent( chkAll, "top:40.0px;left:395.0px;");

	/*	lblAll.setWidth("-1px");
		lblAll.setHeight("-1px");
		lblAll.setValue("All");
		mainLayout.addComponent( lblAll, "top:40.0px;left:415.0px;");*/

		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:67.0px;left:62.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("150px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:65.0px;left:130.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:92.0px;left:62.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("150px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:90.0px;left:130.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:115.0px; left:130.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setValue("____________________________________________________________________________________________________");
		mainLayout.addComponent(lblLine, "top:140.0px;left:25.0px;");

		previewButton.setWidth("95px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:165.opx; left:130.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:165.opx; left:220.0px");

		return mainLayout;


	}
}
