package com.example.productionReport;
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
import com.vaadin.ui.Window.Notification;
public class RptConvertionCostPartyAndFgWise extends Window{

	SessionBean sessionBean;

	private Label lblParty;
	private ComboBox cmbParty;
	private Label lblProduction;
	private ComboBox cmbProduction;
	private Label lblFGName;
	private ComboBox cmbFGName;

	private CheckBox chkAllType;
	private Label lblAllType=new Label();
	
	private CheckBox chkAllParty;
	private Label lblAllParty=new Label();
	
	private CheckBox chkAllFG;
	private Label lblAllFG=new Label();
	
	private Label lblFDate;
	private Label lblToDate;
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblLine;

	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;

	public RptConvertionCostPartyAndFgWise(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("CONVERTION COST PARTY AND FG WISE::"+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();		
		this.addComponent(mainLayout);

		cmbPartyData();
		setEventAction();
	}

	private void cmbPartyData()
	{
		cmbParty.removeAllItems();

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql = "select  distinct partyid,partyName from funcCOGPConVertionCost('"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"')";
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();

				cmbParty.addItem(element[0].toString());
				cmbParty.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void fgDataLoad(){
		cmbFGName.removeAllItems();

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql = "select   distinct fgid,fgName from funcCOGPConVertionCost('"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"') where partyId like '"+cmbParty.getValue()+"'";
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();

				cmbFGName.addItem(element[0].toString());
				cmbFGName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void setEventAction() {

		/*cmbProduction.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProduction.getValue()!=null)
				{
					//cmbJobOrderData();
				}
			}
		});*/
		
		formDate.addListener(new ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
				cmbPartyData();
			}
		});
		toDate.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				cmbPartyData();
			}
		});
		cmbParty.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbParty.getValue()!=null)
				{
					fgDataLoad();
				}
			}
		});

		previewButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbParty.getValue()!=null||chkAllParty.booleanValue())
				{
					reportView();
				}
				else
				{
					showNotification("Please Select Party Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		/*chkAllType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllType.booleanValue();
				if(bv==true)
				{
					cmbProduction.setEnabled(false);
					cmbProduction.setValue(null);					
				}
				else{
					cmbProduction.setEnabled(true);
					cmbProduction.focus();
				}
			}
		});*/
		
		chkAllParty.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllParty.booleanValue();
				if(bv==true)
				{
					cmbParty.setEnabled(false);
					cmbParty.setValue(null);					
				}
				else{
					cmbParty.setEnabled(true);
					cmbParty.focus();
				}
			}
		});

		chkAllFG.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllFG.booleanValue();
				if(bv==true)
				{
					cmbFGName.setEnabled(false);
					cmbFGName.setValue(null);					
				}
				else{
					cmbFGName.setEnabled(true);
					cmbFGName.focus();
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

	/*private void productionStepDataLoad() {

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql = " select distinct a.StepId, a.StepName from tbProductionStep a" +
					" inner join tbTubeProductionInfo b" +
					" on b.Stepid = a.StepId" +
					" where b.jobOrderNo like  '"+cmbJobOrder.getValue()+"'";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbStep.addItem(element[0]);

				cmbStep.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}*/

	

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
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromdate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			//hm.put("issueFrom",cmbType.getItemCaption(cmbType.getValue()) );
			//hm.put("issueTo",cmbType.getItemCaption(cmbStep.getValue()) );


			/*query= " select  a.IssueDate,a.IssueNo,a.ChallanNo, a.FinishedGood ,b.IssueQty,b.RMitemCode ,case when  Fromflag='1'  then c.vRawItemName  else d.vProductName  end as  productName,b.unit, e.vProductName as finishProductName     from tbTubeIssueInfo a "
					+" inner join "
					+" tbTubeIssueDetails b "
					+"on a.IssueNo=b.IssueNo  "
					+"left join "
					+"tbRawItemInfo c " 
					+"on c.vRawItemCode=b.RMitemCode "
					+"left join "
					+"tbFinishedProductInfo d "
					+"on d.vProductId =b.RMitemCode "
					+"left join "
					+"tbFinishedProductInfo e "
					+"on e.vProductId=a.FinishedGood "
					+"where CONVERT(Date,a.IssueDate,105) <='"+datef.format(formDate.getValue())+"'  and a.IssueFrom like '"+cmbType.getValue()+"' and a.IssueTo like '"+cmbStep.getValue()+"' ";*/
			/*query="select fgid,fgName,rawItemId,rawItemName,sum(finishedPcs)as finishedPcs,sum(cogpStdQty) "+
					" as cogpStdQty,sum(printingInk)as PrintingInk,sum(hdpe)as hdpe,sum(mb)as mb," +
					"(SUM(laminateWastage)+SUM(hdpeWastage)+SUM(mbWastage)+SUM(inkWastage)) as ttlWastage from funcCOGPstandard "+
					" ('"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"','"+ new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"') group by fgid,fgName,rawItemId,rawItemName";*/

			/*query="select fgid,fgName,rawItemId,rawItemName,sum(finishedPcs)as finishedPcs,sum(cogpStdQty) "+
					"as cogpStdQty,SUM(labelStock)as labelStock,SUM(labelInk)as labelInk,SUM(lblStockWastage)as lblStockWastage, "+
					"SUM(lblinkWastage)as lblInkWastage,sum(printingInk)as PrintingInk,sum(hdpe)as hdpe,sum(mb)as mb, "+
					"(SUM(laminateWastage)+SUM(hdpeWastage)+SUM(mbWastage)+SUM(inkWastage)) as ttlWastage from funcCOGPstandard  "+
					"('"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"','"+ new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"') group by fgid,fgName,rawItemId,rawItemName";
			
			String subQuery="select fgid,fgName,rawItemId,rawItemName,finishedPcs,sum(cogpStdQty)as LabelStock," +
					"sum(PrintingInk)as labelInk,sum(laminateWastage)as labelStockWastage,sum(inkWastage)as " +
					"labelInkWastage from funcCOGPLabel('"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"'," +
					"'"+ new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"')group by fgid,fgName,rawItemId,rawItemName,finishedPcs";*/
			//query=" select *,(select vUnitName from tbFinishedProductInfo where vProductId like fgId) fgunit,(select vUnitName from tbRawItemInfo where vRawItemCode like rawId) rawunit from " +
					//"funcCOGPStandardAstech('"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"','"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"') order by  categoryId,fgId ";

			/*query="select categoryName,fgName,unit,finishedPcs,rawid,rawName,stdQty,SUM(consumpedQty)consumpedQty,rate,sum(amount)amount from  "+
					" funcCOGPStandardAstech('"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"','"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"') " +
					"group by categoryName,fgName,unit,finishedPcs,rawid,rawName,stdQty,rate   "+
					" order by cast(subString(rawId,CHARINDEX('-',rawid)+1,len(rawid)-CHARINDEX('-',rawid))as int)";*/
			
			query="select partyId,partyName,fgid,fgName,ledgerName,finishedPcs,costPerPcs,SUM(convertionAmount)convertionAmt from "+
					"funcCOGPConVertionCost('"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"','"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"') where convertionAmount>0 and partyId like '%' and fgId like '%' "+
					"group by partyId,partyName,fgid,fgName,ledgerName,finishedPcs,costPerPcs";
			hm.put("sql", query);
			//hm.put("subSql", subQuery);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptConvertionCostPartAndFgWise.jasper",
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
	private AbsoluteLayout buildMainLayout() {

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("460px");
		mainLayout.setHeight("220px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("300px");

		/*lblProduction = new Label("Production Type :");
		lblProduction.setImmediate(false);
		lblProduction.setWidth("-1px");
		lblProduction.setHeight("-1px");
		mainLayout.addComponent(lblProduction, "top:15.0px;left:40.0px;");

		cmbProduction = new ComboBox();
		cmbProduction.setImmediate(true);
		cmbProduction.setWidth("200px");
		cmbProduction.setHeight("24px");
		cmbProduction.setNullSelectionAllowed(false);
		cmbProduction.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbProduction, "top:13.0px;left:170.0px;");*/
		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:15.0px;left:40.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:13.0px;left:170.0px;");
		
		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("From Date: ");
		mainLayout.addComponent(lblToDate, "top:40.0px;left:40.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:37.0px;left:170.0px;");

		/*lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:67.0px;left:40.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:67.0px;left:140.0px;");*/
		
		/*chkAllType = new CheckBox("All");
		chkAllType .setImmediate(true);
		chkAllType .setValue(true);
		chkAllType .setWidth("-1px");
		chkAllType .setHeight("-1px");
		mainLayout.addComponent(chkAllType , "top:15.0px;left:377.0px;");*/


		lblParty = new Label("Party Name :");
		lblParty.setImmediate(false);
		lblParty.setWidth("-1px");
		lblParty.setHeight("-1px");	
		mainLayout.addComponent(lblParty, "top:67.0px;left:40.0px;");
		

		cmbParty= new ComboBox();
		cmbParty.setImmediate(true);
		cmbParty.setWidth("200px");
		cmbParty.setHeight("24px");
		cmbParty.setNullSelectionAllowed(true);
		cmbParty.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbParty, "top:63.0px;left:170.0px;");
		
		chkAllParty = new CheckBox("All");
		chkAllParty.setImmediate(true);
		//chkAllParty.setValue(true);
		chkAllParty.setWidth("-1px");
		chkAllParty.setHeight("-1px");
		mainLayout.addComponent(chkAllParty, "top:63.0px;left:377.0px;");

		lblFGName = new Label("Finished Goods Name :");
		lblFGName.setImmediate(false);
		lblFGName.setWidth("-1px");
		lblFGName.setHeight("-1px");
		mainLayout.addComponent(lblFGName, "top:94.0px;left:40.0px;");
		
		cmbFGName = new ComboBox();
		cmbFGName.setImmediate(true);
		cmbFGName.setWidth("200px");
		cmbFGName.setHeight("24px");
		cmbFGName.setNullSelectionAllowed(true);
		cmbFGName.setNewItemsAllowed(false);
		cmbFGName.setEnabled(true);
		mainLayout.addComponent( cmbFGName, "top:91.0px;left:170.0px;");


		chkAllFG = new CheckBox("All");
		chkAllFG.setImmediate(true);
		//chkAllFG.setValue(true);
		chkAllFG.setWidth("-1px");
		chkAllFG.setHeight("-1px");
		mainLayout.addComponent(chkAllFG, "top:91.0px;left:377.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);


		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:160.0px;left:0.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));


		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));


			//chkAll.setVisible(true);
		//lblAll.setVisible(false);
		
		mainLayout.addComponent(chklayout, "top:125.0px; left:140.0px");
		mainLayout.addComponent(previewButton,"top:185.opx; left:135.0px");
		mainLayout.addComponent(exitButton,"top:185.opx; left:220.0px");

		return mainLayout;


	}
}
