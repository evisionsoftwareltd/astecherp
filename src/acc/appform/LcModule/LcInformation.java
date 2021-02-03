package acc.appform.LcModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class LcInformation  extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private ComboBox cmbPrimaryGroup;
	private ComboBox cmbMainGroup;
	private ComboBox cmbSubGroup;

	private static final List<String> incotermType = Arrays.asList(new String[] {"FOB", "CFR"});
	private static final List<String>type  = Arrays.asList(new String[] {"UPAS", "AT SIGHT","OBU","DAF"});
	private OptionGroup ogNatureofLc;

	private ComboBox cmbLcOpeningBank;
	private ComboBox cmbLcOpeningBranch;
	private TextField txtVessaleName;
	private AmountCommaSeperator txtMarginAmount;
	private TextField txtDischargePort;
	private ComboBox cmbArrivalPort;
	private PopupDateField dArrivalDate;
	private TextField txtBbReffrenceNo;
	private PopupDateField dBbReffrenceDate;

	private TextField txtTransactionNo;
	private TextField txtLcNo;
	private PopupDateField dLcOpeningDate;

	private TextField txtOrigin;
	private PopupDateField dShipmentDate;
	private ComboBox cmbModeOfShipment;
	private static final List<String> shipmentType = Arrays.asList(new String[] {"By Air", "By Sea","By Road"});

	private PopupDateField dExpiryDate;
	private ComboBox cmbIncotermType;

	private TextField txtSupplier;
	private TextField txtSupplierAddress;
	private AmountCommaSeperator txtLcValueUsd;
	private AmountCommaSeperator txtLcValueBdt;
	private AmountCommaSeperator txtMarginPer;
	private AmountCommaSeperator txtExchangeRate;
	private AmountCommaSeperator txtFreeightcost;

	private TextField txtProformaNo;
	private PopupDateField dProformaDate;

	private TextField txtMarineCoveredNo;
	private PopupDateField dMarineCoveredDate;

	private TextField txtBenificiaryName;
	private TextField txtBenificiaryBranch;

	private TextField txtInsCompanyName;

	private TextField txtCnfAgentName;
	private PopupDateField dClearingDate;

	private AmountCommaSeperator txtTotalPremium;
	private AmountCommaSeperator txtNetPremium;
	private AmountCommaSeperator txtInsuranceRefund;

	private TextField txtAmmendmentNo;
	private PopupDateField dAmmendmentDate;
	private TextField txtAmmendmentReason;
	private NativeSelect cmbIsActive;

	private TextField txtLcNoForFind = new TextField();

	private Table table = new Table();
	private ArrayList<ComboBox> tbCmbItemName = new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbTxtUnit = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> tbTxtQuantity = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> tbTxtRate = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> tbTxtRateBdt = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> tbTxtAmount = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbTxtAmountBDT = new ArrayList<TextRead>();
	private ArrayList<TextField> tbTxtHsCode = new ArrayList<TextField>();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private Boolean isUpdate = false,isFind=false;

	private ArrayList<Component> allComp = new ArrayList<Component>();

	private DecimalFormat dfRate = new DecimalFormat("#0.00");
	private CommaSeparator cm = new CommaSeparator();

	private CommonButton cButton = new CommonButton( "New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");
	private String Notify="";
	private String ledgerID="";
	private static final String[] cities = new String[] { "Open", "Closed" };
	
	private OptionGroup oplctype= new OptionGroup();
	private String [] lctype={"RM","Machinery"};
	private Label lbllctype=new Label();

	public LcInformation(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("L/C INFORMATION :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		tableinitialise();

		txtInit(true);
		btnIni(true);

		addPrimaryGroupData();

		setEventAction();
		focusEnter();
		
		authenticationCheck();
		cButton.btnNew.focus();
		bankIni();
		branchIni();
	}
	private void bankIni()
	{
		cmbLcOpeningBank.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> bh = session.createSQLQuery("Select id,bankName from tbBankName order by bankName").list();
			for(Iterator<?> iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbLcOpeningBank.addItem(element[0].toString());
				cmbLcOpeningBank.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void branchIni()
	{
		cmbLcOpeningBranch.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> bh = session.createSQLQuery("Select id,branchName from tbBankBranch order by branchName").list();
			for(Iterator<?> iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbLcOpeningBranch.addItem(element[0].toString());
				cmbLcOpeningBranch.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			cButton.btnSave.setVisible(false);
		}
		if(!sessionBean.isUpdateable())
		{
			cButton.btnEdit.setVisible(false);
		}
		if(!sessionBean.isDeleteable())
		{
			cButton.btnDelete.setVisible(false);
		}
	}
	private boolean isClosed(){
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> bh = session.createSQLQuery("select * from tbLcOpeningInfo where vLedgerID='"+ledgerID+"' " +
					"and vLcNo='"+txtLcNo.getValue()+"' and isActive!=0").list();
			if(bh.iterator().hasNext()){
				return true;
			}
			
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return false;
	}
	private void setEventAction()
	{
		txtLcValueUsd.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtLcValueUsd.getValue().toString().trim().isEmpty() && 
						!txtExchangeRate.getValue().toString().trim().isEmpty())
				{
					lcValueBDTCalculation();
					if(!txtLcValueBdt.getValue().toString().trim().isEmpty() && 
							!txtMarginPer.getValue().toString().trim().isEmpty())
						marginCalculation();
				}
			}
		});
		
		
		txtFreeightcost.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtFreeightcost.getValue().toString().trim().isEmpty()  )
				{
					
					double freeightcost=Double.parseDouble(txtFreeightcost.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtFreeightcost.getValue().toString().replaceAll(",", ""));
					
			
					double LCValueUSD=0.0;
					for(int a=0;a<tbTxtAmount.size();a++){
						if(!tbTxtAmount.get(a).getValue().toString().isEmpty()){
							double amt = Double.parseDouble("0"+tbTxtAmount.get(a).getValue().toString().replace(",", ""));
							LCValueUSD=LCValueUSD+amt;
						}
					}
					txtLcValueUsd.setValue(cm.setComma(LCValueUSD+freeightcost));
					if(!txtMarginPer.getValue().toString().isEmpty()){
						marginCalculation();
					}
					
					
				}
			}
		});

		txtExchangeRate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtLcValueUsd.getValue().toString().trim().isEmpty() && 
						!txtExchangeRate.getValue().toString().trim().isEmpty())
				{
					lcValueBDTCalculation();
					if(!txtLcValueBdt.getValue().toString().trim().isEmpty() && 
							!txtMarginPer.getValue().toString().trim().isEmpty())
						marginCalculation();
				}
			}
		});

		txtMarginPer.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				txtMarginAmount.setValue("");
				if(!txtLcValueUsd.getValue().toString().trim().isEmpty() && 
						!txtMarginPer.getValue().toString().trim().isEmpty())
				{
					marginCalculation();
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				newButtonEvent();

			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(isClosed()){
					isUpdate = true;
					isFind=false;
					updateButtonAction();
				}
				else{
					showNotification("Sorry!!","LC No: "+txtLcNo.getValue()+" is already Closed",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				refreshButtonEvent();
			}
		});

		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				findButtonEvent();
				isFind=true;
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		cmbPrimaryGroup.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addMainGroupData((cmbPrimaryGroup.getValue()!=null?cmbPrimaryGroup.getValue().toString():""));
			}
		});

		cmbMainGroup.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addSubGroupData((cmbMainGroup.getValue()!=null?cmbMainGroup.getValue().toString():""));
			}
		});
		
		cmbIncotermType.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbIncotermType.getValue()!=null)
				{
					if(cmbIncotermType.getValue().toString().equalsIgnoreCase("FOB"))
					{
						txtFreeightcost.setEnabled(false);		
					}
					else
					{
						txtFreeightcost.setEnabled(true);		
					}
				   
				}
				
			}
		});
		
		oplctype.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				if(oplctype.getValue()!=null)
				{
					tableClear();
				    if(oplctype.getValue().toString().equalsIgnoreCase("RM"))
				    {
				       for(int i=0;i<tbCmbItemName.size();i++)
				       {
				    	  productAddRM(i);   
				       }
				    }
				    else
				    {
				    	for(int i=0;i<tbCmbItemName.size();i++)
					       {
					    	  productAddMachinery(i);   
					       }	
				    }
				}
				
			}
		});
		
	}

	private void lcValueBDTCalculation()
	{
		double lcValueUSD = Double.parseDouble(txtLcValueUsd.getValue().toString().trim());
		double lcExchangeRate = Double.parseDouble(txtExchangeRate.getValue().toString().trim());
		double lcValueBDT = lcValueUSD * lcExchangeRate;
		txtLcValueBdt.setValue(cm.setComma(lcValueBDT));
	}

	private void marginCalculation()
	{
		double marginPercentage = Double.parseDouble(txtMarginPer.getValue().toString().trim().replaceAll(",", ""));
		double marginAmount = (Double.parseDouble(txtLcValueUsd.getValue().toString().replaceAll(",", ""))*marginPercentage)/100;
		txtMarginAmount.setValue(cm.setComma(marginAmount));
	}

	private void addPrimaryGroupData()
	{
		cmbPrimaryGroup.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String query = "select Head_Id,Head_Name from tbPrimary_Group where Head_Id like 'A4' order by Head_Id,SlNO";
			List<?> lst=session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{
				for(Iterator<?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbPrimaryGroup.addItem(element[0]);
					cmbPrimaryGroup.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbPrimaryDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addMainGroupData(String primaryId)
	{
		cmbMainGroup.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String query = "select Group_Id,Group_Name from tbMain_Group where" +
					" Head_Id = '"+primaryId+"' order by Group_Name";
			List<?> lst=session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{
				for(Iterator<?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbMainGroup.addItem(element[0]);
					cmbMainGroup.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbMainGroupDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addSubGroupData(String groupId)
	{
		cmbSubGroup.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String query = "select Sub_Group_Id,Sub_Group_Name from tbSub_Group" +
					" where Group_Id = '"+groupId+"' order by Sub_Group_Name";
			List<?> lst=session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{
				for(Iterator<?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbSubGroup.addItem(element[0]);
					cmbSubGroup.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbSubGroupDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean tableCheck(){
		
		for(int a=0;a<tbCmbItemName.size();a++){
			if(tbCmbItemName.get(a).getValue()!=null&&(tbTxtRate.get(a).getValue().toString().isEmpty()||
													  // tbTxtRateBdt.get(a).getValue().toString().isEmpty()||
													   tbTxtHsCode.get(a).getValue().toString().isEmpty())){
				return false;
			}
		}
		return true;
	}
	private boolean checkForm()
	{
		if(cmbPrimaryGroup.getValue()!=null)
		{
			if(!txtLcNo.getValue().toString().trim().isEmpty())
			{
				if(dLcOpeningDate.getValue()!=null)
				{
					if(cmbLcOpeningBank.getValue()!=null)
					{
						if(cmbLcOpeningBranch.getValue()!=null)
						{
							if(!txtOrigin.getValue().toString().trim().isEmpty())
							{
								if(!txtDischargePort.getValue().toString().trim().isEmpty())
								{
									if(!txtVessaleName.getValue().toString().trim().isEmpty())
									{
										if(dShipmentDate.getValue()!=null)
										{
											if(cmbModeOfShipment.getValue()!=null)
											{
												if(cmbArrivalPort.getValue()!=null)
												{
													if(dArrivalDate.getValue()!=null)
													{
														if(dExpiryDate.getValue()!=null)
														{
															if(cmbIncotermType.getValue()!=null)
															{
																if(!txtSupplier.getValue().toString().trim().isEmpty())
																{
																	if(!txtLcValueUsd.getValue().toString().trim().isEmpty())
																	{
																		//if(!txtExchangeRate.getValue().toString().trim().isEmpty())
																		//{
																			//if(!txtLcValueBdt.getValue().toString().trim().isEmpty())
																			//{
																				if(!txtMarginPer.getValue().toString().trim().isEmpty())
																				{
																					if(!txtMarginAmount.getValue().toString().trim().isEmpty())
																					{
																						if(!txtBbReffrenceNo.getValue().toString().trim().isEmpty())
																						{
																							if(dBbReffrenceDate.getValue()!=null)
																							{
																								if(txtProformaNo.getValue()!=null)
																								{
																									if(dProformaDate.getValue()!=null)
																									{
																										if(txtMarineCoveredNo.getValue()!=null)
																										{
																											if(dMarineCoveredDate.getValue()!=null)
																											{
																												if(txtInsCompanyName.getValue()!=null)
																												{
																													if(txtTotalPremium.getValue()!=null)
																													{
																														if(txtNetPremium.getValue()!=null)
																														{
																															if(txtBenificiaryName.getValue()!=null)
																															{
																																if(txtInsuranceRefund.getValue()!=null)
																																{
																																	if(txtBenificiaryBranch.getValue()!=null)
																																	{
																																		if(txtAmmendmentNo.getValue()!=null)
																																		{
																																			if(dAmmendmentDate.getValue()!=null)
																																			{
																																				if(txtAmmendmentReason.getValue()!=null)
																																				{
																																					if(txtCnfAgentName.getValue()!=null)
																																					{
																																						if(dClearingDate.getValue()!=null)
																																						{
																																							if(!tbCmbItemName.get(0).getValue().toString().isEmpty())
																																							{
																																								if(!tbTxtQuantity.get(0).getValue().toString().isEmpty())
																																								{
																																									if(!tbTxtRate.get(0).getValue().toString().isEmpty())
																																									{
																																										//if(!tbTxtRateBdt.get(0).getValue().toString().isEmpty())
																																										//{
																																											if(!tbTxtHsCode.get(0).getValue().toString().isEmpty())
																																											{
																																												if(tableCheck()){
																																													return true;
																																												}
																																												else{
																																													Notify="Please Provide All Table Data";
																																												}
																																												
																																											}
																																											else
																																											{
																																												Notify="Please Provide HS Code!!";
																																											}
																																										/*}
																																										else
																																										{
																																											Notify="Please Provide BD Rate!!";
																																										}*/
																																									}
																																									else
																																									{
																																										Notify="Please Provide Rate!!";
																																									}
																																								}
																																								else
																																								{
																																									Notify="Please Provide Quantity!!";
																																								}
																																							}
																																							else
																																							{
																																								Notify="Please Provide Item Name!!";
																																							}
																																						}
																																						else
																																						{
																																							Notify="Please Provide Clearinf Date!!";
																																						}
																																					}
																																					else
																																					{
																																						Notify="Please Provide CNF Agent Name!!";
																																					}
																																				}
																																				else
																																				{
																																					Notify="Please Provide Ammendment Reason!!";
																																				}
																																			}
																																			else
																																			{
																																				Notify="Please Provide Ammendment Date!!";
																																			}
																																		}
																																		else
																																		{
																																			Notify="Please Provide Ammendment No!!";
																																		}
																																	}
																																	else
																																	{
																																		Notify="Please Provide Benificiary Branch!!";
																																	}
																																}
																																else
																																{
																																	Notify="Please Provide Benificiary Bank!!";
																																}
																															}
																															else
																															{
																																Notify="Please Provide Insurance Refund!!";
																															}
																														}
																														else
																														{
																															Notify="Please Provide Net premium!!";
																														}
																													}
																													else
																													{
																														Notify="Please Provide Total premium!!";
																													}
																												}

																												else
																												{
																													Notify="Please Provide Ins. Company Name!!";
																												}
																											}
																											else
																											{
																												Notify="Please Provide Marine Covered Date!!";
																											}
																										}
																										else
																										{
																											Notify="Please Provide Marine Covered No.!!";
																										}
																									}
																									else
																									{
																										Notify="Please Provide Proforma Invoice Date!!";
																									}
																								}
																								else
																								{
																									Notify="Please Provide Proforma Invoice No.!!";
																								}
																							}
																							else
																							{
																								Notify="Please Provide Refrence Date!!";
																							}
																						}
																						else
																						{
																							Notify="Please Provide Refrence No.!!!";
																						}
																					}
																					else
																					{
																						Notify="Please Provide Margin Amount!!!";
																					}
																				}
																				else
																				{
																					Notify="Please Provide Margin Percent!!!";
																				}
																			/*}
																			else
																			{
																				Notify="Please Provide LC Value BDT!!!";
																			}*/
																		/*}
																		else
																		{
																			Notify="Please Provide Exchange Rate!!!";
																		}*/
																	}
																	else
																	{
																		Notify="Please Provide LC Value USD!!!";
																	}
																}
																else
																{
																	Notify="Please Provide Supplier Name!!!";
																}
															}
															else
															{
																Notify="Please Select Incoterm Type!!!";
															}
														}
														else
														{
															Notify="Please Provide Expiry Date!!!";
														}
													}
													else
													{
														Notify="Please Provide Arrival Date!!!";
													}
												}
												else
												{
													Notify="Please Provide Arrival Port!!!";
												}
											}
											else
											{
												Notify="Please Provide Shipment Mode!!!";
											}
										}
										else
										{
											Notify="Please Provide Shipment Date!!!";
										}

									}

									else
									{
										Notify="Please Provide Vessel Name!!!";
									}
								}
								else
								{
									Notify="Please Provide Discharge Port!!!";
								}
							}
							else
							{
								Notify="Please Provide Origin!!!";
							}
						}
						else
						{
							Notify="Please Provide Branch!!!";
						}
					}
					else
					{
						Notify="Please Provide Bank!!!";
					}
				}
				else
				{
					Notify="Please Provide Opening Date!!!";
				}
			}
			else
			{
				Notify="Please Provide L/C No.!!!";
			}
		}
		else
		{
			Notify="Please Select Primary Group Name!!!";
		}
		return false;
	}

	private void formValidation()
	{
		if(checkForm())
		{
			saveButtonEvent();
		}
		else
		{
			showNotification("Warning!",Notify,Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void updateButtonAction() 
	{
		btnIni(false);
		txtInit(false);
		if(cmbIncotermType.getValue()!=null)
		{
		  if(cmbIncotermType.getValue().toString().equalsIgnoreCase("FOB"))
		  {
			txtFreeightcost.setEnabled(false);  
		  }
		  else
		  {
			  txtFreeightcost.setEnabled(true);   
		  }
		}

	}

	private void newButtonEvent() 
	{
		System.out.println(isFind);	
		if(isFind){
			txtLcNo.setValue("L/C#");
			//tableClear();
			txtInit(false);
			btnIni(false);
		}
		else{
			isUpdate = false;
			txtClear();
			txtInit(false);
			btnIni(false);
			cmbPrimaryGroup.focus();
		}
		isFind=false;
	}

	private void refreshButtonEvent() 
	{
		isUpdate = false;
		isFind=false;
		ogNatureofLc.setValue("UPAS");
		txtInit(true);
		btnIni(true);
		txtClear();
		tableClear();
		txtFreeightcost.setEnabled(false);
	}

	private void findButtonEvent()
	{
		Window win = new LcFindWindowNew(sessionBean,txtLcNoForFind,"LcNo");
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(txtLcNoForFind.getValue().toString().length()>0)
				{
					findInitialise(txtLcNoForFind.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String strLcNo) 
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String sql = " select loi.vPrimaryGroup,loi.vMainGroup,loi.vSubGroup,loi.vLcNo,loi.vLedgerID,loi.dLcOpeningDate," +
					"loi.vLcOpeningBank,loi.vLcOpeningBranch,loi.vOrigin,loi.vDischargePort,loi.vVessaileName,loi.dShipmentDate,loi.vModeOfShipment," +
					"loi.vArrivalPort,loi.dArrivalDate,loi.dExpiryDate,loi.vIncoterm,loi.vSupplierName,loi.vSupplierAddress," +
					"loi.mLCAmountBDT,loi.mExchangeRate,loi.mLCValueUSD,loi.mMarginPercentage,loi.mMarginAmount,loi.vBbRefferenceNo," +
					"loi.dRefferenceDate,loi.vProformaInvNo,loi.dProformaInvDate,loi.vMarineCoveredNo,loi.dMarineCoveredDate," +
					"loi.vInsCompanyName,loi.mTotalPremium,loi.mNetPremium,loi.mInsuranceRefund,loi.vAmmendmentNo,loi.dAmmentmentDate,loi.vAmmendmentReason,loi.dClearingDate," +
					"loi.vBenificiaryBank,loi.vBenificiaryBranch,loi.vCnfAgentNAme,loi.vNameOfLC," +
					"lod.vProductId,lod.mQuantity,lod.mRate,lod.mRateBdt,lod.mAmount,lod.mAmountBdt,lod.vHsCode,loi.vTransactionID,loi.isActive,loi.mfreeightcost,loi.vlcType " +
					"from tbLcOpeningInfo loi inner join tbLcOpeningDetails lod on loi.vTransactionID = lod.vTransactionID where loi.vTransactionID = '"+strLcNo+"' ";
			List<?> led = session.createSQLQuery(sql).list();
			int i = 0;
			for (Iterator<?> iter = led.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				if(i==0)
				{
					cmbPrimaryGroup.setValue(element[0]);
					cmbMainGroup.setValue(element[1]);
					cmbSubGroup.setValue(element[2]);
					txtTransactionNo.setValue(element[49].toString());
					txtLcNo.setValue(element[3].toString());
					ledgerID=element[4].toString();
					dLcOpeningDate.setValue(element[5]);
					cmbLcOpeningBank.setValue(element[6]);
					cmbLcOpeningBranch.setValue(element[7]);
					txtOrigin.setValue(element[8]);

					txtDischargePort.setValue(element[9]);
					txtVessaleName.setValue(element[10]);
					dShipmentDate.setValue(element[11]);
					cmbModeOfShipment.setValue(element[12]);
					cmbArrivalPort.setValue(element[13]);
					dArrivalDate.setValue(element[14]);
					dExpiryDate.setValue(element[15]);
					cmbIncotermType.setValue(element[16]);
					txtFreeightcost.setEnabled(false);
					
					txtSupplier.setValue(element[17]);

					txtSupplierAddress.setValue(element[18]);
					txtLcValueBdt.setValue(cm.setComma(Double.parseDouble(element[19].toString())));
					txtExchangeRate.setValue(cm.setComma(Double.parseDouble(element[20].toString())));
					txtLcValueUsd.setValue(cm.setComma(Double.parseDouble(element[21].toString())));
					txtMarginPer.setValue(cm.setComma(Double.parseDouble(element[22].toString())));
					txtMarginAmount.setValue(cm.setComma(Double.parseDouble(element[23].toString())));
					txtBbReffrenceNo.setValue(element[24]);
					dBbReffrenceDate.setValue(element[25]);

					txtProformaNo.setValue(element[26]);
					dProformaDate.setValue(element[27]);
					txtMarineCoveredNo.setValue(element[28]);
					dMarineCoveredDate.setValue(element[29]);
					txtInsCompanyName.setValue(element[30]);
					txtTotalPremium.setValue(cm.setComma(Double.parseDouble(element[31].toString())));
					txtNetPremium.setValue(cm.setComma(Double.parseDouble(element[32].toString())));
					txtInsuranceRefund.setValue(cm.setComma(Double.parseDouble(element[33].toString())));
					txtAmmendmentNo.setValue(element[34]);
					dAmmendmentDate.setValue(element[35]);
					txtAmmendmentReason.setValue(element[36]);
					dClearingDate.setValue(element[37]);
					txtBenificiaryName.setValue(element[38]);
					txtBenificiaryBranch.setValue(element[39]);
					txtCnfAgentName.setValue(element[40]);
					ogNatureofLc.setValue(element[41]);
					
					if(element[50].toString().equalsIgnoreCase("1"))
					{
						cmbIsActive.setValue("Open");
					}
					else
					{
						cmbIsActive.setValue("Closed");
					}
					
					txtFreeightcost.setValue(cm.setComma(Double.parseDouble(element[51].toString())));
					oplctype.select(element[52].toString());
					

				}
				//Table info
				tbCmbItemName.get(i).setValue(element[42]);
				tbTxtQuantity.get(i).setValue(cm.setComma(Double.parseDouble(element[43].toString())));
				tbTxtRate.get(i).setValue(dfRate.format(Double.parseDouble(element[44].toString())));
				tbTxtRateBdt.get(i).setValue(dfRate.format(Double.parseDouble(element[45].toString())));
				tbTxtAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[46].toString())));
				tbTxtAmountBDT.get(i).setValue(cm.setComma(Double.parseDouble(element[47].toString())));
				tbTxtHsCode.get(i).setValue(element[48].toString());
				if(tbCmbItemName.size()-1==i)
					tableRowAdd(i+1);
				i++;
			}

		}
		catch (Exception exp)
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
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
					{	mb.buttonLayout.getComponent(0).setEnabled(false);
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();
						try
						{
							updateTrack(session);
							deleteData(session);
							insertData(session);
							showNotification("All information updated successfully.");
							btnIni(true);
							cButton.btnNew.focus();
							tx.commit();
							isUpdate=false;
							isFind=false;
						}
						catch(Exception exp)
						{
							showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
							tx.rollback();
						}
						finally{session.close();}
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
						try
						{
							insertData(session);
							showNotification("All information saved successfully.");
							btnIni(true);
							cButton.btnNew.focus();
							tx.commit();
							isUpdate=false;
							isFind=false;
						}
						catch(Exception exp)
						{
							showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
							tx.rollback();
						}
						finally{session.close();}
					}
				}
			});
		}
	}

	private void updateTrack(Session session)
	{
		String udTrackInfo = " INSERT into tbUdLcOpeningInfo (vPrimaryGroup,vMainGroup,vSubGroup,vTransactionID,vLcNo,vLedgerID,dLcOpeningDate,vLcOpeningBank," +
				"vLcOpeningBranch,vOrigin,vDischargePort,vVessaileName,dShipmentDate,vModeOfShipment,vArrivalPort,dArrivalDate,dExpiryDate," +
				"vIncoterm,vSupplierName,vSupplierAddress,mLCAmountBDT,mExchangeRate,mLCValueUSD,mMarginPercentage," +
				"mMarginAmount,vBbRefferenceNo,dRefferenceDate,vProformaInvNo,dProformaInvDate,vMarineCoveredNo," +
				"dMarineCoveredDate,vInsCompanyName,mTotalPremium,mNetPremium,mInsuranceRefund,vBenificiaryBank,vBenificiaryBranch,vAmmendmentNo," +
				"dAmmentmentDate,vAmmendmentReason,vCnfAgentNAme,dClearingDate,vNameOfLC,vUserName," +
				"vUserIp,dEntryTime,vUDFlag) select vPrimaryGroup,vMainGroup,vSubGroup,vTransactionID,vLcNo,vLedgerID,dLcOpeningDate,vLcOpeningBank," +
				"vLcOpeningBranch,vOrigin,vDischargePort,vVessaileName,dShipmentDate,vModeOfShipment,vArrivalPort,dArrivalDate,dExpiryDate," +
				"vIncoterm,vSupplierName,vSupplierAddress,mLCAmountBDT,mExchangeRate,mLCValueUSD,mMarginPercentage," +
				"mMarginAmount,vBbRefferenceNo,dRefferenceDate,vProformaInvNo,dProformaInvDate,vMarineCoveredNo," +
				"dMarineCoveredDate,vInsCompanyName,mTotalPremium,mNetPremium,mInsuranceRefund,vBenificiaryBank,vBenificiaryBranch,vAmmendmentNo,dAmmentmentDate,vAmmendmentReason," +
				"vCnfAgentNAme,dClearingDate,vNameOfLC,vUserName," +
				"vUserIp,dEntryTime,'OLD' from tbLcOpeningInfo where " +
				"vTransactionID = '"+txtTransactionNo.getValue().toString().trim()+"'";

		//System.out.println("udTrack: "+udTrack);
		session.createSQLQuery(udTrackInfo).executeUpdate();

		String udTrackDetails = " INSERT into tbUdLcOpeningDetails (vTransactionID,vProductId,vProductName,vProductUnit,mQuantity,mRate,mRateBdt,mAmountBdt," +
				"mAmount,vHsCode,vLcNo,vFlag) select vTransactionID,vProductId,vProductName,vProductUnit,mQuantity,mRate,mRateBdt," +
				"mAmount,mAmountBdt,vHsCode,vLcNo,'OLD' from tbLcOpeningDetails where vTransactionID = '"+txtTransactionNo.getValue().toString().trim()+"'";

		//System.out.println("udTrack: "+udTrack);
		session.createSQLQuery(udTrackDetails).executeUpdate();

	}

	private void deleteData(Session session)
	{
		String delFormSql = " delete from tbLcOpeningInfo where vTransactionID='"+txtTransactionNo.getValue().toString().trim()+"' ";
		session.createSQLQuery(delFormSql).executeUpdate();

		String delTableSql = " delete from tbLcOpeningDetails where vTransactionID='"+txtTransactionNo.getValue().toString().trim()+"' ";
		session.createSQLQuery(delTableSql).executeUpdate();
	}

	private String generateLedgerID(Session session,String Query)
	{
		String ledgerID = session.createSQLQuery(Query).list().iterator().next().toString();
		return ledgerID;
	}

	private void insertData(Session session)
	{
		String sql = "";
		String InsertDetails= "";
		String TransactionID = "";
		String parent_ID = "";
		if(cmbSubGroup.getValue()!=null)
		{
			parent_ID=cmbSubGroup.getValue().toString();
		}
		else
		{
			if(cmbMainGroup.getValue()!=null)
			{
				parent_ID=cmbMainGroup.getValue().toString();
			}
			else
			{
				parent_ID=cmbPrimaryGroup.getValue().toString();
			}
		}
		
		int acId = 0;
		String activity = cmbIsActive.getValue().toString();
		if(activity.equalsIgnoreCase("Open"))
		{
			acId = 1;
		}
		
		
		String createForm = cmbPrimaryGroup.getValue().toString()+(cmbMainGroup.getValue()!=null?"-"+cmbMainGroup.getValue().toString():"")+(cmbSubGroup.getValue()!=null?"-"+cmbSubGroup.getValue().toString():"");
		if(!isUpdate)
		{
			String ledgerQuery = "select 'AL'+cast(ISNULL(MAX(cast(SUBSTRING(Ledger_Id,3,LEN(Ledger_Id)) as int)),0)+1 " +
					"as varchar) from tbLedger where Ledger_Id like 'AL%'";
			ledgerID=generateLedgerID(session,ledgerQuery);

			String ledgerSql = "insert into tbLedger (Ledger_Id,Ledger_Name,Creation_Year,Parent_Id,Create_From,userId,userIp," +
					"entryTime,companyId) values (" +
					"'"+ledgerID+"'," +
					"'"+txtLcNo.getValue().toString()+"'," +
					"YEAR(GETDATE())," +
					"'"+parent_ID+"'," +
					"'"+createForm+"'," +
					"'"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',GetDATE(),'"+sessionBean.getCompanyId()+"')";
			session.createSQLQuery(ledgerSql).executeUpdate();

			ledgerSql = "insert into tbLedger_Op_Balance(Ledger_Id,DrAmount,CrAmount,Op_Year,userId,userIp,entryTime,companyId) " +
					"values ('"+ledgerID+"','0.00','0.00',YEAR(GETDATE())," +
					"'"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',GETDATE(),'"+sessionBean.getCompanyId()+"')";
			session.createSQLQuery(ledgerSql).executeUpdate();

			String TransactionQuery = "select isnull(max(cast(vTransactionID as int)),0)+1 from tbLcOpeningInfo";
			TransactionID = generateLedgerID(session, TransactionQuery);
		}
		else if (isUpdate)
		{
			String lederSql = "update tbLedger set Ledger_Name = '"+txtLcNo.getValue().toString().trim()+"' where Ledger_Id = '"+ledgerID+"'";
			session.createSQLQuery(lederSql).executeUpdate();
			TransactionID = txtTransactionNo.getValue().toString().trim();
		}
		sql = "insert into tbLcOpeningInfo(vPrimaryGroup,vMainGroup,vSubGroup,vTransactionID,vLcNo,vLedgerID,dLcOpeningDate,vLcOpeningBank," +
				"vLcOpeningBranch,vOrigin,vDischargePort,vVessaileName,dShipmentDate,vModeOfShipment,vArrivalPort,dArrivalDate,dExpiryDate," +
				"vIncoterm,vSupplierName,vSupplierAddress,mLCAmountBDT,mExchangeRate,mLCValueUSD,mMarginPercentage," +
				"mMarginAmount,vBbRefferenceNo,dRefferenceDate,vProformaInvNo,dProformaInvDate,vMarineCoveredNo," +
				"dMarineCoveredDate,vInsCompanyName,mTotalPremium,mNetPremium,mInsuranceRefund,vBenificiaryBank,vBenificiaryBranch," +
				"vAmmendmentNo,dAmmentmentDate,vAmmendmentReason,vCnfAgentNAme,vNameOfLC,dClearingDate,vUserName," +
				"vUserIp,dEntryTime,isActive,mfreeightcost,vlcType) values(" +
				" '"+cmbPrimaryGroup.getValue().toString()+"'," +
				" '"+(cmbMainGroup.getValue()!=null?cmbMainGroup.getValue().toString():"")+"'," +
				" '"+(cmbSubGroup.getValue()!=null?cmbSubGroup.getValue().toString():"")+"'," +
				" '"+TransactionID+"',"+
				" '"+txtLcNo.getValue().toString().trim()+"'," +
				" '"+ledgerID+"'," +
				" '"+dFormat.format(dLcOpeningDate.getValue())+"'," +
				" '"+cmbLcOpeningBank.getValue()+"'," +
				" '"+cmbLcOpeningBranch.getValue()+"'," +
				" '"+txtOrigin.getValue().toString().trim()+"'," +

					" '"+txtDischargePort.getValue().toString().trim()+"'," +
					" '"+txtVessaleName.getValue().toString().trim()+"'," +
					"'"+dFormat.format(dShipmentDate.getValue())+"'," +
					" '"+cmbModeOfShipment.getValue().toString().trim()+"'," +
					" '"+cmbArrivalPort.getValue()+"'," +
					"'"+dFormat.format(dArrivalDate.getValue())+"'," +
					"'"+dFormat.format(dExpiryDate.getValue())+"'," +
					" '"+cmbIncotermType.getValue().toString().trim()+"'," +
					" '"+txtSupplier.getValue().toString().trim()+"'," +

					" '"+(!txtSupplierAddress.getValue().toString().trim().isEmpty()?txtSupplierAddress.getValue().toString().trim():"")+"'," +
					" '"+txtLcValueBdt.getValue().toString().replaceAll(",", "")+"'," +
					" '"+txtExchangeRate.getValue().toString().replaceAll(",", "")+"'," +
					" '"+txtLcValueUsd.getValue().toString().replaceAll(",", "")+"'," +
					" '"+txtMarginPer.getValue().toString().replaceAll(",", "")+"'," +
					" '"+txtMarginAmount.getValue().toString().replaceAll(",", "")+"'," +
					" '"+txtBbReffrenceNo.getValue().toString().trim()+"'," +
					" '"+dFormat.format(dBbReffrenceDate.getValue())+"'," +

					" '"+txtProformaNo.getValue().toString().trim()+"'," +
					" '"+dFormat.format(dProformaDate.getValue())+"'," +
					" '"+txtMarineCoveredNo.getValue().toString().trim()+"'," +
					" '"+dFormat.format(dMarineCoveredDate.getValue())+"'," +
					" '"+txtInsCompanyName.getValue().toString()+"'," +
					" '"+txtTotalPremium.getValue().toString().replaceAll(",", "")+"'," +
					" '"+txtNetPremium.getValue().toString().replaceAll(",", "")+"'," +
					" '"+txtInsuranceRefund.getValue().toString().replaceAll(",", "")+"'," +
					" '"+txtBenificiaryName.getValue().toString().trim()+"'," +
					" '"+txtBenificiaryBranch.getValue().toString().trim()+"'," +
					" '"+txtAmmendmentNo.getValue().toString().trim()+"'," +
					" '"+dFormat.format(dAmmendmentDate.getValue())+"'," +
					" '"+txtAmmendmentReason.getValue().toString().trim()+"'," +
					" '"+txtCnfAgentName.getValue().toString().trim()+"'," +
					" '"+ogNatureofLc.getValue().toString()+"'," +
					" '"+dFormat.format(dClearingDate.getValue())+"'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+acId+"','"+txtFreeightcost.getValue().toString().replaceAll(",", "")+"','"+oplctype.getValue().toString()+"')";

		System.out.println("insert : "+sql);
		session.createSQLQuery(sql).executeUpdate();

		for(int i = 0; i<tbCmbItemName.size(); i++)
		{
			if(tbCmbItemName.get(i).getValue()!=null)
			{
				if(!tbTxtQuantity.get(i).getValue().toString().trim().isEmpty())
				{
					if(!tbTxtRate.get(i).getValue().toString().trim().isEmpty())
					{
						//if(!tbTxtRateBdt.get(i).getValue().toString().trim().isEmpty())
						//{
							if(!tbTxtHsCode.get(i).getValue().toString().trim().isEmpty())
							{
								InsertDetails = "Insert into tbLcOpeningDetails(vTransactionID,vProductId,vProductName,vProductUnit,mQuantity,mRate,mRateBdt," +
										"mAmount,mAmountBdt,vHsCode,vLcNo) values ( " +
										" '"+TransactionID+"','"+tbCmbItemName.get(i).getValue().toString()+"','"+tbCmbItemName.get(i).getItemCaption(tbCmbItemName.get(i).getValue().toString())+"'," +
										" '"+tbTxtUnit.get(i).getValue().toString()+"'," +
										" '"+tbTxtQuantity.get(i).getValue().toString().trim().replaceAll(",", "")+"'," +
										" '"+tbTxtRate.get(i).getValue().toString().trim().replaceAll(",", "")+"'," +
										" '"+tbTxtRateBdt.get(i).getValue().toString().trim().replaceAll(",", "")+"'," +
										" '"+tbTxtAmount.get(i).getValue().toString().trim().replaceAll(",", "")+"'," +
										" '"+tbTxtAmountBDT.get(i).getValue().toString().trim().replaceAll(",", "")+"'," +
										" '"+tbTxtHsCode.get(i).getValue().toString().trim()+"'," +
										" '"+txtLcNo.getValue().toString().trim()+"')";

								System.out.println("insert : "+InsertDetails);
								session.createSQLQuery(InsertDetails).executeUpdate();
							}
							else
							{
								tbTxtHsCode.get(i).focus();
								showNotification("Warning", "Please provide HsCode!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						/*}
						else
						{
							tbTxtRateBdt.get(i).focus();
							showNotification("Warning", "Please provide BDT rate!!!", Notification.TYPE_WARNING_MESSAGE);
						}*/
					}
					else
					{
						tbTxtRate.get(i).focus();
						showNotification("Warning", "Please provide USD rate!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					tbTxtQuantity.get(i).focus();
					showNotification("Warning", "Please provide quantity!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}
		txtClear();
		btnIni(true);
		txtInit(true);
		txtFreeightcost.setEnabled(false);
		isUpdate = false;
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		mainLayout.setHeight("610px");
		mainLayout.setWidth("1020px");

		cmbPrimaryGroup = new ComboBox();
		cmbPrimaryGroup.setImmediate(true);
		cmbPrimaryGroup.setWidth("180px");
		cmbPrimaryGroup.setHeight("-1px");
		cmbPrimaryGroup.setNewItemsAllowed(false);
		cmbPrimaryGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Primary Group :"), "top:15.0px; left:30.0px");
		mainLayout.addComponent(cmbPrimaryGroup, "top:13.0px; left:160.0px;");

		cmbMainGroup = new ComboBox();
		cmbMainGroup.setImmediate(true);
		cmbMainGroup.setWidth("180px");
		cmbMainGroup.setHeight("-1px");
		cmbMainGroup.setNewItemsAllowed(false);
		cmbMainGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Main Group :"), "top:40.0px; left:30.0px");
		mainLayout.addComponent(cmbMainGroup, "top:38.0px; left:160.0px");

		cmbSubGroup = new ComboBox();
		cmbSubGroup.setImmediate(true);
		cmbSubGroup.setWidth("180px");
		cmbSubGroup.setHeight("-1px");
		cmbSubGroup.setNewItemsAllowed(false);
		cmbSubGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Sub Group :"), "top:65.0px; left:30.0px");
		mainLayout.addComponent(cmbSubGroup,  "top:63.0px; left:160.0px;");

		txtTransactionNo = new TextField();
		txtTransactionNo.setImmediate(true);

		txtLcNo = new TextField();
		txtLcNo.setImmediate(true);
		txtLcNo.setWidth("160px");
		mainLayout.addComponent(new Label("L/C No :"), "top:90.0px; left:30.0px");
		mainLayout.addComponent(txtLcNo, "top:88.0px; left:160.0px;");
		txtLcNo.setValue("L/C#");
		
		// txtPersonMobile
				cmbIsActive = new NativeSelect();
				cmbIsActive.setNullSelectionAllowed(false);
				cmbIsActive.setImmediate(true);
				cmbIsActive.setWidth("80px");
				cmbIsActive.setHeight("-1px");
				for (int i = 0; i < cities.length; i++) {
					cmbIsActive.addItem(cities[i]);
				}
				cmbIsActive.setValue(cities[0]);
				mainLayout.addComponent(new Label("Status :"), "top:115.0px; left:30.0px");
				mainLayout.addComponent(cmbIsActive,  "top:113.0px; left:160.0px;");

		dLcOpeningDate = new PopupDateField();
		dLcOpeningDate.setWidth("110px");
		dLcOpeningDate.setImmediate(true);
		dLcOpeningDate.setDateFormat("dd-MM-yyyy");
		dLcOpeningDate.setValue(new Date());
		dLcOpeningDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(new Label("Opening Date :"), "top:140.0px; left:30.0px");
		mainLayout.addComponent(dLcOpeningDate, "top:138.0px; left:160.0px;");

		cmbLcOpeningBank = new ComboBox();
		cmbLcOpeningBank.setImmediate(true);
		cmbLcOpeningBank.setWidth("180px");
		cmbLcOpeningBank.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Opening Bank :"), "top:165.0px; left:30.0px");
		mainLayout.addComponent(cmbLcOpeningBank, "top:163.0px; left:160.0px;");

		cmbLcOpeningBranch = new ComboBox();
		cmbLcOpeningBranch.setImmediate(true);
		cmbLcOpeningBranch.setWidth("180px");
		cmbLcOpeningBranch.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Opening Branch :"), "top:190.0px; left:30.0px");
		mainLayout.addComponent(cmbLcOpeningBranch, "top:188.0px; left:160.0px;");

		txtOrigin = new TextField();
		txtOrigin.setImmediate(false);
		txtOrigin.setWidth("160px");
		mainLayout.addComponent(new Label("Origin :"), "top:215.0px; left:30.0px");
		mainLayout.addComponent(txtOrigin,"top:213.0px; left:160.0px;");

		txtDischargePort = new TextField();
		txtDischargePort.setWidth("110px");
		txtDischargePort.setImmediate(true);
		mainLayout.addComponent(new Label("Discharge Port :"), "top:15.0px; left:360.0px");
		mainLayout.addComponent(txtDischargePort, "top:13.0px; left:480px;");

		txtVessaleName = new TextField();
		txtVessaleName.setImmediate(true);
		txtVessaleName.setWidth("160px");
		mainLayout.addComponent(new Label("Vessel Name :"), "top:40.0px; left:360.0px");
		mainLayout.addComponent(txtVessaleName, "top:38.0px; left:480px;");

		dShipmentDate = new PopupDateField();
		dShipmentDate.setImmediate(false);
		dShipmentDate.setWidth("110px");
		dShipmentDate.setDateFormat("dd-MM-yyyy");
		dShipmentDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dShipmentDate.setValue(new Date());
		mainLayout.addComponent(new Label("Shipment Date :"), "top:65.0px; left:360.0px");
		mainLayout.addComponent(dShipmentDate, "top:63.0px; left:480.0px;");

		cmbModeOfShipment = new ComboBox("",shipmentType);
		cmbModeOfShipment.setImmediate(true);
		cmbModeOfShipment.setWidth("160px");
		cmbModeOfShipment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Mode Of Shipment :"), "top:90.0px; left:360.0px");
		mainLayout.addComponent(cmbModeOfShipment, "top:88.0px; left:480.0px;");

		cmbArrivalPort = new ComboBox();
		cmbArrivalPort.setWidth("160px");
		cmbArrivalPort.setImmediate(true); 
		cmbArrivalPort.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Arrival Port :"), "top:115.0px; left:360.0px");
		mainLayout.addComponent(cmbArrivalPort, "top:113.0px; left:480px;");
		cmbArrivalPort.addItem("Chittagong");
		cmbArrivalPort.addItem("Mongla");

		dArrivalDate = new PopupDateField();
		dArrivalDate.setImmediate(false);
		dArrivalDate.setWidth("110px");
		dArrivalDate.setDateFormat("dd-MM-yyyy");
		dArrivalDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dArrivalDate.setValue(new Date());
		mainLayout.addComponent(new Label("Arrival Date :"), "top:140.0px; left:360.0px");
		mainLayout.addComponent(dArrivalDate,"top:138.0px; left:480.0px;");

		dExpiryDate = new PopupDateField();
		dExpiryDate.setImmediate(false);
		dExpiryDate.setWidth("110px");
		dExpiryDate.setDateFormat("dd-MM-yyyy");
		dExpiryDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dExpiryDate.setValue(new Date());
		mainLayout.addComponent(new Label("Expiry Date :"), "top:165.0px; left:360.0px");
		mainLayout.addComponent(dExpiryDate,"top:163.0px; left:480.0px;");

		cmbIncotermType = new ComboBox("",incotermType);
		cmbIncotermType.setImmediate(true);
		cmbIncotermType.setWidth("160px");
		cmbIncotermType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Incoterm :"), "top:190.0px; left:360.0px");
		mainLayout.addComponent(cmbIncotermType, "top:188.0px; left:480.0px;");

		txtSupplier = new TextField();
		txtSupplier.setImmediate(false);
		txtSupplier.setWidth("160px");
		mainLayout.addComponent(new Label("Supplier Name :"), "top:215.0px; left:360.0px");
		mainLayout.addComponent(txtSupplier, "top:213.0px; left:480.0px;");

		txtSupplierAddress = new TextField();
		txtSupplierAddress.setImmediate(false);
		txtSupplierAddress.setWidth("160px");
		mainLayout.addComponent(new Label("Supplier Address :"), "top:15.0px; left:680.0px");
		mainLayout.addComponent(txtSupplierAddress, "top:13.0px; left:800.0px;");

		txtLcValueUsd = new AmountCommaSeperator();
		txtLcValueUsd.setImmediate(false);
		txtLcValueUsd.setWidth("160px");
		mainLayout.addComponent(new Label("L/C Value (USD) :"), "top:40.0px; left:680.0px");
		mainLayout.addComponent(txtLcValueUsd, "top:38.0px; left:800.0px;");

		txtExchangeRate = new AmountCommaSeperator();
		txtExchangeRate.setImmediate(false);
		txtExchangeRate.setWidth("160px");
		//mainLayout.addComponent(new Label("Exchange Rate :"), "top:65.0px; left:680.0px");
		mainLayout.addComponent(txtExchangeRate, "top:63.0px; left:800.0px;");
		txtExchangeRate.setVisible(false);
		

		txtLcValueBdt = new AmountCommaSeperator();
		txtLcValueBdt.setImmediate(false);
		txtLcValueBdt.setWidth("160px");
		//mainLayout.addComponent(new Label("L/C Value (BDT) :"), "top:90.0px; left:680.0px");
		mainLayout.addComponent(txtLcValueBdt, "top:88.0px; left:800.0px;");
		txtLcValueBdt.setVisible(false);

		txtMarginPer = new AmountCommaSeperator();
		txtMarginPer.setImmediate(false);
		txtMarginPer.setWidth("160px");
		mainLayout.addComponent(new Label("Margin (%)USD :"), "top:65.0px; left:680.0px");
		mainLayout.addComponent(txtMarginPer, "top:63.0px; left:800.0px;");

		txtMarginAmount = new AmountCommaSeperator();
		txtMarginAmount.setImmediate(false);
		txtMarginAmount.setWidth("160px");
		mainLayout.addComponent(new Label("Margin Amount(USD) :"), "top:90.0px; left:680.0px");
		mainLayout.addComponent(txtMarginAmount,"top:88.0px; left:800.0px;");

		txtBbReffrenceNo = new TextField();
		txtBbReffrenceNo.setImmediate(false);
		txtBbReffrenceNo.setWidth("160px");
		mainLayout.addComponent(new Label("B.B Ref. No :"), "top:115.0px; left:680.0px");
		mainLayout.addComponent(txtBbReffrenceNo,"top:113.0px; left:800.0px;");

		dBbReffrenceDate = new PopupDateField();
		dBbReffrenceDate.setImmediate(false);
		dBbReffrenceDate.setWidth("110px");
		dBbReffrenceDate.setDateFormat("dd-MM-yyyy");
		dBbReffrenceDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dBbReffrenceDate.setValue(new Date());
		mainLayout.addComponent(new Label("Ref. Date :"), "top:140.0px; left:680.0px");
		mainLayout.addComponent(dBbReffrenceDate,"top:138.0px; left:800.0px;");
		
		txtFreeightcost = new AmountCommaSeperator();
		txtFreeightcost.setImmediate(false);
		txtFreeightcost.setWidth("160px");
		mainLayout.addComponent(new Label("Freeight Cost(USD) :"), "top:165.0px; left:680.0px");
		mainLayout.addComponent(txtFreeightcost,"top:163.0px; left:800.0px;");
		txtFreeightcost.setEnabled(false);
		
		
		lbllctype = new Label();
		lbllctype.setImmediate(false);
		lbllctype.setWidth("-1px");
		lbllctype.setHeight("-1px");
		lbllctype.setValue("L/C For:");
		mainLayout.addComponent(lbllctype, "top:190.0px;left:680.0px;");
	
		oplctype= new OptionGroup("");
		oplctype.setImmediate(true);
		oplctype.setWidth("-1px");
		oplctype.setHeight("-1px");
		oplctype.setStyleName("horizontal");
		mainLayout.addComponent(oplctype, "top:188.0px;left:800.0px;");

		for(int i=0;i<lctype.length;i++)	
		{
			oplctype.addItem(lctype[i]);
		}

		oplctype.select("RM");

		table.setWidth("100%");
		table.setHeight("175px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Item Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Item Name", 350);

		table.addContainerProperty("Unit", TextRead.class, new TextRead(1));
		table.setColumnWidth("Unit", 60);

		table.addContainerProperty("Qty", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Qty", 70);

		table.addContainerProperty("Unit Price(USD)", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Unit Price(USD)",  77);

		table.addContainerProperty("Unit Price(BDT)", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Unit Price(BDT)",  75);
		table.setColumnCollapsed("Unit Price(BDT)", true);

		table.addContainerProperty("Amount(USD)", TextRead.class, new TextRead());
		table.setColumnWidth("Amount(USD)", 120);

		table.addContainerProperty("Amount(BDT)", TextRead.class, new TextRead());
		table.setColumnWidth("Amount(BDT)", 120);
		table.setColumnCollapsed("Amount(BDT)", true);

		table.addContainerProperty("HS Code", TextField.class, new TextField());
		table.setColumnWidth("HS Code", 120);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER}); 

		mainLayout.addComponent(table,"top:240.0px; left:5.0px;");

		txtProformaNo = new TextField();
		txtProformaNo.setImmediate(false);
		txtProformaNo.setWidth("160px");
		mainLayout.addComponent(new Label("Proforma Inv. No :"), "top:420.0px; left:30.0px");
		mainLayout.addComponent(txtProformaNo, "top:418.0px; left:160.0px;");

		dProformaDate = new PopupDateField();
		dProformaDate.setImmediate(false);
		dProformaDate.setWidth("110px");
		dProformaDate.setDateFormat("dd-MM-yyyy");
		dProformaDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dProformaDate.setValue(new Date());
		mainLayout.addComponent(new Label("Proforma Inv. Date :"), "top:445.0px; left:30.0px");
		mainLayout.addComponent(dProformaDate,"top:443.0px; left:160.0px;");

		txtMarineCoveredNo = new TextField();
		txtMarineCoveredNo.setImmediate(false);
		txtMarineCoveredNo.setWidth("160px");
		mainLayout.addComponent(new Label("Marine Covered No :"), "top:470.0px; left:30.0px");
		mainLayout.addComponent(txtMarineCoveredNo, "top:468.0px; left:160.0px;");

		dMarineCoveredDate = new PopupDateField();
		dMarineCoveredDate.setImmediate(false);
		dMarineCoveredDate.setWidth("110px");
		dMarineCoveredDate.setDateFormat("dd-MM-yyyy");
		dMarineCoveredDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dMarineCoveredDate.setValue(new Date());
		mainLayout.addComponent(new Label("Marine Covered Date :"), "top:495.0px; left:30.0px");
		mainLayout.addComponent(dMarineCoveredDate,"top:493.0px; left:160.0px;");

		txtInsCompanyName = new TextField();
		txtInsCompanyName.setImmediate(true);
		txtInsCompanyName.setWidth("160px");
		mainLayout.addComponent(new Label("Ins. Company Name :"), "top:520.0px; left:30.0px");
		mainLayout.addComponent(txtInsCompanyName,"top:518.0px; left:160.0px;");

		txtTotalPremium = new AmountCommaSeperator();
		txtTotalPremium.setImmediate(false);
		txtTotalPremium.setWidth("160px");
		mainLayout.addComponent(new Label("Total Premium :"), "top:420.0px; left:360.0px");
		mainLayout.addComponent(txtTotalPremium,"top:418.0px; left:480.0px;");

		txtNetPremium = new AmountCommaSeperator();
		txtNetPremium.setImmediate(true);
		txtNetPremium.setWidth("160px");
		mainLayout.addComponent(new Label("Net Premium :"), "top:445.0px; left:360.0px");
		mainLayout.addComponent(txtNetPremium,"top:443.0px; left:480.0px;");

		txtInsuranceRefund = new AmountCommaSeperator();
		txtInsuranceRefund.setImmediate(true);
		txtInsuranceRefund.setWidth("160px");
		mainLayout.addComponent(new Label("Insurance Refund :"), "top:470.0px; left:360.0px");
		mainLayout.addComponent(txtInsuranceRefund, "top:468.0px; left:480.0px;");

		txtBenificiaryName = new TextField();
		txtBenificiaryName.setImmediate(false);
		txtBenificiaryName.setWidth("160px");
		mainLayout.addComponent(new Label("Benificiary Bank :"), "top:495.0px;left:360.0px");
		mainLayout.addComponent(txtBenificiaryName, "top:493.0px; left:480.0px;");

		txtBenificiaryBranch = new TextField();
		txtBenificiaryBranch.setImmediate(false);
		txtBenificiaryBranch.setWidth("160px");
		mainLayout.addComponent(new Label("Benificiary Branch :"), "top:520.0px; left:360.0px");
		mainLayout.addComponent(txtBenificiaryBranch, "top:518.0px; left:480.0px;");

		txtAmmendmentNo = new TextField();
		txtAmmendmentNo.setImmediate(false);
		txtAmmendmentNo.setWidth("160px");
		mainLayout.addComponent(new Label("Ammendment No :"), "top:420.0px; left:680.0px");
		mainLayout.addComponent(txtAmmendmentNo, "top:418.0px; left:820.0px;");

		dAmmendmentDate = new PopupDateField();
		dAmmendmentDate.setImmediate(false);
		dAmmendmentDate.setWidth("110px");
		dAmmendmentDate.setDateFormat("dd-MM-yyyy");
		dAmmendmentDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dAmmendmentDate.setValue(new Date());
		mainLayout.addComponent(new Label("Ammendment Date :"), "top:445.0px; left:680.0px");
		mainLayout.addComponent(dAmmendmentDate,"top:443.0px; left:820.0px;");

		txtAmmendmentReason = new TextField();
		txtAmmendmentReason.setImmediate(false);
		txtAmmendmentReason.setWidth("160px");
		mainLayout.addComponent(new Label("Ammendment Reason :"), "top:470.0px; left:680.0px");
		mainLayout.addComponent(txtAmmendmentReason, "top:468.0px; left:820.0px;");

		ogNatureofLc = new OptionGroup("",type);
		ogNatureofLc.setImmediate(true);
		ogNatureofLc.setWidth("-1px");
		ogNatureofLc.setHeight("-1px");
		ogNatureofLc.setStyleName("horizontal");
		ogNatureofLc.select("UPAS");
		mainLayout.addComponent(new Label("Mode Of Payment :"), "top:495.0px; left:680.0px");
		mainLayout.addComponent(ogNatureofLc, "top:495.0px; left:800.0px;");

		txtCnfAgentName = new TextField();
		txtCnfAgentName.setImmediate(false);
		txtCnfAgentName.setWidth("160px");
		mainLayout.addComponent(new Label("CNF Agent Name :"), "top:520.0px; left:680.0px");
		mainLayout.addComponent(txtCnfAgentName, "top:518.0px; left:820.0px;");

		dClearingDate = new PopupDateField();
		dClearingDate.setImmediate(false);
		dClearingDate.setWidth("110px");
		dClearingDate.setDateFormat("dd-MM-yyyy");
		dClearingDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dClearingDate.setValue(new Date());
		mainLayout.addComponent(new Label("Clearing Date :"), "top:545.0px; left:680.0px");
		mainLayout.addComponent(dClearingDate,"top:543.0px; left:820.0px;");
		mainLayout.addComponent(cButton,"top:570.0px; left:270.0px;");
		
		
		return mainLayout;
		
	}

	private void tableinitialise()
	{
		for(int i=0;i<5;i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd( final int ar)
	{
		tbCmbItemName.add(ar,new ComboBox());
		tbCmbItemName.get(ar).setWidth("100%");
		tbCmbItemName.get(ar).setHeight("-1px");
		tbCmbItemName.get(ar).setImmediate(true);
		tbCmbItemName.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		productAddRM(ar);
		tbCmbItemName.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tbTxtUnit.get(ar).setValue("");
				if(tbCmbItemName.get(ar).getValue()!=null)
				{
					dataSet(ar);
					duplicateItemCheck(ar);
					if(tbCmbItemName.size()-1==ar)
					{
						tableRowAdd(ar+1);
						if(oplctype.getValue().toString().equalsIgnoreCase("RM"))
						{
							productAddRM(ar+1);
						}
						else
						{
							productAddMachinery(ar+1);	
						}
					}
					
						
					
				}
			}
		});

		tbTxtUnit.add(ar, new TextRead(1));
		tbTxtUnit.get(ar).setWidth("100%");
		tbTxtUnit.get(ar).setImmediate(true);

		tbTxtQuantity.add(ar, new AmountCommaSeperator());
		tbTxtQuantity.get(ar).setWidth("100%");
		tbTxtQuantity.get(ar).setImmediate(true);
		tbTxtQuantity.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				calculateAmountUsd(ar);
				calculateAmountBdt(ar);
			}
		});

		tbTxtRate.add(ar,new AmountCommaSeperator());
		tbTxtRate.get(ar).setWidth("100%");
		tbTxtRate.get(ar).setImmediate(true);
		tbTxtRate.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				calculateAmountUsd(ar);
			}
		});

		tbTxtRateBdt.add(ar,new AmountCommaSeperator());
		tbTxtRateBdt.get(ar).setWidth("100%");
		tbTxtRateBdt.get(ar).setImmediate(true);
		tbTxtRateBdt.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				calculateAmountBdt(ar);
			}
		});

		tbTxtAmount.add(ar, new TextRead(1));
		tbTxtAmount.get(ar).setWidth("100%");
		tbTxtAmount.get(ar).setImmediate(true);

		tbTxtAmountBDT.add(ar, new TextRead(1));
		tbTxtAmountBDT.get(ar).setWidth("100%");
		tbTxtAmountBDT.get(ar).setImmediate(true);

		tbTxtHsCode.add(ar, new TextField());
		tbTxtHsCode.get(ar).setWidth("100%");
		tbTxtHsCode.get(ar).setImmediate(true);

		table.addItem(new Object[]{tbCmbItemName.get(ar),tbTxtUnit.get(ar),tbTxtQuantity.get(ar),
				tbTxtRate.get(ar),tbTxtRateBdt.get(ar),tbTxtAmount.get(ar),tbTxtAmountBDT.get(ar),tbTxtHsCode.get(ar)},ar);
	}

	private void duplicateItemCheck(int index)
	{
		for(int i=0; i<index; i++)
		{
			if(tbCmbItemName.get(i).getValue()!=null)
			{
				if(tbCmbItemName.get(i).getValue().toString().equals(tbCmbItemName.get(index).getValue().toString()))
				{
					tbCmbItemName.get(index).setValue(null);
					tbCmbItemName.get(index).focus();
					showNotification("Warning", "Item name already exists!!!", Notification.TYPE_WARNING_MESSAGE);
					break;
				}
			}
		}
	}

	private void productAddRM(final int ar)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String query = "select vRawItemCode,vRawItemName from tbRawItemInfo order by vRawItemCode";
			List<?> lst = session.createSQLQuery(query).list();
			tbCmbItemName.get(ar).removeAllItems();
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object [] element=(Object[])itr.next();
				tbCmbItemName.get(ar).addItem(element[0]);
				tbCmbItemName.get(ar).setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbMainGroupDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	
	private void productAddMachinery(final int ar)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String query = "select Ledger_Id,Ledger_Name from tbLedger where Create_From  like '%G512%'";
			List<?> lst = session.createSQLQuery(query).list();
			tbCmbItemName.get(ar).removeAllItems();
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object [] element=(Object[])itr.next();
				tbCmbItemName.get(ar).addItem(element[0]);
				tbCmbItemName.get(ar).setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbMainGroupDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	
	private void dataSet(final int ar) 
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String query = "select 0,vUnitName from tbRawItemInfo where vRawItemCode =" +
					" '"+tbCmbItemName.get(ar).getValue().toString()+"' ";
			List<?> lst = session.createSQLQuery(query).list();
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object [] element=(Object[])itr.next();
				tbTxtUnit.get(ar).setValue(element[1]);
			}
		}
		catch (Exception exp)
		{
			showNotification("", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void calculateAmountUsd(int ar)
	{
		double qty = Double.parseDouble("0"+tbTxtQuantity.get(ar).getValue().toString().replace(",", ""));
		double rate = Double.parseDouble("0"+tbTxtRate.get(ar).getValue().toString().replace(",", ""));
		tbTxtAmount.get(ar).setValue(cm.setComma(qty*rate));
		double freeightcharge=0;
		freeightcharge=Double.parseDouble("0"+txtFreeightcost.getValue().toString().replace(",", ""));
		
		double LCValueUSD=0.0;
		for(int a=0;a<tbTxtAmount.size();a++){
			if(!tbTxtAmount.get(a).getValue().toString().isEmpty()){
				double amt = Double.parseDouble("0"+tbTxtAmount.get(a).getValue().toString().replace(",", ""));
				LCValueUSD=LCValueUSD+amt;
			}
		}
		txtLcValueUsd.setValue(cm.setComma(LCValueUSD+freeightcharge));
		if(!txtMarginPer.getValue().toString().isEmpty()){
			marginCalculation();
		}
	}

	private void calculateAmountBdt(int ar)
	{
		double qtyBdt = Double.parseDouble("0"+tbTxtQuantity.get(ar).getValue().toString().replace(",", ""));
		double rateBdt = Double.parseDouble("0"+tbTxtRateBdt.get(ar).getValue().toString().replace(",", ""));
		tbTxtAmountBDT.get(ar).setValue(cm.setComma(qtyBdt*rateBdt));
	}

	private void txtClear()
	{
		cmbPrimaryGroup.setValue(null);
		cmbMainGroup.setValue(null);
		cmbSubGroup.setValue(null);

		txtTransactionNo.setValue("");
		txtLcNo.setValue("L/C#");
		cmbIsActive.setValue(cities[0]);
		txtOrigin.setValue("");
		cmbIncotermType.setValue(null);
		cmbModeOfShipment.setValue(null);
		txtSupplier.setValue("");
		txtProformaNo.setValue("");
		txtMarineCoveredNo.setValue("");
		txtBenificiaryName.setValue("");
		txtBenificiaryBranch.setValue("");
		txtInsCompanyName.setValue("");
		txtTotalPremium.setValue("");
		txtNetPremium.setValue("");
		txtInsuranceRefund.setValue("");
		txtAmmendmentNo.setValue("");
		txtAmmendmentReason.setValue("");
		txtCnfAgentName.setValue("");
		cmbLcOpeningBank.setValue(null);
		cmbLcOpeningBranch.setValue(null);
		txtVessaleName.setValue("");
		txtMarginAmount.setValue("");
		txtDischargePort.setValue("");
		cmbArrivalPort.setValue(null);
		txtBbReffrenceNo.setValue("");
		txtSupplierAddress.setValue("");
		txtLcValueUsd.setValue("");
		txtLcValueBdt.setValue("");
		txtMarginPer.setValue("");
		txtExchangeRate.setValue("");
		txtFreeightcost.setValue("");
		oplctype.select("RM");
		//tableClear();
	}

	private void tableClear()
	{
		for(int i = 0; i<tbCmbItemName.size(); i++)
		{
			tbCmbItemName.get(i).setValue(null);
			tbTxtAmount.get(i).setValue("");
			tbTxtAmountBDT.get(i).setValue("");
			tbTxtHsCode.get(i).setValue("");
			tbTxtQuantity.get(i).setValue("");
			tbTxtRate.get(i).setValue("");
			tbTxtRateBdt.get(i).setValue("");
			tbTxtUnit.get(i).setValue("");
		}
	}

	public void txtInit(boolean t)
	{
		cmbPrimaryGroup.setEnabled(!t);
		cmbMainGroup.setEnabled(!t);
		cmbSubGroup.setEnabled(!t);

		txtLcNo.setEnabled(!t);
		cmbIsActive.setEnabled(!t);
		dLcOpeningDate.setEnabled(!t);
		txtOrigin.setEnabled(!t);
		dShipmentDate.setEnabled(!t);
		cmbModeOfShipment.setEnabled(!t);
		dExpiryDate.setEnabled(!t);
		cmbIncotermType.setEnabled(!t);
		txtSupplier.setEnabled(!t);
		txtProformaNo.setEnabled(!t);
		dProformaDate.setEnabled(!t);
		txtMarineCoveredNo.setEnabled(!t);
		dMarineCoveredDate.setEnabled(!t);
		txtBenificiaryName.setEnabled(!t);
		txtBenificiaryBranch.setEnabled(!t);
		ogNatureofLc.setEnabled(!t);
		txtInsCompanyName.setEnabled(!t);
		txtTotalPremium.setEnabled(!t);
		txtNetPremium.setEnabled(!t);
		txtInsuranceRefund.setEnabled(!t);
		txtAmmendmentNo.setEnabled(!t);
		dAmmendmentDate.setEnabled(!t);
		txtAmmendmentReason.setEnabled(!t);
		dClearingDate.setEnabled(!t);
		txtCnfAgentName.setEnabled(!t);
		cmbLcOpeningBank.setEnabled(!t);
		cmbLcOpeningBranch.setEnabled(!t);
		txtVessaleName.setEnabled(!t);
		txtMarginAmount.setEnabled(!t);
		txtDischargePort.setEnabled(!t);
		cmbArrivalPort.setEnabled(!t);
		dArrivalDate.setEnabled(!t);
		dBbReffrenceDate.setEnabled(!t);
		txtBbReffrenceNo.setEnabled(!t);
		txtSupplierAddress.setEnabled(!t);
		txtLcValueUsd.setEnabled(!t);
		txtLcValueBdt.setEnabled(!t);
		txtMarginPer.setEnabled(!t);
		txtExchangeRate.setEnabled(!t);
		oplctype.setEnabled(!t);
		table.setEnabled(!t);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnPreview.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private void focusEnter()
	{
		allComp.add(cmbPrimaryGroup);
		allComp.add(cmbMainGroup);
		allComp.add(cmbSubGroup);
		allComp.add(txtLcNo);
		allComp.add(dLcOpeningDate);
		allComp.add(cmbLcOpeningBank);
		allComp.add(cmbLcOpeningBranch);
		allComp.add(txtOrigin);
		allComp.add(txtDischargePort);
		allComp.add(txtVessaleName);
		allComp.add(dShipmentDate);
		allComp.add(cmbModeOfShipment);
		allComp.add(cmbArrivalPort);
		allComp.add(dArrivalDate);
		allComp.add(dExpiryDate);
		allComp.add(cmbIncotermType);
		allComp.add(txtSupplier);
		allComp.add(txtSupplierAddress);
		allComp.add(txtLcValueUsd);
		allComp.add(txtExchangeRate);
		allComp.add(txtLcValueBdt);
		allComp.add(txtMarginPer);
		allComp.add(txtMarginAmount);
		allComp.add(txtBbReffrenceNo);
		allComp.add(dBbReffrenceDate);
		allComp.add(txtFreeightcost);

		for(int i=0;i<tbCmbItemName.size();i++)
		{
			allComp.add(tbCmbItemName.get(i));
			allComp.add(tbTxtQuantity.get(i));
			allComp.add(tbTxtRate.get(i));
			//allComp.add(tbTxtRateBdt.get(i));
			//allComp.add(tbTxtAmount.get(i));
			//allComp.add(tbTxtAmountBDT.get(i));
			allComp.add(tbTxtHsCode.get(i));
		}

		allComp.add(txtProformaNo);
		allComp.add(dProformaDate);
		allComp.add(txtMarineCoveredNo);
		allComp.add(dMarineCoveredDate);
		allComp.add(txtInsCompanyName);
		allComp.add(txtTotalPremium);
		allComp.add(txtNetPremium);
		allComp.add(txtInsuranceRefund);
		allComp.add(txtBenificiaryName);
		allComp.add(txtBenificiaryBranch);
		allComp.add(txtAmmendmentNo);
		allComp.add(dAmmendmentDate);
		allComp.add(txtAmmendmentReason);
		allComp.add(txtCnfAgentName);
		allComp.add(dClearingDate);
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}
}