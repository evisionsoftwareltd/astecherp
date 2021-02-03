package acc.appform.hrmModule;

	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.List;

	import org.hibernate.Session;

	import com.common.share.CommonButton;
	import com.common.share.FocusMoveByEnter;
	import com.common.share.SessionBean;
	import com.common.share.SessionFactoryUtil;
	import com.common.share.TextRead;
	import com.vaadin.data.Property.ValueChangeEvent;
	import com.vaadin.data.Property.ValueChangeListener;
	import com.vaadin.event.ItemClickEvent;
	import com.vaadin.event.ItemClickEvent.ItemClickListener;
	import com.vaadin.event.ShortcutAction.KeyCode;
	import com.vaadin.ui.AbsoluteLayout;
	import com.vaadin.ui.Button;
	import com.vaadin.ui.CheckBox;
	import com.vaadin.ui.ComboBox;
	import com.vaadin.ui.Component;
	import com.vaadin.ui.Label;
	import com.vaadin.ui.PopupDateField;
	import com.vaadin.ui.Table;
	import com.vaadin.ui.Window;
	import com.vaadin.ui.Button.ClickEvent;

public class MonthlyAdjustmentNDeductionFind_CHO extends Window {
	
		@SuppressWarnings("unused")
		private SessionBean sessionBean;	
		private AbsoluteLayout mainLayout;

		private Label lblSectionname;
		private Label lblWorkingMonth;
		private ComboBox cmbDepartmentName;
		private ComboBox cmbSectionName;
		private PopupDateField dWorkingMonth=new PopupDateField();

		private Table table = new Table();
		private ArrayList<Label> lbSL = new ArrayList<Label>();
		private ArrayList<PopupDateField> lblMonthName = new ArrayList<PopupDateField>();
		private ArrayList<Label> lblDepartmentID = new ArrayList<Label>();
		private ArrayList<Label> lblDepartmentName = new ArrayList<Label>();
		private ArrayList<Label> lblSectionID = new ArrayList<Label>();
		private ArrayList<Label> lblSectionName = new ArrayList<Label>();

		private CheckBox ChkSection;
		ArrayList<Component> allComp = new ArrayList<Component>();
		private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

		private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MM");
		private SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");

		TextRead strMonth=new TextRead("");
		TextRead strSectionID=new TextRead("");
		TextRead strDepartmentID=new TextRead("");

		public MonthlyAdjustmentNDeductionFind_CHO(SessionBean sessionBean, TextRead strMonth, TextRead strDepartmentID, TextRead strSectionID)
		{		
			this.strMonth = strMonth;
			this.strSectionID = strSectionID;
			this.strDepartmentID = strDepartmentID;
			this.sessionBean = sessionBean;
			this.setCaption("FIND MONTHLY ADJUSTMENT & DEDUCTION :: "+sessionBean.getCompany());
			this.center();
			this.setWidth("570px");
			this.setCloseShortcut(KeyCode.ESCAPE);
			this.setModal(true);
			this.setResizable(false);
			this.setStyleName("cwindow");

			buildMainLayout();
			setContent(mainLayout);
			tableinitialization();
			departmentDataAdd();
			setEventActions();
			cmbDepartmentName.focus();
			focusEnter();
		}

		private void focusEnter()
		{
			allComp.add(cmbDepartmentName);
			allComp.add(cmbSectionName);
			allComp.add(dWorkingMonth);
			allComp.add(cButton.btnFind);
			new FocusMoveByEnter(this,allComp);
		}

		private void tableinitialization()
		{
			table.setColumnCollapsingAllowed(true);
			table.setWidth("98%");
			table.setHeight("235px");
			table.setPageLength(0);

			table.addContainerProperty("SL#", Label.class , new Label());
			table.setColumnWidth("SL#",20);

			table.addContainerProperty("Month", PopupDateField.class , new PopupDateField());
			table.setColumnWidth("Month",120);

			table.addContainerProperty("Department ID", Label.class , new Label());
			table.setColumnWidth("Department ID",50);

			table.addContainerProperty("Department Name", Label.class , new Label());
			table.setColumnWidth("Department Name",145);

			table.addContainerProperty("Section ID", Label.class , new Label());
			table.setColumnWidth("Section ID",50);

			table.addContainerProperty("Section Name", Label.class , new Label());
			table.setColumnWidth("Section Name",145);

			table.setColumnCollapsed("Department ID", true);
			table.setColumnCollapsed("Section ID", true);

			rowAddinTable();	
		}

		public void rowAddinTable()
		{
			for(int i=0; i<8; i++)
			{
				tableRowAdd(i);
			}
		}

