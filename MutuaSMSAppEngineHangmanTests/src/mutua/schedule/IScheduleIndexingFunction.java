package mutua.schedule;

/** <pre>
 * IScheduleIndexingFunction.java
 * ==============================
 * (created by luiz, Jan 8, 2016)
 *
 * Implementations must know how to index event objects in order to identify
 * and differentiate between each one 
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public interface IScheduleIndexingFunction<EVENT_TYPE> {

	/** index functions must return a string that identifies each possible event, with the desired precision or flexibility */
	String getKey(EVENT_TYPE event);

}
