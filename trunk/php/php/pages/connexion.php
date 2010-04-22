<?php
	session_start();

	require('./../../includes/functions.php');
	require('../dal/donneesBouchon.php');

	try {
		if(isset($_POST))
		{
			if(isset($_POST['txt_login']) && isset($_POST['txt_pwd']))
			{
				$donnees = new donneesBouchon();
				if($donnees->isLoginValid($_POST['txt_login'])) 
				{
					if($donnees->isPasswordValid($_POST['txt_login'],$_POST['txt_pwd']))
					{
						$_SESSION['login'] = $_POST['txt_login'];
						header('Location: ../../index.php');
					}
					else
					{
						throw new Exception('Le mot de passe est incorrect.');
					}
				}
				else
				{
					throw new Exception('Le login '. htmlspecialchars($_POST['txt_login']).' n\'existe pas.');
				}
			}	
			else
			{
				throw new Exception('Veuillez entrer un login et un mot de passe.');
			}
		}
		else
		{
			throw new Exception('Veuillez passer par le formulaire de connexion.');
		}
	}
	catch(Exception $e) 
	{
		creerPageErreur($e->getMessage(), htmlspecialchars($_POST['origine']));
	}
?>