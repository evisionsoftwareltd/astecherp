package acc.appform.asset;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.accountsSetup.CostInformation;
import acc.appform.accountsSetup.GroupCreate;
import acc.appform.accountsSetup.SubGroupCreate;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.astechac.AstechacApplication;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class FixedAssetOpen extends Window
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "","Exit");

	private CommaSeparator cms = new CommaSeparator();
	private DecimalFormat df = new DecimalFormat("#0");
	private NumberFormat frmt = new DecimalFormat("#0.00");

	private boolean isUpdate = false;
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private TextField assetFindId = new TextField();
	
	private SessionBean sessionBean;
	
	private AbsoluteLayout mainLayout;
	
	private HorizontalLayout btnLayout = new HorizontalLayout();
	
	private Label lblAssetId;
	private Label lblGroup;
	private Label lblSubGroup;
	private Label lblLedgerName;
	private Label lblCostCenter;
	private Label lblAssetName;
	private Label lblAssetOpenBal;
	private Label lblAssetBalType;
	private Label lblDepOpenBal;
	private Label lblDepBalType;
	private Label lblIdentificationMark;
	private Label lblDept;
	private Label lblLocation;
	private Label lblSupplierName;
	private Label lblSupplierAddress;
	private Label lblDateAcquisition;
	private Label lblDateInstall;
	private Label lblBillDate;
	private Label lblBillNo;
	private Label lblChallNo;
	private Label lblProcCost;
	private Label lblInstallCost;
	private Label lblOtherCost;
	private Label lblTotalCost;
	private Label lblLifeYear;
	private Label lblDepSystem;
	private Label lblDepreciation;
	private Label lblAnnualDepreciation;
	
	private TextField assetId;
	private TextField ledgerName;
	private TextField assetName;
	private TextField identificationMark;
	private TextField dept;
	private TextField location;
	private TextField supplierName;
	private TextField supplierAddress;
	private TextField billNo;
	private TextField challNo;
	private TextField totalCost;
	private TextField depreciation;
	private TextField annualDepreciation;
	
	private AmountCommaSeperator assetOpenBal;
	private AmountCommaSeperator depOpenBal;
	private AmountCommaSeperator procCost;
	private AmountCommaSeperator installCost;
	private AmountCommaSeperator otherCost;
	
	private AmountField lifeYear;
	
	private NativeSelect assetBalType;
	private NativeSelect depBalType;
	private NativeSelect depSystem;
	
	private ComboBox group;
	private ComboBox subGroup;
	private ComboBox costCenter;
	
	private NativeButton btnGroup;
	private NativeButton btnSubGroup;
	private NativeButton btnCostCenter;
	
	private DateField dateAcquisition;
	private DateField dateInstall;
	private DateField billDate;
	
	private String lcw = "230px";
	private String rcw = "190px";

	public FixedAssetOpen(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("FIXED ASSET OPENING BALANCE :: "+sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainLayout();
		setContent(mainLayout);
		
		Component allComp[] = {group, subGroup, ledgerName, costCenter, assetName, assetOpenBal, depOpenBal, identificationMark, dept, location, supplierName, supplierAddress, dateAcquisition, dateInstall, billDate, billNo, challNo, procCost, installCost, otherCost, lifeYear, depSystem, depreciation, button.btnSave, button.btnNew};

		setButtonAction();
		btnIni(true);
		txtEnable(false);
		groupInitialise();
		costCenterInitialize();
		new FocusMoveByEnter(this,allComp);
		button.btnNew.focus();
	}
	
	private void setButtonAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newBtnAction();
				assetFindId.setValue("");
				group.focus();
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//updateBtnAction(event);
				//if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(dateAcquisition.getValue())))
				//{
					updateBtnAction(event);
				/*}
				else
				{
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
				}*/
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//saveBtnAction();
				//if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(dateAcquisition.getValue())))
				//{
					saveBtnAction();
				/*}
				else
				{
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
				}*/
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{				
				btnIni(true);
				txtEnable(false);
				txtClear();
				assetFindId.setValue("");
			}
		});

		button.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//deleteBtnAction(event);
				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(dateAcquisition.getValue())))
				{
					deleteBtnAction(event);
				}
				else
				{
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findBtnAction();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		group.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				subgroupInitialise();
			}
		});
		
		depreciation.addListener(new TextChangeListener() 
		{
			public void textChange(TextChangeEvent event) 
			{
				float d = Float.valueOf("0"+event.getText());
				System.out.println(d);
				
				if(d>0.0)
				{
				//	lifeYear.setValue(df.format((100/d)));
					annualDepreciation.setReadOnly(false);
					annualDepreciation.setValue(Double.valueOf(totalCost.getValue().toString().replace(",", ""))/(100/d));
				//	annualDepreciation.setValue(cms.setComma(Double.valueOf(annualDepreciation.getValue().toString())));
					annualDepreciation.setReadOnly(true);
				}
				else
				{
					//depreciation.setValue(d);
					//lifeYear.setValue("");
					annualDepreciation.setReadOnly(false);
					annualDepreciation.setValue(0);
				//	annualDepreciation.setValue(cms.setComma(Double.valueOf(annualDepreciation.getValue().toString())));
					annualDepreciation.setReadOnly(true);
				}
			}
		});
		
		lifeYear.addListener(new TextChangeListener() 
		{
			public void textChange(TextChangeEvent event) 
			{
				float ly = Float.valueOf("0"+event.getText());
				
				if(ly>0.0)
				{
					depreciation.setValue(df.format(100/ly));
					annualDepreciation.setReadOnly(false);
					annualDepreciation.setValue(Double.valueOf("0"+totalCost.getValue().toString().replace(",",""))/ly);
					annualDepreciation.setValue(cms.setComma(Double.valueOf(annualDepreciation.getValue().toString())));
					annualDepreciation.setReadOnly(true);
				}
				else
				{
				//	depreciation.setValue(0);
					annualDepreciation.setReadOnly(false);
					annualDepreciation.setValue(0);
					annualDepreciation.setValue(cms.setComma(Double.valueOf(annualDepreciation.getValue().toString())));
					annualDepreciation.setReadOnly(true);
				}
			}
		});
		
		assetOpenBal.addListener(new TextChangeListener() 
		{
			public void textChange(TextChangeEvent event) 
			{
				//ttlValueSet(event);
				procCost.setReadOnly(false);
				installCost.setReadOnly(false);
				otherCost.setReadOnly(false);
				totalCost.setReadOnly(false);
				
			//	System.out.println(totalCost);
	//			totalCost.setValue((Double.valueOf("0"+event.getText().replace(",",""))+Double.valueOf("0"+installCost.getValue().replace(",", ""))+
	//					Double.valueOf("0"+otherCost.getValue().replace(",",""))));
				//System.out.println(totalCost);
				procCost.setValue(cms.setComma(Double.valueOf(event.getText().replace(",",""))));
				installCost.setValue("0");
				otherCost.setValue("0");
				totalCost.setValue(cms.setComma(Double.valueOf(event.getText().replace(",",""))));
				
				procCost.setReadOnly(true);
				installCost.setReadOnly(true);
				otherCost.setReadOnly(true);
				totalCost.setReadOnly(true);
			//	cms.setComma(Double.valueOf(totalCost.getValue().toString()));
			//	totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
			}
		});

		procCost.addListener(new TextChangeListener() 
		{
			public void textChange(TextChangeEvent event) 
			{
				//ttlValueSet(event);
				totalCost.setReadOnly(false);
			//	System.out.println(totalCost);
				totalCost.setValue((Double.valueOf("0"+event.getText().replace(",",""))+Double.valueOf("0"+installCost.getValue().replace(",", ""))+
						Double.valueOf("0"+otherCost.getValue().replace(",",""))));
				//System.out.println(totalCost);
				totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
				totalCost.setReadOnly(true);
			//	cms.setComma(Double.valueOf(totalCost.getValue().toString()));
			//	totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
			}
		});

		installCost.addListener(new TextChangeListener() 
		{
			public void textChange(TextChangeEvent event) 
			{
				totalCost.setReadOnly(false);
				totalCost.setValue((Double.valueOf("0"+procCost.getValue().replace(",",""))+Double.valueOf("0"+event.getText().replace(",",""))+
						Double.valueOf("0"+otherCost.getValue().replace(",",""))));
				totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
				totalCost.setReadOnly(true);
				//cms.setComma(Double.valueOf(totalCost.getValue().toString()));
			//	totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
			}
		});
		
		otherCost.addListener(new TextChangeListener() 
		{
			public void textChange(TextChangeEvent event) 
			{
				totalCost.setReadOnly(false);
				totalCost.setValue((Double.valueOf("0"+procCost.getValue().replace(",",""))+Double.valueOf("0"+installCost.getValue().replace(",",""))+
						Double.valueOf("0"+event.getText().replace(",",""))));
				
				//cms.setComma(Double.valueOf(totalCost.getValue().toString()));
				//cms.setComma(Double.parseDouble(totalCost.getValue().toString()))
			//	double a = 152222;
				totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
				//System.out.println(totalCost);
				totalCost.setReadOnly(true);
			}
		});
		
		btnGroup.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				groupLink();
			}
		});
		
		btnSubGroup.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				subGroupLink();
			}
		});
		
		btnCostCenter.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				costLink();
			}
		});
	}
	
	public void costLink()
	{
		Window win = new CostInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				costCenterInitialize();
			}
		});
		
		this.getParent().addWindow(win);
	}
	
	public void subGroupLink()
	{
		Window win = new SubGroupCreate(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				subgroupInitialise();
			}
		});
		
		this.getParent().addWindow(win);
	}
	
	public void groupLink()
	{
		Window win = new GroupCreate(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				groupInitialise();
			}
		});
		
		this.getParent().addWindow(win);
	}
	
	private void saveBtnAction()
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
						updateData();
						button.btnNew.focus();
					}
				}
			});
		}
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						if(ledgerCheck())
						{
							insertData();
							button.btnNew.focus();
						}
					}
				}
			});
		}
	}
	
	private void insertData()
	{
		if(sessionBean.isSubmitable())
		{
//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(dateAcquisition.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
//					&&
//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(dateAcquisition.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
//			{
				Transaction tx = null;
				try
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					tx = session.beginTransaction();
					String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(dateAcquisition.getValue())+"')").list().iterator().next().toString();
					String voucher =  "voucher"+fsl;
					String cheque =  "chequedetails"+fsl;

					String parentId = "";
					String createFrom = "";

					if(subGroup.getValue() != null)
					{
						parentId = subGroup.getValue().toString();
						createFrom ="A1-"+group.getValue()+"-"+subGroup.getValue();
					}
					else if(group.getValue() != null)
					{
						parentId = group.getValue().toString();
						createFrom = "A1-"+group.getValue();
					}
					else
					{
						parentId = "A1";
						createFrom = "A1";
					}
					
					if(depreciation.getValue().toString().equals(""))
					{
						depreciation.setValue("0");
					}

					//String assetGroup = "";
					//String asetSubGroup = "";
					String assetNameWithSubGroup = "";
					if(subGroup.getValue()==null)
					{
						assetNameWithSubGroup = assetName.getValue().toString();
					}
					else
					{
						assetNameWithSubGroup = assetName.getValue()+" ("+subGroup.getItemCaption(subGroup.getValue())+")";
					}
					
					String cmbSubGroup = subGroup.getValue()==null ? "" : subGroup.getValue().toString();

					Iterator iter = session.createSQLQuery("SELECT 'AL'+CAST(ISNULL(max(cast(substring(Ledger_Id,3,len(Ledger_Id)) AS integer))+1,101) AS varchar) "+
							"FROM tbLedger WHERE SUBSTRING(Ledger_Id,1,1) = 'A' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					String ledgerId = iter.next().toString();

					//tbLedger Table
					String sql = "INSERT INTO tbLedger(Ledger_Id,Ledger_Name,Creation_Year,Parent_Id,Create_From,userId,userIp,entryTime, companyId) VALUES('"+ledgerId+"','"+
					ledgerName.getValue()+"',(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo="+fsl+"),'"+parentId+"','"+createFrom+"','"+
					sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					//tbFixedAsset Table
					//					sql = "INSERT INTO tbFixedAsset(VoucherNo,AssetID,dAcquisition,dInstallation,vDescription,vGroupID,vSubGroupID,"+
					//					"vLedgerID,vLedgerName,AssetDrAmount,AssetCrAmount,DepreciationDrAmount,DepreciationCrAmount,vSupplierName,vSupplierAddress,mProcurement,mInstallation,mOther,mTotal,iLife,mResidualValue,vDepreciationSystem,"+
					//					"iDepreciationPer,mAnnualDepreciation,vDepreciationPolicy,mWrittenValue,vIdentification,vLocation,vDepartment,"+
					//					"VoucherType,[vBill/L/CNo],DelChallanNo,BillDate,[Type], companyId) VALUES('','"+ledgerId+"','"+dtfYMD.format(dateAcquisition.getValue())+
					//					"','"+dtfYMD.format(dateInstall.getValue())+"','"+assetName.getValue()+"','"+group.getValue()+"','"+subGroup.getValue()+"','"+
					//					ledgerId+"','"+ledgerName.getValue()+"',"+(assetBalType.getValue().equals(1)? "0"+assetOpenBal.getValue():"0")+","+
					//					(assetBalType.getValue().equals(2)? "0"+assetOpenBal.getValue():"0")+","+(depBalType.getValue().equals(1)? "0"+depOpenBal.getValue():"0")+","+
					//					(depBalType.getValue().equals(2)? "0"+depOpenBal.getValue():"0")+ ",'"+supplierName.getValue()+"','"+supplierAddress.getValue()+"',0"+procCost.getValue()+",0"+installCost.getValue()+",0"+
					//					otherCost.getValue()+","+(Double.valueOf("0"+procCost.getValue())+Double.valueOf("0"+installCost.getValue())+Double.valueOf("0"+
					//							otherCost.getValue()))+",0"+lifeYear.getValue()+",0,'"+depSystem.getValue()+"',0"+depreciation.getValue()+",0"+annualDepreciation.getValue()+
					//							",'',0,'"+identificationMark.getValue()+"','"+location.getValue()+"','"+dept.getValue()+"','','"+billNo.getValue()+"','"+challNo.getValue()+"','"+
					//							dtfYMD.format(billDate.getValue())+"','O', '"+ sessionBean.getCompanyId() +"')";

					sql = "Insert into tbFixedAsset(VoucherNo, AssetID, dAcquisition, dInstallation, vDescription, vGroupID, vSubGroupID, vLedgerID,vLedgerName, AssetDrAmount, AssetCrAmount, DepreciationDrAmount, DepreciationCrAmount, vSupplierName, vSupplierAddress, mProcurement, mInstallation, mOther, mTotal, iLife, mResidualValue, vDepreciationSystem, iDepreciationPer, mAnnualDepreciation, vDepreciationPolicy, mWrittenValue, vIdentification, vLocation, vDepartment, VoucherType, [vBill/L/CNo], DelChallanNo, BillDate, Type, companyId, costId)" +
					" values ('', '"+ledgerId+"', '"+dtfYMD.format(dateAcquisition.getValue())+"','"+dtfYMD.format(dateInstall.getValue())+"', '"+assetName.getValue()+"', '"+ group.getValue() +"', '"+ cmbSubGroup +"', '"+ ledgerId +"','"+ ledgerName.getValue().toString() +"','"+(assetBalType.getValue().equals(1)? "0"+assetOpenBal.getValue():"0")+"', 0, 0,'"+(depBalType.getValue().equals(2)? "0"+depOpenBal.getValue():"0")+"', '"+ supplierName.getValue() +"', '"+ supplierAddress.getValue() +"', '"+ procCost.getValue() +"', '"+ installCost.getValue() +"', '"+ otherCost.getValue() +"', '"+ totalCost.getValue().toString().replace(",", "") +"', '"+ Double.valueOf(lifeYear.getValue().toString()) +"', 0, '"+ depSystem.getValue() +"', "+ Integer.valueOf(depreciation.getValue().toString()) +", '"+ annualDepreciation.getValue().toString().replace(",", "") +"', '', 0, '"+ identificationMark.getValue() +"', '"+ location.getValue() +"', '"+ dept.getValue() +"', '', '"+ billNo.getValue() +"', '"+ challNo.getValue() +"', '"+ dtfYMD.format(billDate.getValue()) +"', 'O', '"+ sessionBean.getCompanyId() +"', '"+ costCenter.getValue() +"')";
					session.createSQLQuery(sql).executeUpdate();

					//AssetOpBalance Table 
					sql = "INSERT INTO tbAssetOpBalance(AssetId,AssetDrAmount,AssetCrAmount,DepreciationDrAmount,DepreciationCrAmount,Op_Year, companyId) VALUES('"+
					ledgerId+"',"+(assetBalType.getValue().equals(1)? "0"+assetOpenBal.getValue():"0")+","+(assetBalType.getValue().equals(2)? "0"+assetOpenBal.getValue():"0")+
					","+(depBalType.getValue().equals(1)? "0"+depOpenBal.getValue():"0")+","+(depBalType.getValue().equals(2)? "0"+depOpenBal.getValue():"0")+
					",(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo = "+fsl+"), '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					//LedgerOpenBalance Table
					sql = "INSERT INTO tbLedger_Op_Balance(Ledger_Id,DrAmount,CrAmount,Op_Year,userId,userIp,entryTime, companyId) VALUES('"+ledgerId+"',"+
					(assetBalType.getValue().equals(1)? "0"+assetOpenBal.getValue():"0")+","+(assetBalType.getValue().equals(2)? "0"+assetOpenBal.getValue():"0")+
					",(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo = "+fsl+"),'"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					tx.commit();
					showNotification("All Information save successfully.");
					assetId.setReadOnly(false);
					assetId.setValue(ledgerId);
					assetId.setReadOnly(true);
					txtEnable(false);
					btnIni(true);
				}
				catch(Exception exp)
				{
					this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
					tx.rollback();
				}
			}
			else
				showNotification("Warning :","Please Enter Date Between Fiscal Year.",Notification.TYPE_WARNING_MESSAGE);
