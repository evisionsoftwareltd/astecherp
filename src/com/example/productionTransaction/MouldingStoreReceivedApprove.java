package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.productionReport.RptDailyProduction;
import com.example.productionReport.RptMouldingDailyProduction;
import com.example.productionReport.RptRequisitionProduction0;
import com.vaadin.data.Property.*;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.*;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class MouldingStoreReceivedApprove extends Window {

	private static final long serialVersionUID = -3672729251787238695L;
	SessionBean sessionBean;
	AbsoluteLayout mainLayout;
	Label lblFromDate,lblToDate,lblDate,lblProductionType,lblProductionStep;
	PopupDateField dateFrom,dateTo,dateCur;
	ComboBox cmbProductionType;
	private NativeButton btnDailyPrdReport = new NativeButton("Daily Production Report");
	private NativeButton btnLoadData = new NativeButton("Load Data");

	private NativeButton btnApproved = new NativeButton("Approved");
	private NativeButton btnRefresh = new NativeButton("Refresh");
	private NativeButton btnExit = new NativeButton("Exit");

	ArrayList<Label>tbSl = new ArrayList<Label>();
	ArrayList<CheckBox>tbChk=new ArrayList<CheckBox>();
	ArrayList<CheckBox>tbisFG=new ArrayList<CheckBox>();
	ArrayList<Label>tbProductId=new ArrayList<Label>();
	ArrayList<Label>tbProductName=new ArrayList<Label>();
	ArrayList<Label>tbColor=new ArrayList<Label>();
	ArrayList<Label>tbUnit=new ArrayList<Label>();
	ArrayList<Label>tbStdWeight=new ArrayList<Label>();
	ArrayList<TextRead>tbPrdQty=new ArrayList<TextRead>(1);

	Table table=new Table();

	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0");
	DecimalFormat dfstdWeight=new DecimalFormat("#0.000");
	CommaSeparator coma=new CommaSeparator();
	int count=0;
	
	public MouldingStoreReceivedApprove(SessionBean sesionBean){
		this.sessionBean=sesionBean;
		this.setResizable(false);
		this.setCaption("STORE RECEIVED APPROVE [Moulding] :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();
		setEventAction();
		cmbProductionTypeData();
	}
	private void cmbProductionTypeData() {
		cmbProductionType.removeAllItems();
		Iterator iter=dbService("select productTypeId,productTypeName from tbProductionType where productTypeId in('PT-1','PT-2','PT-4') order by productTypeName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}
	}
	private void tableDataLoad(){
		String sql="select b.FinishedProduct,c.semiFgName,c.color,c.unit,c.stdWeight,isnull(sum(b.TotalPcs),0)TotalPcs "+
				" from tbMouldProductionInfo a  "+
				" inner join tbMouldProductionDetails b "+ 
				" inner join tbSemiFgInfo c on b.FinishedProduct=c.semiFgCode "+
				" on a.ProductionNo=b.ProductionNo "+
				" where a.productionType='"+cmbProductionType.getValue()+"' and convert(date,a.ProductionDate,105)='"+dateFormat.format(dateFrom.getValue())+"'  and b.isApproved=0 "+ 
				" group by b.FinishedProduct,c.semiFgName,c.color,c.unit,c.stdWeight order by semiFgName";
		Iterator iter=dbService(sql);
		int a=0;
		if(iter.hasNext()){
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				
				tbProductId.get(a).setValue(element[0]);
				tbProductName.get(a).setValue(element[1]);
				tbChk.get(a).setValue(true);
				tbColor.get(a).setValue(element[2]);
				tbUnit.get(a).setValue(element[3]);
				tbStdWeight.get(a).setValue(dfstdWeight.format(element[4]));
				tbPrdQty.get(a).setValue(coma.setComma(Double.parseDouble(df.format(element[5]))));
				a++;
				if(a==tbChk.size()-1){
					tableRowAdd(a+1);
				}
			}
		}
		else{
			showNotification("Sorry!!","There are no Data",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void tableClear(){
		for(int a=0;a<tbChk.size();a++){
			tbChk.get(a).setValue(false);
			tbisFG.get(a).setValue(false);
			tbProductId.get(a).setValue("");
			tbProductName.get(a).setValue("");
			tbColor.get(a).setValue("");
			tbUnit.get(a).setValue("");
			tbStdWeight.get(a).setValue("");
			tbPrdQty.get(a).setValue("");
		}
	}
	private void setEventAction() {
		/*table.addListener(new HeaderClickListener() {

			public void headerClick(HeaderClickEvent event) {

				if(count==0){
					for(int a=0;a<tbChk.size();a++){
						if(!tbProductName.get(a).getValue().toString().isEmpty()){
							tbChk.get(a).setValue(true);
						}
					}
					count=1;
				}
				else if(count==1){
					for(int a=0;a<tbChk.size();a++){
						if(!tbProductName.get(a).getValue().toString().isEmpty()){
							tbChk.get(a).setValue(false);
						}
					}
					count=0;
				}
			}
		});*/
		cmbProductionType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				tableClear();
				if(cmbProductionType.getValue()!=null){
					//tableDataLoad();
				}
				
				
			}
		});
		btnLoadData.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if(cmbProductionType.getValue()!=null){
					tableDataLoad();
				}
				else{
					tableClear();
					showNotification("Please Provide Production Type",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		btnApproved.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
			
				if(checkValidation()){
					approvedButtonEvent();
				}
			}
		});
		btnRefresh.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				txtClear();
				tableClear();
			}
		});
		
		btnExit.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				
				close();
			}
		});
		btnDailyPrdReport.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				showReportWindow();
			}
		});
	}
	private void showReportWindow(){
		Window win = new RptMouldingDailyProduction(sessionBean,"");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				//cmbGroupValueAdd();
				System.out.println("As On Date");
			}
		});

		this.getParent().addWindow(win);
	}
	
	
	private boolean checkValidation(){
		
		if(cmbProductionType.getValue()!=null){
			if(!tbProductId.get(0).getValue().toString().isEmpty()){
				if(tbChk.get(0).booleanValue()){
					//if(tbisFG.get(0).booleanValue()){
					return true;
					/*}
					else{
						showNotification("Please Check  Product",Notification.TYPE_WARNING_MESSAGE);
					}*/
				}
				else{
					showNotification("Please Check at least on Product",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Production Type",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private int getTransactionNo(Session session){
		String sql="select isnull(MAX(TransactionNo),0)+1 from tbStoreReceivedApproveInfo";
		Iterator iter=session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext()){
			return Integer.parseInt(iter.next().toString());
		}
		return 0;
	}
	private void insertData(){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			/*for(int a=0;a<tbProductId.size();a++){
				if(tbChk.get(a).booleanValue()&&!tbProductId.get(a).getValue().toString().isEmpty()){
					String sql="update tbMouldSectionReceiptDetails set ApproveFlag=1,ApprovedBy='"+sessionBean.getUserName()+"' where ReqNo like '"+cmbProductionType.getValue()+"'" +
							" and rawItemCode like '"+tbProductId.get(a).getValue()+"'";
					session.createSQLQuery(sql).executeUpdate();
					
				}
			}*/
			String sqlProduction="update tbMouldProductionDetails set isApproved=1 where ProductionNo in( "+
					" select distinct b.ProductionNo from tbMouldProductionInfo a "+
					" inner join tbMouldProductionDetails b "+
					" on a.ProductionNo=b.ProductionNo  "+
					" where a.productionType='"+cmbProductionType.getValue()+"' " +
					" and convert(date,a.ProductionDate,105)='"+dateFormat.format(dateFrom.getValue())+"')";
			
			String sqlFinishedProduct="update tbMouldFinishProduct set isApproved=1 where ProductionNo in( "+
					" select distinct b.ProductionNo from tbMouldProductionInfo a "+
					" inner join tbMouldProductionDetails b "+
					" on a.ProductionNo=b.ProductionNo  "+
					" where a.productionType='"+cmbProductionType.getValue()+"' " +
					" and convert(date,a.ProductionDate,105)='"+dateFormat.format(dateFrom.getValue())+"')";
			
			session.createSQLQuery(sqlProduction).executeUpdate();
			session.createSQLQuery(sqlFinishedProduct).executeUpdate();
			
			int TransactionNo=getTransactionNo(session);
			String info="insert into tbStoreReceivedApproveInfo(transactionNo,productionDate,ProductionTypeId," +
					"productionTypeName,userIp,userName,entryTime,ApproveFrom)values('"+TransactionNo+"','"+dateFormat.format(dateFrom.getValue())+"','"+cmbProductionType.getValue()+"'," +
					"'"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'Moulding')";
			session.createSQLQuery(info).executeUpdate();
			
			for(int a=0;a<tbChk.size();a++){
				if(Double.parseDouble("0"+tbPrdQty.get(a).getValue().toString().replace(",", ""))>0){
					String details="insert into tbStoreReceivedApproveDetails(transactionNo,productId," +
							"productName,color,unit,stdWeight,goodQty)values('"+TransactionNo+"'," +
							"'"+tbProductId.get(a).getValue()+"','"+tbProductName.get(a).getValue()+"'," +
							"'"+tbColor.get(a).getValue()+"','"+tbUnit.get(a).getValue()+"'," +
							"'"+tbStdWeight.get(a).getValue()+"','"+tbPrdQty.get(a).getValue()+"')";
					session.createSQLQuery(details).executeUpdate();
				}
			}
		}
		catch(Exception exp){
			tx.rollback();
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				session.close();
				showNotification("Approved Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			}
		}
	}
	private void txtClear(){
		
		dateFrom.setValue(new java.util.Date());
		//cmbProductionType.removeAllItems();
		cmbProductionType.setValue(null);
		
	}
	private void approvedButtonEvent(){
		final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Approve?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					mb.buttonLayout.getComponent(0).setEnabled(false);
					insertData();
					txtClear();
					tableClear();
					
				}
			}
		});
	}
	private Iterator<?> dbService(String sql){
		Session session=SessionFactoryUtil.getInstance().openSession();
		Iterator<?> iter=null;
		try{
			iter= session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}
	private void tableInitialize() {
		for(int a=0;a<10;a++){
			tableRowAdd(a);
		}
	}
	private void tableRowAdd(int ar){

		tbSl.add(ar, new Label());
		tbSl.get(ar).setValue(ar+1);
		tbSl.get(ar).setWidth("100%");
		tbSl.get(ar).setHeight("-1px");

		tbChk.add(ar, new CheckBox());
		tbChk.get(ar).setImmediate(true);
		
		tbisFG.add(ar, new CheckBox());
		tbisFG.get(ar).setImmediate(true);

		tbProductName.add(ar, new Label());
		tbProductName.get(ar).setWidth("100%");
		tbProductName.get(ar).setHeight("-1px");

		tbProductId.add(ar, new Label());
		tbProductId.get(ar).setValue(ar+1);
		tbProductId.get(ar).setWidth("100%");
		tbProductId.get(ar).setHeight("-1px");
		
		tbColor.add(ar, new Label());
		tbColor.get(ar).setWidth("100%");
		tbColor.get(ar).setHeight("-1px");

		tbUnit.add(ar, new Label());
		tbUnit.get(ar).setWidth("100%");
		tbUnit.get(ar).setHeight("-1px");
		
		tbStdWeight.add(ar, new Label());
		tbStdWeight.get(ar).setWidth("100%");
		tbStdWeight.get(ar).setHeight("-1px");
		
		tbPrdQty.add(ar, new TextRead(1));
		tbPrdQty.get(ar).setWidth("100%");
		tbPrdQty.get(ar).setHeight("-1px");

		table.addItem(new Object[]{tbSl.get(ar),tbChk.get(ar),tbisFG.get(ar),tbProductName.get(ar),tbProductId.get(ar),tbColor.get(ar),tbUnit.get(ar),
				tbStdWeight.get(ar), tbPrdQty.get(ar)},ar);
	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("770px");
		setHeight("540px");

		// lblDate
		lblFromDate = new Label("Production Date: ");
		lblFromDate.setImmediate(true);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("18px");

		//Declare Date
		dateFrom = new PopupDateField();
		dateFrom.setImmediate(true);
		dateFrom.setWidth("110px");
		dateFrom.setDateFormat("dd-MM-yyyy");
		dateFrom.setValue(new java.util.Date());
		dateFrom.setResolution(PopupDateField.RESOLUTION_DAY);

		lblProductionStep = new Label("Production Type: ");
		lblProductionStep.setImmediate(false);
		lblProductionStep.setWidth("100.0%");
		lblProductionStep.setHeight("18px");

		cmbProductionType =new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
	

		btnLoadData.setWidth("100px");
		btnLoadData.setHeight("28px");
		btnLoadData.setIcon(new ThemeResource("../icons/icon_get_world.gif"));

		btnDailyPrdReport.setWidth("180px");
		btnDailyPrdReport.setHeight("28px");
		btnDailyPrdReport.setIcon(new ThemeResource("../icons/generate.png"));

		table.setWidth("740px");
		table.setHeight("320px");
		table.setImmediate(true); // react at once when something is selected
		table.setColumnCollapsingAllowed(true);	
		table.setFooterVisible(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",15);
		table.setColumnAlignment("SL", table.ALIGN_CENTER);

		table.addContainerProperty("CHK",  CheckBox.class, new CheckBox());
		table.setColumnWidth("CHK",20);
		table.setColumnCollapsed("CHK", true);
		
		table.addContainerProperty("isFG",  CheckBox.class, new CheckBox());
		table.setColumnWidth("isFG",20);
		table.setColumnCollapsed("isFG", true);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name", 300);
		table.setColumnAlignment("Product Name", table.ALIGN_LEFT);

		table.addContainerProperty("Product Id", Label.class, new Label());
		table.setColumnWidth("Product Id", 40);
		table.setColumnAlignment("Product Id", table.ALIGN_CENTER);
		table.setColumnCollapsed("Product Id", true);
		
		table.addContainerProperty("Color", Label.class, new Label());
		table.setColumnWidth("Color", 80);
		table.setColumnAlignment("Color", table.ALIGN_CENTER);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit", 30);
		table.setColumnAlignment("Unit", table.ALIGN_CENTER);

		table.addContainerProperty("Std. Weight", Label.class, new Label());
		table.setColumnWidth("Std. Weight", 60);
		table.setColumnAlignment("Std. Weight", table.ALIGN_RIGHT);
		
		table.addContainerProperty("Goods Qty", TextRead.class, new TextRead(1));
		table.setColumnWidth("Goods", 90);
		table.setColumnAlignment("Goods", table.ALIGN_RIGHT);

		btnApproved.setWidth("100px");
		btnApproved.setHeight("28px");
		btnApproved.setIcon(new ThemeResource("../icons/icon_get_world.gif"));

		btnRefresh.setWidth("100px");
		btnRefresh.setHeight("28px");
		btnRefresh.setIcon(new ThemeResource("../icons/generate.png"));

		btnExit.setWidth("90px");
		btnExit.setHeight("28px");
		btnExit.setIcon(new ThemeResource("../icons/generate.png"));

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);

		mainLayout.addComponent(lblFromDate,"top:30px;left:20px;");
		mainLayout.addComponent(dateFrom,"top:28px;left:120px;");

		mainLayout.addComponent(lblProductionStep,"top:60px;left:20px;");
		mainLayout.addComponent(cmbProductionType,"top:58px;left:120px;");

		mainLayout.addComponent(btnDailyPrdReport,"top:30px;left:340px;");
		mainLayout.addComponent(btnLoadData,"top:60px;left:340px;");

		mainLayout.addComponent(table,"top:100px;left:20px;");
		mainLayout.addComponent(lblLine,"top:420px;left:0px;");
		mainLayout.addComponent(btnApproved,"top:450px;left:270px;");
		mainLayout.addComponent(btnRefresh,"top:450px;left:380px;");
		mainLayout.addComponent(btnExit,"top:450px;left:490px;");


		return mainLayout;
	}
}