		public void tableRowAdd(final int ar)
		{
			table.setSelectable(true);
			table.setImmediate(true);
			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);		

			lbSL.add(ar, new Label(""));
			lbSL.get(ar).setWidth("100%");
			lbSL.get(ar).setHeight("20px");
			lbSL.get(ar).setValue(ar+1);

			lblMonthName.add(ar,new PopupDateField());
			lblMonthName.get(ar).setDateFormat("MMMMM-yyyy");
			lblMonthName.get(ar).setResolution(PopupDateField.RESOLUTION_MONTH);
			lblMonthName.get(ar).setReadOnly(true);
			lblMonthName.get(ar).setWidth("100%");
			lblMonthName.get(ar).setHeight("20px");

			lblDepartmentID.add(ar, new Label(""));
			lblDepartmentID.get(ar).setWidth("100%");
			lblDepartmentID.get(ar).setHeight("20.0px");

			lblDepartmentName.add(ar, new Label(""));
			lblDepartmentName.get(ar).setWidth("100%");
			lblDepartmentName.get(ar).setHeight("20px");

			lblSectionID.add(ar, new Label(""));
			lblSectionID.get(ar).setWidth("100%");
			lblSectionID.get(ar).setHeight("20.0px");

			lblSectionName.add(ar, new Label(""));
			lblSectionName.get(ar).setWidth("100%");
			lblSectionName.get(ar).setHeight("20px");

