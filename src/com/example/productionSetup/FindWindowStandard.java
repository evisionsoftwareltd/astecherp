package com.example.productionSetup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import java.text.DecimalFormat;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
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
import com.vaadin.ui.Window.Notification;

public class FindWindowStandard extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private NativeSelect cmbOpeningYear = new NativeSelect("Opening Year"); 
	private Table table=new Table();

	public String receiptProductId = "";
	private TextField autoId;

	public String receiptOpeningYear = "";
	private TextField txtReceiptOpeningYear;

	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lbProductId = new ArrayList<Label>();
	private ArrayList<Label> lbProductCode = new ArrayList<Label>();
	private ArrayList<Label> lbProductName = new ArrayList<Label>();
	private ArrayList<Label> lbPerBatch = new ArrayList<Label>();
	private ArrayList<Label> lbPerCtn = new ArrayList<Label>();
//	private ArrayList<Label> lbWt = new ArrayList<Label>();
	private ArrayList<Label> lbDeclare = new ArrayList<Label>();
	private ArrayList<Label> lbRate = new ArrayList<Label>();
	
	private DecimalFormat df = new DecimalFormat("#0.00");
	private SimpleDateFormat dateFormat= new SimpleDateFormat("dd-MM-yyyy");

	private SessionBean sessionBean;
	public FindWindowStandard(SessionBean sessionBean,TextField autoId)
	{
		this.autoId = autoId;
		this.txtReceiptOpeningYear = txtReceiptOpeningYear;
		this.sessionBean=sessionBean;
		this.setCaption("FIND STANDARD :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("900px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		tableclear();
		tableDataAdding();
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

		lbProductId.add(ar, new Label(""));
		lbProductId.get(ar).setWidth("100%");
		lbProductId.get(ar).setImmediate(true);
		lbProductId.get(ar).setHeight("23px");

		lbProductName.add(ar, new Label(""));
		lbProductName.get(ar).setWidth("100%");
		lbProductName.get(ar).setImmediate(true);
		lbProductName.get(ar).setHeight("23px");
		
		lbPerBatch.add(ar, new Label(""));
		lbPerBatch.get(ar).setWidth("100%");
		lbPerBatch.get(ar).setImmediate(true);
		lbPerBatch.get(ar).setHeight("23px");
		
		lbPerCtn.add(ar, new Label(""));
		lbPerCtn.get(ar).setWidth("100%");
		lbPerCtn.get(ar).setImmediate(true);
		lbPerCtn.get(ar).setHeight("23px");
		
//		lbWt.add(ar, new Label(""));
//		lbWt.get(ar).setWidth("100%");
//		lbWt.get(ar).setImmediate(true);
//		lbWt.get(ar).setHeight("23px");
		
		lbDeclare.add(ar, new Label(""));
		lbDeclare.get(ar).setWidth("100%");
		lbDeclare.get(ar).setImmediate(true);
		lbDeclare.get(ar).setHeight("23px");



		table.addItem(new Object[]{lbSL.get(ar),lbProductId.get(ar),lbProductName.get(ar),lbPerBatch.get(ar),lbPerCtn.get(ar),lbDeclare.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
						receiptProductId = lbProductId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						autoId.setValue(receiptProductId);

						windowClose();
				}
			}
		});
		
	}

	private void tableDataAdding()
	{
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String selectQuery = "select vProductId, vProductName, mQty, vUnitName, dDeclaredDate from tbStandardFinishedInfo";

			System.out.println("selectQuery : "+selectQuery);
			List list = session.createSQLQuery(selectQuery).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbProductId.get(i).setValue(element[0]);
					lbProductName.get(i).setValue(element[1]);
					lbPerBatch.get(i).setValue(df.format(element[2]));
					lbPerCtn.get(i).setValue(element[3]);
//					lbWt.get(i).setValue(df.format(element[5]));
					lbDeclare.get(i).setValue(dateFormat.format(element[4]));

					/*if(!element[5].toString().equals("")){
						lbCategory.get(i).setValue(element[6]);
					}
					else{
						lbCategory.get(i).setValue("");
					}

					if(!element[8].toString().equals("")){
						lbSubCategory.get(i).setValue(element[9]);
					}
					else{
						lbSubCategory.get(i).setValue("");
					}

					lbUnit.get(i).setValue(element[11]);*/

					if((i)==lbProductId.size()-1) {
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

	private void tableclear()
	{
		for(int i=0; i<lbProductId.size(); i++)
		{
			lbProductId.get(i).setValue("");

			lbProductName.get(i).setValue("");
		}
	}

	private void windowClose(){
		this.close();
	}

	private void compInit()
	{

		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#",20);
		table.setColumnAlignment("SL#", table.ALIGN_CENTER);

		table.addContainerProperty("ID", Label.class, new Label());
		table.setColumnWidth("ID",20);
		table.setColumnCollapsed("ID", true);
		table.setColumnAlignment("ID", table.ALIGN_CENTER);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name",250);
		
		table.addContainerProperty("Qty", Label.class, new Label());
		table.setColumnWidth("Qty",100);
		table.setColumnAlignment("Qty", table.ALIGN_CENTER);
		
		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",100);
		table.setColumnAlignment("Unit", table.ALIGN_CENTER);
		
//		table.addContainerProperty("Process Loss", Label.class, new Label());
//		table.setColumnWidth("Process Loss",100);
//		table.setColumnAlignment("Process Loss", table.ALIGN_CENTER);
		
		table.addContainerProperty("Decleration Date", Label.class, new Label());
		table.setColumnWidth("Decleration Date",100);
		table.setColumnAlignment("Decleration Date", table.ALIGN_CENTER);

	}

	private void compAdd()
	{
		mainLayout.addComponent(table);
		mainLayout.addComponent(btnLayout);
		addComponent(mainLayout);
	}
}