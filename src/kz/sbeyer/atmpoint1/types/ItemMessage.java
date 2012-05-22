package kz.sbeyer.atmpoint1.types;

public class ItemMessage {

	int id;	
	String date;
	String msgimg;
	String msgtxt;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getMsgimg() {
		return msgimg;
	}

	public void setMsgimg(String msgimg) {
		this.msgimg = msgimg;
	}
	
	public String getMsgtxt() {
		return msgtxt;
	}

	public void setMsgtxt(String msgtxt) {
		this.msgtxt = msgtxt;
	}
}
