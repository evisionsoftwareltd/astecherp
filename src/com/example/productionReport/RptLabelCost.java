package com.example.productionReport;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.xerces.impl.dtd.models.CMBinOp;
import org.hibernate.Session;
import org.hibernate.Transaction;


import com.common.share.ReportDate;
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
public class RptLabelCost extends Window{

	SessionBean sessionBean;

	private Label lblType=new Label("From : ");
	private ComboBox cmbType=new ComboBox();

	private Label lblProductName=new Label("To : ");
	private ComboBox cmbProductName=new ComboBox();
	private Label lblProductUnit = new Label();
	
	private Label lblpartyname=new Label();
	private ComboBox cmbpartyname=new ComboBox();
	
	

	private Label lblCostType=new Label("Cost Type : ");
	private ComboBox cmbCostType=new ComboBox();

	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate;
	//	private Label formDate;
	private Label lblLine;

	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private ComboBox cmbDeclareDate;

	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");

	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private ReportDate reportTime = new ReportDate();
	
	private AbsoluteLayout mainLayout;

	public RptLabelCost(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("COST REPORT::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		productionTypeDataLoad();
		setContent(mainLayout);
		addDeclareDate();
		setEventAction();
	}

	private void productionTypeDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select productTypeId,productTypeName from tbProductionType";
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbType.addItem(element[0].toString());
				cmbType.setItemCaption(element[0].toString(), element[1].toString());
				
			}
			
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	
	private void partyNameLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			//String sql="  select  vGroupId,partyName  from tbPartyInfo ";
			String sql="select  distinct partyCode,  (select partyName  from tbPartyInfo where vGroupId=tbcostSheetInfo.partyCode) as partyName   from tbcostSheetInfo  where partyCode in (SElect vGroupId from tbPartyInfo)and convert(date,declareDate,105) = '"+dateFormat.format(cmbDeclareDate.getValue())+"'";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbpartyname.addItem(element[0].toString());
				cmbpartyname.setItemCaption(element[0].toString(), element[1].toString());
			
			}
			
		}
		catch(Exception exp)
		{
		showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	
	
	private void setEventAction() 
	{

		previewButton.addListener(new ClickListener() 
		{

			public void buttonClick(ClickEvent event)
			{
				if(cmbType.getValue()!=null)
				{
					if(cmbpartyname.getValue()!=null)
					{
						if(cmbProductName.getValue()!=null)
						{
							reportView();	
						}
						else
						{
							showNotification("Plese Select Product Name ",Notification.TYPE_WARNING_MESSAGE);	
						}	
					}
					else
					{
						showNotification("Plese Select Party Name",Notification.TYPE_WARNING_MESSAGE);	
					}		
				}
				else
				{
					showNotification("Plese Select Production Type",Notification.TYPE_WARNING_MESSAGE);
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
					cmbType.setEnabled(false);
					cmbType.setValue(null);

				}
				else{
					cmbType.setEnabled(true);
					cmbType.focus();
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
		cmbDeclareDate.addListener(new ValueChangeListener() {


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDeclareDate.getValue()!=null )
				{
					partyNameLoad();	
				}
				else
				{
					
				  cmbpartyname.removeAllItems();	
				  //showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cmbpartyname.addListener(new ValueChangeListener() {


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDeclareDate.getValue()!=null && cmbpartyname.getValue()!=null)
				{
					productNameData();	
				}
				else
				{
					
				  cmbProductName.removeAllItems();	
				  showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		
		

	}

	private void productNameData() {

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			//String sql="  select vProductId, vProductName, vUnitName from tbFinishedProductInfo where vCategoryId like '"+cmbpartyname.getValue().toString()+"'";
			String sql="SElect fGCode,(select vProductName from tbFinishedProductInfo where vProductId=tbcostSheetInfo.fGCode) as vProductName " +
					"from tbcostSheetInfo where partyCode ='"+cmbpartyname.getValue().toString()+"'  and convert(date,declareDate,125) = '"+dateFormat.format(cmbDeclareDate.getValue())+"' " +
							"and fGCode in (select vProductId from tbFinishedProductInfo) order by vProductName";
			cmbProductName.removeAllItems();
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbProductName.addItem(element[0]);
				cmbProductName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			//showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
		}
	}	
	private void addDeclareDate()
	{
		cmbDeclareDate.removeAllItems();
		cmbDeclareDate.setValue(null);

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = "select distinct CONVERT(date,declareDate,105) declareDate, REPLACE(CONVERT(varchar(50),declareDate ,103),'/','-') Date from tbcostSheetInfo order by declareDate desc";

			List list = session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[]element=(Object[]) iter.next();
				cmbDeclareDate.addItem(element[0]);
				cmbDeclareDate.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void reportView()
	{	
		String query="";
		String subsql = "";
		String report=null;
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
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(cmbDeclareDate.getValue()) );
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			hm.put("productName",cmbProductName.getItemCaption(cmbProductName.getValue()) );
			//			hm.put("productUnit",lblProductUnit.getValue() );
			hm.put("user", sessionBean.getUserName());
			System.out.println(cmbProductName.getValue());
			System.out.println(cmbType.getItemCaption(cmbType.getValue()).toString());
			System.out.println(cmbType.getValue().toString());
			hm.put("SysDate",reportTime.getTime);
			System.out.println("Step1");
			/*if(cmbType.getItemCaption(cmbType.getValue()).equalsIgnoreCase("Tube Production"))
			{
				query = "select * from  (select '1' as type , a.vProductId,a.vProductName,a.vUnitName,a.vCategoryName," +
						"convert(date,b.declareDate,105) declareDate, a.Dia,a.LengthConsumption,a.Length,a.perSqmQty," +
						"d.vRawItemName,c.amount,b.transportCost,b.packingCost,b.markupPercent,b.markupAmt,'' " +
						"wastagepercent,(select ISNULL(SUM(transportCost+packingCost),0) from tbCostSheetInfo where " +
						"fGCode like '"+cmbProductName.getValue()+"' and CONVERT(date,declareDate,105)  like '"+dateFormat.format(cmbDeclareDate.getValue())+"') as othercost," +
						"b.endPrice  from tbFinishedProductInfo a inner join tbCostSheetInfo b on a.vProductId=b.fGCode" +
						" inner join tbCostSheetRmDetails c on c.jobNo=b.jobNo inner join  tbRawItemInfo d on " +
						"d.vRawItemCode=c.rawItemCode where  a.vProductId like '"+cmbProductName.getValue()+"' and   " +
						"convert(date,b.declareDate,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"' union select distinct '2' " +
						"as type ,a.vProductId,a.vProductName,a.vUnitName,a.vCategoryName, convert(date," +
						"b.declareDate,105) as declareDate, '','','','','wastage' " +
						"as wastage,(select(select isnull(SUM(wastageRawCost),0)   from  " +
						"tbCostSheetInfo a where a.fGCode like '"+cmbProductName.getValue()+"' and convert(date,a.declareDate,105) " +
						"like '"+dateFormat.format(cmbDeclareDate.getValue())+"' )- (select  ISNULL(SUM(a.amount),0)  from tbCostSheetRmDetails a " +
						"inner join tbCostSheetInfo b on a.jobNo=b.jobNo where b.fGCode like '"+cmbProductName.getValue()+"' and" +
						" convert(date, b.declareDate ,105)like '"+dateFormat.format(cmbDeclareDate.getValue())+"' ) as wastage),b.transportCost," +
						"b.packingCost,'',b.markupAmt,b.wstPercent,(select ISNULL(SUM(transportCost+packingCost),0) " +
						"  from tbCostSheetInfo where fGCode like '"+cmbProductName.getValue()+"' and CONVERT(date,declareDate,105) " +
						"like '"+dateFormat.format(cmbDeclareDate.getValue())+"') as othercost,b.endPrice  from tbFinishedProductInfo a inner join " +
						"tbCostSheetInfo b on a.vProductId=b.fGCode inner join tbCostSheetRmDetails c on " +
						"c.jobNo=b.jobNo inner join tbRawItemInfo d on d.vRawItemCode=c.rawItemCode where " +
						"a.vProductId like '"+cmbProductName.getValue()+"' and   convert(date,b.declareDate,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"') as " +
						"a  order by a.type";

				System.out.println(query);
				subsql = "select  b.overheadId, (select  Ledger_Name from tbLedger where Ledger_Id like b.overheadId ) as CoasHead ,b.amount as cost  from  tbCostSheetInfo a "
						+"inner join "
						+"tbCostSheetConvertionDetails b "
						+"on "
						+"a.jobNo=b.jobNo "
						+"where a.fGCode like '"+cmbProductName.getValue()+"' and convert(date,a.declareDate ,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"' ";
				System.out.println(subsql);
				report="report/production/ProductionOpening.jasper";
			}
*/

			/*if(cmbType.getItemCaption(cmbType.getValue()).equalsIgnoreCase("Level Production"))
			{*/
			if(cmbType.getItemCaption(cmbType.getValue()).equalsIgnoreCase("Labeling")||cmbType.getItemCaption(cmbType.getValue()).equalsIgnoreCase("Printing")||cmbType.getItemCaption(cmbType.getValue()).equalsIgnoreCase("Cap Folding")||cmbType.getItemCaption(cmbType.getValue()).equalsIgnoreCase("SBM"))
					{
				query = "select * from  ( select '1' as type , a.vProductId,a.vProductName,a.vUnitName,a.vCategoryName," +
						"convert(date,b.declareDate,105) declareDate, a.Dia,a.LengthConsumption,a.Length,a.perSqmQty," +
						"d.vRawItemName,c.amount,b.transportCost,b.packingCost,b.markupPercent,b.markupAmt,'' " +
						"wastagepercent,(select ISNULL(SUM(transportCost+packingCost),0)   from tbCostSheetInfo where fGCode " +
						"like '"+cmbProductName.getValue()+"' and CONVERT(date,declareDate,105)  like '"+dateFormat.format(cmbDeclareDate.getValue())+"' ) as othercost,b.endPrice," +
						"CAST(a.Length as varchar(120)) +'X'+CAST(a.width as varchar)as 'labelSize',CAST(a.LengthConsumption " +
						"as varchar(120)) +'X'+CAST(a.widthConsumption as varchar)as 'labelSizeConsum'  from " +
						"tbFinishedProductInfo a inner join tbCostSheetInfo b on a.vProductId=b.fGCode inner join " +
						"tbCostSheetRmDetails c on c.jobNo=b.jobNo inner join  tbRawItemInfo d on d.vRawItemCode=c.rawItemCode " +
						" where  a.vProductId like '"+cmbProductName.getValue()+"' and   convert(date,b.declareDate,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"'  union select " +
						"distinct '2' as type, a.vProductId,a.vProductName,a.vUnitName,a.vCategoryName, convert(date,b.declareDate,105) as" +
						" declareDate,'','','','','wastage' as wastage,(select(select isnull(SUM(wastageRawCost),0)   from  " +
						"tbCostSheetInfo a where a.fGCode like '"+cmbProductName.getValue()+"' and convert(date,a.declareDate,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"' )- " +
						"(select  ISNULL(SUM(a.amount),0)  from tbCostSheetRmDetails a inner join tbCostSheetInfo b on " +
						"a.jobNo=b.jobNo where b.fGCode like '"+cmbProductName.getValue()+"' and convert(date, b.declareDate ,105)like '"+dateFormat.format(cmbDeclareDate.getValue())+"' ) " +
						"as wastage),b.transportCost,b.packingCost,'',b.markupAmt,b.wstPercent,(select ISNULL(SUM(transportCost+" +
						"packingCost),0)   from tbCostSheetInfo where fGCode like '"+cmbProductName.getValue()+"' and CONVERT(date,declareDate,105)" +
						" like '"+dateFormat.format(cmbDeclareDate.getValue())+"') as othercost,b.endPrice,'',''  from tbFinishedProductInfo a inner join " +
						"tbCostSheetInfo b on a.vProductId=b.fGCode inner join tbCostSheetRmDetails c on c.jobNo=b.jobNo " +
						"inner join tbRawItemInfo d  on d.vRawItemCode=c.rawItemCode where  a.vProductId like '"+cmbProductName.getValue()+"' and  " +
						" convert(date,b.declareDate,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"' ) as  a  order by a.type ";
				System.out.println("cmbType"+query);
				subsql = "select  b.overheadId, (select  Ledger_Name from tbLedger where Ledger_Id like b.overheadId ) as " +
						"CoasHead ,b.amount as cost  from  tbCostSheetInfo a inner join tbCostSheetConvertionDetails b on " +
						"a.jobNo=b.jobNo where a.fGCode like '"+cmbProductName.getValue()+"' and convert(date,a.declareDate ,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"'" ;

				report="report/production/CostSheetLabel.jasper";
				System.out.println("sqlConsumption"+subsql);
				System.out.println("sql"+query);

			}

			if(cmbType.getItemCaption(cmbType.getValue()).equalsIgnoreCase("Blow Moulding")||cmbType.getItemCaption(cmbType.getValue()).equalsIgnoreCase("Injection Moulding")||cmbType.getItemCaption(cmbType.getValue()).equalsIgnoreCase("Injection Blow Molding."))
			{
				query = "select * from  ( select '1' as type , a.vProductId,a.vProductName,a.vUnitName,a.vCategoryName," +
						"convert(date,b.declareDate,105) declareDate, a.Dia,a.LengthConsumption,a.Length,a.perSqmQty," +
						"d.vRawItemName,c.amount,b.transportCost,b.packingCost,b.markupPercent,b.markupAmt,'0' wastagepercent," +
						"(select ISNULL(SUM(transportCost+packingCost),0)   from tbCostSheetInfo where fGCode like '"+cmbProductName.getValue()+"' " +
						"and CONVERT(date,declareDate,105)  like '"+dateFormat.format(cmbDeclareDate.getValue())+"') as othercost,b.endPrice,CAST(a.Length as " +
						"varchar(120)) +'X'+CAST(a.width as varchar)as 'labelSize',CAST(a.LengthConsumption as varchar(120)) " +
						"+'X'+CAST(a.widthConsumption as varchar)as 'labelSizeConsum'," +
						//"(select SUM(CAST(LTRIM(RTRIM(CAST(SUBSTRING(qty,1,CHARINDEX(' ',qty)-1) as varchar(120)) )) as money )) " +
						"(select SUM(CAST(LTRIM(RTRIM(CAST(qty as varchar(120)) )) as money )) " +
						" from tbCostSheetRmDetails a inner join tbCostSheetInfo b on a.jobNo=b.jobNo where  convert(date," +
						"b.declareDate,105)  like '"+dateFormat.format(cmbDeclareDate.getValue())+"'  and b.fGCode like '"+cmbProductName.getValue()+"' and a.itemType like 'Raw Material') " +
						"as 'HDPE'," +
						//"(select  SUM(CAST(LTRIM(RTRIM(CAST(SUBSTRING(qty,1,CHARINDEX(' ',qty)-1) as varchar(120)) )) as money ))  " +
						"(select SUM(CAST(LTRIM(RTRIM(CAST(qty as varchar(120)) )) as money )) " +
						"from tbCostSheetRmDetails a inner join tbCostSheetInfo b on a.jobNo=b.jobNo where  " +
						"convert(date,b.declareDate,105)  like '"+dateFormat.format(cmbDeclareDate.getValue())+"' and b.fGCode like '"+cmbProductName.getValue()+"' and a.itemType like " +
						"'Ink')as 'MB'  from tbFinishedProductInfo a inner join tbCostSheetInfo b on a.vProductId=b.fGCode inner " +
						"join tbCostSheetRmDetails c on c.jobNo=b.jobNo inner join  tbRawItemInfo d on d.vRawItemCode=c.rawItemCode " +
						" where  a.vProductId like '"+cmbProductName.getValue()+"' and   convert(date,b.declareDate,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"' union select distinct " +
						"'2' as type, a.vProductId,a.vProductName,a.vUnitName,a.vCategoryName, convert(date,b.declareDate,105) " +
						"as declareDate,'','','','','wastage' as wastage,(select(select isnull(SUM(wastageRawCost),0)   from  " +
						"tbCostSheetInfo a where a.fGCode like '"+cmbProductName.getValue()+"' and convert(date,a.declareDate,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"' )-" +
						" (select  ISNULL(SUM(a.amount),0)  from tbCostSheetRmDetails a inner join tbCostSheetInfo b on a.jobNo=b.jobNo " +
						"where b.fGCode like '"+cmbProductName.getValue()+"' and convert(date, b.declareDate ,105)like '"+dateFormat.format(cmbDeclareDate.getValue())+"' ) as wastage)," +
						"b.transportCost,b.packingCost,'',b.markupAmt,b.wstPercent,(select ISNULL(SUM(transportCost+packingCost),0)  " +
						" from tbCostSheetInfo where fGCode like '"+cmbProductName.getValue()+"' and CONVERT(date,declareDate,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"') as " +
						"othercost,b.endPrice,'','','0','0'  from tbFinishedProductInfo a inner join tbCostSheetInfo b on " +
						"a.vProductId=b.fGCode inner join tbCostSheetRmDetails c on c.jobNo=b.jobNo inner join tbRawItemInfo d  " +
						"on d.vRawItemCode=c.rawItemCode where  a.vProductId like '"+cmbProductName.getValue()+"' and   convert(date,b.declareDate,105) like " +
						"'"+dateFormat.format(cmbDeclareDate.getValue())+"' ) as  a  order by a.type ";
				System.out.println("cmbType2"+query);
				subsql = "select  b.overheadId, (select  Ledger_Name from tbLedger where Ledger_Id like b.overheadId ) as CoasHead ," +
						"b.amount as cost  from  tbCostSheetInfo a inner join tbCostSheetConvertionDetails b on a.jobNo=b.jobNo where a.fGCode like" +
						" '"+cmbProductName.getValue()+"' and convert(date,a.declareDate ,105) like '"+dateFormat.format(cmbDeclareDate.getValue())+"' " ;
				System.out.println("cmbType2"+subsql);
				report="report/production/CostSheetBottle.jasper";
			}

			System.out.println("sqlConsumption"+subsql);
			System.out.println("sql"+query);

			hm.put("sql", query);
			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,report,
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else{
				showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp){
			showNotification("Error1",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		setWidth("520px");
		setHeight("276px");
		mainLayout.setMargin(false);
		
		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("Date: ");
		mainLayout.addComponent(lblFDate, "top:16.0px;left:20.0px;");
		
		cmbDeclareDate=new ComboBox();
		cmbDeclareDate.setImmediate(true);
		cmbDeclareDate.setWidth("120px");
		cmbDeclareDate.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDeclareDate, "top:15.0px; left:120.0px;");

	/*	formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:15.0px;left:120.0px;");*/

		lblType = new Label();
		lblType.setImmediate(false);
		lblType.setWidth("-1px");
		lblType.setHeight("-1px");
		lblType.setValue("Production Type :");
		mainLayout.addComponent(lblType, "top:43.0px;left:20.0px;");

		cmbType = new ComboBox();
		cmbType.setImmediate(true);
		cmbType.setWidth("200px");
		cmbType.setHeight("24px");
		cmbType.setNullSelectionAllowed(false);
		cmbType.setNewItemsAllowed(false);
		cmbType.setEnabled(true);
		mainLayout.addComponent( cmbType, "top:41.0px;left:120.0px;");
		
		lblpartyname = new Label();
		lblpartyname.setImmediate(false);
		lblpartyname.setWidth("-1px");
		lblpartyname.setHeight("-1px");
		lblpartyname.setValue("Party Name:");
		mainLayout.addComponent(lblpartyname, "top:69.0px;left:20.0px;");

		cmbpartyname = new ComboBox();
		cmbpartyname.setImmediate(true);
		cmbpartyname.setWidth("280px");
		cmbpartyname.setHeight("24px");
		cmbpartyname.setNullSelectionAllowed(false);
		cmbpartyname.setNewItemsAllowed(false);
		cmbpartyname.setEnabled(true);
		mainLayout.addComponent( cmbpartyname, "top:67.0px;left:120.0px;");
		
		
		

		lblProductName = new Label();
		lblProductName.setImmediate(false);
		lblProductName.setWidth("-1px");
		lblProductName.setHeight("-1px");
		lblProductName.setValue("Product Name :");
		mainLayout.addComponent(lblProductName, "top:95.0px;left:20.0px;");

		cmbProductName= new ComboBox();
		cmbProductName.setImmediate(true);
		cmbProductName.setWidth("280px");
		cmbProductName.setHeight("24px");
		cmbProductName.setNullSelectionAllowed(true);
		cmbProductName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbProductName, "top:93.0px;left:120.0px;");




		/*		formDate = new Label();
		formDate.setImmediate(false);
		formDate.setWidth("-1px");
		formDate.setHeight("-1px");
		formDate.setValue("To Date: ");
		mainLayout.addComponent(formDate, "top:91.0px;left:62.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:93.0px;left:130.0px;");*/

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:145.0px; left:140.0px");

		lblLine=new Label("<font color='#e65100'>===============================================================================================================================================================================================================================================================</font>", Label.CONTENT_XHTML);
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		mainLayout.addComponent(lblLine, "top:171.0px;left:0.0px;");

		previewButton.setWidth("90px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:197.opx; left:150.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:197.opx; left:240.0px");

		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
