package cc.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cc.entity.CurrencyName;
import cc.entity.CurrencyRate;

@Repository
public interface CurrencyRateRepository  extends JpaRepository<CurrencyRate, Long>{

	int countByDate(LocalDate date);
	Optional<List<CurrencyRate>> findAllByDate(LocalDate date);
	Optional<List<CurrencyRate>> findByDateAfterAndDateBeforeAndNameOrderByDate(LocalDate dateFrom, LocalDate dateTo, CurrencyName name);
	Optional<CurrencyRate> findTopByNameOrderByDateDesc(CurrencyName name);
	Optional<CurrencyRate> findTopByNameAndDateAfterOrderByDate(CurrencyName name, LocalDate date);
}
