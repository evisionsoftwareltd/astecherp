package com.example.sparePartsSetup;

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
import com.common.share.CommonButton;
import com.common.share.FileUpload;
import com.common.share.FileUploadItemInfo;
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
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.SucceededEvent;

public class SparePartsItemInfo extends Window 
{
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;

	private Label lblDate;
	private PopupDateField dDate;

	private TextRead txtRawItemCode;

	private Label lblRawItemName;
	private TextField  txtRawItemName,txtUnit,txtmodelNo,txtOrigin,txtSource,txtExpiryMonth;

	private ComboBox cmbCategory, cmbSubCategory,cmbRackName,cmbSubRackName,cmbSpareItemType;	

	private AmountCommaSeperator txtMaxQty,txtMinQty;
	//private Label lblCategoryType;
	//private ComboBox cmbcategoryType;	
	private TextArea txtItemDescription;
	private Label lblSampleImage;

	private DecimalFormat df = new DecimalFormat("#0.00");
	private Label lblReLavel;
	private AmountCommaSeperator txtReOrderQty;

	private NativeButton nbCategory,nbSubCategory,nbRack,nbSubRack;
	String RawItemId="";
	private String findUpdateRawItemId="";

	String LedgerId="";

	private TextRead ledgerCode = new TextRead();

	private static final String[] itemType = new String[]{"Store Item","Non-Store Item"};
	private OptionGroup isActivetype;
	private List <String> isActive=Arrays.asList(new String[]{"Yes","No"});
	boolean isUpdate=false;
	boolean isFind=false;
	int index;
	SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	
	public FileUploadItemInfo image;
	String imageLoc= "0";
	private String spareproductSample = "0";

	Button btnDeleteImage=new Button();
	
