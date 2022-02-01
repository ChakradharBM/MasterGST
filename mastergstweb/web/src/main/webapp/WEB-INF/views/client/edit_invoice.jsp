<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<c:set var="varPurchase" value="<%=MasterGSTConstants.PURCHASE_REGISTER%>"/>
<c:set var="varAdvance" value="<%=MasterGSTConstants.ADVANCES%>"/>
<c:set var="varATPAID" value="<%=MasterGSTConstants.ATPAID%>"/>
<c:set var="varCreditDebit" value="<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>"/>
<c:set var="varCDNUR" value="<%=MasterGSTConstants.CDNUR%>"/>
<c:set var="varExport" value="<%=MasterGSTConstants.EXPORTS%>"/>
<c:set var="varNIL" value="<%=MasterGSTConstants.NIL%>"/>
<c:set var="varB2BUR" value="<%=MasterGSTConstants.B2BUR%>"/>
<c:set var="varImpGoods" value="<%=MasterGSTConstants.IMP_GOODS%>"/>
<c:set var="varImpServices" value="<%=MasterGSTConstants.IMP_SERVICES%>"/>
<c:set var="varITCReversal" value="<%=MasterGSTConstants.ITC_REVERSAL%>"/>
<c:set var="varGSTR2A" value="<%=MasterGSTConstants.GSTR2A%>"/>
<title>MasterGST | View <c:choose>
<c:when test="${returntype eq varPurchase}">Purchase Invoice</c:when>
<c:when test="${invoice.invtype eq varAdvance}">Advance Receipt</c:when>
<c:when test="${invoice.invtype eq varATPAID}">Advance Tax Paid</c:when>
<c:when test="${invoice.invtype eq varExport}">Export</c:when>
<c:when test="${invoice.invtype eq varCreditDebit}">Credit/Debit Notes</c:when>
<c:when test="${invoice.invtype eq varCDNUR}">Credit/Debit Note for Unregistered</c:when>
<c:when test="${invoice.invtype eq varNIL}">Nil Supplies</c:when>
<c:when test="${invoice.invtype eq varB2BUR}">B2B Unregistered</c:when>
<c:when test="${invoice.invtype eq varImpGoods}">Import of Goods</c:when>
<c:when test="${invoice.invtype eq varImpServices}">Import of Services</c:when>
<c:when test="${invoice.invtype eq varITCReversal}">ITC Reversal</c:when>
<c:otherwise>Sales Invoice</c:otherwise>
</c:choose></title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
</head>

