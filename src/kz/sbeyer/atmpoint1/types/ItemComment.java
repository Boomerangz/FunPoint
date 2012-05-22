package kz.sbeyer.atmpoint1.types;

public class ItemComment {

	int id;	
	String cmntUsrname;
	String cmntDatetime;
	String objType;
	String objName;
	String objTypeTitle;
	String objId;
	String cmntText;
	String errBtnId;
	String cmntType;
	String bankid;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	
	
	public String getCmntUsrname() {
		return cmntUsrname;
	}

	public void setCmntUsrname(String cmntUsrname) {
		this.cmntUsrname = cmntUsrname;
	}
	
	public String getCmntDatetime() {
		return cmntDatetime;
	}

	public void setCmntDatetime(String cmntDatetime) {
		this.cmntDatetime = cmntDatetime;
	}
	
	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}
	
	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}
	
	public String getObjTypeTitle() {
		return objTypeTitle;
	}

	public void setObjTypeTitle(String objTypeTitle) {
		this.objTypeTitle = objTypeTitle;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}	
	
	public String getCmntText() {
		return cmntText;
	}

	public void setCmntText(String cmntText) {
		this.cmntText = cmntText;
	}
	
	public String getErrBtnId() {
		return errBtnId;
	}

	public void setErrBtnId(String errBtnId) {
		this.errBtnId = errBtnId;
	}
	
	public String getCmntType() {
		return cmntType;
	}

	public void setCmntType(String cmntType) {
		this.cmntType = cmntType;
	}
	
	public String getBankid() {
		return bankid;
	}

	public void setBankid(String bankid) {
		this.bankid = bankid;
	}
}
