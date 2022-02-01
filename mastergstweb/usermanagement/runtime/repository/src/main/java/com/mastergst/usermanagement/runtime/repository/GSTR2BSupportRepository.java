package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR2BSupport;

public interface GSTR2BSupportRepository extends MongoRepository<GSTR2BSupport, String> {

	public List<GSTR2BSupport> findByIdIn(List<String> ids);
	public List<GSTR2BSupport> findByClientidAndDocKey(String clientid, String docKey);
	public List<GSTR2BSupport> findByClientidAndFpAndInvtype(String clientId, String fp, String invType);
	public List<GSTR2BSupport> findByClientidAndFpInAndInvtype(String clientId, List<String> rtarray, String invType);
	public Page<GSTR2BSupport> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIsNull(String clientId, List<String> invTypes, Date ystDate, Date yendDate, Pageable pageable);
	public Page<GSTR2BSupport> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatus(String clientId, List<String> invTypes, Date ystDate, Date yendDate, String matchStatus, Pageable pageable);
	
	Page<GSTR2BSupport> findByClientidAndFpIn(final String clientid,List<String> fps, Pageable pageable);
	
	public Page<GSTR2BSupport> findByClientidAndInvtypeAndFpInAndInvoicenoIn(String clientid, String invType, List<String> fps, List<String> gstr2bInvoiceNoList, Pageable pageable);
	public Page<GSTR2BSupport> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIn(String clientid, List<String> invTypes, Date pstdate, Date penddate, List<String> gstr2status, Pageable pageable);
}
