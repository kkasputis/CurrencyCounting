package cc.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "currency_rate")
public class CurrencyRate {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
private long id;
private LocalDate date;
@Column(columnDefinition="Decimal(19,5)")
private BigDecimal rate;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "name", nullable = false)
private CurrencyName name;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}


public LocalDate getDate() {
	return date;
}
public void setDate(LocalDate date) {
	this.date = date;
}

public BigDecimal getRate() {
	return rate;
}
public void setRate(BigDecimal rate) {
	this.rate = rate;
}
public CurrencyName getName() {
	return name;
}
public void setName(CurrencyName name) {
	this.name = name;
}


}
