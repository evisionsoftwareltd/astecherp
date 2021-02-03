package com.example.rawMaterialReport;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.ReportViewer;
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

public class IssueRegisterRpt extends Window{

	SessionBean sessionBean;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();
	int type=0;
	private Label lblFDate;
	private Label lblToDate;
	private Label lblLine;
	private Label lblParentType=new Label();
	private Label lblItemName=new Label();
	private Label lblSectionName=new Label();
	private ComboBox cmbParentType=new ComboBox();
	private ComboBox cmbSectionName=new ComboBox();
	private ComboBox cmbItemName=new ComboBox();
	private CheckBox chkAllsection;
	private CheckBox chkAllItem;
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	private AbsoluteLayout mainLayout;

	public IssueRegisterRpt(SessionBean sessionBean, String string){

		this.sessionBean=sessionBean;
		this.setCaption("ISSUE REGISTER ::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
		parentType();
	}


	@SuppressWarnings("serial")
	private void setEventAction() {

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) 
			{
				if(cmbParentType.getValue()!=null)
				{
					if(cmbSectionName.getValue()!=null || chkAllsection.booleanValue())
					{
						if(cmbItemName.getValue()!=null || chkAllItem.booleanValue())
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
						showNotification("Please Select Section Name",Notification.TYPE_WARNING_MESSAGE);	
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

		cmbParentType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{

				if(cmbParentType.getValue()!=null)
				{
					sectionDataLoad();
				}

			}
		});

		cmbSectionName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{

				if(cmbSectionName.getValue()!=null)
				{
					String  section=cmbSectionName.getValue().toString();
					ItemDataLoad(section);
				}

			}
		});
		
		chkAllsection.addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event)
			{

				if(chkAllsection.booleanValue()==true)
				{
					cmbSectionName.setValue(null);
					cmbSectionName.setEnabled(false);
					ItemDataLoad("%");
				}
					
				else
				{
					cmbSectionName.setEnabled(true);
					cmbItemName.removeAllItems();
				}
					

			}
		});	
		
		
		chkAllItem.addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event)
			{

				if(chkAllItem.booleanValue()==true)
				{
					cmbItemName.setValue(null);
					cmbItemName.setEnabled(false);
					
				}
					
				else
				{
					cmbItemName.setEnabled(true);
					
				}
					

			}
		});	
		
		
		

		
		
	}




	private void parentType()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		String query = "select distinct 0, c.vCategoryType  from tbRawIssueInfo a "
						+"inner join "
						+"tbRawIssueDetails b on a.IssueNo=b.IssueNo "
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


	private void sectionDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		/*String query =  "select distinct  a.IssuedTo,b.SectionName from tbRawIssueInfo a "
						+"inner join  "
						+"tbSectionInfo b "
						+" on a.IssuedTo=b.AutoID ";*/
		
		String query= "select distinct  a.IssuedTo,d.SectionName from tbRawIssueInfo a "
				      +"inner join "
				      +"tbRawIssueDetails b on a.IssueNo=b.IssueNo "
				      +"inner join "
				      +"tbRawItemInfo c on c.vRawItemCode=b.ProductID "
				      +"inner join "
				      +"tbSectionInfo d on d.AutoID=a.IssuedTo "
				      +"where c.vCategoryType like '"+cmbParentType.getValue().toString()+"' ";
		
		


		System.out.println(query);

		cmbSectionName.removeAllItems();
		List<?> list = session.createSQLQuery(query).list();		
		for(Iterator<?> iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbSectionName.addItem(element[0]);
			cmbSectionName.setItemCaption(element[0], element[1].toString());
		}
	}


	private void ItemDataLoad(String section)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		
	/*	String query ="select vRawItemCode, vRawItemName,vGroupId from tbRawItemInfo a " 
					+ "inner join tbRawProductDetails b on a.vGroupId = b.categoryId where a.vGroupId like '"+cmbSectionName.getValue().toString()+"'";
*/
		String query= "select distinct b.ProductID,c.vRawItemName from tbRawIssueInfo a inner join tbRawIssueDetails b "
				    +"on a.IssueNo=b.IssueNo "
				    +"inner join "
				    +"tbRawItemInfo c on c.vRawItemCode=b.ProductID "
				    +"where a.IssuedTo like '"+section+"' and c.vCategoryType like '"+cmbParentType.getValue().toString()+"' " ;
		
		
		
		
		System.out.println(query);
		
		cmbItemName.removeAllItems();
		List<?> list = session.createSQLQuery(query).list();	
		for(Iterator<?> iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbItemName.addItem(element[0]);
			cmbItemName.setItemCaption(element[0], element[1].toString());
		}

	}



