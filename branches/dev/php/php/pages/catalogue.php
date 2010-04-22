<?php

session_start();

include('../../includes/MyTemplate.php');
include('../dal/parserXslt.php');

$template = new MyTemplate('catalogue.tpl');

$xmlTest = '<?xml version="1.0" encoding="utf-8"?>
<personnes>
<personne>
<nom>Pillou</nom>
<prenom>Jean</prenom>
</personne>
<personne>
<nom>VanHaute</nom>
<prenom>Nico</prenom>
</personne>
<personne>
<nom>Andrieu</nom>
<prenom>Seb</prenom>
</personne>
</personnes>';
$resultParse = ParserXslt::parse('test.xsl', $xmlTest);

$template->assign_block_vars('bloc_menu', array('LIBELLE_LIEN' => 'Accueil', 'URL_LIEN' => '../../index.php'));
$template->assign_var('CONTENU', $resultParse);

$template->createLoginMenu();		

$template->execute();

?>