package com.example.sparePartsTransaction;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import com.vaadin.ui.*;
import com.common.share.*;

public class RequistionEntry extends Window {

	private SessionBean sessionBean;
	private Label lbLine = new Label("______________________________________________________________________________________________________________________________________________________________________________________");
	private CommonButtonNew cButton = new CommonButtonNew("New", "Save", "Edit","", "Refresh", "Find", "", "Exit", "", "Preview");

	boolean isUpdate = false,isFind=false;
	String filePathTmpReq= "";
	String imageLoc= "0";
	private TextField txtReqId = new TextField();
	private DecimalFormat df = new DecimalFormat("#0.00");
	SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Component> allComp = new ArrayList<Component>();

	private AbsoluteLayout mainLayout;
	
	public RequistionEntry(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("REQUISITION ENTRY :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
	}
	private AbsoluteLayout buildMainLayout(){
		
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("850px");
		setHeight("500px");
		
		return mainLayout;
	}
}
