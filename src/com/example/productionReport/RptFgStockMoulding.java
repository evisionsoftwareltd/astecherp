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
public class RptFgStockMoulding extends Window{

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

	private Label lblAsonDate,lblToDate;
	private Label lblProductionTypeDate;
	private Label lblLine;
	private Label lblReportType =new Label("Report Type :");
	private PopupDateField asonDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private OptionGroup opgValue;
	private static final List<String> OptionValue=Arrays.asList(new String[]{"With Value","All FG"});


	private AbsoluteLayout mainLayout;

	public RptFgStockMoulding(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("FG STOCK (MOULDING) ::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
		productionTypeDataLoad();
		cmbFgName.setEnabled(false);
		chkAll.setEnabled(false);
	}

	private void productionTypeDataLoad()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct  productionTypeId,productionTypeName from tbSemiFgInfo";
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
		cmbType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbType.getValue()!=null){
					cmbFgName.setEnabled(true);
					chkAll.setEnabled(true);
					cmbFgLoad();
				}
				else{
					cmbFgName.removeAllItems();
					cmbFgName.setEnabled(false);
					chkAll.setEnabled(false);
				}
			}
		});

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
						showNotification("Please Select Fg Name",Notification.TYPE_WARNING_MESSAGE); 
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

	}
	private void cmbFgLoad() {
		cmbFgName.removeAllItems();

		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select semiFgCode,semiFgName from tbSemiFgInfo where productionTypeId='"+cmbType.getValue()+"' and  semiFgCode in  "
                        +"( "
                        +"select fGCode from tbFinishedGoodsStandardInfo where isFg='YES' "
                        +")";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbFgName.addItem(element[0]);
				cmbFgName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			
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
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//hm.put("openyear",new SimpleDateFormat("yyyy").format(asonDate.getValue()) );
			//hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("asOnDate", new SimpleDateFormat("dd-MM-yyyy").format(asonDate.getValue()));
			hm.put("productionType",cmbType.getItemCaption(cmbType.getValue()) );
			//hm.put("productionStep",cmbFgName.getItemCaption(cmbFgName.getValue()) );
			//hm.put("user", sessionBean.getUserName());



			String semiFg="%";
			if(cmbFgName.getValue()!=null){
				semiFg=cmbFgName.getValue().toString();
			}
			String productType= cmbType.getValue().toString();
			
			if(opgValue.getValue().toString().equalsIgnoreCase("With Value")){
				query="select * from funcMouldingStock('"+semiFg+"','"+datef.format(asonDate.getValue())+"','"+productType+"')  where closingStock>0 ";
			}
			else{
			    	 query= "select * from funcMouldingStock('"+semiFg+"','"+datef.format(asonDate.getValue())+"','"+productType+"')   ";
				}	

			//query="select * from funcSemiFgStockDateBetween('"+semiFg+"','"+datef.format(asonDate.getValue())+"','"+datef.format(toDate.getValue())+"') where semiFgStock>0";
			hm.put("sql", query);
			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/RptFgStockMouldingAsonDate.jasper",
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
				cmbType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
				mainLayout.addComponent( cmbType, "top:15.0px;left:140.0px;");
				
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
				cmbFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
				mainLayout.addComponent( cmbFgName, "top:41.0px;left:140.0px;");

				chkAll.setImmediate(true);
				chkAll.setWidth("-1px");
				chkAll.setHeight("-1px");
				mainLayout.addComponent(chkAll, "top:43.0px;left:450.0px;");
				
				lblAsonDate = new Label();
				lblAsonDate.setImmediate(false);
				lblAsonDate.setWidth("-1px");
				lblAsonDate.setHeight("-1px");
				lblAsonDate.setValue("As on Date: ");
				mainLayout.addComponent(lblAsonDate, "top:67.0px;left:20.0px;");

				asonDate.setImmediate(true);
				asonDate.setResolution(PopupDateField.RESOLUTION_DAY);
				asonDate.setValue(new java.util.Date());
				asonDate.setDateFormat("dd-MM-yyyy");
				asonDate.setWidth("107px");
				asonDate.setHeight("-1px");
				asonDate.setInvalidAllowed(false);
				mainLayout.addComponent( asonDate, "top:67.0px;left:140.0px;");
				
				lblReportType = new Label();
				lblReportType.setImmediate(false);
				lblReportType.setWidth("-1px");
				lblReportType.setHeight("-1px");
				lblReportType.setValue("Report Type: ");
				mainLayout.addComponent(lblReportType, "top:90.0px;left:20.0px;");
				
				opgValue=new OptionGroup("",OptionValue);
				opgValue.setImmediate(true);
				opgValue.setValue("With Value");
				opgValue.setStyleName("horizontal");
				mainLayout.addComponent(opgValue, "top:92.0px;left:140.0px;");

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
				mainLayout.addComponent(lblLine, "top:150.0px;left:25.0px;");

				previewButton.setWidth("80px");
				previewButton.setHeight("28px");
				previewButton.setIcon(new ThemeResource("../icons/print.png"));
				mainLayout.addComponent(previewButton,"top:175.opx; left:135.0px");

				exitButton.setWidth("70px");
				exitButton.setHeight("28px");
				exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
				mainLayout.addComponent(exitButton,"top:175.opx; left:220.0px");

//				chkAll.setVisible(false);
				lblAll.setVisible(false);
				return mainLayout;


	}
}
