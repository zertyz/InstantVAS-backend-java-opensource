/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mutua.iccapp.HangmanSMSGame;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author luiz
 */
public class DateTimeHelper {

    public static boolean isValidDateTimeString(String dateTime) {
        System.out.println("DT: '"+dateTime+"' validity = "+dateTime.replaceAll("\\d\\d?/\\d\\d?/\\d\\d\\d?\\d?\\s+\\d\\d?:\\d\\d?:\\d\\d?", "§").equals("§"));
        return dateTime.replaceAll("\\d\\d?/\\d\\d?/\\d\\d\\d?\\d?\\s+\\d\\d?:\\d\\d?:\\d\\d?", "§").equals("§");
    }

    public static long getMillisFromDateTimeString(String dateTime) {
        Calendar date = Calendar.getInstance();
        date.setLenient(false);
        date.clear();

        String dateString = dateTime.substring(0, 10);
        String timeString = dateTime.substring(11, dateTime.length());

        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateString.substring(0, 2)));
        date.set(Calendar.MONTH,        Integer.parseInt(dateString.substring(3, 5))-1);
        date.set(Calendar.YEAR,         Integer.parseInt(dateString.substring(6, 10)));

        if(timeString.contains(":")){ //"22:00:00"
            date.set(Calendar.HOUR_OF_DAY,  Integer.parseInt(timeString.substring(0,   2)));
            date.set(Calendar.MINUTE,       Integer.parseInt(timeString.substring(3,   5)));
            date.set(Calendar.SECOND,       Integer.parseInt(timeString.substring(6,   8)));
        } else { //Hora e Minutos como inteiro 2200, 800
            int time = Integer.valueOf(timeString);
            date.set(Calendar.HOUR_OF_DAY,  time/100);
            date.set(Calendar.MINUTE,       time%100);
        }

        return date.getTime().getTime();
    }
    
    public static String getSimpleDateStringFromMillis(long millis) {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
		return sdf.format(new Date(millis));
    }

}
