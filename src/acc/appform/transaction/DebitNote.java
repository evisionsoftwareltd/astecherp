package acc.appform.transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.ImmediateFileUpload;
import com.common.share.MessageBox;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;

@SuppressWarnings("serial")
public class DebitNote extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "", "Exit");

	public static final List<String> lsttype = Arrays.asList(new String[]{"Dues", "Advance", "PO", "Others"});
	private OptionGroup ogAgainst = new OptionGroup("",lsttype);

	public static final List<String> lsttype1 = Arrays.asList(new String[]{"Part", "Full"});
	private OptionGroup ogFullPart = new OptionGroup("",lsttype1);

	public static final List<String> lsttype2 = Arrays.asList(new String[]{"Cash", "Cheque"});
	private OptionGroup ogCashBank = new OptionGroup("",lsttype2);

	private Label lblDate;
	private PopupDateField dDate;
	private TextRead txtVoucherNo;
	private TextRead txtTempVoucherNo;

	private Label lblSupplierHead;
	private ComboBox cmbSupplierName;

	private Label lblRefferenceNo;
	private TextRead txtRefferenceNo;

	private Label lblPaymentAgainst;

	private Label lblPaymentStatus;

	private Label lblModeOfPayment;

	private Label lblAmount;
	private AmountCommaSeperator txtAmount;

	private Label lblPreparedBy;
	private TextRead txtPreparedBy;

	private boolean isUpdate = false;
	private boolean isApprove = false;

	private TextField txtFromFindWindow = new TextField();

	// dues panel
	private FormLayout fLayoutDues = new FormLayout();
	private Panel DuesPanel;
	private TextRead txtCurrentBalance;
	private PopupDateField dLastDate;
	private TextRead txtPaymentAmount;

	// advance panel
	private FormLayout fLayoutAdvance = new FormLayout();
	private Panel AdvancePanel;
	private ImmediateFileUpload bpvUpload = new ImmediateFileUpload("","Advance Attach");
	private ComboBox cmbApprovedBy;

	// Do panel
	private FormLayout fLayoutPo = new FormLayout();
	private Panel PoPanel;
	private ComboBox cmbPoNo;
	private PopupDateField dPoDate;
	private TextRead txtPoAmount;
	private TextRead txtPoApprovedBy;

	// other panel
	private FormLayout fLayoutOther = new FormLayout();
	private Panel OtherPanel;
	private ImmediateFileUpload bpvUploadOther = new ImmediateFileUpload("","Other Attach");
	private TextField txtOtherRemarks;

	// Cheque/Bank panel
	private FormLayout fLayoutCheque = new FormLayout();
	private Panel ChequePanel;
	private ComboBox cmbBankCashName;
	private ComboBox cmbChequeNo;
	private PopupDateField dChequeDate;
	private ComboBox cmbNarration;

	private SimpleDateFormat dfReference = new SimpleDateFormat("ddMMyyyy");
	private SimpleDateFormat dfYear = new SimpleDateFormat("yyyy");
	private SimpleDateFormat dfMonth = new SimpleDateFormat("MM");

	String advanceAttach = "";
	String othersAttach = "";

	String advanceAttachFind = "";
	String othersAttachFind = "";

	public String birthPdf = null;
	public String birthFilePathTmp = "";
	public String nidPdf = null;
	public String nidFilePathTmp = "";

	public DebitNote(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("DEBIT NOTE :: " + sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		componentIni(true);
		btnIni(true);

		setEventAction();

		ogCashBank.setValue("Cash");
		cmbPartyData();
		bankCashHeadIni();
		cmbApproveByData();
		cmbNarrationAdd();
		DuesPanel.setVisible(true);
		AdvancePanel.setVisible(false);
		PoPanel.setVisible(false);
		OtherPanel.setVisible(false);

		advanceAttach = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/advanceAttach/";
		othersAttach = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/othersAttach/";

		buttonShortCut();
		authenticationCheck();
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

	private void buttonShortCut()
	{
		button.btnNew.setClickShortcut(KeyCode.N, ModifierKey.ALT);
		button.btnEdit.setClickShortcut(KeyCode.U, ModifierKey.ALT);
		button.btnSave.setClickShortcut(KeyCode.S, ModifierKey.ALT);
		button.btnRefresh.setClickShortcut(KeyCode.C, ModifierKey.ALT);
		button.btnDelete.setClickShortcut(KeyCode.D, ModifierKey.ALT);
		button.btnFind.setClickShortcut(KeyCode.F, ModifierKey.ALT);
	}

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				componentIni(false);
				btnIni(false);
				dDate.focus();
				clearData();
				generateReference();
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				componentIni(true);
				btnIni(true);
				ogAgainst.setValue("Dues");
				clearData();
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
				updateButtonEvent();
				ogCashBank.setEnabled(false);
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				txtFromFindWindow.setValue("");
				findButtonEvent();
			}
		});

		cmbSupplierName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addPONo();
				selectDataDues();
			}
		});

		cmbPoNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addPOData();
			}
		});

		ogAgainst.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(ogAgainst.getValue().toString().equalsIgnoreCase("Dues"))
				{
					DuesPanel.setVisible(true);
					AdvancePanel.setVisible(false);
					PoPanel.setVisible(false);
					OtherPanel.setVisible(false);
				}
				if(ogAgainst.getValue().toString().equalsIgnoreCase("Advance"))
				{
					AdvancePanel.setVisible(true);
					DuesPanel.setVisible(false);
					PoPanel.setVisible(false);
					OtherPanel.setVisible(false);
				}
				if(ogAgainst.getValue().toString().equalsIgnoreCase("Po"))
				{
					PoPanel.setVisible(true);
					DuesPanel.setVisible(false);
					AdvancePanel.setVisible(false);
					OtherPanel.setVisible(false);
					addPONo();
				}
				if(ogAgainst.getValue().toString().equalsIgnoreCase("Others"))
				{
					DuesPanel.setVisible(false);
					AdvancePanel.setVisible(false);
					PoPanel.setVisible(false);
					OtherPanel.setVisible(true);
				}
				clearPayAgainst();
				selectDataDues();
			}
		});

		ogCashBank.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(ogCashBank.getValue().toString().equalsIgnoreCase("Cheque"))
				{
					ChequePanel.setVisible(true);
					cmbBankCashName.setWidth("300px");
					cmbBankCashName.setCaption("Bank Head : ");
					cmbChequeNo.setVisible(true);
					dChequeDate.setVisible(true);
				}
				if(ogCashBank.getValue().toString().equalsIgnoreCase("Cash"))
				{
					cmbBankCashName.setCaption("Cash Head : ");
					cmbChequeNo.setVisible(false);
					dChequeDate.setVisible(false);
					cmbBankCashName.setWidth("300px");
				}
				bankCashHeadIni();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		button.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSupplierName.getValue()!=null)
				{
					if(isApprove)
					{
						if(checkUser())
						{
							deleteAction();
						}
						else
						{
							showNotification("Debit Note is approved!","You aren't authorized.",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						deleteAction();
					}
				}
				else
				{
					showNotification("Warning!","There are no data to delete.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cmbBankCashName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addChequeNo();
			}
		});

		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				generateReference();				
			}
		});

		button.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbSupplierName.getValue()!=null)
				{
					alterVoucher();
					reportView();
				}
				else
				{
					showNotification("Warning!","There are no data to preview.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				advanceUpload(0,"");
			}
		});

		bpvUploadOther.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				advanceUpload(1,"");
			}
		});
	}

	public String advanceUpload(int flag, String filename)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;
		if(flag==0)
		{
			if(bpvUpload.fileName.trim().length()>0)
			{
				try 
				{
					if(bpvUpload.fileName.toString().endsWith(".jpg"))
					{
						fileMove(basePath+bpvUpload.fileName.trim(),advanceAttach+filename+".jpg");
						stuImage = advanceAttach+filename+".jpg";
					}
					else
					{
						fileMove(basePath+bpvUpload.fileName.trim(),advanceAttach+filename+".pdf");
						stuImage = advanceAttach+filename+".pdf";
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}
		if(flag==1)
		{
			if(bpvUploadOther.fileName.trim().length()>0)
			{
				try 
				{
					if(bpvUploadOther.fileName.toString().endsWith(".jpg"))
					{
						fileMove(basePath+bpvUploadOther.fileName.trim(),othersAttach+filename+".jpg");
						stuImage = othersAttach+filename+".jpg";
					}
					else
					{
						fileMove(basePath+bpvUploadOther.fileName.trim(),othersAttach+filename+".pdf");
						stuImage = othersAttach+filename+".pdf";
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
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

	private void deleteAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to delete information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					deleteData();
					showNotification("All information deleted successfully.");
					componentIni(true);
					btnIni(true);
					clearData();
				}
			}
		});
	}

	private void updateButtonEvent()
	{
		if(cmbSupplierName.getValue()!= null) 
		{
			isUpdate = true;
			if(isApprove)
			{
				if(checkUser())
				{
					btnIni(false);
					componentIni(false);
					DuesPanel.setEnabled(true);
					PoPanel.setEnabled(true);
					AdvancePanel.setEnabled(true);
					OtherPanel.setEnabled(true);
				}
				else
				{
					showNotification("Debit Note is approved!","You aren't authorized.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				btnIni(false);
				componentIni(false);
				DuesPanel.setEnabled(true);
				PoPanel.setEnabled(true);
				AdvancePanel.setEnabled(true);
				OtherPanel.setEnabled(true);
			}
		}
		else
		{
			showNotification("Warning!","Find data to edit.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private boolean checkUser()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vUserId from tbAuthoritySegregation where"
					+ " vUserId = '"+sessionBean.getUserId()+"' and vModuleId = '5'";
			Iterator<?> iterMax = session.createSQLQuery(sql).list().iterator();
			if(iterMax.hasNext())
			{
				return true;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}

	private void findButtonEvent()
	{
		Window win = new DebitNoteFindWindow(sessionBean,txtFromFindWindow,"DeliveryOrder");
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(txtFromFindWindow.getValue().toString().length()>0)
				{
					clearData();
					findInitialise(txtFromFindWindow.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String referenceNo) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String sql = "SELECT dDate,vVoucherNo,vSupplierId,vPaymentAgainst,vPaymentStatus,vModeOfPayment,mAmount, "
					+ "vPreparedBy,mCurrentBalance,dLastPaydate,mLastAmount,vAdvanceAttach,vAdvanceApproveBy,vPoNo,dPoDate,"
					+ "mPoAmount,vPoApproveBy,vOtherAttach,vOtherRemarks,vLedgerIdBankCash,vChequeNo,dChequeDate,iApproveFlag,"
					+ "vApproveBy,vApproveIp,dApproveTime,vNarration,vAdvanceAttach,vOtherAttach FROM tbDebitNote where "
					+ "vReferenceNo = '"+referenceNo+"'";
			List<?> led = session.createSQLQuery(sql).list();

			for(Iterator<?> iter = led.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				dDate.setValue(element[0]);
				txtRefferenceNo.setValue(referenceNo);
				txtVoucherNo.setValue(element[1].toString());
				txtTempVoucherNo.setValue(element[1].toString());
				cmbSupplierName.setValue(element[2].toString());
				ogAgainst.setValue(element[3].toString());
				ogFullPart.setValue(element[4].toString());
				ogCashBank.setValue(element[5].toString());
				txtAmount.setValue(new CommaSeparator().setComma(Double.parseDouble(element[6].toString())));
				txtPreparedBy.setValue(element[7].toString());
				txtCurrentBalance.setValue(new CommaSeparator().setComma(Double.parseDouble(element[8].toString())));
				dLastDate.setReadOnly(false);
				dLastDate.setValue(element[9]);
				dLastDate.setReadOnly(true);
				txtPaymentAmount.setValue(new CommaSeparator().setComma(Double.parseDouble(element[10].toString())));
				cmbApprovedBy.setValue(element[12].toString());
				cmbPoNo.setValue(element[13]);
				dPoDate.setReadOnly(false);
				dPoDate.setValue(element[14]);
				dPoDate.setReadOnly(true);
				txtPoAmount.setValue(new CommaSeparator().setComma(Double.parseDouble(element[15].toString())));
				txtPoApprovedBy.setValue(element[16]);
				txtOtherRemarks.setValue(element[18].toString());
				cmbBankCashName.setValue(element[19].toString());

				cmbChequeNo.addItem(element[20].toString());

				cmbChequeNo.setValue(element[20].toString());
				dChequeDate.setValue(element[21]);
				if(element[22].toString().equals("1") || element[22].toString().equals("2"))
				{
					isApprove = true;
				}
				else
				{
					isApprove = false;
				}

				cmbNarration.addItem(element[26].toString());
				cmbNarration.setValue(element[26].toString());

				advanceAttachFind = element[27].toString();
				othersAttachFind = element[28].toString();

				DuesPanel.setEnabled(false);
				PoPanel.setEnabled(false);
				AdvancePanel.setEnabled(false);
				OtherPanel.setEnabled(false);
			}
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void generateReference()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sl = "";
		try
		{
			String sql = "SELECT ISNULL((MAX(CAST(SUBSTRING(vReferenceNo,9,50) AS INT))+1),1) FROM tbDebitNote "
					+ "where MONTH(dDate) = '"+dfMonth.format(dDate.getValue())+"' and YEAR(dDate) = '"+dfYear.format(dDate.getValue())+"'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
				sl = iter.next().toString();
			txtRefferenceNo.setValue(dfReference.format(dDate.getValue()).toString()+charIncrease(sl,3));
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private String charIncrease(String id, int length)
	{
		String ret = id;
		while(ret.length()!=length)
		{
			ret = "0"+ret;
		}
		return ret;
	}

	private void cmbPartyData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select supplierId,supplierName from tbSupplierInfo where isActive = 1 order by supplierName";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplierName.addItem(element[0].toString());
				cmbSupplierName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbApproveByData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select userId,name from tbLogin where (isAdmin = 1 or isSuperAdmin = 1) and userId != 1 order by name";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbApprovedBy.addItem(element[1].toString());
				cmbApprovedBy.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbNarrationAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select NarrationId,Narration from tbNarrationList where VoucherType in ('BP','CP') order by Narration";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbNarration.addItem(element[1].toString());
				cmbNarration.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addPONo()
	{
		cmbPoNo.setValue(null);
		txtPoAmount.setValue("");
		txtPoApprovedBy.setValue("");
		cmbPoNo.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String supplier = (cmbSupplierName.getValue()!=null?cmbSupplierName.getValue().toString():"");
		try
		{
			String sql = "select 0 zero,poNo from tbRawPurchaseOrderInfo where supplierId = "
					+ "'"+supplier+"' order by poDate";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPoNo.addItem(element[1]);
				cmbPoNo.setItemCaption(element[1], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addPOData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select CONVERT(date, poDate) poDate,SUM(qty*rate) amount,userName from"
					+ " tbRawPurchaseOrderInfo rpi inner join tbRawPurchaseOrderDetails rpd on rpi.poNo ="
					+ " rpd.poNo where rpi.poNo = '"+(cmbPoNo.getValue()!=null?cmbPoNo.getValue().toString():"")+"'"
					+ " group by rpi.poNo,CONVERT(date, poDate),userName";
			List<?>list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				dPoDate.setReadOnly(false);
				dPoDate.setValue(element[0]);
				dPoDate.setReadOnly(true);
				txtPoAmount.setValue(new CommaSeparator().setComma(Double.parseDouble(element[1].toString())));
				txtPoApprovedBy.setValue(element[2].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void bankCashHeadIni()
	{
		cmbBankCashName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String bankCash = (ogCashBank.getValue().toString().equals("Cash")?"A7":"A8");
			String sql = "SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,"
					+ " ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) = '"+bankCash+"' AND companyId"
					+ " in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name";
			List<?>list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBankCashName.addItem(element[0].toString());
				cmbBankCashName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("BankCash Add :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addChequeNo()
	{
		cmbChequeNo.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String cashBank = (cmbBankCashName.getValue()!=null?cmbBankCashName.getValue().toString():"");
		try
		{
			String sql = "Select folioNo,0 zero from tbChequeBook where status = 'NO' and"
					+ " ledgerId = '"+cashBank+"' order by folioNo";
			List<?> bh = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbChequeNo.addItem(element[0].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Cheque Add :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void selectDataDues()
	{
		txtCurrentBalance.setValue("");
		txtPaymentAmount.setValue("");
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			alterVoucher();
			String sql = "select dbo.openingBal(v.Ledger_Id, '"+sessionBean.dfDb.format(dDate.getValue())+"', 'O', '"+sessionBean.getCompanyId()+"') Dues,"
					+ " DrAmount lastPayment, CONVERT(date,Date) dDate from vwVoucher v where Ledger_Id = (select ledgerCode"
					+ " from tbSupplierInfo where supplierId = '"+(cmbSupplierName.getValue()!=null?cmbSupplierName.getValue().toString():"")+"') order by Date asc";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				txtCurrentBalance.setValue(new CommaSeparator().setComma(Double.parseDouble(element[0].toString())));
				dLastDate.setReadOnly(false);
				dLastDate.setValue(element[2]);
				dLastDate.setReadOnly(true);
				txtPaymentAmount.setValue(new CommaSeparator().setComma(Double.parseDouble(element[1].toString())));
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(cmbSupplierName.getValue()!=null)
		{
			if(!txtRefferenceNo.getValue().toString().isEmpty())
			{
				if(!txtAmount.getValue().toString().isEmpty())
				{
					if(cmbBankCashName.getValue()!=null)
					{
						if(checkPaymentAgainst())
						{
							if(ogCashBank.getValue().toString().equals("Cash") || cmbChequeNo.getValue()!=null)
							{
								saveBtnAction();
							}
							else
							{
								showNotification("Warning!","Select cheque no.",Notification.TYPE_WARNING_MESSAGE);
								cmbChequeNo.focus();
							}
						}
					}
					else
					{
						showNotification("Warning!","Select "+(ogCashBank.getValue().toString().equals("Cash")?"Cash":
								"Bank")+" head.",Notification.TYPE_WARNING_MESSAGE);
						cmbBankCashName.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide amount.",Notification.TYPE_WARNING_MESSAGE);
					txtAmount.focus();
				}
			}
			else
			{
				showNotification("Warning!","No reference no found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Warning!","Select supplier name.",Notification.TYPE_WARNING_MESSAGE);
			cmbSupplierName.focus();
		}
	}

	private boolean checkPaymentAgainst()
	{
		String payment = ogAgainst.getValue().toString();
		if(payment.equals("Advance"))
		{
			if(cmbApprovedBy.getValue()!=null)
			{
				return true;
			}
			else
			{
				showNotification("Warning!","Select approved by.",Notification.TYPE_WARNING_MESSAGE);
				cmbApprovedBy.focus();
			}
		}
		else if(payment.equals("PO"))
		{
			if(cmbPoNo.getValue()!=null)
			{
				return true;
			}
			else
			{
				showNotification("Warning!","Select PO No.",Notification.TYPE_WARNING_MESSAGE);
				cmbPoNo.focus();
			}
		}
		else
		{
			return true;
		}

		return false;
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
						deleteData();
						insertData();
						showNotification("All information updated successfully.");
						componentIni(true);
						btnIni(true);
						//clearData();
					}
				}
			});
		}
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?",
					new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						insertData();
						showNotification("All information saved successfully.");
						componentIni(true);
						btnIni(true);
						//clearData();
					}
				}
			});
		}
	}

	private void deleteData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+sessionBean.dfDb.format(dDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "Voucher"+fsl;
			String cheque =  "ChequeDetails"+fsl;

			String queryDebit = " delete from tbDebitNote where vReferenceNo = '"+txtRefferenceNo.getValue().toString()+"' ";
			session.createSQLQuery(queryDebit).executeUpdate();

			String queryVoucher = " delete from "+voucher+" where Voucher_No = '"+txtTempVoucherNo.getValue().toString()+"' ";
			session.createSQLQuery(queryVoucher).executeUpdate();

			String sqlCheque = " update tbChequeBook set status = 'NO' where ledgerId = '"+cmbBankCashName.getValue()+"'"
					+ " and folioNo = (select Cheque_No from ChequeDetails1 where Voucher_No = '"+txtTempVoucherNo.getValue().toString()+"') ";
			session.createSQLQuery(sqlCheque).executeUpdate();

			String queryCheque = " delete from "+cheque+" where Voucher_No = '"+txtTempVoucherNo.getValue().toString()+"' ";
			session.createSQLQuery(queryCheque).executeUpdate();
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("delete Error",""+ex,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		String voucherQuery = "";
		String voucherNo = "";
		String fileAdvance = "0";
		String fileOther = "0";

		String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+sessionBean.dfDb.format(dDate.getValue())+"')").list().iterator().next().toString();
		String voucher = "Voucher"+fsl;
		String cheque = "ChequeDetails"+fsl;

		if(!isUpdate)
		{
			if(ogCashBank.getValue().toString().equals("Cash"))
			{
				voucherQuery = "SELECT 'DR-CH-'+CAST(ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1)as varchar)"
						+ " FROM "+voucher+" WHERE vouchertype = 'dca' and CompanyId = '"+ sessionBean.getCompanyId() +"'";
			}
			else
			{
				voucherQuery = "SELECT 'DR-BK-'+CAST(ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1)as varchar)"
						+ " FROM "+voucher+" WHERE vouchertype = 'dba' and CompanyId = '"+ sessionBean.getCompanyId() +"'";
			}
			Iterator<?> iter = session.createSQLQuery(voucherQuery).list().iterator();
			if(iter.hasNext())
				voucherNo = iter.next().toString();

			if(ogAgainst.getValue().toString().equals("Advance"))
			{
				fileAdvance = advanceUpload(0,txtRefferenceNo.getValue().toString())==null? "0":advanceUpload(0,txtRefferenceNo.getValue().toString());
			}
			if(ogAgainst.getValue().toString().equals("Others"))
			{
				fileOther = advanceUpload(1,txtRefferenceNo.getValue().toString())==null? "0":advanceUpload(1,txtRefferenceNo.getValue().toString());
			}
		}
		else
		{
			fileAdvance = advanceAttachFind;
			fileOther = othersAttachFind;
			voucherNo = txtTempVoucherNo.getValue().toString();
		}

		//Credit Entry to voucher
		String sqlCredit = "INSERT INTO "+voucher+"(Voucher_No, Date, Ledger_Id, Narration, DrAmount, CrAmount, vouchertype, "
				+ "TransactionWith, costId, userId, userIp, entryTime, auditapproveflag, companyId, attachBill, "
				+ "attachChequeBill) VALUES ('"+voucherNo+"','"+sessionBean.dfDb.format(dDate.getValue())+"',"
				+ "'"+cmbBankCashName.getValue().toString()+"','"+(cmbNarration.getValue()==null?"":cmbNarration.getValue().toString())+"','0',"
				+ "'"+txtAmount.getValue().toString().replaceAll(",", "")+"','"+(ogCashBank.getValue().toString().equals("Cash")?"dca":"dba")+"',"
				+ "'"+(cmbSupplierName.getItemCaption(cmbSupplierName.getValue()).toString())+"',"
				+ "'U-1','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,"
				+ " 0, '"+ sessionBean.getCompanyId() +"', '"+fileAdvance+"', '"+fileOther+"')";
		session.createSQLQuery(sqlCredit).executeUpdate();

		//Debit Entry to voucher
		String sqlDebit = "INSERT INTO "+voucher+"(Voucher_No, Date, Ledger_Id, Narration, DrAmount, CrAmount, vouchertype, "
				+ "TransactionWith, costId, userId, userIp, entryTime, auditapproveflag, companyId, attachBill, "
				+ "attachChequeBill) VALUES ('"+voucherNo+"','"+sessionBean.dfDb.format(dDate.getValue())+"',"
				+ "(select ledgerCode from tbSupplierInfo where supplierId = '"+cmbSupplierName.getValue().toString()+"'),"
				+ "'"+(cmbNarration.getValue()==null?"":cmbNarration.getValue().toString())+"','"+txtAmount.getValue().toString().replaceAll(",", "")+"'"
				+ ",'0','"+(ogCashBank.getValue().toString().equals("Cash")?"dca":"dba")+"',"
				+ "'"+(cmbSupplierName.getItemCaption(cmbSupplierName.getValue()).toString())+"',"
				+ "'U-1','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,"
				+ " 0, '"+ sessionBean.getCompanyId() +"', '"+fileAdvance+"', '"+fileOther+"')";
		session.createSQLQuery(sqlDebit).executeUpdate();

		//Cheque No insert to voucher
		if(ogCashBank.getValue().toString().equals("Cheque"))
		{
			String sqlCheque = "INSERT INTO "+cheque+"(Cheque_No, Cheque_Date, Voucher_No, Bank_Id, companyId, flag) VALUES ("
					+ "'"+(cmbChequeNo.getValue()!=null?cmbChequeNo.getValue().toString():"")+"',"
					+ "'"+sessionBean.dfDb.format(dChequeDate.getValue())+"',"
					+ "'"+voucherNo+"','"+cmbBankCashName.getValue().toString().trim()+"',"
					+ "'"+ sessionBean.getCompanyId().toString().trim() +"', 'Cheque')";
			session.createSQLQuery(sqlCheque).executeUpdate();

			String sqlC = " update tbChequeBook set status = 'YES' where ledgerId = '"+cmbBankCashName.getValue()+"'"
					+ " and folioNo = '"+(cmbChequeNo.getValue()!=null?cmbChequeNo.getValue().toString().trim():"")+"' ";
			session.createSQLQuery(sqlC).executeUpdate();
		}

		try
		{
			String query = " insert into tbDebitNote(dDate,vReferenceNo,vVoucherNo,vSupplierId,vSupplierName,vPaymentAgainst,"
					+ "vPaymentStatus,vModeOfPayment,mAmount,vPreparedBy,mCurrentBalance,dLastPaydate,mLastAmount,vAdvanceAttach,"
					+ "vAdvanceApproveBy,vPoNo,dPoDate,mPoAmount,vPoApproveBy,vOtherAttach,vOtherRemarks,vLedgerIdBankCash,"
					+ "vLedgerName,vChequeNo,dChequeDate,vApproveBy,vApproveIp,vUserName,vUserIp,dEntryTime,"
					+ "iApproveFlag,vNarration,vAuditBy,vAuditIp,iPrint) values ("
					+ "'"+sessionBean.dfDb.format(dDate.getValue())+"',"
					+ "'"+txtRefferenceNo.getValue().toString()+"',"
					+ "'"+voucherNo+"',"
					+ "'"+cmbSupplierName.getValue().toString()+"',"
					+ "'"+cmbSupplierName.getItemCaption(cmbSupplierName.getValue()).toString()+"',"
					+ "'"+ogAgainst.getValue().toString()+"',"
					+ "'"+ogFullPart.getValue().toString()+"',"
					+ "'"+ogCashBank.getValue().toString()+"',"
					+ "'"+txtAmount.getValue().toString().replaceAll(",", "")+"',"
					+ "'"+txtPreparedBy.getValue().toString()+"',"
					+ "'"+(txtCurrentBalance.getValue().toString().isEmpty()?"0":txtCurrentBalance.getValue().toString().replaceAll(",", ""))+"',"
					+ "'"+sessionBean.dfDb.format(dLastDate.getValue())+"',"
					+ "'"+(txtPaymentAmount.getValue().toString().isEmpty()?"0":txtPaymentAmount.getValue().toString().replaceAll(",", ""))+"',"
					+ "'"+fileAdvance+"',"
					+ "'"+(cmbApprovedBy.getValue()==null?"0":cmbApprovedBy.getValue().toString())+"',"
					+ "'"+(cmbPoNo.getValue()==null?"":cmbPoNo.getValue().toString())+"',"
					+ "'"+sessionBean.dfDb.format(dPoDate.getValue())+"',"
					+ "'"+(txtPoAmount.getValue().toString().isEmpty()?"0":txtPoAmount.getValue().toString().replaceAll(",", ""))+"',"
					+ "'"+(txtPoApprovedBy.getValue().toString().isEmpty()?"":txtPoApprovedBy.getValue().toString().replaceAll(",", ""))+"',"
					+ "'"+fileOther+"',"
					+ "'"+(txtOtherRemarks.getValue().toString().isEmpty()?"":txtOtherRemarks.getValue().toString())+"',"
					+ "'"+cmbBankCashName.getValue().toString()+"',"
					+ "'"+cmbBankCashName.getItemCaption(cmbBankCashName.getValue()).toString()+"',"
					+ "'"+(cmbChequeNo.getValue()==null?"":cmbChequeNo.getValue().toString())+"',"
					+ "'"+sessionBean.dfDb.format(dChequeDate.getValue())+"',"
					+ "'','',"
					+ "'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,0,"
					+ "'"+(cmbNarration.getValue()==null?"":cmbNarration.getValue().toString())+"','','',"
					+ "'"+(ogCashBank.getValue().toString().equals("Cash")?"1":"0")+"' )";

			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			txtVoucherNo.setValue(voucherNo);
			txtTempVoucherNo.setValue(voucherNo);
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Save Error",""+ex,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void componentIni(boolean b)
	{
		dDate.setEnabled(!b);
		cmbSupplierName.setEnabled(!b);
		txtRefferenceNo.setEnabled(!b);
		txtVoucherNo.setEnabled(!b);
		ogAgainst.setEnabled(!b);
		ogFullPart.setEnabled(!b);
		ogCashBank.setEnabled(!b);
		txtAmount.setEnabled(!b);
		txtPreparedBy.setEnabled(!b);
		DuesPanel.setEnabled(!b);
		ChequePanel.setEnabled(!b);
		cmbNarration.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnDelete.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnFind.setEnabled(t);
		button.btnPreview.setEnabled(t);
	}

	private void clearData()
	{
		advanceAttachFind = "";
		othersAttachFind = "";
		cmbSupplierName.setValue(null);
		txtVoucherNo.setValue("");
		txtTempVoucherNo.setValue("");
		txtRefferenceNo.setValue("");
		ogAgainst.setValue("Dues");
		ogCashBank.setValue("Cash");
		ogFullPart.setValue("Part");
		txtAmount.setValue("");

		cmbApprovedBy.setValue(null);
		cmbPoNo.setValue(null);
		txtPoAmount.setValue("");
		txtPoApprovedBy.setValue("");
		txtCurrentBalance.setValue("");
		txtPaymentAmount.setValue("");
		cmbBankCashName.setValue(null);
		cmbChequeNo.setValue(null);
		txtOtherRemarks.setValue("");
		isApprove = false;
		cmbNarration.setValue(null);
		txtPreparedBy.setValue(sessionBean.getUserName());
	}

	private void clearPayAgainst()
	{
		cmbApprovedBy.setValue(null);
		cmbPoNo.setValue(null);
		txtPoAmount.setValue("");
		txtPoApprovedBy.setValue("");
		txtCurrentBalance.setValue("");
		txtPaymentAmount.setValue("");
		cmbBankCashName.setValue(null);
		cmbChequeNo.setValue(null);
		txtOtherRemarks.setValue("");
		//bpvUpload.set
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1000px");
		mainLayout.setHeight("500px");

		lblDate = new Label("Date : ");
		mainLayout.addComponent(lblDate, "top:30.0px; left:15.00px;");

		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setImmediate(true);
		mainLayout.addComponent(dDate, "top:28.0px; left:130.0px;");

		lblDate = new Label("Voucher No : ");
		mainLayout.addComponent(lblDate, "top:60.0px; left:15.00px;");

		txtVoucherNo = new TextRead();
		txtVoucherNo.setImmediate(true);
		txtVoucherNo.setWidth("150px");
		txtVoucherNo.setHeight("23px");
		mainLayout.addComponent(txtVoucherNo, "top:58.0px; left:130.0px;");

		txtTempVoucherNo = new TextRead();
		txtTempVoucherNo.setImmediate(true);
		txtTempVoucherNo.setWidth("150px");
		txtTempVoucherNo.setHeight("23px");
		mainLayout.addComponent(txtTempVoucherNo, "top:58.0px; left:130.0px;");
		txtTempVoucherNo.setVisible(false);

		lblSupplierHead = new Label("Supplier Name : ");
		mainLayout.addComponent(lblSupplierHead, "top:90.0px; left:15.00px;");

		cmbSupplierName = new ComboBox();
		cmbSupplierName.setImmediate(true);
		cmbSupplierName.setWidth("310px");
		cmbSupplierName.setHeight("-1px");
		cmbSupplierName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSupplierName, "top:88.0px; left:130.0px;");

		lblRefferenceNo = new Label("Reference No : ");
		lblRefferenceNo.setWidth("-1px");
		lblRefferenceNo.setHeight("-1px");
		mainLayout.addComponent(lblRefferenceNo, "top:120.0px; left:15.00px;");

		txtRefferenceNo = new TextRead();
		txtRefferenceNo.setImmediate(true);
		txtRefferenceNo.setWidth("180px");
		txtRefferenceNo.setHeight("23px");
		mainLayout.addComponent(txtRefferenceNo, "top:118.0px; left:130.0px;");

		lblPaymentAgainst = new Label("Payment Against : ");
		mainLayout.addComponent(lblPaymentAgainst, "top:150.0px; left:15.00px;");

		ogAgainst.setImmediate(true);
		ogAgainst.setWidth("-1px");
		ogAgainst.setHeight("-1px");
		ogAgainst.setStyleName("horizontal");
		mainLayout.addComponent(ogAgainst, "top:148.0px;left:130.0px;");

		lblPaymentStatus = new Label("Payment Status : ");
		mainLayout.addComponent(lblPaymentStatus, "top:180.0px; left:15.00px;");

		ogFullPart.setImmediate(true);
		ogFullPart.setStyleName("horizontal");
		mainLayout.addComponent(ogFullPart, "top:178.0px;left:130.0px;");

		lblModeOfPayment = new Label("Mode of Payment : ");
		lblModeOfPayment.setWidth("-1px");
		lblModeOfPayment.setHeight("-1px");
		mainLayout.addComponent(lblModeOfPayment, "top:210.0px; left:15.00px;");

		ogCashBank.setImmediate(true);
		ogCashBank.setStyleName("horizontal");
		mainLayout.addComponent(ogCashBank, "top:208.0px;left:130.0px;");

		lblAmount = new Label("Amount : ");
		mainLayout.addComponent(lblAmount, "top:240.0px; left:15.00px;");

		txtAmount = new AmountCommaSeperator();
		txtAmount.setImmediate(true);
		txtAmount.setWidth("150px");
		txtAmount.setHeight("-1px");
		mainLayout.addComponent(txtAmount, "top:238.0px; left:130.0px;");

		lblPreparedBy = new Label("Prepared By : ");
		mainLayout.addComponent(lblPreparedBy, "top:270.0px; left:15.00px;");

		txtPreparedBy = new TextRead();
		txtPreparedBy.setImmediate(true);
		txtPreparedBy.setWidth("150px");
		txtPreparedBy.setHeight("23px");
		mainLayout.addComponent(txtPreparedBy, "top:268.0px; left:130.0px;");

		lblPreparedBy = new Label("Narration : ");
		mainLayout.addComponent(lblPreparedBy, "top:300.0px; left:15.00px;");

		cmbNarration = new ComboBox();
		cmbNarration.setImmediate(true);
		cmbNarration.setWidth("310px");
		cmbNarration.setHeight("-1px");
		cmbNarration.setNewItemsAllowed(true);
		cmbNarration.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbNarration, "top:298.0px; left:130.0px;");

		DuesPanel = new Panel();
		DuesPanel.setWidth("500px");
		DuesPanel.setHeight("220px");
		DuesPanel.setImmediate(true);

		txtCurrentBalance = new TextRead("Current Balance : ");
		txtCurrentBalance.setImmediate(true);
		txtCurrentBalance.setWidth("150px");
		txtCurrentBalance.setHeight("-1px");
		txtCurrentBalance.setStyleName("fright");

		dLastDate = new PopupDateField("Last Date Of Payment : ");
		dLastDate.setWidth("110px");
		dLastDate.setDateFormat("dd-MM-yyyy");
		dLastDate.setValue(new java.util.Date());
		dLastDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dLastDate.setReadOnly(true);
		dLastDate.setStyleName("fright");

		txtPaymentAmount = new TextRead("Payment Amount : ");
		txtPaymentAmount.setImmediate(true);
		txtPaymentAmount.setWidth("150px");
		txtPaymentAmount.setHeight("-1px");
		txtPaymentAmount.setStyleName("fright");

		fLayoutDues.addComponent(txtCurrentBalance);
		fLayoutDues.addComponent(dLastDate);
		fLayoutDues.addComponent(txtPaymentAmount);

		DuesPanel.addComponent(fLayoutDues);
		mainLayout.addComponent(DuesPanel, "top:10px;right:30px;");

		AdvancePanel = new Panel();
		AdvancePanel.setWidth("500px");
		AdvancePanel.setHeight("180px");
		AdvancePanel.setImmediate(true);

		cmbApprovedBy = new ComboBox("Approved By : ");
		cmbApprovedBy.setImmediate(true);
		cmbApprovedBy.setWidth("150px");
		cmbApprovedBy.setHeight("-1px");

		fLayoutAdvance.addComponent(bpvUpload);
		fLayoutAdvance.addComponent(cmbApprovedBy);

		AdvancePanel.addComponent(fLayoutAdvance);
		mainLayout.addComponent(AdvancePanel, "top:10px;right:30px;");

		PoPanel = new Panel();
		PoPanel.setWidth("500px");
		PoPanel.setHeight("260px");
		PoPanel.setImmediate(true);

		cmbPoNo = new ComboBox("PO No : ");
		cmbPoNo.setImmediate(true);
		cmbPoNo.setWidth("220px");
		cmbPoNo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbPoNo.setHeight("-1px");

		dPoDate = new PopupDateField("PO Date : ");
		dPoDate.setWidth("110px");
		dPoDate.setDateFormat("dd-MM-yyyy");
		dPoDate.setValue(new java.util.Date());
		dPoDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dPoDate.setReadOnly(true);

		txtPoAmount = new TextRead("PO Amount : ");
		txtPoAmount.setImmediate(true);
		txtPoAmount.setWidth("150px");
		txtPoAmount.setHeight("-1px");
		txtPoAmount.setStyleName("fright");

		txtPoApprovedBy = new TextRead("Approved By : ");
		txtPoApprovedBy.setImmediate(true);
		txtPoApprovedBy.setWidth("150px");
		txtPoApprovedBy.setHeight("-1px");

		fLayoutPo.addComponent(cmbPoNo);
		fLayoutPo.addComponent(dPoDate);
		fLayoutPo.addComponent(txtPoAmount);
		fLayoutPo.addComponent(txtPoApprovedBy);

		PoPanel.addComponent(fLayoutPo);
		mainLayout.addComponent(PoPanel, "top:10px;right:30px;");

		OtherPanel = new Panel();
		OtherPanel.setWidth("500px");
		OtherPanel.setHeight("180px");
		OtherPanel.setImmediate(true);

		txtOtherRemarks = new TextField("Remarks : ");
		txtOtherRemarks.setImmediate(true);
		txtOtherRemarks.setWidth("380px");
		txtOtherRemarks.setHeight("50px");

		fLayoutOther.addComponent(bpvUploadOther);
		fLayoutOther.addComponent(txtOtherRemarks);

		OtherPanel.addComponent(fLayoutOther);
		mainLayout.addComponent(OtherPanel, "top:10px;right:30px;");

		ChequePanel = new Panel();
		ChequePanel.setWidth("500px");
		ChequePanel.setHeight("180px");
		ChequePanel.setImmediate(true);

		cmbBankCashName = new ComboBox("Bank Head : ");
		cmbBankCashName.setImmediate(true);
		cmbBankCashName.setWidth("300px");
		cmbBankCashName.setHeight("-1px");
		cmbBankCashName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);

		cmbChequeNo = new ComboBox("Cheque No : ");
		cmbChequeNo.setImmediate(true);
		cmbChequeNo.setWidth("200px");
		cmbChequeNo.setHeight("-1px");
		cmbChequeNo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);

		dChequeDate = new PopupDateField("Cheque Date : ");
		dChequeDate.setWidth("110px");
		dChequeDate.setDateFormat("dd-MM-yyyy");
		dChequeDate.setValue(new java.util.Date());
		dChequeDate.setResolution(PopupDateField.RESOLUTION_DAY);

		fLayoutCheque.addComponent(cmbBankCashName);
		fLayoutCheque.addComponent(cmbChequeNo);
		fLayoutCheque.addComponent(dChequeDate);

		ChequePanel.addComponent(fLayoutCheque);
		mainLayout.addComponent(ChequePanel,"top:260px;right:30px;");

		mainLayout.addComponent(button,"top:460.0px; left:150.0px");

		return mainLayout;
	}

	private void alterVoucher()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			if(!txtVoucherNo.getValue().toString().isEmpty())
			{
				String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+sessionBean.dfDb.format(dDate.getValue())+"')").list().iterator().next().toString();
				session.createSQLQuery("exec prcAlterVoucher "+fsl+"").executeUpdate();
				tx.commit();
			}
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Warning","There are no data for Preview",Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportView()
	{
		try
		{
			String subQuery = "";
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("path", "./report/account/voucher/");
			hm.put("urlLink", this.getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", ""));
			String query = "select vSupplierId,vSupplierName,(select address from tbSupplierInfo si where"+
					" dn.vSupplierId = si.supplierId )adress,dLastPaydate,mAmount,vReferenceNo,vVoucherNo,"+
					" mCurrentBalance,dDate,vPoNo,dPoDate,mPoAmount,vLedgerName,vChequeNo,dChequeDate,vPaymentAgainst,"+
					" vPaymentStatus,vModeOfPayment,mAmount,vPreparedBy,vAuditBy,vApproveBy from tbDebitNote dn "+
					" where vReferenceNo = '"+txtRefferenceNo.getValue().toString()+"'";
			if(ogCashBank.getValue().toString().equals("Cash"))
			{
				subQuery = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+txtVoucherNo.getValue().toString()+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' order by DrAmount desc";
			}
			else
			{
				subQuery = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+txtVoucherNo.getValue().toString()+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount DESC";
			}
			hm.put("sql", query);
			hm.put("subsql", subQuery);
			Window win = new ReportViewer(hm,"report/account/voucher/rptDebitNote.jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false); 
			win.setStyleName("cwindow");
			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}