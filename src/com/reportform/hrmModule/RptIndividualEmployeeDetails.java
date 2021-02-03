package com.reportform.hrmModule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import com.common.share.CommonButton;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class RptIndividualEmployeeDetails extends Window {
	
		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		private OptionGroup radioButtonGroup;
		private OptionGroup opgReport;

		private ComboBox cmbDynamic;
		private ComboBox cmbSection;

		private CheckBox chkDynamic;
		private CheckBox chkSection;

		private Label lblComboLabel ;
		private OptionGroup RadioBtnStatus;
		private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
		private static final List<String> groupButton = Arrays.asList(new String[]{"Employee ID","Finger ID","Proximity ID","Employee Name"});
		private static final List<String> type1 = Arrays.asList(new String[]{"PDF","Other"});

		boolean isPreview=false;

		private ReportDate reportTime = new ReportDate();

		private CommonButton cButton= new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

		public RptIndividualEmployeeDetails(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setCaption("INDIVIDUAL EMPLOYEE DETAILS:: "+sessionBean.getCompany());
			this.setResizable(false);

			buildMainLayout();
			setBtnAction();
			setContent(mainLayout);
			cmbSectionData();
			cmbEmployeeNameDataAdd();
			cmbSection.focus();
		}

		private void setBtnAction()
		{
			cButton.btnPreview.addListener(new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event)
				{
					if(cmbSection.getValue()!=null || chkSection.booleanValue()==true)
					{
						if(cmbDynamic.getValue()!=null || chkDynamic.booleanValue()==true)
						{
							reportView();
						}
						else
						{
							getParent().showNotification("Please Select "+lblComboLabel.getValue().toString()+"", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						getParent().showNotification("Please Select Section", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			});

			cButton.btnExit.addListener(new ClickListener() 
			{	
				public void buttonClick(ClickEvent event) 
				{
					close();
				}
			});

			cmbSection.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbSection.getValue()!=null)
					{
						cmbEmployeeNameDataAdd();
					}
				}
			});

			chkDynamic.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					if(chkDynamic.booleanValue()==true)
					{	
						cmbDynamic.setValue(null);
						cmbDynamic.setEnabled(false);
					}
					else
					{
						cmbDynamic.setEnabled(true);
					}
				}
			});

			chkSection.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					cmbSectionAction();
					cmbEmployeeNameDataAdd();
				}
			});
			
			RadioBtnStatus.addListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					cmbEmployeeNameDataAdd();
					
				}
			});
			radioButtonGroup.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbEmployeeNameDataAdd();
				}
			});
		}

		private void cmbEmployeeNameDataAdd()
		{
			cmbDynamic.removeAllItems();

			String query="";
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


			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();

			try
			{
				if(cmbSection.getValue()==null)
				{

					query="select vEmployeeId,iFingerID,vProximityId,vEmployeeName,employeeCode from tbEmployeeInfo where iStatus like '"+status+"' and vDepartmentId !='DEPT10'";
				}
				else
				{
					query= " select vEmployeeId,iFingerID,vProximityId,vEmployeeName,employeeCode from tbEmployeeInfo " +
							" where vSectionId = '"+(chkSection.booleanValue()?"%":
								(cmbSection.getValue()!=null?cmbSection.getValue().toString():""))+"' and iStatus like '"+status+"' and vDepartmentId !='DEPT10' ";
				}
	         System.out.println("sumaa"+query);

	List <?> lst=session.createSQLQuery(query).list();

				if(!lst.isEmpty())
				{
					if(radioButtonGroup.getValue().toString().equals("Employee ID"))
					{
						for(Iterator <?> itr=lst.iterator();itr.hasNext();)
						{
							Object [] element=(Object[])itr.next();
							cmbDynamic.addItem(element[0]);
							cmbDynamic.setItemCaption(element[0], element[4].toString());
						}
					}

					if(radioButtonGroup.getValue().toString().equals("Finger ID"))
					{
						for(Iterator <?> itr=lst.iterator();itr.hasNext();)
						{
							Object [] element=(Object[])itr.next();
							cmbDynamic.addItem(element[0]);
							cmbDynamic.setItemCaption(element[0], element[1].toString());
						}
					}
					if(radioButtonGroup.getValue().toString().equals("Proximity ID"))
					{
						for(Iterator <?> itr=lst.iterator();itr.hasNext();)
						{
							Object [] element=(Object[])itr.next();
							cmbDynamic.addItem(element[0]);
							cmbDynamic.setItemCaption(element[0], element[2].toString());
						}
					}
					if(radioButtonGroup.getValue().toString().equals("Employee Name"))
					{
						for(Iterator <?> itr=lst.iterator();itr.hasNext();)
						{
							Object [] element=(Object[])itr.next();
							cmbDynamic.addItem(element[0]);
							cmbDynamic.setItemCaption(element[0], element[3].toString());
						}
					}
				}
			}
			catch (Exception exp)
			{
				showNotification("cmbEmployeeNameDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void cmbSectionAction()
		{
			if(cmbSection.getValue()==null)
			{	
				cmbDynamic.setValue(null);
				cmbDynamic.removeAllItems();
			}

			if(chkSection.booleanValue()==true)
			{	
				cmbSection.setValue(null);
				cmbSection.setEnabled(false);
				cmbDynamic.setValue(null);
			}

			else
			{
				cmbSection.setEnabled(true);
			}
		}

		private AbsoluteLayout buildMainLayout()
		{
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(false);
			mainLayout.setMargin(false);

			setWidth("500px");
			setHeight("350px");
			
			RadioBtnStatus = new OptionGroup("",status);
			RadioBtnStatus.setImmediate(true);
			RadioBtnStatus.setStyleName("horizontal");
			RadioBtnStatus.setValue("Active");
			mainLayout.addComponent(RadioBtnStatus, "top:10.0px;left:40.0px;");

			cmbSection=new ComboBox();
			cmbSection.setWidth("250.0px");
			cmbSection.setHeight("-1px");
			cmbSection.setImmediate(true);
			cmbSection.setInputPrompt("Section Name");
			mainLayout.addComponent(new Label("Section Name : "), "top:50.0px;left:30.0px;");
			mainLayout.addComponent(cmbSection, "top:48.0px;left:150.0px");

			chkSection = new CheckBox("All");
			chkSection.setHeight("-1px");
			chkSection.setWidth("-1px");
			chkSection.setImmediate(true);
			mainLayout.addComponent(chkSection,"top:48.0px;left:400.0px");

			radioButtonGroup = new OptionGroup("",groupButton);
			radioButtonGroup.setImmediate(true);
			radioButtonGroup.setValue("Employee ID");
			mainLayout.addComponent(radioButtonGroup, "top:88.0px; left:10.0px;");

			cmbDynamic = new ComboBox();
			cmbDynamic.setImmediate(true);
			cmbDynamic.setWidth("250.0px");
			cmbDynamic.setHeight("-1px");
			cmbDynamic.setInputPrompt("Employee");
			mainLayout.addComponent(cmbDynamic, "top:88.0px; left:150.0px;");

			chkDynamic = new CheckBox("All");
			chkDynamic.setHeight("-1px");
			chkDynamic.setWidth("-1px");
			chkDynamic.setImmediate(true);
			mainLayout.addComponent(chkDynamic, "top:88.0px; left:400.0px;");

			opgReport = new OptionGroup("",type1);
			opgReport.setImmediate(true);
			opgReport.setStyleName("horizontal");
			opgReport.setValue("PDF");
			mainLayout.addComponent(opgReport, "top:190.0px;left:200.0px;");

			cButton.btnPreview.setImmediate(true);
			mainLayout.addComponent(cButton, "top:240.0px; left:160.0px;");

			return mainLayout;
		}

		private void cmbSectionData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String sql="select distinct tei.vSectionId,tsi.SectionName from tbEmployeeInfo tei inner join  tbSectionInfo as tsi on tei.vSectionID=tsi.vSectionID where tsi.SectionName!='CHO' ";
				List <?> lst=session.createSQLQuery(sql).list();
				if(!lst.isEmpty())
				{
					Iterator <?> itr=lst.iterator();
					while(itr.hasNext())
					{
						Object[] element=(Object[])itr.next();
						cmbSection.addItem(element[0]);
						cmbSection.setItemCaption(element[0], element[1].toString());
					}
				}
			}
			catch(Exception exp)
			{
				showNotification("CmbSectionDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void reportView()
		{
			String section="";
			String employee="";

			ReportOption RadioBtn= new ReportOption(opgReport.getValue().toString());
			String query=null;

			try
			{
				HashMap <String, Object> hm = new HashMap <String, Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);

				section = (chkSection.booleanValue()?"%":cmbSection.getValue().toString());

				employee = (chkDynamic.booleanValue()?"%":cmbDynamic.getValue().toString());;

				/*query = " select * from [dbo].[funEmployeeDetails]('"+employee+"') as " +
						" fe left join tbEmpEducationInfo ee on " +
						" fe.vEmployeeId=ee.vEmployeeId left join " +
						" tbEmpNomineeInfo en on en.vEmployeeId=fe.vEmployeeId where vSectionId like '"+section+"' " +
						" order by iSectionSerial,iDesignationSerial,iPayScaleSerial,fe.vEmployeeId ";*/
				
				query="select tei.vEmployeeId,employeeCode,iFingerID,vProximityId,vEmployeeName,vEmployeeNameBan,vReligion," +
						"vContact,vEmail,vGender,dDateOfBirth,DateOfBirthLocation,vNationality,nId,nIdLocation,vEmployeeType," +
						"dApplicationDate,applicationDateLocation,dInterviewDate,joiningDateLocation,dConfirmationDate," +
						"confirmationDateLocation,vStatus,iStatus,dStatusDate,tei.vDepartmentId,tei.vSectionId,vDesignationId," +
						"vFloor,vLine,vGrade,imageLocation,vFatherName,vMotherName,vPermanentAddress,vMailingAddress," +
						"vBloodGroup,vMeritalStatus,dMarriageDate,vSpouseName,vSpouseOccupation,iNoOfChild,mMonthlySalary," +
						"mHouseRent,mMedicalAllowance,mClinical,mOthersAllowance,mSpecial,mDearnessAllowance,mFireAllowance,mRoomCharge,mProvidentFund,mKFund," +
						"mKhichuri,dApplicationDate,dInterviewDate,dJoiningDate,dConfirmationDate,tedi.vNameOfExam," +
						"tedi.vGroup_Subject,tedi.vNameOfInstitution,tedi.vYearOfPassing,tedi.vDivision_Class_Grade," +
						"tedi.vBoard_University,tdi.designationName,tdpti.vDepartmentName,tsi.SectionName,tsfi.vShiftName,tei.vGradesName" +
						" from tbEmployeeInfo tei left join tbEducation as tedi on tedi.vEmployeeId=tei.vEmployeeId" +
						" inner join tbDesignationInfo as tdi on tdi.designationId=tei.vDesignationId" +
						" inner join tbDepartmentInfo as tdpti on tdpti.vDepartmentId=tei.vDepartmentId " +
						"inner join tbSectionInfo as tsi on tsi.vSectionID=tei.vSectionId" +
						" left join tbshiftInformation as tsfi on tsfi.vShiftId=tei.vFloor where " +
						"tei.vEmployeeId like '"+employee+"' and tei.vSectionId like '"+section+"' order by tdi.designationSerial" +
						",tei.vEmployeeId";

/*			query = "select fe.vEmployeeId,fe.vEmployeeCode,fe.vFingerId,fe.vProximityId,fe.vEmployeeName,fe.vContactNo," +
						"fe.vEmailAddress,fe.vDesignation,fe.vSectionName,fe.vEmployeePhoto,fe.vFatherName,fe.vMotherName," +
						"fe.dDateOfBirth,fe.vPermanentAddress,fe.vPresentAddress,fe.vReligion,fe.vGender,fe.vNationality," +
						"fe.vMaritalStatus,fe.vMarriageDate,fe.vSpouseName,fe.vSpouseOccupation,fe.iNumberOfChild,fe.mBasic," +
						"fe.mHouseRent,fe.mMedicalAllowance,fe.mClinicalAllowance,fe.mConveyanceAllowance," +
						"fe.mNonPracticeAllowance,fe.mOtherAllowance,fe.mDearnessAllowance,fe.mSpecialAllowance," +
						"fe.mAttendanceBonus,fe.mRoomCharge,fe.mProvidentFund,fe.mKallanFund,fe.mIncomeTax,fe.mKhichuriMeal," +
						"fe.dApplicationDate,fe.dInterviewDate,fe.dJoiningDate,fe.vConfirmationDate,ee.vExamName,ee.vGroupName," +
						"ee.vInstituteName,ee.vPassingYear,ee.vGradePoint,ee.vComputerSkill,ee.vOtherQualification from " +
						"[dbo].[funEmployeeDetails]('"+employee+"') as fe left join tbEmpEducationInfo ee on fe.vEmployeeId=ee.vEmployeeId " +
						"left join tbEmpNomineeInfo en on en.vEmployeeId=fe.vEmployeeId where vSectionId like '"+section+"' order by " +
						"iSectionSerial,iDesignationSerial,iPayScaleSerial,fe.vEmployeeId";*/
				
				System.out.println(query);
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/RptIndividualEmployeeDetails.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}

			catch(Exception exp)
			{
				this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				System.out.println(exp);
			}
		}
	}
