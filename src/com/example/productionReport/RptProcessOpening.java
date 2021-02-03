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
@SuppressWarnings("serial")
public class RptProcessOpening extends Window{

	SessionBean sessionBean;

	private Label lblOpeningYear = new Label("Year : ");
	@SuppressWarnings("unused")
	private ComboBox cmbOpeningYear=new ComboBox();
	private Label lblType=new Label("Type : ");
	private ComboBox cmbType=new ComboBox();
	private Label lblStep=new Label("Step : ");
	private ComboBox cmbStep=new ComboBox();
	private Label lblJorder=new Label("JOrder : ");
	private ComboBox cmbJorder=new ComboBox();
	private Label lblMould=new Label("Mould : ");
	private ComboBox cmbMould=new ComboBox();
	private Label lblFg=new Label("FG : ");
	private ComboBox cmbFg=new ComboBox();
	private Label lblSemiFG=new Label("SEMIFG : ");;
	private ComboBox cmbSemiFG=new ComboBox();

	private InlineDateField dOpeningYear= new InlineDateField();


	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();

	//private CheckBox chkAllType = new CheckBox();
	private CheckBox chkAllStep = new CheckBox();
	private CheckBox chkAllJOrder = new CheckBox();
	private CheckBox chkAllMould = new CheckBox();
	private CheckBox chkAllFg= new CheckBox();
	private CheckBox chkAllSemiFG = new CheckBox();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblLine;
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	
	private AbsoluteLayout mainLayout;

	private SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");
	
	public RptProcessOpening(SessionBean sessionBean){

		this.sessionBean=sessionBean;
		this.setCaption("PROCESS OPENNING STOCK::"+sessionBean.getCompany());
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

		previewButton.addListener(new ClickListener() 
		{

			public void buttonClick(ClickEvent event)
			{
				/*if(cmbType.getValue()!=null)
				{
					if(cmbMould.getValue()!=null || chkAllMould.booleanValue()
					{
						if(cmbFg.getValue()!=null ||  chkAllFg.booleanValue())
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
						
					}
					
				}
				else
				{
					showNotification("Please Select Production Step",Notification.TYPE_WARNING_MESSAGE);
				}*/
				
				if(cmbType.getValue()!=null)
				{
					if(cmbStep.getValue()!=null || chkAllStep.booleanValue())
					{
						if(cmbJorder.getValue()!=null || chkAllJOrder.booleanValue())
						{
							reportView();		
						}
						else
						{
							showNotification("Please Select Job Order No",Notification.TYPE_WARNING_MESSAGE);		
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


		chkAllJOrder.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllJOrder.booleanValue())
				{
					cmbJorder.setEnabled(false);
					cmbJorder.setValue(null);					
				}
				else
				{
					cmbJorder.setEnabled(true);
					
				}
			}
		});


		/*chkAllMould.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				if(chkAllMould.booleanValue())
				{
					cmbMould.setEnabled(false);
					cmbMould.setValue(null);
					String supplier="%";
					//mouldData(supplier);

				}
				else
				{
					cmbMould.setEnabled(true);
					cmbMould.focus();
					//cmbMould.removeAllItems();
				}
			}
		});*/


		chkAllJOrder.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				
				if(chkAllJOrder.booleanValue())
				{
					cmbJorder.setEnabled(false);
					cmbJorder.setValue(null);
					String JOrder="%";
					//productData(JOrder);

				}
				else
				{
					cmbJorder.setEnabled(true);
					cmbJorder.focus();
					//cmbJoeder.removeAllItems();
				}
			}
		});


