package com.example.yy.dashgraduationdesign.Entities;

import com.example.yy.dashgraduationdesign.Celluar.CellularDownPolicy;
import com.example.yy.dashgraduationdesign.Celluar.CellularSharePolicy;
import com.example.yy.dashgraduationdesign.Celluar.TCPDown;
import com.example.yy.dashgraduationdesign.Celluar.TCPShare;
import com.example.yy.dashgraduationdesign.util.dipatchers.Bus;
import com.example.yy.dashgraduationdesign.util.dipatchers.Dispatcher;
import com.example.yy.dashgraduationdesign.util.dipatchers.MultiPullDispatcher;

public class ConfigureData {
	private static final String TAG = ConfigureData.class.getSimpleName();
	private CellularDownPolicy cellularDownPolicy;
	private CellularSharePolicy cellularSharePolicy;
	private String url ;
	private boolean serviceAlive;
	private WorkMode workingMode;
	private Dispatcher dispatcher;

	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public CellularDownPolicy getCellularDownPolicy() {
		return cellularDownPolicy;
	}

	public CellularSharePolicy getCellularSharePolicy() {
		return cellularSharePolicy;
	}

	public Dispatcher getDispatcher() {
		return dispatcher;
	}
	public  enum WorkMode {
		LOCAL_MODE,G_MDOE,COOPERATIVE_MODE,JUNIT_TEST_MODE,
		FAKE_MODE
	}

	private ConfigureData() {
	}


	public WorkMode getWorkingMode() {
		return workingMode;
	}

	public void setWorkingMode(WorkMode workingMode) {
		this.workingMode = workingMode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isServiceAlive() {
		return serviceAlive;
	}

	public void setServiceAlive(boolean serviceAlive) {
		this.serviceAlive = serviceAlive;
	}
	public static class Builder{
		private String url = "http://127.0.0.1:9999/4/index.m3u8";
		private boolean serviceAlive = false;
		private WorkMode workingMode = WorkMode.LOCAL_MODE;
		private CellularDownPolicy cellularDownPolicy = new TCPDown();
		private CellularSharePolicy cellularSharePolicy = new TCPShare();
		private Dispatcher dispatcher;

		public ConfigureData build() {
			ConfigureData configureData = new ConfigureData();
			configureData.cellularDownPolicy = cellularDownPolicy;
			configureData.cellularSharePolicy = cellularSharePolicy;
			configureData.workingMode = workingMode;
			configureData.serviceAlive = serviceAlive;
			configureData.url = url;
			configureData.dispatcher = dispatcher;
			return configureData;
		}

		public Builder setUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder setWorkingMode(WorkMode workingMode) {
			this.workingMode = workingMode;
			return this;
		}

		public Builder setCellularDownPolicy(CellularDownPolicy cellularDownPolicy) {
			this.cellularDownPolicy = cellularDownPolicy;
			return this;
		}

		public Builder setCellularSharePolicy(CellularSharePolicy cellularSharePolicy) {
			this.cellularSharePolicy = cellularSharePolicy;
			return this;
		}

		public Builder setDispatcher(Dispatcher dispatcher) {
			this.dispatcher = dispatcher;
			return this;
		}
	}

}
