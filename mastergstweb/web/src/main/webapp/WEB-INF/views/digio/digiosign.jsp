<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<title>MasterGST | GST Software | Digio</title>
    <script src="${contextPath}/static/mastergst/js/jquery/jquery.min.js" type="text/javascript"></script>
	<script src="${contextPath}/static/mastergst/js/digio/digio.js" type="text/javascript"></script>
	
	<script type="text/javascript">
		var id,file_name,email;

		var options = {

			"callback": function(t){
						if(t.hasOwnProperty('error_code'))
						{
							document.getElementById("loading").style.display='none';
							document.getElementById("result").innerHTML="failed to sign with error : "+t.message;
						}
						else
						{ 
							document.getElementById("result").innerHTML="Sign Successful"
							success();
						}
			          },
			  "logo":  "https://www.mastergst.com/static/images/master/logo-mastergst.png"        
		};


		var uploadDoc = function()
		{	
			var digio = new Digio(options);
            digio.init();

			document.getElementById("result").innerHTML="";
			var file = document.getElementById('file').value;
			
			var postdata = {}
			postdata["file"] = '{\"abc\":\"test\",\"def\":\"asd\"}';
			postdata["email"] = 'ashok.s.samrat@gmail.com';

			$.ajax({
					type : "POST",
					contentType : "application/json",
					url : "http://localhost:8080/mastergst/site/uploaddoc",
					data : JSON.stringify(postdata),
					dataType : 'json',
					timeout : 100000,
			headers:{
			"Access-Control-Allow-Origin": "*",
			"Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS"
			},
			success : function(data) {
			console.log("SUCCESS: ", data.data);
				
				var obj = JSON.parse(data.data); 
				console.log("json: ", obj);
			      id = obj.id;
				  	console.log("id: ", id);
					
				  email=obj.signing_parties[0].email;
				  	console.log("email: ", email);
			      if(id)
			      {
						digio.esign(id, email);
						console.log("testts: ", "test");
			  	  }
			  	  else
			  	  {
			  	  		digio.cancel();
			  	  }
			}
		})
		}

		var failure = function(){
			document.getElementById("result").innerHTML="failed to upload document";
			document.getElementById("loading").style.display='none';
		}
		var success = function(){
			console.log('success');
		  	var xhttp = new XMLHttpRequest();
			xhttp.onreadystatechange = function() {
				if (xhttp.readyState == 4 && xhttp.status == 200) {
					console.log(xhttp.responseText);
			    	document.getElementById('link').innerHTML = xhttp.responseText;
			    	document.getElementById("loading").style.display='none';
			    }
			};
			xhttp.open('GET', 'files/downloadfile.php?id='+id+'&name='+file_name, true);	
			xhttp.send();
		}
	</script>

	
</head>
  <body>

<div id="loading"></div>

<input type="text" id="email" placeholder="Email Id" />
<input type="textarea" id="file"/>
<input type="button" id="result" value="eSign" onclick="uploadDoc()"/>


	
	
	
    <!-- header page end --><!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
  </body>
</html>