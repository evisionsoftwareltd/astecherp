package com.example.RecycleModuleTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButtonNew;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
//import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class RecycledItemOpening extends Window 
{
	private CommonButtonNew button = new CommonButtonNew( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Exit","","");
	private VerticalLayout btnLayout = new VerticalLayout();


	private AbsoluteLayout mainLayout;


	private PopupDateField dOpeningYear;

	private Label lblOpeningYear;


	private Label lblBaseProduct;

	private ComboBox cmbBaseProduct;

	private PopupDateField dTransactionDate;

	private ComboBox cmbCategory;

	private Label lblTransactionDate;
	private Label lblCategory;

	private Label lblsubCategory;
	private ComboBox cmbsubCategory;

	private String ProductLedeger="";


	private Label lblItemName;

	private TextRead trGrade;

	private Label lblGrade;
	private ComboBox cmbItemName;


	private Label lblUnit;

	private TextRead trUnit;

	private Label lblTransactionId;
	private TextRead trTransactionId;

	private Label lblColor;
	private TextRead trColor;


	private Label lblQuantity;

	private AmountField amtQuantity;


	private Label lblRate;

	private AmountField amtRate;


	private Label lblAmount;
	private TextRead amtAmount;

	private Label lblsection;
	private ComboBox cmbsection;


	private Label lblline;

	boolean isUpdate=false;
	int index;

	private TextField txtCategoryID = new TextField();
	private TextField txtSubCategoryID = new TextField();
	private TextField txtTransactionId = new TextField();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
	private SimpleDateFormat dateF=new SimpleDateFormat("yyyy-MM-dd");

	ArrayList<Component> allComp = new ArrayList<Component>();

	private DecimalFormat df = new DecimalFormat("#0.00");

	private Formatter fmt = new Formatter();

	private SessionBean sessionBean;

	public RecycledItemOpening(SessionBean sessionBean)
	{
		buildMainLayout();
		this.sessionBean = sessionBean;
		this.setCaption(" RECYCLED ITEM OPENING :: "+sessionBean.getCompany());
		this.setResizable(false);
		this.setContent(mainLayout);
		buttonLayoutAdd();
		btnIni(true);
		componentIni(true);
		updateBtnFileldED(true);
		setEventAction();
		CategoryDataLoad();	
		BaseProductLoad();
		//cmbItemNameload();
		focusEnter();
		SectionLoad();
	}

	private void focusEnter()
	{
		allComp.add(dOpeningYear);
		allComp.add(trTransactionId);
		allComp.add(dTransactionDate);
		allComp.add(cmbCategory);
		allComp.add(cmbsubCategory);		
		allComp.add(cmbBaseProduct);
		allComp.add(cmbItemName);
		allComp.add(amtQuantity);
		allComp.add(amtRate);
		allComp.add(cmbsection);

		allComp.add(button.btnNew);
		allComp.add(button.btnUpdate);
		allComp.add(button.btnSave);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnDelete);
		allComp.add(button.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				focusEnter();
				newButtonEvent();
			}
		});

		button.btnUpdate.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(sessionBean.isUpdateable()){
					updateButtonEvent();
				}else{
					getParent().showNotification("You are not Permitted to Update", Notification.TYPE_WARNING_MESSAGE);	
				}
			}
		});

		button.btnDelete.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				//deleteButtonEvent();
				txtClear();
			}
		});

		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(sessionBean.isSubmitable()){
					formValidation();
				}
				else{
					getParent().showNotification("You are not Permitted to Save",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});

		button.btnExit.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				close();
				//showData();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event){ 
				refreshButtonEvent();
			}
		});

		cmbCategory.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbCategory.getValue()!=null)
				{
					subcategoryData();
					cmbItemNameload();
				}
				else
				{

				}
			}
		});

		cmbItemName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbItemName.getValue()!=null)
				{
					SetUnitGradeColor();	
				}

			}
		});


		cmbsubCategory.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbsubCategory.getValue()!=null)
				{
					cmbItemNameload();
				}

			}
		});


		amtQuantity.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try
				{

					double tamount,unitPrice,requiredQty; 
					if(!event.getProperty().toString().trim().isEmpty() && !amtRate.getValue().toString().trim().isEmpty())
					{
						requiredQty= Double.parseDouble(event.getProperty().toString().trim().replaceAll(",", ""));
						unitPrice= Double.parseDouble(amtRate.getValue().toString().replaceAll(",", ""));

						System.out.println("column Action");
						tamount=unitPrice*requiredQty;
						fmt = new Formatter();


						amtAmount.setValue(fmt.format("%.2f",tamount));
					}						


				}
				catch(Exception exp)
				{
					getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		amtRate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try
				{
					double tamount,unitPrice,requiredQty; 
					if(!event.getProperty().toString().trim().isEmpty() && !amtRate.getValue().toString().trim().isEmpty())
					{
						requiredQty= Double.parseDouble(event.getProperty().toString().trim().replaceAll(",", ""));
						unitPrice= Double.parseDouble(amtQuantity.getValue().toString().replaceAll(",", ""));
						System.out.println("column Action");
						tamount=unitPrice*requiredQty;
						fmt = new Formatter();
						amtAmount.setValue(fmt.format("%.2f",tamount));
					}						


				}
				catch(Exception exp)
				{
					getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});


	}

	private String selectTranscationId()
	{
		String itemcode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "select'TRN-'+ CAST(ISNULL(MAX(CAST(SUBSTRING(vTransactionId,5,LEN(vTransactionId)) as int) ),0)+1 as varchar(120))     from tbRecycleItemOpening";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext())
			{
				itemcode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return itemcode;
	}

	private void updateButtonEvent()
	{

		isUpdate = true;
		btnIni(false);
		//componentIni(false);
		//updateBtnFileldED(false);
		updateinit(true);

	}
	private void findButtonEvent() 
	{
		Window win = new RecycledItemOpeningFindWindow(sessionBean, txtTransactionId);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{

				if(!txtTransactionId.getValue().toString().isEmpty())
				{
					findInitialise(txtTransactionId.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtTransactionId) 
	{


		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();


			String sql= "select openingYear,vTransactionId,dTransactionDate,iCategoryId,vCategoryName,"
					+ "iSubCategoryid,vSubCategoryName,vBaseProductId,"
					+ "vBaseProductName,vItemId,vItemName,vUnit,"
					+ "vGrade,vColor,iQty,mRate,mAmount,section from tbRecycleItemOpening"
					+ " where vTransactionId like '"+txtTransactionId+"' ";

			System.out.println("Find query is"+sql);


			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object element[]=(Object[]) iter.next();

				dOpeningYear.setValue(element[0]);
				trTransactionId.setValue(element[1].toString());
				dTransactionDate.setValue(element[2]);
				cmbCategory.setValue(element[3]);
				System.out.println("cmbCategory :"+element[3]);
				cmbsubCategory.setValue(element[5]);
				System.out.println("cmbSubCategory :"+element[5]);
				cmbBaseProduct.setValue(element[7]);
				System.out.println("cmbBaseProduct :"+element[7]);
				cmbItemName.setValue(element[9]);
				System.out.println("cmbItemName :"+element[9]);
				trUnit.setValue(element[11].toString());
				trGrade.setValue(element[12].toString());
				trColor.setValue(element[13].toString());
				fmt=new Formatter();
				amtQuantity.setValue(element[14].toString().trim());
				fmt=new Formatter();
				amtRate.setValue(element[15].toString().trim());
				fmt=new Formatter();
				amtAmount.setValue(fmt.format("%.2f", Double.parseDouble(element[16].toString().trim())));
				fmt=new Formatter();
				cmbsection.setValue(element[17]);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void saveButtonEvent()
	{
		if(isUpdate)
		{
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{

						mb.buttonLayout.getComponent(0).setEnabled(false);
						saveUdTable();
						updateData();
						isUpdate=false;
						componentIni(true);
						updateBtnFileldED(true);
						txtClear();
						btnIni(true);
						button.btnNew.focus();
						mb.close();


					}
				}
			});	
		}
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save ?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{	

						mb.buttonLayout.getComponent(0).setEnabled(false);
						insertData();
						btnIni(true);
						componentIni(true);
						txtClear();
						mb.close();

					}
				}
			});	
		}
	}
	private void formValidation()
	{
		if(dOpeningYear.getValue()!=null)
		{
			if(trTransactionId.getValue()!=null)
			{	
				if(dTransactionDate.getValue()!=null)
				{
					if(cmbCategory.getValue()!=null)
					{
						if(cmbsubCategory.getValue()!=null)
						{
							if(cmbBaseProduct.getValue()!=null )
							{
								if(cmbItemName.getValue()!=null)
								{
									if(!trUnit.getValue().toString().trim().isEmpty())
									{
										if(!trGrade.getValue().toString().trim().isEmpty())
										{
											if(!trColor.getValue().toString().trim().isEmpty())
											{
												if(!amtQuantity.getValue().toString().trim().isEmpty())
												{
													if(!amtRate.getValue().toString().trim().isEmpty())
													{
														saveButtonEvent();
													}
													else{
														this.getParent().showNotification("Warning !","Enter Rate.", Notification.TYPE_WARNING_MESSAGE);
														amtQuantity.focus();
													}
												}
												else{
													this.getParent().showNotification("Warning !","Enter Quantity.", Notification.TYPE_WARNING_MESSAGE);
													amtQuantity.focus();
												}
											}
											else{
												this.getParent().showNotification("Warning !","Enter Color.", Notification.TYPE_WARNING_MESSAGE);
											}
										}
										else{
											this.getParent().showNotification("Warning !","Enter Grade.", Notification.TYPE_WARNING_MESSAGE);
										}
									}
									else{
										this.getParent().showNotification("Warning !","Enter Unit.", Notification.TYPE_WARNING_MESSAGE);
									}
								}
								else{
									this.getParent().showNotification("Warning !","Select Item Name.", Notification.TYPE_WARNING_MESSAGE);
									cmbItemName.focus();
								}  	
							}
							else{
								this.getParent().showNotification("Warning !","Select Base Product Name.", Notification.TYPE_WARNING_MESSAGE);
								cmbBaseProduct.focus();
							}
						}
						else{
							this.getParent().showNotification("Warning !","Select Sub Category Name.", Notification.TYPE_WARNING_MESSAGE);
							cmbsubCategory.focus();
						}
					}
					else{
						this.getParent().showNotification("Warning !","Select Category Name.", Notification.TYPE_WARNING_MESSAGE);
						cmbCategory.focus();
					}  
				}

				else
				{
					showNotification("Please Select Transaction Date",Notification.TYPE_WARNING_MESSAGE);
				}	
			}
			else
			{
				showNotification("Please Select Transaction Id",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Please Select Opening Year",Notification.TYPE_WARNING_MESSAGE);
		}

	}
	private void saveUdTable() {
		String flag="Old Data";
		String UdUserName= sessionBean.getUserName();
		String UdUserIp=sessionBean.getUserIp();
		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String insertUdRecycledOpening = "Insert Into tbUdRecycleItemOpening(openingYear,vTransactionId,"
					+ "dTransactionDate,iCategoryId,vCategoryName,iSubCategoryid,vSubCategoryName,vBaseProductId,"
					+ " vBaseProductName,vItemId,vItemName,vUnit,vGrade,vColor,iQty,mRate, "
					+ "mAmount,vflag,vUserName,vUserIp,dtEntryTime) select openingYear,vTransactionId,"
					+ "dTransactionDate,iCategoryId,vCategoryName,iSubCategoryid,vSubCategoryName,vBaseProductId,"
					+ " vBaseProductName,vItemId,vItemName,vUnit,vGrade,vColor,iQty,mRate,"
					+ " mAmount,'"+flag+"' ,'"+UdUserName+"','"+UdUserIp+"',CURRENT_TIMESTAMP from tbRecycleItemOpening where vTransactionId like '"+txtTransactionId.getValue().toString()+"'";

			session.createSQLQuery(insertUdRecycledOpening).executeUpdate();

			tx.commit();
			//this.getParent().showNotification("Ud All information update successfully.");

		}catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}


	}

	public void updateData()
	{

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String updateQOS = " Update tbRecycleItemOpening set iQty = '"+amtQuantity.getValue().toString().trim()+"',  "
					+ "mRate = '"+(amtRate.getValue().toString().equals("") ?"0.00":amtRate.getValue().toString().trim())+"',"
					+ "vUserName = '"+sessionBean.getUserName()+"',"
					+ " vUserIP = '"+sessionBean.getUserIp()+"', "
					+ "dtEntryTime = CURRENT_TIMESTAMP, mAmount='"+amtAmount.getValue().toString().trim()+"',section='"+cmbsection.getValue()+"', "
					+ " openingYear = '"+dateFormat.format(dOpeningYear.getValue())+"' "
					+ "WHERE vTransactionId = '"+trTransactionId.getValue()+"'";

			System.out.println("updateQOS :"+updateQOS);
			session.createSQLQuery(updateQOS).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information update successfully.");

		}catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}


	private void insertData()
	{
		Transaction tx = null;


		String grade="";
		String categoryId="";
		String categoryName="";

		String subcategoryId="";
		String subcategoryName="";

		String baseProductId="";
		String baseProductName="";
		String itemId="";
		String itemName="";
		String color="";
		String unitName="";

		if(!trGrade.getValue().toString().isEmpty())
		{
			grade=trGrade.getValue().toString();
		}

		if(!trColor.getValue().toString().isEmpty())
		{
			color=trColor.getValue().toString();
		}

		if(!trUnit.getValue().toString().isEmpty())
		{
			unitName=trUnit.getValue().toString();
		}
		if(cmbItemName.getValue()!= null)
		{
			itemId=cmbItemName.getValue().toString();
			itemName=cmbItemName.getItemCaption(cmbItemName.getValue());
		}

		if(cmbCategory.getValue()!=null)
		{
			categoryId=cmbCategory.getValue().toString();
			categoryName=cmbCategory.getItemCaption(cmbCategory.getValue());
		}

		if(cmbsubCategory.getValue()!=null)
		{
			subcategoryId=cmbsubCategory.getValue().toString();
			subcategoryName=cmbsubCategory.getItemCaption(cmbsubCategory.getValue());
		}


		if(cmbBaseProduct.getValue()!=null)
		{
			baseProductId=cmbBaseProduct.getValue().toString();
			baseProductName=cmbBaseProduct.getItemCaption(cmbBaseProduct.getValue());
		}

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String insertRecycledOpening = "Insert Into tbRecycleItemOpening(openingYear,vTransactionId,"
					+ "dTransactionDate,iCategoryId,vCategoryName,iSubCategoryid,vSubCategoryName,vBaseProductId,"
					+ " vBaseProductName,vItemId,vItemName,vUnit,vGrade,vColor,iQty,mRate,"
					+ "mAmount,vUserName,vUserIp,dtEntryTime,section)" 
					+ " values('"+dateFormat.format(dOpeningYear.getValue())+"',"
					+ "'"+trTransactionId.getValue().toString().trim()+"',"
					+ "'"+dateF.format(dTransactionDate.getValue())+"',"
					+ "'"+categoryId+"',"
					+ "'"+categoryName+"',"
					+ "'"+subcategoryId+"',"
					+ "'"+subcategoryName+"',"
					+ "'"+baseProductId+"',"
					+ "'"+baseProductName+"',"
					+ "'"+itemId+"',"
					+ "'"+itemName+"',"
					+ "'"+unitName+"',"
					+ "'"+grade+"',"
					+ "'"+color+"',"
					+ "'"+amtQuantity.getValue().toString().trim()+"',"
					+ "'"+amtRate.getValue().toString().trim()+"',"
					+ "'"+amtAmount.getValue().toString().trim()+"',"
					+ "'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+cmbsection.getValue()+"')	";


			/*	String LedgerOpen=" update tbLedger_Op_Balance set  DrAmount='"+amtAmount.getValue()+"',CrAmount='0.00' ,userId='"+sessionBean.getUserId()+"' ,userIp='"+sessionBean.getUserIp()+"',entryTime=getdate() where Ledger_Id like '"+ProductLedeger+"' ";
			System.out.println("LedgerOpen : "+LedgerOpen);
			session.createSQLQuery(LedgerOpen).executeUpdate();

			System.out.println("insertProductInfo : "+insertProductOpening);
			 */
			session.createSQLQuery(insertRecycledOpening).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information save successfully.");

		}catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void refreshButtonEvent()
	{
		componentIni(true);
		updateBtnFileldED(true);
		btnIni(true);
		txtClear();
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		updateBtnFileldED(false);
		btnIni(false);
		txtClear();
		trTransactionId.setValue(selectTranscationId());
		//	trTransactionId.focus();
		dTransactionDate.setValue(new Date());
		isUpdate = false;
	}

	public void txtClear()
	{
		cmbBaseProduct.setValue(null);
		trGrade.setValue("");
		trColor.setValue("");
		cmbItemName.setValue(null);
		txtCategoryID.setValue("");
		dTransactionDate.setValue(null);
		cmbCategory.setValue(null);
		trUnit.setValue("");
		amtQuantity.setValue("");
		amtRate.setValue("");
		amtAmount.setValue("");
		cmbsubCategory.setValue(null);
		trTransactionId.setValue("");
		dOpeningYear.setValue(new java.util.Date());
		cmbsection.setValue(null);
	}

	public void CategoryDataLoad()
	{
		cmbCategory.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String squery="select iCategoryId,vCategoryName from tbRecycleItemInfo";

			List list=session.createSQLQuery(squery).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbCategory.addItem(element[0]);
				cmbCategory.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void subcategoryData()
	{
		cmbsubCategory.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql= "select iSubCategoryID,vSubCategoryName from tbRecycleItemInfo where iCategoryId='"+cmbCategory.getValue()+"' ";

			List list=session.createSQLQuery(sql).list();


			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbsubCategory.addItem(element[0]);
				cmbsubCategory.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

	}
	public void BaseProductLoad()
	{
		cmbBaseProduct.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String query= "select vBaseProductId,vBaseProductName from tbRecycleItemInfo order by vBaseProductName";

			List list=session.createSQLQuery(query).list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBaseProduct.addItem(element[0]);
				cmbBaseProduct.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void SectionLoad()
	{
		cmbsection.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String query= "select AutoID,SectionName from tbSectionInfo";

			List list=session.createSQLQuery(query).list();
			cmbsection.removeAllItems();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbsection.addItem(element[0]);
				cmbsection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


	public void cmbItemNameload()
	{
		cmbItemName.removeAllItems();

		Transaction tx = null;

		try 
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String category="";
			String subCategory="";

			if(cmbCategory.getValue()!=null)
			{
				category=cmbCategory.getValue().toString();	
			}
			else
			{
				category="%";
			}

			if(cmbsubCategory.getValue()!=null)
			{
				subCategory=cmbsubCategory.getValue().toString();	
			}
			else
			{
				subCategory="%";
			}

			String squery= "select vItemCode,vItemName from tbRecycleItemInfo where iCategoryId like '"+category+"' and iSubCategoryId like '"+subCategory+"' ";
			System.out.println("squery : "+squery);

			List list = session.createSQLQuery(squery).list();

			cmbItemName.removeAllItems();
			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbItemName.addItem(element[0]);
				cmbItemName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}





	public void SetUnitGradeColor()
	{
		trUnit.setValue("");
		trGrade.setValue("");
		trColor.setValue("");
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String squery="select vUnitName,vModelNo,vColor from tbRecycleItemInfo where vItemCode='"+cmbItemName.getValue().toString()+"'";

			List list=session.createSQLQuery(squery).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				trUnit.setValue(element[0].toString());
				trGrade.setValue(element[1].toString());
				trColor.setValue(element[2].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void updateBtnFileldED(boolean b)
	{
		lblQuantity.setEnabled(!b);
		amtQuantity.setEnabled(!b);

		lblRate.setEnabled(!b);
		amtRate.setEnabled(!b);

		lblAmount.setEnabled(!b);
		amtAmount.setEnabled(!b);

		lblline.setEnabled(!b);
	}

	private void componentIni(boolean b) 
	{
		lblOpeningYear.setEnabled(!b);
		dOpeningYear.setEnabled(!b);

		lblBaseProduct.setEnabled(!b);
		cmbBaseProduct.setEnabled(!b);

		lblItemName.setEnabled(!b);
		cmbItemName.setEnabled(!b);

		trGrade.setEnabled(!b);
		lblGrade.setEnabled(!b);

		lblColor.setEnabled(!b);
		trColor.setEnabled(!b);

		lblTransactionDate.setEnabled(!b);
		dTransactionDate.setEnabled(!b);

		lblCategory.setEnabled(!b);
		cmbCategory.setEnabled(!b);
		cmbsubCategory.setEnabled(!b);
		trTransactionId.setEnabled(!b);
		lblTransactionId.setEnabled(!b);
		lblsubCategory.setEnabled(!b);
		cmbsection.setEnabled(!b);
		lblUnit.setEnabled(!b);
		trUnit.setEnabled(!b);

		lblQuantity.setEnabled(!b);
		amtQuantity.setEnabled(!b);

		lblRate.setEnabled(!b);
		amtRate.setEnabled(!b);

		lblAmount.setEnabled(!b);
		amtAmount.setEnabled(!b);

		lblline.setEnabled(!b);
	}

	private void updateinit(boolean b) 
	{
		lblOpeningYear.setEnabled(b);
		dOpeningYear.setEnabled(b);

		lblBaseProduct.setEnabled(!b);
		cmbBaseProduct.setEnabled(!b);

		lblItemName.setEnabled(!b);
		cmbItemName.setEnabled(!b);

		lblGrade.setEnabled(!b);
		trGrade.setEnabled(!b);

		lblColor.setEnabled(!b);
		trGrade.setEnabled(!b);

		lblTransactionDate.setEnabled(!b);
		dTransactionDate.setEnabled(!b);

		lblCategory.setEnabled(!b);
		cmbCategory.setEnabled(!b);
		cmbsubCategory.setEnabled(!b);
		trTransactionId.setEnabled(!b);

		lblUnit.setEnabled(!b);
		trUnit.setEnabled(!b);

		lblQuantity.setEnabled(b);
		amtQuantity.setEnabled(b);

		lblRate.setEnabled(b);
		amtRate.setEnabled(b);

		lblAmount.setEnabled(b);
		amtAmount.setEnabled(b);

		lblline.setEnabled(!b);
	}





	private void btnIni(boolean t)
	{

		button.btnNew.setEnabled(t);
		button.btnUpdate.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}


	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("585px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("590px");
		setHeight("514px");

		// lblOpeningYear
		lblOpeningYear = new Label();
		lblOpeningYear.setImmediate(false);
		lblOpeningYear.setWidth("-1px");
		lblOpeningYear.setHeight("-1px");
		lblOpeningYear.setValue("Opening Year :");
		mainLayout.addComponent(lblOpeningYear, "top:32.0px;right:400.0px;");

		// dOpeningYear
		dOpeningYear = new PopupDateField();
		dOpeningYear.setImmediate(true);
		dOpeningYear.setDateFormat("yyyy");
		dOpeningYear.setWidth("-1px");
		dOpeningYear.setHeight("-1px");
		dOpeningYear.setInvalidAllowed(false);
		dOpeningYear.setResolution(6);
		mainLayout.addComponent(dOpeningYear, "top:30.0px;left:194.0px;");

		lblTransactionId = new Label();
		lblTransactionId.setImmediate(false);
		lblTransactionId.setWidth("-1px");
		lblTransactionId.setHeight("-1px");
		lblTransactionId.setValue("Transaction Id :");
		mainLayout.addComponent(lblTransactionId, "top:60.0px;right:400.0px;");

		trTransactionId = new TextRead();
		trTransactionId.setImmediate(true);
		trTransactionId.setWidth("120px");
		trTransactionId.setHeight("-1px");
		mainLayout.addComponent(trTransactionId, "top:58.0px;left:194.0px;");

		lblTransactionDate = new Label();
		lblTransactionDate.setImmediate(false);
		lblTransactionDate.setWidth("-1px");
		lblTransactionDate.setHeight("-1px");
		lblTransactionDate.setValue("Transaction Date:");
		mainLayout.addComponent(lblTransactionDate, "top:86.0px;right:400.0px;");

		dTransactionDate = new PopupDateField();
		dTransactionDate.setDateFormat("dd-MM-yy");
		dTransactionDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dTransactionDate.setValue(new Date());
		dTransactionDate.setWidth("100px");
		mainLayout.addComponent(dTransactionDate, "top:84.0px;left:194.0px;");

		lblCategory = new Label();
		lblCategory.setImmediate(false);
		lblCategory.setWidth("-1px");
		lblCategory.setHeight("-1px");
		lblCategory.setValue("Category Name:");
		mainLayout.addComponent(lblCategory, "top:112.0px;right:400.0px;");

		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(true);
		cmbCategory.setNullSelectionAllowed(false);
		cmbCategory.setNewItemsAllowed(false);
		cmbCategory.setWidth("280px");
		cmbCategory.setHeight("-1px");
		cmbCategory.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbCategory, "top:110.0px;left:194.0px;");

		lblsubCategory = new Label();
		lblsubCategory.setImmediate(false);
		lblsubCategory.setWidth("-1px");
		lblsubCategory.setHeight("-1px");
		lblsubCategory.setValue(" Sub Category Name:");
		mainLayout.addComponent(lblsubCategory, "top:138.0px;right:400.0px;");

		cmbsubCategory = new ComboBox();
		cmbsubCategory.setImmediate(true);
		cmbsubCategory.setNullSelectionAllowed(false);
		cmbsubCategory.setNewItemsAllowed(false);
		cmbsubCategory.setWidth("280px");
		cmbsubCategory.setHeight("-1px");
		cmbsubCategory.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbsubCategory, "top:136.0px;left:194.0px;");

		lblBaseProduct = new Label();
		lblBaseProduct.setImmediate(false);
		lblBaseProduct.setWidth("-1px");
		lblBaseProduct.setHeight("-1px");
		lblBaseProduct.setValue("Base Product Name:");
		mainLayout.addComponent(lblBaseProduct, "top:164.0px;right:400.0px;");

		cmbBaseProduct = new ComboBox();
		cmbBaseProduct.setImmediate(true);
		cmbBaseProduct.setNullSelectionAllowed(false);
		cmbBaseProduct.setNewItemsAllowed(false);
		cmbBaseProduct.setWidth("300px");
		cmbBaseProduct.setHeight("-1px");
		cmbBaseProduct.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbBaseProduct, "top:162.0px;left:194.0px;");

		// lblItemName
		lblItemName = new Label();
		lblItemName.setImmediate(false);
		lblItemName.setWidth("-1px");
		lblItemName.setHeight("-1px");
		lblItemName.setValue("ItemName:");
		mainLayout.addComponent(lblItemName, "top:190.0px;right:400.0px;");

		// cmbItemName
		cmbItemName = new ComboBox();
		cmbItemName.setImmediate(true);
		cmbItemName.setNullSelectionAllowed(false);
		cmbItemName.setNewItemsAllowed(false);
		cmbItemName.setWidth("280px");
		cmbItemName.setHeight("-1px");
		cmbItemName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbItemName, "top:188.0px;left:195.0px;");

		// lblUnit
		lblUnit = new Label();
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		lblUnit.setValue("Unit :");
		mainLayout.addComponent(lblUnit, "top:216.0px;right:400.0px;");

		// trUnit
		trUnit = new TextRead();
		trUnit.setImmediate(false);
		trUnit.setWidth("100px");
		trUnit.setHeight("22px");
		mainLayout.addComponent(trUnit, "top:214.0px;left:195.0px;");

		// lblGrade
		lblGrade = new Label();
		lblGrade.setImmediate(false);
		lblGrade.setWidth("-1px");
		lblGrade.setHeight("-1px");
		lblGrade.setValue("Grade:");
		mainLayout.addComponent(lblGrade, "top:242.0px;right:400.0px;");		

		// trGrade
		trGrade = new TextRead();
		trGrade.setImmediate(false);
		trGrade.setWidth("100px");
		trGrade.setHeight("22px");
		mainLayout.addComponent(trGrade, "top:240.0px;left:193.5px;");

		lblColor = new Label();
		lblColor.setImmediate(false);
		lblColor.setWidth("-1px");
		lblColor.setHeight("-1px");
		lblColor.setValue("Color:");
		mainLayout.addComponent(lblColor, "top:268.0px;right:400.0px;");		

		// trGrade
		trColor = new TextRead();
		trColor.setImmediate(false);
		trColor.setWidth("100px");
		trColor.setHeight("22px");
		mainLayout.addComponent(trColor, "top:266.0px;left:193.5px;");


		// lblQuantity
		lblQuantity = new Label();
		lblQuantity.setImmediate(false);
		lblQuantity.setWidth("-1px");
		lblQuantity.setHeight("-1px");
		lblQuantity.setValue("Quantity :");
		mainLayout.addComponent(lblQuantity,"top:294.0px;right:400.0px;");

		// amtQuantity
		amtQuantity = new AmountField();
		amtQuantity.setImmediate(true);
		amtQuantity.setWidth("102px");
		amtQuantity.setHeight("22px");
		mainLayout.addComponent(amtQuantity,"top:292.0px;left:193.5px;");

		// lblRate
		lblRate = new Label();
		lblRate.setImmediate(false);
		lblRate.setWidth("-1px");
		lblRate.setHeight("-1px");
		lblRate.setValue("Rate :");
		mainLayout.addComponent(lblRate, "top:320.0px;right:400.0px;");

		// amtRate
		amtRate = new AmountField();
		amtRate.setImmediate(true);
		amtRate.setWidth("102px");
		amtRate.setHeight("22px");
		mainLayout.addComponent(amtRate, "top:318.0px;left:193.0px;");

		// lblAmount
		lblAmount = new Label();
		lblAmount.setImmediate(false);
		lblAmount.setWidth("-1px");
		lblAmount.setHeight("-1px");
		lblAmount.setValue("Amount :");
		mainLayout.addComponent(lblAmount, "top:346.0px;right:400.0px;");

		// amtAmount
		amtAmount = new TextRead(1);
		amtAmount.setImmediate(true);
		amtAmount.setWidth("102px");
		amtAmount.setHeight("22px");
		amtAmount.setStyleName("Amount");
		mainLayout.addComponent(amtAmount, "top:346.0px;left:197.0px;");

		// lblItemName
		lblsection = new Label();
		lblsection.setImmediate(false);
		lblsection.setWidth("-1px");
		lblsection.setHeight("-1px");
		lblsection.setValue("Section:");
		mainLayout.addComponent(lblsection, "top:374.0px;right:400.0px;");

		// cmbItemName
		cmbsection = new ComboBox();
		cmbsection.setImmediate(true);
		cmbsection.setNullSelectionAllowed(false);
		cmbsection.setNewItemsAllowed(false);
		cmbsection.setWidth("280px");
		cmbsection.setHeight("-1px");
		cmbsection.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbsection, "top:374.0px;left:195.0px;");

		lblline = new Label();
		lblline.setImmediate(false);
		lblline.setWidth("-1");
		lblline.setHeight("-1");
		lblline.setValue("________________________________________________________________________");
		mainLayout.addComponent(lblline,"top:402.0px; left:18.0px;");


		return mainLayout;
	}

	private void buttonLayoutAdd()
	{
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout,"top:430px;left:15px;");

		/*fileAttUpload = new BtUpload("temp/attendanceFolder/c");
		fileAttUpload.setImmediate(true);
		mainLayout.addComponent(fileAttUpload, "top:360.0px; left:0.0px;");*/
	}

}
