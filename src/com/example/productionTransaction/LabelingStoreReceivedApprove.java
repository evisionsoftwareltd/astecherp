package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.productionReport.RptDailyProduction;
import com.example.productionReport.RptRequisitionProduction0;
import com.vaadin.data.Property.*;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.*;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class LabelingStoreReceivedApprove extends Window {

	private static final long serialVersionUID = -3672729251787238695L;
	SessionBean sessionBean;
	AbsoluteLayout mainLayout;
	Label lblFromDate,lblToDate,lblDate,lblProductionType,lblProductionStep,lblBatchNo,lblProductionNo;
	PopupDateField dateFrom;
	ComboBox cmbProductionType,cmbProductionStep,cmbBatchNo,cmbProductionNo;
	CheckBox chkBatchNo,chkProductionNo;
	private NativeButton btnDailyPrdReport = new NativeButton("Daily Production Report");
	private NativeButton btnLoadData = new NativeButton("Load Data");

	private NativeButton btnApproved = new NativeButton("Approved");
	private NativeButton btnRefresh = new NativeButton("Refresh");
	private NativeButton btnExit = new NativeButton("Exit");

	ArrayList<Label>tbSl = new ArrayList<Label>();
	ArrayList<CheckBox>tbChk=new ArrayList<CheckBox>();
	ArrayList<Label>tbProductId=new ArrayList<Label>();
	ArrayList<Label>tbProductName=new ArrayList<Label>();
	ArrayList<Label>tbBatchNo=new ArrayList<Label>();
	ArrayList<Label>tbUnit=new ArrayList<Label>();
	ArrayList<Label>tbProductionNo=new ArrayList<Label>();
	ArrayList<TextRead>tbPrdQty=new ArrayList<TextRead>(1);

	Table table=new Table();

	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0.00");
	int count=0;

	public LabelingStoreReceivedApprove(SessionBean sesionBean)
	{
		this.sessionBean=sesionBean;
		this.setResizable(false);
		this.setCaption("STORE RECEIVED APPROVE [LABELING/PRINTING] :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();
		setEventAction();
		ProductionStepLoad();
		
		
	}
	private void ProductionStepLoad() {
		cmbProductionStep.removeAllItems();
		
		String sql= "select 0,productionStep from tbLabelingPrintingDailyProductionInfo " 
				    +"where convert(date,productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and isApproved=0  "
				    +"union "
				    +"select 0,productionStep from tbSBMDailyProductionInfo " 
				    +"where convert(date,productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and isApproved=0 "
				    +"union "
				    +"select 0,productionStep from tbLacqureDailyProductionInfo where "
				    +"convert(date,productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and isApproved=0 order by productionStep " ;
		
	/*	Iterator iter=dbService("select 0,productionStep from tbLabelingPrintingDailyProductionInfo " +
				"where convert(date,productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and isApproved=0 union "+
				" select 0,productionStep from tbLacqureDailyProductionInfo where "+
				" convert(date,productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and isApproved=0 order by productionStep ");
		*/
		Iterator iter=dbService(sql);
		
		
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionStep.addItem(element[1]);
			cmbProductionStep.setItemCaption(element[1], element[1].toString());
		}
	}
	private void tableDataLoad(){
		String productionNo="%",batchNo="%",sql="";
		if(cmbProductionNo.getValue()!=null){
			productionNo=cmbProductionNo.getValue().toString();
		}
		if(cmbBatchNo.getValue()!=null){
			batchNo=cmbBatchNo.getValue().toString();
		}
		if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Lacqure"))
		{
			sql="select fgCode,fgName,'Pcs' unit,(SUM(shiftA)+SUM(shiftB))GoodQty,b.batchNo from tbLacqureDailyProductionInfo a "+
					" inner join tbLacqureDailyProductionDetails b on a.productionNo=b.productionNo "+
					" where a.productionStep like '"+cmbProductionStep.getValue()+"' and b.productionNo like '"+productionNo+"' and  "+
					" convert(date,a.productionDate,105) ='"+dateFormat.format(dateFrom.getValue())+"' and b.batchNo like '"+batchNo+"' and b.isApproved=0  group by fgCode,fgName,b.batchNo "+
					" order by cast(SUBSTRING(batchNo,CHARINDEX('-',batchNo)+1,len(batchNo)-CHARINDEX('-',batchNo))as int),fgName";
		}
		else if (cmbProductionStep.getValue().toString().equalsIgnoreCase("Stretch Blow Molding"))
		{
			sql=    
					"select fgCode,fgName,'Pcs' unit,(SUM(shiftA)+SUM(shiftB))GoodQty,b.batchNo from tbSBMDailyProductionInfo a " 
					+"inner join tbSBMDailyProductionDetails b on a.productionNo=b.productionNo " 
					+"where a.productionStep like '"+cmbProductionStep.getValue()+"' and b.productionNo like '"+productionNo+"' and "  
					+"convert(date,a.productionDate,105) ='"+dateFormat.format(dateFrom.getValue())+"' and b.batchNo like '"+batchNo+"' and b.isApproved=0  group by fgCode,fgName,b.batchNo " 
					+"order by cast(SUBSTRING(batchNo,CHARINDEX('-',batchNo)+1,len(batchNo)-CHARINDEX('-',batchNo))as int),fgName ";
		
		}
		else
		{
			sql="select fgCode,fgName,'Pcs' unit,(SUM(shiftA)+SUM(shiftB))GoodQty,b.batchNo from tbLabelingPrintingDailyProductionInfo a "+
					" inner join tbLabelingPrintingDailyProductionDetails b on a.productionNo=b.productionNo "+
					" where a.productionStep like '"+cmbProductionStep.getValue()+"' and b.productionNo like '"+productionNo+"' and  "+
					" convert(date,a.productionDate,105) ='"+dateFormat.format(dateFrom.getValue())+"' and b.batchNo like '"+batchNo+"' and b.isApproved=0  group by fgCode,fgName,b.batchNo "+
					" order by cast(SUBSTRING(batchNo,CHARINDEX('-',batchNo)+1,len(batchNo)-CHARINDEX('-',batchNo))as int),fgName";
			
		}
		
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbChk.get(a).setValue(true);
			tbProductId.get(a).setValue(element[0]);
			tbProductName.get(a).setValue(element[1]);
			tbUnit.get(a).setValue(element[2]);
			tbPrdQty.get(a).setValue(df.format(element[3]));
			tbBatchNo.get(a).setValue(element[4]);
			if(a==tbChk.size()-1){
				tableRowAdd(a+1);
			}
			a++;
		}
	}
	private void tableClear(){
		for(int a=0;a<tbChk.size();a++){
			tbChk.get(a).setValue(false);
			tbProductId.get(a).setValue("");
			tbProductName.get(a).setValue("");
			tbBatchNo.get(a).setValue("");
			tbUnit.get(a).setValue("");
			//tbProductionNo.get(a).setValue("");
			tbPrdQty.get(a).setValue("");
		}
	}

	private void cmbProductionNoLoad()
	{
		cmbProductionNo.removeAllItems();
		Iterator iter=dbService("select 0,productionNo from tbLabelingPrintingDailyProductionInfo where convert(date,productionDate,105)" +
				"='"+dateFormat.format(dateFrom.getValue())+"' and productionStep='"+cmbProductionStep.getValue()+"' and isApproved=0 order by productionStep ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionNo.addItem(element[1]);
			cmbProductionNo.setItemCaption(element[1], element[1].toString());
		}
	}
	
	private void cmbProductionNoLoadSBM()
	{
		cmbProductionNo.removeAllItems();
		Iterator iter=dbService("select 0,productionNo from tbSBMDailyProductionInfo where convert(date,productionDate,105)" +
				"='"+dateFormat.format(dateFrom.getValue())+"' and productionStep='"+cmbProductionStep.getValue()+"' and isApproved=0 order by productionStep ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionNo.addItem(element[1]);
			cmbProductionNo.setItemCaption(element[1], element[1].toString());
		}
	}
	
	
	
	private void cmbProductionNoLoadLacqure() 
	{
		cmbProductionNo.removeAllItems();
		Iterator iter=dbService("select 0,productionNo from tbLacqureDailyProductionInfo where convert(date,productionDate,105)" +
				"='"+dateFormat.format(dateFrom.getValue())+"' and productionStep='"+cmbProductionStep.getValue()+"' and isApproved=0 order by productionStep ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionNo.addItem(element[1]);
			cmbProductionNo.setItemCaption(element[1], element[1].toString());
		}
	}
	private void cmbBatchNoLoad() 
	
	{
		cmbBatchNo.removeAllItems();
		Iterator iter=dbService("select distinct 0,batchNo from tbLabelingPrintingDailyProductionDetails" +
				" where productionNo='"+cmbProductionNo.getValue()+"' and isApproved=0");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbBatchNo.addItem(element[1]);
			cmbBatchNo.setItemCaption(element[1], element[1].toString());
		}
	}
	
	
	private void cmbBatchNoLoadSBM() 
	
	{
		cmbBatchNo.removeAllItems();
		Iterator iter=dbService("select distinct 0,batchNo from tbSbmDailyProductionDetails" +
				" where productionNo='"+cmbProductionNo.getValue()+"' and isApproved=0");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbBatchNo.addItem(element[1]);
			cmbBatchNo.setItemCaption(element[1], element[1].toString());
		}
	}
	
	
	private void cmbBatchNoLoadLacqure() {
		cmbBatchNo.removeAllItems();
		Iterator iter=dbService("select distinct 0,batchNo from tbLacqureDailyProductionDetails" +
				" where productionNo='"+cmbProductionNo.getValue()+"' and isApproved=0");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbBatchNo.addItem(element[1]);
			cmbBatchNo.setItemCaption(element[1], element[1].toString());
		}
	}
	private void setEventAction() {
		dateFrom.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				ProductionStepLoad();
			}
		});
		cmbProductionStep.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbProductionStep.getValue()!=null){
					
					if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Lacqure")){
						cmbProductionNoLoadLacqure();
					}
					
					else if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Stretch Blow Molding")){
						cmbProductionNoLoadSBM();
					}
					
					else
					{
						cmbProductionNoLoad();
					}
				}
				else{
					cmbProductionNo.removeAllItems();
				}

			}
		});
		cmbProductionNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbProductionNo.getValue()!=null){
					
					if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Lacqure"))
					{
						cmbBatchNoLoadLacqure();
					}
					else if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Stretch Blow Molding"))
					{
						cmbBatchNoLoadSBM();
					}
					
					else
					{
						cmbBatchNoLoad();
					}
				}
				else{
					cmbBatchNo.removeAllItems();
				}

			}
		});
		btnLoadData.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(cmbProductionStep.getValue()!=null
						&&(cmbProductionNo.getValue()!=null||chkProductionNo.booleanValue())
						&&(cmbBatchNo.getValue()!=null||chkBatchNo.booleanValue())){
					tableDataLoad();
				}
				else{
					tableClear();
					showNotification("Please Provide all Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		btnApproved.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(checkValidation())
				{
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

			@Override
			public void buttonClick(ClickEvent event) {
				showReportWindow();
			}
		});
		chkBatchNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(chkBatchNo.booleanValue()){
					cmbBatchNo.setEnabled(false);
					cmbBatchNo.setValue(null);
				}
				else{
					cmbBatchNo.setEnabled(true);
				}
			}
		});
		chkProductionNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(chkProductionNo.booleanValue()){
					cmbProductionNo.setEnabled(false);
					cmbProductionNo.setValue(null);
				}
				else{
					cmbProductionNo.setEnabled(true);
				}
			}
		});
	}
	private void showReportWindow(){
		Window win = new RptDailyProduction(sessionBean,"");

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

		if(cmbProductionStep.getValue()!=null
				&&(cmbProductionNo.getValue()!=null||chkProductionNo.booleanValue())
				&&(cmbBatchNo.getValue()!=null||chkBatchNo.booleanValue()))
		{
			if(!tbProductId.get(0).getValue().toString().isEmpty()){
				if(tbChk.get(0).booleanValue()){
					return true;
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
			showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
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
		String productionNo="%",batchNo="%",ProductionNoSave="All",batchNoSave="All",
				sqlUpdateDetails="",sqlUpdatInfo="",sqlUpdatInfoFinishedProduct="";
		if(cmbProductionNo.getValue()!=null){
			productionNo=cmbProductionNo.getValue().toString();
			ProductionNoSave=cmbProductionNo.getValue().toString();
		}
		if(cmbBatchNo.getValue()!=null){
			batchNo=cmbBatchNo.getValue().toString();
			batchNoSave=cmbBatchNo.getValue().toString();
		}
		Session session=null,session1=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();

			if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Lacqure")){
				sqlUpdateDetails="update tbLacqureDailyProductionDetails set isApproved=1 where productionNo in( "+
						" select a.productionNo from tbLacqureDailyProductionInfo a "+
						" inner join tbLacqureDailyProductionDetails b on a.productionNo=b.productionNo "+
						" where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and a.productionStep='"+cmbProductionStep.getValue()+"'  "+
						" and b.productionNo like '"+productionNo+"' and b.batchNo like '"+batchNo+"')and batchNo in( "+
						" select b.batchNo from tbLacqureDailyProductionInfo a "+
						" inner join tbLacqureDailyProductionDetails b on a.productionNo=b.productionNo "+
						" where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and a.productionStep='"+cmbProductionStep.getValue()+"'  "+
						" and b.productionNo like '"+productionNo+"' and b.batchNo like '"+batchNo+"')";
				
				sqlUpdatInfo="update tbLacqureDailyProductionInfo set isApproved=1  "+
						" where productionNo not in( "+
						" select distinct b.productionNo from tbLacqureDailyProductionInfo a "+
						" inner join tbLacqureDailyProductionDetails b on a.productionNo=b.productionNo "+
						" where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and a.productionStep=" +
						"'"+cmbProductionStep.getValue()+"' and b.productionNo like '"+productionNo+"'  "+
						" and b.isApproved=0 "+
						" ) and convert(date,productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and productionStep=" +
						"'"+cmbProductionStep.getValue()+"' and productionNo like '"+productionNo+"'  "+
						" and isApproved=0 ";
			}
			
			else if (cmbProductionStep.getValue().toString().equalsIgnoreCase("Stretch Blow Molding"))
			{
				sqlUpdateDetails= "update tbSBMDailyProductionDetails set isApproved =1 where productionNo in " 
						          +"("
								  +"select a.productionNo from tbSBMDailyProductionInfo a inner join tbSBMDailyProductionDetails b "
								  +"on a.productionNo=b.productionNo where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' "
								  +"and a.productionNo like  '"+productionNo+"' and batchNo like '"+batchNo+"' and a.productionStep like '"+cmbProductionStep.getValue()+"'  "
								  +") " 
								  +"and batchNo in " 
								  +"( "
								  +"select b.batchNo from tbSBMDailyProductionInfo a inner join tbSBMDailyProductionDetails b "
								  +"on a.productionNo=b.productionNo where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' "
								  +"and a.productionNo like  '"+productionNo+"' and batchNo like '"+batchNo+"' and a.productionStep like '"+cmbProductionStep.getValue()+"'  "
								  +") ";
				
				sqlUpdatInfo=   "  update tbSBMDailyProductionInfo set isApproved=1 "  
						        +" where productionNo not in( "
								+" select distinct b.productionNo from tbSBMDailyProductionInfo a " 
								+" inner join tbSBMDailyProductionDetails b on a.productionNo=b.productionNo " 
								+" where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and a.productionStep= "
								+" '"+cmbProductionStep.getValue()+"' and b.productionNo like '"+productionNo+"' "  
								+" and b.isApproved=0  "
								+") and convert(date,productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and productionStep= "
								+" '"+cmbProductionStep.getValue()+"' and productionNo like '"+productionNo+"'  " 
								+"and isApproved=0  ";

			}
			
			
			else
			{
				sqlUpdateDetails="update tbLabelingPrintingDailyProductionDetails set isApproved=1 where productionNo in( "+
						" select a.productionNo from tbLabelingPrintingDailyProductionInfo a "+
						" inner join tbLabelingPrintingDailyProductionDetails b on a.productionNo=b.productionNo "+
						" where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and a.productionStep='"+cmbProductionStep.getValue()+"'  "+
						" and b.productionNo like '"+productionNo+"' and b.batchNo like '"+batchNo+"')and batchNo in( "+
						" select b.batchNo from tbLabelingPrintingDailyProductionInfo a "+
						" inner join tbLabelingPrintingDailyProductionDetails b on a.productionNo=b.productionNo "+
						" where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and a.productionStep='"+cmbProductionStep.getValue()+"'  "+
						" and b.productionNo like '"+productionNo+"' and b.batchNo like '"+batchNo+"')";
				
				sqlUpdatInfo="update tbLabelingPrintingDailyProductionInfo set isApproved=1  "+
						" where productionNo not in( "+
						" select distinct b.productionNo from tbLabelingPrintingDailyProductionInfo a "+
						" inner join tbLabelingPrintingDailyProductionDetails b on a.productionNo=b.productionNo "+
						" where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and a.productionStep=" +
						"'"+cmbProductionStep.getValue()+"' and b.productionNo like '"+productionNo+"'  "+
						" and b.isApproved=0 "+
						" ) and convert(date,productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and productionStep=" +
						"'"+cmbProductionStep.getValue()+"' and productionNo like '"+productionNo+"'  "+
						" and isApproved=0 ";
				
				sqlUpdatInfoFinishedProduct="update tbLabelingPrintingFinishedProduct set isApproved=1  "+
						" where productionNo in( "+
						" select distinct b.productionNo from tbLabelingPrintingDailyProductionInfo a "+
						" inner join tbLabelingPrintingDailyProductionDetails b on a.productionNo=b.productionNo "+
						" where convert(date,a.productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and a.productionStep=" +
						"'"+cmbProductionStep.getValue()+"' and b.productionNo like '"+productionNo+"'  "+
						" and b.isApproved=0 "+
						" ) and convert(date,productionDate,105)='"+dateFormat.format(dateFrom.getValue())+"' and productionStepId=" +
						"'"+cmbProductionStep.getValue()+"' and productionNo like '"+productionNo+"'  "+
						" and isApproved=0 ";
				session.createSQLQuery(sqlUpdatInfoFinishedProduct).executeUpdate();
				
			}
			
			session.createSQLQuery(sqlUpdateDetails).executeUpdate();
			session.createSQLQuery(sqlUpdatInfo).executeUpdate();
			
			
			
			int TransactionNo=getTransactionNo(session);
			
			String info="insert into tbStoreReceivedApproveInfo(transactionNo,productionDate,ProductionTypeId," +
					"productionTypeName,userIp,userName,entryTime,ApproveFrom,productionNo,BatchNo)values('"+TransactionNo+"','"+dateFormat.format(dateFrom.getValue())+"','"+cmbProductionStep.getValue()+"'," +
					"'"+cmbProductionStep.getItemCaption(cmbProductionStep.getValue())+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP," +
					"'Others','"+ProductionNoSave+"','"+batchNoSave+"')";
			System.out.println(info);
			session.createSQLQuery(info).executeUpdate();
			
			for(int a=0;a<tbChk.size();a++){
				if(Double.parseDouble("0"+tbPrdQty.get(a).getValue().toString().replace(",", ""))>0){
					String details="insert into tbStoreReceivedApproveDetails(transactionNo,productId," +
							"productName,batchNo,unit,goodQty)values('"+TransactionNo+"'," +
							"'"+tbProductId.get(a).getValue()+"','"+tbProductName.get(a).getValue()+"'," +
							"'"+tbBatchNo.get(a).getValue()+"','"+tbUnit.get(a).getValue()+"'," +
							"'"+tbPrdQty.get(a).getValue()+"')";
					System.out.println(details);
					session.createSQLQuery(details).executeUpdate();
				}
			}
			
			
		}
		catch(Exception exp){
			System.out.println(exp);
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
		cmbProductionStep.removeAllItems();
		cmbProductionStep.setValue(null);
		cmbBatchNo.removeAllItems();
		cmbBatchNo.setValue(null);

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

		tbProductName.add(ar, new Label());
		tbProductName.get(ar).setWidth("100%");
		tbProductName.get(ar).setHeight("-1px");

		tbProductId.add(ar, new Label());
		tbProductId.get(ar).setValue(ar+1);
		tbProductId.get(ar).setWidth("100%");
		tbProductId.get(ar).setHeight("-1px");

		tbBatchNo.add(ar, new Label());
		tbBatchNo.get(ar).setWidth("100%");
		tbBatchNo.get(ar).setHeight("-1px");

		tbUnit.add(ar, new Label());
		tbUnit.get(ar).setWidth("100%");
		tbUnit.get(ar).setHeight("-1px");

		/*tbProductionNo.add(ar, new Label());
		tbProductionNo.get(ar).setWidth("100%");
		tbProductionNo.get(ar).setHeight("-1px");*/

		tbPrdQty.add(ar, new TextRead(1));
		tbPrdQty.get(ar).setWidth("100%");
		tbPrdQty.get(ar).setHeight("-1px");

		table.addItem(new Object[]{tbSl.get(ar),tbChk.get(ar),tbBatchNo.get(ar),tbProductName.get(ar),tbProductId.get(ar),tbUnit.get(ar),
				/*tbProductionNo.get(ar),*/ tbPrdQty.get(ar)},ar);
	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("740px");
		setHeight("570px");

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

		lblProductionStep = new Label("Production Step: ");
		lblProductionStep.setImmediate(false);
		lblProductionStep.setWidth("100.0%");
		lblProductionStep.setHeight("18px");

		cmbProductionStep =new ComboBox();
		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setWidth("200px");
		cmbProductionStep.setHeight("24px");
		cmbProductionStep.setNullSelectionAllowed(true);
		cmbProductionStep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblProductionNo = new Label("Production No: ");
		lblProductionNo.setImmediate(false);
		lblProductionNo.setWidth("100.0%");
		lblProductionNo.setHeight("18px");

		cmbProductionNo =new ComboBox();
		cmbProductionNo.setImmediate(true);
		cmbProductionNo.setWidth("200px");
		cmbProductionNo.setHeight("24px");
		cmbProductionNo.setNullSelectionAllowed(true);
		cmbProductionNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblBatchNo = new Label("Batch No: ");
		lblBatchNo.setImmediate(false);
		lblBatchNo.setWidth("100.0%");
		lblBatchNo.setHeight("18px");

		cmbBatchNo =new ComboBox();
		cmbBatchNo.setImmediate(true);
		cmbBatchNo.setWidth("200px");
		cmbBatchNo.setHeight("24px");
		cmbBatchNo.setNullSelectionAllowed(true);
		cmbBatchNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		chkBatchNo=new CheckBox("All");
		chkBatchNo.setImmediate(true);
		chkBatchNo.setHeight("-1px");
		chkBatchNo.setWidth("-1px");

		chkProductionNo=new CheckBox("All");
		chkProductionNo.setImmediate(true);
		chkProductionNo.setHeight("-1px");
		chkProductionNo.setWidth("-1px");


		btnLoadData.setWidth("100px");
		btnLoadData.setHeight("28px");
		btnLoadData.setIcon(new ThemeResource("../icons/icon_get_world.gif"));

		btnDailyPrdReport.setWidth("180px");
		btnDailyPrdReport.setHeight("28px");
		btnDailyPrdReport.setIcon(new ThemeResource("../icons/generate.png"));

		table.setWidth("700px");
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

		table.addContainerProperty("Batch No", Label.class, new Label());
		table.setColumnWidth("Batch No", 80);
		table.setColumnAlignment("Batch No", table.ALIGN_LEFT);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name", 360);
		table.setColumnAlignment("Product Name", table.ALIGN_LEFT);

		table.addContainerProperty("Product Id", Label.class, new Label());
		table.setColumnWidth("Product Id", 40);
		table.setColumnAlignment("Product Id", table.ALIGN_CENTER);
		table.setColumnCollapsed("Product Id", true);

		/*table.addContainerProperty("Color", Label.class, new Label());
		table.setColumnWidth("Color", 80);
		table.setColumnAlignment("Color", table.ALIGN_CENTER);*/

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit", 50);
		table.setColumnAlignment("Unit", table.ALIGN_CENTER);

		/*table.addContainerProperty("Std. Weight", Label.class, new Label());
		table.setColumnWidth("Std. Weight", 80);
		table.setColumnAlignment("Std. Weight", table.ALIGN_CENTER);*/

		table.addContainerProperty("Good Qty", TextRead.class, new TextRead(1));
		table.setColumnWidth("Good Qty", 100);
		table.setColumnAlignment("Good Qty", table.ALIGN_CENTER);

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

		mainLayout.addComponent(lblFromDate,"top:10px;left:20px;");
		mainLayout.addComponent(dateFrom,"top:8px;left:120px;");

		mainLayout.addComponent(lblProductionStep,"top:35px;left:20px;");
		mainLayout.addComponent(cmbProductionStep,"top:33px;left:120px;");

		mainLayout.addComponent(lblProductionNo,"top:60px;left:20px;");
		mainLayout.addComponent(cmbProductionNo,"top:58px;left:120px;");
		mainLayout.addComponent(chkProductionNo,"top:60px;left:325px;");

		mainLayout.addComponent(lblBatchNo,"top:85px;left:20px;");
		mainLayout.addComponent(cmbBatchNo,"top:83px;left:120px;");
		mainLayout.addComponent(chkBatchNo,"top:85px;left:325px;");

		mainLayout.addComponent(btnDailyPrdReport,"top:30px;left:360px;");
		mainLayout.addComponent(btnLoadData,"top:60px;left:360px;");

		mainLayout.addComponent(table,"top:130px;left:20px;");
		mainLayout.addComponent(lblLine,"top:450px;left:0px;");
		mainLayout.addComponent(btnApproved,"top:480px;left:200px;");
		mainLayout.addComponent(btnRefresh,"top:480px;left:310px;");
		mainLayout.addComponent(btnExit,"top:480px;left:420px;");


		return mainLayout;
	}
}
