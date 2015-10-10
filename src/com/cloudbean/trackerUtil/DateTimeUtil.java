package com.cloudbean.trackerUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

import android.net.ParseException;
import android.util.Log;

public class DateTimeUtil {
	public static String subDateHour(String day, int x)//���ص����ַ����͵�ʱ�䣬����� 
	//��String day, int x 
	{    
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24Сʱ��   
	//�����������ʽҲ������ HH:mm:ss����HH:mm�ȵȣ�������ģ�����������������ʱ��Ҫ������ı� 
	//��day��ʽһ�� 
	        Date date = null;    
	        try {    
	            date = format.parse(day);    
	        } catch (Exception ex) {    
	            ex.printStackTrace();    
	        }    
	        if (date == null)    
	            return "";    
	        //System.out.println("front:" + format.format(date)); //��ʾ���������   
	        Calendar cal = Calendar.getInstance();    
	        cal.setTime(date);    
	        cal.add(Calendar.HOUR, -x);// 24Сʱ��    
	        date = cal.getTime();    
	        //System.out.println("after:" + format.format(date));  //��ʾ���º������  
	        cal = null;    
	        return format.format(date);    
	   
	}  
	
	
	public static long minBetweenPoint(String start, String end)
	{    
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24Сʱ��   
	//�����������ʽҲ������ HH:mm:ss����HH:mm�ȵȣ�������ģ�����������������ʱ��Ҫ������ı� 
	//��day��ʽһ�� 
	        Date date1 = null;    
	        Date date2 = null;
	        long min = 0;
	        try {    
	            date1 = format.parse(start);
	            date2 = format.parse(end);
	            long diff = date2.getTime()-date1.getTime();
	            min = diff/(1000*60);
	        } catch (Exception ex) {    
	            ex.printStackTrace();    
	        }        
	        return min;    
	   
	 } 

    /**  
     * ������(ʱ��)�е��ս��мӼ�����. <br>  
     * ����: <br>  
     * ���Date���͵�dΪ 2005��8��20��,��ô <br>  
     * calculateByDate(d,-10)��ֵΪ2005��8��10�� <br>  
     * ��calculateByDate(d,+10)��ֵΪ2005��8��30�� <br>  
     *   
     * @param d  
     *            ����(ʱ��).  
     * @param amount  
     *            �Ӽ�����ķ���.+n=��n��;-n=��n��.  
     * @return ����������(ʱ��).  
     */  
    public static Date calculateByDate(Date d, int amount) {   
        return calculate(d, GregorianCalendar.DATE, amount);   
    }   
       
    public static Date calculateByMinute(Date d, int amount) {   
        return calculate(d, GregorianCalendar.MINUTE, amount);   
    }   
       
    public static Date calculateByYear(Date d, int amount) {   
        return calculate(d, GregorianCalendar.YEAR, amount);   
    }   
  
    /**  
     * ������(ʱ��)����field����ָ�������ڳ�Ա���мӼ�����. <br>  
     * ����: <br>  
     * ���Date���͵�dΪ 2005��8��20��,��ô <br>  
     * calculate(d,GregorianCalendar.YEAR,-10)��ֵΪ1995��8��20�� <br>  
     * ��calculate(d,GregorianCalendar.YEAR,+10)��ֵΪ2015��8��20�� <br>  
     *   
     * @param d  
     *            ����(ʱ��).  
     * @param field  
     *            ���ڳ�Ա. <br>  
     *            ���ڳ�Ա��Ҫ��: <br>  
     *            ��:GregorianCalendar.YEAR <br>  
     *            ��:GregorianCalendar.MONTH <br>  
     *            ��:GregorianCalendar.DATE <br>  
     *            ʱ:GregorianCalendar.HOUR <br>  
     *            ��:GregorianCalendar.MINUTE <br>  
     *            ��:GregorianCalendar.SECOND <br>  
     *            ����:GregorianCalendar.MILLISECOND <br>  
     * @param amount  
     *            �Ӽ�����ķ���.+n=��n���ɲ���fieldָ�������ڳ�Աֵ;-n=��n���ɲ���field��������ڳ�Աֵ.  
     * @return ����������(ʱ��).  
     */  
    private static Date calculate(Date d, int field, int amount) {   
        if (d == null)   
            return null;   
        GregorianCalendar g = new GregorianCalendar();   
        g.setGregorianChange(d);   
        g.add(field, amount);   
        return g.getTime();   
    }   
  
    /**  
     * ����(ʱ��)ת��Ϊ�ַ���.  
     *   
     * @param formater  
     *            ���ڻ�ʱ��ĸ�ʽ.  
     * @param aDate  
     *            java.util.Date���ʵ��.  
     * @return ����ת������ַ���.  
     */  
    public static String date2String(String formater, Date aDate) {   
        if (formater == null || "".equals(formater))   
            return null;   
        if (aDate == null)   
            return null;   
        return (new SimpleDateFormat(formater)).format(aDate);   
    }   
  
    /**  
     * ��ǰ����(ʱ��)ת��Ϊ�ַ���.  
     *   
     * @param formater  
     *            ���ڻ�ʱ��ĸ�ʽ.  
     * @return ����ת������ַ���.  
     */  
    public static String date2String(String formater) {   
        return date2String(formater, new Date());   
    }   
       
