package com.example.sparePartsSetup;

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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class RawItemInfo extends Window 
{
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;

	private Label lblRawItemCode;
	private TextRead txtRawItemCode;

	private Label lblRawItemName;
	private TextField txtRawItemName;

	private Label lblGroup;
	private ComboBox cmbGroup;	

	private Label lblSubGroup;
	private ComboBox cmbSubGroup;

	private Label lblUnit;
	private TextField txtUnit;

	private Label lblRawItemType;
	private ComboBox cmbRawItemType;

	private Label lblMaxLavel;
	private AmountCommaSeperator txtMaxLabel;

	private Label lblMinLavel;
	private AmountCommaSeperator txtMinLabel;

	private Label lblsubsubCategory;
	private ComboBox cmbsubsubCategory;

	private Label lblCategoryType;
	private ComboBox cmbcategoryType;
	
	private Label lblSampleImage;

	private DecimalFormat df = new DecimalFormat("#0.00");
	private Label lblReLavel;
	private AmountCommaSeperator txtReLabel;

	private NativeButton nbGroup;
	private NativeButton nbSubGroup;

	String RawItemId="";
	private String findUpdateRawItemId="";

	String LedgerId="";

	private TextRead ledgerCode = new TextRead();

	private static final String[] itemType = new String[]{"Store Item","Non-Store Item"};

	boolean isUpdate=false;
	boolean isFind=false;
	int index;
	SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
	
	private TextField txtmodelNo=new TextField();
	private Label lblModelNo=new Label();

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	
	public FileUpload image;
	String imageLoc= "0";
	private String productSample = "0";
	

	public RawItemInfo(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("RAW ITEM INFORMATION :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		categoryType();
		setEventAction();
		focusEnter();
		authenticationCheck();
		productSample = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/productSample/";
		System.out.println("Location Is"+productSample);
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
						fileMove(basePath+image.fileName.trim(),productSample+path+".jpg");
						sampleImage = productSample+path+".jpg";
						
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
		eStu_I.setWidth("100px");
		eStu_I.setHeight("135px");
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

		nbGroup.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(cmbcategoryType.getValue()!=null)
				{
					groupLink();
				}
								
			}
		});

		nbSubGroup.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(cmbGroup.getValue()!=null)
				{
					subGroupLink();			
				
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{  isFind=true;
				findButtonEvent();
			}
		});

		cmbGroup.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbGroup.getValue()!=null)
				{
					cmbSubGroupAddData();
				}
				else
				{
					cmbSubGroup.removeAllItems();
				}
			}
		});

		cmbSubGroup.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbSubGroup.getValue()!=null)
				{
					//selectsubsubGroup();
				}
			}
		});


		cmbcategoryType.addListener( new ValueChangeListener() {


			public void valueChange(ValueChangeEvent event)
			{
				if(cmbcategoryType.getValue()!=null)
				{
					cmbGroupAddData();
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
	
	      txtReLabel.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtReLabel.getValue().toString().isEmpty())
				{
					
					double minimumlabel=0;
					double maxLabel=0;
					double relebel=0;
					
					minimumlabel=Double.parseDouble(txtMinLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMinLabel.getValue().toString().replaceAll(",", ""));
					maxLabel=Double.parseDouble(txtMaxLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtMaxLabel.getValue().toString().replaceAll(",", ""));
					relebel=Double.parseDouble(txtReLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtReLabel.getValue().toString().replaceAll(",", ""));
					
					if(relebel==0)
					{
						showNotification("Reorder Lebel Must be greater than Zerro",Notification.TYPE_WARNING_MESSAGE);
						txtReLabel.setValue("");	
					}
					
					else if(relebel<minimumlabel)
					{
					  showNotification("Reorder Lebel Must be greater than mimimum Label",Notification.TYPE_WARNING_MESSAGE);
					  txtReLabel.setValue("");
					}
					
					else if(relebel>maxLabel)
					{
					  showNotification("Reorder Lebel Must be Less than Maximum Label",Notification.TYPE_WARNING_MESSAGE);
					  txtReLabel.setValue("");
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
						relebel=Double.parseDouble(txtReLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtReLabel.getValue().toString().replaceAll(",", ""));
						
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
						relebel=Double.parseDouble(txtReLabel.getValue().toString().replaceAll(",", "").isEmpty()?"0.00":txtReLabel.getValue().toString().replaceAll(",", ""));
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

	public void cmbGroupAddData()
	{
		cmbGroup.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select  iCategoryCode, vCategoryName from tbRawItemCategory  where vCategoryType like '"+cmbcategoryType.getValue().toString()+"' ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbGroup.addItem(element[0].toString());
				cmbGroup.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}



	public void categoryType()
	{
		cmbcategoryType.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String query="select distinct  0,vCategoryType  from tbRawItemCategory";
			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbcategoryType.addItem(element[1].toString());
				cmbcategoryType.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbSubGroupAddData()
	{
		cmbSubGroup.removeAllItems();
	//	int i=0;
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT iSubCategoryID,vSubCategoryName FROM tbRawItemSubCategory where iCategoryID = '"+cmbGroup.getValue().toString()+"' order by iSubCategoryID ").list();

		//	i=1;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSubGroup.addItem(element[0].toString());
				cmbSubGroup.setItemCaption(element[0].toString(), element[1].toString());
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

	public void selectsubsubGroup()
	{
		cmbsubsubCategory.removeAllItems();
		int i=0;
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select iSubSubCategoryID,vSubSubCategoryName from  tbRawItemsubSubCategory where SubGroupid like '"+cmbSubGroup.getValue().toString()+"' order by iSubSubCategoryID").list();

			i=1;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbsubsubCategory.addItem(element[0].toString());
				cmbsubsubCategory.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		if(i==0)
		{
			showNotification("Warning","There are no Sub-Group in this Group");
		}
	}

	private void updateButtonEvent()
	{
		if(!txtRawItemName.getValue().toString().isEmpty())
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
			if(!txtRawItemCode.getValue().toString().isEmpty())
			{
				if(!txtRawItemName.getValue().toString().isEmpty())
				{
					if(cmbGroup.getValue()!=null)
					{
						if(!txtUnit.getValue().toString().isEmpty())
						{
							if(cmbRawItemType.getValue()!=null)
							{
								if(!txtMinLabel.getValue().toString().isEmpty())
								{
									if(!txtReLabel.getValue().toString().isEmpty())
										
									{
										if(!txtMaxLabel.getValue().toString().isEmpty())
										{
											saveButtonEvent();		
										}
										else
										{
											showNotification("Please, provide Maximum Label Qty ",Notification.TYPE_WARNING_MESSAGE);	
										}
										
									}
									else
									{
										showNotification("Please, provide Relebel Label Qty ",Notification.TYPE_WARNING_MESSAGE);	
									}
										
								}
								else
								{
									showNotification("Please, provide Minimum Label Qty ",Notification.TYPE_WARNING_MESSAGE);
								}
								
							}
							else
							{
								getParent().showNotification("Warning","Select Item Type",Notification.TYPE_WARNING_MESSAGE);
								cmbRawItemType.focus();
							}
						}
						else
						{
							getParent().showNotification("Warning","Please provide unit",Notification.TYPE_WARNING_MESSAGE);
							txtUnit.focus();
						}
					}
					else
					{
						getParent().showNotification("Warning","Please select category",Notification.TYPE_WARNING_MESSAGE);
						cmbGroup.focus();
					}
				}
				else
				{
					getParent().showNotification("Warning","Please provide Item name",Notification.TYPE_WARNING_MESSAGE);
					txtRawItemName.focus();
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
		Window win = new RawItemInfoFind(sessionBean, txtItemID,"ItemId");
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
			sql = "select  vRawItemCode,replace(vRawItemName,'~','''')vRawItemName, *  from tbRawItemInfo where vRawItemCode = '"+txtItemId+"' ";
			
			
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtRawItemCode.setValue(element[0]);
				txtRawItemName.setValue(element[1]);
				

				cmbcategoryType.setValue(element[20].toString());
				
				cmbGroup.addItem(element[5]);
				cmbGroup.setItemCaption(element[5].toString(), element[6].toString());
				cmbGroup.setValue(element[5]);
				cmbSubGroup.addItem(element[7]);
				cmbSubGroup.setItemCaption(element[7].toString(), element[8].toString());
				cmbSubGroup.setValue(element[7]);
				if(element[21]!=null)
				{
					txtmodelNo.setValue(element[21]);
				}
              
				txtUnit.setValue(element[9]);

				ledgerCode.setValue(element[10].toString());

				cmbRawItemType.setValue(element[11].toString());
				txtMaxLabel.setValue(df.format(element[15]));
				txtReLabel.setValue(df.format(element[17]));
				txtMinLabel.setValue(df.format(element[16]));
				//cmbsubsubCategory.setValue(element[18].toString());
				
				if(element[22]!=null)
				{
					ProductImage(element[22].toString());
					imageLoc = element[22].toString();	
				}
				
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	public void subGroupLink()
	{
		Window win = new RawItemSubCategory(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbSubGroupAddData();
			}

		});
		this.getParent().addWindow(win);
	}

	public void groupLink()
	{
		Window win = new RawItemCategory(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbGroupAddData();
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
						insertData();
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


	private void insertData()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		System.out.println("AutoID: "+RawItemId+" LedgerId: "+LedgerId);

		String groupId = "";
		String subGroupId = "";
		String ModelNo="";
		
		if(!txtmodelNo.getValue().toString().isEmpty())
		{
			ModelNo=txtmodelNo.getValue().toString();
		}

		String createForm = "";
		String subGroup = "";
		String subGroupCaption = "";
		String parentId = "";

		LedgerId=ledgerId();

		if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Chemical")||
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
		}
		//createForm = "A5-"+cmbGroup.getValue().toString()+""+(cmbSubGroup.getValue()==null?"":"-"+cmbSubGroup.getValue().toString());
		subGroup = cmbSubGroup.getValue()==null?"":"-"+cmbSubGroup.getValue().toString();
		subGroupCaption = "-"+cmbSubGroup.getItemCaption(cmbSubGroup.getValue()==null?"":cmbSubGroup.getValue().toString());


		System.out.println("GROUP: "+cmbGroup.getValue().toString());
		System.out.println("Group Caption: "+cmbGroup.getItemCaption(cmbGroup.getValue().toString()));

		String subGroupId1,SubGroupCaption,subSubGroupId,SubSubGroupCaption;
		if(cmbSubGroup.getValue()==null){
			subGroupId1="";
			SubGroupCaption="";
		}
		else{
			subGroupId1=cmbSubGroup.getValue().toString();
			subGroupCaption=cmbSubGroup.getItemCaption(cmbSubGroup.getValue());
		}

		if(cmbsubsubCategory.getValue()==null){
			subSubGroupId="";
			SubSubGroupCaption="";
		}
		else
		{
			subSubGroupId=cmbsubsubCategory.getValue().toString();
			SubSubGroupCaption=cmbsubsubCategory.getItemCaption(cmbsubsubCategory.getValue());
		}
		
		if(!isUpdate)
		{
			txtRawItemCode.setValue(selectRawItemCode());	
		}

		String imagePathSample = imagePath(1,txtRawItemCode.getValue().toString())==null?imageLoc:imagePath(1,txtRawItemCode.getValue().toString());
		String image="";
		try
		{
			String InsertRawItem = " INSERT into tbRawItemInfo values(" +
					" '"+"RI-"+txtRawItemCode.getValue().toString()+"'," +
					" '"+txtRawItemName.getValue().toString().trim().replaceAll("'","~")+"'," +
					" '"+cmbGroup.getValue().toString()+"'," +
					" '"+cmbGroup.getItemCaption(cmbGroup.getValue().toString())+"'," +
					" '"+subGroupId1+"'," +
					" '"+subGroupCaption+"'," +
					" '"+txtUnit.getValue().toString().trim()+"'," +
					" '"+LedgerId+"', " +
					" '"+cmbRawItemType.getItemCaption(cmbRawItemType.getValue())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+txtMaxLabel.getValue().toLowerCase().trim()+"',"+
					" '"+txtMinLabel.getValue().toString().trim()+"','"+txtReLabel.getValue().toString().trim()+"'," +
					"'"+subSubGroupId+"','"+SubSubGroupCaption+"'," +
							"'"+cmbcategoryType.getValue().toString()+"','"+ModelNo+"','"+imagePathSample+"') ";

			System.out.println("Insertquery : "+InsertRawItem);
			session.createSQLQuery(InsertRawItem).executeUpdate();

			/*if(cmbSubGroup.getValue()!=null)
			{
				parentId = cmbSubGroup.getValue().toString();
			}
			else
			{
				parentId = cmbGroup.getValue().toString();
			}*/

			String InsertLedger="INSERT into tbLedger values(" +
					" '"+LedgerId+"', " +
					" '"+txtRawItemName.getValue().toString().trim().replaceAll("'","~")+"', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+parentId+"', " +
					" '"+createForm+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			System.out.println("InsertLedger : "+InsertLedger);
			session.createSQLQuery(InsertLedger).executeUpdate();

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
		String subGroup = "";
		String subGroupCaption = "";

		String parentId="";

		if(cmbSubGroup.getValue()!=null)
		{
			//createForm = "A5-"+cmbGroup.getValue().toString()+"-"+cmbSubGroup.getValue().toString();
			subGroup = cmbSubGroup.getValue().toString();
			subGroupCaption = cmbSubGroup.getItemCaption(cmbSubGroup.getValue());
		}
		else
		{
			//createForm = "A5-"+cmbGroup.getValue().toString();
			subGroup = "";
			subGroupCaption = "";
		}
		String subSubGroupId,SubSubGroupCaption;
		
		if(cmbsubsubCategory.getValue()==null){
			subSubGroupId="";
			SubSubGroupCaption="";
		}
		else
		{
			subSubGroupId=cmbsubsubCategory.getValue().toString();
			SubSubGroupCaption=cmbsubsubCategory.getItemCaption(cmbsubsubCategory.getValue());
		}

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
					" vGroupId = '"+cmbGroup.getValue().toString()+"' ," +
					" vGroupName = '"+cmbGroup.getItemCaption(cmbGroup.getValue())+"' ," +
					" vSubGroupId = '"+subGroup+"' ," +
					" vSubGroupName = '"+subGroupCaption+"' ," +
					" vUnitName = '"+(txtUnit.getValue().toString().isEmpty()?"":txtUnit.getValue().toString())+"' ," +
					" vProductType = '"+cmbRawItemType.getItemCaption(cmbRawItemType.getValue())+"' ," +
					" vUserName = '"+sessionBean.getUserId()+"', " +
					" vUserIp = '"+sessionBean.getUserIp()+"', " +
					" dEntryTime = CURRENT_TIMESTAMP, mMaxLabel = '"+txtMaxLabel.getValue().toString().trim()+"',"+
					" mMinLabel = '"+txtMinLabel.getValue().toString().trim()+"', mReLabel = '"+txtReLabel.getValue().toString().trim()+"', "+
					" vsubsubCategoryId='"+subSubGroupId+"', "+
					" vCategoryType='"+cmbcategoryType.getValue().toString()+"' ,    "+
					" vSubSubCategoryName='"+SubSubGroupCaption+"', ModelNo='"+txtmodelNo.getValue().toString()+"',imageLoc='"+imagePathSample+"'  "+
					" where vRawItemCode='"+txtRawItemCode.getValue().toString()+"'";
			
			System.out.println(updateRawItem);
			session.createSQLQuery(updateRawItem).executeUpdate();

			if(cmbSubGroup.getValue()!=null)
			{
				parentId = cmbSubGroup.getValue().toString();
			}
			else
			{
				parentId = cmbGroup.getValue().toString();
			}

			if(cmbcategoryType.getValue().toString().equalsIgnoreCase("Chemical")||
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
			}
			
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

			this.getParent().showNotification("All information update successfully.");

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
		categoryType();
	}

	private void focusEnter()
	{
		allComp.add(txtRawItemCode);
		allComp.add(txtRawItemName);
		allComp.add(cmbcategoryType);
		allComp.add(cmbGroup);
		allComp.add(cmbSubGroup);
		allComp.add(txtmodelNo);
		allComp.add(txtUnit);
		allComp.add(txtMaxLabel);
		allComp.add(txtReLabel);
		allComp.add(txtMinLabel);
		allComp.add(cmbRawItemType);
		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public void txtClear()
	{
		txtRawItemCode.setValue("");
		txtRawItemName.setValue("");
		cmbGroup.setValue(null);
		cmbSubGroup.setValue(null);
		txtmodelNo.setValue("");
		txtUnit.setValue("");
		cmbRawItemType.setValue(null);
		txtMaxLabel.setValue("");
		txtMinLabel.setValue("");
		txtReLabel.setValue("");
		//cmbsubsubCategory.setValue(null);
		cmbcategoryType.setValue(null);
		image.image.removeAllComponents();
		image.status.setValue("Please select a image file(jpg) to upload");
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
		cmbGroup.setEnabled(!b);
		cmbSubGroup.setEnabled(!b);
		nbGroup.setEnabled(!b);
		nbSubGroup.setEnabled(!b);
		txtmodelNo.setEnabled(!b);
		txtUnit.setEnabled(!b);
		cmbRawItemType.setEnabled(!b);
		txtMaxLabel.setEnabled(!b);
		txtMinLabel.setEnabled(!b);
		txtReLabel.setEnabled(!b);
		//cmbsubsubCategory.setEnabled(!b);
		cmbcategoryType.setEnabled(!b);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("650px");
		setHeight("490px");

		// lblRawItemCode
		lblRawItemCode = new Label("Item Code: ");
		lblRawItemCode.setImmediate(true);
		lblRawItemCode.setWidth("100.0%");
		lblRawItemCode.setHeight("18px");
		mainLayout.addComponent(lblRawItemCode,"top:30.0px;left:50.0px;");

		// txtRawItemCode
		txtRawItemCode = new TextRead();
		txtRawItemCode.setImmediate(false);
		txtRawItemCode.setWidth("100px");
		txtRawItemCode.setHeight("23px");
		mainLayout.addComponent(txtRawItemCode, "top:27.0px;left:190.0px;");

		// lblRawItemName
		lblRawItemName = new Label("Item Name: ");
		lblRawItemName.setImmediate(false);
		lblRawItemName.setWidth("-1px");
		lblRawItemName.setHeight("-1px");
		mainLayout.addComponent(lblRawItemName, " top:55.0px;left:50.0px;");

		// txtRawItemName
		txtRawItemName = new TextField();
		txtRawItemName.setImmediate(false);
		txtRawItemName.setWidth("318px");
		txtRawItemName.setHeight("-1px");
		txtRawItemName.setSecret(false);
		mainLayout.addComponent(txtRawItemName, "top:53.0px;left:190.0px;");

		lblCategoryType = new Label("Category Type: ");
		lblCategoryType.setImmediate(false);
		lblCategoryType.setWidth("-1px");
		lblCategoryType.setHeight("-1px");
		mainLayout.addComponent(lblCategoryType, "top:82.0px;left:50.0px;");

		cmbcategoryType = new ComboBox();
		cmbcategoryType.setImmediate(true);
		cmbcategoryType.setWidth("318px");
		cmbcategoryType.setHeight("24px");
		cmbcategoryType.setNullSelectionAllowed(true);
		cmbcategoryType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbcategoryType, "top:79.0px;left:190.0px;");		

		// lblGroup
		lblGroup = new Label("Category: ");
		lblGroup.setImmediate(false);
		lblGroup.setWidth("-1px");
		lblGroup.setHeight("-1px");
		mainLayout.addComponent(lblGroup, "top:108.0px;left:50.0px;");

		// cmbCategory
		cmbGroup = new ComboBox();
		cmbGroup.setImmediate(true);
		cmbGroup.setWidth("318px");
		cmbGroup.setHeight("24px");
		cmbGroup.setNullSelectionAllowed(true);
		cmbGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbGroup, "top:105.0px;left:190.0px;");

		// nbGroup
		nbGroup = new NativeButton();
		nbGroup.setIcon(new ThemeResource("../icons/add.png"));
		nbGroup.setImmediate(true);
		nbGroup.setWidth("32px");
		nbGroup.setHeight("20px");
		mainLayout.addComponent(nbGroup, " top:105.0px;left:515.0px;");

		// lblSubGroup
		lblSubGroup = new Label("Sub-Category: ");
		lblSubGroup.setImmediate(false);
		lblSubGroup.setWidth("-1px");
		lblSubGroup.setHeight("-1px");
		mainLayout.addComponent(lblSubGroup, "top:134.0px;left:50.0px;");

		// cmbSubGroup
		cmbSubGroup = new ComboBox();
		cmbSubGroup.setImmediate(false);
		cmbSubGroup.setWidth("318px");
		cmbSubGroup.setHeight("-1px");
		cmbSubGroup.setNullSelectionAllowed(true);
		cmbSubGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSubGroup, "top:131.0px;left:190.0px;");

		// nbSubGroup
		nbSubGroup = new NativeButton();
		nbSubGroup.setIcon(new ThemeResource("../icons/add.png"));	
		nbSubGroup.setImmediate(true);
		nbSubGroup.setWidth("32px");
		nbSubGroup.setHeight("20px");
		mainLayout.addComponent(nbSubGroup, "top:131.0px;left:515.0px;");

		lblsubsubCategory = new Label("Sub Sub-Category: ");
		lblsubsubCategory.setImmediate(false);
		lblsubsubCategory.setWidth("-1px");
		lblsubsubCategory.setHeight("-1px");
		//mainLayout.addComponent(lblsubsubCategory, "top:160.0px;left:50.0px;");

		cmbsubsubCategory = new ComboBox();
		cmbsubsubCategory.setImmediate(false);
		cmbsubsubCategory.setWidth("318px");
		cmbsubsubCategory.setHeight("-1px");
		cmbsubsubCategory.setNullSelectionAllowed(true);
		cmbsubsubCategory.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		//mainLayout.addComponent(cmbsubsubCategory, "top:157.0px;left:190.0px;");
	
		//lblModel
		lblModelNo = new Label("Model No :");
		lblModelNo.setImmediate(false);
		lblModelNo.setWidth("-1px");
		lblModelNo.setHeight("-1px");
		mainLayout.addComponent(lblModelNo, "top:160.0px;left:50.0px;");

		// txtmodelNo
		txtmodelNo = new TextField();
		txtmodelNo.setImmediate(false);
		txtmodelNo.setWidth("140px");
		txtmodelNo.setHeight("-1px");
		txtmodelNo.setSecret(false);
	    mainLayout.addComponent(txtmodelNo, "top:157.0px;left:190.0px;");

		// lblUnit
		lblUnit = new Label("Unit :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:186.0px;left:50.0px;");

		// txtUnit
		txtUnit = new TextField();
		txtUnit.setImmediate(false);
		txtUnit.setWidth("100px");
		txtUnit.setHeight("-1px");
		txtUnit.setSecret(false);
		mainLayout.addComponent(txtUnit, "top:183.0px;left:190.0px;");

		// lblUnit
		lblMaxLavel = new Label("Max Level :");
		lblMaxLavel.setImmediate(false);
		lblMaxLavel.setWidth("-1px");
		lblMaxLavel.setHeight("-1px");
		mainLayout.addComponent(lblMaxLavel, "top:212.0px;left:50.0px;");

		// txtUnit
		txtMaxLabel = new AmountCommaSeperator();
		txtMaxLabel.setImmediate(false);
		txtMaxLabel.setWidth("100px");
		txtMaxLabel.setHeight("-1px");
		txtMaxLabel.setSecret(false);
		mainLayout.addComponent(txtMaxLabel, "top:209.0px;left:190.0px;");
		
		
		lblReLavel = new Label("Re Level :");
		lblReLavel.setImmediate(false);
		lblReLavel.setWidth("-1px");
		lblReLavel.setHeight("-1px");
		mainLayout.addComponent(lblReLavel, "top:238.0px;left:50.0px;");

		// txtUnit
		txtReLabel = new AmountCommaSeperator();
		txtReLabel.setImmediate(false);
		txtReLabel.setWidth("100px");
		txtReLabel.setHeight("-1px");
		txtReLabel.setSecret(false);
		mainLayout.addComponent(txtReLabel, "top:235.0px;left:190.0px;");


		// lblUnit
		lblMinLavel = new Label("Min Level :");
		lblMinLavel.setImmediate(false);
		lblMinLavel.setWidth("-1px");
		lblMinLavel.setHeight("-1px");
		mainLayout.addComponent(lblMinLavel, "top:264.0px;left:50.0px;");

		// txtUnit
		txtMinLabel = new AmountCommaSeperator();
		txtMinLabel.setImmediate(false);
		txtMinLabel.setWidth("100px");
		txtMinLabel.setHeight("-1px");
		txtMinLabel.setSecret(false);
		mainLayout.addComponent(txtMinLabel, "top:261.0px;left:190.0px;");

		
		// lblRawItemType
		lblRawItemType = new Label("Item Type :");
		lblRawItemType.setImmediate(false);
		lblRawItemType.setWidth("-1px");
		lblRawItemType.setHeight("-1px");
		mainLayout.addComponent(lblRawItemType, "top:297.0px;left:50.0px;");

		// cmbRawItemType
		cmbRawItemType = new ComboBox();
		cmbRawItemType.setImmediate(true);
		cmbRawItemType.setWidth("150px");
		cmbRawItemType.setHeight("-1px");
		cmbRawItemType.setNullSelectionAllowed(true);
		cmbRawItemType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbRawItemType, "top:291.0px;left:190.0px;");
		for(int i=0; i<itemType.length; i++)
		{cmbRawItemType.addItem(itemType[i]);}

		mainLayout.addComponent(button, "top:390.0px;left:55.0px;");
		
		// lblSampleImage
		lblSampleImage = new Label("Sample Image :");
		lblSampleImage.setImmediate(false);
		lblSampleImage.setWidth("-1px");
		lblSampleImage.setHeight("-1px");
		//mainLayout.addComponent(lblSampleImage, "top:333.0px;left:360.0px;");

		image = new FileUpload("Picture");
		image.upload.setButtonCaption("Sample Image");
		mainLayout.addComponent(image, "top:190.0px;left:380.0px;");

		return mainLayout;
	}
}
