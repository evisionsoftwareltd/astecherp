package com.example.CostingTransaction;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;

@SuppressWarnings("serial")
public class ConversionCostEntry extends Window
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblHeadName;

	ComboBox cmbPartyName,cmbMasterProductName,cmbProductionStep,cmbSemiFgName,cmbFgName;
	PopupDateField dCurDate;
	TextRead txtTransactionId;

	private ArrayList<Label> lblsa = new ArrayList<Label>();
	private ArrayList<ComboBox>  tblCmbConCost= new ArrayList<ComboBox>();
	private ArrayList<AmountField>  tbTxtConAmount = new ArrayList<AmountField>();
	private ArrayList<TextField>  tbtxtConRemarks = new ArrayList<TextField>();
	Table table=new Table();

	private boolean isUpdate = false;
	private boolean isFind = true;
	SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0.00");
	DecimalFormat dfInteger=new DecimalFormat("#0.00");


	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtCategoryID = new TextField();
	ArrayList<Component> allComp = new ArrayList<Component>();

	TextField txtTotalAmount=new TextField();

	private List<String> list=Arrays.asList("SEMI FG","FG","MASTER PRODUCT");
	OptionGroup optionGroup=new OptionGroup();
	Label lblPartyName=new Label("Party Name: ");
	Label lblSemiFg=new Label("Semi FG Name: ");
	Label lblMasterProduct=new Label("Master Product Name: ");
	Label lblFg=new Label("FG Name: ");
	Label lblProductionStep=new Label("Production Step: ");
	
	public ConversionCostEntry(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("CONVERSION COST ENTRY :: "+this.sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		btnIni(true);
		componentIni(true);
		setEventAction();
		focusEnter();
		authenticationCheck();
		partyNameLoadData();

	}
	private void productionStepDataLoadSemiFg() {
		cmbProductionStep.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select productTypeId,productTypeName from tbProductionType where productTypeId in('PT-1','PT-2','PT-4')";
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[]) iter.next();
				cmbProductionStep.addItem(element[0]);
				cmbProductionStep.setItemCaption(element[0],element[1].toString());
			}	
		}
		catch(Exception exp)
		{
			showNotification( exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void productionStepDataLoadFG() {
		cmbProductionStep.removeAllItems();
		cmbProductionStep.addItem("Dry Offset Printing");
		cmbProductionStep.addItem("Screen Printing");
		cmbProductionStep.addItem("Heat Trasfer Label");
		cmbProductionStep.addItem("Manual Printing");
		cmbProductionStep.addItem("Labeling");
		cmbProductionStep.addItem("Lacqure");
		cmbProductionStep.addItem("Cap Folding");
		cmbProductionStep.addItem("Stretch Blow Molding");
		
	}
	private void productionStepDataLoadMasterProduct() {
		cmbProductionStep.removeAllItems();
		cmbProductionStep.addItem("Assemble");
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
		allComp.add(cmbFgName);
		allComp.add(cmbSemiFgName);
		allComp.add(cmbProductionStep);

		for(int x=0;x<tblCmbConCost.size();x++){
			allComp.add(tblCmbConCost.get(x));
			allComp.add(tbTxtConAmount.get(x));
			allComp.add(tbtxtConRemarks.get(x));
		}

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
					"where vCategoryId='"+partyId+"' order by vProductName";
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
	private void cmbSemiFgDataLoad()
	{
		cmbSemiFgName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select semiFgCode,semiFgName+' # '+color semiFgName from tbSemiFgInfo where status='Active' order by semiFgName";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbSemiFgName.addItem(element[0]);
				cmbSemiFgName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("partyNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void tableinitialise()
	{
		for(int i=0; i<=9; i++)
		{
			tableRowAdd(i);
		}
	}
	private void cmbConversionDataLoad(int ar){
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select headId,headName from tbConversionCostHeadinfo order by headName";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				tblCmbConCost.get(ar).addItem(element[0]);
				tblCmbConCost.get(ar).setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("partyNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private boolean doubleEntryCheck(String caption,int row){

		for(int i=0;i<tblCmbConCost.size();i++){

			if(i!=row && caption.equals(tblCmbConCost.get(i).getItemCaption(tblCmbConCost.get(i).getValue()))){

				return false;
			}
		}
		return true;
	}
	private void calcTotalAmount(){
		double totalAmount=0.0;
		for(int x=0;x<tblCmbConCost.size();x++){
			if(tblCmbConCost.get(x).getValue()!=null){
				double amount=Double.parseDouble(tbTxtConAmount.get(x).getValue().toString().isEmpty()?"0.0":tbTxtConAmount.get(x).getValue().toString());
				totalAmount=totalAmount+amount;
			}
		}
		txtTotalAmount.setValue(totalAmount);
		table.setColumnFooter("Amount/Pcs", "Total : "+df.format(totalAmount));
	}
	private void tableRowAdd(final int ar)
	{
		lblsa.add(ar,new Label());
		lblsa.get(ar).setWidth("100%");
		lblsa.get(ar).setValue(ar+1);

		tblCmbConCost.add(ar, new ComboBox());
		tblCmbConCost.get(ar).setWidth("100%");
		tblCmbConCost.get(ar).setImmediate(true);
		cmbConversionDataLoad(ar);

		tblCmbConCost.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tblCmbConCost.get(ar).getValue()!=null)
				{

					if(doubleEntryCheck(tblCmbConCost.get(ar).getItemCaption(tblCmbConCost.get(ar).getValue()),ar))
					{
						tbTxtConAmount.get(ar).focus();
						if(ar==tblCmbConCost.size()-1)
						{
							tableRowAdd(ar+1);	
						}
					}
					else
					{
						tblCmbConCost.get(ar).setValue(null);
						showNotification("Double Entry",Notification.TYPE_WARNING_MESSAGE);
						tblCmbConCost.get(ar).focus();
					}

				}
			}
		});

		tbTxtConAmount.add(ar,new AmountField());
		tbTxtConAmount.get(ar).setWidth("100%");
		tbTxtConAmount.get(ar).setImmediate(true);

		tbTxtConAmount.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				calcTotalAmount();
			}
		});


		tbtxtConRemarks.add(ar,new TextField());
		tbtxtConRemarks.get(ar).setWidth("100%");
		tbtxtConRemarks.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblsa.get(ar),tblCmbConCost.get(ar),tbTxtConAmount.get(ar),tbtxtConRemarks.get(ar)},ar);
	}
	private boolean tableValueCheck(){
		for(int a=0;a<tblCmbConCost.size();a++){
			if(tblCmbConCost.get(a).getValue()!=null&&!tbTxtConAmount.get(a).getValue().toString().isEmpty()){
				return true;
			}
		}
		return false;
	}
	private boolean infoDataCheck(){
		if(optionGroup.getValue().equals("SEMI FG")){
			if(cmbSemiFgName.getValue()==null){
				return false;
			}
		}
		if(optionGroup.getValue().equals("FG")){
			if(cmbPartyName.getValue()==null||cmbMasterProductName.getValue()==null||cmbFgName.getValue()==null){
				return false;
			}
		}
		if(optionGroup.getValue().equals("MASTER PRODUCT")){
			if(cmbPartyName.getValue()==null||cmbMasterProductName.getValue()==null){
				return false;
			}
		}
		return true;
	}
	private boolean checkValidation(){
		System.out.println();
		if(optionGroup.getValue().equals("SEMI FG")||optionGroup.getValue().equals("FG")||optionGroup.getValue().equals("MASTER PRODUCT")){
			if(infoDataCheck()){
				if(cmbProductionStep.getValue()!=null){
					if(tableValueCheck()){
						return true;
					}
					else{
						showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Please Provide Production Step",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Product Type",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	/*private void tableClearIng(){
		for(int a=0;a<tbLblConsumptionStage.size();a++){
			tblblSemiFgId.get(a).setValue("");
			tbLblSemiFgName.get(a).setValue("");
			tbLblFgId.get(a).setValue("");
			tbLblFgName.get(a).setValue("");
			tbLblWeight.get(a).setValue("");
			tbLblConsumptionStage.get(a).setValue("");
		}
	}*/
	/*private void tableDataLoadIng(){
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
	}*/
	private void cmbFgDataLoad(){
		cmbFgName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select b.semiFgSubId,b.semiFgSubName from tbFinishedProductInfo a inner join tbFinishedProductDetailsNew b   "+
					" on a.vProductId=b.fgId  where a.vProductId like '"+cmbMasterProductName.getValue()+"' and b.semiFgSubId not like ''";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbFgName.addItem(element[0]);
				cmbFgName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("partyNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
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
		cmbMasterProductName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbMasterProductName.getValue()!=null){
					cmbFgDataLoad();
				}
				else{
					cmbFgName.removeAllItems();
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
		optionGroup.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(optionGroup.getValue().equals("SEMI FG")){
					cmpTrueFalse();
					lblSemiFg.setVisible(true);
					lblProductionStep.setVisible(true);
					cmbSemiFgName.setVisible(true);
					cmbProductionStep.setVisible(true);
					productionStepDataLoadSemiFg();
					cmbSemiFgDataLoad();
				}
				else if(optionGroup.getValue().equals("FG")){
					cmpTrueFalse();
					lblPartyName.setVisible(true);
					lblMasterProduct.setVisible(true);
					lblProductionStep.setVisible(true);
					cmbPartyName.setVisible(true);
					cmbMasterProductName.setVisible(true);
					cmbProductionStep.setVisible(true);
					lblFg.setVisible(true);
					cmbFgName.setVisible(true);
					productionStepDataLoadFG();
				}
				else if(optionGroup.getValue().equals("MASTER PRODUCT")){
					cmpTrueFalse();
					lblPartyName.setVisible(true);
					lblMasterProduct.setVisible(true);
					lblProductionStep.setVisible(true);
					cmbPartyName.setVisible(true);
					cmbMasterProductName.setVisible(true);
					cmbProductionStep.setVisible(true);
					productionStepDataLoadMasterProduct();
				}
				else{
					cmpTrueFalse();
				}
			}
		});
	}

	private void findButtonEvent() 
	{
		Window win = new ConversionCostEntryFind(sessionBean, txtCategoryID);
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
			String sql = "select a.transactionNo,a.Date,a.productType,a.partyId,a.masterProductId," +
					"a.FgId,a.SemiFgId,a.stepId,b.headId, "+
					"b.amount,b.remarks from tbConversionCostInfo a  "+
					" inner join tbConversionCostDetails b on a.transactionNo=b.transactionNo "+
					" where a.transactionNo='"+txtCategoryId+"'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			int a=0;
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				if(a==0){
					txtTransactionId.setValue(element[0]);
					dCurDate.setValue(element[1]);
					optionGroup.setValue(element[2]);
					cmbPartyName.setValue(element[3]);
					cmbMasterProductName.setValue(element[4]);
					cmbFgName.setValue(element[5]);
					cmbSemiFgName.setValue(element[6]);
					cmbProductionStep.setValue(element[7]);
				}
				tblCmbConCost.get(a).setValue(element[8]);
				tbTxtConAmount.get(a).setValue(element[9]);
				tbtxtConRemarks.get(a).setValue(element[10]);
				if(a==tblCmbConCost.size()-1){
					tableRowAdd(a+1);
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
			String sqlInfo = "delete from tbConversionCostInfo where transactionNo='"+txtTransactionId.getValue()+"'";
			String sqlDetails="delete from tbConversionCostDetails where transactionNo='"+txtTransactionId.getValue()+"'";
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
			String query = "select isnull(MAX(transactionNo),0)+1 from tbConversionCostInfo";
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
			String type="Update";
			if(!isUpdate){
				getTransactionNo();
				type="New";
			}
			
			
			String partyId="",partyName="",masterProductId="",masterProductName="",FgId="",FgName="",semiFgId="",semiFgName="";
			if(cmbPartyName.getValue()!=null){
				partyId=cmbPartyName.getValue().toString();
				partyName=cmbPartyName.getItemCaption(cmbPartyName.getValue());
			}
			if(cmbMasterProductName.getValue()!=null){
				masterProductId=cmbMasterProductName.getValue().toString();
				masterProductName=cmbMasterProductName.getItemCaption(cmbMasterProductName.getValue());
			}
			if(cmbFgName.getValue()!=null){
				FgId=cmbFgName.getValue().toString();
				FgName=cmbFgName.getItemCaption(cmbFgName.getValue());
			}
			if(cmbSemiFgName.getValue()!=null){
				semiFgId=cmbSemiFgName.getValue().toString();
				semiFgName=cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue());
			}

			String insertQuery = "insert into tbConversionCostInfo(transactionNo,Date,ProductType,partyId,partyName,masterProductId, "+
					" masterProductName,FgId,FgName,semiFgId,semiFgName,stepId,stepName,totalAmount,userIp,userName,entryTime)values "+
					" ('"+txtTransactionId.getValue()+"','"+dFormat.format(dCurDate.getValue())+"','"+optionGroup.getValue()+"'," +
					"'"+partyId+"','"+partyName+"'," +
					"'"+masterProductId+"','"+masterProductName+"','"+FgId+"','"+FgName+"','"+semiFgId+"','"+semiFgName+"'," +
					"'"+cmbProductionStep.getValue()+"','"+cmbProductionStep.getItemCaption(cmbProductionStep.getValue())+"'," +
					"'"+txtTotalAmount.getValue()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
			
			String UdinsertQuery ="insert into tbUdConversionCostInfo(transactionNo,Date,ProductType,partyId,partyName,masterProductId, "+
					" masterProductName,FgId,FgName,semiFgId,semiFgName,stepId,stepName,totalAmount,userIp,userName,entryTime,type)values "+
					" ('"+txtTransactionId.getValue()+"','"+dFormat.format(dCurDate.getValue())+"','"+optionGroup.getValue()+"'," +
					"'"+partyId+"','"+partyName+"'," +
					"'"+masterProductId+"','"+masterProductName+"','"+FgId+"','"+FgName+"','"+semiFgId+"','"+semiFgName+"'," +
					"'"+cmbProductionStep.getValue()+"','"+cmbProductionStep.getItemCaption(cmbProductionStep.getValue())+"'," +
					"'"+txtTotalAmount.getValue()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'"+type+"')";
			
			session.createSQLQuery(insertQuery).executeUpdate();
			session.createSQLQuery(UdinsertQuery).executeUpdate();

			for(int a=0;a<tblCmbConCost.size();a++){
				if(tblCmbConCost.get(a).getValue()!=null&&!tbTxtConAmount.get(a).getValue().toString().isEmpty()){

					String insertQueryDetails="insert into tbConversionCostDetails(transactionNo," +
							" headId,headName,amount,remarks) "+
							" values('"+txtTransactionId.getValue()+"'," +
							"'"+tblCmbConCost.get(a).getValue()+"'," +
							"'"+tblCmbConCost.get(a).getItemCaption(tblCmbConCost.get(a).getValue())+"'," +
							"'"+tbTxtConAmount.get(a).getValue()+"','"+tbtxtConRemarks.get(a).getValue()+"')";

					session.createSQLQuery(insertQueryDetails).executeUpdate();
					
					String UdinsertQueryDetails="insert into tbUdConversionCostDetails(transactionNo," +
							" headId,headName,amount,remarks,type) "+
							" values('"+txtTransactionId.getValue()+"'," +
							"'"+tblCmbConCost.get(a).getValue()+"'," +
							"'"+tblCmbConCost.get(a).getItemCaption(tblCmbConCost.get(a).getValue())+"'," +
							"'"+tbTxtConAmount.get(a).getValue()+"','"+tbtxtConRemarks.get(a).getValue()+"','"+type+"')";
					
					session.createSQLQuery(UdinsertQueryDetails).executeUpdate();
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
		if(cmbProductionStep.getValue()!=null)
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
		optionGroup.select("");
		cmbPartyName.setValue(null);
		cmbMasterProductName.setValue(null);
		cmbProductionStep.setValue(null);
		cmbFgName.setValue(null);
		cmbSemiFgName.setValue(null);

		dCurDate.setValue(new java.util.Date());
		txtTransactionId.setValue("");
		//chkGroupAll.setValue(false);
		//chkSubGroupAll.setValue(false);
		tableClear();
	}

	private void tableClear() {
		for(int a=0;a<tblCmbConCost.size();a++){
			tblCmbConCost.get(a).setValue(null);
			tbTxtConAmount.get(a).setValue("");
			tbtxtConRemarks.get(a).setValue("");
		}
		
	}

	private void componentIni(boolean b) 
	{	
		cmbPartyName.setEnabled(!b);
		cmbMasterProductName.setEnabled(!b);
		cmbProductionStep.setEnabled(!b);
		cmbSemiFgName.setEnabled(!b);
		cmbFgName.setEnabled(!b);

		dCurDate.setEnabled(!b);
		txtTransactionId.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnFind.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
	}
	private void cmpTrueFalse(){
		lblPartyName.setVisible(false);
		cmbPartyName.setVisible(false);
		lblSemiFg.setVisible(false);
		cmbSemiFgName.setVisible(false);
		lblMasterProduct.setVisible(false);
		cmbMasterProductName.setVisible(false);
		lblFg.setVisible(false);
		cmbFgName.setVisible(false);
		lblProductionStep.setVisible(false);
		cmbProductionStep.setVisible(false);
	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("760px");
		setHeight("570px");
		
		optionGroup=new OptionGroup("", list);
		optionGroup.setImmediate(true);
		optionGroup.setStyleName("horizontal");
		mainLayout.addComponent(new Label("Product Type: "), "top:7.0px;left:20.0px;");
		mainLayout.addComponent(optionGroup, "top:5.0px;left:145.0px;");

		
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("300px");
		cmbPartyName.setHeight("24px");	
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblPartyName, "top:30.0px;left:20.0px;");
		mainLayout.addComponent(cmbPartyName, "top:28.0px;left:150.0px;");
				
		cmbSemiFgName = new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("300px");
		cmbSemiFgName.setHeight("24px");	
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblSemiFg, "top:60.0px;left:20.0px;");
		mainLayout.addComponent(cmbSemiFgName, "top:58.0px;left:150.0px;");
		
		cmbMasterProductName = new ComboBox();
		cmbMasterProductName.setImmediate(true);
		cmbMasterProductName.setWidth("300px");
		cmbMasterProductName.setHeight("24px");	
		cmbMasterProductName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblMasterProduct, "top:60.0px;left:20.0px;");
		mainLayout.addComponent(cmbMasterProductName, "top:58.0px;left:150.0px;");

		cmbFgName = new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setWidth("300px");
		cmbFgName.setHeight("24px");	
		cmbFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblFg, "top:90.0px;left:20.0px;");
		mainLayout.addComponent(cmbFgName, "top:88.0px;left:150.0px;");
		
		cmbProductionStep = new ComboBox();
		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setWidth("300px");
		cmbProductionStep.setHeight("24px");	
		cmbProductionStep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblProductionStep, "top:120.0px;left:20.0px;");
		mainLayout.addComponent(cmbProductionStep, "top:118.0px;left:150.0px;");


		txtTransactionId=new TextRead();
		txtTransactionId.setImmediate(true);
		txtTransactionId.setWidth("105px");
		txtTransactionId.setHeight("24px");
		mainLayout.addComponent(new Label("Transaction No : "), "top:30.0px;left:460.0px;");
		mainLayout.addComponent( txtTransactionId, "top:28.0px;left:570.0px;");

		dCurDate=new PopupDateField();
		dCurDate.setImmediate(true);
		dCurDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dCurDate.setValue(new java.util.Date());
		dCurDate.setDateFormat("dd-MM-yyyy");
		dCurDate.setWidth("107px");
		dCurDate.setHeight("-1px");
		dCurDate.setInvalidAllowed(false);
		mainLayout.addComponent(new Label("Date : "), "top:60.0px;left:460.0px;");
		mainLayout.addComponent( dCurDate, "top:58.0px;left:570.0px;");

		Label lblRMCost= new Label("<font color='#FF0000'><b><Strong>A.<Strong></b></font>  <font color='##0000FF'><b><Strong>Conversion cost<Strong></b></font>");
		lblRMCost.setImmediate(false);
		lblRMCost.setWidth("-1px");
		lblRMCost.setHeight("-1px");
		lblRMCost.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblRMCost, "top:140.0px;left:20.0px;");

		table.setWidth("98%");
		table.setHeight("270px");
		table.setFooterVisible(true);
		table.setColumnFooter("Amount/Pcs", "Total : "+0);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Over Head Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Over Head Name",350);

		table.addContainerProperty("Amount/Pcs", AmountField.class, new AmountField());
		table.setColumnWidth("Amount/Pcs",80);

		table.addContainerProperty("Remarks", TextField.class, new TextField());
		table.setColumnWidth("Remarks",190);

		tableinitialise();

		mainLayout.addComponent(table, "top:160.0px;left:20.0px;");
		mainLayout.addComponent(button, "top:460.0px;left:90.0px;");
		
		cmpTrueFalse();

		return mainLayout;
	}

}