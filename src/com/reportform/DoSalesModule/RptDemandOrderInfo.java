package com.reportform.DoSalesModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptDemandOrderInfo extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblPartyName;
	private ComboBox cmbPartyName;

	private CheckBox chkReceiptNo;
	private Label lblReceipt;
	private ComboBox cmbPONo;

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1 = Arrays.asList(new String[]{"PDF","Other"});

	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");

	ArrayList<Component> allComp = new ArrayList<Component>();

	public RptDemandOrderInfo (SessionBean sessionBean)
	{
		this.setCaption("PURCHASE ORDER REPORT :: "+sessionBean.getCompany());
		this.setWidth("460px");
		this.setHeight("250px");
		this.setResizable(false);
		this.sessionBean=sessionBean;

		buildMainLayout();
		setContent(mainLayout);

		addSupplierName();
		focusMove();
		actionEvent();
	}

	public void actionEvent()
	{
		button.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		chkReceiptNo.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkReceiptNo.booleanValue()==true)
				{
					cmbPONo.setValue(null);
					cmbPONo.setEnabled(false);
				}
				else
				{
					cmbPONo.setEnabled(true);
				}
			}
		});

		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPartyName.getValue()!=null)
				{
					addPONo();
				}
			}
		});
	}

	private void formValidation()
	{
		if(cmbPartyName.getValue() != null)
		{
			if(cmbPONo.getValue() != null || chkReceiptNo.booleanValue() == true)
			{
				reportPreview();
			}
			else
			{
				showNotification("Warning!","Select PO No",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning!","Select Supplier Name",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public void addSupplierName()
	{
		cmbPartyName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = " select distinct partyId,partyname from tbDemandOrderInfo order by partyname";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void addPONo()
	{
		cmbPONo.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = " select 0,doNo from tbDemandOrderInfo where partyId = '"+cmbPartyName.getValue()+"' ";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPONo.addItem(element[1]);
				cmbPONo.setItemCaption(element[1], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void reportPreview()
	{
		ReportDate reportTime = new ReportDate();
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String PONo = (chkReceiptNo.booleanValue()?"%":cmbPONo.getValue().toString());
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("Date",reportTime.getTime);

			String query = " Select DO.doNo,DO.doDate,DO.deiveryDate,DO.commission,DO.note,DOD.remarks,DO.partyName,"
					+ " DO.address,DO.mobile,DOD.productId, case when DOD.productId like '%FI%' then (select vProductName from tbFinishedProductInfo fi where"
					+ " fi.vProductId = DOD.productId) else (select vLabelName from tb3rdPartylabelInformation where vLabelCode=DOD.productId) end productName,DOD.unit,DOD.rate,DOD.qty,DOD.amount,PI.DivisionName,"
					+ "PI.AreaName,AI.vEmployeeName,REPLACE(BankId,'D:/Tomcat 7.0/webapps/', '') attachPO,DO.vApproveBy"
					+ ",DO.note2,DO.note3,DO.note4,DO.note5 from tbDemandOrderInfo as DO left join tbDemandOrderDetails as DOD on Do.doNo = DOD.doNo left"
					+ " Join tbPartyInfo as PI on Pi.partyCode = DO.partyId left join tbAreaInfo as AI on Ai.vAreaId ="
					+ " PI.AreaId where DO.doNo like '"+PONo+"' and DO.partyId = '"+cmbPartyName.getValue()+"'"
					+ " order by DO.doDate,productName ";

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/DoSales/rptDemadOrderList.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);
				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{return true;}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private void focusMove()
	{
		allComp.add(cmbPartyName);
		allComp.add(cmbPONo);
		allComp.add(button.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("450px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		lblPartyName = new Label("Party Name : ");
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		mainLayout.addComponent(lblPartyName, "top:30.0px; left:20.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("300px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setNewItemsAllowed(true);
		mainLayout.addComponent(cmbPartyName, "top:28.0px;left:115.0px;");

		lblReceipt = new Label("PO No :");
		lblReceipt.setImmediate(false);
		lblReceipt.setWidth("-1px");
		lblReceipt.setHeight("-1px");
		mainLayout.addComponent(lblReceipt, "top:70.0px;left:20.0px;");

		cmbPONo = new ComboBox();
		cmbPONo.setImmediate(true);
		cmbPONo.setWidth("250px");
		cmbPONo.setHeight("24px");
		cmbPONo.setNullSelectionAllowed(true);
		cmbPONo.setNewItemsAllowed(true);
		mainLayout.addComponent( cmbPONo, "top:68.0px;left:115.0px;");

		chkReceiptNo = new CheckBox("All");
		chkReceiptNo.setImmediate(true);
		chkReceiptNo.setWidth("-1px");
		chkReceiptNo.setHeight("24px");
		mainLayout.addComponent(chkReceiptNo, "top:70.0px;left:370.0px;");

		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setValue("PDF");
		RadioBtnGroup.setStyleName("horizontal");
		mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:170.0px;");

		mainLayout.addComponent(button,"top:150.opx; left:150.0px");

		return mainLayout;
	}
}