package com.example.productionReport;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.Transaction;


import com.common.share.FocusMoveByEnter;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
public class RptJobOrder extends Window{

	SessionBean sessionBean;
	private Label lblCustomer;
	private ComboBox cmbCustomer;
	private Label lblJobOrder;
	private ComboBox cmbJobOrder;

	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate;
	private Label lblToDate;
	private Label lblLine;
	private PopupDateField fromDate;
	private PopupDateField toDate;
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;

	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;

	public RptJobOrder(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("JOB ORDER REPORT::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		FromDataLoad();
		this.addComponent(mainLayout);
		setEventAction();
		focusMove();

		cmbCustomer.focus();
	}

	private void focusMove()
	{
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(cmbCustomer);
		focusComp.add(cmbJobOrder);
		focusComp.add(fromDate);
		focusComp.add(toDate);

		focusComp.add(previewButton);
		focusComp.add(exitButton);

		new FocusMoveByEnter(this, focusComp);
	}
	/*	private void FromDataLoad() {

		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select vRawItemCode,vRawItemName,subString(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName))as category,"
					+" vSubSubCategoryName from tbRawItemInfo where vCategoryType like '%' and vRawItemCode in(select ProductID from tbRawPurchaseDetails) order by category,vSubSubCategoryName";
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbCustomer.addItem(element[0]);
				String name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
				cmbCustomer.setItemCaption(element[0], name);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}*/

	private void FromDataLoad() {
		cmbCustomer.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			query= "  select distinct vGroupId,partyName from tbPartyInfo a" +
							" inner join" +
							" tbJobOrderInfo b" +
							" on b.partyId = a.vGroupId" +
							" order by partyName ";

			System.out.println(query);

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbCustomer.addItem(element[0].toString());
				cmbCustomer.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}



	private void setEventAction() {

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbCustomer.getValue()!=null)
				{
					if(cmbJobOrder.getValue()!=null)
					{
						reportView(); 
					}
					else
					{
						showNotification("Please Select Order No",Notification.TYPE_WARNING_MESSAGE); 
						cmbJobOrder.focus();
					} 
				}
				else
				{
					showNotification("Please Select Customer",Notification.TYPE_WARNING_MESSAGE);
					cmbCustomer.focus();
				}

			}
		});

		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAll.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbCustomer.setEnabled(false);
					cmbCustomer.setValue(null);

				}
				else{
					cmbCustomer.setEnabled(true);
					cmbCustomer.focus();
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

		cmbCustomer.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbCustomer.getValue()!=null)
				{
					System.out.println("I am Ok");
					issueToDataLoad();


				}
			}
		});

	}

	private void issueToDataLoad() {
		cmbJobOrder.removeAllItems();
		cmbJobOrder.setValue(null);
		Transaction tx=null;
		String query=null;

		try{			

				query="select orderNo,0 from tbJobOrderInfo where partyId like '"+cmbCustomer.getValue()+"' ";

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				System.out.print("Is This OK");

				Object[] element=(Object[]) iter.next();

				cmbJobOrder.addItem(element[0]);
				cmbJobOrder.setItemCaption(element[0], (String) element[0]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void reportView()
	{	
		String query=null;
		Transaction tx=null;

		if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("fromDate",new SimpleDateFormat("dd-MM-yyyy").format(fromDate.getValue()) );
			hm.put("toDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("issueFrom",cmbCustomer.getItemCaption(cmbCustomer.getValue()) );
			hm.put("issueTo",cmbCustomer.getItemCaption(cmbJobOrder.getValue()) );

			/*query= " select *,(DATEDIFF(DD,startDate,endDate)+1) as totalDays from tbJobOrderInfo a" +
					" left join" +
					" (select * from tbJobOrderDetails) b" +
					" on b.orderNo = a.orderNo" +
					" left join" +
					" (select vProductId,vProductName from tbFinishedProductInfo) c" +
					" on c.vProductId = b.fgId" +
					" left join" +
					" (select partyName,vGroupId from tbPartyInfo) d" +
					" on d.vGroupId = a.partyId "
					+"where CONVERT(Date,a.orderDate,105) between  '"+datef.format(fromDate.getValue())+"' and  '"+datef.format(toDate.getValue())+"'   and a.partyId like '"+cmbCustomer.getValue()+"' and a.orderNo like '"+cmbJobOrder.getValue()+"' ";*/
			
			/*query=" select a.poType,a.poNo ,a.orderNo,a.poDate,a.DeliveryDate,a.startDate,a.endDate,"+
					 " (DATEDIFF(DD,startDate,endDate)+1) as totalDays,a.remarks, "+
					 " a.partyId,(select partyName from tbPartyInfo where vGroupId=a.partyId)partyName,fgId,c.semiFgName,c.color,b.orderQty "+
					 " from tbJobOrderInfo a inner join tbJobOrderDetails b on a.orderNo=b.orderNo "+
					 " inner join tbSemiFgInfo c on b.fgId=c.semiFgCode  where a.partyId like '"+cmbCustomer.getValue()+"' and a.orderNo like '"+cmbJobOrder.getValue()+"'";*/
			
			query="select a.poType,a.poNo ,a.orderNo,a.poDate,a.DeliveryDate,a.startDate,a.endDate,totalDays,a.remarks,  a.partyId, "+
					" partyName,fgId,a.semiFgName,a.color,orderQty,GoodQty,rejectQty,(orderQty-GoodQty)balance,a.remarks as termsCondition,a.termsCondition1,a.termsCondition2,a.termsCondition3,a.termsCondition4  from( "+
					" select a.poType,a.poNo ,a.orderNo,a.poDate,a.DeliveryDate,a.startDate,a.endDate,  "+
					" (DATEDIFF(DD,startDate,endDate)+1) as totalDays,a.remarks,  a.partyId, "+
					" (select partyName from tbPartyInfo where vGroupId=a.partyId)partyName,fgId,c.semiFgName,c.color, "+
					" b.orderQty,(select isnull(SUM(TotalPcs),0) from tbMouldProductionDetails where jobOrderNo='"+cmbJobOrder.getValue()+"' and FinishedProduct=c.semiFgCode)GoodQty, "+
					" (select isnull(SUM(WastageQty),0) from tbMouldProductionDetails where jobOrderNo='"+cmbJobOrder.getValue()+"' and FinishedProduct=c.semiFgCode)rejectQty,a.remarks as termsCondition, "
					+ "a.termsCondition1,a.termsCondition2,a.termsCondition3,a.termsCondition4  "+
					" from tbJobOrderInfo a inner join tbJobOrderDetails b on a.orderNo=b.orderNo   "+
					" inner join tbSemiFgInfo c on b.fgId=c.semiFgCode  where a.partyId like '"+cmbCustomer.getValue()+"' and a.orderNo like '"+cmbJobOrder.getValue()+"') a";
			
			System.out.println(query);
			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptJobOrder.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}
	private AbsoluteLayout buildMainLayout() {

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("460px");
		mainLayout.setHeight("240px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("320px");

		lblCustomer = new Label();
		lblCustomer.setImmediate(false);
		lblCustomer.setWidth("-1px");
		lblCustomer.setHeight("-1px");
		lblCustomer.setValue("Party Name :");
		

		cmbCustomer = new ComboBox();
		cmbCustomer.setImmediate(true);
		cmbCustomer.setWidth("260px");
		cmbCustomer.setHeight("24px");
		cmbCustomer.setNullSelectionAllowed(true);
		cmbCustomer.setNewItemsAllowed(false);
		cmbCustomer.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		

		lblJobOrder = new Label();
		lblJobOrder.setImmediate(false);
		lblJobOrder.setWidth("-1px");
		lblJobOrder.setHeight("-1px");
		lblJobOrder.setValue("Order No :");
		

		cmbJobOrder= new ComboBox();
		cmbJobOrder.setImmediate(true);
		cmbJobOrder.setWidth("260px");
		cmbJobOrder.setHeight("24px");
		cmbJobOrder.setNullSelectionAllowed(true);
		cmbJobOrder.setNewItemsAllowed(false);
		cmbJobOrder.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		


		/*		
		chkAll.setImmediate(true);
		chkAll.setWidth("30px");
		chkAll.setHeight("15px");
		mainLayout.addComponent( chkAll, "top:18.0px;left:395.0px;");

		lblAll.setWidth("-1px");
		lblAll.setHeight("-1px");
		lblAll.setValue("All");
		mainLayout.addComponent( lblAll, "top:18.0px;left:415.0px;");*/

		lblFDate = new Label();
		lblFDate.setImmediate(false);
		lblFDate.setWidth("-1px");
		lblFDate.setHeight("-1px");
		lblFDate.setValue("From Date: ");
		
		fromDate = new PopupDateField();
		fromDate.setImmediate(true);
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setValue(new java.util.Date());
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setWidth("107px");
		fromDate.setHeight("-1px");
		fromDate.setInvalidAllowed(false);
		

		lblToDate = new Label();
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To Date: ");
		
		toDate = new PopupDateField();
		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setWidth("107px");
		toDate.setHeight("-1px");
		toDate.setInvalidAllowed(false);
		

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
		lblLine.setValue("<b><font color='#e65100'>=============================================================================================================</font></b>");
		

		previewButton.setWidth("90px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
	

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		
		
		mainLayout.addComponent(lblFDate, "top:15.0px;left:62.0px;");
		mainLayout.addComponent( fromDate, "top:12.0px;left:130.0px;");
		
		mainLayout.addComponent(lblToDate, "top:45.0px;left:62.0px;");
		mainLayout.addComponent( toDate, "top:43.0px;left:130.0px;");
		
		mainLayout.addComponent(lblCustomer, "top:75.0px;left:62.0px;");
		mainLayout.addComponent( cmbCustomer, "top:73.0px;left:130.0px;");
		
		mainLayout.addComponent(lblJobOrder, "top:105.0px;left:62.0px;");
		mainLayout.addComponent( cmbJobOrder, "top:103.0px;left:130.0px;");
		
		mainLayout.addComponent(chklayout, "top:135.0px; left:130.0px");
		mainLayout.addComponent(lblLine, "top:165.0px;left:0.0px;");
		
		mainLayout.addComponent(previewButton,"top:195.0px; left:125.0px");
		mainLayout.addComponent(exitButton,"top:195.0px; left:220.0px");

		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
