package com.example.sparePartsReport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.FocusMoveByEnter;
import com.common.share.ReportViewer;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptProductWiseStockRegister extends Window
{

	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private Label lblproductname,lblfromdate,lbltodate;
	private ComboBox cmbproductname;
	private PopupDateField pdffromdate, pdftodate;
	private SimpleDateFormat dbformat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat norformat=new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat yearformat=new SimpleDateFormat("yyyy");
	private NativeButton btnpreview,btnexit;
	private CheckBox chkpdf,chkother;
	int type=0;

	public RptProductWiseStockRegister(SessionBean sessionBean, String str)
	{

		this.sessionbean=sessionBean;
		buildmainlayout();
		this.setContent(mainlayout);
		this.setCaption("PRODUCT WISE STOCK REGISTER :: "+sessionbean.getCompany());
		this.center();
		this.setResizable(false);
		this.setWidth("490px");
		this.setHeight("280px");
		loadcmbdata();
		allbtnaction();
		cmbproductname.focus();
		Component com[]={cmbproductname,pdffromdate,pdftodate,btnpreview};
		new FocusMoveByEnter(this, com);

	}

	private void loadcmbdata()
	{

		Transaction tx=null;
		String query=null;

		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			query="select vRawItemCode,vRawItemName  from tbRawItemInfo where vCategoryType like '%Spare Parts%' and vflag='New'";

			Iterator itr=session.createSQLQuery(query).list().iterator();

			while(itr.hasNext())
			{
				Object [] element=(Object []) itr.next();
				cmbproductname.addItem(element[0]);
				cmbproductname.setItemCaption(element[0], element[1].toString());
			}
		}

		catch(Exception exp)
		{
			getParent().showNotification(exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void allbtnaction()
	{
		chkpdf.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkpdf.booleanValue())
				{
					chkother.setValue(false);
					type=1;
				}
				else
				{
		        	chkother.setValue(true);
					type=0;
				}
			}

		});

		chkother.addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event)
			{

				if(chkother.booleanValue())                                                                                                                                                         
				{
					chkpdf.setValue(false);
					type=0;
				}
				else
				{
					chkpdf.setValue(true);
					type=1;
				}

			}
		});

		btnpreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				reportview();
			}
		});

		btnexit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}

	private void reportview()
	{
		String query=null;

		/*if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(pdffromdate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionbean.getFiscalOpenDate()))  && 
				Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(pdftodate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionbean.getFiscalCloseDate())))*/
		{
			Transaction tx=null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String query1="exec PrcRawStockReportDate '"+dbformat.format(pdffromdate.getValue())+"','"+dbformat.format(pdftodate.getValue())+"','"+cmbproductname.getValue()+"'";
				session.createSQLQuery(query1).executeUpdate();
				tx.commit();
			}
			catch(Exception exp)
			{
				tx.rollback();
				this.getParent().showNotification(exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			
			try
			{
				HashMap hm = new HashMap();
				hm.put("logo", sessionbean.getCompanyLogo());
				hm.put("company", sessionbean.getCompany());
				hm.put("address", sessionbean.getCompanyAddress());
				hm.put("PhoneNumber", "Phone: "+sessionbean.getCompanyAddress());
				hm.put("username", sessionbean.getUserName());
				hm.put("userIp",sessionbean.getUserIp());
				hm.put("Phone", sessionbean.getCompanyContact());

				if(cmbproductname.getValue()!=null)
				{
					query="select * from dbo.[funRawMaterialStockFlownew]('"+cmbproductname.getValue()+"')";

					System.out.println(query);
					hm.put("sql", query);

					hm.put("productname", cmbproductname.getItemCaption(cmbproductname.getValue()));

					hm.put("fromdate",norformat.format(pdffromdate.getValue()));
					hm.put("todate",norformat.format(pdftodate.getValue())) ;

					Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					tx=session.beginTransaction();

					List lst=session.createSQLQuery(query).list();
					

					if(!lst.isEmpty())
					{
						Window win = new ReportViewerNew(hm,"report/raw/RptProductWiseStockRegister.jasper",
								this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
								this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
								this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
						win.setCaption("Report : Goods Receive Note (GRN)");
						this.getParent().getWindow().addWindow(win);
					}
					else
					{
						this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
					}			
				}
				else
				{	
					getParent().showNotification("Please Select Product Name!!!", Notification.TYPE_WARNING_MESSAGE);		
				}
			}
			catch(Exception exp){
				this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
		}
		/*else{

			this.getParent().showNotification("Warning","Please Select vaild Date.",Notification.TYPE_WARNING_MESSAGE);
		}*/
	}
	private AbsoluteLayout buildmainlayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setImmediate(true);

		lblproductname=new Label("Product Name");
		lblproductname.setImmediate(false);
		mainlayout.addComponent(lblproductname, "top:40px; left:40px");
		mainlayout.addComponent(new Label(":"), "top:40px; left:140px");

		cmbproductname=new ComboBox();
		cmbproductname.setImmediate(true);
		cmbproductname.setWidth("270px");
		cmbproductname.setHeight("24px");
		cmbproductname.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(cmbproductname, "top:38px; left:150px");

		lblfromdate=new Label("From Date");
		lblfromdate.setImmediate(false);
		mainlayout.addComponent(lblfromdate, "top:70px; left:40px");
		mainlayout.addComponent(new Label(":"), "top:70px; left:140px");

		pdffromdate=new PopupDateField();
		pdffromdate.setImmediate(true);
		pdffromdate.setWidth("110px");
		pdffromdate.setHeight("24px");
		pdffromdate.setResolution(pdffromdate.RESOLUTION_DAY);
		pdffromdate.setValue(new Date());
		pdffromdate.setDateFormat("dd-MM-yyyy");
		mainlayout.addComponent(pdffromdate, "top:68px; left:150px");

		lbltodate=new Label("To Date");
		lbltodate.setImmediate(false);
		mainlayout.addComponent(lbltodate, "top:100px; left:40px");
		mainlayout.addComponent(new Label(":"), "top:100px; left:140px");

		pdftodate=new PopupDateField();
		pdftodate.setImmediate(true);
		pdftodate.setWidth("110px");
		pdftodate.setHeight("24px");
		pdftodate.setResolution(pdffromdate.RESOLUTION_DAY);
		pdftodate.setValue(new Date());
		pdftodate.setDateFormat("dd-MM-yyyy");
		mainlayout.addComponent(pdftodate, "top:98px; left:150px");

		chkpdf=new CheckBox("PDF");
		chkpdf.setImmediate(true);
		chkpdf.setValue(true);
		mainlayout.addComponent(chkpdf, "top:130px; left:145px");

		chkother=new CheckBox("Other");
		chkother.setImmediate(true);
		//chkother.setValue(true);
		mainlayout.addComponent(chkother, "top:130px; left:190px");

		mainlayout.addComponent(new Label("_______________________________________________________________________________________________________________"), "top:155px; left:20px; right:20px");

		btnpreview=new NativeButton("Preview");
		btnpreview.setImmediate(true);
		btnpreview.setIcon(new ThemeResource("../icons/print.png"));
		btnpreview.setWidth("95px");
		btnpreview.setHeight("24px");
		mainlayout.addComponent(btnpreview, "top:185px; left:145px");

		btnexit=new NativeButton("Exit");
		btnexit.setImmediate(true);
		btnexit.setIcon(new ThemeResource("../icons/exit.png"));
		btnexit.setWidth("80px");
		btnexit.setHeight("24px");
		mainlayout.addComponent(btnexit, "top:185px; left:240px");

		return mainlayout;

	}

}
