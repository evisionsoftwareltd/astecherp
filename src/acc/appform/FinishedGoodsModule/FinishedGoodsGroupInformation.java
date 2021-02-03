package acc.appform.FinishedGoodsModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class FinishedGoodsGroupInformation extends Window {

	SessionBean sessionBean;
	AbsoluteLayout mainLayout;

	private Label lbldeclaratinDate, lblProductionType, lblPartyName, lblFgName, lblLine, 
	lblOpeningYearFind,lblparentcolor,lblsubColor;

	private ComboBox cmbProductionType, cmbPartyName, cmbFgName,cmbFindParty,cmbFindFG,cmbFindDate,cmbFindColor;
	PopupDateField dDeclaration;
	private DecimalFormat decFormat = new DecimalFormat("#0");
	private TextField txttransactionNo=new TextField();
	private CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "","Exit");

	private ArrayList<Label> tbsubSl = new ArrayList<Label>();
	private ArrayList<ComboBox> tbsubCmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbsublblunit = new ArrayList<TextRead>(1);
	private Table tableSubProduct= new Table();
	private TextRead txtjobNo=new TextRead();
	private SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	Panel panelSearch=new Panel();
	Label lblpanenSearch=new Label();


	ArrayList<ComboBox>cmbProduct=new ArrayList<ComboBox>();

	boolean isUpdate = false,isFind = false;


	public FinishedGoodsGroupInformation(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("Finished Goods Group:: "+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		setEventAction();
		cmbProductionTypeData();
		cmbPartyData();
		focusEnter();
	}
	private void focusEnter()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();

		allComp.add(cmbProductionType);
		allComp.add(cmbPartyName);
		allComp.add(cmbFgName);
		allComp.add(dDeclaration);


		for(int i=0;i<tbsubCmbProduct.size();i++)
		{
			allComp.add(tbsubCmbProduct.get(i));

		}

		allComp.add(button.btnNew);
		allComp.add(button.btnSave);
		allComp.add(button.btnEdit);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnFind);
		allComp.add(button.btnExit);

		new FocusMoveByEnter(this,allComp);
	}

	private void setEventAction() {
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				cmbProductionType.focus();
			}
		});


		cmbFgName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbFgName.getValue()!=null)
				{
					for(int i=0;i<tbsubCmbProduct.size();i++)
					{
						subproductDataLoad(i);	
					}

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

		button.btnSave.addListener(new ClickListener()
		{

			public void buttonClick(ClickEvent event)
			{
				if(cmbProductionType.getValue()!=null)
				{
					if(cmbPartyName.getValue()!=null)
					{
						if(cmbFgName.getValue()!=null)
						{
							if(tbsubCmbProduct.get(0).getValue()!=null)
							{
								saveButtonEvent();	
							}
							else
							{
								showNotification("Invalid Entry","Please Select Sub Product Namer",Notification.TYPE_WARNING_MESSAGE);
								tbsubCmbProduct.get(0).focus();
							}
						}
						else
						{
							showNotification("Invalid Entry","Please Select Finished Good",Notification.TYPE_WARNING_MESSAGE);
							cmbFgName.focus();
						}
					}
					else
					{
						showNotification("Invalid Entry","Please Select Party Name",Notification.TYPE_WARNING_MESSAGE);
						cmbPartyName.focus();
					}
				}
				else
				{
					showNotification("Invalid Entry","Please Select Production Type",Notification.TYPE_WARNING_MESSAGE);
					cmbProductionType.focus();
				}
			}
		});

		button.btnFind.addListener(new ClickListener() 
		{

			public void buttonClick(ClickEvent event) {
				findButtonEvent();
				cmbFinishedPartyInfo();
				autoJobNo(); 
			}

		});

		cmbPartyName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbPartyName.getValue()!=null){
					cmbFgNameData();
				}
			}
		});

		cmbFindParty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindParty.getValue()!=null){
					cmbFindFgDataLoad();
				}
				else{
					cmbFindFG.removeAllItems();
				}
			}
		});

		cmbFindFG.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event)
			{
				if(cmbFindFG.getValue()!=null && cmbFindParty.getValue()!=null ){
					txtClear();
					FindDateLoad();
				}
				else{
					txtClear();
				}
			}
		});

		cmbFindDate.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindDate.getValue()!=null){
					txtClear();
					itmeLoadToCmb();
				}
				else{
					txtClear();
				}
			}
		});

		button.btnEdit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(sessionBean.isUpdateable())
				{
					updateButtonEvent();

				}
				else
				{
					showNotification("Warning,","You are not permitted to edit data.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

	}


	private void updateButtonEvent() {
		if (cmbFgName.getValue() != null&& cmbPartyName.getValue()!=null  && tbsubCmbProduct.get(0).getValue()!=null ) 
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
			panelSearch.setEnabled(false);
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}


	private void itmeLoadToCmb() 
	{
		int raw=0,pack=0,ink=0,i=0;
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql=  "select a.productionType,a.partyId,a.fgId,a.declarationDate,b.subProductId from tbfgGroupInfo a inner join tbFgGroupdetails b "
					+"on a.jobNo=b.jobNo  where a.partyId like '"+cmbFindParty.getValue().toString()+"' and a.fgId like '"+cmbFindFG.getValue().toString()+"'  "
					+"and a.declarationDate =(select MAX(declarationDate) from tbfgGroupInfo  where partyId like '"+cmbFindParty.getValue().toString()+"' and fgId like '"+cmbFindFG.getValue().toString()+"'  "
					+"and CONVERT(date,declarationDate,105) like '"+cmbFindDate.getValue().toString()+"') ";

			System.out.println("Sql Is"+sql);

			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				if(i==0)
				{

					//txttransactionNo.setValue(element[0].toString());
					cmbProductionType.setValue(element[0].toString());
					cmbPartyName.setValue(element[1].toString());
					cmbFgName.setValue(element[2].toString());
					dDeclaration.setValue(element[3]);
				}

				tbsubCmbProduct.get(i).setValue(element[4]);

				i++;
			}


			System.out.println("OK");

		}
		catch(Exception exp){
			showNotification("cmbDelareDateLoad: "+exp);
		}
	}


	private void FindDateLoad() {
		cmbFindDate.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select 0,CONVERT(date,declarationDate,105) as date from tbFgGroupInfo where partyId='"+cmbFindParty.getValue()+"' and fgId='"+cmbFindFG.getValue()+"' ";
			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element=(Object[]) iter.next();
				cmbFindDate.addItem(element[1]);

			}
		}
		catch(Exception exp){
			showNotification("cmbDelareDateLoad: "+exp);
		}
	}

	private void cmbFindFgDataLoad() 
	{
		cmbFindFG.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select vProductId,vProductName from tbFinishedProductInfo where vProductId in" +
					"(select fgId from tbFgGroupInfo where partyId like '"+cmbFindParty.getValue()+"')";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindFG.addItem(element[0]);
				cmbFindFG.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("cmbFindFgDataLoad: "+exp);
		}
	}

	private void findButtonEvent()
	{
		componentIni(true);
		panelSearch.setEnabled(true);
		isFind=true;
	}

	private void cmbFinishedPartyInfo() 
	{
		cmbFindParty.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct vGroupId,partyName from tbPartyInfo where vGroupId in (select partyId from tbFgGroupInfo)";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindParty.addItem(element[0]);
				cmbFindParty.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
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
						insertData(session,tx);
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
						isFind = false;
						componentIni(true);
						btnIni(true);
						txtClear();
					}	
				}
			}));
		}	

	}

	private void insertData(Session session,Transaction tx) {

		try
		{
			String sql = "";
			String query="";


			sql=  "insert into tbFgGroupInfo (jobNo,productionType,partyId,fgId,declarationDate,userIp,userId,entryTime) " 
					+" values('"+txttransactionNo.getValue().toString()+"','"+cmbProductionType.getValue().toString()+"','"+cmbPartyName.getValue().toString()+"', " +
					"'"+cmbFgName.getValue().toString()+"','"+dateformat1.format(dDeclaration.getValue())+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',getdate() )"; 



			System.out.println("insert info: "+sql);	

			session.createSQLQuery(sql).executeUpdate();

			for(int i=0;i<tbsubCmbProduct.size();i++)
			{
				if(tbsubCmbProduct.get(i).getValue()!=null)
				{
					query= "insert into tbFgGroupDetails (jobNo,subProductId,subUnit) "
							+"values('"+txttransactionNo.getValue()+"','"+tbsubCmbProduct.get(i).getValue().toString()+"','"+tbsublblunit.get(i).getValue()+"') ";

					session.createSQLQuery(query).executeUpdate();
				}
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

	private void cmbFgNameData()
	{
		cmbFgName.removeAllItems();

		String sql = "";
		sql = "select a.fGCode, b.vProductName from tbFinishedGoodsStandardInfo a" +
				"	left join" +
				"	(select vProductId, vProductName from tbFinishedProductInfo) b" +
				"	on b.vProductId = a.fGCode" +
				"	where a.partyCode like '"+cmbPartyName.getValue()+"'";

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

	private void subproductDataLoad(int position)
	{
		tbsubCmbProduct.get(position).removeAllItems();

		String sql = "";
		sql = sql ="select distinct vProductId,vProductName from tbFinishedProductInfo where vProductId not in ('"+cmbFgName.getValue().toString()+"') ";

		System.out.println("cmb Product Name : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			tbsubCmbProduct.get(position).addItem(element[0].toString());
			tbsubCmbProduct.get(position).setItemCaption(element[0].toString(), (String) element[1]);	
		}
	}


	private void newButtonEvent() 
	{
		componentIni(false);
		txtClear();
		btnIni(false);
		isFind = false;
		isUpdate = false;
		cmbProductionType.focus();
		autoJobNo(); 
	}

	private void refreshButtonEvent()
	{
		isUpdate=false;
		isFind = false;
		componentIni(true);
		btnIni(true);
		txtClear();

	}

	private void txtClear()
	{
		cmbProductionType.setValue(null);
		cmbPartyName.setValue(null);
		cmbFgName.setValue(null);
		dDeclaration.setValue(new java.util.Date());

		for(int i=0;i<tbsubCmbProduct.size();i++)
		{
			tbsubCmbProduct.get(i).removeAllItems();
			tbsubCmbProduct.get(i).setValue(null);
			tbsublblunit.get(i).setValue("");
		}
	}

	private void autoJobNo() 
	{
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql=" select ISNULL(MAX(jobNo),0)+1  from tbFgGroupInfo";
			List list=session.createSQLQuery(sql).list();
			Iterator iter=list.iterator();
			if(iter.hasNext())
			{
				txttransactionNo.setValue(iter.next());	
			}	
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
	}

	public List dbService(String sql)
	{
		List list = null;
		Transaction tx = null;
		Session session=null;
		try
		{
			session = SessionFactoryUtil.getInstance().openSession();
			tx = session.beginTransaction();
			list=session.createSQLQuery(sql).list();
			return list;

		}
		catch(Exception exp){
			tx.rollback();
		}
		finally{
			if(tx!=null||session!=null){
				tx.commit();
				session.close();
			}
		}
		return list;
	}
	private void cmbProductionTypeData() 
	{
		cmbProductionType.removeAllItems();

		String sql = "";
		sql = "select  productTypeId,productTypeName from tbProductionType";
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
		cmbPartyName.removeAllItems();

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
			cmbPartyName.addItem(element[0].toString());
			cmbPartyName.setItemCaption(element[0].toString(), (String) element[1]);	
		}
	}
	private AbsoluteLayout buildMainLayout(){
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("1000px");
		setHeight("580px");

		// lblProductionType
		lblProductionType = new Label("Production Type :");
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");

		// cmbProductionType
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("180px");
		cmbProductionType.setHeight("-1px");

		// lblPartyName
		lblPartyName = new Label("Party Name :");
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");

		// cmbPartyName
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("280px");
		cmbPartyName.setHeight("-1px");

		// lblFgName
		lblFgName = new Label("Finished Good :");
		lblFgName.setImmediate(false);
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");

		// cmbFgName
		cmbFgName = new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setWidth("280px");
		cmbFgName.setHeight("-1px");


		// lblOpeningYear
		lbldeclaratinDate = new Label("Declaration Date:");
		lbldeclaratinDate.setImmediate(false);
		lbldeclaratinDate.setWidth("-1px");
		lbldeclaratinDate.setHeight("-1px");


		dDeclaration=new PopupDateField();
		dDeclaration.setResolution(PopupDateField.RESOLUTION_DAY);
		dDeclaration.setValue(new java.util.Date());
		dDeclaration.setWidth("110px");
		dDeclaration.setDateFormat("dd-MM-yyyy");

		lblsubColor = new Label("<font color=blue size=+.5> SUB PRODUCT  :</font>",Label.CONTENT_XHTML);
		lblsubColor.setImmediate(false);
		lblsubColor.setWidth("-1px");
		lblsubColor.setHeight("-1px");

		tableSubProduct.setSelectable(true);
		tableSubProduct.setWidth("400px");
		tableSubProduct.setHeight("270px");
		tableSubProduct.setColumnReorderingAllowed(true);
		tableSubProduct.setColumnCollapsingAllowed(true);
		tableSubProduct.setFooterVisible(true);

		tableSubProduct.addContainerProperty("SL #", Label.class, new Label());
		tableSubProduct.setColumnWidth("SL #",20);

		tableSubProduct.addContainerProperty("PRODUCT", ComboBox.class, new ComboBox());
		tableSubProduct.setColumnWidth("PRODUCT",260);

		tableSubProduct.addContainerProperty("UNIT",TextRead.class, new TextRead(1));
		tableSubProduct.setColumnWidth("UNIT",58);

		tableSubProductInitioalise();

		lblpanenSearch= new Label(" <font color='##0000FF' size='4px'><b><Strong>Search :<Strong></b></font>");
		lblpanenSearch.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblpanenSearch,"top:220px;left:520px;");


		cmbFindParty=new ComboBox("Party Name");
		cmbFindParty.setImmediate(true);
		cmbFindParty.setWidth("180px");
		cmbFindParty.setHeight("24px");
		cmbFindParty.setNullSelectionAllowed(true);


		cmbFindDate=new ComboBox("Date");
		cmbFindDate.setImmediate(true);
		cmbFindDate.setWidth("107px");
		cmbFindDate.setHeight("24px");
		cmbFindDate.setNullSelectionAllowed(true);


		cmbFindFG=new ComboBox("Finished Goods");
		cmbFindFG.setImmediate(true);
		cmbFindFG.setWidth("200px");
		cmbFindFG.setHeight("24px");
		cmbFindFG.setNullSelectionAllowed(true);



		panelSearch=new Panel();
		panelSearch.setWidth("400px");
		panelSearch.setHeight("180px");
		panelSearch.setEnabled(false);
		panelSearch.setStyleName("panelSearch");
		mainLayout.addComponent(panelSearch,"top:250px;left:520px;");

		FormLayout frmLayout=new FormLayout();
		frmLayout.setSpacing(true);
		frmLayout.setMargin(true);
		frmLayout.addComponent(cmbFindParty);
		frmLayout.addComponent(cmbFindFG);
		frmLayout.addComponent(cmbFindDate);

		cmbFindDate.setImmediate(true);
		panelSearch.addComponent(frmLayout);




		mainLayout.addComponent(lblProductionType,"top:20px;left:20px;");
		mainLayout.addComponent(cmbProductionType,"top:18px;left:120px;");

		mainLayout.addComponent(lblPartyName,"top:50px;left:20px;");
		mainLayout.addComponent(cmbPartyName,"top:48px;left:120px;");

		mainLayout.addComponent(lblFgName,"top:80px;left:20px;");
		mainLayout.addComponent(cmbFgName,"top:78px;left:120px;");

		mainLayout.addComponent(lbldeclaratinDate,"top:110px;left:20px;");
		mainLayout.addComponent(dDeclaration,"top:108px;left:120px;");

		mainLayout.addComponent(lblsubColor,"top:145px;left:80px;");
		mainLayout.addComponent(tableSubProduct,"top:170px;left:80px;");



		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:465.0px;left:0.0px;");

		mainLayout.addComponent(button, "top:495.0px;left:30.0px;");



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

	public void tableSubProductInitioalise()
	{
		for(int i=0;i<15;i++)
		{
			tableRowAddSubProduct(i);
		}
	}

	public void tableRowAddSubProduct(final int ar)
	{

		tbsubSl.add(ar, new Label());
		tbsubSl.get(ar).setWidth("100%");
		tbsubSl.get(ar).setImmediate(true);
		tbsubSl.get(ar).setHeight("23px");
		tbsubSl.get(ar).setValue(ar+1);


		tbsubCmbProduct.add(ar, new ComboBox());
		tbsubCmbProduct.get(ar).setWidth("100%");
		tbsubCmbProduct.get(ar).setImmediate(true);
		tbsubCmbProduct.get(ar).setHeight("23px");

		tbsubCmbProduct.get(ar).addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(tbsubCmbProduct.get(ar).getValue()!=null)
				{
					if (doubleEntryChecksub(ar))
					{
						String sql = "";
						sql = "select vUnitName from tbFinishedProductInfo where vProductId like '"+tbsubCmbProduct.get(ar).getValue().toString()+"'  ";
						List list=dbService(sql);
						Iterator iter=list.iterator();
						if(iter.hasNext())
						{
							tbsublblunit.get(ar).setValue(iter.next());

						}
						if(ar==tbsubCmbProduct.size()-1)
						{
							tableRowAddSubProduct(ar+1);
							subproductDataLoad(ar+1);
						}

					}
					else
					{
						showNotification("Double Entry Is Not Allowed",Notification.TYPE_WARNING_MESSAGE);
						tbsubCmbProduct.get(ar).setValue(null);
					}
				}

			}
		});


		tbsublblunit.add(ar, new TextRead(1));
		tbsublblunit.get(ar).setWidth("100%");
		tbsublblunit.get(ar).setImmediate(true);
		tbsublblunit.get(ar).setHeight("23px");



		tableSubProduct.addItem(new Object[]{tbsubSl.get(ar),tbsubCmbProduct.get(ar),tbsublblunit.get(ar) },ar);
	}

	private boolean doubleEntryChecksub( int ar)
	{
		String captyion=tbsubCmbProduct.get(ar).getItemCaption(tbsubCmbProduct.get(ar).getValue().toString());

		for(int i=0;i<tbsubCmbProduct.size();i++)
		{
			if(tbsubCmbProduct.get(i).getValue()!=null)
			{
				if(ar!=i && captyion.equals(tbsubCmbProduct.get(i).getItemCaption(tbsubCmbProduct.get(i).getValue().toString())))
				{
					return false;	
				}
			}
		}


		return true;

	}


	private void componentIni(boolean b) 
	{

		dDeclaration.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		cmbPartyName.setEnabled(!b);
		cmbFgName.setEnabled(!b);
		tableSubProduct.setEnabled(!b);
	}
}
