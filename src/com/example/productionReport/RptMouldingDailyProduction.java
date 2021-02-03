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
public class RptMouldingDailyProduction extends Window{

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

	public RptMouldingDailyProduction(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption(" DAILY PRODUCTION REPORT::"+sessionBean.getCompany());
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
		String query=null;
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
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getValue().toString() );
			hm.put("productionStep","" );
			hm.put("user", sessionBean.getUserName());


			/*query="select (select vMachineName from tbMachineInfo where vMachineCode like b.MachineName)machineName,c.vProductName,cycleTime,cavityNo, "+
					"ShiftAQty,ShiftAPcs,ShiftBQty,ShiftBPcs,TotalQty,TotalPcs,WastageQty as wastagePcs,WastagePcs as wastageQty,b.joborderNo from tbMouldProductionInfo a "+
					"inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo "+
					"inner join tbFinishedProductInfo c on b.FinishedProduct=c.vProductId "+
					"where CONVERT(date,a.ProductionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and a.Stepid like '"+cmbStep.getValue()+"'";*/

			query= "select a.productionDate,b.batchNo,b.machineName,b.semiFgCode,b.semiFgName,b.fgCode,b.fgName,SUM(shiftA)shiftAProduction,SUM(shiftB)shiftBProduction, "+
					" SUM(rejectA)rejectAProduction,SUM(rejectB)rejectBProduction from tbLabelingPrintingDailyProductionInfo a  "+
					" inner join tbLabelingPrintingDailyProductionDetails b  "+
					" on a.productionNo=b.productionNo where convert(date,a.productionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and productionStep like '"+cmbType.getValue()+"' "+
					" group by a.productionDate,b.batchNo,b.machineName,b.semiFgCode,b.semiFgName,b.fgCode,b.fgName "+
					" order by b.semiFgName,b.fgName";

			String subsql = "";
			subsql = "select b.semiFgCode,b.semiFgName,b.fgCode,b.fgName,SUM(shiftA)shiftAProduction,SUM(shiftB)shiftBProduction, "+
					" SUM(rejectA)rejectAProduction,SUM(rejectB)rejectBProduction from tbLabelingPrintingDailyProductionInfo a  "+
					" inner join tbLabelingPrintingDailyProductionDetails b  "+
					" on a.productionNo=b.productionNo where convert(date,a.productionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and productionStep like '"+cmbType.getValue()+"' "+
					" group by b.semiFgCode,b.semiFgName,b.fgCode,b.fgName "+
					" order by b.semiFgName,b.fgName";
			
			System.out.println("Sub Sql"+subsql);

			hm.put("sql", query);
			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				//Window win = new ReportViewerNew(hm,"report/production/rptDailyProduction.jasper",
				Window win = new ReportViewerNew(hm,"report/production/rptDailyProductionLabelingPrinting.jasper",
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
		String query=null;
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
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getValue().toString() );
			hm.put("productionStep","" );
			hm.put("user", sessionBean.getUserName());


			/*query="select (select vMachineName from tbMachineInfo where vMachineCode like b.MachineName)machineName,c.vProductName,cycleTime,cavityNo, "+
					"ShiftAQty,ShiftAPcs,ShiftBQty,ShiftBPcs,TotalQty,TotalPcs,WastageQty as wastagePcs,WastagePcs as wastageQty,b.joborderNo from tbMouldProductionInfo a "+
					"inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo "+
					"inner join tbFinishedProductInfo c on b.FinishedProduct=c.vProductId "+
					"where CONVERT(date,a.ProductionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and a.Stepid like '"+cmbStep.getValue()+"'";*/

			query= "select distinct b.batchNo,b.machineName,b.fgName,isnull(SUM(shiftA),0)shiftA,isnull(SUM(shiftB),0)shiftB,isnull(SUM(rejectA),0)rejectA, "+
					" isnull(SUM(rejectB),0)rejectB,isnull(SUM(totalShift),0)totalShift,isnull(SUM(totalReject),0)totalReject from tbLacqureDailyProductionInfo a "+
					" inner join tbLacqureDailyProductionDetails b on a.productionNo=b.productionNo  "+
					" where CONVERT(date,a.ProductionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and a.productionStep like 'Lacqure' "+
					" group by b.batchNo,b.machineName,b.fgName";

			String subsql = "select b.fgName,isnull(SUM(totalShift),0)totalShift,isnull(SUM(totalReject),0)totalReject from tbLacqureDailyProductionInfo a "+
					" inner join tbLacqureDailyProductionDetails b on a.productionNo=b.productionNo  "+
					" where CONVERT(date,a.ProductionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and a.productionStep like 'Lacqure' "+
					" group by b.fgName";
			/*subsql = "select semiFgId,semiFgName,isnull(SUM(assembleQty),0)consumptionQty from tbIngradiantAssembleDetails "+
					" where CONVERT(date,assembleDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"'" +
					" group by semiFgId,semiFgName order by semiFgName";*/
			
			System.out.println("Sub Sql"+subsql);

