package com.example.productionTransaction;
//import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.Window;

public class DailyProductionEntryLabelingPrinting extends Window
{
	private SessionBean sessionBean;
	private Label lblProductionNo;
	private Label lblProductionDate;
	private Label lblProductionStep;

	private Label lblBatchQty;
	private Label lblProductionQty;
	private Label lblTotalReject;
	private Label lblTotalProduction,lblRemainQty,lblRejectQty,lblPresentProductionFG,
	lblpresentJejectionFG,lblTotalConsumption,lblVoucherNo;

	private TextRead txtProductionNo,txtPresentProductionFG,txtpresentRejectionFG,txtVoucherNo;
	private PopupDateField dProductionDate;
	private ComboBox cmbProductionStep;

	private TextRead txtBatchQty,txtProductionQty,txtTotalReject,txtTotalProduction,txtRemainQty,txtRejectQty,txtTotalConsumption;

	private Table table;
	ArrayList<Label>tbSl = new ArrayList<Label>();
	private ArrayList<CheckBox> tbChkShow = new ArrayList<CheckBox>();
	private ArrayList<ComboBox> tbCmbBatchNo = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbCmbMachineName = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbCmbSemiFgName = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbCmbFGName = new ArrayList<ComboBox>();
	private ArrayList<AmountField> tbTxtShiftA = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbTxtRejectA = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbTxtShiftB = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbTxtRejectB = new ArrayList<AmountField>();
	private ArrayList<TextField> tbTxtRemarks = new ArrayList<TextField>();

	ArrayList<Label>tbLblSemiFgStandardNo = new ArrayList<Label>();
	ArrayList<Label>tbLblSemiFgStandardSlFlag = new ArrayList<Label>();
	ArrayList<Label>tbLblSemiFgStandardUnitPrice = new ArrayList<Label>();
	ArrayList<Label>tbLblSemiFgStandardAmount = new ArrayList<Label>();
	ArrayList<Label>tbLblInkStandardNo = new ArrayList<Label>();
	ArrayList<Label>tbLblInkStandardSlFlag = new ArrayList<Label>();
	ArrayList<Label>tbLblInkStandardUnitPrice = new ArrayList<Label>();
	ArrayList<Label>tbLblInkStandardAmount = new ArrayList<Label>();
	private ArrayList<CheckBox> tbchkisFg = new ArrayList<CheckBox>();

	ArrayList<Component> allComp = new ArrayList<Component>();

	private CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	boolean isUpdate=false,isFind=false;
	private AbsoluteLayout mainLayout;
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat df=new DecimalFormat("#0");
	private DecimalFormat df2=new DecimalFormat("#0.00");
	private DecimalFormat df3=new DecimalFormat("#0.00000000");
	private TextField txtProductionNoFind=new TextField();

	HashMap hmap=new HashMap();

	public DailyProductionEntryLabelingPrinting(SessionBean sessionBean,String caption,int a)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("Daily Production Entry[Labelling/Printing/Cap Folding/SHRINK] :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		focusEnter();
		btnAction();
	}