			table.addItem(new Object[]{lbSL.get(ar),lblMonthName.get(ar),lblDepartmentID.get(ar),lblDepartmentName.get(ar),
					lblSectionID.get(ar),lblSectionName.get(ar)},ar);
		}

		private void departmentDataAdd()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select distinct vDepartmentID,vDepartmentName from tbAdjustment_Deduction " +
						"where MONTH(dDate)='"+dMonthFormat.format(dWorkingMonth.getValue())+"' " +
						"and YEAR(dDate)='"+dYearFormat.format(dWorkingMonth.getValue())+"' and vDepartmentID='DEPT10' order by vDepartmentName";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbDepartmentName.addItem(element[0]);
					cmbDepartmentName.setItemCaption(element[0], element[1].toString());	
				}
			}

			catch(Exception ex)
			{
				showNotification("departmentDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void sectionDataAdd()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select distinct vSectionId,vSectionName from tbAdjustment_Deduction " +
						"where MONTH(dDate)='"+dMonthFormat.format(dWorkingMonth.getValue())+"' " +
						"and YEAR(dDate)='"+dYearFormat.format(dWorkingMonth.getValue())+"' " +
						"and vDepartmentID='"+cmbDepartmentName.getValue()+"' " +
						"order by vSectionName";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());	
				}
			}

			catch(Exception ex)
			{
				showNotification("sectionDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void setEventActions()
		{
			dWorkingMonth.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(dWorkingMonth.getValue()!=null)
					{
						departmentDataAdd();
					}
				}
			});

			cmbDepartmentName.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbDepartmentName.getValue()!=null)
					{
						sectionDataAdd();
					}
				}
			});

			ChkSection.addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(ChkSection.booleanValue())
					{
						cmbSectionName.setValue(null);
						cmbSectionName.setEnabled(false);
					}
					else
					{
						cmbSectionName.setEnabled(true);
					}
				}
			});

			cButton.btnFind.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbDepartmentName.getValue()!=null)
					{
						if(cmbSectionName.getValue()!=null || ChkSection.booleanValue())
						{
							findButtonEvent();
						}
						else
						{
							showNotification("Please Select Section Name ", Notification.TYPE_WARNING_MESSAGE);
							cmbSectionName.focus();
						}
					}

					else 
					{
						showNotification("Please Select Department Name ", Notification.TYPE_WARNING_MESSAGE);
						cmbDepartmentName.focus();
					}
				}
			});

			table.addListener(new ItemClickListener() 
			{
				public void itemClick(ItemClickEvent event) 
				{
					if(event.isDoubleClick())
					{
						lblMonthName.get(Integer.valueOf(event.getItemId().toString())).setReadOnly(false);
						strMonth.setValue(dFormat.format(lblMonthName.get(Integer.valueOf(event.getItemId().toString())).getValue()));
						lblMonthName.get(Integer.valueOf(event.getItemId().toString())).setReadOnly(true);
						strSectionID.setValue(lblSectionID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
						strDepartmentID.setValue(lblDepartmentID.get(Integer.valueOf(event.getItemId().toString())).getValue());
						windowClose();
					}
				}
			});
		}

		private void findButtonEvent()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String Findquery = " SELECT distinct dDate,vDepartmentID,vDepartmentName,vSectionID,vSectionName from tbAdjustment_Deduction" +
						" where vSectionId like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%")+"' " +
						" and Month(dDate)=Month('"+dFormat.format(dWorkingMonth.getValue())+"') and " +
						" Year(dDate)=Year('"+dFormat.format(dWorkingMonth.getValue())+"') order by vSectionName";

				List <?> list = session.createSQLQuery(Findquery).list();

				if(!list.isEmpty())
				{
					tableclear();
					int i=0;
					for(Iterator <?> iter = list.iterator(); iter.hasNext();)
					{
						Object[] element = (Object[]) iter.next();
						lblMonthName.get(i).setReadOnly(false);
						lblMonthName.get(i).setValue(element[0]);
						lblMonthName.get(i).setReadOnly(true);
						lblDepartmentID.get(i).setValue(element[1].toString());
						lblDepartmentName.get(i).setValue(element[2].toString());
						lblSectionID.get(i).setValue(element[3].toString());
						lblSectionName.get(i).setValue(element[4].toString());

						if((i)==lblSectionName.size()-1)
						{
							tableRowAdd(i+1);
						}
						i++;
					}
				}
				else
				{
					tableclear();
					showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
				}
			}
			catch (Exception ex)
			{
				showNotification("findButtonEvent", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void tableclear()
		{
			for(int i=0; i<lbSL.size(); i++)
			{
				lblMonthName.get(i).setReadOnly(false);
				lblMonthName.get(i).setValue(null);
				lblMonthName.get(i).setReadOnly(true);
				lblDepartmentID.get(i).setValue("");
				lblDepartmentName.get(i).setValue("");
				lblSectionID.get(i).setValue("");
				lblSectionName.get(i).setValue("");
			}
		}

		private void windowClose()
		{
			this.close();
		}

		private AbsoluteLayout buildMainLayout()
		{
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(true);
			mainLayout.setMargin(false);

			setWidth("530px");
			setHeight("385px");

			lblWorkingMonth = new Label("Month :");
			lblWorkingMonth.setImmediate(true);
			lblWorkingMonth.setWidth("-1px");
			lblWorkingMonth.setHeight("-1px");
			mainLayout.addComponent(lblWorkingMonth, "top:20.0px;left:20.0px;");

			dWorkingMonth.setValue(new java.util.Date());
			dWorkingMonth.setDateFormat("MMMMM-yyyy");
			dWorkingMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
			dWorkingMonth.setInvalidAllowed(false);
			dWorkingMonth.setImmediate(true);
			dWorkingMonth.setWidth("130px");
			mainLayout.addComponent(dWorkingMonth, "top:18.0px;left:140.0px;");

			cmbDepartmentName = new ComboBox();
			cmbDepartmentName.setImmediate(true);
			cmbDepartmentName.setWidth("260px");
			cmbDepartmentName.setHeight("24px");
			cmbDepartmentName.setImmediate(true);
			cmbDepartmentName.setNullSelectionAllowed(false);
			mainLayout.addComponent(new Label("Department Name : "), "top:45.0px; left:20.0px;");
			mainLayout.addComponent(cmbDepartmentName, "top:43.0px;left:140.0px;");

			lblSectionname = new Label();
			lblSectionname.setImmediate(true);
			lblSectionname.setWidth("-1px");
			lblSectionname.setHeight("-1px");
			lblSectionname.setValue("Section Name  :");
			mainLayout.addComponent(lblSectionname, "top:70.0px;left:20.0px;");

			cmbSectionName = new ComboBox();
			cmbSectionName.setImmediate(true);
			cmbSectionName.setWidth("260px");
			cmbSectionName.setHeight("24px");
			cmbSectionName.setImmediate(true);
			cmbSectionName.setNullSelectionAllowed(false);
			mainLayout.addComponent(cmbSectionName, "top:68.0px;left:140.0px;");

			ChkSection = new CheckBox("All");
			ChkSection.setWidth("-1px");
			ChkSection.setHeight("24px");
			ChkSection.setImmediate(true);
			mainLayout.addComponent(ChkSection, "top:70.0px;left:400.0px;");

			cButton.btnFind.setWidth("80px");
			cButton.btnFind.setHeight("28px");
			mainLayout.addComponent(cButton.btnFind, "top:68.0px;left:440.0px;");

			mainLayout.addComponent(table, "top:100.0px;left:15.0px;");
			return mainLayout;
		}

}
