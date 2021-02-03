package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import acc.appform.FinishedGoodsModule.SemiFgSubFindWindow;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.productionReport.RptFloorStockAsOnDate;
import com.example.productionReport.RptRequisitionProduction0;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.Filter;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class ProductionRequistion extends Window 
{
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private Label lblPartyName,lblReqNo;
	
	private Label lblJobOrder;
	private ComboBox cmbJobOrder;
	private ComboBox cmbFindJobOrder;

	private Label lblProductName,lblMouldName,lblProductionType,lblMachineName;
	private ComboBox cmbProductName,cmbMouldName,cmbProductionType,cmbMachineName;
	private ComboBox cmbFindProductName;

	private Label lblOrderQty;
	private TextRead txtOrderQty;

	private Label lblRemainQty;
	private TextRead txtRemainQty;

	private NativeButton submitButton = new NativeButton("Submit");
	private NativeButton generateButton = new NativeButton("Generate Requisition");
	private NativeButton btnRequisitionReport = new NativeButton("Requisition Report");

	private Label lblReqQty,lblReqDate,lblDeliveryDate,lblReqNo1;
	private AmountField txtReqQty;
	private TextRead txtFindReqQty,txtReqNo;

	private Panel designPanel;
	PopupDateField dateReq,dateDelivery;

	private FormLayout frmLayout = new FormLayout();

	Table tableRequisition = new Table();
	ArrayList<Label>tbRqSl = new ArrayList<Label>();
	ArrayList<Label>tbRqItem = new ArrayList<Label>();
	ArrayList<TextRead>tbRqUnit =  new ArrayList<TextRead>();
	ArrayList<AmountField>tbRqStdPercent = new ArrayList<AmountField>();
	ArrayList<TextRead>tbRqStdQty=new ArrayList<TextRead>();
	ArrayList<TextRead>tbRqReqQty=new ArrayList<TextRead>();

	Table tableChkRequisition = new Table();
	ArrayList<Label>tbChkRqSl = new ArrayList<Label>();
	ArrayList<CheckBox>tbChkEdit=new ArrayList<CheckBox>();
	ArrayList<CheckBox>tbChkRqCheck=new ArrayList<CheckBox>();
	ArrayList<Label>tbChkRqJO=new ArrayList<Label>();
	ArrayList<Label>tbChkRqProductName=new ArrayList<Label>();
	ArrayList<TextRead>tbChkRqReqQty=new ArrayList<TextRead>();
	ArrayList<Label>tbFgId = new ArrayList<Label>();
	ArrayList<Label>tbFgName = new ArrayList<Label>();
	ArrayList<Label>tbSemiFgId = new ArrayList<Label>();
	ArrayList<Label>tbMouldId = new ArrayList<Label>();
	ArrayList<Label>tbMouldName = new ArrayList<Label>();
	ArrayList<Label>tbPartyId = new ArrayList<Label>();
	ArrayList<Label>tbPartyName = new ArrayList<Label>();
	ArrayList<Label>tbMachineId = new ArrayList<Label>();
	ArrayList<Label>tbMachineName = new ArrayList<Label>();

	Table tableReqItem = new Table();
	ArrayList<Label>tbRqISl = new ArrayList<Label>();
	//ArrayList<ComboBox>tbRqIJO=new ArrayList<ComboBox>();
	ArrayList<ComboBox>tbRqIProductName =new ArrayList<ComboBox>();
	ArrayList<TextRead>tbRqIUnit=new ArrayList<TextRead>();
	ArrayList<TextRead>tbRqIFlrRcvQty=new ArrayList<TextRead>();
	ArrayList<TextRead>tbRqIReqQty=new ArrayList<TextRead>();
	DecimalFormat df=new DecimalFormat("#0.00");
	DecimalFormat df1=new DecimalFormat("#0.00000");
	SimpleDateFormat dformat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private Label lblReqItem;

	boolean isUpdate=false;
	ArrayList<Component> allComp = new ArrayList<Component>();

	public ProductionRequistion(SessionBean sessionBean,String caption,int flag)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("MATERIAL REQUISITION ENTRY :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		componentIni(true);
		btnIni(true);
		focusEnter();
		//partyNameLoad();
		//mouldNameLoad();
		cmbProductionTypeDataLoad();
		cmbMachineLoad();
		cmbJobOrderDataLoad() ;
		tableInitializeRq();
		tableInitializeChkRq();
		tableInitializeRqItem();
		if(flag==1){
			txtItemID.setValue(caption);
			findInitialise(caption);
		}
	}
	private void mouldNameLoad(){
		cmbMouldName.removeAllItems();
		String sql="select distinct a.mouldName,(select mouldName from tbmouldInfo where mouldid=a.mouldName) " +
				"from tbFinishedGoodsStandardInfo a where fGCode like '"+cmbProductName.getValue()+"'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbMouldName.addItem(element[0]);
			cmbMouldName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void tableInitializeRq() 
	{
		for (int a = 0; a<9; a++)
		{
			tableRowAddRq(a);
		}

	}

	private void tableRowAddRq(int rq)
	{
		tbRqSl.add(rq, new Label());
		tbRqSl.get(rq).setValue(rq+1);
		tbRqSl.get(rq).setWidth("100%");
		tbRqSl.get(rq).setHeight("-1px");

		tbRqItem.add(rq, new Label());
		tbRqItem.get(rq).setImmediate(true);
		tbRqItem.get(rq).setWidth("-1px");
		tbRqItem.get(rq).setHeight("-1px");

		tbRqUnit.add(rq, new TextRead(1));
		tbRqUnit.get(rq).setImmediate(true);
		tbRqUnit.get(rq).setWidth("100%");
		tbRqUnit.get(rq).setHeight("-1px");

		tbRqStdPercent.add(rq, new AmountField());
		tbRqStdPercent.get(rq).setImmediate(true);
		tbRqStdPercent.get(rq).setWidth("100%");
		tbRqStdPercent.get(rq).setHeight("-1px");

		tbRqStdQty.add(rq, new TextRead(1));
		tbRqStdQty.get(rq).setImmediate(true);
		tbRqStdQty.get(rq).setWidth("100%");
		tbRqStdQty.get(rq).setHeight("-1px");

		tbRqReqQty.add(rq, new TextRead(1));
		tbRqReqQty.get(rq).setImmediate(true);
		tbRqReqQty.get(rq).setWidth("100%");
		tbRqReqQty.get(rq).setHeight("-1px");


		tableRequisition.addItem(new Object[]{tbRqSl.get(rq),tbRqItem.get(rq),tbRqUnit.get(rq),tbRqStdPercent.get(rq),tbRqStdQty.get(rq), tbRqReqQty.get(rq)},rq);
	}


	private void tableInitializeChkRq()
	{
		for (int a = 0; a<10; a++)
		{
			tableRowAddChkRq(a);
		}	
	}
	private void showChkData(String semiFgId,String mouldId,String reqQty){
		double Qty=Double.parseDouble(reqQty);
		
		String sql="select b.RawItemCode, case when b.RawItemCode like '%RI%' then (select vRawItemName from tbRawItemInfo where vRawItemCode=b.RawItemCode) " 
				   +"when b.RawItemCode like '%TH%' then (select vItemName from tbThirdPartyItemInfo where vCode=b.RawItemCode) else (select vItemName from tbThirdPartyItemInfo where vCode=b.RawItemCode ) end rawItemName,b.unit,b.percentage,b.Qty " 
				   +"from tbFinishedGoodsStandardInfo a inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo where a.fGCode like '"+semiFgId+"' and "  
				   +"a.mouldName like '"+mouldId+"' and a.status='Active'  and a.declarationDate=(select MAX(declarationDate)from tbFinishedGoodsStandardInfo where fGCode=a.fGCode and mouldName like '"+mouldId+"' and status='Active') ";
					
		
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbRqItem.get(a).setValue(element[1]);
			tbRqUnit.get(a).setValue(element[2]);
			tbRqStdPercent.get(a).setValue(df.format(element[3]));
			tbRqStdQty.get(a).setValue(df1.format(element[4]));
			tbRqReqQty.get(a).setValue(df.format(Qty*Double.parseDouble(element[4].toString())));
			a++;
		}
	}
	private void tableClearShow(){
		for(int a=0;a<tbRqSl.size();a++){
			tbRqItem.get(a).setValue("");
			tbRqUnit.get(a).setValue("");
			tbRqStdPercent.get(a).setValue("");
			tbRqStdQty.get(a).setValue("");
			tbRqReqQty.get(a).setValue("");
		}
	}
	private void tableRowAddChkRq(final int rq)
	{
		tbChkRqSl.add(rq, new Label());
		tbChkRqSl.get(rq).setValue(rq+1);
		tbChkRqSl.get(rq).setWidth("100%");
		tbChkRqSl.get(rq).setHeight("-1px");

		tbChkEdit.add(rq, new CheckBox());
		tbChkEdit.get(rq).setImmediate(true);

		tbChkEdit.get(rq).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tbChkEdit.get(rq).booleanValue()){
					cmbJobOrder.setValue(tbChkRqJO.get(rq).getValue());
					cmbProductName.setValue(tbSemiFgId.get(rq).getValue().toString());
					cmbMouldName.setValue(tbMouldId.get(rq).getValue().toString());
					cmbMachineName.setValue(tbMachineId.get(rq).getValue().toString());
					txtReqQty.setValue(tbChkRqReqQty.get(rq).getValue());
					tbSemiFgClear(rq);
				}
			}
		});

		tbChkRqCheck.add(rq, new CheckBox());
		tbChkRqCheck.get(rq).setImmediate(true);

		tbChkRqCheck.get(rq).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				/*String id=getSemiFgIdRef();
				if(!id.isEmpty()){
					System.out.println("ID is: "+id);
				}*/
				tableClearShow();
				if(!tbChkRqJO.get(rq).getValue().toString().isEmpty()){
					if(tbChkRqCheck.get(rq).booleanValue()){
						for(int a=0;a<tbChkEdit.size();a++){
							if(a!=rq){
								tbChkRqCheck.get(a).setValue(false);
							}
						}
						showChkData(tbSemiFgId.get(rq).getValue().toString(),tbMouldId.get(rq).getValue().toString(),tbChkRqReqQty.get(rq).getValue().toString());
					}
				}
				else{
					tbChkRqCheck.get(rq).setValue(false);
					showNotification("Empty Field",Notification.TYPE_WARNING_MESSAGE);
				}

			}
		});

		tbChkRqJO.add(rq, new Label());
		tbChkRqJO.get(rq).setImmediate(true);
		tbChkRqJO.get(rq).setWidth("-1px");
		tbChkRqJO.get(rq).setHeight("-1px");

		tbChkRqProductName.add(rq, new Label());
		tbChkRqProductName.get(rq).setImmediate(true);
		tbChkRqProductName.get(rq).setWidth("100%");
		tbChkRqProductName.get(rq).setHeight("-1px");


		tbChkRqReqQty.add(rq, new TextRead(1));
		tbChkRqReqQty.get(rq).setImmediate(true);
		tbChkRqReqQty.get(rq).setWidth("90%");
		tbChkRqReqQty.get(rq).setHeight("-1px");

		tbFgId.add(rq, new Label());
		tbFgId.get(rq).setImmediate(true);
		tbFgId.get(rq).setWidth("-1px");
		tbFgId.get(rq).setHeight("-1px");

		tbFgName.add(rq, new Label());
		tbFgName.get(rq).setImmediate(true);
		tbFgName.get(rq).setWidth("-1px");
		tbFgName.get(rq).setHeight("-1px");

		tbSemiFgId.add(rq, new Label());
		tbSemiFgId.get(rq).setImmediate(true);
		tbSemiFgId.get(rq).setWidth("-1px");
		tbSemiFgId.get(rq).setHeight("-1px");

		tbMouldId.add(rq, new Label());
		tbMouldId.get(rq).setImmediate(true);
		tbMouldId.get(rq).setWidth("-1px");
		tbMouldId.get(rq).setHeight("-1px");

		tbMouldName.add(rq, new Label());
		tbMouldName.get(rq).setImmediate(true);
		tbMouldName.get(rq).setWidth("-1px");
		tbMouldName.get(rq).setHeight("-1px");

		tbPartyId.add(rq, new Label());
		tbPartyId.get(rq).setImmediate(true);
		tbPartyId.get(rq).setWidth("-1px");
		tbPartyId.get(rq).setHeight("-1px");

		tbPartyName.add(rq, new Label());
		tbPartyName.get(rq).setImmediate(true);
		tbPartyName.get(rq).setWidth("-1px");
		tbPartyName.get(rq).setHeight("-1px");
		
		tbMachineId.add(rq, new Label());
		tbMachineId.get(rq).setImmediate(true);
		tbMachineId.get(rq).setWidth("-1px");
		tbMachineId.get(rq).setHeight("-1px");

		tbMachineName.add(rq, new Label());
		tbMachineName.get(rq).setImmediate(true);
		tbMachineName.get(rq).setWidth("-1px");
		tbMachineName.get(rq).setHeight("-1px");

		tableChkRequisition.addItem(new Object[]{tbChkRqSl.get(rq),tbChkEdit.get(rq),tbChkRqCheck.get(rq),tbChkRqJO.get(rq),tbChkRqProductName.get(rq),tbChkRqReqQty.get(rq),
				tbFgId.get(rq),tbFgName.get(rq),tbSemiFgId.get(rq),tbMouldId.get(rq),tbMouldName.get(rq),tbMachineId.get(rq),tbMachineName.get(rq),tbPartyId.get(rq),tbPartyName.get(rq)},rq);

	}
	private void tableInitializeRqItem()
	{
		for (int a = 0; a<10; a++)
		{
			tableRowAddRqItem(a);
		}
	}

	private void tableRowAddRqItem(int rq)
	{
		tbRqISl.add(rq, new Label());
		tbRqISl.get(rq).setValue(rq+1);
		tbRqISl.get(rq).setWidth("100%");
		tbRqISl.get(rq).setHeight("-1px");

		tbRqIProductName.add(rq, new ComboBox() );
		tbRqIProductName.get(rq).setImmediate(true);
		tbRqIProductName.get(rq).setWidth("100%");
		tbRqIProductName.get(rq).setHeight("-1px");
		tbRqIProductName.get(rq).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		tbRqIUnit.add(rq, new TextRead(1));
		tbRqIUnit.get(rq).setImmediate(true);
		tbRqIUnit.get(rq).setWidth("100%");
		tbRqIUnit.get(rq).setHeight("-1px");

		tbRqIFlrRcvQty.add(rq, new TextRead(1));
		tbRqIFlrRcvQty.get(rq).setImmediate(true);
		tbRqIFlrRcvQty.get(rq).setWidth("100%");
		tbRqIFlrRcvQty.get(rq).setHeight("-1px");

		tbRqIReqQty.add(rq, new TextRead(1));
		tbRqIReqQty.get(rq).setImmediate(true);
		tbRqIReqQty.get(rq).setWidth("100%");
		tbRqIReqQty.get(rq).setHeight("-1px");

		tableReqItem.addItem(new Object[]{tbRqISl.get(rq),tbRqIProductName.get(rq),tbRqIUnit.get(rq),tbRqIFlrRcvQty.get(rq),tbRqIReqQty.get(rq)},rq); 
	}	

	private void saveButtonEvent(){
		if(isUpdate)
		{
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						if(deleteData(session,tx)){
					
							insertData(session,tx);
							isUpdate = false;
							btnIni(true);
							componentIni(true);
							//txtClear();
							button.btnNew.focus();
							
						}
					}
				}
			});																	
		}
		else
		{									
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						insertData(session,tx);
						isUpdate = false;
						btnIni(true);
						componentIni(true);
						
						//txtClear();
					}
				}
			});
		}
	}
	private String getReqNo(){
		String query="select isnull(max(cast(SUBSTRING(ReqNo,CHARINDEX('-',ReqNo)+1, "+
				"len(ReqNo)-CHARINDEX('-',ReqNo))as int)),0)+1 from tbProductionReqInfo";
		Iterator iter=dbService(query);
		if(iter.hasNext()){
			Object element=iter.next();
			return "Req-"+element.toString();
		}
		return null;
	}
	private void insertData(Session session,Transaction tx){
		String reqNo="";
		if(isUpdate){
			reqNo=txtItemID.getValue().toString();
		}
		else{
			String query="select isnull(max(cast(SUBSTRING(ReqNo,CHARINDEX('-',ReqNo)+1, "+
					"len(ReqNo)-CHARINDEX('-',ReqNo))as int)),0)+1 from tbProductionReqInfo";
			Iterator<?> iter= session.createSQLQuery(query).list().iterator();
			if(iter.hasNext()){
				reqNo=iter.next().toString();
			}
			txtReqNo.setValue(reqNo);
		}
		try{
			
			String sql="insert into tbProductionReqInfo (ReqNo,ReqDate,DeliveryDate,userName,userIp,entryTime,productionTypeId,productionTypeName)values "+
					" ('"+reqNo+"','"+dformat.format(dateReq.getValue())+"','"+dformat.format(dateDelivery.getValue())+"','"+sessionBean.getUserName()+"'," +
					"'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+cmbProductionType.getValue()+"','"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"')";
			System.out.println(sql);
			session.createSQLQuery(sql).executeUpdate();
			if(isUpdate){
				String UdProductReq="insert into tbUdProductionReqInfo (ReqNo,ReqDate,DeliveryDate,userName,userIp,entryTime,productionTypeId,productionTypeName,vUdFlag)values "+
						" ('"+reqNo+"','"+dformat.format(dateReq.getValue())+"','"+dformat.format(dateDelivery.getValue())+"','"+sessionBean.getUserName()+"'," +
						"'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+cmbProductionType.getValue()+"','"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"','New')";
				System.out.println(UdProductReq);
				session.createSQLQuery(UdProductReq).executeUpdate();
			}
			for(int a=0;a<tbChkEdit.size();a++){
				if(!tbChkRqJO.get(a).getValue().toString().isEmpty()){
					
					String sql1="insert into tbProductionSemiFgDetails (ReqNo,JobOrder,semiFgId,semiFgName,mouldId,mouldName," +
							"reqQty,machineId,machineName)values "+
							" ('"+reqNo+"','"+tbChkRqJO.get(a).getValue()+"'," +
							"'"+tbSemiFgId.get(a).getValue()+"','"+tbChkRqProductName.get(a).getValue()+"','"+tbMouldId.get(a).getValue()+"'," +
							"'"+tbMouldName.get(a).getValue()+"','"+tbChkRqReqQty.get(a).getValue()+"'," +
							"'"+tbMachineId.get(a).getValue()+"','"+tbMachineName.get(a).getValue()+"')";
					session.createSQLQuery(sql1).executeUpdate();
					
					if(isUpdate){
						String UdProductionSemiFgDetails="insert into tbUdProductionSemiFgDetails (ReqNo,JobOrder,semiFgId,semiFgName,mouldId,mouldName," +
								"reqQty,machineId,machineName,vUdFlag)values "+
								" ('"+reqNo+"','"+tbChkRqJO.get(a).getValue()+"'," +
								"'"+tbSemiFgId.get(a).getValue()+"','"+tbChkRqProductName.get(a).getValue()+"',"
								+ "'"+tbMouldId.get(a).getValue()+"'," +
								"'"+tbMouldName.get(a).getValue()+"','"+tbChkRqReqQty.get(a).getValue()+"'," +
								"'"+tbMachineId.get(a).getValue()+"','"+tbMachineName.get(a).getValue()+"','New')";
						session.createSQLQuery(UdProductionSemiFgDetails).executeUpdate();
					}
	
				}
			}
			for(int a=0;a<tbRqIProductName.size();a++){
				if(tbRqIProductName.get(a).getValue()!=null){
					
					String sql2="insert into tbProductionRmDetails (ReqNo,rawItemCode,rawItemName,unit,ReqQty) values" +
							"('"+reqNo+"','"+tbRqIProductName.get(a).getValue()+"','"+tbRqIProductName.get(a).getItemCaption(tbRqIProductName.get(a).getValue())+"'," +
							"'"+tbRqIUnit.get(a).getValue()+"','"+tbRqIReqQty.get(a).getValue()+"')";
					session.createSQLQuery(sql2).executeUpdate();
					
					if(isUpdate){
						String tbProductionRmDetails="insert into tbUdProductionRmDetails (ReqNo,rawItemCode,rawItemName,unit,ReqQty,vUdFlag) values" +
								"('"+reqNo+"','"+tbRqIProductName.get(a).getValue()+"','"+tbRqIProductName.get(a).getItemCaption(tbRqIProductName.get(a).getValue())+"'," +
								"'"+tbRqIUnit.get(a).getValue()+"','"+tbRqIReqQty.get(a).getValue()+"','New')";
						session.createSQLQuery(tbProductionRmDetails).executeUpdate();
					}
				}
			}
			if(isUpdate){
				showNotification("All Information update Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			}
			else{
				showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			}
			tx.commit();
		}
		catch(Exception exp){
			if(tx!=null){
				tx.rollback();
			}
			showNotification("Insert Data: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}
	private boolean deleteData(Session session,Transaction tx){
		try{
			
			String tbProductionReqInfo ="insert into tbUdProductionReqInfo select  ReqNo, ReqDate, DeliveryDate, "
			+ "userName, userIp, entryTime, productionTypeId, productionTypeName, 'Update' vUdFlag  "
			+ "from tbProductionReqInfo  where ReqNo='"+txtItemID.getValue()+"' ";
			
			String  tbProductionRmDetails="insert into tbUdProductionRmDetails select ReqNo, rawItemCode, "
			+ "rawItemName, unit, ReqQty,'Update' vUdFlag from tbProductionRmDetails "
			+ "where ReqNo='"+txtItemID.getValue()+"'";
			
			String tbProductionSemiFgDetails ="insert into tbUdProductionSemiFgDetails select  ReqNo, JobOrder, "
			+ "FgId, FgName, semiFgId, semiFgName, mouldId, mouldName, partyId, partyName, reqQty, machineId,"
			+ " machineName ,'Update' vUdFlag from tbProductionSemiFgDetails where ReqNo='"+txtItemID.getValue()+"'";
			
			session.createSQLQuery(tbProductionReqInfo).executeUpdate();
			session.createSQLQuery(tbProductionRmDetails).executeUpdate();
			session.createSQLQuery(tbProductionSemiFgDetails).executeUpdate();
			
			session.createSQLQuery("delete from tbProductionReqInfo where ReqNo='"+txtItemID.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete from tbProductionSemiFgDetails where ReqNo='"+txtItemID.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete from tbProductionRmDetails where ReqNo='"+txtItemID.getValue()+"'").executeUpdate();
			return true;
		}
		catch(Exception exp){
			showNotification("Delete Data: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}
	public void findInitialise(String id){
		System.out.println("Before");
		String sql="select ReqNo,ReqDate,DeliveryDate,productionTypeId from tbProductionReqInfo where ReqNo='"+id+"'";
		Iterator iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtReqNo.setValue(element[0]);
			dateReq.setValue(element[1]);
			dateDelivery.setValue(element[2]);
			cmbProductionType.setValue(element[3]);
		}
		String sql1="select JobOrder,FgId,FgName,semiFgId,semiFgName,mouldId,mouldName," +
				"partyId,partyName,reqQty,machineId,machineName from tbProductionSemiFgDetails where ReqNo='"+id+"'";
		Iterator iter1=dbService(sql1);
		int a=0;
		while(iter1.hasNext()){
			Object element[]=(Object[])iter1.next();
			tbChkRqJO.get(a).setValue(element[0]);
			tbFgId.get(a).setValue(element[1]);
			tbFgName.get(a).setValue(element[2]);
			tbSemiFgId.get(a).setValue(element[3]);
			tbChkRqProductName.get(a).setValue(element[4]);
			tbMouldId.get(a).setValue(element[5]);
			tbMouldName.get(a).setValue(element[6]);
			tbPartyId.get(a).setValue(element[7]);
			tbPartyName.get(a).setValue(element[8]);
			tbChkRqReqQty.get(a).setValue(df.format(element[9]));
			tbMachineId.get(a).setValue(element[10]);
			tbMachineName.get(a).setValue(element[11]);
			
			if(a==tbChkRqCheck.size()-1){
				tableRowAddChkRq(a+1);
			}
			
			a++;
		}
		String sql2="select rawItemCode,rawItemName,unit,ReqQty  from tbProductionRmDetails where ReqNo='"+id+"'";
		Iterator iter2=dbService(sql2);
		int a1=0;
		while(iter2.hasNext()){
			Object element[]=(Object[])iter2.next();
			tbRqIProductName.get(a1).addItem(element[0]);
			tbRqIProductName.get(a1).setItemCaption(element[0], element[1].toString());
			tbRqIProductName.get(a1).setValue(element[0]);
			tbRqIUnit.get(a1).setValue(element[2]);
			tbRqIReqQty.get(a1).setValue(df.format(element[3]));
			
			if(a1==tbRqIProductName.size()-1){
				tableRowAddRqItem(a1+1);
			}
			
			a1++;
		}
		System.out.println("After");
	}
	private void findButtonEvent(){
		Window win = new ProductionRequistioFind(sessionBean, txtItemID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtItemID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtItemID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}
	private void updateButtonEvent(){
		if(!txtItemID.getValue().toString().isEmpty())
		{
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void showReportWindow(){
		Window win = new RptRequisitionProduction0(sessionBean,"");

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				System.out.println("As On Date");
			}
		});

		this.getParent().addWindow(win);
	}
	private boolean MixtureCheck()
	{


		Iterator<?>iter=dbService("select * from tbMixtureIssueEntryInfo where reqNo='"+txtReqNo.getValue()+"'");

		if (iter.hasNext()) 
		{
			return true;
		}


		return false;
	}
	private void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				newButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				/*if(sessionBean.isUpdateable()){
					
					if(MixtureCheck())
					{
						showNotification("Warning!","Mixture Issue Entry already given against this Req No", Notification.TYPE_WARNING_MESSAGE);
					}
					else
					{
						isUpdate = true;
						updateButtonEvent();
					}
				}
				else{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}*/
				
				isUpdate = true;
				updateButtonEvent();
			}
		});

		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(formValidation()){
					saveButtonEvent();
				}
			}
		});

		button.btnExit.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}
		});

		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});
		btnRequisitionReport.addListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				showReportWindow();
			}
		});


	/*	cmbPartyName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbPartyName.getValue()!=null){
					cmbJobOrderDataLoad() ;
				}
			}
		});*/
		cmbJobOrder.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbJobOrder.getValue()!=null)
				{
					cmbSemiFgNameLoad();
				}
			}
		});
		cmbProductionType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(cmbProductionType.getValue()!=null){
					cmbMachineLoad();
				}
				else{
					cmbMachineName.removeAllItems();
				}
			}
		});
		cmbMouldName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				txtOrderQty.setValue("");
				txtRemainQty.setValue("");
				txtReqQty.setValue("");
				if(cmbMouldName.getValue()!=null){
					if(checkValidation()){
						findOrderRemainQty();
					}
				}
			}
		});
		cmbProductName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbProductName.getValue()!=null){
					mouldNameLoad();
				}
			}
		});
		txtReqQty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!txtReqQty.getValue().toString().isEmpty()){
					Double ReqQty=Double.parseDouble(txtReqQty.getValue().toString().isEmpty()?"0.0":txtReqQty.getValue().toString());
					Double remainQty=Double.parseDouble(txtRemainQty.getValue().toString().isEmpty()?"0.0":txtRemainQty.getValue().toString());
					if(ReqQty>remainQty){
						txtReqQty.setValue("");
						showNotification("Req Qty can't exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});
		submitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(checkValidation()){
					if(!txtReqQty.getValue().toString().isEmpty()){
						if(formulationCheck()){
							submitRowAdd();
						}
						else{
							showNotification("Please Check Formulation",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else{
						showNotification("Please Provid Req Qty",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});
		generateButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(!tbChkRqJO.get(0).getValue().toString().isEmpty()){
					generateButtonEventInsert();
					generateButtonEventSelect();
				}
				else{
					showNotification("Empty Table",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
	}
	private boolean formulationCheck(){
		String sql="select * from tbFinishedGoodsStandardInfo where fGCode ='"+cmbProductName.getValue()+"' and mouldName = '"+cmbMouldName.getValue()+"' "+
				" and status='Active' and(isFg in('YES','NO'))";
		Iterator iter=dbService(sql);
		if(iter.hasNext()){
			return true;
		}
		return false;
	}
	private boolean formValidation(){

		if(!tbChkRqJO.get(0).getValue().toString().isEmpty()&&tbRqIProductName.get(0).getValue()!=null){
			return true;
		}
		else{
			showNotification("Please Provid All Data",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}
	private void generateButtonEventSelect(){
		tableRqClear();
		Iterator iter=dbService("select rawItemId,rawItemName,unit,SUM(requiredQty)as requiredQty from funcRequisitionLoadData () group by rawItemId,rawItemName,unit order by rawItemId");
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbRqIProductName.get(a).addItem(element[0]);
			tbRqIProductName.get(a).setItemCaption(element[0], element[1].toString());
			tbRqIProductName.get(a).setValue(element[0]);
			tbRqIUnit.get(a).setValue(element[2]);
			tbRqIReqQty.get(a).setValue(df.format(element[3]));
			
			if(a==tbRqIProductName.size()-1){
				tableRowAddRqItem(a+1);
			}
			a++;
		}
	}
	private void generateButtonEventInsert(){
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			for(int a=0;a<tbChkRqCheck.size();a++){
				if(!tbChkRqJO.get(a).getValue().toString().isEmpty()){
					if(a==0){
						session.createSQLQuery("truncate table tbReqHelp").executeUpdate();
					}
					String sql="insert into tbReqHelp values('"+tbSemiFgId.get(a).getValue()+"'," +
							"'"+tbChkRqProductName.get(a).getValue()+"','"+tbChkRqReqQty.get(a).getValue()+"','"+tbMouldId.get(a).getValue()+"','"+tbMouldName.get(a).getValue()+"')";
					session.createSQLQuery(sql).executeUpdate();
				}
			}
			//showNotification("Click is Ok",Notification.TYPE_WARNING_MESSAGE);
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
	}
	private boolean doubleEntryCheck(String jobOrder,String SemiFg,String mouldName,String machineName){
		for(int a=0;a<tbChkEdit.size();a++){
			if(!tbChkRqJO.get(a).getValue().toString().isEmpty()){
				if(jobOrder.equalsIgnoreCase(tbChkRqJO.get(a).getValue().toString())&&
						SemiFg.equalsIgnoreCase(tbChkRqProductName.get(a).getValue().toString())&&
						mouldName.equalsIgnoreCase(tbMouldId.get(a).getValue().toString())&&
						machineName.equalsIgnoreCase(tbMachineId.get(a).getValue().toString())){
					return false;
				}
			}
		}
		return true;
	}
	private void tbSemiFgClear(int rq){
		tbChkRqCheck.get(rq).setValue(false);
		tbChkRqJO.get(rq).setValue("");
		tbFgId.get(rq).setValue("");
		tbFgName.get(rq).setValue("");
		tbSemiFgId.get(rq).setValue("");
		tbChkRqProductName.get(rq).setValue("");
		tbMouldId.get(rq).setValue("");
		tbMouldName.get(rq).setValue("");
		tbPartyId.get(rq).setValue("");
		tbPartyName.get(rq).setValue("");
		tbChkRqReqQty.get(rq).setValue("");
	}
	private boolean hasStandard(){
		String sql="select * from tbFinishedGoodsStandardInfo where fGCode like '"+cmbProductName.getValue()+"' and " +
				"mouldName like '"+cmbMouldName.getValue()+"'";
		Iterator iter=dbService(sql);
		if(iter.hasNext()){
			return true;
		}
		return false;
	}
	private void submitRowAdd(){
		int rq=findBlank();
		if(hasStandard()){
			if(doubleEntryCheck(cmbJobOrder.getValue().toString(),
					cmbProductName.getItemCaption(cmbProductName.getValue()),
					cmbMouldName.getValue().toString(),cmbMachineName.getValue().toString())){
				
				
				
				tbChkRqJO.get(rq).setValue(cmbJobOrder.getValue());
				tbSemiFgId.get(rq).setValue(cmbProductName.getValue());
				tbChkRqProductName.get(rq).setValue(cmbProductName.getItemCaption(cmbProductName.getValue()));
				tbMouldId.get(rq).setValue(cmbMouldName.getValue());
				tbMouldName.get(rq).setValue(cmbMouldName.getItemCaption(cmbMouldName.getValue()));
				tbMachineId.get(rq).setValue(cmbMachineName.getValue());
				tbMachineName.get(rq).setValue(cmbMachineName.getItemCaption(cmbMachineName.getValue()));
				tbChkRqReqQty.get(rq).setValue(txtReqQty.getValue());
				
				System.out.println(cmbMachineName.getValue());
				System.out.println(tbMachineId.get(rq).getValue());
				
				if(rq==tbChkEdit.size()-1){
					tableRowAddChkRq(rq+1);
				}
				
			}
			else{
				showNotification("Double Entry",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Standard is not Given",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private int findBlank(){
		for(int a=0;a<tbChkEdit.size();a++){
			if(tbChkRqJO.get(a).getValue().toString().isEmpty()){
				return a;
			}
		}
		return 0;
	}
	private void findOrderRemainQty(){
		Iterator iter=dbService("select * from funcJobOrder_ReqRemain('"+cmbJobOrder.getValue()+"'," +
				"'"+cmbProductName.getValue()+"')");
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtOrderQty.setValue(df.format(element[0]));
			txtRemainQty.setValue(df.format(element[1]));
		}
	}
	private boolean checkValidation()
	{
			if(cmbProductionType.getValue()!=null){
				if(cmbJobOrder.getValue()!=null)
				{
						if(cmbProductName.getValue()!=null)
						{
							return true;
						}
						else{
							showNotification("Please Provide Semi Fg",Notification.TYPE_WARNING_MESSAGE);
						}
					
				}
				else{
					showNotification("Please Provide Job Order",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide Production Type",Notification.TYPE_WARNING_MESSAGE);
			}
		
		return false;
	}
	private void cmbSemiFgNameLoad(){
		cmbProductName.removeAllItems();
		String sql="select distinct  fgId,(select semiFgName from tbSemiFgInfo where semiFgCode like fgId ) semifgName" +
				",(select color from tbSemiFgInfo where semiFgCode like fgId ) color from tbJobOrderDetails where " 
				   +"orderNo like '"+cmbJobOrder.getValue().toString()+"' ";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductName.addItem(element[0]);
			cmbProductName.setItemCaption(element[0], element[1].toString()+" # "+element[2].toString());
		}
	}
/*	private void cmbProductDataLoad() {
		cmbProductName.removeAllItems();
		String sql="select semiFgId,semiFgName from tbFinishedProductDetailsNew where fgId='"+cmbMasterProduct.getValue()+"'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductName.addItem(element[0]);
			cmbProductName.setItemCaption(element[0], element[1].toString());
		}
	}*/
	private void cmbJobOrderDataLoad() {
		cmbJobOrder.removeAllItems();
		String sql="select distinct orderNo from tbJobOrderInfo where vStatus like 'Active'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element=iter.next();
			cmbJobOrder.addItem(element);
		}
	}

	private void focusEnter() 
	{
		allComp.add(cmbProductionType);
		allComp.add(cmbJobOrder);
		allComp.add(cmbProductName);
		allComp.add(cmbMouldName);
		allComp.add(txtOrderQty);
		allComp.add(txtRemainQty);
		allComp.add(txtReqQty);
		allComp.add(submitButton);
		allComp.add(cmbFindProductName);
		allComp.add(cmbFindJobOrder);
		allComp.add(txtFindReqQty);
		allComp.add(button.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	protected void refreshButtonEvent()
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	protected void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		//txtFinishItemName.setValue("FI-"+selectFinishItemName());
		cmbProductionType.focus();
	}

	private void componentIni(boolean t)
	{
		cmbProductionType.setEnabled(!t);
		cmbJobOrder.setEnabled(!t);
		cmbProductName.setEnabled(!t);
		cmbMouldName.setEnabled(!t);
		txtOrderQty.setEnabled(!t);
		txtRemainQty.setEnabled(!t);
		txtReqQty.setEnabled(!t);
		tableRequisition.setEnabled(!t);
		cmbMachineName.setEnabled(!t);
		txtReqNo.setEnabled(!t);
		//tableChkRequisition.setEnabled(!t);
		for(int a=0;a<tbChkEdit.size();a++){
			tbChkRqSl.get(a).setEnabled(!t);
			tbChkEdit.get(a).setEnabled(!t);
			tbChkRqCheck.get(a).setEnabled(!t);
			tbChkRqJO.get(a).setEnabled(!t);
			tbChkRqProductName.get(a).setEnabled(!t);
			tbChkRqReqQty.get(a).setEnabled(!t);
			tbFgId.get(a).setEnabled(!t);
			tbFgName.get(a).setEnabled(!t);
			tbSemiFgId.get(a).setEnabled(!t);
			tbMouldId.get(a).setEnabled(!t);
			tbMouldName.get(a).setEnabled(!t);
			tbPartyId.get(a).setEnabled(!t);
			tbPartyName.get(a).setEnabled(!t);
		}
		for(int a=0;a<tbRqSl.size();a++){
			tbRqISl.get(a).setEnabled(!t);
			tbRqIProductName.get(a).setEnabled(!t);
			tbRqIUnit.get(a).setEnabled(!t);
			tbRqIFlrRcvQty.get(a).setEnabled(!t);
			tbRqIReqQty.get(a).setEnabled(!t);
		}
		//tableReqItem.setEnabled(!t);
		submitButton.setEnabled(!t);
		generateButton.setEnabled(!t);
		dateDelivery.setEnabled(!t);
		dateReq.setEnabled(!t);
	}

	private void btnIni(boolean t) 
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);

	}
	private Iterator dbService(String sql){
		Session session=null;
		Iterator iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
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
	private void cmbProductionTypeDataLoad() 
	{
		cmbProductionType.removeAllItems();
		String sql="select productTypeId,productTypeName from tbProductionType";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbMachineLoad() 
	{
		cmbMachineName.removeAllItems();
		String sql="select vMachineCode,vMachineName from tbMachineInfo where productionTypeId='"+cmbProductionType.getValue()+"' order by vMachineName";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbMachineName.addItem(element[0]);
			cmbMachineName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void txtClear() 
	{
		cmbProductionType.setValue(null);
		cmbJobOrder.setValue(null);
		cmbProductName.setValue(null);
		cmbMachineName.setValue(null);
		cmbMouldName.setValue(null);
		txtReqNo.setValue("");
		txtOrderQty.setValue("");
		txtRemainQty.setValue("");
		txtReqQty.setValue("");
		tableChkRequisition.setValue("");
		tableReqItem.setValue("");
		tableChkClear();
		tableRqClear();
	}
	private void tableChkClear(){
		for(int rq=0;rq<tbChkEdit.size();rq++){
			tbChkRqCheck.get(rq).setValue(false);
			tbChkRqJO.get(rq).setValue("");
			tbFgId.get(rq).setValue("");
			tbFgName.get(rq).setValue("");
			tbSemiFgId.get(rq).setValue("");
			tbChkRqProductName.get(rq).setValue("");
			tbMouldId.get(rq).setValue("");
			tbMouldName.get(rq).setValue("");
			tbPartyId.get(rq).setValue("");
			tbPartyName.get(rq).setValue("");
			tbChkRqReqQty.get(rq).setValue("");
			tbMachineId.get(rq).setValue("");
			tbMachineName.get(rq).setValue("");
		}
	}
	private void tableRqClear(){
		for(int a=0;a<tbRqISl.size();a++){
			tbRqIProductName.get(a).setValue(null);
			tbRqIUnit.get(a).setValue("");
			tbRqIFlrRcvQty.get(a).setValue("");
			tbRqIReqQty.get(a).setValue("");
		}
	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("1080px");
		setHeight("640px");

		lblProductionType = new Label("Production Type: ");
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("100.0%");
		lblProductionType.setHeight("18px");

		cmbProductionType=new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		
		lblJobOrder = new Label("Job Order: ");
		lblJobOrder.setImmediate(false);
		lblJobOrder.setWidth("100.0%");
		lblJobOrder.setHeight("18px");

		cmbJobOrder=new ComboBox();
		cmbJobOrder.setImmediate(true);
		cmbJobOrder.setWidth("260px");
		cmbJobOrder.setHeight("24px");
		cmbJobOrder.setNullSelectionAllowed(true);
		cmbJobOrder.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		cmbFindJobOrder =new ComboBox("Job order:");
		cmbFindJobOrder.setImmediate(true);
		cmbFindJobOrder.setWidth("260px");
		cmbFindJobOrder.setHeight("24px");
		cmbFindJobOrder.setNullSelectionAllowed(true);
		cmbFindJobOrder.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


	/*	cmbMasterProduct =new ComboBox();
		cmbMasterProduct.setImmediate(true);
		cmbMasterProduct.setWidth("260px");
		cmbMasterProduct.setHeight("24px");
		cmbMasterProduct.setNullSelectionAllowed(false);
		cmbMasterProduct.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);*/

		lblProductName = new Label("Product Name: ");
		lblProductName.setImmediate(false);
		lblProductName.setWidth("100.0%");
		lblProductName.setHeight("18px");

		cmbProductName =new ComboBox();
		cmbProductName.setImmediate(true);
		cmbProductName.setWidth("260px");
		cmbProductName.setHeight("24px");
		cmbProductName.setNullSelectionAllowed(true);
		cmbProductName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblMouldName=new Label();
		lblMouldName.setValue("Mould Name: ");
		//mainLayout.addComponent(lblMouldName,"top:20px;left:450px;");

		cmbMouldName=new ComboBox();
		cmbMouldName.setImmediate(true);
		cmbMouldName.setWidth("200px");
		cmbMouldName.setHeight("24px");
		cmbMouldName.setNullSelectionAllowed(true);
		cmbMouldName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		//mainLayout.addComponent(cmbMouldName,"top:18px;left:530px;");
		
		lblMachineName=new Label();
		lblMachineName.setValue("Machine Name: ");
		//mainLayout.addComponent(lblMouldName,"top:20px;left:450px;");

		cmbMachineName=new ComboBox();
		cmbMachineName.setImmediate(true);
		cmbMachineName.setWidth("200px");
		cmbMachineName.setHeight("24px");
		cmbMachineName.setNullSelectionAllowed(true);
		cmbMachineName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		cmbFindProductName =new ComboBox("Product Name:");
		cmbFindProductName.setImmediate(true);
		cmbFindProductName.setWidth("260px");
		cmbFindProductName.setHeight("24px");
		cmbFindProductName.setNullSelectionAllowed(true);
		cmbFindProductName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblOrderQty = new Label("Order Qty : ");
		lblOrderQty.setImmediate(false);
		lblOrderQty.setWidth("100.0%");
		lblOrderQty.setHeight("18px");

		txtOrderQty=new TextRead(1);
		txtOrderQty.setImmediate(true);
		txtOrderQty.setWidth("100px");
		txtOrderQty.setHeight("24px");


		lblRemainQty = new Label("Remain Qty : ");
		lblRemainQty.setImmediate(false);
		lblRemainQty.setWidth("100.0%");
		lblRemainQty.setHeight("18px");

		txtRemainQty =new TextRead(1);
		txtRemainQty.setImmediate(true);
		txtRemainQty.setWidth("100px");
		txtRemainQty.setHeight("24px");

		lblReqQty = new Label("Requisition Qty : ");
		lblReqQty.setImmediate(false);
		lblReqQty.setWidth("100.0%");
		lblReqQty.setHeight("18px");

		txtReqQty =new AmountField();
		txtReqQty.setImmediate(true);
		txtReqQty.setWidth("100px");
		txtReqQty.setHeight("24px");

		lblReqNo1 = new Label("Req No : ");
		lblReqNo1.setImmediate(false);
		lblReqNo1.setWidth("100.0%");
		lblReqNo1.setHeight("18px");

		txtReqNo =new TextRead(1);
		txtReqNo.setImmediate(true);
		txtReqNo.setWidth("100px");
		txtReqNo.setHeight("24px");
		
		// lblDate
		lblReqDate = new Label("Req Date: ");
		lblReqDate.setImmediate(true);
		lblReqDate.setWidth("100.0%");
		lblReqDate.setHeight("18px");

		//Declare Date
		dateReq = new PopupDateField();
		dateReq.setImmediate(true);
		dateReq.setWidth("110px");
		dateReq.setDateFormat("dd-MM-yyyy");
		dateReq.setValue(new java.util.Date());
		dateReq.setResolution(PopupDateField.RESOLUTION_DAY);

		// lblDate
		lblDeliveryDate = new Label("Delivery Date: ");
		lblDeliveryDate.setImmediate(true);
		lblDeliveryDate.setWidth("100.0%");
		lblDeliveryDate.setHeight("18px");

		//Declare Date
		dateDelivery = new PopupDateField();
		dateDelivery.setImmediate(true);
		dateDelivery.setWidth("110px");
		dateDelivery.setDateFormat("dd-MM-yyyy");
		dateDelivery.setValue(new java.util.Date());
		dateDelivery.setResolution(PopupDateField.RESOLUTION_DAY);

		txtFindReqQty =new TextRead("Requisition Qty:");
		txtFindReqQty.setImmediate(true);
		txtFindReqQty.setWidth("100px");
		txtFindReqQty.setHeight("24px");

		submitButton.setWidth("100px");
		submitButton.setHeight("28px");
		submitButton.setIcon(new ThemeResource("../icons/icon_get_world.gif"));

		generateButton.setWidth("180px");
		generateButton.setHeight("28px");
		generateButton.setIcon(new ThemeResource("../icons/generate.png"));
		
		btnRequisitionReport.setWidth("180px");
		btnRequisitionReport.setHeight("28px");
		btnRequisitionReport.setIcon(new ThemeResource("../icons/generate.png"));

		designPanel = new Panel();
		designPanel.setImmediate(true);
		designPanel.setWidth("560px");
		designPanel.setHeight("200px");
		designPanel.setStyleName("rectangle");

		tableRequisition.setSelectable(true);
		tableRequisition.setWidth("600px");
		tableRequisition.setHeight("154px");
		tableRequisition.setImmediate(true); // react at once when something is selected
		tableRequisition.setColumnCollapsingAllowed(true);	
		tableRequisition.setFooterVisible(true);

		tableRequisition.addContainerProperty("SL", Label.class, new Label());
		tableRequisition.setColumnWidth("SL",15);
		tableRequisition.setColumnAlignment("SL", tableRequisition.ALIGN_CENTER);

		tableRequisition.addContainerProperty("Item Name", Label.class, new Label());
		tableRequisition.setColumnWidth("Item Name", 250);
		tableRequisition.setColumnAlignment("Item Name", tableRequisition.ALIGN_CENTER);

		tableRequisition.addContainerProperty("Unit", TextRead.class, new TextRead());
		tableRequisition.setColumnWidth("Unit", 35);
		tableRequisition.setColumnAlignment("Unit", tableRequisition.ALIGN_CENTER);


		tableRequisition.addContainerProperty("Percent",AmountField.class, new AmountField());
		tableRequisition.setColumnWidth("Percent", 45);
		tableRequisition.setColumnAlignment("Percent", tableRequisition.ALIGN_CENTER);

		tableRequisition.addContainerProperty("Std Qty", TextRead.class, new TextRead());
		tableRequisition.setColumnWidth("Std Qty", 70);
		tableRequisition.setColumnAlignment("Std Qty", tableRequisition.ALIGN_CENTER);

		tableRequisition.addContainerProperty("Req Qty", TextRead.class, new TextRead());
		tableRequisition.setColumnWidth("Req Qty", 80);
		tableRequisition.setColumnAlignment("Req Qty", tableRequisition.ALIGN_CENTER);

		tableChkRequisition.setWidth("640px");
		tableChkRequisition.setHeight("290px");
		tableChkRequisition.setFooterVisible(true);
		tableChkRequisition.setImmediate(true); 
		tableChkRequisition.setColumnCollapsingAllowed(true);

		tableChkRequisition.addContainerProperty("SL", Label.class, new Label());
		tableChkRequisition.setColumnWidth("SL", 15);
		tableChkRequisition.setColumnAlignment("SL", tableChkRequisition.ALIGN_CENTER);

		tableChkRequisition.addContainerProperty("Edit",  CheckBox.class, new CheckBox());
		tableChkRequisition.setColumnWidth("Edit",18);

		tableChkRequisition.addContainerProperty("Show",  CheckBox.class, new CheckBox());
		tableChkRequisition.setColumnWidth("Show",18);

		tableChkRequisition.addContainerProperty("Job Order",Label.class , new Label());
		tableChkRequisition.setColumnWidth("Job Order",40);

		tableChkRequisition.addContainerProperty("Product Name", Label.class , new Label());
		tableChkRequisition.setColumnWidth("Product Name",170);

		tableChkRequisition.addContainerProperty("Req Qty", TextRead.class , new TextRead(1));
		tableChkRequisition.setColumnWidth("Req Qty",80);

		tableChkRequisition.addContainerProperty("FGid",Label.class , new Label());
		tableChkRequisition.setColumnWidth("FGid",60);
		tableChkRequisition.setColumnCollapsed("FGid", true);

		tableChkRequisition.addContainerProperty("FgName",Label.class , new Label());
		tableChkRequisition.setColumnWidth("FgName",60);
		tableChkRequisition.setColumnCollapsed("FgName", true);

		tableChkRequisition.addContainerProperty("SemiFgId",Label.class , new Label());
		tableChkRequisition.setColumnWidth("SemiFgId",50);
		tableChkRequisition.setColumnCollapsed("SemiFgId", true);

		tableChkRequisition.addContainerProperty("mouldId",Label.class , new Label());
		tableChkRequisition.setColumnWidth("mouldId",50);
		tableChkRequisition.setColumnCollapsed("mouldId", true);

		tableChkRequisition.addContainerProperty("mouldName",Label.class , new Label());
		tableChkRequisition.setColumnWidth("mouldName",180);
		//tableChkRequisition.setColumnCollapsed("mouldName", true);
		
		tableChkRequisition.addContainerProperty("machineId",Label.class , new Label());
		tableChkRequisition.setColumnWidth("machineId",50);
		tableChkRequisition.setColumnCollapsed("machineId", true);

		tableChkRequisition.addContainerProperty("machineName",Label.class , new Label());
		tableChkRequisition.setColumnWidth("machineName",80);
		tableChkRequisition.setColumnCollapsed("machineName", true);

		tableChkRequisition.addContainerProperty("partyId",Label.class , new Label());
		tableChkRequisition.setColumnWidth("partyId",50);
		tableChkRequisition.setColumnCollapsed("partyId", true);

		tableChkRequisition.addContainerProperty("partyName",Label.class , new Label());
		tableChkRequisition.setColumnWidth("partyName",80);
		tableChkRequisition.setColumnCollapsed("partyName", true);

		lblReqItem = new Label("<font color='Green' size='4px' allign= 'centre'><b>Item For Requisition</b></font>", Label.CONTENT_XHTML);
		lblReqItem.setHeight("-1px");
		lblReqItem.setImmediate(false);	

		tableReqItem.setWidth("400px");
		tableReqItem.setHeight("300px");
		tableReqItem.setFooterVisible(true);
		tableReqItem.setImmediate(true); 
		tableReqItem.setColumnCollapsingAllowed(true);

		tableReqItem.addContainerProperty("SL", Label.class, new Label());
		tableReqItem.setColumnWidth("SL",15);
		tableReqItem.setColumnAlignment("SL", tableReqItem.ALIGN_CENTER);

		tableReqItem.addContainerProperty("RM Name", ComboBox.class , new ComboBox());
		tableReqItem.setColumnWidth("RM Name",210);

		tableReqItem.addContainerProperty("Unit", TextRead.class , new TextRead(1));
		tableReqItem.setColumnWidth("Unit",50);

		tableReqItem.addContainerProperty("Flr Rcv Qty", TextRead.class , new TextRead(1));
		tableReqItem.setColumnWidth("Flr Rcv Qty",90);
		tableReqItem.setColumnCollapsed("Flr Rcv Qty", true);

		tableReqItem.addContainerProperty("Req Qty", Label.class , new Label());
		tableReqItem.setColumnWidth("Req Qty",50);

		mainLayout.addComponent(tableRequisition,"top:10.0px;left:470px;");

		mainLayout.addComponent(lblProductionType,"top:10.0px;left:50.0px;");
		mainLayout.addComponent(cmbProductionType , "top:8.0px;left:150.0px;");
		
		mainLayout.addComponent(lblJobOrder,"top:36.0px;left:50.0px;");
		mainLayout.addComponent(cmbJobOrder , "top:34.0px;left:150.0px;");

		mainLayout.addComponent(lblProductName,"top:62.0px;left:50.0px;");
		mainLayout.addComponent(cmbProductName, "top:60.0px;left:150.0px;");

		mainLayout.addComponent(lblMachineName,"top:88.0px;left:50.0px;");
		mainLayout.addComponent(cmbMachineName, "top:86.0px;left:150.0px;");
		
		mainLayout.addComponent(lblMouldName,"top:114.0px;left:50.0px;");
		mainLayout.addComponent(cmbMouldName, "top:112.0px;left:150.0px;");

		mainLayout.addComponent(lblOrderQty,"top:140.0px;left:50.0px;");
		mainLayout.addComponent(txtOrderQty, "top:138.0px;left:150.0px;");

		mainLayout.addComponent(lblRemainQty,"top:166.0px;left:50.0px;");
		mainLayout.addComponent(txtRemainQty, "top:164.0px;left:150.0px;");

		mainLayout.addComponent(lblReqQty,"top:192.0px;left:50.0px;");
		mainLayout.addComponent(txtReqQty, "top:190.0px;left:150.0px;");

		mainLayout.addComponent(lblReqNo1,"top:140.0px;left:260.0px;");
		mainLayout.addComponent(txtReqNo, "top:138.0px;left:360.0px;");

		mainLayout.addComponent(lblReqDate,"top:166.0px;left:260.0px;");
		mainLayout.addComponent(dateReq, "top:164.0px;left:360.0px;");
		
		mainLayout.addComponent(lblDeliveryDate,"top:192.0px;left:260.0px;");
		mainLayout.addComponent(dateDelivery, "top:190.0px;left:360.0px;");

		mainLayout.addComponent(submitButton,"top:190.0px;left:470.0px;");

		mainLayout.addComponent(tableChkRequisition,"top:230.0px;left:10px;");
		mainLayout.addComponent(lblReqItem,"top:190.0px;left:670px;");
		mainLayout.addComponent(tableReqItem,"top:216.0px;left:670px;");

		mainLayout.addComponent(generateButton,"top:515.0px;left:80.0px;");
		mainLayout.addComponent(btnRequisitionReport,"top:560.0px;left:720.0px;");

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:540.0px;left:0.0px;");
		mainLayout.addComponent(button, "top:560.0px;left:160.0px;");

		return mainLayout;


	}
}
