package com.example.productionReport;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.xerces.impl.dtd.models.CMBinOp;
import org.hibernate.Session;
import org.hibernate.Transaction;




import com.common.share.ExampleUtil;
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

public class MachineWiseProduction extends Window{

	SessionBean sessionBean;
	private Label lblMachineName=new Label("Machine Name : ");
	private ComboBox cmbmachineName=new ComboBox();
	private Label lblTo=new Label("To : ");
	private ComboBox cmbStep=new ComboBox();
	private Label lblReportType =new Label("Report Type :");
	CheckBox chkPrinting=new CheckBox("Printing");
	CheckBox chkTubing=new CheckBox("Tubing");
	CheckBox chkShouldering=new CheckBox("Shouldering");
	CheckBox chkSealing=new CheckBox("Sealing");
	private CheckBox chkAll=new CheckBox("All");
	private Label lblAll=new Label();
	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();
	private Label lblFDate;
	private Label lblToDate;
	private Label lblLine;
	private PopupDateField dFromDate=new PopupDateField();
	private PopupDateField dToDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;
	private OptionGroup opgSummary;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Summary","Details"});

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;

	public MachineWiseProduction(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("MACHINE WISE PRODUCTION::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		machineDataLoad();
		setEventAction();
	}
	private void machineDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct  b.vMachineCode,b.vMachineName from tbMouldProductionDetails a   "+
					" inner join tbMachineInfo b on a.MachineName=b.vMachineCode   "+
					" union  "+
					" select distinct   b.vMachineCode,b.vMachineName from tbLabelingPrintingDailyProductionDetails a    "+
					" inner join tbMachineInfo b on a.machineCode=b.vMachineCode   "+
					" union  "+
					" select distinct   b.vMachineCode,b.vMachineName from tbLacqureDailyProductionDetails a    "+
					" inner join tbMachineInfo b on a.machineCode=b.vMachineCode ";



			List list=session.createSQLQuery(sql).list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbmachineName.addItem(element[0]);
				cmbmachineName.setItemCaption(element[0], element[1].toString());
			}

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

