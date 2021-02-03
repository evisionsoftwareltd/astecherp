package acc.appform.hrmModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

//import com.example.general.Department;
//import com.example.general.Designation;
/*import com.example.general.Floor;
import com.example.general.Grade;
import com.example.general.Line;*/
//import com.example.general.Section;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExampleConfirmation;
import com.common.share.ImmediateUploadExampleJoining;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Window;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class EmployeeInformation extends Window
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","","","","","Exit");

	private AbsoluteLayout mainLayout;
	private ComboBox cmbDepartmentName;
	private ComboBox cmbSectionName;
	private ComboBox cmbEmployeeName;
	private Label lblFind;
	private OptionGroup RadioBtnGroup;

	String imageLoc = "0" ;
	String birthImageLoc = "0" ;
	String tinNoImageLoc = "0";
	String circleImageLoc = "0";
	String zoneImageLoc = "0";
	String nidImageLoc = "0" ;
	String applicationImageLoc = "0" ;
	String joinImageLoc = "0" ;
	String conImageLoc = "0" ;
	//String employeeImages = "0";


	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateYear = new SimpleDateFormat("yyyy");
	private static final List<String> type2 = Arrays.asList(new String[] {"Employee ID","Proximity ID","Name"});

	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	
	TabSheet tabSheet=new TabSheet();
	TabOfficialInfo firstTab;
	TabPersonalInformation secondTab=new TabPersonalInformation();
	TabEducation thirdTab=new TabEducation();
	TabExperience fourthTab=new TabExperience();
	TabSalaryStructure fifthTab=new TabSalaryStructure();

	boolean isEdit = false;
	boolean isFind = false;
	boolean t = false;

	ArrayList<Component> allComp = new ArrayList<Component>();

	public ArrayList<TextField> tblTxtExam = new ArrayList<TextField>();
	public ArrayList<TextField> tblTxtGroup = new ArrayList<TextField>();
	public ArrayList<TextField> tblTxtInstitute = new ArrayList<TextField>();
	public ArrayList<TextField> tblTxtBoard = new ArrayList<TextField>();
	public ArrayList<TextField> tblTxtDivision = new ArrayList<TextField>();
	public ArrayList<InlineDateField> tblDateYear = new ArrayList<InlineDateField>();

	public DecimalFormat df = new DecimalFormat("#0.00"); 
	int OtStatus=0;
	SessionBean sessionBean;
	private String employeeCode = "";
	

	private FileWriter log;

	public EmployeeInformation(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("EMPLOYEE INFORMATION :: "+sessionBean.getCompany());
		

		firstTab = new TabOfficialInfo(sessionBean);

		//employeeImages = "D:/Tomcat 7.0/webapps/report/"+sessionBean.getContextName()+"/employee/";
		
		addCmp();
		buildMainLayout();
		btnInit(true);
		tabInit(false);
		addEmployeeDepartmentData();
		addEmployeeSectionData("%");
		addCmbEmployeeData();
		addCmbDepartmentData();
		addCmbSectionData("%");
		addCmbDesignationData();
		addCmbShiftData();
		addCmbGradeData();
		setBtnAction();
		setContent(mainLayout);
		authencationCheck();
		button.btnNew.focus();

		focusMove();
	}

	private void addEmployeeDepartmentData()
	{
		cmbDepartmentName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ein.vDepartmentId, dept.vDepartmentName from tbEmployeeInfo ein inner join " +
					"tbDepartmentInfo dept on ein.vDepartmentId=dept.vDepartmentId where iStatus=1 and " +
					"ISNULL(ein.vProximityID,'')!='' and vDepartmentName!= 'CHO' order by dept.vDepartmentName";
			
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object [])itr.next();
					cmbDepartmentName.addItem(element[0]);
					cmbDepartmentName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("addEmployeeDepartmentData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void addEmployeeSectionData(String departmentID)
	{
		cmbSectionName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ein.vSectionId, si.SectionName from tbEmployeeInfo ein inner join " +
					"tbSectionInfo si on ein.vSectionId=si.vSectionID where ein.vDepartmentID like '"+departmentID+"' " +
					"and iStatus=1 and ISNULL(ein.vProximityID,'')!='' and SectionName!= 'CHO' order by si.SectionName";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object [])itr.next();
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("addEmployeeSectionData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	private void updateBntEvent()
	{
		isEdit = true;
		isFind=true;
	
		btnInit(false);
		tabInit(true);
		
		if (sessionBean.isAdmin()) {
			
			button.btnSave.setEnabled(true);
						
		//	fifthTab.txtGross.setReadOnly(false);
			fifthTab.txtGross.setEnabled(true);
			
			fifthTab.txtBasic.setReadOnly(false);
			fifthTab.txtBasic.setEnabled(true);
			
			fifthTab.txtHouseRent.setReadOnly(false);
			fifthTab.txtHouseRent.setEnabled(true);
			
			fifthTab.txtCon.setReadOnly(false);
			fifthTab.txtCon.setEnabled(true);
			
			fifthTab.txtAttBonus.setReadOnly(false);
			fifthTab.txtAttBonus.setEnabled(true);
			
			fifthTab.txtFridayLunch.setReadOnly(false);
			fifthTab.txtFridayLunch.setEnabled(true);
			
			fifthTab.txtMedical.setReadOnly(false);
			fifthTab.txtMedical.setEnabled(true);
			
			fifthTab.txtProvidentFund.setReadOnly(false);
			fifthTab.txtProvidentFund.setEnabled(true);
			
			fifthTab.txtTax.setReadOnly(false);
			fifthTab.txtTax.setEnabled(true);
			
			fifthTab.txtInsurance.setReadOnly(false);
			fifthTab.txtInsurance.setEnabled(true);
			
			fifthTab.txtDearnessAllowance.setReadOnly(false);
			fifthTab.txtDearnessAllowance.setEnabled(true);
			
			fifthTab.txtFireAllowance.setReadOnly(false);
			fifthTab.txtFireAllowance.setEnabled(true);						
			
			fifthTab.opgBank.setEnabled(true);
			
			fifthTab.cmbBankName.setEnabled(true);
			fifthTab.cmbBranchName.setEnabled(true);
						
			fifthTab.txtAccountNo.setReadOnly(false);
			fifthTab.txtAccountNo.setEnabled(true);

			firstTab.btnJoiningDate.setEnabled(true);
			firstTab.btnJoinPreview.setEnabled(true);
			firstTab.dJoiningDate.setEnabled(true);
			
			firstTab.btnConfirmdate.setEnabled(true);
			firstTab.btnConPreview.setEnabled(true);
			firstTab.dConDate.setEnabled(true);
			
	

		}
		else
		{
			
		//	fifthTab.txtGross.setReadOnly(true);
			fifthTab.txtGross.setEnabled(false);
			
			fifthTab.txtBasic.setReadOnly(true);
			fifthTab.txtBasic.setEnabled(true);
			
			fifthTab.txtHouseRent.setReadOnly(true);
			fifthTab.txtHouseRent.setEnabled(true);
			
			fifthTab.txtCon.setReadOnly(true);
			fifthTab.txtCon.setEnabled(true);
			
			fifthTab.txtAttBonus.setReadOnly(true);
			fifthTab.txtAttBonus.setEnabled(true);
			
			fifthTab.txtFridayLunch.setReadOnly(true);
			fifthTab.txtFridayLunch.setEnabled(true);
			
			fifthTab.txtMedical.setReadOnly(true);
			fifthTab.txtMedical.setEnabled(true);
			
			fifthTab.txtProvidentFund.setReadOnly(true);
			fifthTab.txtProvidentFund.setEnabled(true);
			
			fifthTab.txtTax.setReadOnly(true);
			fifthTab.txtTax.setEnabled(true);
			
			fifthTab.txtInsurance.setReadOnly(true);
			fifthTab.txtInsurance.setEnabled(true);
			
			fifthTab.txtDearnessAllowance.setReadOnly(true);
			fifthTab.txtDearnessAllowance.setEnabled(true);
			
			fifthTab.txtFireAllowance.setReadOnly(true);
			fifthTab.txtFireAllowance.setEnabled(true);					
			
			fifthTab.opgBank.setEnabled(false);
			fifthTab.cmbBranchName.setEnabled(false);
			fifthTab.cmbBankName.setEnabled(false);
			
			fifthTab.txtAccountNo.setReadOnly(true);
			fifthTab.txtAccountNo.setEnabled(true);
			
			firstTab.btnJoiningDate.setEnabled(false);
			firstTab.btnJoinPreview.setEnabled(false);
			firstTab.dJoiningDate.setEnabled(false);
		
			firstTab.btnConfirmdate.setEnabled(false);
			firstTab.btnConPreview.setEnabled(false);
			firstTab.dConDate.setEnabled(false);

			
			//button.btnSave.setEnabled(true);
		}
	
	}


	private void authencationCheck()
	{
		if(sessionBean.isSubmitable())
		{
			button.btnSave.setVisible(true);
		}
		else
		{
			button.btnSave.setVisible(false);
		}
		if(sessionBean.isUpdateable())
		{
			button.btnEdit.setVisible(true);
		}
		else
		{
			button.btnEdit.setVisible(false);
		}
		if(sessionBean.isDeleteable())
		{
			button.btnDelete.setVisible(true);
		}
		else
		{
			button.btnDelete.setVisible(false);
		}
	}

	private void btnInit(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		//button.btnExit.setEnabled(t);
	}

	private void addCmp()
	{
		tabSheet.addTab(firstTab,"Official Information",new ThemeResource("../icons/official_info.png"));
		tabSheet.addTab(secondTab,"Personal Information",new ThemeResource("../icons/personal_information.png"));
		tabSheet.addTab(thirdTab,"Education",new ThemeResource("../icons/education.png"));
		tabSheet.addTab(fourthTab,"Experience",new ThemeResource("../icons/experience2.png"));
		tabSheet.addTab(fifthTab,"Salary Structure",new ThemeResource("../icons/Salary_Stucture1.png"));
	}

	private void addCmbEmployeeData()
	{
		String status="%";
		if(RadioBtnStatus.getValue().equals("Active"))
		{
			status="1";
		}
		else if(RadioBtnStatus.getValue().equals("Left"))
		{
			status="0";
		}
		else
		{
			status="%";
		}
		String query="";
		if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Employee ID"))
			query = "select vEmployeeId,employeeCode from tbEmployeeInfo where vDepartmentID " +
					"like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue():"%")+"' " +
					"and vSectionID like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue():"%")+"' " +
					" and iStatus like '"+status+"'  order by employeeCode";

		else if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Proximity ID"))
			query = "select vEmployeeId,vProximityID from tbEmployeeInfo where vDepartmentID " +
					"like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue():"%")+"' " +
					"and vSectionID like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue():"%")+"' " +
					" and iStatus like '"+status+"' order by employeeCode";

		else if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Name"))
			query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo where vDepartmentID " +
					"like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue():"%")+"' " +
					"and vSectionID like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue():"%")+"' " +
					" and iStatus like '"+status+"' order by employeeCode";

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbEmployeeName.addItem(element[0].toString());
				cmbEmployeeName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("addCmbEmployeeData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void setBtnAction()
	{
		cmbDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.setValue(null);
				cmbEmployeeName.removeAllItems();

				if(cmbDepartmentName.getValue()!=null)
				{
					addEmployeeSectionData(cmbDepartmentName.getValue().toString().trim());
					addCmbEmployeeData();
				}
				else
				{
					addEmployeeSectionData("%");
					addCmbEmployeeData();
				}
			}
		});
		
		cmbSectionName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				
				addCmbEmployeeData();

			}
		});
		
		RadioBtnStatus.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event)
			{ 
				cmbEmployeeName.removeAllItems();
				addCmbEmployeeData();
			}
		});

		firstTab.cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(firstTab.cmbDepartment.getValue()!=null)
				{
					addCmbSectionData(firstTab.cmbDepartment.getValue().toString());
				}
			}
		});

		/*firstTab.txtEmpName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!firstTab.txtEmpName.getValue().toString().isEmpty() && cmbEmployeeName.getValue()==null)
				{
					Session session=SessionFactoryUtil.getInstance().openSession();
					session.beginTransaction();
					try
					{
						String chkvalue="select count(vEmployeeName) countEmp from tbEmployeeInfo where " +
								"vEmployeeName='"+firstTab.txtEmpName.getValue().toString()+"'";

						Iterator <?> itr=session.createSQLQuery(chkvalue).list().iterator();
						String count=itr.next().toString();
						if(Integer.parseInt(count)>0)
						{
							showNotification("Warning", firstTab.txtEmpName.getValue().toString()+" is already exist!!!", Notification.TYPE_WARNING_MESSAGE);
							firstTab.txtEmpName.setValue(firstTab.txtEmpName.getValue().toString()+count);
						}
					}
					catch (Exception exp)
					{
						showNotification("txtEmpName.addListener", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
					}
					finally
					{
						session.close();
					}
				}
			}
		});*/

		firstTab.txtProximityId.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!firstTab.txtProximityId.getValue().toString().isEmpty() && cmbEmployeeName.getValue()!=null)
				{
					Session session=SessionFactoryUtil.getInstance().openSession();
					session.beginTransaction();
					try
					{
						String chkvalue="select vProximityId from tbEmployeeInfo where vProximityId='"+firstTab.txtProximityId.getValue().toString()+"' and iStatus = 1";
						List <?> lst=session.createSQLQuery(chkvalue).list();
						if(!lst.isEmpty() && !isFind)
						{
							firstTab.txtProximityId.setValue("");
							showNotification("Warning","Proximity ID already exist!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					catch (Exception exp)
					{
						showNotification("txtProximityId.addListener", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
					}
					finally
					{
						session.close();
					}
				}
			}
		});

		firstTab.chkOtEnable.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(firstTab.chkOtEnable.booleanValue())
				{
					firstTab.chkFriEnable.setValue(false);
					fifthTab.txtFridayLunch.setEnabled(false);
					fifthTab.txtFridayLunch.setValue("0.0");
					OtStatus=1;
				}
				else
				{
					firstTab.chkFriEnable.setValue(true);
					fifthTab.txtFridayLunch.setEnabled(true);
					fifthTab.txtFridayLunch.setValue("75.0");
					OtStatus=0;
				}
			}
		});

		firstTab.chkFriEnable.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(firstTab.chkFriEnable.booleanValue())
				{
					firstTab.chkOtEnable.setValue(false);
					fifthTab.txtFridayLunch.setEnabled(true);
					fifthTab.txtFridayLunch.setValue("75.0");
					OtStatus=0;
				}
				else
				{
					fifthTab.txtFridayLunch.setEnabled(false);
					firstTab.chkOtEnable.setValue(true);
					fifthTab.txtFridayLunch.setValue("0.0");
					OtStatus=1;
				}
			}
		});
		
