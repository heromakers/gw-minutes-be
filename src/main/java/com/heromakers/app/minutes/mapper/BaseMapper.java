package com.heromakers.app.minutes.mapper;

import java.util.List;
import java.util.Map;

public interface BaseMapper<T, PK> {
    public List<T> queryList(Map param);
    public int queryCount(Map param);
    public T selectOne (PK id);
    public int insert (T obj);
    public int update (T obj);
    public int deleteFlag(PK id);
}