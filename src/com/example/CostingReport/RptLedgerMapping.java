package com.example.CostingReport;
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
public class RptLedgerMapping extends Window{

	SessionBean sessionBean;
	private Label lblHeadName;
	private ComboBox cmbHeadName=new ComboBox();
	private CheckBox chkAll=new CheckBox("All");
	private Label lblAll=new Label();

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
	
	private OptionGroup opgValue;
	private static final List<String> OptionValue=Arrays.asList(new String[]{"With Value","All FG"});

	private AbsoluteLayout mainLayout;

	public RptLedgerMapping(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("CONVERSION COST LEDGER MAPPING ::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		this.addComponent(mainLayout);
		setEventAction();
		cmbHeadLoad();
	}

	private void setEventAction() {		
		
		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbHeadName.getValue()!=null|| chkAll.booleanValue()==true)
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
		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAll.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbHeadName.setEnabled(false);
					cmbHeadName.setValue(null);

				}
				else{
					cmbHeadName.setEnabled(true);
					cmbHeadName.focus();
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
	private void cmbHeadLoad() {
		cmbHeadName.removeAllItems();
		
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="SElect distinct headId,headName from tbConversionCostWiseLedgerMappingInfo order by headName";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbHeadName.addItem(element[0]);
				cmbHeadName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			//this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
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
			SemiFg=cmbHeadName.getValue().toString();

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
	
			HashMap hm=new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+""+sessionBean.getUserIp());
			
			String headId="%";
			if(cmbHeadName.getValue()!=null){
				headId=cmbHeadName.getValue().toString();
			}
				query="SElect  a.headId,a.headName,b.groupId,b.groupName,b.subGroupId,b.subGroupName,b.ledgerId,b.ledgerName from "
						+ "tbConversionCostWiseLedgerMappingInfo a inner join tbConversionCostWiseLedgerMappingDetails b "
						+ "on a.transactionNo=b.tranasactionNo where a.headId like '"+headId+"'";
				hm.put("sql", query);
				List list=session.createSQLQuery(query).list();
	
				if(!list.isEmpty())
				{
					Window win = new ReportViewerNew(hm, "report/production/RptLedgerMapping.jasper", 
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\", "/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp", false,
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
		mainLayout.setImmediate(true);
		mainLayout.setWidth("550px");
		mainLayout.setHeight("145px");
		
		mainLayout.setMargin(false);
		// top-level component properties
		setWidth("550px");
		setHeight("220px");

		lblHeadName=new Label();
		lblHeadName.setImmediate(true);
		lblHeadName.setWidth("-1px");
		lblHeadName.setHeight("-1px");
		lblHeadName.setValue("Head Name :");
		mainLayout.addComponent(lblHeadName,"top:16.0px; left:20.0px;");
		
		cmbHeadName= new ComboBox();
		cmbHeadName.setImmediate(true);
		cmbHeadName.setWidth("300px");
		cmbHeadName.setHeight("24px");
		cmbHeadName.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbHeadName,"top:15.0px; left:130.0px;");

		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:15.0px;left:450.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:80.0px; left:220.0px");
		
		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:60.0px;left:15.0px;");
		
		previewButton.setWidth("100px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:115.opx; left:186.0px");
		
		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:115.opx; left:270.0px");

//		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;

	}
}
