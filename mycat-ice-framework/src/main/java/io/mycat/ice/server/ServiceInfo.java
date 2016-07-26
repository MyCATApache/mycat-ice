package io.mycat.ice.server;

/**
 * by song lin
 */
import java.io.Serializable;

import Ice.Communicator;
import IceBox.Service;

public class ServiceInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6768752333560473529L;
	private Service service = null;
	private String jarSite = null;
	private String jarNames = null;
	private String serventClassName = null;

	private String name;
	private Communicator communicator;
	private String[] args;

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getJarNames() {
		return jarNames;
	}

	public void setJarNames(String jarNames) {
		this.jarNames = jarNames;
	}

	public String getServentClassName() {
		return serventClassName;
	}

	public void setServentClassName(String serventClassName) {
		this.serventClassName = serventClassName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Communicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

}
