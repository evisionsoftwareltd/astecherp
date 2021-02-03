package acc.appform.FinishedGoodsModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
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
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbSubGroup;

public  class ProductInformation extends Window 
{
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;

	private Label lblDate;
	private PopupDateField dDate;

	private Label lblFinishItemCode;
	private TextRead txtFinishItemCode;

	private Label lblFinishItemName;
	private TextField txtFinishItemName;

	private Label lblGroup;
	private ComboBox cmbGroup;	

	private Label lblProductionType;
	private ComboBox cmbProductionType;

	private Label lblProductType;
	private ComboBox cmbProductType;

	/*private Label lblSubGroup;
	private ComboBox cmbSubGroup;*/

	private Label lblUnit;
	private TextField txtUnit;

	private Label lblFinishItemRate;
	private AmountField txtFinishItemRate;

	private Label lblSetQty;
	private AmountField txtSetQty;

	// labels
	private Label lblDia;
	private Label lblLength;
	private Label lblLengthConsumption;
	private Label lblWeight;
	private Label lblCycleTime;
	private Label lblPerSqm;
	private Label lblPerPcs;
	private Label lblCavityNo;
	private Label lblEstimatedProduction;
	private Label lblActualProduction;
	private Label lblPcs;
	private Label lblWidthActual;
	private Label lblWidthConsumption;
	private Label lblProcessWastage;
	private Label lblPercent;
	private Label lblWeightConsumption;
	private Label lblPaperSize;
	private Label lblShoulderingWeight;

	private Label lblRmName;
	private Label lblHdpeAndMbName;

	// amountfields
	private AmountField afDia;
	private AmountField afLength;
	private AmountField afLengthConsumption;
	private AmountField afWeight;
	private AmountField afCycleTime;
	private AmountField afPerSqm;
	private AmountField afPerPcs;
	private AmountField afCavityNo;
	private AmountField afEstimatedProduction;
	private AmountField afActualProduction;
	private AmountField afWidthActual;
	private AmountField afWidthConsumption;
	private AmountField afProcessWastage;

	String Groupname;

	private ComboBox cmbPerSqm;
	private ComboBox cmbPerPcsUnit;

	private TextRead txtWeightConsumption;

	//private NativeButton nbGroup;
	//private NativeButton nbSubGroup;

	String FinishItemId="";
	private String findUpdateFinishItemId="";

	String LedgerId="";

	private TextRead ledgerCode = new TextRead();

	boolean isUpdate=false;
	int index;
	SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");

	private SimpleDateFormat dfYMD=new SimpleDateFormat("yyyy-MM-dd");

	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat df3 = new DecimalFormat("#0.000000");
	private DecimalFormat df1 = new DecimalFormat("#0");
	private DecimalFormat df2 = new DecimalFormat("#0.00000000");

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	/*private ArrayList<Label> tblblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> tbcmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbUnit = new ArrayList<TextRead>();
	private ArrayList<AmountField> tbPerSqm = new ArrayList<AmountField>();
	private ArrayList<TextRead> tbPerSqmUnit = new ArrayList<TextRead>(1);
	private ArrayList<AmountField> tbPerPcs = new ArrayList<AmountField>(1);
	private ArrayList<TextRead> tbPerPcsUnit = new ArrayList<TextRead>();*/
	Table tableRm=new Table();

	private ArrayList<Label> tbHdpelblSl1 = new ArrayList<Label>();
	private ArrayList<ComboBox> tbHdepecmbProduct1 = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbCmbType1 = new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbHdpeUnit1 = new ArrayList<TextRead>();
	private ArrayList<AmountField> tbHdepePerCent1 = new ArrayList<AmountField>();
	private ArrayList<TextRead>tbhdepMBqty1=new ArrayList<TextRead>(1);

	Table tableHdpeMb=new Table();

	private ArrayList<Label> tbHdpelblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> tbHdepecmbProduct = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbCmbType = new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbHdpeUnit = new ArrayList<TextRead>();
	private ArrayList<AmountField> tbHdepePerCent = new ArrayList<AmountField>();
	private ArrayList<TextRead>tbhdepMBqty=new ArrayList<TextRead>(1);


	HashMap<String, String> hmUnit=new HashMap<String, String>();


