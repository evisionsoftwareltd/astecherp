package com.example.CostingReport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.control.messages.Message;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RptConversionCostEntry extends Window {

	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	ComboBox cmbPartyName,cmbMasterProductName,cmbProductionStep,cmbSemiFgName,cmbFgName,cmbUnit;

	private List<String> list=Arrays.asList("SEMI FG","FG","MASTER PRODUCT");
	OptionGroup optionGroup=new OptionGroup();
	Label lblPartyName=new Label("Party Name: ");
	Label lblSemiFg=new Label("Semi FG Name: ");
	Label lblMasterProduct=new Label("Master Product Name: ");
	Label lblFg=new Label("FG Name: ");
	Label lblProductionStep=new Label("Production Step: ");
	private HorizontalLayout chklayout=new HorizontalLayout();
	private List<String> listReportType=Arrays.asList("SUMMARY","DETAILS");
	OptionGroup optionGroupReport=new OptionGroup();
	private Label lblLine;
	private Label lblAll=new Label();
	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");

	public RptConversionCostEntry(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("CONVERSION COST ENTRY :: "+this.sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		partyNameLoadData();
	}

	private void productionStepDataLoadSemiFg() {
		cmbProductionStep.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			//String sql="select productTypeId,productTypeName from tbProductionType where productTypeId in('PT-1','PT-2','PT-4')";
			String sql="SElect distinct stepId,stepName from tbConversionCostInfo where ProductType like 'SEMI FG' and semiFgId = '"+cmbSemiFgName.getValue().toString()+"' order by stepName";
			System.out.println(sql);
			List list=session.createSQLQuery(sql).list();
			
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbProductionStep.addItem(element[0]);
				cmbProductionStep.setItemCaption(element[0],element[1].toString());
			}	
		}
		catch(Exception exp)
		{
			showNotification( exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void productionStepDataLoadFG() {
		cmbProductionStep.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			//String sql="select productTypeId,productTypeName from tbProductionType where productTypeId in('PT-1','PT-2','PT-4')";
			String sql="SElect distinct stepId,stepName from tbConversionCostInfo where ProductType like 'FG' and FgId = '"+cmbFgName.getValue().toString()+"' order by stepName";
			System.out.println(sql);
			List list=session.createSQLQuery(sql).list();
			
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbProductionStep.addItem(element[0]);
				cmbProductionStep.setItemCaption(element[0],element[1].toString());
			}	
		}
		catch(Exception exp)
		{
			showNotification( exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	
		/*cmbProductionStep.removeAllItems();
		cmbProductionStep.addItem("Dry Offset Printing");
		cmbProductionStep.addItem("Screen Printing");
		cmbProductionStep.addItem("Heat Trasfer Label");
		cmbProductionStep.addItem("Manual Printing");
		cmbProductionStep.addItem("Labeling");
		cmbProductionStep.addItem("Lacqure");
		cmbProductionStep.addItem("Cap Folding");
		cmbProductionStep.addItem("Stretch Blow Molding");*/
	}
	public void txtClear()
	{
		try
		{	
			cmbPartyName.setValue(null);
			cmbFgName.setValue(null);
			cmbSemiFgName.setValue(null);
			cmbMasterProductName.setValue(null);
			cmbProductionStep.setValue(null);
		}
		catch(Exception ex)
		{
			showNotification("Error",ex.toString(),Notification.TYPE_WARNING_MESSAGE);
		}

	}
	private void productionStepDataLoadMasterProduct() {
		cmbProductionStep.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			//String sql="select productTypeId,productTypeName from tbProductionType where productTypeId in('PT-1','PT-2','PT-4')";
			String sql="SElect distinct stepId,stepName from tbConversionCostInfo where ProductType like 'MASTER PRODUCT' and masterProductId = '"+cmbMasterProductName.getValue().toString()+"' order by stepName";
			System.out.println(sql);
			List list=session.createSQLQuery(sql).list();
			
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbProductionStep.addItem(element[0]);
				cmbProductionStep.setItemCaption(element[0],element[1].toString());
			}	
		}
		catch(Exception exp)
		{
			showNotification( exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();
		}
		}

	
	private void masterProductNameDataLoad()
	{
		cmbMasterProductName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String partyId="%";
			if(cmbPartyName.getValue()!=null){
				partyId=cmbPartyName.getValue().toString();
			}
			/*String query = " select vProductId,vProductName from tbFinishedProductInfo " +
					"where vCategoryId='"+partyId+"' order by vProductName";*/
			
			String query = "select distinct masterProductId,masterProductName from tbConversionCostInfo where partyId='"+partyId+"' order by masterProductName";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbMasterProductName.addItem(element[0]);
				cmbMasterProductName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void partyNameLoadData()
	{
		cmbPartyName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select distinct vCategoryId,vCategoryName from tbFinishedProductInfo" +
					" where vCategoryId in(select distinct partyId from tbConversionCostInfo) " +
					" order by vCategoryName";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbPartyName.addItem(element[0]);
				cmbPartyName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("partyNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbSemiFgDataLoad()
	{
		cmbSemiFgName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select semiFgCode,semiFgName+' # '+color semiFgName from tbSemiFgInfo " +
					"where status='Active' and semiFgCode in(select distinct semiFgId from tbConversionCostInfo) order by semiFgName";

			/*String query = "SElect semiFgId,(SElect semiFgName+' # '+color semiFgName from tbSemiFgInfo where semiFgCode=tbConversionCostInfo.semiFgId) semiFgName "
			 +"from tbConversionCostInfo where masterProductId= '"+cmbMasterProductName.getValue()+"' ";*/
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
				
			{
				Object element[]=(Object[])iter.next();
				cmbSemiFgName.addItem(element[0]);
				cmbSemiFgName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("partyNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbFgDataLoad(){
		cmbFgName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "SElect FgId,FgName from tbConversionCostInfo where masterProductId='"+cmbMasterProductName.getValue()+"' ";
					//"select b.semiFgSubId,b.semiFgSubName from tbFinishedProductInfo a inner join tbFinishedProductDetailsNew b   "+
					//" on a.vProductId=b.fgId  where a.vProductId like '"+cmbMasterProductName.getValue()+"' " ;
			//"and a.vProductId in(select distinct fgId from tbConversionCostInfo) and b.semiFgSubId not like ''";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbFgName.addItem(element[0]);
				cmbFgName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("partyNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbUnitDataLoad(){
		cmbUnit.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "SElect vUnitName from tbFinishedProductInfo where vProductId  like '"+cmbMasterProductName.getValue()+"' " ;
			//"and a.vProductId in(select distinct fgId from tbConversionCostInfo) and b.semiFgSubId not like ''";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			System.out.println(query);
			while(iter.hasNext())
			{
				cmbUnit.addItem(iter.next());
			}
		}
		catch(Exception exp)
		{
			showNotification("PartyName Alreadry Exist. Check", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEventAction() {
		optionGroup.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(optionGroup.getValue().equals("SEMI FG")){
					txtClear();
					cmbProductionStep.removeAllItems();
					cmpTrueFalse();
					lblSemiFg.setVisible(true);
					lblProductionStep.setVisible(true);
					cmbSemiFgName.setVisible(true);
					cmbProductionStep.setVisible(true);
					cmbSemiFgDataLoad();
					
				}
				else if(optionGroup.getValue().equals("FG")){
					txtClear();
					cmbProductionStep.removeAllItems();
					cmpTrueFalse();
					lblPartyName.setVisible(true);
					lblMasterProduct.setVisible(true);
					lblProductionStep.setVisible(true);
					cmbPartyName.setVisible(true);
					cmbMasterProductName.setVisible(true);
					cmbProductionStep.setVisible(true);
					lblFg.setVisible(true);
					lblFg.setValue("FG Name: ");
					cmbUnit.setVisible(false);
					cmbFgName.setVisible(true);
					/*if(cmbFgName.getValue()!=null)
					{
					productionStepDataLoadFG();}*/
				}
				else if(optionGroup.getValue().equals("MASTER PRODUCT")){
					txtClear();
					cmbProductionStep.removeAllItems();
					cmpTrueFalse();
					lblPartyName.setVisible(true);
					lblMasterProduct.setVisible(true);
					lblProductionStep.setVisible(true);
					cmbPartyName.setVisible(true);
					cmbMasterProductName.setVisible(true);
					cmbProductionStep.setVisible(true);
					/*if(cmbMasterProductName.getValue()!=null)
					{
					productionStepDataLoadMasterProduct();}*/
				}
				else{
					cmpTrueFalse();
				}
			}
		});
		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPartyName.getValue()!=null){
					masterProductNameDataLoad();
				}
				else{
					cmbMasterProductName.removeAllItems();
				}
			}
		});
		cmbMasterProductName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				
				if(cmbMasterProductName.getValue()!=null){
					cmbFgDataLoad();
					if(optionGroup.getValue().equals("MASTER PRODUCT")){
					productionStepDataLoadMasterProduct();}
					cmbUnitDataLoad();
				}
				else{
					cmbFgName.removeAllItems();
					cmbUnit.removeAllItems();
				
				}
			}
		});
		cmbSemiFgName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(optionGroup.getValue().equals("SEMI FG")){
				if(cmbSemiFgName.getValue()!=null)
				{
				productionStepDataLoadSemiFg();
				}
				else{
					
				}
				}
			}
		});
		cmbFgName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(optionGroup.getValue().equals("FG")){
				if(cmbFgName.getValue()!=null)
				{
				productionStepDataLoadFG();
				}
				else{
					
				}
				}
			}
		});
		optionGroupReport.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(optionGroupReport.getValue().equals("SUMMARY")){
					optionGroup.setEnabled(true);
					cmbUnit.setVisible(false);
					lblFg.setVisible(false);
					lblProductionStep.setVisible(true);
					cmbProductionStep.setVisible(true);
				}
				else if(optionGroupReport.getValue().equals("DETAILS")){
					optionGroup.setValue("MASTER PRODUCT");
					lblFg.setVisible(true);
					lblFg.setValue("Unit : ");
					cmbUnit.setVisible(true);
					optionGroup.setEnabled(false);
					lblProductionStep.setVisible(false);
					cmbProductionStep.setVisible(false);
				}
			}
		});
		

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(optionGroupReport.getValue().equals("SUMMARY")){
					if(optionGroup.getValue().equals("SEMI FG")){
						if(cmbSemiFgName.getValue()!=null && cmbProductionStep.getValue()!=null)
						{
							reportView(); 
						}
						else
						{
							showNotification("Warning!! Please provide all data.",Notification.TYPE_WARNING_MESSAGE);
						}	
					}
					if(optionGroup.getValue().equals("FG")){
						if(cmbPartyName.getValue()!=null && cmbMasterProductName.getValue()!=null && cmbFgName.getValue()!=null || cmbProductionStep.getValue()!=null)
						{
							reportView(); 
						}
						else
						{
							showNotification("Warning!! Please provide all data.",Notification.TYPE_WARNING_MESSAGE);
						}	
					}
					if(optionGroup.getValue().equals("MASTER PRODUCT")){
						if(cmbPartyName.getValue()!=null && cmbMasterProductName.getValue()!=null &&  cmbProductionStep.getValue()!=null)
						{
							reportView(); 
						}
						else
						{
							showNotification("Warning!! Please provide all data.",Notification.TYPE_WARNING_MESSAGE);
						}	
					}

				}
				if(optionGroupReport.getValue().equals("DETAILS")){
					if(cmbPartyName.getValue()!=null && cmbMasterProductName.getValue()!=null && cmbUnit.getValue()!=null)
					{
						reportView(); 
					}
					else{
						showNotification("Warning!! Please provide all data.",Notification.TYPE_WARNING_MESSAGE);
					}
				}			
			}
		});
		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
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
	private void cmpTrueFalse(){
		lblPartyName.setVisible(false);
		cmbPartyName.setVisible(false);
		lblSemiFg.setVisible(false);
		cmbSemiFgName.setVisible(false);
		lblMasterProduct.setVisible(false);
		cmbMasterProductName.setVisible(false);
		lblFg.setVisible(false);
		cmbFgName.setVisible(false);
		lblProductionStep.setVisible(false);
		cmbProductionStep.setVisible(false);
	}
	private void reportView()
	{	
		String query=null;

		Transaction tx=null;
		String report = "";
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;

		String SemiFG = "",FG="",party="",productionStep="",masterProduct="";
		System.out.print("MNBVG");

		try{

			String subsql1="",subsql2="",subsql3="";

			HashMap hm=new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+""+sessionBean.getUserIp());
			System.out.println("up");
			hm.put("semiFG", cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue()));
			hm.put("FG",  cmbFgName.getItemCaption(cmbFgName.getValue()));
			hm.put("party",  cmbPartyName.getItemCaption(cmbPartyName.getValue()));
			hm.put("productionstep", cmbProductionStep.getItemCaption(cmbProductionStep.getValue()));
			hm.put("masterProduct", cmbMasterProductName.getItemCaption(cmbMasterProductName.getValue()));
			//hm.put("unit",  cmbUnit.getValue().toString());
			//System.out.println("down5");

			String headId="%";
			System.out.println("up123");
			if(optionGroupReport.getValue().equals("SUMMARY")){
				System.out.println("Q4");
				if(cmbProductionStep.getValue()!=null)
				{
					if(optionGroup.getValue().equals("SEMI FG")){
						if(cmbSemiFgName.getValue()!=null)
						{
							System.out.println("10");
							query="SElect b.headId,b.headName,b.amount,b.remarks,a.ProductType from tbConversionCostInfo a "
									+ "inner join tbConversionCostDetails b on a.transactionNo=b.transactionNo where a.stepId " +
									"like '"+cmbProductionStep.getValue()+"' "
									+ "and a.semiFgId like '"+cmbSemiFgName.getValue()+"' ";
							System.out.println(query);
						}
					}

					else if(optionGroup.getValue().equals("FG")){
						if(cmbFgName.getValue()!=null)
						{
							if(cmbMasterProductName.getValue()!=null)
							{
								query="SElect b.headId,b.headName,b.amount,b.remarks,a.ProductType from tbConversionCostInfo a "
										+ "inner join tbConversionCostDetails b on a.transactionNo=b.transactionNo where a.stepId like '"+cmbProductionStep.getValue()+"' "
										+ "and  a.FgId like '"+cmbFgName.getValue()+"'  and a.masterProductId " +
												"like '"+cmbMasterProductName.getValue()+"'  and " +
														"a.partyId like '"+cmbPartyName.getValue()+"' ";
								System.out.println(query);

							}}}

					else {
						if(cmbMasterProductName.getValue()!=null)
						{
							if(cmbPartyName.getValue()!=null)
							{
								query="SElect b.headId,b.headName,b.amount,b.remarks,a.ProductType from tbConversionCostInfo a "
										+ "inner join tbConversionCostDetails b on a.transactionNo=b.transactionNo where a.stepId like '"+cmbProductionStep.getValue()+"' "
										+ "and a.masterProductId like '"+cmbMasterProductName.getValue()+"'  and " +
												"a.partyId like '"+cmbPartyName.getValue()+"' ";
								System.out.println(query);
							}
						}	
					}
				}
				report = "report/production/RptConversionCost.jasper";
			}

			if(optionGroupReport.getValue().equals("DETAILS")){

				hm.put("unit",  cmbUnit.getValue());
				if(cmbPartyName.getValue()!=null && cmbMasterProductName.getValue()!=null && cmbUnit.getValue()!=null )
				{
					System.out.println("Q3");
					subsql1="select b.stepId,b.stepName,b.stepSl from tbProductionStepSelectionInfo a "
							+ "inner join tbProductionStepSelectionDetails b on a.transactionNo=b.transactionNo "
							+ "where FgId like '"+cmbMasterProductName.getValue().toString()+"' order by stepSl,stepName";
					System.out.println(subsql1);

					subsql2="select b.semiFgId,b.semiFgName,b.semiFgSubId,b.semiFgSubName,b.stdWeight, "
							+ "b.consumptionStage,b.qty from tbFinishedProductInfo a left join tbFinishedProductDetailsNew b  "
							+ "on a.vProductId=b.fgId  where a.vProductId like '"+cmbMasterProductName.getValue().toString()+"'";
					System.out.println(subsql2);

					query="select * from funcCostingEntry('"+cmbMasterProductName.getValue()+"')";
					System.out.println(query);

					subsql3="select SemiFgName,stepName,isnull(SUM(amount),0) Cost,sl from funcCostingEntry( '"+cmbMasterProductName.getValue().toString()+"') group by semiFgId,SemiFgName,stepName,sl order by sl";
					System.out.println(subsql3);	
				}
				else
				{
					this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
				}
				report = "report/production/RptConversionCostDetails.jasper";
			}


			hm.put("sql", query);
			hm.put("subsql1", subsql1);
			hm.put("subsql2", subsql2);
			hm.put("subsql3", subsql3);
			hm.put("SUBREPORT_DIR", "./report/production/");

			System.out.println("From HashMap "+hm.get("sql"));
			List list=session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				Window win = new ReportViewerNew(hm, report, 
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\", "/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp", false,
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
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("520px");
		setHeight("310px");

		optionGroupReport=new OptionGroup("", listReportType);
		optionGroupReport.setImmediate(true);
		optionGroupReport.setStyleName("horizontal");
		mainLayout.addComponent(new Label("Report Type: "), "top:7.0px;left:20.0px;");
		mainLayout.addComponent(optionGroupReport, "top:5.0px;left:145.0px;");

		optionGroup=new OptionGroup("", list);
		optionGroup.setImmediate(true);
		optionGroup.setStyleName("horizontal");
		mainLayout.addComponent(new Label("Product Type: "), "top:30.0px;left:20.0px;");
		mainLayout.addComponent(optionGroup, "top:28.0px;left:145.0px;");


		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("330px");
		cmbPartyName.setHeight("24px");	
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblPartyName, "top:60.0px;left:20.0px;");
		mainLayout.addComponent(cmbPartyName, "top:58.0px;left:150.0px;");

		cmbSemiFgName = new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("330px");
		cmbSemiFgName.setHeight("24px");	
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblSemiFg, "top:90.0px;left:20.0px;");
		mainLayout.addComponent(cmbSemiFgName, "top:88.0px;left:150.0px;");

		cmbMasterProductName = new ComboBox();
		cmbMasterProductName.setImmediate(true);
		cmbMasterProductName.setWidth("330px");
		cmbMasterProductName.setHeight("24px");	
		cmbMasterProductName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblMasterProduct, "top:90.0px;left:20.0px;");
		mainLayout.addComponent(cmbMasterProductName, "top:88.0px;left:150.0px;");

		cmbFgName = new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setWidth("330px");
		cmbFgName.setHeight("24px");	
		cmbFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblFg, "top:120.0px;left:20.0px;");
		mainLayout.addComponent(cmbFgName, "top:118.0px;left:150.0px;");

		cmbUnit= new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setVisible(false);
		cmbUnit.setWidth("330px");
		cmbUnit.setHeight("24px");	
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbUnit, "top:118.0px;left:150.0px;");

		cmbProductionStep = new ComboBox();
		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setWidth("330px");
		cmbProductionStep.setHeight("24px");	
		cmbProductionStep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblProductionStep, "top:150.0px;left:20.0px;");
		mainLayout.addComponent(cmbProductionStep, "top:148.0px;left:150.0px;");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:180.0px;left:0.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:200.0px; left:190.0px");

		previewButton.setWidth("100px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));

		mainLayout.addComponent(previewButton,"top:230.opx; left:160.0px");
		mainLayout.addComponent(exitButton,"top:230.opx; left:254.0px");

		cmpTrueFalse();

		return mainLayout;
	}
}
