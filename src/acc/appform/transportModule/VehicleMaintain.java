package acc.appform.transportModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FileUpload;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImageUpload;/*
import com.common.share.ImmediateUploadBill;
import com.common.share.ImmediateUploadExampleBirth;*/
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

public class VehicleMaintain extends Window 
{
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	
	private Table commonTable = new Table();
	
	private Label lblUnitCode;
	private Label lblSubUnitCode;
	private Label lblUnitName;
	private Label lblSubUnitName;
	private Label lblEntryDate;
	private Label lblMaintainence;
	private Label lblVehicleNo;
	
	private ComboBox cmbUnitCode;
	private ComboBox cmbSubUnitCode;
	private ComboBox cmbVehicleNo;
	
	private TextRead txtUnitName;
	private TextRead txtSubUnitName;
	
	private ImageUpload billFile;
	
	private PopupDateField dEntryDate;

	private static final String[] strType = new String[]{"Repair","Replacement"};
	
	private CheckBox selectRegu = new CheckBox("Regular");
	private CheckBox selectMain = new CheckBox("Maintenance");
	private CheckBox selectService = new CheckBox("Servicing");
	
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "","","","Exit");
	
//	public ImmediateUploadBill btnBill;
	public Button btnBillPreview;
	
	//Common variables
	private ArrayList<Label> lblSL = new ArrayList<Label>();
	private ArrayList<TextField> lblExpHead = new ArrayList<TextField>();
	private ArrayList<TextField> txtQty = new ArrayList<TextField>();
	private ArrayList<TextField> txtUnit = new ArrayList<TextField>();
	private ArrayList<AmountField> txtRate = new ArrayList<AmountField>();
	private ArrayList<AmountCommaSeperator> txtAmount = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextField> txtSupplier = new ArrayList<TextField>();
	private ArrayList<TextField> txtRemarks = new ArrayList<TextField>();
	private ArrayList<TextField> txtBillNo = new ArrayList<TextField>();
	private ArrayList<TextField> fileAttachment = new ArrayList<TextField>();
	
	//Common variables in two table
	private ArrayList<AmountField> txtTotalAmountBoth = new ArrayList<AmountField>();
	private ArrayList<TextField> txtSupplierBoth = new ArrayList<TextField>();
	
	//table1 different variables
	private ArrayList<AmountField> txtEndMetRead = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtRunMilage = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtCostPerMile = new ArrayList<AmountField>();
	
	//table2 different variables
	private ArrayList<ComboBox> cmbType = new ArrayList<ComboBox>();
	private ArrayList<PopupDateField> lastRepDate = new ArrayList<PopupDateField>();
	
	//table3 different variables
	private ArrayList<PopupDateField> lastSerDate = new ArrayList<PopupDateField>();
		
	ArrayList<Component> allComp = new ArrayList<Component>();
	
	private Boolean isUpdate= false;
	private Boolean isFirst=false;
	private Boolean isSecond=false;
	private Boolean isThird=false;
	private Boolean isFind=false;
	
	//Find Properties
	private TextRead findMaintenID = new TextRead();
	private TextRead mainType = new TextRead(); 
	
	String autoMaintenId;
	String MaintenType="";
	public String billPdf = null;
	public String conFilePathTmp = "";
	public String billFilePathTmp = "";
	String billImageLoc = "0" ;
	
	private DecimalFormat df=new DecimalFormat("#0.00");
	private DecimalFormat df1=new DecimalFormat("#0");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateF = new SimpleDateFormat("dd-MM-yyyy");
	
	double RateOne=0.0;
	double QuantityOne=0.0;
		
	public VehicleMaintain(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("VEHICLE MAINTAINANCE :: "+sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainlayout();
		setContent(mainLayout);
		
		btnIni(true);
		componentIni(false);
		
		loadVehicleRegistration();

		firstTableInitialise();
		
		focusEnter();
		setBtnAction();
		
		authencationCheck();
		selectRegu.setValue(true);
	}
	
	private void authencationCheck()
	{
		if(sessionBean.isSubmitable())
		{
			cButton.btnSave.setVisible(true);
		}
		else
		{
			cButton.btnSave.setVisible(false);
		}
		if(sessionBean.isUpdateable())
		{
			cButton.btnEdit.setVisible(true);
		}
		else
		{
			cButton.btnEdit.setVisible(false);
		}
		if(sessionBean.isDeleteable())
		{
			cButton.btnDelete.setVisible(true);
		}
		else
		{
			cButton.btnDelete.setVisible(false);
		}
	}
	
	private void setBtnAction()
	{
		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(checkSelectValidation())
				{
					txtClear();
					componentIni(true);
					btnIni(false);
					chkIni(true);
				}
			}
		});
		
		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				checkValidation();
			}
		});
		
		selectRegu.addListener(new Listener()
		{	
			public void componentEvent(Event event)
			{
				if(selectRegu.getValue().toString().equalsIgnoreCase("true"))
				{
					selectMain.setValue(false);
					selectService.setValue(false);
					
					if(isFind)
						{txtClear();}
					
					setFirstTableColum(false);
					isFirst=true;
//					focusEnterFirst();
				}
			}
		});
		
		selectMain.addListener(new Listener()
		{	
			public void componentEvent(Event event)
			{
				if(selectMain.getValue().toString().equalsIgnoreCase("true"))
				{					
					selectRegu.setValue(false);
					selectService.setValue(false);
					
					if(isFind)
					{txtClear();}
					
					setSecondTableColum(false);
					isSecond=true;
//					focusEnterSecond();
				}
			}
		});
		
		selectService.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(selectService.getValue().toString().equalsIgnoreCase("true"))
				{					
					selectRegu.setValue(false);
					selectMain.setValue(false);
					
					if(isFind)
					{txtClear();}

					setThirdTableColum(false);
					isThird=true;
//					focusEnterThird();
				}
			}
		});
		
		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbVehicleNo.getValue()!=null)
				{
					isUpdate=true;
					isFind=false;
					componentIni(true);
					btnIni(false);
					chkIni(true);
				}
				else
				{
					showNotification("Warning","There are nothing to edit");
				}
			}
		});
		
		cButton.btnRefresh.addListener(new ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				isUpdate=false;
				componentIni(false);
				btnIni(true);
				txtClear();
			}
		});
		
		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				findButtonEvent();
			}
		});
		
		cButton.btnExit.addListener(new ClickListener()
		{	
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}
	
	private void chkIni(boolean b)
	{
		if(selectRegu.getValue().toString().equalsIgnoreCase("true"))
		{
			selectRegu.setReadOnly(b);
			selectMain.setEnabled(!b);
			selectService.setEnabled(!b);
		}
		if(selectMain.getValue().toString().equalsIgnoreCase("true"))
		{
			selectMain.setReadOnly(b);
			selectRegu.setEnabled(!b);
			selectService.setEnabled(!b);
		}
		if(selectService.getValue().toString().equalsIgnoreCase("true"))
		{
			selectService.setReadOnly(b);
			selectRegu.setEnabled(!b);
			selectMain.setEnabled(!b);
		}
	}
	
	private boolean checkSelectValidation()
	{
		if(!selectRegu.getValue().toString().equalsIgnoreCase("true") && !selectMain.getValue().toString().equalsIgnoreCase("true") && !selectService.getValue().toString().equalsIgnoreCase("true"))
		{
			showNotification("Warning","Select Maintenance Type");
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private void checkValidation()
	{
		if(checkSelectValidation())
		{
			if(cmbVehicleNo.getValue()!=null)
				{
					if(!lblExpHead.get(0).getValue().toString().isEmpty() && !txtQty.get(0).getValue().toString().isEmpty() && !txtUnit.get(0).getValue().toString().isEmpty() 
						&& !txtRate.get(0).getValue().toString().isEmpty() && !txtBillNo.get(0).getValue().toString().isEmpty() && !txtSupplier.get(0).getValue().toString().isEmpty())
					{
						saveButtonEvent();
					}
					else
					{
						showNotification("Warning","Provide all information in the table");
					}
				}
				else
				{
					showNotification("Warning","Select Vehicle Registration Number");
				}			
		}
	}
	
	private void saveButtonEvent()
	{
		Transaction tx;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		
		autoMaintenId = (String) findMaintenID.getValue();
		System.out.println("Find Id: "+autoMaintenId);
		
		try 
		{
			if(isUpdate)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType== ButtonType.YES)
						{
						   if(deleteData())
						   {
							   insertData();
						   }
						}
						isUpdate=false;
						isFind=false;
					}
				});
			}
			else
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							insertData();
						}
					}
				});
			}			
		}
		catch (Exception e)
		{
			this.getParent().showNotification("Error", e.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	
	private void insertData()
	{
		Transaction tx;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		
		try
		{
			if(!isUpdate)
			{
				autoMaintenId = autoId();
			}
			else
			{
				autoMaintenId = (String) findMaintenID.getValue();
			}
			
			System.out.println("Find Update id: "+autoMaintenId);
			
			//Form Save
			String sql = " Insert into tbVehicleMainten values ( "+
					" '"+autoMaintenId+"', " +
					" '0', " +
					" '0', " +
					" '"+dateFormat.format(dEntryDate.getValue())+"', " +
					" '"+cmbVehicleNo.getValue().toString().replace("#", "")+"', " +
					" '"+" "+"', " +
					" '"+MaintenType+"', " +
					" '"+sessionBean.getUserId()+"', " +
					" '"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP ) ";
			
			System.out.println("Insert Form Query: "+sql);
			session.createSQLQuery(sql).executeUpdate();
			
			//For Regular
			if(MaintenType=="Regular")
			{
				for(int i = 0;i<lblSL.size();i++)
				{
					if(!lblExpHead.get(i).getValue().toString().isEmpty() && !txtAmount.get(i).getValue().toString().isEmpty())
					{
						String query = "Insert into tbVehicleMaintenTable values " +
							"('"+autoMaintenId+"'," +
							" '"+lblExpHead.get(i).getValue().toString().trim()+"'," +
							" '"+txtQty.get(i).getValue().toString().trim()+"'," +
							" '"+" "+"'," +
							" '"+txtUnit.get(i).getValue().toString().trim()+"'," +
							" '"+txtRate.get(i).getValue().toString().trim()+"'," +
							" '"+txtAmount.get(i).getValue().toString().trim()+"'," +
							" '"+txtEndMetRead.get(i).getValue().toString().trim()+"'," +
							" '"+txtRunMilage.get(i).getValue().toString().trim()+"'," +
							" '"+txtCostPerMile.get(i).getValue().toString().trim()+"'," +
							" '"+txtBillNo.get(i).getValue().toString().trim()+"'," +
							" '"+txtSupplier.get(i).getValue().toString().trim()+"'," +
							" '"+" "+"'," +
							" '"+" "+"'," +
							" '"+" "+"'," +
							" '"+" "+"'," +
							" '"+txtRemarks.get(i).getValue().toString().trim()+"'," +
							" '"+sessionBean.getUserName()+"'," +
							" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP )";
						System.out.println("Regular:"+query);
						session.createSQLQuery(query).executeUpdate();
					}
				}
			}
			
			//For Maintenance
			if(MaintenType=="Maintenance")
			{
				for(int i = 0;i<lblSL.size();i++)
				{
					if(!lblExpHead.get(i).getValue().toString().isEmpty() && !txtAmount.get(i).getValue().toString().isEmpty())
					{
						String query = "Insert into tbVehicleMaintenTable values " +
							"('"+autoMaintenId+"'," +
							" '"+lblExpHead.get(i).getValue().toString().trim()+"'," +
							" '"+txtQty.get(i).getValue().toString().trim()+"'," +
							" '"+cmbType.get(i).getValue().toString()+"'," +
							" '"+txtUnit.get(i).getValue().toString().trim()+"'," +
							" '"+txtRate.get(i).getValue().toString().trim()+"'," +
							" '"+txtAmount.get(i).getValue().toString().trim()+"'," +
							" '"+" "+"'," +
							" '"+" "+"'," +
							" '"+" "+"'," +
							" '"+txtBillNo.get(i).getValue().toString().trim()+"'," +
							" '"+txtSupplier.get(i).getValue().toString().trim()+"'," +
							" '"+dateFormat.format(lastRepDate.get(i).getValue())+"'," +
							" '"+" "+"'," +
							" '"+txtTotalAmountBoth.get(i).getValue().toString()+"'," +
							" '"+txtSupplierBoth.get(i).getValue().toString()+"'," +
							" '"+txtRemarks.get(i).getValue().toString().trim()+"'," +
							" '"+sessionBean.getUserName()+"'," +
							" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP )";
						System.out.println("Mainten:"+query);
						session.createSQLQuery(query).executeUpdate();
					}
				}
			}
			
			//For S
			if(MaintenType=="Servicing")
			{
				for(int i = 0;i<lblSL.size();i++)
				{
					if(!lblExpHead.get(i).getValue().toString().isEmpty() && !txtAmount.get(i).getValue().toString().isEmpty())
					{
						String query = "Insert into tbVehicleMaintenTable values " +
							"('"+autoMaintenId+"'," +
							" '"+lblExpHead.get(i).getValue().toString().trim()+"'," +
							" '"+txtQty.get(i).getValue().toString().trim()+"'," +
							" '"+" "+"'," +
							" '"+txtUnit.get(i).getValue().toString().trim()+"'," +
							" '"+txtRate.get(i).getValue().toString().trim()+"'," +
							" '"+txtAmount.get(i).getValue().toString().trim()+"'," +
							" '"+" "+"'," +
							" '"+" "+"'," +
							" '"+" "+"'," +
							" '"+txtBillNo.get(i).getValue().toString().trim()+"'," +
							" '"+txtSupplier.get(i).getValue().toString().trim()+"'," +
							" '"+" "+"'," +
							" '"+dateFormat.format(lastSerDate.get(i).getValue())+"'," +
							" '"+txtTotalAmountBoth.get(i).getValue().toString()+"'," +
							" '"+txtSupplierBoth.get(i).getValue().toString()+"'," +
							" '"+txtRemarks.get(i).getValue().toString().trim()+"'," +
							" '"+sessionBean.getUserName()+"'," +
							" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP )";
						System.out.println("Mainten:"+query);
						session.createSQLQuery(query).executeUpdate();
					}
				}
			}
			
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			componentIni(false);
			btnIni(true);
			txtClear();
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
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

			String query = " Select cast(isnull(max(cast(replace(maintenId, '', '')as int))+1, 1)as varchar) from tbVehicleMainten ";
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

	private boolean deleteData()
	{
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		
		autoMaintenId = (String) findMaintenID.getValue();
		System.out.println("Find Update id: "+autoMaintenId);
		
		try
		{	
			String delFormSql = " delete from tbVehicleMainten where maintenId='"+autoMaintenId+"' ";
			String delTablesql = " delete from tbVehicleMaintenTable where maintenId='"+autoMaintenId+"' ";
			
			session.createSQLQuery(delFormSql).executeUpdate();
			session.createSQLQuery(delTablesql).executeUpdate();
			
			System.out.println(delTablesql);
			System.out.println(delFormSql);

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
	
	//Find Button Action
	private void findButtonEvent()
	{
		Window win=new VehicleMaintenFind(sessionBean,findMaintenID,"VehicleMainTen",mainType);
		isFind=true;
			win.addListener(new Window.CloseListener() 
			{
				public void windowClose(CloseEvent e) 
				{
					if(mainType.getValue()!="")
					{
						if(mainType.getValue()=="Regular")
						{
							selectRegu.setValue(true);
							
							txtClear();
							setFirstTableColum(false);
							isFirst=true;
//							focusEnterFirst();
						}
						
						if(mainType.getValue()=="Maintenance")
						{
							selectMain.setValue(true);
														
							txtClear();
							setSecondTableColum(false);
							isSecond=true;
//							focusEnterSecond();
						}
						
						if(mainType.getValue()=="Servicing")
						{
							selectService.setValue(true);
							
							txtClear();
							setThirdTableColum(false);
							isThird=true;
//							focusEnterThird();
						}
					}
					if(findMaintenID.getValue().toString().length()>0)
					{
						txtClear();
						findInitialise(findMaintenID.getValue().toString());
					}
				}
			});
			this.getParent().addWindow(win);
	}
	
	//Find Initialize
	private void findInitialise(String findIFD) 
	{
		System.out.println("FindId: "+findIFD);
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List led = session.createSQLQuery(" select * from vwVehicleMaintenance where maintenId = '"+findIFD+"' ").list();
			
				int i = 0;
				for (Iterator iter = led.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					if(lblSL.size()-1==i)
					{
						firstTableRowAdd(i+1);
					}
			
					if(i==0)
					{
						dEntryDate.setValue(element[1]);
						cmbVehicleNo.setValue(element[27]+"#");
					}
					
					//Common
					if(!element[4].toString().isEmpty())
					{
						lblExpHead.get(i).setValue(element[4].toString());
					}
			
					if(!element[5].toString().isEmpty())
					{
						txtQty.get(i).setValue(df.format(Double.parseDouble(element[5].toString())));
					}
				
					if(!element[6].toString().isEmpty())
					{
						txtUnit.get(i).setValue(element[6].toString());
					}
				
					if(!element[7].toString().isEmpty())
					{
						txtRate.get(i).setValue(df.format(Double.parseDouble(element[7].toString())));
					}
				
					if(!element[8].toString().isEmpty())
					{
						txtAmount.get(i).setValue(df.format(Double.parseDouble(element[8].toString())));
					}
					
					if(!element[12].toString().isEmpty())
					{
						txtBillNo.get(i).setValue(element[12].toString());
					}
					
					if(!element[13].toString().isEmpty())
					{
						txtSupplier.get(i).setValue(element[13].toString());
					}

					if(!element[18].toString().isEmpty())
					{
						txtRemarks.get(i).setValue(element[18].toString());
					}

					if(MaintenType=="Regular")
					{
						System.out.println("Mainten Regular");
						if(!element[9].toString().isEmpty())
						{
							txtEndMetRead.get(i).setValue(df1.format(Double.parseDouble(element[9].toString())));
						}
						
						if(!element[10].toString().isEmpty())
						{
							txtRunMilage.get(i).setValue(df1.format(Double.parseDouble(element[10].toString())));
						}
						
						if(!element[11].toString().isEmpty())
						{
							txtCostPerMile.get(i).setValue(df.format(Double.parseDouble(element[11].toString())));
						}
					}
					
					else if(MaintenType=="Maintenance")
					{
						System.out.println("Mainten Maintenance");
						if(!element[24].toString().isEmpty())
						{
							cmbType.get(i).setValue(element[24].toString());
						}
						System.out.println("MaintenType: "+element[24]);
						if(!element[14].toString().isEmpty())
						{
							lastRepDate.get(i).setValue(element[14]);
						}
						
						if(!element[16].toString().isEmpty())
						{
							txtTotalAmountBoth.get(i).setValue(df.format(element[16]));
						}
						
						if(!element[17].toString().isEmpty())
						{
							txtSupplierBoth.get(i).setValue(element[17]);
						}
					}
					
					else if(MaintenType=="Servicing")
					{
						System.out.println("Mainten Servicing");
						if(!element[15].toString().isEmpty())
						{
							lastSerDate.get(i).setValue(element[15]);
						}
						
						if(!element[16].toString().isEmpty())
						{
							txtTotalAmountBoth.get(i).setValue(df.format(element[16]));
						}
						
						if(!element[17].toString().isEmpty())
						{
							txtSupplierBoth.get(i).setValue(element[17]);
						}
					}
				i++;
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}	
	
	private void loadVehicleRegistration()
	{
		cmbVehicleNo.removeAllItems();		
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		
			tx = session.beginTransaction();
			
			String query=" SELECT vehicleId,regNumber from tbVehicleInfo order by autoId ";
			
			System.out.println("Vehicle Query: "+query);

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbVehicleNo.addItem(element[0]+"#");
				cmbVehicleNo.setItemCaption(element[0]+"#", element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.print("Hi"+ex);
		}
	}
	
	private void firstTableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			firstTableRowAdd(i);
		}
	}
	
	private void firstTableRowAdd(final int ar)
	{
		lblSL.add(ar,new Label());
		lblSL.get(ar).setWidth("100%");
		lblSL.get(ar).setHeight("21px");
		lblSL.get(ar).setValue(ar+1);

		lblExpHead.add(ar, new TextField());
		lblExpHead.get(ar).setWidth("100%");

		txtQty.add(ar,new AmountField());
		txtQty.get(ar).setWidth("100%");
		txtQty.get(ar).setImmediate(true);
		
		cmbType.add(ar,new ComboBox());
		cmbType.get(ar).setWidth("100%");
		for (int i = 0; i < strType.length; i++)
		{
			cmbType.get(ar).addItem(strType[i].toString());
			cmbType.get(ar).setItemCaption(strType[i].toString(), strType[i].toString());
        }
		
		txtUnit.add(ar, new TextField());
		txtUnit.get(ar).setWidth("100%");

		txtRate.add(ar, new AmountField());
		txtRate.get(ar).setWidth("100%");
		txtRate.get(ar).setImmediate(true);

		txtAmount.add(ar, new AmountCommaSeperator());
		txtAmount.get(ar).setWidth("100%");
		txtAmount.get(ar).setImmediate(true);
		
		txtEndMetRead.add(ar, new AmountField());
		txtEndMetRead.get(ar).setWidth("100%");
		
		txtRunMilage.add(ar, new AmountField());
		txtRunMilage.get(ar).setWidth("100%");
		
		txtCostPerMile.add(ar, new AmountField());
		txtCostPerMile.get(ar).setWidth("100%");
		
		txtBillNo.add(ar, new TextField());
		txtBillNo.get(ar).setWidth("100%");
				
		txtSupplier.add(ar, new TextField());
		txtSupplier.get(ar).setWidth("100%");
		
		lastRepDate.add(ar, new PopupDateField());
		lastRepDate.get(ar).setWidth("100%");
		lastRepDate.get(ar).setValue(new java.util.Date());
		lastRepDate.get(ar).setDateFormat("dd/MM/yy");
		lastRepDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		
		lastSerDate.add(ar, new PopupDateField());
		lastSerDate.get(ar).setWidth("100%");
		lastSerDate.get(ar).setValue(new java.util.Date());
		lastSerDate.get(ar).setDateFormat("dd/MM/yy");
		lastSerDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		
		txtTotalAmountBoth.add(ar, new AmountField());
		txtTotalAmountBoth.get(ar).setWidth("100%");
		
		txtSupplierBoth.add(ar, new TextField());
		txtSupplierBoth.get(ar).setWidth("100%");
		
		txtRemarks.add(ar, new TextField());
		txtRemarks.get(ar).setWidth("100%");

		txtRate.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtRate.get(ar).getValue().toString().isEmpty() && !isFind)
				{
					double Qty =Double.parseDouble(txtQty.get(ar).getValue().toString());
					double Rate =Double.parseDouble(txtRate.get(ar).getValue().toString());
					
					txtAmount.get(ar).setValue(df.format(Qty*Rate));
				}
			}
		});
		
		txtQty.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtQty.get(ar).getValue().toString().isEmpty() && !isFind)
				{
					double Qty =Double.parseDouble(txtRate.get(ar).getValue().toString());
					double Rate =Double.parseDouble(txtQty.get(ar).getValue().toString());
					
					txtAmount.get(ar).setValue(df.format(Qty*Rate));
				}
			}
		});
		
		txtRunMilage.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtRunMilage.get(ar).getValue().toString().isEmpty())// && !isFind)
				{
					double Amount =Double.parseDouble(txtAmount.get(ar).getValue().toString());
					double Milage =Double.parseDouble(txtRunMilage.get(ar).getValue().toString());
					
					txtCostPerMile.get(ar).setValue(df.format(Amount/Milage));
				}
			}
		});

		commonTable.addItem(new Object[]{lblSL.get(ar),lblExpHead.get(ar),txtQty.get(ar),cmbType.get(ar),txtUnit.get(ar),txtRate.get(ar),txtAmount.get(ar),txtEndMetRead.get(ar),txtRunMilage.get(ar),txtCostPerMile.get(ar),txtBillNo.get(ar),txtSupplier.get(ar),lastRepDate.get(ar),lastSerDate.get(ar),txtTotalAmountBoth.get(ar),txtSupplierBoth.get(ar),txtRemarks.get(ar)},ar);
	}
	
	private void setFirstTableColum(boolean t)
	{
		MaintenType="Regular";
		
		commonTable.setColumnCollapsed("SL", t);
		commonTable.setColumnCollapsed("Expense Head", t);
		commonTable.setColumnWidth("Expense Head", 140);
		commonTable.setColumnCollapsed("Qty", t);
		commonTable.setColumnCollapsed("Unit", t);
		commonTable.setColumnCollapsed("Rate", t);
		commonTable.setColumnCollapsed("Amount", t);
		commonTable.setColumnCollapsed("End Meter Reading", t);
		commonTable.setColumnCollapsed("Running Milage", t);
		commonTable.setColumnCollapsed("Cost Per Mile", t);
		commonTable.setColumnCollapsed("Bill No", t);
		commonTable.setColumnCollapsed("Supplier", t);
		commonTable.setColumnCollapsed("Remarks", t);
		commonTable.setColumnWidth("Remarks", 90);

			commonTable.setColumnCollapsed("Type", !t);
			commonTable.setColumnCollapsed("Last Rep. Date", !t);
			commonTable.setColumnCollapsed("Last Servicing", !t);
			commonTable.setColumnCollapsed("Last Supplier", !t);
			commonTable.setColumnCollapsed("L. Amount", !t);
	}
	private void setSecondTableColum(boolean t)
	{
		MaintenType="Maintenance";
		commonTable.setColumnCollapsed("SL", t);
		commonTable.setColumnCollapsed("Expense Head", t);
		commonTable.setColumnWidth("Expense Head", 120);
		commonTable.setColumnCollapsed("Qty", t);
		commonTable.setColumnCollapsed("Type", t);
		commonTable.setColumnCollapsed("Unit", t);
		commonTable.setColumnCollapsed("Rate", t);
		commonTable.setColumnCollapsed("Amount", t);
		commonTable.setColumnCollapsed("Last Rep. Date", t);
		commonTable.setColumnCollapsed("Last Supplier", t);
		commonTable.setColumnWidth("Last Supplier", 80);
		commonTable.setColumnCollapsed("L. Amount", t);
		commonTable.setColumnCollapsed("Bill No", t);
		commonTable.setColumnWidth("Bill No", 70);
		commonTable.setColumnCollapsed("Supplier", t);
		commonTable.setColumnWidth("Supplier", 90);
		commonTable.setColumnCollapsed("Remarks", t);
		commonTable.setColumnWidth("Remarks", 80);

			commonTable.setColumnCollapsed("Last Servicing", !t);
			commonTable.setColumnCollapsed("End Meter Reading", !t);
			commonTable.setColumnCollapsed("Running Milage", !t);
			commonTable.setColumnCollapsed("Cost Per Mile", !t);
	}
	private void setThirdTableColum(boolean t)
	{
		MaintenType="Servicing";
		commonTable.setColumnCollapsed("SL", t);
		commonTable.setColumnCollapsed("Expense Head", t);
		commonTable.setColumnWidth("Expense Head", 140);
		commonTable.setColumnCollapsed("Qty", t);
		commonTable.setColumnCollapsed("Unit", t);
		commonTable.setColumnCollapsed("Rate", t);
		commonTable.setColumnCollapsed("Amount", t);
		commonTable.setColumnCollapsed("Last Supplier", t);
		commonTable.setColumnWidth("Last Supplier", 95);
		commonTable.setColumnCollapsed("Last Servicing", t);
		commonTable.setColumnCollapsed("L. Amount", t);
		commonTable.setColumnCollapsed("Bill No", t);
		commonTable.setColumnCollapsed("Supplier", t);
		commonTable.setColumnWidth("Supplier", 95);
		commonTable.setColumnCollapsed("Remarks", t);

			commonTable.setColumnCollapsed("Last Rep. Date", !t);
			commonTable.setColumnCollapsed("Type", !t);
			commonTable.setColumnCollapsed("End Meter Reading", !t);
			commonTable.setColumnCollapsed("Running Milage", !t);
			commonTable.setColumnCollapsed("Cost Per Mile", !t);
	}
	
	private void focusEnter()
	{
		allComp.add(cmbVehicleNo);
		allComp.add(dEntryDate);
		
		for(int i=0;i<lblSL.size();i++)
		{
			allComp.add(lblExpHead.get(i));
			allComp.add(txtQty.get(i));
			allComp.add(cmbType.get(i));
			allComp.add(txtUnit.get(i));
			
			allComp.add(txtRate.get(i));
			allComp.add(txtEndMetRead.get(i));
			allComp.add(txtRunMilage.get(i));
			allComp.add(txtBillNo.get(i));
			allComp.add(txtSupplier.get(i));
			
			allComp.add(lastRepDate.get(i));
			allComp.add(lastSerDate.get(i));
			allComp.add(txtSupplierBoth.get(i));
			
			allComp.add(txtRemarks.get(i));
		}
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
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
	
	private void componentIni(boolean t)
	{
		cmbVehicleNo.setEnabled(t);
		dEntryDate.setEnabled(t);
		commonTable.setEnabled(t);
		commonTable.setEnabled(t);
		commonTable.setEnabled(t);
		btnBillPreview.setEnabled(t);
		
//		if()
		selectRegu.setReadOnly(false);
		selectMain.setReadOnly(false);
		selectService.setReadOnly(false);
	}
	
	private void txtClear()
	{
		cmbVehicleNo.setValue(null);
		dEntryDate.setValue(new java.util.Date());
		selectRegu.setEnabled(true);
		selectMain.setEnabled(true);
		selectService.setEnabled(true);
		
		for (int i = 0; i < lblSL.size(); i++)
		{
			lblExpHead.get(i).setValue("");
			txtQty.get(i).setValue("");
			txtUnit.get(i).setValue("");
			txtRate.get(i).setValue("");
			txtAmount.get(i).setValue("");
			txtEndMetRead.get(i).setValue("");
			txtRunMilage.get(i).setValue("");
			txtCostPerMile.get(i).setValue("");
			txtBillNo.get(i).setValue("");
			txtSupplier.get(i).setValue("");
			txtSupplierBoth.get(i).setValue("");
			txtTotalAmountBoth.get(i).setValue("");
			lastRepDate.get(i).setValue(new java.util.Date());
			lastSerDate.get(i).setValue(new java.util.Date());
			cmbType.get(i).setValue(null);
			txtRemarks.get(i).setValue("");
		}
	}
	
	private AbsoluteLayout buildMainlayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("1100px");
		setHeight("520px");
		
		lblVehicleNo = new Label("Registration No :");
		lblVehicleNo.setImmediate(false);
		lblVehicleNo.setWidth("100px");
		lblVehicleNo.setHeight("-1px");
		mainLayout.addComponent(lblVehicleNo, "top:20px; left:30px;");
		
		cmbVehicleNo = new ComboBox();
		cmbVehicleNo.setImmediate(false);
		cmbVehicleNo.setWidth("220px");
		cmbVehicleNo.setHeight("-1px");
		mainLayout.addComponent(cmbVehicleNo, "top:18px; left:145px;");
		
		lblEntryDate = new Label("Date :");
		lblEntryDate.setImmediate(false);
		lblEntryDate.setWidth("100px");
		lblEntryDate.setHeight("-1px");
		mainLayout.addComponent(lblEntryDate, "top:45px; left:30px;");
		
		dEntryDate = new PopupDateField();
		dEntryDate.setImmediate(false);
		dEntryDate.setWidth("120px");
		dEntryDate.setDateFormat("dd-MM-yyyy");
		dEntryDate.setValue(new java.util.Date());
		dEntryDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dEntryDate.setHeight("-1px");
		mainLayout.addComponent(dEntryDate, "top:43px; left:145px;");
		
		lblMaintainence = new Label("Maintainence Type :");
		lblMaintainence.setImmediate(false);
		lblMaintainence.setWidth("120px");
		lblMaintainence.setHeight("-1px");
		mainLayout.addComponent(lblMaintainence, "top:70px; left:30px;");
		
		selectRegu.setImmediate(true);
		selectRegu.setStyleName("horizontal");
		mainLayout.addComponent(selectRegu, "top:68px; left:140px;");
		
		selectMain.setImmediate(true);
		selectMain.setStyleName("horizontal");
		mainLayout.addComponent(selectMain, "top:68px; left:220px;");

		selectService.setImmediate(true);
		selectService.setStyleName("horizontal");
		mainLayout.addComponent(selectService, "top:68px; left:325px;");
		
		// btnBillPreview
		btnBillPreview = new Button();
		btnBillPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnBillPreview.addStyleName("icon-after-caption");
		btnBillPreview.setImmediate(true);
//		btnBillPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
//		mainLayout.addComponent(btnBillPreview, "top:100.0px;left:555.0px;");
		
		//firstTable();
		commonTable.setWidth("1080px");
		commonTable.setHeight("320px");
		commonTable.setColumnCollapsingAllowed(true);
		
		commonTable.addContainerProperty("SL", Label.class, new Label());
		commonTable.setColumnWidth("SL", 15);

		commonTable.addContainerProperty("Expense Head", TextField.class, new TextField());
		commonTable.setColumnWidth("Expense Head", 150);

		commonTable.addContainerProperty("Qty", AmountField.class, new AmountField());
		commonTable.setColumnWidth("Qty", 45);

		commonTable.addContainerProperty("Type", ComboBox.class, new ComboBox());
		commonTable.setColumnWidth("Type", 80);
		
		commonTable.addContainerProperty("Unit", TextField.class, new TextField());
		commonTable.setColumnWidth("Unit", 50);

		commonTable.addContainerProperty("Rate", AmountField.class, new AmountField());
		commonTable.setColumnWidth("Rate", 45);

		commonTable.addContainerProperty("Amount", AmountCommaSeperator.class, new AmountCommaSeperator());
		commonTable.setColumnWidth("Amount", 70);
		
		commonTable.addContainerProperty("End Meter Reading", AmountField.class, new AmountField());
		commonTable.setColumnWidth("End Meter Reading", 80);
		
		commonTable.addContainerProperty("Running Milage", AmountField.class, new AmountField());
		commonTable.setColumnWidth("Running Milage", 70);
		
		commonTable.addContainerProperty("Cost Per Mile", AmountField.class, new AmountField());
		commonTable.setColumnWidth("Cost Per Mile", 70);
		
		commonTable.addContainerProperty("Bill No", TextField.class, new TextField());
		commonTable.setColumnWidth("Bill No", 80);
			
		commonTable.addContainerProperty("Supplier", TextField.class, new TextField());
		commonTable.setColumnWidth("Supplier", 130);
		
		commonTable.addContainerProperty("Last Rep. Date", PopupDateField.class, new PopupDateField());
		commonTable.setColumnWidth("Last Rep. Date", 85);
		
		commonTable.addContainerProperty("Last Servicing", PopupDateField.class, new PopupDateField());
		commonTable.setColumnWidth("Last Servicing", 90);
		
		commonTable.addContainerProperty("L. Amount", AmountField.class, new AmountField());
		commonTable.setColumnWidth("L. Amount", 65);
		
		commonTable.addContainerProperty("Last Supplier", TextField.class, new TextField());
		commonTable.setColumnWidth("Last Supplier", 90);
		
		commonTable.addContainerProperty("Remarks", TextField.class, new TextField());
		commonTable.setColumnWidth("Remarks", 110);
		
		mainLayout.addComponent(commonTable,"top: 100.0px; left: 10.0px; ");
		commonTable.setImmediate(true);
		
		mainLayout.addComponent(cButton, "top:440px; left:300px;");
		
		return mainLayout;
	}
}
