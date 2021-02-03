package com.example.productionSetup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.hrmModule.SectionInformation;
//import acc.appform.inventoryModule.OpeningStockFindWindow;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExample;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
//import com.reportform.inventoryReport.Indentrpt;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
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

public class StandardFormFinished extends Window 
{
	SessionBean sessionBean;

	private Label lblProductName;
	private Label lblProductCode;
	private Label lblPerBatchCtn;
	//	private Label lblPerCtnQty;
	private Label lblQty;
	private Label lblProcessLoss;
	private Label lblRawMaterials;
	private Label lblInk;
	private Label lblPackingMaterials;
	private TextField productId = new TextField();
	
	private Label lblDia;
	private AmountField afDia;

	private Label lblDate;

	private ComboBox cmbProductName;
	private TextRead txtProductCode;
	private TextRead txtProductUnit;
	private AmountField afQty;

	private static final String[] ctnUnit = new String[]{"ctn"};
	private static final String[] qtyUnit = new String[]{"pkt","pcs"};
	private static final String[] wtUnit = new String[]{"gm","kg"};

	private PopupDateField dateField;

	private Table tableRaw = new Table();
	private ArrayList<Label> txtSlRaw = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbItemRaw = new ArrayList<ComboBox>();
	private ArrayList<TextRead> unitNameRaw = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> reqQtyRaw = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> groupIdRaw = new ArrayList<TextRead>();
	private ArrayList<TextRead> subGroupIdRaw = new ArrayList<TextRead>();
	private ArrayList<TextRead> categoryTypeRaw = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> processLossRaw = new ArrayList<AmountCommaSeperator>();

	private Table tableInk = new Table();
	private ArrayList<Label> txtSlInk = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbItemInk = new ArrayList<ComboBox>();
	private ArrayList<TextRead> unitNameInk = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> reqQtyInk = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> groupIdInk = new ArrayList<TextRead>();
	private ArrayList<TextRead> subgroupIdInk = new ArrayList<TextRead>();
	private ArrayList<TextRead> categoryTypeInk = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> processLossInk = new ArrayList<AmountCommaSeperator>();

	private Table tablePacking = new Table();
	private ArrayList<Label> txtSlPacking = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbItemPacking = new ArrayList<ComboBox>();
	private ArrayList<TextRead> unitNamePacking = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> reqQtyPacking = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> groupIdPacking = new ArrayList<TextRead>();
	private ArrayList<TextRead> subGroupIdPacking = new ArrayList<TextRead>();
	private ArrayList<TextRead> categoryTypePacking = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> processLossPacking = new ArrayList<AmountCommaSeperator>();