		chkAllStep.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				if(chkAllStep.booleanValue())
				{
					cmbStep.setEnabled(false);
					cmbStep.setValue(null);
					String step="%";
					cmbJOrderData(step);

				}
				else
				{
					cmbStep.setEnabled(true);
					cmbStep.focus();
					cmbJorder.removeAllItems();
					//cmbJoeder.removeAllItems();
				}
			}
		});
		
		

		/*cmbSemiFG.addListener(new ValueChangeListener() 
		{


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbType.getValue()!=null && cmbStep.getValue()!=null && cmbJorder.getValue()!=null && cmbMould.getValue()!=null && cmbFg.getValue()!=null)
				{
					String SemiFG=cmbSemiFG.getValue().toString();
					//cmbJorderdata();
					//productData();
				}
			}

		});*/
		/*cmbFg.addListener(new ValueChangeListener() 
		{


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbType.getValue()!=null && cmbStep.getValue()!=null && cmbJorder.getValue()!=null )
				{
					String Fg=cmbFg.getValue().toString();
					semiFgData(Fg);
				}
			}

		});*/

	/*	cmbMould.addListener(new ValueChangeListener() 
		{


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbType.getValue()!=null && cmbStep.getValue()!=null && cmbJorder.getValue()!=null )
				{
					String mould=cmbMould.getValue().toString();
					String productiontype=cmbType.getValue().toString();
					String step="";
					String joborder="";
					
					if(cmbStep.getValue()!=null)	
					{
						step=cmbStep.getValue().toString();	
					}
					
					if(chkAllStep.booleanValue())
					{
						step="%";	
					}
					
					if(cmbJorder.getValue()!=null)
					{
						joborder=cmbJorder.getValue().toString();	
					}
					
					if(chkAllJOrder.booleanValue())
					{
						joborder="%";
					}
						
					fgData(productiontype,step,joborder,mould);
					String type="";
					
					
				}
			}

		});*/

		/*cmbJorder.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event)
			{
				if(cmbType.getValue()!=null && cmbStep.getValue()!=null && cmbJorder.getValue()!=null)
				{
					String moulsetp="";
					String type="";
					String joborder="";
					
					if(cmbType.getValue()!=null)
					{
						type=cmbType.getValue().toString();	
					}
					
					if(cmbJorder.getValue()!=null)
					{
						joborder=cmbJorder.getValue().toString();	
					}
					
					if(cmbStep.getValue()!=null)
					{
						moulsetp=cmbStep.getValue().toString();
					}
					if(chkAllStep.booleanValue())
					{
						moulsetp="%";	
					}
				
					mouldData(type,moulsetp,joborder);
				}
			}

		});*/

		cmbStep.addListener(new ValueChangeListener() 
		{


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbStep.getValue()!=null && cmbType.getValue()!=null)
				{
					String step=cmbStep.getValue().toString();
					cmbJOrderData(step);
				}

			}
		});

		cmbType.addListener(new ValueChangeListener() 
		{


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbType.getValue()!=null)
				{
					String Type=cmbType.getValue().toString();
					productionStepLoad(Type);
				}

			}
		});
		
		/*
		cmbFg.addListener(new ValueChangeListener() 
		{


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbFg!=null )
				{
					String Type=cmbType.getValue().toString();
					productionStepLoad(Type);
				}

			}
		});*/

	}

	protected void semiFgData(String SemiFG)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Fg="";

			if(cmbFg.getValue()!=null)
			{
				Fg=cmbFg.getValue().toString();  
			}
			if(chkAllFg.booleanValue())
			{
				Fg="%";	
			}

			String sql= " select semiFgCode,semiFgName from  tbSemiFgInfo where productionTypeId like '"+SemiFG+"' and partyCode like '%' "
					+"order by semiFgName ";
			cmbSemiFG.removeAllItems();
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbSemiFG.addItem(element[0].toString());
				cmbSemiFG.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}

	}

	
	protected void fgData(String type,String step,String joborder,String mouldId)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String mould="";
			if(cmbMould.getValue()!=null)
			{
				mould=cmbMould.getValue().toString();
			}
			else
			{
				mould="%";	
			}
			String sql = "select fgId,FgName from tbProductionOpening where  productionType like '"+type+"' and  "
					     +" productionStep like '"+step+"' and jobOrderNo like '"+joborder+"' and mouldId like '"+mouldId+"' ";
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbFg.removeAllItems();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbFg.addItem(element[0].toString());
				cmbFg.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		
	}
	private void mouldData(String type,String step,String order ) 
	{

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			  
			String sql=	 "select distinct  mouldId,mouldName  from tbProductionOpening  where productionType  like '"+type+"'  and productionStep  like '"+step+"'  "
					    + "and jobOrderNo like '"+order+"' ";

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
	
	protected void cmbJOrderData(String Step) 
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql= "select 0, jobOrderNo from tbProductionOpening where productionType like '"+cmbType.getValue().toString()+"' and productionStep like '"+Step+"' ";

			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbJorder.addItem(element[1]);	
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

		
	}
	
	

	private void productionStepLoad(String type) 
	{

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql= "select distinct  productionStep,b.StepName from tbProductionOpening a "
					    +"inner join tbProductionStep b on a.productionStep=b.StepId where a.productionType like '"+cmbType.getValue().toString()+"' "
					    +"order by b.StepName ";
			List list=session.createSQLQuery(sql).list();
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

	
	private void reportView()
	{
		String mouldName= "";
		String productiontype = "";
		String Step="";
		String joborder="";
		String productionstep="";
		String supplier="";
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();

		if(cmbType.getValue()!=null)
		{
			productiontype=cmbType.getValue().toString();	
		}
		
		if(cmbStep.getValue()!=null)
		{
			Step=cmbStep.getValue().toString();	
		}
		else
		{
			Step="%";
		}
		if(cmbJorder.getValue()!=null)
		{
			joborder=cmbJorder.getValue().toString();
		}
		else
		{
			joborder="%";	
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

			String query =  "select CONVERT(date, openingYear,105)openingYear ,productionType,b.productTypeName,productionStep,c.StepName,jobOrderNo,fgId,FgName,semiFgId,semiFgName,inputProductId,inputUnit, inputProductName,mouldId,mouldName,inputQty,inputpcs,inputRate,outputProductId,outputProductName,outputUnit, "
					        +"outputpcs,outputRate from tbProductionOpening a inner join tbProductionType b on a.productionType=b.productTypeId "
					        +"inner join tbProductionStep c on c.StepId=a.productionStep   where  YEAR(openingYear) like '"+dfYear.format(dOpeningYear.getValue())+"' and productionStep like '"+Step+"' and productionType like '"+productiontype+"' "
					       + "and jobOrderNo  like '"+joborder+"'  ";

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


				Window win = new ReportViewerNew(hm,"report/production/rptProcessOpening.jasper",
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


		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("570px");
		mainLayout.setHeight("220px");
		mainLayout.setMargin(false);

		setWidth("570px");
		//setHeight("300px");*/

		lblOpeningYear = new Label("Opening Year   : ");
		lblOpeningYear.setImmediate(false);
		lblOpeningYear.setWidth("-1px");
		lblOpeningYear.setHeight("-1px");
		mainLayout.addComponent(lblOpeningYear, "top: 16px; left: 40px;");

		dOpeningYear.setImmediate(true);		
		dOpeningYear.setWidth("-1px");
		dOpeningYear.setHeight("-1px");
		dOpeningYear.setInvalidAllowed(false);
		dOpeningYear.setDateFormat("yyyy");
		dOpeningYear.setResolution(6);
		mainLayout.addComponent(dOpeningYear, "top: 15px; left: 190px;");

		lblType = new Label();
		lblType.setImmediate(false);
		lblType.setWidth("-1px");
		lblType.setHeight("-1px");
		lblType.setValue("Production Type :");
		mainLayout.addComponent(lblType, "top:41.0px;left:40.0px;");

		cmbType = new ComboBox();
		cmbType.setImmediate(true);
		cmbType.setWidth("200px");
		cmbType.setHeight("24px");
		cmbType.setNullSelectionAllowed(false);
		cmbType.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbType, "top:40.0px;left:190.0px;");

		/*chkAllType = new CheckBox("All");
			chkAllType.setImmediate(true);
			chkAllType.setWidth("-1px");
			chkAllType.setHeight("24px");
			//mainLayout.addComponent(chkAllType , "top:16.0px;left:340.0px;");
		 */
		lblStep = new Label();
		lblStep.setImmediate(false);
		lblStep.setWidth("-1px");
		lblStep.setHeight("-1px");
		lblStep.setValue("Production Step :");
		mainLayout.addComponent(lblStep, "top:66.0px;left:40.0px;");

		cmbStep= new ComboBox();
		cmbStep.setImmediate(true);
		cmbStep.setWidth("200px");
		cmbStep.setHeight("24px");
		cmbStep.setNullSelectionAllowed(true);
		cmbStep.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbStep, "top:65.0px;left:190.0px;");

		chkAllStep = new CheckBox("All");
		chkAllStep.setImmediate(true);
		chkAllStep.setValue(false);
		chkAllStep.setWidth("-1px");
		chkAllStep.setHeight("24px");
		mainLayout.addComponent(chkAllStep, "top:66.0px;left:390.0px;");

		lblJorder = new Label();
		lblJorder.setImmediate(false);
		lblJorder.setWidth("-1px");
		lblJorder.setHeight("-1px");
		lblJorder.setValue("JOb Order No. :");
		mainLayout.addComponent(lblJorder, "top:91.0px;left:40.0px;");

		cmbJorder= new ComboBox();
		cmbJorder.setImmediate(false);
		cmbJorder.setWidth("250px");
		cmbJorder.setHeight("24px");
		cmbJorder.setNullSelectionAllowed(true);
		cmbJorder.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbJorder, "top:90.0px;left:190.0px;");

		chkAllJOrder = new CheckBox("All");
		chkAllJOrder.setImmediate(true);
		chkAllJOrder.setValue(false);
		chkAllJOrder.setWidth("-1px");
		chkAllJOrder.setHeight("24px");
		mainLayout.addComponent(chkAllJOrder, "top:91.0px;left:440.0px;");

		lblMould = new Label();
		lblMould.setImmediate(false);
		lblMould.setWidth("-1px");
		lblMould.setHeight("-1px");
		lblMould.setValue("Mould Name   :");
		//mainLayout.addComponent(lblMould, "top:116.0px;left:40.0px;");

		cmbMould= new ComboBox();
		cmbMould.setImmediate(false);
		cmbMould.setWidth("250px");
		cmbMould.setHeight("24px");
		cmbMould.setNullSelectionAllowed(true);
		cmbMould.setNewItemsAllowed(false);
		//mainLayout.addComponent( cmbMould, "top:115.0px;left:190.0px;");

		chkAllMould = new CheckBox("All");
		chkAllMould.setImmediate(true);
		chkAllMould.setValue(false);
		chkAllMould.setWidth("-1px");
		chkAllMould.setHeight("24px");
		//mainLayout.addComponent(chkAllMould, "top:116.0px;left:440.0px;");

		lblFg = new Label();
		lblFg.setImmediate(false);
		lblFg.setWidth("-1px");
		lblFg.setHeight("-1px");
		lblFg.setValue("Finished Goods   :");
		//mainLayout.addComponent(lblFg, "top:141.0px;left:40.0px;");

		cmbFg= new ComboBox();
		cmbFg.setImmediate(true);
		cmbFg.setWidth("250px");
		cmbFg.setHeight("24px");
		cmbFg.setNullSelectionAllowed(true);
		cmbFg.setNewItemsAllowed(false);
		//mainLayout.addComponent(cmbFg, "top:140.0px;left:190.0px;");

		chkAllFg= new CheckBox("All");
		chkAllFg.setImmediate(true);
		chkAllFg.setValue(false);
		chkAllFg.setWidth("-1px");
		chkAllFg.setHeight("24px");
		//mainLayout.addComponent(chkAllFg, "top:141.0px;left:440.0px;");

		lblSemiFG = new Label("Semi Finished Goods :");
		lblSemiFG.setImmediate(false);
		lblSemiFG.setWidth("-1px");
		lblSemiFG.setHeight("-1px");
		//mainLayout.addComponent(lblSemiFG, "top:166.0px;left:40.0px;");

		cmbSemiFG = new ComboBox();
		cmbSemiFG.setImmediate(true);
		cmbSemiFG.setWidth("250px");
		cmbSemiFG.setHeight("24px");
		cmbSemiFG.setNullSelectionAllowed(true);
		cmbSemiFG.setNewItemsAllowed(false);
		cmbSemiFG.setEnabled(true);
		//mainLayout.addComponent( cmbSemiFG, "top:165.0px;left:190.0px;");

		chkAllSemiFG = new CheckBox("All");
		chkAllSemiFG.setImmediate(true);
		chkAllSemiFG.setValue(false);
		chkAllSemiFG.setWidth("-1px");
		chkAllSemiFG.setHeight("24px");
		//mainLayout.addComponent(chkAllSemiFG, "top:166.0px;left:440.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:117.0px; left:200.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:143.0px;left:0.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:180.opx; left:175.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:180.opx; left:270.0px");

		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
