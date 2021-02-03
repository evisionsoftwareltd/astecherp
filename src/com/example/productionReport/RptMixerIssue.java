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

public class RptMixerIssue extends Window
{
	SessionBean sessionBean;
	private Label lblSemiFgName, lblMachineName,lblmouldName;
	private ComboBox cmbType=new ComboBox();
	private Label lblProductionType;
	private ComboBox cmbSemiFgName=new ComboBox();
	private ComboBox cmbMachineName=new ComboBox();
	private ComboBox cmbmouldName=new ComboBox();

	private CheckBox chkAll=new CheckBox("All");
	private CheckBox chkAllmachine=new CheckBox("All");
	private CheckBox chkAllMould=new CheckBox("All");
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

	public RptMixerIssue(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("Mixer Issue ::"+sessionBean.getCompany());
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
			
			
			String sql=	
					"select distinct productionTypeId,productionTypeName from tbMixtureIssueEntryInfo "
					+"order by productionTypeName ";
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
						if(cmbmouldName.getValue()!=null || chkAllMould.booleanValue())
						{
							if(cmbMachineName.getValue()!=null || chkAllmachine.booleanValue()==true)
							{
								reportView();	
							}
							else
							{
								showNotification("Please Select Machine Name",Notification.TYPE_WARNING_MESSAGE); 	
							}	
						}
						
						else
						{
							showNotification("Please Select Mould Name",Notification.TYPE_WARNING_MESSAGE); 	
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
				if(bv==true && cmbType.getValue()!=null)
				{
					cmbSemiFgName.setEnabled(false);
					cmbSemiFgName.setValue(null);
					Mouldata("%");

				}
				else{
					cmbSemiFgName.setEnabled(true);
					cmbSemiFgName.focus();
					cmbmouldName.removeAllItems();
				}
			}
		});
		
		
		
		/*chkAllFg.addListener(new ValueChangeListener() 
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
		});*/

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
		
		cmbmouldName.addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event) 
			{
			
				if(cmbmouldName.getValue()!=null && cmbType.getValue()!=null && (cmbSemiFgName.getValue()!=null || chkAll.booleanValue()))
				{
					//cmbSemiFgLoad();
					
					String mould=cmbmouldName.getValue().toString();
					machineDataload(mould);
					
				}
				else
				{
					cmbMachineName.removeAllItems();
				}
			}
		});
		
		chkAllMould.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllMould.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true && cmbType.getValue()!=null && (cmbSemiFgName.getValue()!=null || chkAll.booleanValue()))
				{
					cmbmouldName.setEnabled(false);
					cmbmouldName.setValue(null);
					machineDataload("%");

				}
				else{
					cmbmouldName.setEnabled(true);
					cmbmouldName.focus();
					cmbMachineName.removeAllItems();
				}
			}
		});
		
		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllMould.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true && cmbType.getValue()!=null && (cmbSemiFgName.getValue()!=null || chkAll.booleanValue()))
				{
					cmbmouldName.setEnabled(false);
					cmbmouldName.setValue(null);
					machineDataload("%");

				}
				else{
					cmbmouldName.setEnabled(true);
					cmbmouldName.focus();
					cmbMachineName.removeAllItems();
				}
			}
		});
		
		chkAllmachine.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllmachine.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true)
				{
					cmbMachineName.setEnabled(false);
					cmbMachineName.setValue(null);
					

				}
				else
				{
					cmbMachineName.setEnabled(true);
					cmbMachineName.focus();
					
				}
			}
		});
		
		
		cmbType.addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event) 
			{
			
				if(cmbType.getValue()!=null)
				{
					semiFgDataLoad();
						
				}
				else
				{
					cmbSemiFgName.removeAllItems();
				}
			}
		});
		
		
		
		cmbSemiFgName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(cmbType.getValue()!=null && cmbSemiFgName.getValue()!=null)
				{
					String semifg=cmbSemiFgName.getValue().toString();
					Mouldata(semifg);
				}
				else{
					cmbmouldName.removeAllItems();
				}
			}
		});

	}
	private void Mouldata(String semifg) 
	{
		cmbmouldName.removeAllItems();
		String type=cmbType.getValue().toString();
		try
		{
			
			String sql= "select distinct b.mouldId,b.mouldName from tbMixtureIssueEntryInfo a inner join "
					    +"tbMixtureIssueEntryDetails b on a.issueNo=b.issueNo "
					    +"where a.productionTypeId like '"+type+"' and b.semiFgId like '"+semifg+"' order by b.mouldName ";
			Iterator<?>iter=dbService(sql);
			
			while(iter.hasNext()){
				Object element[]=(Object[]) iter.next();
				cmbmouldName.addItem(element[0]);
				cmbmouldName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		
		
	}
	
	private void machineDataload(String mould) 
	{
		cmbMachineName.removeAllItems();
		String type="";
		String semifg="";
		try
		{
			type=cmbType.getValue().toString();
			if(cmbSemiFgName.getValue()!=null)
			{
				semifg=	cmbSemiFgName.getValue().toString();
			}
			else
			{
				semifg="%";	
			}
		
			String sql="select distinct b.machineId,b.machineName from tbMixtureIssueEntryInfo a inner join "
					  +"tbMixtureIssueEntryDetails b on a.issueNo=b.issueNo "
					  +"where a.productionTypeId like '"+type+"' and b.semiFgId like '"+semifg+"' and b.mouldId like '"+mould+"' order by b.machineName ";
			
					Iterator<?>iter=dbService(sql);
			while(iter.hasNext())
			{
				Object element[]=(Object[]) iter.next();
				cmbMachineName.addItem(element[0]);
				cmbMachineName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void semiFgDataLoad() 
	{
		cmbSemiFgName.removeAllItems();
		try
		{
		
			String sql="select distinct b.semiFgId,b.semiFgName from tbMixtureIssueEntryInfo a inner join "
					   +"tbMixtureIssueEntryDetails b on a.issueNo=b.issueNo "
					   + "where a.productionTypeId like '"+cmbType.getValue().toString()+"' order by semiFgName ";
			
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
		String mould="";
		String machine="";
		
		productionType=cmbType.getValue().toString();
		if(cmbSemiFgName.getValue()!=null)
		{
			semifg=cmbSemiFgName.getValue().toString();
		}
		else
		{
			semifg="%";	
		}
		
		if(cmbmouldName.getValue()!=null)
		{
			mould=cmbmouldName.getValue().toString();
		}
		else
		{
			mould="%";	
		}
		
		if(cmbMachineName.getValue()!=null)
		{
			machine=cmbMachineName.getValue().toString();
		}
		else
		{
			machine="%";	
		}
		
		

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("toDate", new SimpleDateFormat("dd-MM-yy").format(toDate.getValue()));
			hm.put("fromDate", new SimpleDateFormat("dd-MM-yy").format(formDate.getValue()));
			hm.put("type",cmbType.getItemCaption(cmbType.getValue()) );


			 query=  "select a.issueDate ,a.productionTypeName,b.semiFgName,b.semiFgUnit,b.mouldName,b.machineName,issueQty,issueBag from tbMixtureIssueEntryInfo a "
					 +"inner join tbMixtureIssueEntryDetailsEntry b on a.issueNo=b.issueNo "
					 +"where a.productionTypeId like '"+productionType+"' and b.semiFgId like '"+semifg+"' and b.mouldId like '"+mould+"' and machineId like '"+machine+"' "
					 +"and convert(date,a.issueDate,105)  between '"+datef.format(formDate.getValue())+"' and '"+datef.format(toDate.getValue())+"' order by productionTypeName,semiFgName,mouldName,machineName ";

			
			 
			 System.out.println(query);
			
			hm.put("sql", query);
			
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/RptMixtureIssueEntry.jasper",
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
		setHeight("300px");

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
		
		lblSemiFgName = new Label();
		lblSemiFgName.setImmediate(false);
		lblSemiFgName.setWidth("-1px");
		lblSemiFgName.setHeight("-1px");
		lblSemiFgName.setValue("Semi FG Name :");
		mainLayout.addComponent(lblSemiFgName, "top:43.0px;left:20.0px;");

		cmbSemiFgName= new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("300px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setNullSelectionAllowed(true);
		cmbSemiFgName.setNewItemsAllowed(false);
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbSemiFgName, "top:41.0px;left:140.0px;");
		
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:41.0px;left:450.0px;");
		
		lblmouldName = new Label();
		lblmouldName.setImmediate(false);
		lblmouldName.setWidth("-1px");
		lblmouldName.setHeight("-1px");
		lblmouldName.setValue("Mould Name :");
		mainLayout.addComponent(lblmouldName, "top:68.0px;left:20.0px;");		

		cmbmouldName = new ComboBox();
		cmbmouldName.setImmediate(true);
		cmbmouldName.setWidth("300px");
		cmbmouldName.setHeight("24px");
		cmbmouldName.setNullSelectionAllowed(false);
		cmbmouldName.setNewItemsAllowed(false);
		cmbmouldName.setEnabled(true);
		cmbmouldName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbmouldName, "top:66.0px;left:140.0px;");
		
		chkAllMould.setImmediate(true);
		chkAllMould.setWidth("-1px");
		chkAllMould.setHeight("-1px");
		mainLayout.addComponent(chkAllMould, "top:66.0px;left:450.0px;");
		
		
	
		
		lblMachineName = new Label();
		lblMachineName.setImmediate(false);
		lblMachineName.setWidth("-1px");
		lblMachineName.setHeight("-1px");
		lblMachineName.setValue("Machine Name :");
		mainLayout.addComponent(lblMachineName, "top:93.0px;left:20.0px;");

		cmbMachineName= new ComboBox();
		cmbMachineName.setImmediate(true);
		cmbMachineName.setWidth("300px");
		cmbMachineName.setHeight("24px");
		cmbMachineName.setNullSelectionAllowed(true);
		cmbMachineName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbMachineName, "top:91.0px;left:140.0px;");
		cmbMachineName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		
		chkAllmachine.setImmediate(true);
		chkAllmachine.setWidth("-1px");
		chkAllmachine.setHeight("-1px");
		mainLayout.addComponent(chkAllmachine, "top:91.0px;left:450.0px;");


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

		previewButton.setWidth("100px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:210.opx; left:160.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:210.opx; left:255.0px");

//		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
