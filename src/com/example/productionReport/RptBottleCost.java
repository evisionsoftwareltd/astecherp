package com.example.productionReport;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.xerces.impl.dtd.models.CMBinOp;
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
public class RptBottleCost extends Window{

	SessionBean sessionBean;
	
	private Label lblType=new Label("From : ");
	private ComboBox cmbType=new ComboBox();
	
	private Label lblProductName=new Label("To : ");
	private ComboBox cmbProductName=new ComboBox();
	private Label lblProductUnit = new Label();

	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate;
	private Label lblProductNameDate;
	private Label lblLine;

	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();

	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");

	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	private AbsoluteLayout mainLayout;

	public RptBottleCost(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("BOTTLE COST REPORT::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		productionTypeDataLoad();
		this.addComponent(mainLayout);
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

			//			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbType.addItem(element[0].toString());
				//				if(i==0){
				//					cmbType.setValue(element[0]);
				//					 productNameData();
				//					i++;
				//				}
				cmbType.setItemCaption(element[0].toString(), element[1].toString());
//				cmbType.setValue("PT-2");
			}
			productNameData();
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
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
					if(cmbProductName.getValue()!=null)
					{
						reportView(); 
					}
					else
					{
						showNotification("Please Select Issue TO",Notification.TYPE_WARNING_MESSAGE); 
					}

				}
				else
				{
					showNotification("Please Select Issue From",Notification.TYPE_WARNING_MESSAGE);
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

	}

	private void productNameData() {

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select vProductId, vProductName, vUnitName from tbFinishedProductInfo";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbProductName.addItem(element[0].toString());
				cmbProductName.setItemCaption(element[0].toString(), element[1].toString());
				
				lblProductUnit.setValue(element[2].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
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
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			hm.put("productName",cmbProductName.getItemCaption(cmbProductName.getValue()) );
			hm.put("productUnit",lblProductUnit.getValue() );
			hm.put("user", sessionBean.getUserName());


			query = "select * from  "
					+"( "
					  +" select '1' as type , a.vProductId, a.Dia,a.LengthConsumption,a.Length,a.perSqmQty,d.vRawItemName,c.amount,b.transportCost,b.packingCost,b.markupPercent,b.markupAmt,'' wastagepercent,(select ISNULL(SUM(transportCost+packingCost),0)   from tbCostSheetInfo where fGCode like 'FI-272' and CONVERT(date,declareDate,105)  like '2015-12-03') as othercost,b.endPrice  from tbFinishedProductInfo a "
					+"inner join "
					+"tbCostSheetInfo b on a.vProductId=b.fGCode "
					+"inner join "
					+"tbCostSheetRmDetails c "
					+"on "
					+"c.jobNo=b.jobNo "
					+"inner join  "
					+"tbRawItemInfo d "
					+"on "
					+"d.vRawItemCode=c.rawItemCode  "
					+"where  a.vProductId like 'FI-272' and   convert(date,b.declareDate,105) like '2015-12-03' "
					+"union "
					+"select distinct '2' as type, '', '','','','','wastage' as wastage,(select(select isnull(SUM(wastageRawCost),0)   from  tbCostSheetInfo a where a.fGCode like 'FI-272' and convert(date,a.declareDate,105) like '2015-12-03' )- (select  ISNULL(SUM(a.amount),0)  from tbCostSheetRmDetails a inner join tbCostSheetInfo b on a.jobNo=b.jobNo where b.fGCode like 'FI-272' and convert(date, b.declareDate ,105)like '2015-12-03' ) as wastage"
					+"),b.transportCost,b.packingCost,'',b.markupAmt,b.wstPercent,(select ISNULL(SUM(transportCost+packingCost),0)   from tbCostSheetInfo where fGCode like 'FI-272' and CONVERT(date,declareDate,105) like '2015-12-03') as othercost,b.endPrice  from tbFinishedProductInfo a "
					+"inner join "
					+"tbCostSheetInfo b on a.vProductId=b.fGCode "
					+"inner join "
					+"tbCostSheetRmDetails c "
					+"on "
					+"c.jobNo=b.jobNo "
					+"inner join "
					+"tbRawItemInfo d  "
					+"on "
					+"d.vRawItemCode=c.rawItemCode "
					+"where  a.vProductId like 'FI-272' and   convert(date,b.declareDate,105) like '2015-12-03' "
					+") as  a  order by a.type ";

			String subsql = "";
			subsql = "select  b.overheadId, (select  Ledger_Name from tbLedger where Ledger_Id like b.overheadId ) as CoasHead ,b.amount as cost  from  tbCostSheetInfo a "
					+"inner join "
					+"tbCostSheetConvertionDetails b "
					+"on "
					+"a.jobNo=b.jobNo "
					+"where a.fGCode like 'FI-272' and convert(date,a.declareDate ,105) like '2015-12-03' " ;
			System.out.println("sqlConsumption"+subsql);

			hm.put("sql", query);
			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/CostSheetLabel.jasper",
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

	private AbsoluteLayout buildMainLayout() 
	{

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("460px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		lblType = new Label();
		lblType.setImmediate(false);
		lblType.setWidth("-1px");
		lblType.setHeight("-1px");
		lblType.setValue("Production Type :");
		mainLayout.addComponent(lblType, "top:16.0px;left:40.0px;");

		cmbType = new ComboBox();
		cmbType.setImmediate(true);
		cmbType.setWidth("200px");
		cmbType.setHeight("24px");
		cmbType.setNullSelectionAllowed(false);
		cmbType.setNewItemsAllowed(false);
		cmbType.setEnabled(true);
		mainLayout.addComponent( cmbType, "top:15.0px;left:140.0px;");

		lblProductName = new Label();
		lblProductName.setImmediate(false);
		lblProductName.setWidth("-1px");
		lblProductName.setHeight("-1px");
		lblProductName.setValue("Product Name :");
		mainLayout.addComponent(lblProductName, "top:41.0px;left:40.0px;");

		cmbProductName= new ComboBox();
		cmbProductName.setImmediate(true);
		cmbProductName.setWidth("200px");
		cmbProductName.setHeight("24px");
		cmbProductName.setNullSelectionAllowed(true);
		cmbProductName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbProductName, "top:41.0px;left:140.0px;");


		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("Date: ");
		mainLayout.addComponent(lblFDate, "top:67.0px;left:40.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:67.0px;left:140.0px;");

		/*		lblProductNameDate = new Label();
		lblProductNameDate.setImmediate(false);
		lblProductNameDate.setWidth("-1px");
		lblProductNameDate.setHeight("-1px");
		lblProductNameDate.setValue("To Date: ");
		mainLayout.addComponent(lblProductNameDate, "top:91.0px;left:62.0px;");

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
		mainLayout.addComponent(chklayout, "top:119.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setValue("____________________________________________________________________________________________________");
		mainLayout.addComponent(lblLine, "top:145.0px;left:25.0px;");

		previewButton.setWidth("90px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:171.opx; left:130.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:171.opx; left:220.0px");

		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
