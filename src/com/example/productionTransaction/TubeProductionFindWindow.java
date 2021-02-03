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

public class TubeProductionFindWindow extends Window
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
	private ComboBox cmbJobOrderNo = new ComboBox("Job Order No :");
	private PopupDateField dFromDate;
	private PopupDateField dToDate;

	private ArrayList<Label> lblProductionNo = new ArrayList<Label>();
	private ArrayList<Label> lblProductionDate = new ArrayList<Label>();
	private ArrayList<Label> lblIssueNo = new ArrayList<Label>();
	private ArrayList<Label> lblRawName = new ArrayList<Label>();
	private ArrayList<Label> lblStep = new ArrayList<Label>();

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private NativeButton findButton = new NativeButton("Search");
	private CheckBox chkAll = new CheckBox("All");
	
	private Label lblSpace = new Label();

	private SessionBean sessionBean;
	public TubeProductionFindWindow(SessionBean sessionBean,TextField txtProdNo)
	{
		this.txtProdNo = txtProdNo;
		this.sessionBean=sessionBean;
		this.setCaption("FIND TUBE PRODUCTION ENTRY INFO :: "+sessionBean.getCompany());
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
		//cmbJobOrderData();
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

		lblIssueNo.add(ar, new Label(""));
		lblIssueNo.get(ar).setWidth("100%");
		lblIssueNo.get(ar).setImmediate(true);
		lblIssueNo.get(ar).setHeight("23px");

		//		lblRawName.add(ar, new Label(""));
		//		lblRawName.get(ar).setWidth("100%");
		//		lblRawName.get(ar).setImmediate(true);
		//		lblRawName.get(ar).setHeight("23px");
		
		lblStep.add(ar, new Label(""));
		lblStep.get(ar).setWidth("100%");
		lblStep.get(ar).setImmediate(true);
		lblStep.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblProductionNo.get(ar),lblProductionDate.get(ar),lblIssueNo.get(ar)/*,lblRawName.get(ar)*/,lblStep.get(ar)},ar);
	}

	public void setEventAction()
	{
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
				if(cmbJobOrderNo.getValue()!=null){
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
					windowClose();
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
			lblIssueNo.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		tableclear();
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			String productionStep = "";
			
			if(chkAll.booleanValue()!=false)
			{
				productionStep = "%";
			}
			else
			{
				productionStep = cmbProductionStep.getValue().toString();
			}
			
			String query ="select ProductionNo, ProductionDate, IssueNo, rawItemName, b.StepName from tbTubeProductionInfo a" +
					" left join" +
					" (select StepId, StepName from tbProductionStep) b" +
					" on b.StepId = a.Stepid where a.Stepid like '"+productionStep+"' and convert(date,ProductionDate,105)" +
					" between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dToDate.getValue())+"'" +
					" and a.jobOrderNo like '"+cmbJobOrderNo.getValue().toString()+"' order by convert(date,ProductionDate,105)";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblProductionNo.get(i).setValue(element[0]);
					lblProductionDate.get(i).setValue(dateFormat.format(element[1]));
					lblIssueNo.get(i).setValue(element[2]);
					//					lblRawName.get(i).setValue(element[3]);
					lblStep.get(i).setValue(element[4]);
					if((i)==lblProductionNo.size()-1) {
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else {
				tableclear();
				this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) {
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private void cmbProductionStepData()
	{
		cmbProductionStep.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql = "select StepId, StepName from tbProductionStep where productionTypeId like 'PT-1'";
			System.out.print("cmbProductionStepData: "+sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbProductionStep.addItem(element[0].toString());
				cmbProductionStep.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}

	}
	private void cmbJobOrderData()
	{
		cmbJobOrderNo.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql = "select distinct jobOrderNo,0 from tbTubeProductionInfo";
			System.out.print("cmbJobOrderNo: "+sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbJobOrderNo.addItem(element[0].toString());
				cmbJobOrderNo.setItemCaption(element[0].toString(), element[0].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}

	}
	private void compInit()
	{
		mainLayout.setSpacing(true);

		cmbJobOrderNo.setImmediate(true);
		cmbJobOrderNo.setNullSelectionAllowed(true);
		cmbJobOrderNo.setImmediate(true);
		cmbJobOrderNo.setWidth("-1px");
		cmbJobOrderNo.setHeight("-1px");
		
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
		table.setColumnWidth("Date",70);
		table.setColumnAlignment("Date", table.ALIGN_CENTER);

		table.addContainerProperty("Issue No", Label.class, new Label());
		table.setColumnWidth("Issue No",60);
		table.setColumnAlignment("Issue No", table.ALIGN_CENTER);

		//		table.addContainerProperty("R/M Name", Label.class, new Label());
		//		table.setColumnWidth("R/M Name",315);
		
		table.addContainerProperty("Production Step", Label.class, new Label());
		table.setColumnWidth("Production Step",100);
		table.setColumnAlignment("Production Step", table.ALIGN_CENTER);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(false);
		cmbLayout.addComponent(cmbJobOrderNo);
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