package com.heromakers.app.minutes.repository;

import com.heromakers.app.minutes.model.FileModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends CrudRepository<FileModel, Integer> {
    List<FileModel> findAllByLinkInfoAndLinkKey(String linkInfo, Integer linkKey);

    Optional<FileModel> findByLinkInfoAndLinkKey(String linkInfo, Integer linkKey);
}