<body>
   <!-- header page begin -->
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>
    
		<!--- breadcrumb start -->
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
				<div class="breadcrumbwrap">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>"><c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${returntype}"/>?type=">${returntype}</a></li>
						<li class="breadcrumb-item active">View
							<c:choose>
							<c:when test="${returntype eq varPurchase}">Purchase Invoice</c:when>
							<c:when test="${invoice.invtype eq varAdvance}">Advance Receipt</c:when>
							<c:when test="${invoice.invtype eq varATPAID}">Advance Tax Paid</c:when>
							<c:when test="${invoice.invtype eq varExport}">Export</c:when>
							<c:when test="${invoice.invtype eq varCreditDebit}">Credit/Debit Notes</c:when>
							<c:when test="${invoice.invtype eq varNIL}">Nil Supplies</c:when>
							<c:when test="${invoice.invtype eq varCDNUR}">Credit/Debit Note for Unregistered</c:when>
							<c:when test="${invoice.invtype eq varB2BUR}">B2B Unregistered</c:when>
							<c:when test="${invoice.invtype eq varImpGoods}">Import of Goods</c:when>
							<c:when test="${invoice.invtype eq varImpServices}">Import of Services</c:when>
							<c:when test="${invoice.invtype eq varITCReversal}">ITC Reversal</c:when>
							<c:otherwise>Sales Invoice</c:otherwise>
							</c:choose></li>
					</ol>
					<span class="datetimetxt"> 
						<input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i>  
					</span>
					<span class="f-14-b pull-right mt-1 font-weight-bold">
						Return Period: 
					</span>
				</div>
			</div>
		</div>
	</div>

        <!--- breadcrumb end -->

        <!--- breadcrumb end -->
        <div class="db-ca-wrap">
            <div class="container">

                <!-- Dashboard body start -->
                <div class="row">
                    <!-- dashboard left block begin -->
                    <div class="col-md-12 col-sm-12">
                        <h2 class="hdrtitle-18">View
							<c:choose>
							<c:when test="${returntype eq varPurchase}">Purchase Invoice</c:when>
							<c:when test="${invoice.invtype eq varAdvance}">Advance Receipt</c:when>
							<c:when test="${invoice.invtype eq varATPAID}">Advance Tax Paid</c:when>
							<c:when test="${invoice.invtype eq varExport}">Export</c:when>
							<c:when test="${invoice.invtype eq varCreditDebit}">Credit/Debit Notes</c:when>
							<c:when test="${invoice.invtype eq varNIL}">Nil Supplies</c:when>
							<c:when test="${invoice.invtype eq varCDNUR}">Credit/Debit Note for Unregistered</c:when>
							<c:when test="${invoice.invtype eq varB2BUR}">B2B Unregistered</c:when>
							<c:when test="${invoice.invtype eq varImpGoods}">Import of Goods</c:when>
							<c:when test="${invoice.invtype eq varImpServices}">Import of Services</c:when>
							<c:when test="${invoice.invtype eq varITCReversal}">ITC Reversal</c:when>
							<c:otherwise>Sales Invoice</c:otherwise>
							</c:choose> <a href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:choose><c:when test="${returntype ne varGSTR2A}">${returntype}?type=inv</c:when><c:otherwise>${varGSTR2}?type=dwnld</c:otherwise></c:choose>" class="btn btn-secondary pl-5 pr-5 pull-right urllink mb-2">Back</a> <a href='${contextPath}/invprint/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${client.id}/${invoice.id}/${returntype}' class="btn btn-secondary pl-5 pr-5 pull-right urllink mb-2" target="_blank">Print</a> <c:if test="${returntype ne varGSTR2 && returntype ne varGSTR2A}"><a href="#" link="${contextPath}/editnsaveinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${invoice.id}"/>/${returntype}" class="btn btn-secondary pl-5 pr-5 pull-right mb-2 ml-2 urllink btn-blue-dark">Edit</a></c:if></h2>
						<c:if test="${not empty invoice.gstStatus && invoice.gstStatus ne 'SUCCESS'}"><span class="errormsg pull-left">${invoice.gstStatus}</span></c:if>
						<div class="clearfix"></div>
                        <div class="invoice-wrap">
                            <div class="invoice-wrap-in">
                                <form class="meterialform invoiceform">
                                    <!-- invoice header start -->
                                    <div class="invoice-hdr">
										<c:if test="${not empty ntty}">
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Document Type: </div>
                                                <div class="form-group">
                                            	<span class="f-12">${ntty}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Voucher Number</div>
                                                <div class="form-group">
                                               	<span class="f-12">${ntNum}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Voucher Date</div>
                                                <div class="form-group">
                                               	<span class="f-12"><fmt:formatDate value="${ntDt}" pattern="dd/MM/yyyy" /></span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Reason For Issue</div>
                                                <div class="form-group">
                                               	<span class="f-12">${rsn}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Is Pre-GST?</div>
                                                <div class="form-group">
                                               	<span class="f-12">${pGst}</span>
												</div>
											</div>
										</div>
										</c:if>
										<c:if test="${not empty invoice.cdnur[0].ntty}">
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Document Type: </div>
                                                <div class="form-group">
                                            	<span class="f-12">${invoice.cdnur[0].ntty}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Voucher Number</div>
                                                <div class="form-group">
                                               	<span class="f-12">${invoice.cdnur[0].ntNum}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Voucher Date</div>
                                                <div class="form-group">
                                               	<span class="f-12"><fmt:formatDate value="${invoice.cdnur[0].ntDt}" pattern="dd/MM/yyyy" /></span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Reason For Issue</div>
                                                <div class="form-group">
                                               	<span class="f-12">${invoice.cdnur[0].rsn}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Is Pre-GST?</div>
                                                <div class="form-group">
                                               	<span class="f-12">${invoice.cdnur[0].pGst}</span>
												</div>
											</div>
										</div>
										</c:if>
										<c:if test="${invoice.invtype eq varITCReversal}">
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Type: </div>
                                                <div class="form-group">
                                            	<span class="f-12">Rule 2(2)</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Integrated Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule22.iamt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Central Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule22.camt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">State/UT Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule22.samt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Cess </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule22.csamt}</span>
												</div>
											</div>
										</div>
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Type: </div>
                                                <div class="form-group">
                                            	<span class="f-12">Rule 7(1)(m)</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Integrated Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule71m.iamt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Central Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule71m.camt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">State/UT Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule71m.samt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Cess </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule71m.csamt}</span>
												</div>
											</div>
										</div>
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Type: </div>
                                                <div class="form-group">
                                            	<span class="f-12">Rule 8(1)(h)</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Integrated Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule81h.iamt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Central Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule81h.camt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">State/UT Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule81h.samt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Cess </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule81h.csamt}</span>
												</div>
											</div>
										</div>
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Type: </div>
                                                <div class="form-group">
                                            	<span class="f-12">Rule 7(2)(a)</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Integrated Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule72a.iamt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Central Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule72a.camt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">State/UT Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule72a.samt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Cess </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule72a.csamt}</span>
												</div>
											</div>
										</div>
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Type: </div>
                                                <div class="form-group">
                                            	<span class="f-12">Rule 7(2)(b)</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Integrated Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule72b.iamt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Central Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule72b.camt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">State/UT Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule72b.samt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Cess </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.rule72b.csamt}</span>
												</div>
											</div>
										</div>
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Type: </div>
                                                <div class="form-group">
                                            	<span class="f-12">Reversal ITC</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Integrated Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.revitc.iamt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Central Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.revitc.camt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">State/UT Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.revitc.samt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Cess </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.revitc.csamt}</span>
												</div>
											</div>
										</div>
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Type: </div>
                                                <div class="form-group">
                                            	<span class="f-12">Other</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Integrated Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.other.iamt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Central Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.other.camt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">State/UT Tax </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.other.samt}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Cess </div>
                                                <div class="form-group">
                                            	<span class="f-12">${itcrvsl.other.csamt}</span>
												</div>
											</div>
										</div>
										</c:if>
										<c:if test="${not empty invoice.exp[0].expTyp}">
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Export Type: </div>
                                                <div class="form-group">
                                               	<span class="f-12">${invoice.exp[0].expTyp}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Port Code</div>
                                                <div class="form-group">
                                               	<span class="f-12">${invoice.exp[0].inv[0].sbpcode}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Shipping Bill Number</div>
                                                <div class="form-group">
                                               	<span class="f-12">${invoice.exp[0].inv[0].sbnum}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Shipping Bill Date</div>
                                                <div class="form-group">
                                               	<span class="f-12"><fmt:formatDate value="${invoice.exp[0].inv[0].sbdt}" pattern="dd/MM/yyyy" /></span>
												</div>
											</div>
										</div>
										</c:if>
										<c:if test="${not empty impg}">
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Is SEZ? </div>
                                                <div class="form-group">
                                               	<span class="f-12">${impg[0].isSez}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">SEZ GSTIN/Unique ID</div>
                                                <div class="form-group">
                                               	<span class="f-12">${impg[0].stin}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Port Code</div>
                                                <div class="form-group">
                                               	<span class="f-12">${impg[0].portCode}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Bill Number</div>
                                                <div class="form-group">
                                               	<span class="f-12">${impg[0].boeNum}</span>
												</div>
											</div>
											<div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Bill Date</div>
                                                <div class="form-group">
                                               	<span class="f-12"><fmt:formatDate value="${impg[0].boeDt}" pattern="dd/MM/yyyy" /></span>
												</div>
											</div>
										</div>
										</c:if>
										<c:if test="${not empty splyTy}">
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Supply Type: </div>
                                                <div class="form-group">
                                               	<span class="f-12">${splyTy}</span>
												</div>
											</div>
										</div>
										</c:if>
										<c:if test="${not empty invoice.txpd[0].splyTy}">
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Supply Type: </div>
                                                <div class="form-group">
                                               	<span class="f-12">${invoice.txpd[0].splyTy}</span>
												</div>
											</div>
										</div>
										</c:if>
                                        <c:if test="${not empty b2bur && not empty b2bur[0].inv}">
										<div class="row">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Supply Type: </div>
                                                <div class="form-group">
                                               	<span class="f-12">${b2bur[0].inv[0].splyType}</span>
												</div>
											</div>
										</div>
										</c:if>
                                        <div class="row">
											<c:if test="${not empty invoice.billedtoname}">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Customer Name</div>
                                                <div class="form-group">
                                               	<span class="f-12">${invoice.billedtoname}</span>
																							  </div>
                                            </div>
											</c:if>
                                            <c:if test="${not empty invoice.b2b[0].ctin}">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">GSTIN/Unique ID </div>
                                                <span class="f-12">${invoice.b2b[0].ctin}</span>
                                            </div>
											</c:if>
											<c:if test="${not empty invoice.cdnur[0].typ}">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Credit/Debit for Unregistered Type</div>
                                                <span class="f-12">${invoice.cdnur[0].typ}</span>
                                            </div>
											</c:if>
                                            <c:if test="${not empty invoice.b2b[0].inv[0].inum}">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Invoice Number </div>
                                                <span class="f-12">${invoice.b2b[0].inv[0].inum}</span>
                                            </div>
                                            </c:if>
                                            <c:if test="${not empty invoice.dateofinvoice}">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Invoice Date </div>
                                                <span class="f-12"><fmt:formatDate value="${invoice.dateofinvoice}" pattern="dd/MM/yyyy" /></span>
                                            </div>
                                            </c:if>
                                            <c:if test="${not empty invoice.statename}">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Place Of Supply</div>
                                                <span class="f-12">${invoice.statename}</span>
                                            </div>
                                            </c:if>
                                            <c:if test="${not empty invoice.b2b[0].inv[0].address}">
                                            <div class="col-md-2 col-sm-12">
                                                <div class="lable-txt">Billing Address</div>
                                                <span class="f-12">${invoice.b2b[0].inv[0].address}</span>
                                            </div>
											</c:if>

                                            <!-- invoice header end -->


                                            <c:if test="${not empty invoice.isbilledto}">
                                            <!-- <h4 class="hdrtitle-18">Details of Receiver (Billed to)</h4>-->
                                            <div class="col-md-4 col-sm-12 billed mt-3">
                                                <div class="lable-txt">Is the Billed &amp; Shipped is the same Address?</div>

                                                <div class="form-radio inline">
                                                    <div class="radio">
                                                        <label>
															<c:choose><c:when test="${not empty invoice.isbilledto}">${invoice.isbilledto}</c:when><c:otherwise>Yes</c:otherwise></c:choose></label>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="col-md-8 col-sm-12  mt-3">
                                                <div class="billedbox billedno" style="display: block;">


                                                    <div class="row">

                                                        <div class="col-md-4 col-sm-12">
                                                            <div class="lable-txt">Customer Name</div>
                                                            <span class="f-12">${invoice.consigneename}</span>
                                                        </div>

                                                        <div class="col-md-4 col-sm-12">
                                                            <div class="lable-txt">State</div>
                                                            <span class="f-12">${invoice.consigneepos}</span>
                                                        </div>

                                                        <div class="col-md-4 col-sm-12">
                                                            <div class="lable-txt">Shipping Address</div>
                                                            <span class="f-12">${invoice.consigneeaddress}</span>
                                                        </div>
                                                    </div>

                                                </div>
                                            </div>
											</c:if>
                                            <!-- start -->

                                        </div>
                                    </div>
                                    <!-- end -->

                                    <div class="mb-3 mt-2"></div>
                                    <!-- item details start -->
                                    <!-- itemdetailsblock-wrap start -->
                                    <div class="itemdetailsblock-wrap">
                                         
                                         
                                        <!-- table start -->

					<div class="clearfix">
						<h4 class="f-18-b pull-left">Item Details</h4>
					</div>
					<div class="clearfix"></div>
					<div class="customtable invoicetable">
						<div id="invoicetable_wrapper" class="dataTables_wrapper">
							<table id="invoicetable" class="display row-border dataTable meterialform" role="grid" aria-describedby="invoicetable_info" style="width: 100%;" cellspacing="0" width="100%">
								<thead>
									<c:if test="${invoice.invtype ne varNIL}">
									<tr role="row">
										<th rowspan="2" class="sorting_disabled" colspan="1" style="width: 40px;"> Name</th>
										<th rowspan="2" class="sorting_disabled" colspan="1" style="width: 66px;">Category</th>
										<th rowspan="2" class="sorting_disabled" colspan="1" style="width: 69px;">HSN/ SAC</th>
										<th rowspan="2" class="sorting_disabled" colspan="1" style="width: 90px;">Taxable Value</th>
										<c:choose>
										<c:when test="${returntype eq varPurchase}">
										<th colspan="3" class="text-center" rowspan="1">IGST </th>
										<th colspan="3" class="text-center" rowspan="1">CGST </th>
										<th colspan="3" class="text-center" rowspan="1">SGST </th>
										<th colspan="3" class="text-center" rowspan="1">CESS </th>
										</c:when>
										<c:otherwise>
										<th colspan="2" class="text-center" rowspan="1">IGST </th>
										<th colspan="2" class="text-center" rowspan="1">CGST </th>
										<th colspan="2" class="text-center" rowspan="1">SGST </th>
										<th colspan="2" class="text-center" rowspan="1">CESS </th>
										</c:otherwise>
										</c:choose>
										<th rowspan="2" class="text-right sorting_disabled" colspan="1" style="width: 82px;">Total</th>
										<c:if test="${invoice.invtype eq varAdvance || invoice.invtype eq varATPAID}">
										<th rowspan="2" class="text-right sorting_disabled" colspan="1" style="width: 82px;">Advance Received</th>
										</c:if>
									</tr>
									<tr role="row">
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 36px;">Rate</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">Amount</th>
										<c:if test="${returntype eq varPurchase}">
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">ITC Value</th>
										</c:if>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 36px;">Rate</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">Amount</th>
										<c:if test="${returntype eq varPurchase}">
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">ITC Value</th>
										</c:if>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 36px;">Rate</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">Amount</th>
										<c:if test="${returntype eq varPurchase}">
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">ITC Value</th>
										</c:if>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 36px;">Rate</th>
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">Amount</th>
										<c:if test="${returntype eq varPurchase}">
										<th class="sorting_disabled" rowspan="1" colspan="1" style="width: 59px;">ITC Value</th>
										</c:if>
									</tr>
									</c:if>
									<c:if test="${invoice.invtype eq varNIL}">
									<tr>
									  <th class="sorting_disabled" rowspan="1" colspan="1">Type</th>
									  <th class="sorting_disabled" rowspan="1" colspan="1">Nil Rated</th>
									  <th class="sorting_disabled" rowspan="1" colspan="1">Exempted</th>
									  <th class="sorting_disabled" rowspan="1" colspan="1">Non GST</th>
									</tr>
									</c:if>
								</thead>
								
								<tfoot>
									<c:if test="${invoice.invtype ne varNIL}">
									<tr>
									  <th colspan="3" style="text-align:right">Total Invoice Value:</th>
									  <th></th>
									  <c:choose>
									  <c:when test="${returntype eq varPurchase || returntype eq varPurchase}">
									  <th colspan="3" style="text-align:right"></th>
									  <th colspan="3" style="text-align:right"></th>
									  <th colspan="3" style="text-align:right"></th>
									  <th colspan="3" style="text-align:right"></th>
									  </c:when>
									  <c:otherwise>
									  <th colspan="2" style="text-align:right"></th>
									  <th colspan="2" style="text-align:right"></th>
									  <th colspan="2" style="text-align:right"></th>
									  <th colspan="2" style="text-align:right"></th>
									  </c:otherwise>
									  </c:choose>
									  <th></th>
									  <c:if test="${invoice.invtype eq varAdvance || invoice.invtype eq varATPAID}"><th></th></c:if>
									</tr>
									<tr>
									  <th colspan="3" style="text-align:right"></th>
									  <th></th>
									  <c:choose>
									  <c:when test="${returntype eq varPurchase || returntype eq varPurchase}">
									  <th colspan="3" style="text-align:right"></th>
									  <th colspan="3" style="text-align:right"></th>
									  <th colspan="3" style="text-align:right"></th>
									  <th colspan="3" style="text-align:right"></th>
									  </c:when>
									  <c:otherwise>
									  <th colspan="2" style="text-align:right"></th>
									  <th colspan="2" style="text-align:right"></th>
									  <th colspan="2" style="text-align:right"></th>
									  <th colspan="2" style="text-align:right"></th>
									  </c:otherwise>
									  </c:choose>
									  <th></th>
									  <c:if test="${invoice.invtype eq varAdvance || invoice.invtype eq varATPAID}"><th></th></c:if>
									</tr>
									</c:if>
								</tfoot>
								<tbody>
									<c:forEach items="${invoice.items}" var="item">
									<tr role="row">
										<c:if test="${invoice.invtype ne varNIL}">
										<td data-th="Name">${item.itemno}</td>
										<td data-th="Category">${item.category}</td>
										<td data-th="HSN/ SAC"><c:choose><c:when test="${fn:contains(item.hsn, ' :')}">${fn:substring(item.hsn, 0, fn:indexOf(item.hsn, " :"))}</c:when><c:otherwise>${item.hsn}</c:otherwise></c:choose></td>
										<td class="text-right" data-th="Taxble Value"><fmt:formatNumber type = "number" minFractionDigits="2" maxFractionDigits="2" value = "${item.taxablevalue}" /></td>
										<td class="text-right" data-th="IGST ">${item.igstrate}</td>
										<td class="text-right" data-th="CGST "><fmt:formatNumber type = "number" minFractionDigits="2" maxFractionDigits="2" value = "${item.igstamount}" /></td>
										<c:if test="${returntype eq varPurchase || returntype eq varPurchase}">
										<td class="text-right" data-th="CGST ">${item.igstavltax}</td>
										</c:if>
										<td class="text-right" data-th="SGST ">${item.cgstrate}</td>
										<td class="text-right" data-th="CESS "><fmt:formatNumber type = "number" minFractionDigits="2" maxFractionDigits="2" value = "${item.cgstamount}" /></td>
										<c:if test="${returntype eq varPurchase || returntype eq varPurchase}">
										<td class="text-right" data-th="CGST ">${item.cgstavltax}</td>
										</c:if>
										<td class="text-right" data-th="Total">${item.sgstrate}</td>
										<td class="text-right" data-th="Rate"><fmt:formatNumber type = "number" minFractionDigits="2" maxFractionDigits="2" value = "${item.sgstamount}" /></td>
										<c:if test="${returntype eq varPurchase || returntype eq varPurchase}">
										<td class="text-right" data-th="CGST ">${item.sgstavltax}</td>
										</c:if>
										<td class="text-right" data-th="Amount">${item.cessrate}</td>
										<td class="text-right" data-th="Rate"><fmt:formatNumber type = "number" minFractionDigits="2" maxFractionDigits="2" value = "${item.cessamount}" /></td>
										<c:if test="${returntype eq varPurchase || returntype eq varPurchase}">
										<td class="text-right" data-th="CGST ">${item.cessavltax}</td>
										</c:if>
										<td class="text-right" data-th="Amount">${item.total}</td>
										<c:if test="${invoice.invtype eq varAdvance || invoice.invtype eq varATPAID}">
										<td class="text-right" data-th="AdvReceived">${item.advreceived}</td>
										</c:if>
										</c:if>
										<c:if test="${invoice.invtype eq varNIL}">
										<td class="text-right" data-th="Type">${item.splyType}</td>
										<td class="text-right" data-th="Nil">${item.nilAmt}</td>
										<td class="text-right" data-th="Exempted">${item.exptAmt}</td>
										<td class="text-right" data-th="NonGST">${item.ngsupAmt}</td>
										</c:if>
									</tr>
									</c:forEach>
								</table>
											</div>
                                        </div>

                                        <!-- table end -->
										<div class="row mb-4">
											<div class="form-group col-6 pr-0">
												<span class="f-16-b bluetxt mr-3"><strong>Invoice Type :</strong></span>
												<div class="form-radio inline">
													<div class="radio mb-0">
														<label>
															<c:choose><c:when test="${not empty invoice.revchargetype}">${invoice.revchargetype}</c:when><c:otherwise>Regular</c:otherwise></c:choose></label>
													</div>
												</div>
											</div>
											<div class="form-group col-6 pl-0" style="display:none" id="idEcommerce">
												<div class="form-group col-6 pl-0">
													<div class="lable-txt">Ecommerce Operator GSTIN</div>
													<span class="errormsg" id="ecommercegstn_Msg"></span>
													<input type="text" id="ecommercegstn" name="b2b[0].inv[0].etin" aria-describedby="ecommercegstn" pattern="^[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" placeholder="07CQZCD1111I4Z7" placeholder="Ecommerce Operatior GSTIN" value="${invoice.b2b[0].inv[0].etin}">
													<label for="ecommercegstn" class="control-label"></label>
													<div class="help-block with-errors"></div>
													<i class="bar"></i> </div>
												
												<div class="form-group col-6 pl-0">
													<div class="lable-txt">Operatior Id</div>
													<span class="errormsg" id="ecommercegstn_Msg"></span>
													<input type="text" id="ecomoperatorid" name="ecomoperatorid" aria-describedby="ecomoperatorid" placeholder="Operator Id" placeholder="Ecommerce Operatior Id" value="${invoice.ecomoperatorid}">
													<label for="ecomoperatorid" class="control-label"></label>
													<div class="help-block with-errors"></div>
													<i class="bar"></i> </div>
											</div>
										</div>
										<c:if test="${not empty invoice.b2b[0].inv[0].invTyp}">
										<div class="form-group col-6 pl-0" id="idRegularType">
											<div class="form-group col-6 pl-0">
												<span class="f-16-b bluetxt mr-3"><strong>Type :</strong></span>
												<c:choose>
													<c:when test="${invoice.b2b[0].inv[0].invTyp eq 'R'}">None</c:when>
													<c:when test="${invoice.b2b[0].inv[0].invTyp eq 'DE'}">Deemed Exports</c:when>
													<c:otherwise>${invoice.b2b[0].inv[0].invTyp}</c:otherwise></c:choose>
											</div>
										</div>
										</c:if>

                                    </div>
                                    <!-- itemdetailsblock-wrap end -->
 
                                    <div class="col-sm-12  text-center">
										<c:if test="${returntype ne varGSTR2 && returntype ne varGSTR2A}"><a href="#" link="${contextPath}/editnsaveinv/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${invoice.id}"/>/${returntype}" class="btn btn-secondary pl-5 pr-5 urllink btn-blue-dark">Edit</a></c:if>
										<a href="#" link="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:choose><c:when test="${returntype ne varGSTR2A}">${returntype}?type=inv</c:when><c:otherwise>${varGSTR2}?type=dwnld</c:otherwise></c:choose>" class="btn btn-secondary pl-5 pr-5 urllink">Back</a>
                                    </div>
                                </form>
                            </div>
                        </div>
                        <!-- inovice wrap end -->
                    </div>

                    <!-- dashboard left block end -->


                </div>

                <!-- Dashboard body end -->
            </div>
        </div>
        <!-- db-ca-wrap end -->

       <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
