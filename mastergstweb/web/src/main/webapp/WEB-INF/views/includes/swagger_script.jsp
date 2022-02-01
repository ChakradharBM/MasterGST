  <link href='static/swagger/css/typography.css' media='screen' rel='stylesheet' type='text/css'/>
  <link href='static/swagger/css/reset.css' media='screen' rel='stylesheet' type='text/css'/>
  <link href='static/swagger/css/screen.css' media='screen' rel='stylesheet' type='text/css'/>
  <link href='static/swagger/css/reset.css' media='print' rel='stylesheet' type='text/css'/>
  <link href='static/swagger/css/print.css' media='print' rel='stylesheet' type='text/css'/>
  <script src='static/swagger/lib/object-assign-pollyfill.js' type='text/javascript'></script>
  <script src='static/swagger/lib/jquery-1.8.0.min.js' type='text/javascript'></script>
  <script src='static/swagger/lib/jquery.slideto.min.js' type='text/javascript'></script>
  <script src='static/swagger/lib/jquery.wiggle.min.js' type='text/javascript'></script>
  <script src='static/swagger/lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
  <script src='static/swagger/lib/handlebars-4.0.5.js' type='text/javascript'></script>
  <script src='static/swagger/lib/lodash.min.js' type='text/javascript'></script>
  <script src='static/swagger/lib/backbone-min.js' type='text/javascript'></script>
  <script src='static/swagger/swagger-ui.min.js' type='text/javascript'></script>
  <script src='static/swagger/lib/highlight.9.1.0.pack.js' type='text/javascript'></script>
  <script src='static/swagger/lib/highlight.9.1.0.pack_extended.js' type='text/javascript'></script>
  <script src='static/swagger/lib/jsoneditor.min.js' type='text/javascript'></script>
  <script src='static/swagger/lib/marked.js' type='text/javascript'></script>
  <script src='static/swagger/lib/swagger-oauth.js' type='text/javascript'></script>
  <link  href='${contextPath}/static/swagger/lib/swagger-ui-3.css'  media='screen' rel='stylesheet' type='text/css'>
  <script src='${contextPath}/static/swagger/lib/swagger-ui-bundle.js' type='text/javascript'> </script>
  <script src='${contextPath}/static/swagger/lib/swagger-ui-standalone-preset.js' type='text/javascript'> </script> 
<script type="text/javascript">
var hiddenUserName1 = '<c:out value="${fullname}"/>';
var hiddenId = '<c:out value="${id}"/>';
var apiType = '<c:out value="${apiType}"/>';

    $(function () {
      var url = window.location.search.match(/url=([^&]+)/);
      if (url && url.length > 1) {
        url = decodeURIComponent(url[1]);
      } else {
        if(apiType == 'gst'){
    	  url = "${contextPath}/static/swagger/mastergst.json";
        }else if(apiType == 'eway'){
         url = "${contextPath}/static/swagger/mastergst_ewaybill.json";
        }else if(apiType == 'einv'){

        	window.onload = function() {
        				  const ui = SwaggerUIBundle({
        					url: "${contextPath}/static/swagger/mastergst_einvoice.json",
        					dom_id: '#swagger-ui-container',
        					deepLinking: true,
        					presets: [
        					  SwaggerUIBundle.presets.apis,
        					  SwaggerUIStandalonePreset
        					],
        					plugins: [
        					  SwaggerUIBundle.plugins.DownloadUrl
        					],
        					layout: "StandaloneLayout"
        				  })
        				  

        				  window.ui = ui
        				  
        				}
        }else{
    	  url = "${contextPath}/static/swagger/mastergst.json";
        }
      }

      hljs.configure({
        highlightSizeThreshold: 5000
      });

      // Pre load translate...
      if(window.SwaggerTranslator) {
        window.SwaggerTranslator.translate();
      }
      window.swaggerUi = new SwaggerUi({
        url: url,
		validatorUrl : null,
        dom_id: "swagger-ui-container",
        supportedSubmitMethods: ['get', 'post', 'put', 'delete', 'patch'],
        onComplete: function(swaggerApi, swaggerUi){
          if(typeof initOAuth == "function") {
            initOAuth({
              clientId: "your-client-id",
              clientSecret: "your-client-secret-if-required",
              realm: "your-realms",
              appName: "your-app-name",
              scopeSeparator: " ",
              additionalQueryStringParams: {}
            });
          }

          if(window.SwaggerTranslator) {
            window.SwaggerTranslator.translate();
          }
        },
        onFailure: function(data) {
          log("Unable to Load SwaggerUI");
        },
        docExpansion: "none",
        jsonEditor: false,
        defaultModelRendering: 'schema',
        showRequestHeaders: false,
        showOperationIds: false
      });

      window.swaggerUi.load();

      function log() {
        if ('console' in window) {
          console.log.apply(console, arguments);
        }
      }
  });
  
  function profile(){
	  alert("ASVSYSCY");
	  alert("hiddenId\t"+hiddenId);
	  alert("hiddenUserName1\t"+hiddenUserName1);
	  
	 $.ajax({
     type : "GET", 
     url : "credentials", 
     data : "id=" + hiddenId + "&fullname=" + hiddenUserName1,
     success : function(response) {
      alert(response); 
     },
     error : function(e) {
      alert('Error: ' + e); 
     }
    });
	  
  }
  
  </script>
