package config;

/** <pre>
 * MutuaHardCodedConfiguration.java
 * ================================
 * (created by luiz, Apr 7, 2016)
 *
 * This class contains all "static final" variables used in hard-coding
 * configuration into classes and modules -- it is a reincarnation of the
 * old "Wage Mobile's Configuration Pattern", which uses "Class File Overwriting" 
 *
 * @version $Id$
 * @author luiz
*/

public class MutuaHardCodedConfiguration {
	
	// InstantVASNativeWebApplication & InstantVASWebApplication configurations
	///////////////////////////////////////////////////////////////////////////
	
	/** Set to true to have attended HTTP requests debugging information issued on the log files */
	public static final boolean IFDEF_WEB_DEBUG    = true;
	/** Set to true to dump details of configuration loading on the log files -- this may be undesired for licensed production versions, due to the 
	 *  ease this information brings to reverse-engineers */
	public static final boolean IFDEF_CONFIG_DEBUG = true;
	
	/** Set to true to use a 10x faster method to parse the HTTP GET query string, at the expense of requiring a fixed-ordered parameter/value declaration on that string */
	public static final boolean IFDEF_USE_STRICT_GET_PARSER = true;

	// TODO 18/5/2016 -- corrigir a arquitetura de Instancias: Para as considerações marcadas com o dia de hoje, cheia de IFDEFs e envolvendo vários módulos,
	//      poderem ser viáveis, uma implementação generalizada do Pattern 'MutuaHardCodedConfiguration' precisa ser definida e implantada.
	


}
