package cc.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CurrencyService {
	@Autowired
	CurrencyNameRepository currencyNameRepository;
	@Autowired
	CurrencyRateRepository currencyRateRepository;

	public void getAllCurrencyNames() throws ParserConfigurationException, SAXException, IOException {
		String url = "http://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrencyList";
		Document doc = getXmlResponseDocument(url);
		NodeList nodeList = doc.getElementsByTagName("CcyNtry");
		for (int x = 0; x < nodeList.getLength(); x++) {
			CurrencyName currencyName = new CurrencyName();
			Node node = nodeList.item(x);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				currencyName.setCode(eElement.getElementsByTagName("Ccy").item(0).getTextContent());
				currencyName.setNameLt(eElement.getElementsByTagName("CcyNm").item(0).getTextContent());
				currencyName.setNameEn(eElement.getElementsByTagName("CcyNm").item(1).getTextContent());
				currencyNameRepository.save(currencyName);

			}
		}
	}

	public void getCurrencyRateHistory(String cur, Map<LocalDate, BigDecimal> euroRateMap)
			throws ParserConfigurationException, SAXException, IOException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime timeNow = LocalDateTime.now();
		final BigDecimal ltlEurRate = new BigDecimal("3.4528");

//		Istoriją imu tik nuo 1999 metu, nes iki tol nėra Euro kurso istorijos ir negalėčiau tiksliai paskaičiuoti kurso.

		String url = "http://www.lb.lt/webservices/FxRates/FxRates.asmx/getFxRatesForCurrency?tp=LT&ccy=" + cur
				+ "&dtFrom=1999-01-01&dtTo=" + timeNow.format(formatter);
		CurrencyName currency = currencyNameRepository.findOneByCode(cur).orElse(null);
		if (currency != null) {
			Document doc = getXmlResponseDocument(url);
			NodeList nodeList = doc.getElementsByTagName("FxRate");
			List<CurrencyRate> rateList = new ArrayList<>();
			for (int x = 0; x < nodeList.getLength(); x++) {
				CurrencyRate currencyRate = new CurrencyRate();
				currencyRate.setName(currency);
				Node node = nodeList.item(x);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					currencyRate.setDate(
							LocalDate.parse(eElement.getElementsByTagName("Dt").item(0).getTextContent(), formatter));

//					Jei iš LB gaunu valiutos kursą su Lito, o ne euro santykiu, perskaičiuoju į eurą.

					if (eElement.getElementsByTagName("Ccy").item(0).getTextContent().equals("LTL")) {
						BigDecimal oldRateLtl = new BigDecimal(
								eElement.getElementsByTagName("Amt").item(0).getTextContent());
						BigDecimal oldRate = new BigDecimal(
								eElement.getElementsByTagName("Amt").item(1).getTextContent());
						BigDecimal currenEurRate = new BigDecimal("1");
						if ((!cur.equals("EUR"))
								&& (currencyRate.getDate().isBefore(LocalDate.parse("2002-02-01", formatter)))
								&& (currenEurRate != null)) {
							currenEurRate = euroRateMap.get(currencyRate.getDate());

						}
						BigDecimal eurRate = oldRate.divide(oldRateLtl, 5, RoundingMode.HALF_UP)
								.multiply(ltlEurRate.divide(currenEurRate, 5, RoundingMode.HALF_UP));
						currencyRate.setRate(eurRate);
					} else {
						currencyRate
								.setRate(new BigDecimal(eElement.getElementsByTagName("Amt").item(1).getTextContent()));
					}
					rateList.add(currencyRate);
				}
			}
			currencyRateRepository.saveAll(rateList);
		}
	}

	public List<CurrencyRate> getTodaysRates() {
		List<CurrencyRate> todaysRate;
		int minus = 1;
		todaysRate = currencyRateRepository.findAllByDate(LocalDate.now()).orElse(null);
		while (todaysRate == null) {
			todaysRate = currencyRateRepository.findAllByDate(LocalDate.now().minusDays(minus)).orElse(null);
			minus++;
		}

		return todaysRate;
	}

	

	public String calculate(String sum, String from, String to) {
		BigDecimal bigSum = new BigDecimal(sum);
		CurrencyName nameFrom = currencyNameRepository.findOneByCode(from).orElse(null);
		if (nameFrom == null) {
			return "Error finding currency name";
		}
		CurrencyRate fromRateObject = currencyRateRepository.findTopByNameOrderByDateDesc(nameFrom).orElse(null);
		if (fromRateObject == null) {
			return "Error finding currency rate";
		}
		BigDecimal fromRate = fromRateObject.getRate();
		CurrencyName nameTo = currencyNameRepository.findOneByCode(to).orElse(null);
		if (nameTo == null) {
			return "Error finding currency name";
		}
		CurrencyRate toRateObject = currencyRateRepository.findTopByNameOrderByDateDesc(nameTo).orElse(null);
		if (toRateObject == null) {
			return "Error finding currency rate";
		}
		BigDecimal toRate = toRateObject.getRate();
		return bigSum.divide(fromRate, 6, RoundingMode.HALF_UP).multiply(toRate).stripTrailingZeros().toString();
	}

	public Document getXmlResponseDocument(String url) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document doc = factory.newDocumentBuilder().parse(new URL(url).openStream());
		return doc;
	}

	public void removeNameWithNoData() {
		List<CurrencyName> nameList = currencyNameRepository.findAll();
		for (CurrencyName name : nameList) {
			if (name.getRate().size() < 1) {
				currencyNameRepository.delete(name);

			}

		}
	}



}
