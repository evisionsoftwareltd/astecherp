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
public class RptThirdPartyIntemReceipt extends Window{

	SessionBean sessionBean;
	
	private Label lblAll=new Label();
	private Label lblPartyType= new Label();
	private ComboBox cmbPartyName=new ComboBox();
	private CheckBox chkPartyTypeAll= new CheckBox();
	
	private Label lblItemType= new Label();
	private ComboBox cmbItemName=new ComboBox();
	private CheckBox chkItemTypeAll= new CheckBox();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
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

	public RptThirdPartyIntemReceipt(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("THIRD PARTY ITEM RECEIPT :: "+sessionBean.getCompany());
		this.setResizable(false);
		this.addComponent(buildMainLayout());
		
		cmbItemName.setEnabled(false);
		chkItemTypeAll.setEnabled(false);
		
		
		setEventAction();
		PartytypeDataload();
		cmbPartyName.focus();
		
	}
	@SuppressWarnings({ "rawtypes", "unused" })
	
	

	private void PartytypeDataload() {

		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql= "select distinct  vPartyName,vPartyName from tb3rdPartyReceiptInformation info " +
					"inner join tb3rdPartyReceiptDetails details " +
					"on Info.vReceiptNo = details.vReceiptNo ";

			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbPartyName.addItem(element[1]);
				cmbPartyName.setItemCaption(element[1], element[1].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void ItemTypeDataload() {

		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String parentype="";
			
			if(chkPartyTypeAll.booleanValue())
			{
				parentype="%";	
			}
			else
			{
				parentype=cmbPartyName.getValue().toString();	
			}

			String sql= "select distinct  vProductId,vProductName from tb3rdPartyReceiptInformation info "
					+ "inner join tb3rdPartyReceiptDetails details "
					+ "on Info.vReceiptNo = details.vReceiptNo"
					+ " where vPartyName='"+parentype+"' ";
			System.out.println("Item Type Data Load : "+sql);

			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbItemName.addItem(element[1]);
				cmbItemName.setItemCaption(element[1], element[1].toString());
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
				if(cmbPartyName.getValue()!=null || chkPartyTypeAll.booleanValue())
				{
						reportView();
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
		
		chkPartyTypeAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkPartyTypeAll.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbPartyName.setEnabled(false);
					cmbPartyName.setValue(null);

					cmbItemName.setEnabled(true);
					chkItemTypeAll.setEnabled(true);
					ItemTypeDataload();
				}
				else{
					cmbPartyName.setEnabled(true);
					cmbPartyName.focus();
					
					cmbItemName.setEnabled(false);
					chkItemTypeAll.setEnabled(false);
					
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
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPartyName.getValue()!=null)
				{
					System.out.println("Cmb Party Name 1 : ");
					cmbItemName.setEnabled(true);
					chkItemTypeAll.setEnabled(true);
					ItemTypeDataload();
					System.out.println("Cmb Party Name  2 : ");
				}
				else {
					System.out.println("Cmb Party Name 3 : ");
				}
			}
		});
	
		chkItemTypeAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkItemTypeAll.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbItemName.setEnabled(false);
					cmbItemName.setValue(null);

				}
				else{
					cmbItemName.setEnabled(true);
					cmbItemName.focus();
					
				}
			}
		});

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private void reportView()
	{	
		String query=null;
		Transaction tx=null;
		String ReceiptNo="";
		String parentype="";
		String Itemtype="";

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
			
			hm.put("user", sessionBean.getUserName());
			
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			


			if(chkPartyTypeAll.booleanValue())
			{
				parentype="%";	
			}
			else
			{
				parentype=cmbPartyName.getValue().toString();	
			}
			if(chkItemTypeAll.booleanValue())
			{
				Itemtype="%";	
			}
			else
			{
				Itemtype=cmbItemName.getValue().toString();	
			}
			query="select Info.vPartyName,Info.vPartyAddress,Info.vChallanNo,Info.dChallanDate, (select vSourceName from tbSourceInfo where info.vSource = iSourceID) vSourceName,Info.vReceiptNo," +
					"Info.dReceiptDate, details.vProductName,details.vUnit,details.mQty,details.mRate,details.mAmount,details.vlabelNamesource " +
					"from tb3rdPartyReceiptInformation Info inner join tb3rdPartyReceiptDetails details " +
					" on Info.vReceiptNo = details.vReceiptNo " +
					"where Info.vPartyName like '"+parentype+"' and details.vProductName like '"+Itemtype+"' order by Info.dReceiptDate ";

			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				Window win = new ReportViewerNew(hm,"report/raw/rptThirdPartyItemReceipt.jasper",
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
		mainLayout.setHeight("130px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("210px");

		lblPartyType = new Label();
		lblPartyType.setImmediate(true);
		lblPartyType.setWidth("-1px");
		lblPartyType.setHeight("-1px");
		lblPartyType.setValue("Party Name:");
		mainLayout.addComponent(lblPartyType, "top:22.0px;left:50.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("260px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setNewItemsAllowed(false);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbPartyName, "top:20.0px;left:130.0px;");

		chkPartyTypeAll.setCaption("All");
		chkPartyTypeAll.setWidth("-1px");
		chkPartyTypeAll.setHeight("24px");
		chkPartyTypeAll.setImmediate(true);
		mainLayout.addComponent( chkPartyTypeAll, "top:21.0px;left:395.0px;");
		
		lblItemType = new Label();
		lblItemType.setImmediate(true);
		lblItemType.setWidth("-1px");
		lblItemType.setHeight("-1px");
		lblItemType.setValue("Item Name:");
		mainLayout.addComponent(lblItemType, "top:52.0px;left:50.0px;");

		cmbItemName = new ComboBox();
		cmbItemName.setImmediate(true);
		cmbItemName.setWidth("260px");
		cmbItemName.setHeight("24px");
		cmbItemName.setNullSelectionAllowed(true);
		cmbItemName.setNewItemsAllowed(false);
		cmbItemName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbItemName, "top:50.0px;left:130.0px;");

		chkItemTypeAll.setCaption("All");
		chkItemTypeAll.setWidth("-1px");
		chkItemTypeAll.setHeight("24px");
		chkItemTypeAll.setImmediate(true);
		mainLayout.addComponent( chkItemTypeAll, "top:51.0px;left:395.0px;");


	/*	lblAll.setWidth("-1px");
		lblAll.setHeight("-1px");
		lblAll.setValue("All");
		mainLayout.addComponent( lblAll, "top:40.0px;left:415.0px;");*/

		
		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:70.0px; left:130.0px");
		chkpdf.setVisible(false);
		chkother.setVisible(false);

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setValue("____________________________________________________________________________________________________");
		mainLayout.addComponent(lblLine, "top:80.0px;left:25.0px;");

		previewButton.setWidth("95px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:100.opx; left:130.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:100.opx; left:220.0px");

		return mainLayout;


	}
}
