package com.example.productionSetup;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

//import org.apache.jasper.compiler.Node.DoBodyAction;
import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.setupTransaction.AreaFindWindow;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;

public class CostSheet extends Window 

{

	private static final String String = null;
	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");
	private AbsoluteLayout mainLayout=new AbsoluteLayout();

	private Label lblRMCost,lblRemarks,lblJobNo,lblWastage,lblAdd,lblAdd1,lblPanel,lblPercent2,lblPercent1;
	private Label lblRMList,lblWidthActual,lblWidthConsumption,lblHeightActual,lblHeightConsumption,lblProductionType;
	ComboBox cmbProduction;

	private Label lblTransport;
	private AmountCommaSeperator AmtTransport;

	private Label lblPacking;
	private AmountCommaSeperator AmtPacking;

	private Label lblTotalCost;
	private AmountCommaSeperator AmtTotalCost;

	private Label lblMarkUp;
	private AmountCommaSeperator AmtMarkUp;

	private Label lblEndPrice;
	private AmountCommaSeperator AmtEndPrice;

	private Label lblCompany,lblFinishedGoods;
	private ComboBox cmbCompany,cmbFinishedGoods;

	private Label lblDia,lblWeightFg,lblDate;
	private TextRead txtDia,txtWeight,txtWidthActual,txtWidthConsumption,txtLengthActual,txtLengthConsumption,txtStdNo;

	private PopupDateField dCurrent;
	private TextArea txtRemarks=new TextArea();
	private Label lblMarkup;
	private TextField txtMarkup;
	private Panel panelSearch;


	private Table table1= new Table();
	private Table table2= new Table();
	private Table table3= new Table();
	
	private Label lblAitImort=new Label();
	private Label lblAitImportPercent=new Label();
	private Label lblAitSales=new Label();
	private Label lblAitSalesPercent=new Label();
	private TextField txtAitImportPercentage=new TextField();
	private  TextField txtAitImport=new TextField();
	private TextField txtAitSalesPercentage=new TextField();
	private TextField txtAitSales=new TextField();
	private Label lblnetMarkup =new Label();
	private TextField txtnetMarkup=new TextField();
	
	private SessionBean sessionBean;

	boolean isFind=false;
	boolean isUpdate=false;
	private TextField AutoId=new TextField();

	private DecimalFormat decf= new DecimalFormat("#0.00");
	private DecimalFormat decf1= new DecimalFormat("#0.000000");
	private DecimalFormat decf2= new DecimalFormat("#0.0000");
	private DecimalFormat dFormat= new DecimalFormat("#0");

	private ArrayList<Label> lblsa1 = new ArrayList<Label>();
	private ArrayList<Label>  lblRawName= new ArrayList<Label>();
	private ArrayList<Label>  lblType = new ArrayList<Label>();
	private ArrayList<AmountField>  lblWeight= new ArrayList<AmountField>();
	private ArrayList<AmountField>  lblRate= new ArrayList<AmountField>();
	private ArrayList<Label>  lblId= new ArrayList<Label>();
	private ArrayList<Label>  lblAmount= new ArrayList<Label>();
	private ArrayList<Label>  lblPercent = new ArrayList<Label>();
	private ArrayList<TextField>  txtRawRemarks = new ArrayList<TextField>();
	private ArrayList<CheckBox>  chk= new ArrayList<CheckBox>();
	private ArrayList<AmountField> txtwastagepercent=new ArrayList<AmountField>();
	private ArrayList<TextRead>lblTotal=new ArrayList<TextRead>(1);
	private ArrayList<TextRead>lblUnit=new ArrayList<TextRead>();
	private ArrayList<Label> lblsa3 = new ArrayList<Label>();
	private ArrayList<ComboBox>  tblCmbConCost= new ArrayList<ComboBox>();
	//private ArrayList<AmountField>  tbTxtConAmountPerSqm = new ArrayList<AmountField>();
	private ArrayList<AmountField>  tbTxtConAmount = new ArrayList<AmountField>();
	private ArrayList<TextField>  tbtxtConRemarks = new ArrayList<TextField>();
	private ArrayList<ComboBox> tbcmbItemType =new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbcmbheadtype =new ArrayList<ComboBox>();
	private String value[]={"Raw Material","INK","HDPE","MB"};
	public HashMap hm=new HashMap();
	

	ArrayList<Component> allComp = new ArrayList<Component>();	

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormatnew=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat dF=new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dFt=new SimpleDateFormat("yyyy");

	private Label lblExistPartyName;
	//private AmountField txtWastage;

	String DivIncharge="";
	String areaIncharge="";

	ComboBox cmbFindParty=new ComboBox("Party Name: ");
	ComboBox cmbFindFG=new ComboBox("Finished Goods: ");
	ComboBox cmbFindDate=new ComboBox("Date: ");

	public CostSheet(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("Cost Sheet:: "+sessionBean.getCompany());
		//this.setWidth("580px");
		this.setResizable(false);
		setContent(buildMainLayout());
		tableinitialise();
		componentIni(true);
		btnInit(true);
		cmbProductionTypeData();
		btnAction();
		cmbCompanyData();
		focusEnter();
		authenticationCheck();
		button.btnNew.focus();		
	}
	private void cmbProductionTypeData() {
		cmbProduction.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			
			List<?> list=session.createSQLQuery("select productTypeId,productTypeName from tbProductionType order by autoId").list();

			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProduction.addItem(element[0].toString());
				cmbProduction.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}


