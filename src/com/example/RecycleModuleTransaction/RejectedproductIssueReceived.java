package com.example.RecycleModuleTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButtonNew;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;

import java.text.DecimalFormat;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RejectedproductIssueReceived extends Window
{
	private AbsoluteLayout mainLayout;

	private Label lblDate;
	private PopupDateField dDate;

	private Label lblReceiveNo;
	private TextRead trReceiveNo;

	private Label lblIssueNo;
	private ComboBox cmbIssueNo;

	private Label lblChallanNo;
	private TextField txtChallanNo;

	private Label lblChallanDate;
	private PopupDateField dChallanDate;

	boolean isUpdate=false;

	private Table table=new Table();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lblIssueNotb = new ArrayList<Label>();
	private ArrayList<Label> lblProductName = new ArrayList<Label>();
	private ArrayList<Label> lblProductId = new ArrayList<Label>();
	private ArrayList<Label> lblColor= new ArrayList<Label>();
	private ArrayList<Label> lblUnit = new ArrayList<Label>();
	private ArrayList<TextField> txtQtyKg = new ArrayList<TextField>();
	private ArrayList<TextField> txtQtyPcs = new ArrayList<TextField>();
	private ArrayList<Label> lblRemarks = new ArrayList<Label>();
    private DecimalFormat df= new DecimalFormat("#0.00");

	private CommonButtonNew button = new CommonButtonNew( "New",  "Save",  "",  "",  "Refresh",  "", "", "Exit","","");

	ArrayList<Component> allComp = new ArrayList<Component>();

	private SessionBean sessionBean;
	public RejectedproductIssueReceived(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("REJECTED PRODUCT RECEIVED :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("850px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		//this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		tableInitialise();
		setEventAction();
		tableclear();
		focusEnter();
		issuNoData();
	}


	private void componentIni(boolean b) 
	{
		lblDate.setEnabled(!b);
		dDate.setEnabled(!b);

		lblReceiveNo.setEnabled(!b);
		trReceiveNo.setEnabled(!b);

		lblIssueNo.setEnabled(!b);
		cmbIssueNo.setEnabled(!b);

		lblChallanNo.setEnabled(!b);
		txtChallanNo.setEnabled(!b);

		lblChallanDate.setEnabled(!b);
		dChallanDate.setEnabled(!b);

		table.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{

		button.btnNew.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
	}
	private void focusEnter()
	{

		allComp.add(dDate);
		allComp.add(cmbIssueNo);
		allComp.add(txtChallanNo);
		allComp.add(dChallanDate);
		allComp.add(table);

		allComp.add(button.btnNew);
		allComp.add(button.btnSave);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnExit);

		new FocusMoveByEnter(this,allComp);
	}

	public void tableInitialise()
	{
		for(int i=0;i<7;i++){
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbSL.add(ar, new Label(""));
		lbSL.get(ar).setWidth("100%");
		lbSL.get(ar).setImmediate(true);
		lbSL.get(ar).setHeight("23px");
		lbSL.get(ar).setValue(ar+1);

		lblIssueNotb.add(ar, new Label(""));
		lblIssueNotb.get(ar).setWidth("100%");
		lblIssueNotb.get(ar).setImmediate(true);
		lblIssueNotb.get(ar).setHeight("23px");

		lblProductName.add(ar, new Label(""));
		lblProductName.get(ar).setWidth("100%");
		lblProductName.get(ar).setImmediate(true);
		lblProductName.get(ar).setHeight("23px");

		lblProductId.add(ar, new Label(""));
		lblProductId.get(ar).setWidth("100%");
		lblProductId.get(ar).setImmediate(true);
		lblProductId.get(ar).setHeight("23px");

		lblColor.add(ar, new Label(""));
		lblColor.get(ar).setWidth("100%");
		lblColor.get(ar).setImmediate(true);
		lblColor.get(ar).setHeight("23px");

		lblUnit.add(ar, new Label(""));
		lblUnit.get(ar).setWidth("100%");
		lblUnit.get(ar).setImmediate(true);
		lblUnit.get(ar).setHeight("23px");

		txtQtyKg.add(ar, new TextField(""));
		txtQtyKg.get(ar).setWidth("100%");
		txtQtyKg.get(ar).setImmediate(true);
		txtQtyKg.get(ar).setHeight("23px");


		txtQtyPcs.add(ar, new TextField(""));
		txtQtyPcs.get(ar).setWidth("100%");
		txtQtyPcs.get(ar).setImmediate(true);
		txtQtyPcs.get(ar).setHeight("23px");

		lblRemarks.add(ar, new Label(""));
		lblRemarks.get(ar).setWidth("100%");
		lblRemarks.get(ar).setImmediate(true);
		lblRemarks.get(ar).setHeight("23px");

		table.addItem(new Object[]{lbSL.get(ar),lblIssueNotb.get(ar),lblProductId.get(ar),lblProductName.get(ar),lblColor.get(ar),lblUnit.get(ar),txtQtyKg.get(ar),txtQtyPcs.get(ar),lblRemarks.get(ar)},ar);
	}

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				focusEnter();
				newButtonEvent();
			}
		});
		button.btnRefresh.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event){ 
				refreshButtonEvent();
			}
		});
		cmbIssueNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				tableDataLoad();
			}
		});
		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event){ 
				chkValidation();
			}
		});
		button.btnExit.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event){ 
				close();
			}
		});

	}

	private void chkValidation() {
		if(dDate.getValue()!=null)
		{
			if(trReceiveNo.getValue()!=null)
			{
				if(cmbIssueNo.getValue()!=null)
				{
					if(!txtChallanNo.toString().trim().isEmpty())
					{
						if(dChallanDate.getValue()!=null)
						{
							if(tableCheck())
							{

								saveButtonEvent();
							}
							else
							{
								getParent().showNotification("No Data Found!!",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Please Select Challan Date",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Please Enter Challan No",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Please Select Issue No",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Please Enter Receive No",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Please Select Date",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private boolean tableCheck(){
		for(int a=0;a<lblProductId.size();a++)
		{
			if(!lblProductId.get(a).getValue().toString().isEmpty()){
				if(!lblProductName.get(a).getValue().toString().isEmpty()){
					if(txtQtyKg.get(a).getValue()!=null){
						if(txtQtyPcs.get(a).getValue()!=null){
							System.out.println("Table Check true return.......:");
							return true;
						}				
					}				
				}
			}

		}
		return false;
	}

	public void saveButtonEvent()
	{

		final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{	

					mb.buttonLayout.getComponent(0).setEnabled(false);
					insertData();
					btnIni(true);
					componentIni(true);
					txtClear();
					mb.close();

				}
			}
		});	
	}

	private void insertData()
	{
		Transaction tx = null;


		String date="";
		String receiveNo="";
		String issueNo="";
		String challanNo="";
		String challanDate="";

		date=dateFormat.format(dDate.getValue());
		receiveNo=trReceiveNo.getValue().toString();
		challanNo=txtChallanNo.getValue().toString();
		challanDate=dateFormat.format(dChallanDate.getValue());
		issueNo=cmbIssueNo.getValue().toString();

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String insertRejectedProductReceived="insert into tbRejectedProductReceivedInfo"
					+ "(dDate,vReceiveNo,vIssueNo,vChallanNo,dChallanDate,userIp,userId,userName,entryTime)"
					+ " values"
					+ "('"+date+"',"
					+ "'"+receiveNo+"',"
					+ "'"+issueNo+"',"
					+ "'"+challanNo+"',"
					+ "'"+challanDate+"',"
					+ "'"+sessionBean.getUserIp()+"',"
					+ "'"+sessionBean.getUserId()+"',"
					+ "'"+sessionBean.getUserName()+"',"
					+ "CURRENT_TIMESTAMP)";

			session.createSQLQuery(insertRejectedProductReceived).executeUpdate();
			System.out.println("insertRejectedProductReceived: "+ insertRejectedProductReceived);
         
			String RecycleApproveQuery="update tbRejectedIssueInfo set isReceived='1' where issueNo='"+cmbIssueNo.getValue().toString()+"' ";
			session.createSQLQuery(RecycleApproveQuery).executeUpdate();
			
			
			String sqlDetails = "";
			for (int i=0; i<lblProductName.size(); i++)
			{
				//Object temp = lblProductName.get(i).getValue();
				if (!lblProductName.get(i).getValue().toString().trim().isEmpty())
				{

					String productName =lblProductName.get(i).getValue().toString().trim();
					System.out.println("lblProductName: "+ productName);

					sqlDetails="insert into tbRejectedProductReceivedDetails"
							+ "(vReceiveNo,vIssueNo,vProductId,vProductName,vColor,vUnit,"
							+ "vQtyKg,vQtyPcs,vRemarks,userIp,userId,userName,entryTime)"
							+ " values"
							+ "('"+receiveNo+"',"
							+ "'"+lblIssueNotb.get(i).getValue().toString().trim()+"',"
							+ "'"+lblProductId.get(i).getValue().toString().trim()+"',"
							+ "'"+lblProductName.get(i).getValue().toString().trim()+"',"
							+ "'"+lblColor.get(i).getValue().toString().trim()+"',"
							+ "'"+lblUnit.get(i).getValue().toString().trim()+"',"
							+ "'"+txtQtyKg.get(i).getValue().toString().trim().replaceAll("'", "")+"',"
							+ "'"+txtQtyPcs.get(i).getValue().toString().trim().replaceAll("'", "")+"',"
							+ "'"+lblRemarks.get(i).getValue().toString().trim().replaceAll("'", "")+"',"
							+ "'"+sessionBean.getUserIp()+"',"
							+ "'"+sessionBean.getUserId()+"',"
							+ "'"+sessionBean.getUserName()+"',"
							+ "CURRENT_TIMESTAMP)";
					session.createSQLQuery(sqlDetails).executeUpdate();
					System.out.println(sqlDetails);
				}
			}


			tx.commit();
			this.getParent().showNotification(" All information save successfully.");

		}catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	private void refreshButtonEvent()
	{
		componentIni(true);
		btnIni(true);
		txtClear();
		tableclear();
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		//updateBtnFileldED(false);
		btnIni(false);
		txtClear();
		trReceiveNo.setValue(selectReceiveNo());
		//isUpdate = false;
	}
	private void txtClear() {

		dDate.setValue(new Date());
		trReceiveNo.setValue("");
		cmbIssueNo.setValue(null);
		txtChallanNo.setValue("");
		dChallanDate.setValue(new Date());
		tableclear();

	}

	private String selectReceiveNo()
	{
		String itemcode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "select'RN-'+ CAST(ISNULL(MAX(CAST(SUBSTRING(vReceiveNo,4,LEN(vReceiveNo)) as int) ),0)+1 as varchar(120))     from tbRejectedProductReceivedInfo";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext())
			{
				itemcode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return itemcode;
	}

	public void issuNoData()
	{
		cmbIssueNo.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql= "select a.issueNo, a.issueNo from tbRejectedIssueInfo a inner join tbRejectedIssueDetails b on a.issueNo=b.issueNo where isReceived='0' ";

			List list=session.createSQLQuery(sql).list();


			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbIssueNo.addItem(element[0]);
				cmbIssueNo.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void tableDataLoad() {
		tableclear();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql=" select a.issueNo,productId,productName,unit,issueQtyKg,issueQtyPcs,isnull(color,'')color ,isnull(Remarks,'')Remarks  "
					+ "from tbRejectedIssueInfo a inner join "
					+ "tbRejectedIssueDetails b on a.issueNo=b.issueNo "
					+ "where a.issueNo like '"+cmbIssueNo.getValue()+"'";

			System.out.println("Tabel : "+sql );
			List list=session.createSQLQuery(sql).list();
			if(!list.isEmpty()){
				int ar=0;
				for(Iterator iter=list.iterator();iter.hasNext();){
					Object element[]=(Object[]) iter.next();
					lblIssueNotb.get(ar).setValue(element[0].toString());
					lblProductId.get(ar).setValue(element[1].toString());
					lblProductName.get(ar).setValue(element[2].toString());
					lblUnit.get(ar).setValue(element[3].toString());
					txtQtyKg.get(ar).setValue(df.format(Double.parseDouble(element[4].toString())));
					txtQtyPcs.get(ar).setValue(df.format(Double.parseDouble(element[5].toString())) );
					lblColor.get(ar).setValue(element[6].toString());
					lblRemarks.get(ar).setValue(element[7].toString());
					ar++;
				}
			}
		}
		catch(Exception exp){
			getParent().showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tableclear()
	{
		for(int i=0; i<lblProductName.size(); i++)
		{
			lblIssueNotb.get(i).setValue("");
			lblProductName.get(i).setValue("");
			lblProductId.get(i).setValue("");
			lblUnit.get(i).setValue("");
			lblColor.get(i).setValue("");
			txtQtyKg.get(i).setValue("");
			txtQtyPcs.get(i).setValue("");
			lblRemarks.get(i).setValue("");

		}
	}


	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("700px");
		mainLayout.setHeight("450");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("750px");
		setHeight("500px");

		lblDate = new Label();
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:30.0px; left:20.0px;");

		dDate = new PopupDateField();
		dDate.setDateFormat("dd-MM-yy");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setValue(new Date());
		dDate.setWidth("100px");
		mainLayout.addComponent(dDate, "top:30.0px;left:80.0px;");

		lblReceiveNo = new Label();
		lblReceiveNo.setWidth("-1px");
		lblReceiveNo.setHeight("-1px");
		lblReceiveNo.setValue("Receive No:");
		mainLayout.addComponent(lblReceiveNo, "top:30.0px; left:285.0px;");

		trReceiveNo = new TextRead();
		trReceiveNo.setImmediate(true);
		trReceiveNo.setWidth("120px");
		trReceiveNo.setHeight("-1px");
		mainLayout.addComponent(trReceiveNo,"top:30.0px; left:370.0px;");

		lblIssueNo = new Label();
		lblIssueNo.setWidth("-1px");
		lblIssueNo.setHeight("-1px");
		lblIssueNo.setValue("IssueNo:");
		mainLayout.addComponent(lblIssueNo,"top:60.0px; left:20.0px;");

		cmbIssueNo = new ComboBox();
		cmbIssueNo.setImmediate(true);
		cmbIssueNo.setNullSelectionAllowed(false);
		cmbIssueNo.setNewItemsAllowed(false);
		cmbIssueNo.setWidth("200px");
		cmbIssueNo.setHeight("-1px");
		cmbIssueNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbIssueNo,"top:58.0px; left:80.0px;");

		lblChallanNo = new Label();
		lblChallanNo.setWidth("-1px");
		lblChallanNo.setHeight("-1px");
		lblChallanNo.setValue("Challan No:");
		mainLayout.addComponent(lblChallanNo, "top:60.0px; left:285.0px;");

		txtChallanNo = new TextField();
		txtChallanNo.setWidth("120px");
		txtChallanNo.setHeight("-1px");
		mainLayout.addComponent(txtChallanNo,"top:58.0px; left:370.0px;");

		lblChallanDate = new Label();
		lblChallanDate.setWidth("-1px");
		lblChallanDate.setHeight("-1px");
		lblChallanDate.setValue("Challan Date :");
		mainLayout.addComponent(lblChallanDate, "top:60.0px; left:495.0px;");

		dChallanDate = new PopupDateField();
		dChallanDate.setDateFormat("dd-MM-yy");
		dChallanDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dChallanDate.setValue(new Date());
		dChallanDate.setWidth("100px");
		mainLayout.addComponent(dChallanDate, "top:58.0px; left:590.0px;");

		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("SL #", Label.class, new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Issue No", Label.class, new Label());
		table.setColumnWidth("Issue No",65);
		table.setColumnCollapsed("Issue No",true);


		table.addContainerProperty("Product Id", Label.class, new Label());
		table.setColumnWidth("Product Id",65);
		table.setColumnCollapsed("Product Id",true);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name",165);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",70);

		table.addContainerProperty("Color", Label.class, new Label());
		table.setColumnWidth("Color",70);

		table.addContainerProperty("Qty(kg)", TextField.class, new TextField());
		table.setColumnWidth("Qty(kg)",70);

		table.addContainerProperty("Qty(pcs)", TextField.class, new TextField());
		table.setColumnWidth("Qty(pcs)",70);


		table.addContainerProperty("Remarks", Label.class, new Label());
		table.setColumnWidth("Remarks",90);

		table.setColumnCollapsingAllowed(true);


		mainLayout.addComponent(table,"top:110.0px; left:60.0px;");

		mainLayout.addComponent(button,"top:400.0px; left:180.0px;");

		return mainLayout;
	}

}