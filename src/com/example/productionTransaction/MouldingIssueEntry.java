package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.naming.java.javaURLContextFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class MouldingIssueEntry extends Window {

	SessionBean sessionBean;

	private AbsoluteLayout mainLayout;

	private Panel searchPanel;
	private Label lblSearchPanel= new Label();
	private FormLayout frmLayout = new FormLayout();

	private PopupDateField dFromDate;
	private PopupDateField dToDate;
	private ComboBox cmbIssueFromFind = new ComboBox("From :");
	private ComboBox cmbIssueToFind = new ComboBox("To :");
	private ComboBox cmbTypeFind = new ComboBox("Production Type:");

	private NativeButton findButton = new NativeButton("Search");

	Table tableFind = new Table();

	ArrayList <Label> lblFindJobNo=new ArrayList<Label>();
	ArrayList <Label> lblFindChallanNo=new ArrayList<Label>();
	ArrayList <Label> lblFindJobDate=new ArrayList<Label>();



	private  Label lbljobNo=new Label("Job No :");
	private TextRead txtJobNo=new TextRead();

	private Label lbljobDate=new Label("Job Date :");
	private PopupDateField dJobDate=new PopupDateField();

	private  Label lblchaallanNo=new Label("Challan No :");
	private TextField txtChallanNo =new TextField();

	private Label lblFloorStock= new Label();
	private TextRead txtFloorStock = new TextRead();

	private Label lblFloorpcs= new Label();
	private TextRead txtFloorStockpcs = new TextRead();

	private Label lblMouldStock= new Label();
	private TextRead txtMouldStock = new TextRead();

	private Label lblMouldpcs= new Label(); 
	private TextRead txtMouldStockPcs = new TextRead();


	private ArrayList<Label>lblSl = new ArrayList<Label>();
	private ArrayList<ComboBox>cmbfrom = new ArrayList<ComboBox>();
	private ArrayList<ComboBox>cmbTo = new ArrayList<ComboBox>();
	private ArrayList<ComboBox>cmbproductionType = new ArrayList<ComboBox>();
	private ArrayList<ComboBox>cmbjoborder = new ArrayList<ComboBox>();
	private ArrayList<ComboBox>cmbsemiFgName = new ArrayList<ComboBox>();
	private ArrayList<TextRead>txtunit = new ArrayList<TextRead>(1);
	private ArrayList<TextField>txtissueQty = new ArrayList<TextField>();
	private ArrayList<AmountField>txtissuePcs = new ArrayList<AmountField>();
	private ArrayList<TextField>txtremarks= new ArrayList<TextField>();
	private ArrayList<TextRead>txtmouldStock= new ArrayList<TextRead>(1);

	private ArrayList<AmountField> txtSectinIssuePcs = new ArrayList<AmountField>();
	private ArrayList<AmountField>txtWastageIssue= new ArrayList<AmountField>();


	private Table tableRM = new Table();

	private DecimalFormat decf=new DecimalFormat("#0.00");
	private DecimalFormat decf1=new DecimalFormat("#0.000");
	private DecimalFormat dfInteger=new DecimalFormat("#0");
	private DecimalFormat decimalf = new DecimalFormat("#0.00");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat df1 = new DecimalFormat("#0");
	private SimpleDateFormat dFormat = new SimpleDateFormat("dd-MM-yyyy");


	private  Label lblFinishedGoods=new Label("<font color='blue' size='4px'><b>Finished Goods</b></font>", Label.CONTENT_XHTML);


	private ArrayList<Label>lblsl2 = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbFinishGoods = new ArrayList<ComboBox>();
	private ArrayList<TextRead> txtFgUnit = new ArrayList<TextRead>(1);	

	private Label lbLine=new Label("<font color='#e65100'>===============================================================================================================================================================================================================================================================</font>", Label.CONTENT_XHTML);
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");

	private HashMap hRate = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();

	private TextField txtReceiptId=new TextField();
	Double ttlissue=0.00;
	Double totalissue=0.00;
	private PopupDateField dateField = new PopupDateField();

	private Label amountWordLabel = new Label("Amount In Words: ");
	private Label totalLabel = new Label("Total  : ");
	private Label lbllank= new Label();
	private TextField amountWordsField = new TextField();

	private TextRead totalField = new TextRead(1);

	private Label label = new Label();
	private Label l1 = new Label();
	private Label l2 = new Label();
	boolean isUpdate=false,isFind=false;
	private Formatter fmt;


	private ReportDate reportTime = new ReportDate();

	OptionGroup Loantype;
	private static final List<String>areatype  = Arrays.asList(new String[] {"Production" });


	double stdQty=0.0;

	public MouldingIssueEntry(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption(" ISSUE TO LABEL OR PRINTING :::"+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		componentIni(true);
		searchPanel.setEnabled(false);
		btnIni(true);
		tableFindClear();
		tableRMInitiaLize();
		tableFindInitialize();
		setEventAction();
		focusMove();
	}

	private Iterator dbService(String sql)
	{
		Transaction tx=null;
		Session session=null;
		try
		{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			return session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			if(tx!=null||session!=null)
			{
				tx.commit();
				session.close();
			}
		}
		return null;
	}


	private AbsoluteLayout buildMainLayout()
	{

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("1250px");
		mainLayout.setHeight("550px");
		mainLayout.setMargin(false);

		lbljobNo.setWidth("-1px");
		lbljobNo.setHeight("-1px");
		lbljobNo.setImmediate(false);	

		txtJobNo.setWidth("100px");
		txtJobNo.setHeight("24px");
		txtJobNo.setImmediate(true);

		lbljobDate.setWidth("-1px");
		lbljobDate.setHeight("-1px");
		lbljobDate.setImmediate(false);

		dJobDate = new PopupDateField();
		dJobDate.setImmediate(true);
		dJobDate.setWidth("107px");
		dJobDate.setDateFormat("dd-MM-yyyy");
		dJobDate.setValue(new java.util.Date());
		dJobDate.setResolution(PopupDateField.RESOLUTION_DAY);		

		lblchaallanNo.setWidth("-1px");
		lblchaallanNo.setHeight("-1px");
		lblchaallanNo.setImmediate(false);		

		txtChallanNo.setWidth("100px");
		txtChallanNo.setHeight("24px");
		txtChallanNo.setImmediate(true);

		lblFloorStock.setWidth("-1px");
		lblFloorStock.setHeight("-1px");
		lblFloorStock.setImmediate(false);		

		txtFloorStock.setWidth("100px");
		txtFloorStock.setHeight("24px");
		txtFloorStock.setImmediate(true);

		lblMouldStock.setWidth("-1px");
		lblMouldStock.setHeight("-1px");
		lblMouldStock.setImmediate(false);		

		txtMouldStock.setWidth("100px");
		txtMouldStock.setHeight("24px");
		txtMouldStock.setImmediate(true);

		lblMouldpcs.setWidth("-1px");
		lblMouldpcs.setHeight("-1px");
		lblMouldpcs.setImmediate(false);		

		txtMouldStockPcs.setWidth("100px");
		txtMouldStockPcs.setHeight("24px");
		txtMouldStockPcs.setImmediate(true);

		// tableRM
		tableRM.setWidth("1050px");
		tableRM.setHeight("250px");
		tableRM.setColumnCollapsingAllowed(true);
		tableRM.setFooterVisible(true);

		tableRM.addContainerProperty("SL", Label.class , new Label());
		tableRM.setColumnWidth("SL",15);

		tableRM.addContainerProperty("PRODUCTION TYPE", ComboBox.class , new ComboBox());
		tableRM.setColumnWidth("PRODUCTION TYPE",120);

		tableRM.addContainerProperty("FROM [STORE]", ComboBox.class , new ComboBox());
		tableRM.setColumnWidth("FROM [STORE]",120);

		tableRM.addContainerProperty("TO", ComboBox.class , new ComboBox());
		tableRM.setColumnWidth("TO",120);

		//tableRM.addContainerProperty("JOB ORDER", ComboBox.class , new ComboBox());
		//tableRM.setColumnWidth("JOB ORDER",138);

		tableRM.addContainerProperty("SEMI FINISHED GOODS", ComboBox.class , new ComboBox());
		tableRM.setColumnWidth("SEMI FINISHED GOODS",220);

		tableRM.addContainerProperty("UNIT", TextRead.class , new TextRead(1));
		tableRM.setColumnWidth("UNIT",50);

		tableRM.addContainerProperty("STOCK PCS", TextRead.class , new TextRead(1));
		tableRM.setColumnWidth("STOCK PCS",70);

		tableRM.addContainerProperty("ISSUE PCS", AmountField.class , new AmountField());
		tableRM.setColumnWidth("ISSUE PCS",70);

		tableRM.addContainerProperty("REMARKS", TextField.class , new TextField());
		tableRM.setColumnWidth("REMARKS",125);



		dFromDate = new PopupDateField("From Date :");
		dFromDate.setImmediate(false);
		dFromDate.setWidth("-1px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		// dToDate
		dToDate = new PopupDateField("To Date :");
		dToDate.setImmediate(false);
		dToDate.setWidth("-1px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);

		cmbIssueFromFind.setWidth("250px");
		cmbIssueFromFind.setNewItemsAllowed(true);
		cmbIssueFromFind.setNullSelectionAllowed(true);
		cmbIssueFromFind.setImmediate(true);	

		cmbIssueToFind.setWidth("250px");
		cmbIssueToFind.setNewItemsAllowed(true);
		cmbIssueToFind.setNullSelectionAllowed(true);
		cmbIssueToFind.setImmediate(true);

		cmbTypeFind.setWidth("250px");
		cmbTypeFind.setNewItemsAllowed(true);
		cmbTypeFind.setNullSelectionAllowed(true);
		cmbTypeFind.setImmediate(true);

		// findButton
		findButton.setWidth("80px");
		findButton.setHeight("28px");
		findButton.setIcon(new ThemeResource("../icons/find.png"));

		// tableFind
		tableFind.setSelectable(true);
		tableFind.setWidth("400px");
		tableFind.setHeight("160px");
		tableFind.setColumnCollapsingAllowed(true);

		tableFind.addContainerProperty("JOB NO", Label.class, new Label());
		tableFind.setColumnWidth("JOB NO", 40);
		tableFind.setColumnAlignment("JOB NO", tableFind.ALIGN_CENTER);
		//tableFind.setColumnCollapsed("JOB NO", true);

		tableFind.addContainerProperty("Challan No", Label.class, new Label());
		tableFind.setColumnWidth("Challan No", 200);
		tableFind.setColumnAlignment("Challan No", tableFind.ALIGN_CENTER);

		tableFind.addContainerProperty("JOB DATE", Label.class, new Label());
		tableFind.setColumnWidth("JOB DATE", 100);
		tableFind.setColumnAlignment("JOB DATE", tableFind.ALIGN_CENTER);

		tableFind.setImmediate(true); // react at once when something is selected
		tableFind.setColumnReorderingAllowed(true);
		tableFind.setColumnCollapsingAllowed(true);	

		HorizontalLayout serchLayout=new HorizontalLayout();

		frmLayout.addComponent(dFromDate);
		frmLayout.addComponent(dToDate);
		frmLayout.addComponent(cmbTypeFind);
		frmLayout.addComponent(cmbIssueFromFind);
		frmLayout.addComponent(cmbIssueToFind);
		frmLayout.addComponent(findButton);


		serchLayout.setSpacing(true);
		serchLayout.addComponent(frmLayout);
		serchLayout.addComponent(tableFind);

		searchPanel = new Panel();
		searchPanel.setImmediate(true);
		searchPanel.setWidth("790px");
		searchPanel.setHeight("210px");
		searchPanel.setStyleName("radius");

		searchPanel.addComponent(serchLayout);


		lblSearchPanel = new Label("<font color='blue' size='4px'><b>Search Panel</b></font>", Label.CONTENT_XHTML);
		lblSearchPanel.setWidth("-1px");
		lblSearchPanel.setHeight("-1px");
		lblSearchPanel.setImmediate(false);	

		mainLayout.addComponent(lbljobNo,"top:15.0px;left:10.0px;");
		mainLayout.addComponent(txtJobNo,"top:13.0px;left:80.0px");

		mainLayout.addComponent(lbljobDate,"top:45.0px;left:10.0px;");
		mainLayout.addComponent(dJobDate,"top:43.0px;left:80.0px");

		mainLayout.addComponent(lblchaallanNo,"top:75.0px;left:10.0px;");
		mainLayout.addComponent(txtChallanNo,"top:73.0px;left:80.0px");

		mainLayout.addComponent(tableRM,"top:15.0px;left:195.0px;");

		mainLayout.addComponent(searchPanel,"top:280.0px;left:50px;");
		mainLayout.addComponent(lblSearchPanel,"top:285.0px;left:170.0px;");

		mainLayout.addComponent(lbLine,"top:495px;left:0.0px;");
		mainLayout.addComponent(cButton,"top:510px;left:250px;");

		return mainLayout;	
	}

	private void tableRowAdd(int ar,String issNo,String issDate,String challanNo){

		lblFindJobNo.add(ar,new Label());
		lblFindJobNo.get(ar).setWidth("100%");
		lblFindJobNo.get(ar).setImmediate(true);
		lblFindJobNo.get(ar).setValue(issNo);

		lblFindChallanNo.add(ar,new Label());
		lblFindChallanNo.get(ar).setWidth("100%");
		lblFindChallanNo.get(ar).setImmediate(true);
		lblFindChallanNo.get(ar).setValue(challanNo);

		lblFindJobDate.add(ar,new Label());
		lblFindJobDate.get(ar).setWidth("100%");
		lblFindJobDate.get(ar).setImmediate(true);
		lblFindJobDate.get(ar).setValue(issDate);

		tableFind.addItem(new Object[]{lblFindJobNo.get(ar),lblFindChallanNo.get(ar),lblFindJobDate.get(ar)},ar);
	}

	private void tableFindClear()
	{
		dFromDate.setValue(new java.util.Date());
		dToDate.setValue(new java.util.Date());
		cmbIssueFromFind.setValue(null);
		cmbIssueToFind.setValue(null);
		tableFind.removeAllItems();
	}

	/*private void searchPanelIni(boolean b)
	{
		lblSearchPanel.setEnabled(!b);
		searchPanel.setEnabled(!b);
	}*/

	private void focusMove()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();

		allComp.add(dJobDate);
		allComp.add(txtChallanNo);

		for(int i=0;i<cmbfrom.size();i++)
		{
			allComp.add(cmbproductionType.get(i));
			allComp.add(cmbfrom.get(i));
			allComp.add(cmbTo.get(i));
			//allComp.add(cmbjoborder.get(i));
			allComp.add(cmbsemiFgName.get(i));
			allComp.add(txtissuePcs.get(i));
			allComp.add(txtremarks.get(i));
		}
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);

		new FocusMoveByEnter(this,allComp);
	}

	private void FindTypeData() 
	{
		cmbTypeFind.removeAllItems();
		String sql= "select distinct   productTypeId,productTypeName from tbProductionType order by productTypeName";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbTypeFind.addItem(element[0]);
			cmbTypeFind.setItemCaption(element[0], element[1].toString());
		}
	}
	/*private void issueToDataLoad() 
	{
		cmbIssueTo.removeAllItems();
		String sql="select StepId,StepName from tbProductionStep where productionTypeId like '"+cmbProductionType.getValue()+"' and StepId not like '"+cmbIssueFrom.getValue()+"'";

		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbIssueTo.addItem(element[0]);
			cmbIssueTo.setItemCaption(element[0], element[1].toString());
		}
	}	*/



	private void txtclear()
	{

		txtJobNo.setValue("");
		dJobDate.setValue(new java.util.Date());
		txtChallanNo.setValue("");

		for(int a=0;a<lblSl.size();a++)
		{

			
			cmbTo.get(a).setValue(null);
			//cmbjoborder.get(a).setValue(null);
			
			cmbsemiFgName.get(a).setValue(null);
			cmbfrom.get(a).setValue(null);
			txtunit.get(a).setValue("");
			txtmouldStock.get(a).setValue("");
			txtissuePcs.get(a).setValue("");
			txtremarks.get(a).setValue("");
			cmbproductionType.get(a).setValue(null);
			cmbfrom.get(a).setValue(null);
		}
	}


	private void insertData()
	{

		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String itemType = "";
		String jobNo = "";
		String name="";
		String query1="";
		Double totalIssue=0.00;
		Double ttlIssue=0.00;

		try
		{

			String sqlnew= "insert into tbMouldIssueToProcessInfo(jobNo,jobDate,challanNo,userName,userIp,entryTime,reqNo) "
					+"values ('"+txtJobNo.getValue().toString()+"','"+dateFormat.format(dJobDate.getValue())+"','"+txtChallanNo.getValue()+"',"
					+ " '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate(),'' ) ";




			System.out.println(sqlnew);

			for (int i = 0; i < lblSl.size(); i++)
			{
				if (!txtissuePcs.get(i).getValue().toString().isEmpty())
				{

					String querynew="insert into tbMouldIssueToProcessDetails (jobNo,productionTypeId,IssueFrom,IssueTo,semiFgId,semiFgName,sectionIssuePcs,Remarks) "
							+"values('"+txtJobNo.getValue().toString()+"','"+cmbproductionType.get(i).getValue().toString()+"',"
							+ " '"+cmbfrom.get(i).getValue().toString()+"','"+cmbTo.get(i).getValue().toString()+"','"+cmbsemiFgName.get(i).getValue().toString()+"' ,"
							+ " '"+cmbsemiFgName.get(i).getItemCaption(cmbsemiFgName.get(i).getValue())+"','"+txtissuePcs.get(i).getValue().toString()+"','"+txtremarks.get(i).getValue().toString()+"' ) ";


					System.out.println("Altufaltu");
					session.createSQLQuery(querynew).executeUpdate();

					if(i==0)
					{
						session.createSQLQuery(sqlnew).executeUpdate();
					}
				}
			}

			tx.commit();
			this.getParent().showNotification("All information is saved successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error is"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void saveButtonEvent() 
	{
		if(!txtChallanNo.getValue().toString().isEmpty())
		{
			if(cmbproductionType.get(0).getValue()!=null)
			{
				if(cmbfrom.get(0).getValue()!=null)
				{
					if(cmbTo.get(0).getValue()!=null)
					{
						if(cmbproductionType.get(0).getValue()!=null)
						{
							if(cmbsemiFgName.get(0).getValue()!=null)
							{
								if(!txtissuePcs.get(0).getValue().toString().isEmpty())
								{
									if (isUpdate) 
									{
										MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
										mb.show(new EventListener() 
										{
											public void buttonClicked(ButtonType buttonType) 
											{
												if (buttonType == ButtonType.YES) 
												{
													Transaction tx = null;
													Session session = SessionFactoryUtil.getInstance().getCurrentSession();

													tx = session.beginTransaction();

													if (deleteData(session, tx))
													{
														insertData();
													}
													else 
													{
														tx.rollback();
													}
													componentIni(true);
													searchPanel.setEnabled(false);
													btnIni(true);
													txtclear();
													tableFindClear();
													isFind=false;
													isUpdate=false;
												}
											}
										});
									} 
									else
									{
										MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
										mb.show(new EventListener() 
										{
											public void buttonClicked(ButtonType buttonType) 
											{
												if(buttonType == ButtonType.YES)
												{
													insertData();
													componentIni(true);
													btnIni(true);
													txtclear();
													//tableFindClear();
												}
											}
										});
									}		
								}
								else
								{
									this.getParent().showNotification("Warning :","Please Provide Issue Pcs",Notification.TYPE_WARNING_MESSAGE);
									cmbsemiFgName.get(0).focus();	
								}


							}
							else
							{
								this.getParent().showNotification("Warning :","Please Select Semi Finished Goods Name",Notification.TYPE_WARNING_MESSAGE);
								cmbsemiFgName.get(0).focus();		
							}

						}
						else
						{
							this.getParent().showNotification("Warning :","Please Provide Production Type",Notification.TYPE_WARNING_MESSAGE);
							cmbjoborder.get(0).focus();		
						}

					}

					else
					{
						this.getParent().showNotification("Warning :","Please Select IssueTO ",Notification.TYPE_WARNING_MESSAGE);
						cmbTo.get(0).focus();		
					}

				}

				else
				{
					this.getParent().showNotification("Warning :","Please Select IssueFrom ",Notification.TYPE_WARNING_MESSAGE);
					cmbfrom.get(0).focus();	
				}

			}

			else
			{
				this.getParent().showNotification("Warning :","Please Production Type",Notification.TYPE_WARNING_MESSAGE);
				cmbproductionType.get(0).focus();
			}

		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Challan No",Notification.TYPE_WARNING_MESSAGE);
			txtChallanNo.focus();
		}
	}

	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete tbMouldIssueToProcessInfo where jobNo='"+txtJobNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbMouldIssueToProcessInfo where jobNo='"+txtJobNo.getValue()+ "' ");

			session.createSQLQuery("delete tbMouldIssueToProcessDetails where jobNo='"+txtJobNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbMouldIssueToProcessDetails where jobNo='"+txtJobNo.getValue()+ "' ");

			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private boolean itemcheck() 
	{
		for(int i=0;i<cmbFinishGoods.size();i++)
		{
			if(cmbFinishGoods.get(i).getValue()!=null)
			{
				return true;	
			}
		}

		return false;
	}


	public void setEventAction()
	{

		cmbIssueFromFind.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {

				if(cmbIssueFromFind.getValue()!=null)
				{

					issueToDataLoadFind();
					showNotification(cmbIssueFromFind.getValue().toString(),Notification.TYPE_TRAY_NOTIFICATION);
				}
			}
		});

		cButton.btnSave.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				saveButtonEvent();
			}
		});

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				txtJobNo.setValue(autoIssueNo());
				isFind=false;
				isUpdate=false;
				dJobDate.focus();

			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				editButtonEvent();
				//isFind=false;
			}
		});

		cButton.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=false;
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				isFind=false;
				isUpdate=false;
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		findButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(findValidation()){
					tableFindDataLoad();
				}
			}
		});

		tableFind.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) {
				if(!event.isDoubleClick())
				{
					try
					{
						txtclear();
						findInitialise(lblFindJobNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());	
					}

					catch(Exception ex)
					{
						showNotification("Error is Here"+ex);	
					}

				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtChallanNo.getValue().toString().isEmpty())

				{

				}

				else
				{
					showNotification("Warning!","Find a Challan No to genarate preview",Notification.TYPE_WARNING_MESSAGE);	
				}


			}
		});

		cmbTypeFind.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{

				if(cmbTypeFind.getValue()!=null)
				{

					String sql=  "select StepId,StepName from tbProductionStep where productionTypeId   like  "
							+" '"+cmbTypeFind.getValue()+"' and StepName like '%Moulding%'  order by StepName  ";

					Iterator iter=dbService(sql);

					cmbIssueFromFind.removeAllItems();
					while(iter.hasNext())
					{
						Object element[]=(Object[]) iter.next();
						cmbIssueFromFind.addItem(element[0].toString());
						cmbIssueFromFind.setItemCaption(element[0].toString(), element[1].toString());
					}	
				}

			}
		});

	}
	private boolean findValidation(){
		if(cmbTypeFind.getValue()!=null){
			if(cmbIssueFromFind.getValue()!=null){
				if(cmbIssueToFind.getValue()!=null){
					return true;
				}
				else{
					showNotification("Please Provide Find Issue To",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide Find Issue From",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Find Production Type",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private void issueToDataLoadFind() {
		cmbIssueToFind.removeAllItems();
		Iterator iter=dbService(" select distinct issueTo,(select StepName from tbProductionStep where StepId=issueTo)as" +
				" Name from tbMouldIssueToProcessDetails where issueFrom='"+cmbIssueFromFind.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbIssueToFind.addItem(element[0]);
			cmbIssueToFind.setItemCaption(element[0], element[1].toString());
		}
	}
	private void tableFGClear()
	{
		for(int i = 0; i < cmbFinishGoods.size(); i++)
		{
			cmbFinishGoods.get(i).removeAllItems();
			txtFgUnit.get(i).setValue("");
		}
	}

	public boolean doubleentrycheck(String semifgId, int ar)
	{

		String comparecaption=semifgId;

		for(int i=0;i<txtunit.size();i++)
		{
			if(cmbsemiFgName.get(ar).getValue()!=null)
			{
				//String job=cmbjoborder.get(i).getValue().toString();
				String semifg=cmbsemiFgName.get(i).getValue().toString();
				String caption=semifg;
				if(i!=ar && comparecaption.equalsIgnoreCase(caption))
				{
					return false;   
				}

				return true;

			}


		}

		return true;

	}


	private void tableRMClear()
	{
	}

	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
	}

	private void tableFindDataLoad()
	{

		tableFind.removeAllItems();
		String sql="";

		sql=    "select distinct b.jobNo, a.challanNo,convert(date,a.jobDate,105)date  from tbMouldIssueToProcessInfo a "
				+"inner join tbMouldIssueToProcessDetails b "
				+"on a.jobNo=b.jobNo  where b.IssueFrom like '"+cmbIssueFromFind.getValue().toString()+"' and b.IssueTo like '"+cmbIssueToFind.getValue().toString()+"' and CONVERT(Date,a.jobDate,105) "
				+"between '"+new SimpleDateFormat("yyyy-MM-dd").format(dFromDate.getValue())+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(dToDate.getValue())+"'  ";


		Iterator iter=dbService(sql);
		int ar=0;
		if(!iter.hasNext()){
			showNotification("Sorry!!","There is No Data",Notification.TYPE_WARNING_MESSAGE);
		}
		while(iter.hasNext())
		{
			Object element[]=(Object[])iter.next();
			tableRowAdd(ar,element[0].toString(),element[2].toString(),element[1].toString());
			ar++;
		}
	}

	private void tableFindRowAdd(int ar){

		lblFindJobNo.add(ar,new Label());
		lblFindJobNo.get(ar).setWidth("100%");
		lblFindJobNo.get(ar).setImmediate(true);


		lblFindChallanNo.add(ar,new Label());
		lblFindChallanNo.get(ar).setWidth("100%");
		lblFindChallanNo.get(ar).setImmediate(true);


		lblFindJobDate.add(ar,new Label());
		lblFindJobDate.get(ar).setWidth("100%");
		lblFindJobDate.get(ar).setImmediate(true);


		tableFind.addItem(new Object[]{lblFindJobNo.get(ar),lblFindChallanNo.get(ar),lblFindJobDate.get(ar)},ar);
	}

	private void RawItemDataLoad(int ar)
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String query = "";
			List list=session.createSQLQuery(query).list();

		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tableFinishedGoodData(int i)
	{

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String query ="select vProductId, vProductName, vUnitName from tbFinishedProductInfo"; 
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			cmbFinishGoods.get(i).removeAllItems();
			txtFgUnit.get(i).setValue("");

			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbFinishGoods.get(i).addItem(element[0].toString());
				cmbFinishGoods.get(i).setItemCaption(element[0].toString(), element[1].toString());

				System.out.println("Product Name: "+element[1].toString());
			}

		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void reportShow()
	{
		String query=null;
		String Subquery=null;
		Transaction tx=null;
		int type=0;
		type=1;

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			System.out.println(query);
			System.out.println(Subquery);
			hm.put("sql", query);
			hm.put("subsql", Subquery);
			hm.put("path", "./report/production/");


			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptMoudingIssue.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}

	private String autoIssueNo() 
	{
		String autoCode = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select  ISNULL(MAX(jobNo),0)+1  from tbMouldIssueToProcessInfo";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}

	private void findButtonEvent()
	{
		FindTypeData();
		searchPanel.setEnabled(true);
	}

	private void findInitialise(String findJobNo) 
	{
		System.out.println(findJobNo);
		String sql="select jobNo,convert(date,jobDate,105)date,challanNo from tbMouldIssueToProcessInfo where jobNo='"+findJobNo+"'";
		Iterator iterInfo=dbService(sql);
		while(iterInfo.hasNext()){
			Object element[]=(Object[])iterInfo.next();
			txtJobNo.setValue(element[0]);
			dJobDate.setValue(element[1]);
			txtChallanNo.setValue(element[2]);
		}
		int ar=0;
		Iterator iterDetails=dbService("select productionTypeId,IssueFrom,IssueTo,semiFgId,sectionIssuePcs,Remarks from tbMouldIssueToProcessDetails  where jobNo ='"+findJobNo+"'");
		while(iterDetails.hasNext())
		{
			Object element[]=(Object[])iterDetails.next();
			cmbproductionType.get(ar).setValue(element[0]);
			cmbfrom.get(ar).setValue(element[1]);
			cmbTo.get(ar).setValue(element[2]);
			cmbsemiFgName.get(ar).setValue(element[3]);
			txtissuePcs.get(ar).setValue(df1.format(element[4]));
			txtremarks.get(ar).setValue(element[5]);
			ar++;

		}
	}
	private void refreshButtonEvent()
	{
		componentIni(true);
		searchPanel.setEnabled(false);
		btnIni(true);
		txtclear();
		tableFindClear();
	}
	private void newButtonEvent() 
	{
		componentIni(false);
		searchPanel.setEnabled(false);
		btnIni(false);
		txtclear();
		tableFindClear();
	}



	private void componentIni(boolean b) 
	{
		txtJobNo.setEnabled(!b);
		dJobDate.setEnabled(!b);
		txtChallanNo.setEnabled(!b);
		lblFinishedGoods.setEnabled(!b);
		tableRM.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnPreview.setEnabled(t);

	}
	public void tableRMInitiaLize()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}


	public void tableFindInitialize()
	{
		for(int i=0;i<5;i++)
		{
			tableFindRowAdd(i);
		}
	}


	public void tableRowAdd(final int ar)
	{
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query=null;

		try
		{

			lblSl.add(ar,new Label());
			lblSl.get(ar).setWidth("20px");
			lblSl.get(ar).setValue(ar + 1);

			cmbproductionType.add(ar,new ComboBox());
			cmbproductionType.get(ar).setWidth("100%");
			cmbproductionType.get(ar).setImmediate(true);
			cmbproductionType.get(ar).setNullSelectionAllowed(true);


			Iterator iter=dbService("select productTypeId,productTypeName from tbProductionType");
			while(iter.hasNext())
			{
				Object element[]=(Object[]) iter.next();
				cmbproductionType.get(ar).addItem(element[0].toString());
				cmbproductionType.get(ar).setItemCaption(element[0].toString(), element[1].toString());
			}

			cmbproductionType.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{

					if(cmbproductionType.get(ar).getValue()!=null)
					{

						String sql=  "select StepId,StepName from tbProductionStep where productionTypeId   like  "
								+" '"+cmbproductionType.get(ar).getValue()+"' and StepName like '%Moulding%'  order by StepName  ";

						Iterator iter=dbService(sql);

						cmbfrom.get(ar).removeAllItems();
						while(iter.hasNext())
						{
							Object element[]=(Object[]) iter.next();
							cmbfrom.get(ar).addItem(element[0].toString());
							cmbfrom.get(ar).setItemCaption(element[0].toString(), element[1].toString());
						}	
						//Semi Fg Load Data
						String sqlSemiFg="select distinct b.FinishedProduct,c.semiFgName from tbMouldProductionInfo a "+
								" inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo  "+
								" inner join tbSemiFgInfo c on b.FinishedProduct=c.semiFgCode  "+
								" where a.Stepid in('BlowSTP-1','InjectionSTP-1','Injection Blow STP-1') and " +
								"a.productionType like '"+cmbproductionType.get(ar).getValue()+"'";
						Iterator iterSemiFg=dbService(sqlSemiFg);
						cmbsemiFgName.get(ar).removeAllItems();
						while(iterSemiFg.hasNext()){
							Object element1[]=(Object[]) iterSemiFg.next();
							cmbsemiFgName.get(ar).addItem(element1[0]);
							cmbsemiFgName.get(ar).setItemCaption(element1[0], element1[1].toString());
						}
					}

				}
			});

			cmbfrom.add(ar,new ComboBox());
			cmbfrom.get(ar).setWidth("100%");
			cmbfrom.get(ar).setImmediate(true);
			cmbfrom.get(ar).setNullSelectionAllowed(true);

			cmbfrom.get(ar).addListener(new ValueChangeListener() 
			{

				public void valueChange(ValueChangeEvent event) 
				{

					if(cmbproductionType.get(ar).getValue()!=null && cmbfrom.get(ar).getValue()!=null )

					{
						String sql=   " select StepId,StepName from  tbProductionStep where productionTypeId like '"+cmbproductionType.get(ar).getValue().toString()+"' "
								+"and  StepName not like '%Moulding%' ";

						Iterator iter=dbService(sql);

						cmbTo.get(ar).removeAllItems();
						while(iter.hasNext())
						{
							Object element[]=(Object[]) iter.next();
							cmbTo.get(ar).addItem(element[0].toString());
							cmbTo.get(ar).setItemCaption(element[0].toString(), element[1].toString());
						} 

					}
				}
			});


			cmbTo.add(ar,new ComboBox());
			cmbTo.get(ar).setWidth("100%");
			cmbTo.get(ar).setImmediate(true);
			cmbTo.get(ar).setNullSelectionAllowed(true);

			/*cmbTo.get(ar).addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbproductionType.get(ar).getValue()!=null && cmbTo.get(ar).getValue()!=null)
					{

						String sql= "select distinct 0, b.orderNo  from tbJobOrderInfo a inner join  tbJobOrderDetails b on a.orderNo=b.orderNo where b.productionType like '"+cmbproductionType.get(ar).getValue()+"' ";

						Iterator iter=dbService(sql);

						cmbjoborder.get(ar).removeAllItems();
						while(iter.hasNext())
						{
							Object element[]=(Object[]) iter.next();
							cmbjoborder.get(ar).addItem(element[1].toString());
						} 

					}

				}
			});*/

			/*cmbjoborder.add(ar,new ComboBox());
			cmbjoborder.get(ar).setWidth("100%");
			cmbjoborder.get(ar).setImmediate(true);
			cmbjoborder.get(ar).setNullSelectionAllowed(true);*/

			/*cmbjoborder.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					  if(cmbjoborder.get(ar).getValue()!=null)
					  {
						  String sql=   "select distinct  ISNULL(d.semiFgId,'')semiFgId, ISNULL(d.semiFgName,'')semiFgname  from tbJobOrderInfo a inner join tbJobOrderDetails b on a.orderNo=b.orderNo  "
								        +"inner join tbFinishedProductInfo c on c.vProductId=b.fgId left join  tbFinishedProductDetailsNew d on d.fgId=c.vProductId  "
								        +" where a.orderNo like '"+cmbjoborder.get(ar).getValue().toString()+"' order by semiFgName  ";


						  String sql = "select distinct  b.FinishedProduct,(select semiFgName from tbSemiFgInfo where semiFgCode= b.FinishedProduct)semifgName "
								       +"from tbMouldProductionInfo a inner join tbMouldProductionDetails b on a.ProductionNo=b.ProductionNo "
								       +"where b.jobOrderNo='"+cmbjoborder.get(ar).getValue()+"' ";


					       Iterator iter=dbService(sql);
					       cmbsemiFgName.get(ar).removeAllItems();
							while(iter.hasNext())
							{
								Object element[]=(Object[]) iter.next();
								cmbsemiFgName.get(ar).addItem(element[0].toString());
								cmbsemiFgName.get(ar).setItemCaption(element[0].toString(), element[1].toString());
							}   
					  }

				}
			});*/


			cmbsemiFgName.add(ar,new ComboBox());
			cmbsemiFgName.get(ar).setWidth("100%");
			cmbsemiFgName.get(ar).setImmediate(true);
			cmbsemiFgName.get(ar).setNullSelectionAllowed(true);

			cmbsemiFgName.get(ar).addListener(new ValueChangeListener() 
			{

				public void valueChange(ValueChangeEvent event) 
				{

					try
					{
						if(cmbfrom.get(ar).getValue()!=null && cmbsemiFgName.get(ar).getValue()!=null)
						{
							if(doubleentrycheck(cmbsemiFgName.get(ar).getValue().toString(),ar))
							{
								String sql=   "select unit,dbo.mouldstock('"+cmbsemiFgName.get(ar).getValue().toString()+"'," +
										"'"+cmbfrom.get(ar).getValue().toString()+"')mould from tbSemiFgInfo " +
												"where semiFgCode like '"+cmbsemiFgName.get(ar).getValue().toString()+"' ";
								System.out.println(sql);
								Iterator iter=dbService(sql);
								txtunit.get(ar).setValue("");
								txtmouldStock.get(ar).setValue("");
								while(iter.hasNext())
								{
									Object element[]=(Object[]) iter.next();
									txtunit.get(ar).setValue(element[0].toString());
									txtmouldStock.get(ar).setValue(df.format(Double.parseDouble(element[1].toString()) ));	
								} 

								if(ar==cmbsemiFgName.size()-1)
								{
									tableRowAdd(ar+1);	
								}

							} 
							else
							{
								showNotification("Double Entry Is Not Allowed",Notification.TYPE_WARNING_MESSAGE);
								cmbsemiFgName.get(ar).setValue(null);
								txtunit.get(ar).setValue("");
								txtmouldStock.get(ar).setValue("");
							}
						} 
						/*else{
							showNotification("Please Provide Production Type and From[Store]",Notification.TYPE_WARNING_MESSAGE);
						}*/
					}

					catch(Exception ex)
					{
						showNotification(ex+"",Notification.TYPE_ERROR_MESSAGE);
					}

				}
			});



			txtunit.add(ar,new TextRead(1));
			txtunit.get(ar).setWidth("100%");
			txtunit.get(ar).setImmediate(true);

			txtmouldStock.add(ar,new TextRead(1));
			txtmouldStock.get(ar).setWidth("100%");
			txtmouldStock.get(ar).setImmediate(true);

			txtissuePcs.add(ar,new AmountField());
			txtissuePcs.get(ar).setWidth("100%");
			txtissuePcs.get(ar).setImmediate(true);

			txtissuePcs.get(ar).addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event)
				{
					try
					{
						if(!txtmouldStock.get(ar).getValue().toString().isEmpty() && ! txtissuePcs.get(ar).getValue().toString().isEmpty())
						{
							double issuepcs= Double.parseDouble(txtissuePcs.get(ar).getValue().toString().isEmpty()?"0.00":txtissuePcs.get(ar).getValue().toString()) ;
							double stock=Double.parseDouble(txtmouldStock.get(ar).getValue().toString().isEmpty()?"0.00":txtmouldStock.get(ar).getValue().toString()) ;

							if(stock<issuepcs && !isFind)
							{
								txtissuePcs.get(ar).setValue("");

								showNotification("Issue Qty Must Not Exceed Stock Qty",Notification.TYPE_WARNING_MESSAGE);   
							}
							else
							{

							}   
						}	
					}

					catch(Exception ex)
					{
						System.out.println("Excepti Is"+ex);

					}
				}
			});


			txtremarks.add(ar,new TextField());
			txtremarks.get(ar).setWidth("100%");
			txtremarks.get(ar).setImmediate(true);


			tableRM.addItem(new Object[]{lblSl.get(ar), cmbproductionType.get(ar),cmbfrom.get(ar),cmbTo.get(ar),cmbsemiFgName.get(ar),txtunit.get(ar) ,txtmouldStock.get(ar),txtissuePcs.get(ar),txtremarks.get(ar)},ar);

		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
}
