package com.reportform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptReport extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblSection;
	private Label lblDepartment;
	private Label lblDesignation;	
	private Label lblCategory;
	private Label lblReligion;

	private Table departmentTable = new Table();
	private Table sectionTable = new Table();
	private Table designationTable = new Table();
	private Table categoryTable = new Table();
	private Table religionTable = new Table();
	private Table bloodGroupTable = new Table();

	// Section Table
	private ArrayList<Label>tblLbldepartmentName = new ArrayList<Label>();	
	private ArrayList<CheckBox>tblChkdepartment = new ArrayList<CheckBox>();
	private ArrayList<Label> tbllbldepartmentId = new ArrayList<Label>();
	private ArrayList<Label> tbDeptCheckId = new ArrayList<Label>();

	// Section Table
	private ArrayList<Label>tblLblsectionName = new ArrayList<Label>();	
	private ArrayList<CheckBox>tblChkSection = new ArrayList<CheckBox>();
	private ArrayList<Label> tbllblSecId = new ArrayList<Label>();
	private ArrayList<Label> tbCheckId = new ArrayList<Label>();

	// DesignationTable
	private ArrayList<Label>tblLblDesName = new ArrayList<Label>();	
	private ArrayList<CheckBox>tblChkDes = new ArrayList<CheckBox>();
	private ArrayList<Label> tbllblDesId = new ArrayList<Label>();
	private ArrayList<Label> tbDesCheckId = new ArrayList<Label>();

	// CategoryTable
	private ArrayList<Label>tblLblCategoryName = new ArrayList<Label>();	
	private ArrayList<CheckBox>tblChkCategory = new ArrayList<CheckBox>();
	private ArrayList<Label> tbllblCategory = new ArrayList<Label>();
	private ArrayList<Label> tbCategoryId = new ArrayList<Label>();

	// ReligionTable
	private ArrayList<Label>tblLblReligionName = new ArrayList<Label>();	
	private ArrayList<CheckBox>tblChkReligion = new ArrayList<CheckBox>();
	private ArrayList<Label> tbllblReligion = new ArrayList<Label>();
	private ArrayList<Label> tbReligionId = new ArrayList<Label>();

	private ArrayList<Label> tbLblBloodGroup = new ArrayList<Label>();
	private ArrayList<CheckBox> tbChkBlood = new ArrayList<CheckBox>();

	// CheckBox
	private CheckBox chkDepartmentAll;
	private CheckBox chkSectionAll;
	private CheckBox chkDesignationAll;
	private CheckBox chkCategoryAll;
	private CheckBox chkReligionAll;
	private CheckBox chkBloodGroup;
	private CheckBox chkPhysicallyDisabled;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	private static final String[] category = new String[] {"Permanent", "Temporary", "Provisionary", "Casual"};
	private static final String[] religion = new String[] {"Islam","Hindu","Buddism","Cristian"};

	private String departmentIds="";
	private String sectionIds="";
	private String designationIds="";
	private String categoryIds="";
	private String religionIDs="";
	private String bloodGroup="";
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private static final String CHO="'DEPT10'";
	public RptReport(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("PROJECT REPORT :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		departmentTableInitialise();
		departmentTableDataAdd();
		sectionTableInitialise();
		sectionTableDataAdd();
		desTableInitialise();
		designationTableDataAdd();
		categoryTableInitialise();
		categoryTableDataAdd();
		bloodGroupTableInitialise();
		bloodGroupTableDataAdd();
		religionTableInitialise();
		religionTableDataAdd();
		focusMove();
		setEventAction();
	}

	public void setEventAction()
	{
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!departmentIds.equals("") || chkDepartmentAll.booleanValue())
				{
					if(!sectionIds.equals("") || chkSectionAll.booleanValue())
					{
						if(!designationIds.equals("") || chkDesignationAll.booleanValue())
						{
							if(!categoryIds.equals("") || chkCategoryAll.booleanValue())
							{
								if(!religionIDs.equals("") || chkReligionAll.booleanValue())
								{
									reportShow();
								}
								else
								{
									showNotification("Warning","At Least Select One Row From Religion Table",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Warning","At Least Select One Row From Category Table",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","At Least Select One Row From Designation Table",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","At Least Select One Row From Section Table",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","At Least Select One Row From Department Table",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkDepartmentAll.booleanValue()==true)
				{
					departmentTable.setEnabled(false);
					departmentIds = "";
					for(int i=0;i<tblLbldepartmentName.size();i++)
					{
						tblChkdepartment.get(i).setValue(false);
					}
				}
				else
				{
					departmentTable.setEnabled(true);
					departmentIds = "";
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkSectionAll.booleanValue()==true)
				{
					sectionTable.setEnabled(false);
					sectionIds = "";
					for(int i=0;i<tblLblsectionName.size();i++)
					{
						tblChkSection.get(i).setValue(false);
					}
				}
				else
				{
					sectionTable.setEnabled(true);
					sectionIds = "";
				}
			}
		});

		chkDesignationAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkDesignationAll.booleanValue()==true)
				{
					designationTable.setEnabled(false);
					designationIds = "";
					for(int i=0;i<tblLblDesName.size();i++)
					{
						tblChkDes.get(i).setValue(false);
					}
				}
				else
				{
					designationTable.setEnabled(true);
					designationIds = "";
				}
			}
		});

		chkCategoryAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkCategoryAll.booleanValue()==true)
				{
					categoryTable.setEnabled(false);
					categoryIds = "";
					for(int i=0;i<tblLblCategoryName.size();i++)
					{
						tblChkCategory.get(i).setValue(false);
					}
				}
				else
				{
					categoryTable.setEnabled(true);
					categoryIds = "";
				}
			}
		});

		chkBloodGroup.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkBloodGroup.booleanValue()==true)
				{
					bloodGroupTable.setEnabled(false);
					bloodGroup = "";
					for(int i=0;i<tbLblBloodGroup.size();i++)
					{
						tbChkBlood.get(i).setValue(false);
					}
				}
				else
				{
					bloodGroupTable.setEnabled(true);
					bloodGroup = "";
				}
			}
		});

		chkReligionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkReligionAll.booleanValue()==true)
				{
					religionTable.setEnabled(false);
					religionIDs = "";
					for(int i=0;i<tblLblReligionName.size();i++)
					{
						tblChkReligion.get(i).setValue(false);
					}
				}
				else
				{
					religionTable.setEnabled(true);
					religionIDs = "";
				}
			}
		});
	}

	private void reportShow()
	{

		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query="";
		String DepartmentIN="";
		String setionIn="";
		String designationIn="";
		String categoryIn="";
		String religionIn="";
		String bloodGroupIn="";

		try
		{
			if(chkDepartmentAll.booleanValue()==true)
			{
				DepartmentIN="like";
				departmentIds="'%'";
			}
			else
			{
				DepartmentIN="in";
			}


			if(chkSectionAll.booleanValue()==true)
			{
				setionIn="like";
				sectionIds="'%'";
			}
			else
			{
				setionIn="in";
			}

			if(chkDesignationAll.booleanValue()==true)
			{
				designationIn="like";
				designationIds="'%'";
			}
			else
			{
				designationIn="in";
			}

			if(chkCategoryAll.booleanValue()==true)
			{
				categoryIn="like";
				categoryIds="'%'";
			}
			else
			{
				categoryIn="in";
			}

			if(chkReligionAll.booleanValue()==true)
			{
				religionIn="like";
				religionIDs="'%'";
			}
			else
			{
				religionIn="in";
			}

			if(chkBloodGroup.booleanValue())
			{
				bloodGroupIn="like";
				bloodGroup="'%'";
			}
			else
			{
				bloodGroupIn="in";
			}

			if(!chkPhysicallyDisabled.booleanValue())
			{
				query = " SELECT vEmployeeType,dept.vDepartmentName,b.SectionName,employeeCode,iFingerID,vProximityId," +
						" vEmployeeId,vEmployeeName,c.designationName,vReligion,iStatus,a.dJoiningDate from tbEmployeeInfo " +
						" as a inner join tbDepartmentInfo as dept on a.vDepartmentID=dept.vDepartmentID inner join " +
						" tbSectionInfo as b on a.vSectionId=b.vSectionId inner join tbDesignationInfo as c on " +
						" a.vDesignationId=c.designationId where a.vDepartmentId "+DepartmentIN+" ("+departmentIds+") and "+
						" a.vSectionId "+setionIn+" ("+sectionIds+") and a.vDesignationId "+designationIn+" ("+designationIds+") and" +
						" a.vEmployeeType "+categoryIn+" ("+categoryIds+") and a.vReligion "+religionIn+" ("+religionIDs+") " +
						" and vBloodGroup "+bloodGroupIn+" ("+bloodGroup+") and a.vDepartmentId!="+CHO+" order by dept.vDepartmentName,b.SectionName," +
						" a.iStatus,a.employeeCode";
			}

			else
			{
				query = " SELECT a.vEmployeeType,dept.vDepartmentName,b.SectionName,a.employeeCode,a.iFingerID,a.vProximityId," +
						" a.vEmployeeId,a.vEmployeeName,c.designationName,a.vReligion,a.iStatus,a.dJoiningDate from tbEmployeeInfo as a" +
						" inner join tbDepartmentInfo as dept on a.vDepartmentID=dept.vDepartmentID inner join tbSectionInfo as " +
						" b on a.vSectionId=b.vSectionId inner join tbDesignationInfo as c on a.vDesignationId=c.designationId " +
						" inner join tbPhysicallyDisable pd on pd.vEmployeeID=a.vEmployeeId where a.vDepartmentId "+DepartmentIN+" " +
						" ("+departmentIds+")  and  a.vSectionId "+setionIn+" ("+sectionIds+") and a.vDesignationId "+designationIn+" " +
						" ("+designationIds+") and a.vEmployeeType "+categoryIn+" ("+categoryIds+") and a.vReligion "+religionIn+" ("+religionIDs+") " +
						" and vBloodGroup "+bloodGroupIn+" ("+bloodGroup+") and a.vDepartmentId!="+CHO+" order by dept.vDepartmentName,b.SectionName,a.iStatus,a.employeeCode";
			}

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEmployeeList.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try 
		{
			Iterator <?> iter = session.createSQLQuery(sql).list().iterator();
			if (iter.hasNext()) 
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

	private void focusMove()
	{
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	public void departmentTableInitialise()
	{
		for(int i=0;i<=0;i++)
		{
			departmentTableRowAdd(i);
		}
	}

	public void departmentTableRowAdd(final int ar)
	{
		tblLbldepartmentName.add(ar, new Label(""));
		tblLbldepartmentName.get(ar).setWidth("100%");
		tblLbldepartmentName.get(ar).setImmediate(true);
		tblLbldepartmentName.get(ar).setHeight("16px");

		tblChkdepartment.add(ar, new CheckBox());
		tblChkdepartment.get(ar).setWidth("100%");
		tblChkdepartment.get(ar).setImmediate(true);
		tblChkdepartment.get(ar).setHeight("16px");
		tblChkdepartment.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				//				System.out.println(ar+" "+tbllblSecId.get(ar).getValue());

				if(tblChkdepartment.get(ar).booleanValue()==true)
				{
					tbDeptCheckId.get(ar).setValue(tbllbldepartmentId.get(ar).getValue());
				}
				else
				{
					tbDeptCheckId.get(ar).setValue("");
					departmentIds = "";
				}
				String str = "";
				for(int i=0;i<tblChkdepartment.size();i++)
				{
					if(!tbDeptCheckId.get(i).getValue().toString().isEmpty())
					{
						str = "'"+tbDeptCheckId.get(i).getValue().toString()+"'"+","+str;
						System.out.println(str);
					}
				}

				if(str != "")
				{
					int length = str.length();
					departmentIds = str.substring(0, length-1);
					System.out.println("DepartmentID's: "+departmentIds);
				}
			}
		});


		tbllbldepartmentId.add(ar, new Label("")); // Chechbox

		tbDeptCheckId.add(ar, new Label("")); // ChechId with table column

		departmentTable.addItem(new Object[]{tblLbldepartmentName.get(ar),tblChkdepartment.get(ar)},ar);
	}

	private void departmentTableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Findquery =" SELECT vDepartmentID,vDepartmentName from tbDepartmentInfo where vDepartmentId!="+CHO+" order by " +
					"vDepartmentName ";
			List <?> list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tbllbldepartmentId.get(i).setValue(element[0]);
					tblLbldepartmentName.get(i).setValue(element[1]);

					if((i)==tblLbldepartmentName.size()-1)
					{
						departmentTableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("departmentTableDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void sectionTableInitialise()
	{
		for(int i=0;i<=0;i++)
		{
			sectionTableRowAdd(i);
		}
	}

	public void sectionTableRowAdd(final int ar)
	{
		tblLblsectionName.add(ar, new Label(""));
		tblLblsectionName.get(ar).setWidth("100%");
		tblLblsectionName.get(ar).setImmediate(true);
		tblLblsectionName.get(ar).setHeight("16px");

		tblChkSection.add(ar, new CheckBox());
		tblChkSection.get(ar).setWidth("100%");
		tblChkSection.get(ar).setImmediate(true);
		tblChkSection.get(ar).setHeight("16px");
		tblChkSection.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				//				System.out.println(ar+" "+tbllblSecId.get(ar).getValue());

				if(tblChkSection.get(ar).booleanValue()==true)
				{
					tbCheckId.get(ar).setValue(tbllblSecId.get(ar).getValue());
				}
				else
				{
					tbCheckId.get(ar).setValue("");
					sectionIds = "";
				}
				String str = "";
				for(int i=0;i<tblChkSection.size();i++)
				{
					if(!tbCheckId.get(i).getValue().toString().isEmpty())
					{
						str = "'"+tbCheckId.get(i).getValue().toString()+"'"+","+str;
						System.out.println(str);
					}
				}

				if(str != "")
				{
					int length = str.length();
					sectionIds = str.substring(0, length-1);
					System.out.println("SectionID's: "+sectionIds);
				}
			}
		});


		tbllblSecId.add(ar, new Label("")); // Chechbox

		tbCheckId.add(ar, new Label("")); // ChechId with table column

		sectionTable.addItem(new Object[]{tblLblsectionName.get(ar),tblChkSection.get(ar)},ar);
	}

	private void sectionTableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Findquery =" SELECT vSectionID,vDepartmentName,SectionName from tbSectionInfo where vDepartmentId!="+CHO+" order by vDepartmentName,SectionName ";
			List <?> list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tbllblSecId.get(i).setValue(element[0]);
					tblLblsectionName.get(i).setValue(element[1].toString()+" ("+element[2].toString()+")");

					if((i)==tblLblsectionName.size()-1)
					{
						sectionTableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("sectionTableDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void desTableInitialise()
	{
		for(int i=0;i<=0;i++)
		{
			desTableRowAdd(i);
		}
	}

	public void desTableRowAdd(final int ar)
	{
		tblLblDesName.add(ar, new Label(""));
		tblLblDesName.get(ar).setWidth("100%");
		tblLblDesName.get(ar).setImmediate(true);
		tblLblDesName.get(ar).setHeight("16px");

		tblChkDes.add(ar, new CheckBox());
		tblChkDes.get(ar).setWidth("100%");
		tblChkDes.get(ar).setImmediate(true);
		tblChkDes.get(ar).setHeight("16px");
		tblChkDes.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				//				System.out.println(ar+" "+tbllblSecId.get(ar).getValue());

				if(tblChkDes.get(ar).booleanValue()==true)
				{
					tbDesCheckId.get(ar).setValue(tbllblDesId.get(ar).getValue());
				}
				else
				{
					tbDesCheckId.get(ar).setValue("");
					designationIds = "";
				}

				String str = "";
				for(int i=0;i<tblChkDes.size();i++)
				{
					if(!tbDesCheckId.get(i).getValue().toString().isEmpty())
					{
						str = "'"+tbDesCheckId.get(i).getValue().toString()+"'"+","+str;	
						System.out.println(str);
					}
				}

				if(str != "")
				{
					int length = str.length();
					designationIds = str.substring(0, length-1);
					System.out.println("Designation ID's: "+designationIds);
				}
			}
		});


		tbllblDesId.add(ar, new Label("")); // Chechbox

		tbDesCheckId.add(ar, new Label("")); // ChechId with table column

		designationTable.addItem(new Object[]{tblLblDesName.get(ar),tblChkDes.get(ar)},ar);
	}

	private void designationTableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Findquery =" SELECT designationId,designationName from tbDesignationInfo order by " +
					"designationId ";
			List <?> list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tbllblDesId.get(i).setValue(element[0]);
					tblLblDesName.get(i).setValue(element[1]);

					if((i)==tblLblDesName.size()-1)
					{
						desTableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("designationTableDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void categoryTableInitialise()
	{
		for(int i=0;i<=0;i++)
		{
			categoryTableRowAdd(i);
		}
	}

	public void categoryTableRowAdd(final int ar)
	{
		tblLblCategoryName.add(ar, new Label(""));
		tblLblCategoryName.get(ar).setWidth("100%");
		tblLblCategoryName.get(ar).setImmediate(true);
		tblLblCategoryName.get(ar).setHeight("16px");

		tblChkCategory.add(ar, new CheckBox());
		tblChkCategory.get(ar).setWidth("100%");
		tblChkCategory.get(ar).setImmediate(true);
		tblChkCategory.get(ar).setHeight("16px");
		tblChkCategory.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				//				System.out.println(ar+" "+tbllblSecId.get(ar).getValue());

				if(tblChkCategory.get(ar).booleanValue()==true)
				{
					tbCategoryId.get(ar).setValue(tbllblCategory.get(ar).getValue());
				}
				else
				{
					tbCategoryId.get(ar).setValue("");
					categoryIds = "";
				}

				String str = "";
				for(int i=0;i<tblLblCategoryName.size();i++)
				{
					if(!tbCategoryId.get(i).getValue().toString().isEmpty())
					{
						str = "'"+tbCategoryId.get(i).getValue().toString()+"'"+","+str;	
						System.out.println(str);
					}
				}

				if(str != "")
				{
					int length = str.length();
					categoryIds = str.substring(0, length-1);
					System.out.println("Category ID's: "+categoryIds);
				}
			}
		});


		tbllblCategory.add(ar, new Label("")); // Chechbox

		tbCategoryId.add(ar, new Label("")); // ChechId with table column

		categoryTable.addItem(new Object[]{tblLblCategoryName.get(ar),tblChkCategory.get(ar)},ar);
	}

	private void categoryTableDataAdd()
	{
		for(int j=0; j<category.length; j++)
		{
			tblLblCategoryName.get(j).setValue(category[j]);
			tbllblCategory.get(j).setValue(category[j]);

			if((j)==tblLblCategoryName.size()-1)
			{
				categoryTableRowAdd(j+1);
			}
		}
	}

	public void religionTableInitialise()
	{
		for(int i=0;i<=0;i++)
		{
			religionTableRowAdd(i);
		}
	}

	public void religionTableRowAdd(final int ar)
	{
		tblLblReligionName.add(ar, new Label(""));
		tblLblReligionName.get(ar).setWidth("100%");
		tblLblReligionName.get(ar).setImmediate(true);
		tblLblReligionName.get(ar).setHeight("16px");

		tblChkReligion.add(ar, new CheckBox());
		tblChkReligion.get(ar).setWidth("100%");
		tblChkReligion.get(ar).setImmediate(true);
		tblChkReligion.get(ar).setHeight("16px");
		tblChkReligion.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				//				System.out.println(ar+" "+tbllblSecId.get(ar).getValue());
				String str = "";
				if(tblChkReligion.get(ar).booleanValue()==true)
				{
					tbReligionId.get(ar).setValue(tbllblReligion.get(ar).getValue());
				}
				else
				{
					tbReligionId.get(ar).setValue("");
					religionIDs = "";
				}

				for(int i=0;i<tblChkReligion.size();i++)
				{
					if(!tbReligionId.get(i).getValue().toString().isEmpty())
					{
						str = "'"+tbReligionId.get(i).getValue().toString()+"'"+","+str;	
						System.out.println(str);
					}
				}

				if(str != "")
				{
					int length = str.length();
					religionIDs = str.substring(0, length-1);
					System.out.println("Religion ID's: "+religionIDs);
				}
			}
		});


		tbllblReligion.add(ar, new Label("")); // Chechbox

		tbReligionId.add(ar, new Label("")); // ChechId with table column

		religionTable.addItem(new Object[]{tblLblReligionName.get(ar),tblChkReligion.get(ar)},ar);
	}

	public void bloodGroupTableInitialise()
	{
		for(int i=0;i<=0;i++)
		{
			bloodGroupTableRowAdd(i);
		}
	}

	public void bloodGroupTableRowAdd(final int ar)
	{
		tbLblBloodGroup.add(ar, new Label(""));
		tbLblBloodGroup.get(ar).setWidth("100%");
		tbLblBloodGroup.get(ar).setImmediate(true);
		tbLblBloodGroup.get(ar).setHeight("16px");

		tbChkBlood.add(ar, new CheckBox());
		tbChkBlood.get(ar).setWidth("100%");
		tbChkBlood.get(ar).setImmediate(true);
		tbChkBlood.get(ar).setHeight("16px");
		tbChkBlood.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				//				System.out.println(ar+" "+tbllblSecId.get(ar).getValue());
				String str = "";

				for(int i=0;i<tbChkBlood.size();i++)
				{
					if(!tbLblBloodGroup.get(i).getValue().toString().isEmpty())
					{
						str = "'"+tbLblBloodGroup.get(i).getValue().toString()+"'"+","+str;	
						System.out.println(str);
					}
				}

				if(str != "")
				{
					int length = str.length();
					bloodGroup = str.substring(0, length-1);
					System.out.println("Blood ID's: "+bloodGroup);
				}
			}
		});

		bloodGroupTable.addItem(new Object[]{tbLblBloodGroup.get(ar),tbChkBlood.get(ar)},ar);
	}

	private void bloodGroupTableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Findquery ="select distinct 0,vBloodGroup from tbEmployeeInfo where ISNULL(vBloodGroup,'')!='' and vDepartmentId!="+CHO+" order by vBloodGroup";
			List <?> list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tbLblBloodGroup.get(i).setValue(element[1]);

					if((i)==tbLblBloodGroup.size()-1)
					{
						bloodGroupTableRowAdd(i+1);
					}
					i++;
				}
			}
		}
		catch (Exception ex)
		{
			showNotification("bloodGroupTableDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void religionTableDataAdd()
	{
		for(int j=0; j<religion.length; j++)
		{
			tblLblReligionName.get(j).setValue(religion[j]);
			tbllblReligion.get(j).setValue(religion[j]);

			if((j)==tblLblReligionName.size()-1)
			{
				religionTableRowAdd(j+1);
			}
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("650px");
		setHeight("540px");

		lblDepartment = new Label("<html><font color='#74078B'> <b>Department List</b></font></html>",Label.CONTENT_XHTML);
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment,"top:20.0px; left:20.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setHeight("-1px");
		chkDepartmentAll.setWidth("-1px");
		mainLayout.addComponent(chkDepartmentAll, "top:20.0px; left:135.0px;");

		departmentTable.setWidth("260px");
		departmentTable.setHeight("150px");

		departmentTable.setImmediate(true); // react at once when something is selected
		departmentTable.setColumnReorderingAllowed(true);
		departmentTable.setColumnCollapsingAllowed(true);

		departmentTable.addContainerProperty("DEPARTMENT NAME", Label.class, new Label());
		departmentTable.setColumnWidth("DEPARTMENT NAME",178);

		departmentTable.addContainerProperty("SELECT", CheckBox.class, new CheckBox());
		departmentTable.setColumnWidth("SELECT",37);

		departmentTable.setColumnAlignments(new String[] { Table.ALIGN_LEFT, Table.ALIGN_CENTER});
		mainLayout.addComponent(departmentTable,"top:40.opx; left:20.0px");

		// Section table start
		lblSection = new Label("<html><font color='#74078B'> <b>Section List</b></font></html>",Label.CONTENT_XHTML);
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:20.0px; left:300.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		mainLayout.addComponent(chkSectionAll, "top:20.0px; left:390.0px;");

		sectionTable.setWidth("330px");
		sectionTable.setHeight("150px");

		sectionTable.setImmediate(true); // react at once when something is selected
		sectionTable.setColumnReorderingAllowed(true);
		sectionTable.setColumnCollapsingAllowed(true);

		sectionTable.addContainerProperty("SECTION NAME", Label.class, new Label());
		sectionTable.setColumnWidth("SECTION NAME",248);

		sectionTable.addContainerProperty("SELECT", CheckBox.class, new CheckBox());
		sectionTable.setColumnWidth("SELECT",37);

		sectionTable.setColumnAlignments(new String[] { Table.ALIGN_LEFT, Table.ALIGN_CENTER});
		mainLayout.addComponent(sectionTable,"top:40.opx; left:300.0px");
		// Section table end

		// Designation table start
		lblDesignation = new Label("<html><font color='#74078B'> <b>Designation List</b></font></html>",Label.CONTENT_XHTML);
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("100.0%");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation,"top:220.0px; left:20.0px;");

		chkDesignationAll = new CheckBox("All");
		chkDesignationAll.setImmediate(true);
		chkDesignationAll.setHeight("-1px");
		chkDesignationAll.setWidth("-1px");
		mainLayout.addComponent(chkDesignationAll, "top:220.0px; left:125.0px;");

		designationTable.setWidth("170px");
		designationTable.setHeight("150px");

		designationTable.setImmediate(true); // react at once when something is selected
		designationTable.setColumnReorderingAllowed(true);
		designationTable.setColumnCollapsingAllowed(true);

		designationTable.addContainerProperty("DESIGNATION", Label.class, new Label());
		designationTable.setColumnWidth("DESIGNATION",90);

		designationTable.addContainerProperty("SELECT", CheckBox.class, new CheckBox());
		designationTable.setColumnWidth("SELECT",34);

		sectionTable.setColumnAlignments(new String[] { Table.ALIGN_LEFT, Table.ALIGN_CENTER});
		mainLayout.addComponent(designationTable,"top:240.opx; left:20.0px");
		// Designation table end

		// Category table start
		lblCategory = new Label("<html><font color='#74078B'> <b>Category List</b></font></html>",Label.CONTENT_XHTML);
		lblCategory.setImmediate(false);
		lblCategory.setWidth("100.0%");
		lblCategory.setHeight("-1px");
		mainLayout.addComponent(lblCategory,"top:220.0px; left:205.0px;");

		chkCategoryAll = new CheckBox("All");
		chkCategoryAll.setImmediate(true);
		chkCategoryAll.setHeight("-1px");
		chkCategoryAll.setWidth("-1px");
		mainLayout.addComponent(chkCategoryAll, "top:220.0px; left:290.0px;");

		categoryTable.setWidth("140px");
		categoryTable.setHeight("150px");

		categoryTable.setImmediate(true); // react at once when something is selected
		categoryTable.setColumnReorderingAllowed(true);
		categoryTable.setColumnCollapsingAllowed(true);

		categoryTable.addContainerProperty("CATEGORY", Label.class, new Label());
		categoryTable.setColumnWidth("CATEGORY",75);

		categoryTable.addContainerProperty("SELECT", CheckBox.class, new CheckBox());
		categoryTable.setColumnWidth("SELECT",35);

		categoryTable.setColumnAlignments(new String[] { Table.ALIGN_LEFT, Table.ALIGN_CENTER});
		mainLayout.addComponent(categoryTable,"top:240.opx; left:205.0px");
		// Designation table end

		// Religion table start
		lblReligion = new Label("<html><font color='#74078B'> <b>Religion List</b></font></html>",Label.CONTENT_XHTML);
		lblReligion.setImmediate(false);
		lblReligion.setWidth("100.0%");
		lblReligion.setHeight("-1px");
		mainLayout.addComponent(lblReligion,"top:220.0px; left:360.0px;");

		chkReligionAll = new CheckBox("All");
		chkReligionAll.setImmediate(true);
		chkReligionAll.setHeight("-1px");
		chkReligionAll.setWidth("-1px");
		mainLayout.addComponent(chkReligionAll, "top:220.0px; left:435.0px;");

		religionTable.setWidth("120px");
		religionTable.setHeight("150px");

		religionTable.setImmediate(true); // react at once when something is selected
		religionTable.setColumnReorderingAllowed(true);
		religionTable.setColumnCollapsingAllowed(true);

		religionTable.addContainerProperty("RELIGION", Label.class, new Label());
		religionTable.setColumnWidth("RELIGION",55);

		religionTable.addContainerProperty("SELECT", CheckBox.class, new CheckBox());
		religionTable.setColumnWidth("SELECT",35);

		religionTable.setColumnAlignments(new String[] { Table.ALIGN_LEFT, Table.ALIGN_CENTER});
		mainLayout.addComponent(religionTable,"top:240.opx; left:360.0px");

		chkBloodGroup=new CheckBox("All");
		chkBloodGroup.setImmediate(true);
		chkBloodGroup.setWidth("-1px");
		chkBloodGroup.setHeight("-1px");
		mainLayout.addComponent(new Label("<html><font color='#74078B'> <b>Blood Group</b></font></html>",Label.CONTENT_XHTML), "top:220.0px; left:495.0px;");
		mainLayout.addComponent(chkBloodGroup, "top:220.0px; left:575.0px;");

		bloodGroupTable.setWidth("135.0px");
		bloodGroupTable.setHeight("150.0px");

		bloodGroupTable.setImmediate(true); // react at once when something is selected
		bloodGroupTable.setColumnReorderingAllowed(true);
		bloodGroupTable.setColumnCollapsingAllowed(true);

		bloodGroupTable.addContainerProperty("Bl. Group", Label.class, new Label());
		bloodGroupTable.setColumnWidth("Bl. Group",55);

		bloodGroupTable.addContainerProperty("SELECT", CheckBox.class, new CheckBox());
		bloodGroupTable.setColumnWidth("SELECT",34);

		bloodGroupTable.setColumnAlignments(new String[] { Table.ALIGN_LEFT, Table.ALIGN_CENTER});
		mainLayout.addComponent(bloodGroupTable,"top:240.opx; left:495.0px");

		chkPhysicallyDisabled=new CheckBox("All");
		chkPhysicallyDisabled.setImmediate(true);
		mainLayout.addComponent(new Label("<html><font color='#740788'><b>Physically Disabled</b></font></html>",Label.CONTENT_XHTML), "top:405.0px; left:20.0px;");
		mainLayout.addComponent(chkPhysicallyDisabled, "top:405.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:435.0px;left:280.0px;");
		mainLayout.addComponent(cButton,"bottom:10.0px; left:245.0px");
		return mainLayout;
	}
}
