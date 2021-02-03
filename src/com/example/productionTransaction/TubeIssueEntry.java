package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.*;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
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
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;


public class TubeIssueEntry extends Window {

	SessionBean sessionBean;
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Preview","","Exit");
	private AbsoluteLayout mainLayout1;

	private Label lbljobNo = new Label("Job No :");
	private TextRead txtjobno = new TextRead();
	private Label lbljobdate = new Label("Job Date:");
	private PopupDateField djobdate = new PopupDateField();
	private Label lblchallanNo = new Label("Challan No :");
	private TextField challanNo = new TextField();
	private Label lblIssueFrom = new Label("From:");
	private ComboBox cmbIssueFrom = new ComboBox(); 
	private Label lblIssueTo = new Label("To :");
	private ComboBox cmbIssueTo = new ComboBox();
	private Label lblFinishedGoods = new Label("Finished Goods:");
	private ComboBox cmbfinishedGoods = new ComboBox();
	private Label lblDia = new Label("Dia:");
	private TextRead txtDia;
	private Label lblshoulderWeight = new Label("Shoulder Weight:");
	private TextRead txtshoulderweight;

	private Label lblHdpeAndMb = new Label("HDPE AND MASTERBATCH:");
	private Label lblrawmaterials = new Label("RAW MATERIALS :");
	private Label lbljoborderNo=new Label("Job Order No :");
	private ComboBox cmbjoborderNo=new ComboBox();

	private Table tbhdpe= new Table();

	//HDPE AND MB
	private ArrayList<Label> lblslmb = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProductmb = new ArrayList<ComboBox>();
	private ArrayList<TextRead> txtunitmb = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> SectionstockQtymb = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> FloorstockQtymb = new ArrayList<TextRead>(1);
	private ArrayList<AmountField>sectionIssuemb = new ArrayList<AmountField>();
	private ArrayList<TextRead> wastagestock = new ArrayList<TextRead>();
	private ArrayList<AmountField> wastageIssuemb = new ArrayList<AmountField>();
	private ArrayList<TextRead> totalIssurmb = new ArrayList<TextRead>(1);
	private ArrayList<TextField> txtremarksmb = new ArrayList<TextField>(1);

	private Table table = new Table();

	//Rawmaterials

	private ArrayList<Label> lblsl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> unit = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> UPSectionstockQty = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> UPFloorstockQty = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> stockQty = new ArrayList<AmountField>();
	private ArrayList<TextRead> printedPcs = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> targetqty = new ArrayList<TextRead>();
	private ArrayList<AmountField> Issueqty = new ArrayList<AmountField>();
	private ArrayList<AmountCommaSeperator>IssueTarget= new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextField>txtremarks= new ArrayList<TextField>();


	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat df2 = new DecimalFormat("#0.00");
	private DecimalFormat df1 = new DecimalFormat("#0");
	private TextField txtChallanNo;
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");

	private Label lbLine=new Label("<b><font color='#e65100'>======================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);

	private HashMap hRate = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();
	private HashMap hreverse = new HashMap();

	private TextField txtReceiptId=new TextField();
	Double ttlissue=0.00;
	Double totalissue=0.00;

	private PopupDateField dateField = new PopupDateField();
	//private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat decimalf = new DecimalFormat("#0.00");

	private Label amountWordLabel = new Label("Amount In Words: ");
	private Label totalLabel = new Label("Total  : ");
	private Label lbllank= new Label();
	private TextField amountWordsField = new TextField();

	private TextRead totalField = new TextRead(1);
	private TextRead txtIssueCaption,txtPrintedCaption;
	private Label lblIssueCaption,lblPrintedCaption;

	private Label label = new Label();
	private Label l1 = new Label();
	private Label l2 = new Label();
	boolean isUpdate=false,isFind=false;
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	OptionGroup Loantype;
	private static final List<String>areatype  = Arrays.asList(new String[] {"Production" });
	
	private NativeButton btnProductionEntry = new NativeButton();

	public TubeIssueEntry(SessionBean sessionBean) {

		this.sessionBean=sessionBean;
		this.setCaption(" TUBE ISSUE ENTRY::"+sessionBean.getCompany());
		this.setResizable(false);
		this.addComponent(buildMainLayout());
		componentIni(true);
		btnIni(true);
		setEventAction();
		sectionDataLoad();
		joborderDataLoad();
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(djobdate);
		allComp.add(txtChallanNo);
		allComp.add(cmbIssueFrom); 
		allComp.add(cmbIssueTo);
		allComp.add(cmbjoborderNo);
		allComp.add(cmbfinishedGoods);
		

		for(int i=0;i<lblslmb.size();i++)
		{
			allComp.add(cmbProductmb.get(i));
			allComp.add(sectionIssuemb.get(i));	
			allComp.add(wastageIssuemb.get(i));
		}


		for(int i=0;i<cmbProduct.size();i++)
		{
			allComp.add(cmbProduct.get(i));
			allComp.add(Issueqty.get(i));	
		}
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);

		new FocusMoveByEnter(this,allComp);

		cButton.btnNew.focus();

	}