	private void cmbCompanyData(){

		cmbCompany.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			
			String sql="select vGroupId,partyName from tbPartyInfo where vGroupId in (select partyCode from tbFinishedGoodsStandardInfo)";
			List<?> list=session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbCompany.addItem(element[0]);
				cmbCompany.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
		
		finally{session.close();}
	}
	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			button.btnDelete.setVisible(false);
		}
	}


	private void cmbProductionTypeAdd() {
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			String sql="select 0,isnull(vProductionTypeId,'')  from tbFinishedProductInfo where vProductId='"+cmbFinishedGoods.getValue()+"'";
			List<?> list=session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbProduction.setValue(element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("cmbProductionTypeAdd: "+exp);
		}
		finally{session.close();}
	}

	public void btnAction()
	{
		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				
				newButtonEvent();
				panelSearch.setEnabled(false);
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event)
			{
				componentIni(true);
				btnInit(true);
				txtClear();
				//tableClear();
			}
		});

		button.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
		
			public void buttonClick(ClickEvent event)
			{
				isFind = true;
				if(sessionBean.isUpdateable()){
					isUpdate = true;
					btnInit(false);
					componentIni(false);//Enable(true);
					panelSearch.setEnabled(false);
				}
				else{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				componentIni(true);
				btnInit(true);
				txtClear();
				panelSearch.setEnabled(true);
				findPartyLoad();
				
			}
		});

		button.btnSave.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				
				saveButtonEvent();
			}
		});
		cmbCompany.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbCompany.getValue()!=null)
				{
					cmbFinishedGoodsData();
					cmbFinishedGoods.focus();
				}
			}
		});
		cmbFinishedGoods.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbFinishedGoods.getValue()!=null && !isFind)
				{

					tableClear1();
					System.out.println("hello");
					jobNoLoad();
					finishedDetails();
					cmbProductionTypeAdd();
				}

				/*else
				{
					txtClear();	
				}*/
			}
		});
	
		AmtPacking.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				//totalCalc();
				if(!AmtPacking.getValue().toString().isEmpty()&&!AmtPacking.getValue().toString().equalsIgnoreCase("0.00")){
					totalCalc();
				}
			}
		});
		AmtTransport.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(!AmtTransport.getValue().toString().isEmpty())
				{
					totalCalc();
				}
			}
		});
		txtMarkup.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!txtMarkup.getValue().toString().isEmpty()){
					totalCalc();
					double d=Double.parseDouble(AmtTotalCost.getValue().toString());
					double perc=d*Double.parseDouble(txtMarkup.getValue().toString())/100;
					AmtMarkUp.setValue(decf2.format(perc));
				}
			}
		});
		AmtMarkUp.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(!AmtMarkUp.getValue().toString().isEmpty()){
					totalCalc();
					double d=Double.parseDouble(AmtTotalCost.getValue().toString());
					double d1=Double.parseDouble(AmtMarkUp.getValue().toString());
					double d2=d+d1;
					AmtEndPrice.setValue(decf.format(d2));
				}
			}
		});
		cmbFindParty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbFindParty.getValue()!=null){
					cmbFindFGDataLoad();
				}
			}
		});
		cmbFindFG.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindFG.getValue()!=null){
					cmbFindDateLoadData();
				}
			}
		});
		cmbFindDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event)
			{

				if(cmbFindDate.getValue()!=null)
				{
					System.out.println("Done");
					findDataLoad();
					findNextpart();
				}
			}

		});
		
		
		txtAitImportPercentage.addListener(new ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtAitImportPercentage.getValue().toString().isEmpty())
				{
					StringTokenizer token=new StringTokenizer(table1.getColumnFooter("Total"),":");
					token.nextToken();
					String s=token.nextToken();
					double RmCost=Double.parseDouble(s.equalsIgnoreCase(" ")?"0.00":s);
					double ImportPerCentage=Double.parseDouble(txtAitImportPercentage.getValue().toString());
					double AitImport=(RmCost*ImportPerCentage)/100;
					txtAitImport.setValue(decf2.format(AitImport));	
				}
			
			
			}
		});
		
	txtAitSalesPercentage.addListener(new ValueChangeListener() 
	{
		public void valueChange(ValueChangeEvent event) 
			{
			if(!txtAitSalesPercentage.getValue().toString().isEmpty())
			{
				double endprice=Double.parseDouble(AmtEndPrice.getValue().toString().isEmpty()?"0.00":AmtEndPrice.getValue().toString()); 
				double SalestPerCentage=Double.parseDouble(txtAitSalesPercentage.getValue().toString().isEmpty()?"0.00":txtAitSalesPercentage.getValue().toString());
				double AitSales=(endprice*SalestPerCentage)/100;
				txtAitSales.setValue(decf2.format(AitSales) );
				double  netmarkup=0.00;
				double markup=Double.parseDouble(AmtMarkUp.getValue().toString().isEmpty()?"0.00":AmtMarkUp.getValue().toString());
				double aitImport=Double.parseDouble(txtAitImport.getValue().toString().isEmpty()?"0.00":txtAitImport.getValue().toString());
				double aitsales=Double.parseDouble(txtAitSales.getValue().toString().isEmpty()?"0.00":txtAitSales.getValue().toString());
				double netmarkup1=markup-(aitImport+aitsales);
				txtnetMarkup.setValue(decf2.format(netmarkup1));
				
			}
				
			}
		});	
	}
	
	private void findDataLoad() {
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			String  sql="";
	
/*			String  query= "select a.partyCode,a.fGCode,a.jobNo,a.declareDate,b.rawItemCode, "
					       +"isnull((select case when b.rawItemCode like 'INk' then 'Ink' else vRawItemName end from tbRawItemInfo where vRawItemCode like b.rawItemCode  ),'INk')  as itemName, " 
					       +" itemType,qty,rate,amount,flag,b.remarks,b.wastagePercent,b.Head  from tbCostSheetInfo  "
					       +"a inner join tbCostSheetRmDetails b on a.jobNo=b.jobNo where a.partyCode  "
					       +"like '"+cmbFindParty.getValue()+"' and convert(date,a.declareDate,105) like '"+cmbFindDate.getValue()+"' and a.fGCode like '"+cmbFindFG.getValue()+"' ";*/
			
			String  query= "select a.partyCode,a.fGCode,a.jobNo,a.declareDate,b.rawItemCode, "
				       +"isnull((select case when b.rawItemCode like 'INk' then 'Ink' else vRawItemName end from tbRawItemInfo where vRawItemCode like b.rawItemCode  ),'INk')  as itemName, " 
				       +" itemType,qty,rate,amount,flag,b.remarks,b.wastagePercent,b.Head  from tbCostSheetInfo  "
				       +"a inner join tbCostSheetRmDetails b on a.jobNo=b.jobNo where a.partyCode  "
				       +"like '"+cmbFindParty.getValue()+"' and  a.declareDate like (select MAX(declareDate) from tbCostSheetInfo where convert(date,declareDate,105) like '"+cmbFindDate.getValue()+"' and fGCode like '"+cmbFindFG.getValue()+"') ";
			

			System.out.println(query);
			List<?> list=session.createSQLQuery(query).list();
			int a=0;
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();

				if(a==0)
				{
					System.out.println("Shoaib bin Adhem");
					
					cmbCompany.setValue(element[0]);
					cmbFinishedGoods.addItem(element[1]);
					cmbFinishedGoods.setValue(element[1]);
					dCurrent.setValue(element[3]);
					txtStdNo.setValue(element[2]);	
				}

				lblId.get(a).setValue(element[4]);
				lblRawName.get(a).setValue(element[5]);
				lblType.get(a).setValue(element[6].toString());
				lblWeight.get(a).setValue(decf1.format(element[7]));
				lblRate.get(a).setValue(decf1.format(element[8]) );
				lblAmount.get(a).setValue(decf1.format(element[9]));
				txtRawRemarks.get(a).setValue(element[11]);
				tbcmbItemType.get(a).setValue(element[13]);

				if(element[10].equals(1))
				{
					chk.get(a).setValue(true);
				}

				else
				{
					chk.get(a).setValue(false);	
				}
				
				txtwastagepercent.get(a).setValue(element[12]);

				a++;
			}

		}
		catch(Exception exp){
			showNotification("Rm Details: "+exp);
		}
	}
	
	
	private void findNextpart()
	{
		String sju="";
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		
	
/*		sju="select b.overheadId,b.amount,b.remarks ,a.transportCost ,a.packingCost,a.markupPercent,aitImportPercent,aitImportAmount,aitSalesPercent, "
			+" aitSalesAmount,netMarkup from tbCostSheetInfo a inner join tbCostSheetConvertionDetails  b on a.jobNo=b.jobNo where  "
			+"  a.partyCode like '"+cmbFindParty.getValue()+"' and a.fGCode like '"+cmbFindFG.getValue()+"' "
			+" and convert(date,a.declareDate) like '"+cmbFindDate.getValue()+"'   ";*/
		

		sju="select b.overheadId,b.amount,b.remarks ,a.transportCost ,a.packingCost,a.markupPercent,aitImportPercent,aitImportAmount,aitSalesPercent, "
			+" aitSalesAmount,netMarkup from tbCostSheetInfo a inner join tbCostSheetConvertionDetails  b on a.jobNo=b.jobNo where  "
			+"  a.partyCode like '"+cmbFindParty.getValue()+"' and a.fGCode like '"+cmbFindFG.getValue()+"' "
			+"  and  a.declareDate like (select MAX(declareDate) from tbCostSheetInfo where convert(date,declareDate,105) like '"+cmbFindDate.getValue()+"' and fGCode like '"+cmbFindFG.getValue()+"')   ";
		
		System.out.println("sql is"+sju);
		
	
		 cinvertionclerar();
		List<?> lst =session.createSQLQuery(sju).list();
		if(!lst.isEmpty())
		{
			int size=lst.size();
			int b=0;
			for(Iterator<?> iter1=lst.iterator();iter1.hasNext();)
			{
				Object[] element=(Object[]) iter1.next();

				tblCmbConCost.get(b).setValue(element[0]);
				tbTxtConAmount.get(b).setValue(decf1.format(element[1]));
				tbtxtConRemarks.get(b).setValue(element[2]);

				if(b==size-1)
				{
					AmtTransport.setValue(decf1.format(element[3]));
					AmtPacking.setValue(decf1.format(element[4]));
					txtMarkup.setValue(element[5]);
					txtAitImportPercentage.setValue(element[6]);
					txtAitSalesPercentage.setValue(element[8]);
					
				}

				b++;
			}
		}
	}
	
	
	private void cinvertionclerar()
	{
		for(int i=0;i<tblCmbConCost.size();i++)
		{
			tblCmbConCost.get(i).setValue(null);
			tbTxtConAmount.get(i).setValue("");
			tbtxtConRemarks.get(i).setValue("");
				
		}
		
	}
	
	
	
	
	
	private void cmbFindFGDataLoad(){

		cmbFindFG.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			String sql="select b.vProductId,b.vProductName from tbCostSheetInfo a inner join tbFinishedProductInfo b on a.fGCode=b.vProductId where a.partyCode like '"+cmbFindParty.getValue()+"'";
			List<?> list=session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindFG.addItem(element[0]);
				cmbFindFG.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
		finally{session.close();}
	}
	
	private void cmbFindDateLoadData(){
		cmbFindDate.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			String sql="select 0,CONVERT(date,declareDate,105)as date from tbCostSheetInfo where partyCode like '"+cmbFindParty.getValue()+"' and fGCode like '"+cmbFindFG.getValue()+"'";
			List<?> list=session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindDate.addItem(element[1]);
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
		finally{session.close();}
	}
	private void finishedDetails() {
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			String sql="select isnull(Dia,'')as dia,isnull(weight,0)as weight,"
					+" isnull(width,0)as width,isnull(widthConsumption,0)as widthC,"
					+" isnull(Length,0)as Length,isnull(LengthConsumption,0)as LengthC from tbFinishedProductInfo where vProductId='"+cmbFinishedGoods.getValue()+"'";
			List<?> list=session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				if(!element[0].toString().equalsIgnoreCase("0.00")){
					txtDia.setValue(decf.format(element[0]));
				}
				if(!element[1].toString().equalsIgnoreCase("0.00")){
					txtWeight.setValue(decf.format(element[1]));
				}
				if(!element[2].toString().equalsIgnoreCase("0.00")){
					txtWidthActual.setValue(decf.format(element[2]));
				}
				if(!element[3].toString().equalsIgnoreCase("0.00")){
					txtWidthConsumption.setValue(decf.format(element[3]));
				}
				if(!element[4].toString().equalsIgnoreCase("0.00")){
					txtLengthActual.setValue(decf.format(element[4]));
				}
				if(!element[5].toString().equalsIgnoreCase("0.00")){
					txtLengthConsumption.setValue(decf.format(element[5]));
				}
			}

			Session session1=SessionFactoryUtil.getInstance().getCurrentSession();
			session1.beginTransaction();
			/*String sql1="select c.vRawItemName,c.vUnitName,c.vCategoryType,b.Qty as itemWeight,b.Amount,c.vRawItemCode,b.rate from tbFinishedGoodsStandardInfo a"
					+" inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo"
					+" inner join tbRawItemInfo c on b.RawItemCode=c.vRawItemCode where a.fGCode like '"+cmbFinishedGoods.getValue()+"' and b.Qty>0 order by b.RawItemCode";*/

		/*	String sql1="select c.vRawItemName,c.vUnitName,c.vCategoryType,b.Qty as itemWeight,b.rate,b.amount,c.vRawItemCode, "+
					"cast(SUBSTRING(vRawItemCode,CHARINDEX('-',vRawItemCode)+1,LEN(vRawItemCode)-CHARINDEX('-',vRawItemCode))as int)as ord  "+
					"from tbFinishedGoodsStandardInfo a inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo  "+
					"inner join tbRawItemInfo c on b.RawItemCode=c.vRawItemCode where a.fGCode like '"+cmbFinishedGoods.getValue()+"' and a.declarationDate= "+
					"(select MAX(declarationDate) from tbFinishedGoodsStandardInfo where fGCode like '"+cmbFinishedGoods.getValue()+"') order by ord";
			*/
			String sql1= "select * from "
					        +" ( "
						    +"  select  c.vRawItemName,c.vUnitName,c.vCategoryType,b.Qty as itemWeight,b.rate,b.amount,c.vRawItemCode, " 
						    +" cast(SUBSTRING(vRawItemCode,CHARINDEX('-',vRawItemCode)+1,LEN(vRawItemCode)-CHARINDEX('-',vRawItemCode))as int)as ord,'1' as type  "
						    +" from tbFinishedGoodsStandardInfo a inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo " 
						    +"inner join tbRawItemInfo c on b.RawItemCode=c.vRawItemCode where a.fGCode like '"+cmbFinishedGoods.getValue()+"'  "
						    +"and a.declarationDate= (select MAX(declarationDate) from tbFinishedGoodsStandardInfo where fGCode like '"+cmbFinishedGoods.getValue()+"') " 
						    +" union "
						    + " select 'INK' as vRawItemName,'Kg' as vUnitName,'INK' as vCategoryType, CAST('' as money)as itemWeight,CAST('' as money)rate, CAST('' as money)amount,'INk' vRawItemCode "
						    +",'1000' as ord,'2' as type "
						    +" )  as temporary  order by type,ord ";
			
			

			List<?> list1=session1.createSQLQuery(sql1).list();
			int a=0;
			double packingSum=0;;
			for(Iterator <?>itr=list1.iterator();itr.hasNext();){

				Object element1[]=(Object[])itr.next();
				if(!element1[2].toString().equalsIgnoreCase("Packing Material"))
				{
					lblRawName.get(a).setValue(element1[0]);
					lblType.get(a).setValue(element1[2]);
					lblWeight.get(a).setValue(decf1.format(element1[3]));
					lblAmount.get(a).setValue(decf1.format(element1[5]));
					lblRate.get(a).setValue(decf.format(element1[4]));
					//lblTotal.get(a).setValue(decf1.format(Double.parseDouble(element1[4].toString()) * Double.parseDouble(element1[5].toString())));
					lblId.get(a).setValue(element1[6]);
					lblUnit.get(a).setValue(element1[1].toString());

					a++;
				}
				else{
					packingSum=packingSum+Double.parseDouble(element1[4].toString());
				}


			}
			AmtPacking.setValue(decf.format(packingSum));
			double sum=0;

			/*	for(int b=0;b<lblRawName.size();b++)
			{
				sum=sum+Double.parseDouble(lblAmount.get(b).getValue().toString().isEmpty()?"0.0":lblAmount.get(b).toString());
				table1.setColumnFooter("Amount", "Total : "+sum);
			}
			System.out.println(sum);*/

		}
		catch(Exception exp){
			showNotification("Company Data: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void cmbFinishedGoodsData(){
		cmbFinishedGoods.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			String sql="select vProductId,vProductName from tbFinishedProductInfo where vCategoryId like '"+cmbCompany.getValue()+"' and vProductId in(select fGCode from tbFinishedGoodsStandardInfo) and isActive ='1'";
			List<?> list=session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFinishedGoods.addItem(element[0]);
				cmbFinishedGoods.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
		finally{session.close();}
	}
	private void tableinitialise()
	{
		for(int i=0; i<=9; i++)
		{
				tableRowAdd1(i);
				tableRowAdd3(i);
		}
	}

	private void tableRowAdd1(final int ar)
	{
		lblsa1.add(ar,new Label());
		lblsa1.get(ar).setWidth("100%");
		lblsa1.get(ar).setValue(ar+1);

		chk.add(ar,new  CheckBox());
		chk.get(ar).setWidth("100%");
		chk.get(ar).setValue(false);
		chk.get(ar).setImmediate(true);
		
		
		
		tbcmbItemType.add(ar,new ComboBox());
		tbcmbItemType.get(ar).setValue(true);
		tbcmbItemType.get(ar).setWidth("100%");
		tbcmbItemType.get(ar).setValue(ar+1);
		
		/*for(int i=0;i<value.length;i++)
		{
			tbcmbItemType.get(ar).addItem(value[i]);	
		}*/
		
		
		for(int i=0;i<value.length;i++)
		{
			tbcmbItemType.get(ar).addItem(value[i]);	
			if(i==2){
				tbcmbItemType.get(ar).setItemCaption(value[i], "Base RM");
			}
		}
		
		chk.get(ar).addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event)
			{

		/*		double sum =0.00;
				for(int i=0;i<chk.size();i++)
				{

					if(chk.get(i).booleanValue())
					{
						sum= sum +Double.parseDouble(lblAmount.get(i).getValue().toString()) ;
					}
				}

				table1.setColumnFooter("Amount", "Total : "+sum);*/
				
			/*	double total=0.00;
				
				if(isFind)
				{
					double convertionCost=0.00;
					double TranportationCost=0.00;
					double packingcost=0.00;
					double TotalCost=0.00;
					for(int a=0;a<tblCmbConCost.size();a++)
					{
						if(!tbTxtConAmount.get(ar).getValue().toString().isEmpty()){
							convertionCost=convertionCost+Double.parseDouble(tbTxtConAmount.get(a).getValue().toString().isEmpty()?"0.0":tbTxtConAmount.get(a).getValue().toString());
							//table3.setColumnFooter("Amount", "Total: "+decf.format(convertionCost));
						}
					}
					
					if(!AmtTransport.getValue().toString().isEmpty())
					{
						TranportationCost=Double.parseDouble(AmtTransport.getValue().toString()); 
					}
					
					if(!AmtPacking.getValue().toString().isEmpty())
					{
						packingcost=Double.parseDouble(AmtPacking.getValue().toString()); 
					}
					
					TotalCost=sum+convertionCost+TranportationCost+packingcost;
					
					AmtTotalCost.setValue(TotalCost);
					
					double markup=0.00;
					
					if (!txtMarkup.getValue().toString().isEmpty())
					{
						markup=(Double.parseDouble(txtMarkup.getValue().toString())* TotalCost)/100 ;
						AmtMarkUp.setValue(markup);
					}
					
					double EndPrice=0.00;
					 EndPrice=markup+TotalCost;
					
					AmtEndPrice.setValue(decf.format(EndPrice));*/
					
					
				//}
				

			}
		});



		lblRawName.add(ar, new Label());
		lblRawName.get(ar).setWidth("100%");
		lblRawName.get(ar).setImmediate(true);

		lblRawName.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(!lblRawName.get(ar).getValue().toString().isEmpty())

				{
					if(ar==lblRawName.size()-1)
					{
						tableRowAdd1(ar+1);	
					}

				}

			}
		});

		lblType.add(ar, new Label());
		lblType.get(ar).setWidth("100%");
		lblType.get(ar).setImmediate(true);

		lblWeight.add(ar,new AmountField());
		lblWeight.get(ar).setWidth("100%");
		lblWeight.get(ar).setImmediate(true);

		lblRate.add(ar,new AmountField());
		lblRate.get(ar).setWidth("100%");
		lblRate.get(ar).setImmediate(true);
		
		
		lblRate.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(!lblRate.get(ar).getValue().toString().isEmpty())
				{
					Double rate=0.00;
					double qty=0.00;
					double amount;
					if(!lblWeight.get(ar).getValue().toString().isEmpty())
					{
						qty=Double.parseDouble(lblWeight.get(ar).getValue().toString());
					}
					
					if(!lblRate.get(ar).getValue().toString().isEmpty())
					{
						rate=Double.parseDouble(lblRate.get(ar).getValue().toString()); 
					}
					amount=rate*qty;
					
					lblAmount.get(ar).setValue(decf1.format(amount));
					
					
				}
				
			}
		});
		
		lblWeight.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(!lblWeight.get(ar).getValue().toString().isEmpty())
				{
					Double rate=0.00;
					double qty=0.00;
					double amount;
					if(!lblWeight.get(ar).getValue().toString().isEmpty())
					{
						qty=Double.parseDouble(lblWeight.get(ar).getValue().toString());
					}
					
					if(!lblRate.get(ar).getValue().toString().isEmpty())
					{
						rate=Double.parseDouble(lblRate.get(ar).getValue().toString()); 
					}
					amount=rate*qty;
					
					lblAmount.get(ar).setValue(decf1.format(amount));
					
					
				}
				
			}
		});
		
		
		lblAmount.add(ar,new Label());
		lblAmount.get(ar).setWidth("100%");
		lblAmount.get(ar).setImmediate(true);
		
		
		txtwastagepercent.add(ar,new AmountField());
		txtwastagepercent.get(ar).setWidth("100%");
		txtwastagepercent.get(ar).setImmediate(true);
		
		txtwastagepercent.get(ar).addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtwastagepercent.get(ar).getValue().toString().isEmpty() && chk.get(ar).booleanValue())
				{ 
					double percent=Double.parseDouble(txtwastagepercent.get(ar).getValue().toString());
					double amount=Double.parseDouble(lblAmount.get(ar).getValue().toString());
					double percentAmount=(percent*amount)/100;
					double total=percentAmount+amount;
					lblTotal.get(ar).setValue(decf1.format(total) );	
				}
			
			}
		});
		

		lblTotal.add(ar,new TextRead(1));
		lblTotal.get(ar).setWidth("100%");
		lblTotal.get(ar).setImmediate(true);
		
		 lblTotal.get(ar).addListener(new ValueChangeListener() 
		  {
					public void valueChange(ValueChangeEvent event)
					{
						if(!lblTotal.get(ar).getValue().toString().isEmpty())
						{
							double sum=0.00;
							for(int i=0;i<lblTotal.size();i++)
							{
								if(!lblTotal.get(i).getValue().toString().isEmpty())
								{
									sum=sum+ Double.parseDouble(lblTotal.get(i).getValue().toString());	
								} 	
							}	
							
							System.out.println("sum is"+sum);
							table1.setColumnFooter("Total", "Total : "+sum);
							
							double convertionCost=0.00;
							double TranportationCost=0.00;
							double packingcost=0.00;
							double TotalCost=0.00;
							for(int a=0;a<tblCmbConCost.size();a++)
							{
								if(!tbTxtConAmount.get(ar).getValue().toString().isEmpty()){
									convertionCost=convertionCost+Double.parseDouble(tbTxtConAmount.get(a).getValue().toString().isEmpty()?"0.0":tbTxtConAmount.get(a).getValue().toString());
									//table3.setColumnFooter("Amount", "Total: "+decf.format(convertionCost));
								}
							}
							
							if(!AmtTransport.getValue().toString().isEmpty())
							{
								TranportationCost=Double.parseDouble(AmtTransport.getValue().toString()); 
							}
							
							if(!AmtPacking.getValue().toString().isEmpty())
							{
								packingcost=Double.parseDouble(AmtPacking.getValue().toString()); 
							}
							
							TotalCost=sum+convertionCost+TranportationCost+packingcost;
							
							AmtTotalCost.setValue(TotalCost);
							
							double markup=0.00;
							
							if (!txtMarkup.getValue().toString().isEmpty())
							{
								markup=(Double.parseDouble(txtMarkup.getValue().toString())* TotalCost)/100 ;
								AmtMarkUp.setValue(markup);
							}
							
							double EndPrice=0.00;
							 EndPrice=markup+TotalCost;
							
							AmtEndPrice.setValue(decf.format(EndPrice));
						}
					}
				});
		
		
		lblId.add(ar,new Label());
		lblId.get(ar).setWidth("100%");
		lblId.get(ar).setImmediate(true);


		txtRawRemarks.add(ar,new TextField());
		txtRawRemarks.get(ar).setWidth("100%");
		txtRawRemarks.get(ar).setImmediate(true);
		
		lblUnit.add(ar,new TextRead());
		lblUnit.get(ar).setWidth("100%");
		lblUnit.get(ar).setImmediate(true);
		
		


		table1.addItem(new Object[]{lblsa1.get(ar),chk.get(ar),tbcmbItemType.get(ar) ,lblRawName.get(ar),lblType.get(ar),lblUnit.get(ar),lblWeight.get(ar),lblRate.get(ar),lblAmount.get(ar),txtwastagepercent.get(ar),lblTotal.get(ar),lblId.get(ar),txtRawRemarks.get(ar)},ar);

	}

	/*private void tableRowAdd2(final int ar)
	{
		lblsa2.add(ar,new Label());
		lblsa2.get(ar).setWidth("100%");
		//lblsa.get(ar).setHeight("15px");
		lblsa2.get(ar).setValue(ar+1);

		tblblRMCost.add(ar, new Label());
		tblblRMCost.get(ar).setWidth("100%");
		tblblRMCost.get(ar).setImmediate(true);

		tblblRMAmount.add(ar,new Label());
		tblblRMAmount.get(ar).setWidth("100%");
		tblblRMAmount.get(ar).setImmediate(true);

		tblblRMPercent.add(ar,new Label());
		tblblRMPercent.get(ar).setWidth("100%");
		tblblRMPercent.get(ar).setImmediate(true);

		table2.addItem(new Object[]{lblsa2.get(ar),tblblRMCost.get(ar),tblblRMAmount.get(ar),tblblRMPercent.get(ar)},ar);
	}
	 */
	private void findPartyLoad(){

		cmbFindParty.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			String sql="select b.vGroupId,b.partyName from tbCostSheetInfo a inner join tbPartyInfo b on a.partyCode=b.vGroupId";
			List<?> list=session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindParty.addItem(element[0]);
				cmbFindParty.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
		finally{session.close();}
	}
	private void cmbConvertionDataLoad(int ar){
		tblCmbConCost.get(ar).removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			
			//String sql="select Ledger_Id,Ledger_Name from tbLedger where Ledger_Id like '%EL%'";
			
		/*	String sql="select * from "
					   +"( "
					   +"select Group_Id,Group_Name,'1' as flag  from tbMain_Group where Head_Id like 'E2' or Head_Id like  'E3' "
					   +"union all "
					   +"select Ledger_Id,Ledger_Name,'2' from tbLedger where Create_From not like 'E2-%' and Create_From not like  'E3-%' and Ledger_Id like '%EL%' "
					   +") as temp order by temp.flag ";*/
			
			String sql ="select Ledger_Id,Ledger_Name,1 from tbLedger where Parent_Id like 'G136' ";
			int i=0;
			List<?> list=session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();
				tblCmbConCost.get(ar).addItem(element[0]);
				tblCmbConCost.get(ar).setItemCaption(element[0], element[1].toString());
				
				if(ar==0)
				{
					hm.put(element[0],element[2].toString() );
					i++;	
				}
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
		finally{session.close();}
	}
	private boolean doubleEntryCheck(String caption,int row){

		for(int i=0;i<tblCmbConCost.size();i++){

			if(i!=row && caption.equals(tblCmbConCost.get(i).getItemCaption(tblCmbConCost.get(i).getValue()))){

				return false;
			}
		}
		return true;
	}
	private void tableRowAdd3(final int ar)
	{
		lblsa3.add(ar,new Label());
		lblsa3.get(ar).setWidth("100%");
		lblsa3.get(ar).setValue(ar+1);
		

		tblCmbConCost.add(ar, new ComboBox());
		tblCmbConCost.get(ar).setWidth("100%");
		tblCmbConCost.get(ar).setImmediate(true);
		cmbConvertionDataLoad(ar);

		tblCmbConCost.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tblCmbConCost.get(ar).getValue()!=null)
				{

					if(doubleEntryCheck(tblCmbConCost.get(ar).getItemCaption(tblCmbConCost.get(ar).getValue()),ar))
					{
						tbTxtConAmount.get(ar).focus();
						if(ar==tblCmbConCost.size()-1)
						{
							tableRowAdd3(ar+1);	
						}
					}
					else
					{
						tblCmbConCost.get(ar).setValue(null);
						showNotification("Double Entry",Notification.TYPE_WARNING_MESSAGE);
						tblCmbConCost.get(ar).focus();
					}

				}
			}
		});

		/*tbTxtConAmountPerSqm.add(ar,new AmountField());
		tbTxtConAmountPerSqm.get(ar).setWidth("100%");
		tbTxtConAmountPerSqm.get(ar).setImmediate(true);
		
		tbTxtConAmountPerSqm.get(ar).addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event) 
			{
				
				if(!tbTxtConAmountPerSqm.get(ar).getValue().toString().isEmpty()&&tblCmbConCost.get(ar).getValue()!=null){
					Double perPcs = 0.0;
					for(int a=0;a<table1.size();a++){
						if(tbcmbItemType.get(a).getValue()!=null){
							if(tbcmbItemType.get(a).getValue().toString().equalsIgnoreCase("Raw Material")){
								perPcs=Double.parseDouble(lblWeight.get(a).getValue().toString());
								perPcs=1/perPcs;
								break;
							}
						}
					}
					Session session=null;
					Transaction tx=null;
					try{
						session=SessionFactoryUtil.getInstance().openSession();
						tx=session.beginTransaction();
						Iterator iter=session.createSQLQuery("select "+tbTxtConAmountPerSqm.get(ar).getValue()+"/"+perPcs+"").list().iterator();
						if(iter.hasNext()){
							Object element=(Object) iter.next();
							tbTxtConAmount.get(ar).setValue(element);
						}
					}
					catch(Exception exp){
						showNotification(""+exp,Notification.TYPE_WARNING_MESSAGE);
					}
					finally{
						if(tx!=null||session!=null){
							tx.commit();
							session.close();
						}
					}
				}
			}
		});*/
		
		tbTxtConAmount.add(ar,new AmountField());
		tbTxtConAmount.get(ar).setWidth("100%");
		tbTxtConAmount.get(ar).setImmediate(true);
	
		tbTxtConAmount.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				double d=0;
				for(int a=0;a<tblCmbConCost.size();a++)
				{
					if(!tbTxtConAmount.get(ar).getValue().toString().isEmpty()){
						d=d+Double.parseDouble(tbTxtConAmount.get(a).getValue().toString().isEmpty()?"0.0":tbTxtConAmount.get(a).getValue().toString());
						table3.setColumnFooter("Amount/Pcs", "Total: "+decf1.format(d));
					}
				}
				
					double RmCost=0.00;
					double TranportationCost=0.00;
					double packingcost=0.00;
					double TotalCost=0.00;
					
					StringTokenizer token=new StringTokenizer(table1.getColumnFooter("Total"),":");
					token.nextToken();

					 RmCost=Double.parseDouble(token.nextToken());
					
					if(!AmtTransport.getValue().toString().isEmpty())
					{
						TranportationCost=Double.parseDouble(AmtTransport.getValue().toString()); 
					}
					
					if(!AmtPacking.getValue().toString().isEmpty())
					{
						packingcost=Double.parseDouble(AmtPacking.getValue().toString()); 
					}
					
					TotalCost=d+RmCost+TranportationCost+packingcost;
					
					AmtTotalCost.setValue(decf2.format(TotalCost) );
					
					double markup=0.00;
					
					if (!txtMarkup.getValue().toString().isEmpty())
					{
						markup=(Double.parseDouble(txtMarkup.getValue().toString())* TotalCost)/100 ;
						AmtMarkUp.setValue(decf2.format(markup) );
					}
					
					double EndPrice=0.00;
					 EndPrice=markup+TotalCost;
					
					AmtEndPrice.setValue(decf.format(EndPrice));
					
				System.out.println(d);
			}
		});
		tbtxtConRemarks.add(ar,new TextField());
		tbtxtConRemarks.get(ar).setWidth("100%");
		tbtxtConRemarks.get(ar).setImmediate(true);

		table3.addItem(new Object[]{lblsa3.get(ar),tblCmbConCost.get(ar),tbTxtConAmount.get(ar),tbtxtConRemarks.get(ar)},ar);
	}

	private void tableClear1()
	{
		for(int i=0;i<lblsa1.size();i++)
		{
			tbcmbItemType.get(i).setValue(null);
			lblRawName.get(i).setValue("");
			lblType.get(i).setValue("");
			lblWeight.get(i).setValue("");
			lblAmount.get(i).setValue("");
			lblId.get(i).setValue("");
			lblRate.get(i).setValue("");
			txtRawRemarks.get(i).setValue("");
			chk.get(i).setValue(false);
			txtwastagepercent.get(i).setValue("");
			lblTotal.get(i).setValue("");
			table1.setColumnFooter("Total", "Total : "+0);
			lblUnit.get(i).setValue("");
			
		}
	}
	private void tableClear2()
	{
		for(int i=0;i<lblsa3.size();i++)
		{
			tblCmbConCost.get(i).setValue(null);
			tbTxtConAmount.get(i).setValue("");
			tbtxtConRemarks.get(i).setValue("");
			//tbTxtConAmountPerSqm.get(i).setValue("");
		}
	}
	private void jobNoLoad(){
		
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try{
			String sql="select 0,isnull(MAX(cast(jobNo as int)),0)+1 as id from tbcostSheetInfo";
			List<?> list=session.createSQLQuery(sql).list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				txtStdNo.setValue(element[1]);
			}
		}
		catch(Exception exp){
			showNotification("jobNoLoad: "+exp);
		}
		finally{session.close();}
	}
	private void newButtonEvent() 
	{
		componentIni(false);
		btnInit(false);
		txtClear();
		jobNoLoad();
		cmbCompany.focus();
		isFind=false;
	}

	private void txtClear() 
	{
		cmbCompany.setValue(null);
		cmbFinishedGoods.setValue(null);
		txtStdNo.setValue("");
		dCurrent.setValue(new java.util.Date());
		cmbProduction.setValue(null);
		txtDia.setValue("");
		txtWeight.setValue("");
		txtWidthActual.setValue("");
		txtWidthConsumption.setValue("");
		txtLengthActual.setValue("");
		txtLengthConsumption.setValue("");
		txtRemarks.setValue("");
		AmtTransport.setValue("");
		AmtPacking.setValue("");
		AmtTotalCost.setValue("");
		txtMarkup.setValue("");
		AmtMarkUp.setValue("");
		AmtEndPrice.setValue("");
		tableClear1();
		tableClear2();
		cmbFindParty.setValue(null);
		cmbFindFG.setValue(null);
		cmbFindDate.setValue(null);
		cmbFindDate.setValue(null);

		button.btnNew.focus();
		table1.setColumnFooter("Rate", "");
		table3.setColumnFooter("Amount", "Total : ");
		
		
		txtAitImportPercentage.setValue("");
		txtAitImport.setValue("");
		txtAitSalesPercentage.setValue("");
		txtAitSales.setValue("");
		txtnetMarkup.setValue("");
		
		
	}

	private void focusEnter()
	{
		allComp.add(cmbCompany);
		allComp.add(cmbFinishedGoods);
		for(int i=0;i<lblsa3.size();i++)
		{
			allComp.add(tblCmbConCost.get(i));
			allComp.add(tbTxtConAmount.get(i));
			allComp.add(tbtxtConRemarks.get(i));
		}
		allComp.add(AmtTransport);
		allComp.add(AmtPacking);
		allComp.add(AmtTotalCost);
		allComp.add(txtMarkup);
		allComp.add(AmtMarkUp);
		allComp.add(AmtEndPrice);
		allComp.add(txtRemarks);
		allComp.add(txtAitImportPercentage);
		allComp.add(txtAitSalesPercentage);

		allComp.add(button.btnSave);
		allComp.add(button.btnNew);
		new FocusMoveByEnter(this,allComp);
	}

	private void btnInit(boolean t) 
	{
		button.btnNew.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnEdit.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void componentIni(boolean t) 
	{
		lblCompany.setEnabled(!t);
		cmbCompany.setEnabled(!t);

		lblFinishedGoods.setEnabled(!t);
		cmbFinishedGoods.setEnabled(!t);

		lblJobNo.setEnabled(!t);
		txtStdNo.setEnabled(!t);

		lblDate.setEnabled(!t);
		dCurrent.setEnabled(!t);

		lblPercent1.setEnabled(!t);
		
		table1.setEnabled(!t);
		table3.setEnabled(!t);

		lblTransport.setEnabled(!t);
		AmtTransport.setEnabled(!t);

		lblPacking.setEnabled(!t);
		AmtPacking.setEnabled(!t);

		lblTotalCost.setEnabled(!t);
		AmtTotalCost.setEnabled(!t);

		//lblMarkup.setEnabled(!t);
		lblMarkUp.setEnabled(!t);
		AmtMarkUp.setEnabled(!t);

		lblEndPrice.setEnabled(!t);
		AmtEndPrice.setEnabled(!t);

		panelSearch.setEnabled(!t);
		lblPanel.setEnabled(!t);

		txtRemarks.setEnabled(!t);
		lblAdd.setEnabled(!t);
		lblAdd1.setEnabled(!t);
		txtMarkup.setEnabled(!t);
		lblRMList.setEnabled(!t);
		lblRMCost.setEnabled(!t);
		lblRemarks.setEnabled(!t);
		txtAitImportPercentage.setEnabled(!t);
		txtAitImport.setEnabled(!t);
		txtAitSalesPercentage.setEnabled(!t);
		txtAitSales.setEnabled(!t);
		txtnetMarkup.setEnabled(!t);
	}

	private void formValidation()
	{
		if(sessionBean.isSubmitable())
		{

		}
	}

	private boolean deleteData(Session session,Transaction tx) {

		try
		{

			session.createSQLQuery("delete from tbCostSheetInfo where jobNo='"+txtStdNo.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete from tbCostSheetRmDetails where jobNo='"+txtStdNo.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete from tbCostSheetConvertionDetails where jobNo='"+txtStdNo.getValue()+"'").executeUpdate();

			return true;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	private void saveButtonEvent()
	{
		if(cmbCompany.getValue()!=null){
			if(cmbFinishedGoods.getValue()!=null){
					if(tblCmbConCost.get(0).getValue()!=null&&!tbTxtConAmount.get(0).getValue().toString().isEmpty()){
						if(isUpdate){
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
										if(deleteData(session,tx)){

											insertData(session,tx);
											txtClear();
											componentIni(true);
										}

									}
								}
							});
						}
						else{
							MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
							mb.show(new EventListener() 
							{
								public void buttonClicked(ButtonType buttonType) 
								{
									if (buttonType == ButtonType.YES) 
									{
										Transaction tx = null;
										Session session = SessionFactoryUtil.getInstance().getCurrentSession();
										tx = session.beginTransaction();
										insertData(session,tx);
										txtClear();
										componentIni(true);
										btnInit(true);
									}
								}
							});
						}
					}
					else{
						showNotification("Please Provid All Fields.",Notification.TYPE_WARNING_MESSAGE);
					}
			
			}
			else{
				showNotification("Select Finished Goods Name Please.",Notification.TYPE_WARNING_MESSAGE);
				cmbFinishedGoods.focus();
			}
		}
		else{
			showNotification("Select Party Name Please.",Notification.TYPE_WARNING_MESSAGE);
			cmbCompany.focus();
		}
	}
	private void insertData(Session session,Transaction tx){
		StringTokenizer token=new StringTokenizer(table1.getColumnFooter("Total"),":");
		token.nextToken();
		StringTokenizer token1=new StringTokenizer(table3.getColumnFooter("Amount/Pcs"),":");
		token1.nextToken();
		try{
			
			double AitImportpercent=0.00;
			double AitImportAmount=0.00;
			double AitSalesPercent=0.00;
			double AitSalesAmount=0.00;
			double netMarkup=0.00;
			
				
			
			if(!txtAitImportPercentage.getValue().toString().isEmpty())
			{
				AitImportpercent=Double.parseDouble(txtAitImportPercentage.getValue().toString());	
			}
			
			if(!txtAitImport.getValue().toString().isEmpty())
			{
				AitImportAmount=Double.parseDouble(txtAitImport.getValue().toString());	
			}
			
			if(!txtAitSalesPercentage.getValue().toString().isEmpty())
			{
				AitSalesPercent=Double.parseDouble(txtAitSalesPercentage.getValue().toString());	
			}
			
			if(!txtAitSales.getValue().toString().isEmpty())
			{
				AitSalesAmount=Double.parseDouble(txtAitSales.getValue().toString());	
			}
			
			if(!txtnetMarkup.getValue().toString().isEmpty())
			{
				netMarkup=Double.parseDouble(txtnetMarkup.getValue().toString()); 
			}
			else
			{
				System.out.println("Rabiul Hasan Bahar");
				netMarkup=0.00;	
			}
			
			
			String sql="insert into tbcostSheetInfo (jobNo,partyCode,fgCode,productionType,declareDate,"+
					" wstPercent,wastageRawCost,convertionCost,transportCost,packingCost,"+
					" totalCost,markupPercent,markupAmt,endPrice,remarks,userip,userid,entryTime,aitImportPercent,aitImportAmount," +
					" aitSalesPercent,aitSalesAmount,netMarkup)"+
					"values"+
					" ('"+txtStdNo.getValue()+"','"+cmbCompany.getValue()+"','"+cmbFinishedGoods.getValue()+"','"+cmbProduction.getValue()+"','"+dateFormatnew.format(dCurrent.getValue())+"',"+
					" 0,'"+token.nextToken().trim()+"','"+token1.nextToken().trim()+"'," +
					"'"+AmtTransport.getValue()+"','"+AmtPacking.getValue()+"','"+AmtTotalCost.getValue()+"'," +
					"'"+txtMarkup.getValue()+"','"+AmtMarkUp.getValue()+"','"+AmtEndPrice.getValue()+"'," +
					"'"+txtRemarks.getValue()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP ," +
					" '"+AitImportpercent+"', '"+AitImportAmount+"','"+AitSalesPercent+"','"+AitSalesAmount+"','"+netMarkup+"')";
			session.createSQLQuery(sql).executeUpdate();
			for(int a=0;a<lblsa1.size();a++){
				if(!lblRawName.get(a).toString().isEmpty() && chk.get(a).booleanValue() )
				{
					int flag=0;
					if(chk.get(a).booleanValue())
					{
						flag=1;	
					}
					else
					{
						flag=0;	
					}
					
					double total=Double.parseDouble(lblTotal.get(a).getValue().toString().isEmpty()?"0.00":lblTotal.get(a).getValue().toString()) ;
					String head="";
					if(tbcmbItemType.get(a).getValue()!=null)
					{
						head=tbcmbItemType.get(a).getValue().toString();
					}
					else
					{
						head="";	
					}

					String sql1="insert into tbcostSheetRmDetails (jobno,rawitemcode,itemtype,qty,rate,amount,remarks,userip,userid,entrytime,flag,wastagePercent,total,Head)values "+
							"('"+txtStdNo.getValue()+"','"+lblId.get(a).getValue()+"','"+lblType.get(a).getValue()+"'," +
							" '"+decf1.format(Double.parseDouble(lblWeight.get(a).getValue().toString()) ) +"','"+decf1.format( Double.parseDouble(lblRate.get(a).getValue().toString())) +"','"+decf1.format(Double.parseDouble(lblAmount.get(a).getValue().toString()) ) +"'," +
							" '"+txtRawRemarks.get(a).getValue()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'"+flag+"','"+txtwastagepercent.get(a).getValue()+"','"+total+"','"+head+"' )";
					session.createSQLQuery(sql1).executeUpdate();
				}
			}
			for(int b=0;b<lblsa3.size();b++){
				if(tblCmbConCost.get(b).getValue()!=null&&!tbTxtConAmount.get(b).getValue().toString().isEmpty()){

					String sql2="insert into tbCostSheetConvertionDetails (jobNo,overheadId,amount,remarks,userip,userid,entrytime,overHeadName,flag)values"+
							" ('"+txtStdNo.getValue()+"','"+tblCmbConCost.get(b).getValue()+"','"+tbTxtConAmount.get(b).getValue()+"'," +
							" '"+tbtxtConRemarks.get(b).getValue()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'"+tblCmbConCost.get(b).getItemCaption(tblCmbConCost.get(b).getValue())+"','"+hm.get(tblCmbConCost.get(b).getValue())+"')";
					session.createSQLQuery(sql2).executeUpdate();
				}
			}
			tx.commit();
			showNotification("All Information Saved Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("InsertData: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}



	private void updateButtonEvent()
	{

	}

	private void findButtonEvent()
	{
		/*Window win = new (sessionBean, AutoId,"AutoId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(AutoId.getValue().toString().length() > 0)
				{
					System.out.println(AutoId.getValue().toString());
					txtClear();
					findInitialise(AutoId.getValue().toString());
					button.btnEdit.focus();
				}
			}
		});

		this.getParent().addWindow(win);*/
	}

	private void findInitialise(String AutoId)
	{
		Transaction tx = null;
		String sql = null;
		try 
		{

		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void totalCalc()
	{
		StringTokenizer token=new StringTokenizer(table1.getColumnFooter("Total"),":");
		token.nextToken();
		String s=token.nextToken();
		StringTokenizer token1=new StringTokenizer(table3.getColumnFooter("Amount/Pcs"),":");
		token1.nextToken();
		String s1=token1.nextToken();
		
		System.out.println("String Is"+s1);

		if(!s.isEmpty()&&!s1.isEmpty())
		{
			String transport=AmtTransport.getValue().toString().isEmpty()?"0.0":AmtTransport.getValue();
			String packing=AmtPacking.getValue().toString().isEmpty()?"0.0":AmtPacking.getValue();
			double total=Double.parseDouble(s.equalsIgnoreCase(" ")?"0.00":s)+Double.parseDouble(s1.equalsIgnoreCase(" ")?"0.00":s1)+Double.parseDouble(transport)+Double.parseDouble(packing);
			AmtTotalCost.setValue(decf.format(total));
			System.out.println(AmtTotalCost.getValue());
			
			
			/*if(isFind)
			{*/
				double markup=0.00;
				
				if (!txtMarkup.getValue().toString().isEmpty())
				{
					markup=(Double.parseDouble(txtMarkup.getValue().toString())* total)/100 ;
					AmtMarkUp.setValue(markup);
				}
				
				double EndPrice=0.00;
				 EndPrice=markup+total;
				
				AmtEndPrice.setValue(decf.format(EndPrice));
			//}
		
			
		}
	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("1100px");
		mainLayout.setHeight("670px");
		mainLayout.setMargin(false);

		setWidth("1130px");

		lblCompany=new Label();
		lblCompany.setValue("Party Name: ");
		lblCompany.setWidth("-1px");
		lblCompany.setHeight("-1px");
		mainLayout.addComponent(lblCompany,"top:20px;left:10px");

		cmbCompany=new ComboBox();
		cmbCompany.setImmediate(true);
		cmbCompany.setNullSelectionAllowed(true);
		cmbCompany.setWidth("300px");
		//cmbCompany.setHeight("28px");
		mainLayout.addComponent(cmbCompany,"top:18px;left:120px;");

		lblFinishedGoods=new Label();
		lblFinishedGoods.setValue("Finished Goods: ");
		lblFinishedGoods.setWidth("-1px");
		lblFinishedGoods.setHeight("-1px");
		mainLayout.addComponent(lblFinishedGoods,"top:45px;left:10px");

		cmbFinishedGoods=new ComboBox();
		cmbFinishedGoods.setImmediate(true);
		cmbFinishedGoods.setNullSelectionAllowed(true);
		cmbFinishedGoods.setWidth("300px");
		//cmbFinishedGoods.setHeight("28px");
		mainLayout.addComponent(cmbFinishedGoods,"top:43px;left:120px;");

		lblProductionType=new Label();
		lblProductionType.setValue("Production Type: ");
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		lblProductionType.setEnabled(false);
		mainLayout.addComponent(lblProductionType,"top:70px;left:10px");

		cmbProduction=new ComboBox();
		cmbProduction.setImmediate(true);
		cmbProduction.setNullSelectionAllowed(true);
		cmbProduction.setWidth("300px");
		cmbProduction.setEnabled(false);
		//cmbFinishedGoods.setHeight("28px");
		mainLayout.addComponent(cmbProduction,"top:68px;left:120px;");

		lblWidthActual = new Label("Width  <font color='#0000FF'><b>[Actual]</b></Font>  :",Label.CONTENT_XHTML);
		lblWidthActual.setImmediate(false);
		lblWidthActual.setWidth("-1px");
		lblWidthActual.setHeight("-1px");
		lblWidthActual.setVisible(false);
		mainLayout.addComponent(lblWidthActual,"top:70px;left:130px;");

		txtWidthActual = new TextRead(1);
		txtWidthActual.setImmediate(true);
		txtWidthActual.setWidth("70px");
		txtWidthActual.setHeight("-1px");
		txtWidthActual.setVisible(false);
		mainLayout.addComponent(txtWidthActual,"top:68px;left:220px;");

		lblWidthConsumption = new Label("Width  <font color='#0000FF'><b>[Consumption]</b></Font>  :",Label.CONTENT_XHTML);
		lblWidthConsumption.setImmediate(false);
		lblWidthConsumption.setWidth("-1px");
		lblWidthConsumption.setHeight("-1px");
		lblWidthConsumption.setVisible(false);
		mainLayout.addComponent(lblWidthConsumption,"top:70px;left:305px;");

		txtWidthConsumption = new TextRead(1);
		txtWidthConsumption.setImmediate(true);
		txtWidthConsumption.setWidth("70px");
		txtWidthConsumption.setHeight("-1px");
		txtWidthConsumption.setVisible(false);
		mainLayout.addComponent(txtWidthConsumption,"top:68px;left:430px;");

		lblHeightActual = new Label("Height  <font color='#0000FF'><b>[Actual]</b></Font>  :",Label.CONTENT_XHTML);
		lblHeightActual.setImmediate(false);
		lblHeightActual.setWidth("-1px");
		lblHeightActual.setHeight("-1px");
		lblHeightActual.setVisible(false);
		mainLayout.addComponent(lblHeightActual,"top:70px;left:520px;");

		txtLengthActual = new TextRead(1);
		txtLengthActual.setImmediate(true);
		txtLengthActual.setWidth("70px");
		txtLengthActual.setHeight("-1px");
		txtLengthActual.setVisible(false);
		mainLayout.addComponent(txtLengthActual,"top:68px;left:620px;");

		lblHeightConsumption = new Label("Height  <font color='#0000FF'><b>[Consumption]</b></Font>  :",Label.CONTENT_XHTML);
		lblHeightConsumption.setImmediate(false);
		lblHeightConsumption.setWidth("-1px");
		lblHeightConsumption.setHeight("-1px");
		lblHeightConsumption.setVisible(false);
		mainLayout.addComponent(lblHeightConsumption,"top:70px;left:720px;");

		txtLengthConsumption = new TextRead(1);
		txtLengthConsumption.setImmediate(true);
		txtLengthConsumption.setWidth("70px");
		txtLengthConsumption.setHeight("-1px");
		txtLengthConsumption.setVisible(false);
		mainLayout.addComponent(txtLengthConsumption,"top:68px;left:850px;");

		lblDate=new Label();
		lblDate.setValue("Date: ");
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate,"top:20px;left:430px");

		dCurrent=new PopupDateField();
		dCurrent.setResolution(PopupDateField.RESOLUTION_DAY);
		dCurrent.setValue(new java.util.Date());
		dCurrent.setWidth("110px");
		dCurrent.setDateFormat("dd/MM/yyyy");
		mainLayout.addComponent(dCurrent,"top:18px;left:500px;");
		
		lblJobNo=new Label();
		lblJobNo.setValue("Job No : ");
		mainLayout.addComponent(lblJobNo,"top:45px;left:430px;");

		txtStdNo=new TextRead(1);
		txtStdNo.setWidth("70px");
		txtStdNo.setHeight("-1px");
		txtStdNo.setImmediate(true);
		mainLayout.addComponent(txtStdNo,"top:43px;left:500px;");

		lblDia=new Label();
		lblDia.setValue("Dia: ");
		lblDia.setWidth("-1px");
		lblDia.setHeight("-1px");
		lblDia.setVisible(false);
		mainLayout.addComponent(lblDia,"top:20px;left:650px");

		txtDia=new TextRead(1);
		txtDia.setImmediate(true);
		txtDia.setWidth("100px");
		txtDia.setHeight("24px");
		txtDia.setVisible(false);
		mainLayout.addComponent(txtDia,"top:18px;left:720px");

		lblWeightFg=new Label();
		lblWeightFg.setValue("Weight: ");
		lblWeightFg.setWidth("-1px");
		lblWeightFg.setHeight("-1px");
		lblWeightFg.setVisible(false);
		mainLayout.addComponent(lblWeightFg,"top:45px;left:650px");

		txtWeight=new TextRead(1);
		txtWeight.setImmediate(true);
		txtWeight.setWidth("100px");
		txtWeight.setHeight("24px");
		txtWeight.setVisible(false);
		mainLayout.addComponent(txtWeight,"top:43px;left:720px");
		
		lblPanel= new Label(" <font color='##0000FF' size='4px'><b><Strong>Search :<Strong></b></font>");
		lblPanel.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblPanel, "top:270px;left:700px;");

		panelSearch=new Panel();
		panelSearch.setWidth("400px");
		panelSearch.setHeight("150px");
		panelSearch.setStyleName("panelSearch");
		mainLayout.addComponent(panelSearch, "top:290.0px;left:650.0px;");

		FormLayout frmLayout=new FormLayout();
		frmLayout.setSpacing(true);
		frmLayout.setMargin(false);
		frmLayout.addComponent(cmbFindParty);

		cmbFindParty.setImmediate(true);
		cmbFindParty.setWidth("250px");

		frmLayout.addComponent(cmbFindFG);

		cmbFindFG.setImmediate(true);
		cmbFindFG.setWidth("250px");

		frmLayout.addComponent(cmbFindDate);
		cmbFindDate.setImmediate(true);
		panelSearch.addComponent(frmLayout);

		lblTransport= new Label("<font color='#FF0000'><b><Strong>C.<Strong></b></font> <font color='##0000FF'><b><Strong>Transporation :<Strong></b></font>");
		lblTransport.setImmediate(true);
		lblTransport.setWidth("-1px");
		lblTransport.setHeight("-1px");
		lblTransport.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblTransport, "top:480.0px;left:120.0px;");

		AmtTransport = new AmountCommaSeperator();
		AmtTransport.setImmediate(true);
		AmtTransport.setWidth("150px");
		AmtTransport.setHeight("24px");
		mainLayout.addComponent(AmtTransport, "top:478.0px;left:260.0px;");

		lblPacking= new Label("<font color='#FF0000'><b><Strong>D.<Strong></b></font>  <font color='##0000FF'><b><Strong>Packing Material :<Strong></b></font>");
		lblPacking.setImmediate(false);
		lblPacking.setWidth("-1px");
		lblPacking.setHeight("-1px");
		lblPacking.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblPacking,"top:505.0px;left:120.0px;");

		AmtPacking = new AmountCommaSeperator();
		AmtPacking.setImmediate(true);
		AmtPacking.setWidth("150px");
		AmtPacking.setHeight("24px");
		mainLayout.addComponent(AmtPacking, "top:503.0px;left:260.0px;");

		lblTotalCost= new Label("<font color='#FF0000'><b><Strong>E.<Strong></b></font>  <font color='##0000FF'><b><Strong>Total Cost :<Strong></b></font>");
		lblTotalCost.setImmediate(false);
		lblTotalCost.setWidth("-1px");
		lblTotalCost.setHeight("-1px");
		lblTotalCost.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblTotalCost,"top:530.0px;left:120.0px;");

		AmtTotalCost = new AmountCommaSeperator();
		AmtTotalCost.setImmediate(true);
		AmtTotalCost.setWidth("150px");
		AmtTotalCost.setHeight("24px");
		mainLayout.addComponent(AmtTotalCost, "top:528.0px;left:260.0px;");

		lblAdd=new Label("<font color='##0000FF'><b><Strong>( A+B+C+D )<Strong></b></font> ");
		lblAdd.setImmediate(false);
		lblAdd.setWidth("-1px");
		lblAdd.setHeight("-1px");
		lblAdd.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblAdd,"top:530.0px;left:20.0px;");

		lblMarkUp= new Label("<font color='#FF0000'><b><Strong>F.<Strong></b></font>  <font color='##0000FF'><b><Strong>Mark-Up :<Strong></b></font>");
		lblMarkUp.setImmediate(false);
		lblMarkUp.setWidth("-1px");
		lblMarkUp.setHeight("-1px");
		lblMarkUp.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblMarkUp,"top:555.0px;left:120.0px;");

		txtMarkup=new TextField();
		txtMarkup.setWidth("40px");
		txtMarkup.setHeight("24px");
		txtMarkup.setImmediate(true);
		mainLayout.addComponent(txtMarkup,"top:553px;left:260.0px");

		lblPercent1=new Label();
		lblPercent1.setWidth("-1px");
		lblPercent1.setHeight("-1px");
		lblPercent1.setValue("%");
		mainLayout.addComponent(lblPercent1,"top:555px;left:305px");

		AmtMarkUp = new AmountCommaSeperator();
		AmtMarkUp.setImmediate(true);
		AmtMarkUp.setWidth("80px");
		AmtMarkUp.setHeight("24px");
		mainLayout.addComponent(AmtMarkUp, "top:553.0px;left:330.0px;");

		lblAdd1=new Label("<font color='##0000FF'><b><Strong>( E+F )<Strong></b></font> ");
		lblAdd1.setImmediate(false);
		lblAdd1.setWidth("-1px");
		lblAdd1.setHeight("-1px");
		lblAdd1.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblAdd1,"top:580.0px;left:20.0px;");

		lblEndPrice= new Label("<font color='#FF0000'><b><Strong>G.<Strong></b></font>  <font color='##0000FF'><b><Strong>End Price : <Strong></b></font>");
		lblEndPrice.setImmediate(false);
		lblEndPrice.setWidth("-1px");
		lblEndPrice.setHeight("-1px");
		lblEndPrice.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblEndPrice,"top:580.0px;left:120.0px;");

		AmtEndPrice = new AmountCommaSeperator();
		AmtEndPrice.setImmediate(true);
		AmtEndPrice.setWidth("150px");
		AmtEndPrice.setHeight("24px");
		mainLayout.addComponent(AmtEndPrice, "top:578.0px;left:260.0px;");
		
		lblRemarks= new Label("<font color='#FF0000'><b><Strong>#.<Strong></b></font>  <font color='##0000FF'><b><Strong>Remarks<Strong></b></font>");
		lblRemarks.setImmediate(false);
		lblRemarks.setWidth("-1px");
		lblRemarks.setHeight("-1px");
		lblRemarks.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblRemarks, "top:482.0px;left:430.0px;");
		//mainLayout.addComponent(lblRemarks, "top:110.0px;left:20.0px;");

		txtRemarks = new TextArea();
		txtRemarks.setImmediate(true);
		txtRemarks.setWidth("380px");
		txtRemarks.setHeight("100px");
		mainLayout.addComponent(txtRemarks,"top:500; left:430.0px;");
		
		lblAitImort= new Label("<font color=blue>AIT On Import :</font>");
		lblAitImort.setImmediate(false);
		lblAitImort.setWidth("-1px");
		lblAitImort.setHeight("-1px");
		lblAitImort.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblAitImort, "top:530.0px;left:840.0px;");
		
		txtAitImportPercentage = new  TextField();
		txtAitImportPercentage.setImmediate(true);
		txtAitImportPercentage.setWidth("40px");
		txtAitImportPercentage.setHeight("24.00px");
		mainLayout.addComponent(txtAitImportPercentage,"top:528; left:930.0px;");
		
		lblAitImportPercent= new Label("<font color= red> <b>%</b></font>");
		lblAitImportPercent.setImmediate(false);
		lblAitImportPercent.setWidth("-1px");
		lblAitImportPercent.setHeight("-1px");
		lblAitImportPercent.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblAitImportPercent, "top:528.0px;left:975.0px;");
		
		txtAitImport = new  TextField();
		txtAitImport.setImmediate(true);
		txtAitImport.setWidth("150px");
		txtAitImport.setHeight("24px");
		mainLayout.addComponent(txtAitImport,"top:528; left:985.0px;");	
		
		lblAitSales= new Label("<font color=blue> AIT On Sales :</font>");
		lblAitSales.setImmediate(false);
		lblAitSales.setWidth("-1px");
		lblAitSales.setHeight("-1px");
		lblAitSales.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblAitSales, "top:556.0px;left:840.0px;");

		txtAitSalesPercentage = new  TextField();
		txtAitSalesPercentage.setImmediate(true);
		txtAitSalesPercentage.setWidth("40px");
		txtAitSalesPercentage.setHeight("24px");
		mainLayout.addComponent(txtAitSalesPercentage,"top:554; left:930.0px;");
		
		lblAitSalesPercent= new Label("<font color= red> <b>%</b></font>");
		lblAitSalesPercent.setImmediate(false);
		lblAitSalesPercent.setWidth("-1px");
		lblAitSalesPercent.setHeight("-1px");
		lblAitSalesPercent.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblAitSalesPercent, "top:556.0px;left:975.0px;");
		
		txtAitSales = new  TextField();
		txtAitSales.setImmediate(true);
		txtAitSales.setWidth("150px");
		txtAitSales.setHeight("24px");
		mainLayout.addComponent(txtAitSales,"top:554.0px; left:985.0px;");
		
		lblnetMarkup= new Label("<font color=blue>Net Markup :</b>");
		lblnetMarkup.setImmediate(false);
		lblnetMarkup.setWidth("-1px");
		lblnetMarkup.setHeight("-1px");
		lblnetMarkup.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblnetMarkup, "top:582.0px;left:840.0px;");
		
		txtnetMarkup = new  TextField();
		txtnetMarkup.setImmediate(true);
		txtnetMarkup.setWidth("150px");
		txtnetMarkup.setHeight("24px");
		mainLayout.addComponent(txtnetMarkup,"top:580.0px; left:985.0px;");
		
		lblRMList= new Label("<font color='#FF0000'><b><Strong>A.<Strong></b></font>  <font color='##0000FF'><b><Strong>RM List And Cost<Strong></b></font>");
		lblRMList.setImmediate(false);
		lblRMList.setWidth("-1px");
		lblRMList.setHeight("-1px");
		lblRMList.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblRMList, "top:95.0px;left:20.0px;");
		
		table1.setWidth("1080px");
		table1.setHeight("130px");
		table1.setFooterVisible(true);
		table1.setColumnCollapsingAllowed(true);
		
		table1.setColumnFooter("Total", "Total : "+0);
		table1.setColumnCollapsingAllowed(true);

		table1.addContainerProperty("SL", Label.class, new Label());
		table1.setColumnWidth("SL", 10);

		table1.addContainerProperty("CHK", CheckBox.class, new CheckBox());
		table1.setColumnWidth("CHK", 20);
		
		table1.addContainerProperty("Item Head", ComboBox.class, new ComboBox());
		table1.setColumnWidth("Item Head", 120);

		table1.addContainerProperty("Raw Material Name", Label.class, new Label());
		table1.setColumnWidth("Raw Material Name",200);

		table1.addContainerProperty("Item Type", Label.class, new Label());
		table1.setColumnWidth("Item Type",100);
		table1.setColumnCollapsed("Item Type", true);
		
		table1.addContainerProperty("Unit", Label.class, new Label());
		table1.setColumnWidth("Unit",50);

		table1.addContainerProperty("Qty", AmountField.class, new AmountField());
		table1.setColumnWidth("Qty",75);

		table1.addContainerProperty("Rate", AmountField.class, new AmountField());
		table1.setColumnWidth("Rate", 70);

		table1.addContainerProperty("Amount", Label.class, new Label());
		table1.setColumnWidth("Amount",75);
		
		table1.addContainerProperty("Wtg.Percent",AmountField.class, new AmountField());
		table1.setColumnWidth("Wtg.Percent",70);
		
		table1.addContainerProperty("Total",TextRead.class, new TextRead(1));
		table1.setColumnWidth("Total",70);
		
		table1.addContainerProperty("RmCode", Label.class, new Label());
		table1.setColumnWidth("RmCode",60);
		table1.setColumnCollapsed("RmCode", true);

		table1.addContainerProperty("Remarks", TextField.class, new TextField());
		table1.setColumnWidth("Remarks",120);

		lblRMCost= new Label("<font color='#FF0000'><b><Strong>B.<Strong></b></font>  <font color='##0000FF'><b><Strong>Conversion cost<Strong></b></font>");
		lblRMCost.setImmediate(false);
		lblRMCost.setWidth("-1px");
		lblRMCost.setHeight("-1px");
		lblRMCost.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblRMCost, "top:250.0px;left:24.0px;");

		table3.setWidth("590px");
		table3.setHeight("170px");
		table3.setFooterVisible(true);
		table3.setColumnFooter("Amount/Pcs", "Total : "+0);

	/*	table3.addContainerProperty("SL", Label.class, new Label());
		table3.setColumnWidth("SL", 15);

		table3.addContainerProperty("Over Head", ComboBox.class, new ComboBox());
		table3.setColumnWidth("Over Head",230);

		table3.addContainerProperty("Amount/Sqm", AmountField.class, new AmountField());
		table3.setColumnWidth("Amount/Sqm",80);
		
		table3.addContainerProperty("Amount/Pcs", AmountField.class, new AmountField());
		table3.setColumnWidth("Amount/Pcs",80);

		table3.addContainerProperty("Remarks", TextField.class, new TextField());
		table3.setColumnWidth("Remarks",100);*/
		
		table3.addContainerProperty("SL", Label.class, new Label());
		table3.setColumnWidth("SL", 15);
		
		table3.addContainerProperty("Over Head", ComboBox.class, new ComboBox());
		table3.setColumnWidth("Over Head",300);

		/*table3.addContainerProperty("Amount/Sqm", AmountField.class, new AmountField());
		table3.setColumnWidth("Amount/Sqm",80);*/
		
		table3.addContainerProperty("Amount/Pcs", AmountField.class, new AmountField());
		table3.setColumnWidth("Amount/Pcs",80);

		table3.addContainerProperty("Remarks", TextField.class, new TextField());
		table3.setColumnWidth("Remarks",120);

		mainLayout.addComponent(table1,"top:115; left:20.0px;");

		//mainLayout.addComponent(table2, "top:130.0px;left:400.0px;");
		mainLayout.addComponent(table3,"top:270; left:20.0px;");
		mainLayout.addComponent(button, "top:620.0px; left:225.0px ");		
		return mainLayout;
	}
}
