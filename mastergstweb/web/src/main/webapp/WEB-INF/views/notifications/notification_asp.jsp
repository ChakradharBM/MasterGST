
<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Dashboard</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/notifications/notifications.css" media="all" />
<script type="text/javascript" src="${contextPath}/static/mastergst/js/echarts/echarts.js"></script>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/echarts/echartsTheme.js"></script>
</head>
<body>
<%@include file="/WEB-INF/views/includes/app_header.jsp" %>
<div class="bodywrap">
  <div class="db-inner">
  <div class="dashboard-inner">
  &nbsp;
<div class="db-ca-wrap">
    <div class="container">
      <!-- notifications  begin -->
      <div class="notify-wrap row">
        <!-- end -->
        <div class="notify-inbox col-sm-6">
        <c:if test="${not empty messages}"> 
          <ul>
           <c:forEach items="${messages}" var="message">
		  
            <li>
              <div class="inbox-info">
                <div class="inbox-info-label">
                <c:choose>
                	<c:when test="${messageId == message.msgId}">
                		<span class="notify-icons icon8"></span>	
                	</c:when>
                	<c:otherwise>
                		<span class="notify-icons icon14"></span>	
                	</c:otherwise>
                </c:choose>
                
                 </div>
                <div class="inbox-info-txt">
                  <h5><span class="msg_sub">${message.subject}</span> <span class="inbox-datetime"> <fmt:formatDate value="${message.createdDate}" pattern="MMM dd yyyy HH:mm:ss" /></span></h5>
                  <p><span class="msg_bdy">${message.message}</span></p>
                </div>
              </div>
            </li>
            </c:forEach>
          </ul>
          </c:if>
        </div>
        <!-- end -->
        <!-- The Modal -->
        <div id="notifyModal" class="custommodal  col-sm-6 col-xs-12">
          <!-- Modal content -->
          <div class="custommodal-content"><span class="customclose"><img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span>
            <div class="row">
              <div class="inbox-mail col-sm-12">
                <div class="inbox-mail-wrap">
                  <div class="user-block">  <h5 class="user-name-block"> <span class="user-name"></span> <span class="msgdate"></span></h5> </div>
                  
                  <div class="mail-txt">
                    <pre><span class="user-desc"></span></pre>
                  </div>
                </div>
                 
              </div>
            </div>
          </div>
        </div>
        <!-- Edit Modal End -->
        <!-- end -->
      </div>
    </div>
    <!-- notifications end -->
    <div class="clearfix">&nbsp;</div>
  </div>
 </div>
 </div>
 </div>
  <!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
  </body>
  <script type="text/javascript">
$(".notify-inbox li").on('click',function() {
	$('#notifyModal .user-name').text($(this).find('.msg_sub').text());
	$('#notifyModal .user-desc').text($(this).find('.msg_bdy').text());
	$('#notifyModal .msgdate').text($(this).find('.inbox-datetime').text());
	$('.inbox-info .icon8').addClass('icon14');
	$('.inbox-info .icon8').removeClass('icon8');
	$(this).find('.notify-icons').removeClass('icon14');
	$(this).find('.notify-icons').addClass('icon8');
	
	$('#notifyModal').addClass('custommodal-show');   
});
$('.customclose').click(function(){
	//$(this).parent().css('opacity',0);
	$('#notifyModal').removeClass('custommodal-show'); 
});
//$('.notify-inbox .sel').click();
</script>
</html>
