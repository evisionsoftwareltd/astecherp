package acc.appform.DoSalesModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommaSeparator;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class AuthorityApprove extends Window
{
	private String title = "";
	private SessionBean sessionBean;	
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout bottomLayout = new HorizontalLayout();

	private Table table = new Table();
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<CheckBox> tbChkApprove = new ArrayList<CheckBox>();
	private ArrayList<Label> tbLblPoNo = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbdPoDate = new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> tbdDeliveryDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> tbLblTotalQty = new ArrayList<Label>();
	private ArrayList<Label> tbUserName = new ArrayList<Label>();
	private ArrayList<Label> tbUserIp = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbdEntryTime = new ArrayList<PopupDateField>();
	private ArrayList<NativeButton> tbBtnDetails = new ArrayList<NativeButton>();

	public AuthorityApprove(SessionBean sessionBean, String title)
	{
		this.title = title;
		this.sessionBean = sessionBean;		
		this.setWidth("1100px");
		this.setResizable(false);

		addAllComponent();
		if(this.title.equals("purchaseOrder"))
		{
			this.setCaption("PURCHASE ORDER APPROVE :: "+sessionBean.getCompany());
			tablePo();
		}

		this.addComponent(mainLayout);
		setDataToTable();
	}

	private void saveBtnAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", 
				MessageBox.Icon.QUESTION, "Do you want to approve information?",
				new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
				new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					approveData();
					doTableClear();
					setDataToTable();
					showNotification("Successful","Information approved successfully.",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
	}

	private void approveData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int i=0; i<tbLblPoNo.size(); i++)
			{
				if(tbChkApprove.get(i).booleanValue())
				{
					String queryInfo = " update tbDemandOrderInfo set "
							+ "iApproveFlag = 1, "
							+ "vApproveBy = '"+sessionBean.getUserName()+"', "
							+ "vApproveIp = '"+sessionBean.getUserIp()+"', "
							+ "dApproveTime = CURRENT_TIMESTAMP "
							+ "where doNo = '"+tbLblPoNo.get(i).getValue().toString()+"' ";
					session.createSQLQuery(queryInfo).executeUpdate();

					String queryDetails = " update tbDemandOrderDetails set "
							+ "iApproveFlag = 1, "
							+ "vApproveBy = '"+sessionBean.getUserName()+"', "
							+ "vApproveIp = '"+sessionBean.getUserIp()+"', "
							+ "dApproveTime = CURRENT_TIMESTAMP "
							+ "where doNo = '"+tbLblPoNo.get(i).getValue().toString()+"' ";
					session.createSQLQuery(queryDetails).executeUpdate();
				}
			}
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Warning!",""+ex,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void setDataToTable()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String query = "select doNo,doDate,deiveryDate,(select SUM(dd.qty) from tbDemandOrderDetails dd where dd.doNo = do.doNo)"
					+ " totalQty,userId,userIp,entryTime,vApproveBy from tbDemandOrderInfo do where iApproveFlag = 0 and vStatus not like 'Inactive' order by"
					+ " CONVERT(date,doDate)";
			List<?> list = session.createSQLQuery(query).list();
			int i = 0;
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				if(tbLblPoNo.size() <= i)
				{
					tableRowAddPo(i);
				}
				tbLblPoNo.get(i).setValue(element[0].toString());

				tbdPoDate.get(i).setReadOnly(false);
				tbdPoDate.get(i).setValue(element[1]);
				tbdPoDate.get(i).setReadOnly(true);

				tbdDeliveryDate.get(i).setReadOnly(false);
				tbdDeliveryDate.get(i).setValue(element[2]);
				tbdDeliveryDate.get(i).setReadOnly(true);

				tbLblTotalQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[3].toString())));
				tbUserName.get(i).setValue(element[4].toString());
				tbUserIp.get(i).setValue(element[5].toString());

				tbdEntryTime.get(i).setReadOnly(false);
				tbdEntryTime.get(i).setValue(element[6]);
				tbdEntryTime.get(i).setReadOnly(true);
				i++;
			}
			if(i == 0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			showNotification("Warning!",""+ex,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void addAllComponent()
	{
		mainLayout.addComponent(table);
		table.setWidth("100%");
		table.setHeight("500px");
		mainLayout.setSpacing(true);
	}

	private void tablePo()
	{
		table.addContainerProperty("SL#", Label.class, null);		
		table.setColumnWidth("SL#", 23);

		table.addContainerProperty("Sel", CheckBox.class, null);		
		table.setColumnWidth("Sel", 18);

		table.addContainerProperty("PO No", Label.class,null);
		table.setColumnWidth("PO No", 180);

		table.addContainerProperty("PO Date", PopupDateField.class, null);
		table.setColumnWidth("PO Date", 83);

		table.addContainerProperty("Delivery Date", PopupDateField.class,null);		
		table.setColumnWidth("Delivery Date", 83);

		table.addContainerProperty("Total PO Qty", Label.class, null);
		table.setColumnWidth("Total PO Qty", 85);

		table.addContainerProperty("Prepared By", Label.class, null);
		table.setColumnWidth("Prepared By", 70);

		table.addContainerProperty("User Ip", Label.class, null);
		table.setColumnWidth("User Ip", 90);

		table.addContainerProperty("Entry Time", PopupDateField.class, null);
		table.setColumnWidth("Entry Time", 170);

		table.addContainerProperty("Details", NativeButton.class, null);
		table.setColumnWidth("Details", 60);
		table.setColumnCollapsingAllowed(true);

		bottomLayout.setSpacing(true);

		table.setColumnAlignments(new String[] {Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT});

		table.setImmediate(true);
	}

	private void tableRowAddPo(final int ans)
	{
		tbLblSl.add(ans, new Label());
		tbLblSl.get(ans).setWidth("100%");
		tbLblSl.get(ans).setImmediate(true);
		tbLblSl.get(ans).setValue(ans+1);

		tbChkApprove.add(ans, new CheckBox());
		tbChkApprove.get(ans).setWidth("100%");
		tbChkApprove.get(ans).setImmediate(true);
		tbChkApprove.get(ans).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbChkApprove.get(ans).booleanValue() && !tbLblPoNo.get(ans).getValue().toString().isEmpty())
				{
					if(checkAuthentication())
					{
						saveBtnAction();
					}
					else
					{
						showNotification("Warning!","You aren't proper authenticate"
								+ " to do this.",Notification.TYPE_WARNING_MESSAGE);
						tbChkApprove.get(ans).setValue(false);
					}
				}
				else
				{
					tbChkApprove.get(ans).setValue(false);
				}
			}
		});

		tbLblPoNo.add(ans, new Label());
		tbLblPoNo.get(ans).setWidth("100%");
		tbLblPoNo.get(ans).setImmediate(true);

		tbdPoDate.add(ans, new PopupDateField());
		tbdPoDate.get(ans).setWidth("100%");
		tbdPoDate.get(ans).setReadOnly(true);
		tbdPoDate.get(ans).setDateFormat("dd-MM-yyyy");

		tbdDeliveryDate.add(ans, new PopupDateField());
		tbdDeliveryDate.get(ans).setWidth("100%");
		tbdDeliveryDate.get(ans).setReadOnly(true);
		tbdDeliveryDate.get(ans).setDateFormat("dd-MM-yyyy");

		tbLblTotalQty.add(ans, new Label());
		tbLblTotalQty.get(ans).setWidth("100%");

		tbUserName.add(ans, new Label());
		tbUserName.get(ans).setWidth("100%");

		tbUserIp.add(ans, new Label());
		tbUserIp.get(ans).setWidth("100%");

		tbdEntryTime.add(ans, new PopupDateField());
		tbdEntryTime.get(ans).setWidth("100%");
		tbdEntryTime.get(ans).setReadOnly(true);
		tbdEntryTime.get(ans).setDateFormat("dd-MM-yyyy hh:mm:ss aa");

		tbBtnDetails.add(ans, new NativeButton());
		tbBtnDetails.get(ans).setWidth("100%");
		tbBtnDetails.get(ans).setDescription("Click to view details");
		tbBtnDetails.get(ans).setIcon(new ThemeResource("../icons/preview.png"));
		tbBtnDetails.get(ans).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!tbLblPoNo.get(ans).getValue().toString().isEmpty())
				{
					reportView(ans);
				}
			}
		});

		table.addItem(new Object[]{tbLblSl.get(ans), tbChkApprove.get(ans), tbLblPoNo.get(ans), tbdPoDate.get(ans), tbdDeliveryDate.get(ans),
				tbLblTotalQty.get(ans), tbUserName.get(ans), tbUserIp.get(ans), tbdEntryTime.get(ans), tbBtnDetails.get(ans)},ans);
	}

	private boolean checkAuthentication()
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
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}

	private void doTableClear()
	{
		for(int i = 0; i<tbLblPoNo.size(); i++)
		{
			tbChkApprove.get(i).setValue(false);
			tbLblPoNo.get(i).setValue("");

			tbdPoDate.get(i).setReadOnly(false);
			tbdPoDate.get(i).setValue(null);
			tbdPoDate.get(i).setReadOnly(true);

			tbdDeliveryDate.get(i).setReadOnly(false);
			tbdDeliveryDate.get(i).setValue(null);
			tbdDeliveryDate.get(i).setReadOnly(true);

			tbLblTotalQty.get(i).setValue("");
			tbUserName.get(i).setValue("");
			tbUserIp.get(i).setValue("");

			tbdEntryTime.get(i).setReadOnly(false);
			tbdEntryTime.get(i).setValue(null);
			tbdEntryTime.get(i).setReadOnly(true);
		}
	}

	private void reportView(int ar)
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

			String query = " Select DO.doNo,DO.doDate,DO.deiveryDate,DO.commission,DO.note,DOD.remarks,DO.partyName,"
					+ " DO.address,DO.mobile,DOD.productId,(select vProductName from tbFinishedProductInfo fi where"
					+ " fi.vProductId = DOD.productId)productName,DOD.unit,DOD.rate,DOD.qty,DOD.amount,PI.DivisionName,PI.AreaName,"
					+ " AI.vEmployeeName,REPLACE(BankId,'D:/Tomcat 7.0/webapps/', '') attachPO,DO.vApproveBy,DO.note2,DO.note3,DO.note4,DO.note5 from tbDemandOrderInfo as"
					+ " DO left join tbDemandOrderDetails as DOD on Do.doNo = DOD.doNo left Join tbPartyInfo as PI on"
					+ " Pi.partyCode = DO.partyId left join tbAreaInfo as AI on Ai.vAreaId=PI.AreaId where "
					+ " DO.doNo = '"+tbLblPoNo.get(ar).getValue().toString()+"' order by productName ";
			String link = getApplication().getURL().toString();

			if(link.endsWith("astecherp/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("astecherp/", ""));
			}
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