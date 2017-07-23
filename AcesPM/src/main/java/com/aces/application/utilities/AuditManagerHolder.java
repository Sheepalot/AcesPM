package com.aces.application.utilities;

public class AuditManagerHolder {
	
	public static RunningAuditManager manager = null;

	public static RunningAuditManager getManager() {
		return manager;
	}

	public static void setManager(RunningAuditManager manager) {
		AuditManagerHolder.manager = manager;
	}
}
