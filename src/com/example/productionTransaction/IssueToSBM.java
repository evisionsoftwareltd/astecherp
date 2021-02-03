package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.*;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class IssueToSBM extends Window {

	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	boolean isUpdate=false,isFind=false;

	private Label lblIssueTo,lblBatchNo,lblReqNo,lblIssueDate,lblIssueNo,lblReqDate;
	private ComboBox cmbIssueTo,cmbBatchNo;
	private TextRead txtReqNo,txtIssueNo;
	private PopupDateField dIssueDate,dReqDate;

	Table table = new Table();
	ArrayList<Label>tbSl = new ArrayList<Label>();
	ArrayList<ComboBox>tbCmbItemName = new ArrayList<ComboBox>();
	ArrayList<Label>tbColor =  new ArrayList<Label>();
	ArrayList<Label>tbUnit =  new ArrayList<Label>();
	ArrayList<TextRead>tbStoreStock =  new ArrayList<TextRead>();
	ArrayList<TextRead>tbSMBStock =  new ArrayList<TextRead>();
	ArrayList<TextRead>tbReqQty =  new ArrayList<TextRead>();
	ArrayList<TextRead>tbIssuedQty =  new ArrayList<TextRead>();
	ArrayList<TextRead>tbRemainQty =  new ArrayList<TextRead>();
	ArrayList<AmountField>tbIssueQty = new ArrayList<AmountField>();
	ArrayList<TextField>tbRemarks=new ArrayList<TextField>();
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0");

	public IssueToSBM(SessionBean sessionBean,String caption,int a){
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("ISSUE TO SBM :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		componentIni(true);
		btnIni(true);
		cmbBatchNoLoad();

		focusEnter();
	}

	private void focusEnter() {
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(cmbIssueTo);
		focusComp.add(cmbBatchNo);

		for(int i = 0; i < tbSl.size(); i++)
		{
			focusComp.add(tbCmbItemName.get(i));
			focusComp.add(tbIssueQty.get(i));
			focusComp.add(tbRemarks.get(i));
		}

		focusComp.add(cButton.btnNew);
		focusComp.add(cButton.btnEdit);
		focusComp.add(cButton.btnSave);
		focusComp.add(cButton.btnRefresh);
		focusComp.add(cButton.btnDelete);
		focusComp.add(cButton.btnFind);

		new FocusMoveByEnter(this, focusComp);
	}

	private void btnIni(boolean t) 
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);

	}
	private Iterator dbService(String sql){
		Session session=null;


		Iterator iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
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
	protected void refreshButtonEvent()
	{
		isFind=false;
		isUpdate=false;
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void txtClear() {
		cmbBatchNo.setValue(null);
		txtReqNo.setValue("");
		dIssueDate.setValue(new java.util.Date());
		dReqDate.setValue(new java.util.Date());
		txtIssueNo.setValue("");
		tableClear();
	}

	private void tableClear() {
		for(int a=0;a<tbSl.size();a++){
			//tbCmbItemName.get(a).removeAllItems();
			tbCmbItemName.get(a).setValue(null);
			tbUnit.get(a).setValue("");
			tbColor.get(a).setValue("");
			tbStoreStock.get(a).setValue("");
			tbSMBStock.get(a).setValue("");
			tbReqQty.get(a).setValue("");
			tbIssuedQty.get(a).setValue("");
			tbRemainQty.get(a).setValue("");
			tbIssueQty.get(a).setValue("");
			tbRemarks.get(a).setValue("");
		}
	}

	protected void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		issueNoLoad();
		cmbBatchNo.focus();
	}
	private boolean doubleEntryCheck(int ar){
		for(int a=0;a<tbSl.size();a++){
			if(a!=ar){
				if(tbCmbItemName.get(a).getValue()!=null){
					if(tbCmbItemName.get(ar).getValue().toString().equalsIgnoreCase(tbCmbItemName.get(a).getValue().toString())){
						return false;
					}
				}
			}
		}
		return true;
	}
	private void componentIni(boolean b) {
		cmbIssueTo.setEnabled(!b);
		cmbBatchNo.setEnabled(!b);
		txtReqNo.setEnabled(!b);
		dIssueDate.setEnabled(!b);
		dReqDate.setEnabled(!b);
		txtIssueNo.setEnabled(!b);
		table.setEnabled(!b);
	}
	private void updateButtonEvent(){
		if(!txtIssueNo.getValue().toString().isEmpty())
		{
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void setEventAction() {
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind=false;

				newButtonEvent();
			}
		});

		cButton.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sessionBean.isUpdateable()){
					isUpdate = true;
					updateButtonEvent();
				}
				else{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnExit.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}
		});
		cButton.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(checkValidation()){
					saveButtonEvent();
				}
				else
				{
					showNotification("Please select necessary Field",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				isFind=true;
				isUpdate=true;
				findButtonEvent();
			}
		});

		cmbBatchNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbBatchNo.getValue()!=null){
					reqIssueNoSet();
					tableClear();
					tableCmbItemDataLoad();
				}
				else{
					tableClear();
					txtReqNo.setValue("");
					txtIssueNo.setValue("");
				}
			}
		});
		
		dIssueDate.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				tableClear();
				
			}
		});
		
	}
	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			String sqlInfo="insert into tbUdIssueToSBM Select a.issueNo,a.batchNo,issueTo,ReqNo,reqDate,"
					+ "issueDate,productId,productName,unit,color,ReqQty, issuedQty,remainQty,issueQty,"
					+ "remarks,a.userIp,a.userName,a.entryTime,'Old'vFlag from tbIssueToSBMInfo a "
					+ "inner join tbIssueToSBMDetails b on a.issueNo= b.issueNo "
					+ "where a.issueNo='"+txtIssueNo+"'";
			session.createSQLQuery(sqlInfo).executeUpdate();


			session.createSQLQuery("delete from tbIssueToSBMInfo where issueNo='"+txtIssueNo.getValue()+ "'").executeUpdate();

			session.createSQLQuery("delete from tbIssueToSBMDetails where issueNo='"+txtIssueNo.getValue()+ "'").executeUpdate();

			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	private void saveButtonEvent() 
	{
		if (isUpdate) 
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener() 
			{
				public void buttonClicked(ButtonType buttonType) 
				{
					if (buttonType == ButtonType.YES) 
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						Transaction tx = null;
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();

						tx = session.beginTransaction();

						if (deleteData(session, tx))
						{
							insertData();
						}
						else 
						{
							tx.rollback();
						}
						isUpdate=false;
						isFind=false;
						componentIni(true);
						btnIni(true);
						tableClear();
						txtClear();
					}
				}
			});
		} 
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener() 
			{
				public void buttonClicked(ButtonType buttonType) 
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						insertData();
						componentIni(true);
						btnIni(true);
						tableClear();
						txtClear();							
					}
				}
			});
		}

	}
	private void insertData(){
		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String issueNo="";

		try{

			if(isUpdate){
				/*-------------------- New data for Update table -----------------*/
				for(int a=0;a<tbSl.size();a++){
					if(tbCmbItemName.get(0).getValue()!=null&&Double.parseDouble("0"+tbIssueQty.get(a).getValue().toString())>0){

						Object cmbItemId=tbCmbItemName.get(a).getValue();
						Object cmbItemName=tbCmbItemName.get(a).getItemCaption(tbCmbItemName.get(a).getValue());
						Object txtUnit=tbUnit.get(a).getValue();
						Object txtColor=tbColor.get(a).getValue();
						Object txtReqQty=tbReqQty.get(a).getValue();
						Object txtIssuedQty= tbIssuedQty.get(a).getValue();
						Object txtRemainQty= tbRemainQty.get(a).getValue();
						Object txtIssueQty= tbIssueQty.get(a).getValue();
						Object txtRemarks= tbRemarks.get(a).getValue();

						String sqlUdinfo="insert into tbUdIssueToSBM(issueTo,batchNo,reqNo,issueNo,reqDate,"
								+ "issueDate,productId,productName,unit,color,ReqQty,issuedQty,remainQty,issueQty,remarks,userIp,userName,entryTime,vFlag)"
								+ "values"
								+ "('"+cmbIssueTo.getValue()+"',"
								+ "'"+cmbBatchNo.getValue()+"',"
								+ "'"+txtReqNo.getValue()+"',"
								+ "'"+txtIssueNo.getValue()+"',"
								+ "'"+dateFormat.format(dReqDate.getValue())+"',"
								+ "'"+dateFormat.format(dIssueDate.getValue())+"',"
								+ "'"+cmbItemId+"',"
								+ "'"+cmbItemName+"',"
								+ "'"+txtUnit+"',"
								+ "'"+txtColor+"',"
								+ "'"+txtReqQty+"',"
								+ "'"+txtIssuedQty+"',"
								+ "'"+txtRemainQty+"',"
								+ "'"+txtIssueQty+"',"
								+ "'"+txtRemarks+"',"
								+ "'"+sessionBean.getUserIp()+"',"
								+ "'"+sessionBean.getUserName()+"',"
								+ "CURRENT_TIMESTAMP,'New')";
						session.createSQLQuery(sqlUdinfo).executeUpdate();

					}
				}
			}



			String sqlInfo="insert into tbIssueToSBMInfo(issueTo,batchNo,reqNo,issueNo,reqDate,"
					+ "issueDate,userIp,userName,entryTime)"
					+ "values"
					+ "('"+cmbIssueTo.getValue()+"',"
					+ "'"+cmbBatchNo.getValue()+"',"
					+ "'"+txtReqNo.getValue()+"',"
					+ "'"+txtIssueNo.getValue()+"',"
					+ "'"+dateFormat.format(dReqDate.getValue())+"',"
					+ "'"+dateFormat.format(dIssueDate.getValue())+"',"
					+ "'"+sessionBean.getUserIp()+"',"
					+ "'"+sessionBean.getUserName()+"',"
					+ "CURRENT_TIMESTAMP)";
			session.createSQLQuery(sqlInfo).executeUpdate();

			for(int a=0;a<tbSl.size();a++){
				if(tbCmbItemName.get(0).getValue()!=null&&Double.parseDouble("0"+tbIssueQty.get(a).getValue().toString())>0){

					String sqlDetails="insert into tbIssueToSBMDetails(issueNo,productId,productName,unit,color,ReqQty,"
							+ " issuedQty,remainQty,issueQty,remarks,batchNo,userIp,userName,entryTime)"
							+ "values"
							+ "('"+txtIssueNo.getValue()+"',"
							+ "'"+tbCmbItemName.get(a).getValue()+"',"
							+ "'"+tbCmbItemName.get(a).getItemCaption(tbCmbItemName.get(a).getValue())+"',"
							+ "'"+tbUnit.get(a).getValue()+"',"
							+ "'"+tbColor.get(a).getValue()+"',"
							+ "'"+tbReqQty.get(a).getValue()+"',"
							+ "'"+tbIssuedQty.get(a).getValue()+"',"
							+ "'"+tbRemainQty.get(a).getValue()+"',"
							+ "'"+tbIssueQty.get(a).getValue()+"',"
							+ "'"+tbRemarks.get(a).getValue()+"',"
							+ "'"+cmbBatchNo.getValue()+"',"
							+ "'"+sessionBean.getUserIp()+"',"
							+ "'"+sessionBean.getUserName()+"',"
							+ "CURRENT_TIMESTAMP)";

					session.createSQLQuery(sqlDetails).executeUpdate();
				}
			}

			showNotification("All Information Save Successfully",Notification.TYPE_WARNING_MESSAGE);
			tx.commit();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	/*private boolean checkValidation(){
		if(cmbIssueTo.getValue()!=null){
			if(cmbBatchNo.getValue()!=null){
				if(tbCmbItemName.get(0).getValue()!=null&&
						Double.parseDouble("0"+tbIssueQty.get(0).getValue().toString())>0){
					return true;
				}
				else{
					showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide Batch No",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Issue To",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}*/
	
	
	private boolean checkValidation()
	{
		if(cmbIssueTo.getValue()!=null && cmbBatchNo.getValue()!=null)
		{
		   for(int i=0;i<tbCmbItemName.size();i++)
		   {
			    if (tbCmbItemName.get(i).getValue()!=null && Double.parseDouble("0"+tbIssueQty.get(i).getValue().toString())>0 )
			    {
			       return true;	
			    }
		   }
		}
		
		return false;
		
	}
	
	
	private void tableCmbItemDataLoad() {

		Iterator iter=dbService("select  b.vProductId,b.vProductName  from tbStretchBlowMoldingRequisitionInfo a  inner join tbStretchBlowMoldingRequisitionDetails b "
				+ " on a.vBatchNo=b.vBatchNo where a.vBatchNo='"+cmbBatchNo.getValue()+"'  order by b.vProductName");
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbItemName.get(a).addItem(element[0]);
			tbCmbItemName.get(a).setItemCaption(element[0], element[1].toString());
			tbCmbItemName.get(a).setValue(element[0]);
			a++;
			if(a==tbCmbItemName.get(a).size()){
				tableRowAdd(a+1);
			}
			
			tbIssueQty.get(a).focus();
		}
		for(int x=0;x<a;x++){
			tbCmbAction(x);
		}
		
		
	}
	private void reqIssueNoSet() {
		Iterator iter=dbService("select distinct vReqNo, vReqNo from tbStretchBlowMoldingRequisitionInfo where vBatchNo = '"+cmbBatchNo.getValue()+"'");
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtReqNo.setValue(element[1]);
		}

	}
	private void issueNoLoad(){
		Iterator iter1=dbService("select 0,isnull(max(issueNo),0)+1 id from tbIssueToSBMInfo");
		if(iter1.hasNext()){
			Object element1[]=(Object[])iter1.next();
			txtIssueNo.setValue(element1[1]);
		}
	}
	private void cmbBatchNoLoad() {
		String sql="select vBatchNo, vBatchNo  from tbStretchBlowMoldingRequisitionInfo where vBatchNo!='' order by cast(SUBSTRING(vBatchNo,CHARINDEX('-',vBatchNo)+1,len(vBatchNo)-CHARINDEX('-',vBatchNo))as int) asc";
		if(isFind){
			sql="select 0,batchNo from tbRequisitionEntryLacqureInfo where ReqFrom like '"+cmbIssueTo.getValue()+"'";
		}
		cmbBatchNo.removeAllItems();
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbBatchNo.addItem(element[1]);
			cmbBatchNo.setItemCaption(element[1], element[1].toString());
		}

	}
	private void tableInitialize()
	{
		for (int a=0; a<10; a++)
		{
			tableRowAdd(a);
		}
	}
	
	
