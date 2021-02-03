package acc.appform.LcModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

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
import com.example.productionSetup.ProductionType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class LtrInformation extends Window
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblDate;
	private DateField date;

	private Label lblLcNo;
	private ComboBox cmbLcNo;

	private Label lblGroup;
	private ComboBox cmbGroup;

	private NativeButton nbGroup;

	private Label lblSubGroup;
	private ComboBox cmbSubGroup;

	private NativeButton nbSubGroup;

	private Label    lblamtLcAmount;	
	private AmountCommaSeperator amtLcAmount;

	private Label lblLtrNo;
	private ComboBox cmbLtrNo;

	private Label    lblamtLtrAmount;	
	private AmountCommaSeperator amtLtrAmount;

	private boolean isUpdate = false;
	private boolean isFind = true;

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtCategoryID = new TextField();
	ArrayList<Component> allComp = new ArrayList<Component>();
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0.00");
	private CommaSeparator cms = new CommaSeparator();	

	String findLedgerId="";
	String findVoucherNo="";

	public LtrInformation(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("LTR INFORMATION :: "+this.sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		btnIni(true);
		componentIni(true);
		setEventAction();
		focusEnter();
		authenticationCheck();

		//subGroupNameLoad();
	}
	public String ledgerId() 
	{
		String ledgerId = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select cast(isnull(max(cast(replace(Ledger_Id, 'LL', '')as int))+1, 1)as varchar)" +
					" from tbLedger where Ledger_Id like 'LL%' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				ledgerId = "LL"+iter.next().toString();
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return ledgerId;
	}
	private void subGroupNameLoad(){
		cmbSubGroup.removeAllItems();
		String sql="select Group_Id,Group_Name from tbMain_Group where Head_Id='"+cmbGroup.getValue()+"'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbSubGroup.addItem(element[0]);
			cmbSubGroup.setItemCaption(element[0], element[1].toString());
		}
	}
	private void groupNameLoad(){
		cmbGroup.removeAllItems();
		String sql="select Head_Id,Head_Name from tbPrimary_Group where Head_Id='L8'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbGroup.addItem(element[0]);
			cmbGroup .setItemCaption(element[0], element[1].toString());
		}
	}
	private void ltrNoLoad(){
		String ltr="";
		if(cmbGroup.getValue()!=null){
			ltr=ltr+cmbGroup.getValue();
		}
		if(cmbSubGroup.getValue()!=null){
			ltr=ltr+'-'+cmbSubGroup.getValue();
		}
		cmbLtrNo.removeAllItems();
		String sql="select Ledger_Id,Ledger_Name from tbLedger where Create_From like '%"+ltr+"%'  order by Ledger_Name";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbLtrNo.addItem(element[0]);
			cmbLtrNo .setItemCaption(element[0], element[1].toString());
		}
	}
	private void lcNoLoad(){

		String sql;
		if(isFind){
			sql="select vTransactionID,vLcNo from tbLcOpeningInfo where isActive=1 /*and vLcNo */";
			//+ " in(select lcNo  from tbLtrInformation)";
		}
		else{
			sql="select vTransactionID,vLcNo from tbLcOpeningInfo where isActive=1 and vLcNo "
					+ "not in(select lcNo  from tbLtrInformation)";
		}
		cmbLcNo.removeAllItems();
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbLcNo.addItem(element[0]);
			cmbLcNo.setItemCaption(element[0], element[1].toString());
		}
	}
	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{button.btnSave.setVisible(false);}
		if(!sessionBean.isUpdateable())
		{button.btnEdit.setVisible(false);}
		if(!sessionBean.isDeleteable())
		{button.btnDelete.setVisible(false);}
	}
	private boolean isClosed(){

		Iterator<?>iter=dbService("select * from tbLcOpeningInfo where vTransactionID='"+cmbLcNo.getValue()+"' " +
				"and vLcNo='"+cmbLcNo.getItemCaption(cmbLcNo.getValue())+"' and isActive=1");
		if(iter.hasNext()){
			return true;
		}

		return false;
	}
	private void focusEnter()
	{	
		allComp.add(cmbLcNo);
		allComp.add(amtLcAmount);
		allComp.add(cmbGroup);
		allComp.add(cmbSubGroup);
		allComp.add(cmbLtrNo);
		allComp.add(amtLtrAmount);    	

		allComp.add(button.btnNew);
		allComp.add(button.btnSave);
		allComp.add(button.btnEdit);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnFind);
		allComp.add(button.btnExit);

		new FocusMoveByEnter(this,allComp);
	}

	/*	private void HeadNameAlreadryExistCheck()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select headName from tbLcHeadInfo where headName='"+txtHeadName.getValue().toString().trim()+"'";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty() && !isFind)
			{
				txtHeadName.setValue("");
				showNotification("Warning!","Head name is already exists.", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}*/

	public void setEventAction()
	{
		cmbGroup.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbGroup.getValue()!=null){
					subGroupNameLoad();
					ltrNoLoad();
				}
				else{
					cmbSubGroup.removeAllItems();
					cmbLtrNo.removeAllItems();
				}
			}
		});
		cmbSubGroup.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbSubGroup.getValue()!=null){
					ltrNoLoad();
				}
				else{
					cmbLtrNo.removeAllItems();
				}
			}
		});
		/*txtHeadName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtHeadName.getValue().toString().isEmpty())
				{
					HeadNameAlreadryExistCheck();
				}
			}
		});*/

		/*nbGroup.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				System.out.println("Group");
				groupLink();				
			}
		});

		nbSubGroup.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				System.out.println("Sub Group");
				subGroupLink();				
			}
		});*/

		button.btnNew.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbLcNo.getValue()!=null)
				{
					if(isClosed()){
						isFind = false;
						updateButtonEvent();
					}
					else{
						showNotification("Sorry!!","LC No: "+cmbLcNo.getItemCaption(cmbLcNo.getValue())+" is already Closed",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Sorry!!","No data for update",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(checkValidation()){
					saveButtonEvent();
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				findButtonEvent();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				refreshButtonEvent();
			}
		});

		button.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		amtLcAmount.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				amtLtrAmount.setValue(cms.setComma(Double.parseDouble("0"+amtLcAmount.getValue())));
			}
		});
		amtLtrAmount.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				amtLcAmount.setValue(cms.setComma(Double.parseDouble("0"+amtLtrAmount.getValue())));
			}
		});
	}
	private void updateButtonEvent(){
		btnIni(false);
		componentIni(false);
		isUpdate=true;
	}
	private boolean deleteUpdateData(){
		Session session = SessionFactoryUtil.getInstance().openSession();								
		Transaction tx = session.beginTransaction();								
		try								
		{								
			String deleteLtr = "delete from tbLtrInformation where ltrLedgerId='"+findLedgerId+"'";														
			session.createSQLQuery(deleteLtr).executeUpdate();							

			/*String UpdateLedger = "UPDATE tbLedger set" +
				" Ledger_Name = '"+cmbLtrNo.getValue()+"' " +
				" where Ledger_Id='"+findLedgerId+"' ";
		session.createSQLQuery(UpdateLedger).executeUpdate();*/

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			//voucher delete
			String sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+findVoucherNo+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();

			tx.commit();	
			return true;
		}								
		catch(Exception exp)								
		{								
			tx.rollback();							
			showNotification("Error to update",exp+"",Notification.TYPE_ERROR_MESSAGE);							
		}								
		finally{session.close();}								

		return false;
	}
	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{	
						mb.buttonLayout.getComponent(0).setEnabled(false);
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();
						try
						{
							//updateTrack(session);
							//deleteData(session);
							//insertData(session);
							if(deleteUpdateData()){
								if(insertData()){
									showNotification("All information updated successfully.");
									btnIni(true);
									componentIni(true);
									txtClear();
									button.btnNew.focus();

									isFind = false;
									isUpdate=false;
									findLedgerId="";
									findVoucherNo="";
								}
							}
						}
						catch(Exception exp)
						{
							showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
						}
					}
				}
			});
		}
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						try
						{
							//insertData(session);
							if(insertData()){
								showNotification("All information saved successfully.");
								btnIni(true);
								componentIni(true);
								txtClear();
								button.btnNew.focus();
							}
						}
						catch(Exception exp)
						{
							showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);

						}
					}
				}
			});
		}
	}
	private boolean insertData()												
	{											
		boolean ret = false;										

		Session session = SessionFactoryUtil.getInstance().openSession();									
		Transaction tx = session.beginTransaction();									
		try									
		{	
			String ledgerId="";
			String voucherNo="";


			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			int sl = 1;




			if(isUpdate)
			{
				ledgerId=findLedgerId;
				voucherNo=findVoucherNo;
			}
			else{
				Iterator<?> iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1)  "
						+ "FROM "+voucher+" WHERE vouchertype = 'jau' and CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
				if(iter.hasNext())
					sl = Integer.valueOf(iter.next().toString());

				ledgerId=ledgerId();
				voucherNo="JV-NO-"+sl;
			}


			String insertLtr = "insert into tbLtrInformation (ltrId,ltrNo,ltrAmount,ltrledgerId,lcTransactionId,lcLegerId, "+
					" lcNo,lcAmount,groupId,groupName,subGroupId,subGroupName,userIp,userName,entryTime,voucherNo)values  "+
					" ('"+getLtrId(session)+"',"
					+ "'"+cmbLtrNo.getValue()+"',"
					+ "'"+amtLtrAmount.getValue().replace(",", "")+"',"
					+ "'"+ledgerId+"',"
					+ "'"+cmbLcNo.getValue()+"',"
					+ "(select vLedgerID from tbLcOpeningInfo where vTransactionID='"+cmbLcNo.getValue()+"'),"
					+ "'"+cmbLcNo.getItemCaption(cmbLcNo.getValue())+"',"
					+ "'"+amtLcAmount.getValue().replace(",", "")+"',"
					+ "'"+cmbGroup.getValue()+"',"
					+ "'"+cmbGroup.getItemCaption(cmbGroup.getValue())+"',"
					+ "'"+cmbSubGroup.getValue()+"',"
					+ "'"+cmbSubGroup.getItemCaption(cmbSubGroup.getValue())+"',"
					+ "'"+sessionBean.getUserIp()+"',"
					+ "'"+sessionBean.getUserName()+"',"
					+ "CURRENT_TIMESTAMP,'"+voucherNo+"') ";								
			session.createSQLQuery(insertLtr).executeUpdate();

			/*if(!isUpdate){
			String InsertLedger="insert into tbLedger(Ledger_Id,Ledger_Name,Creation_Year,Parent_Id,Create_From,userId,"
					+ "userIp,entryTime,companyId)values("
					+ "'"+ledgerId+"',"
					+ "'"+cmbLtrNo.getValue()+"',"
					+ "'"+dateFormat.format(sessionBean.getFiscalOpenDate())+"',"
					+" '"+(cmbGroup.getValue()!=null?cmbGroup.getValue().toString():cmbSubGroup.getValue().toString())+"'," 
					+" 'L8-"+cmbGroup.getValue()+"-"+cmbSubGroup.getValue()+"', " 
					+ "'"+sessionBean.getUserId()+"',"
					+ "'"+sessionBean.getUserIp()+"',"
					+ "CURRENT_TIMESTAMP,'"+sessionBean.getCompanyId()+"')";
			session.createSQLQuery(InsertLedger).executeUpdate();

			String LedgerOpen="INSERT into tbLedger_Op_Balance values(" +
					" '"+ledgerId+"', " +
					" '0.00', " +
					" '0.00', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";
			session.createSQLQuery(LedgerOpen).executeUpdate();
		}*/



			//voucherNo.setValue("JV-NO-"+sl);

			String debitHead = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,"
					+ "costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill, attachChequeBill,skuId,skuName) "+
					" VALUES('"+voucherNo+"','"+dtfYMD.format(date.getValue())+"',(select vLedgerID from tbLcOpeningInfo where vTransactionID='"+cmbLcNo.getValue()+"'),"
					+ "'','0"+amtLcAmount.getValue().replace(",", "")+"','0','jau','"+cmbLcNo.getItemCaption(cmbLcNo.getValue())+"',"
					+ "'1','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 2, '"+ sessionBean.getCompanyId()+"', "
					+ "'', '','','','ltrInformation')";
			session.createSQLQuery(debitHead).executeUpdate();

			String creditHead = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,"
					+ "costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill, attachChequeBill,skuId,skuName) "+
					" VALUES('"+voucherNo+"','"+dtfYMD.format(date.getValue())+"','"+
					ledgerId+"','Amount Credited Against LTR Liability','0','0"+amtLtrAmount.getValue().replace(",", "")+"','jau','"+cmbLcNo.getItemCaption(cmbLcNo.getValue())+"',"
					+ "'1','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 2, '"+ sessionBean.getCompanyId()+"', "
					+ "'', '','','','ltrInformation')";
			session.createSQLQuery(creditHead).executeUpdate();


			tx.commit();								
			ret = true;								
		}									
		catch(Exception exp)									
		{									
			tx.rollback();								
			showNotification("Error to save",exp+"",Notification.TYPE_ERROR_MESSAGE);								
		}									
		finally{session.close();}																			
		return ret;										
	}											
	private String getLtrId(Session session){
		String sql="select isnull(max(cast(ltrId as int)),0)+1 from tbLtrInformation";
		Iterator iter=session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext()){
			return iter.next().toString();
		}

		return "";
	}
	private boolean checkValidation(){

		if(cmbLcNo.getValue()!=null){
			if(!amtLcAmount.getValue().toString().isEmpty()){
				if(cmbGroup.getValue()!=null){
					if(cmbLtrNo.getValue()!=null){
						if(!amtLtrAmount.getValue().toString().isEmpty()){
							return true;
						}
						else{
							showNotification("Please Provide LTR amount",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else{
						showNotification("Please Provide LTR No",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Please Provide Group Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide LC Amount",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide LC No",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}


	/*public void groupLink()
	{
		Window win = new ProductionType(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				//cmbGroup();
				System.out.println("Group");
			}
		});
		this.getParent().addWindow(win);
	}

	public void subGroupLink()
	{
		Window win = new ProductionType(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				//cmbSubGroup();
				System.out.println("Sub Group");
			}
		});
		this.getParent().addWindow(win);
	}*/

	private void findButtonEvent() 
	{
		Window win = new LtrInformationFind(sessionBean, txtCategoryID);
		win.setStyleName("cwindow");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtCategoryID.getValue().toString().length() > 0)
				{
					txtClear();
					lcNoLoad();
					groupNameLoad();
					findInitialise(txtCategoryID.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtCategoryId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		isFind = true;

		try 
		{
			String sql = " SELECT ltrNo,ltrAmount,lcTransactionId,lcAmount,groupId,subGroupId,ltrLedgerId,voucherNo"
					+ " FROM dbo.tbLtrInformation Where ltrid = '"+txtCategoryId+"'";
			List<?> led = session.createSQLQuery(sql).list();
			System.out.println(sql);
			if(led.iterator().hasNext())
			{
				Object[] element = (Object[]) led.iterator().next();
			
				cmbGroup.setValue(element[4]);
				cmbSubGroup.setValue(element[5]);
				cmbLtrNo.setValue(element[0]);
				cmbLcNo.setValue(element[2]);
				amtLtrAmount.setValue(df.format(element[1]));
				amtLcAmount.setValue(df.format(element[3]));
				findLedgerId=element[6].toString();
				findVoucherNo=element[7].toString();
			}
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}


	private Iterator<?> dbService(String sql)		
	{		
		Iterator<?> iter=null;	
		Session session=null;	
		try{	
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		}	
		catch(Exception exp){	
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}	
		finally{	
			if(session!=null){
				session.close();
			}
		}	
		return iter;	
	}		


	private void refreshButtonEvent()
	{
		isFind = false;
		isUpdate = false;
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	/*private void saveButtonEvent() 
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(deleteData())
						{
							insertData();
							txtClear();
							componentIni(true);
							btnIni(true);
							button.btnNew.focus();
							isUpdate = false;
							showNotification("All Information update successfully.");
						}
					}
				}
			});		
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						btnIni(true);							
						componentIni(true);
						txtClear();
						button.btnNew.focus();
						showNotification("All Information Save successfully.");
					}
				}
			});		
		}
	}
	 */
	/*	private boolean deleteData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String sql = "delete from tbLcHeadInfo where headId like '"+txtHeadId.getValue()+"'";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{session.close();}
	}*/

	/*private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String insertQuery = "Insert Into tbLcHeadInfo(headId,headName,isActive,userName,userIp,entryTime) values ("+
					" '"+headId()+"',"+
					" '"+txtHeadName.getValue()+"',"+
					" '"+(RadioBtnGroup.getValue().toString().equals("Yes")?"1":"0")+"',"+
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
			session.createSQLQuery(insertQuery).executeUpdate();
			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	 */
	/*private void updateButtonEvent()
	{
		if(!txtHeadId.getValue().toString().isEmpty())
		{
			isUpdate = true;
			isFind = false;
			btnIni(false);
			componentIni(false);
		}
		else
		{
			showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}*/

	private void newButtonEvent()
	{
		isFind = false;
		isUpdate=false;
		findLedgerId="";
		findVoucherNo="";
		componentIni(false);
		btnIni(false);
		txtClear();
		//txtHeadId.setValue(headId());
		//txtHeadName.focus();
		lcNoLoad();
		groupNameLoad();
	}

	/*	public String headId()
	{
		String ret = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "SELECT ISNULL((MAX(CAST(SUBSTRING(headId,3,50) AS INT))+1),1)  FROM tbLcHeadInfo";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				ret = "H-"+iter.next().toString();
			}
		}
		catch(Exception ex)
		{
			showNotification("Error",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}*/

	public void txtClear()
	{	
		date.setValue(new java.util.Date());
		cmbLcNo.setValue(null);
		amtLcAmount.setValue("");
		cmbLtrNo.setValue(null);
		amtLtrAmount.setValue("");
		cmbGroup.setValue(null);
		cmbSubGroup.setValue(null);


	}

	private void componentIni(boolean b) 
	{	
		date.setEnabled(!b);		
		cmbLcNo.setEnabled(!b);
		amtLcAmount.setEnabled(!b);
		cmbLtrNo.setEnabled(!b);
		amtLtrAmount.setEnabled(!b);
		cmbGroup.setEnabled(!b);
		cmbSubGroup.setEnabled(!b);
		//nbGroup.setEnabled(!b);
		//nbSubGroup.setEnabled(!b);

	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnFind.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("540px");
		setHeight("350px");

		lblDate = new Label("Date :");
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");

		date = new DateField();
		date.setValue(new java.util.Date());
		date.setWidth("110px");
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);

		lblLcNo = new Label("L/C No :");
		lblLcNo.setImmediate(true);
		lblLcNo.setWidth("-1px");
		lblLcNo.setHeight("-1px");		

		cmbLcNo = new ComboBox();
		cmbLcNo.setImmediate(true);
		cmbLcNo.setWidth("210px");
		cmbLcNo.setHeight("24px");
		cmbLcNo.setNullSelectionAllowed(true);
		cmbLcNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblamtLcAmount = new Label("L/C Amount (Dr.) :");
		lblamtLcAmount.setWidth("-1px");
		lblamtLcAmount.setHeight("-1px");
		lblamtLcAmount.setImmediate(false);

		amtLcAmount = new AmountCommaSeperator();
		amtLcAmount.setWidth("130px");
		amtLcAmount.setHeight("24px");
		amtLcAmount.setImmediate(true);
		//amtLcAmount.setMaxLength(2);

		lblGroup = new Label("Group :");
		lblGroup.setImmediate(true);
		lblGroup.setWidth("-1px");
		lblGroup.setHeight("-1px");		

		cmbGroup = new ComboBox();
		cmbGroup.setImmediate(true);
		cmbGroup.setWidth("230px");
		cmbGroup.setHeight("24px");
		cmbGroup.setNullSelectionAllowed(true);
		cmbGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		nbGroup = new NativeButton();
		nbGroup.setIcon(new ThemeResource("../icons/add.png"));
		nbGroup.setImmediate(true);
		nbGroup.setWidth("35px");
		nbGroup.setHeight("24px");

		lblSubGroup = new Label("Sub Group :");
		lblSubGroup.setImmediate(true);
		lblSubGroup.setWidth("-1px");
		lblSubGroup.setHeight("-1px");		

		cmbSubGroup = new ComboBox();
		cmbSubGroup.setImmediate(true);
		cmbSubGroup.setWidth("230px");
		cmbSubGroup.setHeight("24px");
		cmbSubGroup.setNullSelectionAllowed(true);
		cmbSubGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		nbSubGroup = new NativeButton();
		nbSubGroup.setIcon(new ThemeResource("../icons/add.png"));
		nbSubGroup.setImmediate(true);
		nbSubGroup.setWidth("35px");
		nbSubGroup.setHeight("24px");


		lblLtrNo= new Label("LTR No :");
		lblLtrNo.setImmediate(false);
		lblLtrNo.setWidth("-1px");
		lblLtrNo.setHeight("-1px");

		cmbLtrNo = new ComboBox();
		cmbLtrNo.setImmediate(false);
		cmbLtrNo.setWidth("230px");
		cmbLtrNo.setHeight("24px");
		cmbLtrNo.setNullSelectionAllowed(true);
		cmbLtrNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblamtLtrAmount = new Label("LTR Amount (Cr.) :");
		lblamtLtrAmount.setWidth("-1px");
		lblamtLtrAmount.setHeight("-1px");
		lblamtLtrAmount.setImmediate(false);

		amtLtrAmount = new AmountCommaSeperator();
		amtLtrAmount.setWidth("130px");
		amtLtrAmount.setHeight("24px");
		amtLtrAmount.setImmediate(true);
		//amtLtrAmount.setMaxLength(2);

		mainLayout.addComponent(lblDate, "top:30.0px;left:45.0px;");
		mainLayout.addComponent(date, "top:28.0px;left:160.0px;");

		mainLayout.addComponent(lblLcNo, "top:55.0px;left:45.0px;");
		mainLayout.addComponent(cmbLcNo, "top:53.0px;left:160.0px;");

		mainLayout.addComponent(lblamtLcAmount, "top:80.0px;left:45.0px;");
		mainLayout.addComponent(amtLcAmount, "top:78.0px;left:160.0px;");

		mainLayout.addComponent(lblGroup, "top:105.0px;left:45.0px;");
		mainLayout.addComponent(cmbGroup, "top:103.0px;left:160.0px;");
		//mainLayout.addComponent(nbGroup,"top:103.0px;left:400.0px;");

		mainLayout.addComponent(lblSubGroup, "top:130.0px;left:45.0px;");
		mainLayout.addComponent(cmbSubGroup, "top:128.0px;left:160.0px;");
		//mainLayout.addComponent(nbSubGroup,"top:128.0px;left:400.0px;");

		mainLayout.addComponent(lblLtrNo, "top:155.0px;left:45.0px;");
		mainLayout.addComponent(cmbLtrNo, "top:153.0px;left:160.0px;");

		mainLayout.addComponent(lblamtLtrAmount, "top:180.0px;left:45.0px;");
		mainLayout.addComponent(amtLtrAmount, "top:178.0px;left:160.0px;");

		mainLayout.addComponent(button, "top:220.0px;left:12.0px;");

		return mainLayout;
	}
}