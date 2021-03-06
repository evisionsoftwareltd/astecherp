package database.hibernate;

// Generated Jan 11, 2011 10:26:51 AM by Hibernate Tools 3.3.0.GA

/**
 * TbMainGroup generated by hbm2java
 */
public class TbMainGroup implements java.io.Serializable {

	private String groupId;
	private String groupName;
	private String headId;

	public TbMainGroup() {
	}

	public TbMainGroup(String groupId) {
		this.groupId = groupId;
	}

	public TbMainGroup(String groupId, String groupName, String headId) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.headId = headId;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getHeadId() {
		return this.headId;
	}

	public void setHeadId(String headId) {
		this.headId = headId;
	}

}
