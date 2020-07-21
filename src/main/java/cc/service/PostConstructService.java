package cc.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import cc.entity.CurrencyName;
import cc.entity.CurrencyRate;
import cc.repository.CurrencyNameRepository;
import cc.repository.CurrencyRateRepository;

@Service
public class PostConstructService {
	@Autowired
	CurrencyNameRepository currencyNameRepository;
	@Autowired
	CurrencyRateRepository currencyRateRepository;
	@Autowired
	CurrencyService currencyService;
	
	@PostConstruct
	public void fillDataBase() throws ParserConfigurationException, SAXException, IOException {
		System.out.println("Please wait, the applications is starting...");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		Viską išvalau, kad nesiduobliuotų įrašai.

		currencyRateRepository.deleteAll();
		currencyNameRepository.deleteAll();
		System.out.println("Retrieving currency name list..");
		currencyService.getAllCurrencyNames();
		System.out.println("Retrieving currency rate history.. This might take a few minites. Please, be patient.");
		List<CurrencyName> nameList = currencyNameRepository.findAll();
		currencyService.getCurrencyRateHistory("EUR", null);
		
//			Sukuriu Euro kurso Map'a iki 2002 metų, kad nereikėtų kiekvieną kartą 
//			konvertuojant siųsti užklausos duomenų bazei ir programa galėtų pasileistų greičiau.
//			Kurdamas map'ą iškart pakeičiu Euro kursą į 1, nes visgi iš tikro euro kursas su euru visada yra 1 :)
		
		Map<LocalDate, BigDecimal> euroRateMap = new HashMap<LocalDate, BigDecimal>();

		CurrencyName eurName = currencyNameRepository.findOneByCode("EUR").orElse(null);
		List<CurrencyRate> eurRateList = currencyRateRepository
				.findByDateAfterAndDateBeforeAndNameOrderByDate(LocalDate.parse("1998-12-30", formatter),
						LocalDate.parse("2002-02-02", formatter), eurName)
				.orElse(null);
		for (CurrencyRate rate : eurRateList) {
			euroRateMap.put(rate.getDate(), rate.getRate());
			rate.setRate(new BigDecimal("1.00"));
			currencyRateRepository.save(rate);

		}

		for (CurrencyName name : nameList) {
			if (!name.getCode().equals("EUR")) {
				currencyService.getCurrencyRateHistory(name.getCode(), euroRateMap);
			}
		}
		System.out.println("Removing currency names that have no history.");
		currencyService.removeNameWithNoData();
	}
}
