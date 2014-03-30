<!DOCTYPE html>
<html lang="en">
<head>
<!--  styles -->
<link href="/~core/css/bootstrap.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="/~core/css/admin.css">
<link rel="stylesheet" type="text/css" href="/~core/css/style.css">
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.js"></script>
<script type="text/javascript" src="/~core/js/bootstrap.min.js"></script>

</head>
 
<body>
 
<div class="container">
 <header>
     <h1><span>App</span> <span> Cloud</span></h1>
 </header>
<!-------->
<div id="content">
    <ul id="tabs" class="nav nav-tabs" data-tabs="tabs">
        <li class="active"><a href="#app-upload" data-toggle="tab">App Upload</a></li>
        <li><a href="#app-history" data-toggle="tab">App History</a></li>
        <li><a href="#app-preview" data-toggle="tab">App Preview</a></li>
    </ul>
    <div id="my-tab-content" class="tab-content">
        <div class="tab-pane active" id="app-upload">
            <h3>App Upload</h3>
            
            	<form action="/admin/app/upload" method="post" enctype="multipart/form-data">
						<label for="app"></label><input type="file" name="app" id="app"/><br/><br/>
						<input type="submit" name="submit" value="Submit"/>
				</form>
	   			<div id="msg">${msg}</div>
	   			<h4>Static App</h4>
	   			<p id="help">
	   			An app may contain static files including html, scripts, styles, images, videos etc.
	   			Root folder should also contain a <a href="/~core/sample/lic.key">lic.key</a> to make it active in cloud.
	   			Package all the contents in to a zip file and upload to cloud. 
	   			</p>
	   			<h4>Dynamic App</h4>
	   			<p id="help">
	   			Future version of app cloud will allow server side javascripting to access any http resources(REST).
	   			</p>
        </div>
        <div class="tab-pane" id="app-history">
            <h3>App History</h3>
            <p></p>
        </div>
        <div class="tab-pane" id="app-preview">
            <h3>App Preview</h3>
            <p>Preview your app</p>
        </div>
      </div>
</div>
<footer>
<p>Ratheesh Kamoor</p>
<p>University Of Michigan-Dearborn</p>
<p>rkamoor@umich.edu</p>
<p>This is only a proof of concept project.</p>

</footer>
 
<script type="text/javascript">
    jQuery(document).ready(function ($) {
        $('#tabs').tab();
    });
</script> 

</div> <!-- container -->
 
 

 
</body>
</html>