	public SparePartsItemInfo(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("SPARE PARTS ITEM INFORMATION :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		setEventAction();
		cmbCategoryAddData();
		focusEnter();
		authenticationCheck();
		spareproductSample = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/spareproductSample/";
		System.out.println("Location Is"+spareproductSample);
	}

	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String sampleImage = null;

		if(flag==1)
		{
			// image move
			if(image.fileName.trim().length()>0)
				try 
			{
					if(image.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						fileMove(basePath+image.fileName.trim(),spareproductSample+path+".jpg");
						sampleImage = spareproductSample+path+".jpg";

						System.out.println("Location is"+sampleImage);
					}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Sample Image: "+sampleImage);
			return sampleImage;
		}
		return null;
	}

	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			System.out.println("Rabiul Hasan BGC");

			File f1 = new File(tStr);
			if(f1.isFile())
			{
				f1.delete();
			}
		}
		catch(Exception exp)
		{
			System.out.println("Error is"+exp);
		}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			System.out.print("Yes Done");
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}

	public void ProductImage(String img)
	{
		File  fileStu_I = new File(img);
		Embedded eStu_I = new Embedded("",new FileResource(fileStu_I, getApplication()));
		eStu_I.requestRepaint();
		eStu_I.setWidth("260px");
		eStu_I.setHeight("350px");
		image.image.removeAllComponents();
		image.image.addComponent(eStu_I);
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

	public void setEventAction()
	{
		
		btnDeleteImage.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				image.image.removeAllComponents();
				image.status.setValue("Please select a image file(jpg) to upload");
				imageLoc= "0";
				spareproductSample = "0";
				btnDeleteImage.setVisible(false);
			}
		});
		image.upload.addListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
				btnDeleteImage.setVisible(true);
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
					getParent().showNotification("You are not Permitted to Edit.",Notification.TYPE_WARNING_MESSAGE);
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

		nbCategory.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(!txtRawItemName.getValue().toString().isEmpty())
				{
					categoryLink();
				}
				else
				{
					showNotification("Please provide Item Name.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		nbSubCategory.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(cmbCategory.getValue()!=null)
				{
					subCategoryLink();			
				}
				else{
				showNotification("Please select Category Name.",Notification.TYPE_WARNING_MESSAGE);}
			}
		});
		
		nbRack.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			if(cmbCategory.getValue()!=null)
			{
				rackLink();
			}	
			else{
				showNotification("Please select Category Name.",Notification.TYPE_WARNING_MESSAGE);}
			
			}
		});
		nbSubRack.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			if(cmbRackName.getValue()!=null)
			{
				subrackLink();
			}	
			else{
				showNotification("Please select Rack Name.",Notification.TYPE_WARNING_MESSAGE);}
			}
		});
		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{  isFind=true;
			findButtonEvent();
			}
		});

		cmbCategory.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbCategory.getValue()!=null)
				{
					cmbSubCategoryAddData();
					cmbRackAddData();
				}
				else
				{
					cmbSubCategory.removeAllItems();
				}
			}
		});
		cmbRackName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbRackName.getValue()!=null)
				{
					cmbSubRackAddData();
				}
			}
		});
		txtRawItemName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				Transaction tx = null;
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				try
				{
					if(!txtRawItemName.getValue().toString().isEmpty())
					{
						String query = "select * from tbRawItemInfo where vRawItemName like '"+txtRawItemName.getValue().toString()+"'";

						Iterator iter = session.createSQLQuery(query).list().iterator();

						if (iter.hasNext())
						{

							if(!isFind)
							{
								showNotification("Item Name Already Exists :"+txtRawItemName.getValue().toString());
								txtRawItemName.setValue("");
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
		txtMaxQty.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtMaxQty.getValue().toString().isEmpty())
				{
					double minimumlabel=0;
					double maxlevel=0;
					double relevel=0;

					minimumlabel=Double.parseDouble(txtMinQty.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMinQty.getValue().toString().replaceAll(",", ""));
					maxlevel=Double.parseDouble(txtMaxQty.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMaxQty.getValue().toString().replaceAll(",", ""));
					relevel=Double.parseDouble(txtReOrderQty.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtReOrderQty.getValue().toString().replaceAll(",", ""));
					if(maxlevel==0)
					{
						showNotification("Maximum Level must be greater than Zerro",Notification.TYPE_WARNING_MESSAGE);
						txtMaxQty.setValue("");	
					}
					else if(minimumlabel>maxlevel)
					{
						showNotification("Minimum Level must be Less than Maximum Level",Notification.TYPE_WARNING_MESSAGE);
						txtMaxQty.setValue("");
					}
					else if(relevel>maxlevel)
					{
						showNotification("ReOrder Level must be Less than Maximum Level",Notification.TYPE_WARNING_MESSAGE);
						txtMaxQty.setValue("");
					}
					else
					{

					}
				}
			}
		});
		txtReOrderQty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtReOrderQty.getValue().toString().isEmpty())
				{

					double minimumlabel=0;
					double maxlevel=0;
					double relevel=0;

					minimumlabel=Double.parseDouble(txtMinQty.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMinQty.getValue().toString().replaceAll(",", ""));
					maxlevel=Double.parseDouble(txtMaxQty.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMaxQty.getValue().toString().replaceAll(",", ""));
					relevel=Double.parseDouble(txtReOrderQty.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtReOrderQty.getValue().toString().replaceAll(",", ""));

					if(relevel==0)
					{
						showNotification("ReOrder Level should be greater than Zero",Notification.TYPE_WARNING_MESSAGE);
						txtReOrderQty.setValue("");	
					}

					else if(relevel<minimumlabel)
					{
						showNotification("ReOrder Level should be greater than mimimum Level",Notification.TYPE_WARNING_MESSAGE);
						txtReOrderQty.setValue("");
					}

					else if(relevel>maxlevel)
					{
						showNotification("ReOrder Level should be Less than Maximum Level",Notification.TYPE_WARNING_MESSAGE);
						txtReOrderQty.setValue("");
					}
					else
					{

					}
				}
			}
		});
		/*txtMinQty.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtMinQty.getValue().toString().isEmpty())
				{
					double minimumlabel=0;
					double maxlevel=0;
					double relevel=0;

					minimumlabel=Double.parseDouble(txtMinQty.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMinQty.getValue().toString().replaceAll(",", ""));
					maxlevel=Double.parseDouble(txtMaxQty.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMaxQty.getValue().toString().replaceAll(",", ""));
					relevel=Double.parseDouble(txtReOrderQty.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtReOrderQty.getValue().toString().replaceAll(",", ""));

					if(minimumlabel==0)
					{
						showNotification("Minimum Level must be greater than Zero",Notification.TYPE_WARNING_MESSAGE);
						txtMinQty.setValue("");	
					}
					else if(minimumlabel>relevel)
					{
						showNotification("Minimum Level must be Less than ReOrder Level",Notification.TYPE_WARNING_MESSAGE);
						txtMinQty.setValue("");
					}

					else if(minimumlabel>maxlevel)
					{
						showNotification("Minimum Level must be Less than Maximum Level",Notification.TYPE_WARNING_MESSAGE);
						txtMinQty.setValue("");
					}
					else
					{

					}
				}
			}
		});*/
	}

	private String selectRawItemCode()
	{
		String RawItemCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " Select isnull(max(cast(SUBSTRING(vRawItemCode,4,LEN(vRawItemCode)) as int)),0)+1 from tbRawItemInfo";

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

	public void cmbCategoryAddData()
	{
		cmbCategory.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select  iCategoryCode, vCategoryName from tbRawItemCategory  where vCategoryType like '%Spare Parts%' and vFlag='New' order by vCategoryName ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbCategory.addItem(element[0].toString());
				cmbCategory.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public void cmbSubCategoryAddData()
	{
		cmbSubCategory.removeAllItems();
		//	int i=0;
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT distinct iSubCategoryID,vSubCategoryName FROM tbRawItemSubCategory where iCategoryID = '"+cmbCategory.getValue().toString()+"' and vFlag='New' order by iSubCategoryID ").list();

			//	i=1;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSubCategory.addItem(element[0].toString());
				cmbSubCategory.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		/*if(i==0)
		{
			showNotification("Warning","There are no Sub-Group in this Group");
		}*/
	}
	public void cmbRackAddData()
	{
		cmbRackName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select  vRackId, vRackName from tbRackInformation  order by vRackName").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbRackName.addItem(element[0].toString());
				cmbRackName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public void cmbSubRackAddData()
	{
		cmbSubRackName.removeAllItems();
		Transaction tx=null;
		String rackId="";
		try
		{
			if(cmbRackName.getValue()!=null)
			{rackId=cmbRackName.getValue().toString();}
			
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("Select  vSubRackId, vSubRackName from tbSubRackInformation  where vRackId='"+rackId+"' order by vSubRackName").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSubRackName.addItem(element[0].toString());
				cmbSubRackName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void updateButtonEvent()
	{
		if(!txtRawItemName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIniUpdate(false);//Enable(true);
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
			if(!txtRawItemCode.getValue().toString().isEmpty())
			{
				if(!txtRawItemName.getValue().toString().isEmpty())
				{
					if(cmbCategory.getValue()!=null)
					{
						if(cmbRackName.getValue()!=null)
						{
						if(!txtUnit.getValue().toString().isEmpty())
						{
							if(cmbSpareItemType.getValue()!=null)
							{
								if(!txtMinQty.getValue().toString().isEmpty())
								{
									if(!txtReOrderQty.getValue().toString().isEmpty())
										{
										if(!txtMaxQty.getValue().toString().isEmpty())
										{
											saveButtonEvent();		
										}
										else
										{
											showNotification("Please, provide Maximum Qty ",Notification.TYPE_WARNING_MESSAGE);	
										}
									}
									else
									{
										showNotification("Please, provide ReOrder Qty ",Notification.TYPE_WARNING_MESSAGE);	
									}
								}
								else
								{
									showNotification("Please, provide Minimum Qty ",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								getParent().showNotification("Warning!","Select Item Type",Notification.TYPE_WARNING_MESSAGE);
								cmbSpareItemType.focus();
							}
						}
						else
						{
							getParent().showNotification("Warning!","Please provide unit",Notification.TYPE_WARNING_MESSAGE);
							txtUnit.focus();
						}
					}
					else
					{
						getParent().showNotification("Warning!","Please select rack",Notification.TYPE_WARNING_MESSAGE);
						cmbRackName.focus();
					}
					}
					else
					{
						getParent().showNotification("Warning!","Please select category",Notification.TYPE_WARNING_MESSAGE);
						cmbCategory.focus();
					}
				}
				else
				{
					getParent().showNotification("Warning!","Please provide Item name",Notification.TYPE_WARNING_MESSAGE);
					txtRawItemName.focus();
				}
			}
			else
			{
				getParent().showNotification("Warning!","Please provide Item code",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			getParent().showNotification("Warning!","Please provide all data",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void findButtonEvent() 
	{
		Window win = new SparePartsItemInfoFind(sessionBean, txtItemID,"ItemId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtItemID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtItemID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtItemId) 
	{
		Transaction tx = null;
		String sql = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select  vRawItemCode,replace(vRawItemName,'~','''')vRawItemName, *  from tbRawItemInfo where vRawItemCode = '"+txtItemId+"' and vCategoryType like '%Spare Parts%' and vFlag='New'";


			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				dDate.setValue(element[14]);
				txtRawItemCode.setValue(element[0]);
				txtRawItemName.setValue(element[1]);
				
				cmbCategory.addItem(element[5]);
				cmbCategory.setItemCaption(element[5].toString(), element[6].toString());
				cmbCategory.setValue(element[5]);
				cmbSubCategory.addItem(element[7]);
				cmbSubCategory.setItemCaption(element[7].toString(), element[8].toString());
				cmbSubCategory.setValue(element[7]);
				cmbRackName.addItem(element[23]);
				//cmbRackName.setItemCaption(element[5].toString(), element[6].toString());
				cmbRackName.setValue(element[23]);
				cmbSubRackName.addItem(element[25]);
				//cmbSubRackName.setItemCaption(element[7].toString(), element[8].toString());
				cmbSubRackName.setValue(element[25]);
				if(element[21]!=null)
				{
					txtmodelNo.setValue(element[21]);
				}

				txtUnit.setValue(element[9]);

				ledgerCode.setValue(element[10].toString());

				txtMaxQty.setValue(df.format(element[15]));
				txtReOrderQty.setValue(df.format(element[17]));
				txtMinQty.setValue(df.format(element[16]));
				cmbSpareItemType.setValue(element[11].toString());
				isActivetype.setValue(element[27]);
				txtSource.setValue(element[28]);
				txtOrigin.setValue(element[29]);
				txtExpiryMonth.setValue(element[30]);
				//cmbsubsubCategory.setValue(element[18].toString());
				
				txtItemDescription.setValue(element[32]);

				if(element[22]!=null)
				{
					ProductImage(element[22].toString());
					imageLoc = element[22].toString();	
				}

			}
		}
		catch (Exception exp)
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	public void subCategoryLink()
	{
		Window win = new RawItemSubCategory(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbSubCategoryAddData();
			}

		});
		this.getParent().addWindow(win);
	}

	public void categoryLink()
	{
		Window win = new SpareItemCategory(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbCategoryAddData();
			}

		});
		this.getParent().addWindow(win);
	}
	public void subRackLink()
	{
		Window win = new SpareItemSubCategory(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbSubCategoryAddData();
			}

		});
		this.getParent().addWindow(win);
	}

	public void rackLink()
	{
		Window win = new SpareRackInfo(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbRackAddData();
			}

		});
		this.getParent().addWindow(win);
	}
	public void subrackLink()
	{
		Window win = new SpareSubRackInfo(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbSubRackAddData();
			}

		});
		this.getParent().addWindow(win);
	}
	private void saveButtonEvent()
	{
		if(!isUpdate)
		{
			RawItemId = selectRawItemCode();
			LedgerId="";
			System.out.println("AutoID: "+RawItemId+" LedgerId: "+LedgerId);
		}
		else
		{
			RawItemId = findUpdateRawItemId;
			LedgerId = "";
		}

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
						updateData();
						isUpdate = false;
						isFind=false;
						btnIni(true);
						componentIni(true);
						txtClear();
						button.btnNew.focus();
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
						mb.buttonLayout.getComponent(0).setEnabled(false);
						Data();
						isUpdate = false;
						isFind=false;
						btnIni(true);
						componentIni(true);
						txtClear();
						button.btnNew.focus();
						mb.close();
					}
				}
			});
		}
	}
	public String ledgerId() 
	{
		String ledgerId = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select cast(isnull(max(cast(replace(Ledger_Id, 'AL', '')as int))+1, 1)as varchar)" +
					" from tbLedger where Ledger_Id like 'AL%' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				ledgerId = "AL"+iter.next().toString();
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return ledgerId;
	}
	private void Data()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		System.out.println("AutoID: "+RawItemId+" LedgerId: "+LedgerId);
		String groupId = "",subGroupId = "",rackId="",subrackId="",rackName="",subrackName="";
		String ModelNo="";

		if(!txtmodelNo.getValue().toString().isEmpty())
		{
			ModelNo=txtmodelNo.getValue().toString().trim().replaceAll("//W", "");
		}

		String createForm = "";
		String subGroup = "";
		String subGroupCaption = "";
		String parentId = "";

		LedgerId=ledgerId();

	/*	if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Chemical")||
				cmbcategoryType.getValue().toString().equalsIgnoreCase("Ink")||
				cmbcategoryType.getValue().toString().equalsIgnoreCase("Raw Material")){
			createForm="A5-G101";
			parentId = "G101";
		}
		else if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Packing Material")){
			createForm="A5-G102";
			parentId = "G102";
		}
		else if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Spare Parts")){
			createForm="A5-G103";
			parentId = "G103";
		}
		else if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Othes")){
			createForm="A5-G532";
			parentId = "G532";
		}*/
		//createForm = "A5-"+cmbCategory.getValue().toString()+""+(cmbSubCategory.getValue()==null?"":"-"+cmbSubCategory.getValue().toString());
		subGroup = cmbSubCategory.getValue()==null?"":"-"+cmbSubCategory.getValue().toString();
		subGroupCaption = "-"+cmbSubCategory.getItemCaption(cmbSubCategory.getValue()==null?"":cmbSubCategory.getValue().toString());


		System.out.println("GROUP: "+cmbCategory.getValue().toString());
		System.out.println("Group Caption: "+cmbCategory.getItemCaption(cmbCategory.getValue().toString()));

		String subGroupId1,SubGroupCaption,subSubGroupId,SubSubGroupCaption;
		if(cmbSubCategory.getValue()==null){
			subGroupId1="";
			SubGroupCaption="";
		}
		
		else{
			subGroupId1=cmbSubCategory.getValue().toString();
			subGroupCaption=cmbSubCategory.getItemCaption(cmbSubCategory.getValue());
		}
		if(cmbRackName.getValue()!=null){
			rackId=cmbRackName.getValue().toString();
			rackName=cmbRackName.getItemCaption(cmbRackName.getValue().toString());
		}
		
		if(cmbSubRackName.getValue()!=null){
			subrackId=cmbSubRackName.getValue().toString();
			subrackName=cmbSubRackName.getItemCaption(cmbSubRackName.getValue().toString());
		}
/*
		if(cmbsubsubCategory.getValue()==null){
			subSubGroupId="";
			SubSubGroupCaption="";
		}
		else
		{
			subSubGroupId=cmbsubsubCategory.getValue().toString();
			SubSubGroupCaption=cmbsubsubCategory.getItemCaption(cmbsubsubCategory.getValue());
		}*/

		if(!isUpdate)
		{
			txtRawItemCode.setValue(selectRawItemCode());	
		}

		String imagePathSample = imagePath(1,txtRawItemCode.getValue().toString())==null?imageLoc:imagePath(1,txtRawItemCode.getValue().toString());
		String image="";
		try
		{
			String RawItem = "INSERT  into tbRawItemInfo (vRawItemCode, vRawItemName, vGroupId, "
					+ "vGroupName, vSubGroupId, vSubGroupName, vUnitName, vLedgerCode, vProductType, "
					+ "vUserName, vUserIp, dEntryTime, mMaxLabel, mMinLabel, mReLabel, vsubsubCategoryId, "
					+ "vSubSubCategoryName, vCategoryType, modelNo, imageLoc, vRackId, vRackName, vSubRackId, "
					+ "vSubRackName, isActive,vSource,vOrigin,vExpiryMonth,vFlag,vItemDescription)values(" +
					" '"+"RI-"+txtRawItemCode.getValue().toString()+"'," +
					" '"+txtRawItemName.getValue().toString().trim().replaceAll("'","~")+"'," +
					" '"+cmbCategory.getValue().toString()+"'," +
					" '"+cmbCategory.getItemCaption(cmbCategory.getValue().toString())+"'," +
					" '"+subGroupId1+"'," +
					" '"+subGroupCaption+"'," +
					" '"+txtUnit.getValue().toString().trim().replaceAll("\\W", "")+"'," +
					" '"+LedgerId+"', " +
					" '"+cmbSpareItemType.getItemCaption(cmbSpareItemType.getValue())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+txtMaxQty.getValue().toLowerCase().trim().replaceAll(",", "")+"',"+
					" '"+txtMinQty.getValue().toString().trim().replaceAll(",", "")+"','"+txtReOrderQty.getValue().toString().trim().replaceAll(",", "")+"','','','Spare Parts'," +
					"'"+ModelNo+"','"+imagePathSample+"','"+rackId+"','"+rackName+"','"+subrackId+"','"+subrackName+"','"+isActivetype.getValue().toString()+"',"
					+ "'"+txtSource.getValue().toString().trim().replaceAll("\\W", "")+"','"+txtOrigin.getValue().toString().trim().replaceAll("\\W", "")+"','"+txtExpiryMonth.getValue().toString().trim().replaceAll("\\W", "")+"','New',"
							+ "'"+txtItemDescription.getValue().toString()+"') ";
			//replaceAll("\\W", "")
			System.out.println("query : "+RawItem);
			session.createSQLQuery(RawItem).executeUpdate();

			/*if(cmbSubCategory.getValue()!=null)
			{
				parentId = cmbSubCategory.getValue().toString();
			}
			else
			{
				parentId = cmbCategory.getValue().toString();
			}*/

			String Ledger="INSERT into tbLedger values(" +
					" '"+LedgerId+"', " +
					" '"+txtRawItemName.getValue().toString().trim().replaceAll("'","~")+"', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+parentId+"', " +
					" '"+createForm+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			System.out.println("Ledger : "+Ledger);
			session.createSQLQuery(Ledger).executeUpdate();

			String LedgerOpen="INSERT into tbLedger_Op_Balance values(" +
					" '"+LedgerId+"', " +
					" '0.00', " +
					" '0.00', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			System.out.println("LedgerOpen : "+LedgerOpen);
			session.createSQLQuery(LedgerOpen).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private boolean updateData()
	{
		String createForm = "";
		String subGroup = "",rackId="",subrackId="",rackName="",subrackName="";
		String subGroupCaption = "";

		String parentId="";

		if(cmbSubCategory.getValue()!=null)
		{
			//createForm = "A5-"+cmbCategory.getValue().toString()+"-"+cmbSubCategory.getValue().toString();
			subGroup = cmbSubCategory.getValue().toString();
			subGroupCaption = cmbSubCategory.getItemCaption(cmbSubCategory.getValue());
		}
		else
		{
			//createForm = "A5-"+cmbCategory.getValue().toString();
			subGroup = "";
			subGroupCaption = "";
		}
		if(cmbRackName.getValue()!=null){
			rackId=cmbRackName.getValue().toString();
			rackName=cmbRackName.getItemCaption(cmbRackName.getValue().toString());
		}
		
		if(cmbSubRackName.getValue()!=null){
			subrackId=cmbSubRackName.getValue().toString();
			subrackName=cmbSubRackName.getItemCaption(cmbSubRackName.getValue().toString());
		}
		String subSubGroupId,SubSubGroupCaption;

	/*	if(cmbsubsubCategory.getValue()==null){
			subSubGroupId="";
			SubSubGroupCaption="";
		}
		else
		{
			subSubGroupId=cmbsubsubCategory.getValue().toString();
			SubSubGroupCaption=cmbsubsubCategory.getItemCaption(cmbsubsubCategory.getValue());
		}*/

		System.out.println("AutoID: "+createForm);

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String imagePathSample = imagePath(1,txtRawItemCode.getValue().toString())==null?imageLoc:imagePath(1,txtRawItemCode.getValue().toString());
			String image="";
			
			String updateRawItem ="UPDATE tbRawItemInfo set" +
					" vRawItemName = '"+txtRawItemName.getValue().toString().trim()+"' ," +
					" vGroupId = '"+cmbCategory.getValue().toString()+"' ," +
					" vGroupName = '"+cmbCategory.getItemCaption(cmbCategory.getValue())+"' ," +
					" vSubGroupId = '"+subGroup+"' ," +
					" vSubGroupName = '"+subGroupCaption+"' ," +
					" vUnitName = '"+(txtUnit.getValue().toString().isEmpty()?"":txtUnit.getValue().toString())+"' ," +
					" vProductType = '"+cmbSpareItemType.getItemCaption(cmbSpareItemType.getValue())+"' ," +
					" vUserName = '"+sessionBean.getUserId()+"', " +
					" vUserIp = '"+sessionBean.getUserIp()+"', " +
					" dEntryTime = CURRENT_TIMESTAMP, mMaxLabel = '"+txtMaxQty.getValue().toString().trim()+"',"+
					" mMinLabel = '"+txtMinQty.getValue().toString().trim()+"', mReLabel = '"+txtReOrderQty.getValue().toString().trim()+"', "+
					" vsubsubCategoryId='', "+
					" vCategoryType='Spare Parts' ,    "+
					" vSubSubCategoryName='', ModelNo='"+txtmodelNo.getValue().toString()+"',imageLoc='"+imagePathSample+"'   ,"+
					" vRackId='"+rackId+"' , vRackName='"+rackName+"',vSubRackId='"+subrackId+"',vSubRackNAme='"+subrackName+"' ,isActive='"+isActivetype.getValue().toString()+"',vFlag='New' "+
					" where vRawItemCode='"+txtRawItemCode.getValue().toString()+"'";

			System.out.println(updateRawItem);
			session.createSQLQuery(updateRawItem).executeUpdate();

			if(cmbSubCategory.getValue()!=null)
			{
				parentId = cmbSubCategory.getValue().toString();
			}
			else
			{
				parentId = cmbCategory.getValue().toString();
			}

		/*	if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Chemical")||
					cmbcategoryType.getValue().toString().equalsIgnoreCase("Ink")||
					cmbcategoryType.getValue().toString().equalsIgnoreCase("Raw Material")){
				createForm="A5-G101";
			}
			else if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Packing Material")){
				createForm="A5-G102";
			}
			else if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Spare Parts")){
				createForm="A5-G103";
			}
			else if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Othes")){
				createForm="A5-G532";
				parentId = "G532";
			}*/

			String UpdateLedger = "UPDATE tbLedger set" +
					" Ledger_Name = '"+txtRawItemName.getValue().toString().trim()+"', " +
					" Creation_Year = '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" Parent_Id = '"+parentId+"', " +
					" Create_From = '"+createForm+"', " +
					" userId = '"+sessionBean.getUserId()+"', " +
					" userIp = '"+sessionBean.getUserIp()+"', " +
					" entryTime = CURRENT_TIMESTAMP " +
					" where Ledger_Id='"+ledgerCode.getValue()+"'";

			//System.out.println("UpdateLedger: "+UpdateLedger);
			session.createSQLQuery(UpdateLedger).executeUpdate();

			this.getParent().showNotification("All information updated successfully.");

			tx.commit();

			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
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
		txtRawItemCode.setValue("RI-"+selectRawItemCode());
		txtRawItemName.focus();
		
	}

	private void focusEnter()
	{
		allComp.add(txtRawItemCode);
		allComp.add(txtRawItemName);
		allComp.add(txtmodelNo);
		allComp.add(txtItemDescription);
		allComp.add(txtSource);
		allComp.add(txtOrigin);
		allComp.add(txtExpiryMonth);
		allComp.add(cmbCategory);
		allComp.add(cmbSubCategory);
		allComp.add(cmbRackName);
		allComp.add(cmbSubRackName);
		allComp.add(txtUnit);
		allComp.add(txtMaxQty);
		allComp.add(txtMinQty);
		allComp.add(txtReOrderQty);
		allComp.add(cmbSpareItemType);
		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public void txtClear()
	{
		txtRawItemCode.setValue("");
		txtRawItemName.setValue("");
		cmbCategory.setValue(null);
		cmbSubCategory.setValue(null);
		cmbRackName.setValue(null);
		cmbSubRackName.setValue(null);
		txtmodelNo.setValue("");
		txtUnit.setValue("");
		cmbSpareItemType.setValue(null);
		txtMaxQty.setValue("");
		txtMinQty.setValue("");
		txtReOrderQty.setValue("");
		isActivetype.setValue("Yes");
		txtItemDescription.setValue("");
		txtSource.setValue("");
		txtOrigin.setValue("");
		txtExpiryMonth.setValue("");
		image.image.removeAllComponents();
		image.status.setValue("Please select a image file(jpg) to upload");
		btnDeleteImage.setVisible(false);
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
		txtRawItemCode.setEnabled(!b);
		txtRawItemName.setEnabled(!b);
		cmbCategory.setEnabled(!b);
		cmbSubCategory.setEnabled(!b);
		nbCategory.setEnabled(!b);
		nbSubCategory.setEnabled(!b);
		cmbRackName.setEnabled(!b);
		cmbSubRackName.setEnabled(!b);
		nbRack.setEnabled(!b);
		nbSubRack.setEnabled(!b);
		txtmodelNo.setEnabled(!b);
		txtUnit.setEnabled(!b);
		cmbSpareItemType.setEnabled(!b);
		txtMaxQty.setEnabled(!b);
		txtMinQty.setEnabled(!b);
		txtReOrderQty.setEnabled(!b);
		txtItemDescription.setEnabled(!b);
		txtSource.setEnabled(!b);
		txtOrigin.setEnabled(!b);
		txtExpiryMonth.setEnabled(!b);
		isActivetype.setEnabled(!b);
		
	}

	private void componentIniUpdate(boolean b) 
	{
		txtRawItemCode.setEnabled(b);
		txtRawItemName.setEnabled(b);
		cmbCategory.setEnabled(b);
		cmbSubCategory.setEnabled(b);
		nbCategory.setEnabled(b);
		nbSubCategory.setEnabled(b);
		cmbRackName.setEnabled(!b);
		cmbSubRackName.setEnabled(!b);
		nbRack.setEnabled(!b);
		nbSubRack.setEnabled(!b);
		txtmodelNo.setEnabled(b);
		txtUnit.setEnabled(b);
		cmbSpareItemType.setEnabled(!b);
		txtMaxQty.setEnabled(!b);
		txtMinQty.setEnabled(!b);
		txtReOrderQty.setEnabled(!b);
		txtItemDescription.setEnabled(!b);
		txtSource.setEnabled(!b);
		txtOrigin.setEnabled(!b);
		txtExpiryMonth.setEnabled(!b);
		isActivetype.setEnabled(!b);
	}
	 
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("900px");
		setHeight("640px");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setHeight("24px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setValue(new java.util.Date());
		dDate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent((new Label("Date :")),"top:30.0px;left:50.0px;");
		mainLayout.addComponent(dDate, "top:27.0px;left:190.0px;");

		// txtRawItemCode
		txtRawItemCode = new TextRead();
		txtRawItemCode.setImmediate(false);
		txtRawItemCode.setWidth("100px");
		txtRawItemCode.setHeight("23px");
		mainLayout.addComponent((new Label("Item Code :")),"top:55.0px;left:50.0px;");
		mainLayout.addComponent(txtRawItemCode, "top:53.0px;left:190.0px;");

		// txtRawItemName
		txtRawItemName = new TextField();
		txtRawItemName.setImmediate(false);
		txtRawItemName.setWidth("318px");
		txtRawItemName.setHeight("-1px");
		txtRawItemName.setSecret(false);
		mainLayout.addComponent((new Label("Item Name :")), " top:80.0px;left:50.0px;");
		mainLayout.addComponent(txtRawItemName, "top:78.0px;left:190.0px;");


		// txtmodelNo
		txtmodelNo = new TextField();
		txtmodelNo.setImmediate(false);
		txtmodelNo.setWidth("140px");
		txtmodelNo.setHeight("-1px");
		txtmodelNo.setSecret(false);
		mainLayout.addComponent((new Label("MOdel No. :")), "top:105.0px;left:50.0px;");
		mainLayout.addComponent(txtmodelNo, "top:103.0px;left:190.0px;");

		
		txtItemDescription = new TextArea();
		txtItemDescription.setImmediate(false);
		txtItemDescription.setWidth("318px");
		txtItemDescription.setHeight("24px");
		mainLayout.addComponent((new Label("Item Description :")), "top:130.0px;left:50.0px;");
		mainLayout.addComponent(txtItemDescription, "top:128.0px;left:190.0px;");
		
		txtSource = new TextField();
		txtSource.setImmediate(false);
		txtSource.setWidth("318px");
		txtSource.setHeight("-1px");
		txtSource.setSecret(false);
		mainLayout.addComponent((new Label("Source :")), "top:155.0px;left:50.0px;");
		mainLayout.addComponent(txtSource, "top:153.0px;left:190.0px;");
		
		txtOrigin = new TextField();
		txtOrigin.setImmediate(false);
		txtOrigin.setWidth("318px");
		txtOrigin.setHeight("-1px");
		txtOrigin.setSecret(false);
		mainLayout.addComponent((new Label("Origin :")), "top:180.0px;left:50.0px;");
		mainLayout.addComponent(txtOrigin, "top:178.0px;left:190.0px;");

		txtExpiryMonth = new TextField();
		txtExpiryMonth.setImmediate(false);
		txtExpiryMonth.setWidth("150px");
		txtExpiryMonth.setHeight("-1px");
		txtExpiryMonth.setSecret(false);
		mainLayout.addComponent((new Label("Expiry Month :")), "top:205.0px;left:50.0px;");
		mainLayout.addComponent(txtExpiryMonth, "top:203.0px;left:190.0px;");
		
/*		cmbcategoryType = new ComboBox();
		cmbcategoryType.setImmediate(true);
		cmbcategoryType.setWidth("318px");
		cmbcategoryType.setHeight("24px");
		cmbcategoryType.setNullSelectionAllowed(true);
		cmbcategoryType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblCategoryType, "top:82.0px;left:50.0px;");
		mainLayout.addComponent(cmbcategoryType, "top:79.0px;left:190.0px;");		
*/
		// cmbCategory
		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(true);
		cmbCategory.setWidth("318px");
		cmbCategory.setHeight("24px");
		cmbCategory.setNullSelectionAllowed(true);
		cmbCategory.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent((new Label("Category Name:")), "top:230.0px;left:50.0px;");
		mainLayout.addComponent(cmbCategory, "top:228.0px;left:190.0px;");

		// nbCategory
		nbCategory = new NativeButton();
		nbCategory.setIcon(new ThemeResource("../icons/add.png"));
		nbCategory.setImmediate(true);
		nbCategory.setWidth("32px");
		nbCategory.setHeight("20px");
		mainLayout.addComponent(nbCategory, " top:230.0px;left:515.0px;");


		// cmbSubCategory
		cmbSubCategory = new ComboBox();
		cmbSubCategory.setImmediate(true);
		cmbSubCategory.setWidth("318px");
		cmbSubCategory.setHeight("-1px");
		cmbSubCategory.setNullSelectionAllowed(true);
		cmbSubCategory.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent((new Label("SubCategory Name:")), "top:255.0px;left:50.0px;");
		mainLayout.addComponent(cmbSubCategory, "top:253.0px;left:190.0px;");

		// nbSubCategory
		nbSubCategory = new NativeButton();
		nbSubCategory.setIcon(new ThemeResource("../icons/add.png"));	
		nbSubCategory.setImmediate(true);
		nbSubCategory.setWidth("32px");
		nbSubCategory.setHeight("20px");
		mainLayout.addComponent(nbSubCategory, "top:255.0px;left:515.0px;");

		cmbRackName = new ComboBox();
		cmbRackName.setImmediate(true);
		cmbRackName.setWidth("318px");
		cmbRackName.setHeight("24px");
		cmbRackName.setNullSelectionAllowed(true);
		cmbRackName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent((new Label("Rack Name:")), "top:280.0px;left:50.0px;");
		mainLayout.addComponent(cmbRackName, "top:278.0px;left:190.0px;");

		// nbCategory
		nbRack= new NativeButton();
		nbRack.setIcon(new ThemeResource("../icons/add.png"));
		nbRack.setImmediate(true);
		nbRack.setWidth("32px");
		nbRack.setHeight("20px");
		mainLayout.addComponent(nbRack, " top:280.0px;left:515.0px;");


		// cmbSubCategory
		cmbSubRackName= new ComboBox();
		cmbSubRackName.setImmediate(true);
		cmbSubRackName.setWidth("318px");
		cmbSubRackName.setHeight("-1px");
		cmbSubRackName.setNullSelectionAllowed(true);
		cmbSubRackName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent((new Label("SubRack Name:")), "top:305.0px;left:50.0px;");
		mainLayout.addComponent(cmbSubRackName, "top:303.0px;left:190.0px;");

		// nbSubCategory
		nbSubRack = new NativeButton();
		nbSubRack.setIcon(new ThemeResource("../icons/add.png"));	
		nbSubRack.setImmediate(true);
		nbSubRack.setWidth("32px");
		nbSubRack.setHeight("20px");
		mainLayout.addComponent(nbSubRack, "top:305.0px;left:515.0px;");

		// txtUnit
		txtUnit = new TextField();
		txtUnit.setImmediate(false);
		txtUnit.setWidth("100px");
		txtUnit.setHeight("-1px");
		txtUnit.setSecret(false);
		mainLayout.addComponent((new Label("Unit :")),  "top:330.0px;left:50.0px;");
		mainLayout.addComponent(txtUnit, "top:328.0px;left:190.0px;");

		txtMaxQty = new AmountCommaSeperator();
		txtMaxQty.setImmediate(false);
		txtMaxQty.setWidth("100px");
		txtMaxQty.setHeight("-1px");
		txtMaxQty.setSecret(false);
		mainLayout.addComponent((new Label("Max Qty :")), "top:355.0px;left:50.0px;");
		mainLayout.addComponent(txtMaxQty, "top:353.0px;left:190.0px;");

		txtMinQty = new AmountCommaSeperator();
		txtMinQty.setImmediate(false);
		txtMinQty.setWidth("100px");
		txtMinQty.setHeight("-1px");
		txtMinQty.setSecret(false);
		mainLayout.addComponent((new Label("Min Qty :")), "top:380.0px;left:50.0px;");
		mainLayout.addComponent(txtMinQty, "top:378.0px;left:190.0px;");
		

		txtReOrderQty = new AmountCommaSeperator();
		txtReOrderQty.setImmediate(false);
		txtReOrderQty.setWidth("100px");
		txtReOrderQty.setHeight("-1px");
		txtReOrderQty.setSecret(false);
		mainLayout.addComponent((new Label("ReOrder Qty :")), "top:405.0px;left:50.0px;");
		mainLayout.addComponent(txtReOrderQty, "top:403.0px;left:190.0px;");


		// cmbSpareItemType
		cmbSpareItemType = new ComboBox();
		cmbSpareItemType.setImmediate(true);
		cmbSpareItemType.setWidth("150px");
		cmbSpareItemType.setHeight("-1px");
		cmbSpareItemType.setNullSelectionAllowed(true);
		cmbSpareItemType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent((new Label("Item Type :")), "top:430.0px;left:50.0px;");
		mainLayout.addComponent(cmbSpareItemType, "top:428.0px;left:190.0px;");
		for(int i=0; i<itemType.length; i++)
		{cmbSpareItemType.addItem(itemType[i]);}
		
	
		
		isActivetype=new OptionGroup("",isActive);
		isActivetype.setStyleName("horizontal");
		isActivetype.select("Yes");
		isActivetype.setImmediate(true);
		mainLayout.addComponent((new Label("isActive :")), "top:455.0px; left:50.0px;");
		mainLayout.addComponent(isActivetype, "top:453.0px; left:190.0px;");
	

		mainLayout.addComponent(button, "top:555.0px;left:55.0px;");

		// lblSampleImage
		lblSampleImage = new Label("Sample Image :");
		lblSampleImage.setImmediate(false);
		lblSampleImage.setWidth("-1px");
		lblSampleImage.setHeight("-1px");
		//mainLayout.addComponent(lblSampleImage, "top:333.0px;left:360.0px;");
		image = new FileUploadItemInfo("Picture");
		image.upload.setButtonCaption("Sample Image");
		mainLayout.addComponent(image, "top:25.0px;left:600.0px;");
		image.setImmediate(true);
		
		btnDeleteImage = new Button("Delete");
		//btnDeleteImage.setIcon(new ThemeResource("../icons/add.png"));
		btnDeleteImage.setImmediate(true);
		///btnDeleteImage.setWidth("32px");
		///btnDeleteImage.setHeight("20px");
		mainLayout.addComponent(btnDeleteImage, " bottom:170.0px;left:750.0px;");
		btnDeleteImage.setVisible(false);
		
		return mainLayout;
	}
}
