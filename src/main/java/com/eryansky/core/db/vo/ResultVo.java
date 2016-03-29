/**
 *  Copyright (c) 2013 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 抽取结果集
 * @author 尔演&Eryan eryanwcp@gmail.com
 *
 */
public class ResultVo implements Serializable {

	// 触发器专用参数
	public static final String SDI_ACTION_FLAG = "__sdi_action_flag";

	public static final String SDI_INDEX_ID = "__sdi_index_id";

	private boolean sizeLimit = false;

	private long sizeCount = 0;

	private String name = null; // 眏射表名

	private List<Map<String, String>> metadata = null;

	private List<Map<String, String>> rows = null;

	private Map<String, byte[]> lobs = null;

    /**
     * 时间戳专用参数
     */
	private String maxTimestamp = null;

    private Date extractStart;
    
    private Date extractEnd;
    
    private Date loadStart;
    
    private Date loadEnd;

    private String extractCacheElementKey;

    public ResultVo() {
    }

    public String getMaxTimestamp() {
		return maxTimestamp;
	}

	public void setMaxTimestamp(String maxTimestamp) {
		this.maxTimestamp = maxTimestamp;
	}

	public void setSizeLimit(boolean sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public boolean getSizeLimit() {
		return this.sizeLimit;
	}

	public long getSizeCount() {
		return sizeCount;
	}

	public void setSizeCount(long sizeCount) {
		this.sizeCount = sizeCount;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Map<String, String>> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<Map<String, String>> metadata) {
		this.metadata = metadata;
	}

	public List<Map<String, String>> getRows() {
		return rows;
	}

	public void setRows(List<Map<String, String>> rows) {
		this.rows = rows;
	}

	public Map<String, byte[]> getLobs() {
		return lobs;
	}

	public void setLobs(Map<String, byte[]> lobs) {
		this.lobs = lobs;
	}

	public Date getExtractStart() {
		return extractStart;
	}

	public void setExtractStart(Date extractStart) {
		this.extractStart = extractStart;
	}

	public Date getExtractEnd() {
		return extractEnd;
	}

	public void setExtractEnd(Date extractEnd) {
		this.extractEnd = extractEnd;
	}

	public Date getLoadStart() {
		return loadStart;
	}

	public void setLoadStart(Date loadStart) {
		this.loadStart = loadStart;
	}

	public Date getLoadEnd() {
		return loadEnd;
	}

	public void setLoadEnd(Date loadEnd) {
		this.loadEnd = loadEnd;
	}

    public String getExtractCacheElementKey() {
        return extractCacheElementKey;
    }

    public void setExtractCacheElementKey(String extractCacheElementKey) {
        this.extractCacheElementKey = extractCacheElementKey;
    }
}