private void tbCmbAction(int ar)
{
	
	String sql= 

         "select a.semiFgStock,(select isnull(sum(semiFgSectionStock),0)   from funcSemiFgSBMStock('"+tbCmbItemName.get(ar).getValue()+"',getdate()))vSBMQty,b.ReqQty,(select ISNULL(SUM(issueQty),0)  from tbIssueToSBMDetails where productId='"+tbCmbItemName.get(ar).getValue()+"' and batchNo='"+cmbBatchNo.getValue().toString()+"' ) issuedQty, " +
         "b.ReqQty-(select ISNULL(SUM(issueQty),0)  from tbIssueToSBMDetails where productId='"+tbCmbItemName.get(ar).getValue()+"' and batchNo='"+cmbBatchNo.getValue().toString()+"' )remaingQty,a.color "+
         "from funcSemiFgStock('"+tbCmbItemName.get(ar).getValue()+"',getdate()) a inner join "+
         "tbStretchBlowMoldingRequisitionDetails b on b.vProductId=a.semiFgId "+
  	     "where vProductId = '"+tbCmbItemName.get(ar).getValue()+"' and b.vBatchNo='"+cmbBatchNo.getValue().toString()+"'  ";
	
		/*Iterator iter=dbService("select a.semiFgStock,b.vSBMQty,b.ReqQty,"
				+ "a.issuedQty,(vSBMQty-a.semiFgStock)remaingQty,a.color"
				+ " from funcSemiFgStock('"+tbCmbItemName.get(ar).getValue()+"','"+dateFormat.format(dIssueDate.getValue())+"') a "
				+ "inner join tbStretchBlowMoldingRequisitionDetails b on b.vProductId=a.semiFgId	"
				+ "where vProductId = '"+tbCmbItemName.get(ar).getValue()+"' and b.vBatchNo='"+cmbBatchNo.getValue().toString()+"' ");
		
		*/
		
	Iterator iter=dbService(sql);
		while(iter.hasNext()){


			Object element[]=(Object[])iter.next();
			tbStoreStock.get(ar).setValue(element[0]);
			tbSMBStock.get(ar).setValue(df.format(element[1]));
			tbReqQty.get(ar).setValue(df.format(element[2]));
			tbIssuedQty.get(ar).setValue(df.format(element[3]));
			tbRemainQty.get(ar).setValue(df.format(element[4]));
			tbColor.get(ar).setValue(element[5]);

		}
	}
	
	
	
	private void tableRowAdd(final int rq)
	{
		tbSl.add(rq, new Label());
		tbSl.get(rq).setValue(rq+1);
		tbSl.get(rq).setWidth("100%");
		tbSl.get(rq).setHeight("-1px");

		tbCmbItemName.add(rq, new ComboBox() );
		tbCmbItemName.get(rq).setImmediate(true);
		tbCmbItemName.get(rq).setNullSelectionAllowed(true);
		tbCmbItemName.get(rq).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		tbCmbItemName.get(rq).setWidth("100%");
		tbCmbItemName.get(rq).setHeight("-1px");
		tbCmbItemName.get(rq).setEnabled(false);
		
		tbCmbItemName.get(rq).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tbCmbItemName.get(rq).getValue()!=null){
					if(doubleEntryCheck(rq)){
						tbCmbAction(rq);
						if(rq==tbSl.size()-1){
							tableRowAdd(rq+1);
						}
					}
					else{
						showNotification("Sorry!!","Double Entry",Notification.TYPE_WARNING_MESSAGE);
						tbCmbItemName.get(rq).setValue(null);
						tbCmbItemName.get(rq).focus();
					}
				}
			}
		});

		
		
		
		tbColor.add(rq, new Label());
		tbColor.get(rq).setImmediate(true);
		tbColor.get(rq).setWidth("-1px");
		tbColor.get(rq).setHeight("-1px");

		tbUnit.add(rq, new Label());
		tbUnit.get(rq).setImmediate(true);
		tbUnit.get(rq).setWidth("-1px");
		tbUnit.get(rq).setHeight("-1px");

		tbStoreStock.add(rq, new TextRead(1));
		tbStoreStock.get(rq).setImmediate(true);
		tbStoreStock.get(rq).setWidth("100%");
		tbStoreStock.get(rq).setHeight("-1px");

		tbSMBStock.add(rq, new TextRead(1));
		tbSMBStock.get(rq).setImmediate(true);
		tbSMBStock.get(rq).setWidth("100%");
		tbSMBStock.get(rq).setHeight("-1px");

		tbReqQty.add(rq, new TextRead(1));
		tbReqQty.get(rq).setImmediate(true);
		tbReqQty.get(rq).setWidth("100%");
		tbReqQty.get(rq).setHeight("-1px");

		tbIssuedQty.add(rq, new TextRead(1));
		tbIssuedQty.get(rq).setImmediate(true);
		tbIssuedQty.get(rq).setWidth("100%");
		tbIssuedQty.get(rq).setHeight("-1px");

		tbRemainQty.add(rq, new TextRead(1));
		tbRemainQty.get(rq).setImmediate(true);
		tbRemainQty.get(rq).setWidth("100%");
		tbRemainQty.get(rq).setHeight("-1px");

		tbIssueQty.add(rq, new AmountField());
		tbIssueQty.get(rq).setImmediate(true);
		tbIssueQty.get(rq).setWidth("100%");
		tbIssueQty.get(rq).setHeight("-1px");

		tbIssueQty.get(rq).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				/*if(!isUpdate&&!isFind){
					double remainQty=Double.parseDouble(tbRemainQty.get(rq).getValue().toString().isEmpty()?"0.0":tbRemainQty.get(rq).getValue().toString());
					double issueQty=Double.parseDouble(tbIssueQty.get(rq).getValue().toString().isEmpty()?"0.0":tbIssueQty.get(rq).getValue().toString());
					double stockQty=Double.parseDouble(tbStoreStock.get(rq).getValue().toString().isEmpty()?"0.0":tbStoreStock.get(rq).getValue().toString());

					if(stockQty>=issueQty){
						if(remainQty<issueQty){
							showNotification("Sorry!!","Issue Qty Exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
							tbIssueQty.get(rq).setValue("");
							tbIssueQty.get(rq).focus();
						}
					}
					else{
						showNotification("Sorry!!","Issue Qty Exceed Stock Qty",Notification.TYPE_WARNING_MESSAGE);
						tbIssueQty.get(rq).setValue("");
						tbIssueQty.get(rq).focus();
					}
				}*/
				
				
					double remainQty=Double.parseDouble(tbRemainQty.get(rq).getValue().toString().isEmpty()?"0.0":tbRemainQty.get(rq).getValue().toString());
					double issueQty=Double.parseDouble(tbIssueQty.get(rq).getValue().toString().isEmpty()?"0.0":tbIssueQty.get(rq).getValue().toString());
					double stockQty=Double.parseDouble(tbStoreStock.get(rq).getValue().toString().isEmpty()?"0.0":tbStoreStock.get(rq).getValue().toString());

					if(stockQty>=issueQty){
						if(remainQty<issueQty){
							showNotification("Sorry!!","Issue Qty Exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
							tbIssueQty.get(rq).setValue("");
							tbIssueQty.get(rq).focus();
						}
					}
					else{
						showNotification("Sorry!!","Issue Qty Exceed Stock Qty",Notification.TYPE_WARNING_MESSAGE);
						tbIssueQty.get(rq).setValue("");
						tbIssueQty.get(rq).focus();
					}
				
				
			}
		});

		tbRemarks.add(rq, new TextField());
		tbRemarks.get(rq).setImmediate(true);
		tbRemarks.get(rq).setWidth("100%");
		tbRemarks.get(rq).setHeight("-1px");

		table.addItem(new Object[]{tbSl.get(rq),tbCmbItemName.get(rq),tbUnit.get(rq),tbColor.get(rq),tbStoreStock.get(rq),
				tbSMBStock.get(rq),tbReqQty.get(rq),tbIssuedQty.get(rq),tbRemainQty.get(rq),tbIssueQty.get(rq),tbRemarks.get(rq)},rq);

	}
	private void findButtonEvent(){
		Window win = new IssueToSBMFind(sessionBean, txtItemID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtItemID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtItemID.getValue().toString());
					//System.out.println("Issue No: "+txtItemID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}
	private void findInitialise(String issueNo){
		Iterator iter=dbService("select a.issueTo,a.batchNo,a.ReqNo,a.issueDate,a.reqDate,a.issueNo, "+
				" b.productId,b.ReqQty,b.IssuedQty,b.remainQty,b.issueQty,b.remarks "+
				" from tbIssueToSBMInfo a inner join tbIssueToSBMDetails b "+ 
				" on a.issueNo=b.issueNo where a.issueNo='"+issueNo+"'");
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==0){
				cmbIssueTo.setValue(element[0]);
				cmbBatchNo.setValue(element[1]);
				txtReqNo.setValue(element[2]);
				dIssueDate.setValue(element[3]);
				dReqDate.setValue(element[4]);
				txtIssueNo.setValue(element[5]);
			}
			tbCmbItemName.get(a).setValue(element[6]);
			tbReqQty.get(a).setValue(df.format(element[7]));
			tbIssuedQty.get(a).setValue(df.format(element[8]));
			tbRemainQty.get(a).setValue(df.format(element[9]));
			double storeStock=Double.parseDouble(tbStoreStock.get(a).getValue().toString().isEmpty()?"0.00":tbStoreStock.get(a).getValue().toString())+Double.parseDouble(element[10].toString());
			tbStoreStock.get(a).setValue(storeStock);
			tbIssueQty.get(a).setValue(df.format(element[10]));
			tbRemarks.get(a).setValue(element[11]);
			a++;
			if(a==tbCmbItemName.size()-1){
				tableRowAdd(a+1);
			}
		}
	}
	private AbsoluteLayout buildMainLayout() {
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("1065px");
		setHeight("520px");

		cmbIssueTo=new ComboBox();
		cmbIssueTo.setImmediate(true);
		cmbIssueTo.setWidth("200px");
		cmbIssueTo.setHeight("24px");
		cmbIssueTo.setNullSelectionAllowed(true);
		cmbIssueTo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbIssueTo.addItem("SBM");
		cmbIssueTo.setValue("SBM");
		cmbIssueTo.setEnabled(false);
		mainLayout.addComponent(new Label("Issue To: "),"top:10.0px;left:10px;");
		mainLayout.addComponent(cmbIssueTo,"top:8.0px;left:100px;");

		cmbBatchNo=new ComboBox();
		cmbBatchNo.setImmediate(true);
		cmbBatchNo.setWidth("200px");
		cmbBatchNo.setHeight("24px");
		cmbBatchNo.setNullSelectionAllowed(true);
		cmbBatchNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Batch No: "),"top:35.0px;left:10px;");
		mainLayout.addComponent(cmbBatchNo,"top:33.0px;left:100px;");

		txtReqNo=new TextRead(1);
		txtReqNo.setImmediate(true);
		txtReqNo.setWidth("100px");
		txtReqNo.setHeight("24px");
		mainLayout.addComponent(new Label("Requisition No: "),"top:60.0px;left:10px;");
		mainLayout.addComponent(txtReqNo,"top:58.0px;left:100px;");


		dIssueDate = new PopupDateField();
		dIssueDate.setImmediate(true);
		dIssueDate.setWidth("110px");
		dIssueDate.setDateFormat("dd-MM-yyyy");
		dIssueDate.setValue(new java.util.Date());
		dIssueDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label("Issue Date: "),"top:10.0px;left:840px;");
		mainLayout.addComponent(dIssueDate,"top:8.0px;left:940px;");


		dReqDate = new PopupDateField();
		dReqDate.setImmediate(true);
		dReqDate.setWidth("110px");
		dReqDate.setDateFormat("dd-MM-yyyy");
		dReqDate.setValue(new java.util.Date());
		dReqDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label("Requisition Date: "),"top:35.0px;left:840px;");
		mainLayout.addComponent(dReqDate,"top:33.0px;left:940px;");

		txtIssueNo=new TextRead(1);
		txtIssueNo.setImmediate(true);
		txtIssueNo.setWidth("100px");
		txtIssueNo.setHeight("24px");
		mainLayout.addComponent(new Label("Issue No: "),"top:60.0px;left:840px;");
		mainLayout.addComponent(txtIssueNo,"top:58.0px;left:940px;");


		table.setWidth("99%");
		table.setHeight("320px");
		table.setFooterVisible(true);
		table.setImmediate(true); 
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 15);
		table.setColumnAlignment("SL", table.ALIGN_CENTER);

		table.addContainerProperty("ITEM Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("ITEM Name",320);

		table.addContainerProperty("Unit", Label.class , new Label());
		table.setColumnWidth("Unit",40);
		table.setColumnCollapsed("Unit", true);

		table.addContainerProperty("Color", Label.class , new Label());
		table.setColumnWidth("Color",80);
		table.setColumnCollapsed("Color", true);

		table.addContainerProperty("Store Stock", TextRead.class , new TextRead(1));
		table.setColumnWidth("Store Stock",80);

		table.addContainerProperty("SECTION Stock", TextRead.class , new TextRead(1));
		table.setColumnWidth("SECTION Stock",85);

		table.addContainerProperty("Req Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Req Qty",60);

		table.addContainerProperty("Issued Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Issued Qty",60);

		table.addContainerProperty("Remain Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Remain Qty",60);

		table.addContainerProperty("Issue Qty", AmountField.class , new AmountField());
		table.setColumnWidth("Issue Qty",60);

		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",150);

		mainLayout.addComponent(table,"top:90.0px;left:10px;");
		tableInitialize();

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:410.0px;left:0.0px;");
		mainLayout.addComponent(cButton, "top:430.0px;left:260.0px;");
		return mainLayout;
	}

}
