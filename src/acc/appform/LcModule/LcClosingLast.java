package acc.appform.LcModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommaSeparator;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class LcClosingLast extends Window {

	private CommonButton cButton = new CommonButton("New", "Save", "","", "Refresh", "Find", "", "", "", "Exit");
	private SessionBean sessionBean;
	private boolean isUpdate = false,isFind=false;
	private AbsoluteLayout mainLayout;

	//ComboBox cmbMrrNo;
	DecimalFormat df=new DecimalFormat("#0.00");
	DecimalFormat df4=new DecimalFormat("#0.0000");

	ArrayList<Component> allComp = new ArrayList<Component>();	
	String fiscaleOpen,fiscalClose;
	CommaSeparator cm=new CommaSeparator();
	SimpleDateFormat dateF=new SimpleDateFormat("yyyy-MM-dd");

	ComboBox cmbLcNo,cmbStatus;
	PopupDateField dOpenDate,dCurDate;
	TextRead txtLedgerBalance,txtLCValueUSD,txtTransactionNo;

	private ArrayList<Label> tbSl = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbReceiveDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> tbMrrNo = new ArrayList<Label>();
	private ArrayList<Label> tbProductId = new ArrayList<Label>();
	private ArrayList<Label> tbProductName = new ArrayList<Label>();
	private ArrayList<Label> tbReceiveUserName = new ArrayList<Label>();
	private ArrayList<TextRead> tbReceivedQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbReceivedRate = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbReceivedAmount = new ArrayList<TextRead>(1);
	private ArrayList<PopupDateField> tbVoucherDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> tbVoucherNo = new ArrayList<Label>();
	private ArrayList<Label> tbVoucherUserName = new ArrayList<Label>();

	Table table=new Table();
	
	public LcClosingLast(SessionBean sessionBean){
		this.sessionBean = sessionBean;
		this.setCaption("L/C CLOSING :: "+this.sessionBean.getCompany());
		this.setResizable(false);
		this.fiscaleOpen=dateF.format(sessionBean.getFiscalOpenDate());
		this.fiscalClose=dateF.format(sessionBean.getFiscalCloseDate());

		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		//cmbLCNoLoad();
		cButton.btnNew.focus();
		focusEnter();
		authencationCheck();
		eventAction();

	}
	private Iterator<?> dbService(String sql){
		Transaction tx=null;
		Session session=null;
		Iterator<?> iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			iter=session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){

				session.close();
			}
		}
		return iter;
	}
	private void cmbLCNoLoad(){
		cmbLcNo.removeAllItems();
		String sql="select vLedgerId,vLcNo from tbLCOpeningINfo where isActive=1  order by dLcOpeningDate";
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbLcNo.addItem(element[0]);
			cmbLcNo.setItemCaption(element[0], element[1].toString());
		}
	}
	private void focusEnter() {
		allComp.add(cmbLcNo);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}
	private void componentIni(boolean b) {
		cmbLcNo.setEnabled(!b);
		dOpenDate.setEnabled(!b);
		cmbStatus.setEnabled(!b);
		txtLCValueUSD.setEnabled(!b);
		txtLedgerBalance.setEnabled(!b);
		txtTransactionNo.setEnabled(!b);
		dCurDate.setEnabled(!b);
		table.setEnabled(!b);
	}
	private void tableClear(){
		for(int a=0;a<tbSl.size();a++){
			tbReceiveDate.get(a).setReadOnly(false);
			tbReceiveDate.get(a).setValue(null);
			tbReceiveDate.get(a).setReadOnly(true);
			tbMrrNo.get(a).setValue("");
			tbProductId.get(a).setValue("");
			tbProductName.get(a).setValue("");
			tbReceivedQty.get(a).setValue("");
			tbReceivedRate.get(a).setValue("");
			tbReceivedAmount.get(a).setValue("");		
			tbReceiveUserName.get(a).setValue("");
			tbVoucherDate.get(a).setReadOnly(false);
			tbVoucherDate.get(a).setValue(null);
			tbVoucherDate.get(a).setReadOnly(true);
			tbVoucherNo.get(a).setValue("");
			tbVoucherUserName.get(a).setValue("");
		}
	}
	private void lcInfoLoad(){
		String sql="select mLCValueUSD,(SELECT (sum(CrAmount)-sum(DrAmount))amount FROM dbo.rptCostLedger(  '"+fiscaleOpen+"',  '"+fiscalClose+"', "+ 
				" vLedgerID,  '%','1') ),dLcOpeningDate,case when isActive=1 then 'Open' else 'Close' end active from  tbLcOpeningInfo where vLcNo='"+cmbLcNo.getItemCaption(cmbLcNo.getValue())+"'";
		Iterator<?> iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtLCValueUSD.setValue(cm.setComma(Double.parseDouble("0"+element[0])));
			txtLedgerBalance.setValue(cm.setComma(Double.parseDouble(element[1].toString())));
			dOpenDate.setValue(element[2]);
			cmbStatus.addItem(element[3]);
			cmbStatus.setValue(element[3]);
		}
	}
	private void newBtnAction()
	{
		btnIni(false);
		componentIni(false);
		txtClear();
		isUpdate = false;
		cmbLcNo.focus();	
	}
	private void txtClear() {
		cmbLcNo.setValue(null);
		dOpenDate.setValue(new java.util.Date());
		cmbStatus.setValue(null);
		txtLCValueUSD.setValue("");
		txtLedgerBalance.setValue("");
		txtTransactionNo.setValue("");
		dCurDate.setValue(new java.util.Date());
		tableClear();
	}
	private boolean tableCheck(){
		for(int a=0;a<tbMrrNo.size();a++){
			if(!tbMrrNo.get(a).getValue().toString().isEmpty()){
				if(tbVoucherDate.get(a).getValue()==null||tbVoucherNo.get(a).getValue().toString().isEmpty()
						||tbVoucherUserName.get(a).getValue().toString().isEmpty()){
					return false;
				}
			}
		}
		return true;
	}
	private boolean checkValidation(){
		
		if(cmbLcNo.getValue()!=null){
			if(cmbStatus.getValue()!=null){
				if(cmbStatus.getValue().toString().equalsIgnoreCase("Open")){
					if(Double.parseDouble("0"+txtLedgerBalance.getValue().toString().replace(",", ""))>0){
						if(tableCheck()){
							return true;
						}
					}
					else{
						showNotification("Please Check Ledger Balance",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Sorry!!","LC Already Closed",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide Status",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide LC No",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private void eventAction() {
		cmbLcNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbLcNo.getValue()!=null){
					tableClear();
					lcInfoLoad();
					lcDetailsLoad();
				}
				else{
					txtLedgerBalance.setValue("");
					txtLCValueUSD.setValue("");
					dCurDate.setValue(new java.util.Date());
					dOpenDate.setValue(new java.util.Date());
					tableClear();
				}
			}
		});
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;
				cmbLCNoLoad();
				newBtnAction();
				
			}
		});
		cButton.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				//formValidation();
				if(checkValidation()){
					saveBtnAction();
				}
			}
		});
		cButton.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}
	private void saveBtnAction()
	{
		try
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{mb.buttonLayout.getComponent(0).setEnabled(false);					
						insertData();
						//reportView();
					}
				}
			});
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error.", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private String getTransactionNo(){
		String autoId=null;
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("select isnull(MAX(TransactionNo),0)+1 from tbLcClosingInfoLast").list().iterator();

			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
				txtTransactionNo.setValue(autoId);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
		return autoId;
	}
	private void insertData(){
		try{
			Transaction tx=null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			getTransactionNo();
			
			String sqlInfo="insert into tbLcClosingInfoLast(TransactionNo,LcLedgerId,lcLedgerName," +
					"lcOpenDate,lcStatus,lcLedgerBalance,lcValueUsd,date,userName,userIp,entryTime)values(" +
					"'"+txtTransactionNo.getValue().toString().trim()+"'," +
					"'"+cmbLcNo.getValue()+"'," +
					"'"+cmbLcNo.getItemCaption(cmbLcNo.getValue())+"'," +
					"'"+dateF.format(dOpenDate.getValue())+"'," +
					"'"+cmbStatus.getValue()+"'," +
					"'"+txtLedgerBalance.getValue().toString().replace(",", "").trim()+"'," +
					"'"+txtLCValueUSD.getValue().toString().replace(",", "").trim()+"'," +
					"'"+dateF.format(dCurDate.getValue())+"'," +
					"'"+sessionBean.getUserName()+"'," +
					"'"+sessionBean.getUserIp()+"'," +
					"CURRENT_TIMESTAMP)";
			System.out.println("Insert : "+sqlInfo);
			session.createSQLQuery(sqlInfo).executeUpdate();
			
			for(int a=0;a<tbMrrNo.size();a++){
				if(!tbVoucherNo.get(a).getValue().toString().isEmpty()){
					String sqlDetails="insert into tbLcClosingDetailsLast(TransactionNo,ReceiveDate,MrrNo," +
							"productId,productName,Qty,Rate,Amount,ReceivedUserName,VoucherDate,VoucherNo," +
							"VoucherUserName)values(" +
							"'"+txtTransactionNo.getValue().toString().trim()+"'," +
							"'"+dateF.format(tbReceiveDate.get(a).getValue())+"'," +
							"'"+tbMrrNo.get(a).getValue()+"'," +
							"'"+tbProductId.get(a).getValue()+"'," +
							"'"+tbProductName.get(a).getValue()+"'," +
							"'"+tbReceivedQty.get(a).getValue().toString().replace(",", "")+"'," +
							"'"+tbReceivedRate.get(a).getValue().toString().replace(",", "")+"'," +
							"'"+tbReceivedAmount.get(a).getValue().toString().replace(",", "")+"'," +
							"'"+tbReceiveUserName.get(a).getValue()+"'," +
							"'"+dateF.format(tbVoucherDate.get(a).getValue())+"'," +
							"'"+tbVoucherNo.get(a).getValue()+"'," +
							"'"+tbVoucherUserName.get(a).getValue()+"')";
					System.out.println("Insert : "+sqlDetails);
					session.createSQLQuery(sqlDetails).executeUpdate();
					
					
				}
			}
			String lcUpdate="update tbLcOpeningInfo set isActive=0 where vLedgerID='"+cmbLcNo.getValue()+"'";
			session.createSQLQuery(lcUpdate).executeUpdate();
			System.out.println(lcUpdate);
			
			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			componentIni(true);
			btnIni(true);
		}
		catch(Exception exp){
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void lcDetailsLoad(){
		String sql="select a.Date,a.MrrNo,b.ProductId,(select vRawItemName from tbRawItemInfo  "+
				" where vRawItemCode=b.ProductID)productName,b.Qty,b.Rate,(b.Qty*b.Rate)Amount,  "+
				" b.userName,a.VoucherNo,case when a.VoucherNo not like ''then (select distinct Date from  "+
				" Voucher2 where Voucher_No=a.VoucherNo) else '' end VoucherDate,case when a.VoucherNo not like ''  "+
				" then(select distinct name from tbLogin where userId=(select distinct userId from Voucher2   "+
				" where Voucher_No=a.VoucherNo))else '' end voucherUserName from tbRawPurchaseInfo a  "+
				" inner join tbRawPurchaseDetails b on a.ReceiptNo=b.ReceiptNo where SupplierId='"+cmbLcNo.getValue()+"'";
		Iterator<?>iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbReceiveDate.get(a).setReadOnly(false);
			tbReceiveDate.get(a).setValue(element[0]);
			tbReceiveDate.get(a).setReadOnly(true);
			tbMrrNo.get(a).setValue(element[1]);
			tbProductId.get(a).setValue(element[2]);
			tbProductName.get(a).setValue(element[3]);
			tbReceivedQty.get(a).setValue(df.format(element[4]));
			tbReceivedRate.get(a).setValue(df.format(element[5]));
			tbReceivedAmount.get(a).setValue(df.format(element[6]));
			tbReceiveUserName.get(a).setValue(element[7]);
			
			tbVoucherNo.get(a).setValue(element[8]);
			if(!element[8].toString().isEmpty()){
				tbVoucherDate.get(a).setReadOnly(false);
				tbVoucherDate.get(a).setValue(element[9]);
				tbVoucherDate.get(a).setReadOnly(true);
			}
			
			tbVoucherUserName.get(a).setValue(element[10]);
			a++;
			if(a==tbMrrNo.size()){
				tableRowAdd(a-1);
			}
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
	private void authencationCheck()
	{
		if(sessionBean.isSubmitable())
		{
			cButton.btnSave.setVisible(true);
		}
		else
		{
			cButton.btnSave.setVisible(false);
		}
		if(sessionBean.isUpdateable())
		{
			cButton.btnEdit.setVisible(true);
		}
		else
		{
			cButton.btnEdit.setVisible(false);
		}
		if(sessionBean.isDeleteable())
		{
			cButton.btnDelete.setVisible(true);
		}
		else
		{
			cButton.btnDelete.setVisible(false);
		}
	}
	private void tableInitialize(){
		for(int a=0;a<8;a++){
			tableRowAdd(a);
		}
	}
	private void tableRowAdd(final int ar)
	{
		tbSl.add(ar, new Label(""));
		tbSl.get(ar).setWidth("100%");
		tbSl.get(ar).setValue(ar+1);

		tbReceiveDate.add(ar,new PopupDateField());
		tbReceiveDate.get(ar).setWidth("100%");
		tbReceiveDate.get(ar).setImmediate(true);
		tbReceiveDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbReceiveDate.get(ar).setDateFormat("dd-MM-yyyy");
		//tbReceiveDate.get(ar).setValue(new java.util.Date());
		tbReceiveDate.get(ar).setReadOnly(true);

		tbMrrNo.add(ar, new Label());
		tbMrrNo.get(ar).setWidth("100%");

		tbProductId.add(ar, new Label());
		tbProductId.get(ar).setWidth("100%");

		tbProductName.add(ar, new Label());
		tbProductName.get(ar).setWidth("100%");

		tbReceivedQty.add(ar, new TextRead(1));
		tbReceivedQty.get(ar).setWidth("100%");
		tbReceivedQty.get(ar).setImmediate(true);

		tbReceivedRate.add(ar, new TextRead(1));
		tbReceivedRate.get(ar).setWidth("100%");
		tbReceivedRate.get(ar).setImmediate(true);

		tbReceivedAmount.add(ar, new TextRead(1));
		tbReceivedAmount.get(ar).setWidth("100%");
		tbReceivedAmount.get(ar).setImmediate(true);

		tbReceiveUserName.add(ar, new Label());
		tbReceiveUserName.get(ar).setWidth("100%");

		tbVoucherDate.add(ar,new PopupDateField());
		tbVoucherDate.get(ar).setWidth("100%");
		tbVoucherDate.get(ar).setImmediate(true);
		tbVoucherDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbVoucherDate.get(ar).setDateFormat("dd-MM-yyyy");
		//tbVoucherDate.get(ar).setValue(new java.util.Date());
		tbVoucherDate.get(ar).setReadOnly(true);

		tbVoucherNo.add(ar, new Label());
		tbVoucherNo.get(ar).setWidth("100%");
		
		String concatevariable="";
	    StringTokenizer st=new StringTokenizer(",",concatevariable);
	    int counttoken=st.countTokens();
	    while(counttoken>0)
	    {
	    	concatevariable=concatevariable+st.nextToken();
	    	System.out.print("\n");
	    	counttoken--;
	    	
	    }
	    
	    int a[]={12,45,56,67,78};
	    
	    for(int x:a)
	    {
	    	System.out.println("value of a is:"+x);
	    }
	    
	    

		tbVoucherUserName.add(ar, new Label());
		tbVoucherUserName.get(ar).setWidth("100%");

		table.addItem(new Object[]{tbSl.get(ar),tbReceiveDate.get(ar),tbMrrNo.get(ar),tbProductId.get(ar),tbProductName.get(ar),
				tbReceivedQty.get(ar),tbReceivedRate.get(ar),tbReceivedAmount.get(ar),tbReceiveUserName.get(ar),tbVoucherDate.get(ar),
				tbVoucherNo.get(ar),tbVoucherUserName.get(ar)},ar);
	}
	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("1180px");
		setHeight("540px");

		cmbLcNo= new ComboBox();
		cmbLcNo.setImmediate(true);
		cmbLcNo.setWidth("210px");
		cmbLcNo.setHeight("24px");
		cmbLcNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("L/C No :"),"top:20px;left:20px;");
		mainLayout.addComponent(cmbLcNo, "top:18.0px;left:120.0px;");

		dOpenDate = new PopupDateField();
		dOpenDate.setWidth("120px");
		dOpenDate.setHeight("-1px");
		dOpenDate.setImmediate(true);
		dOpenDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dOpenDate.setDateFormat("dd-MM-yyyy");
		dOpenDate.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Open Date : "),"top:45px;left:20px;");
		mainLayout.addComponent(dOpenDate, "top:43.0px;left:120.0px;");

		cmbStatus= new ComboBox();
		cmbStatus.setImmediate(true);
		cmbStatus.setWidth("210px");
		cmbStatus.setHeight("24px");
		cmbStatus.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Status :"),"top:70px;left:20px;");
		mainLayout.addComponent(cmbStatus, "top:68.0px;left:120.0px;");

		txtLedgerBalance= new TextRead(1);
		txtLedgerBalance.setImmediate(true);
		txtLedgerBalance.setWidth("120px");
		txtLedgerBalance.setHeight("24px");
		mainLayout.addComponent(new Label("Ledger Balance :"),"top:20px;left:360px;");
		mainLayout.addComponent(txtLedgerBalance, "top:18.0px;left:460.0px;");

		txtLCValueUSD= new TextRead(1);
		txtLCValueUSD.setImmediate(true);
		txtLCValueUSD.setWidth("120px");
		txtLCValueUSD.setHeight("24px");
		mainLayout.addComponent(new Label("L/C Value USD :"),"top:45px;left:360px;");
		mainLayout.addComponent(txtLCValueUSD, "top:43.0px;left:460.0px;");

		dCurDate = new PopupDateField();
		dCurDate.setWidth("120px");
		dCurDate.setHeight("-1px");
		dCurDate.setImmediate(true);
		dCurDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dCurDate.setDateFormat("dd-MM-yyyy");
		dCurDate.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Date : "),"top:20px;left:610px;");
		mainLayout.addComponent(dCurDate, "top:18.0px;left:710.0px;");

		txtTransactionNo= new TextRead(1);
		txtTransactionNo.setImmediate(true);
		txtTransactionNo.setWidth("120px");
		txtTransactionNo.setHeight("24px");
		mainLayout.addComponent(new Label("Transaction No :"),"top:45px;left:610px;");
		mainLayout.addComponent(txtTransactionNo,  "top:43.0px;left:710.0px;");

		table.setFooterVisible(true);
		table.setWidth("100%");
		table.setHeight("280px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 15);

		table.addContainerProperty("Receive Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Receive Date", 110);

		table.addContainerProperty("MRR No", Label.class, new Label());
		table.setColumnWidth("MRR No", 70);

		table.addContainerProperty("Product ID", Label.class, new Label());
		table.setColumnWidth("Product ID", 30);
		table.setColumnCollapsed("Product ID", true);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name", 240);

		table.addContainerProperty("Qty", TextRead.class, new TextRead(1));
		table.setColumnWidth("Qty", 60);

		table.addContainerProperty("Rate", TextRead.class, new TextRead(1));
		table.setColumnWidth("Rate", 60);

		table.addContainerProperty("Amount", TextRead.class, new TextRead(1));
		table.setColumnWidth("Amount", 90);

		table.addContainerProperty("Received UserName", Label.class, new Label());
		table.setColumnWidth("Received UserName", 80);

		table.addContainerProperty("Voucher Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Voucher Date", 110);

		table.addContainerProperty("Voucher No", Label.class, new Label());
		table.setColumnWidth("Voucher No", 80);

		table.addContainerProperty("Voucher UserName", Label.class, new Label());
		table.setColumnWidth("Voucher UserName", 100);

		mainLayout.addComponent(table,"top:100.0px;left:0.0px;");
		tableInitialize();

		Label lblLine = new Label("<b><font color='#e65100'>==============================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:400px;left:10px;");
		mainLayout.addComponent(cButton, "top:440px;left:350px;");

		return mainLayout;
	}

}