			hm.put("sql", query);
			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				//Window win = new ReportViewerNew(hm,"report/production/rptDailyProduction.jasper",
				Window win = new ReportViewerNew(hm,"report/production/rptDailyProductionLacqure.jasper",
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
		String query=null;
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
			hm.put("productionType",cmbType.getValue().toString() );
		
			hm.put("user", sessionBean.getUserName());

			query= " select b.batchNo,b.machineName,b.fgName,ISNULL(SUM(shiftA),0)shiftA,ISNULL(SUM(shiftB),0)shiftB,ISNULL(SUM(rejectA),0)rejectA, "
					+ "ISNULL(SUM(rejectB),0)rejectB,ISNULL(SUM(totalShift),0)totalShift,ISNULL(SUM(totalReject),0)totalRejec "
					+ "from tbSBMDailyProductionInfo a inner join tbSBMDailyProductionDetails b on a.productionNo=b.productionNo "
					+ "where CONVERT(date,a.productionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and a.productionStep like 'Stretch Blow Molding'  "
							+ "group by b.batchNo,b.machineName,b.fgName";
				

			String subsql = "select b.fgName,isnull(SUM(totalShift),0)totalShift,isnull(SUM(totalReject),0)totalReject "
					+ "from tbSBMDailyProductionInfo a inner join tbSBMDailyProductionDetails b on a.productionNo=b.productionNo "
					+ "where CONVERT(date,a.productionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and a.productionStep like 'Stretch Blow Molding'  "
							+ "group by b.fgName";
			
			System.out.println("Sub Sql"+subsql);

			hm.put("sql", query);
			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptDailyProductionSBM.jasper",
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
		String query=null;
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
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getValue().toString() );
			hm.put("productionStep","" );
			hm.put("user", sessionBean.getUserName());


			/*query="select (select vMachineName from tbMachineInfo where vMachineCode like b.MachineName)machineName,c.vProductName,cycleTime,cavityNo, "+
					"ShiftAQty,ShiftAPcs,ShiftBQty,ShiftBPcs,TotalQty,TotalPcs,WastageQty as wastagePcs,WastagePcs as wastageQty,b.joborderNo from tbMouldProductionInfo a "+
					"inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo "+
					"inner join tbFinishedProductInfo c on b.FinishedProduct=c.vProductId "+
					"where CONVERT(date,a.ProductionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and a.Stepid like '"+cmbStep.getValue()+"'";*/

			query= "select masterProductId,masterProductName,isnull(SUM(assembleQty),0)assembleQty, "+
					" semiFgId,semiFgName,isnull(SUM(rejectQty),0)consumptionQty from tbIngradiantAssembleDetails "+
					" where CONVERT(date,assembleDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' group by masterProductId,masterProductName,semiFgId,semiFgName "+
					" order by masterProductName,semiFgName";

			String subsql = "";
			subsql = "select semiFgId,semiFgName,isnull(SUM(assembleQty),0)+isnull(SUM(rejectQty),0) consumptionQty from tbIngradiantAssembleDetails "+
					" where CONVERT(date,assembleDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"'" +
					" group by semiFgId,semiFgName order by semiFgName";
			
			System.out.println("Sub Sql"+subsql);

			hm.put("sql", query);
			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				//Window win = new ReportViewerNew(hm,"report/production/rptDailyProduction.jasper",
				Window win = new ReportViewerNew(hm,"report/production/rptDailyProductionAssemble.jasper",
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
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			hm.put("productionStep","Moulding" );
			hm.put("user", sessionBean.getUserName());


			/*query="select (select vMachineName from tbMachineInfo where vMachineCode like b.MachineName)machineName,c.vProductName,cycleTime,cavityNo, "+
					"ShiftAQty,ShiftAPcs,ShiftBQty,ShiftBPcs,TotalQty,TotalPcs,WastageQty as wastagePcs,WastagePcs as wastageQty,b.joborderNo from tbMouldProductionInfo a "+
					"inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo "+
					"inner join tbFinishedProductInfo c on b.FinishedProduct=c.vProductId "+
					"where CONVERT(date,a.ProductionDate,105)='"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and a.Stepid like '"+cmbStep.getValue()+"'";*/

			query= "select * from funcRptDailyProduction('"+cmbType.getValue().toString()+"','"+stepId+"','"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"') ";

			String subsql = "";
			subsql = "select semiFgId,semiFgName,(isnull(SUM(ShiftAPcs),0)+isnull(SUM(ShiftBPcs),0))TotalProduction,(isnull(SUM(RejShiftA),0)+isnull(SUM(RejShiftB),0))TotalRejection "  
					+"from funcRptDailyProduction('"+cmbType.getValue().toString()+"','"+stepId+"','"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"') group by semiFgId,semiFgName ";
			System.out.println("Sub Sql"+subsql);

			hm.put("sql", query);
			hm.put("subsql", subsql);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				//Window win = new ReportViewerNew(hm,"report/production/rptDailyProduction.jasper",
				Window win = new ReportViewerNew(hm,"report/production/rptDailyProductioneditbyShehab.jasper",
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
		cmbType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
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
		cmbStep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbStep, "top:41.0px;left:140.0px;");


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

		/*		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:91.0px;left:62.0px;");

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
