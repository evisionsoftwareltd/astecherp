package com.reportform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptChallan extends Window
{
	private SessionBean sessionBean;

	private VerticalLayout mainLayout = new VerticalLayout();
	private ComboBox cmbPartyName = new ComboBox("Party Name:");
	private ComboBox cmbChallanNo = new ComboBox("Challan No:");
	private DateField dFromDate = new DateField("From Date :");
	private DateField dToDate = new DateField("To Date :");

	private FormLayout formLayout = new FormLayout();
	private FormLayout left = new FormLayout();

	private HorizontalLayout middleLayout = new  HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private OptionGroup RadioBtnGroup;
	private static final List<String> reportType = Arrays.asList(new String[]{"PDF","Other"});

	private OptionGroup RadioBtnType;
	private static final List<String> reportGroup = Arrays.asList(new String[]{"Single Copy","Multiple Copy"});

	private CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	public RptChallan(SessionBean sessionBean)
	{
		this.setCaption("DELIVERY CHALLAN REPORT:: "+sessionBean.getCompany());
		this.sessionBean = sessionBean;
		this.setWidth("550px");
		this.setHeight("350px");
		this.setResizable(false);

		dFromDate.setWidth("110px");
		dToDate.setWidth("110px");
		left.setWidth("50px");

		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new Date());
		dFromDate.setImmediate(true);
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new Date());
		dToDate.setImmediate(true);
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);

		cmbPartyName.setImmediate(true);
		cmbChallanNo.setImmediate(true);
		cmbChallanNo.setWidth("250px");

		addAllComponent();
		allButtonAction();
		this.addComponent(mainLayout);

		addPartyName();
	}

	public void allButtonAction()
	{
		button.btnPreview.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbPartyName.getValue()!=null)
				{
					if(cmbChallanNo.getValue()!=null)
					{
						reportPreview();
					}
					else
					{
						cmbChallanNo.focus();
						showNotification("Warning!","Select product name.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					cmbPartyName.focus();
					showNotification("Warning!","Select party name.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addChallanNo();
			}
		});

		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addChallanNo();
			}
		});

		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addChallanNo();
			}
		});

		RadioBtnType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addChallanNo();
			}
		});
	}
	public void addPartyName()
	{
		cmbPartyName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> list = session.createSQLQuery("select distinct vPartyId,vPartyName from"
					+ " tbDeliveryChallanInfo order by vPartyName").list();
			if(!list.isEmpty())
			{
				cmbPartyName.addItem("%");
				cmbPartyName.setItemCaption("%", "All");
				for(Iterator<?> iter = list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbPartyName.addItem(element[0]);
					cmbPartyName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addChallanNo()
	{
		cmbChallanNo.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> list = session.createSQLQuery(" select iAutoId,vChallanNo,vDelChallanNo,vVatChallanNo from tbDeliveryChallanInfo where"
					+ " convert(date,dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"'"
					+ " and vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' order by vChallanNo").list();
			if(!list.isEmpty())
			{
			if(RadioBtnType.getValue().toString().equals("Single Copy"))
				{
					cmbChallanNo.addItem("%");
					cmbChallanNo.setItemCaption("%", "All");
				}
				for(Iterator<?> iter = list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbChallanNo.addItem(element[1]);
					cmbChallanNo.setItemCaption(element[1], element[1].toString()+" > "+element[2].toString()+" > "+element[3].toString());
					
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	
	private void reportPreview()
	{
		ReportDate reportTime = new ReportDate();
		try
		{
			String query = "";
			HashMap<String, Object> hm = new HashMap<String, Object>();

		if(!RadioBtnType.getValue().toString().equals("Single Copy"))
			{	
			
			query=   "select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
			         +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
			         +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 1' vCopy,'DELIVERY CHALLAN'  "
			         +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
			         +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
			         +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
			         +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
			         +"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
				     +"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' " 
				     
				     +"union all "
				     
				     +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
			         +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
			         +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 2' vCopy,'DELIVERY CHALLAN'  "
			         +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
			         +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
			         +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
			         +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
			         +"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
				     +"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' " 
				     +"union all"
				     
                     +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
                     +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
                     +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 3' vCopy,'DELIVERY CHALLAN'  "
                     +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                     +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
                     +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
                     +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                     +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
                     +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
                     +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
                     +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
                     +"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
                     +"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' " 
                     +"union all"
                     
                     +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
                     +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
                     +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Head Office Copy' vCopy,'DELIVERY CHALLAN'  "
                     +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                     +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
                     +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
                     +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                     +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
                     +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
                     +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
                     +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
                     +"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
                     +"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' " 
                     +"union all"
                     
                    +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
                    +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
                    +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'' vCopy,'GATE PASS'  "
                    +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                    +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
                    +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
                    +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                    +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
                    +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
                    +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
                    +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
                    +"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
                    +"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' " ;
 
				     
			
		/*	query=  "select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else ( select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId) end productCode,DCD.mChallanQty,DCD.vProductUnit, isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1,DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
					+"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					+"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 1' vCopy,'DELIVERY CHALLAN' "
					+"rptHeader,1 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					+"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					+"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					+"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
					+"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) ,'') " 
					+"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' " 
					+"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
					+"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' " 
					 
					+"union all " 

					+"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else ( select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId) end productCode,DCD.mChallanQty,DCD.vProductUnit, isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
					+"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					+"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 2' vCopy,'DELIVERY CHALLAN' "
					+"rptHeader,2 serial,ISNULL(case when CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					+"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					+"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					+"then DCD.mChallanQty%CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
					+"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) ,'') "
					+"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
					+"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
					+"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"'  "
					 
					+"union all" 

					+"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else ( select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId) end productCode,DCD.mChallanQty,DCD.vProductUnit,isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
					+"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					+"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 3' vCopy,'DELIVERY CHALLAN' "  
					+"rptHeader,3 serial,ISNULL(case when CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					+"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					+"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					+"then DCD.mChallanQty%CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
					+"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) ,'') "
					+"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
					+"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
					+"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' " 
					 
					+"union all " 

                    +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else ( select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId) end productCode,DCD.mChallanQty,DCD.vProductUnit,isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
                    +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
                    +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Head Office Copy' vCopy,'DELIVERY CHALLAN' "
                    +"rptHeader,4 serial,ISNULL(case when CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                    +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
                    +"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
                    +"then DCD.mChallanQty%CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs,"
                    +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 
                    +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
					+"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
					+"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"'  "
					+"union all" 

					+"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else ( select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId) end productCode,DCD.mChallanQty,DCD.vProductUnit,isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode,"
					+"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					+"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'' as vCopy,'GATE PASS' " 
					+"rptHeader,5 serial,ISNULL(case when CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					+"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					+"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "  
					+"then DCD.mChallanQty%CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
					+"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 
					+"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
					+"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
					+"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' order by serial,DCD.vProductName ";
		*/
			}
		else
		{
			/*query = "select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,(select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) productCode,DCD.mChallanQty,DCD.vProductUnit,(select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId) vBox1,DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode," +
					"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo," +
					"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCD.vRemarks,'Customer Copy' vCopy,'DELIVERY CHALLAN'" +
					" rptHeader,1 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " +
					"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox," +
					"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0" +
					"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs," +
					"(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) " +
					" from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"'"
					+ " and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"'"
					+ " and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' order by vChallanNo";
		*/
			
			/*query= "select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else ( select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId) end productCode,DCD.mChallanQty,DCD.vProductUnit, isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1,DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
				   +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
				   +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCD.vRemarks,'Customer Copy' vCopy,'DELIVERY CHALLAN' "
				   +"rptHeader,1 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
				   +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
				   +"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
				   +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
				   +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) ,'') " 
				   +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' " 
				   +"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
				   +"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' order by vChallanNo ";
			*/
			
			query=   "select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
			         +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
			         +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy' vCopy,'DELIVERY CHALLAN'  "
			         +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
			         +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
			         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
			         +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
			         +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' "
			         +"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
				     +"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' order by vChallanNo " ;
			
			/*query= "select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else ( select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId) end productCode,DCD.mChallanQty,DCD.vProductUnit, isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1,DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
					   +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					   +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCD.vRemarks,'Customer Copy' vCopy,'DELIVERY CHALLAN' "
					   +"rptHeader,1 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					   +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					   +"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					   +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
					   +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) ,'') " 
					   +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where convert(date,DCI.dChallanDate) between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"' " 
					   +"and DCI.vPartyId like '"+(cmbPartyName.getValue()!=null?cmbPartyName.getValue().toString():"")+"' "
					   +"and DCI.vChallanNo like '"+(cmbChallanNo.getValue()!=null?cmbChallanNo.getValue().toString():"")+"' order by vChallanNo ";
				
			
			*/
			
		
		
		}
			

			System.out.println(query);

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("user", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("userName", sessionBean.getUserName());
			hm.put("copy",RadioBtnType.getValue().toString());
			hm.put("Date",reportTime.getTime);
			hm.put("Author", sessionBean.getUserName());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("sql", query);
			
			Window win = new ReportViewer(hm,"report/account/DoSales/rptDeliveryChallan.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
			win.setStyleName("cwindow");
			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void addAllComponent()
	{
		RadioBtnType = new OptionGroup("",reportGroup);
		RadioBtnType.setImmediate(true);
		RadioBtnType.setStyleName("horizontal");
		RadioBtnType.setValue("Single Copy");

		cmbPartyName.setWidth("330px");
		formLayout.addComponent(cmbPartyName);
		formLayout.addComponent(dFromDate);
		formLayout.addComponent(dToDate);
		formLayout.addComponent(RadioBtnType);
		formLayout.addComponent(cmbChallanNo);
		cmbChallanNo.setWidth("330px");
		cmbChallanNo.setDescription("System Challan No > Delivery Challan No > Vat Challan No");

		RadioBtnGroup = new OptionGroup("",reportType);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		formLayout.addComponent(RadioBtnGroup);

		formLayout.setComponentAlignment(cmbPartyName, Alignment.BOTTOM_LEFT);
		formLayout.setComponentAlignment(dFromDate, Alignment.BOTTOM_LEFT);
		formLayout.setComponentAlignment(dToDate, Alignment.BOTTOM_LEFT);
		formLayout.setComponentAlignment(RadioBtnType, Alignment.BOTTOM_LEFT);
		formLayout.setComponentAlignment(cmbChallanNo, Alignment.BOTTOM_LEFT);

		mainLayout.setSpacing(true);
		middleLayout.addComponent(left);
		middleLayout.addComponent(formLayout);

		mainLayout.addComponent(middleLayout);

		btnLayout.addComponent(button);

		btnLayout.setSpacing(true);
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		addComponent(mainLayout);
	}
}