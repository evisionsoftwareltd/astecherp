package acc.appform.transportModule;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.*;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
public class DayOff extends Window{

	private CommonButton cButton = new CommonButton("New", "Save", "","", "Refresh", "Find", "", "", "", "Exit");
	private SessionBean sessionBean;
	private boolean isUpdate = false,isFind=false;
	private AbsoluteLayout mainLayout;
	ArrayList<Component> allComp = new ArrayList<Component>();	

	ComboBox cmbDepartment,cmbSectionName;
	PopupDateField dFromDate,dToDate;
	CheckBox chkAll;
	SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	HashMap<String,String> hMapDate=new HashMap<String,String>();

	private ArrayList<Label> tbSl = new ArrayList<Label>();
	private ArrayList<Label> tbEmployeeId = new ArrayList<Label>();
	private ArrayList<Label> tbEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tbDesignation = new ArrayList<Label>();
	private ArrayList<CheckBox> tbChkSaturday = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkSunday = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkMonday = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkThuesday = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkWednesday = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkThursday = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbChkFriday = new ArrayList<CheckBox>();
	Table table=new Table();

	public DayOff(SessionBean sessionBean){
		this.sessionBean = sessionBean;
		this.setCaption("DAY OFF :: "+this.sessionBean.getCompany());
		this.setResizable(false);
		this.center();

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
	private void txtClear(){
		cmbDepartment.setValue(null);
		cmbSectionName.setValue(null);
		chkAll.setValue(false);
		dFromDate.setValue(new Date());
		dToDate.setValue(new Date());
		hMapDate.clear();
	}
	private AbsoluteLayout buildMainLayout(){
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("1135px");
		setHeight("540px");

		cmbDepartment= new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("210px");
		cmbDepartment.setHeight("24px");
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department :"),"top:20px;left:20px;");
		mainLayout.addComponent(cmbDepartment, "top:18.0px;left:120.0px;");

		cmbSectionName= new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("210px");
		cmbSectionName.setHeight("24px");
		cmbSectionName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Section :"),"top:45px;left:20px;");
		mainLayout.addComponent(cmbSectionName, "top:43.0px;left:120.0px;");

		chkAll=new CheckBox("All");
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:45.0px;left:340.0px;");

		dFromDate = new PopupDateField();
		dFromDate.setWidth("120px");
		dFromDate.setHeight("-1px");
		dFromDate.setImmediate(true);
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("From Date : "),"top:20px;left:650px;");
		mainLayout.addComponent(dFromDate, "top:18.0px;left:740.0px;");

		dToDate = new PopupDateField();
		dToDate.setWidth("120px");
		dToDate.setHeight("-1px");
		dToDate.setImmediate(true);
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setEnabled(false);
		mainLayout.addComponent(new Label("To Date : "),"top:45px;left:650px;");
		mainLayout.addComponent(dToDate, "top:43.0px;left:740.0px;");

		table.setFooterVisible(true);
		table.setWidth("100%");
		table.setHeight("280px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 15);

		table.addContainerProperty("Employee Id", Label.class, new Label());
		table.setColumnWidth("Employee Id", 100);
		

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 200);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 100);

		table.addContainerProperty("SAT ", CheckBox.class, new CheckBox());
		table.setColumnWidth("SAT ", 75);
		table.setColumnAlignment("SAT ",Table.ALIGN_CENTER);
		
		table.addContainerProperty("Sun ", CheckBox.class, new CheckBox());
		table.setColumnWidth("Sun ", 75);
		table.setColumnAlignment("Sun ",Table.ALIGN_CENTER);
		
		table.addContainerProperty("Mon ", CheckBox.class, new CheckBox());
		table.setColumnWidth("Mon ", 80);
		table.setColumnAlignment("Mon ",Table.ALIGN_CENTER);
		
		table.addContainerProperty("Tue ", CheckBox.class, new CheckBox());
		table.setColumnWidth("Tue ", 75);
		table.setColumnAlignment("Tue ",Table.ALIGN_CENTER);
		
		table.addContainerProperty("Wed ", CheckBox.class, new CheckBox());
		table.setColumnWidth("Wed ", 80);
		table.setColumnAlignment("Wed ",Table.ALIGN_CENTER);
		
		table.addContainerProperty("Thu ", CheckBox.class, new CheckBox());
		table.setColumnWidth("Thu ", 75);
		table.setColumnAlignment("Thu ",Table.ALIGN_CENTER);
		
		table.addContainerProperty("Fri ", CheckBox.class, new CheckBox());
		table.setColumnWidth("Fri ", 75);
		table.setColumnAlignment("Fri ",Table.ALIGN_CENTER);
		
		mainLayout.addComponent(table,"top:100.0px;left:0.0px;");
		tableInitialize();

		Label lblLine = new Label("<b><font color='#e65100'>==============================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:400px;left:10px;");
		mainLayout.addComponent(cButton, "top:440px;left:350px;");

