/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

/**
 *
 * @author sam20
 */
import java.sql.Timestamp;


public class history {
	private String UserId;
	private Timestamp fdate;
	private Timestamp tdate;
	private int interval;
	private String start;
	private String stop;
	private String BikeUID;
	private int cost;
	
	public history(String us, Timestamp d, String st, String b) {
		UserId = us;
		fdate = d;
		start = st;
		BikeUID = b;
	}
	public void setUserId(String us) {
		 UserId = new String(us);
	}
	public String getUserId() {
		return new String(UserId);
	}
	public void setdate() {
		long currentTimeMillis = System.currentTimeMillis();
		fdate = new Timestamp(currentTimeMillis);
	}
	public Timestamp getfdate() {
		return fdate;
	}
	public Timestamp gettdate() {
		return tdate;
	}
	public int getiv() {
		return interval;
	}
	public void setst(String st) {
		 start = new String(st);
	}
	public String getst() {
		return new String(start);
	}
	public void setsp(String sp) {
		 stop = new String(sp);
	}
	public String getsp() {
		return new String(stop);
	}
	public void setbid(String bid) {
		 BikeUID = new String(bid);
	}
	public String getbid() {
		return new String(BikeUID);
	}
	public void setc(Timestamp c) {
		tdate = c;
		long t = (c.getTime() - fdate.getTime()) / (60 * 1000);
		interval = (int)t;
		if(t <= 30) {
			cost = 0;
		}else {
			cost = (int)t/30;
		}
	}
	public int getc() {
		return cost;
	}
	
}
