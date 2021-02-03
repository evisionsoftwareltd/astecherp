package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import acc.appform.hrmModule.AuditMonthlySalaryGenerate;
import acc.appform.hrmModule.DeleteFestivalBonus;
import acc.appform.hrmModule.DeleteMonthlySalaryCHO;
import acc.appform.hrmModule.EditIndividualEmployeeAttendanceMonthly;
import acc.appform.hrmModule.EditMonthlySalary;
import acc.appform.hrmModule.EditMonthlySalaryCHO;
import acc.appform.hrmModule.EmployeeAttendanceUploadSingleDevice;
import acc.appform.hrmModule.EmployeeInformation_CHO;
import acc.appform.hrmModule.FestivalBonus;
import acc.appform.hrmModule.FestivalBonusCHO;
import acc.appform.hrmModule.IncrementProcess;
import acc.appform.hrmModule.IncrementProcessCHO;

import acc.appform.hrmModule.DeleteFestivalBonus_CHO;
import acc.appform.hrmModule.Delete_Monthly_Salary;
import acc.appform.hrmModule.EditEmployeeAttendanceCHO;
import acc.appform.hrmModule.EditIndividualEmployeeAttendanceMonthly_CHO;
import acc.appform.hrmModule.GradeIformation;
import acc.appform.hrmModule.IncrementType;
import acc.appform.hrmModule.LayOffOption;
import acc.appform.hrmModule.LeaveApplicationCHO;
import acc.appform.hrmModule.LeaveCancleFormCHO;
import acc.appform.hrmModule.LeaveClosing;
import acc.appform.hrmModule.LeaveEncashment;
import acc.appform.hrmModule.LeaveEncashmentCHO;
import acc.appform.hrmModule.LeavePolicy;
import acc.appform.hrmModule.Designation;
import acc.appform.hrmModule.EmployeeInformation;
import acc.appform.hrmModule.LeaveBalance;
import acc.appform.hrmModule.LeaveApplication;
import acc.appform.hrmModule.LeaveType;
import acc.appform.hrmModule.LoanApplicationForm;
import acc.appform.hrmModule.LoanApplicationFormCHO;
import acc.appform.hrmModule.LoanRecoveryForm;
import acc.appform.hrmModule.LoanRecoveryFormCHO;
import acc.appform.hrmModule.MonthlyAdjustmentNDeduction;
import acc.appform.hrmModule.MonthlyAdjustmentNDeduction_CHO;
import acc.appform.hrmModule.MonthlySalaryGenerate;
import acc.appform.hrmModule.EmployeeAttendanceUploadIn;
import acc.appform.hrmModule.DeleteMonthlySalary;
import acc.appform.hrmModule.DesignationSerialInfo;
import acc.appform.hrmModule.EditEmployeeAttendance;
import acc.appform.hrmModule.EditEmployeeAttendanceIDoubleShift;
import acc.appform.hrmModule.IDoubleShiftDeclare;
import acc.appform.hrmModule.LeaveCancleForm;
import acc.appform.hrmModule.MonthlySalaryGenerateCHO;
import acc.appform.hrmModule.NewEmployeeList;
import acc.appform.hrmModule.OTNFridayEnable;
import acc.appform.hrmModule.OTNFridayEnableCHO;

import com.reportform.hrmModule.RptDailyPunchReportCHO;
import com.reportform.hrmModule.RptDateWiseEmployeeAttendanceEdit;
import com.reportform.hrmModule.RptMonthWiseOt_CHO;

