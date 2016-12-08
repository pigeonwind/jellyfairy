package com.jerry.data;
/**
 * IHS log Data
 * @author jerry
 *
 */
public class IHSLogData {
	
	private String IHSLogDataID,clientIp,requestUserId,requestCompleteDate,requestCompleteTime,requestMethod,requestLine,requestProtocol,status,responseSizeExcludedHedersize;
	
	public IHSLogData(String iHSLogDataID, String clientIp, String requestUserId, String requestCompleteDate,
			String requestCompleteTime, String requestMethod, String requestLine, String requestProtocol, String status,
			String responseSizeExcludedHedersize) {
		IHSLogDataID = iHSLogDataID;
		this.clientIp = clientIp;
		this.requestUserId = requestUserId;
		this.requestCompleteDate = requestCompleteDate;
		this.requestCompleteTime = requestCompleteTime;
		this.requestMethod = requestMethod;
		this.requestLine = requestLine;
		this.requestProtocol = requestProtocol;
		this.status = status;
		this.responseSizeExcludedHedersize = responseSizeExcludedHedersize;
	}
	/**
	 * clientIp+requestUserId+requestCompleteDate+requestCompleteTime+currenttimemilly
	 * @return
	 */
	public String getIHSLogDataID() {
		return IHSLogDataID;
	}
	/**
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * @return the requestUserId
	 */
	public String getRequestUserId() {
		return requestUserId;
	}

	/**
	 * @return the requestCompleteDate
	 */
	public String getRequestCompleteDate() {
		return requestCompleteDate;
	}

	/**
	 * @return the requestCompleteTime
	 */
	public String getRequestCompleteTime() {
		return requestCompleteTime;
	}

	/**
	 * @return the requestMethod
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * @return the requestLine
	 */
	public String getRequestLine() {
		return requestLine;
	}

	/**
	 * @return the requestProtocol
	 */
	public String getRequestProtocol() {
		return requestProtocol;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the responseSizeExcludedHedersize
	 */
	public String getResponseSizeExcludedHedersize() {
		return responseSizeExcludedHedersize;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IHSLogDataID == null) ? 0 : IHSLogDataID.hashCode());
		result = prime * result + ((clientIp == null) ? 0 : clientIp.hashCode());
		result = prime * result + ((requestCompleteDate == null) ? 0 : requestCompleteDate.hashCode());
		result = prime * result + ((requestCompleteTime == null) ? 0 : requestCompleteTime.hashCode());
		result = prime * result + ((requestLine == null) ? 0 : requestLine.hashCode());
		result = prime * result + ((requestMethod == null) ? 0 : requestMethod.hashCode());
		result = prime * result + ((requestProtocol == null) ? 0 : requestProtocol.hashCode());
		result = prime * result + ((requestUserId == null) ? 0 : requestUserId.hashCode());
		result = prime * result
				+ ((responseSizeExcludedHedersize == null) ? 0 : responseSizeExcludedHedersize.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IHSLogData other = (IHSLogData) obj;
		if (IHSLogDataID == null) {
			if (other.IHSLogDataID != null)
				return false;
		} else if (!IHSLogDataID.equals(other.IHSLogDataID))
			return false;
		if (clientIp == null) {
			if (other.clientIp != null)
				return false;
		} else if (!clientIp.equals(other.clientIp))
			return false;
		if (requestCompleteDate == null) {
			if (other.requestCompleteDate != null)
				return false;
		} else if (!requestCompleteDate.equals(other.requestCompleteDate))
			return false;
		if (requestCompleteTime == null) {
			if (other.requestCompleteTime != null)
				return false;
		} else if (!requestCompleteTime.equals(other.requestCompleteTime))
			return false;
		if (requestLine == null) {
			if (other.requestLine != null)
				return false;
		} else if (!requestLine.equals(other.requestLine))
			return false;
		if (requestMethod == null) {
			if (other.requestMethod != null)
				return false;
		} else if (!requestMethod.equals(other.requestMethod))
			return false;
		if (requestProtocol == null) {
			if (other.requestProtocol != null)
				return false;
		} else if (!requestProtocol.equals(other.requestProtocol))
			return false;
		if (requestUserId == null) {
			if (other.requestUserId != null)
				return false;
		} else if (!requestUserId.equals(other.requestUserId))
			return false;
		if (responseSizeExcludedHedersize == null) {
			if (other.responseSizeExcludedHedersize != null)
				return false;
		} else if (!responseSizeExcludedHedersize.equals(other.responseSizeExcludedHedersize))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"IHSLogData [IHSLogDataID=%s, clientIp=%s, requestUserId=%s, requestCompleteDate=%s, requestCompleteTime=%s, requestMethod=%s, requestLine=%s, requestProtocol=%s, status=%s, responseSizeExcludedHedersize=%s]",
				IHSLogDataID, clientIp, requestUserId, requestCompleteDate, requestCompleteTime, requestMethod,
				requestLine, requestProtocol, status, responseSizeExcludedHedersize);
	}
}
