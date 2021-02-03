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
public class RptProductionSummary extends Window{

	SessionBean sessionBean;
	private Label lblFrom=new Label("From : ");
	private ComboBox cmbType=new ComboBox();
	private Label lblTo=new Label("To : ");
	private ComboBox cmbStep=new ComboBox();

	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();

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
	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;

	public RptProductionSummary(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("PRODUCTION SUMMARY::"+sessionBean.getCompany());
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

			//			int i=0;
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
				if(cmbType.getValue()!=null)
				{
					if(cmbType.getValue().toString().equalsIgnoreCase("PT-1")||cmbType.getValue().toString().equalsIgnoreCase("PT-2")){
						System.out.println("Done");
						reportViewMoulding(); 
					}
					else if(cmbType.getValue().toString().equalsIgnoreCase("Assemble")){
						reportViewAssemble();
					}
					else if(cmbType.getValue().toString().equalsIgnoreCase("Lacqure")){
						reportViewLacqure();
					}
					else if(cmbType.getValue().toString().equalsIgnoreCase("Stretch Blow Molding")){
						reportViewSBM();
					}
					else{
						reportViewOthers();
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
	
	private void reportViewOthers()
	{	

		Transaction tx=null;
		String step="";
		
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
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getValue().toString() );
			hm.put("productionStep","" );
			hm.put("user", sessionBean.getUserName());


			String subsql = "";
			
			
			subsql= "select b.semiFgCode,b.semiFgName,b.fgCode,b.fgName,SUM(shiftA)shiftAProduction,SUM(shiftB)shiftBProduction,  "
					+"SUM(rejectA)rejectAProduction,SUM(rejectB)rejectBProduction, "
					+"(select ISNULL(SUM(shiftA),0) + ISNULL(SUM(shiftB),0) from tbLabelingPrintingDailyProductionInfo c "
					+"inner join tbLabelingPrintingDailyProductionDetails d "
					+"on c.productionNo=d.productionNo "
					+"where CONVERT(date,c.productionDate,105) "
					+"between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"' and d.semiFgCode=b.semiFgCode and d.fgCode=b.fgCode "
					+"and c.isApproved='1' and c.productionStep like '"+cmbType.getValue()+"')ApprovedQty, "
					+"( "
					+"select ISNULL(SUM(shiftA),0) + ISNULL(SUM(shiftB),0) from tbLabelingPrintingDailyProductionInfo c "
					+"inner join tbLabelingPrintingDailyProductionDetails d "
					+"on c.productionNo=d.productionNo "
					+"where CONVERT(date,c.productionDate,105) "
					+"between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  and d.semiFgCode=b.semiFgCode and d.fgCode=b.fgCode "
					+"and c.isApproved='0' and c.productionStep like '"+cmbType.getValue()+"')nonApprovedQty from tbLabelingPrintingDailyProductionInfo a   "
					+"inner join tbLabelingPrintingDailyProductionDetails b " 
					+"on a.productionNo=b.productionNo where convert(date,a.productionDate,105) between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  and productionStep like '"+cmbType.getValue()+"' "
					+"group by b.semiFgCode,b.semiFgName,b.fgCode,b.fgName "
					+"order by b.semiFgName,b.fgName ";

			
			System.out.println("Sub Sql"+subsql);

		
			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(subsql).list();
			if(!list.isEmpty()){
				//Window win = new ReportViewerNew(hm,"report/production/rptDailyProduction.jasper",
				Window win = new ReportViewerNew(hm,"report/production/rptProductionOtherSummary.jasper",
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
	private void reportViewLacqure()
	{	

		Transaction tx=null;
		String step="";
		
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
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getValue().toString() );
			hm.put("productionStep","" );
			hm.put("user", sessionBean.getUserName());


			
			String  subsql=  "select fgCode, fgName,isnull(SUM(totalShift),0)totalShift,isnull(SUM(totalReject),0)totalReject, "
					         +"(select SUM(totalShift) from tbLacqureDailyProductionInfo a inner join tbLacqureDailyProductionDetails b "
							 +"on a.productionNo=b.productionNo where b.fgCode=fgCode "
							 +"and CONVERT(date,productionDate,105) "
							 +"between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  and a.productionStep='Lacqure' "
							 +"and a.isApproved='1')ApprovedQty, "
							 +"(select SUM(totalShift) from tbLacqureDailyProductionInfo a inner join tbLacqureDailyProductionDetails b "
							 +"on a.productionNo=b.productionNo where b.fgCode=fgCode "
							 +"and CONVERT(date,productionDate,105) "
							 +"between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  and a.productionStep='Lacqure' "
							 +"and a.isApproved='0')NonApprovedQty from tbLacqureDailyProductionInfo a " 
							 +"inner join tbLacqureDailyProductionDetails b on a.productionNo=b.productionNo   "
							 +"where CONVERT(date,a.ProductionDate,105) between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  and a.productionStep like 'Lacqure'  "
							 +"group by b.fgName,fgCode ";

			
			System.out.println("Sub Sql"+subsql);

			
			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(subsql).list();
			if(!list.isEmpty()){
				//Window win = new ReportViewerNew(hm,"report/production/rptDailyProduction.jasper",
				Window win = new ReportViewerNew(hm,"report/production/rptproductionLacqureSummary.jasper",
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
	private void reportViewSBM()
	{	

		Transaction tx=null;
		String step="";
		
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
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getValue().toString() );
			hm.put("productionStep","" );
			hm.put("user", sessionBean.getUserName());


			
			String  subsql= "select fgCode, fgName, isnull(sum(totalShift),0)totalShift,isnull(Sum(totalReject),0)totalReject, "
					+ " (select ISNULL(SUM(totalShift),0)totalShift from tbSBMDailyProductionInfo c inner join tbSBMDailyProductionDetails d on c.productionNo=d.productionNo where d.fgCode=b.fgCode "
					+ "and  CONVERT(date,productionDate,105) "
					+ "between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"'  "
					+ "and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  "
					+ "and c.productionStep='Stretch Blow Molding' and d.isApproved='1')ApprovedQty, "
					+ "(select ISNULL(SUM(totalShift),0)totalShift from tbSBMDailyProductionInfo c inner join tbSBMDailyProductionDetails d on c.productionNo=d.productionNo where d.fgCode=b.fgCode  "
					+ "and  CONVERT(date,productionDate,105) "
					+ "between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"'  "
					+ "and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  "
					+ " and c.productionStep='Stretch Blow Molding' and d.isApproved='0')NonApprovedQty "
					+ "from tbSBMDailyProductionInfo a inner join tbSBMDailyProductionDetails b on a.productionNo=b.productionNo "
					+ "where CONVERT(date,productionDate,105) "
					+ "between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"'  "
					+ "and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  "
					+ " and a.productionStep='Stretch Blow Molding' "
					+ "group by b.fgName,fgCode";
			
			
			System.out.println("Sub Sql"+subsql);

			
			hm.put("sql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(subsql).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptproductionSBMSummary.jasper",
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
	private void reportViewAssemble()
	{	
		
		Transaction tx=null;
		String step="";
		
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
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getValue().toString() );
			hm.put("productionStep","" );
			hm.put("user", sessionBean.getUserName());


			String subsql = "";
			subsql = "select semiFgId,semiFgName,isnull(SUM(assembleQty),0)assembleQty,isnull(SUM(rejectQty),0) rejectQty from tbIngradiantAssembleDetails "+
					" where CONVERT(date,assembleDate,105) between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  " +
					" group by semiFgId,semiFgName order by semiFgName";
			
			System.out.println("Sub Sql"+subsql);

			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(subsql).list();
			if(!list.isEmpty()){
				//Window win = new ReportViewerNew(hm,"report/production/rptDailyProduction.jasper",
				Window win = new ReportViewerNew(hm,"report/production/rptAssembleSummary.jasper",
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
	private void reportViewMoulding()
	{	
		String query=null;
		Transaction tx=null;
		String stepId="";
		if(cmbType.getValue().toString().equalsIgnoreCase("PT-1")){
			stepId="BlowSTP-1";
		}
		else if(cmbType.getValue().toString().equalsIgnoreCase("PT-2")){
			stepId="InjectionSTP-1";
		}

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
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			hm.put("productionStep","Moulding" );
			hm.put("user", sessionBean.getUserName());

			String subsql = "";
			
			subsql=    "select semiFgId,semiFgName,(isnull(SUM(ShiftAPcs),0)+isnull(SUM(ShiftBPcs),0))TotalProduction,(isnull(SUM(RejShiftA),0)+isnull(SUM(RejShiftB),0))TotalRejection, "
					   +"(select ISNULL(SUM(b.ShiftAPcs),0) + ISNULL(SUM(b.ShiftBPcs),0) as ApprovedQty from tbMouldProductionInfo a inner join tbMouldProductionDetails b "
					    +"on a.ProductionNo=b.ProductionNo where CONVERT(Date,ProductionDate,105) "
						+"between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"' and a.Stepid='"+stepId+"' "
						+"and a.productionType='"+cmbType.getValue().toString()+"'  and b.FinishedProduct=semiFgId "
						+"and b.isApproved='1')ApprovedQty,(select ISNULL(SUM(b.ShiftAPcs),0) + ISNULL(SUM(b.ShiftBPcs),0) as ApprovedQty from tbMouldProductionInfo a inner join tbMouldProductionDetails b "
						+"on a.ProductionNo=b.ProductionNo where CONVERT(Date,ProductionDate,105) "
						+"between '"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"' and a.Stepid='"+stepId+"' "
						+"and a.productionType='"+cmbType.getValue().toString()+"'  and b.FinishedProduct=semiFgId "
						+"and b.isApproved='0')nonApprovedQty "
						+"from funcRptDailyProductionSummary('"+cmbType.getValue().toString()+"','"+stepId+"','"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"','"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"') group by semiFgId,semiFgName  ";
			
			
			/*
			subsql = "select semiFgId,semiFgName,(isnull(SUM(ShiftAPcs),0)+isnull(SUM(ShiftBPcs),0))TotalProduction,(isnull(SUM(RejShiftA),0)+isnull(SUM(RejShiftB),0))TotalRejection "  
					+"from funcRptDailyProductionSummary('"+cmbType.getValue().toString()+"','"+stepId+"','"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"','"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"') group by semiFgId,semiFgName ";
			System.out.println("Sub Sql"+subsql);*/

			//hm.put("sql", query);
			hm.put("subsql", subsql);
			//hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(subsql).list();
			if(!list.isEmpty()){
				//Window win = new ReportViewerNew(hm,"report/production/rptDailyProduction.jasper",
				Window win = new ReportViewerNew(hm,"report/production/rptProductionMouldingSummary.jasper",
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
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		lblFrom = new Label();
		lblFrom.setImmediate(false);
		lblFrom.setWidth("-1px");
		lblFrom.setHeight("-1px");
		lblFrom.setValue("Production Type :");
		mainLayout.addComponent(lblFrom, "top:16.0px;left:40.0px;");

		cmbType = new ComboBox();
		cmbType.setImmediate(true);
		cmbType.setWidth("200px");
		cmbType.setHeight("24px");
		cmbType.setNullSelectionAllowed(false);
		cmbType.setNewItemsAllowed(false);
		//cmbType.setEnabled(false);
		mainLayout.addComponent( cmbType, "top:15.0px;left:140.0px;");

		lblTo = new Label();
		lblTo.setImmediate(false);
		lblTo.setWidth("-1px");
		lblTo.setHeight("-1px");
		lblTo.setValue("Production Step :");
		lblTo.setVisible(false);
		mainLayout.addComponent(lblTo, "top:41.0px;left:40.0px;");

		cmbStep= new ComboBox();
		cmbStep.setImmediate(true);
		cmbStep.setWidth("200px");
		cmbStep.setHeight("24px");
		cmbStep.setNullSelectionAllowed(true);
		cmbStep.setNewItemsAllowed(false);
		cmbStep.setVisible(false);
		mainLayout.addComponent( cmbStep, "top:41.0px;left:140.0px;");


		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:67.0px;left:40.0px;");

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
		mainLayout.addComponent(lblToDate, "top:91.0px;left:40.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:93.0px;left:140.0px;");

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
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:145.0px;left:25.0px;");

		previewButton.setWidth("90px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:171.opx; left:125.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:171.opx; left:220.0px");

		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}

