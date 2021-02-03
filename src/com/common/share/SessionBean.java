package com.common.share;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

public class SessionBean 
{
	private boolean isAdmin;
	private boolean isSuperAdmin;
	private String userId;
	private String userName;
	private String userIp = "";
	private boolean authenticWindow ;
	private TextField permitFormTxtField;
	private Button permitBtn;
	private Button nopermitBtn;
	private String companyId;
	private String companyName;
	private String companyLogo;
	private String companyAddress;
	private String phonefaxemail;
	private boolean submitable;
	private boolean updateable;
	private boolean deleteable;
	private Date fiscalOpenDate;
	private Date fiscalCloseDate;
	private String fiscalRunningSerial;
	private Object url ;

	private String contextNname;
	private String war;

	private static Date asOnDate;
	private static Date asFromDate;
	private static Object p;

	public static String report = "report";

	public static String backPath = "D:/backup/";

	//project name should be updated for new project according to project name
	//public static String projectName = "";
	public static String projectName = "astecherp";
	public static Object folder_cpv="cashBillPayment";
	//public static String imagePath = "D:/Tomcat 7.0/webapps/report/";
	//public static String imagePathTmp = "D:/Tomcat 7.0/webapps/report/";
	public static String imagePath = "D:/Attachments/";
	public static String imagePathTmp = "D:/Attachments/";
	public static String Purchase = "D:/Attachments/";
	// attachment file link to upload
	public static String imageLogo = "";// "D:/Tomcat 7.0/webapps/report/uptd/";
	public static String supplierLogo = "";// "D:/Tomcat 7.0/webapps/report/uptd/supplier/";
	public static String employeeImage = "";// "D:/Tomcat 7.0/webapps/report/uptd/employee/";
	public static String ProductImage = "";// "D:/Tomcat 7.0/webapps/report/uptd/Product/";
	public static String employeeBirth = "";// "D:/Tomcat 7.0/webapps/report/uptd/employee/birth/";
	public static String employeeNid = "";// "D:/Tomcat 7.0/webapps/report/uptd/employee/nid/";
	public static String employeeApplication = "";// "D:/Tomcat 7.0/webapps/report/uptd/employee/application/";
	public static String employeeJoin = "";// "D:/Tomcat 7.0/webapps/report/uptd/employee/join/";
	public static String vehicleMaintenBillImage = "";// "D:/Tomcat 7.0/webapps/report/uptd/vehicleBill/";
	public static String Requisition = "";// "D:/Tomcat 7.0/webapps/report/uptd/Requisition/";
	// "D:/Tomcat 7.0/webapps/report/uptd/Purchase/";
	
	public static String emailPath = "D:/Tomcat 7.0/webapps/report/astecherp/Email/";
	public static String emailPathmachine = "D:/Tomcat 7.0/webapps/report/astecherp/Machine/";
	public static String emailPathmould = "D:/Tomcat 7.0/webapps/report/astecherp/Mould/";
	public static String emailPathsemifg = "D:/Tomcat 7.0/webapps/report/astecherp/Semifg/";
	public static String emailPathfglabeling = "D:/Tomcat 7.0/webapps/report/astecherp/Fglabelingprinting/";
	public static String emailPathmasterproduct = "D:/Tomcat 7.0/webapps/report/astecherp/MasterProduct/";
	public static String emailpathproducformulation = "D:/Tomcat 7.0/webapps/report/astecherp/Formulation/";
	public static String emailpathpinkformulation = "D:/Tomcat 7.0/webapps/report/astecherp/Inkformulation/";
	
	public boolean setupModule;
	public boolean rawMeterialModule;
	public boolean productionModule;
	public boolean finishGoodsModule;
	public boolean PoSalesModule;
	public boolean accountsModule;
	public boolean fixedAssetModule;
	public boolean hrmModule;
	public boolean transportModule;
	public boolean lcModule;
	public boolean costingModule;
	public boolean sparePartsModule;
	
	public boolean crashingModule;
	public boolean thirdpartyrmModule;
	
	public SimpleDateFormat dfDb = new SimpleDateFormat("yyyy-MM-dd");
	public SimpleDateFormat dfBd = new SimpleDateFormat("dd-MM-yyyy");
	

	public Object getasOnDate()
	{
		return asOnDate;
	}

	public Object getFromDate()
	{
		return asFromDate;
	}

