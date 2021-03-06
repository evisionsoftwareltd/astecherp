package acc.appform.FinishedGoodsModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.FinishedGoodsModule.FGOpeningFindWindow;
import acc.appform.FinishedGoodsModule.SemiFgSubFindWindow;

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

public class MasterProductOpeningStock extends Window{

	SessionBean sessionBean;
	AbsoluteLayout mainLayout;

	private Label
	lblOpeningYear, lblProductionType, lblPartyName,lblFgName, lblUnit, lblQty, lblLine, lblOpeningYearFind,lblRate,lblAmount;

	private ComboBox
	cmbSemiFg, cmbPartyName, cmbFgName, cmbOpeningYear;

	private InlineDateField
	dOpeningYear;

	private TextRead
	txtUnit;

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
	private TextField txtItemID = new TextField();

	private CommonButton
	button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "","Exit");
	double tempOpAmount=0.0;

	public MasterProductOpeningStock(SessionBean sessionBean){
		this.sessionBean = sessionBean;
		this.setCaption("ASSEMBLE PRODUCT OPENING STOCK :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		btnIni(true);
		componentIni(true);
		txtClear();
		focusEnter();

		setEventAction();

		//cmbProductionTypeData();
		cmbPartyData();
		//cmbFgNameData();

		//tableInitialise();
		//componentIniFind(true);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("590px");
		setHeight("360px");

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

		/*// lblProductionType
		lblProductionType = new Label("Production Type :");
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");

		// cmbProductionType
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("-1px");
		cmbProductionType.setHeight("-1px");

		// lblPartyName
		lblPartyName = new Label("Party Name :");
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");

		// cmbPartyName
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("300px");
		cmbPartyName.setHeight("-1px");*/

		// lblFgName
		lblFgName = new Label("Product Name :");
		lblFgName.setImmediate(false);
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");

		// cmbFgName
		cmbFgName = new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbFgName.setWidth("400px");
		cmbFgName.setHeight("-1px");

		// lblFgName
		lblPartyName = new Label("Party Name :");
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");

		// cmbFgName
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbPartyName.setWidth("400px");
		cmbPartyName.setHeight("-1px");
		

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

		// lblOpeningYearFind
		lblOpeningYearFind = new Label("Opening Year :");
		lblOpeningYearFind.setImmediate(false);
		lblOpeningYearFind.setWidth("-1px");
		lblOpeningYearFind.setHeight("-1px");

		// cmbOpeningYear
		cmbOpeningYear = new ComboBox();
		cmbOpeningYear.setImmediate(true);
		cmbOpeningYear.setWidth("-1px");
		cmbOpeningYear.setHeight("-1px");

		

		// adding components to the mainLayout (component distance : 30px)
		mainLayout.addComponent(lblOpeningYear, "top: 20px; left: 20px;");
		mainLayout.addComponent(dOpeningYear, "top: 18px; left: 120px;");

		mainLayout.addComponent(lblPartyName, "top: 50px; left: 20px;");
		mainLayout.addComponent(cmbPartyName, "top: 48px; left: 120px;");

		/*mainLayout.addComponent(lblPartyName, "top: 80px; left: 20px;");
		mainLayout.addComponent(cmbPartyName, "top: 78px; left: 120px;");*/

		mainLayout.addComponent(lblFgName, "top: 80px; left: 20px;");
		mainLayout.addComponent(cmbFgName, "top: 78px; left: 120px;");

		mainLayout.addComponent(lblUnit, "top: 110px; left: 20px;");
		mainLayout.addComponent(txtUnit, "top: 108px; left: 120px;");

		mainLayout.addComponent(lblQty, "top: 140px; left: 20px;");
		mainLayout.addComponent(aQty, "top: 138px; left: 120px;");

		mainLayout.addComponent(lblRate, "top: 170px; left: 20px;");
		mainLayout.addComponent(aRate, "top: 168px; left: 120px;");

		mainLayout.addComponent(lblAmount, "top: 200px; left: 20px;");
		mainLayout.addComponent(aAmount, "top: 198px; left: 120px;");

		mainLayout.addComponent(lblLine, "top: 240px; left: 0px;");
		mainLayout.addComponent(button, "top: 270px; left: 20px;");


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
		cmbFgName.setEnabled(!b);
		cmbPartyName.setEnabled(!b);
		txtUnit.setEnabled(!b);
		aQty.setEnabled(!b);
		aRate.setEnabled(!b);
		aAmount.setEnabled(!b);
	}

	private void txtClear()
	{
		cmbPartyName.setValue(null);
		cmbFgName.setValue(null);
		txtUnit.setValue("");
		aQty.setValue("");
		aRate.setValue("");
		aAmount.setValue("");
	}

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				cmbPartyName.focus();
			}
		});

		button.btnSave.addListener(new ClickListener()
		{

			public void buttonClick(ClickEvent event)
			{

				if(cmbFgName.getValue()!=null)
				{
					if(!aQty.getValue().toString().isEmpty())
					{
						saveButtonEvent();
					}
					else
					{
						showNotification("Invalid Entry","Please Enter Valid Qty",Notification.TYPE_WARNING_MESSAGE);
						aQty.focus();
					}
				}
				else
				{
					showNotification("Invalid Entry","Please Select Finished Good",Notification.TYPE_WARNING_MESSAGE);
					cmbFgName.focus();
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
				//cmbOpeningYearData();
				isFind = true;
				//componentIniFind(false);
				findButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				/*if(UpdateCheck())
				{*/
						if(cmbFgName.getValue()!=null)
						{
							isUpdate=true;
							cmbPartyName.setEnabled(true);
							aQty.setEnabled(true);
							btnIni(false);
							//componentIniFind(true);
						}
						else
						{
							showNotification("There Is Nothing To Update",Notification.TYPE_WARNING_MESSAGE);
						}
				/*}
				else{
					showNotification("Already 2 Times Update",Notification.TYPE_WARNING_MESSAGE);
				}*/		
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		cmbFgName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbFgName.getValue()!=null){
					if(!isFind)
					{
						if(duplicateCheck())
						{
							//productData();
							txtUnit.setValue("Pcs");
							aQty.focus();
						}
					}
					else
					{
						//productData();
						txtUnit.setValue("Pcs");
						aQty.focus();
					}
				}
				else{
					txtUnit.setValue("");
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
		
		cmbPartyName.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbPartyName.getValue()!=null){
					cmbFgNameData();
				}
				else{
					cmbFgName.removeAllItems();
				}
			}
		});
	}
	
	private boolean UpdateCheck() {
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		
		try
		{
			String sql=" Select isnull(max(udSl)+1,1) as id from tbUdMasterProductOpening where fgCode = '"+cmbFgName.getValue().toString()+"' and udFlag = 'New'";
			Iterator iter = session.createSQLQuery(sql).list().iterator();
			int num = 0;

			if (iter.hasNext())
			{
				num = Integer.parseInt(iter.next().toString());
				if(num>=4){
					return false;
				}
			}
		}
		catch(Exception ex)
		{
			showNotification("Error1",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
		return true;
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();

		isFind = false;
		isUpdate = false;
		//componentIniFind(true);
	}

	private void refreshButtonEvent()
	{
		isUpdate=false;
		isFind = false;
		componentIni(true);
		btnIni(true);
		txtClear();
		//componentIniFind(true);
	}

	private void focusEnter()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();

		//allComp.add(cmbProductionType);
		allComp.add(cmbPartyName);
		allComp.add(cmbFgName);
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
	private void cmbPartyData()
	{
		cmbPartyName.removeAllItems();

		String sql = "";
		sql = "select vGroupId,partyName from tbPartyInfo where isActive = '1' order by autoId ";

		System.out.println("cmbPartyData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			cmbPartyName.addItem(element[0].toString());
			cmbPartyName.setItemCaption(element[0].toString(), (String) element[1]);	
		}
	}
	/*private void cmbFindFinishItemLoad(){
		cmbFgName.removeAllItems();
		String sql="select distinct vProductId,vProductName from tbFinishedProductInfo where vProductionTypeId like '"+cmbFindProductionType.getValue()+"' and vCategoryId like '"+cmbFindPartyName.getValue()+"'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element = (Object[]) iter.next();
			cmbFindFinishItemName.addItem(element[0].toString());
			cmbFindFinishItemName.setItemCaption(element[0].toString(), element[1].toString());
		}
	}*/
	private void cmbFgNameData()
	{
		cmbFgName.removeAllItems();

		String sql = "";
		sql =     "select distinct vProductId,vProductName from tbFinishedProductInfo a inner join tbFinishedProductDetailsNew b "
				  +"on a.vProductId=b.fgId where consumptionStage='Assemble' and vCategoryId='"+cmbPartyName.getValue()+"' ";

		System.out.println("cmbFgNameData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			cmbFgName.addItem(element[0].toString());
			cmbFgName.setItemCaption(element[0].toString(), (String) element[1]);	
		}
	}
	private void findButtonEvent(){
		Window win = new MasterProductOpeningFindWindow(sessionBean, txtItemID);
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
		String sql="select fgCode,openingQty,rate,amount,partyId from tbMasterProductOpening where fgCode='"+id+"'";
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		Iterator iter=session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbPartyName.setValue(element[4]);
			cmbFgName.setValue(element[0]);
			aQty.setValue(decFormat1.format(element[1]));
			aRate.setValue(decFormat1.format(element[2]));
			aAmount.setValue(decFormat1.format(element[3]));
			tempOpAmount=Double.parseDouble(element[3].toString());
		}
	}
	private boolean duplicateCheck()
	{
		String sql = "";
		sql = "select * from tbMasterProductOpening where fgCode like '"+cmbFgName.getValue()+"' and DATEPART(YEAR, openingYear) like '2017'";

		System.out.println("cmbFgNameData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		if(iter.hasNext())
		{			
			showNotification("Duplicate Entry","Opening Already Exists!!!",Notification.TYPE_WARNING_MESSAGE);
			cmbFgName.setValue(null);
			return false;
		}
		else
		{
			return true;
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
							double tempOpAmount=0.0;
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
						double tempOpAmount=0.0;
					}	
				}
			}));
		}	

	}

	private boolean deleteData(Session session,Transaction tx){
		try{
			String sqlUd = "insert into tbUdMasterProductOpening Select openingYear,partyId,partyName,"
					+ "fgCode,fgName,openingQty,rate,amount,userIp,userId,entryTime, 'Update',"
					+ "(Select max(udSl)+1 as id from tbUdMasterProductOpening where fgCode = '"+cmbFgName.getValue()+"' and udFlag = 'New'),userName"
							+ " from tbMasterProductOpening where fgCode like '"+cmbFgName.getValue()+"'";
			session.createSQLQuery(sqlUd).executeUpdate();
			
			session.createSQLQuery("delete from tbMasterProductOpening where fgCode like '"+cmbFgName.getValue()+"'").executeUpdate();
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
			String sqlUd = "insert into tbUdMasterProductOpening (openingYear,partyId,partyName,fgCode,fgName,openingQty,rate,amount,userIp,userId,entryTime,udFlag,udSl,userName)values "+
					" ('2017','"+cmbPartyName.getValue()+"','"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"','"+cmbFgName.getValue()+"'," +
					"'"+cmbFgName.getItemCaption(cmbFgName.getValue())+"','"+aQty.getValue()+"'," +
					"'"+aRate.getValue()+"',"+aAmount.getValue()+",'"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'New',(Select max(udSl)+1 as id from tbUdMasterProductOpening where fgCode = '"+cmbFgName.getValue()+"' and udFlag = 'New'),'"+sessionBean.getUserName()+"')";
			session.createSQLQuery(sqlUd).executeUpdate();
			
			
			String sql = "insert into tbMasterProductOpening (openingYear,partyId,partyName,fgCode,fgName,openingQty,rate,amount,userIp,userId,entryTime,userName)values "+
					" ('2017','"+cmbPartyName.getValue()+"','"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"','"+cmbFgName.getValue()+"'," +
					"'"+cmbFgName.getItemCaption(cmbFgName.getValue())+"','"+aQty.getValue()+"'," +
					"'"+aRate.getValue()+"',"+aAmount.getValue()+",'"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserName()+"')";
			session.createSQLQuery(sql).executeUpdate();

			/*if(isUpdate){
				String LedgerOpen=" update tbLedger_Op_Balance set  DrAmount=DrAmount-"+tempOpAmount+"+"+aAmount.getValue()+"," +
						"CrAmount='0.00' ,userId='"+sessionBean.getUserId()+"' ,userIp='"+sessionBean.getUserIp()+"'," +
						"entryTime=getdate() where Ledger_Id like 'AL1707' and Op_Year='2016' ";
				System.out.println("LedgerOpen : "+LedgerOpen);
				session.createSQLQuery(LedgerOpen).executeUpdate();
			}
			else{
				String LedgerOpen=" update tbLedger_Op_Balance set  DrAmount=DrAmount+"+aAmount.getValue()+",CrAmount='0.00' ,userId='"+sessionBean.getUserId()+"' ," +
						"userIp='"+sessionBean.getUserIp()+"',entryTime=getdate() where Ledger_Id like 'AL1707' " +
						"and Op_Year='2016' ";
				System.out.println("LedgerOpen : "+LedgerOpen);
				session.createSQLQuery(LedgerOpen).executeUpdate();
			}*/

			

			tx.commit();
			this.getParent().showNotification("All information save successfully.");
		}

		catch(Exception exp)
		{
			tx.rollback();
			showNotification("From Insert"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}


}