    /**  
     * ��ȡ��ǰ���ڶ�Ӧ��������.  
     * <br>1=������,2=����һ,3=���ڶ�,4=������,5=������,6=������,7=������  
     * @return ��ǰ���ڶ�Ӧ��������  
     */  
    public static int dayOfWeek() {   
        GregorianCalendar g = new GregorianCalendar();   
        int ret = g.get(java.util.Calendar.DAY_OF_WEEK);   
        g = null;   
        return ret;   
    }   
  
  
    /**  
     * ��ȡ���е�ʱ�����. <br>  
     * �������:����ASCII�ַ��������������. <br>  
     * ����ʱ������ַ���Сд.  
     *   
     * @return ���е�ʱ�����(ʱ������Ѿ������ַ�[���Դ�Сд]����).  
     */  
    public static String[] fecthAllTimeZoneIds() {   
        Vector v = new Vector();   
        String[] ids = TimeZone.getAvailableIDs();   
        for (int i = 0; i < ids.length; i++) {   
            v.add(ids[i]);   
        }   
        java.util.Collections.sort(v, String.CASE_INSENSITIVE_ORDER);   
        v.copyInto(ids);   
        v = null;   
        return ids;   
    }   
  
    /**  
     * ���Ե�main����.  
     *   
     * @param argc  
     */  
    public static void main(String[] argc) {   
           
        String[] ids = fecthAllTimeZoneIds();   
        String nowDateTime =date2String("yyyy-MM-dd HH:mm:ss");   
        System.out.println("The time Asia/Shanhai is " + nowDateTime);//���򱾵���������ʱ��Ϊ[Asia/Shanhai]   
        //��ʾ����ÿ��ʱ����ǰ��ʵ��ʱ��   
        for(int i=0;i <ids.length;i++){   
            System.out.println(" * " + ids[i] + "=" + string2TimezoneDefault(nowDateTime,ids[i]));    
        }   
        //��ʾ�����������ڵص�ʱ��   
        System.out.println("TimeZone.getDefault().getID()=" +TimeZone.getDefault().getID());   
    }   
  
    /**  
     * ������ʱ���ַ�������ת��Ϊָ��ʱ��������ʱ��.  
     *   
     * @param srcFormater  
     *            ��ת��������ʱ��ĸ�ʽ.  
     * @param srcDateTime  
     *            ��ת��������ʱ��.  
     * @param dstFormater  
     *            Ŀ�������ʱ��ĸ�ʽ.  
     * @param dstTimeZoneId  
     *            Ŀ���ʱ�����.  
     *   
     * @return ת���������ʱ��.  
     */  
    public static String string2Timezone(String srcFormater,   
            String srcDateTime, String dstFormater, String dstTimeZoneId) {   
        if (srcFormater == null || "".equals(srcFormater))   
            return null;   
        if (srcDateTime == null || "".equals(srcDateTime))   
            return null;   
        if (dstFormater == null || "".equals(dstFormater))   
            return null;   
        if (dstTimeZoneId == null || "".equals(dstTimeZoneId))   
            return null;   
        SimpleDateFormat sdf = new SimpleDateFormat(srcFormater);   
        try {   
            int diffTime = getDiffTimeZoneRawOffset(dstTimeZoneId);   
            Date d = sdf.parse(srcDateTime);   
            long nowTime = d.getTime();   
            long newNowTime = nowTime - diffTime;   
            d = new Date(newNowTime);   
            return date2String(dstFormater, d);   
        } catch (Exception e) {   
           e.printStackTrace();
            return null;   
        } finally {   
            sdf = null;   
        }   
    }   
  
    /**  
     * ��ȡϵͳ��ǰĬ��ʱ����UTC��ʱ���.(��λ:����)  
     *   
     * @return ϵͳ��ǰĬ��ʱ����UTC��ʱ���.(��λ:����)  
     */  
    private static int getDefaultTimeZoneRawOffset() {   
        return TimeZone.getDefault().getRawOffset();   
    }   
  
    /**  
     * ��ȡָ��ʱ����UTC��ʱ���.(��λ:����)  
     *   
     * @param timeZoneId  
     *            ʱ��Id  
     * @return ָ��ʱ����UTC��ʱ���.(��λ:����)  
     */  
    private static int getTimeZoneRawOffset(String timeZoneId) {   
        return TimeZone.getTimeZone(timeZoneId).getRawOffset();   
    }   
  
    /**  
     * ��ȡϵͳ��ǰĬ��ʱ����ָ��ʱ����ʱ���.(��λ:����)  
     *   
     * @param timeZoneId  
     *            ʱ��Id  
     * @return ϵͳ��ǰĬ��ʱ����ָ��ʱ����ʱ���.(��λ:����)  
     */  
    private static int getDiffTimeZoneRawOffset(String timeZoneId) {   
        return TimeZone.getDefault().getRawOffset()   
                - TimeZone.getTimeZone(timeZoneId).getRawOffset();   
    }   
  
    /**  
     * ������ʱ���ַ�������ת��Ϊָ��ʱ��������ʱ��.  
     *   
     * @param srcDateTime  
     *            ��ת��������ʱ��.  
     * @param dstTimeZoneId  
     *            Ŀ���ʱ�����.  
     *   
     * @return ת���������ʱ��.  
     * @see #string2Timezone(String, String, String, String)  
     */  
    public static String string2TimezoneDefault(String srcDateTime,   
            String dstTimeZoneId) {   
        return string2Timezone("yyyy-MM-dd HH:mm:ss", srcDateTime,   
                "yyyy-MM-dd HH:mm:ss", dstTimeZoneId);   
    }   
  

}
