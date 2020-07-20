package cc.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "currency_name")
public class CurrencyName {
	
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private long id;
private String code;
private String nameLt;
private String nameEn;
@OneToMany(fetch = FetchType.LAZY, mappedBy = "name")
private List<CurrencyRate> rate;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
public String getNameLt() {
	return nameLt;
}
public void setNameLt(String nameLt) {
	this.nameLt = nameLt;
}
public String getNameEn() {
	return nameEn;
}
public void setNameEn(String nameEn) {
	this.nameEn = nameEn;
}
public List<CurrencyRate> getRate() {
	return rate;
}
public void setRate(List<CurrencyRate> rate) {
	this.rate = rate;
}


}