private AbsoluteLayout buildMainLayout() {
	mainLayout = new AbsoluteLayout();
	mainLayout.setImmediate(false);

	mainLayout.setWidth("550px");
	mainLayout.setHeight("230px");
	mainLayout.setMargin(false);

	setWidth("580px");
	setHeight("315px");

	lblParentType = new Label();
	lblParentType.setImmediate(false);
	lblParentType.setWidth("-1px");
	lblParentType.setHeight("-1px");
	lblParentType.setValue("Parent Type:");
	mainLayout.addComponent(lblParentType, "top:20.0px;left:52.0px;");

	cmbParentType = new ComboBox();
	cmbParentType.setImmediate(true);
	cmbParentType.setWidth("260px");
	cmbParentType.setHeight("24px");
	cmbParentType.setNullSelectionAllowed(false);
	cmbParentType.setNewItemsAllowed(false);
	mainLayout.addComponent( cmbParentType, "top:18.0px;left:145.0px;");

	lblSectionName = new Label();
	lblSectionName.setImmediate(false);
	lblSectionName.setWidth("-1px");
	lblSectionName.setHeight("-1px");
	lblSectionName.setValue("Section Name :");
	mainLayout.addComponent(lblSectionName, "top:46.0px;left:52.0px;");

	cmbSectionName = new ComboBox();
	cmbSectionName.setImmediate(true);
	cmbSectionName.setWidth("260px");
	cmbSectionName.setHeight("24px");
	cmbSectionName.setNullSelectionAllowed(false);
	cmbSectionName.setNewItemsAllowed(false);
	mainLayout.addComponent( cmbSectionName, "top:44.0px;left:145.0px;");
	
	chkAllsection=new CheckBox("");
	chkAllsection.setCaption("All");
	chkAllsection.setImmediate(true);
	chkAllsection.setWidth("-1px");
	chkAllsection.setHeight("24px");
	mainLayout.addComponent(chkAllsection, "top:46.0px;left:410.0px;");

	lblItemName= new Label();
	lblItemName.setImmediate(false);
	lblItemName.setWidth("-1px");
	lblItemName.setHeight("-1px");
	lblItemName.setValue("Item Name :");
	mainLayout.addComponent(lblItemName, "top:72.0px;left:52.0px;");

	cmbItemName = new ComboBox();
	cmbItemName.setImmediate(true);
	cmbItemName.setWidth("260px");
	cmbItemName.setHeight("24px");
	cmbItemName.setNullSelectionAllowed(false);
	cmbItemName.setNewItemsAllowed(false);
	mainLayout.addComponent( cmbItemName, "top:70.0px;left:145.0px;");
	
	chkAllItem=new CheckBox("");
	chkAllItem.setCaption("All");
	chkAllItem.setImmediate(true);
	chkAllItem.setWidth("-1px");
	chkAllItem.setHeight("24px");
	mainLayout.addComponent(chkAllItem, "top:72.0px;left:410.0px;");


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
	mainLayout.addComponent(chklayout, "top:160.0px; left:210.0px");

	lblLine = new Label();
	lblLine.setImmediate(false);
	lblLine.setWidth("-1px");
	lblLine.setHeight("-1px");
	lblLine.setContentMode(Label.CONTENT_XHTML);
	//lblLine.setValue("_______________________________________________________________________");
	lblLine.setValue("<b><font color='#e65100'>========================================================================</font></b>");
	mainLayout.addComponent(lblLine, "top:180.0px;left:25.0px;");

	previewButton.setWidth("80px");
	previewButton.setHeight("28px");
	previewButton.setIcon(new ThemeResource("../icons/print.png"));
	mainLayout.addComponent(previewButton,"top:198.opx; left:180.0px");

	exitButton.setWidth("80px");
	exitButton.setHeight("28px");
	exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
	mainLayout.addComponent(exitButton,"top:198.opx; left:260.0px");

	return mainLayout;
}



