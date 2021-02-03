package com.example.productionSetup;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.FinishedGoodsModule.SemiFgFindWindow;
import acc.appform.FinishedGoodsModule.SemiFgOpeningFindWindow;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class SemiFgOpeningStock extends Window{

	SessionBean sessionBean;
	AbsoluteLayout mainLayout;

	private Label
	lblOpeningYear, lblProductionType, lblsemiFgName, lblUnit, lblcolor, lblstdWeight, lblQty, lblLine, lblOpeningYearFind,lblRate,lblAmount,lblgm;

	private ComboBox
	cmbProductionType, cmbsemiFgName, cmbOpeningYear;

	private InlineDateField
	dOpeningYear;

	private TextRead
	txtUnit,txtcolor,txtstdWeight;

	private AmountField
	aQty,aRate,aAmount;

	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");

	private DecimalFormat decFormat = new DecimalFormat("#0");
	private DecimalFormat decFormat1 = new DecimalFormat("#0.00");

	boolean 
	isUpdate = false;
	boolean 
	isFind = false;

	private Table table=new Table();

	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblFg = new ArrayList<Label>();
	private ArrayList<Label> lblFgCode = new ArrayList<Label>();

	private CommonButton
	button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "","Exit");
	private TextField txtItemID = new TextField();
	double tempOpAmount=0.0;

	public SemiFgOpeningStock(SessionBean sessionBean){
		this.sessionBean = sessionBean;
		this.setCaption("SEMI FINISHED GOODS OPENING STOCK :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		btnIni(true);
		componentIni(true);
		txtClear();
		focusEnter();
		setEventAction();
		cmbProductionTypeData();
		
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("590px");
		setHeight("410px");

		// lblOpeningYear
		lblOpeningYear = new Label("Opening Year :");
		lblOpeningYear.setImmediate(false);
		lblOpeningYear.setWidth("-1px");
		lblOpeningYear.setHeight("-1px");

		// dOpeningYear
		dOpeningYear = new InlineDateField();
		dOpeningYear.setImmediate(true);		
		dOpeningYear.setWidth("-1px");
		dOpeningYear.setHeight("-1px");
		dOpeningYear.setInvalidAllowed(false);
		dOpeningYear.setDateFormat("yyyy");
		dOpeningYear.setResolution(6);

		// lblProductionType
		lblProductionType = new Label("Production Type :");
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");

		// cmbProductionType
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbProductionType.setWidth("-1px");
		cmbProductionType.setHeight("-1px");

		// lblPartyName
		lblsemiFgName = new Label("Semi Finished Goods:");
		lblsemiFgName.setImmediate(false);
		lblsemiFgName.setWidth("-1px");
		lblsemiFgName.setHeight("-1px");

		// cmbPartyName
		cmbsemiFgName = new ComboBox();
		cmbsemiFgName.setImmediate(true);
		cmbsemiFgName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbsemiFgName.setWidth("300px");
		cmbsemiFgName.setHeight("-1px");


		// lblUnit
		lblUnit = new Label("Unit :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");

		// txtUnit
		txtUnit = new TextRead("");
		txtUnit.setImmediate(true);
		txtUnit.setWidth("50px");
		txtUnit.setHeight("-1px");

		// lblUnit
		lblcolor = new Label("Color:");
		lblcolor.setImmediate(false);
		lblcolor.setWidth("-1px");
		lblcolor.setHeight("-1px");

		// txtUnit
		txtcolor = new TextRead("");
		txtcolor.setImmediate(true);
		txtcolor.setWidth("100px");
		txtcolor.setHeight("-1px");

		// lblUnit
		lblstdWeight = new Label("Standard Weight :");
		lblstdWeight.setImmediate(false);
		lblstdWeight.setWidth("-1px");
		lblstdWeight.setHeight("-1px");

		// txtUnit
		txtstdWeight = new TextRead();
		txtstdWeight.setImmediate(true);
		txtstdWeight.setWidth("100px");
		txtstdWeight.setHeight("-1px");
		
		//Unit
		lblgm = new Label("gm");
		lblgm.setImmediate(false);
		lblgm.setWidth("-1px");
		lblgm.setHeight("-1px");



		// lblQty
		lblQty = new Label("Qty :");
		lblQty.setImmediate(false);
		lblQty.setWidth("-1px");
		lblQty.setHeight("-1px");

		// aQty
		aQty = new AmountField();
		aQty.setImmediate(true);
		aQty.setWidth("-1px");
		aQty.setHeight("-1px");

		// lblQty
		lblRate = new Label("Rate :");
		lblRate.setImmediate(false);
		lblRate.setWidth("-1px");
		lblRate.setHeight("-1px");

		// aQty
		aRate = new AmountField();
		aRate.setImmediate(true);
		aRate.setWidth("-1px");
		aRate.setHeight("-1px");

		// lblQty
		lblAmount = new Label("Amount :");
		lblAmount.setImmediate(false);
		lblAmount.setWidth("-1px");
		lblAmount.setHeight("-1px");

		// aQty
		aAmount = new AmountField();
		aAmount.setImmediate(true);
		aAmount.setWidth("-1px");
		aAmount.setHeight("-1px");

		// lblLine
		lblLine = new Label("<font color='red'>==================================================================================================================================================================</font>", Label.CONTENT_XHTML);
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");




		// adding components to the mainLayout (component distance : 30px)
		mainLayout.addComponent(lblOpeningYear, "top: 20px; left: 20px;");
		mainLayout.addComponent(dOpeningYear, "top: 18px; left: 145px;");

		mainLayout.addComponent(lblProductionType, "top: 50px; left: 20px;");
		mainLayout.addComponent(cmbProductionType, "top: 48px; left: 145px;");

		mainLayout.addComponent(lblsemiFgName, "top: 80px; left: 20px;");
		mainLayout.addComponent(cmbsemiFgName, "top: 78px; left: 145px;");

		//mainLayout.addComponent(lblFgName, "top: 110px; left: 20px;");
		//mainLayout.addComponent(cmbFgName, "top: 108px; left: 120px;");

		mainLayout.addComponent(lblUnit, "top: 110px; left: 20px;");
		mainLayout.addComponent(txtUnit, "top: 108px; left: 145px;");
		
		mainLayout.addComponent(lblcolor, "top: 140px; left: 20px;");
		mainLayout.addComponent(txtcolor, "top: 138px; left: 145px;");
		
		mainLayout.addComponent(lblstdWeight, "top:170px; left: 20px;");
		mainLayout.addComponent(txtstdWeight, "top: 168px; left: 145px;");
		mainLayout.addComponent(lblgm, "top: 168px; left: 250px;");

		mainLayout.addComponent(lblQty, "top: 200px; left: 20px;");
		mainLayout.addComponent(aQty, "top: 198px; left: 145px;");

		mainLayout.addComponent(lblRate, "top:230px; left: 20px;");
		mainLayout.addComponent(aRate, "top: 228px; left: 145px;");

		mainLayout.addComponent(lblAmount, "top: 260px; left: 20px;");
		mainLayout.addComponent(aAmount, "top: 258px; left: 145px;");

		mainLayout.addComponent(lblLine, "top:290px; left: 0px;");
		mainLayout.addComponent(button, "top: 320px; left: 20px;");

		return mainLayout;
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void componentIni(boolean b) 
	{

		dOpeningYear.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		cmbsemiFgName.setEnabled(!b);
		//cmbFgName.setEnabled(!b);
		txtUnit.setEnabled(!b);
		aQty.setEnabled(!b);
		aRate.setEnabled(!b);
		aAmount.setEnabled(!b);
		txtcolor.setEnabled(!b);
		txtstdWeight.setEnabled(!b);
	}

	private void txtClear()
	{
		cmbProductionType.setValue(null);
		cmbsemiFgName.setValue(null);
		txtUnit.setValue("");
		aQty.setValue("");
		aRate.setValue("");
		aAmount.setValue("");
		txtcolor.setValue("");
		txtstdWeight.setValue("");
		
	}

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				cmbProductionType.focus();
			}
		});

		button.btnSave.addListener(new ClickListener()
		{

			public void buttonClick(ClickEvent event)
			{
				if(cmbProductionType.getValue()!=null)
				{
					if(cmbsemiFgName.getValue()!=null)
					{
						if(!aQty.getValue().toString().isEmpty())
						{
							if(!aRate.getValue().toString().isEmpty())
							{
								saveButtonEvent();	
							}
							else
							{
								showNotification("Invalid Entry","Please Enter Valid Rate",Notification.TYPE_WARNING_MESSAGE);
								aRate.focus();
							}
							
						}
						else
						{
							showNotification("Invalid Entry","Please Enter Valid Qty",Notification.TYPE_WARNING_MESSAGE);
							aQty.focus();
						}

					}
					else
					{
						showNotification("Invalid Entry","Please Select SemiFinished Goods Name",Notification.TYPE_WARNING_MESSAGE);
						cmbsemiFgName.focus();
					}
				}
				else
				{
					showNotification("Invalid Entry","Please Select Production Type",Notification.TYPE_WARNING_MESSAGE);
					cmbProductionType.focus();
				}
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
			}
		});

		button.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				findButtonEvent();
				
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbsemiFgName.getValue()!=null)
				{
					isUpdate=true;
					aQty.setEnabled(true);
					btnIni(false);
				}
				else
				{
					showNotification("There Is Nothing To Update",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbsemiFgName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbsemiFgName.getValue()!=null){
					//cmbFgNameData();
				}
			}
		});
		
		cmbProductionType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProductionType.getValue()!=null)
				{
					semifgDataLoad();
				}
			}
		});
		
		
		cmbsemiFgName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbsemiFgName.getValue()!=null)
				{
					semifgInformation();
				}
			}
		});
		aRate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!aRate.getValue().toString().isEmpty()){
					double qty=Double.parseDouble(aQty.getValue().toString().isEmpty()?"0.0":aQty.getValue().toString());
					double rate=Double.parseDouble(aRate.getValue().toString().isEmpty()?"0.0":aRate.getValue().toString());
					double amount=qty*rate;
					aAmount.setValue(decFormat1.format(amount));
				}
			}
		});
		aQty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!aQty.getValue().toString().isEmpty()){
					double qty=Double.parseDouble(aQty.getValue().toString().isEmpty()?"0.0":aQty.getValue().toString());
					double rate=Double.parseDouble(aRate.getValue().toString().isEmpty()?"0.0":aRate.getValue().toString());
					double amount=qty*rate;
					aAmount.setValue(decFormat1.format(amount));
				}
			}
		});
	}
	private void findButtonEvent(){
		Window win = new SemiFgOpeningFindWindow(sessionBean, txtItemID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtItemID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtItemID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}
	private void findInitialise(String id){
		String sql="select openingYear,productionTypeId,SemiFgCode,unit,color,stdWeight," +
				"qty,rate,amount from tbSemiFgOpening where semifgCode='"+id+"'";
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		Iterator iter=session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			//dOpeningYear.setValue(element[0]);
			cmbProductionType.setValue(element[1]);
			cmbsemiFgName.setValue(element[2]);
			txtUnit.setValue(element[3]);
			txtcolor.setValue(element[4]);
			txtstdWeight.setValue(decFormat1.format(element[5]));
			aQty.setValue(decFormat1.format(element[6]));
			aRate.setValue(decFormat1.format(element[7]));
			aAmount.setValue(decFormat1.format(element[8]));
			tempOpAmount=Double.parseDouble(element[8].toString());
		}
	}
	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();

		isFind = false;
		isUpdate = false;
	}

	private void refreshButtonEvent()
	{
		isUpdate=false;
		isFind = false;
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void focusEnter()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();

		allComp.add(cmbProductionType);
		allComp.add(cmbsemiFgName);
		//allComp.add(cmbFgName);
		allComp.add(aQty);
		allComp.add(aRate);

		allComp.add(button.btnNew);
		allComp.add(button.btnSave);
		allComp.add(button.btnEdit);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnFind);
		allComp.add(button.btnExit);

		new FocusMoveByEnter(this,allComp);
	}

	public List dbService(String sql)
	{
		List list = null;
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			list=session.createSQLQuery(sql).list();
			return list;

		}
		catch(Exception exp){

		}
		return list;
	}

	private void cmbProductionTypeData() 
	{
		cmbProductionType.removeAllItems();

		String sql = "";
		sql =   "select distinct productionTypeId,productionTypeName from  tbSemiFgInfo "
				+"where productionTypeId not like 'PT-3' order by productionTypeName";
		
		System.out.println("cmbProductionTypeData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element = (Object[]) iter.next();
			cmbProductionType.addItem(element[0].toString());
			cmbProductionType.setItemCaption(element[0].toString(), element[1].toString());
		}
	}

	private void cmbPartyData()
	{
		cmbsemiFgName.removeAllItems();

		String sql = "";
		sql = "select vGroupId,partyName from tbPartyInfo a" +
				" inner join" +
				" tbFinishedGoodsStandardInfo b" +
				" on b.partyCode = a.vGroupId" +
				" order by partyName ";

		System.out.println("cmbPartyData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			cmbsemiFgName.addItem(element[0].toString());
			cmbsemiFgName.setItemCaption(element[0].toString(), (String) element[1]);	
		}
	}

	private void semifgDataLoad()
	{
		cmbsemiFgName.removeAllItems();

		String sql = "";
		
		/*if(!isFind)
		{
			//sql = "select semiFgCode,semiFgName,color from tbSemiFgInfo where productionTypeId like '"+cmbProductionType.getValue()+"' " +
					//  " and semiFgCode not in (select SemifgCode from tbSemiFgOpening) and status='Active'   order by semiFgName ";
			
			sql="select distinct fGCode,semiFgName,color from (select fGCode,(select semiFgName from tbSemiFgInfo where "+
					" semiFgCode=a.fGCode)semiFgName,(select color from tbSemiFgInfo where  "+
					" semiFgCode=a.fGCode)color,isFg,status from tbFinishedGoodsStandardInfo a where slFlag= "+
					" (select MAX(slFlag) from tbFinishedGoodsStandardInfo where fGCode=a.fGCode) ) a  "+
					" where  isFg='NO' and status='Active' and fGCode not in(select SemifgCode from tbSemiFgOpening) order by semiFgName"	;
	
		}
		else
		{*/
			//sql = "select semiFgCode,semiFgName,color from tbSemiFgInfo where productionTypeId like '"+cmbProductionType.getValue()+"' " +
				  //" and status='Active' order by semiFgName ";
		
		sql=     "select distinct fGCode,semiFgName,color from (select fGCode,(select semiFgName from tbSemiFgInfo where " 
				 +"semiFgCode=a.fGCode)semiFgName,(select color from tbSemiFgInfo where "  
				 +"semiFgCode=a.fGCode)color,isFg,a.status from tbFinishedGoodsStandardInfo a inner join tbSemiFgInfo b on a.fGCode=b.semiFgCode where slFlag= " 
			     +"(select MAX(slFlag) from tbFinishedGoodsStandardInfo where fGCode=a.fGCode) and b.productionTypeId='"+cmbProductionType.getValue()+"' ) a " 
				 +"where  isFg='NO' and status='Active' order by semiFgName "	;
		
		/*	sql="select distinct fGCode,semiFgName,color from (select fGCode,(select semiFgName from tbSemiFgInfo where "+
				" semiFgCode=a.fGCode)semiFgName,(select color from tbSemiFgInfo where  "+
				" semiFgCode=a.fGCode)color,isFg,status from tbFinishedGoodsStandardInfo a where slFlag= "+
				" (select MAX(slFlag) from tbFinishedGoodsStandardInfo where fGCode=a.fGCode) ) a  "+
				" where  isFg='NO' and status='Active' order by semiFgName"	;*/
		//}
	
	
		System.out.println("cmbFgNameData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();
		cmbsemiFgName.removeAllItems();
		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			cmbsemiFgName.addItem(element[0].toString());
			cmbsemiFgName.setItemCaption(element[0].toString(), (String) element[1]+" # "+element[2]);	
		}
	}
	
	//semifgInformation
	
	private void semifgInformation()
	{
		

		String sql = "";
		sql = " select  unit,color,stdWeight from tbSemiFgInfo where semiFgCode like '"+cmbsemiFgName.getValue().toString()+"' ";
		txtUnit.setValue("");
		txtcolor.setValue("");
		txtstdWeight.setValue("");

		List list=dbService(sql);
		Iterator iter=list.iterator();

		if(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			txtUnit.setValue(element[0].toString());
			txtcolor.setValue(element[1].toString());
			txtstdWeight.setValue(element[2].toString());
		}
	}

	private boolean duplicateCheck()
	{
		String sql = "";
		//sql = "select * from tbFgOpening where fgCode like '"+cmbFgName.getValue()+"' and DATEPART(YEAR, openingYear) like '2016'";

		System.out.println("cmbFgNameData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		if(iter.hasNext())
		{			
			showNotification("Duplicate Entry","Opening Already Exists!!!",Notification.TYPE_WARNING_MESSAGE);
			//cmbFgName.setValue(null);
			return false;
		}
		else
		{
			return true;
		}
	}

	private void productData()
	{
		txtUnit.setValue("");

		String sql = "";
		//sql = "select vUnitName,0 from tbFinishedProductInfo where vProductId like '"+cmbFgName.getValue()+"'";

		System.out.println("productData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			txtUnit.setValue(element[0].toString());
		}
	}

	public void saveButtonEvent()
	{
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
						{
							insertData(session,tx);	
							tempOpAmount=0.0;
						}

						else
						{
							tx.rollback();
						}

						isUpdate=false;
						refreshButtonEvent();
						componentIni(true);
						btnIni(true);
					}
				}
			}));
		}
		else
		{
			this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
					new YesNoDialog.Callback() {

				public void onDialogResult(boolean yes) 
				{
					if(yes)
					{
						Transaction tx=null;
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						tx = session.beginTransaction();
						insertData(session,tx);

						isUpdate=false;
						refreshButtonEvent();
						componentIni(true);
						btnIni(true);
						tempOpAmount=0.0;
					}	
				}
			}));
		}	

	}

	private boolean deleteData(Session session,Transaction tx){
		try{
			System.out.println("Delete Start");
			String sql="delete from tbSemiFgOpening where SemifgCode like '"+cmbsemiFgName.getValue().toString()+"'";
			System.out.println("Delete End");
			session.createSQLQuery(sql).executeUpdate();
			
			
			return true;
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void insertData(Session session,Transaction tx) {

		try
		{
			String sql = "";

		/*	sql = 	"insert into tbFgOpening" +
					" (openingYear, productionType, partyCode, fgCode, openingQty,rate,amount, userIp, userId, entryTime)" +
					" values" +
					" ('"+ df.format(dOpeningYear.getValue())+"','"+cmbProductionType.getValue()+"','"+cmbsemiFgName.getValue()+"','','"+aQty.getValue()+"','"+aRate.getValue()+"','"+aAmount.getValue()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP)";
*/
			System.out.println("Ok");
			sql= "insert into tbSemiFgOpening (openingYear,productionTypeId,productionTypeName,ledgerId,semiFgCode,semiFgName, "+
				" unit,color,stdweight,qty,rate,amount,userIp,userId,entryTime) values ('2016','"+cmbProductionType.getValue()+"', "+
				" '"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"','','"+cmbsemiFgName.getValue()+"'," +
				"'"+cmbsemiFgName.getItemCaption(cmbsemiFgName.getValue())+"','"+txtUnit.getValue()+"','"+txtcolor.getValue()+"'," +
				"'"+txtstdWeight.getValue()+"','"+aQty.getValue()+"','"+aRate.getValue()+"','"+aAmount.getValue()+"'," +
				"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP) ";

			System.out.println("insert info: "+sql);	
			session.createSQLQuery(sql).executeUpdate();
			
			if(isUpdate){
				String LedgerOpen=" update tbLedger_Op_Balance set  DrAmount=DrAmount-"+tempOpAmount+"+"+aAmount.getValue()+"," +
						"CrAmount='0.00' ,userId='"+sessionBean.getUserId()+"' ,userIp='"+sessionBean.getUserIp()+"'," +
						"entryTime=getdate() where Ledger_Id like 'AL1706' and Op_Year='2016' ";
				System.out.println("LedgerOpen : "+LedgerOpen);
				session.createSQLQuery(LedgerOpen).executeUpdate();
			}
			else{
				String LedgerOpen=" update tbLedger_Op_Balance set  DrAmount=DrAmount+"+aAmount.getValue()+",CrAmount='0.00' ,userId='"+sessionBean.getUserId()+"' ," +
						"userIp='"+sessionBean.getUserIp()+"',entryTime=getdate() where Ledger_Id like 'AL1706' " +
						"and Op_Year='2016' ";
				System.out.println("LedgerOpen : "+LedgerOpen);
				session.createSQLQuery(LedgerOpen).executeUpdate();
			}
			
			

			tx.commit();
			this.getParent().showNotification("All information save successfully.");
		}

		catch(Exception exp)
		{
			tx.rollback();
			showNotification("From Insert"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbOpeningYearData()
	{
		cmbOpeningYear.removeAllItems();

		String sql = "";
		sql = "select distinct DATEPART(YEAR, openingYear), 0 from tbsemifgOpeinng";

		System.out.println("cmbOpeningYearData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			cmbOpeningYear.addItem(element[0]);
		}
	}




	private void setFgData(String fg)
	{
		txtClear();
		String sql = "";
		 
		sql = "select  openingYear,productionType,SemifgCode,openingQty,rate from tbsemifgOpeinng "
			  +"where DATEPART(YEAR,openingYear)='"+cmbOpeningYear.getValue().toString()+"' and SemifgCode='"+fg+"' ";

		System.out.println("productData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			dOpeningYear.setValue(element[0]);
			cmbProductionType.setValue(element[1].toString());
			cmbsemiFgName.setValue(element[2]);
			//cmbFgName.setValue(element[3].toString());
			aQty.setValue(decFormat.format(element[3]));
			aRate.setValue(decFormat1.format(element[4]));
			
		}
	}

}
