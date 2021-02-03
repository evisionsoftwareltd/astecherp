package acc.appform.FinishedGoodsModule;
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
public class RptFinishedGoodsStockReport extends Window{

	SessionBean sessionBean;
	private Label lblItem=new Label("Item Name: ");
	private ComboBox cmbItem=new ComboBox();
	private Label lblAll=new Label();
	private Label lblPartyName= new Label();
	private ComboBox cmbPartyName=new ComboBox();
	private CheckBox chkallPartyName= new CheckBox();

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

	public RptFinishedGoodsStockReport(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("FINISHED GOOD STOCK REPORT::"+sessionBean.getCompany());
		this.setResizable(false);
		this.addComponent(buildMainLayout());
		setEventAction();
		PartyNameLoad();
		cmbPartyName.focus();
	}
	@SuppressWarnings({ "rawtypes", "unused" })
	private void cmbItemDataLoad(String partyid) 
	{
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			cmbItem.removeAllItems();
			String sql="select distinct vProductId,vProductName from tbFinishedProductInfo where vCategoryId like '"+partyid+"' order by vProductName";
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

	private void PartyNameLoad() {

		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql= "select distinct vCategoryId,vCategoryName from tbFinishedProductInfo order by vCategoryName";

			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbPartyName.addItem(element[0]);
				cmbPartyName.setItemCaption(element[0], element[1].toString());
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
				if(cmbPartyName.getValue()!=null /*|| chkallPartyName.booleanValue()*/)
				{
					if(cmbItem.getValue()!=null /*|| chkAll.booleanValue()*/)
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

		chkallPartyName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkallPartyName.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbPartyName.setEnabled(false);
					cmbPartyName.setValue(null);
					cmbItemDataLoad("%");

				}
				else{
					cmbPartyName.setEnabled(true);
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

		cmbPartyName.addListener(new ValueChangeListener() 
		{

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbPartyName.getValue()!=null)
				{
					String partyid=cmbPartyName.getValue().toString();
					cmbItemDataLoad(partyid);
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
		String PartyId="";

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


			if(chkAll.booleanValue())
			{
				productId="%";	
			}
			else
			{
				productId=cmbItem.getValue().toString(); 	
			}

			if(chkallPartyName.booleanValue())
			{
				PartyId="%";	
			}
			else
			{
				PartyId=cmbPartyName.getValue().toString();	
			}
			
			/*query="select rpi.Date,rpi.ReceiptNo,rpi.purchaseType,rpi.poNo,rpi.ChallanNo,rpi.challanDate,"
					+"  si.supplierName,rpd.Qty,rpd.Rate,(rpd.Qty*rpd.Rate)as Amount,pi.vRawItemCode,PI.vRawItemName , "
					+" pi.vUnitName,poi.poDate,PI.vCategoryType   from tbRawPurchaseInfo rpi inner join tbSupplierInfo si "
					+" on rpi.SupplierId=si.supplierId inner join tbRawPurchaseDetails rpd on "
					+" rpi.ReceiptNo=rpd.ReceiptNo inner join tbRawItemInfo pi on rpd.ProductID=pi.vRawItemCode "
					+" left join tbRawPurchaseOrderInfo poi on rpi.poNo=poi.poNo where CONVERT(Date, rpi.Date,105) "
					+" between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' and pi.vRawItemCode like '"+productId+"' and  PI.vCategoryType like '"+PartyId+"'   order by PI.vCategoryType , pi.vRawItemCode,rpi.Date, rpi.ReceiptNo ";

			*/


			query= 	"select *  from funMasterProductStockDatebtn('"+datef.format(formDate.getValue())+"', '"+datef.format(toDate.getValue())+"' , '"+productId+"')" ;




			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				Window win = new ReportViewerNew(hm,"report/account/finishedGoods/rptFinishedGoodsStockReport.jasper",
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
		mainLayout.setWidth("490px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("520px");
		setHeight("280px");

		lblPartyName = new Label();
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		lblPartyName.setValue("Party Name:");
		mainLayout.addComponent(lblPartyName,"top:17.0px;left:10px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(false);
		cmbPartyName.setWidth("390px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setNewItemsAllowed(false);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbPartyName, "top:15.0px;left:85px;");

		chkallPartyName.setCaption("All");
		chkallPartyName.setWidth("-1px");
		chkallPartyName.setHeight("24px");
		chkallPartyName.setImmediate(true);
		mainLayout.addComponent( chkallPartyName, "top:15.0px;left:395.0px;");
		chkallPartyName.setVisible(false);


		lblItem = new Label();
		lblItem.setImmediate(false);
		lblItem.setWidth("-1px");
		lblItem.setHeight("-1px");
		lblItem.setValue("Item Name:");
		mainLayout.addComponent(lblItem, "top:42.0px;left:10px;");

		cmbItem = new ComboBox();
		cmbItem.setImmediate(false);
		cmbItem.setWidth("390px");
		cmbItem.setHeight("24px");
		cmbItem.setNullSelectionAllowed(true);
		cmbItem.setNewItemsAllowed(false);
		cmbItem.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbItem, "top:40.0px;left:85px;");

		chkAll.setCaption("All");
		chkAll.setWidth("-1px");
		chkAll.setHeight("24px");
		chkAll.setImmediate(true);
		mainLayout.addComponent( chkAll, "top:40.0px;left:395.0px;");
		chkAll.setVisible(false);

	/*	lblAll.setWidth("-1px");
		lblAll.setHeight("-1px");
		lblAll.setValue("All");
		mainLayout.addComponent( lblAll, "top:40.0px;left:415.0px;");*/

		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:67.0px;left:10px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("110px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:65.0px;left:85px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:92.0px;left:10px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("110px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:90.0px;left:85px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:115.0px; left:85px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setValue("____________________________________________________________________________________________________");
		mainLayout.addComponent(lblLine, "top:140.0px;left:25.0px;");

		previewButton.setWidth("95px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:165.opx; left:85px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:165.opx; left:220.0px");

		return mainLayout;


	}
}
