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
public class RptSecOpeningStock extends Window{

	SessionBean sessionBean;
	private Label productionType;
	private ComboBox cmbProductionType=new ComboBox();

	private InlineDateField dOpeningYear;
	private Label lblOpeningYear;

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
	private SimpleDateFormat dateYear = new SimpleDateFormat("yyyy");
	private OptionGroup opgValue;
	private static final List<String> OptionValue=Arrays.asList(new String[]{"With Value","All FG"});


	private AbsoluteLayout mainLayout;

	public RptSecOpeningStock(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("ASSEMBLE OPENING::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
		ProductionTypeLoad();
	}

	private void setEventAction() {		

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbProductionType.getValue()!=null)
				{
					reportView(); 
				}
				else
				{
					showNotification("Please Select Fg Name",Notification.TYPE_WARNING_MESSAGE); 
				}


			}
		});
		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		/*chkAllParty.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAllParty.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbPartyName.setEnabled(false);
					cmbPartyName.setValue(null);
					cmbMaterProductDataLoad();

				}
				else{
					cmbPartyName.setEnabled(true);
					cmbPartyName.focus();
					cmbMaterProductDataLoad();
				}
			}
		});*/
		/*cmbPartyName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbPartyName.getValue()!=null||chkAllParty.booleanValue()){
					cmbMaterProductDataLoad();
				}
				else{
					cmbMasterProductName.removeAllItems();
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

	private void ProductionTypeLoad() {
		//cmbPartyName.removeAllItems();

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct productionStepId,productionStepName from tbSemiFgSubInformation "
					+"where productionStepId not in ('','Assemble')";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbProductionType.addItem(element[0]);
				cmbProductionType.setItemCaption(element[0], element[1].toString());
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
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("OpeningYear", dateYear.format(dOpeningYear.getValue()));
			hm.put("ProductionType", cmbProductionType.getValue());

			query=  "select productid,(select semiFgSubName from tbSemiFgSubInformation where semiFgSubId=productid) "
					+"prductName,unit,qty,rate,amount from tbsectionOpenignStock "
					+"where YEAR(openignYear)='"+dateYear.format(dOpeningYear.getValue())+"' and productionType='"+cmbProductionType.getValue()+" ' ";
			
			hm.put("sql", query);
			


			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/Rptsectionopn.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else
			{
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
		mainLayout.setHeight("220px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("530px");
		setHeight("300px");

		productionType = new Label();
		productionType.setImmediate(false);
		productionType.setWidth("-1px");
		productionType.setHeight("-1px");
		productionType.setValue("Production Type :");
		mainLayout.addComponent(productionType, "top:15.0px;left:20.0px;");		

		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setNullSelectionAllowed(false);
		cmbProductionType.setNewItemsAllowed(false);
		cmbProductionType.setEnabled(true);
		mainLayout.addComponent( cmbProductionType, "top:15.0px;left:140.0px;");

		// lblOpeningYear
		lblOpeningYear = new Label();
		lblOpeningYear.setImmediate(false);
		lblOpeningYear.setWidth("-1px");
		lblOpeningYear.setHeight("-1px");
		lblOpeningYear.setValue("Opening Year :");
		mainLayout.addComponent(lblOpeningYear, "top:45.0px;left:20.0px;");

		// dOpeningYear
		dOpeningYear = new InlineDateField();
		dOpeningYear.setImmediate(true);
		dOpeningYear.setDateFormat("yyyy");
		dOpeningYear.setWidth("-1px");
		dOpeningYear.setHeight("-1px");
		dOpeningYear.setInvalidAllowed(false);
		dOpeningYear.setResolution(6);
		mainLayout.addComponent(dOpeningYear, "top:45.0px;left:140.0px;");




		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:75.0px; left:140.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:105.0px;left:25.0px;");

		previewButton.setWidth("100px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:135.opx; left:135.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:135.opx; left:220.0px");


		return mainLayout;
	}
}
