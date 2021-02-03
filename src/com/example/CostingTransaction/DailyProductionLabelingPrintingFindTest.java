package com.example.CostingTransaction;

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

public class DailyProductionLabelingPrintingFindTest extends Window
{
	private SessionBean sessionBean;
	private PopupDateField dFromDate;
	private PopupDateField dToDate;
	private ComboBox cmbProductionStep;
	private CheckBox chkFindAll;

	private Label lblFromDate;
	private Label lblToDate;
	private Label lblProductionStep;

	private NativeButton btnFind;

	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<Label> tblblProductionStep = new ArrayList<Label>();
	private ArrayList<Label> tblblProductionNo = new ArrayList<Label>();
	private ArrayList<Label> tblblProductionDate = new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();
	boolean isUpdate=false,isFind=false;
	private AbsoluteLayout mainLayout;
	TextField txtProductionNo=new TextField();
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

	public DailyProductionLabelingPrintingFindTest(SessionBean sessionBean,TextField txtProductionNo)
	{
		this.txtProductionNo=txtProductionNo;
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("DAILY PRODUCTION ENTRY LABELING/PRINTING FIND :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		btnAction();
		center();
		setModal(true);
		ProductionStepLoad();
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
	private void ProductionStepLoad() {
		cmbProductionStep.removeAllItems();
		Iterator iter=dbService("select 0,productionStep from tbLabelingPrintingDailyProductionInfo" +
				" where convert(date,productionDate,105) between '"+dateFormat.format(dFromDate.getValue())+"' " +
				"and '"+dateFormat.format(dToDate.getValue())+"'  order by productionStep ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionStep.addItem(element[1]);
			cmbProductionStep.setItemCaption(element[1], element[1].toString());
		}
	}

	public void btnAction()
	{
		chkFindAll.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(chkFindAll.booleanValue()){
					cmbProductionStep.setEnabled(false);
					cmbProductionStep.setValue(null);
				}
				else{
					cmbProductionStep.setEnabled(true);
				}
			}
		});
		dFromDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				ProductionStepLoad();
			}
		});
		dToDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				ProductionStepLoad();
			}
		});
		btnFind.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
			
				if(cmbProductionStep.getValue()!=null||chkFindAll.booleanValue()){
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
					String receiptAreaId = tblblProductionNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtProductionNo.setValue(receiptAreaId);
					close();
					
				}
			}
		});
	}

	private void tableDataLoad() {
		String productionStep="%";
		if(cmbProductionStep.getValue()!=null){
			productionStep=cmbProductionStep.getValue().toString();
		}
		Iterator iter=dbService("select productionStep,productionNo,convert(varchar(10),productionDate,103) from tbLabelingPrintingDailyProductionInfo  " +
				"where convert(date,productionDate,105) between '"+dateFormat.format(dFromDate.getValue())+"' and '"+dateFormat.format(dToDate.getValue())+"'" +
				" and productionStep like '"+productionStep+"' order by productionNo");
		int a=0;
		if(iter.hasNext()){
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				tblblProductionStep.get(a).setValue(element[0]);
				tblblProductionNo.get(a).setValue(element[1]);
				tblblProductionDate.get(a).setValue(element[2]);
				
				a++;
				if(a==tbLblSl.size()-1){
					tableRowAdd(a+1);
				}
			}
		}
		else{
			showNotification("Sorr!!","There are No Data",Notification.TYPE_WARNING_MESSAGE);
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

			tblblProductionStep.add(ar, new Label());
			tblblProductionStep.get(ar).setImmediate(true);
			tblblProductionStep.get(ar).setWidth("100%");
			tblblProductionStep.get(ar).setHeight("-1px");

			tblblProductionNo.add(ar, new Label());
			tblblProductionNo.get(ar).setImmediate(true);
			tblblProductionNo.get(ar).setWidth("100%");
			tblblProductionNo.get(ar).setHeight("-1px");


			tblblProductionDate.add(ar, new Label());
			tblblProductionDate.get(ar).setImmediate(true);
			tblblProductionDate.get(ar).setWidth("100%");
			tblblProductionDate.get(ar).setHeight("-1px");	


			table.addItem(new Object[]{tbLblSl.get(ar),	tblblProductionStep.get(ar),tblblProductionNo.get(ar),tblblProductionDate.get(ar)},ar);

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
		mainLayout.setWidth("460px");
		mainLayout.setHeight("400px");

		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate, "top:20.0px;left:40.0px;");

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
		mainLayout.addComponent(lblToDate, "top:46.0px;left:40.0px;");

		dToDate =new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setHeight("-1px");
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		mainLayout.addComponent(dToDate, "top:44.0px;left:140.0px;");

		lblProductionStep = new Label();
		lblProductionStep.setImmediate(false);
		lblProductionStep.setWidth("-1px");
		lblProductionStep.setHeight("-1px");
		lblProductionStep.setValue("Production Step :");
		mainLayout.addComponent(lblProductionStep, "top:74.0px;left:40.0px;");

		cmbProductionStep =new ComboBox();
		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setWidth("200px");
		cmbProductionStep.setHeight("-1px");
		mainLayout.addComponent(cmbProductionStep, "top:72.0px;left:140.0px;");

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

		table.addContainerProperty("Proudction Step",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Proudction Step",130);

		table.addContainerProperty("Production No",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Production No",80);

		table.addContainerProperty("Production Date",  Label.class , new  Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Production Date",110);

		mainLayout.addComponent(table, "top:134.0px;left:10.0px;");
		tableInitialise();

		setStyleName("cwindow");
		return mainLayout;
	}
}
