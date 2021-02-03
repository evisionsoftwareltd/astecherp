package com.example.productionTransaction;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

public class DailyProductionEntryLacqure extends Window
{
	private SessionBean sessionBean;
	private Label lblProductionNo;
	private Label lblProductionDate;
	private Label lblProductionStep;

	private Label lblBatchQty;
	private Label lblProductionQty;
	private Label lblTotalReject;
	private Label lblTotalProduction,lblRemainQty,lblRejectQty,lblPresentProductionFG,lblpresentJejectionFG,lblTotalConsumption;

	private TextRead txtProductionNo,txtPresentProductionFG,txtpresentRejectionFG;
	private PopupDateField dProductionDate;
	private ComboBox cmbProductionStep;

	private TextRead txtBatchQty,txtProductionQty,txtTotalReject,txtTotalProduction,txtRemainQty,txtRejectQty,txtTotalConsumption;

	private Table table;
	ArrayList<Label>tbSl = new ArrayList<Label>();
	private ArrayList<CheckBox> tbChkShow = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkisFG = new ArrayList<CheckBox>();
	private ArrayList<ComboBox> tbCmbBatchNo = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbCmbMachineName = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbCmbSemiFgName = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbCmbFGName = new ArrayList<ComboBox>();
	private ArrayList<AmountField> tbTxtShiftA = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbTxtRejectA = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbTxtShiftB = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbTxtRejectB = new ArrayList<AmountField>();
	private ArrayList<TextField> tbTxtRemarks = new ArrayList<TextField>();

	ArrayList<Component> allComp = new ArrayList<Component>();

	private CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	boolean isUpdate=false,isFind=false;
	private AbsoluteLayout mainLayout;
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat df=new DecimalFormat("#0");
	private TextField txtProductionNoFind=new TextField();

	HashMap hmap=new HashMap();

