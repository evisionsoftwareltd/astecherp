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
public class RptAssembleStock extends Window{

	SessionBean sessionBean;
	private Label lblSemiFgName;
	//private ComboBox cmbType=new ComboBox();
	//private Label lblProductionType;
	private ComboBox cmbSemiFgName=new ComboBox();

	private CheckBox chkAll=new CheckBox("All");
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
	
	private OptionGroup opgValueType;
	private static final List<String> OptionValueType=Arrays.asList(new String[]{"SUMMARY","DETAILS"});
	private OptionGroup opgValue;
	private static final List<String> OptionValue=Arrays.asList(new String[]{"INGRADIANT","MASTER PRODUCT"});


	private AbsoluteLayout mainLayout;

	public RptAssembleStock(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("ASSEMBLE STOCK ::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
		cmbSemiFgLoad();
		cmpIni(true);
	}
	private void cmpIni(boolean b){
		opgValue.setEnabled(!b);
		cmbSemiFgName.setEnabled(!b);
		chkAll.setEnabled(!b);
		formDate.setEnabled(!b);
		toDate.setEnabled(!b);
		chkother.setEnabled(!b);
		chkpdf.setEnabled(!b);
	}
	private void setEventAction() {		
		opgValueType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(opgValueType.getValue().toString().equalsIgnoreCase("SUMMARY")){
				
					cmpIni(false);
				}
				else if(opgValueType.getValue().toString().equalsIgnoreCase("DETAILS")){
					cmpIni(false);
					chkAll.setEnabled(false);
				}
			}
		});
		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbSemiFgName.getValue()!=null|| chkAll.booleanValue()==true)
				{
					if(opgValueType.getValue().toString().equalsIgnoreCase("SUMMARY")){
						reportView(); 
					}
					else if(opgValueType.getValue().toString().equalsIgnoreCase("DETAILS")){
						reportViewDetails();
					}
				}
				else
				{
					showNotification("Please Select Fg Name",Notification.TYPE_WARNING_MESSAGE); 
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
		opgValue.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				cmbSemiFgLoad();
			}
		});

	}
	private void cmbSemiFgLoad() {
		String sql="";
		if(opgValue.getValue().toString().equalsIgnoreCase("INGRADIANT")){
			sql="select distinct productId,productName from tbIssueToAssembleDetails order by productName";
		}
		else if(opgValue.getValue().toString().equalsIgnoreCase("MASTER PRODUCT"))
		{
			//sql="select distinct masterProductId,masterProductName from tbIngradiantAssembleDetails";
			sql="select distinct fgId,fgName from tbFinishedProductDetailsNew where consumptionStage='Assemble' ";
		}
		cmbSemiFgName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			//String sql="select distinct fgId,fgName from funcFgStock('%',getdate()) where FgStock>0";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbSemiFgName.addItem(element[0]);
				cmbSemiFgName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			//this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void reportView()
	{	
		String query=null,reportName="";
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
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("openyear",new SimpleDateFormat("yyyy").format(formDate.getValue()) );
			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("fromdate", new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()));
			
			if(opgValue.getValue().toString().equalsIgnoreCase("INGRADIANT")){
				query="select * from funAssembleStockDateBetween('"+SemiFg+"','"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"')";
				reportName="rptAssembleIngradiant.jasper";
			}
			else if(opgValue.getValue().toString().equalsIgnoreCase("MASTER PRODUCT")){
				query="select * from funAssembleStockMasterProductDateBetween('"+SemiFg+"','"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"')";
				reportName="rptAssembleMasterProduct.jasper";
			}
			
			hm.put("sql", query);
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/"+reportName,
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
	private void reportViewDetails()
	{	
		String query=null,reportName="";
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
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("openyear",new SimpleDateFormat("yyyy").format(formDate.getValue()) );
			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("fromdate", new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()));
			
			if(opgValue.getValue().toString().equalsIgnoreCase("INGRADIANT"))
			{
				query="select * from funcAssembleProductionDetails('"+SemiFg+"'," +
						"'"+cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue())+"'," +
								"'"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"')";
				reportName="rptAssembleIngradiantDetails.jasper";
			}
			
			else if(opgValue.getValue().toString().equalsIgnoreCase("MASTER PRODUCT"))
			{
				query="select * from funcAssembleMasterProductDetailStock('"+SemiFg+"',"
						+ "'"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"') "
								+ "order by vPartyName,masterProductName,ingradiantName ";
						
				reportName="RptMasterProductAssembleStockDetails.jasper";
			}
			
			hm.put("sql", query);
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/"+reportName,
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
		mainLayout.setWidth("520px");
		mainLayout.setHeight("250px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("550px");
		setHeight("330px");

		/*	lblProductionType = new Label();
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
		mainLayout.addComponent( cmbType, "top:15.0px;left:140.0px;");*/

		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:69.0px;left:450.0px;");

		lblSemiFgName = new Label();
		lblSemiFgName.setImmediate(false);
		lblSemiFgName.setWidth("-1px");
		lblSemiFgName.setHeight("-1px");
		lblSemiFgName.setValue("Product Name :");
		mainLayout.addComponent(lblSemiFgName, "top:67.0px;left:20.0px;");

		cmbSemiFgName= new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("300px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setNullSelectionAllowed(true);
		cmbSemiFgName.setNewItemsAllowed(false);
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbSemiFgName, "top:69.0px;left:140.0px;");

		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:95.0px;left:20.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:93.0px;left:140.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:125.0px;left:20.0px;");



		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:123.0px;left:140.0px;");

		lblReportType = new Label();
		lblReportType.setImmediate(false);
		lblReportType.setWidth("-1px");
		lblReportType.setHeight("-1px");
		lblReportType.setValue("Report For: ");
		mainLayout.addComponent(lblReportType, "top:41.0px;left:20.0px;");

		opgValue=new OptionGroup("",OptionValue);
		opgValue.setImmediate(true);
		opgValue.setValue("INGRADIANT");
		opgValue.setStyleName("horizontal");
		mainLayout.addComponent(opgValue, "top:43.0px;left:140.0px;");
		
		opgValueType=new OptionGroup("",OptionValueType);
		opgValueType.setImmediate(true);
		//opgValueType.setValue("SUMMARY");
		opgValueType.setStyleName("horizontal");
		mainLayout.addComponent(new Label("Report Type: "), "top:16.0px;left:20.0px;");
		mainLayout.addComponent(opgValueType, "top:15.0px;left:140.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:150.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:165.0px;left:25.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:190.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:190.opx; left:220.0px");

		//		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
