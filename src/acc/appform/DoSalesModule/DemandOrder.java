package acc.appform.DoSalesModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.FinishedGoodsModule.FinishedGoodsInformation;
import acc.appform.FinishedGoodsModule.ProductInformation;
import acc.appform.setupTransaction.PartyInformation;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExample;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class DemandOrder extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private Table table = new Table();

	private Label lblDote ;

	private Label lblDono,lblpartyname,lbladdress,lbldeliveryDate,lblMobileNo;

	private Label lblCommission;
	private AmountField txtCommission;
	private NativeButton btnParty;
	private NativeButton btnProduct;

	private DecimalFormat dfAmount = new DecimalFormat("#0.00");
	private DecimalFormat dfRate = new DecimalFormat("#0.000");
	SimpleDateFormat dateFYMD = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFDMY = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	SimpleDateFormat recDateFormat = new SimpleDateFormat("MM/yy");

	private TextField txtDoNo;
	private PopupDateField dDoDate;
	private ComboBox cmbPartyname,cmbStatus;
	private TextField txtaddress;
	private TextField txtMobileNo;
	private PopupDateField dDeliveryDate,dInActive;
	private static final String[] cities = new String[] { "Active", "Inactive" };
	TextArea txtInActioveRemarks;
	//private Label lblTollarance;
	private AmountField txtTollarance;
	//private CheckBox chkWithTax;
	
	Label lblInactiveDate=new Label("Inactive Date :");
	Label lblInactioveRemarks=new Label("Remarks :");

	private ArrayList<Label> lblsa = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProductName = new ArrayList<ComboBox>();
	private ArrayList<Label> lblUnit = new ArrayList<Label>();
	private ArrayList<Label> lblproductCode = new ArrayList<Label>();
	private ArrayList<AmountCommaSeperator> txtqty = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> txtrate = new ArrayList<TextRead>();
	private ArrayList<TextRead> txtAmounttb = new ArrayList<TextRead>();
	private ArrayList<TextField> txtRemarks = new ArrayList<TextField>();
	ArrayList<Component> allComp = new ArrayList<Component>();	

	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "", "Exit");

	String username = "";
	String DONo = "";
	String maxId = "0";
	String GetDONo = "";
	String flag = "0";

	private boolean isUpdate = false;
	private boolean isFind = false;
	private boolean isApprove = false;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private TextField txtFromFindWindow = new TextField();

	private ImmediateUploadExample bpvUpload = new ImmediateUploadExample("");
	Button btnPreview;
	String poPdf = "",filePathTmp = "",imageLoc = "0";
	
	private Label lblNote1;
	private Label lblNote2;
	private Label lblNote3;
	private Label lblNote4;
	private Label lblNote5;
	//private TextField txtNote;
	
	private TextField txtNote1;
	private TextField txtNote2;
	private TextField txtNote3;
	private TextField txtNote4;
	private TextField txtNote5;
	
	private TextRead txtcolorBox1=new TextRead();
	private TextRead txtcolorBox2=new TextRead();
	private TextRead txtcolorBox3=new TextRead();
	private TextRead txtcolorBox4=new TextRead();
	private TextRead txtcolorBox5=new TextRead();
	
	public DemandOrder(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("PURCHASE ORDER INFORMATION :: " + sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		partyNameData();
		tableinitialise(4);
		btnIni(true);
		componentIni(true);
		SetEventAction(); 
		authenticationCheck();
		button.btnNew.focus();
		focusEnter();
		cmbStatus.setVisible(false);
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
	public void cmbStatusAction(){
		dInActive.setValue(new Date());
		txtInActioveRemarks.setValue("");
		dInActive.setVisible(true);
		txtInActioveRemarks.setVisible(true);
		lblInactiveDate.setVisible(true);
		lblInactioveRemarks.setVisible(true);
	}
	public void statusWiseClear(){
		dInActive.setValue(new Date());
		txtInActioveRemarks.setValue("");
		dInActive.setVisible(false);
		txtInActioveRemarks.setVisible(false);
		lblInactiveDate.setVisible(false);
		lblInactioveRemarks.setVisible(false);
		
	}
	public boolean dateValidation(){
		Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Date dateInactive=(Date) dInActive.getValue();//
        Date datePoDate=(Date) dDoDate.getValue();
        cal1.setTime(datePoDate);
        cal2.setTime(dateInactive);
       if(cal1.after(cal2)){
            System.out.println("dateInactive is after CurrentDate");
            return false;
        }
        if(cal1.before(cal2)){
            System.out.println("dateInactive is before CurrentDate");
            return true;
        }
        if(cal1.equals(cal2)){
            System.out.println("dateInactive is equal CurrentDate");
            return true;
        }
        return false;
	}
	
	private void SetEventAction()
	{
		
		cmbStatus.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbStatus.getValue()!=null){
					if(cmbStatus.getValue().toString().equals("Inactive")){
						cmbStatusAction();
					}
					else{
						statusWiseClear();
					}
				}
				else{
					statusWiseClear();
				}
			}
		});
		
		dInActive.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(!dateValidation()){
					dInActive.setValue(new Date());
					showNotification(null,"Inactive date should greater than PO date",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;
				newButtonEvent();
				cmbPartyname.focus();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				refButtonEvent();
				isUpdate = false;
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				updateButtonEvent();
			}
		});

		button.btnDelete.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				deleteButtonEvent();
			}
		});

		cmbPartyname.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				txtaddress.setValue("");
				txtMobileNo.setValue("");
				tableClear();

				setPartyData();
				setProductData();
				txtDoNo.focus();
			}
		});

		btnParty.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				bankHeadLink();
			}
		});

		btnProduct.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				productLink();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(!txtDoNo.getValue().toString().isEmpty())
				{
					if(cmbPartyname.getValue()!=null)
					{
						if(cmbProductName.get(0).getValue()!=null)
						{
							if(!txtqty.get(0).getValue().toString().isEmpty())
							{
								if(cmbStatus.getValue().toString().trim().equals("Inactive")){ 
									if(!txtInActioveRemarks.getValue().toString().isEmpty()){
										saveBtnAction();
									}
									else{
										showNotification("Warning!","Provide Inactive remarks.", Notification.TYPE_WARNING_MESSAGE);
										txtInActioveRemarks.focus();
									}
								}
								else{
									saveBtnAction();
								}
							}
							else
							{
								showNotification("Warning!","Provide product qty.", Notification.TYPE_WARNING_MESSAGE);
								txtqty.get(0).focus();
							}
						}
						else
						{
							showNotification("Warning!","Select product name.", Notification.TYPE_WARNING_MESSAGE);
							cmbProductName.get(0).focus();
						}
					}
					else
					{
						showNotification("Warning!","Select party name.", Notification.TYPE_WARNING_MESSAGE);
						cmbPartyname.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide PO No",Notification.TYPE_WARNING_MESSAGE);
					txtDoNo.focus();
				}
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

		button.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!txtDoNo.getValue().toString().trim().isEmpty())
				{
					reportView();
				}
				else 
				{
					showNotification("Warning!","There are nothing to preview", Notification.TYPE_WARNING_MESSAGE);	
				}
			}
		});

		btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				// Hyperlink to a given URL
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
					getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
				}
				if(isUpdate)
				{
					if(!bpvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", imageLoc.substring(22, imageLoc.length()));
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});

		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"","");
			}
		});

		button.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		txtDoNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtDoNo.getValue().toString().trim().isEmpty())
				{
					if(doublePoCheck() && !isFind)
					{
						txtDoNo.setValue("");
						showNotification("Warning!","PO no already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtDoNo.focus();
					}
				}
			}
		});
	}

	public void partyNameData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> lst = session.createSQLQuery(" select partyCode,partyName from tbPartyInfo" +
					" where isActive = '1' order by partyName ").list();
			cmbPartyname.removeAllItems();
			for(Iterator<?> iter = lst.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyname.addItem(element[0].toString());
				cmbPartyname.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
	}

	public boolean doublePoCheck()
	{
		boolean ret = false;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			Iterator<?> iterMax = session.createSQLQuery(" select doNo from tbDemandOrderInfo where doNo =" +
					" '"+txtDoNo.getValue().toString()+"' ").list().iterator();
			if(iterMax.hasNext())
			{ret = true;}
		}
		catch (Exception e)
		{}
		finally{session.close();}
		return ret;
	}

	private void setPartyData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> list = session.createSQLQuery(" select partyCode,address,mobile from tbPartyInfo" +
					" where partyCode = '"+cmbPartyname.getValue()+"' ").list();
			Iterator<?> iter = list.iterator();
			if(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				txtaddress.setValue(element[1].toString());
				txtMobileNo.setValue(element[2].toString());
			}
		}
		catch(Exception ex)
		{}
		finally{session.close();}
	}

	public void productLink()
	{
		Window win = new FinishedGoodsInformation(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				setProductData();
			}
		});
		this.getParent().addWindow(win);
	}

	public void bankHeadLink()
	{
		Window win = new PartyInformation(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				partyNameData();
			}
		});
		this.getParent().addWindow(win);
	}

	private void findButtonEvent()
	{
		Window win = new DemandFindWindow(sessionBean,txtFromFindWindow,"DeliveryOrder");
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(txtFromFindWindow.getValue().toString().length()>0)
				{
					isFind = true;
					isApprove = false;
					tableClear();
					findInitialise(txtFromFindWindow);
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void updateButtonEvent()
	{
		if(cmbPartyname.getValue()!= null) 
		{
			if(isApprove)
			{
				if(checkUser())
				{
					btnIni(false);
					componentIni(false);

					if(flag.equals("1"))
					{
						txtDoNo.setEnabled(false);
						cmbPartyname.setEnabled(false);
						btnParty.setEnabled(false);
						btnProduct.setEnabled(false);
					}
				}
				else
				{
					showNotification("PO is approved!","You aren't proper authenticate to do this operation.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				btnIni(false);
				componentIni(false);

				if(flag.equals("1"))
				{
					txtDoNo.setEnabled(false);
					cmbPartyname.setEnabled(false);
					btnParty.setEnabled(false);
					btnProduct.setEnabled(false);
				}
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
					+ " vUserId = '"+sessionBean.getUserId()+"' and vModuleId = '4'";
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

	private void deleteButtonEvent()
	{
		if(!txtDoNo.getValue().toString().isEmpty())
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				Iterator<?> iter = session.createSQLQuery("select vChallanNo from tbDeliveryChallanDetails where vDoNo ='"+txtDoNo.getValue().toString()+"'"+
						" select vProductId from tbDeliveryChallanDetails where vDoNo = '"+txtDoNo.getValue().toString()+"'").list().iterator();
				if(!iter.hasNext())
				{
					MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
					mb.show(new EventListener()
					{
						public void buttonClicked(ButtonType buttonType)
						{
							Session session = SessionFactoryUtil.getInstance().openSession();
							Transaction tx = session.beginTransaction();
							if(buttonType == ButtonType.YES)
							{
								try {
									deleteData(session,tx);
									tx.commit();
									showNotification("All Information Deleted Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
									txtClear();
									tableClear();
								}
								catch (Exception e){
									if(tx!=null){
										tx.commit();
									}
								}
								finally{
									if(session!=null){
										session.close();
									}
								}
								
							}
						}
					});
				}
				else
				{
					showNotification("Warning!","PO already in use.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			catch(Exception exp)
			{
				showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}
		else
		{
			showNotification("Warning!","Find PO first.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private String imagePath(int flag,String str,String fiscalYearNo)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+
				"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;
		if(flag==0)
		{
			if(bpvUpload.fileName.trim().length()>0)
			{
				try
				{
					if(bpvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"PO";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+path+".jpg");
						poPdf = SessionBean.imagePath+path+".jpg";
						filePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"PO";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+path+".pdf");
						poPdf = SessionBean.imagePath+path+".pdf";
						filePathTmp = path+".pdf";
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return poPdf;
		}
		if(flag==1)
		{
			if(bpvUpload.fileName.trim().length()>0)
			{
				try
				{
					if(bpvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/purchaseOrder/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/purchaseOrder/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/purchaseOrder/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/purchaseOrder/"+path+".pdf";
					}
				}
				catch (IOException e)
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
			{f1.delete();}
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
	private void findInitialise(TextField idAndName) 
	{
		String id = idAndName.getValue().toString().substring(0,idAndName.getValue().toString().indexOf("@"));
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String sql = "select partyId,deiveryDate,a.doNo,doDate,address,mobile,commission,note,BankId,Bank,productId,"
					+ " unit,qty,rate,(qty*rate) amount, remarks, a.iApproveFlag,note2,note3,note4,note5,vStatus,dInactivedate,vRemark from tbDemandOrderInfo as a inner join"
					+ " tbDemandOrderDetails as b on a.doNo = b.doNo where a.doNo = '"+id+"' ";
			List<?> led = session.createSQLQuery(sql).list();

			int i = 0;
			for(Iterator<?> iter = led.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbProductName.get(i).setEnabled(true);
				txtqty.get(i).setEnabled(true);
				if(i==0)
				{
					cmbPartyname.setValue(null);
					cmbPartyname.setValue(element[0].toString());
					dDeliveryDate.setValue(element[1]);
					txtDoNo.setValue(element[2].toString());
					dDoDate.setValue(element[3]);

					txtaddress.setValue(element[4].toString());
					txtMobileNo.setValue(element[5].toString());
					txtCommission.setValue(dfAmount.format(element[6]));
					txtNote1.setValue(element[7].toString());
					//maxId = element[2].toString();

					imageLoc = element[8].toString();

					txtTollarance.setValue(element[9].toString());

					if(element[16].toString().equals("1"))
					{
						isApprove = true;
					}
					else
					{
						isApprove = false;
					}
					txtNote2.setValue(element[17].toString());
					txtNote3.setValue(element[18].toString());
					txtNote4.setValue(element[19].toString());
					txtNote5.setValue(element[20].toString());
					
					cmbStatus.setValue(element[21].toString());
					if(element[21].toString().equals("Inactive"))
					{
						dInActive.setValue(element[22]);
						txtInActioveRemarks.setValue(element[23].toString());
					}
				}
				cmbProductName.get(i).setValue(element[10]);
				lblUnit.get(i).setValue(element[11].toString());
				txtqty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[12].toString())));
				txtrate.get(i).setValue(dfRate.format(Double.parseDouble(element[13].toString())));
				txtAmounttb.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[14].toString())));				
				txtRemarks.get(i).setValue(element[15]);
				if(lblsa.size()-1==i)
				{
					tableRowAdd(i+1);
				}
				i++;
			}
			GetDONo = txtDoNo.getValue().toString();
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addProductData(final int i)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			cmbProductName.get(i).removeAllItems();
			
			String sql= "select vProductId,vProductName from tbFinishedProductInfo where isActive = 1 and vCategoryId = "
					    +"(select vGroupId from tbPartyInfo where partyCode = '"+(cmbPartyname.getValue()!=null?cmbPartyname.getValue().toString():"")+"') " 
					    +"union all "
					    +"select vLabelCode,vLabelName from tb3rdPartylabelInformation where vPartyId='"+cmbPartyname.getValue().toString()+"' "
					    +"order by vProductName ";
			
			
	/*		String sql = "select vProductId,vProductName from tbFinishedProductInfo where isActive = 1 and vCategoryId ="+
					" (select vGroupId from tbPartyInfo where partyCode = '"+(cmbPartyname.getValue()!=null?
							cmbPartyname.getValue().toString():"")+"') order by vProductName";*/
			
			List<?> lst = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbProductName.get(i).addItem(element[0]);	
				cmbProductName.get(i).setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception ex)
		{}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(cmbPartyname);
		allComp.add(txtDoNo);
		allComp.add(dDoDate);
		allComp.add(dDeliveryDate);
		allComp.add(txtTollarance);

		for(int i=0;i<cmbProductName.size();i++)
		{
			allComp.add(cmbProductName.get(i));
			allComp.add(txtqty.get(i));
			allComp.add(txtRemarks.get(i));
		}
		
		allComp.add(txtNote1);
		allComp.add(txtNote2);
		allComp.add(txtNote3);
		allComp.add(txtNote4);
		allComp.add(txtNote5);
		

		allComp.add(txtCommission);
		allComp.add(txtNote1);
		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{
		dDoDate.setEnabled(!b);
		txtDoNo.setEnabled(!b);
		cmbPartyname.setEnabled(!b);
		txtaddress.setEnabled(!b);
		txtMobileNo.setEnabled(!b);
		dDeliveryDate.setEnabled(!b);
		//chkWithTax.setEnabled(!b);
		btnProduct.setEnabled(!b);
		txtTollarance.setEnabled(!b);

		btnParty.setEnabled(!b);
		txtCommission.setEnabled(!b);
		txtNote1.setEnabled(!b);

		bpvUpload.setEnabled(!b);
		btnPreview.setEnabled(!b);
		
		
		txtNote1.setEnabled(!b);
		txtNote2.setEnabled(!b);
		txtNote3.setEnabled(!b);
		txtNote4.setEnabled(!b);
		txtNote5.setEnabled(!b);
		
		bpvUpload.setEnabled(!b);
		btnPreview.setEnabled(!b);
		
		cmbStatus.setEnabled(!b);
		txtInActioveRemarks.setEnabled(!b);
		dInActive.setEnabled(!b);

		//table.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
		button.btnPreview.setEnabled(t);
	}

	public void txtClear()
	{
		flag = "0";
		cmbPartyname.setValue(null);
		txtCommission.setValue("");
		txtNote1.setValue("");
		txtNote2.setValue("");
		txtNote3.setValue("");
		txtNote4.setValue("");
		txtNote5.setValue("");
		txtDoNo.setValue("");
		txtaddress.setValue("");
		txtMobileNo.setValue("");
		dDeliveryDate.setValue(new java.util.Date());
		txtTollarance.setValue("20");

		bpvUpload.fileName = "";
		bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		bpvUpload.actionCheck = false;
		imageLoc = "0";
		cmbStatus.setValue("Active");
		tableClear();
	}

	private void tableinitialise(int ar)
	{
		for(int i=0; i<ar; i++)
		{
			tableRowAdd(i);
		}
		table.setColumnFooter("Rate","Grand Total = ");
	}

	private void tableRowAdd(final int ar)
	{
		lblsa.add(ar,new Label());
		lblsa.get(ar).setWidth("100%");
		lblsa.get(ar).setHeight("15px");
		lblsa.get(ar).setValue(ar+1);

		cmbProductName.add(ar, new ComboBox());
		cmbProductName.get(ar).setWidth("100%");
		cmbProductName.get(ar).setImmediate(true);
		cmbProductName.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				lblUnit.get(ar).setValue("");
				txtqty.get(ar).setValue("");
				txtrate.get(ar).setValue("");
				txtAmounttb.get(ar).setValue("");
				txtRemarks.get(ar).setValue("");
				if(cmbProductName.get(ar).getValue()!=null)
				{
					if(checkDuplicate(ar)) 
					{
						Session session = SessionFactoryUtil.getInstance().openSession();
						session.beginTransaction();
						try
						{
							
							String SQL= "Select vProductName,vUnitName,mDealerPrice,ISNULL(vFgcode,'') fgCode from tbFinishedProductInfo "
									    +"where vProductId = '"+cmbProductName.get(ar).getValue().toString()+"' " 
									    +"union all " 
									    +"select vLabelName,vUnit,mthirdPartyItemRate,vProductCode from tb3rdPartylabelInformation "
									    +"where vLabelCode='"+cmbProductName.get(ar).getValue().toString()+"' ";
						/*	
							String SQL = "Select vProductName,vUnitName,mDealerPrice,ISNULL(vFgcode,'') fgCode from tbFinishedProductInfo" +
									" where vProductId = '"+cmbProductName.get(ar).getValue().toString()+"' ";
							*/
							List<?> list = session.createSQLQuery(SQL).list();
							for(Iterator<?> iter = list.iterator();iter.hasNext();)
							{
								Object[] element = (Object[]) iter.next();
								lblUnit.get(ar).setValue(element[1].toString());
								txtrate.get(ar).setValue(dfRate.format(element[2]));
								lblproductCode.get(ar).setValue(element[3].toString());
								
							}
							Iterator<?> iter = session.createSQLQuery("select vProductId from tbDeliveryChallanDetails where vDoNo ="+
									" '"+txtDoNo.getValue().toString()+"' and vProductId = '"+cmbProductName.get(ar).getValue().toString()+"'").list().iterator();
							if(iter.hasNext())
							{
								flag = "1";
								cmbProductName.get(ar).setEnabled(false);
								//txtqty.get(ar).setEnabled(false);
							}
							if((ar+1)==cmbProductName.size())
							{
								tableRowAdd(ar+1);
								addProductData(ar+1);
							}
						}
						catch(Exception exp)
						{
							showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
						}
						finally{session.close();}
					}
					else
					{
						showNotification("Warning!","Duplicate product name selected.",Notification.TYPE_WARNING_MESSAGE);
						cmbProductName.get(ar).setValue(null);
						cmbProductName.get(ar).focus();
					}
					txtqty.get(ar).focus();
				}
			}
		});

		lblUnit.add(ar, new Label());
		lblUnit.get(ar).setWidth("100%");
		lblUnit.get(ar).setImmediate(true);
		
		lblproductCode.add(ar, new Label());
		lblproductCode.get(ar).setWidth("100%");
		lblproductCode.get(ar).setImmediate(true);
		
		

		txtqty.add(ar, new AmountCommaSeperator());
		txtqty.get(ar).setWidth("100%");
		txtqty.get(ar).setImmediate(true);
		txtqty.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtqty.get(ar).getValue().toString().isEmpty() && !txtrate.get(ar).getValue().toString().isEmpty())
				{
					double requiredQty = Double.parseDouble(txtqty.get(ar).getValue().toString().trim().replaceAll(",", ""));
					double tempPrice = Double.parseDouble(txtrate.get(ar).getValue().toString().replaceAll(",", ""));
					txtAmounttb.get(ar).setValue(new CommaSeparator().setComma(requiredQty*tempPrice));
					if(isFind)
					{
						Session session = SessionFactoryUtil.getInstance().openSession();
						session.beginTransaction();
						try
						{
							Iterator<?> iter = session.createSQLQuery(" select isnull(SUM(mChallanQty),0) from" +
									" tbDeliveryChallanDetails where vDoNo = '"+txtDoNo.getValue().toString()+"' " +
									" and vProductId = '"+cmbProductName.get(ar).getValue().toString()+"' ").list().iterator();
							if(iter.hasNext())
							{
								double deliveredQty = Double.parseDouble(iter.next().toString());
								if(deliveredQty>0)
								{
									if(deliveredQty>Double.parseDouble(txtqty.get(ar).getValue().toString()))
									{
										showNotification("Warning!","PO Quantity < Delivered Qty",Notification.TYPE_WARNING_MESSAGE);
										txtqty.get(ar).setValue(deliveredQty);
									}
								}
							}
						}
						catch(Exception ex)
						{
							showNotification("Warning!","Cannot update",Notification.TYPE_WARNING_MESSAGE);
						}
						finally{session.close();}
					}
					totalAmount();
				}
			}
		});

		txtrate.add(ar, new TextRead(1));
		txtrate.get(ar).setWidth("100%");
		txtrate.get(ar).setImmediate(true);

		txtAmounttb.add(ar, new TextRead(1));
		txtAmounttb.get(ar).setWidth("100%");
		txtAmounttb.get(ar).setImmediate(true);

		txtRemarks.add(ar, new TextField());
		txtRemarks.get(ar).setWidth("100%");
		txtRemarks.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblsa.get(ar),cmbProductName.get(ar),lblUnit.get(ar),lblproductCode.get(ar),
				txtqty.get(ar),txtrate.get(ar),txtAmounttb.get(ar),txtRemarks.get(ar)},ar);
	}

	private boolean checkDuplicate(int ar)
	{
		for(int i=0; i<cmbProductName.size(); i++)
		{
			if(cmbProductName.get(i).getValue()!=null && ar!=i)
			{
				if(cmbProductName.get(ar).getValue().toString().equals(cmbProductName.get(i).getValue().toString()))
				{
					return false;
				}
			}
		}
		return true;
	}

	private void totalAmount()
	{
		double summation = 0;
		for(int i=0; i<cmbProductName.size(); i++)
		{
			if(Double.parseDouble("0"+txtAmounttb.get(i).getValue().toString().replaceAll(",", ""))>0)
			{
				summation = summation + Double.parseDouble(txtAmounttb.get(i).getValue().toString().replaceAll(",", ""));
			}
		}
		table.setColumnFooter("Amount",""+new CommaSeparator().setComma(summation));
	}

	private void newButtonEvent()
	{
		for(int i=0; i<cmbProductName.size(); i++)
		{
			cmbProductName.get(i).setEnabled(true);
			txtqty.get(i).setEnabled(true);
		}
		componentIni(false);
		btnIni(false);
		txtClear();
		selectPoNo();
		txtDoNo.setValue("uptd-"+recDateFormat.format(dDoDate.getValue())+"-"+maxId);
	}

	private String selectPoNo()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select ISNULL(MAX(CAST(vDoSerial as int)),0)+1 from tbDemandOrderInfo";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				String srt = iter.next().toString();
				maxId = srt;
			}
		} 
		catch(Exception ex)
		{
			showNotification("Error!","Do select",Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return maxId;
	}

	private void refButtonEvent()
	{
		componentIni(true);
		btnIni(true);
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
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();
						if(deleteData(session,tx))
						{
							if(insertData(session,tx)){
								showNotification("All information update successfully.");
								reportView();
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
							showNotification("All information saved successfully.");
							reportView();
						}
						
					}
				}
			});
		}
	}

	private void setProductData()
	{
		for(int i = 0; i<cmbProductName.size(); i++)
		{
			if(cmbProductName.get(i).getValue()==null)
			{
				addProductData(i);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private boolean insertData(Session session,Transaction tx)
	{
		String remarks="";
		if(cmbStatus.getValue().toString().equals("Inactive")){
			remarks=txtInActioveRemarks.getValue().toString();
		}
		else{
			//Date FastDate=Date.parse("1900-01-01");
			//dInActive.setValue(dateFDMY.format(Date.parse(1900-01-01)));
		}
		try
		{
			String autoCode = maxId;
			DONo = txtDoNo.getValue().toString();

			if(cmbPartyname.getValue()!=null)
			{
				String approved = (isApprove?"1, '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP ":"0,'','',''");
				Object objImage = imagePath(1,txtDoNo.getValue().toString(),"1");

				String poImagePath = (objImage == null?imageLoc : objImage.toString());
				String queryInfo = "Insert into tbDemandOrderInfo(deiveryDate, vDoSerial, doNo, doDate, partyId, partyname," +
						" address, mobile, mode, transactionNo, transactionDate, amount, commission, note, BankId, Bank,"+
						" BranchId, Branch, userIp, userId, entrytime, iApproveFlag, vApproveBy, vApproveIp, dApproveTime,vFlag,note2,note3,note4,note5, vStatus,dInactivedate,vRemark)"+
						" values ('"+dFormat.format(dDeliveryDate.getValue())+"', '"+autoCode+"', '"+DONo+"',"+
						" '"+dFormat.format(dDoDate.getValue())+"'," +
						" '"+cmbPartyname.getValue()+"'," +
						" '"+cmbPartyname.getItemCaption(cmbPartyname.getValue())+"'," +
						" '"+txtaddress.getValue().toString().trim()+"'," +
						" '"+txtMobileNo.getValue().toString().trim()+"'," +
						" ''," +
						" ''," +
						" '"+dFormat.format(new Date())+"'," +
						" ''," +
						" '"+txtCommission.getValue().toString().replaceAll(",", "").trim()+"'," +
						" '"+txtNote1.getValue().toString().trim()+"'," +
						" '"+poImagePath+"'," +
						" '"+(txtTollarance.getValue().toString().isEmpty()?"0":txtTollarance.getValue().toString().trim())+"'," +
						" ''," +
						" ''," +
						" '"+sessionBean.getUserIp()+"'," +
						" '"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP, "+approved+", '','"+txtNote2.getValue()+"','"+txtNote3.getValue()+"','"+txtNote4.getValue()+"','"+txtNote5.getValue()+"','"+cmbStatus.getValue().toString()+"',"
						+ "'"+dateFYMD.format(dInActive.getValue())+"','"+remarks+"')";
				System.out.println(queryInfo);
				session.createSQLQuery(queryInfo).executeUpdate();

				for(int i=0; i<txtqty.size();i++)
				{
					if(cmbProductName.get(i).getValue()!=null)
					{
						if(!txtqty.get(i).getValue().toString().isEmpty()&& !txtrate.get(i).getValue().toString().isEmpty())
						{
							String queryDetails = "Insert into tbDemandOrderDetails(doNo,productId,productName,unit,qty," +
									" rate,amount,remarks,status,userIp,userId,entrytime,vCancel,iApproveFlag,"+
									" vApproveBy,vApproveIp, dApproveTime) values ('"+DONo+"'," +
									" '"+cmbProductName.get(i).getValue().toString()+"'," +
									" '"+cmbProductName.get(i).getItemCaption(cmbProductName.get(i).getValue())+"'," +
									" '"+lblUnit.get(i).getValue().toString().trim()+"'," +
									" '"+(txtqty.get(i).getValue().toString().replaceAll(",", "").trim())+"'," +
									" '"+(txtrate.get(i).getValue().toString().replaceAll(",", "").trim())+"'," +
									" '"+(txtAmounttb.get(i).getValue().toString().replace(",", "").trim())+"'," +
									" '"+(txtRemarks.get(i).getValue().toString().trim())+"'," +
									" '1'," +
									" '"+sessionBean.getUserIp()+"'," +
									" '"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'Continue', "+approved+")";
							//System.out.println(query);
							session.createSQLQuery(queryDetails).executeUpdate();
						}
					}
				}
			}
			tx.commit();
			componentIni(true);
			btnIni(true);
			isFind = false;
			isUpdate = false;
			return true;
		}
		catch(Exception ex)
		{
			if(tx!=null){
				tx.rollback();
			}
			showNotification("Can't Save", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private boolean deleteData(Session session,Transaction tx)
	{
		String id = txtFromFindWindow.getValue().toString().substring(0,txtFromFindWindow.getValue().toString().indexOf("@"));
		try
		{	
			String sql1 = "DELETE from tbDemandOrderDetails where doNo='"+id+"'";
			session.createSQLQuery(sql1).executeUpdate();
			String sql2 = "DELETE from tbDemandOrderInfo where doNo='"+id+"'";
			session.createSQLQuery(sql2).executeUpdate();
			return true;
		}
		catch(Exception exp)
		{
			showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void tableClear()
	{
		for(int i=0; i<lblsa.size(); i++)
		{
			cmbProductName.get(i).removeAllItems();
			lblUnit.get(i).setValue("");
			lblproductCode.get(i).setValue("");
			txtqty.get(i).setValue("");
			txtrate.get(i).setValue("");
			txtAmounttb.get(i).setValue("");
			txtRemarks.get(i).setValue("");
		}
	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("930px");
		mainLayout.setHeight("620px");

		Label lblFiPro = new Label("Finish Product Details");
		lblFiPro.setImmediate(false);
		lblFiPro.setWidth("-1px");
		lblFiPro.setHeight("-1px");
		mainLayout.addComponent(lblFiPro, "top:18.0px; left:600.0px;");
		
		cmbStatus = new ComboBox();
		cmbStatus.setImmediate(true);
		cmbStatus.setWidth("110px");
		cmbStatus.setHeight("-1px");
		cmbStatus.setFilteringMode(cmbStatus.FILTERINGMODE_CONTAINS);
		//mainLayout.addComponent(new Label("Status"),"top:50.0px; left:600.0px;");
		mainLayout.addComponent(cmbStatus, "top:48.0px; left:690.0px;");
		cmbStatus.setNullSelectionAllowed(false);
		for (int i = 0; i < cities.length; i++) {
			cmbStatus.addItem(cities[i]);
		}
		cmbStatus.setValue(cities[0]);
		cmbStatus.setVisible(false);
		
		dInActive = new PopupDateField("");
		dInActive.setImmediate(true);
		dInActive.setWidth("110px");
		dInActive.setHeight("-1px");
		dInActive.setDateFormat("dd-MM-yyyy");
		dInActive.setValue(new java.util.Date());
		dInActive.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(lblInactiveDate,"top:80.0px; left:600.0px;");
		mainLayout.addComponent(dInActive, "top:78.0px; left:690.0px;");
		
		lblInactiveDate.setVisible(false);
		dInActive.setVisible(false);
		
		txtInActioveRemarks = new TextArea();
		txtInActioveRemarks.setImmediate(false);
		txtInActioveRemarks.setWidth("200px");
		txtInActioveRemarks.setHeight("48px");
		txtInActioveRemarks.setImmediate(true);
		mainLayout.addComponent(lblInactioveRemarks,"top:110.0px; left:600.0px;");
		mainLayout.addComponent(txtInActioveRemarks, "top:108.0px; left:690.5px;");
		
		lblInactioveRemarks.setVisible(false);
		txtInActioveRemarks.setVisible(false);

		btnProduct = new NativeButton();
		btnProduct.setCaption("");
		btnProduct.setImmediate(true);
		btnProduct.setWidth("28px");
		btnProduct.setHeight("24px");
		btnProduct.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnProduct,"top:16.0px;left:730.0px;");

		/*lblTollarance = new Label("PO Tollarance :");
		lblTollarance.setImmediate(true);
		lblTollarance.setWidth("-1px");
		lblTollarance.setHeight("-1px");
		mainLayout.addComponent(lblTollarance, "top:48.0px; left:600.0px;");*/

		txtTollarance = new AmountField();
		txtTollarance.setImmediate(true);
		txtTollarance.setWidth("30px");
		txtTollarance.setHeight("-1px");
		txtTollarance.setMaxLength(2);
		txtTollarance.setVisible(false);
		mainLayout.addComponent(txtTollarance,"top:46.0px;left:730.0px;");

		/*chkWithTax = new CheckBox("Unit Rate With VAT");
		chkWithTax.setImmediate(true);
		chkWithTax.setWidth("-1px");
		chkWithTax.setHeight("-1px");
		mainLayout.addComponent(chkWithTax,"top:80.0px;left:595.0px;");*/

		/*Label lblPercent = new Label("%");
		lblPercent.setImmediate(false);
		lblPercent.setWidth("-1px");
		lblPercent.setHeight("-1px");
		mainLayout.addComponent(lblPercent, "top:48.0px; left:765.0px;");*/

		lblpartyname = new Label();
		lblpartyname.setImmediate(false);
		lblpartyname.setWidth("-1px");
		lblpartyname.setHeight("-1px");
		lblpartyname.setValue("Party Name :");
		mainLayout.addComponent(lblpartyname, "top:20.0px; left:30.0px;");

		cmbPartyname = new ComboBox();
		cmbPartyname.setImmediate(true);
		cmbPartyname.setWidth("300px");
		cmbPartyname.setHeight("24px");
		mainLayout.addComponent(cmbPartyname, "top:18.0px; left:165.0px;");

		btnParty = new NativeButton();
		btnParty.setCaption("");
		btnParty.setImmediate(true);
		btnParty.setWidth("28px");
		btnParty.setHeight("24px");
		btnParty.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnParty,"top:19.0px;left:470.0px;");

		lbladdress = new Label();
		lbladdress.setImmediate(false);
		lbladdress.setWidth("-1px");
		lbladdress.setHeight("-1px");
		lbladdress.setValue("Address :");
		mainLayout.addComponent(lbladdress, "top:45.0px; left:30.0px;");

		txtaddress = new TextField();
		txtaddress.setImmediate(false);
		txtaddress.setWidth("300px");
		txtaddress.setHeight("48px");
		txtaddress.setImmediate(true);
		mainLayout.addComponent(txtaddress, "top:43.0px; left:165.5px;");

		lblMobileNo = new Label();
		lblMobileNo.setImmediate(false);
		lblMobileNo.setWidth("-1px");
		lblMobileNo.setHeight("-1px");
		lblMobileNo.setValue("Mobile No:");
		mainLayout.addComponent(lblMobileNo, "top:95.0px; left:30.0px;");

		txtMobileNo = new TextField();
		txtMobileNo.setImmediate(false);
		txtMobileNo.setWidth("220px");
		txtMobileNo.setHeight("-1px");
		txtMobileNo.setImmediate(true);
		mainLayout.addComponent(txtMobileNo, "top:93.0px; left:165.5px;");

		lblDono = new Label();
		lblDono.setImmediate(false);
		lblDono.setWidth("-1px");
		lblDono.setHeight("-1px");
		lblDono.setValue("P.O No :");
		mainLayout.addComponent(lblDono, "top:120.0px; left:30.0px;");

		txtDoNo = new TextField();
		txtDoNo.setImmediate(true);
		txtDoNo.setWidth("300px");
		txtDoNo.setHeight("-1px");
		mainLayout.addComponent(txtDoNo, "top:118.0px; left:165.5px;");

		lblDote = new Label();
		lblDote.setImmediate(false);
		lblDote.setWidth("-1px");
		lblDote.setHeight("-1px");
		lblDote.setValue("PO Date :");
		mainLayout.addComponent(lblDote, "top:145; left:30.0px;");

		dDoDate = new PopupDateField();
		dDoDate.setWidth("110px");
		dDoDate.setHeight("-1px");
		dDoDate.setDateFormat("dd-MM-yyyy");
		dDoDate.setValue(new java.util.Date());
		dDoDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDoDate, "top:143.0px; left:165.0px;");

		lbldeliveryDate = new Label();
		lbldeliveryDate.setImmediate(false);
		lbldeliveryDate.setWidth("-1px");
		lbldeliveryDate.setHeight("-1px");
		lbldeliveryDate.setValue("Expected Del Date :");
		mainLayout.addComponent(lbldeliveryDate, "top:170.0px; left:30.0px;");

		dDeliveryDate = new PopupDateField();
		dDeliveryDate.setWidth("110px");
		dDeliveryDate.setHeight("24px");
		dDeliveryDate.setDateFormat("dd-MM-yyyy");
		dDeliveryDate.setValue(new java.util.Date());
		dDeliveryDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDeliveryDate.setImmediate(true);
		mainLayout.addComponent(dDeliveryDate, "top:168.0px; left:165.0px;");

		mainLayout.addComponent(bpvUpload, "top:150.0px;left:580.0px;");

		// btnPreview
		btnPreview = new Button("PO Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:177.0px;left:690.0px;");

		table.setWidth("890px");
		table.setHeight("180px");

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Product Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Product Name", 340);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit", 30);
		
		table.addContainerProperty("Code", Label.class, new Label());
		table.setColumnWidth("Code", 80);
		

		table.addContainerProperty("PO Qty",AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("PO Qty", 87);

		table.addContainerProperty("Unit Rate", TextRead.class, new TextRead(1));
		table.setColumnWidth("Unit Rate", 80);

		table.addContainerProperty("Amount", TextRead.class, new TextRead(),null,null,Table.ALIGN_RIGHT);
		table.setColumnWidth("Amount", 100);

		table.addContainerProperty("Remarks", TextField.class, new TextField());
		table.setColumnWidth("Remarks", 120);

		table.setFooterVisible(true);
		table.setColumnCollapsingAllowed(true);

		mainLayout.addComponent(table,"top:200.0px; left:20.0px;");

		lblCommission = new Label("Commission (%) :");
		lblCommission.setImmediate(false);
		lblCommission.setWidth("-1px");
		lblCommission.setHeight("-1px");
		mainLayout.addComponent(lblCommission, "top:400.0px; left:25.0px;");

		txtCommission = new AmountField();
		txtCommission.setImmediate(true);
		txtCommission.setWidth("40");
		txtCommission.setHeight("-1px");
		mainLayout.addComponent(txtCommission, "top:400.0px; left:130.0px;");

		lblNote1 = new Label("Note :");
		lblNote1.setImmediate(false);
		lblNote1.setWidth("-1px");
		lblNote1.setHeight("-1px");
		mainLayout.addComponent(lblNote1, "top:400.0px; left:190.0px;");
		
		txtcolorBox1 = new TextRead();
		txtcolorBox1.setImmediate(true);
		txtcolorBox1.setWidth("12px");
		txtcolorBox1.setHeight("12px");
		txtcolorBox1.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox1, "top:420.0px;left:215.0px;");

		txtNote1 = new TextField();
		txtNote1.setImmediate(true);
		txtNote1.setWidth("650px");
		txtNote1.setHeight("-1px");
		mainLayout.addComponent(txtNote1, "top:415.0px; left:240.0px;");
		
		

		
		txtcolorBox2 = new TextRead();
		txtcolorBox2.setImmediate(true);
		txtcolorBox2.setWidth("12px");
		txtcolorBox2.setHeight("12px");
		txtcolorBox2.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox2, "top:446.0px;left:215.0px;");

		txtNote2 = new TextField();
		txtNote2.setImmediate(true);
		txtNote2.setWidth("650px");
		txtNote2.setHeight("-1px");
		mainLayout.addComponent(txtNote2, "top:441.0px; left:240.0px;");
		
		
		txtcolorBox3 = new TextRead();
		txtcolorBox3.setImmediate(true);
		txtcolorBox3.setWidth("12px");
		txtcolorBox3.setHeight("12px");
		txtcolorBox3.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox3, "top:472.0px;left:215.0px;");

		txtNote3 = new TextField();
		txtNote3.setImmediate(true);
		txtNote3.setWidth("650px");
		txtNote3.setHeight("-1px");
		mainLayout.addComponent(txtNote3, "top:467.0px; left:240.0px;");
		
		txtcolorBox4 = new TextRead();
		txtcolorBox4.setImmediate(true);
		txtcolorBox4.setWidth("12px");
		txtcolorBox4.setHeight("12px");
		txtcolorBox4.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox4, "top:498.0px;left:215.0px;");

		txtNote4 = new TextField();
		txtNote4.setImmediate(true);
		txtNote4.setWidth("650px");
		txtNote4.setHeight("-1px");
		mainLayout.addComponent(txtNote4, "top:493.0px; left:240.0px;");
		
		
		txtcolorBox5 = new TextRead();
		txtcolorBox5.setImmediate(true);
		txtcolorBox5.setWidth("12px");
		txtcolorBox5.setHeight("12px");
		txtcolorBox5.setStyleName("bcolorbox");
		mainLayout.addComponent(txtcolorBox5, "top:524.0px;left:215.0px;");

		txtNote5 = new TextField();
		txtNote5.setImmediate(true);
		txtNote5.setWidth("650px");
		txtNote5.setHeight("-1px");
		mainLayout.addComponent(txtNote5, "top:519.0px; left:240.0px;");
		mainLayout.addComponent(button,"top:550.0px; left:100.0px");
		return mainLayout;
	}
	private void reportView()
	{
		ReportDate reportTime = new ReportDate();
		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("Date",reportTime.getTime);

		/*	String query = " Select DO.doNo,DO.doDate,DO.deiveryDate,DO.commission,DO.note,DOD.remarks,DO.partyName,"
					+ " DO.address,DO.mobile,DOD.productId,(select vProductName from tbFinishedProductInfo fi where"
					+ " fi.vProductId = DOD.productId)productName,DOD.unit,DOD.rate,DOD.qty,DOD.amount,PI.DivisionName,PI.AreaName,"
					+ " AI.vEmployeeName,REPLACE(BankId,'D:/Tomcat 7.0/webapps/', '') attachPO,DO.vApproveBy,DO.note2,DO.note3,DO.note4,DO.note5 from tbDemandOrderInfo as"
					+ " DO left join tbDemandOrderDetails as DOD on Do.doNo = DOD.doNo left Join tbPartyInfo as PI on"
					+ " Pi.partyCode = DO.partyId left join tbAreaInfo as AI on Ai.vAreaId=PI.AreaId where "
					+ " DO.doNo = '"+txtDoNo.getValue().toString()+"' order by productName ";
			*/
			String query="Select DO.doNo,DO.doDate,DO.deiveryDate,DO.commission,DO.note,DOD.remarks,DO.partyName, "
					     +"DO.address,DO.mobile,DOD.productId, case when DOD.productId like '%FI%' then (select vProductName from tbFinishedProductInfo fi where "
					     +"fi.vProductId = DOD.productId)else (select vLabelName from tb3rdPartylabelInformation where vLabelCode=DOD.productId) end productName,DOD.unit,DOD.rate,DOD.qty,DOD.amount,PI.DivisionName,PI.AreaName, "
					     +"AI.vEmployeeName,REPLACE(BankId,'D:/Tomcat 7.0/webapps/', '') attachPO,DO.vApproveBy,DO.note2,DO.note3,DO.note4,DO.note5 from tbDemandOrderInfo as "
					     +"DO left join tbDemandOrderDetails as DOD on Do.doNo = DOD.doNo left Join tbPartyInfo as PI on "
					     +"Pi.partyCode = DO.partyId left join tbAreaInfo as AI on Ai.vAreaId=PI.AreaId where " 
					     +"DO.doNo = '"+txtDoNo.getValue().toString()+"' order by productName ";

			String link = this.getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", "");
			hm.put("urlLink", link);
			hm.put("sql", query);
			Window win = new ReportViewer(hm,"report/account/DoSales/rptDemadOrderList.jasper",
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