	private void cmbProductionStepDataLoad() {
		cmbProductionStep.removeAllItems();
		Iterator iter=dbService("select vDepoId,vDepoName from tbDepoInformation order by vDepoName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionStep.addItem(element[0]);
			cmbProductionStep.setItemCaption(element[0], element[1].toString());
		}
	}
	private void tbCmbDataLoad(int ar)
	{

	}
	private Iterator<?> dbService(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Iterator<?> iter=null;
		try{
			iter= session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			if(session!=null)
			{
				session.close();
			}
		}
		return iter;
	}
	private void componentIni(boolean b)
	{

		txtProductionNo.setEnabled(!b);
		dProductionDate.setEnabled(!b);
		cmbProductionStep.setEnabled(!b);
		txtBatchQty.setEnabled(!b);
		txtProductionQty.setEnabled(!b);
		txtRejectQty.setEnabled(!b);
		txtTotalReject.setEnabled(!b);
		txtTotalProduction.setEnabled(!b);
		txtRemainQty.setEnabled(!b);
		txtPresentProductionFG.setEnabled(!b);
		txtpresentRejectionFG.setEnabled(!b);
		txtTotalConsumption.setEnabled(!b);
		for(int a=0;a<tbChkShow.size();a++)
		{
			tbChkShow.get(a).setEnabled(!b);
			tbCmbBatchNo.get(a).setEnabled(!b);
			tbCmbMachineName.get(a).setEnabled(!b);
			tbCmbSemiFgName.get(a).setEnabled(!b);
			tbCmbFGName.get(a).setEnabled(!b);
			tbTxtShiftA.get(a).setEnabled(!b);
			tbTxtRejectA.get(a).setEnabled(!b);
			tbTxtShiftB.get(a).setEnabled(!b);
			tbTxtRejectB.get(a).setEnabled(!b);
			tbTxtRemarks.get(a).setEnabled(!b);
		}

	}
	private boolean checkTableData()
	{
		for(int a=0;a<tbSl.size();a++)
		{
			if(tbCmbBatchNo.get(a).getValue()!=null&&tbCmbMachineName.get(a).getValue()!=null){
				double shiftA,rejectA,shiftB,rejectB;
				shiftA=Double.parseDouble("0"+tbTxtShiftA.get(a).getValue().toString());
				rejectA=Double.parseDouble("0"+tbTxtRejectA.get(a).getValue().toString());
				shiftB=Double.parseDouble("0"+tbTxtShiftB.get(a).getValue().toString());
				rejectB=Double.parseDouble("0"+tbTxtRejectB.get(a).getValue().toString());
				if((shiftA+rejectA+shiftB+rejectB)<=0){
					return false;
				}
			}
		}
		return true;
	}
	private boolean checkValidation()
	{
		if(cmbProductionStep.getValue()!=null){
			if(tbCmbBatchNo.get(0).getValue()!=null&&tbCmbMachineName.get(0).getValue()!=null&&
					tbCmbSemiFgName.get(0).getValue()!=null&&tbCmbFGName.get(0).getValue()!=null){
				if(checkTableData()){
					return true;
				}
				else{
					showNotification("Please Provide all Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide all Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Production Step",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}
	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete from tbLabelingPrintingDailyProductionInfo where productionNo='"+txtProductionNo.getValue()+ "'").executeUpdate();

			session.createSQLQuery("delete from tbLabelingPrintingDailyProductionDetails where productionNo='"+txtProductionNo.getValue()+ "'").executeUpdate();

			String Sql="insert into tbudLabelingPrintingFinishedProduct select  productionNo, "
					+ "productionDate, ProductionStepId, ProductionStepName, batchNo, machineCode, "
					+ "machineName, semiFgCode, semiFgName, fgCode, fgName, shiftA, shiftB, rejectA, "
					+ "rejectB, totalShift, totalReject, remarks, isApproved,'Update' vUdFlag "
					+ "from tbLabelingPrintingFinishedProduct where ProductionNo like '"+txtProductionNo.getValue().toString()+"' ";

			session.createSQLQuery(Sql).executeUpdate();

			session.createSQLQuery(" delete from tbLabelingPrintingFinishedProduct where ProductionNo like '"+txtProductionNo.getValue().toString()+"' ").executeUpdate();

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormat.format(dProductionDate.getValue())+"')").list().iterator().next().toString();
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
							insertData();
						}
						else 
						{
							tx.rollback();
						}
						componentIni(true);
						btnIni(true);
						tableClear();
						txtClear();
						isUpdate=false;
						isFind=false;
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
	public String vocherIdGenerate()
	{
		String vo_id = null;
		Transaction tx = null;
		try{
			Session session1 = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session1.beginTransaction();
			String fsl = session1.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormat.format(dProductionDate.getValue())+"')").list().iterator().next().toString();
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
	private void insertData(){
		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String type="New";
		String productionNo="",voucherNo="";
		int handleFlag=0;
		double referenceFgQty=0,inkAmount=0,semiFgAmount=0,totalStandardAmountInk=0,totalStandardAmountSemiFg=0;
		String referenceFgId = "",referenceFgName="";
		if(isUpdate)
		{
			type="update";
			productionNo=txtProductionNo.getValue().toString();
			voucherNo=txtVoucherNo.getValue().toString();
		}
		else{
			productionNoLoadData();
			voucherNo=vocherIdGenerate();
		}

		for(int a=0;a<tbChkShow.size();a++){
			semiFgAmount=Double.parseDouble(tbLblSemiFgStandardAmount.get(a).getValue().toString().isEmpty()?
					"0.0":tbLblSemiFgStandardAmount.get(a).getValue().toString());
			totalStandardAmountSemiFg=semiFgAmount+totalStandardAmountSemiFg;

			inkAmount=Double.parseDouble(tbLblInkStandardAmount.get(a).getValue().toString().isEmpty()?
					"0.0":tbLblInkStandardAmount.get(a).getValue().toString());
			totalStandardAmountInk=inkAmount+totalStandardAmountInk;
		}
		//Production No: 863 Production Date: 03-05-2018 Step: Cap Folding
		String narration="Production No: "+productionNo+" Production Date: "+
				new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+" Step: "+cmbProductionStep.getValue();
		try{

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]" +
					"('"+dateFormat.format(dProductionDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			String sqlInfo="insert into tbLabelingPrintingDailyProductionInfo(productionNo,productionDate," +
					"productionStep,userIp,userName,entryTime,isApproved,voucherNo,voucherDate,totalStandardAmountInk," +
					"totalStandardAmontSemiFg,fiscalYearSl)values('"+txtProductionNo.getValue()+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"','"+cmbProductionStep.getValue()+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,0,'"+voucherNo+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"'," +
					"'"+totalStandardAmountInk+"','"+totalStandardAmountSemiFg+"','"+fsl+"')";
			session.createSQLQuery(sqlInfo).executeUpdate();

			String sqlInfoUd="insert into tbUdLabelingPrintingDailyProductionInfo(productionNo,productionDate," +
					"productionStep,userIp,userName,entryTime,isApproved,type)values('"+txtProductionNo.getValue()+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"','"+cmbProductionStep.getValue()+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,0,'"+type+"')";
			session.createSQLQuery(sqlInfoUd).executeUpdate();

			String sqlInfoVoucher=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration," +
					"DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime," +
					"auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
					+" values(" +
					"'"+voucherNo+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"'," +
					"'AL1707', "  
					+" '"+narration+"'," +
					" '"+df2.format(totalStandardAmountSemiFg+totalStandardAmountInk)+"'," +
					"'0' , "+
					"'jau'," +
					"'U-1'," +
					"'1' ," +
					"'"+sessionBean.getUserId()+"', "
					+" '"+sessionBean.getUserIp()+"'," +
					"CURRENT_TIMESTAMP, "
					+" '2', " +
					"'"+sessionBean.getCompanyId()+"' ," +
					"''," +
					"'"+cmbProductionStep.getValue()+"','DailyProudctionLabelingPrinting' ) ";
			session.createSQLQuery(sqlInfoVoucher).executeUpdate();	
			int isfg=0;
			for(int a=0;a<tbChkShow.size();a++)
			{
				double shiftA,rejectA,shiftB,rejectB;
				shiftA=Double.parseDouble("0"+tbTxtShiftA.get(a).getValue().toString());
				rejectA=Double.parseDouble("0"+tbTxtRejectA.get(a).getValue().toString());
				shiftB=Double.parseDouble("0"+tbTxtShiftB.get(a).getValue().toString());
				rejectB=Double.parseDouble("0"+tbTxtRejectB.get(a).getValue().toString());

				if(tbCmbBatchNo.get(a).getValue()!=null&&tbCmbMachineName.get(a).getValue()!=null&&
						tbCmbSemiFgName.get(a).getValue()!=null&&tbCmbFGName.get(a).getValue()!=null&&
						(shiftA+rejectA+shiftB+rejectB)>0)
				{
					double totalShift=shiftA+shiftB;
					double totalReject=rejectA+rejectB;

					if(!(tbCmbSemiFgName.get(a).isEnabled()&&tbCmbFGName.get(a).isEnabled()))
					{

						handleFlag=1;
						referenceFgQty=Double.parseDouble("0"+tbTxtShiftA.get(a-1).getValue().toString())+
								Double.parseDouble("0"+tbTxtShiftB.get(a-1).getValue().toString());
						referenceFgId=tbCmbFGName.get(a-1).getValue().toString();
						referenceFgName=tbCmbFGName.get(a-1).getItemCaption(tbCmbFGName.get(a-1).getValue());

					}

					if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Dry Offset Printing"))
					{
						if(tbchkisFg.get(a).booleanValue()==true)
						{
							isfg=1;	
						}
					}
					else
					{
						isfg=0;
					}

					//tbLblInkStandardUnitPrice.get(a).getValue()
					String sqlDetails="insert into tbLabelingPrintingDailyProductionDetails(productionNo,batchNo," +
							"machineCode,machineName,semiFgCode,semiFgName, "+
							" fgCode,fgName,shiftA,shiftB,rejectA,rejectB,totalShift,totalReject,remarks," +
							"isApproved,handleFlag,referenceFgId,referenceFgName,referenceFgQty,voucherNo," +
							"voucherdate,semiFgStandardNo,semiFgStandardSl,semiFgStandardUnitPrice,semiFgStandardAmount," +
							"InkStandardNo,InkStandardSl,inkStandardUnitPrice,inkStandardAmount,isFg) "+
							" values('"+txtProductionNo.getValue()+"','"+tbCmbBatchNo.get(a).getValue()+"'," +
							"'"+tbCmbMachineName.get(a).getValue()+"','"+tbCmbMachineName.get(a).getItemCaption(tbCmbMachineName.get(a).getValue())+"'," +
							"'"+tbCmbSemiFgName.get(a).getValue()+"','"+tbCmbSemiFgName.get(a).getItemCaption(tbCmbSemiFgName.get(a).getValue())+"'," +
							"'"+tbCmbFGName.get(a).getValue()+"','"+tbCmbFGName.get(a).getItemCaption(tbCmbFGName.get(a).getValue())+"'," +
							"'"+tbTxtShiftA.get(a).getValue()+"','"+tbTxtShiftB.get(a).getValue()+"','"+tbTxtRejectA.get(a).getValue()+"'," +
							"'"+tbTxtRejectB.get(a).getValue()+"','"+totalShift+"','"+totalReject+"','"+tbTxtRemarks.get(a).getValue()+"','0'," +
							"'"+handleFlag+"','"+referenceFgId+"','"+referenceFgName+"','"+referenceFgQty+"','"+voucherNo+"'," +
							"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"'," +
							"'"+tbLblSemiFgStandardNo.get(a).getValue()+"'," +
							"'"+tbLblSemiFgStandardSlFlag.get(a).getValue()+"'," +
							"'"+tbLblSemiFgStandardUnitPrice.get(a).getValue()+"'," +
							"'"+tbLblSemiFgStandardAmount.get(a).getValue()+"'," +
							"'"+tbLblInkStandardNo.get(a).getValue()+"'," +
							"'"+tbLblInkStandardSlFlag.get(a).getValue()+"'," +
							"'"+df3.format(Double.parseDouble(( tbLblInkStandardUnitPrice.get(a).getValue().toString().isEmpty()?"0.00":tbLblInkStandardUnitPrice.get(a).getValue().toString())))+"'," +
							"'"+ df3.format(Double.parseDouble(tbLblInkStandardAmount.get(a).getValue().toString().isEmpty()?"0.00":tbLblInkStandardAmount.get(a).getValue().toString()))+"','"+isfg+"'  " +
							")";


					session.createSQLQuery(sqlDetails).executeUpdate();

					String sqlDetailsUd="insert into tbUdLabelingPrintingDailyProductionDetails(productionNo,batchNo,machineCode,machineName,semiFgCode,semiFgName, "+
							" fgCode,fgName,shiftA,shiftB,rejectA,rejectB,totalShift,totalReject,remarks,isApproved,type,handleFlag,referenceFgId,referenceFgName,referenceFgQty,isFg) "+
							" values('"+txtProductionNo.getValue()+"','"+tbCmbBatchNo.get(a).getValue()+"'," +
							"'"+tbCmbMachineName.get(a).getValue()+"','"+tbCmbMachineName.get(a).getItemCaption(tbCmbMachineName.get(a).getValue())+"'," +
							"'"+tbCmbSemiFgName.get(a).getValue()+"','"+tbCmbSemiFgName.get(a).getItemCaption(tbCmbSemiFgName.get(a).getValue())+"'," +
							"'"+tbCmbFGName.get(a).getValue()+"','"+tbCmbFGName.get(a).getItemCaption(tbCmbFGName.get(a).getValue())+"'," +
							"'"+tbTxtShiftA.get(a).getValue()+"','"+tbTxtShiftB.get(a).getValue()+"','"+tbTxtRejectA.get(a).getValue()+"'," +
							"'"+tbTxtRejectB.get(a).getValue()+"','"+totalShift+"','"+totalReject+"','"+tbTxtRemarks.get(a).getValue()+"','0','"+type+"'," +
							"'"+handleFlag+"','"+referenceFgId+"','"+referenceFgName+"','"+referenceFgQty+"','"+isfg+"')";

					session.createSQLQuery(sqlDetailsUd).executeUpdate();

					if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Labeling")
							||cmbProductionStep.getValue().toString().equalsIgnoreCase("Cap Folding")
							||cmbProductionStep.getValue().toString().equalsIgnoreCase("Stretch Blow Molding"))
					{
						String sqlDetailsFinishedProduct="insert into tbLabelingPrintingFinishedProduct(productionNo,productionDate," +
								"productionStepId,productionStepName,batchNo," +
								"machineCode,machineName,semiFgCode,semiFgName, "+
								" fgCode,fgName,shiftA,shiftB,rejectA,rejectB,totalShift,totalReject,remarks," +
								"isApproved) "+
								" values('"+txtProductionNo.getValue()+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"','"+cmbProductionStep.getValue()+"'," +
								"'"+cmbProductionStep.getItemCaption(cmbProductionStep.getValue())+"'," +
								"'"+tbCmbBatchNo.get(a).getValue()+"'," +
								"'"+tbCmbMachineName.get(a).getValue()+"','"+tbCmbMachineName.get(a).getItemCaption(tbCmbMachineName.get(a).getValue())+"'," +
								"'"+tbCmbSemiFgName.get(a).getValue()+"','"+tbCmbSemiFgName.get(a).getItemCaption(tbCmbSemiFgName.get(a).getValue())+"'," +
								"'"+tbCmbFGName.get(a).getValue()+"','"+tbCmbFGName.get(a).getItemCaption(tbCmbFGName.get(a).getValue())+"'," +
								"'"+tbTxtShiftA.get(a).getValue()+"','"+tbTxtShiftB.get(a).getValue()+"','"+tbTxtRejectA.get(a).getValue()+"'," +
								"'"+tbTxtRejectB.get(a).getValue()+"','"+totalShift+"','"+totalReject+"','"+tbTxtRemarks.get(a).getValue()+"','0')";

						session.createSQLQuery(sqlDetailsFinishedProduct).executeUpdate();

						if(isUpdate)
						{
							String sqlDetailsFinishedProductUd="insert into tbUdLabelingPrintingFinishedProduct(productionNo,productionDate," +
									"productionStepId,productionStepName,batchNo," +
									"machineCode,machineName,semiFgCode,semiFgName, "+
									" fgCode,fgName,shiftA,shiftB,rejectA,rejectB,totalShift,totalReject,remarks," +
									"isApproved,vUdFlag) "+
									" values('"+txtProductionNo.getValue()+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"','"+cmbProductionStep.getValue()+"'," +
									"'"+cmbProductionStep.getItemCaption(cmbProductionStep.getValue())+"'," +
									"'"+tbCmbBatchNo.get(a).getValue()+"'," +
									"'"+tbCmbMachineName.get(a).getValue()+"','"+tbCmbMachineName.get(a).getItemCaption(tbCmbMachineName.get(a).getValue())+"'," +
									"'"+tbCmbSemiFgName.get(a).getValue()+"','"+tbCmbSemiFgName.get(a).getItemCaption(tbCmbSemiFgName.get(a).getValue())+"'," +
									"'"+tbCmbFGName.get(a).getValue()+"','"+tbCmbFGName.get(a).getItemCaption(tbCmbFGName.get(a).getValue())+"'," +
									"'"+tbTxtShiftA.get(a).getValue()+"','"+tbTxtShiftB.get(a).getValue()+"','"+tbTxtRejectA.get(a).getValue()+"'," +
									"'"+tbTxtRejectB.get(a).getValue()+"','"+totalShift+"','"+totalReject+"','"+tbTxtRemarks.get(a).getValue()+"','0','New')";

							session.createSQLQuery(sqlDetailsFinishedProductUd).executeUpdate();
						}
					}


					if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Dry Offset Printing"))

					{
						if(tbchkisFg.get(a).booleanValue()==true)
						{

							String sqlDetailsFinishedProduct="insert into tbLabelingPrintingFinishedProduct(productionNo,productionDate," +
									"productionStepId,productionStepName,batchNo," +
									"machineCode,machineName,semiFgCode,semiFgName, "+
									" fgCode,fgName,shiftA,shiftB,rejectA,rejectB,totalShift,totalReject,remarks," +
									"isApproved) "+
									" values('"+txtProductionNo.getValue()+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"','"+cmbProductionStep.getValue()+"'," +
									"'"+cmbProductionStep.getItemCaption(cmbProductionStep.getValue())+"'," +
									"'"+tbCmbBatchNo.get(a).getValue()+"'," +
									"'"+tbCmbMachineName.get(a).getValue()+"','"+tbCmbMachineName.get(a).getItemCaption(tbCmbMachineName.get(a).getValue())+"'," +
									"'"+tbCmbSemiFgName.get(a).getValue()+"','"+tbCmbSemiFgName.get(a).getItemCaption(tbCmbSemiFgName.get(a).getValue())+"'," +
									"'"+tbCmbFGName.get(a).getValue()+"','"+tbCmbFGName.get(a).getItemCaption(tbCmbFGName.get(a).getValue())+"'," +
									"'"+tbTxtShiftA.get(a).getValue()+"','"+tbTxtShiftB.get(a).getValue()+"','"+tbTxtRejectA.get(a).getValue()+"'," +
									"'"+tbTxtRejectB.get(a).getValue()+"','"+totalShift+"','"+totalReject+"','"+tbTxtRemarks.get(a).getValue()+"','0')";

							session.createSQLQuery(sqlDetailsFinishedProduct).executeUpdate();

							if(isUpdate)
							{
								String sqlDetailsFinishedProductUd="insert into tbUdLabelingPrintingFinishedProduct(productionNo,productionDate," +
										"productionStepId,productionStepName,batchNo," +
										"machineCode,machineName,semiFgCode,semiFgName, "+
										" fgCode,fgName,shiftA,shiftB,rejectA,rejectB,totalShift,totalReject,remarks," +
										"isApproved,vUdFlag) "+
										" values('"+txtProductionNo.getValue()+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"','"+cmbProductionStep.getValue()+"'," +
										"'"+cmbProductionStep.getItemCaption(cmbProductionStep.getValue())+"'," +
										"'"+tbCmbBatchNo.get(a).getValue()+"'," +
										"'"+tbCmbMachineName.get(a).getValue()+"','"+tbCmbMachineName.get(a).getItemCaption(tbCmbMachineName.get(a).getValue())+"'," +
										"'"+tbCmbSemiFgName.get(a).getValue()+"','"+tbCmbSemiFgName.get(a).getItemCaption(tbCmbSemiFgName.get(a).getValue())+"'," +
										"'"+tbCmbFGName.get(a).getValue()+"','"+tbCmbFGName.get(a).getItemCaption(tbCmbFGName.get(a).getValue())+"'," +
										"'"+tbTxtShiftA.get(a).getValue()+"','"+tbTxtShiftB.get(a).getValue()+"','"+tbTxtRejectA.get(a).getValue()+"'," +
										"'"+tbTxtRejectB.get(a).getValue()+"','"+totalShift+"','"+totalReject+"','"+tbTxtRemarks.get(a).getValue()+"','0','New')";

								session.createSQLQuery(sqlDetailsFinishedProductUd).executeUpdate();
							}

						}


					}

					handleFlag=0;
					referenceFgQty=0;
					referenceFgId = "";
					referenceFgName="";
				}
			}


			String sqlDetailsVoucherSemiFg=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration," +
					"DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime," +
					"auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
					+" values(" +
					"'"+voucherNo+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"'," +
					"'AL1706', "  
					+" '"+narration+"'," +
					" '0'," +
					"'"+df2.format(totalStandardAmountSemiFg)+"' , "+
					"'jau'," +
					"'U-1'," +
					"'1' ," +
					"'"+sessionBean.getUserId()+"', "
					+" '"+sessionBean.getUserIp()+"'," +
					"CURRENT_TIMESTAMP, "
					+" '2', " +
					"'"+sessionBean.getCompanyId()+"' ," +
					"''," +
					"'"+cmbProductionStep.getValue()+"','DailyProudctionLabelingPrinting' ) ";
			session.createSQLQuery(sqlDetailsVoucherSemiFg).executeUpdate();

			if(!(cmbProductionStep.getValue().toString().equalsIgnoreCase("Labeling")
					||cmbProductionStep.getValue().toString().equalsIgnoreCase("Cap Folding")
					||cmbProductionStep.getValue().toString().equalsIgnoreCase("Stretch Blow Molding")))
			{
				String sqlDetailsVoucherInk=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration," +
						"DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime," +
						"auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
						+" values(" +
						"'"+voucherNo+"'," +
						"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"'," +
						"'AL1704', "  
						+" '"+narration+"'," +
						" '0'," +
						"'"+df2.format(totalStandardAmountInk)+"' , "+
						"'jau'," +
						"'U-1'," +
						"'1' ," +
						"'"+sessionBean.getUserId()+"', "
						+" '"+sessionBean.getUserIp()+"'," +
						"CURRENT_TIMESTAMP, "
						+" '2', " +
						"'"+sessionBean.getCompanyId()+"' ," +
						"''," +
						"'"+cmbProductionStep.getValue()+"' ,'DailyProudctionLabelingPrinting') ";
				session.createSQLQuery(sqlDetailsVoucherInk).executeUpdate();
			}

			showNotification("All Information Save Successfully",Notification.TYPE_WARNING_MESSAGE);
			tx.commit();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;

		for(int a=0;a<hmap.size();a++)
		{
			int ar=(Integer) hmap.get(a);
			tbSl.get(ar).setEnabled(false);
			tbChkShow.get(ar).setEnabled(false);
			tbCmbBatchNo.get(ar).setEnabled(false);
			tbCmbMachineName.get(ar).setEnabled(false);
			tbCmbSemiFgName.get(ar).setEnabled(false);
			tbCmbFGName.get(ar).setEnabled(false);
			tbTxtShiftA.get(ar).setEnabled(false);
			tbTxtShiftB.get(ar).setEnabled(false);
			tbTxtRemarks.get(ar).setEnabled(false);
		}
	}
	private void findInitialise(String productionNo)
	{
		Iterator iter=dbService("select a.productionNo,a.productionDate,a.productionStep,b.batchNo,b.machineCode, "+
				" b.semiFgCode,b.fgCode,b.shiftA,b.rejectA,b.shiftB,b.rejectB,b.remarks,b.fgName,b.handleFlag," +
				" isnull(b.semiFgStandardUnitPrice,0)semiFgStandardUnitPrice,isnull(b.semiFgStandardAmount,0)semiFgStandardAmount," +
				" isnull(b.InkStandardNo,0)InkStandardNo,isnull(b.InkStandardSl,0)InkStandardSl," +
				" isnull(b.inkStandardUnitPrice,0)inkStandardUnitPrice,isnull(b.inkStandardAmount,0)inkStandardAmount,a.voucherNo,b.isFg  "+
				" from tbLabelingPrintingDailyProductionInfo a inner join tbLabelingPrintingDailyProductionDetails b "+ 
				" on a.productionNo=b.productionNo where a.productionNo='"+productionNo+"'");
		int a=0;
		int ar=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==0){
				txtProductionNo.setValue(element[0]);
				dProductionDate.setValue(element[1]);
				cmbProductionStep.setValue(element[2]);
				txtVoucherNo.setValue(element[20]);
			}
			tbCmbBatchNo.get(a).setValue(element[3]);
			tbCmbMachineName.get(a).setValue(element[4]);
			tbCmbSemiFgName.get(a).setValue(element[5]);
			//if(checkHandle(a-1)){
			tbCmbFGName.get(a).addItem(element[6]);
			tbCmbFGName.get(a).setItemCaption(element[6], element[12].toString());
			//}
			tbCmbFGName.get(a).setValue(element[6]);
			tbTxtShiftA.get(a).setValue(df.format(element[7]));
			tbTxtRejectA.get(a).setValue(df.format(element[8]));
			tbTxtShiftB.get(a).setValue(df.format(element[9]));
			tbTxtRejectB.get(a).setValue(df.format(element[10]));
			tbTxtRemarks.get(a).setValue(element[11]);

			tbLblSemiFgStandardUnitPrice.get(a).setValue(df.format(element[14]));
			tbLblSemiFgStandardAmount.get(a).setValue(df.format(element[15]));
			tbLblInkStandardNo.get(a).setValue(df.format(element[16]));
			tbLblInkStandardSlFlag.get(a).setValue(df.format(element[17]));
			tbLblInkStandardUnitPrice.get(a).setValue(df.format(element[18]));
			tbLblInkStandardAmount.get(a).setValue(df.format(element[19]));

			if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Dry Offset Printing"))
			{
				if(Integer.parseInt(element[21].toString())==1)
				{
					tbchkisFg.get(a).setValue(true);  	
				}
				else
				{
					tbchkisFg.get(a).setValue(false); 	
				}

			}

			if(element[13].toString().equalsIgnoreCase("1"))
			{
				hmap.put(ar, a);
				ar++;
			}
			a++;
			if(a==tbChkShow.size()-1){
				tableRowAdd(a+1);
			}
		}
	}
	private void findButtonEvent()
	{
		Window win = new DailyProductionLabelingPrintingFind(sessionBean,txtProductionNoFind);

		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtProductionNoFind.getValue().toString().length() > 0)
				{
					System.out.println(txtProductionNoFind.getValue().toString());
					findInitialise(txtProductionNoFind.getValue().toString());
					cButton.btnEdit.focus();
				}
			}
		});
		this.getParent().addWindow(win);
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
				isFind=false;
				isUpdate=false;
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
				isFind=false;
				editButtonEvent();
				//isUpdate = true;

			}
		});

		cButton.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				isUpdate=true;
				findButtonEvent();	
			}
		});
		cmbProductionStep.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbProductionStep.getValue()!=null)
				{
					System.out.println(cmbProductionStep.getValue());
					for(int a=0;a<tbCmbBatchNo.size();a++)
					{
						tbCmbBatchNoLoadData(a);
					}

					if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Dry Offset Printing"))
					{

						table.setColumnCollapsed("IsFg", false);	
					}
					else
					{
						table.setColumnCollapsed("IsFg", true);	
					}

				}
				else
				{
					for(int a=0;a<tbCmbBatchNo.size();a++)
					{
						tbCmbBatchNo.get(a).removeAllItems();
					}
				}

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

		/*
		 * 
		 * button.btnSave.addListener(new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					if(!txtjobNo.getValue().toString().isEmpty())
					{
						if(!txtjobName.getValue().toString().isEmpty())
						{
							if(cmbclientName.getValue()!=null)
							{
								saveButtonEvent();	
							}
							else
							{
								showNotification("Warning!","Please Select Client Name",Notification.TYPE_WARNING_MESSAGE);
								cmbclientName.focus();	
							}	
						}
						else
						{
							showNotification("Warning!","Provide Job Name",Notification.TYPE_WARNING_MESSAGE);
							txtjobName.focus();
						}	
					}
					else
					{
						showNotification("Warning!","Provide Job No",Notification.TYPE_WARNING_MESSAGE);
						txtjobNo.focus();
					}


				}
			});


			txtjobNo.addListener(new ValueChangeListener() 
			{

				public void valueChange(ValueChangeEvent event) {

					if(!isFind){
						if(!isExistName()){
							showNotification("Job No: "+txtjobNo.getValue()+" is already Eixsts.",Notification.TYPE_WARNING_MESSAGE);
							txtjobNo.setValue("");
							txtjobNo.focus();
						}
					}
				}
			});

			txtjobName.addListener(new ValueChangeListener() 
			{

				public void valueChange(ValueChangeEvent event) {

					if(!isFind){
						if(!isExistjob()){
							showNotification("Job Name: "+txtjobName.getValue()+" is already Eixsts.",Notification.TYPE_WARNING_MESSAGE);
							txtjobName.setValue("");
							txtjobName.focus();
						}
					}
				}
			});

			cmbclientName.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbclientName.getValue()!=null)
					{
					   clientInfoLoad();	
					}

				}
			});

			nbStore.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					System.out.println("Rack Form");
					gradeFormLink();				
				}
			});

		 * 
		 */

	}

	//Button Action End

	/*private void findButtonEvent()
	{
		Window win = new RequisitionFindWindow();

	 * 
	 * win.addListener(new Window.CloseListener() 
			{
				public void windowClose(CloseEvent e) 
				{
					if(DepoId.getValue().toString().length() > 0)
					{
						System.out.println(DepoId.getValue().toString());
						findInitialise(DepoId.getValue().toString());
						button.btnEdit.focus();
					}
				}
			});



		this.getParent().addWindow(win);
	}*/


	private void tbCmbBatchNoLoadData(int ar) {
		tbCmbBatchNo.get(ar).removeAllItems();
		Iterator iter=dbService("select 0,batchNo,CONVERT(varchar(10),issueDate,103)issueDate from tbIssueToLabelPrintingInfo where issueTo " +
				"like '"+cmbProductionStep.getValue()+"' order by issueDate");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbBatchNo.get(ar).addItem(element[1]);
			tbCmbBatchNo.get(ar).setItemCaption(element[1], element[1]+" # "+element[2]);
		}
	}

	private void tbCmbFgLoadData(int ar) {
		/*String sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation " +
				"where semiFgId='"+tbCmbSemiFgName.get(ar).getValue()+"' order by semiFgSubName";
		 */

		String sql="";

		if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Labeling"))
		{
			sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation where productionStepId in('Labeling','Manual Printing')  ";  	
		}

		if(cmbProductionStep.getValue().toString().equalsIgnoreCase("Cap Folding"))
		{
			sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation " +
					" where semiFgId = '"+tbCmbSemiFgName.get(ar).getValue()+"' order by semiFgSubName"; 	
		}

		String	step=cmbProductionStep.getValue().toString();


		if(step.equalsIgnoreCase("SHRINK"))
		{
			sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation " +
					" where semiFgId = '"+tbCmbSemiFgName.get(ar).getValue()+"' order by semiFgSubName"; 	
		}

		if(step.equalsIgnoreCase("Dry Offset Printing")||step.equalsIgnoreCase("Screen Printing")||
				step.equalsIgnoreCase("Heat Trasfer Label")||step.equalsIgnoreCase("Manual Printing"))
		{
			if(!tbCmbSemiFgName.get(ar).getValue().toString().contains("SemiFgSub"))
			{
				sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation " +
						"where semiFgId='"+tbCmbSemiFgName.get(ar).getValue()+"' " +
						"and semiFgSubId in (select semiFgSubId from tbInkFormulationInfo) order by semiFgSubName";

			}
			else
			{
				/*sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation " +
					"where semiFgSubId='"+tbCmbSemiFgName.get(ar).getValue()+"' " +
					"and semiFgSubId in (select semiFgSubId from tbInkFormulationInfo) order by semiFgSubName";
				 */
				sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation " +
						"where semiFgSubId='"+tbCmbSemiFgName.get(ar).getValue()+"' " ;

			}

		}
		tbCmbFGName.get(ar).removeAllItems();
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbFGName.get(ar).addItem(element[0]);
			tbCmbFGName.get(ar).setItemCaption(element[0], element[1].toString());
		}

	}
	public void tableInitialise(){
		for(int i=0;i<10;i++){
			tableRowAdd(i);
		}
	}
	private void tbHandleLoadData(int ar){
		tbCmbBatchNo.get(ar+1).setValue(tbCmbBatchNo.get(ar).getValue());
		tbCmbMachineName.get(ar+1).setValue(tbCmbMachineName.get(ar).getValue());


		tbSl.get(ar+1).setEnabled(false);
		tbChkShow.get(ar+1).setEnabled(false);
		tbCmbBatchNo.get(ar+1).setEnabled(false);
		tbCmbMachineName.get(ar+1).setEnabled(false);
		tbCmbSemiFgName.get(ar+1).setEnabled(false);
		tbCmbFGName.get(ar+1).setEnabled(false);
		tbTxtShiftA.get(ar+1).setEnabled(false);
		tbTxtShiftB.get(ar+1).setEnabled(false);
		tbTxtRemarks.get(ar+1).setEnabled(false);

		tbCmbFGName.get(ar+1).removeAllItems();
		String sql="select isnull(semiFgIdTwo,'')semiFgIdTwo,isnull(semiFgNameTwo,'')semiFgNameTwo" +
				" from tbSemiFgSubInformation where semiFgSubId='"+tbCmbFGName.get(ar).getValue()+"' " +
				"and semiFgIdTwo in(select distinct productId from tbIssueToLabelPrintingDetails)" +
				"and semiFgIdTwo in(select distinct fGCode from tbFinishedGoodsStandardInfo where isFg='NO')";
		Iterator iter=dbService(sql);
		System.out.println(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbSemiFgName.get(ar+1).addItem(element[0].toString());
			tbCmbSemiFgName.get(ar+1).setItemCaption(element[0].toString(), element[1].toString());
			tbCmbSemiFgName.get(ar+1).setValue(element[0].toString());
		}
		tbCmbFGName.get(ar+1).addItem(tbCmbFGName.get(ar).getValue());
		tbCmbFGName.get(ar+1).setItemCaption(tbCmbFGName.get(ar).getValue(), tbCmbFGName.get(ar).getItemCaption(tbCmbFGName.get(ar).getValue()));
		tbCmbFGName.get(ar+1).setValue(tbCmbFGName.get(ar).getValue());
	}
	private boolean checkHandle(int ar){
		Iterator iter=dbService("select semiFgIdTwo,semiFgNameTwo from tbSemiFgSubInformation where" +
				" semiFgSubId='"+tbCmbFGName.get(ar).getValue()+"' and semiFgIdTwo is not null and semiFgIdTwo not like ''");
		if(iter.hasNext()){
			return true;
		}
		return false;
	}
	private void batchDataLoad(int ar){
		String sql="select isnull(receivedQty,0)receivedQty,isnull(goodQty,0)goodQty,isnull(rejectQty,0)rejectQty from funcBatchWiseStock" +
				"('"+tbCmbBatchNo.get(ar).getValue()+"','"+tbCmbSemiFgName.get(ar).getValue()+"','"+cmbProductionStep.getValue()+"')";
		Iterator<?>iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtBatchQty.setValue(df.format(element[0]));
			txtTotalProduction.setValue(df.format(element[1]));
			txtTotalReject.setValue(df.format(element[2]));
			double remainQty=Double.parseDouble(element[0].toString())-
					(Double.parseDouble(element[1].toString())+Double.parseDouble(element[2].toString()));
			txtRemainQty.setValue(df.format(remainQty));
		}
	}
	private void batchDataLoadFgWise(int ar){
		String sql="select isnull(SUM(goodQty),0)goodQty,ISNULL(SUM(rejectQty),0)rejectQty from funcBatchWiseProduction" +
				"('"+tbCmbBatchNo.get(ar).getValue()+"','"+tbCmbSemiFgName.get(ar).getValue()+"','"+cmbProductionStep.getValue()+"')" +
				" where FgId='"+tbCmbFGName.get(ar).getValue()+"'";
		Iterator<?>iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtProductionQty.setValue(df.format(element[0]));
			txtRejectQty.setValue(df.format(element[1]));
		}
	}
	private int calcProduction(int ar){
		double shiftAProduction,shiftBProduction,shiftARejection,
		shiftBRejection,totalProduction,totalRejection,remainQty,semiFgStandardUnitPrice,semiFgStandardAmount,
		inkStandardUnitPrice,inkStandardAmount;
		shiftAProduction=Double.parseDouble(tbTxtShiftA.get(ar).getValue().toString().isEmpty()?
				"0.0":tbTxtShiftA.get(ar).getValue().toString());
		shiftBProduction=Double.parseDouble(tbTxtShiftB.get(ar).getValue().toString().isEmpty()?
				"0.0":tbTxtShiftB.get(ar).getValue().toString());
		shiftARejection=Double.parseDouble(tbTxtRejectA.get(ar).getValue().toString().isEmpty()?
				"0.0":tbTxtRejectA.get(ar).getValue().toString());
		shiftBRejection=Double.parseDouble(tbTxtRejectB.get(ar).getValue().toString().isEmpty()?
				"0.0":tbTxtRejectB.get(ar).getValue().toString());

		semiFgStandardUnitPrice=Double.parseDouble(tbLblSemiFgStandardUnitPrice.get(ar).getValue().toString().isEmpty()?
				"0.0":tbLblSemiFgStandardUnitPrice.get(ar).getValue().toString());

		inkStandardUnitPrice=Double.parseDouble(tbLblInkStandardUnitPrice.get(ar).getValue().toString().isEmpty()?
				"0.0":tbLblInkStandardUnitPrice.get(ar).getValue().toString());

		remainQty=Double.parseDouble(txtRemainQty.getValue().toString().isEmpty()?"0":txtRemainQty.getValue().toString());
		totalProduction=shiftAProduction+shiftBProduction;
		totalRejection=shiftARejection+shiftBRejection;
		semiFgStandardAmount=totalProduction*semiFgStandardUnitPrice;
		inkStandardAmount=totalProduction*inkStandardUnitPrice;

		txtPresentProductionFG.setValue(df.format(totalProduction));
		txtpresentRejectionFG.setValue(df.format(totalRejection));
		txtTotalConsumption.setValue(df.format(totalProduction+totalRejection));
		tbLblSemiFgStandardAmount.get(ar).setValue(df.format(semiFgStandardAmount));
		tbLblInkStandardAmount.get(ar).setValue(df.format(inkStandardAmount));

		if((totalProduction+totalRejection)>remainQty){
			return 0;
		}
		return 1;
	}
	private void tbSemiFgFormulationDataLoad(int ar)
	{
		String query="";
		if(!tbCmbSemiFgName.get(ar).getValue().toString().contains("SemiFgSub"))
		{
			query="select 0,AVG(rawMaterialAmount)rawMaterialAmount from tbFinishedGoodsStandardInfo where " +
					"fGCode='"+tbCmbSemiFgName.get(ar).getValue()+"' ";
		}
		else
		{
			query="select 0,AVG(rawMaterialAmount)rawMaterialAmount from tbFinishedGoodsStandardInfo where " +
					"fGCode=(select top 1 semiFgCode from tbSBMDailyProductionDetails where fgCode='"+tbCmbSemiFgName.get(ar).getValue()+"')  ";	
		}

		Iterator<?> iter=dbService(query);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbLblSemiFgStandardUnitPrice.get(ar).setValue(element[1]);
		}
	}
	private void tbInkFormulationDataLoad(int ar)
	{
		String query="select jobNo,slFlag,isnull(amount,0)amount from tbInkFormulationInfo where " +
				"semiFgSubId='"+tbCmbFGName.get(ar).getValue()+"' and slFlag=(select MAX(slFlag) from tbInkFormulationInfo " +
				"where semiFgSubId='"+tbCmbFGName.get(ar).getValue()+"')";
		Iterator<?> iter=dbService(query);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbLblInkStandardNo.get(ar).setValue(element[0]);
			tbLblInkStandardSlFlag.get(ar).setValue(element[1]);
			tbLblInkStandardUnitPrice.get(ar).setValue(element[2]);
		}
	}
	public void tableRowAdd(final int ar)
	{
		try{
			tbSl.add(ar, new Label());
			tbSl.get(ar).setValue(ar+1);
			tbSl.get(ar).setWidth("100%");
			tbSl.get(ar).setHeight("-1px");

			tbChkShow.add(ar, new CheckBox());
			tbChkShow.get(ar).setImmediate(true);
			tbChkShow.get(ar).setWidth("100%");
			tbChkShow.get(ar).setHeight("-1px");


			tbchkisFg.add(ar, new CheckBox());
			tbchkisFg.get(ar).setImmediate(true);
			tbchkisFg.get(ar).setWidth("100%");
			tbchkisFg.get(ar).setHeight("-1px");

			tbChkShow.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(tbChkShow.get(ar).booleanValue()){
						for(int a=0;a<tbChkShow.size();a++){
							if(a!=ar){
								tbChkShow.get(a).setValue(false);
							}
						}
						if(tbCmbBatchNo.get(ar).getValue()!=null&&
								tbCmbSemiFgName.get(ar).getValue()!=null&&
								tbCmbFGName.get(ar).getValue()!=null)
						{
							batchDataLoad(ar);
							batchDataLoadFgWise(ar);
							calcProduction(ar);
						}
					}
					else{
						txtBatchQty.setValue("");
						txtProductionQty.setValue("");
						txtTotalReject.setValue("");
						txtTotalProduction.setValue("");
						txtRejectQty.setValue("");
						txtRemainQty.setValue("");
						txtPresentProductionFG.setValue("");
						txtpresentRejectionFG.setValue("");
						txtTotalConsumption.setValue("");
					}
				}
			});

			tbCmbBatchNo.add(ar, new ComboBox());
			tbCmbBatchNo.get(ar).setImmediate(true);
			tbCmbBatchNo.get(ar).setWidth("100%");
			tbCmbBatchNo.get(ar).setHeight("-1px");
			tbCmbBatchNo.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			tbCmbBatchNo.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(tbCmbBatchNo.get(ar).getValue()!=null){
						tbCmbSemiFgLoadData(ar);
					}
					else{
						tbCmbSemiFgName.get(ar).removeAllItems();
					}
				}
			});

			tbCmbMachineName.add(ar, new ComboBox());
			tbCmbMachineName.get(ar).setImmediate(true);
			tbCmbMachineName.get(ar).setWidth("100%");
			tbCmbMachineName.get(ar).setHeight("-1px");
			tbCmbMachineName.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			tbCmbMachineLoadData(ar);

			tbCmbSemiFgName.add(ar, new ComboBox());
			tbCmbSemiFgName.get(ar).setImmediate(true);
			tbCmbSemiFgName.get(ar).setWidth("100%");
			tbCmbSemiFgName.get(ar).setHeight("-1px");	
			tbCmbSemiFgName.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			tbCmbSemiFgName.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(tbCmbSemiFgName.get(ar).getValue()!=null)
					{
						tbCmbFgLoadData(ar);
						tbSemiFgFormulationDataLoad(ar);
						if(tbCmbBatchNo.get(ar).getValue()!=null)
						{
							batchDataLoad(ar);
						}
					}
					else{
						tbCmbFGName.get(ar).removeAllItems();
						txtBatchQty.setValue("");
						txtProductionQty.setValue("");
						txtTotalReject.setValue("");
						txtTotalProduction.setValue("");
						txtRejectQty.setValue("");
						txtRemainQty.setValue("");
						txtPresentProductionFG.setValue("");
						txtpresentRejectionFG.setValue("");
						txtTotalConsumption.setValue("");

						tbLblInkStandardNo.get(ar).setValue("");
						tbLblInkStandardSlFlag.get(ar).setValue("");
						tbLblInkStandardUnitPrice.get(ar).setValue("");
						tbLblInkStandardAmount.get(ar).setValue("");
						tbLblSemiFgStandardNo.get(ar).setValue("");
						tbLblSemiFgStandardSlFlag.get(ar).setValue("");
						tbLblSemiFgStandardUnitPrice.get(ar).setValue("");
						tbLblSemiFgStandardAmount.get(ar).setValue("");
					}
				}
			});

			tbCmbFGName.add(ar, new ComboBox());
			tbCmbFGName.get(ar).setImmediate(true);
			tbCmbFGName.get(ar).setWidth("100%");
			tbCmbFGName.get(ar).setHeight("-1px");
			tbCmbFGName.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			tbCmbFGName.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					//if(tbCmbBatchNo.get(ar).getValue()!=null&&tbCmbSemiFgName.get(ar).getValue()!=null){
					if(tbCmbFGName.get(ar).getValue()!=null&&
							tbCmbBatchNo.get(ar).getValue()!=null&&
							tbCmbSemiFgName.get(ar).getValue()!=null&&
							tbCmbMachineName.get(ar).getValue()!=null)
					{
						if(!doubleEntryCheck(ar)){
							showNotification("Sorry!!","Double Entry",Notification.TYPE_WARNING_MESSAGE);
							tbTxtShiftA.get(ar).setValue("");
							tbTxtRejectA.get(ar).setValue("");
							tbTxtShiftB.get(ar).setValue("");
							tbTxtRejectB.get(ar).setValue("");
							tbTxtRemarks.get(ar).setValue("");
							tbCmbFGName.get(ar).setValue(null);
							tbCmbFGName.get(ar).focus();
							txtProductionQty.setValue("");
							txtRejectQty.setValue("");
							txtPresentProductionFG.setValue("");
							txtpresentRejectionFG.setValue("");
							txtTotalConsumption.setValue("");

							tbLblInkStandardNo.get(ar).setValue("");
							tbLblInkStandardSlFlag.get(ar).setValue("");
							tbLblInkStandardUnitPrice.get(ar).setValue("");
							tbLblInkStandardAmount.get(ar).setValue("");
						}
						else{
							/*if(checkHandle(ar)&&tbCmbFGName.get(ar).isEnabled()){
								tbHandleLoadData(ar);
								tbTxtShiftA.get(ar).focus();
							}*/
							batchDataLoadFgWise(ar);
							tbInkFormulationDataLoad(ar);
						}

					}
					else{
						tbTxtShiftA.get(ar).setValue("");
						tbTxtRejectA.get(ar).setValue("");
						tbTxtShiftB.get(ar).setValue("");
						tbTxtRejectB.get(ar).setValue("");
						tbTxtRemarks.get(ar).setValue("");
						txtProductionQty.setValue("");
						txtRejectQty.setValue("");
						txtPresentProductionFG.setValue("");
						txtpresentRejectionFG.setValue("");
						txtTotalConsumption.setValue("");
					}
					/*}
					else{
						showNotification("Please Provide All Data Before",Notification.TYPE_WARNING_MESSAGE);
					}*/
				}
			});

			tbTxtShiftA.add(ar, new AmountField());
			tbTxtShiftA.get(ar).setImmediate(true);
			tbTxtShiftA.get(ar).setWidth("100%");
			tbTxtShiftA.get(ar).setHeight("-1px");
			tbTxtShiftA.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(!tbTxtShiftA.get(ar).getValue().toString().isEmpty())
					{
					   int a=calcProduction(ar);

						/*Date date1= new Date() ;
						Date date2= (Date) dProductionDate.getValue();
						long seconds = (date1.getTime()-date2.getTime())/1000;
						System.out.println("second is:"+seconds);	*/
					   
					   
					   Date date1= new Date();
						//Date date2= (Date) dProductionDate.getValue();
						String strDate=dateFormat.format(dProductionDate.getValue());
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
						
						
						
						if(!isUpdate)
						{
							if (!sessionBean.getUserId().toString().equalsIgnoreCase("22") && seconds>259199)
							{
								
									showNotification("Not Permited TO Entry Time Limit Exceed")	;
									tbTxtShiftA.get(ar).setValue("");
									tbTxtShiftA.get(ar).focus();
									calcProduction(ar);	
									
									
							}
							else
							{
								if(a==0)
								{
								showNotification("Total Consumption Exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
								tbTxtShiftA.get(ar).setValue("");
								tbTxtShiftA.get(ar).focus();
								calcProduction(ar); 
								}
								
							}
							
						}	
					}

				}
			});

			tbTxtRejectA.add(ar, new AmountField());
			tbTxtRejectA.get(ar).setImmediate(true);
			tbTxtRejectA.get(ar).setWidth("100%");
			tbTxtRejectA.get(ar).setHeight("-1px");

			tbTxtRejectA.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(!tbTxtRejectA.get(ar).getValue().toString().isEmpty())
					{
						int a=calcProduction(ar);
						if(!isUpdate){
							if(a==0){
								showNotification("Total Consumption Exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
								tbTxtRejectA.get(ar).setValue("");
								tbTxtRejectA.get(ar).focus();
								calcProduction(ar);
							}
						}	
					}

				}
			});


			tbTxtShiftB.add(ar, new AmountField());
			tbTxtShiftB.get(ar).setImmediate(true);
			tbTxtShiftB.get(ar).setWidth("100%");
			tbTxtShiftB.get(ar).setHeight("-1px");
			tbTxtShiftB.get(ar).addListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					if(!tbTxtShiftB.get(ar).getValue().toString().isEmpty())
					{
					   int a=calcProduction(ar);

						/*Date date1= new Date() ;
						Date date2= (Date) dProductionDate.getValue();
						long seconds = (date1.getTime()-date2.getTime())/1000;
						System.out.println("second is:"+seconds);*/
						
						
						    Date date1= new Date();
							//Date date2= (Date) dProductionDate.getValue();
							String strDate=dateFormat.format(dProductionDate.getValue());
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
						
						
						
						if(!isUpdate)
						{
							if (!sessionBean.getUserId().toString().equalsIgnoreCase("22") && seconds>259199  )
							{
									showNotification("Not Permited TO Entry Time Limit Exceed")	;
									tbTxtShiftB.get(ar).setValue("");
									tbTxtShiftB.get(ar).focus();
									calcProduction(ar);	
										
							}
							else
							{
								if(a==0)
								{
								showNotification("Total Consumption Exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
								tbTxtShiftB.get(ar).setValue("");
								tbTxtShiftB.get(ar).focus();
								calcProduction(ar); 
								}
								
							}
							
						}	
					}
					
				}
			});
			
			
			
			

			/*tbTxtShiftB.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event)
				{
					if(!tbTxtShiftB.get(ar).getValue().toString().isEmpty())
					{
						int a=calcProduction(ar);
						if(!isUpdate){
							if(a==0){
								showNotification("Total Consumption Exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
								tbTxtShiftB.get(ar).setValue("");
								tbTxtShiftB.get(ar).focus();
								calcProduction(ar);
							}
						}	
					}

				}
			});*/

			tbTxtRejectB.add(ar, new AmountField());
			tbTxtRejectB.get(ar).setImmediate(true);
			tbTxtRejectB.get(ar).setWidth("100%");
			tbTxtRejectB.get(ar).setHeight("-1px");

			tbTxtRejectB.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(!tbTxtRejectB.get(ar).getValue().toString().isEmpty())
					{
						int a=calcProduction(ar);
						if(!isUpdate){
							if(a==0){
								showNotification("Total Consumption Exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
								tbTxtRejectB.get(ar).setValue("");
								tbTxtRejectB.get(ar).focus();
								calcProduction(ar);
							}
						}	
					}

				}
			});

			tbTxtRemarks.add(ar, new TextField());
			tbTxtRejectB.get(ar).setImmediate(true);
			tbTxtRemarks.get(ar).setWidth("100%");
			tbTxtRemarks.get(ar).setHeight("-1px");

			tbLblSemiFgStandardNo.add(ar, new Label());
			tbLblSemiFgStandardNo.get(ar).setWidth("100%");
			tbLblSemiFgStandardNo.get(ar).setHeight("-1px");

			tbLblSemiFgStandardSlFlag.add(ar, new Label());
			tbLblSemiFgStandardSlFlag.get(ar).setWidth("100%");
			tbLblSemiFgStandardSlFlag.get(ar).setHeight("-1px");

			tbLblSemiFgStandardUnitPrice.add(ar, new Label());
			tbLblSemiFgStandardUnitPrice.get(ar).setWidth("100%");
			tbLblSemiFgStandardUnitPrice.get(ar).setHeight("-1px");

			tbLblSemiFgStandardAmount.add(ar, new Label());
			tbLblSemiFgStandardAmount.get(ar).setWidth("100%");
			tbLblSemiFgStandardAmount.get(ar).setHeight("-1px");

			tbLblInkStandardNo.add(ar, new Label());
			tbLblInkStandardNo.get(ar).setWidth("100%");
			tbLblInkStandardNo.get(ar).setHeight("-1px");

			tbLblInkStandardSlFlag.add(ar, new Label());
			tbLblInkStandardSlFlag.get(ar).setWidth("100%");
			tbLblInkStandardSlFlag.get(ar).setHeight("-1px");

			tbLblInkStandardUnitPrice.add(ar, new Label());
			tbLblInkStandardUnitPrice.get(ar).setWidth("100%");
			tbLblInkStandardUnitPrice.get(ar).setHeight("-1px");

			tbLblInkStandardAmount.add(ar, new Label());
			tbLblInkStandardAmount.get(ar).setWidth("100%");
			tbLblInkStandardAmount.get(ar).setHeight("-1px");



			table.addItem(new Object[]{tbSl.get(ar),tbChkShow.get(ar),tbCmbBatchNo.get(ar),tbCmbMachineName.get(ar),tbCmbSemiFgName.get(ar),
					tbCmbFGName.get(ar),tbTxtShiftA.get(ar),tbTxtRejectA.get(ar),tbTxtShiftB.get(ar),tbTxtRejectB.get(ar),
					tbTxtRemarks.get(ar),
					tbLblSemiFgStandardNo.get(ar),tbLblSemiFgStandardSlFlag.get(ar),
					tbLblSemiFgStandardUnitPrice.get(ar),tbLblSemiFgStandardAmount.get(ar),
					tbLblInkStandardNo.get(ar),tbLblInkStandardSlFlag.get(ar),tbLblInkStandardUnitPrice.get(ar),
					tbLblInkStandardAmount.get(ar),tbchkisFg.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private boolean doubleEntryCheck(int ar) {
		String batchNo=tbCmbBatchNo.get(ar).getValue().toString();
		String semiFgName=tbCmbSemiFgName.get(ar).getValue().toString();
		String fgName=tbCmbFGName.get(ar).getValue().toString();
		String machineName=tbCmbMachineName.get(ar).getValue().toString();

		for(int a=0;a<tbChkShow.size();a++){
			if(a!=ar){
				if(tbCmbFGName.get(a).getValue()!=null&&
						tbCmbBatchNo.get(a).getValue()!=null&&
						tbCmbSemiFgName.get(a).getValue()!=null&&
						tbCmbMachineName.get(a).getValue()!=null)
				{
					String batchNo1=tbCmbBatchNo.get(a).getValue().toString();
					String semiFgName1=tbCmbSemiFgName.get(a).getValue().toString();
					String fgName1=tbCmbFGName.get(a).getValue().toString();
					String machineName1=tbCmbMachineName.get(a).getValue().toString();
					if(batchNo.equalsIgnoreCase(batchNo1)
							&&machineName.equalsIgnoreCase(machineName1)
							&&semiFgName.equalsIgnoreCase(semiFgName1)
							&&fgName.equalsIgnoreCase(fgName1)){
						return false;
					}
				}
			}
		}
		return true;
	}
	private void tbCmbMachineLoadData(int ar) {
		tbCmbMachineName.get(ar).removeAllItems();
		String sql= "select vMachineCode,vMachineName  from tbMachineInfo  "
				+"where productionTypeId not in ('PT-1','PT-2') order by vMachineName ";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbMachineName.get(ar).addItem(element[0]);
			tbCmbMachineName.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}
	private void tbCmbSemiFgLoadData(int ar) {
		tbCmbSemiFgName.get(ar).removeAllItems();
		Iterator iter=dbService("select productId,productName from tbIssueToLabelPrintingInfo a "+
				" inner join tbIssueToLabelPrintingDetails b on a.issueNo=b.issueNo  "+
				" where a.batchNo='"+tbCmbBatchNo.get(ar).getValue()+"' and a.issueTo='"+cmbProductionStep.getValue()+"' order by productName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbSemiFgName.get(ar).addItem(element[0]);
			tbCmbSemiFgName.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1270px");
		mainLayout.setHeight("520px");

		lblProductionNo = new Label("Production No :");
		lblProductionNo.setImmediate(false);
		lblProductionNo.setWidth("-1px");
		lblProductionNo.setHeight("-1px");
		mainLayout.addComponent(lblProductionNo, "top:20.0px;left:20.0px;");

		txtProductionNo =new TextRead();
		txtProductionNo.setImmediate(true);
		txtProductionNo.setWidth("110px");
		txtProductionNo.setHeight("-1px");
		mainLayout.addComponent(txtProductionNo, "top:18.0px;left:130.0px;");

		lblProductionDate = new Label("Production Date:");
		lblProductionDate.setImmediate(false);
		lblProductionDate.setWidth("-1px");
		lblProductionDate.setHeight("-1px");
		mainLayout.addComponent(lblProductionDate, "top:42.0px;left:20.0px;");

		dProductionDate =new PopupDateField();
		dProductionDate.setImmediate(true);
		dProductionDate.setWidth("110px");
		dProductionDate.setHeight("-1px");
		dProductionDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dProductionDate.setDateFormat("dd-MM-yyyy");
		dProductionDate.setValue(new java.util.Date());
		mainLayout.addComponent(dProductionDate, "top:40.0px;left:130.0px;");

		lblProductionStep = new Label();
		lblProductionStep.setImmediate(false);
		lblProductionStep.setWidth("-1px");
		lblProductionStep.setHeight("-1px");
		lblProductionStep.setValue("Production Step :");
		mainLayout.addComponent(lblProductionStep, "top:68.0px;left:20.0px;");

		cmbProductionStep=new ComboBox();
		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setWidth("150px");
		cmbProductionStep.setHeight("-1px");
		cmbProductionStep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbProductionStep, "top:66.0px;left:130.0px;");
		cmbProductionStep.addItem("Dry Offset Printing");
		cmbProductionStep.addItem("Screen Printing");
		cmbProductionStep.addItem("Heat Trasfer Label");
		cmbProductionStep.addItem("Manual Printing");
		cmbProductionStep.addItem("Labeling");
		cmbProductionStep.addItem("Cap Folding");
		cmbProductionStep.addItem("Shrink");
		//cmbProductionStep.addItem("Stretch Blow Molding");

		lblBatchQty = new Label("Batch Qty [SEMI FG] :");
		lblBatchQty.setImmediate(false);
		lblBatchQty.setWidth("-1px");
		lblBatchQty.setHeight("-1px");
		mainLayout.addComponent(lblBatchQty, "top:20.0px;left:300.0px;");

		txtBatchQty =new TextRead(1);
		txtBatchQty.setImmediate(true);
		txtBatchQty.setWidth("100px");
		txtBatchQty.setHeight("-1px");
		mainLayout.addComponent(txtBatchQty, "top:18.0px;left:460.0px;");

		lblTotalProduction = new Label("Total Production Of Batch:");
		lblTotalProduction.setImmediate(false);
		lblTotalProduction.setWidth("-1px");
		lblTotalProduction.setHeight("-1px");
		mainLayout.addComponent(lblTotalProduction, "top:45.0px;left:300.0px;");

		txtTotalProduction=new TextRead(1);
		txtTotalProduction.setImmediate(true);
		txtTotalProduction.setWidth("100px");
		txtTotalProduction.setHeight("-1px");
		mainLayout.addComponent(txtTotalProduction, "top:43.0px;left:460.0px;");

		lblTotalReject = new Label("Total Rejection Of Batch: ");
		lblTotalReject.setImmediate(false);
		lblTotalReject.setWidth("-1px");
		lblTotalReject.setHeight("-1px");
		mainLayout.addComponent(lblTotalReject, "top:70.0px;left:300.0px;");

		txtTotalReject=new TextRead(1);
		txtTotalReject.setImmediate(true);
		txtTotalReject.setWidth("100px");
		txtTotalReject.setHeight("-1px");
		mainLayout.addComponent(txtTotalReject, "top:68.0px;left:460.0px;");

		lblRemainQty = new Label("Remain Qty Of Batch:");
		lblRemainQty.setImmediate(false);
		lblRemainQty.setWidth("-1px");
		lblRemainQty.setHeight("-1px");
		mainLayout.addComponent(lblRemainQty, "top:20.0px;left:580.0px;");

		txtRemainQty =new TextRead(1);
		txtRemainQty.setImmediate(true);
		txtRemainQty.setWidth("100px");
		txtRemainQty.setHeight("-1px");
		mainLayout.addComponent(txtRemainQty, "top:18.0px;left:710.0px;");

		lblProductionQty = new Label("Production Qty [FG]:");
		lblProductionQty.setImmediate(false);
		lblProductionQty.setWidth("-1px");
		lblProductionQty.setHeight("-1px");
		mainLayout.addComponent(lblProductionQty, "top:45.0px;left:580.0px;");

		txtProductionQty =new TextRead(1);
		txtProductionQty.setImmediate(true);
		txtProductionQty.setWidth("100px");
		txtProductionQty.setHeight("-1px");
		mainLayout.addComponent(txtProductionQty, "top:43.0px;left:710.0px;");

		lblRejectQty = new Label("Reject Qty [FG]:");
		lblRejectQty.setImmediate(false);
		lblRejectQty.setWidth("-1px");
		lblRejectQty.setHeight("-1px");
		mainLayout.addComponent(lblRejectQty, "top:70.0px;left:580.0px;");

		txtRejectQty =new TextRead(1);
		txtRejectQty.setImmediate(true);
		txtRejectQty.setWidth("100px");
		txtRejectQty.setHeight("-1px");
		mainLayout.addComponent(txtRejectQty, "top:68.0px;left:710.0px;");


		lblPresentProductionFG = new Label("Present Production [FG]:");
		lblPresentProductionFG.setImmediate(false);
		lblPresentProductionFG.setWidth("-1px");
		lblPresentProductionFG.setHeight("-1px");
		mainLayout.addComponent(lblPresentProductionFG, "top:20.0px;left:840.0px;");

		txtPresentProductionFG =new TextRead(1);
		txtPresentProductionFG.setImmediate(true);
		txtPresentProductionFG.setWidth("100px");
		txtPresentProductionFG.setHeight("-1px");
		mainLayout.addComponent(txtPresentProductionFG, "top:18.0px;left:990.0px;");


		lblPresentProductionFG = new Label("Present Rejection [FG]:");
		lblPresentProductionFG.setImmediate(false);
		lblPresentProductionFG.setWidth("-1px");
		lblPresentProductionFG.setHeight("-1px");
		mainLayout.addComponent(lblPresentProductionFG, "top:45.0px;left:840.0px;");

		txtpresentRejectionFG =new TextRead(1);
		txtpresentRejectionFG.setImmediate(true);
		txtpresentRejectionFG.setWidth("100px");
		txtpresentRejectionFG.setHeight("-1px");
		mainLayout.addComponent(txtpresentRejectionFG, "top:43.0px;left:990.0px;");

		lblTotalConsumption = new Label("Total Consumption [FG]:");
		lblTotalConsumption.setImmediate(false);
		lblTotalConsumption.setWidth("-1px");
		lblTotalConsumption.setHeight("-1px");
		mainLayout.addComponent(lblTotalConsumption, "top:70.0px;left:840.0px;");

		txtTotalConsumption =new TextRead(1);
		txtTotalConsumption.setImmediate(true);
		txtTotalConsumption.setWidth("100px");
		txtTotalConsumption.setHeight("-1px");
		mainLayout.addComponent(txtTotalConsumption, "top:68.0px;left:990.0px;");

		lblVoucherNo = new Label("Voucher No:");
		lblVoucherNo.setImmediate(false);
		lblVoucherNo.setWidth("-1px");
		lblVoucherNo.setHeight("-1px");
		mainLayout.addComponent(lblVoucherNo, "top:20.0px;left:1095.0px;");

		txtVoucherNo =new TextRead(1);
		txtVoucherNo.setImmediate(true);
		txtVoucherNo.setWidth("100px");
		txtVoucherNo.setHeight("-1px");
		mainLayout.addComponent(txtVoucherNo, "top:18.0px;left:1165.0px;");


		table = new Table();
		table.setWidth("100%");
		table.setHeight("350px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 15);
		table.setColumnAlignment("SL", table.ALIGN_CENTER);

		table.addContainerProperty("Show",  CheckBox.class , new  CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Show",20);

		table.addContainerProperty("Batch No",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Batch No",110);

		table.addContainerProperty("Machine Name",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Machine Name",150);

		table.addContainerProperty("SemiFg Name",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SemiFg Name",210);

		table.addContainerProperty("FG Name",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("FG Name",210);

		table.addContainerProperty("ShiftA",  AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ShiftA",70);

		table.addContainerProperty("RejectA", AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("RejectA",70);

		table.addContainerProperty("ShiftB",  AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ShiftB",70);

		table.addContainerProperty("RejectB",  AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("RejectB",70);

		table.addContainerProperty("Remarks",  TextField.class , new TextField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Remarks",80);

		table.addContainerProperty("S.FG.No", Label.class, new Label());
		table.setColumnWidth("S.FG.No", 30);
		table.setColumnCollapsed("S.FG.No", true);

		table.addContainerProperty("S.FG.SL", Label.class, new Label());
		table.setColumnWidth("S.FG.SL", 30);
		table.setColumnCollapsed("S.FG.SL", true);

		table.addContainerProperty("SemiFgStandardUnitPrice", Label.class, new Label());
		table.setColumnWidth("SemiFgStandardUnitPrice", 30);
		table.setColumnCollapsed("SemiFgStandardUnitPrice", true);

		table.addContainerProperty("SemiFgStandardAmount", Label.class, new Label());
		table.setColumnWidth("SemiFgStandardAmount", 30);
		table.setColumnCollapsed("SemiFgStandardAmount", true);

		table.addContainerProperty("InkStandardNo", Label.class, new Label());
		table.setColumnWidth("InkStandardNo", 30);
		table.setColumnCollapsed("InkStandardNo", true);

		table.addContainerProperty("InkStandardSlFlag", Label.class, new Label());
		table.setColumnWidth("InkStandardSlFlag", 30);
		table.setColumnCollapsed("InkStandardSlFlag", true);

		table.addContainerProperty("InkStandardUnitPrice", Label.class, new Label());
		table.setColumnWidth("InkStandardUnitPrice", 30);
		table.setColumnCollapsed("InkStandardUnitPrice", true);

		table.addContainerProperty("InkStandardAmount", Label.class, new Label());
		table.setColumnWidth("InkStandardAmount", 30);
		table.setColumnCollapsed("InkStandardAmount", true);

		table.addContainerProperty("IsFg",  CheckBox.class , new  CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("IsFg",20);
		table.setColumnCollapsed("IsFg", true);

		mainLayout.addComponent(table, "top:100.0px;left:5.0px;");
		tableInitialise();

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:460.0px;left:0.0px;");
		mainLayout.addComponent(cButton, "top:480.0px;left:300.0px;");
		setStyleName("cwindow");
		return mainLayout;
	}

	private void focusEnter()
	{
		allComp.add(cmbProductionStep);

		for(int a=0;a<tbChkShow.size();a++){
			allComp.add(tbCmbBatchNo.get(a));
			allComp.add(tbCmbMachineName.get(a));
			allComp.add(tbCmbSemiFgName.get(a));
			allComp.add(tbCmbFGName.get(a));
			allComp.add(tbTxtShiftA.get(a));
			allComp.add(tbTxtRejectA.get(a));
			allComp.add(tbTxtShiftB.get(a));
			allComp.add(tbTxtRejectB.get(a));
			allComp.add(tbTxtRemarks.get(a));
		}

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);
		allComp.add(cButton.btnFind);

		new FocusMoveByEnter(this,allComp);
	}
	public void txtClear()
	{
		txtProductionNo.setValue("");
		dProductionDate.setValue(new java.util.Date());
		cmbProductionStep.setValue(null);
		txtBatchQty.setValue("");
		txtProductionQty.setValue("");
		txtTotalReject.setValue("");
		txtTotalProduction.setValue("");
		txtRejectQty.setValue("");
		txtRemainQty.setValue("");
		txtPresentProductionFG.setValue("");
		txtpresentRejectionFG.setValue("");
		txtTotalConsumption.setValue("");

		tableClear();
	}
	private void tableClear()
	{
		table.setColumnCollapsed("IsFg", true);	
		for(int a=0;a<tbSl.size();a++){
			tbChkShow.get(a).setValue(false);
			tbCmbBatchNo.get(a).setValue(null);
			tbCmbMachineName.get(a).setValue(null);
			tbCmbSemiFgName.get(a).setValue(null);
			tbCmbFGName.get(a).setValue(null);
			tbTxtShiftA.get(a).setValue("");
			tbTxtRejectA.get(a).setValue("");
			tbTxtShiftB.get(a).setValue("");
			tbTxtRejectB.get(a).setValue("");

			tbLblInkStandardNo.get(a).setValue("");
			tbLblInkStandardSlFlag.get(a).setValue("");
			tbLblInkStandardUnitPrice.get(a).setValue("");
			tbLblInkStandardAmount.get(a).setValue("");
			tbLblSemiFgStandardNo.get(a).setValue("");
			tbLblSemiFgStandardSlFlag.get(a).setValue("");
			tbLblSemiFgStandardUnitPrice.get(a).setValue("");
			tbLblSemiFgStandardAmount.get(a).setValue("");
		}
	}

	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		isFind=false;
		isUpdate=false;
		cmbProductionStep.focus();

	}

	private void productionNoLoadData() {
		Iterator iter=dbService("select 0,isnull(MAX(productionNo),0)+1 id from tbLabelingPrintingDailyProductionInfo");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtProductionNo.setValue(element[1].toString());
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
