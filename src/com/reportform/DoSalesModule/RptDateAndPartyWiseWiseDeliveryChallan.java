package com.reportform.DoSalesModule;

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
public class RptDateAndPartyWiseWiseDeliveryChallan extends Window 
{	
	private SessionBean sessionBean;

	private VerticalLayout mainLayout = new VerticalLayout();
	private ComboBox cmbPartyName = new ComboBox("Party Name :");
	private ComboBox cmbPoNo = new ComboBox("PO No : ");
	private ComboBox cmbProductName = new ComboBox("Product Name :");
	private DateField dFromDate = new DateField("From Date :");
	private DateField dToDate = new DateField("To Date :");

	private FormLayout formLayout = new FormLayout();
	private FormLayout left = new FormLayout();

	private HorizontalLayout middleLayout = new  HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private OptionGroup RadioBtnGroup;
	private static final List<String> reportType = Arrays.asList(new String[]{"PDF","Other"});

	private OptionGroup RadioBtnType;
	private static final List<String> reportGroup = Arrays.asList(new String[]{"Details","Summary"});

	private CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private String frame = "";

	public RptDateAndPartyWiseWiseDeliveryChallan(SessionBean sessionBean, String frame)
	{
		if(frame.equals("dateWise"))
		{
			this.setCaption("DATE & PARTY WISE DELIVERY CHALLAN :: "+sessionBean.getCompany());
		}
		if(frame.equals("poBalance"))
		{
			dFromDate = new PopupDateField("As on date :");
			this.setCaption("AS ON DATE PO BALANCE :: "+sessionBean.getCompany());
		}
		this.sessionBean = sessionBean;
		this.frame = frame;
		this.setWidth("550px");
		this.setHeight("340px");
		this.setResizable(false);

		dFromDate.setWidth("110px");
		dToDate.setWidth("110px");
		left.setWidth("50px");

		addPartyName();

		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new Date());
		dFromDate.setImmediate(true);
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new Date());
		dToDate.setImmediate(true);
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);

		addAllComponent();
		allButtonAction();

		this.addComponent(mainLayout);		
	}

	public void allButtonAction()
	{
		button.btnPreview.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbPartyName.getValue()!=null)
				{
					if(frame.equals("dateWise") || cmbPoNo.getValue()!=null)
					{
						if(cmbProductName.getValue()!=null)
						{
							reportPreview();
						}
						else
						{
							cmbProductName.focus();
							showNotification("Warning!","Select product name.",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						cmbPoNo.focus();
						showNotification("Warning!","Select PO No.",Notification.TYPE_WARNING_MESSAGE);
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
				if(frame.equals("poBalance"))
				{
					addPoNo();
				}
				else
				{
					addProductName();
				}
			}
		});
		
		

		cmbPoNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addProductNamePo();
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
			List<?> list = session.createSQLQuery("select distinct vPartyId,vPartyName from tbDeliveryChallanInfo"+
					" order by vPartyName").list();
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

	public void addPoNo()
	{
		cmbPoNo.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> list = session.createSQLQuery("select doNo,doNo from tbDemandOrderInfo where partyId"
					+ " like '"+(cmbPartyName.getValue()==null?"":cmbPartyName.getValue().toString())+"'"
					+ " order by partyname,doDate").list();
			if(!list.isEmpty())
			{
				cmbPoNo.addItem("%");
				cmbPoNo.setItemCaption("%", "All");
				for(Iterator<?> iter = list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbPoNo.addItem(element[0]);
					cmbPoNo.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addProductName()
	{
		cmbProductName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> list = session.createSQLQuery("select vProductId,vProductName from tbFinishedProductInfo where vCategoryId in"
					+ " (select vGroupId from tbPartyInfo where partyCode like '"+(cmbPartyName.getValue()==null?"":
						cmbPartyName.getValue().toString())+"') union  select vLabelCode,vLabelName from tb3rdPartylabelInformation where vPartyId='"+cmbPartyName.getValue()+"'	 order by vProductName").list();
			if(!list.isEmpty())
			{
				cmbProductName.addItem("%");
				cmbProductName.setItemCaption("%", "All");
				for(Iterator<?> iter = list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbProductName.addItem(element[0]);
					cmbProductName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addProductNamePo()
	{
		cmbProductName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> list = session.createSQLQuery("select distinct productId, case when productId like '%FI%' then  (select vProductName from tbFinishedProductInfo"
					+ " fi where fi.vProductId = productId) else (select vLabelName from tb3rdPartylabelInformation where vLabelCode=productId)end productName from tbDemandOrderDetails where doNo like"
					+ " '"+(cmbPoNo.getValue()==null?"":cmbPoNo.getValue().toString())+"' and doNo in (select doNo"
					+ " from tbDemandOrderInfo where partyId like '"+(cmbPartyName.getValue()==null?"":
						cmbPartyName.getValue().toString())+"') order by productName").list();
			if(!list.isEmpty())
			{
				cmbProductName.addItem("%");
				cmbProductName.setItemCaption("%", "All");
				for(Iterator<?> iter = list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbProductName.addItem(element[0]);
					cmbProductName.setItemCaption(element[0], element[1].toString());
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
		try
		{
			ReportDate reportTime = new ReportDate();
			String query = "";
			String jasper = "";
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("userName", sessionBean.getUserName());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("fromDate",dFromDate.getValue());
			hm.put("toDate",dToDate.getValue());
			hm.put("logo",sessionBean.getCompanyLogo());

			if(frame.equals("dateWise"))
			{
				if(RadioBtnType.getValue().toString().equals("Details"))
				{
					

					  query="select dChallanDate,vPartyId,vPartyName,vProductId,LTRIM(RTRIM(vProductName))vProductName,vProductUnit,mChallanQty,mProductRate, "
												 +"(mChallanQty*mProductRate)mAmount,dcd.vChallanNo,vDelChallanNo,vVatChallanNo,dcd.vDoNo,dDoDate,CONVERT(time,dEntryTime) " 
												 +"dTime,vTruckNo, case when dcd.vProductId like '%FI%' then   isnull(case when CONVERT(money,(select ISNULL(vSizeName,0)from tbFinishedProductInfo fi " 
												 +"where fi.vProductId = dcd.vProductId))>0 then FLOOR(mChallanQty/CONVERT(money,(select ISNULL(vSizeName,0) "
												 +"from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId)))end,0) else isnull(case when CONVERT(money,(select ISNULL(vSizeName,0)from tb3rdPartylabelInformation "  
												 +"where tb3rdPartylabelInformation.vLabelCode = dcd.vProductId))>0 then FLOOR(mChallanQty/CONVERT(money,(select ISNULL(vSizeName,0) "
												 +"from tb3rdPartylabelInformation  where vLabelCode = dcd.vProductId)))end,0) end  vBox,case when dcd.vProductId like '%FI%' then  isnull(case when CONVERT(money,(select ISNULL(vSizeName,0) "
												 +"from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId))>0 then FLOOR(mChallanQty%CONVERT(money,(select ISNULL(vSizeName,0) "
												 +"from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId)))end,0) else isnull(case when CONVERT(money,(select ISNULL(vSizeName,0) "
												 +"from tb3rdPartylabelInformation  where vLabelCode = dcd.vProductId))>0 then FLOOR(mChallanQty%CONVERT(money,(select ISNULL(vSizeName,0) "
												 +"from tb3rdPartylabelInformation  where vLabelCode = dcd.vProductId)))end,0) end vPcs from tbDeliveryChallanInfo dci "
												 +" inner join tbDeliveryChallanDetails dcd on dci.vChallanNo = dcd.vChallanNo where vPartyId like "  
												 +" '"+cmbPartyName.getValue().toString()+"' and CONVERT(date,dci.dChallanDate) between "
												 +" '"+sessionBean.dfDb.format(dFromDate.getValue())+"' and '"+sessionBean.dfDb.format(dToDate.getValue())+"' and mChallanQty > 0 and vProductId like "
												 +" '"+cmbProductName.getValue().toString()+"' order by vPartyName,vProductName,dChallanDate ";
					
					
					
					/*query = " select dChallanDate,vPartyId,vPartyName,vProductId,vProductName,vProductUnit,mChallanQty,mProductRate," +
							" (mChallanQty*mProductRate)mAmount,dcd.vChallanNo,vDelChallanNo,vVatChallanNo,dcd.vDoNo,dDoDate,CONVERT(time,dEntryTime) " +
							" dTime,vTruckNo,isnull(case when CONVERT(money,(select ISNULL(vSizeName,0)from tbFinishedProductInfo fi " +
							" where fi.vProductId = dcd.vProductId))>0 then FLOOR(mChallanQty/CONVERT(money,(select ISNULL(vSizeName,0)" +
							" from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId)))end,0) vBox,isnull(case when CONVERT(money,(select ISNULL(vSizeName,0)" +
							" from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId))>0 then FLOOR(mChallanQty%CONVERT(money,(select ISNULL(vSizeName,0)" +
							" from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId)))end,0) vPcs from tbDeliveryChallanInfo dci" +
							" inner join tbDeliveryChallanDetails dcd on dci.vChallanNo = dcd.vChallanNo where vPartyId like  " +
							" '"+cmbPartyName.getValue().toString()+"' and CONVERT(date,dci.dChallanDate) between" +
							" '"+sessionBean.dfDb.format(dFromDate.getValue())+"' and '"+sessionBean.dfDb.format(dToDate.getValue())+"' and mChallanQty > 0 and vProductId like "+
							" '"+cmbProductName.getValue().toString()+"' order by vPartyName,vProductName,dChallanDate";
					*/
					
					
					jasper = "report/account/DoSales/rptDateAndPartyWiseDeliveryChallan.jasper";
				
				}
				else if(RadioBtnType.getValue().toString().equals("Summary"))
				{
					/*query = " select vProductId ,vProductName ,vPartyId, vPartyName ,ISNULL(SUM(mChallanQty),0)mChallanQty ," +
							" ISNULL(SUM(vBox),0)vBox, ISNULL(SUM(mAmount),0)mAmount, ISNULL(SUM(vPcs),0)vPcs, mProductRate  " +
							" from (select dChallanDate,vPartyId,vPartyName,vProductId,vProductName,vProductUnit,mChallanQty,mProductRate, (mChallanQty*mProductRate)mAmount," +
							"vDelChallanNo,vVatChallanNo,dcd.vDoNo,dDoDate,CONVERT(time,dEntryTime)  dTime,vTruckNo,isnull(case when CONVERT(money,(select ISNULL(vSizeName,0)from " +
							"tbFinishedProductInfo fi  where fi.vProductId = dcd.vProductId))>0 then FLOOR(mChallanQty/CONVERT(money,(select ISNULL(vSizeName,0) " +
							"from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId)))end,0) vBox,isnull(case when CONVERT(money,(select ISNULL(vSizeName,0) " +
							"from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId))>0 then FLOOR(mChallanQty%CONVERT(money,(select ISNULL(vSizeName,0) " +
							"from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId)))end,0) vPcs from tbDeliveryChallanInfo dci " +
							"inner join tbDeliveryChallanDetails dcd on dci.vChallanNo = dcd.vChallanNo where vPartyId like '"+cmbPartyName.getValue().toString()+"' and " +
							"CONVERT(date,dci.dChallanDate) between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' and '"+sessionBean.dfDb.format(dToDate.getValue())+"' and mChallanQty > 0 and vProductId like  '"+cmbProductName.getValue().toString()+"' ) " +
							"as temptable  group by vProductId ,vProductName ,vPartyId, vPartyName, mProductRate order by vPartyName,vProductName ";
					*/
					query= 
							"select vProductId ,LTRIM(RTRIM(vProductName))vProductName ,vPartyId, vPartyName ,ISNULL(SUM(mChallanQty),0)mChallanQty , "
							+"ISNULL(SUM(vBox),0)vBox, ISNULL(SUM(mAmount),0)mAmount, ISNULL(SUM(vPcs),0)vPcs, mProductRate "  
							+"from (select dChallanDate,vPartyId,vPartyName,vProductId,vProductName,vProductUnit,mChallanQty,mProductRate, (mChallanQty*mProductRate)mAmount, "
							+"vDelChallanNo,vVatChallanNo,dcd.vDoNo,dDoDate,CONVERT(time,dEntryTime)  dTime,vTruckNo,case when dcd.vProductId like '%FI%' then  isnull(case when CONVERT(money,(select ISNULL(vSizeName,0)from " 
							+"tbFinishedProductInfo fi  where fi.vProductId = dcd.vProductId))>0 then FLOOR(mChallanQty/CONVERT(money,(select ISNULL(vSizeName,0) " 
							+"from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId)))end,0) else isnull(case when CONVERT(money,(select ISNULL(vSizeName,0)from "
							+"tb3rdPartylabelInformation   where tb3rdPartylabelInformation.vLabelCode = dcd.vProductId))>0 then FLOOR(mChallanQty/CONVERT(money,(select ISNULL(vSizeName,0) " 
							+"from tb3rdPartylabelInformation  where tb3rdPartylabelInformation.vLabelCode = dcd.vProductId)))end,0) end vBox,case when dcd.vProductId like '%FI%' then  isnull(case when CONVERT(money,(select ISNULL(vSizeName,0) " 
							+"from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId))>0 then FLOOR(mChallanQty%CONVERT(money,(select ISNULL(vSizeName,0) " 
							+"from tbFinishedProductInfo fi where fi.vProductId = dcd.vProductId)))end,0) else isnull(case when CONVERT(money,(select ISNULL(vSizeName,0) " 
							+"from tb3rdPartylabelInformation  where vLabelCode = dcd.vProductId))>0 then FLOOR(mChallanQty%CONVERT(money,(select ISNULL(vSizeName,0) " 
							+"from tb3rdPartylabelInformation  where vLabelCode = dcd.vProductId)))end,0) end  vPcs from tbDeliveryChallanInfo dci " 
							+"inner join tbDeliveryChallanDetails dcd on dci.vChallanNo = dcd.vChallanNo where vPartyId like '"+cmbPartyName.getValue().toString()+"' and " 
							+"CONVERT(date,dci.dChallanDate) between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' and '"+sessionBean.dfDb.format(dToDate.getValue())+"' and mChallanQty > 0 and vProductId like  '"+cmbProductName.getValue().toString()+"' ) " 
							+"as temptable  group by vProductId ,vProductName ,vPartyId, vPartyName, mProductRate order by vPartyName,vProductName "; 
					
					
					
					jasper = "report/account/DoSales/rptDateAndPartyWiseDeliveryChallanSummary.jasper";
				
				}
			}
			else if(frame.equals("poBalance"))
			{
				query = "select * from funAsOnDateDO('"+cmbPartyName.getValue().toString()+"',"
						+ "'"+cmbPoNo.getValue().toString()+"','"+cmbProductName.getValue().toString()+"',"
						+ "'"+(sessionBean.dfDb.format(dFromDate.getValue()))+"') where mBalanceQty>0 order by vPartyName,vDoDate,vDoNo  ";
				jasper = "report/account/DoSales/rptAsOnDateWisePO.jasper";
			}
			System.out.println(query);
			hm.put("sql", query);
			Window win = new ReportViewer(hm,jasper,
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",
					(RadioBtnGroup.getValue().toString().equals("PDF")?true:false));

			win.setCaption("Project Report");
			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			showNotification("Error!",""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addAllComponent()
	{
		cmbPartyName.setImmediate(true);
	    cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbPartyName.setWidth("280px");
		formLayout.addComponent(cmbPartyName);
		cmbProductName.setWidth("330px");

		formLayout.addComponent(cmbPoNo);
		cmbPoNo.setWidth("280px");
		cmbPoNo.setImmediate(true);

		formLayout.addComponent(cmbProductName);
		cmbProductName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbProductName.setImmediate(true);

		formLayout.addComponent(dFromDate);
		formLayout.addComponent(dToDate);
		RadioBtnType = new OptionGroup("",reportGroup);
		RadioBtnType.setImmediate(true);
		RadioBtnType.setStyleName("horizontal");
		RadioBtnType.setValue("Details");
		formLayout.addComponent(RadioBtnType);

		RadioBtnGroup = new OptionGroup("",reportType);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		formLayout.addComponent(RadioBtnGroup);

		formLayout.setComponentAlignment(cmbPartyName, Alignment.BOTTOM_LEFT);
		formLayout.setComponentAlignment(cmbPoNo, Alignment.BOTTOM_LEFT);
		formLayout.setComponentAlignment(dFromDate, Alignment.BOTTOM_LEFT);
		formLayout.setComponentAlignment(dToDate, Alignment.BOTTOM_LEFT);

		if(frame.equals("dateWise"))
		{
			cmbPoNo.setVisible(false);
		}
		if(frame.equals("poBalance"))
		{
			dToDate.setVisible(false);
			RadioBtnType.setVisible(false);
		}

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