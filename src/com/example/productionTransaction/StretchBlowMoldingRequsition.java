
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
public class StretchBlowMoldingRequsition extends Window 
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
	private ArrayList<TextRead> tblblSBAQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tblblSectionStockQty = new ArrayList<TextRead>();
	private ArrayList<AmountField> tblblReqQty = new ArrayList<AmountField>();

	private CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	private AbsoluteLayout mainLayout;
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat df=new DecimalFormat("#0.00");
	boolean isUpdate=false;
	private TextField txtBatchNoFind=new TextField();

	public StretchBlowMoldingRequsition(SessionBean sessionBean, String string, int i)
	{
		this.setResizable(false);
		this.setCaption("STRETCH BLOW MOLDING REQUISITION :: "+sessionBean.getCompany());
		this.sessionBean=sessionBean;

		setContent(buildMainLayout());
		btnAction();

		btnIni(true);
		componentIni(true);
		txtClear();
		tableClear();
		focusMove();
		cButton.btnNew.focus();
		IniAction();
		sectionToLoadData();
	}
	private void tableClear() {
		for(int a=0;a<tbCmbItemName.size();a++){
			tbCmbItemName.get(a).setValue(null);
			tblblCode.get(a).setValue("");
			tblblColor.get(a).setValue("");
			tblblSBAQty.get(a).setValue("");
			tblblSectionStockQty.get(a).setValue("");
			tblblReqQty.get(a).setValue("");
		}
	}
	private void txtClear() {
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
		IniAction();
	}
	private void IniAction(){
		txtreqNo.setValue("Req-"+getReqNo());
		System.out.println("Req-"+getReqNo());
		txtBatchNo.setValue("SBM-"+getTransactionNo());
		System.out.println("SBM-"+getTransactionNo());
		cmbFrom.setEnabled(false);
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
		
		String sql="select semiFgCode,semiFgName,color from tbSemiFgInfo where productionTypeId='PT-2' and (select top 1 isFg from tbFinishedGoodsStandardInfo "
				   +"where  fGCode=tbSemiFgInfo.semiFgCode and declarationDate=(select MAX(declarationDate) from tbFinishedGoodsStandardInfo where fGCode=semiFgCode))='NO' ";
		//Iterator iter=dbService("select semiFgId,semiFgName,color from funcSemiFgStock('%','"+dateFormat.format(dreqDate.getValue())+"') order by semiFgName");
		
		Iterator iter=dbService(sql);
		
		
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbItemName.get(ar).addItem(element[0]);
			tbCmbItemName.get(ar).setItemCaption(element[0], element[1].toString()+" # "+element[2].toString());
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
	private void sectionToLoadData() {
		cmbTo.removeAllItems();
		Iterator iter=dbService("select vDepoId,vDepoName from tbDepoInformation order by vDepoName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbTo.addItem(element[0]);
			cmbTo.setItemCaption(element[0], element[1].toString());
		}
	}
	private String getTransactionNo(){

		Iterator iter=dbService("select 0,isNull(max(cast(SUBSTRING(vBatchNo,CHARINDEX('-',vBatchNo)+1,len(vBatchNo)-CHARINDEX('-',vBatchNo))as int)),0)+1 id from tbStretchBlowMoldingRequisitionDetails");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			return element[1].toString();
		}
		return "";
	}
	private String getReqNo(){

		Iterator iter=dbService("select 0,isNull(max(cast(SUBSTRING(vReqNo,CHARINDEX('-',vReqNo)+1,len(vReqNo)-CHARINDEX('-',vReqNo))as int)),0)+1 id from tbStretchBlowMoldingRequisitionInfo");
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


	}
	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			String sqlUd="insert into tbUdStretchBlowMoldingRequisition select  a.vBatchNo,a.vReqNo, "
					+ " a.vReqFrom, a.vReqTo, a.dReqDate,b.vProductId,b.vProductName,b.vColor,b.vSectionStock,"
					+ "b.vSBMQty,b.ReqQty, a.userIp, a.userName, a.entryTime,'Old' vUdFlag  "
					+ "from tbStretchBlowMoldingRequisitionInfo a "
					+ "inner join tbStretchBlowMoldingRequisitionDetails b on a.vBatchNo= b.vBatchNo "
					+ "where a.vBatchNo='"+txtBatchNo.getValue()+ "'";


			session.createSQLQuery(sqlUd).executeUpdate();
			session.createSQLQuery("delete from tbStretchBlowMoldingRequisitionInfo where vBatchNo='"+txtBatchNo.getValue()+ "'").executeUpdate();
			session.createSQLQuery("delete from tbStretchBlowMoldingRequisitionDetails where vBatchNo='"+txtBatchNo.getValue()+ "'").executeUpdate();		
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
			System.out.println("add a Result: "+isUpdate);
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

		if(isUpdate){
			for(int a=0;a<tbCmbItemName.size();a++){
				if(tbCmbItemName.get(a).getValue()!=null&&Double.parseDouble("0"+tblblReqQty.get(a).getValue().toString())>0){

					Object cmbItemId=tbCmbItemName.get(a).getValue();
					Object cmbItemName=tbCmbItemName.get(a).getItemCaption(tbCmbItemName.get(a).getValue());
					Object txtColor=tblblColor.get(a).getValue();
					Object txtSectionStockQty = tblblSectionStockQty.get(a).getValue();
					Object txtSBAQty = tblblSBAQty.get(a).getValue();
					Object txtReqQty=tblblReqQty.get(a).getValue();

					String sqlUpdate="insert into tbUdStretchBlowMoldingRequisition(vBatchNo,vReqNo,vReqFrom,vReqTo,dReqDate,"
							+ "vProductId,vProductName,vColor,vSectionStock,"
							+ "vSBMQty,ReqQty,userIp,userName,entryTime,vFlag) "
							+ "values"
							+ "('"+txtBatchNo.getValue()+"',"
							+ "'"+txtreqNo.getValue()+"',"
							+ "'"+cmbFrom.getValue()+"',"
							+ "'"+cmbTo.getItemCaption(cmbTo.getValue())+"',"
							+ "'"+dateFormat.format(dreqDate.getValue())+"',"
							+ "'"+cmbItemId+"',"
							+ "'"+cmbItemName+"',"
							+ "'"+txtColor+"',"
							+ "'"+txtSectionStockQty+"',"
							+ "'"+txtSBAQty+"',"
							+ "'"+txtReqQty+"',"
							+ "'"+sessionBean.getUserIp()+"',"
							+ "'"+sessionBean.getUserName()+"',"
							+ "CURRENT_TIMESTAMP,'New')";

					session.createSQLQuery(sqlUpdate).executeUpdate();

				}
			}
		}





		try{
			String sqlInfo="insert into tbStretchBlowMoldingRequisitionInfo(vBatchNo,vReqNo,vReqFrom,vReqTo,dReqDate,userIp,userName,entryTime) "
					+ "values"
					+ "('"+txtBatchNo.getValue()+"',"
					+ "'"+txtreqNo.getValue()+"',"
					+ "'"+cmbFrom.getValue()+"',"
					+ "'"+cmbTo.getItemCaption(cmbTo.getValue())+"',"
					+ "'"+dateFormat.format(dreqDate.getValue())+"',"
					+ "'"+sessionBean.getUserIp()+"',"
					+ "'"+sessionBean.getUserName()+"',"
					+ "CURRENT_TIMESTAMP)";

			session.createSQLQuery(sqlInfo).executeUpdate();


			for(int a=0;a<tbCmbItemName.size();a++){
				if(tbCmbItemName.get(a).getValue()!=null&&Double.parseDouble("0"+tblblReqQty.get(a).getValue().toString())>0){

					String sqlDetails="insert into tbStretchBlowMoldingRequisitionDetails"
							+ "(vBatchNo,vReqNo,vProductId,vProductName,vColor,vSectionStock,"
							+ "vSBMQty,ReqQty,userIp,userName,entryTime)  "
							+ "values"
							+ "('"+txtBatchNo.getValue()+"',"
							+ "'"+txtreqNo.getValue()+"',"
							+ "'"+tbCmbItemName.get(a).getValue()+"',"
							+ "'"+tbCmbItemName.get(a).getItemCaption(tbCmbItemName.get(a).getValue())+"',"
							+ "'"+tblblColor.get(a).getValue()+"',"
							+ "'"+tblblSectionStockQty.get(a).getValue()+"',"
							+ "'"+tblblSBAQty.get(a).getValue()+"',"
							+ "'"+tblblReqQty.get(a).getValue()+"',"
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
		Window win = new StretchBlowMoldingRequsitionFindWindow(sessionBean,txtBatchNoFind);

		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtBatchNoFind.getValue().toString().length() > 0)
				{
					System.out.println(txtBatchNoFind.getValue().toString());
					txtClear();
					tableClear();
					findInitialise(txtBatchNoFind.getValue().toString());
					cButton.btnEdit.focus();
				}
			}
		});
		this.getParent().addWindow(win);
	}
	private void findInitialise(String batchNo){
		Iterator iter=dbService("select  a.vReqNo,a.vBatchNo,a.vReqFrom,a.vReqTo,a.dReqDate,"
				+ "b.vProductId,b.vProductName,"
				+ "b.vColor,b.vSectionStock,b.vSBMQty,b.ReqQty "
				+ "from tbStretchBlowMoldingRequisitionInfo a  "
				+ "inner join tbStretchBlowMoldingRequisitionDetails b  "
				+ "on a.vBatchNo=b.vBatchNo where a.vBatchNo='"+batchNo+"'");
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==0){

				cmbFrom.setValue(element[2]);
				cmbTo.addItem(element[3]);
				cmbTo.setValue(element[3]);
				System.out.println("New Problem "+element[3]);
				txtreqNo.setValue(element[0]);
				txtBatchNo.setValue(element[1]);

				dreqDate.setValue(element[4]);
			}
			tbCmbItemName.get(a).setValue(element[5]);

			tblblReqQty.get(a).setValue(df.format(element[10]));

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

		Iterator iter=dbService("select color,semiFgStock,isnull( (select semiFgSectionStock from " +
				"funcSemiFgSBMStock('"+tbCmbItemName.get(ar).getValue()+"','"+dateFormat.format(dreqDate.getValue())+"')),0)semiFgSectionStock from funcSemiFgStock" +
				"('"+tbCmbItemName.get(ar).getValue()+"','"+dateFormat.format(dreqDate.getValue())+"') order by semiFgName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			/*tblblColor.get(ar).setValue(element[0]);
			tblblSBAQty.get(ar).setValue(df.format(element[1]));
			tblblSectionStockQty.get(ar).setValue(df.format(element[2]));*/
			
			tblblColor.get(ar).setValue(element[0]);
			tblblSectionStockQty.get(ar).setValue(df.format(element[1]));
			tblblSBAQty.get(ar).setValue(df.format(element[2]));
		}
	}
	private boolean doubleEntryCheck(int ar){
		for(int a=0;a<tblblCode.size();a++){
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
						tblblSBAQty.get(ar).setValue("");
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

			tblblSectionStockQty.add(ar, new TextRead(1));
			tblblSectionStockQty.get(ar).setImmediate(true);
			tblblSectionStockQty.get(ar).setWidth("100%");
			tblblSectionStockQty.get(ar).setHeight("-1px");

			tblblSBAQty.add(ar, new TextRead(1));
			tblblSBAQty.get(ar).setImmediate(true);
			tblblSBAQty.get(ar).setWidth("100%");
			tblblSBAQty.get(ar).setHeight("-1px");


			tblblReqQty.add(ar, new AmountField());
			tblblReqQty.get(ar).setImmediate(true);
			tblblReqQty.get(ar).setWidth("100%");
			tblblReqQty.get(ar).setHeight("-1px");

			/*tblblReqQty.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(!isUpdate){
						double stock=Double.parseDouble(tblblSBAQty.get(ar).getValue().toString().isEmpty()?"0.0":tblblSBAQty.get(ar).getValue().toString());
						double reqQty=Double.parseDouble(tblblReqQty.get(ar).getValue().toString().isEmpty()?"0.0":tblblReqQty.get(ar).getValue().toString());
						if(stock<reqQty){
							showNotification("Sorry!!","Req Qty Exceed Stock Qty",Notification.TYPE_WARNING_MESSAGE);
							tblblReqQty.get(ar).setValue("");
						}
					}
				}
			});*/

			table.addItem(new Object[]{tbLblSl.get(ar),	tbCmbItemName.get(ar),tblblCode.get(ar),tblblColor.get(ar),
					tblblSectionStockQty.get(ar),tblblSBAQty.get(ar),tblblReqQty.get(ar)},ar);

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
		mainLayout.setWidth("800px");
		mainLayout.setHeight("480px");

		lblBatchNo=new Label();
		lblBatchNo.setImmediate(false);
		lblBatchNo.setWidth("-1px");
		lblBatchNo.setHeight("-1px");
		lblBatchNo.setValue("Batch No :");
		mainLayout.addComponent(lblBatchNo, "top:20.0px;left:20.0px;");

		txtBatchNo=new TextRead();
		txtBatchNo.setImmediate(true);
		txtBatchNo.setWidth("105px");
		txtBatchNo.setHeight("-1px");
		mainLayout.addComponent(txtBatchNo, "top:18.0px;left:110.0px;");

		lblFrom = new Label("Section From : ");
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
		cmbFrom.addItem("Stretch Blow Molding");
		cmbFrom.setValue("Stretch Blow Molding");


		lblTo = new Label("Section To : ");
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



		lblReqNo = new Label();
		lblReqNo.setImmediate(false);
		lblReqNo.setWidth("-1px");
		lblReqNo.setHeight("-1px");
		lblReqNo.setValue("Requsition No : ");
		mainLayout.addComponent(lblReqNo, "top:20.0px;left:570.0px;");

		txtreqNo =new TextField();
		txtreqNo.setImmediate(true);
		txtreqNo.setWidth("105px");
		txtreqNo.setHeight("-1px");
		mainLayout.addComponent(txtreqNo, "top:18.0px;left:670.0px;");

		lblReqDate = new Label("Requsition Date : ");
		lblReqDate.setImmediate(false);
		lblReqDate.setWidth("-1px");
		lblReqDate.setHeight("-1px");
		mainLayout.addComponent(lblReqDate, "top:46.0px;left:570.0px;");

		dreqDate = new PopupDateField();
		dreqDate.setImmediate(true);
		dreqDate.setWidth("110px");
		dreqDate.setDateFormat("dd-MM-yyyy");
		dreqDate.setValue(new java.util.Date());
		dreqDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dreqDate, "top:44.0px;left:670.0px;");


		table = new Table();
		table.setWidth("99%");
		table.setHeight("325px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Item Name",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Item Name",307);

		table.addContainerProperty("ItemCod",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ItemCod",60);
		table.setColumnCollapsed("ItemCod",true);

		table.addContainerProperty("Color",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Color",90);

		table.addContainerProperty("Section Stock",  TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Section Stock",90);

		table.addContainerProperty("SBM Stock",  TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SBM Stock",90);

		table.addContainerProperty("ReqQty",  AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ReqQty",90);
		mainLayout.addComponent(table, "top:90.0px;left:5.0px;");
		tableInitialise();
		mainLayout.addComponent(cButton, "top:440.0px;left:70.0px;");
		setStyleName("cwindow");
		return mainLayout;
	}

}