/*private void reportView()
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

		query= "select rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate,si.supplierId, si.supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.ProductCode,PI.ProductName,  pi.Unit,poi.poDate "
					+"from tbRawPurchaseInfo rpi inner join tbSupplier_Info si on rpi.SupplierId=si.supplierId "
					+"inner join tbRawPurchaseDetails rpd on rpi.ReceiptNo=rpd.ReceiptNo "
					+"inner join tbRawProductInfo pi on rpd.ProductID=pi.ProductCode "
					+"left join tbRawPurchaseOrderInfo poi on rpi.poNo=poi.poNo "
					+"where CONVERT(Date, rpi.Date,105) between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' order by cast(si.supplierId as int)   ";

		query="select rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate, "
				+" si.supplierId, si.supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode,"
				+" PI.vRawItemName,  pi.vUnitName,poi.poDate,pi.vCategoryType from tbRawPurchaseInfo rpi inner join tbSupplierInfo si "
				+" on rpi.SupplierId=si.supplierId inner join tbRawPurchaseDetails rpd on rpi.ReceiptNo=rpd.ReceiptNo "
				+" inner join tbRawItemInfo pi on rpd.ProductID=pi.vRawItemCode left join tbRawPurchaseOrderInfo poi "
				+" on rpi.poNo=poi.poNo where CONVERT(Date, rpi.Date,105) between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' and pi.vCategoryType like '%' and si.supplierId like '"+cmbSectionName.getValue().toString()+"'  "
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

}*/

private void reportView()
{
	System.out.println("into Reportshow");
	String query=null;
	String activeFlag = null;
	String categoryID="";
	String productID="";
	String subcategoryID="";
	String categirytype="";
	String subsubcategory="";
	String section="";
	String Item="";
	
	
	
	
	String parentType="";
	
	if(cmbParentType.getValue()!=null)
	{
		parentType=cmbParentType.getValue().toString();
	}
	
	if(cmbSectionName.getValue()!=null)
	{
		section=cmbSectionName.getValue().toString();	
	}
	else
	{
		section="%";
	}
	
	if(cmbItemName.getValue()!=null)
	{
	  Item=cmbItemName.getValue().toString();	
	}
	else
	{
		Item="%";	
	}





	try{

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();



		HashMap hm = new HashMap();
		hm.put("logo", sessionBean.getCompanyLogo());
		hm.put("company", sessionBean.getCompany());
		hm.put("address", sessionBean.getCompanyAddress());
		hm.put("Phone", sessionBean.getCompanyContact());
		hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
		/*hm.put("asonDate", new SimpleDateFormat("dd-MM-yy").format(dAsOnDate.getValue()));*/
		hm.put("parentType", cmbParentType.getValue().toString());
		hm.put("fromDate", new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()));
		hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));


		query = " select b.ProductID, c.vRawItemName,c.vUnitName,a.IssueNo,a.Date,a.IssuedTo,d.SectionName," +
				" a.ProductionType,a.productionStep,a.finishedGoods,a.challanNo,b.Qty,b.Rate,(b.Qty*b.Rate) as amount," +
				" c.vGroupId,c.vGroupName,c.vSubGroupId,c.vSubGroupName , c.vsubsubCategoryId,c.vSubSubCategoryName," +
				" c.vProductType,e.productTypeName,f.StepName,g.vProductName,c.vCategoryType  " +
				" from tbRawIssueInfo a inner join tbRawIssueDetails b on a.IssueNo=b.IssueNo " +
				" inner join tbRawItemInfo c " +
				" on c.vRawItemCode=b.ProductID " +
				" inner join tbSectionInfo d " +
				" on d.AutoID=a.IssuedTo" +
				" left join tbProductionType e" +
				" on e.productTypeId=a.ProductionType" +
				" left join tbProductionStep f" +
				" on f.StepId=a.productionStep" +
				" left join tbFinishedProductInfo g" +
				" on g.vProductId=a.finishedGoods" +
				" where CONVERT(Date,a.Date,105)  between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  and c.vCategoryType like '"+parentType+"'  and a.IssuedTo like '"+section+"' and b.ProductID like '"+Item+"'  order by a.IssuedTo,c.vGroupId,c.vSubGroupId, c.vsubsubCategoryId,b.ProductID,a.Date";


		System.out.println(query);
		hm.put("sql", query);
		System.out.println("123");


		List lst= session.createSQLQuery(query).list();
		if(!lst.isEmpty())
		{
			Window win = new ReportViewerNew(hm,"report/raw/rptIssueRegisterNew.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
			
			System.out.println("789");

			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);	
		}
		else
		{
		   showNotification("There Is No Data",Notification.TYPE_WARNING_MESSAGE);	
		}
		
		
	}
	catch(Exception exp){

		this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);

	}
}
}