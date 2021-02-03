package com.example.CostingReport;
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
public class COGPBetweenDateOLD extends Window{

	SessionBean sessionBean;
	private Label lblFrom=new Label("From : ");
	private ComboBox cmbMasterProduct=new ComboBox();
	private Label lblTo=new Label("To : ");
	private ComboBox cmbStep=new ComboBox();
	

	private CheckBox chkAll=new CheckBox("All");
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

	public COGPBetweenDateOLD(SessionBean sessionBean){

		this.sessionBean=sessionBean;
		this.setCaption("ASTECH COGP STANDARD BASE::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		masterproductDataLoad();
		this.addComponent(mainLayout);
		setEventAction();
	}
	private void masterproductDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select vProductId,vProductName from tbFinishedProductInfo order by vProductName";
			List list=session.createSQLQuery(sql).list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbMasterProduct.addItem(element[0]);
				if(i==0){
					cmbMasterProduct.setValue(element[0]);
					// productionStepDataLoad();
					i=1;
				}
				cmbMasterProduct.setItemCaption(element[0], element[1].toString());
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
				if(cmbMasterProduct.getValue()!=null||chkAll.booleanValue()){
					reportView();
				}
				else{
					showNotification("Please Provide Master Product",Notification.TYPE_WARNING_MESSAGE);
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
					cmbMasterProduct.setEnabled(false);
					cmbMasterProduct.setValue(null);

				}
				else{
					cmbMasterProduct.setEnabled(true);
					cmbMasterProduct.focus();
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
		String query=null,SubSqlMis=null,subSqlConvertion=null,ProductId="%";
		Transaction tx=null;

		if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;
		if(!chkAll.booleanValue()){
			ProductId=cmbMasterProduct.getValue().toString();
		}

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo",sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromdate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("issueFrom",cmbMasterProduct.getItemCaption(cmbMasterProduct.getValue()) );
			
			/*query=  "select rawid,rawName,SUM(consumpedQty)consumpedQty,sum(amount)amount,sum(processWastage)as processWastage,sum(processWastageAmount)as processWastageAmount from " +
					"funcCOGPStandardAstech('"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+ " 00:00:00"+"','"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+" 23:59:59"+"') group by rawid,rawName " +
					"order by cast(subString(rawid,CHARINDEX('-',rawid)+1,len(rawId)-CHARINDEX('-',rawid))as int)";
			
			String SubSqlMis="select SUM(aitImport)aitImport,SUM(aitSales)aitSales,SUM(salesAmount)salesAmount," +
					"sum(convertionAmount)convertionAmount,(sum(amount)+sum(processWastageAmount))totalCost," +
					"(sum(convertionAmount)+sum(amount)+sum(processWastageAmount))ttlCst,(SUM(salesAmount)-" +
					"((sum(convertionAmount)+sum(amount)+sum(processWastageAmount)))-SUM(aitImport))D_C from " +
					"funcCOGPStandardAstech('"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"','"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"') ";

			String subSqlConvertion="select * from funcCOGPConVertionCostNew('"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"','"+new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"')";*/
			
			 query="select RawItemCode,RawItemName,isNull(SUM(ConsumedQty),0)ConsumedQty," +
			 		"isnull(SUM(ConsumedAmount),0)ConsumedAmount from "+
					" [funcCOGPDateBetween]('"+ProductId+"','"+datef.format(formDate.getValue())+"'," +
					"'"+datef.format(toDate.getValue())+"') group by RawItemCode,RawItemName order by RawItemName";
			
			hm.put("sql", query);
			hm.put("SubSqlMis", SubSqlMis);
			hm.put("subSqlConvertion",subSqlConvertion);
			hm.put("path", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptCogpStandardDateBetween.jasper",
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
		mainLayout.setWidth("600px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("620px");
		setHeight("280px");

		lblFrom = new Label();
		lblFrom.setImmediate(false);
		lblFrom.setWidth("-1px");
		lblFrom.setHeight("-1px");
		lblFrom.setValue("Master Product Name :");
		mainLayout.addComponent(lblFrom, "top:16.0px;left:20.0px;");

		cmbMasterProduct = new ComboBox();
		cmbMasterProduct.setImmediate(true);
		cmbMasterProduct.setWidth("350px");
		cmbMasterProduct.setHeight("24px");
		cmbMasterProduct.setNullSelectionAllowed(true);
		cmbMasterProduct.setNewItemsAllowed(false);
		//cmbMasterProduct.setEnabled(false);
		mainLayout.addComponent( cmbMasterProduct, "top:15.0px;left:160.0px;");

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
		mainLayout.addComponent(lblFDate, "top:41.0px;left:20.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:41.0px;left:160.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:67.0px;left:20.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:67.0px;left:160.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:115.0px; left:160.0px");

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
		mainLayout.addComponent( chkAll, "top:17.0px;left:525.0px;");
		lblAll.setVisible(false);
		return mainLayout;


	}
}
