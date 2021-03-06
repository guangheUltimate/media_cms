package io.guangsoft.media.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

public interface BaseTreeMapper<T> extends BaseMapper<T> {
	/**
	 * @param id
	 * @return
	 * @return: Menu
	 */
	T selectByTreeId(Serializable id);

	/**
	 * @param wrapper
	 * @return
	 * @return: List<T>
	 */
	List<T> selectTreeList(@Param("ew") Wrapper<T> wrapper);

	/**
	 * @param newParentIds
	 * @param oldParentIds
	 * @return: void
	 */
	void updateSunTreeParentIds(@Param("newParentIds") String newParentIds, @Param("oldParentIds") String oldParentIds);

	/**
	 * @param parentIds
	 * @return
	 * @return: List<T>
	 */
	void deleteSunTree(String parentIds);

}