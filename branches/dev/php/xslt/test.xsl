<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" indent="yes"/> 

<xsl:template match="/">
		<xsl:apply-templates/>
</xsl:template >

<xsl:template match="personne" >

		<ul>

			<li>

			<xsl:value-of select="nom"/>

			-
			<xsl:value-of select="prenom"/>

			</li>

		</ul>

</xsl:template>
</xsl:stylesheet>