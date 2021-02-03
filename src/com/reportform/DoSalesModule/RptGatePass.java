package com.reportform.DoSalesModule;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.Window.Notification;

public class RptGatePass extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblGatePassNo;
	private ComboBox cmbGatePassNo;

	private Label lblPassDate;
	private PopupDateField dPassDate;

	private CheckBox chkGAtePassAll;
	private ReportDate reportTime = new ReportDate();
	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat rptdateFormat = new SimpleDateFormat("dd-MM-yyyy");

	private ArrayList<Component> allComp = new ArrayList<Component>();

	public RptGatePass(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("GATE PASS:: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbAddGatePassData();
		btnAction();
		focusEnter();
	}

	private void focusEnter()
	{
		allComp.add(dPassDate);

		allComp.add(button.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	private void btnAction()
	{
		button.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbGatePassNo.getValue()!=null || chkGAtePassAll.booleanValue()==true){
				previewReport();
				}
				else{
					getParent().showNotification("Warning","Please provide Gate Pass No",Notification.TYPE_WARNING_MESSAGE);
					cmbGatePassNo.focus();
				}
			}
		});

		button.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		chkGAtePassAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkGAtePassAll.booleanValue()==true)
				{
					cmbGatePassNo.setValue(null);
					cmbGatePassNo.setEnabled(false);
				}
				else
				{
					cmbGatePassNo.setEnabled(true);
				}
			}
		});
		
		dPassDate.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				cmbGatePassNo.removeAllItems();
				if(dPassDate.getValue()!=null)
				{
					cmbGatePassNo.removeAllItems();
					cmbGatePassNo.setValue(null);
					cmbAddGatePassData();
				}
				
			}
		});
	}

	private void previewReport()
	{
		String queryAll = null;
		String GatePass = "";
		
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			if(chkGAtePassAll.booleanValue()==true)
			{
				GatePass="%";
			}
			else
			{
				GatePass=cmbGatePassNo.getValue().toString();
			}
			

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String date=dateFormat.format(dPassDate.getValue());
			System.out.println("Date: "+date);

			HashMap hm = new HashMap();

			queryAll = "SELECT * from [funGatePass] ('"+dateFormat.format(dPassDate.getValue()) +"','"+GatePass+"')";

			System.out.println("Report Query: "+queryAll);

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("user", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("Date",reportTime.getTime);
			hm.put("Author", sessionBean.getUserName());
			hm.put("logo", sessionBean.getCompanyLogo());
			
			if(queryValueCheck(queryAll))
			{
				hm.put("sql", queryAll);

				Window win = new ReportViewer(hm,"report/account/DoSales/rptGatePass.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	public void cmbAddGatePassData()
	{
		cmbGatePassNo.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select iAutoId,vGatePassNo from tbDeliveryChallanInfo where convert(date,dChallanDate)='"+dateFormat.format(dPassDate.getValue())+"' order by iAutoId").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbGatePassNo.addItem(element[1]);
				cmbGatePassNo.setItemCaption(element[1], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("430px");
		setHeight("200px");


		// lblPassDate
		lblPassDate = new Label("Date : ");
		lblPassDate.setImmediate(true);
		lblPassDate.setWidth("-1px");
		lblPassDate.setHeight("-1px");
		mainLayout.addComponent(lblPassDate, "top:30.0px; left:30.0px;");

		// dPassDate
		dPassDate = new PopupDateField();
		dPassDate.setWidth("110px");
		dPassDate.setDateFormat("dd-MM-yyyy");
		dPassDate.setValue(new java.util.Date());
		dPassDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dPassDate,"top:28.0px; left:170px;");

		lblGatePassNo = new Label("Gate Pass No :");
		lblGatePassNo.setImmediate(false);
		lblGatePassNo.setWidth("-1px");
		lblGatePassNo.setHeight("-1px");
		mainLayout.addComponent(lblGatePassNo, "top:55.0px; left:30.0px;");

		cmbGatePassNo = new ComboBox();
		cmbGatePassNo.setImmediate(true);
		cmbGatePassNo.setWidth("200px");
		cmbGatePassNo.setHeight("-1px");
		cmbGatePassNo.setNullSelectionAllowed(false);
		cmbGatePassNo.setNewItemsAllowed(false);
		cmbGatePassNo.setImmediate(true);
		mainLayout.addComponent(cmbGatePassNo, "top:53.0px; left:170px;");

		//CategoryAll
		chkGAtePassAll = new CheckBox("All");
		chkGAtePassAll.setHeight("-1px");
		chkGAtePassAll.setWidth("-1px");
		chkGAtePassAll.setImmediate(true);
		mainLayout.addComponent(chkGAtePassAll, "top:55.0px; left:366.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:80.0px;left:170.0px;");

		// CButton
		mainLayout.addComponent(button, "top:115.0px;left:130.0px;");

		return mainLayout;
	}
}
