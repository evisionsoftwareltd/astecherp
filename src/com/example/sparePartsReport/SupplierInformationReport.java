package com.example.sparePartsReport;

import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.ReportViewer;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import java.text.DateFormat;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class SupplierInformationReport extends Window 
{
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout cmbLayout = new FormLayout();
	
	private HorizontalLayout  middleLayout = new HorizontalLayout();
	private VerticalLayout blankLayout = new VerticalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	
	private ComboBox cmbSupplierName = new ComboBox("");
	
	private Label lbLine=new Label("______________________________________________________________");
	
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");


	private SessionBean sessionBean;
	Formatter fmt;
	
	String identify;
	

	public SupplierInformationReport(SessionBean sessionBean2,String str) 
	{
		this.identify = str;		
		this.sessionBean = sessionBean2;
		this.setWidth("395px");
		this.setHeight("245px");
		this.setResizable(false);
		if(identify.equals("Supplier Information"))
		{
			this.setCaption("SUPPLIER INFORMATION :: "+sessionBean.getCompany());
			cmbSupplierName.setCaption("Supplier Name :");
			addSupplierName();
		}
		if (identify.equals("Product Information"))
		{
			this.setCaption("PRODUCT INFORMATION :: "+sessionBean.getCompany());
			cmbSupplierName.setCaption("Product Name :");
			
			rawProductcmb();
		}
		if (identify.equals("Packing Item Information"))
		{
			this.setCaption("PRODUCT INFORMATION (PACKING) :: "+sessionBean.getCompany());
			cmbSupplierName.setCaption("Product Name :");
			packProductcmb();
		}
		if (identify.equals("Finished Goods Product Information"))
		{
			this.setCaption("F / G PRODUCT INFORMATION :: "+sessionBean.getCompany());
			cmbSupplierName.setCaption("Category Name :");
			finiProductcmb();
		}
		addAllComponent();
		this.addComponent(mainLayout);
		allButtonAction();
		cmbSupplierName.focus();
	}

	public void finiProductcmb(){
		
		cmbSupplierName.removeAllItems();
		cmbSupplierName.addItem("All");
		
		cmbSupplierName.setValue("All");
	
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("Select * from VwFiniProduct").list();
		
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplierName.addItem(element[0]);
				cmbSupplierName.setItemCaption(element[0], (String) element[1]);
	
			}
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
		
	}
	

	private void addAllComponent()
	{
		cmbSupplierName.setNullSelectionAllowed(false);
		//cmbSupplierName.addItem("All");
		cmbSupplierName.setWidth("250px");
		cmbLayout.addComponent(cmbSupplierName);
		
		
		middleLayout.addComponent(cmbLayout);
		
		buttonLayout.setSpacing(true);
		blankLayout.setWidth("300");
		blankLayout.setHeight("20px");
		//buttonLayout.addComponent(blankLayout);
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		buttonLayout.addComponent(previewButton);
		//previewButton.setIcon(new ThemeResource("../icons/print.png"));
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		buttonLayout.addComponent( exitButton);
		
		mainLayout.setSpacing(true);
		mainLayout.addComponent(blankLayout);
		mainLayout.addComponent(middleLayout);
		lbLine.setEnabled(false);
		mainLayout.addComponent(lbLine);
		mainLayout.addComponent(buttonLayout);
		mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
	}
	
	private void allButtonAction()
	{	
		 exitButton.addListener( new Button.ClickListener() 
		{
            public void buttonClick(ClickEvent event) 
            {
            	close();
            }
        });
		
		previewButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
				reportView();
				
			}
		});
	}
	
	
	public void addSupplierName(){
		
		cmbSupplierName.removeAllItems();
		cmbSupplierName.addItem("All");
		
		cmbSupplierName.setValue("All");
	
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("select * from tbSupplierDetails").list();
		
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplierName.addItem(element[1]);
				cmbSupplierName.setItemCaption(element[1], (String) element[2]);
	
			}
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
		
	}
	public void rawProductcmb(){
		
		cmbSupplierName.removeAllItems();
		cmbSupplierName.addItem("All");
		
		cmbSupplierName.setValue("All");
	
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("Select Distinct  ProductName,ProductCode from  VwRawProduct order by ProductName").list();
		
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplierName.addItem(element[1].toString());
				cmbSupplierName.setItemCaption(element[1].toString(), element[0].toString());
	
			}
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
		
	}
public void packProductcmb(){
		
		cmbSupplierName.removeAllItems();
		cmbSupplierName.addItem("All");
		
		cmbSupplierName.setValue("All");
	
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("Select Distinct  ProductName,ProductCode from  VwPackingProduct order by ProductName").list();
		
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplierName.addItem(element[1]);
				cmbSupplierName.setItemCaption(element[1], (String) element[0]);
	
			}
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
		
	}

	private void reportView(){		
		String cmbId;
		cmbId=cmbSupplierName.getValue().toString().trim();
		System.out.print(cmbId);
		if(cmbId=="All")
		{
		
			cmbId="%";
		}
		System.out.println(cmbId);
		String query=null;
		
		
		
		try{
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			//hm.put("phone", "Phone: "+sessionBean.getCompanyPhone()+" Fax: "+sessionBean.getCompanyFax()+" E-Mail: "+sessionBean.getCompanyEmail());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			
			
			if(identify.equals("Supplier Information")){
				
				query="select * from tbSupplierDetails where SupplierID like '"+cmbId+"'  ";
				System.out.println(query);
				hm.put("sql", query);
	
				Window win = new ReportViewerNew(hm,"report/raw/Supplierinfo.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",0);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			if (identify.equals("Product Information")){
				query="Select distinct * from VwRawProduct where ProductCode like '"+cmbId + "%'";
				hm.put("sql", query);
				
				Window win = new ReportViewerNew(hm,"report/raw/rawProductInfoRpt.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			if (identify.equals("Packing Item Information")){
				
				query="Select distinct * from VwPackingProduct where ProductCode like '"+cmbId + "%'";
				hm.put("sql", query);
				
				Window win = new ReportViewerNew(hm,"report/packing/packingProductInfoRpt.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",0);
				System.out.println("Hello");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			if (identify.equals("Finished Goods Product Information")){
				
				query="Select distinct * from VwFiniProduct where ItemCode like '"+cmbId + "%'";
				hm.put("sql", query);
				
				Window win = new ReportViewerNew(hm,"report/rptFinishedProductInfo.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",0);
				System.out.println("Hello");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
	
		
			
			//Window win = new ReportViewer(hm,"report/rptSupplierDetails.jasper",this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			
			
			
		}
		catch(Exception exp){
			
			this.getParent().showNotification("Error",Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
		
	}
	
	
}