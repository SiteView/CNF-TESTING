<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/">
    <browse_data>
      <xsl:apply-templates />
    </browse_data>
  </xsl:template>

  <xsl:template match="object">
    <object>
      <xsl:attribute name="name">
        <xsl:value-of select="concat(@class, ': ', @name)" />
      </xsl:attribute>

      <xsl:apply-templates />
    </object>
  </xsl:template>

  <xsl:template match="counter">
    <counter>
      <xsl:attribute name="name">
        <xsl:value-of select="@name" />
      </xsl:attribute>

      <xsl:attribute name="value">
        <xsl:value-of select="@val" />
      </xsl:attribute>
    </counter>
  </xsl:template>

</xsl:stylesheet>
