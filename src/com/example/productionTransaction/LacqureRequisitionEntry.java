
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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class LacqureRequisitionEntry extends Window 
{
	private TextRead txtBatchNo;
	private ComboBox cmbFrom,cmbTo;
	private TextField txtreqNo;
	private PopupDateField dreqDate;
	private SessionBean sessionBean;

	private Label lblBatchNo;
	private Label lblFrom;
	private Label lblTo;
	private Label lblReqNo;
	private Label lblReqDate;

	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> tbCmbItemName = new ArrayList<ComboBox>();
	private ArrayList<Label> tblblCode= new ArrayList<Label>();
	private ArrayList<Label> tblblColor = new ArrayList<Label>();
	private ArrayList<Label> tblblUnit = new ArrayList<Label>();
	private ArrayList<TextRead> tbLblSectionStock = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbLblLacqureStock = new ArrayList<TextRead>();
	private ArrayList<AmountField> tblblReqQty = new ArrayList<AmountField>();

	private CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	private AbsoluteLayout mainLayout;
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat df=new DecimalFormat("#0.00");
	boolean isUpdate=false;
	private TextField txtBatchNoFind=new TextField();

	public LacqureRequisitionEntry(SessionBean sessionBean, String string, int i)
	{
		this.setResizable(false);
		this.setCaption("REQUISITION ENTRY [LACQURE] :: "+sessionBean.getCompany());
		this.sessionBean=sessionBean;

		setContent(buildMainLayout());
		btnAction();

		btnIni(true);
		componentIni(true);
		txtClear();
		tableClear();
		focusMove();
		cButton.btnNew.focus();
	}
	private void tableClear() {
		for(int a=0;a<tbCmbItemName.size();a++){
			tbCmbItemName.get(a).setValue(null);
			tblblCode.get(a).setValue("");
			tblblUnit.get(a).setValue("");
			tblblColor.get(a).setValue("");
			tbLblSectionStock.get(a).setValue("");
			tbLblLacqureStock.get(a).setValue("");
			tblblReqQty.get(a).setValue("");
		}
	}
	private void txtClear() {
		cmbFrom.setValue(null);
		cmbTo.setValue(null);
		txtBatchNo.setValue("");
		txtreqNo.setValue("");
	}
	private void newButtonEvent()
	{

		componentIni(false);
		btnIni(false);
		txtClear();
		tableClear();
		isUpdate=false;
	}
	private void componentIni(boolean b) {
		txtBatchNo.setEnabled(!b);
		cmbFrom.setEnabled(!b);
		cmbTo.setEnabled(!b);
		dreqDate.setEnabled(!b);
		txtreqNo.setEnabled(!b);
		table.setEnabled(!b);
	}
	private void focusMove(){
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(cmbFrom);
		focusComp.add(cmbTo);

		for(int i = 0; i < tblblCode.size(); i++)
		{
			focusComp.add(tbCmbItemName.get(i));
			focusComp.add(tblblReqQty.get(i));
		}

		focusComp.add(cButton.btnNew);
		focusComp.add(cButton.btnEdit);
		focusComp.add(cButton.btnSave);
		focusComp.add(cButton.btnRefresh);
		focusComp.add(cButton.btnDelete);
		focusComp.add(cButton.btnFind);

		new FocusMoveByEnter(this, focusComp);
	}
	private void tbCmbDataLoad(int ar){
		tbCmbItemName.get(ar).removeAllItems();
		Iterator iter=dbService("select FgId,FgName from funLacqureStock('%','"+dateFormat.format(dreqDate.getValue())+"') order by FgName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbItemName.get(ar).addItem(element[0]);
			tbCmbItemName.get(ar).setItemCaption(element[0], element[1].toString());
		}
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
	
	private String getTransactionNo(){

		Iterator iter=dbService("select 0,isNull(max(cast(SUBSTRING(batchNo,CHARINDEX('-',batchNo)+1,len(batchNo)-CHARINDEX('-',batchNo))as int)),0)+1 id from tbRequisitionEntryLacqureInfo");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			return element[1].toString();
		}
		return "";
	}
	private String getReqNo(){

		Iterator iter=dbService("select 0,isNull(max(cast(SUBSTRING(reqNo,CHARINDEX('-',reqNo)+1,len(reqNo)-CHARINDEX('-',reqNo))as int)),0)+1 id from tbRequisitionEntryLacqureInfo");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			return element[1].toString();
		}
		return "";
	}
	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
	}
	public void btnAction()
	{
		cButton.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				componentIni(true);
				btnIni(true);
				txtClear();
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cButton.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				editButtonEvent();
				isUpdate = true;

			}
		});

		cButton.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate=true;
				findButtonEvent();	
			}
		});
		cButton.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(checkValidation()){
					saveButtonEvent();
				}
			}
		});
		
		cmbFrom.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbFrom.getValue()!=null){
					txtreqNo.setValue("Req-"+getReqNo());
					if(cmbFrom.getValue().toString().equalsIgnoreCase("Lacqure")){
						txtBatchNo.setValue("Lacqure-"+getTransactionNo());
					}
					/*else if(cmbFrom.getValue().toString().equalsIgnoreCase("Screen Printing")){
						txtBatchNo.setValue("SP-"+getTransactionNo());
					}
					else if(cmbFrom.getValue().toString().equalsIgnoreCase("Labeling")){
						txtBatchNo.setValue("Labeling-"+getTransactionNo());
					}
					else if(cmbFrom.getValue().toString().equalsIgnoreCase("Cap Folding")){
						txtBatchNo.setValue("Cap-"+getTransactionNo());
					}
					else if(cmbFrom.getValue().toString().equalsIgnoreCase("Stretch Blow Molding")){
						txtBatchNo.setValue("SBM-"+getTransactionNo());
					}*/

				}
				else{
					txtBatchNo.setValue("");
					txtreqNo.setValue("");
				}
			}
		});

	}
	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			
			String sqlUd="insert into tbUdRequisitionEntryLacqureInfo select reqNo, batchNo, ReqFrom,"
					+ " ReqTo, ReqDate, userIp, userName, entryTime,'Update' vUdFlag "
					+ "from tbRequisitionEntryLacqureInfo where batchNo='"+txtBatchNo.getValue()+ "'";
			
			session.createSQLQuery(sqlUd).executeUpdate();	
			
			
			String sqlDetailsUd="insert into tbUdRequisitionEntryLacqureDetails "
					+ "select  batchNo, reqNo, productId, productName, unit, color,"
					+ " reqQty,'Update' vUdFlag  from tbRequisitionEntryLacqureDetails"
					+ " where batchNo='"+txtBatchNo.getValue()+ "'";
			
			session.createSQLQuery(sqlDetailsUd).executeUpdate();	
			
			session.createSQLQuery("delete from tbRequisitionEntryLacqureInfo where batchNo='"+txtBatchNo.getValue()+ "'").executeUpdate();
			///System.out.println("delete tbLabelProductionInfo where ProductionNo='"+txtProductionNo.getValue()+ "' ");

			session.createSQLQuery("delete from tbRequisitionEntryLacqureDetails where batchNo='"+txtBatchNo.getValue()+ "'").executeUpdate();
			//System.out.println("delete tbLabelProductionDetails where ProductionNo='"+txtProductionNo.getValue()+"' ");
			//session.createSQLQuery(" delete from tbMouldFinishProduct where ProductionNo like '"+txtProductionNo.getValue().toString()+"' ").executeUpdate();
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
						componentIni(true);
						btnIni(true);
						tableClear();
						txtClear();
						mb.buttonLayout.getComponent(0).setEnabled(false);
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
						mb.buttonLayout.getComponent(0).setEnabled(false);
					}
				}
			});
		}

	}
	private void insertData(){
		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		try{
			String sqlInfo="insert into tbRequisitionEntryLacqureInfo(reqNo,batchNo,ReqFrom,ReqTo,ReqDate,userIp,userName,entryTime) "+
					" values('"+txtreqNo.getValue()+"','"+txtBatchNo.getValue()+"','"+cmbFrom.getValue()+"'," +
					"'"+cmbTo.getValue()+"','"+dateFormat.format(dreqDate.getValue())+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
			session.createSQLQuery(sqlInfo).executeUpdate();
			
			if(isUpdate){
				String sqlUdInfo="insert into tbUdRequisitionEntryLacqureInfo(reqNo,batchNo,ReqFrom,ReqTo,ReqDate,userIp,userName,entryTime,vUdFlag) "+
						" values('"+txtreqNo.getValue()+"','"+txtBatchNo.getValue()+"','"+cmbFrom.getValue()+"'," +
						"'"+cmbTo.getValue()+"','"+dateFormat.format(dreqDate.getValue())+"'," +
						"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'New')";
				session.createSQLQuery(sqlUdInfo).executeUpdate();
			}
			
			for(int a=0;a<tblblCode.size();a++){
				if(tbCmbItemName.get(a).getValue()!=null&&Double.parseDouble("0"+tblblReqQty.get(a).getValue().toString())>0){
					String sqlDetails="insert into tbRequisitionEntryLacqureDetails(batchNo,ReqNo,productId," +
					"productName,unit,color,reqQty) "+
					" values('"+txtBatchNo.getValue()+"','"+txtreqNo.getValue()+"'," +
					"'"+tbCmbItemName.get(a).getValue()+"','"+tbCmbItemName.get(a).getItemCaption(tbCmbItemName.get(a).getValue())+"'," +
					"'"+tblblUnit.get(a).getValue()+"','"+tblblColor.get(a).getValue()+"','"+tblblReqQty.get(a).getValue()+"')";
					session.createSQLQuery(sqlDetails).executeUpdate();
					
					if(isUpdate){
						String sqlUdDetails="insert into tbUdRequisitionEntryLacqureDetails(batchNo,ReqNo,productId," +
								"productName,unit,color,reqQty,vUdFlag) "+
								" values('"+txtBatchNo.getValue()+"','"+txtreqNo.getValue()+"'," +
								"'"+tbCmbItemName.get(a).getValue()+"','"+tbCmbItemName.get(a).getItemCaption(tbCmbItemName.get(a).getValue())+"'," +
								"'"+tblblUnit.get(a).getValue()+"','"+tblblColor.get(a).getValue()+"','"+tblblReqQty.get(a).getValue()+"','New')";
								session.createSQLQuery(sqlUdDetails).executeUpdate();
					}
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
	private boolean checkValidation(){
		if(cmbFrom.getValue()!=null){
			if(cmbTo.getValue()!=null){
				if(tbCmbItemName.get(0).getValue()!=null&&
						Double.parseDouble("0"+tblblReqQty.get(0).getValue().toString())>0){
					return true;
				}
				else{
					showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide To",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide From",Notification.TYPE_WARNING_MESSAGE);
		}
		
		return false;
	}
	//Button Action End

	private void findButtonEvent()
	{
		Window win = new LacqureRequisitonEntryFindWindow(sessionBean,txtBatchNoFind);
		
		  win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtBatchNoFind.getValue().toString().length() > 0)
				{
					System.out.println(txtBatchNoFind.getValue().toString());
					findInitialise(txtBatchNoFind.getValue().toString());
					cButton.btnEdit.focus();
				}
			}
		});
		this.getParent().addWindow(win);
	}
	private void findInitialise(String batchNo){
		Iterator iter=dbService("select  a.reqNo,a.batchNo,a.ReqFrom,a.ReqTo,a.ReqDate,b.productId,b.reqQty "+
				" from tbRequisitionEntryLacqureInfo a "+
				" inner join tbRequisitionEntryLacqureDetails b "+ 
				" on a.batchNo=b.batchNo where a.batchNo='"+batchNo+"'");
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==0){
				cmbFrom.setValue(element[2]);
				cmbTo.setValue(element[3]);
				txtreqNo.setValue(element[0]);
				txtBatchNo.setValue(element[1]);
				dreqDate.setValue(element[4]);
			}
			tbCmbItemName.get(a).setValue(element[5]);
			tblblReqQty.get(a).setValue(df.format(element[6]));
			a++;
			if(a==tbCmbItemName.size()-1){
				tableRowAdd(a+1);
			}
		}
	}

	public void tableInitialise(){
		for(int i=0;i<10;i++){
			tableRowAdd(i);
		}
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
	private void tbCmbAction(int ar){

		/*Iterator iter=dbService("select unit,color,semiFgStock,isnull((select semiFgSectionStock from " +
				"funcSemiFgPrintingLabelingStock('"+tbCmbItemName.get(ar).getValue()+"','"+dateFormat.format(dreqDate.getValue())+"')),0)semiFgSectionStock from funcSemiFgStock" +
				"('"+tbCmbItemName.get(ar).getValue()+"','"+dateFormat.format(dreqDate.getValue())+"') order by semiFgName");*/
		Iterator iter=dbService("select sectionStock,lecqureStock from funLacqureStock" +
				"('"+tbCmbItemName.get(ar).getValue()+"','"+dateFormat.format(dreqDate.getValue())+"')");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbLblSectionStock.get(ar).setValue(df.format(element[0]));
			tbLblLacqureStock.get(ar).setValue(df.format(element[1]));
		}
	}
	private boolean doubleEntryCheck(int ar){
		for(int a=0;a<tblblCode.size();a++)
		{
			if(a!=ar){
				if(tbCmbItemName.get(a).getValue()!=null)
				{
					if(tbCmbItemName.get(ar).getValue().toString().equalsIgnoreCase(tbCmbItemName.get(a).getValue().toString())){
						return false;
					}
				}
			}
		}
		return true;
	}
	public void tableRowAdd(final int ar)
	{
		try{

			tbLblSl.add(ar,new Label());
			tbLblSl.get(ar).setWidth("20px");
			tbLblSl.get(ar).setValue(ar + 1);	

			tbCmbItemName.add(ar,new ComboBox());
			tbCmbItemName.get(ar).setWidth("100%");
			tbCmbItemName.get(ar).setImmediate(true);
			tbCmbItemName.get(ar).setNullSelectionAllowed(true);
			tbCmbItemName.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			tbCmbDataLoad(ar);

			tbCmbItemName.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(tbCmbItemName.get(ar).getValue()!=null){
						if(doubleEntryCheck(ar)){
							tbCmbAction(ar);
							if(ar==tbLblSl.size()-1){
								tableRowAdd(ar+1);
							}
						}
						else{
							showNotification("Sorry!!","Double Entry",Notification.TYPE_WARNING_MESSAGE);
							tbCmbItemName.get(ar).setValue(null);
							tbCmbItemName.get(ar).focus();
						}
					}
					else{
						tblblCode.get(ar).setValue("");
						tblblColor.get(ar).setValue("");
						tblblUnit.get(ar).setValue("");
						tbLblSectionStock.get(ar).setValue("");
						tblblReqQty.get(ar).setValue("");
					}
				}
			});


			tblblCode.add(ar, new Label());
			tblblCode.get(ar).setImmediate(true);
			tblblCode.get(ar).setWidth("100%");
			tblblCode.get(ar).setHeight("-1px");

			tblblColor.add(ar, new Label());
			tblblColor.get(ar).setImmediate(true);
			tblblColor.get(ar).setWidth("100%");
			tblblColor.get(ar).setHeight("-1px");


			tblblUnit.add(ar, new Label());
			tblblUnit.get(ar).setImmediate(true);
			tblblUnit.get(ar).setWidth("100%");
			tblblUnit.get(ar).setHeight("-1px");

			tbLblSectionStock.add(ar, new TextRead(1));
			tbLblSectionStock.get(ar).setImmediate(true);
			tbLblSectionStock.get(ar).setWidth("100%");
			tbLblSectionStock.get(ar).setHeight("-1px");
			
			tbLblLacqureStock.add(ar, new TextRead(1));
			tbLblLacqureStock.get(ar).setImmediate(true);
			tbLblLacqureStock.get(ar).setWidth("100%");
			tbLblLacqureStock.get(ar).setHeight("-1px");

			tblblReqQty.add(ar, new AmountField());
			tblblReqQty.get(ar).setImmediate(true);
			tblblReqQty.get(ar).setWidth("100%");
			tblblReqQty.get(ar).setHeight("-1px");

			tblblReqQty.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(!isUpdate){
						double stock=Double.parseDouble(tbLblSectionStock.get(ar).getValue().toString().isEmpty()?"0.0":tbLblSectionStock.get(ar).getValue().toString());
						double reqQty=Double.parseDouble(tblblReqQty.get(ar).getValue().toString().isEmpty()?"0.0":tblblReqQty.get(ar).getValue().toString());
						if(stock<reqQty){
							showNotification("Sorry!!","Req Qty Exceed Stock Qty",Notification.TYPE_WARNING_MESSAGE);
							tblblReqQty.get(ar).setValue("");
						}
					}
				}
			});

			table.addItem(new Object[]{tbLblSl.get(ar),	tbCmbItemName.get(ar),tblblCode.get(ar),tblblUnit.get(ar),tblblColor.get(ar),
					tbLblSectionStock.get(ar),tbLblLacqureStock.get(ar),tblblReqQty.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("700px");
		mainLayout.setHeight("480px");

		lblBatchNo=new Label();
		lblBatchNo.setImmediate(false);
		lblBatchNo.setWidth("-1px");
		lblBatchNo.setHeight("-1px");
		lblBatchNo.setValue("BatchNo :");
		mainLayout.addComponent(lblBatchNo, "top:20.0px;left:20.0px;");

		txtBatchNo=new TextRead();
		txtBatchNo.setImmediate(true);
		txtBatchNo.setWidth("105px");
		txtBatchNo.setHeight("-1px");
		mainLayout.addComponent(txtBatchNo, "top:18.0px;left:110.0px;");

		lblFrom = new Label("From: ");
		lblFrom.setImmediate(false);
		lblFrom.setWidth("-1px");
		lblFrom.setHeight("-1px");
		mainLayout.addComponent(lblFrom, "top:40.0px;left:20.0px;");

		cmbFrom =new ComboBox();
		cmbFrom.setImmediate(true);
		cmbFrom.setWidth("200px");
		cmbFrom.setHeight("24px");
		cmbFrom.setNullSelectionAllowed(true);
		cmbFrom.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbFrom, "top:38.0px;left:110.0px;");
		cmbFrom.addItem("Lacqure");
		/*cmbFrom.addItem("Dry Offset Printing");
		cmbFrom.addItem("Screen Printing");
		cmbFrom.addItem("Heat Trasfer Label");
		cmbFrom.addItem("Manual Printing");
		cmbFrom.addItem("Labeling");
		cmbFrom.addItem("Cap Folding");
		cmbFrom.addItem("Stretch Blow Molding");*/

		lblTo = new Label("To: ");
		lblTo.setImmediate(false);
		lblTo.setWidth("-1px");
		lblTo.setHeight("-1px");
		mainLayout.addComponent(lblTo, "top:64.0px;left:20.0px;");

		cmbTo =new ComboBox();
		cmbTo.setImmediate(true);
		cmbTo.setWidth("200px");
		cmbTo.setHeight("24px");
		cmbTo.setNullSelectionAllowed(true);
		cmbTo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbTo, "top:62.0px;left:110.0px;");
		cmbTo.addItem("Dry Offset Printing");



		lblReqNo = new Label();
		lblReqNo.setImmediate(false);
		lblReqNo.setWidth("-1px");
		lblReqNo.setHeight("-1px");
		lblReqNo.setValue("ReqNo :");
		mainLayout.addComponent(lblReqNo, "top:20.0px;left:350.0px;");

		txtreqNo =new TextField();
		txtreqNo.setImmediate(true);
		txtreqNo.setWidth("105px");
		txtreqNo.setHeight("-1px");
		mainLayout.addComponent(txtreqNo, "top:18.0px;left:440.0px;");

		lblReqDate = new Label("Req Date: ");
		lblReqDate.setImmediate(false);
		lblReqDate.setWidth("-1px");
		lblReqDate.setHeight("-1px");
		mainLayout.addComponent(lblReqDate, "top:46.0px;left:350.0px;");

		dreqDate = new PopupDateField();
		dreqDate.setImmediate(true);
		dreqDate.setWidth("110px");
		dreqDate.setDateFormat("dd-MM-yyyy");
		dreqDate.setValue(new java.util.Date());
		dreqDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dreqDate, "top:44.0px;left:440.0px;");


		table = new Table();
		table.setWidth("99%");
		table.setHeight("325px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("ItemName",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ItemName",307);

		table.addContainerProperty("ItemCod",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ItemCod",60);
		table.setColumnCollapsed("ItemCod",true);

		table.addContainerProperty("Unit",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Unit",40);
		table.setColumnCollapsed("Unit",true);

		table.addContainerProperty("Color",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Color",90);
		table.setColumnCollapsed("Color",true);

		table.addContainerProperty("Section Stock",  TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Section Stock",90);
		
		table.addContainerProperty("Lacqure Stock",  TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Lacqure Stock",90);

		table.addContainerProperty("ReqQty",  AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ReqQty",90);
		mainLayout.addComponent(table, "top:90.0px;left:5.0px;");
		tableInitialise();
		mainLayout.addComponent(cButton, "top:440.0px;left:70.0px;");
		setStyleName("cwindow");
		return mainLayout;
	}

}
