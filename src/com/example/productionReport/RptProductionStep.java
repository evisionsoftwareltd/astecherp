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
public class RptProductionStep extends Window{

	SessionBean sessionBean;
	private Label lblProductName;
	private ComboBox cmbPartyName=new ComboBox();
	private Label lblPartyName;
	private ComboBox cmbProductName=new ComboBox();

	private CheckBox chkAll=new CheckBox("All");
	private CheckBox chkAllProduct=new CheckBox("All");
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();
	private Label lblFDate,lblToDate;
	private Label lblPartyNameDate;
	private Label lblLine;
	private Label lblReportType =new Label("Report Type :");
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private OptionGroup opgSummary;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Summary","Details"});


	private AbsoluteLayout mainLayout;

	public RptProductionStep(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("PRODUCTION STEP SELECTION::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		partyNameDataLoad();
		this.addComponent(mainLayout);
		setEventAction();
	}
	private void partyNameDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="SElect partyId,partyName from tbProductionStepSelectionInfo";
			List list=session.createSQLQuery(sql).list();

			int i=0;
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

	private void setEventAction() {		
		
		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbPartyName.getValue()!=null || chkAll.booleanValue()==true )
				{
					if(cmbProductName.getValue()!=null || chkAllProduct.booleanValue()==true)
					{
						reportView(); 
					}
					else
					{
						showNotification("Please Select Product Name.",Notification.TYPE_WARNING_MESSAGE); 
					}
				}
				else
				{
					showNotification("Please Select Party Name.",Notification.TYPE_WARNING_MESSAGE);
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
					cmbPartyName.setEnabled(false);
					cmbPartyName.setValue(null);
					cmbProductLoad();
				}
				else{
					cmbPartyName.setEnabled(true);
					cmbPartyName.focus();
				}
			}
		});

		chkAllProduct.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllProduct.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbProductName.setEnabled(false);
					cmbProductName.setValue(null);

				}
				else{
					cmbProductName.setEnabled(true);
					cmbProductName.focus();
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
		cmbPartyName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(cmbPartyName.getValue()!=null ||chkAll.booleanValue()==true){
					cmbProductLoad();
				}
				else{
					cmbProductName.removeAllItems();
				}
			}
		});

	}
	private void cmbProductLoad() {
		cmbProductName.removeAllItems();
		String party="";
		if(chkAll.booleanValue()==true)
		{
			party="%";
		}
		else
		{
			party=cmbPartyName.getValue().toString();
		}
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="SElect FgId,fgName from tbProductionStepSelectionInfo where partyId like '"+party+"' order by fgName";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbProductName.addItem(element[0]);
				cmbProductName.setItemCaption(element[0], element[1].toString());
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
		
		String product = "";
		String party = "";
		
		if(chkAll.booleanValue()==true)
			party="%";
		else
			party=cmbPartyName.getValue().toString();
		if(chkAllProduct.booleanValue()==true)
			product="%";
		else
			product=cmbProductName.getValue().toString();

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("openyear",new SimpleDateFormat("yyyy").format(formDate.getValue()) );
			//hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			//hm.put("fromdate", new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()));
		//	hm.put("productionType",cmbPartyName.getItemCaption(cmbPartyName.getValue()) );
			//hm.put("productionStep",cmbProductName.getItemCaption(cmbProductName.getValue()) );
			//hm.put("user", sessionBean.getUserName());

			query="SElect a.date,a.partyId,a.partyName,a.FgId,a.fgName,b.stepId,b.stepName,b.stepSl from "
					+ "tbProductionStepSelectionInfo a inner join tbProductionStepSelectionDetails b "
					+ "on a.transactionNo=b.transactionNo where a.partyId like  '"+party+"' and a.FgId like  '"+product+"'  order by a.partyName,a.fgName,b.stepSl";
				hm.put("sql", query);
				List list=session.createSQLQuery(query).list();
				if(!list.isEmpty()){
					Window win = new ReportViewerNew(hm,"report/production/RptProductionStep.jasper",
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
		mainLayout.setHeight("210px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("530px");
		setHeight("290px");

		lblPartyName = new Label();
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		lblPartyName.setValue("Party Name :");
		mainLayout.addComponent(lblPartyName, "top:16.0px;left:20.0px;");		

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("300px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(false);
		cmbPartyName.setNewItemsAllowed(false);
		cmbPartyName.setEnabled(true);
		mainLayout.addComponent( cmbPartyName, "top:15.0px;left:140.0px;");
		
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:17.0px;left:450.0px;");
		
		

		lblProductName = new Label();
		lblProductName.setImmediate(false);
		lblProductName.setWidth("-1px");
		lblProductName.setHeight("-1px");
		lblProductName.setValue("Product Name :");
		mainLayout.addComponent(lblProductName, "top:41.0px;left:20.0px;");

		cmbProductName= new ComboBox();
		cmbProductName.setImmediate(true);
		cmbProductName.setWidth("300px");
		cmbProductName.setHeight("24px");
		cmbProductName.setNullSelectionAllowed(true);
		cmbProductName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbProductName, "top:41.0px;left:140.0px;");
		
		chkAllProduct.setImmediate(true);
		chkAllProduct.setWidth("-1px");
		chkAllProduct.setHeight("-1px");
		mainLayout.addComponent(chkAllProduct, "top:43.0px;left:450.0px;");

		/*lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:67.0px;left:20.0px;");

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
		mainLayout.addComponent(lblToDate, "top:91.0px;left:20.0px;");
		
		

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:93.0px;left:140.0px;");*/
		
		lblReportType = new Label();
		lblReportType.setImmediate(false);
		lblReportType.setWidth("-1px");
		lblReportType.setHeight("-1px");
		lblReportType.setValue("Report Type: ");
		//mainLayout.addComponent(lblReportType, "top:115.0px;left:20.0px;");
		
		opgSummary=new OptionGroup("",Optiontype);
		opgSummary.setImmediate(true);
		opgSummary.setValue("Summary");
		opgSummary.setStyleName("horizontal");
		//mainLayout.addComponent(opgSummary, "top:117.0px;left:140.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:140.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:150.0px;left:25.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:175.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:175.opx; left:220.0px");

//		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
