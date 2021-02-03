package com.example.rawMaterialReport;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportViewer;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

public class GoodsReceiveNote extends Window {


	private FormLayout formLayout1 = new FormLayout();
	private FormLayout formLayout2 = new FormLayout();
	private VerticalLayout mainlayout = new VerticalLayout();
	private HorizontalLayout hLayout2 = new HorizontalLayout();	
	private HorizontalLayout hLayout1 = new HorizontalLayout();	
	private HorizontalLayout hLayout = new HorizontalLayout();	

	int type=0;
	private HorizontalLayout chklayout=new HorizontalLayout();
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	
	private Label lblMRR = new Label();
	private ComboBox cmbSupplier;	
	private ComboBox cmbMrrNo = new ComboBox();
	private CheckBox chkAll = new CheckBox();
	private Label lblBlank = new Label("____________________________________________________________________");
	private Label lblblank1= new Label(); 
	private Label lblblank2= new Label(); 
	private Label lblSupplier= new Label(); 
	
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");

	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private SessionBean sessionBean;
	private HashMap hm1= new HashMap();
	
	private OptionGroup oppurchaseType= new OptionGroup();
	private String [] purchaseType={"Local Credit/Cash Purchase","L / C",};

	public  GoodsReceiveNote (SessionBean sessionBean2,String str){
		this.sessionBean = sessionBean2;
		this.setWidth("520px");
		this.setCaption("GOODS RECEIVE NOTE ::"+sessionBean.getCompany());
		this.setResizable(false);
		AddComponent();		
		EvenAction();		
		Component Object[]={oppurchaseType,cmbSupplier,cmbMrrNo,previewButton,exitButton};
		new FocusMoveByEnter(this, Object);	
		cmbSupplier.focus();
		cmbSupplierAddData("Local Credit/Cash Purchase");
	}

