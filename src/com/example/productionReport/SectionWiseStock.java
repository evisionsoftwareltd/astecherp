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
public class SectionWiseStock extends Window{

	SessionBean sessionBean;
	private Label lblSemiFgName;
	//private ComboBox cmbType=new ComboBox();
	//private Label lblProductionType;
	private ComboBox cmbSemiFgName=new ComboBox();
	private ComboBox cmbproduction=new ComboBox();

	private CheckBox chkAll=new CheckBox("All");
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblAsonDate,lblProductionType;
	
	private Label lblLine;
	private Label lblReportType =new Label("Report Type :");
	private PopupDateField asonDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private OptionGroup opgValue;
	private static final List<String> OptionValue=Arrays.asList(new String[]{"With Value","All FG"});


	private AbsoluteLayout mainLayout;

	public SectionWiseStock(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("SECTION WISE STOCK::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
		//cmbSemiFgLoad();
		productionTypeLoad();
	}

	private void setEventAction() {		
		
		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbSemiFgName.getValue()!=null|| chkAll.booleanValue()==true)
					{
						reportView(); 
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
		
		cmbproduction.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbproduction.getValue()!=null)
				{
				   cmbSemiFgLoad();	
				}
				
			}
		});
	
	}
	private void cmbSemiFgLoad() {
		cmbSemiFgName.removeAllItems();
		
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			//String sql="select distinct fgId,fgName from funcFgStock('%',getdate()) where FgStock>0";
			
			String sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation where productionStepId='"+cmbproduction.getValue().toString()+" '";
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
	
	
	private void productionTypeLoad() {
		cmbproduction.removeAllItems();
		
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			//String sql="select distinct fgId,fgName from funcFgStock('%',getdate()) where FgStock>0";
			
			String sql="select distinct  productionStepId,productionStepName from tbSemiFgSubInformation where productionStepId!='' and  productionStepId!='Assemble'";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbproduction.addItem(element[0]);
				cmbproduction.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			//this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
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
		
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("openyear",new SimpleDateFormat("yyyy").format(asonDate.getValue()) );
			//hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("asOnDate", new SimpleDateFormat("dd-MM-yyyy").format(asonDate.getValue()));
			hm.put("productionType",cmbproduction.getItemCaption(cmbproduction.getValue()) );
			//hm.put("productionStep",cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue()) );
			//hm.put("user", sessionBean.getUserName());
			
			String semiFg="%";
			if(cmbSemiFgName.getValue()!=null){
				semiFg=cmbSemiFgName.getValue().toString();
			}
			String productType= cmbproduction.getValue().toString();
			
			if(opgValue.getValue().toString().equalsIgnoreCase("With Value")){
			    query= "select * from funcSectionWiseStock('"+productType+"','"+semiFg+"','"+datef.format(asonDate.getValue())+"') where closingStock>0 ";
			}
			else{
			    	 query= "select * from funcSectionWiseStock('"+productType+"','"+semiFg+"','"+datef.format(asonDate.getValue())+"')  ";
				}	
			
				//query="select * from funcSemiFgStockDateBetween('"+semiFg+"','"+datef.format(asonDate.getValue())+"','"+datef.format(toDate.getValue())+"') where semiFgStock>0";
				hm.put("sql", query);
				List list=session.createSQLQuery(query).list();
				if(!list.isEmpty()){
					Window win = new ReportViewerNew(hm,"report/production/RptSectionWiseStockAsonDate.jasper",
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
		mainLayout.setWidth("500px");
		mainLayout.setHeight("250px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("530px");
		setHeight("330px");

		
		lblProductionType = new Label();
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		lblProductionType.setValue("Production Type:");
		mainLayout.addComponent(lblProductionType, "top:16.0px;left:20.0px;");

		cmbproduction= new ComboBox();
		cmbproduction.setImmediate(true);
		cmbproduction.setWidth("300px");
		cmbproduction.setHeight("24px");
		cmbproduction.setNullSelectionAllowed(true);
		cmbproduction.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbproduction, "top:15.0px;left:140.0px;");
		
		
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:45.0px;left:450.0px;");

		lblSemiFgName = new Label();
		lblSemiFgName.setImmediate(false);
		lblSemiFgName.setWidth("-1px");
		lblSemiFgName.setHeight("-1px");
		lblSemiFgName.setValue("FG Name :");
		mainLayout.addComponent(lblSemiFgName, "top:45.0px;left:20.0px;");

		cmbSemiFgName= new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("300px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setNullSelectionAllowed(true);
		cmbSemiFgName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbSemiFgName, "top:45.0px;left:140.0px;");

		lblAsonDate = new Label();
		lblAsonDate.setImmediate(false);
		lblAsonDate.setWidth("-1px");
		lblAsonDate.setHeight("-1px");
		lblAsonDate.setValue("As on Date: ");
		mainLayout.addComponent(lblAsonDate, "top:75.0px;left:20.0px;");

		asonDate.setImmediate(true);
		asonDate.setResolution(PopupDateField.RESOLUTION_DAY);
		asonDate.setValue(new java.util.Date());
		asonDate.setDateFormat("dd-MM-yyyy");
		asonDate.setWidth("107px");
		asonDate.setHeight("-1px");
		asonDate.setInvalidAllowed(false);
		mainLayout.addComponent( asonDate, "top:75.0px;left:140.0px;");
		
		lblReportType = new Label();
		lblReportType.setImmediate(false);
		lblReportType.setWidth("-1px");
		lblReportType.setHeight("-1px");
		lblReportType.setValue("Report Type: ");
		mainLayout.addComponent(lblReportType, "top:135.0px;left:20.0px;");
		
		opgValue=new OptionGroup("",OptionValue);
		opgValue.setImmediate(true);
		opgValue.setValue("With Value");
		opgValue.setStyleName("horizontal");
		mainLayout.addComponent(opgValue, "top:135.0px;left:140.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:165.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:195.0px;left:25.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:205.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:205.opx; left:220.0px");

//		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
