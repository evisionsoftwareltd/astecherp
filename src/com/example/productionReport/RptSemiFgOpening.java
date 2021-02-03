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
public class RptSemiFgOpening extends Window{

	SessionBean sessionBean;
	private Label lblSemiFgName;
	private ComboBox cmbType=new ComboBox();
	private Label lblProductionType;
	private ComboBox cmbSemiFgName=new ComboBox();

	private CheckBox chkAll=new CheckBox("All");
	private CheckBox chkAllType=new CheckBox("All");
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
	private static final List<String> OptionValue=Arrays.asList(new String[]{"With Value","All Semi FG"});


	private AbsoluteLayout mainLayout;

	public RptSemiFgOpening(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("SEMI FG OPENING STOCK ::"+sessionBean.getCompany());
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
			String sql="select distinct productionTypeId,productionTypeName from tbSemiFgOpening";
			List list=session.createSQLQuery(sql).list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbType.addItem(element[0].toString());				
				cmbType.setItemCaption(element[0].toString(), element[1].toString());
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
				if(cmbType.getValue()!=null|| chkAllType.booleanValue()==true )
				{
					if(cmbSemiFgName.getValue()!=null|| chkAll.booleanValue()==true)
					{
						reportView(); 
					}
					else
					{
						showNotification("Please Select Semi Fg Name",Notification.TYPE_WARNING_MESSAGE); 
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
					cmbSemiFgName.setEnabled(false);
					cmbSemiFgName.setValue(null);

				}
				else{
					cmbSemiFgName.setEnabled(true);
					cmbSemiFgName.focus();
				}
			}
		});
		chkAllType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllType.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbType.setEnabled(false);
					cmbType.setValue(null);
					cmbSemiFgLoad();
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
		cmbType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(cmbType.getValue()!=null){
					cmbSemiFgLoad();
				}
				else{
					cmbSemiFgName.removeAllItems();
				}
			}
		});

	}
	private void cmbSemiFgLoad() {
		cmbSemiFgName.removeAllItems();
		String type="%";
		if(cmbType.getValue()!=null){
			type=cmbType.getValue().toString();
		}
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select semiFgCode,semiFgName from tbSemiFgOpening where productionTypeId like '"+type+"'  and CONVERT(date,entryTime,105)>'2020-01-31'  order by semiFgName ";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbSemiFgName.addItem(element[0]);
				
				cmbSemiFgName.setItemCaption(element[0], element[1].toString());
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
			SemiFg=cmbSemiFgName.getValue().toString();

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
			//hm.put("productionStep",cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue()) );
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
			
			String semiFg="%",Productiontype="%";
			if(cmbSemiFgName.getValue()!=null){
				semiFg=cmbSemiFgName.getValue().toString();
			}
			if(cmbType.getValue()!=null){
				Productiontype=cmbType.getValue().toString();
			}
			query="select openingYear,productionTypeName,semiFgName,stdWeight,qty,rate,amount "+
				" from tbSemiFgOpening  where productionTypeId like '"+Productiontype+"' and semiFgCode like '"+semiFg+"' and CONVERT(date,entryTime,105)>'2020-01-31' "+
				" order by productionTypeName,semiFgName";
				
				
				
				hm.put("sql", query);
				List list=session.createSQLQuery(query).list();
				if(!list.isEmpty()){
					Window win = new ReportViewerNew(hm,"report/production/RptSemiFGOpeningStock.jasper",
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

		lblProductionType = new Label();
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
		mainLayout.addComponent( cmbType, "top:15.0px;left:140.0px;");
		
		chkAllType.setImmediate(true);
		chkAllType.setWidth("-1px");
		chkAllType.setHeight("-1px");
		mainLayout.addComponent(chkAllType, "top:15.0px;left:350.0px;");
		
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:43.0px;left:450.0px;");

		lblSemiFgName = new Label();
		lblSemiFgName.setImmediate(false);
		lblSemiFgName.setWidth("-1px");
		lblSemiFgName.setHeight("-1px");
		lblSemiFgName.setValue("Semi FG Name :");
		mainLayout.addComponent(lblSemiFgName, "top:41.0px;left:20.0px;");

		cmbSemiFgName= new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("300px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setNullSelectionAllowed(true);
		cmbSemiFgName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbSemiFgName, "top:41.0px;left:140.0px;");

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
		mainLayout.addComponent( toDate, "top:93.0px;left:140.0px;");
		
		lblReportType = new Label();
		lblReportType.setImmediate(false);
		lblReportType.setWidth("-1px");
		lblReportType.setHeight("-1px");
		lblReportType.setValue("Report Type: ");
		mainLayout.addComponent(lblReportType, "top:115.0px;left:20.0px;");
		
		opgValue=new OptionGroup("",OptionValue);
		opgValue.setImmediate(true);
		opgValue.setValue("With Value");
		opgValue.setStyleName("horizontal");
		mainLayout.addComponent(opgValue, "top:117.0px;left:140.0px;");*/

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
		mainLayout.addComponent(lblLine, "top:150.0px;left:25.0px;");

		previewButton.setWidth("110px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:175.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:175.opx; left:250.0px");

//		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
