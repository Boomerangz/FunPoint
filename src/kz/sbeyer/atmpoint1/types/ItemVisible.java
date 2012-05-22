package kz.sbeyer.atmpoint1.types;

//id - id из таблицы
//add - адрес
//lon - долгота
//lat - широта
//bnkid - id банка
//al - альяс
//rat - рейтинг
//typ - тип банкомата(Евро, Доллар)
//cas - является ли банкомат кэш-ин(0-нет,1-да)
//{'id':,'add':'','lon':'','lat':'','bnkid':,'al':'','rat':,'typ':'','cas':}

public class ItemVisible {

	int id;				//atms, branches, exchange type
	String address;		//atms, branches, exchange type
	float longitude;	//atms, branches, exchange type
	float latitude;		//atms, branches, exchange type
	int bankId;			//atms, branches, exchange type
	String alias;		//atms, branches, exchange type
	int rating;			//atms, branches, exchange type
	String atmType;		//atms type
	int isCashIn;		//atms type
	int isValid;		//atms, branches, exchange type
	String wrktime;		//atms, branches, exchange type
	String products; 	//branches type
	String objName;	//exchange type
	String phones;		//exchange type
	
	int dist;			//atms, branches type
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
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
	
	public int getBankId() {
		return bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}	
	
	public String getAtmType() {
		return atmType;
	}

	public void setAtmType(String atmType) {
		this.atmType = atmType;
	}
	
	public int getIsCashIn() {
		return isCashIn;
	}

	public void setIsCashIn(int isCashIn) {
		this.isCashIn = isCashIn;
	}
	
	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}
	
	public String getWrktime() {
		return wrktime;
	}

	public void setWrktime(String wrktime) {
		this.wrktime = wrktime;
	}
	
	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}
	
	public String getPhones() {
		return phones;
	}

	public void setPhones(String phones) {
		this.phones = phones;
	}
	
	public int getDist() {
		return dist;
	}

	public void setDist(int dist) {
		this.dist = dist;
	}
}
