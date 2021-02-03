package com.example.productionReport;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.Transaction;





import acc.appform.accountsSetup.GroupCreate;
import acc.reportmodule.ledger.GeneralLedger;

import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
public class RptProductionAccountingValuetion extends Window{

	SessionBean sessionBean;
	private Label lblproductionType;
	private ComboBox cmbProductionDate=new ComboBox();
	private Label lblproductionDate;
	private ComboBox cmbproductionType=new ComboBox();

	private Label lblproductionStep;
	private ComboBox cmbprductionStep=new ComboBox();
	private ComboBox cmbVoucherNO=new ComboBox();


	private CheckBox chkAll=new CheckBox("All");
	private CheckBox chkAllVoucherNO=new CheckBox("All");
	//private CheckBox chkAllType=new CheckBox("All");
	//private Label lblAll=new Label();

	int type=0;
	/*private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");*/
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblProductionTypeDate;
	private Label lblLine;
	private Label lblReportType =new Label("Report Type :");
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;
	Button LinkMouldWiseProduction=new Button();
	Button LinkProductWiseProduction=new Button();
	Button Link=new Button();
	private NativeButton btnMouldLink,btnProductWiseProduction,btnDailyProduction,btnWipLedger,btnJobStandardInfo;
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private AbsoluteLayout mainLayout;

	public RptProductionAccountingValuetion(SessionBean sessionBean,String s){
		this.sessionBean=sessionBean;
		this.setCaption("Production Accounting Transaction (Journal)::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		productionDateLoad();
		this.addComponent(mainLayout);
		setEventAction();
	}
	private void productionDateLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct CONVERT(date,ProductionDate,105) dDate,REPLACE( CONVERT(varchar(120),ProductionDate,103),'/','-') "
					+"bangla from tbMouldProductionInfo order by CONVERT(date,ProductionDate,105)  desc ";
			List list=session.createSQLQuery(sql).list();
			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbProductionDate.addItem(element[0]);				
				cmbProductionDate.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void productionType()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct  productionType id ,(select productTypeName from tbProductionType "
					+"where productTypeId=productionType) productionTypeName from " +
					"tbMouldProductionInfo  where CONVERT(date,ProductionDate,105) like " +
					"'"+cmbProductionDate.getValue()+"' ";
			List list=session.createSQLQuery(sql).list();
			int i=0;
			cmbproductionType.removeAllItems();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbproductionType.addItem(element[0].toString());				
				cmbproductionType.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void productionStepLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct  Stepid,(select StepName  from tbProductionStep "
					+"where tbProductionStep.StepId=tbMouldProductionInfo.Stepid)stepName from tbMouldProductionInfo where productionType='"+cmbproductionType.getValue()+"' "
					+"and CONVERT(date,ProductionDate,105) like '"+cmbProductionDate.getValue()+"' ";

			List list=session.createSQLQuery(sql).list();

			int i=0;
			cmbprductionStep.removeAllItems();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbprductionStep.addItem(element[0].toString());				
				cmbprductionStep.setItemCaption(element[0].toString(), element[1].toString());
			}

		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbVoucherNoLoad(String id)
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct  0,voucherNo from tbMouldProductionInfo where " +
					"productionType like '"+cmbproductionType.getValue()+"' and Stepid like " +
					"'"+id+"' and  " +
					" CONVERT(date,ProductionDate,105) like '"+cmbProductionDate.getValue()+"'";
			List list=session.createSQLQuery(sql).list();

			int i=0;
			cmbVoucherNO.removeAllItems();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbVoucherNO.addItem(element[1].toString());				
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void setEventAction() {		

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbProductionDate.getValue()!=null )
				{
					if(cmbproductionType.getValue()!=null)
					{
						if(cmbprductionStep.getValue()!=null||chkAll.booleanValue()){
							if(cmbVoucherNO.getValue()!=null||chkAllVoucherNO.booleanValue()){
								reportView();
							}
						}
						else
						{
							showNotification("Please Select Voucher No",Notification.TYPE_WARNING_MESSAGE); 
						}
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


		cmbProductionDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductionDate.getValue()!=null)
				{
					productionType();

				}
			}
		});


