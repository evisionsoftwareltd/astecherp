package com.example.productionReport;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.xerces.impl.dtd.models.CMBinOp;
import org.hibernate.Session;
import org.hibernate.Transaction;





import com.common.share.ReportPdf;
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
public class RptBatchWiseStock extends Window{

	SessionBean sessionBean;
	private Label lblProductionType=new Label("Type : ");
	private ComboBox cmbProductionType=new ComboBox();
	private Label lblBatchNo=new Label("Step : ");
	private ComboBox cmbBatchNo=new ComboBox();
	private Label lblFgName=new Label("Supplier : ");
	private ComboBox cmbFgName=new ComboBox();


	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();

	private CheckBox chkAllType = new CheckBox();
	private CheckBox chkAllBatchNo = new CheckBox();
	private CheckBox chkAllFgName = new CheckBox();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate;
	private Label lblLine;
	private PopupDateField formDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");

	private OptionGroup opgSummary;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Summary","Details"});
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;
	private Label lblReportType =new Label("Report Type :");

	public RptBatchWiseStock(SessionBean sessionBean){

		this.sessionBean=sessionBean;
		this.setCaption("BATCH WISE STOCK::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
	}


	private void setEventAction() {

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbProductionType.getValue()!=null)
				{
					if(cmbBatchNo.getValue()!=null|| chkAllBatchNo.booleanValue())
					{
						if(cmbFgName.getValue()!=null || chkAllFgName.booleanValue())
						{
							 
							if(opgSummary.getValue().toString().equalsIgnoreCase("Summary")){
								reportView();
							}
							else if(opgSummary.getValue().toString().equalsIgnoreCase("Details")){
								reportViewDeatails();
							}

						}
						else
						{
							showNotification("Please Provide FG Name",Notification.TYPE_WARNING_MESSAGE); 	
						}

					}
					else
					{
						showNotification("Please Provide Batch NO",Notification.TYPE_WARNING_MESSAGE); 
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
		chkAllBatchNo.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {

				if(chkAllBatchNo.booleanValue()){
					cmbBatchNo.setEnabled(false);
					cmbBatchNo.setValue(null);
					fgNameDataLoad();
				}
				else{
					cmbBatchNo.setEnabled(true);
					cmbBatchNo.focus();
				}
			}
		});
		chkAllFgName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {

				if(chkAllFgName.booleanValue()){
					cmbFgName.setEnabled(false);
					cmbFgName.setValue(null);

				}
				else{
					cmbFgName.setEnabled(true);
					cmbFgName.focus();
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


		cmbProductionType.addListener(new ValueChangeListener() {


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbProductionType.getValue()!=null)
				{

					BatchNoDataLoad(cmbProductionType.getValue().toString());
				}
				else{
					cmbBatchNo.removeAllItems();
				}

			}
		});


		cmbBatchNo.addListener(new ValueChangeListener() 
		{


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbBatchNo.getValue()!=null && cmbProductionType.getValue()!=null)
				{

					fgNameDataLoad();
				}

			}
		});


	}
	private void BatchNoDataLoad(String type) 
	{
		
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql= "select 0,batchNo from tbIssueToLabelPrintingInfo where issueTo like '"+type+"'";
			if(type.equalsIgnoreCase("Lacqure")){
				sql= "select 0,batchNo from tbIssueToLacqureInfo where issueTo like '"+type+"'";
			}
			/*else if(type.equalsIgnoreCase("Assemble")){
				sql= "select 0,batchNo from tbIssueToAssembleInfo where issueTo like '"+type+"'";
			}*/
			List list=session.createSQLQuery(sql).list();
			cmbBatchNo.removeAllItems();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbBatchNo.addItem(element[1]);
				cmbBatchNo.setItemCaption(element[1], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void fgNameDataLoad() 
	{
		cmbFgName.removeAllItems();
		String batchNo="%";
		if(cmbBatchNo.getValue()!=null){
			batchNo=cmbBatchNo.getValue().toString();
		}
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct b.productId,b.productName from tbIssueToLabelPrintingInfo a  "+
					" inner join tbIssueToLabelPrintingDetails b  "+
					" on a.issueNo=b.issueNo where a.batchNo like '"+batchNo+"'";
			if(cmbProductionType.getValue().toString().equalsIgnoreCase("Lacqure")){
				sql="select distinct b.productId,b.productName from tbIssueToLacqureInfo a  "+
					" inner join tbIssueToLacqureDetails b  "+
					" on a.issueNo=b.issueNo where a.batchNo like '"+batchNo+"'";
			}
			/*else if(cmbProductionType.getValue().toString().equalsIgnoreCase("Assemble")){
				sql="select distinct b.productId,b.productName from tbIssueToAssembleInfo a  "+
						" inner join tbIssueToAssembleDetails b  "+
						" on a.issueNo=b.issueNo where a.batchNo like '"+batchNo+"'";
			}*/
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbFgName.addItem(element[0]);
				cmbFgName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void reportView()
	{
		String mouldName= "";
		String productiontype = "";
		String productionstep="";
		String batchNo="%",fgName="%";
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();

		if(cmbProductionType.getValue()!=null)
		{
			productiontype=cmbProductionType.getValue().toString();	
		}

		/*if(cmbBatchNo.getValue()!=null)
		{
			productionstep=cmbBatchNo.getValue().toString();	
		}*/

		if(cmbBatchNo.getValue()!=null)
		{
			batchNo = cmbBatchNo.getValue().toString();
		}
		if(cmbFgName.getValue()!=null)
		{
			fgName = cmbFgName.getValue().toString();
		}


		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("productionType", cmbProductionType.getValue().toString());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());

			String query = "select * from funcBatchWiseStock('"+batchNo+"','"+fgName+"','"+cmbProductionType.getValue()+"')  order by semiFgName";

			hm.put("sql", query);

			List lst = session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{	
				int type=0;

				if(chkpdf.booleanValue())
				{
					type=1;
				}

				else
				{
					type=0;	
				}


				Window win = new ReportViewerNew(hm,"report/production/RptBatchWiseStock.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);

				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);	
			}
			else
			{
				showNotification("There Is No Data",Notification.TYPE_WARNING_MESSAGE);	
			}

		}
		catch(Exception exp)
		{
			showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void reportViewDeatails()
	{
		String mouldName= "";
		String productiontype = "";
		String productionstep="";
		String batchNo="%",fgName="%";
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();

		if(cmbProductionType.getValue()!=null)
		{
			productiontype=cmbProductionType.getValue().toString();	
		}

		/*if(cmbBatchNo.getValue()!=null)
		{
			productionstep=cmbBatchNo.getValue().toString();	
		}*/

		if(cmbBatchNo.getValue()!=null)
		{
			batchNo = cmbBatchNo.getValue().toString();
		}
		if(cmbFgName.getValue()!=null)
		{
			fgName = cmbFgName.getValue().toString();
		}


		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("productionType", cmbProductionType.getValue().toString());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());

			String query = "select * from funcBatchWiseProduction('"+batchNo+"','"+fgName+"','"+cmbProductionType.getValue()+"')  order by semiFgName";

			hm.put("sql", query);

			List lst = session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{	
				int type=0;

				if(chkpdf.booleanValue())
				{
					type=1;
				}

				else
				{
					type=0;	
				}


				Window win = new ReportViewerNew(hm,"report/production/RptBatchWiseStockDetails.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);

				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);	
			}
			else
			{
				showNotification("There Is No Data",Notification.TYPE_WARNING_MESSAGE);	
			}

		}
		catch(Exception exp)
		{
			showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
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
		setWidth("570px");
		setHeight("280px");

		lblProductionType = new Label();
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		lblProductionType.setValue("Production Type :");
		mainLayout.addComponent(lblProductionType, "top:16.0px;left:40.0px;");

		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setNullSelectionAllowed(false);
		cmbProductionType.setNewItemsAllowed(false);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbProductionType, "top:15.0px;left:140.0px;");
		
		/*cmbProductionType.addItem("Dry Offset Printing");
		cmbProductionType.addItem("Screen Printing");
		cmbProductionType.addItem("Heat Trasfer Label");
		cmbProductionType.addItem("Manual Printing");
		cmbProductionType.addItem("Labeling");
		cmbProductionType.addItem("Cap Folding");
		cmbProductionType.addItem("Stretch Blow Molding");*/
		
		//cmbProductionType.addItem("Assemble");
		cmbProductionType.addItem("Dry Offset Printing");
		cmbProductionType.addItem("Screen Printing");
		cmbProductionType.addItem("Heat Trasfer Label");
		cmbProductionType.addItem("Manual Printing");
		cmbProductionType.addItem("Labeling");
		cmbProductionType.addItem("Lacqure");
		cmbProductionType.addItem("Cap Folding");
		cmbProductionType.addItem("Stretch Blow Molding");


		lblBatchNo = new Label();
		lblBatchNo.setImmediate(false);
		lblBatchNo.setWidth("-1px");
		lblBatchNo.setHeight("-1px");
		lblBatchNo.setValue("Batch No :");
		mainLayout.addComponent(lblBatchNo, "top:41.0px;left:40.0px;");

		cmbBatchNo= new ComboBox();
		cmbBatchNo.setImmediate(true);
		cmbBatchNo.setWidth("200px");
		cmbBatchNo.setHeight("24px");
		cmbBatchNo.setNullSelectionAllowed(true);
		cmbBatchNo.setNewItemsAllowed(false);
		cmbBatchNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbBatchNo, "top:40.0px;left:140.0px;");

		chkAllBatchNo = new CheckBox("All");
		chkAllBatchNo.setImmediate(true);
		chkAllBatchNo.setValue(false);
		chkAllBatchNo.setWidth("-1px");
		chkAllBatchNo.setHeight("24px");
		mainLayout.addComponent(chkAllBatchNo, "top:41.0px;left:340.0px;");


		lblFgName = new Label();
		lblFgName.setImmediate(false);
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");
		lblFgName.setValue("FG Name :");
		mainLayout.addComponent(lblFgName, "top:67.0px;left:40.0px;");

		cmbFgName= new ComboBox();
		cmbFgName.setImmediate(false);
		cmbFgName.setWidth("280px");
		cmbFgName.setHeight("24px");
		cmbFgName.setNullSelectionAllowed(true);
		cmbFgName.setNewItemsAllowed(false);
		cmbFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbFgName, "top:66.0px;left:140.0px;");
		
		lblReportType = new Label();
		lblReportType.setImmediate(false);
		lblReportType.setWidth("-1px");
		lblReportType.setHeight("-1px");
		lblReportType.setValue("Report Type: ");
		mainLayout.addComponent(lblReportType, "top:95.0px;left:40.0px;");

		
		opgSummary=new OptionGroup("",Optiontype);
		opgSummary.setImmediate(true);
		opgSummary.setValue("Summary");
		opgSummary.setStyleName("horizontal");
		mainLayout.addComponent(opgSummary, "top:93.0px;left:130.0px;");

		chkAllFgName = new CheckBox("All");
		chkAllFgName.setImmediate(true);
		chkAllFgName.setValue(false);
		chkAllFgName.setWidth("-1px");
		chkAllFgName.setHeight("24px");
		mainLayout.addComponent(chkAllFgName, "top:67.0px;left:420.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:120.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:145.0px;left:0.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:165.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:165.opx; left:220.0px");

		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
