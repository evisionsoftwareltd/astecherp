package acc.appform.FinishedGoodsModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommonButton;
import com.common.share.FileUpload;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.rawMaterialSetup.RawItemCategory;
import com.example.rawMaterialSetup.RawItemInfoFind;
import com.example.rawMaterialSetup.RawItemSubCategory;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class ThirdPartyItemInfo extends Window 
{
	private TextField txtTransectionId = new TextField();
	private AbsoluteLayout mainLayout;


	private Label lblDate;
	private PopupDateField dDate;
	
	private Label lblLabelCode;
	private TextRead txtLabelCode;

	private Label lblItemCode;
	private TextField txtItemCode;
	
	private Label lblLabelName;
	private TextField txtLabelName;
	
	private Label lblsourceName;
	private TextField txtsourceName;

	private Label lblPartyId;
	private ComboBox cmbPartyName;	

	private Label lblStatus;
	private ComboBox cmbStatus;

	private static final String[] Type = new String[] { "Active", "Inactive" };
	
	String unit1[]={"Pcs", "No", "Bag", "gm", "Kgs", "Lbs", "Ton", "Ltr", "Ft", "Cft", "Rft", "Mtr", "Cubic Mtr", "Inch", "Watt", "K. Watt"};
	
	private Label lblUnit;
	private ComboBox cmbUnit;

	private Label lblSize;
	
	private AmountCommaSeperator txtSize;
	
	private Label lblMaxLavel;
	private AmountCommaSeperator txtMaxLabel;

	private Label lblMinLavel;
	private AmountCommaSeperator txtMinLabel;

	private Label lblRate;
	private AmountCommaSeperator txtRate;

	private Label lblRefill;
	private AmountCommaSeperator txtRefill;

	/*private Label lblSourceName;
	private ComboBox cmbSourceName;*/
	
	private ComboBox cmbSourceName;
	
	private Label lblSource=new Label();

	private DecimalFormat decimaldotf = new DecimalFormat("#0.00");
	
	private DecimalFormat dacimalf = new DecimalFormat("#0");

	String RawItemId="";
	private String findUpdateRawItemId="";
	String LedgerId="";
	TextField txtLedger=new TextField();

	boolean isUpdate=false;
	boolean isFind=false;
	int index;
	
	SessionBean sessionBean;
	
	ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	
	private SimpleDateFormat dateYear=new SimpleDateFormat("yyyy");

	private SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yyyy");
	
	private DecimalFormat df = new DecimalFormat("#0.00");
	
	private DecimalFormat dintformat = new DecimalFormat("#0");
	
	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	TextField txtTransection=new TextField();
	public ThirdPartyItemInfo(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("THIRD PARTY ITEM INFORMATION :: "+sessionBean.getCompany());
		this.setWidth("650px");
		this.setHeight("520px");
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		cmbPartyNameLoad();
		setEventAction();
		focusEnter();
		authenticationCheck();
		//cmbSourceName();
	}
	public void cmbSourceName(String partyId)
	{
		cmbSourceName.removeAllItems();
		try
		{
			cmbSourceName.removeAllItems();
			String sql="select iSourceID,vSourceName from tbSourceInfo where vPartyID like '"+partyId+"'";
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbSourceName.addItem(element[0].toString());
				cmbSourceName.setItemCaption(element[0].toString(), element[1].toString());
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
	public void cmbPartyNameLoad()
	{
		cmbPartyName.removeAllItems();
		try
		{
			String sql="select distinct partyCode,partyName from tbPartyInfo where isActive like '1'";
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void setEventAction()
	{
		cmbPartyName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbPartyName.getValue()!=null&&!isFind){
					cmbSourceName(cmbPartyName.getValue().toString());
				}
			}
		});
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind=false;
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
			public void buttonClick(ClickEvent event)
			{   
				isFind=true;
				findButtonEvent();
			}
		});

		txtLabelName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				Transaction tx = null;
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				try
				{
					if(!txtLabelName.getValue().toString().isEmpty())
					{
						String query = "select * from tbRawItemInfo where vRawItemName like '"+txtLabelName.getValue().toString()+"'";

						Iterator iter = session.createSQLQuery(query).list().iterator();

						if (iter.hasNext())
						{

							if(!isFind)
							{
								showNotification("Item Name Already Exists :"+txtLabelName.getValue().toString());
								txtLabelName.setValue("");
							}
							else
							{
								System.out.println("This Is Update");	
							}
						}	
					}
				}
				catch(Exception ex)
				{
					System.out.print(ex);	
				}

			}
		});

		txtRefill.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtRefill.getValue().toString().isEmpty())
				{

					double minimumlabel=0;
					double maxLabel=0;
					double relebel=0;

					minimumlabel=Double.parseDouble(txtMinLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMinLabel.getValue().toString().replaceAll(",", ""));
					maxLabel=Double.parseDouble(txtMaxLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMaxLabel.getValue().toString().replaceAll(",", ""));
					relebel=Double.parseDouble(txtRefill.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtRefill.getValue().toString().replaceAll(",", ""));

					if(relebel==0)
					{
						showNotification("Reorder Lebel Must be greater than Zerro",Notification.TYPE_WARNING_MESSAGE);
						txtRefill.setValue("");	
					}

					else if(relebel<minimumlabel)
					{
						showNotification("Reorder Lebel Must be greater than mimimum Label",Notification.TYPE_WARNING_MESSAGE);
						txtRefill.setValue("");
					}

					else if(relebel>maxLabel)
					{
						showNotification("Reorder Lebel Must be Less than Maximum Label",Notification.TYPE_WARNING_MESSAGE);
						txtRefill.setValue("");
					}
					else
					{

					}


				}

			}
		});

		txtMinLabel.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtMinLabel.getValue().toString().isEmpty())
				{

					double minimumlabel=0;
					double maxLabel=0;
					double relebel=0;

					minimumlabel=Double.parseDouble(txtMinLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMinLabel.getValue().toString().replaceAll(",", ""));
					maxLabel=Double.parseDouble(txtMaxLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMaxLabel.getValue().toString().replaceAll(",", ""));
					relebel=Double.parseDouble(txtRefill.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtRefill.getValue().toString().replaceAll(",", ""));

					if(minimumlabel==0)
					{
						showNotification("Minimum Lebel Must be greater than Zerro",Notification.TYPE_WARNING_MESSAGE);
						txtMinLabel.setValue("");	
					}
					else if(minimumlabel>relebel)
					{
						showNotification("Minimum Lebel Must be Less than Reorder Label",Notification.TYPE_WARNING_MESSAGE);
						txtMinLabel.setValue("");
					}

					else if(minimumlabel>maxLabel)
					{
						showNotification("Minimum Lebel Must be Less than Maximum Label",Notification.TYPE_WARNING_MESSAGE);
						txtMinLabel.setValue("");
					}
					else
					{

					}
				}
			}
		});


		txtMaxLabel.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtMaxLabel.getValue().toString().isEmpty())
				{

					double minimumlabel=0;
					double maxLabel=0;
					double relebel=0;

					minimumlabel=Double.parseDouble(txtMinLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMinLabel.getValue().toString().replaceAll(",", ""));
					maxLabel=Double.parseDouble(txtMaxLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMaxLabel.getValue().toString().replaceAll(",", ""));
					relebel=Double.parseDouble(txtRefill.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtRefill.getValue().toString().replaceAll(",", ""));
					if(maxLabel==0)
					{
						showNotification("Maximum Lebel Must be greater than Zerro",Notification.TYPE_WARNING_MESSAGE);
						txtMaxLabel.setValue("");	
					}

					else if(minimumlabel>maxLabel)
					{
						showNotification("Minimum Lebel Must be Less than Maxleb Label",Notification.TYPE_WARNING_MESSAGE);
						txtMaxLabel.setValue("");
					}

					else if(relebel>maxLabel)
					{
						showNotification("Reorder Lebel Must be Less than Maximum Label",Notification.TYPE_WARNING_MESSAGE);
						txtMaxLabel.setValue("");
					}
					else
					{

					}


				}

			}
		});
	}

	private String selectTransectionNo()
	{
		String TransectionNo = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query = "select isnull(MAX(iTransectionId),0)+1 id from tb3rdPartylabelInformation";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if (iter.hasNext())
			{
				TransectionNo = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return TransectionNo;
	}

	private String selectItemCode()
	{
		String RawItemCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select isnull(max(cast(SUBSTRING(vLabelCode,,LEN(vLabelCode)) as int)),0)+1 id from tb3rdPartylabelInformation";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext())
			{
				RawItemCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return RawItemCode;
	}




	private void updateButtonEvent()
	{
		if(!txtLabelName.getValue().toString().isEmpty())
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
			if(!txtItemCode.getValue().toString().isEmpty())
			{
				if(!txtLabelName.getValue().toString().isEmpty())
				{
					if(cmbPartyName.getValue()!=null)
					{
						if(cmbUnit.getValue()!=null)
						{
							if(!txtMinLabel.getValue().toString().isEmpty())
							{
								if(!txtRefill.getValue().toString().isEmpty())
								{
									if(!txtMaxLabel.getValue().toString().isEmpty())
									{
										if(!cmbSourceName.getValue().toString().isEmpty())
										{
											if(!txtRate.getValue().toString().isEmpty())
											{
												if(!txtSize.getValue().toString().isEmpty())
												{
													saveButtonEvent();		
												}
												else
												{
													showNotification("Please, provide Size",Notification.TYPE_WARNING_MESSAGE);	
												}		
											}
											else
											{
												showNotification("Please, provide Rate",Notification.TYPE_WARNING_MESSAGE);	
											}		
										}
										else
										{
											showNotification("Please, provide Source",Notification.TYPE_WARNING_MESSAGE);	
										}	
									}
									else
									{
										showNotification("Please, provide Maximum Label Qty ",Notification.TYPE_WARNING_MESSAGE);	
									}
								}
								else
								{
									showNotification("Please, provide Refill Label Qty ",Notification.TYPE_WARNING_MESSAGE);	
								}
							}
							else
							{
								showNotification("Please, provide Minimum Label Qty ",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							getParent().showNotification("Warning","Please provide unit",Notification.TYPE_WARNING_MESSAGE);
							cmbUnit.focus();
						}
					}
					else
					{
						getParent().showNotification("Warning","Please select PartyName",Notification.TYPE_WARNING_MESSAGE);
						cmbPartyName.focus();
					}
				}
				else
				{
					getParent().showNotification("Warning","Please provide Item name",Notification.TYPE_WARNING_MESSAGE);
					txtLabelName.focus();
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
		Window win = new ThirdPartyItemInfoFind(sessionBean, txtTransectionId);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtTransectionId.getValue().toString().length() > 0)
				{
					txtClear();
					cmbSourceName("%");
					findInitialise(txtTransectionId.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtTransectionId) 
	{
		String sql = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			sql = " select iTransectionId, dDate, vLabelCode, vLabelName, vSource,vPartyId,"
					+ " vUnit, mMax, "
					+ " mMin, mRefill,vLedgerId,mthirdPartyItemRate,isnull(vlabelNamesource,'')vlabelNamesource,vProductCode,vSizeName  from tb3rdPartylabelInformation "
					+ " where  iTransectionId like '"+txtTransectionId+"'";
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();
				txtTransection.setValue(element[0]);
				dDate.setValue(element[1]);
				txtItemCode.setValue(element[13].toString());
				txtLabelCode.setValue(element[2].toString());
				txtLabelName.setValue(element[3].toString());
				cmbSourceName.setValue(element[4].toString());
				cmbPartyName.setValue(element[5].toString());
				cmbUnit.setValue(element[6].toString());
				txtSize.setValue(element[14]);
				txtMaxLabel.setValue(dintformat.format(element[7]));
				txtRefill.setValue(dintformat.format(element[9]));
				txtMinLabel.setValue(dintformat.format(element[8]));
				txtLedger.setValue(element[10].toString());
				txtRate.setValue(df.format(element[11]));
				txtsourceName.setValue(element[12].toString());
				
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						Transaction tx = null;
						Session session = SessionFactoryUtil.getInstance().openSession();
						tx = session.beginTransaction();
						if(deleteData(session,tx)){
							if(insertData(session,tx)){
								isUpdate = false;
								isFind=false;
								btnIni(true);
								componentIni(true);
								txtClear();
								button.btnNew.focus();
							}
						}
						mb.close();
					}
				}
			});																	
		}
		else
		{									
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						Transaction tx = null;
						Session session = SessionFactoryUtil.getInstance().openSession();
						tx = session.beginTransaction();
						mb.buttonLayout.getComponent(0).setEnabled(false);
						if(insertData(session, tx)){
							isUpdate = false;
							isFind=false;
							btnIni(true);
							componentIni(true);
							txtClear();
							button.btnNew.focus();
						}
						mb.close();
					}
				}
			});
		}
	}
	public boolean deleteData(Session session,Transaction tx){
		try {
			String sqlUd="insert into tbUd3rdPartylabelInformation select iTransectionId, "
					+ "dDate, vLabelCode, vLabelName, vSource, vPartyId, vPartyName, vUnit,vUserIp, "
					+ "vUserId, vUserName, dEntryTime, mMax, mMin, mRefill,isActive,'Update',vLedgerId,"
					+ "mthirdPartyItemRate,vSourceName,vlabelNamesource,vProductCode,vSizeName from tb3rdPartylabelInformation "
					+ " where iTransectionId like '"+txtTransection.getValue()+"'";
			System.out.println("deleteInsert : "+sqlUd);
			session.createSQLQuery(sqlUd).executeUpdate();

			String sqlDelete="delete from tb3rdPartylabelInformation where iTransectionId like '"+txtTransection.getValue()+"'";

			System.out.println("delete : "+sqlDelete);
			session.createSQLQuery(sqlDelete).executeUpdate();
			return true;
		}
		catch (Exception e) {
			showNotification("DeleteData",null,Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}
	
	private Iterator<?> dbService(String sql){

		System.out.println(sql);
		Session session=null;
		Iterator<?> iter=null;
		try {
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		} 
		catch (Exception e) {
			showNotification(null,""+e,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}

	public String ledgerId()
	{
		
		String ledgerId = "";
		String query = " Select cast(isnull(max(cast(replace(Ledger_Id, 'IL', '')as int))+1, 1)as varchar) from tbLedger" +
				" where Ledger_Id like 'IL%' ";
		Iterator iter=dbService(query);
		if (iter.hasNext()) 
		{
			ledgerId = "IL"+iter.next().toString();
		}
		return ledgerId;
	}


	private boolean insertData(Session session,Transaction tx)
	{
		try
		{
			String TransecId="";
			String parentId="";
			String createForm="";
			////////////Transection Id
			String query = "select isnull(MAX(iTransectionId),0)+1 id from tb3rdPartylabelInformation";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if (iter.hasNext())
			{
				TransecId = iter.next().toString();
			}
			
			//////partyLadger
			String queryLedger="select vGroupId from tbPartyInfo where partyCode like '"+cmbPartyName.getValue().toString()+"'"; 
			System.out.println(queryLedger);
			Iterator<?> iterLedger = session.createSQLQuery(queryLedger).list().iterator();
			if (iterLedger.hasNext()) 
			{
				String str=iterLedger.next().toString();
				parentId = str;
				createForm = "I1-"+str;
			}
			////////////
			////////////Ledger Id
			String Leger = " Select cast(isnull(max(cast(replace(Ledger_Id, 'IL', '')as int))+1, 1)as varchar) from tbLedger" +
					" where Ledger_Id like 'IL%' ";
			Iterator<?> iterLeger=session.createSQLQuery(Leger).list().iterator();
			if (iterLeger.hasNext()) 
			{
				LedgerId = "IL"+iterLeger.next().toString();
			}
			
			
			//LedgerId=ledgerId();
			
			String caption="Saved",PartyId="",PartyName="",Sourceid="",SourceName="";
			if(isUpdate)
			{
				TransecId=txtTransection.getValue().toString();
				caption="Updated";
				LedgerId=txtLedger.getValue().toString();
			}
			
			if(cmbPartyName.getValue()!=null){
				PartyId=cmbPartyName.getValue().toString();
				PartyName=cmbPartyName.getItemCaption(cmbPartyName.getValue().toString());
			}
			if(cmbSourceName.getValue()!=null){
				Sourceid=cmbSourceName.getValue().toString();
				SourceName=cmbSourceName.getItemCaption(cmbSourceName.getValue().toString());
			}
			
			String Insert = "insert into tb3rdPartylabelInformation (iTransectionId, dDate, vLabelCode,"
					+ " vLabelName, vSource, vPartyId, vPartyName, vUnit, vUserIp, vUserId, vUserName, "
					+ "dEntryTime, mMax, mMin, mRefill,isActive,vLedgerId,mthirdPartyItemRate,vSourceName,"
					+ "vlabelNamesource,vProductCode,vSizeName) "
					+ "values('"+TransecId+"','"+dateFormat.format(dDate.getValue())+"', "
					+ "'"+txtLabelCode.getValue().toString()+"','"+txtLabelName.getValue().toString()+"',"
					+ "'"+Sourceid+"','"+PartyId+"','"+PartyName+"',"
					+ "'"+cmbUnit.getValue().toString()+"',"
					+ "'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',"
					+ "'"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP, "
					+ "'"+txtMaxLabel.getValue()+"','"+txtMinLabel.getValue()+"',"
					+ "'"+txtRefill.getValue()+"','"+cmbStatus.getValue().toString()+"','"+LedgerId+"',"
					+ "'"+txtRate.getValue()+"','"+SourceName+"','"+txtsourceName.getValue().toString()+"',"
					+ "'"+txtItemCode.getValue().toString()+"','"+txtSize.getValue().toString()+"')";
			session.createSQLQuery(Insert).executeUpdate();
			if(isUpdate){
				String InsertUd = "insert into tbUd3rdPartylabelInformation (iTransectionId, dDate, vLabelCode,"
						+ " vLabelName, vSource, vPartyId, vPartyName, vUnit, vUserIp, vUserId, vUserName, "
						+ "dEntryTime, mMax, mMin, mRefill,isActive,vUdFlag,vLedgerId,mthirdPartyItemRate,vSourceName,"
						+ "vlabelNamesource,vProductCode,vSizeName) "
						+ "values('"+TransecId+"','"+dateFormat.format(dDate.getValue())+"', "
						+ "'"+txtLabelCode.getValue().toString()+"','"+txtLabelName.getValue().toString()+"',"
						+ "'"+SourceName+"','"+PartyId+"','"+PartyName+"',"
						+ "'"+cmbUnit.getValue().toString()+"',"
						+ "'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',"
						+ "'"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP, "
						+ "'"+txtMaxLabel.getValue()+"','"+txtMinLabel.getValue()+"',"
						+ "'"+txtRefill.getValue()+"','"+cmbStatus.getValue().toString()+"','New',"
						+ "'"+LedgerId+"','"+txtRate.getValue()+"','"+SourceName+"',"
						+ "'"+txtsourceName.getValue().toString()+"','"+txtItemCode.getValue().toString()+"',"
						+ "'"+txtSize.getValue().toString()+"')";
				session.createSQLQuery(InsertUd).executeUpdate();
			}
			if(!isUpdate){
				String InsertLedger="INSERT into tbLedger values(" +
						" '"+LedgerId+"', " +
						" '"+txtLabelName.getValue().toString().trim()+"', " +
						" '"+dateYear.format(sessionBean.getFiscalOpenDate())+"', " +
						" '"+parentId+"', " +
						" '"+createForm+"', " +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
						" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

				session.createSQLQuery(InsertLedger).executeUpdate();

				String LedgerOpen="INSERT into tbLedger_Op_Balance values(" +
						" '"+LedgerId+"', " +
						" '0.00', " +
						" '0.00', " +
						" '"+dateYear.format(sessionBean.getFiscalOpenDate())+"', " +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
						" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";
				session.createSQLQuery(LedgerOpen).executeUpdate();
			}
			tx.commit();
			showNotification("All Information "+caption+" Successfully!",null,Notification.TYPE_HUMANIZED_MESSAGE);
			return true;
		}
		catch(Exception exp)
		{
			if(tx!=null){
				tx.rollback();
			}
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return false;
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
		setItemCode();
		txtLabelName.focus();
	}
	
	private String setItemCode(){
		String autoId=null;
		try{
			String sql="Select isnull(max(cast(vLabelCode as int) ) ,0)+1 id  from tb3rdPartylabelInformation";
			Iterator<?> iter=dbService(sql);
			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
				txtLabelCode.setValue(autoId);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
		return autoId;
	}
	
	private void focusEnter()
	{
		allComp.add(dDate);
		allComp.add(txtItemCode);
		allComp.add(txtLabelName);
		allComp.add(cmbSourceName);
		allComp.add(cmbPartyName);
		allComp.add(cmbUnit);
		allComp.add(txtRate);
		allComp.add(txtMaxLabel);
		allComp.add(txtMinLabel);
		allComp.add(txtRefill);
		allComp.add(button.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	public void txtClear()
	{
		txtRate.setValue("");
		txtItemCode.setValue("");
		txtLabelCode.setValue("");
		txtLabelName.setValue("");
		cmbPartyName.setValue(null);
		cmbSourceName.setValue(null);
		cmbUnit.setValue(null);
		txtMaxLabel.setValue("");
		txtMinLabel.setValue("");
		txtRefill.setValue("");
		cmbStatus.setValue(1);
		txtTransection.setValue(selectTransectionNo());
		txtLedger.setValue("");
		txtsourceName.setValue("");
		txtSize.setValue("");
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
		txtItemCode.setEnabled(!b);
		txtLabelCode.setEnabled(!b);
		txtLabelName.setEnabled(!b);
		cmbPartyName.setEnabled(!b);
		cmbSourceName.setEnabled(!b);
		cmbUnit.setEnabled(!b);
		txtMaxLabel.setEnabled(!b);
		txtMinLabel.setEnabled(!b);
		txtRefill.setEnabled(!b);
		cmbStatus.setEnabled(!b);
		txtRate.setEnabled(!b);
		txtsourceName.setEnabled(!b);
		txtSize.setEnabled(!b);
	}

	private AbsoluteLayout buildMainLayout()
	{

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);

		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		lblDate = new Label("Date: ");
		lblDate.setImmediate(true);
		lblDate.setWidth("100.0%");
		lblDate.setHeight("18px");
		mainLayout.addComponent(lblDate,"top:20.0px;left:50.0px;");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setHeight("24px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setValue(new java.util.Date());
		dDate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(dDate, "top:18.0px;left:230.0px;");

		lblLabelCode = new Label("Code: ");
		lblLabelCode.setImmediate(true);
		lblLabelCode.setWidth("100.0%");
		lblLabelCode.setHeight("18px");
		mainLayout.addComponent(lblLabelCode,"top:50.0px;left:50.0px;");
		lblLabelCode.setVisible(false);
		
		
		lblItemCode = new Label("Code: ");
		lblItemCode.setImmediate(true);
		lblItemCode.setWidth("100.0%");
		lblItemCode.setHeight("18px");
		mainLayout.addComponent(lblItemCode,"top:50.0px;left:50.0px;");
		
		
		txtLabelCode = new TextRead();
		txtLabelCode.setImmediate(false);
		txtLabelCode.setWidth("100px");
		txtLabelCode.setHeight("23px");
		mainLayout.addComponent(txtLabelCode, "top:48.0px;left:230.0px;");
		txtLabelCode.setVisible(false);
		
		txtItemCode = new TextField();
		txtItemCode.setImmediate(false);
		txtItemCode.setWidth("100px");
		txtItemCode.setHeight("23px");
		mainLayout.addComponent(txtItemCode, "top:48.0px;left:230.0px;");
		
		
		//lblLabelName = new Label("Name(Party): ");
		lblLabelName = new Label("Item Name (Delivery stage) : ");
		lblLabelName.setImmediate(false);
		lblLabelName.setWidth("-1px");
		lblLabelName.setHeight("-1px");
		mainLayout.addComponent(lblLabelName, " top:80.0px;left:50.0px;");

		txtLabelName = new TextField();
		txtLabelName.setImmediate(false);
		txtLabelName.setWidth("380px");
		txtLabelName.setHeight("-1px");
		txtLabelName.setSecret(false);
		mainLayout.addComponent(txtLabelName, "top:78.0px;left:230.0px;");
		
		//lblsourceName = new Label("Name(Source): ");
		lblsourceName = new Label("Item Name (Receiving Stage) : ");
		lblsourceName.setImmediate(false);
		lblsourceName.setWidth("-1px");
		lblsourceName.setHeight("-1px");
		mainLayout.addComponent(lblsourceName, " top:110.0px;left:50.0px;");
		
		txtsourceName = new TextField();
		txtsourceName.setImmediate(false);
		txtsourceName.setWidth("380px");
		txtsourceName.setHeight("-1px");
		txtsourceName.setSecret(false);
		mainLayout.addComponent(txtsourceName, "top:108.0px;left:230.0px;");

		
		//lblPartyId = new Label("Party Name ");
		lblPartyId = new Label("Owner Name (Party) :");
		lblPartyId.setImmediate(false);
		lblPartyId.setWidth("-1px");
		lblPartyId.setHeight("-1px");
		mainLayout.addComponent(lblPartyId, "top:140.0px;left:50.0px;");
		
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("318px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbPartyName, "top:138.0px;left:230.0px;");
		
		
		//lblSource = new Label("Source :");
		lblSource = new Label("Supplier Name (Source) :");
		lblSource.setImmediate(false);
		lblSource.setWidth("-1px");
		lblSource.setHeight("-1px");
		mainLayout.addComponent(lblSource, "top:170.0px;left:50.0px;");

		cmbSourceName = new ComboBox();
		cmbSourceName.setImmediate(false);
		cmbSourceName.setWidth("318px");
		cmbSourceName.setHeight("-1px");
		cmbSourceName.setNullSelectionAllowed(true);
		cmbSourceName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSourceName, "top:168.0px;left:230.0px;");

		lblUnit = new Label("Unit :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:200.0px;left:50.0px;");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(false);
		cmbUnit.setWidth("100px");
		cmbUnit.setHeight("-1px");
		mainLayout.addComponent(cmbUnit, "top:198.0px;left:230.0px;");
		
		for(int i=0;i<unit1.length;i++)
		{
			cmbUnit.addItem(unit1[i]);	
		}
		
		lblSize = new Label("Size :");
		lblSize.setImmediate(false);
		lblSize.setWidth("-1px");
		lblSize.setHeight("-1px");
		mainLayout.addComponent(lblSize,"top:230.0px;left:50.0px;");
		
		txtSize = new AmountCommaSeperator();
		txtSize.setImmediate(false);
		txtSize.setWidth("100px");
		txtSize.setHeight("-1px");
		mainLayout.addComponent(txtSize, "top:228.0px;left:230.0px;");
		
		lblRate = new Label("Rate :");
		lblRate.setImmediate(false);
		lblRate.setWidth("-1px");
		lblRate.setHeight("-1px");
		mainLayout.addComponent(lblRate,"top:260.0px;left:50.0px;");
		
		txtRate = new AmountCommaSeperator();
		txtRate.setImmediate(false);
		txtRate.setWidth("100px");
		txtRate.setHeight("-1px");
		txtRate.setSecret(false);
		mainLayout.addComponent(txtRate, "top:258.0px;left:230.0px;");

		lblMaxLavel = new Label("Max Level :");
		lblMaxLavel.setImmediate(false);
		lblMaxLavel.setWidth("-1px");
		lblMaxLavel.setHeight("-1px");
		mainLayout.addComponent(lblMaxLavel, "top:290.0px;left:50.0px;");
		// cmbUnit
		txtMaxLabel = new AmountCommaSeperator();
		txtMaxLabel.setImmediate(false);
		txtMaxLabel.setWidth("100px");
		txtMaxLabel.setHeight("-1px");
		txtMaxLabel.setSecret(false);
		mainLayout.addComponent(txtMaxLabel, "top:288.0px;left:230.0px;");


		lblRefill = new Label("Refill Level :");
		lblRefill.setImmediate(false);
		lblRefill.setWidth("-1px");
		lblRefill.setHeight("-1px");
		mainLayout.addComponent(lblRefill, "top:320.0px;left:50.0px;");


		txtRefill = new AmountCommaSeperator();
		txtRefill.setImmediate(false);
		txtRefill.setWidth("100px");
		txtRefill.setHeight("-1px");
		txtRefill.setSecret(false);
		mainLayout.addComponent(txtRefill, "top:318.0px;left:230.0px;");

		
		lblMinLavel = new Label("Min Level :");
		lblMinLavel.setImmediate(false);
		lblMinLavel.setWidth("-1px");
		lblMinLavel.setHeight("-1px");
		mainLayout.addComponent(lblMinLavel, "top:350.0px;left:50.0px;");

		txtMinLabel = new AmountCommaSeperator();
		txtMinLabel.setImmediate(false);
		txtMinLabel.setWidth("100px");
		txtMinLabel.setHeight("-1px");
		txtMinLabel.setSecret(false);
		mainLayout.addComponent(txtMinLabel, "top:348.0px;left:230.0px;");

		lblStatus = new Label("Status :");
		lblStatus.setImmediate(false);
		lblStatus.setWidth("-1px");
		lblStatus.setHeight("-1px");
		mainLayout.addComponent(lblStatus, "top:380.0px;left:50.0px;");

		cmbStatus = new ComboBox();
		cmbStatus.setImmediate(true);
		cmbStatus.setWidth("110px");
		cmbStatus.setHeight("-1px");
		cmbStatus.setFilteringMode(cmbStatus.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbStatus, "top:378.0px; left:230.0px;");
		cmbStatus.setNullSelectionAllowed(false);
		
		cmbStatus.addItem(0);
		cmbStatus.setItemCaption(0,"Inactive");
		cmbStatus.addItem(1);
		cmbStatus.setItemCaption(1,"Active");

		mainLayout.addComponent(button, "bottom:20.0px;left:55.0px;");

		return mainLayout;
	}
}
