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
public class RptMasterProductOpening extends Window{

	SessionBean sessionBean;
	private Label lblMasterProductName;
	private ComboBox cmbPartyName=new ComboBox();
	private Label lblPartyName;
	private ComboBox cmbMasterProductName=new ComboBox();

	private CheckBox chkAll=new CheckBox("All");
	private CheckBox chkAllParty=new CheckBox("All");
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
	private OptionGroup opgValue;
	private static final List<String> OptionValue=Arrays.asList(new String[]{"With Value","All FG"});


	private AbsoluteLayout mainLayout;

	public RptMasterProductOpening(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("ASSEMBLE OPENING::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
		cmbSemiFgLoad();
	}

	private void setEventAction() {		
		
		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbMasterProductName.getValue()!=null|| chkAll.booleanValue()==true)
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
					cmbMasterProductName.setEnabled(false);
					cmbMasterProductName.setValue(null);

				}
				else{
					cmbMasterProductName.setEnabled(true);
					cmbMasterProductName.focus();
				}
			}
		});
		chkAllParty.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllParty.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbPartyName.setEnabled(false);
					cmbPartyName.setValue(null);
					cmbMaterProductDataLoad();

				}
				else{
					cmbPartyName.setEnabled(true);
					cmbPartyName.focus();
					cmbMaterProductDataLoad();
				}
			}
		});
		cmbPartyName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbPartyName.getValue()!=null||chkAllParty.booleanValue()){
					cmbMaterProductDataLoad();
				}
				else{
					cmbMasterProductName.removeAllItems();
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
	private void cmbMaterProductDataLoad() {
		cmbMasterProductName.removeAllItems();
		String partyId="%";
		if(cmbPartyName.getValue()!=null){
			partyId=cmbPartyName.getValue().toString();
		}
		
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct fgCode,fgName from tbMasterProductOpening where partyId like '"+partyId+"' order by fgName";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbMasterProductName.addItem(element[0]);
				cmbMasterProductName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void cmbSemiFgLoad() {
		cmbPartyName.removeAllItems();
		
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct partyId,partyName from tbMasterProductOpening order by partyName";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbPartyName.addItem(element[0]);
				cmbPartyName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
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
		
		if(chkAll.booleanValue()==true)
			SemiFg="%";
		else
			SemiFg=cmbMasterProductName.getValue().toString();

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("openyear",new SimpleDateFormat("yyyy").format(formDate.getValue()) );
			//hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			//hm.put("fromdate", new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()));
			//hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			//hm.put("productionStep",cmbMasterProductName.getItemCaption(cmbMasterProductName.getValue()) );
			//hm.put("user", sessionBean.getUserName());



			/*query="select * from funcSemiFgWiseProductionDateBetween" +
					"('"+SemiFg+"','"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"') order by productionDate,semiFgName";
			System.out.println(query);


			hm.put("sql", query);
			String subQuery="select a.semiFgName,a.unit,a.color,sum(a.totalProduction)totalProduciton,sum(a.totalRejection)totalRejection from( "+
						" select * from funcSemiFgWiseProductionDateBetween('"+SemiFg+"','"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"') "+
						" )a group by a.semiFgName,a.unit,a.color ";
			hm.put("subQuery", subQuery);
			hm.put("SUBREPORT_DIR", "./report/production/");*/
			
			

			/*List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/SemiFgProductionDateBetween.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}*/
			
			String semiFg="%";
			if(cmbMasterProductName.getValue()!=null){
				semiFg=cmbMasterProductName.getValue().toString();
			}
			String partyId="%";
			if(cmbPartyName.getValue()!=null){
				partyId=cmbPartyName.getValue().toString();
			}
				query="select openingYear,partyName,fgName,openingQty,rate,amount from tbMasterProductOpening " +
						"where partyId like '"+partyId+"' and  fgCode like '"+semiFg+"' order by partyName,fgName";
				hm.put("sql", query);
				List list=session.createSQLQuery(query).list();
				if(!list.isEmpty()){
					Window win = new ReportViewerNew(hm,"report/production/RptMasterProductOpeningStock.jasper",
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
		mainLayout.setHeight("220px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("530px");
		setHeight("300px");

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
		mainLayout.addComponent(chkAll, "top:45.0px;left:450.0px;");

		lblMasterProductName = new Label();
		lblMasterProductName.setImmediate(false);
		lblMasterProductName.setWidth("-1px");
		lblMasterProductName.setHeight("-1px");
		lblMasterProductName.setValue("FG Name :");
		mainLayout.addComponent(lblMasterProductName, "top:41.0px;left:20.0px;");

		cmbMasterProductName= new ComboBox();
		cmbMasterProductName.setImmediate(true);
		cmbMasterProductName.setWidth("300px");
		cmbMasterProductName.setHeight("24px");
		cmbMasterProductName.setNullSelectionAllowed(true);
		cmbMasterProductName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbMasterProductName, "top:45.0px;left:140.0px;");
		
		chkAllParty.setImmediate(true);
		chkAllParty.setWidth("-1px");
		chkAllParty.setHeight("-1px");
		mainLayout.addComponent(chkAllParty, "top:15.0px;left:450.0px;");

		lblPartyName = new Label();
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		lblPartyName.setValue("Party Name :");
		mainLayout.addComponent(lblPartyName, "top:16.0px;left:20.0px;");

		cmbPartyName= new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("300px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbPartyName, "top:15.0px;left:140.0px;");

		/*lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:43.0px;left:20.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:41.0px;left:140.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:69.0px;left:20.0px;");
		
		

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:67.0px;left:140.0px;");
		
		lblReportType = new Label();
		lblReportType.setImmediate(false);
		lblReportType.setWidth("-1px");
		lblReportType.setHeight("-1px");
		lblReportType.setValue("Report Type: ");
		mainLayout.addComponent(lblReportType, "top:93.0px;left:20.0px;");
		
		opgValue=new OptionGroup("",OptionValue);
		opgValue.setImmediate(true);
		opgValue.setValue("With Value");
		opgValue.setStyleName("horizontal");
		mainLayout.addComponent(opgValue, "top:91.0px;left:140.0px;");*/

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:114.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:124.0px;left:25.0px;");

		previewButton.setWidth("100px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:160.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:160.opx; left:220.0px");

//		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;
	}
}
