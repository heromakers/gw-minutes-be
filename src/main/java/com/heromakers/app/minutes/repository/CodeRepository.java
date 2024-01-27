package com.heromakers.app.minutes.repository;

import com.heromakers.app.minutes.model.CodeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeRepository extends CrudRepository<CodeModel, Integer> {
    List<CodeModel> findAllByParentCode(String parentCode);
}
