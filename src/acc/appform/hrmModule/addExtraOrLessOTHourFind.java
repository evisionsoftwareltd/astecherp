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
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class addExtraOrLessOTHourFind extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;

	private Label lblMonth;
	private PopupDateField dMonth=new PopupDateField();

	private Table table = new Table();
	private ArrayList<Label> tbLblSL = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbDMonth=new ArrayList<PopupDateField>();
	private ArrayList<Label> tbLblDepartmentID=new ArrayList<Label>();
	private ArrayList<Label> tbLblDepartmentName=new ArrayList<Label>();
	private ArrayList<Label> tbLblSectionID=new ArrayList<Label>();
	private ArrayList<Label> tbLblSectionName=new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	String autoId = "";
	String empId = "";
	String ApplicationDate="";
	String findDate = "a";
	String Section="";

	private TextRead txtFindDate = new TextRead();
	private TextRead txtSectionId = new TextRead();
	private TextRead txtDepartmentID = new TextRead(); 
	
	public addExtraOrLessOTHourFind(SessionBean sessionBean, TextRead txtDepartmentID, TextRead txtSectionID, TextRead txtFindDate)
	{
		this.txtSectionId=txtSectionID;
		this.txtFindDate = txtFindDate;
		this.txtDepartmentID=txtDepartmentID;
		this.sessionBean = sessionBean;
		this.setCaption("FIND ADD/LESS EXTRA OT HOUR :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("570px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		buildMainLayout();
		setContent(mainLayout);
		tableinitialization();
		setEventActions();
		focusEnter();
	}

	private void focusEnter()
	{
		allComp.add(dMonth);
		allComp.add(cButton.btnFind);
		new FocusMoveByEnter(this,allComp);
	}

	@SuppressWarnings("static-access")
	private void tableinitialization()
	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("285px");
		table.setPageLength(0);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Date", 120);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 60);
		
		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 130);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 60);
		
		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 130);
		
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("Department ID", true);
		
		table.setColumnAlignments(new String[]{table.ALIGN_RIGHT,table.ALIGN_CENTER,
				table.ALIGN_RIGHT,table.ALIGN_LEFT,table.ALIGN_RIGHT,table.ALIGN_LEFT});
		
		rowAddinTable();	
	}

	public void rowAddinTable()
	{
		for(int i=0; i<10; i++)
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

		tbLblSL.add(ar, new Label());
		tbLblSL.get(ar).setWidth("100%");
		tbLblSL.get(ar).setHeight("20px");
		tbLblSL.get(ar).setValue(ar+1);

		tbDMonth.add(ar, new PopupDateField());
		tbDMonth.get(ar).setDateFormat("MMMMM-yyyy");
		tbDMonth.get(ar).setResolution(PopupDateField.RESOLUTION_MONTH);
		tbDMonth.get(ar).setImmediate(true);
		tbDMonth.get(ar).setReadOnly(true);
		tbDMonth.get(ar).setWidth("100%");
		tbDMonth.get(ar).setHeight("20px");

		tbLblDepartmentID.add(ar, new Label());
		tbLblDepartmentID.get(ar).setWidth("100%");
		tbLblDepartmentID.get(ar).setHeight("20px");
		
		tbLblDepartmentName.add(ar, new Label());
		tbLblDepartmentName.get(ar).setWidth("100%");
		tbLblDepartmentName.get(ar).setHeight("20px");

		tbLblSectionID.add(ar, new Label());
		tbLblSectionID.get(ar).setWidth("100%");
		tbLblSectionID.get(ar).setHeight("20px");
		
		tbLblSectionName.add(ar, new Label());
		tbLblSectionName.get(ar).setWidth("100%");
		tbLblSectionName.get(ar).setHeight("20px");
		
		table.addItem(new Object[]{tbLblSL.get(ar),tbDMonth.get(ar),tbLblDepartmentID.get(ar),
				tbLblDepartmentName.get(ar),tbLblSectionID.get(ar),tbLblSectionName.get(ar)},ar);
	}

	private void setEventActions()
	{
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dMonth.getValue()!=null) 
				{
					findButtonEvent();
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{

					txtFindDate.setValue(dFormat.format(tbDMonth.get(Integer.valueOf(event.getItemId().toString())).getValue()));
					txtSectionId.setValue(tbLblSectionID.get(Integer.valueOf(event.getItemId().toString())).getValue());
					txtDepartmentID.setValue(tbLblDepartmentID.get(Integer.valueOf(event.getItemId().toString())).getValue());
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
			String Findquery = " SELECT distinct dDate,vDepartmentID,vDepartmentName,vSectionID,vSectionName from tbAddOrLessExtraOT " +
					"where Month(dDate) = MONTH('"+dFormat.format(dMonth.getValue())+"') and YEAR(dDate)=YEAR('"+dFormat.format(dMonth.getValue())+"') order by dDate desc";

			List <?> list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					tbDMonth.get(i).setReadOnly(false);
					tbDMonth.get(i).setValue(element[0]);
					tbDMonth.get(i).setReadOnly(true);
					tbLblDepartmentID.get(i).setValue(element[1]);
					tbLblDepartmentName.get(i).setValue(element[2]);
					tbLblSectionID.get(i).setValue(element[3]);
					tbLblSectionName.get(i).setValue(element[4]);

					if((i)==tbDMonth.size()-1)
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
		for(int i=0; i<tbLblSL.size(); i++)
		{
			tbDMonth.get(i).setReadOnly(false);
			tbDMonth.get(i).setValue(null);
			tbDMonth.get(i).setReadOnly(true);
			tbLblDepartmentID.get(i).setValue("");
			tbLblDepartmentName.get(i).setValue("");
			tbLblSectionID.get(i).setValue("");
			tbLblSectionName.get(i).setValue("");
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
		setHeight("415px");

		lblMonth = new Label("To Date :");
		lblMonth.setImmediate(true);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:35.0px;left:25.0px;");

		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setInvalidAllowed(false);
		dMonth.setImmediate(true);
		dMonth.setWidth("150px");
		mainLayout.addComponent(dMonth, "top:33.0px;left:100.0px;");

		cButton.btnFind.setWidth("80px");
		cButton.btnFind.setHeight("28px");
		mainLayout.addComponent(cButton.btnFind, "top:33.0px;left:440.0px;");

		mainLayout.addComponent(table, "top:65.0px;left:15.0px;");
		return mainLayout;
	}
}
