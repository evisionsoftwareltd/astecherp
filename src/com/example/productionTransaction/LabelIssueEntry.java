package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

//import org.apache.bcel.generic.POP;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.productionSetup.ProductionFindWindow;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbFiscalYear;

public class LabelIssueEntry extends Window 
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	// Labels
	private Label lblJobNo;
	private Label lblJobDate;
	private Label lblChallanNo;
	private Label lblFrom;
	private Label lblTo;
	private Label lblFinsshedGoodsName;
	private Label lblUnit; 
	private Label lblSectionStock;
	private Label lblFloorStock;
	private Label lblPrintedStock;
	private Label lblIssueQty;
	private Label lblIssueTarget;
	private Label lblRemarks;
	private Label lblLine;
	private Label lbljoborderNo;

	// ComboBox
	private ComboBox cmbFrom;
	private ComboBox cmbTo;
	private ComboBox cmbFinishedGoods;
	private ComboBox cmbjoborderNo;

	// TextRead
	private TextRead txtJobNo;
	private TextRead txtUnit;
	private TextRead txtFloorStock;
	private TextRead txtPrintedStock;


	// TextField
	private TextField txtChallanNo;
	private TextField findJobNo;

	// TextArea
	private TextArea txtRemarks;

	// popupdatefields
	private PopupDateField dJobDate;

	
	private AmountField txtIssueQty;
	private DecimalFormat df = new DecimalFormat("#0");
	private DecimalFormat df1 = new DecimalFormat("#0.00");
	

	// dateFormats
	private SimpleDateFormat dFormatSql = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	ArrayList<Component> focusComp = new ArrayList<Component>();

	// boolean Values
	boolean isUpdate = false;
	boolean isFind = false;

	// table
	//private Table tableFG;
	private Table tableFgnew;

	//ArrayList tableFG
	private ArrayList<Label> tblblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> tbcmbRM = new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbtxtUnit  = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtsectionStock  = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtfloorStock  = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtPrintedSqm  = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtPrintedPcs  = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtTargetQty  = new ArrayList<TextRead>();
	private ArrayList<TextField> tbtxtIssueSqm  = new ArrayList<TextField>();
	private ArrayList<TextField> tbtxtIssuePcs  = new ArrayList<TextField>();
	private ArrayList<TextField> tbtxtRemarks  = new ArrayList<TextField>();
	

	// Button
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "Preview", "", "Exit");
	Double ttlissue=0.00;
	Double totalissue=0.00;

	public LabelIssueEntry(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("Issue To Process :: "+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		tableInitialiseFgNew();
		eventAction();
		cmbFromData();
		focusMove();
		btnIni(true);
		componentIni(true);
		txtClear();
		tableClear();
		joborderNoLoad();
		cButton.btnNew.focus();
	}

	private AbsoluteLayout buildMainLayout()
	{
		// mainLayout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("600px");
		
		this.setWidth("1220px");
		setHeight("650px");

		// lblJobNo
		lblJobNo = new Label("Job No :");
		lblJobNo.setImmediate(false);
		lblJobNo.setWidth("-1px");
		lblJobNo.setHeight("-1px");

		// txtJobNo
		txtJobNo = new TextRead();
		txtJobNo.setImmediate(false);
		txtJobNo.setWidth("80px");
		txtJobNo.setHeight("23px");

		// lblChallanNo
		lblChallanNo= new Label("Challan No :");
		lblChallanNo.setImmediate(false);
		lblChallanNo.setWidth("-1px");
		lblChallanNo.setHeight("-1px");

		// txtChallanNo
		txtChallanNo = new TextField();
		txtChallanNo.setImmediate(false);
		txtChallanNo.setWidth("-1px");
		txtChallanNo.setHeight("-1px");

		// lblJobDate
		lblJobDate = new Label("Date :");
		lblJobDate.setImmediate(false);
		lblJobDate.setWidth("-1px");
		lblJobDate.setHeight("-1px");

		// dJobDate
		dJobDate = new PopupDateField();
		dJobDate.setImmediate(false);
		dJobDate.setWidth("-1px");
		dJobDate.setHeight("-1px");
		dJobDate.setDateFormat("dd-MM-yyyy");
		dJobDate.setValue(new java.util.Date());
		dJobDate.setResolution(PopupDateField.RESOLUTION_DAY);

		// lblFrom
		lblFrom = new Label("From :");
		lblFrom.setImmediate(false);
		lblFrom.setWidth("-1px");
		lblFrom.setHeight("-1px");

		// cmbFrom
		cmbFrom = new ComboBox();
		cmbFrom.setImmediate(true);
		cmbFrom.setNullSelectionAllowed(false);
		cmbFrom.setWidth("190px");
		cmbFrom.setHeight("-1px");

		// lblTo
		lblTo = new Label("To :");
		lblTo.setImmediate(false);
		lblTo.setWidth("-1px");
		lblTo.setHeight("-1px");

		// cmbTo
		cmbTo = new ComboBox();
		cmbTo.setImmediate(true);
		cmbTo.setNullSelectionAllowed(false);
		cmbTo.setWidth("190px");
		cmbTo.setHeight("-1px");

		// lblFinsshedGoodsName
		lblFinsshedGoodsName = new Label("Finished Goods:");
		lblFinsshedGoodsName.setImmediate(false);
		lblFinsshedGoodsName.setWidth("-1px");
		lblFinsshedGoodsName.setHeight("-1px");

		// cmbFinishedGoods
		cmbFinishedGoods = new ComboBox();
		cmbFinishedGoods.setImmediate(true);
		cmbFinishedGoods.setNullSelectionAllowed(true);
		cmbFinishedGoods.setWidth("250px");
		cmbFinishedGoods.setHeight("-1px");

		// lblUnit
		lblUnit = new Label("Unit :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");

		// txtUnit
		txtUnit = new TextRead();
		txtUnit.setImmediate(true);
		txtUnit.setWidth("50px");
		txtUnit.setHeight("23px");

		// lblIssueQty
		lblIssueQty = new Label("Issue Qty :");
		lblIssueQty.setImmediate(false);
		lblIssueQty.setWidth("-1px");
		lblIssueQty.setHeight("-1px");

		// txtIssueQty
		txtIssueQty= new AmountField();
		txtIssueQty.setImmediate(true);
		txtIssueQty.setWidth("80px");
		txtIssueQty.setHeight("23px");



		// lblSectionStock
		lblSectionStock = new Label("UP Section Stock :");
		lblSectionStock.setImmediate(false);
		lblSectionStock.setWidth("-1px");
		lblSectionStock.setHeight("-1px");


		// lblFloorStock
		lblFloorStock = new Label("UP Floor Stock :");
		lblFloorStock.setImmediate(false);
		lblFloorStock.setWidth("-1px");
		lblFloorStock.setHeight("-1px");

		// txtFloorStock
		txtFloorStock = new TextRead(1);
		txtFloorStock.setImmediate(true);
		txtFloorStock.setWidth("80px");
		txtFloorStock.setHeight("23px");

		// lblPrintedStock
		lblPrintedStock = new Label("Printed Stock :");
		lblPrintedStock.setImmediate(false);
		lblPrintedStock.setWidth("-1px");
		lblPrintedStock.setHeight("-1px");

		// txtPrintedStock
		txtPrintedStock= new TextRead(1);
		txtPrintedStock.setImmediate(true);
		txtPrintedStock.setWidth("80px");
		txtPrintedStock.setHeight("23px");

		
		lbljoborderNo = new Label("Job Order No:");
		lbljoborderNo.setImmediate(false);
		lbljoborderNo.setWidth("-1px");
		lbljoborderNo.setHeight("-1px");
		
		
		cmbjoborderNo = new ComboBox();
		cmbjoborderNo.setImmediate(true);
		cmbjoborderNo.setNullSelectionAllowed(true);
		cmbjoborderNo.setWidth("190px");
		cmbjoborderNo.setHeight("-1px");
		
	
		tableFgnew=new Table();
		tableFgnew.setWidth("1180px");
		tableFgnew.setHeight("150px");
		tableFgnew.setColumnCollapsingAllowed(true);
		
		
		tableFgnew.addContainerProperty("SL", Label.class, new Label());
		tableFgnew.setColumnWidth("SL", 20);
		tableFgnew.setColumnAlignment("SL", tableFgnew.ALIGN_CENTER);

		tableFgnew.addContainerProperty("R/M Name", ComboBox.class, new ComboBox());
		tableFgnew.setColumnWidth("R/M Name",250);

		tableFgnew.addContainerProperty("Unit", TextRead.class, new TextRead());
		tableFgnew.setColumnWidth("Unit",60);

		tableFgnew.addContainerProperty("UP Sec Stock", TextRead.class, new TextRead());
		tableFgnew.setColumnWidth("UP Sec Stock",75);
		
		tableFgnew.addContainerProperty("UP FLR Stock", TextRead.class, new TextRead());
		tableFgnew.setColumnWidth("UP FLR Stock",75);
		
		tableFgnew.addContainerProperty("Printed SQM", TextRead.class, new TextRead());
		tableFgnew.setColumnWidth("Printed SQM",100);
		
		tableFgnew.addContainerProperty("Printed Pcs", TextRead.class, new TextRead());
		tableFgnew.setColumnWidth("Printed Pcs",100);
		
		tableFgnew.addContainerProperty("Target Qty", TextRead.class, new TextRead());
		tableFgnew.setColumnWidth("Target Qty",80);
		
		tableFgnew.addContainerProperty("Issue Sqm", TextField.class, new TextField());
		tableFgnew.setColumnWidth("Issue Sqm",80);
		
		tableFgnew.addContainerProperty("Issue pcs", TextField.class, new TextField());
		tableFgnew.setColumnWidth("Issue pcs",80);
		
		tableFgnew.addContainerProperty("Remarks", TextField.class, new TextField());
		tableFgnew.setColumnWidth("Remarks",100);
		
		

		// lblRemarks
		lblRemarks = new Label("Remarks :");
		lblRemarks.setImmediate(false);
		lblRemarks.setWidth("-1px");
		lblRemarks.setHeight("-1px");

		// txtRemarks
		txtRemarks= new TextArea();
		txtRemarks.setImmediate(true);
		txtRemarks.setWidth("380px");
		txtRemarks.setHeight("150px");

		findJobNo = new TextField();
		findJobNo.setImmediate(true);

		mainLayout.addComponent(lblJobNo, "top: 20px; left: 20px;");
		mainLayout.addComponent(txtJobNo, "top: 18px; left: 140px;");
		
		mainLayout.addComponent(lbljoborderNo, "top: 20px; left: 450px;");
		mainLayout.addComponent(cmbjoborderNo, "top: 18px; left: 570px;");

		mainLayout.addComponent(lblJobDate, "top: 50px; left: 20px;");
		mainLayout.addComponent(dJobDate, "top: 48px; left: 140px;");

		mainLayout.addComponent(lblChallanNo, "top: 110px; left:450px;");
		mainLayout.addComponent(txtChallanNo, "top: 108px; left: 570px;");

		mainLayout.addComponent(lblFrom, "top: 80px; left: 20px;");
		mainLayout.addComponent(cmbFrom, "top: 78px; left: 140px;");

		mainLayout.addComponent(lblTo, "top: 110px; left: 20px;");
		mainLayout.addComponent(cmbTo, "top: 108px; left: 140px;");
		
		mainLayout.addComponent(lblFinsshedGoodsName, "top: 50px; left: 450px;");
		mainLayout.addComponent(cmbFinishedGoods, "top: 48px; left: 570px;");

		mainLayout.addComponent(lblUnit, "top: 80px; left: 450px;");
		mainLayout.addComponent(txtUnit, "top: 78px; left: 570px;");
		
		
		//mainLayout.addComponent(lblFinishedGoods, "top:150px; left: 20px;");
		mainLayout.addComponent(tableFgnew,"top: 150px; left: 20px");
		
		mainLayout.addComponent(lblRemarks, "top: 360px; left: 20px;");
		mainLayout.addComponent(txtRemarks,"top: 358px; left: 140px");

		lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:520.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:540.0px; left:270.0px;");


		return mainLayout;
	}

	private void focusMove()
	{
		
		focusComp.add(dJobDate);
		focusComp.add(cmbFrom);
		focusComp.add(cmbTo);
		focusComp.add(cmbjoborderNo);
		focusComp.add(cmbFinishedGoods);
		focusComp.add(txtChallanNo);
		
		for(int i = 0; i < tbcmbRM.size(); i++)
		{
			focusComp.add(tbcmbRM.get(i));
			focusComp.add(tbtxtIssueSqm.get(i));
			focusComp.add(tbtxtIssuePcs.get(i));
		}

		
		focusComp.add(txtRemarks);

		focusComp.add(cButton.btnNew);
		focusComp.add(cButton.btnEdit);
		focusComp.add(cButton.btnSave);
		focusComp.add(cButton.btnRefresh);
		focusComp.add(cButton.btnDelete);
		focusComp.add(cButton.btnFind);

		new FocusMoveByEnter(this, focusComp);
	}

	public List perTubeCalc(int ar){
		Transaction tx = null;
		String sql = "";
		List std = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String productId = tbcmbRM.get(ar).getValue().toString();

			sql="select 0, isnull(perSqm,0)as qty  from tbFinishedProductDetails  where fgId like '"+cmbFinishedGoods.getValue().toString()+"' and rawItemCode like '"+productId+"'";
			System.out.println("sql is"+sql);

			std=session.createSQLQuery(sql).list();

		}
		catch(Exception exp){
			this.getParent().showNotification("From perTubeCalc: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		return std;
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

	private void componentIni(boolean b)
	{
		lblJobNo.setEnabled(!b);
		txtJobNo.setEnabled(!b);
		lbljoborderNo.setEnabled(!b);
		cmbjoborderNo.setEnabled(!b);

		lblJobDate.setEnabled(!b);
		dJobDate.setEnabled(!b);

		lblChallanNo.setEnabled(!b);
		txtChallanNo.setEnabled(!b);

		lblFrom.setEnabled(!b);
		cmbFrom.setEnabled(!b);

		lblTo.setEnabled(!b);
		cmbTo.setEnabled(!b);

		lblFinsshedGoodsName.setEnabled(!b);
		cmbFinishedGoods.setEnabled(!b);

		lblUnit.setEnabled(!b);
		txtUnit.setEnabled(!b);

		lblSectionStock.setEnabled(!b);
		

		lblFloorStock.setEnabled(!b);
		txtFloorStock.setEnabled(!b);

		lblPrintedStock.setEnabled(!b);
		txtPrintedStock.setEnabled(!b);

		tableFgnew.setEnabled(!b);

		lblIssueQty.setEnabled(!b);
		txtIssueQty.setEnabled(!b);

		lblRemarks.setEnabled(!b);
		txtRemarks.setEnabled(!b);
	}

	private void txtClear()
	{
		txtJobNo.setValue("");
		cmbjoborderNo.setValue(null);
		dJobDate.setValue(new java.util.Date());
		txtChallanNo.setValue("");
		cmbFrom.setValue(null);
		cmbTo.setValue(null);
		cmbFinishedGoods.setValue(null);
		txtUnit.setValue("");
		txtRemarks.setValue("");
	}

	private void tableClear()
	{
		for(int i = 0; i < tbcmbRM.size(); i++)
		{
			tbcmbRM.get(i).removeAllItems();
			tbtxtUnit.get(i).setValue("");
			tbtxtsectionStock.get(i).setValue("");
			tbtxtfloorStock.get(i).setValue("");
			tbtxtPrintedSqm.get(i).setValue("");
			tbtxtPrintedPcs.get(i).setValue("");
			tbtxtTargetQty.get(i).setValue("");
			tbtxtIssueSqm.get(i).setValue("");
			tbtxtIssuePcs.get(i).setValue("");
			tbtxtRemarks.get(i).setValue("");
			
		}
	}

	private void eventAction()
	{
		cButton.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				dJobDate.focus();
				newButtonEvent();
				txtJobNo.setValue(autoJobNo());
			}

			
		});
		cButton.btnEdit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				editButtonEvent();
				dJobDate.focus();

			}
		});
		cButton.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				cButton.btnNew.focus();
			}
		});

		cButton.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
									
					saveButtonEvent();
					isFind = false;
					isUpdate = false;	
					System.out.print("What A Shame");
				
			}
		});

		cButton.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbFrom.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{

				if(cmbFrom.getValue()!=null)
				{
					cmbToData();
				}
				
				
				
				
			}
		});

		cmbTo.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbTo.getValue()!=null)
				{
					
				}
			}
		});
		
		
		cmbjoborderNo.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{

				if(cmbjoborderNo.getValue()!=null)
				{
					
					
						FinishedGoodsDataLoad();
					
				}
			}
		});
		

		cmbFinishedGoods.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbFinishedGoods.getValue()!=null)
				{
					FinishUnitData();

					for(int i=0;i<tblblSl.size();i++)
					{
						tableDataAdding(i);	
					}
				}
			}
		});
		
	}
	

	private void FinishedGoodsDataLoad() 
	{
		Transaction tx=null;
		String query=null;

		try
		{

			query= "select distinct  fgId,(select vProductName from tbFinishedProductInfo where vProductId like fgId ) as fgName from tbJobOrderDetails " 
					+"where productionType like (select productTypeId from tbProductionType where productTypeName like 'Label Production') and orderNo like '"+cmbjoborderNo.getValue().toString()+"'  ";  


			
				  System.out.println("query is"+query);
			
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();
			Iterator iter=list.iterator();

			  while(iter.hasNext())
			  {
				  Object[] element=(Object[]) iter.next();
				  cmbFinishedGoods.addItem(element[0].toString());
				  cmbFinishedGoods.setItemCaption(element[0].toString(), element[1].toString());
				  
				   
			  }
		}

		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	
	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
	}


	private void tableRowAddRMFgNew(final int ar)
	{

		tblblSl.add(ar, new Label());
		tblblSl.get(ar).setWidth("-1");;
		tblblSl.get(ar).setHeight("-1");
		tblblSl.get(ar).setValue(ar+1);

		tbcmbRM.add(ar, new ComboBox());
		tbcmbRM.get(ar).setImmediate(true);
		tbcmbRM.get(ar).setWidth("100%");
		tbcmbRM.get(ar).setHeight("-1");
		
		tbcmbRM.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{


				if(tbcmbRM.get(ar).getValue()!=null)
				{
					if(doubleCheck(ar))
					{
						String sql="";
					  Transaction tx=null;
					  Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					  
					try
					{
						tx=session.beginTransaction();
						sql=" select  unit,UnprintedSectionStock,florStock,PrintedStock from funcproductionStockLabelNew('"+tbcmbRM.get(ar).getValue().toString()+"','"+dFormatSql.format(dJobDate.getValue()) +"','2','PT-2','"+cmbTo.getValue().toString()+"','"+cmbFrom.getValue().toString()+"','"+cmbFinishedGoods.getValue().toString()+"')  ";
						List lst=session.createSQLQuery(sql).list();
						Iterator iter=lst.iterator();
						if(iter.hasNext())
						{
							Object[]element=(Object[]) iter.next();
							System.out.println("Rabiul Hasan");
							tbtxtUnit.get(ar).setValue(element[0].toString());
							tbtxtsectionStock.get(ar).setValue(element[1].toString());
							tbtxtfloorStock.get(ar).setValue(element[2].toString());
							tbtxtPrintedSqm.get(ar).setValue(element[3].toString());
							
						}
						
						List std=perTubeCalc(ar);
						if(!std.isEmpty())
						{
							Object[] element = (Object[]) std.iterator().next();
							System.out.println("You are Ok"+element[1]);
							double target=Double.parseDouble(tbtxtsectionStock.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
							System.out.println("target qty is"+target);
							tbtxtTargetQty.get(ar).setValue(df.format(target));	
						}
						
						
						
					}
					catch(Exception ex)
					{
						
					}
						
						
					}
					else{
						showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
						tbcmbRM.get(ar).setValue(null);
						tbcmbRM.get(ar).focus();
						System.out.println("Not Ok");
					}

				}


			}
		});

		tbtxtUnit.add(ar, new TextRead(1));
		tbtxtUnit.get(ar).setImmediate(true);
		tbtxtUnit.get(ar).setWidth("100%");

		tbtxtsectionStock.add(ar, new TextRead(1));
		tbtxtsectionStock.get(ar).setImmediate(true);
		tbtxtsectionStock.get(ar).setWidth("100%");
		
		tbtxtfloorStock.add(ar, new TextRead(1));
		tbtxtfloorStock.get(ar).setImmediate(true);
		tbtxtfloorStock.get(ar).setWidth("100%");
		
		tbtxtPrintedSqm.add(ar, new TextRead(1));
		tbtxtPrintedSqm.get(ar).setImmediate(true);
		tbtxtPrintedSqm.get(ar).setWidth("100%");
		
		tbtxtPrintedSqm.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tbcmbRM.get(ar).getValue()!=null&&!tbtxtPrintedSqm.get(ar).getValue().toString().isEmpty())
				{
					List std=perTubeCalc( ar);
					if(std.iterator().hasNext()){
						Object[] element = (Object[]) std.iterator().next();
						double sqm=Double.parseDouble(tbtxtPrintedSqm.get(ar).getValue().toString().isEmpty()?"0.0":tbtxtPrintedSqm.get(ar).getValue().toString());
						double perSqm=Double.parseDouble(element[1].toString());
						double pcs=sqm*perSqm;
						tbtxtPrintedPcs.get(ar).setValue(df.format(pcs));
					}
				}
				else{
					tbtxtPrintedPcs.get(ar).setValue("");
				}
			}
		});
		
		tbtxtPrintedPcs.add(ar, new TextRead(1));
		tbtxtPrintedPcs.get(ar).setImmediate(true);
		tbtxtPrintedPcs.get(ar).setWidth("100%");
		
		tbtxtTargetQty.add(ar, new TextRead(1));
		tbtxtTargetQty.get(ar).setImmediate(true);
		tbtxtTargetQty.get(ar).setWidth("100%");
		
		tbtxtIssueSqm.add(ar, new TextField());
		tbtxtIssueSqm.get(ar).setImmediate(true);
		tbtxtIssueSqm.get(ar).setWidth("100%");
		
	tbtxtIssueSqm.get(ar).addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {
				if(tbcmbRM.get(ar).getValue()!=null && !tbtxtIssueSqm.get(ar).getValue().toString().isEmpty())
				{
					Double totalissue=0.00;
					for(int i=0;i<tbcmbRM.size();i++)
					{
						if(!tbtxtIssueSqm.get(i).getValue().toString().isEmpty())
						{
							totalissue=totalissue+ Double.parseDouble(tbtxtIssueSqm.get(i).getValue().toString()) ;		
						}	
					}
					System.out.print("Total Issue is"+totalissue);

					if(selectFrom())
					{
						System.out.println("Hellow Bangladesh");
						
						double unPrintedSecStock=Double.parseDouble(tbtxtsectionStock.get(ar).getValue().toString().isEmpty()?"0.0":
							tbtxtsectionStock.get(ar).getValue().toString());
						double issueQty=Double.parseDouble(tbtxtIssueSqm.get(ar).getValue().toString().isEmpty()?"0.0":
							tbtxtIssueSqm.get(ar).getValue().toString());
						if(!isFind)
						{
							if(unPrintedSecStock>=issueQty)	
							{

								List std=perTubeCalc( ar);
								if(std.iterator().hasNext())
								{
									Object[] element = (Object[]) std.iterator().next();
									double target=Double.parseDouble(tbtxtIssueSqm.get(ar).getValue().toString().isEmpty()?"0.0":
										tbtxtIssueSqm.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
									tbtxtIssuePcs.get(ar).setValue(df.format(target));
								}
							}
							else
							{
								tbtxtIssueSqm.get(ar).setValue("");
								tbtxtIssuePcs.get(ar).setValue("");
								tbtxtIssueSqm.get(ar).focus();
								showNotification("Issue Qty Exceed Section Stock Qty",Notification.TYPE_WARNING_MESSAGE);

							}	
						}
						else
						{
							Transaction tx=null;
							String sql=null; 
							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();

							List std=perTubeCalc( ar);
							if(std.iterator().hasNext())
							{
								Object[] element = (Object[]) std.iterator().next();
								double target=Double.parseDouble(tbtxtIssueSqm.get(ar).getValue().toString().isEmpty()?"0.0":
									tbtxtIssueSqm.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
								tbtxtIssuePcs.get(ar).setValue(df.format(target));
							}

							if(ar==0)
							{

								sql=    " select 0, ISNULL(SUM(b.ShiftASqm+b.ShiftBSqm+WastageSqm ),0)   from tbTubeProductionInfo a "
										+"inner join "
										+"tbTubeProductionDetails b "
										+"on "
										+"a.ProductionNo=b.ProductionNo "
										+"where a.IssueNo like '"+txtJobNo.getValue().toString()+"' ";
								System.out.print("Desire sql is"+sql);

								Double ttlissue=0.00;
								List lst=session.createSQLQuery(sql).list();
								if(!lst.isEmpty())
								{
									Iterator iter=lst.iterator();
									if(iter.hasNext())
									{
										Object[] element1=(Object[]) iter.next();	
										ttlissue=Double.parseDouble(element1[1].toString());

									}

								}	
							}

						}

					}
					else
					{
						Transaction tx=null;
						String sql=null; 
						Session session=SessionFactoryUtil.getInstance().getCurrentSession();
						tx=session.beginTransaction();
						double printedStock=Double.parseDouble(tbtxtPrintedSqm.get(ar).getValue().toString().isEmpty()?"0.0":
							tbtxtPrintedSqm.get(ar).getValue().toString());
						double issueQty=Double.parseDouble(tbtxtIssueSqm.get(ar).getValue().toString().isEmpty()?"0.0":
							tbtxtIssueSqm.get(ar).getValue().toString());

						System.out.println("Printed Stock: "+printedStock);
						System.out.println("IssueQty: "+issueQty);


						totalissue=0.00;
						for(int i=0;i<tbcmbRM.size();i++)
						{
							
							totalissue=totalissue+ Double.parseDouble(tbtxtIssueSqm.get(ar).getValue().toString()) ;	
						}

						if(!isFind)
						{
							for(int i=0;i<tbcmbRM.size();i++)
							{
								totalissue=totalissue+ Double.parseDouble(tbtxtIssueSqm.get(ar).getValue().toString()) ;	
							}
							if(printedStock<issueQty)
							{
								tbtxtIssueSqm.get(ar).setValue("");
								tbtxtIssuePcs.get(ar).setValue("");
								tbtxtIssuePcs.get(ar).focus();
								showNotification("Issue Target Exceed Printed Stock Target",Notification.TYPE_WARNING_MESSAGE);
							}	
							List std=perTubeCalc( ar);
							if(std.iterator().hasNext())
							{
								Object[] element = (Object[]) std.iterator().next();
								double target=Double.parseDouble(tbtxtIssueSqm.get(ar).getValue().toString().isEmpty()?"0.0":
									tbtxtIssueSqm.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
								tbtxtIssuePcs.get(ar).setValue(df.format(target));
							}
						}

						else
						{
							if(ar==0)
							{
								sql=  " select SUM(b.IssueQty) from tbTubeIssueInfo a "
										+" inner join "
										+"  tbTubeIssueDetails b "
										+" on "
										+" a.IssueNo=b.IssueNo " 
										+ "where a.IssueNo like '"+txtJobNo.getValue().toString()+"' ";
								Double ttlissue=0.00;
								//Double totalissue=0.00;

								List lst=session.createSQLQuery(sql).list();
								if(lst.isEmpty())
								{
									Iterator iter=lst.iterator();
									if(iter.hasNext())
									{
										Object[] element1=(Object[]) iter.next();	
										ttlissue=(Double) element1[0];

									}

								}	
							}

							List std=perTubeCalc( ar);
							if(std.iterator().hasNext())
							{
								Object[] element = (Object[]) std.iterator().next();
								double target=Double.parseDouble(tbtxtIssueSqm.get(ar).getValue().toString().isEmpty()?"0.0":
									tbtxtIssueSqm.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
								tbtxtIssuePcs.get(ar).setValue(df.format(target));
							}
						}

					}
				}

			}
		});
		
		
		
		tbtxtIssuePcs.add(ar, new TextField());
		tbtxtIssuePcs.get(ar).setImmediate(true);
		tbtxtIssuePcs.get(ar).setWidth("100%");
		
		tbtxtIssuePcs.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(tbcmbRM.get(ar).getValue()!=null&&!tbtxtIssuePcs.get(ar).getValue().toString().isEmpty()  ){
					List std=perTubeCalc( ar);
					if(std.iterator().hasNext()){
						Object[] element = (Object[]) std.iterator().next();
						System.out.println("IssueTarget Changed before");

						double qty=Double.parseDouble(tbtxtIssuePcs.get(ar).getValue().toString().isEmpty()?"0.0":
							tbtxtIssuePcs.get(ar).getValue().toString())/Double.parseDouble(element[1].toString());
						System.out.println("Quantity Is "+qty);

						tbtxtIssueSqm.get(ar).setValue((df1.format(qty) ));
						System.out.println("IssueTarget Changed after"+qty);
					}
				}
			}
		});
		
		tbtxtRemarks.add(ar, new TextField());
		tbtxtRemarks.get(ar).setImmediate(true);
		tbtxtRemarks.get(ar).setWidth("100%");
		
		
		tableFgnew.addItem(new Object[]{tblblSl.get(ar),tbcmbRM.get(ar),tbtxtUnit.get(ar),tbtxtsectionStock.get(ar),tbtxtfloorStock.get(ar),tbtxtPrintedSqm.get(ar),tbtxtPrintedPcs.get(ar),tbtxtTargetQty.get(ar),tbtxtIssueSqm.get(ar),tbtxtIssuePcs.get(ar),tbtxtRemarks.get(ar)},ar);

	}
	
	
	public boolean selectFrom(){

		try{
			Transaction tx=null;
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select AutoID from tbSectionInfo where AutoID like '"+cmbFrom.getValue().toString()+"'";
			List list=session.createSQLQuery(sql).list();
			if(list.isEmpty()){
				return false;
			}
		}
		catch(Exception exp){
			showNotification("Issueqty Value change"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		return true;
	}
	
	public boolean doubleCheck(int ar){
		String value=tbcmbRM.get(ar).getValue().toString();
		for(int x=0;x<tbcmbRM.size();x++)
		{
			if(tbcmbRM.get(x).getValue()!=null)
			{
				if(x!=ar&&value.equalsIgnoreCase(tbcmbRM.get(x).getValue().toString())){
					return false;
				}
			}
		}
		return true;
	}

	private void findButtonEvent() 
	{
		Window win = new LabelIssueFindWindow(sessionBean, findJobNo);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{

				txtClear();
				isFind = true;
				findInitialise(findJobNo.getValue().toString());

			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String findJobNo) 
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql1 = "select * from tbLabelIssueInfo where jobNo='"+findJobNo+ "'";
			System.out.println(sql1);
			List list=session.createSQLQuery(sql1).list();
			for(Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object element[] = (Object[])iter.next();
				txtJobNo.setValue(element[1]);
				txtChallanNo.setValue(element[3]);
				dJobDate.setValue(element[2]);
				cmbFrom.setValue(element[4]);
				cmbTo.setValue(element[5]);
				cmbjoborderNo.setValue(element[15].toString());
				cmbFinishedGoods.setValue(element[6]);
				txtRemarks.setValue(element[8].toString());
			
			}

			String sql2 = "";
			sql2 = "select * from tbLabelIssueDetails where jobNo='"+findJobNo+ "'";
			System.out.println(sql2);
			List list1=session.createSQLQuery(sql2).list();

			int i = 0;

			for (Iterator iter2 = list1.iterator(); iter2.hasNext();)
			{
				Object element[] = (Object[])iter2.next();

				tbcmbRM.get(i).setValue(element[3].toString());
				tbtxtIssueSqm.get(i).setValue(element[8]);
				tbtxtRemarks.get(i).setValue(element[9]);

				i++;
				//tableRowAddRM(i+1);
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error1", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		tableClear();

		isFind = false;
	
	}

	private void refreshButtonEvent()
	{
		componentIni(true);
		btnIni(true);
		txtClear();
		tableClear();
		isFind = false;
		isUpdate = false;
	}


private void tableDataAdding(int i)
	{
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String  query="";

			
			
			query=	"select distinct b.RawItemCode,(select vRawItemName from tbRawItemInfo where vRawItemCode like b.RawItemCode ) as RmName "  
					+"from tbFinishedGoodsStandardInfo a "
					+"inner join tbFinishedGoodsStandardDetails b "
					+"on  a.JobNo=b.JobNo where a.fGCode like '"+cmbFinishedGoods.getValue().toString()+"' and declarationDate like " 
					+"(select MAX(declarationDate)  from tbFinishedGoodsStandardInfo where fGCode like '"+cmbFinishedGoods.getValue().toString()+"')";
			
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			tbcmbRM.get(i).removeAllItems();
			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				tbcmbRM.get(i).addItem(element[0].toString());
				tbcmbRM.get(i).setItemCaption(element[0].toString(), element[1].toString());
				System.out.println("Product Name: "+element[1].toString());

			}

		}
		catch (Exception ex) {
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbFromData() 
	{
		cmbFrom.removeAllItems();

		Transaction tx=null;
		String query=null;

		try
		{

			query= "  select * from  "
					+"( "
					+"	select '1' as type, CAST(AutoID as varchar(120)) as id ,SectionName as section  from tbSectionInfo where SectionName like '%Tube Sec%'  "
					+"	union "
					+"	select distinct '2' as type,  StepId as id ,StepName section  from tbProductionStep a  "
					+"	inner join  "
					+"	tbProductionType b  "
					+"	on a.productionTypeId=b.productTypeId "
					+"	where b.productTypeName like '%Label Production%' "
					+"	) as a  order by a.type ";
			
		
			System.out.println(query);

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();

				cmbFrom.addItem(element[1].toString());
				cmbFrom.setItemCaption(element[1].toString(), (String) element[2]);
			}
		}


		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	

	private void cmbToData() {
		cmbTo.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			if(cmbFrom.getItemCaption(cmbFrom.getValue()).equalsIgnoreCase("Tube Section"))
			{

				query="select Distinct  StepId,StepName  from tbProductionStep a  inner join  tbProductionType b on  a.productionTypeId=b.productTypeId " 
						+"where   b.productTypeName like '%Label Production%' and a.StepName like 'Printing' ";

			}
			if(cmbFrom.getItemCaption(cmbFrom.getValue()).equalsIgnoreCase("Printing"))
			{
				query="select distinct StepId,StepName  from  tbProductionStep where StepName not like 'Printing' and StepId like '%Level%' ";

			}

			System.out.println("To: "+query);


			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				System.out.print("Is This OK");

				Object[] element=(Object[]) iter.next();

				cmbTo.addItem(element[0]);
				cmbTo.setItemCaption(element[0], (String) element[1]);
			}
		}


		catch(Exception exp){
			//showNotification("CmbTOData: "+exp);
			System.out.println(exp);
		}
	}

	private void joborderNoLoad() 
	{
		cmbFinishedGoods.removeAllItems();

		Transaction tx=null;
		String query=null;

		try
		{

			query="select distinct 0, orderNo from tbJobOrderDetails where productionType "
					  +" like (select productTypeId from tbProductionType where productTypeName like 'Label Production') ";
			System.out.println("Qyery is"+query);
			
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();

				cmbjoborderNo.addItem(element[1]);
			}
		}

		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	private void FinishUnitData() 
	{
		txtUnit.setValue("");

		Transaction tx=null;
		String query=null;

		try
		{

			query= " select 0,vUnitName from tbFinishedProductInfo where vProductId like '"+cmbFinishedGoods.getValue().toString()+"' ";

			System.out.println(query);

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				Object[] element=(Object[]) iter.next();

				txtUnit.setValue(element[1].toString());
			}
		}


		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}


	private void tableInitialiseFgNew()
	{
		int n;
		
		for(int i = 0; i < 4; i++)
		{
			tableRowAddRMFgNew(i);	
		}
		
		
	}
	
	

	private void saveButtonEvent() 
	{
		
	
		if(cmbjoborderNo.getValue()!=null)
		{

			if (!txtChallanNo.getValue().toString().isEmpty()) 
			{
				if(cmbFrom.getValue()!=null)
				{
				 if(cmbTo.getValue()!=null)
				 {
					 if(cmbFinishedGoods.getValue()!=null)
					 {
						 if(itemcheck())
						 {
							 if (isUpdate) 
								{
									final MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
									mb.show(new EventListener() 
									{
										public void buttonClicked(ButtonType buttonType) 
										{
											
											if (buttonType == ButtonType.YES) 
											{
												mb.buttonLayout.getComponent(0).setEnabled(false);
												Transaction tx = null;
												Session session = SessionFactoryUtil.getInstance().getCurrentSession();
												tx = session.beginTransaction();
												
												if (deleteData(session, tx) && nullCheck())
												{
													insertData(session,tx);
												}
												else 
												{
													tx.rollback();
												}

												componentIni(true);
												btnIni(true);
												tableClear();
												txtClear();
											}
										}
									});
								} 
								else
								{
									final MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
									mb.show(new EventListener() 
									{
										public void buttonClicked(ButtonType buttonType) 
										{
											if(buttonType == ButtonType.YES)
											{
												mb.buttonLayout.getComponent(0).setEnabled(false);
												Transaction tx = null;
												Session session = SessionFactoryUtil.getInstance().getCurrentSession();
												tx = session.beginTransaction();
												
												insertData(session,tx);
												componentIni(true);
												btnIni(true);
												tableClear();
												txtClear();							
											}
										}
									});
								}	 
						 }
						 else
						 {
							 this.getParent().showNotification("Warning :","Please Select Product Name.",Notification.TYPE_WARNING_MESSAGE);	 
						 }   tbcmbRM.get(0).focus();
						 	 
					 }
					 else
					 {
						 this.getParent().showNotification("Warning :","Please Select Finished Goods.",Notification.TYPE_WARNING_MESSAGE);
						 txtIssueQty.focus();
					 }
						 
				 }
				 else
				 {
					 this.getParent().showNotification("Warning :","Please Select Issue To.",Notification.TYPE_WARNING_MESSAGE);
					 cmbFinishedGoods.focus();
				 }
						
				}
				else
				{
					this.getParent().showNotification("Warning :","Please Select Issue From.",Notification.TYPE_WARNING_MESSAGE);
					cmbTo.focus();
				}
				
			} 
			else
			{
				this.getParent().showNotification("Warning :","Please Select Challan No",Notification.TYPE_WARNING_MESSAGE);
				cmbFrom.focus();
			}	
		}
		else
		{   
			this.getParent().showNotification("Warning :","Please Select Job Order No",Notification.TYPE_WARNING_MESSAGE);
			txtChallanNo.focus();;
			 
		}  
		
	}

	private void insertData(Session session,Transaction tx)
	{

		try{
			System.out.println("Next Issue Is"+totalissue);
			
			Double totalissue=0.00;
			for(int i=0;i<tbcmbRM.size();i++)
			{
				if(!tbtxtIssueSqm.get(i).getValue().toString().isEmpty())
				{
					totalissue=totalissue+ Double.parseDouble(tbtxtIssueSqm.get(i).getValue().toString()) ;		
				}	
			}
			
			if(isFind)
			{
				totalissue=totalissue-ttlissue;	
			}
			
			
			

	
			String sql="insert into tbLabelIssueInfo values('"+txtJobNo.getValue().toString()+"','"+dFormatSql.format(dJobDate.getValue())+"'," +
					"'"+txtChallanNo.getValue().toString()+"','"+cmbFrom.getValue().toString()+"','"+cmbTo.getValue().toString()+"'," +
					"'"+cmbFinishedGoods.getValue().toString()+"','"+txtUnit.getValue().toString()+"','"+txtRemarks.getValue().toString()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"' ,CURRENT_TIMESTAMP,1,'"+totalissue+"', '1','"+cmbjoborderNo.getValue().toString()+"')";
			session.createSQLQuery(sql).executeUpdate();
			
			for(int a=0;a<tbcmbRM.size();a++){

				if(tbcmbRM.get(a).getValue()!=null&&!tbtxtIssueSqm.get(a).getValue().toString().isEmpty()){

					String sql1="insert into tbLabelIssueDetails values('"+txtJobNo.getValue().toString().trim()+"'," +
							"'"+cmbjoborderNo.getValue().toString()+"','"+tbcmbRM.get(a).getValue().toString()+"','"+
							tbtxtsectionStock.get(a).getValue()+"','"+tbtxtfloorStock.get(a).getValue()+"','"+tbtxtPrintedSqm.get(a).getValue()+"'," +
							"'"+tbtxtUnit.get(a).getValue().toString()+"','"+tbtxtIssueSqm.get(a).getValue()+"', '"+tbtxtRemarks.get(a).getValue().toString()+"' ,'"+sessionBean.getUserIp()+"'," +
							"'"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
					session.createSQLQuery(sql1).executeUpdate();
				}
			}

			
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			totalissue=0.00;
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("From Insert"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	
	
	
	private Date getTime()
	{
		Date time = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = null;

		try
		{
			String sql = "";

			sql = "select convert(time, CURRENT_TIMESTAMP)";
			System.out.println("time sql"+sql);

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if(iter.hasNext())
			{
				time = (Date) iter.next();
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("getTime error: "+ex, Notification.TYPE_ERROR_MESSAGE);
		}

		return time;
	}

	private boolean nullCheck() 
	{
		if (cmbFinishedGoods.getValue() != null)
		{
			for (int i = 0; i < tbcmbRM.size(); i++) 
			{
				Object temp = tbcmbRM.get(i).getItemCaption(tbcmbRM.get(i).getValue());

				System.out.println(tbcmbRM.get(i).getValue());

				if (temp != null && !tbcmbRM.get(i).getValue().equals(("x#" + i))) 
				{
					if (tbcmbRM.get(i).getValue()!=null) 
					{
						return true;
					} 
					else
					{
						this.getParent().showNotification("Warning :","Please Enter Valid Qty .",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Section To .",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}
	
	private boolean itemcheck() 
	{
		for(int i=0;i<tbcmbRM.size();i++)
		{
			if(tbcmbRM.get(i).getValue()!=null)
			{
			  return true;	
			}
		}
		
		return false;
	}
	

	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete tbLabelIssueInfo where jobNo='"+txtJobNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbLabelIssueInfo where jobNo='"+txtJobNo.getValue()+ "' ");

			session.createSQLQuery("delete tbLabelIssueDetails where jobNo='"+txtJobNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbLabelIssueDetails where jobNo='"+txtJobNo.getValue()+ "' ");

			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private String autoJobNo()
	{
		String autoNo=null;
		Transaction tx;

		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select cast(isnull(max(cast(replace(jobNo, '', '')as int))+1, 1)as varchar) from tbLabelIssueInfo").list().iterator();

			if(iter.hasNext())
			{
				autoNo=iter.next().toString().trim();
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}

		return autoNo;
	}
}
