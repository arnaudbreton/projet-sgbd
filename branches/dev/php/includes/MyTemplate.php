<?php 

require('C:/wamp/www/sgbd/includes/template.php');  

class MyTemplate extends Template 
{ 
    public function MyTemplate($tplContentName) 
    { 
        parent::__construct('C:/wamp/www/sgbd/templates/default');
		$this->init($tplContentName);
    }
	
	private function init($tplContentName) 
	{
		$this->set_filenames(array(
		'header' => 'header.tpl',
		'content' => $tplContentName,
		'footer' => 'footer.tpl'));
		
		$this->assign_vars(
			array(
				'TITRE' => 'Site client de l\'entreprise BHB',
				'CSS' => 'default.css',
				'DATE_JOUR' => date('d/m/Y')
			)
		);
	}
	
	public function createLoginMenu()
	{
		if(isset($_SESSION['login'])) 
		{
			$this->assign_block_vars('bloc_menu', array('LIBELLE_LIEN' => 'Consulter mes commandes', 'URL_LIEN' => './php/pages/commandes.php'));
			$this->assign_block_vars('bloc_menu',array('LIBELLE_LIEN' => 'Mon profil', 'URL_LIEN' => './php/pages/profil.php'));
			$this->assign_block_vars('bloc_menu',array('LIBELLE_LIEN' => 'Déconnexion', 'URL_LIEN' => './php/pages/deconnexion.php'));			
			$this->assign_block_vars('bloc_connected',array('LOGIN' => $_SESSION['login']));			
		}
		else
		{
			$this->assign_block_vars('bloc_notconnected', array());
		}		
	}

	public function execute() 
	{
		$this->display('header');
		$this->display('content');
		$this->display('footer');
	}

    public function getHtml($string) 
    {
        return htmlentities($string, ENT_QUOTES, 'ISO-8859-1'); 
    } 
} 

?> 