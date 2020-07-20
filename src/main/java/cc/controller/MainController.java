package cc.controller;



import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cc.entity.CurrencyName;
import cc.entity.CurrencyRate;
import cc.repository.CurrencyNameRepository;
import cc.repository.CurrencyRateRepository;
import cc.service.CurrencyService;

@Controller
public class MainController {

	@Autowired
	CurrencyService currencyService;
	@Autowired
	CurrencyNameRepository currencyNameRepository;
	@Autowired
	CurrencyRateRepository currencyRateRepository;

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model) {
		List<CurrencyRate> currentRate = currencyService.getTodaysRates();
		model.addAttribute("currentRate", currentRate);
		model.addAttribute("currencyNames", currencyNameRepository.findAll());
		return "index";
	}

	@ResponseBody
	@RequestMapping(value = "/calculate")
	public String calculate(@RequestParam String sum, @RequestParam String from, @RequestParam String to, Model model) {
		return currencyService.calculate(sum, from, to);

	}
 

	@RequestMapping(value = "/history", method = RequestMethod.POST)
	public String history(@RequestBody MultiValueMap<String, String> map, Model model) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<CurrencyRate> rateList = null;
		CurrencyName name = currencyNameRepository.findOneByCode(map.toSingleValueMap().get("currencyCode")).orElse(null);

		if (name != null) {

			rateList = currencyRateRepository
					.findByDateAfterAndDateBeforeAndNameOrderByDate(LocalDate.parse(map.toSingleValueMap().get("dateFrom"), formatter).minusDays(1),
							LocalDate.parse(map.toSingleValueMap().get("dateTo"), formatter).plusDays(1), name)
					.orElse(null);
		}
		model.addAttribute("currencyNames", currencyNameRepository.findAll());
		model.addAttribute("rateList", rateList);
		return "history";

	}
	@RequestMapping(value = "/latestrates", method = RequestMethod.GET)
	public String latestRates(Model model) {
		List<CurrencyRate> currentRate = currencyService.getTodaysRates();

		model.addAttribute("currentRate", currentRate);
		model.addAttribute("currencyNames", currencyNameRepository.findAll());
		return "latestRates";

	}
}
