package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import com.common.share.*;
import java.text.SimpleDateFormat;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class individualLeaveEntitlementFind extends Window 
{
	private AbsoluteLayout mainLayout;
	private Label lbYear = new Label("Year :");
	private InlineDateField dYear = new InlineDateField();

	private Label lbDeptName;
	private ComboBox cmbDepartName;

	private Label lbSecName;
	private ComboBox cmbSecName;

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	String computerName = "";
	String userName = "";
	String year = "";
	String deptID = "";

	String receiptempID = "";
	String receiptDeptID = "";

	private TextRead trIdDept = new TextRead();

	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private Table table = new Table();

	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblEmpID=new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> amtCL = new ArrayList<Label>();
	private ArrayList<Label> amtSL = new ArrayList<Label>();
	private ArrayList<Label> amtAL = new ArrayList<Label>();

	public individualLeaveEntitlementFind(SessionBean sessionBean, TextRead trIdDept)
	{
		this.trIdDept = trIdDept;
		this.sessionBean = sessionBean;

		computerName = sessionBean.getUserName();
		userName = sessionBean.getUserName();

		this.setCaption("FIND INDIVIDUAL LEAVE ENTITLEMENT :: "+sessionBean.getCompany());
		this.setWidth("606px");
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);

		tableInitialize();
		departMentdataAdd();
		cmbButtonAction();
	}

	private void tableInitialize()
	{
		table.setColumnCollapsingAllowed(true);

		table.setWidth("98%");
		table.setHeight("200px");
		table.setPageLength(0);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("EMP ID", Label.class , new Label());
		table.setColumnWidth("EMP ID",50);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",70);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",222);

		table.addContainerProperty("CL", Label.class , new Label());
		table.setColumnWidth("CL",33);	

		table.addContainerProperty("SL", Label.class , new Label());
		table.setColumnWidth("SL",33);

		table.addContainerProperty("AL", Label.class , new Label());
		table.setColumnWidth("AL",33);

		table.setColumnCollapsed("EMP ID", true);

		rowAddinTable();
	}

	public void rowAddinTable()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbSl.add(ar, new Label(""));
		lbSl.get(ar).setWidth("100%");
		lbSl.get(ar).setHeight("20px");
		lbSl.get(ar).setValue(ar+1);

		lblEmpID.add(ar, new Label(""));
		lblEmpID.get(ar).setWidth("100%");
		lblEmpID.get(ar).setHeight("20px");

		lbEmployeeID.add(ar, new Label(""));
		lbEmployeeID.get(ar).setWidth("100%");
		lbEmployeeID.get(ar).setImmediate(true);
		lbEmployeeID.get(ar).setHeight("23px");

		lbEmployeeName.add(ar, new Label(""));
		lbEmployeeName.get(ar).setWidth("100%");
		lbEmployeeName.get(ar).setImmediate(true);
		lbEmployeeName.get(ar).setHeight("23px");

		amtCL.add(ar, new Label());
		amtCL.get(ar).setWidth("100%");
		amtCL.get(ar).setImmediate(true);
		amtCL.get(ar).setHeight("20px");

		amtSL.add(ar, new Label());
		amtSL.get(ar).setWidth("100%");
		amtSL.get(ar).setImmediate(true);
		amtSL.get(ar).setHeight("20px");

		amtAL.add(ar, new Label());
		amtAL.get(ar).setWidth("100%");
		amtAL.get(ar).setImmediate(true);
		amtAL.get(ar).setHeight("20px");

		table.addItem(new Object[]{lbSl.get(ar),lblEmpID.get(ar),lbEmployeeID.get(ar),lbEmployeeName.get(ar),
				amtCL.get(ar),amtSL.get(ar),amtAL.get(ar)},ar);
	}

	private void departMentdataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					"tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where (vEmployeeType='Permanent' " +
					"or vEmployeeType='Provisionary') order by dept.vDepartmentName";
			System.out.println("EMP Name :"+query);
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbDepartName.addItem(element[0]);
				cmbDepartName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("departMentdataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void sectiondataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct ein.vSectionId,sein.SectionName from tbSectionInfo sein inner join "
					+ " tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where (vEmployeeType='Permanent' "
					+ " or vEmployeeType='Provisionary') and ein.vDepartmentID = '"+cmbDepartName.getValue()+"' order "
					+ " by sein.SectionName";
			System.out.println("EMP Name :"+query);
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbSecName.addItem(element[0]);
				cmbSecName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("sectiondataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbButtonAction()
	{
		cmbDepartName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				cmbSecName.removeAllItems();
				if(cmbDepartName.getValue()!=null)
				{
					sectiondataAdd();
				}
			}
		});

		cmbSecName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSecName.getValue()!=null)
				{
					tableclear();
					tableDataAdding();
				}
			}
		});

		table.addListener(new ItemClickListener()
		{
			public void itemClick(ItemClickEvent event)
			{
				if(cmbDepartName.getValue()!=null)
				{
					if(cmbSecName.getValue()!=null)
					{
						if(event.isDoubleClick())
						{
							receiptempID =lblEmpID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
							trIdDept.setValue(receiptempID);
							windowClose();
						}
					}
					else
					{
						showNotification("Warning","Select Section!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Department!!!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
	}

	private void windowClose()
	{
		this.close();
	}

	private void tableclear()
	{
		for(int i=0; i<lbEmployeeID.size(); i++)
		{
			lbEmployeeID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			amtCL.get(i).setValue("");
			amtSL.get(i).setValue("");
			amtAL.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " select LB.vAutoEmployeeId,LB.vEmployeeId,EI.vEmployeeName,LB.iClOpening,LB.iSlOpening,LB.iAlOpening "
					+ " from tbLeaveBalanceNew LB inner join tbEmployeeInfo EI on LB.vAutoEmployeeId=EI.vEmployeeId where "
					+ " LB.vSectionId = '"+cmbSecName.getValue().toString()+"' and YEAR(LB.currentYear)=YEAR('"+dateformat.format(dYear.getValue())+"') "
					+ " and (vEmployeeType='Permanent' or vEmployeeType='Provisionary') order by LB.vEmployeeId";

			System.out.println("Increment : "+query);
			List <?> list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblEmpID.get(i).setValue(element[0]);
					lbEmployeeID.get(i).setValue(element[1]);
					lbEmployeeName.get(i).setValue(element[2]);
					amtCL.get(i).setValue(element[3]);
					amtSL.get(i).setValue(element[4]);
					amtAL.get(i).setValue(element[5]);

					if((i)==lblEmpID.size()-1) 
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification(ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("560px");
		setHeight("345px");

		lbYear = new Label();
		lbYear.setImmediate(true);
		lbYear.setWidth("-1px");
		lbYear.setHeight("-1px");
		lbYear.setValue("Year :");
		mainLayout.addComponent(lbYear, "top:20.0px;left:25.0px;");

		dYear = new InlineDateField();
		dYear.setValue(new java.util.Date());
		dYear.setWidth("110px");
		dYear.setHeight("24px");
		dYear.setResolution(InlineDateField.RESOLUTION_YEAR);
		dYear.setDateFormat("yyyy");
		dYear.setInvalidAllowed(false);
		dYear.setImmediate(true);
		mainLayout.addComponent(dYear, "top:18.0px;left:65.0px;");

		lbDeptName = new Label();
		lbDeptName.setImmediate(true);
		lbDeptName.setWidth("-1px");
		lbDeptName.setHeight("-1px");
		lbDeptName.setValue("Department Name :");
		mainLayout.addComponent(lbDeptName, "top:20.0px;left:200.0px;");

		cmbDepartName = new ComboBox();
		cmbDepartName.setImmediate(true);
		cmbDepartName.setWidth("210px");
		cmbDepartName.setHeight("24px");
		cmbDepartName.setImmediate(true);
		cmbDepartName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbDepartName, "top:18.0px;left:320.0px;");

		lbSecName = new Label();
		lbSecName.setImmediate(true);
		lbSecName.setWidth("-1px");
		lbSecName.setHeight("-1px");
		lbSecName.setValue("Section Name :");
		mainLayout.addComponent(lbSecName, "top:50.0px;left:200.0px;");

		cmbSecName = new ComboBox();
		cmbSecName.setImmediate(true);
		cmbSecName.setWidth("210px");
		cmbSecName.setHeight("24px");
		cmbSecName.setImmediate(true);
		cmbSecName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbSecName, "top:48.0px;left:320.0px;");

		mainLayout.addComponent(table, "top:85.0px;left:20.0px;");

		return mainLayout;
	}
}
