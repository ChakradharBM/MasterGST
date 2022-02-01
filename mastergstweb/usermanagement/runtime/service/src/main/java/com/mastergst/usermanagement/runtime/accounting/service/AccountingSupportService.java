package com.mastergst.usermanagement.runtime.accounting.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.AccountingHeads;
import com.mastergst.usermanagement.runtime.domain.Amounts;
import com.mastergst.usermanagement.runtime.domain.Node;
import com.mastergst.usermanagement.runtime.repository.AccountingHeadsRepository;

@Service
public class AccountingSupportService {

	@Autowired
	AccountingHeadsRepository accountingHeadsRepository;

	Map<String, Amounts> accJournalsMap = new HashMap<String, Amounts>();
	List<AccountingHeads> heads = null;
	Map<String, Node> mainHeadNodes = new HashMap<String, Node>();
	Map<String, Node> headNodes = new HashMap<String, Node>();

	@PostConstruct
	public void init() {
		heads = accountingHeadsRepository.findAll();
	}

	public void initiate() {
		for (AccountingHeads mainHead : heads) {
			Node node = new Node();
			node.setGroupname(mainHead.getName());
			node.setHeadname(mainHead.getHeadname());
			Amounts amounts = new Amounts(0, 0, 0, 0);
			//amounts.setPreviousyearclosingamt(0);
			node.setAmount(amounts);
			node.setPath(mainHead.getPath());
			// List<Node> nodes = new ArrayList<Node>();
			for (AccountingHeads childHead : mainHead.getHeads()) {
				if (childHead != null && childHead.getHeadname() != null && childHead.getName() != null) {
					Node childNode = new Node();
					childNode.setGroupname(childHead.getName());
					childNode.setHeadname(childHead.getHeadname());
					Amounts amts = new Amounts(0, 0, 0, 0);
					//amts.setPreviousyearclosingamt(0);
					childNode.setAmount(amts);
					childNode.setPath(childHead.getPath());
					// nodes.add(childNode);
					headNodes.put(childHead.getName(), childNode);

				}
			}
			mainHeadNodes.put(mainHead.getName(), node);
		}
	}

	public Map<String, Node> createHeads(Map<String, List<Node>> target) {
		initiate();
		target.forEach((key, eleNode) -> {
			Amounts source = new Amounts(0, 0, 0, 0);
			List<Node> childs = new ArrayList<Node>();
			Node node = null;
			for (Node ele : eleNode) {
				node = headNodes.get(ele.getHeadname());
				if (node != null) {
					childs.add(ele);
					addAmounts(source, ele.getAmount());
				}
			}
			if (node != null) {
				node.setAmount(source);
				if (childs.size() > 0) {
					node.setChildren(childs);
				}
				headNodes.put(key, node);
			}
		});

		headNodes.forEach((key, eleNode) -> {
			Node node = mainHeadNodes.get(eleNode.getHeadname());
			if (node != null) {
				List<Node> childs = node.getChildren();
				if (childs == null) {
					childs = new ArrayList<Node>();
				}
				childs.add(eleNode);
				if (childs.size() > 0) {
					node.setChildren(childs);
				}
				node.setAmount(addAmounts(node.getAmount(), eleNode.getAmount()));
				mainHeadNodes.put(eleNode.getHeadname(), node);
			}
		});

		return mainHeadNodes;
	}

	private Amounts addAmounts(Amounts target, Amounts source) {
		target.setOpeningamt(target.getOpeningamt() + source.getOpeningamt());
		target.setDebitamt(target.getDebitamt() + source.getDebitamt());
		target.setCreditamt(target.getCreditamt() + source.getCreditamt());
		target.setClosingamt(target.getClosingamt() + source.getClosingamt());
		
		target.setPreviousyearclosingamt(target.getPreviousyearclosingamt() + source.getPreviousyearclosingamt());

		return target;
	}
}
