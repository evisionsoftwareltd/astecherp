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

public class RptFgStockDateBetween extends Window
{
	SessionBean sessionBean;
	private Label lblSemiFgName, lblFgName,lblstep;
	private ComboBox cmbType=new ComboBox();
	private Label lblProductionType;
	private ComboBox cmbSemiFgName=new ComboBox();
	private ComboBox cmbFgName=new ComboBox();
	private ComboBox cmbstep=new ComboBox();

	private CheckBox chkAll=new CheckBox("All");
	private CheckBox chkAllFg=new CheckBox("All");
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
	private SimpleDateFormat datefNew = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


	private AbsoluteLayout mainLayout;

	public RptFgStockDateBetween(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("FINISHED GOODS (Labeling/Printing) STOCK DATE BETWEEN::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		productionTypeDataLoad();
		setContent(mainLayout);
		setEventAction();
	}
	
	@SuppressWarnings("unused")
	private Iterator dbService(String sql)
	{
		Transaction tx=null;
		Session session=null;
		Iterator iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			iter= session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			if(tx!=null||session!=null)
			{
				tx.commit();
				session.close();
			}
		}
		return iter;
	}
	
	private void productionTypeDataLoad()
	{
		try
		{
			
			
			String sql=	"select distinct  a.productionType,(select productTypeName from tbProductionType where productTypeId=a.productionType) "
					    +"productypeName from tbMouldProductionInfo a "
					    +"inner join "
					    +"tbMouldProductionDetails b "
					    +"on a.ProductionNo=b.ProductionNo "
					    +"where a.Stepid not in ('BlowSTP-1','InjectionSTP-1','Injection Blow STP-1') ";
			Iterator<?>iter=dbService(sql);

			int i=0;
			while(iter.hasNext())
			{
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
					if(cmbSemiFgName.getValue()!=null|| chkAll.booleanValue()==true)
					{
						if(cmbFgName.getValue()!=null || chkAllFg.booleanValue()==true)
						{
							reportView();	
						}
						else
						{
							showNotification("Please Select Fg Name",Notification.TYPE_WARNING_MESSAGE); 	
						}
						 
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
				if(bv==true)
				{
					cmbSemiFgName.setEnabled(false);
					cmbSemiFgName.setValue(null);
					cmbFgLoad("%");

				}
				else{
					cmbSemiFgName.setEnabled(true);
					cmbSemiFgName.focus();
					cmbFgName.removeAllItems();
				}
			}
		});
		
		
		
		chkAllFg.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllFg.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true)
				{
					cmbFgName.setEnabled(false);
					cmbFgName.setValue(null);
					
				}
				else
				{
					cmbFgName.setEnabled(true);
					
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
		
		cmbstep.addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event) 
			{
			
				if(cmbstep.getValue()!=null)
				{
					cmbSemiFgLoad();
				}
				else
				{
					cmbSemiFgName.removeAllItems();
				}
			}
		});
		
		
		cmbType.addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event) 
			{
			
				if(cmbType.getValue()!=null)
				{
					cmbStepLoad();
				}
				else
				{
					cmbSemiFgName.removeAllItems();
				}
			}
		});
		
		
		
		cmbSemiFgName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(cmbSemiFgName.getValue()!=null)
				{
					String semifg=cmbSemiFgName.getValue().toString();
					cmbFgLoad(semifg);
				}
				else{
					cmbFgName.removeAllItems();
				}
			}
		});

	}
	private void cmbFgLoad(String semifg) 
	{
		cmbFgName.removeAllItems();
		try
		{
			
			String sql= "select distinct b.subSubStepId,(select semiFgSubName from tbSemiFgSubInformation where semiFgSubId= b.subSubStepId) "
					    +"semifgName from tbMouldProductionInfo a inner join tbMouldProductionDetails b "
					    +"on a.ProductionNo=b.ProductionNo "
					    +"where a.Stepid not in ('BlowSTP-1','InjectionSTP-1','Injection Blow STP-1') and a.productionType like '"+cmbType.getValue().toString()+"' "
					    +"and FinishedProduct like '"+semifg+"' and a.Stepid like '"+cmbstep.getValue().toString()+"' ";
			
			Iterator<?>iter=dbService(sql);
			
			while(iter.hasNext()){
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
	
	private void cmbSemiFgLoad() 
	{
		cmbSemiFgName.removeAllItems();
		try
		{
		
			String sql="select distinct b.FinishedProduct,c.semiFgName "
					   +" from tbMouldProductionInfo a inner join tbMouldProductionDetails b "
					   +"on a.ProductionNo=b.ProductionNo  inner join tbSemiFgInfo c on b.FinishedProduct=c.semiFgCode "
					   +"where a.Stepid not in ('BlowSTP-1','InjectionSTP-1','Injection Blow STP-1') and a.productionType like '"+cmbType.getValue().toString()+"' and a.Stepid ='"+cmbstep.getValue().toString()+"'  " ;
			
					Iterator<?>iter=dbService(sql);
			while(iter.hasNext())
			{
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
	
	private void cmbStepLoad() 
	{
		cmbstep.removeAllItems();
		try
		{
		
			String sql="select distinct Stepid,(select StepName from tbProductionStep where StepId=a.Stepid) stepName "
					   +"from tbMouldProductionInfo a where productionType='"+cmbType.getValue().toString()+"' "
					  + "and Stepid not in ('BlowSTP-1','InjectionSTP-1','Injection Blow STP-1') " ;
			
					Iterator<?>iter=dbService(sql);
			while(iter.hasNext())
			{
				Object element[]=(Object[]) iter.next();
				cmbstep.addItem(element[0]);
				cmbstep.setItemCaption(element[0], element[1].toString());
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
		
		String productionType="";
		String semifg="";
		String fg="";
		String Stepid="";
		
		productionType=cmbType.getValue().toString();
		Stepid=cmbstep.getValue().toString();
		
		if(cmbSemiFgName.getValue()!=null)
		{
			semifg=cmbSemiFgName.getValue().toString();	
		}
		else
		{
			semifg="%";	
		}
		
		if(cmbFgName.getValue()!=null)
		{
		   fg=cmbFgName.getValue().toString();	
		}
		else
		{
		  fg="%";	
		}
		
		
		

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("fromDate", new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()));
			hm.put("type",cmbType.getItemCaption(cmbType.getValue()) );


			 query= "select * from funcLabeling_PrintingStockDateBetween('"+productionType+"' ,'"+Stepid+"','"+semifg+"','"+fg+"','"+datefNew.format(formDate.getValue())+"','"+datefNew.format(toDate.getValue())+"')";
			System.out.println(query);
			
			hm.put("sql", query);
			
			String subQuery= "select semiFgId, semiFgName,semiFgUnit,SUM(semiFgRcvQty)ReceiveQty,0 production ,0 reject ,0 balance,1 flag from funcLabeling_PrintingStockDateBetween "
							+"('"+productionType+"' ,'"+Stepid+"','"+semifg+"','"+fg+"','"+datefNew.format(formDate.getValue())+"','"+datefNew.format(toDate.getValue())+"')  where semiFgName not like '' and semiFgName is not null "
							+"group by semiFgName,semiFgUnit,semiFgId "
							+"union "
							+"select semiFgId, semiFgSubName,semiFgSubUnit,0 ReceiveQty,SUM(fgProduction)production,SUM(fgReject)reject,0 balance,2 flag from funcLabeling_PrintingStockDateBetween "
							+"('"+productionType+"' ,'"+Stepid+"','"+semifg+"','"+fg+"','"+datefNew.format(formDate.getValue())+"','"+datefNew.format(toDate.getValue())+"') where semiFgSubName not like '' and semiFgSubName is not null "
							+"group by semiFgSubName,semiFgSubUnit,semiFgId order by semiFgId ";
			
			
			hm.put("subsql", subQuery);
			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/RptFgStockDateBetween.jasper",
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
		//mainLayout.setWidth("520px");
		//mainLayout.setHeight("270px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("550px");
		setHeight("320px");

		lblProductionType = new Label();
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		lblProductionType.setValue("Production Type :");
		mainLayout.addComponent(lblProductionType, "top:18.0px;left:20.0px;");		

		cmbType = new ComboBox();
		cmbType.setImmediate(true);
		cmbType.setWidth("200px");
		cmbType.setHeight("24px");
		cmbType.setNullSelectionAllowed(false);
		cmbType.setNewItemsAllowed(false);
		cmbType.setEnabled(true);
		cmbType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbType, "top:16.0px;left:140.0px;");
		
		lblstep = new Label();
		lblstep.setImmediate(false);
		lblstep.setWidth("-1px");
		lblstep.setHeight("-1px");
		lblstep.setValue("Production Step :");
		mainLayout.addComponent(lblstep, "top:43.0px;left:20.0px;");		

		cmbstep = new ComboBox();
		cmbstep.setImmediate(true);
		cmbstep.setWidth("200px");
		cmbstep.setHeight("24px");
		cmbstep.setNullSelectionAllowed(false);
		cmbstep.setNewItemsAllowed(false);
		cmbstep.setEnabled(true);
		cmbstep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbstep, "top:41.0px;left:140.0px;");
		
		
		lblSemiFgName = new Label();
		lblSemiFgName.setImmediate(false);
		lblSemiFgName.setWidth("-1px");
		lblSemiFgName.setHeight("-1px");
		lblSemiFgName.setValue("Semi FG Name :");
		mainLayout.addComponent(lblSemiFgName, "top:68.0px;left:20.0px;");

		cmbSemiFgName= new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("300px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setNullSelectionAllowed(true);
		cmbSemiFgName.setNewItemsAllowed(false);
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbSemiFgName, "top:66.0px;left:140.0px;");
		
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:66.0px;left:450.0px;");
		
		lblFgName = new Label();
		lblFgName.setImmediate(false);
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");
		lblFgName.setValue("FG Name :");
		mainLayout.addComponent(lblFgName, "top:93.0px;left:20.0px;");

		cmbFgName= new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setWidth("300px");
		cmbFgName.setHeight("24px");
		cmbFgName.setNullSelectionAllowed(true);
		cmbFgName.setNewItemsAllowed(false);
		cmbFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbFgName, "top:91.0px;left:140.0px;");
		
		chkAllFg.setImmediate(true);
		chkAllFg.setWidth("-1px");
		chkAllFg.setHeight("-1px");
		mainLayout.addComponent(chkAllFg, "top:91.0px;left:450.0px;");


		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		mainLayout.addComponent(lblFDate, "top:118.0px;left:20.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:116.0px;left:140.0px;");

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:143.0px;left:20.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:141.0px;left:140.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:165.0px; left:210.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:190.0px;left:0.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:210.opx; left:175.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:210.opx; left:255.0px");

//		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