	public DailyProductionEntryLacqure(SessionBean sessionBean,String caption,int a)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("Daily Production Entry[LACQURE] :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		focusEnter();
		btnAction();
	}
	private void tbCmbDataLoad(int ar){

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
		//table.setEnabled(!b);
		for(int a=0;a<tbChkShow.size();a++){
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
			tbChkisFG.get(a).setEnabled(!b);
		}

	}
	private boolean checkTableData(){
		for(int a=0;a<tbSl.size();a++){
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
	private boolean checkValidation(){
		/*double shiftA,rejectA,shiftB,rejectB;
		shiftA=Double.parseDouble("0"+tbTxtShiftA.get(0).getValue().toString());
		rejectA=Double.parseDouble("0"+tbTxtRejectA.get(0).getValue().toString());
		shiftB=Double.parseDouble("0"+tbTxtShiftB.get(0).getValue().toString());
		rejectB=Double.parseDouble("0"+tbTxtRejectB.get(0).getValue().toString());*/

		if(cmbProductionStep.getValue()!=null){
			if(tbCmbBatchNo.get(0).getValue()!=null&&tbCmbMachineName.get(0).getValue()!=null&&
					tbCmbFGName.get(0).getValue()!=null){
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
			session.createSQLQuery("delete from tbLacqureDailyProductionInfo where productionNo='"+txtProductionNo.getValue()+ "'").executeUpdate();
			///System.out.println("delete tbLabelProductionInfo where ProductionNo='"+txtProductionNo.getValue()+ "' ");

			session.createSQLQuery("delete from tbLacqureDailyProductionDetails where productionNo='"+txtProductionNo.getValue()+ "'").executeUpdate();
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
	private void insertData(){
		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String type="New";
		String productionNo="";
		int isfg=0;
		if(isUpdate){
			type="update";
			productionNo=txtProductionNo.getValue().toString();
		}
		else{
			productionNoLoadData();
		}
		try{
			String sqlInfo="insert into tbLacqureDailyProductionInfo(productionNo,productionDate," +
					"productionStep,userIp,userName,entryTime,isApproved)values('"+txtProductionNo.getValue()+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"','"+cmbProductionStep.getValue()+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,0)";
			session.createSQLQuery(sqlInfo).executeUpdate();

			String sqlInfoUd="insert into tbUdLacqureDailyProductionInfo(productionNo,productionDate," +
					"productionStep,userIp,userName,entryTime,isApproved,type)values('"+txtProductionNo.getValue()+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd").format(dProductionDate.getValue())+"','"+cmbProductionStep.getValue()+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,0,'"+type+"')";
			session.createSQLQuery(sqlInfoUd).executeUpdate();

			for(int a=0;a<tbChkShow.size();a++)
			{
				double shiftA,rejectA,shiftB,rejectB;
				shiftA=Double.parseDouble("0"+tbTxtShiftA.get(a).getValue().toString());
				rejectA=Double.parseDouble("0"+tbTxtRejectA.get(a).getValue().toString());
				shiftB=Double.parseDouble("0"+tbTxtShiftB.get(a).getValue().toString());
				rejectB=Double.parseDouble("0"+tbTxtRejectB.get(a).getValue().toString());

				if(tbCmbBatchNo.get(a).getValue()!=null&&tbCmbMachineName.get(a).getValue()!=null&&
						tbCmbFGName.get(a).getValue()!=null&&
						(shiftA+rejectA+shiftB+rejectB)>0)
				{
					double totalShift=shiftA+shiftB;
					double totalReject=rejectA+rejectB;
                    
					if(tbChkisFG.get(a).booleanValue()==true)
					{
					   isfg=1;	
					}
					else
					{
						isfg=0;
					}
					
					String sqlDetails="insert into tbLacqureDailyProductionDetails(productionNo,batchNo," +
							"machineCode,machineName, "+
							" fgCode,fgName,shiftA,shiftB,rejectA,rejectB,totalShift,totalReject,remarks," +
							"isApproved,isFG) "+
							" values('"+txtProductionNo.getValue()+"','"+tbCmbBatchNo.get(a).getValue()+"'," +
							"'"+tbCmbMachineName.get(a).getValue()+"','"+tbCmbMachineName.get(a).getItemCaption(tbCmbMachineName.get(a).getValue())+"'," +
							//"'"+tbCmbSemiFgName.get(a).getValue()+"','"+tbCmbSemiFgName.get(a).getItemCaption(tbCmbSemiFgName.get(a).getValue())+"'," +
							"'"+tbCmbFGName.get(a).getValue()+"','"+tbCmbFGName.get(a).getItemCaption(tbCmbFGName.get(a).getValue())+"'," +
							"'"+tbTxtShiftA.get(a).getValue()+"','"+tbTxtShiftB.get(a).getValue()+"','"+tbTxtRejectA.get(a).getValue()+"'," +
							"'"+tbTxtRejectB.get(a).getValue()+"','"+totalShift+"','"+totalReject+"','"+tbTxtRemarks.get(a).getValue()+"','0','"+isfg+"')" ;
							//"'"+handleFlag+"','"+referenceFgId+"','"+referenceFgName+"','"+referenceFgQty+"')";

					session.createSQLQuery(sqlDetails).executeUpdate();

					String sqlDetailsUd="insert into tbUdLacqureDailyProductionDetails(productionNo,batchNo,machineCode,machineName, "+
							" fgCode,fgName,shiftA,shiftB,rejectA,rejectB,totalShift,totalReject,remarks,isApproved,type,isFG) "+
							" values('"+txtProductionNo.getValue()+"','"+tbCmbBatchNo.get(a).getValue()+"'," +
							"'"+tbCmbMachineName.get(a).getValue()+"','"+tbCmbMachineName.get(a).getItemCaption(tbCmbMachineName.get(a).getValue())+"'," +
							//"'"+tbCmbSemiFgName.get(a).getValue()+"','"+tbCmbSemiFgName.get(a).getItemCaption(tbCmbSemiFgName.get(a).getValue())+"'," +
							"'"+tbCmbFGName.get(a).getValue()+"','"+tbCmbFGName.get(a).getItemCaption(tbCmbFGName.get(a).getValue())+"'," +
							"'"+tbTxtShiftA.get(a).getValue()+"','"+tbTxtShiftB.get(a).getValue()+"','"+tbTxtRejectA.get(a).getValue()+"'," +
							"'"+tbTxtRejectB.get(a).getValue()+"','"+totalShift+"','"+totalReject+"','"+tbTxtRemarks.get(a).getValue()+"','0','"+type+"','"+isfg+"')" ;
							//"'"+handleFlag+"','"+referenceFgId+"','"+referenceFgName+"','"+referenceFgQty+"')";

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
	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;

		for(int a=0;a<hmap.size();a++){
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
				" '' semifgCode,b.fgCode,b.shiftA,b.rejectA,b.shiftB,b.rejectB,b.remarks,b.fgName,'' handleFlag,isFG  "+
				" from tbLacqureDailyProductionInfo a inner join tbLacqureDailyProductionDetails b "+ 
				" on a.productionNo=b.productionNo where a.productionNo='"+productionNo+"'");
		int a=0;
		int ar=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==0){
				txtProductionNo.setValue(element[0]);
				dProductionDate.setValue(element[1]);
				cmbProductionStep.setValue(element[2]);
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
			
			if(Integer.parseInt(element[14].toString())==1)
			{
				tbChkisFG.get(a).setValue(true);	
				
			}
			else
			{
				tbChkisFG.get(a).setValue(false);	
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
		Window win = new DalilyProductionEntryLacqureFind(sessionBean,txtProductionNoFind);

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
				if(cmbProductionStep.getValue()!=null){
					System.out.println(cmbProductionStep.getValue());
					for(int a=0;a<tbCmbBatchNo.size();a++){
						tbCmbBatchNoLoadData(a);
					}
				}
				else{
					for(int a=0;a<tbCmbBatchNo.size();a++){
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

	}


	private void tbCmbBatchNoLoadData(int ar) {
		tbCmbBatchNo.get(ar).removeAllItems();
		Iterator iter=dbService("select 0,batchNo from tbIssueToLacqureInfo where issueTo " +
				"like '"+cmbProductionStep.getValue()+"' order by issueDate");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbBatchNo.get(ar).addItem(element[1]);
			tbCmbBatchNo.get(ar).setItemCaption(element[1], element[1].toString());
		}
	}

	private void tbCmbFgLoadData(int ar) {
		tbCmbFGName.get(ar).removeAllItems();
		Iterator iter=dbService("select semiFgSubId,semiFgSubName from tbSemiFgSubInformation " +
				"where semiFgId='"+tbCmbSemiFgName.get(ar).getValue()+"' order by semiFgSubName");
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
		String sql="select productId,productName from tbIssueToLacqureDetails where batchNo ='"+tbCmbBatchNo.get(ar).getValue()+"' order by productName";
		Iterator<?>iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbFGName.get(ar).addItem(element[0]);
			tbCmbFGName.get(ar).setItemCaption(element[0], element[1].toString());
			
		}
	}
	private void batchDataLoadFgWise(int ar){
		String sql="select batchQty,totalProduction,totalRejection,(batchQty-(totalProduction+totalRejection))remainQty from ( "+
				" select isnull(SUM(issueQty),0)batchQty,(select isnull(SUM(totalShift),0) from tbLacqureDailyProductionDetails  "+
						" where batchNo='"+tbCmbBatchNo.get(ar).getValue()+"' and fgCode like '"+tbCmbFGName.get(ar).getValue()+"')totalProduction, "+
						" (select isnull(SUM(totalReject),0) from tbLacqureDailyProductionDetails  "+
						" where batchNo='"+tbCmbBatchNo.get(ar).getValue()+"' and fgCode like '"+tbCmbFGName.get(ar).getValue()+"')totalRejection  "+
						" from tbIssueToLacqureDetails where batchNo='"+tbCmbBatchNo.get(ar).getValue()+"' and productId like '"+tbCmbFGName.get(ar).getValue()+"' "+
						" ) a";
		Iterator<?>iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtBatchQty.setValue(df.format(element[0]));
			txtTotalProduction.setValue(df.format(element[1]));
			txtTotalReject.setValue(df.format(element[2]));
			txtRemainQty.setValue(df.format(element[3]));
		}
	}
	private int calcProduction(int ar){
		double shiftAProduction,shiftBProduction,shiftARejection,shiftBRejection,totalProduction,totalRejection,remainQty;
		shiftAProduction=Double.parseDouble(tbTxtShiftA.get(ar).getValue().toString().isEmpty()?
				"0.0":tbTxtShiftA.get(ar).getValue().toString());
		shiftBProduction=Double.parseDouble(tbTxtShiftB.get(ar).getValue().toString().isEmpty()?
				"0.0":tbTxtShiftB.get(ar).getValue().toString());
		shiftARejection=Double.parseDouble(tbTxtRejectA.get(ar).getValue().toString().isEmpty()?
				"0.0":tbTxtRejectA.get(ar).getValue().toString());
		shiftBRejection=Double.parseDouble(tbTxtRejectB.get(ar).getValue().toString().isEmpty()?
				"0.0":tbTxtRejectB.get(ar).getValue().toString());
		remainQty=Double.parseDouble(txtRemainQty.getValue().toString().isEmpty()?"0":txtRemainQty.getValue().toString());
		totalProduction=shiftAProduction+shiftBProduction;
		totalRejection=shiftARejection+shiftBRejection;
		txtPresentProductionFG.setValue(df.format(totalProduction));
		txtpresentRejectionFG.setValue(df.format(totalRejection));
		txtTotalConsumption.setValue(df.format(totalProduction+totalRejection));

		if((totalProduction+totalRejection)>remainQty){
			return 0;
		}
		return 1;
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
			
			tbChkShow.get(ar).addListener(new ValueChangeListener() {
				
				public void valueChange(ValueChangeEvent event) {
				
					if(tbChkShow.get(ar).booleanValue())
					{
						for(int a=0;a<tbChkShow.size();a++){
							if(a!=ar){
								tbChkShow.get(a).setValue(false);
							}
						}
						if(tbCmbBatchNo.get(ar).getValue()!=null&&
							//tbCmbSemiFgName.get(ar).getValue()!=null&&
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
						
							batchDataLoad(ar);
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

			/*tbCmbSemiFgName.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(tbCmbSemiFgName.get(ar).getValue()!=null){
						tbCmbFgLoadData(ar);
						if(tbCmbBatchNo.get(ar).getValue()!=null){
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
					}
				}
			});*/

			tbCmbFGName.add(ar, new ComboBox());
			tbCmbFGName.get(ar).setImmediate(true);
			tbCmbFGName.get(ar).setWidth("100%");
			tbCmbFGName.get(ar).setHeight("-1px");
			tbCmbFGName.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			tbCmbFGName.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(tbCmbFGName.get(ar).getValue()!=null&&
							tbCmbBatchNo.get(ar).getValue()!=null&&
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
						}
						else{
							
							batchDataLoadFgWise(ar);
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
					
				}
			});

			tbTxtShiftA.add(ar, new AmountField());
			tbTxtShiftA.get(ar).setImmediate(true);
			tbTxtShiftA.get(ar).setWidth("100%");
			tbTxtShiftA.get(ar).setHeight("-1px");

			tbTxtShiftA.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					int a=calcProduction(ar);
					if(!isUpdate){
						if(a==0){
							showNotification("Total Consumption Exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
							tbTxtShiftA.get(ar).setValue("");
							tbTxtShiftA.get(ar).focus();
							calcProduction(ar);
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
			});


			tbTxtShiftB.add(ar, new AmountField());
			tbTxtShiftB.get(ar).setImmediate(true);
			tbTxtShiftB.get(ar).setWidth("100%");
			tbTxtShiftB.get(ar).setHeight("-1px");

			tbTxtShiftB.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
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
			});

			tbTxtRejectB.add(ar, new AmountField());
			tbTxtRejectB.get(ar).setImmediate(true);
			tbTxtRejectB.get(ar).setWidth("100%");
			tbTxtRejectB.get(ar).setHeight("-1px");

			tbTxtRejectB.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
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
			});

			tbTxtRemarks.add(ar, new TextField());
			tbTxtRejectB.get(ar).setImmediate(true);
			tbTxtRemarks.get(ar).setWidth("100%");
			tbTxtRemarks.get(ar).setHeight("-1px");
			
			tbChkisFG.add(ar, new CheckBox());
			tbChkisFG.get(ar).setImmediate(true);
			tbChkisFG.get(ar).setWidth("100%");
			tbChkisFG.get(ar).setHeight("-1px");

			table.addItem(new Object[]{tbSl.get(ar),tbChkShow.get(ar),tbCmbBatchNo.get(ar),tbCmbMachineName.get(ar),tbCmbSemiFgName.get(ar),
					tbCmbFGName.get(ar),tbTxtShiftA.get(ar),tbTxtRejectA.get(ar),tbTxtShiftB.get(ar),tbTxtRejectB.get(ar),tbTxtRemarks.get(ar),tbChkisFG.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private boolean doubleEntryCheck(int ar) {
		String batchNo=tbCmbBatchNo.get(ar).getValue().toString();
		//String semiFgName=tbCmbSemiFgName.get(ar).getValue().toString();
		String fgName=tbCmbFGName.get(ar).getValue().toString();
		String machineName=tbCmbMachineName.get(ar).getValue().toString();

		for(int a=0;a<tbChkShow.size();a++){
			if(a!=ar){
				if(tbCmbFGName.get(a).getValue()!=null&&
						tbCmbBatchNo.get(a).getValue()!=null&&
					//	tbCmbSemiFgName.get(a).getValue()!=null&&
						tbCmbMachineName.get(a).getValue()!=null)
				{
					String batchNo1=tbCmbBatchNo.get(a).getValue().toString();
					//String semiFgName1=tbCmbSemiFgName.get(a).getValue().toString();
					String fgName1=tbCmbFGName.get(a).getValue().toString();
					String machineName1=tbCmbMachineName.get(a).getValue().toString();
					if(batchNo.equalsIgnoreCase(batchNo1)
							&&machineName.equalsIgnoreCase(machineName1)
							//&&semiFgName.equalsIgnoreCase(semiFgName1)
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
	/*private void tbCmbSemiFgLoadData(int ar) {
		tbCmbSemiFgName.get(ar).removeAllItems();
		Iterator iter=dbService("select productId,productName from tbIssueToLabelPrintingInfo a "+
				" inner join tbIssueToLabelPrintingDetails b on a.issueNo=b.issueNo  "+
				" where a.batchNo='"+tbCmbBatchNo.get(ar).getValue()+"' and a.issueTo='"+cmbProductionStep.getValue()+"' order by productName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbSemiFgName.get(ar).addItem(element[0]);
			tbCmbSemiFgName.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}*/

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1170px");
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
		mainLayout.addComponent(txtProductionNo, "top:18.0px;left:160.0px;");

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
		mainLayout.addComponent(dProductionDate, "top:40.0px;left:160.0px;");

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
		mainLayout.addComponent(cmbProductionStep, "top:66.0px;left:160.0px;");
		cmbProductionStep.addItem("Lacqure");
		/*cmbProductionStep.addItem("Screen Printing");
		cmbProductionStep.addItem("Heat Trasfer Label");
		cmbProductionStep.addItem("Manual Printing");
		cmbProductionStep.addItem("Labeling");
		cmbProductionStep.addItem("Cap Folding");
		cmbProductionStep.addItem("Stretch Blow Molding");*/

		lblBatchQty = new Label("Batch Qty [FG] :");
		lblBatchQty.setImmediate(false);
		lblBatchQty.setWidth("-1px");
		lblBatchQty.setHeight("-1px");
		mainLayout.addComponent(lblBatchQty, "top:20.0px;left:350.0px;");

		txtBatchQty =new TextRead(1);
		txtBatchQty.setImmediate(true);
		txtBatchQty.setWidth("100px");
		txtBatchQty.setHeight("-1px");
		mainLayout.addComponent(txtBatchQty, "top:18.0px;left:510.0px;");

		lblTotalProduction = new Label("Total Production Of Batch:");
		lblTotalProduction.setImmediate(false);
		lblTotalProduction.setWidth("-1px");
		lblTotalProduction.setHeight("-1px");
		mainLayout.addComponent(lblTotalProduction, "top:45.0px;left:350.0px;");

		txtTotalProduction=new TextRead(1);
		txtTotalProduction.setImmediate(true);
		txtTotalProduction.setWidth("100px");
		txtTotalProduction.setHeight("-1px");
		mainLayout.addComponent(txtTotalProduction, "top:43.0px;left:510.0px;");

		lblTotalReject = new Label("Total Rejection Of Batch: ");
		lblTotalReject.setImmediate(false);
		lblTotalReject.setWidth("-1px");
		lblTotalReject.setHeight("-1px");
		mainLayout.addComponent(lblTotalReject, "top:70.0px;left:350.0px;");

		txtTotalReject=new TextRead(1);
		txtTotalReject.setImmediate(true);
		txtTotalReject.setWidth("100px");
		txtTotalReject.setHeight("-1px");
		mainLayout.addComponent(txtTotalReject, "top:68.0px;left:510.0px;");

		lblRemainQty = new Label("Remain Qty Of Batch:");
		lblRemainQty.setImmediate(false);
		lblRemainQty.setWidth("-1px");
		lblRemainQty.setHeight("-1px");
		mainLayout.addComponent(lblRemainQty, "top:20.0px;left:630.0px;");

		txtRemainQty =new TextRead(1);
		txtRemainQty.setImmediate(true);
		txtRemainQty.setWidth("100px");
		txtRemainQty.setHeight("-1px");
		mainLayout.addComponent(txtRemainQty, "top:18.0px;left:760.0px;");

		lblProductionQty = new Label("Production Qty [FG]:");
		lblProductionQty.setImmediate(false);
		lblProductionQty.setWidth("-1px");
		lblProductionQty.setHeight("-1px");
		mainLayout.addComponent(lblProductionQty, "top:45.0px;left:630.0px;");

		txtProductionQty =new TextRead(1);
		txtProductionQty.setImmediate(true);
		txtProductionQty.setWidth("100px");
		txtProductionQty.setHeight("-1px");
		mainLayout.addComponent(txtProductionQty, "top:43.0px;left:760.0px;");

		lblRejectQty = new Label("Reject Qty [FG]:");
		lblRejectQty.setImmediate(false);
		lblRejectQty.setWidth("-1px");
		lblRejectQty.setHeight("-1px");
		mainLayout.addComponent(lblRejectQty, "top:70.0px;left:630.0px;");

		txtRejectQty =new TextRead(1);
		txtRejectQty.setImmediate(true);
		txtRejectQty.setWidth("100px");
		txtRejectQty.setHeight("-1px");
		mainLayout.addComponent(txtRejectQty, "top:68.0px;left:760.0px;");


		lblPresentProductionFG = new Label("Present Production [FG]:");
		lblPresentProductionFG.setImmediate(false);
		lblPresentProductionFG.setWidth("-1px");
		lblPresentProductionFG.setHeight("-1px");
		mainLayout.addComponent(lblPresentProductionFG, "top:20.0px;left:900.0px;");

		txtPresentProductionFG =new TextRead(1);
		txtPresentProductionFG.setImmediate(true);
		txtPresentProductionFG.setWidth("100px");
		txtPresentProductionFG.setHeight("-1px");
		mainLayout.addComponent(txtPresentProductionFG, "top:18.0px;left:1050.0px;");


		lblPresentProductionFG = new Label("Present Rejection [FG]:");
		lblPresentProductionFG.setImmediate(false);
		lblPresentProductionFG.setWidth("-1px");
		lblPresentProductionFG.setHeight("-1px");
		mainLayout.addComponent(lblPresentProductionFG, "top:45.0px;left:900.0px;");

		txtpresentRejectionFG =new TextRead(1);
		txtpresentRejectionFG.setImmediate(true);
		txtpresentRejectionFG.setWidth("100px");
		txtpresentRejectionFG.setHeight("-1px");
		mainLayout.addComponent(txtpresentRejectionFG, "top:43.0px;left:1050.0px;");

		lblTotalConsumption = new Label("Total Consumption [FG]:");
		lblTotalConsumption.setImmediate(false);
		lblTotalConsumption.setWidth("-1px");
		lblTotalConsumption.setHeight("-1px");
		mainLayout.addComponent(lblTotalConsumption, "top:70.0px;left:900.0px;");

		txtTotalConsumption =new TextRead(1);
		txtTotalConsumption.setImmediate(true);
		txtTotalConsumption.setWidth("100px");
		txtTotalConsumption.setHeight("-1px");
		mainLayout.addComponent(txtTotalConsumption, "top:68.0px;left:1050.0px;");


		table = new Table();
		table.setWidth("100%");
		table.setHeight("350px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 15);
		table.setColumnAlignment("SL", table.ALIGN_CENTER);

		table.addContainerProperty("Show",  CheckBox.class , new  CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Show",30);

		table.addContainerProperty("Batch No",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Batch No",110);

		table.addContainerProperty("Machine Name",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Machine Name",150);

		table.addContainerProperty("SemiFg Name",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SemiFg Name",300);
		table.setColumnCollapsed("SemiFg Name", true);

		table.addContainerProperty("FG Name",  ComboBox.class , new  ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("FG Name",280);

		table.addContainerProperty("ShiftA",  AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ShiftA",70);

		table.addContainerProperty("RejectA", AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("RejectA",70);

		table.addContainerProperty("ShiftB",  AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ShiftB",70);

		table.addContainerProperty("RejectB",  AmountField.class , new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("RejectB",70);

		table.addContainerProperty("Remarks",  TextField.class , new TextField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Remarks",120);
		
		table.addContainerProperty("ISFG",  CheckBox.class , new  CheckBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("ISFG",30);

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
	private void tableClear(){
		for(int a=0;a<tbSl.size();a++){
			tbChkShow.get(a).setValue(false);
			tbChkisFG.get(a).setValue(false);
			tbCmbBatchNo.get(a).setValue(null);
			tbCmbMachineName.get(a).setValue(null);
			tbCmbSemiFgName.get(a).setValue(null);
			tbCmbFGName.get(a).setValue(null);
			tbTxtShiftA.get(a).setValue("");
			tbTxtRejectA.get(a).setValue("");
			tbTxtShiftB.get(a).setValue("");
			tbTxtRejectB.get(a).setValue("");
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
		Iterator iter=dbService("select 0,isnull(MAX(productionNo),0)+1 id from tbLacqureDailyProductionInfo");
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