//		}
//		else
//			this.getParent().showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);

	}
	
	private void subgroupInitialise()
	{
		try
		{
			Transaction tx = null;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			try
			{
				subGroup.removeAllItems();
			}
			catch(Exception e)
			{
				
			}
			
			tx = session.beginTransaction();
			List g = session.createSQLQuery("SELECT sub_Group_Id,sub_Group_Name FROM TbSub_Group WHERE group_Id = '"+group.getValue()+"'").list();

			for (Iterator iter = g.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				subGroup.addItem(element[0].toString());
				subGroup.setItemCaption(element[0].toString(), element[1].toString());
			}
			//subGroup.setNullSelectionAllowed(true);				
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error ",exp.toString() ,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void findBtnAction()
	{
		Window win = new SearchFixedAssetOpen(sessionBean,assetFindId);
		win.center();
		this.getParent().addWindow(win);
		win.setModal(true);
		win.setCloseShortcut(KeyCode.ESCAPE);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(assetFindId.getValue().toString().length()>0)
				{
					findInitialise();
				}
			}
		});
	}
	
	private void deleteBtnAction(ClickEvent e)
	{
		if(sessionBean.isDeleteable())
		{
			if(assetFindId.getValue().toString().trim().length()>0)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete asset id "+assetFindId.getValue()+"?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{				
							if(isValidDelete())
							{
								deleteData();
							}
						}
					}
				});
			}
			else
			{
				this.getParent().showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for delete.",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void deleteData()
	{
		Transaction tx = null;
		
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String ledgerId = assetFindId.getValue().toString();

			session.createSQLQuery("DELETE FROM tbLedger WHERE Ledger_Id = '"+ledgerId+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
			session.createSQLQuery("DELETE FROM tbFixedAsset WHERE AssetID = '"+ledgerId+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
			session.createSQLQuery("DELETE FROM tbAssetOpBalance WHERE AssetID = '"+ledgerId+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
			session.createSQLQuery("DELETE FROM tbLedger_Op_Balance WHERE Ledger_Id = '"+ledgerId+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
			tx.commit();
			this.getParent().showNotification("Desired Information delete successfully.");
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	
	private boolean ledgerCheck()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String cmbSubGroup = subGroup.getValue()==null?"":subGroup.getValue().toString();

			Iterator iter = session.createSQLQuery("SELECT vLedgerName FROM tbFixedAsset WHERE vLedgerName = '"+ledgerName.getValue()+"' "+
					"AND vGroupID = '"+group.getValue()+"' AND vSubGroupID = '"+cmbSubGroup+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();

			if(iter.hasNext())
			{
				this.getParent().showNotification("","Already opening balance inserted for this ledger.",Notification.TYPE_ERROR_MESSAGE);
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	
	private void updateData()
	{
		if(sessionBean.isUpdateable())
		{
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(dateAcquisition.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				String cheque =  "chequedetails"+fsl;

				String parentId = "";
				String createFrom = "";

				if(subGroup.getValue() != null)
				{
					parentId = subGroup.getValue().toString();
					createFrom ="A1-"+group.getValue()+"-"+subGroup.getValue();
				}
				else if(group.getValue() != null)
				{
					parentId = group.getValue().toString();
					createFrom = "A1-"+group.getValue();
				}
				else
				{
					parentId = "A1";
					createFrom = "A1";
				}
				
				if(depreciation.getValue().toString().equals(""))
				{
					depreciation.setValue("0");
				}

				//String assetGroup = "";
				//String asetSubGroup = "";
				//String assetNameWithSubGroup = "";
				//if(subGroup.getValue().equals(""))
				//assetNameWithSubGroup = assetName.getValue().toString();
				//else
				//assetNameWithSubGroup = assetName.getValue()+" ("+subGroup.getItemCaption(subGroup.getValue())+")";

				Iterator iter = session.createSQLQuery("SELECT 'AL'+CAST(ISNULL(max(cast(substring(Ledger_Id,3,len(Ledger_Id)) AS integer))+1,101) AS varchar) "+
				"FROM tbLedger WHERE SUBSTRING(Ledger_Id,1,1) = 'A'").list().iterator();
				 
				String ledgerId = assetFindId.getValue().toString();

				//String sql = ""; 

				session.createSQLQuery("DELETE FROM tbFixedAsset WHERE [Type] = 'O' AND AssetID = '"+ledgerId+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
				session.createSQLQuery("DELETE FROM tbAssetOpBalance WHERE AssetID = '"+ledgerId+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
				session.createSQLQuery("DELETE FROM tbLedger_Op_Balance WHERE Ledger_Id = '"+ledgerId+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();

				//Update tbLedger Table
				/*String sql = "INSERT INTO tbLedger(Ledger_Id,Ledger_Name,Creation_Year,Parent_Id,Create_From,userId,userIp,entryTime) VALUES('"+ledgerId+"','"+
				ledgerName.getValue()+"',(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE Running_Flag = 1),'"+parentId+"','"+createFrom+"','"+
				sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
				session.createSQLQuery(sql).executeUpdate();*/

				String sql = "UPDATE tbLedger SET Ledger_Name = '"+ledgerName.getValue()+"',Parent_Id = '"+parentId+"',Create_From = '"+createFrom+"',userId = '"+
				sessionBean.getUserId()+"',userIp = '"+sessionBean.getUserIp()+"',entryTime = CURRENT_TIMESTAMP, companyId = '"+ sessionBean.getCompanyId() +"' WHERE Ledger_Id = '"+ledgerId+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				//tbFixedAsset Table

				//				sql = "INSERT INTO tbFixedAsset(VoucherNo,AssetID,dAcquisition,dInstallation,vDescription,vGroupID,vSubGroupID,"+
				//				"vLedgerID,vLedgerName,AssetDrAmount,AssetCrAmount,DepreciationDrAmount,DepreciationCrAmount,vSupplierName,vSupplierAddress,mProcurement,mInstallation,mOther,mTotal,iLife,mResidualValue,vDepreciationSystem,"+
				//				"iDepreciationPer,mAnnualDepreciation,vDepreciationPolicy,mWrittenValue,vIdentification,vLocation,vDepartment,"+
				//				"VoucherType,[vBill/L/CNo],DelChallanNo,BillDate,[Type], companyId) VALUES('','"+ledgerId+"','"+dtfYMD.format(dateAcquisition.getValue())+
				//				"','"+dtfYMD.format(dateInstall.getValue())+"','"+assetName.getValue()+"','"+group.getValue()+"','"+subGroup.getValue()+"','"+
				//				ledgerId+"','"+ledgerName.getValue()+"',"+(assetBalType.getValue().equals(1)? "0"+assetOpenBal.getValue():"0")+","+
				//				(assetBalType.getValue().equals(2)? "0"+assetOpenBal.getValue():"0")+","+(depBalType.getValue().equals(1)? "0"+depOpenBal.getValue():"0")+","+
				//				(depBalType.getValue().equals(2)? "0"+depOpenBal.getValue():"0")+ ",'"+supplierName.getValue()+"','"+supplierAddress.getValue()+"',0"+procCost.getValue()+",0"+installCost.getValue()+",0"+
				//				otherCost.getValue()+","+(Double.valueOf("0"+procCost.getValue())+Double.valueOf("0"+installCost.getValue())+Double.valueOf("0"+
				//						otherCost.getValue()))+",0"+lifeYear.getValue()+",0,'"+depSystem.getValue()+"',0"+depreciation.getValue()+",0"+annualDepreciation.getValue()+
				//						",'',0,'"+identificationMark.getValue()+"','"+location.getValue()+"','"+dept.getValue()+"','','"+billNo.getValue()+"','"+challNo.getValue()+"','"+
				//						dtfYMD.format(billDate.getValue())+"','O', '"+ sessionBean.getCompanyId() +"')";
				sql = "Insert into tbFixedAsset(VoucherNo, AssetID, dAcquisition, dInstallation, vDescription, vGroupID, vSubGroupID, vLedgerID, vLedgerName, AssetDrAmount, AssetCrAmount, DepreciationDrAmount, DepreciationCrAmount, vSupplierName, vSupplierAddress, mProcurement, mInstallation, mOther, mTotal, iLife, mResidualValue, vDepreciationSystem, iDepreciationPer, mAnnualDepreciation, vDepreciationPolicy, mWrittenValue, vIdentification, vLocation, vDepartment, VoucherType, [vBill/L/CNo], DelChallanNo, BillDate, Type, companyId, costId)" +
				" values ('', '"+ledgerId+"', '"+dtfYMD.format(dateAcquisition.getValue())+"','"+dtfYMD.format(dateInstall.getValue())+"', '"+assetName.getValue()+"', '"+ group.getValue() +"', '"+ subGroup.getValue() +"', '"+ ledgerId +"','"+ledgerName.getValue()+"', "+(assetBalType.getValue().equals(1)? "0"+assetOpenBal.getValue():"0")+", "+(assetBalType.getValue().equals(2)? "0"+assetOpenBal.getValue():"0")+", "+(depBalType.getValue().equals(1)? "0"+depOpenBal.getValue():"0")+", "+(depBalType.getValue().equals(2)? "0"+depOpenBal.getValue():"0")+", '"+ supplierName.getValue() +"', '"+ supplierAddress.getValue() +"', '"+ procCost.getValue() +"', '"+ installCost.getValue() +"', '"+ otherCost.getValue() +"', "+(Double.valueOf("0"+procCost.getValue())+Double.valueOf("0"+installCost.getValue())+Double.valueOf("0"+otherCost.getValue()))+", '"+ lifeYear.getValue() +"', 0, '"+ depSystem.getValue() +"', '"+ depreciation.getValue() +"', '"+ annualDepreciation.getValue() +"', '', 0, '"+ identificationMark.getValue() +"', '"+ location.getValue() +"', '"+ dept.getValue() +"', '', '"+ billNo.getValue() +"', '"+ challNo.getValue() +"', '"+ dtfYMD.format(billDate.getValue()) +"', 'O', '"+ sessionBean.getCompanyId() +"', '"+ costCenter.getValue() +"')";

				System.out.println(sql);
				session.createSQLQuery(sql).executeUpdate();

				//AssetOpBalance Table 
				sql = "INSERT INTO tbAssetOpBalance(AssetId,AssetDrAmount,AssetCrAmount,DepreciationDrAmount,DepreciationCrAmount,Op_Year, companyId) VALUES('"+ledgerId+"',"+(assetBalType.getValue().equals(1)? "0"+assetOpenBal.getValue():"0")+","+(assetBalType.getValue().equals(2)? "0"+assetOpenBal.getValue():"0")+","+(depBalType.getValue().equals(1)? "0"+depOpenBal.getValue():"0")+","+(depBalType.getValue().equals(2)? "0"+depOpenBal.getValue():"0")+",(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo=1), '"+ sessionBean.getCompanyId() +"')";

				session.createSQLQuery(sql).executeUpdate();

				//LedgerOpenBalance Table
				sql = "INSERT INTO tbLedger_Op_Balance(Ledger_Id,DrAmount,CrAmount,Op_Year,userId,userIp,entryTime, companyId) VALUES('"+ledgerId+"',"+
				(assetBalType.getValue().equals(1)? "0"+assetOpenBal.getValue():"0")+","+(assetBalType.getValue().equals(2)? "0"+assetOpenBal.getValue():"0")+
				",(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo=1),'"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";

				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				showNotification("All Information updated successfully.");
				assetId.setReadOnly(false);
				assetId.setValue(ledgerId);
				assetId.setReadOnly(true);
				txtEnable(false);
				btnIni(true);
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
	}
	
	private boolean isValidUpdate()
	{
		try
		{
			Transaction tx = null;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String c = session.createSQLQuery("SELECT COUNT(*) FROM tbAssetOpBalance WHERE AssetId = '"+assetFindId.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator().next().toString();
			
			if(Integer.valueOf("0"+c)>1)
			{
				this.getParent().showNotification("","Already year close for this ledger. So you can not update this ledger.",Notification.TYPE_ERROR_MESSAGE);
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	
	private void updateBtnAction(ClickEvent e)
	{
		if(sessionBean.isUpdateable())
		{
			if(assetFindId.getValue().toString().trim().length()>0)
			{
				if(isValidUpdate())
				{
					btnIni(false);
					txtEnable(true);
					isUpdate = true;
				}
			}
			else
			{
				this.getParent().showNotification("Edit Failed","There are no data for Edit.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void newBtnAction()
	{
		isUpdate = false;
		btnIni(false);
		txtEnable(true);
		txtClear();
	}
	
	private void txtClear()
	{
		group.setValue(null);
		subGroup.setValue(null);
		assetId.setReadOnly(false);
		assetId.setValue("");
		assetId.setReadOnly(true);
		ledgerName.setValue("");
		costCenter.setValue(null);
		assetName.setValue("");
		assetOpenBal.setValue("");
		//assetBalType.setEnabled(t);
		depOpenBal.setValue("");
		//depBalType.setEnabled(t);
		identificationMark.setValue("");
		dept.setValue("");
		location.setValue("");
		supplierName.setValue("");
		supplierAddress.setValue("");
		//dateAcquisition.setEnabled(t);
		//dateInstall.setEnabled(t);
		//billDate.setEnabled(t);
		billNo.setValue("");
		challNo.setValue("");
		procCost.setReadOnly(false);
		procCost.setValue("");
		procCost.setReadOnly(true);
		
		installCost.setReadOnly(false);
		installCost.setValue("");
		installCost.setReadOnly(true);
		
		otherCost.setReadOnly(false);
		otherCost.setValue("");
		otherCost.setReadOnly(true);
		
		totalCost.setReadOnly(false);
		totalCost.setValue("");
		totalCost.setReadOnly(true);
		lifeYear.setValue("");
		//depSystem.setEnabled(t);
		depreciation.setValue("");
		annualDepreciation.setReadOnly(false);
		annualDepreciation.setValue("");
		annualDepreciation.setReadOnly(true);
	}
	
	private void costCenterInitialize()
	{
		try
		{
			Transaction tx = null;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			List g = session.createSQLQuery("SELECT id,costCentreName FROM tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' order by costCentreName").list();
			
			costCenter.removeAllItems();

			for (Iterator iter = g.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				
				costCenter.addItem(element[0].toString());
				costCenter.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			//System.out.println(exp);
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void groupInitialise()
	{
		try
		{
			Transaction tx = null;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			List g = session.createSQLQuery("SELECT group_Id,group_Name FROM TbMain_Group WHERE head_Id = 'A1'").list();
			
			try
			{
				group.removeAllItems();
			}
			catch(Exception e)
			{
				
			}
			for (Iterator iter = g.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				
				group.addItem(element[0].toString());
				group.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			//System.out.println(exp);
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		//button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}
	
	private void txtEnable(boolean t)
	{
		group.setEnabled(t);
		subGroup.setEnabled(t);
		ledgerName.setEnabled(t);
		costCenter.setEnabled(t);
		assetName.setEnabled(t);
		assetOpenBal.setEnabled(t);
		assetBalType.setEnabled(t);
		depOpenBal.setEnabled(t);
		depBalType.setEnabled(t);
		identificationMark.setEnabled(t);
		dept.setEnabled(t);
		location.setEnabled(t);
		supplierName.setEnabled(t);
		supplierAddress.setEnabled(t);
		dateAcquisition.setEnabled(t);
		dateInstall.setEnabled(t);
		billDate.setEnabled(t);
		billNo.setEnabled(t);
		challNo.setEnabled(t);
		procCost.setEnabled(false);
		installCost.setEnabled(false);
		otherCost.setEnabled(false);
		totalCost.setEnabled(t);
		lifeYear.setEnabled(t);
		depSystem.setEnabled(t);
		depreciation.setEnabled(t);
		annualDepreciation.setEnabled(t);
		
		btnGroup.setEnabled(t);
		btnSubGroup.setEnabled(t);
		btnCostCenter.setEnabled(t);
	}
	
	private void findInitialise()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List led = session.createSQLQuery("SELECT vGroupID,vSubGroupID,vLedgerName,vDescription,AssetDrAmount,AssetCrAmount,"+
					"DepreciationDrAmount,DepreciationCrAmount,vIdentification,vDepartment,vLocation,"+
					"vSupplierName,vSupplierAddress,dAcquisition,dInstallation,BillDate,[vBill/L/CNo],DelChallanNo,"+
					"mProcurement,mInstallation,mOther,mTotal,iLife,vDepreciationSystem,iDepreciationPer,mAnnualDepreciation, costId "+
					"FROM tbFixedAsset WHERE [Type] = 'O' AND companyId = '"+ sessionBean.getCompanyId() +"' AND AssetID = '"+assetFindId.getValue()+"'").list();

			String Sql;
			Sql = "SELECT vGroupID,vSubGroupID,vLedgerName,vDescription,AssetDrAmount,AssetCrAmount,"+
			"DepreciationDrAmount,DepreciationCrAmount,vIdentification,vDepartment,vLocation,"+
			"vSupplierName,vSupplierAddress,dAcquisition,dInstallation,BillDate,[vBill/L/CNo],DelChallanNo,"+
			"mProcurement,mInstallation,mOther,mTotal,iLife,vDepreciationSystem,iDepreciationPer,mAnnualDepreciation, costId "+
			"FROM tbFixedAsset WHERE [Type] = 'O' AND companyId = '"+ sessionBean.getCompanyId() +"' AND AssetID = '"+assetFindId.getValue()+"'";

			//System.out.println(Sql);
			
			for(Iterator iter = led.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				assetId.setReadOnly(false);
				assetId.setValue(assetFindId.getValue());
				assetId.setReadOnly(true);
				group.setValue(element[0]);
				subGroup.setValue(element[1]);
				ledgerName.setValue(element[2]);
				assetName.setValue(element[3]);

			//	BigDecimal i = new BigDecimal(element[4]+"");
				
				//procCost.setValue(new BigDecimal(element[4]+""));
				
//				if((Double.valueOf("0"+element[4].toString()))>0){
					assetOpenBal.setValue(frmt.format(new BigDecimal(element[4]+"")));
					//System.out.println("AA");
//					assetBalType.setValue(1);
//				}else{
//					assetOpenBal.setValue(Double.valueOf(element[5]+""));
//					assetBalType.setValue(2);
//				}
//
//				if((Double.valueOf("0"+element[6].toString()))>0){
//					depOpenBal.setValue(Double.valueOf(element[6]+""));
//					depBalType.setValue(1);
//				}else{
					//private NumberFormat frmt = new DecimalFormat("#0.00");
					depOpenBal.setValue(frmt.format(new BigDecimal(element[7]+"")));
//					depBalType.setValue(2);
//				}

				identificationMark.setValue(element[8]);
				dept.setValue(element[9]);
				location.setValue(element[10]);
				supplierName.setValue(element[11]);
				supplierAddress.setValue(element[12]);

				dateAcquisition.setValue(new Date(element[13].toString().replace("-", "/").substring(0,10).trim()));
				dateInstall.setValue(new Date(element[14].toString().replace("-", "/").substring(0,10).trim()));
				billDate.setValue(new Date(element[15].toString().replace("-", "/").substring(0,10).trim()));
				billNo.setValue(element[16]);
				challNo.setValue(element[17]);
				procCost.setReadOnly(false);
				procCost.setValue(frmt.format(new BigDecimal(element[18]+"")));
				procCost.setReadOnly(true);
				installCost.setReadOnly(false);
				installCost.setValue(Double.valueOf(element[19]+""));
				installCost.setReadOnly(true);
				otherCost.setReadOnly(false);
				otherCost.setValue(Double.valueOf(element[20]+""));
				otherCost.setReadOnly(false);
				totalCost.setReadOnly(false);
				totalCost.setValue(Double.valueOf(element[21]+""));
				totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
				totalCost.setReadOnly(true);
				lifeYear.setValue(element[22]);
				depSystem.setValue(element[23]);
				depreciation.setValue(element[24]); 
				costCenter.setValue(element[26]);
				annualDepreciation.setReadOnly(false);
				annualDepreciation.setValue(cms.setComma(Double.valueOf(element[25].toString())));
				annualDepreciation.setReadOnly(true);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	
	private boolean isValidDelete()
	{
		try
		{
			Transaction tx = null;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String c = session.createSQLQuery("SELECT COUNT(*) FROM tbAssetOpBalance WHERE AssetId = '"+assetFindId.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator().next().toString();

			if(Integer.valueOf("0"+c)>1)
			{
				this.getParent().showNotification("","Already year close for this ledger. So you can not delete this ledger.",Notification.TYPE_ERROR_MESSAGE);
				return false;
			}
			else
			{
				c = session.createSQLQuery("SELECT COUNT(*) FROM tbFixedAsset WHERE [Type] = 'P' AND AssetId = '"+assetFindId.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator().next().toString();

				if(Integer.valueOf("0"+c)>0)
				{
					this.getParent().showNotification("","There are purchase data for this ledger.So you can not delete this ledger.",Notification.TYPE_ERROR_MESSAGE);
					return false;
				}
				else
				{
					c = session.createSQLQuery("SELECT COUNT(*) FROM tbAssetSales WHERE AssetId = '"+assetFindId.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator().next().toString();
					
					if(Integer.valueOf("0"+c)>0)
					{
						this.getParent().showNotification("","There are sales data for this ledger.So you can not delete this ledger.",Notification.TYPE_ERROR_MESSAGE);
						return false;
					}
					else
					{
						return true;
					}
				}
			}	
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	
	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("820px");
		setHeight("500px");
		
		// lblAssetId
		lblAssetId = new Label();
		lblAssetId.setImmediate(true);
		lblAssetId.setWidth("-1px");
		lblAssetId.setHeight("-1px");
		lblAssetId.setValue("Asset Id :");
		mainLayout.addComponent(lblAssetId, "top:20.0px;left:20.0px;");
		
		// assetId
		assetId  = new TextField();
		assetId.setImmediate(true);
		assetId.setWidth("180px");
		assetId.setHeight("-1px");
		assetId.setSecret(false);
		assetId.setReadOnly(true);
		mainLayout.addComponent(assetId, "top:20.0px;left:160.0px;");
		
		// lblGroup
		lblGroup = new Label();
		lblGroup.setImmediate(true);
		lblGroup.setWidth("-1px");
		lblGroup.setHeight("-1px");
		lblGroup.setValue("Group :");
		mainLayout.addComponent(lblGroup, "top:45.0px;left:20.0px;");
		
		// group
		group = new ComboBox();
		group.setImmediate(true);
		group.setHeight("-1px");
		group.setWidth("210px");
		group.setImmediate(true);
		group.setWidth(lcw);
		mainLayout.addComponent(group, "top:45.0px;left:160.0px;");
		
		// btnGroup
		btnGroup = new NativeButton();
		btnGroup.setCaption("");
		btnGroup.setImmediate(true);
		btnGroup.setWidth("28px");
		btnGroup.setHeight("24px");
		btnGroup.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnGroup,"top:45.0px;left:390.0px;");

		// lblSubGroup
		lblSubGroup = new Label();
		lblSubGroup.setImmediate(true);
		lblSubGroup.setWidth("-1px");
		lblSubGroup.setHeight("-1px");
		lblSubGroup.setValue("Sub Group :");
		mainLayout.addComponent(lblSubGroup, "top:70.0px;left:20.0px;");

		// subGroup
		subGroup = new ComboBox();
		subGroup.setImmediate(true);
		subGroup.setHeight("-1px");
		subGroup.setWidth("210px");
		subGroup.setImmediate(true);
		subGroup.setWidth(lcw);
		mainLayout.addComponent(subGroup, "top:70.0px;left:160.0px;");
		
		// btnSubGroup
		btnSubGroup = new NativeButton();
		btnSubGroup.setCaption("");
		btnSubGroup.setImmediate(true);
		btnSubGroup.setWidth("28px");
		btnSubGroup.setHeight("24px");
		btnSubGroup.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnSubGroup,"top:70.0px;left:390.0px;");
		
		// lblLedgerName
		lblLedgerName = new Label();
		lblLedgerName.setImmediate(true);
		lblLedgerName.setWidth("-1px");
		lblLedgerName.setHeight("-1px");
		lblLedgerName.setValue("Ledger Name :");
		mainLayout.addComponent(lblLedgerName, "top:95.0px;left:20.0px;");

		// ledgerName
		ledgerName  = new TextField();
		ledgerName.setImmediate(true);
		ledgerName.setWidth(lcw);
		ledgerName.setImmediate(true);
		mainLayout.addComponent(ledgerName, "top:95.0px;left:160.0px;");
		
		// lblCostCenter
		lblCostCenter = new Label();
		lblCostCenter.setImmediate(true);
		lblCostCenter.setWidth("-1px");
		lblCostCenter.setHeight("-1px");
		lblCostCenter.setValue("Cost Center :");
		mainLayout.addComponent(lblCostCenter, "top:120.0px;left:20.0px;");

		// costCenter
		costCenter = new ComboBox();
		costCenter.setImmediate(true);
		costCenter.setNullSelectionAllowed(false);
		costCenter.setImmediate(true);
		costCenter.setWidth(lcw);
		mainLayout.addComponent(costCenter, "top:120.0px;left:160.0px;");
		
		// btnCostCenter
		btnCostCenter = new NativeButton();
		btnCostCenter.setCaption("");
		btnCostCenter.setImmediate(true);
		btnCostCenter.setWidth("28px");
		btnCostCenter.setHeight("24px");
		btnCostCenter.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnCostCenter,"top:120.0px;left:390.0px;");
		
		// lblAssetName
		lblAssetName = new Label();
		lblAssetName.setImmediate(true);
		lblAssetName.setWidth("-1px");
		lblAssetName.setHeight("-1px");
		lblAssetName.setValue("Asset Name :");
		mainLayout.addComponent(lblAssetName, "top:145.0px;left:20.0px;");

		// assetName
		assetName  = new TextField();
		assetName.setImmediate(true);
		assetName.setWidth(lcw);
		assetName.setImmediate(true);
		mainLayout.addComponent(assetName, "top:145.0px;left:160.0px;");
		
		// lblAssetOpenBal
		lblAssetOpenBal = new Label();
		lblAssetOpenBal.setImmediate(true);
		lblAssetOpenBal.setWidth("-1px");
		lblAssetOpenBal.setHeight("-1px");
		lblAssetOpenBal.setValue("Asset Opening Balance :");
		mainLayout.addComponent(lblAssetOpenBal, "top:170.0px;left:20.0px;");

		// assetOpenBal
		assetOpenBal = new AmountCommaSeperator();
		assetOpenBal.setImmediate(true);
		assetOpenBal.setTextChangeEventMode(TextChangeEventMode.LAZY);
		assetOpenBal.setTextChangeTimeout(200);
		mainLayout.addComponent(assetOpenBal, "top:170.0px;left:160.0px;");
		
		// lblAssetOpenBal
		lblAssetBalType = new Label();
		lblAssetBalType.setImmediate(true);
		lblAssetBalType.setWidth("-1px");
		lblAssetBalType.setHeight("-1px");
		lblAssetBalType.setValue("Balance Type :");
		mainLayout.addComponent(lblAssetBalType, "top:195.0px;left:20.0px;");
		
		// assetBalType
		assetBalType  = new NativeSelect();
		assetBalType.setNullSelectionAllowed(false);
		assetBalType.addItem(1);
		assetBalType.setItemCaption(1, "Debit");
		assetBalType.addItem(2);
		assetBalType.setItemCaption(2, "Credit");
		assetBalType.setValue(1);
		assetBalType.setReadOnly(true);
		mainLayout.addComponent(assetBalType, "top:195.0px;left:160.0px;");
		
		// lblDepOpenBal
		lblDepOpenBal = new Label();
		lblDepOpenBal.setImmediate(true);
		lblDepOpenBal.setWidth("-1px");
		lblDepOpenBal.setHeight("-1px");
		lblDepOpenBal.setValue("Dep. Opening Balance :");
		mainLayout.addComponent(lblDepOpenBal, "top:220.0px;left:20.0px;");

		// depOpenBal
		depOpenBal = new AmountCommaSeperator();
		depOpenBal.setImmediate(true);
		mainLayout.addComponent(depOpenBal, "top:220.0px;left:160.0px;");
		
		// lblDepBalType
		lblDepBalType = new Label();
		lblDepBalType.setImmediate(true);
		lblDepBalType.setWidth("-1px");
		lblDepBalType.setHeight("-1px");
		lblDepBalType.setValue("Balance Type :");
		mainLayout.addComponent(lblDepBalType, "top:245.0px;left:20.0px;");
		
		// depBalType
		depBalType  = new NativeSelect();
		depBalType.setNullSelectionAllowed(false);
		depBalType.addItem(1);
		depBalType.setItemCaption(1, "Debit");
		depBalType.addItem(2);
		depBalType.setItemCaption(2, "Credit");
		depBalType.setValue(2);
		depBalType.setReadOnly(true);
		mainLayout.addComponent(depBalType, "top:245.0px;left:160.0px;");
		
		// lblIdentificationMark
		lblIdentificationMark = new Label();
		lblIdentificationMark.setImmediate(true);
		lblIdentificationMark.setWidth("-1px");
		lblIdentificationMark.setHeight("-1px");
		lblIdentificationMark.setValue("Identification Mark :");
		mainLayout.addComponent(lblIdentificationMark, "top:270.0px;left:20.0px;");

		// identificationMark
		identificationMark  = new TextField();
		identificationMark.setImmediate(true);
		identificationMark.setWidth(lcw);
		identificationMark.setImmediate(true);
		mainLayout.addComponent(identificationMark, "top:270.0px;left:160.0px;");
		
		// lblDept
		lblDept = new Label();
		lblDept.setImmediate(true);
		lblDept.setWidth("-1px");
		lblDept.setHeight("-1px");
		lblDept.setValue("Department :");
		mainLayout.addComponent(lblDept, "top:295.0px;left:20.0px;");

		// dept
		dept = new TextField();
		dept.setImmediate(true);
		dept.setWidth(lcw);
		dept.setImmediate(true);
		mainLayout.addComponent(dept, "top:295.0px;left:160.0px;");
		
		// lblLocation
		lblLocation = new Label();
		lblLocation.setImmediate(true);
		lblLocation.setWidth("-1px");
		lblLocation.setHeight("-1px");
		lblLocation.setValue("Location :");
		mainLayout.addComponent(lblLocation, "top:320.0px;left:20.0px;");

		// location
		location = new TextField();
		location.setImmediate(true);
		location.setWidth(lcw);
		location.setImmediate(true);
		mainLayout.addComponent(location, "top:320.0px;left:160.0px;");
		
		// lblSupplierName
		lblSupplierName = new Label();
		lblSupplierName.setImmediate(true);
		lblSupplierName.setWidth("-1px");
		lblSupplierName.setHeight("-1px");
		lblSupplierName.setValue("Supplier Name :");
		mainLayout.addComponent(lblSupplierName, "top:345.0px;left:20.0px;");

		// supplierName
		supplierName = new TextField();
		supplierName.setImmediate(true);
		supplierName.setWidth(lcw);
		supplierName.setImmediate(true);
		mainLayout.addComponent(supplierName, "top:345.0px;left:160.0px;");
		
		// lblSupplierAddress
		lblSupplierAddress = new Label();
		lblSupplierAddress.setImmediate(true);
		lblSupplierAddress.setWidth("-1px");
		lblSupplierAddress.setHeight("-1px");
		lblSupplierAddress.setValue("Supplier Address :");
		mainLayout.addComponent(lblSupplierAddress, "top:20.0px;left:430.0px;");

		// supplierAddress
		supplierAddress = new TextField();
		supplierAddress.setImmediate(true);
		supplierAddress.setRows(2);
		supplierAddress.setWidth(lcw);
		supplierAddress.setImmediate(true);
		mainLayout.addComponent(supplierAddress, "top:20.0px;left:555.0px;");
		
		// lblDateAcquisition
		lblDateAcquisition = new Label();
		lblDateAcquisition.setImmediate(true);
		lblDateAcquisition.setWidth("-1px");
		lblDateAcquisition.setHeight("-1px");
		lblDateAcquisition.setValue("Date of Acquisition :");
		mainLayout.addComponent(lblDateAcquisition, "top:82.0px;left:430.0px;");
		
		// dateAcquisition
		dateAcquisition = new DateField();
		dateAcquisition.setValue(new java.util.Date());
		dateAcquisition.setResolution(PopupDateField.RESOLUTION_DAY);
		dateAcquisition.setDateFormat("dd-MM-yyyy");
		dateAcquisition.setImmediate(true);
		mainLayout.addComponent(dateAcquisition, "top:82.0px;left:555.0px;");
		
		// lblDateInstall
		lblDateInstall = new Label();
		lblDateInstall.setImmediate(true);
		lblDateInstall.setWidth("-1px");
		lblDateInstall.setHeight("-1px");
		lblDateInstall.setValue("Date of Installation :");
		mainLayout.addComponent(lblDateInstall, "top:107.0px;left:430.0px;");
		
		// dateInstall
		dateInstall = new DateField();
		dateInstall.setValue(new java.util.Date());
		dateInstall.setResolution(PopupDateField.RESOLUTION_DAY);
		dateInstall.setDateFormat("dd-MM-yyyy");
		dateInstall.setImmediate(true);
		mainLayout.addComponent(dateInstall, "top:107.0px;left:555.0px;");
		
		// lblBillDate
		lblBillDate = new Label();
		lblBillDate.setImmediate(true);
		lblBillDate.setWidth("-1px");
		lblBillDate.setHeight("-1px");
		lblBillDate.setValue("Bill Date :");
		mainLayout.addComponent(lblBillDate, "top:132.0px;left:430.0px;");
		
		// billDate
		billDate = new DateField();
		billDate.setValue(new java.util.Date());
		billDate.setResolution(PopupDateField.RESOLUTION_DAY);
		billDate.setDateFormat("dd-MM-yyyy");
		billDate.setImmediate(true);
		mainLayout.addComponent(billDate, "top:132.0px;left:555.0px;");
		
		// lblBillNo
		lblBillNo = new Label();
		lblBillNo.setImmediate(true);
		lblBillNo.setWidth("-1px");
		lblBillNo.setHeight("-1px");
		lblBillNo.setValue("Bill No/L/C No :");
		mainLayout.addComponent(lblBillNo, "top:157.0px;left:430.0px;");

		// billNo
		billNo = new TextField();
		billNo.setImmediate(true);
		billNo.setWidth(rcw);
		billNo.setImmediate(true);
		mainLayout.addComponent(billNo, "top:157.0px;left:555.0px;");
		
		// lblChallNo
		lblChallNo = new Label();
		lblChallNo.setImmediate(true);
		lblChallNo.setWidth("-1px");
		lblChallNo.setHeight("-1px");
		lblChallNo.setValue("Del. Chall No :");
		mainLayout.addComponent(lblChallNo, "top:182.0px;left:430.0px;");

		// challNo
		challNo = new TextField();
		challNo.setImmediate(true);
		challNo.setWidth(rcw);
		challNo.setImmediate(true);
		mainLayout.addComponent(challNo, "top:182.0px;left:555.0px;");
		
		// lblProcCost
		lblProcCost = new Label();
		lblProcCost.setImmediate(true);
		lblProcCost.setWidth("-1px");
		lblProcCost.setHeight("-1px");
		lblProcCost.setValue("Procurement Cost :");
		mainLayout.addComponent(lblProcCost, "top:207.0px;left:430.0px;");

		// procCost
		procCost  = new AmountCommaSeperator();
		procCost.setImmediate(true);
		procCost.setTextChangeEventMode(TextChangeEventMode.LAZY);
		procCost.setTextChangeTimeout(200);
		mainLayout.addComponent(procCost, "top:207.0px;left:555.0px;");
		
		// lblInstallCost
		lblInstallCost = new Label();
		lblInstallCost.setImmediate(true);
		lblInstallCost.setWidth("-1px");
		lblInstallCost.setHeight("-1px");
		lblInstallCost.setValue("Installation Cost :");
		mainLayout.addComponent(lblInstallCost, "top:232.0px;left:430.0px;");

		// installCost
		installCost  = new AmountCommaSeperator();
		installCost.setImmediate(true);
		installCost.setTextChangeEventMode(TextChangeEventMode.LAZY);
		installCost.setTextChangeTimeout(200);
		mainLayout.addComponent(installCost, "top:232.0px;left:555.0px;");
		
		// lblOtherCost
		lblOtherCost = new Label();
		lblOtherCost.setImmediate(true);
		lblOtherCost.setWidth("-1px");
		lblOtherCost.setHeight("-1px");
		lblOtherCost.setValue("Other Cost :");
		mainLayout.addComponent(lblOtherCost, "top:257.0px;left:430.0px;");

		// otherCost
		otherCost  = new AmountCommaSeperator();
		otherCost.setImmediate(true);
		otherCost.setTextChangeEventMode(TextChangeEventMode.LAZY);
		otherCost.setTextChangeTimeout(200);
		mainLayout.addComponent(otherCost, "top:257.0px;left:555.0px;");
		
		// lblTotalCost
		lblTotalCost = new Label();
		lblTotalCost.setImmediate(true);
		lblTotalCost.setWidth("-1px");
		lblTotalCost.setHeight("-1px");
		lblTotalCost.setValue("Total Cost :");
		mainLayout.addComponent(lblTotalCost, "top:282.0px;left:430.0px;");

		// totalCost
		totalCost = new TextField();
		totalCost.setStyleName("fright");
		totalCost.setReadOnly(true);
		mainLayout.addComponent(totalCost, "top:282.0px;left:555.0px;");
		
		// lblLifeYear
		lblLifeYear = new Label();
		lblLifeYear.setImmediate(true);
		lblLifeYear.setWidth("-1px");
		lblLifeYear.setHeight("-1px");
		lblLifeYear.setValue("Life(years) :");
		mainLayout.addComponent(lblLifeYear, "top:307.0px;left:430.0px;");

		// lifeYear
		lifeYear = new AmountField();
		lifeYear.setImmediate(true);
		lifeYear.setTextChangeEventMode(TextChangeEventMode.LAZY);
		lifeYear.setTextChangeTimeout(200);
		mainLayout.addComponent(lifeYear, "top:307.0px;left:555.0px;");
		
		// lblDepSystem
		lblDepSystem = new Label();
		lblDepSystem.setImmediate(true);
		lblDepSystem.setWidth("-1px");
		lblDepSystem.setHeight("-1px");
		lblDepSystem.setValue("Depreciation System :");
		mainLayout.addComponent(lblDepSystem, "top:332.0px;left:430.0px;");
		
		// depSystem
		depSystem = new NativeSelect();
		depSystem.setWidth(rcw);
		depSystem.addItem("");
		depSystem.addItem("Straight Line");
		depSystem.addItem("Diminishing");
		depSystem.addItem("Annuity");
		depSystem.addItem("Sinking Fund");
		depSystem.addItem("Re-Valuation");
		depSystem.addItem("Sum of Year Digit");
		depSystem.addItem("Replacement Cost");
		depSystem.addItem("Insurance");
		depSystem.setNullSelectionAllowed(false);
		depSystem.setValue("");
		mainLayout.addComponent(depSystem, "top:332.0px;left:555.0px;");
		
		// lblDepreciation
		lblDepreciation = new Label();
		lblDepreciation.setImmediate(true);
		lblDepreciation.setWidth("-1px");
		lblDepreciation.setHeight("-1px");
		lblDepreciation.setValue("Depreciation :");
		mainLayout.addComponent(lblDepreciation, "top:357.0px;left:430.0px;");

		// depreciation
		depreciation = new TextField();
		depreciation.setStyleName("fright");
		depreciation.setImmediate(true);
		depreciation.setTextChangeEventMode(TextChangeEventMode.LAZY);
		depreciation.setTextChangeTimeout(200);
		mainLayout.addComponent(depreciation, "top:357.0px;left:555.0px;");
		
		// lblAnnualDepreciation
		lblAnnualDepreciation = new Label();
		lblAnnualDepreciation.setImmediate(true);
		lblAnnualDepreciation.setWidth("-1px");
		lblAnnualDepreciation.setHeight("-1px");
		lblAnnualDepreciation.setValue("Annual Depreciation :");
		mainLayout.addComponent(lblAnnualDepreciation, "top:382.0px;left:430.0px;");

		// annualDepreciation
		annualDepreciation = new TextField();
		annualDepreciation.setStyleName("fright");
		annualDepreciation.setReadOnly(true);
		mainLayout.addComponent(annualDepreciation, "top:382.0px;left:555.0px;");
		
		//common button
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout, "top:415.0px;left:80.0px;");
		
		return mainLayout;
	}

/*	
	private void newBtnAction()
	{
		isUpdate = false;
		btnIni(false);
		txtEnable(true);
		txtClear();
	}

*/
	}
