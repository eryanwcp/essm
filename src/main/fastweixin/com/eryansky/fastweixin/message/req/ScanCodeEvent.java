package com.eryansky.fastweixin.message.req;

public final class ScanCodeEvent extends BaseEvent {

	private String eventKey;
	private String scanType;
	private String scanResult;

    public ScanCodeEvent(String eventKey, String scanType, String scanResult) {
        super();
        this.eventKey = eventKey;
        this.scanType = scanType;
        this.scanResult = scanResult;
    }

	public String getEventKey() {
		return eventKey;
	}

	public ScanCodeEvent setEventKey(String eventKey) {
		this.eventKey = eventKey;
		return this;
	}

	public String getScanType() {
		return scanType;
	}

	public ScanCodeEvent setScanType(String scanType) {
		this.scanType = scanType;
		return this;
	}

	public String getScanResult() {
		return scanResult;
	}

	public ScanCodeEvent setScanResult(String scanResult) {
		this.scanResult = scanResult;
		return this;
	}

	@Override
    public String toString() {
        return "ScanCodeEvent [eventKey=" + eventKey + ", scanType=" + scanType + ", scanResult=" + scanResult + ", toUserName=" + toUserName
                + ", fromUserName=" + fromUserName + ", createTime="
                + createTime + ", msgType=" + msgType + "]";
    }
}
