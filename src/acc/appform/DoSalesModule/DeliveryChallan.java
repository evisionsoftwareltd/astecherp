package acc.appform.DoSalesModule;

import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;





import acc.appform.setupTransaction.DepoInformation;
import acc.appform.setupTransaction.PartyInformation;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
@SuppressWarnings("serial")
public class DeliveryChallan extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table = new Table();

	private TextField txtChallanNoFind = new TextField();

	private Label lblPartyName;
	private ComboBox cmbPartyName;

	private Label lblDepoName;
	private ComboBox cmbDepoName;

	private Label lblPartyAddress;
	private TextField txtPartyAddress;

	private Label lblPartyMobile;
	private TextField txtPartyMobile;

	private TextField txtPartyTinNo;
	private ComboBox cmbDestinationTinNo;
	private TextField txtDelChallanNo;
	private TextField  txtVatChallanNo;

	private Label lblChallanNo;
	private TextRead txtChallanNo;

	private Label lblVoucehrNo;
	private TextRead txtVoucherNo;

	private Label lblGatePassNo;
	private TextRead txtGatePassNo;

	private ComboBox cmbPONo;

	private ComboBox cmbDriverName;
	private ComboBox cmbDriverMobile;
	private ComboBox cmbTruckNo;
	private ComboBox cmbDestination;
	
    public HorizontalLayout hlt= new HorizontalLayout();
    

	private Label lblDate;
	private PopupDateField dDate;

	private TextField txtRemarks;

	private Table DOTable = new Table();

	//Table Value
	private ArrayList<CheckBox> partyTbchkDO = new ArrayList<CheckBox>();
	private ArrayList<Label> partyTblblDONo = new ArrayList<Label>();
	private ArrayList<Label> partyTblblDemnadDate = new ArrayList<Label>();
	private ArrayList<Label> partyTblblDOQty = new ArrayList<Label>();

	//Table Value
	private ArrayList<Label> tblblSelect = new ArrayList<Label>();
	private ArrayList<Label> tblblDO = new ArrayList<Label>();
	private ArrayList<Label> tblblDOMerge = new ArrayList<Label>();
	private ArrayList<TextRead> tblblDODate = new ArrayList<TextRead>();
	private ArrayList<ComboBox> tbcmbProCode = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> tbcmbLabel = new ArrayList<ComboBox>();

	private ArrayList<Label> tblblUnit = new ArrayList<Label>();
	private ArrayList<TextRead> tblblProductRate = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtDOQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtDeliveredQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtBalanceQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtStockQty = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> tbtxtQty = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> tbtxtAmount = new ArrayList<TextRead>();
	private ArrayList<TextField> tbtxtRemarks = new ArrayList<TextField>();
	private ArrayList<TextField> tbSize = new ArrayList<TextField>();

	private ArrayList<NativeButton> btnpreview = new ArrayList<NativeButton>();

	private ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat tbDOdateFormat = new SimpleDateFormat("dd-MM-yy");
	private SimpleDateFormat recDateFormat = new SimpleDateFormat("MM/yy");

	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete","Refresh","Find","","","","Exit");

	//private NativeButton btnGatePass = new NativeButton("Gate Pass");
	private NativeButton btnChallan = new NativeButton("Delivery Challan");
	private NativeButton btnVatChallan = new NativeButton("VAT Challan");

	private Boolean isUpdate = false;
	private Boolean isFind = false;

	private NativeButton btnParty;
	private NativeButton btnStore;

	private String DoNO = "";
	private String DoNOAll = "";

	private DecimalFormat decimal = new DecimalFormat("#0");
	private DecimalFormat dfRate = new DecimalFormat("#0.000");
	private DecimalFormat dfRate1 = new DecimalFormat("#0.0000");
	private DecimalFormat dfRateVoucher = new DecimalFormat("#0.00");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	String maxId = "0";
	String challanNo = "";
	String VATchallaanNo = "";
	String gatepassNo = "";
	int action = 0;

	private HashMap<Object, Object> hmProductId = new HashMap<Object, Object>();
	private HashMap<Object, Object> hmProductName = new HashMap<Object, Object>();
	private HashMap<Object, Object> hmProductUnit = new HashMap<Object, Object>();
	private HashMap<Object, Object> hmProductStockQty = new HashMap<Object, Object>();
	private HashMap<Object, Object> hmProductRate = new HashMap<Object, Object>();
	private HashMap<Object, Object> hmProductFlag = new HashMap<Object, Object>();

	private ReportDate reportTime = new ReportDate();
	TextField txtTransactionNo=new TextField();

	public DeliveryChallan(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("DELIVERY CHALLAN :: " + sessionBean.getCompany());

		buildMainLayout();		
		setContent(mainLayout);
		tableinitialise();
		componentIni(true);
		btnIni(true);
		setEventAction();
		focusEnter();
		authenticationCheck();
		DOTableinitialise();
		cmbPartyData();
		cmbDepoData();

		button.btnNew.focus();
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

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = false;
				componentIni(false);
				btnIni(false);
				txtClear();
				clearDOData();
				selectReceiptNo();
				//setVATChallanNo();
				cmbPartyName.focus();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(isFind || !txtChallanNo.getValue().toString().equals(""))
				{
					if(chkInvoiceNo())
					{
						setDoDisable(true);
						btnIni(false);

						isUpdate = true;

						if(partyTbchkDO.get(0).booleanValue()==true)
						{
							for(int i=0;i<tbcmbProCode.size();i++)
							{
								tbcmbProCode.get(i).setReadOnly(false);
								tbcmbProCode.get(i).setEnabled(true);
							}
						}
					}
					else
					{
						showNotification("Warning!","Invoice already generated.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning!","Please find data for edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtChallanNo.getValue().toString().equals(""))
				{
					if(sessionBean.isAdmin()||sessionBean.isSuperAdmin())
					{						
						if(chkInvoiceNo())
						{
							setDoDisable(true);
							btnIni(false);

							isUpdate = true;

							if(partyTbchkDO.get(0).booleanValue()==true)
							{
								for(int i=0;i<tbcmbProCode.size();i++)
								{
									tbcmbProCode.get(i).setReadOnly(false);
									tbcmbProCode.get(i).setEnabled(true);
								}
							}
						}
						else
						{
							showNotification("Warning!","Invoice already generated.",Notification.TYPE_WARNING_MESSAGE);
						}											
					}
					else
					{
						showNotification("Warning!","You have no authorization",Notification.TYPE_WARNING_MESSAGE);
					}					
				}
				else
				{
					showNotification("Warning!","There are nothing to delete",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		btnChallan.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbPartyName.getValue()!=null || !txtChallanNo.getValue().toString().equals(""))
				{
					previewChallanEvent();
				}
				else
				{
					showNotification("Warning!","Find a Challan No",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		btnVatChallan.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbPartyName.getValue()!=null || !txtChallanNo.getValue().toString().equals(""))
				{
					previewVatChallanEvent();
				}
				else
				{
					showNotification("Warning!","Find a Challan No",Notification.TYPE_WARNING_MESSAGE);
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

		button.btnRefresh.addListener( new Button.ClickListener()   
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				componentIni(true);
				btnIni(true);
				txtClear();
				clearDOData();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		
		
		/*hlt.addListener(new LayoutClickListener() {
			
			@Override
			public void layoutClick(LayoutClickEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Action Done");
				cmbPartyData();
			}
		});
		*/
		
		/*cmbPartyName.addListener(null, new FocusListener() {
			
			@Override
			public void focusLost(java.awt.event.FocusEvent arg0) {
				// TODO Auto-generated method stub
				cmbPartyData();
			}
			
			@Override
			public void focusGained(java.awt.event.FocusEvent arg0) {
				// TODO Auto-generated method stub
				
				cmbPartyData();
				
			}
		}, DoNO);*/
		
		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPartyName.getValue()!=null)
				{
					setPartyInfo();
					tableClear();
					clearDOData();

					addDestination();
					addTruck();
					addDriverName();
					addDriverMobile();

					selectReceiptNo();
					setDoNo();
					tableClear();
					for(int i=0; i<tbcmbProCode.size(); i++)
					{
						if(cmbPartyName.getValue()!=null)
						{
							addProductData(i);
						}
					}
				}
				else
				{
					txtPartyAddress.setValue("");
					txtPartyMobile.setValue("");
				}
			}
		});

		btnParty.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				partyData();
			}
		});

		btnStore.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				storeData();
			}
		});

		txtDelChallanNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtDelChallanNo.getValue().toString().trim().isEmpty())
				{
					if(checkDuplicate(1) && !isFind)
					{
						showNotification("Warning!","Delivery challan no. already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtDelChallanNo.setValue("");
					}
				}
			}
		});

		txtVatChallanNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtVatChallanNo.getValue().toString().trim().isEmpty())
				{
					if(checkDuplicate(2) && !isFind)
					{
						showNotification("Warning!","Vat challan no. already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtVatChallanNo.setValue("");
					}
				}
			}
		});
		cmbDestination.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDestination.getValue()!=null)
				{
					cmbDestinationTinNo.removeAllItems();
					destinationTINData();
					//showNotification("Warning!","TIN  no. already exist.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		
		
	}
	private void tbcmbLabelDataLoad(int index) {
	
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			
				String cmbProCode= tbcmbProCode.get(index).getValue().toString();
				
			
			String sql=
					"select semiFgSubId,semiFgSubName as cnt from tbSemiFgSubInformation "
					+"where semiFgId=(select a.semiFgId from tbFinishedProductDetailsNew a inner join tbFinishedProductInfo b on a.fgId=b.vProductId  where a.semiFgId!='' "
					+"and fgId='"+cmbProCode+"' and b.isFlag='YES' and a.consumptionStage='Delivery Challan' )  or  semiFgIdTwo=(select a.semiFgId from tbFinishedProductDetailsNew a inner join tbFinishedProductInfo b on a.fgId=b.vProductId  where a.semiFgId!='' "
					+"and fgId='"+cmbProCode+"' and b.isFlag='YES' and a.consumptionStage='Delivery Challan' ) or  semiFgIdThree=(select a.semiFgId from tbFinishedProductDetailsNew a inner join tbFinishedProductInfo b on a.fgId=b.vProductId  where a.semiFgId!='' "
					+"and fgId='"+cmbProCode+"' and b.isFlag='YES' and a.consumptionStage='Delivery Challan'  )  ";
			
			
			System.out.println("sql is:"+sql);
			
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			int count=0;
			
		
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();

				
				tbcmbLabel.get(index).addItem(element[0]);
				tbcmbLabel.get(index).setItemCaption(element[0], element[1].toString());
			   
			}
			
		}
		catch (Exception e)
		{
			showNotification("Error",e+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}		
		
		
	}

	private boolean LabelDatacheck(String productID) {
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		int autoVATchallan=0;
		
		try
		{
			
			String query="select [dbo].[NewLabelDeliveryChallan]('"+productID+"')";
			System.out.println("count check:"+query);
			Iterator iter =session.createSQLQuery(query).list().iterator();

			if(iter.hasNext())
			{
				autoVATchallan= Integer.parseInt(iter.next().toString()) ;
				
				if( autoVATchallan>0)
				{
					return true;
				}	
			}
			
		}
		catch (Exception e)
		{
			showNotification("Error",e+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return false;
	}
	
	
	
	private boolean chkInvoiceNo()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vBillNo from tbSalesInvoiceDetails where vChallanNo = '"+txtChallanNo.getValue().toString()+"'";
			Iterator<?> iterMax = session.createSQLQuery(sql).list().iterator();
			if(iterMax.hasNext())
			{
				return false;
			}
		}
		catch (Exception e)
		{
			showNotification("Error",e+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}		
		return true;
	}

	private boolean checkDuplicate(int flag)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			Iterator<?> iterMax = null;
			if(flag==1)
			{iterMax = session.createSQLQuery("select * from tbDeliveryChallanInfo where"
					+ " vDelChallanNo = '"+txtDelChallanNo.getValue().toString().trim()+"'").list().iterator();}
			if(flag==2)
			{iterMax = session.createSQLQuery("select * from tbDeliveryChallanInfo where"
					+ " vVatChallanNo = '"+txtVatChallanNo.getValue().toString().trim()+"'").list().iterator();}
			if(iterMax.hasNext())
			{
				return true;
			}
		}
		catch (Exception e)
		{
			showNotification("Error",e+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}		
		return false;
	}

	public void partyData()
	{
		Window win = new PartyInformation(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				cmbPartyData();
			}
		});
		this.getParent().addWindow(win);
	}

	public void storeData()
	{
		Window win = new DepoInformation(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				cmbDepoData();
			}
		});
		this.getParent().addWindow(win);
	}

	public void selectReceiptNo()
	{
		String Challan = "";
		String Gatepass = "";

		String partyId = "";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" select iAutoId,vReceiptPrefix from tbReceiptPrefixInfo where" +
					" iAutoId in ('2','3') ").list();
			Iterator<?> iterMax = session.createSQLQuery(" SELECT ISNULL((MAX(CAST(SUBSTRING(vChallanSerial,CHARINDEX('-'," +
					" vChallanSerial,9)+1,50) AS INT))+1),1) FROM tbDeliveryChallanInfo ").list().iterator();
			if(iterMax.hasNext())
				maxId = iterMax.next().toString();
			if(cmbPartyName.getValue()!=null)
			{
				/*Iterator<?> partyMax = session.createSQLQuery(" select COUNT(vChallanNo)+1 from tbDeliveryChallanInfo" +
						" where vPartyId = '"+cmbPartyName.getValue().toString()+"' ").list().iterator();*/

				Iterator<?> partyMax =session.createSQLQuery("SElect ISNULL(MAX(CAST(REVERSE(Substring(Reverse(vChallanNo),0, "
						+ "CHARINDEX('/',Reverse(vChallanNo))))as int)),0)+1 from tbDeliveryChallanInfo where vPartyId='"+cmbPartyName.getValue().toString()+"' ").list().iterator(); 

				if(partyMax.hasNext())
					partyId = "/"+Integer.valueOf(partyMax.next().toString()).toString();
			}

			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				if(element[0].toString().equals("2"))
					Challan = element[1].toString();
				if(element[0].toString().equals("3"))
					Gatepass = element[1].toString();
			}
		}
		catch (Exception e)
		{

		}

		txtChallanNo.setValue(Challan+"-"+recDateFormat.format(new Date())+"-"+maxId+partyId);
		txtGatePassNo.setValue(Gatepass+"-"+recDateFormat.format(new Date())+"-"+maxId+partyId);

		challanNo = Challan+"-"+recDateFormat.format(new Date())+"-"+maxId+partyId;
		gatepassNo = Gatepass+"-"+recDateFormat.format(new Date())+"-"+maxId+partyId;
	}

	private void setVATChallanNo()
	{
		String autoVATchallan="";
		Transaction tx=null;
		try
		{

			Session session =SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String query="Select ISNULL(MAX(CAST (REPLACE(vVatChallanNo,'.','') as int))+1,'') from tbDeliveryChallanInfo --where dEntryTime >'"+dFormat.format(dDate.getValue())+"'";
			Iterator iter =session.createSQLQuery(query).list().iterator();

			if(iter.hasNext())
			{
				autoVATchallan = iter.next().toString();
			}
		}
		catch(Exception ex)
		{

		}
		txtVatChallanNo.setValue(autoVATchallan);

		VATchallaanNo=autoVATchallan;
	}
	private void setDoNo()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery("select di.doNo,di.doDate,SUM(dd.qty) qty, ISNULL((select SUM(dcd.mChallanQty)"
					+ " from tbDeliveryChallanDetails dcd where dcd.vDoNo = di.doNo),0) challanQty from tbDemandOrderInfo di"
					+ " inner join tbDemandOrderDetails dd on di.doNo = dd.doNo where partyId = '"+cmbPartyName.getValue().toString()+"' and ISNULL(di.vFlag,'') != 'Cancel' "
					+ " /*and di.iApproveFlag = 1*/and di.vStatus not like 'Inactive' group by di.doNo,di.doDate having (SUM(dd.qty)-ISNULL((select SUM(dcd.mChallanQty)"
					+ " from tbDeliveryChallanDetails dcd where dcd.vDoNo = di.doNo),0))>5 order by di.doDate asc ").list();
			
			int i = 1;
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				partyTblblDONo.get(i).setValue(element[0].toString());
				partyTblblDemnadDate.get(i).setValue(tbDOdateFormat.format(element[1]));
				partyTblblDOQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[2].toString())));
				if(i==partyTblblDONo.size()-1)
				{
					DOTableRowAdd(i+1);
				}
				i++;
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setDoData()
	{
		try
		{  
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String sql=" SELECT vDoNo,vDoNoMerge,dDoDate,vProductId,mDoQty,mDelQty,mBalQty," +
					" mStockQty,vUnit,mRate,isMultiple,vSizeName from [funDeliveryChallan] ('"+DoNOAll.replaceAll("'", "")+"') ";
			System.out.println("sql is:"+sql);
			
			List<?> list = session.createSQLQuery(" SELECT vDoNo,vDoNoMerge,dDoDate,vProductId,mDoQty,mDelQty,mBalQty," +
					" mStockQty,vUnit,mRate,isMultiple,vSizeName from [funDeliveryChallan] ('"+DoNOAll.replaceAll("'", "")+"') ").list();
			tableClear();
			Iterator<?> iter = list.iterator();

			int i = 0;
			
			for(int j=0;j<tbcmbLabel.size();j++)
			{
			   	tbcmbLabel.get(j).removeAllItems();
			}
			table.setColumnCollapsed("Label",true);

			for(;iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				tbcmbProCode.get(i).setValue(element[3]);
				tblblDO.get(i).setValue(element[0].toString());
				tblblDOMerge.get(i).setValue(element[1].toString());
				tblblDODate.get(i).setValue(dFormat.format(element[2]));

				tbtxtDOQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[4].toString())));
				tbtxtDeliveredQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[5].toString())));
				tbtxtBalanceQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[6].toString())));

				tblblUnit.get(i).setValue(element[8].toString());
				tblblProductRate.get(i).setValue(dfRate.format(element[9]));
				
				tbSize.get(i).setValue(element[11].toString());

				if(partyTbchkDO.get(0).booleanValue()==true)
				{
					tblblDO.get(i).setValue("Without PO");
				}
				
				
				
				String fgid="";
				fgid=tbcmbProCode.get(i).getValue().toString();

				if(LabelDatacheck(fgid))
				{
				System.out.println("YES");
				table.setColumnCollapsed("Label",false);
				tbcmbLabelDataLoad(i);
				}
				/*else
				{
					table.setColumnCollapsed("Label", true);
					//tbcmbLabel.get(i).setValue(null);
					tbcmbLabel.get(i).removeAllItems();
				}*/
				

				String sqlStock="";

				if(Integer.parseInt(hmProductFlag.get(tbcmbProCode.get(i).getValue().toString()).toString()) ==1)
				{
					sqlStock="select isnull(MIN(stockQty),0) from funcMasterProductReadyForSale" +
							"('"+tbcmbProCode.get(i).getValue()+"'," +
							"'"+tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue())+"'," +
							"'"+dFormat.format(dDate.getValue())+"','','')";	
				}
				else
				{
					sqlStock= "select  dbo.fun3rdPartystock('"+tbcmbProCode.get(i).getValue()+"') ";  	
				}


				Iterator iter1=session.createSQLQuery(sqlStock).list().iterator();
				double stock=0;
				if(iter1.hasNext())
				{
					stock=Double.parseDouble(iter1.next().toString());
					System.out.println("stock is:"+stock);

					tbtxtStockQty.get(i).setValue(decimal.format(stock));
				}
				if(element[10].toString().equalsIgnoreCase("yes"))
				{
					
					tbtxtStockQty.get(i).setValue("0.00");	
				}
				if(tbcmbLabel.get(i).getValue()!=null)
				{
					tbLabelAction(i);	
				}
				
				if(tbcmbProCode.size()-1==i)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			setEnableProduct(true);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tbLabelAction(final int i){
		tbcmbLabel.get(i).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				session.beginTransaction();
				String sqlStock="";
				String cmbLabelName= tbcmbLabel.get(i).getItemCaption(tbcmbLabel.get(i).getValue());
				String cmbLabelId= tbcmbLabel.get(i).getValue().toString();
				
				
				if(Integer.parseInt(hmProductFlag.get(tbcmbProCode.get(i).getValue().toString()).toString()) ==1)
				{
					sqlStock="select isnull(MIN(stockQty),0) from funcMasterProductReadyForSale" +
							"('"+tbcmbProCode.get(i).getValue()+"'," +
							"'"+tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue())+"'," +
							"'"+dFormat.format(dDate.getValue())+"','"+cmbLabelId+"','"+cmbLabelName+"')";	
				}
				else
				{
					sqlStock= "select  dbo.fun3rdPartystock('"+tbcmbProCode.get(i).getValue()+"') ";  	
				}


				Iterator iter1=session.createSQLQuery(sqlStock).list().iterator();
				double stock=0;
				if(iter1.hasNext())
				{
					stock=Double.parseDouble(iter1.next().toString());
					System.out.println("stock is:"+stock);

					tbtxtStockQty.get(i).setValue(decimal.format(stock));
				} 
			}
		});
	
		}
		

	
	private void setEnableProduct(boolean t)
	{
		for(int i = 0; i<tblblDO.size(); i++)
		{
			tbcmbProCode.get(i).setReadOnly(t);
		}
	}

	private void setDoDisable(boolean b)
	{
		dDate.setEnabled(b);

		txtChallanNo.setEnabled(b);
		txtGatePassNo.setEnabled(b);

		cmbDriverName.setEnabled(b);
		cmbDriverMobile.setEnabled(b);
		cmbTruckNo.setEnabled(b);
		cmbDestination.setEnabled(b);
		cmbDestinationTinNo.setEnabled(b);
		table.setEnabled(b);
		txtRemarks.setEnabled(b);
		txtDelChallanNo.setEnabled(b);
		txtVatChallanNo.setEnabled(b);
	}

	public boolean checkExistDO(String DoNo)
	{
		boolean ret = true;
		for(int i=0; i<tblblDO.size(); i++)
		{
			if(DoNo.equals(tblblDO.get(i).getValue().toString()))
			{
				ret = false;
				break;
			}
		}
		return ret;
	}

	private void findButtonEvent() 
	{
		Window win = new DeliveryChallanFind(sessionBean, txtChallanNoFind,"ItemId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtChallanNoFind.getValue().toString().length() > 0)
				{
					txtClear();
					isFind=true;
					findInitialise(txtChallanNoFind.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String ChallanNo) 
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();

			String sqlVoucherNo="select ISNULL(voucherNo,'') from tbDeliveryChallanInfo where vChallanNo='"+ChallanNo+"'";
		  Iterator iter1=session.createSQLQuery(sqlVoucherNo).list().iterator();
			if(iter1.hasNext()){
				txtVoucherNo.setValue(iter1.next());
			}
			
			
			
			String sql = " SELECT * from [funFindDeliveryChallan] ('"+ChallanNo+"') order by vDoNo ";
			System.out.println("find challan is:"+sql);
			List<?> led = session.createSQLQuery(sql).list();
			int i = 0;
			for (Iterator<?> iter = led.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				if(i==0)
				{
					cmbPartyName.setValue(element[0]);
					cmbDepoName.setValue(element[19]);

					dDate.setValue(element[1]);
					txtChallanNo.setValue(element[2].toString());
					txtGatePassNo.setValue(element[3].toString());

					txtPartyTinNo.setValue(element[4].toString());
					cmbDriverName.setValue(element[5].toString());
					cmbDriverMobile.setValue(element[6].toString());
					cmbTruckNo.setValue(element[7].toString());
					cmbDestination.setValue(element[8].toString());
					cmbDestinationTinNo.addItem(element[24].toString());
					cmbDestinationTinNo.setValue(element[24].toString());
					txtRemarks.setValue(element[9].toString());

					clearDOData();

					// Set demand order data according to saved
					if(!element[10].toString().equalsIgnoreCase("Without PO"))
					{
						getChallanDo(element[18].toString());
					}
					else
					{
						partyTbchkDO.get(0).setValue(true);
						tblblDO.get(0).setValue("Without PO");
					}

					maxId = decimal.format(element[19]);

					if(!element[10].toString().equalsIgnoreCase("Without PO"))
					{
						for(int k=1;k<partyTblblDONo.size();k++)
						{
							if(!partyTblblDONo.get(k).getValue().toString().isEmpty())
							{
								partyTbchkDO.get(k).setValue(true);
							}
						}
					}

					cmbDepoName.setValue(element[20]);

					txtDelChallanNo.setValue(element[22].toString());
					txtVatChallanNo.setValue(element[23].toString());
					// Clear table before setvalue to table
					tableClear();
				}

				//Table info
				tbcmbProCode.get(i).setValue(element[12]);
				tblblProductRate.get(i).setValue(dfRate.format(element[21]));
				tblblDO.get(i).setValue(element[10].toString());
				tblblDOMerge.get(i).setValue(element[18].toString());
				tblblDODate.get(i).setValue(dFormat.format(element[11]));
				tblblUnit.get(i).setValue(element[26]);
				tbSize.get(i).setValue(element[27]);
				
				System.out.println("elemt 25 is:"+element[25].toString());
				if(element[25].toString().equalsIgnoreCase(""))
				{
					table.setColumnCollapsed("Label",true);	
				}
				else
				{
					table.setColumnCollapsed("Label",false);		
				}
          
				
				tbcmbLabelDataLoad(i);
				tbcmbLabel.get(i).setValue(element[25]);
				
				String LabelCaption="";
				
				if(element[25]!="")
				{
					LabelCaption=tbcmbLabel.get(i).getItemCaption(element[25]).toString();	
					
				}
				
				
				String	sqlStock="";
				if(Integer.parseInt(hmProductFlag.get(tbcmbProCode.get(i).getValue().toString()).toString()) ==1)
				{
				 sqlStock="select isnull(MIN(stockQty),0) from funcMasterProductReadyForSale " +
						"( '"+element[12]+"', " +
						" '"+tbcmbProCode.get(i).getItemCaption(element[12])+"', " +
						"'"+dFormat.format(dDate.getValue())+"','"+element[25]+"','"+LabelCaption+"')";
				 System.out.println("sqlStock is:"+sqlStock);
				}
				else
				{
						sqlStock= "select  dbo.fun3rdPartystock('"+element[12]+"') ";  	
				}
				Iterator iter3=session.createSQLQuery(sqlStock).list().iterator();
				double stock=0;
				if(iter3.hasNext())
				{
					stock=Double.parseDouble(iter3.next().toString());
					System.out.println("stock is:"+stock);

					tbtxtStockQty.get(i).setValue(decimal.format(stock+Double.parseDouble(element[16].toString())));
				} 
				
				if(!element[10].toString().equals("Without PO"))
				{
					tbtxtDOQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[13].toString())));
					tbtxtDeliveredQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[14].toString())-Double.parseDouble(element[16].toString())));
					tbtxtBalanceQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(tbtxtDOQty.get(i).getValue().toString().replaceAll(",", ""))-Double.parseDouble(tbtxtDeliveredQty.get(i).getValue().toString().replaceAll(",", ""))));
				}
				tbtxtQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[16].toString())));
				tbtxtRemarks.get(i).setValue(element[17].toString());

				if(tbcmbProCode.size()-1==i)
				{
					tableRowAdd(i+1);
				}
				
				i++;				
			}
			
		
		
			if(partyTbchkDO.get(0).booleanValue()==false)
			{
				setEnableProduct(true);
			}
			//Table footer sum
			sumAmount();
		}
		catch (Exception exp)
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void getChallanDo(String DoNoFind)
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery("select di.doNo,di.doDate,SUM(qty)" +
					" qty from tbDemandOrderInfo di inner join tbDemandOrderDetails dd on di.doNo = dd.doNo " +
					" where di.doNo in (SELECT * FROM dbo.Split('"+DoNoFind+"')) group by di.doNo,di.doDate" +
					" having SUM(dd.status)>0").list();
			int i = 1;
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				partyTblblDONo.get(i).setValue(element[0].toString());
				partyTblblDemnadDate.get(i).setValue(tbDOdateFormat.format(element[1]));
				partyTblblDOQty.get(i).setValue(decimal.format(element[2]).toString());
				i++;
			}
			if(i==partyTblblDONo.size()-1)
			{
				DOTableRowAdd(i+1);
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public Double getChallanQty(String doNo,String ProId,String ChallanNo)
	{
		double findQty = 0;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String sql = " select isnull(SUM(mChallanQty),0) asd from tbDeliveryChallanDetails" +
					" where vDoNo = '"+doNo+"' and vProductId = '"+ProId+"' and vChallanNo != '"+ChallanNo+"' ";
			List<?> lst = session.createSQLQuery(sql).list();
			Iterator<?> iter = lst.iterator();
			if(iter.hasNext())
			{
				findQty = Double.parseDouble(iter.next().toString());
			}
		}
		catch(Exception e)
		{
			showNotification("Error "+e,Notification.TYPE_ERROR_MESSAGE);
		}

		return findQty;
	}

	private void cmbPartyData()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" Select partyCode,partyName from tbPartyInfo where" +
					" isActive='1' ORDER by partyName ").list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbDepoData()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" select vDepoId,vDepoName from tbDepoInformation order by vDepoName ").list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepoName.addItem(element[0].toString());
				cmbDepoName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addDestination()
	{
		cmbDestination.removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery("select distinct vDestination,0 from tbDeliveryChallanInfo order by vDestination").list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDestination.addItem(element[0].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addTruck()
	{
		cmbTruckNo.removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery("select distinct vTruckNo,0 from tbDeliveryChallanInfo order by vTruckNo").list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbTruckNo.addItem(element[0].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addDriverName()
	{
		cmbDriverName.removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery("select distinct vDriverName,0 from tbDeliveryChallanInfo order by vDriverName").list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDriverName.addItem(element[0].toString());

			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void addDriverMobile()
	{
		cmbDriverMobile.removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery("select distinct vDriverMobile,0 from tbDeliveryChallanInfo order by vDriverMobile").list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDriverMobile.addItem(element[0].toString());

			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void setPartyInfo()
	{
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" Select partyCode,address,mobile,tinNo from tbPartyInfo where partyCode='"+cmbPartyName.getValue().toString()+"' ").list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				txtPartyAddress.setValue(element[1].toString());
				txtPartyMobile.setValue(element[2].toString());
				txtPartyTinNo.setValue(element[3].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void formValidation()
	{
		if(cmbPartyName.getValue()!=null)
		{
			if(cmbDepoName.getValue()!=null)
			{
				if(!dDate.getValue().toString().equals(""))
				{
					if(!txtChallanNo.getValue().toString().isEmpty())
					{
						if(!txtGatePassNo.getValue().toString().isEmpty())
						{
							if(!txtVatChallanNo.getValue().toString().isEmpty())
							{
								if(checkDo())
								{
									if(cmbDriverName.getValue()!=null)
									{
										if(cmbDestination.getValue()!=null && cmbDestinationTinNo.getValue()!=null)
										{
											if(validTableSelect())
											{
												saveButtonEvent();
											}
											else
											{
												showNotification("Warning!","Provide challan qty",Notification.TYPE_WARNING_MESSAGE);
												tbtxtQty.get(0).focus();
											}
										}
										else
										{
											showNotification("Warning!","Provide destination && destination TIN No.",Notification.TYPE_WARNING_MESSAGE);
											cmbDestination.focus();
											cmbDestinationTinNo.focus();
										}
									}
									else
									{
										showNotification("Warning!","Provide driver name",Notification.TYPE_WARNING_MESSAGE);
										cmbDriverName.focus();
									}
								}
								else
								{
									showNotification("Warning!","Select PO No",Notification.TYPE_WARNING_MESSAGE);
									partyTbchkDO.get(0).focus();
								}	
							}

							else
							{
								showNotification("Warning!","Please,Provide Vat Challan No",Notification.TYPE_WARNING_MESSAGE);
								//txtVatChallanNo.focus();	
							}


						}
						else
						{
							showNotification("Warning!","Gate pass no is not availiable",Notification.TYPE_WARNING_MESSAGE);
						}	
					}
					else
					{
						showNotification("Warning!","Challan No is not availiable",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning!","Enter valid date",Notification.TYPE_WARNING_MESSAGE);
					dDate.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Store Name",Notification.TYPE_WARNING_MESSAGE);
				cmbDepoName.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select party name",Notification.TYPE_WARNING_MESSAGE);
			cmbPartyName.focus();
		}
	}

	public boolean validTableSelect()
	{
		boolean ret = false;
		for(int i=0; i<tblblDO.size(); i++)
		{
			if(Double.parseDouble(tbtxtQty.get(i).getValue().toString().isEmpty()?"0.0":tbtxtQty.get(i).getValue().toString().replaceAll(",", "").toString())>0)
			{
				ret = true;
				break;
			}
		}
		return ret;
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
						if(deleteData(session,tx))
						{
							if(insertData(session,tx)){
								showNotification("All information update successfully.");
								btnIni(true);
								componentIni(true);
								isUpdate = false;
								isFind = false;
								button.btnNew.focus();
							}
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
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();
						if(insertData(session,tx)){
							showNotification("All information save successfully.");
							btnIni(true);
							componentIni(true);
							button.btnNew.focus();
						}
					}
				}
			});
		}
	}

	private Date getDatetime()
	{
		Date dateTime = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		try
		{
			session.beginTransaction();
			String query = "select CONVERT(time ,CURRENT_TIMESTAMP)";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				dateTime= (Date) iter.next();
			}
		}
		catch(Exception ex)
		{
			showNotification("Error3",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
		return dateTime;
	}
	private void getTransactionNo(){
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().getCurrentSession();
			String sql="select isnull(MAX(transactionId),0)+1 from tbDelliveryIngradiantConsumptionInfo";
			Iterator iter=session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext()){
				txtTransactionNo.setValue(iter.next());
			}
		}
		catch(Exception exp){
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public String vocherIdGenerate()
	{
		String vo_id = null;
		Transaction tx = null;
		try{
			Session session1 = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session1.beginTransaction();
			String fsl = session1.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dFormat.format(dDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			Iterator<?> iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE  CompanyId = '"+ sessionBean.getCompanyId() +"' and (vouchertype = 'jau' or vouchertype = 'jcv' or vouchertype = 'jai')").list().iterator();
			if(iter.hasNext())
			{
				vo_id=iter.next().toString().trim();
			}
		}
		catch(Exception ex){

			this.getParent().showNotification(
					"Error",
					ex+"",
					Notification.TYPE_ERROR_MESSAGE);
		}

		return "JV-NO-"+vo_id;
	}
	private boolean insertData(Session session,Transaction tx)
	{
		String udFlag = "";

		String dono = "",transactionNo="",voucherNo="";
		double doQty = 0;
		double formDoQty = 0;
		double costAmount=0;

		for(int i=0; i<partyTbchkDO.size(); i++)
		{
			if(partyTbchkDO.get(i).booleanValue()==true)
			{
				if(partyTbchkDO.get(0).booleanValue()==true)
				{
					dono = "Without PO";
					break;
				}
				else
				{
					dono = dono + partyTblblDONo.get(i).getValue().toString() +",";
				}
				dono = dono.substring(0, dono.length()-1);
			}
		}
		if(isUpdate)
		{
			challanNo = txtChallanNo.getValue().toString();
			gatepassNo = txtGatePassNo.getValue().toString();
			//transactionNo=txtTransactionNo.getValue().toString();
			voucherNo=txtVoucherNo.getValue().toString();
			//VATchallaanNo=txtVatChallanNo.getValue().toString();
		}
		else
		{
			selectReceiptNo();
			//getTransactionNo();
			//setVATChallanNo();
			voucherNo=vocherIdGenerate();
		}
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]" +
				"('"+dFormat.format(dDate.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;
		try
		{


			for(int i = 0; i<tblblDO.size(); i++)
			{
				// For without do entry start
				if(partyTbchkDO.get(0).booleanValue()==true)
				{
					if(tbcmbProCode.get(i).getValue()!=null && Double.parseDouble((tbtxtQty.get(i).getValue().toString().isEmpty()?"0.0":tbtxtQty.get(i).getValue().toString().replaceAll(",", "")))>0)
					{
						formDoQty = Double.parseDouble(tbtxtQty.get(i).getValue().toString().replace(",", "").trim());
						double amount=getCostprice(tbcmbProCode.get(i).getValue().toString(),
								tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue().toString()));

						/*BigDecimal amount=getCostprice(tbcmbProCode.get(i).getValue().toString(),
								tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue().toString()));*/

						System.out.println("cosst price is:"+amount);
						amount=amount* formDoQty;
						costAmount=costAmount+amount;

						System.out.println("Amount is: "+amount);
						System.out.println("Cost Amount is: "+costAmount);

						String InsertDetails = "Insert into tbDeliveryChallanDetails values ( " +
								" '"+challanNo+"'," +
								" '"+gatepassNo+"'," +
								" '"+tblblDO.get(i).getValue().toString()+"'," +
								" '"+tblblDODate.get(i).getValue()+"'," +
								" '"+tbcmbProCode.get(i).getValue().toString()+"'," +
								" '"+tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue().toString())+"'," +
								" '"+tblblUnit.get(i).getValue().toString().trim()+"'," +
								" '"+formDoQty+"'," +
								" '"+(tbtxtRemarks.get(i).getValue().toString().isEmpty()?"":tbtxtRemarks.get(i).getValue().toString().trim())+"', "+
								" '"+(tblblProductRate.get(i).getValue().toString().isEmpty()?"":tblblProductRate.get(i).getValue().toString().trim())+"'," +
								"'"+voucher+"','"+amount+"',"
										+ "'"+tbcmbLabel.get(i).getValue().toString()+"',"
										+ "'"+tbcmbLabel.get(i).getItemCaption(tbcmbLabel.get(i).getValue().toString())+"','"+tbSize.get(i).getValue()+"' )";

						System.out.println("InsertDetails: "+InsertDetails);
						session.createSQLQuery(InsertDetails).executeUpdate();

						if(!isUpdate)
						{ udFlag = "New"; }
						else
						{ udFlag = "Update"; }
						// Challan Update delete Track
						String udTrack = " INSERT into tbUdDeliveryChallan values(" +
								" '"+tblblDO.get(i).getValue().toString()+"'," +
								" '"+challanNo+"'," +
								" '"+dFormat.format(dDate.getValue())+" "+(getDatetime())+"'," +
								" '"+cmbPartyName.getValue().toString()+"'," +
								" '"+(cmbDriverName.getValue()==null?"":cmbDriverName.getValue().toString().trim())+"'," +
								" '"+(cmbDriverMobile.getValue()==null?"":cmbDriverMobile.getValue().toString())+"'," +
								" '"+(cmbTruckNo.getValue()==null?"":cmbTruckNo.getValue().toString())+"'," +
								" '"+(cmbDestination.getValue()==null?"":cmbDestination.getValue().toString())+"'," +
								" '"+(txtRemarks.getValue().toString().isEmpty()?"":txtRemarks.getValue().toString())+"'," +
								" '"+tbcmbProCode.get(i).getValue().toString()+"'," +
								" '"+tblblProductRate.get(i).getValue().toString()+"'," +
								" '"+formDoQty+"'," +
								" '"+(tbtxtRemarks.get(i).getValue().toString().isEmpty()?"":tbtxtRemarks.get(i).getValue().toString().trim())+"',"+
								" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+udFlag+"',"
										+ "'"+tbcmbLabel.get(i).getValue().toString()+"',"
										+ "'"+tbcmbLabel.get(i).getItemCaption(tbcmbLabel.get(i).getValue().toString())+"' )";

						System.out.println("udTrack: "+udTrack);
						session.createSQLQuery(udTrack).executeUpdate();

						//Tracking to Production Module

						if(Integer.parseInt(hmProductFlag.get(tbcmbProCode.get(i).getValue()).toString()) ==1)
						{
							String track="exec prcDelliveryIngradiantsInsert" +
									//"'"+txtTransactionNo.getValue()+"'," +
									"'"+challanNo+"'," +
									"'"+dFormat.format(dDate.getValue())+" "+(getDatetime())+"',"+
									" '"+tbcmbProCode.get(i).getValue().toString()+"'," +
									" '"+tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue().toString())+"'," +
									" '"+tblblUnit.get(i).getValue().toString().trim()+"'," +
									" '"+tbtxtQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
									" '"+(tblblProductRate.get(i).getValue().toString().isEmpty()?"":
										tblblProductRate.get(i).getValue().toString().trim())+"'" ;

							session.createSQLQuery(track).executeUpdate();	
						}

					}
				}
				// For without do entry end

				// For with do entry start
				else
				{
					if(tbcmbProCode.get(i).getValue()!=null && Double.parseDouble((tbtxtQty.get(i).getValue().toString().isEmpty()?"0.0":tbtxtQty.get(i).getValue().toString().replaceAll(",", "")))>0)
					{
						formDoQty = Double.parseDouble(tbtxtQty.get(i).getValue().toString().replace(",", "").trim());
						double amount=getCostprice(tbcmbProCode.get(i).getValue().toString(),
								tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue().toString()));
						amount=amount*formDoQty;
						costAmount=costAmount+amount;

						System.out.println("Amount is: "+amount);
						System.out.println("Cost Amount is: "+costAmount);
						
						String labelId="",labelName="";
						
						if(tbcmbLabel.get(i).getValue()!=null)
						{
							labelId=tbcmbLabel.get(i).getValue().toString();
							labelName=tbcmbLabel.get(i).getItemCaption(tbcmbLabel.get(i).getValue());
						}

						session.createSQLQuery(" EXEC prcDeliveryChallanSave " +
								" '"+txtChallanNo.getValue().toString()+"'," +
								" '"+txtGatePassNo.getValue().toString()+"'," +
								" '"+tblblDOMerge.get(i).getValue().toString().replaceAll("'", "")+"'," +
								" '"+tbcmbProCode.get(i).getValue().toString()+"'," +
								" '"+tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue().toString())+"'," +
								" '"+tblblProductRate.get(i).getValue().toString()+"'," +
								" '"+tblblUnit.get(i).getValue().toString()+"'," +
								" '"+tbtxtQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
								" '"+tbtxtRemarks.get(i).getValue().toString()+"','"+labelId+"','"+labelName+"','"+tbSize.get(i).getValue()+"'   ").executeUpdate();
						formDoQty = doQty;

						//System.out.println("amount is:"+amount);

						//String newamount=decimal.format(amount);

						double  a=1250.3654215666;

						//System.out.print("new amount is"+DecimalUtils.round(amount,4) );
						//DecimalUtils.round(amount, 4);



						String sql="update tbDeliveryChallanDetails set voucherNo='"+voucherNo+"',costAmount='"+dfRate1.format(amount)+"' " +
								"where vChallanNo='"+txtChallanNo.getValue()+"' and " +
								"vProductId='"+tbcmbProCode.get(i).getValue()+"'";

						System.out.println("udTrack: "+sql);
						session.createSQLQuery(sql).executeUpdate();

						if(!isUpdate)
						{ udFlag = "New"; } 
						else
						{ udFlag = "Update"; }
						// Challan Update delete Track
						String udTrack = " INSERT into tbUdDeliveryChallan values(" +
								" '"+tblblDO.get(i).getValue().toString()+"'," +
								" '"+challanNo+"'," +
								" '"+dFormat.format(dDate.getValue())+" "+(getDatetime())+"'," +
								" '"+cmbPartyName.getValue().toString()+"'," +
								" '"+(cmbDriverName.getValue()==null?"":cmbDriverName.getValue().toString().trim())+"'," +
								" '"+(cmbDriverMobile.getValue()==null?"":cmbDriverMobile.getValue().toString())+"'," +
								" '"+(cmbTruckNo.getValue()==null?"":cmbTruckNo.getValue().toString())+"'," +
								" '"+(cmbDestination.getValue()==null?"":cmbDestination.getValue().toString())+"'," +
								" '"+(txtRemarks.getValue().toString().isEmpty()?"":txtRemarks.getValue().toString())+"'," +
								" '"+tbcmbProCode.get(i).getValue().toString()+"'," +
								" '"+tblblProductRate.get(i).getValue().toString()+"'," +
								" '"+formDoQty+"'," +
								" '"+(tbtxtRemarks.get(i).getValue().toString().isEmpty()?"":tbtxtRemarks.get(i).getValue().toString().trim())+"',"+
								" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+udFlag+"',"
										+ "'"+labelId+"',"
										+ "'"+labelName+"' )";
						
						//System.out.println("udTrack: "+udTrack);
						session.createSQLQuery(udTrack).executeUpdate();


						//Tracking to Production Module

						if(Integer.parseInt(hmProductFlag.get(tbcmbProCode.get(i).getValue()).toString()) ==1)
						{
							String track="exec prcDelliveryIngradiantsInsert" +
									//"'"+txtTransactionNo.getValue()+"'," +
									"'"+challanNo+"'," +
									"'"+dFormat.format(dDate.getValue())+" "+(getDatetime())+"',"+
									" '"+tbcmbProCode.get(i).getValue().toString()+"'," +
									" '"+tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue().toString())+"'," +
									" '"+tblblUnit.get(i).getValue().toString().trim()+"'," +
									" '"+tbtxtQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
									" '"+(tblblProductRate.get(i).getValue().toString().isEmpty()?"":
										tblblProductRate.get(i).getValue().toString().trim())+"','"+labelId+"','"+labelName+"'   " ;

							session.createSQLQuery(track).executeUpdate();
						}

					}
				}
			}
            
			System.out.println("cost Amount is Now:"+costAmount);

			if (costAmount==0)
			{
				voucherNo="";	
			}

			String InsertInfo = " INSERT into tbDeliveryChallanInfo(vDoNo,vChallanSerial,dChallanDate," +
					" vChallanNo,vGatePassNo,vPartyId,vPartyName,vPartyAddress,vPartyTinNo,vPartyMobile,vDepoId," +
					" vDepoName,vDriverName,vDriverMobile,vTruckNo,vDestination,vRemarks,vDelChallanNo,vVatChallanNo," +
					" status,vUserName,vUserIp,dEntryTime,voucherNo,totalCostAmount,vDestinationTIN) values (" +
					" '"+dono+"'," +
					" '"+maxId+"'," +
					" '"+dFormat.format(dDate.getValue())+" "+(getDatetime())+"'," +
					" '"+challanNo+"'," +
					" '"+gatepassNo+"'," +
					" '"+cmbPartyName.getValue().toString()+"'," +
					" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"'," +
					" '"+txtPartyAddress.getValue().toString().trim()+"'," +
					" '"+(txtPartyTinNo.getValue().toString().isEmpty()?"":txtPartyTinNo.getValue().toString().trim())+"'," +
					" '"+txtPartyMobile.getValue().toString().trim()+"'," +
					" '"+cmbDepoName.getValue().toString()+"'," +
					" '"+cmbDepoName.getItemCaption(cmbDepoName.getValue().toString())+"'," +
					" '"+(cmbDriverName.getValue()==null?"":cmbDriverName.getValue().toString().trim())+"'," +
					" '"+(cmbDriverMobile.getValue()==null?"":cmbDriverMobile.getValue().toString())+"'," +
					" '"+(cmbTruckNo.getValue()==null?"":cmbTruckNo.getValue().toString())+"'," +
					" '"+(cmbDestination.getValue()==null?"":cmbDestination.getValue().toString())+"'," +
					" '"+(txtRemarks.getValue().toString().isEmpty()?"":txtRemarks.getValue().toString())+"'," +
					" '"+(txtDelChallanNo.getValue().toString().isEmpty()?"":txtDelChallanNo.getValue().toString())+"'," +
					" '"+txtVatChallanNo.getValue().toString()+"'," +
					" '1','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					"'"+voucherNo+"','"+ dfRate1.format(costAmount)+"','"+(cmbDestinationTinNo.getValue()==null?"":cmbDestinationTinNo.getValue().toString())+"') ";

			//System.out.println("InsertInfo: "+InsertInfo);
			session.createSQLQuery(InsertInfo).executeUpdate();

			//Accounting Treatmen Start
       System.out.println("     SQL       :    "+InsertInfo);

       System.out.println("     println out 4       :    "+voucherNo.trim());
			if(!voucherNo.trim().isEmpty())
			{
				 System.out.println("     println out        :    "+voucherNo.trim());
				if(costAmount>0)
				{
					System.out.println("     println out 1 2        :    ");
					String narration="Production No: "+txtChallanNo.getValue()+" Production Date: "+
							new SimpleDateFormat("yyyy-MM-dd").format(dDate.getValue());

					String sqlInfoVoucherDr=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration," +
							"DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime," +
							"auditapproveflag,companyId,attachBill,TransactionWith) "
							+" values(" +
							"'"+voucherNo+"'," +
							"'"+new SimpleDateFormat("yyyy-MM-dd").format(dDate.getValue())+"'," +
							"'EL5', "  
							+" '"+narration+"'," +
							"'"+dfRateVoucher.format(costAmount)+"' , "+
							" '0'," +
							"'jau'," +
							"'U-1'," +
							"'1' ," +
							"'"+sessionBean.getUserId()+"', "
							+" '"+sessionBean.getUserIp()+"'," +
							"CURRENT_TIMESTAMP, "
							+" '2', " +
							"'"+sessionBean.getCompanyId()+"' ," +
							"''," +
							"'Delivery Challan' ) ";
					session.createSQLQuery(sqlInfoVoucherDr).executeUpdate();
					
					System.out.println("           "
							+ "         sqlInfoVoucherDr        "+sqlInfoVoucherDr
							+ "                 ");

					String sqlInfoVoucherCr=" insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration," +
							"DrAmount,CrAmount,vouchertype,costId,chqClear,userId,userIp,entryTime," +
							"auditapproveflag,companyId,attachBill,TransactionWith) "
							+" values(" +
							"'"+voucherNo+"'," +
							"'"+new SimpleDateFormat("yyyy-MM-dd").format(dDate.getValue())+"'," +
							"'AL1707', "  
							+" '"+narration+"'," +
							" '0'," +
							"'"+dfRateVoucher.format(costAmount)+"' , "+
							"'jau'," +
							"'U-1'," +
							"'1' ," +
							"'"+sessionBean.getUserId()+"', "
							+" '"+sessionBean.getUserIp()+"'," +
							"CURRENT_TIMESTAMP, "
							+" '2', " +
							"'"+sessionBean.getCompanyId()+"' ," +
							"''," +
							"'Delivery Challan' ) ";
					session.createSQLQuery(sqlInfoVoucherCr).executeUpdate();	

				}

			}
			//Accounting Treatmen END
			tx.commit();
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

			}
		}
		return false;
	}
	/*	private double getCostprice(String fgId,String fgName){
		Session session=null;
		try{
			session = SessionFactoryUtil.getInstance().getCurrentSession();
			String sql="select isnull(SUM(RmFormulationAmount),0)+isnull(SUM(inkFormulationAmount),0) "+
					"+(select isnull(SUM(amount),0)amount from funcCostingEntry('"+fgId+"'))FormulationAmount from funcRawMaterialFormulationPrice('"+fgId+"','"+fgName+"') ";
			Iterator<?> iter= session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext()){
				return   Double.parseDouble(iter.next()+"");

			//return Double.parseDouble(decimal.format(Double.parseDouble(iter.next()+""))) 	;



			}
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		return 0;
	}*/

	BigDecimal bigDecimal;
	public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
		BigDecimal bigDecimal = new BigDecimal(value);
		bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
				BigDecimal.ROUND_HALF_UP);
		return bigDecimal.doubleValue();
	}


	private double getCostprice(String fgId,String fgName){
		Session session=null;
		try{
			session = SessionFactoryUtil.getInstance().getCurrentSession();
			String sql="select isnull(SUM(RmFormulationAmount),0)+isnull(SUM(inkFormulationAmount),0) "+
					"+(select isnull(SUM(amount),0)amount from funcCostingEntry('"+fgId+"'))FormulationAmount from funcRawMaterialFormulationPrice('"+fgId+"','"+fgName+"') ";
			Iterator<?> iter= session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext()){


				return Double.parseDouble(decimal.format(Double.parseDouble(iter.next()+""))) 	;



			}
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		return 0;
	}




	private boolean deleteData(Session session,Transaction tx)
	{
		try
		{
			String delFormSql = " delete from tbDeliveryChallanInfo where vChallanNo='"+txtChallanNo.getValue().toString().trim()+"' ";
			String delTableSql = " delete from tbDeliveryChallanDetails where vChallanNo='"+txtChallanNo.getValue().toString().trim()+"' ";
			String delFormSqlTrack = " delete from tbDelliveryIngradiantConsumptionInfo where challanNo='"+txtChallanNo.getValue().toString().trim()+"' ";
			String delTableSqlTrack = " delete from tbDelliveryIngradiantConsumptionDetails where challanNo='"+txtChallanNo.getValue().toString().trim()+"' ";
			session.createSQLQuery(delFormSql).executeUpdate();
			session.createSQLQuery(delTableSql).executeUpdate();
			session.createSQLQuery(delFormSqlTrack).executeUpdate();
			session.createSQLQuery(delTableSqlTrack).executeUpdate();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dFormat.format(dDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			session.createSQLQuery("delete "+voucher+"  where Voucher_No='"+txtVoucherNo.getValue()+"'").executeUpdate();
			System.out.println("Delete Finished");
			return true;
		}
		catch(Exception exp)
		{
			showNotification("Error Delete",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void focusEnter()
	{
		allComp.add(cmbPartyName);
		allComp.add(cmbDepoName);
		allComp.add(dDate);
		allComp.add(cmbPONo);

		for(int i=0; i<tblblUnit.size(); i++)
		{
			allComp.add(tbcmbProCode.get(i));
			allComp.add(tbcmbLabel.get(i));
			allComp.add(tbtxtQty.get(i));
			allComp.add(tbtxtRemarks.get(i));
		}

		allComp.add(txtPartyTinNo);
		allComp.add(cmbDriverName);
		allComp.add(cmbDriverMobile);
		allComp.add(cmbTruckNo);
		allComp.add(cmbDestination);
		allComp.add(cmbDestinationTinNo);
		allComp.add(txtRemarks);

		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{
		cmbPartyName.setEnabled(!b);
		txtPartyAddress.setEnabled(!b);
		txtPartyMobile.setEnabled(!b);

		cmbDepoName.setEnabled(!b);
		btnStore.setEnabled(!b);
		txtVoucherNo.setEnabled(!b);

		dDate.setEnabled(!b);

		txtChallanNo.setEnabled(!b);
		txtGatePassNo.setEnabled(!b);
		DOTable.setEnabled(!b);

		txtPartyTinNo.setEnabled(!b);
		cmbDriverName.setEnabled(!b);
		cmbDriverMobile.setEnabled(!b);
		txtDelChallanNo.setEnabled(!b);
		txtVatChallanNo.setEnabled(!b);
		cmbDestinationTinNo.setEnabled(!b);
		cmbTruckNo.setEnabled(!b);
		cmbDestination.setEnabled(!b);
		table.setEnabled(!b);
		table.setColumnCollapsed("Label",true);

		txtRemarks.setEnabled(!b);

		btnParty.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnCancel.setEnabled(t);
		button.btnPreview.setEnabled(t);
		button.btnFind.setEnabled(t);

		//btnGatePass.setEnabled(t);
		btnChallan.setEnabled(t);
		btnVatChallan.setEnabled(t);
	}

	private void txtClear()
	{
		isFind = false;
		isUpdate = false;

		DoNOAll = "";
		DoNO = "";
		action = 0;

		cmbPartyName.setValue(null);
		txtPartyAddress.setValue("");
		txtPartyMobile.setValue("");

		cmbDepoName.setValue(null);
		txtChallanNo.setValue("");
		txtGatePassNo.setValue("");

		txtPartyTinNo.setValue("");
		cmbDestinationTinNo.setValue(null);
		cmbDriverName.setValue(null);
		cmbDriverMobile.setValue(null);
		txtDelChallanNo.setValue("");
		txtVatChallanNo.setValue("");
		cmbTruckNo.setValue(null);
		cmbDestination.setValue(null);

		tableClear();

		txtRemarks.setValue("");

		table.setColumnFooter("Item Code", "");
		table.setColumnFooter("Stk Qty", "");
		table.setColumnFooter("Qty", "");
	}

	private void clearDOData()
	{
		partyTbchkDO.get(0).setValue(false);
		for(int i=1;i<partyTbchkDO.size();i++)
		{
			partyTbchkDO.get(i).setValue(false);
			partyTblblDONo.get(i).setValue("");
			partyTblblDemnadDate.get(i).setValue("");
			partyTblblDOQty.get(i).setValue("");
		}
	}

	private void tableClear()
	{
		for(int i=0;i<tblblDO.size();i++)
		{
			tblblDO.get(i).setValue("");

			tbcmbProCode.get(i).setReadOnly(false);
			tbcmbProCode.get(i).setValue(null);
			tbcmbProCode.get(i).setEnabled(true);
			
			
			tbcmbLabel.get(i).setValue(null);
			tbcmbLabel.get(i).setEnabled(true);

			tblblDOMerge.get(i).setValue("");
			tblblDODate.get(i).setValue("");

			tblblUnit.get(i).setValue("");
			tblblProductRate.get(i).setValue("");
			tbtxtDOQty.get(i).setValue("");
			tbtxtDeliveredQty.get(i).setValue("");
			tbtxtBalanceQty.get(i).setValue("");
			tbtxtStockQty.get(i).setValue("");
			tbtxtQty.get(i).setValue("");
			tbtxtRemarks.get(i).setValue("");
			//tbSize.get(i).setValue("");
		}
		table.setColumnFooter("Item Code", "");
		table.setColumnFooter("Stock Qty", "");
	}

	private void addProductData(final int i)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			tbcmbProCode.get(i).removeAllItems();
			String sql= "select vProductId,vProductName,'1' flag from tbFinishedProductInfo "
					+"where isActive = 1 and vCategoryId = (select vGroupId from tbPartyInfo where "
					+"partyCode = '"+cmbPartyName.getValue().toString()+"') " 
					+"union " 					 
					+"select vLabelCode,vLabelName,'2'flag from tb3rdPartylabelInformation where isActive=1 "
					+"and vPartyId='"+cmbPartyName.getValue().toString()+"' order by vProductName ";

			/*List<?> lst = session.createSQLQuery("select vProductId,vProductName from tbFinishedProductInfo" +
					" where isActive = 1 and vCategoryId = (select vGroupId from tbPartyInfo where" +
					" partyCode = '"+cmbPartyName.getValue().toString()+"') order by vProductName").list();
			 */
			List<?> lst = session.createSQLQuery(sql).list();



			for(Iterator<?> iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				tbcmbProCode.get(i).addItem(element[0].toString());	
				tbcmbProCode.get(i).setItemCaption(element[0].toString(), element[1].toString());
				hmProductFlag.put(element[0].toString(), element[2].toString());
			}
		}
		catch(Exception ex)
		{
			showNotification("Error1",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void destinationTINData()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			cmbDestinationTinNo.removeAllItems(); 
			String sql=" SElect distinct 0, vDestinationTIN  from tbDeliveryChallanInfo where vPartyId ='"+cmbPartyName.getValue()+"'   and vDestination like  '%"+cmbDestination.getValue().toString().trim()+"%'  and vDestinationTIN!=''";
			/*+"union "
			+" SElect distinct 0, tinNo from tbPartyInfo where partyCode ='"+cmbPartyName.getValue()+"' ";*/

			/*	String sql="SElect distinct 0,vDestinationTIN  from tbDeliveryChallanInfo "
					+ "where vPartyId ='"+cmbPartyName.getValue()+"' and vDestination like  '"+cmbDestination.getValue().toString().trim()+"' ";			*/
			List<?> lst = session.createSQLQuery(sql).list();
			Iterator<?> iter = lst.iterator();
			System.out.println(sql);	
			while( iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbDestinationTinNo.addItem(element[1].toString());	
				cmbDestinationTinNo.setItemCaption(element[1].toString(),element[1].toString());	
				cmbDestinationTinNo.setValue(element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			showNotification("Error1",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1140px");
		mainLayout.setHeight("590px");

		lblPartyName = new Label("Party Name :");
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		mainLayout.addComponent(lblPartyName, "top:20.0px; left:20.0px;");

		//hlt.setWidth("250px");
		//hlt.setImmediate(true);
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("250px");
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setNullSelectionAllowed(true);
		//hlt.addComponent(cmbPartyName);
		mainLayout.addComponent(cmbPartyName, "top:16.0px; left:100.0px;");
		
		
		btnParty = new NativeButton();
		btnParty.setCaption("");
		btnParty.setImmediate(true);
		btnParty.setWidth("28px");
		btnParty.setHeight("24px");
		btnParty.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnParty,"top:16.0px; left:355.0px;");

		lblPartyAddress = new Label("Address :");
		lblPartyAddress.setWidth("-1px");
		lblPartyAddress.setHeight("-1px");
		mainLayout.addComponent(lblPartyAddress, "top:45.0px; left:20.0px;");

		txtPartyAddress = new TextField();
		txtPartyAddress.setImmediate(false);
		txtPartyAddress.setWidth("250px");
		txtPartyAddress.setHeight("47px");
		mainLayout.addComponent(txtPartyAddress, "top:42.0px; left:101.0px;");

		lblPartyMobile = new Label("Mobile :");
		lblPartyMobile.setWidth("-1px");
		lblPartyMobile.setHeight("-1px");
		mainLayout.addComponent(lblPartyMobile, "top:95.0px; left:20.0px;");

		txtPartyMobile = new TextField();
		txtPartyMobile.setImmediate(false);
		txtPartyMobile.setWidth("250px");
		txtPartyMobile.setHeight("22px");
		mainLayout.addComponent(txtPartyMobile, "top:92.0px; left:101.0px;");

		lblDepoName = new Label("Store Name :");
		lblDepoName.setImmediate(false);
		lblDepoName.setWidth("-1px");
		lblDepoName.setHeight("-1px");
		mainLayout.addComponent(lblDepoName, "top:120.0px; left:20.0px;");

		cmbDepoName = new ComboBox();
		cmbDepoName.setImmediate(true);
		cmbDepoName.setWidth("200px");
		cmbDepoName.setHeight("-1px");
		cmbDepoName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDepoName, "top:118.0px; left:101.0px;");

		btnStore = new NativeButton();
		btnStore.setCaption("");
		btnStore.setImmediate(true);
		btnStore.setWidth("28px");
		btnStore.setHeight("24px");
		btnStore.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnStore,"top:118.0px; left:306.0px;");

		lblDate = new Label("Date :");
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:410.0px;");

		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:18.0px; left:505.0px;");

		lblVoucehrNo = new Label("Voucher No :");
		lblVoucehrNo.setWidth("-1px");
		lblVoucehrNo.setHeight("-1px");
		mainLayout.addComponent(lblVoucehrNo, "top:20.0px; left:620.0px;");

		txtVoucherNo = new TextRead();
		txtVoucherNo.setImmediate(false);
		txtVoucherNo.setWidth("100px");
		txtVoucherNo.setHeight("22px");
		mainLayout.addComponent(txtVoucherNo, "top:18.0px; left:700.0px;");

		lblChallanNo = new Label("Challan No :");
		lblChallanNo.setWidth("-1px");
		lblChallanNo.setHeight("-1px");
		mainLayout.addComponent(lblChallanNo, "top:45.0px; left:410.0px;");

		txtChallanNo = new TextRead();
		txtChallanNo.setImmediate(false);
		txtChallanNo.setWidth("150px");
		txtChallanNo.setHeight("22px");
		mainLayout.addComponent(txtChallanNo, "top:43.0px; left:506.0px;");

		lblGatePassNo = new Label("Gate Pass No :");
		lblGatePassNo.setWidth("-1px");
		lblGatePassNo.setHeight("-1px");
		mainLayout.addComponent(lblGatePassNo, "top:70.0px; left:410.0px;");

		txtGatePassNo = new TextRead();
		txtGatePassNo.setImmediate(false);
		txtGatePassNo.setWidth("150px");
		txtGatePassNo.setHeight("22px");
		mainLayout.addComponent(txtGatePassNo, "top:68.0px; left:506.0px;");

		lblGatePassNo = new Label("Challan No :");
		mainLayout.addComponent(lblGatePassNo, "top:95.0px; left:410.0px;");

		txtDelChallanNo = new TextField();
		txtDelChallanNo.setImmediate(false);
		txtDelChallanNo.setWidth("150px");
		txtDelChallanNo.setHeight("-1px");
		mainLayout.addComponent(txtDelChallanNo, "top:93.0px; left:506.0px;");

		lblGatePassNo = new Label("VAT Challan No :");
		mainLayout.addComponent(lblGatePassNo, "top:120.0px; left:410.0px;");

		txtVatChallanNo = new TextField();
		txtVatChallanNo.setImmediate(false);
		txtVatChallanNo.setWidth("150px");
		txtVatChallanNo.setHeight("22px");
		mainLayout.addComponent(txtVatChallanNo, "top:118.0px; left:506.0px;");

		DOTable.setWidth("330px");
		DOTable.setHeight("135px");
		DOTable.setColumnCollapsingAllowed(true);

		DOTable.addContainerProperty("Select", CheckBox.class, new CheckBox());
		DOTable.setColumnWidth("Select", 38);

		DOTable.addContainerProperty("PO No", Label.class, new Label());
		DOTable.setColumnWidth("PO No", 125);

		DOTable.addContainerProperty("Date", Label.class, new Label());
		DOTable.setColumnWidth("Date", 50);

		DOTable.addContainerProperty("Qty", Label.class, new Label());
		DOTable.setColumnWidth("Qty", 40);

		DOTable.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_LEFT, Table.ALIGN_CENTER,Table.ALIGN_RIGHT});

		mainLayout.addComponent(DOTable, "top:3.0px; right:5.0px;");

		table.setWidth("1130px");
		table.setHeight("260px");
		table.setColumnCollapsingAllowed(true);
		table.setImmediate(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("PO No", Label.class, new Label());
		table.setColumnWidth("PO No", 80);

		table.addContainerProperty("PO Merge", Label.class, new Label());
		table.setColumnWidth("PO Merge", 60);

		table.addContainerProperty("PO Date", TextRead.class, new TextRead(1));
		table.setColumnWidth("PO Date", 55);

		table.addContainerProperty("Product Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Product Name", 340);
		
		table.addContainerProperty("Label", ComboBox.class, new ComboBox());
		table.setColumnWidth("Label", 240);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit", 20);

		table.addContainerProperty("Unit Rate", TextRead.class, new TextRead(1));
		table.setColumnWidth("Unit Rate", 50);

		table.addContainerProperty("PO Qty", TextRead.class, new TextRead(1));
		table.setColumnWidth("PO Qty", 80);

		table.addContainerProperty("Delivered Qty", TextRead.class, new TextRead(1));
		table.setColumnWidth("Delivered Qty", 80);

		table.addContainerProperty("Balance Qty", TextRead.class, new TextRead(1));
		table.setColumnWidth("Balance Qty", 80);

		table.addContainerProperty("Stock Qty", TextRead.class, new TextRead(1));
		table.setColumnWidth("Stock Qty", 80);

		table.addContainerProperty("Issue Qty", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Issue Qty", 80);

		table.addContainerProperty("Amount", TextRead.class, new TextRead(1));
		table.setColumnWidth("Amount", 90);

		table.addContainerProperty("Remarks", TextField.class, new TextField());
		table.setColumnWidth("Remarks", 60);
		
		table.addContainerProperty("Show", NativeButton.class, new NativeButton());
		table.setColumnWidth("Show", 60);
		
		table.addContainerProperty("SIZE", TextField.class, new TextField());
		table.setColumnWidth("SIZE", 60);

	

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_LEFT ,
				Table.ALIGN_LEFT ,Table.ALIGN_LEFT,Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_CENTER, Table.ALIGN_CENTER,
				Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_CENTER});

		table.setColumnCollapsed("PO Date", true);
		table.setColumnCollapsed("PO Merge", true);
		table.setColumnCollapsed("PO No", true);
		table.setColumnCollapsed("Remarks", true);
		table.setColumnCollapsed("Label",true);
		table.setColumnCollapsed("SIZE",true);
		table.setFooterVisible(true);

		mainLayout.addComponent(table,"top:150.0px; left:5.0px; ");

		txtPartyTinNo = new TextField("VAT Reg. No :");
		txtPartyTinNo.setImmediate(false);
		txtPartyTinNo.setWidth("170px");
		txtPartyTinNo.setHeight("-1px");
		mainLayout.addComponent(txtPartyTinNo, "top:435.0px; left:25.0px;");
		txtPartyTinNo.setVisible(false);

		cmbDriverName = new ComboBox("Driver Name :");
		cmbDriverName.setImmediate(true);
		cmbDriverName.setNewItemsAllowed(true);
		cmbDriverName.setWidth("170px");
		cmbDriverName.setHeight("-1px");
		cmbDriverName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDriverName, "top:425.0px; left:25.0px;");

		cmbDriverMobile = new ComboBox("Mobile No :");
		cmbDriverMobile.setImmediate(true);
		cmbDriverMobile.setNewItemsAllowed(true);
		cmbDriverMobile.setWidth("170px");
		cmbDriverMobile.setHeight("-1px");
		cmbDriverMobile.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDriverMobile, "top:425.0px; left:250.0px;");

		cmbTruckNo = new ComboBox("Truck No :");
		cmbTruckNo.setImmediate(true);
		cmbTruckNo.setNewItemsAllowed(true);
		cmbTruckNo.setWidth("170px");
		cmbTruckNo.setHeight("-1px");
		cmbTruckNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbTruckNo, "top:425.0px; left:475.0px;");

		cmbDestination = new ComboBox("Destination :");
		cmbDestination.setNewItemsAllowed(true);
		cmbDestination.setImmediate(true);
		cmbDestination.setWidth("880px");
		cmbDestination.setHeight("-1px");
		mainLayout.addComponent(cmbDestination, "top:470.0px; left:25.0px;");

		cmbDestinationTinNo = new ComboBox("Destination TIN No :");
		cmbDestinationTinNo.setWidth("170px");
		cmbDestinationTinNo.setHeight("-1px");
		cmbDestinationTinNo.setImmediate(true);
		cmbDestinationTinNo.setNewItemsAllowed(true);
		cmbDestinationTinNo.setNullSelectionAllowed(true);
		cmbDestinationTinNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDestinationTinNo, "top:470.0px; left:920.0px;");


		txtRemarks = new TextField("Remarks :");
		txtRemarks.setImmediate(false);
		txtRemarks.setWidth("1080px");
		txtRemarks.setHeight("-1px");
		mainLayout.addComponent(txtRemarks, "top:510.0px; left:25.0px;");

		/*btnGatePass.setImmediate(false);
		btnGatePass.setWidth("95px");
		btnGatePass.setHeight("28px");
		btnGatePass.setIcon(new ThemeResource("../icons/preview.png"));
		mainLayout.addComponent(btnGatePass, "top:510.0px; left:685.0px;");*/

		btnChallan.setImmediate(false);
		btnChallan.setWidth("165px");
		btnChallan.setHeight("28px");
		btnChallan.setIcon(new ThemeResource("../icons/preview.png"));
		//mainLayout.addComponent(btnChallan, "top:550.0px; left:685.0px;");
		mainLayout.addComponent(btnChallan, "top:550.0px; left:710.0px;");

		btnVatChallan.setImmediate(false);
		btnVatChallan.setWidth("115px");
		btnVatChallan.setHeight("28px");
		btnVatChallan.setIcon(new ThemeResource("../icons/preview.png"));
		mainLayout.addComponent(btnVatChallan, "top:550.0px; left:880.0px;");

		mainLayout.addComponent(button,"top:550.0px; left:110.0px");

		return mainLayout;
	}

	private void tableinitialise()
	{
		for(int i=0; i<7; i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd( final int ar)
	{
		tblblSelect.add(ar,new Label());
		tblblSelect.get(ar).setWidth("100%");
		tblblSelect.get(ar).setHeight("15px");
		tblblSelect.get(ar).setValue(ar+1);

		tblblDO.add(ar,new Label());
		tblblDO.get(ar).setWidth("100%");
		tblblDO.get(ar).setHeight("15px");

		tblblDOMerge.add(ar,new Label());
		tblblDOMerge.get(ar).setWidth("100%");
		tblblDOMerge.get(ar).setHeight("15px");

		tblblDODate.add(ar,new TextRead());
		tblblDODate.get(ar).setWidth("100%");
		tblblDODate.get(ar).setHeight("15px");
		
		tbSize.add(ar, new TextField());
		tbSize.get(ar).setWidth("100%");
		tbSize.get(ar).setImmediate(true);
		

		tbcmbProCode.add(ar, new ComboBox());
		tbcmbProCode.get(ar).setWidth("100%");
		tbcmbProCode.get(ar).setImmediate(true);

	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql= "select iAutoId,vProductId,vProductName,vUnitName,0 stockQty,"
					+"mDealerPrice,'1'flag from tbFinishedProductInfo where isActive=1 " 
					+"union all "
					+"select iAutoId,vLabelCode,vLabelName,vUnit,0 stockQty, "
					+"0 mDealerPrice,'2'flag from tb3rdPartylabelInformation where isActive=1 " 
					+"order by vProductName " ;

			/*List<?> lst = session.createSQLQuery(" select iAutoId,vProductId,vProductName,vUnitName,0 stockQty," +
					" mDealerPrice from tbFinishedProductInfo where isActive=1 order by vProductName ").list();
			 */
			List<?> lst = session.createSQLQuery(sql).list();


			for(Iterator<?> iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				hmProductId.put(element[1], element[1].toString());
				hmProductName.put(element[1], element[2].toString());
				hmProductUnit.put(element[1], element[3].toString());
				hmProductStockQty.put(element[1], element[4].toString());
				hmProductRate.put(element[1], element[5].toString());
				hmProductFlag.put(element[1], element[6].toString());
			}
		}
		catch(Exception ex)
		{
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}

		tbcmbProCode.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbcmbProCode.get(ar).getValue()!=null)
				{
					boolean fla = (doubleEntryCheck(tbcmbProCode.get(ar).getItemCaption(tbcmbProCode.get(ar).getValue()), ar));

					if (tbcmbProCode.get(ar).getValue() != null && fla) 
					{
						if(partyTbchkDO.get(0).booleanValue()==true)
						{
							tblblDO.get(ar).setValue("Without PO");
							try
							{
								Session session = SessionFactoryUtil.getInstance().getCurrentSession();
								session.beginTransaction();

								/*String SQL = "Select vProductName,vUnitName,mDealerPrice from tbFinishedProductInfo where vProductId='"+tbcmbProCode.get(ar).getValue()+"' ";
								 */

								String sql= "Select vProductName,vUnitName,mDealerPrice,vSizeName from tbFinishedProductInfo where vProductId='"+tbcmbProCode.get(ar).getValue()+"'  "
										+"union all "
										+"select vLabelName,vUnit,mthirdPartyItemRate,vSizeName mDealerPrice from tb3rdPartylabelInformation where vLabelCode='"+tbcmbProCode.get(ar).getValue()+"' ";

								List<?> list = session.createSQLQuery(sql).list();
								for(Iterator<?> iter = list.iterator();iter.hasNext();)
								{
									Object[] element = (Object[]) iter.next();
									tblblUnit.get(ar).setValue(element[1].toString());
									tblblProductRate.get(ar).setValue(dfRate.format(element[2]));
									tbSize.get(ar).setValue(element[3].toString());
								}
								String sqlStock="";
								String proid=tbcmbProCode.get(ar).getValue().toString();
								if(Integer.parseInt(hmProductFlag.get(proid).toString()) ==1)
								{
									sqlStock="select isnull(MIN(stockQty),0) from funcMasterProductReadyForSale" +
											"('"+tbcmbProCode.get(ar).getValue()+"'," +
											"'"+tbcmbProCode.get(ar).getItemCaption(tbcmbProCode.get(ar).getValue())+"'," +
											"'"+dFormat.format(dDate.getValue())+"','','')";		
								}
								else
								{
									sqlStock= "select dbo.fun3rdPartystock('"+tbcmbProCode.get(ar).getValue().toString()+"')";

								}


								Iterator iter1=session.createSQLQuery(sqlStock).list().iterator();
								if(iter1.hasNext()){
									tbtxtStockQty.get(ar).setValue(decimal.format(iter1.next()));
								}
								
								
								
							}
							catch(Exception exp)
							{
								showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
							}
						}

						// have to find stock again after saved this should be get by query return value not hashmap. and this temporary
						//tbtxtStockQty.get(ar).setValue(hmProductStockQty.get(tbcmbProCode.get(ar).getValue()));

						if((ar+1)==tbcmbProCode.size())
						{
							tableRowAdd(tbcmbProCode.size());
							addProductData(ar+1);
						}
					}
					else 
					{
						Object checkNull = (Object) tbcmbProCode.get(ar).getItemCaption(tbcmbProCode.get(ar).getValue());

						if(!checkNull.equals("")) 
						{
							getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							tbcmbProCode.get(ar).setValue(null);

							tblblUnit.get(ar).setValue("");
							tbtxtStockQty.get(ar).setValue("");
						}
					}
					sumAmount();
					tbtxtQty.get(ar).focus();
				}
			}
		});
		
		tbcmbLabel.add(ar, new ComboBox());
		tbcmbLabel.get(ar).setWidth("100%");
		tbcmbLabel.get(ar).setImmediate(true);
		tbLabelAction(ar);

		tblblUnit.add(ar, new Label());
		tblblUnit.get(ar).setWidth("100%");
		tblblUnit.get(ar).setImmediate(true);

		tblblProductRate.add(ar, new TextRead(1));
		tblblProductRate.get(ar).setWidth("100%");
		tblblProductRate.get(ar).setImmediate(true);

		tbtxtDOQty.add(ar, new TextRead(1));
		tbtxtDOQty.get(ar).setWidth("100%");
		tbtxtDOQty.get(ar).setImmediate(true);

		tbtxtDeliveredQty.add(ar, new TextRead(1));
		tbtxtDeliveredQty.get(ar).setWidth("100%");
		tbtxtDeliveredQty.get(ar).setImmediate(true);

		tbtxtBalanceQty.add(ar, new TextRead(1));
		tbtxtBalanceQty.get(ar).setWidth("100%");
		tbtxtBalanceQty.get(ar).setImmediate(true);

		tbtxtStockQty.add(ar, new TextRead(1));
		tbtxtStockQty.get(ar).setWidth("100%");
		tbtxtStockQty.get(ar).setImmediate(true);

		tbtxtQty.add(ar, new AmountCommaSeperator());
		tbtxtQty.get(ar).setWidth("100%");
		tbtxtQty.get(ar).setImmediate(true);
		tbtxtQty.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbtxtQty.get(ar).getValue().toString().isEmpty())
				{
					if(partyTbchkDO.get(0).booleanValue()==false)
					{
						if(Double.parseDouble(tbtxtQty.get(ar).getValue().toString().replaceAll(",", ""))>Double.parseDouble((tbtxtBalanceQty.get(ar).getValue().toString().isEmpty()?"0":tbtxtBalanceQty.get(ar).getValue().toString().replaceAll(",", ""))))
						{
							tbtxtQty.get(ar).setValue("0");
							tbtxtAmount.get(ar).setValue("0");
							showNotification("Warning!","Balance Limit Exceeded",Notification.TYPE_WARNING_MESSAGE);
						}
						
						else
						{
							 if(Double.parseDouble(tbtxtQty.get(ar).getValue().toString().replaceAll(",", ""))>Double.parseDouble((tbtxtStockQty.get(ar).getValue().toString().isEmpty()?"0":tbtxtStockQty.get(ar).getValue().toString().replaceAll(",", ""))))
							{
								tbtxtQty.get(ar).setValue("0");
								tbtxtAmount.get(ar).setValue("0");
								showNotification("Warning!","Stock Limit Exceeded",Notification.TYPE_WARNING_MESSAGE);	

							}	
							
						}

						/*else if(Double.parseDouble(tbtxtQty.get(ar).getValue().toString().replaceAll(",", ""))>Double.parseDouble((tbtxtStockQty.get(ar).getValue().toString().isEmpty()?"0":tbtxtStockQty.get(ar).getValue().toString().replaceAll(",", ""))))
						{
							tbtxtQty.get(ar).setValue("0");
							tbtxtAmount.get(ar).setValue("0");
							showNotification("Warning!","Stock Limit Exceeded",Notification.TYPE_WARNING_MESSAGE);	

						}*/
					}
					if(!tblblProductRate.get(ar).getValue().toString().isEmpty())
					{
						tbtxtAmount.get(ar).setValue(new CommaSeparator().setComma((Double.parseDouble(tblblProductRate.get(ar).toString()))*
								(Double.parseDouble(tbtxtQty.get(ar).toString()))));
					}
					sumAmount();
				}
				else
				{
					tbtxtAmount.get(ar).setValue("");
				}
			}
		});

		tbtxtAmount.add(ar, new TextRead(1));
		tbtxtAmount.get(ar).setWidth("100%");
		tbtxtAmount.get(ar).setImmediate(true);

		tbtxtRemarks.add(ar, new TextField());
		tbtxtRemarks.get(ar).setWidth("100%");
		tbtxtRemarks.get(ar).setImmediate(true);

		btnpreview.add(ar, new NativeButton());
		btnpreview.get(ar).setWidth("100%");
		btnpreview.get(ar).setImmediate(true);
		btnpreview.get(ar).setIcon(new ThemeResource("../icons/preview.png"));

		btnpreview.get(ar).addListener(new ClickListener()
		{

			@Override
			public void buttonClick(ClickEvent event) 
			{
				if(tbcmbProCode.get(ar).getValue()!=null)
				{
					if(Integer.parseInt(hmProductFlag.get(tbcmbProCode.get(ar).getValue()).toString()) ==1)
					{
						previewstock(ar);	
					}

				}

			}
		});
		
		

		table.addItem(new Object[]{tblblSelect.get(ar),tblblDO.get(ar),tblblDOMerge.get(ar),
				tblblDODate.get(ar),tbcmbProCode.get(ar),tbcmbLabel.get(ar),tblblUnit.get(ar),tblblProductRate.get(ar),
				tbtxtDOQty.get(ar),tbtxtDeliveredQty.get(ar),tbtxtBalanceQty.get(ar),
				tbtxtStockQty.get(ar),tbtxtQty.get(ar),tbtxtAmount.get(ar),tbtxtRemarks.get(ar),btnpreview.get(ar),tbSize.get(ar)},ar);
	}

	private void sumAmount()
	{
		double totalQty = 0;
		double totalItem = 0;
		double totalAmount = 0;

		for(int i = 0; i<tbtxtQty.size(); i++)
		{
			if(!tbtxtQty.get(i).getValue().toString().equals(""))
			{
				if(Double.parseDouble(tbtxtQty.get(i).getValue().toString().replaceAll(",", ""))>0)
				{
					totalQty = totalQty + Double.parseDouble(tbtxtQty.get(i).getValue().toString().replaceAll(",", ""));
					totalAmount = totalAmount + Double.parseDouble("0"+tbtxtAmount.get(i).getValue().toString().replaceAll(",", ""));
				}
			}
			if(tbcmbProCode.get(i).getValue()!=null)
			{
				totalItem++;
			}
		}

		table.setColumnFooter("Product Name", "Total Item =");
		table.setColumnFooter("Product Name", decimal.format(totalItem)+"");
		table.setColumnFooter("Stock Qty", "Total =");
		table.setColumnFooter("Issue Qty", new CommaSeparator().setComma(totalQty)+"");
		table.setColumnFooter("Amount", new CommaSeparator().setComma(totalAmount)+"");
	}

	private boolean doubleEntryCheck(String caption,int row)
	{
		for(int i=0; i<tbcmbProCode.size(); i++)
		{
			if(i!=row && caption.equals(tbcmbProCode.get(i).getItemCaption(tbcmbProCode.get(i).getValue())))
			{
				return false;
			}
		}
		return true;
	}

	private void validSelect(int ar)
	{
		for(int i=0; i<partyTbchkDO.size(); i++)
		{
			if(ar!=i)
			{
				partyTbchkDO.get(i).setValue(false);
			}
		}
	}

	private void DOTableinitialise()
	{
		for(int i=0; i<350; i++)
		{
			DOTableRowAdd(i);
		}
	}

	private void DOTableRowAdd(final int ar)
	{
		partyTbchkDO.add(ar,new CheckBox());
		partyTbchkDO.get(ar).setWidth("100%");
		partyTbchkDO.get(ar).setHeight("15px");
		partyTbchkDO.get(ar).setImmediate(true);
		
	

		partyTblblDONo.add(ar,new Label());
		partyTblblDONo.get(ar).setWidth("100%");
		partyTblblDONo.get(ar).setHeight("15px");
		partyTblblDONo.get(ar).setImmediate(true);
		partyTblblDONo.get(0).setValue("Without PO");

		partyTblblDemnadDate.add(ar, new Label());
		partyTblblDemnadDate.get(ar).setWidth("100%");
		partyTblblDemnadDate.get(ar).setHeight("15px");
		partyTblblDemnadDate.get(ar).setImmediate(true);

		partyTblblDOQty.add(ar, new Label());
		partyTblblDOQty.get(ar).setWidth("100%");
		partyTblblDOQty.get(ar).setImmediate(true);

		DOTable.addItem(new Object[]{partyTbchkDO.get(ar),partyTblblDONo.get(ar),
				partyTblblDemnadDate.get(ar),partyTblblDOQty.get(ar)},ar);
		
		partyTbchkDO.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				System.out.println("oooooooooooook");
				validSelect(ar);
				if(cmbPartyName.getValue()!=null)
				{
					if(partyTbchkDO.get(0).booleanValue()==true)
					{
						for(int i=1;i<partyTbchkDO.size();i++)
						{
							partyTbchkDO.get(i).setEnabled(false);
							partyTbchkDO.get(i).setValue(false);
						}
					}
					else
					{
						for(int i=1;i<partyTbchkDO.size();i++)
						{
							partyTbchkDO.get(i).setEnabled(true);	
						}
					}
					if((ar+1)==partyTblblDONo.size())
					{
						DOTableRowAdd(partyTblblDONo.size());
					}
					
					System.out.println("Hellow Bangladesh:");

					DoNO = "";
					DoNOAll = "";
					tableClear();
					getDoNo();
					if(DoNOAll!="")
					{setDoData();}
				}
				else
				{
					showNotification("Warning!", "Select party name first");
				}
			}
		});
	}

	public boolean checkDo()
	{
		boolean ret = false;
		for(int i=0; i<partyTbchkDO.size(); i++)
		{
			if(partyTbchkDO.get(i).booleanValue()==true)
			{
				ret = true;
			}
		}
		return ret;
	}

	private void getDoNo()
	{
		for(int i=1; i<partyTbchkDO.size(); i++)
		{
			if(partyTbchkDO.get(i).booleanValue()==true)
			{
				DoNO = "'"+partyTblblDONo.get(i).getValue().toString()+"'"+","+DoNO;
				DoNOAll = DoNO.substring(0, DoNO.length()-1);
			}
		}
	}

	public String QtyWord()
	{
		String QtyWord="";
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String query = "select dbo.numberQty('"+GetQty()+"')";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				QtyWord = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return QtyWord;
	}

	public Double GetQty()
	{
		Double Qty = 0.0;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String query = " select sum(mChallanQty) from tbDeliveryChallanDetails where" +
					" vGatePassNo='"+txtGatePassNo.getValue().toString()+"' ";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				Qty = Double.parseDouble(iter.next().toString());
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return Qty;
	}

	private void previewChallanEvent()
	{
		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();

			/*String queryAll = "select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,(select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) productCode,DCD.mChallanQty,DCD.vProductUnit,(select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId) vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode," +
					"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo," +
					"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 1' vCopy,'DELIVERY CHALLAN'" +
					" rptHeader,1 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " +
					"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox," +
					"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0" +
					"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs," +
					"(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) " +
					"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " +
					"like '"+txtChallanNo.getValue().toString()+"' union all " +

					"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,(select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) productCode,DCD.mChallanQty,DCD.vProductUnit,(select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId) vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode," +
					"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo," +
					"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 2' vCopy,'DELIVERY CHALLAN'" +
					" rptHeader,2 serial,ISNULL(case when CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " +
					"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox," +
					"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0" +
					"then DCD.mChallanQty%CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs," +
					"(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) " +
					"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " +
					"like '"+txtChallanNo.getValue().toString()+"' union all " +

					"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,(select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) productCode,DCD.mChallanQty,DCD.vProductUnit,(select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId) vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode," +
					"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo," +
					"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 3' vCopy,'DELIVERY CHALLAN'" +
					" rptHeader,3 serial,ISNULL(case when CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " +
					"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox," +
					"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0" +
					"then DCD.mChallanQty%CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs," +
					"(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) " +
					"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " +
					"like '"+txtChallanNo.getValue().toString()+"' union all "+

					"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,(select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) productCode,DCD.mChallanQty,DCD.vProductUnit,(select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId) vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode," +
					"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo," +
					"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Head Office Copy' vCopy,'DELIVERY CHALLAN'" +
					" rptHeader,4 serial,ISNULL(case when CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " +
					"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox," +
					"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0" +
					"then DCD.mChallanQty%CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs," +
					"(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) " +
					"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " +
					"like '"+txtChallanNo.getValue().toString()+"' union all "+

					"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,(select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) productCode,DCD.mChallanQty,DCD.vProductUnit,(select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId) vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode," +
					"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo," +
					"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,DCD.vGatePassNo as vCopy,'GATE PASS'" +
					" rptHeader,5 serial,ISNULL(case when CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " +
					"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox," +
					"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0" +
					"then DCD.mChallanQty%CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs," +
					"(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId) " +
					"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " +
					"like '"+txtChallanNo.getValue().toString()+"' order by serial,DCD.vProductName";*/

			/*	
			String queryAll= "select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit,isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
					         +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					         +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 1' vCopy,'DELIVERY CHALLAN' "
					         +"rptHeader,1 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					         +"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
				             +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') "	 
					         +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " 
					         +"like '"+txtChallanNo.getValue().toString()+"' union all "

					         +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit,isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
					         +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					         +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 2' vCopy,'DELIVERY CHALLAN' "
					         +"rptHeader,2 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					         +"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
				             +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') "	 
					         +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " 
					         +"like '"+txtChallanNo.getValue().toString()+"' union all "

					         +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit,isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
					         +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					         +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 3' vCopy,'DELIVERY CHALLAN' "
					         +"rptHeader,3 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					         +"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
				             +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') "	 
					         +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " 
					         +"like '"+txtChallanNo.getValue().toString()+"' union all "

					          +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit,isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
					         +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					         +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Head Office Copy' vCopy,'DELIVERY CHALLAN' "
					         +"rptHeader,4 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					         +"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
				             +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') "	 
					         +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " 
					         +"like '"+txtChallanNo.getValue().toString()+"' union all "

					          +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName,case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit,isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'')  vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, "
					         +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, "
					         +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,DCD.vGatePassNo as vCopy,'GATE PASS' "
					         +"rptHeader,5 serial,ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vBox, "
					         +"ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 "
					         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) vPcs, "
				             +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') "	 
					         +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo " 
					         +"like '"+txtChallanNo.getValue().toString()+"' ";*/


			String queryAll= "select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
					         +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
					         +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 1' vCopy,'DELIVERY CHALLAN'  "
					         +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
					         +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
					         +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
					         +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
					         +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
					         +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo "
					         +"like '"+txtChallanNo.getValue().toString()+"' "

					        +"union all "  

					        +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
					        +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
					        +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 2' vCopy,'DELIVERY CHALLAN'  "
					        +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					        +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
					        +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
					        +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
					        +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
					        +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
					        +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
					        +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo "
					        +"like '"+txtChallanNo.getValue().toString()+"' "

							+"union all " 

                            +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
                            +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
                            +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Customer Copy 3' vCopy,'DELIVERY CHALLAN'  "
                            +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                            +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
                            +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
                            +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                            +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
                            +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
                            +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
                            +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo "
                            +"like '"+txtChallanNo.getValue().toString()+"' "

	                        +"union all " 
	                        
                          +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
                          +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
                          +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,'Head Office Copy' vCopy,'DELIVERY CHALLAN'  "
                          +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                          +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
                          +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
                          +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                          +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
                          +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
                          +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
                          +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo "
                          +"like '"+txtChallanNo.getValue().toString()+"' "

                          +"union all " 
                          
                          +"select DCD.vChallanNo,DCD.vGatePassNo,DCD.vProductId,DCD.vProductName, case when DCD.vProductId like '%FI%' then  (select vFgCode from tbFinishedProductInfo where vProductId = DCD.vProductId) else (select vProductCode from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end productCode,DCD.mChallanQty,DCD.vProductUnit, case when DCD.vProductId like '%FI%' then isnull((select vSizeName from tbFinishedProductInfo fi where dcd.vProductId = fi.vProductId),'') else(select vSizeName from tb3rdPartylabelInformation where vLabelCode=DCD.vProductId)end vBox1, DCI.vPartyName,(select vPartryCode from tbPartyInfo where DCI.vPartyId = partyCode) pCode, " 
                          +"DCI.dEntryTime,DCI.vUserName,DCI.vPartyAddress,DCI.vPartyMobile,dCD.vDoNo, dCD.dDoDate,DCI.dChallanDate,DCI.vDelChallanNo, " 
                          +"DCI.vVatChallanNo,DCI.vDriverName,DCI.vDriverMobile, DCI.vTruckNo,DCI.vDestination,DCI.vRemarks,DCD.vGatePassNo as vCopy,'GATE PASS'  "
                          +"rptHeader,1 serial,case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                          +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))>0 " 
                          +"then DCD.mChallanQty/CONVERT(money,(select vSizeName from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0)end  vBox, " 
                          +"case when DCD.vProductId like '%FI%' then  ISNULL(case when CONVERT(money,(select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))>0 " 
                          +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId))end,0) else ISNULL(case when CONVERT(money,(select vSizeName from tb3rdPartylabelInformation fi where vLabelCode = DCD.vProductId))>0 " 
                          +"then DCD.mChallanQty%CONVERT(money,(select Cast(vSizeName as int) from tb3rdPartylabelInformation  where vLabelCode = DCD.vProductId))end,0) end vPcs, "
                          +"isnull((select vSizeName from tbFinishedProductInfo fi where fi.vProductId = DCD.vProductId),'') " 	 
                          +"from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on DCI.vGatePassNo=DCD.vGatePassNo where DCD.vChallanNo "
                          +"like '"+txtChallanNo.getValue().toString()+"' ";


			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("user", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("userName", sessionBean.getUserName());
			hm.put("Date",reportTime.getTime);
			hm.put("Author", sessionBean.getUserName());
			hm.put("logo", sessionBean.getCompanyLogo());

			if(queryValueCheck(queryAll))
			{
				hm.put("sql", queryAll);

				Window win = new ReportViewer(hm,"report/account/DoSales/rptDeliveryChallan.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}


	private void previewstock(int index)
	{
		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();

			//String queryAll ="select * from funcMasterProductReadyForSale('"+tbcmbProCode.get(index).getValue()+"','','"+dFormat.format(dDate.getValue())+"')";


			String queryAll="select *,  case when productId like '%Fi%' then (select vUnitName from tbFinishedProductInfo where vProductId=productId)  when productId like '%semi%' then  (select unit from tbSemiFgInfo where semiFgCode=productId)  else '' end unit  from funcMasterProductReadyForSale('"+tbcmbProCode.get(index).getValue()+"','"+tbcmbProCode.get(index).getItemCaption(tbcmbProCode.get(index).getValue())+"','"+dFormat.format(dDate.getValue())+"','','')";
           
			System.out.print("sql is:"+queryAll);
			
			System.out.println("query all:"+queryAll);
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("user", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("userName", sessionBean.getUserName());
			hm.put("Date",reportTime.getTime);
			hm.put("Author", sessionBean.getUserName());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("date", tbDOdateFormat.format(dDate.getValue()));
			hm.put("unit", hmProductUnit.get(tbcmbProCode.get(index).getValue()));

			if(queryValueCheck(queryAll))
			{
				hm.put("sql", queryAll);

				Window win = new ReportViewer(hm,"report/production/rptIngredientStock.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void previewVatChallanEvent()
	{
		String queryAll = null;
		HashMap<String, Object> hm = new HashMap<String, Object>();
		String query ="";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			try
			{
				if(cmbPartyName.getValue().equals("47")){
					query = " select SUM(mChallanQty)mChallanQty,"
							+ "0.00 mVat, SUM(mChallanQty*mProductRate) mAmount, (0.00 + SUM(mChallanQty*mProductRate)) "
							+ "mVatPlusAmount, dbo.number(SUM((mChallanQty*mProductRate)) +0.00)+ ' Only' "
							+ "vInword from tbDeliveryChallanDetails where vChallanNo= '"+txtChallanNo.getValue().toString()+"'";

				}
				else
				{
					query = "select SUM(mChallanQty)mChallanQty,(SUM(mChallanQty*mProductRate)*15)/100 mVat,"+
							" SUM(mChallanQty*mProductRate) mAmount, ((SUM(mChallanQty*mProductRate)*15)/100 +"+
							" SUM(mChallanQty*mProductRate)) mVatPlusAmount, dbo.number(SUM((mChallanQty*mProductRate)"+
							" +((mChallanQty*mProductRate)*15)/100))+ ' Only' vInword from tbDeliveryChallanDetails where"+
							" vChallanNo = '"+txtChallanNo.getValue().toString()+"'";
				}
				List<?> lst = session.createSQLQuery(query).list();
				for(Iterator<?> iter = lst.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();
					hm.put("sumChallanQty", element[0]);
					hm.put("sumVat", element[1]);
					hm.put("sumAmount", element[2]);
					hm.put("sumVatPlusAmount", element[3]);
					hm.put("inWord", element[4]);
				}
			}
			catch(Exception ex)
			{
				showNotification("Error", ex+"", Notification.TYPE_ERROR_MESSAGE);
			}
			if(cmbPartyName.getValue().equals("47")){
				queryAll = " select dci.vPartyName,dci.vDestinationTIN,DCD.mProductRate,dci.vPartyAddress,dci.vDestination,dci.vTruckNo,dci.vVatChallanNo as vChallanNo,dci.dChallanDate,DATEADD(HH,1,dci.dChallanDate) dActualDate,DATEADD(DAY,1,dci.dChallanDate) dActualDateDay,"+
						" dcd.vProductName,dcd.mChallanQty,dcd.vProductUnit,(dcd.mChallanQty*DCD.mProductRate) mAmount,0 mTotalVat,"+
						" ((dcd.mChallanQty*DCD.mProductRate)+0) mTotalAmount,dbo.number((dcd.mChallanQty*DCD.mProductRate)+0) vInword,"+
						" 'Copy-1' Copy,1 serial "+
						" from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on"+
						" DCI.vGatePassNo=DCD.vGatePassNo where dci.vChallanNo = '"+txtChallanNo.getValue().toString()+"'"+
						" union "+
						" select dci.vPartyName,dci.vDestinationTIN,DCD.mProductRate,dci.vPartyAddress,dci.vDestination,dci.vTruckNo,dci.vVatChallanNo as vChallanNo,dci.dChallanDate,DATEADD(HH,1,dci.dChallanDate) dActualDate,DATEADD(Day,1,dci.dChallanDate) dActualDateDay,"+
						" dcd.vProductName,dcd.mChallanQty,dcd.vProductUnit,(dcd.mChallanQty*DCD.mProductRate) mAmount,0 mTotalVat,"+
						" ((dcd.mChallanQty*DCD.mProductRate)+0) mTotalAmount,dbo.number((dcd.mChallanQty*DCD.mProductRate)+0) vInword,"+
						" 'Copy-2' Copy,2 serial"+
						" from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on"+
						" DCI.vGatePassNo=DCD.vGatePassNo where dci.vChallanNo = '"+txtChallanNo.getValue().toString()+"'"+
						" union "+
						" select dci.vPartyName,dci.vDestinationTIN,DCD.mProductRate,dci.vPartyAddress,dci.vDestination,dci.vTruckNo,dci.vVatChallanNo as vChallanNo,dci.dChallanDate,DATEADD(HH,1,dci.dChallanDate) dActualDate,DATEADD(DAY,1,dci.dChallanDate) dActualDateDay,"+
						" dcd.vProductName,dcd.mChallanQty,dcd.vProductUnit,(dcd.mChallanQty*DCD.mProductRate) mAmount,0 mTotalVat,"+
						" ((dcd.mChallanQty*DCD.mProductRate)+0) mTotalAmount,dbo.number((dcd.mChallanQty*DCD.mProductRate)+0) vInword,"+
						" 'Copy-3' Copy,3 serial"+
						" from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on"+
						" DCI.vGatePassNo=DCD.vGatePassNo where dci.vChallanNo = '"+txtChallanNo.getValue().toString()+"'"+
						" order by serial ";
			}
			else
			{
				queryAll = " select dci.vPartyName,dci.vDestinationTIN,DCD.mProductRate,dci.vPartyAddress,dci.vDestination,dci.vTruckNo,dci.vVatChallanNo as vChallanNo,dci.dChallanDate,DATEADD(HH,1,dci.dChallanDate) dActualDate,DATEADD(DAY,1,dci.dChallanDate) dActualDateDay,"+
						" dcd.vProductName,dcd.mChallanQty,dcd.vProductUnit,(dcd.mChallanQty*DCD.mProductRate) mAmount,((dcd.mChallanQty*DCD.mProductRate)*15)/100 mTotalVat,"+
						" ((dcd.mChallanQty*DCD.mProductRate)+((dcd.mChallanQty*DCD.mProductRate)*15)/100) mTotalAmount,dbo.number(((dcd.mChallanQty*DCD.mProductRate)+((dcd.mChallanQty*DCD.mProductRate)*15)/100)) vInword,"+
						" 'Copy-1' Copy,1 serial "+
						" from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on"+
						" DCI.vGatePassNo=DCD.vGatePassNo where dci.vChallanNo = '"+txtChallanNo.getValue().toString()+"'"+
						" union "+
						" select dci.vPartyName,dci.vDestinationTIN,DCD.mProductRate,dci.vPartyAddress,dci.vDestination,dci.vTruckNo,dci.vVatChallanNo as vChallanNo,dci.dChallanDate,DATEADD(HH,1,dci.dChallanDate) dActualDate,DATEADD(Day,1,dci.dChallanDate) dActualDateDay,"+
						" dcd.vProductName,dcd.mChallanQty,dcd.vProductUnit,(dcd.mChallanQty*DCD.mProductRate) mAmount,((dcd.mChallanQty*DCD.mProductRate)*15)/100 mTotalVat,"+
						" ((dcd.mChallanQty*DCD.mProductRate)+((dcd.mChallanQty*DCD.mProductRate)*15)/100) mTotalAmount,dbo.number(((dcd.mChallanQty*DCD.mProductRate)+((dcd.mChallanQty*DCD.mProductRate)*15)/100)) vInword,"+
						" 'Copy-2' Copy,2 serial"+
						" from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on"+
						" DCI.vGatePassNo=DCD.vGatePassNo where dci.vChallanNo = '"+txtChallanNo.getValue().toString()+"'"+
						" union "+
						" select dci.vPartyName,dci.vDestinationTIN,DCD.mProductRate,dci.vPartyAddress,dci.vDestination,dci.vTruckNo,dci.vVatChallanNo as vChallanNo,dci.dChallanDate,DATEADD(HH,1,dci.dChallanDate) dActualDate,DATEADD(DAY,1,dci.dChallanDate) dActualDateDay,"+
						" dcd.vProductName,dcd.mChallanQty,dcd.vProductUnit,(dcd.mChallanQty*DCD.mProductRate) mAmount,((dcd.mChallanQty*DCD.mProductRate)*15)/100 mTotalVat,"+
						" ((dcd.mChallanQty*DCD.mProductRate)+((dcd.mChallanQty*DCD.mProductRate)*15)/100) mTotalAmount,dbo.number(((dcd.mChallanQty*DCD.mProductRate)+((dcd.mChallanQty*DCD.mProductRate)*15)/100)) vInword,"+
						" 'Copy-3' Copy,3 serial"+
						" from tbDeliveryChallanDetails as DCD inner join tbDeliveryChallanInfo as DCI on"+
						" DCI.vGatePassNo=DCD.vGatePassNo where dci.vChallanNo = '"+txtChallanNo.getValue().toString()+"'"+
						" order by serial ";
			}
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("user", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("userName", sessionBean.getUserName());
			hm.put("Date",reportTime.getTime);
			hm.put("Author", sessionBean.getUserName());
			hm.put("logo", sessionBean.getCompanyLogo());
			if(queryValueCheck(queryAll))
			{
				hm.put("sql", queryAll);
				Window win = new ReportViewer(hm,"report/account/DoSales/rptVatChallan.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning!!","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}
}