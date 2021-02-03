package com.example.productionTransaction;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import com.example.productionReport.RptDailyProduction;
import com.example.productionReport.RptDailyProductionSummary;
import com.example.productionReport.RptMouldingDailyProduction;
import com.example.productionSetup.ProductionFindWindow;
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

public class MouldingProductionEntry extends Window 
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	// Labels
	private Label lblProductionNo;
	private Label lblProductionDate;
	private Label lblProductionStep;
	private Label lblIssueNo;
	private Label lblRawItem;

	private Label lblIssue;
	private Label lblShiftA;
	private Label lblShiftB;
	private Label lblTotal;
	private Label lblWastage;
	private Label lblProductionType;
	private Label lblMixtureDate;
	private Label lblVoucherNo;

	private Label lblLine;
	private Label lblIssueQty,lblIssuePcs,lblRemainQty,lblRemainPcs,lblTotalPcs,
	lblTotalQty,lblTotalReject,lblRejectPercent;

	// ComboBox
	private ComboBox cmbProductionType;
	private ComboBox cmbProductionStep;
	private ComboBox cmbIssueNo;
	private ComboBox cmbRawItem;
	private ComboBox cmbjoborderNO;
	private ComboBox cmbMixtureDate;

	// TextRead
	private TextRead txtProductionNo;
	private TextRead txtIssue;   
	private TextRead txtShiftA;
	private TextRead txtShiftB;
	private TextRead txtTotal;
	private TextRead txtWastage;
	private TextRead txtIssueQty1,txtIssuePcs,txtRemainQty,txtRemainPcs,
	txtTotalQty1,txtTotalPcs,txtTotalReject,txtRejectPercent,txtVoucherNo;


	private TextField txtProdNo = new TextField();


	private PopupDateField dProductionDate;


	private DecimalFormat decFormat = new DecimalFormat("#0.000");
	private DecimalFormat decFormat2 = new DecimalFormat("#0.000");
	private DecimalFormat df2 = new DecimalFormat("#0.00");
	private DecimalFormat df = new DecimalFormat("#0");

	// dateFormats
	private SimpleDateFormat dFormatSql = new SimpleDateFormat("yyyy-MM-dd");
	
	private SimpleDateFormat dFormatSqlHourminutesecond = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	// boolean Values
	boolean isUpdate = false;
	boolean isFind = false;

	// table
	private Table table,tableRM;


	private ArrayList<Label> lblSl = new ArrayList<Label>();

	private ArrayList<CheckBox> chkFG=new ArrayList<CheckBox>();
	private ArrayList<CheckBox> chkShow=new ArrayList<CheckBox>();

	private ArrayList<TextRead> tbTxtStandardAmount = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtStandardFlag = new ArrayList<TextRead>();
	private ArrayList<ComboBox> cmbMachine = new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbTxtStandardNo = new ArrayList<TextRead>();
	private ArrayList<ComboBox> cmbSemiFG = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbCmbJobOrder = new ArrayList<ComboBox>();

	private ArrayList<TextRead> txtIssueQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> txtIssueRemainQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtIssuepcs = new ArrayList<TextRead>();

	private ArrayList<TextRead> afShiftAQty = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> afShiftApcs = new ArrayList<AmountField>();

	private ArrayList<TextRead> afShiftBQty = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> afShiftBpcs = new ArrayList<AmountField>();

	private ArrayList<TextRead> txtTotalQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> txtTotalpcs = new ArrayList<TextRead>();

	private ArrayList<AmountField> afWastageQty = new ArrayList<AmountField>();
	private ArrayList<AmountField> afWastagePercent = new ArrayList<AmountField>();
	private ArrayList<TextRead> afWastageTotal = new ArrayList<TextRead>(1);

	//private ArrayList<TextField>txtColor=new ArrayList<TextField>();
	private ArrayList<AmountField>tbtxtCavity=new ArrayList<AmountField>();
	private ArrayList<AmountField>tbtxtCT=new ArrayList<AmountField>();
	//private ArrayList<AmountField>txtTarget=new ArrayList<AmountField>();
	private ArrayList<AmountField>txtRejShiftA=new ArrayList<AmountField>();
	private ArrayList<AmountField>txtRejShiftB=new ArrayList<AmountField>();
	private ArrayList<ComboBox>cmbMould=new ArrayList<ComboBox>();
	private ArrayList<TextRead>tbTxtStandardPrice=new ArrayList<TextRead>();
	//private ArrayList<TextField>txtBreakDownTime=new ArrayList<TextField>();
	//private ArrayList<TextField>txtBreakReason=new ArrayList<TextField>();
	//private ArrayList<TextField>txtRemarks=new ArrayList<TextField>();

	Label lblCavity,lblCT,lblTarget;
	TextRead txtCavity,txtCT,txtTarget;

	// Button
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "Preview", "", "Exit");
	private NativeButton btnReport;
	String[]a1={"Not Sample","Sample Not Finished","Sample Finished"};
	private List<String> lst1=Arrays.asList(a1[0],a1[1],a1[2]);
	private OptionGroup optiongroup1=new OptionGroup();
	double stdWeight=0.0;
	String productionNoUpdate="";
	public MouldingProductionEntry(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("Moulding Production Entry :: "+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		tableInitialise();
		setEventAction();

		btnIni(true);
		componentIni(true);
		txtClear();
		tableClear();
		focusMove();
		cButton.btnNew.focus();
		cmbProductionTypeData();
		cmbMixtureDataLoad("%");
	}
	private void cmbMixtureDataLoad(String type) {
		cmbMixtureDate.removeAllItems();
		Iterator iter=dbService("select distinct 0,CONVERT(varchar(10),issueDate,103)date from tbMixtureIssueEntryInfo " +
				"where productionTypeId like '"+type+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbMixtureDate.addItem(element[1].toString());
		}
	}
	private void cmbProductionTypeData() {
		cmbProductionType.removeAllItems();
		Iterator iter=dbService("select productTypeId,productTypeName from tbProductionType  where productTypeId!='PT-3'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbProductionStepData() {
		cmbProductionStep.removeAllItems();
		Iterator iter=dbService("select StepId, StepName from tbProductionStep where productionTypeId like '"+cmbProductionType.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionStep.addItem(element[0]);
			cmbProductionStep.setItemCaption(element[0], element[1].toString());
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

		focusComp.add(dProductionDate);
		focusComp.add(cmbProductionStep);
		focusComp.add(cmbIssueNo);

		for(int i = 0; i < cmbSemiFG.size(); i++)
		{
			focusComp.add(cmbMachine.get(i));
			focusComp.add(cmbSemiFG.get(i));
			focusComp.add(tbtxtCT.get(i));
			focusComp.add(tbtxtCavity.get(i));
			focusComp.add(afShiftApcs.get(i));
			focusComp.add(txtRejShiftA.get(i));
			focusComp.add(afShiftBpcs.get(i));
			focusComp.add(txtRejShiftB.get(i));
			focusComp.add(afWastageQty.get(i));
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
	}

	private void componentIni(boolean b)
	{
		lblProductionNo.setEnabled(!b);
		txtProductionNo.setEnabled(!b);

		lblProductionDate.setEnabled(!b);
		dProductionDate.setEnabled(!b);

		lblProductionType.setEnabled(!b);
		cmbProductionType.setEnabled(!b);

		lblProductionStep.setEnabled(!b);
		cmbProductionStep.setEnabled(!b);

		lblIssueNo.setEnabled(!b);
		cmbIssueNo.setEnabled(!b);

		lblVoucherNo.setEnabled(!b);
		txtVoucherNo.setEnabled(!b);


		//table.setEnabled(!b);
		for(int a=0;a<tbCmbJobOrder.size();a++){

			tbtxtCT.get(a).setEnabled(!b);
			tbtxtCavity.get(a).setEnabled(!b);

			afShiftApcs.get(a).setEnabled(!b);
			afShiftBpcs.get(a).setEnabled(!b);
			txtRejShiftA.get(a).setEnabled(!b);
			txtRejShiftB.get(a).setEnabled(!b);

		}


		lblIssue.setEnabled(!b);
		txtIssue.setEnabled(!b);

		lblIssueQty.setEnabled(!b);
		txtIssueQty1.setEnabled(!b);

		lblRemainQty.setEnabled(!b);
		txtRemainQty.setEnabled(!b);

		lblIssuePcs.setEnabled(!b);
		txtIssuePcs.setEnabled(!b);

		lblRemainPcs.setEnabled(!b);
		txtRemainPcs.setEnabled(!b);

		lblTotalQty.setEnabled(!b);
		txtTotalQty1.setEnabled(!b);

		lblTotalPcs.setEnabled(!b);
		txtTotalPcs.setEnabled(!b);

		lblTotalReject.setEnabled(!b);
		txtTotalReject.setEnabled(!b);

		lblRejectPercent.setEnabled(!b);
		txtRejectPercent.setEnabled(!b);

		//lbljoborderNo.setEnabled(!b);
		//cmbjoborderNO.setEnabled(!b);
		optiongroup1.setEnabled(!b);

		lblCavity.setEnabled(!b);
		txtCavity.setEnabled(!b);

		lblCT.setEnabled(!b);
		txtCT.setEnabled(!b);

		lblTarget.setEnabled(!b);
		txtTarget.setEnabled(!b);

		btnReport.setEnabled(!b);
	}
	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete tbMouldProductionInfo where ProductionNo='"+txtProductionNo.getValue()+"' ").executeUpdate();
			///System.out.println("delete tbLabelProductionInfo where ProductionNo='"+txtProductionNo.getValue()+ "' ");

			session.createSQLQuery("delete tbMouldProductionDetails where ProductionNo='"+txtProductionNo.getValue()+"' ").executeUpdate();
			//System.out.println("delete tbLabelProductionDetails where ProductionNo='"+txtProductionNo.getValue()+"' ");
			session.createSQLQuery(" delete from tbMouldFinishProduct where ProductionNo like '"+txtProductionNo.getValue().toString()+"' ").executeUpdate();

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dFormatSql.format(dProductionDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			session.createSQLQuery("delete "+voucher+"  where Voucher_No='"+txtVoucherNo.getValue()+"'").executeUpdate();

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
							insertData("Update");
						}
						else 
						{
							tx.rollback();
						}
						componentIni(true);
						btnIni(true);
						tableClear();
						txtClear();
						productionNoUpdate="";
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
						insertData("New");
						componentIni(true);
						btnIni(true);
						tableClear();
						txtClear();		
						productionNoUpdate="";
					}
				}
			});
		}

	}
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
	}
	private void checkFg(int ar){

		Iterator iter=dbService("select 0,isnull(isFg,'') from tbFinishedGoodsStandardInfo " +
				"where mouldName='"+cmbMould.get(ar).getValue()+"' and fGCode='"+cmbSemiFG.get(ar).getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(element[1].toString().equalsIgnoreCase("Yes")){
				chkFG.get(ar).setValue(true);
			}
			else{
				chkFG.get(ar).setValue(false);
			}
		}
	}
	public String vocherIdGenerate()
	{
		String vo_id = null;
		Transaction tx = null;
		try{
			Session session1 = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session1.beginTransaction();
			String fsl = session1.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dFormatSql.format(dProductionDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			Iterator<?> iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE  CompanyId = '"+ sessionBean.getCompanyId() +"' and (vouchertype = 'jau' or vouchertype = 'jcv' or vouchertype = 'jai')").list().iterator();
			if(iter.hasNext())
			{
				vo_id=iter.next().toString().trim();
			}
		}
		catch(Exception ex){

			this.getParent().showNotification(
					"Error",
					ex+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
		return "JV-NO-"+vo_id;
	}

	private void insertData(String Type)
	{

		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String itemType = "";
		String productionNo = "";
		String name="";
		String sampleFlag="";
		int sampleFinFlag=0;
		String mixtureDate="";
		String voucherId="";
		double standardAmount=0.0,fgStandardAmount=0.0,semiFgStandardAmount=0.0;


		//calc Standard Amount///

		double sum=0.0;
		for(int a=0;a<tbTxtStandardAmount.size();a++)
		{
			double x=Double.parseDouble(tbTxtStandardAmount.get(a).getValue().toString().isEmpty()?
					"0.0":tbTxtStandardAmount.get(a).getValue().toString());
			if(chkFG.get(a).booleanValue()){
				fgStandardAmount=fgStandardAmount+x;
			}
			else{
				semiFgStandardAmount=semiFgStandardAmount+x;
			}
		}
		standardAmount= fgStandardAmount+semiFgStandardAmount;

		////end//////////

		if(optiongroup1.getValue().toString().equalsIgnoreCase("Sample Not Finished")||optiongroup1.getValue().toString().equalsIgnoreCase("Sample Finished")){
			sampleFlag=optiongroup1.getValue().toString();
		}
		if(cmbMixtureDate.getValue()!=null){
			mixtureDate=cmbMixtureDate.getValue().toString();
		}
		try
		{

			if(Type.equalsIgnoreCase("Update")){
				productionNo =productionNoUpdate;
				voucherId=txtVoucherNo.getValue().toString();
			}
			else{
				productionNo = autoProductionNo(); 
				voucherId=vocherIdGenerate();
			}
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dFormatSql.format(dProductionDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;


			String sql =" insert into tbMouldProductionInfo" +
					" (ProductionNo, ProductionDate,productionType, userIp, userName, EntryTime,Stepid,sampleFlag," +
					"mixtureDate,voucherNo,voucherDate,ttlStandardAmount,fiscalYearSl)" +
					" values" +
					" ('"+productionNo+"'," +
					"'"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"'," +
					"'"+cmbProductionType.getValue()+"'," +
					"'"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserName()+"'," +
					"CURRENT_TIMESTAMP" +
					",'"+cmbProductionStep.getValue()+"','"+sampleFlag+"' ," +
					"'"+mixtureDate+"','"+voucherId+"','"+dFormatSql.format(dProductionDate.getValue())+"'," +
					"'"+df2.format(standardAmount)+"','"+fsl+"')" ;
			System.out.println(sql);

			String sqlUpdate =" insert into tbUdMouldProductionInfo" +
					" (ProductionNo, ProductionDate,userIp, userName, EntryTime,Stepid,sampleFlag,type," +
					"voucherNo,voucherDate,ttlStandardAmount,fiscalYearSl)" +
					" values" +
					" ('"+productionNo+"'," +
					"'"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"'," +
					//"''," +
					"'"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserName()+"'," +
					"CURRENT_TIMESTAMP" +
					",'"+cmbProductionStep.getValue()+"','"+sampleFlag+"','"+Type+"'," +
					"'"+voucherId+"','"+dFormatSql.format(dProductionDate.getValue())+"','"+df2.format(standardAmount)+"','"+fsl+"' )" ;
			System.out.println(sqlUpdate);



			session.createSQLQuery(sql).executeUpdate();
			session.createSQLQuery(sqlUpdate).executeUpdate();

			for (int i = 0; i < cmbSemiFG.size(); i++)
			{
				System.out.println("for");
				if(!txtTotalpcs.get(i).getValue().toString().isEmpty()&&tbCmbJobOrder.get(i).getValue()!=null&&
						cmbMachine.get(i).getValue()!=null&&cmbSemiFG.get(i).getValue()!=null&&cmbMould.get(i).getValue()!=null&&
						!txtTotalpcs.get(i).getValue().toString().equalsIgnoreCase("0.0"))
				{
					System.out.println("if");

					if(optiongroup1.getValue().toString().equalsIgnoreCase("Sample Finished"))
					{
						sampleFinFlag=1;
					}
					int finFlag=0;
					if(chkFG.get(i).booleanValue()){
						finFlag=1;
					}
					/*String wastagePercent="0";
					if(!afWastagePercent.get(i).getValue().toString().trim().equalsIgnoreCase("�")){//
						wastagePercent=afWastagePercent.get(i).getValue().toString();
					}*/

					String query = 	" insert into tbMouldProductionDetails" +
							" (ProductionNo, MachineName,FinishedProduct, IssueQty, IssuePcs, ShiftAQty, ShiftAPcs," +
							"ShiftBQty, ShiftBPcs, TotalQty, TotalPcs, WastageQty, WastagePcs, WastagePercent," +
							" UserIp, UserName, EntryTime,sampleFinFlag,jobOrderNo,subStepId,subSubStepId," +
							"RejShiftA,RejShiftB,khw,mouldNo,finFlag,machineModelNo,ct,cavity,isApproved," +
							"voucherNo,voucherDate,standardNo,standardFlag,standardUnitPrice,standardAmount)" +
							" values" +
							" ( "
							+"'"+productionNo+"'," +
							"'"+cmbMachine.get(i).getValue()+"'," +
							"'"+cmbSemiFG.get(i).getValue()+"'," +
							"'"+txtIssueQty.get(i).getValue().toString()+"'," +
							"'"+tbtxtIssuepcs.get(i).getValue().toString()+"'," +
							"'"+afShiftAQty.get(i).getValue().toString()+"'," +
							"'"+afShiftApcs.get(i).getValue().toString()+"'," +
							"'"+afShiftBQty.get(i).getValue().toString()+"'," +
							"'"+afShiftBpcs.get(i).getValue().toString()+"'," +
							"'"+txtTotalQty.get(i).getValue().toString()+"'," +
							"'"+txtTotalpcs.get(i).getValue().toString()+"'," +
							"'"+afWastageQty.get(i).getValue().toString()+"'," +
							"'"+afWastageTotal.get(i).getValue().toString()+"'," +
							"'"+afWastagePercent.get(i).getValue().toString()+"'," +
							"'"+sessionBean.getUserIp()+"'," +
							"'"+sessionBean.getUserName()+"'," +
							"CURRENT_TIMESTAMP" +
							",'"+sampleFinFlag+"'," +
							"'"+tbCmbJobOrder.get(i).getValue()+"'," +
							"'"+tbTxtStandardAmount.get(i).getValue()+"'," +
							"'"+tbTxtStandardFlag.get(i).getValue()+"'," +
							//"'"+txtColor.get(i).getValue()+"'," +
							//"'"+txtCavity.get(i).getValue()+"'," +
							//"'"+txtCT.get(i).getValue()+"'," +
							//"'"+txtTarget.get(i).getValue()+"'," +
							"'"+txtRejShiftA.get(i).getValue()+"'," +
							"'"+txtRejShiftB.get(i).getValue()+"'," +
							"'"+tbTxtStandardPrice.get(i).getValue()+"'," +
							//"'"+txtBreakDownTime.get(i).getValue()+"'," +
							//"'"+txtBreakReason.get(i).getValue()+"'," +
							//"'"+txtRemarks.get(i).getValue()+"'," +
							"'"+cmbMould.get(i).getValue()+"','"+finFlag+"','"+tbTxtStandardNo.get(i).getValue()+"'," +
							"'"+tbtxtCT.get(i).getValue()+"','"+tbtxtCavity.get(i).getValue()+"',0," +
							"'"+voucherId+"','"+dFormatSql.format(dProductionDate.getValue())+"','"+tbTxtStandardNo.get(i).getValue()+"'," +
							"'"+tbTxtStandardFlag.get(i).getValue()+"','"+tbTxtStandardPrice.get(i).getValue()+"'," +
							"'"+tbTxtStandardAmount.get(i).getValue()+"')" ;
					//System.out.println(query);
					session.createSQLQuery(query).executeUpdate();

					String queryUpdate = 	" insert into tbUdMouldProductionDetails" +
							" (ProductionNo, MachineName,FinishedProduct, IssueQty, IssuePcs, ShiftAQty, ShiftAPcs," +
							"ShiftBQty, ShiftBPcs, TotalQty, TotalPcs, WastageQty, WastagePcs, WastagePercent," +
							" UserIp, UserName, EntryTime,sampleFinFlag,jobOrderNo,subStepId,subSubStepId," +
							"RejShiftA,RejShiftB,khw,mouldNo,ct,cavity," +
							"voucherNo,voucherDate,standardNo,standardFlag,standardUnitPrice,standardAmount)" +
							" values" +
							" ( "
							+"'"+productionNo+"'," +
							"'"+cmbMachine.get(i).getValue()+"'," +
							"'"+cmbSemiFG.get(i).getValue()+"'," +
							"'"+txtIssueQty.get(i).getValue().toString()+"'," +
							"'"+tbtxtIssuepcs.get(i).getValue().toString()+"'," +
							"'"+afShiftAQty.get(i).getValue().toString()+"'," +
							"'"+afShiftApcs.get(i).getValue().toString()+"'," +
							"'"+afShiftBQty.get(i).getValue().toString()+"'," +
							"'"+afShiftBpcs.get(i).getValue().toString()+"'," +
							"'"+txtTotalQty.get(i).getValue().toString()+"'," +
							"'"+txtTotalpcs.get(i).getValue().toString()+"'," +
							"'"+afWastageQty.get(i).getValue().toString()+"'," +
							"'"+afWastageTotal.get(i).getValue().toString()+"'," +
							"'"+afWastagePercent.get(i).getValue().toString()+"'," +
							"'"+sessionBean.getUserIp()+"'," +
							"'"+sessionBean.getUserName()+"'," +
							"CURRENT_TIMESTAMP" +
							",'"+sampleFinFlag+"'," +
							"'"+tbCmbJobOrder.get(i).getValue()+"'," +
							"'"+tbTxtStandardAmount.get(i).getValue()+"'," +
							"'"+tbTxtStandardFlag.get(i).getValue()+"'," +
							//"'"+txtColor.get(i).getValue()+"'," +
							//"'"+txtCavity.get(i).getValue()+"'," +
							//"'"+txtCT.get(i).getValue()+"'," +
							//"'"+txtTarget.get(i).getValue()+"'," +
							"'"+txtRejShiftA.get(i).getValue()+"'," +
							"'"+txtRejShiftB.get(i).getValue()+"'," +
							"'"+tbTxtStandardPrice.get(i).getValue()+"'," +
							//"'"+txtBreakDownTime.get(i).getValue()+"'," +
							//"'"+txtBreakReason.get(i).getValue()+"'," +
							//"'"+txtRemarks.get(i).getValue()+"'," +
							"'"+cmbMould.get(i).getValue()+"','"+tbtxtCT.get(i).getValue()+"','"+tbtxtCavity.get(i).getValue()+"'," +
							"'"+voucherId+"','"+dFormatSql.format(dProductionDate.getValue())+"','"+tbTxtStandardNo.get(i).getValue()+"'," +
							"'"+tbTxtStandardFlag.get(i).getValue()+"','"+tbTxtStandardPrice.get(i).getValue()+"'," +
							"'"+tbTxtStandardAmount.get(i).getValue()+"')" ;
					//System.out.println(query);
					session.createSQLQuery(queryUpdate).executeUpdate();


					/*if(i==0)
					{
						session.createSQLQuery(sql).executeUpdate();
						session.createSQLQuery(sqlUpdate).executeUpdate();
					}*/




					if(chkFG.get(i).booleanValue()||sampleFinFlag==1)
					{
						String productId="";
						if(tbTxtStandardFlag.get(i).getValue()!=null){
							productId=tbTxtStandardFlag.get(i).getValue().toString();
						}

						String sqlFG="insert into tbMouldFinishProduct values ('','"+productionNo+"'" +
								",'"+cmbProductionStep.getValue()+"','"+cmbSemiFG.get(i).getValue()+"'," +
								"'"+txtTotalQty.get(i).getValue().toString()+"','"+txtTotalpcs.get(i).getValue().toString()+"'," +
								"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP," +
								"'"+tbCmbJobOrder.get(i).getValue()+"','"+sampleFinFlag+"'," +
								"'"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"','"+cmbMachine.get(i).getValue()+"'," +
								"'"+cmbMould.get(i).getValue()+"','"+productId+"',0)";
						System.out.println(sqlFG);
						session.createSQLQuery(sqlFG).executeUpdate();

						String sqlFGUpdate="insert into tbUdMouldFinishProduct values ('','"+productionNo+"'" +
								",'"+cmbProductionStep.getValue()+"','"+cmbSemiFG.get(i).getValue()+"'," +
								"'"+txtTotalQty.get(i).getValue().toString()+"','"+txtTotalpcs.get(i).getValue().toString()+"'," +
								"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP," +
								"'"+tbCmbJobOrder.get(i).getValue()+"','"+sampleFinFlag+"','"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"','"+Type+"')";
						System.out.println(sqlFG);
						session.createSQLQuery(sqlFGUpdate).executeUpdate();

						/**/
					}
					sampleFinFlag=0;
				}
				//System.out.println("Ok");
			}


			String naration="Production Type :"+cmbProductionType.getItemCaption(
					cmbProductionType.getValue()).toString()+" "+"ProductionNo No :"+
					txtProductionNo.getValue().toString()+" "+"production Date :"+
					new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue()).toString();

			String WipRmCr=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration," +
					"DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime," +
					"auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
					+" values(" +
					"'"+voucherId+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"'," +
					"'AL1704', "  
					+" '"+naration+"'," +
					"'0' , "
					+" '"+df2.format(standardAmount)+"'," +
					"'jau'," +
					"'U-1'," +
					"'1' ," +
					"'"+sessionBean.getUserId()+"', "
					+" '"+sessionBean.getUserIp()+"'," +
					"CURRENT_TIMESTAMP, "
					+" '2', " +
					"'"+sessionBean.getCompanyId()+"' ," +
					"''," +
					"'Production Entry Moulding','mouldingproductionEntry' ) ";
			session.createSQLQuery(WipRmCr).executeUpdate();

			if(fgStandardAmount>0){
				String FgStockDr=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration," +
						"DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime," +
						"auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
						+" values(" +
						"'"+voucherId+"'," +
						"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"'," +
						"'AL1707', "  
						+" '"+naration+"'," +
						"'"+df2.format(fgStandardAmount)+"' , "
						+" '0'," +
						"'jau'," +
						"'U-1'," +
						"'1' ," +
						"'"+sessionBean.getUserId()+"', "
						+" '"+sessionBean.getUserIp()+"'," +
						"CURRENT_TIMESTAMP, "
						+" '2', " +
						"'"+sessionBean.getCompanyId()+"' ," +
						"''," +
						"'Production Entry Moulding','mouldingproductionEntry') ";
				session.createSQLQuery(FgStockDr).executeUpdate();
			}
			if(semiFgStandardAmount>0){
				String FgStockDr=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration," +
						"DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime," +
						"auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
						+" values(" +
						"'"+voucherId+"'," +
						"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"'," +
						"'AL1706', "  
						+" '"+naration+"'," +
						"'"+df2.format(semiFgStandardAmount)+"' , "
						+" '0'," +
						"'jau'," +
						"'U-1'," +
						"'1' ," +
						"'"+sessionBean.getUserId()+"', "
						+" '"+sessionBean.getUserIp()+"'," +
						"CURRENT_TIMESTAMP, "
						+" '2', " +
						"'"+sessionBean.getCompanyId()+"' ," +
						"''," +
						"'Production Entry Moulding','mouldingproductionEntry') ";
				session.createSQLQuery(FgStockDr).executeUpdate();
			}

			tx.commit();

			this.getParent().showNotification("All information is saved successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void txtClear()
	{

		txtProductionNo.setValue("");
		dProductionDate.setValue(new java.util.Date());
		cmbProductionType.setValue(null);
		cmbProductionStep.setValue(null);
		cmbMixtureDate.setValue(null);
		cmbIssueNo.setValue(null);
		optiongroup1.select(a1[0].toString());

		txtIssueQty1.setValue("");
		txtIssuePcs.setValue("");
		txtRemainQty.setValue("");
		txtRemainPcs.setValue("");

		txtTotalQty1.setValue("");
		txtTotalPcs.setValue("");
		txtTotalReject.setValue("");
		txtRejectPercent.setValue("");

		txtCavity.setValue("");
		txtCT.setValue("");
		txtTarget.setValue("");
		txtVoucherNo.setValue("");
	}

	private void tableClear()
	{
		for(int i = 0; i < cmbSemiFG.size(); i++)
		{
			chkFG.get(i).setValue(false);
			tbTxtStandardAmount.get(i).setValue("");
			tbTxtStandardFlag.get(i).setValue("");
			cmbMachine.get(i).setValue(null);
			//cmbMould.get(i).setValue(null);
			tbCmbJobOrder.get(i).setValue(null);
			cmbSemiFG.get(i).removeAllItems();
			cmbMould.get(i).removeAllItems();

			tbtxtCT.get(i).setValue("");
			tbtxtCavity.get(i).setValue("");

			afShiftAQty.get(i).setValue("");
			afShiftApcs.get(i).setValue("");

			afShiftBQty.get(i).setValue("");
			afShiftBpcs.get(i).setValue("");

			txtTotalQty.get(i).setValue("");
			txtTotalpcs.get(i).setValue("");

			afWastageQty.get(i).setValue("");
			afWastagePercent.get(i).setValue("");
			afWastageTotal.get(i).setValue("");

			txtIssueQty.get(i).setValue("");
			txtIssueRemainQty.get(i).setValue("");
			tbtxtIssuepcs.get(i).setValue("");

			cmbMould.get(i).setValue(null);
			txtRejShiftA.get(i).setValue("");
			txtRejShiftB.get(i).setValue("");
			tbTxtStandardPrice.get(i).setValue("");
			//txtBreakDownTime.get(i).setValue("");
			//txtBreakReason.get(i).setValue("");
			//txtRemarks.get(i).setValue("");

		}
	}

	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		tableClear();


	}
	private void tableDataLoadMixture(){

		String sql=" select  jobOrderNo,machineId,semiFgId,mouldId, "+
				" isnull((select isFg from tbFinishedGoodsStandardInfo where fGCode=semiFgId and mouldName=mouldId and "+
				" declarationDate=(select MAX(declarationDate) from tbFinishedGoodsStandardInfo where fGCode=semiFgId and mouldName=mouldId)),''),b.standardNo,b.standardFlag," +
				"(select [dbo].[funcStandardDetailsInventoryRate](semiFgId,mouldId,standardNo,a.issueDate))unitPrice   from tbMixtureIssueEntryInfo a   "+
				" inner join tbMixtureIssueEntryDetailsEntry b  on a.issueNo=b.issueNo where a.productionTypeId like  "+
				" '"+cmbProductionType.getValue()+"' and convert(varchar(10),a.issueDate,103)='"+cmbMixtureDate.getValue()+"' order by b.autoId asc ";

		System.out.println("mixing query is:"+sql);
		
		/*Iterator iter=dbService("select distinct jobOrderNo,machineId,semiFgId,mouldId, isnull((select isFg from tbFinishedGoodsStandardInfo where fGCode=semiFgId and mouldName=mouldId and "+
								" declarationDate=(select MAX(declarationDate) from tbFinishedGoodsStandardInfo)),'') "+
							" inner join tbMixtureIssueEntryDetailsEntry b "+
							" on a.issueNo=b.issueNo where a.productionTypeId like '"+cmbProductionType.getValue()+"' and " +
							"convert(varchar(10),a.issueDate,103)='"+cmbMixtureDate.getValue()+"' ");*/

		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbJobOrder.get(a).setValue(element[0]);
			cmbMachine.get(a).setValue(element[1]);
			cmbSemiFG.get(a).setValue(element[2]);
			cmbMould.get(a).setValue(element[3]);
			if(element[4].toString().equalsIgnoreCase("Yes")){
				chkFG.get(a).setValue(true);
			}
			tbTxtStandardNo.get(a).setValue(element[5]);
			tbTxtStandardFlag.get(a).setValue(element[6]);
			tbTxtStandardPrice.get(a).setValue(element[7]);
			a++;
			if(a==cmbMachine.size()-1){
				tableRowAdd(a+1);
			}
		}
	}
	private void setEventAction() 
	{
		cmbMixtureDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!isFind){
					if(cmbMixtureDate.getValue()!=null&&cmbProductionType.getValue()!=null&&cmbProductionStep.getValue()!=null){
						tableClear();
						tableDataLoadMixture();
					}
					else{
						tableClear();
					}
				}
			}
		});
		cmbProductionType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductionType.getValue()!=null){
					cmbProductionStepData();
					cmbMixtureDataLoad(cmbProductionType.getValue().toString());
				}
			}
		});
		cmbProductionStep.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductionStep.getValue()!=null){
					for(int ar=0;ar<tbCmbJobOrder.size();ar++){
						//stepDataLoad(ar);
						cmbJobOrderLoadData(ar);
					}
					if(!(cmbProductionStep.getValue().toString().equalsIgnoreCase("BlowSTP-1")||
							cmbProductionStep.getValue().toString().equalsIgnoreCase("InjectionSTP-1")||
							cmbProductionStep.getValue().toString().equalsIgnoreCase("Injection Blow STP-1"))){

						cmbMixtureDate.setEnabled(false);
						table.setColumnCollapsed("JOb Order", true);
						table.setColumnCollapsed("Mould Name", true);
						table.setColumnCollapsed("FG(Labeling/Printing)", false);
						table.setColumnWidth("FG(Labeling/Printing)", 200);

					}
					else{
						cmbMixtureDate.setEnabled(true);
						table.setColumnCollapsed("JOb Order", false);
						table.setColumnCollapsed("Mould Name", false);
						table.setColumnCollapsed("FG(Labeling/Printing)", true);
						table.setColumnWidth("FG(Labeling/Printing)", 110);

						/*for(int a=0;a<tbCmbJobOrder.size();a++){
							String sql="select vMachineCode,vMachineName from tbMachineInfo order by vMachineName";
							System.out.println("Before");
							machineDataLoad(a,sql);
							System.out.println("After");
						}*/
					}
				}
				else{
					txtIssue.setVisible(true);
					lblIssue.setVisible(true);
					table.setColumnCollapsed("Step", true);
					table.setColumnCollapsed("Sub Step", true);
				}
			}
		});
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				//txtProductionNo.setValue(autoProductionNo());
				dProductionDate.focus();
				isFind = false;

			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				editButtonEvent();
				//isFind=false;
			}
		});
		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				isFind=false;
				isUpdate=false;

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

		/*cmbjoborderNO.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbjoborderNO.getValue()!=null){
					finishedProductLoadData();
				}
				else{
					for(int a=0;a<lblSl.size();a++){
						cmbSemiFG.get(a).removeAllItems();
					}
				}
			}
		});*/


		cButton.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbProductionStep.getValue()!=null)
				{
					if(tableCheck())
					{
						saveButtonEvent();
						cButton.btnNew.focus();
						isFind = false;
						isUpdate = false;
					}	
					else{
						showNotification("Please Provide All Data", Notification.TYPE_WARNING_MESSAGE);

					}							
				}
				else
				{
					showNotification("Please Select Production Step", Notification.TYPE_WARNING_MESSAGE);
					cmbProductionStep.focus();
				}
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


	}

	private void reportLink() 
	{
		Window win = new RptMouldingDailyProduction(sessionBean, "");

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


	private boolean tableCheck(){
		for(int a=0;a<tbCmbJobOrder.size();a++){
			if(Double.parseDouble("0"+txtTotalpcs.get(a).getValue())>0){
				return true;
			}
		}
		return false;
	}
	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
	}
	private void findButtonEvent() 
	{
		Window win = new MouldProductionFindWindow(sessionBean, txtProdNo);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtProdNo.getValue().toString().length() > 0)
				{
					txtClear();
					tableClear();
					isFind = true;
					isUpdate=true;
					findInitialise(txtProdNo.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}
	private void findInitialise(String txtProdNo) 
	{
		String sqlInfo="select ProductionNo,ProductionDate,productionType,Stepid,(select StepName from tbProductionStep "+
				" where StepId=a.Stepid),sampleFlag,isnull(mixtureDate,'')mixtureDate,voucherNo from tbMouldProductionInfo a where ProductionNo like '"+txtProdNo+"'";
		System.out.println(sqlInfo);
		Iterator iter=dbService(sqlInfo);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtProductionNo.setValue(element[0]);
			productionNoUpdate=element[0].toString();
			dProductionDate.setValue(element[1]);
			cmbProductionType.setValue(element[2].toString());
			cmbProductionStep.setValue(element[3].toString());
			if(element[4].toString().equalsIgnoreCase("Sample Not Finished")){
				optiongroup1.select(a1[1]);
			}
			else if(element[4].toString().equalsIgnoreCase("Sample Finished")){
				optiongroup1.select(a1[2]);
			}
			else{
				optiongroup1.select(a1[0]);
			}
			if(element[6]!=null){
				cmbMixtureDate.setValue(element[6].toString());
			}
			txtVoucherNo.setValue(element[7]);
		}
		String sqlDetails="select sampleFinFlag,MachineName,jobOrderNo,FinishedProduct,ShiftAQty,ShiftAPcs," +
				"ShiftBQty,ShiftBPcs,WastageQty as wastagePcs, "+
				"WastagePcs as wastageQty,WastagePercent,isnull(subStepId,' ')StepId,isnull(subSubStepId,'')stepName," +
				"color,isnull(cavity,0)cavity,isnull(ct,0)ct,target,RejShiftA,RejShiftB,khw,bdTime,bdReason,remarks," +
				"mouldNo,isnull(finFlag,0),standardNo,standardFlag,standardUnitPrice,standardAmount  from tbMouldProductionDetails where ProductionNo like '"+txtProdNo+"'";
		System.out.println(sqlDetails);
		Iterator iterDetails=dbService(sqlDetails);
		int ar=0;
		while(iterDetails.hasNext()){
			Object element[]=(Object[])iterDetails.next();
			if(Double.parseDouble(element[0].toString())==1){
				chkFG.get(ar).setValue(true);
			}
			else{
				chkFG.get(ar).setValue(false);
			}

			tbCmbJobOrder.get(ar).setValue(element[2]);
			cmbMachine.get(ar).setValue(element[1]);
			cmbSemiFG.get(ar).setValue(element[3]);
			afShiftApcs.get(ar).setValue(df.format(element[5]));
			afShiftAQty.get(ar).setValue(df.format(element[4]));
			afShiftBpcs.get(ar).setValue(df.format(element[7]));
			afShiftBQty.get(ar).setValue(df.format(element[6]));
			afWastageQty.get(ar).setValue(element[8]);
			afWastageTotal.get(ar).setValue(element[9]);
			afWastagePercent.get(ar).setValue(element[10]);
			System.out.println("Before: "+element[12]);
			if(!element[11].toString().isEmpty()){
				tbTxtStandardAmount.get(ar).setValue(element[11].toString());
			}
			/*if(!element[12].toString().isEmpty()){
				tbTxtStandardFlag.get(ar).setValue(element[12]);
				System.out.println("After: "+element[12]);
			}*/
			//txtColor.get(ar).setValue(element[13]);
			tbtxtCavity.get(ar).setValue(element[14]);
			tbtxtCT.get(ar).setValue(element[15]);
			//txtTarget.get(ar).setValue(element[16]);
			txtRejShiftA.get(ar).setValue(df.format(element[17]));
			txtRejShiftB.get(ar).setValue(df.format(element[18]));
			tbTxtStandardPrice.get(ar).setValue(element[19]);
			//txtBreakDownTime.get(ar).setValue(element[20]);
			//txtBreakReason.get(ar).setValue(element[21]);
			//txtRemarks.get(ar).setValue(element[22]);
			cmbMould.get(ar).setValue(element[23]);
			if(Integer.parseInt(element[24].toString())==1){
				chkFG.get(ar).setValue(true);
			}
			tbTxtStandardNo.get(ar).setValue(element[25]);
			tbTxtStandardFlag.get(ar).setValue(element[26]);
			tbTxtStandardPrice.get(ar).setValue(element[27]);
			tbTxtStandardAmount.get(ar).setValue(element[28]);

			ar++;
			if(ar==cmbMachine.size()-1){
				tableRowAdd(ar+1);
			}
		}
		txtIssueQty1.setValue("");
		txtIssuePcs.setValue("");
		txtRemainQty.setValue("");
		txtRemainPcs.setValue("");

		txtTotalQty1.setValue("");
		txtTotalPcs.setValue("");
		txtTotalReject.setValue("");
		txtRejectPercent.setValue("");

		txtCavity.setValue("");
		txtCT.setValue("");
		txtTarget.setValue("");
	}
	private void cmbJobOrderLoadData(int ar) {
		tbCmbJobOrder.get(ar).removeAllItems();
		Iterator iter=dbService("select distinct jobOrderNo from tbMixtureIssueEntryDetailsEntry union select distinct jobOrderNo from tbProductionOpening");
		while(iter.hasNext()){
			Object element=(Object)iter.next();
			tbCmbJobOrder.get(ar).addItem(element);
		}
	}
	private void tableInitialise()
	{
		for(int i = 0; i < 15; i++)
		{
			tableRowAdd(i);
		}


	}
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
	private void totlaQtyCalc(int ar,String caption){

		double shiftAQty = 0;
		double shiftBQty = 0;
		double shiftAPcs = 0;
		double shiftBPcs = 0;
		double issuePcs = 0;
		double standardPrice=0.0;
		double standardAmount=0.0;

		shiftAQty = Double.parseDouble(afShiftAQty.get(ar).getValue().toString().isEmpty()?"0.0":afShiftAQty.get(ar).getValue().toString());
		shiftBQty = Double.parseDouble(afShiftBQty.get(ar).getValue().toString().isEmpty()?"0.0":afShiftBQty.get(ar).getValue().toString());
		shiftAPcs = Double.parseDouble(afShiftApcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftApcs.get(ar).getValue().toString());
		shiftBPcs = Double.parseDouble(afShiftBpcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftBpcs.get(ar).getValue().toString());
		issuePcs = Double.parseDouble(tbtxtIssuepcs.get(ar).getValue().toString().isEmpty()?"0.0":tbtxtIssuepcs.get(ar).getValue().toString());
		standardPrice = Double.parseDouble(tbTxtStandardPrice.get(ar).getValue().toString().isEmpty()?"0.0":tbTxtStandardPrice.get(ar).getValue().toString());

		double totalQty = 0;
		double totalPcs = 0;
		totalQty = shiftAQty + shiftBQty;
		totalPcs = shiftAPcs + shiftBPcs;
		standardAmount=totalPcs*standardPrice;
		System.out.println(totalPcs);
		if(!isFind){
			if(issuePcs<totalPcs){
				if(caption.equalsIgnoreCase("shiftA")){
					showNotification("Sorry!!","Total Pcs Exceed Issued Pcs",Notification.TYPE_WARNING_MESSAGE);
					txtTotalQty.get(ar).setValue("");
					txtTotalpcs.get(ar).setValue("");
					afShiftApcs.get(ar).setValue("");
					afShiftAQty.get(ar).setValue("");
					tbTxtStandardAmount.get(ar).setValue("");
				}
				else if(caption.equalsIgnoreCase("shiftB")){
					showNotification("Sorry!!","Total Pcs Exceed Issued Pcs",Notification.TYPE_WARNING_MESSAGE);
					txtTotalQty.get(ar).setValue("");
					txtTotalpcs.get(ar).setValue("");
					afShiftBpcs.get(ar).setValue("");
					afShiftBQty.get(ar).setValue("");
					tbTxtStandardAmount.get(ar).setValue("");
				}
			}
			else{
				txtTotalQty.get(ar).setValue(decFormat.format(totalQty));
				txtTotalpcs.get(ar).setValue(totalPcs);
				tbTxtStandardAmount.get(ar).setValue(decFormat2.format(standardAmount));

				txtTotalQty1.setValue(decFormat.format(totalQty));
				txtTotalPcs.setValue(totalPcs);

			}
		}
		else{
			txtTotalQty.get(ar).setValue(decFormat.format(totalQty));
			txtTotalpcs.get(ar).setValue(totalPcs);
			tbTxtStandardAmount.get(ar).setValue(decFormat2.format(standardAmount));

			txtTotalQty1.setValue(decFormat.format(totalQty));
			txtTotalPcs.setValue(totalPcs);
		}
	}
	private boolean doubleEntryCheck(int row)
	{
		String fgName = cmbSemiFG.get(row).getValue().toString();
		String machineName = cmbMachine.get(row).getValue().toString();
		String jobOrderName = tbCmbJobOrder.get(row).getValue().toString();
		String mouldName = tbCmbJobOrder.get(row).getValue().toString();

		for(int i=0;i<cmbSemiFG.size();i++)
		{
			if(cmbSemiFG.get(i).getValue()!=null){
				if(i!=row && fgName.equals(cmbSemiFG.get(i).getValue().toString())
						&& machineName.equals(cmbMachine.get(i).getValue().toString())&&
						jobOrderName.equals(tbCmbJobOrder.get(i).getValue().toString())&&
						mouldName.equals(cmbMould.get(i).getValue().toString()))
				{
					return false;
				}
			}
		}
		return true;
	}
	private boolean doubleEntryCheckSemiFgSub(int row)
	{
		String fgName = cmbSemiFG.get(row).getValue().toString();
		String machineName = cmbMachine.get(row).getValue().toString();
		String semiFgSubName = tbTxtStandardFlag.get(row).getValue().toString();

		for(int i=0;i<cmbSemiFG.size();i++)
		{
			if(tbTxtStandardFlag.get(i).getValue()!=null){
				if(i!=row && fgName.equals(cmbSemiFG.get(i).getValue().toString())
						&& machineName.equals(cmbMachine.get(i).getValue().toString())&&
						semiFgSubName.equals(tbTxtStandardFlag.get(i).getValue().toString()))
				{
					return false;
				}
			}
		}
		return true;
	}
	/*private void stepDataLoad(int ar){
		tbTxtStandardAmount.get(ar).removeAllItems();
		Iterator iter=dbService("select SubStepId,SubStepName from tbProductionSubStep where productionTypeId like '"+cmbProductionType.getValue()+"' " +
				"and productionStepId like '"+cmbProductionStep.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbTxtStandardAmount.get(ar).addItem(element[0]);
			tbTxtStandardAmount.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}*/
	/*private void subStepDataLoad(int ar){
		tbTxtStandardFlag.get(ar).removeAllItems();
		Iterator iter=dbService("select SubSubStepId,SubSubStepName from tbProductionSubSubStep where productionTypeId like " +
				"'"+cmbProductionType.getValue()+"' and productionStepId like '"+cmbProductionStep.getValue()+"' " +
				"and productionSubStepId like '"+tbTxtStandardAmount.get(ar).getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbTxtStandardFlag.get(ar).addItem(element[0]);
			tbTxtStandardFlag.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}*/
	private void totalRejCalc(int ar){
		double shiftA=Double.parseDouble(txtRejShiftA.get(ar).getValue().toString().isEmpty()?"0.0":txtRejShiftA.get(ar).getValue().toString());
		double shiftB=Double.parseDouble(txtRejShiftB.get(ar).getValue().toString().isEmpty()?"0.0":txtRejShiftB.get(ar).getValue().toString());
		double total=shiftA+shiftB;
		afWastageQty.get(ar).setValue(decFormat.format(total));
		txtTotalReject.setValue(afWastageQty.get(ar).getValue());
		txtRejectPercent.setValue(afWastagePercent.get(ar).getValue());
	}
	private void IssueRemainCalc(int ar){
		Iterator iter=dbService("select issueQty,remainQty,stdWeight,issuePcs,cycleTime,cavity,terget from funcMouldProductionLeftQty " +
				"('"+tbCmbJobOrder.get(ar).getValue()+"','"+cmbSemiFG.get(ar).getValue()+"','"+cmbMould.get(ar).getValue()+"','"+cmbMachine.get(ar).getValue()+"') ");
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtIssueQty.get(ar).setValue(decFormat.format(element[0]));
			txtIssueRemainQty.get(ar).setValue(decFormat.format(element[1]));
			tbtxtIssuepcs.get(ar).setValue(element[3]);
			stdWeight=Double.parseDouble(element[2].toString());

			txtIssueQty1.setValue(decFormat.format(element[0]));
			txtIssuePcs.setValue(df.format(element[3]));
			txtRemainQty.setValue(decFormat.format(element[1]));
			double remainPcs=Double.parseDouble(element[1].toString())/stdWeight;
			txtRemainPcs.setValue(df.format(remainPcs));
			txtCT.setValue(decFormat.format(element[4]));
			txtCavity.setValue(element[5]);
			txtTarget.setValue(decFormat.format(element[6]));
		}
		totalRejCalc(ar);
		double totalQty=Double.parseDouble(txtTotalQty.get(ar).getValue().toString().isEmpty()?"0.0":txtTotalQty.get(ar).getValue().toString());
		double totalPcs=Double.parseDouble(txtTotalpcs.get(ar).getValue().toString().isEmpty()?"0.0":txtTotalpcs.get(ar).getValue().toString());
		txtTotalQty1.setValue(decFormat.format(totalQty));
		txtTotalPcs.setValue(df.format(totalPcs));

	}
	private void mouldLoadData(int ar){
		cmbMould.get(ar).removeAllItems();
		Iterator iter=dbService("select distinct a.mouldId,a.mouldName from tbMixtureIssueEntryDetailsEntry a   "+
				" where a.jobOrderNo like '"+tbCmbJobOrder.get(ar).getValue()+"' and a.semiFgId like '"+cmbSemiFG.get(ar).getValue()+"' and mouldId is not null union  select b.mouldId, "+
				" (select mouldName from tbmouldInfo where mouldid=b.mouldId) from tbProductionOpening b where mouldId not like '' and inputProductId like '"+cmbSemiFG.get(ar).getValue()+"'");

		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbMould.get(ar).addItem(element[0]);
			cmbMould.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}
	private void semiFgLoadData(int ar,String jobOrder){
		cmbSemiFG.get(ar).removeAllItems();
		Iterator iter=dbService("select distinct semiFgid,semiFgName from tbMixtureIssueEntryDetailsEntry where jobOrderNo like '"+jobOrder+"' "+
				" union select distinct inputProductId,inputProductName from tbProductionOpening where jobOrderNo like " +
				"'"+jobOrder+"' and inputProductId not like '%RI-%' "+
				" union select distinct outputProductId,outputProductName from " +
				"tbProductionOpening where jobOrderNo like '"+tbCmbJobOrder.get(ar)+"' and outputProductId not like '%SemiFgSub-%'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbSemiFG.get(ar).addItem(element[0]);
			cmbSemiFG.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}
	/*private void semiFgSubLoadData(int ar){
		tbTxtStandardFlag.get(ar).removeAllItems();
		Iterator iter=dbService("select semiFgSubId,semiFgSubName from tbSemiFgSubInformation where semiFgId like '"+cmbSemiFG.get(ar).getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbTxtStandardFlag.get(ar).addItem(element[0]);
			tbTxtStandardFlag.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}*/
	private void IssueRemainCalcWithOutMoulding(int ar){
		Iterator iter=dbService("select IssuePcs,floorStock from funcMouldProductionStockWithoutMoulding" +
				"('"+cmbProductionStep.getValue()+"','"+cmbSemiFG.get(ar).getValue()+"' )");
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbtxtIssuepcs.get(ar).setValue(decFormat.format(element[0]));
			txtIssuePcs.setValue(decFormat.format(element[0]));
			txtRemainPcs.setValue(decFormat.format(element[1]));
			txtIssueQty1.setValue("");
			txtRemainQty.setValue("");
		}
	}
	/*private void machineModelLoad(int ar){
		tbTxtStandardNo.get(ar).removeAllItems();
		Iterator iter=dbService("  select machineModel from tbMachineInfo where vMachineCode='"+cmbMachine.get(ar)+"'");
		while(iter.hasNext()){
			//Object element[]=(Object[])iter.next();
			tbTxtStandardNo.get(ar).addItem(iter.next());
		}
	}*/
	private void tableRowAdd(final int ar)

	{
		try
		{
			lblSl.add(ar, new Label());
			lblSl.get(ar).setWidth("100%");
			lblSl.get(ar).setValue(ar+1);

			chkShow.add(ar,new CheckBox());
			chkShow.get(ar).setWidth("100%");
			chkShow.get(ar).setImmediate(true);

			chkShow.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(chkShow.get(ar).booleanValue()){
						for(int x=0;x<chkShow.size();x++){
							if(x!=ar){
								chkShow.get(x).setValue(false);
							}
						}
						if(!(cmbProductionStep.getValue().toString().equalsIgnoreCase("BlowSTP-1")||
								cmbProductionStep.getValue().toString().equalsIgnoreCase("InjectionSTP-1")||
								cmbProductionStep.getValue().toString().equalsIgnoreCase("Injection Blow STP-1"))){
							IssueRemainCalcWithOutMoulding(ar);
						}
						else{
							IssueRemainCalc(ar);
						}

					}
				}
			});

			chkFG.add(ar,new CheckBox());
			chkFG.get(ar).setWidth("100%");
			chkFG.get(ar).setImmediate(true);

			tbTxtStandardAmount.add(ar, new TextRead());
			tbTxtStandardAmount.get(ar).setWidth("100%");
			tbTxtStandardAmount.get(ar).setImmediate(true);
			//stepDataLoad(ar);

			tbTxtStandardFlag.add(ar, new TextRead());
			tbTxtStandardFlag.get(ar).setWidth("100%");
			tbTxtStandardFlag.get(ar).setImmediate(true);
			//tbTxtStandardFlag.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			//subStepDataLoad(ar);
			/*tbTxtStandardFlag.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(tbTxtStandardFlag.get(ar).getValue()!=null)
					{
						if(!(cmbProductionStep.getValue().toString().equalsIgnoreCase("BlowSTP-1")||
								cmbProductionStep.getValue().toString().equalsIgnoreCase("InjectionSTP-1")||
								cmbProductionStep.getValue().toString().equalsIgnoreCase("Injection Blow STP-1")))
						{
							if(!doubleEntryCheckSemiFgSub(ar))
							{
								cmbSemiFG.get(ar).setValue(null);
								cmbMachine.get(ar).setValue(null);
								tbTxtStandardFlag.get(ar).setValue(null);
								getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
								tbTxtStandardFlag.get(ar).focus();
							}
						}
					}
				}
			});*/

			/*tbTxtStandardAmount.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(tbTxtStandardAmount.get(ar).getValue()!=null){
						subStepDataLoad(ar);
					}
				}
			});*/
			tbCmbJobOrder.add(ar, new ComboBox());
			tbCmbJobOrder.get(ar).setWidth("100%");
			tbCmbJobOrder.get(ar).setImmediate(true);
			tbCmbJobOrder.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			cmbJobOrderLoadData(ar);
			tbCmbJobOrder.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(tbCmbJobOrder.get(ar).getValue()!=null){
						//mouldLoadData(ar);
						/*String sql="select machineId,machineName from tbMixtureIssueEntryDetailsEntry " +
								"where jobOrderNo like '"+tbCmbJobOrder.get(ar).getValue().toString()+"'";
						*/
						String sql="select machineId,(select vMachineName from tbMachineInfo where vMachineCode=machineId) macName from tbMixtureIssueEntryDetailsEntry " +
								"where jobOrderNo like '"+tbCmbJobOrder.get(ar).getValue().toString()+"'";
						
						machineDataLoad(ar,sql);
						semiFgLoadData(ar,tbCmbJobOrder.get(ar).getValue().toString());
					}
					else{
						//cmbMachine.get(ar).removeAllItems();
						String sql="select vMachineCode,vMachineName from tbMachineInfo order by vMachineName";
						machineDataLoad(ar,sql);
						cmbSemiFG.get(ar).removeAllItems();
					}
				}
			});
			cmbMachine.add(ar, new ComboBox());
			cmbMachine.get(ar).setWidth("100%");
			cmbMachine.get(ar).setImmediate(true);
			cmbMachine.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			String sql="select vMachineCode,vMachineName from tbMachineInfo order by vMachineName";
			machineDataLoad(ar,sql);

			cmbMachine.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(cmbMachine.get(ar).getValue()!=null){
						//machineModelLoad(ar);
						if(!(cmbProductionStep.getValue().toString().equalsIgnoreCase("BlowSTP-1")||
								cmbProductionStep.getValue().toString().equalsIgnoreCase("InjectionSTP-1")||
								cmbProductionStep.getValue().toString().equalsIgnoreCase("Injection Blow STP-1"))){
							semiFgLoadData(ar,"%");
						}
						if(cmbMachine.get(ar).getValue()!=null&&tbCmbJobOrder.get(ar).getValue()!=null&&cmbSemiFG.get(ar).getValue()!=null){
							if(!doubleEntryCheck(ar)){
								cmbMachine.get(ar).setValue(null);
								getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
								cmbMachine.get(ar).focus();
							}
						}
					}
					else{
						//machineModelLoad(ar);
						//tbTxtStandardNo.get(ar).removeAllItems();
					}
				}
			});

			tbTxtStandardNo.add(ar, new TextRead());
			tbTxtStandardNo.get(ar).setWidth("100%");
			tbTxtStandardNo.get(ar).setImmediate(true);
			//tbTxtStandardNo.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);



			cmbSemiFG.add(ar, new ComboBox());
			cmbSemiFG.get(ar).setWidth("100%");
			cmbSemiFG.get(ar).setImmediate(true);
			cmbSemiFG.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			cmbSemiFG.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(cmbSemiFG.get(ar).getValue()!=null&&tbCmbJobOrder.get(ar).getValue()!=null&&cmbMachine.get(ar).getValue()!=null){
						if(doubleEntryCheck(ar)){
							//semiFgSubLoadData(ar);
							mouldLoadData(ar);
							//checkFg(ar);
						}
						else{
							cmbSemiFG.get(ar).setValue(null);
							getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							cmbSemiFG.get(ar).focus();
						}
					}
					//For Printing or Labeling
					if(cmbSemiFG.get(ar).getValue()!=null&&cmbMachine.get(ar).getValue()!=null){
						if(!(cmbProductionStep.getValue().toString().equalsIgnoreCase("BlowSTP-1")||
								cmbProductionStep.getValue().toString().equalsIgnoreCase("InjectionSTP-1")||
								cmbProductionStep.getValue().toString().equalsIgnoreCase("Injection Blow STP-1")))
						{
							IssueRemainCalcWithOutMoulding(ar);
							//semiFgSubLoadData(ar);
						}
					}
				}
			});

			txtIssueQty.add(ar, new TextRead(1));
			txtIssueQty.get(ar).setWidth("100%");
			txtIssueQty.get(ar).setImmediate(true);

			txtIssueRemainQty.add(ar,new TextRead(1));
			txtIssueRemainQty.get(ar).setWidth("100%");
			txtIssueRemainQty.get(ar).setImmediate(true);

			tbtxtIssuepcs.add(ar, new TextRead(1));
			tbtxtIssuepcs.get(ar).setWidth("100%");
			tbtxtIssuepcs.get(ar).setImmediate(true);

			afShiftAQty.add(ar, new TextRead(1));
			afShiftAQty.get(ar).setWidth("100%");
			afShiftAQty.get(ar).setImmediate(true);

			afShiftApcs.add(ar, new AmountField());
			afShiftApcs.get(ar).setWidth("100%");
			afShiftApcs.get(ar).setImmediate(true);
			afShiftApcs.get(ar).addListener(new ValueChangeListener() 
			{

				public void valueChange(ValueChangeEvent event) 
				{
					double Apcs=Double.parseDouble(afShiftApcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftApcs.get(ar).getValue().toString());
					double AQty=Apcs*stdWeight;
					afShiftAQty.get(ar).setValue(decFormat.format(AQty));
					totlaQtyCalc(ar,"shiftA");;
					
					if (!isUpdate)
					{
						Date date1= new Date();
						//Date date2= (Date) dProductionDate.getValue();
						String strDate=dFormatSql.format(dProductionDate.getValue());
						DateFormat formatter;
						Date date2 = null;
						formatter = new SimpleDateFormat("yyyy-MM-dd");
						try {
							date2 = formatter.parse(strDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						long seconds = (date1.getTime()-date2.getTime())/1000;
						 System.out.println("second is:"+seconds);
						 
						if (!sessionBean.getUserId().toString().equalsIgnoreCase("22") && seconds>259199  )
						{
						   showNotification("Not Permited TO Entry Time Limit Exceed")	;
						   afShiftApcs.get(ar).setValue("");
						}
						
						
						
					}
					
				}
			});
			
			
			

		/*	afShiftApcs.get(ar).addListener(new ValueChangeListener() 
			{

				public void valueChange(ValueChangeEvent event) 
				{

					double Apcs=Double.parseDouble(afShiftApcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftApcs.get(ar).getValue().toString());
					double AQty=Apcs*stdWeight;
					afShiftAQty.get(ar).setValue(decFormat.format(AQty));
					totlaQtyCalc(ar,"shiftA");
				}
			});*/

			afShiftBQty.add(ar, new TextRead(1));
			afShiftBQty.get(ar).setWidth("100%");
			afShiftBQty.get(ar).setImmediate(true);

			afShiftBpcs.add(ar, new AmountField());
			afShiftBpcs.get(ar).setWidth("100%");
			afShiftBpcs.get(ar).setImmediate(true);

			afShiftBpcs.get(ar).addListener(new ValueChangeListener() 
			{

				public void valueChange(ValueChangeEvent event) 
				{
					double Bpcs=Double.parseDouble(afShiftBpcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftBpcs.get(ar).getValue().toString());
					double BQty=Bpcs*stdWeight;
					afShiftBQty.get(ar).setValue(decFormat.format(BQty));
					totlaQtyCalc(ar,"shiftB");
					
					/*if (!isUpdate)
					{
						Date date1= new Date();
						 Date date2= (Date) dProductionDate.getValue();
						 long seconds = (date1.getTime()-date2.getTime())/1000;
						 System.out.println("second is:"+seconds);	
						if (!sessionBean.isSuperAdmin() && seconds>172799  )
						{
						   showNotification("Not Permited TO Entry Time Limit Exceed")	;
						   afShiftBpcs.get(ar).setValue("");
							
						}
						
					}*/
					
					
					if (!isUpdate)
					{
						Date date1= new Date();
						//Date date2= (Date) dProductionDate.getValue();
						String strDate=dFormatSql.format(dProductionDate.getValue());
						DateFormat formatter;
						Date date2 = null;
						formatter = new SimpleDateFormat("yyyy-MM-dd");
						try {
							date2 = formatter.parse(strDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						
						
						long seconds = (date1.getTime()-date2.getTime())/1000;
						 System.out.println("second is:"+seconds);
						 
						if (!sessionBean.getUserId().toString().equalsIgnoreCase("22") && seconds>259199 )
						{
						   showNotification("Not Permited TO Entry Time Limit Exceed")	;
						   afShiftBpcs.get(ar).setValue("");
						}
						
						
						
					}
					
					
					
					
					
				}
			});

			txtTotalQty.add(ar, new TextRead(1));
			txtTotalQty.get(ar).setWidth("100%");
			txtTotalQty.get(ar).setImmediate(true);

			txtTotalpcs.add(ar, new TextRead(1));
			txtTotalpcs.get(ar).setWidth("100%");
			txtTotalpcs.get(ar).setImmediate(true);

			afWastageQty.add(ar, new AmountField());
			afWastageQty.get(ar).setWidth("100%");
			afWastageQty.get(ar).setImmediate(true);

			afWastageQty.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					double Wstpcs=Double.parseDouble(afWastageQty.get(ar).getValue().toString().isEmpty()?"0.0":afWastageQty.get(ar).getValue().toString());
					double Issuepcs=Double.parseDouble(tbtxtIssuepcs.get(ar).getValue().toString().isEmpty()?"0.0":tbtxtIssuepcs.get(ar).getValue().toString());
					double WstQty=Wstpcs*stdWeight;
					double wstPercent=0.0;

					if(Wstpcs!=0&&Issuepcs!=0){
						wstPercent=(100*Wstpcs)/Issuepcs;
					}

					afWastagePercent.get(ar).setValue(decFormat.format(wstPercent));
					afWastageTotal.get(ar).setValue(decFormat.format(WstQty));
				}
			});

			afWastagePercent.add(ar, new AmountField());
			afWastagePercent.get(ar).setWidth("100%");
			afWastagePercent.get(ar).setImmediate(true);

			afWastageTotal.add(ar, new TextRead(1));
			afWastageTotal.get(ar).setWidth("100%");
			afWastageTotal.get(ar).setImmediate(true);

			/*txtColor.add(ar, new TextField());
			txtColor.get(ar).setWidth("100%");
			txtColor.get(ar).setImmediate(true);

			txtCavity.add(ar, new AmountField());
			txtCavity.get(ar).setWidth("100%");
			txtCavity.get(ar).setImmediate(true);

			txtCT.add(ar, new AmountField());
			txtCT.get(ar).setWidth("100%");
			txtCT.get(ar).setImmediate(true);

			txtTarget.add(ar, new AmountField());
			txtTarget.get(ar).setWidth("100%");
			txtTarget.get(ar).setImmediate(true);*/

			tbtxtCT.add(ar, new AmountField());
			tbtxtCT.get(ar).setWidth("100%");
			tbtxtCT.get(ar).setImmediate(true);

			tbtxtCavity.add(ar, new AmountField());
			tbtxtCavity.get(ar).setWidth("100%");
			tbtxtCavity.get(ar).setImmediate(true);

			txtRejShiftA.add(ar, new AmountField());
			txtRejShiftA.get(ar).setWidth("100%");
			txtRejShiftA.get(ar).setImmediate(true);

			txtRejShiftA.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					totalRejCalc(ar);
				}
			});

			txtRejShiftB.add(ar, new AmountField());
			txtRejShiftB.get(ar).setWidth("100%");
			txtRejShiftB.get(ar).setImmediate(true);

			txtRejShiftB.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					totalRejCalc(ar);
				}
			});

			cmbMould.add(ar, new ComboBox());
			cmbMould.get(ar).setWidth("100%");
			cmbMould.get(ar).setImmediate(true);
			cmbMould.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			cmbMould.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(cmbMould.get(ar).getValue()!=null){
						if(cmbProductionStep.getValue().toString().equalsIgnoreCase("BlowSTP-1")||cmbProductionStep.getValue().toString().equalsIgnoreCase("InjectionSTP-1")||cmbProductionStep.getValue().toString().equalsIgnoreCase("Injection Blow STP-1")){
							IssueRemainCalc(ar);
							//checkFg(ar);
						}
						if(ar==cmbMachine.size()-1){
							tableRowAdd(ar+1);
						}
					}
				}
			});

			tbTxtStandardPrice.add(ar, new TextRead());
			tbTxtStandardPrice.get(ar).setWidth("100%");
			tbTxtStandardPrice.get(ar).setImmediate(true);

			/*txtBreakDownTime.add(ar, new TextField());
			txtBreakDownTime.get(ar).setWidth("100%");
			txtBreakDownTime.get(ar).setImmediate(true);

			txtBreakReason.add(ar, new TextField());
			txtBreakReason.get(ar).setWidth("100%");
			txtBreakReason.get(ar).setImmediate(true);

			txtRemarks.add(ar, new TextField());
			txtRemarks.get(ar).setWidth("100%");
			txtRemarks.get(ar).setImmediate(true);*/

			chkFG.get(ar).setEnabled(false);
			chkShow.get(ar).setEnabled(false);
			tbCmbJobOrder.get(ar).setEnabled(false);
			cmbMachine.get(ar).setEnabled(false);
			cmbSemiFG.get(ar).setEnabled(false);
			tbTxtStandardFlag.get(ar).setEnabled(false);
			cmbMould.get(ar).setEnabled(false);
			chkFG.get(ar).setEnabled(!false);
			tbTxtStandardPrice.get(ar).setEnabled(false);

			table.addItem(new Object[] {lblSl.get(ar),chkShow.get(ar),chkFG.get(ar), tbTxtStandardAmount.get(ar),tbCmbJobOrder.get(ar),cmbMachine.get(ar),tbTxtStandardNo.get(ar),
					cmbSemiFG.get(ar),tbTxtStandardFlag.get(ar),cmbMould.get(ar),tbtxtCT.get(ar),tbtxtCavity.get(ar),txtIssueQty.get(ar),txtIssueRemainQty.get(ar),
					tbtxtIssuepcs.get(ar),afShiftApcs.get(ar),txtRejShiftA.get(ar), afShiftAQty.get(ar), afShiftBpcs.get(ar),txtRejShiftB.get(ar),
					afShiftBQty.get(ar), txtTotalpcs.get(ar),txtTotalQty.get(ar),  afWastageQty.get(ar), afWastagePercent.get(ar), afWastageTotal.get(ar),
					tbTxtStandardPrice.get(ar)}, ar);

		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	private void finishedProductLoadData(int ar) {
		cmbSemiFG.get(ar).removeAllItems();
		String sql="select distinct fgCode,(select vProductName from tbFinishedProductInfo where vProductId=fgCode)as name " +
				"from tbMouldIssueDetails where JoborderNo='"+tbCmbJobOrder.get(ar).getValue()+"'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbSemiFG.get(ar).addItem(element[0]);
			cmbSemiFG.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}
	private void machineDataLoad(int ar,String sql) 
	{
		cmbMachine.get(ar).removeAllItems();
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbMachine.get(ar).addItem(element[0]);
			cmbMachine.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}
	private AbsoluteLayout buildMainLayout()
	{
		// mainLayout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		setWidth("100%");
		setHeight("600px");

		// lblProductionNo
		lblProductionNo = new Label("Production No :");
		lblProductionNo.setImmediate(false);
		lblProductionNo.setWidth("-1px");
		lblProductionNo.setHeight("-1px");

		// txtProductionNo
		txtProductionNo = new TextRead();
		txtProductionNo.setImmediate(true);
		txtProductionNo.setWidth("80px");
		txtProductionNo.setHeight("22px");

		// lblProductionDate
		lblProductionDate = new Label("Production Date :");
		lblProductionDate.setImmediate(false);
		lblProductionDate.setWidth("-1px");
		lblProductionDate.setHeight("-1px");

		// dProductionDate
		dProductionDate = new PopupDateField();
		dProductionDate.setImmediate(false);
		dProductionDate.setWidth("-1px");
		dProductionDate.setHeight("-1px");
		dProductionDate.setDateFormat("dd-MM-yyyy");
		dProductionDate.setValue(new java.util.Date());
		dProductionDate.setResolution(PopupDateField.RESOLUTION_DAY);

		// lblProductionSteop
		lblProductionType = new Label("Production Type :");
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");

		// cmbProductionStep
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("150px");
		cmbProductionType.setNullSelectionAllowed(false);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		// lblProductionSteop
		lblMixtureDate = new Label("Mixture Date :");
		lblMixtureDate.setImmediate(false);
		lblMixtureDate.setWidth("-1px");
		lblMixtureDate.setHeight("-1px");

		// cmbProductionStep
		cmbMixtureDate = new ComboBox();
		cmbMixtureDate.setImmediate(true);
		cmbMixtureDate.setNullSelectionAllowed(true);
		cmbProductionType.setWidth("150px");
		cmbMixtureDate.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbMixtureDate.setEnabled(false);

		// lblProductionSteop
		lblProductionStep = new Label("Production Step :");
		lblProductionStep.setImmediate(false);
		lblProductionStep.setWidth("-1px");
		lblProductionStep.setHeight("-1px");

		// cmbProductionStep
		cmbProductionStep = new ComboBox();
		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setNullSelectionAllowed(false);
		cmbProductionStep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		//cmbProductionStep.setWidth("-1px");
		//cmbProductionStep.setHeight("-1px");

		/*lbljoborderNo = new Label("Job Order No :");
		lbljoborderNo.setImmediate(false);
		lbljoborderNo.setWidth("-1px");
		lbljoborderNo.setHeight("-1px");

		// cmbProductionStep
		cmbjoborderNO = new ComboBox();
		cmbjoborderNO.setImmediate(true);
		cmbjoborderNO.setNullSelectionAllowed(false);*/

		// lblIssueNo
		lblIssueNo = new Label("R/M Issue No :");
		lblIssueNo.setImmediate(false);
		lblIssueNo.setWidth("-1px");
		lblIssueNo.setHeight("-1px");


		cmbIssueNo = new ComboBox();
		cmbIssueNo.setImmediate(true);
		cmbIssueNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		// txtIssue
		txtIssue = new TextRead();
		txtIssue.setImmediate(true);
		txtIssue.setWidth("200px");
		txtIssue.setHeight("20px");
		txtIssue.setStyleName("txtcolor");

		lblIssueQty = new Label("Issue Qty :");
		lblIssueQty.setImmediate(false);
		lblIssueQty.setWidth("-1px");
		lblIssueQty.setHeight("-1px");

		txtIssueQty1 = new TextRead();
		txtIssueQty1.setImmediate(true);
		txtIssueQty1.setWidth("100px");
		txtIssueQty1.setHeight("20px");
		//txtIssueQty1.setStyleName("txtcolor");

		lblIssuePcs = new Label("Issue Pcs :");
		lblIssuePcs.setImmediate(false);
		lblIssuePcs.setWidth("-1px");
		lblIssuePcs.setHeight("-1px");

		txtIssuePcs = new TextRead();
		txtIssuePcs.setImmediate(true);
		txtIssuePcs.setWidth("100px");
		txtIssuePcs.setHeight("20px");
		//txtIssuePcs.setStyleName("txtcolor");

		lblRemainQty = new Label("Remain Qty :");
		lblRemainQty.setImmediate(false);
		lblRemainQty.setWidth("-1px");
		lblRemainQty.setHeight("-1px");

		txtRemainQty = new TextRead();
		txtRemainQty.setImmediate(true);
		txtRemainQty.setWidth("100px");
		txtRemainQty.setHeight("20px");
		//txtRemainQty.setStyleName("txtcolor");

		lblRemainPcs = new Label("Remain Pcs :");
		lblRemainPcs.setImmediate(false);
		lblRemainPcs.setWidth("-1px");
		lblRemainPcs.setHeight("-1px");

		// txtShiftB
		txtRemainPcs = new TextRead();
		txtRemainPcs.setImmediate(true);
		txtRemainPcs.setWidth("100px");
		txtRemainPcs.setHeight("20px");
		//txtRemainPcs.setStyleName("txtcolor");


		lblTotalQty = new Label("Total Qty :");
		lblTotalQty.setImmediate(false);
		lblTotalQty.setWidth("-1px");
		lblTotalQty.setHeight("-1px");

		txtTotalQty1 = new TextRead();
		txtTotalQty1.setImmediate(true);
		txtTotalQty1.setWidth("100px");
		txtTotalQty1.setHeight("20px");
		//txtIssuePcs.setStyleName("txtcolor");

		lblTotalPcs = new Label("Total Pcs :");
		lblTotalPcs.setImmediate(false);
		lblTotalPcs.setWidth("-1px");
		lblTotalPcs.setHeight("-1px");

		txtTotalPcs = new TextRead();
		txtTotalPcs.setImmediate(true);
		txtTotalPcs.setWidth("100px");
		txtTotalPcs.setHeight("20px");
		//txtRemainQty.setStyleName("txtcolor");

		lblTotalReject = new Label("Total Reject :");
		lblTotalReject.setImmediate(false);
		lblTotalReject.setWidth("-1px");
		lblTotalReject.setHeight("-1px");

		// txtShiftB
		txtTotalReject = new TextRead();
		txtTotalReject.setImmediate(true);
		txtTotalReject.setWidth("100px");
		txtTotalReject.setHeight("20px");

		lblRejectPercent = new Label("Reject %:");
		lblRejectPercent.setImmediate(false);
		lblRejectPercent.setWidth("-1px");
		lblRejectPercent.setHeight("-1px");

		// txtShiftB
		txtRejectPercent = new TextRead();
		txtRejectPercent.setImmediate(true);
		txtRejectPercent.setWidth("100px");
		txtRejectPercent.setHeight("20px");

		lblCavity = new Label("Cavity :");
		lblCavity.setImmediate(false);
		lblCavity.setWidth("-1px");
		lblCavity.setHeight("-1px");

		// txtShiftB
		txtCavity = new TextRead();
		txtCavity.setImmediate(true);
		txtCavity.setWidth("100px");
		txtCavity.setHeight("20px");

		lblCT = new Label("CT :");
		lblCT.setImmediate(false);
		lblCT.setWidth("-1px");
		lblCT.setHeight("-1px");

		// txtShiftB
		txtCT = new TextRead();
		txtCT.setImmediate(true);
		txtCT.setWidth("100px");
		txtCT.setHeight("20px");

		lblTarget = new Label("Target :");
		lblTarget.setImmediate(false);
		lblTarget.setWidth("-1px");
		lblTarget.setHeight("-1px");

		// txtShiftB
		txtTarget = new TextRead();
		txtTarget.setImmediate(true);
		txtTarget.setWidth("100px");
		txtTarget.setHeight("20px");

		lblVoucherNo = new Label("VoucherNo :");
		lblVoucherNo.setImmediate(false);
		lblVoucherNo.setWidth("-1px");
		lblVoucherNo.setHeight("-1px");

		// txtShiftB
		txtVoucherNo = new TextRead();
		txtVoucherNo.setImmediate(true);
		txtVoucherNo.setWidth("100px");
		txtVoucherNo.setHeight("20px");

		// txtTotal
		/*txtTotal = new TextRead();
		txtTotal.setImmediate(true);
		txtTotal.setWidth("115px");
		txtTotal.setHeight("20px");
		txtTotal.setStyleName("txtcolor");

		// txtWastage
		txtWastage = new TextRead();
		txtWastage.setImmediate(true);
		txtWastage.setWidth("180px");
		txtWastage.setHeight("20px");
		txtWastage.setStyleName("txtcolor");*/

		// lblIssue
		lblIssue = new Label("<b><font color='#fff'>ISSUE</font></b>", Label.CONTENT_XHTML);
		lblIssue.setImmediate(false);
		lblIssue.setWidth("-1px");
		lblIssue.setHeight("-1px");

		/*// lblShiftA
		lblShiftA = new Label("<b><font color='#fff'>SHIFT A</font></b>", Label.CONTENT_XHTML);
		lblShiftA.setImmediate(false);
		lblShiftA.setWidth("-1px");
		lblShiftA.setHeight("-1px");

		// lblShiftB
		lblShiftB = new Label("<b><font color='#fff'>SHIFT B</font></b>", Label.CONTENT_XHTML);
		lblShiftB.setImmediate(false);
		lblShiftB.setWidth("-1px");
		lblShiftB.setHeight("-1px");

		// lblTotal
		lblTotal = new Label("<b><font color='#fff'>TOTAL</font></b>", Label.CONTENT_XHTML);
		lblTotal.setImmediate(false);
		lblTotal.setWidth("-1px");
		lblTotal.setHeight("-1px");

		// lblWastage
		lblWastage = new Label("<b><font color='#fff'>WASTAGE</font></b>", Label.CONTENT_XHTML);
		lblWastage.setImmediate(false);
		lblWastage.setWidth("-1px");
		lblWastage.setHeight("-1px");*/

		optiongroup1=new OptionGroup("", lst1);
		optiongroup1.select(a1[0].toString());
		optiongroup1.setStyleName("vertical");

		btnReport= new NativeButton("Show Report");
		btnReport.setImmediate(false);
		btnReport.setIcon(new ThemeResource("../icons/preview.png"));
		btnReport.setImmediate(true);
		btnReport.setWidth("130px");
		btnReport.setHeight("24px");

		table = new Table();
		table.setColumnCollapsingAllowed(true);
		table.setFooterVisible(true);
		table.setWidth("100%");
		table.setHeight("315px");

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 10);

		table.addContainerProperty("Show", CheckBox.class, new CheckBox());
		table.setColumnWidth("Show", 30);

		table.addContainerProperty("Is FG", CheckBox.class, new CheckBox());
		table.setColumnWidth("Is FG", 20);
		table.setColumnCollapsed("Is FG", true);

		table.addContainerProperty("StandardAmount", TextRead.class, new TextRead());
		table.setColumnWidth("StandardAmount", 110);
		table.setColumnCollapsed("StandardAmount", true);

		table.addContainerProperty("JOb Order", ComboBox.class, new ComboBox());
		table.setColumnWidth("JOb Order", 160);


		table.addContainerProperty("Machine", ComboBox.class, new ComboBox());
		table.setColumnWidth("Machine", 140);

		table.addContainerProperty("StandardNo", TextRead.class, new TextRead());
		table.setColumnWidth("StandardNo", 100);
		table.setColumnCollapsed("StandardNo", true);

		table.addContainerProperty("Product Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Product Name", 250);

		table.addContainerProperty("StandardFlag", TextRead.class, new TextRead());
		table.setColumnWidth("StandardFlag", 110);
		table.setColumnCollapsed("StandardFlag", true);

		table.addContainerProperty("Mould Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Mould Name", 180);

		/*table.addContainerProperty("Color", TextField.class, new TextField());
		table.setColumnWidth("Color", 50);
		table.setColumnCollapsed("Color", true);

		table.addContainerProperty("Cavity", AmountField.class, new AmountField());
		table.setColumnWidth("Cavity", 40);

		table.addContainerProperty("CT(SEC)", AmountField.class, new AmountField());
		table.setColumnWidth("CT(SEC)",40);

		table.addContainerProperty("Target", AmountField.class, new AmountField());
		table.setColumnWidth("Target",50);*/
		table.addContainerProperty("CT(SEC)", AmountField.class, new AmountField());
		table.setColumnWidth("CT(SEC)",40);

		table.addContainerProperty("Cavity", AmountField.class, new AmountField());
		table.setColumnWidth("Cavity", 40);
		//table.setColumnCollapsed("CT(SEC)", true);

		table.addContainerProperty("Issue QTY ", TextRead.class, new TextRead());
		table.setColumnWidth("Issue QTY ", 55);
		table.setColumnCollapsed("Issue QTY ", true);

		table.addContainerProperty("Remain QTY ", TextRead.class, new TextRead());
		table.setColumnWidth("Remain QTY ", 55);
		table.setColumnCollapsed("Remain QTY ", true);

		table.addContainerProperty(" pcs", TextRead.class, new TextRead());
		table.setColumnWidth(" pcs", 55);
		table.setColumnCollapsed(" pcs", true);

		// Shift A
		table.addContainerProperty("Shift A Pcs", AmountField.class, new AmountField());
		table.setColumnWidth("Shift A Pcs",65);

		table.addContainerProperty("Reject A", AmountField.class, new AmountField());
		table.setColumnWidth("Reject A", 50);

		table.addContainerProperty("ShiftA QTY", TextRead.class, new TextRead(1));
		table.setColumnWidth("ShiftA QTY", 50);
		table.setColumnCollapsed("ShiftA QTY", true);


		// Shift B
		table.addContainerProperty("Shift B Pcs", AmountField.class, new AmountField());
		table.setColumnWidth("Shift B Pcs",65);

		table.addContainerProperty("Reject B", AmountField.class, new AmountField());
		table.setColumnWidth("Reject B", 50);

		table.addContainerProperty("ShiftB QTY", TextRead.class, new TextRead(1));
		table.setColumnWidth("ShiftB QTY", 50);
		table.setColumnCollapsed("ShiftB QTY", true);


		// Total(A+B)
		table.addContainerProperty("Total pcs", TextRead.class, new TextRead());
		table.setColumnWidth("Total pcs", 60);
		table.setColumnCollapsed("Total pcs", true);

		table.addContainerProperty("Total  QTY", TextRead.class, new TextRead());
		table.setColumnWidth("Total  QTY", 55);
		table.setColumnCollapsed("Total  QTY", true);


		// wastage
		table.addContainerProperty("Reject PCS", AmountField.class, new AmountField());
		table.setColumnWidth("Reject PCS", 50);
		table.setColumnCollapsed("Reject PCS", true);

		table.addContainerProperty("Reject %", AmountField.class, new AmountField());
		table.setColumnWidth("Reject %", 55);
		table.setColumnCollapsed("Reject %", true);

		table.addContainerProperty("Reject   QTY", TextRead.class, new TextRead());
		table.setColumnWidth("Reject   QTY", 55);
		table.setColumnCollapsed("Reject   QTY", true);


		table.addContainerProperty("StandardPrice", TextRead.class, new TextRead());
		table.setColumnWidth("StandardPrice", 60);
		table.setColumnCollapsed("StandardPrice", true);

		/*table.addContainerProperty("B.D. Time", TextField.class, new TextField());
		table.setColumnWidth("B.D. Time", 50);

		table.addContainerProperty("B.D. Reason", TextField.class, new TextField());
		table.setColumnWidth("B.D. Reason", 100);

		table.addContainerProperty("Remarks", TextField.class, new TextField());
		table.setColumnWidth("Remarks", 140);*/

		// adding components to mainLayout (distance: 30px)
		mainLayout.addComponent(lblProductionNo, "top: 20px; left: 20px;");
		mainLayout.addComponent(txtProductionNo, "top: 18px; left: 130px;");

		mainLayout.addComponent(lblProductionDate, "top: 50px; left: 20px;");
		mainLayout.addComponent(dProductionDate, "top: 48px; left: 130px;");

		mainLayout.addComponent(lblProductionStep, "top: 110px; left: 20px;");
		mainLayout.addComponent(cmbProductionStep, "top: 108px; left: 130px;");

		mainLayout.addComponent(lblProductionType, "top: 80px; left: 20px;");
		mainLayout.addComponent(cmbProductionType, "top: 78px; left: 130px;");

		mainLayout.addComponent(lblMixtureDate, "top: 80px; left: 297px;");
		mainLayout.addComponent(cmbMixtureDate, "top: 78px; left: 377px;");

		mainLayout.addComponent(optiongroup1, "top: 20px; left: 400px;");

		mainLayout.addComponent(lblIssueQty, "top: 20px; left: 560px;");
		mainLayout.addComponent(txtIssueQty1, "top: 18px; left: 660px;");

		mainLayout.addComponent(lblRemainQty, "top: 50px; left: 560px;");
		mainLayout.addComponent(txtRemainQty, "top: 48px; left: 660px;");

		mainLayout.addComponent(lblIssuePcs, "top: 80px; left: 560px;");
		mainLayout.addComponent(txtIssuePcs, "top: 78px; left: 660px;");

		mainLayout.addComponent(lblRemainPcs, "top: 110px; left: 560px;");
		mainLayout.addComponent(txtRemainPcs, "top: 108px; left: 660px;");

		mainLayout.addComponent(lblTotalQty, "top: 20px; left: 770px;");
		mainLayout.addComponent(txtTotalQty1, "top: 18px; left: 870px;");

		mainLayout.addComponent(lblTotalPcs, "top: 50px; left: 770px;");
		mainLayout.addComponent(txtTotalPcs, "top: 48px; left: 870px;");

		mainLayout.addComponent(lblTotalReject, "top: 80px; left: 770px;");
		mainLayout.addComponent(txtTotalReject, "top: 78px; left: 870px;");

		mainLayout.addComponent(lblRejectPercent, "top: 110px; left: 770px;");
		mainLayout.addComponent(txtRejectPercent, "top: 108px; left: 870px;");

		mainLayout.addComponent(lblCavity, "top: 20px; left: 980px;");
		mainLayout.addComponent(txtCavity, "top: 18px; left: 1060px;");

		mainLayout.addComponent(lblCT, "top: 50px; left: 980px;");
		mainLayout.addComponent(txtCT, "top: 48px; left: 1060px;");

		mainLayout.addComponent(lblTarget, "top: 80px; left: 980px;");
		mainLayout.addComponent(txtTarget, "top: 78px; left: 1060px;");

		mainLayout.addComponent(lblVoucherNo, "top: 110px; left: 980px;");
		mainLayout.addComponent(txtVoucherNo, "top: 108px; left: 1060px;");


		/*mainLayout.addComponent(txtShiftA, "top: 230px; left: 635px;");
		mainLayout.addComponent(lblShiftA, "top: 230px; left: 665px;");

		mainLayout.addComponent(txtShiftB, "top: 230px; left: 760px;");
		mainLayout.addComponent(lblShiftB, "top: 230px; left: 790px;");

		mainLayout.addComponent(txtTotal, "top: 230px; left: 892px;");
		mainLayout.addComponent(lblTotal, "top: 230px; left: 935px;");

		mainLayout.addComponent(txtWastage, "top: 230px; left: 1025px;");
		mainLayout.addComponent(lblWastage, "top: 230px; left: 1070px;");*/

		mainLayout.addComponent(table, "top: 140px; left: 0px;");

		lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:470.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:500.0px; left:250.0px;");
		mainLayout.addComponent(btnReport, "top:500.0px;left:1050.0px;");

		return mainLayout;
	}
	private String autoProductionNo()
	{
		String autoNo=null;
		Transaction tx;
		Iterator iter = dbService("select isnull(max(CAST(ProductionNo as int)),0)+1 from tbMouldProductionInfo");

		if(iter.hasNext())
		{
			autoNo=iter.next().toString().trim();
		}
		return autoNo;
	}
}
