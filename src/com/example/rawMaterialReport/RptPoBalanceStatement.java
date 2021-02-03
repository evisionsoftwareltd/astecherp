package com.example.rawMaterialReport;

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
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptPoBalanceStatement extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSupplierName;
	private ComboBox cmbSupplierName;
	private CheckBox chkSupplierNameAll;

	private CheckBox chkPoNoAll;
	private Label lblReceipt;
	private ComboBox cmbPONo;

	private Label lblAsOnDate;	
	private PopupDateField asOnDate;

	private SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
	
	private OptionGroup RadioBtnGroup;

	private static final List<String> type1 = Arrays.asList(new String[]{"PDF","Other"});

	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");

	ArrayList<Component> allComp = new ArrayList<Component>();

	public RptPoBalanceStatement (SessionBean sessionBean)
	{
		this.setCaption("PO BALANCE STATMENT :: "+sessionBean.getCompany());
		this.setWidth("460px");
		this.setHeight("250px");
		this.setResizable(false);
		this.sessionBean=sessionBean;

		buildMainLayout();
		setContent(mainLayout);
		cmbSupplierNameDataAdd();
		focusMove();
		actionEvent();
		cmbPONo.setEnabled(false);
		chkPoNoAll.setEnabled(false);
	}

	/**
	 * 
	 */
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

		cmbSupplierName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSupplierName.getValue()!=null)
				{
					addPONo();
					cmbPONo.setEnabled(true);
					chkPoNoAll.setEnabled(true);
				}
				else{
					
					cmbPONo.setEnabled(false);
					chkPoNoAll.setEnabled(false);
					chkPoNoAll.setValue(false);
					cmbPONo.removeAllItems();
				}
			}
		});

		chkSupplierNameAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkSupplierNameAll.booleanValue()==true)
				{
					cmbSupplierName.setValue(null);
					cmbSupplierName.setEnabled(false);
					cmbPONo.setEnabled(true);
					chkPoNoAll.setEnabled(true);
					addPONo();
				}
				else
				{
					cmbSupplierName.setEnabled(true);
					cmbPONo.setEnabled(false);
					chkPoNoAll.setEnabled(false);
					chkPoNoAll.setValue(false);
					cmbPONo.removeAllItems();
				}
			}
		});
		

		chkPoNoAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkPoNoAll.booleanValue()==true)
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
	}

	private void formValidation()
	{
		if(cmbSupplierName.getValue() != null|| chkSupplierNameAll.booleanValue() == true)
		{
			if(cmbPONo.getValue() != null || chkPoNoAll.booleanValue() == true)
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

	private void cmbSupplierNameDataAdd()
	{
		cmbSupplierName.removeAllItems();
		Transaction tx = null;
		try {
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("select a.supplierId,b.supplierName from tbRawPurchaseOrderInfo a inner join tbSupplierInfo b on a.supplierId=b.supplierId and a.vStatus like 'Active' ").list();
			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplierName.addItem(element[0].toString());
				cmbSupplierName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
	}

	public void addPONo()
	{
		cmbPONo.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String supplierid=cmbSupplierName.getValue()!=null?cmbSupplierName.getValue().toString():"%";
			String sql="select distinct 0,a.poNo from tbRawPurchaseOrderInfo a inner join tbRawPurchaseOrderDetails "
					+ "b on a.poNo=b.poNo where b.leftQty>0 and a.supplierid like '"+cmbSupplierName.getValue()+"' "
					+ "and a.vStatus like 'Active'";
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
		String PartyId = (chkSupplierNameAll.booleanValue()?"%":cmbSupplierName.getValue().toString());
		String PONo = (chkPoNoAll.booleanValue()?"%":cmbPONo.getValue().toString());
		
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

			String query = "select * from funAsOnDatePOBalance('"+PartyId+"','"+PONo+"','%','"+dateF.format(asOnDate.getValue())+"')";

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/raw/rptPoBalanceInventoryAsOn.jasper",
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
		allComp.add(cmbSupplierName);
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

		lblSupplierName = new Label("Party Name :");
		lblSupplierName.setImmediate(true);
		lblSupplierName.setWidth("-1px");
		lblSupplierName.setHeight("-1px");
		mainLayout.addComponent(lblSupplierName, "top:30.0px; left:20.0px;");

		cmbSupplierName = new ComboBox();
		cmbSupplierName.setImmediate(true);
		cmbSupplierName.setWidth("300px");
		cmbSupplierName.setHeight("24px");
		cmbSupplierName.setNullSelectionAllowed(true);
		cmbSupplierName.setNewItemsAllowed(true);
		mainLayout.addComponent(cmbSupplierName, "top:28.0px;left:100.0px;");

		chkSupplierNameAll = new CheckBox("All");
		chkSupplierNameAll.setImmediate(true);
		chkSupplierNameAll.setWidth("-1px");
		chkSupplierNameAll.setHeight("24px");
		mainLayout.addComponent(chkSupplierNameAll, "top:30.0px;left:400.0px;");

		lblReceipt = new Label("PO No :");
		lblReceipt.setImmediate(false);
		lblReceipt.setWidth("-1px");
		lblReceipt.setHeight("-1px");
		mainLayout.addComponent(lblReceipt, "top:60.0px;left:20.0px;");

		cmbPONo = new ComboBox();
		cmbPONo.setImmediate(true);
		cmbPONo.setWidth("250px");
		cmbPONo.setHeight("24px");
		cmbPONo.setNullSelectionAllowed(true);
		cmbPONo.setNewItemsAllowed(true);
		mainLayout.addComponent( cmbPONo, "top:58.0px;left:100.0px;");

		chkPoNoAll = new CheckBox("All");
		chkPoNoAll.setImmediate(true);
		chkPoNoAll.setWidth("-1px");
		chkPoNoAll.setHeight("24px");
		mainLayout.addComponent(chkPoNoAll, "top:60.0px;left:400.0px;");

		// lblAsOnDate
		lblAsOnDate = new Label();
		lblAsOnDate.setImmediate(false);
		lblAsOnDate.setWidth("-1px");
		lblAsOnDate.setHeight("-1px");
		lblAsOnDate.setValue("As On :");
		mainLayout.addComponent(lblAsOnDate, "top:90.0px;left:20.0px;");

		// asOnDate
		asOnDate= new PopupDateField();
		asOnDate.setWidth("110px");
		asOnDate.setDateFormat("dd-MM-yyyy");
		asOnDate.setValue(new java.util.Date());
		asOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent( asOnDate, "top:88.0px;left:100.0px;");


		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setValue("PDF");
		RadioBtnGroup.setStyleName("horizontal");
		//mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:170.0px;");

		mainLayout.addComponent(button,"top:150.opx; left:150.0px");

		return mainLayout;
	}
}