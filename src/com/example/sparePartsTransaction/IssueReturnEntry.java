package com.example.sparePartsTransaction;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vaadin.data.Property.*;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
public class IssueReturnEntry extends Window{

	SessionBean sessionBean=new SessionBean();
	AbsoluteLayout mainLayout;
	private DecimalFormat df = new DecimalFormat("#0.00");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private NativeButton btnSubmit,btnFind;
	private Label lbLine=new Label("<b><font color='#e65100'>===========================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
	private Label lbLineInfo=new Label("<b><font color='#ffffff'>=============================================INFO END=============================================</font></b>", Label.CONTENT_XHTML);
	private Label lbLineDetails=new Label("<b><font color='#ffffff'>==========================================DETAILS END=============================================</font></b>", Label.CONTENT_XHTML);
	private Label lbLineFind1=new Label("<b><font color='#ffffff'>======================FIND DATA======================</font></b>", Label.CONTENT_XHTML);
	private Label lbLineFind2=new Label("<b><font color='#ffffff'>======================FIND DATA======================</font></b>", Label.CONTENT_XHTML);

	private CommonButton cButton = new CommonButton( "New",  "Save",  "",  "",  "Refresh",  "", "", "Preview","","Exit");

	private ArrayList<Label> lblSl = new ArrayList<Label>();
	ArrayList<CheckBox>tbChkShow=new ArrayList<CheckBox>();
	private ArrayList<Label> tbLblGroupName = new ArrayList<Label>();
	private ArrayList<Label> tbLblItemName = new ArrayList<Label>();
	private ArrayList<Label> tbLblProductCode = new ArrayList<Label>();
	private ArrayList<Label> tbLblStore = new ArrayList<Label>();
	private ArrayList<Label> tbLblRack = new ArrayList<Label>();
	private ArrayList<Label> tbLblShelf = new ArrayList<Label>();
	private ArrayList<Label> tbSubItem = new ArrayList<Label>();
	private ArrayList<Label> tbUnit = new ArrayList<Label>();
	private ArrayList<Label> tbModelNo = new ArrayList<Label>();
	private ArrayList<TextRead> tbStockQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbBalanceIndentQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbBalanceStockQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbRate = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbAmount = new ArrayList<TextRead>(1);
	private ArrayList<Label> tbRemarks = new ArrayList<Label>();
	private ArrayList<TextRead> tbIndentQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbIssueQty = new ArrayList<TextRead>(1);
	private ArrayList<Label> tbLblGroupCode = new ArrayList<Label>();
	private ArrayList<Label> tbLblItemCode = new ArrayList<Label>();
	private ArrayList<Label> tbLblStoreId = new ArrayList<Label>();
	private ArrayList<Label> tbLblRackId = new ArrayList<Label>();
	private ArrayList<Label> tbLblShelfId = new ArrayList<Label>();
	private ArrayList<Label> tbLblSpecId = new ArrayList<Label>();
	private ArrayList<Label> tbLblSpecName = new ArrayList<Label>();
	Table table=new Table();

	ArrayList<CheckBox>tbChkShowFind=new ArrayList<CheckBox>();
	private ArrayList<Label> lblSlFind = new ArrayList<Label>();
	private ArrayList<Label> lblDeptFind = new ArrayList<Label>();
	private ArrayList<Label> lblSectionFind = new ArrayList<Label>();
	private ArrayList<Label> lblIssueNo = new ArrayList<Label>();

	Table tableFind=new Table();

	Label lblJObName,lblDepartment,lblSection,lblTransactionNo,lblIndentNo,lblIndentDate,lblDate,lblSrNo,lblSrDate,
	lblSubItemName,lblUnit,lblModelName,lblRate,lblStockQty,lblAmount,lblIndentQty,lblIssueQty,lblStoreName,lblRackName,
	lblShelfName,lblRemarks,lblGroupName,lblItemName,lblProductCod,lblFromDateFind,lblToDateFind;

	ComboBox cmbGroupName,cmbItemName,cmbProductCode,cmbJobName,cmbDepartment,cmbSection,cmbStoreName,cmbRackName,cmbShelfName;
	TextRead txtTransactionNo,txtSubItemName,txtUnit,txtModelName,txtRate,txtStockQty,txtAmount;
	AmountField txtIndentQty,txtIssueQty;
	TextField txtIndentNo,txtSRNo,txtRemarks;
	PopupDateField dDate,dIndentDate,dSrDate,dFromDateFind,dToDateFind;

	HashMap<String,String> hMapSubItemName=new HashMap<String,String>();
	HashMap<String,String> hMapUnit=new HashMap<String,String>();
	HashMap<String,String> hMapSpecCode=new HashMap<String,String>();
	HashMap<String,String> hMapSpecName=new HashMap<String,String>();
	HashMap<String,String> hMapModel=new HashMap<String,String>();
	private boolean isFind=false;
	private boolean isUpdate=false;


	public IssueReturnEntry(SessionBean sessionBean){
		this.sessionBean=sessionBean;
		this.setCaption("ISSUE RETURN ENTRY::"+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		setEventAction();
		focusEnter();
	}
	private void focusEnter(){
		
		ArrayList<Component> allComp = new ArrayList<Component>();
		
		//Info
		allComp.add(cmbJobName);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(txtIndentNo);
		allComp.add(dIndentDate);
		allComp.add(txtSRNo);
		allComp.add(dSrDate);
		
		//Details
		allComp.add(cmbGroupName);
		allComp.add(cmbItemName);
		allComp.add(cmbProductCode);
		allComp.add(txtIndentQty);
		allComp.add(txtIssueQty);
		allComp.add(cmbStoreName);
		allComp.add(cmbRackName);
		allComp.add(cmbShelfName);
		allComp.add(txtRemarks);
		allComp.add(btnSubmit);
		
		new FocusMoveByEnter(this,allComp);
	}
	private void refreshButtonEvent() {
		componentIni(true);
		btnIni(true);
		infoClear();
		detailsClear();
		findClear();
		tableClear();
		dFromDateFind.setValue(new java.util.Date());
		dToDateFind.setValue(new java.util.Date());
	}
	
	private void setEventAction(){
		cButton.btnNew.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				newButtonEvent();
			}
		});
		cButton.btnSave.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(saveCheckValidation()){
					saveButtonEvent();
				}
			}
		});
		cButton.btnRefresh.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
				isFind=false;
				isUpdate=false;
			}

		});
		cButton.btnPreview.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

			}
		});
		cButton.btnExit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbDepartment.getValue()!=null){
					cmbSectionDataLoad();
				}
				else{
					cmbSection.removeAllItems();
				}
			}
		});
		cmbGroupName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbGroupName.getValue()!=null){
					cmbItemDataLoad();
					//showNotification(cmbGroupName.getValue().toString(),Notification.TYPE_WARNING_MESSAGE);
				}
				else{
					cmbItemName.removeAllItems();
				}
			}
		});
		cmbItemName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbItemName.getValue()!=null){
					cmbProductCodeLoad();
				}
				else{
					cmbProductCode.removeAllItems();
				}
			}
		});
		cmbProductCode.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				txtIndentQty.setValue("");
				txtIssueQty.setValue("");
				//txtRate.setValue("");
				txtAmount.setValue("");
				if(cmbProductCode.getValue()!=null){
					if(!isFind){
						txtSubItemName.setValue(hMapSubItemName.get(cmbProductCode.getValue()));
						txtUnit.setValue(hMapUnit.get(cmbProductCode.getValue()));
						txtModelName.setValue(hMapModel.get(cmbProductCode.getValue()));
					}
				}
				else{
					txtSubItemName.setValue("");
					txtUnit.setValue("");
					txtModelName.setValue("");
				}
			}
		});
		cmbStoreName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbStoreName.getValue()!=null){
					cmbRackDataLoad();
				}
				else{
					cmbRackName.removeAllItems();
				}
			}
		});
		cmbRackName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbRackName.getValue()!=null){
					cmbShelfDataLoad();
				}
				else{
					cmbShelfName.removeAllItems();
				}
			}
		});
		txtIssueQty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				amountCalc();
			}
		});
		btnSubmit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				subMitButtonEvent();
			}
		});
		btnFind.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
			
				findTableDataLoad(0);
			}
		});

	}
	private void findTableDataLoad(int ar){
		findClear();
		String sql="select deptName,sectionName,transactionNo from tbIssueInfo a  where  " +
				"convert(Date,a.date,105) between '"+datef.format(dFromDateFind.getValue())+"' and '"+datef.format(dToDateFind.getValue())+"' order by transactionNo";
		Iterator<?>iter=dbService(sql);
		System.out.println(sql);
		
		if(!iter.hasNext()&&ar==0){
			showNotification("No data Found For Find Table",Notification.TYPE_WARNING_MESSAGE);
		}
		else{
			int a=0;
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				lblDeptFind.get(a).setValue(element[0]);
				lblSectionFind.get(a).setValue(element[1]);
				lblIssueNo.get(a).setValue(element[2]);
				a++;
			}
		}
	}
	private void findClear(){
		//dFromDateFind.setValue(new java.util.Date());
		//dToDateFind.setValue(new java.util.Date());
		for(int a=0;a<lblSlFind.size();a++){
			tbChkShowFind.get(a).setValue(false);
			lblSectionFind.get(a).setValue("");
			lblDeptFind.get(a).setValue("");
			lblIssueNo.get(a).setValue("");
		}
	}
	private void infoClear(){
		txtTransactionNo.setValue("");
		cmbJobName.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		txtIndentNo.setValue("");
		dIndentDate.setValue(new java.util.Date());
		dDate.setValue(new java.util.Date());
		dSrDate.setValue(new java.util.Date());
		txtSRNo.setValue("");
	}
	private void tableClear(){
		for(int a=0;a<tbLblGroupCode.size();a++){
			tableRowClear(a);
		}
	}
	private void saveButtonEvent(){
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update product information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(deleteData()){
							insertData();
							infoClear();
							detailsClear();
							tableClear();
							isFind=false;
							isUpdate=false;
							autoTransactionNo();
							cmbJobName.focus();
							findTableDataLoad(0);
						}
					}
				}
			});		
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						infoClear();
						detailsClear();
						tableClear();
						autoTransactionNo();
						cmbJobName.focus();
						isFind=false;
						isUpdate=false;
						findTableDataLoad(0);
					}
				}
			});		
		}
	}
	private boolean deleteData(){
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			String sql="delete from tbIssueInfo where transactionNo='"+txtTransactionNo.getValue()+"'";
			String sql1="delete from tbIssueDetails where transactionNo='"+txtTransactionNo.getValue()+"'";
			session.createSQLQuery(sql).executeUpdate();
			session.createSQLQuery(sql1).executeUpdate();
			if(session!=null||tx!=null){
				tx.commit();
				session.close();
			}
			return true;
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

		return false;
	}
	private void insertData(){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			String sql="insert into tbIssueInfo(transactionNo,date,jobId,jobName,deptId,deptName,sectionId,sectionName, "+
					" indentNo,indentDate,srNo,srDate,userIp,userName,entryTime)values('"+txtTransactionNo.getValue()+"'," +
					"'"+datef.format(dDate.getValue())+"','"+cmbJobName.getValue()+"','"+cmbJobName.getItemCaption(cmbJobName.getValue())+"'," +
					"'"+cmbDepartment.getValue()+"','"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"'," +
					"'"+cmbSection.getValue()+"','"+cmbSection.getItemCaption(cmbSection.getValue())+"','"+txtIndentNo.getValue()+"'," +
					"'"+datef.format(dIndentDate.getValue())+"','"+txtSRNo.getValue()+"','"+datef.format(dSrDate.getValue())+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";

			for(int a=0;a<tbLblGroupCode.size();a++){
				if(!tbLblGroupCode.get(a).getValue().toString().isEmpty()){
					String sqDetails="insert into tbIssueDetails(transactionNo,groupId,groupName,ItemCode,ItemName,ProductCode,unit,subItemName,specId,specName, "+
							" modelName,IndentQty,IssueQty,rate,amount,storeId,storeName,rackId,rackName,shelfId,shelfName,remarks)values" +
							"('"+txtTransactionNo.getValue()+"','"+tbLblGroupCode.get(a).getValue()+"','"+tbLblGroupName.get(a).getValue()+"'," +
							"'"+tbLblItemCode.get(a).getValue()+"','"+tbLblItemName.get(a).getValue()+"','"+tbLblProductCode.get(a).getValue()+"'," +
							"'"+tbUnit.get(a).getValue()+"','"+tbSubItem.get(a).getValue()+"','"+tbLblSpecId.get(a).getValue()+"','"+tbLblSpecName.get(a).getValue()+"'," +
							"'"+tbModelNo.get(a).getValue()+"','"+tbIndentQty.get(a).getValue()+"','"+tbIssueQty.get(a).getValue()+"','"+tbRate.get(a).getValue()+"'," +
							"'"+tbAmount.get(a).getValue()+"','"+tbLblStoreId.get(a).getValue()+"','"+tbLblStore.get(a).getValue()+"','"+tbLblRackId.get(a).getValue()+"'," +
							"'"+tbLblRack.get(a).getValue()+"','"+tbLblShelfId.get(a).getValue()+"','"+tbLblShelf.get(a).getValue()+"','"+tbRemarks.get(a).getValue()+"')";

					if(a==0){
						session.createSQLQuery(sql).executeUpdate();
					}
					session.createSQLQuery(sqDetails).executeUpdate();
				}
			}
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				session.close();
				showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			}
		}
	}
	private boolean saveCheckValidation(){
		if(!txtTransactionNo.getValue().toString().isEmpty()){
			if(cmbJobName.getValue()!=null){
				if(cmbDepartment.getValue()!=null){
					if(cmbSection.getValue()!=null){
						//if(checkValidation()){
							if(!tbLblGroupCode.get(0).getValue().toString().isEmpty()){
								return true;
							}
							else{
								showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
							}
						//}
					}
					else{
						showNotification("Please Provide Section Name",Notification.TYPE_WARNING_MESSAGE);
						cmbSection.focus();
					}
				}
				else{
					showNotification("Please Provide Department Name",Notification.TYPE_WARNING_MESSAGE);
					cmbDepartment.focus();
				}
			}
			else{
				showNotification("Please Provide Job Name",Notification.TYPE_WARNING_MESSAGE);
				cmbJobName.focus();
			}
		}
		else{
			showNotification("Please Provide Transaction No.",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private void subMitButtonEvent(){
		if(checkValidation()){
			if(checkDoubleEntry(cmbProductCode.getValue().toString())){
				tableRowAddSubmit();
				detailsClear();
				cmbGroupName.focus();
			}
			else{
				showNotification("Sorr!!","Double Entry",Notification.TYPE_WARNING_MESSAGE);
				cmbGroupName.focus();
			}
		}
	}
	private void detailsClear(){
		cmbGroupName.setValue(null);
		cmbItemName.setValue(null);
		cmbProductCode.setValue(null);
		txtSubItemName.setValue("");
		txtUnit.setValue("");
		txtModelName.setValue("");
		//txtStockQty.setValue("");
		txtIssueQty.setValue("");
		txtIndentQty.setValue("");
		//txtRate.setValue("");
		txtAmount.setValue("");
		cmbStoreName.setValue(null);
		cmbRackName.setValue(null);
		cmbShelfName.setValue(null);
		txtRemarks.setValue("");
	}
	private boolean checkValidation(){
		if(cmbGroupName.getValue()!=null){
			if(cmbItemName.getValue()!=null){
				if(cmbProductCode.getValue()!=null){
					if(!txtIssueQty.getValue().toString().isEmpty()){
						if(!txtRate.getValue().toString().isEmpty()){
							if(!txtAmount.getValue().toString().isEmpty()){
								if(cmbStoreName.getValue()!=null){
									return true;
								}
								else{
									showNotification("Please Select Store Name",Notification.TYPE_WARNING_MESSAGE);
									cmbStoreName.focus();
								}
							}
							else{
								showNotification("Please Select Amount",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else{
							showNotification("Please Select Rate",Notification.TYPE_WARNING_MESSAGE);
							
						}
					}
					else{
						showNotification("Please Select Qty",Notification.TYPE_WARNING_MESSAGE);
						txtIssueQty.focus();
					}
				}
				else{
					showNotification("Please Select Product Code",Notification.TYPE_WARNING_MESSAGE);
					cmbProductCode.focus();
				}
			}
			else{
				showNotification("Please Select Item Name",Notification.TYPE_WARNING_MESSAGE);
				cmbItemName.focus();
			}
		}
		else{
			showNotification("Please Select Group Name",Notification.TYPE_WARNING_MESSAGE);
			cmbGroupName.focus();
		}
		return false;
	}
	private boolean checkDoubleEntry(String productCode){
		for(int a=0;a<tbLblGroupCode.size();a++){
			if(tbLblProductCode.get(a).getValue().toString().equals(productCode)){
				return false;
			}
		}
		return true;
	}
	private void tableRowAddSubmit(){

		int a=findBlankRow();
		tbChkShow.get(a).setValue(false);
		tbLblGroupCode.get(a).setValue(cmbGroupName.getValue());
		tbLblGroupName.get(a).setValue(cmbGroupName.getItemCaption(cmbGroupName.getValue()));
		tbLblItemCode.get(a).setValue(cmbItemName.getValue());
		tbLblItemName.get(a).setValue(cmbItemName.getItemCaption(cmbItemName.getValue()));
		tbLblStoreId.get(a).setValue(cmbStoreName.getValue());
		tbLblStore.get(a).setValue(cmbStoreName.getItemCaption(cmbStoreName.getValue()));
		tbLblRackId.get(a).setValue(cmbRackName.getValue());
		tbLblRack.get(a).setValue(cmbRackName.getItemCaption(cmbRackName.getValue()));
		tbLblShelfId.get(a).setValue(cmbShelfName.getValue());
		tbLblShelf.get(a).setValue(cmbShelfName.getItemCaption(cmbShelfName.getValue()));
		tbRemarks.get(a).setValue(txtRemarks.getValue());
		tbAmount.get(a).setValue(df.format(Double.parseDouble(txtAmount.getValue().toString())));
		tbLblProductCode.get(a).setValue(cmbProductCode.getValue());
		tbSubItem.get(a).setValue(txtSubItemName.getValue());
		tbUnit.get(a).setValue(txtUnit.getValue());
		tbModelNo.get(a).setValue(txtModelName.getValue());
		tbStockQty.get(a).setValue(df.format(Double.parseDouble(txtStockQty.getValue().toString())));
		tbIndentQty.get(a).setValue(df.format(Double.parseDouble(txtIndentQty.getValue().toString())));
		tbIssueQty.get(a).setValue(df.format(Double.parseDouble(txtIssueQty.getValue().toString())));
		tbRate.get(a).setValue(df.format(Double.parseDouble(txtRate.getValue().toString())));
		tbLblSpecId.get(a).setValue(hMapSpecCode.get(cmbProductCode.getValue()));
		tbLblSpecName.get(a).setValue(hMapSpecName.get(cmbProductCode.getValue()));

		if(a==tbLblGroupCode.size()-1){
			tableRowAdd(a+1);
		}
	}
	private void tableRowClear(int ar){
		tbLblGroupCode.get(ar).setValue("");
		tbLblGroupName.get(ar).setValue("");
		tbLblItemCode.get(ar).setValue("");
		tbLblItemName.get(ar).setValue("");
		tbLblStoreId.get(ar).setValue("");
		tbLblStore.get(ar).setValue("");
		tbLblRackId.get(ar).setValue("");
		tbLblRack.get(ar).setValue("");
		tbLblShelfId.get(ar).setValue("");
		tbLblShelf.get(ar).setValue("");
		tbRemarks.get(ar).setValue("");
		tbAmount.get(ar).setValue("");
		tbLblProductCode.get(ar).setValue("");
		tbSubItem.get(ar).setValue("");
		tbUnit.get(ar).setValue("");
		tbModelNo.get(ar).setValue("");
		tbStockQty.get(ar).setValue("");
		tbIndentQty.get(ar).setValue("");
		tbIssueQty.get(ar).setValue("");
		tbRate.get(ar).setValue("");
		tbLblSpecId.get(ar).setValue("");
		tbLblSpecName.get(ar).setValue("");
	}
	private void detailsDataLoad(int ar){
		cmbGroupName.setValue(tbLblGroupCode.get(ar).getValue());
		cmbItemName.setValue(tbLblItemCode.get(ar).getValue());
		cmbProductCode.setValue(tbLblProductCode.get(ar).getValue());
		txtIndentQty.setValue(tbIndentQty.get(ar).getValue());
		txtIssueQty.setValue(tbIssueQty.get(ar).getValue());
		cmbStoreName.setValue(tbLblStoreId.get(ar).getValue());
		cmbRackName.setValue(tbLblRackId.get(ar).getValue());
		cmbShelfName.setValue(tbLblShelfId.get(ar).getValue());
	}
	private int findBlankRow(){
		for(int a=0;a<tbChkShow.size();a++){
			if(tbLblProductCode.get(a).getValue().toString().isEmpty()){
				return a;
			}
		}
		return 0;
	}
	private void amountCalc(){
		double qty=Double.parseDouble( txtIssueQty.getValue().toString().isEmpty()?"0.0":txtIssueQty.getValue().toString());
		double rate=Double.parseDouble( txtRate.getValue().toString().isEmpty()?"0.0":txtRate.getValue().toString());
		double amount=qty*rate;
		txtAmount.setValue(df.format(amount));
	}
	private void cmbRackDataLoad(){
		cmbRackName.removeAllItems();
		Iterator<?>iter=dbService("select vSubStoreId,vSubStoreName from tbRackInfo where vStoreId='"+cmbStoreName.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbRackName.addItem(element[0]);
			cmbRackName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbShelfDataLoad(){
		cmbShelfName.removeAllItems();
		Iterator<?>iter=dbService("select vSubSubStoreId,vSubSubStoreName from tbShelfInfo where vSubStoreId='"+cmbRackName.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbShelfName.addItem(element[0]);
			cmbShelfName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbStoreDataLoad(){
		cmbStoreName.removeAllItems();
		Iterator<?>iter=dbService("select vDepoId,vDepoName from tbDepoInformation");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbStoreName.addItem(element[0]);
			cmbStoreName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbProductCodeLoad(){
		cmbProductCode.removeAllItems();
		hMapSubItemName.clear();
		hMapUnit.clear();
		hMapSubItemName.clear();
		hMapSpecCode.clear();
		hMapModel.clear();
		String sql="select a.Specificaionid,case when a.flag=0 then '' else a.subitemName end,a.specificationName,a.unit,(b.vGroupCode+'.'+c.itemcode+'.'+ a.specificationcode)productCode,a.modelName "+
				" from tbspecification a inner join tbGroupInformation b on a.groupId=b.Group_Id "+
				" inner join tbItemInfosub c  on a.subgroupId=c.SubGroup_Id "+
				" where a.groupId like '"+cmbGroupName.getValue()+"' and a.subgroupId like '"+cmbItemName.getValue()+"' " +
				"and a.Specificaionid not in(select distinct vSpecCode from tbItemOpening) ";
		Iterator<?>iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductCode.addItem(element[4]);
			hMapSubItemName.put(element[4].toString(), element[1].toString());
			hMapUnit.put(element[4].toString(), element[3].toString());
			hMapSpecName.put(element[4].toString(), element[2].toString());
			hMapSpecCode.put(element[4].toString(), element[0].toString());
			hMapModel.put(element[4].toString(),element[5].toString());
		}
	}
	private void cmbItemDataLoad(){
		cmbItemName.removeAllItems();
		Iterator<?>iter=dbService("select SubGroup_Id,vItemName from tbItemInfosub where Group_Id='"+cmbGroupName.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbItemName.addItem(element[0]);
			cmbItemName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbSectionDataLoad(){
		cmbSection.removeAllItems();
		Iterator<?>iter=dbService("select vSectionID,SectionName from tbSectionInfo where vDepartmentID like '"+cmbDepartment.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbSection.addItem(element[0]);
			cmbSection.setItemCaption(element[0], element[1].toString());
		}
	}
	private void autoTransactionNo(){
		Iterator<?>iter=dbService("select isnull(MAX(TransactionNo),0)+1 from tbIssueInfo");
		if(iter.hasNext()){
			txtTransactionNo.setValue(iter.next());
		}
	}
	private void cmbGroupDataLoad(){

		Iterator<?>iter=dbService("select Group_Id,vGroupName from tbGroupInformation");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbGroupName.addItem(element[0]);
			cmbGroupName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void newButtonEvent() 
	{
		infoClear();
		detailsClear();
		findClear();
		componentIni(false);
		btnIni(false);
		autoTransactionNo();
		cmbJobName.focus();
		cmbJobNoDataLoad();
		cmbDepartmentDataLoad();
		cmbGroupDataLoad();
		cmbStoreDataLoad();
		findTableDataLoad(1);
	}
	private void componentIni(boolean t){
		cmbJobName.setEnabled(!t);
		cmbDepartment.setEnabled(!t);
		cmbSection.setEnabled(!t);
		txtIndentNo.setEnabled(!t);
		dIndentDate.setEnabled(!t);
		txtTransactionNo.setEnabled(!t);
		dDate.setEnabled(!t);
		txtSRNo.setEnabled(!t);
		dSrDate.setEnabled(!t);
		cmbGroupName.setEnabled(!t);
		cmbItemName.setEnabled(!t);
		cmbProductCode.setEnabled(!t);
		txtSubItemName.setEnabled(!t);
		txtUnit.setEnabled(!t);
		txtModelName.setEnabled(!t);
		txtIndentQty.setEnabled(!t);
		txtStockQty.setEnabled(!t);
		txtIssueQty.setEnabled(!t);
		txtRate.setEnabled(!t);
		txtAmount.setEnabled(!t);
		cmbStoreName.setEnabled(!t);
		cmbShelfName.setEnabled(!t);
		cmbRackName.setEnabled(!t);
		txtRemarks.setEnabled(!t);
		dFromDateFind.setEnabled(!t);
		dToDateFind.setEnabled(!t);
		btnFind.setEnabled(!t);
		tableFind.setEnabled(!t);
		table.setEnabled(!t);
		btnSubmit.setEnabled(!t);
	}
	private void cmbDepartmentDataLoad(){

		Iterator<?>iter=dbService("select vDepartmentId,vDepartmentName from tbDepartmentInfo");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbDepartment.addItem(element[0]);
			cmbDepartment.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbJobNoDataLoad(){

		Iterator<?>iter=dbService("select jobId,jobName from tbJobInfo");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbJobName.addItem(element[0]);
			cmbJobName.setItemCaption(element[0], element[1].toString());
		}
	}
	private Iterator<?> dbService(String sql){
		Transaction tx=null;
		Session session=null;
		Iterator<?> iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			iter=session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){

				session.close();
			}
		}
		return iter;
	}
	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnPreview.setEnabled(t);
	}
	private AbsoluteLayout buildMainLayout(){
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("1230px");
		mainLayout.setHeight("600px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("1250px");
		setHeight("650px");

		lblJObName=new Label();
		lblJObName.setWidth("-1px");
		lblJObName.setValue("Job Name :");
		lblJObName.setHeight("-1px");
		lblJObName.setImmediate(false);

		cmbJobName=new ComboBox();
		cmbJobName.setWidth("200px");
		//cmbJobName.setNewItemsAllowed(true);
		cmbJobName.setNullSelectionAllowed(true);
		cmbJobName.setImmediate(true);
		cmbJobName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblDepartment=new Label();
		lblDepartment.setWidth("-1px");
		lblDepartment.setValue("Department :");
		lblDepartment.setHeight("-1px");
		lblDepartment.setImmediate(false);

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("200px");
		//cmbDepartment.setNewItemsAllowed(true);
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblSection=new Label();
		lblSection.setWidth("-1px");
		lblSection.setValue("Section :");
		lblSection.setHeight("-1px");
		lblSection.setImmediate(false);

		cmbSection=new ComboBox();
		cmbSection.setWidth("200px");
		//cmbSection.setNewItemsAllowed(true);
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblTransactionNo=new Label();
		lblTransactionNo.setWidth("-1px");
		lblTransactionNo.setValue("Transaction No :");
		lblTransactionNo.setHeight("-1px");
		lblTransactionNo.setImmediate(false);

		txtTransactionNo=new TextRead(1);
		txtTransactionNo.setWidth("100px");
		txtTransactionNo.setHeight("24px");
		txtTransactionNo.setImmediate(true);

		lblIndentNo=new Label();
		lblIndentNo.setWidth("-1px");
		lblIndentNo.setValue("Indent No :");
		lblIndentNo.setHeight("-1px");
		lblIndentNo.setImmediate(false);

		txtIndentNo=new TextField();
		txtIndentNo.setWidth("100px");
		txtIndentNo.setHeight("24px");
		txtIndentNo.setImmediate(true);

		lblIndentDate=new Label();
		lblIndentDate=new Label();
		lblIndentDate.setValue("Indent Date :");
		lblIndentDate.setWidth("-1px");
		lblIndentDate.setHeight("-1px");

		dIndentDate=new PopupDateField();
		dIndentDate.setWidth("108px");
		dIndentDate.setHeight("24px");
		dIndentDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dIndentDate.setDateFormat("dd-MM-yyyy");
		dIndentDate.setValue(new java.util.Date());
		dIndentDate.setImmediate(true);

		lblDate=new Label();
		lblDate=new Label();
		lblDate.setValue("Date :");
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");

		dDate=new PopupDateField();
		dDate.setWidth("108px");
		dDate.setHeight("24px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setImmediate(true);

		lblSrNo=new Label();
		lblSrNo.setWidth("-1px");
		lblSrNo.setValue("SR No:");
		lblSrNo.setHeight("-1px");
		lblSrNo.setImmediate(false);

		txtSRNo=new TextField();
		txtSRNo.setWidth("100px");
		txtSRNo.setHeight("24px");
		txtSRNo.setImmediate(true);


		lblSrDate=new Label();
		lblSrDate.setValue("SR Date :");
		lblSrDate.setWidth("-1px");
		lblSrDate.setHeight("-1px");

		dSrDate=new PopupDateField();
		dSrDate.setWidth("108px");
		dSrDate.setHeight("24px");
		dSrDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dSrDate.setDateFormat("dd-MM-yyyy");
		dSrDate.setValue(new java.util.Date());
		dSrDate.setImmediate(true);


		lblGroupName=new Label();
		lblGroupName.setWidth("-1px");
		lblGroupName.setValue("Group Name :");
		lblGroupName.setHeight("-1px");
		lblGroupName.setImmediate(false);

		cmbGroupName=new ComboBox();
		cmbGroupName.setWidth("220px");
		//cmbGroupName.setNewItemsAllowed(true);
		cmbGroupName.setNullSelectionAllowed(true);
		cmbGroupName.setImmediate(true);
		cmbGroupName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblItemName=new Label();
		lblItemName.setWidth("-1px");
		lblItemName.setValue("Item Name :");
		lblItemName.setHeight("-1px");
		lblItemName.setImmediate(false);

		cmbItemName=new ComboBox();
		cmbItemName.setWidth("220px");
		//cmbItemName.setNewItemsAllowed(true);
		cmbItemName.setNullSelectionAllowed(true);
		cmbItemName.setImmediate(true);
		cmbItemName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblProductCod=new Label();
		lblProductCod.setWidth("-1px");
		lblProductCod.setValue("Product Code:");
		lblProductCod.setHeight("-1px");
		lblProductCod.setImmediate(false);

		cmbProductCode=new ComboBox();
		cmbProductCode.setWidth("220px");
		//cmbProductCode.setNewItemsAllowed(true);
		cmbProductCode.setNullSelectionAllowed(true);
		cmbProductCode.setImmediate(true);
		cmbProductCode.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


		lblSubItemName=new Label();
		lblSubItemName.setWidth("-1px");
		lblSubItemName.setValue("Sub Item:");
		lblSubItemName.setHeight("-1px");
		lblSubItemName.setImmediate(false);

		txtSubItemName=new TextRead(1);
		txtSubItemName.setWidth("100px");
		txtSubItemName.setHeight("24px");
		txtSubItemName.setImmediate(true);

		lblUnit=new Label();
		lblUnit.setWidth("-1px");
		lblUnit.setValue("Unit:");
		lblUnit.setHeight("-1px");
		lblUnit.setImmediate(false);

		txtUnit=new TextRead(1);
		txtUnit.setWidth("100px");
		txtUnit.setHeight("24px");
		txtUnit.setImmediate(true);

		lblModelName=new Label();
		lblModelName.setWidth("-1px");
		lblModelName.setValue("Model No:");
		lblModelName.setHeight("-1px");
		lblModelName.setImmediate(false);

		txtModelName=new TextRead(1);
		txtModelName.setWidth("100px");
		txtModelName.setHeight("24px");
		txtModelName.setImmediate(true);

		lblStockQty=new Label();
		lblStockQty.setWidth("-1px");
		lblStockQty.setValue("Stock Qty:");
		lblStockQty.setHeight("-1px");
		lblStockQty.setImmediate(false);

		txtStockQty=new TextRead(1);
		txtStockQty.setWidth("100px");
		txtStockQty.setHeight("24px");
		txtStockQty.setImmediate(true);
		txtStockQty.setValue("100");

		lblRate=new Label();
		lblRate.setWidth("-1px");
		lblRate.setValue("Rate:");
		lblRate.setHeight("-1px");
		lblRate.setImmediate(false);

		txtRate=new TextRead(1);
		txtRate.setWidth("100px");
		txtRate.setHeight("24px");
		txtRate.setImmediate(true);
		txtRate.setValue("25");

		lblAmount=new Label();
		lblAmount.setWidth("-1px");
		lblAmount.setValue("Amount:");
		lblAmount.setHeight("-1px");
		lblAmount.setImmediate(false);

		txtAmount=new TextRead(1);
		txtAmount.setWidth("100px");
		txtAmount.setHeight("24px");
		txtAmount.setImmediate(true);

		lblIndentQty=new Label();
		lblIndentQty.setWidth("-1px");
		lblIndentQty.setValue("Indent Qty:");
		lblIndentQty.setHeight("-1px");
		lblIndentQty.setImmediate(false);

		txtIndentQty=new AmountField();
		txtIndentQty.setWidth("100px");
		txtIndentQty.setHeight("24px");
		txtIndentQty.setImmediate(true);

		lblIssueQty=new Label();
		lblIssueQty.setWidth("-1px");
		lblIssueQty.setValue("Issue Qty:");
		lblIssueQty.setHeight("-1px");
		lblIssueQty.setImmediate(false);

		txtIssueQty=new AmountField();
		txtIssueQty.setWidth("100px");
		txtIssueQty.setHeight("24px");
		txtIssueQty.setImmediate(true);

		lblStoreName=new Label();
		lblStoreName.setWidth("-1px");
		lblStoreName.setValue("Store Name :");
		lblStoreName.setHeight("-1px");
		lblStoreName.setImmediate(false);

		cmbStoreName=new ComboBox();
		cmbStoreName.setWidth("100px");
		//cmbStoreName.setNewItemsAllowed(true);
		cmbStoreName.setNullSelectionAllowed(true);
		cmbStoreName.setImmediate(true);
		cmbStoreName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblRackName=new Label();
		lblRackName.setWidth("-1px");
		lblRackName.setValue("Rack Name :");
		lblRackName.setHeight("-1px");
		lblRackName.setImmediate(false);

		cmbRackName=new ComboBox();
		cmbRackName.setWidth("100px");
		//cmbRackName.setNewItemsAllowed(true);
		cmbRackName.setNullSelectionAllowed(true);
		cmbRackName.setImmediate(true);
		cmbRackName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblShelfName=new Label();
		lblShelfName.setWidth("-1px");
		lblShelfName.setValue("Shelf :");
		lblShelfName.setHeight("-1px");
		lblShelfName.setImmediate(false);

		cmbShelfName=new ComboBox();
		cmbShelfName.setWidth("100px");
		//cmbShelfName.setNewItemsAllowed(true);
		cmbShelfName.setNullSelectionAllowed(true);
		cmbShelfName.setImmediate(true);
		cmbShelfName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblRemarks=new Label();
		lblRemarks.setWidth("-1px");
		lblRemarks.setValue("Remarks:");
		lblRemarks.setHeight("-1px");
		lblRemarks.setImmediate(false);

		txtRemarks=new TextField();
		txtRemarks.setWidth("170px");
		txtRemarks.setHeight("24px");
		txtRemarks.setImmediate(true);



		table.setWidth("100%");
		table.setHeight("300px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",15);



		table.addContainerProperty("Group Name", Label.class , new Label());
		table.setColumnWidth("Group Name",210);

		table.addContainerProperty("Item Name", Label.class , new Label());
		table.setColumnWidth("Item Name",120);

		table.addContainerProperty("Product Code", Label.class , new Label());
		table.setColumnWidth("Product Code",80);

		table.addContainerProperty("SubItem Name", Label.class , new Label());
		table.setColumnWidth("SubItem Name",70);
		table.setColumnCollapsed("SubItem Name", true);

		table.addContainerProperty("Unit", Label.class , new Label());
		table.setColumnWidth("Unit",40);

		table.addContainerProperty("Model Name", Label.class , new Label());
		table.setColumnWidth("Model Name",70);
		table.setColumnCollapsed("Model Name", true);

		table.addContainerProperty("Stock Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Stock Qty",70);

		table.addContainerProperty("Indent Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Indent Qty",70);

		table.addContainerProperty("Issue Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Issue Qty",70);

		table.addContainerProperty("Balance Indent Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Balance Indent Qty",70);
		table.setColumnCollapsed("Balance Indent Qty", true);

		table.addContainerProperty("Balance Stock Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Balance Stock Qty",70);
		table.setColumnCollapsed("Balance Stock Qty", true);

		table.addContainerProperty("Rate", TextRead.class , new TextRead(1));
		table.setColumnWidth("Rate",50);

		table.addContainerProperty("Amount", TextRead.class , new TextRead(1));
		table.setColumnWidth("Amount",70);

		table.addContainerProperty("Store Name", Label.class , new Label());
		table.setColumnWidth("Store Name",120);

		table.addContainerProperty("Rack Name", Label.class , new Label());
		table.setColumnWidth("Rack Name",90);

		table.addContainerProperty("Shelf Name", Label.class , new Label());
		table.setColumnWidth("Shelf Name",90);
		table.setColumnCollapsed("Shelf Name", true);

		table.addContainerProperty("Remarks", Label.class , new Label());
		table.setColumnWidth("Remarks",150);
		table.setColumnCollapsed("Remarks", true);

		table.addContainerProperty("Edit", CheckBox.class, new CheckBox());
		table.setColumnWidth("Edit", 30);

		table.addContainerProperty("Group Code", Label.class , new Label());
		table.setColumnWidth("Group Code",120);
		table.setColumnCollapsed("Group Code", true);

		table.addContainerProperty("Item Code", Label.class , new Label());
		table.setColumnWidth("Item Code",120);
		table.setColumnCollapsed("Item Code", true);

		table.addContainerProperty("Store Id", Label.class , new Label());
		table.setColumnWidth("Store Id",120);
		table.setColumnCollapsed("Store Id", true);

		table.addContainerProperty("Rack Id", Label.class , new Label());
		table.setColumnWidth("Rack Id",120);
		table.setColumnCollapsed("Rack Id", true);

		table.addContainerProperty("Shelf Id", Label.class , new Label());
		table.setColumnWidth("Shelf Id",120);
		table.setColumnCollapsed("Shelf Id", true);

		table.addContainerProperty("Spec Id", Label.class , new Label());
		table.setColumnWidth("Spec Id",120);
		table.setColumnCollapsed("Spec Id", true);

		table.addContainerProperty("Spec Name", Label.class , new Label());
		table.setColumnWidth("Spec Name",120);
		table.setColumnCollapsed("Spec Name", true);

		tableInitialise();

		lblFromDateFind=new Label();
		lblFromDateFind.setValue("From Date :");
		lblFromDateFind.setWidth("-1px");
		lblFromDateFind.setHeight("-1px");

		dFromDateFind=new PopupDateField();
		dFromDateFind.setWidth("108px");
		dFromDateFind.setHeight("24px");
		dFromDateFind.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDateFind.setDateFormat("dd-MM-yyyy");
		dFromDateFind.setValue(new java.util.Date());
		dFromDateFind.setImmediate(true);

		lblToDateFind=new Label();
		lblToDateFind.setValue("To Date :");
		lblToDateFind.setWidth("-1px");
		lblToDateFind.setHeight("-1px");

		dToDateFind=new PopupDateField();
		dToDateFind.setWidth("108px");
		dToDateFind.setHeight("24px");
		dToDateFind.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDateFind.setDateFormat("dd-MM-yyyy");
		dToDateFind.setValue(new java.util.Date());
		dToDateFind.setImmediate(true);

		btnFind= new NativeButton("Find");
		btnFind.setImmediate(false);
		btnFind.setIcon(new ThemeResource("../icons/find.png"));
		btnFind.setImmediate(true);
		btnFind.setWidth("60px");
		btnFind.setHeight("24px");

		btnSubmit= new NativeButton("SUBMIT");
		btnSubmit.setImmediate(false);
		btnSubmit.setIcon(new ThemeResource("../icons/action_save.gif"));
		btnSubmit.setImmediate(true);
		btnSubmit.setWidth("75px");
		btnSubmit.setHeight("75px");

		tableFind.setWidth("420px");
		tableFind.setHeight("185px");
		tableFind.setFooterVisible(true);
		tableFind.setColumnCollapsingAllowed(true);

		tableFind.addContainerProperty("SL", Label.class , new Label());
		tableFind.setColumnWidth("SL",15);
		
		tableFind.addContainerProperty("CHECK", CheckBox.class, new CheckBox());
		tableFind.setColumnWidth("CHECK", 30);
		
		tableFind.addContainerProperty("Department", Label.class , new Label());
		tableFind.setColumnWidth("Department",125);

		tableFind.addContainerProperty("Section", Label.class , new Label());
		tableFind.setColumnWidth("Section",95);

		tableFind.addContainerProperty("Trans. No", Label.class , new Label());
		tableFind.setColumnWidth("Trans. No",60);

		tableInitialiseFind();

		mainLayout.addComponent(lblJObName,"top:10px;left:10px;");
		mainLayout.addComponent(cmbJobName,"top:8px;left:90px;");
		mainLayout.addComponent(lblDepartment,"top:35px;left:10px;");
		mainLayout.addComponent(cmbDepartment,"top:33px;left:90px;");
		mainLayout.addComponent(lblSection,"top:60px;left:10px;");
		mainLayout.addComponent(cmbSection,"top:58px;left:90px;");

		mainLayout.addComponent(lblTransactionNo,"top:10px;left:300px;");
		mainLayout.addComponent(txtTransactionNo,"top:8px;left:400px;");
		mainLayout.addComponent(lblIndentNo,"top:35px;left:300px;");
		mainLayout.addComponent(txtIndentNo,"top:33px;left:400px;");
		mainLayout.addComponent(lblIndentDate,"top:60px;left:300px;");
		mainLayout.addComponent(dIndentDate,"top:58px;left:400px;");

		mainLayout.addComponent(lblDate,"top:10px;left:510px;");
		mainLayout.addComponent(dDate,"top:8px;left:580px;");
		mainLayout.addComponent(lblSrNo,"top:35px;left:510px;");
		mainLayout.addComponent(txtSRNo,"top:33px;left:580px;");
		mainLayout.addComponent(lblSrDate,"top:60px;left:510px;");
		mainLayout.addComponent(dSrDate,"top:58px;left:580px;");

		mainLayout.addComponent(lbLineInfo,"top:80px;left:5px;");

		mainLayout.addComponent(lblGroupName,"top:100px;left:10px;");
		mainLayout.addComponent(cmbGroupName,"top:98px;left:90px;");
		mainLayout.addComponent(lblItemName,"top:125px;left:10px;");
		mainLayout.addComponent(cmbItemName,"top:123px;left:90px;");
		mainLayout.addComponent(lblProductCod,"top:150px;left:10px;");
		mainLayout.addComponent(cmbProductCode,"top:148px;left:90px;");
		mainLayout.addComponent(lblSubItemName,"top:175px;left:10px;");
		mainLayout.addComponent(txtSubItemName,"top:173px;left:90px;");
		mainLayout.addComponent(lblUnit,"top:200px;left:10px;");
		mainLayout.addComponent(txtUnit,"top:198px;left:90px;");

		mainLayout.addComponent(lblModelName,"top:100px;left:320px;");
		mainLayout.addComponent(txtModelName,"top:98px;left:400px;");
		mainLayout.addComponent(lblStockQty,"top:125px;left:320px;");
		mainLayout.addComponent(txtStockQty,"top:123px;left:400px;");
		mainLayout.addComponent(lblIndentQty,"top:150px;left:320px;");
		mainLayout.addComponent(txtIndentQty,"top:148px;left:400px;");
		mainLayout.addComponent(lblIssueQty,"top:175px;left:320px;");
		mainLayout.addComponent(txtIssueQty,"top:173px;left:400px;");
		mainLayout.addComponent(lblRate,"top:200px;left:320px;");
		mainLayout.addComponent(txtRate,"top:198px;left:400px;");

		mainLayout.addComponent(lblAmount,"top:100px;left:510px;");
		mainLayout.addComponent(txtAmount,"top:98px;left:600px;");
		mainLayout.addComponent(lblStoreName,"top:125px;left:510px;");
		mainLayout.addComponent(cmbStoreName,"top:123px;left:600px;");
		mainLayout.addComponent(lblRackName,"top:150px;left:510px;");
		mainLayout.addComponent(cmbRackName,"top:148px;left:600px;");
		mainLayout.addComponent(lblShelfName,"top:175px;left:510px;");
		mainLayout.addComponent(cmbShelfName,"top:173px;left:600px;");
		mainLayout.addComponent(lblRemarks,"top:200px;left:510px;");
		mainLayout.addComponent(txtRemarks,"top:198px;left:600px;");

		mainLayout.addComponent(btnSubmit,"top:120px;left:710px;");

		mainLayout.addComponent(lbLineDetails,"top:220px;left:5px;");

		mainLayout.addComponent(table,"top:245px;left:0px;");
		mainLayout.addComponent(lbLine,"top:550px;left:5px;");
		mainLayout.addComponent(cButton,"top:570px;left:300px;");

		mainLayout.addComponent(lbLineFind1,"top:0px;left:805px;");
		mainLayout.addComponent(lbLineFind2,"top:230px;left:805px;");

		mainLayout.addComponent(lblFromDateFind,"top:20px;left:805px;");
		mainLayout.addComponent(dFromDateFind,"top:18px;left:875px;");
		mainLayout.addComponent(lblToDateFind,"top:20px;left:995px;");
		mainLayout.addComponent(dToDateFind,"top:18px;left:1055px;");
		mainLayout.addComponent(btnFind,"top:20px;left:1170px;");

		mainLayout.addComponent(tableFind,"top:45px;left:805px;");

		return mainLayout;
	}
	private void findInitialize(String id){
		String sql="select transactionNo,date,jobid,deptId,sectionId,indentNo,indentDate,srNo,srDate " +
				"from tbIssueInfo where transactionNo='"+id+"'";
		Iterator<?> iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtTransactionNo.setValue(element[0]);
			dDate.setValue(element[1]);
			cmbJobName.setValue(element[2]);
			cmbDepartment.setValue(element[3]);
			cmbSection.setValue(element[4]);
			txtIndentNo.setValue(element[5]);
			dIndentDate.setValue(element[6]);
			txtSRNo.setValue(element[7]);
			dSrDate.setValue(element[8]);
		}
		String sqlDetails="select groupId,groupName,ItemCode,ItemName,productCode,unit," +
				"subItemName,specId,specName,modelName, "+
				" IndentQty,IssueQty,rate,amount,storeId,storename,rackId,rackName,shelfId," +
				"shelfName,remarks from tbIssueDetails where transactionNo='"+id+"'";
		System.out.println(sqlDetails);
		Iterator<?>iterDetails=dbService(sqlDetails);
		int a=0;
		while(iterDetails.hasNext()){
			Object element[]=(Object[])iterDetails.next();
			tbLblGroupCode.get(a).setValue(element[0]);
			tbLblGroupName.get(a).setValue(element[1]);
			tbLblItemCode.get(a).setValue(element[2]);
			tbLblItemName.get(a).setValue(element[3]);
			tbLblProductCode.get(a).setValue(element[4]);
			tbUnit.get(a).setValue(element[5]);
			tbSubItem.get(a).setValue(element[6]);
			tbLblSpecId.get(a).setValue(element[7]);
			tbLblSpecName.get(a).setValue(element[8]);
			tbModelNo.get(a).setValue(element[9]);
			tbIndentQty.get(a).setValue(df.format(element[10]));
			tbIssueQty.get(a).setValue(df.format(element[11]));
			tbRate.get(a).setValue(df.format(element[12]));
			tbAmount.get(a).setValue(df.format(element[13]));
			tbLblStoreId.get(a).setValue(element[14]);
			tbLblStore.get(a).setValue(element[15]);
			tbLblRackId.get(a).setValue(element[16]);
			tbLblRack.get(a).setValue(element[17]);
			tbLblShelfId.get(a).setValue(element[18]);
			tbLblShelf.get(a).setValue(element[19]);
			tbRemarks.get(a).setValue(element[20]);
			a++;
		}
	}
	private void tableRowAddFind(final int ar){
		lblSlFind.add(ar, new Label());
		lblSlFind.get(ar).setValue(ar+1);
		lblSlFind.get(ar).setWidth("100%");
		lblSlFind.get(ar).setHeight("-1px");
		
		tbChkShowFind.add(ar, new CheckBox());
		tbChkShowFind.get(ar).setImmediate(true);
		tbChkShowFind.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tbChkShowFind.get(ar).booleanValue())
				{
					for(int a=0;a<lblSlFind.size();a++){
						if(ar!=a){
							tbChkShowFind.get(a).setValue(false);
						}
					}
					if(!lblIssueNo.get(ar).getValue().toString().isEmpty()){
						infoClear();
						detailsClear();
						findInitialize(lblIssueNo.get(ar).getValue().toString());
						isUpdate=true;
					}
					else{
						showNotification("Sorry!!","Empty Row",Notification.TYPE_WARNING_MESSAGE);
						tbChkShowFind.get(ar).setValue(false);
					}
				}

			}
		});

		lblDeptFind.add(ar, new Label());
		lblDeptFind.get(ar).setWidth("100%");
		lblDeptFind.get(ar).setHeight("-1px");

		lblSectionFind.add(ar, new Label());
		lblSectionFind.get(ar).setWidth("100%");
		lblSectionFind.get(ar).setHeight("-1px");

		lblIssueNo.add(ar, new Label());
		lblIssueNo.get(ar).setWidth("100%");
		lblIssueNo.get(ar).setHeight("-1px");

		tableFind.addItem(new Object[]{lblSlFind.get(ar),tbChkShowFind.get(ar),lblDeptFind.get(ar),lblSectionFind.get(ar),lblIssueNo.get(ar)},ar);
	}
	private void tableRowAdd(final int ar)
	{
		lblSl.add(ar, new Label());
		lblSl.get(ar).setValue(ar+1);
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setHeight("-1px");

		tbChkShow.add(ar, new CheckBox());
		tbChkShow.get(ar).setImmediate(true);
		tbChkShow.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tbChkShow.get(ar).booleanValue())
				{
					for(int a=0;a<lblSl.size();a++){
						if(ar!=a){
							tbChkShow.get(a).setValue(false);
						}
					}
					if(!tbLblGroupCode.get(ar).getValue().toString().isEmpty()){
						detailsClear();
						detailsDataLoad(ar);
						tableRowClear(ar);
						//isUpdate=true;
					}
					else{
						showNotification("Sorry!!","Nothing To Update",Notification.TYPE_WARNING_MESSAGE);
						tbChkShow.get(ar).setValue(false);
					}
				}

			}
		});

		tbLblGroupName.add(ar,new Label());
		tbLblGroupName.get(ar).setWidth("100%");
		tbLblGroupName.get(ar).setImmediate(true);

		tbLblItemName.add(ar,new Label());
		tbLblItemName.get(ar).setWidth("100%");
		tbLblItemName.get(ar).setImmediate(true);

		tbLblProductCode.add(ar,new Label());
		tbLblProductCode.get(ar).setWidth("100%");
		tbLblProductCode.get(ar).setImmediate(true);

		tbSubItem.add( ar , new Label());
		tbSubItem.get(ar).setWidth("100%");

		tbUnit.add( ar , new Label());
		tbUnit.get(ar).setWidth("100%");

		tbModelNo.add( ar , new Label());
		tbModelNo.get(ar).setWidth("100%");

		tbStockQty.add( ar , new TextRead(1));
		tbStockQty.get(ar).setWidth("100%");

		tbIndentQty.add( ar , new TextRead(1));
		tbIndentQty.get(ar).setWidth("100%");

		tbIssueQty.add( ar , new TextRead(1));
		tbIssueQty.get(ar).setWidth("100%");

		tbBalanceIndentQty.add( ar , new TextRead(1));
		tbBalanceIndentQty.get(ar).setWidth("100%");

		tbBalanceStockQty.add( ar , new TextRead(1));
		tbBalanceStockQty.get(ar).setWidth("100%");

		tbRate.add( ar , new TextRead(1));
		tbRate.get(ar).setWidth("100%");

		tbAmount.add( ar , new TextRead(1));
		tbAmount.get(ar).setWidth("100%");

		tbLblStore.add(ar,new Label());
		tbLblStore.get(ar).setWidth("100%");
		tbLblStore.get(ar).setImmediate(true);

		tbLblRack.add(ar,new Label());
		tbLblRack.get(ar).setWidth("100%");
		tbLblRack.get(ar).setImmediate(true);

		tbLblShelf.add(ar,new Label());
		tbLblShelf.get(ar).setWidth("100%");
		tbLblShelf.get(ar).setImmediate(true);

		tbRemarks.add( ar , new Label());
		tbRemarks.get(ar).setWidth("100%");
		tbRemarks.get(ar).setImmediate(true);

		tbLblGroupCode.add( ar , new Label());
		tbLblGroupCode.get(ar).setWidth("100%");
		tbLblGroupCode.get(ar).setImmediate(true);

		tbLblItemCode.add( ar , new Label());
		tbLblItemCode.get(ar).setWidth("100%");
		tbLblItemCode.get(ar).setImmediate(true);

		tbLblStoreId.add( ar , new Label());
		tbLblStoreId.get(ar).setWidth("100%");
		tbLblStoreId.get(ar).setImmediate(true);

		tbLblRackId.add( ar , new Label());
		tbLblRackId.get(ar).setWidth("100%");
		tbLblRackId.get(ar).setImmediate(true);

		tbLblShelfId.add( ar , new Label());
		tbLblShelfId.get(ar).setWidth("100%");
		tbLblShelfId.get(ar).setImmediate(true);

		tbLblSpecId.add( ar , new Label());
		tbLblSpecId.get(ar).setWidth("100%");
		tbLblSpecId.get(ar).setImmediate(true);

		tbLblSpecName.add( ar , new Label());
		tbLblSpecName.get(ar).setWidth("100%");
		tbLblSpecName.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblSl.get(ar),tbLblGroupName.get(ar),tbLblItemName.get(ar),tbLblProductCode.get(ar),
				tbSubItem.get(ar),tbUnit.get(ar),tbModelNo.get(ar),tbStockQty.get(ar),tbIndentQty.get(ar),tbIssueQty.get(ar),
				tbBalanceIndentQty.get(ar),tbBalanceStockQty.get(ar),tbRate.get(ar),tbAmount.get(ar),tbLblStore.get(ar),
				tbLblRack.get(ar),tbLblShelf.get(ar),tbRemarks.get(ar),tbChkShow.get(ar),tbLblGroupCode.get(ar),
				tbLblItemCode.get(ar),tbLblStoreId.get(ar),tbLblRackId.get(ar),tbLblShelfId.get(ar),tbLblSpecId.get(ar),tbLblSpecName.get(ar)},ar);
	}
	public void tableInitialise()
	{

		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}
	public void tableInitialiseFind()
	{

		for(int i=0;i<10;i++)
		{
			tableRowAddFind(i);
		}
	}
}
