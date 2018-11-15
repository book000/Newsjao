package com.jaoafa.Newsjao.Event;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.jaoafa.Newsjao.NewsSpeak;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.util.audio.AudioPlayer;

public class MorningEvent {
	// 朝6時～9時にVC(General)に入るとニュースを流す
	public static long last = -1;
	@EventSubscriber
	public void onUserVoiceChannelJoinEvent(UserVoiceChannelJoinEvent event){
		if(event.getUser().getLongID() == event.getClient().getOurUser().getLongID()){
			return;
		}
		if(event.getVoiceChannel().getLongID() != 189377933356302336L){
			return; // General VCじゃなければスキップ
		}
		System.out.println("VoiceJoin: " + event.getUser().getName() + " " + event.getVoiceChannel().getName());

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 6, 0, 0); // 今日の6時
		Date today6 = cal.getTime();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 9, 0, 0); // 今日の9時
		Date today9 = cal.getTime();

		if(!isPeriod(today6, today9)){
			// 6時から9時の間ではない
			return;
		}

		AudioPlayer audioP = AudioPlayer.getAudioPlayerForGuild(event.getGuild());
		if(audioP.getPlaylistSize() != 0){
			// 放送中？
			return;
		}

		if(last == event.getUser().getLongID()){
			return;
		}
		last = event.getUser().getLongID();

		NewsSpeak.speakNews(event.getClient(), event.getGuild().getChannelByID(512242412635029514L), event.getVoiceChannel(), true);
	}
	/**
	 * 指定された期間内かどうか
	 * @param start 期間の開始
	 * @param end 期間の終了
	 * @return 期間内ならtrue、期間外ならfalse
	 * @see http://www.yukun.info/blog/2009/02/java-jsp-gregoriancalendar-period.html
	 */
	public static boolean isPeriod(Date start, Date end){
		Date now = new Date();
		if(now.after(start)){
			if(now.before(end)){
				return true;
			}
		}

		return false;
	}
}
