package com.example.productionSetup;



import java.io.File;
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
import java.util.StringTokenizer;

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
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

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
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window.Notification;
public class InkFormulation extends Window{

	SessionBean sessionBean;
	AbsoluteLayout mainLayout;
	ComboBox cmbFgName;
	TextRead txtUnit,txtJobNo;
	PopupDateField dDeclaration;
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "", "", "Exit");
	OptionGroup optGroup;
	private static final List<String>optStatus  = Arrays.asList(new String[] {"Active" ,"Inactive" });
	OptionGroup optLacqure;
	private static final List<String>optStatusLacqure  = Arrays.asList(new String[] {"YES" ,"NO" });

	private DecimalFormat df = new DecimalFormat("#0.00000000");
	private DecimalFormat df2 = new DecimalFormat("#0.00");
	private DecimalFormat df1 = new DecimalFormat("#0");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private ArrayList<Label>tbLblSl=new ArrayList<Label>();
	private ArrayList<ComboBox>tbCmbInk=new ArrayList<ComboBox>();
	private ArrayList<TextRead>tbTxtUnit=new ArrayList<TextRead>();
	private ArrayList<AmountField>tbTxtPcsPerKg=new ArrayList<AmountField>();
	private ArrayList<AmountField>tbTxtQtyKg=new ArrayList<AmountField>();
	private ArrayList<AmountField>tbTxtRate=new ArrayList<AmountField>();
	private ArrayList<AmountField>tbTxtAmount=new ArrayList<AmountField>();
	private ArrayList<TextField>tbTxtRemarks=new ArrayList<TextField>();
	private Table tableInk=new Table();

	Panel panelSearch;
	ComboBox cmbFindFG=new ComboBox("FG Name: ");
	ComboBox cmbFindDate=new ComboBox("Declaration Date: ");

	boolean isUpdate = false;
	boolean isFind = false;
	int sl=0;
	Label lblInk=new Label();
	private FileWriter log;

	public InkFormulation(SessionBean sessionBean){
		this.sessionBean=sessionBean;
		this.setCaption("INK FORMULATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		tableInitInk();

		btnIni(true);
		componentIni(true);
		btnAction();
		cmbFgNameLoadData();
		focusMove();
	}
	private void focusMove() {
		ArrayList<Component> allComp = new ArrayList<Component>();

		allComp.add(cmbFgName);
		for(int a=0;a<tbLblSl.size();a++){
			allComp.add(tbCmbInk.get(a));
			allComp.add(tbTxtPcsPerKg.get(a));
			allComp.add(tbTxtRate.get(a));
			allComp.add(tbTxtRemarks.get(a));
		}
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);
		allComp.add(cButton.btnFind);

		new FocusMoveByEnter(this,allComp);
	}
	private Iterator<?> dbService(String sql){
		Session session=SessionFactoryUtil.getInstance().openSession();
		Iterator<?> iter=null;
		try{
			iter= session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}
	private void cmbFgNameLoadData() {
		cmbFgName.removeAllItems();
		Iterator iter=dbService("select semiFgSubId,semiFgSubName from tbSemiFgSubInformation order by semiFgSubName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbFgName.addItem(element[0]);
			cmbFgName.setItemCaption(element[0], element[1].toString());
		}
	}
	protected void newButtonEvent() {
		btnIni(false);
		componentIni(false);
		isUpdate=false;
		isFind=false;
		cmbFgName.focus();
		panelSearch.setEnabled(false);
	}
	private void refreshEvent(){
		isFind=false;
		isUpdate=false;
		btnIni(true);
		txtClear();
		componentIni(true);
		searchtableclear();
		panelSearch.setEnabled(false);
	}
	private void searchtableclear()
	{
		cmbFindFG.setValue(null);
		cmbFindDate.setValue(null);
	}
	private void txtClear() {
		optGroup.setValue("Active");
		optLacqure.setValue("NO");
		dDeclaration.setValue(new java.util.Date());
		cmbFgName.setValue(null);
		txtJobNo.setValue("");
		tableClear();
		tableInk.setColumnFooter("Amount", "Total:"+0.0);
		lblInk.setValue("0.0");

	}
	private void tableClear() {
		for(int x=0;x<tbLblSl.size();x++){
			tbCmbInk.get(x).setValue(null);
			tbTxtUnit.get(x).setValue("");
			tbTxtPcsPerKg.get(x).setValue("");
			tbTxtQtyKg.get(x).setValue("");
			tbTxtRate.get(x).setValue("");
			tbTxtAmount.get(x).setValue("");
			tbTxtRemarks.get(x).setValue("");
		}
	}
	private void updateButtonEvent() {
		if (cmbFgName.getValue() != null&&!tbTxtAmount.get(0).getValue().toString().isEmpty()) 
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
			panelSearch.setEnabled(false);
			System.out.println(isUpdate);
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private boolean qtyZeroCheck(){



		for(int a=0;a<tbLblSl.size();a++){
			if(tbCmbInk.get(a).getValue()!=null){
				if(tbTxtAmount.get(a).getValue().toString().isEmpty()||tbTxtAmount.get(a).getValue().toString().equalsIgnoreCase("0")){
					return false;
				}
			}
		}

		return true;
	}
	private void standardSl()
	{
		Transaction tx=null;
		Session ses=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=ses.beginTransaction();
		Iterator iter=ses.createSQLQuery("select ISNULL(MAX(slFlag),0)+1 from tbUdInkFormulationInfo " +
				"where semiFgSubId='"+cmbFgName.getValue()+"'").list().iterator();
		if(iter.hasNext()){
			sl=Integer.valueOf(iter.next().toString());
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
			File f = new File(sessionBean.emailpathpinkformulation);
			f.mkdirs();
			System.out.printf("3");
			System.out.printf("f"+f);
			String MasterId="";
			log = new FileWriter("D:/Tomcat 7.0/webapps/report/astecherp/Inkformulation/log.txt");
			System.out.printf("log"+log);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			
			String host = "smtp.gmail.com";
			String from = "";
			String pass = "";
			
			
			from="evisionsoftwareltd@gmail.com";
			pass="786@esl10";
			

			String EmailTo="support@eslctg.com";
			String EmailSubject="Ink Formulation Edit";
			String EmailTxt="Product Name "+cmbFgName.getItemCaption(cmbFgName.getValue())+" has been Edited \n"+
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
			MasterId=txtJobNo.getValue().toString();
			
			System.out.printf("4");
			System.out.printf("\n4.1"+MasterId);
			reportGenerate(MasterId,sessionBean.emailpathpinkformulation+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			
			
			
			MimeMessage message = new MimeMessage(esession);
			
			
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("astechfty@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("costing@astechbd.com"));
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
			
			DataSource source = (DataSource) new FileDataSource(sessionBean.emailpathpinkformulation+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
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
			System.out.println(sessionBean.emailpathpinkformulation+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
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

		query=  "select a.userIp,a.userName, a.entryTime, case when   a.type='New' then 'Updated' else 'Updated' end type ,  semiFgSubId,semiFgSubName,rawItemCode,rawItemName,unit,pcsPerKg,QtyInKg,slFlag,status " 
				+"from tbUdInkFormulationInfo a inner join tbUdInkFormulationDetails b on a.jobNo=b.jobNo " 
				+"where semiFgSubId like '"+cmbFgName.getValue().toString()+"' order by a.autoId desc ";
				   
		
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
			hm.put("parentType", "Ink Formulation");
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("sql", query);
			System.out.println("Done!!");
			FileOutputStream of = new FileOutputStream(fpath);
				
            try
            {
            	JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream("report/production/RpudtInkFormulation.jasper"), of, hm,session.connection());
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
	
	
	private void autoJobNo() 
	{
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select 0,isnull(max(CAST(jobNo as int)),0)+1 as jobNo from tbInkFormulationInfo";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				txtJobNo.setValue(element[1]);
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
	}
	private void insertData(Session session,Transaction tx){

		try{
			String type="New";
			if(isUpdate){

				type="Update";
			}
			autoJobNo();
			String inkTotal=lblInk.getValue().toString().isEmpty()?"0.0":lblInk.getValue().toString();

			String sql="insert into tbInkFormulationInfo (jobNo,declareDate,semiFgSubId,semiFgSubName,amount, "+
					" userIp,userName,entryTime,status,slFlag,LacqureFlag)values('"+txtJobNo.getValue()+"'," +
					"'"+dateformat.format(dDeclaration.getValue())+"','"+cmbFgName.getValue()+"'," +
					"'"+cmbFgName.getItemCaption(cmbFgName.getValue())+"','"+inkTotal+"','"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'"+optGroup.getValue()+"','"+sl+"','"+optLacqure.getValue()+"')";
			session.createSQLQuery(sql).executeUpdate();

			String sqlUd="insert into tbUdInkFormulationInfo (jobNo,declareDate,semiFgSubId,semiFgSubName,amount, "+
					" userIp,userName,entryTime,status,slFlag,type,LacqureFlag)values('"+txtJobNo.getValue()+"'," +
					"'"+dateformat.format(dDeclaration.getValue())+"','"+cmbFgName.getValue()+"'," +
					"'"+cmbFgName.getItemCaption(cmbFgName.getValue())+"','"+inkTotal+"','"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP,'"+optGroup.getValue()+"','"+sl+"','"+type+"','"+optLacqure.getValue()+"')";
			session.createSQLQuery(sqlUd).executeUpdate();

			for(int a=0;a<tbCmbInk.size();a++){
				if(tbCmbInk.get(a).getValue()!=null&&!tbTxtAmount.get(a).toString().isEmpty())
				{
					String sql1="insert into tbInkFormulationDetails(jobNo,rawItemCode,rawItemName,unit, "+
							" pcsPerKg,QtyInKg,rate,amount,remarks)values('"+txtJobNo.getValue()+"'," +
							"'"+tbCmbInk.get(a).getValue()+"','"+tbCmbInk.get(a).getItemCaption(tbCmbInk.get(a).getValue())+"'," +
							"'"+tbTxtUnit.get(a).getValue()+"','"+tbTxtPcsPerKg.get(a).getValue()+"'," +
							"'"+tbTxtQtyKg.get(a).getValue()+"','"+tbTxtRate.get(a).getValue()+"'," +
							"'"+tbTxtAmount.get(a).getValue()+"','"+tbTxtRemarks.get(a).getValue()+"')";
					session.createSQLQuery(sql1).executeUpdate();

					String sql1Ud="insert into tbUdInkFormulationDetails(jobNo,rawItemCode,rawItemName,unit, "+
							" pcsPerKg,QtyInKg,rate,amount,remarks,type)values('"+txtJobNo.getValue()+"'," +
							"'"+tbCmbInk.get(a).getValue()+"','"+tbCmbInk.get(a).getItemCaption(tbCmbInk.get(a).getValue())+"'," +
							"'"+tbTxtUnit.get(a).getValue()+"','"+tbTxtPcsPerKg.get(a).getValue()+"'," +
							"'"+tbTxtQtyKg.get(a).getValue()+"','"+tbTxtRate.get(a).getValue()+"'," +
							"'"+tbTxtAmount.get(a).getValue()+"','"+tbTxtRemarks.get(a).getValue()+"','"+type+"')";
					session.createSQLQuery(sql1Ud).executeUpdate();
				}
			}

			tx.commit();
			showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);

		}
		catch(Exception exp){
			tx.rollback();
			showNotification("InsertData: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void saveButtonEvent() {

		//if(cmbMouldName.getValue()!=null){
		if(cmbFgName.getValue()!=null){
			if(tbCmbInk.get(0).getValue()!=null&&!tbTxtAmount.get(0).getValue().toString().isEmpty()){

				if(qtyZeroCheck()){
					if(isUpdate){
						MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
						mb.show(new EventListener() 
						{
							public void buttonClicked(ButtonType buttonType) 
							{
								if (buttonType == ButtonType.YES) 
								{
									standardSl();
									Transaction tx = null;
									Session session = SessionFactoryUtil.getInstance().getCurrentSession();
									tx = session.beginTransaction();
									//if(deleteData(session,tx)){
									insertData(session,tx);
									//emailSend();
									txtClear();
									componentIni(true);
									//}
									isFind = false;
									isUpdate = false;
								}
							}
						});
					}
					else{
						MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
						mb.show(new EventListener() 
						{
							public void buttonClicked(ButtonType buttonType) 
							{
								if (buttonType == ButtonType.YES) 
								{
									standardSl();
									Transaction tx = null;
									Session session = SessionFactoryUtil.getInstance().getCurrentSession();
									tx = session.beginTransaction();

									insertData(session,tx);
									txtClear();
									componentIni(true);
									btnIni(true);
									isFind = false;
									isUpdate = false;
								}
							}
						});
					}
				}
				else{
					showNotification("Quantity Can't be Zero.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide all fields.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Select Finished Goods Please.",Notification.TYPE_WARNING_MESSAGE);
		}

	}
	private void btnAction() {
		cButton.btnNew.addListener(new Listener() {
			public void componentEvent(Event event) {
				newButtonEvent();
			}
		});

		cButton.btnRefresh.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				refreshEvent();
			}
		});
		cButton.btnEdit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(sessionBean.isUpdateable())
				{
					updateButtonEvent();
					isFind = false;
					cmbFgName.focus();
				}
				else
				{
					showNotification("Warning,","You are not permitted to edit data.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnSave.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				saveButtonEvent();
				cButton.btnNew.focus();

			}
		});
		cButton.btnFind.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				panelSearch.setEnabled(true);
				fgFindDataLoad();
			}

		});

		cButton.btnExit.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				close();
			}

		});
		cmbFindFG.addListener(new ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbFindFG.getValue()!=null){
					cmbDeclareDateFindLoad();
				}
				else{
					cmbFindDate.removeAllItems();
				}
			}
		});
		cmbFindDate.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				
				txtClear();
				if(cmbFindFG.getValue()!=null){
					StringTokenizer token=new StringTokenizer(cmbFindDate.getItemCaption(cmbFindDate.getValue()),"S");
					findDataLoad(token.nextToken());
				}
			}
		});
	}

	private void findDataLoad(String date) {
		Iterator iter=dbService("select a.semiFgSubId,a.declareDate,a.jobNo,a.status,b.rawItemCode,b.unit, "+
				" b.pcsPerKg,b.QtyInKg,b.rate,b.amount,b.remarks,a.LacqureFlag from tbInkFormulationInfo a "+
				" inner join tbInkFormulationDetails b on a.jobNo=b.jobNo where a.semiFgSubId='"+cmbFindFG.getValue()+"' "+
				" and slFlag='"+cmbFindDate.getValue()+"' and convert(varchar(10),a.declareDate,103)='"+date+"'");
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			if(a==0){
				cmbFgName.setValue(element[0]);
				dDeclaration.setValue(element[1]);
				txtJobNo.setValue(element[2]);
				optGroup.setValue(element[3]);
				optLacqure.setValue(element[11]);
				
			}
			tbCmbInk.get(a).setValue(element[4]);
			tbTxtUnit.get(a).setValue(element[5]);
			tbTxtPcsPerKg.get(a).setValue(df1.format(element[6]));
			tbTxtQtyKg.get(a).setValue(df.format(element[7]));
			tbTxtRate.get(a).setValue(df2.format(element[8]));
			tbTxtAmount.get(a).setValue(df.format(element[9]));
			tbTxtRemarks.get(a).setValue(element[10]);
			a++;
		}
	}
	private void cmbDeclareDateFindLoad() {
		cmbFindDate.removeAllItems();
		Iterator iter=dbService("select slFlag,convert(varchar(10),declareDate,103)declareDate from " +
				"tbInkFormulationInfo where semiFgSubId='"+cmbFindFG.getValue()+"'");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbFindDate.addItem(element[0]);
			cmbFindDate.setItemCaption(element[0], element[1].toString()+" SL # "+element[0] );
		}
	}
	private void fgFindDataLoad() {
		cmbFindFG.removeAllItems();
		Iterator iter=dbService("select distinct semiFgSubId,semiFgSubName from tbInkFormulationInfo order by semiFgSubName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbFindFG.addItem(element[0]);
			cmbFindFG.setItemCaption(element[0], element[1].toString());
		}
	}
	private void componentIni(boolean b) 
	{
		optLacqure.setEnabled(!b);
		optGroup.setEnabled(!b);
		cmbFgName.setEnabled(!b);
		//		txtUnit.setEnabled(!b);
		dDeclaration.setEnabled(!b);
		txtJobNo.setEnabled(!b);
		tableInk.setEnabled(!b);
		//panelSearch.setEnabled(!b);
		//cmbFindDate.setEnabled(!b);
		panelSearch.setEnabled(!b);


	}
	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private void tableInitInk(){
		for(int a=0;a<7;a++){
			tableRowAddInk(a);
		}
	}
	private void tbInkDataLoad(int ar){
		tbCmbInk.get(ar).removeAllItems();
		Iterator iter=dbService("select vRawItemCode,vRawItemName from tbRawItemInfo where vCategoryType like 'Ink' "+
					" union select vRawItemCode,vRawItemName from tbRawItemInfo where vGroupId='7' union select vCode,vItemName from tbThirdPartyItemInfo  where vCategoryType='INK'  order by vRawItemName");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbInk.get(ar).addItem(element[0]);
			tbCmbInk.get(ar).setItemCaption(element[0], element[1].toString());
		}
	}
	private boolean doubleEntryCheck(String id,int ar,String head){

		if(head.equalsIgnoreCase("INK")){
			for(int a=0;a<tbCmbInk.size();a++)
			{
				if(tbCmbInk.get(a).getValue()!=null){
					if(a!=ar&&id.equalsIgnoreCase(tbCmbInk.get(a).getValue().toString())){

						return true;
					}
				}
			}
		}
		return false;
	}
	private void calcAmount(int ar){
		double qty,rate,amount,totalAmount=0;
		qty=Double.parseDouble("0"+tbTxtQtyKg.get(ar).getValue());
		rate=Double.parseDouble("0"+tbTxtRate.get(ar).getValue());
		amount=qty*rate;
		tbTxtAmount.get(ar).setValue(df.format(amount));

		for(int x=0;x<tbLblSl.size();x++){
			totalAmount=totalAmount+Double.parseDouble("0"+tbTxtAmount.get(x).getValue());
		}
		tableInk.setColumnFooter("Amount", "Total:"+df.format(totalAmount));
		lblInk.setValue(df.format(totalAmount));
	}
	private void tableRowAddInk(final int ar){

		tbLblSl.add(ar, new Label());
		tbLblSl.get(ar).setValue(ar+1);
		tbLblSl.get(ar).setWidth("100%");

		tbCmbInk.add(ar, new ComboBox());
		tbCmbInk.get(ar).setNullSelectionAllowed(true);
		tbCmbInk.get(ar).setImmediate(true);
		tbCmbInk.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		tbCmbInk.get(ar).setWidth("100%");
		tbInkDataLoad(ar);
		tbCmbInk.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tbCmbInk.get(ar).getValue()!=null){
					if(!doubleEntryCheck(tbCmbInk.get(ar).getValue().toString(),ar,"INK"))
					{
						Iterator iter=dbService("select unit,closingRate from dbo.[funRawMaterialsStock]" +
								"('"+dateformat.format(dDeclaration.getValue())+" 23:59:59','"+tbCmbInk.get(ar).getValue()+"') union select vUnitName,0 rate from tbThirdPartyItemInfo  where vCode='"+tbCmbInk.get(ar).getValue()+"'  ");
						if(iter.hasNext()){
							Object element[]=(Object[])iter.next();
							tbTxtUnit.get(ar).setValue(element[0]);
							tbTxtRate.get(ar).setValue(df2.format(element[1]));
						}
						tbTxtPcsPerKg.get(ar).focus();
					}
					else{

						showNotification("Double Entry");
						tbCmbInk.get(ar).setValue(null);
						tbCmbInk.get(ar).focus();
					}
				}
				else{
					tbTxtUnit.get(ar).setValue("");
					tbTxtPcsPerKg.get(ar).setValue("");
					tbTxtQtyKg.get(ar).setValue("");
					tbTxtRate.get(ar).setValue("");
					tbTxtAmount.get(ar).setValue("");
				}
			}


		});

		tbTxtUnit.add(ar, new TextRead(1));
		tbTxtUnit.get(ar).setImmediate(true);
		tbTxtUnit.get(ar).setWidth("100%");

		tbTxtPcsPerKg.add(ar,new AmountField());
		tbTxtPcsPerKg.get(ar).setImmediate(true);
		tbTxtPcsPerKg.get(ar).setWidth("100%");

		tbTxtPcsPerKg.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(tbCmbInk.get(ar).getValue()!=null&&Double.parseDouble("0"+tbTxtPcsPerKg.get(ar).getValue())>0){
					double qtyInPcs=1/Double.parseDouble("0"+tbTxtPcsPerKg.get(ar).getValue());
					tbTxtQtyKg.get(ar).setValue(df.format(qtyInPcs));
				}
				else{
					tbTxtQtyKg.get(ar).setValue("");
				}
			}
		});


		tbTxtQtyKg.add(ar,new AmountField());
		tbTxtQtyKg.get(ar).setImmediate(true);
		tbTxtQtyKg.get(ar).setWidth("100%");
		tbTxtQtyKg.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				calcAmount(ar);
			}
		});


		tbTxtRate.add(ar,  new AmountField());
		tbTxtRate.get(ar).setImmediate(true);
		tbTxtRate.get(ar).setWidth("100%");
		tbTxtRate.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				calcAmount(ar);
			}
		});

		tbTxtAmount.add(ar, new AmountField());
		tbTxtAmount.get(ar).setImmediate(true);
		tbTxtAmount.get(ar).setWidth("100%");

		tbTxtRemarks.add(ar,  new TextField());
		tbTxtRemarks.get(ar).setImmediate(true);
		tbTxtRemarks.get(ar).setWidth("100%");

		tableInk.addItem(new Object[]{tbLblSl.get(ar),tbCmbInk.get(ar),tbTxtUnit.get(ar),
				tbTxtPcsPerKg.get(ar) ,tbTxtQtyKg.get(ar),tbTxtRate.get(ar),tbTxtAmount.get(ar),tbTxtRemarks.get(ar)},ar);
	}

	private AbsoluteLayout buildMainLayout() {

		// mainLayout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		setWidth("910px");
		setHeight("570px");

		cmbFgName=new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setWidth("300px");
		cmbFgName.setHeight("24px");
		cmbFgName.setNullSelectionAllowed(true);
		cmbFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("FG Name: "),"top:10px;left:20px;");
		mainLayout.addComponent(cmbFgName,"top:08px;left:120px;");

		/*txtUnit = new TextRead();
		txtUnit.setImmediate(true);
		txtUnit.setWidth("120px");
		mainLayout.addComponent(new Label("Unit: "),"top:40px;left:20px;");
		mainLayout.addComponent(txtUnit,"top:38px;left:120px;");*/

		dDeclaration=new PopupDateField();
		dDeclaration.setResolution(PopupDateField.RESOLUTION_DAY);
		dDeclaration.setValue(new java.util.Date());
		dDeclaration.setWidth("110px");
		dDeclaration.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(new Label("Declare Date: "),"top:40px;left:20px;");
		mainLayout.addComponent(dDeclaration,"top:38px;left:120px;");
		
		optLacqure=new OptionGroup("", optStatusLacqure);
		optLacqure.setImmediate(true);
		optLacqure.setStyleName("horizontal");
		optLacqure.setValue("NO");
		mainLayout.addComponent(new Label("Lacqure: "), "top:40px;left:240.0px;");
		mainLayout.addComponent(optLacqure, "top:38.0px;left:300.0px;");

		optGroup=new OptionGroup("", optStatus);
		optGroup.setImmediate(true);
		optGroup.setStyleName("horizontal");
		optGroup.setValue("Active");
		mainLayout.addComponent(new Label("Status: "), "top:10px;left:460.0px;");
		mainLayout.addComponent(optGroup, "top:10.0px;left:510.0px;");

		txtJobNo = new TextRead();
		txtJobNo.setImmediate(true);
		txtJobNo.setWidth("120px");
		mainLayout.addComponent(new Label("Job No: "),"top:40px;left:460.0px;");
		mainLayout.addComponent(txtJobNo,"top:38px;left:510.0px;");

		//Table
		tableInk=new Table();
		tableInk.setWidth("890px");
		tableInk.setHeight("225px");
		tableInk.setColumnCollapsingAllowed(true);
		tableInk.setFooterVisible(true);

		tableInk.addContainerProperty("SL", Label.class,new Label());
		tableInk.setColumnWidth("SL", 10);

		tableInk.addContainerProperty("Ink Name", ComboBox.class,new ComboBox());
		tableInk.setColumnWidth("Ink Name", 290);

		tableInk.addContainerProperty("Unit", TextRead.class,new TextRead(1));
		tableInk.setColumnWidth("Unit", 40);

		tableInk.addContainerProperty("pcs/kg", AmountField.class,new AmountField());
		tableInk.setColumnWidth("pcs/kg", 75);

		tableInk.addContainerProperty("Qty(kg)", AmountField.class,new AmountField());
		tableInk.setColumnWidth("Qty(kg)", 75);

		tableInk.addContainerProperty("Rate", AmountField.class,new AmountField());
		tableInk.setColumnWidth("Rate", 75);

		tableInk.addContainerProperty("Amount", AmountField.class,new AmountField());
		tableInk.setColumnWidth("Amount", 90);

		tableInk.addContainerProperty("Remarks", TextField.class,new TextField());
		tableInk.setColumnWidth("Remarks", 100);

		mainLayout.addComponent(tableInk,"top:70px;left:10px;");
		tableInk.setColumnFooter("Amount", "Total:"+0.0);


		Label lblPanel= new Label(" <font color='##0000FF' size='4px'><b><Strong>Search :<Strong></b></font>");
		lblPanel.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblPanel, "top:300px;left:10px;");

		panelSearch=new Panel();
		panelSearch.setWidth("450px");
		panelSearch.setHeight("140px");
		mainLayout.addComponent(panelSearch,"top:330px;left:10px;");
		panelSearch.setStyleName("panelSearch");
		FormLayout frmLayout=new FormLayout();
		frmLayout.setSpacing(true);
		frmLayout.setMargin(true);


		cmbFindFG.setImmediate(true);
		cmbFindFG.setWidth("250px");
		cmbFindFG.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		cmbFindDate.setImmediate(true);
		cmbFindDate.setWidth("250px");
		cmbFindDate.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		frmLayout.addComponent(cmbFindFG);
		frmLayout.addComponent(cmbFindDate);
		cmbFindDate.setImmediate(true);
		cmbFindDate.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		panelSearch.addComponent(frmLayout);


		mainLayout.addComponent(cButton,"top:490px;left:100px;");

		return mainLayout;
	}
}
