package com.example.rawMaterialTransaction;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.poi.hssf.util.HSSFColor.AQUA;
import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.setupTransaction.DepartmentInformation;
import acc.appform.setupTransaction.SectionInformation;

import com.vaadin.data.Property.*;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;

public class OpeningRateEdit extends Window{

	SessionBean sessionBean=new SessionBean();
	AbsoluteLayout mainLayout;
	private DecimalFormat df = new DecimalFormat("#0.00");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private NativeButton btnSubmit,btnFind;
	private Label lbLine=new Label("<b><font color='#e65100'>===========================================================================================================================</font></b>", Label.CONTENT_XHTML);
	private String ProductLedeger="";
	private CommonButton cButton = new CommonButton( "New",  "Save",  "",  "",  "Refresh",  "", "", "","","Exit");
	private Label lblOpeningYear;
	private InlineDateField dOpeningYear;
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<Label> tbLblCategoryName = new ArrayList<Label>();
	private ArrayList<Label> tbLblItemName = new ArrayList<Label>();
	private ArrayList<Label> tbUnit = new ArrayList<Label>();
	private ArrayList<Label> tbStoreName = new ArrayList<Label>();
	private ArrayList<TextRead> tbStockQty = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> tbRate = new ArrayList<AmountField>(1);
	private ArrayList<AmountField> tbAmount = new ArrayList<AmountField>(1);
	private ArrayList<Label> tbLblCategoryCode = new ArrayList<Label>();
	private ArrayList<Label> tbLblItemCode = new ArrayList<Label>();
	private ArrayList<Label> tbLblStoreId = new ArrayList<Label>();
	private ArrayList<CheckBox>tbChkShow=new ArrayList<CheckBox>();
	Table table=new Table();
	private boolean isUpdate=false;
	private NativeButton nbDepartment,nbSection;
	private NativeButton nbJobInfo;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
	Label lblCategoryType,lblCategory,lblSubSubCategory,lblSubCategory,
	lblUnit,lblRate,lblStockQty,lblAmountlblStoreName;
	int j;
	ComboBox cmbSubCategory, cmbSubSubCategory,cmbCategoryType,cmbCategoryName,cmbStoreName;

