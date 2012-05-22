package kz.sbeyer.atmpoint1.types;

public class ItemMapObject {

	int id;				
	String titleShort;	
	String titleLong;
	float longitude;
	float latitude;
	String imgSrcStr;
	int objTypeId;
	
	public int getObjTypeId() {
		return objTypeId;
	}

	public void setObjTypeId(int objTypeId) {
		this.objTypeId = objTypeId;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	
	
	public String getTitleShort() {
		return titleShort;
	}

	public void setTitleShort(String titleShort) {
		this.titleShort = titleShort;
	}
	
	public String getTitleLong() {
		return titleLong;
	}

	public void setTitleLong(String titleLong) {
		this.titleLong = titleLong;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	
	
	public String getImgSrcStr() {
		return imgSrcStr;
	}

	public void setImgSrcStr(String imgSrcStr) {
		this.imgSrcStr = imgSrcStr;
	}
}
