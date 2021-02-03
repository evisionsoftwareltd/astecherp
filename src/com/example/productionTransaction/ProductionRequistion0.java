package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.vaadin.data.Property.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
public class ProductionRequistion0 extends Window {

	SessionBean sessionBean;
	AbsoluteLayout mainLayout;

	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "Preview", "", "Exit");
	Label lblFrom,lblTo,lblReqNo,lblReqRef,lblCurDate,lblDeliveryDate,lblLine,lblType,lblParty,lblFromDate,lblToDate;

	ComboBox cmbFrom,cmbTo,cmbProductionType,cmbParty;
	TextRead txtReqNo;
	TextArea txtReqRef;
	AmountField txtTolerance;
	PopupDateField dCurDate,dDeliveryDate,dFromDate,dToDate;
	PopupDateField fromDate = new PopupDateField("From Date");
	PopupDateField toDate = new PopupDateField("To Date");
	ListSelect typeList;
	CheckBox chkAll,chkNew,chkEdit;

	DecimalFormat df=new DecimalFormat("#0.00");
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

	ArrayList<Label>tbJobSl=new ArrayList<Label>();
	ArrayList<CheckBox>tbJobChk=new ArrayList<CheckBox>();
	ArrayList<Label>tbJobOrderNo=new ArrayList<Label>();
	Table tableJobOrder=new Table();

	ArrayList<Label>tbSl=new ArrayList<Label>();
	ArrayList<ComboBox>tbCmbProductName=new ArrayList<ComboBox>();
	ArrayList<Label>tbUnit=new ArrayList<Label>();
	ArrayList<Label>tbMerge=new ArrayList<Label>();
	ArrayList<TextRead>tbStock=new ArrayList<TextRead>();
	ArrayList<TextRead>tbjobQty=new ArrayList<TextRead>();
	ArrayList<TextRead>tbRcvQty=new ArrayList<TextRead>();
	ArrayList<TextRead>tbRemainQty=new ArrayList<TextRead>();
	ArrayList<TextRead>tbToleranceQty=new ArrayList<TextRead>();
	ArrayList<AmountField>tbPercantage=new ArrayList<AmountField>();
	ArrayList<AmountField>tbReqQty=new ArrayList<AmountField>();
	ArrayList<TextRead>tbRequisiteQty=new ArrayList<TextRead>(1);
	ArrayList<TextField>tbRemarks=new ArrayList<TextField>();
	ArrayList<Label>tbProductID=new ArrayList<Label>();
	ArrayList<TextRead>tbReqRemain=new ArrayList<TextRead>();
	Table tableReq=new Table();

	boolean isFind=false,isUpdate=false;

	public ProductionRequistion0(SessionBean sessionBean){
		this.sessionBean = sessionBean;
		this.setCaption("Production Requisition Entry :: "+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		tableInitializeJob();
		tableInitializeReq();
		btnIni(true);
		componentIni(true);
		txtClear();
		setEventAction();
		productionTypeData();
		cmbFromDataLoad();
		cmbToDataLoad();
		orderNoLoadData("%");
		focusMove();
		typeListDataLoad();
		partyNameLoad();
	}
	private void partyNameLoad() {
		cmbParty.removeAllItems();
		List list=dbService("select  vGroupId,partyName from tbPartyInfo where vGroupId in(select distinct partyId from tbJobOrderInfo ) order by vGroupId");
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			cmbParty.addItem(element[0]);
			cmbParty.setItemCaption(element[0], element[1].toString());
		}
	}
	private void typeListDataLoad(){
		typeList.removeAllItems();
		List list=dbService("select reqNo,reqRef from tbProductionRequisitionInfo");
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			typeList.addItem(element[0]);
			String caption=element[0]+">>"+element[1].toString();
			typeList.setItemCaption(element[0], caption);
		}
	}
	private void typeListDataLoadDateBetween(){
		typeList.removeAllItems();
		List list=dbService("select reqNo,reqRef from tbProductionRequisitionInfo " +
				"where convert(date,reqDate,105) between '"+dateFormat.format(fromDate.getValue())+"' and '"+dateFormat.format(toDate.getValue())+"'");
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			typeList.addItem(element[0]);
			String caption=element[0]+">>"+element[1].toString();
			typeList.setItemCaption(element[0], caption);
		}
	}
	private void focusMove(){
		ArrayList<Component>allComp=new ArrayList<Component>();

		allComp.add(cmbFrom);
		allComp.add(cmbTo);
		allComp.add(cmbProductionType);
		//allComp.add(txtTolerance);
		for(int a=0;a<tbCmbProductName.size();a++){
			allComp.add(tbPercantage.get(a));
			allComp.add(tbReqQty.get(a));
		}
		new FocusMoveByEnter(this,allComp);
	}
	private void orderNoLoadData(String partyId) {
		List list=dbService("select 0,orderNo from tbJobOrderInfo where isActive=1 and partyId like '"+partyId+"' and " +
				"orderNo not in(select orderNo from funcOrderNoLodToReq (''))");
		int a=0;
		tableJobOrderClear();
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			tbJobSl.get(a).setValue(a+1);
			tbJobOrderNo.get(a).setValue(element[1]);
			a++;
			if(a==tbJobSl.size()){
				tableRowAddJob(a);
			}
		}
	}
	private void reqNoLoadData(){

		List list=dbService("select 0,isnull(max(cast(SUBSTRING(reqNo,CHARINDEX('-',reqNo)+1,len(reqNo)-CHARINDEX('-',reqNo)) as int)),0)+1 as autoId from tbProductionRequisitionInfo");
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			txtReqNo.setValue("Req-"+element[1]);
		}
	}
	private void cmbToDataLoad() {
		cmbTo.removeAllItems();
		List list=dbService("select vDepoId,vDepoName from tbDepoInformation");
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			cmbTo.addItem(element[0]);
			cmbTo.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbFromDataLoad() {
		cmbFrom.removeAllItems();
		List list=dbService("select AutoID,SectionName from tbSectionInfo");
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			cmbFrom.addItem(element[0].toString());
			cmbFrom.setItemCaption(element[0].toString(), element[1].toString());
		}
	}
	private void productionTypeData() {

		cmbProductionType.removeAllItems();
		List list=dbService("select productTypeId,productTypeName from tbProductionType");
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}
	}
	private void setEventAction() {
		cButton.btnNew.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				newButtonEvent();
				reqNoLoadData();
				chkNew.setValue(true);
				orderNoLoadData("%");
			}
		});
		cButton.btnRefresh.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
				
			}
		});
		cButton.btnFind.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				isFind=true;
				componentIni(true);
				fromDate.setEnabled(true);
				toDate.setEnabled(true);
				typeList.setEnabled(true);
			}
		});
		cButton.btnSave.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				saveButtonEvent();
			}
		});
		cButton.btnEdit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(sessionBean.isUpdateable())
				{
					updateButtonEvent();
					tableJobOrder.setEnabled(false);
					txtReqRef.setEnabled(false);
					cmbParty.setEnabled(false);
					chkEdit.setValue(true);
				}
				else
				{
					showNotification("Warning,","You are not permitted to edit data.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnExit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		/*chkAll.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(chkAll.booleanValue()){
					cmbProductionType.setValue(null);
					cmbProductionType.setEnabled(false);
				}
				else{
					cmbProductionType.setEnabled(true);
				}
			}
		});*/
		/*txtTolerance.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(!txtTolerance.getValue().toString().isEmpty()){
					calcTolerance();
				}
			}
		});*/
		txtReqRef.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!txtReqRef.getValue().toString().isEmpty()){
					reqRefAction();
					/*if(!txtTolerance.getValue().toString().isEmpty()){
						calcTolerance();
					}*/
				}
				else{
					tableReqClear();
				}
			}
		});
		typeList.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(typeList.getValue()!=null){
					typeListSelectData(typeList.getValue().toString());
				}
			}
		});
		cmbParty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbParty.getValue()!=null){
					orderNoLoadData(cmbParty.getValue().toString());
				}
			}
		});
		fromDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				typeListDataLoadDateBetween();
				//typeList.requestRepaintRequests();
			}
		});
		toDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				typeListDataLoadDateBetween();
				//typeList.requestRepaintRequests();
			}
		});
		chkNew.addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event)
			{
				
				if(chkNew.booleanValue()==true)
				{
					reqNoLoadData();
					isFind=false;
					for(int i=0;i<tbSl.size();i++){
						tbReqQty.get(i).setValue("");
					}
					chkEdit.setValue(false);
				}
				else
					chkEdit.setValue(true);

			}
		});

		chkEdit.addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event)
			{

				if(chkEdit.booleanValue()==true)
					chkNew.setValue(false);
				else
					chkNew.setValue(true);

			}
		});
	}
	private void updateButtonEvent(){
		if (cmbTo.getValue()!=null&&!txtReqNo.getValue().toString().isEmpty()&&!txtReqRef.getValue().toString().isEmpty()) 
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
			fromDate.setEnabled(false);
			toDate.setEnabled(false);
			typeList.setEnabled(false);
			/*if(chkAll.booleanValue()){
				cmbProductionType.setEnabled(false);
			}*/
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void typeListSelectData(String id){
		List list=dbService("select a.FromSection,a.toStore,a.reqNo,a.reqRef,a.reqDate,a.deliveryDate, "+
				" a.productionTypeId,isnull(b.tolerance,0)as tolerance,b.reqQty,b.remarks from tbProductionRequisitionInfo a  "+
				" inner join tbProductionRequisitionDetails b on a.reqNo=b.reqNo where a.reqNo like '"+id+"'");
		int a=0;
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			cmbFrom.setValue(element[0].toString());
			cmbTo.setValue(element[1]);
			txtReqNo.setValue(element[2]);
			txtReqRef.setValue(element[3]);
			dCurDate.setValue(element[4]);
			dDeliveryDate.setValue(element[5]);
			cmbProductionType.setValue(element[6]);
			//tbPercantage.get(a).setValue(df.format(element[7]));
			tbRemarks.get(a).setValue(element[9]);
			a++;
		}

		List list1=dbService("select *,cast(subString(rawItemId,CHARINDEX('-',rawItemId)+1,len(rawItemId)-CHARINDEX('-',rawItemId))as int) as sl " +
				" from funcRequisitionFindData ('"+txtReqRef.getValue()+"','"+txtReqNo.getValue()+"') order by sl");
		for(Iterator iter1=list1.iterator();iter1.hasNext();) {
			Object[] element1=(Object[]) iter1.next();
			String rawId=element1[0].toString();

			for(int x=0;x<tbSl.size();x++){
				if(tbCmbProductName.get(x).getValue()!=null){
					if(tbCmbProductName.get(x).getValue().toString().equalsIgnoreCase(rawId)){
						tbReqQty.get(x).setValue(df.format(element1[2]));
						tbPercantage.get(x).setValue(df.format(element1[3]));
						tbRcvQty.get(x).setValue(df.format(element1[4]));
						tbRequisiteQty.get(x).setValue(df.format(element1[5]));
					}
				}
			}
		}
	}
	private void calcTolerance(){
		for(int a=0;a<tbCmbProductName.size();a++){
			if(tbCmbProductName.get(a).getValue()!=null){
				double remain=Double.parseDouble(tbRemainQty.get(a).getValue().toString().isEmpty()?"0.0":tbRemainQty.get(a).getValue().toString());
				double tolerance=Double.parseDouble(txtTolerance.getValue().toString().isEmpty()?"0.0":txtTolerance.getValue().toString());
				double percentageValue=(remain*tolerance)/100;
				tbToleranceQty.get(a).setValue(df.format(remain+percentageValue));
			}
		}
	}
	private void reqRefAction() {
		tableReqClear();
		List list=dbService("select *,cast(subString(rawItemId,CHARINDEX('-',rawItemId)+1,len(rawItemId)-CHARINDEX('-',rawItemId))as int) as sl " +
				" from funcProductionRequisitionLoadData ('"+txtReqRef.getValue().toString().trim()+"') order by sl");
		int a=0;
		for(Iterator iter=list.iterator();iter.hasNext();) {
			Object[] element=(Object[]) iter.next();
			tbCmbProductName.get(a).removeAllItems();
			tbCmbProductName.get(a).addItem(element[0]);
			tbCmbProductName.get(a).setItemCaption(element[0], element[1].toString());
			tbCmbProductName.get(a).setValue(element[0]);
			tbUnit.get(a).setValue(element[2]);
			tbStock.get(a).setValue(df.format(element[3]));
			tbjobQty.get(a).setValue(df.format(element[8]));
			tbRcvQty.get(a).setValue(df.format(0.0));
			tbPercantage.get(a).setValue(0);
			if(a==tbSl.size()-1){
				tableRowAddReq(a+1);
			}
			a++;
		}
	}
	private boolean tableCheck(){
		int count,a;
		for( a=0,count=0;a<tbSl.size();a++){

			if(tbCmbProductName.get(a).getValue()!=null){
				if(tbRemainQty.get(a).getValue().toString().isEmpty()){
					count=1;
					break;
				}
			}
		}
		if(count==1){
			return false;
		}
		else{
			return true;
		}
	}
	private void saveButtonEvent(){
		if(cmbFrom.getValue()!=null){
			if(cmbTo.getValue()!=null){
				if(!txtReqRef.getValue().toString().isEmpty()){
					//if(!txtTolerance.getValue().toString().isEmpty()){
					if(tableCheck()){
						if(isUpdate){
							messageBox("Do you want to update ?","update");
						}
						else{
							messageBox("Do you want to save ?","save");
						}
					}
					else{
						this.showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
					}
					/*}
					else{
						this.showNotification("Please Provide Tolerance",Notification.TYPE_WARNING_MESSAGE);
						txtTolerance.focus();
					}*/
				}
				else{
					this.showNotification("Please Select  Job Order",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				this.showNotification("Please Select  To",Notification.TYPE_WARNING_MESSAGE);
				cmbTo.focus();
			}
		}
		else{
			this.showNotification("Please Select  From",Notification.TYPE_WARNING_MESSAGE);
			cmbFrom.focus();
		}
	}
	private boolean deleteData()
	{
		Transaction tx=null;
		Session session = null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			String sqlDetails="delete from tbProductionRequisitionDetails where reqNo like '"+txtReqNo.getValue()+"'";
			session.createSQLQuery(sqlDetails).executeUpdate();
			String sqlInfo="delete from tbProductionRequisitionInfo where reqNo like '"+txtReqNo.getValue()+"'";
			session.createSQLQuery(sqlInfo).executeUpdate();
			return true;
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null){
				tx.commit();
				session.close();
			}
		}
		return false;
	}
	private void messageBox(String caption,final String from){

		MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, caption,new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener() 
		{
			public void buttonClicked(ButtonType buttonType) 
			{
				if(buttonType == ButtonType.YES)
				{
					if(from.equalsIgnoreCase("update")&chkEdit.booleanValue()){
						if(deleteData()){

							insertData();
							txtClear();
							componentIni(true);
							btnIni(true);
							typeListDataLoad();
						}
					}
					else if(from.equalsIgnoreCase("save")||chkNew.booleanValue()){

						insertData();
						txtClear();
						componentIni(true);
						btnIni(true);
						typeListDataLoad();
					}
				}
			}
		});
	}

	private void insertData() {
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=null;
		try{
			tx=session.beginTransaction();
			String sql="insert into tbProductionRequisitionInfo(reqNo,reqRef,FromSection,toStore,reqDate,deliveryDate,productionTypeId, "+
					" tolerance,isActive,userIp,userName,entryTime)values('"+txtReqNo.getValue()+"','"+txtReqRef.getValue()+"','"+cmbFrom.getValue()+"'," +
					"'"+cmbTo.getValue()+"','"+dateFormat.format(dCurDate.getValue())+"', "+
					" '"+dateFormat.format(dDeliveryDate.getValue())+"','"+cmbProductionType.getValue()+"'," +
					"'0.0',1,'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
			session.createSQLQuery(sql).executeUpdate();
			for(int a=0;a<tbCmbProductName.size();a++){
				if(tbCmbProductName.get(a).getValue()!=null&&!tbReqQty.get(a).getValue().toString().isEmpty())
				{
					String sqlDetails="exec RequisitionSave '"+txtReqNo.getValue()+"','"+tbCmbProductName.get(a).getValue()+"'," +
							"'"+tbReqQty.get(a).getValue()+"','"+txtReqRef.getValue()+"','"+tbRemarks.get(a).getValue()+"','"+tbPercantage.get(a).getValue()+"'";
					session.createSQLQuery(sqlDetails).executeUpdate();
				}
			}

		}
		catch(Exception exp){
			tx.rollback();
			this.getParent().showNotification("Erron in Insert: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{			
			if(tx!=null){
				tx.commit();
				session.close();
				this.getParent().showNotification("All information save successfully.");
			}
		}
	}
	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		tableReqClear();
		tableJobOrderClear();
		isFind = false;

		fromDate.setEnabled(false);
		toDate.setEnabled(false);
		typeList.setEnabled(false);
	}
	private void refreshButtonEvent()
	{
		componentIni(true);
		btnIni(true);
		txtClear();
		tableReqClear();
		tableJobOrderClear();
		isFind = false;
		isUpdate = false;
	}
	private void txtClear() {
		fromDate.setValue(new java.util.Date());
		toDate.setValue(new java.util.Date());
		cmbFrom.setValue(null);
		cmbTo.setValue(null);
		cmbProductionType.setValue(null);
		cmbParty.setValue(null);
		//chkAll.setValue(false);
		txtReqNo.setValue("");

		//txtTolerance.setValue("");
		dCurDate.setValue(new java.util.Date());
		dDeliveryDate.setValue(new java.util.Date());

		txtReqRef.setValue("");
		chkNew.setValue(false);
		chkEdit.setValue(false);
		
		tableJobOrderClear();
		tableReqClear();

	}
	private void tableReqClear() {
		for(int a=0;a<tbSl.size();a++){

			tbCmbProductName.get(a).setValue(null);
			tbUnit.get(a).setValue("");
			tbMerge.get(a).setValue("");
			tbStock.get(a).setValue("");
			tbjobQty.get(a).setValue("");
			tbRcvQty.get(a).setValue("");
			tbRemainQty.get(a).setValue("");
			tbPercantage.get(a).setValue("");
			tbToleranceQty.get(a).setValue("");
			tbRequisiteQty.get(a).setValue("");
			tbReqRemain.get(a).setValue("");
			tbReqQty.get(a).setValue("");
			tbRemarks.get(a).setValue("");
			
			
			//tbProductID.get(a).setValue("");
		}
	}
	private void tableJobOrderClear() {

		for(int a=0;a<tbJobChk.size();a++){

			tbJobChk.get(a).setValue(false);
			tbJobOrderNo.get(a).setValue("");
		}
	}
	private void componentIni(boolean b) {
		lblFrom.setEnabled(!b);
		lblTo.setEnabled(!b);
		lblReqNo.setEnabled(!b);
		lblReqRef.setEnabled(!b);
		lblCurDate.setEnabled(!b);
		lblDeliveryDate.setEnabled(!b);
		lblLine.setEnabled(!b);
		lblType.setEnabled(!b);
		cmbFrom.setEnabled(!b);
		cmbTo.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		txtReqNo.setEnabled(!b);
		txtReqRef.setEnabled(!b);
		dCurDate.setEnabled(!b);
		dDeliveryDate.setEnabled(!b);
		cmbParty.setEnabled(!b);
		lblParty.setEnabled(!b);
		//chkAll.setEnabled(!b);
		tableJobOrder.setEnabled(!b);
		tableReq.setEnabled(!b);
		lblLine.setEnabled(!b);
		//lblTolerance.setEnabled(!b);
		//txtTolerance.setEnabled(!b);

		fromDate.setEnabled(!b);
		toDate.setEnabled(!b);
		typeList.setEnabled(!b);
		chkEdit.setEnabled(!b);
		chkNew.setEnabled(!b);
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
	private void tableInitializeJob() {
		for(int a=0;a<10;a++){
			tableRowAddJob(a);
		}
	}
	private String getJobOrderRef(){

		String s="";
		int count=0;
		for(int a=0;a<tbJobChk.size();a++){
			if(tbJobChk.get(a).booleanValue()){
				if(!s.isEmpty()){
					if(count==0){
						s=s+','+tbJobOrderNo.get(a).getValue().toString()+',';	
						count++;
					}
					else{
						s=s+tbJobOrderNo.get(a).getValue().toString()+',';	
					}
				}
				else{
					s=s+tbJobOrderNo.get(a).getValue().toString();	
				}
			}
		}
		if(s.lastIndexOf(",")==s.length()-1){
			if(!s.isEmpty()){
				s=s.substring(0,s.length()-1);
			}
		}
		return s;
	}
	private void tableRowAddJob(int ar) {

		tbJobSl.add(ar, new Label());
		tbJobSl.get(ar).setValue(ar+1);
		tbJobSl.get(ar).setWidth("-1px");
		tbJobSl.get(ar).setHeight("-1px");

		tbJobChk.add(ar, new CheckBox());
		tbJobChk.get(ar).setImmediate(true);

		tbJobChk.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				txtReqRef.setValue(getJobOrderRef());
			}
		});

		tbJobOrderNo.add(ar, new Label());
		//tbJobOrderNo.get(ar).setValue("JOB-ORDER "+(ar+1));
		tbJobOrderNo.get(ar).setImmediate(true);
		tbJobOrderNo.get(ar).setWidth("-1px");
		tbJobOrderNo.get(ar).setHeight("-1px");


		tableJobOrder.addItem(new Object[]{tbJobSl.get(ar),tbJobChk.get(ar),tbJobOrderNo.get(ar)},ar);
	}
	private void tableInitializeReq() {
		for(int a=0;a<8;a++){
			tableRowAddReq(a);
		}
	}
	public List dbService(String sql){
		List list = null;
		Transaction tx = null;
		Session session = null;
		try
		{
			session = SessionFactoryUtil.getInstance().openSession();
			tx = session.beginTransaction();
			list=session.createSQLQuery(sql).list();
			return list;
		}
		catch(Exception exp){

		}
		finally{
			if(tx!=null){
				session.close();
			}
		}

		return list;
	}
	private void calcRemainQty(int ar){
		double toleranceQty=Double.parseDouble(tbToleranceQty.get(ar).getValue().toString().isEmpty()?"0.0":tbToleranceQty.get(ar).getValue().toString());
		double requisiteQty=Double.parseDouble(tbRequisiteQty.get(ar).getValue().toString().isEmpty()?"0.0":tbRequisiteQty.get(ar).getValue().toString());
		
		tbReqRemain.get(ar).setValue(df.format(toleranceQty-requisiteQty));
	}
	private void tableRowAddReq(final int ar) {

		tbSl.add(ar, new Label());
		tbSl.get(ar).setValue(ar+1);
		tbSl.get(ar).setWidth("100%");
		tbSl.get(ar).setHeight("-1px");

		tbCmbProductName.add(ar, new ComboBox());
		tbCmbProductName.get(ar).setImmediate(true);
		tbCmbProductName.get(ar).setNullSelectionAllowed(false);
		tbCmbProductName.get(ar).setWidth("100%");
		tbCmbProductName.get(ar).setHeight("-1px");
		tbCmbProductName.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		tbUnit.add(ar, new Label());
		tbUnit.get(ar).setWidth("100%");
		tbUnit.get(ar).setHeight("-1px");
		tbUnit.get(ar).setImmediate(true);

		tbMerge.add(ar, new Label());
		tbMerge.get(ar).setWidth("100%");
		tbMerge.get(ar).setHeight("-1px");
		tbMerge.get(ar).setImmediate(true);

		tbStock.add(ar, new TextRead(1));
		tbStock.get(ar).setImmediate(true);
		tbStock.get(ar).setWidth("100%");
		tbStock.get(ar).setHeight("-1px");

		tbjobQty.add(ar, new TextRead(1));
		tbjobQty.get(ar).setImmediate(true);
		tbjobQty.get(ar).setWidth("100%");
		tbjobQty.get(ar).setHeight("-1px");

		tbRcvQty.add(ar, new TextRead(1));
		tbRcvQty.get(ar).setImmediate(true);
		tbRcvQty.get(ar).setWidth("100%");
		tbRcvQty.get(ar).setHeight("-1px");

		tbRcvQty.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(!tbRcvQty.get(ar).getValue().toString().isEmpty()){
					double rcv,req;
					rcv=Double.parseDouble(tbRcvQty.get(ar).getValue().toString().isEmpty()?"0.0":tbRcvQty.get(ar).getValue().toString());
					req=Double.parseDouble(tbjobQty.get(ar).getValue().toString().isEmpty()?"0.0":tbjobQty.get(ar).getValue().toString());
					tbRemainQty.get(ar).setValue(req-rcv);
				}
				if(tbCmbProductName.get(ar).getValue()!=null){
					double remain=Double.parseDouble(tbjobQty.get(ar).getValue().toString().isEmpty()?"0.0":tbjobQty.get(ar).getValue().toString());
					double tolerance=Double.parseDouble(tbPercantage.get(ar).getValue().toString().isEmpty()?"0.0":tbPercantage.get(ar).getValue().toString());
					double percentageValue=(remain*tolerance)/100;
					double rcvQty=Double.parseDouble(tbRcvQty.get(ar).getValue().toString().isEmpty()?"0.0":tbRcvQty.get(ar).getValue().toString());
					//tbToleranceQty.get(ar).setValue(df.format(((remain+percentageValue))-rcvQty));
				}
			}
		});

		tbRemainQty.add(ar, new TextRead(1));
		tbRemainQty.get(ar).setImmediate(true);
		tbRemainQty.get(ar).setWidth("100%");
		tbRemainQty.get(ar).setHeight("-1px");

		tbToleranceQty.add(ar, new TextRead(1));
		tbToleranceQty.get(ar).setImmediate(true);
		tbToleranceQty.get(ar).setWidth("100%");
		tbToleranceQty.get(ar).setHeight("-1px");
		
		tbToleranceQty.get(ar).addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(!tbToleranceQty.get(ar).getValue().toString().isEmpty()){
					calcRemainQty(ar);
				}
			}
		});

		tbPercantage.add(ar, new AmountField());
		tbPercantage.get(ar).setImmediate(true);
		tbPercantage.get(ar).setWidth("100%");
		tbPercantage.get(ar).setHeight("-1px");
		//tbPercantage.get(ar).setEnabled(false);

		tbPercantage.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(tbCmbProductName.get(ar).getValue()!=null){
					double remain=Double.parseDouble(tbjobQty.get(ar).getValue().toString().isEmpty()?"0.0":tbjobQty.get(ar).getValue().toString());
					double tolerance=Double.parseDouble(tbPercantage.get(ar).getValue().toString().isEmpty()?"0.0":tbPercantage.get(ar).getValue().toString());
					double percentageValue=(remain*tolerance)/100;
					double rcvQty=Double.parseDouble(tbRcvQty.get(ar).getValue().toString().isEmpty()?"0.0":tbRcvQty.get(ar).getValue().toString());
					tbToleranceQty.get(ar).setValue(df.format(((remain+percentageValue))-rcvQty));
				}
			}
		});

		tbReqQty.add(ar, new AmountField());
		tbReqQty.get(ar).setImmediate(true);
		tbReqQty.get(ar).setWidth("100%");
		tbReqQty.get(ar).setHeight("-1px");

		tbReqQty.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				if(!isFind){
					double reqRemain=Double.parseDouble(tbReqRemain.get(ar).getValue().toString().isEmpty()?"0.0":tbReqRemain.get(ar).getValue().toString());
					double reqQTy=Double.parseDouble(tbReqQty.get(ar).getValue().toString().isEmpty()?"0.0":tbReqQty.get(ar).getValue().toString());
					if(reqQTy>reqRemain){
						tbReqQty.get(ar).setValue("");
						tbReqQty.get(ar).focus();
						showNotification("Requisition Qty Exceed Remain Qty ",Notification.TYPE_WARNING_MESSAGE);

					}
				}
				/*else if(isFind&&chkNew.booleanValue()){
					double reqRemain=Double.parseDouble(tbReqRemain.get(ar).getValue().toString().isEmpty()?"0.0":tbReqRemain.get(ar).getValue().toString());
					double reqQTy=Double.parseDouble(tbReqQty.get(ar).getValue().toString().isEmpty()?"0.0":tbReqQty.get(ar).getValue().toString());
					if(reqQTy>reqRemain){
						tbReqQty.get(ar).setValue("");
						tbReqQty.get(ar).focus();
						showNotification("Requisition Qty Exceed Remain Qty ",Notification.TYPE_WARNING_MESSAGE);

					}
				}*/


			}
		});
		
		tbRequisiteQty.add(ar, new TextRead(1));
		tbRequisiteQty.get(ar).setImmediate(true);
		tbRequisiteQty.get(ar).setWidth("100%");
		tbRequisiteQty.get(ar).setHeight("-1px");
		
		tbRequisiteQty.get(ar).addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				calcRemainQty(ar);
			}
		});

		tbReqRemain.add(ar, new TextRead(1));
		tbReqRemain.get(ar).setImmediate(true);
		tbReqRemain.get(ar).setWidth("100%");
		tbReqRemain.get(ar).setHeight("-1px");
		
		tbRemarks.add(ar, new TextField());
		tbRemarks.get(ar).setImmediate(true);
		tbRemarks.get(ar).setWidth("100%");
		tbRemarks.get(ar).setHeight("-1px");

		/*tbProductID.add(ar, new Label());
		tbProductID.get(ar).setWidth("100%");
		tbProductID.get(ar).setHeight("-1px");
		tbProductID.get(ar).setImmediate(true);*/

		tableReq.addItem(new Object[]{tbSl.get(ar),tbCmbProductName.get(ar),tbUnit.get(ar),tbMerge.get(ar),tbStock.get(ar),
				tbjobQty.get(ar),tbPercantage.get(ar),tbToleranceQty.get(ar),tbRequisiteQty.get(ar),
				tbRemainQty.get(ar),tbReqRemain.get(ar),tbRcvQty.get(ar),tbReqQty.get(ar),tbRemarks.get(ar)},ar);
	}
	private AbsoluteLayout buildMainLayout(){
		// mainLayout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("1250px");
		setHeight("668px");

		fromDate = new PopupDateField("From Date: ");
		fromDate.setImmediate(true);
		fromDate.setWidth("-1px");
		fromDate.setHeight("-1px");
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		toDate = new PopupDateField("To Date: ");
		toDate.setImmediate(true);
		toDate.setWidth("-1px");
		toDate.setHeight("-1px");
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);

		typeList=new ListSelect();
		typeList.setWidth("200px");
		typeList.setHeight("420px");
		typeList.setImmediate(true);
		typeList.setMultiSelect(false);
		typeList.setNullSelectionAllowed(true);



		// lblFrom
		lblFrom = new Label("From :");
		lblFrom.setImmediate(false);
		lblFrom.setWidth("-1px");
		lblFrom.setHeight("-1px");

		// cmbFrom
		cmbFrom = new ComboBox();
		cmbFrom.setImmediate(true);
		cmbFrom.setNullSelectionAllowed(false);
		cmbFrom.setWidth("190px");
		cmbFrom.setHeight("-1px");

		// lblTo
		lblTo = new Label("To :");
		lblTo.setImmediate(false);
		lblTo.setWidth("-1px");
		lblTo.setHeight("-1px");

		// cmbTo
		cmbTo = new ComboBox();
		cmbTo.setImmediate(true);
		cmbTo.setNullSelectionAllowed(false);
		cmbTo.setWidth("190px");
		cmbTo.setHeight("-1px");

		lblReqNo = new Label("Req No :");
		lblReqNo.setImmediate(false);
		lblReqNo.setWidth("-1px");
		lblReqNo.setHeight("-1px");

		txtReqNo = new TextRead(1);
		txtReqNo.setImmediate(false);
		txtReqNo.setWidth("80px");
		txtReqNo.setHeight("23px");

		lblReqRef = new Label("Req Ref. :");
		lblReqRef.setImmediate(false);
		lblReqRef.setWidth("-1px");
		lblReqRef.setHeight("-1px");

		txtReqRef = new TextArea();
		txtReqRef.setImmediate(false);
		txtReqRef.setWidth("300px");
		txtReqRef.setRows(1);

		lblCurDate = new Label("Date :");
		lblCurDate.setImmediate(false);
		lblCurDate.setWidth("-1px");
		lblCurDate.setHeight("-1px");

		dCurDate = new PopupDateField();
		dCurDate.setImmediate(false);
		dCurDate.setWidth("-1px");
		dCurDate.setHeight("-1px");
		dCurDate.setDateFormat("dd-MM-yyyy");
		dCurDate.setValue(new java.util.Date());
		dCurDate.setResolution(PopupDateField.RESOLUTION_DAY);

		lblDeliveryDate = new Label("Expect Delivery Date :");
		lblDeliveryDate.setImmediate(false);
		lblDeliveryDate.setWidth("-1px");
		lblDeliveryDate.setHeight("-1px");

		dDeliveryDate = new PopupDateField();
		dDeliveryDate.setImmediate(false);
		dDeliveryDate.setWidth("-1px");
		dDeliveryDate.setHeight("-1px");
		dDeliveryDate.setDateFormat("dd-MM-yyyy");
		dDeliveryDate.setValue(new java.util.Date());
		dDeliveryDate.setResolution(PopupDateField.RESOLUTION_DAY);

		// lblType
		lblType = new Label("Production Type :");
		lblType.setImmediate(false);
		lblType.setWidth("-1px");
		lblType.setHeight("-1px");

		// cmbType
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setWidth("190px");
		cmbProductionType.setHeight("-1px");

		// lblParty
		lblParty = new Label("Party Name :");
		lblParty.setImmediate(false);
		lblParty.setWidth("-1px");
		lblParty.setHeight("-1px");

		// cmbParty
		cmbParty = new ComboBox();
		cmbParty.setImmediate(true);
		cmbParty.setNullSelectionAllowed(true);
		cmbParty.setWidth("250px");
		cmbParty.setHeight("-1px");

		/*lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");

		dFromDate = new PopupDateField();
		dFromDate.setImmediate(false);
		dFromDate.setWidth("-1px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		lblToDate = new Label("To Date :");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");

		dToDate = new PopupDateField();
		dToDate.setImmediate(false);
		dToDate.setWidth("-1px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);*/

		lblDeliveryDate = new Label("Expect Delivery Date :");
		lblDeliveryDate.setImmediate(false);
		lblDeliveryDate.setWidth("-1px");
		lblDeliveryDate.setHeight("-1px");

		dDeliveryDate = new PopupDateField();
		dDeliveryDate.setImmediate(false);
		dDeliveryDate.setWidth("-1px");
		dDeliveryDate.setHeight("-1px");
		dDeliveryDate.setDateFormat("dd-MM-yyyy");
		dDeliveryDate.setValue(new java.util.Date());
		dDeliveryDate.setResolution(PopupDateField.RESOLUTION_DAY);
		
		chkNew=new CheckBox("NEW");
		chkNew.setImmediate(true);
		chkNew.setWidth("70px");
		
		chkEdit=new CheckBox("EDIT");
		chkEdit.setImmediate(true);
		chkEdit.setWidth("70px");

		/*chkAll=new CheckBox("All");
		chkAll.setImmediate(true);
		chkAll.setWidth("70px");*/

		/*lblTolerance = new Label("Tolerance (%):");
		lblTolerance.setImmediate(false);
		lblTolerance.setWidth("-1px");
		lblTolerance.setHeight("-1px");

		txtTolerance=new AmountField();
		txtTolerance.setWidth("100px");
		txtTolerance.setHeight("-1px");
		txtTolerance.setImmediate(true);*/

		tableJobOrder=new Table();
		tableJobOrder.setWidth("260px");
		tableJobOrder.setHeight("160px");
		tableJobOrder.setColumnCollapsingAllowed(true);
		tableJobOrder.setFooterVisible(true);

		tableJobOrder.addContainerProperty("SL", Label.class, new Label());
		tableJobOrder.setColumnWidth("SL", 20);
		tableJobOrder.setColumnAlignment("SL", tableJobOrder.ALIGN_CENTER);

		tableJobOrder.addContainerProperty("check", CheckBox.class, new CheckBox());
		tableJobOrder.setColumnWidth("check", 50);

		tableJobOrder.addContainerProperty("JOB ORDER", Label.class, new Label());
		tableJobOrder.setColumnWidth("JOB ORDER", 120);

		tableReq=new Table();
		tableReq.setWidth("1030px");
		tableReq.setHeight("310px");
		tableReq.setColumnCollapsingAllowed(true);
		tableReq.setFooterVisible(true);

		tableReq.addContainerProperty("SL", Label.class, new Label());
		tableReq.setColumnWidth("SL", 20);
		tableReq.setColumnAlignment("SL", tableReq.ALIGN_CENTER);

		tableReq.addContainerProperty("Item Name", ComboBox.class, new ComboBox());
		tableReq.setColumnWidth("Item Name", 210);

		tableReq.addContainerProperty("Unit", Label.class, new Label());
		tableReq.setColumnWidth("Unit", 25);

		tableReq.addContainerProperty("Merge From", Label.class, new Label());
		tableReq.setColumnWidth("Merge From", 100);
		tableReq.setColumnCollapsed("Merge From", true);

		tableReq.addContainerProperty("Stock", TextRead.class, new TextRead(1));
		tableReq.setColumnWidth("Stock", 70);
		tableReq.setColumnCollapsed("Stock", true);

		tableReq.addContainerProperty("Required Qty", TextRead.class, new TextRead(1));
		tableReq.setColumnWidth("Required Qty", 75);
		
		tableReq.addContainerProperty("Tolerance %", AmountField.class, new AmountField());
		tableReq.setColumnWidth("Tolerance %",70);

		tableReq.addContainerProperty("Req. Qty (Tolerance)", TextRead.class, new TextRead(1));
		tableReq.setColumnWidth("Req. Qty (Tolerance)",110);
		
		tableReq.addContainerProperty("Requisite Qty", TextRead.class, new TextRead(1));
		tableReq.setColumnWidth("Requisite Qty",70);

		tableReq.addContainerProperty("Remain Qty.", TextRead.class, new TextRead(1));
		tableReq.setColumnWidth("Remain Qty.",70);
		tableReq.setColumnCollapsed("Remain Qty.", true);
		
		tableReq.addContainerProperty("Remain Qty", TextRead.class, new TextRead(1));
		tableReq.setColumnWidth("Remain Qty",70);
			
		tableReq.addContainerProperty("Flr Receive Qty", TextRead.class, new TextRead(1));
		tableReq.setColumnWidth("Flr Receive Qty", 80);

		tableReq.addContainerProperty("Req Qty", AmountField.class, new AmountField());
		tableReq.setColumnWidth("Req Qty", 70);

		tableReq.addContainerProperty("Remarks", TextField.class, new TextField());
		tableReq.setColumnWidth("Remarks", 120);

		/*tableReq.addContainerProperty("Item ID", Label.class, new Label());
		tableReq.setColumnWidth("Item ID", 50);
		tableReq.setColumnCollapsed("Item ID", true);*/

		mainLayout.addComponent(fromDate, "top: 20px; left: 10px;");
		mainLayout.addComponent(toDate, "top: 60px; left: 10px;");
		mainLayout.addComponent(typeList, "top: 90px; left: 10px;");

		mainLayout.addComponent(lblFrom, "top: 20px; left: 220px;");
		mainLayout.addComponent(cmbFrom, "top: 18px; left: 300px;");

		mainLayout.addComponent(lblTo, "top: 50px; left: 220px;");
		mainLayout.addComponent(cmbTo, "top: 48px; left: 300px;");

		mainLayout.addComponent(lblReqNo, "top: 80px; left: 220px;");
		mainLayout.addComponent(txtReqNo, "top: 78px; left: 300px;");
		
		mainLayout.addComponent(chkNew, "top: 160px; left: 300px;");
		mainLayout.addComponent(chkEdit, "top: 160px; left: 380px;");

		mainLayout.addComponent(lblReqRef, "top: 110px; left: 220px;");
		mainLayout.addComponent(txtReqRef, "top: 108px; left: 300px;");

		mainLayout.addComponent(lblCurDate, "top: 20px; left: 500px;");
		mainLayout.addComponent(dCurDate, "top: 18px; left: 620px;");

		mainLayout.addComponent(lblDeliveryDate, "top: 50px; left: 500px;");
		mainLayout.addComponent(dDeliveryDate, "top: 48px; left: 620px;");

		mainLayout.addComponent(lblType, "top: 80px; left: 500px;");
		mainLayout.addComponent(cmbProductionType, "top: 78px; left: 620px;");

		//mainLayout.addComponent(chkAll, "top: 80px; left: 820px;");

		mainLayout.addComponent(lblParty, "top: 110px; left: 610px;");
		mainLayout.addComponent(cmbParty, "top: 108px; left: 700px;");

		/*mainLayout.addComponent(lblFromDate, "top: 140px; left: 610px;");
		mainLayout.addComponent(dFromDate, "top: 138px; left: 700px;");

		mainLayout.addComponent(lblToDate, "top: 170px; left: 610px;");
		mainLayout.addComponent(dToDate, "top: 168px; left: 700px;");*/

		mainLayout.addComponent(tableJobOrder, "top: 10px; left: 970px;");

		mainLayout.addComponent(tableReq, "top: 200px; left: 220px;");

		lblLine = new Label("<b><font color='#e65100'>===========================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:530.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:570.0px; left:270.0px;");


		return mainLayout;
	}
}