	public boolean isAdmin()
	{
		return isAdmin;
	}

	public boolean isSuperAdmin()
	{
		return isSuperAdmin;
	}

	public String getUserId()
	{
		return userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public Object getUrl()
	{
		return url;
	}

	public String getCompanyId()
	{
		return companyId;
	}

	public String getCompany()
	{
		return companyName;
	}

	public String getCompanyLogo()
	{
		return companyLogo;
	}

	public String getCompanyAddress()
	{
		return companyAddress;
	}

	public String getCompanyContact()
	{
		return phonefaxemail;
	}

	public Date getFiscalOpenDate()
	{
		return fiscalOpenDate;
	}

	public Date getFiscalCloseDate()
	{
		return fiscalCloseDate;
	}

	public String getfiscalRunningSerial()
	{
		return fiscalRunningSerial;
	}

	public boolean getAuthenticWindow()
	{
		return authenticWindow;
	}

	public String getUserIp()
	{
		return userIp;
	}

	public boolean isUpdateable()
	{
		return updateable;
	}

	public boolean isDeleteable()
	{
		return deleteable;
	}

	public boolean isSubmitable()
	{
		return submitable;
	}

	public String getContextName()
	{
		return contextNname;
	}

	////////////// SET //////////////////
	public void setCompanyId(String companyId)
	{
		this.companyId = companyId;
	}
	public void setCompany(String companyname)
	{
		this.companyName = companyname;
	}
	public void setCompanyLogo(String companyLogo)
	{
		this.companyLogo = companyLogo;
	}
	public void setCompanyAddress(String companyAddress)
	{
		this.companyAddress = companyAddress;
	}	
	public void setCompanyContact(String phonefaxemail)
	{
		this.phonefaxemail = phonefaxemail;
	}
	public void setUrl(Object url)
	{
		this.url = url;
	}
	public void setUserName(String uname)
	{
		userName = uname;
	}
	public void setUserId(String uid)
	{
		userId = uid;
	}
	public void isAdmin(boolean ia)
	{
		isAdmin = ia;
	}
	public void isSuperAdmin(boolean ia)
	{
		isSuperAdmin = ia;
	}
	public void setAuthenticWindow(boolean authenticWindow)
	{
		this.authenticWindow = authenticWindow;
	}

	public void setPermitBtn(Button permitBtn)
	{
		this.permitBtn = permitBtn;
	}
	public void setNoPermitBtn(Button nopermitBtn)
	{
		this.nopermitBtn = nopermitBtn;
	}
	public void setPermitFormTxt(TextField permitFormTxtField)
	{
		this.permitFormTxtField = permitFormTxtField;
	}
	public void setPermitForm(String menuId,String txt)
	{
		//this.menuId = menuId;
		permitFormTxtField.setDebugId(menuId);
		permitFormTxtField.setReadOnly(false);
		permitFormTxtField.setValue(txt);
		permitFormTxtField.setReadOnly(true);
		permitBtn.setEnabled(false);
		nopermitBtn.setEnabled(true);
	}

	public void setAsOnDate(Object asdate)
	{
		asOnDate = (Date)asdate;
	}
	public void setFromDate(Object asdate)
	{
		asFromDate = (Date)asdate;
	}


	public void setSubmitable(boolean sb)
	{
		submitable = sb;
	}
	public void setUpdateable(boolean sb)
	{
		updateable = sb;
	}

	public void setDeleteable(boolean sb)
	{
		deleteable = sb;
	}

	public void setFiscalOpenDate(Object fiscalOpenDate)
	{
		this.fiscalOpenDate = (Date) fiscalOpenDate;
	}
	public void setFiscalCloseDate(Object fiscalCloseDate)
	{
		this.fiscalCloseDate =(Date) fiscalCloseDate;
	}
	public void setFiscalRunningSerial(Object fiscalrunning)
	{
		this.fiscalRunningSerial = (String) fiscalrunning;
	}

	public void setUserIp(String userIp)
	{
		this.userIp = userIp;
	}
	public void setContextName(String contextname)
	{
		this.contextNname = contextname;
	}
	public void setWar(String war){
		this.war = war;
	}
	public String getWar(){
		return war;
	}

	public void setP(Object p){
		this.p = p;
	}

	public Object getP(){
		return p;
	}
	public void setfolder_cpv()
	{
		this.folder_cpv =  folder_cpv;
	}
}

