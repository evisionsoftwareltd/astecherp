package acc.appform.transportModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.hrmModule.EmployeeInformation;
import acc.appform.setupTransaction.SupplierInformation;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class VehicleInfo extends Window 
{
	private AbsoluteLayout mainLayout;
	private Label lblOwnerShip;
	private Label lblVehicleType;
	private Label lblVehicleNo;
	private Label lblRegType;
	private Label lblRegNo;
	private Label lblAccNo;
	private Label lblAddress;
	private Label lblTinNo;
	private Label lblUnloadWeight;
	private Label lblLoadWeight;
	private Label lblManufacturer;
	private Label lblManuYear;
	private Label lblPurCost;
	private Label lblMonthlyFare;
	private Label lblMonthlyInstall;
	private Label lblTaxTokenDate;
	private Label lblTaxTokenExpDate;
	private Label lblFitnessDate;
	private Label lblFitnessExpDate;
	private Label lblInsuranceDate;
	private Label lblChassisNo;
	private Label lblEngineNo;
	private Label lblCylinder;
	private Label lblCC;
	private Label lblColor;
	private Label lblNoSeat;
	private Label lblTyreSize;
	private Label lblNoOfTyre;
	private Label lblSupplierName;
	private Label lblSupplierAdd;
	private Label lblSupplierPhone;
	private Label lblSupplierMobile;
	private Label lblUnitCode;
	private Label lblSubUnitCode;
	private Label lblDriverCode;
	private Label lblDriverName;
	private Label lblDriverMobile;
	
	private TextField txtVehicleNo;
	private TextField txtRegNo;
	private TextField txtAccNo;
	private TextField txtAddress;
	private TextField txtTinNo;
	private TextField txtManuYear;
	private AmountField txtUnloadWeight;
	private AmountField txtLoadWeight;
	private AmountCommaSeperator txtPurCost;
	private AmountCommaSeperator txtMonthlyFare;
	private AmountCommaSeperator txtMonthlyInstall;
	private TextField txtChasisNo;
	private TextField txtEngineNo;
	private TextField txtCylinder;
	private TextField txtCC;
	private TextField txtColor;
	private TextField txtNoSeat;
	private TextField txtTyreSize;
	private TextField txtNoOfTyre;
	
	private TextRead findVehicleID = new TextRead();
	private TextRead txtSupplierAdd;
	private TextRead txtSupplierPhone;
	private TextRead txtSupplierMobile;
	private TextRead txtDriverName;
	private TextRead txtDriverMobile;
	
	private PopupDateField dtaxTokenDate;
	private PopupDateField dtaxTokenExpDate;
	private PopupDateField dFitnessDate;
	private PopupDateField dFitnessExpDate;
	private PopupDateField dInsuranceDate;
	private PopupDateField dInsuranceExpDate;
	
	private static final String[] strVehicleType = new String[]{"TRUCK","PICK-UP","MICRO","HI-ACE","TEMPO VAN","TEXI VAN","RIKSHA VAN"};
	private static final String[] strOwnerShip = new String[]{"Own","Rent","Lease"};
	private static final String[] strRegType = new String[]{"Single","Joint"};
	private static final String[] strManufacturer = new String[]{"TATA","Toyota","Maruti"};
	
	private ComboBox cmbVehicleType;
	
	private ComboBox cmbManufacturer;
	private ComboBox cmbOwnership;
	private ComboBox cmbRegType;
	private ComboBox cmbSupplierName;
	private ComboBox cmbUnitCode;
	private ComboBox cmbSubUnitCode;
	private ComboBox cmbDriverCode;

	private NativeButton btnTinNo;
	private NativeButton btnTaxTokenDate;
	private NativeButton btnRegNo;
	private NativeButton btnFitnessDate;
	private NativeButton btnEmployee;
	private NativeButton btnSupplier;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat formDateFormat = new SimpleDateFormat("dd-MM-yyyy");

	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "","","","Exit");

	private ArrayList<Component> allComp = new ArrayList<Component>();

	boolean isUpadte = false;
	boolean isFind = false;

	String autoVehicleId;

	TextRead id = new TextRead();
	TextRead vehicleId = new TextRead();

	SessionBean sessionBean;
	
	public VehicleInfo(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("VEHICLE INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainLayout();
		setContent(mainLayout);
		
		focusMove();
		componentIni(false);
		btnIni(true);
		
		loadSupplierName();
		loadDriverCode();
		
		setBtnAction();
	}
	
	private void setBtnAction()
	{	
		cButton.btnNew.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				btnIni(false);
				componentIni(true);
				clearData();
			}
		});

		cButton.btnSave.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				checkValidation();
			}
		});

		cButton.btnEdit.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				if(cmbVehicleType.getValue()!=null)
				{
					isUpadte = true;
					componentIni(true);
					btnIni(false);
				}
				else
				{
					getWindow().showNotification("Warning","There are no data for update",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnFind.addListener(new ClickListener() 
		{		
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

		cButton.btnRefresh.addListener(new ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				isUpadte=false;
				componentIni(false);
				btnIni(true);
				clearData();
			}
		});

		cButton.btnExit.addListener(new ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		
		cmbUnitCode.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				loadSubUnitCode();
			}
		});
		
		cmbSupplierName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				setSupplierInfo();
			}
		});
		
		cmbOwnership.addListener(new Listener() 
		{
			public void componentEvent(Event event)
			{
				changeTxtField();
			}
		});
		
		cmbDriverCode.addListener(new Listener() 
		{
			public void componentEvent(Event event)
			{
				setDriverCode();
			}
		});
		
		btnEmployee.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				EmployeeInfoLink();	
			}
		});
		
		btnSupplier.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				SupplierLink();	
			}
		});
	}
	
	public void EmployeeInfoLink()
	{
		Window win = new EmployeeInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				loadDriverCode();
			}
		});
		this.getParent().addWindow(win);
	}
	
	public void SupplierLink()
	{
		Window win = new SupplierInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				loadSupplierName();
			}
		});
		this.getParent().addWindow(win);
	}
	
	private void changeTxtField()
	{
		txtPurCost.setValue("");
		txtMonthlyFare.setValue("");
		txtMonthlyInstall.setValue("");
		cmbSupplierName.setValue(null);
		txtSupplierAdd.setValue("");
		txtSupplierPhone.setValue("");
		txtSupplierMobile.setValue("");
		
		if(cmbOwnership.getValue()=="Own")
		{
			getOwnField();
		}
		if(cmbOwnership.getValue()=="Rent")
		{
			getRentField();
		}
		if(cmbOwnership.getValue()=="Lease")
		{
			getLeaseField();
		}
	}
	
	private void getOwnField()
	{
		txtPurCost.setEnabled(true);
		txtMonthlyFare.setEnabled(false);
		txtMonthlyInstall.setEnabled(false);
		cmbSupplierName.setEnabled(true);
		txtSupplierAdd.setEnabled(true);
		txtSupplierPhone.setEnabled(true);
		txtSupplierMobile.setEnabled(true);
		
		if(txtPurCost.getValue()==null)
		{
			txtPurCost.setValue("");
			txtMonthlyFare.setValue("");
			txtMonthlyInstall.setValue("");
			cmbSupplierName.setValue(null);
			txtSupplierAdd.setValue("");
			txtSupplierPhone.setValue("");
			txtSupplierMobile.setValue("");
		}
	}
	
	private void getRentField()
	{
		txtPurCost.setEnabled(false);
		txtMonthlyFare.setEnabled(true);
		txtMonthlyInstall.setEnabled(false);
		cmbSupplierName.setEnabled(false);
		txtSupplierAdd.setEnabled(false);
		txtSupplierPhone.setEnabled(false);
		txtSupplierMobile.setEnabled(false);
		
		if(txtMonthlyFare.getValue()==null)
		{
			txtPurCost.setValue("");
			txtMonthlyFare.setValue("");
			txtMonthlyInstall.setValue("");
			cmbSupplierName.setValue(null);
			txtSupplierAdd.setValue("");
			txtSupplierPhone.setValue("");
			txtSupplierMobile.setValue("");
		}
	}
	
	private void getLeaseField()
	{
		txtPurCost.setEnabled(false);
		txtMonthlyFare.setEnabled(false);
		txtMonthlyInstall.setEnabled(true);
		cmbSupplierName.setEnabled(true);
		txtSupplierAdd.setEnabled(true);
		txtSupplierPhone.setEnabled(true);
		txtSupplierMobile.setEnabled(true);
		
		if(txtMonthlyInstall.getValue()==null)
		{
			txtPurCost.setValue("");
			txtMonthlyFare.setValue("");
			txtMonthlyInstall.setValue("");
			cmbSupplierName.setValue(null);
			txtSupplierAdd.setValue("");
			txtSupplierPhone.setValue("");
			txtSupplierMobile.setValue("");
		}
	}
	
	private void checkValidation()
	{
		if(cmbVehicleType.getValue()!=null)
		{
			if(cmbOwnership.getValue()!=null)
			{
				if(!txtVehicleNo.getValue().toString().isEmpty())
				{
					if(cmbRegType.getValue()!=null)
					{
						if(!txtRegNo.getValue().toString().isEmpty())
						{
							if(!txtAccNo.getValue().toString().isEmpty())
							{
								if(!txtAddress.getValue().toString().isEmpty())
								{
									if(!txtTinNo.getValue().toString().isEmpty())
									{
										if(!txtUnloadWeight.getValue().toString().isEmpty())
										{
											if(!txtLoadWeight.getValue().toString().isEmpty())
											{
												if(cmbManufacturer.getValue()!=null)
												{
													if(!txtManuYear.getValue().toString().isEmpty())
													{
														if(!txtChasisNo.getValue().toString().isEmpty())
															{
																if(!txtEngineNo.getValue().toString().isEmpty())
																	{
																		if(!txtCylinder.getValue().toString().isEmpty())
																			{
																				if(!txtCC.getValue().toString().isEmpty())
																				{
																					if(!txtColor.getValue().toString().isEmpty())
																					{
																						if(!txtNoSeat.getValue().toString().isEmpty())
																						{
																							if(!txtTyreSize.getValue().toString().isEmpty())
																							{
																								if(!txtNoOfTyre.getValue().toString().isEmpty())
																								{	
																									chkOwnerShip();																									
																								}
																								else
																								{
																									showNotification("Warning","provide Number of Tyre");
																									txtNoOfTyre.focus();
																								}
																							}
																							else
																							{
																								showNotification("Warning","provide Tyre Size");
																								txtTyreSize.focus();
																							}
																						}
																						else
																						{
																							showNotification("Warning","provide Number Of Seat");
																							txtNoSeat.focus();
																						}
																					}
																					else
																					{
																						showNotification("Warning","provide Color");
																						txtColor.focus();
																					}
																				}
																				else
																				{
																					showNotification("Warning","provide Engine CC");
																					txtCC.focus();
																				}
																			}
																			else
																			{
																				showNotification("Warning","provide Cylinder");
																				txtCylinder.focus();
																			}
																		}
																		else
																		{
																			showNotification("Warning","provide Engine No");
																			txtEngineNo.focus();
																		}
																	}
																else
																{
																	showNotification("Warning","provide Chasis No");
																	txtChasisNo.focus();
																}
														}
													else
													{
														showNotification("Warning","provide Manufacture Year");
														txtManuYear.focus();
													}
												}
												else
												{
													showNotification("Warning","provide Manufacturer Name");
													cmbManufacturer.focus();
												}
											}
											else
											{
												showNotification("Warning","provide Laden Weight");
												txtLoadWeight.focus();
											}
										}
										else
										{
											showNotification("Warning","provide Unladen Weight");
											txtUnloadWeight.focus();
										}
									}
									else
									{
										showNotification("Warning","provide TIN No");
										txtTinNo.focus();
									}
								}
								else
								{
									showNotification("Warning","provide Address");
									txtAddress.focus();
								}
							}
							else
							{
								showNotification("Warning","provide Account No");
								txtAccNo.focus();
							}
						}
						else
						{
							showNotification("Warning","provide Registration No");
							txtRegNo.focus();
						}
					}
					else
					{
						showNotification("Warning","provide Registration Type");
						cmbRegType.focus();
					}
				}
				else
				{
					showNotification("Warning","provide Vehicle No");
					txtVehicleNo.focus();
				}
			}
			else
			{
				showNotification("Warning","select Ownership Type");
				cmbOwnership.focus();
			}
		}
		else
		{
			showNotification("Warning","select Vehicle Type");
			cmbVehicleType.focus();
		}
	}
	
	private void chkOwnerShip()
	{
		System.out.print(cmbOwnership.getValue());
		
		if(cmbOwnership.getValue()=="Own")
		{
			if(txtPurCost.getValue().toString()!="")
			{
				saveButtonEvent();
			}
			else
			{
				showNotification("Warning","Provide Purchase Cost");
				txtPurCost.focus();
			}
		}
		
		else if(cmbOwnership.getValue()=="Rent")
		{
			if(txtMonthlyFare.getValue().toString()!="")
			{	
				saveButtonEvent();
			}
			else
			{
				showNotification("Warning","Provide Monthly Fare");
				txtMonthlyFare.focus();
			}
		}
		
		else if(cmbOwnership.getValue()=="Lease")
		{
			if(txtMonthlyInstall.getValue().toString()!="")
			{
				//if(cmbSupplierName.getValue()!=null)
				{
					saveButtonEvent();
				}
//				else
//				{
//					showNotification("Warning","Select Supplier Name");
//					cmbSupplierName.focus();
//				}
			}
			else
			{
				showNotification("Warning","Provide Monthly Istallment");
				txtPurCost.focus();
			}
		}
		else
		{
			saveButtonEvent();
		}
	}
	
	private void loadDriverCode()
	{
		cmbDriverCode.removeAllItems();
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();

			tx = session.beginTransaction();
			
			String query = " SELECT vEmployeeId,employeeCode from tbEmployeeInfo ";
			System.out.println("Route Query: "+query);

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbDriverCode.addItem(element[1].toString());
				cmbDriverCode.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}
	
	private void setDriverCode()
	{
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();

			tx = session.beginTransaction();
			
			String query = "   select vEmployeeName,vContact from tbEmployeeInfo where employeeCode='"+cmbDriverCode.getValue().toString().replace("#", "")+"' ";
			System.out.println("Driver Query: "+query);

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				txtDriverName.setValue(element[0]);
				txtDriverMobile.setValue(element[1]);
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}
	
	private void saveButtonEvent() 
	{
		if (isUpadte) 
		{
			MessageBox mb = new MessageBox(getParent(),"Are you sure?", MessageBox.Icon.QUESTION,
					"Do you want to update  information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,
							"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener() 
			{
				public void buttonClicked(ButtonType buttonType) 
				{
					if (buttonType == ButtonType.YES) 
					{
						Transaction tx = null;
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();

						tx = session.beginTransaction();

						if(deleteData())
						{
							System.out.println("delete");
							insertData();
						}
						else
						{
							tx.rollback();
						}

						isUpadte = false;
						isFind = false;

						componentIni(false);
						btnIni(true);
					}
				}
			});
		}
		else 
		{
			MessageBox mb = new MessageBox(getParent(),"Are you sure?", MessageBox.Icon.QUESTION,
					"Do you want to save  information?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,
							"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener() 
			{
				public void buttonClicked(ButtonType buttonType) 
				{
					if (buttonType == ButtonType.YES) 
					{
						insertData();

						componentIni(false);
						btnIni(true);

						clearData();
					}
				}
			});
		}
	}
	
	//Find Button Action
	private void findButtonEvent()
	{
		Window win=new VehicleFindWindow(sessionBean,findVehicleID,"VehicleInfo");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(findVehicleID.getValue().toString().length()>0)
				{
					clearData();
					findInitialise(findVehicleID.getValue().toString());
					
					System.out.println("Main ID: "+findVehicleID);
					
				}
			}
		});
		this.getParent().addWindow(win);
	}
	
	private boolean deleteData()
	{
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		
		try
		{	
			String sql1 = " DELETE from tbVehicleInfo where vehicleId = '"+findVehicleID.getValue().toString()+ "' ";
			System.out.println("Delet Query: "+sql1);
			
			session.createSQLQuery(sql1).executeUpdate();
			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			return false;
		}
	}
	
	//Find Initialize
	private void findInitialise(String findIFD) 
	{
		Transaction tx = null;
		try 
		{
			vehicleId.setValue(findIFD);
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List led = session.createSQLQuery("select * from tbVehicleInfo where vehicleId = '"+findIFD+"' ").list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				//cmbVehicleType.setItemCaption(cmbVehicleType, element[2].toString());
				cmbVehicleType.addItem(element[2].toString());
				cmbVehicleType.setValue(element[2].toString());
				
				//cmbOwnership.setItemCaption(cmbOwnership, element[3].toString());
				cmbOwnership.addItem(element[3].toString());
				cmbOwnership.setValue(element[3].toString());
				
				//cmbRegType.setItemCaption(cmbRegType, element[4].toString());
				cmbRegType.addItem(element[5].toString());
				cmbRegType.setValue(element[5].toString());
				
				txtVehicleNo.setValue(element[4].toString());
				txtRegNo.setValue(element[6].toString());
				txtAccNo.setValue(element[7].toString());
				txtAddress.setValue(element[8].toString());
				txtTinNo.setValue(element[9].toString());
				txtUnloadWeight.setValue(element[10].toString());
				txtLoadWeight.setValue(element[11].toString());
				
				//cmbManufacturer.setItemCaption(cmbManufacturer, element[12].toString());
				cmbManufacturer.addItem(element[12].toString());
				cmbManufacturer.setValue(element[12].toString());
				
				txtManuYear.setValue(element[13].toString());
				txtPurCost.setValue(element[14].toString());
				txtMonthlyFare.setValue(element[15].toString());
				txtMonthlyInstall.setValue(element[16].toString());
				
				dtaxTokenDate.setValue(element[17]);
				dtaxTokenExpDate.setValue(element[18]);
				dFitnessDate.setValue(element[19]);
				dFitnessExpDate.setValue(element[20]);
				dInsuranceDate.setValue(element[43]);
				dInsuranceExpDate.setValue(element[44]);
				
				txtChasisNo.setValue(element[21].toString());
				txtEngineNo.setValue(element[22].toString());
				txtCylinder.setValue(element[23].toString());
				txtCC.setValue(element[24].toString());
				txtColor.setValue(element[25].toString());
				txtNoSeat.setValue(element[26].toString());
				txtTyreSize.setValue(element[27].toString());
				txtNoOfTyre.setValue(element[28].toString());
				txtSupplierAdd.setValue(element[30].toString());
				txtSupplierPhone.setValue(element[31].toString());
				txtSupplierMobile.setValue(element[32].toString());

//				cmbUnitCode.setValue(element[33]+"#");
//				cmbSubUnitCode.setValue(element[34]+"#");

				//cmbDriverCode.addItem(element[35].toString());
				cmbDriverCode.setValue(element[35].toString());
				
				txtDriverName.setValue(element[36].toString());
				txtDriverMobile.setValue(element[37].toString());
				
				//cmbSupplierName.addItem(element[29].toString());
				cmbSupplierName.setValue(element[45].toString());
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}	
		
	private void insertData() 
	{
		Transaction tx = null;

		try 
		{
			String SupplierCode = "";
			String query = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			if(!isUpadte)
			{
				autoVehicleId = autoId();
			}
			else
			{
				autoVehicleId = vehicleId.getValue().toString();
			}
			if(cmbSupplierName.getValue()!=null)
			{
				SupplierCode = cmbSupplierName.getValue().toString();
			}
			
			
			String sql = " Insert into tbVehicleInfo values ( "+
					" '"+autoVehicleId+"', " +
					" '"+cmbVehicleType.getItemCaption(cmbVehicleType.getValue())+"', " +
					" '"+cmbOwnership.getItemCaption(cmbOwnership.getValue())+"', " +
					" '"+txtVehicleNo.getValue()+"', " +
					" '"+cmbRegType.getItemCaption(cmbRegType.getValue())+"', " +
					" '"+txtRegNo.getValue()+"', " +
					" '"+txtAccNo.getValue()+"', " +
					" '"+txtAddress.getValue()+"', " +
					" '"+txtTinNo.getValue()+"', " +
					" '"+txtUnloadWeight.getValue()+"', " +
					" '"+txtLoadWeight.getValue()+"', " +
					" '"+cmbManufacturer.getItemCaption(cmbManufacturer.getValue())+"', " +
					" '"+txtManuYear.getValue()+"', " +
					" '"+txtPurCost.getValue()+"', " +
					" '"+txtMonthlyFare.getValue()+"', " +
					" '"+txtMonthlyInstall.getValue()+"', " +
					" '"+dateFormat.format(dtaxTokenDate.getValue())+"', " +
					" '"+dateFormat.format(dtaxTokenExpDate.getValue())+"', " +
					" '"+dateFormat.format(dFitnessDate.getValue())+"', " +
					" '"+dateFormat.format(dFitnessExpDate.getValue())+"', " +
					" '"+txtChasisNo.getValue()+"', " +
					" '"+txtEngineNo.getValue()+"', " +
					" '"+txtCylinder.getValue()+"', " +
					" '"+txtCC.getValue()+"', " +
					" '"+txtColor.getValue()+"', " +
					" '"+txtNoSeat.getValue()+"', " +
					" '"+txtTyreSize.getValue()+"', " +
					" '"+txtNoOfTyre.getValue()+"', " +
					" '"+cmbSupplierName.getItemCaption(cmbSupplierName.getValue())+"', " +
					" '"+txtSupplierAdd.getValue()+"', " +
					" '"+txtSupplierPhone.getValue()+"', " +
					" '"+txtSupplierMobile.getValue()+"', " +
					" '0', " +
					" '0', " +
					" '"+cmbDriverCode.getItemCaption(cmbDriverCode.getValue())+"', " +
					" '"+txtDriverName.getValue()+"', " +
					" '"+txtDriverMobile.getValue()+"', " +
					" '"+sessionBean.getUserId()+"', " +
					" '"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, " +
					" '0'," +
					" '0', "+
					" '"+dateFormat.format(dInsuranceDate.getValue())+"', " +
					" '"+dateFormat.format(dInsuranceExpDate.getValue())+"', " +
					" '"+SupplierCode+"' ) ";
			
			System.out.println("Insert Query: "+sql);
			
			session.createSQLQuery(sql).executeUpdate();
			isUpadte = false;
			
			tx.commit();

			getWindow().showNotification("All information saved successfully.");

			clearData();
		}
		catch (Exception exp) 
		{
			tx.rollback();
			showNotification("Error","Couldn't save data correctly",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public String autoId() 
	{
		String autoCode = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " Select cast(isnull(max(cast(replace(vehicleId, '', '')as int))+1, 1)as varchar) from tbVehicleInfo ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}
	
	private void loadUnitCode()
	{
		cmbUnitCode.removeAllItems();
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();

			tx = session.beginTransaction();
			
			String query = "select unitId,unitCode,unitName from tbUnitInfo order by unitId";
			System.out.println("Route Query: "+query);

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbUnitCode.addItem(element[0]+"#");
				cmbUnitCode.setItemCaption(element[0]+"#", element[1].toString()+" ("+element[2].toString()+")");
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}
	
	private void loadSubUnitCode()
	{
		cmbSubUnitCode.removeAllItems();
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();

			tx = session.beginTransaction();
			
			String query = "select subUnitId,subUnitCode,subUnitName from tbSubUnitInfo where unitId='"+cmbUnitCode.getValue().toString().replaceAll("#", "")+"' order by subUnitId";
			System.out.println("Route Query: "+query);

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSubUnitCode.addItem(element[0]+"#");
				cmbSubUnitCode.setItemCaption(element[0]+"#", element[1].toString()+" ("+element[2].toString()+")");
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}
	
	private void loadSupplierName()
	{
		cmbSupplierName.removeAllItems();
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();

			tx = session.beginTransaction();
			
			String query = "select supplierCode,supplierName from tbSupplier_Info order by supplierId";
			
			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSupplierName.addItem(element[0].toString());
				cmbSupplierName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}
	
	private void setSupplierInfo()
	{
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();

			tx = session.beginTransaction();
			
			String query = "select address,telephone,mobile from tbSupplier_Info where supplierCode='"+cmbSupplierName.getValue().toString().replaceAll("#", "")+"' ";
			System.out.println("Route Query: "+query);

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				txtSupplierAdd.setValue(element[0]);
				txtSupplierPhone.setValue(element[1]);
				txtSupplierMobile.setValue(element[2]);
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}
	
	private void focusMove()
	{
		allComp.add(cmbVehicleType);
		allComp.add(cmbOwnership);
		allComp.add(txtVehicleNo);
		allComp.add(cmbRegType);
		allComp.add(txtRegNo);
		allComp.add(txtAccNo);
		allComp.add(txtAddress);
		allComp.add(txtTinNo);
		allComp.add(txtUnloadWeight);
		allComp.add(txtLoadWeight);
		allComp.add(cmbManufacturer);
		allComp.add(txtManuYear);
		allComp.add(txtPurCost);
		allComp.add(txtMonthlyFare);
		allComp.add(txtMonthlyInstall);
		allComp.add(dtaxTokenDate);
		allComp.add(dtaxTokenExpDate);
		allComp.add(dFitnessDate);
		allComp.add(dFitnessExpDate);
		allComp.add(dInsuranceDate);
		allComp.add(dInsuranceExpDate);
		allComp.add(txtChasisNo);
		allComp.add(txtEngineNo);
		allComp.add(txtCylinder);
		allComp.add(txtCC);
		allComp.add(txtColor);
		allComp.add(txtNoSeat);
		allComp.add(txtTyreSize);
		allComp.add(txtNoOfTyre);
		allComp.add(cmbSupplierName);
		allComp.add(cmbDriverCode);

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}
	
	private void componentIni(boolean t)
	{
		txtVehicleNo.setEnabled(t);
		txtRegNo.setEnabled(t);
		txtAccNo.setEnabled(t);
		txtAddress.setEnabled(t);
		txtTinNo.setEnabled(t);
		txtManuYear.setEnabled(t);
		txtUnloadWeight.setEnabled(t);
		txtLoadWeight.setEnabled(t);
		txtPurCost.setEnabled(t);
		txtMonthlyFare.setEnabled(t);
		txtMonthlyInstall.setEnabled(t);
		txtChasisNo.setEnabled(t);
		txtEngineNo.setEnabled(t);
		txtCylinder.setEnabled(t);
		txtCC.setEnabled(t);
		txtColor.setEnabled(t);
		txtNoSeat.setEnabled(t);
		txtTyreSize.setEnabled(t);
		txtNoOfTyre.setEnabled(t);
		txtSupplierAdd.setEnabled(t);
		txtSupplierPhone.setEnabled(t);
		txtSupplierMobile.setEnabled(t);
		cmbUnitCode.setEnabled(t);
		cmbSubUnitCode.setEnabled(t);
		cmbDriverCode.setEnabled(t);
		txtDriverName.setEnabled(t);
		txtDriverMobile.setEnabled(t);
		
		dtaxTokenDate.setEnabled(t);
		dtaxTokenExpDate.setEnabled(t);
		dFitnessDate.setEnabled(t);
		dFitnessExpDate.setEnabled(t);
		dInsuranceDate.setEnabled(t);
		dInsuranceExpDate.setEnabled(t);
		
		cmbVehicleType.setEnabled(t);
		cmbManufacturer.setEnabled(t);
		cmbOwnership.setEnabled(t);
		cmbRegType.setEnabled(t);
		cmbSupplierName.setEnabled(t);

		btnTinNo.setEnabled(t);
		btnTaxTokenDate.setEnabled(t);
		btnRegNo.setEnabled(t);
		btnFitnessDate.setEnabled(t);
		btnSupplier.setEnabled(t);
		btnEmployee.setEnabled(t);
		
		if(isUpadte)
		{
			System.out.print("Get Ownership:"+cmbOwnership.getValue());
			if(cmbOwnership.getValue().toString().equals("Own"))
			{
				getOwnField();
			}
			if(cmbOwnership.getValue().toString().equals("Rent"))
			{
				getRentField();
			}
			if(cmbOwnership.getValue().toString().equals("Lease"))
			{
				getLeaseField();
			}
		}
	}
	
	private void clearData()
	{
		txtVehicleNo.setValue("");
		txtRegNo.setValue("");
		txtAccNo.setValue("");
		txtAddress.setValue("");
		txtTinNo.setValue("");
		txtManuYear.setValue("");
		txtUnloadWeight.setValue("");
		txtLoadWeight.setValue("");
		txtPurCost.setValue("");
		txtMonthlyFare.setValue("");
		txtMonthlyInstall.setValue("");
		txtChasisNo.setValue("");
		txtEngineNo.setValue("");
		txtCylinder.setValue("");
		txtCC.setValue("");
		txtColor.setValue("");
		txtNoSeat.setValue("");
		txtTyreSize.setValue("");
		txtNoOfTyre.setValue("");
		txtSupplierAdd.setValue("");
		txtSupplierPhone.setValue("");
		txtSupplierMobile.setValue("");
		txtDriverName.setValue("");
		txtDriverMobile.setValue("");
		
		cmbVehicleType.setValue(null);
		cmbManufacturer.setValue(null);
		cmbOwnership.setValue(null);
		cmbRegType.setValue(null);
		cmbSupplierName.setValue(null);
		cmbUnitCode.setValue(null);
		cmbSubUnitCode.setValue(null);
		cmbDriverCode.setValue(null);
		
		dFitnessDate.setValue(new java.util.Date());
		dFitnessExpDate.setValue(new java.util.Date());
		dInsuranceDate.setValue(new java.util.Date());
		dInsuranceExpDate.setValue(new java.util.Date());
		dtaxTokenDate.setValue(new java.util.Date());
		dtaxTokenExpDate.setValue(new java.util.Date());
	}
	
	private void btnIni(boolean t) 
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}
	
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("750px");
		setHeight("590px");
		
		// lblUnitCode
		lblVehicleType = new Label("Vehicle Type :");
		lblVehicleType.setImmediate(false);
		lblVehicleType.setWidth("-1px");
		lblVehicleType.setHeight("-1px");
		mainLayout.addComponent(lblVehicleType,"top:12.0px;left:44.0px;");
		
		// cmbVehicleType
		cmbVehicleType = new ComboBox();
		cmbVehicleType.setImmediate(true);
		cmbVehicleType.setWidth("120px");
		cmbVehicleType.setHeight("-1px");
		mainLayout.addComponent(cmbVehicleType, "top:12.0px;left:170.0px;");
		for (int i = 0; i < strVehicleType.length; i++) {
			cmbVehicleType.addItem(strVehicleType[i]);
        }

		// lblOwnerShip
		lblOwnerShip = new Label("Ownership Type :");
		lblOwnerShip.setImmediate(false);
		lblOwnerShip.setWidth("-1px");
		lblOwnerShip.setHeight("-1px");
		mainLayout.addComponent(lblOwnerShip,"top:37.0px;left:44.0px;");

		// cmbOwnership
		cmbOwnership = new ComboBox();
		cmbOwnership.setImmediate(true);
		cmbOwnership.setWidth("100px");
		cmbOwnership.setHeight("-1px");
		mainLayout.addComponent(cmbOwnership, "top:37.0px;left:170.0px;");
		for (int i = 0; i < strOwnerShip.length; i++) {
			cmbOwnership.addItem(strOwnerShip[i]);
        }

		// lblVehicleNo
		lblVehicleNo = new Label("Vehicle No :");
		lblVehicleNo.setImmediate(false);
		lblVehicleNo.setWidth("-1px");
		lblVehicleNo.setHeight("-1px");
		mainLayout.addComponent(lblVehicleNo,"top:64.0px;left:44.0px;");

		// txtVehicleNo
		txtVehicleNo = new TextField();
		txtVehicleNo.setImmediate(false);
		txtVehicleNo.setWidth("200px");
		txtVehicleNo.setHeight("-1px");
		mainLayout.addComponent(txtVehicleNo, "top:62.0px;left:170.0px;");

		// lblRegType
		lblRegType = new Label("Registration Type :");
		lblRegType.setImmediate(false);
		lblRegType.setWidth("-1px");
		lblRegType.setHeight("-1px");
		mainLayout.addComponent(lblRegType,"top:87.0px;left:44.0px;");

		// cmbRegType
		cmbRegType = new ComboBox();
		cmbRegType.setImmediate(false);
		cmbRegType.setWidth("100px");
		cmbRegType.setHeight("-1px");
		mainLayout.addComponent(cmbRegType, "top:88.0px;left:170.0px;");
		for (int i = 0; i < strRegType.length; i++)
			{cmbRegType.addItem(strRegType[i]);}

		// lblRegNo
		lblRegNo = new Label("Registration No :");
		lblRegNo.setImmediate(false);
		lblRegNo.setWidth("-1px");
		lblRegNo.setHeight("-1px");
		mainLayout.addComponent(lblRegNo,"top:114.0px;left:44.0px;");
		
		//
		btnRegNo = new NativeButton();
		btnRegNo.setCaption("");
		btnRegNo.setImmediate(true);
		btnRegNo.setWidth("33px");
		btnRegNo.setHeight("25px");
		btnRegNo.setIcon(new ThemeResource("../icons/add.png"));
		//mainLayout.addComponent(btnRegNo,"top:113.0px;left:330.0px;");
		
		// txtRegNo
		txtRegNo = new TextField();
		txtRegNo.setImmediate(false);
		txtRegNo.setWidth("200px");
		txtRegNo.setHeight("-1px");
		mainLayout.addComponent(txtRegNo, "top:113.0px;left:170.0px;");

		// lblAccNo
		lblAccNo = new Label("Account No :");
		lblAccNo.setImmediate(false);
		lblAccNo.setWidth("-1px");
		lblAccNo.setHeight("-1px");
		mainLayout.addComponent(lblAccNo,"top:139.0px;left:44.0px;");

		// txtAccNo
		txtAccNo = new TextField();
		txtAccNo.setImmediate(false);
		txtAccNo.setWidth("200px");
		txtAccNo.setHeight("-1px");
		mainLayout.addComponent(txtAccNo, "top:138.0px;left:170.0px;");

		// lblAddress
		lblAddress = new Label("Address :");
		lblAddress.setImmediate(false);
		lblAddress.setWidth("-1px");
		lblAddress.setHeight("-1px");
		mainLayout.addComponent(lblAddress,"top:165.0px;left:44.0px;");

		// txtAddress
		txtAddress = new TextField();
		txtAddress.setImmediate(false);
		txtAddress.setWidth("200px");
		txtAddress.setRows(1);
		txtAddress.setHeight("-1px");
		mainLayout.addComponent(txtAddress, "top:164.0px;left:170.0px;");

		// lblTinNo
		lblTinNo = new Label("TIN No. :");
		lblTinNo.setImmediate(false);
		lblTinNo.setWidth("-1px");
		lblTinNo.setHeight("-1px");
		mainLayout.addComponent(lblTinNo,"top:212.0px;left:44.0px;");

		// txtTinNo
		txtTinNo = new TextField();
		txtTinNo.setImmediate(true);
		txtTinNo.setWidth("170px");
		txtTinNo.setHeight("-1px");
		mainLayout.addComponent(txtTinNo, "top:208.0px;left:170.0px;");
				
		// btnTinNo
		btnTinNo = new NativeButton();
		btnTinNo.setCaption("");
		btnTinNo.setImmediate(true);
		btnTinNo.setWidth("33px");
		btnTinNo.setHeight("25px");
		btnTinNo.setIcon(new ThemeResource("../icons/add.png"));
		//mainLayout.addComponent(btnTinNo,"top:209.0px;left:330.0px;");

		// lblUnloadWeight
		lblUnloadWeight = new Label("Unladen Weight :");
		lblUnloadWeight.setImmediate(false);
		lblUnloadWeight.setWidth("-1px");
		lblUnloadWeight.setHeight("-1px");
		mainLayout.addComponent(lblUnloadWeight,"top:238.0px;left:44.0px;");

		// txtUnloadWeight
		txtUnloadWeight = new AmountField();
		txtUnloadWeight.setImmediate(false);
		txtUnloadWeight.setWidth("100px");
		txtUnloadWeight.setHeight("-1px");
		mainLayout.addComponent(txtUnloadWeight, "top:233.0px;left:170.0px;");

		Label UnloadKgs=new Label("Kgs");
		UnloadKgs.setImmediate(false);
		UnloadKgs.setWidth("-1px");
		UnloadKgs.setHeight("-1px");
		mainLayout.addComponent(UnloadKgs,"top:238.0px;left:275.0px;");
		
		// lblLoadWeight
		lblLoadWeight = new Label("Laden Weight :");
		lblLoadWeight.setImmediate(false);
		lblLoadWeight.setWidth("-1px");
		lblLoadWeight.setHeight("-1px");
		mainLayout.addComponent(lblLoadWeight,"top:263.0px;left:44.0px;");

		// txtLoadWeight
		txtLoadWeight = new AmountField();
		txtLoadWeight.setImmediate(false);
		txtLoadWeight.setWidth("100px");
		txtLoadWeight.setHeight("-1px");
		mainLayout.addComponent(txtLoadWeight, "top:258.0px;left:170.0px;");

		Label LoadKgs=new Label("Kgs");
		LoadKgs.setImmediate(false);
		LoadKgs.setWidth("-1px");
		LoadKgs.setHeight("-1px");
		mainLayout.addComponent(LoadKgs,"top:263.0px;left:275.0px;");
		
		// lblManufacturer
		lblManufacturer = new Label("Manufacturer Name :");
		lblManufacturer.setImmediate(true);
		lblManufacturer.setWidth("-1px");
		lblManufacturer.setHeight("-1px");
		mainLayout.addComponent(lblManufacturer,"top:288.0px;left:44.0px;");

		// cmbManufacturer
		cmbManufacturer = new ComboBox();
		cmbManufacturer.setImmediate(true);
		cmbManufacturer.setNewItemsAllowed(true);
		cmbManufacturer.setWidth("100px");
		cmbManufacturer.setHeight("-1px");
		mainLayout.addComponent(cmbManufacturer, "top:283.0px;left:170.0px;");
		for (int i = 0; i < strManufacturer.length; i++) {
			cmbManufacturer.addItem(strManufacturer[i]);
        }
		
		// lblManuYear
		lblManuYear = new Label("Manufacturing Year :");
		lblManuYear.setImmediate(false);
		lblManuYear.setWidth("-1px");
		lblManuYear.setHeight("-1px");
		mainLayout.addComponent(lblManuYear,"top:314.0px;left:44.0px;");
		
		//txtManuYear
		txtManuYear = new TextField("");
		txtManuYear.setImmediate(false);
		txtManuYear.setWidth("100px");
		txtManuYear.setHeight("-1px");
		mainLayout.addComponent(txtManuYear, "top:308.0px;left:170.0px;");
		
		// lblPurCost
		lblPurCost = new Label("Purchase Cost :");
		lblPurCost.setImmediate(false);
		lblPurCost.setWidth("-1px");
		lblPurCost.setHeight("-1px");
		mainLayout.addComponent(lblPurCost,"top:340.0px;left:44.0px;");
		
		//txtManuYear
		txtPurCost = new AmountCommaSeperator();
		txtPurCost.setImmediate(false);
		txtPurCost.setWidth("100px");
		txtPurCost.setHeight("-1px");
		mainLayout.addComponent(txtPurCost, "top:336.0px;left:170.0px;");
		
		Label PurCost=new Label("TK");
		PurCost.setImmediate(false);
		PurCost.setWidth("-1px");
		PurCost.setHeight("-1px");
		mainLayout.addComponent(PurCost,"top:341.0px;left:275.0px;");
		
		// lblMonthlyFare
		lblMonthlyFare = new Label("Monthly Fare :");
		lblMonthlyFare.setImmediate(false);
		lblMonthlyFare.setWidth("-1px");
		lblMonthlyFare.setHeight("-1px");
		mainLayout.addComponent(lblMonthlyFare,"top:367.0px;left:44.0px;");
		
		//txtMonthlyFare
		txtMonthlyFare = new AmountCommaSeperator();
		txtMonthlyFare.setImmediate(false);
		txtMonthlyFare.setWidth("100px");
		txtMonthlyFare.setHeight("-1px");
		mainLayout.addComponent(txtMonthlyFare, "top:362.0px;left:170.0px;");
		
		Label MonthFare=new Label("TK");
		MonthFare.setImmediate(false);
		MonthFare.setWidth("-1px");
		MonthFare.setHeight("-1px");
		mainLayout.addComponent(MonthFare,"top:366.0px;left:275.0px;");
		
		// lblMonthlyInstall
		lblMonthlyInstall = new Label("Monthly Installment :");
		lblMonthlyInstall.setImmediate(false);
		lblMonthlyInstall.setWidth("-1px");
		lblMonthlyInstall.setHeight("-1px");
		mainLayout.addComponent(lblMonthlyInstall,"top:391.0px;left:44.0px;");
		
		//txtMonthlyInstall
		txtMonthlyInstall = new AmountCommaSeperator();
		txtMonthlyInstall.setImmediate(false);
		txtMonthlyInstall.setWidth("100px");
		txtMonthlyInstall.setHeight("-1px");
		mainLayout.addComponent(txtMonthlyInstall, "top:388.0px;left:170.0px;");
		
		Label MothlyIsntall=new Label("TK");
		MothlyIsntall.setImmediate(false);
		MothlyIsntall.setWidth("-1px");
		MothlyIsntall.setHeight("-1px");
		mainLayout.addComponent(MothlyIsntall,"top:391.0px; left:275.0px;");
		
		// lblTaxTokenDate
		lblTaxTokenDate = new Label("Tax Token From :");
		lblTaxTokenDate.setImmediate(false);
		lblTaxTokenDate.setWidth("-1px");
		lblTaxTokenDate.setHeight("-1px");
		mainLayout.addComponent(lblTaxTokenDate,"top:418.0px; left:44.0px;");
		
		// dtaxTokenDate
		dtaxTokenDate = new PopupDateField();
		dtaxTokenDate.setImmediate(false);
		dtaxTokenDate.setWidth("105px");
		dtaxTokenDate.setHeight("-1px");
		dtaxTokenDate.setDateFormat("dd-MM-yyyy");
		dtaxTokenDate.setValue(new java.util.Date());
		dtaxTokenDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dtaxTokenDate, "top:414.0px; left:170.0px;");
		
		// btnTaxTokenDate
		btnTaxTokenDate = new NativeButton();
		btnTaxTokenDate.setCaption("");
		btnTaxTokenDate.setImmediate(true);
		btnTaxTokenDate.setWidth("33px");
		btnTaxTokenDate.setHeight("25px");
		btnTaxTokenDate.setIcon(new ThemeResource("../icons/add.png"));
		//mainLayout.addComponent(btnTaxTokenDate,"top:415.0px;left:295.0px;");
		
		// lblTaxTokenExpDate
		lblTaxTokenExpDate = new Label("To");
		lblTaxTokenExpDate.setImmediate(false);
		lblTaxTokenExpDate.setWidth("-1px");
		lblTaxTokenExpDate.setHeight("-1px");
		mainLayout.addComponent(lblTaxTokenExpDate,"top:418.0px; left:279.0px;");
		
		// dtaxTokenExpDate
		dtaxTokenExpDate = new PopupDateField();
		dtaxTokenExpDate.setImmediate(false);
		dtaxTokenExpDate.setWidth("105px");
		dtaxTokenExpDate.setHeight("-1px");
		dtaxTokenExpDate.setDateFormat("dd-MM-yyyy");
		dtaxTokenExpDate.setValue(new java.util.Date());
		dtaxTokenExpDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dtaxTokenExpDate, "top:414.0px; left:295.0px;");
		
		// lblFitnessDate
		lblFitnessDate = new Label("Fitness From :");
		lblFitnessDate.setImmediate(false);
		lblFitnessDate.setWidth("-1px");
		lblFitnessDate.setHeight("-1px");
		mainLayout.addComponent(lblFitnessDate,"top:441.0px; left:44.0px;");
				
		// dFitnessDate
		dFitnessDate = new PopupDateField();
		dFitnessDate.setImmediate(false);
		dFitnessDate.setWidth("105px");
		dFitnessDate.setHeight("-1px");
		dFitnessDate.setDateFormat("dd-MM-yyyy");
		dFitnessDate.setValue(new java.util.Date());
		dFitnessDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFitnessDate, "top:438.0px; left:170.0px;");
		
		// btnFitnessDate
		btnFitnessDate = new NativeButton();
		btnFitnessDate.setCaption("");
		btnFitnessDate.setImmediate(true);
		btnFitnessDate.setWidth("33px");
		btnFitnessDate.setHeight("25px");
		btnFitnessDate.setIcon(new ThemeResource("../icons/add.png"));
		//mainLayout.addComponent(btnFitnessDate,"top:463.0px;left:295.0px;");
		
		// lblFitnessExpDate
		lblFitnessExpDate = new Label("To");
		lblFitnessExpDate.setImmediate(false);
		lblFitnessExpDate.setWidth("-1px");
		lblFitnessExpDate.setHeight("-1px");
		mainLayout.addComponent(lblFitnessExpDate,"top:441.0px;left:279.0px;");
						
		// dFitnessDate
		dFitnessExpDate = new PopupDateField();
		dFitnessExpDate.setImmediate(false);
		dFitnessExpDate.setWidth("105px");
		dFitnessExpDate.setHeight("-1px");
		dFitnessExpDate.setDateFormat("dd-MM-yyyy");
		dFitnessExpDate.setValue(new java.util.Date());
		dFitnessExpDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFitnessExpDate, "top:438.0px;left:295.0px;");
		
		//lblInsuranceDate
		lblInsuranceDate=new Label("Insurance From :");
		lblInsuranceDate.setImmediate(false);
		lblInsuranceDate.setWidth("-1px");
		lblInsuranceDate.setHeight("-1px");
		mainLayout.addComponent(lblInsuranceDate,"top:464.0px; left:44.0px;");
		
		// dInsuranceDate
		dInsuranceDate = new PopupDateField();
		dInsuranceDate.setImmediate(false);
		dInsuranceDate.setWidth("105px");
		dInsuranceDate.setHeight("-1px");
		dInsuranceDate.setDateFormat("dd-MM-yyyy");
		dInsuranceDate.setValue(new java.util.Date());
		dInsuranceDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dInsuranceDate, "top:461.0px; left:170.0px;");
		
		//lblInsuranceExpiredDate
		lblInsuranceDate=new Label("To");
		lblInsuranceDate.setImmediate(false);
		lblInsuranceDate.setWidth("-1px");
		lblInsuranceDate.setHeight("-1px");
		mainLayout.addComponent(lblInsuranceDate,"top:464.0px;left:279.0px;");
		
		// dInsuranceExpDate
		dInsuranceExpDate = new PopupDateField();
		dInsuranceExpDate.setImmediate(false);
		dInsuranceExpDate.setWidth("105px");
		dInsuranceExpDate.setHeight("-1px");
		dInsuranceExpDate.setDateFormat("dd-MM-yyyy");
		dInsuranceExpDate.setValue(new java.util.Date());
		dInsuranceExpDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dInsuranceExpDate, "top:461.0px;left:295.0px;");
		
		//lblChesisNo
		lblChassisNo = new Label("Chassis No :");
		lblChassisNo.setImmediate(false);
		lblChassisNo.setWidth("-1px");
		lblChassisNo.setHeight("-1px");
		mainLayout.addComponent(lblChassisNo,"top:14.0px;left:420.0px;");
		
		//txtChasisNo
		txtChasisNo = new TextField();
		txtChasisNo.setImmediate(true);
		txtChasisNo.setWidth("120px");
		txtChasisNo.setHeight("-1px");
		mainLayout.addComponent(txtChasisNo, "top:12.0px;left:520.0px;");
		
		//lblEngineNo
		lblEngineNo = new Label("Engine No :");
		lblEngineNo.setImmediate(false);
		lblEngineNo.setWidth("-1px");
		lblEngineNo.setHeight("-1px");
		mainLayout.addComponent(lblEngineNo,"top:40.0px;left:420.0px;");
			
		//txtEngineNo
		txtEngineNo = new TextField();
		txtEngineNo.setImmediate(true);
		txtEngineNo.setWidth("120px");
		txtEngineNo.setHeight("-1px");
		mainLayout.addComponent(txtEngineNo, "top:37.0px;left:520.0px;");
		
		//lblCylinder
		lblCylinder = new Label("Cylinder :");
		lblCylinder.setImmediate(false);
		lblCylinder.setWidth("-1px");
		lblCylinder.setHeight("-1px");
		mainLayout.addComponent(lblCylinder,"top:65.0px;left:420.0px;");
				
		//txtCylinder
		txtCylinder = new TextField();
		txtCylinder.setImmediate(true);
		txtCylinder.setWidth("120px");
		txtCylinder.setHeight("-1px");
		mainLayout.addComponent(txtCylinder, "top:62.0px;left:520.0px;");
		
		//lblCC
		lblCC = new Label("Engine CC :");
		lblCC.setImmediate(false);
		lblCC.setWidth("-1px");
		lblCC.setHeight("-1px");
		mainLayout.addComponent(lblCC,"top:90.0px;left:420.0px;");
						
		//txtCC
		txtCC = new TextField();
		txtCC.setImmediate(true);
		txtCC.setWidth("120px");
		txtCC.setHeight("-1px");
		mainLayout.addComponent(txtCC, "top:87.0px;left:520.0px;");
		
		//lblColor
		lblColor = new Label("Color :");
		lblColor.setImmediate(false);
		lblColor.setWidth("-1px");
		lblColor.setHeight("-1px");
		mainLayout.addComponent(lblColor,"top:117.0px;left:420.0px;");
							
		//txtColor
		txtColor = new TextField();
		txtColor.setImmediate(true);
		txtColor.setWidth("120px");
		txtColor.setHeight("-1px");
		mainLayout.addComponent(txtColor, "top:112.0px;left:520.0px;");
		
		//lblNoSeat
		lblNoSeat = new Label("No of Seat :");
		lblNoSeat.setImmediate(false);
		lblNoSeat.setWidth("-1px");
		lblNoSeat.setHeight("-1px");
		mainLayout.addComponent(lblNoSeat,"top:140.0px;left:420.0px;");
		
		//txtNoSeat
		txtNoSeat = new TextField();
		txtNoSeat.setImmediate(true);
		txtNoSeat.setWidth("120px");
		txtNoSeat.setHeight("-1px");
		mainLayout.addComponent(txtNoSeat, "top:137.0px;left:520.0px;");
		
		//lblTyreSize
		lblTyreSize = new Label("Tyre Size :");
		lblTyreSize.setImmediate(false);
		lblTyreSize.setWidth("-1px");
		lblTyreSize.setHeight("-1px");
		mainLayout.addComponent(lblTyreSize,"top:165.0px;left:420.0px;");
				
		//txtTyreSize
		txtTyreSize = new TextField();
		txtTyreSize.setImmediate(true);
		txtTyreSize.setWidth("120px");
		txtTyreSize.setHeight("-1px");
		mainLayout.addComponent(txtTyreSize, "top:162.0px;left:520.0px;");
		
		//lblNoOfTyre
		lblNoOfTyre = new Label("No of Tyre :");
		lblNoOfTyre.setImmediate(false);
		lblNoOfTyre.setWidth("-1px");
		lblNoOfTyre.setHeight("-1px");
		mainLayout.addComponent(lblNoOfTyre,"top:190.0px;left:420.0px;");
						
		//txtNoOfTyre
		txtNoOfTyre = new TextField();
		txtNoOfTyre.setImmediate(true);
		txtNoOfTyre.setWidth("120px");
		txtNoOfTyre.setHeight("-1px");
		mainLayout.addComponent(txtNoOfTyre, "top:187.0px;left:520.0px;");
		
		Label lblSupInfo=new Label("SUPPLIER INFORMATION");
		lblSupInfo.setImmediate(false);
		lblSupInfo.setStyleName("styleLabel");
		lblSupInfo.setWidth("-1px");
		lblSupInfo.setHeight("-1px");
		mainLayout.addComponent(lblSupInfo,"top:220.0px;left:470.0px;");
		
		//lblSupplierName
		lblSupplierName = new Label("Supplier Name :");
		lblSupplierName.setImmediate(false);
		lblSupplierName.setWidth("-1px");
		lblSupplierName.setHeight("-1px");
		mainLayout.addComponent(lblSupplierName,"top:245.0px;left:420.0px;");
		
		// cmbSupplierAdd
		cmbSupplierName = new ComboBox();
		cmbSupplierName.setImmediate(true);
		cmbSupplierName.setWidth("180px");
		cmbSupplierName.setHeight("-1px");
		mainLayout.addComponent(cmbSupplierName, "top:242.0px;left:520.0px;");
		
		// btnEmployee
		btnSupplier=new NativeButton();
		btnSupplier.setImmediate(true);
		btnSupplier.setWidth("33px");
		btnSupplier.setHeight("24px");
		btnSupplier.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnSupplier,"top:242.0px;left:700.0px;");
		
		// lblSupplierAdd
		lblSupplierAdd = new Label("Address :");
		lblSupplierAdd.setImmediate(false);
		lblSupplierAdd.setWidth("-1px");
		lblSupplierAdd.setHeight("-1px");
		mainLayout.addComponent(lblSupplierAdd,"top:270.0px;left:420.0px;");
		
		//txtSupplierName
		txtSupplierAdd = new TextRead();
		txtSupplierAdd.setImmediate(true);
		txtSupplierAdd.setWidth("180px");
		txtSupplierAdd.setHeight("42px");
		mainLayout.addComponent(txtSupplierAdd, "top:268.0px;left:521.0px;");
		
		//lblSupplierPhone
		lblSupplierPhone = new Label("Phone :");
		lblSupplierPhone.setImmediate(false);
		lblSupplierPhone.setWidth("-1px");
		lblSupplierPhone.setHeight("-1px");
		mainLayout.addComponent(lblSupplierPhone,"top:315.0px;left:420.0px;");
												
		//txtSupplierPhone
		txtSupplierPhone = new TextRead();
		txtSupplierPhone.setImmediate(true);
		txtSupplierPhone.setWidth("150px");
		txtSupplierPhone.setHeight("21px");
		mainLayout.addComponent(txtSupplierPhone, "top:313.0px;left:521.0px;");
		
		//lblSupplierMobile
		lblSupplierMobile = new Label("Mobile :");
		lblSupplierMobile.setImmediate(false);
		lblSupplierMobile.setWidth("-1px");
		lblSupplierMobile.setHeight("-1px");
		mainLayout.addComponent(lblSupplierMobile,"top:340.0px;left:420.0px;");
														
		//txtSupplierMobile
		txtSupplierMobile = new TextRead();
		txtSupplierMobile.setImmediate(true);
		txtSupplierMobile.setWidth("150px");
		txtSupplierMobile.setHeight("21px");
		mainLayout.addComponent(txtSupplierMobile, "top:338.0px;left:521.0px;");
				
		//lblUnitCode
		lblUnitCode = new Label("Unit Code :");
		lblUnitCode.setImmediate(false);
		lblUnitCode.setWidth("-1px");
		lblUnitCode.setHeight("-1px");
