package acc.appform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class PurchaseOrderActiveInactive extends Window
{
	private SessionBean sessionBean;

	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout hLayout = new HorizontalLayout();
	private boolean tick = false,isFind=false;
	//private Label lblType = new Label("Type:");
	//private static final String[] stType = new String[] {"PO","JO"};
	private ComboBox cmbType = new ComboBox();
	private PopupDateField dFromDate = new PopupDateField();
	private PopupDateField dToDate = new PopupDateField();
	private Label lblFrom = new Label("Form:");
	private Label lblTo = new Label("To:");
	private SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
	private CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");
	private CommonButton button = new CommonButton("", "Save", "", "", "Refresh", "Find", "", "", "", "Exit");
	private Table table = new Table();
	private Label lblParty = new Label("Party Name: ");
	private ComboBox cmbPartyName;

	private ArrayList<Label> tbsl = new ArrayList<Label>();
	private ArrayList<CheckBox> tbselect = new ArrayList<CheckBox>();
	private ArrayList<Label> tbpoNo = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbPoDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> tbTotalPoQty = new ArrayList<Label>();
	private ArrayList<Label> tbTotalDelQty = new ArrayList<Label>();
	private ArrayList<Label> tbTotalBalance = new ArrayList<Label>();
	private ArrayList<TextField> tbReason = new ArrayList<TextField>();
	private ArrayList<NativeButton> tbBtnDetails = new ArrayList<NativeButton>();
	private ArrayList<Component> allComp = new ArrayList<Component>();
	private ReportDate reportTime = new ReportDate();
	private boolean isUpdate = false;
	private int reasonIndex = 0;

	public PurchaseOrderActiveInactive(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("PURCHASE ORDER ACTIVE/INACTIVE :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("1050px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setResizable(false);
		this.setStyleName("cwindow");
		btnIni(true);
		compInit();
		compAdd();
		tableInitialise();
		partyNameData();
		setEventAction();
		authenticationCheck();
		focusEnter();
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

	public void setEventAction()
	{
		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPartyName.getValue()!=null)
				{
					tableClear();
				}
				if(isFind==true)
				{
					findCancelledData();			
				}
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(tableSelectCheck())
				{
					if(tableReasonCheck())
					{
						saveBtnAction();
					}
					else
					{
						showNotification("Warning!","Provide reason of cancellation.",Notification.TYPE_WARNING_MESSAGE);
						tbReason.get(reasonIndex).focus();
					}
				}
				else
				{
					showNotification("Warning!","No row selected.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbPartyName.getValue()!=null)
				{
					findButtonEvent();
				}
				else
				{
					showNotification("Warning!","Select party name.",Notification.TYPE_WARNING_MESSAGE);
					cmbPartyName.focus();
				}
			}				
		});

		table.addListener(new Table.HeaderClickListener() 
		{
			public void headerClick(HeaderClickEvent event) 
			{
				if(event.getPropertyId().toString().equalsIgnoreCase("SEL"))
				{
					if(tick)
					{tick = false;}
					else
					{tick = true;} 
					for(int i = 0; i < tbselect.size(); i++)
					{
						if(!tbpoNo.get(i).getValue().toString().isEmpty())
						{
							tbselect.get(i).setValue(tick);
						}
					}
				}
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				cButton.btnFind.setEnabled(false);
				button.btnRefresh.setEnabled(true);
				cmbPartyName.setValue(null);
				tableClear();
				partyNameData();
				table.setEnabled(true);
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=false;
				cButton.btnFind.setEnabled(true);
				btnIni(true);
				refreshClear();
				tableClear();
				partyNameData();
			}
		});

		cmbType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				if(cmbType.getValue()!=null)
				{
					changeTableHeader();
				}
			}
		});

		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(isFind==false)
					tableClear();
				else
					findCancelledData();
			}
		});

		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(isFind==false)
					tableClear();
				else
					findCancelledData();
			}
		});
	}

	public void changeTableHeader()
	{
		if(cmbType.getValue().toString().equals("PO"))
		{
			table.setColumnHeader("PO No", "PO No");
			table.setColumnHeader("PO Date", "PO Date");
			table.setColumnHeader("PO Qty", "PO Qty");
		}
		else
		{
			table.setColumnHeader("PO No", "JO No");
			table.setColumnHeader("PO Date", "JO Date");
			table.setColumnHeader("PO Qty", "JO Qty");
		}
	}

	public void partyNameData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		try
		{
			cmbPartyName.removeAllItems();
			cmbPartyName.addItem("%");
			cmbPartyName.setItemCaption("%","All");
			String sql="";
			if(isFind==false){
				sql ="select distinct partyId,partyname from tbDemandOrderInfo where vStatus like 'Active' order by partyname";}
			else{
				sql ="select distinct vPartyId,vPartyName from tbPurchaseOrderCancel order by vPartyName";}

			List<?> lst = session.createSQLQuery(sql).list();
			Iterator<?> iter = lst.iterator();
			if(queryValueCheck(sql)){
				while (iter.hasNext()) 
				{
					Object[] element = (Object[]) iter.next();
					System.out.println("cmbLoad");
					cmbPartyName.addItem(element[0].toString());
					cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
				}
			}

		}

		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
		finally{
			if(session!=null){
				session.close();
			}
				
			}
	}

	private boolean queryValueCheck(String sql)
	{
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	public boolean tableSelectCheck()
	{
		boolean ret = false;
		for(int i=0; i<tbpoNo.size(); i++)
		{
			if(!tbpoNo.get(i).getValue().toString().isEmpty())
			{
				if(tbselect.get(i).booleanValue())
				{
					ret = true;
				}
			}
		}
		return ret;
	}

	private boolean tableReasonCheck()
	{
		boolean ret = true;
		for(int i=0; i<tbpoNo.size(); i++)
		{
			if(!tbpoNo.get(i).getValue().toString().isEmpty())
			{
				if(tbselect.get(i).booleanValue())
				{
					if(tbReason.get(i).getValue().toString().isEmpty())
					{
						reasonIndex = i;
						ret = false;
						break;
					}
				}
			}
		}
		return ret;
	}

	private void findButtonEvent()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "";
			sql = " select doi.doNo,CONVERT(date,doi.doDate) doDate,SUM(dod.qty) doQty,ISNULL((select"+
					" SUM(dcd.mChallanQty) from tbDeliveryChallanDetails dcd where dcd.vDoNo=doi.doNo),0)"+
					" challanQty,(SUM(dod.qty)-ISNULL((select SUM(dcd.mChallanQty) from tbDeliveryChallanDetails"+
					" dcd where dcd.vDoNo=doi.doNo),0))balanceQty from tbDemandOrderInfo doi inner join"+
					" tbDemandOrderDetails dod on doi.doNo = dod.doNo and ISNULL(doi.vFlag,'') != 'Cancel' where"+
					" partyId like '"+cmbPartyName.getValue().toString()+"' and CONVERT"+
					" (date,doi.doDate) between '"+sessionBean.dfDb.format(dFromDate.getValue())+"' and"+
					" '"+sessionBean.dfDb.format(dToDate.getValue())+"' and doi.vStatus not like 'Inactive' group by doi.doNo,doi.doDate"+
					" having (SUM(dod.qty)-ISNULL((select SUM(dcd.mChallanQty) from tbDeliveryChallanDetails"+
					" dcd where dcd.vDoNo=doi.doNo),0))>0 order by CONVERT(date,doi.doDate)";

			System.out.println(sql);
			List<?> lst = session.createSQLQuery(sql).list();
			int i=0;
			if(!lst.isEmpty())
			{
				tableClear();
				btnIni(false);
				for(Iterator<?> iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					if(i == tbpoNo.size())
					{
						tableRowAdd(i);
					}
					tbpoNo.get(i).setValue(element[0].toString());
					tbPoDate.get(i).setReadOnly(false);
					tbPoDate.get(i).setValue(element[1]);
					tbPoDate.get(i).setReadOnly(true);
					tbTotalPoQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[2].toString())));
					tbTotalDelQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[3].toString())));
					tbTotalBalance.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[4].toString())));
					i++;
				}
			}
			else
			{
				showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);					
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		finally{session.close();}
	}
	private void findCancelledData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "";

			sql = "SElect vPoNo,dPoDate,mTotalQty,mDeliveredQty,mBalanceQty from tbPurchaseOrderCancel "
					+ "where vPartyId like  '"+cmbPartyName.getValue().toString()+"'  and"
					+ " dPoDate between '"+dateF.format(dFromDate.getValue())+"' and '"+dateF.format(dToDate.getValue())+"'";
			System.out.println(sql);
			List<?> lst = session.createSQLQuery(sql).list();
			int i=0;
			if(!lst.isEmpty())
			{
				tableClear();
				btnIni(false);
				for(Iterator<?> iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					if(i == tbpoNo.size())
					{
						tableRowAdd(i);
					}
					tbpoNo.get(i).setValue(element[0].toString());
					tbPoDate.get(i).setReadOnly(false);
					tbPoDate.get(i).setValue(element[1]);
					tbPoDate.get(i).setReadOnly(true);
					tbTotalPoQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[2].toString())));
					tbTotalDelQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[3].toString())));
					tbTotalBalance.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[4].toString())));
					i++;
				}
			}
			else
			{
				showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);					
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		finally{session.close();}
	}
	private void saveBtnAction()
	{
		if(isFind==true)
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
						formClear();
						btnIni(true);
						button.btnRefresh.setEnabled(false);
						showNotification("All information updated successfully.",Notification.TYPE_WARNING_MESSAGE);
						isFind = false;
						partyNameData();
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
						insertData();
						formClear();
						btnIni(true);
						isFind = false;

					}
				}
			});
		}
	}
	private void updateData()
	{
		String insertQuery = "";
		String updateQuery = "";
		String select = "";
		for(int i = 0; i<tbpoNo.size(); i++)
		{
			if(tbselect.get(i).booleanValue() && !tbpoNo.get(i).getValue().toString().isEmpty())
			{
				Session session = SessionFactoryUtil.getInstance().openSession();
				Transaction tx = session.beginTransaction();
				try
				{
					insertQuery = "Insert into tbUdPurchaseOrderCancel(vCancelId,vPartyId,vPartyName,"+
							" vPoNo,dPoDate,mTotalQty,mDeliveredQty,mBalanceQty,vReason,vProductId,vProductName,"+
							" vProductUnit,vUserIp,vUserName,dEntryTime) ";

					select  = "select  '"+referenceNo()+"',"+
							" (select partyId from tbDemandOrderInfo di where di.doNo='"+tbpoNo.get(i).getValue().toString()+"'),"+
							" (select partyname from tbDemandOrderInfo di where di.doNo='"+tbpoNo.get(i).getValue().toString()+"'),"+
							" '"+tbpoNo.get(i).getValue().toString()+"', '"+sessionBean.dfDb.format(tbPoDate.get(i).getValue())+"',"+
							" qty,ISNULL((select SUM(mChallanQty) from tbDeliveryChallanDetails dc where dc.vDoNo=do.doNo and"+
							" dc.vProductId = do.productId),0),(qty-ISNULL((select SUM(mChallanQty) from tbDeliveryChallanDetails"+
							" dc where dc.vDoNo=do.doNo and dc.vProductId = do.productId),0)), '"+tbReason.get(i).getValue().toString().trim()+"',"+
							" productId, (select vProductName from tbFinishedProductInfo fi where fi.vProductId = do.productId), unit,"+
							" '"+sessionBean.getUserIp()+"', '"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP from tbDemandOrderDetails do where"+
							" doNo = '"+tbpoNo.get(i).getValue().toString()+"'";

					System.out.println(insertQuery+select);
					session.createSQLQuery(insertQuery+select).executeUpdate();

					String 	deleteQuery = "delete from  tbPurchaseOrderCancel where  vPoNo='"+tbpoNo.get(i).getValue().toString()+"'";
					System.out.println(deleteQuery);
					session.createSQLQuery(deleteQuery).executeUpdate();
					/*String updateFindQuery = "update tbDemandOrderInfo set " +
								" vFlag = ' '  where" +
								" doNo = '"+tbpoNo.get(i).getValue().toString()+"'  and vFlag = 'Cancel' ";*/
					String updateFindQuery="update tbDemandOrderInfo set vFlag = ' ',vStatus = 'Active',dInactivedate='1900-01-01',vRemark='"+tbReason.get(i).getValue().toString()+"'  where doNo = '"+tbpoNo.get(i).getValue().toString()+"'  and vStatus = 'Inactive' ";
					System.out.println("updateFindQuery");
					session.createSQLQuery(updateFindQuery).executeUpdate();

					updateFindQuery = "update tbDemandOrderDetails set " +
							" vCancel = ' ' where" +
							" doNo = '"+tbpoNo.get(i).getValue().toString()+"' and  vCancel = 'Cancel'";
					session.createSQLQuery(updateFindQuery).executeUpdate();
					//	}
					tx.commit();
				}
				catch(Exception ex)
				{
					if(tx!=null){
						tx.rollback();
					}
					showNotification("Can't Save", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
				}
				finally{
					if(session!=null){
						session.close();
					}
					
				}
			}
		}
	}
	private void insertData()
	{
		String insertQuery = "";
		String updateQuery = "";
		String select = "";
		for(int i = 0; i<tbpoNo.size(); i++)
		{
			if(tbselect.get(i).booleanValue() && !tbpoNo.get(i).getValue().toString().isEmpty())
			{
				Session session = SessionFactoryUtil.getInstance().openSession();
				Transaction tx = session.beginTransaction();
				try
				{
					//	if(isFind==false){
					insertQuery = "Insert into tbPurchaseOrderCancel(vCancelId,vPartyId,vPartyName,"+
							" vPoNo,dPoDate,mTotalQty,mDeliveredQty,mBalanceQty,vReason,vProductId,vProductName,"+
							" vProductUnit,vUserIp,vUserName,dEntryTime) ";

					select  = "select  '"+referenceNo()+"',"+
							" (select partyId from tbDemandOrderInfo di where di.doNo='"+tbpoNo.get(i).getValue().toString()+"'),"+
							" (select partyname from tbDemandOrderInfo di where di.doNo='"+tbpoNo.get(i).getValue().toString()+"'),"+
							" '"+tbpoNo.get(i).getValue().toString()+"', '"+sessionBean.dfDb.format(tbPoDate.get(i).getValue())+"',"+
							" qty,ISNULL((select SUM(mChallanQty) from tbDeliveryChallanDetails dc where dc.vDoNo=do.doNo and"+
							" dc.vProductId = do.productId),0),(qty-ISNULL((select SUM(mChallanQty) from tbDeliveryChallanDetails"+
							" dc where dc.vDoNo=do.doNo and dc.vProductId = do.productId),0)), '"+tbReason.get(i).getValue().toString().trim()+"',"+
							" productId, (select vProductName from tbFinishedProductInfo fi where fi.vProductId = do.productId), unit,"+
							" '"+sessionBean.getUserIp()+"', '"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP from tbDemandOrderDetails do where"+
							" doNo = '"+tbpoNo.get(i).getValue().toString()+"'";

					System.out.println(insertQuery+select);
					session.createSQLQuery(insertQuery+select).executeUpdate();

					updateQuery = "update tbDemandOrderInfo set " +
							" vFlag = 'Cancel',vStatus = 'Inactive',dInactivedate=CURRENT_TIMESTAMP,vRemark='"+tbReason.get(i).getValue().toString()+"' where" +
							" doNo = '"+tbpoNo.get(i).getValue().toString()+"' ";
					session.createSQLQuery(updateQuery).executeUpdate();

					updateQuery = "update tbDemandOrderDetails set " +
							" vCancel = 'Cancel' where" +
							" doNo = '"+tbpoNo.get(i).getValue().toString()+"' ";
					session.createSQLQuery(updateQuery).executeUpdate();	

					tx.commit();
					showNotification("All information saved successfully.");
				}
				catch(Exception ex)
				{
					if(tx!=null){
						tx.rollback();
					}

					showNotification("Can't Save", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
				}
				finally{session.close();}
			}
		}
	}

	private String referenceNo()
	{
		String id = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String sql = "Select isnull(max(cast(SUBSTRING(vCancelId,2,50) as int)),0)+1 from tbPurchaseOrderCancel";
		Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext())
		{
			id = "C"+iter.next().toString();
		}

		return id;
	}

	public void refreshClear()
	{
		for(int i=0; i<tbpoNo.size(); i++)
		{
			tbselect.get(i).setValue(false);
			tbReason.get(i).setValue("");
		}
	}

	public void tableInitialise()
	{
		for(int i=0; i<11; i++)
		{
			tableRowAdd(i);
		}
	}

	private void btnIni(boolean t)
	{
		button.btnSave.setEnabled(!t);
		button.btnEdit.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void focusEnter()
	{
		allComp.add(cmbPartyName);
		allComp.add(dFromDate);
		allComp.add(dToDate);
		allComp.add(cButton.btnFind);
		new FocusMoveByEnter(this,allComp);
	}

	public void tableRowAdd(final int ar)
	{
		tbsl.add(ar, new Label());
		tbsl.get(ar).setWidth("100%");
		tbsl.get(ar).setImmediate(true);
		tbsl.get(ar).setValue(ar+1);

		tbselect.add(ar,new CheckBox());
		tbselect.get(ar).setWidth("100%");
		tbselect.get(ar).setImmediate(true);
		tbselect.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbpoNo.get(ar).getValue().toString().isEmpty() && tbselect.get(ar).booleanValue())
				{
					tbReason.get(ar).setEnabled(true);
					tbReason.get(ar).focus();
					tbReason.get(ar).setValue("Done");
				}
				else
				{
					tbReason.get(ar).setEnabled(false);
					tbReason.get(ar).setValue("");
				}
			}
		});

		tbpoNo.add(ar, new Label());
		tbpoNo.get(ar).setWidth("100%");
		tbpoNo.get(ar).setImmediate(true);

		tbPoDate.add(ar, new PopupDateField());
		tbPoDate.get(ar).setWidth("100%");
		tbPoDate.get(ar).setImmediate(true);
		tbPoDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbPoDate.get(ar).setReadOnly(true);

		tbTotalPoQty.add(ar, new Label());
		tbTotalPoQty.get(ar).setWidth("100%");
		tbTotalPoQty.get(ar).setImmediate(true);

		tbTotalDelQty.add(ar, new Label());
		tbTotalDelQty.get(ar).setWidth("100%");
		tbTotalDelQty.get(ar).setImmediate(true);

		tbTotalBalance.add(ar, new Label());
		tbTotalBalance.get(ar).setWidth("100%");
		tbTotalBalance.get(ar).setImmediate(true);

		tbReason.add(ar, new TextField());
		tbReason.get(ar).setWidth("100%");
		tbReason.get(ar).setImmediate(true);
		tbReason.get(ar).setEnabled(false);

		tbBtnDetails.add(ar, new NativeButton());
		tbBtnDetails.get(ar).setWidth("100%");
		tbBtnDetails.get(ar).setImmediate(true);
		tbBtnDetails.get(ar).setIcon(new ThemeResource("../icons/preview.png"));
		tbBtnDetails.get(ar).setDescription("Click to view details");
		tbBtnDetails.get(ar).setStyleName(BaseTheme.BUTTON_LINK);
		tbBtnDetails.get(ar).addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!tbpoNo.get(ar).getValue().toString().equals(""))
				{
					detailsAction(ar);
				}
				else
				{
					showNotification("Warning!","No PO no found.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		table.addItem(new Object[]{tbsl.get(ar),tbselect.get(ar),tbpoNo.get(ar),tbPoDate.get(ar),tbTotalPoQty.get(ar),
				tbTotalDelQty.get(ar),tbTotalBalance.get(ar),tbReason.get(ar),tbBtnDetails.get(ar)},ar);
	}

	private void compInit()
	{
		cmbType.setImmediate(true);
		cmbType.setStyleName("horizontal");
		cmbType.setDescription("PO = Purchase Order, JO = Job Order");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("300px");
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);

		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setWidth("110px");
		dFromDate.setInvalidAllowed(false);
		dFromDate.setImmediate(true);

		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setInvalidAllowed(false);
		dToDate.setImmediate(true);

		cButton.btnFind.setHeight("26px");

		mainLayout.setSpacing(true);
		table.setWidth("100%");
		table.setHeight("352px");

		table.addContainerProperty("SL#", Label.class, null);		
		table.setColumnWidth("SL#", 18);

		table.addContainerProperty("Inactive", CheckBox.class, null);		
		table.setColumnWidth("Inactive", 50);

		table.addContainerProperty("PO No", Label.class, null);
		table.setColumnWidth("PO No", 150);

		table.addContainerProperty("PO Date", PopupDateField.class, null);
		table.setColumnWidth("PO Date", 80);

		table.addContainerProperty("PO Qty", Label.class, null);		
		table.setColumnWidth("PO Qty", 100);

		table.addContainerProperty("Delivered Qty", Label.class, null);
		table.setColumnWidth("Delivered Qty", 100);

		table.addContainerProperty("Balance Qty", Label.class, null);
		table.setColumnWidth("Balance Qty", 100);

		table.addContainerProperty("Reason of inactive", TextField.class, null);
		table.setColumnWidth("Reason of inactive", 245);

		table.addContainerProperty("Details", NativeButton.class, null);
		table.setColumnWidth("Details", 40);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_CENTER});

		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);
	}

	private void compAdd()
	{
		hLayout.setSpacing(true);
		//hLayout.addComponent(lblType);
		//hLayout.addComponent(cmbType);
		/*	for(int i = 0;i<stType.length;i++)
		{
			cmbType.addItem(stType[i]);
		}*/
		cmbType.setWidth("80px");
		hLayout.addComponent(lblParty);
		hLayout.addComponent(cmbPartyName);
		hLayout.addComponent(lblFrom);
		hLayout.addComponent(dFromDate);
		hLayout.addComponent(lblTo);
		hLayout.addComponent(dToDate);
		hLayout.addComponent(cButton.btnFind);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(table);
		mainLayout.addComponent(button);

		mainLayout.setComponentAlignment(hLayout, Alignment.TOP_CENTER);
		mainLayout.setComponentAlignment(button, Alignment.BOTTOM_CENTER);

		addComponent(mainLayout);
	}

	private void formClear()
	{
		//cmbType.setValue(null);
		cmbPartyName.setValue(null);
		cButton.btnFind.setEnabled(true);
		tableClear();
	}

	private void tableClear()
	{
		for(int i = 0; i<tbselect.size(); i++)
		{
			tbselect.get(i).setValue(false);
			tbpoNo.get(i).setValue("");
			tbPoDate.get(i).setReadOnly(false);
			tbPoDate.get(i).setValue(null);
			tbPoDate.get(i).setReadOnly(true);

			tbTotalPoQty.get(i).setValue("");
			tbTotalDelQty.get(i).setValue("");
			tbTotalBalance.get(i).setValue("");

			tbReason.get(i).setValue("");
		}
	}

	private void detailsAction(int i)
	{
		try
		{
			String PONo = tbpoNo.get(i).getValue().toString();
			String query = "";
			String jasper = "";
			HashMap<Object,Object> hm = new HashMap<Object,Object>();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("Date",reportTime.getTime);

			query = " Select DO.doNo,DO.doDate,DO.deiveryDate,DO.commission,DO.note,DOD.remarks,DO.partyName,"
					+ " DO.address,DO.mobile,DOD.productId,(select vProductName from tbFinishedProductInfo fi where"
					+ " fi.vProductId = DOD.productId)productName,DOD.unit,DOD.rate,DOD.qty,DOD.amount,PI.DivisionName,"
					+ "PI.AreaName,AI.vEmployeeName,REPLACE(BankId,'D:/Tomcat 7.0/webapps/', '') attachPO,DO.vApproveBy,DO.note2,DO.note3,DO.note4,DO.note5"
					+ " from tbDemandOrderInfo as DO left join tbDemandOrderDetails as DOD on Do.doNo = DOD.doNo left"
					+ " Join tbPartyInfo as PI on Pi.partyCode = DO.partyId left join tbAreaInfo as AI on Ai.vAreaId ="
					+ " PI.AreaId where DO.doNo like '"+PONo+"' and DO.partyId = '"+cmbPartyName.getValue()+"'"
					+ " order by DO.doDate,productName ";

			jasper = "report/account/DoSales/rptDemadOrderList.jasper";

			//System.out.println(query);
			hm.put("urlLink", this.getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", ""));
			hm.put("sql", query);
			Window win = new ReportViewer(hm,jasper,
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