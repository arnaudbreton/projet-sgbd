<?php
session_start();

include('/includes/MyTemplate.php');

$template = new MyTemplate('index.tpl');

$template->assign_block_vars('bloc_menu', array('LIBELLE_LIEN' => 'Consulter le catalogue', 'URL_LIEN' => './php/pages/catalogue.php'));

$template->createLoginMenu();			

$template->execute();
?>