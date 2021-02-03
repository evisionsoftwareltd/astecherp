package com.example.productionReport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RptInkFormulation extends Window{
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;

	private Label lblProduction;
	private ComboBox cmbProduction;
	private Label lblFGName;
	private ComboBox cmbFGName;
	
	private CheckBox chkAllFG = new CheckBox();

	private CheckBox chkpdf = new CheckBox("PDF");
	private CheckBox chkother = new CheckBox("Others");
	private HorizontalLayout chklayout = new HorizontalLayout();
	private Label lblStatus;
	private Label lblLine;
	private OptionGroup opgStatus;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Active","Inactive"});
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");


	public RptInkFormulation(SessionBean sessionBean,String s){
		this.sessionBean = sessionBean;
		this.setCaption("INK FORMULATION :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();		
		setContent(mainLayout);
		cmbFgDataLoad();
		setEventAction();
	}
	private void reportView()
	{
		String fgName = "";
		String status="Active";
		String fgFlag="";

		if(chkAllFG.booleanValue())
		{
			fgName ="%";
		}
		else
		{
			fgName = cmbFGName.getValue().toString();
		}
		if(opgStatus.getValue().toString().equalsIgnoreCase("Inactive")){
			status="Inactive";
		}
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("parentType", "INK FORMULATION");
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());

			String query="select semiFgSubId,semiFgSubName,rawItemCode,rawItemName,unit,pcsPerKg,QtyInKg,slFlag,status "+
					" from tbInkFormulationInfo a inner join tbInkFormulationDetails b on a.jobNo=b.jobNo "+
					" where semiFgSubId like '"+fgName+"' and status='Active' and slFlag=( "+
					" select MAX(slFlag) from tbInkFormulationInfo where semiFgSubId=a.semiFgSubId and status='Active')";
			
			System.out.println(query);


			hm.put("sql", query);
			Window win = new ReportViewer(hm,"report/production/RptInkFormulation.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

			win.setStyleName("cwindow");
			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void setEventAction() {
		previewButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbFGName.getValue()!=null || chkAllFG.booleanValue())
				{
					reportView(); 
				}
				else
				{
					showNotification("Please Select FG Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		exitButton.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});


		chkpdf.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkpdf.booleanValue()==true)
				{
					chkother.setValue(false);
				}
				else
				{
					chkother.setValue(true);
				}
			}
		});

		chkother.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkother.booleanValue()==true)
				{
					chkpdf.setValue(false);
				}
				else
				{
					chkpdf.setValue(true);
				}
			}
		});
		chkAllFG.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllFG.booleanValue())
				{
					cmbFGName.setEnabled(false);
					cmbFGName.setValue(null);	
				}
				else
				{
					cmbFGName.setEnabled(true);
					cmbFGName.focus();
				}
			}
		});
	}
	private void cmbFgDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct semiFgSubId,semiFgSubName from tbInkFormulationInfo order by semiFgSubName";
			List<?> list = session.createSQLQuery(sql).list();
			cmbFGName.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbFGName.addItem(element[0].toString());
				cmbFGName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private AbsoluteLayout buildMainLayout()
	{
		this.setWidth("550px");
		this.setHeight("200px");
		
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		
		lblProduction = new Label("Production Type :");
		lblProduction.setImmediate(false);
		lblProduction.setWidth("-1px");
		lblProduction.setHeight("-1px");
		lblProduction.setVisible(false);
		mainLayout.addComponent(lblProduction, "top:45.0px;left:40.0px;");

		cmbProduction = new ComboBox();
		cmbProduction.setImmediate(true);
		cmbProduction.setEnabled(false);
		cmbProduction.setWidth("260px");
		cmbProduction.setHeight("24px");
		cmbProduction.setNullSelectionAllowed(false);
		cmbProduction.setNewItemsAllowed(false);
		cmbProduction.setValue(null);	
		cmbProduction.setVisible(false);
		mainLayout.addComponent( cmbProduction, "top:43.0px;left:170.0px;");

		lblFGName = new Label("FG Name :");
		lblFGName.setImmediate(false);
		lblFGName.setWidth("-1px");
		lblFGName.setHeight("-1px");
		mainLayout.addComponent(lblFGName, "top:10.0px;left:40.0px;");

		cmbFGName = new ComboBox();
		cmbFGName.setImmediate(true);
		cmbFGName.setWidth("260px");
		cmbFGName.setHeight("24px");
		cmbFGName.setNullSelectionAllowed(true);
		cmbFGName.setNewItemsAllowed(false);
		cmbFGName.setEnabled(true);
		mainLayout.addComponent( cmbFGName, "top:10.0px;left:150.0px;");

		chkAllFG = new CheckBox("All");
		chkAllFG.setImmediate(true);
		chkAllFG.setValue(false);
		chkAllFG.setWidth("-1px");
		chkAllFG.setHeight("24px");
		mainLayout.addComponent(chkAllFG, "top:10.0px;left:420.0px;");

		lblStatus = new Label();
		lblStatus.setImmediate(false);
		lblStatus.setWidth("-1px");
		lblStatus.setHeight("-1px");
		lblStatus.setValue("Status: ");
		mainLayout.addComponent(lblStatus, "top:40.0px;left:40.0px;");

		opgStatus=new OptionGroup("",Optiontype);
		opgStatus.setImmediate(true);
		opgStatus.setValue("Active");
		opgStatus.setStyleName("horizontal");
		mainLayout.addComponent(opgStatus, "top:38.0px;left:170.0px;");

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
		mainLayout.addComponent(lblLine, "top:70.0px;left:0.0px;");

		previewButton.setWidth("100px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));

		mainLayout.addComponent(chklayout, "top:100.0px; left:220.0px");
		mainLayout.addComponent(previewButton,"top:130.opx; left:186.0px");
		mainLayout.addComponent(exitButton,"top:130.opx; left:285.0px");

		return mainLayout;
	}

}