import acc.appform.hrmModule.SectionWiseAbsent;
import acc.appform.hrmModule.SectionWiseActiveInactiveEmployee;
import acc.appform.hrmModule.SectionWiseAttendance;
import acc.appform.hrmModule.SectionWiseAttendance_CHO;
import acc.appform.hrmModule.SectionWiseDateBetweenEmployeeAttendance;
import acc.appform.hrmModule.SectionWiseDateBetweenEmployeeAttendance_CHO;
import acc.appform.hrmModule.SectionWiseDeleteProximityID;
import acc.appform.hrmModule.SectionWiseLeave;
import acc.appform.hrmModule.ShiftAssign;
import acc.appform.hrmModule.TourApplicationForm;
import acc.appform.hrmModule.TourApplicationFormCHO;
import acc.appform.hrmModule.accessGeneralReport;
import acc.appform.hrmModule.accessLeave;
import acc.appform.hrmModule.accessLeaveReport;
import acc.appform.hrmModule.accessLoan;
import acc.appform.hrmModule.accessLoanReport;
import acc.appform.hrmModule.accessOtReport;
import acc.appform.hrmModule.accessOverTime;
import acc.appform.hrmModule.accessSalaryReport;
import acc.appform.hrmModule.addExtraOrLessOTHour;
import acc.appform.hrmModule.holidayDeclare;
import acc.appform.hrmModule.hrmMaster;
import acc.appform.hrmModule.hrmReports;
import acc.appform.hrmModule.hrmTransaction;
import acc.appform.hrmModule.individualLeaveEntitlement;
import acc.appform.hrmModule.individualLeaveEntitlementCHO;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.reportform.hrmModule.*;
import com.share.accessModule.accessMonthlySalary;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class HrmMenu 
{
	Tree tree;
	SessionBean sessionBean;
	Component component;

	private HashMap <String,String> winMap = new HashMap <String,String> ();
	private static final Object CAPTION_PROPERTY = "caption";

	Object master = null;

	Object transaction = null;
	Object salary = null;
	Object ot = null;
	Object leave = null;
	Object loan = null;
    Object hrmchoSetup=null;
    Object hrmchoReport=null;
	Object reports = null;
	Object reportsGeneral = null;
	Object reportsSalary = null;
	Object attendanceReport = null;
	Object reportsOt = null;
	Object reportsLeave = null;
	Object reportsLoan = null;

	public HrmMenu(Object hrmModule, Tree tree, SessionBean sessionBean,Component component) 
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("hrmMaster"))
		{
			master = addCaptionedItem("HRM MASTER SETUP", hrmModule);
			addMasterChild(master);
		}

		if(isValidMenu("hrmTransection"))
		{
			transaction = addCaptionedItem("HRM TRANSACTION", hrmModule);
			addTransectionChild(transaction);
		}

		if(isValidMenu("hrmReports"))
		{
			reports = addCaptionedItem("HRM REPORTS", hrmModule);
			addReportsChild(reports);
		}
		if(isValidMenu("hrmcho"))
		{
			 hrmchoSetup= addCaptionedItem("HRM CHO SETUP", hrmModule);
			  addHrmCHOChild(hrmchoSetup);
		}
		if(isValidMenu("hrmchoreport"))
		{
			 hrmchoReport= addCaptionedItem("HRM CHO REPORT", hrmModule);
			  addHrmCHOReportChild(hrmchoReport);
		}
	}

	private void addHrmCHOReportChild(Object hrmchoreport) {
		if(isValidMenu("IndividualEmployeeDetails_CHO"))
		{
			addCaptionedItem("INDIVIDUAL EMPLOYEE DETAILS_CHO", hrmchoreport);
		}
	/*	if(isValidMenu("employeeOfficialInformation_CHO"))
		{
			addCaptionedItem("EMPLOYEE (OFFICIAL) INFORMATION_CHO", hrmchoreport);
		}*/
		if(isValidMenu("employeeListSectionWise_CHO"))
		{
			addCaptionedItem("EMPLOYEE LIST(SECTION WISE_CHO)", hrmchoreport);
		}
		if(isValidMenu("editemployeeinformation_CHO"))
		{
			addCaptionedItem("EDIT EMPLOYEE INFORMATION_CHO", hrmchoreport);
		}
		if(isValidMenu("appointmentletter_CHO"))
		{
			addCaptionedItem("APPLICATION AND APPOINTMENT LETTER_CHO", hrmchoreport);
		}
		if(isValidMenu("LengthOfServiceWithGross_CHO"))
		{
			addCaptionedItem("LENGTH OF SERVICE WITH GROSS_CHO", hrmchoreport);
		}
		if(isValidMenu("otNFridayEnableRpt_CHO"))
		{
			addCaptionedItem("OT & FRIDAY ENABLE_CHO.", hrmchoreport);
		}
		if(isValidMenu("dailypunchreport_CHO"))
		{
			addCaptionedItem("DAILY PUNCH REPORT_CHO", hrmchoreport);
		}
		if(isValidMenu("monthlyattendancecho"))
		{
			addCaptionedItem("MONTHLY ATTENDANCE (CHO)",hrmchoreport);
		}
		if(isValidMenu("salaryStructure_CHO"))
		{
			addCaptionedItem("SALARY STRUCTURE_CHO", hrmchoreport);
		}
		if(isValidMenu("salaryIncrementProposalCHO"))
		{
			addCaptionedItem("INCREMENT PROPOSAL_CHO",hrmchoreport);
		}
		if(isValidMenu("AllIncrementListCHO"))
		{
			addCaptionedItem("ALL INCREMENT LIST_CHO",hrmchoreport);
		}
		
		if(isValidMenu("IndividualIncrementHistoryCHO"))
		{
			addCaptionedItem("INDIVIDUAL INCREMENT HISTORY_CHO",hrmchoreport);
		}
		if(isValidMenu("MonthlySalaryCHO"))
		{
			addCaptionedItem("MONTHLY SALARY REPORT_CHO",hrmchoreport);
		}
		if(isValidMenu("editMonthlySalaryReport_CHO"))
		{
			addCaptionedItem("EDIT MONTHLY SALARY REPORT_CHO", hrmchoreport);
		}
		if(isValidMenu("monthlySalaryComparison_CHO"))
		{
			addCaptionedItem("MONTHLY SALARY COMPARISON_CHO", hrmchoreport);
		}
		if(isValidMenu("comparativeSalarySummary_CHO"))
		{
			addCaptionedItem("COMPARATIVE SALARY SUMMARY_CHO", hrmchoreport);
		}
		if(isValidMenu("EmployeeCoinAnalysis_CHO"))
		{
			addCaptionedItem("EMPLOYEE WISE COIN ANALYSIS_CHO", hrmchoreport);
		}
		if(isValidMenu("festivalBonusReport_CHO"))
		{
			addCaptionedItem("FESTIVAL BONUS REPORT_CHO", hrmchoreport);
		}
		if(isValidMenu("DeleteMonthlySalaryCHO"))
		{
			addCaptionedItem("DELETE MONTHLY SALARY REPORT_CHO", hrmchoreport);
		}
		if(isValidMenu("monthlyFridayAllowance_CHO"))
		{
			addCaptionedItem("MONTHLY FRIDAY ALLOWANCE_CHO",hrmchoreport);
		}
		if(isValidMenu("sectionCoinAnalysis_CHO"))
		{
			addCaptionedItem("SECTION WISE COIN ANALYSIS_CHO",hrmchoreport);
		}
		if(isValidMenu("bankStatement_CHO"))
		{
			addCaptionedItem("BANK ADVICE WITH FORWARDING LETTER_CHO", hrmchoreport);
		}
		if(isValidMenu("monthlyPaySlipCHO"))
		{
			addCaptionedItem("MONTHLY PAY SLIP_CHO",hrmchoreport);
		}
		if(isValidMenu("monthWiseOt_CHO"))
		{
			addCaptionedItem("MONTH WISE OT_CHO", hrmchoreport);
		}
		if(isValidMenu("individualLoanStatementCHO"))
		{
			addCaptionedItem("INDIVIDUAL LOAN STATEMENT_CHO",hrmchoreport);
		}
		if(isValidMenu("rptTourApplicationCHO"))
		{
			addCaptionedItem("TOUR APPLICATION_CHO", hrmchoreport);
		}
		if(isValidMenu("leaveApplicationCHO"))
		{
			addCaptionedItem("LEAVE APPLICATION_CHO", hrmchoreport);
		}
		if(isValidMenu("leaveRegisterCHO"))
		{
			addCaptionedItem("LEAVE REGISTER_CHO", hrmchoreport);
		}

		if(isValidMenu("leaveRegisterIndividualCHO"))
		{
			addCaptionedItem("INDIVIDUAL LEAVE REGISTER_CHO",hrmchoreport);
		}

/*		if(isValidMenu("RptleaveCancelFormCHO"))
		{
			addCaptionedItem("LEAVE CANCEL FORM_CHO", hrmchoreport);
		}
*/
		
		if(isValidMenu("rptLeaveEncashmentCHO"))
		{
			addCaptionedItem("LEAVE ENCASHMENT REPORT_CHO",hrmchoreport);
		}
		if(isValidMenu("laonApplicationCHO"))
		{
			addCaptionedItem("LOAN APPLICATION_CHO",hrmchoreport);
		}
		if(isValidMenu("loanRegisterCHO"))
		{
			addCaptionedItem("LOAN REGISTER_CHO",hrmchoreport);
		}

	}

	private void addHrmCHOChild(Object hrmchosetup) 
	{
		if(isValidMenu("employeeInformation_CHO"))
		{
			addCaptionedItem("EMPLOYEE INFORMATION_CHO", hrmchosetup);
		}
		if(isValidMenu("ot&FridayEnable_CHO"))
		{
			addCaptionedItem("OT & FRIDAY ENABLE_CHO", hrmchosetup);
		}
		if(isValidMenu("attendanceTimeEntrySingleDevice"))
		{
			addCaptionedItem("EMPLOYEE ATTENDANCE UPLOAD (SINGLE DEVICE)", hrmchosetup);
		}
		if(isValidMenu("editAttendanceIndividual_CHO"))
		{
			addCaptionedItem("EDIT EMPLOYEE ATTENDANCE (INDIVIDUAL)_CHO", hrmchosetup);
		}
		if(isValidMenu("dateBetweenEmployeeAttendance_CHO"))
		{
			addCaptionedItem("EDIT EMPLOYEE ATTENDANCE (DATE BETWEEN)_CHO", hrmchosetup);
		}
		if(isValidMenu("editAttendanceCHO"))
		{
			addCaptionedItem("EDIT EMPLOYEE ATTENDANCE_CHO", hrmchosetup);
		}
		if(isValidMenu("sectionWiseAttendanceManually_CHO"))
		{
			addCaptionedItem("SECTION WISE ATTENDANCE MANUALLY_CHO", hrmchosetup);
		}
		if(isValidMenu("monthlyAdjustmentNDeduction_CHO"))
		{
			addCaptionedItem("MONTHLY ADJUSTMENT & DEDUCTION_CHO", hrmchosetup);
		}
		if(isValidMenu("salaryIncrementProcess_CHO"))
		{
			addCaptionedItem("INCREMENT PROCESS_CHO", hrmchosetup);
		}
		if(isValidMenu("festivalBonus_CHO"))
		{
			addCaptionedItem("FESTIVAL BONUS_CHO",hrmchosetup);
		}
		if(isValidMenu("generateMonthlySalaryCHO"))
		{
			addCaptionedItem("GENERATE MONTHLY SALARY_CHO", hrmchosetup);
		}
		if(isValidMenu("deleteMonthlySalaryCHO"))
		{
			addCaptionedItem("DELETE MONTHLY SALARY_CHO", hrmchosetup);
		}
		if(isValidMenu("DeleteFestivalBonus_CHO"))
		{
			addCaptionedItem("DELETE FESTIVAL BONUS_CHO", hrmchosetup);
		}
			
		if(isValidMenu("leaveApplicationFormCHO"))
		{
			addCaptionedItem("LEAVE APPLICATION FORM_CHO",hrmchosetup);
		}
		if(isValidMenu("individualLeaveEntitlementCHO"))
		{
			addCaptionedItem("INDIVIDUAL LEAVE ENTITLEMENT_CHO",hrmchosetup);
		}
		if(isValidMenu("leaveCancelFormCHO"))
		{
			addCaptionedItem("LEAVE CANCEL FORM_CHO",hrmchosetup);
		}
		if(isValidMenu("tourApplicationFormCHO"))
		{
			addCaptionedItem("TOUR APPLICATION FORM_CHO", hrmchosetup);
		}
		
		if(isValidMenu("leaveEncashmentCHO"))
		{
			addCaptionedItem("LEAVE ENCASHMENT_CHO", hrmchosetup);
		}
		
		if(isValidMenu("loanApplicationFormCHO"))
		{
			addCaptionedItem("LOAN APPLICATION FORM_CHO", hrmchosetup);
		}
		if(isValidMenu("loanRecoveryFormCHO"))
		{
			addCaptionedItem("LOAN RECOVERY FORM_CHO", hrmchosetup);
		}
	}

	private void addMasterChild(Object master)
	{
		if(isValidMenu("designationInformation"))
		{
			addCaptionedItem("DESIGNATION INFORMATION", master);
		}

		if(isValidMenu("designationSerial"))
		{
			addCaptionedItem("DESIGNATION SERIAL", master);
		}
		if(isValidMenu("gradeInformation"))
		{
			addCaptionedItem("GRADE INFORMATION", master);
		}

		if(isValidMenu("employeeInformation"))
		{
			addCaptionedItem("EMPLOYEE INFORMATION", master);
		}
		
	/*	if(isValidMenu("employeeInformation_CHO"))
		{
			addCaptionedItem("EMPLOYEE INFORMATION_CHO", master);
		}*/

		if(isValidMenu("activeInactiveEmployee"))
		{
			addCaptionedItem("ACTIVE/INACTIVE EMPLOYEE", master);
		}

		if(isValidMenu("deleteProximityCard"))
		{
			addCaptionedItem("DELETE PROXIMITY ID/CARD NO", master);
		}

		if(isValidMenu("ot&FridayEnable"))
		{
			addCaptionedItem("OT & FRIDAY ENABLE", master);
		}

		if(isValidMenu("shiftAssign"))
		{
			addCaptionedItem("SHIFT ASSIGN", master);
		}

		if(isValidMenu("iDoubleShiftAssign"))
		{
			addCaptionedItem("I DOUBLE/RAMADAN SHIFT ASSIGN", master);
		}
		
		if(isValidMenu("IncrementType"))
		{
			addCaptionedItem("INCREMENT TYPE", master);
		}
		
	}

	private void addTransectionChild(Object transaction)
	{
		if(isValidMenu("accessOverTime"))
		{
			ot = addCaptionedItem("ATTENDANCE TIME", transaction);
			addOtChild(ot);
		}
		if(isValidMenu("accessMonthlySalary"))
		{
			salary = addCaptionedItem("MONTHLY SALARY", transaction);
			addSalaryChild(salary);
		}
		if(isValidMenu("accessLeave"))
		{
			leave = addCaptionedItem("LEAVE", transaction);
			addLeaveChild(leave);
		}
		if(isValidMenu("accessLoan"))
		{
			loan = addCaptionedItem("LOAN", transaction);
			addLoanChild(loan);
		}
	}

	private void addLoanChild(Object loan)
	{
		if(isValidMenu("loanApplicationForm"))
		{
			addCaptionedItem("LOAN APPLICATION FORM", loan);
		}

		if(isValidMenu("loanRecoveryForm"))
		{
			addCaptionedItem("LOAN RECOVERY FORM", loan);
		}
	}

	private void addLeaveChild(Object leave)
	{
		if(isValidMenu("leaveType"))
		{
			addCaptionedItem("LEAVE TYPE",leave);
		}

		if(isValidMenu("leavePolicy"))
		{
			addCaptionedItem("LEAVE POLICY",leave);
		}
		
		if(isValidMenu("leaveOpeningBalance"))
		{
			addCaptionedItem("LEAVE OPENING BALANCE",leave);
		}
		
		if(isValidMenu("individualLeaveEntitlement"))
		{
			addCaptionedItem("INDIVIDUAL LEAVE ENTITLEMENT",leave);
		}

		if(isValidMenu("leaveApplicationForm"))
		{
			addCaptionedItem("LEAVE APPLICATION FORM",leave);
		}

		if(isValidMenu("leaveCancelForm"))
		{
			addCaptionedItem("LEAVE CANCEL FORM", leave);
		}

		if(isValidMenu("sectionWiseLeave"))
		{
			addCaptionedItem("SECTION WISE LEAVE", leave);
		}

		if(isValidMenu("tourApplicationForm"))
		{
			addCaptionedItem("TOUR APPLICATION FORM", leave);
		}

		/*if(isValidMenu("leaveClosingBalance"))
		{
			addCaptionedItem("LEAVE CLOSING BALANCE",leave);
		}*/
		
		if(isValidMenu("leaveEncashment"))
		{
		  addCaptionedItem("LEAVE ENCASHMENT", leave);
		}
	}

	private void addSalaryChild(Object salary)
	{
		if(isValidMenu("holidyDeclaration"))
		{
			addCaptionedItem("HOLIDAY DECLARATION", salary);
		}

		if(isValidMenu("monthlyAdjustmentNDeduction"))
		{
			addCaptionedItem("MONTHLY ADJUSTMENT & DEDUCTION", salary);
		}

		if(isValidMenu("LayOffOption"))
		{
			addCaptionedItem("LAYOFF ENTRY FORM", salary);
		}

		if(isValidMenu("salaryIncrementProcess"))
		{
			addCaptionedItem("INCREMENT PROCESS", salary);
		}
		
/*		if(isValidMenu("salaryIncrementProcess_CHO"))
		{
			addCaptionedItem("INCREMENT PROCESS_CHO", salary);
		}*/
		
		if(isValidMenu("generateMonthlySalary"))
		{
			addCaptionedItem("GENERATE MONTHLY SALARY", salary);
		}

		if(isValidMenu("auditgenerateMonthlySalary"))
		{
			addCaptionedItem("GENERATE_MONTHLY_SALARY", salary);
		}

		if(isValidMenu("deleteMonthlySalary"))
		{
			addCaptionedItem("DELETE MONTHLY SALARY", salary);
		}

		if(isValidMenu("delete_Monthly_Salary"))
		{
			addCaptionedItem("DELETE_MONTHLY_SALARY", salary);
		}
		
/*		if(isValidMenu("deleteMonthlySalary_CHO"))
		{
			addCaptionedItem("DELETE MONTHLY SALARY_CHO", salary);
		}
*/
		if(isValidMenu("editMonthlySalary"))
		{
			addCaptionedItem("EDIT MONTHLY SALARY", salary);
		}
/*		
		if(isValidMenu("editMonthlySalary_CHO"))
		{
			addCaptionedItem("EDIT MONTHLY SALARY_CHO", salary);
		}*/

		if(isValidMenu("festivalBonus"))
		{
			addCaptionedItem("FESTIVAL BONUS", salary);
		}
		
	/*	if(isValidMenu("festivalBonus_CHO"))
		{
			addCaptionedItem("FESTIVAL BONUS_CHO", salary);
		}
		*/
		if(isValidMenu("DeleteFestivalBonus"))
		{
			addCaptionedItem("DELETE FESTIVAL BONUS", salary);
		}
			
	}

	private void addOtChild(Object ot)
	{
		if(isValidMenu("attendanceTimeEntry"))
		{
			addCaptionedItem("EMPLOYEE ATTENDANCE UPLOAD", ot);
		}
	/*	if(isValidMenu("attendanceTimeEntrySingleDevice"))
		{
			addCaptionedItem("EMPLOYEE ATTENDANCE UPLOAD (SINGLE DEVICE)", ot);
		}*/
		if(isValidMenu("editAttendance"))
		{
			addCaptionedItem("EDIT EMPLOYEE ATTENDANCE", ot);
		}
		if(isValidMenu("editAttendanceIndividual"))
		{
			addCaptionedItem("EDIT EMPLOYEE ATTENDANCE (INDIVIDUAL)", ot);
		}
		if(isValidMenu("dateBetweenEmployeeAttendance"))
		{
			addCaptionedItem("EDIT EMPLOYEE ATTENDANCE (DATE BETWEEN)", ot);
		}
		if(isValidMenu("editEmployeeAttendanceDoubleShift"))
		{
			addCaptionedItem("EDIT EMPLOYEE ATTENDANCE (DOUBLE SHIFT)", ot);
		}
		if(isValidMenu("sectionWiseAttendance"))
		{
			addCaptionedItem("SECTION WISE ATTENDANCE MANUALLY", ot);
		}
		if(isValidMenu("SectionWiserAbsent"))
		{
			addCaptionedItem("SECTION WISE ABSENT AS PUNISHMENT", ot);
		}
		if(isValidMenu("addOrLessExtraOT"))
		{
			addCaptionedItem("ADD/LESS EXTRA OT HOUR", ot);
		}
	}

	private void addReportsChild(Object reports)
	{
		if(isValidMenu("rptGeneralReport"))
		{
			reportsGeneral = addCaptionedItem("GENERAL REPORT", reports);
			AddGeneralReportChild(reportsGeneral);
		}

		if(isValidMenu("rptAttendanceReport"))
		{
			attendanceReport = addCaptionedItem("ATTENDANCE REPORT", reports);
			AddAttendanceReportChild(attendanceReport);
		}

		if(isValidMenu("rptSalaryReport"))
		{
			reportsSalary = addCaptionedItem("SALARY REPORT", reports);
			AddSalaryReportChild(reportsSalary);
		}

		if(isValidMenu("rptotReport"))
		{
			reportsOt = addCaptionedItem("OT REPORT", reports);
			AddOtReportChild(reportsOt);
		}

		if(isValidMenu("rptleaveReport"))
		{
			reportsLeave = addCaptionedItem("LEAVE REPORT", reports);
			AddLeaveReportChild(reportsLeave);
		}

		if(isValidMenu("rptloanReport"))
		{
			reportsLoan = addCaptionedItem("LOAN REPORT", reports);
			AddLoanReportChild(reportsLoan);
		}
	}

	private void AddGeneralReportChild(Object reportsGeneral)
	{
		if(isValidMenu("employeeCV"))
		{
			addCaptionedItem("CV(CURRICULUM VITAE)",reportsGeneral);
		}

		if(isValidMenu("employeeList"))
		{
			addCaptionedItem("EMPLOYEE LIST", reportsGeneral);
		}
		if(isValidMenu("NewEmployeeList"))
		{
			addCaptionedItem("NEW EMPLOYEE LIST", reportsGeneral);
		}
		if(isValidMenu("RptMonthlyInactiveEmployeeList"))
		{
			addCaptionedItem("MONTHLY INACTIVE EMPLOYEE LIST", reportsGeneral);
		}
		if(isValidMenu("employeeListSectionWise"))
		{
			addCaptionedItem("EMPLOYEE LIST(SECTION WISE)", reportsGeneral);
		}	
		if(isValidMenu("workerStatus"))
		{
			addCaptionedItem("WORKER STATUS", reportsGeneral);
		}
		
/*		if(isValidMenu("employeeListSectionWise_CHO"))
		{
			addCaptionedItem("EMPLOYEE LIST(SECTION WISE_CHO)", reportsGeneral);
		}
*/

		if(isValidMenu("IndividualEmployeeDetails"))
		{
			addCaptionedItem("INDIVIDUAL EMPLOYEE DETAILS", reportsGeneral);
		}
		
/*		if(isValidMenu("IndividualEmployeeDetails_CHO"))
		{
			addCaptionedItem("INDIVIDUAL EMPLOYEE DETAILS_CHO", reportsGeneral);
		}*/
		
		if(isValidMenu("employeeOfficialInformation"))
		{
			addCaptionedItem("EMPLOYEE (OFFICIAL) INFORMATION", reportsGeneral);
		}
		
/*		if(isValidMenu("employeeOfficialInformation_CHO"))
		{
			addCaptionedItem("EMPLOYEE (OFFICIAL) INFORMATION_CHO", reportsGeneral);
		}*/
		
		if(isValidMenu("editemployeeinformation"))
		{
			addCaptionedItem("EDIT EMPLOYEE INFORMATION", reportsGeneral);
		}
		
	/*	if(isValidMenu("editemployeeinformation_CHO"))
		{
			addCaptionedItem("EDIT EMPLOYEE INFORMATION_CHO", reportsGeneral);
		}*/

		if(isValidMenu("DesignationList"))
		{
			addCaptionedItem("DESIGNATION LIST", reportsGeneral);
		}
		
		if(isValidMenu("appointmentletter"))
		{
			addCaptionedItem("APPLICATION AND APPOINTMENT LETTER", reportsGeneral);
		}
		
/*		if(isValidMenu("appointmentletter_CHO"))
		{
			addCaptionedItem("APPLICATION AND APPOINTMENT LETTER_CHO", reportsGeneral);
		}*/

		if(isValidMenu("JoiningDate"))
		{
			addCaptionedItem("JOINING DATE", reportsGeneral);
		}

		if(isValidMenu("ConfirmationDate"))
		{
			addCaptionedItem("CONFIRMATION DATE", reportsGeneral);
		}

		if(isValidMenu("LengthOfService"))
		{
			addCaptionedItem("LENGTH OF SERVICE", reportsGeneral);
		}

		if(isValidMenu("LengthOfServiceWithGross"))
		{
			addCaptionedItem("LENGTH OF SERVICE WITH GROSS", reportsGeneral);
		}
	
/*		if(isValidMenu("LengthOfServiceWithGross_CHO"))
		{
			addCaptionedItem("LENGTH OF SERVICE WITH GROSS_CHO", reportsGeneral);
		}*/

		if(isValidMenu("ageAsOnDate"))
		{
			addCaptionedItem("AGE(AS ON DATE)", reportsGeneral);
		}

		if(isValidMenu("employeeReport"))
		{
			addCaptionedItem("EMPLOYEE REPORT", reportsGeneral);
		}

		if(isValidMenu("rptOtNFridayEnable"))
		{
			addCaptionedItem("OT & FRIDAY ENABLE.", reportsGeneral);
		}

		if(isValidMenu("rptIDoubleShiftAssign"))
		{
			addCaptionedItem("I DOUBLE SHIFT ASSIGN.", reportsGeneral);
		}

		if(isValidMenu("rptEditEmployeeProximityID"))
		{
			addCaptionedItem("EDIT PROXIMITY ID",reportsGeneral);
		}

		if(isValidMenu("rptActiveInactiveEmployee"))
		{
			addCaptionedItem("ACTIVE/INACTIVE EMPLOYEE LIST",reportsGeneral);
		}

		if(isValidMenu("performanceEvaluation"))
		{
			addCaptionedItem("PERFORMANCE EVALUATION", reportsGeneral);
		}
		if(isValidMenu("EditEmployeeInformationWithUser"))
		{
			addCaptionedItem("EDIT EMPLOYEE INFORMATION WITH USER", reportsGeneral);
		}
		/*if(isValidMenu("SALARY CERTIFICATE"))
		{
			addCaptionedItem("RptSalarySertificate", reportsGeneral);
		}*/
		
	
	}

	private void AddSalaryReportChild(Object reportsSalary)
	{
		if(isValidMenu("dailysalarysheet"))
		{
			addCaptionedItem("DAILY SALARY SHEET", reportsSalary);
		}
		
		if(isValidMenu("RPTholidayDeclare"))
		{
			addCaptionedItem("HOLIDAY DECLARATION.", reportsSalary);
		}
		
		if(isValidMenu("salaryIncrementProposal"))
		{
			addCaptionedItem("INCREMENT PROPOSAL", reportsSalary);
		}
			
	/*	if(isValidMenu("salaryIncrementProposal_CHO"))
		{
			addCaptionedItem("INCREMENT PROPOSAL_CHO", reportsSalary);
		}*/
		
		if(isValidMenu("AllIncrementList"))
		{
			addCaptionedItem("ALL INCREMENT LIST", reportsSalary);
		}
		if(isValidMenu("IndividualIncrementHistory"))
		{
			addCaptionedItem("INDIVIDUAL INCREMENT HISTORY", reportsSalary);
		}
		
		if(isValidMenu("salaryIndividualIncrementTypeProposal"))
		{
			addCaptionedItem("INDIVIDUAL INCREMENT TYPE PROPOSAL", reportsSalary);
		}
		
	/*	if(isValidMenu("salaryIncrementTypeProposal_CHO"))
		{
			addCaptionedItem("INCREMENT TYPE PROPOSAL_CHO", reportsSalary);
		}
		*/
		if(isValidMenu("salaryStructure"))
		{
			addCaptionedItem("SALARY STRUCTURE", reportsSalary);
		}
		
/*		if(isValidMenu("salaryStructure_CHO"))
		{
			addCaptionedItem("SALARY STRUCTURE_CHO", reportsSalary);
		}*/

		if(isValidMenu("MonthlySalary."))
		{
			addCaptionedItem("MONTHLY SALARY.", reportsSalary);
		}
		
/*		if(isValidMenu("MonthlySalary_CHO."))
		{
			addCaptionedItem("MONTHLY SALARY_CHO.", reportsSalary);
		}*/

		if(isValidMenu("auditMonthlySalary."))
		{
			addCaptionedItem("MONTHLY_SALARY.", reportsSalary);
		}
		if(isValidMenu("RptDeductionSummary."))
		{
			addCaptionedItem("DEDUCTION SUMMARY", reportsSalary);
		}
		if(isValidMenu("editMonthlySalarySectionWise"))
		{
			addCaptionedItem("EDIT MONTHLY SALARY.", reportsSalary);
		}
		
/*		if(isValidMenu("editMonthlySalarySectionWise_CHO"))
		{
			addCaptionedItem("EDIT MONTHLY SALARY_CHO.", reportsSalary);
		}*/
		
		if(isValidMenu("sectionWisesalarySummary"))
		{
			addCaptionedItem("SECTION WISE SALARY SUMMARY", reportsSalary);
		}

		if(isValidMenu("monthlyPaySlip"))
		{
			addCaptionedItem("MONTHLY PAY SLIP", reportsSalary);
		}
		
		if(isValidMenu("auditmonthlyPaySlip"))
		{
			addCaptionedItem("MONTHLY_PAY_SLIP", reportsSalary);
		}


		if(isValidMenu("monthlySalaryComparison"))
		{
			addCaptionedItem("MONTHLY SALARY COMPARISON", reportsSalary);
		}
		
/*		if(isValidMenu("monthlySalaryComparison_CHO"))
		{
			addCaptionedItem("MONTHLY SALARY COMPARISON_CHO", reportsSalary);
		}*/

		if(isValidMenu("comparativeSalarySummary"))
		{
			addCaptionedItem("COMPARATIVE SALARY SUMMARY", reportsSalary);
		}
		
/*		if(isValidMenu("comparativeSalarySummary_CHO"))
		{
			addCaptionedItem("COMPARATIVE SALARY SUMMARY_CHO", reportsSalary);
		}*/

		if(isValidMenu("EmployeeCoinAnalysis"))
		{
			addCaptionedItem("EMPLOYEE WISE COIN ANALYSIS", reportsSalary);
		}
		
/*		if(isValidMenu("EmployeeCoinAnalysis_CHO"))
		{
			addCaptionedItem("EMPLOYEE WISE COIN ANALYSIS_CHO", reportsSalary);
		}*/

		if(isValidMenu("sectionCoinAnalysis"))
		{
			addCaptionedItem("SECTION WISE COIN ANALYSIS", reportsSalary);
		}
		
	/*	if(isValidMenu("sectionCoinAnalysis_CHO"))
		{
			addCaptionedItem("SECTION WISE COIN ANALYSIS_CHO", reportsSalary);
		}*/

		if(isValidMenu("bankStatement"))
		{
			addCaptionedItem("BANK ADVICE WITH FORWARDING LETTER", reportsSalary);
		}
		
/*		if(isValidMenu("bankStatement_CHO"))
		{
			addCaptionedItem("BANK ADVICE WITH FORWARDING LETTER_CHO", reportsSalary);
		}*/
		
		if(isValidMenu("layOffsheet"))
		{
			addCaptionedItem("LAYOFF REPORT", reportsSalary);
		}

		if(isValidMenu("festivalBonusReport"))
		{
			addCaptionedItem("FESTIVAL BONUS REPORT", reportsSalary);
		}
		
/*		if(isValidMenu("festivalBonusReport_CHO"))
		{
			addCaptionedItem("FESTIVAL BONUS REPORT_CHO", reportsSalary);
		}*/
		
		if(isValidMenu("salaryincrement"))
		{
			addCaptionedItem("SALARY INCREMENT REPORT", reportsSalary);
		}
		if(isValidMenu("DeleteMonthlySalary"))
		{
			addCaptionedItem("DELETE MONTHLY SALARY REPORT", reportsSalary);
		}
		
		
	}

	private void AddAttendanceReportChild(Object attendanceReport)
	{
		
		if(isValidMenu("dailypunchreport"))
		{
			addCaptionedItem("DAILY PUNCH REPORT", attendanceReport);
		}
		
		if(isValidMenu("ShiftWiseTotalManPowerTwoShift"))
		{
			addCaptionedItem("SHIFT TOTAL WISE MAN POWER TWO SHIFT", attendanceReport);
		}
		
		
		if(isValidMenu("sectionWiseDailyAttendance"))
		{
			addCaptionedItem("SECTION WISE DAILY ATTENDANCE", attendanceReport);
		}

		if(isValidMenu("sectionWiseDateBetweenAttendance"))
		{
			addCaptionedItem("SECTION WISE DATE BETWEEN ATTENDANCE", attendanceReport);
		}

		if(isValidMenu("AuditsectionWiseDailyAttendance"))
		{
			addCaptionedItem("SECTION_WISE_DAILY_ATTENDANCE", attendanceReport);
		}

		if(isValidMenu("AuditsectionWiseDateBetweenAttendance"))
		{
			addCaptionedItem("SECTION_WISE_DATE_BETWEEN_ATTENDANCE", attendanceReport);
		}
		
		if(isValidMenu("DateWiseEmployeeAttendanceEdit"))
		{
			addCaptionedItem("DATE WISE EMPLOYEE ATTENDANCE EDIT", attendanceReport);
		}

		if(isValidMenu("attendanceUpdate"))
		{
			addCaptionedItem("EDIT EMPLOYEE ATTENDANCE REGISTER", attendanceReport);
		}

		if(isValidMenu("attendanceUpdateIDoubleShift"))
		{
			addCaptionedItem("EDIT EMPLOYEE ATTENDANCE (DOUBLE SHIFT).", attendanceReport);
		}

		if(isValidMenu("sectionWiseSpecialAbsent"))
		{
			addCaptionedItem("SECTION WISE DAILY SPECIAL ABSENT", attendanceReport);
		}

		if(isValidMenu("RptDateBetweenSectionWiseSpecialAbsent"))
		{
			addCaptionedItem("SECTION WISE DATE BETWEEN SPECIAL ABSENT", attendanceReport);
		}

		if(isValidMenu("sectionWiseDailylateattendance"))
		{
			addCaptionedItem("SECTION WISE DAILY LATE ATTENDANCE", attendanceReport);
		}

		if(isValidMenu("sectionWiseDailyEarlyOut"))
		{
			addCaptionedItem("SECTION WISE DAILY EARLY OUT", attendanceReport);
		}

		if(isValidMenu("sectionWiseDailyMovement"))
		{
			addCaptionedItem("SECTION WISE DAILY MOVEMENT", attendanceReport);
		}

		if(isValidMenu("sectionWiseDailyAbsent"))
		{
			addCaptionedItem("SECTION WISE DAILY ABSENT", attendanceReport);
		}

		if(isValidMenu("sectionWiseDateBtnAbsentRegister"))
		{
			addCaptionedItem("SECTION WISE DATE BETWEEN ABSENT REGISTER", attendanceReport);
		}

		if(isValidMenu("dailyAttendancePosition"))
		{
			addCaptionedItem("DAILY ATTENDANCE POSITION", attendanceReport);
		}
		
		if(isValidMenu("shiftWiseInTime"))
		{
			addCaptionedItem("SHIFT WISE IN TIME REPORT", attendanceReport);
		}
		
		if(isValidMenu("shiftWiseManPower"))
		{
			addCaptionedItem("SHIFT WISE TOTAL MAN POWER", attendanceReport);
		}

		if(isValidMenu("sectionWiseAttendanceManually."))
		{
			addCaptionedItem("SECTION WISE ATTENDANCE MANUALLY.", attendanceReport);
		}

		if(isValidMenu("SectionWiserAbsentAsPunishment"))
		{
			addCaptionedItem("SECTION WISE ABSENT AS PUNISHMENT.", attendanceReport);
		}

		if(isValidMenu("monthlyattendance"))
		{
			addCaptionedItem("MONTHLY ATTENDANCE", attendanceReport);
		}
		
/*		if(isValidMenu("monthlyattendancecho"))
		{
			addCaptionedItem("MONTHLY ATTENDANCE (CHO)", attendanceReport);
		}*/


		if(isValidMenu("auditmonthlyattendance"))
		{
			addCaptionedItem("MONTHLY_ATTENDANCE", attendanceReport);
		}

		if(isValidMenu("netWorkingHour"))
		{
			addCaptionedItem("NET WORKING HOUR", attendanceReport);
		}
		
		if(isValidMenu("monthlyattendanceinactiveid"))
		{
			addCaptionedItem("INACTIVE ID WITH MONTHLY ATTENDANCE", attendanceReport);
		}
		
		if(isValidMenu("withoutattendancedataandactiveid"))
		{
			addCaptionedItem("ACTIVE ID REPORT WITHOUT ATTEDANCE", attendanceReport);
		}

		if(isValidMenu("monthlyattendancesummary"))
		{
			addCaptionedItem("SECTION WISE MONTHLY ATTENDANCE SUMMARY", attendanceReport);
		}

		if (isValidMenu("monthlyAttendanceSummaryRegister"))
		{
			addCaptionedItem("SECTION WISE MONTHLY ATTENDANCE SUMMARY REGISTER", attendanceReport);
		}
	}

	private void AddOtReportChild(Object reportsOt)
	{
		if(isValidMenu("monthWiseOt"))
		{
			addCaptionedItem("MONTH WISE OT", reportsOt);
		}

		if(isValidMenu("auditmonthWiseOt"))
		{
			addCaptionedItem("MONTH_WISE_OT", reportsOt);
		}

		if(isValidMenu("individualOt"))
		{
			addCaptionedItem("INDIVIDUAL OT", reportsOt);
		}
		if(isValidMenu("rptMonthlyOTComparison"))
		{
			addCaptionedItem("MONTHLY OT COMPARISON",reportsOt);
		}
		if(isValidMenu("rptSectionWiseMonthlyOTComparison"))
		{
			addCaptionedItem("SECTION WISE MONTHLY OT COMPARISON",reportsOt);
		}
		if(isValidMenu("monthlyFridayAllowance"))
		{
			addCaptionedItem("MONTHLY FRIDAY ALLOWANCE",reportsOt);
		}
		
	/*	if(isValidMenu("monthlyFridayAllowance_CHO"))
		{
			addCaptionedItem("MONTHLY FRIDAY ALLOWANCE_CHO",reportsOt);
		}*/
	}

	private void AddLeaveReportChild(Object reportsLeave)
	{
		if(isValidMenu("leaveApplication"))
		{
			addCaptionedItem("LEAVE APPLICATION", reportsLeave);
		}
		

		if(isValidMenu("leaveRegister"))
		{
			addCaptionedItem("LEAVE REGISTER", reportsLeave);
		}

		if(isValidMenu("leaveRegisterIndividual"))
		{
			addCaptionedItem("INDIVIDUAL LEAVE REGISTER", reportsLeave);
		}

		if(isValidMenu("RptleaveCancelForm"))
		{
			addCaptionedItem("LEAVE CANCEL FORM.", reportsLeave);
		}

		if(isValidMenu("rptTourApplication"))
		{
			addCaptionedItem("TOUR APPLICATION", reportsLeave);
		}
		
		if(isValidMenu("rptLeaveEncashment"))
		{
			addCaptionedItem("LEAVE ENCASHMENT REPORT", reportsLeave);
		}
		
	}

	private void AddLoanReportChild(Object reportsLeave)
	{
		if(isValidMenu("laonApplication"))
		{
			addCaptionedItem("LOAN APPLICATION", reportsLeave);
		}

		if(isValidMenu("individualLoanStatement"))
		{
			addCaptionedItem("INDIVIDUAL LOAN STATEMENT", reportsLeave);
		}

		if(isValidMenu("loanRegister"))
		{
			addCaptionedItem("LOAN REGISTER", reportsLeave);
		}
	}

	private Object addCaptionedItem(String caption, Object parent) 
	{
		final Object id = tree.addItem();
		final Item item = tree.getItem(id);
		final Property p = item.getItemProperty(CAPTION_PROPERTY);

		p.setValue(caption);

		if (parent != null) 
		{
			tree.setChildrenAllowed(parent, true);
			tree.setParent(id, parent);
			tree.setChildrenAllowed(id, false);
		}

		return id;
	}

	// check is valid menu for add menu bar
	private boolean isValidMenu(String id)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			Iterator<?> iter = session.createSQLQuery("SELECT menuId FROM tbAuthentication WHERE"
					+ " userId = '"+sessionBean.getUserId()+"' AND menuId = '"+id+"'").list().iterator();
			if(!iter.hasNext())
			{
				return true;
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		finally{session.close();}
		return false;
	}

	@SuppressWarnings("serial")
	public void treeAction()
	{
		tree.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				// MASTER
				if(event.getItem().toString().equalsIgnoreCase("HRM MASTER SETUP"))
				{
					showWindow(new hrmMaster(sessionBean),event.getItem(),"hrmMaster");
				}
				// TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("HRM TRANSACTION"))
				{
					showWindow(new hrmTransaction(sessionBean),event.getItem(),"hrmTransection");
				}
				// REPORTS
				if(event.getItem().toString().equalsIgnoreCase("HRM REPORTS"))
				{
					showWindow(new hrmReports(sessionBean),event.getItem(),"hrmReports");
				}

				// MONTHLY SALARY 
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY SALARY"))
				{
					showWindow(new accessMonthlySalary(sessionBean),event.getItem(),"accessMonthlySalary");
				}

				// OVER TIME
				if(event.getItem().toString().equalsIgnoreCase("OVER TIME"))
				{
					showWindow(new accessOverTime(sessionBean),event.getItem(),"accessOverTime");
				}

				// LEAVE
				if(event.getItem().toString().equalsIgnoreCase("LEAVE"))
				{
					showWindow(new accessLeave(sessionBean),event.getItem(),"accessLeave");
				}

				// LOAN
				if(event.getItem().toString().equalsIgnoreCase("LOAN"))
				{
					showWindow(new accessLoan(sessionBean),event.getItem(),"accessLoan");
				}

				// GENERAL REPORT
				if(event.getItem().toString().equalsIgnoreCase("GENERAL REPORT"))
				{
					showWindow(new accessGeneralReport(sessionBean),event.getItem(),"rptGeneralReport");
				}

				//SALARY REPORT
				if(event.getItem().toString().equalsIgnoreCase("SALARY REPORT"))
				{
					showWindow(new accessSalaryReport(sessionBean),event.getItem(),"rptSalaryReport");
				}

				//OT REPORT
				if(event.getItem().toString().equalsIgnoreCase("OT REPORT"))
				{
					showWindow(new accessOtReport(sessionBean),event.getItem(),"rptotReport");
				}

				// LEAVE REPORT
				if(event.getItem().toString().equalsIgnoreCase("LEAVE REPORT"))
				{
					showWindow(new accessLeaveReport(sessionBean),event.getItem(),"rptleaveReport");
				}

				// LOAN REPORT
				if(event.getItem().toString().equalsIgnoreCase("LOAN REPORT"))
				{
					showWindow(new accessLoanReport(sessionBean),event.getItem(),"rptloanReport");
				}

				if(event.getItem().toString().equalsIgnoreCase("DESIGNATION INFORMATION"))
				{
					showWindow(new Designation(sessionBean),event.getItem(),"designationInformation");
				}
				if(event.getItem().toString().equalsIgnoreCase("DESIGNATION SERIAL"))
				{
					showWindow(new DesignationSerialInfo(sessionBean),event.getItem(),"designationSerial");
				}
				if(event.getItem().toString().equalsIgnoreCase("GRADE INFORMATION"))
				{
					showWindow(new GradeIformation(sessionBean),event.getItem(),"gradeInformation");
				}

				// employee Information
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE INFORMATION"))
				{
					showWindow(new EmployeeInformation(sessionBean),event.getItem(),"employeeInformation");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE INFORMATION_CHO"))
				{
					showWindow(new EmployeeInformation_CHO(sessionBean),event.getItem(),"employeeInformation_CHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("DELETE PROXIMITY ID/CARD NO"))
				{
					showWindow(new SectionWiseDeleteProximityID(sessionBean),event.getItem(),"deleteProximityCard");
				}

				if(event.getItem().toString().equalsIgnoreCase("ACTIVE/INACTIVE EMPLOYEE"))
				{
					showWindow(new SectionWiseActiveInactiveEmployee(sessionBean),event.getItem(),"activeInactiveEmployee");
				}

				if(event.getItem().toString().equalsIgnoreCase("OT & FRIDAY ENABLE"))
				{
					showWindow(new OTNFridayEnable(sessionBean),event.getItem(),"ot&FridayEnable");
				}
				if(event.getItem().toString().equalsIgnoreCase("OT & FRIDAY ENABLE_CHO"))
				{
					showWindow(new OTNFridayEnableCHO(sessionBean),event.getItem(),"ot&FridayEnable_CHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("SHIFT ASSIGN"))
				{
					showWindow(new ShiftAssign(sessionBean),event.getItem(),"shiftAssign");
				}

				if(event.getItem().toString().equalsIgnoreCase("I DOUBLE/RAMADAN SHIFT ASSIGN")) 
				{
					showWindow(new IDoubleShiftDeclare(sessionBean),event.getItem(),"iDoubleShiftAssign");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("INCREMENT TYPE"))
				{
					showWindow(new IncrementType(sessionBean),event.getItem(),"IncrementType");
				}

				//Master Setup End
				//Salary Module Start
				if(event.getItem().toString().equalsIgnoreCase("HOLIDAY DECLARATION"))
				{
					showWindow(new holidayDeclare(sessionBean),event.getItem(),"holidyDeclaration");
				}
				// EMPLOYEE ATTENDANCE UPLOAD
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE ATTENDANCE UPLOAD"))
				{
					showWindow(new EmployeeAttendanceUploadIn(sessionBean),event.getItem(),"attendanceTimeEntry");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE ATTENDANCE UPLOAD (SINGLE DEVICE)"))
				{
					showWindow(new EmployeeAttendanceUploadSingleDevice(sessionBean),event.getItem(),"attendanceTimeEntrySingleDevice");
				}

				//EDIT EMPLOYEE ATTENDANCE 
				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE ATTENDANCE"))
				{
					showWindow(new EditEmployeeAttendance(sessionBean),event.getItem(),"editAttendance");
				}
				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE ATTENDANCE_CHO"))
				{
					showWindow(new EditEmployeeAttendanceCHO(sessionBean),event.getItem(),"editAttendanceCHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE ATTENDANCE (INDIVIDUAL)"))
				{
					showWindow(new EditIndividualEmployeeAttendanceMonthly(sessionBean),event.getItem(),"editAttendanceIndividual");
				}
				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE ATTENDANCE (INDIVIDUAL)_CHO"))
				{
					showWindow(new EditIndividualEmployeeAttendanceMonthly_CHO(sessionBean),event.getItem(),"editAttendanceIndividual_CHO");
				}
				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE ATTENDANCE (DATE BETWEEN)"))
				{
					showWindow(new SectionWiseDateBetweenEmployeeAttendance(sessionBean),event.getItem(),"dateBetweenEmployeeAttendance");
				}
				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE ATTENDANCE (DATE BETWEEN)_CHO"))
				{
					showWindow(new SectionWiseDateBetweenEmployeeAttendance_CHO(sessionBean),event.getItem(),"dateBetweenEmployeeAttendance_CHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE ATTENDANCE (DOUBLE SHIFT)"))
				{
					showWindow(new EditEmployeeAttendanceIDoubleShift(sessionBean), event.getItem(), "editEmployeeAttendanceDoubleShift");
				}

				//SECTION WISE ATTENDANCE
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE ATTENDANCE MANUALLY"))
				{
					showWindow(new SectionWiseAttendance(sessionBean),event.getItem(),"SectionWiseAttendance");
				}
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE ATTENDANCE MANUALLY_CHO"))
				{
					showWindow(new SectionWiseAttendance_CHO(sessionBean),event.getItem(),"SectionWiseAttendance_CHO");
				}

				//SECTION WISE ABSENT
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE ABSENT AS PUNISHMENT"))
				{
					showWindow(new SectionWiseAbsent(sessionBean),event.getItem(),"SectionWiserAbsent");
				}

				if(event.getItem().toString().equalsIgnoreCase("ADD/LESS EXTRA OT HOUR"))
				{
					showWindow(new addExtraOrLessOTHour(sessionBean),event.getItem(),"addOrLessExtraOT");
				}

				if(event.getItem().toString().equalsIgnoreCase("MONTHLY ADJUSTMENT & DEDUCTION"))
				{
					showWindow(new MonthlyAdjustmentNDeduction(sessionBean),event.getItem(),"monthlyAdjustmentNDeduction");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY ADJUSTMENT & DEDUCTION_CHO"))
				{
					showWindow(new MonthlyAdjustmentNDeduction_CHO(sessionBean),event.getItem(),"monthlyAdjustmentNDeduction_CHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("LAYOFF ENTRY FORM"))
				{
					showWindow(new LayOffOption(sessionBean),event.getItem(),"LayOffOption");
				}

				if(event.getItem().toString().equalsIgnoreCase("INCREMENT PROCESS"))
				{
					showWindow(new IncrementProcess(sessionBean),event.getItem(),"salaryIncrementProcess");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("INCREMENT PROCESS_CHO"))
				{
					showWindow(new IncrementProcessCHO(sessionBean),event.getItem(),"salaryIncrementProcess_CHO");
				}

				// GENERATE MONTHLY SALARY
				if(event.getItem().toString().equalsIgnoreCase("GENERATE MONTHLY SALARY"))
				{
					showWindow(new MonthlySalaryGenerate(sessionBean),event.getItem(),"generateMonthlySalary");
				}
				if(event.getItem().toString().equalsIgnoreCase("GENERATE MONTHLY SALARY_CHO"))
				{
					showWindow(new MonthlySalaryGenerateCHO(sessionBean),event.getItem(),"generateMonthlySalaryCHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("GENERATE_MONTHLY_SALARY"))
				{
					showWindow(new AuditMonthlySalaryGenerate(sessionBean),event.getItem(),"auditgenerateMonthlySalary");
				}
				if(event.getItem().toString().equalsIgnoreCase("DEDUCTION SUMMARY"))
				{
					showWindow(new RptDeductionSummary(sessionBean),event.getItem(),"RptDeductionSummary");
				}

				if(event.getItem().toString().equalsIgnoreCase("DELETE MONTHLY SALARY"))
				{
					showWindow(new DeleteMonthlySalary(sessionBean),event.getItem(),"deleteMonthlySalary");
				}

				if(event.getItem().toString().equalsIgnoreCase("DELETE_MONTHLY_SALARY"))
				{
					showWindow(new Delete_Monthly_Salary(sessionBean),event.getItem(),"delete_Monthly_Salary");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("DELETE MONTHLY SALARY_CHO"))
				{
					showWindow(new DeleteMonthlySalaryCHO(sessionBean),event.getItem(),"deleteMonthlySalary_CHO");
				}
				// EDIT MONTHLY SALARY
				if(event.getItem().toString().equalsIgnoreCase("EDIT MONTHLY SALARY"))
				{
					showWindow(new EditMonthlySalary(sessionBean),event.getItem(),"editMonthlySalary");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("EDIT MONTHLY SALARY_CHO"))
				{
					showWindow(new EditMonthlySalaryCHO(sessionBean),event.getItem(),"editMonthlySalary_CHO");
				}

				// FESTIVAL BONUS
				if(event.getItem().toString().equalsIgnoreCase("FESTIVAL BONUS"))
				{
					showWindow(new FestivalBonus(sessionBean),event.getItem(),"festivalBonus");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("FESTIVAL BONUS_CHO"))
				{
					showWindow(new FestivalBonusCHO(sessionBean),event.getItem(),"festivalBonus_CHO");
				}
				
				
				
				if(event.getItem().toString().equalsIgnoreCase("DELETE FESTIVAL BONUS"))
				{
					showWindow(new DeleteFestivalBonus(sessionBean),event.getItem(),"DeleteFestivalBonus");
				}
				if(event.getItem().toString().equalsIgnoreCase("DELETE FESTIVAL BONUS_CHO"))
				{
					showWindow(new DeleteFestivalBonus_CHO(sessionBean),event.getItem(),"DeleteFestivalBonus_CHO");
				}
		
				//Salary Module End
				//Leave Module Start
				// Leave Type
				if(event.getItem().toString().equalsIgnoreCase("LEAVE TYPE"))
				{
					showWindow(new LeaveType(sessionBean),event.getItem(),"leaveType");
				}

				// LEAVE BALANCE
				if(event.getItem().toString().equalsIgnoreCase("LEAVE OPENING BALANCE"))
				{
					showWindow(new LeaveBalance(sessionBean),event.getItem(),"leaveOpeningBalance");
				}

				// Leave Policy
				if(event.getItem().toString().equalsIgnoreCase("LEAVE POLICY"))
				{
					showWindow(new LeavePolicy(sessionBean),event.getItem(),"leavePolicy");
				}

				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL LEAVE ENTITLEMENT"))
				{
					showWindow(new individualLeaveEntitlement(sessionBean),event.getItem(),"individualLeaveEntitlement");
				}
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL LEAVE ENTITLEMENT_CHO"))
				{
					showWindow(new individualLeaveEntitlementCHO(sessionBean),event.getItem(),"individualLeaveEntitlementCHO");
				}

				// LEAVE INFORMATION
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPLICATION FORM"))
				{
					showWindow(new LeaveApplication(sessionBean),event.getItem(),"leaveApplicationForm");
				}
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPLICATION FORM_CHO"))
				{
					showWindow(new LeaveApplicationCHO(sessionBean),event.getItem(),"leaveApplicationFormCHO");
				}

				// LEAVE CANCEL FORM
				if(event.getItem().toString().equalsIgnoreCase("LEAVE CANCEL FORM"))
				{
					showWindow(new LeaveCancleForm(sessionBean),event.getItem(),"leaveCancelForm");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("LEAVE CANCEL FORM_CHO"))
				{
					showWindow(new LeaveCancleFormCHO(sessionBean),event.getItem(),"leaveCancelFormCHO");
				}

				// LEAVE CLOSING BALANCE
				if(event.getItem().toString().equalsIgnoreCase("LEAVE CLOSING BALANCE"))
				{
					showWindow(new LeaveClosing(sessionBean),event.getItem(),"leaveClosingBalance");
				}
				//SECTION WISE LEAVE
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE LEAVE"))
				{
					showWindow(new SectionWiseLeave(sessionBean),event.getItem(),"sectionWiseLeave");
				}

				if(event.getItem().toString().equalsIgnoreCase("TOUR APPLICATION FORM"))
				{
					showWindow(new TourApplicationForm(sessionBean),event.getItem(),"tourApplicationForm");
				}
				if(event.getItem().toString().equalsIgnoreCase("TOUR APPLICATION FORM_CHO"))
				{
					showWindow(new TourApplicationFormCHO(sessionBean),event.getItem(),"tourApplicationFormCHO");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("LEAVE ENCASHMENT"))
				{
					showWindow(new LeaveEncashment(sessionBean),event.getItem(),"leaveEncashment");
				}
				if(event.getItem().toString().equalsIgnoreCase("LEAVE ENCASHMENT_CHO"))
				{
					showWindow(new LeaveEncashmentCHO(sessionBean),event.getItem(),"leaveEncashmentCHO");
				}

				//Loan Module Start
				// LEAVE APPLICATION FORM
				if(event.getItem().toString().equalsIgnoreCase("LOAN APPLICATION FORM"))
				{
					showWindow(new LoanApplicationForm(sessionBean),event.getItem(),"loanApplicationForm");
				}
				if(event.getItem().toString().equalsIgnoreCase("LOAN APPLICATION FORM_CHO"))
				{
					showWindow(new LoanApplicationFormCHO(sessionBean),event.getItem(),"loanApplicationFormCHO");
				}

				// LEAVE RECOVERY
				if(event.getItem().toString().equalsIgnoreCase("LOAN RECOVERY FORM"))
				{
					showWindow(new LoanRecoveryForm(sessionBean),event.getItem(),"loanRecoveryForm");
				}
				if(event.getItem().toString().equalsIgnoreCase("LOAN RECOVERY FORM_CHO"))
				{
					showWindow(new LoanRecoveryFormCHO(sessionBean),event.getItem(),"loanRecoveryFormCHO");
				}
				//Loan Module End

				// CV(CURRICULAM VITAE)
				if(event.getItem().toString().equalsIgnoreCase("CV(CURRICULUM VITAE)"))
				{
					showWindow(new ReportEmployeeInformation(sessionBean),event.getItem(),"employeeCV");
				}

				// CV(CURRICULAM VITAE)
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE LIST"))
				{
					showWindow(new RptEmployeeList(sessionBean),event.getItem(),"employeeList");
				}
				if(event.getItem().toString().equalsIgnoreCase("NEW EMPLOYEE LIST"))
				{
					showWindow(new NewEmployeeList(sessionBean),event.getItem(),"NewEmployeeList");
				}

				if(event.getItem().toString().equalsIgnoreCase("MONTHLY INACTIVE EMPLOYEE LIST"))
				{
					showWindow(new RptMonthlyInactiveEmployeeList(sessionBean),event.getItem(),"RptMonthlyInactiveEmployeeList");
				}
				// EMPLOYEE LIST(REGISTER WISE)
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE LIST(SECTION WISE)"))
				{
					showWindow(new RptEmployeeListSectionWise(sessionBean),event.getItem(),"employeeListSectionWise");
				}
				if(event.getItem().toString().equalsIgnoreCase("WORKER STATUS"))
				{
					showWindow(new RptWorkerStatusHaqueandSons(sessionBean),event.getItem(),"workerStatus");
				}

				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE LIST(SECTION WISE_CHO)"))
				{
					showWindow(new RptEmployeeListSectionWiseCHO(sessionBean),event.getItem(),"employeeListSectionWise_CHO");
				}


				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL EMPLOYEE DETAILS"))
				{
					showWindow(new RptIndividualEmployeeDetails(sessionBean),event.getItem(),"IndividualEmployeeDetails");
				}			

				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL EMPLOYEE DETAILS_CHO"))
				{
					showWindow(new RptIndividualEmployeeDetailsCHO(sessionBean),event.getItem(),"IndividualEmployeeDetails_CHO");
				}
		
				
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE (OFFICIAL) INFORMATION"))
				{
					showWindow(new RptEmployeeInformationOfficial(sessionBean),event.getItem(),"employeeOfficialInformation");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE (OFFICIAL) INFORMATION_CHO"))
				{
					showWindow(new RptEmployeeInformationOfficial_CHO(sessionBean),event.getItem(),"employeeOfficialInformation_CHO");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE INFORMATION"))
				{
					showWindow(new RptEditEmployeeInformation(sessionBean),event.getItem(),"editemployeeinformation");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE INFORMATION_CHO"))
				{
					showWindow(new RptEditEmployeeInformationCHO(sessionBean),event.getItem(),"editemployeeinformation_CHO");
				}

				// DESIGNATION LIST
				if(event.getItem().toString().equalsIgnoreCase("DESIGNATION LIST"))
				{
					showWindow(new RptDesignationList(sessionBean),event.getItem(),"DesignationList");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("APPLICATION AND APPOINTMENT LETTER"))
				{
					showWindow(new RptAppointmentLetter(sessionBean),event.getItem(),"appointmentletter");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("APPLICATION AND APPOINTMENT LETTER_CHO"))
				{
					showWindow(new RptAppointmentLetter_CHO(sessionBean),event.getItem(),"appointmentletter_CHO");
				}
				// JOINING DATE
				if(event.getItem().toString().equalsIgnoreCase("JOINING DATE"))
				{
					showWindow(new RptJoiningDate(sessionBean),event.getItem(),"JoiningDate");
				}

				// CONFIRMATION DATE
				if(event.getItem().toString().equalsIgnoreCase("CONFIRMATION DATE"))
				{
					showWindow(new RptConfirmationDate(sessionBean),event.getItem(),"ConfirmationDate");
				}

				// LENGTH OF SERVICE
				if(event.getItem().toString().equalsIgnoreCase("LENGTH OF SERVICE"))
				{
					showWindow(new RptServiceOfLength(sessionBean),event.getItem(),"LengthOfService");
				}

				// LENGTH OF SERVICE
				if(event.getItem().toString().equalsIgnoreCase("LENGTH OF SERVICE WITH GROSS"))
				{
					showWindow(new RptServiceOfLengthWithGross(sessionBean),event.getItem(),"LengthOfServiceWithGross");
				}
			
				if(event.getItem().toString().equalsIgnoreCase("LENGTH OF SERVICE WITH GROSS_CHO"))
				{
					showWindow(new RptServiceOfLengthWithGross_CHO(sessionBean),event.getItem(),"LengthOfServiceWithGross_CHO");
				}

								// AGE(AS ON DATE)
				if(event.getItem().toString().equalsIgnoreCase("AGE(AS ON DATE)"))
				{
					showWindow(new RptAgeAsOnDate(sessionBean),event.getItem(),"ageAsOnDate");
				}
				// REPORT
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE REPORT"))
				{
					showWindow(new RptReport(sessionBean),event.getItem(),"employeeReport");
				}


				if(event.getItem().toString().equalsIgnoreCase("OT & FRIDAY ENABLE."))
				{
					showWindow(new RptOTNFridayEnable(sessionBean),event.getItem(),"otNFridayEnable.");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("OT & FRIDAY ENABLE_CHO."))
				{
					showWindow(new RptOTNFridayEnableCHO(sessionBean),event.getItem(),"otNFridayEnableRpt_CHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("I DOUBLE SHIFT ASSIGN."))
				{
					showWindow(new rptIDoubleShiftAssign(sessionBean),event.getItem(),"rptIDoubleShiftAssign");
				}

				if(event.getItem().toString().equalsIgnoreCase("EDIT PROXIMITY ID"))
				{
					showWindow(new RptEditProximityID(sessionBean),event.getItem(),"editProximityID");
				}

				if(event.getItem().toString().equalsIgnoreCase("ACTIVE/INACTIVE EMPLOYEE LIST"))
				{
					showWindow(new rptActiveInactiveEmployeeList(sessionBean),event.getItem(),"activeInactiveEmployeeList");
				}

				if(event.getItem().toString().equalsIgnoreCase("PERFORMANCE EVALUATION"))
				{
					showWindow(new RptPerformanceEvaluation(sessionBean),event.getItem(),"performanceEvaluation");
				}

				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE INFORMATION WITH USER"))
				{
					showWindow(new RptEditEmployeeInformationWithUser(sessionBean),event.getItem(),"EditEmployeeInformationWithUser");
				}
				if(event.getItem().toString().equalsIgnoreCase("SALARY CERTIFICATE"))
				{
					showWindow(new RptSalarySertificate(sessionBean),event.getItem(),"RptSalarySertificate");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("ACTIVE/INACTIVE EMPLOYEE LIST"))
				{
					showWindow(new rptActiveInactiveEmployeeList(sessionBean),event.getItem(),"rptActiveInactiveEmployee");
				}

				//Attendance Report
				// SALARY STRUCTURE
				
				if(event.getItem().toString().equalsIgnoreCase("DAILY PUNCH REPORT"))
				{
					showWindow(new RptDailyPunchReport(sessionBean),event.getItem(),"dailypunchreport");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY PUNCH REPORT_CHO"))
				{
					showWindow(new RptDailyPunchReportCHO(sessionBean),event.getItem(),"dailypunchreport_CHO");
				}
				if(event.getItem().toString().equalsIgnoreCase("SHIFT TOTAL WISE MAN POWER TWO SHIFT"))
				{
					showWindow(new RptShiftWiseTotalManPowerTwoShift(sessionBean),event.getItem(),"ShiftWiseTotalManPowerTwoShift");
				}

			
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE DAILY ATTENDANCE"))
				{
					showWindow(new RptSectionWiseDailyAttendance(sessionBean),event.getItem(),"sectionWiseDailyAttendance");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE DATE BETWEEN ATTENDANCE"))
				{
					showWindow(new RptDateBetweenSectionWiseAttendance(sessionBean),event.getItem(),"sectionWiseDateBetweenAttendance");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION_WISE_DAILY_ATTENDANCE"))
				{
					showWindow(new RptAuditSectionWiseDailyAttendance(sessionBean),event.getItem(),"AuditsectionWiseDailyAttendance");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION_WISE_DATE_BETWEEN_ATTENDANCE"))
				{
					showWindow(new RptAuditDateBetweenSectionWiseAttendance(sessionBean),event.getItem(),"AuditsectionWiseDateBetweenAttendance");
				}
				if(event.getItem().toString().equalsIgnoreCase("DATE WISE EMPLOYEE ATTENDANCE EDIT"))
				{
					showWindow(new RptDateWiseEmployeeAttendanceEdit(sessionBean),event.getItem(),"DateWiseEmployeeAttendanceEdit");
				}

				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE ATTENDANCE REGISTER"))
				{
					showWindow(new RptUDEmployeeAttendance(sessionBean),event.getItem(),"attendanceUpdate");
				}

				if(event.getItem().toString().equalsIgnoreCase("EDIT EMPLOYEE ATTENDANCE (DOUBLE SHIFT)."))
				{
					showWindow(new RptUDEmployeeAttendanceDoubleShift(sessionBean),event.getItem(),"attendanceUpdateIDoubleShift");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE DAILY SPECIAL ABSENT"))
				{
					showWindow(new RptSectionWiseSpecialAbsent(sessionBean), event.getItem(), "sectionWiseSpecialAbsent");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE DATE BETWEEN SPECIAL ABSENT"))
				{
					showWindow(new RptDateBetweenSectionWiseSpecialAbsent(sessionBean), event.getItem(), "RptDateBetweenSectionWiseSpecialAbsent");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE DAILY LATE ATTENDANCE"))
				{
					showWindow(new RptDailyLateIn(sessionBean), event.getItem(), "sectionWiseDailylateattendance");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE DAILY EARLY OUT"))
				{
					showWindow(new RptDailyEarlyOut(sessionBean), event.getItem(), "sectionWiseDailyEarlyOut");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE DAILY MOVEMENT"))
				{
					showWindow(new RptDailyAttendanceMovement(sessionBean), event.getItem(), "sectionWiseDailyMovement");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE DAILY ABSENT"))
				{
					showWindow(new RptSectionWiseDailyAbsent(sessionBean), event.getItem(), "sectionWiseDailyAbsent");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE DATE BETWEEN ABSENT REGISTER"))
				{
					showWindow(new RptDateBetweenAbsentRegister(sessionBean), event.getItem(),"sectionWiseDateBtnAbsentRegister");
				}

				if(event.getItem().toString().equalsIgnoreCase("DAILY ATTENDANCE POSITION"))
				{
					showWindow(new RptSectionWiseDailyAttendancePosition(sessionBean), event.getItem(), "dailyAttendancePosition");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SHIFT WISE IN TIME REPORT"))
				{
					showWindow(new RptSectionWiseInTimePosition(sessionBean), event.getItem(),"shiftWiseInTime");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SHIFT WISE TOTAL MAN POWER"))
				{
					showWindow(new RptShiftWiseTotalManPower(sessionBean), event.getItem(),"shiftWiseManPower");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE ATTENDANCE MANUALLY."))
				{
					showWindow(new rptAttendanceManually(sessionBean), event.getItem(), "sectionWiseAttendanceManually.");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE ABSENT AS PUNISHMENT."))
				{
					showWindow(new RptAbsentAsPunishment(sessionBean), event.getItem(), "SectionWiserAbsentAsPunishment");
				}

				if(event.getItem().toString().equalsIgnoreCase("MONTHLY ATTENDANCE"))
				{
					showWindow(new RptMonthlyAttendance(sessionBean), event.getItem(), "monthlyattendance");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY ATTENDANCE (CHO)"))
				{
					showWindow(new RptMonthlyAttendanceCHO(sessionBean), event.getItem(), "monthlyattendancecho");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY_ATTENDANCE"))
				{
					showWindow(new RptAuditMonthlyAttendance(sessionBean), event.getItem(), "auditmonthlyattendance");
				}

				if(event.getItem().toString().equalsIgnoreCase("NET WORKING HOUR"))
				{
					showWindow(new RptNetWorkingHour(sessionBean), event.getItem(),"netWorkingHour");
				}

                if(event.getItem().toString().equalsIgnoreCase("INACTIVE ID WITH MONTHLY ATTENDANCE"))
				{
					showWindow(new RptInactiveIDMonthlyAttendance(sessionBean), event.getItem(), "monthlyattendanceinactiveid");
				}				
				
				if(event.getItem().toString().equalsIgnoreCase("ACTIVE ID REPORT WITHOUT ATTEDANCE"))
				{
					showWindow(new RptWithoutAttendancewithActiveID(sessionBean), event.getItem(), "withoutattendancedataandactiveid");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE MONTHLY ATTENDANCE SUMMARY"))
				{
					showWindow(new rptMonthlyAttendanceSummary(sessionBean), event.getItem(), "monthlyattendancesummary");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE MONTHLY ATTENDANCE SUMMARY REGISTER"))
				{
					showWindow(new RptMonthlyAttendanceSummaryRegister(sessionBean), event.getItem(), "monthlyAttendanceSummaryRegister");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("DAILY SALARY SHEET"))
				{
					showWindow(new RptDailySalarySheet(sessionBean),event.getItem(),"dailysalarysheet");
				}

				if(event.getItem().toString().equalsIgnoreCase("HOLIDAY DECLARATION."))
				{
					showWindow(new RptHolidays(sessionBean),event.getItem(),"RptholidayDeclare");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("INCREMENT PROPOSAL"))
				{
					showWindow(new RptIncrementProposal(sessionBean),event.getItem(),"salaryIncrementProposal");
				}
				if(event.getItem().toString().equalsIgnoreCase("INCREMENT PROPOSAL_CHO"))
				{
					showWindow(new RptIncrementProposalCHO(sessionBean),event.getItem(),"salaryIncrementProposalCHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL INCREMENT TYPE PROPOSAL"))
				{
					showWindow(new RptIncrementTypeProposal(sessionBean),event.getItem(),"salaryIndividualIncrementTypeProposal");
				}
				/*	
				 * if(event.getItem().toString().equalsIgnoreCase("INCREMENT TYPE PROPOSAL_CHO"))
				{
					showWindow(new RptIncrementTypeProposalCHO(sessionBean),event.getItem(),"salaryIncrementTypeProposal_CHO");
				}*/
				
				if(event.getItem().toString().equalsIgnoreCase("ALL INCREMENT LIST"))
				{
					showWindow(new RptAllIncrementList(sessionBean),event.getItem(),"AllIncrementList");
				}
				if(event.getItem().toString().equalsIgnoreCase("ALL INCREMENT LIST_CHO"))
				{
					showWindow(new RptAllIncrementListCHO(sessionBean),event.getItem(),"AllIncrementListCHO");
				}
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL INCREMENT HISTORY"))
				{
					showWindow(new RptIndividualIncrementHistory(sessionBean),event.getItem(),"IndividualIncrementHistory");
				}
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL INCREMENT HISTORY_CHO"))
				{
					showWindow(new RptIndividualIncrementHistoryCHO(sessionBean),event.getItem(),"IndividualIncrementHistoryCHO");
				}
				// SALARY STRUCTURE
				if(event.getItem().toString().equalsIgnoreCase("SALARY STRUCTURE"))
				{
					showWindow(new RptSalaryStructure(sessionBean),event.getItem(),"salaryStructure");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SALARY STRUCTURE_CHO"))
				{
					showWindow(new RptSalaryStructure_CHO(sessionBean),event.getItem(),"salaryStructure_CHO");
				}

				// MONTHLY SALARY
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY SALARY."))
				{
					showWindow(new RptMonthlySalary(sessionBean),event.getItem(),"MonthlySalary.");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY SALARY REPORT_CHO"))
				{
					showWindow(new RptMonthlySalaryCHO(sessionBean),event.getItem(),"MonthlySalaryCHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("MONTHLY_SALARY."))
				{
					showWindow(new RptAuditMonthlySalary(sessionBean),event.getItem(),"auditMonthlySalary.");
				}

				if(event.getItem().toString().equalsIgnoreCase("EDIT MONTHLY SALARY."))
				{
					showWindow(new RptEditMonthlySalary(sessionBean),event.getItem(),"editMonthlySalarySectionWise");
				}
				if(event.getItem().toString().equalsIgnoreCase("EDIT MONTHLY SALARY REPORT_CHO"))
				{
					showWindow(new RptEditMonthlySalaryCHO(sessionBean),event.getItem(),"editMonthlySalaryReport_CHO");
				}
		/*		if(event.getItem().toString().equalsIgnoreCase("EDIT MONTHLY SALARY_CHO."))
				{
					showWindow(new RptEditMonthlySalary_CHO(sessionBean),event.getItem(),"editMonthlySalarySectionWise_CHO");
				}
				*/
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE SALARY SUMMARY"))
				{
					showWindow(new RptSectionWiseSalarySummary(sessionBean),event.getItem(),"sectionWisesalarySummary");
				}

				//MONTHLY PAY SLIP
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY PAY SLIP"))
				{
					showWindow(new RptMonthlyPaySlip(sessionBean),event.getItem(),"monthlyPaySlip");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY PAY SLIP_CHO"))
				{
					showWindow(new RptMonthlyPaySlipCHO(sessionBean),event.getItem(),"monthlyPaySlipCHO");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY_PAY_SLIP"))
				{
					showWindow(new RptAuditMonthlyPaySlip(sessionBean),event.getItem(),"auditmonthlyPaySlip");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY SALARY COMPARISON"))
				{
					showWindow(new RptMonthlySalaryComparison(sessionBean),event.getItem(),"monthlySalaryComparison");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY SALARY COMPARISON_CHO"))
				{
					showWindow(new RptMonthlySalaryComparisonCHO(sessionBean),event.getItem(),"monthlySalaryComparison_CHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("COMPARATIVE SALARY SUMMARY"))
				{
					showWindow(new RptComparativeSalarySummarySectionWise(sessionBean),event.getItem(),"comparativeSalarySummary");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("COMPARATIVE SALARY SUMMARY_CHO"))
				{
					showWindow(new RptComparativeSalarySummarySectionWiseCHO(sessionBean),event.getItem(),"comparativeSalarySummary_CHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE WISE COIN ANALYSIS"))
				{
					showWindow(new RptEmployeeWiseCoinAnalysis(sessionBean),event.getItem(),"EmployeeCoinAnalysis");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("EMPLOYEE WISE COIN ANALYSIS_CHO"))
				{
					showWindow(new RptEmployeeWiseCoinAnalysis_CHO(sessionBean),event.getItem(),"EmployeeCoinAnalysis_CHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE COIN ANALYSIS"))
				{
					showWindow(new RptDepartmentWiseCoinAnalysis(sessionBean),event.getItem(),"sectionCoinAnalysis");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE COIN ANALYSIS_CHO"))
				{
					showWindow(new RptDepartmentWiseCoinAnalysis_CHO(sessionBean),event.getItem(),"sectionCoinAnalysis_CHO");
				}

				//Bank Statement
				if(event.getItem().toString().equalsIgnoreCase("BANK ADVICE WITH FORWARDING LETTER"))
				{
					showWindow(new RptBankStatement(sessionBean),event.getItem(),"bankStatement");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("BANK ADVICE WITH FORWARDING LETTER_CHO"))
				{
					showWindow(new RptBankStatementCHO(sessionBean),event.getItem(),"bankStatement_CHO");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("LAYOFF REPORT"))
				{
					showWindow(new RptLayOffOption(sessionBean),event.getItem(),"layOffsheet");
				}

				if(event.getItem().toString().equalsIgnoreCase("FESTIVAL BONUS REPORT"))
				{
					showWindow(new RptEmployeeBonus(sessionBean),event.getItem(),"festivalBonusReport");
				}
				if(event.getItem().toString().equalsIgnoreCase("FESTIVAL BONUS REPORT_CHO"))
				{
					showWindow(new RptEmployeeBonusCHO(sessionBean),event.getItem(),"festivalBonusReportCHO");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("FESTIVAL BONUS REPORT_CHO"))
				{
					showWindow(new RptEmployeeBonusCHO(sessionBean),event.getItem(),"festivalBonusReport_CHO");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SALARY INCREMENT REPORT"))
				{
					showWindow(new RptIncrementList(sessionBean),event.getItem(),"salaryincrement");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("DELETE MONTHLY SALARY REPORT"))
				{
					showWindow(new RptDeleteMonthlySalary(sessionBean),event.getItem(),"DeleteMonthlySalary");
				}
				if(event.getItem().toString().equalsIgnoreCase("DELETE MONTHLY SALARY REPORT_CHO"))
				{
					showWindow(new RptDeleteMonthlySalaryCHO(sessionBean),event.getItem(),"DeleteMonthlySalaryCHO");
				}

				
				// MONTH WISE OT
				if(event.getItem().toString().equalsIgnoreCase("MONTH WISE OT"))
				{
					showWindow(new RptMonthWiseOt(sessionBean),event.getItem(),"monthWiseOt");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTH WISE OT_CHO"))
				{
					showWindow(new RptMonthWiseOt_CHO(sessionBean),event.getItem(),"monthWiseOt_CHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("MONTH_WISE_OT"))
				{
					showWindow(new RptAuditMonthWiseOT(sessionBean),event.getItem(),"auditmonthWiseOt");
				}
				// INDIVIDUAL OT
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL OT"))
				{
					showWindow(new RptIndividualOt(sessionBean),event.getItem(),"individualOt");
				}

				// MONTHLY OT COMPARISON
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY OT COMPARISON"))
				{
					showWindow(new RptMonthlyOTComparison(sessionBean),event.getItem(),"rptMonthlyOTComparison");
				}

				// SECTION WISE MONTHLY OT COMPARISON
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE MONTHLY OT COMPARISON"))
				{
					showWindow(new RptSectionWiseMonthlyOTComparison(sessionBean),event.getItem(),"rptSectionWiseMonthlyOTComparison");
				}

				if(event.getItem().toString().equalsIgnoreCase("MONTHLY FRIDAY ALLOWANCE"))
				{
					showWindow(new rptMonthlyFridayAllowance(sessionBean),event.getItem(),"monthlyFridayAllowance");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY FRIDAY ALLOWANCE_CHO"))
				{
					showWindow(new rptMonthlyFridayAllowance_CHO(sessionBean),event.getItem(),"monthlyFridayAllowance_CHO");
				}

				// LEAVE APPLICATION
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPLICATION"))
				{
					showWindow(new RptLeaveApplication(sessionBean),event.getItem(),"leaveApplication");
				}
				
				// LEAVE APPLICATION
				if(event.getItem().toString().equalsIgnoreCase("LEAVE APPLICATION_CHO"))
				{
					showWindow(new RptLeaveApplicationCHO(sessionBean),event.getItem(),"leaveApplicationCHO");
				}

				// LEAVE REGISTER
				if(event.getItem().toString().equalsIgnoreCase("LEAVE REGISTER"))
				{
					showWindow(new RptLeaveRegister(sessionBean),event.getItem(),"leaveRegister");
				}
				
				// LEAVE REGISTER
				if(event.getItem().toString().equalsIgnoreCase("LEAVE REGISTER_CHO"))
				{
					showWindow(new RptLeaveRegisterCHO(sessionBean),event.getItem(),"leaveRegisterCHO");
				}


				// INDIVIDUAL LEAVE REGISTER
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL LEAVE REGISTER"))
				{
					showWindow(new RptLeaveRegisterIndividual(sessionBean),event.getItem(),"leaveRegisterIndividual");
				}
				

				// INDIVIDUAL LEAVE REGISTER
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL LEAVE REGISTER_CHO"))
				{
					showWindow(new RptLeaveRegisterIndividualCHO(sessionBean),event.getItem(),"leaveRegisterIndividualCHO");
				}

				if(event.getItem().toString().equalsIgnoreCase("TOUR APPLICATION"))
				{
					showWindow(new RptTourApplication(sessionBean),event.getItem(),"rptTourApplication");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("TOUR APPLICATION_CHO"))
				{
					showWindow(new RptTourApplicationCHO(sessionBean),event.getItem(),"rptTourApplicationCHO");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("LEAVE ENCASHMENT REPORT"))
				{
					showWindow(new RptLeaveEncashment(sessionBean),event.getItem(),"rptLeaveEncashment");
				}
				
						
				if(event.getItem().toString().equalsIgnoreCase("LEAVE ENCASHMENT REPORT_CHO"))
				{
					showWindow(new RptLeaveEncashmentCHO(sessionBean),event.getItem(),"rptLeaveEncashmentCHO");
				}
				

				
				
				// LOAN APPLICATION
				if(event.getItem().toString().equalsIgnoreCase("LOAN APPLICATION"))
				{
					showWindow(new RptLoanApplication(sessionBean),event.getItem(),"laonApplication");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("LOAN APPLICATION_CHO"))
				{
					showWindow(new RptLoanApplicationCHO(sessionBean),event.getItem(),"laonApplicationCHO");
				}

				// INDIVIDUAL LOAN STATEMENT
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL LOAN STATEMENT"))
				{
					showWindow(new RptIndividualLoanStatement(sessionBean),event.getItem(),"individualLoanStatement");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL LOAN STATEMENT_CHO"))
				{
					showWindow(new RptIndividualLoanStatementCHO(sessionBean),event.getItem(),"individualLoanStatementCHO");
				}

				// LOAN REGISTER
				if(event.getItem().toString().equalsIgnoreCase("LOAN REGISTER"))
				{
					showWindow(new RptLoanRegister(sessionBean),event.getItem(),"loanRegister");
				}
				if(event.getItem().toString().equalsIgnoreCase("LOAN REGISTER_CHO"))
				{
					showWindow(new RptLoanRegisterCHO(sessionBean),event.getItem(),"loanRegisterCHO");
				}
				//Loan Report End
			}
		});
	}

	@SuppressWarnings("serial")
	private void showWindow(Window win, Object selectedItem,String mid)
	{
		try
		{
			final String id = selectedItem+"";

			if(!sessionBean.getAuthenticWindow())
			{
				if(isOpen(id))
				{
					win.center();
					win.setStyleName("cwindow");

					component.getWindow().addWindow(win);
					win.setCloseShortcut(KeyCode.ESCAPE);

					winMap.put(id,id);

					win.addListener(new Window.CloseListener() 
					{
						public void windowClose(CloseEvent e) 
						{
							winMap.remove(id);                	
						}
					});
				}
			}
			else
			{
				sessionBean.setPermitForm(mid,selectedItem.toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private  boolean isOpen(String id)
	{
		return !winMap.containsKey(id);
	}
}