		return mainLayout;
	}
	private void tableInitialize(){
		for(int a=0;a<10;a++){
			tableRowAdd(a);
		}
	}
	private void tableRowAdd(final int ar)
	{
		tbSl.add(ar, new Label(""));
		tbSl.get(ar).setWidth("100%");
		tbSl.get(ar).setValue(ar+1);
		
		tbEmployeeId.add(ar, new Label(""));
		tbEmployeeId.get(ar).setWidth("100%");
		
		tbEmployeeName.add(ar, new Label(""));
		tbEmployeeName.get(ar).setWidth("100%");
		
		tbDesignation.add(ar, new Label(""));
		tbDesignation.get(ar).setWidth("100%");
		
		tbChkSaturday.add(ar,new CheckBox());
		tbChkSaturday.get(ar).setWidth("100%");
		tbChkSaturday.get(ar).setImmediate(true);
		
		tbChkSunday.add(ar,new CheckBox());
		tbChkSunday.get(ar).setWidth("100%");
		tbChkSunday.get(ar).setImmediate(true);
		
		tbChkMonday.add(ar,new CheckBox());
		tbChkMonday.get(ar).setWidth("100%");
		tbChkMonday.get(ar).setImmediate(true);
		
		tbChkThuesday.add(ar,new CheckBox());
		tbChkThuesday.get(ar).setWidth("100%");
		tbChkThuesday.get(ar).setImmediate(true);
		
		tbChkWednesday.add(ar,new CheckBox());
		tbChkWednesday.get(ar).setWidth("100%");
		tbChkWednesday.get(ar).setImmediate(true);
		
		tbChkThursday.add(ar,new CheckBox());
		tbChkThursday.get(ar).setWidth("100%");
		tbChkThursday.get(ar).setImmediate(true);
		
		tbChkFriday.add(ar,new CheckBox());
		tbChkFriday.get(ar).setWidth("100%");
		tbChkFriday.get(ar).setImmediate(true);
		
		table.addItem(new Object[]{tbSl.get(ar),tbEmployeeId.get(ar),tbEmployeeName.get(ar),tbDesignation.get(ar),
				tbChkSaturday.get(ar),tbChkSunday.get(ar),tbChkMonday.get(ar),tbChkThuesday.get(ar),
				tbChkWednesday.get(ar),tbChkThursday.get(ar),tbChkFriday.get(ar)},ar);
	}
	private void eventAction() {
		dFromDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(!simpleDateformat.format(dFromDate.getValue()).equalsIgnoreCase("SAT")){
					showNotification("From day must be start from Saturday.",Notification.TYPE_WARNING_MESSAGE);
					dFromDate.setValue(new java.util.Date());
					dToDate.setValue(new java.util.Date());
					
					//Reset Column Name of Table
					table.setColumnHeader("SAT ", "SAT ");
					table.setColumnHeader("Sun ", "Sun ");
					table.setColumnHeader("Mon ", "Mon ");
					table.setColumnHeader("Tue ", "Tue ");
					table.setColumnHeader("Wed ", "Wed ");
					table.setColumnHeader("Thu ", "Thu ");
					table.setColumnHeader("Fri ", "Fri ");
				}
				else{
					Date fromDate=(Date) dFromDate.getValue();
					Calendar cal=Calendar.getInstance();
					cal.setTime(fromDate);
					cal.add(cal.DAY_OF_MONTH, 6);
					Date d=new Date();
					d=cal.getTime();
					dToDate.setValue(d);
					setDateToHashMap();
				}
			}
		});
	}
	private void setDateToHashMap()
	{
		hMapDate.clear();
		Date fromDate=(Date) dFromDate.getValue();
		Date toDate=(Date) dToDate.getValue();

		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);

		while (cal.getTime().before(toDate)) {
			//Take First Six Day
			Date d=new Date();
			d=cal.getTime();
			hMapDate.put(simpleDateformat.format(d), dateFormat.format(d));
			cal.add(Calendar.DATE, 1);
		}
		//For Taking Last Day
		Date d=new Date();
		d=cal.getTime();
		hMapDate.put(simpleDateformat.format(d), dateFormat.format(d));
		
		//Change Column Name
		table.setColumnHeader("SAT ", "SAT "+hMapDate.get("Sat"));
		table.setColumnHeader("Sun ", "SUN "+hMapDate.get("Sun"));
		table.setColumnHeader("Mon ", "MON "+hMapDate.get("Mon"));
		table.setColumnHeader("Tue ", "TUE "+hMapDate.get("Tue"));
		table.setColumnHeader("Wed ", "WED "+hMapDate.get("Wed"));
		table.setColumnHeader("Thu ", "THU "+hMapDate.get("Thu"));
		table.setColumnHeader("Fri ", "FRI "+hMapDate.get("Fri"));
	}
	private void focusEnter() {
		//allComp.add(cmbLcNo);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}
	private void componentIni(boolean b) {

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
}