	private Label lblLine;
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "Preview", "", "Exit");

	boolean isUpdate = false;
	boolean isFind=false;

	private DecimalFormat df = new DecimalFormat("#0.00");
	SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormatShow = new SimpleDateFormat("dd-MM-yyyy");
	ArrayList<Component> allComp = new ArrayList<Component>();

	Button btnPreview;

	private AbsoluteLayout mainLayout;

	public StandardFormFinished(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("PRODUCT STANDARD SETTINGS :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		tableInitialiseRaw();
		tableInitialiseCream();
		tableInitialisePacking();

		//cmbProductData();
		setEventAction();
		btnIni(true);
		componentIni(true);
		focusEnter();
		authenticationCheck();

		cButton.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			cButton.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			cButton.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			cButton.btnDelete.setVisible(false);
		}
	}

	private void focusEnter()
	{
		allComp.add(cmbProductName);
		allComp.add(afDia);
		allComp.add(afQty);
		allComp.add(dateField);

		for(int i=0;i<cmbItemRaw.size();i++)
		{
			allComp.add(cmbItemRaw.get(i));
			allComp.add(reqQtyRaw.get(i));
			allComp.add(unitNameRaw.get(i));
			allComp.add(processLossRaw.get(i));
		}

		for(int i=0;i<cmbItemInk.size();i++)
		{
			allComp.add(cmbItemInk.get(i));
			allComp.add(reqQtyInk.get(i));
			allComp.add(unitNameInk.get(i));
			allComp.add(processLossInk.get(i));
		}

		for(int i=0;i<cmbItemPacking.size();i++)
		{
			allComp.add(cmbItemPacking.get(i));
			allComp.add(reqQtyPacking.get(i));
			allComp.add(unitNamePacking.get(i));
			allComp.add(processLossPacking.get(i));
		}

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);
		allComp.add(cButton.btnFind);

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

	private void componentIni(boolean b) 
	{
		cmbProductName.setEnabled(!b);
		txtProductCode.setEnabled(!b);
		txtProductUnit.setEnabled(!b);
		afDia.setEnabled(!b);
		afQty.setEnabled(!b);
		dateField.setEnabled(!b);
		tableRaw.setEnabled(!b);
		tableInk.setEnabled(!b);
		tablePacking.setEnabled(!b);
	}

	private void cmbProductData() 
	{
		cmbProductName.removeAllItems();

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = "select vProductId,vProductName from tbFinishedProductInfo ORDER by iAutoId ";
			System.out.println("cmbProductName: "+sql);

			List list = session.createSQLQuery(sql).list();

			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbProductName.addItem(element[0].toString());
				cmbProductName.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}

	public void setEventAction()
	{
		cButton.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				cmbProductName.focus();
				isUpdate = false;
				isFind = false;
				cmbProductData();
			}
		});

		cButton.btnSave.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isSubmitable())
				{
					if(cmbProductName.getValue()!=null)
					{
						//						if(cmbCtnUnit.getValue()!=null)
						//						{

						if(!afQty.getValue().toString().isEmpty())
						{
							//								if(cmbWtUnit.getValue()!=null)
							//								{
							if(nullCheckRaw())
							{
								saveButtonEvent();
							}
							else
							{
								showNotification("Warning,","please Provide All Fields",Notification.TYPE_WARNING_MESSAGE);
							}
							//								}
							//								else
							//								{
							//									showNotification("Warning,","Please Provide Wt. Unit",Notification.TYPE_WARNING_MESSAGE);
							//									cmbWtUnit.focus();
							//								}

						}
						else
						{
							showNotification("Warning,","Please Provide Per Packet Wt.",Notification.TYPE_WARNING_MESSAGE);
							afQty.focus();
						}
						//						}
						//						else
						//						{
						//							showNotification("Warning,","Please Provide Ctn. Unit",Notification.TYPE_WARNING_MESSAGE);
						//							cmbCtnUnit.focus();
						//						}
					}
				}
			}

		});

		cButton.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isUpdateable())
				{
					updateButtonEvent();
					isFind = false;
					cmbProductName.focus();
				}
				else
				{
					showNotification("Warning,","You are not permitted to edit data.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnDelete.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isDeleteable())
				{
					deleteButtonEvent();
				}
				else
				{
					showNotification("Warning,","You are not permitted to delete data.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				isFind = false;
				cButton.btnNew.focus();
			}
		});

		cButton.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
				isFind = true;
			}
		});

		cButton.btnPreview.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				//				previewButtonEvent();
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbProductName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProductName.getValue()!=null)
				{

					if(!isFind)
					{
						cmbValueDuplicateCheck();
					}
					else
					{
						dataAddFields();
						for(int i=0;i<cmbItemRaw.size();i++){
							cmbItemRawAdd(i);
						}

						for(int i=0;i<cmbItemInk.size();i++){
							cmbItemInkAdd(i);
						}

						for(int i=0;i<cmbItemPacking.size();i++){
							cmbItemPackingAdd(i);
						}
					}
				}
			}
		});

		/*btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				// Hyperlink to a given URL
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString();

					if(link.endsWith("ZFoods/"))
					{
						link = link.replaceAll("ZFoods", "report")+filePathTmp;
					}
					else if(link.endsWith("MSML/"))
					{
						link = link.replaceAll("MSML", "report")+filePathTmp;
					}
					else if(link.endsWith("ZahidFoods/"))
					{
						link = link.replaceAll("ZahidFoods", "report")+filePathTmp;
					}
					else if(link.endsWith("RJSL/"))
					{
						link = link.replaceAll("RJSL", "report")+filePathTmp;
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

							if(link.endsWith("ZFoods/"))
							{
								link = link.replaceAll("ZFoods/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("ZahidFoods/"))
							{
								link = link.replaceAll("ZahidFoods/", imageLoc.substring(23, imageLoc.length()));
							}
							else if(link.endsWith("MSML/"))
							{
								link = link.replaceAll("MSML/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("RJSL/"))
							{
								link = link.replaceAll("RJSL/", imageLoc.substring(22, imageLoc.length()));
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

						if(link.endsWith("ZFoods/"))
						{
							link = link.replaceAll("ZFoods", "report")+filePathTmp;
						}
						else if(link.endsWith("ZahidFoods/"))
						{
							link = link.replaceAll("ZahidFoods", "report")+filePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+filePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+filePathTmp;
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
		});*/
	}

	private void cmbItemRawAdd(int ar)
	{
		cmbItemRaw.get(ar).removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String sql = "";
			sql = "Select vRawItemCode,vRawItemName from tbRawItemInfo where vCategoryType = 'Raw Material' ";

			List lst = session.createSQLQuery(sql).list();
			System.out.println(sql);

			for (Iterator iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbItemRaw.get(ar).addItem(element[0]);
				cmbItemRaw.get(ar).setItemCaption(element[0] , element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private void cmbItemInkAdd(int ar)
	{
		cmbItemInk.get(ar).removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			List lst = session.createSQLQuery("Select vRawItemCode,vRawItemName from tbRawItemInfo where vCategoryType = 'Ink' ").list();

			for (Iterator iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbItemInk.get(ar).addItem(element[0].toString());
				cmbItemInk.get(ar).setItemCaption(element[0].toString() , element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private void cmbItemPackingAdd(int ar)
	{
		cmbItemPacking.get(ar).removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			List lst = session.createSQLQuery("Select vRawItemCode,vRawItemName from tbRawItemInfo where vCategoryType = 'Packing Material'").list();

			for (Iterator iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbItemPacking.get(ar).addItem(element[0]);
				cmbItemPacking.get(ar).setItemCaption(element[0] , element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private void cmbValueDuplicateCheck()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String sql= "Select vProductId from tbStandardFinishedInfo where vProductId='"+cmbProductName.getValue().toString().replaceAll("#", "")+"' ";
			System.out.println(sql);

			List list = session.createSQLQuery(sql).list();
			Iterator iter=list.iterator();

			System.out.println("duplicate: "+sql);

			if(iter.hasNext())
			{
				cmbProductName.setValue(null);
				showNotification("Standard Information Already Given!",Notification.TYPE_WARNING_MESSAGE);
				cmbProductName.focus();
			}
			else
			{
		dataAddFields();
		for(int i=0;i<cmbItemRaw.size();i++){
			cmbItemRawAdd(i);
		}
				for(int i=0;i<cmbItemInk.size();i++){
					cmbItemInkAdd(i);
				}

				for(int i=0;i<cmbItemPacking.size();i++){
					cmbItemPackingAdd(i);
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private void dataAddFields()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String selectQuery = " select vProductId,vUnitName from tbFinishedProductInfo" +
					" where vProductId = '"+cmbProductName.getValue().toString().trim().replaceAll("#", "")+"'";

			System.out.println("dataAddFields : "+selectQuery);
			List list = session.createSQLQuery(selectQuery).list();

			if(list.iterator().hasNext())
			{
				Object[] element = (Object[]) list.iterator().next();

				txtProductCode.setValue(element[0]);
				txtProductUnit.setValue(element[1]);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	/*

	private void cmbProductAdd(int ar)
	{
		cmbProduct.get(ar).removeAllItems();
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			List lst = session.createSQLQuery("select productId, productName from tbRawProductInfo order by productId ").list();

			for (Iterator iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbProduct.get(ar).addItem(element[0]);
				cmbProduct.get(ar).setItemCaption(element[0] , element[1].toString());
			}
		}catch(Exception ex){
			System.out.println(ex);
		}
	}

	private void previewButtonEvent()
	{
		Window win = new Indentrpt(sessionBean,"Indent");
		win.setStyleName("cwindow");
		win.setModal(true);

		this.getParent().addWindow(win);
	}
	 */
	private void findButtonEvent() 
	{
		Window win = new FindWindowStandard(sessionBean, productId);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (productId.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(productId.getValue().toString());
					cButton.btnEdit.focus();
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String productId) 
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql = "select vProductId, mDia, mQty, dDeclaredDate from tbStandardFinishedInfo where vProductId='"+productId+ "'";
			System.out.println(sql);
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();
				cmbProductName.setValue(element[0].toString());
				afDia.setValue(df.format(element[1]));
				afQty.setValue(df.format(element[2]));
				dateField.setValue((element[3]));
			}
			dataAddFields();

			String query = "Select vRawItemCode, mReqQty, vUnitName, vGroupId, vSubGroupId, mProcessLoss from tbStandardFinishedDetails  where vProductId='"+productId+ "' and vCategoryType = 'Raw Material' ";
			System.out.println(query);
			List list = session.createSQLQuery(query).list();
			int i = 0;
			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbItemRaw.get(i).setValue(element[0].toString());
				reqQtyRaw.get(i).setValue(df.format(element[1]));
				unitNameRaw.get(i).setValue((element[2]));
				groupIdRaw.get(i).setValue((element[3]));
				subGroupIdRaw.get(i).setValue((element[4]));
				processLossRaw.get(i).setValue(df.format(element[5]));

				i++;
			}

			String queryCream = "Select vRawItemCode, mReqQty, vUnitName, vGroupId, vSubGroupId, mProcessLoss from tbStandardFinishedDetails  where vProductId='"+productId+ "' and vCategoryType = 'Ink'";
			System.out.println(queryCream);
			List listCream = session.createSQLQuery(queryCream).list();
			int k = 0;
			for (Iterator iter = listCream.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbItemInk.get(k).setValue(element[0].toString());
				reqQtyInk.get(k).setValue(df.format(element[1]));
				unitNameInk.get(k).setValue((element[2]));
				groupIdInk.get(k).setValue((element[3]));
				subgroupIdInk.get(k).setValue((element[4]));
				processLossInk.get(i).setValue(df.format(element[5]));

				k++;
			}

			String queryPacking = "Select vRawItemCode, mReqQty, vUnitName, vGroupId, vSubGroupId, mProcessLoss from tbStandardFinishedDetails  where vProductId='"+productId+ "' and vCategoryType = 'Packing Material'";
			System.out.println(queryPacking);
			List listPacking = session.createSQLQuery(queryPacking).list();
			int j = 0;
			for (Iterator iter = listPacking.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbItemPacking.get(j).setValue(element[0].toString());
				reqQtyPacking.get(j).setValue(df.format(element[1]));
				unitNamePacking.get(j).setValue((element[2]));
				groupIdPacking.get(j).setValue((element[3]));
				subGroupIdPacking.get(j).setValue((element[4]));
				processLossPacking.get(i).setValue(df.format(element[5]));

				j++;
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);

		txtClear();
	}

	private void deleteButtonEvent() 
	{
		if (cmbProductName.getValue() != null) 
		{
			this.getParent().addWindow(
					new YesNoDialog("", "Do you want to update information?",
							new YesNoDialog.Callback() 
					{	
						public void onDialogResult(boolean yes) 
						{
							if (yes) 
							{
								Session session = SessionFactoryUtil.getInstance().getCurrentSession();
								Transaction tx = session.beginTransaction();

								if (deleteData(session, tx)) 
								{
									tx.commit();
									getParent().showNotification("All information delete Successfully");
									refreshButtonEvent();
								}
								else 
								{
									tx.rollback();
									getParent().showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
								}
							}
						}
					}));
		} 
		else
		{
			this.getParent().showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void updateButtonEvent() 
	{
		if (cmbProductName.getValue() != null) 
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveButtonEvent() 
	{
		if (!cmbProductName.getValue().toString().trim().isEmpty()) 
		{
			if (isUpdate) 
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener() 
				{
					public void buttonClicked(ButtonType buttonType) 
					{
						if (buttonType == ButtonType.YES) 
						{
							Transaction tx = null;
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();

							tx = session.beginTransaction();

							if (deleteData(session, tx) && nullCheckRaw())
							{
								insertData();
							}
							else 
							{
								tx.rollback();
							}

							isUpdate = false;

							txtClear();
							componentIni(true);
							btnIni(true);
						}
					}
				});
			} 
			else
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener() 
				{
					public void buttonClicked(ButtonType buttonType) 
					{
						if(buttonType == ButtonType.YES)
						{
							insertData();
							txtClear();
							componentIni(true);
							btnIni(true);
						}
					}
				});
			}
		} 
		else
		{
			this.getParent().showNotification("Warning :","Please Select Product .",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void insertData()
	{

		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		try
		{

			String sql = " insert into tbStandardFinishedInfo " +
					" (vProductId, vProductName, mDia, mTubePer, vUnitName,  dDeclaredDate, userId, userIp, entryTime)" +
					" values" +
					" ('"+cmbProductName.getValue().toString().trim()+"', '"+cmbProductName.getItemCaption(cmbProductName.getValue().toString()).toString().trim()+"', '"+afDia.getValue().toString().trim()+"', '"+afQty.getValue().toString().trim()+"', '"+txtProductUnit.getValue().toString().trim()+"', '"+dateFormat.format(dateField.getValue())+"', '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP)";	
			System.out.println(sql);
			for (int i = 0; i < cmbItemRaw.size(); i++)
			{
				System.out.println("for");
				if (!reqQtyRaw.get(i).getValue().toString().isEmpty())
				{
					System.out.println("if");

					String query = 	" insert into tbStandardFinishedDetails " +
							" (vProductId, vRawItemCode, vRawItemName, mReqQty, vUnitName, userId, userIp, entryTime, vGroupId, vSubGroupId,vCategoryType,mProcessLoss)" +
							" values" +
							" ('"+cmbProductName.getValue().toString().trim()+"', '"+cmbItemRaw.get(i).getValue().toString().trim()+"', '"+cmbItemRaw.get(i).getItemCaption(cmbItemRaw.get(i).getValue()).toString().trim()+"', '"+reqQtyRaw.get(i).getValue().toString().trim()+"', '"+unitNameRaw.get(i).getValue().toString().trim()+"', '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, '"+groupIdRaw.get(i).getValue().toString().trim()+"', '"+subGroupIdRaw.get(i).getValue().toString().trim()+"','"+categoryTypeRaw.get(i).getValue().toString().trim()+"','"+processLossRaw.get(i).getValue().toString().trim()+"')";
					System.out.println(query);
					session.createSQLQuery(query).executeUpdate();
					//												if(i==0){
					//													session.createSQLQuery(sql).executeUpdate();
					//												}
				}
			}

			for (int i = 0; i < cmbItemInk.size(); i++)
			{
				System.out.println("for");
				if (!reqQtyInk.get(i).getValue().toString().isEmpty())
				{
					System.out.println("if");

					String sqlCream = 	" insert into tbStandardFinishedDetails " +
							" (vProductId, vRawItemCode, vRawItemName, mReqQty, vUnitName, userId, userIp, entryTime, vGroupId, vSubGroupId,vCategoryType,mProcessLoss)" +
							" values" +
							" ('"+cmbProductName.getValue().toString().trim()+"', '"+cmbItemInk.get(i).getValue().toString().trim()+"', '"+cmbItemInk.get(i).getItemCaption(cmbItemInk.get(i).getValue()).toString().trim()+"', '"+reqQtyInk.get(i).getValue().toString().trim()+"', '"+unitNameInk.get(i).getValue().toString().trim()+"', '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, '"+groupIdInk.get(i).getValue().toString().trim()+"', '"+subgroupIdInk.get(i).getValue().toString().trim()+"','"+categoryTypeInk.get(i).getValue().toString().trim()+"','"+processLossInk.get(i).getValue().toString().trim()+"')";
					System.out.println(sqlCream);
					session.createSQLQuery(sqlCream).executeUpdate();					
				}
			}

			for (int i = 0; i < cmbItemPacking.size(); i++)
			{
				System.out.println("for");
				if (!reqQtyPacking.get(i).getValue().toString().isEmpty())
				{
					System.out.println("if");

					String sqlPacking = 	" insert into tbStandardFinishedDetails " +
							" (vProductId, vRawItemCode, vRawItemName, mReqQty, vUnitName, userId, userIp, entryTime, vGroupId, vSubGroupId,vCategoryType,mProcessLoss)" +
							" values" +
							" ('"+cmbProductName.getValue().toString().trim()+"', '"+cmbItemPacking.get(i).getValue().toString().trim()+"', '"+cmbItemPacking.get(i).getItemCaption(cmbItemPacking.get(i).getValue()).toString().trim()+"', '"+reqQtyPacking.get(i).getValue().toString().trim()+"', '"+unitNamePacking.get(i).getValue().toString().trim()+"', '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, '"+groupIdPacking.get(i).getValue().toString().trim()+"', '"+subGroupIdPacking.get(i).getValue().toString().trim()+"','"+categoryTypePacking.get(i).getValue().toString().trim()+"','"+processLossPacking.get(i).getValue().toString().trim()+"')";
					System.out.println(sqlPacking);
					session.createSQLQuery(sqlPacking).executeUpdate();

				}
			}

			session.createSQLQuery(sql).executeUpdate();

			/*if(!isUpdate)
			{
				trackMethod("New", session, tx);
			}
			else
			{
				trackMethod("Update", session, tx);
			}
			 */
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			System.out.println(exp);
		}
	}

	private void trackMethod(String getTrack, Session session, Transaction tx)
	{
		String trackName = getTrack;
		int trackId = 0;
		trackId = Integer.parseInt(trackAutoId());

		String sql = " insert into tbUdStandardFinishedInfo " +
				" (productId, productName, perBatchCtn, ctnUnit, perCtnQty, qtyUnit, perPackWt, wtUnit, declaredDate, userId, userIp, entryTime, trackId, trackName)" +
				" values" +
				" ('"+cmbProductName.getValue().toString().trim()+"', '"+cmbProductName.getItemCaption(cmbProductName.getValue().toString()).toString().trim()+"', '', '', '', '', '"+afQty.getValue().toString().trim()+"', '', '"+dateFormat.format(dateField.getValue())+"', '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, '"+trackId+"','"+trackName+"')";	
		System.out.println(sql);
		for (int i = 0; i < cmbItemRaw.size(); i++)
		{
			System.out.println("for");
			if (!reqQtyRaw.get(i).getValue().toString().isEmpty())
			{
				System.out.println("if");

				String query = 	" insert into tbUdStandardFinishedDetails " +
						" (productId, itemId, itemName, reqQty, unitName, userId, userIp, entryTime, Group_Id, Sub_Group_Id, trackId, creamFlag)" +
						" values" +
						" ('"+cmbProductName.getValue().toString().trim()+"', '"+cmbItemRaw.get(i).getValue().toString().trim()+"', '"+cmbItemRaw.get(i).getItemCaption(cmbItemRaw.get(i).getValue()).toString().trim()+"', '"+reqQtyRaw.get(i).getValue().toString().trim()+"', '"+unitNameRaw.get(i).getValue().toString().trim()+"', '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, '"+groupIdRaw.get(i).getValue().toString().trim()+"', '"+subGroupIdRaw.get(i).getValue().toString().trim()+"', '"+trackId+"','')";
				System.out.println(query);
				session.createSQLQuery(query).executeUpdate();
				//												if(i==0){
				//													session.createSQLQuery(sql).executeUpdate();
				//												}
			}
		}

		for (int i = 0; i < cmbItemInk.size(); i++)
		{
			System.out.println("for");
			if (!reqQtyInk.get(i).getValue().toString().isEmpty())
			{
				System.out.println("if");

				String sqlCream = 	" insert into tbUdStandardFinishedDetails " +
						" (productId, itemId, itemName, reqQty, unitName, userId, userIp, entryTime, Group_Id, Sub_Group_Id, trackId,creamFlag)" +
						" values" +
						" ('"+cmbProductName.getValue().toString().trim()+"', '"+cmbItemInk.get(i).getValue().toString().trim()+"', '"+cmbItemInk.get(i).getItemCaption(cmbItemInk.get(i).getValue()).toString().trim()+"', '"+reqQtyInk.get(i).getValue().toString().trim()+"', '"+unitNameInk.get(i).getValue().toString().trim()+"', '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, '"+groupIdRaw.get(i).getValue().toString().trim()+"', '"+subGroupIdRaw.get(i).getValue().toString().trim()+"', '"+trackId+"','cream')";
				System.out.println(sqlCream);
				session.createSQLQuery(sqlCream).executeUpdate();
				if(i==0){
					session.createSQLQuery(sql).executeUpdate();
				}
			}
		}	

		for (int i = 0; i < cmbItemPacking.size(); i++)
		{
			System.out.println("for");
			if (!reqQtyPacking.get(i).getValue().toString().isEmpty())
			{
				System.out.println("if");

				String sqlPacking = 	" insert into tbUdStandardFinishedDetails " +
						" (productId, itemId, itemName, reqQty, unitName, userId, userIp, entryTime, Group_Id, Sub_Group_Id, trackId,creamFlag)" +
						" values" +
						" ('"+cmbProductName.getValue().toString().trim()+"', '"+cmbItemPacking.get(i).getValue().toString().trim()+"', '"+cmbItemPacking.get(i).getItemCaption(cmbItemPacking.get(i).getValue()).toString().trim()+"', '"+reqQtyPacking.get(i).getValue().toString().trim()+"', '"+unitNamePacking.get(i).getValue().toString().trim()+"', '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP, '"+groupIdPacking.get(i).getValue().toString().trim()+"', '"+subGroupIdPacking.get(i).getValue().toString().trim()+"', '"+trackId+"','')";
				System.out.println(sqlPacking);
				session.createSQLQuery(sqlPacking).executeUpdate();
				if(i==0){
					session.createSQLQuery(sql).executeUpdate();
				}
			}
		}		
	}

	public String trackAutoId() 
	{
		String autoCode = "";

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "select cast(isnull(max(cast(replace(trackId, '', '')as int))+1, 1)as varchar) from tbUdStandardFinishedInfo";

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

	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete tbStandardFinishedInfo where vProductId='"+cmbProductName.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbStandardFinishedInfo where vProductId='"+cmbProductName.getValue()+ "' ");

			session.createSQLQuery("delete tbStandardFinishedDetails where vProductId='"+cmbProductName.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbStandardFinishedDetails where vProductId='"+cmbProductName.getValue()+ "' ");

			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private boolean nullCheckRaw() 
	{
		if (cmbProductName.getValue() != null)
		{
			for (int i = 0; i < cmbItemRaw.size(); i++) 
			{
				Object temp = cmbItemRaw.get(i).getItemCaption(cmbItemRaw.get(i).getValue());

				System.out.println(cmbItemRaw.get(i).getValue());

				if (temp != null && !cmbItemRaw.get(i).getValue().equals(("x#" + i))) 
				{
					if (!reqQtyRaw.get(i).getValue().toString().trim().isEmpty()) 
					{
						return true;
					} 
					else
					{
						this.getParent().showNotification("Warning :","Please Enter Valid Req. Qty .",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Section To .",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}

	private boolean nullCheckPacking() 
	{
		if (cmbProductName.getValue() != null)
		{
			for (int i = 0; i < cmbItemPacking.size(); i++) 
			{
				Object temp = cmbItemPacking.get(i).getItemCaption(cmbItemPacking.get(i).getValue());

				System.out.println(cmbItemPacking.get(i).getValue());

				if (temp != null && !cmbItemPacking.get(i).getValue().equals(("x#" + i))) 
				{
					if (!reqQtyPacking.get(i).getValue().toString().trim().isEmpty()) 
					{
						return true;
					} 
					else
					{
						this.getParent().showNotification("Warning :","Please Enter Valid Req. Qty .",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Section To .",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}


	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);

		txtClear();
	}

	public void txtClear()
	{
		cmbProductName.setValue(null);
		txtProductCode.setValue("");
		txtProductUnit.setValue("");
//		afProcessLoss.setValue("");
		afQty.setValue("");
		afDia.setValue("");
		//		txtPerBatchCtn.setValue("");
		//		txtPerCtnQty.setValue("");
		//		cmbQtyUnit.setValue(null);
		//		afQty.setValue("");
		//		cmbWtUnit.setValue(null);
		//		dateField.setValue(new java.util.Date());

		for (int i = 0; i < unitNameRaw.size(); i++)
		{
			cmbItemRaw.get(i).removeAllItems();
			reqQtyRaw.get(i).setValue("");
			unitNameRaw.get(i).setValue("");
			groupIdRaw.get(i).setValue("");
			subGroupIdRaw.get(i).setValue("");
			categoryTypeRaw.get(i).setValue("");
			processLossRaw.get(i).setValue("");
		}

		for (int i = 0; i < unitNameInk.size(); i++)
		{
			cmbItemInk.get(i).removeAllItems();
			reqQtyInk.get(i).setValue("");
			unitNameInk.get(i).setValue("");
			groupIdInk.get(i).setValue("");
			subgroupIdInk.get(i).setValue("");
			categoryTypeInk.get(i).setValue("");
			processLossInk.get(i).setValue("");
		}

		for (int i = 0; i < unitNamePacking.size(); i++)
		{
			cmbItemPacking.get(i).removeAllItems();
			reqQtyPacking.get(i).setValue("");
			unitNamePacking.get(i).setValue("");
			groupIdPacking.get(i).setValue("");
			subGroupIdPacking.get(i).setValue("");
			categoryTypePacking.get(i).setValue("");
			processLossPacking.get(i).setValue("");
		}

	}

	public void tableInitialiseRaw() 
	{
		for (int i = 0; i<10; i++) 
		{
			tableRowAddRaw(i);
		}
	}

	public void tableInitialiseCream() 
	{
		for (int i = 0; i<10; i++) 
		{
			tableRowAddCream(i);
		}
	}

	public void tableRowAddRaw(final int ar)
	{
		final Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();

		try
		{				
			txtSlRaw.add(ar,new Label());
			txtSlRaw.get(ar).setWidth("100%");
			txtSlRaw.get(ar).setValue(ar+1);

			cmbItemRaw.add(ar, new ComboBox());
			cmbItemRaw.get(ar).setWidth("100%");
			cmbItemRaw.get(ar).setImmediate(true);
			cmbItemRaw.get(ar).setNullSelectionAllowed(true);

			cmbItemRaw.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					//					tblClear();
					if(cmbItemRaw.get(ar).getValue()!=null)
					{
						boolean fla = (doubleEntryCheckRaw(cmbItemRaw.get(ar).getItemCaption(cmbItemRaw.get(ar).getValue()), ar));
						if(!cmbItemRaw.get(ar).getValue().toString().replaceAll("#", "").equalsIgnoreCase("x0") && fla)
						{
							unitNameRaw.get(ar).setValue("");
							groupIdRaw.get(ar).setValue("");
							subGroupIdRaw.get(ar).setValue("");

							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							Transaction tx = session.beginTransaction();
							try
							{
								String sql = " select vGroupId, vSubGroupId, vUnitName, vCategoryType from tbRawItemInfo   where vRawItemCode = '"+cmbItemRaw.get(ar).getValue()+"' ";
								List list = session.createSQLQuery(sql).list();
								int cmbflag=0;
								System.out.print(sql);

								if(list.iterator().hasNext())
								{
									Object[] element = (Object[]) list.iterator().next();
									cmbflag=1;
									unitNameRaw.get(ar).setValue(element[2].toString());
									groupIdRaw.get(ar).setValue(element[0].toString());
									subGroupIdRaw.get(ar).setValue(element[1].toString());
									categoryTypeRaw.get(ar).setValue(element[3].toString());
								}

							}
							catch(Exception ex)
							{
								System.out.println(ex);
							}

							if(cmbItemRaw.size()-1==ar)
							{	
								tableRowAddRaw(ar+1);
								cmbItemRawAdd(ar+1);
								cmbItemRaw.get(ar+1).focus();
							}
						}
						else 
						{
							//Object checkNull = (Object) cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue());

							if (cmbItemRaw.get(ar).getValue()!=null) 
							{
								cmbItemRaw.get(ar).setValue(null);
								getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							}
						}
					}
				}
			});

			reqQtyRaw.add(ar, new AmountCommaSeperator());
			reqQtyRaw.get(ar).setWidth("100%");
			reqQtyRaw.get(ar).setImmediate(true);

			unitNameRaw.add(ar, new TextRead(""));
			unitNameRaw.get(ar).setWidth("100%");
			unitNameRaw.get(ar).setImmediate(true);

			groupIdRaw.add(ar, new TextRead(""));
			groupIdRaw.get(ar).setWidth("100%");
			groupIdRaw.get(ar).setImmediate(true);

			subGroupIdRaw.add(ar, new TextRead(""));
			subGroupIdRaw.get(ar).setWidth("100%");
			subGroupIdRaw.get(ar).setImmediate(true);

			categoryTypeRaw.add(ar, new TextRead(""));
			categoryTypeRaw.get(ar).setWidth("100%");
			categoryTypeRaw.get(ar).setImmediate(true);
			
			processLossRaw.add(ar, new AmountCommaSeperator(""));
			processLossRaw.get(ar).setWidth("100%");
			processLossRaw.get(ar).setImmediate(true);

			tableRaw.addItem(new Object[]{txtSlRaw.get(ar), cmbItemRaw.get(ar), reqQtyRaw.get(ar), unitNameRaw.get(ar), groupIdRaw.get(ar), subGroupIdRaw.get(ar), categoryTypeRaw.get(ar), processLossRaw.get(ar)},ar);

		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	public void tableRowAddCream(final int ar)
	{
		final Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();

		try
		{				
			txtSlInk.add(ar,new Label());
			txtSlInk.get(ar).setWidth("100%");
			txtSlInk.get(ar).setValue(ar+1);

			cmbItemInk.add(ar, new ComboBox());
			cmbItemInk.get(ar).setWidth("100%");
			cmbItemInk.get(ar).setImmediate(true);
			cmbItemInk.get(ar).setNullSelectionAllowed(true);

			cmbItemInk.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					//					tblClear();
					if(cmbItemInk.get(ar).getValue()!=null)
					{
						boolean fla = (doubleEntryCheckCream(cmbItemInk.get(ar).getItemCaption(cmbItemInk.get(ar).getValue()), ar));
						if(!cmbItemInk.get(ar).getValue().toString().replaceAll("#", "").equalsIgnoreCase("x0") && fla)
						{
							unitNameInk.get(ar).setValue("");
							groupIdInk.get(ar).setValue("");
							subgroupIdInk.get(ar).setValue("");

							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							Transaction tx = session.beginTransaction();
							try
							{
								String sql = " select vGroupId, vSubGroupId, vUnitName, vCategoryType from tbRawItemInfo   where vRawItemCode = '"+cmbItemInk.get(ar).getValue()+"' ";
								List list = session.createSQLQuery(sql).list();
								int cmbflag=0;
								System.out.print(sql);

								if(list.iterator().hasNext())
								{
									Object[] element = (Object[]) list.iterator().next();
									cmbflag=1;
									unitNameInk.get(ar).setValue(element[2].toString());
									groupIdInk.get(ar).setValue(element[0].toString());
									subgroupIdInk.get(ar).setValue(element[1].toString());
									categoryTypeInk.get(ar).setValue(element[3].toString());
								}

							}
							catch(Exception ex)
							{
								System.out.println(ex);
							}

							if(cmbItemInk.size()-1==ar)
							{	
								tableRowAddCream(ar+1);
								cmbItemInkAdd(ar+1);
								cmbItemInk.get(ar+1).focus();
							}
						}
						else 
						{
							//Object checkNull = (Object) cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue());

							if (cmbItemInk.get(ar).getValue()!=null) 
							{
								cmbItemInk.get(ar).setValue(null);
								getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							}
						}
					}
				}
			});

			reqQtyInk.add(ar, new AmountCommaSeperator());
			reqQtyInk.get(ar).setWidth("100%");
			reqQtyInk.get(ar).setImmediate(true);

			unitNameInk.add(ar, new TextRead(""));
			unitNameInk.get(ar).setWidth("100%");
			unitNameInk.get(ar).setImmediate(true);

			groupIdInk.add(ar, new TextRead(""));
			groupIdInk.get(ar).setWidth("100%");
			groupIdInk.get(ar).setImmediate(true);

			subgroupIdInk.add(ar, new TextRead(""));
			subgroupIdInk.get(ar).setWidth("100%");
			subgroupIdInk.get(ar).setImmediate(true);
			
			categoryTypeInk.add(ar, new TextRead(""));
			categoryTypeInk.get(ar).setWidth("100%");
			categoryTypeInk.get(ar).setImmediate(true);
			
			processLossInk.add(ar, new AmountCommaSeperator());
			processLossInk.get(ar).setWidth("100%");
			processLossInk.get(ar).setImmediate(true);

			tableInk.addItem(new Object[]{txtSlInk.get(ar), cmbItemInk.get(ar), reqQtyInk.get(ar), unitNameInk.get(ar), groupIdInk.get(ar), subgroupIdInk.get(ar), categoryTypeInk.get(ar), processLossInk.get(ar)},ar);

		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	public void tableRowAddPacking(final int ar)
	{
		final Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();

		try
		{				
			txtSlPacking.add(ar,new Label());
			txtSlPacking.get(ar).setWidth("100%");
			txtSlPacking.get(ar).setValue(ar+1);

			cmbItemPacking.add(ar, new ComboBox());
			cmbItemPacking.get(ar).setWidth("100%");
			cmbItemPacking.get(ar).setImmediate(true);
			cmbItemPacking.get(ar).setNullSelectionAllowed(true);

			cmbItemPacking.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					//					tblClear();
					if(cmbItemPacking.get(ar).getValue()!=null)
					{
						boolean fla = (doubleEntryCheckPacking(cmbItemPacking.get(ar).getItemCaption(cmbItemPacking.get(ar).getValue()), ar));
						if(!cmbItemPacking.get(ar).getValue().toString().replaceAll("#", "").equalsIgnoreCase("x0") && fla)
						{
							unitNamePacking.get(ar).setValue("");
							groupIdPacking.get(ar).setValue("");
							subGroupIdPacking.get(ar).setValue("");

							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							Transaction tx = session.beginTransaction();
							try
							{
								String sql = " select vGroupId, vSubGroupId, vUnitName, vCategoryType from tbRawItemInfo   where vRawItemCode = '"+cmbItemPacking.get(ar).getValue()+"' ";
								List list = session.createSQLQuery(sql).list();
								int cmbflag=0;
								System.out.print(sql);

								if(list.iterator().hasNext())
								{
									Object[] element = (Object[]) list.iterator().next();
									cmbflag=1;
									unitNamePacking.get(ar).setValue(element[2].toString());
									groupIdPacking.get(ar).setValue(element[0].toString());
									subGroupIdPacking.get(ar).setValue(element[1].toString());
									categoryTypePacking.get(ar).setValue(element[3].toString());
								}

							}
							catch(Exception ex)
							{
								System.out.println(ex);
							}

							if(cmbItemPacking.size()-1==ar)
							{	
								tableRowAddPacking(ar+1);
								cmbItemPackingAdd(ar+1);
								cmbItemPacking.get(ar+1).focus();
							}
						}
						else 
						{
							//Object checkNull = (Object) cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue());

							if (cmbItemPacking.get(ar).getValue()!=null) 
							{
								cmbItemPacking.get(ar).setValue(null);
								getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							}
						}
					}
				}
			});

			reqQtyPacking.add(ar, new AmountCommaSeperator());
			reqQtyPacking.get(ar).setWidth("100%");
			reqQtyPacking.get(ar).setImmediate(true);

			unitNamePacking.add(ar, new TextRead(""));
			unitNamePacking.get(ar).setWidth("100%");
			unitNamePacking.get(ar).setImmediate(true);

			groupIdPacking.add(ar, new TextRead(""));
			groupIdPacking.get(ar).setWidth("100%");
			groupIdPacking.get(ar).setImmediate(true);

			subGroupIdPacking.add(ar, new TextRead(""));
			subGroupIdPacking.get(ar).setWidth("100%");
			subGroupIdPacking.get(ar).setImmediate(true);
			
			categoryTypePacking.add(ar, new TextRead(""));
			categoryTypePacking.get(ar).setWidth("100%");
			categoryTypePacking.get(ar).setImmediate(true);
			
			processLossPacking.add(ar, new AmountCommaSeperator());
			processLossPacking.get(ar).setWidth("100%");
			processLossPacking.get(ar).setImmediate(true);

			tablePacking.addItem(new Object[]{txtSlPacking.get(ar), cmbItemPacking.get(ar), reqQtyPacking.get(ar), unitNamePacking.get(ar), groupIdPacking.get(ar), subGroupIdPacking.get(ar), categoryTypePacking.get(ar), processLossPacking.get(ar)},ar);

		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private boolean doubleEntryCheckRaw(String caption,int row)
	{
		for(int i=0;i<cmbItemRaw.size();i++)
		{
			if(i!=row && caption.equals(cmbItemRaw.get(i).getItemCaption(cmbItemRaw.get(i).getValue())))
			{
				return false;
			}

		}
		return true;
	}

	private boolean doubleEntryCheckCream(String caption,int row)
	{
		for(int i=0;i<cmbItemInk.size();i++)
		{
			if(i!=row && caption.equals(cmbItemInk.get(i).getItemCaption(cmbItemInk.get(i).getValue())))
			{
				return false;
			}

		}
		return true;
	}

	public void tableInitialisePacking() 
	{
		for (int i = 0; i<10; i++) 
		{
			tableRowAddPacking(i);
		}
	}

	

	private boolean doubleEntryCheckPacking(String caption,int row)
	{
		for(int i=0;i<cmbItemPacking.size();i++)
		{
			if(i!=row && caption.equals(cmbItemPacking.get(i).getItemCaption(cmbItemPacking.get(i).getValue())))
			{
				return false;
			}

		}
		return true;
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("1280px");
		setHeight("710px");

		// lblProductName
		lblProductName = new Label();
		lblProductName.setImmediate(false);
		lblProductName.setWidth("-1px");
		lblProductName.setHeight("-1px");
		lblProductName.setValue("Product Name :");
		

		// cmbProductName
		cmbProductName = new ComboBox();
		cmbProductName.setImmediate(true);
		cmbProductName.setWidth("260px");
		cmbProductName.setHeight("-1px");
		cmbProductName.setNullSelectionAllowed(true);
		cmbProductName.setNewItemsAllowed(false);
		

		// lblProductCode
		lblProductCode = new Label();
		lblProductCode.setImmediate(false);
		lblProductCode.setWidth("-1px");
		lblProductCode.setHeight("-1px");
		lblProductCode.setValue("Product Code :");
		

		// txtProductCode
		txtProductCode = new TextRead();
		txtProductCode.setImmediate(true);
		txtProductCode.setWidth("80px");
		txtProductCode.setHeight("22px");
		
		// lblDia
		lblDia = new Label();
		lblDia.setImmediate(false);
		lblDia.setWidth("-1px");
		lblDia.setHeight("-1px");
		lblDia.setValue("Tube Dia (mm) :");

		// lblQty
		lblQty = new Label();
		lblQty.setImmediate(false);
		lblQty.setWidth("-1px");
		lblQty.setHeight("-1px");
		lblQty.setValue("Tube Per Sqm :");
		
		// afDia
		afDia = new AmountField();
		afDia.setImmediate(false);
		afDia.setWidth("80px");
		afDia.setHeight("-1px");

		// txtPerCtnQty
		afQty =  new AmountField();
		afQty.setImmediate(false);
		afQty.setWidth("80px");
		afQty.setHeight("-1px");
		

		// txtProductCode
		txtProductUnit = new TextRead();
		txtProductUnit.setImmediate(true);
		txtProductUnit.setWidth("80px");
		txtProductUnit.setHeight("22px");
		
		// lblProductName
		lblProcessLoss = new Label();
		lblProcessLoss.setImmediate(false);
		lblProcessLoss.setWidth("-1px");
		lblProcessLoss.setHeight("-1px");
		lblProcessLoss.setValue("Process Loss (%) :");

		// lblDate
		lblDate =  new Label();
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Declaration Date :");
		

		// dateField
		dateField = new PopupDateField();
		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		dateField.setWidth("120px");
		dateField.setHeight("-1px");
		

		// lblRawMaterials
		lblRawMaterials = new Label();
		lblRawMaterials.setImmediate(false);
		lblRawMaterials.setWidth("-1px");
		lblRawMaterials.setHeight("-1px");
		lblRawMaterials.setContentMode(Label.CONTENT_XHTML);
		lblRawMaterials.setValue("<b><font size='3px' color='black'>Raw Materials</font></b>");
		

		// table
		tableRaw.setWidth("410px");
		tableRaw.setHeight("330px");
		tableRaw.setColumnCollapsingAllowed(true);

		tableRaw.addContainerProperty("SL", Label.class, new Label());
		tableRaw.setColumnWidth("SL", 10);
		tableRaw.setColumnAlignment("SL", tableRaw.ALIGN_CENTER);

		tableRaw.addContainerProperty("Item Name", ComboBox.class,new ComboBox());
		tableRaw.setColumnWidth("Item Name", 180);

		tableRaw.addContainerProperty("Req. Qty", AmountCommaSeperator.class, new AmountCommaSeperator());
		tableRaw.setColumnWidth("Req. Qty", 50);

		tableRaw.addContainerProperty("Unit", TextRead.class, new TextRead());
		tableRaw.setColumnWidth("Unit", 40);
		tableRaw.setColumnAlignment("Unit", tableRaw.ALIGN_CENTER);

		tableRaw.addContainerProperty("Group", TextRead.class, new TextRead());
		tableRaw.setColumnWidth("Group", 60);
		tableRaw.setColumnAlignment("Group", tableRaw.ALIGN_CENTER);
		tableRaw.setColumnCollapsed("Group", true);

		tableRaw.addContainerProperty("Sub-Group", TextRead.class, new TextRead());
		tableRaw.setColumnWidth("Sub-Group", 60);
		tableRaw.setColumnAlignment("Sub-Group", tableRaw.ALIGN_CENTER);
		tableRaw.setColumnCollapsed("Sub-Group", true);

		tableRaw.addContainerProperty("CategoryType", TextRead.class, new TextRead());
		tableRaw.setColumnWidth("CategoryType", 60);
		tableRaw.setColumnAlignment("CategoryType", tableRaw.ALIGN_CENTER);
		tableRaw.setColumnCollapsed("CategoryType", true);
		
		tableRaw.addContainerProperty("Loss(%)", AmountCommaSeperator.class, new AmountCommaSeperator());
		tableRaw.setColumnWidth("Loss(%)", 40);
		tableRaw.setColumnAlignment("Loss(%)", tableRaw.ALIGN_CENTER);

		

		// lblRawMaterials
		lblInk = new Label();
		lblInk.setImmediate(false);
		lblInk.setWidth("-1px");
		lblInk.setHeight("-1px");
		lblInk.setContentMode(Label.CONTENT_XHTML);
		lblInk.setValue("<b><font size='3px' color='black'>Ink</font></b>");
		

		// table
		tableInk.setWidth("410px");
		tableInk.setHeight("330px");
		tableInk.setColumnCollapsingAllowed(true);

		tableInk.addContainerProperty("SL", Label.class, new Label());
		tableInk.setColumnWidth("SL", 10);
		tableInk.setColumnAlignment("SL", tableInk.ALIGN_CENTER);

		tableInk.addContainerProperty("Item Name", ComboBox.class,new ComboBox());
		tableInk.setColumnWidth("Item Name", 180);

		tableInk.addContainerProperty("Req. Qty", AmountCommaSeperator.class, new AmountCommaSeperator());
		tableInk.setColumnWidth("Req. Qty", 50);

		tableInk.addContainerProperty("Unit", TextRead.class, new TextRead());
		tableInk.setColumnWidth("Unit", 40);
		tableInk.setColumnAlignment("Unit", tableInk.ALIGN_CENTER);

		tableInk.addContainerProperty("Group", TextRead.class, new TextRead());
		tableInk.setColumnWidth("Group", 60);
		tableInk.setColumnAlignment("Group", tableInk.ALIGN_CENTER);
		tableInk.setColumnCollapsed("Group", true);

		tableInk.addContainerProperty("Sub-Group", TextRead.class, new TextRead());
		tableInk.setColumnWidth("Sub-Group", 60);
		tableInk.setColumnAlignment("Sub-Group", tableInk.ALIGN_CENTER);
		tableInk.setColumnCollapsed("Sub-Group", true);

		tableInk.addContainerProperty("CategoryType", TextRead.class, new TextRead());
		tableInk.setColumnWidth("CategoryType", 60);
		tableInk.setColumnAlignment("CategoryType", tableRaw.ALIGN_CENTER);
		tableInk.setColumnCollapsed("CategoryType", true);
		
		tableInk.addContainerProperty("Loss(%)", AmountCommaSeperator.class, new AmountCommaSeperator());
		tableInk.setColumnWidth("Loss(%)", 40);
		tableInk.setColumnAlignment("Loss(%)", tableRaw.ALIGN_CENTER);
		

		// lblRawMaterials
		lblPackingMaterials = new Label();
		lblPackingMaterials.setImmediate(false);
		lblPackingMaterials.setWidth("-1px");
		lblPackingMaterials.setHeight("-1px");
		lblPackingMaterials.setContentMode(Label.CONTENT_XHTML);
		lblPackingMaterials.setValue("<b><font size='3px' color='black'>Packing Materials</font></b>");
		

		// table
		tablePacking.setWidth("410px");
		tablePacking.setHeight("330px");
		tablePacking.setColumnCollapsingAllowed(true);

		tablePacking.addContainerProperty("SL", Label.class, new Label());
		tablePacking.setColumnWidth("SL", 10);
		tablePacking.setColumnAlignment("SL", tablePacking.ALIGN_CENTER);

		tablePacking.addContainerProperty("Item Name", ComboBox.class,new ComboBox());
		tablePacking.setColumnWidth("Item Name", 180);

		tablePacking.addContainerProperty("Req. Qty", AmountCommaSeperator.class, new AmountCommaSeperator());
		tablePacking.setColumnWidth("Req. Qty", 50);

		tablePacking.addContainerProperty("Unit", TextRead.class, new TextRead());
		tablePacking.setColumnWidth("Unit", 40);
		tablePacking.setColumnAlignment("Unit", tablePacking.ALIGN_CENTER);

		tablePacking.addContainerProperty("Group", TextRead.class, new TextRead());
		tablePacking.setColumnWidth("Group", 60);
		tablePacking.setColumnAlignment("Group", tablePacking.ALIGN_CENTER);
		tablePacking.setColumnCollapsed("Group", true);

		tablePacking.addContainerProperty("Sub-Group", TextRead.class, new TextRead());
		tablePacking.setColumnWidth("Sub-Group", 60);
		tablePacking.setColumnAlignment("Sub-Group", tablePacking.ALIGN_CENTER);
		tablePacking.setColumnCollapsed("Sub-Group", true);
		
		tablePacking.addContainerProperty("CategoryType", TextRead.class, new TextRead());
		tablePacking.setColumnWidth("CategoryType", 60);
		tablePacking.setColumnAlignment("CategoryType", tableRaw.ALIGN_CENTER);
		tablePacking.setColumnCollapsed("CategoryType", true);
		
		tablePacking.addContainerProperty("Loss(%)", AmountCommaSeperator.class, new AmountCommaSeperator());
		tablePacking.setColumnWidth("Loss(%)", 40);
		tablePacking.setColumnAlignment("Loss(%)", tableRaw.ALIGN_CENTER);

		

		lblLine = new Label("<b><font color='#e65100'>============================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		
		// adding components to the mainLayout (component distance: 30px)
		mainLayout.addComponent(lblProductName, "top:20.0px; left:10.0px;");
		mainLayout.addComponent(cmbProductName, "top:17.0px;left:128.0px;");
		
		mainLayout.addComponent(lblProductCode, "top:50.0px; left:10.0px;");
		mainLayout.addComponent(txtProductCode, "top:47.0px;left:129.0px;");
		
		mainLayout.addComponent(lblDia, "top:80.0px; left:10.0px;");
		mainLayout.addComponent(afDia, "top:77.0px;left:129.0px;");
		
		mainLayout.addComponent(lblQty, "top:110.0px; left:10.0px;");
		mainLayout.addComponent(afQty, "top:107.0px;left:128.0px;");
		mainLayout.addComponent(txtProductUnit, "top: 110.0px; left: 225.0px;");
		
//		mainLayout.addComponent(lblProcessLoss, "top:110.0px; left:10.0px;");
//		mainLayout.addComponent(afProcessLoss, "top:107.0px;left:128.0px;");
		
		mainLayout.addComponent(lblDate, "top:140.0px; left:10.0px;");
		mainLayout.addComponent(dateField, "top:137.0px;left:128.0px;");
		
		mainLayout.addComponent(lblRawMaterials, "top:205.0px; left:130.0px;");
		mainLayout.addComponent(tableRaw, "top:230.0px; left:10.0px;");
		
		mainLayout.addComponent(lblInk, "top:205.0px; left:630.0px;");
		mainLayout.addComponent(tableInk, "top:230.0px; left:430.0px;");
//		
		mainLayout.addComponent(lblPackingMaterials, "top:205.0px; left:990.0px;");
		mainLayout.addComponent(tablePacking, "top:230.0px; left:850.0px;");
		
		mainLayout.addComponent(lblLine, "top:580.0px;left:0.0px;");
		mainLayout.addComponent(cButton, "top:610.0px; left:200.0px;");

		return mainLayout;
	}
}
