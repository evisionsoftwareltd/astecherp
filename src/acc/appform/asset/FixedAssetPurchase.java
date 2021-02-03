package acc.appform.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExample;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.TextRead;
import com.example.astechac.AstechacApplication;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
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
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class FixedAssetPurchase extends Window
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "","Exit");
	private VerticalLayout mainLayout = new VerticalLayout();
	private GridLayout titleGrid = new GridLayout(1,1);
	private HorizontalLayout horLayout = new HorizontalLayout();
	private FormLayout leftLayout = new FormLayout();
	private FormLayout rightLayout = new FormLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();

	private TextField voucherNo = new TextField("Voucher No:");
	private ComboBox purchaseFrom = new ComboBox("Purchase From:");
	private TextField assetId = new TextField("Asset Id:");

	private ComboBox group = new ComboBox("Group:");
	private ComboBox subGroup = new ComboBox("Sub Group:");
	private ComboBox ledgerName = new ComboBox("Ledger Name:");
	private ComboBox costCenter = new ComboBox("Cost Center:");

	private TextField assetName = new TextField("Asset Name:");
	private TextField identificationMark = new TextField("Identification Mark:");
	private TextField dept = new TextField("Department:");
	private TextField location = new TextField("Location:");
	private TextField supplierName = new TextField("Supplier Name:");
	private TextField supplierAddress = new TextField("Supplier Address:");

	private DateField dateAcquisition = new DateField("Date of Acquisition:");
	private DateField dateInstall = new DateField("Date of Installation:");
	private DateField billDate = new DateField("Bill Date:");
	private TextField billNo = new TextField("Bill No/L/C No:");
	private TextField challNo = new TextField("Del. Chall No:");

	private AmountCommaSeperator procCost = new AmountCommaSeperator("Procurement Cost:");
	private AmountCommaSeperator installCost = new AmountCommaSeperator("Installation Cost:");
	private AmountCommaSeperator otherCost = new AmountCommaSeperator("Other Cost:");

	private TextField totalCost = new TextField("Total Cost:");
	private AmountField lifeYear = new AmountField("Life(years):");
	private NativeSelect depSystem = new NativeSelect("Depreciation System:");
	private TextField depreciation = new TextField("Depreciation:");
	private TextField annualDepreciation = new TextField("Annual Depreciation:");
	private NumberFormat frmt = new DecimalFormat("#0.00");
	private CommaSeparator cms = new CommaSeparator();
	private SessionBean sessionBean;
	private boolean isUpdate = false;
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat df = new DecimalFormat("#0");

	private String lcw = "240px";
	private String rcw = "190px";

	private TextField findVoucherNo = new TextField();
	private TextField findAssetId = new TextField();

	private ImmediateUploadExample bpvUpload = new ImmediateUploadExample("");
	String bpvPdf = null;
	String filePathTmp = "";
	String imageLoc = "0" ;

	private Button btnPreview = new Button("Bill Preview");

	private Date vDate = null;

	public FixedAssetPurchase(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("FIXED ASSET PURCHASE :: "+sessionBean.getCompany());
		this.setWidth("820px");
		this.setResizable(false);
		titleGrid.addComponent(new Label("<h3>FIXED ASSET PURCHASE</h3>",Label.CONTENT_XHTML));
		mainLayout.addComponent(titleGrid);
		mainLayout.setComponentAlignment(titleGrid, Alignment.TOP_CENTER);

		leftLayout.addComponent(voucherNo);
		voucherNo.setWidth(lcw);
		voucherNo.setReadOnly(true);
		leftLayout.addComponent(purchaseFrom);
		purchaseFrom.setWidth(lcw);
		leftLayout.addComponent(assetId);
		assetId.setReadOnly(true);
		leftLayout.addComponent(group);
		group.setWidth(lcw);
		group.setImmediate(true);
		leftLayout.addComponent(subGroup);
		subGroup.setWidth(lcw);
		subGroup.setImmediate(true);
		leftLayout.addComponent(ledgerName);
		
		ledgerName.setNewItemsAllowed(true);
		ledgerName.setNullSelectionAllowed(false);
		ledgerName.setWidth(lcw);
		ledgerName.setImmediate(true);
		
		leftLayout.addComponent(costCenter);
		costCenter.setNullSelectionAllowed(false);
		costCenter.setWidth(lcw);
		costCenter.setImmediate(true);	

		leftLayout.addComponent(assetName);
		assetName.setWidth(lcw);
		leftLayout.addComponent(identificationMark);
		identificationMark.setWidth(lcw);
		leftLayout.addComponent(dept);
		dept.setWidth(lcw);
		leftLayout.addComponent(location);
		location.setWidth(lcw);
		leftLayout.addComponent(supplierName);
		supplierName.setWidth(lcw);
		leftLayout.addComponent(supplierAddress);
		supplierAddress.setWidth(lcw);
		supplierAddress.setRows(2);
		leftLayout.setMargin(true);
		leftLayout.setSpacing(true);

		rightLayout.addComponent(dateAcquisition);
		dateAcquisition.setValue(new java.util.Date());
		dateAcquisition.setResolution(PopupDateField.RESOLUTION_DAY);
		dateAcquisition.setDateFormat("dd-MM-yyyy");
		dateAcquisition.setImmediate(true);
		rightLayout.addComponent(dateInstall);
		dateInstall.setValue(new java.util.Date());
		dateInstall.setResolution(PopupDateField.RESOLUTION_DAY);
		dateInstall.setDateFormat("dd-MM-yyyy");
		dateInstall.setImmediate(true);
		rightLayout.addComponent(billDate);
		billDate.setValue(new java.util.Date());
		billDate.setResolution(PopupDateField.RESOLUTION_DAY);
		billDate.setDateFormat("dd-MM-yyyy");
		billDate.setImmediate(true);
		rightLayout.addComponent(billNo);
		billNo.setWidth(rcw);
		rightLayout.addComponent(challNo);
		challNo.setWidth(rcw);
		rightLayout.addComponent(procCost);
		//procCost.setWidth(rcw);
		procCost.setImmediate(true);
		procCost.setTextChangeEventMode(TextChangeEventMode.LAZY);
		procCost.setTextChangeTimeout(200);
		procCost.addListener(new TextChangeListener() 
		{
			public void textChange(TextChangeEvent event) 
			{
				totalCost.setReadOnly(false);
				totalCost.setValue((Double.valueOf("0"+event.getText().replace(",",""))+Double.valueOf("0"+installCost.getValue().replace(",",""))+
						Double.valueOf("0"+otherCost.getValue().replace(",",""))));
				totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
				totalCost.setReadOnly(true);
			}
		});
		rightLayout.addComponent(installCost);
		//installCost.setWidth(rcw);
		installCost.setImmediate(true);
		installCost.setTextChangeEventMode(TextChangeEventMode.LAZY);
		installCost.setTextChangeTimeout(200);
		installCost.addListener(new TextChangeListener() 
		{
			@Override
			public void textChange(TextChangeEvent event) 
			{
				totalCost.setReadOnly(false);
				totalCost.setValue((Double.valueOf("0"+procCost.getValue().replace(",",""))+Double.valueOf("0"+event.getText().replace(",",""))+
						Double.valueOf("0"+otherCost.getValue().replace(",",""))));
				totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
				totalCost.setReadOnly(true);
			}
		});
		rightLayout.addComponent(otherCost);
		otherCost.setImmediate(true);
		otherCost.setTextChangeEventMode(TextChangeEventMode.LAZY);
		otherCost.setTextChangeTimeout(200);
		otherCost.addListener(new TextChangeListener() 
		{
			@Override
			public void textChange(TextChangeEvent event) 
			{
				totalCost.setReadOnly(false);
				totalCost.setValue((Double.valueOf("0"+procCost.getValue().replace(",",""))+Double.valueOf("0"+installCost.getValue().replace(",",""))+
						Double.valueOf("0"+event.getText().replace(",",""))));
				totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
				totalCost.setReadOnly(true);
			}
		});
		rightLayout.addComponent(totalCost);
		totalCost.setStyleName("fright");
		totalCost.setReadOnly(true);
		rightLayout.addComponent(lifeYear);
		lifeYear.setImmediate(true);
		lifeYear.setTextChangeEventMode(TextChangeEventMode.LAZY);
		lifeYear.setTextChangeTimeout(200);
		lifeYear.addListener(new TextChangeListener() 
		{
			@Override
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
				else{
					//	depreciation.setValue(0);
					annualDepreciation.setReadOnly(false);
					annualDepreciation.setValue(0);
					annualDepreciation.setValue(cms.setComma(Double.valueOf(annualDepreciation.getValue().toString())));
					annualDepreciation.setReadOnly(true);
				}
			}
		});
		rightLayout.addComponent(depSystem);
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
		rightLayout.addComponent(depreciation);
		depreciation.setCaption("Depreciation:");
		depreciation.setImmediate(true);
		depreciation.setTextChangeEventMode(TextChangeEventMode.LAZY);
		depreciation.setTextChangeTimeout(200);
		depreciation.addListener(new TextChangeListener() 
		{
			@Override
			public void textChange(TextChangeEvent event) 
			{
				float d = Float.valueOf("0"+event.getText());
				System.out.println(d);
				if(d>0.0)
				{
					annualDepreciation.setReadOnly(false);
					annualDepreciation.setValue(Double.valueOf(totalCost.getValue().toString().replace(",", ""))/(100/d));
					annualDepreciation.setReadOnly(true);
				}
				else
				{
					annualDepreciation.setReadOnly(false);
					annualDepreciation.setValue(0);
					annualDepreciation.setReadOnly(true);
				}
			}
		});

		rightLayout.addComponent(annualDepreciation);

		annualDepreciation.setStyleName("fright");
		annualDepreciation.setReadOnly(true);

		rightLayout.addComponent(bpvUpload);

		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));

		rightLayout.addComponent(btnPreview);

		rightLayout.setMargin(true);
		rightLayout.setSpacing(true);

		horLayout.addComponent(leftLayout);
		horLayout.addComponent(rightLayout);

		HorizontalLayout  sp1 = new HorizontalLayout();
		sp1.setWidth("70px");
		btnLayout.addComponent(sp1);
		btnLayout.addComponent(button);
		btnLayout.setSpacing(true);
		btnLayout.setMargin(true);

		horLayout.setSpacing(true);

		mainLayout.addComponent(horLayout);
		mainLayout.addComponent(btnLayout);
		Component allComp[] = {purchaseFrom, group, subGroup, ledgerName, costCenter, assetName, identificationMark, dept, location, supplierName, supplierAddress, dateAcquisition, dateInstall, billDate, billNo, challNo, procCost, installCost, otherCost, lifeYear, depSystem, depreciation, button.btnSave, button.btnNew};
		new FocusMoveByEnter(this,allComp);
		this.addComponent(mainLayout);
		setButtonAction();
		btnIni(true);
		txtEnable(false);
		groupInitialise();
		purchaseFromInitialise();
		costCenterInitialize();
		button.btnNew.focus();
	}

	private void setButtonAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findVoucherNo.setValue("");
				findAssetId.setValue("");
				newBtnAction();
				purchaseFrom.focus();
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(dateAcquisition.getValue())))
					updateBtnAction();
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(nullCheck())
				{
					if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(dateAcquisition.getValue())))
						saveBtnAction();
					else
						showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findVoucherNo.setValue("");
				findAssetId.setValue("");
				isUpdate = false;
				btnIni(true);
				txtEnable(false);
				txtClear();
				button.btnNew.focus();
			}
		});

		button.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(dateAcquisition.getValue())))
					deleteBtnAction(event);
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
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

		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"");
				System.out.println("Done");
			}
		});

		group.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				subgroupInitialise();
			}
		});

		subGroup.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(subGroup.getValue()!=null)
					ledgerInitialise();
			}
		});


		btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				// Hyperlink to a given URL
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString();

					if(link.endsWith("uptd/"))
					{
						link = link.replaceAll("uptd", "report")+filePathTmp;
					}
					else if(link.endsWith("astechltd/"))
					{
						link = link.replaceAll("astechltd", "report")+filePathTmp;
					}
					else if(link.endsWith("SamPolymer/"))
					{
						link = link.replaceAll("SamPolymer", "report")+filePathTmp;
					}
					else if(link.endsWith("UNIGLOBAL/"))
					{
						link = link.replaceAll("UNIGLOBAL", "report")+filePathTmp;
					}

					System.out.println(link);
					System.out.println("aa :"+event.getSource());

					getWindow().open(new ExternalResource(link),"_blank", // window name
							500, // width
							200, // weight
							Window.BORDER_NONE // decorations
							);
				}
				if(isUpdate)
				{
					if(!bpvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								link = link.replaceAll("uptd/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("astechltd/"))
							{
								link = link.replaceAll("astechltd/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("SamPolymer/"))
							{
								link = link.replaceAll("SamPolymer/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("UNIGLOBAL/"))
							{
								link = link.replaceAll("UNIGLOBAL/", imageLoc.substring(27, imageLoc.length()));
							}

							System.out.println(link);
							System.out.println("bb : "+event.getSource());

							getWindow().open(new ExternalResource(link),"_blank", // window name
									500, // width
									200, // weight
									Window.BORDER_NONE // decorations
									);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+filePathTmp;
						}
						else if(link.endsWith("astechltd/"))
						{
							link = link.replaceAll("astechltd", "report")+filePathTmp;
						}
						else if(link.endsWith("SamPolymer/"))
						{
							link = link.replaceAll("SamPolymer", "report")+filePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+filePathTmp;
						}

						System.out.println(link);
						System.out.println("aa :"+event.getSource());

						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
			}
		});
	}

	private void findBtnAction()
	{
		Window win = new SearchAssetPurchase(sessionBean,findVoucherNo,findAssetId);
		win.center();
		this.getParent().addWindow(win);
		win.setModal(true);
		win.setCloseShortcut(KeyCode.ESCAPE);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(findVoucherNo.getValue().toString().length()>0)
					findInitialise();
			}
		});
	}

	private void findInitialise()
	{
		voucherNo.setReadOnly(false);
		voucherNo.setValue(findVoucherNo.getValue());
		voucherNo.setReadOnly(true);

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List led = session.createSQLQuery("SELECT vGroupID,vSubGroupID,vLedgerID,vDescription,AssetDrAmount,AssetCrAmount,"+
					"DepreciationDrAmount,DepreciationCrAmount,vIdentification,vDepartment,vLocation,"+
					"vSupplierName,vSupplierAddress,dAcquisition,dInstallation,BillDate,[vBill/L/CNo],DelChallanNo,"+
					"mProcurement,mInstallation,mOther,mTotal,iLife,vDepreciationSystem,iDepreciationPer,mAnnualDepreciation,AssetID, costId "+
					"FROM tbFixedAsset WHERE VoucherNo = '"+findVoucherNo.getValue()+"' and AssetId = '"+findAssetId.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list();

			for(Iterator iter = led.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				purchaseFrom.setValue(element[2]);
				assetId.setReadOnly(false);
				assetId.setValue(element[26]);
				assetId.setReadOnly(true);
				group.setValue(element[0]);
				subGroup.setValue(element[1]);

				ledgerName.setValue(element[26]);
				//	System.out.println(element[2].toString());
				assetName.setValue(element[3]);
				//h
				identificationMark.setValue(element[8]);
				dept.setValue(element[9]);
				location.setValue(element[10]);
				supplierName.setValue(element[11]);
				supplierAddress.setValue(element[12]);

				vDate = new Date(element[13].toString().replace("-", "/").substring(0,10).trim());
				dateAcquisition.setValue(new Date(element[13].toString().replace("-", "/").substring(0,10).trim()));
				dateInstall.setValue(new Date(element[14].toString().replace("-", "/").substring(0,10).trim()));
				billDate.setValue(new Date(element[15].toString().replace("-", "/").substring(0,10).trim()));
				billNo.setValue(element[16]);
				challNo.setValue(element[17]);

				BigDecimal i = new BigDecimal(element[18]+"");

				procCost.setValue(frmt.format(i));
				installCost.setValue(Double.valueOf(element[19]+""));
				otherCost.setValue(Double.valueOf(element[20]+""));
				totalCost.setReadOnly(false);
				totalCost.setValue(Double.valueOf(element[21]+""));
				totalCost.setValue(cms.setComma(Double.valueOf(totalCost.getValue().toString())));
				totalCost.setReadOnly(true);
				lifeYear.setValue(element[22]);
				depSystem.setValue(element[23]);
				depreciation.setValue(element[24]); 
				costCenter.setValue(element[27]);
				annualDepreciation.setReadOnly(false);
				annualDepreciation.setValue(cms.setComma(Double.valueOf(element[25].toString())));
				annualDepreciation.setReadOnly(true);
			}

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(dateAcquisition.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "ChequeDetails"+fsl;

			String q = "SELECT isNull(attachBill,0) FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND crAmount != 0 AND companyId = '"+ sessionBean.getCompanyId() +"'";
			Iterator iter = session.createSQLQuery(q).list().iterator();

			if(iter.hasNext())
			{
				imageLoc = iter.next().toString();
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void newBtnAction()
	{
		isUpdate = false;
		btnIni(false);
		txtEnable(true);
		txtClear();
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
						insertData();
						button.btnNew.focus();
					}
				}
			});
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

				String ledgerId = "";
				String sql = ""; 
				if(depreciation.getValue().toString().equals("")){
					depreciation.setValue("0");
				}

				String imagePath = imagePath(1,voucherNo.getValue().toString())==null?imageLoc:imagePath(1,voucherNo.getValue().toString());

				if(ledgerName.getValue().toString().equals(ledgerName.getItemCaption(ledgerName.getValue())))
				{
					Iterator iter = session.createSQLQuery("SELECT 'AL'+CAST(ISNULL(max(cast(substring(Ledger_Id,3,len(Ledger_Id)) AS integer))+1,101) AS varchar) "+
							"FROM tbLedger WHERE SUBSTRING(Ledger_Id,1,1) = 'A' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					ledgerId = iter.next().toString();

					sql = "INSERT INTO tbLedger(Ledger_Id,Ledger_Name,Creation_Year,Parent_Id,Create_From,userId,userIp,entryTime, companyId) VALUES('"+ledgerId+"','"+
							ledgerName.getValue()+"',(SELECT YEAR(op_date) FROM tbFiscal_Year  WHERE SlNo = '"+fsl+"'),'"+parentId+"','"+createFrom+"','"+
							sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();


					sql = "INSERT INTO tbAssetOpBalance(AssetId,AssetDrAmount,AssetCrAmount,DepreciationDrAmount,DepreciationCrAmount,Op_Year, companyId) VALUES('"+
							ledgerId+"',0,0,0,0,(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo = '"+fsl+"'), '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					System.out.println("A");
					sql = "INSERT INTO tbLedger_Op_Balance(Ledger_Id,DrAmount,CrAmount,Op_Year,userId,userIp,entryTime, companyId) VALUES('"+ledgerId+"',0,0"+
							",(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo = '"+fsl+"'),'"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

				}
				else
				{
					ledgerId = ledgerName.getValue().toString();
				}

				session.createSQLQuery("DELETE FROM tbFixedAsset WHERE [Type]= 'P' AND VoucherNo = '"+findVoucherNo.getValue()+"' AND dAcquisition = '"+dtfYMD.format(vDate)+"' and  CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
				session.createSQLQuery("DELETE FROM "+voucher+" WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();

				session.createSQLQuery("DELETE FROM tbLedger_Op_Balance WHERE Ledger_Id = '"+ledgerId+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' and op_Year = (SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo = '"+fsl+"')").executeUpdate();

				sql = "Insert into tbFixedAsset(VoucherNo, AssetID, dAcquisition, dInstallation, vDescription, vGroupID, vSubGroupID, vLedgerID,vLedgerName, AssetDrAmount, AssetCrAmount, DepreciationDrAmount, DepreciationCrAmount, vSupplierName, vSupplierAddress, mProcurement, mInstallation, mOther, mTotal, iLife, mResidualValue, vDepreciationSystem, iDepreciationPer, mAnnualDepreciation, vDepreciationPolicy, mWrittenValue, vIdentification, vLocation, vDepartment, VoucherType, [vBill/L/CNo], DelChallanNo, BillDate, Type, companyId, costId)" +
						"values ('"+findVoucherNo.getValue()+"', '"+ledgerId+"', '"+dtfYMD.format(dateAcquisition.getValue())+"','"+dtfYMD.format(dateInstall.getValue())+"', '"+assetName.getValue()+"', '"+ group.getValue() +"', '"+ subGroup.getValue() +"', '"+ purchaseFrom.getValue() +"','"+ledgerName.getItemCaption(ledgerName.getValue())+"', 0, 0, 0, 0, '"+ supplierName.getValue() +"', '"+ supplierAddress.getValue() +"', '"+ procCost.getValue() +"', '"+ installCost.getValue() +"', '"+ otherCost.getValue() +"', '"+ totalCost.getValue().toString().replace(",", "") +"', '"+ lifeYear.getValue() +"', 0, '"+ depSystem.getValue() +"', '"+ depreciation.getValue() +"', '"+ annualDepreciation.getValue().toString().replace(",", "") +"', '', 0, '"+ identificationMark.getValue() +"', '"+ location.getValue() +"', '"+ dept.getValue() +"', '', '"+ billNo.getValue() +"', '"+ challNo.getValue() +"', '"+ dtfYMD.format(billDate.getValue()) +"', 'P', '"+ sessionBean.getCompanyId() +"', '"+ costCenter.getValue() +"')";
				session.createSQLQuery(sql).executeUpdate();

				BigDecimal p = new BigDecimal(procCost.getValue().toString());
				BigDecimal i = new BigDecimal(installCost.getValue().toString());
				BigDecimal o = new BigDecimal(otherCost.getValue().toString());

				String ttlAmt = o.add(p.add(i)).toString();				

				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId, userId,userIp,entryTime, companyId,attachBill) "+
						" VALUES('"+findVoucherNo.getValue()+"','"+dtfYMD.format(dateAcquisition.getValue())+"','"+ledgerId+"','"+assetName.getValue()+"','"+
						ttlAmt+"','0','pao', '"+ costCenter.getValue() +"', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"')";
				session.createSQLQuery(sql).executeUpdate();

				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId, userId,userIp,entryTime, companyId,attachBill) "+
						" VALUES('"+findVoucherNo.getValue()+"','"+dtfYMD.format(dateAcquisition.getValue())+"','"+purchaseFrom.getValue()+"','"+assetName.getValue()+"','0','"+
						ttlAmt+"','pao', '"+ costCenter.getValue() +"', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"')";
				session.createSQLQuery(sql).executeUpdate();

				sql = "INSERT INTO tbLedger_Op_Balance(Ledger_Id,DrAmount,CrAmount,Op_Year,userId,userIp,entryTime, companyId) VALUES('"+ledgerId+"',0,0"+
						",(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo = '"+fsl+"'),'"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";
				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				showNotification("All Information updated successfully.");
				assetId.setReadOnly(false);
				assetId.setValue(ledgerId);
				assetId.setReadOnly(true);
				txtEnable(false);
				btnIni(true);

				voucherNo.setReadOnly(false);
				voucherNo.setValue(findVoucherNo.getValue());
				voucherNo.setReadOnly(true);
				assetId.setReadOnly(false);
				assetId.setValue(ledgerId);
				assetId.setReadOnly(true);
			}
			catch(Exception exp)
			{
				showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
	}

	private boolean nullCheck()
	{
		boolean a = false;
		if(purchaseFrom.getValue()!=null)
		{
			if(group.getValue()!=null)
			{
				if(ledgerName.getValue()!=null)
				{
					if(!assetName.getValue().toString().equals(""))
					{
						if(!procCost.getValue().toString().equals(""))
						{
							if(!lifeYear.getValue().toString().equals(""))
							{
								if(depSystem.getValue()!=null)
								{
									a = true;
								}
								else
								{
									showNotification("Warning","Select Depreciation System",Notification.TYPE_WARNING_MESSAGE);
									depSystem.focus();
								}
							}
							else
							{
								showNotification("Warning","Provide Asset Life(Years)",Notification.TYPE_WARNING_MESSAGE);
								lifeYear.focus();
							}
						}
						else
						{
							showNotification("Warning","Provide Procurement Cost",Notification.TYPE_WARNING_MESSAGE);
							procCost.focus();
						}
					}
					else
					{				
						showNotification("Warning","Provide Asset Name",Notification.TYPE_WARNING_MESSAGE);
						assetName.focus();
					}
				}
				else
				{
					showNotification("Warning","Select Asset Name",Notification.TYPE_WARNING_MESSAGE);
					ledgerName.focus();
				}
			}
			else
			{
				showNotification("Warning","Select Group Name",Notification.TYPE_WARNING_MESSAGE);
				group.focus();
			}
		}
		else
		{
			showNotification("Warning","Select purchase from.",Notification.TYPE_WARNING_MESSAGE);
			purchaseFrom.focus();
		}
		return a;
	}

	private void insertData()
	{
		if(sessionBean.isSubmitable())
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

				int sl = 1;
				Iterator itr = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE vouchertype = 'pao' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();

				if(itr.hasNext())
					sl = Integer.valueOf(itr.next().toString());

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

				if(depreciation.getValue().toString().equals("")){
					depreciation.setValue("0");
				}

				String assetNameWithSubGroup = "";
				if(subGroup.getValue() == null)
					assetNameWithSubGroup = assetName.getValue().toString();
				else
					assetNameWithSubGroup = assetName.getValue()+" ("+subGroup.getItemCaption(subGroup.getValue())+")";

				String ledgerId = "",sql;
				if(ledgerName.getValue().toString().equals(ledgerName.getItemCaption(ledgerName.getValue()))){
					Iterator iter = session.createSQLQuery("SELECT 'AL'+CAST(ISNULL(max(cast(substring(Ledger_Id,3,len(Ledger_Id)) AS integer))+1,101) AS varchar) "+
							"FROM tbLedger WHERE SUBSTRING(Ledger_Id,1,1) = 'A' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					ledgerId = iter.next().toString();

					//tbLedger Table
					sql = "INSERT INTO tbLedger(Ledger_Id,Ledger_Name,Creation_Year,Parent_Id,Create_From,userId,userIp,entryTime, companyId) VALUES('"+ledgerId+"','"+
							ledgerName.getValue()+"',(SELECT YEAR(op_date) FROM tbFiscal_Year  WHERE SlNo = '"+fsl+"'),'"+parentId+"','"+createFrom+"','"+
							sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					//AssetOpBalance Table 
					sql = "INSERT INTO tbAssetOpBalance(AssetId,AssetDrAmount,AssetCrAmount,DepreciationDrAmount,DepreciationCrAmount,Op_Year, companyId) VALUES('"+
							ledgerId+"',0,0,0,0,(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo = '"+fsl+"'), '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					//LedgerOpenBalance Table
					sql = "INSERT INTO tbLedger_Op_Balance(Ledger_Id,DrAmount,CrAmount,Op_Year,userId,userIp,entryTime, companyId) VALUES('"+ledgerId+"',0,0"+
							",(SELECT YEAR(op_date) FROM tbFiscal_Year WHERE SlNo = '"+fsl+"'),'"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();
				}
				else
				{						
					ledgerId = ledgerName.getValue().toString();
				}

				sql = "Insert into tbFixedAsset(VoucherNo, AssetID, dAcquisition, dInstallation, vDescription, vGroupID, vSubGroupID, vLedgerID,vLedgerName, AssetDrAmount, AssetCrAmount, DepreciationDrAmount, DepreciationCrAmount, vSupplierName, vSupplierAddress, mProcurement, mInstallation, mOther, mTotal, iLife, mResidualValue, vDepreciationSystem, iDepreciationPer, mAnnualDepreciation, vDepreciationPolicy, mWrittenValue, vIdentification, vLocation, vDepartment, VoucherType, [vBill/L/CNo], DelChallanNo, BillDate, Type, companyId, costId)" +
						"values ('AP-CR-"+sl+"', '"+ledgerId+"', '"+dtfYMD.format(dateAcquisition.getValue())+"','"+dtfYMD.format(dateInstall.getValue())+"', '"+assetName.getValue()+"', '"+ group.getValue() +"', '"+ subGroup.getValue() +"', '"+ purchaseFrom.getValue() +"','"+ledgerName.getItemCaption(ledgerName.getValue())+"', 0, 0, 0, 0, '"+ supplierName.getValue() +"', '"+ supplierAddress.getValue() +"', '"+ procCost.getValue() +"', '"+ installCost.getValue() +"', '"+ otherCost.getValue() +"', '"+ totalCost.getValue().toString().replace(",", "") +"', '"+ lifeYear.getValue() +"', 0, '"+ depSystem.getValue() +"', '"+ depreciation.getValue() +"', '"+ annualDepreciation.getValue() +"', '', 0, '"+ identificationMark.getValue() +"', '"+ location.getValue() +"', '"+ dept.getValue() +"', '', '"+ billNo.getValue() +"', '"+ challNo.getValue() +"', '"+ dtfYMD.format(billDate.getValue()) +"', 'P', '"+ sessionBean.getCompanyId() +"', '"+ costCenter.getValue() +"')";
				session.createSQLQuery(sql).executeUpdate();

				BigDecimal p = new BigDecimal(procCost.getValue().toString());
				BigDecimal i = new BigDecimal(installCost.getValue().toString());
				BigDecimal o = new BigDecimal(otherCost.getValue().toString());

				String ttlAmt = o.add(p.add(i)).toString();	
				String imagePath = imagePath(1,"DR-CH-"+sl)==null?imageLoc:imagePath(1,"DR-CH-"+sl);

				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId, userId,userIp,entryTime, companyId, attachBill) "+
						" VALUES('AP-CR-"+sl+"','"+dtfYMD.format(dateAcquisition.getValue())+"','"+ledgerId+"','"+assetName.getValue()+"','"+
						ttlAmt+"','0','pao', '"+ costCenter.getValue() +"', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"')";
				session.createSQLQuery(sql).executeUpdate();

				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId,userId,userIp,entryTime, companyId, attachBill) "+
						" VALUES('AP-CR-"+sl+"','"+dtfYMD.format(dateAcquisition.getValue())+"','"+purchaseFrom.getValue()+"','"+assetName.getValue()+"','0','"+
						ttlAmt+"','pao', '"+ costCenter.getValue() +"', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"')";
				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				showNotification("All Information saved successfully.");
				assetId.setReadOnly(false);
				assetId.setValue(ledgerId);
				assetId.setReadOnly(true);
				txtEnable(false);
				btnIni(true);

				voucherNo.setReadOnly(false);
				voucherNo.setValue("AP-CR-"+sl);
				voucherNo.setReadOnly(true);

				if(ledgerName.getValue().toString().equals(ledgerName.getItemCaption(ledgerName.getValue())))
				{
					String sn = ledgerName.getValue().toString();
					ledgerName.removeItem(sn);
					ledgerName.addItem(ledgerId);
					ledgerName.setItemCaption(ledgerId, sn);
				}

			}
			catch(Exception exp)
			{
				showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void deleteBtnAction(ClickEvent e)
	{
		if(sessionBean.isDeleteable())
		{
			if(findVoucherNo.getValue().toString().trim().length()>0)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete voucher no "+findVoucherNo.getValue()+"?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{

							//		if(isValidDeleteUp(0))
							deleteData();
						}
					}
				});
			}
			else
			{
				showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for delete.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void deleteData()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(dateAcquisition.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequedetails"+fsl;

			session.createSQLQuery("DELETE FROM tbFixedAsset WHERE [Type]= 'P' AND VoucherNo = '"+findVoucherNo.getValue()+"' AND dAcquisition = '"+dtfYMD.format(dateAcquisition.getValue())+"' and CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
			session.createSQLQuery("DELETE FROM "+voucher+" WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
			tx.commit();
			showNotification("Desired Information delete successfully.");
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void updateBtnAction()
	{
		if(sessionBean.isUpdateable())
		{
			if(findVoucherNo.getValue().toString().trim().length()>0)
			{
				btnIni(false);
				txtEnable(true);
				isUpdate = true;
				dateAcquisition.setEnabled(false);
			}
			else
			{
				showNotification("Edit Failed","There are no data for Edit.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void purchaseFromInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			try
			{
				purchaseFrom.removeAllItems();
			}
			catch(Exception e){}
			tx = session.beginTransaction();
			//List g = session.createQuery("SELECT h.ledgerId,h.ledgerName,h.createFrom FROM TbLedger as h WHERE substring(h.createFrom,1,2) in ('L6')").list();
			List g = session.createSQLQuery("SELECT ledger_Id,ledger_Name,create_From FROM TbLedger where companyId in ('0', '"+ sessionBean.getCompanyId()+"')").list();
			//purchaseFrom.addItem("");
			for (Iterator iter = g.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				purchaseFrom.addItem(element[0].toString());
				purchaseFrom.setItemCaption(element[0].toString(), element[1].toString());
			}
			purchaseFrom.setNullSelectionAllowed(false);
			//subGroup.setValue("");
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void subgroupInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			try
			{
				subGroup.removeAllItems();}catch(Exception e){}
			tx = session.beginTransaction();
			List g = session.createSQLQuery("SELECT sub_Group_Id,sub_Group_Name FROM TbSub_Group WHERE group_Id = '"+group.getValue()+"'").list();

			for (Iterator iter = g.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				subGroup.addItem(element[0].toString());
				subGroup.setItemCaption(element[0].toString(), element[1].toString());
			}
			//subGroup.setNullSelectionAllowed(false);				
		}
		catch(Exception exp)
		{
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
			{}

			for (Iterator iter = g.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				group.addItem(element[0].toString());
				group.setItemCaption(element[0].toString(), element[1].toString());
			}
			//group.setNullSelectionAllowed(false);

		}
		catch(Exception exp)
		{
			//System.out.println(exp);
			showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void ledgerInitialise()
	{
		try
		{
			String createFrom = "";

			if(subGroup.getValue() != null)
			{
				createFrom ="A1-"+group.getValue()+"-"+subGroup.getValue();
			}
			else if(group.getValue() != null)
			{
				createFrom = "A1-"+group.getValue();
			}
			else
			{
				createFrom = "A1";
			}

			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List g = session.createSQLQuery("SELECT ledger_Id,ledger_Name,create_From FROM TbLedger WHERE create_From = '"+createFrom+"' AND CompanyId in ('0', '"+ sessionBean.getCompanyId() +"')").list();
			try
			{
				ledgerName.removeAllItems();
			}
			catch(Exception e)
			{}
			for (Iterator iter = g.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				ledgerName.addItem(element[0]);
				ledgerName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			//System.out.println(exp);
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
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
		purchaseFrom.setEnabled(t);
		group.setEnabled(t);
		subGroup.setEnabled(t);
		ledgerName.setEnabled(t);
		costCenter.setEnabled(t);
		assetName.setEnabled(t);		
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
		procCost.setEnabled(t);
		installCost.setEnabled(t);
		otherCost.setEnabled(t);
		totalCost.setEnabled(t);
		lifeYear.setEnabled(t);
		depSystem.setEnabled(t);
		depreciation.setEnabled(t);
		annualDepreciation.setEnabled(t);
		bpvUpload.setEnabled(t);
		btnPreview.setEnabled(t);
	}

	private void txtClear()
	{
		voucherNo.setReadOnly(false);
		voucherNo.setValue("");
		voucherNo.setReadOnly(true);

		purchaseFrom.setValue(null);

		assetId.setReadOnly(false);
		assetId.setValue("");
		assetId.setReadOnly(true);
		group.setValue(null);
		subGroup.setValue(null);
		ledgerName.setValue(null);
		costCenter.setValue(null);
		assetName.setValue("");

		identificationMark.setValue("");
		dept.setValue("");
		location.setValue("");
		supplierName.setValue("");
		supplierAddress.setValue("");

		billNo.setValue("");
		challNo.setValue("");
		procCost.setValue("");
		installCost.setValue("");
		otherCost.setValue("");

		totalCost.setReadOnly(false);
		totalCost.setValue("");
		totalCost.setReadOnly(true);

		lifeYear.setValue("");
		depSystem.setValue(null);
		depreciation.setValue("");

		annualDepreciation.setReadOnly(false);
		annualDepreciation.setValue("");
		annualDepreciation.setReadOnly(true);

		bpvUpload.fileName = "";
		bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		bpvUpload.actionCheck = false;
		imageLoc = "0";
	}

	private boolean isnull()
	{
		if(purchaseFrom.getValue()!=null)
		{
			showNotification("", "Please select purchase form.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
		else if(ledgerName.getValue()!=null)
		{
			showNotification("", "Please select ledger name.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
				try {
					if(bpvUpload.fileName.toString().endsWith(".jpg")){
						String path = sessionBean.getUserId()+"APV";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						bpvPdf = SessionBean.imagePathTmp+path+".jpg";
						filePathTmp = path+".jpg";
					}else{
						String path = sessionBean.getUserId()+"APV";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						bpvPdf = SessionBean.imagePathTmp+path+".pdf";
						filePathTmp = path+".pdf";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return bpvPdf;
		}

		if(flag==1)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
				try {
					if(bpvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/assetPurchase/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/assetPurchase/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/assetPurchase/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/assetPurchase/"+path+".pdf";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return stuImage;
		}

		return null;
	}
	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp){}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}

}
