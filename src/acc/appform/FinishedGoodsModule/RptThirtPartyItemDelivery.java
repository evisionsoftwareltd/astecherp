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
public class RptThirtPartyItemDelivery extends Window{

	SessionBean sessionBean;
	private Label lblItem=new Label("Item Name: ");
	private ComboBox cmbItem=new ComboBox();
	private Label lblAll=new Label();
	private Label lblPartyName= new Label();
	private ComboBox cmbPartyName=new ComboBox();
	private CheckBox chkAllItemPartyName= new CheckBox();

	int type=1;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private CheckBox chkAllItem=new CheckBox();
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

	public RptThirtPartyItemDelivery(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("THIRT PARTY ITEM DELIVERY ::"+sessionBean.getCompany());
		this.setResizable(false);
		this.addComponent(buildMainLayout());
		setEventAction();
		partyNameDataload();
		cmbPartyName.focus();
	}
	@SuppressWarnings({ "rawtypes", "unused" })
	private void cmbItemDataLoad(String parenttype) 
	{

		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			cmbItem.removeAllItems();

			String sql="select distinct  vProductId,vProductName from tbDeliveryChallanInfo a " +
					"inner join tbDeliveryChallanDetails b on a.vChallanNo=b.vChallanNo" +
					" where b.vProductId not like 'FI%' and  a.vPartyId like '"+parenttype+"' ";

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

	private void partyNameDataload() {

		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql= "select distinct  vPartyId,vPartyName from tbDeliveryChallanInfo a " +
					"inner join tbDeliveryChallanDetails b on a.vChallanNo=b.vChallanNo where  CONVERT(Date, a.dChallanDate,105) " +
					"between '"+datef.format(formDate.getValue())+"' " +
					"and '"+datef.format(toDate.getValue())+"'and b.vProductId not like 'FI%'";

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
				if(cmbPartyName.getValue()!=null||chkAllItemPartyName.booleanValue())
				{
					if(cmbItem.getValue()!=null || chkAllItem.booleanValue())
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
					cmbItem.setEnabled(true);
					chkAllItem.setEnabled(true);
					chkAllItem.setValue(false);
					String parenttype=cmbPartyName.getValue().toString();
					cmbItemDataLoad(parenttype);
				}
				else{
					chkAllItem.setValue(false);
					cmbItem.setEnabled(false);
					chkAllItem.setEnabled(false);
					cmbItem.setValue(null);
				}
			}

		});
		chkAllItemPartyName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllItemPartyName.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbPartyName.setEnabled(false);
					cmbPartyName.setValue(null);
					
					cmbItem.setEnabled(true);
					cmbItem.setValue(null);
					chkAllItem.setEnabled(true);
					chkAllItem.setValue(false);
					cmbItemDataLoad("%");
				}
				else{
					
					cmbPartyName.setEnabled(true);
					cmbPartyName.setValue(null);
					cmbItem.setEnabled(false);
					chkAllItem.setEnabled(false);
					chkAllItem.setValue(false);
				}
			}
		});
		
		
		chkAllItem.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllItem.booleanValue();
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

		/*cmbPartyName.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cmbPartyName.getValue()!=null)
				{
					cmbItem.setEnabled(true);
					chkAllItem.setEnabled(false);
					String parenttype=cmbPartyName.getValue().toString();
					cmbItemDataLoad(parenttype);
				}
				else{
					cmbItem.setEnabled(true);
					String parenttype=cmbPartyName.getValue().toString();
					cmbItemDataLoad(parenttype);
				}

			}
		});*/
		formDate.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
					cmbPartyName.removeAllItems();
					partyNameDataload();
			}
		});
		toDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbPartyName.removeAllItems();
				partyNameDataload();
			}
		});

	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private void reportView()
	{	
		String query=null;
		Transaction tx=null;
		String productId="%";
		String parentype="%";
		String PartyName="%";

		/*if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;*/

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("user", sessionBean.getUserName());
			hm.put("FromDate",new SimpleDateFormat("dd-MM-yy").format(formDate.getValue()) );
			hm.put("ToDate", new SimpleDateFormat("dd-MM-yy").format(toDate.getValue()));

			if(cmbItem.getValue()!=null){
				productId=cmbItem.getValue().toString(); 	
			}
			if(cmbPartyName.getValue()!=null){	
				PartyName =cmbPartyName.getValue().toString();	
			}
			query="select a.vPartyName,a.vPartyAddress,a.vChallanNo,a.dChallanDate,b.vProductName,b.vProductUnit,b.mChallanQty,b.vRemarks " +
					"from tbDeliveryChallanInfo a " +
					"inner join tbDeliveryChallanDetails b " +
					"on a.vChallanNo=b.vChallanNo where CONVERT(Date, a.dChallanDate,105) " +
					"between '"+datef.format(formDate.getValue())+"' " +
					"and '"+datef.format(toDate.getValue())+"' " +
					"and b.vProductId not like 'FI%' and  a.vPartyId like '"+PartyName+"' " +
					"and b.vProductId like '"+productId+"'";

			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				Window win = new ReportViewerNew(hm,"report/raw/rptThirdPartyItemDelivery.jasper",
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
		mainLayout.setImmediate(true);
		mainLayout.setWidth("470px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		lblFDate = new Label();
		lblFDate.setImmediate(true);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:17.0px;left:20.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("120px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:15.0px;left:100.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(true);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:42.0px;left:20.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("120px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:40.0px;left:100.0px;");

		lblPartyName = new Label();
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		lblPartyName.setValue("Party Name:");
		mainLayout.addComponent(lblPartyName, "top:67.0px;left:20.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("285px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setNewItemsAllowed(false);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbPartyName, "top:65.0px;left:100.0px;");

		chkAllItemPartyName.setCaption("All");
		chkAllItemPartyName.setWidth("-1px");
		chkAllItemPartyName.setHeight("24px");
		chkAllItemPartyName.setImmediate(true);
		mainLayout.addComponent( chkAllItemPartyName, "top:67.0px;left:395.0px;");
		
		lblItem = new Label();
		lblItem.setImmediate(true);
		lblItem.setWidth("-1px");
		lblItem.setHeight("-1px");
		lblItem.setValue("Item Name:");
		mainLayout.addComponent(lblItem, "top:92.0px;left:20.0px;");

		cmbItem = new ComboBox();
		cmbItem.setImmediate(true);
		cmbItem.setWidth("285px");
		cmbItem.setHeight("24px");
		cmbItem.setNullSelectionAllowed(true);
		cmbItem.setNewItemsAllowed(false);
		cmbItem.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbItem.setEnabled(false);
		mainLayout.addComponent( cmbItem, "top:90.0px;left:100.0px;");

		chkAllItem.setCaption("All");
		chkAllItem.setWidth("-1px");
		chkAllItem.setHeight("24px");
		chkAllItem.setImmediate(true);
		mainLayout.addComponent( chkAllItem, "top:90.0px;left:395.0px;");

		/*	lblAll.setWidth("-1px");
		lblAll.setHeight("-1px");
		lblAll.setValue("All");
		mainLayout.addComponent( lblAll, "top:40.0px;left:415.0px;");*/



		/*chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:115.0px; left:130.0px");*/

		lblLine = new Label();
		lblLine.setImmediate(true);
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
