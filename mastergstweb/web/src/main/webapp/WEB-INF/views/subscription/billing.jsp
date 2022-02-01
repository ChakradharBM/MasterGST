<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Billing</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<style>
#billing-subscription #dbTable_filter ,#billing-subscription #dbTable_info ,#billing-subscription #dbTable_paginate{display:none}
.btn-billdetails{color: #374583!important;cursor: pointer;padding: 8px 19px!important;font-size: 14px;}
.billing-sub tbody tr td:first-child{border-left:1px solid lightgray;}
.billing-sub tbody tr td:last-child{border-right:1px solid lightgray;}
body{min-height:90%;padding-bottom:0rem!important;}
.container-fluid.billing{width:91%;}
</style>

</head>
<body>
	<c:set var="varASP" value="<%=MasterGSTConstants.ASPDEVELOPER%>" />
	<c:set var="varSuvidha" value="<%=MasterGSTConstants.SUVIDHA_CENTERS%>" />
	<c:if test="${usertype ne varASP}">
		<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
	</c:if>

	<c:if test="${usertype eq varASP}">
		<%@include file="/WEB-INF/views/includes/app_header.jsp"%>
	</c:if>
	<div class="bodywrap">

		<c:if test="${usertype eq varASP}">
			<!-- Header tag only for asp start -->
			<div class="bodybreadcrumb" id="breadcrumb" style="display: block">
				<div class="container">
					<div class="row">
						<div class="col-sm-12">
							<div class="navbar-left">
								<ul>
									<li>Subscriptions</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- Header tag only for asp end -->
		</c:if>

		<!--- breadcrumb start -->
		<div class="breadcrumbwrap main">
			<div class="container-fluid">
			<div class="container">
				<div class="row">
					<div class="col-md-12 col-sm-12">
						<ol class="breadcrumb">
							<li class="breadcrumb-item active">Billing</li>
						</ol>
						<span id="errorMessage" class="ml-4"
							style="color: red; font-size: 12px; font-weight: bold;"></span> <span
							class="datetimetxt"> <!-- <input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i>  -->
						</span> <span class="f-14-b pull-right mt-1 font-weight-bold"> <!-- Return Period: -->
						</span>
						<div class="retresp"></div>
					</div>
				</div>
				</div>
			</div>
		</div>
	</div>
	<!--- breadcrumb end -->
	<c:if test="${usertype eq varASP}">
		<div class="bodywrap billing-body"
			style="margin-top: 43px !important; padding-top: 110px !important;">
			<div class="db-inner">
				<!-- begin content  -->
				<div class="container db-inner-txt">
					<h3>
						My Subscriptions
						<c:if test="${usertype ne varASP && usertype ne varSuvidha}">
							<a
								href='${contextPath}/sbscrpln/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${pType}/${month}/${year}'
								class="btn btn-sm btn-blue pull-right">Upgrade Subscription</a>
						</c:if>
					</h3>
					<div id="billing-subscription">
						<table id="dbTable"
							class="display row-border dataTable meterialform billing-sub"
							cellspacing="0" width="100%">
							<thead>
								<tr style="background-color: #5769bb; color: white;">
									<th>Subscription Type</th>
									<th class="text-center">Start Date</th>
									<th class="text-center">Expiry Date</th>
									<th class="text-center">Amount</th>
									<th class="text-center">Status</th>
									<th class="text-center">Action</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${subscriptionDetailsList}" var="subscriptionList">
								
									<tr style="font-size: 14px;">
										<td class="rm-sort">
											<c:if test="${subscriptionList.apiType eq 'GSTAPI'}"> GST Production API</c:if><c:if test="${subscriptionList.apiType eq 'EWAYBILLSANDBOXAPI'}"> EWAYBILL Sandbox API</c:if><c:if test="${subscriptionList.apiType eq 'EWAYAPI'}"> EWAYBILL Production API</c:if><c:if test="${subscriptionList.apiType eq 'GSTSANDBOXAPI'}"> GST Sandbox API</c:if>
											<c:if test="${subscriptionList.apiType eq 'E-INVOICESANDBOXAPI'}"> E-Invoice Sandbox API</c:if>
											<c:if test="${subscriptionList.apiType eq 'E-INVOICEAPI'}"> E-Invoice API</c:if>
										</td>
										<td class="text-center"><fmt:formatDate	value="${subscriptionList.registeredDate}" pattern="dd/MM/yyyy" /></td>
										<td class="text-center">
											<c:choose>
												<c:when test="${subscriptionList.subscriptionType eq 'UNLIMITED'}">
													UNLIMITED
												</c:when>
												<c:otherwise>
													<fmt:formatDate	value="${subscriptionList.expiryDate}" pattern="dd/MM/yyyy" />
												</c:otherwise>
											</c:choose>
										</td>
										<td class="text-center"><c:out	value="${subscriptionList.paidAmount}" /></td>
										<td class="text-center" <c:if test="${subscriptionList.subscriptionStatus eq 'Expired'}">style="color:red"</c:if> <c:if test="${subscriptionList.subscriptionStatus eq 'Active'}">style="color:green"</c:if> ><strong><c:out value="${subscriptionList.subscriptionStatus}"/></strong></td>
										<td class="text-center"><a href="${contextPath}/dbilling/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${subscriptionList.apiType}"/>/<%=MasterGSTConstants.ASPDEVELOPER%>/<fmt:formatDate value="${now}" pattern="MM" />/<fmt:formatDate value="${now}" pattern="yyyy"/>/<c:out value="${type}"/>"	class="btn btn-blue btn-billdetails urllink">Details</a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>

				</div>
			</div>
		</div>
	</c:if>

	<c:if test="${usertype ne varASP}">
	<div class="bodywrap"
			style="margin-top: 43px !important; padding-top: 113px !important;">
			<div class="db-inner">
				<div class="container db-inner-txt">
	<c:choose>
	<c:when test="${msgdata eq 'yes'}">
	 <div class="row">
          <!-- login form begin -->
          <div class="formboxwrap">
           <h1 style="font-size: 1.5rem;text-align:center;color:#8a8b8b;"> India's Best GST Software, We Simplify GST Return Filing </h1>
            <h5>TRUSTED BY MOST CA's AND COMPANIES NATIONALLY</h5>
			<div class="col-md-2 col-sm-12 m-auto"></div>
            <div class="col-md-8 col-sm-12 m-auto">
              <div class="formbox">
                <form class="meterialform">
                     <div class="whitebg">
                        <h2>Welcome Message</h2>
                           <!-- serverside error begin -->                    
                    <!-- serverside error end --> 
                        <div class="col-xs-12 p-4">
                           <p class="alert sucess-txt" style="text-align:justify;">Thank you  for your interest in MasterGST GST Software,<br/> For Subscription Please contact our Sales team.<br/>You can reach us at <a href="mailto:info@mastergst.com">sales@mastergst.com</a> or call us @+91-7901022478 | 040-48531992. </p>
                        </div>
                     </div>
                  </form>
              </div>
            </div>
            <div class="col-md-4 col-sm-12 m-auto"></div>
		  </div>
          <!-- login form end -->
        </div>
	</c:when>
	<c:otherwise>
				<!-- begin content  -->
					<div>
						<div class="credentialwrap">
							<div class="row">
								<div class="col-md-12 col-sm-12">
									<div class="search_details">
										<div class="subscript_details_box">
											<h3>
												My Subscription Details
													<c:if test="${paymentLink eq 'yes'}">
														<a href='${contextPath}/sbscrrvwk/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${pType}/${month}/${year}' class="btn btn-sm btn-blue pull-right">Pay Now</a>
													</c:if>
												<!--<c:if test="${usertype ne varASP && usertype ne varSuvidha}">
													<a href='${contextPath}/sbscrpln/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${pType}/${month}/${year}' class="btn btn-sm btn-blue pull-right">Upgrade	Subscription</a>
												</c:if>-->
											</h3>
											<div class="">&nbsp;</div>
											<div class="table_custom row">
												<div class="table_row col-md-6 col-sm-12 nopad_l">
													<div class="table_fild">My Registration Date :</div>
													<div class="table_txt"><fmt:formatDate value="${userregisterdate}" pattern="dd/MM/yyyy" /></div>
												</div>
												<div class="table_row col-md-6 col-sm-12 nopad_r">
													<div class="table_fild">Subscription Amount :</div>
													<div class="table_txt">
														<i class="fa fa-rupee"></i><c:choose><c:when test="${subscription.paidAmount != 0.00}">${subscription.paidAmount}</c:when><c:otherwise>${plan.price}</c:otherwise></c:choose> 
													</div>
												</div>
												<div class="table_row col-md-6 col-sm-12 nopad_l">
													<div class="table_fild">Product Type :</div>
													<div class="table_txt">${plan.name}</div>
												</div>
												<div class="table_row col-md-6 col-sm-12 nopad_l">
													<div class="table_fild">Subscription Type :</div>
													<div class="table_txt">${plan.duration}Months</div>
												</div>
												<div class="table_row col-md-6 col-sm-12 nopad_r">
													<div class="table_fild">Renewal Type :</div>
													<div class="table_txt">${plan.name}</div>
												</div>
												<div class="table_row col-md-6 col-sm-12 nopad_l">
													<div class="table_fild">Subscription Start Date :</div>
													<div class="table_txt">
														<fmt:formatDate value="${subscription.registeredDate}"
															pattern="dd/MM/yyyy" />
													</div>
												</div>
												<div class="table_row col-md-6 col-sm-12 nopad_r">
													<div class="table_fild">Renewal Date :</div>
													<div class="table_txt">
														<fmt:formatDate value="${subscription.expiryDate}"
															pattern="dd/MM/yyyy" />
													</div>
												</div>
												<div class="table_row col-md-6 col-sm-12 nopad_l">
													<div class="table_fild">Subscription End Date :</div>
													<div class="table_txt">
														<fmt:formatDate value="${subscription.expiryDate}"
															pattern="dd/MM/yyyy" />
													</div>
												</div>
												<div class="table_row col-md-6 col-sm-12 nopad_r">
													<div class="table_fild">Permitted Invoices :</div>
													<div class="table_txt">${subscription.allowedInvoices}</div>
												</div>
												<div class="table_row col-md-6 col-sm-12 nopad_l">
													<div class="table_fild">Remaining Invoices :</div>
													<div class="table_txt">${subscription.allowedInvoices-subscription.processedInvoices}
														<jsp:useBean id="today" class="java.util.Date" />
														<c:if
															test="${usertype ne varASP && usertype ne varSuvidha && today.time lt subscription.expiryDate.time}">
															<c:if
																test="${(subscription.allowedInvoices-subscription.processedInvoices) < 1000}">
																<a
																	href='${contextPath}/sbscrpln/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/AddOn/${month}/${year}'
																	class="btn btn-sm btn-blue pull-right">Buy More
																	Invoices</a>
															</c:if>
														</c:if>
													</div>
												</div>
											</div>
										</div>
									</div>
									<!--   profile table begin -->
									<div class="customtable">
										<div class="customtable-hdr">
											<h6>My Credentials List</h6>
										</div>
										<table id="dbTable"
											class="display row-border dataTable meterialform"
											cellspacing="0" width="100%">
											<thead>
												<tr>
													<th>Order ID</th>
													<th>Payment Date</th>
													<th>Status</th>
													<th>Transaction ID</th>
													<th>Paid Amount ( <i class="fa fa-rupee"></i> )
													</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${payments}" var="payment">
													<tr>
														<td><c:if test="${payment.orderId eq null}">${payment.id}</c:if>
															<c:if test="${payment.orderId ne null}">${payment.orderId}</c:if>
														</td>
														<td><fmt:formatDate value="${payment.paymentDate}"
																pattern="dd/MM/yyyy" /></td>
														<td>${payment.status}</td>
														<td><c:if test="${payment.trackingId eq null}">${payment.response}</c:if>
															<c:if test="${payment.trackingId ne null}">${payment.trackingId}</c:if>
														</td>
														<td>${payment.amount}</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
									<!--   profile table end -->
								</div>


							</div>
						</div>
					</div>
				
			
		</c:otherwise>
	</c:choose>
	</div>
				<!-- end content  -->
	</div>
			<!-- Button trigger modal -->
		</div>
	</c:if>

<!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">

   var table = $('table.display').DataTable({
            dom: 'Bfrtip',
            "pageLength": 5
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

  $(document).ready(function(){
	$('#nav-billing').addClass('active');
	$('.rm-sort').removeClass('sorting_1');
	//$(".deactivelink").removeClass('active');
  });
 </script>
</body>
</html>
