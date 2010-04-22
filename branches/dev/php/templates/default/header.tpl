<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fr" >
   <head>
       <title>{TITRE}</title>
	   <link rel="stylesheet" media="screen" type="text/css" title="Design" href="/sgbd/css/{CSS}" />
       <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
   </head>
   <body>
   <div id="top" />
   
   <div id="links">
		<div id="menu">
			<!-- BEGIN bloc_menu-->
				<div class="link">
					<a href="{bloc_menu.URL_LIEN}" title="{bloc_menu.LIBELLE_LIEN}">{bloc_menu.LIBELLE_LIEN}</a>
				</div>
			<!-- END bloc_menu -->
		</div>
   </div>
   
   <div id="left">
		<div id="compte">
				<!-- BEGIN bloc_notconnected-->
					<div id="connexion">
						<form method="post" action="./php/pages/connexion.php">
							<fieldset>	
								<legend>Mon compte</legend>
									<label for="txt_login">Login :</label>
									<input name="txt_login" type="text" class="txt_login" />
									<label for="txt_pwd">Password :</label>
									<input name="txt_pwd" type="text" class="txt_pwd" />
									
									<input type="hidden" name="origine" value="{URL_PAGE}" />
									
									<input type="submit" value="Connexion" class="btn_connexion" />
							</fieldset>
						</form>
					</div>
				<!-- END bloc_notconnected -->
				
				<!-- BEGIN bloc_connected -->
					<div id="profil">
						Bonjour {bloc_menu.bloc_connected.LOGIN} <br/>
						Nous sommes le {DATE_JOUR}
					</div>
				<!-- END bloc_connected -->
		</div>
   </div>
   
   <div id="content">