package instantvas.smsengine.producersandconsumers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mutua.events.IEventLink;

/** <pre>
 * InstantVASEvent.java
 * ====================
 * (created by luiz, Mar 16, 2016)
 *
 * Annotation to allow {@link EInstantVASEvents} events propagation via an instance of {@link IEventLink}
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
*/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) 
public @interface InstantVASEvent {
	
	EInstantVASEvents[] value();
	
}