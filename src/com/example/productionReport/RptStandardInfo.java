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

public class RptStandardInfo extends Window
{
	SessionBean sessionBean;
	private Label lblFgName, lblMachineName,lblmouldName;
	private ComboBox cmbMouldName=new ComboBox();
	private Label lblMouldName;
	private ComboBox cmbFGName=new ComboBox();
	private ComboBox cmbStandardNo=new ComboBox();
	//private ComboBox cmbmouldName=new ComboBox();
	private ComboBox cmbDate;

	private CheckBox chkAll=new CheckBox("All");
	private CheckBox chkAllMould=new CheckBox("All");
	private CheckBox chkAllStandard=new CheckBox("All");
	private CheckBox chkAllDate=new CheckBox("All");
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate,lblToDate;
	private Label lblMouldNameDate;
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

	public RptStandardInfo(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("JOB/STANDARD INFORMATION ::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		MouldNameDataLoad();
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

	private void MouldNameDataLoad()
	{
		try
		{
			String sql="select distinct mouldName mouldId, (select mouldName from tbmouldInfo where mouldid=tbFinishedGoodsStandardInfo.mouldName)" +
					"mould from tbFinishedGoodsStandardInfo where mouldName not like 'null' ";
			System.out.println(sql);
			Iterator<?>iter=dbService(sql);
			while(iter.hasNext())
			{
				Object element[]=(Object[]) iter.next();
				cmbMouldName.addItem(element[0]);				
				cmbMouldName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void FGData(String type) 
	{
		cmbFGName.removeAllItems();


		try
		{
			String sql="select fGCode,(select semiFgName from tbSemiFgInfo where semiFgCode=fGCode)semifgName from " +
					"tbFinishedGoodsStandardInfo where mouldName like  '"+type+"' and  (select semiFgName from tbSemiFgInfo where semiFgCode=fGCode) is not null  " ;
			Iterator<?>iter=dbService(sql);
			System.out.println(sql);
			while(iter.hasNext()){
				Object element[]=(Object[]) iter.next();
				cmbFGName.addItem(element[0]);
				cmbFGName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}		
	}
	private void cmbDateLoad(){
		cmbDate.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		String MouldName="%",FGName="%";
		if(cmbMouldName.getValue()!=null){
			MouldName =cmbMouldName.getValue().toString();
		}
		if(cmbFGName.getValue()!=null){
			FGName =cmbFGName.getValue().toString();
		}
		try{
		/*	String sql="select declarationDate, REPLACE(CONVERT(varchar(120),declarationDate,103),'/','-')" +
					" bangladate from tbFinishedGoodsStandardInfo where mouldName like '"+MouldName+"'" +
					" and fGCode like '"+FGName+"'";*/
			
			String sql="select Convert(date,declarationDate,105), REPLACE(CONVERT(date,declarationDate,105),'/','-') " +
					"bangladate from tbFinishedGoodsStandardInfo where mouldName like '"+MouldName+"' and fGCode like" +
					" '"+FGName+"'";
			System.out.print(sql);
			Iterator<?>iter=dbService(sql);
			while (iter.hasNext()) 
			{
				Object[] element = (Object[]) iter.next();
				cmbDate.addItem(element[0].toString());
				cmbDate.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("cmbDeclarationDate"+exp);
		}
	}
	private void cmbStandard(){
		cmbStandardNo.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		String MouldName="%",FGName="%",date="%";
		if(cmbMouldName.getValue()!=null){
			MouldName =cmbMouldName.getValue().toString();
		}
		if(cmbFGName.getValue()!=null){
			FGName =cmbFGName.getValue().toString();
		}
		if(cmbDate.getValue()!=null){
			date=cmbDate.getValue().toString();
		}
		//dateformat.format(cmbDate.getValue())
		try{
			String sql="select 0,JobNo from tbFinishedGoodsStandardInfo where mouldName like '"+MouldName+"' and " +
					"fGCode like '"+FGName+"' and CONVERT(date,declarationDate,105)like '"+date+"'";
			System.out.println(sql);
			Iterator<?>iter=dbService(sql);
			while (iter.hasNext()) 
			{
				Object[] element = (Object[]) iter.next();
				cmbStandardNo.addItem(element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("cmbDeclarationDate"+exp);
		}
	}
	private void setEventAction() 
	{		

			previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbMouldName.getValue()!=null|| chkAllMould.booleanValue()==true )
				{
					if(cmbFGName.getValue()!=null|| chkAll.booleanValue()==true)
					{
						if(cmbDate.getValue()!=null || chkAllDate.booleanValue())
						{
							if(cmbStandardNo.getValue()!=null || chkAllStandard.booleanValue()==true)
							{
								reportView();	
							}
							else
							{
								showNotification("Please Select Standard No",Notification.TYPE_WARNING_MESSAGE); 	
							}	
						}

						else
						{
							showNotification("Please Select Date",Notification.TYPE_WARNING_MESSAGE); 	
						}


					}
					else
					{
						showNotification("Please Select Fg Name",Notification.TYPE_WARNING_MESSAGE); 
					}

				}
				else
				{
					showNotification("Please Select Mould Name",Notification.TYPE_WARNING_MESSAGE);
				}

			}
		});
		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		cmbMouldName.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) 
			{

				if(cmbMouldName.getValue()!= null)
				{
					System.out.println("FG Data");

					String type="";
					if(cmbMouldName.getValue()!=null)
					{
						type=cmbMouldName.getValue().toString().trim();
					}
					FGData(type);			
				}

			}
		});

		chkAllMould.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				boolean bv = chkAllMould.booleanValue();

				if(bv==true)
				{
					cmbMouldName.setEnabled(false);
					cmbMouldName.setValue(null);
					FGData("%");
				}
				else{
					cmbMouldName.setEnabled(true);
					cmbMouldName.focus();

				}
			}
		});
		cmbFGName.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) 
			{		
				if(chkAllMould.booleanValue()||cmbMouldName.getValue()!=null&& (cmbFGName.getValue()!=null || chkAll.booleanValue()))
				{
					cmbDateLoad();

				}
				else{
					cmbFGName.removeAllItems();
				}

			}
		});
		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				boolean bv = chkAll.booleanValue();

				if(bv==true)
				{
					cmbFGName.setEnabled(false);
					cmbFGName.setValue(null);
					cmbDateLoad();
				}
				else{
					cmbFGName.setEnabled(true);
					cmbFGName.focus();

				}
			}
		});
		cmbDate.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{		
				if(chkAllMould.booleanValue()||cmbMouldName.getValue()!=null&&(cmbFGName.getValue()!=null || chkAll.booleanValue())
						&& (cmbDate.getValue()!=null || chkAllDate.booleanValue()))
				{
					cmbStandard();
				}
				else{
					cmbDate.removeAllItems();
				}

			}
		});
		chkAllDate.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				boolean bv = chkAllDate.booleanValue();

				if(bv==true)
				{
					cmbDate.setEnabled(false);
					cmbDate.setValue(null);
					cmbStandard();
				}
				else{
					cmbDate.setEnabled(true);
					cmbDate.focus();

				}
			}
		});
		chkAllStandard.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				boolean bv = chkAllStandard.booleanValue();

				if(bv==true)
				{
					cmbStandardNo.setEnabled(false);
					cmbStandardNo.setValue(null);
					cmbStandard();
				}
				else{
					cmbStandardNo.setEnabled(true);
					cmbStandardNo.focus();

				}
			}
		});
		/*		chkpdf.addListener(new ValueChangeListener()
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
		});*/
	}



	private void reportView()
	{	
		String query=null;
		Transaction tx=null;

		if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;

		String MouldName="";
		String FGName="";
		String Date="";
		String StandardNo="";

		if(cmbMouldName.getValue()!=null)
		{
			MouldName=cmbMouldName.getValue().toString();
		}
		else
		{
			MouldName="%";	
		}
		if(cmbFGName.getValue()!=null)
		{
			FGName=cmbFGName.getValue().toString();
		}
		else
		{
			FGName="%";	
		}

		if(cmbDate.getValue()!=null)
		{
			Date=cmbDate.getValue().toString();
		}
		else
		{
			Date="%";	
		}

		if(cmbStandardNo.getValue()!=null)
		{
			StandardNo=cmbStandardNo.getValue().toString();
		}
		else
		{
			StandardNo="%";	
		}

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("type",cmbMouldName.getItemCaption(cmbMouldName.getValue()) );
			query=  "select a.JobNo standardNo, (select mouldName from tbmouldInfo where mouldid=a.mouldName)" +
					" mouldName ,(select semiFgName from tbSemiFgInfo where semiFgCode= a.fGCode)fgName ," +
					"CONVERT(date,a.declarationDate,105)declarationDate ,b.RawItemCode,(select vRawItemName " +
					"from tbRawItemInfo where vRawItemCode=b.RawItemCode)rawitemName,b.Qty from" +
					" tbFinishedGoodsStandardInfo a inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo " +
					"where a.JobNo like '"+StandardNo+"'and a.mouldName like '"+MouldName+"' and a.fGCode like '"+FGName+"' and CONVERT(date,a.declarationDate,105) like" +
					"'"+Date+"'order by mouldName,fgName,standardNo";



			System.out.println(query);

			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptStandard-info.jasper",
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

		lblMouldName = new Label();
		lblMouldName.setImmediate(false);
		lblMouldName.setWidth("-1px");
		lblMouldName.setHeight("-1px");
		lblMouldName.setValue("Mould Name :");
		mainLayout.addComponent(lblMouldName, "top:18.0px;left:20.0px;");		

		cmbMouldName = new ComboBox();
		cmbMouldName.setImmediate(true);
		cmbMouldName.setWidth("300px");
		cmbMouldName.setHeight("24px");
		cmbMouldName.setNullSelectionAllowed(false);
		cmbMouldName.setNewItemsAllowed(false);
		cmbMouldName.setEnabled(true);
		mainLayout.addComponent( cmbMouldName, "top:16.0px;left:140.0px;");
		cmbMouldName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		chkAllMould.setImmediate(true);
		chkAllMould.setWidth("-1px");
		chkAllMould.setHeight("-1px");
		mainLayout.addComponent(chkAllMould, "top:16.0px;left:450.0px;");

		lblFgName = new Label();
		lblFgName.setImmediate(false);
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");
		lblFgName.setValue("FG Name :");
		mainLayout.addComponent(lblFgName, "top:43.0px;left:20.0px;");

		cmbFGName= new ComboBox();
		cmbFGName.setImmediate(true);
		cmbFGName.setWidth("300px");
		cmbFGName.setHeight("24px");
		cmbFGName.setNullSelectionAllowed(true);
		cmbFGName.setNewItemsAllowed(false);
		cmbFGName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbFGName, "top:41.0px;left:140.0px;");

		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:41.0px;left:450.0px;");

		lblmouldName = new Label();
		lblmouldName.setImmediate(false);
		lblmouldName.setWidth("-1px");
		lblmouldName.setHeight("-1px");
		lblmouldName.setValue("Declaration Date :");
		mainLayout.addComponent(lblmouldName, "top:68.0px;left:20.0px;");		

		cmbDate = new ComboBox();
		cmbDate.setImmediate(true);
		cmbDate.setWidth("150px");
		cmbDate.setHeight("24px");
		cmbDate.setNullSelectionAllowed(false);
		cmbDate.setNewItemsAllowed(false);
		cmbDate.setEnabled(true);
		cmbDate.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbDate, "top:66.0px;left:140.0px;");

		chkAllDate.setImmediate(true);
		chkAllDate.setWidth("-1px");
		chkAllDate.setHeight("-1px");
		mainLayout.addComponent(chkAllDate, "top:69.0px;left:293.0px;");




		lblMachineName = new Label();
		lblMachineName.setImmediate(false);
		lblMachineName.setWidth("-1px");
		lblMachineName.setHeight("-1px");
		lblMachineName.setValue("Standard No :");
		mainLayout.addComponent(lblMachineName, "top:93.0px;left:20.0px;");

		cmbStandardNo= new ComboBox();
		cmbStandardNo.setImmediate(true);
		cmbStandardNo.setWidth("150px");
		cmbStandardNo.setHeight("24px");
		cmbStandardNo.setNullSelectionAllowed(true);
		cmbStandardNo.setNewItemsAllowed(false);
		cmbStandardNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbStandardNo, "top:91.0px;left:140.0px;");

		chkAllStandard.setImmediate(true);
		chkAllStandard.setWidth("-1px");
		chkAllStandard.setHeight("-1px");
		mainLayout.addComponent(chkAllStandard, "top:94.0px;left:293.0px;");


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