	private void cmbSupplierAddData(String caption)
	{
		
		cmbSupplier.removeAllItems();
		Transaction tx = null;
		try {
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql="";
			if(caption.equalsIgnoreCase("Local Credit/Cash Purchase"))
			{
				sql ="select distinct '1' as type ,a.supplierId,b.supplierName from tbRawPurchaseInfo a "
				 		+ "inner join tbSupplierInfo b on a.supplierId=b.supplierId where a.SupplierId not like '%AL%'";	
			}
			if(caption.equalsIgnoreCase("L / C"))
			{
				 sql = "select distinct '2' as type,  a.supplierId,(select Ledger_Name from tbLedger  "
				 		+ "where Ledger_Id=a.supplierId) from tbRawPurchaseInfo a  where a.SupplierId  like '%AL%' order by type ";
			}
		/*	String sql = "select distinct '1' as type ,a.supplierId,b.supplierName from tbRawPurchaseInfo a inner join tbSupplierInfo b on a.supplierId=b.supplierId "
					     +"where a.SupplierId not like '%AL%' " 
					     +"union "
					     +"select distinct '2' as type,  a.supplierId,(select Ledger_Name from tbLedger where Ledger_Id=a.supplierId) from tbRawPurchaseInfo a  "
					     +"where a.SupplierId  like '%AL%' order by type ";*/
			
			System.out.print(sql);
			
			List list = session.createSQLQuery(sql).list();
			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				hm1.put(element[1].toString(), element[0].toString());
				System.out.println(hm1.get(element[1].toString()));
				cmbSupplier.addItem(element[1].toString());
				cmbSupplier.setItemCaption(element[1].toString(), element[2].toString());
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
	}

	private void EvenAction()
	{
		 oppurchaseType.addListener(new ValueChangeListener() 
		 {
			
			public void valueChange(ValueChangeEvent event)
			{
				if(oppurchaseType.getValue().toString().equalsIgnoreCase("L / C"))
				{
					cmbSupplier.setCaption("L / C No :");	
					cmbSupplier.removeAllItems();
					cmbSupplierAddData("L / C");
					
				}
				if(oppurchaseType.getValue().toString().equalsIgnoreCase("Local Credit/Cash Purchase"))
				{
					cmbSupplier.setCaption("Supplier Name :");	
					cmbSupplier.removeAllItems();
					cmbSupplierAddData("Local Credit/Cash Purchase");
				}				
			}
		});
		previewButton.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				if(cmbSupplier.getValue()!=null && cmbMrrNo.getValue()!=null|| chkAll.booleanValue()==true)
				{
					reportShow();
				}
				else{
					getParent().showNotification("Select All Fields", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});


		exitButton.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		

		cmbSupplier.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbSupplier.getValue()!=null){
					addChallanNo();
					
				}
			}
		});
		chkAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkAll.booleanValue()==true)
				{
					cmbMrrNo.setEnabled(false);
					cmbMrrNo.setValue(null);
				}
				else
				{
					cmbMrrNo.setEnabled(true);
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


	private void addChallanNo()
	{
		
		cmbMrrNo.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();
		
		String query="select  distinct 0, MrrNo  from tbRawPurchaseInfo where SupplierId like '"+cmbSupplier.getValue()+"'";
		
		
		System.out.println(query);

		List list = session.createSQLQuery(query).list();		
		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbMrrNo.addItem(element[1]);
		}
		
		

	}
	
	
	private void reportShow()
	{
		String query=null;
		String activeFlag = null;
		
		if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try{
			
			HashMap<String, Object> hm = new HashMap<String, Object>();
			int value=0;
			System.out.println("new value is"+hm1.get(cmbSupplier.getValue()));
			
			value= Integer.parseInt((String) hm1.get(cmbSupplier.getValue())) ;
		
			if(value==1)
			{
				query="select * from vwRawPurchaseReceipt where SupplierId = '"+cmbSupplier.getValue().toString()+"'" +
						  " and MrrNo like  '"+(cmbMrrNo.getValue()!=null?cmbMrrNo.getValue().toString():"%")+"' ";	
			}
			
			if(value==2)
			{
				query="select * from vwRawPurchaseReceiptLc where SupplierId = '"+cmbSupplier.getValue().toString()+"'" +
						  " and MrrNo  like '"+(cmbMrrNo.getValue()!=null?cmbMrrNo.getValue().toString():"%")+"' ";	
			}
			System.out.println(query);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("user", sessionBean.getUserName());

			hm.put("URL",getApplication().getURL().toString().replace("uptd/", ""));
			
			if(queryValueCheck(query))
			{
				hm.put("sql", query);
			
		
			//hm.put("sql", query);
		//	List list=session.createSQLQuery(query).list();
			//if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/raw/rptGRN.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				
				win.setCaption("Report : Goods Receive Note (GRN)");
				this.getParent().getWindow().addWindow(win);
			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){

			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);

		}
	}
	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try 
		{
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				return true;
			}
		} 
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		return false;
	}
	private void AddComponent() {
		
		hLayout.setSpacing(true);
		hLayout1.setSpacing(true);
		mainlayout.setSpacing(true);
		formLayout1.setSpacing(true);
		formLayout2.setSpacing(true);
		
		oppurchaseType= new OptionGroup("Purchase Type:");
		oppurchaseType.setImmediate(true);
		oppurchaseType.setWidth("-1px");
		oppurchaseType.setHeight("-1px");
		oppurchaseType.setStyleName("horizontal");
		
		
		for(int i=0;i<purchaseType.length;i++)	
		{
			oppurchaseType.addItem(purchaseType[i]);
		}
		
		oppurchaseType.select("Local Credit/Cash Purchase");
		formLayout1.addComponent(oppurchaseType);

	/*	lblSupplier = new Label();
		lblSupplier.setImmediate(false);
		lblSupplier.setWidth("-1px");
		lblSupplier.setHeight("-1px");
		lblSupplier.setValue("Supplier Name :");
		mainlayout.addComponent(lblSupplier, "top:72.0px;left:20.0px;");
		*/
		cmbSupplier=new ComboBox("Supplier Name");
		cmbSupplier.setWidth("260px");
		cmbSupplier.setNullSelectionAllowed(true);
		cmbSupplier.setImmediate(true);
		cmbSupplier.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		formLayout1.addComponent(cmbSupplier);

		lblMRR = new Label("MRR No :");
		lblMRR.setImmediate(true);
		lblMRR.setWidth("-1px");
		lblMRR.setHeight("-1px");
		//hLayout2.addComponent(lblMRR);
		//hLayout2.addComponent(lblblank1);
		
		cmbMrrNo.setWidth("220px");
		cmbMrrNo.setNullSelectionAllowed(true);
		cmbMrrNo.setInputPrompt("Select MRR No.");
		hLayout2.addComponent(cmbMrrNo);
		cmbMrrNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		
		
		chkAll = new CheckBox("All");
		chkAll.setImmediate(true);
		chkAll.setHeight("-1px");
		chkAll.setWidth("-1px");
		hLayout2.addComponent(chkAll);
		formLayout1.addComponent(hLayout2);
		
		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		formLayout1.addComponent(chklayout);

		previewButton.setWidth("90px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		
		buttonLayout.addComponent(previewButton);
		buttonLayout.addComponent(exitButton);

		hLayout1.addComponent(formLayout1);
		lblblank1.setHeight("24px");
		formLayout2.addComponent(lblblank1);

		lblblank2.setHeight("1px");
		formLayout2.addComponent(lblblank2);
		
	
		mainlayout.addComponent(hLayout1);
		//mainlayout.addComponent(hLayout);
		mainlayout.addComponent(lblBlank);
		mainlayout.addComponent(buttonLayout);
		buttonLayout.setSpacing(true);
		mainlayout.setSpacing(true);
		
		formLayout1.setComponentAlignment(hLayout2, Alignment.BOTTOM_LEFT);
		mainlayout.setComponentAlignment(hLayout1, Alignment.BOTTOM_CENTER);
		
		mainlayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
		this.addComponent(mainlayout);

	}
	
}