<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Dashboard</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/client/dashboard.js"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard/ca-dashboard.css" media="all" />
<script type="text/javascript">
var echarts, ecConfig, yr, clientList = new Array();var pendingDataArray = new Array();var filledDataArray = new Array();var submittedDataArray = new Array();var pendingGSTNDataArray = new Array();var filledGSTNDataArray = new Array();var submittedGSTNDataArray = new Array();var ticks = new Array();</script>
<style>
.clintlist {font-weight: unset;}
 .client-detail .clintlist .gstn-phone .ind_formatss{font-weight:bolder}
 .reportTable tr td:first-child, .reportTable tr th:first-child {
    background-color: #bbdef9!important;
    color: #000000!important;
 border-bottom: 1px solid #fff;}
.clintlist a{color:#000!important}
.DTFC_RightBodyWrapper,.DTFC_RightBodyLiner{overflow: visible!important;}
.client-detail .clintlist{padding:0px!important;border:none;background-color:none;}
.client-detail .clintlist .compname{padding-right:11px;padding-left:11px;padding-top:11px;width:100%;}
.client-detail .clintlist .gstn-phone{padding-right:11px;padding-bottom:11px;padding-left:11px;width:100%;border-bottom: 1px solid lavender;}
.ytddrop .dropdown-content-right {right: 100px!important;}
.ytddrop .dropdown-content-left {left: 110px!important;}
.ytddrop{min-width:100px}
#processing{color: green;position: absolute;width: 100%;top:40%;z-index: 2;font-weight: bold;font-size: 20px;margin-left: 50%!important;}
.downloads_links{background-color: #fff;padding: 10px;margin: 0 0 15px; -webkit-box-shadow: 0 0 2px 2px #eff3f6;box-shadow: 0 0 2px 2px #eff3f6;/* float: left; */width: 100%;}
.downloads_links .sm-img {background-color: #e6eaeb; border-radius: 50%; margin-right: 10px;float: left;height: 30px;width: 30px;}
.downloads_links a{vertical-align: middle;font-size: 15px;}
</style>
</head>
<body class="body-cls">
  <!-- header page begin -->
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>
	<!--- breadcrumb start -->
	<div class="breadcrumbwrap dash_bread">
<div class="container">
	<div class="row">
        <div class="col-md-12 col-sm-12">
				<ol class="breadcrumb"><li class="breadcrumb-item active" style="font-size: 14px;font-weight: 700;">All <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Businesses</c:otherwise></c:choose> GST Filing Status </li></ol>
					<select class="pull-right mt-1" name="financialYear" id="financialYear"><option value="2021">2021 - 2022</option><option value="2020">2020 - 2021</option><option value="2019">2019 - 2020</option><option value="2018">2018 - 2019</option><option value="2017">2017 - 2018</option></select>
					<h6 class="f-14-b pull-right mt-2 mr-2 font-weight-bold" style="display: inline-block;">Financial Year : </h6> 
					<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>
  <div class="db-ca-wrap">
    <div class="container">
	<div style="height:35px;background-color: #f6f9fb;" class="dash_tabs_container">
	<div>
<div id="dataLoad" class="text-center dash_tabs">Loading Data, Please wait...</div>
<ul class="nav nav-tabs dash_bread-nav" role="tablist" style="width:100%;color:#707172; font-size:13px">
 
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab4" onClick="fetchFilingStatus('4')" href="#" role="tab">April</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab5" onClick="fetchFilingStatus('5')" href="#" role="tab">May</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab6" onClick="fetchFilingStatus('6')" href="#" role="tab" aria-expanded="false">June</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab7" onClick="fetchFilingStatus('7')" href="#" role="tab" aria-expanded="true">July</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab8" onClick="fetchFilingStatus('8')" href="#" role="tab">August</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab9" onClick="fetchFilingStatus('9')" href="#" role="tab">September</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab10" onClick="fetchFilingStatus('10')" href="#" role="tab">October</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab11" onClick="fetchFilingStatus('11')" href="#" role="tab">November</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab12" onClick="fetchFilingStatus('12')" href="#" role="tab">December</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab1" onClick="fetchFilingStatus('1')" href="#" role="tab">January </a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab2" onClick="fetchFilingStatus('2')" href="#" role="tab">February</a></li>
  <li class="nav-item"><a class="nav-link mndash" data-toggle="tab" id="tab3" onClick="fetchFilingStatus('3')" href="#" role="tab">March</a></li>
</ul>
  <div class="tab-content">
	<div class="tab-pane active" id="dashtab1" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab2" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab3" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab4" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab5" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab6" role="tabpanel"><p></p></div>
	<div class="tab-pane" id="dashtab7" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab8" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab9" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab10" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab11" role="tabpanel"><p></p></div>
    <div class="tab-pane" id="dashtab12" role="tabpanel"><p></p></div>
  </div>
</div>
	</div>		
       <div class="row">
				<!-- GST info start -->
				<div class="col-sm-12"><div class="gstdateinfo pb-0">
				<ul>
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR1]}'>
				<li><div class="gstinfobox"><span class="gstname boldtxt">Returns <br />Last Date</span> </div> <div class="gstinfobox"><span class="statusname">Pending</span><span class="submittedname">Submitted</span><span class="filledname">Filed</span></div></li>
				<li><div class="gstinfobox"><span class="gstname">GSTR 1</span><span class="datename"  id="date<%=MasterGSTConstants.GSTR1%>">${returnsSummaryMap[MasterGSTConstants.GSTR1].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR1%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR1%>"><span class="arrow-left"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR1%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown  p-0"><a href="#" class="urllink submittedtxt"  style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR1%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR1%>"><span class="arrow-left"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR1%>"class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR1%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR1%>"><span class="arrow-left"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR1%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR2]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 2</span><span class="datename" id="date<%=MasterGSTConstants.GSTR2%>">${returnsSummaryMap[MasterGSTConstants.GSTR2].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR2%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR2%>"><span class="arrow-left"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR2%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown  p-0"><a href="#" class="urllink submittedtxt"  style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR2%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR2%>"><span class="arrow-left"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR2%>"class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR2%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR2%>"><span class="arrow-left"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR2%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR3]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 3</span><span class="datename" id="date<%=MasterGSTConstants.GSTR3%>">${returnsSummaryMap[MasterGSTConstants.GSTR3].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR3%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR3%>"><span class="arrow-left"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR3%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown  p-0"><a href="#" class="urllink submittedtxt"  style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR3%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR3%>"><span class="arrow-left"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR3%>"class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR3%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR3%>"><span class="arrow-left"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR3%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR3B]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 3B</span><span class="datename" id="date<%=MasterGSTConstants.GSTR3B%>">${returnsSummaryMap[MasterGSTConstants.GSTR3B].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR3B%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR3B%>"><span class="arrow-left"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR3B%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown  p-0"><a href="#" class="urllink submittedtxt"  style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR3B%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR3B%>"><span class="arrow-left"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR3B%>"class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR3B%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR3B%>"><span class="arrow-left"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR3B%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR4]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 4</span><span class="datename" id="date<%=MasterGSTConstants.GSTR4%>">${returnsSummaryMap[MasterGSTConstants.GSTR4].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR4%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR4%>"><span class="arrow-left"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR4%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown  p-0"><a href="#" class="urllink submittedtxt"  style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR4%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR4%>"><span class="arrow-left"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR4%>"class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR4%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR4%>"><span class="arrow-left"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR4%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR5]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 5</span><span class="datename" id="date<%=MasterGSTConstants.GSTR5%>">${returnsSummaryMap[MasterGSTConstants.GSTR5].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR5%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR5%>"><span class="arrow-left"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR5%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown  p-0"><a href="#" class="urllink submittedtxt"  style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR5%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR5%>"><span class="arrow-left"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR5%>"class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR5%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR5%>"><span class="arrow-left"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR5%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR6]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 6</span><span class="datename" id="date<%=MasterGSTConstants.GSTR6%>">${returnsSummaryMap[MasterGSTConstants.GSTR6].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR6%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR6%>"><span class="arrow-left"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR6%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown  p-0"><a href="#" class="urllink submittedtxt"  style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR6%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR6%>"><span class="arrow-left"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR6%>"class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR6%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR6%>"><span class="arrow-left"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR6%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR7]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 7</span><span class="datename" id="date<%=MasterGSTConstants.GSTR7%>">${returnsSummaryMap[MasterGSTConstants.GSTR7].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR7%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR7%>"><span class="arrow-left"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR7%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown  p-0"><a href="#" class="urllink submittedtxt"  style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR7%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR7%>"><span class="arrow-left"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR7%>"class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR7%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR7%>"><span class="arrow-left"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR7%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR8]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 8</span><span class="datename" id="date<%=MasterGSTConstants.GSTR8%>">${returnsSummaryMap[MasterGSTConstants.GSTR8].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR8%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR8%>"><span class="arrow-left"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR8%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown  p-0"><a href="#" class="urllink submittedtxt"  style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR8%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR8%>"><span class="arrow-left"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR8%>"class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR8%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR8%>"><span class="arrow-left"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR8%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR9]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 9</span><span class="datename" id="date<%=MasterGSTConstants.GSTR9%>">${returnsSummaryMap[MasterGSTConstants.GSTR9].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR9%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR9%> dropdown-content-right"><span class="arrow-right"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR9%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown p-0"><a href="#" class="urllink submittedtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR9%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR9%> dropdown-content-right"><span class="arrow-right"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR9%>" class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR9%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR9%> dropdown-content-right"><span class="arrow-right"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR9%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				
				<c:if test='${not empty returnsSummaryMap && not empty returnsSummaryMap[MasterGSTConstants.GSTR10]}'>
				<li><div class="gstinfobox"><span class="gstname">GSTR 10</span><span class="datename" id="date<%=MasterGSTConstants.GSTR10%>">${returnsSummaryMap[MasterGSTConstants.GSTR10].duedate}</span></div>
				<div class="gstinfobox"><span class="statustxt dropdown p-0"><a href="#" class="urllink statustxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span class="poi" id="pending<%=MasterGSTConstants.GSTR10%>"></span></a><div class="dropdown-content pending<%=MasterGSTConstants.GSTR10%> dropdown-content-right"><span class="arrow-right"></span><h6 class="dash_hdr">Pending Clients</h6><div class="scrollbar" id="style-2"><ul id="pendingClients<%=MasterGSTConstants.GSTR10%>" class="force-overflow"></ul></div></div></span><span class="submittedtxt dropdown p-0"><a href="#" class="urllink submittedtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="submitted<%=MasterGSTConstants.GSTR10%>" class="pois"></span></a><div class="dropdown-content submitted<%=MasterGSTConstants.GSTR10%> dropdown-content-right"><span class="arrow-right"></span><h6 class="dash_hdr">Submitted Clients</h6><div class="scrollbar" id="style-2"><ul id="submittedClients<%=MasterGSTConstants.GSTR10%>" class="force-overflow"></ul></div></div></span><span class="filledtxt dropdown p-0"><a href="#" class="urllink filledtxt" style="padding: 0px 10px;margin-bottom:0px;border:unset" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"><span id="filed<%=MasterGSTConstants.GSTR10%>" class="poif"></span></a><div class="dropdown-content filed<%=MasterGSTConstants.GSTR10%> dropdown-content-right"><span class="arrow-right"></span><h6 class="dash_hdr">Filed Clients</h6><div class="scrollbar" id="style-2"><ul id="filedClients<%=MasterGSTConstants.GSTR10%>"class="force-overflow"></ul></div></div></span></div></li>
				</c:if>
				 </ul>
				
				</div><a class="pull-right mt-2" href="${contextPath}/cp_ClientsReportsData/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">Click here to see detailed report</a></div>
				  </div>
				<div class="col-12">&nbsp;</div>
				<div class="row graph_row_wrap">
				<div id="processing" class="Process_text"></div>
        <div class="col-sm-12">
        <h4 class="hdrtitle mb-2" style="display: inline-block;"> All <c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Businesses</c:otherwise></c:choose> Financial Summary of <span class="compdashfinancialYear"  style="margin-left: 3px;"></span></h4>
		<a href="#" id="dwnldxls" class="btn btn-blue mb-3 pull-right excel_btn" style="padding: 6px 15px 5px;font-weight: bold;color: #435a93;" data-toggle="tooltip" data-placement="top" title="Download Financial Summary To Excel">Download To Excel<i class="fa fa-download ml-1" aria-hidden="true"></i></a>
        <div class="customtable db-ca-view reportTable reportTable2 summaryTable" style="width: 100%;overflow: visible!important;">
            <table id="reportTable3" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
              <thead><tr><th class="text-center"></th><th class="text-center">April</th><th class="text-center">May</th><th class="text-center">June</th><th class="text-center">July</th><th class="text-center">August</th><th class="text-center">September</th><th class="text-center">October</th><th class="text-center">November</th><th class="text-center">December</th><th class="text-center">January</th><th class="text-center">February</th><th class="text-center">March</th><th class="text-center">YTD(Year To Date)</th></tr></thead>
              <tbody>
			  <c:set var="monthArray" value="${['4','5','6','7','8','9','10','11','12','1']}" />
			  <c:set var="monthArray23" value="${['2','3']}" />
				<tr data-toggle="modal" data-target="#viewModal"><td align="center"><h6>Sales</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="sales${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail sales${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="sales${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail sales${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdSales" id="ytdSales">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail allclntsalesss p-0"></ul></div></div></span></td> </tr>
				 <tr><td align="center"><h6>Purchases</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="purchase${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail purchase${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="purchase${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail purchase${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdPurchases" id="ytdPurchases">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdpurchasess" class="force-overflow client-detail allclntpurchasesss p-0"></ul></div></div></span></td></tr>
				 <tr><td align="center"><h6>Expenses</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="expense${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail expense${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="expense${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail expense${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdexpenses" id="ytdexpenses">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdexpensess" class="force-overflow client-detail allclntexpensesss p-0"></ul></div></div></span></td></tr>
				 <tr><td align="center"><h6>Balance</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="bal${month}" data-toggle="tooltip" title = "">0.00</td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="bal${month}" data-toggle="tooltip" title = "">0.00</td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdBalance" id="ytdBalance">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdBalanceAmt" class="force-overflow client-detail allclntBalanceAmt p-0"></ul></div></div></span></td> </tr>
				 <tr><td align="center"><h6>Output Tax</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="salestax${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail salestax${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="salestax${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail salestax${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdSalestax" id="ytdSalestax">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdSalestaxAmt" class="force-overflow client-detail allclntSalestaxAmt p-0"></ul></div></div></span></td></tr>
				 <tr><td align="center"><h6>Input Tax</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="purchasetax${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail purchasetax${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="purchasetax${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail purchasetax${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdPurchasetax" id="ytdPurchasetax">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdPurchasetaxAmt" class="force-overflow client-detail allclntPurchasetaxAmt p-0"></ul></div></div></span></td></tr>
				 <tr class="addrowshadow"><td align="center"><h6>Monthly Tax</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="tax${month}" data-toggle="tooltip" title = "" style="font-weight:bold">0.00</td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right ind_formatss" id="tax${month}" data-toggle="tooltip" title = "" style="font-weight:bold">0.00</td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdTax" id="ytdTax">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdTaxamt" class="force-overflow client-detail allclnttaxamt p-0"></ul></div></div></span></td></tr>
                 <tr><td align="center"><h6>Exempted</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="exempted${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail exempted${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="exempted${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail exempted${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdexempted" id="ytdexempted">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdexemptedamt" class="force-overflow client-detail allclntexemptedamt p-0"></ul></div></div></span></td></tr>
				 <tr><td align="center"><h6>TCS Payable</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="tcs${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail tcs${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="tcs${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail tcs${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdtcs" id="ytdtcs">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdtcstax" class="force-overflow client-detail allclnttcstax p-0"></ul></div></div></span></td></tr>
				 <tr><td align="center"><h6>TCS Receivable</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="ptcs${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail ptcs${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="ptcs${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail ptcs${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdptcs" id="ytdptcs">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdptcstax" class="force-overflow client-detail allclnttcstax p-0"></ul></div></div></span></td></tr>
				 <tr><td align="center"><h6>TDS Payable</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="tds${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail tds${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="tds${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail tds${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdtds" id="ytdtds">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdtdstax" class="force-overflow client-detail allclnttdstax p-0"></ul></div></div></span></td></tr>
				 <tr><td align="center"><h6>Cummulative Tax</h6> </td><c:forEach items="${monthArray}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="cumtax${month}">0.00</span><div class="dropdown-content dropdown-content-left"><span class="arrow-left"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail cumtax${month} p-0"></ul></div></div></span></td></c:forEach><c:forEach items="${monthArray23}" var="month" varStatus="loop"><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss" id="cumtax${month}">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdsalesss" class="force-overflow client-detail cumtax${month} p-0"></ul></div></div></span></td></c:forEach><td class="text-right"><span class="dropdown ytddrop"><span class="text-right ytd ind_formatss ytdCumTax" id="ytdCumTax">0.00</span><div class="dropdown-content dropdown-content-right"><span class="arrow-right"></span><div class="scrollbar" id="style-2"><ul id="ytdcumtax" class="force-overflow client-detail allclntcumtax p-0"></ul></div></div></span></td> </tr>
               </tbody>
            </table><p style="font-size: 13px; text-align: right;">Note: The above currency is in Indian Rupees</p>
          </div>
		</div>
		</div>
         <h5>Download Templates</h5>
      <div class="row downloads_links">
           <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template.xls">MasterGST Sales Template</a></div>
           <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_Purchase_Invoices_Template.xlsx">Sage Purchase Template</a></div>
             <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_einvoices_Template.xls">Sage E-Invoice Template</a></div>
             <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Sage_Sales_Invoices_Template.xlsx">Sage Sales Template</a> </div>
             <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Purchase_Invoices_Template.xls">MasterGST Purchase Template</a></div>
             <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_ewaybill_excel_Template.xls">MasterGST EwayBill Template</a></div>
              <div class="col-md-4 pb-1"> <span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Template.xls">Tally Sales Template</a></div>
			 <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Purchase_Invoices_Template.xlsx">Tally Purchase Template</a></div>
			 <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_einvoices_Template.xls">E-Invoice Template</a></div>
			 <div class="col-md-4 pb-1 entertaiment_einvoice_import_template"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Entertainment_Mastergst_Einvoice_Template.xls">Entertainment E-Invoice Template</a></div>
			 <div class="col-md-4 pb-1"> <span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Template_v17.xls">Tally Sales Template_V1.7</a> </div>
			 <div class="col-md-4 pb-1"> <span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Tally_Sales_Prime_Template.xls">Tally Sales Prime Template</a> </div>
			 <div class="col-md-4 pb-1"> <span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/GSTR1_Offline_Utility.xlsx">GSTR1 Offline Template</a> </div>
			 <div class="col-md-4 pb-1 fhpl_import_template"> <span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template_with_Additional_Fields.xls">MasterGST Sales Template with Additional Fields </a></div>
			 <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/MasterGST_Sales_Invoices_Template_with_All_Fields.xls">MasterGST Sales Template with All Fields</a></div>
			 <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/static/mastergst/template/Single_Sheet_Purchase_Invoices_Template.xlsx">Single Sheet Template of Purchase Invoice </a></div>
     </div>
     
     <h5><a class="urllink" href="#" link="${contextPath}/cp_ClientsReports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>">Global Reports</a></h5>
	      <div class="row downloads_links">
	              <div class="col-md-4 pb-1"><span class="sm-img"></span><a class="" href="${contextPath}/cp_ClientsReportsData/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">All clients filing status Report</a></div>
	              <c:if test="${usertype eq userCenter}"><div class="col-md-4 pb-1"><span class="sm-img"></span><a class="pull-right" href="${contextPath}/cp_allcenters/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">All Suvidha Kendra Usage Report.</a></div></c:if>
				  <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/cp_ClientsReportsGroupData/Sales/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">Group Wise Sales Reports</a></div>
	             <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/cp_ClientsReportsGroupData/GSTR2/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">Group Wise Purchase Reports</a></div>
	             <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/PaymentReportsGroupData/GSTR1/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">Group Wise Payments Received Report</a></div>
	           <div class="col-md-4 pb-1"> <span class="sm-img"></span><a href="${contextPath}/PaymentReportsGroupData/GSTR2/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}">Group Wise Payments Made Report</a></div>
	             <div class="col-md-4 pb-1"><span class="sm-img"></span><a href="${contextPath}/cp_multiGtstin/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}?type=multigstin">Multi GSTIN Search Report.</a></div>
         </div>
      </div> 
                
           
        </div>

   <!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">
$(document).ready(function() {
    var table = $('#reportTable3').DataTable( {
		"paging": false,
        scrollX:        true,
        scrollCollapse: true,
		ordering: false,
		info:false,
		filter:false,
		lengthChange: false,
		fixedColumns:{
		leftColumns:1,
		rightColumns:1
		}
    });
});
$(function() {
	$('#nav-dashboard').addClass('active');
	var yrss = '<c:out value="${year}"/>';
	if(${month} == '1' || ${month} == '2' || ${month} == '3'){
		yrss--;
	}
	$('#financialYear').val(yrss);$('.mndash').removeClass('active');$('#tab'+${month}).addClass('active');
	<c:forEach items="${clientList}" var="clientId">
	clientList.push('<c:out value="${clientId}"/>');
	</c:forEach>
	ticks.push('<%=MasterGSTConstants.GSTR1%>');
	ticks.push('<%=MasterGSTConstants.GSTR2%>');
	ticks.push('<%=MasterGSTConstants.GSTR3%>');
	ticks.push('<%=MasterGSTConstants.GSTR3B%>');
	ticks.push('<%=MasterGSTConstants.GSTR4%>');
	ticks.push('<%=MasterGSTConstants.GSTR5%>');
	ticks.push('<%=MasterGSTConstants.GSTR6%>');
	ticks.push('<%=MasterGSTConstants.GSTR7%>');
	ticks.push('<%=MasterGSTConstants.GSTR8%>');
	ticks.push('<%=MasterGSTConstants.GSTR9%>');
	ticks.push('<%=MasterGSTConstants.GSTR10%>');
	summaryReport(yrss);
	updateStatus();
	$('#dataLoad').addClass('processingEndText')
});
function updateStatus() { 
	ticks.forEach(function(type) {
		$.ajax({
			url: "${contextPath}/fstatuss?retType="+type+"&month="+month+"&year="+${year}+"&clientIds="+clientList,
			async: false,
			cache: false,
			success : function(response) {
				pendingDataArray = new Array(),submittedDataArray = new Array(),submittedGSTNDataArray = new Array(),filledDataArray = new Array(),filledGSTNDataArray = new Array();
				$('#pendingClients'+type).html('');$('#submittedClients'+type).html('');$('#filedClients'+type).html('');
				if(response["Pending"]) {
					pendingDataArray.push(response["Pending"].length);
					for(var i=0; i<response["Pending"].length;i++){
						if(type == 'GSTR1'){
							$('#pendingClients'+type).append("<li class='clintlist'><a class='permissionInvoices-Sales-View-Invoices-Sales' style='display:none'><span class='compname'>" + response['Pending'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Pending'][i].gstnnumber +" - +91 "+response['Pending'][i].mobilenumber+"</span></a><a class='permissionInvoices-Sales-View' href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Pending'][i].groupName+"/"+type+"/"+month+"/<c:out value='${year}'/>?type=initial'><span class='compname'>" + response['Pending'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Pending'][i].gstnnumber +" - +91 "+response['Pending'][i].mobilenumber+"</span></a></li>");
						}else{							
							$('#pendingClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Pending'][i].groupName+"/"+type+"/"+month+"/<c:out value='${year}'/>?type=initial'><span class='compname'>" + response['Pending'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Pending'][i].gstnnumber +" - +91 "+response['Pending'][i].mobilenumber+"</span></a></li>");
						}
						}
				} else {pendingDataArray.push(0);}
				if(response["Submitted"]) {
					var counts = 0,increment=1;
					if(response["Submitted"].length>=1){
						for(var i=0; i<response["Submitted"].length;i++){
							if(counts == 0){
								submittedDataArray.push(increment);
								submittedGSTNDataArray.push(response['Submitted'][i].gstnnumber+response['Submitted'][i].businessname);
								$('#submittedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Submitted'][i].groupName+"/"+type+"/"+month+"/<c:out value='${year}'/>?type=initial'><span class='compname'>" + response['Submitted'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Submitted'][i].gstnnumber +" - +91 "+response['Submitted'][i].mobilenumber+"</span></a></li>");
								increment++;
							}
							if(jQuery.inArray(response['Submitted'][i].gstnnumber+response['Submitted'][i].businessname,submittedGSTNDataArray) == -1){
								submittedDataArray.push(increment);
								submittedGSTNDataArray.push(response['Submitted'][i].gstnnumber+response['Submitted'][i].businessname);
								$('#submittedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Submitted'][i].groupName+"/"+type+"/"+month+"/<c:out value='${year}'/>?type=initial'><span class='compname'>" + response['Submitted'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Submitted'][i].gstnnumber +" - +91 "+response['Submitted'][i].mobilenumber+"</span></a></li>");
								increment++;
							}counts++;
						}
					}else{submittedDataArray.push(0);}
				} else {submittedDataArray.push(0);}
				if(response["Filed"]) {
					var counts = 0,increment=1;
					if(response["Filed"].length>=1){
						for(var i=0; i<response["Filed"].length;i++){
							if(counts == 0){
								filledDataArray.push(increment);
								filledGSTNDataArray.push(response['Filed'][i].gstnnumber+response['Filed'][i].businessname);
								$('#filedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Filed'][i].groupName+"/"+type+"/"+month+"/<c:out value='${year}'/>?type=initial'><span class='compname'>" + response['Filed'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Filed'][i].gstnnumber +" - +91 "+response['Filed'][i].mobilenumber+"</span></a></li>");
								increment++;
							}
							if(jQuery.inArray(response['Filed'][i].gstnnumber+response['Filed'][i].businessname,filledGSTNDataArray) == -1){
								filledDataArray.push(increment);
								filledGSTNDataArray.push(response['Filed'][i].gstnnumber+response['Filed'][i].businessname);
								$('#filedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Filed'][i].groupName+"/"+type+"/"+month+"/<c:out value='${year}'/>?type=initial'><span class='compname'>" + response['Filed'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Filed'][i].gstnnumber +" - +91 "+response['Filed'][i].mobilenumber+"</span></a></li>");
								increment++;
							}counts++;
						}						
					}else{filledDataArray.push(0);}
				} else {filledDataArray.push(0);}
				$('#pending'+type).html(pendingDataArray[pendingDataArray.length-1]);$('#submitted'+type).html(submittedDataArray[submittedDataArray.length-1]);$('#filed'+type).html(filledDataArray[filledDataArray.length-1]);
			},
			error : function(e) {
				pendingDataArray.push(0);
				submittedDataArray.push(0);
				filledDataArray.push(0);
				$('#pending'+type).html(0);
				$('#submitted'+type).html(0);
				$('#filed'+type).html(0);
			}
		});
	});
}
$('#financialYear').change(function() {
	$('#dataLoad').addClass('processingText').removeClass('processingEndText');
	var finYear = $(this).val();
	if(month == '1' || month == '2' || month == '3'){
		finYear++;
	}
	$('.mndash').removeClass('active');
	$('#tab'+month).addClass('active');
	var date = '01' + '-'+ ((''+month).length<2 ? '0' : '') + month + '-' + finYear;
	$.ajax({
		url: "${contextPath}/mdfyreturns?userid=${id}&date="+date,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(summary) {
			if(summary) {
				$.each(summary, function(key, value){
					$('#date'+key).html(value.duedate);
				});
			}
		}
	});
	ticks.forEach(function(type) {
		$.ajax({
			url: "${contextPath}/fstatuss?retType="+type+"&month="+month+"&year="+finYear+"&clientIds="+clientList,
			async: true,
			cache: false,
			success : function(response) {
				pendingDataArray = new Array(),submittedDataArray = new Array(),submittedGSTNDataArray = new Array(),filledDataArray = new Array(),filledGSTNDataArray = new Array();
				$('#pendingClients'+type).html('');$('#submittedClients'+type).html('');$('#filedClients'+type).html('');
				if(response["Pending"]) {
					pendingDataArray.push(response["Pending"].length);
					for(var i=0; i<response["Pending"].length;i++){
					
						$('#pendingClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Pending'][i].groupName+"/"+type+"/"+month+"/"+finYear+"?type=initial'><span class='compname'>" + response['Pending'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Pending'][i].gstnnumber +" - +91 "+response['Pending'][i].mobilenumber+"</span></a></li>");
					}		
				} else {pendingDataArray.push(0);}
				if(response["Submitted"]) {
					var counts = 0,increment;
					if(response["Submitted"].length>=1){
						for(var i=0; i<response["Submitted"].length;i++){
							if(counts == 0){
								submittedDataArray.push(increment);	
								submittedGSTNDataArray.push(response['Submitted'][i].gstnnumber+response['Submitted'][i].businessname);
								$('#submittedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Submitted'][i].groupName+"/"+type+"/"+month+"/"+finYear+"?type=initial'><span class='compname'>" + response['Submitted'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Submitted'][i].gstnnumber +" - +91 "+response['Submitted'][i].mobilenumber+"</span></a></li>");
								increment++;
							}
							if(jQuery.inArray(response['Submitted'][i].gstnnumber+response['Submitted'][i].businessname,submittedGSTNDataArray) == -1){
								submittedDataArray.push(increment);	
								submittedGSTNDataArray.push(response['Submitted'][i].gstnnumber+response['Submitted'][i].businessname);
								$('#submittedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Submitted'][i].groupName+"/"+type+"/"+month+"/"+finYear+"?type=initial'><span class='compname'>" + response['Submitted'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Submitted'][i].gstnnumber +" - +91 "+response['Submitted'][i].mobilenumber+"</span></a></li>");
								increment++;
							}counts++;
						}
					}else{submittedDataArray.push(0);}
				} else {submittedDataArray.push(0);}
				if(response["Filed"]) {
					var counts = 0,increment=1;
					if(response["Filed"].length>=1){
						for(var i=0; i<response["Filed"].length;i++){
							if(counts == 0){
								filledDataArray.push(increment);
								filledGSTNDataArray.push(response['Filed'][i].gstnnumber+response['Filed'][i].businessname);
								$('#filedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Filed'][i].groupName+"/"+type+"/"+month+"/"+finYear+"?type=initial'><span class='compname'>" + response['Filed'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Filed'][i].gstnnumber +" - +91 "+response['Filed'][i].mobilenumber+"</span></a></li>");
								increment++;
							}
							if(jQuery.inArray(response['Filed'][i].gstnnumber+response['Filed'][i].businessname,filledGSTNDataArray) == -1){
								filledDataArray.push(increment);
								filledGSTNDataArray.push(response['Filed'][i].gstnnumber+response['Filed'][i].businessname);
								$('#filedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Filed'][i].groupName+"/"+type+"/"+month+"/"+finYear+"?type=initial'><span class='compname'>" + response['Filed'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Filed'][i].gstnnumber +" - +91 "+response['Filed'][i].mobilenumber+"</span></a></li>");
								increment++;
							}counts++;
						}
					}else {filledDataArray.push(0);}
				} else {filledDataArray.push(0);}
				$('#pending'+type).html(pendingDataArray[pendingDataArray.length-1]);
				$('#submitted'+type).html(submittedDataArray[submittedDataArray.length-1]);
				$('#filed'+type).html(filledDataArray[filledDataArray.length-1]);
			},
			error : function(e) {
				pendingDataArray.push(0);
				submittedDataArray.push(0);
				filledDataArray.push(0);
				$('#pending'+type).html(0);
				$('#submitted'+type).html(0);
				$('#filed'+type).html(0);
			}
		});
	});
	var yrss = $(this).val();
	summaryReport(yrss);
	$('#dataLoad').addClass('processingEndText').removeClass('processingText');
});
function fetchFilingStatus(mnth){
	$('#dataLoad').addClass('processingText').removeClass('processingEndText');
	var yrs = $('#financialYear').val();
	if(mnth == '1' || mnth == '2' || mnth == '3'){
		yrs++;
	}
	month = mnth;
	var date = '01' + '-'+ ((''+mnth).length<2 ? '0' : '') + mnth + '-' + yrs;
	$.ajax({
		url: "${contextPath}/mdfyreturns?userid=${id}&date="+date,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(summary) {
			if(summary) {
				$.each(summary, function(key, value){
					$('#date'+key).html(value.duedate);
				});
			}
		}
	});
	ticks.forEach(function(type) {
		$.ajax({
			url: "${contextPath}/fstatuss?retType="+type+"&month="+mnth+"&year="+yrs+"&clientIds="+clientList,
			async: false,
			cache: false,
			success : function(response) {
				pendingDataArray = new Array(),submittedDataArray = new Array(),submittedGSTNDataArray = new Array(),filledDataArray = new Array(),filledGSTNDataArray = new Array();
				$('#pendingClients'+type).html('');$('#submittedClients'+type).html('');$('#filedClients'+type).html('');
				if(response["Pending"]) {
					pendingDataArray.push(response["Pending"].length);
					for(var i=0; i<response["Pending"].length;i++){
						$('#pendingClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Pending'][i].groupName+"/"+type+"/"+mnth+"/"+yrs+"?type=initial'><span class='compname'>" + response['Pending'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Pending'][i].gstnnumber +" - +91 "+response['Pending'][i].mobilenumber+"</span></a></li>");
					}
				} else {pendingDataArray.push(0);}
				if(response["Submitted"]) {
					var counts = 0,increment=1;
					if(response["Submitted"].length>=1){
						for(var i=0; i<response["Submitted"].length;i++){
							if(counts == 0){
								submittedDataArray.push(increment);
								submittedGSTNDataArray.push(response['Submitted'][i].gstnnumber+response['Submitted'][i].businessname);
								$('#submittedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Submitted'][i].groupName+"/"+type+"/"+mnth+"/"+yrs+"?type=initial'><span class='compname'>" + response['Submitted'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Submitted'][i].gstnnumber +" - +91 "+response['Submitted'][i].mobilenumber+"</span></a></li>");
								increment++;
							}
							if(jQuery.inArray(response['Submitted'][i].gstnnumber+response['Submitted'][i].businessname,submittedGSTNDataArray) == -1){
								submittedDataArray.push(increment);
								submittedGSTNDataArray.push(response['Submitted'][i].gstnnumber+response['Submitted'][i].businessname);
								$('#submittedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Submitted'][i].groupName+"/"+type+"/"+mnth+"/"+yrs+"?type=initial'><span class='compname'>" + response['Submitted'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Submitted'][i].gstnnumber +" - +91 "+response['Submitted'][i].mobilenumber+"</span></a></li>");
								increment++;
							}counts++;
						}						
					}else{submittedDataArray.push(0);}
				} else {submittedDataArray.push(0);}
				if(response["Filed"]) {
					var counts = 0,increment=1;
					if(response["Filed"].length>=1){
						for(var i=0; i<response["Filed"].length;i++){
							if(counts == 0){
								filledDataArray.push(increment);
								filledGSTNDataArray.push(response['Filed'][i].gstnnumber+""+response['Filed'][i].businessname);
								$('#filedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Filed'][i].groupName+"/"+type+"/"+mnth+"/"+yrs+"?type=initial'><span class='compname'>" + response['Filed'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Filed'][i].gstnnumber +" - +91 "+response['Filed'][i].mobilenumber+"</span></a></li>");
								increment++;
							}
							if(jQuery.inArray(response['Filed'][i].gstnnumber+""+response['Filed'][i].businessname,filledGSTNDataArray) == -1){
								filledDataArray.push(increment);
								filledGSTNDataArray.push(response['Filed'][i].gstnnumber+response['Filed'][i].businessname);
								$('#filedClients'+type).append("<li class='clintlist'><a href='${contextPath}/alliview/<c:out value='${id}'/>/<c:out value='${fullname}'/>/<c:out value='${usertype}'/>/"+response['Filed'][i].groupName+"/"+type+"/"+mnth+"/"+yrs+"?type=initial'><span class='compname'>" + response['Filed'][i].businessname + "</span><br><span class='gstn-phone'>"+response['Filed'][i].gstnnumber +" - +91 "+response['Filed'][i].mobilenumber+"</span></a></li>");
								increment++;
							}counts++;
						}
					}else{filledDataArray.push(0);}
				} else {filledDataArray.push(0);}
				$('#pending'+type).html(pendingDataArray[pendingDataArray.length-1]);$('#submitted'+type).html(submittedDataArray[submittedDataArray.length-1]);$('#filed'+type).html(filledDataArray[filledDataArray.length-1]);
			},
			error : function(e) {
				pendingDataArray.push(0);submittedDataArray.push(0);filledDataArray.push(0);$('#pending'+type).html(0);$('#submitted'+type).html(0);$('#filed'+type).html(0);
			}
		});
	});
	$('#dataLoad').addClass('processingEndText').removeClass('processingText');
}

function summaryReport(finYear){
	var nextyear = (parseInt(finYear)+1).toString();
	$('.compdashfinancialYear').text(finYear+" - "+nextyear);
	$(".allclntsalesss").html('');
	var excelurl = "${contextPath}/allindclientsdwnldFinancialSummaryxls?userid=${id}&year="+finYear+"&clientIds="+clientList;
	$('#dwnldxls').attr("href",excelurl);
	if($.fn.DataTable.isDataTable('#reportTable3')){$('#reportTable3').DataTable().destroy();}
	var clnsales = "";
			$.ajax({
				url: "${contextPath}/allindclientsinvoicessummary?userid=${id}&year="+finYear+"&clientIds="+clientList,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				beforeSend: function () {$('#processing').text('Processing...')},
				success : function(summary) {
					
					$.each(summary, function(keys, values){	
						if(values.client.businessname == 'Allclients'){
							$.each(values, function(keyss, valuess){
								if(keyss == 'summaryMap'){
									$.each(valuess, function(key, value){
									if(key != 'totals'){
										$('#sales'+key).html('<span class="ind_formatss" style="color:black" id="sal'+key+'">'+value.Sales+'</span>');
										$('#purchase'+key).html('<span class="ind_formatss" style="color:black" id="pur'+key+'">'+value.Purchase+'</span>');
										$('#expense'+key).html('<span class="ind_formatss" style="color:black" id="pur'+key+'">'+value.Expenses+'</span>');
										$('#cumtax'+key).html(value.cummulativeTax);
										if(value.Balance < 0){$('#bal'+key).css("color","red");}else{$('#bal'+key).css("color","#000");}
										$('#bal'+key).html(value.Balance);$('#bal'+key).attr("data-original-title","Sales : "+formatNumber(value.Sales)+" - (Purchase : "+formatNumber(value.Purchase)+" + Expenses : "+formatNumber(value.Expenses)+") = "+formatNumber(value.Balance));$('#salestax'+key).html(value.SalesTax);$('#purchasetax'+key).html(value.PurchaseTax);$('#exempted'+key).html(value.exempted);$('#tcs'+key).html(value.tcsamount);$('#ptcs'+key).html(value.ptcsamount);$('#tds'+key).html(value.tdsamount);$('#tax'+key).html(value.Tax);$('#tax'+key).attr("data-original-title","Sales GST : "+formatNumber(value.SalesTax)+" - ITC : "+formatNumber(value.PurchaseTax)+" = "+formatNumber(value.Tax));
									}else{
										$('.ytdSales').html(parseFloat(valuess[key].totSales));
										$('.ytdPurchases').html(parseFloat(valuess[key].totpurchase));
										$('.ytdexpenses').html(parseFloat(valuess[key].totexpenses));
										$('.ytdBalance').html(parseFloat(valuess[key].totbalance));
										$('.ytdSalestax').html(parseFloat(valuess[key].totSalesTax));
										$('.ytdPurchasetax').html(parseFloat(valuess[key].totPurchasetax));
										$('.ytdTax').html(parseFloat(valuess[key].totTax));
										$('.ytdexempted').html(parseFloat(valuess[key].totExempted));
										$('.ytdtcs').html(parseFloat(valuess[key].totTcsAmount));
										$('.ytdptcs').html(parseFloat(valuess[key].ptotTcsAmount));
										$('.ytdtds').html(parseFloat(valuess[key].totTdsAmount));
										$('.ytdCumTax').html(parseFloat(valuess[key].totTax));
									}
									});
								}
							});
						}else{
							$.each(values, function(keyss, valuess){
								if(keyss == 'summaryMap'){
									$.each(valuess, function(key, value){
										if(key != 'totals'){
												$('.sales'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.Sales)+"</span></span></li>");
												$('.purchase'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.Purchase)+"</span></span></li>");
												$('.expense'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.Expenses)+"</span></span></li>");
												$('.salestax'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.SalesTax)+"</span></span></li>");
												$('.purchasetax'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.PurchaseTax)+"</span></span></li>");
												$('.exempted'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.exempted)+"</span></span></li>");
												$('.tcs'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.tcsamount)+"</span></span></li>");
												$('.ptcs'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.ptcsamount)+"</span></span></li>");
												$('.tds'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.tdsamount)+"</span></span></li>");
												$('.cumtax'+key).append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(value.cummulativeTax)+"</span></span></li>");
										}else{
											$(".allclntsalesss").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +" </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totSales)+"</span></span></li>");
											$(".allclntpurchasesss").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totpurchase)+"</span></span></li>");
											$(".allclntexpensesss").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totexpenses)+"</span></span></li>");
											$(".allclntBalanceAmt").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totbalance)+"</span></span></li>");
											$(".allclntSalestaxAmt").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totSalesTax)+"</span></span></li>");
											$(".allclntPurchasetaxAmt").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totPurchasetax)+"</span></span></li>");
											$(".allclnttaxamt").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totTax)+"</span></span></li>");
											$(".allclntexemptedamt").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totExempted)+"</span></span></li>");
											$(".allclnttcstax").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totTcsAmount)+"</span></span></li>");
											$(".allclntptcstax").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].ptotTcsAmount)+"</span></span></li>");
											$(".allclnttdstax").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totTdsAmount)+"</span></span></li>");
											$(".allclntcumtax").append("<li class='clintlist'><span class='compname'>" + values.client.businessname +"  </span><br/><span class='gstn-phone'> "+values.client.gstnnumber+" :  <span class='ind_formatss'>"+parseFloat(valuess[key].totTax)+"</span></span></li>");
										}
									});
								}
							});
						}
						});
						OSREC.CurrencyFormatter.formatAll({selector: '.ind_formatss'});
						$('#processing').text('');
				},error:function(err){
			$('#processing').text('');
		}
			});
			
			$('#reportTable3').DataTable({
		"paging": false,
		scrollX: true,
		scrollCollapse: true,
		ordering: false,
		info: false,
		filter: false,
		"dom": '<"toolbar">frtip',  
		lengthChange: false,
		fixedColumns:{
			leftColumns:1,
			rightColumns:1
		}
	});
		}


</script>
</body></html>