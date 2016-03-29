package com.eryansky.core.db.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.SysConstants;
import com.google.common.collect.Lists;
import com.eryansky.core.db.dao.ExtractDAO;
import com.eryansky.core.db.dao.LoadDao;
import com.eryansky.core.db.meta.DiDataSource;
import com.eryansky.core.db.vo.ColumnVo;
import com.eryansky.core.db.vo.DbConfig;
import com.eryansky.core.db.vo.SchemaVo;
import com.eryansky.core.db.vo.TableVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.List;

/**
 * 元数据管理
 * @author wencp@尔演&Eryan eryanwcp@gmail.com
 *
 */
@Service
public class MetaService {

	private static final Logger logger = LoggerFactory
			.getLogger(MetaService.class);

	private ExtractDAO extractDAO;
	private LoadDao loadDao;
	private DbConfig localDatasource;//本地数据源

	public MetaService() {
		super();
		try {
			localDatasource = new DbConfig(
					SysConstants.getJdbcDriverClassName(),
					SysConstants.getJdbcUrl(), SysConstants.getJdbcUserName(),
					SysConstants.getJdbcPassword());
			this.extractDAO = new ExtractDAO(localDatasource);
			this.loadDao = new LoadDao(localDatasource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到模式列表 （本地数据源）
	 * 
	 * @return
	 * @throws com.eryansky.common.exception.DaoException
	 * @throws SQLException
	 */
	public List<SchemaVo> getSchemaVos() throws DaoException, SQLException {
		return getSchemaVos(localDatasource);
	}

	/**
	 * 得到模式列表
	 *
	 * @param dbDatasource
	 *            数据源
	 * @return
	 * @throws com.eryansky.common.exception.DaoException
	 * @throws SQLException
	 */
	public List<SchemaVo> getSchemaVos(DbConfig dbDatasource)
			throws DaoException, SQLException {
		Assert.notNull(dbDatasource, "参数[dbDatasource]为空!");
		DiDataSource diDataSource = extractDAO.getDataSource();
		return diDataSource.getSchemaVos();
	}

	/**
	 * 得到所有表列表 （本地数据源）
	 *
	 * @return
	 * @throws com.eryansky.common.exception.DaoException
	 * @throws SQLException
	 */
	public List<TableVo> getAllTableVos() throws DaoException {
		return getAllTableVos(localDatasource);
	}

	/**
	 * 得到所有表列表
	 *
	 * @param dbDatasource
	 *            数据源
	 * @return
	 * @throws com.eryansky.common.exception.DaoException
	 * @throws SQLException
	 */
	public List<TableVo> getAllTableVos(DbConfig dbDatasource)
			throws DaoException {
		Assert.notNull(dbDatasource, "参数[dbDatasource]为空!");
		return getTableVos(dbDatasource, null, null, "%");
	}

	/**
	 * 得到表列表 （本地数据源）
	 *
	 * @param catalog
	 *            目录（%通配）
	 * @param schemaPattern
	 *            模式 （%通配）
	 * @param tableNamePattern
	 *            表 （%通配）
	 * @return
	 * @throws com.eryansky.common.exception.DaoException
	 */
	public List<TableVo> getTableVos(String catalog, String schemaPattern,
			String tableNamePattern) throws DaoException {
		return getTableVos(this.localDatasource, catalog, schemaPattern,
				tableNamePattern);
	}

	/**
	 * 得到表列表
	 *
	 * @param dbDatasource
	 *            数据源
	 * @param catalog
	 *            目录（%通配）
	 * @param schemaPattern
	 *            模式 （%通配）
	 * @param tableNamePattern
	 *            表 （%通配）
	 * @return
	 * @throws com.eryansky.common.exception.DaoException
	 */
	public List<TableVo> getTableVos(DbConfig dbDatasource, String catalog,
			String schemaPattern, String tableNamePattern) throws DaoException {
		Assert.notNull(dbDatasource, "参数[dbDatasource]为空!");
		String schemaP = schemaPattern;
		String catelogP = catalog;
		List<TableVo> tableVos = null;
		try {
			DiDataSource diDataSource = extractDAO.getDataSource();
			if (StringUtils.isBlank(schemaPattern)) {
				schemaP = diDataSource.getSchema();
			}
			if (StringUtils.isBlank(catalog)) {
				catelogP = diDataSource.getCatalog();
			}
			tableVos = diDataSource.getTableVos(catelogP, schemaP,
                    tableNamePattern);
		} catch (Exception e) {
			throw new DaoException(e);
		}
		return tableVos;
	}

	/**
	 * 得到表字段列表 （本地数据源）
	 *
	 * @param catalog
	 *            目录（%通配）
	 * @param schemaPattern
	 *            模式 （%通配）
	 * @param tableNamePattern
	 *            表 （%通配）
	 * @return
	 * @throws com.eryansky.common.exception.DaoException
	 */
	public List<ColumnVo> getColumnVos(String catalog, String schemaPattern,
			String tableNamePattern) throws DaoException {
		return getColumnVos(localDatasource, catalog, schemaPattern,
				tableNamePattern);
	}

	/**
	 * 得到表字段列表
	 *
	 * @param dbDatasource
	 *            数据源
	 * @param catalog
	 *            目录（%通配）
	 * @param schemaPattern
	 *            模式 （%通配）
	 * @param tableNamePattern
	 *            表 （%通配）
	 * @return
	 * @throws com.eryansky.common.exception.DaoException
	 */
	public List<ColumnVo> getColumnVos(DbConfig dbDatasource,
			String catalog, String schemaPattern, String tableNamePattern)
			throws DaoException {
		Assert.notNull(dbDatasource);
		String schemaP = schemaPattern;
		String catelogP = catalog;
		List<ColumnVo> columnVos = Lists.newArrayList();
		try {
			DiDataSource diDataSource = extractDAO.getDataSource();
			if (StringUtils.isBlank(schemaPattern)) {
				schemaP = diDataSource.getSchema();
			}
			if (StringUtils.isBlank(catalog)) {
				catelogP = diDataSource.getCatalog();
			}
			columnVos = diDataSource.getColumnVos(catelogP, schemaP,
					tableNamePattern);
		} catch (Exception e) {
			throw new DaoException(e);
		}
		return columnVos;
	}

	public DbConfig getLocalDatasource() {
		return localDatasource;
	}

}
