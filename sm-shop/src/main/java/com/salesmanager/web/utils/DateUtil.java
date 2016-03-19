/*
 * Licensed to csti consulting 
 * You may obtain a copy of the License at
 *
 * http://www.csticonsulting.com
 * Copyright (c) 2006-Aug 24, 2010 Consultation CS-TI inc. 
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.salesmanager.web.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesmanager.core.constants.Constants;



public class DateUtil {

	private Date startDate = new Date(new Date().getTime());
	private Date endDate = new Date(new Date().getTime());
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
	private final static String LONGDATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

	
	
	/**
	 * Generates a time stamp
	 * yyyymmddhhmmss
	 * @return
	 */
	public static String generateTimeStamp() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSS");
		return format.format(new Date());
	}
	
	/**
	 * yyyy-MM-dd
	 * 
	 * @param dt
	 * @return
	 */
	public static String formatDate(Date dt) {

		if (dt == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		return format.format(dt);

	}
	
	public static String formatYear(Date dt) {

		if (dt == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_YEAR);
		return format.format(dt);

	}
	
	public static String formatLongDate(Date date) {
		
		if (date == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat(LONGDATE_FORMAT);
		return format.format(date);
		
	}

	/**
	 * yy-MMM-dd
	 * 
	 * @param dt
	 * @return
	 */
	public static String formatDateMonthString(Date dt) {

		if (dt == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		return format.format(dt);

	}

	public static Date getDate(String date) throws Exception {
		DateFormat myDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		return myDateFormat.parse(date);
	}

	public static Date addDaysToCurrentDate(int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, days);
		return c.getTime();

	}

	public static Date getDate() {

		return new Date(new Date().getTime());

	}

	public static String getPresentDate() {

		Date dt = new Date();

		SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		return format.format(new Date(dt.getTime()));
	}

	public static String getPresentYear() {

		Date dt = new Date();

		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		return format.format(new Date(dt.getTime()));
	}

	public void processPostedDates(HttpServletRequest request) {
		Date dt = new Date();
		DateFormat myDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		Date sDate = null;
		Date eDate = null;
		try {
			if (request.getParameter("startdate") != null) {
				sDate = myDateFormat.parse(request.getParameter("startdate"));
			}
			if (request.getParameter("enddate") != null) {
				eDate = myDateFormat.parse(request.getParameter("enddate"));
			}
			this.startDate = sDate;
			this.endDate = eDate;
		} catch (Exception e) {
			LOGGER.error("",e);
			this.startDate = new Date(dt.getTime());
			this.endDate = new Date(dt.getTime());
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public Date getStartDate() {
		return startDate;
	}
	public static int getAgeRange(Date birtdate){
		int  year;
        
       
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		
        
       
        year= Integer.parseInt(format.format(new Date(birtdate.getTime())));
       
         Date d = new Date();
       
       
            
        /*Calculating the num of year, month and date*/
        
        year = (d.getYear() + 1900) - year - 1;
                
       
        
        if(year >=20 && year<30){
        	return 1;
        }else if(year >=30 && year<40){
        	return 2;
        }else if(year >=40 && year<50){
        	return 3;
        }else if(year >=50 )
        	return 4;
        else
        	return -1;
        	
        
	}
}
