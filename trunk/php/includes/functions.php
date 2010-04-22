<?php 
	include('Template.php');

	function creerPageErreur($msg,$origine)
	{	
		$template = new Template('C:/wamp/www/sgbd/templates/default');
		$template->set_filenames(
			array(
			'erreur' => 'erreur.tpl')
		);
		
		$template->assign_vars(array(
										"MESSAGE" => $msg,
										"ORIGINE" => $origine
									)
							);
		
		$template->display('erreur');
	}
	
?>