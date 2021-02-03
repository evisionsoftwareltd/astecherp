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
public class RptProductWiseProduction extends Window{

	SessionBean sessionBean;
	private Label lblProductType=new Label("ProductType : ");
	private Label lblReportType=new Label("Report Type : ");

	private Label lblSemiFG=new Label("Semi FG : ");
	private ComboBox cmbSemiFG=new ComboBox();
	private Label lblTo=new Label("To : ");
	private ComboBox cmbStep=new ComboBox();

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

	private OptionGroup opgProductType;
	private static final List<String> ProductType=Arrays.asList(new String[]{"Semi FG","FG"});

	private Label lblFDate;
	private Label lblToDate;
	private Label lblLine;
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;
	private OptionGroup opgSummary;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Summary","Details"});

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;

	public RptProductWiseProduction(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("PRODUCT WISE PRODUCTION::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		mouldDataLoad();
		setEventAction();
	}
	private void mouldDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct  mouldNo,b.mouldName from tbMouldProductionDetails a "
					+"inner join tbmouldInfo b on a.mouldNo=b.mouldid";


			List list=session.createSQLQuery(sql).list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbSemiFG.addItem(element[0]);
				cmbSemiFG.setItemCaption(element[0], element[1].toString());
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

				if(cmbSemiFG.getValue()!=null || chkAll.booleanValue())
				{
					reportView(); 	
				}
				else
				{
					showNotification("Please Select Desire Mould Name",Notification.TYPE_WARNING_MESSAGE);	
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
					cmbSemiFG.setEnabled(false);
					cmbSemiFG.setValue(null);

				}
				else
				{
					cmbSemiFG.setEnabled(true);
					cmbSemiFG.focus();
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

		opgProductType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgProductType.getValue().toString().equals("FG"))
				{
					lblSemiFG.setValue("FG :");
				}
				else
				{
					lblSemiFG.setValue("Semi FG :");
				}
			}
		});



	}
	/*private void productionStepDataLoad() {

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select StepId,StepName from tbProductionStep where productionTypeId like '"+cmbType.getValue()+"'";
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
			hm.put("fromdate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));

			hm.put("SUBREPORT_DIR", "./report/production/");

			String mould="";

			if(cmbSemiFG.getValue()!=null)

			{
				mould=cmbSemiFG.getValue().toString();

			}
			else
			{
				mould="%";	
			}


			query=  "select a.productionType,d.productTypeName,c.StepId,c.StepName,b.mouldNo,e.mouldName,b.ShiftAPcs,b.ShiftBPcs,(b.ShiftAPcs+b.ShiftBPcs) as total, " 
					+"b.FinishedProduct,f.semiFgName,g.vMachineCode,g.vMachineName,RejShiftA,RejShiftB,RejShiftA+RejShiftB as totalreject,ProductionDate,f.unit from tbMouldProductionInfo a "
					+"inner join tbMouldProductionDetails b "
					+"on a.ProductionNo=b.ProductionNo "
					+"inner join tbProductionStep c on c.StepId=a.Stepid "
					+"inner join tbProductionType d on d.productTypeId=a.productionType "
					+"inner join tbmouldInfo e on e.mouldid=b.mouldNo "
					+"inner join tbSemiFgInfo f on f.semiFgCode=b.FinishedProduct "
					+"inner join tbMachineInfo g on g.vMachineCode=b.MachineName "
					+"where b.mouldNo like '"+mould+"' and CONVERT (date,a.ProductionDate,105) between '"+ new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+ new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"' "
					+"order by a.productionType, b.mouldNo,c.StepId,g.vMachineCode,b.FinishedProduct ";

			hm.put("sql", query);

			subquery="select b.mouldNo,c.mouldName,b.FinishedProduct,d.semiFgName, ISNULL(SUM(ShiftAPcs),0)+ISNULL(SUM(ShiftBPcs),0)totalproduction,ISNULL(SUM(RejShiftA),0)+ISNULL(SUM(RejShiftB),0)totalreject,d.unit  from tbMouldProductionInfo a "
					+"inner join "
					+"tbMouldProductionDetails b " 
					+"on a.ProductionNo=b.ProductionNo "
					+"inner join tbmouldInfo c on c.mouldid=b.mouldNo "
					+"inner join "
					+"tbSemiFgInfo d on d.semiFgCode=b.FinishedProduct  where b.mouldNo like '"+mould+"' and CONVERT(date,a.ProductionDate,105) between '"+ new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+ new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"' "
					+"group by b.mouldNo,c.mouldName,b.FinishedProduct,d.semiFgName,d.unit  order by b.mouldNo,b.FinishedProduct ";

			System.out.println(subquery);



			hm.put("subsql", subquery);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptmouldWiseproduction.jasper",
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

		lblProductType = new Label();
		lblProductType.setImmediate(false);
		lblProductType.setWidth("-1px");
		lblProductType.setHeight("-1px");
		lblProductType.setValue("ProductType :");
		mainLayout.addComponent(lblProductType, "top:17.0px;left:40.0px;");

		opgProductType=new OptionGroup("",ProductType);
		opgProductType.setImmediate(true);
		opgProductType.setValue("Semi FG");
		opgProductType.setStyleName("horizontal");
		mainLayout.addComponent(opgProductType, "top:16.0px;left:140.0px;");

		lblSemiFG = new Label();
		lblSemiFG.setImmediate(false);
		lblSemiFG.setWidth("-1px");
		lblSemiFG.setHeight("-1px");
		lblSemiFG.setValue("Semi FG:");
		mainLayout.addComponent(lblSemiFG, "top:40.0px;left:40.0px;");

		cmbSemiFG = new ComboBox();
		cmbSemiFG.setImmediate(true);
		cmbSemiFG.setWidth("200px");
		cmbSemiFG.setHeight("24px");
		cmbSemiFG.setNullSelectionAllowed(false);
		cmbSemiFG.setNewItemsAllowed(false);
		//cmbType.setEnabled(false);
		mainLayout.addComponent( cmbSemiFG, "top:38.0px;left:140.0px;");

		/*lblTo = new Label();
		lblTo.setImmediate(false);
		lblTo.setWidth("-1px");
		lblTo.setHeight("-1px");
		lblTo.setValue("Production Step :");
		mainLayout.addComponent(lblTo, "top:41.0px;left:40.0px;");*/

		/*HorizontalLayout hLayout=new HorizontalLayout();
		hLayout.setSpacing(true);
		hLayout.addComponent(chkPrinting);
		chkPrinting.setValue(true);
		chkPrinting.setImmediate(true);
		hLayout.addComponent(chkTubing);
		chkTubing.setValue(true);
		chkTubing.setImmediate(true);
		hLayout.addComponent(chkShouldering);
		chkShouldering.setValue(true);
		chkShouldering.setImmediate(true);
		hLayout.addComponent(chkSealing);
		chkSealing.setValue(true);
		chkSealing.setImmediate(true);

		mainLayout.addComponent( hLayout, "top:41.0px;left:140.0px;");*/


		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:65.0px;left:40.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:63.0px;left:140.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:90.0px;left:40.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:88.0px;left:140.0px;");

		lblReportType = new Label();
		lblReportType.setImmediate(false);
		lblReportType.setWidth("-1px");
		lblReportType.setHeight("-1px");
		lblReportType.setValue("Report Type: ");
		mainLayout.addComponent(lblReportType, "top:112.0px;left:40.0px;");
		
		
		opgSummary=new OptionGroup("",Optiontype);
		opgSummary.setImmediate(true);
		opgSummary.setValue("Summary");
		opgSummary.setStyleName("horizontal");
		mainLayout.addComponent(opgSummary, "top:113.0px;left:130.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:132.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:149.0px;left:25.0px;");

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
		mainLayout.addComponent( chkAll, "top:38.0px;left:350.0px;");
		lblAll.setVisible(false);
		return mainLayout;


	}
}

