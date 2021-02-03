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
public class RptSemiFgStockAsOnDate extends Window{

	SessionBean sessionBean;
	private Label lblSemiFgName;
	private ComboBox cmbType=new ComboBox();
	private Label lblProductionType;
	private ComboBox cmbSemiFgName=new ComboBox();

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

	public RptSemiFgStockAsOnDate(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("SECTION STOCK AS ON DATE::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		productionTypeDataLoad();
		setEventAction();
	}
	
	@SuppressWarnings({ "rawtypes" })
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
		
			String sql="select distinct ProductionType,(select productTypeName from tbProductionType where " +
					"productTypeId=ProductionType) from tbRawIssueInfo where IssueRef like '%Req%'";
			Iterator<?>iter=dbService(sql);

			int i=0;
			while (iter.hasNext())
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
			
				if(cmbType.getValue()!=null)
				{
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
		try
		{
			String sql="select distinct rawItemCode,(select vRawItemName from tbRawItemInfo where vRawItemCode=" +
					"rawItemCode) from tbMouldSectionReceiptDetails";
			Iterator<?>iter=dbService(sql);
			while(iter.hasNext()){
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
		String subquery="";

		if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;
		
		String SemiFg = "";
		
		if(chkAll.booleanValue())
			SemiFg="%";
		else
			SemiFg=cmbSemiFgName.getValue().toString();

		try{
			
			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("asOnDate", new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()));
			hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			
			
			query= "select * from funMixtureRmStock('"+SemiFg+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd").format(formDate.getValue())+"')" ;
			hm.put("sql", query);
			
			/*subquery= "select semifgId , semifgName,ISNULL(SUM(productionqty),0)proQty,ISNULL(SUM(issueQty),0)issueqty, ISNULL(SUM(productionqty),0)-ISNULL(SUM(issueQty),0)balnce " 
					  +"from funSemiMouldproductionAsonDate ('"+cmbType.getValue()+"','"+datef.format(formDate.getValue())+"','"+SemiFg+"') group by semifgId,semifgName "
					  +"order by semifgName ";*/
			
			//hm.put("subsql", subquery);
			//hm.put("SUBREPORT_DIR", "./report/production/");
			
			Iterator<?>iter=dbService(query);
			

			if(iter.hasNext()){
				Window win = new ReportViewerNew(hm,"report/production/rptSectionStockSummaryAsOnDate.jasper",
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
	
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("550px");
		setHeight("260px");

		lblProductionType = new Label();
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		lblProductionType.setValue("Production Type :");
		mainLayout.addComponent(lblProductionType, "top:20.0px;left:20.0px;");		

		cmbType = new ComboBox();
		cmbType.setImmediate(true);
		cmbType.setWidth("200px");
		cmbType.setHeight("24px");
		cmbType.setNullSelectionAllowed(false);
		cmbType.setNewItemsAllowed(false);
		cmbType.setEnabled(true);
		mainLayout.addComponent( cmbType, "top:18.0px;left:140.0px;");
		
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:45.0px;left:450.0px;");

		lblSemiFgName = new Label();
		lblSemiFgName.setImmediate(false);
		lblSemiFgName.setWidth("-1px");
		lblSemiFgName.setHeight("-1px");
		lblSemiFgName.setValue("Raw Material Name :");
		mainLayout.addComponent(lblSemiFgName, "top:47.0px;left:20.0px;");

		cmbSemiFgName= new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("300px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setNullSelectionAllowed(true);
		cmbSemiFgName.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbSemiFgName, "top:45.0px;left:140.0px;");

		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("As On Date: ");
		mainLayout.addComponent(lblFDate, "top:74.0px;left:20.0px;");

		formDate.setImmediate(true);
		formDate.setResolution(PopupDateField.RESOLUTION_DAY);
		formDate.setValue(new java.util.Date());
		formDate.setDateFormat("dd-MM-yyyy");
		formDate.setWidth("107px");
		formDate.setHeight("-1px");
		formDate.setInvalidAllowed(false);
		mainLayout.addComponent( formDate, "top:72.0px;left:140.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:125.0px; left:210.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:165.0px;left:0.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:185.opx; left:175.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:185.opx; left:255.0px");


		lblAll.setVisible(false);
		return mainLayout;


	}
}
