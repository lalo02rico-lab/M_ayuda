package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.CatDepartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CatDepartamentoRepository extends JpaRepository<CatDepartamento, Long> {}
