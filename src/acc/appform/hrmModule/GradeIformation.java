package acc.appform.hrmModule;

	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.List;

	import org.hibernate.Session;
	import org.hibernate.Transaction;

	import com.common.share.CommonButton;
	import com.common.share.FocusMoveByEnter;
	import com.common.share.MessageBox;
	import com.common.share.SessionBean;
	import com.common.share.SessionFactoryUtil;
	import com.common.share.TextRead;
	import com.common.share.MessageBox.ButtonType;
	import com.common.share.MessageBox.EventListener;
	import com.vaadin.data.Property.ValueChangeEvent;
	import com.vaadin.data.Property.ValueChangeListener;
	import com.vaadin.event.ShortcutAction.KeyCode;
	import com.vaadin.event.ShortcutAction.ModifierKey;
	import com.vaadin.ui.AbsoluteLayout;
	import com.vaadin.ui.Button;
	import com.vaadin.ui.Button.ClickListener;
	import com.vaadin.ui.Button.ClickShortcut;
	import com.vaadin.ui.Component;
	import com.vaadin.ui.Label;
	import com.vaadin.ui.TextField;
	import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
	
	
	@SuppressWarnings("serial")
	public class GradeIformation extends Window 
	{
		CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

		private TextField txtFindId = new TextField();
		boolean isUpdate = false;
		boolean isNew = false;
		private AbsoluteLayout mainLayout;
		private Label lblCommon;
		private Label lblSl;
		private TextRead txtGradeID;
		private TextField txtGradeName;
		private TextRead txtGradeSL;
		private TextField txtPartyIdBack = new TextField();

		SessionBean sessionBean;

		ArrayList<Component> allComp = new ArrayList<Component>();
		private boolean isFind = false;

		public GradeIformation(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setResizable(false);
			this.setCaption("GRADE INFORMATION :: "+sessionBean.getCompany());
			buildMainLayout();
			setContent(mainLayout);
			txtInit(true);
			btnIni(true);
			focusEnter();
			btnAction();
			authenticationCheck();
			setButtonShortCut();
			cButton.btnNew.focus();
		}

		private void setButtonShortCut()
		{
			this.addAction(new ClickShortcut(cButton.btnSave, KeyCode.S, ModifierKey.ALT));
			this.addAction(new ClickShortcut(cButton.btnNew, KeyCode.N, ModifierKey.ALT));
			this.addAction(new ClickShortcut(cButton.btnRefresh, KeyCode.R, ModifierKey.ALT));
		}

		private void authenticationCheck()
		{
			if(!sessionBean.isSubmitable())
			{cButton.btnSave.setVisible(false);}
			if(!sessionBean.isUpdateable())
			{cButton.btnEdit.setVisible(false);}
			if(!sessionBean.isDeleteable())
			{cButton.btnDelete.setVisible(false);}
		}

		public void btnAction()
		{
			cButton.btnNew.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					isFind = false;
					isUpdate = false;
					txtInit(false);
					btnIni(false);
					txtClear();
					txtGradeID.setValue(selectHeadId());
					txtGradeSL.setValue(selectSl());
					txtGradeName.focus();
				}
			});

			cButton.btnSave.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					formValidation();
				}
			});

			cButton.btnEdit.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					updateButtonEvent();
				}
			});

			cButton.btnRefresh.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					isFind = false;
					isUpdate = false;
					refreshButtonEvent();
				}
			});

			cButton.btnFind.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					isFind = true;
					txtFindId.setValue("");
					findButtonEvent();
				}
			});

			cButton.btnExit.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					close();
				}
			});

			txtGradeName.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!txtGradeName.getValue().toString().trim().isEmpty())
					{
						if(duplicateDepartmentName())
						{
							showNotification("Warning!","Department already exist.",Notification.TYPE_WARNING_MESSAGE);
							txtGradeName.setValue("");
							txtGradeName.focus();
						}
					}
				}
			});
		}

		private void formValidation()
		{
			if(!txtGradeName.getValue().toString().isEmpty())
			{
				saveButtonEvent();
			}
			else
			{
				showNotification("Warning!","Provide Department Name.", Notification.TYPE_WARNING_MESSAGE);
				txtGradeName.focus();
			}
		}

		private boolean duplicateDepartmentName()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select vDepartmentName from tbDepartmentInfo where vDepartmentName" +
						" like '"+txtGradeName.getValue().toString().trim()+"'";
				Iterator<?> iter = session.createSQLQuery(query).list().iterator();
				if(iter.hasNext() && !isFind) 
				{
					return true;
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
			return false;
		}

		private void findButtonEvent() 
		{
			Window win = new GradeFindWindow(sessionBean, txtFindId, "GRADE");
			win.addListener(new Window.CloseListener() 
			{
				public void windowClose(CloseEvent e) 
				{
					if (txtFindId.getValue().toString().length() > 0)
					{
						txtClear();
						txtPartyIdBack.setValue(txtFindId.getValue().toString());
						findInitialise(txtFindId.getValue().toString());
					}
				}
			});
			this.getParent().addWindow(win);	
		}

		private String selectHeadId()
		{
			String GradeId = "";
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = " Select isnull(max(cast(SUBSTRING(vGradeId,4,LEN(vGradeId)) as int)),0)+1 from tbGradeInfo ";
				Iterator<?> iter = session.createSQLQuery(query).list().iterator();
				if(iter.hasNext())
				{
					GradeId = "GRD"+iter.next().toString();
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

			return GradeId;
		}
		
		private int selectSl()
		{
			int GradeSl=0;
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = " Select count(*)+1 from tbGradeInfo";
				Iterator<?> iter = session.createSQLQuery(query).list().iterator();
				if(iter.hasNext())
				{
					GradeSl =(Integer) iter.next();
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

			return GradeSl;
			
		}

		private void findInitialise(String DepartmentId) 
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String sql = "select vGradeId,vGradeName,vGradeSl from tbGradeInfo Where" +
						" vGradeId = '"+DepartmentId+"'";
				List<?> led = session.createSQLQuery(sql).list();
				if(led.iterator().hasNext())
				{
					Object[] element = (Object[]) led.iterator().next();

					txtGradeID.setValue(element[0].toString());
					txtGradeName.setValue(element[1].toString());
					txtGradeSL.setValue(element[2].toString());
				}
				isFind = false;
			}
			catch (Exception exp) 
			{
				showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
			}
			finally
			{
				session.close();
			}
		}

		private void refreshButtonEvent() 
		{
			txtInit(true);
			btnIni(true);
			txtClear();
			isNew=false;
		}

		private void updateButtonEvent()
		{
			if(!txtGradeID.getValue().toString().trim().isEmpty())
			{
				btnIni(false);
				txtInit(false);
				isUpdate = true;
				isFind = false;
			}
			else
			{
				showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		private void saveButtonEvent()
		{
			if(isUpdate)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							if(isUpdate)
							{
								updateData();

								btnIni(true);
								txtInit(true);
								txtClear();
								cButton.btnNew.focus();
							}
							isUpdate=false;
							isFind = false;
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
							insertData();		
							btnIni(true);
							txtInit(true);
							txtClear();

							cButton.btnNew.focus();
						}
					}
				});
			}
		}

		private void insertData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			try
			{
				String Insert = " INSERT into tbGradeInfo (vGradeId,vGradeName,vGradeSl, " +
						" vUserName,vUserIp,dEntryTime) values(" +
						" '"+selectHeadId()+"'," +
						" '"+txtGradeName.getValue().toString().trim()+"'," +
						" '"+txtGradeSL.getValue().toString().trim()+"'," +
						" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
				session.createSQLQuery(Insert).executeUpdate();

				tx.commit();
				showNotification("All information saved successfully.");
			}
			catch(Exception exp)
			{
				showNotification("insertData",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
			finally
			{
				session.close();
			}
		}

		public boolean updateData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			try
			{

				String udInsertQuery = "insert into tbUDGradeInfo (vGradeId,vGradeName,vGradeSl," +
						"vUDFlag,vUserName,vUserIp,dEntryTime) select vGradeId,vGradeName,vGradeSl," +
						"'OLD',vUserName,vUserIp,dEntryTime from tbGradeInfo where " +
						"vGradeId = '"+txtGradeID.getValue().toString().trim()+"'";
				session.createSQLQuery(udInsertQuery).executeUpdate();

				// Main table update
				String updateUnit = "UPDATE tbGradeInfo set" +
						" vGradeName = '"+txtGradeName.getValue().toString().trim()+"'," +
						" vUserName = '"+sessionBean.getUserName()+"'," +
						" vUserIp = '"+sessionBean.getUserIp()+"'," +
						" dEntryTime = GETDATE()" +
						" where vGradeId = '"+txtFindId.getValue().toString()+"' ";

				session.createSQLQuery(updateUnit).executeUpdate();

				udInsertQuery = "insert into tbUDGradeInfo (vGradeId,vGradeName,vGradeSl," +
						"vUDFlag,vUserName,vUserIp,dEntryTime) select vGradeId,vGradeName,vGradeSl," +
						"'UPDATE',vUserName,vUserIp,dEntryTime from tbGradeInfo where " +
						"vGradeId = '"+txtGradeID.getValue().toString().trim()+"'";
				session.createSQLQuery(udInsertQuery).executeUpdate();

				txtFindId.setValue("");

				showNotification("All information update successfully.");

				tx.commit();
			}
			catch(Exception exp)
			{
				tx.rollback();
				showNotification("updateData",exp+"",Notification.TYPE_ERROR_MESSAGE);
				return false;
			}
			finally
			{
				session.close();
			}
			return true;
		}

		private void focusEnter()
		{
			allComp.add(txtGradeName);
			allComp.add(cButton.btnSave);
			new FocusMoveByEnter(this,allComp);
		}

		private void txtClear()
		{
			txtGradeID.setValue("");
			txtGradeName.setValue("");
			txtGradeSL.setValue("");
		}

		public void txtInit(boolean t)
		{
			txtGradeID.setEnabled(!t);
			txtGradeName.setEnabled(!t);
			txtGradeSL.setEnabled(!t);
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

		private AbsoluteLayout buildMainLayout() 
		{
			// common part: create layout
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(false);
			mainLayout.setWidth("550px");
			mainLayout.setHeight("240px");
			mainLayout.setMargin(false);

			lblCommon = new Label("Grade ID:");
			lblCommon.setImmediate(false);
			lblCommon.setWidth("-1px");
			lblCommon.setHeight("-1px");
			mainLayout.addComponent(lblCommon, "top:40.0px; left:50.0px;");

			txtGradeID = new TextRead();
			txtGradeID.setImmediate(true);
			txtGradeID.setWidth("60px");
			txtGradeID.setHeight("24px");
			mainLayout.addComponent(txtGradeID, "top:38.0px;left:180.5px;");

			lblCommon = new Label("Grade Name :");
			mainLayout.addComponent(lblCommon, "top:80.0px; left:50.0px;");

			txtGradeName = new TextField();
			txtGradeName.setImmediate(true);
			txtGradeName.setWidth("320px");
			txtGradeName.setHeight("-1px");
			mainLayout.addComponent(txtGradeName, "top:78.0px; left:180.0px;");
			
			lblSl =new Label("Grade Serial :");
			lblSl.setImmediate(false);
			lblSl.setWidth("-1px");
			lblSl.setHeight("-1px");
			mainLayout.addComponent(lblSl,"top:120.0px; left:50.0px");
			
			txtGradeSL =new TextRead();
			txtGradeSL.setImmediate(true);
			txtGradeSL.setWidth("40.0px");
			txtGradeSL.setHeight("24px");
			mainLayout.addComponent(txtGradeSL,"top:118.0px;left:180.0px");

			mainLayout.addComponent(cButton,"top:170px; left:18px;");
			return mainLayout;
		}

}
