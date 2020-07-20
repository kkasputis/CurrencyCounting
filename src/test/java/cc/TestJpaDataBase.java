package cc;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import cc.entity.CurrencyName;
import cc.entity.CurrencyRate;
import cc.repository.CurrencyNameRepository;
import cc.repository.CurrencyRateRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestJpaDataBase {

    @Autowired
    private TestEntityManager entityManager;
 
    @Autowired
    private CurrencyNameRepository currencyNameRepository;
    @Autowired
    private CurrencyRateRepository currencyRateRepository;

 
	@Test
	public void findByCode() {
	  
	    CurrencyName name = new CurrencyName();
	    name.setCode("USD");
	    name.setNameLt("Doleriai");
	    entityManager.persist(name);
	    entityManager.flush();
	 
	   
	    CurrencyName found = currencyNameRepository.findOneByCode(name.getCode()).get();
	  
	    assertThat(found.getCode())
	      .isEqualTo(name.getCode());
	}
	
	
	@Test
	public void findByRateRepository() {
	   
	    CurrencyName name = new CurrencyName();
	    name.setCode("USD");
	    name.setNameLt("Doleriai");
	    entityManager.persist(name);

	 
	    for (int x = 0; x < 6; x++) {
	    CurrencyRate rate = new CurrencyRate();
	    rate.setName(name);
	    rate.setDate(LocalDate.now().minusDays(x));
	    entityManager.persist(rate);
	    }
	    entityManager.flush();
	 
	    List<CurrencyRate> found = currencyRateRepository.findByDateAfterAndDateBeforeAndNameOrderByDate(LocalDate.now().minusDays(6),LocalDate.now(), name).get();
	    CurrencyRate foundDate = currencyRateRepository.findTopByNameOrderByDateDesc(name).orElse(null);
	

	    assertThat(found.size() == 5);
	    assertThat(foundDate.getDate().equals(LocalDate.now()));
	}

}