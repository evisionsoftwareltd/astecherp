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
import com.vaadin.data.Property;
import com.vaadin.data.Property.*;
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

public class AdjustmentEntry extends Window {
	
	private SessionBean sessionBean;
	private ComboBox cmbProductionStep;
	private ComboBox cmbBatchNumber;
	private Label lblProductionStep;
	private Label lblBatchNumber;
	TextRead txtAdjustmentNo;
	PopupDateField dAdjustmentDate;

	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<Label> tblblProductId = new ArrayList<Label>();
	private ArrayList<Label> tblblProductName = new ArrayList<Label>();
	private ArrayList<Label> tblblColor = new ArrayList<Label>();
	private ArrayList<Label> tblblUnit = new ArrayList<Label>();
	private ArrayList<TextRead> tbTxtBatchQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtProductionQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtRejectionQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtUsedQty = new ArrayList<TextRead>();
	private ArrayList<AmountField> tbTxtActualQty = new ArrayList<AmountField>();
	private ArrayList<TextRead> tbTxtOverQty = new ArrayList<TextRead>();

	ArrayList<Component> allComp = new ArrayList<Component>();

	private CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	boolean isUpdate=false,isFind=false;
	private AbsoluteLayout mainLayout;
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0");
	private TextField txtAdjustmentFind=new TextField();

	public AdjustmentEntry(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("Adjustment Entry :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		btnAction();
		focusEnter();
		btnIni(true);
		componentIni(true);
		ProductionStepLoad();
	}
	private void ProductionStepLoad() {
		cmbProductionStep.removeAllItems();
		Iterator iter=dbService("select 0,issueTo  from tbIssueToLabelPrintingInfo order by issueTo");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionStep.addItem(element[1]);
			cmbProductionStep.setItemCaption(element[1], element[1].toString());
		}
	}
	private void componentIni(boolean b)
	{

		cmbBatchNumber.setEnabled(!b);
		cmbProductionStep.setEnabled(!b);
		txtAdjustmentNo.setEnabled(!b);
		dAdjustmentDate.setEnabled(!b);
		table.setEnabled(!b);

	}

