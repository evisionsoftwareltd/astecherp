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

public class SemiFgOpeningFindWindow extends Window {

	SessionBean sessionBean;
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	Label lblProductionType,lblPartyName,lblSemiFgName;
	ComboBox cmbProductionType,cmbPartyName,cmbSemiFgName;

	private ArrayList<Label> tbSl=new ArrayList<Label>();
	private ArrayList<Label> tbSemiFgId=new ArrayList<Label>();
	private ArrayList<Label> tbSemiFgName=new ArrayList<Label>();
	private ArrayList<Label> tbUnit=new ArrayList<Label>();
	private ArrayList<Label> tbColor=new ArrayList<Label>();
	private ArrayList<Label> tbStdWeight=new ArrayList<Label>();
	private ArrayList<InlineDateField> tbDate=new ArrayList<InlineDateField>();
	Table table=new Table();
	NativeButton btnFind=new NativeButton("FIND");
	String receiptItemId;

	public SemiFgOpeningFindWindow(SessionBean sessionBean,TextField txtItemId){
		this.sessionBean=sessionBean;
		this.txtItemID=txtItemId;
		this.setResizable(false);
		this.setModal(true);
		center();
		setStyleName("cwindow");
		this.setCaption("SEMI FINISH GOODS OPENING FIND :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		tableDataLoad("%","%");
		productionTypeLoad();
		setEventAction();
	}

	private void semiFgLoad(String type) {
		cmbSemiFgName.removeAllItems();
		String sql="select distinct semiFgCode,semiFgName,color from tbSemiFgOpening a where productionTypeId like '"+type+"'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbSemiFgName.addItem(element[0]);
			cmbSemiFgName.setItemCaption(element[0], element[1].toString()+" # "+element[2].toString());
		}
	}
	private void setEventAction() {
		cmbProductionType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbProductionType.getValue()!=null){
					//partyDataLoad(cmbProductionType.getValue().toString());
					semiFgLoad(cmbProductionType.getValue().toString());
				}
				else{
					cmbSemiFgName.removeAllItems();
				}
			}
		});
		/*cmbPartyName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbProductionType.getValue()!=null&&cmbPartyName.getValue()!=null){
					semiFgLoad(cmbProductionType.getValue().toString(),cmbPartyName.getValue().toString());
				}
				else{
					cmbSemiFgName.removeAllItems();
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
					receiptItemId = tbSemiFgId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println(receiptItemId);
					txtItemID.setValue(receiptItemId);
					close();
				}
			}
		});
	}

	private void btnFindEvent() {
		String type="%",party="%",semiFg="%";
		if(cmbProductionType.getValue()!=null){
			type=cmbProductionType.getValue().toString();
		}
		if(cmbSemiFgName.getValue()!=null){
			semiFg=cmbSemiFgName.getValue().toString();
		}
		System.out.println("Type: "+type+" Party: "+party+" semiFg: "+semiFg);
		tableDataLoad(type,semiFg);
	}
	private void productionTypeLoad() {
		String sql="select distinct productionTypeId,(select productTypeName from tbProductionType where productTypeId=productionTypeId) from tbSemiFgInfo where productionTypeId not like '%null%'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}
	}
	private void tableDataLoad(String type,String semiFg){
		for(int a=0;a<tbSemiFgId.size();a++){
			tbSemiFgId.get(a).setValue("");
			tbSemiFgName.get(a).setValue("");
			tbUnit.get(a).setValue("");
			tbColor.get(a).setValue("");
			tbStdWeight.get(a).setValue("");
			//tbDate.get(a).setValue(null);
		}
		String sql="select semiFgCode,semiFgName,unit,stdWeight,openingYear,color from tbSemiFgOpening" +
				" where productionTypeId like '"+type+"'  and semiFgCode like '"+semiFg+"'";
		System.out.println(sql);
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==tbSl.size()-1){
				tableRowAdd(a);
			}
			tbSemiFgId.get(a).setValue(element[0]);
			tbSemiFgName.get(a).setValue(element[1]);
			tbUnit.get(a).setValue(element[2]);
			tbStdWeight.get(a).setValue(element[3]);
		//	tbDate.get(a).setValue(element[4]);
			tbColor.get(a).setValue(element[5]);
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
		setWidth("600px");
		setHeight("500px");

		// lblProudctionType
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
		cmbProductionType.setNullSelectionAllowed(true);

		/*// lblProudctionType
		lblPartyName = new Label("Party Name: ");
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("100.0%");
		lblPartyName.setHeight("18px");

		// cmbProductionType
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("310px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbPartyName.setNullSelectionAllowed(true);*/

		// lblProudctionType
		lblSemiFgName = new Label("Semi Fg Name: ");
		lblSemiFgName.setImmediate(true);
		lblSemiFgName.setWidth("100.0%");
		lblSemiFgName.setHeight("18px");

		// cmbProductionType
		cmbSemiFgName = new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("310px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSemiFgName.setNullSelectionAllowed(true);
		
		btnFind.setImmediate(true);
		btnFind.setWidth("100px");
		btnFind.setHeight("28px");
		
		table.setWidth("590px");
		table.setHeight("295px");
		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);
		table.setSelectable(true);

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",15);
		
		table.addContainerProperty("Semi Fg ID", Label.class , new Label());
		table.setColumnWidth("Semi Fg ID",100);
		table.setColumnCollapsed("Semi Fg ID", true);
		
		table.addContainerProperty("Semi Fg NAME", Label.class , new Label());
		table.setColumnWidth("Semi Fg NAME",210);

		table.addContainerProperty("Unit", Label.class , new Label());
		table.setColumnWidth("Unit",50);

		table.addContainerProperty("Color", Label.class , new Label());
		table.setColumnWidth("Color",70);
		
		table.addContainerProperty("Std. Weight", Label.class , new Label());
		table.setColumnWidth("Std. Weight",80);

		tableInitialize();
		
		mainLayout.addComponent(lblProductionType,"top:30.0px;left:70.0px;");
		mainLayout.addComponent(cmbProductionType, "top:27.0px;left:170.0px;");
		
		//mainLayout.addComponent(lblPartyName,"top:60.0px;left:70.0px;");
		//mainLayout.addComponent(cmbPartyName, "top:57.0px;left:170.0px;");
		
		mainLayout.addComponent(lblSemiFgName,"top:60.0px;left:70.0px;");
		mainLayout.addComponent(cmbSemiFgName, "top:57.0px;left:170.0px;");
		
		mainLayout.addComponent(btnFind, "top:125.0px;left:150.0px;");
		
		mainLayout.addComponent(table, "top:155.0px;left:5.0px;");

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
		
		tbSemiFgId.add(ar,new Label());
		tbSemiFgId.get(ar).setWidth("100%");
		tbSemiFgId.get(ar).setHeight("25px");
		
		tbSemiFgName.add(ar,new Label());
		tbSemiFgName.get(ar).setWidth("100%");
		tbSemiFgName.get(ar).setHeight("25px");
		
		tbUnit.add(ar,new Label());
		tbUnit.get(ar).setWidth("100%");
		tbUnit.get(ar).setHeight("25px");
		
		tbColor.add(ar,new Label());
		tbColor.get(ar).setWidth("100%");
		tbColor.get(ar).setHeight("25px");
		
		tbStdWeight.add(ar,new Label());
		tbStdWeight.get(ar).setWidth("100%");
		tbStdWeight.get(ar).setHeight("25px");
		
		/*tbDate.add(ar,new InlineDateField());
		tbDate.get(ar).setDateFormat("yyyy");
		tbDate.get(ar).setImmediate(true);
		tbDate.get(ar).setResolution(InlineDateField.RESOLUTION_YEAR);
		tbDate.get(ar).setWidth("100%");
		tbDate.get(ar).setHeight("25px");*/
		
		table.addItem(new Object[]{tbSl.get(ar),tbSemiFgId.get(ar),tbSemiFgName.get(ar),tbUnit.get(ar),tbColor.get(ar),tbStdWeight.get(ar)}, ar);
	}
}
