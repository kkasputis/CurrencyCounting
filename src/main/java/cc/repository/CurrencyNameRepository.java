package cc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cc.entity.CurrencyName;



@Repository
public interface CurrencyNameRepository extends JpaRepository<CurrencyName, Long>{
	
	Optional<CurrencyName> findOneByCode(String code);
}
