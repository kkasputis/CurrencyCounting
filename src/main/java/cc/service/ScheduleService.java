package cc.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cc.entity.CurrencyName;
import cc.entity.CurrencyRate;
import cc.repository.CurrencyNameRepository;
import cc.repository.CurrencyRateRepository;

@Service
public class ScheduleService {
	@Autowired
	CurrencyNameRepository currencyNameRepository;
	@Autowired
	CurrencyRateRepository currencyRateRepository;
	@Autowired
	CurrencyService currencyService;
	
	@Async
	@Scheduled(cron = "0 0 8 * * *")
	public void getNewCurrencyRates() throws ParserConfigurationException, SAXException, IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime timeNow = LocalDateTime.now();
		// Nenaudojau /webservices/FxRates/FxRates.asmx/getCurrentFxRates nes duoda
		// datas iš ateities.
		String url = "http://www.lb.lt/webservices/FxRates/FxRates.asmx/getFxRates?tp=LT&dt="
				+ timeNow.format(formatter);
		int count = currencyRateRepository.countByDate(timeNow.toLocalDate());
		if (count < 1) {
			Document doc = currencyService.getXmlResponseDocument(url);
			NodeList nodeList = doc.getElementsByTagName("FxRate");
			for (int x = 0; x < nodeList.getLength(); x++) {
				CurrencyRate currencyRate = new CurrencyRate();
				Node node = nodeList.item(x);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					CurrencyName currency = currencyNameRepository
							.findOneByCode(eElement.getElementsByTagName("Ccy").item(1).getTextContent()).orElse(null);
					if (currency != null) {
						currencyRate.setName(currency);

						currencyRate
								.setRate(new BigDecimal(eElement.getElementsByTagName("Amt").item(1).getTextContent()));
						currencyRate.setDate(LocalDate
								.parse(eElement.getElementsByTagName("Dt").item(0).getTextContent(), formatter));
						currencyRateRepository.save(currencyRate);
					}

				}
			}

		}
	}
}
