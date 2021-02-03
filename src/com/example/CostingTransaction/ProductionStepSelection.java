package com.example.CostingTransaction;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ProductionStepSelection extends Window
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblHeadName;

	ComboBox cmbPartyName,cmbMasterProductName,cmbSubGroupName;
	CheckBox chkGroupAll,chkSubGroupAll;
	PopupDateField dCurDate;
	TextRead txtTransactionId;

	private ArrayList<Label>tbLblSl=new ArrayList<Label>();
	private ArrayList<Label>tblblSemiFgId=new ArrayList<Label>();
	private ArrayList<Label>tbLblSemiFgName=new ArrayList<Label>();
	private ArrayList<Label>tbLblFgId=new ArrayList<Label>();
	private ArrayList<Label>tbLblFgName=new ArrayList<Label>();
	private ArrayList<TextRead>tbLblWeight=new ArrayList<TextRead>();
	private ArrayList<Label>tbLblConsumptionStage=new ArrayList<Label>();
	private ArrayList<Label>tbLblQty=new ArrayList<Label>();
	//private ArrayList<CheckBox>tbChkSelect=new ArrayList<CheckBox>();
	Table table=new Table();
	
	private ArrayList<Label>tbLblSLStep=new ArrayList<Label>();
	private ArrayList<Label>tbLblProductionStepName=new ArrayList<Label>();
	private ArrayList<Label>tbLblProductStepId=new ArrayList<Label>();
	private ArrayList<CheckBox>tbChkSelectStep=new ArrayList<CheckBox>();
	private ArrayList<AmountField>tbTxtStepSl=new ArrayList<AmountField>();
	
	Table tableStep=new Table();

	private boolean isUpdate = false;
	private boolean isFind = false;
	SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0.00");
	DecimalFormat dfInteger=new DecimalFormat("#0.00");
	
	
	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtCategoryID = new TextField();
	ArrayList<Component> allComp = new ArrayList<Component>();
	//boolean isFind=

	public ProductionStepSelection(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("PRODUCTION STEP SELECTION :: "+this.sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		btnIni(true);
		componentIni(true);
		setEventAction();
		focusEnter();
		authenticationCheck();
		partyNameLoadData();
		//masterProductNameDataLoad();
		productionStepDataLoad();
	}

	private void productionStepDataLoad() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select productTypeId,productTypeName from tbProductionType where productTypeId in('PT-1','PT-2','PT-4')";
			List list=session.createSQLQuery(sql).list();

			int i=0;
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				tbLblProductStepId.get(i).setValue(element[0]);
				tbLblProductionStepName.get(i).setValue(element[1]);
				i++;
			}
			tbLblProductStepId.get(i).setValue("Assemble");
			tbLblProductionStepName.get(i).setValue("Assemble");
			i++;
			tbLblProductStepId.get(i).setValue("Dry Offset Printing");
			tbLblProductionStepName.get(i).setValue("Dry Offset Printing");
			i++;
			tbLblProductStepId.get(i).setValue("Screen Printing");
			tbLblProductionStepName.get(i).setValue("Screen Printing");
			i++;
			tbLblProductStepId.get(i).setValue("Heat Trasfer Label");
			tbLblProductionStepName.get(i).setValue("Heat Trasfer Label");
			i++;
			tbLblProductStepId.get(i).setValue("Manual Printing");
			tbLblProductionStepName.get(i).setValue("Manual Printing");
			i++;
			tbLblProductStepId.get(i).setValue("Labeling");
			tbLblProductionStepName.get(i).setValue("Labeling");
			i++;
			tbLblProductStepId.get(i).setValue("Lacqure");
			tbLblProductionStepName.get(i).setValue("Lacqure");
			i++;
			tbLblProductStepId.get(i).setValue("Cap Folding");
			tbLblProductionStepName.get(i).setValue("Cap Folding");
			i++;
			tbLblProductStepId.get(i).setValue("Stretch Blow Molding");
			tbLblProductionStepName.get(i).setValue("Stretch Blow Molding");
			i++;
		}
		catch(Exception exp)
		{
			showNotification( exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{button.btnSave.setVisible(false);}
		if(!sessionBean.isUpdateable())
		{button.btnEdit.setVisible(false);}
		if(!sessionBean.isDeleteable())
		{button.btnDelete.setVisible(false);}
	}

	private void focusEnter()
	{
		allComp.add(cmbPartyName);
		allComp.add(cmbMasterProductName);
		allComp.add(button.btnNew);
		allComp.add(button.btnSave);
		allComp.add(button.btnEdit);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnFind);
		allComp.add(button.btnExit);

		new FocusMoveByEnter(this,allComp);
	}
	private void masterProductNameDataLoad()
	{
		cmbMasterProductName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String partyId="%";
			if(cmbPartyName.getValue()!=null){
				partyId=cmbPartyName.getValue().toString();
			}
			String query = " select vProductId,vProductName from tbFinishedProductInfo " +
					"where vCategoryId='"+partyId+"' and vProductId not in  " +
							"(select FgId from tbProductionStepSelectionInfo) order by vProductName";
			if(isFind){
				query = " select vProductId,vProductName from tbFinishedProductInfo " +
						"where vCategoryId='"+partyId+"'";
			}
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbMasterProductName.addItem(element[0]);
				cmbMasterProductName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void partyNameLoadData()
	{
		cmbPartyName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select distinct vCategoryId,vCategoryName from tbFinishedProductInfo order by vCategoryName";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbPartyName.addItem(element[0]);
				cmbPartyName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("partyNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	
	private boolean tableValueCheck(){
		for(int a=0;a<tbChkSelectStep.size();a++){
			if(!tbLblProductStepId.get(a).getValue().toString().isEmpty()&&tbChkSelectStep.get(a).booleanValue()&&!tbTxtStepSl.get(a).getValue().toString().isEmpty()){
				return true;
			}
		}
		return false;
	}
	private boolean checkValidation(){
		if(cmbPartyName.getValue()!=null){
			if(cmbMasterProductName.getValue()!=null){
				if(tableValueCheck()){
					return true;
				}
				else{
					showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide Master Product Name",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Party Name",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private void tableClearIng(){
		for(int a=0;a<tbLblConsumptionStage.size();a++){
			tblblSemiFgId.get(a).setValue("");
			tbLblSemiFgName.get(a).setValue("");
			tbLblFgId.get(a).setValue("");
			tbLblFgName.get(a).setValue("");
			tbLblWeight.get(a).setValue("");
			tbLblConsumptionStage.get(a).setValue("");
		}
	}
	private void tableDataLoadIng(){
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
			String query = " select b.semiFgId,b.semiFgName,b.semiFgSubId,b.semiFgSubName,b.stdWeight, "+
					" b.consumptionStage,b.qty from tbFinishedProductInfo a left join tbFinishedProductDetailsNew b  "+
					" on a.vProductId=b.fgId  where a.vProductId like '"+cmbMasterProductName.getValue()+"'";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			int a=0;
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				tblblSemiFgId.get(a).setValue(element[0]);
				tbLblSemiFgName.get(a).setValue(element[1]);
				tbLblFgId.get(a).setValue(element[2]);
				tbLblFgName.get(a).setValue(element[3]);
				tbLblWeight.get(a).setValue(df.format(element[4]));
				tbLblConsumptionStage.get(a).setValue(element[5]);
				tbLblQty.get(a).setValue(dfInteger.format(element[6]));
				a++;
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	public void setEventAction()
	{
		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPartyName.getValue()!=null){
					masterProductNameDataLoad();
				}
				else{
					cmbMasterProductName.removeAllItems();
				}
			}
		});
		cmbMasterProductName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClearIng();
				if(cmbMasterProductName.getValue()!=null){
					tableDataLoadIng();
				}
			}
		});
		

		button.btnNew.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind = false;
				updateButtonEvent();
				cmbPartyName.focus();
			}
		});

		button.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(checkValidation()){
					saveButtonEvent();
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind=true;
				findButtonEvent();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				refreshButtonEvent();
			}
		});

		button.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void findButtonEvent() 
	{
		Window win = new ProductionStepSelectionFind(sessionBean, txtCategoryID);
		win.setStyleName("cwindow");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtCategoryID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtCategoryID.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtCategoryId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		isFind = true;
		try 
		{
			String sql = "select partyId,FgId,a.transactionNo,date,b.stepId,b.stepSl from tbProductionStepSelectionInfo a "+
					" inner join tbProductionStepSelectionDetails b on a.transactionNo=b.transactionNo where a.transactionNo='"+txtCategoryId+"'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			int a=0;
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				if(a==0){
					cmbPartyName.setValue(element[0]);
					cmbMasterProductName.setValue(element[1]);
					dCurDate.setValue(element[3]);
					txtTransactionId.setValue(element[2]);
				}
				for(int x=0;x<tbLblProductionStepName.size();x++){
					if(tbLblProductStepId.get(x).getValue().toString().equalsIgnoreCase(element[4].toString())){
						tbChkSelectStep.get(x).setValue(true);
						tbTxtStepSl.get(x).setValue(element[5]);
					}
				}
				a++;
			}
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void refreshButtonEvent()
	{
		isFind = false;
		isUpdate = false;
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void saveButtonEvent() 
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(deleteData())
						{
							insertData();
							txtClear();
							componentIni(true);
							btnIni(true);
							button.btnNew.focus();
							isUpdate = false;
							
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
						btnIni(true);							
						componentIni(true);
						txtClear();
						button.btnNew.focus();
						
					}
				}
			});		
		}
	}

	private boolean deleteData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String sqlInfo = "delete from tbProductionStepSelectionInfo where transactionNo='"+txtTransactionId.getValue()+"'";
			String sqlDetails="delete from tbProductionStepSelectionDetails where transactionNo='"+txtTransactionId.getValue()+"'";
			session.createSQLQuery(sqlInfo).executeUpdate();
			session.createSQLQuery(sqlDetails).executeUpdate();
			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{session.close();}
	}
	
	private void getTransactionNo(){
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select isnull(MAX(transactionNo),0)+1 from tbProductionStepSelectionInfo";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				txtTransactionId.setValue(iter.next());
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			if(!isUpdate){
				getTransactionNo();
			}
			String groupIdInfo="All",groupNameInfo="All",subGroupIdInfo="All",subGroupNameInfo="All";
			
			String insertQuery = "insert into tbProductionStepSelectionInfo(transactionNo,date,partyId," +
					"partyName,FgId,fgName,userIp,userName,entryTime) "+
					" values('"+txtTransactionId.getValue()+"','"+dFormat.format(dCurDate.getValue())+"'," +
					"'"+cmbPartyName.getValue()+"','"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"'," +
					"'"+cmbMasterProductName.getValue()+"','"+cmbMasterProductName.getItemCaption(cmbMasterProductName.getValue())+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
			session.createSQLQuery(insertQuery).executeUpdate();
			
			for(int a=0;a<tbLblProductionStepName.size();a++){
				if(tbChkSelectStep.get(a).booleanValue()&&!tbLblProductStepId.get(a).getValue().toString().isEmpty()&&!tbTxtStepSl.get(a).getValue().toString().isEmpty()){
					
					String insertQueryDetails="insert into tbProductionStepSelectionDetails(transactionNo,stepId," +
							"stepName,stepSl)values('"+txtTransactionId.getValue()+"','"+tbLblProductStepId.get(a).getValue()+"'," +
									"'"+tbLblProductionStepName.get(a).getValue()+"','"+tbTxtStepSl.get(a).getValue()+"')";
					
					session.createSQLQuery(insertQueryDetails).executeUpdate();
				}
			}
			tx.commit();
			
			if(isUpdate){
				showNotification("All Information update successfully.");
			}
			else{
				showNotification("All Information Save successfully.");
			}
			
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void updateButtonEvent()
	{
		if(cmbPartyName.getValue()!=null)
		{
			isUpdate = true;
			isFind = false;
			btnIni(false);
			componentIni(false);
			
		}
		else
		{
			showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void newButtonEvent()
	{
		isFind = false;
		componentIni(false);
		btnIni(false);
		txtClear();
		//txtHeadId.setValue(headId());
		//txtHeadName.focus();
	}

	

	public void txtClear()
	{
		cmbPartyName.setValue(null);
		cmbMasterProductName.setValue(null);
		//cmbSubGroupName.setValue(null);
		dCurDate.setValue(new java.util.Date());
		txtTransactionId.setValue("");
		//chkGroupAll.setValue(false);
		//chkSubGroupAll.setValue(false);
		tableClear();
	}

	private void tableClear() {
		for(int a=0;a<tblblSemiFgId.size();a++){
			tblblSemiFgId.get(a).setValue("");
			tbLblSemiFgName.get(a).setValue("");
			tbLblFgId.get(a).setValue("");
			tbLblFgName.get(a).setValue("");
			tbLblWeight.get(a).setValue("");
			tbLblQty.get(a).setValue("");
			tbLblConsumptionStage.get(a).setValue("");
		}
		for(int a=0;a<tbLblProductionStepName.size();a++){
			tbChkSelectStep.get(a).setValue(false);
			tbTxtStepSl.get(a).setValue("");
		}
	}

	private void componentIni(boolean b) 
	{	
		cmbPartyName.setEnabled(!b);
		cmbMasterProductName.setEnabled(!b);
		
		dCurDate.setEnabled(!b);
		txtTransactionId.setEnabled(!b);
		table.setEnabled(!b);
		tableStep.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnFind.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
	}
	public void tableRowAdd(final int ar)
	{
		tbLblSl.add(ar, new Label(""));
		tbLblSl.get(ar).setWidth("100%");
		tbLblSl.get(ar).setImmediate(true);
		tbLblSl.get(ar).setHeight("23px");
		tbLblSl.get(ar).setValue(ar+1);

		tblblSemiFgId.add(ar, new Label(""));
		tblblSemiFgId.get(ar).setWidth("100%");
		tblblSemiFgId.get(ar).setImmediate(true);
		tblblSemiFgId.get(ar).setHeight("23px");


		tbLblSemiFgName.add(ar, new Label(""));
		tbLblSemiFgName.get(ar).setWidth("100%");
		tbLblSemiFgName.get(ar).setImmediate(true);
		tbLblSemiFgName.get(ar).setHeight("23px");

		tbLblFgId.add(ar, new Label(""));
		tbLblFgId.get(ar).setWidth("100%");
		tbLblFgId.get(ar).setImmediate(true);
		tbLblFgId.get(ar).setHeight("23px");

		tbLblFgName.add(ar, new Label(""));
		tbLblFgName.get(ar).setWidth("100%");
		tbLblFgName.get(ar).setImmediate(true);
		tbLblFgName.get(ar).setHeight("23px");

		tbLblConsumptionStage.add(ar, new Label(""));
		tbLblConsumptionStage.get(ar).setWidth("100%");
		tbLblConsumptionStage.get(ar).setImmediate(true);
		tbLblConsumptionStage.get(ar).setHeight("23px");

		tbLblWeight.add(ar, new TextRead(1));
		tbLblWeight.get(ar).setWidth("100%");
		tbLblWeight.get(ar).setImmediate(true);
		tbLblWeight.get(ar).setHeight("23px");
		
		tbLblQty.add(ar, new TextRead(1));
		tbLblQty.get(ar).setWidth("100%");
		tbLblQty.get(ar).setImmediate(true);
		tbLblQty.get(ar).setHeight("23px");

		table.addItem(new Object[]{tbLblSl.get(ar),tblblSemiFgId.get(ar),tbLblSemiFgName.get(ar),tbLblFgId.get(ar),
				tbLblFgName.get(ar),tbLblWeight.get(ar),tbLblQty.get(ar),tbLblConsumptionStage.get(ar)},ar);
	}
	private void tableInitialze() {
		for(int a=0;a<4;a++){
			tableRowAdd(a);
		}

	}
	public void tableRowAddStep(final int ar)
	{
		tbLblSLStep.add(ar, new Label(""));
		tbLblSLStep.get(ar).setWidth("100%");
		tbLblSLStep.get(ar).setImmediate(true);
		tbLblSLStep.get(ar).setHeight("23px");
		tbLblSLStep.get(ar).setValue(ar+1);

		tbChkSelectStep.add(ar,new CheckBox());
		tbChkSelectStep.get(ar).setWidth("100%");
		tbChkSelectStep.get(ar).setImmediate(true);
		tbChkSelectStep.get(ar).setHeight("23px");

		tbLblProductStepId.add(ar, new Label(""));
		tbLblProductStepId.get(ar).setWidth("100%");
		tbLblProductStepId.get(ar).setImmediate(true);
		tbLblProductStepId.get(ar).setHeight("23px");


		tbLblProductionStepName.add(ar, new Label(""));
		tbLblProductionStepName.get(ar).setWidth("100%");
		tbLblProductionStepName.get(ar).setImmediate(true);
		tbLblProductionStepName.get(ar).setHeight("23px");

		tbTxtStepSl.add(ar, new AmountField());
		tbTxtStepSl.get(ar).setWidth("100%");
		tbTxtStepSl.get(ar).setImmediate(true);
		tbTxtStepSl.get(ar).setHeight("23px");
		
		tableStep.addItem(new Object[]{tbLblSLStep.get(ar),tbChkSelectStep.get(ar),tbLblProductStepId.get(ar),
				tbLblProductionStepName.get(ar),tbTxtStepSl.get(ar)},ar);
	}
	private void tableInitializeStep() {
		for(int a=0;a<15;a++){
			tableRowAddStep(a);
		}

	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("760px");
		setHeight("690px");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("300px");
		cmbPartyName.setHeight("24px");	
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Party Name: "), "top:15.0px;left:20.0px;");
		mainLayout.addComponent(cmbPartyName, "top:13.0px;left:150.0px;");

		cmbMasterProductName = new ComboBox();
		cmbMasterProductName.setImmediate(true);
		cmbMasterProductName.setWidth("300px");
		cmbMasterProductName.setHeight("24px");	
		cmbMasterProductName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Master Product Name: "), "top:45.0px;left:20.0px;");
		mainLayout.addComponent(cmbMasterProductName, "top:43.0px;left:150.0px;");


		txtTransactionId=new TextRead();
		txtTransactionId.setImmediate(true);
		txtTransactionId.setWidth("105px");
		txtTransactionId.setHeight("24px");
		mainLayout.addComponent(new Label("Transaction No : "), "top:15.0px;left:460.0px;");
		mainLayout.addComponent( txtTransactionId, "top:13.0px;left:570.0px;");

		dCurDate=new PopupDateField();
		dCurDate.setImmediate(true);
		dCurDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dCurDate.setValue(new java.util.Date());
		dCurDate.setDateFormat("dd-MM-yyyy");
		dCurDate.setWidth("107px");
		dCurDate.setHeight("-1px");
		dCurDate.setInvalidAllowed(false);
		mainLayout.addComponent(new Label("Date : "), "top:45.0px;left:460.0px;");
		mainLayout.addComponent( dCurDate, "top:43.0px;left:570.0px;");

		table.setWidth("100%");
		table.setHeight("130px");
		table.setImmediate(true);
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);	

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#",20);

		table.addContainerProperty("Semi Fg Id", Label.class, new Label());
		table.setColumnWidth("Semi Fg Id",80);
		table.setColumnCollapsed("Semi Fg Id", true);

		table.addContainerProperty("SemiFg Name", Label.class, new Label());
		table.setColumnWidth("SemiFg Name",220);

		table.addContainerProperty("FG Id", Label.class, new Label());
		table.setColumnWidth("FG Id",80);
		table.setColumnCollapsed("FG Id", true);

		table.addContainerProperty("FG Name", Label.class, new Label());
		table.setColumnWidth("FG Name",220);
		
		table.addContainerProperty("STD. Weight", TextRead.class, new TextRead(1));
		table.setColumnWidth("STD. Weight",60);
		
		table.addContainerProperty("Qty", TextRead.class, new TextRead(1));
		table.setColumnWidth("Qty",30);
		

		table.addContainerProperty("Consumption Stage", Label.class, new Label());
		table.setColumnWidth("Consumption Stage",110);
		
		tableInitialze();
		
		tableStep.setWidth("450px");
		tableStep.setHeight("385px");
		tableStep.setImmediate(true);
		tableStep.setFooterVisible(true);
		tableStep.setColumnCollapsingAllowed(true);	

		tableStep.addContainerProperty("SL#", Label.class, new Label());
		tableStep.setColumnWidth("SL#",25);

		tableStep.addContainerProperty("Check", CheckBox.class, new CheckBox());
		tableStep.setColumnWidth("Check",40);

		tableStep.addContainerProperty("Proudction Step Id", Label.class, new Label());
		tableStep.setColumnWidth("Proudction Step Id",80);
		tableStep.setColumnCollapsed("Proudction Step Id", true);

		tableStep.addContainerProperty("Production Step Name", Label.class, new Label());
		tableStep.setColumnWidth("Production Step Name",260);
		
		tableStep.addContainerProperty("STEP SL", AmountField.class, new AmountField());
		tableStep.setColumnWidth("STEP SL",50);
		
		tableInitializeStep();

		mainLayout.addComponent(table, "top:75.0px;left:0.0px;");
		
		mainLayout.addComponent(tableStep, "top:210.0px;left:140.0px;");
		mainLayout.addComponent(button, "top:605.0px;left:90.0px;");

		return mainLayout;
	}

}