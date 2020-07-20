package cc;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cc.controller.MainController;
import cc.service.CurrencyService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeviceTest {
	



    @Autowired
    private CurrencyService currencyService;
    @Autowired
	private MainController controller;

	@Test
	public void contexLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

    @Test
	public void calculationTest() {

 	    String response = currencyService.calculate("1", "AAB", "BBA");
 	  
  	  assertThat(response.equals("Error finding currency name"));
    }
}
