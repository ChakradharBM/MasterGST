<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<c:set var="varRetType" value="<%=MasterGSTConstants.GSTR2A%>"/>
<c:set var="varRetTypeCode" value='${varRetType.replaceAll(" ", "_")}'/>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Group Wise Sales Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<%@include file="/WEB-INF/views/includes/reports_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
 <link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/groupwisesalesreports.css" media="all" /> 

<script type="text/javascript">
$(document).ready(function(){$('.nonAspReports').addClass('active');});
</script>
<style>
.reportmenu:hover .dropdown-menu#reportdrop{display:block}
button#monthlydwnldxls ,#yearlydwnldxls,#customdwnldxls{margin-left: 0px;height: 30px;box-shadow:none;}
.td_headings{padding-top: 13px;border:none}
#multimonth_gstr3b, #reportTable4 , #reportTable4 th {border:none;overflow:hidden}
a.btn.btn-blue.reportbtn {
    float: right; color: #374583; padding: 5px 10px; font-size: 14px; font-weight: bold;position: absolute;
    right: 1.5%;
    top: 2.5%;
    z-index: 1;
}
#yearProcess, #customProcess {
    position: absolute;
    z-index: 9;
    font-size: 23px;
    left: 50%;
    color: red;
    width: 30em;
    height: 20px;
    margin-left: -15em;
    top: 320px;
}
</style>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cp_ClientsReports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Global Reports</a></li>
						<li class="breadcrumb-item active">GST Group Wise GSTR3B Reports</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div id="group_and_client" class="group_and_client">
		<div class="form-group" style="display:inline;">
			<select id="multeselectgroup" class="multeselectgroup multiselect-ui form-control" multiple="multiple">
			</select>
		</div>
		<div class="form-group" style="display:inline;">
			<select id="multeselectclient" class="multeselectclient multiselect-ui form-control" multiple="multiple">
			</select>
		</div>
	</div>
	<div class="db-ca-wrap monthely1">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/cp_ClientsReports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h2>
				<span class="reports-yearly">Group wise Yearly GSTR3B Report</span>
			</h2>
			<p>
				<span class="reports-yearly">Group wise Yearly GSTR3B Report gives you a summary of your quarterly and annual purchases.<span id="yearly_clients_errormsg"></span></span>
			</p>
				<div class="dropdown chooseteam mr-0" style="z-index:2;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
						<div class="typ-ret" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span id="filingoption" class="filingoption"	style="vertical-align: top;">Yearly</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
						</div>
					</span>
					<div class="dropdown-menu ret-type"	style="width: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
						<a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
					</div>
					<span class="yearly-sp"> 
						<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="filingoption1"	style="margin-right: 10px; display: inline-flex;">
							<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
							<div class="typ-ret type_ret_yearly" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
								<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
								<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
									<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
								</span>
							</div>
						</span>
						<div class="dropdown-menu ret-type1" id="financialYear1" style="width: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
						</div>
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px;margin-right: 3px;" onClick="getdiv()">Generate</button>
					</span>
				</div>
	<!-- <a href="#" class="btn btn-blue reportbtn">Export Report as Excel</a> -->
			<a id="downloadLink" class="btn btn-blue reportbtn mb-2 mt-3" onclick="exportF(this)" type="button">Download To excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
			<div id="yearProcess" class="text-center"></div>
			<c:set var="monthArray" value="${['4','5','6','7','8','9','10','11','12','1','2','3']}" />
			<div class="customtable db-ca-view reportTable reportTable4" style="margin-top:30px;">
			<table id="multimonth_gstr3b" border="1" style="width: 100%;">
			<tr><td class="td_headings" style="background-color:#f6f9fb!important;"><h5 style="text-align:center;">3.1 Tax on outward and reverse charge inward supplies</h5></td></tr>
			<tr><td><div class="containerdiv">
				<table id="reportTable4"  border="1" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
					<thead>
						<tr>
							<th class="text-center">Particulars</th>
							  <th class="text-center">April</th>
							  <th class="text-center">May</th>
							  <th class="text-center">June</th>
							  <th class="text-center">July</th>
							  <th class="text-center">August</th>
							  <th class="text-center">September</th>
							  <th class="text-center">October</th>
							  <th class="text-center">November</th>
							  <th class="text-center">December</th>
							  <th class="text-center">January</th>
							  <th class="text-center">February</th>
							  <th class="text-center">March</th>
							  <th class="text-center">Total</th>
						</tr>
					</thead>
					<tbody id="yeartotoalreport">
			  <tr>
					<td colspan="14"  style="background-color: aliceblue!important;"><h6 align="left">a) Outward taxable supplies (other than zero rated, nil rated and exempted)</h6> </td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Total Taxable Value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igst31a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigst31a">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgst31a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgst31a">0.00</td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgst31a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgst31a">0.00</td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cess31a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcess31a">0.00</td>
              </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">b) Outward taxable supplies (zero rated)</h6> </td>
	          </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Total taxable value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable31b${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable31b">0.00</td>
              </tr>
              <tr>
					<td><h6 class="gstr3binfo" align="right">Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igst31b${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigst31b">0.00</td>
              </tr>
              <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cess31b${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcess31b">0.00</td>
              </tr>
              <tr>
					<td colspan="14"style="background-color: aliceblue!important;"><h6 align="left">c) Other outward supplies (Nil rated, exempted)</h6> </td>
	          </tr>
              <tr>
					<td><h6 class="gstr3binfo" align="right">Total Taxable value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable31c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable31c">0.00</td>
              </tr>
              <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">d)Inward Supplies Liable to Reverse charge</h6> </td>
	          </tr>
              <tr>
					<td><h6 class="gstr3binfo" align="right">Total Taxable Value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable31d">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igst31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigst31d">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgst31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgst31d">0.00</td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgst31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgst31d">0.00</td>
              </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cess31d${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcess31d">0.00</td>
              </tr>
               <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">e)Non-GST Outward Supplies</h6> </td>
	          </tr>
               <tr>
					<td><h6 class="gstr3binfo" align="right">Total taxable value</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxable31e${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxable31e">0.00</td>
              </tr>
    				</tbody>
				</table>
				</div>
				</td></tr>
			<!-- </div>
			<div class="customtable db-ca-view reportTable reportTable4"> -->
			<tr><td class="td_headings"  style="background-color:#f6f9fb!important;"><h5 align="center">4.Eligible ITC</h5></td></tr>
			<tr><td><div class="containerdiv">	<table  border="1" id="reportTable4" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
				<thead>
			   <tr>
                 <th align="center">Particulars</th>
				  <th style="text-align: center;">April</th>
				  <th style="text-align: center;">May</th>
				  <th style="text-align: center;">June</th>
				  <th style="text-align: center;">July</th>
				  <th style="text-align: center;">August</th>
				  <th style="text-align: center;">September</th>
				  <th style="text-align: center;">October</th>
				  <th style="text-align: center;">November</th>
				  <th style="text-align: center;">December</th>
				  <th style="text-align: center;">January</th>
				  <th style="text-align: center;">February</th>
				  <th style="text-align: center;">March</th>  
                     <th style="text-align: center;">Total</th> 				  
                </tr>
              </thead>
              <tbody>
              <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">A)ITC Available (Whether in full or part)</h6> </td>
	          </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">1.Import of Goods</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="itcigstimpgoods${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytditcigstimpgoods">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">ii)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="itcimpgoodscess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytditccessimpgoods">0.00</td>
			   <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">1.Import of Services</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="itcigstimpservices${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytditcigstimpservices">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="itcimpservicescess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytditccessimpservices">0.00</td>
			  </tr>
			   <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">3.Inward supplies liable to reverse charge(other than 1 &2 above)</h6> </td>			
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="left">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitc4c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitc4c">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="left">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitc4c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitc4c">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitc4c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitc4c">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitc4c${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitc4c">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">4.Inward supplies from ISD</h6> </td>
			</tr>
			   <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcisd${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcisd">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcisd${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcisd">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcisd${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcisd">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcisd${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcisd">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">5.All other ITC</h6> </td>
			  </tr>
			   <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcother${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcother">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcother${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcother">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcother${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcother">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcother${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcother">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">B.ITC Reversed</h6> </td>
			  </tr>
			   <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6  align="left">1.As per Rule 42 & 43 of SGST/CGST rules</h6> </td>
			  </tr>
			   <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcrev${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcrev">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcrev${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcrev">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcrev${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcrev">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcrev${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcrev">0.00</td>
			  </tr>
			   <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">2.Others</h6> </td>
			  </tr>
			    <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcrevothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcrevothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcrevothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcrevothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcrevothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcrevothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcrevothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcrevothers">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">C) Net ITC Available (A-B)</h6> </td>
			  </tr>
			   <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcnet${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcnet">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcnet${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcnet">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcnet${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcnet">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcnet${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcnet">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">D) Ineligible ITC</h6> </td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">1) As per section 17(5) of CGST/SGST Act</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcinelg${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcinelg">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcinelg${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcinelg">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcinelg${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcinelg">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcinelg${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcinelg">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">2) Others</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">i)Integrated Tax(IGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="igstitcinelgothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdigstitcinelgothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">ii)Central Tax(CGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cgstitcinelgothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcgstitcinelgothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iii)State Tax(SGST)</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="sgstitcinelgothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdsgstitcinelgothers">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">iv)Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="cessitcinelgothers${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdcessitcinelgothers">0.00</td>
			  </tr>
              </tbody>
				</table>
				</div>
				</td></tr>
				<!-- </div>
			<div class="customtable db-ca-view reportTable reportTable4"> -->
			<tr><td class="td_headings"  style="background-color:#f6f9fb!important;"><h5 align="center">5. Values of exempt, Nil-rated and non-GST inward supplies</h5></td></tr>
			<tr><td><div class="containerdiv">
				<table  border="1" id="reportTable4" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
				<thead>
			   <tr>
                 <th align="center">Particulars</th>
				  <th style="text-align: center;">April</th>
				  <th style="text-align: center;">May</th>
				  <th style="text-align: center;">June</th>
				  <th style="text-align: center;">July</th>
				  <th style="text-align: center;">August</th>
				  <th style="text-align: center;">September</th>
				  <th style="text-align: center;">October</th>
				  <th style="text-align: center;">November</th>
				  <th style="text-align: center;">December</th>
				  <th style="text-align: center;">January</th>
				  <th style="text-align: center;">February</th>
				  <th style="text-align: center;">March</th>  
                   	<th style="text-align: center;">Total</th>			  
                </tr>
              </thead>
			<tbody>
			<tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">1) From a supplier under composition scheme, Exempt and Nil rated supply</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"class="gstr3binfo" align="right">i)Inter-state-supplie</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interstate51a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterstate51a">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">i)Intra-state-supplie</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interstate51a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterstate51a">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">2) Non GST supplies</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">i)Inter-state-supplie</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="nongstinterstate51a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdnongstinterstate51a">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo"align="right">ii)Intra-state-supplie</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="nongstintrastate51a${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdnongstintrastate51a">0.00</td>
			  </tr>
			</tbody>
			</table>
			</div>
			</td>
			</tr>
			
			
			
			<!-- Table 6 Offset Liability start-->
				

				<tr><td class="td_headings"  style="background-color:#f6f9fb!important;"><h5 align="center">6. Offset Liability</h5></td></tr>
				<tr><td><div class="containerdiv">	<table  border="1" id="reportTable4" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
				<thead>
			   <tr>
                 <th align="center">Particulars</th>
				  <th style="text-align: center;">April</th>
				  <th style="text-align: center;">May</th>
				  <th style="text-align: center;">June</th>
				  <th style="text-align: center;">July</th>
				  <th style="text-align: center;">August</th>
				  <th style="text-align: center;">September</th>
				  <th style="text-align: center;">October</th>
				  <th style="text-align: center;">November</th>
				  <th style="text-align: center;">December</th>
				  <th style="text-align: center;">January</th>
				  <th style="text-align: center;">February</th>
				  <th style="text-align: center;">March</th>  
                     <th style="text-align: center;">Total</th> 				  
                </tr>
              </thead>
              <tbody>
              <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">1) Other than reverse charge</h6> </td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Tax Payable</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxpayableigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxpayableigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxpayablecgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxpayablecgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxpayablesgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxpayablesgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxpayablecess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxpayablecess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Paid through ITC</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST using IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcigstusingigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcigstusingigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST using CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcigstusingcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcigstusingcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST using SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcigstusingsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcigstusingsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST using IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitccgstusingigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitccgstusingigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST using CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitccgstusingcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitccgstusingcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST using IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcsgstusingigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcsgstusingigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST using SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitcsgstusingsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitcsgstusingsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess using Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="paidthroughitccessusingcess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdpaidthroughitccessusingcess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Tax/Cess Paid in Cash</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxorcesspaidincashigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxorcesspaidincashigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxorcesspaidincashcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxorcesspaidincashcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxorcesspaidincashsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxorcesspaidincashsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="taxorcesspaidincashcess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdtaxorcesspaidincashcess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Interest Paid in Cash</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interestpaidincashigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterestpaidincashigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interestpaidincashcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterestpaidincashcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interestpaidincashsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterestpaidincashsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="interestpaidincashcess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdinterestpaidincashigstcess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.1 Late Fee Paid in Cash</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="latefeepaidincashcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdlatefeepaidincashcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="latefeepaidincashsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdlatefeepaidincashsgst">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">2. Reverse Charge</h6> </td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.2 Tax Payable</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxpayableigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxpayableigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxpayablecgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxpayablecgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxpayablesgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxpayablesgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxpayablecess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxpayablecess">0.00</td>
			  </tr>
			  <tr>
					<td colspan="14" style="background-color: aliceblue!important;"><h6 align="left">6.2 Tax/Cess Paid in Cash</h6> </td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">IGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxorcesspaidincashigst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxorcesspaidincashigst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">CGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxorcesspaidincashcgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxorcesspaidincashcgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">SGST</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxorcesspaidincashsgst${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxorcesspaidincashsgst">0.00</td>
			  </tr>
			  <tr>
					<td><h6 class="gstr3binfo" align="right">Cess</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ind_formatss" id="rctaxorcesspaidincashcess${month}"></td>
					</c:forEach>
					<td class="text-right ind_formatss" id="ytdrctaxorcesspaidincashcess">0.00</td>
			  </tr>
              </tbody>
				</table>
				</div>
				</td></tr>
			<!-- Table 6 Offset Liability end-->
			
			<!-- </div>
			-->
			</table>
			</div>
			

	</div>
</div>

	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
</body>
<script type="text/javascript">
var clientidsArray=new Array();
var groupArr =new Array();
var selectGroupArrayClientids= new Array();
var salesFileName;var gstnnumber='<c:out value="${client.gstnnumber}"/>';
var booksOrReturns = 'dwnldfromgstn';
var rtype = '${varRetTypeCode}';
	$(function() {
		var clientname = '<c:out value="${client.businessname}"/>';	var date = new Date();month = '<c:out value="${month}"/>';year = '<c:out value="${year}"/>';
		<c:forEach items="${listOfClients}" var="clients">	
			$(".multeselectclient").append($("<option></option>").attr("value","${clients.id}").html("${clients.businessname}- <span> ${clients.gstnnumber}</span>"));
		</c:forEach>
	var counts=0;var groupArray=[], selectGroupArray=[];
	<c:forEach items="${listOfClients}" var="group">
		<c:if test="${not empty group.groupName}">
			if(counts == 0){groupArray.push('<c:out value="${group.groupName}"/>');	$(".multeselectgroup").append($("<option></option>").attr("value","${group.groupName}").text("${group.groupName}"));}
			if(jQuery.inArray('<c:out value="${group.groupName}"/>', groupArray ) == -1){groupArray.push('<c:out value="${group.groupName}"/>');$(".multeselectgroup").append($("<option></option>").attr("value","${group.groupName}").text("${group.groupName}"));}
			counts++;
		</c:if>
	</c:forEach>
	
	function getGroupNameData(groupsnamelist){
		if(groupsnamelist == "GROUPS_NOTFOUND"){
			selectGroupArrayClientids= new Array();
			clientidsArray=new Array();
    		clientidsArray.push('CLIENTS_NOTFOUND');
			selectGroupArrayClientids.push('GROUPS_NOTFOUND');
    		var userid='<c:out value="${id}"/>';
    		$.ajax({
    			url: '${contextPath}/getclientsdata/'+userid,
    			type:'GET',
    			contentType: 'application/json',
    			success : function(data) {
    				$('.multeselectclient').empty().multiselect('rebuild');
	    			for(var i =0;i <=data.length-1;i++){
	    				$(".multeselectclient").append($("<option></option>").attr("value",data[i].firstname).text(data[i].businessname+"-"+data[i].gstnnumber));
	    			} 
	    			$('.multeselectclient').multiselect('rebuild');
	    		},error : function(data) {}
    		});
    	}else{
    		$.ajax({
    			url: '${contextPath}/getclientdata/'+groupsnamelist+'/<c:out value="${id}"/>',
    			type:'GET',
    			contentType: 'application/json',
    			success : function(data) {
    				selectGroupArrayClientids= new Array();
    				$('.multeselectclient').empty().multiselect('rebuild');
	    			for(var i =0;i <=data.length-1;i++){
	    				selectGroupArrayClientids.push(data[i].firstname);
	    			  	$(".multeselectclient").append($("<option></option>").attr("value",data[i].firstname).text(data[i].businessname+"-"+data[i].gstnnumber));
	    			} 
	    			$('.multeselectclient').multiselect('rebuild');
    			},error : function(data) {}
    		});
    	}
     }
    function applyGroup() {
		var groupArr =new Array(); var groupOptions = $('.multeselectgroup option:selected');
	    if(groupOptions.length > 0) {
	    	for(var i=0;i<groupOptions.length;i++) {groupArr.push(groupOptions[i].value);getGroupNameData(groupArr);}
		}else{groupArr.push('GROUPS_NOTFOUND');
		getGroupNameData(groupArr);}
    }
    $('.multeselectgroup').multiselect({
		nonSelectedText: '- Group Name -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {applyGroup();},
		onSelectAll: function() {applyGroup();},
		onDeselectAll: function() {applyGroup();}
	});
	function applyClient() {
		var clientArr =new Array();
	    var clientOptions = $('.multeselectclient option:selected');
    	if(clientOptions.length > 0) {
	    	for(var i=0;i<clientOptions.length;i++) {
	    		clientArr.push(clientOptions[i].value);	
	    		getlistOfClientids(clientArr);
			}
		}else{
			clientArr.push('CLIENTS_NOTFOUND');
			getlistOfClientids(clientArr);
		}
    }
	function getlistOfClientids(listofclients){
		clientidsArray=new Array();
		clientidsArray.push(listofclients);
	}
	$('.multeselectclient').multiselect({
		nonSelectedText: '- client/business Name -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyClient();
		},
		onSelectAll: function() {
			applyClient();
		},
		onDeselectAll: function() {applyClient();}
	});
	
	
});

$( document ).ready(function() {
	$('#multeselectclient').parent().css('width','172.81px');
});
var invoiceArray=new Object();
$('input.btaginput').tagsinput({tagClass : 'big',});
$(document).on('click', '.btn-remove-tag', function() {$('.bootstrap-tagsinput').html('');});
$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();
	$('.bootstrap-tagsinput').append('<span class="tag label label-info">' + t + '<span data-role="remove"></span></span>');
});

function getdiv() {
	var clientname = '<c:out value="${client.businessname}"/>';var abc = $('#filingoption').html();
	$('#multiselectGSTR14,#multiselectcustomGSTR14,#multiselectyearGSTR14').empty();$('#divFiltersGSTR1').html('');
if(abc == 'Monthly'){
	$('#GSTR1SummaryTable').css("display", "none");
	$('.reports-monthly').css("display", "block");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "none");
	$('span#fillingoption').css("vertical-align", "middle");
	var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
	salesFileName = 'MGST_Sales_Monthly_'+gstnnumber+'_'+mn+yr;
	var monthlydwnldurl_itemwise,monthlydwnldurl_invoicewise,monthlydwnldurl_fulldetails;
	var mnthUrl;
	if(selectGroupArrayClientids.length == 0 || selectGroupArrayClientids[0] == 'GROUPS_NOTFOUND'){
		if(clientidsArray.length == 0 || clientidsArray[0]== 'CLIENTS_NOTFOUND'){
			$('#monthly_clients_errormsg').html('Please select at least one client/business name').css('color','red');
			
		}else{
			loadGlobalReportsUsersByClient('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
			loadGlobalReportsInvoiceSupport(clientidsArray, '${varRetType}', '${varRetTypeCode}',mn, yr, abc, booksOrReturns,loadGlobalReportsCustomersInDropdown);
			loadGlobalReportsInvTable('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', mn, yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
			initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
			invsDownloads('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', mn , yr, '${usertype}', '${fullname}', booksOrReturns,'Monthly');
		}
	}else{
		if(clientidsArray.length > 0 && clientidsArray[0] != 'CLIENTS_NOTFOUND'){
			$('#monthly_clients_errormsg').html('');
			loadGlobalReportsUsersByClient('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
			loadGlobalReportsInvoiceSupport(clientidsArray, '${varRetType}', '${varRetTypeCode}',mn, yr, abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
			loadGlobalReportsInvTable('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', mn , yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
			initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
			invsDownloads('${id}', clientidsArray, '${varRetType}','${varRetTypeCode}', mn , yr, '${usertype}', '${fullname}', booksOrReturns,'Monthly');
		}else{
			$('#monthly_clients_errormsg').html('');
			loadGlobalReportsUsersByClient('${id}', selectGroupArrayClientids, '${varRetType}','${varRetTypeCode}', loadGlobalReportsUsersInDropDown);
			loadGlobalReportsInvoiceSupport(selectGroupArrayClientids, '${varRetType}', '${varRetTypeCode}',mn, yr, abc,booksOrReturns, loadGlobalReportsCustomersInDropdown);
			loadGlobalReportsInvTable('${id}', selectGroupArrayClientids, '${varRetType}','${varRetTypeCode}', mn , yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
			initiateCallBacksForMultiSelectGlobalReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersGlobalReports('${varRetType}','${varRetTypeCode}');
			invsDownloads('${id}', selectGroupArrayClientids, '${varRetType}','${varRetTypeCode}', mn , yr, '${usertype}', '${fullname}', booksOrReturns,'Monthly');
		}		
	}
}else if (abc == 'Yearly') {
	$('#GSTR1SummaryTable').css("display", "block");
	$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "block");$('.reports-custom').css("display", "none");
	$('span#filingoption').css("vertical-align", "bottom");		
		var fp = $('.yearlyoption').text();
		var fpsplit = fp.split(' - ');
		var yrs = parseInt(fpsplit[0]);
		var yrs1 = parseInt(fpsplit[0])+1;
		var year=$('#yearlyoption').html().split("-");
		salesFileName = 'MGST_Sales_Yearly_'+gstnnumber+'_'+yrs+'-'+yrs1;
		$(".reportTable4  div.toolbar").html('<h4>Financial Summary</h4>');
		var yearlydwnldurl_itemwise,yearlydwnldurl_invoicewise,yearlydwnldurl_fullDetails; 
		var yearlyUrl;		
		if(selectGroupArrayClientids.length == 0 || selectGroupArrayClientids[0] == 'GROUPS_NOTFOUND'){
			if(clientidsArray.length == 0 || clientidsArray[0]== 'CLIENTS_NOTFOUND'){
				$('#yearly_clients_errormsg').html('Please select at least one client/business name').css('color','red');
			}else{
				$('#yearly_clients_errormsg').html('');
				$.ajax({
					url: "${contextPath}/getadminGSTR3BYearlyinvs/${id}/GSTR3B/"+yrs+"?clientids="+clientidsArray,
					async: true,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(summary) {
						gstr3bsummary(summary);
					}
				});
			}
		}else{
			if(clientidsArray.length > 0 && clientidsArray[0] != 'CLIENTS_NOTFOUND'){
				$('#yearly_clients_errormsg').html('');
				$.ajax({
					url: "${contextPath}/getadminGSTR3BYearlyinvs/${id}/GSTR3B/"+yrs+"?clientids="+clientidsArray,
					async: true,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(summary) {
						gstr3bsummary(summary);
					}
				});
			}else{
				$('#yearly_clients_errormsg').html('');
				$.ajax({
					url: "${contextPath}/getadminGSTR3BYearlyinvs/${id}/GSTR3B/"+yrs+"?clientids="+selectGroupArrayClientids,
					async: true,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(summary) {
						gstr3bsummary(summary);
					}
				});
			}		
		}
	}
}
function gstr3bsummary(summarys){
	$('.ind_formatss').html(0.00);
	var ytdtax = 0.00;var ytdigst31a = 0.00;var ytdsgst31a = 0.00;var ytdcgst31a = 0.00; var ytdcess31a = 0.00;
	var ytdtax31b = 0.00;var ytdigst31b = 0.00;var ytdcess31b = 0.00;
	var ytdtax31c = 0.00;
	var ytdtax31d = 0.00;var ytdigst31d = 0.00;var ytdsgst31d = 0.00;var ytdcgst31d = 0.00; var ytdcess31d = 0.00;
	var ytdtax31e = 0.00;
	var ytditcimpgoodsigst = 0.00;var ytditcimpgoodscess = 0.00;
	var ytditcimpservicesigst = 0.00;var ytditcimpservicescess = 0.00;
	var ytdigstitc4c = 0.00;var ytdsgstitc4c = 0.00;var ytdcgstitc4c = 0.00; var ytdcessitc4c = 0.00;
	var ytdigstitcisd = 0.00;var ytdsgstitcisd = 0.00;var ytdcgstitcisd = 0.00; var ytdcessitcisd = 0.00;
	var ytdigstitcother = 0.00;var ytdsgstitcother = 0.00;var ytdcgstitcother = 0.00; var ytdcessitcother = 0.00;
	var ytdigstitcrev = 0.00;var ytdsgstitcrev = 0.00;var ytdcgstitcrev = 0.00; var ytdcessitcrev = 0.00;
	var ytdigstitcrevothers = 0.00;var ytdsgstitcrevothers = 0.00;var ytdcgstitcrevothers = 0.00; var ytdcessitcrevothers = 0.00;
	var ytdigstitcnet = 0.00;var ytdsgstitcnet = 0.00;var ytdcgstitcnet = 0.00; var ytdcessitcnet = 0.00;
	var ytdigstitcinelg = 0.00;var ytdsgstitcinelg = 0.00;var ytdcgstitcinelg = 0.00; var ytdcessitcinelg = 0.00;
	var ytdigstitcinelgothers = 0.00;var ytdsgstitcinelgothers = 0.00;var ytdcgstitcinelgothers = 0.00; var ytdcessitcinelgothers = 0.00;
	var ytdinterstate51a = 0.00;var ytdintrastate51a = 0.00;var ytdnongstinterstate51a = 0.00;var ytdnongstintrastate51a = 0.00;
	
	var ytdtaxpayableigst = 0.00;var ytdtaxpayablecgst = 0.00;var ytdtaxpayablesgst = 0.00;var ytdtaxpayablecess = 0.00;
	
	var ytdpaidthroughitcigstusingigst = 0.00;var ytdpaidthroughitcigstusingcgst = 0.00;var ytdpaidthroughitcigstusingsgst = 0.00;
	var ytdpaidthroughitccgstusingigst = 0.00;var ytdpaidthroughitccgstusingcgst = 0.00;var ytdpaidthroughitcsgstusingigst = 0.00;
	var ytdpaidthroughitcsgstusingsgst = 0.00;var ytdpaidthroughitccessusingcess = 0.00;
	
	var ytdtaxorcesspaidincashigst = 0.00;var ytdtaxorcesspaidincashcgst = 0.00;var ytdtaxorcesspaidincashsgst = 0.00;var ytdtaxorcesspaidincashcess = 0.00;
	var ytdinterestpaidincashigst = 0.00;var ytdinterestpaidincashcgst = 0.00;var ytdinterestpaidincashsgst = 0.00;var ytdinterestpaidincashcess = 0.00;
	var ytdlatefeepaidincashcgst = 0.00;var ytdlatefeepaidincashsgst = 0.00;
	
	var ytdrctaxpayableigst = 0.00;var ytdrctaxpayablecgst = 0.00;var ytdrctaxpayablesgst = 0.00;var ytdrctaxpayablecess = 0.00;
	var ytdrctaxorcesspaidincashigst = 0.00;var ytdrctaxorcesspaidincashcgst = 0.00;var ytdrctaxorcesspaidincashsgst = 0.00;var ytdrctaxorcesspaidincashcess = 0.00;
	for(var i =0;i <=summarys.length;i++){
	$.each(summarys[i], function(key, value){
		var gstr3bdet = JSON.parse(value);
		
		if(gstr3bdet != null){
			updatinggstr3bvalues('taxable'+key,gstr3bdet.supDetails.osupDet.txval);
			updatinggstr3bvalues('igst31a'+key,gstr3bdet.supDetails.osupDet.iamt);
			updatinggstr3bvalues('cgst31a'+key,gstr3bdet.supDetails.osupDet.camt);
			updatinggstr3bvalues('sgst31a'+key,gstr3bdet.supDetails.osupDet.samt);
			updatinggstr3bvalues('cess31a'+key,gstr3bdet.supDetails.osupDet.csamt);
			
			updatinggstr3bvalues('taxable31b'+key,gstr3bdet.supDetails.osupZero.txval);
			updatinggstr3bvalues('igst31b'+key,gstr3bdet.supDetails.osupZero.iamt);
			updatinggstr3bvalues('cess31b'+key,gstr3bdet.supDetails.osupZero.csamt);
			
			updatinggstr3bvalues('taxable31c'+key,gstr3bdet.supDetails.osupNilExmp.txval);
			
			updatinggstr3bvalues('taxable31d'+key,gstr3bdet.supDetails.isupRev.txval);
			updatinggstr3bvalues('igst31d'+key,gstr3bdet.supDetails.isupRev.iamt);
			updatinggstr3bvalues('cgst31d'+key,gstr3bdet.supDetails.isupRev.camt);
			updatinggstr3bvalues('sgst31d'+key,gstr3bdet.supDetails.isupRev.samt);
			updatinggstr3bvalues('cess31d'+key,gstr3bdet.supDetails.isupRev.csamt);
			
			updatinggstr3bvalues('taxable31e'+key,gstr3bdet.supDetails.osupNongst.txval);
			
			if(gstr3bdet.itcElg.itcAvl.length > 0){
				if(gstr3bdet.itcElg.itcAvl[0] != undefined){
					if(gstr3bdet.itcElg.itcAvl[0].ty == "IMPG"){
						updatinggstr3bvalues('itcigstimpgoods'+key,gstr3bdet.itcElg.itcAvl[0].iamt);
						updatinggstr3bvalues('itcimpgoodscess'+key,gstr3bdet.itcElg.itcAvl[0].csamt);
							if(gstr3bdet.itcElg.itcAvl[0].iamt !=null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt !=null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[0].ty == "IMPS"){
							updatinggstr3bvalues('itcigstimpservices'+key,gstr3bdet.itcElg.itcAvl[0].iamt);
							updatinggstr3bvalues('itcimpservicescess'+key,gstr3bdet.itcElg.itcAvl[0].csamt);
							if(gstr3bdet.itcElg.itcAvl[0].iamt !=null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt !=null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[0].ty == "ISRC"){
							updatinggstr3bvalues('igstitc4c'+key,gstr3bdet.itcElg.itcAvl[0].iamt);
							updatinggstr3bvalues('cgstitc4c'+key,gstr3bdet.itcElg.itcAvl[0].camt);
							updatinggstr3bvalues('sgstitc4c'+key,gstr3bdet.itcElg.itcAvl[0].samt);
							updatinggstr3bvalues('cessitc4c'+key,gstr3bdet.itcElg.itcAvl[0].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[0].iamt !=null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].camt !=null && gstr3bdet.itcElg.itcAvl[0].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[0].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].samt !=null && gstr3bdet.itcElg.itcAvl[0].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[0].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt !=null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[0].ty == "ISD"){
							updatinggstr3bvalues('igstitcisd'+key,gstr3bdet.itcElg.itcAvl[0].iamt);
							updatinggstr3bvalues('cgstitcisd'+key,gstr3bdet.itcElg.itcAvl[0].camt);
							updatinggstr3bvalues('sgstitcisd'+key,gstr3bdet.itcElg.itcAvl[0].samt);
							updatinggstr3bvalues('cessitcisd'+key,gstr3bdet.itcElg.itcAvl[0].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[0].iamt != null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].camt != null && gstr3bdet.itcElg.itcAvl[0].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[0].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].samt != null && gstr3bdet.itcElg.itcAvl[0].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[0].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt != null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[0].ty == "OTH"){
							updatinggstr3bvalues('igstitcother'+key,gstr3bdet.itcElg.itcAvl[0].iamt);
							updatinggstr3bvalues('cgstitcother'+key,gstr3bdet.itcElg.itcAvl[0].camt);
							updatinggstr3bvalues('sgstitcother'+key,gstr3bdet.itcElg.itcAvl[0].samt);
							updatinggstr3bvalues('cessitcother'+key,gstr3bdet.itcElg.itcAvl[0].csamt);
							if(gstr3bdet.itcElg.itcAvl[0].iamt != null && gstr3bdet.itcElg.itcAvl[0].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[0].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].camt != null && gstr3bdet.itcElg.itcAvl[0].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[0].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].samt != null && gstr3bdet.itcElg.itcAvl[0].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[0].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[0].csamt != null && gstr3bdet.itcElg.itcAvl[0].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[0].csamt);
							}
					}
					
				}
				if(gstr3bdet.itcElg.itcAvl[1] != undefined){
					if(gstr3bdet.itcElg.itcAvl[1].ty == "IMPG"){
							updatinggstr3bvalues('itcigstimpgoods'+key,gstr3bdet.itcElg.itcAvl[1].iamt);
							updatinggstr3bvalues('itcimpgoodscess'+key,gstr3bdet.itcElg.itcAvl[1].csamt);
							if(gstr3bdet.itcElg.itcAvl[1].iamt !=null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt !=null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[1].ty == "IMPS"){
							updatinggstr3bvalues('itcigstimpservices'+key,gstr3bdet.itcElg.itcAvl[1].iamt);
							updatinggstr3bvalues('itcimpservicescess'+key,gstr3bdet.itcElg.itcAvl[1].csamt);
							if(gstr3bdet.itcElg.itcAvl[1].iamt !=null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt !=null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[1].ty == "ISRC"){
							updatinggstr3bvalues('igstitc4c'+key,gstr3bdet.itcElg.itcAvl[1].iamt);
							updatinggstr3bvalues('cgstitc4c'+key,gstr3bdet.itcElg.itcAvl[1].camt);
							updatinggstr3bvalues('sgstitc4c'+key,gstr3bdet.itcElg.itcAvl[1].samt);
							updatinggstr3bvalues('cessitc4c'+key,gstr3bdet.itcElg.itcAvl[1].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[1].iamt !=null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].camt !=null && gstr3bdet.itcElg.itcAvl[1].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[1].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].samt !=null && gstr3bdet.itcElg.itcAvl[1].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[1].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt !=null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[1].ty == "ISD"){
							updatinggstr3bvalues('igstitcisd'+key,gstr3bdet.itcElg.itcAvl[1].iamt);
							updatinggstr3bvalues('cgstitcisd'+key,gstr3bdet.itcElg.itcAvl[1].camt);
							updatinggstr3bvalues('sgstitcisd'+key,gstr3bdet.itcElg.itcAvl[1].samt);
							updatinggstr3bvalues('cessitcisd'+key,gstr3bdet.itcElg.itcAvl[1].csamt);
							if(gstr3bdet.itcElg.itcAvl[1].iamt != null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].camt != null && gstr3bdet.itcElg.itcAvl[1].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[1].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].samt != null && gstr3bdet.itcElg.itcAvl[1].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[1].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt != null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[1].ty == "OTH"){
							updatinggstr3bvalues('igstitcother'+key,gstr3bdet.itcElg.itcAvl[1].iamt);
							updatinggstr3bvalues('cgstitcother'+key,gstr3bdet.itcElg.itcAvl[1].camt);
							updatinggstr3bvalues('sgstitcother'+key,gstr3bdet.itcElg.itcAvl[1].samt);
							updatinggstr3bvalues('cessitcother'+key,gstr3bdet.itcElg.itcAvl[1].csamt);
							if(gstr3bdet.itcElg.itcAvl[1].iamt != null && gstr3bdet.itcElg.itcAvl[1].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[1].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].camt != null && gstr3bdet.itcElg.itcAvl[1].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[1].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].samt != null && gstr3bdet.itcElg.itcAvl[1].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[1].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[1].csamt != null && gstr3bdet.itcElg.itcAvl[1].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[1].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcAvl[2] != undefined){
					if(gstr3bdet.itcElg.itcAvl[2].ty == "IMPG"){
							updatinggstr3bvalues('itcigstimpgoods'+key,gstr3bdet.itcElg.itcAvl[2].iamt);
							updatinggstr3bvalues('itcimpgoodscess'+key,gstr3bdet.itcElg.itcAvl[2].csamt);
							if(gstr3bdet.itcElg.itcAvl[2].iamt !=null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt !=null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[2].ty == "IMPS"){
							updatinggstr3bvalues('itcigstimpservices'+key,gstr3bdet.itcElg.itcAvl[2].iamt);
							updatinggstr3bvalues('itcimpservicescess'+key,gstr3bdet.itcElg.itcAvl[2].csamt);
							if(gstr3bdet.itcElg.itcAvl[2].iamt !=null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt !=null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[2].ty == "ISRC"){
							updatinggstr3bvalues('igstitc4c'+key,gstr3bdet.itcElg.itcAvl[2].iamt);
							updatinggstr3bvalues('cgstitc4c'+key,gstr3bdet.itcElg.itcAvl[2].camt);
							updatinggstr3bvalues('sgstitc4c'+key,gstr3bdet.itcElg.itcAvl[2].samt);
							updatinggstr3bvalues('cessitc4c'+key,gstr3bdet.itcElg.itcAvl[2].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[2].iamt !=null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].camt !=null && gstr3bdet.itcElg.itcAvl[2].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[2].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].samt !=null && gstr3bdet.itcElg.itcAvl[2].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[2].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt !=null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[2].ty == "ISD"){
							updatinggstr3bvalues('igstitcisd'+key,gstr3bdet.itcElg.itcAvl[2].iamt);
							updatinggstr3bvalues('cgstitcisd'+key,gstr3bdet.itcElg.itcAvl[2].camt);
							updatinggstr3bvalues('sgstitcisd'+key,gstr3bdet.itcElg.itcAvl[2].samt);
							updatinggstr3bvalues('cessitcisd'+key,gstr3bdet.itcElg.itcAvl[2].csamt);
							if(gstr3bdet.itcElg.itcAvl[2].iamt != null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].camt != null && gstr3bdet.itcElg.itcAvl[2].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[2].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].samt != null && gstr3bdet.itcElg.itcAvl[2].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[2].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt != null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[2].ty == "OTH"){
							updatinggstr3bvalues('igstitcother'+key,gstr3bdet.itcElg.itcAvl[2].iamt);
							updatinggstr3bvalues('cgstitcother'+key,gstr3bdet.itcElg.itcAvl[2].camt);
							updatinggstr3bvalues('sgstitcother'+key,gstr3bdet.itcElg.itcAvl[2].samt);
							updatinggstr3bvalues('cessitcother'+key,gstr3bdet.itcElg.itcAvl[2].csamt);
							if(gstr3bdet.itcElg.itcAvl[2].iamt != null && gstr3bdet.itcElg.itcAvl[2].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[2].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].camt != null && gstr3bdet.itcElg.itcAvl[2].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[2].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].samt != null && gstr3bdet.itcElg.itcAvl[2].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[2].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[2].csamt != null && gstr3bdet.itcElg.itcAvl[2].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[2].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcAvl[3] != undefined){
					if(gstr3bdet.itcElg.itcAvl[3].ty == "IMPG"){
							updatinggstr3bvalues('itcigstimpgoods'+key,gstr3bdet.itcElg.itcAvl[3].iamt);
							updatinggstr3bvalues('itcimpgoodscess'+key,gstr3bdet.itcElg.itcAvl[3].csamt);
							if(gstr3bdet.itcElg.itcAvl[3].iamt !=null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt !=null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[3].ty == "IMPS"){
							updatinggstr3bvalues('itcigstimpservices'+key,gstr3bdet.itcElg.itcAvl[3].iamt);
							updatinggstr3bvalues('itcimpservicescess'+key,gstr3bdet.itcElg.itcAvl[3].csamt);
							if(gstr3bdet.itcElg.itcAvl[3].iamt !=null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt !=null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[3].ty == "ISRC"){
							updatinggstr3bvalues('igstitc4c'+key,gstr3bdet.itcElg.itcAvl[3].iamt);
							updatinggstr3bvalues('cgstitc4c'+key,gstr3bdet.itcElg.itcAvl[3].camt);
							updatinggstr3bvalues('sgstitc4c'+key,gstr3bdet.itcElg.itcAvl[3].samt);
							updatinggstr3bvalues('cessitc4c'+key,gstr3bdet.itcElg.itcAvl[3].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[3].iamt !=null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].camt !=null && gstr3bdet.itcElg.itcAvl[3].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[3].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].samt !=null && gstr3bdet.itcElg.itcAvl[3].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[3].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt !=null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[3].ty == "ISD"){
							updatinggstr3bvalues('igstitcisd'+key,gstr3bdet.itcElg.itcAvl[3].iamt);
							updatinggstr3bvalues('cgstitcisd'+key,gstr3bdet.itcElg.itcAvl[3].camt);
							updatinggstr3bvalues('sgstitcisd'+key,gstr3bdet.itcElg.itcAvl[3].samt);
							updatinggstr3bvalues('cessitcisd'+key,gstr3bdet.itcElg.itcAvl[3].csamt);
							if(gstr3bdet.itcElg.itcAvl[3].iamt != null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].camt != null && gstr3bdet.itcElg.itcAvl[3].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[3].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].samt != null && gstr3bdet.itcElg.itcAvl[3].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[3].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt != null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[3].ty == "OTH"){
							updatinggstr3bvalues('igstitcother'+key,gstr3bdet.itcElg.itcAvl[3].iamt);
							updatinggstr3bvalues('cgstitcother'+key,gstr3bdet.itcElg.itcAvl[3].camt);
							updatinggstr3bvalues('sgstitcother'+key,gstr3bdet.itcElg.itcAvl[3].samt);
							updatinggstr3bvalues('cessitcother'+key,gstr3bdet.itcElg.itcAvl[3].csamt);
							if(gstr3bdet.itcElg.itcAvl[3].iamt != null && gstr3bdet.itcElg.itcAvl[3].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[3].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].camt != null && gstr3bdet.itcElg.itcAvl[3].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[3].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].samt != null && gstr3bdet.itcElg.itcAvl[3].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[3].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[3].csamt != null && gstr3bdet.itcElg.itcAvl[3].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[3].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcAvl[4] != undefined){
					if(gstr3bdet.itcElg.itcAvl[4].ty == "IMPG"){
							updatinggstr3bvalues('itcigstimpgoods'+key,gstr3bdet.itcElg.itcAvl[4].iamt);
							updatinggstr3bvalues('itcimpgoodscess'+key,gstr3bdet.itcElg.itcAvl[4].csamt);
							if(gstr3bdet.itcElg.itcAvl[4].iamt !=null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytditcimpgoodsigst = ytditcimpgoodsigst+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt !=null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytditcimpgoodscess =ytditcimpgoodscess+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);					
							}
					}
					if(gstr3bdet.itcElg.itcAvl[4].ty == "IMPS"){
							updatinggstr3bvalues('itcigstimpservices'+key,gstr3bdet.itcElg.itcAvl[4].iamt);
							updatinggstr3bvalues('itcimpservicescess'+key,gstr3bdet.itcElg.itcAvl[4].csamt);
							if(gstr3bdet.itcElg.itcAvl[4].iamt !=null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytditcimpservicesigst = ytditcimpservicesigst+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);						
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt !=null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytditcimpservicescess =ytditcimpservicescess+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[4].ty == "ISRC"){
							updatinggstr3bvalues('igstitc4c'+key,gstr3bdet.itcElg.itcAvl[4].iamt);
							updatinggstr3bvalues('cgstitc4c'+key,gstr3bdet.itcElg.itcAvl[4].camt);
							updatinggstr3bvalues('sgstitc4c'+key,gstr3bdet.itcElg.itcAvl[4].samt);
							updatinggstr3bvalues('cessitc4c'+key,gstr3bdet.itcElg.itcAvl[4].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[4].iamt !=null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytdigstitc4c = ytdigstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].camt !=null && gstr3bdet.itcElg.itcAvl[4].camt !=""){
								ytdcgstitc4c = ytdcgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[4].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].samt !=null && gstr3bdet.itcElg.itcAvl[4].samt !=""){
								ytdsgstitc4c = ytdsgstitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[4].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt !=null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytdcessitc4c = ytdcessitc4c+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[4].ty == "ISD"){
							updatinggstr3bvalues('igstitcisd'+key,gstr3bdet.itcElg.itcAvl[4].iamt);
							updatinggstr3bvalues('cgstitcisd'+key,gstr3bdet.itcElg.itcAvl[4].camt);
							updatinggstr3bvalues('sgstitcisd'+key,gstr3bdet.itcElg.itcAvl[4].samt);
							updatinggstr3bvalues('cessitcisd'+key,gstr3bdet.itcElg.itcAvl[4].csamt);
							if(gstr3bdet.itcElg.itcAvl[4].iamt != null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytdigstitcisd = ytdigstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].camt != null && gstr3bdet.itcElg.itcAvl[4].camt !=""){
								ytdcgstitcisd = ytdcgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[4].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].samt != null && gstr3bdet.itcElg.itcAvl[4].samt !=""){
								ytdsgstitcisd = ytdsgstitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[4].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt != null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytdcessitcisd = ytdcessitcisd+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);
							}
					}
					if(gstr3bdet.itcElg.itcAvl[4].ty == "OTH"){
							updatinggstr3bvalues('igstitcother'+key,gstr3bdet.itcElg.itcAvl[4].iamt);
							updatinggstr3bvalues('cgstitcother'+key,gstr3bdet.itcElg.itcAvl[4].camt);
							updatinggstr3bvalues('sgstitcother'+key,gstr3bdet.itcElg.itcAvl[4].samt);
							updatinggstr3bvalues('cessitcother'+key,gstr3bdet.itcElg.itcAvl[4].csamt);
							
							if(gstr3bdet.itcElg.itcAvl[4].iamt != null && gstr3bdet.itcElg.itcAvl[4].iamt !=""){
								ytdigstitcother = ytdigstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[4].iamt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].camt != null && gstr3bdet.itcElg.itcAvl[4].camt !=""){
								ytdcgstitcother = ytdcgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[4].camt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].samt != null && gstr3bdet.itcElg.itcAvl[4].samt !=""){
								ytdsgstitcother = ytdsgstitcother+parseFloat(gstr3bdet.itcElg.itcAvl[4].samt);
							}
							if(gstr3bdet.itcElg.itcAvl[4].csamt != null && gstr3bdet.itcElg.itcAvl[4].csamt !=""){
								ytdcessitcother = ytdcessitcother+parseFloat(gstr3bdet.itcElg.itcAvl[4].csamt);
							}
					}
				}
			}
			if(gstr3bdet.itcElg.itcRev.length > 0){
				if(gstr3bdet.itcElg.itcRev[0] != undefined){
					if(gstr3bdet.itcElg.itcRev[0].ty == "RUL"){
						updatinggstr3bvalues('igstitcrev'+key,gstr3bdet.itcElg.itcRev[0].iamt);
						updatinggstr3bvalues('cgstitcrev'+key,gstr3bdet.itcElg.itcRev[0].camt);
						updatinggstr3bvalues('sgstitcrev'+key,gstr3bdet.itcElg.itcRev[0].samt);
						updatinggstr3bvalues('cessitcrev'+key,gstr3bdet.itcElg.itcRev[0].csamt);
						
						if(gstr3bdet.itcElg.itcRev[0].iamt !=null && gstr3bdet.itcElg.itcRev[0].iamt !=""){
							ytdigstitcrev = ytdigstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[0].iamt);
						}
						if(gstr3bdet.itcElg.itcRev[0].camt !=null && gstr3bdet.itcElg.itcRev[0].camt !=""){
							ytdcgstitcrev = ytdcgstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[0].camt);
						}
						if(gstr3bdet.itcElg.itcRev[0].samt !=null && gstr3bdet.itcElg.itcRev[0].csamt !=""){
							ytdsgstitcrev = ytdsgstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[0].samt);
						}
						if(gstr3bdet.itcElg.itcRev[0].csamt !=null && gstr3bdet.itcElg.itcRev[0].csamt !=""){
							ytdcessitcrev = ytdcessitcrev+parseFloat(gstr3bdet.itcElg.itcRev[0].csamt);
						}
					}
					if(gstr3bdet.itcElg.itcRev[0].ty == "OTH"){
							updatinggstr3bvalues('igstitcrevothers'+key,gstr3bdet.itcElg.itcRev[0].iamt);
							updatinggstr3bvalues('cgstitcrevothers'+key,gstr3bdet.itcElg.itcRev[0].camt);
							updatinggstr3bvalues('sgstitcrevothers'+key,gstr3bdet.itcElg.itcRev[0].samt);
							updatinggstr3bvalues('cessitcrevothers'+key,gstr3bdet.itcElg.itcRev[0].csamt);
							
							if(gstr3bdet.itcElg.itcRev[0].iamt !=null && gstr3bdet.itcElg.itcRev[0].iamt !=""){
								ytdigstitcrevothers = ytdigstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[0].iamt);
							}
							if(gstr3bdet.itcElg.itcRev[0].camt !=null && gstr3bdet.itcElg.itcRev[0].camt !=""){
								ytdcgstitcrevothers = ytdcgstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[0].camt);
							}
							if(gstr3bdet.itcElg.itcRev[0].samt !=null && gstr3bdet.itcElg.itcRev[0].samt !=""){
								ytdsgstitcrevothers = ytdsgstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[0].samt);
							}
							if(gstr3bdet.itcElg.itcRev[0].csamtt !=null && gstr3bdet.itcElg.itcRev[0].csamt !=""){
								ytdcessitcrevothers = ytdcessitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[0].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcRev[1] != undefined){
					if(gstr3bdet.itcElg.itcRev[1].ty == "RUL"){
						updatinggstr3bvalues('igstitcrev'+key,gstr3bdet.itcElg.itcRev[1].iamt);
						updatinggstr3bvalues('cgstitcrev'+key,gstr3bdet.itcElg.itcRev[1].camt);
						updatinggstr3bvalues('sgstitcrev'+key,gstr3bdet.itcElg.itcRev[1].samt);
						updatinggstr3bvalues('cessitcrev'+key,gstr3bdet.itcElg.itcRev[1].csamt);
						
						if(gstr3bdet.itcElg.itcRev[1].iamt !=null && gstr3bdet.itcElg.itcRev[1].iamt !=""){
							ytdigstitcrev = ytdigstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[1].iamt);
						}
						if(gstr3bdet.itcElg.itcRev[1].camt !=null && gstr3bdet.itcElg.itcRev[1].camt !=""){
							ytdcgstitcrev = ytdcgstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[1].camt);
						}
						if(gstr3bdet.itcElg.itcRev[1].samt !=null && gstr3bdet.itcElg.itcRev[1].csamt !=""){
							ytdsgstitcrev = ytdsgstitcrev+parseFloat(gstr3bdet.itcElg.itcRev[1].samt);
						}
						if(gstr3bdet.itcElg.itcRev[1].csamt !=null && gstr3bdet.itcElg.itcRev[1].csamt !=""){
							ytdcessitcrev = ytdcessitcrev+parseFloat(gstr3bdet.itcElg.itcRev[1].csamt);
						}
					}
					if(gstr3bdet.itcElg.itcRev[1].ty == "OTH"){
						updatinggstr3bvalues('igstitcrevothers'+key,gstr3bdet.itcElg.itcRev[1].iamt);
						updatinggstr3bvalues('cgstitcrevothers'+key,gstr3bdet.itcElg.itcRev[1].camt);
						updatinggstr3bvalues('sgstitcrevothers'+key,gstr3bdet.itcElg.itcRev[1].samt);
						updatinggstr3bvalues('cessitcrevothers'+key,gstr3bdet.itcElg.itcRev[1].csamt);
						
						if(gstr3bdet.itcElg.itcRev[1].iamt !=null && gstr3bdet.itcElg.itcRev[1].iamt !=""){
							ytdigstitcrevothers = ytdigstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[1].iamt);
						}
						if(gstr3bdet.itcElg.itcRev[1].camt !=null && gstr3bdet.itcElg.itcRev[1].camt !=""){
							ytdcgstitcrevothers = ytdcgstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[1].camt);
						}
						if(gstr3bdet.itcElg.itcRev[1].samt !=null && gstr3bdet.itcElg.itcRev[1].samt !=""){
							ytdsgstitcrevothers = ytdsgstitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[1].samt);
						}
						if(gstr3bdet.itcElg.itcRev[1].csamtt !=null && gstr3bdet.itcElg.itcRev[1].csamt !=""){
							ytdcessitcrevothers = ytdcessitcrevothers+parseFloat(gstr3bdet.itcElg.itcRev[1].csamt);
						}
					}
				}
			}
			if(gstr3bdet.itcElg.itcNet != undefined){
				updatinggstr3bvalues('igstitcnet'+key,gstr3bdet.itcElg.itcNet.iamt);
				updatinggstr3bvalues('cgstitcnet'+key,gstr3bdet.itcElg.itcNet.camt);
				updatinggstr3bvalues('sgstitcnet'+key,gstr3bdet.itcElg.itcNet.samt);
				updatinggstr3bvalues('cessitcnet'+key,gstr3bdet.itcElg.itcNet.csamt);
				
				if(gstr3bdet.itcElg.itcNet.iamt !=null && gstr3bdet.itcElg.itcNet.iamt !=""){
					ytdigstitcnet = ytdigstitcnet+parseFloat(gstr3bdet.itcElg.itcNet.iamt);
				}
				if(gstr3bdet.itcElg.itcNet.camt !=null && gstr3bdet.itcElg.itcNet.camt !=""){
					ytdcgstitcnet = ytdcgstitcnet+parseFloat(gstr3bdet.itcElg.itcNet.camt);
				}
				if(gstr3bdet.itcElg.itcNet.samt !=null && gstr3bdet.itcElg.itcNet.samt !=""){
					ytdsgstitcnet = ytdsgstitcnet+parseFloat(gstr3bdet.itcElg.itcNet.samt);
				}
				if(gstr3bdet.itcElg.itcNet.csamt !=null && gstr3bdet.itcElg.itcNet.csamt !=""){
					ytdcessitcnet = ytdcessitcnet+parseFloat(gstr3bdet.itcElg.itcNet.csamt);
				}
			}
			if(gstr3bdet.itcElg.itcInelg.length > 0){
				if(gstr3bdet.itcElg.itcInelg[0] != undefined){
					if(gstr3bdet.itcElg.itcInelg[0].ty == "RUL"){
						updatinggstr3bvalues('igstitcinelg'+key,gstr3bdet.itcElg.itcInelg[0].iamt);
						updatinggstr3bvalues('cgstitcinelg'+key,gstr3bdet.itcElg.itcInelg[0].camt);
						updatinggstr3bvalues('sgstitcinelg'+key,gstr3bdet.itcElg.itcInelg[0].samt);
						updatinggstr3bvalues('cessitcinelg'+key,gstr3bdet.itcElg.itcInelg[0].csamt);
					
						if(gstr3bdet.itcElg.itcInelg[0].iamt !=null && gstr3bdet.itcElg.itcInelg[0].iamt !=""){
							ytdigstitcinelg = ytdigstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[0].iamt);
						}
						if(gstr3bdet.itcElg.itcInelg[0].camt !=null && gstr3bdet.itcElg.itcInelg[0].camt !=""){
							ytdcgstitcinelg = ytdcgstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[0].camt);
						}
						if(gstr3bdet.itcElg.itcInelg[0].samt !=null && gstr3bdet.itcElg.itcInelg[0].samt !=""){
							ytdsgstitcinelg = ytdsgstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[0].samt);
						}
						if(gstr3bdet.itcElg.itcInelg[0].csamt !=null && gstr3bdet.itcElg.itcInelg[0].csamt !=""){
							ytdcessitcinelg = ytdcessitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[0].csamt);
						}
					}
					if(gstr3bdet.itcElg.itcInelg[0].ty == "OTH"){
							updatinggstr3bvalues('igstitcinelgothers'+key,gstr3bdet.itcElg.itcInelg[0].iamt);
							updatinggstr3bvalues('cgstitcinelgothers'+key,gstr3bdet.itcElg.itcInelg[0].camt);
							updatinggstr3bvalues('sgstitcinelgothers'+key,gstr3bdet.itcElg.itcInelg[0].samt);
							updatinggstr3bvalues('cessitcinelgothers'+key,gstr3bdet.itcElg.itcInelg[0].csamt);
							
							if(gstr3bdet.itcElg.itcInelg[0].iamt !=null && gstr3bdet.itcElg.itcInelg[0].iamt !=""){
								ytdigstitcinelgothers = ytdigstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[0].iamt);
							}
							if(gstr3bdet.itcElg.itcInelg[0].camt !=null && gstr3bdet.itcElg.itcInelg[0].camt !=""){
								ytdcgstitcinelgothers = ytdcgstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[0].camt);
							}
							if(gstr3bdet.itcElg.itcInelg[0].samt !=null && gstr3bdet.itcElg.itcInelg[0].samt !=""){
								ytdsgstitcinelgothers = ytdsgstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[0].samt);
							}
							if(gstr3bdet.itcElg.itcInelg[0].csamt !=null && gstr3bdet.itcElg.itcInelg[0].csamt !=""){
								ytdcessitcinelgothers = ytdcessitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[0].csamt);
							}
					}
				}
				if(gstr3bdet.itcElg.itcInelg[1] != undefined){
					
					if(gstr3bdet.itcElg.itcInelg[1].ty == "RUL"){
						updatinggstr3bvalues('igstitcinelg'+key,gstr3bdet.itcElg.itcInelg[1].iamt);
						updatinggstr3bvalues('cgstitcinelg'+key,gstr3bdet.itcElg.itcInelg[1].camt);
						updatinggstr3bvalues('sgstitcinelg'+key,gstr3bdet.itcElg.itcInelg[1].samt);
						updatinggstr3bvalues('cessitcinelg'+key,gstr3bdet.itcElg.itcInelg[1].csamt);
						
						if(gstr3bdet.itcElg.itcInelg[1].iamt !=null && gstr3bdet.itcElg.itcInelg[1].iamt !=""){
							ytdigstitcinelg = ytdigstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[1].iamt);
						}
						if(gstr3bdet.itcElg.itcInelg[1].camt !=null && gstr3bdet.itcElg.itcInelg[1].camt !=""){
							ytdcgstitcinelg = ytdcgstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[1].camt);
						}
						if(gstr3bdet.itcElg.itcInelg[1].samt !=null && gstr3bdet.itcElg.itcInelg[1].samt !=""){
							ytdsgstitcinelg = ytdsgstitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[1].samt);
						}
						if(gstr3bdet.itcElg.itcInelg[1].csamt !=null && gstr3bdet.itcElg.itcInelg[1].csamt !=""){
							ytdcessitcinelg = ytdcessitcinelg+parseFloat(gstr3bdet.itcElg.itcInelg[1].csamt);
						}
					}
					if(gstr3bdet.itcElg.itcInelg[1].ty == "OTH"){
							updatinggstr3bvalues('igstitcinelgothers'+key,gstr3bdet.itcElg.itcInelg[1].iamt);
							updatinggstr3bvalues('cgstitcinelgothers'+key,gstr3bdet.itcElg.itcInelg[1].camt);
							updatinggstr3bvalues('sgstitcinelgothers'+key,gstr3bdet.itcElg.itcInelg[1].samt);
							updatinggstr3bvalues('cessitcinelgothers'+key,gstr3bdet.itcElg.itcInelg[1].csamt);
							
							if(gstr3bdet.itcElg.itcInelg[1].iamt !=null && gstr3bdet.itcElg.itcInelg[1].iamt !=""){
								ytdigstitcinelgothers = ytdigstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[1].iamt);
							}
							if(gstr3bdet.itcElg.itcInelg[1].camt !=null && gstr3bdet.itcElg.itcInelg[1].camt !=""){
								ytdcgstitcinelgothers = ytdcgstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[1].camt);
							}
							if(gstr3bdet.itcElg.itcInelg[1].samt !=null && gstr3bdet.itcElg.itcInelg[1].samt !=""){
								ytdsgstitcinelgothers = ytdsgstitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[1].samt);
							}
							if(gstr3bdet.itcElg.itcInelg[1].csamt !=null && gstr3bdet.itcElg.itcInelg[1].csamt !=""){
								ytdcessitcinelgothers = ytdcessitcinelgothers+parseFloat(gstr3bdet.itcElg.itcInelg[1].csamt);
							}
					}
				}
			}
			
			if(gstr3bdet.inwardSup.isupDetails.length > 0){
				if(gstr3bdet.inwardSup.isupDetails[0] != undefined){
					updatinggstr3bvalues('interstate51a'+key,gstr3bdet.inwardSup.isupDetails[0].inter);
					if(gstr3bdet.inwardSup.isupDetails[0].inter !=null && gstr3bdet.inwardSup.isupDetails[0].inter !=""){
						ytdinterstate51a = ytdinterstate51a+parseFloat(gstr3bdet.inwardSup.isupDetails[0].inter);
					}
					updatinggstr3bvalues('intrastate51a'+key,gstr3bdet.inwardSup.isupDetails[0].intra);
					if(gstr3bdet.inwardSup.isupDetails[0].intra !=null && gstr3bdet.inwardSup.isupDetails[0].intra !=""){
						ytdintrastate51a = ytdinterstate51a+parseFloat(gstr3bdet.inwardSup.isupDetails[0].intra);
					}
				}
				if(gstr3bdet.inwardSup.isupDetails[1] != undefined){
					updatinggstr3bvalues('nongstinterstate51a'+key,gstr3bdet.inwardSup.isupDetails[1].inter);
					if(gstr3bdet.inwardSup.isupDetails[1].inter !=null && gstr3bdet.inwardSup.isupDetails[1].inter !=""){
						ytdnongstinterstate51a = ytdnongstinterstate51a+parseFloat(gstr3bdet.inwardSup.isupDetails[1].inter);
					}
					updatinggstr3bvalues('nongstintrastate51a'+key,gstr3bdet.inwardSup.isupDetails[1].intra);
					if(gstr3bdet.inwardSup.isupDetails[1].intra !=null && gstr3bdet.inwardSup.isupDetails[1].intra !=""){
						ytdnongstintrastate51a = ytdnongstinterstate51a+parseFloat(gstr3bdet.inwardSup.isupDetails[1].intra);
					}
				}
			}
			if(gstr3bdet.supDetails.osupDet.txval !=null && gstr3bdet.supDetails.osupDet.txval !=""){
				ytdtax = ytdtax+parseFloat(gstr3bdet.supDetails.osupDet.txval);				
			}
			if(gstr3bdet.supDetails.osupDet.iamt !=null && gstr3bdet.supDetails.osupDet.iamt !=""){
				ytdigst31a = ytdigst31a+parseFloat(gstr3bdet.supDetails.osupDet.iamt);
			}
			if(gstr3bdet.supDetails.osupDet.camt !=null && gstr3bdet.supDetails.osupDet.camt !=""){
				ytdcgst31a = ytdcgst31a+parseFloat(gstr3bdet.supDetails.osupDet.camt);
			}
			if(gstr3bdet.supDetails.osupDet.samt !=null && gstr3bdet.supDetails.osupDet.samt !=""){
				ytdsgst31a = ytdsgst31a+parseFloat(gstr3bdet.supDetails.osupDet.samt);
			}
			if(gstr3bdet.supDetails.osupDet.csamt !=null && gstr3bdet.supDetails.osupDet.csamt !=""){
				ytdcess31a = ytdcess31a+parseFloat(gstr3bdet.supDetails.osupDet.csamt);
			}
			if(gstr3bdet.supDetails.osupZero.txval !=null && gstr3bdet.supDetails.osupZero.txval !=""){
				ytdtax31b = ytdtax31b+parseFloat(gstr3bdet.supDetails.osupZero.txval);
			}
			if(gstr3bdet.supDetails.osupZero.iamt !=null && gstr3bdet.supDetails.osupZero.iamt !=""){
				ytdigst31b = ytdigst31b+parseFloat(gstr3bdet.supDetails.osupZero.iamt);
			}
			if(gstr3bdet.supDetails.osupZero.csamt !=null && gstr3bdet.supDetails.osupZero.csamt !=""){
				ytdcess31b = ytdcess31b+parseFloat(gstr3bdet.supDetails.osupZero.csamt);
			}
			if(gstr3bdet.supDetails.osupNilExmp.txval !=null && gstr3bdet.supDetails.osupNilExmp.txval !=""){
				ytdtax31c = ytdtax31c+parseFloat(gstr3bdet.supDetails.osupNilExmp.txval);
			}
			if(gstr3bdet.supDetails.isupRev.txval !=null && gstr3bdet.supDetails.isupRev.txval !=""){
				ytdtax31d = ytdtax31d+parseFloat(gstr3bdet.supDetails.isupRev.txval);
			}
			if(gstr3bdet.supDetails.isupRev.iamt !=null && gstr3bdet.supDetails.isupRev.iamt !=""){
				ytdigst31d = ytdigst31d+parseFloat(gstr3bdet.supDetails.isupRev.iamt);
			}
			if(gstr3bdet.supDetails.isupRev.camt !=null && gstr3bdet.supDetails.isupRev.camt !=""){
				ytdcgst31d = ytdcgst31d+parseFloat(gstr3bdet.supDetails.isupRev.camt);
			}
			if(gstr3bdet.supDetails.isupRev.samt !=null && gstr3bdet.supDetails.isupRev.samt !=""){
				ytdsgst31d = ytdsgst31d+parseFloat(gstr3bdet.supDetails.isupRev.samt);
			}
			if(gstr3bdet.supDetails.isupRev.csamt !=null && gstr3bdet.supDetails.isupRev.csamt !=""){
				ytdcess31d = ytdcess31d+parseFloat(gstr3bdet.supDetails.isupRev.csamt);
			}
			if(gstr3bdet.supDetails.osupNongst.txval !=null && gstr3bdet.supDetails.osupNongst.txval != ""){
				ytdtax31e = ytdtax31e+parseFloat(gstr3bdet.supDetails.osupNongst.txval);
			}
			if(gstr3bdet.offLiab !=null){
				if(gstr3bdet.offLiab.taxPayable !=null){
					if(gstr3bdet.offLiab.taxPayable[0] != null  && gstr3bdet.offLiab.taxPayable[0].igst != null && gstr3bdet.offLiab.taxPayable[0].igst.tx !=null){
						updatinggstr3bvalues('taxpayableigst'+key,gstr3bdet.offLiab.taxPayable[0].igst.tx);
						ytdtaxpayableigst += parseFloat(gstr3bdet.offLiab.taxPayable[0].igst.tx);						
					}
					if(gstr3bdet.offLiab.taxPayable[0] != null  && gstr3bdet.offLiab.taxPayable[0].cgst != null && gstr3bdet.offLiab.taxPayable[0].cgst.tx !=null){
						updatinggstr3bvalues('taxpayablecgst'+key,gstr3bdet.offLiab.taxPayable[0].cgst.tx);
						ytdtaxpayablecgst += parseFloat(gstr3bdet.offLiab.taxPayable[0].cgst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[0] != null  && gstr3bdet.offLiab.taxPayable[0].sgst != null && gstr3bdet.offLiab.taxPayable[0].sgst.tx !=null){
						updatinggstr3bvalues('taxpayablesgst'+key,gstr3bdet.offLiab.taxPayable[0].sgst.tx);
						ytdtaxpayablesgst += parseFloat(gstr3bdet.offLiab.taxPayable[0].sgst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[0] != null  && gstr3bdet.offLiab.taxPayable[0].cess != null && gstr3bdet.offLiab.taxPayable[0].cess.tx !=null){
						updatinggstr3bvalues('taxpayablecess'+key,gstr3bdet.offLiab.taxPayable[0].cess.tx);
						ytdtaxpayablecess += parseFloat(gstr3bdet.offLiab.taxPayable[0].cess.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[1] != null  && gstr3bdet.offLiab.taxPayable[1].igst != null && gstr3bdet.offLiab.taxPayable[1].igst.tx !=null){
						updatinggstr3bvalues('rctaxpayableigst'+key,gstr3bdet.offLiab.taxPayable[1].igst.tx);
						ytdrctaxpayableigst += parseFloat(gstr3bdet.offLiab.taxPayable[1].igst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[1] != null  && gstr3bdet.offLiab.taxPayable[1].cgst != null && gstr3bdet.offLiab.taxPayable[1].cgst.tx !=null){
						updatinggstr3bvalues('rctaxpayablecgst'+key,gstr3bdet.offLiab.taxPayable[1].cgst.tx);
						ytdrctaxpayablecgst += parseFloat(gstr3bdet.offLiab.taxPayable[1].cgst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[1] != null  && gstr3bdet.offLiab.taxPayable[1].sgst != null && gstr3bdet.offLiab.taxPayable[1].sgst.tx !=null){
						updatinggstr3bvalues('rctaxpayablesgst'+key,gstr3bdet.offLiab.taxPayable[1].sgst.tx);
						ytdrctaxpayablesgst += parseFloat(gstr3bdet.offLiab.taxPayable[1].sgst.tx);
					}
					if(gstr3bdet.offLiab.taxPayable[1] != null  && gstr3bdet.offLiab.taxPayable[1].cess != null && gstr3bdet.offLiab.taxPayable[1].cess.tx !=null){
						updatinggstr3bvalues('rctaxpayablecess'+key,gstr3bdet.offLiab.taxPayable[1].cess.tx);
						ytdrctaxpayablecess += parseFloat(gstr3bdet.offLiab.taxPayable[1].cess.tx);
					}
				}
				if(gstr3bdet.offLiab.pditc !=null){
					if(gstr3bdet.offLiab.pditc.igstPdigst != null){
						updatinggstr3bvalues('paidthroughitcigstusingigst'+key,gstr3bdet.offLiab.pditc.igstPdigst);
						ytdpaidthroughitcigstusingigst += parseFloat(gstr3bdet.offLiab.pditc.igstPdigst);
					}
					if(gstr3bdet.offLiab.pditc.igstPdcgst != null){
						updatinggstr3bvalues('paidthroughitcigstusingcgst'+key,gstr3bdet.offLiab.pditc.igstPdcgst);
						ytdpaidthroughitcigstusingcgst += parseFloat(gstr3bdet.offLiab.pditc.igstPdcgst);
					}
					if(gstr3bdet.offLiab.pditc.igstPdsgst != null){
						updatinggstr3bvalues('paidthroughitcigstusingsgst'+key,gstr3bdet.offLiab.pditc.igstPdsgst);
						ytdpaidthroughitcigstusingsgst += parseFloat(gstr3bdet.offLiab.pditc.igstPdsgst);
					}
					if(gstr3bdet.offLiab.pditc.cgstPdigst != null){
						updatinggstr3bvalues('paidthroughitccgstusingigst'+key,gstr3bdet.offLiab.pditc.cgstPdigst);
						ytdpaidthroughitccgstusingigst += parseFloat(gstr3bdet.offLiab.pditc.cgstPdigst);
					}
					if(gstr3bdet.offLiab.pditc.cgstPdcgst != null){
						updatinggstr3bvalues('paidthroughitccgstusingcgst'+key,gstr3bdet.offLiab.pditc.cgstPdcgst);
						ytdpaidthroughitccgstusingcgst += parseFloat(gstr3bdet.offLiab.pditc.cgstPdcgst);
					}
					if(gstr3bdet.offLiab.pditc.sgstPdigst != null){
						updatinggstr3bvalues('paidthroughitcsgstusingigst'+key,gstr3bdet.offLiab.pditc.sgstPdigst);
						ytdpaidthroughitcsgstusingigst += parseFloat(gstr3bdet.offLiab.pditc.sgstPdigst);
					}
					if(gstr3bdet.offLiab.pditc.sgstPdsgst != null){
						updatinggstr3bvalues('paidthroughitcsgstusingsgst'+key,gstr3bdet.offLiab.pditc.sgstPdsgst);
						ytdpaidthroughitcsgstusingsgst += parseFloat(gstr3bdet.offLiab.pditc.sgstPdsgst);
					}
					if(gstr3bdet.offLiab.pditc.cessPdcess != null){
						updatinggstr3bvalues('paidthroughitccessusingcess'+key,gstr3bdet.offLiab.pditc.cessPdcess);
						ytdpaidthroughitccessusingcess += parseFloat(gstr3bdet.offLiab.pditc.cessPdcess);
					}
				}
				//gstr3bdet.offLiab.pdcash[0].ipd
				if(gstr3bdet.offLiab.pdcash !=null){
					if(gstr3bdet.offLiab.pdcash[0] != null && gstr3bdet.offLiab.pdcash[0].ipd !=null){
						updatinggstr3bvalues('taxorcesspaidincashigst'+key,gstr3bdet.offLiab.pdcash[0].ipd);
						ytdtaxorcesspaidincashigst += parseFloat(gstr3bdet.offLiab.pdcash[0].ipd);
					}
					if(gstr3bdet.offLiab.pdcash[1] != null && gstr3bdet.offLiab.pdcash[1].ipd !=null){
						updatinggstr3bvalues('rctaxorcesspaidincashigst'+key,gstr3bdet.offLiab.pdcash[1].ipd);
						ytdrctaxorcesspaidincashigst += parseFloat(gstr3bdet.offLiab.pdcash[1].ipd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cpd !=null){
						updatinggstr3bvalues('taxorcesspaidincashcgst'+key,gstr3bdet.offLiab.pdcash[0].cpd);
						ytdtaxorcesspaidincashcgst += parseFloat(gstr3bdet.offLiab.pdcash[0].cpd);
					}
					if(gstr3bdet.offLiab.pdcash[1] != null  && gstr3bdet.offLiab.pdcash[1].cpd !=null){
						updatinggstr3bvalues('rctaxorcesspaidincashcgst'+key,gstr3bdet.offLiab.pdcash[1].cpd);
						ytdrctaxorcesspaidincashcgst += parseFloat(gstr3bdet.offLiab.pdcash[1].cpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].spd !=null){
						updatinggstr3bvalues('taxorcesspaidincashsgst'+key,gstr3bdet.offLiab.pdcash[0].spd);
						ytdtaxorcesspaidincashsgst += parseFloat(gstr3bdet.offLiab.pdcash[0].spd);
					}
					if(gstr3bdet.offLiab.pdcash[1] != null  && gstr3bdet.offLiab.pdcash[1].spd !=null){
						$('#rctaxorcesspaidincashsgst'+key).html(gstr3bdet.offLiab.pdcash[1].spd);
						updatinggstr3bvalues('rctaxorcesspaidincashsgst'+key,gstr3bdet.offLiab.pdcash[1].spd);
						ytdrctaxorcesspaidincashsgst += parseFloat(gstr3bdet.offLiab.pdcash[1].spd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cspd !=null){
						updatinggstr3bvalues('taxorcesspaidincashcess'+key,gstr3bdet.offLiab.pdcash[0].cspd);
						ytdtaxorcesspaidincashcess += parseFloat(gstr3bdet.offLiab.pdcash[0].cspd);	
					}
					if(gstr3bdet.offLiab.pdcash[1] != null  && gstr3bdet.offLiab.pdcash[1].cspd !=null){
						updatinggstr3bvalues('rctaxorcesspaidincashcess'+key,gstr3bdet.offLiab.pdcash[1].cspd);
						ytdrctaxorcesspaidincashcess += parseFloat(gstr3bdet.offLiab.pdcash[1].cspd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].igstIntrpd !=null){
						updatinggstr3bvalues('interestpaidincashigst'+key,gstr3bdet.offLiab.pdcash[0].igstIntrpd);
						ytdinterestpaidincashigst += parseFloat(gstr3bdet.offLiab.pdcash[0].igstIntrpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cgstIntrpd !=null){
						updatinggstr3bvalues('interestpaidincashcgst'+key,gstr3bdet.offLiab.pdcash[0].cgstIntrpd);
						ytdinterestpaidincashcgst += parseFloat(gstr3bdet.offLiab.pdcash[0].cgstIntrpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].sgstIntrpd !=null){
						updatinggstr3bvalues('interestpaidincashsgst'+key,gstr3bdet.offLiab.pdcash[0].sgstIntrpd);
						ytdinterestpaidincashsgst += parseFloat(gstr3bdet.offLiab.pdcash[0].sgstIntrpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cessIntrpd !=null){
						updatinggstr3bvalues('interestpaidincashcess'+key,gstr3bdet.offLiab.pdcash[0].cessIntrpd);
						ytdinterestpaidincashcess += parseFloat(gstr3bdet.offLiab.pdcash[0].cessIntrpd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cgstLfeepd !=null){
						updatinggstr3bvalues('latefeepaidincashcgst'+key,gstr3bdet.offLiab.pdcash[0].cgstLfeepd);
						ytdlatefeepaidincashcgst += parseFloat(gstr3bdet.offLiab.pdcash[0].cgstLfeepd);
					}
					if(gstr3bdet.offLiab.pdcash[0] != null  && gstr3bdet.offLiab.pdcash[0].cgstLfeepd !=null){
						updatinggstr3bvalues('latefeepaidincashsgst'+key,gstr3bdet.offLiab.pdcash[0].sgstLfeepd);
						ytdlatefeepaidincashsgst += parseFloat(gstr3bdet.offLiab.pdcash[0].sgstLfeepd);
					}
				}	
			}
		}
	});
}
	$('#ytdtaxable').html(ytdtax);$('#ytdigst31a').html(ytdigst31a);$('#ytdcgst31a').html(ytdcgst31a);$('#ytdsgst31a').html(ytdsgst31a);$('#ytdcess31a').html(ytdcess31a);
	$('#ytdtaxable31b').html(ytdtax31b);$('#ytdigst31b').html(ytdigst31b);$('#ytdcess31b').html(ytdcess31b);
	$('#ytdtaxable31c').html(ytdtax31c);
	$('#ytdtaxable31d').html(ytdtax31d);$('#ytdigst31d').html(ytdigst31d);$('#ytdcgst31d').html(ytdcgst31d);$('#ytdsgst31d').html(ytdsgst31d);$('#ytdcess31d').html(ytdcess31d);
	$('#ytdtaxable31e').html(ytdtax31e);
	$('#ytditcigstimpgoods').html(ytditcimpgoodsigst);$('#ytditccessimpgoods').html(ytditcimpgoodscess);
	$('#ytditcigstimpservices').html(ytditcimpservicesigst);$('#ytditccessimpservices').html(ytditcimpservicescess);
	$('#ytdigstitc4c').html(ytdigstitc4c);$('#ytdcgstitc4c').html(ytdcgstitc4c);$('#ytdsgstitc4c').html(ytdsgstitc4c);$('#ytdcessitc4c').html(ytdcessitc4c);
	$('#ytdigstitcisd').html(ytdigstitcisd);$('#ytdcgstitcisd').html(ytdcgstitcisd);$('#ytdsgstitcisd').html(ytdsgstitcisd);$('#ytdcessitcisd').html(ytdcessitcisd);
	$('#ytdigstitcother').html(ytdigstitcother);$('#ytdcgstitcother').html(ytdcgstitcother);$('#ytdsgstitcother').html(ytdsgstitcother);$('#ytdcessitcother').html(ytdcessitcother);
	$('#ytdigstitcrev').html(ytdigstitcrev);$('#ytdcgstitcrev').html(ytdcgstitcrev);$('#ytdsgstitcrev').html(ytdsgstitcrev);$('#ytdcessitcrev').html(ytdcessitcrev);
	$('#ytdigstitcrevothers').html(ytdigstitcrevothers);$('#ytdcgstitcrevothers').html(ytdcgstitcrevothers);$('#ytdsgstitcrevothers').html(ytdsgstitcrevothers);$('#ytdcessitcrevothers').html(ytdcessitcrevothers);
	$('#ytdigstitcnet').html(ytdigstitcnet);$('#ytdcgstitcnet').html(ytdcgstitcnet);$('#ytdsgstitcnet').html(ytdsgstitcnet);$('#ytdcessitcnet').html(ytdcessitcnet);
	$('#ytdigstitcinelg').html(ytdigstitcinelg);$('#ytdcgstitcinelg').html(ytdcgstitcinelg);$('#ytdsgstitcinelg').html(ytdsgstitcinelg);$('#ytdcessitcinelg').html(ytdcessitcinelg);
	$('#ytdigstitcinelgothers').html(ytdigstitcinelgothers);$('#ytdcgstitcinelgothers').html(ytdcgstitcinelgothers);$('#ytdsgstitcinelgothers').html(ytdsgstitcinelgothers);$('#ytdcessitcinelgothers').html(ytdcessitcinelgothers);
	$('#ytdinterstate51a').html(ytdinterstate51a);$('#ytdintrastate51a').html(ytdintrastate51a);
	$('#ytdnongstinterstate51a').html(ytdinterstate51a);$('#ytdnongstintrastate51a').html(ytdnongstintrastate51a);
	
	$('#ytdtaxpayableigst').html(ytdtaxpayableigst);$('#ytdtaxpayablecgst').html(ytdtaxpayablecgst);$('#ytdtaxpayablesgst').html(ytdtaxpayablesgst);$('#ytdtaxpayablecess').html(ytdtaxpayablecess);
	
	$('#ytdpaidthroughitcigstusingigst').html(ytdpaidthroughitcigstusingigst);$('#ytdpaidthroughitcigstusingcgst').html(ytdpaidthroughitcigstusingcgst);$('#ytdpaidthroughitcigstusingsgst').html(ytdpaidthroughitcigstusingsgst);
	$('#ytdpaidthroughitccgstusingigst').html(ytdpaidthroughitccgstusingigst);$('#ytdpaidthroughitccgstusingcgst').html(ytdpaidthroughitccgstusingcgst);$('#ytdpaidthroughitcsgstusingigst').html(ytdpaidthroughitcsgstusingigst);
	$('#ytdpaidthroughitcsgstusingsgst').html(ytdpaidthroughitcsgstusingsgst);$('#ytdpaidthroughitccessusingcess').html(ytdpaidthroughitccessusingcess);
	
	$('#ytdtaxorcesspaidincashigst').html(ytdtaxorcesspaidincashigst);$('#ytdtaxorcesspaidincashcgst').html(ytdtaxorcesspaidincashcgst);$('#ytdtaxorcesspaidincashsgst').html(ytdtaxorcesspaidincashsgst);$('#ytdtaxorcesspaidincashcess').html(ytdtaxorcesspaidincashcess);
	$('#ytdinterestpaidincashigst').html(ytdinterestpaidincashigst);$('#ytdinterestpaidincashcgst').html(ytdinterestpaidincashcgst);$('#ytdinterestpaidincashsgst').html(ytdinterestpaidincashsgst);$('#ytdinterestpaidincashcess').html(ytdinterestpaidincashcess);
	$('#ytdlatefeepaidincashcgst').html(ytdlatefeepaidincashcgst);$('#ytdlatefeepaidincashsgst').html(ytdlatefeepaidincashsgst);
	
	$('#ytdrctaxpayableigst').html(ytdrctaxpayableigst);$('#ytdrctaxpayablecgst').html(ytdrctaxpayablecgst);$('#ytdrctaxpayablesgst').html(ytdrctaxpayablesgst);$('#ytdrctaxpayablecess').html(ytdrctaxpayablecess);
	$('#ytdrctaxorcesspaidincashigst').html(ytdrctaxorcesspaidincashigst);$('#ytdrctaxorcesspaidincashcgst').html(ytdrctaxorcesspaidincashcgst);$('#ytdrctaxorcesspaidincashsgst').html(ytdrctaxorcesspaidincashsgst);$('#ytdrctaxorcesspaidincashcess').html(ytdrctaxorcesspaidincashcess);
	
	OSREC.CurrencyFormatter.formatAll({
		selector: '.ind_formatss'
	});
}

function getval(sel) {
	if(sel){
		document.getElementById('filingoption').innerHTML = sel;
		if (sel == 'Custom') {
			$('#group_and_client').css("right","53%");
			$('.monthely-sp').css("display", "none");
			$('.yearly-sp').css("display", "none");
			$('.custom-sp').css("display", "inline-block");
			$('.dropdown-menu.ret-type').css("left", "16%");
		} else if (sel == 'Yearly') {
			$('.monthely-sp').css("display", "none");
			$('.yearly-sp').css("display", "inline-block");
			$('.custom-sp').css("display", "none");
			$('.dropdown-menu.ret-type').css("left", "19%");
		} else {
			$('#group_and_client').css("right","47%");
			$('.monthely-sp').css("display", "inline-block");
			$('.yearly-sp').css("display", "none");
			$('.custom-sp').css("display", "none");
			$('.dropdown-menu.ret-type').css("left", "19%"); 
		}
	}
}
function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;
}


function updatinggstr3bvalues(key,value){
	if(value != null && value != ""){
		var tax1 = $('#'+key).text();
		if(tax1 != ""){
			tax1 = parseFloat(tax1)+value;
			$('#'+key).html(tax1);
		}else{
			$('#'+key).html(value);
		}
	}
}
var table = $('table.display').DataTable({
  	"dom": '<"toolbar">frtip',
  	"buttons": [
  		{
	  		extend: 'excelHtml5',
	  		title: "test",
	  		filename: "testFile"
  		}
  		],
  	"pageLength": 8,
	 "responsive":false,
	 "ordering": false,
	 "searching": true,
	 "paging":true,
	 "bInfo" : true,
     "language": {
  	    "search": "_INPUT_",
        "searchPlaceholder": "Search...",
        "paginate": {
           "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
			"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
       }
     }
   }); 
   function exportF(elem) {
	  var table = document.getElementById("multimonth_gstr3b");
	  var html = table.outerHTML;
	  var url = 'data:application/vnd.ms-excel,' + escape(html); // Set your html table into url 
	  elem.setAttribute("href", url);
	  elem.setAttribute("download", "MGST_MULTI_MONTH_GSTR3B.xls"); // Choose the file name
	  return false;
}
</script>
<script type="text/javascript">
$(function(){
	var type='';
	var url_itemwise,url_invoicewise,url_fulldetails;
	var url='';
	$('.ind_formatss').html(0.00);
	OSREC.CurrencyFormatter.formatAll({
		selector: '.ind_formatss'
	});
});

</script>
<jsp:include page="/WEB-INF/views/reports/invoicedetails.jsp" />
</body>
</html>