	private void cmbBatchNumberLoad() {
		cmbBatchNumber.removeAllItems();
		Iterator iter=dbService("select 0,batchNo from tbIssueToLabelPrintingInfo order by batchNo");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbBatchNumber.addItem(element[1]);
			cmbBatchNumber.setItemCaption(element[1], element[1].toString());
		}
	}

	private void tableDataLoad() {
		int a=0;
		Iterator iter=dbService("select semiFgId,semiFgName,unit,color,batchQty,productionQty," +
				"rejectionQty,TotalUsedQty from funcAdjustmentEntry('"+cmbBatchNumber.getValue()+"')");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tblblProductId.get(a).setValue(element[0]);
			tblblProductName.get(a).setValue(element[1]);
			tblblUnit.get(a).setValue(element[2]);
			tblblColor.get(a).setValue(element[3]);
			tbTxtBatchQty.get(a).setValue(df.format(element[4]));
			tbTxtProductionQty.get(a).setValue(df.format(element[5]));
			tbTxtRejectionQty.get(a).setValue(df.format(element[6]));
			tbTxtUsedQty.get(a).setValue(df.format(element[7]));
			
			if(a==tblblColor.size()-1){
				tableRowAdd(a+1);
			}
			a++;
		}
	}
	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
	}
	public void btnAction()
	{
		cmbProductionStep.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbProductionStep.getValue()!=null){
					cmbBatchNumberLoad();
				}
				else{
					cmbBatchNumber.removeAllItems();
				}
			}
		});
		cmbBatchNumber.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				tableClear();
				if(cmbBatchNumber.getValue()!=null){
					tableDataLoad();
				}
				
			}
		});
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
				isFind=true;
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
	private void findButtonEvent(){
		Window win = new AdjustmentEntryFind(sessionBean, txtAdjustmentFind);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtAdjustmentFind.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtAdjustmentFind.getValue().toString());
					//System.out.println("Issue No: "+txtAdjustmentFind.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}
	private void findInitialise(String adjustmentNo){
		Iterator iter=dbService("select a.productionStep,a.batchNo,a.AdjustmentNo,a.adjustmentDate,semiFgId,semiFgName, "+
				" color,unit,batchQty,totalProduction,totalRejection,totalUsed,actualQty,shortOverQty "+
				" from tbAdjustmentInfo a inner join tbAdjustmentDetails b on a.AdjustmentNo=b.adjustmentNo "+
				" where a.AdjustmentNo='"+adjustmentNo+"'");
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==0){
				cmbProductionStep.setValue(element[0]);
				cmbBatchNumber.setValue(element[1]);
				txtAdjustmentNo.setValue(element[2]);
				dAdjustmentDate.setValue(element[3]);
				
			}
			tblblProductId.get(a).setValue(element[4]);
			tblblProductName.get(a).setValue(element[5]);
			tblblUnit.get(a).setValue(element[7]);
			tblblColor.get(a).setValue(element[6]);
			tbTxtBatchQty.get(a).setValue(df.format(element[8]));
			tbTxtProductionQty.get(a).setValue(df.format(element[9]));
			tbTxtRejectionQty.get(a).setValue(df.format(element[10]));
			tbTxtUsedQty.get(a).setValue(df.format(element[11]));
			tbTxtActualQty.get(a).setValue(df.format(element[12]));
			tbTxtOverQty.get(a).setValue(df.format(element[13]));
			a++;
			if(a==tblblProductId.size()-1){
				tableRowAdd(a+1);
			}
		}
	}
	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete from tbAdjustmentInfo where adjustmentNo='"+txtAdjustmentNo.getValue()+ "'").executeUpdate();
			///System.out.println("delete tbLabelProductionInfo where ProductionNo='"+txtProductionNo.getValue()+ "' ");

			session.createSQLQuery("delete from tbAdjustmentDetails where adjustmentNo='"+txtAdjustmentNo.getValue()+ "'").executeUpdate();
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
		try{
			String sqlInfo="insert into tbAdjustmentInfo(productionStep,batchNo,AdjustmentNo,adjustmentDate,userIp,userName,entryTime) "+
					" values('"+cmbProductionStep.getValue()+"','"+cmbBatchNumber.getValue()+"','"+txtAdjustmentNo.getValue()+"'," +
					"'"+dateFormat.format(dAdjustmentDate.getValue())+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
			session.createSQLQuery(sqlInfo).executeUpdate();
			
			for(int a=0;a<tblblProductId.size();a++){
				if(!tblblProductId.get(a).getValue().toString().isEmpty()
				&&Double.parseDouble("0"+tbTxtActualQty.get(a).getValue().toString())>0){
					String sqlDetails="insert into tbAdjustmentDetails(adjustmentNo,semiFgId,semiFgName,color,unit,batchQty,actualQty," +
							"shortOverQty,totalProduction,totalRejection,totalUsed) "+ 
							" values('"+txtAdjustmentNo.getValue()+"','"+tblblProductId.get(a).getValue()+"'," +
							"'"+tblblProductName.get(a).getValue()+"','"+tblblColor.get(a).getValue()+"'," +
							"'"+tblblUnit.get(a).getValue()+"','"+tbTxtBatchQty.get(a).getValue()+"'," +
							"'"+tbTxtActualQty.get(a).getValue()+"','"+tbTxtOverQty.get(a).getValue()+"'," +
							"'"+tbTxtProductionQty.get(a).getValue()+"','"+tbTxtRejectionQty.get(a).getValue()+"'," +
							"'"+tbTxtUsedQty.get(a).getValue()+"')";
					
					session.createSQLQuery(sqlDetails).executeUpdate();
				}
			}
			String sqlPrc="exec prcAdjustmentApproveToIssue '"+cmbBatchNumber.getValue()+"'";
			session.createSQLQuery(sqlPrc).executeUpdate();
			
			String type="New";
			if(isUpdate){
				type="Update";
			}
			
			String sqlInfoUd="insert into tbUdAdjustmentInfo(productionStep,batchNo,AdjustmentNo,adjustmentDate,userIp,userName,entryTime,type) "+
					" values('"+cmbProductionStep.getValue()+"','"+cmbBatchNumber.getValue()+"','"+txtAdjustmentNo.getValue()+"'," +
					"'"+dateFormat.format(dAdjustmentDate.getValue())+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'"+type+"')";
			session.createSQLQuery(sqlInfoUd).executeUpdate();
			
			for(int a=0;a<tblblProductId.size();a++){
				if(!tblblProductId.get(a).getValue().toString().isEmpty()
				&&Double.parseDouble("0"+tbTxtActualQty.get(a).getValue().toString())>0){
					String sqlDetailsUd="insert into tbUdAdjustmentDetails(adjustmentNo,semiFgId,semiFgName,color,unit,batchQty,actualQty," +
							"shortOverQty,totalProduction,totalRejection,totalUsed,type) "+ 
							" values('"+txtAdjustmentNo.getValue()+"','"+tblblProductId.get(a).getValue()+"'," +
							"'"+tblblProductName.get(a).getValue()+"','"+tblblColor.get(a).getValue()+"'," +
							"'"+tblblUnit.get(a).getValue()+"','"+tbTxtBatchQty.get(a).getValue()+"'," +
							"'"+tbTxtActualQty.get(a).getValue()+"','"+tbTxtOverQty.get(a).getValue()+"'," +
							"'"+tbTxtProductionQty.get(a).getValue()+"','"+tbTxtRejectionQty.get(a).getValue()+"'," +
							"'"+tbTxtUsedQty.get(a).getValue()+"','"+type+"')";
					
					session.createSQLQuery(sqlDetailsUd).executeUpdate();
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
		if(cmbProductionStep.getValue()!=null){
			if(cmbBatchNumber.getValue()!=null){
				if(!tblblProductId.get(0).getValue().toString().isEmpty()&&
						Double.parseDouble("0"+tbTxtActualQty.get(0).getValue().toString())>0){
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
			showNotification("Please Provide Production Step",Notification.TYPE_WARNING_MESSAGE);
		}
		
		return false;
	}
	public void tableInitialise(){
		for(int i=0;i<10;i++){
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		try{

			tbLblSl.add(ar,new Label());
			tbLblSl.get(ar).setWidth("20px");
			tbLblSl.get(ar).setValue(ar + 1);	

			tblblProductId.add(ar, new Label());
			tblblProductId.get(ar).setImmediate(true);
			tblblProductId.get(ar).setWidth("100%");
			tblblProductId.get(ar).setHeight("-1px");

			tblblProductName.add(ar, new Label());
			tblblProductName.get(ar).setImmediate(true);
			tblblProductName.get(ar).setWidth("100%");
			tblblProductName.get(ar).setHeight("-1px");

			tblblColor.add(ar, new Label());
			tblblColor.get(ar).setImmediate(true);
			tblblColor.get(ar).setWidth("100%");
			tblblColor.get(ar).setHeight("-1px");


			tblblUnit.add(ar, new Label());
			tblblUnit.get(ar).setImmediate(true);
			tblblUnit.get(ar).setWidth("100%");
			tblblUnit.get(ar).setHeight("-1px");	

			tbTxtBatchQty.add(ar, new TextRead(1));
			tbTxtBatchQty.get(ar).setImmediate(true);
			tbTxtBatchQty.get(ar).setWidth("100%");
			tbTxtBatchQty.get(ar).setHeight("-1px");

			tbTxtProductionQty.add(ar, new TextRead(1));
			tbTxtProductionQty.get(ar).setImmediate(true);
			tbTxtProductionQty.get(ar).setWidth("100%");
			tbTxtProductionQty.get(ar).setHeight("-1px");

			tbTxtRejectionQty.add(ar, new TextRead(1));
			tbTxtRejectionQty.get(ar).setImmediate(true);
			tbTxtRejectionQty.get(ar).setWidth("100%");
			tbTxtRejectionQty.get(ar).setHeight("-1px");

			tbTxtUsedQty.add(ar, new TextRead(1));
			tbTxtUsedQty.get(ar).setImmediate(true);
			tbTxtUsedQty.get(ar).setWidth("100%");
			tbTxtUsedQty.get(ar).setHeight("-1px");

			tbTxtActualQty.add(ar, new AmountField());
			tbTxtActualQty.get(ar).setImmediate(true);
			tbTxtActualQty.get(ar).setWidth("100%");
			tbTxtActualQty.get(ar).setHeight("-1px");
			
			tbTxtActualQty.get(ar).addListener(new ValueChangeListener() {
				
				public void valueChange(ValueChangeEvent event) {
					
					double batchQty=Double.parseDouble("0"+tbTxtBatchQty.get(ar).getValue());
					double actualQty=Double.parseDouble("0"+tbTxtActualQty.get(ar).getValue());
					double totalUsedQty=Double.parseDouble("0"+tbTxtUsedQty.get(ar).getValue());
					double shortOver=batchQty-actualQty;
					if(actualQty<totalUsedQty){
						tbTxtActualQty.get(ar).setValue("");
						tbTxtOverQty.get(ar).setValue("");
						showNotification("Sorry!!","Used Quantity Greater Than Actual Qty",Notification.TYPE_WARNING_MESSAGE);
					}
					else{
						tbTxtOverQty.get(ar).setValue(df.format(shortOver));
					}
					
				}
			});

			tbTxtOverQty.add(ar, new TextRead(1));
			tbTxtOverQty.get(ar).setImmediate(true);
			tbTxtOverQty.get(ar).setWidth("100%");
			tbTxtOverQty.get(ar).setHeight("-1px");

			table.addItem(new Object[]{tbLblSl.get(ar),	tblblProductId.get(ar),tblblProductName.get(ar),tblblColor.get(ar),tblblUnit.get(ar),
					tbTxtBatchQty.get(ar),tbTxtProductionQty.get(ar),tbTxtRejectionQty.get(ar),tbTxtUsedQty.get(ar),tbTxtActualQty.get(ar),tbTxtOverQty.get(ar)},ar);

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
		mainLayout.setWidth("1070px");
		mainLayout.setHeight("485px");


		lblProductionStep = new Label("Production Step :");
		lblProductionStep.setImmediate(false);
		lblProductionStep.setWidth("-1px");
		lblProductionStep.setHeight("-1px");
		mainLayout.addComponent(lblProductionStep, "top:20.0px;left:20.0px;");

		cmbProductionStep =new ComboBox();
		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setWidth("200px");
		cmbProductionStep.setHeight("-1px");
		cmbProductionStep.setNullSelectionAllowed(false);
		cmbProductionStep.setNewItemsAllowed(false);
		cmbProductionStep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbProductionStep, "top:18.0px;left:120.0px;");

		lblBatchNumber = new Label("Batch No. :");
		lblBatchNumber.setImmediate(false);
		lblBatchNumber.setWidth("-1px");
		lblBatchNumber.setHeight("-1px");
		mainLayout.addComponent(lblBatchNumber, "top:46.0px;left:20.0px;");

		cmbBatchNumber =new ComboBox();
		cmbBatchNumber.setImmediate(true);
		cmbBatchNumber.setWidth("200px");
		cmbBatchNumber.setHeight("-1px");
		cmbBatchNumber.setNullSelectionAllowed(true);
		cmbBatchNumber.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbBatchNumber, "top:44.0px;left:120.0px;");
		cmbBatchNumber.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		txtAdjustmentNo=new TextRead(1);
		txtAdjustmentNo.setImmediate(true);
		txtAdjustmentNo.setWidth("100px");
		txtAdjustmentNo.setHeight("24px");
		mainLayout.addComponent(new Label("Adjustment No: "),"top:20.0px;left:340px;");
		mainLayout.addComponent(txtAdjustmentNo,"top:18.0px;left:450px;");


		dAdjustmentDate = new PopupDateField();
		dAdjustmentDate.setImmediate(true);
		dAdjustmentDate.setWidth("110px");
		dAdjustmentDate.setDateFormat("dd-MM-yyyy");
		dAdjustmentDate.setValue(new java.util.Date());
		dAdjustmentDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label("Adjustment Date: "),"top:46.0px;left:340px;");
		mainLayout.addComponent(dAdjustmentDate,"top:44.0px;left:450px;");


		table = new Table();
		table.setWidth("100%");
		table.setHeight("350px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Product Id",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Product Id",50);
		table.setColumnCollapsed("Product Id", true);

		table.addContainerProperty("Product Name",  Label.class , new  Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Product Name",300);

		table.addContainerProperty("Color",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Color",50);

		table.addContainerProperty("Unit",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Unit",35);

		table.addContainerProperty("Batch Qty",  TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Batch Qty",80);

		table.addContainerProperty("Total Proudction",  TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Total Proudction",90);

		table.addContainerProperty("Total Rejection",  TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Total Rejection",90);

		table.addContainerProperty("Total Used",  TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Batch Qty",80);

		table.addContainerProperty("Actual Qty",  AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Actual Qty",80);

		table.addContainerProperty("Short/Over Qty",  TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Short/Over Qty",85);

		mainLayout.addComponent(table, "top:80.0px;left:5.0px;");
		tableInitialise();
		mainLayout.addComponent(cButton, "top:440.0px;left:130.0px;");
		setStyleName("cwindow");
		return mainLayout;
	}

	private void focusEnter()
	{

		allComp.add(cmbProductionStep);
		allComp.add(cmbBatchNumber);

		for(int i=0;i<tbLblSl.size();i++)
		{
			allComp.add(tbTxtActualQty.get(i));
		}

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);
		allComp.add(cButton.btnFind);
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}
	public void txtClear()
	{
		cmbProductionStep.setValue(null);
		cmbBatchNumber.setValue(null);
		txtAdjustmentNo.setValue("");
		dAdjustmentDate.setValue(new java.util.Date());
		tableClear();
	}
	private void tableClear() {
		for(int i=0;i<tbLblSl.size();i++)
		{
			tblblProductName.get(i).setValue("");
			tblblProductId.get(i).setValue("");
			tblblColor.get(i).setValue("");
			tblblUnit.get(i).setValue("");
			tbTxtBatchQty.get(i).setValue("");
			tbTxtProductionQty.get(i).setValue("");
			tbTxtRejectionQty.get(i).setValue("");
			tbTxtUsedQty.get(i).setValue("");
			tbTxtActualQty.get(i).setValue("");
			tbTxtOverQty.get(i).setValue("");
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
	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		isFind=false;
		isUpdate=false;
		cmbProductionStep.focus();
		adjustmentNoLoad();
	}

	private void adjustmentNoLoad() {
		Iterator iter=dbService("select 0,isnull(max(cast(adjustmentNo as int)),0)+1 id from tbAdjustmentInfo");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtAdjustmentNo.setValue(element[1].toString());
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
		cButton.btnPreview.setEnabled(t);
	}
}
