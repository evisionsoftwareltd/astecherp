package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

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
import com.example.productionReport.RptDailyProductionSummary;
import com.example.productionReport.RptMixerIssue;
import com.example.productionSetup.ProductionFindWindow;
import com.example.rawMaterialTransaction.FindWindow;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings({ "serial", "unused" })
public class MixtureIssueEntry extends Window 
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblIssueNo;
	private Label lblDate;
	private Label lblProductionType;
	//	private Label lblItem;
	/*private Label lblUnit;
	private Label lblPercent;
	private Label lblStdQty;
	private Label lblStockQty;
	private Label lblIssueQty;
	private Label lblBalanceQty;
	private Label lblIssue;
	private Label lblShiftA;
	private Label lblShiftB;
	private Label lblTotal;
	private Label lblWastage;*/

	private Label lblLine;
	private ComboBox cmbProductionType;

	private Label lblReqNo;
	private ComboBox cmbReqNo;

	private TextRead txtIssueNo =  new TextRead(1);
	private PopupDateField dIssueDate;

	private DecimalFormat decFormat = new DecimalFormat("#0.000");
	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat df1 = new DecimalFormat("#0.000000");

	private SimpleDateFormat dFormatSql = new SimpleDateFormat("yyyy-MM-dd");

	boolean isUpdate = false;
	boolean isFind = false;

	private Table table,tableQty;
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<CheckBox> chkShow=new ArrayList<CheckBox>();
	private ArrayList<ComboBox> cmbJobOrder= new ArrayList<ComboBox>();
	private ArrayList<ComboBox> cmbSemiFg = new ArrayList<ComboBox>();
	private ArrayList<TextRead>tbtxtUnit= new ArrayList<TextRead>();
	private ArrayList<ComboBox> cmbMould  = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> cmbMachine = new ArrayList<ComboBox>();
	private ArrayList<AmountField> aIssueBag = new ArrayList<AmountField>();
	private ArrayList<AmountField> aIssueKg = new ArrayList <AmountField>();
	private ArrayList<Label> lblStandardNo = new ArrayList<Label>();
	private ArrayList<Label> lblStandardFlag = new ArrayList<Label>();

	private ArrayList<Label> lblSl1 = new ArrayList<Label>();
	private ArrayList<Label> lblItem  = new ArrayList<Label>();
	private ArrayList<TextRead> txtUnit= new ArrayList<TextRead>();
	private ArrayList<TextRead> txtPercent= new ArrayList<TextRead>();
	private ArrayList<TextRead> txtStdQty= new ArrayList<TextRead>();
	private ArrayList<TextRead> txtStockQty= new ArrayList<TextRead>();
	private ArrayList<TextRead> txtIssueQty= new ArrayList<TextRead>();
	private ArrayList<TextRead> txtBalanceQty= new ArrayList<TextRead>(); 

	private TextField txtReceiptId=new TextField();



	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "", "", "Exit");
	private NativeButton btnReport;
	String standardNo="",standardFlag="";
	public MixtureIssueEntry(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("Mixture Issue Entry [Moulding] :: "+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		tableInitialise();
		tableQtyInitialise();
		setEventAction();
		btnIni(true);
		componentIni(true);
		txtClear();
		tableClear();
		tableQtyClear();
		focusMove();
		cButton.btnNew.focus();
		cmbProductionTypeData();

	}
	@SuppressWarnings("rawtypes")
	private void cmbProductionTypeData() 
	{
		cmbProductionType.removeAllItems();
		Iterator iter=dbService("select productTypeId,productTypeName from tbProductionType");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}

	}

	private void refreshButtonEvent()
	{
		componentIni(true);
		btnIni(true);
		txtClear();
		tableClear();
		isFind = false;
		isUpdate = false;
	}

	private void focusMove()
	{
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(txtIssueNo);
		focusComp.add(dIssueDate);
		focusComp.add(cmbProductionType);

		for(int i = 0; i < tbtxtUnit.size(); i++)
		{
			focusComp.add(chkShow.get(i));
			focusComp.add(cmbJobOrder.get(i));
			focusComp.add(cmbSemiFg.get(i));
			focusComp.add(cmbMould.get(i));
			focusComp.add(cmbMachine.get(i));

			focusComp.add(aIssueBag.get(i));
			focusComp.add(aIssueKg.get(i));
		}

		focusComp.add(cButton.btnNew);
		focusComp.add(cButton.btnEdit);
		focusComp.add(cButton.btnSave);
		focusComp.add(cButton.btnRefresh);
		focusComp.add(cButton.btnDelete);
		focusComp.add(cButton.btnFind);
		focusComp.add(btnReport);

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
		btnReport.setEnabled(!t);
	}

	private void componentIni(boolean b)
	{
		lblIssueNo.setEnabled(!b);
		txtIssueNo.setEnabled(!b);

		lblDate.setEnabled(!b);
		dIssueDate.setEnabled(!b);

		lblProductionType.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		lblReqNo.setEnabled(!b);
		cmbReqNo.setEnabled(!b);

		tableQty.setEnabled(!b);
		//table.setEnabled(!b);
		for(int a=0;a<cmbJobOrder.size();a++){
			/*chkShow.get(a).setEnabled(!b);
			cmbJobOrder.get(a).setEnabled(!b);
			cmbSemiFg.get(a).setEnabled(!b);
			cmbMould.get(a).setEnabled(!b);
			cmbMachine.get(a).setEnabled(!b);
			tbtxtUnit.get(a).setEnabled(!b);*/
			aIssueBag.get(a).setEnabled(!b);
			aIssueKg.get(a).setEnabled(!b);
		}

	}

	/*@SuppressWarnings({ "rawtypes" })
	private Date getTime()
	{
		Date time = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = null;

		try
		{
			String sql = "";

			sql = "select convert(time, CURRENT_TIMESTAMP)";
			System.out.println("time sql"+sql);

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if(iter.hasNext())
			{
				time = (Date) iter.next();
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("getTime error: "+ex, Notification.TYPE_ERROR_MESSAGE);
		}

		return time;
	}*/


	private void txtClear()
	{

		txtIssueNo.setValue("");
		dIssueDate.setValue(new java.util.Date());
		cmbProductionType.setValue(null);
		cmbReqNo.setValue(null);
	}

	private void tableClear()
	{
		for(int i = 0; i < chkShow.size(); i++)
		{
			chkShow.get(i).setValue(false);
			cmbMachine.get(i).setValue(null);
			cmbSemiFg.get(i).setValue(null);
			cmbJobOrder.get(i).setValue(null);
			cmbMould.get(i).setValue(null);
			tbtxtUnit.get(i).setValue("");


			aIssueBag.get(i).setValue("");
			aIssueKg.get(i).setValue("");
			lblStandardFlag.get(i).setValue("");
			lblStandardNo.get(i).setValue("");

		}
	}
	private void tableQtyClear()
	{
		for(int i = 0; i < txtUnit.size(); i++)
		{
			lblItem.get(i).setValue("");
			txtUnit.get(i).setValue("");
			txtPercent.get(i).setValue("");
			txtStdQty.get(i).setValue("");
			txtStockQty.get(i).setValue("");
			txtIssueQty.get(i).setValue("");
			txtBalanceQty.get(i).setValue("");

		}

	}
	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		tableClear();
		tableQtyClear();

	}
	private boolean checktableData(){
		System.out.println("Mezbah: "+aIssueBag.get(0).getValue().toString());
		for(int a=0;a<tbtxtUnit.size();a++){
			if(!aIssueBag.get(a).getValue().toString().isEmpty()&&
					!aIssueBag.get(a).getValue().toString().equalsIgnoreCase("0.00")){
				return true;
			}
		}
		return false;
	}
	private boolean checkValidation(){

		if(cmbProductionType.getValue()!=null){
			if(cmbReqNo.getValue()!=null){
				if(checktableData()){
					return true;
				}
				else{
					showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide Req No",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Production Type",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private String autoId(){
		String sql="select isnull(MAX(issueNo),0)+1 from tbMixtureIssueEntryInfo";
		Iterator iter=dbService(sql);
		if(iter.hasNext()){
			return iter.next().toString();
		}
		return "";
	}
	private void getStandardInfo(String semiFg,String mould,int ar) {
		String sql="select JobNo,mouldName,isnull(max(slFlag),1) from tbFinishedGoodsStandardInfo where fGCode='"+semiFg+"' and mouldName='"+mould+"'  "+
				" and status='Active'and declarationDate=( "+
				" select max(declarationDate) from tbFinishedGoodsStandardInfo  "+
				" where fGCode='"+semiFg+"' and mouldName='"+mould+"' and status='Active') group by JobNo,mouldName";
		Iterator iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			lblStandardNo.get(ar).setValue(element[0]);
			lblStandardFlag.get(ar).setValue(element[2]);
		}
	}
	private void insertData(){
		String autoIssue;
		if(isUpdate){
			autoIssue=txtIssueNo.getValue().toString();

		}
		else{
			autoIssue=autoId();
		}
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			
			String sql="insert into tbMixtureIssueEntryInfo(issueNo,issueDate,productionTypeId,productionTypeName,userName,userIp,entryTime,reqNo)values "+
					" ('"+autoIssue+"','"+dFormatSql.format(dIssueDate.getValue())+"','"+cmbProductionType.getValue()+"'," +
					"'"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"'," +
					"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'," +
					"CURRENT_TIMESTAMP,'"+cmbReqNo.getValue()+"')";
			
			if(isUpdate){
				
				String sqlUd="insert into tbUdMixtureIssueEntryInfo(issueNo,issueDate,productionTypeId,productionTypeName,userName,userIp,entryTime,reqNo,vUdFlag)values "+
						" ('"+autoIssue+"','"+dFormatSql.format(dIssueDate.getValue())+"','"+cmbProductionType.getValue()+"'," +
						"'"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"'," +
						"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'," +
						"CURRENT_TIMESTAMP,'"+cmbReqNo.getValue()+"','New')";
				
				session.createSQLQuery(sqlUd).executeUpdate();
			}
	
			session.createSQLQuery(sql).executeUpdate();
			
			for(int a=0;a<cmbJobOrder.size();a++)
			{
				if(!aIssueBag.get(a).getValue().toString().isEmpty()&&!aIssueKg.get(a).getValue().toString().isEmpty()&&
						cmbJobOrder.get(a).getValue()!=null&&cmbMachine.get(a).getValue()!=null&&
						!aIssueBag.get(a).getValue().toString().equalsIgnoreCase("0.00")&&
						!aIssueKg.get(a).getValue().toString().equalsIgnoreCase("0.00"))
				{
					//if(!isUpdate){
					getStandardInfo(cmbSemiFg.get(a).getValue().toString(),cmbMould.get(a).getValue().toString(),a);
					//}
					String sqDetailsEntry="insert into tbMixtureIssueEntryDetailsEntry(issueNo,jobOrderNo,semiFgId,semiFgName,semiFgUnit,mouldId,mouldName, "+
							"machineid,machineName,issueQty,issueBag,standardNo,standardFlag)values('"+autoIssue+"','"+cmbJobOrder.get(a).getValue()+"'," +
							"'"+cmbSemiFg.get(a).getValue()+"','"+cmbSemiFg.get(a).getItemCaption(cmbSemiFg.get(a).getValue())+"'," +
							"'"+tbtxtUnit.get(a).getValue()+"','"+cmbMould.get(a).getValue()+"','"+cmbMould.get(a).getItemCaption(cmbMould.get(a).getValue())+"'," +
							"'"+cmbMachine.get(a).getValue()+"','"+cmbMachine.get(a).getItemCaption(cmbMachine.get(a).getValue())+"'," +
							"'"+aIssueKg.get(a).getValue()+"','"+aIssueBag.get(a).getValue()+"','"+lblStandardNo.get(a).getValue()+"','"+lblStandardFlag.get(a).getValue()+"')";
					
					if(isUpdate){
						String sqDetailsEntryUd="insert into tbUdMixtureIssueEntryDetailsEntry(issueNo,jobOrderNo,semiFgId,semiFgName,semiFgUnit,mouldId,mouldName, "+
								"machineid,machineName,issueQty,issueBag,standardNo,standardFlag,vUdFlag)values('"+autoIssue+"','"+cmbJobOrder.get(a).getValue()+"'," +
								"'"+cmbSemiFg.get(a).getValue()+"','"+cmbSemiFg.get(a).getItemCaption(cmbSemiFg.get(a).getValue())+"'," +
								"'"+tbtxtUnit.get(a).getValue()+"','"+cmbMould.get(a).getValue()+"','"+cmbMould.get(a).getItemCaption(cmbMould.get(a).getValue())+"'," +
								"'"+cmbMachine.get(a).getValue()+"','"+cmbMachine.get(a).getItemCaption(cmbMachine.get(a).getValue())+"'," +
								"'"+aIssueKg.get(a).getValue()+"','"+aIssueBag.get(a).getValue()+"','"+lblStandardNo.get(a).getValue()+"','"+lblStandardFlag.get(a).getValue()+"','New')";
						session.createSQLQuery(sqDetailsEntryUd).executeUpdate();
					}

					String sqDetails="exec PrcMixtureIssueEntry '"+autoIssue+"','"+cmbJobOrder.get(a).getValue()+"'," +
							"'"+cmbSemiFg.get(a).getValue()+"','"+cmbSemiFg.get(a).getItemCaption(cmbSemiFg.get(a).getValue())+"'," +
							"'"+tbtxtUnit.get(a).getValue()+"','"+cmbMould.get(a).getValue()+"','"+cmbMould.get(a).getItemCaption(cmbMould.get(a).getValue())+"'," +
							"'"+cmbMachine.get(a).getValue()+"','"+cmbMachine.get(a).getItemCaption(cmbMachine.get(a).getValue())+"'," +
							"'"+aIssueKg.get(a).getValue()+"','"+aIssueBag.get(a).getValue()+"','Issue'";

					//if(a==0){

					//}
					session.createSQLQuery(sqDetails).executeUpdate();
					session.createSQLQuery(sqDetailsEntry).executeUpdate();
					
				}
			}
			tx.commit();
		}
		catch(Exception exp){
			if(tx!=null){
				tx.rollback();
			}
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
				showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			
			}
		}
	}

	private boolean deleteData(){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			
			String sqlUd1="insert into tbUdMixtureIssueEntryInfo "
					+ "select issueNo, issueDate, productionTypeId, productionTypeName, userName, userIp, "
					+ "entryTime, reqNo,'Updage' vUdFlag  from tbMixtureIssueEntryInfo"
					+ " where issueNo='"+txtIssueNo.getValue()+"' ";
			
			String sqlUd2="insert into tbUdMixtureIssueEntryDetailsEntry select issueNo, jobOrderNo, semiFgId, "
					+ "semiFgName, semiFgUnit, mouldId, mouldName, machineId, machineName, issueQty, issueBag, "
					+ "standardNo, standardFlag,'Update' vUdFlag from tbMixtureIssueEntryDetailsEntry "
					+ "where issueNo='"+txtIssueNo.getValue()+"'";
			
			String sqlUd3="insert into tbUdMixtureIssueEntryDetails "
					+ "select issueNo, jobOrderNo, semiFgId, semiFgName, semiFgUnit, mouldId, mouldName, machineId, "
					+ "machineName, stdWeight, issueQty, issueBag, rawId, rawName, rawUnit, percentage, stdQty, stdDeclareDate, "
					+ "rawIssueQty, rawIssueBag, 'Update' vUdFlag from tbMixtureIssueEntryDetails where issueNo='"+txtIssueNo.getValue()+"'";
			
			session.createSQLQuery(sqlUd1).executeUpdate();
			session.createSQLQuery(sqlUd2).executeUpdate();
			session.createSQLQuery(sqlUd3).executeUpdate();
			
			String sql="delete from tbMixtureIssueEntryInfo where issueNo='"+txtIssueNo.getValue()+"'";
			String sql2="delete from tbMixtureIssueEntryDetailsEntry where issueNo='"+txtIssueNo.getValue()+"'";
			String sql1="delete from tbMixtureIssueEntryDetails where issueNo='"+txtIssueNo.getValue()+"'";
			session.createSQLQuery(sql).executeUpdate();
			session.createSQLQuery(sql1).executeUpdate();
			session.createSQLQuery(sql2).executeUpdate();
			if(session!=null||tx!=null){
				tx.commit();
				session.close();
			}
			return true;
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}
	private void saveButtonEvent(){
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
						if(deleteData()){
							insertData();
							componentIni(true);
							btnIni(true);
							tableClear();
							tableQtyClear();
							txtClear();
							isFind=false;
							isUpdate=false;
						}
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
						tableQtyClear();
						txtClear();	
						isFind=false;
						isUpdate=false;
					}
				}
			});
		}	
	}
	private void cmbReqNoLoad(){
		cmbReqNo.removeAllItems();
		Iterator<?>iter=dbService(" select distinct 0,ReqNo,CONVERT(varchar(10),ReqDate,103)ReqDate from tbProductionReqInfo where productionTypeId='"+cmbProductionType.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbReqNo.addItem(element[1]);
			cmbReqNo.setItemCaption(element[1],element[1].toString()+" Date: "+element[2]);
		}
	}
	private void setEventAction() {
		cmbProductionType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductionType.getValue()!=null){
					cmbMachineLoadData(cmbProductionType.getValue().toString());
					cmbReqNoLoad();
				}
				else{
					cmbMachineLoadData("%");
					cmbReqNo.removeAllItems();
				}
			}
		});

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				dIssueDate.focus();
				isFind = false;

			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{

			//	if(ProductionCheck())
				//{
					//showNotification("Warning!","Daily Production already given against this Mixtuer Date and Production Type", Notification.TYPE_WARNING_MESSAGE);
				//}
				//else
				//{
					editButtonEvent();
					isFind=false;
				//}
			}
		});
		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				isFind=false;

			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(checkValidation()){
					saveButtonEvent();
				}

			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		btnReport.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				System.out.println("More Detais");
				reportLink();			

			}
		});
		cmbReqNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				tableClear();
				if(cmbReqNo.getValue()!=null&&!isFind){
					tableDataLoad();
				}

			}
		});
	}
	private boolean ProductionCheck()
	{


		Iterator<?>iter=dbService("select * from tbMouldProductionInfo where convert(date,mixtureDate,105)" +
				"='"+dFormatSql.format(dIssueDate.getValue())+"' and productionType='"+cmbProductionType.getValue()+"'");

		if (iter.hasNext()) 
		{
			return true;
		}


		return false;
	}
	private void tableDataLoad(){

		Iterator<?>iter=dbService("select JobOrder,semiFgId,mouldId,machineId " +
				"from tbProductionSemiFgDetails where ReqNo='"+cmbReqNo.getValue()+"' ");
		int ar=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbJobOrder.get(ar).setValue(element[0]);
			cmbSemiFg.get(ar).setValue(element[1]);
			cmbMould.get(ar).setValue(element[2]);
			cmbMachine.get(ar).setValue(element[3]);
			ar++;
			if(ar==cmbJobOrder.size()-1){
				tableRowAdd(ar+1);
				if(cmbProductionType.getValue()!=null){
					cmbMachineLoadData(cmbProductionType.getValue().toString(),ar+1);
				}
				else{
					cmbMachineLoadData("%",ar+1);
				}
			}
		}
	}
	private void reportLink() 
	{
		Window win = new RptMixerIssue(sessionBean,"");

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
	private void findButtonEvent(){

		Window win=new FindWindow(sessionBean,txtReceiptId,"MixtureIssue");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtReceiptId.getValue().toString().length()>0)
					System.out.println("Not Done");
				findInitialise();
			}
		});


		this.getParent().addWindow(win);

	}
	private void findInitialise(){
		tableClear();
		tableQtyClear();
		String sql="select issueNo,issueDate,productionTypeId,reqNo from tbMixtureIssueEntryInfo where issueNo='"+txtReceiptId.getValue().toString()+"'";
		System.out.println(sql);
		Iterator<?>iter=dbService("select issueNo,issueDate,productionTypeId,reqNo from tbMixtureIssueEntryInfo where issueNo='"+txtReceiptId.getValue().toString()+"'");
		if(iter.hasNext()){
			System.out.println(sql);
			Object element[]=(Object[])iter.next();
			txtIssueNo.setValue(element[0]);
			dIssueDate.setValue(element[1]);
			cmbProductionType.setValue(element[2]);
			cmbReqNo.setValue(element[3]);
		}
		Iterator<?>iterDetails=dbService("select jobOrderNo,semiFgId,semiFgUnit,mouldId,machineId,issueBag,issueQty,standardNo,standardFlag from tbMixtureIssueEntryDetailsEntry where issueNo='"+txtReceiptId.getValue().toString()+"'");
		int a=0;
		while(iterDetails.hasNext()){
			Object element1[]=(Object[])iterDetails.next();
			cmbJobOrder.get(a).setValue(element1[0]);
			cmbSemiFg.get(a).setValue(element1[1]);
			tbtxtUnit.get(a).setValue(element1[2]);
			cmbMould.get(a).setValue(element1[3]);
			cmbMachine.get(a).setValue(element1[4]);
			aIssueBag.get(a).setValue(df.format(element1[5]));
			aIssueKg.get(a).setValue(df.format(element1[6]));
			lblStandardNo.get(a).setValue(element1[7]);
			lblStandardFlag.get(a).setValue(element1[8]);
			a++;
			if(a==cmbJobOrder.size()-1){
				tableRowAdd(a+1);
				if(cmbProductionType.getValue()!=null){
					cmbMachineLoadData(cmbProductionType.getValue().toString(),a+1);
				}
				else{
					cmbMachineLoadData("%",a+1);
				}
			}
		}

	}
	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
		//table.refreshCurrentPage();
	}

	@SuppressWarnings("rawtypes")
	private Iterator dbService(String sql){
		Transaction tx=null;
		Session session=null;
		Iterator iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			iter= session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null||session!=null){
				tx.commit();
				session.close();
			}
		}
		return iter;
	}


	private void tableInitialise()
	{
		for(int i = 0; i < 15; i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableQtyInitialise()
	{
		for(int i = 0; i < 5; i++)
		{
			tableQtyRowAdd(i);
		}


	}

	private void tableQtyRowAdd(int ar)
	{
		lblSl1.add(ar, new Label());
		lblSl1.get(ar).setWidth("100%");
		lblSl1.get(ar).setValue(ar+1);

		lblItem.add(ar,new Label());
		lblItem.get(ar).setWidth("100%");
		lblItem.get(ar).setImmediate(true);

		txtUnit.add(ar, new TextRead(1));
		txtUnit.get(ar).setWidth("100%");
		txtUnit.get(ar).setImmediate(true);

		txtPercent.add(ar, new TextRead(1));
		txtPercent.get(ar).setWidth("100%");
		txtPercent.get(ar).setImmediate(true);

		txtStdQty.add(ar, new TextRead(1));
		txtStdQty.get(ar).setWidth("100%");
		txtStdQty.get(ar).setImmediate(true);

		txtStockQty.add(ar, new TextRead(1));
		txtStockQty.get(ar).setWidth("100%");
		txtStockQty.get(ar).setImmediate(true);

		txtIssueQty.add(ar, new TextRead(1));
		txtIssueQty.get(ar).setWidth("100%");
		txtIssueQty.get(ar).setImmediate(true);

		txtBalanceQty.add(ar, new TextRead(1));
		txtBalanceQty.get(ar).setWidth("100%");
		txtBalanceQty.get(ar).setImmediate(true);

		tableQty.addItem(new Object[] {lblSl1.get(ar),lblItem.get(ar), txtUnit.get(ar), txtPercent.get(ar), txtStdQty.get(ar) , txtStockQty.get(ar),
				txtIssueQty.get(ar), 	txtBalanceQty.get(ar)}, ar);

	}

	private void cmbJobOrderLoadData(int ar){
		Iterator<?>iter=dbService("select distinct orderNo from tbJobOrderInfo where isActive=1 order by orderNo");
		while(iter.hasNext()){
			cmbJobOrder.get(ar).addItem(iter.next());
		}
	}
	/*private void cmbMachineLoadData(int ar){

			cmbMachine.get(ar).removeAllItems();
			Iterator<?>iter=dbService(" select distinct vMachineCode,vMachineName from tbMachineInfo " +
					"where productionTypeid like '"+cmbProductionType.getValue()+"' order by vMachineName ");
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				cmbMachine.get(ar).addItem(element[0]);
				cmbMachine.get(ar).setItemCaption(element[0],element[1].toString());
			}

	}*/
	private void cmbMachineLoadData(String type){
		for(int ar=0;ar<cmbJobOrder.size();ar++){
			cmbMachine.get(ar).removeAllItems();
			Iterator<?>iter=dbService(" select distinct vMachineCode,vMachineName from tbMachineInfo " +
					"where productionTypeid like '"+type+"' order by vMachineName ");
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				cmbMachine.get(ar).addItem(element[0]);
				cmbMachine.get(ar).setItemCaption(element[0],element[1].toString());
			}
		}
	}
	private void cmbMachineLoadData(String type,int ar){
		//for(int ar=0;ar<cmbJobOrder.size();ar++){
		cmbMachine.get(ar).removeAllItems();
		Iterator<?>iter=dbService(" select distinct vMachineCode,vMachineName from tbMachineInfo " +
				"where productionTypeid like '"+type+"' order by vMachineName ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbMachine.get(ar).addItem(element[0]);
			cmbMachine.get(ar).setItemCaption(element[0],element[1].toString());
		}
		//}
	}
	private boolean showValidation(int ar){

		if(cmbSemiFg.get(ar).getValue()!=null){
			if(cmbMould.get(ar).getValue()!=null){
				if(!aIssueBag.get(ar).getValue().toString().isEmpty()||!aIssueKg.get(ar).getValue().toString().isEmpty()){
					return true;
				}
				else{
					showNotification("Please Provide Issue Qty",Notification.TYPE_WARNING_MESSAGE);
					chkShow.get(ar).setValue(false);
				}
			}
			else{
				showNotification("Please Provide Mould Name",Notification.TYPE_WARNING_MESSAGE);
				chkShow.get(ar).setValue(false);
			}
		}
		else{
			showNotification("Please Provide SemiFg Name",Notification.TYPE_WARNING_MESSAGE);
			chkShow.get(ar).setValue(false);
		}
		return false;
	}
	private boolean doubleEntryCheck(Object jobOrder,Object SemiFg,Object Mould,Object Machine,int ar)
	{
		for(int a=0;a<=chkShow.size();a++){
			if(a!=ar){
				if(jobOrder!=null&&SemiFg!=null&&Mould!=null&&Machine!=null){
					if(jobOrder.equals(cmbJobOrder.get(a).getValue())&&
							SemiFg.equals(cmbSemiFg.equals(cmbSemiFg.get(a).getValue()))&&
							Mould.equals(cmbMachine.equals(cmbMachine.get(a).getValue()))&&
							Machine.equals(cmbMachine.get(a).getValue())){
						return false;
					}
				}
			}
		}
		return true;
	}
	private void tableRowAdd( final int ar)

	{
		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setValue(ar+1);

		chkShow.add(ar,new CheckBox());
		chkShow.get(ar).setWidth("100%");
		chkShow.get(ar).setImmediate(true);
		chkShow.get(ar).setEnabled(false);

		chkShow.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(chkShow.get(ar).booleanValue()){
					for(int a=0;a<lblSl.size();a++){
						if(a!=ar){
							chkShow.get(a).setValue(false);
						}
					}
					if(showValidation(ar)){
						Iterator<?>iter=dbService("select * from funMixtureIssueEntryShow" +
								"('"+cmbSemiFg.get(ar).getValue()+"','"+cmbMould.get(ar).getValue()+"'," +
								"'"+aIssueKg.get(ar).getValue()+"','"+dFormatSql.format(dIssueDate.getValue())+"')");
						int a=0;
						while(iter.hasNext()){
							Object element[]=(Object[])iter.next();
							lblItem.get(a).setValue(element[1]);
							txtUnit.get(a).setValue(element[2]);
							txtPercent.get(a).setValue(df.format(element[3]));
							txtStdQty.get(a).setValue(df1.format(element[4]));
							txtStockQty.get(a).setValue(df.format(element[5]));
							txtIssueQty.get(a).setValue(df.format(element[6]));
							txtBalanceQty.get(a).setValue(df.format(element[7]));
							a++;

							if(a==txtUnit.size()-1){
								tableQtyRowAdd(ar+1);
							}
						}
					}
				}
				else{
					tableQtyClear();
				}
			}
		});

		cmbJobOrder.add(ar, new ComboBox());
		cmbJobOrder.get(ar).setWidth("100%");
		cmbJobOrder.get(ar).setImmediate(true);
		cmbJobOrder.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbJobOrder.get(ar).setEnabled(false);
		cmbJobOrderLoadData(ar);
		cmbJobOrder.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbJobOrder.get(ar).getValue()!=null)
				{
					cmbSemiFg.get(ar).removeAllItems();
					String sql="select fgId,(select semiFgName from tbSemiFgInfo where semiFgCode like fgId)semiFgName,(select color from tbSemiFgInfo where semiFgCode like fgId)color "+
							" from tbJobOrderDetails where orderNo='"+cmbJobOrder.get(ar).getValue()+"'";
					System.out.println(sql);
					Iterator<?>iter=dbService(sql);
					while(iter.hasNext()){
						Object element[]=(Object[])iter.next();
						cmbSemiFg.get(ar).addItem(element[0]);
						cmbSemiFg.get(ar).setItemCaption(element[0], element[1].toString()+" # "+element[2].toString());
					}
				}
				else{
					cmbSemiFg.get(ar).removeAllItems();
				}
			}
		});

		cmbSemiFg.add(ar, new ComboBox());
		cmbSemiFg.get(ar).setWidth("100%");
		cmbSemiFg.get(ar).setImmediate(true);
		cmbSemiFg.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSemiFg.get(ar).setEnabled(false);

		cmbSemiFg.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbJobOrder.get(ar).getValue()!=null){
					if(cmbSemiFg.get(ar).getValue()!=null){
						cmbMould.get(ar).removeAllItems();
						String sql="select distinct a.mouldName,(select mouldName from tbmouldInfo where mouldid=a.mouldName), "+
								"(select unit from tbSemiFgInfo where semiFgCode like '"+cmbSemiFg.get(ar).getValue()+"')unit from tbFinishedGoodsStandardInfo a "+
								"inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo where a.fGCode='"+cmbSemiFg.get(ar).getValue()+"'";
						Iterator<?>iter=dbService(sql);
						while(iter.hasNext()){
							Object element[]=(Object[])iter.next();
							cmbMould.get(ar).addItem(element[0]);
							cmbMould.get(ar).setItemCaption(element[0], element[1].toString());
							tbtxtUnit.get(ar).setValue(element[2]);
						}
					}
					else{
						cmbMould.get(ar).removeAllItems();
					}
				}
				else{
					showNotification("Please provide Job order No",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		tbtxtUnit.add(ar, new TextRead(1));
		tbtxtUnit.get(ar).setWidth("100%");
		tbtxtUnit.get(ar).setImmediate(true);
		tbtxtUnit.get(ar).setEnabled(false);

		cmbMould.add(ar, new ComboBox());
		cmbMould.get(ar).setWidth("100%");
		cmbMould.get(ar).setImmediate(true);
		cmbMould.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbMould.get(ar).setEnabled(false);

		cmbMachine.add(ar, new ComboBox());
		cmbMachine.get(ar).setWidth("100%");
		cmbMachine.get(ar).setImmediate(true);
		cmbMachine.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbMachine.get(ar).setEnabled(false);
		//cmbMachineLoadData(ar);

		cmbMachine.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbJobOrder.get(ar).getValue()!=null){
					if(cmbSemiFg.get(ar).getValue()!=null){
						if(cmbMould.get(ar).getValue()!=null){
							if(cmbMachine.get(ar).getValue()!=null){
								/*if(doubleEntryCheck(cmbJobOrder.get(ar).getValue(), cmbSemiFg.get(ar).getValue()
										, cmbMould.get(ar).getValue(), cmbMachine.get(ar).getValue(), ar))
								{
									aIssueBag.get(ar).setEnabled(true);
									aIssueKg.get(ar).setEnabled(true);

									if(ar==cmbJobOrder.size()-1){
										tableRowAdd(ar+1);
									}
								}
								else{
									showNotification("Double Entry!!",Notification.TYPE_WARNING_MESSAGE);
									cmbMachine.get(ar).setValue(null);
									aIssueBag.get(ar).setEnabled(false);
									aIssueKg.get(ar).setEnabled(false);
									cmbMachine.get(ar).focus();
								}*/
								aIssueBag.get(ar).setEnabled(true);
								aIssueKg.get(ar).setEnabled(true);

								if(ar==cmbJobOrder.size()-1){
									tableRowAdd(ar+1);
									if(cmbProductionType.getValue()!=null){
										cmbMachineLoadData(cmbProductionType.getValue().toString(),ar+1);
									}
									else{
										cmbMachineLoadData("%",ar+1);
									}
								}
							}
							else{
								aIssueBag.get(ar).setValue("");
								aIssueKg.get(ar).setValue("");
								aIssueBag.get(ar).setEnabled(false);
								aIssueKg.get(ar).setEnabled(false);
							}
						}
						else{
							showNotification("Please provide Mould Name",Notification.TYPE_WARNING_MESSAGE);
							cmbMachine.get(ar).setValue(null);
							cmbMachine.get(ar).focus();
						}
					}
					else{
						showNotification("Please provide Semi Fg Name",Notification.TYPE_WARNING_MESSAGE);
						cmbMachine.get(ar).setValue(null);
						cmbMachine.get(ar).focus();
					}
				}
				else{
					showNotification("Please provide Job order No",Notification.TYPE_WARNING_MESSAGE);
					cmbMachine.get(ar).setValue(null);
					cmbMachine.get(ar).focus();
				}
			}
		});

		aIssueBag.add(ar, new AmountField());
		aIssueBag.get(ar).setWidth("100%");
		aIssueBag.get(ar).setImmediate(true);
		aIssueBag.get(ar).setEnabled(false);
		aIssueBag.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				bagKgCalc("Bag",ar);
			}
		});

		aIssueKg.add(ar, new AmountField());
		aIssueKg.get(ar).setWidth("100%");
		aIssueKg.get(ar).setImmediate(true);
		aIssueKg.get(ar).setEnabled(false);
		aIssueKg.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				bagKgCalc("Kg",ar);
			}
		});

		lblStandardNo.add(ar, new Label());
		lblStandardNo.get(ar).setWidth("100%");

		lblStandardFlag.add(ar, new Label());
		lblStandardFlag.get(ar).setWidth("100%");

		table.addItem(new Object[] {lblSl.get(ar), chkShow.get(ar), cmbJobOrder.get(ar), cmbSemiFg.get(ar), tbtxtUnit.get(ar),cmbMould.get(ar),cmbMachine.get(ar),  aIssueBag.get(ar),
				aIssueKg.get(ar),lblStandardNo.get(ar),lblStandardFlag.get(ar)}, new Integer(ar));

	}
	private void bagKgCalc(String caption,int ar){
		if(caption.equalsIgnoreCase("Bag")){
			double bag=Double.parseDouble(aIssueBag.get(ar).getValue().toString().isEmpty()?"0.0":aIssueBag.get(ar).getValue().toString());
			double kg=bag*25;
			aIssueKg.get(ar).setValue(df.format(kg));
		}
		else if(caption.equalsIgnoreCase("Kg")){
			double kg=Double.parseDouble(aIssueKg.get(ar).getValue().toString().isEmpty()?"0.0":aIssueKg.get(ar).getValue().toString());
			double bag=kg/25;
			aIssueBag.get(ar).setValue(df.format(bag));
		}
	}
	private AbsoluteLayout buildMainLayout()
	{

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		setWidth("1100px");
		setHeight("640px");

		lblIssueNo = new Label("Issue No :");
		lblIssueNo.setImmediate(false);
		lblIssueNo.setWidth("-1px");
		lblIssueNo.setHeight("-1px");

		txtIssueNo=new TextRead(1);
		txtIssueNo.setImmediate(true);
		txtIssueNo.setWidth("100px");
		txtIssueNo.setHeight("22px");

		lblDate = new Label(" Date :");
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");

		dIssueDate = new PopupDateField();
		dIssueDate.setImmediate(true);
		dIssueDate.setWidth("-1px");
		dIssueDate.setHeight("-1px");
		dIssueDate.setDateFormat("dd-MM-yyyy");
		dIssueDate.setValue(new java.util.Date());
		dIssueDate.setResolution(PopupDateField.RESOLUTION_DAY);

		lblProductionType = new Label("Production Type :");
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");

		cmbProductionType = new ComboBox();
		cmbProductionType.setWidth("150px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setImmediate(true);
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblReqNo = new Label("Req No :");
		lblReqNo.setImmediate(false);
		lblReqNo.setWidth("-1px");
		lblReqNo.setHeight("-1px");

		cmbReqNo = new ComboBox();
		cmbReqNo.setWidth("150px");
		cmbReqNo.setHeight("24px");
		cmbReqNo.setImmediate(true);
		cmbReqNo.setNullSelectionAllowed(true);
		cmbReqNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		btnReport= new NativeButton("Show Report");
		btnReport.setImmediate(false);
		btnReport.setIcon(new ThemeResource("../icons/preview.png"));
		btnReport.setImmediate(true);
		btnReport.setWidth("130px");
		btnReport.setHeight("24px");

		table = new Table();
		tableQty =new Table();

		table.setWidth("97%");
		table.setHeight("355px");
		table.setImmediate(true); 
		table.setColumnCollapsingAllowed(true);
		table.setFooterVisible(true);

		//table

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Show", CheckBox.class, new CheckBox());
		table.setColumnWidth("Show", 25);

		table.addContainerProperty("Job Order No", ComboBox.class, new ComboBox());
		table.setColumnWidth("Job Order No", 120);

		table.addContainerProperty("Product Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Product Name", 210);

		table.addContainerProperty("Unit ", TextRead.class, new TextRead(1));
		table.setColumnWidth("Unit", 40);

		table.addContainerProperty("Mould Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Mould Name", 160);

		table.addContainerProperty("Machine Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Machine Name", 190);

		table.addContainerProperty("Issue BAG ", AmountField.class, new AmountField());
		table.setColumnWidth("Issue BAG ", 50);

		table.addContainerProperty("Issue KG ", AmountField.class, new AmountField());
		table.setColumnWidth("Issue KG", 80);

		table.addContainerProperty("StandardNo", Label.class, new Label());
		table.setColumnWidth("StandardNo", 20);
		table.setColumnCollapsed("StandardNo", true);

		table.addContainerProperty("StandardFlag", Label.class, new Label());
		table.setColumnWidth("StandardFlag", 20);
		table.setColumnCollapsed("StandardFlag", true);


		tableQty.setWidth("97%");
		tableQty.setHeight("155px");
		tableQty.setImmediate(true); 
		tableQty.setColumnCollapsingAllowed(true);
		tableQty.setFooterVisible(true);

		//tableQty
		tableQty.addContainerProperty("SL#", Label.class, new Label());
		tableQty.setColumnWidth("SL#", 20);

		tableQty.addContainerProperty("Item Name", Label.class, new Label());
		tableQty.setColumnWidth("Item Name", 265);

		tableQty.addContainerProperty("Unit. ", TextRead.class, new TextRead(1));
		tableQty.setColumnWidth("Unit. ", 40);

		tableQty.addContainerProperty("Percent", TextRead.class, new TextRead(1));
		tableQty.setColumnWidth("Percent", 50);

		tableQty.addContainerProperty("Std Qty", TextRead.class, new TextRead(1));
		tableQty.setColumnWidth("Std Qty", 70);

		tableQty.addContainerProperty("Stock Qty", TextRead.class, new TextRead(1));
		tableQty.setColumnWidth("Stock Qty", 70);

		tableQty.addContainerProperty("Issue Qty", TextRead.class, new TextRead(1));
		tableQty.setColumnWidth("Issue Qty", 70);

		tableQty.addContainerProperty("Balance Qty", TextRead.class, new TextRead(1));
		tableQty.setColumnWidth("Balance Qty", 70);

		mainLayout.addComponent(lblIssueNo, "top: 20px; left: 20px;");
		mainLayout.addComponent(txtIssueNo, "top: 18px; left: 120px;");

		mainLayout.addComponent(lblDate, "top: 50px; left: 20px;");
		mainLayout.addComponent(dIssueDate, "top: 48px; left: 120px;");

		mainLayout.addComponent(lblProductionType, "top: 80px; left: 20px;");
		mainLayout.addComponent(cmbProductionType, "top: 78px; left: 120px;");

		mainLayout.addComponent(lblReqNo, "top: 110px; left: 20px;");
		mainLayout.addComponent(cmbReqNo, "top: 108px; left: 120px;");

		mainLayout.addComponent(tableQty, "top: 20px; left:290px;");
		mainLayout.addComponent(table, "top: 180px; left:15px;");


		lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:540.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:560.0px; left:220.0px;");
		mainLayout.addComponent(btnReport, "top:560.0px;left:800.0px;");

		return mainLayout;
	}

}