//		mainLayout.addComponent(lblUnitCode,"top:365.0px;left:420.0px;");
																
		//cmbUnitCode
		cmbUnitCode = new ComboBox();
		cmbUnitCode.setImmediate(true);
		cmbUnitCode.setWidth("150px");
		cmbUnitCode.setHeight("-1px");
//		mainLayout.addComponent(cmbUnitCode, "top:362.0px;left:520.0px;");

		//lblSubUnitCode
		lblSubUnitCode = new Label("Sub-Unit Code :");
		lblSubUnitCode.setImmediate(false);
		lblSubUnitCode.setWidth("-1px");
		lblSubUnitCode.setHeight("-1px");
//		mainLayout.addComponent(lblSubUnitCode,"top:390.0px;left:420.0px;");
																		
		//cmbSubUnitCode
		cmbSubUnitCode = new ComboBox();
		cmbSubUnitCode.setImmediate(true);
		cmbSubUnitCode.setWidth("150px");
		cmbSubUnitCode.setHeight("-1px");
//		mainLayout.addComponent(cmbSubUnitCode, "top:387.0px;left:520.0px;");
		
		Label lblDriver=new Label("DRIVER INFORMATION");
		lblDriver.setImmediate(false);
		lblDriver.setStyleName("styleLabel");
		lblDriver.setWidth("-1px");
		lblDriver.setHeight("-1px");
		mainLayout.addComponent(lblDriver,"top:370.0px;left:470.0px;");
		
		//lblDriverCode
		lblDriverCode = new Label("Driver Code :");
		lblDriverCode.setImmediate(false);
		lblDriverCode.setWidth("-1px");
		lblDriverCode.setHeight("-1px");
		mainLayout.addComponent(lblDriverCode,"top:400.0px;left:420.0px;");
		
		//cmbDriverCode
		cmbDriverCode = new ComboBox();
		cmbDriverCode.setImmediate(true);
		cmbDriverCode.setNewItemsAllowed(true);
		cmbDriverCode.setWidth("130px");
		cmbDriverCode.setHeight("-1px");
		mainLayout.addComponent(cmbDriverCode, "top:397.0px;left:520.0px;");
		
		//btnEmployee
		btnEmployee=new NativeButton();
		btnEmployee.setImmediate(true);
		btnEmployee.setWidth("33px");
		btnEmployee.setHeight("24px");
		btnEmployee.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnEmployee,"top:397.0px;left:650.0px;");
		
		//lblDriverName
		lblDriverName = new Label("Driver Name :");
		lblDriverName.setImmediate(false);
		lblDriverName.setWidth("-1px");
		lblDriverName.setHeight("-1px");
		mainLayout.addComponent(lblDriverName,"top:427.0px;left:420.0px;");
																				
		//txtDriverName
		txtDriverName = new TextRead();
		txtDriverName.setImmediate(true);
		txtDriverName.setWidth("150px");
		txtDriverName.setHeight("21px");
		mainLayout.addComponent(txtDriverName, "top:424.0px;left:521.0px;");
		
		//lblDriverMobile
		lblDriverMobile = new Label("Driver Mobile :");
		lblDriverMobile.setImmediate(false);
		lblDriverMobile.setWidth("-1px");
		lblDriverMobile.setHeight("-1px");
		mainLayout.addComponent(lblDriverMobile,"top:452.0px;left:420.0px;");
																				
		//txtDriverMobile
		txtDriverMobile = new TextRead();
		txtDriverMobile.setImmediate(true);
		txtDriverMobile.setWidth("150px");
		txtDriverMobile.setHeight("21px");
		mainLayout.addComponent(txtDriverMobile, "top:448.0px;left:521.0px;");
		
		mainLayout.addComponent(cButton, "top:505.0px;left:110.0px;");
		
		return mainLayout;
	}
}
