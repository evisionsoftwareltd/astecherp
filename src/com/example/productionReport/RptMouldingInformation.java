package com.example.productionReport;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.xerces.impl.dtd.models.CMBinOp;
import org.hibernate.Session;
import org.hibernate.Transaction;






import com.common.share.ReportPdf;
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
public class RptMouldingInformation extends Window{

	SessionBean sessionBean;
	private Label lblType=new Label("Type : ");
	private ComboBox cmbType=new ComboBox();
	private Label lblStep=new Label("Step : ");
	private ComboBox cmbStep=new ComboBox();
	private Label lblSupplier=new Label("Supplier : ");
	private ComboBox cmbSupplier=new ComboBox();
	private Label lblMould=new Label("Mould : ");
	private ComboBox cmbMould=new ComboBox();
	private Label lblStatus;
	
	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();
	
	private CheckBox chkAllType = new CheckBox();
	private CheckBox chkAllStep = new CheckBox();
	private CheckBox chkAllSupplier = new CheckBox();
	private CheckBox chkAllMould= new CheckBox();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate;
	private Label lblLine;
	private PopupDateField formDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private OptionGroup opgStatus;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Active","Inactive"});
	

	private AbsoluteLayout mainLayout;

	public RptMouldingInformation(SessionBean sessionBean){

		this.sessionBean=sessionBean;
		this.setCaption("MOULD INFORMATION::"+sessionBean.getCompany());
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
			String sql=  "select distinct  productTypeId,productTypeName from tbProductionType where productTypeId in (select productionType  from tbmouldInfo)" 
			             +"order by productTypeName ";
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbType.addItem(element[0]);
				cmbType.setItemCaption(element[0], element[1].toString());
					
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
				if(cmbType.getValue()!=null)
				{
					if(cmbStep.getValue()!=null)
					{
						if(cmbSupplier.getValue()!=null || chkAllSupplier.booleanValue())
						{
							if(cmbMould.getValue()!=null ||  chkAllMould.booleanValue())
							{
								reportView(); 	
							}
							else
							{
								showNotification("Please Select Mould Name",Notification.TYPE_WARNING_MESSAGE);	
							}
								
						}
						else
						{
							showNotification("Please Select Supplier Name",Notification.TYPE_WARNING_MESSAGE); 	
						}
						
					}
					else
					{
						showNotification("Please Select Production Step",Notification.TYPE_WARNING_MESSAGE); 
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
		chkAllSupplier.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllSupplier.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true && cmbType.getValue()!=null && cmbStep.getValue()!=null)
				{
					cmbSupplier.setEnabled(false);
					cmbSupplier.setValue(null);
					String supplier="%";
					mouldData(supplier);

				}
				else
				{
					cmbSupplier.setEnabled(true);
					cmbSupplier.focus();
					cmbMould.removeAllItems();
				}
			}
		});
		
		chkAllMould.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllMould.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true && cmbType.getValue()!=null && cmbStep.getValue()!=null)
				{
					cmbMould.setEnabled(false);
					cmbMould.setValue(null);
					

				}
				else
				{
					cmbMould.setEnabled(true);
					cmbMould.focus();
					
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
			
			
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbType.getValue()!=null)
				{
				  String Type=cmbType.getValue().toString();
				  productionStepLoad(Type);
				}
				
			}
		});
		

		cmbStep.addListener(new ValueChangeListener() 
		{
			
			
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStep.getValue()!=null && cmbType.getValue()!=null)
				{
				  
					supplierdata();
				}
				
			}
		});
		
		cmbSupplier.addListener(new ValueChangeListener() 
		{
			
			
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbType.getValue()!=null && cmbStep.getValue()!=null && cmbSupplier.getValue()!=null)
				{
				   String supplier=cmbSupplier.getValue().toString();
					//supplierdata();
					mouldData(supplier);
				}
				
			}
		});
		

	}
	private void productionStepLoad(String type) 
	{

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql= "select distinct  StepId,StepName from tbProductionStep where StepId in "
					    +"(select productionstep from tbmouldInfo where productionType like '"+type+"') "
			            +"order by StepName ";
			List list=session.createSQLQuery(sql).list();
			cmbStep.removeAllItems();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbStep.addItem(element[0]);
				cmbStep.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void supplierdata() 
	{

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct (select tbSupplierInfo.supplierId from tbSupplierInfo where tbSupplierInfo.supplierName=a.supplierId)supplierId ,a.supplierId suppliername from tbmouldInfo a where productionType like '"+cmbType.getValue().toString()+"' and productionstep like '"+cmbStep.getValue().toString()+"' and supplierId not like ''  ";
			//String sql="select distinct a.supplierId ,(select supplierName from tbSupplierInfo where supplierId  like  a.supplierId )suppliername from tbmouldInfo a where productionType like '"+cmbType.getValue().toString()+"' and productionstep like '"+cmbStep.getValue().toString()+"' and supplierId not like ''   ";
			List list=session.createSQLQuery(sql).list();
			cmbSupplier.removeAllItems();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				/*cmbSupplier.addItem(element[0]);
				cmbSupplier.setItemCaption(element[0], element[1].toString());*/
				
				cmbSupplier.addItem(element[1]);
				cmbSupplier.setItemCaption(element[1], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void mouldData(String Supplier) 
	{

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql=  "select distinct mouldid,mouldName from tbmouldInfo where productionType like '"+cmbType.getValue()+"' and productionstep like '"+cmbStep.getValue()+"' and supplierId like '"+Supplier+"' "
					    +" order by mouldName ";
					  
			List list=session.createSQLQuery(sql).list();
			cmbMould.removeAllItems();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbMould.addItem(element[0]);
				cmbMould.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void reportView()
	{
		String mouldName= "";
		String productiontype = "";
		String productionstep="";
		String supplier="";
		String status="Active";
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		
		if(cmbType.getValue()!=null)
		{
			productiontype=cmbType.getValue().toString();	
		}
		
		if(cmbStep.getValue()!=null)
		{
			productionstep=cmbStep.getValue().toString();	
		}

		if(chkAllSupplier.booleanValue())
		{
			supplier = "%";
		}
		else
		{
			supplier = cmbSupplier.getValue().toString();
		}

		if(chkAllMould.booleanValue())
		{
			mouldName ="%";
		}
		else
		{
			mouldName = cmbMould.getValue().toString();
		}
		if(opgStatus.getValue().toString().equalsIgnoreCase("Inactive")){
			status="Inactive";
		}
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("parentType", "MOULD INFORMATION");
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());

			String query = "select a.status, a.productionType,b.productTypeName,a.productionstep,c.StepName,a.supplierId,isnull(d.supplierName,'')supplierName,isnull(d.address,'')address,stdweight,cost,lifetime,cycletime,cavityNo,terget, mouldid,mouldName,ownership from tbmouldInfo  a inner join tbProductionType b on a.productionType=b.productTypeId " 
					       +"inner join tbProductionStep c on c.StepId=a.productionstep left join tbSupplierInfo d on d.supplierName=a.supplierId  "
					       +"where productionType like '"+productiontype+"' and productionstep like '"+productionstep+"' and a.supplierId like '"+supplier+"' and mouldid "
					      +"like '"+mouldName+"' and a.status ='"+status+"'  order by b.productTypeName,c.StepName,d.supplierName";

			hm.put("sql", query);
			
			List lst = session.createSQLQuery(query).list();
			
			if(!lst.isEmpty())
			{	
						int type=0;
						
						if(chkpdf.booleanValue())
						{
							type=1;
						}
						
						else
						{
							type=0;	
						}
						
			
				Window win = new ReportViewerNew(hm,"report/production/rptMouldInformation.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				
				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);	
			}
			else
			{
				showNotification("There Is No Data",Notification.TYPE_WARNING_MESSAGE);	
			}
			
		}
		catch(Exception exp)
		{
			showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private AbsoluteLayout buildMainLayout() {

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("450px");
		mainLayout.setHeight("230px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("560px");
		setHeight("310px");

		lblType = new Label();
		lblType.setImmediate(false);
		lblType.setWidth("-1px");
		lblType.setHeight("-1px");
		lblType.setValue("Production Type :");
		mainLayout.addComponent(lblType, "top:16.0px;left:40.0px;");

		cmbType = new ComboBox();
		cmbType.setImmediate(true);
		cmbType.setWidth("200px");
		cmbType.setHeight("24px");
		cmbType.setNullSelectionAllowed(false);
		cmbType.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbType, "top:15.0px;left:140.0px;");
		
		chkAllType = new CheckBox("All");
		chkAllType.setImmediate(true);
		chkAllType.setWidth("-1px");
		chkAllType.setHeight("24px");
		//mainLayout.addComponent(chkAllType , "top:16.0px;left:340.0px;");

		lblStep = new Label();
		lblStep.setImmediate(false);
		lblStep.setWidth("-1px");
		lblStep.setHeight("-1px");
		lblStep.setValue("Production Step :");
		mainLayout.addComponent(lblStep, "top:41.0px;left:40.0px;");

		cmbStep= new ComboBox();
		cmbStep.setImmediate(true);
		cmbStep.setWidth("200px");
		cmbStep.setHeight("24px");
		cmbStep.setNullSelectionAllowed(true);
		cmbStep.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbStep, "top:40.0px;left:140.0px;");
		
		chkAllStep = new CheckBox("All");
		chkAllStep.setImmediate(true);
		chkAllStep.setValue(false);
		chkAllStep.setWidth("-1px");
		chkAllStep.setHeight("24px");
		//mainLayout.addComponent(chkAllStep, "top:41.0px;left:340.0px;");
		
		
		lblSupplier = new Label();
		lblSupplier.setImmediate(false);
		lblSupplier.setWidth("-1px");
		lblSupplier.setHeight("-1px");
		lblSupplier.setValue("Supplier :");
		mainLayout.addComponent(lblSupplier, "top:67.0px;left:40.0px;");

		cmbSupplier= new ComboBox();
		cmbSupplier.setImmediate(false);
		cmbSupplier.setWidth("280px");
		cmbSupplier.setHeight("24px");
		cmbSupplier.setNullSelectionAllowed(true);
		cmbSupplier.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbSupplier, "top:66.0px;left:140.0px;");
		
		chkAllSupplier = new CheckBox("All");
		chkAllSupplier.setImmediate(true);
		chkAllSupplier.setValue(false);
		chkAllSupplier.setWidth("-1px");
		chkAllSupplier.setHeight("24px");
		mainLayout.addComponent(chkAllSupplier, "top:67.0px;left:420.0px;");
		
		lblMould = new Label();
		lblMould.setImmediate(false);
		lblMould.setWidth("-1px");
		lblMould.setHeight("-1px");
		lblMould.setValue("Mould Name:");
		mainLayout.addComponent(lblMould, "top:93.0px;left:40.0px;");

		cmbMould= new ComboBox();
		cmbMould.setImmediate(true);
		cmbMould.setWidth("280px");
		cmbMould.setHeight("24px");
		cmbMould.setNullSelectionAllowed(true);
		cmbMould.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbMould, "top:92.0px;left:140.0px;");
		
		chkAllMould= new CheckBox("All");
		chkAllMould.setImmediate(true);
		chkAllMould.setValue(false);
		chkAllMould.setWidth("-1px");
		chkAllMould.setHeight("24px");
		mainLayout.addComponent(chkAllMould, "top:93.0px;left:420.0px;");
		
		/*lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("Date: ");
		mainLayout.addComponent(lblFDate, "top:67.0px;left:40.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:67.0px;left:140.0px;");
*/
		/*		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		mainLayout.addComponent(lblToDate, "top:91.0px;left:62.0px;");

		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		mainLayout.addComponent( toDate, "top:93.0px;left:130.0px;");*/
		lblStatus = new Label();
		lblStatus.setImmediate(false);
		lblStatus.setWidth("-1px");
		lblStatus.setHeight("-1px");
		lblStatus.setValue("Status: ");
		mainLayout.addComponent(lblStatus, "top:120.0px;left:40.0px;");
	
		opgStatus=new OptionGroup("",Optiontype);
		opgStatus.setImmediate(true);
		opgStatus.setValue("Active");
		opgStatus.setStyleName("horizontal");
		mainLayout.addComponent(opgStatus, "top:120.0px;left:170.0px;");
		
		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:145.0px; left:180.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>==============================================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:170.0px;left:0.0px;");

		previewButton.setWidth("100px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:195.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:195.opx; left:225.0px");

		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
