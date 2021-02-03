package acc.appform.hrmModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.Session;

import com.common.share.FileUpload;
import com.common.share.ImmediateUploadExampleApplication;
import com.common.share.ImmediateUploadExampleBirth;
import com.common.share.ImmediateUploadExampleConfirmation;
import com.common.share.ImmediateUploadExampleJoining;
import com.common.share.ImmediateUploadExampleNid;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.CheckBox;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class TabOfficialInfo extends VerticalLayout
{
	public AbsoluteLayout mainLayout;
	public NativeButton btnDesignation;
	public NativeButton btnDepartment;
	public NativeButton btnSection;
	public NativeButton btnSubUnit;

	public Button btnGrade;
	public ComboBox cmbGrade;
	public Label lblGrade;
	public Button btnLine;
	public ComboBox cmbLine;
	public Label lblLine;
	public Button btnFloor;
	public ComboBox cmbFloor;
	public Label lblFloor;

	public ComboBox cmbShiftId;
	public Label lblShiftId;

	public ComboBox cmbDesignation;
	public Label lblDesignation;
	public Label lblDepartment;
	public ComboBox cmbDepartment;
	public Label lblSection;
	public ComboBox cmbSection;
	
	public Label lblGrades;
	public ComboBox cmbGrades;
	public NativeButton btnGrades;

	public ComboBox cmbStatus;
	public Label lblStatus;
	public ComboBox cmbStatu;
	public Label lblStatu;
	public Label lblTINNo;
	public TextField txtTINNo;
	public ImmediateUploadExampleBirth btnTINNO;
	public String tINNoPdf = null;
	public String tINNoFilePathTmp = "";
	public Button btnTINNOPreview;
	public Label lblCircle;
	public TextField txtCircle;
	public ImmediateUploadExampleBirth btnCircleNO;
	public Button btnCirclePreview;
	public String CirclePdf = null;
	public String CircleFilePathTmp = "";
	public Label lblZone;
	public TextField txtZone;
	public ImmediateUploadExampleBirth btnZoneNO;
	public Button btnZonePreview;
	public String ZonePdf = null;
	public String ZoneFilePathTmp = "";
	public PopupDateField dDate;
	public Label lblDateName;

	public PopupDateField dInterviewDate;
	public Label lblInterviewdate;

	public Label lblEmpType;
	public ComboBox cmbEmpType;
	public Label lblSerType;
	public ComboBox cmbSerType;
	public TextField txtNationality;
	public Label lblNationality;

	public ComboBox cmbGender;
	public Label lblGender;
	public TextField txtEmail;
	public Label lblEmail;
	public TextField txtContact;
	public Label lblContact;
	public ComboBox cmbReligion;
	public Label lblReligion;
	public TextField txtEmpName;
	public Label lblEmpName;
	public Label lblEmpNameBan;
	public TextField txtProximityId;
	public Label lblProximityId;
	public TextField txtFingerId;
	public Label lblFingerId;
	public TextField txtEmployeeCode;
	public Label lblEmpId;
	public FileUpload Image;

	public CheckBox chkOtEnable;
	public CheckBox chkFriEnable;
	public CheckBox chkPhysicallyDesable;


	///    Start of Date of Birth
	public ImmediateUploadExampleBirth btnDateofBirth;
	public PopupDateField dDateofBirth;
	public Label lblDateofBirth;
	public Button btnBirthPreview;
	public String birthPdf = null;
	public String birthFilePathTmp = "";
	///    End of Date of Birth

	///    Start of NID
	public ImmediateUploadExampleNid btnNid;
	public TextField txtNid;
	public Label lblNid;
	public Button btnNidPreview;
	public String nidPdf = null;
	public String nidFilePathTmp = "";
	///    End of NID

	///    Start of Application Date
	public ImmediateUploadExampleApplication btnAppDate;
	public PopupDateField dAppDate;
	public Label lblAppDate;
	public Button btnApplicationPreview;
	public String applicationPdf = null;
	public String applicationFilePathTmp = "";
	///    End of Application Date

	///    Start of Joining Letter
	public ImmediateUploadExampleJoining btnJoiningDate;
	public PopupDateField dJoiningDate;
	public Label lblJoiningDate;
	public Button btnJoinPreview;
	public String joinPdf = null;
	public String joinFilePathTmp = "";
	///    End of Joining Letter

	///    Start of Confirmation Letter
	public ImmediateUploadExampleConfirmation btnConfirmdate;
	public PopupDateField dConDate;
	public Label lblConfirmDate;
	public Button btnConPreview;
	public String conPdf = null;
	public String conFilePathTmp = "";
	///    End of Confirmation Letter

	public TextField txtEmployeeId = new  TextField();
	ArrayList<Component> allComp = new ArrayList<Component>();

	public Label lblStar;

	SessionBean sessionBean;

	public PopupDateField dateFinalSett = new PopupDateField("Final Settelment Date:");

	private static final String[] religion = new String[] {"Islam","Hindu","Buddism","Cristian","Other"};
	private static final String[] status = new String[] { "Continue", "Discontinue", "Retired", "Dismiss", "Terminated"};

	public TabOfficialInfo(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		buildMainLayout();
		addComponent(mainLayout);
		imageFileListener();
	}

	private void imageFileListener()
	{
		btnDateofBirth.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnBirthPreview.setCaption("Preview");
				birthPath(0,"");
			}
		});
		
		btnTINNO.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnTINNOPreview.setCaption("Preview");
				tinPath(0,"");
			}
		});

		btnCircleNO.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnCirclePreview.setCaption("Preview");
				circlePath(0,"");
			}
		});

		btnZoneNO.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnZonePreview.setCaption("Preview");
				zonePath(0,"");
			}
		});

		btnNid.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnNidPreview.setCaption("Preview");
				nidPath(0,"");
			}
		});

		btnAppDate.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnApplicationPreview.setCaption("Preview");
				applicationPath(0,"");
			}
		});

		btnJoiningDate.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnJoinPreview.setCaption("Preview");
				joinPath(0,"");
			}
		});

		btnConfirmdate.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				btnConPreview.setCaption("Preview");
				conPath(0,"");
			}
		});
	}

	public String conPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String conImage = null;

		if(flag==0)
		{
			if(btnConfirmdate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnConfirmdate.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"confirm";
						fileMove(basePath+btnConfirmdate.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						conPdf = SessionBean.imagePathTmp+path+".jpg";
						conFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"confirm";
						fileMove(basePath+btnConfirmdate.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						conPdf = SessionBean.imagePathTmp+path+".pdf";
						conFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return conPdf;
		}

		if(flag==1)
		{
			if(btnConfirmdate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnConfirmdate.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnConfirmdate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/confirm/"+path+".jpg");
						conImage = SessionBean.imagePath+projectName+"/employee/confirm/"+path+".jpg";
					}
					else
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnConfirmdate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/confirm/"+path+".pdf");
						conImage = SessionBean.imagePath+projectName+"/employee/confirm/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return conImage;
		}
		return null;
	}

	public String joinPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String joinImage = null;

		if(flag==0)
		{
			if(btnJoiningDate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnJoiningDate.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"join";
						fileMove(basePath+btnJoiningDate.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						joinPdf = SessionBean.imagePathTmp+path+".jpg";
						joinFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"join";
						fileMove(basePath+btnJoiningDate.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						joinPdf = SessionBean.imagePathTmp+path+".pdf";
						joinFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return joinPdf;
		}

		if(flag==1)
		{
			if(btnJoiningDate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnJoiningDate.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnJoiningDate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/join/"+path+".jpg");
						joinImage = SessionBean.imagePath+projectName+"/employee/join/"+path+".jpg";
					}
					else
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnJoiningDate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/join/"+path+".pdf");
						joinImage = SessionBean.imagePath+projectName+"/employee/join/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return joinImage;
		}

		return null;
	}

	public String applicationPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String applicationImage = null;

		if(flag==0)
		{
			if(btnAppDate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnAppDate.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"application";
						fileMove(basePath+btnAppDate.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						applicationPdf = SessionBean.imagePathTmp+path+".jpg";
						applicationFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"application";
						fileMove(basePath+btnAppDate.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						applicationPdf = SessionBean.imagePathTmp+path+".pdf";
						applicationFilePathTmp = path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return applicationPdf;
		}

		if(flag==1)
		{
			if(btnAppDate.fileName.trim().length()>0)
			{
				try 
				{
					if(btnAppDate.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnAppDate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/application/"+path+".jpg");
						applicationImage = SessionBean.imagePath+projectName+"/employee/application/"+path+".jpg";
					}
					else
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnAppDate.fileName.trim(),SessionBean.imagePath+projectName+"/employee/application/"+path+".pdf");
						applicationImage = SessionBean.imagePath+projectName+"/employee/application/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return applicationImage;
		}
		return null;
	}

	public String nidPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String nidImage = null;

		if(flag==0)
		{
			if(btnNid.fileName.trim().length()>0)
			{
				try 
				{
					if(btnNid.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"nid";
						fileMove(basePath+btnNid.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						nidPdf = SessionBean.imagePathTmp+path+".jpg";
						nidFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"nid";
						fileMove(basePath+btnNid.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						nidPdf = SessionBean.imagePathTmp+path+".pdf";
						nidFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return nidPdf;
		}

		if(flag==1)
		{
			if(btnNid.fileName.trim().length()>0)
			{
				try 
				{
					if(btnNid.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnNid.fileName.trim(),SessionBean.imagePath+projectName+"/employee/nid/"+path+".jpg");
						nidImage = SessionBean.imagePath+projectName+"/employee/nid/"+path+".jpg";
					}
					else
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnNid.fileName.trim(),SessionBean.imagePath+projectName+"/employee/nid/"+path+".pdf");
						nidImage = SessionBean.imagePath+projectName+"/employee/nid/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return nidImage;
		}
		return null;
	}

	public String birthPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			if(btnDateofBirth.fileName.trim().length()>0)
			{
				try 
				{
					if(btnDateofBirth.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"birth";
						fileMove(basePath+btnDateofBirth.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						birthPdf = SessionBean.imagePathTmp+path+".jpg";
						birthFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"birth";
						fileMove(basePath+btnDateofBirth.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						birthPdf = SessionBean.imagePathTmp+path+".pdf";
						birthFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return birthPdf;
		}

		if(flag==1)
		{
			if(btnDateofBirth.fileName.trim().length()>0)
			{
				try 
				{
					if(btnDateofBirth.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnDateofBirth.fileName.trim(),SessionBean.imagePath+projectName+"/employee/birth/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/employee/birth/"+path+".jpg";
					}
					else
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnDateofBirth.fileName.trim(),SessionBean.imagePath+projectName+"/employee/birth/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/employee/birth/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}
		return null;
	}

	public String tinPath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			if(btnTINNO.fileName.trim().length()>0)
			{
				try 
				{
					if(btnTINNO.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"TINNO";
						fileMove(basePath+btnTINNO.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						tINNoPdf = SessionBean.imagePathTmp+path+".jpg";
						tINNoFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"TINNO";
						fileMove(basePath+btnTINNO.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						tINNoPdf = SessionBean.imagePathTmp+path+".pdf";
						tINNoFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return birthPdf;
		}

		if(flag==1)
		{
			if(btnTINNO.fileName.trim().length()>0)
			{
				try 
				{
					if(btnTINNO.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnTINNO.fileName.trim(),SessionBean.imagePath+projectName+"/employee/tin/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/employee/tin/"+path+".jpg";
					}
					else
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnTINNO.fileName.trim(),SessionBean.imagePath+projectName+"/employee/tin/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/employee/tin/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}
		return null;
	}

	public String circlePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			if(btnCircleNO.fileName.trim().length()>0)
			{
				try 
				{
					if(btnCircleNO.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"Circle";
						fileMove(basePath+btnCircleNO.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						CirclePdf = SessionBean.imagePathTmp+path+".jpg";
						CircleFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"Circle";
						fileMove(basePath+btnCircleNO.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						CirclePdf = SessionBean.imagePathTmp+path+".pdf";
						CircleFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return birthPdf;
		}

		if(flag==1)
		{
			if(btnCircleNO.fileName.trim().length()>0)
			{
				try 
				{
					if(btnCircleNO.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnCircleNO.fileName.trim(),SessionBean.imagePath+projectName+"/employee/circle/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/employee/circle/"+path+".jpg";
					}
					else
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnCircleNO.fileName.trim(),SessionBean.imagePath+projectName+"/employee/circle/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/employee/circle/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}
		return null;
	}

	public String zonePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			if(btnZoneNO.fileName.trim().length()>0)
			{
				try 
				{
					if(btnZoneNO.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"Zone";
						fileMove(basePath+btnZoneNO.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						ZonePdf = SessionBean.imagePathTmp+path+".jpg";
						ZoneFilePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"Zone";
						fileMove(basePath+btnZoneNO.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						ZonePdf = SessionBean.imagePathTmp+path+".pdf";
						ZoneFilePathTmp = path+".pdf";
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return birthPdf;
		}

		if(flag==1)
		{
			if(btnZoneNO.fileName.trim().length()>0)
			{
				try 
				{
					if(btnZoneNO.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnZoneNO.fileName.trim(),SessionBean.imagePath+projectName+"/employee/zone/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/employee/zone/"+path+".jpg";
					}
					else
					{
						String path = str;
						@SuppressWarnings("static-access")
						String projectName = sessionBean.projectName;
						fileMove(basePath+btnZoneNO.fileName.trim(),SessionBean.imagePath+projectName+"/employee/zone/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/employee/zone/"+path+".pdf";
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			return stuImage;
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
		catch(Exception exp){}
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

	public AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100.0%");
		mainLayout.setHeight("100.0%");
		mainLayout.setMargin(false);

		setWidth("845px");
		setHeight("395px");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:08.0px;left:29.0px;");

		// lblEmpId
		lblEmpId = new Label("Employee Code:");
		lblEmpId.setImmediate(true);
		lblEmpId.setWidth("-1px");
		lblEmpId.setHeight("-1px");
		mainLayout.addComponent(lblEmpId, "top:08.0px;left:35.0px;");

		// txtEmpId
		txtEmployeeCode = new TextField();
		txtEmployeeCode.setImmediate(true);
		txtEmployeeCode.setWidth("138px");
		txtEmployeeCode.setHeight("-1px");
		mainLayout.addComponent(txtEmployeeCode, "top:08.0px;left:128.0px;");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:34.0px;left:65.0px;");

		lblFingerId = new Label("Finger ID:");
		lblFingerId.setImmediate(true);
		lblFingerId.setWidth("-1px");
		lblFingerId.setHeight("-1px");
		mainLayout.addComponent(lblFingerId, "top:34.0px;left:73.0px;");

		// txtFingerId
		txtFingerId = new TextField();
		txtFingerId.setImmediate(true);
		txtFingerId.setWidth("85px");
		txtFingerId.setHeight("-1px");
		txtFingerId.setMaxLength(5);
		mainLayout.addComponent(txtFingerId, "top:32.0px;left:128.0px;");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:62.0px;left:52.0px;");

		// lblProximityId
		lblProximityId = new Label("Proximity ID:");
		lblProximityId.setImmediate(true);
		lblProximityId.setWidth("-1px");
		lblProximityId.setHeight("-1px");
		mainLayout.addComponent(lblProximityId, "top:62.0px;left:59.0px;");

		// txtProximityId
		txtProximityId = new TextField();
		txtProximityId.setImmediate(true);
		txtProximityId.setWidth("138px");
		txtProximityId.setHeight("-1px");
		txtProximityId.setMaxLength(10);
		//txtProximityId.setRequired(true);
		mainLayout.addComponent(txtProximityId, "top:58.0px;left:128.0px;");

		//lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:86.0px;left:23.0px;");

		// lblEmpName
		lblEmpName = new Label("Employee Name:");
		lblEmpName.setImmediate(true);
		lblEmpName.setWidth("-1px");
		lblEmpName.setHeight("-1px");
		mainLayout.addComponent(lblEmpName, "top:86.0px;left:30.0px;");

		// txtEmpName
		txtEmpName = new TextField();
		txtEmpName.setImmediate(true);
		txtEmpName.setWidth("202px");
		txtEmpName.setHeight("-1px");
		//txtEmpName.setRequired(true);
		mainLayout.addComponent(txtEmpName, "top:84.0px;left:128.0px;");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:111.0px;left:68.0px;");

		// lblReligion
		lblReligion = new Label();
		lblReligion.setImmediate(true);
		lblReligion.setWidth("-1px");
		lblReligion.setHeight("-1px");
		lblReligion.setValue("Religion:");
		mainLayout.addComponent(lblReligion, "top:111.0px;left:75.0px;");

		// cmbReligion
		cmbReligion = new ComboBox();
		cmbReligion.setImmediate(true);
		cmbReligion.setNullSelectionAllowed(true);
		cmbReligion.setWidth("133px");
		cmbReligion.setHeight("-1px");
		addReligion();
		mainLayout.addComponent(cmbReligion, "top:109.0px;left:128.0px;");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:139.0px;left:71.0px;"); 

		// lblContact
		lblContact = new Label();
		lblContact.setImmediate(true);
		lblContact.setWidth("-1px");
		lblContact.setHeight("-1px");
		lblContact.setValue("Contact:");
		mainLayout.addComponent(lblContact, "top:139.0px;left:79.0px;");

		// txtContact
		txtContact = new TextField();
		txtContact.setImmediate(true);
		txtContact.setWidth("133px");
		txtContact.setHeight("-1px");
		mainLayout.addComponent(txtContact, "top:137.0px;left:128.0px;");

		// lblEmail
		lblEmail = new Label();
		lblEmail.setImmediate(true);
		lblEmail.setWidth("-1px");
		lblEmail.setHeight("-1px");
		lblEmail.setValue("E-Mail:");
		mainLayout.addComponent(lblEmail, "top:167.0px;left:87.0px;");

		// txtEmail
		txtEmail = new TextField();
		txtEmail.setImmediate(true);
		txtEmail.setWidth("133px");
		txtEmail.setHeight("-1px");
		mainLayout.addComponent(txtEmail, "top:163.0px;left:128.0px;");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:195.0px;left:74.0px;");

		// lblGender
		lblGender = new Label();
		lblGender.setImmediate(true);
		lblGender.setWidth("-1px");
		lblGender.setHeight("-1px");
		lblGender.setValue("Gender:");
		mainLayout.addComponent(lblGender, "top:195.0px;left:81.0px;");

		// cmbGender
		cmbGender = new ComboBox();
		cmbGender.setImmediate(false);
		cmbGender.setNullSelectionAllowed(true);
		cmbGender.setWidth("133px");
		cmbGender.setHeight("-1px");
		cmbGender.addItem("Male");
		cmbGender.addItem("Female");
		/*cmbGender.setValue("Male");
		cmbGender.setEnabled(false);*/
		mainLayout.addComponent(cmbGender, "top:189.0px;left:128.0px;");

		// lblNationality
		lblNationality = new Label();
		lblNationality.setImmediate(true);
		lblNationality.setWidth("66px");
		lblNationality.setHeight("-1px");
		lblNationality.setValue("Nationality:");
		mainLayout.addComponent(lblNationality, "top:217.0px;left:65.0px;");

		// txtNationality
		txtNationality = new TextField();
		txtNationality.setImmediate(true);
		txtNationality.setWidth("106px");
		txtNationality.setHeight("-1px");
		mainLayout.addComponent(txtNationality, "top:215.0px;left:128.0px;");

		// lblNid
		lblNid = new Label();
		lblNid.setImmediate(true);
		lblNid.setWidth("28px");
		lblNid.setHeight("18px");
		lblNid.setValue("NID:");
		mainLayout.addComponent(lblNid, "top:242.0px;left:101.0px;");

		// txtNid
		txtNid = new TextField();
		txtNid.setImmediate(true);
		txtNid.setWidth("138px");
		txtNid.setHeight("-1px");
		txtNid.setMaxLength(17);
		mainLayout.addComponent(txtNid, "top:240.0px;left:128.0px;");

		// btnNidPreview
		btnNidPreview = new Button();
		btnNidPreview.setCaption("jpg/pdf file");
		btnNidPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnNidPreview.addStyleName("icon-after-caption");
		btnNidPreview.setImmediate(true);
		btnNidPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnNidPreview, "top:240.0px;left:320.0px;");

		// btnNid
		btnNid = new ImmediateUploadExampleNid("");
		//btnNid.setCaption("Attach NID");
		btnNid.setImmediate(true);
		btnNid.setWidth("-1px");
		btnNid.setHeight("-1px");
		btnNid.setStyleName("uploadReq");
		mainLayout.addComponent(btnNid, "top:235.0px;left:265.0px;");


		// lblDateofBirth
		lblDateofBirth = new Label();
		lblDateofBirth.setImmediate(true);
		lblDateofBirth.setWidth("74px");
		lblDateofBirth.setHeight("-1px");
		lblDateofBirth.setValue("Date of Birth:");
		mainLayout.addComponent(lblDateofBirth, "top:267.0px;left:55.0px;");

		// dDateofBirth
		dDateofBirth = new PopupDateField();
		dDateofBirth.setImmediate(true);
		dDateofBirth.setHeight("-1px");
		dDateofBirth.setResolution(PopupDateField.RESOLUTION_DAY);
		dDateofBirth.setDateFormat("dd-MM-yyyy");
		dDateofBirth.setValue(new java.util.Date());
		mainLayout.addComponent(dDateofBirth, "top:265.0px;left:128.0px;");

		// btnDateofBirth
		btnDateofBirth = new ImmediateUploadExampleBirth("");
		//btnDateofBirth.setCaption("Birth Cirtificate");
		btnDateofBirth.setImmediate(true);
		btnDateofBirth.setWidth("-1px");
		btnDateofBirth.setHeight("-1px");
		btnDateofBirth.addStyleName("icon-after-caption");
		mainLayout.addComponent(btnDateofBirth, "top:260.0px;left:237.0px;");

		// btnBirthPreview
		btnBirthPreview = new Button();
		btnBirthPreview.setCaption("jpg/pdf file");
		btnBirthPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnBirthPreview.addStyleName("icon-after-caption");
		btnBirthPreview.setImmediate(true);
		btnBirthPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnBirthPreview, "top:267.0px;left:300.0px;");
		
		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:295.0px;left:31.0px;");

		// lblNid
		lblEmpType = new Label();
		lblEmpType.setImmediate(true);
		lblEmpType.setWidth("-1px");
		lblEmpType.setHeight("-1px");
		lblEmpType.setValue("Service Type:");
		mainLayout.addComponent(lblEmpType, "top:295.0px;left:39.0px;");

		// cmbEmpType
		cmbEmpType = new ComboBox();
		cmbEmpType.setImmediate(true);
		cmbEmpType.setNullSelectionAllowed(true);
		cmbEmpType.setWidth("133px");
		cmbEmpType.setHeight("-1px");
		cmbEmpType.addItem("Permanent");
		cmbEmpType.addItem("Temporary");
		cmbEmpType.addItem("Provisionary");
		cmbEmpType.addItem("Casual");
		//cmbEmpType.setValue("Male");
		mainLayout.addComponent(cmbEmpType, "top:290.0px;left:128.0px;");

		//lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:320px;left:31.0px;");

		// lblNid
		lblSerType = new Label();
		lblSerType.setImmediate(true);
		lblSerType.setWidth("-1px");
		lblSerType.setHeight("-1px");
		lblSerType.setValue("Service Status:");
		mainLayout.addComponent(lblSerType, "top:320.0px;left:39.0px;");

		// cmbEmpType
		cmbSerType = new ComboBox();
		cmbSerType.setImmediate(true);
		cmbSerType.setNullSelectionAllowed(true);
		cmbSerType.setWidth("133px");
		cmbSerType.setHeight("-1px");
		cmbSerType.addItem("Management");
		cmbSerType.addItem("Officer");
		cmbSerType.addItem("Staff");
		cmbSerType.addItem("Worker");
		//cmbEmpType.setValue("Male");
		mainLayout.addComponent(cmbSerType, "top:315.0px;left:128.0px;");

		// lblNid
		lblTINNo = new Label();
		lblTINNo.setImmediate(true);
		lblTINNo.setWidth("-1px");
		lblTINNo.setHeight("-1px");
		lblTINNo.setValue("TIN No.:");
		mainLayout.addComponent(lblTINNo, "top:345.0px;left:80.0px;");

		// cmbEmpType
		txtTINNo = new TextField();
		txtTINNo.setImmediate(true);
		txtTINNo.setWidth("110.0px");
		mainLayout.addComponent(txtTINNo, "top:343.0px;left:128.0px;");
		
		btnTINNO = new ImmediateUploadExampleBirth("");
		btnTINNO.setImmediate(true);
		btnTINNO.setWidth("90.0px");
		mainLayout.addComponent(btnTINNO, "top:338.0px; left:243.0px;");

		// btnApplicationPreview
		btnTINNOPreview = new Button();
		btnTINNOPreview.setCaption("jpg/pdf file");
		btnTINNOPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnTINNOPreview.addStyleName("icon-after-caption");
		btnTINNOPreview.setImmediate(true);
		btnTINNOPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnTINNOPreview, "top:345.0px;left:318.0px;");

		// lblNid
		lblCircle = new Label();
		lblCircle.setImmediate(true);
		lblCircle.setWidth("-1px");
		lblCircle.setHeight("-1px");
		lblCircle.setValue("Circle:");
		mainLayout.addComponent(lblCircle, "top:370.0px;left:85.0px;");

		// cmbEmpType
		txtCircle = new TextField();
		txtCircle.setImmediate(true);
		txtCircle.setWidth("110.0px");
		mainLayout.addComponent(txtCircle, "top:368.0px;left:128.0px;");
		
		btnCircleNO = new ImmediateUploadExampleBirth("");
		btnCircleNO.setImmediate(true);
		btnCircleNO.setWidth("90.0px");
		mainLayout.addComponent(btnCircleNO, "top:363.0px; left:243.0px;");

		// btnApplicationPreview
		btnCirclePreview = new Button();
		btnCirclePreview.setCaption("jpg/pdf file");
		btnCirclePreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnCirclePreview.addStyleName("icon-after-caption");
		btnCirclePreview.setImmediate(true);
		btnCirclePreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnCirclePreview, "top:370.0px;left:318.0px;");

		// lblAppDate
		lblAppDate = new Label();
		lblAppDate.setImmediate(true);
		lblAppDate.setWidth("-1px");
		lblAppDate.setHeight("-1px");
		lblAppDate.setValue("Application Date:");
		mainLayout.addComponent(lblAppDate, "top:10.0px;left:349.0px;");

		// dAppDate
		dAppDate = new PopupDateField();
		dAppDate.setImmediate(true);
		dAppDate.setWidth("108px");
		dAppDate.setHeight("-1px");
		dAppDate.setInvalidAllowed(false);
		dAppDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dAppDate.setDateFormat("dd-MM-yyyy");
		dAppDate.setValue(new java.util.Date());
		mainLayout.addComponent(dAppDate, "top:08.0px;left:443.0px;");

		// btnAppDate
		btnAppDate = new ImmediateUploadExampleApplication("");
		//btnAppDate.setCaption("Attach Application");
		btnAppDate.setImmediate(true);
		btnAppDate.setWidth("-1px");
		btnAppDate.setHeight("-1px");
		btnAppDate.setStyleName("uploadReq");
		mainLayout.addComponent(btnAppDate, "top:00.0px;left:553.0px;");

		// btnApplicationPreview
		btnApplicationPreview = new Button();
		btnApplicationPreview.setCaption("jpg/pdf file");
		btnApplicationPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnApplicationPreview.addStyleName("icon-after-caption");
		btnApplicationPreview.setImmediate(true);
		btnApplicationPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnApplicationPreview, "top:10.0px;left:650.0px;");

		// lblInterviewdate
		lblInterviewdate = new Label();
		lblInterviewdate.setImmediate(true);
		lblInterviewdate.setWidth("98px");
		lblInterviewdate.setHeight("-1px");
		lblInterviewdate.setValue("Interview Date:");
		mainLayout.addComponent(lblInterviewdate, "top:36.0px;left:361.0px;");

		// dInterviewDate
		dInterviewDate = new PopupDateField();
		dInterviewDate.setImmediate(true);
		dInterviewDate.setWidth("108px");
		dInterviewDate.setHeight("-1px");
		dInterviewDate.setInvalidAllowed(false);
		dInterviewDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dInterviewDate.setDateFormat("dd-MM-yyyy");
		dInterviewDate.setValue(new java.util.Date());
		mainLayout.addComponent(dInterviewDate, "top:34.0px;left:443.0px;");

		// lblJoiningDate
		lblJoiningDate = new Label();
		lblJoiningDate.setImmediate(true);
		lblJoiningDate.setWidth("74px");
		lblJoiningDate.setHeight("-1px");
		lblJoiningDate.setValue("Joining Date:");
		mainLayout.addComponent(lblJoiningDate, "top:62.0px;left:369.0px;");

		// dJoiningDate
		dJoiningDate = new PopupDateField();
		dJoiningDate.setImmediate(true);
		dJoiningDate.setWidth("108px");
		dJoiningDate.setHeight("-1px");
		dJoiningDate.setInvalidAllowed(false);
		dJoiningDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dJoiningDate.setDateFormat("dd-MM-yyyy");
		dJoiningDate.setValue(new java.util.Date());
		mainLayout.addComponent(dJoiningDate, "top:60.0px;left:443.0px;");

		// btnJoiningDate
		btnJoiningDate = new ImmediateUploadExampleJoining("");
		//btnJoiningDate.setCaption("Attach Joining Letter");
		btnJoiningDate.setImmediate(true);
		btnJoiningDate.setWidth("-1px");
		btnJoiningDate.setHeight("-1px");
		btnJoiningDate.setStyleName("uploadReq");
		mainLayout.addComponent(btnJoiningDate, "top:52.0px;left:553.0px;");

		// btnJoinPreview
		btnJoinPreview = new Button();
		btnJoinPreview.setCaption("jpg/pdf file");
		btnJoinPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnJoinPreview.addStyleName("icon-after-caption");
		btnJoinPreview.setImmediate(true);
		btnJoinPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnJoinPreview, "top:62.0px;left:655.0px;");

		// lblConfirmDate
		lblConfirmDate = new Label();
		lblConfirmDate.setImmediate(true);
		lblConfirmDate.setWidth("104px");
		lblConfirmDate.setHeight("-1px");
		lblConfirmDate.setValue("Confirmation Date:");
		mainLayout.addComponent(lblConfirmDate, "top:88.0px;left:337.0px;");

		// dConDate
		dConDate = new PopupDateField();
		dConDate.setImmediate(true);
		dConDate.setWidth("108px");
		dConDate.setHeight("-1px");
		dConDate.setInvalidAllowed(false);
		dConDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dConDate.setDateFormat("dd-MM-yyyy");
		//dConDate.setValue(new java.util.Date());
		mainLayout.addComponent(dConDate, "top:86.0px;left:443.0px;");

		// btnConfimdate
		btnConfirmdate = new ImmediateUploadExampleConfirmation("");
		//btnConfirmdate.setCaption("Attach Confirmation Letter");
		btnConfirmdate.setImmediate(true);
		btnConfirmdate.setWidth("-1px");
		btnConfirmdate.setHeight("-1px");
		btnConfirmdate.setStyleName("uploadReq");
		mainLayout.addComponent(btnConfirmdate, "top:79.0px;left:553.0px;");

		// btnConPreview
		btnConPreview = new Button();
		btnConPreview.setCaption("jpg/pdf file");
		btnConPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnConPreview.addStyleName("icon-after-caption");
		btnConPreview.setImmediate(true);
		btnConPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnConPreview, "top:88.0px;left:682.0px;");

		// lblStatus
		lblStatus = new Label();
		lblStatus.setImmediate(true);
		lblStatus.setWidth("48px");
		lblStatus.setHeight("-1px");
		lblStatus.setValue("Status:");
		mainLayout.addComponent(lblStatus, "top:116.0px;left:403.0px;");

		// cmbStatus
		cmbStatus = new ComboBox();
		cmbStatus.setImmediate(true);
		cmbStatus.setWidth("108px");
		cmbStatus.setHeight("-1px");
		addStatus();
		//cmbStatus.setValue(status[0]);
		mainLayout.addComponent(cmbStatus, "top:114.0px;left:443.0px;");

		//lblDateName
		lblDateName = new Label();
		lblDateName.setWidth("48px");
		lblDateName.setHeight("-1px");
		//lblDateName.setEnabled(false);
		lblDateName.setCaption("");
		mainLayout.addComponent(lblDateName, "top:116.0px;left:557.0px;");

		// dDate	
		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("108px");
		dDate.setHeight("-1px");
		dDate.setInvalidAllowed(false);
		dDate.setEnabled(false);
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(dDate, "top:114.0px;left:627.0px;");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:143.0px;left:357.0px;");

		// lblSection
		lblDepartment = new Label();
		lblDepartment.setImmediate(true);
		lblDepartment.setWidth("73px");
		lblDepartment.setHeight("-1px");
		lblDepartment.setValue("Department:");
		mainLayout.addComponent(lblDepartment, "top:143.0px; left:365.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("164px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDepartment, "top:141.0px; left:443.0px;");

		// btnSubUnit
		btnDepartment = new NativeButton();
		btnDepartment.setCaption("");
		btnDepartment.setIcon(new ThemeResource("../icons/add.png"));
		btnDepartment.setImmediate(true);
		btnDepartment.setWidth("28px");
		btnDepartment.setHeight("24px");
		mainLayout.addComponent(btnDepartment, "top:141.0px;left:611.0px;");

		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:168.0px;left:382.0px;");
		
		// lblSection
		lblSection = new Label();
		lblSection.setImmediate(true);
		lblSection.setWidth("73px");
		lblSection.setHeight("-1px");
		lblSection.setValue("Section:");
		mainLayout.addComponent(lblSection, "top:168.0px; left:390.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("164px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:166.0px; left:443.0px;");

		// btnSubUnit
		btnSection = new NativeButton();
		btnSection.setCaption("");
		btnSection.setIcon(new ThemeResource("../icons/add.png"));
		btnSection.setImmediate(true);
		btnSection.setWidth("28px");
		btnSection.setHeight("24px");
		mainLayout.addComponent(btnSection, "top:166.0px;left:611.0px;");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:192.0px;left:358.0px;");

		// lblDesignation
		lblDesignation = new Label();
		lblDesignation.setImmediate(true);
		lblDesignation.setWidth("73px");
		lblDesignation.setHeight("-1px");
		lblDesignation.setValue("Designation:");
		mainLayout.addComponent(lblDesignation, "top:193.0px; left:365.0px;");

		// cmbDesignation
		cmbDesignation = new ComboBox();
		cmbDesignation.setImmediate(true);
		cmbDesignation.setWidth("164px");
		cmbDesignation.setHeight("-1px");
		mainLayout.addComponent(cmbDesignation, "top:191.0px;left:443.0px;");

		// btnDesignation
		btnDesignation = new NativeButton();
		btnDesignation.setCaption("");
		btnDesignation.setIcon(new ThemeResource("../icons/add.png"));
		btnDesignation.setImmediate(true);
		btnDesignation.setWidth("28px");
		btnDesignation.setHeight("24px");
		mainLayout.addComponent(btnDesignation, "top:191.0px;left:611.0px;");
		
		// lblDesignation
		lblGrades = new Label();
		lblGrades.setImmediate(true);
		lblGrades.setWidth("73px");
		lblGrades.setHeight("-1px");
		lblGrades.setValue("Grade :");
		mainLayout.addComponent(lblGrades, "top:220.0px; left:380.0px;");

		// cmbDesignation
		cmbGrades = new ComboBox();
		cmbGrades.setImmediate(true);
		cmbGrades.setWidth("164px");
		cmbGrades.setHeight("-1px");
		mainLayout.addComponent(cmbGrades, "top:218.0px;left:443.0px;");

		// btnDesignation
		btnGrades = new NativeButton();
		btnGrades.setCaption("");
		btnGrades.setIcon(new ThemeResource("../icons/add.png"));
		btnGrades.setImmediate(true);
		btnGrades.setWidth("28px");
		btnDesignation.setHeight("24px");
		mainLayout.addComponent(btnGrades, "top:220.0px;left:611.0px;");

		lblShiftId=new Label();
		lblShiftId.setImmediate(true);
		lblShiftId.setWidth("73px");
		lblShiftId.setHeight("-1px");
		lblShiftId.setValue("Shift:");
		mainLayout.addComponent(lblShiftId, "top:250.0px;left:405.0px;");

		cmbShiftId=new ComboBox();
		cmbShiftId.setImmediate(true);
		cmbShiftId.setWidth("164px");
		cmbShiftId.setHeight("-1px");
		mainLayout.addComponent(cmbShiftId, "top:248.0px; left:443.0px;");

		// cmbDesignation
		chkOtEnable = new CheckBox("OT Enable");
		chkOtEnable.setImmediate(true);
		chkOtEnable.setHeight("-1px");
		chkOtEnable.setWidth("-1px");
		mainLayout.addComponent(chkOtEnable, "top:280.0px;left:370.0px;");

		chkFriEnable=new CheckBox("Friday Enable");
		chkFriEnable.setImmediate(true);
		chkFriEnable.setHeight("-1px");
		chkFriEnable.setWidth("-1px");
		mainLayout.addComponent(chkFriEnable, "top:280.0px; left:470.0px;");

		chkPhysicallyDesable=new CheckBox("Physically Disabled");
		chkPhysicallyDesable.setImmediate(true);
		mainLayout.addComponent(chkPhysicallyDesable, "top:280.0px; left:570.0px;");

		// lblFloor
		lblFloor = new Label();
		lblFloor.setImmediate(true);
		lblFloor.setWidth("40px");
		lblFloor.setHeight("-1px");
		lblFloor.setValue("Floor:");
		lblFloor.setVisible(false);
		mainLayout.addComponent(lblFloor, "top:221.0px;left:409.0px;");

		// cmbFloor
		cmbFloor = new ComboBox();
		cmbFloor.setImmediate(true);
		cmbFloor.setWidth("164px");
		cmbFloor.setHeight("-1px");
		cmbFloor.setVisible(false);
		mainLayout.addComponent(cmbFloor, "top:217.0px;left:443.0px;");

		// btnFloor
		btnFloor = new Button();
		btnFloor.setCaption("");
		btnFloor.setIcon(new ThemeResource("../icon/add.png"));
		btnFloor.setImmediate(true);
		btnFloor.setWidth("-1px");
		btnFloor.setHeight("-1px");
		btnFloor.setVisible(false);
		mainLayout.addComponent(btnFloor, "top:216.0px;left:611.0px;");

		// lblLine
		lblLine = new Label();
		lblLine.setImmediate(true);
		lblLine.setWidth("47px");
		lblLine.setHeight("-1px");
		lblLine.setValue("Line:");
		lblLine.setVisible(false);
		mainLayout.addComponent(lblLine, "top:245.0px;left:413.0px;");

		// cmbLine
		cmbLine = new ComboBox();
		cmbLine.setImmediate(true);
		cmbLine.setWidth("164px");
		cmbLine.setHeight("-1px");
		cmbLine.setVisible(false);
		mainLayout.addComponent(cmbLine, "top:243.0px;left:443.0px;");

		// btnLine
		btnLine = new Button();
		btnLine.setCaption("");
		btnLine.setIcon(new ThemeResource("../icon/add.png"));
		btnLine.setImmediate(true);
		btnLine.setWidth("-1px");
		btnLine.setHeight("-1px");
		btnLine.setVisible(false);
		mainLayout.addComponent(btnLine, "top:241.0px;left:611.0px;");

		// lblGrade
		lblGrade = new Label();
		lblGrade.setImmediate(true);
		lblGrade.setWidth("39px");
		lblGrade.setHeight("-1px");
		lblGrade.setValue("Grade:");
		lblGrade.setVisible(false);
		mainLayout.addComponent(lblGrade, "top:272.0px;left:403.0px;");

		// cmbGrade
		cmbGrade = new ComboBox();
		cmbGrade.setImmediate(true);
		cmbGrade.setWidth("164px");
		cmbGrade.setHeight("-1px");
		cmbGrade.setVisible(false);
		mainLayout.addComponent(cmbGrade, "top:269.0px;left:443.0px;");

		// btnGrade
		btnGrade = new Button();
		btnGrade.setCaption("");
		btnGrade.setIcon(new ThemeResource("../icon/add.png"));
		btnGrade.setImmediate(true);
		btnGrade.setWidth("-1px");
		btnGrade.setHeight("-1px");
		btnGrade.setVisible(false);
		mainLayout.addComponent(btnGrade, "top:267.0px;left:611.0px;");

		//studentImage
		Image = new FileUpload("Picture");
		Image.upload.setButtonCaption("Employee Image");
		mainLayout.addComponent(Image, "top:140.0px;left:717.0px;");

		// lblNid
		lblZone = new Label();
		lblZone.setImmediate(true);
		lblZone.setWidth("-1px");
		lblZone.setHeight("-1px");
		lblZone.setValue("Zone:");
		mainLayout.addComponent(lblZone, "top:370.0px;left:479.0px;");

		// cmbEmpType
		txtZone = new TextField();
		txtZone.setImmediate(true);
		txtZone.setWidth("110.0px");
		mainLayout.addComponent(txtZone, "top:368.0px;left:513.0px;");
		
		btnZoneNO = new ImmediateUploadExampleBirth("");
		btnZoneNO.setImmediate(true);
		btnZoneNO.setWidth("90.0px");
		mainLayout.addComponent(btnZoneNO, "top:363.0px; left:628.0px;");

		// btnApplicationPreview
		btnZonePreview = new Button();
		btnZonePreview.setCaption("jpg/pdf file");
		btnZonePreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnZonePreview.addStyleName("icon-after-caption");
		btnZonePreview.setImmediate(true);
		btnZonePreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnZonePreview, "top:370.0px;left:698.0px;");

		return mainLayout;
	}

	public Object generateEmployeeId()
	{
		String Id = null;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "Select cast(isnull(max(cast(replace(SUBSTRING(vEmployeeId,5,Len(vEmployeeId)), '', '')as int))+1, 1)as varchar) from tbEmployeeInfo";
			int num = 0;
			Iterator <?> iter = session.createSQLQuery(query).list().iterator();
			if (iter.hasNext()) 
			{
				num = Integer.parseInt(iter.next().toString());
				Id = "EMP-"+(num);
			}	
		}
		catch(Exception ex)
		{
			
		}
		finally{session.close();}
		return Id;
	}

	private void addReligion()
	{
		for(int i = 0;i<religion.length;i++)
		{
			cmbReligion.addItem(religion[i]);
		}
	}

	private void addStatus()
	{
		for(int i=0;i<status.length;i++)
		{
			cmbStatus.addItem(status[i]);
		}
	}
}