package com.example.productionSetup;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.naming.java.javaURLContextFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.lowagie.text.pdf.TextField;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class ColorFormulation extends Window
{

	SessionBean sessionBean;
	AbsoluteLayout mainLayout;

	private Label
	lbldeclaratinDate, lblProductionType, lblPartyName, lblFgName, lblLine, lblOpeningYearFind,lblparentcolor,lblsubColor;

	private ComboBox
	cmbProductionType, cmbPartyName, cmbFgName,cmbFindParty,cmbFindFG,cmbFindDate,cmbFindColor;
	PopupDateField dDeclaration;
	private DecimalFormat decFormat = new DecimalFormat("#0");
	private com.vaadin.ui.TextField txttransactionNo=new com.vaadin.ui.TextField();

	boolean 
	isUpdate = false;
	boolean 
	isFind = false;

	private Table table=new Table();
	private Table tableparentColor=new Table();
	private Table tableSubColor= new Table();

	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblFg = new ArrayList<Label>();
	private ArrayList<Label> lblFgCode = new ArrayList<Label>();

	//tableparentproperty
	private ArrayList<Label> tbparentlblsl = new ArrayList<Label>();
	private ArrayList<ComboBox> tbparentcmbParentColor = new ArrayList<ComboBox>();
	private ArrayList<AmountField> tbparentAmtpcskg = new ArrayList<AmountField>();
	private ArrayList<TextRead> tbparentQty = new ArrayList<TextRead>(1);
	private ArrayList<Label> tbparentlblunit = new ArrayList<Label>();

	//tableSubColor Property

	private ArrayList<Label> tbsubSl = new ArrayList<Label>();
	private ArrayList<ComboBox> tbsubCmbColor = new ArrayList<ComboBox>();
	private ArrayList<AmountField> tbsubAmtpcskg = new ArrayList<AmountField>();
	private ArrayList<TextRead> tbsubQty = new ArrayList<TextRead>(1);
	private ArrayList<Label> tbsublblunit = new ArrayList<Label>();
	private DecimalFormat df = new DecimalFormat("#0.000000");
	private SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	Panel panelSearch=new Panel();
	Label lblpanenSearch=new Label();
	


	private CommonButton
	button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "","Exit");

	public ColorFormulation(SessionBean sessionBean){
		this.sessionBean = sessionBean;
		this.setCaption("Color Formulatuion:: "+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		txtClear();
		focusEnter();
		setEventAction();
		cmbProductionTypeData();
		cmbPartyData();
		
	}
	
	
	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("1050px");
		setHeight("650px");

		// lblProductionType
		lblProductionType = new Label("Production Type :");
		lblProductionType.setImmediate(false);
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");

		// cmbProductionType
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("160px");
		cmbProductionType.setHeight("-1px");
		
		// lblPartyName
		lblPartyName = new Label("Party Name :");
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");

		// cmbPartyName
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("180px");
		cmbPartyName.setHeight("-1px");
		
		cmbFindParty=new ComboBox("Party Name");
		cmbFindParty.setImmediate(true);
		cmbFindParty.setWidth("180px");
		cmbFindParty.setHeight("24px");
		cmbFindParty.setNullSelectionAllowed(true);
		
		cmbFindColor=new ComboBox("Color :");
		cmbFindColor.setImmediate(true);
		cmbFindColor.setWidth("150px");
		cmbFindColor.setHeight("24px");
		cmbFindColor.setNullSelectionAllowed(true);
		
		
		
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
	
		
		
		// lblFgName
		lblFgName = new Label("Finished Good :");
		lblFgName.setImmediate(false);
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");

		// cmbFgName
		cmbFgName = new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setWidth("220px");
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
		
		
		lblparentcolor = new Label("<font color=blue size=+.5> PARENT COLOR  :</font>",Label.CONTENT_XHTML);
		lblparentcolor.setImmediate(false);
		lblparentcolor.setWidth("-1px");
		lblparentcolor.setHeight("-1px");
		
		
		
		//table parent
		
		tableparentColor.setSelectable(true);
		tableparentColor.setWidth("400px");
		tableparentColor.setHeight("80px");
		tableparentColor.setFooterVisible(true);
		tableparentColor.setColumnReorderingAllowed(true);
		tableparentColor.setColumnCollapsingAllowed(true);
		
		tableparentColor.addContainerProperty("SL #", Label.class, new Label());
		tableparentColor.setColumnWidth("SL #",20);
		
		tableparentColor.addContainerProperty("COLOR", ComboBox.class, new ComboBox());
		tableparentColor.setColumnWidth("COLOR",160);

		tableparentColor.addContainerProperty("Unit", Label.class, new Label());
		tableparentColor.setColumnWidth("Unit",30);

		tableparentColor.addContainerProperty("Pcs/kg", AmountField.class, new AmountField());
		tableparentColor.setColumnWidth("Pcs/kg",60);
		
		tableparentColor.addContainerProperty("Qty", TextRead.class, new TextRead());
		tableparentColor.setColumnWidth("Qty",60);
		
		tableParentInitialise();
		
		lblsubColor = new Label("<font color=blue size=+.5> SUB COLOR  :</font>",Label.CONTENT_XHTML);
		lblsubColor.setImmediate(false);
		lblsubColor.setWidth("-1px");
		lblsubColor.setHeight("-1px");
		
		//table Sub
		
		tableSubColor.setSelectable(true);
		tableSubColor.setWidth("400px");
		tableSubColor.setHeight("270px");
		tableSubColor.setColumnReorderingAllowed(true);
		tableSubColor.setColumnCollapsingAllowed(true);
		tableSubColor.setFooterVisible(true);
		
		tableSubColor.addContainerProperty("SL #", Label.class, new Label());
		tableSubColor.setColumnWidth("SL #",20);
		
		tableSubColor.addContainerProperty("COLOR", ComboBox.class, new ComboBox());
		tableSubColor.setColumnWidth("COLOR",160);

		tableSubColor.addContainerProperty("Unit", Label.class, new Label());
		tableSubColor.setColumnWidth("Unit",30);

		tableSubColor.addContainerProperty("Pcs/kg", AmountField.class, new AmountField());
		tableSubColor.setColumnWidth("Pcs/kg",60);
		
		tableSubColor.addContainerProperty("Qty", TextRead.class, new TextRead());
		tableSubColor.setColumnWidth("Qty",60);
		
		tableSubColorInitioalise();
		
		lblpanenSearch= new Label(" <font color='##0000FF' size='4px'><b><Strong>Search :<Strong></b></font>");
		lblpanenSearch.setContentMode(Label.CONTENT_XHTML);
		//mainLayout.addComponent(lblpanenSearch, "top:350px;left:770px;");
		
		panelSearch=new Panel();
		panelSearch.setWidth("400px");
		panelSearch.setHeight("180px");
		panelSearch.setEnabled(false);
		panelSearch.setStyleName("panelSearch");
		mainLayout.addComponent(panelSearch,"top:325px;left:600px;");
		
		FormLayout frmLayout=new FormLayout();
		frmLayout.setSpacing(true);
		frmLayout.setMargin(true);
		frmLayout.addComponent(cmbFindParty);
		frmLayout.addComponent(cmbFindFG);
		frmLayout.addComponent(cmbFindColor);
		frmLayout.addComponent(cmbFindDate);
		
		cmbFindDate.setImmediate(true);
		panelSearch.addComponent(frmLayout);
		
		
		mainLayout.addComponent(lblProductionType, "top: 17px; left: 20px;");
		mainLayout.addComponent(cmbProductionType, "top: 15px; left: 130px;");
		
		mainLayout.addComponent(lblPartyName, "top: 17px; left: 370px;");
		mainLayout.addComponent(cmbPartyName, "top: 15px; left: 480px;");
		
		mainLayout.addComponent(lblFgName, "top: 43px; left: 20px;");
		mainLayout.addComponent(cmbFgName, "top: 41px; left: 130px;");
		
		mainLayout.addComponent(lbldeclaratinDate, "top: 43px; left: 370px;");
		mainLayout.addComponent(dDeclaration,"top: 41px; left: 480px;");
		
		mainLayout.addComponent(lblparentcolor,"top: 100px; left: 120px;");
		
		mainLayout.addComponent(tableparentColor, "top:126px; left: 120px;");
		
		mainLayout.addComponent(lblsubColor,"top: 222px; left: 120px;");
		mainLayout.addComponent(tableSubColor,"top:248px; left: 120px;");
		
		mainLayout.addComponent(lblpanenSearch,"top: 290px; left:600px;");
		//mainLayout.addComponent(panelSearch,"top:355px;left:600px;");
		
		mainLayout.addComponent(button,"top:550px; left: 120px;");
		
		

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

		dDeclaration.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		cmbPartyName.setEnabled(!b);
		cmbFgName.setEnabled(!b);
		tableparentColor.setEnabled(!b);
		tableSubColor.setEnabled(!b);
		panelSearch.setEnabled(!b);
	}

	private void txtClear()
	{
		cmbProductionType.setValue(null);
		cmbPartyName.setValue(null);
		cmbFgName.setValue(null);
		dDeclaration.setValue(new java.util.Date());
		
		tbparentcmbParentColor.get(0).setValue(null);
		tbparentlblunit.get(0).setValue("");
		tbparentAmtpcskg.get(0).setValue("");
		tbparentQty.get(0).setValue("");
		
		for(int i=0;i<tbsubAmtpcskg.size();i++)
		{
			tbsubCmbColor.get(i).setValue(null);
			tbsublblunit.get(i).setValue("");
			tbsubAmtpcskg.get(i).setValue("");
			tbsubQty.get(i).setValue("");
		}
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
					if(cmbPartyName.getValue()!=null)
					{
						if(cmbFgName.getValue()!=null)
						{
							if(tbparentcmbParentColor.get(0).getValue()!=null)
							{
								if(!tbparentAmtpcskg.get(0).getValue().toString().isEmpty())
								{
									if(tbsubCmbColor.get(0).getValue()!=null)
									{
										if(!tbsubAmtpcskg.get(0).getValue().toString().isEmpty())
										{
											saveButtonEvent();	
										}
										else
										{
											showNotification("Invalid Entry","Please provide Sub Color pcs/Kg",Notification.TYPE_WARNING_MESSAGE);
											tbparentAmtpcskg.get(0).focus();
										}
										
									}
									else
									{
										showNotification("Invalid Entry","Please provide Sub Color",Notification.TYPE_WARNING_MESSAGE);
										tbparentAmtpcskg.get(0).focus();
									}
									
								}
								else
								{
									showNotification("Invalid Entry","Please provide parent Color pcs/kg",Notification.TYPE_WARNING_MESSAGE);
									tbparentAmtpcskg.get(0).focus();
								}
								
								
							}
							else
							{
								showNotification("Invalid Entry","Please Select Parent Color",Notification.TYPE_WARNING_MESSAGE);
								tbparentcmbParentColor.get(0).focus();
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

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
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

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
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

		cmbFgName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbFgName.getValue()!=null){
					if(!isFind)
					{
						if(duplicateCheck())
						{
							productData();
						}
					}
					else
					{
						productData();
					}
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
		
		cmbFindFG.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindFG.getValue()!=null){
					cmbColorDataLoad();
				}
				else{
					cmbFindDate.removeAllItems();
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
		
		cmbFindColor.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindColor.getValue()!=null){
					txtClear();
					FindDateLoad();
				}
				else{
					txtClear();
				}
			}
		});

		/*cmbOpeningYear.addListener(new ValueChangeListener() 
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
						sql=   " select a.fgCode, b.vProductName from tbFgOpening a" +
								" left join" +
								" (select vProductId, vProductName from tbFinishedProductInfo) b" +
								" on b.vProductId = a.fgCode "+
								" where DATEPART(YEAR, openingYear) like  '"+cmbOpeningYear.getValue().toString()+"' ";						

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
									lblFgCode.get(i).setValue(element[0].toString());
									lblFg.get(i).setValue(element[1].toString());									
								}

								if(i==lblFg.size()-1)
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
		});*/

		/*	table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(cmbOpeningYear.getValue()!=null)
				{					
					String   fg = lblFgCode.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					setFgData(fg);

					System.out.println("fg : "+fg);

					System.out.println("Done");
				}
				else{
					getParent().showNotification("Select Opening Year",Notification.TYPE_WARNING_MESSAGE);
				}
			}

		});*/

	}
	
	private void updateButtonEvent() {
		if (cmbFgName.getValue() != null&& cmbPartyName.getValue()!=null && tbparentcmbParentColor.get(0).getValue()!=null && tbsubCmbColor.get(0).getValue()!=null ) 
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
	
			String query= "select a.TransactionNo ,a.productionType,a.partyId, a.fgId,a.declarationDate,a.parentcolor,a.pcskg as parentpcskg,b.subcolorId, "
				      +" b.subpcskg  from tbcolorFormulationInfo a inner join  tbColorformulationDetails b "
				      +"on a.TransactionNo=b.TransactionNo where a.partyId like '"+cmbFindParty.getValue()+"' and a.fgId like '"+cmbFindFG.getValue()+"' and a.declarationDate like  "
				      +"(select MAX(declarationDate) from tbcolorFormulationInfo where partyId like '"+cmbFindParty.getValue()+"' and fgId like '"+cmbFindFG.getValue()+"' and parentcolor like '"+cmbFindColor.getValue().toString()+"' and CONVERT(date,declarationDate,105) like '"+cmbFindDate.getValue()+"') and parentcolor like '"+cmbFindColor.getValue()+"' ";
		
		
					 
			System.out.println("Sql Is"+query);
			
			List list=session.createSQLQuery(query).list();

				for(Iterator iter=list.iterator();iter.hasNext();){

					Object[] element=(Object[]) iter.next();
					if(i==0)
					{
					
						//txttransactionNo.setValue(element[0].toString());
						cmbProductionType.setValue(element[1].toString());
						cmbPartyName.setValue(element[2].toString());
						cmbFgName.setValue(element[3].toString());
						dDeclaration.setValue(element[4]);
						tbparentcmbParentColor.get(0).setValue(element[5].toString());
						tbparentAmtpcskg.get(0).setValue(element[6]);
					
					}
					
					tbsubCmbColor.get(i).setValue(element[7].toString());
					tbsubAmtpcskg.get(i).setValue(element[8]);
					
					
					i++;
				}

			
			System.out.println("OK");

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
					"(select fgId from tbcolorFormulationInfo where partyId like '"+cmbFindParty.getValue()+"')";
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
	
	private void cmbColorDataLoad() {
		cmbFindColor.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			//String sql="select 0,CONVERT(date,declarationDate,105) as date from tbcolorFormulationInfo where partyId='"+cmbFindParty.getValue()+"' and fgId='"+cmbFindFG.getValue()+"' ";
			String query=" select distinct  parentcolor,(select vRawItemName from tbRawItemInfo where vRawItemCode like parentcolor ) as colorName from tbcolorFormulationInfo where partyId like '"+cmbFindParty.getValue()+"' and fgId like '"+cmbFindFG.getValue()+"' ";
			List list=session.createSQLQuery(query).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindColor.addItem(element[0]);
				cmbFindColor.setItemCaption(element[0], element[1].toString());
				
			}
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
			String sql="select 0,CONVERT(date,declarationDate,105) as date from tbcolorFormulationInfo where partyId='"+cmbFindParty.getValue()+"' and fgId='"+cmbFindFG.getValue()+"' and parentcolor like '"+cmbFindColor.getValue().toString()+"' ";
			//String query=" select distinct  parentcolor,(select vRawItemName from tbRawItemInfo where vRawItemCode like parentcolor ) as colorName from tbcolorFormulationInfo where partyId like '"+cmbFindParty.getValue()+"' and fgId like '"+cmbFindFG.getValue()+"' ";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindDate.addItem(element[1]);
				
			}
		}
		catch(Exception exp){
			showNotification("cmbDelareDateLoad: "+exp);
		}
	}
	
	private void cmbFinishedPartyInfo() 
	{
		cmbFindParty.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct vGroupId,partyName from tbPartyInfo where vGroupId in (select partyId from tbcolorFormulationInfo)";
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
	
	private void findButtonEvent()
	{
		componentIni(true);
		panelSearch.setEnabled(true);
		isFind=true;
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		isFind = false;
		isUpdate = false;
		//componentIniFind(true);
		autoJobNo(); 
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

		allComp.add(cmbProductionType);
		allComp.add(cmbPartyName);
		allComp.add(cmbFgName);
		allComp.add(dDeclaration);
		
		allComp.add(tbparentcmbParentColor.get(0));
		allComp.add(tbparentAmtpcskg.get(0));
		
		for(int i=0;i<tbsubCmbColor.size();i++)
		{
			allComp.add(tbsubCmbColor.get(i));
			allComp.add(tbsubAmtpcskg.get(i));	
		}
		
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
	

	private void autoJobNo() 
	{
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select ISNULL(MAX(TransactionNo),0)+1    from tbcolorFormulationInfo";
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

	private boolean duplicateCheck()
	{
		String sql = "";
		sql = "select * from tbFgOpening where fgCode like '"+cmbFgName.getValue()+"' and DATEPART(YEAR, openingYear) like '2016'";

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

	private void productData()
	{


		String sql = "";
		sql = "select vUnitName,0 from tbFinishedProductInfo where vProductId like '"+cmbFgName.getValue()+"'";

		System.out.println("productData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			//txtUnit.setValue(element[0].toString());
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
					/*	if(deleteData(session,tx))
						{
							insertData(session,tx);	
						}

						else
						{
							tx.rollback();
						}*/
						
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

	/*	private boolean deleteData(Session session,Transaction tx){
		try{
			session.createSQLQuery("delete from tbFgOpening where fgCode like '"+cmbFgName.getValue()+"' and DATEPART(YEAR, openingYear) like '"+ dfYear.format(dOpeningYear.getValue())+"'").executeUpdate();
			return true;
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	 */
	private void insertData(Session session,Transaction tx) {

		try
		{
			String sql = "";
			String query="";
			
			if(!isUpdate)
			{
				
				sql=     "insert into tbcolorFormulationInfo "
						 +"(TransactionNo,productionType,partyId,fgId,declarationDate,parentcolor,pcskg,qty,userIp,userId,entryTime) "
				         +"values('"+txttransactionNo.getValue().toString()+"','"+cmbProductionType.getValue().toString()+"','"+cmbPartyName.getValue().toString()+"','"+cmbFgName.getValue().toString()+"' ,  "
				         +" '"+dateformat1.format(dDeclaration.getValue())+"','"+tbparentcmbParentColor.get(0).getValue()+"','"+tbparentAmtpcskg.get(0).getValue()+"' ,'"+tbparentQty.get(0).getValue()+"',"
				         +" '"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',getdate()) ";
				
				System.out.println("insert info: "+sql);	

				session.createSQLQuery(sql).executeUpdate();	
			}
			else
			{
				sql=     "insert into tbcolorFormulationInfo "
						 +"(TransactionNo,productionType,partyId,fgId,declarationDate,parentcolor,pcskg,qty,userIp,userId,entryTime) "
				         +"values('"+txttransactionNo.getValue().toString()+"','"+cmbProductionType.getValue().toString()+"','"+cmbPartyName.getValue().toString()+"','"+cmbFgName.getValue().toString()+"' ,  "
				         +" getdate(),'"+tbparentcmbParentColor.get(0).getValue()+"','"+tbparentAmtpcskg.get(0).getValue()+"' ,'"+tbparentQty.get(0).getValue()+"',"
				         +" '"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',getdate()) ";
				
				System.out.println("insert info: "+sql);	

				session.createSQLQuery(sql).executeUpdate();
				
			}

		
			
			for(int i=0;i<tbsubCmbColor.size();i++)
			{
				if(tbsubCmbColor.get(i).getValue()!=null && ! tbsubAmtpcskg.get(i).getValue().toString().isEmpty())
				{
					    query= "insert into tbColorformulationDetails (TransactionNo,subcolorId,subpcskg,subqty) "
					       +"values('"+txttransactionNo.getValue().toString()+"','"+tbsubCmbColor.get(i).getValue()+"','"+tbsubAmtpcskg.get(i).getValue()+"','"+tbsubQty.get(i).getValue()+"') ";
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

	private void cmbOpeningYearData()
	{
		//cmbOpeningYear.removeAllItems();

		String sql = "";
		sql = "select distinct DATEPART(YEAR, openingYear), 0 from tbFgOpening";

		System.out.println("cmbOpeningYearData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			//cmbOpeningYear.addItem(element[0]);
		}
	}

	public void tableParentInitialise()
	{
		for(int i=0;i<1;i++){
			tableRowAdd(i);
		}
	}
	
	public void tableSubColorInitioalise()
	{
		for(int i=0;i<7;i++)
		{
			tableRowAddSubColor(i);
		}
	}
	
	

	public void tableRowAdd(final int ar)
	{
		
		tbparentlblsl.add(ar, new Label(""));
		tbparentlblsl.get(ar).setWidth("100%");
		tbparentlblsl.get(ar).setImmediate(true);
		tbparentlblsl.get(ar).setHeight("23px");
		tbparentlblsl.get(ar).setValue(ar+1);
		

		tbparentcmbParentColor.add(ar, new ComboBox());
		tbparentcmbParentColor.get(ar).setWidth("100%");
		tbparentcmbParentColor.get(ar).setImmediate(true);
		tbparentcmbParentColor.get(ar).setHeight("23px");
		
		String sql = "";
		sql = "select  vRawItemCode,vRawItemName from tbRawItemInfo "
              +"where vCategoryType like 'Ink' order by vRawItemName ";
		
		System.out.println("cmbProductionTypeData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element = (Object[]) iter.next();
			tbparentcmbParentColor.get(ar).addItem(element[0].toString());
			tbparentcmbParentColor.get(ar).setItemCaption(element[0].toString(), element[1].toString());
		}
		
		tbparentcmbParentColor.get(ar).addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(tbparentcmbParentColor.get(ar).getValue()!=null)
				{
					if (doubleEntryCheck(ar))
					{
						String sql = "";
						sql = "select vUnitName from tbRawItemInfo where vRawItemCode like '"+tbparentcmbParentColor.get(ar).getValue()+"' ";
						List list=dbService(sql);
						Iterator iter=list.iterator();
						if(iter.hasNext())
						{
							tbparentlblunit.get(ar).setValue(iter.next());
							
						}
							
					}
					else
					{
						showNotification("Double Entry Is Not Allowed",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				
			}
		});
		
		

		tbparentlblunit.add(ar, new Label(""));
		tbparentlblunit.get(ar).setWidth("100%");
		tbparentlblunit.get(ar).setImmediate(true);
		tbparentlblunit.get(ar).setHeight("23px");
		
		tbparentAmtpcskg.add(ar, new  AmountField());
		tbparentAmtpcskg.get(ar).setWidth("100%");
		tbparentAmtpcskg.get(ar).setImmediate(true);
		tbparentAmtpcskg.get(ar).setHeight("23px");
		
		tbparentAmtpcskg.get(ar).addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event) 
			{
				if(tbparentcmbParentColor.get(ar).getValue()!=null && !tbparentAmtpcskg.get(ar).getValue().toString().isEmpty())
				{
					double pcskg=0.00;
					double qty=0.00;
					pcskg=Double.parseDouble(tbparentAmtpcskg.get(ar).getValue().toString()); 
					qty= 1/pcskg ;
					tbparentQty.get(ar).setValue(df.format(qty));
				}	
			}
		});
		
		tbparentQty.add(ar, new  TextRead(1));
		tbparentQty.get(ar).setWidth("100%");
		tbparentQty.get(ar).setImmediate(true);
		tbparentQty.get(ar).setHeight("23px");
		

		tableparentColor.addItem(new Object[]{tbparentlblsl.get(ar),tbparentcmbParentColor.get(ar),tbparentlblunit.get(ar),tbparentAmtpcskg.get(ar),tbparentQty.get(ar) },ar);
	}
	
	private boolean doubleEntryCheck( int ar)
	{
		String captyion=tbparentcmbParentColor.get(ar).getItemCaption(tbparentcmbParentColor.get(ar).getValue().toString());

		for(int i=0;i<tbparentcmbParentColor.size();i++)
		{
			if(tbparentcmbParentColor.get(i).getValue()!=null)
			{
				if(ar!=i && captyion.equals(tbparentcmbParentColor.get(i).getItemCaption(tbparentcmbParentColor.get(i).getValue().toString())))
				{
					return false;	
				}
			}
		}


		return true;

	}
	
	
	private boolean doubleEntryChecksub( int ar)
	{
		String captyion=tbsubCmbColor.get(ar).getItemCaption(tbsubCmbColor.get(ar).getValue().toString());

		for(int i=0;i<tbsubCmbColor.size();i++)
		{
			if(tbsubCmbColor.get(i).getValue()!=null)
			{
				if(ar!=i && captyion.equals(tbsubCmbColor.get(i).getItemCaption(tbsubCmbColor.get(i).getValue().toString())))
				{
					return false;	
				}
			}
		}


		return true;

	}
	
	
	public void tableRowAddSubColor(final int ar)
	{
		
		tbsubSl.add(ar, new Label(""));
		tbsubSl.get(ar).setWidth("100%");
		tbsubSl.get(ar).setImmediate(true);
		tbsubSl.get(ar).setHeight("23px");
		tbsubSl.get(ar).setValue(ar+1);
		

		tbsubCmbColor.add(ar, new ComboBox());
		tbsubCmbColor.get(ar).setWidth("100%");
		tbsubCmbColor.get(ar).setImmediate(true);
		tbsubCmbColor.get(ar).setHeight("23px");
		
		String sql = "";
		sql = "select  vRawItemCode,vRawItemName from tbRawItemInfo "
              +"where vCategoryType like 'Ink' order by vRawItemName ";
		
		System.out.println("cmbProductionTypeData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element = (Object[]) iter.next();
			tbsubCmbColor.get(ar).addItem(element[0].toString());
			tbsubCmbColor.get(ar).setItemCaption(element[0].toString(), element[1].toString());
		}
		
		tbsubCmbColor.get(ar).addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(tbsubCmbColor.get(ar).getValue()!=null)
				{
					if (doubleEntryChecksub(ar))
					{
						String sql = "";
						sql = "select vUnitName from tbRawItemInfo where vRawItemCode like '"+tbsubCmbColor.get(ar).getValue()+"' ";
						List list=dbService(sql);
						Iterator iter=list.iterator();
						if(iter.hasNext())
						{
							tbsublblunit.get(ar).setValue(iter.next());
							
						}
						if(ar==tbsubCmbColor.size()-1)
						{
							tableRowAddSubColor(ar+1);
						}
							
					}
					else
					{
						showNotification("Double Entry Is Not Allowed",Notification.TYPE_WARNING_MESSAGE);
						tbsubCmbColor.get(ar).setValue(null);
					}
				}
				
			}
		});
		

		tbsublblunit.add(ar, new Label(""));
		tbsublblunit.get(ar).setWidth("100%");
		tbsublblunit.get(ar).setImmediate(true);
		tbsublblunit.get(ar).setHeight("23px");
		
		tbsubAmtpcskg.add(ar, new  AmountField());
		tbsubAmtpcskg.get(ar).setWidth("100%");
		tbsubAmtpcskg.get(ar).setImmediate(true);
		tbsubAmtpcskg.get(ar).setHeight("23px");
		
		tbsubAmtpcskg.get(ar).addListener(new ValueChangeListener() 
		{
			
			public void valueChange(ValueChangeEvent event) 
			{
				if(tbsubCmbColor.get(ar).getValue()!=null && !tbsubAmtpcskg.get(ar).getValue().toString().isEmpty())
				{
					double pcskg=0.00;
					double qty=0.00;
					pcskg=Double.parseDouble(tbsubAmtpcskg.get(ar).getValue().toString()); 
					qty= 1/pcskg ;
					tbsubQty.get(ar).setValue(df.format(qty));
				}	
			}
		});
		
		
		tbsubQty.add(ar, new  TextRead(1));
		tbsubQty.get(ar).setWidth("100%");
		tbsubQty.get(ar).setImmediate(true);
		tbsubQty.get(ar).setHeight("23px");
		

		tableSubColor.addItem(new Object[]{tbsubSl.get(ar),tbsubCmbColor.get(ar),tbsublblunit.get(ar),tbsubAmtpcskg.get(ar),tbsubQty.get(ar) },ar);
	}
	
	
	

	private void tableclear()
	{
		for(int i=0; i<lblFg.size(); i++)
		{
			lblFg.get(i).setValue("");		
		}
	}

	private void setFgData(String fg)
	{
		txtClear();
		String sql = "";
		sql = "select openingYear, productionType, partyCode, fgCode, openingQty from tbFgOpening where fgCode like '"+fg+"'";

		System.out.println("productData : "+sql);

		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			cmbProductionType.setValue(element[1].toString());
			cmbPartyName.setValue(element[2].toString());
			cmbFgName.setValue(element[3].toString());

		}
	}

	/*private void componentIniFind(boolean b) 
	{
		lblOpeningYearFind.setEnabled(!b);
		table.setEnabled(!b);
	}*/

}
