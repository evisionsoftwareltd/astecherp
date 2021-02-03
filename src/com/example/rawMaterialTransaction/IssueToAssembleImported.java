package com.example.rawMaterialTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.vaadin.data.Property.*;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class IssueToAssembleImported extends Window {

	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	boolean isUpdate=false,isFind=false;

	private Label lblIssueTo,lblBatchNo,lblReqNo,lblIssueDate,lblIssueNo,lblReqDate,lblsection,lblchallanNo,lblstore;
	private ComboBox cmbIssueTo,cmbBatchNo,cmbsection,cmbstorelocation;
	private TextRead txtReqNo,txtIssueNo,txtmasterIssueNo,txtvoucherno;
	private PopupDateField dIssueDate,dReqDate;

	private TextField txtchallanNo;

	Table table = new Table();
	ArrayList<Label>tbSl = new ArrayList<Label>();
	ArrayList<ComboBox>tbCmbItemName = new ArrayList<ComboBox>();
	ArrayList<Label>tbColor =  new ArrayList<Label>();
	ArrayList<Label>tbUnit =  new ArrayList<Label>();
	ArrayList<TextRead>tbSectionStock =  new ArrayList<TextRead>();
	ArrayList<TextRead>tbAssembleStock =  new ArrayList<TextRead>();
	ArrayList<TextRead>tbReqQty =  new ArrayList<TextRead>();
	ArrayList<TextRead>tbIssuedQty =  new ArrayList<TextRead>();
	ArrayList<TextRead>tbRemainQty =  new ArrayList<TextRead>();
	ArrayList<AmountField>tbIssueQty = new ArrayList<AmountField>();
	ArrayList<TextField>tbRemarks=new ArrayList<TextField>();
	ArrayList<TextRead>tbrate =  new ArrayList<TextRead>();
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0");
	private HashMap hmissueQty= new HashMap();
	public IssueToAssembleImported(SessionBean sessionBean,String caption,int a){
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("ISSUE TO ASSEMBLE(Imported) :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		componentIni(true);
		btnIni(true);
		focusEnter();
		sectiondataload();
		storedataload();
	}

	private void sectiondataload()
	{
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		
		String sql="select AutoID,SectionName+'-'+vDepartmentName depName from tbSectionInfo";
		//String sql="select AutoID,SectionName from tbSectionInfo order by SectionName";
		List lst=session.createSQLQuery(sql).list();
		Iterator<?>itr=lst.iterator();
		while(itr.hasNext())

		{
			Object[]element=(Object[]) itr.next();
			cmbsection.addItem(element[0]);
			cmbsection.setItemCaption(element[0], element[1].toString());
		}
	}
	
	private void storedataload()
	{
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		String sql="select vDepoId,vDepoName from tbDepoInformation";
		List lst=session.createSQLQuery(sql).list();
		Iterator<?>itr=lst.iterator();
		while(itr.hasNext())

		{
			Object[]element=(Object[]) itr.next();
			cmbstorelocation.addItem(element[0]);
			cmbstorelocation.setItemCaption(element[0], element[1].toString());
		}
	}

	private void focusEnter() {
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(cmbsection);
		focusComp.add(cmbIssueTo);
		focusComp.add(cmbBatchNo);
		focusComp.add(cmbstorelocation);
		focusComp.add(txtchallanNo);

		for(int i = 0; i < tbSl.size(); i++)
		{
			focusComp.add(tbCmbItemName.get(i));
			focusComp.add(tbIssueQty.get(i));
			focusComp.add(tbRemarks.get(i));
		}

		focusComp.add(cButton.btnNew);
		focusComp.add(cButton.btnEdit);
		focusComp.add(cButton.btnSave);
		focusComp.add(cButton.btnRefresh);
		focusComp.add(cButton.btnDelete);
		focusComp.add(cButton.btnFind);

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
	protected void refreshButtonEvent()
	{
		isFind=false;
		isUpdate=false;
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void txtClear() {
		cmbIssueTo.setValue(null);
		cmbBatchNo.setValue(null);
		txtReqNo.setValue("");
		dIssueDate.setValue(new java.util.Date());
		dReqDate.setValue(new java.util.Date());
		txtIssueNo.setValue("");
		txtmasterIssueNo.setValue("");
		cmbsection.setValue(null);
		txtchallanNo.setValue("");
		txtvoucherno.setValue("");
		cmbstorelocation.setValue(null);
		tableClear();
	}

	private void tableClear() {
		for(int a=0;a<tbSl.size();a++){
			tbCmbItemName.get(a).removeAllItems();
			tbCmbItemName.get(a).setValue(null);
			tbUnit.get(a).setValue("");
			tbColor.get(a).setValue("");
			tbSectionStock.get(a).setValue("");
			tbAssembleStock.get(a).setValue("");
			tbReqQty.get(a).setValue("");
			tbIssuedQty.get(a).setValue("");
			tbRemainQty.get(a).setValue("");
			tbIssueQty.get(a).setValue("");
			tbRemarks.get(a).setValue("");
		}
	}


	protected void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		//txtFinishItemName.setValue("FI-"+selectFinishItemName());
		cmbIssueTo.focus();
	}
	private boolean doubleEntryCheck(int ar){
		for(int a=0;a<tbSl.size();a++){
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
	private void componentIni(boolean b) {
		cmbIssueTo.setEnabled(!b);
		cmbBatchNo.setEnabled(!b);
		txtReqNo.setEnabled(!b);
		dIssueDate.setEnabled(!b);
		dReqDate.setEnabled(!b);
		txtIssueNo.setEnabled(!b);
		cmbsection.setEnabled(!b);
		txtchallanNo.setEnabled(!b);
		cmbstorelocation.setEnabled(!b);
		table.setEnabled(!b);
		txtmasterIssueNo.setEnabled(!b);
	}
	private void updateButtonEvent(){
		if(!txtIssueNo.getValue().toString().isEmpty())
		{
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void setEventAction() {
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind=false;
				newButtonEvent();
			}
		});

		cButton.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sessionBean.isUpdateable()){
					isUpdate = true;
					updateButtonEvent();
				}
				else{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnExit.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
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
		cButton.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				isFind=true;
				isUpdate=true;
				findButtonEvent();
			}
		});
		cmbIssueTo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbIssueTo.getValue()!=null){
					cmbBatchNoLoad();
				}
				else{
					cmbBatchNo.removeAllItems();
				}
			}
		});
		cmbBatchNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbBatchNo.getValue()!=null){
					reqIssueNoSet();
					tableClear();
					tableCmbItemDataLoad();
				}
				else{
					tableClear();
					txtReqNo.setValue("");
					txtIssueNo.setValue("");
				}
			}
		});
		
		dIssueDate.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				
			}
		});
	}
	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete from tbIssueToAssembleInfo where issueNo='"+txtIssueNo.getValue()+ "'").executeUpdate();
			session.createSQLQuery("delete from tbIssueToAssembleDetails where issueNo='"+txtIssueNo.getValue()+ "'").executeUpdate();
			session.createSQLQuery("delete  from tbRawIssueInfo where IssueNo='"+txtmasterIssueNo.getValue().toString()+"' ").executeUpdate();		
			session.createSQLQuery("delete  from tbRawIssueDetails where IssueNo='"+txtmasterIssueNo.getValue().toString()+"' ").executeUpdate();	
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormat.format(dIssueDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			session.createSQLQuery("delete "+voucher+"  where Voucher_No='"+txtvoucherno.getValue()+"'").executeUpdate();

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
						isFind=false;
						componentIni(true);
						btnIni(true);
						tableClear();
						txtClear();
						mb.close();
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
						mb.close();
					}
				}
			});
		}

	}
	
	private String Option(){
		String strFlag="debit";
		if(strFlag.equalsIgnoreCase("debit"))
			strFlag= "RDP";
		else
			strFlag= "RCP";

		return strFlag;
	}
	
	private String autoUdTransactionNo() 
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "select ISNULL(MAX(transactionNo),0)+1  from tbudrawIssueInfo";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}

	
	
	private void insertData()
	{
		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String issueNo="";
		String masterIssueNo="";
		String vocherId="";
		String udFlag="";
		String voucharType="";
		double totalamount=0;
		if(!isUpdate)
		{
			txtmasterIssueNo.setValue(masterInnsueNo());
			
			System.out.println("master transaction:"+txtmasterIssueNo.getValue().toString());
		}

		String type="New";
		if(isUpdate){
			type="Update";
		}
		else{
			issueNoLoad();
		}


		if(!isUpdate)
		{
			vocherId=vocherIdGenerate();
			udFlag="New";
		}

		else
		{
			vocherId=txtvoucherno.getValue().toString();
			udFlag="Update";
		}
		
		voucharType = Option();
		 double amount=0;
		for(int i=0;i<tbrate.size();i++)
		{
		   if(tbCmbItemName.get(i).getValue()!=null && !tbrate.get(i).getValue().toString().isEmpty() && !tbIssueQty.get(i).getValue().toString().isEmpty())
		   {
			   amount=Double.parseDouble(tbIssueQty.get(i).getValue().toString().isEmpty()?"0": tbIssueQty.get(i).getValue().toString())* Double.parseDouble(tbrate.get(i).getValue().toString().isEmpty()?"0": tbrate.get(i).getValue().toString()); 
			   totalamount=totalamount+amount;
		   }
		}
		try{
			String sqlInfo="insert into tbIssueToAssembleInfo(issueTo,batchNo,reqNo,issueNo,reqDate,issueDate, "+
					" userIp,userName,entryTime,isAdjust,importIssueNo)values('"+cmbIssueTo.getValue()+"','"+cmbBatchNo.getValue()+"'," +
					"'"+txtReqNo.getValue()+"','"+txtIssueNo.getValue()+"','"+dateFormat.format(dReqDate.getValue())+"'," +
					"'"+dateFormat.format(dReqDate.getValue())+"','"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,0,'"+txtmasterIssueNo.getValue().toString()+"')";
			session.createSQLQuery(sqlInfo).executeUpdate();
			
			String sql= " insert into tbRawIssueInfo(IssueNo,IssuedTo,Date,TotalAmount,ProductionType,productionStep,finishedGoods,challanNo,UserId,userIp,EntryTime,VoucherNo,VoucherType,issueRef,isActive,vItemType,dReqDate,vflag) "
					+" values('"+txtmasterIssueNo.getValue().toString()+"','"+cmbsection.getValue().toString()+"', "
					+" '"+dateFormat.format(dIssueDate.getValue())+"','"+totalamount+"', "
					+" 'PT-8','Assemble' ,'' ,"
					+" '"+txtchallanNo.getValue().toString()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,  "
					+" '"+vocherId+"','"+voucharType+"','"+cmbBatchNo.getValue()+"', 1, (select  vCategoryType from tbRawItemInfo where vRawItemCode='"+tbCmbItemName.get(0).getValue().toString()+"'),'"+dateFormat.format(dReqDate.getValue())+"','OLD') ";
			session.createSQLQuery(sql).executeUpdate();
			
			String sqludInfo= " insert into tbudRawIssueInfo(IssueNo,IssuedTo,Date,TotalAmount,ProductionType,productionStep,finishedGoods,challanNo,UserId,userIp,EntryTime,VoucherNo,VoucherType,issueRef,isActive,transactionNo,udflag,vItemType,vflag) "
					+" values('"+txtmasterIssueNo.getValue().toString()+"','"+cmbsection.getValue().toString()+"', "
					+" '"+dateFormat.format(dIssueDate.getValue())+"','"+totalamount+"', "
					+" 'PT-8','Assemble' ,'' ,"
					+" '"+txtchallanNo.getValue().toString()+"','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,  "
					+" '"+vocherId+"','"+voucharType+"','"+cmbBatchNo.getValue()+"',1,'"+autoUdTransactionNo()+"','"+udFlag+"',(select  vCategoryType from tbRawItemInfo where vRawItemCode='"+tbCmbItemName.get(0).getValue().toString()+"'),'OLD') ";
			System.out.println("sqludInfo:"+sqludInfo);
			
			session.createSQLQuery(sqludInfo).executeUpdate();
			
			
			for(int a=0;a<tbSl.size();a++){
				if(tbCmbItemName.get(0).getValue()!=null&&Double.parseDouble("0"+tbIssueQty.get(a).getValue().toString())>0){
					String sqlDetails="insert into tbIssueToAssembleDetails(issueNo,productId,productName,unit,color,ReqQty, "+
							" issuedQty,remainQty,issueQty,remarks,isAdjust,batchNo)values('"+txtIssueNo.getValue()+"','"+tbCmbItemName.get(a).getValue()+"'," +
							"'"+tbCmbItemName.get(a).getItemCaption(tbCmbItemName.get(a).getValue())+"','"+tbUnit.get(a).getValue()+"'," +
							"'"+tbColor.get(a).getValue()+"','"+tbReqQty.get(a).getValue()+"','"+tbIssuedQty.get(a).getValue()+"'," +
							"'"+tbRemainQty.get(a).getValue()+"','"+tbIssueQty.get(a).getValue()+"','"+tbRemarks.get(a).getValue()+"',0,'"+cmbBatchNo.getValue()+"')";

					session.createSQLQuery(sqlDetails).executeUpdate();
				}
			}

			String sqlInfoUd="insert into tbUdIssueToAssembleInfo(issueTo,batchNo,reqNo,issueNo,reqDate,issueDate, "+
					" userIp,userName,entryTime,type)values('"+cmbIssueTo.getValue()+"','"+cmbBatchNo.getValue()+"'," +
					"'"+txtReqNo.getValue()+"','"+txtIssueNo.getValue()+"','"+dateFormat.format(dReqDate.getValue())+"'," +
					"'"+dateFormat.format(dReqDate.getValue())+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'"+type+"')";
			session.createSQLQuery(sqlInfoUd).executeUpdate();

			for(int a=0;a<tbSl.size();a++){
				if(tbCmbItemName.get(0).getValue()!=null&&Double.parseDouble("0"+tbIssueQty.get(a).getValue().toString())>0){
					String sqlDetailsUd="insert into tbUdIssueToAssembleDetails(issueNo,productId,productName,unit,color,ReqQty, "+
							" issuedQty,remainQty,issueQty,remarks,type)values('"+txtIssueNo.getValue()+"','"+tbCmbItemName.get(a).getValue()+"'," +
							"'"+tbCmbItemName.get(a).getItemCaption(tbCmbItemName.get(a).getValue())+"','"+tbUnit.get(a).getValue()+"'," +
							"'"+tbColor.get(a).getValue()+"','"+tbReqQty.get(a).getValue()+"','"+tbIssuedQty.get(a).getValue()+"'," +
							"'"+tbRemainQty.get(a).getValue()+"','"+tbIssueQty.get(a).getValue()+"','"+tbRemarks.get(a).getValue()+"','"+type+"')";

					session.createSQLQuery(sqlDetailsUd).executeUpdate();
				}
			}
			
			
			for (int i = 0; i < tbrate.size(); i++)
			{
				Object temp = tbCmbItemName.get(i).getItemCaption(tbCmbItemName.get(i).getValue());

				if (temp != null && !tbCmbItemName.get(i).getValue().toString().isEmpty())
				{
					String productId="";
					String group="";
					
						productId=tbCmbItemName.get(i).getValue().toString().trim();	
					
					
					String sqlDetails="";
					String sqludDetails="";
					
						System.out.print("group Is"+group);
						sqlDetails = "insert into tbRawIssueDetails (IssueNo,ProductID,Qty,Rate,remarks,storeID,returnFlag,ProductType,groupid) " +
								"values('"+txtmasterIssueNo.getValue().toString().trim()+"','"+productId.trim()+"'," +
								" '"+tbIssueQty.get(i).getValue().toString().trim()+"'," +
								" '"+tbrate.get(i).getValue().toString().trim()+"'," +
								" '"+tbRemarks.get(i).getValue().toString()+"', "
								+" '"+cmbstorelocation.getValue()+"','1','','')  " ;
						
						session.createSQLQuery(sqlDetails).executeUpdate();
						
						//sqludDetails
						
						sqludDetails = "insert into tbudRawIssueDetails (IssueNo,ProductID,Qty,Rate,remarks,storeID,returnFlag,ProductType,transactionNo) " +
								"values('"+txtmasterIssueNo.getValue().toString().trim()+"','"+productId.trim()+"'," +
								" '"+tbIssueQty.get(i).getValue().toString().trim()+"'," +
								" '"+tbrate.get(i).getValue().toString().trim()+"'," +
								" '"+tbRemarks.get(i).getValue().toString()+"', "
								+" '"+cmbstorelocation.getValue()+"','1','','"+autoUdTransactionNo()+"')  " ;
						
						session.createSQLQuery(sqludDetails).executeUpdate();
					
					
					String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormat.format(dIssueDate.getValue())+"')").list().iterator().next().toString();
					String voucher =  "voucher"+fsl;

					System.out.println("Receipt Data"+voucher);

					String naration="Section :"+cmbsection.getItemCaption(cmbsection.getValue()).toString()+" "+"Ref No :"+txtmasterIssueNo.getValue()+" "+"Issue Date :"+new SimpleDateFormat("yyyy-MM-dd").format(dIssueDate.getValue()).toString();

					

					String proid =tbCmbItemName.get(i).getValue().toString().trim();

					String ProductLedeger="";
					ProductLedeger= productlededger(i);


					if( totalamount>0  )
					{
						double crAmount=0;
						crAmount=Double.parseDouble(tbIssueQty.get(i).getValue().toString().isEmpty()?"0.00":tbIssueQty.get(i).getValue().toString())* Double.parseDouble(tbrate.get(i).getValue().toString().isEmpty()?"0.00":tbrate.get(i).getValue().toString());
					String purchasevoucherquery=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
							+" values('"+vocherId+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dIssueDate.getValue())+"','"+ProductLedeger+"', "  
							+" '"+naration+"','0' , "
							+" '"+crAmount+"','jau','U-1','1' ,'"+sessionBean.getUserId()+"', "
							+" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "
							+" '2', '"+sessionBean.getCompanyId()+"' ,'','"+cmbsection.getItemCaption(cmbsection.getValue())+"' ,'issueAssemble') ";

					session.createSQLQuery(purchasevoucherquery).executeUpdate();
					System.out.println("purchae"+purchasevoucherquery);
					}

				}
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormat.format(dIssueDate.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				System.out.println("Receipt Data"+voucher);

				String naration="Section :"+cmbsection.getItemCaption(cmbsection.getValue()).toString()+" "+"Ref No :"+txtmasterIssueNo.getValue().toString()+" "+"Issue Date :"+new SimpleDateFormat("yyyy-MM-dd").format(dIssueDate.getValue()).toString();

				
				if(totalamount>0  )
				{
					if (i==0)
					{
						String SupplierVoucherquery=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,TransactionWith,sourceForm) "
								+" values('"+vocherId+"','"+new SimpleDateFormat("yyyy-MM-dd").format(dIssueDate.getValue())+"','AL1704', "  
								+" '"+naration+"','"+totalamount+"' , "
								+" '0','jau','U-1','1' ,'"+sessionBean.getUserId()+"', "
								+" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, "
								+" '2', '"+sessionBean.getCompanyId()+"','' ,'"+cmbsection.getItemCaption(cmbsection.getValue())+"','issueAssemble') ";

						session.createSQLQuery(SupplierVoucherquery).executeUpdate();
						System.out.println("Supplier"+SupplierVoucherquery);	
					}
						
				}
				
			}
			
			showNotification("All Information Saved Successfully",Notification.TYPE_WARNING_MESSAGE);
			tx.commit();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public String productlededger(int i) 
	{
		String autoCode = "";
		String productId="";

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
				productId=tbCmbItemName.get(i).getValue().toString();	
			

			String query="select Ledger_Id  from tbLedger where Ledger_Id=(select vLedgerCode from tbRawItemInfo where vRawItemCode like '"+productId+"')";
			System.out.println("ledgerpr"+query);
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}

	public String vocherIdGenerate()
	{
		String vo_id = null;
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormat.format(dIssueDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;


			String query="select 'JV-NO-' + cast(isnull(max(cast(replace(Voucher_No, 'JV-NO-', '')as int))+1, 1)as varchar) from "+voucher+" where substring(vouchertype ,1,1) = 'j'";
			Iterator iter = session.createSQLQuery(query).list().iterator();
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
		return vo_id;
	}


	private boolean checkValidation(){
		if(cmbIssueTo.getValue()!=null){
			if(cmbBatchNo.getValue()!=null){
				if(tbCmbItemName.get(0).getValue()!=null&&
						Double.parseDouble("0"+tbIssueQty.get(0).getValue().toString())>0){
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
			showNotification("Please Provide Issue To",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}
	private void tableCmbItemDataLoad() {
		/*for(int a=0;a<tbSl.size();a++){
			tbCmbItemName.get(a).removeAllItems();
			Iterator iter=dbService("select  b.productId,b.productName "+
					" from tbRequisitionEntryAssembleInfo a "+
					" inner join tbRequisitionEntryAssembleDetails b "+ 
					" on a.batchNo=b.batchNo where a.batchNo='"+cmbBatchNo.getValue()+"'  order by b.productName");
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				tbCmbItemName.get(a).addItem(element[0]);
				tbCmbItemName.get(a).setItemCaption(element[0], element[1].toString());
			}
		}*/
		int a=0;
		Iterator iter=dbService("select  b.productId,b.productName "+
				" from tbRequisitionEntryAssembleInfo a "+
				" inner join tbRequisitionEntryAssembleDetails b "+ 
				" on a.batchNo=b.batchNo where a.batchNo='"+cmbBatchNo.getValue()+"' and b.productId like '%RI%'    order by b.productName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbItemName.get(a).addItem(element[0]);
			tbCmbItemName.get(a).setItemCaption(element[0], element[1].toString());
			tbCmbItemName.get(a).setValue(element[0]);
			a++;
			if(a==tbCmbItemName.get(a).size()){
				tableRowAdd(a+1);
			}
		}
		for(int x=0;x<a;x++){
			tbCmbAction(x);
		}
		tbIssueQty.get(0).focus();
	}
	private void reqIssueNoSet() {
		Iterator iter=dbService("select 0,ReqNo from tbRequisitionEntryAssembleInfo where ReqFrom like" +
				" '"+cmbIssueTo.getValue()+"' and batchNo='"+cmbBatchNo.getValue()+"'");
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtReqNo.setValue(element[1]);
		}

	}
	private void issueNoLoad(){
		Iterator iter1=dbService("select 0,isnull(max(issueNo),0)+1 id from tbIssueToAssembleInfo");
		if(iter1.hasNext()){
			Object element1[]=(Object[])iter1.next();
			txtIssueNo.setValue(element1[1]);
		}
	}

	//masterInnsueNo

	private int  masterInnsueNo(){
		String sql="select 0 df, ISNULL(max( CAST(IssueNo as int) ),0)+1  fgg  from tbrawIssueInfo ";
		Iterator iter1=dbService(sql);
		if(iter1.hasNext()){
			Object element1[]=(Object[])iter1.next();
			//txtmasterIssueNo.setValue(element1[1]);
			return  Integer.parseInt(element1[1].toString()) ;
		}
		return 0;
	}

	private void cmbBatchNoLoad() {
		String sql="select 0,batchNo from tbRequisitionEntryAssembleInfo where ReqFrom like '"+cmbIssueTo.getValue()+"' " +
				"and batchNo not in(select batchNo from tbIssueToAssembleInfo where issueTo='Assemble') and batchNo in (select batchNo from tbRequisitionEntryAssembleDetails where productId like '%RI%') ";
		if(isFind){
			sql="select 0,batchNo from tbRequisitionEntryAssembleInfo where ReqFrom like '"+cmbIssueTo.getValue()+"' and batchNo in (select batchNo from tbRequisitionEntryAssembleDetails where productId like '%RI%')  ";

		}
		cmbBatchNo.removeAllItems();
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbBatchNo.addItem(element[1]);
			cmbBatchNo.setItemCaption(element[1], element[1].toString());
		}

	}
	private void tableInitialize()
	{
		for (int a = 0; a<10; a++) 
		{
			tableRowAdd(a);
		}
	}
	private void tbCmbAction(int ar){
		Iterator iter=dbService("select sectionStock,assembleStock,reqQty,issuedQty,(reqQty-issuedQty)remainQty,(select closingRate from dbo.[funRawMaterialsStock](CURRENT_TIMESTAMP,'"+tbCmbItemName.get(ar).getValue()+"'))rate from( "+
				" select sectionStock,assembleStock,(select ISNULL(SUM(reqQty),0)  "+
				" from tbRequisitionEntryAssembleDetails  "+
				" where batchNo='"+cmbBatchNo.getValue()+"' and productId='"+tbCmbItemName.get(ar).getValue()+"')reqQty, "+
				" (select ISNULL(SUM(issueQty),0) from tbIssueToAssembleDetails where  "+
				" batchNo='"+cmbBatchNo.getValue()+"' and productId ='"+tbCmbItemName.get(ar).getValue()+"')issuedQty "+
				" from funAssembleStockNew('"+tbCmbItemName.get(ar).getValue()+"',CURRENT_TIMESTAMP) "+
				" ) a  ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbSectionStock.get(ar).setValue(df.format(element[0]));
			tbReqQty.get(ar).setValue(df.format(element[2]));
			tbIssuedQty.get(ar).setValue(df.format(element[3]));
			tbRemainQty.get(ar).setValue(df.format(element[4]));
			tbAssembleStock.get(ar).setValue(df.format(element[1]));
			tbrate.get(ar).setValue(df.format(element[5]));

		}
	}
	private void tableRowAdd(final int rq)
	{
		tbSl.add(rq, new Label());
		tbSl.get(rq).setValue(rq+1);
		tbSl.get(rq).setWidth("100%");
		tbSl.get(rq).setHeight("-1px");

		tbCmbItemName.add(rq, new ComboBox() );
		tbCmbItemName.get(rq).setImmediate(true);
		tbCmbItemName.get(rq).setNullSelectionAllowed(true);
		tbCmbItemName.get(rq).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		tbCmbItemName.get(rq).setWidth("100%");
		tbCmbItemName.get(rq).setHeight("-1px");
		tbCmbItemName.get(rq).setEnabled(false);

		/*tbCmbItemName.get(rq).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tbCmbItemName.get(rq).getValue()!=null){
					if(doubleEntryCheck(rq)){
						tbCmbAction(rq);
						tbIssueQty.get(rq).focus();
						if(rq==tbCmbItemName.get(rq).size()-1){
							tableRowAdd(rq+1);
						}
					}
					else{
						showNotification("Sorry!!","Double Entry",Notification.TYPE_WARNING_MESSAGE);
						tbCmbItemName.get(rq).setValue(null);
						tbCmbItemName.get(rq).focus();
					}
				}
				else{
					tbUnit.get(rq).setValue("");
					tbColor.get(rq).setValue("");
					tbSectionStock.get(rq).setValue("");
					tbReqQty.get(rq).setValue("");
					tbIssuedQty.get(rq).setValue("");
					tbRemainQty.get(rq).setValue("");
					tbIssueQty.get(rq).setValue("");
					tbRemarks.get(rq).setValue("");
				}
			}
		});*/

		tbColor.add(rq, new Label());
		tbColor.get(rq).setImmediate(true);
		tbColor.get(rq).setWidth("-1px");
		tbColor.get(rq).setHeight("-1px");

		tbUnit.add(rq, new Label());
		tbUnit.get(rq).setImmediate(true);
		tbUnit.get(rq).setWidth("-1px");
		tbUnit.get(rq).setHeight("-1px");

		tbSectionStock.add(rq, new TextRead(1));
		tbSectionStock.get(rq).setImmediate(true);
		tbSectionStock.get(rq).setWidth("100%");
		tbSectionStock.get(rq).setHeight("-1px");

		tbAssembleStock.add(rq, new TextRead(1));
		tbAssembleStock.get(rq).setImmediate(true);
		tbAssembleStock.get(rq).setWidth("100%");
		tbAssembleStock.get(rq).setHeight("-1px");

		tbReqQty.add(rq, new TextRead(1));
		tbReqQty.get(rq).setImmediate(true);
		tbReqQty.get(rq).setWidth("100%");
		tbReqQty.get(rq).setHeight("-1px");

		tbIssuedQty.add(rq, new TextRead(1));
		tbIssuedQty.get(rq).setImmediate(true);
		tbIssuedQty.get(rq).setWidth("100%");
		tbIssuedQty.get(rq).setHeight("-1px");

		tbRemainQty.add(rq, new TextRead(1));
		tbRemainQty.get(rq).setImmediate(true);
		tbRemainQty.get(rq).setWidth("100%");
		tbRemainQty.get(rq).setHeight("-1px");

		tbIssueQty.add(rq, new AmountField());
		tbIssueQty.get(rq).setImmediate(true);
		tbIssueQty.get(rq).setWidth("100%");
		tbIssueQty.get(rq).setHeight("-1px");

		tbIssueQty.get(rq).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!isUpdate&&!isFind)
				{
					double remainQty=Double.parseDouble(tbRemainQty.get(rq).getValue().toString().isEmpty()?"0.0":tbRemainQty.get(rq).getValue().toString());
					double issueQty=Double.parseDouble(tbIssueQty.get(rq).getValue().toString().isEmpty()?"0.0":tbIssueQty.get(rq).getValue().toString());
					double stockQty=Double.parseDouble(tbSectionStock.get(rq).getValue().toString().isEmpty()?"0.0":tbSectionStock.get(rq).getValue().toString());

					if(stockQty>=issueQty)
					{
						if(remainQty<issueQty)
						{
							showNotification("Sorry!!","Issue Qty Exceed Remain Qty",Notification.TYPE_WARNING_MESSAGE);
							tbIssueQty.get(rq).setValue("");
							tbIssueQty.get(rq).focus();
						}
					}
					else
					{
						showNotification("Sorry!!","Issue Qty Exceed Stock Qty",Notification.TYPE_WARNING_MESSAGE);
						tbIssueQty.get(rq).setValue("");
						tbIssueQty.get(rq).focus();
					}
				}
				
				if(isUpdate)
				{
				    if(!productionCheck(rq))
				    {
				    	tbIssueQty.get(rq).setValue(hmissueQty.get(tbCmbItemName.get(rq).getValue()));
				    	showNotification("Issue Entry Can not be updated after giving production Entry",Notification.TYPE_WARNING_MESSAGE);
				    }
				   
				}
			}
		});

		tbRemarks.add(rq, new TextField());
		tbRemarks.get(rq).setImmediate(true);
		tbRemarks.get(rq).setWidth("100%");
		tbRemarks.get(rq).setHeight("-1px");

		tbrate.add(rq, new TextRead());
		tbrate.get(rq).setImmediate(true);
		tbrate.get(rq).setWidth("100%");
		tbrate.get(rq).setHeight("-1px");

		table.addItem(new Object[]{tbSl.get(rq),tbCmbItemName.get(rq),tbUnit.get(rq),tbColor.get(rq),tbSectionStock.get(rq),
				tbAssembleStock.get(rq),tbReqQty.get(rq),tbIssuedQty.get(rq),tbRemainQty.get(rq),tbIssueQty.get(rq),tbRemarks.get(rq),tbrate.get(rq)},rq);

	}
	
	
	private boolean productionCheck(int i)
	{
		Transaction tx=null;
		Session session =SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		String sql= "select * from tbIngradiantAssembleDetails where semiFgId='"+tbCmbItemName.get(i).getValue()+"' and assembleDate>'"+dateFormat.format(dIssueDate.getValue())+" ' ";
		List<?>lst=session.createSQLQuery(sql).list();
		if(!lst.isEmpty())
		{
		    return false;	
		}
		
		return true;
	}
	
	
	private void findButtonEvent(){
		Window win = new IssueToAssembleImportedFind(sessionBean, txtItemID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtItemID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtItemID.getValue().toString());
					//System.out.println("Issue No: "+txtItemID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}
	private void findInitialise(String issueNo)
	{
		String sql="select a.issueTo,a.batchNo,a.ReqNo,a.issueDate,a.reqDate,a.issueNo, "+
				" b.productId,b.ReqQty,b.IssuedQty,b.remainQty,b.issueQty,b.remarks,a.importIssueNo,( "+
                "select IssuedTo from tbRawIssueInfo where IssueNo=a.importIssueNo)section,(select distinct  d.storeId from tbRawIssueInfo  c inner join tbRawIssueDetails d "+
                " on c.IssueNo=d.IssueNo where c.IssueNo=a.importIssueNo)storelocation,(select distinct  f.Rate from tbRawIssueInfo  e inner join tbRawIssueDetails f " +
                " on e.IssueNo=f.IssueNo where e.IssueNo=a.importIssueNo and f.ProductID=b.productId)rate,(select VoucherNo from tbRawIssueInfo where IssueNo=a.importIssueNo)voucherNo,(select challanNo from tbRawIssueInfo where IssueNo=a.importIssueNo)challanNo  "+
				" from tbIssueToAssembleInfo a inner join tbIssueToAssembleDetails b "+ 
				" on a.issueNo=b.issueNo where a.issueNo='"+issueNo+"'and a.importIssueNo is not null ";
		
		
		System.out.println("Find sql is:"+sql);
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==0){
				cmbsection.setValue(Integer.parseInt(element[13].toString()) );
				cmbIssueTo.setValue(element[0]);
				cmbBatchNo.setValue(element[1]);
				txtReqNo.setValue(element[2]);
				dIssueDate.setValue(element[3]);
				dReqDate.setValue(element[4]);
				txtIssueNo.setValue(element[5]);
				txtmasterIssueNo.setValue(element[12]);
				cmbstorelocation.setValue(element[14]);
				txtvoucherno.setValue(element[16].toString());
				txtchallanNo.setValue(element[17].toString());
			}
			tbCmbItemName.get(a).setValue(element[6]);
			tbReqQty.get(a).setValue(df.format(element[7]));
			tbIssuedQty.get(a).setValue(df.format(element[8]));
			tbRemainQty.get(a).setValue(df.format(element[9]));
			tbIssueQty.get(a).setValue(df.format(element[10]));
			tbRemarks.get(a).setValue(element[11]);
			tbrate.get(a).setValue(element[15]);
			hmissueQty.put(element[6], element[8]);
			a++;
			if(a==tbCmbItemName.size()-1){
				tableRowAdd(a+1);
			}
		}
	}
	private AbsoluteLayout buildMainLayout() {
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("1065px");
		setHeight("545px");

		cmbsection=new ComboBox();
		cmbsection.setImmediate(true);
		cmbsection.setWidth("200px");
		cmbsection.setHeight("24px");
		cmbsection.setNullSelectionAllowed(true);
		cmbsection.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		/*cmbIssueTo.addItem("Screen Printing");
		cmbIssueTo.addItem("Heat Trasfer Label");
		cmbIssueTo.addItem("Manual Printing");
		cmbIssueTo.addItem("Labeling");
		cmbIssueTo.addItem("Cap Folding");
		cmbIssueTo.addItem("Stretch Blow Molding");*/

		mainLayout.addComponent(new Label("Section: "),"top:10.0px;left:10px;");
		mainLayout.addComponent(cmbsection,"top:8.0px;left:100px;");

		cmbIssueTo=new ComboBox();
		cmbIssueTo.setImmediate(true);
		cmbIssueTo.setWidth("200px");
		cmbIssueTo.setHeight("24px");
		cmbIssueTo.setNullSelectionAllowed(true);
		cmbIssueTo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbIssueTo.addItem("Assemble");
		/*cmbIssueTo.addItem("Screen Printing");
		cmbIssueTo.addItem("Heat Trasfer Label");
		cmbIssueTo.addItem("Manual Printing");
		cmbIssueTo.addItem("Labeling");
		cmbIssueTo.addItem("Cap Folding");
		cmbIssueTo.addItem("Stretch Blow Molding");*/

		mainLayout.addComponent(new Label("Issue To: "),"top:35.0px;left:10px;");
		mainLayout.addComponent(cmbIssueTo,"top:33.0px;left:100px;");

		cmbBatchNo=new ComboBox();
		cmbBatchNo.setImmediate(true);
		cmbBatchNo.setWidth("200px");
		cmbBatchNo.setHeight("24px");
		cmbBatchNo.setNullSelectionAllowed(true);
		cmbBatchNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Batch No: "),"top:60.0px;left:10px;");
		mainLayout.addComponent(cmbBatchNo,"top:58.0px;left:100px;");
		
		cmbstorelocation=new ComboBox();
		cmbstorelocation.setImmediate(true);
		cmbstorelocation.setWidth("200px");
		cmbstorelocation.setHeight("24px");
		cmbstorelocation.setNullSelectionAllowed(true);
		cmbstorelocation.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Store: "),"top:85.0px;left:10px;");
		mainLayout.addComponent(cmbstorelocation,"top:83.0px;left:100px;");

		txtReqNo=new TextRead(1);
		txtReqNo.setImmediate(true);
		txtReqNo.setWidth("130px");
		txtReqNo.setHeight("24px");
		mainLayout.addComponent(new Label("Req No: "),"top:10.0px;left:350px;");
		mainLayout.addComponent(txtReqNo,"top:8.0px;left:490px;");


		txtchallanNo=new TextField();
		txtchallanNo.setImmediate(true);
		
		txtchallanNo.setWidth("100px");
		txtchallanNo.setHeight("24px");
		mainLayout.addComponent(new Label("Challan No: "),"top:35.0px;left:350px;");
		mainLayout.addComponent(txtchallanNo,"top:33.0px;left:490px;");


		dIssueDate = new PopupDateField();
		dIssueDate.setImmediate(true);
		dIssueDate.setWidth("110px");
		dIssueDate.setDateFormat("dd-MM-yyyy");
		dIssueDate.setValue(new java.util.Date());
		dIssueDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label("Issue Date: "),"top:60.0px;left:350px;");
		mainLayout.addComponent(dIssueDate,"top:58.0px;left:490px;");


		dReqDate = new PopupDateField();
		dReqDate.setImmediate(true);
		dReqDate.setWidth("110px");
		dReqDate.setDateFormat("dd-MM-yyyy");
		dReqDate.setValue(new java.util.Date());
		dReqDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label("Req Date: "),"top:10.0px;left:750px;");
		mainLayout.addComponent(dReqDate,"top:8.0px;left:890px;");

		txtIssueNo=new TextRead(1);
		txtIssueNo.setImmediate(true);
		txtIssueNo.setWidth("100px");
		txtIssueNo.setHeight("24px");
		mainLayout.addComponent(new Label("Assemble Issue No: "),"top:35.0px;left:750px;");
		mainLayout.addComponent(txtIssueNo,"top:33.0px;left:890px;");

		txtmasterIssueNo=new TextRead(1);
		txtmasterIssueNo.setImmediate(true);
		txtmasterIssueNo.setWidth("100px");
		txtmasterIssueNo.setHeight("24px");
		mainLayout.addComponent(new Label("Issue No: "),"top:60.0px;left:750px;");
		mainLayout.addComponent(txtmasterIssueNo,"top:58.0px;left:890px;");

		txtvoucherno=new TextRead();





		table.setWidth("100%");
		table.setHeight("320px");
		table.setFooterVisible(true);
		table.setImmediate(true); 
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 15);
		table.setColumnAlignment("SL", table.ALIGN_CENTER);

		table.addContainerProperty("ITEM Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("ITEM Name",320);

		table.addContainerProperty("Unit", Label.class , new Label());
		table.setColumnWidth("Unit",40);
		table.setColumnCollapsed("Unit", true);

		table.addContainerProperty("Color", Label.class , new Label());
		table.setColumnWidth("Color",80);
		table.setColumnCollapsed("Color", true);

		table.addContainerProperty("Store Stock", TextRead.class , new TextRead(1));
		table.setColumnWidth("Store Stock",80);

		table.addContainerProperty("Assemble Stock", TextRead.class , new TextRead(1));
		table.setColumnWidth("Assemble Stock",95);

		table.addContainerProperty("Req Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Req Qty",60);

		table.addContainerProperty("Issued Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Issued Qty",60);

		table.addContainerProperty("Remain Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Remain Qty",60);

		table.addContainerProperty("Issue Qty", AmountField.class , new AmountField());
		table.setColumnWidth("Issue Qty",60);

		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",150);

		table.addContainerProperty("Rate", TextRead.class , new TextRead());
		table.setColumnWidth("Rate",150);
		table.setColumnCollapsed("Rate", true);

		mainLayout.addComponent(table,"top:120.0px;left:10px;");
		tableInitialize();

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:435.0px;left:0.0px;");
		mainLayout.addComponent(cButton, "top:455.0px;left:260.0px;");
		return mainLayout;
	}

}