	private void sectionDataLoad() 
	{
		cmbIssueFrom.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			query= "  select * from  "
					+"( "
					+"	select '1' as type, CAST(AutoID as varchar(120)) as id ,SectionName as section  from tbSectionInfo where SectionName like '%Tube Sec%' "
					+"	union "
					+"	select distinct '2' as type,  StepId as id ,StepName section  from tbProductionStep a  "
					+"	inner join  "
					+"	tbProductionType b  "
					+"	on a.productionTypeId=b.productTypeId "
					+"	where b.productTypeName like '%Tube%' "
					+"	) as a  order by a.type ";

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbIssueFrom.addItem(element[1].toString());
				cmbIssueFrom.setItemCaption(element[1].toString(), (String) element[2]);
			}
		}

		catch(Exception exp){
			System.out.println(exp);
		}
	}
	
	
	private void joborderDataLoad() 
	{
		cmbjoborderNo.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			query= "select 0,orderNo from tbJobOrderInfo where orderNo in(select orderNo from tbJobOrderDetails where productionType in('PT-1')) order by autoId";

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbjoborderNo.addItem(element[1].toString());
				
			}
		}

		catch(Exception exp){
			System.out.println(exp);
		}
	}
	
	

	private void FinishedGoodsDataLoad()
	{
		cmbfinishedGoods.removeAllItems();
		Transaction tx=null;
		String query=null;

		try
		{

			query="select distinct  fgId ,(select vProductName from tbFinishedProductInfo where vProductId like fgId) as productName from "
					+" tbJobOrderDetails where orderNo like '"+cmbjoborderNo.getValue().toString()+"'";
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbfinishedGoods.addItem(element[0].toString());
				cmbfinishedGoods.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}


	private void issueToDataLoad() {
		cmbIssueTo.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Tube Section"))
			{

				query="select Distinct  StepId,StepName  from tbProductionStep a  inner join  tbProductionType b on  a.productionTypeId=b.productTypeId " 
						+"where   b.productTypeName like 'Tube Production' and a.StepName like 'Printing' ";

			}
			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Printing"))
			{
				query="select distinct StepId,StepName  from  tbProductionStep where StepName like 'Tubing' ";

			}
			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Tubing"))
			{
				query="select Distinct  StepId,StepName  from tbProductionStep where  StepName like 'Shouldering' ";

			}
			if(cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()).equalsIgnoreCase("Shouldering"))
			{
				query="select Distinct  StepId,StepName  from tbProductionStep where  StepName like 'sealing' ";

			}


			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				System.out.print("Is This OK");

				Object[] element=(Object[]) iter.next();

				cmbIssueTo.addItem(element[0]);
				cmbIssueTo.setItemCaption(element[0], (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}



	private AbsoluteLayout buildMainLayout(){

		mainLayout1 = new AbsoluteLayout();
		mainLayout1.setImmediate(false);
		mainLayout1.setWidth("1180px");
		mainLayout1.setHeight("577px");
		mainLayout1.setMargin(false);

		// top-level component properties
		setWidth("1220px");
		setHeight("650px");



		lbljobNo.setWidth("-1px");
		lbljobNo.setHeight("-1px");
		lbljobNo.setImmediate(false);


		txtjobno.setWidth("100px");
		txtjobno.setHeight("24px");
		txtjobno.setImmediate(true);


		lbljobdate.setWidth("-1px");
		lbljobdate.setHeight("-1px");
		lbljobdate.setImmediate(false);


		djobdate = new PopupDateField();
		djobdate.setImmediate(true);
		djobdate.setWidth("107px");
		djobdate.setDateFormat("dd-MM-yyyy");
		djobdate.setValue(new java.util.Date());
		djobdate.setResolution(PopupDateField.RESOLUTION_DAY);



		lblchallanNo.setWidth("-1px");
		lblchallanNo.setHeight("-1px");
		lblchallanNo.setImmediate(false);


		txtChallanNo=new TextField();
		txtChallanNo.setWidth("100px");
		txtChallanNo.setHeight("24px");
		txtChallanNo.setImmediate(true);


		lblIssueFrom.setWidth("-1px");
		lblIssueFrom.setHeight("-1px");
		lblIssueFrom.setImmediate(false);


		cmbIssueFrom.setWidth("250px");
		cmbIssueFrom.setNewItemsAllowed(true);
		cmbIssueFrom.setNullSelectionAllowed(true);
		cmbIssueFrom.setImmediate(true);


		lblIssueTo.setWidth("-1px");
		lblIssueTo.setHeight("-1px");
		lblIssueTo.setImmediate(false);


		cmbIssueTo.setWidth("250px");
		cmbIssueTo.setNewItemsAllowed(true);
		cmbIssueTo.setNullSelectionAllowed(true);
		cmbIssueTo.setImmediate(true);
		
		
		lbljoborderNo.setWidth("-1px");
		lbljoborderNo.setHeight("-1px");
		lbljoborderNo.setImmediate(false);


		cmbjoborderNo.setWidth("150px");
		cmbjoborderNo.setNewItemsAllowed(true);
		cmbjoborderNo.setNullSelectionAllowed(true);
		cmbjoborderNo.setImmediate(true);
		
		
		

		lblFinishedGoods.setWidth("-1px");
		lblFinishedGoods.setHeight("-1px");
		lblFinishedGoods.setImmediate(false);


		cmbfinishedGoods.setWidth("250px");
		cmbfinishedGoods.setNewItemsAllowed(true);
		cmbfinishedGoods.setNullSelectionAllowed(true);
		cmbfinishedGoods.setImmediate(true);


		lblDia.setWidth("-1px");
		lblDia.setHeight("-1px");
		lblDia.setImmediate(false);


		txtDia=new TextRead(1);
		txtDia.setWidth("100px");
		txtDia.setHeight("24px");
		txtDia.setImmediate(true);
		//		txtDia.setEnabled(false);


		lblshoulderWeight.setWidth("-1px");
		lblshoulderWeight.setHeight("-1px");
		lblshoulderWeight.setImmediate(false);


		txtshoulderweight=new TextRead(1);
		txtshoulderweight.setWidth("100px");
		txtshoulderweight.setHeight("24px");
		txtshoulderweight.setImmediate(true);
		//		txtshoulderweight.setEnabled(false);


		lblHdpeAndMb = new Label("<font color='blue' size='2px'><b>HDPE AND MASTERBATCH :</b></font>", Label.CONTENT_XHTML);
		lblHdpeAndMb.setWidth("-1px");
		lblHdpeAndMb.setHeight("-1px");
		lblHdpeAndMb.setImmediate(false);

		tbhdpe.setWidth("100%");
		tbhdpe.setHeight("130px");
		tbhdpe.setEnabled(false);

		tbhdpe.addContainerProperty("SL", Label.class , new Label());
		tbhdpe.setColumnWidth("SL",20);
		tbhdpe.addContainerProperty("R/M NAME", ComboBox.class , new ComboBox());
		tbhdpe.setColumnWidth("R/M NAME",270);
		tbhdpe.addContainerProperty("UNIT", TextRead.class , new TextRead());
		tbhdpe.setColumnWidth("UNIT",60);
		tbhdpe.setColumnAlignment("UNIT", Table.ALIGN_CENTER);
		tbhdpe.addContainerProperty("Section Stock", TextRead.class , new TextRead());
		tbhdpe.setColumnWidth("Section Stock",100);
		tbhdpe.addContainerProperty("FLR Stock Qty", TextRead.class , new TextRead());
		tbhdpe.setColumnWidth("FLR Stock Qty",100);
		tbhdpe.addContainerProperty("Section Issue", AmountField.class , new AmountField());
		tbhdpe.setColumnWidth("Section Issue",100);
		tbhdpe.addContainerProperty("Wastage Stock", TextRead.class , new TextRead());
		tbhdpe.setColumnWidth("Wastage Stock",80);
		tbhdpe.addContainerProperty("Wastage Issue", AmountField.class , new AmountField());
		tbhdpe.setColumnWidth("Wastage Issue",80);
		tbhdpe.addContainerProperty("Total", TextRead.class , new TextRead());
		tbhdpe.setColumnWidth("Total",80);
		tbhdpe.addContainerProperty("Remarks", TextField.class , new TextField());
		tbhdpe.setColumnWidth("Remarks",140);


		lblrawmaterials = new Label("<font color='blue' size='2px'><b>RAW MATERIALS :</b></font>", Label.CONTENT_XHTML);
		lblrawmaterials.setWidth("-1px");
		lblrawmaterials.setHeight("-1px");
		lblrawmaterials.setImmediate(false);


		// txtWastage
		txtIssueCaption = new TextRead();
		txtIssueCaption.setImmediate(true);
		txtIssueCaption.setWidth("185px");
		txtIssueCaption.setHeight("20px");
		txtIssueCaption.setStyleName("txtcolor");

		// lblIssue
		lblIssueCaption = new Label("<b><font color='#fff'>ISSUE</font></b>", Label.CONTENT_XHTML);
		lblIssueCaption.setImmediate(false);
		lblIssueCaption.setWidth("-1px");
		lblIssueCaption.setHeight("-1px");

		// txtWastage
		txtPrintedCaption = new TextRead();
		txtPrintedCaption.setImmediate(true);
		txtPrintedCaption.setWidth("225px");
		txtPrintedCaption.setHeight("20px");
		txtPrintedCaption.setStyleName("txtcolor");

		// lblIssue
		lblPrintedCaption = new Label("<b><font color='#fff'>PRINTED STOCK</font></b>", Label.CONTENT_XHTML);
		lblPrintedCaption.setImmediate(false);
		lblPrintedCaption.setWidth("-1px");
		lblPrintedCaption.setHeight("-1px");
		
		// btnProductionEntry
		btnProductionEntry = new NativeButton();
		btnProductionEntry.setCaption("Go to Production Entry");
		btnProductionEntry.setImmediate(true);
//		btnProductionEntry.setWidth("48px");
//		btnProductionEntry.setHeight("24px");

		table.setWidth("100%");
		table.setHeight("130px");

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",20);
		table.addContainerProperty("R/M NAME", ComboBox.class , new ComboBox());
		table.setColumnWidth("R/M NAME",250);
		table.addContainerProperty("UNIT", TextRead.class , new TextRead());
		table.setColumnWidth("UNIT",60);
		table.setColumnAlignment("UNIT", Table.ALIGN_CENTER);

		table.addContainerProperty("UNP Sec Stock Qty", TextRead.class , new TextRead());
		table.setColumnWidth("UNP Sec Stock Qty",75);
		table.addContainerProperty("UNP FLR Stock Qty", TextRead.class , new TextRead());
		table.setColumnWidth("UNP FLR Stock Qty",75);
		table.addContainerProperty("Printed SQM", AmountField.class , new AmountField());
		table.setColumnWidth("Printed SQM",100);
		table.addContainerProperty("Printed Pcs", TextRead.class , new TextRead());
		table.setColumnWidth("Printed Pcs",100);


		table.addContainerProperty("TARGET Qty", TextRead.class , new TextRead());
		table.setColumnWidth("TARGET Qty",80);
		table.addContainerProperty("ISSUE SQM", AmountField.class , new AmountField());
		table.setColumnWidth("ISSUE SQM",80);
		table.addContainerProperty("ISSUE PCS", AmountCommaSeperator.class , new AmountCommaSeperator());
		table.setColumnWidth("ISSUE PCS",80);
		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",100);

		table.setColumnCollapsingAllowed(true);
		//tableInitialise();
		//mainLayout1.addComponent(table,"top:120.0px;left:0.0px;");
		mainLayout1.addComponent(lbljobNo,"top:15.0px;left:15.0px;");
		mainLayout1.addComponent(txtjobno,"top:17.0px;left:115.0px");
		mainLayout1.addComponent(lbljobdate,"top:41.0px;left:15.0px;");
		mainLayout1.addComponent(djobdate,"top:43.0px;left:115.0px");
		mainLayout1.addComponent(lblchallanNo,"top:67.0px;left:15.0px;");
		mainLayout1.addComponent(txtChallanNo,"top:69.0px;left:115.0px;");
		mainLayout1.addComponent(lblIssueFrom,"top:15.0px;left:235.0px;");
		mainLayout1.addComponent(cmbIssueFrom,"top:17.0px;left:335px;");
		mainLayout1.addComponent(lblIssueTo,"top:41.0px;left:235.0px;");
		mainLayout1.addComponent(cmbIssueTo,"top:43.0px;left:335px;");
		
		mainLayout1.addComponent(lbljoborderNo,"top:69.0px;left:235.0px;");
		mainLayout1.addComponent(cmbjoborderNo,"top:67.0px;left:335px;");
		
		mainLayout1.addComponent(lblFinishedGoods,"top:15.0px;left:605.0px;");
		mainLayout1.addComponent(cmbfinishedGoods,"top:17.0px;left:720px;");
		
		mainLayout1.addComponent(lblDia,"top:41.0px;left:605.0px;");
		mainLayout1.addComponent(txtDia,"top:43.0px;left:720.0px");
		mainLayout1.addComponent(lblshoulderWeight,"top:67.0px;left:605.0px;");
		mainLayout1.addComponent(txtshoulderweight,"top:69.0px;left:720.0px");
		
		mainLayout1.addComponent(lblHdpeAndMb,"top:115.0px;left:15.0px");
		mainLayout1.addComponent(tbhdpe,"top:140.0px;left:0.0px;");
		tbhdpeInitialize();
		mainLayout1.addComponent(lblrawmaterials,"top:280.0px;left:15.0px");

		mainLayout1.addComponent(txtIssueCaption,"top:278px;left:867px;");
		mainLayout1.addComponent(lblIssueCaption,"top:278px;left:947px;");
		
		mainLayout1.addComponent(txtPrintedCaption,"top:278px;left:547px;");
		mainLayout1.addComponent(lblPrintedCaption,"top:278px;left:600px;");

		mainLayout1.addComponent(table,"top:300.0px;left:0.0px;");
		tableInitialise();
		mainLayout1.addComponent(lbLine,"top:490px;left:20.0px;");
		mainLayout1.addComponent(cButton,"top:520px;left:250px;");
		mainLayout1.addComponent(btnProductionEntry,"top:550px;left:477px;");


		return mainLayout1;	
	}

	private void tableClear()
	{
		for(int a=0;a<Issueqty.size();a++){
			cmbProduct.get(a).removeAllItems();
			cmbProduct.get(a).setValue("");
			UPSectionstockQty.get(a).setValue("");
			UPFloorstockQty.get(a).setValue("");
			stockQty.get(a).setValue("");
			unit.get(a).setValue("");
			targetqty.get(a).setValue("");
			Issueqty.get(a).setValue("");
			IssueTarget.get(a).setValue("");
			txtremarks.get(a).setValue("");
		}
	}

	private void tbhdpeclear(){
		for(int a=0;a<lblslmb.size();a++)
		{
			cmbProductmb.get(a).removeAllItems();
			cmbProduct.get(a).setValue("");
			txtunitmb.get(a).setValue("");
			SectionstockQtymb.get(a).setValue("");
			FloorstockQtymb.get(a).setValue("");
			sectionIssuemb.get(a).setValue("");
			wastageIssuemb.get(a).setValue("");
			totalIssurmb.get(a).setValue("");
			txtremarksmb.get(a).setValue("");
		}
	}



	private void insertData(Session session,Transaction tx) {

		try{
			if(isFind)
			{
				totalissue=totalissue-ttlissue;	
			}

			String sql="insert into tbTubeIssueInfo values('"+txtjobno.getValue().toString().trim()+"','"+dateFormat.format(djobdate.getValue())+"'," +
					"'"+txtChallanNo.getValue().toString()+"','"+cmbIssueFrom.getValue().toString()+"','"+cmbIssueTo.getValue().toString()+"'," +
					"'"+cmbfinishedGoods.getValue().toString()+"',1,'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,1,'"+totalissue+"','"+cmbjoborderNo.getValue().toString()+"')";
			session.createSQLQuery(sql).executeUpdate();
			for(int a=0;a<cmbProduct.size();a++){

				if(cmbProduct.get(a).getValue()!=null&&!Issueqty.get(a).getValue().toString().isEmpty()){

					String sql1="insert into tbTubeIssueDetails values('"+txtjobno.getValue().toString().trim()+"'," +
							"'"+cmbProduct.get(a).getValue()+"','"+UPSectionstockQty.get(a).getValue()+"','"+
							UPFloorstockQty.get(a).getValue()+"','"+stockQty.get(a).getValue()+"','"+unit.get(a).getValue()+"'," +
							"'"+Issueqty.get(a).getValue()+"','"+txtremarks.get(a).getValue()+"','"+sessionBean.getUserIp()+"'," +
							"'"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP)";
					session.createSQLQuery(sql1).executeUpdate();
				}
			}

			for(int i=0;i<cmbProductmb.size();i++){

				if(cmbProductmb.get(i).getValue()!=null){

					String query = 	" insert into tbTubeHDPEIssueDetails" +
							" (jobNo, productId, sectionissue, wastageissue, userIp, userid, entryTime)" +
							" values" +
							" ('"+txtjobno.getValue().toString().trim()+"'," +
							" '"+cmbProductmb.get(i).getValue().toString()+"'," +
							" '"+sectionIssuemb.get(i).getValue().toString()+"'," +
							" '"+wastageIssuemb.get(i).getValue().toString()+"'," +
							" '"+sessionBean.getUserIp()+"'," +
							" '"+sessionBean.getUserId()+"'," +
							" CURRENT_TIMESTAMP)";
					session.createSQLQuery(query).executeUpdate();
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
	public void saveButtonEvent(){
		if(!txtChallanNo.getValue().toString().isEmpty()){
			if(cmbIssueFrom.getValue()!=null){
				if(cmbIssueTo.getValue()!=null){
					if(cmbfinishedGoods.getValue()!=null){
						if(cmbProduct.get(0).getValue()!=null&&!Issueqty.get(0).toString().isEmpty()){

							if(isUpdate)
							{
								this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
										new YesNoDialog.Callback() {
									public void onDialogResult(boolean yes) {
										if(yes){
											Transaction tx=null;
											Session session = SessionFactoryUtil.getInstance().getCurrentSession();
											tx = session.beginTransaction();
											if(deleteData(session,tx))
												insertData(session,tx);
											else{
												tx.rollback();
											}
											isUpdate=false;
											refreshButtonEvent();
										}
									}
								}));
							}else{
								this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
										new YesNoDialog.Callback() {



									public void onDialogResult(boolean yes) 
									{

										if(yes)
										{
											Transaction tx=null;
											Session session = SessionFactoryUtil.getInstance().getCurrentSession();
											tx = session.beginTransaction();

											//if(nullCheck())
											insertData(session,tx);
											refreshButtonEvent();	
										}	
									}

								}));
							}
						}
						else{
							showNotification("Please Provide all Information",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else{
						showNotification("Please Select Finished Goods",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Please Select Issue To",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Select Issue From",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Challan No",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	public void setEventAction(){


		cmbIssueFrom.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbIssueFrom.getValue()!=null)
				{
					issueToDataLoad();
					if(selectFrom()||cmbIssueFrom.getValue().toString().equalsIgnoreCase("TubeSTP-1")){
						for(int a=0;a<lblsl.size();a++){
							IssueTarget.get(a).setEnabled(false);
							Issueqty.get(a).setEnabled(true);
						}
					}
					else{
						for(int a=0;a<lblsl.size();a++){
							IssueTarget.get(a).setEnabled(true);
							Issueqty.get(a).setEnabled(false);
						}
					}


				}
			}
		});
		cmbIssueTo.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {
				if(cmbIssueTo.getValue()!=null){
					//FinishedGoodsDataLoad();

					//					if(isFind)
					//					{
					if(cmbIssueTo.getValue().toString().equalsIgnoreCase("TubeSTP-3"))
					{
						tbhdpe.setEnabled(true);
					}
					else
					{
						tbhdpe.setEnabled(false);
					}
				}
			}
			//			}
		});
		cmbfinishedGoods.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				tableClear();
				if(cmbfinishedGoods.getValue()!=null&&cmbIssueFrom.getValue()!=null&cmbIssueTo.getValue()!=null){
					cmbMbData();
					cmbProductData();
				}



			}
		});
		
		
		cmbjoborderNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{

				tableClear();
				if(cmbjoborderNo.getValue()!=null){
					FinishedGoodsDataLoad();
				}



			}
		});
		
		
		cButton.btnSave.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				saveButtonEvent();
			}
		});

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{

				newButtonEvent();
				autoIssueNo();
				isFind=false;

			}


		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				updateButtonEvent();
				if(isFind)
				{
					if(cmbIssueTo.getValue().toString().equalsIgnoreCase("TubeSTP-3"))
					{
						tbhdpe.setEnabled(true);
					}
					else
					{
						tbhdpe.setEnabled(false);
					}
				}

			}
		});

		cButton.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//deleteButtonEvent();
				isFind=false;
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{				
				refreshButtonEvent();
				tbhdpe.setEnabled(false);
				isFind=false;
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

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtChallanNo.getValue().toString().isEmpty()&&cmbProduct.get(0).getValue()!=null
						&&!Issueqty.get(0).getValue().toString().isEmpty()&&!targetqty.get(0).getValue().toString().isEmpty())

				{
					reportView();
				}

				else
				{
					showNotification("Warning!","Find a Issue No to genarate Challan",Notification.TYPE_WARNING_MESSAGE);	
				}


			}
		});

		cButton.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		
		btnProductionEntry.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				productionEntryLink();
			}
		});

	}
	
	public void productionEntryLink()
	{
		Window win = new TubeProductionEntry(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				
			}
		});
		this.getParent().addWindow(win);
	}
	
	private void reportView()
	{	
		String query=null;
		Transaction tx=null;

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//			hm.put("fromdate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("issueFrom",cmbIssueFrom.getItemCaption(cmbIssueFrom.getValue()) );
			hm.put("issueTo",cmbIssueTo.getItemCaption(cmbIssueTo.getValue()) );

			query= " select  a.IssueDate,a.IssueNo,a.ChallanNo, a.FinishedGood ,b.IssueQty,b.RMitemCode ,case when  Fromflag='1'  then c.vRawItemName  else d.vProductName  end as  productName,b.unit, e.vProductName as finishProductName     from tbTubeIssueInfo a "
					+" inner join "
					+" tbTubeIssueDetails b "
					+"on a.IssueNo=b.IssueNo  "
					+"left join "
					+"tbRawItemInfo c " 
					+"on c.vRawItemCode=b.RMitemCode "
					+"left join "
					+"tbFinishedProductInfo d "
					+"on d.vProductId =b.RMitemCode "
					+"left join "
					+"tbFinishedProductInfo e "
					+"on e.vProductId=a.FinishedGood "
					+"where a.IssueFrom like '"+cmbIssueFrom.getValue()+"' and a.IssueTo like '"+cmbIssueTo.getValue()+"' and a.ChallanNo like '"+txtChallanNo.getValue()+"'";

			System.out.println(query);
			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptTubeIssue.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
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

			String query = "select ISNULL(MAX(CAST(IssueNo as int )) ,0)+1 from tbTubeIssueInfo";

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



	private void findButtonEvent(){

		Window win=new TubeFindWindow(sessionBean,txtReceiptId,"rawIssue");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtReceiptId.getValue().toString().length()>0)
					System.out.println("Not Done");
				findInitialise();

			}
		});


		this.getParent().addWindow(win);

	}

	private void findInitialise(){

		Transaction tx = null;
		try
		{

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			txtClear();
			System.out.println(txtReceiptId.getValue());

			String sql="select a.IssueNo,a.IssueDate,a.ChallanNo,a.IssueFrom,a.IssueTo,a.FinishedGood,b.RMitemCode,"+
					" b.IssueQty, c.productId, c.sectionissue, c.wastageissue,a.JobOrderNo" +
					" from tbTubeIssueInfo a"+
					" inner join tbTubeIssueDetails b" +
					" on a.IssueNo=b.IssueNo" +
					" left join tbTubeHDPEIssueDetails c" +
					" on a.IssueNo = c.jobNo"+
					" where a.IssueNo like '"+txtReceiptId.getValue()+"'";
			List list=session.createSQLQuery(sql).list();
			Iterator iter=list.iterator();
			int a=list.size(),x=0;
			while(a>0){

				if(iter.hasNext()){
					Object element[]=(Object[]) iter.next();
					txtjobno.setValue(element[0]);
					djobdate.setValue(element[1]);
					txtChallanNo.setValue(element[2]);
					cmbIssueFrom.setValue(element[3]);
					cmbIssueTo.setValue(element[4]);
					cmbjoborderNo.setValue(element[11]);
					cmbfinishedGoods.setValue(element[5]);
					cmbProduct.get(x).setValue(element[6]);
					Issueqty.get(x).setValue(df2.format(element[7]));
					if(element[8]!=null || element[9]!=null || element[10]!=null)
					{
						cmbProductmb.get(x).setValue(element[8]);
						sectionIssuemb.get(x).setValue(element[9]);
						wastageIssuemb.get(x).setValue(element[10]);
					}

					System.out.println(targetqty.get(x).getValue());
				}
				x++;
				a--;
			}
		}catch(Exception exp)
		{
			this.getParent().showNotification(
					"Error2",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);	
		}


	}


	private void refreshButtonEvent() {

		componentIni(true);
		btnIni(true);
		txtClear();
		cButton.btnNew.focus();
	}


	private boolean deleteGodwonData(String itemId,Session session,Transaction tx){

		try{
			session.createSQLQuery("delete tbRawGodownStockReport  where ItemCode='"+itemId+"'").executeUpdate();

			return true;

		}
		catch(Exception exp){

			this.getParent().showNotification("Error3",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}

	}

	public boolean deleteData(Session session,Transaction tx){


		try{
			session.createSQLQuery("delete tbTubeIssueInfo  where IssueNo='"+txtjobno.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete tbTubeIssueDetails  where IssueNo='"+txtjobno.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete tbTubeHDPEIssueDetails  where jobNo='"+txtjobno.getValue()+"'").executeUpdate();
			return true;

		}
		catch(Exception exp){

			this.getParent().showNotification("Error4",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}


	}

	private void updateButtonEvent(){

		if(cmbIssueTo.getValue()!= null&&cmbIssueFrom.getValue()!=null&&cmbfinishedGoods.getValue()!=null)
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
			this.getParent().showNotification(
					"Update Failed",
					"There are no data for update.",
					Notification.TYPE_WARNING_MESSAGE);
	}

	private boolean nullCheck()
	{

		for(int i=0;i<cmbProduct.size();i++)
		{

			if(cmbProduct.get(i).getValue()!=null)
			{
				return true;	
			}
			else
			{
				this.getParent().showNotification("Warning :","Please Select Your Desire Product Name.", Notification.TYPE_WARNING_MESSAGE);
				return false;
			}

		}

		return false;

	}

	private String Option(){
		String strFlag="debit";
		if(strFlag.equalsIgnoreCase("debit"))
			strFlag= "RDP";
		else
			strFlag= "RCP";

		return strFlag;
	}
	public String productlededger(int i) 
	{
		String autoCode = "";

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query="select Ledger_Id  from tbLedger where Ledger_Id=(select vLedgerCode from tbRawItemInfo where vRawItemCode like '"+cmbProduct.get(i).getValue().toString()+"')";
			System.out.println("ledgerpr"+query);
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


	private void newButtonEvent() 
	{

		componentIni(false);
		btnIni(false);
		txtClear();
		//cmbissueTo.focus();
		txtjobno.setValue(autoIssueNo());

	}
	public void txtClear(){

		txtjobno.setValue("");
		challanNo.setValue("");
		txtChallanNo.setValue("");
		djobdate.setValue(new java.util.Date());
		cmbIssueFrom.setValue(null);
		cmbIssueTo.setValue(null);
		cmbjoborderNo.setValue(null);
		cmbfinishedGoods.setValue(null);
		txtDia.setValue("");
		txtshoulderweight.setValue("");

		tableClear();
		tbhdpeclear();
	}

	private void componentIni(boolean b) {

		txtjobno.setEnabled(!b);
		dateField.setEnabled(!b);
		djobdate.setEnabled(!b);
		txtChallanNo.setEnabled(!b);
		cmbIssueFrom.setEnabled(!b);
		cmbIssueTo.setEnabled(!b);
		cmbfinishedGoods.setEnabled(!b);
		lbLine.setEnabled(!b);
		table.setEnabled(!b);
		//		tbhdpe.setEnabled(!b);
		txtDia.setEnabled(!b);
		txtshoulderweight.setEnabled(!b);
		cmbjoborderNo.setEnabled(!b);

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
	public void tableInitialise()
	{

		for(int i=0;i<3;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tbhdpeInitialize()
	{

		for(int i=0;i<3;i++)
		{
			tbhdpeRowAdd(i);
		}
	}


	private void tbhdpeRowAdd(final int ar) 
	{
		lblslmb.add(ar,new Label());
		lblslmb.get(ar).setWidth("20px");
		lblslmb.get(ar).setValue(ar + 1);

		cmbProductmb.add(ar,new ComboBox());
		cmbProductmb.get(ar).setWidth("100%");
		cmbProductmb.get(ar).setImmediate(true);
		cmbProductmb.get(ar).setNullSelectionAllowed(true);

		cmbProductmb.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProductmb.get(ar).getValue()!=null)
				{
					if(!cmbProductmb.get(ar).getValue().toString().replaceAll("#", "").equalsIgnoreCase("x0") /*&& fla*/)
					{
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						Transaction tx = session.beginTransaction();
						try
						{
							/*String sql = "select issueDet.IssueQty,0 from tbTubeIssueInfo as issueInfo" +
									" left join" +
									" (select IssueNo, RMitemCode, IssueQty from tbTubeIssueDetails) as issueDet" +
									" on issueDet.IssueNo = issueInfo.IssueNo" +
									" where issueDet.RMitemCode = '"+cmbRawItem.getValue().toString()+"' and issueInfo.FinishedGood = '"+cmbFinishedProduct.get(ar).getValue().toString()+"'";*/

							String sql="select vUnitName, 0 from tbRawItemInfo where vRawItemCode like '"+cmbProductmb.get(ar).getValue()+"'";
							List list = session.createSQLQuery(sql).list();
							int cmbflag=0;
							System.out.print(sql);

							if(list.iterator().hasNext())
							{
								Object[] element = (Object[]) list.iterator().next();
								cmbflag=1;
								txtunitmb.get(ar).setValue(element[0]);
								//								txtIssueRemainsqm.get(ar).setValue(decFormat.format(element[1]));
							}

							String sql2="select * from funcHdpeAndMbStock('"+cmbProductmb.get(ar).getValue()+"','"+dateFormat.format(djobdate.getValue())+" "+ "23:59:59"+"','"+cmbIssueTo.getValue()+"','2')";
							List list2 = session.createSQLQuery(sql2).list();

							System.out.print(sql2);

							if(list2.iterator().hasNext())
							{
								Object[] element = (Object[]) list2.iterator().next();

								SectionstockQtymb.get(ar).setValue(element[4]);
								FloorstockQtymb.get(ar).setValue(element[5]);
								//								txtIssueRemainsqm.get(ar).setValue(decFormat.format(element[1]));
							}
							else
							{
								SectionstockQtymb.get(ar).setValue(0.00);
								FloorstockQtymb.get(ar).setValue(0.00);
							}

							String sql3="select * from dbo.funcWastageStock('"+cmbProductmb.get(ar).getValue()+"','"+dateFormat.format(djobdate.getValue())+" "+ "23:59:59"+"' ) ";
							List list3 = session.createSQLQuery(sql3).list();

							System.out.print(sql3);

							if(list3.iterator().hasNext())
							{
								Object[] element = (Object[]) list3.iterator().next();

								wastagestock.get(ar).setValue(element[7]);
								//								txtIssueRemainsqm.get(ar).setValue(decFormat.format(element[1]));
							}

							else
							{
								wastagestock.get(ar).setValue(0.00);
							}

						}

						catch(Exception ex)
						{
							System.out.println(ex);
						}

						if(cmbProductmb.size()-1==ar)
						{	
							tbhdpeRowAdd(ar+1);
							cmbMbData();
							//							chkFGListener(ar+1);
							//							cmbFinishedProductData(ar+1);
							//								cmbItemNameAdd(ar+1);
							//								cmbItemName.get(ar+1).focus();
						}
					}
					else 
					{
						if (cmbProductmb.get(ar).getValue()!=null) 
						{
							cmbProductmb.get(ar).setValue(null);
							getParent().showNotification("Warning :","Same Item Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					//					totalItemFG();
				}
				else
				{
					//					totalItemFG();
				}
			}
		});

		txtunitmb.add(ar,new TextRead(""));
		txtunitmb.get(ar).setWidth("100%");

		SectionstockQtymb.add(ar,new TextRead(1));
		SectionstockQtymb.get(ar).setWidth("100%");

		FloorstockQtymb.add(ar,new TextRead(1));
		FloorstockQtymb.get(ar).setWidth("100%");

		sectionIssuemb.add(ar,new AmountField());
		sectionIssuemb.get(ar).setWidth("100%");
		sectionIssuemb.get(ar).setImmediate(true);
		//		sectionIssuemb.get(ar).setTextChangeTimeout(1);
		//		sectionIssuemb.get(ar).setTextChangeEventMode(TextChangeEventMode.EAGER);

		sectionIssuemb.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(!sectionIssuemb.get(ar).getValue().toString().isEmpty())
				{
					double secIssue=Double.parseDouble(sectionIssuemb.get(ar).toString().isEmpty()?"0.0":sectionIssuemb.get(ar).toString());
					double secStock=Double.parseDouble(SectionstockQtymb.get(ar).toString().isEmpty()?"0.0":SectionstockQtymb.get(ar).toString());
					double wasIssue=Double.parseDouble(wastageIssuemb.get(ar).toString().isEmpty()?"0.0":wastageIssuemb.get(ar).toString());

					if(secIssue > secStock)
					{
						getParent().showNotification("Warning :","Stock Limit Exceeds!!!",Notification.TYPE_WARNING_MESSAGE);
						sectionIssuemb.get(ar).setValue("");
					}
					else
					{
						double total = 0.00;
						total = secIssue + wasIssue;
						totalIssurmb.get(ar).setValue("");
						totalIssurmb.get(ar).setValue(total);
					}
				}
			}
		});

		wastagestock.add(ar,new TextRead(1));
		wastagestock.get(ar).setWidth("100%");

		wastageIssuemb.add(ar,new AmountField());
		wastageIssuemb.get(ar).setWidth("100%");
		wastageIssuemb.get(ar).setImmediate(true);
		wastageIssuemb.get(ar).setTextChangeTimeout(1);
		wastageIssuemb.get(ar).setTextChangeEventMode(TextChangeEventMode.EAGER);

		wastageIssuemb.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(!wastageIssuemb.get(ar).getValue().toString().isEmpty())
				{
					double wasIssue=Double.parseDouble(wastageIssuemb.get(ar).toString().isEmpty()?"0.0":wastageIssuemb.get(ar).toString());
					double wasStock=Double.parseDouble(wastagestock.get(ar).toString().isEmpty()?"0.0":wastagestock.get(ar).toString());
					double secIssue=Double.parseDouble(sectionIssuemb.get(ar).toString().isEmpty()?"0.0":sectionIssuemb.get(ar).toString());

					if(wasIssue > wasStock)
					{
						getParent().showNotification("Warning :","Stock Limit Exceeds!!!",Notification.TYPE_WARNING_MESSAGE);
						wastageIssuemb.get(ar).setValue("");
					}
					else
					{
						double total = 0.00;
						total = secIssue + wasIssue;
						totalIssurmb.get(ar).setValue("");
						totalIssurmb.get(ar).setValue(total);
					}
				}
			}
		});

		totalIssurmb.add(ar,new  TextRead(1));
		totalIssurmb.get(ar).setWidth("100%");

		txtremarksmb.add(ar,new TextField());
		txtremarksmb.get(ar).setWidth("100%");

		tbhdpe.addItem(new Object[]{lblslmb.get(ar), cmbProductmb.get(ar),txtunitmb.get(ar),SectionstockQtymb.get(ar),FloorstockQtymb.get(ar),sectionIssuemb.get(ar),wastagestock.get(ar),wastageIssuemb.get(ar),totalIssurmb.get(ar),txtremarksmb.get(ar)},ar);





	}

	private boolean doubleEntryCheck(int row)
	{
		String caption = cmbProduct.get(row).getValue().toString();

		for(int i=0;i<cmbProduct.size();i++)
		{
			if(i!=row && caption.equals(cmbProduct.get(i).getValue().toString()))
			{

				return false;
			}
		}
		return true;
	}
	public List perTubeCalc(int ar,String rawId){
		Transaction tx = null;
		String sql = "";
		List std = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String productId = cmbProduct.get(ar).getValue().toString();

			//sql= "select 0, isnull(perSqmQty,0)as qty  from tbFinishedProductInfo where vProductId like '"+cmbfinishedGoods.getValue().toString().trim()+"' ";
			
			sql="select 0, isnull(perSqm,0)as qty  from tbFinishedProductDetails  where fgId like '"+cmbfinishedGoods.getValue().toString().trim()+"' and rawItemCode like '"+rawId+"'";
			System.out.println("sql is"+sql);

			std=session.createSQLQuery(sql).list();

		}
		catch(Exception exp){
			this.getParent().showNotification("From perTubeCalc: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		return std;
	}
	public void proComboChange(int ar){
		Transaction tx = null;
		String sql = "";
		String query="";
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String productId = cmbProduct.get(ar).getValue().toString();

			String sqlStock="select * from dbo.funcproductionStock('"+productId+"','"+dateFormat.format(djobdate.getValue())+" 23:59:59','2','PT-1','"+cmbIssueTo.getValue().toString().trim()+"','"+cmbIssueFrom.getValue().toString()+"','"+cmbfinishedGoods.getValue().toString()+"') ";
			query="select 0,vUnitName  from tbRawItemInfo where vRawItemCode like '"+cmbProduct.get(ar).getValue().toString()+"' ";
			System.out.println(sqlStock);
			List listStock = session.createSQLQuery(sqlStock).list();

			if(listStock.iterator().hasNext())
			{
				Object[] element = (Object[]) listStock.iterator().next();
				unit.get(ar).setValue(element[3]);
				UPSectionstockQty.get(ar).setValue(df.format(element[4]));
				UPFloorstockQty.get(ar).setValue(df.format(element[5]));
				stockQty.get(ar).setValue(df.format(element[6]));
			}
			else
			{
				List lst = session.createSQLQuery(query).list();
				if(lst.iterator().hasNext())
				{
					Object[] element = (Object[]) lst.iterator().next();
					unit.get(ar).setValue(element[1]);
					UPSectionstockQty.get(ar).setValue(0.00);
					UPFloorstockQty.get(ar).setValue(0.00);
					stockQty.get(ar).setValue(0.00);	

				}

			}


			List std=perTubeCalc( ar,productId);
			if(std.iterator().hasNext()){

				Object[] element = (Object[]) std.iterator().next();
				System.out.println("You are Ok"+element[1]);
				double target=Double.parseDouble(UPSectionstockQty.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
				System.out.println("target qty is"+target);
				targetqty.get(ar).setValue(df1.format(target));
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("From ProCombChange: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public boolean doubleCheck(int ar){
		String value=cmbProduct.get(ar).getValue().toString();
		for(int x=0;x<cmbProduct.size();x++){
			if(cmbProduct.get(x).getValue()!=null){
				if(x!=ar&&value.equalsIgnoreCase(cmbProduct.get(x).getValue().toString())){
					return false;
				}
			}
		}
		return true;
	}
	private void cmbProductData(){

		tableClear();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query=null;
		String sql="";
		try{


			query="select b.RawItemCode,c.vRawItemName," +
					"subString(c.vSubGroupName,CHARINDEX('-',c.vSubGroupName)+1,LEN(c.vSubGroupName)-CHARINDEX('-',c.vSubGroupName)) as vSubGroupName,c.vSubSubCategoryName" +
					" from tbFinishedGoodsStandardInfo a "+
					"inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo "+
					"inner join tbRawItemInfo c on b.RawItemCode=c.vRawItemCode where a.fGCode like '"+cmbfinishedGoods.getValue()+"' and "+ 
					"a.declarationDate=(select MAX(declarationDate) from tbFinishedGoodsStandardInfo where fGCode='"+cmbfinishedGoods.getValue()+"')";

			sql="select Dia,weight from  tbFinishedProductInfo where vProductId like '"+cmbfinishedGoods.getValue()+"'";

			List lst=session.createSQLQuery(query).list();
			int a=lst.size();
			System.out.println(a);
			Iterator iter=lst.iterator();
			while(a>0)
			{
				if(iter.hasNext())
				{
					Object[] element=(Object[]) iter.next();
					for(int ar=0;ar<cmbProduct.size();ar++){
						cmbProduct.get(ar).addItem(element[0].toString());
						//String name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
						cmbProduct.get(ar).setItemCaption(element[0], element[1].toString()+"#"+element[2].toString()+"#"+element[3].toString());
					}

				}
				a--; 
			}


			List lst1=session.createSQLQuery(sql).list();
			int b=lst1.size();
			Iterator iter1=lst1.iterator();
			txtDia.setValue("");
			txtshoulderweight.setValue("");
			if(iter1.hasNext())
			{
				Object[] element=(Object[]) iter1.next();
				txtDia.setValue( element[0] );
				txtshoulderweight.setValue(element[1]);
			}



		}
		catch(Exception exp){
			this.getParent().showNotification("cmbProduct: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbMbData(){

		tableClear();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query=null;

		try{


			query="select vRawItemCode,vRawItemName from tbRawItemInfo  where vGroupId  in('G174','G175','G176','G177','G178')";



			List lst=session.createSQLQuery(query).list();
			int a=lst.size();
			Iterator iter=lst.iterator();
			while(a>0)
			{
				if(iter.hasNext())
				{
					Object[] element=(Object[]) iter.next();
					for(int ar=0;ar<cmbProductmb.size();ar++){
						cmbProductmb.get(ar).addItem(element[0].toString());
						//String name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
						cmbProductmb.get(ar).setItemCaption(element[0].toString(), element[1].toString());
					}

				}
				a--; 
			}			
		}
		catch(Exception exp){
			this.getParent().showNotification("cmbProduct: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public boolean selectFrom(){

		try{
			Transaction tx=null;
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select AutoID from tbSectionInfo where AutoID like '"+cmbIssueFrom.getValue().toString()+"'";
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
	public void tableRowAdd(final int ar)
	{
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query=null;
		try
		{

			lblsl.add(ar,new Label());
			lblsl.get(ar).setWidth("20px");
			lblsl.get(ar).setValue(ar + 1);

			cmbProduct.add(ar,new ComboBox());
			cmbProduct.get(ar).setWidth("100%");
			cmbProduct.get(ar).setImmediate(true);
			cmbProduct.get(ar).setNullSelectionAllowed(true);
			cmbProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			cmbProduct.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
				{


					if(cmbProduct.get(ar).getValue()!=null)
					{
						if(doubleCheck(ar)){
							proComboChange(ar);
							Issueqty.get(ar).focus();
							if(ar==lblsl.size()-1)
							{
								tableRowAdd(ar+1);
								Issueqty.get(ar).focus();
							}
						}
						else{
							showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							cmbProduct.get(ar).setValue(null);
							cmbProduct.get(ar).focus();
							System.out.println("Not Ok");
						}

					}


				}
			});
			unit.add(ar,new TextRead(""));
			unit.get(ar).setWidth("100%");

			UPSectionstockQty.add(ar,new TextRead(1));
			UPSectionstockQty.get(ar).setWidth("100%");

			UPFloorstockQty.add(ar,new TextRead(1));
			UPFloorstockQty.get(ar).setWidth("100%");

			stockQty.add(ar,new AmountField());
			stockQty.get(ar).setWidth("100%");
			stockQty.get(ar).setImmediate(true);

			stockQty.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(cmbProduct.get(ar).getValue()!=null&&!stockQty.get(ar).getValue().toString().isEmpty())
					{
						List std=perTubeCalc( ar,cmbProduct.get(ar).getValue().toString());
						if(std.iterator().hasNext()){
							Object[] element = (Object[]) std.iterator().next();
							double sqm=Double.parseDouble(stockQty.get(ar).getValue().toString().isEmpty()?"0.0":stockQty.get(ar).getValue().toString());
							double perSqm=Double.parseDouble(element[1].toString());
							double pcs=sqm*perSqm;
							printedPcs.get(ar).setValue(df1.format(pcs));
						}
					}
					else{
						printedPcs.get(ar).setValue("");
					}
				}
			});

			printedPcs.add(ar,new TextRead(1));
			printedPcs.get(ar).setWidth("100%");

			targetqty.add(ar,new TextRead(1));
			targetqty.get(ar).setWidth("100%");

			Issueqty.add(ar,new AmountField());
			Issueqty.get(ar).setWidth("100%");
			Issueqty.get(ar).setImmediate(true);
			Issueqty.get(ar).setTextChangeTimeout(1);
			Issueqty.get(ar).setTextChangeEventMode(TextChangeEventMode.EAGER);

			IssueTarget.add(ar,new AmountCommaSeperator());
			IssueTarget.get(ar).setWidth("100%");
			IssueTarget.get(ar).setImmediate(true);

			IssueTarget.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(cmbProduct.get(ar).getValue()!=null&&!IssueTarget.get(ar).getValue().toString().isEmpty()){
						List std=perTubeCalc( ar,cmbProduct.get(ar).getValue().toString());
						if(std.iterator().hasNext()){
							Object[] element = (Object[]) std.iterator().next();
							System.out.println("IssueTarget Changed before");

							double qty=Double.parseDouble(IssueTarget.get(ar).getValue().toString().isEmpty()?"0.0":
								IssueTarget.get(ar).getValue().toString())/Double.parseDouble(element[1].toString());
							System.out.println("Quantity Is "+qty);

							Issueqty.get(ar).setValue(df2.format(qty));
							System.out.println("IssueTarget Changed after"+qty);
						}
					}
				}
			});
			Issueqty.get(ar).addListener(new ValueChangeListener() 
			{

				public void valueChange(ValueChangeEvent event) {
					if(cmbProduct.get(ar).getValue()!=null&&!Issueqty.get(ar).getValue().toString().isEmpty())
					{
						totalissue=0.00;
						for(int i=0;i<cmbProduct.size();i++)
						{
							if(!Issueqty.get(i).getValue().toString().isEmpty())
							{
								totalissue=totalissue+ Double.parseDouble(Issueqty.get(i).getValue().toString()) ;		
							}	
						}
						System.out.print("Total Issue is"+totalissue);

						if(selectFrom())
						{
							double unPrintedSecStock=Double.parseDouble(UPSectionstockQty.get(ar).getValue().toString().isEmpty()?"0.0":
								UPSectionstockQty.get(ar).getValue().toString());
							double issueQty=Double.parseDouble(Issueqty.get(ar).getValue().toString().isEmpty()?"0.0":
								Issueqty.get(ar).getValue().toString());
							if(!isFind)
							{
								if(unPrintedSecStock>=issueQty)	
								{

									List std=perTubeCalc( ar,cmbProduct.get(ar).getValue().toString());
									if(std.iterator().hasNext())
									{
										Object[] element = (Object[]) std.iterator().next();
										double target=Double.parseDouble(Issueqty.get(ar).getValue().toString().isEmpty()?"0.0":
											Issueqty.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
										IssueTarget.get(ar).setValue(df1.format(target));
									}
								}
								else
								{
									Issueqty.get(ar).setValue("");
									IssueTarget.get(ar).setValue("");
									Issueqty.get(ar).focus();
									showNotification("Issue Qty Exceed Section Stock Qty",Notification.TYPE_WARNING_MESSAGE);

								}	
							}
							else
							{
								Transaction tx=null;
								String sql=null; 
								Session session=SessionFactoryUtil.getInstance().getCurrentSession();
								tx=session.beginTransaction();

								List std=perTubeCalc( ar,cmbProduct.get(ar).getValue().toString());
								if(std.iterator().hasNext())
								{
									Object[] element = (Object[]) std.iterator().next();
									double target=Double.parseDouble(Issueqty.get(ar).getValue().toString().isEmpty()?"0.0":
										Issueqty.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
									IssueTarget.get(ar).setValue(df1.format(target));
								}

								if(ar==0)
								{

									sql=    " select 0, ISNULL(SUM(b.ShiftASqm+b.ShiftBSqm+WastageSqm ),0)   from tbTubeProductionInfo a "
											+"inner join "
											+"tbTubeProductionDetails b "
											+"on "
											+"a.ProductionNo=b.ProductionNo "
											+"where a.IssueNo like '"+txtjobno.getValue()+"' ";
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
							double printedStock=Double.parseDouble(stockQty.get(ar).getValue().toString().isEmpty()?"0.0":
								stockQty.get(ar).getValue().toString());
							double issueQty=Double.parseDouble(Issueqty.get(ar).getValue().toString().isEmpty()?"0.0":
								Issueqty.get(ar).getValue().toString());

							System.out.println("Printed Stock: "+printedStock);
							System.out.println("IssueQty: "+issueQty);


							totalissue=0.00;
							for(int i=0;i<cmbProduct.size();i++)
							{
								totalissue=totalissue+ Double.parseDouble(Issueqty.get(ar).getValue().toString()) ;	
							}

							if(!isFind)
							{
								for(int i=0;i<cmbProduct.size();i++)
								{
									totalissue=totalissue+ Double.parseDouble(Issueqty.get(ar).getValue().toString()) ;	
								}
								if(printedStock<issueQty)
								{
									Issueqty.get(ar).setValue("");
									IssueTarget.get(ar).setValue("");
									IssueTarget.get(ar).focus();
									showNotification("Issue Target Exceed Printed Stock Target",Notification.TYPE_WARNING_MESSAGE);
								}	
								List std=perTubeCalc( ar,cmbProduct.get(ar).getValue().toString());
								if(std.iterator().hasNext())
								{
									Object[] element = (Object[]) std.iterator().next();
									double target=Double.parseDouble(Issueqty.get(ar).getValue().toString().isEmpty()?"0.0":
										Issueqty.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
									IssueTarget.get(ar).setValue(df1.format(target));
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
											+ "where a.IssueNo like '"+txtjobno.getValue()+"' ";
									Double ttlissue=0.00;
									Double totalissue=0.00;

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

								List std=perTubeCalc( ar,cmbProduct.get(ar).getValue().toString());
								if(std.iterator().hasNext())
								{
									Object[] element = (Object[]) std.iterator().next();
									double target=Double.parseDouble(Issueqty.get(ar).getValue().toString().isEmpty()?"0.0":
										Issueqty.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));
									IssueTarget.get(ar).setValue(df1.format(target));
								}
							}

						}
					}

				}
			});


			txtremarks.add(ar,new TextField());
			txtremarks.get(ar).setWidth("100%");

			table.addItem(new Object[]{lblsl.get(ar), cmbProduct.get(ar),unit.get(ar),UPSectionstockQty.get(ar),UPFloorstockQty.get(ar),stockQty.get(ar),printedPcs.get(ar),targetqty.get(ar),Issueqty.get(ar),IssueTarget.get(ar),txtremarks.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

}
