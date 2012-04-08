<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/">
    <PerformanceMonitor>
      <xsl:apply-templates />
    </PerformanceMonitor>
  </xsl:template>

  <xsl:template match="object">
    <object>

      <xsl:attribute name="class">
        <xsl:value-of select="substring-before(@name, ': ')" />
      </xsl:attribute>

      <xsl:attribute name="name">
        <xsl:value-of select="substring-after(@name, ': ')" />
      </xsl:attribute>

      <xsl:apply-templates />
    </object>
  </xsl:template>

  <xsl:template match="counter">
    <counter>

      <xsl:attribute name="name">
        <xsl:value-of select="@name" />
      </xsl:attribute>

      <xsl:attribute name="enabled">
        <xsl:value-of select="@enabled" />
      </xsl:attribute>

      <xsl:attribute name="val">

      </xsl:attribute>

    </counter>
  </xsl:template>

</xsl:stylesheet>
