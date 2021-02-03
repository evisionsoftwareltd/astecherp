package com.reportform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class RptPurchaseOrderCancel extends Window
{
	private SessionBean sessionBean;

	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout verLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
	private GridLayout grid = new GridLayout(2,1);

	private PopupDateField dFrom;
	private PopupDateField dTo;
	private VerticalLayout space = new VerticalLayout();

	private Label lblSpace = new Label("");

	private ComboBox cmbPartyName = new ComboBox("Party Name :");
	private CheckBox chkAmount = new CheckBox("With Value");

	private OptionGroup RadioBtnGroup;
	//private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});

	private OptionGroup RadioBtnJoPo;
	//private static final List<String> optionJoPo = Arrays.asList(new String[]{"PO","JO"});

	private String frmName = "";

	public RptPurchaseOrderCancel(SessionBean sessionBean, String frmName)
	{
		this.sessionBean = sessionBean;
		this.frmName = frmName;
		this.setWidth("500px");
		this.setHeight("280px");
		this.setResizable(false);

		dFrom = new PopupDateField("From Date :");
		dFrom.setValue(new java.util.Date());
		dFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dFrom.setImmediate(true);
		dFrom.setDateFormat("dd-MM-yyyy");
		dFrom.setWidth("110px");
		formLayout.addComponent(dFrom);

		dTo = new PopupDateField("To Date :");
		dTo.setValue(new java.util.Date());
		dTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dTo.setImmediate(true);
		dTo.setDateFormat("dd-MM-yyyy");
		dTo.setWidth("110px");
		formLayout.addComponent(dTo);
		formLayout.addComponent(chkAmount);
		chkAmount.setImmediate(true);

		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("300px");
		cmbPartyName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		formLayout.addComponent(cmbPartyName);

		/*RadioBtnJoPo = new OptionGroup("",optionJoPo);
		RadioBtnJoPo.setImmediate(true);
		RadioBtnJoPo.setStyleName("horizontal");
		RadioBtnJoPo.setValue("PO");
		formLayout.addComponent(RadioBtnJoPo);*/

		chkAmount.setVisible(false);
		this.setCaption("PURCHASE ORDER CANCEL REPORT :: "+ this.sessionBean.getCompany());
	
		/*RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		formLayout.addComponent(RadioBtnGroup);*/

		lblSpace.setHeight("15px");
		formLayout.addComponent(lblSpace);

		btnLayout.addComponent(button);

		verLayout.addComponent(space);
		//verLayout.setSpacing(true);
		space.setHeight("42px");
		//space.setSpacing(true);
		grid.addComponent(formLayout,0,0);
		grid.addComponent(verLayout,1,0);
		mainLayout.addComponent(grid);
		mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);

		this.addComponent(mainLayout);		
		setButtonAction();
	}
	public void addPartyName()
	{
		cmbPartyName.removeAllItems();
		cmbPartyName.addItem("%");
		cmbPartyName.setItemCaption("%", "All");

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> lst = session.createSQLQuery("select vPartyId,vPartyName from tbPurchaseOrderCancel where dPoDate between  '"+dateF.format(dFrom.getValue())+"' and '"+dateF.format(dTo.getValue())+"' order by vPartyName ").list();
			for(Iterator<?> iter = lst.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0]);
				cmbPartyName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
		finally{session.close();}
	}

	private void setButtonAction()
	{
		dFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
			addPartyName();
			}
		});
		dTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
			addPartyName();
			}
		});
	
		button.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbPartyName.getValue()!=null)
				{
					showReport();
				}
				else
				{
					showNotification("Warning!","Select Party Name",Notification.TYPE_WARNING_MESSAGE);
					cmbPartyName.focus();
				}
			}
		});

		button.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}

	private void showReport()
	{
		String jasper = "";
//		ReportOption RadioBtn = new ReportOption(RadioBtnGroup.getValue().toString());
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("company",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("fromDate", dFrom.getValue());
			hm.put("toDate", dTo.getValue());
			hm.put("flag", (frmName.equals("Date Wise")?"2":"1"));
			hm.put("userName", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("partyName",cmbPartyName.getItemCaption(cmbPartyName.getValue().toString()));
			String sql = "";


				sql = " select vCancelId,vPartyId,vPartyName,vPoNo,dPoDate,vProductId,vProductName,vProductUnit,"+
						" mTotalQty,mDeliveredQty,mBalanceQty,vReason,vUserName,dEntryTime from tbPurchaseOrderCancel"+
						" where mBalanceQty>0 and CONVERT(date,dEntryTime) between '"+sessionBean.dfDb.format(dFrom.getValue())+"' and"+
						" '"+sessionBean.dfDb.format(dTo.getValue())+"' and vPartyId like '"+cmbPartyName.getValue().toString()+"'"+
						"  order by vPartyName,dPoDate,vPoNo,vProductName";
				jasper = "report/account/DoSales/rptPuchaseOrderCancel.jasper";
			
			if(queryValueCheck(sql))
			{
				//hm.put("po/jo", RadioBtnJoPo.getValue().toString());
				hm.put("sql",sql);
				Window win = new ReportViewer(hm,jasper,
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				win.setCaption("Project Report");
			}
			else
			{
				showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}
}