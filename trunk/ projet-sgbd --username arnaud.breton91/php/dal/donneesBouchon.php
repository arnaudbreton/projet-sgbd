<?php

class donneesBouchon
{
	public function catalogue()
	{
	}
	
	public function isLoginValid($login)
	{
		return true;
	}
	
	public function isPasswordValid($login,$password)
	{
		return true;
	}
	
	public function userProfile($login) 
	{
		return 
		'<?xml version="1.0" encoding="utf-8"?>
		<profil>
			<nom>BRETON</nom>
			<prenom>Arnaud</prenom>
			<date_naissance>05/11/1988</date_naissance>
		</profil>
		';
	}
	
	public function userOrders($login)
	{
	}
	
	/**
	Retourne le numéro de commande crée
	**/
	public function order($login,$idArticle,$quantite)
	{
	}
	
	/**
	Lève une exception si erreur et retourne faux.
	Vrai sinon
	**/
	public function register($login,$password)
	{
	}
}

?>