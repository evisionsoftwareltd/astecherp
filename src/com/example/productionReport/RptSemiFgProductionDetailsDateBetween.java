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
public class RptSemiFgProductionDetailsDateBetween extends Window{

	SessionBean sessionBean;
	private Label lblSemiFgName;
	private ComboBox cmbType=new ComboBox();
	private Label lblProductionType;
	private ComboBox cmbSemiFgName=new ComboBox();
	private CheckBox chkAll=new CheckBox("All");
	private CheckBox chkAllType=new CheckBox("All");
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate,lblToDate;
	private Label lblProductionTypeDate;
	private Label lblLine;
	private Label lblReportType =new Label("Report Type :");
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private OptionGroup opgSummary;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Summary","Details"});


	private AbsoluteLayout mainLayout;

	public RptSemiFgProductionDetailsDateBetween(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("PRODUCT WISE PRODUCTION ::"+sessionBean.getCompany());
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
			String sql="select productTypeId,productTypeName from tbProductionType where productTypeId in('PT-1','PT-2','PT-4')";
			List list=session.createSQLQuery(sql).list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbType.addItem(element[0].toString());				
				cmbType.setItemCaption(element[0].toString(), element[1].toString());
			}
			cmbType.addItem("Assemble");
			cmbType.addItem("Dry Offset Printing");
			cmbType.addItem("Screen Printing");
			cmbType.addItem("Heat Trasfer Label");
			cmbType.addItem("Manual Printing");
			cmbType.addItem("Labeling");
			cmbType.addItem("Lacqure");
			cmbType.addItem("Cap Folding");
			cmbType.addItem("Stretch Blow Molding");
			cmbType.addItem("Shrink");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setEventAction() {		

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbType.getValue()!=null|| chkAllType.booleanValue()==true )
				{
					if(cmbSemiFgName.getValue()!=null|| chkAll.booleanValue()==true)
					{

						reportView(); 

					}
					else
					{
						showNotification("Please Select Product Name",Notification.TYPE_WARNING_MESSAGE); 
					}

				}
				else
				{
					showNotification("Please Select Production Type",Notification.TYPE_WARNING_MESSAGE);
				}

			}
		});
		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		chkAllType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllType.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbType.setEnabled(false);
					cmbType.setValue(null);
					cmbSemiFgLoad();
				}
				else{
					cmbType.setEnabled(true);
					cmbType.focus();
				}
			}
		});
		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAll.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbSemiFgName.setEnabled(false);
					cmbSemiFgName.setValue(null);

				}
				else{
					cmbSemiFgName.setEnabled(true);
					cmbSemiFgName.focus();
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
		cmbType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbType.getValue()!=null){
					cmbSemiFgLoad();
				}
				else{
					cmbSemiFgName.removeAllItems();
				}
			}
		});

	}
	private void cmbSemiFgLoad() {
		cmbSemiFgName.removeAllItems();
		String type="%";
		if(cmbType.getValue()!=null){
			type=cmbType.getValue().toString();
		}
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="";

			if(type.equalsIgnoreCase("PT-1")||type.equalsIgnoreCase("PT-2")||type.equalsIgnoreCase("PT-4")){
				sql="select semiFgCode,semiFgName,color from tbSemiFgInfo where productionTypeId like '"+type+"' order by semiFgName";
				List list=session.createSQLQuery(sql).list();
				for(Iterator iter=list.iterator();iter.hasNext();){
					Object element[]=(Object[]) iter.next();
					cmbSemiFgName.addItem(element[0]);
					cmbSemiFgName.setItemCaption(element[0], element[1].toString()+" # "+element[2].toString());
				}
			}
			else if(type.equalsIgnoreCase("Lacqure")){
				sql="select distinct b.fgCode,b.fgName from tbLacqureDailyProductionInfo a "+
						" inner join tbLacqureDailyProductionDetails b  "+
						" on a.productionNo=b.productionNo where a.productionStep='"+type+"'";
				List list=session.createSQLQuery(sql).list();
				for(Iterator iter=list.iterator();iter.hasNext();){
					Object element[]=(Object[]) iter.next();
					cmbSemiFgName.addItem(element[0]);
					cmbSemiFgName.setItemCaption(element[0], element[1].toString());
				}
			}
			else if(type.equalsIgnoreCase("Assemble"))
			{
				sql=     "select semiFgId,semiFgName from tbFinishedProductDetailsNew where consumptionStage='Assemble' "
						 +"and semiFgSubId='' "
						 +"union "
						 +"select semiFgSubId,semiFgSubName from tbFinishedProductDetailsNew where consumptionStage='Assemble' "
						 +"and semiFgSubId!='' ";
				
				List list=session.createSQLQuery(sql).list();
				for(Iterator iter=list.iterator();iter.hasNext();){
					Object element[]=(Object[]) iter.next();
					cmbSemiFgName.addItem(element[0]);
					cmbSemiFgName.setItemCaption(element[0], element[1].toString());
				}
			}
			else
			{
				sql="select distinct b.fgCode,b.fgName from tbLabelingPrintingDailyProductionInfo a "+
						" inner join tbLabelingPrintingDailyProductionDetails b  "+
						" on a.productionNo=b.productionNo where a.productionStep='"+type+"'";
				List list=session.createSQLQuery(sql).list();
				for(Iterator iter=list.iterator();iter.hasNext();){
					Object element[]=(Object[]) iter.next();
					cmbSemiFgName.addItem(element[0]);
					cmbSemiFgName.setItemCaption(element[0], element[1].toString());
				}
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

		String SemiFg = "";

		if(chkAll.booleanValue()==true)
			SemiFg="%";
		else
			SemiFg=cmbSemiFgName.getValue().toString();

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("openyear",new SimpleDateFormat("yyyy").format(formDate.getValue()) );
			hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("fromDate", new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()));
			hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			//hm.put("productionStep",cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue()) );
			//hm.put("user", sessionBean.getUserName());




			String semiFg="%";
			if(cmbSemiFgName.getValue()!=null){
				semiFg=cmbSemiFgName.getValue().toString();
			}
			if(opgSummary.getValue().toString().equalsIgnoreCase("Summary"))
			{


				if(cmbType.getValue().toString().equalsIgnoreCase("PT-1")||cmbType.getValue().toString().equalsIgnoreCase("PT-2")
						||cmbType.getValue().toString().equalsIgnoreCase("PT-4")){
					query=" select FinishedProduct,(select semiFgName from tbSemiFgInfo where semiFgCode=FinishedProduct)semiFgName," +
							"(select unit from tbSemiFgInfo where semiFgCode=FinishedProduct)unit," +
							"(select color from tbSemiFgInfo where semiFgCode=FinishedProduct)color," +
							"(select stdWeight from tbSemiFgInfo where semiFgCode=FinishedProduct)stdWeight," +
							"(SUM(ShiftAPcs)+SUM(ShiftBPcs))GoodQty,(SUM(RejShiftA)+SUM(RejShiftB))RejectQty," +
							"((SUM(ShiftAPcs)+SUM(ShiftBPcs))+ (SUM(RejShiftA)+SUM(RejShiftB))) Total from " +
							"tbMouldProductionDetails a inner join tbMouldProductionInfo b on a.ProductionNo=b.ProductionNo" +
							" where b.productionType='"+cmbType.getValue()+"' and FinishedProduct like '"+semiFg+"' and convert(date,b.ProductionDate,105) between " +
							"'"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"'" +
							" group by FinishedProduct order by semiFgName";
				}
				else if(cmbType.getValue().toString().equalsIgnoreCase("Lacqure")){
					query="select  b.fgCode FinishedProduct,b.fgName semiFgName,'' unit,'' color,'0' stdWeight,isnull(SUM(totalShift),0)GoodQty, "+
							" isnull(SUM(totalReject),0)RejectQty,(isnull(SUM(totalShift),0)+isnull(SUM(totalReject),0))Total from tbLacqureDailyProductionInfo a "+
							" inner join tbLacqureDailyProductionDetails b  "+
							" on a.productionNo=b.productionNo where  a.productionStep='"+cmbType.getValue()+"' and   b.fgCode like '"+semiFg+"' "+
							" and convert(date,a.productionDate,105) between '"+datef.format(formDate.getValue())+"' " +
							" and '"+datef.format(toDate.getValue())+"' group by b.fgCode,b.fgName "+
							" order by fgName";
				}
				else if(cmbType.getValue().toString().equalsIgnoreCase("Stretch Blow Molding")){
					query="select  b.fgCode FinishedProduct,b.fgName semiFgName,'' unit,'' color,'0' stdWeight,isnull(SUM(totalShift),0)GoodQty, "+
							" isnull(SUM(totalReject),0)RejectQty,(isnull(SUM(totalShift),0)+isnull(SUM(totalReject),0))Total from tbSBMDailyProductionInfo a "+
							" inner join tbSBMDailyProductionDetails b  "+
							" on a.productionNo=b.productionNo where  a.productionStep='"+cmbType.getValue()+"' and   b.fgCode like '"+semiFg+"' "+
							" and convert(date,a.productionDate,105) between '"+datef.format(formDate.getValue())+"' " +
							" and '"+datef.format(toDate.getValue())+"' group by b.fgCode,b.fgName "+
							" order by fgName";
				}
				else if(cmbType.getValue().toString().equalsIgnoreCase("Assemble"))
				{
                    
					query=
							 "select * from "
							 +"( "
							     +"select b.masterProductName FinishedProduct,b.semiFgId,b.semiFgName, "
							  +"ISNULL(SUM(b.assembleQty),0)assembleQty,ISNULL(SUM(b.rejectQty),0)rejectQty, " 
							  +"ISNULL(SUM(b.totalQty) ,0)totalQty,''unit,''color,'0' stdWeight  from tbMasterProductAssembleInfo a inner join tbIngradiantAssembleDetails b "
							 +"on a.transactionId=b.transactionId and a.masterProductId=b.masterProductId "
							 +"where CONVERT(date,a.assembleDate,105) between '"+datef.format(formDate.getValue())+"' "
							 +"and '"+datef.format(toDate.getValue())+"' and b.secondaryStageFgId=''"
							 +"and b.semiFgId like '"+semiFg+"' group by b.masterProductId,b.masterProductName,b.semiFgId,b.semiFgName "
							 
							+" union "
							 
							 + "select b.masterProductName FinishedProduct,b.secondaryStageFgId,b.secondaryStageFgName,"
							  +"ISNULL(SUM(b.assembleQty),0)assembleQty,ISNULL(SUM(b.rejectQty),0)rejectQty, " 
							  +"ISNULL(SUM(b.totalQty) ,0)totalQty,''unit,''color,'0' stdWeight    from tbMasterProductAssembleInfo a inner join tbIngradiantAssembleDetails b "
							 +"on a.transactionId=b.transactionId and a.masterProductId=b.masterProductId "
							 +"where CONVERT(date,a.assembleDate,105) between '"+datef.format(formDate.getValue())+"' "
							 +"and '"+datef.format(toDate.getValue())+"' and b.secondaryStageFgId!='' "
							 +"and b.secondaryStageFgId like '"+semiFg+"'  group by b.masterProductId,b.masterProductName,b.secondaryStageFgId,b.secondaryStageFgName "
							 
							 +") as temp ";
					
					
					
				}
				else
				{
					query="select  b.fgCode FinishedProduct,b.fgName semiFgName,'' unit,'' color,'0' stdWeight,isnull(SUM(totalShift),0)GoodQty, "+
							" isnull(SUM(totalReject),0)RejectQty,(isnull(SUM(totalShift),0)+isnull(SUM(totalReject),0))Total from tbLabelingPrintingDailyProductionInfo a "+
							" inner join tbLabelingPrintingDailyProductionDetails b  "+
							" on a.productionNo=b.productionNo where  a.productionStep='"+cmbType.getValue()+"' and   b.fgCode like '"+semiFg+"' "+
							" and convert(date,a.productionDate,105) between '"+datef.format(formDate.getValue())+"' " +
							" and '"+datef.format(toDate.getValue())+"' group by b.fgCode,b.fgName "+
							" order by fgName";
				}

				hm.put("sql", query);
				List list=session.createSQLQuery(query).list();
				if(!list.isEmpty())
				{
					String Report="";
					
					if(cmbType.getValue().toString().equalsIgnoreCase("Assemble"))
					{
						System.out.println("Rabiul Hasan");
						Report="report/production/ProductwiseproductionReportAssemble .jasper";	
					}
					else
					{
						Report="report/production/ProductwiseproductionReport .jasper";		
						
					}
					
					Window win = new ReportViewerNew(hm,Report,
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
			else if(opgSummary.getValue().toString().equalsIgnoreCase("Details"))//Details
			{
				if(cmbType.getValue().toString().equalsIgnoreCase("PT-1")||cmbType.getValue().toString().equalsIgnoreCase("PT-2")
						||cmbType.getValue().toString().equalsIgnoreCase("PT-4")){
					query="select convert(date,b.ProductionDate,105)ProductionDate,FinishedProduct," +
							"(select semiFgName from tbSemiFgInfo where semiFgCode=FinishedProduct)semiFgName," +
							"(select unit from tbSemiFgInfo where semiFgCode=FinishedProduct)unit," +
							"(select color from tbSemiFgInfo where semiFgCode=FinishedProduct)color," +
							"(select stdWeight from tbSemiFgInfo where semiFgCode=FinishedProduct)stdWeight," +
							"(SUM(ShiftAPcs)+SUM(ShiftBPcs))GoodQty,(SUM(RejShiftA)+SUM(RejShiftB))RejectQty," +
							"((SUM(ShiftAPcs)+SUM(ShiftBPcs))+ (SUM(RejShiftA)+SUM(RejShiftB))) Total from " +
							"tbMouldProductionDetails a inner join tbMouldProductionInfo b on a.ProductionNo=b.ProductionNo" +
							" where b.productionType='"+cmbType.getValue()+"' and   FinishedProduct like '"+semiFg+"' and convert(date,b.ProductionDate,105) between " +
							"'"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"'" +
							"group by convert(date,b.ProductionDate,105),FinishedProduct order by semiFgName,convert(date,b.ProductionDate,105)";
				}
				else if(cmbType.getValue().toString().equalsIgnoreCase("Lacqure")){
					query="select  CONVERT(date,a.productionDate,105)productionDate,b.fgCode FinishedProduct,b.fgName semiFgName,'' unit,'' color, "+
							" '0' stdWeight,isnull(SUM(totalShift),0)GoodQty, "+
							" isnull(SUM(totalReject),0)RejectQty,(isnull(SUM(totalShift),0)+isnull(SUM(totalReject),0))Total from tbLacqureDailyProductionInfo a "+
							" inner join tbLacqureDailyProductionDetails b  "+
							" on a.productionNo=b.productionNo where a.productionStep='"+cmbType.getValue()+"' and  b.fgCode like '"+semiFg+"' "+
							" and convert(date,a.productionDate,105) between '"+datef.format(formDate.getValue())+"'" +
							" and '"+datef.format(toDate.getValue())+"'  "+
							" group by CONVERT(date,a.productionDate,105),b.fgCode,b.fgName  "+
							" order by fgName,CONVERT(date,a.productionDate,105)";
				}
				else if(cmbType.getValue().toString().equalsIgnoreCase("Stretch Blow Molding")){
					query="select  CONVERT(date,a.productionDate,105)productionDate,b.fgCode FinishedProduct,b.fgName semiFgName,'' unit,'' color, "+
							" '0' stdWeight,isnull(SUM(totalShift),0)GoodQty, "+
							" isnull(SUM(totalReject),0)RejectQty,(isnull(SUM(totalShift),0)+isnull(SUM(totalReject),0))Total from tbSBMDailyProductionInfo a "+
							" inner join tbSBMDailyProductionDetails b  "+
							" on a.productionNo=b.productionNo where a.productionStep='"+cmbType.getValue()+"' and  b.fgCode like '"+semiFg+"' "+
							" and convert(date,a.productionDate,105) between '"+datef.format(formDate.getValue())+"'" +
							" and '"+datef.format(toDate.getValue())+"'  "+
							" group by CONVERT(date,a.productionDate,105),b.fgCode,b.fgName  "+
							" order by fgName,CONVERT(date,a.productionDate,105)";
				}
				else if(cmbType.getValue().toString().equalsIgnoreCase("Assemble"))
				{
					query="select * from " 
						  +"( " 
						  +"select a.assembleDate, b.masterProductName FinishedProduct,b.semiFgId,b.semiFgName, " 
						  +"ISNULL(SUM(b.assembleQty),0)assembleQty,ISNULL(SUM(b.rejectQty),0)rejectQty, "  
						  +" ISNULL(SUM(b.totalQty) ,0)totalQty,''unit,''color,'0' stdWeight  from tbMasterProductAssembleInfo a inner join tbIngradiantAssembleDetails b " 
						  +"on a.transactionId=b.transactionId and a.masterProductId=b.masterProductId " 
						  +"where CONVERT(date,a.assembleDate,105) between '"+datef.format(formDate.getValue())+"' " 
						  +" and '"+datef.format(toDate.getValue())+"'  "
						  +" and b.semiFgId like '"+semiFg+"' group by a.assembleDate, b.masterProductId,b.masterProductName,b.semiFgId,b.semiFgName  "
										 	 
						  +") as temp  ";
				}
				else
				{
					query="select  CONVERT(date,a.productionDate,105)productionDate,b.fgCode FinishedProduct,b.fgName semiFgName,'' unit,'' color, "+
							" '0' stdWeight,isnull(SUM(totalShift),0)GoodQty, "+
							" isnull(SUM(totalReject),0)RejectQty,(isnull(SUM(totalShift),0)+isnull(SUM(totalReject),0))Total from tbLabelingPrintingDailyProductionInfo a "+
							" inner join tbLabelingPrintingDailyProductionDetails b  "+
							" on a.productionNo=b.productionNo where a.productionStep='"+cmbType.getValue()+"' and  b.fgCode like '"+semiFg+"' "+
							" and convert(date,a.productionDate,105) between '"+datef.format(formDate.getValue())+"'" +
							" and '"+datef.format(toDate.getValue())+"'  "+
							" group by CONVERT(date,a.productionDate,105),b.fgCode,b.fgName  "+
							" order by fgName,CONVERT(date,a.productionDate,105)";
				}
				
				
				String report="report/production/ProductwiseproductionDetailsReport .jasper";
				
				if(cmbType.getValue().toString().equalsIgnoreCase("Assemble"))
				{
					
					report="report/production/ProductwiseproductionReportAssembleDetails .jasper";
				}
				

				hm.put("sql", query);
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
					this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
				}
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
		mainLayout.setWidth("500px");
		mainLayout.setHeight("210px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("530px");
		setHeight("290px");

		lblProductionType = new Label();
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		lblProductionType.setValue("Production Type :");
		mainLayout.addComponent(lblProductionType, "top:16.0px;left:20.0px;");		

		cmbType = new ComboBox();
		cmbType.setImmediate(true);
		cmbType.setWidth("200px");
		cmbType.setHeight("24px");
		cmbType.setNullSelectionAllowed(false);
		cmbType.setNewItemsAllowed(false);
		cmbType.setEnabled(true);

		mainLayout.addComponent( cmbType, "top:15.0px;left:140.0px;");

		chkAllType.setImmediate(true);
		chkAllType.setWidth("-1px");
		chkAllType.setHeight("-1px");
		chkAllType.setVisible(false);
		mainLayout.addComponent(chkAllType, "top:15.0px;left:350.0px;");

		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:43.0px;left:450.0px;");

		lblSemiFgName = new Label();
		lblSemiFgName.setImmediate(false);
		lblSemiFgName.setWidth("-1px");
		lblSemiFgName.setHeight("-1px");
		lblSemiFgName.setValue("Product Name :");
		mainLayout.addComponent(lblSemiFgName, "top:41.0px;left:20.0px;");

		cmbSemiFgName= new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("300px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setNullSelectionAllowed(true);
		cmbSemiFgName.setNewItemsAllowed(false);
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbSemiFgName, "top:41.0px;left:140.0px;");

		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:67.0px;left:20.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:67.0px;left:140.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:91.0px;left:20.0px;");



		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:93.0px;left:140.0px;");

		lblReportType = new Label();
		lblReportType.setImmediate(false);
		lblReportType.setWidth("-1px");
		lblReportType.setHeight("-1px");
		lblReportType.setValue("Report Type: ");
		mainLayout.addComponent(lblReportType, "top:115.0px;left:20.0px;");

		opgSummary=new OptionGroup("",Optiontype);
		opgSummary.setImmediate(true);
		opgSummary.setValue("Summary");
		opgSummary.setStyleName("horizontal");
		mainLayout.addComponent(opgSummary, "top:117.0px;left:140.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:140.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:150.0px;left:25.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:175.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:175.opx; left:220.0px");

		//		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
