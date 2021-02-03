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
public class RptIncompletemasterproductinfo extends Window{

	SessionBean sessionBean;
	
	private Label lblAll=new Label();
	private Label lblPartyType= new Label();
	private ComboBox cmbPartyName=new ComboBox();
	private CheckBox chkPartyTypeAll= new CheckBox();

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
	
	private OptionGroup opproductType= new OptionGroup();
	private String [] productType={"Assembled","Not Assembled"};
	private Label lblproductType;

	public RptIncompletemasterproductinfo(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("INCOMPLETE MASTER PRODUCT REPORT:: "+sessionBean.getCompany());
		this.setResizable(false);
		this.addComponent(buildMainLayout());
		setEventAction();
		partyLoad("Assemble");
		cmbPartyName.focus();
	}
	@SuppressWarnings({ "rawtypes", "unused" })
	

	private void partyLoad( String caption) 
	{

		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="";
			if(caption.equalsIgnoreCase("Assemble"))
			{
				 sql= 
						 "select distinct b.vCategoryId,b.vCategoryName   from tbFinishedProductDetailsNew a "
						 +"inner join tbFinishedProductInfo b "
						 +"on a.fgId=b.vProductId  where fgId in " 
						 +"( "
						 +"select fgId from tbFinishedProductDetailsNew where consumptionStage='Assemble' "

						 +")and fgId not in (select fgId from tbFinishedProductDetailsNew "
						 +"where consumptionStage='Assemble' and semiFgSubId!='' and semiFgSubId!='null'  ) "	;
			}
			else
			{
			   sql="select distinct  b.vCategoryId,b.vCategoryName from tbFinishedProductDetailsNew a inner join tbFinishedProductInfo b "
				   +"on a.fgId=b.vProductId  where consumptionStage='Delivery Challan' "
				  + "and fgId not in " 
				  + "( "
				  + "select fgId from tbFinishedProductDetailsNew  where consumptionStage='Assemble' "
				  + ")  and semiFgId='' or semiFgId='null' ";
			}

			List list=session.createSQLQuery(sql).list();

			cmbPartyName.removeAllItems();
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

				}
				else{
					cmbPartyName.setEnabled(true);
					cmbPartyName.focus();
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
		
		
		opproductType.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				if(opproductType.getValue()!=null)
				{
					if(opproductType.getValue().toString().equalsIgnoreCase("Assembled"))
					{
						 partyLoad("Assemble");
					}
					else
					{
						partyLoad("Delivery");	
					}
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
		String report="";

		if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("CompanyName", sessionBean.getCompany());
			hm.put("Address", sessionBean.getCompanyAddress());
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
			
			if(opproductType.getValue().toString().equalsIgnoreCase("Assembled"))
			{
				report="report/account/finishedGoods/SemiFgSubName.jasper";
				query=  "select fgId,fgName,semiFgId,semiFgName,semiFgSubName,stdWeight,qty,unitPrice,consumptionStage, "
						+"b.vCategoryId partyId, b.vCategoryName,b.vUnitName    from tbFinishedProductDetailsNew a "
						+"inner join tbFinishedProductInfo b "
						+"on a.fgId=b.vProductId  where fgId in "
						+"( "
						 +"select fgId from tbFinishedProductDetailsNew where consumptionStage='Assemble' "

						+") and fgId not in (select fgId from tbFinishedProductDetailsNew "
						  +"where consumptionStage='Assemble' and semiFgSubId!='' and semiFgSubId!='null'  ) and b.vCategoryId like '"+parentype+"'   order by b.vCategoryId,fgName ";
						  
			}
			else
			{
				report="report/account/finishedGoods/rptnotAssembleFG.jasper";
			    query=  "select fgId,fgName,case when semiFgName='null' then '' else semiFgName end semiFgName,stdWeight,qty,unitPrice,consumptionStage, "
			    		+"b.vUnitName,b.vCategoryId partyId,b.vCategoryName partyName from tbFinishedProductDetailsNew a "
			    		+"inner join tbFinishedProductInfo b "
			    		+"on a.fgId=b.vProductId "
			    		+"where consumptionStage='Delivery Challan' "
			    		+"and fgId not in " 
			    		+"( "
			    		+"select fgId from tbFinishedProductDetailsNew  where consumptionStage='Assemble' "
			    		+")  and( semiFgId='' or semiFgId='null') "
			    		+"and b.vCategoryId like '%'   order by b.vCategoryId,fgName ";
			}
			
			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				Window win = new ReportViewerNew(hm,report,
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
		
		lblproductType = new Label();
		lblproductType.setImmediate(false);
		lblproductType.setWidth("-1px");
		lblproductType.setHeight("-1px");
		lblproductType.setValue("Product Type:");
		mainLayout.addComponent(lblproductType, "top:27.0px;left:50.0px;");

		opproductType= new OptionGroup("");
		opproductType.setImmediate(true);
		opproductType.setWidth("-1px");
		opproductType.setHeight("-1px");
		opproductType.setStyleName("horizontal");
		mainLayout.addComponent(opproductType, "top:25.0px;left:130.0px;");

		for(int i=0;i<productType.length;i++)	
		{
			opproductType.addItem(productType[i]);
		}

		opproductType.select("Assembled");

		lblPartyType = new Label();
		lblPartyType.setImmediate(false);
		lblPartyType.setWidth("-1px");
		lblPartyType.setHeight("-1px");
		lblPartyType.setValue("Party Name:");
		mainLayout.addComponent(lblPartyType, "top:57.0px;left:50.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(false);
		cmbPartyName.setWidth("260px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setNewItemsAllowed(false);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbPartyName, "top:55.0px;left:130.0px;");

		chkPartyTypeAll.setCaption("All");
		chkPartyTypeAll.setWidth("-1px");
		chkPartyTypeAll.setHeight("24px");
		chkPartyTypeAll.setImmediate(true);
		mainLayout.addComponent( chkPartyTypeAll, "top:55.0px;left:395.0px;");
		



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
