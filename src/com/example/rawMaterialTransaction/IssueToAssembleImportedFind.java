package com.example.rawMaterialTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property;
import com.vaadin.data.Property.*;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
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

public class IssueToAssembleImportedFind extends Window
{
	private SessionBean sessionBean;
	private PopupDateField dFromDate;
	private PopupDateField dToDate;
	private ComboBox cmbIssueTo;
	private CheckBox chkFindAll;
	private Label lblFromDate;
	private Label lblToDate;
	private Label lblIssueTo;
	private NativeButton btnFind;

	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<Label> tblblIssueNo = new ArrayList<Label>();
	private ArrayList<Label> tblblIssueTo = new ArrayList<Label>();
	private ArrayList<Label> tblblReqNo = new ArrayList<Label>();
	private ArrayList<Label> tblblBatchNo = new ArrayList<Label>();
	private ArrayList<Label> tblblIssueDate = new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();
	boolean isUpdate=false,isFind=false;
	private AbsoluteLayout mainLayout;
	//TextField txtIssueNo=new TextField();
	
	TextField txtIssueNo;
	
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

	public IssueToAssembleImportedFind(SessionBean sessionBean,TextField txtIssueNo)
	{
		this.txtIssueNo=txtIssueNo;
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("ISSUE TO ASSEMBLE FIND :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		btnAction();
		center();
		setModal(true);
		IssueToLoad();
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
	private void IssueToLoad() {
		cmbIssueTo.removeAllItems();
		Iterator iter=dbService("select 0,issueTo from tbIssueToAssembleInfo where issueDate "+
				" between '"+dateFormat.format(dFromDate.getValue())+"' and '"+dateFormat.format(dToDate.getValue())+"' " +
				"order by issueDate ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbIssueTo.addItem(element[1]);
			cmbIssueTo.setItemCaption(element[1], element[1].toString());
		}
	}

	public void btnAction()
	{
		chkFindAll.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(chkFindAll.booleanValue()){
					cmbIssueTo.setEnabled(false);
					cmbIssueTo.setValue(null);
				}
				else{
					cmbIssueTo.setEnabled(true);
				}
			}
		});
		dFromDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				IssueToLoad();
			}
		});
		dToDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				IssueToLoad();
			}
		});
		btnFind.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
			
				if(cmbIssueTo.getValue()!=null||chkFindAll.booleanValue()){
					tableDataLoad();
				}
				else{
					showNotification("Please select All Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					String receiptAreaId = tblblIssueNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtIssueNo.setValue(receiptAreaId);
					close();
					
				}
			}
		});
	}

	private void tableDataLoad() {
		String issueTo="%";
		if(cmbIssueTo.getValue()!=null){
			issueTo=cmbIssueTo.getValue().toString();
		}
		Iterator iter=dbService("select issueNo,issueTo,ReqNo,batchNo,convert(varchar(103),issueDate,103)issueDate from tbIssueToAssembleInfo "+ 
				" where issueDate  between '"+dateFormat.format(dFromDate.getValue())+"' and '"+dateFormat.format(dToDate.getValue())+"' " +
				"and issueTo like '"+issueTo+"' and  importIssueNo is not null and importIssueNo !='0' order by issueNo ");
		int a=0;
		if(iter.hasNext()){
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				tblblIssueNo.get(a).setValue(element[0]);
				tblblIssueTo.get(a).setValue(element[1]);
				tblblReqNo.get(a).setValue(element[2]);
				tblblBatchNo.get(a).setValue(element[3]);
				tblblIssueDate.get(a).setValue(element[4]);
				a++;
				if(a==tblblBatchNo.size()-1){
					tableRowAdd(a+1);
				}
			}
		}
		else{
			showNotification("Sorry!!","There are No Data",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	public void tableInitialise(){
		for(int i=0;i<10;i++){
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		try{

			tbLblSl.add(ar,new Label());
			tbLblSl.get(ar).setWidth("20px");
			tbLblSl.get(ar).setValue(ar + 1);	

			tblblIssueNo.add(ar, new Label());
			tblblIssueNo.get(ar).setImmediate(true);
			tblblIssueNo.get(ar).setWidth("100%");
			tblblIssueNo.get(ar).setHeight("-1px");

			tblblIssueTo.add(ar, new Label());
			tblblIssueTo.get(ar).setImmediate(true);
			tblblIssueTo.get(ar).setWidth("100%");
			tblblIssueTo.get(ar).setHeight("-1px");


			tblblReqNo.add(ar, new Label());
			tblblReqNo.get(ar).setImmediate(true);
			tblblReqNo.get(ar).setWidth("100%");
			tblblReqNo.get(ar).setHeight("-1px");	

			tblblBatchNo.add(ar, new Label());
			tblblBatchNo.get(ar).setImmediate(true);
			tblblBatchNo.get(ar).setWidth("100%");
			tblblBatchNo.get(ar).setHeight("-1px");

			tblblIssueDate.add(ar, new Label());
			tblblIssueDate.get(ar).setImmediate(true);
			tblblIssueDate.get(ar).setWidth("100%");
			tblblIssueDate.get(ar).setHeight("-1px");

			table.addItem(new Object[]{tbLblSl.get(ar),	tblblIssueNo.get(ar),tblblIssueTo.get(ar),tblblReqNo.get(ar),
					tblblBatchNo.get(ar),tblblIssueDate.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("530px");
		mainLayout.setHeight("400px");

		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate, "top:20.0px;left:50.0px;");

		dFromDate=new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setHeight("-1px");
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		mainLayout.addComponent(dFromDate, "top:18.0px;left:140.0px;");

		lblToDate = new Label("To Date");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:46.0px;left:50.0px;");

		dToDate =new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		mainLayout.addComponent(dToDate, "top:44.0px;left:140.0px;");

		lblIssueTo = new Label();
		lblIssueTo.setImmediate(false);
		lblIssueTo.setWidth("-1px");
		lblIssueTo.setHeight("-1px");
		lblIssueTo.setValue("Issue To :");
		mainLayout.addComponent(lblIssueTo, "top:74.0px;left:50.0px;");

		cmbIssueTo =new ComboBox();
		cmbIssueTo.setImmediate(true);
		cmbIssueTo.setWidth("200px");
		cmbIssueTo.setHeight("-1px");
		mainLayout.addComponent(cmbIssueTo, "top:72.0px;left:140.0px;");

		chkFindAll=new CheckBox("All");
		chkFindAll.setImmediate(true);
		mainLayout.addComponent(chkFindAll, "top:72.0px;left:350.0px;");

		btnFind= new NativeButton("FIND");
		btnFind.setImmediate(false);
		btnFind.setIcon(new ThemeResource("../icons/update1.png"));
		btnFind.setImmediate(true);
		btnFind.setWidth("90px");
		btnFind.setHeight("24px");
		mainLayout.addComponent(btnFind, "top:100.0px;left:140.0px;");

		table = new Table();
		table.setWidth("100%");
		table.setHeight("226px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);
		table.setSelectable(true);

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Issue No",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Issue No",50);

		table.addContainerProperty("Issue To",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Issue To",100);

		table.addContainerProperty("Req No",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Req No",70);

		table.addContainerProperty("Batch No",  Label.class , new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Batch No",80);

		table.addContainerProperty("Issue Date",  Label.class , new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Issue Date",100);

		mainLayout.addComponent(table, "top:134.0px;left:10.0px;");
		tableInitialise();

		setStyleName("cwindow");
		return mainLayout;
	}
}