		cmbproductionType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbproductionType.getValue()!=null && cmbProductionDate.getValue()!=null)
				{
					productionStepLoad(); 
				}
			}
		});
		cmbprductionStep.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbprductionStep.getValue()!=null && cmbProductionDate.getValue()!=null && cmbproductionType.getValue()!=null)
				{
					cmbVoucherNoLoad(cmbprductionStep.getValue().toString());
				}
			}
		});
		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});


		chkAll.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkAll.booleanValue()==true)
				{
					cmbprductionStep.setValue(null);
					cmbprductionStep.setEnabled(false);
					cmbVoucherNoLoad("%");
				}
				else
				{
					cmbprductionStep.setEnabled(true);
				}
			}
		});

		chkAllVoucherNO.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkAllVoucherNO.booleanValue()==true)
				{
					cmbVoucherNO.setValue(null);
					cmbVoucherNO.setEnabled(false);
				}
				else
				{
					cmbVoucherNO.setEnabled(true);
				}

			}
		});
		
		/*LinkMouldWiseProduction.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {	
			}
		});*/
		btnMouldLink.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//System.out.println(" Form");
				MouldReportOpen();			
			}
		});
		
		btnProductWiseProduction.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//System.out.println(" Form");
				ProductwiseProduction();			
			}
		});
		
		btnDailyProduction.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//System.out.println(" Form");
				DailyProduction();			
			}
		});
		
		btnWipLedger.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//System.out.println(" Form");
				WipLedger();			
			}
		});
		
		btnJobStandardInfo.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//System.out.println(" Form");
				jobStanderd();			
			}
		});
		
		

		/*chkAll.addListener(new ValueChangeListener() 
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
		});*/

		/*chkpdf.addListener(new ValueChangeListener()
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
		});*/
		/*	cmbType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbType.getValue()!=null){
					cmbSemiFgLoad();
				}
				else{
					cmbSemiFgName.removeAllItems();
				}
			}
		});*/

	}
	/*	private void cmbSemiFgLoad() {
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
			else if(type.equalsIgnoreCase("Assemble")){

			}
			else{
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
	}*/


	public void MouldReportOpen()
	{
		Window win = new MouldWiseProduction(sessionBean,"");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) {
				
			}
		});
		this.getParent().addWindow(win);
	}
	public void DailyProduction()
	{
		Window win = new RptMouldingDailyProduction(sessionBean,"");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) {
				
			}
		});
		this.getParent().addWindow(win);
	}
	public void ProductwiseProduction()
	{
		Window win = new RptSemiFgProductionDetailsDateBetween(sessionBean,"");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) {
				
			}
		});
		this.getParent().addWindow(win);
	}
	
	public void WipLedger()
	{
		Window win = new GeneralLedger(sessionBean,"g");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) {
				
			}
		});
		this.getParent().addWindow(win);
	}
	
	public void jobStanderd()
	{
		Window win = new RptStandardInfo(sessionBean,"");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) {
				
			}
		});
		this.getParent().addWindow(win);
	}
	
	private void reportView()
	{	
		String query=null;
		Transaction tx=null;
		String voucher="";
		String SemiFg = "";

		if(chkAll.booleanValue()){
			SemiFg="%";
		}
		else{
			SemiFg=cmbprductionStep.getValue().toString();
		}
		if(chkAllVoucherNO.booleanValue()){
			voucher="%";
		}
		else{
			voucher=cmbVoucherNO.getValue().toString();
		}
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
			//hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			/*String semiFg="%";
			if(cmbSemiFgName.getValue()!=null){
				semiFg=cmbSemiFgName.getValue().toString();
			}
			 */
			query="select productionNo,ProductionDate, " +
					"(select productTypeName from tbProductionType where productTypeId= " +
					"(select tbProductionStep.productionTypeId from tbProductionStep where " +
					"tbProductionStep.StepId=a.stepId  ))productionType, stepId, stepName," +
					"Voucher_No,semifgName,mouldNO,(select mouldName from tbmouldInfo " +
					"where mouldid=mouldNO)mouldName,totalSmifgPcs,totalfgPcs,rawItemName," +
					"standardQty,rate,productionQty,productionPcs,amount,fgAmount,semifgAmount," +
					"WIpCrAmount,semifgDrAmount,fgDrAmount,ItemType,MachineName,standardNO from " +
					"funProductionAccountingEvalution" +
					"('"+cmbProductionDate.getValue()+"', '"+cmbProductionDate.getValue()+"' ," +
					"'"+SemiFg+"','"+voucher+"') " +
					"a order by productionNo, ItemType";
			
			hm.put("sql", query);
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/productitionAccountingEvalution.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else{
				showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){
			showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}
	private AbsoluteLayout buildMainLayout() {

		// common part: create layout
		setWidth("530px");
		setHeight("350px");
		
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("500px");
		mainLayout.setHeight("260px");
		mainLayout.setMargin(false);

		// top-level component properties
		
		lblproductionDate = new Label();
		lblproductionDate.setImmediate(false);
		lblproductionDate.setWidth("-1px");
		lblproductionDate.setHeight("-1px");
		lblproductionDate.setValue("Production Date:");
		mainLayout.addComponent(lblproductionDate, "top:16.0px;left:20.0px;");		

		cmbProductionDate = new ComboBox();
		cmbProductionDate.setImmediate(true);
		cmbProductionDate.setWidth("120px");
		cmbProductionDate.setHeight("24px");
		cmbProductionDate.setNullSelectionAllowed(false);
		cmbProductionDate.setNewItemsAllowed(false);
		cmbProductionDate.setEnabled(true);
		cmbProductionDate.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		mainLayout.addComponent( cmbProductionDate, "top:15.0px;left:140.0px;");
		
		lblproductionType = new Label();
		lblproductionType.setImmediate(false);
		lblproductionType.setWidth("-1px");
		lblproductionType.setHeight("-1px");
		lblproductionType.setValue("Production Type:");
		mainLayout.addComponent(lblproductionType, "top:41.0px;left:20.0px;");

		cmbproductionType= new ComboBox();
		cmbproductionType.setImmediate(true);
		cmbproductionType.setWidth("200px");
		cmbproductionType.setHeight("24px");
		cmbproductionType.setNullSelectionAllowed(true);
		cmbproductionType.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbproductionType, "top:41.0px;left:140.0px;");
		cmbproductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


		lblproductionStep = new Label();
		lblproductionStep.setImmediate(false);
		lblproductionStep.setWidth("-1px");
		lblproductionStep.setHeight("-1px");
		lblproductionStep.setValue("Production Step:");
		mainLayout.addComponent(lblproductionStep, "top:66.0px;left:20.0px;");

		cmbprductionStep= new ComboBox();
		cmbprductionStep.setImmediate(true);
		cmbprductionStep.setWidth("200px");
		cmbprductionStep.setHeight("24px");
		cmbprductionStep.setNullSelectionAllowed(true);
		cmbprductionStep.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbprductionStep, "top:66.0px;left:140.0px;");
		cmbprductionStep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		cmbVoucherNO= new ComboBox();
		cmbVoucherNO.setImmediate(true);
		cmbVoucherNO.setWidth("200px");
		cmbVoucherNO.setHeight("24px");
		cmbVoucherNO.setNullSelectionAllowed(true);
		cmbVoucherNO.setNewItemsAllowed(false);
		cmbVoucherNO.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Voucher No"),"top:90px;left:20px;");
		mainLayout.addComponent( cmbVoucherNO, "top:91.0px;left:140.0px;");

		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:66.0px;left:350.0px;");

		chkAllVoucherNO.setImmediate(true);
		chkAllVoucherNO.setWidth("-1px");
		chkAllVoucherNO.setHeight("-1px");
		mainLayout.addComponent(chkAllVoucherNO, "top:91.0px;left:350.0px;");

		/*chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:140.0px; left:100.0px");*/

		/*LinkMouldWiseProduction.setCaption("Mould production");
		LinkMouldWiseProduction.setDescription("Open Mould production report");
		LinkMouldWiseProduction.setStyleName(BaseTheme.BUTTON_LINK);
		mainLayout.addComponent(LinkMouldWiseProduction, "top:170.0px;left:25.0px;");*/
		
		mainLayout.addComponent(new Label("Varify with column No :"),"top:130.0px;left:22px;");
		
		btnProductWiseProduction = new NativeButton();
		btnProductWiseProduction.setIcon(new ThemeResource("../icons/add.png"));
		btnProductWiseProduction.setImmediate(true);
		btnProductWiseProduction.setWidth("50px");
		btnProductWiseProduction.setHeight("24px");
		//mainLayout.addComponent(new Label("6"),"top:145px;left:10px;");
		mainLayout.addComponent(btnProductWiseProduction,"top:160px;left:25px;");
		btnProductWiseProduction.setStyleName(Button.STYLE_LINK);
		mainLayout.addComponent(new Label("FG Qty /"),"top:185.0px;left:32px;");
		mainLayout.addComponent(new Label("Sami FG Qty"),"top:198.0px;left:19px;");
		
		btnJobStandardInfo = new NativeButton();
		btnJobStandardInfo.setIcon(new ThemeResource("../icons/add.png"));
		btnJobStandardInfo.setImmediate(true);
		btnJobStandardInfo.setWidth("50px");
		btnJobStandardInfo.setHeight("24px");
		btnJobStandardInfo.setStyleName(Button.STYLE_LINK);
		mainLayout.addComponent(new Label("6,7,8"),"top:165px;left:92px;");
		mainLayout.addComponent(btnJobStandardInfo,"top:160.0px;left:120px;");
		mainLayout.addComponent(new Label("Job/Standard"),"top:185.0px;left:105px;");
		mainLayout.addComponent(new Label("Information"),"top:198.0px;left:113px;");
		
		btnDailyProduction = new NativeButton();
		btnDailyProduction.setIcon(new ThemeResource("../icons/add.png"));
		btnDailyProduction.setImmediate(true);
		btnDailyProduction.setWidth("50px");
		btnDailyProduction.setHeight("24px");
		btnDailyProduction.setStyleName(Button.STYLE_LINK);
		mainLayout.addComponent(new Label("10"),"top:165px;left:200px;");
		mainLayout.addComponent(btnDailyProduction,"top:160.0px;left:217px;");
		mainLayout.addComponent(new Label("Daily"),"top:185.0px;left:228px;");
		mainLayout.addComponent(new Label("Production"),"top:198.0px;left:212px;");
		
		btnMouldLink = new NativeButton();
		btnMouldLink.setIcon(new ThemeResource("../icons/add.png"));
		btnMouldLink.setImmediate(true);
		btnMouldLink.setWidth("50px");
		btnMouldLink.setHeight("24px");
		btnMouldLink.setStyleName(Button.STYLE_LINK);
		//mainLayout.addComponent(new Label("10"),"top:145px;left:300px;");
		mainLayout.addComponent(btnMouldLink,"top:160.0px;left:317px;");
		mainLayout.addComponent(new Label("Mould Wise"),"top:185.0px;left:311px;");
		mainLayout.addComponent(new Label("Production"),"top:198.0px;left:314px;");
		
		btnWipLedger = new NativeButton();
		btnWipLedger.setIcon(new ThemeResource("../icons/add.png"));
		btnWipLedger.setImmediate(true);
		btnWipLedger.setWidth("50px");
		btnWipLedger.setHeight("24px");
		btnProductWiseProduction.setStyleName(Button.STYLE_LINK);
		//mainLayout.addComponent(new Label("1"),"top:145px;left:400px;");
		mainLayout.addComponent(btnWipLedger,"top:160.0px;left:405px;");
		mainLayout.addComponent(new Label("WIP"),"top:185.0px;left:419px;");
		mainLayout.addComponent(new Label("Ledger"),"top:198.0px;left:412px;");
	
		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:210.0px;left:0px;");

		previewButton.setWidth("120px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		previewButton.setStyleName(Button.STYLE_LINK);
		mainLayout.addComponent(previewButton,"top:230.opx; left:108px");

		exitButton.setWidth("120px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		exitButton.setStyleName(Button.STYLE_LINK);
		mainLayout.addComponent(exitButton,"top:230.opx; left:250px");
		//		chkAll.setVisible(false);
		return mainLayout;
	}
}
