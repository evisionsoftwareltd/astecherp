package acc.appform.FinishedGoodsModule;

import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.*;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class MasterProductOpeningFindWindow extends Window {

	SessionBean sessionBean;
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	Label lblSemiFgName,lblSemiFgSubName;
	ComboBox cmbSemiFgName,cmbSemiFgSubName;

	private ArrayList<Label> tbSl=new ArrayList<Label>();
	private ArrayList<Label> tbSemiFgSubId=new ArrayList<Label>();
	private ArrayList<Label> tbSemiFgSubName=new ArrayList<Label>();
	private ArrayList<PopupDateField> tbDate=new ArrayList<PopupDateField>();
	Table table=new Table();
	NativeButton btnFind=new NativeButton("FIND");
	String receiptItemId;

	public MasterProductOpeningFindWindow(SessionBean sessionBean,TextField txtItemId){
		this.sessionBean=sessionBean;
		this.txtItemID=txtItemId;
		this.setResizable(false);
		this.setModal(true);
		center();
		setStyleName("cwindow");
		this.setCaption("MASTER PRODUCT OPENING FIND :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		//tableDataLoad("%");
		semiFgSubLoad();
		setEventAction();
	}

	private void semiFgSubLoad() {
		cmbSemiFgSubName.removeAllItems();
		String sql="select fgCode,fgName from tbMasterProductOpening order by fgName";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbSemiFgSubName.addItem(element[0]);
			cmbSemiFgSubName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void setEventAction() {
		/*cmbProductionType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbProductionType.getValue()!=null){
					partyDataLoad(cmbProductionType.getValue().toString());
				}
				else{
					cmbSemiFgName.removeAllItems();
				}
			}
		});*/
		/*cmbSemiFgName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbSemiFgName.getValue()!=null){
					semiFgSubLoad(cmbSemiFgName.getValue().toString());
				}
				else{
					cmbSemiFgSubName.removeAllItems();
				}
			}
		});*/
		btnFind.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				btnFindEvent();
			}
		});
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptItemId = tbSemiFgSubId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println(receiptItemId);
					txtItemID.setValue(receiptItemId);
					close();
				}
			}
		});
	}

	private void btnFindEvent() {
		String semiFg="%",semiFgSub="%";
		/*if(cmbProductionType.getValue()!=null){
			type=cmbProductionType.getValue().toString();
		}*/
		/*if(cmbSemiFgName.getValue()!=null){
			semiFg=cmbSemiFgName.getValue().toString();
		}*/
		if(cmbSemiFgSubName.getValue()!=null){
			semiFgSub=cmbSemiFgSubName.getValue().toString();
		}
		tableDataLoad(  semiFgSub);
	}
	/*private void productionTypeLoad() {
		String sql="select distinct productionTypeId,(select productTypeName from tbProductionType where productTypeId=productionTypeId) from tbSemiFgInfo";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}
	}*/
	private void tableDataLoad(String semiFgSub){
		for(int a=0;a<tbSemiFgSubId.size();a++){
			tbSemiFgSubId.get(a).setValue("");
			tbSemiFgSubName.get(a).setValue("");
		}
		String sql="select fgCode,fgName from tbMasterProductOpening where fgCode like '"+semiFgSub+"'";
		System.out.println(sql);
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==tbSl.size()-1){
				tableRowAdd(a);
			}
			tbSemiFgSubId.get(a).setValue(element[0]);
			tbSemiFgSubName.get(a).setValue(element[1]);
			a++;
		}
	}
	private Iterator dbService(String sql){
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			return session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null||session!=null){
				session.close();
			}
		}
		return null;
	}
	private AbsoluteLayout buildMainLayout(){
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("480px");
		setHeight("300px");

		/*// lblProudctionType
		lblProductionType = new Label("Production Type: ");
		lblProductionType.setImmediate(true);
		lblProductionType.setWidth("100.0%");
		lblProductionType.setHeight("18px");

		// cmbProductionType
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbProductionType.setNullSelectionAllowed(true);*/

		// lblProudctionType
		/*lblSemiFgName = new Label("Semi Fg Name: ");
		lblSemiFgName.setImmediate(true);
		lblSemiFgName.setWidth("100.0%");
		lblSemiFgName.setHeight("18px");

		// cmbProductionType
		cmbSemiFgName = new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("310px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSemiFgName.setNullSelectionAllowed(true);*/

		// lblProudctionType
		lblSemiFgSubName = new Label("FG Name: ");
		lblSemiFgSubName.setImmediate(true);
		lblSemiFgSubName.setWidth("100.0%");
		lblSemiFgSubName.setHeight("18px");

		// cmbProductionType
		cmbSemiFgSubName = new ComboBox();
		cmbSemiFgSubName.setImmediate(true);
		cmbSemiFgSubName.setWidth("310px");
		cmbSemiFgSubName.setHeight("24px");
		cmbSemiFgSubName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSemiFgSubName.setNullSelectionAllowed(true);
		
		btnFind.setImmediate(true);
		btnFind.setWidth("100px");
		btnFind.setHeight("28px");
		
		table.setWidth("470px");
		table.setHeight("150px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);
		table.setSelectable(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",15);
		
		table.addContainerProperty("Semi Fg Sub ID", Label.class , new Label());
		table.setColumnWidth("Semi Fg Sub ID",100);
		table.setColumnCollapsed("Semi Fg Sub ID", true);
		
		table.addContainerProperty("Semi Fg Sub NAME", Label.class , new Label());
		table.setColumnWidth("Semi Fg Sub NAME",330);

		//table.addContainerProperty("Declare Date", PopupDateField.class , new PopupDateField());
		//table.setColumnWidth("Declare Date",120);
		tableInitialize();
		
		//mainLayout.addComponent(lblProductionType,"top:30.0px;left:70.0px;");
		//mainLayout.addComponent(cmbProductionType, "top:27.0px;left:170.0px;");
		
		/*mainLayout.addComponent(lblSemiFgName,"top:60.0px;left:30.0px;");
		mainLayout.addComponent(cmbSemiFgName, "top:57.0px;left:150.0px;");*/
		
		mainLayout.addComponent(lblSemiFgSubName,"top:30.0px;left:30.0px;");
		mainLayout.addComponent(cmbSemiFgSubName, "top:27.0px;left:150.0px;");
		
		mainLayout.addComponent(btnFind, "top:60.0px;left:150.0px;");
		
		mainLayout.addComponent(table, "top:90.0px;left:5.0px;");

		return mainLayout;
	}
	private void tableInitialize() {
		for(int a=0;a<10;a++){
			tableRowAdd(a);
		}
	}
	private void tableRowAdd(int ar){
		tbSl.add(ar,new Label());
		tbSl.get(ar).setWidth("100%");
		tbSl.get(ar).setHeight("25px");
		tbSl.get(ar).setValue(ar + 1);
		
		tbSemiFgSubId.add(ar,new Label());
		tbSemiFgSubId.get(ar).setWidth("100%");
		tbSemiFgSubId.get(ar).setHeight("25px");
		
		tbSemiFgSubName.add(ar,new Label());
		tbSemiFgSubName.get(ar).setWidth("100%");
		tbSemiFgSubName.get(ar).setHeight("25px");
		
		/*tbDate.add(ar,new PopupDateField());
		tbDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbDate.get(ar).setImmediate(true);
		tbDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDate.get(ar).setWidth("100%");
		tbDate.get(ar).setHeight("25px");*/
		
		table.addItem(new Object[]{tbSl.get(ar),tbSemiFgSubId.get(ar),tbSemiFgSubName.get(ar)}, ar);
	}
}
