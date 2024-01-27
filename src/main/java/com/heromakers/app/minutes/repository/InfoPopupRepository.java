package com.heromakers.app.minutes.repository;

import com.heromakers.app.minutes.model.InfoPopupModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoPopupRepository extends CrudRepository<InfoPopupModel, Integer> {
}
