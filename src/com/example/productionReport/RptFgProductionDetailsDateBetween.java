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
public class RptFgProductionDetailsDateBetween extends Window{

	SessionBean sessionBean;
	private Label lblFgName;
	private ComboBox cmbType=new ComboBox();
	private Label lblProductionType;
	private ComboBox cmbFgName=new ComboBox();

	private CheckBox chkAll=new CheckBox("All");
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate,lblToDate;
	private Label lblProductionTypeDate;
	private Label lblLine;
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;

	public RptFgProductionDetailsDateBetween(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("FG PRODUCTION DETAILS DATE BETWEEN::"+sessionBean.getCompany());
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
			String sql="select distinct productionType,(select productTypeName from tbProductionType " +
					"where productTypeId=productionType)typename from tbMouldProductionInfo";
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
				if(cmbType.getValue()!=null )
				{
					if(cmbFgName.getValue()!=null|| chkAll.booleanValue()==true)
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
		cmbType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(cmbType.getValue()!=null){
					cmbSemiFgLoad();
				}
				else{
					cmbFgName.removeAllItems();
				}
			}
		});

	}
	private void cmbSemiFgLoad() 
	{
		cmbFgName.removeAllItems();
		
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct FinishedProduct,(select semiFgName from tbSemiFgInfo "+
					"where semiFgCode=FinishedProduct)ProductName  "+
					"from tbMouldProductionInfo a inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo "+
					"where b.finFlag=1 and b.subSubStepId='null'  and a.productionType like '"+cmbType.getValue()+"' "+
					"union "+
					"select distinct subSubStepId,(select semiFgSubName from tbSemiFgSubInformation "+ 
					"where semiFgSubId=subSubStepId)ProductName  "+
					"from tbMouldProductionInfo a inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo "+
					"where finFlag=1 and subSubStepId!='null' and a.productionType like '"+cmbType.getValue()+"'";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
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
			SemiFg=cmbFgName.getValue().toString();

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
			hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("fromDate", new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()));
			hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			//hm.put("productionStep",cmbFgName.getItemCaption(cmbFgName.getValue()) );
			//hm.put("user", sessionBean.getUserName());



			query="select * from funcFgProductionDateBetween" +
					"('"+SemiFg+"','"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"') order by productionDate,semiFgName";
			System.out.println(query);


			hm.put("sql", query);
			String subQuery="select a.semiFgName,a.unit,a.color,sum(a.totalProduction)totalProduciton,sum(a.totalRejection)totalRejection from( "+
						" select * from funcFgProductionDateBetween('"+SemiFg+"','"+datef.format(formDate.getValue())+"','"+datef.format(toDate.getValue())+"') "+
						" )a group by a.semiFgName,a.unit,a.color ";
			hm.put("subQuery", subQuery);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/FgProductionDateBetween.jasper",
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
		
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:43.0px;left:450.0px;");

		lblFgName = new Label();
		lblFgName.setImmediate(false);
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");
		lblFgName.setValue("FG Name :");
		mainLayout.addComponent(lblFgName, "top:41.0px;left:20.0px;");

		cmbFgName= new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setWidth("300px");
		cmbFgName.setHeight("24px");
		cmbFgName.setNullSelectionAllowed(true);
		cmbFgName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbFgName, "top:41.0px;left:140.0px;");

		lblFDate = new Label();
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

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:125.0px; left:140.0px");

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
