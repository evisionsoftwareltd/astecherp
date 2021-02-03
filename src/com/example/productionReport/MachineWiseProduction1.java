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

public class MachineWiseProduction1 extends Window{

	SessionBean sessionBean;
	private Label lblMachineName=new Label("Machine Name : ");
	private ComboBox cmbmachineName=new ComboBox();
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

	public MachineWiseProduction1(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("MACHINE WISE PRODUCTION::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		machineDataLoad();
		setEventAction();
	}
	private void machineDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct  b.vMachineCode,b.vMachineName from tbMouldProductionDetails a "
					   +" inner join tbMachineInfo b on a.MachineName=b.vMachineCode ";
					   
					   
			
			List list=session.createSQLQuery(sql).list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbmachineName.addItem(element[0]);
				cmbmachineName.setItemCaption(element[0], element[1].toString());
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
				
					if(cmbmachineName.getValue()!=null ||  chkAll.booleanValue())
					{
						reportView(); 	
					}
					else
					{
					  showNotification("Please Select Machine Name",Notification.TYPE_WARNING_MESSAGE);	
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
					cmbmachineName.setEnabled(false);
					cmbmachineName.setValue(null);

				}
				else
				{
					cmbmachineName.setEnabled(true);
					cmbmachineName.focus();
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
		
			String machine="";
			
			if(cmbmachineName.getValue()!=null)
			{
				machine=cmbmachineName.getValue().toString();  	
			}
			
			else
				
			{
				machine="%"; 	
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
					+"where g.vMachineCode like '"+machine+"' and CONVERT (date,a.ProductionDate,105) between '"+ new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+ new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"' "
					+" order by b.mouldNo,a.productionType,c.StepId,g.vMachineCode,b.FinishedProduct ";

			hm.put("sql", query);
			
			subquery= " select c.vMachineCode,c.vMachineName,d.semiFgCode,d.semiFgName,ISNULL(SUM(b.ShiftAPcs),0)+ISNULL(SUM(b.ShiftBPcs),0) totalproduction,ISNULL(SUM(RejShiftA),0)+ISNULL(SUM(RejShiftB),0)totalreject,d.unit     from tbMouldProductionInfo a  "
					+ "inner join tbMouldProductionDetails b "
					+ " on a.ProductionNo=b.ProductionNo "
					+ " inner join  "
					+ " tbMachineInfo c on c.vMachineCode=b.MachineName "
					+ " inner join "
					+ " tbSemiFgInfo d on d.semiFgCode=b.FinishedProduct where c.vMachineCode like '"+machine+"' and CONVERT(date,a.ProductionDate,105) between '"+ new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"' and '"+ new SimpleDateFormat("yyyy-MM-dd").format(toDate.getValue())+"'  "
					+ " group by c.vMachineCode,c.vMachineName,d.semiFgCode,d.semiFgName,d.unit ";
			
			hm.put("subsql", subquery);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptmachineWiseproduction.jasper",
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
	private AbsoluteLayout buildMainLayout() 
	{

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("460px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		lblMachineName = new Label();
		lblMachineName.setImmediate(false);
		lblMachineName.setWidth("-1px");
		lblMachineName.setHeight("-1px");
		lblMachineName.setValue("Machine Name :");
		mainLayout.addComponent(lblMachineName, "top:16.0px;left:40.0px;");

		cmbmachineName = new ComboBox();
		cmbmachineName.setImmediate(true);
		cmbmachineName.setWidth("200px");
		cmbmachineName.setHeight("24px");
		cmbmachineName.setNullSelectionAllowed(false);
		cmbmachineName.setNewItemsAllowed(false);
		//cmbType.setEnabled(false);
		mainLayout.addComponent( cmbmachineName, "top:15.0px;left:140.0px;");

		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:41.0px;left:40.0px;");

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
		mainLayout.addComponent(lblToDate, "top:67.0px;left:40.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:67.0px;left:140.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:115.0px; left:140.0px");

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
		mainLayout.addComponent( chkAll, "top:15.0px;left:350.0px;");
		lblAll.setVisible(false);
		return mainLayout;


	}
}

