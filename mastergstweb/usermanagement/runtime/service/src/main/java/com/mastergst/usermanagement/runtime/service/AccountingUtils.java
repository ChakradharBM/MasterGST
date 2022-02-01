package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.mastergst.configuration.service.CommonLedgerRepository;
import com.mastergst.configuration.service.Commonledger;
import com.mastergst.configuration.service.Groups;
import com.mastergst.configuration.service.GroupsRepository;
import com.mastergst.configuration.service.Subgroup;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.Subgroups;
import com.mastergst.usermanagement.runtime.repository.GroupDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;

@Component
public class AccountingUtils {

	@Autowired
	private ClientService clientService;
	@Autowired
	private LedgerRepository ledgerRepository;
	@Autowired
	private GroupsRepository groupsRepository;
	@Autowired
	private GroupDetailsRepository groupDetailsRepository;
	@Autowired
	private CommonLedgerRepository commonLedgerRepository;

	public void initiliazeGroupsAndLedgers(String clientid, String userid) {

		Client client = clientService.findById(clientid);
		if (isNotEmpty(client)) {
			if (isEmpty(client.getIsGroupExit()) || !client.getIsGroupExit()) {
				List<GroupDetails> groupdtls = Lists.newArrayList();
				List<Groups> defgrps = groupsRepository.findAll();
				for (Groups grps : defgrps) {
					GroupDetails grpdetails = new GroupDetails();
					grpdetails.setClientid(client.getId().toString());
					grpdetails.setGroupname(grps.getName());
					grpdetails.setHeadname(grps.getHeadname());
					grpdetails.setPath(grps.getPath());
					List<Subgroups> subgroup = Lists.newArrayList();
					if (isNotEmpty(grps.getSubgroup())) {
						for (Subgroup subgrp : grps.getSubgroup()) {
							if (isNotEmpty(subgrp) && isNotEmpty(subgrp.getSubgroupname())) {
								Subgroups subgrps = new Subgroups();
								subgrps.setGroupname(subgrp.getSubgroupname());
								subgrps.setHeadname(grpdetails.getGroupname());
								subgrps.setPath(grpdetails.getPath() + "/" + subgrp.getSubgroupname());
								subgrps.setGroupid(grpdetails.getId().toString());
								subgroup.add(subgrps);
							}
						}
					}
					grpdetails.setSubgroups(subgroup);
					groupdtls.add(grpdetails);
				}
				groupDetailsRepository.save(groupdtls);

				client.setIsGroupExit(true);
				clientService.saveClient(client);
			}

			if (isEmpty(client.getIsCommonLedgerExit()) || !client.getIsCommonLedgerExit()) {
				List<ProfileLedger> legerdtls = Lists.newArrayList();
				List<Commonledger> defledgers = commonLedgerRepository.findAll();

				for (Commonledger ldgr : defledgers) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(clientid);
					lgrdr.setLedgerName(ldgr.getLedgerName());
					lgrdr.setGrpsubgrpName(ldgr.getGrpsubgrpName());
					lgrdr.setLedgerpath(ldgr.getLedgerpath());
					legerdtls.add(lgrdr);
				}

				ledgerRepository.save(legerdtls);
				client.setIsCommonLedgerExit(true);
				clientService.saveClient(client);
			}
		}
	}
}
