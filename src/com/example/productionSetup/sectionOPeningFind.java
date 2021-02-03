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

public class sectionOPeningFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private NativeSelect cmbOpeningYear = new NativeSelect("Opening Year"); 
	private Table table=new Table();

	public  String receiptProductId = "";
	private TextField txtReceiptrawid;;
	private TextField txttypeid;;
	//private TextField txtsectionId;;
	public TextField receiptOpeningYear;;
	private TextField txtReceiptOpeningYear=new TextField();
	private java.text.SimpleDateFormat datef= new java.text.SimpleDateFormat("yyyy");
	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lblproductionType = new ArrayList<Label>();
	private ArrayList<Label> lblProductionTypeId = new ArrayList<Label>();
	private ArrayList<Label> lblsectionid = new ArrayList<Label>();
	private ArrayList<Label> lblsectionName = new ArrayList<Label>();
	private ArrayList<Label> lblrawmaterialName = new ArrayList<Label>();
	private ArrayList<Label> lblrawmaterialid = new ArrayList<Label>();
	
	private SessionBean sessionBean;
	public sectionOPeningFind(SessionBean sessionBean,TextField txttypeid,TextField txtrawid,TextField txtReceiptOpeningYear)
	{
		
		this.txttypeid=txttypeid;
		//this.txtsectionId=txtsectionId;
		this.txtReceiptrawid=txtrawid;
		this.txtReceiptOpeningYear = txtReceiptOpeningYear;
		this.sessionBean=sessionBean;
		this.setCaption("SECTION OPENING STOCK INFO :: "+sessionBean.getCompany());
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
		//yrarDataAdding();
		yearDataAdding();
		//tableclear();
		//tableDataAdding();
	}

	/*public void yrarDataAdding()
	{
		cmbOpeningYear.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("Select Distinct datepart(year,openingYear) as openingYear,datepart(year,openingYear) as openingYear from vwOpeningStockNew").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbOpeningYear.addItem(element[0]);
				cmbOpeningYear.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}*/
	private void yearDataAdding(){
		cmbOpeningYear.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			
			String sql="select Distinct  0,DATEPART(yyyy,openignYear)as Date from  tbsectionOpenignStock";
                         
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
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
		
	
		lblproductionType.add(ar, new Label(""));
		lblproductionType.get(ar).setWidth("100%");
		lblproductionType.get(ar).setImmediate(true);
		lblproductionType.get(ar).setHeight("23px");
		
		lblProductionTypeId.add(ar, new Label(""));
		lblProductionTypeId.get(ar).setWidth("100%");
		lblProductionTypeId.get(ar).setImmediate(true);
		lblProductionTypeId.get(ar).setHeight("23px");
		
	
		lblrawmaterialName.add(ar, new Label(""));
		lblrawmaterialName.get(ar).setWidth("100%");
		lblrawmaterialName.get(ar).setImmediate(true);
		lblrawmaterialName.get(ar).setHeight("23px");
		

		lblrawmaterialid.add(ar, new Label(""));
		lblrawmaterialid.get(ar).setWidth("100%");
		lblrawmaterialid.get(ar).setImmediate(true);
		lblrawmaterialid.get(ar).setHeight("23px");
		


		table.addItem(new Object[]{lbSL.get(ar),lblproductionType.get(ar),lblProductionTypeId.get(ar),lblrawmaterialName.get(ar), lblrawmaterialid.get(ar) , },ar);
	}

	public void setEventAction()
	{
		
		cmbOpeningYear.addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event)
			{
				
				tableclear();
				Transaction tx=null;
				String sql=null;
				
				try
				{
					Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					tx=session.beginTransaction();
					
					if(cmbOpeningYear.getValue()!=null)
					{
						sql= "select productionType as id,productionType productionTypeName,unit,qty,productid,productName from tbsectionOpenignStock where DATEPART(YEAR, openignYear) like  '"+cmbOpeningYear.getValue().toString()+"'  ";
						List lst=session.createSQLQuery(sql).list();
						Iterator iter=lst.iterator();
						int a=lst.size();
						
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
									lblrawmaterialid.get(i).setValue(element[4].toString());
									lblrawmaterialName.get(i).setValue(element[5].toString());
									
								}
								
								if(i==lblproductionType.size()-1)
								{
									 tableRowAdd(i+1);
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
						
					
						
						String   txttypeid = lblProductionTypeId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						String   txtReceiptrawid = lblrawmaterialid.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						String   txtReceiptOpeningYear = cmbOpeningYear.getItemCaption(cmbOpeningYear.getValue());
						
						System.out.println("type"+txttypeid);
						System.out.println("section"+txttypeid);
						System.out.println("raw"+txttypeid);
						System.out.println("opening"+txtReceiptOpeningYear);
						
						sectionOPeningFind.this.txttypeid.setValue(txttypeid);
						sectionOPeningFind.this.txtReceiptrawid.setValue(txtReceiptrawid);
						sectionOPeningFind.this.txtReceiptOpeningYear.setValue(txtReceiptOpeningYear);
						
						System.out.println("Done");
						

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
			lblrawmaterialid.get(i).setValue("");
			lblrawmaterialName.get(i).setValue("");
		}
	}

	private void windowClose(){
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
		
		table.addContainerProperty("Ram Material Name", Label.class, new Label());
		table.setColumnWidth("Ram Material Name",650);
		
		table.addContainerProperty("Ram Material Name ID", Label.class, new Label());
		table.setColumnWidth("Ram Material Name ID",60);
		table.setColumnCollapsed("Ram Material Name ID", true);
	
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