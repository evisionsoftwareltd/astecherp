package com.example.CostingTransaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

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
public class ConversionCostHeadWiseLedgerMapping extends Window
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblHeadName;

	ComboBox cmbHeadName,cmbGroupName,cmbSubGroupName;
	CheckBox chkGroupAll,chkSubGroupAll;
	PopupDateField dCurDate;
	TextRead txtTransactionId;

	private ArrayList<Label>tbLblSl=new ArrayList<Label>();
	private ArrayList<Label>tbLblLedgerId=new ArrayList<Label>();
	private ArrayList<Label>tbLblLedgerName=new ArrayList<Label>();
	private ArrayList<Label>tbLblGroupId=new ArrayList<Label>();
	private ArrayList<Label>tbLblGroupName=new ArrayList<Label>();
	private ArrayList<Label>tbLblSubGroupId=new ArrayList<Label>();
	private ArrayList<Label>tbLblSubGroupName=new ArrayList<Label>();
	private ArrayList<CheckBox>tbChkSelect=new ArrayList<CheckBox>();
	Table table=new Table();

	private boolean isUpdate = false;
	private boolean isFind = true;
	SimpleDateFormat dFormat=new SimpleDateFormat("yyyy-MM-dd");

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtCategoryID = new TextField();
	ArrayList<Component> allComp = new ArrayList<Component>();

	public ConversionCostHeadWiseLedgerMapping(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("CONVERSION COST HEAD WISE LEDGER MAPPING :: "+this.sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		btnIni(true);
		componentIni(true);
		setEventAction();
		focusEnter();
		authenticationCheck();
		headNameLoadData();
		groupNameLoadData();
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
		allComp.add(cmbHeadName);
		allComp.add(cmbHeadName);
		allComp.add(cmbSubGroupName);
		allComp.add(button.btnNew);
		allComp.add(button.btnSave);
		allComp.add(button.btnEdit);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnFind);
		allComp.add(button.btnExit);

		new FocusMoveByEnter(this,allComp);
	}
	private void groupNameLoadData()
	{
		cmbGroupName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select Group_Id,Group_Name from tbMain_Group where Head_Id like '%E%' order by Group_Name";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbGroupName.addItem(element[0]);
				cmbGroupName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void headNameLoadData()
	{
		cmbHeadName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select headId,headName from tbConversionCostHeadInfo order by headName";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbHeadName.addItem(element[0]);
				cmbHeadName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void subGroupDataLoad()
	{
		cmbSubGroupName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String groupId="%";
			if(cmbGroupName.getValue()!=null){
				groupId=cmbGroupName.getValue().toString();
			}
			String query = "select Sub_Group_Id,Sub_Group_Name from tbSub_Group where Group_Id like '"+groupId+"' " +
					"and Group_Id in(select Group_Id from tbMain_Group where Head_Id like '%E%')";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				cmbSubGroupName.addItem(element[0]);
				cmbSubGroupName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void tableDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String groupId="%",subGroupId="%";
			if(cmbGroupName.getValue()!=null){
				groupId=cmbGroupName.getValue().toString();
			}
			if(cmbSubGroupName.getValue()!=null){
				subGroupId=cmbSubGroupName.getValue().toString();
			}
			String query = "select a.Ledger_Id,a.Ledger_Name,a.GroupId,isNull((select Group_Name from tbMain_Group where Group_Id=a.GroupId),'')Group_Name, "+
						" a.SubGroupId,isNull((select Sub_Group_Name from tbSub_Group where Sub_Group_Id=a.SubGroupId),'')Sub_Group_Name from ( "+
						" select Ledger_Id,Ledger_Name,SUBSTRING(Create_From,4,4)GroupId,SUBSTRING(Create_From,9,4)SubGroupId  "+
						" from tbLedger where Create_From like '%E%' and Create_From like '%"+groupId+"%' and Create_From like '%"+subGroupId+"%' "+
						" )a order by Ledger_Name";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			int a=0;
			while(iter.hasNext())
			{
				Object element[]=(Object[])iter.next();
				tbLblLedgerId.get(a).setValue(element[0]);
				tbLblLedgerName.get(a).setValue(element[1]);
				tbLblGroupId.get(a).setValue(element[2]);
				tbLblGroupName.get(a).setValue(element[3]);
				tbLblSubGroupId.get(a).setValue(element[4]);
				tbLblSubGroupName.get(a).setValue(element[5]);
				
				if(a==tbLblGroupId.size()-1){
					tableRowAdd(a+1);
				}
				a++;
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private boolean tableValueCheck(){
		for(int a=0;a<tbChkSelect.size();a++){
			if(!tbLblLedgerId.get(a).getValue().toString().isEmpty()&&tbChkSelect.get(a).booleanValue()){
				return true;
			}
		}
		return false;
	}
	private boolean checkValidation(){
		if(cmbHeadName.getValue()!=null){
			if(cmbGroupName.getValue()!=null||chkGroupAll.booleanValue()){
				if(cmbSubGroupName.getValue()!=null||chkSubGroupAll.booleanValue()){
					if(tableValueCheck()){
						return true;
					}
					else{
						showNotification("Please Provide All Data",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Please Provide Sub Group Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide Group Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Head Name",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	
	public void setEventAction()
	{
		chkGroupAll.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(chkGroupAll.booleanValue()){
					cmbGroupName.setValue(null);
					cmbGroupName.setEnabled(false);
					chkSubGroupAll.setValue(false);
					subGroupDataLoad();
				}
				else{
					cmbGroupName.setEnabled(true);
					cmbSubGroupName.removeAllItems();
				}
			}
		});
		chkSubGroupAll.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(chkSubGroupAll.booleanValue()){
					cmbSubGroupName.setValue(null);
					cmbSubGroupName.setEnabled(false);
					tableDataLoad();
				}
				else{
					cmbSubGroupName.setEnabled(true);
					tableClear();
				}
			}
		});
		cmbGroupName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbGroupName.getValue()!=null){
					chkSubGroupAll.setValue(false);
					subGroupDataLoad();
				}
				else if(!chkGroupAll.booleanValue()&&cmbGroupName.getValue()==null){
					chkSubGroupAll.setValue(false);
					cmbSubGroupName.removeAllItems();
				}
			}
		});
		cmbSubGroupName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSubGroupName.getValue()!=null){
					tableDataLoad();
				}
				else if(!chkSubGroupAll.booleanValue()&&cmbSubGroupName.getValue()==null){
					tableClear();
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
				cmbHeadName.focus();
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
	}

	private void findButtonEvent() 
	{
		Window win = new ConversionCostHeadWiseLedgerMappingFind(sessionBean, txtCategoryID);
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
			String sql = "select a.transactionNo,a.dDate,a.headId,a.groupId,a.subgroupId,b.ledgerId,b.ledgerName "+
					"  from tbConversionCostWiseLedgerMappingInfo a  "+
					" inner join tbConversionCostWiseLedgerMappingDetails b on a.transactionNo=b.tranasactionNo "+
					" where a.transactionNo='"+txtCategoryId+"'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			int a=0;
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				if(a==0){
					txtTransactionId.setValue(element[0]);
					dCurDate.setValue(element[1]);
					cmbHeadName.setValue(element[2]);
					if(element[3].toString().equalsIgnoreCase("All")){
						chkGroupAll.setValue(true);
					}
					else{
						cmbGroupName.setValue(element[3]);
					}
					if(element[4].toString().equalsIgnoreCase("All")){
						chkSubGroupAll.setValue(true);
					}
					else{
						cmbSubGroupName.setValue(element[3]);
					}
				}
				for(int b=0;b<tbChkSelect.size();b++){
					if(element[5].toString().equalsIgnoreCase(tbLblLedgerId.get(b).getValue().toString())){
						tbChkSelect.get(b).setValue(true);
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
			String sqlInfo = "delete from tbConversionCostWiseLedgerMappingInfo where transactionNo='"+txtTransactionId.getValue()+"'";
			String sqlDetails="delete from tbConversionCostWiseLedgerMappingDetails where tranasactionNo='"+txtTransactionId.getValue()+"'";
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
			String query = "select isnull(MAX(transactionNo),0)+1 from tbConversionCostWiseLedgerMappingInfo";
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
			if(cmbGroupName.getValue()!=null){
				groupIdInfo=cmbGroupName.getValue().toString();
				groupNameInfo=cmbGroupName.getItemCaption(cmbGroupName.getValue());
			}
			if(cmbSubGroupName.getValue()!=null){
				subGroupIdInfo=cmbSubGroupName.getValue().toString();
				subGroupNameInfo=cmbSubGroupName.getItemCaption(cmbSubGroupName.getValue());
			}
			String insertQuery = "insert into tbConversionCostWiseLedgerMappingInfo(transactionNo,dDate,headId,headName," +
					"groupid,groupName,subGroupId,subGroupName,userIp,userName,entrytime)values "+
					" ('"+txtTransactionId.getValue()+"','"+dFormat.format(dCurDate.getValue())+"','"+cmbHeadName.getValue()+"'," +
					"'"+cmbHeadName.getItemCaption(cmbHeadName.getValue())+"','"+groupIdInfo+"','"+groupNameInfo+"'," +
					"'"+subGroupIdInfo+"','"+subGroupNameInfo+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
			session.createSQLQuery(insertQuery).executeUpdate();
			
			for(int a=0;a<tbChkSelect.size();a++){
				if(tbChkSelect.get(a).booleanValue()&&!tbLblLedgerId.get(a).getValue().toString().isEmpty()){
					
					String insertQueryDetails="insert into tbConversionCostWiseLedgerMappingDetails(tranasactionNo,headId,headName, "+
							" ledgerId,ledgerName,groupId,groupName,subGroupId,subGroupName)values('"+txtTransactionId.getValue()+"'," +
							"'"+cmbHeadName.getValue()+"','"+cmbHeadName.getItemCaption(cmbHeadName.getValue())+"','"+tbLblLedgerId.get(a).getValue()+"'," +
							"'"+tbLblLedgerName.get(a).getValue()+"','"+tbLblGroupId.get(a).getValue()+"','"+tbLblGroupName.get(a).getValue()+"'," +
							"'"+tbLblSubGroupId.get(a).getValue()+"','"+tbLblSubGroupName.get(a).getValue()+"')";
					
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
		if(cmbHeadName.getValue()!=null)
		{
			isUpdate = true;
			isFind = false;
			btnIni(false);
			componentIni(false);
			if(chkGroupAll.booleanValue()){
				cmbGroupName.setEnabled(false);
			}
			if(chkSubGroupAll.booleanValue()){
				cmbSubGroupName.setEnabled(false);
			}
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

	public String headId()
	{
		String ret = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "SELECT ISNULL((MAX(CAST(SUBSTRING(headId,3,50) AS INT))+1),1)  FROM tbConversionCostHeadInfo";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				ret = "H-"+iter.next().toString();
			}
		}
		catch(Exception ex)
		{
			showNotification("Error",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}

	public void txtClear()
	{
		cmbHeadName.setValue(null);
		cmbGroupName.setValue(null);
		cmbSubGroupName.setValue(null);
		dCurDate.setValue(new java.util.Date());
		txtTransactionId.setValue("");
		chkGroupAll.setValue(false);
		chkSubGroupAll.setValue(false);
		tableClear();
	}

	private void tableClear() {
		for(int a=0;a<tbLblGroupId.size();a++){
			tbLblGroupId.get(a).setValue("");
			tbLblGroupName.get(a).setValue("");
			tbLblLedgerId.get(a).setValue("");
			tbLblLedgerName.get(a).setValue("");
			tbLblSubGroupId.get(a).setValue("");
			tbLblSubGroupName.get(a).setValue("");
			tbChkSelect.get(a).setValue(false);
		}
	}

	private void componentIni(boolean b) 
	{	
		cmbHeadName.setEnabled(!b);
		cmbGroupName.setEnabled(!b);
		cmbSubGroupName.setEnabled(!b);
		dCurDate.setEnabled(!b);
		txtTransactionId.setEnabled(!b);
		chkGroupAll.setEnabled(!b);
		chkSubGroupAll.setEnabled(!b);
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
	public void tableRowAdd(final int ar)
	{
		tbLblSl.add(ar, new Label(""));
		tbLblSl.get(ar).setWidth("100%");
		tbLblSl.get(ar).setImmediate(true);
		tbLblSl.get(ar).setHeight("23px");
		tbLblSl.get(ar).setValue(ar+1);

		tbChkSelect.add(ar,new CheckBox());
		tbChkSelect.get(ar).setWidth("100%");
		tbChkSelect.get(ar).setImmediate(true);
		tbChkSelect.get(ar).setHeight("23px");

		tbLblLedgerId.add(ar, new Label(""));
		tbLblLedgerId.get(ar).setWidth("100%");
		tbLblLedgerId.get(ar).setImmediate(true);
		tbLblLedgerId.get(ar).setHeight("23px");


		tbLblLedgerName.add(ar, new Label(""));
		tbLblLedgerName.get(ar).setWidth("100%");
		tbLblLedgerName.get(ar).setImmediate(true);
		tbLblLedgerName.get(ar).setHeight("23px");

		tbLblGroupId.add(ar, new Label(""));
		tbLblGroupId.get(ar).setWidth("100%");
		tbLblGroupId.get(ar).setImmediate(true);
		tbLblGroupId.get(ar).setHeight("23px");

		tbLblGroupName.add(ar, new Label(""));
		tbLblGroupName.get(ar).setWidth("100%");
		tbLblGroupName.get(ar).setImmediate(true);
		tbLblGroupName.get(ar).setHeight("23px");

		tbLblSubGroupId.add(ar, new Label(""));
		tbLblSubGroupId.get(ar).setWidth("100%");
		tbLblSubGroupId.get(ar).setImmediate(true);
		tbLblSubGroupId.get(ar).setHeight("23px");

		tbLblSubGroupName.add(ar, new Label(""));
		tbLblSubGroupName.get(ar).setWidth("100%");
		tbLblSubGroupName.get(ar).setImmediate(true);
		tbLblSubGroupName.get(ar).setHeight("23px");

		table.addItem(new Object[]{tbLblSl.get(ar),tbChkSelect.get(ar),tbLblLedgerId.get(ar),tbLblLedgerName.get(ar),
				tbLblGroupId.get(ar),tbLblGroupName.get(ar),tbLblSubGroupId.get(ar),tbLblSubGroupName.get(ar)},ar);
	}
	private void tableInitialze() {
		for(int a=0;a<10;a++){
			tableRowAdd(a);
		}

	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("700px");
		setHeight("530px");

		cmbHeadName = new ComboBox();
		cmbHeadName.setImmediate(true);
		cmbHeadName.setWidth("250px");
		cmbHeadName.setHeight("24px");	
		cmbHeadName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Head Name: "), "top:30.0px;left:45.0px;");
		mainLayout.addComponent(cmbHeadName, "top:28.0px;left:160.0px;");

		cmbGroupName = new ComboBox();
		cmbGroupName.setImmediate(true);
		cmbGroupName.setWidth("250px");
		cmbGroupName.setHeight("24px");	
		cmbGroupName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Group Name: "), "top:60.0px;left:45.0px;");
		mainLayout.addComponent(cmbGroupName, "top:58.0px;left:160.0px;");

		cmbSubGroupName = new ComboBox();
		cmbSubGroupName.setImmediate(true);
		cmbSubGroupName.setWidth("250px");
		cmbSubGroupName.setHeight("24px");	
		cmbSubGroupName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Sub Group Name: "), "top:90.0px;left:45.0px;");
		mainLayout.addComponent(cmbSubGroupName, "top:88.0px;left:160.0px;");



		chkGroupAll=new CheckBox("All");
		chkGroupAll.setImmediate(true);
		mainLayout.addComponent(chkGroupAll, "top:60.0px;left:410.0px;");

		chkSubGroupAll=new CheckBox("All");
		chkSubGroupAll.setImmediate(true);
		mainLayout.addComponent(chkSubGroupAll, "top:90.0px;left:410.0px;");

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

		table.setWidth("550px");
		table.setHeight("275px");
		table.setImmediate(true);
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);	

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#",25);

		table.addContainerProperty("Check", CheckBox.class, new CheckBox());
		table.setColumnWidth("Check",40);

		table.addContainerProperty("Ledger Id", Label.class, new Label());
		table.setColumnWidth("Ledger Id",80);
		table.setColumnCollapsed("Ledger Id", true);

		table.addContainerProperty("Ledger Name", Label.class, new Label());
		table.setColumnWidth("Ledger Name",400);

		table.addContainerProperty("Group Id", Label.class, new Label());
		table.setColumnWidth("Group Id",80);
		table.setColumnCollapsed("Group Id", true);

		table.addContainerProperty("Group Name", Label.class, new Label());
		table.setColumnWidth("Group Name",350);
		table.setColumnCollapsed("Group Name", true);

		table.addContainerProperty("Sub Group Id", Label.class, new Label());
		table.setColumnWidth("Sub Group Id",80);
		table.setColumnCollapsed("Sub Group Id", true);

		table.addContainerProperty("Sub Group Name", Label.class, new Label());
		table.setColumnWidth("Sub Group Name",350);
		table.setColumnCollapsed("Sub Group Name", true);

		tableInitialze();

		mainLayout.addComponent(table, "top:130.0px;left:45.0px;");
		mainLayout.addComponent(button, "top:430.0px;left:90.0px;");

		return mainLayout;
	}

}