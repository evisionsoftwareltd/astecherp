package com.example.productionTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class MouldProductionFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private FormLayout fLayout2 = new FormLayout();
	private TextField txtProdNo;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String prodNo = "";

	private Label lblProductionStep;
	private ComboBox cmbProductionStep = new ComboBox("Production Step :");
	private ComboBox cmbProductionType = new ComboBox("Production Type :");
	private PopupDateField dFromDate;
	private PopupDateField dToDate;

	private ArrayList<Label> lblProductionNo = new ArrayList<Label>();
	private ArrayList<Label> lblProductionDate = new ArrayList<Label>();
	private ArrayList<Label> lblIssueNo = new ArrayList<Label>();
	private ArrayList<Label> lblRawName = new ArrayList<Label>();
	private ArrayList<Label> lblProductionType = new ArrayList<Label>();
	private ArrayList<Label> lblStep = new ArrayList<Label>();

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private NativeButton findButton = new NativeButton("Search");
	private CheckBox chkAll = new CheckBox("All");

	private Label lblSpace = new Label();

	private SessionBean sessionBean;
	public MouldProductionFindWindow(SessionBean sessionBean,TextField txtProdNo)
	{
		this.txtProdNo = txtProdNo;
		this.sessionBean=sessionBean;
		this.setCaption("FIND MOULD PRODUCTION ENTRY INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("440px");
		this.setHeight("520px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		focusMove();
		tableInitialise();
		setEventAction();
		//cmbProductionStepData();
		cmbProudcitonTypeLoad();
		tableclear();
		cmbProductionStep.focus();

	}

	private void focusMove()
	{
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(cmbProductionStep);
		focusComp.add(dFromDate);
		focusComp.add(dToDate);
		focusComp.add(findButton);

		new FocusMoveByEnter(this, focusComp);
	}

	public void tableInitialise()
	{
		for(int i=0;i<7;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lblProductionNo.add(ar, new Label(""));
		lblProductionNo.get(ar).setWidth("100%");
		lblProductionNo.get(ar).setImmediate(true);
		lblProductionNo.get(ar).setHeight("23px");

		lblProductionDate.add(ar, new Label(""));
		lblProductionDate.get(ar).setWidth("100%");
		lblProductionDate.get(ar).setImmediate(true);
		lblProductionDate.get(ar).setHeight("23px");

		/*lblIssueNo.add(ar, new Label(""));
		lblIssueNo.get(ar).setWidth("100%");
		lblIssueNo.get(ar).setImmediate(true);
		lblIssueNo.get(ar).setHeight("23px");*/

		//		lblRawName.add(ar, new Label(""));
		//		lblRawName.get(ar).setWidth("100%");
		//		lblRawName.get(ar).setImmediate(true);
		//		lblRawName.get(ar).setHeight("23px");

		lblStep.add(ar, new Label(""));
		lblStep.get(ar).setWidth("100%");
		lblStep.get(ar).setImmediate(true);
		lblStep.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblProductionNo.get(ar),lblProductionDate.get(ar)/*,lblIssueNo.get(ar)/*,lblRawName.get(ar)*/,lblStep.get(ar)},ar);
	}

	public void setEventAction()
	{
		cmbProductionType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductionType.getValue()!=null){
					cmbProductionStepData();
				}
			}
		});
		chkAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{			
				if(chkAll.booleanValue()!=false)
				{
					tableclear();
					cmbProductionStep.setValue(null);
					cmbProductionStep.setEnabled(false);
				}
				else
				{
					tableclear();
					cmbProductionStep.setValue(null);
					cmbProductionStep.setEnabled(true);
				}
			}
		});

		findButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbProductionType.getValue()!=null){
					if(chkAll.booleanValue()!=false || cmbProductionStep.getValue()!=null)
					{
						tableDataAdding();
					}
					else
					{
						getParent().showNotification("Select Production Step",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					getParent().showNotification("Select Job Order No",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					prodNo = lblProductionNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println("Production No: "+prodNo);
					txtProdNo.setValue(prodNo);
					close();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblProductionNo.size(); i++)
		{
			lblProductionNo.get(i).setValue("");
			lblProductionDate.get(i).setValue("");
			lblStep.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		tableclear();
		String productionStep = "";
		if(chkAll.booleanValue()!=false)
		{
			productionStep = "%";
		}
		else
		{
			productionStep = cmbProductionStep.getValue().toString();
		}
		Iterator iter=dbService("select distinct a.ProductionNo,convert(varchar(20),a.ProductionDate,105)productionDate,a.Stepid,(select StepName from " +
				"tbProductionStep where StepId=a.Stepid)stepName from tbMouldProductionInfo a "+
				" inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo" +
				" where a.productionType='"+cmbProductionType.getValue()+"' and a.Stepid like '"+productionStep+"'" +
				"and convert(date,a.ProductionDate,105) between '"+new SimpleDateFormat("yyyy-MM-dd").format(dFromDate.getValue())+"' "
						+ "and '"+new SimpleDateFormat("yyyy-MM-dd").format(dToDate.getValue())+"' order by ProductionNo asc ");
		int ar=0;
		if(!iter.hasNext()){
			showNotification("Sorry!!","There is No Data",Notification.TYPE_WARNING_MESSAGE);
			tableclear();
		}
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			lblProductionNo.get(ar).setValue(element[0]);
			lblProductionDate.get(ar).setValue(element[1]);
			lblStep.get(ar).setValue(element[3]);
			if(ar==lblProductionNo.size()-1){
				tableRowAdd(ar+1);
			}
			ar++;
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private void cmbProductionStepData()
	{
		cmbProductionStep.removeAllItems();
		Iterator iter=dbService("select distinct a.Stepid,(select StepName from tbProductionStep where StepId=a.Stepid) from tbMouldProductionInfo a where a.productionType like '"+cmbProductionType.getValue()+"'");
				//" inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo where b.jobOrderNo='"+cmbProductionType.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionStep.addItem(element[0]);
			cmbProductionStep.setItemCaption(element[0], element[1].toString());
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
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null||session!=null){
				tx.commit();
				session.close();
			}
		}
		return null;
	}
	private void cmbProudcitonTypeLoad()
	{
		cmbProductionType.removeAllItems();
		Iterator iter=dbService(" select distinct productionType,(select productTypeName from tbProductionType where productTypeId=productionType) from tbMouldProductionInfo");
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}
	}
	private void compInit()
	{
		mainLayout.setSpacing(true);

		cmbProductionType.setImmediate(true);
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("-1px");
		cmbProductionType.setHeight("-1px");

		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setWidth("-1px");
		cmbProductionStep.setHeight("-1px");

		// dFromDate
		dFromDate = new PopupDateField("From :");
		dFromDate.setImmediate(false);
		dFromDate.setWidth("-1px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		// dToDate
		dToDate = new PopupDateField("To :");
		dToDate.setImmediate(false);
		dToDate.setWidth("-1px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);

		lblSpace.setHeight("20px");

		findButton.setWidth("80px");
		findButton.setHeight("28px");
		findButton.setIcon(new ThemeResource("../icons/find.png"));

		chkAll.setImmediate(true);

		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("275px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Prod. No", Label.class, new Label());
		table.setColumnWidth("Prod. No",50);
		table.setColumnAlignment("Prod. No", table.ALIGN_CENTER);

		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date",120);
		table.setColumnAlignment("Date", table.ALIGN_CENTER);

		/*//table.addContainerProperty("Issue No", Label.class, new Label());
		table.setColumnWidth("Issue No",60);
		table.setColumnAlignment("Issue No", table.ALIGN_CENTER);*/

		//		table.addContainerProperty("R/M Name", Label.class, new Label());
		//		table.setColumnWidth("R/M Name",315);
		
		table.addContainerProperty("Production Step", Label.class, new Label());
		table.setColumnWidth("Production Step",150);
		table.setColumnAlignment("Production Step", table.ALIGN_CENTER);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(false);
		cmbLayout.addComponent(cmbProductionType);
		cmbLayout.addComponent(cmbProductionStep);
		cmbLayout.addComponent(dFromDate);
		cmbLayout.addComponent(dToDate);
		cmbLayout.addComponent(lblSpace);
		cmbLayout.addComponent(findButton);
		fLayout2.addComponent(new Label());
		fLayout2.addComponent(new Label());
		fLayout2.addComponent(new Label());
		fLayout2.addComponent(chkAll);
		btnLayout.addComponent(cmbLayout);
		btnLayout.addComponent(fLayout2);
		//		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}