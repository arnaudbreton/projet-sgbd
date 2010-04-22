<?php
define('CHEMIN_XSLT', '../../xslt');

class ParserXslt 
{
	public static function parse($nomXsl, $fluxXML)
	{
		$xslDoc = new DOMDocument();
		$xslDoc->load(CHEMIN_XSLT.'/'.$nomXsl);
		
		$xmlDoc = new DOMDocument();
		$xmlDoc->loadXML($fluxXML);
		
		$proc = new XSLTProcessor();		
		$proc->importStyleSheet($xslDoc);
		
		$result = $proc->transformToXML($xmlDoc);
		
		if(!$result)
		{
			echo libxml_get_last_error();
			return '';
		}
		else 
		{
			return $result;
		}
	}
}

?>