<script type="text/javascript">
$(function () {
	$('#nav-client').addClass('active');
	$('[data-toggle="tooltip"]').tooltip();
	var billedStatus = '<c:out value="${invoice.isbilledto}"/>';
	if(billedStatus != '') {
		if(billedStatus == 'Yes') {
			$('.billedbox').hide();
		}
	}
	var invTypeStatus = '<c:out value="${invoice.revchargetype}"/>';
	if(invTypeStatus != '') {
		if(invTypeStatus == 'TCS') {
			$('#idEcommerce').show();
		}
	}
	var table = $('table.display').DataTable({
		dom: 'Bfrtip',
		"searching": false,
		"pageLength": 5,
		"language": {
			"paginate": {
				"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
				"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
			}
		},
		"footerCallback": function (row, data, start, end, display) {
            var api = this.api(), data;
            var colNumber = [3, 5, 7, 9, 11, 12];
 
            var intVal = function (i) {
                return typeof i === 'string' ?
                        i.replace(/(\.\d{2})/g, "") * 1 :
                        typeof i === 'number' ?
                        i : 0;
            };
			var totalValue=0;
            for (i = 0; i < colNumber.length; i++) {
                var colNo = colNumber[i];
                var total2 = api
                        .column(colNo,{ page: 'current'})
                        .data()
                        .reduce(function (a, b) {
                            return intVal(a) + intVal(b);
                        }, 0);
				if(total2) {
					$(api.column(colNo).footer()).html(total2);
					if(invTypeStatus == 'Regular') {
						$('tr:eq(1) th', api.table().footer()).html('');
					} else if(invTypeStatus == 'Reverse') {
						$('tr:eq(1) th:eq(0)', api.table().footer()).html('Tax under Reverse Charge:');
						if(colNo == 5) {
							$('tr:eq(1) th:eq(2)', api.table().footer()).html(total2);
						} else if(colNo == 7) {
							$('tr:eq(1) th:eq(3)', api.table().footer()).html(total2);
						} else if(colNo == 9) {
							$('tr:eq(1) th:eq(4)', api.table().footer()).html(total2);
						} else if(colNo == 11) {
							$('tr:eq(1) th:eq(5)', api.table().footer()).html(total2);
						} else if(colNo == 12) {
							$('tr:eq(1) th:eq(6)', api.table().footer()).html(total2);
						}
					} else if(invTypeStatus == 'TCS') {
						$('tr:eq(1) th:eq(0)', api.table().footer()).html('TCS:');
						var invValue = parseFloat($(api.column(3).footer()).html())/100;
						if(colNo == 5) {
							totalValue += invValue;
							$('tr:eq(1) th:eq(2)', api.table().footer()).html(invValue);
						} else if(colNo == 7) {
							totalValue += invValue;
							$('tr:eq(1) th:eq(3)', api.table().footer()).html(invValue);
						} else if(colNo == 9) {
							totalValue += invValue;
							$('tr:eq(1) th:eq(4)', api.table().footer()).html(invValue);
						} else if(colNo == 11) {
							totalValue += invValue;
							$('tr:eq(1) th:eq(5)', api.table().footer()).html(invValue);
						} else if(colNo == 12) {
							$('tr:eq(1) th:eq(6)', api.table().footer()).html(totalValue);
						}
					} else if(invTypeStatus == 'TDS') {
						$('tr:eq(1) th:eq(0)', api.table().footer()).html('TDS:');
						var invValue = parseFloat($(api.column(3).footer()).html())/100;
						if(colNo == 5) {
							totalValue += invValue;
							$('tr:eq(1) th:eq(2)', api.table().footer()).html(invValue);
						} else if(colNo == 7) {
							totalValue += invValue;
							$('tr:eq(1) th:eq(3)', api.table().footer()).html(invValue);
						} else if(colNo == 9) {
							totalValue += invValue;
							$('tr:eq(1) th:eq(4)', api.table().footer()).html(invValue);
						} else if(colNo == 11) {
							totalValue += invValue;
							$('tr:eq(1) th:eq(5)', api.table().footer()).html(invValue);
						} else if(colNo == 12) {
							$('tr:eq(1) th:eq(6)', api.table().footer()).html(totalValue);
						}
					}
				}
            }
        }
	});

	var headertext = [],
		headers = document.querySelectorAll("table.display th"),
		tablerows = document.querySelectorAll("table.display th"),
		tablebody = document.querySelector("table.display tbody");

	for (var i = 0; i < headers.length; i++) {
		var current = headers[i];
		headertext.push(current.textContent.replace(/\r?\n|\r/, ""));
	}
	for (var i = 0, row; row = tablebody.rows[i]; i++) {
		for (var j = 0, col; col = row.cells[j]; j++) {
			col.setAttribute("data-th", headertext[j]);
		}
	}
});
</script>
</body>

 </html> 