	public ProductInformation(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("FINISH GOODS INFORMATION :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		cmbGroupAddData();
		cmbProductionTypeData();
		setEventAction();
		focusEnter();
		authenticationCheck();
		//tableInitialize();
		tableHdpeMBInitialise();

	}
	private void tableHdpeMBInitialise()
	{
		for(int a=0;a<4;a++)
		{
			tableRowAddRM(a);
			tableRowAddHdpeAndMb(a);
		}
	}
	private void tableRowAddRM(final int ar){
		tbHdpelblSl1.add(ar,new Label());
		tbHdpelblSl1.get(ar).setWidth("20px");
		tbHdpelblSl1.get(ar).setValue(ar + 1);

		tbHdepecmbProduct1.add(ar,new ComboBox());
		tbHdepecmbProduct1.get(ar).setWidth("100%");
		tbHdepecmbProduct1.get(ar).setImmediate(true);
		tbHdepecmbProduct1.get(ar).setNullSelectionAllowed(true);
		tbHdepecmbProduct1.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		tbCmbType1.add(ar,new ComboBox());
		tbCmbType1.get(ar).setWidth("100%");
		tbCmbType1.get(ar).setImmediate(true);
		tbCmbType1.get(ar).setNullSelectionAllowed(true);
		tbCmbType1.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		tbCmbType1.get(ar).addItem("HDPE");
		tbCmbType1.get(ar).setItemCaption("HDPE", "BASE RM");
		tbCmbType1.get(ar).addItem("MB");

		tbCmbType1.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(tbCmbType1.get(ar).getValue()!=null){
					if(tbHdepecmbProduct1.get(ar).getValue()==null){
						showNotification("Select Raw Material First",Notification.TYPE_WARNING_MESSAGE);
						tbCmbType1.get(ar).setValue(null);
						tbHdepecmbProduct1.get(ar).focus();
					}
				}
			}
		});

		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query=null;

		try
		{
			query="select vRawItemCode,vRawItemName from tbRawItemInfo  where vCategoryType like 'Raw Material'";

			List lst=session.createSQLQuery(query).list();
			int a=lst.size();
			Iterator iter=lst.iterator();

			while(iter.hasNext())
			{
				Object[]element=(Object[]) iter.next();

				tbHdepecmbProduct1.get(ar).addItem(element[0].toString());
				tbHdepecmbProduct1.get(ar).setItemCaption(element[0].toString(), element[1].toString());
			}

		}

		catch(Exception ex)
		{
			System.out.println("Excep IS"+ex);  
		}


		tbHdepecmbProduct1.get(ar).addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event) {


				if(tbHdepecmbProduct1.get(ar).getValue()!=null&& cmbProductionType.getValue()!=null&&cmbProductType.getValue()!=null)
				{
					if(!afWeight.getValue().toString().isEmpty()){	
						if(!doubleEntryCheckHDPE1(tbHdepecmbProduct1.get(ar).getItemCaption(tbHdepecmbProduct1.get(ar).getValue()),ar))
						{
							Transaction tx=null;
							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();
							String sql="";
							sql=" select  0,vUnitName from tbRawItemInfo where vRawItemCode like '"+tbHdepecmbProduct1.get(ar).getValue().toString()+"' ";

							List lst=session.createSQLQuery(sql).list();
							Iterator iter=lst.iterator();

							if(!lst.isEmpty())
							{
								Object[] element=(Object[]) iter.next();
								tbHdpeUnit1.get(ar).setValue(element[1].toString());	
							}
							if(ar==tbHdepecmbProduct1.size()-1)
							{
								tableRowAddRM(ar+1);
							}
						}
						else{
							tbHdepecmbProduct1.get(ar).setValue(null);
							showNotification("Double Entry",Notification.TYPE_WARNING_MESSAGE);
							tbHdepecmbProduct1.get(ar).focus();
						}
					}
					else{
						tbHdepecmbProduct1.get(ar).setValue(null);
						showNotification("Please Provide Standard Weight",Notification.TYPE_WARNING_MESSAGE);
						afWeight.focus();
					}
				}
				else{
					if(cmbProductionType.getValue()==null){
						tbHdepecmbProduct1.get(ar).setValue(null);
						showNotification("Please Provide Production Type",Notification.TYPE_WARNING_MESSAGE);
						cmbProductionType.focus();
					}
					if(cmbProductType.getValue()==null){
						tbHdepecmbProduct1.get(ar).setValue(null);
						showNotification("Please Provide Product Type",Notification.TYPE_WARNING_MESSAGE);
						cmbProductType.focus();
					}
				}
			}
		});

		tbHdpeUnit1.add(ar,new TextRead(""));
		tbHdpeUnit1.get(ar).setWidth("100%");

		tbHdepePerCent1.add(ar,new AmountField());
		tbHdepePerCent1.get(ar).setWidth("100%");
		tbHdepePerCent1.get(ar).setImmediate(true);

		tbhdepMBqty1.add(ar,new TextRead(1));
		tbhdepMBqty1.get(ar).setWidth("100%");
		tbhdepMBqty1.get(ar).setImmediate(true);




		tbHdepePerCent1.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				
				if(!tbHdepePerCent1.get(ar).getValue().toString().isEmpty()){
					if(tbHdepecmbProduct1.get(ar).getValue()!=null){
						if(tbCmbType1.get(ar).getValue()!=null){
							if(cmbProductType.getValue().toString().equalsIgnoreCase("Mono Layer"))
							{
								double stdWeight=Double.parseDouble(afWeight.getValue().toString().isEmpty()?"0.0":afWeight.getValue().toString());
								double percent=Double.parseDouble(tbHdepePerCent1.get(ar).getValue().toString().isEmpty()?"0.0":
									tbHdepePerCent1.get(ar).getValue().toString());
								double qty=((stdWeight*percent)/100)/1000;
								tbhdepMBqty1.get(ar).setValue(df3.format(qty));
							}
							else if(cmbProductType.getValue().toString().equalsIgnoreCase("Bi Layer"))
							{
								double stdWeight=Double.parseDouble(afWeight.getValue().toString().isEmpty()?"0.0":afWeight.getValue().toString());
								double biPercent=(stdWeight*80)/100;
								double percent=Double.parseDouble(tbHdepePerCent1.get(ar).getValue().toString().isEmpty()?"0.0":
									tbHdepePerCent1.get(ar).getValue().toString());
								double qty=((biPercent*percent)/100)/1000;
								tbhdepMBqty1.get(ar).setValue(df3.format(qty));
							}
						}
						else{
							showNotification("Please Provide Raw Material Type",Notification.TYPE_WARNING_MESSAGE);
							tbHdepePerCent1.get(ar).setValue("");
							tbCmbType1.get(ar).focus();
						}
					}
					else{
						showNotification("Please Provide Raw Material Name",Notification.TYPE_WARNING_MESSAGE);
						tbHdepePerCent1.get(ar).setValue("");
						tbHdepecmbProduct1.get(ar).focus();
					}
				}

			}
		});

		tableRm.addItem(new Object[]{tbHdpelblSl1.get(ar),tbHdepecmbProduct1.get(ar),tbCmbType1.get(ar),tbHdpeUnit1.get(ar),tbHdepePerCent1.get(ar),tbhdepMBqty1.get(ar)},ar);
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
	private boolean doubleEntryCheckHDPE(String caption,int row){

		for(int i=0;i<tbHdpelblSl.size();i++)
		{

			if(i!=row && caption.equals(tbHdepecmbProduct.get(i).getItemCaption(tbHdepecmbProduct.get(i).getValue())))
			{

				return true;
			}
		}
		return false;
	}
	private boolean doubleEntryCheckHDPE1(String caption,int row){

		for(int i=0;i<tbHdpelblSl1.size();i++)
		{

			if(i!=row && caption.equals(tbHdepecmbProduct1.get(i).getItemCaption(tbHdepecmbProduct1.get(i).getValue())))
			{

				return true;
			}
		}
		return false;
	}

	private void tableRowAddHdpeAndMb(final int ar)
	{



		tbHdpelblSl.add(ar,new Label());
		tbHdpelblSl.get(ar).setWidth("20px");
		tbHdpelblSl.get(ar).setValue(ar + 1);

		tbHdepecmbProduct.add(ar,new ComboBox());
		tbHdepecmbProduct.get(ar).setWidth("100%");
		tbHdepecmbProduct.get(ar).setImmediate(true);
		tbHdepecmbProduct.get(ar).setNullSelectionAllowed(true);
		tbHdepecmbProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		tbCmbType.add(ar,new ComboBox());
		tbCmbType.get(ar).setWidth("100%");
		tbCmbType.get(ar).setImmediate(true);
		tbCmbType.get(ar).setNullSelectionAllowed(true);
		tbCmbType.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		tbCmbType.get(ar).addItem("HDPE");
		tbCmbType.get(ar).addItem("MB");

		tbCmbType.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(tbCmbType.get(ar).getValue()!=null){
					if(tbHdepecmbProduct.get(ar).getValue()==null){
						showNotification("Select Raw Material First",Notification.TYPE_WARNING_MESSAGE);
						tbCmbType.get(ar).setValue(null);
						tbHdepecmbProduct.get(ar).focus();
					}
				}
			}
		});

		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query=null;

		try
		{
			query="select vRawItemCode,vRawItemName from tbRawItemInfo  where vGroupId  in('G174','G175','G176','G177','G178')";

			List lst=session.createSQLQuery(query).list();
			int a=lst.size();
			Iterator iter=lst.iterator();

			while(iter.hasNext())
			{
				Object[]element=(Object[]) iter.next();

				tbHdepecmbProduct.get(ar).addItem(element[0].toString());
				tbHdepecmbProduct.get(ar).setItemCaption(element[0].toString(), element[1].toString());


			}

		}

		catch(Exception ex)
		{
			System.out.println("Excep IS"+ex);  
		}


		tbHdepecmbProduct.get(ar).addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event) {


				if(tbHdepecmbProduct.get(ar).getValue()!=null&& cmbProductionType.getValue()!=null)
				{
					if(!afWeight.getValue().toString().isEmpty()){
						if(!doubleEntryCheckHDPE(tbHdepecmbProduct.get(ar).getItemCaption(tbHdepecmbProduct.get(ar).getValue()),ar))
						{
							Transaction tx=null;
							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();
							String sql="";
							sql=" select  0,vUnitName from tbRawItemInfo where vRawItemCode like '"+tbHdepecmbProduct.get(ar).getValue().toString()+"' ";

							List lst=session.createSQLQuery(sql).list();
							Iterator iter=lst.iterator();

							if(!lst.isEmpty())
							{
								Object[] element=(Object[]) iter.next();
								tbHdpeUnit.get(ar).setValue(element[1].toString());	
							}
							if(ar==tbHdepecmbProduct.size()-1)
							{
								tableRowAddHdpeAndMb(ar+1);
							}
						}
						else{
							tbHdepecmbProduct.get(ar).setValue(null);
							showNotification("Double Entry",Notification.TYPE_WARNING_MESSAGE);
							tbHdepecmbProduct.get(ar).focus();
						}
					}
					else{
						tbHdepecmbProduct.get(ar).setValue(null);
						showNotification("Please Provide Standard Weight First",Notification.TYPE_WARNING_MESSAGE);
						afWeight.focus();
					}
				}
				else{
					if(cmbProductionType.getValue()==null){
						tbHdepecmbProduct.get(ar).setValue(null);
						showNotification("Please Provide Production Type",Notification.TYPE_WARNING_MESSAGE);
						cmbProductionType.focus();
					}
					if(cmbProductType.getValue()==null){
						tbHdepecmbProduct.get(ar).setValue(null);
						showNotification("Please Provide Product Type",Notification.TYPE_WARNING_MESSAGE);
						cmbProductType.focus();
					}
				}
			}
		});

		tbHdpeUnit.add(ar,new TextRead(""));
		tbHdpeUnit.get(ar).setWidth("100%");

		tbHdepePerCent.add(ar,new AmountField());
		tbHdepePerCent.get(ar).setWidth("100%");
		tbHdepePerCent.get(ar).setImmediate(true);

		tbHdepePerCent.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!tbHdepePerCent.get(ar).getValue().toString().isEmpty()){
					if(tbHdepecmbProduct.get(ar).getValue()!=null){
						if(tbCmbType.get(ar).getValue()!=null){

							double stdWeight=Double.parseDouble(afWeight.getValue().toString().isEmpty()?"0.0":afWeight.getValue().toString());
							double biPercent=(stdWeight*20)/100;
							double percent=Double.parseDouble(tbHdepePerCent.get(ar).getValue().toString().isEmpty()?"0.0":
								tbHdepePerCent.get(ar).getValue().toString());
							double qty=((biPercent*percent)/100)/1000;
							tbhdepMBqty.get(ar).setValue(df3.format(qty));

						}
						else{
							showNotification("Please Provide Raw Material Type",Notification.TYPE_WARNING_MESSAGE);
							tbHdepePerCent.get(ar).setValue("");
							tbCmbType.get(ar).focus();
						}
					}
					else{
						showNotification("Please Provide Raw Material Name",Notification.TYPE_WARNING_MESSAGE);
						tbHdepePerCent.get(ar).setValue("");
						tbHdepecmbProduct.get(ar).focus();
					}
				}
			}
		});

		tbhdepMBqty.add(ar,new TextRead(1));
		tbhdepMBqty.get(ar).setWidth("100%");
		tbhdepMBqty.get(ar).setImmediate(true);

		tableHdpeMb.addItem(new Object[]{tbHdpelblSl.get(ar),tbHdepecmbProduct.get(ar),tbCmbType.get(ar),tbHdpeUnit.get(ar),tbHdepePerCent.get(ar),tbhdepMBqty.get(ar)},ar);
	}






	private void cmbProductionTypeData() {
		cmbProductionType.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select productTypeId,productTypeName from tbProductionType order by autoId").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProductionType.addItem(element[0].toString());
				cmbProductionType.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
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
	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				newButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sessionBean.isUpdateable()){
					isUpdate = true;
					updateButtonEvent();
				}
				else{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				formValidation();
			}
		});

		button.btnExit.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}
		});

		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});
		/*cmbProductType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbProductType.getValue()!=null){
					tableClear();
					afWeight.setValue("");
					if(cmbProductType.getValue().toString().equalsIgnoreCase("Mono Layer")){
						lblRmName.setValue("<font color='blue' size='2px'><b>HDPE AND MB:</b></font>");
						lblRmName.setVisible(true);
						tableRm.setVisible(true);
						lblHdpeAndMbName.setVisible(false);
						tableHdpeMb.setVisible(false);
					}
					else if(cmbProductType.getValue().toString().equalsIgnoreCase("Bi Layer")){
						lblRmName.setValue("<font color='blue' size='2px'><b>HDPE AND MB INNER:</b></font>");
						lblRmName.setVisible(true);
						tableRm.setVisible(true);
						lblHdpeAndMbName.setVisible(true);
						tableHdpeMb.setVisible(true);
					}
				}
				else{
					lblRmName.setVisible(false);
					tableRm.setVisible(false);
					lblHdpeAndMbName.setVisible(false);
					tableHdpeMb.setVisible(false);
				}

			}
		});*/
		afWeight.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(!afWeight.getValue().toString().isEmpty()&&cmbProductType.getValue()!=null){
					if(cmbProductType.getValue().toString().equalsIgnoreCase("Bi Layer")){
						double d=Double.parseDouble(afWeight.getValue().toString());
						double inner=(d*80)/100;
						double outer=(d*20)/100;
						lblRmName.setValue("<font color='blue' size='2px'><b>HDPE AND MB INNER: "+inner+" gm</b></font>");
						lblHdpeAndMbName.setValue("<font color='blue' size='2px'><b>HDPE AND MB OUTER: "+outer+" gm</b></font>");
					}
				}
				else if(afWeight.getValue().toString().isEmpty()){
					lblRmName.setValue("<font color='blue' size='2px'><b>HDPE AND MB INNER: </b></font>");
					lblHdpeAndMbName.setValue("<font color='blue' size='2px'><b>HDPE AND MB OUTER: </b></font>");
				}
			}
		});
	}
	private String selectFinishItemCode()
	{
		String FinishItemCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " Select isnull(max(cast(SUBSTRING(vProductId,4,10) as int)),0)+1 from tbFinishedProductInfo";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext())
			{
				FinishItemCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return FinishItemCode;
	}

	public void cmbGroupAddData()
	{
		cmbGroup.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vGroupId,partyName from tbPartyInfo where isActive = '1' order by autoId").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbGroup.addItem(element[0].toString());
				cmbGroup.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void updateButtonEvent()
	{
		if(!txtFinishItemName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void formValidation()
	{
		if(sessionBean.isSubmitable())
		{
			if(!txtFinishItemCode.getValue().toString().isEmpty())
			{
				if(!txtFinishItemName.getValue().toString().isEmpty())
				{
					if(cmbGroup.getValue()!=null)
					{
						if(cmbProductionType.getValue()!=null){


							if(!txtUnit.getValue().toString().isEmpty())
							{
								if(!txtFinishItemRate.getValue().toString().isEmpty())
								{
									saveButtonEvent();
								}
								else
								{
									getParent().showNotification("Warning","Provide Unit Price",Notification.TYPE_WARNING_MESSAGE);
									txtFinishItemRate.focus();
								}
							}
							else
							{
								getParent().showNotification("Warning","Please provide unit",Notification.TYPE_WARNING_MESSAGE);
								txtUnit.focus();
							}
						}
						else{
							getParent().showNotification("Warning","Please select Production Type",Notification.TYPE_WARNING_MESSAGE);
							cmbProductionType.focus();
						}
					}
					else
					{
						getParent().showNotification("Warning","Please select Party Name",Notification.TYPE_WARNING_MESSAGE);
						cmbGroup.focus();
					}
				}
				else
				{
					getParent().showNotification("Warning","Please provide Item name",Notification.TYPE_WARNING_MESSAGE);
					txtFinishItemName.focus();
				}
			}
			else
			{
				getParent().showNotification("Warning","Please provide Item code",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			getParent().showNotification("Warning","Please provide Item code",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void findButtonEvent() 
	{
		Window win = new ProductFindWindow(sessionBean, txtItemID,"ItemId");
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

	private void findInitialise(String txtItemId) 
	{
		Transaction tx = null;
		String sql = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql = "  select vProductId,vProductName,vCategoryId,vSubCategoryId, vUnitName,isnull(mDealerPrice,0)as price,"+
					" vLedgerId,isnull(vSizeName,0) asd ,isnull(Dia,0) as dia,isnull(Length,0) as length,isnull(weight,0)as weight,isnull(cycleTime,0)as cycleTime,isnull(perSqmQty,0)as perSqmQty,perSqmUnit,"+
					" isnull(perPcsQty,0) as perPcsQty,perPcsUnit,isnull(cavityNo,0) as cavityNo,isnull(EstimatedDailyProduction,0)as estimate,isnull(ActualDailyProduction,0)as actualProd,isnull(LengthConsumption,0)as Lc,isnull(width,0) as width,isnull(widthConsumption,0)as widthC"+
					",vProductionTypeId,productType from tbFinishedProductInfo where vProductId = '"+txtItemId+"'  ";

			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext())
			{
				Object[] element = (Object[]) led.iterator().next();

				txtFinishItemCode.setValue(element[0]);
				txtFinishItemName.setValue(element[1]);

				cmbGroup.setValue(element[2].toString());

				txtUnit.setValue(element[4]);

				txtFinishItemRate.setValue(df3.format(element[5]));

				ledgerCode.setValue(element[6].toString());
				txtSetQty.setValue(element[7].toString());

				afDia.setValue(df1.format(element[8]));
				afLength.setValue(df1.format(element[9]));
				afCycleTime.setValue(df1.format(element[11]));
				afCavityNo.setValue(element[16]);
				afEstimatedProduction.setValue(df1.format(element[17]));
				afActualProduction.setValue(df1.format(element[18]));
				cmbProductionType.setValue(element[22]);
				cmbProductType.setValue(element[23]);
				afWeight.setValue(df.format(element[10]));
			}

			Session session1=null;
			Transaction tx1=null;
			session1=SessionFactoryUtil.getInstance().openSession();
			tx1=session1.beginTransaction();
			int a=0;
			List list=session.createSQLQuery("select rawItemCode,itemType,rawUnit,percentage,qty from tbFinishedProductDetails where fgId like '"+txtItemId+"' and flag not like 1 ").list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object element[]=(Object[])iter.next();
				tbHdepecmbProduct1.get(a).setValue(element[0]);
				tbCmbType1.get(a).setValue(element[1]);
				tbHdpeUnit1.get(a).setValue(element[2]);
				tbHdepePerCent1.get(a).setValue(element[3]);
				tbhdepMBqty1.get(a).setValue(df3.format(element[4]));
				a++;
			}

			if(cmbProductType.getValue().toString().equalsIgnoreCase("Bi Layer"))
			{
				Session session2=null;
				Transaction tx2=null;
				session1=SessionFactoryUtil.getInstance().openSession();
				tx1=session1.beginTransaction();
				int a1=0;
				List list1=session.createSQLQuery("select rawItemCode,itemType,rawUnit,percentage,qty from tbFinishedProductDetails where fgId like '"+txtItemId+"' and flag like 1 ").list();
				for(Iterator iter=list1.iterator();iter.hasNext();)
				{
					Object element[]=(Object[])iter.next();
					tbHdepecmbProduct.get(a1).setValue(element[0]);
					tbCmbType.get(a1).setValue(element[1]);
					tbHdpeUnit.get(a1).setValue(element[2]);
					tbHdepePerCent.get(a1).setValue(element[3]);
					tbhdepMBqty.get(a1).setValue(df3.format(element[4]));

					a1++;
				}	
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	public void subGroupLink()
	{
		Window win = new ItemSubCategory(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				showNotification("Warning","Select Group first");
			}

		});
		this.getParent().addWindow(win);
	}

	public void groupLink()
	{
		Window win = new ItemCategory(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbGroupAddData();
			}

		});
		this.getParent().addWindow(win);
	}

	private void saveButtonEvent()
	{
		if(!isUpdate)
		{
			FinishItemId = selectFinishItemCode();
			LedgerId="";
			System.out.println("AutoID: "+FinishItemId+" LedgerId: "+LedgerId);
		}
		else
		{
			FinishItemId = findUpdateFinishItemId;
			LedgerId = "";
		}

		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
						isUpdate = false;
						btnIni(true);
						componentIni(true);
						txtClear();
						button.btnNew.focus();
					}
				}
			});																	
		}
		else
		{									
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						isUpdate = false;
						btnIni(true);
						componentIni(true);
						txtClear();
					}
				}
			});
		}
	}

	public String ledgerId()
	{
		String ledgerId = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " Select cast(isnull(max(cast(replace(Ledger_Id, 'IL', '')as int))+1, 1)as varchar) from tbLedger" +
					" where Ledger_Id like 'IL%' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				ledgerId = "IL"+iter.next().toString();
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return ledgerId;
	}
	/*private boolean deleteData(String fgId){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			session.createSQLQuery("delete from tbFinishedProductDetails where fgid = '"+fgId+"'").executeUpdate();
			System.out.println("Hello");
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("From Delete: "+exp,Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{
			if(tx!=null&&session!=null){
				tx.commit();
				return true;
			}
		}
		return true;
	}*/
	private void insertData()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		String createForm = "";
		String parentId = "";

		LedgerId=ledgerId();

		createForm = "I1-"+cmbGroup.getValue().toString();

		try
		{
			String InsertFinishItem = " INSERT into tbFinishedProductInfo " +
					" (vProductId,vProductName,vProductionTypeId,vProductionTypeName,vCategoryId,vCategoryName," +
					" vUnitName,mDealerPrice,vLedgerId,dDate,UserId,UserIP,EntryTime," +
					" imageLocation,isActive,vSizeName,weight,cycleTime," +
					"cavityNo,EstimatedDailyProduction,ActualDailyProduction,productType) values (" +
					" '"+"FI-"+selectFinishItemCode()+"'," +
					" '"+txtFinishItemName.getValue().toString().trim()+"'," +
					" '"+cmbProductionType.getValue().toString()+"'," +
					" '"+cmbProductionType.getItemCaption(cmbProductionType.getValue().toString())+"'," +
					" '"+cmbGroup.getValue().toString()+"'," +
					" '"+cmbGroup.getItemCaption(cmbGroup.getValue().toString())+"'," +
					" '"+txtUnit.getValue().toString().trim()+"'," +
					" '"+txtFinishItemRate.getValue().toString()+"', " +
					" '"+LedgerId+"', " +
					" '"+dfYMD.format(dDate.getValue())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,0,'1'," +
					" '"+(txtSetQty.getValue().toString().isEmpty()?"":txtSetQty.getValue().toString())+"'," +
					"'"+afWeight.getValue()+"','"+afCycleTime.getValue()+"'," +
					"'"+afCavityNo.getValue()+"','"+afEstimatedProduction.getValue()+"','"+afActualProduction.getValue()+"','"+cmbProductType.getValue()+"') ";

			session.createSQLQuery(InsertFinishItem).executeUpdate();
			if(cmbProductType.getValue().toString().equalsIgnoreCase("Bi Layer")){
				for(int a=0;a<tbHdpelblSl.size();a++)
				{
					if(tbHdepecmbProduct.get(a).getValue()!=null&&!tbhdepMBqty.get(a).getValue().toString().isEmpty()){

						System.out.println("Mezbah "+tbhdepMBqty.get(a).getValue());
						String insertDetails="insert into tbFinishedProductDetails(fgId,rawItemCode,rawUnit,percentage,qty,flag,perPcs,itemType)values "+
								" ('"+txtFinishItemCode.getValue()+"','"+tbHdepecmbProduct.get(a).getValue()+"'," +
								"'"+tbHdpeUnit.get(a).getValue()+"','"+tbHdepePerCent.get(a).getValue()+"'," +
								"'"+tbhdepMBqty.get(a).getValue()+"',1,'"+tbhdepMBqty.get(a).getValue()+"','"+tbCmbType.get(a).getValue()+"')";
						session.createSQLQuery(insertDetails).executeUpdate();
					}
				}
			}
			
			for(int a=0;a<tbHdpelblSl1.size();a++)
			{
				if(tbHdepecmbProduct1.get(a).getValue()!=null&&!tbhdepMBqty1.get(a).getValue().toString().isEmpty()){

					System.out.println("Mezbah "+tbhdepMBqty.get(a).getValue());
					String insertDetails="insert into tbFinishedProductDetails(fgId,rawItemCode,rawUnit,percentage,qty,flag,perPcs,itemType)values "+
							" ('"+txtFinishItemCode.getValue()+"','"+tbHdepecmbProduct1.get(a).getValue()+"'," +
							"'"+tbHdpeUnit1.get(a).getValue()+"','"+tbHdepePerCent1.get(a).getValue()+"'," +
							"'"+tbhdepMBqty1.get(a).getValue()+"',0,'"+tbhdepMBqty1.get(a).getValue()+"','"+tbCmbType1.get(a).getValue()+"')";
					session.createSQLQuery(insertDetails).executeUpdate();
				}
			}



			String InsertUdFinishItem = " INSERT into tbudFinishedProductInfo " +
					" (vProductId,vProductName,vProductionTypeId,vProductionTypeName,vCategoryId,vCategoryName," +
					" vUnitName,mDealerPrice,vLedgerId,dDate,UserId,UserIP,EntryTime," +
					" imageLocation,isActive,vSizeName,weight,cycleTime," +
					"cavityNo,EstimatedDailyProduction,ActualDailyProduction,vFlag) values (" +
					" '"+"FI-"+selectFinishItemCode()+"'," +
					" '"+txtFinishItemName.getValue().toString().trim()+"'," +
					" '"+cmbProductionType.getValue().toString()+"'," +
					" '"+cmbProductionType.getItemCaption(cmbProductionType.getValue().toString())+"'," +
					" '"+cmbGroup.getValue().toString()+"'," +
					" '"+cmbGroup.getItemCaption(cmbGroup.getValue().toString())+"'," +
					" '"+txtUnit.getValue().toString().trim()+"'," +
					" '"+txtFinishItemRate.getValue().toString()+"', " +
					" '"+LedgerId+"', " +
					" '"+dfYMD.format(dDate.getValue())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,0,'1'," +
					" '"+(txtSetQty.getValue().toString().isEmpty()?"":txtSetQty.getValue().toString())+"'," +
					"'"+afWeight.getValue()+"','"+afCycleTime.getValue()+"'," +
					"'"+afCavityNo.getValue()+"','"+afEstimatedProduction.getValue()+"','"+afActualProduction.getValue()+"','New') ";

			session.createSQLQuery(InsertUdFinishItem).executeUpdate();

			parentId = cmbGroup.getValue().toString();

			String InsertLedger="INSERT into tbLedger values(" +
					" '"+LedgerId+"', " +
					" '"+txtFinishItemName.getValue().toString().trim()+"', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+parentId+"', " +
					" '"+createForm+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			session.createSQLQuery(InsertLedger).executeUpdate();

			String LedgerOpen="INSERT into tbLedger_Op_Balance values(" +
					" '"+LedgerId+"', " +
					" '0.00', " +
					" '0.00', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			session.createSQLQuery(LedgerOpen).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	private boolean deleteData(String fgId){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			session.createSQLQuery("delete from tbFinishedProductDetails where fgid = '"+fgId+"'").executeUpdate();
			System.out.println("Hello");
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("From Delete: "+exp,Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{
			if(tx!=null&&session!=null){
				tx.commit();
				return true;
			}
		}
		return true;
	}
	private boolean updateData()
	{
		String createForm = "";
		String subGroup = "";

		String parentId="";

		createForm = "I1-"+cmbGroup.getValue().toString();
		subGroup = "";

		System.out.println("AutoID: "+createForm);

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String updateFinishItem ="UPDATE tbFinishedProductInfo set" +
					" vProductName = '"+txtFinishItemName.getValue().toString().trim()+"' ," +
					" vProductionTypeId = '"+cmbProductionType.getValue()+"' ," +
					" vProductionTypeName = '"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"' ," +
					" vCategoryId = '"+cmbGroup.getValue().toString()+"' ," +
					" vCategoryName = '"+cmbGroup.getItemCaption(cmbGroup.getValue())+"' ," +
					" vSubCategoryId = '"+subGroup+"' ," +
					" vUnitName = '"+(txtUnit.getValue().toString().isEmpty()?"":txtUnit.getValue().toString())+"' ," +
					" mDealerPrice = '"+txtFinishItemRate.getValue().toString()+"' ," +
					" UserId = '"+sessionBean.getUserId()+"', " +
					" UserIp = '"+sessionBean.getUserIp()+"', " +
					" vSizeName = '"+(txtSetQty.getValue().toString().isEmpty()?"":txtSetQty.getValue().toString())+"', " +
					" EntryTime = CURRENT_TIMESTAMP " +
					",weight='"+afWeight.getValue()+"'"+
					",cycleTime='"+afCycleTime.getValue()+"'"+
					",cavityNo='"+afCavityNo.getValue()+"'"+
					",EstimatedDailyProduction='"+afEstimatedProduction.getValue()+"'"+
					",ActualDailyProduction='"+afActualProduction+"'"+
					" where vProductId='"+txtFinishItemCode.getValue().toString()+"'";

			System.out.println("UpdateFinishItem: "+updateFinishItem);
			session.createSQLQuery(updateFinishItem).executeUpdate();
			
			String id=txtFinishItemCode.getValue().toString();
			if(deleteData(id))
			{
				if(cmbProductType.getValue().toString().equalsIgnoreCase("Bi Layer")){
					for(int a=0;a<tbHdpelblSl.size();a++)
					{
						if(tbHdepecmbProduct.get(a).getValue()!=null&&!tbhdepMBqty.get(a).getValue().toString().isEmpty()){

							System.out.println("Mezbah "+tbhdepMBqty.get(a).getValue());
							String insertDetails="insert into tbFinishedProductDetails(fgId,rawItemCode,rawUnit,percentage,qty,flag,perPcs,itemType)values "+
									" ('"+txtFinishItemCode.getValue()+"','"+tbHdepecmbProduct.get(a).getValue()+"'," +
									"'"+tbHdpeUnit.get(a).getValue()+"','"+tbHdepePerCent.get(a).getValue()+"'," +
									"'"+tbhdepMBqty.get(a).getValue()+"',1,'"+tbhdepMBqty.get(a).getValue()+"','"+tbCmbType.get(a).getValue()+"')";
							session.createSQLQuery(insertDetails).executeUpdate();
						}
					}
				}
				
				for(int a=0;a<tbHdpelblSl1.size();a++)
				{
					if(tbHdepecmbProduct1.get(a).getValue()!=null&&!tbhdepMBqty1.get(a).getValue().toString().isEmpty()){

						System.out.println("Mezbah "+tbhdepMBqty.get(a).getValue());
						String insertDetails="insert into tbFinishedProductDetails(fgId,rawItemCode,rawUnit,percentage,qty,flag,perPcs,itemType)values "+
								" ('"+txtFinishItemCode.getValue()+"','"+tbHdepecmbProduct1.get(a).getValue()+"'," +
								"'"+tbHdpeUnit1.get(a).getValue()+"','"+tbHdepePerCent1.get(a).getValue()+"'," +
								"'"+tbhdepMBqty1.get(a).getValue()+"',0,'"+tbhdepMBqty1.get(a).getValue()+"','"+tbCmbType1.get(a).getValue()+"')";
						session.createSQLQuery(insertDetails).executeUpdate();
					}
				}
				
			}

			//String id=txtFinishItemCode.getValue().toString();
			parentId = cmbGroup.getValue().toString();

			String UpdateLedger = "UPDATE tbLedger set" +
					" Ledger_Name = '"+txtFinishItemName.getValue().toString().trim()+"', " +
					" Parent_Id = '"+parentId+"', " +
					" Create_From = '"+createForm+"', " +
					" userId = '"+sessionBean.getUserId()+"', " +
					" userIp = '"+sessionBean.getUserIp()+"', " +
					" entryTime = CURRENT_TIMESTAMP " +
					" where Ledger_Id='"+ledgerCode.getValue()+"'";

			System.out.println("UpdateLedger: "+UpdateLedger);
			session.createSQLQuery(UpdateLedger).executeUpdate();


			String InsertUdFinishItem = " INSERT into tbudFinishedProductInfo " +
					" (vProductId,vProductName,vProductionTypeId,vProductionTypeName,vCategoryId,vCategoryName," +
					" vUnitName,mDealerPrice,vLedgerId,dDate,UserId,UserIP,EntryTime," +
					" imageLocation,isActive,vSizeName,weight,cycleTime," +
					"cavityNo,EstimatedDailyProduction,ActualDailyProduction,vFlag) values (" +
					" '"+"FI-"+selectFinishItemCode()+"'," +
					" '"+txtFinishItemName.getValue().toString().trim()+"'," +
					" '"+cmbProductionType.getValue().toString()+"'," +
					" '"+cmbProductionType.getItemCaption(cmbProductionType.getValue().toString())+"'," +
					" '"+cmbGroup.getValue().toString()+"'," +
					" '"+cmbGroup.getItemCaption(cmbGroup.getValue().toString())+"'," +
					" '"+txtUnit.getValue().toString().trim()+"'," +
					" '"+txtFinishItemRate.getValue().toString()+"', " +
					" '"+LedgerId+"', " +
					" '"+dfYMD.format(dDate.getValue())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,0,'1'," +
					" '"+(txtSetQty.getValue().toString().isEmpty()?"":txtSetQty.getValue().toString())+"'," +
					"'"+afWeight.getValue()+"','"+afCycleTime.getValue()+"'," +
					"'"+afCavityNo.getValue()+"','"+afEstimatedProduction.getValue()+"','"+afActualProduction.getValue()+"','Update') ";

			session.createSQLQuery(InsertUdFinishItem).executeUpdate();

			this.getParent().showNotification("All information update successfully.");

			tx.commit();

			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		txtFinishItemCode.setValue("FI-"+selectFinishItemCode());
		cmbProductionType.focus();
	}

	private void focusEnter()
	{
		allComp.add(cmbProductionType);
		allComp.add(cmbProductType);
		allComp.add(cmbGroup);
		allComp.add(txtFinishItemName);
		allComp.add(txtUnit);
		allComp.add(txtFinishItemRate);
		allComp.add(txtSetQty);
		allComp.add(afDia);
		allComp.add(afLength);
		allComp.add(afLengthConsumption);
		allComp.add(afWidthActual);
		allComp.add(afWidthConsumption);
		allComp.add(afWeight);
		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public void txtClear()
	{
		tableClear();
		txtFinishItemCode.setValue("");
		txtFinishItemName.setValue("");
		cmbGroup.setValue(null);
		cmbProductionType.setValue(null);
		//cmbProductType.setValue(null);
		dDate.setValue(new Date());
		txtUnit.setValue("");
		txtSetQty.setValue("");
		txtFinishItemRate.setValue("");
		afDia.setValue("");
		afLength.setValue("");
		afLengthConsumption.setValue("");
		afWidthActual.setValue("");
		afWidthConsumption.setValue("");
		afWeight.setValue("");
		afProcessWastage.setValue("");
		afCycleTime.setValue("");
		txtWeightConsumption.setValue("");
		afCavityNo.setValue("");
		afEstimatedProduction.setValue("");
		afActualProduction.setValue("");
		tableClear();


	}
	private void tableClear(){
		for(int i=0;i<tbHdpelblSl.size();i++)
		{
			tbHdepecmbProduct.get(i).setValue(null);
			tbCmbType.get(i).setValue(null);
			tbHdpeUnit.get(i).setValue("");
			tbHdepePerCent.get(i).setValue("");
			tbhdepMBqty.get(i).setValue("");

		}
		for(int i=0;i<tbHdpelblSl1.size();i++)
		{
			tbHdepecmbProduct1.get(i).setValue(null);
			tbCmbType1.get(i).setValue(null);
			tbHdpeUnit1.get(i).setValue("");
			tbHdepePerCent1.get(i).setValue("");
			tbhdepMBqty1.get(i).setValue("");

		}
	}
	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnFind.setEnabled(t);		
	}

	private void componentIni(boolean b) 
	{
		txtFinishItemCode.setEnabled(!b);
		txtFinishItemName.setEnabled(!b);
		cmbGroup.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		cmbProductType.setEnabled(!b);
		txtUnit.setEnabled(!b);
		txtSetQty.setEnabled(!b);
		dDate.setEnabled(!b);
		txtFinishItemRate.setEnabled(!b);
		afDia.setEnabled(!b);
		afLength.setEnabled(!b);
		afLengthConsumption.setEnabled(!b);
		afWidthActual.setEnabled(!b);
		afWidthConsumption.setEnabled(!b);
		afWeight.setEnabled(!b);
		afProcessWastage.setEnabled(!b);
		afCycleTime.setEnabled(!b);
		txtWeightConsumption.setEnabled(!b);
		afCavityNo.setEnabled(!b);
		afEstimatedProduction.setEnabled(!b);
		afActualProduction.setEnabled(!b);
		tableRm.setEnabled(!b);
		tableHdpeMb.setEnabled(!b);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("760px");
		setHeight("575px");

		// lblFinishItemCode
		lblFinishItemCode = new Label("Product Code: ");
		lblFinishItemCode.setImmediate(true);
		lblFinishItemCode.setWidth("100.0%");
		lblFinishItemCode.setHeight("18px");


		// txtFinishItemCode
		txtFinishItemCode = new TextRead();
		txtFinishItemCode.setImmediate(false);
		txtFinishItemCode.setWidth("80px");
		txtFinishItemCode.setHeight("23px");

		// lblProductionType
		lblProductionType = new Label("Production Type: ");
		lblProductionType.setImmediate(true);
		lblProductionType.setWidth("100.0%");
		lblProductionType.setHeight("18px");

		// cmbProductionType
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setNullSelectionAllowed(true);

		// lblProductionType
		lblProductType = new Label("Product Type: ");
		lblProductType.setImmediate(true);
		lblProductType.setWidth("100.0%");
		lblProductType.setHeight("18px");
		lblProductType.setVisible(false);

		// cmbProductionType
		cmbProductType = new ComboBox();
		cmbProductType.setImmediate(true);
		cmbProductType.setWidth("200px");
		cmbProductType.setHeight("24px");
		cmbProductType.setNullSelectionAllowed(true);
		cmbProductType.addItem("Mono Layer");
		cmbProductType.addItem("Bi Layer");
		cmbProductType.setVisible(false);
		cmbProductType.setValue("Mono Layer");


		// lblGroup
		lblGroup = new Label("Party Name: ");
		lblGroup.setImmediate(false);
		lblGroup.setWidth("-1px");
		lblGroup.setHeight("-1px");


		// cmbCategory
		cmbGroup = new ComboBox();
		cmbGroup.setImmediate(true);
		cmbGroup.setWidth("318px");
		cmbGroup.setHeight("24px");
		cmbGroup.setNullSelectionAllowed(true);


		// lblFinishItemName
		lblFinishItemName = new Label("Product Name: ");
		lblFinishItemName.setImmediate(false);
		lblFinishItemName.setWidth("-1px");
		lblFinishItemName.setHeight("-1px");


		// txtFinishItemName
		txtFinishItemName = new TextField();
		txtFinishItemName.setImmediate(false);
		txtFinishItemName.setWidth("318px");
		txtFinishItemName.setHeight("-1px");
		txtFinishItemName.setSecret(false);



		// lblUnit
		lblUnit = new Label("Unit :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");


		// txtUnit
		txtUnit = new TextField();
		txtUnit.setImmediate(false);
		txtUnit.setWidth("100px");
		txtUnit.setHeight("-1px");
		txtUnit.setSecret(false);


		// lblFinishItemRate
		lblFinishItemRate = new Label("Unit Price :");
		lblFinishItemRate.setImmediate(false);
		lblFinishItemRate.setWidth("-1px");
		lblFinishItemRate.setHeight("-1px");


		// txtFinishItemRate
		txtFinishItemRate = new AmountField();
		txtFinishItemRate.setImmediate(true);
		txtFinishItemRate.setWidth("100px");
		txtFinishItemRate.setHeight("-1px");


		// lblSetQty
		lblSetQty = new Label("Packing Qty(PCS) :");
		lblSetQty.setImmediate(false);
		lblSetQty.setWidth("-1px");
		lblSetQty.setHeight("-1px");


		// txtFinishItemRate
		txtSetQty = new AmountField();
		txtSetQty.setImmediate(true);
		txtSetQty.setWidth("100px");
		txtSetQty.setHeight("-1px");




		// lblDia
		lblDia = new Label("Dia :");
		lblDia.setImmediate(false);
		lblDia.setWidth("-1px");
		lblDia.setHeight("-1px");

		// afDia
		afDia = new AmountField();
		afDia.setImmediate(true);
		afDia.setWidth("100px");
		afDia.setHeight("-1px");

		// lblLength
		lblLength = new Label("Length  <font color='#0000FF'><b>[Actual]</b></Font>  :",Label.CONTENT_XHTML);
		lblLength.setImmediate(false);
		lblLength.setWidth("-1px");
		lblLength.setHeight("-1px");

		// afLength
		afLength = new AmountField();
		afLength.setImmediate(true);
		afLength.setWidth("100px");
		afLength.setHeight("-1px");

		lblLengthConsumption = new Label("Length  <font color='#0000FF'><b>[Consumption]</b></Font>  :",Label.CONTENT_XHTML);
		lblLengthConsumption.setImmediate(false);
		lblLengthConsumption.setWidth("-1px");
		lblLengthConsumption.setHeight("-1px");

		// afLength
		afLengthConsumption = new AmountField();
		afLengthConsumption.setImmediate(true);
		afLengthConsumption.setWidth("100px");
		afLengthConsumption.setHeight("-1px");

		// lblLength
		lblWidthActual = new Label("Width  <font color='#0000FF'><b>[Actual]</b></Font>  :",Label.CONTENT_XHTML);
		lblWidthActual.setImmediate(false);
		lblWidthActual.setWidth("-1px");
		lblWidthActual.setHeight("-1px");

		// afLength
		afWidthActual = new AmountField();
		afWidthActual.setImmediate(true);
		afWidthActual.setWidth("100px");
		afWidthActual.setHeight("-1px");

		// lblLength
		lblWidthConsumption = new Label("Width  <font color='#0000FF'><b>[Consumption]</b></Font>  :",Label.CONTENT_XHTML);
		lblWidthConsumption.setImmediate(false);
		lblWidthConsumption.setWidth("-1px");
		lblWidthConsumption.setHeight("-1px");

		// lblPaperSize
		lblPaperSize = new Label("Paper Size :",Label.CONTENT_XHTML);
		lblPaperSize.setImmediate(false);
		lblPaperSize.setWidth("-1px");
		lblPaperSize.setHeight("-1px");

		// afLength
		afWidthConsumption = new AmountField();
		afWidthConsumption.setImmediate(true);
		afWidthConsumption.setWidth("100px");
		afWidthConsumption.setHeight("-1px");

		// lblWeight
		lblWeight = new Label("Std Weight :");
		lblWeight.setImmediate(false);
		lblWeight.setWidth("-1px");
		lblWeight.setHeight("-1px");

		// lblShoulderingWeight
		lblShoulderingWeight = new Label("Shouldering Weight :");
		lblShoulderingWeight.setImmediate(false);
		lblShoulderingWeight.setWidth("-1px");
		lblShoulderingWeight.setHeight("-1px");

		// afWeight
		afWeight = new AmountField();
		afWeight.setImmediate(true);
		afWeight.setWidth("100px");
		afWeight.setHeight("-1px");

		// lblProcessWastage
		lblProcessWastage = new Label("Process Wastage :");
		lblProcessWastage.setImmediate(false);
		lblProcessWastage.setWidth("-1px");
		lblProcessWastage.setHeight("-1px");

		// afProcessWastage
		afProcessWastage = new AmountField();
		afProcessWastage.setImmediate(true);
		afProcessWastage.setWidth("80px");
		afProcessWastage.setHeight("-1px");

		// lblPercent
		lblPercent = new Label("%");
		lblPercent.setImmediate(false);
		lblPercent.setWidth("-1px");
		lblPercent.setHeight("-1px");

		// lblCycleTime
		lblCycleTime = new Label("Cycle Time :");
		lblCycleTime.setImmediate(false);
		lblCycleTime.setWidth("-1px");
		lblCycleTime.setHeight("-1px");
		

		// afCycleTime
		afCycleTime = new AmountField();
		afCycleTime.setImmediate(true);
		afCycleTime.setWidth("100px");
		afCycleTime.setHeight("-1px");

		// lblWeightConsumption
		lblWeightConsumption = new Label("Weight Consumption :");
		lblWeightConsumption.setImmediate(false);
		lblWeightConsumption.setWidth("-1px");
		lblWeightConsumption.setHeight("-1px");

		// txtWeightConsumption
		txtWeightConsumption = new TextRead(1);
		txtWeightConsumption.setImmediate(true);
		txtWeightConsumption.setWidth("80px");
		txtWeightConsumption.setHeight("22px");

		// lblPerSqm
		lblPerSqm = new Label("Per Sqm/Kg :");
		lblPerSqm.setImmediate(false);
		lblPerSqm.setWidth("-1px");
		lblPerSqm.setHeight("-1px");

		// cmbPerSqm
		cmbPerSqm = new ComboBox();
		cmbPerSqm.setImmediate(true);
		cmbPerSqm.setWidth("100px");
		cmbPerSqm.setHeight("-1px");
		cmbPerSqm.setNullSelectionAllowed(true);
		cmbPerSqm.addItem("Sqm");
		cmbPerSqm.addItem("KG");

		// afPerSqm
		afPerSqm = new AmountField();
		afPerSqm.setImmediate(true);
		afPerSqm.setWidth("100px");
		afPerSqm.setHeight("-1px");

		lblPcs = new Label("Pcs");
		lblPcs.setImmediate(false);
		lblPcs.setWidth("-1px");
		lblPcs.setHeight("-1px");

		// lblPerPcs
		lblPerPcs = new Label("Per Pcs :");
		lblPerPcs.setImmediate(false);
		lblPerPcs.setWidth("-1px");
		lblPerPcs.setHeight("-1px");

		// cmbPerPcsUnit
		cmbPerPcsUnit = new ComboBox();
		cmbPerPcsUnit.setImmediate(true);
		cmbPerPcsUnit.setWidth("100px");
		cmbPerPcsUnit.setHeight("-1px");
		cmbPerPcsUnit.setNullSelectionAllowed(true);
		cmbPerPcsUnit.addItem("Sqm");
		cmbPerPcsUnit.addItem("KG");

		// afPerPcs
		afPerPcs = new AmountField();
		afPerPcs.setImmediate(true);
		afPerPcs.setWidth("100px");
		afPerPcs.setHeight("-1px");

		lblRmName = new Label("<font color='blue' size='2px'><b>HDPE AND MB INNER:</b></font>",Label.CONTENT_XHTML);
		lblRmName.setImmediate(false);
		lblRmName.setWidth("-1px");
		lblRmName.setHeight("-1px");

		lblHdpeAndMbName = new Label("<font color='blue' size='2px'><b>HDPE AND MB OUTER:</b></font>",Label.CONTENT_XHTML);
		lblHdpeAndMbName.setImmediate(false);
		lblHdpeAndMbName.setWidth("-1px");
		lblHdpeAndMbName.setHeight("-1px");



		tableRm.setWidth("570px");
		tableRm.setHeight("165px");
		tableRm.setFooterVisible(true);

		tableRm.addContainerProperty("SL", Label.class , new Label());
		tableRm.setColumnWidth("SL",15);

		tableRm.addContainerProperty("R/M NAME", ComboBox.class , new ComboBox());
		tableRm.setColumnWidth("R/M NAME",200);

		tableRm.addContainerProperty("R/M Type", ComboBox.class , new ComboBox());
		tableRm.setColumnWidth("R/M Type",100);

		tableRm.addContainerProperty("UNIT", TextRead.class , new TextRead(1));
		tableRm.setColumnWidth("UNIT",35);

		tableRm.addContainerProperty("Percent", AmountField.class , new AmountField());
		tableRm.setColumnWidth("Percent",45);

		tableRm.addContainerProperty("Qty" ,TextRead.class , new TextRead(1));
		tableRm.setColumnWidth("Qty",80);

		tableHdpeMb.setWidth("570px");
		tableHdpeMb.setHeight("165px");
		tableHdpeMb.setFooterVisible(true);

		tableHdpeMb.addContainerProperty("SL", Label.class , new Label());
		tableHdpeMb.setColumnWidth("SL",15);

		tableHdpeMb.addContainerProperty("R/M NAME", ComboBox.class , new ComboBox());
		tableHdpeMb.setColumnWidth("R/M NAME",200);

		tableHdpeMb.addContainerProperty("R/M Type", ComboBox.class , new ComboBox());
		tableHdpeMb.setColumnWidth("R/M Type",100);

		tableHdpeMb.addContainerProperty("UNIT", TextRead.class , new TextRead(1));
		tableHdpeMb.setColumnWidth("UNIT",35);

		tableHdpeMb.addContainerProperty("Percent", AmountField.class , new AmountField());
		tableHdpeMb.setColumnWidth("Percent",45);

		tableHdpeMb.addContainerProperty("Qty" ,TextRead.class , new TextRead(1));
		tableHdpeMb.setColumnWidth("Qty",80);



		// lblCavityNo
		lblCavityNo = new Label("Cavity No :");
		lblCavityNo.setImmediate(false);
		lblCavityNo.setWidth("-1px");
		lblCavityNo.setHeight("-1px");

		// 		afCavityNo
		afCavityNo = new AmountField();
		afCavityNo.setImmediate(true);
		afCavityNo.setWidth("100px");
		afCavityNo.setHeight("-1px");

		// lblEstimatedProduction
		lblEstimatedProduction = new Label("Estimated Daily Production :");
		lblEstimatedProduction.setImmediate(false);
		lblEstimatedProduction.setWidth("-1px");
		lblEstimatedProduction.setHeight("-1px");

		// afEstimatedProduction
		afEstimatedProduction = new AmountField();
		afEstimatedProduction.setImmediate(true);
		afEstimatedProduction.setWidth("100px");
		afEstimatedProduction.setHeight("-1px");

		// lblActualProduction
		lblActualProduction = new Label("Actual Daily Production :");
		lblActualProduction.setImmediate(false);
		lblActualProduction.setWidth("-1px");
		lblActualProduction.setHeight("-1px");

		// afActualProduction
		afActualProduction = new AmountField();
		afActualProduction.setImmediate(true);
		afActualProduction.setWidth("100px");
		afActualProduction.setHeight("-1px");

		lblDate = new Label("Date :");
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);

		mainLayout.addComponent(lblFinishItemCode,"top:30.0px;left:50.0px;");
		mainLayout.addComponent(txtFinishItemCode, "top:27.0px;left:210.0px;");

		mainLayout.addComponent(lblProductionType,"top:30.0px;left:300.0px;");
		mainLayout.addComponent(cmbProductionType, "top:27.0px;left:400.0px;");

		mainLayout.addComponent(lblProductType,"top:30.0px;left:600.0px;");
		mainLayout.addComponent(cmbProductType, "top:27.0px;left:720.0px;");

		mainLayout.addComponent(lblGroup, "top:60.0px;left:50.0px;");
		mainLayout.addComponent(cmbGroup, "top:57.0px;left:210.0px;");

		mainLayout.addComponent(lblFinishItemName, "top:90.0px;left:50.0px;");
		mainLayout.addComponent(txtFinishItemName, "top:87.0px;left:210.0px;");



		mainLayout.addComponent(lblFinishItemRate, "top:150.0px;left:50.0px;");
		mainLayout.addComponent(txtFinishItemRate, "top:147.0px;left:210.0px;");

		mainLayout.addComponent(lblUnit, "top:120.0px;left:50.0px;");
		mainLayout.addComponent(txtUnit, "top:117.0px;left:210.0px;");

		mainLayout.addComponent(lblCycleTime, "top:120.0px;left:335.0px;");
		mainLayout.addComponent(afCycleTime, "top:117.0px;left:425.0px;");

		mainLayout.addComponent(lblDate, "top:150.0px;left:335.0px;");
		mainLayout.addComponent(dDate, "top:147.0px;left:425.0px;");
		mainLayout.addComponent(lblWeight, "top:120.0px;left:335.0px;");
		mainLayout.addComponent(afWeight, "top:117.0px;left:425.0px;");
		
		mainLayout.addComponent(lblSetQty, "top:180.0px;left:50.0px;");
		mainLayout.addComponent(txtSetQty, "top:177.0px;left:210.0px;");

		mainLayout.addComponent(lblRmName, "top:237.0px;left:10.0px;");

		mainLayout.addComponent(tableRm, "top:267.0px;left:30.0px;");
		mainLayout.addComponent(lblHdpeAndMbName, "top:237.0px;left:575.0px;");

		lblRmName.setVisible(false);
		//tableRm.setVisible(false);
		lblHdpeAndMbName.setVisible(false);
		tableHdpeMb.setVisible(false);
		
		lblCycleTime.setVisible(false);
		lblCavityNo.setVisible(false);
		afCycleTime.setVisible(false);
		afCavityNo.setVisible(false);
		lblActualProduction.setVisible(false);
		afActualProduction.setVisible(false);
		lblEstimatedProduction.setVisible(false);
		afEstimatedProduction.setVisible(false);

		mainLayout.addComponent(tableHdpeMb, "top:267.0px;left:575.0px;");




		mainLayout.addComponent(lblEstimatedProduction, "top:60.0px;left:540.0px;");
		mainLayout.addComponent(afEstimatedProduction, "top:57.0px;left:720.0px;");

		mainLayout.addComponent(lblActualProduction, "top:90.0px;left:540.0px;");
		mainLayout.addComponent(afActualProduction, "top:87.0px;left:720.0px;");

		//mainLayout.addComponent(lblDate, "top:120.0px; left:540.0px;");
		//mainLayout.addComponent(dDate, "top:117.0px; left:720.0px;");

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:445.0px;left:0.0px;");

		mainLayout.addComponent(button, "top:485.0px;left:80.0px;");

		return mainLayout;
	}
}