				if(cmbmachineName.getValue()!=null ||  chkAll.booleanValue())
				{
					reportView(); 	
				}
				else
				{
					showNotification("Please Select Machine Name",Notification.TYPE_WARNING_MESSAGE);	
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
				if(bv==true)
				{
					cmbmachineName.setEnabled(false);
					cmbmachineName.setValue(null);

				}
				else
				{
					cmbmachineName.setEnabled(true);
					cmbmachineName.focus();
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


	private void reportView()
	{	
		String query=null;
		String subquery=null;
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
			hm.put("fromdate",new SimpleDateFormat("dd-MM-yyyy").format(dFromDate.getValue()) );
			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(dToDate.getValue()));
			//hm.put("SUBREPORT_DIR", "./report/production/");

			String machine="%";

			if(cmbmachineName.getValue()!=null)
			{
				machine=cmbmachineName.getValue().toString();  	
			}
			System.out.println(opgSummary.getValue().toString());
			if(opgSummary.getValue().toString().equalsIgnoreCase("Summary"))
			{
				/*query="select MachineName machineId,(select vMachineName from tbMachineInfo where " +
						"vMachineCode=MachineName)MachineName,FinishedProduct,(select semiFgName from tbSemiFgInfo " +
						"where semiFgCode=FinishedProduct)semiFgName,(select unit from tbSemiFgInfo where " +
						"semiFgCode=FinishedProduct)unit,(select color from tbSemiFgInfo where semiFgCode=FinishedProduct)" +
						"color,(select stdWeight from tbSemiFgInfo where semiFgCode=FinishedProduct)stdWeight," +
						"(SUM(ShiftAPcs)+SUM(ShiftBPcs))GoodQty,(SUM(RejShiftA)+SUM(RejShiftB))RejectQty," +
						"((SUM(ShiftAPcs)+SUM(ShiftBPcs))+ (SUM(RejShiftA)+SUM(RejShiftB))) Total from " +
						"tbMouldProductionDetails a inner join tbMouldProductionInfo b on a.ProductionNo=b.ProductionNo " +
						"where MachineName like '"+machine+"' and convert(date,b.ProductionDate,105) between " +
						"'"+datef.format(dFromDate.getValue())+"' and '"+datef.format(dToDate.getValue())+"' group by MachineName," +
						"FinishedProduct order by MachineName,semiFgName";*/
				
				query="select MachineName machineId,(select vMachineName from tbMachineInfo where vMachineCode=MachineName)MachineName, " +
					  " FinishedProduct,(select semiFgName from tbSemiFgInfo where semiFgCode=FinishedProduct)semiFgName, " +
					  " (select unit from tbSemiFgInfo where semiFgCode=FinishedProduct)unit, " +
					  " (select color from tbSemiFgInfo where semiFgCode=FinishedProduct)color, " +
					  " (select stdWeight from tbSemiFgInfo where semiFgCode=FinishedProduct)stdWeight, " +
					  " (SUM(ShiftAPcs)+SUM(ShiftBPcs))GoodQty,(SUM(RejShiftA)+SUM(RejShiftB))RejectQty, " +
					  " ((SUM(ShiftAPcs)+SUM(ShiftBPcs))+ (SUM(RejShiftA)+SUM(RejShiftB))) Total from tbMouldProductionDetails a  " +
					  " inner join tbMouldProductionInfo b on a.ProductionNo=b.ProductionNo where MachineName like '"+machine+"' and  " +
					  " convert(date,b.ProductionDate,105) between '"+datef.format(dFromDate.getValue())+"' and '"+datef.format(dToDate.getValue())+"'  " +
					  " group by MachineName,FinishedProduct  " +
					  " union " +
					  " select machineCode,(select vMachineName from tbMachineInfo where vMachineCode=machineCode)MachineName, " +
					  " fgCode,(select semiFgSubName from tbSemiFgSubInformation where semiFgSubId=fgCode)semiFgName, " +
					  " '' unit,(select color from tbSemiFgSubInformation where semiFgSubId=fgCode)color,'0' stdWeight, " +
					  " (SUM(ShiftA)+SUM(ShiftB))GoodQty,(SUM(rejectA)+SUM(rejectB))RejectQty, " +
					  " ((SUM(ShiftA)+SUM(ShiftB))+ (SUM(rejectA)+SUM(rejectB))) Total from tbLabelingPrintingDailyProductionDetails a  " +
					  " inner join tbLabelingPrintingDailyProductionInfo b on a.ProductionNo=b.ProductionNo where machineCode like '"+machine+"' and  " +
					  " convert(date,b.ProductionDate,105) between '"+datef.format(dFromDate.getValue())+"' and '"+datef.format(dToDate.getValue())+"'  " +
					  " group by machineCode,fgCode  " +
					  " union " +
					  " select machineCode,(select vMachineName from tbMachineInfo where vMachineCode=machineCode)MachineName, " +
					  " fgCode,(select semiFgSubName from tbSemiFgSubInformation where semiFgSubId=fgCode)semiFgName, " +
					  " '' unit,(select color from tbSemiFgSubInformation where semiFgSubId=fgCode)color,'0' stdWeight, " +
					  " (SUM(ShiftA)+SUM(ShiftB))GoodQty,(SUM(rejectA)+SUM(rejectB))RejectQty, " +
					  " ((SUM(ShiftA)+SUM(ShiftB))+ (SUM(rejectA)+SUM(rejectB))) Total from tbLacqureDailyProductionDetails a  " +
					  " inner join tbLacqureDailyProductionInfo b on a.ProductionNo=b.ProductionNo where machineCode like '"+machine+"' and  " +
					  " convert(date,b.ProductionDate,105) between '"+datef.format(dFromDate.getValue())+"' and '"+datef.format(dToDate.getValue())+"'  " +
					  " group by machineCode,fgCode order by MachineName,semiFgName";
				
				
				hm.put("sql", query);
				List list=session.createSQLQuery(query).list();
				if(!list.isEmpty()){
					Window win = new ReportViewerNew(hm,"report/production/MachinewiseproductionReport .jasper",
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
			else if(opgSummary.getValue().toString().equalsIgnoreCase("Details"))//Details
			{
				/*query=" select convert(date,b.ProductionDate,105)ProductionDate,MachineName machineId," +
						" (select vMachineName from tbMachineInfo where vMachineCode=MachineName)MachineName," +
						" FinishedProduct,(select semiFgName from tbSemiFgInfo where semiFgCode=FinishedProduct)semiFgName," +
						" (select unit from tbSemiFgInfo where semiFgCode=FinishedProduct)unit," +
						" (select color from tbSemiFgInfo where semiFgCode=FinishedProduct)color," +
						" (select stdWeight from tbSemiFgInfo where semiFgCode=FinishedProduct)stdWeight," +
						" (SUM(ShiftAPcs)+SUM(ShiftBPcs))GoodQty,(SUM(RejShiftA)+SUM(RejShiftB))RejectQty," +
						" ((SUM(ShiftAPcs)+SUM(ShiftBPcs))+ (SUM(RejShiftA)+SUM(RejShiftB))) Total " +
						" from tbMouldProductionDetails a inner join tbMouldProductionInfo b on " +
						" a.ProductionNo=b.ProductionNo where MachineName like '"+machine+"' and convert(date,b.ProductionDate,105) " +
						" between '"+datef.format(dFromDate.getValue())+"' and '"+datef.format(dToDate.getValue())+"' group by b.ProductionDate,MachineName,FinishedProduct" +
						" order by convert(date,b.ProductionDate,105),MachineName,FinishedProduct";*/
				
				query="select convert(date,b.ProductionDate,105)ProductionDate,MachineName machineId, "+
					  " (select vMachineName from tbMachineInfo where vMachineCode=MachineName)MachineName, "+
					  " FinishedProduct,(select semiFgName from tbSemiFgInfo where semiFgCode=FinishedProduct)semiFgName, "+
					  " (select unit from tbSemiFgInfo where semiFgCode=FinishedProduct)unit, "+
					  " (select color from tbSemiFgInfo where semiFgCode=FinishedProduct)color, "+
					  " (select stdWeight from tbSemiFgInfo where semiFgCode=FinishedProduct)stdWeight, "+
					  " (SUM(ShiftAPcs)+SUM(ShiftBPcs))GoodQty,(SUM(RejShiftA)+SUM(RejShiftB))RejectQty, "+
					  " ((SUM(ShiftAPcs)+SUM(ShiftBPcs))+ (SUM(RejShiftA)+SUM(RejShiftB))) Total from tbMouldProductionDetails a  "+
					  " inner join tbMouldProductionInfo b on a.ProductionNo=b.ProductionNo where MachineName like '"+machine+"' and  "+
					  " convert(date,b.ProductionDate,105) between '"+datef.format(dFromDate.getValue())+"' and '"+datef.format(dToDate.getValue())+"'  "+
					  " group by b.ProductionDate,MachineName,FinishedProduct  "+
					  " union "+
					  " select convert(date,b.ProductionDate,105)ProductionDate,machineCode, "+
					  " (select vMachineName from tbMachineInfo where vMachineCode=machineCode)MachineName, "+
					  " fgCode,(select semiFgSubName from tbSemiFgSubInformation where semiFgSubId=fgCode)semiFgName, "+
					  " '' unit,(select color from tbSemiFgSubInformation where semiFgSubId=fgCode)color,'0' stdWeight, "+
					  " (SUM(ShiftA)+SUM(ShiftB))GoodQty,(SUM(rejectA)+SUM(rejectB))RejectQty, "+
					  " ((SUM(ShiftA)+SUM(ShiftB))+ (SUM(rejectA)+SUM(rejectB))) Total from tbLabelingPrintingDailyProductionDetails a  "+
					  " inner join tbLabelingPrintingDailyProductionInfo b on a.ProductionNo=b.ProductionNo where machineCode like '"+machine+"' and  "+
					  " convert(date,b.ProductionDate,105) between '"+datef.format(dFromDate.getValue())+"' and '"+datef.format(dToDate.getValue())+"'  "+
					  " group by b.ProductionDate,machineCode,fgCode  "+
					  " union "+
					  " select convert(date,b.ProductionDate,105)ProductionDate,machineCode, "+
					  " (select vMachineName from tbMachineInfo where vMachineCode=machineCode)MachineName, "+
					  " fgCode,(select semiFgSubName from tbSemiFgSubInformation where semiFgSubId=fgCode)semiFgName, "+
					  " '' unit,(select color from tbSemiFgSubInformation where semiFgSubId=fgCode)color,'0' stdWeight, "+
					  " (SUM(ShiftA)+SUM(ShiftB))GoodQty,(SUM(rejectA)+SUM(rejectB))RejectQty, "+
					  " ((SUM(ShiftA)+SUM(ShiftB))+ (SUM(rejectA)+SUM(rejectB))) Total from tbLacqureDailyProductionDetails a  "+
					  " inner join tbLacqureDailyProductionInfo b on a.ProductionNo=b.ProductionNo where machineCode like '"+machine+"' and  "+
					  " convert(date,b.ProductionDate,105) between '"+datef.format(dFromDate.getValue())+"' and '"+datef.format(dToDate.getValue())+"'  "+
					  " group by b.ProductionDate,machineCode,fgCode order by  convert(date,b.ProductionDate,105),MachineName,semiFgName";
				
				hm.put("sql", query);
				List list=session.createSQLQuery(query).list();
				if(!list.isEmpty()){
					Window win = new ReportViewerNew(hm,"report/production/MachinewiseproductionDetailsReport .jasper",
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
	private AbsoluteLayout buildMainLayout() 
	{

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("460px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		lblMachineName = new Label();
		lblMachineName.setImmediate(false);
		lblMachineName.setWidth("-1px");
		lblMachineName.setHeight("-1px");
		lblMachineName.setValue("Machine Name :");
		mainLayout.addComponent(lblMachineName, "top:16.0px;left:40.0px;");
		
		ComboBox l = new ComboBox("Please select your country",
                ExampleUtil.getISO3166Container());

        // Sets the combobox to show a certain property as the item caption
        l.setItemCaptionPropertyId(ExampleUtil.iso3166_PROPERTY_NAME);
        l.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);


		cmbmachineName = new ComboBox();
	/*	cmbmachineName.setItemCaptionPropertyId(ExampleUtil.iso3166_PROPERTY_NAME);
		cmbmachineName.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		cmbmachineName.setItemIconPropertyId(ExampleUtil.iso3166_PROPERTY_FLAG);*/
		cmbmachineName.setImmediate(true);
		cmbmachineName.setWidth("200px");
		cmbmachineName.setHeight("24px");
		cmbmachineName.setNullSelectionAllowed(false);
		cmbmachineName.setNewItemsAllowed(false);
		//cmbType.setEnabled(false);
		mainLayout.addComponent( cmbmachineName, "top:15.0px;left:140.0px;");

		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:41.0px;left:40.0px;");

		dFromDate.setImmediate(true);
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setValue(new java.util.Date());
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setWidth("107px");
		dFromDate.setHeight("-1px");
		dFromDate.setInvalidAllowed(false);
		mainLayout.addComponent( dFromDate, "top:41.0px;left:140.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:67.0px;left:40.0px;");

		dToDate.setImmediate(true);
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setValue(new java.util.Date());
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setWidth("107px");
		dToDate.setHeight("-1px");
		dToDate.setInvalidAllowed(false);
		mainLayout.addComponent( dToDate, "top:67.0px;left:140.0px;");

		lblReportType = new Label();
		lblReportType.setImmediate(false);
		lblReportType.setWidth("-1px");
		lblReportType.setHeight("-1px");
		lblReportType.setValue("Report Type: ");
		mainLayout.addComponent(lblReportType, "top:90.0px;left:40.0px;");


		opgSummary=new OptionGroup("",Optiontype);
		opgSummary.setImmediate(true);
		opgSummary.setValue("Summary");
		opgSummary.setStyleName("horizontal");
		mainLayout.addComponent(opgSummary, "top:90.0px;left:130.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:110.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:145.0px;left:25.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:171.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:171.opx; left:220.0px");

		chkAll.setImmediate(true);
		chkAll.setVisible(true);
		mainLayout.addComponent( chkAll, "top:15.0px;left:350.0px;");
		lblAll.setVisible(false);
		return mainLayout;


	}
}

