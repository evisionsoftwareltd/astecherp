package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.EventListener;
import com.common.share.MessageBox.ButtonType;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ItemActivation extends Window {
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	ArrayList<Component> allComp = new ArrayList<Component>(); 
	private TextRead invoiceNo ;
	private TextRead txtGroupId;
	private TextRead txtAutoId=new TextRead();
	/*private HashMap hRate = new HashMap();
	private HashMap hStock = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();*/
	Double Total,Amount,wRate;
	private Label lblgroupName, lblGroupId;
	private ComboBox cmbgroupName ;
	double totalsum = 0.0;

	private Table table = new Table();
	private ArrayList<Label> tblblsl = new ArrayList<Label>();
	private ArrayList<ComboBox> tbcmbItem = new ArrayList<ComboBox>();
	private ArrayList<CheckBox> tbchkActive = new ArrayList<CheckBox>();

	private Label label = new Label();
	private Label l1 = new Label();

	private Label lblLine=new Label("<b><font color='#e65100'>===========================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
	boolean isUpdate=false;
	boolean isFind=false;
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat df = new DecimalFormat("#0.00");
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");

	public ItemActivation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("RAW ITEM GROUP :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		tableInitialise();
		groupDataload();
		btnIni(true);
		componentIni(true);
		setEventAction();
		cButton.btnNew.focus();
		focusEnter();
	}

	public List<?>  dbService(String sql){
		List<?>  list = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			list =session.createSQLQuery(sql).list();
			return list;
		}
		catch(Exception exp){

		}
		return list;
	}
	public void groupDataload()
	{
		cmbgroupName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try {

			String squery="select vRawItemCode,vRawItemName from tbRawItemInfo where vCategoryType like 'Raw Material'";
			System.out.println("squery : "+squery);
			List<?>  list = session.createSQLQuery(squery).list();

			for (Iterator<?>  iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbgroupName.addItem(element[0]);
				cmbgroupName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception ex){
			this.showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		setWidth("520px");
		setHeight("525px");

		/*lblGroupId = new Label();
		lblGroupId.setImmediate(false);
		lblGroupId.setWidth("-1px");
		lblGroupId.setHeight("-1px");
		lblGroupId.setValue("Group Id:");
		mainLayout.addComponent(lblGroupId, "top:20.0px;left:20.0px;");*/

		/*txtAutoId = new TextRead(1);
		txtAutoId.setImmediate(false);
		txtAutoId.setWidth("100px");
		txtAutoId.setHeight("-1px");*/

		/*txtGroupId =  new TextRead(1);
		txtGroupId.setImmediate(true);
		txtGroupId.setWidth("50px");
		txtGroupId.setHeight("24px");
		mainLayout.addComponent(txtGroupId, "top:18.0px;left:150.0px;");*/

		lblgroupName = new Label();
		lblgroupName.setImmediate(false);
		lblgroupName.setWidth("-1px");
		lblgroupName.setHeight("-1px");
		lblgroupName.setValue("Group Name:");
		mainLayout.addComponent(lblgroupName, "top:50.0px;left:20.0px;");

		cmbgroupName = new ComboBox();
		cmbgroupName.setImmediate(true);
		cmbgroupName.setNullSelectionAllowed(false);
		cmbgroupName.setNewItemsAllowed(false);
		cmbgroupName.setWidth("280px");
		cmbgroupName.setHeight("24px");
		cmbgroupName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbgroupName, "top:48.0px;left:150.0px;");

		table.setWidth("90%");
		table.setHeight("325px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",30);

		table.addContainerProperty("Item Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("Item  Name",280);	
		table.setColumnCollapsed("Item  Name", true);

		table.addContainerProperty("isActive", CheckBox.class , new CheckBox());
		table.setColumnWidth("isActive",45);

		mainLayout.addComponent(table, "top: 80px; left: 70px;");

		mainLayout.addComponent(lblLine, "top: 420px; left: 0px;");
		mainLayout.addComponent(cButton, "top: 440px; left: 30px;");

		return mainLayout;
	}

	private void focusEnter()
	{

		allComp.add(txtGroupId);
		allComp.add(cmbgroupName);
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public void setEventAction(){

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				newButtonEvent();
				cmbgroupName.focus();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				saveButtonEvent();

			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				updateButtonEvent();
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				refreshButtonEvent();
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
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

		cmbgroupName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbgroupName.getValue()!=null){
					tbItemLoad();
				}
			}
		});
		
	
	}



	private void tableClear()
	{
		for(int a=0;a<tbchkActive.size();a++){
			tbcmbItem.get(a).setValue(null);
			tbchkActive.get(a).setValue(false);
		}
	}

	private void findButtonEvent()
	{
		isFind=true;
		Window win=new FindWindow(sessionBean,txtAutoId,"");
		win.addListener(new Window.CloseListener() {
			public void windowClose(CloseEvent e) 
			{
				if(txtAutoId.getValue().toString().length()>0)
					findInitialise();
			}
		});


		this.getParent().addWindow(win);

	}

	private void findInitialise(){

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List led = session.createSQLQuery("select 0,groupid from tbItemGroupInfo where transactionid='"+txtAutoId.getValue()+"'").list();
			txtClear();
			if(led.iterator().hasNext()){

				Object[] element = (Object[]) led.iterator().next();
				cmbgroupName.setValue(element[1]);	
				
			}
			List list=session.createSQLQuery("select itemId,isActive from tbItemGroupDetails " +
					"where transactionNo='"+txtAutoId.getValue()+"'").list();


			int i=0;

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element=(Object[]) iter.next();
				tbcmbItem.get(i).setValue(element[0]);
				System.out.println("isActive: "+element[1]);
				if(element[1].toString().equalsIgnoreCase("1")){
					tbchkActive.get(i).setValue(true);
				}
				else{
					tbchkActive.get(i).setValue(false);
				}

				i++;
			}
		}catch(Exception exp)
		{
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}

	}


	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}


	

	private void updateButtonEvent()
	{
		if(cmbgroupName.getValue()!= null)
		{
			isUpdate = true;
			isFind=false;
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
			this.getParent().showNotification(
					"Update Failed",
					"There are no data for update.",
					Notification.TYPE_WARNING_MESSAGE);
	}


	private void saveButtonEvent() {

		if(tbcmbItem.get(0).getValue()!=null){

			if(isUpdate)
			{
				final MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener() 
				{
					public void buttonClicked(ButtonType buttonType) 
					{
						if(buttonType == ButtonType.YES)
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							Transaction tx;
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							tx = session.beginTransaction();
							if(deleteData(session,tx)){
								insertData(session,tx);
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
							Transaction tx;
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							tx = session.beginTransaction();
							//if(nullCheck())
								insertData(session,tx);
						}

					}
				});	
			}
		}
		else
		{
			this.getParent().showNotification("Warning !","Please Select Product .", Notification.TYPE_WARNING_MESSAGE);	
		}
	}
	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			System.out.println(txtAutoId.getValue());
			session.createSQLQuery("delete from tbItemGroupInfo where transactionid='"+txtAutoId.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete from tbItemGroupDetails where transactionNo='"+txtAutoId.getValue()+"'").executeUpdate();
			
			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	
	private String autoTransactionNo() 
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select isnull(max( CAST(transactionId as int) ),0)+1 from  tbItemGroupInfo";

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
	private void insertData(Session session,Transaction tx)
	{
		try
		{
			String transactionNo;
			if(isUpdate){
				transactionNo=txtAutoId.getValue().toString();
			}
			else{
				transactionNo=autoTransactionNo();
			}
			
			String sqlInfo="insert into tbItemGroupInfo(transactionId,groupId,groupName,userIp,userId,entryTime)values" +
					"('"+transactionNo+"','"+cmbgroupName.getValue()+"','"+cmbgroupName.getItemCaption(cmbgroupName.getValue())+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
			for(int a=0;a<tbchkActive.size();a++){
				if(tbcmbItem.get(a).getValue()!=null){
					String chk=tbchkActive.get(a).booleanValue()?"1":"0";
					String sqlDetails="insert into tbItemGroupDetails(transactionNo,itemId,itemName,isActive)values" +
							"('"+transactionNo+"','"+tbcmbItem.get(a).getValue()+"','"+tbcmbItem.get(a).getItemCaption(tbcmbItem.get(a).getValue())+"'," +
							"'"+chk+"')";
					if(a==0){
						session.createSQLQuery(sqlInfo).executeUpdate();
					}
					session.createSQLQuery(sqlDetails).executeUpdate();
				}
			}
			tx.commit();
			componentIni(true);
			btnIni(true);
			txtClear();
			isFind=false;	
			isUpdate=false;
			showNotification("All Information Save Successfully",Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp){
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);

		}

	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();

	}

	public void txtClear()
	{
		cmbgroupName.setValue(null);
		tableClear();
	}

	private void componentIni(boolean b) 
	{
		cmbgroupName.setEnabled(!b);
		table.setEnabled(!b);

	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	public void tableInitialise()
	{

		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private boolean doubleEntryCheck(String caption,int row)
	{

		for(int a=0;a<tbchkActive.size();a++){
			if(a!=row){
				if(caption.equals(tbcmbItem.get(a).getValue())){
					return true;
				}
			}
		}

		return false;
	}
	private void tbItemLoad(){
		for(int a=0;a<tblblsl.size();a++)
		{
			tbcmbItem.get(a).removeAllItems();
			String sql="select vRawItemCode,vRawItemName from tbRawItemInfo where vRawItemCode!='"+cmbgroupName.getValue().toString()+"' and  vCategoryType like 'Raw Material'";
			Iterator<?>iter= dbService(sql).iterator();
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				tbcmbItem.get(a).addItem(element[0]);
				tbcmbItem.get(a).setItemCaption(element[0], element[1].toString());
			}
		}
	}
	public void tableRowAdd(final int ar)
	{

		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();

		try
		{
			tblblsl.add(ar,new Label());
			tblblsl.get(ar).setWidth("20px");
			tblblsl.get(ar).setValue(ar + 1);

			tbcmbItem.add(ar,new ComboBox());
			tbcmbItem.get(ar).setWidth("100%");
			tbcmbItem.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			tbcmbItem.get(ar).setImmediate(true);
			tbcmbItem.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					
					if(tbcmbItem.get(ar).getValue()!=null){
						if(doubleEntryCheck(tbcmbItem.get(ar).getValue().toString(), ar)){
							showNotification("Sorry!!","Double Entry",Notification.TYPE_WARNING_MESSAGE);
							tbcmbItem.get(ar).setValue(null);
							tbcmbItem.get(ar).focus();
						}
					}
				}
			});

			tbchkActive.add(ar,new CheckBox());
			tbchkActive.get(ar).setWidth("100%");
			tbchkActive.get(ar).setImmediate(true);

			table.addItem(new Object[]{tblblsl.get(ar), tbcmbItem.get(ar),tbchkActive.get(ar) },ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}




}
