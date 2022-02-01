package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

public class Node {

	//private String id;
	//private String subgroupid;
	private String groupname;
	private String headname;
	private String path;

	private List<Node> children;
	private Amounts amount;

	public Node() {
		super();
	}

	public Node( String groupname, String headname, String path, Amounts amount) {
		super();
		this.groupname = groupname;
		this.headname = headname;
		this.path = path;
		this.amount = amount;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getHeadname() {
		return headname;
	}

	public void setHeadname(String headname) {
		this.headname = headname;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public Amounts getAmount() {
		return amount;
	}

	public void setAmount(Amounts amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Node [groupname=" + groupname + ", headname=" + headname
				+ ", path=" + path + ", children=" + children + ", amount=" + amount + "]";
	}
}