//		for admin gross effect in edit time(Read only false) and un admin user for gross effect different (Read only true)
		
	if (sessionBean.isAdmin()) {
		
		
		fifthTab.txtGross.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(firstTab.cmbEmpType.getValue()!=null)
				{
					if(firstTab.cmbEmpType.getValue().toString().equalsIgnoreCase("Casual") || firstTab.cmbEmpType.getValue().toString().equalsIgnoreCase("Temporary"))
					{
						
/*						//for first enable false
 						
						fifthTab.txtBasic.setReadOnly(false);
						fifthTab.txtHouseRent.setReadOnly(false);
						fifthTab.txtMedical.setReadOnly(false);
						fifthTab.txtCon.setReadOnly(false);
						fifthTab.txtAttBonus.setReadOnly(false);
						fifthTab.txtFridayLunch.setReadOnly(false);
						fifthTab.txtProvidentFund.setReadOnly(false);
*/						
						
						fifthTab.txtGross.setEnabled(true);	
						
						fifthTab.txtBasic.setValue(0.0);
						fifthTab.txtHouseRent.setValue(0.0);
						fifthTab.txtMedical.setValue(0.0);
						fifthTab.txtCon.setValue(0.0);
						fifthTab.txtAttBonus.setValue("200.0");						
						fifthTab.txtFridayLunch.setEnabled(true);
						
						}
					else
					{
						if(Double.parseDouble(fifthTab.txtGross.getValue().toString().replaceAll(",", "")+"0")>0)
						{
							double gross = Double.parseDouble(fifthTab.txtGross.getValue().toString().replaceAll(",", ""));

							fifthTab.txtBasic.setValue(new  CommaSeparator().setComma(60*(gross)/100));
							fifthTab.txtHouseRent.setValue(new CommaSeparator().setComma(25*(gross)/100));
							fifthTab.txtMedical.setValue(new CommaSeparator().setComma(7.5*(gross)/100));
							fifthTab.txtCon.setValue(new CommaSeparator().setComma(7.5*(gross)/100));
							fifthTab.txtAttBonus.setValue("200.0");
							@SuppressWarnings("unused")
							double AtBonus = Double.parseDouble(fifthTab.txtAttBonus.getValue().toString().replaceAll(",", "").isEmpty()?"0.0":fifthTab.txtAttBonus.getValue().toString().replaceAll(",", ""));
						
							if(cmbEmployeeName.getValue()==null)
							{
								

			
								/*				
								fifthTab.txtBasic.setReadOnly(true);
								fifthTab.txtHouseRent.setReadOnly(true);
								fifthTab.txtMedical.setReadOnly(true);
								fifthTab.txtCon.setReadOnly(true);
								fifthTab.txtAttBonus.setReadOnly(true);
								fifthTab.txtFridayLunch.setReadOnly(true);
								fifthTab.txtProvidentFund.setReadOnly(true);	
						*/
							}
	
						}
					}
				}
			}
		});
	
		}
		else{
			
			fifthTab.txtGross.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(firstTab.cmbEmpType.getValue()!=null)
					{
						if(firstTab.cmbEmpType.getValue().toString().equalsIgnoreCase("Casual") || firstTab.cmbEmpType.getValue().toString().equalsIgnoreCase("Temporary"))
						{
							
							fifthTab.txtGross.setEnabled(true);			
							
							fifthTab.txtBasic.setValue(0.0);
							fifthTab.txtHouseRent.setValue(0.0);
							fifthTab.txtMedical.setValue(0.0);
							fifthTab.txtCon.setValue(0.0);
							fifthTab.txtAttBonus.setValue("200.0");
							
							}
						else
						{
							if(Double.parseDouble(fifthTab.txtGross.getValue().toString().replaceAll(",", "")+"0")>0)
							{
								double gross = Double.parseDouble(fifthTab.txtGross.getValue().toString().replaceAll(",", ""));

								fifthTab.txtBasic.setValue(new  CommaSeparator().setComma(60*(gross)/100));
								fifthTab.txtHouseRent.setValue(new CommaSeparator().setComma(25*(gross)/100));
								fifthTab.txtMedical.setValue(new CommaSeparator().setComma(7.5*(gross)/100));
								fifthTab.txtCon.setValue(new CommaSeparator().setComma(7.5*(gross)/100));
								fifthTab.txtAttBonus.setValue("200.0");
								@SuppressWarnings("unused")
								double AtBonus = Double.parseDouble(fifthTab.txtAttBonus.getValue().toString().replaceAll(",", "").isEmpty()?"0.0":fifthTab.txtAttBonus.getValue().toString().replaceAll(",", ""));
							
								if(cmbEmployeeName.getValue()==null)
								{
		
									
									fifthTab.txtGross.setEnabled(false);			
									
									fifthTab.txtBasic.setReadOnly(true);
									fifthTab.txtHouseRent.setReadOnly(true);
									fifthTab.txtMedical.setReadOnly(true);
									fifthTab.txtCon.setReadOnly(true);
									fifthTab.txtAttBonus.setReadOnly(true);
									fifthTab.txtFridayLunch.setReadOnly(true);
									fifthTab.txtProvidentFund.setReadOnly(true);	
							
									fifthTab.txtFridayLunch.setEnabled(true);
								}
		
							}
						}
					}
				}
			});

		}
		
		
		
		firstTab.cmbStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(firstTab.cmbStatus.getValue()!=null)	
				{	
					if(!firstTab.cmbStatus.getValue().toString().equalsIgnoreCase("Continue"))	
					{
						firstTab.lblDateName.setCaption(firstTab.cmbStatus.getValue().toString());
						firstTab.dDate.setEnabled(true);
					}
					else
					{
						firstTab.lblDateName.setCaption(""); 
						firstTab.dDate.setEnabled(false);
					}
				}
			}
		});
		firstTab.chkOtEnable.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(firstTab.chkOtEnable.booleanValue()==true)
				{
					OtStatus=1;
				}
				else
				{
					OtStatus=0;
				}
			}
		});

		firstTab.txtFingerId.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!firstTab.txtFingerId.getValue().toString().isEmpty())
				{
					firstTab.txtProximityId.setEnabled(false);
				}
				else
				{
					firstTab.txtProximityId.setEnabled(true);
				}
			}
		});

		firstTab.txtProximityId.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!firstTab.txtProximityId.getValue().toString().isEmpty())
				{
					firstTab.txtFingerId.setEnabled(false);
				}
				else
				{
					firstTab.txtFingerId.setEnabled(true);
				}
			}
		});


		button.btnNew.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				cmbDepartmentName.setValue(null);
				cmbSectionName.setValue(null);
				cmbEmployeeName.setValue(null);
				fifthTab.opgBank.setValue("Bank A/C");
				textClear();
				btnInit(false);
				tabInit(true);
				firstTab.chkOtEnable.setValue(true);
				isEdit = false;
				isFind = false;
				imageLoc = "0";
				birthImageLoc = "0";
				birthImageLoc = "0" ;
				tinNoImageLoc = "0";
				circleImageLoc = "0";
				nidImageLoc = "0";
				applicationImageLoc = "0";
				joinImageLoc = "0";
				conImageLoc = "0";
	
				/////newbutton te open setEnable 
		
			fifthTab.opgBank.setEnabled(true);
			fifthTab.cmbBankName.setEnabled(true);
			fifthTab.cmbBranchName.setEnabled(true);
			fifthTab.txtGross.setEnabled(true);
			fifthTab.txtFridayLunch.setEnabled(true);
			
			firstTab.btnJoiningDate.setEnabled(true);
			firstTab.btnJoinPreview.setEnabled(true);
			firstTab.dJoiningDate.setEnabled(true);
		
			firstTab.btnConfirmdate.setEnabled(true);
			firstTab.btnConPreview.setEnabled(true);
			firstTab.dConDate.setEnabled(true);

			}
		});

		button.btnSave.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!firstTab.txtEmployeeCode.getValue().toString().isEmpty())
				{
					if(!firstTab.txtFingerId.getValue().toString().isEmpty() || !firstTab.txtProximityId.getValue().toString().isEmpty())
					{
						if(!firstTab.txtEmpName.getValue().toString().isEmpty())
						{
							if(firstTab.cmbReligion.getValue()!= null)
							{
								if(!firstTab.txtContact.getValue().toString().isEmpty())
								{
									if(firstTab.cmbGender.getValue()!= null)
									{
										if(!firstTab.txtNationality.getValue().toString().isEmpty())
										{
											if(firstTab.cmbEmpType.getValue()!=null)
											{
												if(firstTab.cmbSerType.getValue()!=null)
												{
													if(firstTab.cmbStatus.getValue()!= null)
													{
														if(firstTab.cmbDepartment.getValue()!=null)
														{
															if(firstTab.cmbSection.getValue()!= null)
															{
																if(firstTab.cmbDesignation.getValue()!=null)
																{
																	
																
																		if(!fifthTab.txtBasic.getValue().toString().equals(""))
																		{
																			isFind = false;
																			SaveButtonAction();
																		}
																		else
																		{
																			showNotification("Please Provide Daily Wages at Salary Structure.",Notification.TYPE_WARNING_MESSAGE);
																		}
																
																	
																}
																else
																{
																	showNotification("Please Provide Designation at Official Information.",Notification.TYPE_WARNING_MESSAGE);
																}

															}
															else
															{
																showNotification("Please Provide Section at Official Information.",Notification.TYPE_WARNING_MESSAGE);
															}
														}
														else
														{
															showNotification("Please Provide Department at Official Information.",Notification.TYPE_WARNING_MESSAGE);
														}
													}
													else
													{
														showNotification("Please Provide Status at Official Information.",Notification.TYPE_WARNING_MESSAGE);
													}
												}
												else
												{
													showNotification("Please Provide Service Status at Official Information.",Notification.TYPE_WARNING_MESSAGE);
												}
											}
											else
											{
												showNotification("Please Provide Service Type at Official Information.",Notification.TYPE_WARNING_MESSAGE);
											}
										}
										else
										{
											showNotification("Please Provide Nationality at Official Information.",Notification.TYPE_WARNING_MESSAGE);
										}
									}
									else
									{
										showNotification("Please Provide Gender at Official Information.",Notification.TYPE_WARNING_MESSAGE);
									}
								}
								else
								{
									showNotification("Please Provide Contact at Official Information.",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Please Provide Religion at Official Information.",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Please Provide Employee Name at Official Information.",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Please Provide Finger ID or  Proximity Id at Official Information.",Notification.TYPE_WARNING_MESSAGE);
					}
				}

				else
				{
					showNotification("Please Select Employee Code at Official Information.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnRefresh.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				cmbDepartmentName.setValue(null);
				cmbSectionName.setValue(null);
				cmbEmployeeName.setValue(null);
				fifthTab.opgBank.setValue("Bank A/C");
				   textClear();
			       btnInit(true);
				   tabInit(false);
				firstTab.chkOtEnable.setValue(false);
				RadioBtnGroup.setEnabled(true);
				cmbDepartmentName.setEnabled(true);
				cmbSectionName.setEnabled(true);
				cmbEmployeeName.setEnabled(true);			
				fifthTab.txtGross.setEnabled(true);
				fifthTab.txtAccountNo.setEnabled(true);
				
				addCmbEmployeeData();
				isEdit = false;
				isFind = false;
			}
		});

		button.btnExit.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				close();
			}
		});

		
		
		
		button.btnEdit.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isEdit = true;
				updateBntEvent();
				if(cmbEmployeeName.getValue()!=null)
				{
					btnInit(false);
					tabInit(true);
					RadioBtnGroup.setEnabled(false);
					cmbDepartmentName.setEnabled(false);
					cmbSectionName.setEnabled(false);
					cmbEmployeeName.setEnabled(false);
				
				
			
			    /*fifthTab.txtGross.setEnabled(false);						   	 
				fifthTab.cmbBankName.setEnabled(false);				
			 	fifthTab.txtAccountNo.setEnabled(false);				
				firstTab.btnJoiningDate.setEnabled(true);
				firstTab.dJoiningDate.setEnabled(true);
				firstTab.btnJoinPreview.setEnabled(true);
			    firstTab.btnConfirmdate.setEnabled(true);
				firstTab.dConDate.setEnabled(false);
				firstTab.btnConPreview.setEnabled(false);*/
					
                  	isFind = false;
                	
					
				}
				else
				{
					showNotification("Please Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
				}
				
				
			}
		});

		button.btnDelete.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbEmployeeName.getValue()!=null)
				{
					MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Delelete information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
					mb.show(new EventListener()
					{
						public void buttonClicked(ButtonType buttonType)
						{
							if(buttonType == ButtonType.YES)
							{
								deleteAllData();
								cmbDepartmentName.setValue(null);
								cmbSectionName.setValue(null);
								cmbEmployeeName.setValue(null);
								textClear();
								addCmbEmployeeData();
								RadioBtnGroup.setEnabled(true);
								cmbDepartmentName.setEnabled(true);
								cmbSectionName.setEnabled(true);
								cmbEmployeeName.setEnabled(true);
								tabInit(false);
								btnInit(true);
								isEdit=false;
								isFind = false;
								showNotification("Data Deleted Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
							}
						}
					});
				}
				else
				{
					showNotification("Please Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		RadioBtnGroup.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				cmbEmployeeName.removeAllItems();
				addCmbEmployeeData();
			}
		});

		cmbEmployeeName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				textClear();
				if(cmbEmployeeName.getValue()!=null)
				{
					isFind = true;

					selectEmployeeInfo(RadioBtnGroup.getValue().toString());
					
/*					fifthTab.txtBasic.setReadOnly(true);
					fifthTab.txtHouseRent.setReadOnly(true);
					fifthTab.txtMedical.setReadOnly(true);
					fifthTab.txtCon.setReadOnly(true);
					fifthTab.txtAttBonus.setReadOnly(true);
					fifthTab.txtFridayLunch.setReadOnly(true);				
					fifthTab.txtProvidentFund.setReadOnly(true);
				    fifthTab.txtTax.setReadOnly(true);
					fifthTab.txtInsurance.setReadOnly(true);
					fifthTab.txtDearnessAllowance.setReadOnly(true);
					fifthTab.txtFireAllowance.setReadOnly(true);*/					
				//	fifthTab.opgBank.setReadOnly(true);
					
/*					fifthTab.cmbBankName.setReadOnly(true);
					fifthTab.cmbBranchName.setReadOnly(true);
					fifthTab.txtAccountNo.setReadOnly(true);
*/					
					if(birthImageLoc.equals("0"))
					{
						firstTab.btnBirthPreview.setCaption(".jpg/.pdf");
					}
					else
					{
						firstTab.btnBirthPreview.setCaption("Preview");
					}

					if(tinNoImageLoc.equals("0"))
					{
						firstTab.btnTINNOPreview.setCaption(".jpg/.pdf");
					}
					else
					{
						firstTab.btnTINNOPreview.setCaption("Preview");
					}

					if(circleImageLoc.equals("0"))
					{
						firstTab.btnCirclePreview.setCaption(".jpg/.pdf");
					}
					else
					{
						firstTab.btnCirclePreview.setCaption("Preview");
					}

					if(zoneImageLoc.equals("0"))
					{
						firstTab.btnZonePreview.setCaption(".jpg/.pdf");
					}
					else
					{
						firstTab.btnZonePreview.setCaption("Preview");
					}

					if(nidImageLoc.equals("0"))
					{
						firstTab.btnNidPreview.setCaption(".jpg/.pdf");
					}
					else
					{
						firstTab.btnNidPreview.setCaption("Preview");
					}

					if(applicationImageLoc.equals("0"))
					{
 					}
					else
					{
						firstTab.btnApplicationPreview.setCaption("Preview");
					}

					if(joinImageLoc.equals("0"))
					{
						firstTab.btnJoinPreview.setCaption(".jpg/pdf");
					}
					else
					{
						firstTab.btnJoinPreview.setCaption("Preview");
					}

					if(conImageLoc.equals("0"))
					{
						firstTab.btnConPreview.setCaption(".jpg/.pdf");
					}
					else
					{
						firstTab.btnConPreview.setCaption("Preview");
					}
				}
			}
		});

		firstTab.btnDepartment.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				DepartmentLink();
			}
		});
		firstTab.btnGrades.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				GradesLink();
			}
		});

		firstTab.btnSection.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				sectionLink();
			}
		});

		firstTab.btnDesignation.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				designationLink();
			}
		});

		firstTab.txtEmployeeCode.addListener(new ValueChangeListener() 
		{	
			public void valueChange(ValueChangeEvent event) 
			{
				if(!isFind)
				{
					if(!firstTab.txtEmployeeCode.getValue().toString().isEmpty())
					{
						idCheck();
					}	
				}
			}
		});

		fifthTab.txtAccountNo.addListener(new ValueChangeListener() 
		{	
			public void valueChange(ValueChangeEvent event) 
			{
				if(!isFind)
				{
					if(!fifthTab.txtAccountNo.getValue().toString().isEmpty())
					{
						duplicateAccountNo();
					}	
				}
			}
		});

		firstTab.btnBirthPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isEdit)
				{
					if(!firstTab.btnDateofBirth.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnDateofBirth.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll("ESLDM", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.birthFilePathTmp;
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
				if(isEdit)
				{
					if(!firstTab.btnDateofBirth.actionCheck)
					{
						if(!birthImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								link = link.replaceAll("uptd/", birthImageLoc.substring(22, birthImageLoc.length()));
							}
							else if(link.endsWith("MSML/"))
							{
								link = link.replaceAll("MSML/", birthImageLoc.substring(22, birthImageLoc.length()));
							}
							else if(link.endsWith("RJSL/"))
							{
								link = link.replaceAll("RJSL/", birthImageLoc.substring(22, birthImageLoc.length()));
							}
							else if(link.endsWith("UNIGLOBAL/"))
							{
								link = link.replaceAll("UNIGLOBAL/", birthImageLoc.substring(27, birthImageLoc.length()));
							}
							else if(link.endsWith("ESLDM/"))
							{
								link = link.replaceAll("ESLDM/", birthImageLoc.substring(22, birthImageLoc.length()));
							}
							else if(link.endsWith("RSRIL/"))
							{
								link = link.replaceAll("RSRIL/", birthImageLoc.substring(22, birthImageLoc.length()));
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
					if(firstTab.btnDateofBirth.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll("ESLDM", "report")+firstTab.birthFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.birthFilePathTmp;
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
		});

		firstTab.btnTINNOPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isEdit)
				{
					if(!firstTab.btnTINNO.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnTINNO.actionCheck)
					{
						String link = getApplication().getURL().toString();
						link = link.replaceAll("astecherp", "report")+firstTab.tINNoFilePathTmp;
						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
				if(isEdit)
				{
					if(!firstTab.btnTINNO.actionCheck)
					{
						if(!tinNoImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("astecherp/"))
							{
								link = link.replaceAll("astecherp/", tinNoImageLoc.substring(22, tinNoImageLoc.length()));
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
					if(firstTab.btnTINNO.actionCheck)
					{
						String link = getApplication().getURL().toString();
						link = link.replaceAll("astecherp", "report")+firstTab.tINNoFilePathTmp;
						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
			}
		});

		firstTab.btnCirclePreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isEdit)
				{
					if(!firstTab.btnCircleNO.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnCircleNO.actionCheck)
					{
						String link = getApplication().getURL().toString();
						link = link.replaceAll("astecherp", "report")+firstTab.CircleFilePathTmp;
						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
				if(isEdit)
				{
					if(!firstTab.btnTINNO.actionCheck)
					{
						if(!circleImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("astecherp/"))
							{
								link = link.replaceAll("astecherp/", circleImageLoc.substring(22, circleImageLoc.length()));
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
					if(firstTab.btnCircleNO.actionCheck)
					{
						String link = getApplication().getURL().toString();
						link = link.replaceAll("astecherp", "report")+firstTab.CircleFilePathTmp;
						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
			}
		});

		firstTab.btnZonePreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isEdit)
				{
					if(!firstTab.btnZoneNO.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnZoneNO.actionCheck)
					{
						String link = getApplication().getURL().toString();
						link = link.replaceAll("astecherp", "report")+firstTab.ZoneFilePathTmp;
						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
				if(isEdit)
				{
					if(!firstTab.btnZoneNO.actionCheck)
					{
						if(!zoneImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("astecherp/"))
							{
								link = link.replaceAll("astecherp/", zoneImageLoc.substring(22, zoneImageLoc.length()));
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
					if(firstTab.btnZoneNO.actionCheck)
					{
						String link = getApplication().getURL().toString();
						link = link.replaceAll("astecherp", "report")+firstTab.ZoneFilePathTmp;
						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
							
					 );
					}
				}
			}
		});

		firstTab.btnNidPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isEdit)
				{
					if(!firstTab.btnNid.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnNid.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll("ESLDM", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.nidFilePathTmp;
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
				if(isEdit)
				{
					if(!firstTab.btnNid.actionCheck)
					{
						if(!nidImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								link = link.replaceAll("uptd/", nidImageLoc.substring(22, nidImageLoc.length()));
							}
							else if(link.endsWith("MSML/"))
							{
								link = link.replaceAll("MSML/", nidImageLoc.substring(22, nidImageLoc.length()));
							}
							else if(link.endsWith("RJSL/"))
							{
								link = link.replaceAll("RJSL/", nidImageLoc.substring(22, nidImageLoc.length()));
							}
							else if(link.endsWith("UNIGLOBAL/"))
							{
								link = link.replaceAll("UNIGLOBAL/", nidImageLoc.substring(27, nidImageLoc.length()));
							}
							else if(link.endsWith("ESLDM/"))
							{
								link = link.replaceAll("ESLDM/", nidImageLoc.substring(22, nidImageLoc.length()));
							}
							else if(link.endsWith("RSRIL/"))
							{
								link = link.replaceAll("RSRIL/", nidImageLoc.substring(22, nidImageLoc.length()));
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
					if(firstTab.btnNid.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll("ESLDM", "report")+firstTab.nidFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.nidFilePathTmp;
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
		});

		firstTab.btnApplicationPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isEdit)
				{
					if(!firstTab.btnAppDate.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnAppDate.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll("ESLDM", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.applicationFilePathTmp;
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
				if(isEdit)
				{
					if(!firstTab.btnAppDate.actionCheck)
					{
						if(!applicationImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								link = link.replaceAll("uptd/", applicationImageLoc.substring(22, applicationImageLoc.length()));
							}
							else if(link.endsWith("MSML/"))
							{
								link = link.replaceAll("MSML/", applicationImageLoc.substring(22, applicationImageLoc.length()));
							}
							else if(link.endsWith("RJSL/"))
							{
								link = link.replaceAll("RJSL/", applicationImageLoc.substring(22, applicationImageLoc.length()));
							}
							else if(link.endsWith("UNIGLOBAL/"))
							{
								link = link.replaceAll("UNIGLOBAL/", applicationImageLoc.substring(27, applicationImageLoc.length()));
							}
							else if(link.endsWith("ESLDM/"))
							{
								link = link.replaceAll("ESLDM/", applicationImageLoc.substring(22, applicationImageLoc.length()));
							}
							else if(link.endsWith("RSRIL/"))
							{
								link = link.replaceAll("RSRIL/", applicationImageLoc.substring(22, applicationImageLoc.length()));
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
					if(firstTab.btnAppDate.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll("ESLDM", "report")+firstTab.applicationFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.applicationFilePathTmp;
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
		});

		firstTab.btnJoinPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isEdit)
				{
					if(!firstTab.btnJoiningDate.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnJoiningDate.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll("ESLDM", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.joinFilePathTmp;
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
				if(isEdit)
				{
					if(!firstTab.btnJoiningDate.actionCheck)
					{
						if(!joinImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								link = link.replaceAll("uptd/", joinImageLoc.substring(22, joinImageLoc.length()));
							}
							else if(link.endsWith("MSML/"))
							{
								link = link.replaceAll("MSML/", joinImageLoc.substring(22, joinImageLoc.length()));
							}
							else if(link.endsWith("RJSL/"))
							{
								link = link.replaceAll("RJSL/", joinImageLoc.substring(22, joinImageLoc.length()));
							}
							else if(link.endsWith("UNIGLOBAL/"))
							{
								link = link.replaceAll("UNIGLOBAL/", joinImageLoc.substring(27, joinImageLoc.length()));
							}
							else if(link.endsWith("ESLDM/"))
							{
								link = link.replaceAll("ESLDM/", joinImageLoc.substring(22, joinImageLoc.length()));
							}
							else if(link.endsWith("RSRIL/"))
							{
								link = link.replaceAll("RSRIL/", joinImageLoc.substring(22, joinImageLoc.length()));
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
					if(firstTab.btnJoiningDate.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll(" ", "report")+firstTab.joinFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.joinFilePathTmp;
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
		});

		firstTab.btnConPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isEdit)
				{
					if(!firstTab.btnConfirmdate.actionCheck)
					{
						showNotification("Warning","There is no file");
					}
					if(firstTab.btnConfirmdate.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll("ESLDM", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.conFilePathTmp;
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
				if(isEdit)
				{
					if(!firstTab.btnConfirmdate.actionCheck)
					{
						if(!conImageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								link = link.replaceAll("uptd/", conImageLoc.substring(22, conImageLoc.length()));
							}
							else if(link.endsWith("MSML/"))
							{
								link = link.replaceAll("MSML/", conImageLoc.substring(22, conImageLoc.length()));
							}
							else if(link.endsWith("RJSL/"))
							{
								link = link.replaceAll("RJSL/", conImageLoc.substring(22, conImageLoc.length()));
							}
							else if(link.endsWith("UNIGLOBAL/"))
							{
								link = link.replaceAll("UNIGLOBAL/", conImageLoc.substring(27, conImageLoc.length()));
							}
							else if(link.endsWith("ESLDM/"))
							{
								link = link.replaceAll("ESLDM/", conImageLoc.substring(22, conImageLoc.length()));
							}
							else if(link.endsWith("RSRIL/"))
							{
								link = link.replaceAll("RSRIL/", conImageLoc.substring(22, conImageLoc.length()));
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
					if(firstTab.btnConfirmdate.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("ESLDM/"))
						{
							link = link.replaceAll("ESLDM", "report")+firstTab.conFilePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+firstTab.conFilePathTmp;
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
		});
	}

	private void idCheck()
	{
		String sql = null;
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			sql = " select employeeCode from tbEmployeeInfo  " +
					" where employeeCode = '"+firstTab.txtEmployeeCode.getValue().toString().trim()+"' ";
			List <?> lst = session.createSQLQuery(sql).list();
			Iterator <?> iter = lst.iterator();

			if(iter.hasNext())
			{
				showNotification("Warning,","This Employee ID Already Exist", Notification.TYPE_HUMANIZED_MESSAGE);
				firstTab.txtEmployeeCode.setValue("");
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		finally
		{
			session.close();
		}
	}

	private void duplicateAccountNo()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = " select accountNo from tbEmployeeInfo where accountNo='"+fifthTab.txtAccountNo.getValue().toString().trim()+"' ";
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			if (iter.hasNext()) 
			{
				showNotification("Warning,","This Account No is Already Exist", Notification.TYPE_HUMANIZED_MESSAGE);
				fifthTab.txtAccountNo.setValue("");
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally
		{
			session.close();
		}
	}

	private void deleteAllData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			String sqlWorking = "delete from tbWorkingExperiance where vEmployeeId='"+cmbEmployeeName.getValue()+"' ";
			session.createSQLQuery(sqlWorking).executeUpdate();
			String sqlEducation = "delete from tbEducation where vEmployeeId='"+cmbEmployeeName.getValue()+"' ";
			session.createSQLQuery(sqlEducation).executeUpdate();
			String sqlEmployee = "delete from tbEmployeeInfo where vEmployeeId='"+cmbEmployeeName.getValue()+"' ";
			session.createSQLQuery(sqlEmployee).executeUpdate();
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			System.out.println(ex);
		}
		finally
		{
			session.close();
		}
	}

	private void selectEmployeeInfo(String radio)
	{
		imageLoc = "0";
		birthImageLoc = "0";
		tinNoImageLoc = "0";
		circleImageLoc = "0";
		zoneImageLoc = "0";
		nidImageLoc = "0";
		applicationImageLoc = "0";
		joinImageLoc = "0";
		conImageLoc = "0";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "";
			sql = "select ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vEmployeeNameBan,ein.vReligion,ein.vContact," +
					"ein.vEmail,ein.vGender,ein.dDateOfBirth,ein.DateOfBirthLocation,ein.vNationality,ein.nId,ein.nIdLocation,ein.vEmployeeType," +
					"ein.dApplicationDate,ein.applicationDateLocation,ein.dInterviewDate,ein.dJoiningDate,ein.joiningDateLocation," +
					"convert(date,ein.dConfirmationDate) dConfirmationDate,confirmationDateLocation,ein.vStatus,ein.iStatus,ein.dStatusDate,ein.vDepartmentId," +
					"ein.vSectionId,ein.vDesignationId,ein.vFloor,ein.vLine,ein.vGrade,ein.imageLocation,ein.vFatherName,ein.vMotherName,ein.vPermanentAddress," +
					"ein.vMailingAddress,ein.vBloodGroup,ein.vMeritalStatus,ein.dMarriageDate,ein.vSpouseName,ein.vSpouseOccupation,ein.iNoOfChild," +
					"ein.vN1Name,ein.vN1Relation,ein.vN2Name,ein.vN2Relation,ein.vOtherQualification,ein.vComputerSkill,ein.mMonthlySalary,ein.birthImage," +
					"ein.nidImage,ein.applicationImage,ein.joiningImage,ein.confirmImage,ein.subUnitId,ein.mHouseRent,ein.OtStatus,ein.mMedicalAllowance," +
					"ein.mConAllowance,ein.mClinical,ein.mSpecial,ein.mNonPractice,ein.mOthersAllowance,ein.mProvidentFund,ein.mRoomCharge,ein.mBreej," +
					"ein.mKhichuri,ein.mKFund,ein.bankId,ein.bankName,ein.bankBranchId,ein.branchName,ein.accountNo,ein.userId,ein.userIp,ein.entryTime," +
					"ein.FridayStatus,ein.FridayLunchFee,ein.isDelete,ein.DeleteDate,ein.mDearnessAllowance,ein.mFireAllowance,ISNULL(pd.iPhysicallyDisable,0) " +
					"physicallyDisable,ISNULL(vMobileBankFlag,'') vMobileBankFlag,vTinNo,vTinImageLocation,vCircle,vCircleImageLocation,vZone,vZoneImageLocation,vGrades " +
					"from tbEmployeeInfo ein left join tbPhysicallyDisable pd on ein.vEmployeeId=pd.vEmployeeID where ein.vEmployeeId='"+cmbEmployeeName.getValue().toString()+"' ";
			List <?> list = session.createSQLQuery(sql).list();	
			
			System.out.println(sql);

			for(Iterator <?> iterEmployee = list.iterator(); iterEmployee.hasNext();)
			{
				for(int j = 0;j<thirdTab.tblTxtExam.size();j++)
				{
					if(!thirdTab.tblTxtExam.get(j).getValue().equals(""))
					{
						thirdTab.tblTxtExam.get(j).setValue("");
						thirdTab.tblTxtGroup.get(j).setValue("");
						thirdTab.tblTxtInstitute.get(j).setValue("");
						thirdTab.tblTxtBoard.get(j).setValue("");
						thirdTab.tblTxtDivision.get(j).setValue("");
						thirdTab.tblDateYear.get(j).setValue(new java.util.Date());
					}

					for(int k = 0;k<fourthTab.tblTxtPost.size();k++)
					{
						if(!fourthTab.tblTxtPost.get(k).getValue().equals(""))
						{
							fourthTab.tblTxtPost.get(k).setValue("");
							fourthTab.tblTxtCompanyName.get(k).setValue("");
							fourthTab.tblTxtMajorTask.get(k).setValue("");
							fourthTab.tblDateFrom.get(k).setValue(new java.util.Date());
							fourthTab.tblDateTo.get(k).setValue(new java.util.Date());
						}
					}
				}

				Object[] element = (Object[]) iterEmployee.next();
				firstTab.txtEmployeeId.setValue(element[0]);
				firstTab.txtEmployeeCode.setValue(element[1].toString());
				firstTab.txtProximityId.setValue(element[2].toString());
				firstTab.txtEmpName.setValue(element[3].toString());
				firstTab.cmbSerType.setValue(element[4].toString());
				firstTab.cmbReligion.setValue(element[5].toString());
				firstTab.txtContact.setValue(element[6].toString());
				firstTab.txtEmail.setValue(element[7].toString());
				firstTab.cmbGender.setValue(element[8].toString());
				firstTab.dDateofBirth.setValue(element[9]);
				firstTab.txtNationality.setValue(element[11].toString());
				firstTab.txtNid.setValue(element[12].toString());
				firstTab.cmbEmpType.setValue(element[14].toString());
				firstTab.dAppDate.setValue(element[15]);
				firstTab.dInterviewDate.setValue(element[17]);
				firstTab.dJoiningDate.setValue(element[18]);
				firstTab.cmbGrades.setValue(element[90].toString());

				if(!element[20].toString().trim().equalsIgnoreCase("1900-01-01"))
					firstTab.dConDate.setValue(element[20]);
				else
					firstTab.dConDate.setValue(null);

				firstTab.cmbStatus.setValue(element[22].toString());
				if(element[56].toString().equalsIgnoreCase("1")){
					firstTab.chkOtEnable.setValue(true);
				}
				else{
					firstTab.chkOtEnable.setValue(false);
				}

				if(element[22].toString().equalsIgnoreCase("Continue"))
				{
					firstTab.dDate.setValue(new java.util.Date());
				}
				else
				{
					firstTab.dDate.setValue(element[24]);
				}

				if(element[76].toString().equalsIgnoreCase("1"))
				{
					firstTab.chkFriEnable.setValue(true);
					fifthTab.txtFireAllowance.setValue(df.format(Double.parseDouble(element[77].toString())));
				}
				else
				{
					firstTab.chkFriEnable.setValue(false);
				}

				if(element[82].toString().equalsIgnoreCase("1"))
					firstTab.chkPhysicallyDesable.setValue(true);

				firstTab.cmbDepartment.setValue(element[25]);
				firstTab.cmbSection.setValue(element[26].toString());
				firstTab.cmbDesignation.setValue(element[27].toString());
				firstTab.txtTINNo.setValue(element[84].toString());
				firstTab.txtCircle.setValue(element[86].toString());
				firstTab.txtZone.setValue(element[88].toString());

				employeeImage(element[31].toString());
				imageLoc = element[31].toString();

				birthImageLoc = element[49].toString();
				tinNoImageLoc = element[85].toString();
				circleImageLoc = element[87].toString();
				zoneImageLoc = element[89].toString();
				nidImageLoc = element[50].toString();
				applicationImageLoc = element[51].toString();
				joinImageLoc = element[52].toString();
				conImageLoc = element[53].toString();

				/****************  Data retrieving to second tab from EmployeeInfo table ***************/

				secondTab.txtFatherName.setValue(element[32].toString());
				secondTab.txtMotherName.setValue(element[33].toString());
				secondTab.txtPerAddress.setValue(element[34].toString());
				secondTab.txtMailing.setValue(element[35].toString());
				secondTab.cmbBloodGroup.setValue(element[36]);
				secondTab.cmbMaritalStatus.setValue(element[37]);
				secondTab.DMarriageDate.setValue(element[38]);
				secondTab.txtSpouseName.setValue(element[39].toString());
				secondTab.txtSpouseOccupation.setValue(element[40].toString());
				secondTab.amntNumofChild.setValue(element[41]);
				secondTab.txtNomineeNane.setValue(element[42].toString());
				secondTab.txtRelation.setValue(element[43].toString());
				secondTab.txtNomineeName2.setValue(element[44].toString());
				secondTab.txtRelation2.setValue(element[45].toString());

				/****************  Data retrieving to third tab from EmployeeInfo table ***************/
				thirdTab.txtOtherQualification.setValue(element[46].toString());
				thirdTab.txtComputerSkill.setValue(element[47].toString());
				/****************  Data retrieving to fifth Tab tab from Education table ***************/
				fifthTab.txtGross.setValue(df.format(Double.parseDouble(element[62].toString())) );
				fifthTab.txtBasic.setValue(df.format(Double.parseDouble(element[48].toString())) );
				fifthTab.txtHouseRent.setValue(df.format(Double.parseDouble(element[55].toString())) );
				fifthTab.txtMedical.setValue(df.format(Double.parseDouble(element[57].toString())) );
				fifthTab.txtCon.setValue(df.format(Double.parseDouble(element[58].toString())) );
				fifthTab.txtAttBonus.setValue(df.format(Double.parseDouble(element[60].toString())) );
				fifthTab.txtProvidentFund.setValue(df.format(Double.parseDouble(element[63].toString())) );
				fifthTab.txtInsurance.setValue(df.format(Double.parseDouble(element[66].toString())));
				fifthTab.txtTax.setValue(df.format(Double.parseDouble(element[67].toString())) );
				fifthTab.cmbBankName.setValue(element[68].toString());
				fifthTab.cmbBranchName.setValue(element[70].toString());
				fifthTab.opgBank.setValue(element[83].toString());
				fifthTab.txtAccountNo.setValue(element[72].toString());
				fifthTab.txtDearnessAllowance.setValue(df.format(Double.parseDouble(element[80].toString())));
				fifthTab.txtFireAllowance.setValue(df.format(Double.parseDouble(element[81].toString())));
			

				session.clear();
				String education="select vNameOfExam,vGroup_Subject,vNameOfInstitution,vBoard_University,vDivision_Class_Grade,vYearOfPassing as a from tbEducation where vEmployeeId='"+cmbEmployeeName.getValue()+"' ";
				List <?> lstEducation = session.createSQLQuery(education).list();
				int i=0;
				for (Iterator <?> iterEducation = lstEducation.iterator();iterEducation.hasNext();)
				{
					Object[] elementEducation = (Object[]) iterEducation.next();

					thirdTab.tblTxtExam.get(i).setValue(elementEducation[0].toString());
					thirdTab.tblTxtGroup.get(i).setValue(elementEducation[1].toString());
					thirdTab.tblTxtInstitute.get(i).setValue(elementEducation[2].toString());
					thirdTab.tblTxtBoard.get(i).setValue(elementEducation[3].toString());
					thirdTab.tblTxtDivision.get(i).setValue(elementEducation[4].toString());
					thirdTab.tblDateYear.get(i).setValue(elementEducation[5]);
					i++;
				}
				session.clear();

				/****************  Data retrieving to third Tab tab from WorkingExperience table ***************/
				String experience="select * from tbWorkingExperiance where vEmployeeId='"+cmbEmployeeName.getValue()+"' ";
				List <?> lstexperience = session.createSQLQuery(experience).list();
				int j=0;
				for (Iterator <?> iterEx = lstexperience.iterator();iterEx.hasNext();)
				{
					Object[] elementEx = (Object[]) iterEx.next();

					fourthTab.tblTxtPost.get(j).setValue(elementEx[2].toString());
					fourthTab.tblTxtCompanyName.get(j).setValue(elementEx[3].toString());
					fourthTab.tblTxtMajorTask.get(j).setValue(elementEx[6].toString());

					j++;
				}
			}

		}
		catch(Exception ex)
		{           
			showNotification("selectEmployeeInfo",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void SaveButtonAction()
	{
		if(isEdit)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, 
				"Do you want to update  information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), 
				new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(isEdit)
						{
							System.out.println("delete");
							
							updateEmployeeInfo();
							updateOthersTable();
							reportShow();
							//emailSend();
							cmbDepartmentName.setValue(null);
							cmbSectionName.setValue(null);
							cmbEmployeeName.setValue(null);
							textClear();
							RadioBtnGroup.setEnabled(true);
							cmbSectionName.setEnabled(true);
							cmbEmployeeName.setEnabled(true);
							addCmbEmployeeData();
							tabInit(false);
							btnInit(true);
							isEdit=false;
							showNotification("Data Inserted Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
						}
						isEdit = false;
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertDataToEmployeeInfo();
						insertToOthersTable();
						cmbDepartmentName.setValue(null);
						cmbSectionName.setValue(null);
						cmbEmployeeName.setValue(null);
						textClear();
						RadioBtnGroup.setEnabled(true);
						cmbSectionName.setEnabled(true);
						cmbEmployeeName.setEnabled(true);
						addCmbEmployeeData();
						tabInit(false);
						btnInit(true);
						isEdit=false;
						showNotification("Data Inserted Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
					}
				}
			});
		}
	}

	
	private void reportGenerate(String iclientId, String fpath) throws HibernateException, JRException, IOException 
	{	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String query = "";
		ReportDate reportTime = new ReportDate();

		query="select vEmployeeID,employeeCode,vProximityId,vEmployeeName,(select din.designationName from tbDesignationInfo "
				+ " din where din.designationId=ein.vDesignationId) vDesignationName,vGender,vEmployeeType,vStatus,iStatus,mMonthlySalary,"
				+ " vUserName,dDateTime,vPcIp,mHouseRent,OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,"
				+ " mProvidentFund,bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee,'Present' as vUDFlag "
				+ " from tbEmployeeInfo ein where vEmployeeId = '"+firstTab.txtEmployeeId.getValue().toString().trim()+"' union all "
				+ " select vEmployeeID,employeeCode,vProximityId,vEmployeeName,(select din.designationName from tbDesignationInfo din "
				+ " where din.designationId=uein.vDesignationId) vDesignationName,vGender,vEmployeeType,vStatus,iStatus,mMonthlySalary,"
				+ " vUserName,dDateTime,vPcIp,mHouseRent,OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,"
				+ " mProvidentFund,bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee,'Old' vUDFlag from "
				+ " tbUDEmployeeInfo uein where vEmployeeId = '"+firstTab.txtEmployeeId.getValue().toString().trim()+"' order by "
				+ " vEmployeeName,vEmployeeID,vUDFlag desc,dDateTime desc";
		
		if(queryValueCheck(query))
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("section",firstTab.cmbSection.getItemCaption(firstTab.cmbSection.getValue()));
			hm.put("Department",firstTab.cmbDepartment.getItemCaption(firstTab.cmbDepartment.getValue()));
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("sql", query);
			
			FileOutputStream of = new FileOutputStream(fpath);

			JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream("report/account/hrmModule/rptEditEmployeeInformation.jasper"), of, hm,session.connection());
			tx.commit();
			of.close();
		}		
	}
	
	
	
	private void emailSend() 
	{

		//public static String emailPath = "D:/Tomcat 7.0/webapps/report/astecherp/Email/";
		
		ReportDate reportTime = new ReportDate();
		
		System.out.printf("1");
		//HashMap hm = new HashMap();
		try
		{
			System.out.printf("2");
			File f = new File(sessionBean.emailPath);
			f.mkdirs();
			System.out.printf("3");
			System.out.printf("f"+f);
			String MasterId="";
			log = new FileWriter("D:/Tomcat 7.0/webapps/report/astecherp/Email/log.txt");
			System.out.printf("log"+log);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			
			String host = "smtp.gmail.com";
			String from = "";
			String pass = "";
			
			
			from="evisionsoftwareltd@gmail.com";
			pass="786@esl10";
			

			String EmailTo="support@eslctg.com";
			String EmailSubject="Employee Information Edit";
			String EmailTxt="Employee ID "+firstTab.txtEmployeeCode.getValue()+" has been Edited \n"+
			"by User Name: "+sessionBean.getUserName()+", \nUser IP: "+sessionBean.getUserIp()+" \n and Date Time: "+reportTime.getTime+" , " +
			"and please check here with attached PDF report";
			
			System.out.printf("\nHost"+from);
			System.out.printf("\nPass"+pass);

			Properties props = System.getProperties();
			props.put("mail.smtp.starttls.enable", "true"); // added this line
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.user", from);
			props.put("mail.smtp.password", pass);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");

			javax.mail.Session esession = javax.mail.Session.getDefaultInstance(props, null);
			MasterId=firstTab.txtEmployeeId.getValue().toString();
			
			System.out.printf("4");
			System.out.printf("\n4.1"+MasterId);
			reportGenerate(MasterId,sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			
		
			MimeMessage message = new MimeMessage(esession);
			message.setFrom(new InternetAddress(from));
			/*message.addRecipient(Message.RecipientType.TO, new InternetAddress("support@eslctg.com"));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress("nazimesl@yahoo.com"));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress("emdidar@gmail.com"));*/
			
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("hr.admin@astechbd.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("akidahmed@astechbd.com"));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress("sharif@astechbd.com"));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress("ashim@astechbd.com"));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress("hr.desk@astechbd.com"));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress("nazimesl@yahoo.com"));
			
			message.setSubject(EmailSubject);
			message.setText(EmailTxt);
			System.out.printf("7");
			// create the message part 
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			//fill message
			messageBodyPart.setText(EmailTxt);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			System.out.printf("8");
			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			messageBodyPart.setDataHandler( new DataHandler(source));
			messageBodyPart.setFileName(sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			multipart.addBodyPart(messageBodyPart);
			System.out.printf("9");
			// Put parts in message
			message.setContent(multipart);
			System.out.printf("10");
			Transport transport = esession.getTransport("smtp");
			System.out.println(sessionBean.emailPath+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			System.out.printf("11");
			System.out.printf("host "+host+" from "+from+" pass "+pass);
			transport.connect(host, from, pass);
			System.out.printf("12");
			transport.sendMessage(message, message.getAllRecipients());
			System.out.printf("13");
			transport.close();
			System.out.printf("14");
			//log.write("Info:"+"E-mail Send for client id: "+MasterId+"\n");
			System.out.printf("15");
			this.getParent().showNotification("E-mail Send Successfully.");	
		}
		catch(Exception exp){
			showNotification("mail send :"+exp,Notification.TYPE_ERROR_MESSAGE);
		}

	}
	
	
	
	private void reportShow()
	{		
		ReportDate reportTime = new ReportDate();
		try
		{
			String query="select vEmployeeID,employeeCode,vProximityId,vEmployeeName,(select din.designationName from tbDesignationInfo "
					+ " din where din.designationId=ein.vDesignationId) vDesignationName,vGender,vEmployeeType,vStatus,iStatus,mMonthlySalary,"
					+ " vUserName,dDateTime,vPcIp,mHouseRent,OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,"
					+ " mProvidentFund,bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee,'Present' as vUDFlag "
					+ " from tbEmployeeInfo ein where vEmployeeId = '"+firstTab.txtEmployeeId.getValue().toString().trim()+"' union all "
					+ " select vEmployeeID,employeeCode,vProximityId,vEmployeeName,(select din.designationName from tbDesignationInfo din "
					+ " where din.designationId=uein.vDesignationId) vDesignationName,vGender,vEmployeeType,vStatus,iStatus,mMonthlySalary,"
					+ " vUserName,dDateTime,vPcIp,mHouseRent,OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,"
					+ " mProvidentFund,bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee,'Old' vUDFlag from "
					+ " tbUDEmployeeInfo uein where vEmployeeId = '"+firstTab.txtEmployeeId.getValue().toString().trim()+"' order by "
					+ " vEmployeeName,vEmployeeID,vUDFlag desc,dDateTime desc";

			
			System.out.println("emailSend"+query);
			
			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("section",firstTab.cmbSection.getItemCaption(firstTab.cmbSection.getValue()));
				hm.put("Department",firstTab.cmbDepartment.getItemCaption(firstTab.cmbDepartment.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEditEmployeeInformation.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

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
	
	private String selectMaxEmpId()
	{
		String maxId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = "Select isnull(max(cast(SUBSTRING(vEmployeeId,5,100) as int)),0)+1 from tbEmployeeInfo";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				String srt = iter.next().toString();
				maxId = "EMP-"+srt;
				employeeCode = srt;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return maxId;
	}

	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			List <?> lst = session.createSQLQuery(sql).list();
			if (!lst.isEmpty()) 
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

	private void insertDataToEmployeeInfo()
	{
		String query = "";

		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		@SuppressWarnings("unused")
		int status = 0;
		String shift="0";
		if(firstTab.cmbShiftId.getValue()!=null)
		{
			shift=firstTab.cmbShiftId.getValue().toString();
		}

		if(firstTab.cmbStatus.getValue()!=null)
		{
			if(firstTab.cmbStatus.getValue().toString().equals("Continue"))
			{
				status = 1;
			}
			if(firstTab.cmbStatus.getValue().toString().equals("Discontinue"))
			{
				status = 2;
			}
			if(firstTab.cmbStatus.getValue().toString().equals("Retired"))
			{
				status = 3;
			}
			if(firstTab.cmbStatus.getValue().toString().equals("Dismiss"))
			{
				status = 4;
			}
			if(firstTab.cmbStatus.getValue().toString().equals("Terminated"))
			{
				status = 5;
			}
		}

		firstTab.txtEmployeeId.setValue(firstTab.generateEmployeeId());
		String masterEmployeeId = selectMaxEmpId();
		
		int empStatus=0;
		String imagePathEmployee = imagePath(1,masterEmployeeId)==null?imageLoc:imagePath(1,masterEmployeeId);
		String imagePathBirth = firstTab.birthPath(1,firstTab.txtEmployeeId.getValue().toString())==null?birthImageLoc:firstTab.birthPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathNid = firstTab.nidPath(1,firstTab.txtEmployeeId.getValue().toString())==null?nidImageLoc:firstTab.nidPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathApplication = firstTab.applicationPath(1,firstTab.txtEmployeeId.getValue().toString())==null?applicationImageLoc:firstTab.applicationPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathJoin = firstTab.joinPath(1,firstTab.txtEmployeeId.getValue().toString())==null?joinImageLoc:firstTab.joinPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathCon = firstTab.conPath(1,firstTab.txtEmployeeId.getValue().toString())==null?conImageLoc:firstTab.conPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathTin = firstTab.applicationPath(1,firstTab.txtEmployeeId.getValue().toString())==null?tinNoImageLoc:firstTab.applicationPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathCircle = firstTab.joinPath(1,firstTab.txtEmployeeId.getValue().toString())==null?circleImageLoc:firstTab.joinPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathZone = firstTab.conPath(1,firstTab.txtEmployeeId.getValue().toString())==null?zoneImageLoc:firstTab.conPath(1,firstTab.txtEmployeeId.getValue().toString());

		try
		{
			if(firstTab.cmbStatus.getValue().toString().equals("Continue"))
			{
				empStatus=1;
			}
			else
			{
				empStatus=0;
			}

			int fristatus=0;
			if(firstTab.chkFriEnable.booleanValue())
				fristatus=1;

			query="insert into tbEmployeeInfo (vEmployeeId,employeeCode,iFingerID,vProximityId,vEmployeeName,"
					+ " vEmployeeNameBan,vReligion,vContact,vEmail,vGender,dDateOfBirth,DateOfBirthLocation,"
					+ " vNationality,nId,nIdLocation,vEmployeeType,dApplicationDate,applicationDateLocation,"
					+ " dInterviewDate,dJoiningDate,joiningDateLocation,dConfirmationDate,confirmationDateLocation,"
					+ " vStatus,iStatus,dStatusDate,vDepartmentId,vSectionId,vDesignationId,vFloor,vLine,vGrade,"
					+ " imageLocation,vFatherName,vMotherName,vPermanentAddress,vMailingAddress,vBloodGroup,"
					+ " vMeritalStatus,dMarriageDate,vSpouseName,vSpouseOccupation,iNoOfChild,vN1Name,vN1Relation,"
					+ " vN2Name,vN2Relation,vOtherQualification,vComputerSkill,mMonthlySalary,vUserName,dDateTime,"
					+ " vPcIp,birthImage,nidImage,applicationImage,joiningImage,confirmImage,subUnitId,mHouseRent,"
					+ " OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mNonPractice,mOthersAllowance,"
					+ " mProvidentFund,mRoomCharge,mBreej,mKhichuri,mKFund,bankId,bankName,bankBranchId,branchName,"
					+ " accountNo,userId,userIp,entryTime,FridayStatus,FridayLunchFee,isDelete,DeleteDate,"
					+ " mDearnessAllowance,mFireAllowance,vMobileBankFlag,vTinNo,vTinImageLocation,vCircle,"
					+ " vCircleImageLocation,vZone,vZoneImageLocation,vGrades,vGradesName) values (" +
					" '"+firstTab.txtEmployeeId.getValue().toString().trim()+"', " +
					" '"+firstTab.txtEmployeeCode.getValue().toString().trim()+"', " +
					" '"+(firstTab.txtFingerId.getValue().toString().isEmpty()?"":firstTab.txtFingerId.getValue().toString())+"', " +
					" '"+(firstTab.txtProximityId.getValue().toString().isEmpty()?"":firstTab.txtProximityId.getValue().toString())+"', " +
					" '"+firstTab.txtEmpName.getValue().toString().trim()+"', " +
					" '"+firstTab.cmbSerType.getValue().toString()+"', " +
					" '"+firstTab.cmbReligion.getValue().toString()+"', " +
					" '"+firstTab.txtContact.getValue().toString().trim()+"', " +
					" '"+firstTab.txtEmail.getValue().toString().trim()+"', " +
					" '"+firstTab.cmbGender.getValue().toString()+"', " +
					" '"+(dateFormat.format(firstTab.dDateofBirth.getValue()).isEmpty()?"":dateFormat.format(firstTab.dDateofBirth.getValue()))+"'," +
					" '0', " +
					" '"+(firstTab.txtNationality.getValue().toString().trim())+"', " +
					" '"+(firstTab.txtNid.getValue().toString().trim())+"', " +
					" '0', " +
					" '"+firstTab.cmbEmpType.getValue().toString()+"', " +
					" '"+(dateFormat.format(firstTab.dAppDate.getValue()).isEmpty()?"":dateFormat.format(firstTab.dAppDate.getValue()))+"', " +
					" '0', " +
					" '"+(dateFormat.format(firstTab.dInterviewDate.getValue()).isEmpty()?"":dateFormat.format(firstTab.dInterviewDate.getValue()))+"', " +
					" '"+(dateFormat.format(firstTab.dJoiningDate.getValue()).isEmpty()?"":dateFormat.format(firstTab.dJoiningDate.getValue()))+"', " +
					" '0', " +
					" '"+(firstTab.dConDate.getValue()==null?"":dateFormat.format(firstTab.dConDate.getValue()))+"', " +
					" '0', " +
					" '"+firstTab.cmbStatus.getValue().toString()+"', " +
					" '"+empStatus+"', " +
					" '"+(dateFormat.format(firstTab.dDate.getValue()).isEmpty()?"":dateFormat.format(firstTab.dDate.getValue()))+"', " +
					" '"+firstTab.cmbDepartment.getValue().toString()+"', " +
					" '"+firstTab.cmbSection.getValue().toString()+"' ," +
					" '"+firstTab.cmbDesignation.getValue().toString()+"', " +
					" '0', " +
					" '"+shift+"', " +
					" '0', " +			
					"'"+imagePathEmployee+"', "+
					"'"+(secondTab.txtFatherName.getValue().toString().trim().isEmpty()?"":secondTab.txtFatherName.getValue().toString().trim())+"', " +
					"'"+(secondTab.txtMotherName.getValue().toString().trim().isEmpty()?"":secondTab.txtMotherName.getValue().toString().trim())+"', " +
					"'"+(secondTab.txtPerAddress.getValue().toString().trim().isEmpty()?"":secondTab.txtPerAddress.getValue().toString().trim())+"', " +
					"'"+(secondTab.txtMailing.getValue().toString().trim().isEmpty()?"":secondTab.txtMailing.getValue().toString().trim())+"', " +
					" '"+(secondTab.cmbBloodGroup.getValue()==null?"":secondTab.cmbBloodGroup.getValue().toString())+"', " +
					" '"+(secondTab.cmbMaritalStatus.getValue()==null?"":secondTab.cmbMaritalStatus.getValue().toString())+"', " +
					" '"+(dateFormat.format(secondTab.DMarriageDate.getValue()).isEmpty()?"":dateFormat.format(secondTab.DMarriageDate.getValue()))+"', " +
					"'"+(secondTab.txtSpouseName.getValue().toString().trim().isEmpty()?"":secondTab.txtSpouseName.getValue().toString().trim())+"', " +
					"'"+(secondTab.txtSpouseOccupation.getValue().toString().trim().isEmpty()?"":secondTab.txtSpouseOccupation.getValue().toString().trim())+"', " +
					"'"+(secondTab.amntNumofChild.getValue().toString().trim().isEmpty()?"":secondTab.amntNumofChild.getValue().toString().trim())+"', " +
					"'"+(secondTab.txtNomineeNane.getValue().toString().trim().isEmpty()?"":secondTab.txtNomineeNane.getValue().toString().trim())+"', " +
					"'"+(secondTab.txtRelation.getValue().toString().trim().isEmpty()?"":secondTab.txtRelation.getValue().toString().trim())+"', " +
					"'"+(secondTab.txtNomineeName2.getValue().toString().trim().isEmpty()?"":secondTab.txtNomineeName2.getValue().toString().trim())+"', " +
					"'"+(secondTab.txtRelation2.getValue().toString().trim().isEmpty()?"":secondTab.txtRelation2.getValue().toString().trim())+"', " +
					"'"+(thirdTab.txtOtherQualification.getValue().toString().trim().isEmpty()?"":thirdTab.txtOtherQualification.getValue().toString().trim())+"', " +
					"'"+(thirdTab.txtComputerSkill.getValue().toString().trim().isEmpty()?"":thirdTab.txtComputerSkill.getValue().toString().trim())+"'," +
					"'"+fifthTab.txtBasic.getValue().toString().replaceAll(",", "")+"'," +
					"'"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'"+sessionBean.getUserIp()+"', " +
					" '"+imagePathBirth+"', '"+imagePathNid+"', '"+imagePathApplication+"', '"+imagePathJoin+"', '"+imagePathCon+"', " +
					" '0'," +
					" '"+(fifthTab.txtHouseRent.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtHouseRent.getValue().toString().replaceAll("#", ""))+"'," +
					" '"+OtStatus+"'," +
					" '"+(fifthTab.txtMedical.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtMedical.getValue().toString().replaceAll("#", ""))+"'," +
					" '"+(fifthTab.txtCon.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtCon.getValue().toString().replaceAll("#", ""))+"'," +
					" '0'," +
					" '"+(fifthTab.txtAttBonus.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtAttBonus.getValue().toString().replaceAll("#", ""))+"'," +
					" '0'," +
					// Gross Salary Saved into mOthersAllowance
					" '"+(fifthTab.txtGross.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtGross.getValue().toString().replaceAll("#", ""))+"'," +
					" '"+(fifthTab.txtProvidentFund.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtProvidentFund.getValue().toString().replaceAll("#", ""))+"'," +
					" '0'," +
					" '0'," +
					" '"+(fifthTab.txtInsurance.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtInsurance.getValue().toString().replaceAll("#", ""))+"'," +
					" '"+(fifthTab.txtTax.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtTax.getValue().toString().replaceAll("#", ""))+"'," +
					" '"+(fifthTab.cmbBankName.getValue()==null?"":fifthTab.cmbBankName.getValue().toString())+"'," +
					" '"+(fifthTab.cmbBankName.getValue()==null?"":fifthTab.cmbBankName.getItemCaption(fifthTab.cmbBankName.getValue().toString()))+"'," +
					" '"+(fifthTab.cmbBranchName.getValue()==null?"":fifthTab.cmbBranchName.getValue().toString())+"'," +
					" '"+(fifthTab.cmbBranchName.getValue()==null?"":fifthTab.cmbBranchName.getItemCaption(fifthTab.cmbBranchName.getValue().toString()))+"'," +
					" '"+(fifthTab.txtAccountNo.getValue().toString().isEmpty()?"":fifthTab.txtAccountNo.getValue().toString())+"'," +
					" '"+sessionBean.getUserName()+"'," +
					" '"+sessionBean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP,'"+fristatus+"'," +
					" '"+(fifthTab.txtFridayLunch.getValue().toString().replaceAll(",", "").isEmpty()?"0.0":fifthTab.txtFridayLunch.getValue().toString().replaceAll(",", ""))+"'," +
					" '0','1900-01-01',"+
					" '"+(fifthTab.txtDearnessAllowance.getValue().toString().replaceAll(",", "").isEmpty()?"0.0":fifthTab.txtDearnessAllowance.getValue().toString().replaceAll(",", ""))+"'," +
					" '"+(fifthTab.txtFireAllowance.getValue().toString().replaceAll(",", "").isEmpty()?"0.0":fifthTab.txtFireAllowance.getValue().toString().replaceAll(",", ""))+"',"+
					" '"+fifthTab.BankFlag+"',"
					+ " '"+(!firstTab.txtTINNo.getValue().toString().trim().isEmpty()?firstTab.txtTINNo.getValue().toString().trim():"0")+"',"
					+ " '"+imagePathTin+"',"
					+ " '"+(!firstTab.txtCircle.getValue().toString().trim().isEmpty()?firstTab.txtCircle.getValue().toString().trim():"0")+"',"
					+ " '"+imagePathCircle+"',"
					+ " '"+(!firstTab.txtZone.getValue().toString().trim().isEmpty()?firstTab.txtZone.getValue().toString().trim():"0")+"',"
					+ " '"+imagePathZone+"'," +
					" '"+(firstTab.cmbGrades.getValue()==null?"":firstTab.cmbGrades.getValue().toString())+"'," +
					" '"+(firstTab.cmbGrades.getValue()==null?"":firstTab.cmbGrades.getItemCaption(firstTab.cmbGrades.getValue().toString()))+"'" +
							") ";

			session.createSQLQuery(query).executeUpdate();
			session.clear();

			if(firstTab.chkPhysicallyDesable.booleanValue())
			{
				String physicallyDisable="insert into tbPhysicallyDisable (vEmployeeId,vEmployeeCode,vProximityID,vEmployeeName," +
						"iPhysicallyDisable,vUserName,vUserIP,dEntryTime) values " +
						"('"+firstTab.txtEmployeeId.getValue().toString().trim()+"', " +
						" '"+firstTab.txtEmployeeCode.getValue().toString().trim()+"', " +
						" '"+(firstTab.txtProximityId.getValue().toString().isEmpty()?"":firstTab.txtProximityId.getValue().toString())+"', " +
						" '"+firstTab.txtEmpName.getValue().toString().trim()+"'," +
						" '1'," +
						" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate())";
				System.out.println("Physically Disabled :"+physicallyDisable);
				session.createSQLQuery(physicallyDisable).executeUpdate();
			}
			tx.commit();
		}
		catch(Exception ex)
		{
			showNotification("insertDataToEmployeeInfo"+ex,Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	private void updateEmployeeInfo()
	{
		int empStatus=0;
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		int status = 0;
		if(firstTab.cmbStatus.getValue()!=null)
		{
			if(firstTab.cmbStatus.getValue().toString().equals("Continue"))
			{
				status = 1;
			}
			if(firstTab.cmbStatus.getValue().toString().equals("Discontinue"))
			{
				status = 2;
			}
			if(firstTab.cmbStatus.getValue().toString().equals("Retired"))
			{
				status = 3;
			}
			if(firstTab.cmbStatus.getValue().toString().equals("Dismiss"))
			{
				status = 4;
			}
			if(firstTab.cmbStatus.getValue().toString().equals("Terminated"))
			{
				status = 5;
			}
		}
		

		String imagePathEmployee = imagePath(1,firstTab.txtEmployeeId.getValue().toString())==null?imageLoc:imagePath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathBirth = firstTab.birthPath(1,firstTab.txtEmployeeId.getValue().toString())==null?birthImageLoc:firstTab.birthPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathNid = firstTab.nidPath(1,firstTab.txtEmployeeId.getValue().toString())==null?nidImageLoc:firstTab.nidPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathApplication = firstTab.applicationPath(1,firstTab.txtEmployeeId.getValue().toString())==null?applicationImageLoc:firstTab.applicationPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathJoin = firstTab.joinPath(1,firstTab.txtEmployeeId.getValue().toString())==null?joinImageLoc:firstTab.joinPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathCon = firstTab.conPath(1,firstTab.txtEmployeeId.getValue().toString())==null?conImageLoc:firstTab.conPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathTin = firstTab.applicationPath(1,firstTab.txtEmployeeId.getValue().toString())==null?tinNoImageLoc:firstTab.applicationPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathCircle = firstTab.joinPath(1,firstTab.txtEmployeeId.getValue().toString())==null?circleImageLoc:firstTab.joinPath(1,firstTab.txtEmployeeId.getValue().toString());
		String imagePathZone = firstTab.conPath(1,firstTab.txtEmployeeId.getValue().toString())==null?zoneImageLoc:firstTab.conPath(1,firstTab.txtEmployeeId.getValue().toString());

		if(firstTab.cmbStatus.getValue().toString().equals("Continue"))
		{
			empStatus=1;
		}
		else
		{
			empStatus=0;
		}

		try
		{
			if(status==1 || status==2 || status==3 || status==4 || status==5)
			{
				String query = "";		
				String insertProximityQuery="";
				String insertEmployeeQuery="";
				String sectionId = firstTab.cmbSection.getValue()==null?"":firstTab.cmbSection.getValue().toString();

				int fristatus=0;
				if(firstTab.chkFriEnable.booleanValue())
					fristatus=1;

				String chkQuery="select iStatus from tbEmployeeInfo where vEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'";

				List <?> lstChk=session.createSQLQuery(chkQuery).list();
				int iStatus=Integer.parseInt(lstChk.iterator().next().toString());
				if(iStatus!=empStatus)
				{
					String Sql="insert into tbInactiveEmployee (dInactiveDate,vEmployeeId,vEmployeeCode,vProximityId," +
							"vEmployeeName,vSectionID,vSectionName,vDesignationId,vDesignationName,vActive_Inactive,vPermittedBy," +
							"vReason,vUserName,vUserIP,dEntryTime,vDepartmentID,vDepartmentName) values (getDate()," +
							"'"+firstTab.txtEmployeeId.getValue().toString().trim()+"','"+firstTab.txtEmployeeCode.getValue().toString().trim()+"'," +
							"'"+(firstTab.txtProximityId.getValue().toString().isEmpty()?"":firstTab.txtProximityId.getValue().toString())+"','"+firstTab.txtEmpName.getValue().toString().trim()+"'," +
							"'"+sectionId+"','"+firstTab.cmbSection.getItemCaption(firstTab.cmbSection.getValue().toString())+"'," +
							"'"+firstTab.cmbDesignation.getValue().toString()+"','"+firstTab.cmbDesignation.getItemCaption(firstTab.cmbDesignation.getValue().toString())+"'," +
							"'"+(empStatus==1?"Active":"Inactive")+"','"+sessionBean.getUserName()+"','Unknown','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate()," +
							"'"+cmbDepartmentName.getValue()+"'," +
							"'"+cmbDepartmentName.getItemCaption(cmbDepartmentName.getValue())+"')";
					session.createSQLQuery(Sql).executeUpdate();
				}

				session.clear();

				if(!session.createSQLQuery("select employeeCode from tbEmployeeInfo where vEmployeeId='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'").list().iterator().next().toString().trim().equalsIgnoreCase(firstTab.txtEmployeeCode.getValue().toString().trim()))
				{
					if(session.createSQLQuery("select vEmployeeID from tbEditEmployeeID where vAutoEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'").list().isEmpty())
					{
						insertEmployeeQuery="insert into tbEditEmployeeID (vAutoEmployeeID,vEmployeeID,vProximityID,vEmployeeName,vSectionID," +
								"vUserName,vUserIP,dEntryTime,vDepartmentID) select vEmployeeID,employeeCode,vProximityID,vEmployeeName,vSectionID,'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'," +
								"CURRENT_TIMESTAMP,vDepartmentID from tbEmployeeInfo where vEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'";
						session.createSQLQuery(insertEmployeeQuery).executeUpdate();
					}
					else
					{
						insertEmployeeQuery="Update tbEditEmployeeID set vEmployeeID=(select employeeCode from tbEmployeeInfo where vEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'), " +
								"vUserName='"+sessionBean.getUserName()+"',vUserIP='"+sessionBean.getUserIp()+"',dEntryTime=getdate()  where vAutoEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'";
						session.createSQLQuery(insertEmployeeQuery).executeUpdate();
					}
					session.clear();
				}

				if(!session.createSQLQuery("select vProximityID from tbEmployeeInfo where vEmployeeId='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'").list().iterator().next().toString().trim().equalsIgnoreCase((firstTab.txtProximityId.getValue().toString().trim().isEmpty()?"":firstTab.txtProximityId.getValue().toString().trim())))
				{
					if(session.createSQLQuery("select vProximityID from tbEditProximityID where vAutoEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'").list().isEmpty())
					{
						insertProximityQuery="insert into tbEditProximityID (vAutoEmployeeID,vEmployeeID,vProximityID,vEmployeeName,vSectionID," +
								"vUserName,vUserIP,dEntryTime,vDepartmentID) select vEmployeeID,employeeCode,vProximityID,vEmployeeName,vSectionID,'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"'," +
								"CURRENT_TIMESTAMP,vDepartmentID from tbEmployeeInfo where vEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'";
						session.createSQLQuery(insertProximityQuery).executeUpdate();
					}
					else
					{
						insertProximityQuery="Update tbEditProximityID set vProximityID=(select vProximityID from tbEmployeeInfo where vEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'), " +
								"vUserName='"+sessionBean.getUserName()+"',vUserIP='"+sessionBean.getUserIp()+"',dEntryTime=getdate() where vAutoEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'";
						session.createSQLQuery(insertProximityQuery).executeUpdate();
					}
					session.clear();
				}
				
				String updatequery="INSERT INTO tbUDEmployeeInfo(vEmployeeId,employeeCode,iFingerID,vProximityId," +
						"vEmployeeName,vEmployeeNameBan,vReligion,vContact,vEmail,vGender,dDateOfBirth," +
						"DateOfBirthLocation,vNationality,nId,nIdLocation,vEmployeeType,dApplicationDate," +
						"applicationDateLocation,dInterviewDate,dJoiningDate,joiningDateLocation,dConfirmationDate," +
						"confirmationDateLocation,vStatus,iStatus,dStatusDate,vDepartmentId,vSectionId,vDesignationId," +
						"vFloor,vLine,vGrade,imageLocation,vFatherName,vMotherName,vPermanentAddress,vMailingAddress," +
						"vBloodGroup,vMeritalStatus,dMarriageDate,vSpouseName,vSpouseOccupation,iNoOfChild,vN1Name," +
						"vN1Relation,vN2Name,vN2Relation,vOtherQualification,vComputerSkill,mMonthlySalary,vUserName," +
						"dDateTime,vPcIp,birthImage,nidImage,applicationImage,joiningImage,confirmImage,subUnitId," +
						"mHouseRent,OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mNonPractice," +
						"mOthersAllowance,mProvidentFund,mRoomCharge,mBreej,mKhichuri,mKFund,bankId,bankName,bankBranchId," +
						"branchName,accountNo,userId,userIp,entryTime,FridayStatus,FridayLunchFee,isDelete,DeleteDate," +
						"mDearnessAllowance,mFireAllowance,vUDFlag,vMobileBankFlag,vTinNo,vTinImageLocation,vCircle,vCircleImageLocation," +
						"vZone,vZoneImageLocation,vGrades,vGradesName) select vEmployeeId,employeeCode,iFingerID,vProximityId,vEmployeeName,vEmployeeNameBan," +
						"vReligion,vContact,vEmail,vGender,dDateOfBirth,DateOfBirthLocation,vNationality,nId,nIdLocation,vEmployeeType," +
						"dApplicationDate,applicationDateLocation,dInterviewDate,dJoiningDate,joiningDateLocation,dConfirmationDate," +
						"confirmationDateLocation,vStatus,iStatus,dStatusDate,vDepartmentId,vSectionId," +
						"vDesignationId,vFloor,vLine,vGrade,imageLocation,vFatherName,vMotherName,vPermanentAddress," +
						"vMailingAddress,vBloodGroup,vMeritalStatus,dMarriageDate,vSpouseName,vSpouseOccupation," +
						"iNoOfChild,vN1Name,vN1Relation,vN2Name,vN2Relation,vOtherQualification,vComputerSkill," +
						"mMonthlySalary,vUserName,dDateTime,vPcIp,birthImage,nidImage,applicationImage,joiningImage," +
						"confirmImage,subUnitId,mHouseRent,OtStatus,mMedicalAllowance,mConAllowance,mClinical," +
						"mSpecial,mNonPractice,mOthersAllowance,mProvidentFund,mRoomCharge,mBreej,mKhichuri,mKFund," +
						"bankId,bankName,bankBranchId,branchName,accountNo,userId,userIp,entryTime,FridayStatus," +
						"FridayLunchFee,isDelete,DeleteDate,mDearnessAllowance,mFireAllowance,'Update',vMobileBankFlag," +
						"vTinNo,vTinImageLocation,vCircle,vCircleImageLocation,vZone,vZoneImageLocation,vGrades,vGradesName from tbEmployeeInfo where " +
						"vEmployeeId='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'";
				session.createSQLQuery(updatequery).executeUpdate();
				session.clear();

				query = "update tbEmployeeInfo set "+
						"vEmployeeId='"+firstTab.txtEmployeeId.getValue().toString().trim()+"', " +
						"employeeCode='"+firstTab.txtEmployeeCode.getValue().toString().trim()+"', " +
						"iFingerID='"+(firstTab.txtFingerId.getValue().toString().isEmpty()?"":firstTab.txtFingerId.getValue().toString())+"', " +
						"vProximityId='"+(firstTab.txtProximityId.getValue().toString().isEmpty()?"":firstTab.txtProximityId.getValue().toString())+"' ," +
						"vEmployeeName='"+firstTab.txtEmpName.getValue().toString().trim()+"', " +
						"vEmployeeNameBan='"+firstTab.cmbSerType.getValue().toString().trim()+"', " +
						"vReligion='"+firstTab.cmbReligion.getValue().toString()+"', " +
						"vContact='"+firstTab.txtContact.getValue().toString().trim()+"', " +
						"vEmail='"+firstTab.txtEmail.getValue().toString().trim()+"', " +
						"vGender='"+firstTab.cmbGender.getValue().toString()+"', " +
						"dDateOfBirth='"+(dateFormat.format(firstTab.dDateofBirth.getValue()).isEmpty()?"":dateFormat.format(firstTab.dDateofBirth.getValue()))+"'," +
						"DateOfBirthLocation='0', " +
						"vNationality='"+(firstTab.txtNationality.getValue().toString())+"', " +
						"nId='"+(firstTab.txtNid.getValue().toString().trim())+"', " +
						"nIdLocation='0', " +
						"vEmployeeType='"+firstTab.cmbEmpType.getValue().toString()+"', " +
						"dApplicationDate='"+(dateFormat.format(firstTab.dAppDate.getValue()).isEmpty()?"":dateFormat.format(firstTab.dAppDate.getValue()))+"', " +
						"applicationDateLocation='0', " +
						"dInterviewDate='"+(dateFormat.format(firstTab.dInterviewDate.getValue()).isEmpty()?"":dateFormat.format(firstTab.dInterviewDate.getValue()))+"', " +
						"dJoiningDate='"+(dateFormat.format(firstTab.dJoiningDate.getValue()).isEmpty()?"":dateFormat.format(firstTab.dJoiningDate.getValue()))+"', " +
						"joiningDateLocation='0', " +
						"dConfirmationDate='"+(firstTab.dConDate.getValue()==null?"":dateFormat.format(firstTab.dConDate.getValue()))+"', " +
						"confirmationDateLocation='0', " +
						"vStatus='"+firstTab.cmbStatus.getValue().toString()+"', " +
						"iStatus='"+empStatus+"', " +
						"dStatusDate='"+(dateFormat.format(firstTab.dDate.getValue()).isEmpty()?"":dateFormat.format(firstTab.dDate.getValue()))+"', " +
						"vDepartmentId='"+firstTab.cmbDepartment.getValue().toString()+"', " +
						"vSectionId='"+sectionId+"', " +
						//"vSectionId='' ," +
						"vDesignationId='"+firstTab.cmbDesignation.getValue().toString()+"', " +                 //" '"+firstTab.cmbDesignation.getValue().toString()+"', " +
						"vFloor='"+(firstTab.cmbShiftId.getValue()==null?0:Integer.parseInt(firstTab.cmbShiftId.getValue().toString()))+"', " +
						"vLine='0', " +
						"vGrade='0', " +          //" '"+firstTab.cmbGrade.getValue().toString()+"', " +			
						"imageLocation = '"+imagePathEmployee+"', "+     
						"vFatherName='"+(secondTab.txtFatherName.getValue().toString().trim().isEmpty()?"":secondTab.txtFatherName.getValue().toString().trim())+"', " +
						"vMotherName='"+(secondTab.txtMotherName.getValue().toString().trim().isEmpty()?"":secondTab.txtMotherName.getValue().toString().trim())+"', " +
						"vPermanentAddress='"+(secondTab.txtPerAddress.getValue().toString().trim().isEmpty()?"":secondTab.txtPerAddress.getValue().toString().trim())+"', " +
						"vMailingAddress='"+(secondTab.txtMailing.getValue().toString().trim().isEmpty()?"":secondTab.txtMailing.getValue().toString().trim())+"', " +
						"vBloodGroup='"+(secondTab.cmbBloodGroup.getValue()==null?"":secondTab.cmbBloodGroup.getValue().toString())+"', " +
						"vMeritalStatus='"+(secondTab.cmbMaritalStatus.getValue()==null?"":secondTab.cmbMaritalStatus.getValue().toString())+"', " +
						"dMarriageDate='"+(dateFormat.format(secondTab.DMarriageDate.getValue()).isEmpty()?"":dateFormat.format(secondTab.DMarriageDate.getValue()))+"', " +
						"vSpouseName='"+(secondTab.txtSpouseName.getValue().toString().trim().isEmpty()?"":secondTab.txtSpouseName.getValue().toString().trim())+"', " +
						"vSpouseOccupation='"+(secondTab.txtSpouseOccupation.getValue().toString().trim().isEmpty()?"":secondTab.txtSpouseOccupation.getValue().toString().trim())+"', " +
						"iNoOfChild='"+(secondTab.amntNumofChild.getValue().toString().trim().isEmpty()?"":secondTab.amntNumofChild.getValue().toString().trim())+"', " +
						"vN1Name='"+(secondTab.txtNomineeNane.getValue().toString().trim().isEmpty()?"":secondTab.txtNomineeNane.getValue().toString().trim())+"', " +
						"vN1Relation='"+(secondTab.txtRelation.getValue().toString().trim().isEmpty()?"":secondTab.txtRelation.getValue().toString().trim())+"', " +
						"vN2Name='"+(secondTab.txtNomineeName2.getValue().toString().trim().isEmpty()?"":secondTab.txtNomineeName2.getValue().toString().trim())+"', " +
						"vN2Relation='"+(secondTab.txtRelation2.getValue().toString().trim().isEmpty()?"":secondTab.txtRelation2.getValue().toString().trim())+"', " +
						"vOtherQualification='"+(thirdTab.txtOtherQualification.getValue().toString().trim().isEmpty()?"":thirdTab.txtOtherQualification.getValue().toString().trim())+"', " +
						"vComputerSkill='"+(thirdTab.txtComputerSkill.getValue().toString().trim().isEmpty()?"":thirdTab.txtComputerSkill.getValue().toString().trim())+"', " +
						"mMonthlySalary = '"+fifthTab.txtBasic.getValue().toString().replaceAll(",", "")+"', " +
						" birthImage = '"+imagePathBirth+"', "+
						" nidImage = '"+imagePathNid+"', "+
						" applicationImage = '"+imagePathApplication+"', "+
						" joiningImage = '"+imagePathJoin+"', "+
						" confirmImage = '"+imagePathCon+"', "+
						" mHouseRent = '"+(fifthTab.txtHouseRent.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtHouseRent.getValue().toString().replaceAll("#", ""))+"'," +

						//Ot Status Saved Into OtStatus
						" OtStatus = '"+OtStatus+"'," +
						" mMedicalAllowance = '"+(fifthTab.txtMedical.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtMedical.getValue().toString().replaceAll("#", ""))+"'," +
						" mConAllowance = '"+(fifthTab.txtCon.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtCon.getValue().toString().replaceAll("#", ""))+"'," +
						//						" mClinical = '"+(fifthTab.txtClinical.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtClinical.getValue().toString().replaceAll("#", ""))+"'," +

						//Attendence Bonus Saved Into mSpecial
						" mSpecial = '"+(fifthTab.txtAttBonus.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtAttBonus.getValue().toString().replaceAll("#", ""))+"'," +
						//						" mNonPractice = '"+(fifthTab.txtNonPractice.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtNonPractice.getValue().toString().replaceAll("#", ""))+"'," +

						// Gross Salary Saved into mOthersAllowance
						" mOthersAllowance = '"+(fifthTab.txtGross.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtGross.getValue().toString().replaceAll("#", ""))+"'," +
						" mProvidentFund = '"+(fifthTab.txtProvidentFund.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtProvidentFund.getValue().toString().replaceAll("#", ""))+"'," +
						//						" mRoomCharge = '"+(fifthTab.txtRoomCharge.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtRoomCharge.getValue().toString().replaceAll("#", ""))+"'," +
						//" mBreej = '"+(fifthTab.txtBreej.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtBreej.getValue().toString().replaceAll("#", ""))+"'," +
						" mKhichuri = '"+(fifthTab.txtInsurance.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtInsurance.getValue().toString().replaceAll("#", ""))+"'," +
						" mKFund = '"+(fifthTab.txtTax.getValue().toString().replaceAll("#", "").isEmpty()?"0.0":fifthTab.txtTax.getValue().toString().replaceAll("#", ""))+"'," +
						" bankId = '"+(fifthTab.cmbBankName.getValue()==null?"":fifthTab.cmbBankName.getValue().toString())+"'," +
						" bankName = '"+(fifthTab.cmbBankName.getValue()==null?"":fifthTab.cmbBankName.getItemCaption(fifthTab.cmbBankName.getValue().toString()))+"'," +
						" bankBranchId = '"+(fifthTab.cmbBranchName.getValue()==null?"":fifthTab.cmbBranchName.getValue().toString())+"'," +
						" branchName = '"+(fifthTab.cmbBranchName.getValue()==null?"":fifthTab.cmbBranchName.getItemCaption(fifthTab.cmbBranchName.getValue().toString()))+"'," +
						" accountNo = '"+(fifthTab.txtAccountNo.getValue().toString().isEmpty()?"":fifthTab.txtAccountNo.getValue().toString())+"', " +
						" FridayStatus ='"+fristatus+"'," +
						" FridayLunchFee ='"+(fifthTab.txtFridayLunch.getValue().toString().replace(",", "").isEmpty()?"0.0":fifthTab.txtFridayLunch.getValue().toString().replace(",", ""))+"'," +
						" mDearnessAllowance = '"+(fifthTab.txtDearnessAllowance.getValue().toString().replaceAll(",", "").isEmpty()?"0.0":fifthTab.txtDearnessAllowance.getValue().toString().replaceAll(",", ""))+"'," +
						" mFireAllowance = '"+(fifthTab.txtFireAllowance.getValue().toString().replaceAll(",", "").isEmpty()?"0.0":fifthTab.txtFireAllowance.getValue().toString().replaceAll(",", ""))+"', "+
						" vUserName='"+sessionBean.getUserName()+"',"+
						" dDateTime=getDate(),"+
						" vPcIp='"+sessionBean.getUserIp()+"',"+
						" userId='"+sessionBean.getUserName()+"',"+
						" userIp='"+sessionBean.getUserIp()+"',"+
						" entryTime=getDate(),"+
						" vMobileBankFlag = '"+fifthTab.BankFlag+"'," +
						" vTinNo = '"+(!firstTab.txtTINNo.getValue().toString().trim().isEmpty()?firstTab.txtTINNo.getValue().toString().trim():"0")+"'," +
						" vTinImageLocation = '"+imagePathTin+"'," +
						" vCircle = '"+(!firstTab.txtCircle.getValue().toString().trim().isEmpty()?firstTab.txtCircle.getValue().toString().trim():"0")+"'," +
						" vCircleImageLocation = '"+imagePathCircle+"'," +
						" vZone = '"+(!firstTab.txtZone.getValue().toString().trim().isEmpty()?firstTab.txtZone.getValue().toString().trim():"0")+"'," +
						" vZoneImageLocation = '"+imagePathZone+"'," +
						" vGrades = '"+(firstTab.cmbGrades.getValue()==null?"":firstTab.cmbGrades.getValue().toString())+"'," +
						" vGradesName = '"+(firstTab.cmbGrades.getValue()==null?"":firstTab.cmbGrades.getItemCaption(firstTab.cmbGrades.getValue().toString()))+"'" +
						" where vEmployeeId = '"+firstTab.txtEmployeeId.getValue().toString().trim()+"' ";

				session.createSQLQuery(query).executeUpdate();
				session.clear();

				if(firstTab.chkPhysicallyDesable.booleanValue())
				{
					String physicallyDisableCheckTable="select * from tbPhysicallyDisable where vEmployeeID='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'";

					List <?> lst=session.createSQLQuery(physicallyDisableCheckTable).list();
					if(!lst.isEmpty())
					{
						String physicallyDisable="update tbPhysicallyDisable set vEmployeeId='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'," +
								"vEmployeeCode='"+firstTab.txtEmployeeCode.getValue().toString().trim()+"'," +
								"vProximityID='"+(firstTab.txtProximityId.getValue().toString().isEmpty()?"":firstTab.txtProximityId.getValue().toString())+"'," +
								"vEmployeeName='"+firstTab.txtEmpName.getValue().toString().trim()+"'," +
								"iPhysicallyDisable='1' " +
								"where vEmployeeId='"+firstTab.txtEmployeeId.getValue().toString().trim()+"'";
						session.createSQLQuery(physicallyDisable).executeUpdate();
					}
					else
					{
						String physicallyDisable="insert into tbPhysicallyDisable (vEmployeeId,vEmployeeCode,vProximityID,vEmployeeName," +
								"iPhysicallyDisable,vUserName,vUserIP,dEntryTime) values " +
								"('"+firstTab.txtEmployeeId.getValue().toString().trim()+"', " +
								" '"+firstTab.txtEmployeeCode.getValue().toString().trim()+"', " +
								" '"+(firstTab.txtProximityId.getValue().toString().isEmpty()?"":firstTab.txtProximityId.getValue().toString())+"', " +
								" '"+firstTab.txtEmpName.getValue().toString().trim()+"'," +
								" '1'," +
								" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate())";
						session.createSQLQuery(physicallyDisable).executeUpdate();
					}
				}
				tx.commit();
				session.clear();
			}
		}
		catch(Exception ex)
		{
			showNotification("updateEmployeeInfo"+ex,Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	private void insertToOthersTable()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			for(int i=0;i<thirdTab.tblTxtExam.size();i++)
			{
				if(!thirdTab.tblTxtExam.get(i).getValue().toString().equals(""))
				{
					String sql="insert into tbEducation values" +
							"('"+firstTab.txtEmployeeId.getValue().toString()+"'," +
							" '"+thirdTab.tblTxtExam.get(i).getValue().toString().trim()+"'," +
							" '"+thirdTab.tblTxtGroup.get(i).getValue().toString().trim()+"'," +
							" '"+thirdTab.tblTxtInstitute.get(i).getValue().toString().trim()+"'," +
							" '"+thirdTab.tblTxtBoard.get(i).getValue().toString().trim()+"'," +
							" '"+thirdTab.tblTxtDivision.get(i).getValue().toString().trim()+"'," +
							" '"+dateYear.format(thirdTab.tblDateYear.get(i).getValue())+"'," +
							" '"+sessionBean.getUserName()+"'," +
							" CURRENT_TIMESTAMP," +
							" '"+sessionBean.getUserIp()+"')";

					session.createSQLQuery(sql).executeUpdate(); 
				}
			}

			for(int i=0;i<fourthTab.tblTxtPost.size();i++)
			{
				if(!fourthTab.tblTxtPost.get(i).getValue().toString().equals(""))
				{
					String sql = "insert into tbWorkingExperiance values"+
							"('"+firstTab.txtEmployeeId.getValue().toString()+"'," +
							"'"+fourthTab.tblTxtPost.get(i).getValue().toString().trim()+"'," +
							"'"+fourthTab.tblTxtCompanyName.get(i).getValue().toString().trim()+"'," +
							"'"+dateFormat.format(fourthTab.tblDateFrom.get(i).getValue())+"'," +
							"'"+dateFormat.format(fourthTab.tblDateTo.get(i).getValue())+"'," +
							"'"+fourthTab.tblTxtMajorTask.get(i).getValue().toString().trim() +"'," +
							" '"+sessionBean.getUserName()+"'," +
							" CURRENT_TIMESTAMP," +
							" '"+sessionBean.getUserIp()+"')";

					session.createSQLQuery(sql).executeUpdate(); 
				}
			}
			tx.commit();
		}
		catch(Exception ex)
		{
			showNotification(""+ex,Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	public void employeeImage(String img)
	{
		File  fileStu_I = new File(img);

		Embedded eStu_I = new Embedded("",new FileResource(fileStu_I, getApplication()));
		eStu_I.requestRepaint();
		eStu_I.setWidth("100px");
		eStu_I.setHeight("135px");

		firstTab.Image.image.removeAllComponents();
		firstTab.Image.image.addComponent(eStu_I);
	}

	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String employeeImage = null;

		if(flag==1)
		{
			// image move
			if(firstTab.Image.fileName.trim().length()>0)
				try 
			{
					if(firstTab.Image.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						fileMove(basePath+firstTab.Image.fileName.trim(),SessionBean.employeeImage+path+".jpg");
						employeeImage = SessionBean.employeeImage+path+".jpg";
					}
			}
			catch(IOException e) 
			{
				e.printStackTrace();
			}
			return employeeImage;
		}
		return null;
	}

	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp)
		{

		}
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


	private void updateOthersTable()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			if(!thirdTab.tblTxtExam.get(0).getValue().toString().equals(""))
			{
				String sqlDelete = "delete from tbEducation where vEmployeeId='"+cmbEmployeeName.getValue().toString()+"' ";
				session.createSQLQuery(sqlDelete).executeUpdate();

				for(int i=0;i<thirdTab.tblTxtExam.size();i++)
				{
					if(!thirdTab.tblTxtExam.get(i).getValue().toString().equals(""))
					{
						String sql = "insert into tbEducation values" +
								"('"+firstTab.txtEmployeeId.getValue().toString().trim()+"'," +
								" '"+thirdTab.tblTxtExam.get(i).getValue().toString().trim()+"'," +
								" '"+thirdTab.tblTxtGroup.get(i).getValue().toString().trim()+"'," +
								" '"+thirdTab.tblTxtInstitute.get(i).getValue().toString().trim()+"'," +
								" '"+thirdTab.tblTxtBoard.get(i).getValue().toString().trim()+"'," +
								" '"+thirdTab.tblTxtDivision.get(i).getValue().toString().trim()+"'," +
								" '"+dateYear.format(thirdTab.tblDateYear.get(i).getValue())+"'," +
								" '"+sessionBean.getUserName()+"'," +
								" CURRENT_TIMESTAMP," +
								" '"+sessionBean.getUserIp()+"')";
						session.createSQLQuery(sql).executeUpdate(); 
					}
				}
				session.clear();
			}

			if(!fourthTab.tblTxtPost.get(0).getValue().toString().equals(""))
			{
				String sqlDelete = "delete from tbWorkingExperiance where vEmployeeId='"+cmbEmployeeName.getValue().toString()+"' ";
				session.createSQLQuery(sqlDelete).executeUpdate();

				for(int i=0;i<fourthTab.tblTxtPost.size();i++)
				{
					if(!fourthTab.tblTxtPost.get(i).getValue().toString().equals(""))
					{
						String sql="insert into tbWorkingExperiance values"+
								"('"+firstTab.txtEmployeeId.getValue().toString().trim()+"'," +
								"'"+fourthTab.tblTxtPost.get(i).getValue().toString().trim()+"'," +
								"'"+fourthTab.tblTxtCompanyName.get(i).getValue().toString().trim()+"'," +
								"'"+dateFormat.format(fourthTab.tblDateFrom.get(i).getValue())+"'," +
								"'"+dateFormat.format(fourthTab.tblDateTo.get(i).getValue())+"'," +
								"'"+fourthTab.tblTxtMajorTask.get(i).getValue().toString().trim() +"'," +
								" '"+sessionBean.getUserName()+"'," +
								" CURRENT_TIMESTAMP," +
								" '"+sessionBean.getUserIp()+"')";
						session.createSQLQuery(sql).executeUpdate();
					}
				}
			}
			tx.commit();
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error yes ",ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	public void DepartmentLink()
	{
		Window win = new DepartmentInformation(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				addCmbDepartmentData();
			}
		});
		this.getParent().addWindow(win);
	}
	
	public void GradesLink()
	{
		Window win = new GradeIformation(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				addCmbGradeData();
			}
		});
		this.getParent().addWindow(win);
	}
	

	public void sectionLink()
	{
		Window win = new SectionInformation(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				addCmbSectionData("%");
			}
		});
		this.getParent().addWindow(win);
	}

	private void addCmbDepartmentData()
	{
		firstTab.cmbDepartment.removeAllItems();
		String query = "select vDepartmentID,vDepartmentName from tbDepartmentInfo where vDepartmentName!= 'CHO'";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				firstTab.cmbDepartment.addItem(element[0].toString());
				firstTab.cmbDepartment.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("addCmbDepartmentData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void addCmbSectionData(String Section)
	{
		firstTab.cmbSection.removeAllItems();
		String query = "select vSectionID,SectionName from tbSectionInfo where vDepartmentID='"+Section+"'";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				firstTab.cmbSection.addItem(element[0].toString());
				firstTab.cmbSection.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("addCmbSectionData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	public void designationLink()
	{
		Window win = new Designation(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				addCmbDesignationData();
			}
		});
		this.getParent().addWindow(win);
	}

	private void addCmbShiftData()
	{
		firstTab.cmbShiftId.removeAllItems();
		String query="select vShiftId,vShiftName from tbshiftinformation";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			Iterator <?> itr=session.createSQLQuery(query).list().iterator();
			while(itr.hasNext())
			{
				Object [] element=(Object[])itr.next();

				firstTab.cmbShiftId.addItem(element[0].toString());
				firstTab.cmbShiftId.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("addCmbShiftData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	private void addCmbGradeData()
	{
		firstTab.cmbShiftId.removeAllItems();
		String query="select vGradeId,vGradeName from tbGradeInfo";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			Iterator <?> itr=session.createSQLQuery(query).list().iterator();
			while(itr.hasNext())
			{
				Object [] element=(Object[])itr.next();

				firstTab.cmbGrades.addItem(element[0].toString());
				firstTab.cmbGrades.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("addCmbGradeData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}
	
	

	private void addCmbDesignationData()
	{
		firstTab.cmbDesignation.removeAllItems();
		String query = "select * from tbDesignationInfo";
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		List <?> list = session.createSQLQuery(query).list();
		try
		{
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				firstTab.cmbDesignation.addItem(element[1].toString());
				firstTab.cmbDesignation.setItemCaption(element[1].toString(), element[2].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("addCmbDesignationData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("870px");
		mainLayout.setHeight("100.0%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("910px");
		setHeight("600px");

		this.setResizable(false);

		cmbDepartmentName=new ComboBox();
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setWidth("230.0px");
		cmbDepartmentName.setHeight("22px");
		cmbDepartmentName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Department Name : "), "top:5.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartmentName, "top:30.0px; left:20.0px;");

		cmbSectionName=new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("240.0px");
		cmbSectionName.setHeight("22px");
		cmbSectionName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Section Name : "), "top:5.0px; left:270.0px");
		mainLayout.addComponent(cmbSectionName, "top:30.0px; left:270.0px");

		
		

		cmbEmployeeName = new ComboBox();
		cmbEmployeeName.setImmediate(true);
		cmbEmployeeName.setWidth("300px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeName, "top:30.0px;left:568.0px;");

		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(new Label("Status:"),"top:5.0px; left:380.0px");
		mainLayout.addComponent(RadioBtnStatus, "top:4.0px;left:420.0px;");

				lblFind = new Label();
				lblFind.setImmediate(false);
		/*		lblFind.setWidth("-1px");
				lblFind.setHeight("-1px");
		*/		lblFind.setValue("Find:");
				mainLayout.addComponent(lblFind, "top:5.0px;left:568.0px;");
				
				

				// optionGroup
						RadioBtnGroup = new OptionGroup("",type2);
						RadioBtnGroup.setImmediate(true);
						RadioBtnGroup.setStyleName("horizontal");
						RadioBtnGroup.setValue("Employee ID");
						mainLayout.addComponent(RadioBtnGroup, "top:4.0px;left:600.0px;");
				
		mainLayout.addComponent(button, "top:505.0px;left:210.0px;");

		tabSheet.setStyleName("tabsheet-content");
		mainLayout.addComponent(tabSheet, "top:60.0px;left:20.0px;");

		Label lblComment = new Label("Fields marked with <Font Color='#CD0606' size='3px'>(<b>*</b>)</Font> are mandatory",Label.CONTENT_XHTML);
		lblComment.setImmediate(true);
		lblComment.setWidth("-1px");
		lblComment.setHeight("-1px");
		mainLayout.addComponent(lblComment, "top:535.0px; left:30.0px;");

		return mainLayout;
	}

	public void tabInit(boolean t)
	{
		lblFind.setEnabled(!t);
		RadioBtnGroup.setEnabled(!t);
		cmbDepartmentName.setEnabled(!t);
		cmbSectionName.setEnabled(!t);
		cmbEmployeeName.setEnabled(!t);
		firstTab.mainLayout.setEnabled(t);
		secondTab.mainLayout.setEnabled(t);
		thirdTab.mainLayout.setEnabled(t);
		fourthTab.mainLayout.setEnabled(t);
		fifthTab.mainLayout.setEnabled(t);
	}

	private void textClear()
	{
		fifthTab.txtBasic.setReadOnly(false);
		fifthTab.txtHouseRent.setReadOnly(false);
		fifthTab.txtMedical.setReadOnly(false);
		fifthTab.txtCon.setReadOnly(false);
		fifthTab.txtAttBonus.setReadOnly(false);
		fifthTab.txtFridayLunch.setReadOnly(false);
		fifthTab.txtProvidentFund.setReadOnly(false);
		fifthTab.txtTax.setReadOnly(false);
		fifthTab.txtInsurance.setReadOnly(false);
		fifthTab.txtDearnessAllowance.setReadOnly(false);
		fifthTab.txtFireAllowance.setReadOnly(false);
		fifthTab.opgBank.setReadOnly(false);
		fifthTab.cmbBankName.setReadOnly(false);
		fifthTab.cmbBranchName.setReadOnly(false);
		fifthTab.txtAccountNo.setReadOnly(false);
		
		firstTab.btnDateofBirth.actionCheck = false;
		firstTab.btnBirthPreview.setCaption(".jpg/.pdf");
		firstTab.btnTINNOPreview.setCaption(".jpg/.pdf");
		firstTab.btnCirclePreview.setCaption(".jpg/.pdf");
		firstTab.btnZoneNO.setCaption(".jpg/.pdf");

		firstTab.btnNid.actionCheck = false;
		firstTab.btnNidPreview.setCaption(".jpg/.pdf");

		firstTab.btnAppDate.actionCheck = false;
		firstTab.btnApplicationPreview.setCaption(".jpg/.pdf");

		firstTab.btnJoiningDate.actionCheck = false;
		firstTab.btnJoinPreview.setCaption(".jpg/.pdf");

		firstTab.btnConfirmdate.actionCheck = false;
		firstTab.btnConPreview.setCaption(".jpg/.pdf");

		firstTab.txtEmployeeId.setValue("");
		firstTab.txtEmployeeCode.setValue("");
		firstTab.txtFingerId.setValue("");
		firstTab.txtProximityId.setValue("");
		firstTab.txtEmpName.setValue("");
		firstTab.chkOtEnable.setValue(false);

		firstTab.cmbReligion.setValue(null);
		firstTab.txtContact.setValue("");
		firstTab.txtEmail.setValue("");

		firstTab.cmbGender.setValue(null);
		firstTab.dDateofBirth.setValue(new java.util.Date());
		firstTab.txtNationality.setValue("Bangladeshi");
		firstTab.txtNid.setValue("");

		firstTab.cmbEmpType.setValue(null);
		firstTab.cmbSerType.setValue(null);
		firstTab.dAppDate.setValue(new java.util.Date());
		firstTab.dInterviewDate.setValue(new java.util.Date());
		firstTab.dJoiningDate.setValue(new java.util.Date());
		firstTab.dConDate.setValue(null);
		firstTab.dDate.setValue(new java.util.Date());
		firstTab.lblDateName.setCaption("");

		firstTab.cmbDepartment.setValue(null);
		firstTab.cmbSection.setValue(null);
		firstTab.cmbDesignation.setValue(null);
		firstTab.cmbShiftId.setValue(null);
		firstTab.Image.image.removeAllComponents();
		firstTab.chkFriEnable.setValue(false);
		firstTab.chkPhysicallyDesable.setValue(false);
		firstTab.txtTINNo.setValue("");
		firstTab.txtCircle.setValue("");
		firstTab.txtZone.setValue("");
		//firstTab.cmbGrades.setValue(null);
		firstTab.cmbStatus.setValue(null);
		//firstTab.cmbGrade.setValue(null);

		secondTab.txtFatherName.setValue("");
		secondTab.txtMotherName.setValue("");
		secondTab.txtPerAddress.setValue("");
		secondTab.txtMailing.setValue("");
		secondTab.cmbBloodGroup.setValue(null);
		secondTab.cmbMaritalStatus.setValue(null);
		secondTab.DMarriageDate.setValue(new java.util.Date());
		secondTab.txtSpouseName.setValue("");
		secondTab.txtSpouseOccupation.setValue("");
		secondTab.amntNumofChild.setValue("");
		secondTab.txtNomineeNane.setValue("");
		secondTab.txtRelation.setValue("");
		secondTab.txtNomineeName2.setValue("");
		secondTab.txtRelation2.setValue("");


		for(int i = 0;i<thirdTab.tblTxtExam.size();i++)
		{
			thirdTab.tblTxtExam.get(i).setValue("");
			thirdTab.tblTxtGroup.get(i).setValue("");
			thirdTab.tblTxtInstitute.get(i).setValue("");
			thirdTab.tblTxtBoard.get(i).setValue("");
			thirdTab.tblTxtDivision.get(i).setValue("");
		}

		thirdTab.txtOtherQualification.setValue("");
		thirdTab.txtComputerSkill.setValue("");

		for(int i = 0;i<fourthTab.tblTxtPost.size();i++)
		{
			fourthTab.tblTxtPost.get(i).setValue("");
			fourthTab.tblTxtCompanyName.get(i).setValue("");
			fourthTab.tblDateFrom.get(i).setValue(new java.util.Date());
			fourthTab.tblDateTo.get(i).setValue(new java.util.Date());
			fourthTab.tblTxtMajorTask.get(i).setValue("");
		}

		fifthTab.txtBasic.setValue("");
		fifthTab.txtHouseRent.setValue("");
		fifthTab.txtMedical.setValue("");
		fifthTab.txtCon.setValue("");
		fifthTab.txtAttBonus.setValue("");
		fifthTab.txtTax.setValue("");
		fifthTab.txtProvidentFund.setValue("");
		fifthTab.txtInsurance.setValue("");
		fifthTab.txtDearnessAllowance.setValue("");
		fifthTab.txtFireAllowance.setValue("");
		fifthTab.cmbBankName.setValue(null);
		fifthTab.cmbBranchName.setValue(null);
		fifthTab.txtAccountNo.setValue("");
		fifthTab.txtGross.setValue("");
		fifthTab.txtFridayLunch.setValue("");
		
	}

	public void focusMove()
	{
		allComp.add(firstTab.txtEmployeeCode);
		allComp.add(firstTab.txtFingerId);
		allComp.add(firstTab.txtProximityId);
		allComp.add(firstTab.txtEmpName);
		allComp.add(firstTab.cmbReligion);
		allComp.add(firstTab.txtContact);
		allComp.add(firstTab.txtEmail);
		allComp.add(firstTab.cmbGender);
		allComp.add(firstTab.txtNationality);
		allComp.add(firstTab.txtNid);
		allComp.add(firstTab.dDateofBirth);
		allComp.add(firstTab.cmbEmpType);
		allComp.add(firstTab.cmbSerType);
		allComp.add(firstTab.txtTINNo);
		allComp.add(firstTab.txtCircle);
		allComp.add(firstTab.dAppDate);
		allComp.add(firstTab.dInterviewDate);
		allComp.add(firstTab.dJoiningDate);
		allComp.add(firstTab.dConDate);
		allComp.add(firstTab.cmbStatus);
		allComp.add(firstTab.cmbDepartment);
		allComp.add(firstTab.cmbSection);
		allComp.add(firstTab.cmbDesignation);
		allComp.add(firstTab.cmbShiftId);
		allComp.add(firstTab.txtZone);
		allComp.add(secondTab.txtFatherName);
		allComp.add(secondTab.txtMotherName);
		allComp.add(secondTab.txtPerAddress);
		allComp.add(secondTab.txtMailing);
		allComp.add(secondTab.cmbBloodGroup);
		allComp.add(secondTab.cmbMaritalStatus);
		allComp.add(secondTab.DMarriageDate);
		allComp.add(secondTab.txtSpouseName);
		allComp.add(secondTab.txtSpouseOccupation);
		allComp.add(secondTab.amntNumofChild);
		allComp.add(secondTab.txtNomineeNane);
		allComp.add(secondTab.txtRelation);
		allComp.add(secondTab.txtNomineeName2);
		allComp.add(secondTab.txtRelation2);
		
		for(int i=0;i<tblTxtExam.size();i++)
		{
			
			allComp.add(tblTxtExam.get(i));
			allComp.add(tblTxtGroup.get(i));
			allComp.add(tblTxtInstitute.get(i));
			allComp.add(tblTxtBoard.get(i));
			allComp.add(tblTxtDivision.get(i));	
			allComp.add(tblDateYear.get(i));
		}
	
		allComp.add(thirdTab.txtOtherQualification);
		allComp.add(thirdTab.txtComputerSkill);
		allComp.add(fifthTab.txtGross);
		allComp.add(fifthTab.txtBasic);
		allComp.add(fifthTab.txtHouseRent);
		allComp.add(fifthTab.txtMedical);
		allComp.add(fifthTab.txtCon);
		allComp.add(fifthTab.txtAttBonus);
		allComp.add(fifthTab.txtFridayLunch);
		allComp.add(fifthTab.txtProvidentFund);	
		allComp.add(fifthTab.txtTax);
		allComp.add(fifthTab.txtInsurance);
		allComp.add(fifthTab.txtDearnessAllowance);
		allComp.add(fifthTab.txtFireAllowance);
		allComp.add(fifthTab.cmbBankName);
		allComp.add(fifthTab.cmbBranchName);
		allComp.add(fifthTab.txtAccountNo);
		new FocusMoveByEnter(this,allComp);
	}
}
