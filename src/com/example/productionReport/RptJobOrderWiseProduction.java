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
public class RptJobOrderWiseProduction extends Window{

	SessionBean sessionBean;

	private Label lblParty;
	private ComboBox cmbParty;
	private Label lblJobOrder;
	private ComboBox cmbJobOrder;

	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblLine;

	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;

	public RptJobOrderWiseProduction(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("JOB ORDER WISE PRODUCTION REPORT::"+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();		
		this.addComponent(mainLayout);

		cmbPartyData();
		setEventAction();
	}

	private void cmbPartyData()
	{
		cmbParty.removeAllItems();

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql = " select distinct a.vGroupId, a.partyName from tbPartyInfo a" +
					" inner join" +
					" (select partyId from tbJobOrderInfo) b" +
					" on b.partyId = a.vGroupId" +
					" order by a.partyName";
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();

				cmbParty.addItem(element[0].toString());
				cmbParty.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setEventAction() {

		cmbParty.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbParty.getValue()!=null)
				{
					cmbJobOrderData();
				}
			}
		});

		previewButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbParty.getValue()!=null)
				{
					if(cmbJobOrder.getValue()!=null)
					{
						reportView(); 
					}
					else
					{
						showNotification("Please Select Issue TO",Notification.TYPE_WARNING_MESSAGE); 
					}
				}
				else
				{
					showNotification("Please Select Issue From",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

/*		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAll.booleanValue();
				if(bv==true)
				{
					cmbParty.setEnabled(false);
					cmbParty.setValue(null);					
				}
				else{
					cmbParty.setEnabled(true);
					cmbParty.focus();
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

	}

	private void cmbJobOrderData() 
	{
		cmbJobOrder.removeAllItems();

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql="select 0,orderNo jobOrderNo from tbJobOrderInfo where partyId like '"+cmbParty.getValue()+"'";
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();

				cmbJobOrder.addItem(element[1].toString());
				/*if(element[1].toString().equalsIgnoreCase(""))
				{
					cmbJobOrder.setItemCaption(element[0].toString(), element[0].toString());
				}
				else
				cmbJobOrder.setItemCaption(element[0].toString(), element[1].toString());*/
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

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//			hm.put("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("productionType",cmbParty.getItemCaption(cmbParty.getValue()) );
			if(cmbJobOrder.getItemCaption(cmbJobOrder.getValue()).toString().equalsIgnoreCase(""))
			{
				hm.put("productionStep",cmbJobOrder.getValue() );
			}
			else
			{
			hm.put("productionStep",cmbJobOrder.getItemCaption(cmbJobOrder.getValue()) );
			}
			hm.put("user", sessionBean.getUserName());


			/*query = " select c.vProductName, ISNULL(d.mDia,'') as mDia,'' as 'M/cNo',  b.ShiftASqm,b.ShiftAQty,b.ShiftBSqm,b.ShiftBQty , " +
							" (b.ShiftASqm+b.ShiftBSqm)as totalsqm,(b.ShiftAQty+b.ShiftBQty)as totalqty,b.WastageSqm ," +
							" b.WastageQty,b.WastagePercent, e.StepId,e.StepName,a.joborderNo,a.ProductionDate,dbo.funcWastagePercent('"+cmbJobOrder.getValue()+"') totalWastagePercent" +
							" from tbTubeProductionInfo a" +
							" inner join" +
							" tbTubeProductionDetails b" +
							" on a.ProductionNo=b.ProductionNo" +
							" inner join" +
							" tbFinishedProductInfo c" +
							" on c.vProductId=b.FinishedProduct" +
							" left join" +
							" tbStandardFinishedInfo d" +
							" on d.vProductId=b.FinishedProduct" +
							" inner join" +
							" tbProductionStep e" +
							" on e.StepId=a.Stepid" +
							" where e.StepName in('Printing','Tubing','Shouldering','Sealing')" +
							" and a.Stepid in('TubeSTP-1','TubeSTP-2','TubeSTP-3','TubeSTP-4')" +
							" and a.joborderNo like '"+cmbJobOrder.getValue()+"'" +
							" order by a.Stepid,a.ProductionDate";

			String sqlConsumption = "";
			sqlConsumption = 	"	select a.Stepid,f.StepName,a.rawItemCode,e.vRawItemName, " +
													" ISNULL(sum((b.ShiftASqm+b.ShiftBSqm+b.WastageSqm)),0) as totalsqm," +
													" ISNULL(sum((b.ShiftAQty+b.ShiftBQty+b.WastageQty)),0)  as totalqty" +
													" from tbTubeProductionInfo a" +
													" inner join" +
													" tbTubeProductionDetails b" +
													" on a.ProductionNo=b.ProductionNo" +
													" inner join" +
													" tbFinishedProductInfo c" +
													" on c.vProductId=b.FinishedProduct" +
													" left join" +	
													" tbStandardFinishedInfo d" +
													" on d.vProductId=b.FinishedProduct" +
													" inner join" +
													" tbRawItemInfo e" +
													" on e.vRawItemCode=a.rawItemCode" +
													" inner join" +
													" tbProductionStep f" +
													" on f.StepId=a.Stepid" +
													" where a.Stepid in ('TubeSTP-1','TubeSTP-2','TubeSTP-3','TubeSTP-4')and f.StepName in('Printing','Tubing','Shouldering','Sealing')" +
													" and a.joborderNo like '"+cmbJobOrder.getValue()+"'" +
													" group by  a.rawItemCode,e.vRawItemName,a.Stepid,f.StepName" ;
			System.out.println("sqlConsumption"+sqlConsumption);


			String sqlHdpe = "";
			sqlHdpe = 	" select *  from" +
					" (" +
					" select  ISNULL(sum(consumtionption),0) as Consumption,itemName,itemgrupname,itemType" +
					" from  funHdpeAndMbConsumptionJobOrderWise('"+cmbJobOrder.getValue()+"','TubeSTP-3')" +
					" group by itemgrupname,itemName,itemType" +
					" union " +
					" select  (ISNULL(SUM(b.ShiftASqm),0)+ISNULL(SUM(b.ShiftBSqm),0)+ ISNULL(SUM(b.WastageSqm),0))*7.5/1000 as kg," +
					" 'INK' as itemName,'INK'as itemgrupname,'3' as itemType" +
					" from tbTubeProductionInfo a" +
					" inner join tbTubeProductionDetails b" +
					" on a.ProductionNo=b.ProductionNo" +
					" where a.joborderNo like '"+cmbJobOrder.getValue()+"' and a.Stepid like 'TubeSTP-1'" +
					" ) as dfghdg" +
					" order by  dfghdg.itemType";*/
			
			//sqlHdpe = "select itemgrupname, itemgrupname itemName, itemgrupname itemType,sum(isnull(consumtionption,0)) Consumption from funHdpeAndMbConsumptionJobOrdereWiseNew('"+cmbJobOrder.getValue()+"', 'TubeSTP-3') group by itemgrupname";
			//System.out.println("sqlHdpe"+sqlHdpe);
			//hm.put("sqlHdpe", sqlHdpe);

			query="select a.ProductionDate,(select vMachineName from tbMachineInfo where vMachineCode like b.MachineName)machineName,c.vProductName,cycleTime,cavityNo, "+
					"ShiftAQty,ShiftAPcs,ShiftBQty,ShiftBPcs,TotalQty,TotalPcs,WastageQty as wastagePcs,WastagePcs as wastageQty,b.joborderNo, "+
					"a.Stepid,(select StepName from tbProductionStep where StepId=a.Stepid)stepName from tbMouldProductionInfo a "+
					"inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo "+
					"inner join tbFinishedProductInfo c on b.FinishedProduct=c.vProductId "+
					"where b.joborderNo like '"+cmbJobOrder.getValue()+"' order by a.ProductionDate";
			
			hm.put("sql", query);
			//hm.put("sqlConsumption", sqlConsumption);

			hm.put("SUBREPORT_DIR", "./report/production/");

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptJobWorderWiseProductionMoulding.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

				/*Window win1=new ReportPdf(hm, "report/production/rptDailyProduction.jasper",
						"E:/", "E:/", false);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);*/
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
		mainLayout.setWidth("460px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		lblParty = new Label("Party Name :");
		lblParty.setImmediate(false);
		lblParty.setWidth("-1px");
		lblParty.setHeight("-1px");

		cmbParty = new ComboBox();
		cmbParty.setImmediate(true);
		cmbParty.setWidth("200px");
		cmbParty.setHeight("24px");
		cmbParty.setNullSelectionAllowed(false);
		cmbParty.setNewItemsAllowed(false);	
		cmbParty.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblJobOrder = new Label("Job Order No :");
		lblJobOrder.setImmediate(false);
		lblJobOrder.setWidth("-1px");
		lblJobOrder.setHeight("-1px");		

		cmbJobOrder= new ComboBox();
		cmbJobOrder.setImmediate(true);
		cmbJobOrder.setWidth("200px");
		cmbJobOrder.setHeight("24px");
		cmbJobOrder.setNullSelectionAllowed(true);
		cmbJobOrder.setNewItemsAllowed(false);
		cmbJobOrder.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);


		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:140.0px;left:0.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));


		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));


		chkAll.setVisible(false);
		lblAll.setVisible(false);


		mainLayout.addComponent(lblParty, "top:15.0px;left:40.0px;");
		mainLayout.addComponent( cmbParty, "top:13.0px;left:140.0px;");
		mainLayout.addComponent(lblJobOrder, "top:45.0px;left:40.0px;");
		mainLayout.addComponent( cmbJobOrder, "top:43.0px;left:140.0px;");

		mainLayout.addComponent(chklayout, "top:115.0px; left:140.0px");
		mainLayout.addComponent(previewButton,"top:165.opx; left:135.0px");
		mainLayout.addComponent(exitButton,"top:165.opx; left:220.0px");

		return mainLayout;


	}
}