	public OpeningRateEdit(SessionBean sessionBean){
		this.sessionBean=sessionBean;
		this.setCaption("OPENING RATE EDIT::"+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		setEventAction();
		focusEnter();
		authencationCheck();
		tableClear();
	}

	private void authencationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			cButton.btnSave.setVisible(false);
		}
	}

	private void focusEnter(){

		ArrayList<Component> allComp = new ArrayList<Component>();


		allComp.add(cmbCategoryType);
		allComp.add(cmbCategoryName);
		allComp.add(cmbSubCategory);
		allComp.add(cmbStoreName);
		allComp.add(btnSubmit);
		for(int i=0;i<tbRate.size();i++)
		{
			allComp.add(tbRate.get(i));
		}
		allComp.add(cButton);
		new FocusMoveByEnter(this,allComp);
	}
	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		infoClear();
		tableClear();
	}

	private void setEventAction(){
		cButton.btnNew.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				newButtonEvent();
			}
		});
		cButton.btnSave.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(saveCheckValidation()){
					saveButtonEvent();
				}
			}
		});
		cButton.btnRefresh.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}

		});
		
		cButton.btnExit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		cmbCategoryType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbCategoryType.getValue()!=null){
					cmbCategoryLoad();
					tableClear();
				}
				else{
					cmbCategoryName.removeAllItems();
				}
			}
		});
		cmbCategoryName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbCategoryName.getValue()!=null)
				{
					cmbSubCategoryLoad();
					tableClear();
				}
				else{
					cmbSubCategory.removeAllItems();
				}
			}
		});
		cmbSubCategory.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbSubCategory.getValue()!=null){
					cmbSubSubCategoryLoad();
					tableClear();
				}
				else{
					cmbSubSubCategory.removeAllItems();
				}
			}
		});
		
		cmbSubSubCategory.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbSubSubCategory.getValue()!=null)
				{
					tableDataAdd();
					tableClear();
				}
				else
				{
					//cmbProductCode.removeAllItems();
				}
			}
		});
		
		btnSubmit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				subMitButtonEvent();
			}
		});
	
	/*	btnFind.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				findTableDataLoad(0);
			}
		});*/
		nbDepartment.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				System.out.println("Group Form");
				departmentLink();				
			}
		});
		nbSection.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				System.out.println("Group Form");
				sectionLink();				
			}
		});
		cButton.btnPreview.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(cmbCategoryName.getValue()!=null){
					reportShow( );
				}
				else{
					showNotification("Nothing to Preview",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
	}
	public void departmentLink() 
	{
		Window win = new DepartmentInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbCategoryTypeDataLoad();
				System.out.println("Group Form");
			}
		});

		this.getParent().addWindow(win);

	}
	public void sectionLink() 
	{
		Window win = new SectionInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbCategoryTypeDataLoad();
				System.out.println("Group Form");
			}
		});

		this.getParent().addWindow(win);

	}
	private void infoClear(){
		cmbSubSubCategory.setValue("");
		cmbCategoryType.setValue(null);
		cmbCategoryName.setValue(null);
		cmbSubCategory.setValue("");
	}
	private void tableClear(){
		for(int a=0;a<tbLblItemCode.size();a++){
			tableRowClear(a);
		}
	}
	private void saveButtonEvent(){
	
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update product information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					
					if(buttonType == ButtonType.YES)
					{	   mb.buttonLayout.getComponent(0).setEnabled(false);
							insertData();
							reportShow();
							infoClear();
							tableClear();
							cmbCategoryType.focus();
							mb.close();
					}
				}
			});	
		
		/*else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						infoClear();
						detailsClear();
						tableClear();
						autoTransactionNo();
						cmbJobName.focus();
						isFind=false;
						isUpdate=false;
						findTableDataLoad(0);
					}
				}
			});		
		}*/
	}
	private boolean deleteData(String product){
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="delete from tbRawOpeningRateEdit where productId = '"+product+"'";			
			session.createSQLQuery(sql).executeUpdate();
			
				//tx.commit();
				return true;
			}
			
		catch(Exception exp)
		{
			
			tx.rollback();
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		
		return false;
	}
	private void insertData()
	{
		Session session=null;
		 session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=null;
		try
		{
			tx=session.beginTransaction();
/*	   WebApplicationContext context = ((WebApplicationContext) getApplication().getContext());
			WebBrowser webBrowser = context.getBrowser();
			sessionBean.setUserIp(webBrowser.getAddress());    */
			
			//InetAddress inetAddress =InetAddress.getByName(webBrowser.getAddress().toString()); //get the host Inet using ip
			
			for (int i=0,a=0;i<=tbLblItemCode.size();i++){
				if(!tbLblItemCode.get(a).getValue().toString().isEmpty()){
				String product = tbLblItemCode.get(i).getValue().toString();	
			String updateQuery = "Update tbRawProductDetails set CreationYear = '"+Calendar.getInstance().get(Calendar.YEAR)+"',vUserName = '"+sessionBean.getUserName()+"',vUserIP = '"+sessionBean.getUserIp()+"',dEntryTime = CURRENT_TIMESTAMP  WHERE ProductID = '"+tbLblItemCode.get(a).getValue().toString()+"'";
			//System.out.println("updateQuery :"+updateQuery);
			session.createSQLQuery(updateQuery).executeUpdate();

			String updateQOS = "Update tbRawProductOpening set Qty = '"+tbStockQty.get(a).getValue().toString().trim()+"', " +
					"Rate = '"+(tbRate.get(a).getValue().toString().equals("") ?"0.00":tbRate.get(a).getValue().toString().trim())+"',vUserName = '"+sessionBean.getUserName()+"', " +
					"vUserIP = '"+sessionBean.getUserIp()+"'," +
					"dtEntryTime = CURRENT_TIMESTAMP, amount='"+tbAmount.get(a).getValue().toString().trim()+"',  " +
					"openingYear =  '"+dateFormat.format(dOpeningYear.getValue())+"'  WHERE ProductID = '"+tbLblItemCode.get(a).getValue()+"' ";

			//System.out.println("updateQOS :"+updateQOS);
			session.createSQLQuery(updateQOS).executeUpdate();
			
			String ProductLedeger=productlededger( i) ;

			String LedgerOpen=" update tbLedger_Op_Balance set  DrAmount='"+tbAmount.get(a).getValue()+"',CrAmount='0.00' ,userId='"+sessionBean.getUserId()+"' ,userIp='"+sessionBean.getUserIp()+"',entryTime=getdate() where Ledger_Id like '"+ProductLedeger+"' ";
			//System.out.println("LedgerOpen : "+LedgerOpen);
			session.createSQLQuery(LedgerOpen).executeUpdate();
			a++;
			if(tbChkShow.get(i).booleanValue()){		
				//if(isUpdate){
					if(deleteData(product)){
						
			String insertProductOpening = "Insert Into tbRawOpeningRateEdit(ProductId, Qty, Rate,vUserName,vUserIP,dtEntryTime,amount,openingYear) " +
					" values('"+tbLblItemCode.get(i).getValue().toString()+"','"+tbAmount.get(i).getValue().toString().trim()+"'," +
					" '"+(tbRate.get(i).getValue().toString().equals("")? "0.00" : tbRate.get(i).getValue().toString().trim())+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'"+(tbAmount.get(i).getValue().toString().equals("") ? "0.00":tbAmount.get(i).getValue().toString().trim())+"'," +
					" '"+dateFormat.format(dOpeningYear.getValue())+"')";

			//System.out.println("insertProductInfo : "+insertProductOpening);
			session.createSQLQuery(insertProductOpening).executeUpdate();	
							}		
						}
				//isUpdate=false;		
					//}
		
				}
			}
			tx.commit();
			this.getParent().showNotification("All information updated successfully.");			
		}		
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		/*finally{			
			if(tx!=null){
				tx.commit();
				session.close();
				this.getParent().showNotification("All information save successfully.");
			}
		}*/
	}
	public String productlededger(int index) 
	{
		String autoCode = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query="select Ledger_Id  from tbLedger where Ledger_Id=(select vLedgerCode from tbRawItemInfo where vRawItemCode like '"+tbLblItemCode.get(index).getValue().toString()+"')";
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
	private boolean saveCheckValidation(){
		if(cmbCategoryType.getValue()!=null){
			if(cmbCategoryName.getValue()!=null){
			
							return true;
					}
					else{
						showNotification("Please provide category name",Notification.TYPE_WARNING_MESSAGE);
						cmbCategoryName.focus();
					}
				}
				else{
					showNotification("Please provide category type",Notification.TYPE_WARNING_MESSAGE);
					cmbCategoryType.focus();
				}
			
		
		return false;
	}
	private void subMitButtonEvent(){

		if(checkValidation()){
				tableRowAddSubmit();
				tbChkShow.get(j).setValue(false);
				tbRate.get(0).focus();
		}
	}
	
	private boolean checkValidation(){
	if(cmbCategoryType.getValue()!=null){
		if(cmbCategoryName.getValue()!=null){
			//if(cmbSubCategory.getValue()!=null){									
				return true;							
			/*}
			else{
				showNotification("Warning!","Please select subcategory name",Notification.TYPE_WARNING_MESSAGE);
				cmbSubCategory.focus();
			}*/
		}
		else{
			showNotification("Warning!","Please select category name",Notification.TYPE_WARNING_MESSAGE);
			cmbCategoryName.focus();
		}
		}
		else{
			showNotification("Warning!","Please select category type",Notification.TYPE_WARNING_MESSAGE);
			cmbCategoryType.focus();
		}
		return false;
	}
	
	private void tableRowAddSubmit()
	{
		try
		{
	//int a=findBlankRow();
		tbChkShow.get(j).setValue(false);
		tableDataAdd();
		/*if(a==tbLblItemCode.size()-1){
			tableRowAdd(a+1);
		}*/
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void tableRowClear(int ar){
		tbLblItemCode.get(ar).setValue("");
		tbLblItemName.get(ar).setValue("");
		tbLblStoreId.get(ar).setValue("");
		tbStoreName.get(ar).setValue("");
		tbAmount.get(ar).setValue("");
		tbUnit.get(ar).setValue("");
		tbStockQty.get(ar).setValue("");
		tbRate.get(ar).setValue("");
		tbAmount.get(ar).setValue("");
		tbChkShow.get(ar).setValue(false);
	}

	private int findBlankRow(){
		for(int a=0;a<tbRate.size();a++){
			if(tbLblItemCode.get(a).getValue().toString().isEmpty()){
				return a;
			}
		}
		return 0;
	}
	private void amountCalc(int a)
	{
		double qty=Double.parseDouble( tbStockQty.get(a).getValue().toString().isEmpty()?"0.0":tbStockQty.get(a).getValue().toString());
		double rate=Double.parseDouble(tbRate.get(a).getValue().toString().isEmpty()?"0.0":tbRate.get(a).getValue().toString());
		double amount=qty*rate;
		tbAmount.get(a).setValue(df.format(amount));	
		tbChkShow.get(a).setValue(true);
		isUpdate=true;
	}
	private void tableDataAdd(){

		String subcategoryId ;
		String subsubcategoryId;
		if(cmbSubCategory.getValue()!=null)
		{
			subcategoryId=cmbSubCategory.getValue().toString();
		}
		else
		{
			subcategoryId= "%";
		}
		
		if(cmbSubSubCategory.getValue()!=null)
		{
			subsubcategoryId=cmbSubSubCategory.getValue().toString();
		}
		else
		{
			subsubcategoryId= "%";
		}
		Iterator<?> iter=dbService ("select a.productId,b.vRawItemName,b.vUnitName,c.vDepoId,c.vDepoName,a.qty,a.rate,a.amount from tbRawProductOpening a " +
				"inner join tbRawItemInfo b on a.productId=b.vRawItemCode " +
				"inner join  tbRawProductDetails  c  " +
				"on a.productId = c.productId where  YEAR(openingYear) like '"+dateFormat.format(dOpeningYear.getValue())+"' "+
				"and b.vCategoryType like '"+cmbCategoryType.getValue().toString()+"'  and  b.vGroupId like '"+cmbCategoryName.getValue().toString()+"'  "+
				"and  b.vSubGroupId like '"+subcategoryId+"' and  b.vsubsubCategoryId like  '"+subsubcategoryId+"' order by b.vRawItemName " ) ;
		System.out.println(iter);
		int a=0;

		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbLblItemCode.get(a).setValue(element[0]);
			tbLblItemName.get(a).setValue(element[1]);
			tbUnit.get(a).setValue(element[2]);
			tbLblStoreId.get(a).setValue(element[3]);
			tbStoreName.get(a).setValue(element[4]);
			tbStockQty.get(a).setValue(df.format(element[5]));
			tbRate.get(a).setValue(df.format(element[6]));
			tbAmount.get(a).setValue(df.format(element[7]));
			tbRate.get(a).focus();
			tbChkShow.get(a).setValue(false);
			if(a==tbRate.size()-1)
			{
				tableRowAdd(a+1);
				tbChkShow.get(a).setValue(false);
			}
			a++;		
		}
		if(!iter.hasNext()&& a==0){
			showNotification("There are no data.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void cmbSubSubCategoryLoad(){
		cmbSubSubCategory.removeAllItems();
		Iterator<?>iter=dbService("select b.vsubsubCategoryId,b.vSubSubCategoryName  "
				+ "from tbRawProductOpening a inner join tbRawItemInfo b "
				+ "on a.productId=b.vRawItemCode where b.vSubGroupId like '"+cmbSubCategory.getValue().toString()+"' ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbSubSubCategory.addItem(element[0]);
			cmbSubSubCategory.setItemCaption(element[0], element[1].toString());
		}
	}
	private void cmbSubCategoryLoad(){
		cmbSubCategory.removeAllItems();
		Iterator<?>iter=dbService("select b.vSubGroupId,b.vSubGroupName from tbRawProductOpening a inner join tbRawItemInfo b on a.productId=b.vRawItemCode where b.vGroupId  like '"+cmbCategoryName.getValue().toString()+"' ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbSubCategory.addItem(element[0]);
			cmbSubCategory.setItemCaption(element[0], element[1].toString());
		}
	}
	
	/*private void cmbCategoryNameDataLoad(){
		cmbCategoryName.removeAllItems();
		Iterator<?>iter=dbService("select vSectionID,SectionName from tbSectionInfo where vDepartmentID like '"+cmbCategoryType.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbCategoryName.addItem(element[0]);
			cmbCategoryName.setItemCaption(element[0], element[1].toString());
		}
	}*/
	private void autoTransactionNo(){
		Iterator<?>iter=dbService("select isnull(MAX(TransactionNo),0)+1 from tbO");
		if(iter.hasNext()){
			cmbSubSubCategory.setValue(iter.next());
		}
	}
	private void cmbCategoryLoad(){
		cmbCategoryName.removeAllItems();
		Iterator<?>iter=dbService("select b.vGroupId,b.vGroupName from tbRawProductOpening a "
				+ "inner join tbRawItemInfo b on a.productId=b.vRawItemCode "
				+ "where b.vCategoryType like '"+cmbCategoryType.getValue().toString()+"' ");

		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbCategoryName.addItem(element[0]);
			cmbCategoryName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void newButtonEvent() 
	{
		infoClear();
		componentIni(false);
		btnIni(false);
		//autoTransactionNo();
		cmbCategoryType.focus();
		cmbCategoryTypeDataLoad();
	}
	private void componentIni(boolean t){
		cmbCategoryType.setEnabled(!t);
		cmbCategoryName.setEnabled(!t);
		cmbSubCategory.setEnabled(!t);
		cmbSubSubCategory.setEnabled(!t);
		table.setEnabled(!t);
		btnSubmit.setEnabled(!t);
	}

	private void cmbCategoryTypeDataLoad(){
		cmbCategoryType.removeAllItems();
		Iterator<?>iter=dbService("select distinct 0,vCategoryType  from tbRawItemCategory");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbCategoryType.addItem(element[1]);
			cmbCategoryType.setItemCaption(element[1], element[1].toString());
		}
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
	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnPreview.setEnabled(!t);
	}
	private AbsoluteLayout buildMainLayout(){
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("910px");
		mainLayout.setHeight("610px");
		mainLayout.setMargin(false);

		lblOpeningYear = new Label();
		lblOpeningYear.setImmediate(false);
		lblOpeningYear.setWidth("-1px");
		lblOpeningYear.setHeight("-1px");
		lblOpeningYear.setValue("Opening Year :");

		dOpeningYear = new InlineDateField();
		dOpeningYear.setImmediate(true);
		dOpeningYear.setDateFormat("yyyy");
		dOpeningYear.setWidth("-1px");
		dOpeningYear.setHeight("-1px");
		dOpeningYear.setInvalidAllowed(false);
		dOpeningYear.setResolution(6);

		lblCategoryType=new Label();
		lblCategoryType.setWidth("-1px");
		lblCategoryType.setValue("Category Type :");
		lblCategoryType.setHeight("-1px");
		lblCategoryType.setImmediate(false);

		cmbCategoryType=new ComboBox();
		cmbCategoryType.setWidth("240px");
		//cmbCategoryType.setNewItemsAllowed(true);
		cmbCategoryType.setNullSelectionAllowed(true);
		cmbCategoryType.setImmediate(true);
		cmbCategoryType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		nbDepartment = new NativeButton();
		nbDepartment.setIcon(new ThemeResource("../icons/add.png"));
		nbDepartment.setImmediate(true);
		nbDepartment.setWidth("32px");
		nbDepartment.setHeight("21px");

		lblCategory=new Label();
		lblCategory.setWidth("-1px");
		lblCategory.setValue("Category :");
		lblCategory.setHeight("-1px");
		lblCategory.setImmediate(false);

		cmbCategoryName=new ComboBox();
		cmbCategoryName.setWidth("240px");
		//cmbCategoryName.setNewItemsAllowed(true);
		cmbCategoryName.setNullSelectionAllowed(true);
		cmbCategoryName.setImmediate(true);
		cmbCategoryName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		nbSection = new NativeButton();
		nbSection.setIcon(new ThemeResource("../icons/add.png"));
		nbSection.setImmediate(true);
		nbSection.setWidth("32px");
		nbSection.setHeight("21px");

		lblSubCategory=new Label();
		lblSubCategory.setWidth("-1px");
		lblSubCategory.setValue("Sub Category :");
		lblSubCategory.setHeight("-1px");
		lblSubCategory.setImmediate(false);
		
		cmbSubCategory=new ComboBox();
		cmbSubCategory.setWidth("240px");
		//cmbCategoryName.setNewItemsAllowed(true);
		cmbSubCategory.setNullSelectionAllowed(true);
		cmbSubCategory.setImmediate(true);
		cmbSubCategory.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblSubSubCategory=new Label();
		lblSubSubCategory.setWidth("-1px");
		lblSubSubCategory.setValue("Sub sub Category:");
		lblSubSubCategory.setHeight("-1px");
		lblSubSubCategory.setImmediate(false);
		
		cmbSubSubCategory=new ComboBox();
		cmbSubSubCategory.setWidth("240px");
		//cmbCategoryName.setNewItemsAllowed(true);
		cmbSubSubCategory.setNullSelectionAllowed(true);
		cmbSubSubCategory.setImmediate(true);
		cmbSubSubCategory.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		table.setWidth("99%");
		table.setHeight("385px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",15);

		table.addContainerProperty("Edit", CheckBox.class, new CheckBox());
		table.setColumnWidth("Edit", 20);
		table.setColumnCollapsed("Edit", true);
		
		table.addContainerProperty("ItemName", Label.class , new Label());
		table.setColumnWidth("Item Name",180);

		table.addContainerProperty("Unit", Label.class , new Label());
		table.setColumnWidth("Unit",40);
		
		table.addContainerProperty("Store Name", Label.class , new Label());
		table.setColumnWidth("Store Name",90);

		table.addContainerProperty("Stock Qty", TextRead.class , new TextRead(1));
		table.setColumnWidth("Stock Qty",90);

		table.addContainerProperty("Rate", AmountField.class , new AmountField());
		table.setColumnWidth("Rate",90);

		table.addContainerProperty("Amount", AmountField.class , new AmountField());
		table.setColumnWidth("Amount",110);

		table.addContainerProperty("Item Code", Label.class , new Label());
		table.setColumnWidth("Item Code",80);
		table.setColumnCollapsed("Item Code", true);

		table.addContainerProperty("Store Id", Label.class , new Label());
		table.setColumnWidth("Store Id",80);
		table.setColumnCollapsed("Store Id", true);

		tableInitialise();

		btnSubmit= new NativeButton("FIND");
		btnSubmit.setImmediate(false);
		btnSubmit.setIcon(new ThemeResource("../icons/update1.png"));
		btnSubmit.setImmediate(true);
		btnSubmit.setWidth("108px");
		btnSubmit.setHeight("35px");
		
		mainLayout.addComponent(lblOpeningYear,"top:10px;left:10px;");
		mainLayout.addComponent(dOpeningYear,"top:8px;left:140px;");
		mainLayout.addComponent(lblCategoryType,"top:35px;left:10px;");
		mainLayout.addComponent(cmbCategoryType,"top:33px;left:140px;");
		mainLayout.addComponent(lblCategory,"top:60px;left:10px;");
		mainLayout.addComponent(cmbCategoryName,"top:58px;left:140px;");
		//mainLayout.addComponent(nbSection,"top:60.0px;left:290.0px;");
		mainLayout.addComponent(lblSubCategory,"top:85px;left:10px;");
		mainLayout.addComponent(cmbSubCategory,"top:83px;left:140px;");
		mainLayout.addComponent(lblSubSubCategory,"top:110px;left:10px;");
		mainLayout.addComponent(cmbSubSubCategory,"top:108px;left:140px;");

		mainLayout.addComponent(btnSubmit,"top:90px;left:400px;");

		mainLayout.addComponent(table,"top:150px;left:10px;");
		mainLayout.addComponent(lbLine,"top:540px;left:5px;");
		mainLayout.addComponent(cButton,"top:570px;left:240px;");

		return mainLayout;
	}

	private void tableRowAdd(final int ar)
	{
		lblSl.add(ar, new Label());
		lblSl.get(ar).setValue(ar+1);
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setHeight("-1px");
		
		tbChkShow.add(ar, new CheckBox());
		tbChkShow.get(ar).setImmediate(true);
		
		
	/*	tbChkShow.add(ar, new CheckBox());
		tbChkShow.get(ar).setImmediate(true);
		
		if(isUpdate==true)
		{
			tbChkShow.get(ar).setValue(true);
		}*/
		
	/*	tbChkShow.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!tblbltransactionNo.get(ar).getValue().toString().isEmpty()){
					if(tbChkShow.get(ar).booleanValue()){
						isFind=true;
						isUpdate=true;
						cmbGroup.setValue(tblblGroupCode.get(ar).getValue());
						cmbItem.setValue(tblblItemCode.get(ar).getValue());
						cmbProductCode.addItem(tblblProductCode.get(ar).getValue());
						cmbProductCode.setValue(tblblProductCode.get(ar).getValue());
						txtSpecification.setValue(tblblSpecification.get(ar).getValue());
						lblSpecCode.setValue(tblblSpecificationCode.get(ar).getValue());
						txtUnit.setValue(tblblUnit.get(ar).getValue());
						txtSubItemName.setValue(tblblSubItem.get(ar).getValue());
						aQty.setValue(tblblQty.get(ar).getValue());
						aRate.setValue(tblblRate.get(ar).getValue());
						cmbStore.setValue(tblblStoreId.get(ar).getValue());
						cmbRack.setValue(tblblRackId.get(ar).getValue());
						cmbShelf.setValue(tblblShelfId.get(ar).getValue());
						txtTransactionNo.setValue(tblbltransactionNo.get(ar).getValue());
						tableRowClear(ar);

					}
				}
				else{
					showNotification("Nothing To Show",Notification.TYPE_WARNING_MESSAGE);
					tbChkShow.get(ar).setValue(false);
				}
			}
		});
*/

		tbLblItemName.add(ar,new Label());
		tbLblItemName.get(ar).setWidth("100%");
		tbLblItemName.get(ar).setImmediate(true);

		tbUnit.add( ar , new Label());
		tbUnit.get(ar).setWidth("100%");

		tbStoreName.add( ar , new Label());
		tbStoreName.get(ar).setWidth("100%");

		tbStockQty.add( ar , new TextRead(1));
		tbStockQty.get(ar).setWidth("100%");

		tbRate.add( ar , new AmountField());
		tbRate.get(ar).setWidth("100%");
		
		tbRate.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{if(tbRate.get(ar).getValue()!=null)
				amountCalc(ar)	;		
			}
		});


		tbAmount.add( ar , new AmountField());
		tbAmount.get(ar).setWidth("100%");

		tbLblItemCode.add( ar , new Label());
		tbLblItemCode.get(ar).setWidth("100%");
		tbLblItemCode.get(ar).setImmediate(true);

		tbLblStoreId.add( ar , new Label());
		tbLblStoreId.get(ar).setWidth("100%");
		tbLblStoreId.get(ar).setImmediate(true);


		table.addItem(new Object[]{lblSl.get(ar),tbChkShow.get(ar),tbLblItemName.get(ar),
				tbUnit.get(ar),tbStoreName.get(ar),tbStockQty.get(ar), tbRate.get(ar), tbAmount.get(ar),
				tbLblItemCode.get(ar),tbLblStoreId.get(ar)},ar);
	}
	public void tableInitialise()
	{

		for(int i=0;i<15;i++)
		{
			tableRowAdd(i);
		}
	}
	private void reportShow()
	{

		System.out.println("into Reportshow");
		String query=null;
		String activeFlag = null;
		String subcategoryID="";
		String categoryID = cmbCategoryName.getValue().toString();
		if(cmbSubCategory.getValue()!=null)
		{
			subcategoryID=cmbSubCategory.getValue().toString();	
		}
		else
		{
			subcategoryID="%";	
		}
		
		String categirytype="";

		if(categoryID=="All")
		{
			categoryID="%";
		}

		if(subcategoryID=="All")
		{
			subcategoryID="%";
		}
	

		try{

			Transaction tx =null;
			Session session=null;
			 session = SessionFactoryUtil.getInstance().getCurrentSession();
			 tx = session.beginTransaction();



			HashMap<Object,Object> hm = new HashMap<Object,Object>();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("openyear", datef.format(dOpeningYear.getValue()));
			
			query= " select * from tbRawProductOpening a "
					+"inner join "
					+"tbRawItemInfo b "
					+"on a.productId=b.vRawItemCode "
					+"where  b.vCategoryType like '"+cmbCategoryType.getValue()+"'   and b.vGroupId like '"+categoryID+"' and b.vSubGroupId like '"+subcategoryID+"'  and  DATEPART(YEAR,a.openingYear) like '"+dateFormat.format(dOpeningYear.getValue())+"' order by cast(substring(b.vGroupId,2,LEN(b.vGroupId)) as int),cast(substring(b.vsubsubCategoryId,2,LEN(b.vsubsubCategoryId)) as int), b.vGroupId ,b.vSubGroupId " ;

			System.out.println(query);
			hm.put("sql", query);
			System.out.println("123");
			List<?> list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/raw/rptOpeningStock.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
		
		}
		else{
			this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
		}
		}
		catch(Exception exp){

			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
