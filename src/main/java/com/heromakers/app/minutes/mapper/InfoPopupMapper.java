package com.heromakers.app.minutes.mapper;

import com.heromakers.app.minutes.model.InfoPopupModel;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoPopupMapper extends BaseMapper<InfoPopupModel, Integer> {
    public InfoPopupModel getActiveOne ();
}
