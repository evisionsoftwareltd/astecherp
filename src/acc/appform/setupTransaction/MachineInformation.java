package acc.appform.setupTransaction;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Properties;

//import javax.activation.DataHandler;
//import javax.activation.FileDataSource;
//import javax.mail.Multipart;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperRunManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.setupTransaction.SupplierInformation;


import java.io.File;
//import java.io.FileInputStream;
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

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class MachineInformation extends Window 
{
	private TextField txtMachineId = new TextField();
	private AbsoluteLayout mainLayout;

	private Label lblMachineCode;
	private TextField txtMachineCode;
	
	private Label lblProductionType;
	private ComboBox cmbProductionType;
	
	private Label lblMachineSerial;
	private TextField txtMachineSerial;
	
	private Label lblMachineModel;
	private TextField txtMachineModel;
	
	private Label lblMachineCapacity;
	private TextField txtMachineCapacity;

	private Label lblMachineName;
	private TextField txtMachineName;
	private Label lblExistMachineName;

	private Label lblSupplierName;
	private ComboBox cmbSupplierName;	

	private Label lblPurchaseDate;
	private PopupDateField dPurchaseDate;

	private Label lblLocation;
	private ComboBox cmbLocation;

	private Label lblPurchasePrice;
	private AmountCommaSeperator AmtPurchasePrice;

	private NativeButton nbSupplier;
	private NativeButton nbLocation;

	boolean isUpdate=false;
	boolean isFind=false;
	SessionBean sessionBean;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	OptionGroup optGroup;
	private static final List<String>optStatus  = Arrays.asList(new String[] {"Active" ,"Inactive" });
	private FileWriter log;

	public MachineInformation(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("MACHINE INFORMATION :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		focusEnter();
		cmbSupplierAddData();
		cmbLocationAddData();
		authenticationCheck();
		setEventAction();
		productionTypeData();
	}
	private void productionTypeData() {
		cmbProductionType.removeAllItems();
		Transaction tx=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery("  select productTypeId,productTypeName from tbProductionType").list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbProductionType.addItem(element[0].toString());
				cmbProductionType.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable())
		{
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable())
		{
			button.btnDelete.setVisible(false);
		}
	}

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = true;
				newButtonEvent();
				focusEnter();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sessionBean.isUpdateable())
				{
					isUpdate = true;
					isFind = true;
					updateButtonEvent();
					focusEnter();
					
					
				}
				else
				{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				formValidation();

			}
		});

		button.btnExit.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}
		});

		nbSupplier.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				supplierLink();				
			}
		});

		nbLocation.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				locationLink();				
			}
		});
		
		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {

				isFind = true;
				findButtonEvent();
				txtMachineCode.setEnabled(false);
				isFind = false;
			}
		});
		
		txtMachineCode.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(isFind)
				{
					if(!txtMachineCode.getValue().toString().isEmpty())
					{
						if(duplicateCode())
						{
							lblExistMachineName.setVisible(true);
							lblExistMachineName.setValue("<b><Font Color='#CD0606'>! Already Exist</Font></b>");
							txtMachineCode.setValue("");
							txtMachineCode.focus();
						}
						else
						{	
							lblExistMachineName.setVisible(false);
						}
					}
				}
			}
		});
		button.btnDelete.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				deleteButtonEvent();
			}
		});
	}
	private boolean checkReference(){
		String sql="select machineId from tbMixtureIssueEntryDetails where machineId='"+txtMachineCode.getValue()+"' "+
				" union select MachineName from tbMouldProductionDetails where MachineName='"+txtMachineCode.getValue()+"' "+
				" union select machineCode from tbLabelingPrintingDailyProductionDetails where machineCode='"+txtMachineCode.getValue()+"'";
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext()){
			return false;

		}
		return true;
	}
	private boolean deleteData(Session session,Transaction tx){
		try{
			session.createSQLQuery("delete from tbMachineInfo where vMachineCode = '"+txtMachineCode.getValue().toString()+"'").executeUpdate();
			tx.commit();
			return true;
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			return false;
		} 
	}
	private void deleteButtonEvent(){
		if(!txtMachineCode.getValue().toString().isEmpty()){
			if(checkReference()){

				MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Delete ?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							Transaction tx=null;
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							tx = session.beginTransaction();
							if(deleteData(session,tx)){
								btnIni(true);
								componentIni(true);
								txtClear();
								button.btnNew.focus();
								showNotification("Delete Data Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
							}
						}
					}
				});	
			}
			else{
				showNotification("Referenced Data Can't be delete",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("There is no Data",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	public void cmbSupplierAddData()
	{
		cmbSupplierName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select supplierId,supplierName from tbSupplierInfo where isActive='1' ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplierName.addItem(element[0].toString());
				cmbSupplierName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{}
	}

	public void cmbLocationAddData()
	{
		cmbLocation.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select AutoID,SectionName from tbSectionInfo ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbLocation.addItem(element[0].toString());
				cmbLocation.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{}
	}
	
	private void updateButtonEvent()
	{
		if(!txtMachineName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
			txtMachineCode.setEnabled(true);
		}
		else{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void formValidation()
	{
		if(sessionBean.isSubmitable()){
			if(!txtMachineCode.getValue().toString().isEmpty()){
				if(!txtMachineName.getValue().toString().isEmpty()){
					if(cmbSupplierName.getValue()!=null){
						if(dPurchaseDate.getValue()!=null){
							if(!AmtPurchasePrice.getValue().toString().isEmpty()){
								saveButtonEvent();
							}else{
								getParent().showNotification("Warning","Provide Perchase Price",Notification.TYPE_WARNING_MESSAGE);
								AmtPurchasePrice.focus();
							}
						}else{
							getParent().showNotification("Warning","Select Date",Notification.TYPE_WARNING_MESSAGE);
							dPurchaseDate.focus();
						}
					}else{
						getParent().showNotification("Warning","Please select Supplier Name",Notification.TYPE_WARNING_MESSAGE);
						cmbSupplierName.focus();
					}

				}else{
					getParent().showNotification("Warning","Please Provide Machine Name",Notification.TYPE_WARNING_MESSAGE);
					txtMachineName.focus();
				}
			}else{
				getParent().showNotification("Warning","There is no Machine Code",Notification.TYPE_WARNING_MESSAGE);
				txtMachineCode.focus();
			}
		}
	}

	private void findButtonEvent() 
	{
		Window win = new MachineFindWindow(sessionBean, txtMachineId,"MachineId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtMachineId.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtMachineId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private boolean duplicateCode()
	{
		String MachineName="";

		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " select vMachineCode from tbMachineInfo where vMachineCode='"+txtMachineCode.getValue().toString().trim()+"' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
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

	private void findInitialise(String txtMachineId) 
	{
		Transaction tx = null;
		String sql = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select * from tbMachineInfo where iAutoId = '"+txtMachineId+"' ";
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtMachineCode.setValue(element[1]);
				txtMachineName.setValue(element[2]);
				cmbSupplierName.setValue(element[3].toString());
				cmbLocation.setValue(element[5].toString());
				dPurchaseDate.setValue(element[7]);
				AmtPurchasePrice.setValue(element[8].toString());
				txtMachineSerial.setValue(element[12]);
				txtMachineModel.setValue(element[13]);
				txtMachineCapacity.setValue(element[14]);
				cmbProductionType.setValue(element[15]);
				optGroup.setValue(element[17]);
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	public void supplierLink()
	{
		Window win = new SupplierInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbSupplierAddData();
			}
		});
		this.getParent().addWindow(win);
	}

	public void locationLink()
	{
		Window win = new DeptInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbLocationAddData();
			}
		});
		this.getParent().addWindow(win);
	}

	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
						//emailSend() ;
						isUpdate = false;
						btnIni(true);
						componentIni(true);
						txtClear();
						button.btnNew.focus();
					}
				}
			});																	
		}
		else
		{									
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						isUpdate = false;
						btnIni(true);
						componentIni(true);
						txtClear();
						button.btnNew.focus();
					}
				}
			});
		}
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
	
	private void reportGenerate(String iclientId, String fpath) throws HibernateException, JRException, IOException 
	{	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String query = "";
		ReportDate reportTime = new ReportDate();

		query=     "select  vFlag, (select name from tbLogin where tbLogin.userId=tbUdMachine.UserId)UserId, UserIp,EntryTime,status,vMachineCode,vMachineName,vSupplierId, "
				   +"vSupplierName,vDeptId,vDeptName,machineSerial,machineModel,machineCapacity,dPurchaseDate,cast(vPurchaseAmount as money)vPurchaseAmount,vFlag  from tbUdMachine "
				   +"where iAutoId='"+txtMachineId.getValue().toString()+"' order by sl desc ";
				   
		
		if(queryValueCheck(query))
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			//hm.put("phone", "Phone : "+sessionBean.get+" Fax : "+sessionBean.getCompanyFax()+" E-Mail : "+sessionBean.getCompanyEmail());
			//System.out.println(sessionBean.getCompanyPhone());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("URL",getApplication().getURL().toString().replace("uptd/", ""));
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("parentType", "Machine Information");
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("sql", query);
			System.out.println("Done!!");
			FileOutputStream of = new FileOutputStream(fpath);
				
            try
            {
            	JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream("report/RptUdMachineInfo.jasper"), of, hm,session.connection());
    			System.out.println("Done!!");	
            }
            
            catch(Exception ex)
            {
              System.out.print("Exception is:"+ex)	;
            }
			
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
			File f = new File(sessionBean.emailPathmachine);
			f.mkdirs();
			System.out.printf("3");
			System.out.printf("f"+f);
			String MasterId="";
			log = new FileWriter("D:/Tomcat 7.0/webapps/report/astecherp/Machine/log.txt");
			System.out.printf("log"+log);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			
			String host = "smtp.gmail.com";
			String from = "";
			String pass = "";
			
			
			from="evisionsoftwareltd@gmail.com";
			pass="786@esl10";
			

			String EmailTo="support@eslctg.com";
			String EmailSubject="Machine Information Edit";
			String EmailTxt="Machine Name "+txtMachineName.getValue().toString()+" has been Edited \n"+
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
			MasterId=txtMachineId.getValue().toString();
			
			System.out.printf("4");
			System.out.printf("\n4.1"+MasterId);
			reportGenerate(MasterId,sessionBean.emailPathmachine+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			
			
			
			MimeMessage message = new MimeMessage(esession);
			
			
			message.setFrom(new InternetAddress(from));
			
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("sanjoy@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("ahmedtalba@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("support@eslctg.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("rabiulbgcctg2016@gmail.com"));
			
			
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("nazimesl@yahoo.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("emdidar@gmail.com"));
			
			
			
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("akidahmed@astechbd.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("sharif@astechbd.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("ashim@astechbd.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("hr.desk@astechbd.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("nazimesl@yahoo.com"));
			
			
			
			message.setSubject(EmailSubject);
			message.setText(EmailTxt);
			System.out.printf("7");
			// create the message part 
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			//fill message
			messageBodyPart.setText(EmailTxt);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			
			DataSource source = (DataSource) new FileDataSource(sessionBean.emailPathmachine+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			System.out.println("Hello Banglaedsh3");
			messageBodyPart.setDataHandler( new DataHandler((javax.activation.DataSource) source));
			//messageBodyPart.setFileName(sessionBean.emailPathmachine+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			
			messageBodyPart.setFileName(MasterId+"_"+"_"+EmailSubject+".pdf");
			multipart.addBodyPart(messageBodyPart);
			System.out.printf("9");
			// Put parts in message
			message.setContent(multipart);
			System.out.printf("10");
			Transport transport = esession.getTransport("smtp");
			System.out.println(sessionBean.emailPathmachine+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
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
	
	
	
	public int autoId()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String sql="select ISNULL(MAX(iAutoId),0)+1  from tbMachineInfo";
		List lst=session.createSQLQuery(sql).list();
		Iterator<?>itr=lst.iterator();
		if(itr.hasNext())
		{
		   return (Integer) itr.next();	
		}
		
		
		return 0;
	}

	private void insertData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String locationId="";
			String locationName="";
			int id=0;
			id=autoId();
			if(cmbLocation.getValue()!=null)
			{
				locationId=cmbLocation.getValue().toString();
				locationName=cmbLocation.getItemCaption(cmbLocation.getValue());
			}

			String InsertProduct = " INSERT into tbMachineInfo values(" +
					" '"+txtMachineCode.getValue().toString().trim()+"'," +
					" '"+txtMachineName.getValue().toString().trim()+"'," +
					" '"+cmbSupplierName.getValue()+"'," +
					" '"+cmbSupplierName.getItemCaption(cmbSupplierName.getValue())+"'," +
					" '"+locationId+"'," +
					" '"+locationName+"'," +
					" '"+dateFormat.format(dPurchaseDate.getValue())+"'," +
					" '"+AmtPurchasePrice.getValue().toString().trim()+"'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					"'"+txtMachineSerial.getValue()+"','"+txtMachineModel.getValue()+"'," +
					"'"+txtMachineCapacity.getValue()+"','"+cmbProductionType.getValue()+"'," +
					"'"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"','"+optGroup.getValue()+"') ";
			
			
			

			System.out.println("Insertquery : "+InsertProduct);
			session.createSQLQuery(InsertProduct).executeUpdate();
			
			String udquery = " INSERT into tbUdMachine values(   "  +
					" '"+txtMachineCode.getValue().toString().trim()+"'," +
					" '"+txtMachineName.getValue().toString().trim()+"'," +
					" '"+cmbSupplierName.getValue()+"'," +
					" '"+cmbSupplierName.getItemCaption(cmbSupplierName.getValue())+"'," +
					" '"+locationId+"'," +
					" '"+locationName+"'," +
					" '"+dateFormat.format(dPurchaseDate.getValue())+"'," +
					" '"+AmtPurchasePrice.getValue().toString().trim()+"'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					"'"+txtMachineSerial.getValue()+"','"+txtMachineModel.getValue()+"'," +
					"'"+txtMachineCapacity.getValue()+"','"+cmbProductionType.getValue()+"'," +
					"'"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"','"+optGroup.getValue()+"','NEW') ";
			
			
			

			System.out.println("updatequey : "+udquery);
		
			session.createSQLQuery(udquery).executeUpdate();
			
			
			
			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private boolean updateData()
	{	
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String locationId="";
			String locationName="";
			if(cmbLocation.getValue()!=null)
			{
				locationId=cmbLocation.getValue().toString();
				locationName=cmbLocation.getItemCaption(cmbLocation.getValue());
			}
			
			String udSql="";
			 udSql="insert into tbUdMachine(vMachineCode,vMachineName,vSupplierId,vSupplierName,vDeptId,vDeptName, "
					    +"dPurchaseDate,vPurchaseAmount,UserId,UserIp,EntryTime,machineSerial,machineModel,machineCapacity,productionTypeId,productionTypeName, "
					    +"status,vFlag) select  vMachineCode,vMachineName,vSupplierId,vSupplierName,vDeptId,vDeptName, "
					    + "dPurchaseDate,vPurchaseAmount,UserId,UserIp,EntryTime,machineSerial,machineModel,machineCapacity, "
					    +"productionTypeId,productionTypeName,status,'Update' as flag from tbMachineInfo "
					    +"where iAutoId='"+txtMachineId+"' ";
			session.createSQLQuery(udSql).executeUpdate();


			String updateProduct ="UPDATE tbMachineInfo set" +
					" vMachineCode = '"+txtMachineCode.getValue().toString().trim()+"' ," +
					" vMachineName = '"+txtMachineName.getValue().toString().trim()+"' ," +
					" vSupplierId = '"+cmbSupplierName.getValue()+"' ," +
					" vSupplierName = '"+cmbSupplierName.getItemCaption(cmbSupplierName.getValue())+"' ," +
					" vDeptId = '"+cmbLocation.getValue()+"' ," +
					" vDeptName = '"+cmbLocation.getItemCaption(cmbLocation.getValue())+"' ," +
					" dPurchaseDate = '"+dPurchaseDate.getValue()+"' ," +
					" vPurchaseAmount = '"+AmtPurchasePrice.getValue().toString().trim()+"' ," +
					" UserId = '"+sessionBean.getUserId()+"', " +
					" UserIp = '"+sessionBean.getUserIp()+"', " +
					" EntryTime = CURRENT_TIMESTAMP ," +
					" machineSerial = '"+txtMachineSerial.getValue()+"' ," +
					" machineModel = '"+txtMachineModel.getValue()+"' ," +
					" machineCapacity = '"+txtMachineCapacity.getValue()+"' ," +
					" productionTypeId = '"+cmbProductionType.getValue()+"' ," +
					" productionTypeName = '"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"'," +
					" status = '"+optGroup.getValue()+"'  " +
					" where iAutoId='"+txtMachineId+"'";
			session.createSQLQuery(updateProduct).executeUpdate();
			this.getParent().showNotification("All information update successfully.");
			
			 udSql = " INSERT into tbUdMachine values(   "  +
					" '"+txtMachineCode.getValue().toString().trim()+"'," +
					" '"+txtMachineName.getValue().toString().trim()+"'," +
					" '"+cmbSupplierName.getValue()+"'," +
					" '"+cmbSupplierName.getItemCaption(cmbSupplierName.getValue())+"'," +
					" '"+locationId+"'," +
					" '"+locationName+"'," +
					" '"+dateFormat.format(dPurchaseDate.getValue())+"'," +
					" '"+AmtPurchasePrice.getValue().toString().trim()+"'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					"'"+txtMachineSerial.getValue()+"','"+txtMachineModel.getValue()+"'," +
					"'"+txtMachineCapacity.getValue()+"','"+cmbProductionType.getValue()+"'," +
					"'"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"','"+optGroup.getValue()+"','NEW') ";
			
			session.createSQLQuery(udSql).executeUpdate();
			
			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		cmbProductionType.focus();
	}

	private void focusEnter()
	{
		/*allComp.add(txtMachineCode);
		allComp.add(txtMachineName);
		allComp.add(cmbSupplierName);
		allComp.add(cmbLocation);
		allComp.add(dPurchaseDate);
		allComp.add(AmtPurchasePrice);			
		allComp.add(button.btnSave);
		 */
		new FocusMoveByEnter(this, new Component[]{cmbProductionType,txtMachineCode,txtMachineName,txtMachineSerial,txtMachineModel,txtMachineCapacity,cmbSupplierName,cmbLocation,dPurchaseDate,AmtPurchasePrice,button.btnSave});
	}

	public void txtClear()
	{
		cmbProductionType.setValue(null);
		txtMachineCode.setValue("");
		txtMachineName.setValue("");
		cmbSupplierName.setValue(null);
		cmbLocation.setValue(null);
		AmtPurchasePrice.setValue("");
		lblExistMachineName.setValue("");
		txtMachineCapacity.setValue("");
		txtMachineModel.setValue("");
		txtMachineSerial.setValue("");
		optGroup.setValue("Active");
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnFind.setEnabled(t);		
	}

	private void componentIni(boolean b) 
	{
		cmbProductionType.setEnabled(!b);
		txtMachineName.setEnabled(!b);
		cmbSupplierName.setEnabled(!b);
		cmbLocation.setEnabled(!b);
		dPurchaseDate.setEnabled(!b);
		AmtPurchasePrice.setEnabled(!b);
		nbSupplier.setEnabled(!b);
		nbLocation.setEnabled(!b);
		txtMachineCapacity.setEnabled(!b);
		txtMachineModel.setEnabled(!b);
		txtMachineSerial.setEnabled(!b);
		optGroup.setEnabled(!b);
		if(isUpdate)
		{
			txtMachineCode.setEnabled(false);
		}
		else
		{
			txtMachineCode.setEnabled(!b);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("600px");
		setHeight("400px");
		
		lblProductionType=new Label();
		lblProductionType.setValue("Production Type :");
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		mainLayout.addComponent(lblProductionType,"top:20.0px;left:50.0px;");


		cmbProductionType=new ComboBox();
		cmbProductionType.setWidth("200px");
		cmbProductionType.setImmediate(true);
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbProductionType, "top:17.0px;left:200.0px;");
		

		lblMachineCode = new Label();
		lblMachineCode.setImmediate(true);
		lblMachineCode.setWidth("100.0%");
		lblMachineCode.setHeight("18px");
		lblMachineCode.setValue("Machine Code: ");
		mainLayout.addComponent(lblMachineCode,"top:45.0px;left:50.0px;");

		txtMachineCode = new TextField();
		txtMachineCode.setImmediate(false);
		txtMachineCode.setWidth("-1px");
		txtMachineCode.setHeight("-1px");
		mainLayout.addComponent(txtMachineCode, "top:42.0px;left:200.0px;");

		lblMachineName = new Label();
		lblMachineName.setWidth("-1px");
		lblMachineName.setHeight("-1px");
		lblMachineName.setValue("Machine Name: ");
		mainLayout.addComponent(lblMachineName, " top:70.0px;left:50.0px;");

		txtMachineName = new TextField();
		txtMachineName.setImmediate(true);
		txtMachineName.setWidth("200px");
		txtMachineName.setHeight("-1px");
		mainLayout.addComponent(txtMachineName, "top:67.0px;left:200.0px;");

		lblMachineSerial = new Label();
		lblMachineSerial.setWidth("-1px");
		lblMachineSerial.setHeight("-1px");
		lblMachineSerial.setValue("Machine Serial: ");
		mainLayout.addComponent(lblMachineSerial, " top:95.0px;left:50.0px;");

		txtMachineSerial = new TextField();
		txtMachineSerial.setImmediate(true);
		txtMachineSerial.setWidth("150px");
		txtMachineSerial.setHeight("-1px");
		mainLayout.addComponent(txtMachineSerial, "top:92.0px;left:200.0px;");
		
		lblMachineModel = new Label();
		lblMachineModel.setWidth("-1px");
		lblMachineModel.setHeight("-1px");
		lblMachineModel.setValue("Machine Model: ");
		mainLayout.addComponent(lblMachineModel, " top:120.0px;left:50.0px;");

		txtMachineModel = new TextField();
		txtMachineModel.setImmediate(true);
		txtMachineModel.setWidth("150px");
		txtMachineModel.setHeight("-1px");
		mainLayout.addComponent(txtMachineModel, "top:117.0px;left:200.0px;");
		
		lblMachineCapacity = new Label();
		lblMachineCapacity.setWidth("-1px");
		lblMachineCapacity.setHeight("-1px");
		lblMachineCapacity.setValue("Machine Capacity: ");
		mainLayout.addComponent(lblMachineCapacity, " top:145.0px;left:50.0px;");

		txtMachineCapacity = new TextField();
		txtMachineCapacity.setImmediate(true);
		txtMachineCapacity.setWidth("150px");
		txtMachineCapacity.setHeight("-1px");
		mainLayout.addComponent(txtMachineCapacity, "top:143.0px;left:200.0px;");
		
		lblExistMachineName = new Label();
		lblExistMachineName.setWidth("-1px");
		lblExistMachineName.setHeight("-1px");
		lblExistMachineName.setImmediate(true);
		lblExistMachineName.setContentMode(Label.CONTENT_XHTML);
		lblExistMachineName.setVisible(false);
		lblExistMachineName.setValue("");
		mainLayout.addComponent(lblExistMachineName, " top:55.0px;left:450.0px;");

		lblSupplierName= new Label();
		lblSupplierName.setImmediate(false);
		lblSupplierName.setWidth("-1px");
		lblSupplierName.setHeight("-1px");
		lblSupplierName.setValue("Supplier Name: ");
		mainLayout.addComponent(lblSupplierName, " top:170.0px;left:50.0px;");

		cmbSupplierName= new ComboBox();
		cmbSupplierName.setImmediate(false);
		cmbSupplierName.setWidth("200px");
		cmbSupplierName.setHeight("24px");
		mainLayout.addComponent(cmbSupplierName, "top:167.0px;left:200.0px;");

		nbSupplier = new NativeButton();
		nbSupplier.setIcon(new ThemeResource("../icons/add.png"));
		nbSupplier.setImmediate(true);
		nbSupplier.setWidth("30px");
		nbSupplier.setHeight("25px");
		mainLayout.addComponent(nbSupplier, " top:167.0px;left:400.0px;");

		lblLocation = new Label();
		lblLocation.setWidth("-1px");
		lblLocation.setHeight("-1px");
		lblLocation.setValue("Location / Department : ");
		mainLayout.addComponent(lblLocation, " top:195.0px;left:50.0px;");

		cmbLocation= new ComboBox();
		cmbLocation.setImmediate(false);
		cmbLocation.setWidth("200px");
		cmbLocation.setHeight("-1px");
		cmbLocation.setImmediate(true);
		mainLayout.addComponent(cmbLocation, "top:192.0px;left:200.0px;");

		nbLocation = new NativeButton();
		nbLocation.setIcon(new ThemeResource("../icons/add.png"));
		nbLocation.setImmediate(true);
		nbLocation.setWidth("30px");
		nbLocation.setHeight("25px");
		mainLayout.addComponent(nbLocation, " top:192.0px;left:400.0px;");

		lblPurchaseDate = new Label();
		lblPurchaseDate.setImmediate(false);
		lblPurchaseDate.setWidth("-1px");
		lblPurchaseDate.setHeight("-1px");
		lblPurchaseDate.setValue("Purchase Date: ");
		mainLayout.addComponent(lblPurchaseDate, "top:220.0px;left:50.0px;");

		dPurchaseDate = new PopupDateField();
		dPurchaseDate.setImmediate(true);
		dPurchaseDate.setWidth("110px");
		dPurchaseDate.setHeight("24px");
		dPurchaseDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dPurchaseDate.setValue(new java.util.Date());
		dPurchaseDate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(dPurchaseDate, "top:217.0px;left:200.0px;");

		lblPurchasePrice = new Label();
		lblPurchasePrice.setImmediate(false);
		lblPurchasePrice.setWidth("-1px");
		lblPurchasePrice.setHeight("-1px");
		lblPurchasePrice.setValue("Purchase Amount :");
		mainLayout.addComponent(lblPurchasePrice, "top:245.0px;left:50.0px;");

		AmtPurchasePrice= new AmountCommaSeperator();
		AmtPurchasePrice.setImmediate(true);
		AmtPurchasePrice.setWidth("-1px");
		AmtPurchasePrice.setHeight("-1px");
		mainLayout.addComponent(AmtPurchasePrice, "top:242.0px;left:200.0px;");
		
		optGroup=new OptionGroup("", optStatus);
		optGroup.setImmediate(true);
		optGroup.setStyleName("horizontal");
		optGroup.setValue("Active");
		mainLayout.addComponent(new Label("Status"), "top:270.0px;left:50.0px;");
		mainLayout.addComponent(optGroup, "top:268.0px;left:200.0px;");

		mainLayout.addComponent(button, "top:300.0px;left:10.0px;");

		return mainLayout;
	}
}
