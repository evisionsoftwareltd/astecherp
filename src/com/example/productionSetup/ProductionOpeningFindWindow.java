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

public class ProductionOpeningFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private NativeSelect cmbOpeningYear = new NativeSelect("Opening Year"); 
	private Table table=new Table();

	public String receiptProductId = "";
	private TextField txtReceiptrawid;
	private TextField txtReceipfinishedid;
	private TextField txttypeid;
	private TextField txtstepid;
	private TextField fgId;

	public String receiptOpeningYear = "";
	private TextField txtReceiptOpeningYear;

	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lblproductionType = new ArrayList<Label>();
	private ArrayList<Label> lblProductionTypeId = new ArrayList<Label>();
	private ArrayList<Label> lblproductionStep = new ArrayList<Label>();
	private ArrayList<Label> lblproductionstepid = new ArrayList<Label>();
	private ArrayList<Label> lbljoborder = new ArrayList<Label>();
	
	
	private ArrayList<Label> lblfinishedgoods = new ArrayList<Label>();
	private ArrayList<Label> lblfinishedgoodsid = new ArrayList<Label>();
	
	private ArrayList<Label> lblsemiFinishedGoods = new ArrayList<Label>();
	private ArrayList<Label> lblsemiFinishedGoodsID = new ArrayList<Label>();
	
	private ArrayList<Label> lblmouldName = new ArrayList<Label>();
	private ArrayList<Label> lblmouldNameId = new ArrayList<Label>();
	
	private ArrayList<Label> lblinputname = new ArrayList<Label>();
	private ArrayList<Label> lblinputId = new ArrayList<Label>();
	
	private ArrayList<Label> lbloutputname = new ArrayList<Label>();
	private ArrayList<Label> lbloutputId= new ArrayList<Label>();
	
	private ArrayList<Label> lbljobNo= new ArrayList<Label>();
	
	
	private TextField txttype;
	private TextField txtstep;
	private TextField txtjoborde;
	private TextField txtmouldid;
	private TextField txtfgId;
	private TextField txtsemifgId;
	private TextField txtinputid;
	private TextField txtoutputId;
	private TextField txtyear;
	private TextField txtjobno;
	
	

	private SessionBean sessionBean;
	public ProductionOpeningFindWindow(SessionBean sessionBean,TextField txttypeid,TextField txtstepid,TextField txtjoborder,TextField fgId,  TextField semifgId,TextField mouldid,TextField Inputid,TextField outputId,TextField year,TextField jobNo)
	{
		this.txttypeid = txttypeid;
		this.txtstepid = txtstepid;
		this.txtjoborde = txtjoborder;
		this.txtfgId=fgId;
		this.txtsemifgId=semifgId;
		this.txtmouldid=mouldid;
		this.txtinputid=Inputid;
		this.txtoutputId=outputId;
		this.txtyear=year;
		this.txtReceipfinishedid = txtReceipfinishedid;
		this.txtReceiptOpeningYear = txtReceiptOpeningYear;
		this.fgId=fgId;
		this.txtjobno=jobNo;
		this.sessionBean=sessionBean;
		this.setCaption("FIND OPENING STOCK INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("850px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		yearDataAdding();
		tableclear();
		
	}


	private void yearDataAdding(){
		cmbOpeningYear.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select Distinct  0,DATEPART(yyyy,openingYear)as Date from  tbProductionOpening";

			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[])iter.next();
				cmbOpeningYear.addItem(element[1]);
				
			}
		}
		catch(Exception exp){
			this.getParent().showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
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

		lblproductionType.add(ar, new Label());
		lblproductionType.get(ar).setWidth("100%");
		lblproductionType.get(ar).setImmediate(true);
		lblproductionType.get(ar).setHeight("23px");
		
		lblProductionTypeId.add(ar, new Label());
		lblProductionTypeId.get(ar).setWidth("100%");
		lblProductionTypeId.get(ar).setImmediate(true);
		lblProductionTypeId.get(ar).setHeight("23px");
		
		lblproductionStep.add(ar, new Label());
		lblproductionStep.get(ar).setWidth("100%");
		lblproductionStep.get(ar).setImmediate(true);
		lblproductionStep.get(ar).setHeight("23px");
		
		lblproductionstepid.add(ar, new Label());
		lblproductionstepid.get(ar).setWidth("100%");
		lblproductionstepid.get(ar).setImmediate(true);
		lblproductionstepid.get(ar).setHeight("23px");
		
		lbljoborder.add(ar, new Label());
		lbljoborder.get(ar).setWidth("100%");
		lbljoborder.get(ar).setImmediate(true);
		lbljoborder.get(ar).setHeight("23px");
		
		lblfinishedgoods.add(ar, new Label());
		lblfinishedgoods.get(ar).setWidth("100%");
		lblfinishedgoods.get(ar).setImmediate(true);
		lblfinishedgoods.get(ar).setHeight("23px");
		
		lblfinishedgoodsid.add(ar, new Label());
		lblfinishedgoodsid.get(ar).setWidth("100%");
		lblfinishedgoodsid.get(ar).setImmediate(true);
		lblfinishedgoodsid.get(ar).setHeight("23px");
		
		lblsemiFinishedGoods.add(ar, new Label());
		lblsemiFinishedGoods.get(ar).setWidth("100%");
		lblsemiFinishedGoods.get(ar).setImmediate(true);
		lblsemiFinishedGoods.get(ar).setHeight("23px");
		
		lblsemiFinishedGoodsID.add(ar, new Label());
		lblsemiFinishedGoodsID.get(ar).setWidth("100%");
		lblsemiFinishedGoodsID.get(ar).setImmediate(true);
		lblsemiFinishedGoodsID.get(ar).setHeight("23px");
		
		lblmouldName.add(ar, new Label());
		lblmouldName.get(ar).setWidth("100%");
		lblmouldName.get(ar).setImmediate(true);
		lblmouldName.get(ar).setHeight("23px");
		
		lblmouldNameId.add(ar, new Label());
		lblmouldNameId.get(ar).setWidth("100%");
		lblmouldNameId.get(ar).setImmediate(true);
		lblmouldNameId.get(ar).setHeight("23px");
		
		lblinputname.add(ar, new Label());
		lblinputname.get(ar).setWidth("100%");
		lblinputname.get(ar).setImmediate(true);
		lblinputname.get(ar).setHeight("23px");
		
		lblinputId.add(ar, new Label());
		lblinputId.get(ar).setWidth("100%");
		lblinputId.get(ar).setImmediate(true);
		lblinputId.get(ar).setHeight("23px");
		
		lbloutputname.add(ar, new Label());
		lbloutputname.get(ar).setWidth("100%");
		lbloutputname.get(ar).setImmediate(true);
		lbloutputname.get(ar).setHeight("23px");
		
		lbloutputId.add(ar, new Label());
		lbloutputId.get(ar).setWidth("100%");
		lbloutputId.get(ar).setImmediate(true);
		lbloutputId.get(ar).setHeight("23px");
		
		lbljobNo.add(ar, new Label());
		lbljobNo.get(ar).setWidth("100%");
		lbljobNo.get(ar).setImmediate(true);
		lbljobNo.get(ar).setHeight("23px");
		
		table.addItem(new Object[]{lbSL.get(ar),lblproductionType.get(ar),lblProductionTypeId.get(ar),lblproductionStep.get(ar), lblproductionstepid.get(ar),lbljoborder.get(ar),lblmouldName.get(ar),lblmouldNameId.get(ar),lblfinishedgoods.get(ar),lblfinishedgoodsid.get(ar),lblsemiFinishedGoods.get(ar),lblsemiFinishedGoodsID.get(ar),lblinputname.get(ar),lblinputId.get(ar),lbloutputname.get(ar),lbloutputId.get(ar),lbljobNo.get(ar)},ar);
	}

	public void setEventAction()
	{
		
		cmbOpeningYear.addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event)
			{
				
				
				Transaction tx=null;
				String sql=null;
				
				try
				{
					Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					tx=session.beginTransaction();
					
					if(cmbOpeningYear.getValue()!=null)
					{
						
						sql = "select productionType,b.productTypeName,a.productionStep,c.StepName,jobOrderNo,mouldId,mouldName,inputProductId,inputProductName,outputProductId,outputProductName,fgId,FgName,semiFgId,semiFgName,transactionNo from tbProductionOpening a "
							  +"inner join tbProductionType b on a.productionType=b.productTypeId inner join tbProductionStep c  "
							  +"on c.StepId=a.productionStep where YEAR(openingYear)='"+cmbOpeningYear.getValue()+"'  ";
						
						System.out.print("our Desire query is"+sql);
						
						List lst=session.createSQLQuery(sql).list();
						Iterator iter=lst.iterator();
						int a=lst.size();
						tableclear();
						if(!lst.isEmpty())
							
						{
							int i=0;
							while(a>0)
							{
								if(iter.hasNext())
								{
								 
									Object[]element=(Object[]) iter.next();
									
									lblProductionTypeId.get(i).setValue(element[0].toString());
									lblproductionType.get(i).setValue(element[1].toString());
									
									lblproductionstepid.get(i).setValue(element[2].toString());
									lblproductionStep.get(i).setValue(element[3].toString());
									
									lbljoborder.get(i).setValue(element[4].toString());
									
									lblmouldNameId.get(i).setValue(element[5].toString());
									lblmouldName.get(i).setValue(element[6].toString());
									
									lblfinishedgoods.get(i).setValue(element[12].toString());
									lblfinishedgoodsid.get(i).setValue(element[11].toString());
									
									lblsemiFinishedGoodsID.get(i).setValue(element[13].toString());
									lblsemiFinishedGoods.get(i).setValue(element[14].toString());
									
									lblinputId.get(i).setValue(element[7].toString());
									lblinputname.get(i).setValue(element[8].toString());
									
									lbloutputId.get(i).setValue(element[9].toString());
									lbloutputname.get(i).setValue(element[10].toString());
									
									lbljobNo.get(i).setValue(element[15].toString());
									
									
									if(i==lblProductionTypeId.size()-1)
									{
										tableRowAdd(i+1);	
									}
								
								}
								
								a--;
								i++;
							}
						
						}
						else
						{
						  showNotification("There is No Data Found",Notification.TYPE_WARNING_MESSAGE);	
						}
						
						
						
					}
				
				}
				catch(Exception ex)
				{
				  showNotification("Exception is"+ex,Notification.TYPE_ERROR_MESSAGE)	;
				}
			
			}
		});
		
		
		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					if(cmbOpeningYear.getValue()!=null)
					{
						
						
						
						String   typeid = lblProductionTypeId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					    String	 stepid = lblproductionstepid.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();;
						String   joborder = lbljoborder.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						String fgid   = lblfinishedgoodsid.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						String semifgId   = lblsemiFinishedGoodsID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						String mould=lblmouldNameId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						String input=lblinputId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						String output=lbloutputId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						String year  = cmbOpeningYear.getItemCaption(cmbOpeningYear.getValue());
						String transactionNo=lbljobNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						
						ProductionOpeningFindWindow.this.txttypeid.setValue(typeid);
						ProductionOpeningFindWindow.this.txtstepid.setValue(stepid);
						ProductionOpeningFindWindow.this.txtjoborde.setValue(joborder);
						ProductionOpeningFindWindow.this.txtfgId.setValue(fgid);
						
						ProductionOpeningFindWindow.this.txtsemifgId.setValue(semifgId);
						ProductionOpeningFindWindow.this.txtmouldid.setValue(mould);
						ProductionOpeningFindWindow.this.txtinputid.setValue(input);
						ProductionOpeningFindWindow.this.txtoutputId.setValue(output);
						ProductionOpeningFindWindow.this.txtyear.setValue(year);
						ProductionOpeningFindWindow.this.txtjobno.setValue(transactionNo);
						//ProductionOpeningFindWindow.this.fgId.setValue(fgid);
						

						windowClose();
					}
					else{
						getParent().showNotification("Select Opening Year",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblproductionType.size(); i++)
		{
			lblproductionType.get(i).setValue("");
			lblProductionTypeId.get(i).setValue("");
			lblproductionStep.get(i).setValue("");
			lblproductionstepid.get(i).setValue("");
			lbljoborder.get(i).setValue("");
			lblmouldNameId.get(i).setValue("");
			lblmouldName.get(i).setValue("");
			lblfinishedgoods.get(i).setValue("");
			lblfinishedgoodsid.get(i).setValue("");
			lblsemiFinishedGoods.get(i).setValue("");
			lblsemiFinishedGoodsID.get(i).setValue("");
			lblinputname.get(i).setValue("");
			lblinputId.get(i).setValue("");
			lbloutputId.get(i).setValue("");
			lbloutputname.get(i).setValue("");	
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private void compInit()
	{
		cmbOpeningYear.setWidth("100px");
		cmbOpeningYear.setImmediate(true);
		cmbOpeningYear.setNullSelectionAllowed(true);

		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("SL #", Label.class, new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Production Type", Label.class, new Label());
		table.setColumnWidth("Production Type",130);
		
		table.addContainerProperty("Production Type ID", Label.class, new Label());
		table.setColumnWidth("Production Type ID",65);
		table.setColumnCollapsed("Production Type ID", true);
		
		table.addContainerProperty("Production Step", Label.class, new Label());
		table.setColumnWidth("Production Step",130);
		
		table.addContainerProperty("Production Step ID", Label.class, new Label());
		table.setColumnWidth("Production Step ID",60);
		table.setColumnCollapsed("Production Step ID", true);
		
		
		table.addContainerProperty("Job Order", Label.class, new Label());
		table.setColumnWidth("Job Order",130);
		
		table.addContainerProperty("Mould Name", Label.class, new Label());
		table.setColumnWidth("Mould Name",130);
		
		table.addContainerProperty("Mould Id", Label.class, new Label());
		table.setColumnWidth("Mould Id",60);
		table.setColumnCollapsed("Mould Id", true);
		
		table.addContainerProperty("Finished Goods", Label.class, new Label());
		table.setColumnWidth("Finished Goods",130);
		
		table.addContainerProperty("Finished Goods Id", Label.class, new Label());
		table.setColumnWidth("Finished Goods Id",60);
		table.setColumnCollapsed("Finished Goods Id", true);
		
		
		table.addContainerProperty("Semi Finished Goods", Label.class, new Label());
		table.setColumnWidth("Semi Finished Goods",130);
		
		table.addContainerProperty("Semi Finished Goods Id", Label.class, new Label());
		table.setColumnWidth("Semi Finished Goods Id",60);
		table.setColumnCollapsed("Semi Finished Goods Id", true);
		
		table.addContainerProperty("Input Name", Label.class, new Label());
		table.setColumnWidth("Input Name",250);
		
		table.addContainerProperty("Input ID", Label.class, new Label());
		table.setColumnWidth("Input ID",60);
		table.setColumnCollapsed("Input ID", true);
		
		table.addContainerProperty("Output Name", Label.class, new Label());
		table.setColumnWidth("Output Name",250);
		
		table.addContainerProperty("Output ID", Label.class, new Label());
		table.setColumnWidth("Output ID",60);
		table.setColumnCollapsed("Output ID", true);
		
		table.addContainerProperty("JOB NO", Label.class, new Label());
		table.setColumnWidth("JOB NO",250);
		table.setColumnCollapsed("JOB NO", true);
		
	/*	table.addContainerProperty("Fg ID", Label.class, new Label());
		table.setColumnWidth("Fg ID",60);
		table.setColumnCollapsed("Fg ID", true);*/
		
	}

	private void compAdd()
	{
		cmbLayout.addComponent(cmbOpeningYear);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(table);
		mainLayout.addComponent(btnLayout);
		addComponent(mainLayout);
	}
}